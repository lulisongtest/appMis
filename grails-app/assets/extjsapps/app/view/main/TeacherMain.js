Ext.define('appMis.view.main.TeacherMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.teachermain',
    xtype: 'app-teachermain',
    id: 'teachermainroot',
    requires: [
        'appMis.view.main.TeacherMainController',
        'appMis.view.main.region.TeacherCenter',
        'appMis.view.main.region.TeacherLeft'
    ],
    initComponent: function () {
         this.callParent()
    },
    controller: 'teachermain',//指定控制器，否则就到app.js中查找
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
            xtype: 'teachermainleft',
            hidden:(Roleflag=="单位管理员")||(Roleflag=="单位审核人"),
            region: 'west',
            split: true,
            header: false,
            titleCollapse: true,
            collapsible: true
        }, {
            xtype: 'teachermaincenter',
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
