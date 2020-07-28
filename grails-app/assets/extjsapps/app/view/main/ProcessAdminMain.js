
Ext.define('appMis.view.main.ProcessAdminMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processadminmain',
    xtype: 'app-processadminmain',
    id: 'processadminmainroot',
    requires: [
        'appMis.view.main.ProcessAdminMainController',
        'appMis.view.main.region.ProcessAdminCenter',
        'appMis.view.main.region.ProcessAdminLeft'
    ],
    initComponent: function () {
         this.callParent()
    },
    controller: 'processadminmain',//指定控制器，否则就到app.js中查找
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
        },{
            xtype: 'processadminmainleft',
           // hidden:(Roleflag=="单位管理员")||(Roleflag=="单位审核人"),
            region: 'west',
            split: true,
            header: false,
            titleCollapse: true,
            collapsible: true
        },{
            xtype: 'processAdminmaincenter',
            padding: "1 0 0 0",
            header: false,
            region: 'center'
        },{
            xtype: 'panel', //界面最下方的信息栏
            region: 'south'
        }
    ]

});
