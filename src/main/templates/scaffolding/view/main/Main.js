Ext.define('appMis.view.main.${className}Main', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.${propertyName}main',
    xtype: 'app-${propertyName}main',
    id: '${propertyName}mainroot',
    requires: [
        'appMis.view.main.${className}MainController',
        'appMis.view.main.region.${className}Center',
        'appMis.view.main.region.${className}Left'
    ],
    initComponent: function () {
         this.callParent()
    },
    controller: '${propertyName}main',//指定控制器，否则就到app.js中查找
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
            xtype: '${propertyName}mainleft',
            hidden:(Roleflag=="单位管理员")||(Roleflag=="单位审核人"),
            region: 'west',
            split: true,
            header: false,
            titleCollapse: true,
            collapsible: true
        }, {
            xtype: '${propertyName}maincenter',
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
