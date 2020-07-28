

Ext.define('appMis.view.dynamicTable.DynamicTableAdd' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.dynamictableadd',
    title : '修改表信息',
    layout: 'fit',
    autoShow: true,
    width: 300, ///宽
    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                items: [
                    {
                       xtype: 'textfield',
                       name : 'tableNameId',
                       fieldLabel: '字段名称'
                   },
        
                  {
                       xtype: 'textfield',
                       name : 'tableName',
                       fieldLabel: '字段名称'
                   },
        
                  {
                       xtype: 'textfield',
                       name : 'fieldNameId',
                       fieldLabel: '字段名称'
                   },
        
                  {
                       xtype: 'textfield',
                       name : 'fieldName',
                       fieldLabel: '字段名称'
                   },
        
                  {
                       xtype: 'textfield',
                       name : 'fieldType',
                       fieldLabel: '字段名称'
                   },
        
                   {
                      xtype: 'textfield',
                      name : 'fieldLength',
                      fieldLabel: '字段名称'
                   }
        
                ]
            }
        ];
        this.buttons = [
            {
                text: '保存',
                action: 'save'
            },
            {
                text: '取消',
                scope: this,
                handler: this.close
            }
        ];
        this.callParent(arguments);
    }
});
