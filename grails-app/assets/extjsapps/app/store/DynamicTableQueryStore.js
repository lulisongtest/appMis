Ext.define('appMis.store.DynamicTableQueryStore', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'appMis.model.DynamicTableModel',
        'Ext.grid.*',
        'Ext.toolbar.Paging',
        'Ext.data.*'
    ],
    pageSize:25,
    remoteSort: true,
    constructor: function(cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            model: 'appMis.model.DynamicTableModel',
            storeId: 'DynamicTableQueryStore',
            autoSync : true,//这样修改完一个单元格后会自动保存数据
            autoLoad:  true,//是否与上一句是一个功能！？
            proxy: {
                type: 'ajax',
                api: {
                   // read :'/appMis/dynamicTable/readDynamicTable?tableNameId=all'
                    read :'/appMis/dynamicTable/readDynamicTableQuery'
                },
                noCache: false,
                actionMethods: {
                    read   : 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'dynamictables',
                },
                writer:{
                    type:'json'
                }
            }
            }, cfg)]);
    }
});
