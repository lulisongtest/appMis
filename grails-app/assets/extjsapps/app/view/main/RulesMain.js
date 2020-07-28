Ext.define('salaryMis.view.main.RulesMain', {
    //extend: 'Ext.container.Container',
    // extend: 'Ext.container.Viewport',
    extend: 'Ext.panel.Panel',
    alias: 'widget.rulesmain',
    xtype: 'app-rulesmain',
    id: 'rulesmainroot',
    requires: [
       // 'salaryMis.view.main.RulesMainController'
    ],
    initComponent: function () {
         this.callParent()
    },
   // controller: 'rulesmain',//指定控制器，否则就到app.js中查找
   // viewModel: 'main',
    ui: 'navigation',
    layout: {
        type: 'fit'
    },
    items: [
        {
            xtype: 'rulesviewport',
            header: false
        }
    ]
});
