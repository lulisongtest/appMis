
Ext.define('appMis.view.processDiagram.ImportProcessDiagram', {
    extend: 'Ext.window.Window',
    xtype: 'importProcessDiagram',
    id: 'importProcessDiagram',
    width: 670,
    height: 80,
    title: '上传流程图文件',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    requires: [
       // 'Ext.ux.form.ItemSelector'
    ],
    initComponent: function () {
       this.modal=true;
       this.items = [
           {
               xtype: 'panel',
               layout: 'column',
               items: [
                   {
                       xtype:'button',
                       text: '上传文件',
                       columnWidth:.25,
                       icon: 'images/tupian/folder_table.png',
                       action: 'importFromFile'
                   },{
                       xtype: 'form',
                       id:'importForm',
                       columnWidth:.75,
                       height:60,
                       // baseCls: 'x-plain',
                       url:"processDiagram",//上传服务器的地址
                       fileUpload: true,
                       //defaultType: 'textfield',
                       items: [{
                           xtype: 'fileuploadfield',
                           width:480,
                           labelWidth: 0,
                           // labelAlign: 'right',
                           //fieldLabel: '选择文件',
                           name: 'processDiagramfilePath',
                           id: 'processDiagramfilePath',
                           buttonText: '选择文件',
                           blankText: '文件名不能为空'
                       }]
                   }
               ]
          }
         ];
        this.callParent(arguments);
    }
});           
