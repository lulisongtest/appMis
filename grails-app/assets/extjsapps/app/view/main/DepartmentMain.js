Ext.define('appMis.view.main.DepartmentMain', {
    //extend: 'Ext.container.Container',
    // extend: 'Ext.container.Viewport',
    extend: 'Ext.panel.Panel',
    alias: 'widget.departmentmain',
    xtype: 'app-departmentmain',
    // id:'app-main',
    id: 'departmentmainroot',
    requires: [
        'appMis.view.main.DepartmentMainController',
        'appMis.view.main.region.DepartmentCenter',
        'appMis.view.main.region.DepartmentLeft'
    ],
    initComponent: function () {
        this.callParent()
    },
    controller: 'departmentmain',//指定控制器，否则就到app.js中查找
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
            xtype: 'departmentmainleft',
            region: 'west',
            split: true,
            header: false,
            titleCollapse: true,
            collapsible: true
        }, {
            xtype: 'departmentmaincenter',
            header: false,
            padding: "1 0 0 0",
            region: 'center'
        },
        {
            xtype: 'panel', //界面最下方的信息栏
            hidden: true,
            region: 'south'
        }
    ]
})
