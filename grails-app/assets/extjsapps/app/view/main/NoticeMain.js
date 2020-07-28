Ext.define('salaryMis.view.main.NoticeMain', {
    //extend: 'Ext.container.Container',
    // extend: 'Ext.container.Viewport',
    extend: 'Ext.panel.Panel',
    alias: 'widget.noticemain',
    xtype: 'app-noticemain',
    id: 'noticemainroot',
    requires: [
       // 'salaryMis.view.main.NoticeMainController'
    ],
    initComponent: function () {
         this.callParent()
    },
   // controller: 'noticemain',//指定控制器，否则就到app.js中查找
   // viewModel: 'main',
    ui: 'navigation',
    layout: {
        type: 'fit'
    },
    items: [
        {
            xtype: 'noticeviewport',
            header: false
        }
    ]
});
