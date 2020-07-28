
Ext.define('appMis.view.notice.ImportNotice', {
    extend: 'Ext.window.Window',
    xtype: 'importNotice',
    id: 'importNotice',
    width: 670,
    height: 120,
    title: '上传文件',
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
               padding: '10 0 10 0',
               items: [
                   {
                       xtype:'button',
                       text: '上传文件',
                       columnWidth:.25,
                       icon: 'tupian/folder_table.png',
                       action: 'importFromFile'
                   },{
                       xtype: 'form',
                       id:'importForm',
                       columnWidth:.75,
                       height:60,
                       // baseCls: 'x-plain',
                       url:"notice",//上传服务器的地址
                       fileUpload: true,
                       //defaultType: 'textfield',
                       items: [{
                           xtype: 'fileuploadfield',
                           width:480,
                           labelWidth: 0,
                           // labelAlign: 'right',
                           //fieldLabel: '选择文件',
                           name: 'noticefilePath',
                           id: 'noticefilePath',
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
