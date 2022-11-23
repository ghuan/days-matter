Ext.define('view.system.SystemManageContainer', {
  extend: 'Ext.container.Container',
  requires: ["Ext.ux.PasswordField"],
  constructor : function(cfg) {
    var exCfg = {
      scrollable:true,
      layout : {
        type: 'table',
        columns: 3,
      },
      defaults:{
        margin:5,
      }
    }
    Ext.apply(exCfg,cfg);
    this.callParent([exCfg]);
  },
  initComponent : function() {
    this.items = [
      this.getCacheMangeCfg(),
      this.getSQLManageCfg(),
      this.getRemoteSQLManageCfg()
    ]
    this.callParent(arguments);
  },
  getCacheMangeCfg:function(){
    return {
      xtype:'fieldset',
      itemId:'cacheManage',
      colspan:3,
      height:250,
      title:'缓存管理',
      defaults:{
        margin:5,
        labelWidth:50
      },
      layout : {
        type: 'table',
        columns: 2
      },
      items:[{
        xtype:'label',
        colspan:2,
        html:'<span class="color-danger text-bold">注：不填默认清除全部！</span>',
      },{
        xtype:'textfield',
        itemId:'schemaCode',
        fieldLabel:'Schema',
      },{
        xtype:'button',
        text:'清除',
        scope:this,
        handler:this.removeSchemaCache
      },{
        xtype:'textfield',
        itemId:'dicCode',
        fieldLabel:'Dic',
      },{
        xtype:'button',
        text:'清除',
        scope:this,
        handler:this.removeDicCache
      }]
    }
  },
  getSQLManageCfg:function(){
    return {
      xtype:'fieldset',
      itemId:'SQLManage',
      colspan:3,
      title:'简易SQL操作（当前数据库）',
      defaults:{
        margin:5,
        labelWidth:50
      },
      items:[{
        xtype:'toolbar',
        width:850,
        itemId:'toolbar',
        margin:0,
        items:['->',{
          xtype:'button',
          ui:'default',
          itemId:'executeButton',
          text:'执行',
          scope:this,
          handler:this.executeSQL
        },{
          xtype:'button',
          ui:'default',
          itemId:'commitButton',
          text:'提交事务',
          disabled:true,
          scope:this,
          handler:this.commitTransaction
        }]
      },{
        xtype:'textarea',
        fieldLabel:'SQL',
        width:850,
        height:100,
        itemId:'sqlContent',
      },{
        xtype:'textarea',
        fieldLabel:'结果',
        itemId:'result',
        width:850,
        height:200,
      }]
    }
  },
  getRemoteSQLManageCfg:function(){
    return {
      xtype:'fieldset',
      itemId:'RemoteSQLManage',
      colspan:3,
      title:'远程数据库SQL操作',
      defaults:{
        margin:5,
        labelWidth:50
      },
      items:[{
        xtype:'toolbar',
        width:850,
        itemId:'toolbar',
        margin:0,
        items:['->',{
          xtype:'button',
          ui:'default',
          itemId:'executeButton',
          text:'执行',
          scope:this,
          handler:this.executeSQLRemote
        },{
          xtype:'button',
          ui:'default',
          itemId:'commitButton',
          text:'提交事务',
          disabled:true,
          scope:this,
          handler:this.commitTransactionRemote
        }]
      },{
        xtype:'toolbar',
        itemId:'connectProps',
        padding:5,
        margin:0,
        items:[{
          xtype:'textfield',
          colspan:3,
          labelWidth:50,
          width:455,
          emptyText:'jdbc:dbType:thin:@ip:port/sid',
          allowBlank:false,
          fieldLabel:'<span style="color:red">jdbc url</span>',
          itemId:'url',
          listeners: {
            focus: function (field, e) {
              field.originalEmptyText = field.emptyText
              field.setEmptyText();
              field.selectText();
            },
            blur: function (field, e) {
              field.setEmptyText(field.originalEmptyText);
            },
            render:function(field,e){
              new Ext.tip.ToolTip({
                target: field.el,
                anchor: 'top',
                html:field.emptyText
              });
            }
          }
        },{
          xtype:'textfield',
          labelWidth:60,
          width:175,
          allowBlank:false,
          fieldLabel:'<span style="color:red">username</span>',
          itemId:'username',
        },{
          xtype:'passwordfield',
          labelWidth:60,
          width:204,
          allowBlank:false,
          fieldLabel:'<span style="color:red">password</span>',
          itemId:'password',
        }]
      },{
        xtype:'textarea',
        fieldLabel:'SQL',
        width:850,
        height:100,
        itemId:'sqlContent',
      },{
        xtype:'textarea',
        fieldLabel:'结果',
        itemId:'result',
        width:850,
        height:200,
      }]
    }
  },
  removeSchemaCache:function(){
    var schemaCode = this.getComponent('cacheManage').getComponent('schemaCode').getValue();
    schemaCode = schemaCode || '*';
    this.ownerCt.mask('正在处理...')
    AsyncRequest.get('cache/removeSchemaCache/'+schemaCode, {}, function (opts, success, response) {
      this.ownerCt.unmask();
      if(success){
        Msg.Toast.success('清除成功');
      }else {
        VTools.processResponse(response);
      }
    }, this);
  },
  removeDicCache:function(){
    var dicCode = this.getComponent('cacheManage').getComponent('dicCode').getValue();
    dicCode = dicCode || '*';
    this.ownerCt.mask('正在处理...')
    AsyncRequest.get('cache/removeDicCache/'+dicCode, {}, function (opts, success, response) {
      this.ownerCt.unmask();
      if(success){
        Msg.Toast.success('清除成功');
      }else {
        VTools.processResponse(response);
      }
    }, this);
  },
  executeSQL:function(){
    var commitButton = this.getComponent('SQLManage').getComponent('toolbar').getComponent('commitButton');
    commitButton.commitSQLContent = null;
    commitButton.disable();
    var sqlContent = this.getComponent('SQLManage').getComponent('sqlContent').getValue();
    this.ownerCt.mask('正在处理...')
    AsyncRequest.post('platform/simple/executeSQL', {
      sqlContent:sqlContent,
      commit:false
    }, function (opts, success, response) {
      this.ownerCt.unmask();
      if(success){
        var rs = Ext.decode(response.responseText);
        this.getComponent('SQLManage').getComponent('result').setValue(null);
        this.getComponent('SQLManage').getComponent('result').setValue(rs.result);
        if(rs.shouldCommit){
          commitButton.commitSQLContent = rs.commitSQLContent;
          commitButton.enable();
        }
      }else {
        VTools.processResponse(response);
      }
    }, this);
  },
  commitTransaction:function(){
    var commitButton = this.getComponent('SQLManage').getComponent('toolbar').getComponent('commitButton');
    if(commitButton.commitSQLContent){
      Msg.confirm('是否提交当前事务?', function (btn) {
        if (btn == 'yes') {
          this.ownerCt.mask('正在处理...')
          AsyncRequest.post('platform/simple/executeSQL', {
            sqlContent:commitButton.commitSQLContent,
            commit:true
          }, function (opts, success, response) {
            this.ownerCt.unmask();
            if(success){
              Msg.Toast.success('提交成功');
              commitButton.disable();
              commitButton.commitSQLContent = null;
            }else {
              VTools.processResponse(response);
            }
          }, this);
        }
      }, this)
    }
  },
  executeSQLRemote:function(){
    var manager = this.getComponent('RemoteSQLManage');
    var connectProps = manager.getComponent('connectProps');
    var urlField = connectProps.getComponent('url');
    var usernameField = connectProps.getComponent('username');
    var passwordField = connectProps.getComponent('password');
    var valid = true;
    if(!urlField.isValid()){
      valid = false;
    }
    if(!usernameField.isValid()){
      valid = false;
    }
    if(!passwordField.isValid()){
      valid = false;
    }
    if(!valid){
      return;
    }

    var commitButton = manager.getComponent('toolbar').getComponent('commitButton');
    commitButton.url = null;
    commitButton.username = null;
    commitButton.password = null;
    commitButton.commitSQLContent = null;
    commitButton.disable();
    var sqlContent = manager.getComponent('sqlContent').getValue();
    this.ownerCt.mask('正在处理...')
    AsyncRequest.post('platform/simple/executeSQLRemote', {
      url:urlField.getValue(),
      username:usernameField.getValue(),
      password:passwordField.getValue(),
      sqlContent:sqlContent,
      commit:false
    }, function (opts, success, response) {
      this.ownerCt.unmask();
      if(success){
        var rs = Ext.decode(response.responseText);
        manager.getComponent('result').setValue(null);
        manager.getComponent('result').setValue(rs.result);
        if(rs.shouldCommit){
          commitButton.url = rs.url;
          commitButton.username = rs.username;
          commitButton.password = rs.password;
          commitButton.commitSQLContent = rs.commitSQLContent;
          commitButton.enable();
        }
      }else {
        VTools.processResponse(response);
      }
    }, this);

  },
  commitTransactionRemote:function(){
    var commitButton = this.getComponent('RemoteSQLManage').getComponent('toolbar').getComponent('commitButton');
    if(commitButton.commitSQLContent){
      Msg.confirm('是否提交当前事务?', function (btn) {
        if (btn == 'yes') {
          this.ownerCt.mask('正在处理...')
          AsyncRequest.post('platform/simple/executeSQLRemote', {
            url:commitButton.url,
            username:commitButton.username,
            password:commitButton.password,
            sqlContent:commitButton.commitSQLContent,
            commit:true
          }, function (opts, success, response) {
            this.ownerCt.unmask();
            if(success){
              Msg.Toast.success('提交成功');
              commitButton.disable();
              commitButton.url = null;
              commitButton.username = null;
              commitButton.password = null;
              commitButton.commitSQLContent = null;
            }else {
              VTools.processResponse(response);
            }
          }, this);
        }
      }, this)
    }
  }
});
