
Ext.define('appMis.view.processDiagram.ExportProcessDiagram', {
    extend: 'Ext.window.Window',
    xtype: 'exportProcessDiagram',
    id: 'exportProcessDiagram',
    width: '100%',
    height: '100%',
    title: '查看流程图文件',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    requires: [],
    items:[{
        xtype: 'panel',
        id:'diagramDisplay'
    }]
});
           
