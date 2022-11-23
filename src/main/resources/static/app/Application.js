/**
 * 应用程序入口
 */
Ext.define('MSFApp.Application', {
  extend: 'Ext.app.Application',
  name: 'MSFApp',
  quickTips: false,
  platformConfig: {
    desktop: {
      quickTips: true
    }
  },
  views: [
    'MSFApp.view.desktop.login.LoginWin',
    'MSFApp.view.login.LoginBody',
    'MSFApp.view.main.Main'
  ],
  stores: [
    // TODO: add global / shared stores here
  ],
  initBodyStyle:function(){
    document.getElementById('msf-web-loading').style.display = 'none'
    Ext.getBody().setStyle('height',null)
    Ext.getBody().setStyle('background',null)
    Ext.getBody().setStyle('padding',null)
    Ext.getBody().setStyle('margin',null)
  },
  launch: function () {//页面及js加载完后执行
    Ext.apply(MSFApp,MSFApp.getApplication().config);
    //同步加载
    Ext.Loader.setConfig({
      paths: {
        'Ext': 'app/Ext',
        'model': 'app/model',
        'store': 'app/store'
      },
      disableCaching:true,
      disableCachingParam: MSFApp.uiProfiles == 'dev' ?
          MSFApp.getApplication().getDefaultCachingParamName()
          :
          MSFApp.getApplication().getVersionCachingParamName()
    }).loadScriptsSync([
      //charts插件
      'app/Ext/ux/charts/charts.js',
      'app/Ext/ux/charts/charts-all.css',
      //calendar插件
      'app/Ext/ux/calendar/calendar.js',
      'app/Ext/ux/calendar/calendar-all.css',
      //exporter插件
      'app/Ext/ux/exporter/exporter.js',
      //websocket依赖js
      'js/websocket/sockjs.min.js',
      'js/websocket/stomp.min.js',
      'app/Ext/ux/WebSocket.js',
      //系统自定义全局css
      'css/app.css',
      //自定义皮肤样式
      'css/theme.css',
      //iconfont
      'ext/resources/iconfont/iconfont.css',
      //加载消息提示工具
      'app/Ext/utils/Message.js',
      //加载请求工具
      'app/Ext/utils/SyncRequest.js',
      'app/Ext/utils/AsyncRequest.js',
      //加载登陆登出工具类
      'app/Ext/utils/LogonUtils.js',
      //加载param工具类
      'app/Ext/utils/ParamUtils.js',
      //加载工具类
      'app/Ext/utils/VTools.js',
      //日期工具类
      'app/Ext/utils/DateUtils.js',
      //URL工具类
      'app/Ext/utils/URLUtils.js',
      //加载全局键盘工具
      'app/Ext/utils/AppKeyboard.js',
      //加载WebReport工具
      'app/Ext/utils/WebReport.js',
      //加载vtype
      'app/Ext/vtype/Vtype.js',
      //加载extjs组件扩展、重写js
      'app/Ext/Ext.js',
      //加载CLodopFuncs
      'clodop/CLodop.js',
    ]);
    AppKeyboard.create();//创建系统快捷键功能
    //判断是否创建单独展示的desktop模块
    var desktop = URLUtils.getParamValue('desktop');
    if (desktop) {
      this.toDesktop();
    }else {
      this.initBodyStyle();
      Msg.Toast.warn('url入参desktop不能为空');
    }
  },
  toDesktop:function(desktop){
    Ext.create({
      xtype: 'app-desktop'
    });
  },
  onAppUpdate: function () {

  },
});
