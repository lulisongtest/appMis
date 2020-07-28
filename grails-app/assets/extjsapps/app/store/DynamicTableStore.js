Ext.define('appMis.store.DynamicTableStore', {
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
            storeId: 'DynamicTableStore',
            autoSync : true,//这样修改完一个单元格后会自动保存数据
            autoLoad:  true,//是否与上一句是一个功能！？
            proxy: {
                type: 'ajax',
                api: {
                   // read :'/appMis/dynamicTable/readDynamicTable?tableNameId=all'
                    read :'/appMis/dynamicTable/readDynamicTable?tableNameId='
                },
                noCache: false,
                actionMethods: {
                    read   : 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'dynamictables',
                    totalProperty:"totalCount"
                },
                writer:{
                    type:'json'
                }
            },

            listeners:{
                update:function(store,record){
                    Ext.Ajax.request({
                        url:'/appMis/dynamicTable/update',
                        params:{
                            id : record.get("id"),
                            tableNameId:record.get("tableNameId"),
                            tableName:record.get("tableName"),
                            fieldNameId:record.get("fieldNameId"),
                            fieldName:record.get("fieldName"),
                            fieldType:record.get("fieldType"),
                            fieldLength:record.get("fieldLength")
                        },
                        success:
                            function(resp,opts) {//成功后的回调方法
                                if(resp.responseText=='success'){
                                        var panelStore = (Ext.getCmp('dynamictablegrid')).getStore();
                                        panelStore.loadPage(panelStore.currentPage);
                                }else{
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '数据更新失败！ ',
                                        buttons: Ext.MessageBox.OK
                                    })
                                    setTimeout(function () {
                                        var panelStore = (Ext.getCmp('dynamictablegrid')).getStore();
                                        panelStore.loadPage(panelStore.currentPage);
                                        Ext.Msg.hide();
                                    },1500);
                                }
                            },
                        failure: function(resp,opts) {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据更新失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                var panelStore = (Ext.getCmp('dynamictablegrid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                                Ext.Msg.hide();
                            },1500)
                        }
                    });
                },
                sorters: [{
                    property: 'id',
                    direction: 'DESC'
                }]
            }}, cfg)]);
    }
});
