Ext.define('appMis.view.main.StudentMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.studentmain',
    xtype: 'app-studentmain',
    id: 'studentmainroot',
    requires: [
        'appMis.view.main.StudentMainController',
        'appMis.view.main.region.StudentCenter',
        'appMis.view.main.region.StudentLeft'
    ],
    initComponent: function () {
         this.callParent()
    },
    controller: 'studentmain',//指定控制器，否则就到app.js中查找
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
            xtype: 'studentmainleft',
            hidden:(Roleflag=="单位管理员")||(Roleflag=="单位审核人"),
            region: 'west',
            split: true,
            header: false,
            titleCollapse: true,
            collapsible: true
        }, {
            xtype: 'studentmaincenter',
            header: false,
            padding: "1 0 0 0",
            region: 'center',
            listeners: {
                beforeremove: function () {
               }
            }
        },
        {
            xtype: 'panel', //界面最下方的信息栏
            hidden: true,
            region: 'south'
        }
    ]
});
