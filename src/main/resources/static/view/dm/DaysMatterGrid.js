Ext.define('view.dm.DaysMatterGrid', {
    extend: 'Ext.base.BaseGrid',
    constructor: function (cfg) {
        var exCfg = {
            schemaCode: 'days_matter',
            title: '纪念日管理',
            removeByFiled: 'title',
            formCls:'view.dm.DaysMatterForm',
            queryUrl:'daysMatter/doQuery',
            colActions: [{text: "修改", handler: "doBtnUpdate", iconCls: 'color-success x-fa fa-edit'}, {
                text: "删除",
                handler: "doBtnRemove",
                iconCls: 'color-danger iconfont icon_del'
            }],
            buttons: [{text: "新建(F1)", cmd: "F1", handler: "doBtnCreate"}, {
                text: "客户端设置(F2)",
                cmd: "F2",
                handler: "doSetClientConfig"
            }],
        }
        Ext.apply(exCfg, cfg);
        this.callParent([exCfg]);
    },
    doNotice:function(){
        SyncRequest.get('daysMatter/call')
    },
    doSaveClientConfig: function () {
        var regular = this.regularPromptWin.getComponent('regular');
        var client = this.regularPromptWin.getComponent('client');
        if(regular.getComponent('regular_minute').getComponent('value').isValid()
        && regular.getComponent('threshold_days').isValid()
            && client.getComponent('client_width').isValid()
            && client.getComponent('client_height').isValid()){
            this.regularPromptWin.mask('保存中...')
            var regularMinute = regular.getComponent('regular_minute').getComponent('value').getValue();
            var thresholdDays = regular.getComponent('threshold_days').getValue();
            var clientWidth = client.getComponent('client_width').getValue();
            var clientHeight = client.getComponent('client_height').getValue();
            AsyncRequest.post("daysMatter/saveConfig", {
                regularMinute: regularMinute,
                thresholdDays: thresholdDays,
                clientWidth:clientWidth,
                clientHeight:clientHeight
            }, function (opts, success, response) {
                this.regularPromptWin.unmask();
                if (success) {
                    Msg.success('保存成功');
                    this.regularPromptWin.hide();
                } else {
                    VTools.processResponse(response);
                }
            }, this);
        }
    },
    doSetClientConfig: function () {
        var me = this;
        if (!this.regularPromptWin) {
            this.regularPromptWin = this.createWin({
                title: '客户端设置',
                iconCls: 'x-fa fa-user-circle',
                width: 450,
                layout: 'vbox',
                buttonAlign: 'center',
                maximizable: false,
                resizable: false,
                buttons: [
                    {text: '确定', handler: me.doSaveClientConfig, scope: me},
                    {
                        text: '关闭', handler: function () {
                            me.regularPromptWin.hide()
                        }
                    }
                ],
                defaults: {
                    margin: '8 8 8 8',
                },
                items: [
                    {
                        layout: 'hbox',
                        itemId: 'client',
                        items:[{
                            xtype: 'numberfield',
                            itemId: 'client_width',
                            minValue: 1,
                            decimalPrecision: 0,
                            value: 500,
                            labelWidth: 90,
                            width: 180,
                            allowBlank:false,
                            fieldLabel: '<span class="color-danger">客户端-宽</span>'
                        },{
                            xtype: 'numberfield',
                            itemId: 'client_height',
                            margin: '0 0 0 82',
                            minValue: 1,
                            decimalPrecision: 0,
                            value: 500,
                            labelWidth: 20,
                            width: 130,
                            allowBlank:false,
                            fieldLabel: '<span class="color-danger">高</span>'
                        }]
                    },{
                        layout: 'hbox',
                        itemId: 'regular',
                        items:[{
                            layout: 'hbox',
                            itemId: 'regular_minute',
                            items: [
                                {xtype: 'label', html: '<span class="color-danger">提醒间隔:</span>', margin: '8 8 0 0'},
                                {xtype: 'label', html: '每', margin: '8 10 0 8'},
                                {
                                    xtype: 'numberfield',
                                    width: 85,
                                    value: 30,
                                    itemId: 'value',
                                    minValue: 1,
                                    allowBlank:false,
                                    decimalPrecision: 0
                                },
                                {xtype: 'label', html: '分钟', margin: '8 0 0 8'},
                            ]
                        }, {
                            xtype: 'numberfield',
                            itemId: 'threshold_days',
                            minValue: 1,
                            decimalPrecision: 0,
                            value: 7,
                            labelWidth: 60,
                            margin: '0 0 0 8',
                            width: 170,
                            allowBlank:false,
                            fieldLabel: '<span class="color-danger">天数阈值</span>'
                        }]
                    },
                    {
                        width: '100%',
                        margin: '0 8 0 8',
                        html: '<p class="color-danger">如果存在纪念日记录离目标日期的剩余天数小于等于提醒天数阈值，客户端将每隔提醒间隔分钟数弹窗提醒</p>'
                    }
                ]
            })
        }
        //查询纪念日配置
        AsyncRequest.post("daysMatter/getConfig", {}, function (opts, success, response) {
            if (success) {
                var responseText = response.responseText;
                var result = Ext.decode(responseText);
                var regular = this.regularPromptWin.getComponent('regular');
                var client = this.regularPromptWin.getComponent('client');
                regular.getComponent('regular_minute').getComponent('value').setValue(result.regularMinute || 30);
                regular.getComponent('threshold_days').setValue(result.thresholdDays || 7);
                client.getComponent('client_width').setValue(result.clientWidth || 500);
                client.getComponent('client_height').setValue(result.clientHeight || 400);
                this.regularPromptWin.show();
            } else {
                VTools.processResponse(response);
            }
        }, this);

    },
    onFormSave: function (params, data) {
        this.loadData();
    },
});

