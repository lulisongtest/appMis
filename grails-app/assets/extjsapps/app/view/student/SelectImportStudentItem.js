Ext.define('appMis.view.student.SelectImportStudentItem', {
    extend: 'Ext.window.Window',
    xtype: 'selectImportStudentItem',
    id: 'selectImportStudentItem',
    width: 570,
    height: 500,
    title: '选择导入职工应发工资信息字段，右列初值是必选项，且第一个字段是关键字段！',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    requires: [
       // 'Ext.ux.form.ItemSelector'
    ],
    initComponent: function () {
       this.items = [
           {
               xtype: 'itemselector',
               id:'itemselectorImportStudent',
               store: DynamicTableStoreStudent,
               displayField: 'fieldName',
               valueField: 'fieldNameId',
               hideNavIcons: false,
               cls: 'aria-itemselector',
               reference: 'itemselectorImportStudent',
              // fieldLabel: '请选择要导出的字段',
               allowBlank: false,
               msgTarget: 'side',
               fromTitle: '待选择字段',
               toTitle: '已选择字段',
               value: ['username', 'truename','department']//已选择的初值
           }
        ];
         this.buttons=[
         {
            text: '导入',
            glyph:'xf0c7@FontAwesome',
            action: 'importFromExcel',
         },{
            text: '完成',
            glyph:'xf0c7@FontAwesome',
            scope: this,
            handler: this.close
         },{
            text: '放弃',
            glyph:'xf00d@FontAwesome',
            scope: this,
            handler: this.close
         }
         ]
        this.callParent(arguments);
    }
});
