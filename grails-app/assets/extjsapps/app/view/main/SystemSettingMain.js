Ext.define('appMis.view.main.SystemSettingMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.systemSettingmain',
    xtype: 'systemSettingmain',
    requires: [
        'appMis.view.main.SystemSettingMainController',
        'appMis.view.main.region.SystemSettingCenter',
        'appMis.view.main.region.SystemSettingLeft'
    ],
    initComponent: function () {
         this.callParent()
    },
    controller: 'systemSettingmain',//指定控制器，否则就到app.js中查找
    viewModel: 'main',
    ui: 'navigation',
    layout: {
        type: 'border'
    },
    items: [
        {
            xtype: 'panel',
            hidden: true,
            region: 'north'
        }, {
            xtype: 'systemSettingmainleft',
            id: 'systemSettingleft',
            region: 'west',
            split: true,
            header: false,
            titleCollapse: true,
            collapsible: true
        }, {
            xtype: 'systemSettingmaincenter',
            id: 'systemSettingcenter',
            padding: "1 0 0 0",
            header: false,
            region: 'center'
        },
        {
            xtype: 'panel', //界面最下方的信息栏
            hidden: true,
            region: 'south'
        }
    ]
});
