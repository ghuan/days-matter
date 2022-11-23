Ext.define('view.dm.DaysMatterForm', {
    extend: 'Ext.base.BaseForm',
    constructor: function (cfg) {
        var exCfg = {
            schemaCode: 'days_matter',
            id:'view.dm.DaysMatterForm',
            saveUrl:'daysMatter/doSave',
            width:600,
            buttons:[
                {text: "保存(Ctrl+S)",cmd:'s',handler:"doSave",formBind:true},
                {text: "关闭(Esc)",handler:"doWinClose"}
            ],
            buttonDockedItemPos: "bottom",
            buttonPos : "center"
        }
        Ext.apply(exCfg, cfg);
        this.callParent([exCfg]);
    },
    exFieldConfig:function(f){
        var me = this;
        if(f.name == 'dateShow'){
            Ext.apply(f,{
                triggers: {
                    foo: {
                        scope:me,
                        cls: 'x-fa fa-calendar',
                        tooltip:'打开日历选择',
                        handler: me.openCalendar
                    }
                }
            })
        }
        if(f.name == "date"){
            Ext.apply(f,{
                listeners:{
                    change:me.onDateChange,
                    scope:me
                }
            })
        }
        if(f.name == "dateType"){
            Ext.apply(f,{
                listeners:{
                    change:me.onDateChange,
                    scope:me
                }
            })
        }
        if(f.name == "bigDay"){
            console.log(f)
        }
    },
    onLoadData: function(params,result){
        this.onDateChange();
        this.fireEvent('loadData',params,result);
    },
    onDateChange:function(a,b){
        var dateType = this.findField('dateType').getValue();
        var date = this.findField('date').getValue();
        if(dateType == 2){
            date = this.getLunarDate(date);
        }
        this.findField('dateShow').setValue(date);
    },
    getLunarDate:function(date){
        var result = SyncRequest.post("daysMatter/getLunarDate", {date:date});
        if (result.success) {
            return result.response.responseText;
        } else {
            VTools.processResponse(result.response);
            return null;
        }
    },
    openCalendar:function(){
        if(!this.calendarWin){
            this.calendarWin = this.createWin({
                width:800,
                height:600,
                title:'日期选择',
                html:'<p class="color-danger text-bold text-large" style="height: 0px;">&emsp;&emsp;双击选择</p><iframe src="" id="calendarIframe" scrolling="yes" frameborder="0" width="100%" height="99%"></iframe>'
            });
        }
        this.calendarWin.show();
        var date = this.findField('date').getValue();
        console.log(date)
        document.getElementById('calendarIframe').src = "view/dm/calendar/index.html?currDate="+(date||'')+"&_ds="+(new Date()).getTime()
    }
});

var setDateValue = function(date,desc){
    var form = Ext.getCmp('view.dm.DaysMatterForm');
    var title = form.findField('title').getValue();
    if(!title && desc){
        form.findField('title').setValue(desc);
    }
    form.findField('date').setValue(new Date(date).format('yyyy-MM-dd'));
    form.calendarWin.hide();
}