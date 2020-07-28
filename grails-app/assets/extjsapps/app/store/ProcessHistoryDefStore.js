
Ext.define('appMis.store.ProcessHistoryDefStore',
    {
        extend: 'Ext.data.Store',
        singleton: false,
        requires: [
            'appMis.model.ProcessHistoryTaskModel',
            'Ext.grid.*',
            'Ext.toolbar.Paging',
            'Ext.data.*'
        ],
        pageSize: 25,
        remoteSort: true,
        constructor: function (cfg) {
            var me = this;
            cfg = cfg || {};
            me.callParent([
                Ext.apply({
                    model: 'appMis.model.ProcessHistoryTaskModel',
                    storeId: 'ProcessHistoryDefStore',
                    autoLoad: true,

                    proxy: {
                        type: 'ajax',
                        api: {
                            read: '/appMis/processTask/readProcessHistoryDef'
                        },
                        noCache: false,
                        actionMethods: {
                            read: 'POST'
                        },
                        reader: {
                            rootProperty: 'processHistoryDefs',
                            totalProperty: "totalCount"
                        },
                        writer: {type: 'json'}
                    },
                    listeners: {

                    }
                }, cfg)]);
        }
    });
                    
