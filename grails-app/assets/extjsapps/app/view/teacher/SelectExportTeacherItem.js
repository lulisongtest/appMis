/*Wed Feb 26 00:17:19 CST 2020陆立松自动创建*/
Ext.define('appMis.view.teacher.SelectExportTeacherItem', {
    extend: 'Ext.window.Window',
    xtype: 'selectExportTeacherItem',
    id: 'selectExportTeacherItem',
    width: 570,
    height: 500,
    title: '选择导出信息字段，右列初值是必选项',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    requires: [
    ],
    initComponent: function () {
        this.items = [
           {
               xtype: 'itemselector',
               id:'itemselectorExportTeacher',
               store: DynamicTableStoreTeacher,
               displayField: 'fieldName',
               valueField: 'fieldNameId',
               hideNavIcons: false,
               cls: 'aria-itemselector',
               reference: 'itemselectorTeacher',
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
            text: '导出',
            glyph:'xf0c7@FontAwesome',
            action: 'exportToExcel',
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
