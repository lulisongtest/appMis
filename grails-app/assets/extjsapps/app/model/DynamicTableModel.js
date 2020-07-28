Ext.define('appMis.model.DynamicTableModel', {
    extend: 'Ext.data.Model',
    requires: [
        'Ext.data.Field'
    ],
fields: ['id',
'tableNameId',
'tableName',
'fieldNameId',
'fieldName',
'fieldType',
'fieldLength'
]
});