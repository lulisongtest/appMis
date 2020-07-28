
Ext.define('appMis.view.student.StudentImportBatchPhoto' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.studentImportBatchPhoto',
    width:1070,
    height: 550,
    title : '批量导入职工照片信息',
    modal: true,//创建模态窗口
     autoShow: true,
   // buttonAlign:'left',
    layout: 'column',
    initComponent: function() {
        this.items = [
                {
                    xtype: 'panel'

                }
        ];

        this.buttons = [
            {
                text: '关闭',
                glyph:'xf00d@FontAwesome',
                scope: this,
                handler: this.close
            }
        ];
        this.callParent(arguments);
    }
});

