
Ext.define('appMis.store.ProcessDefStore',
    {
        extend: 'Ext.data.Store',
        singleton: false,
        requires: [
            'appMis.model.ProcessTaskModel',
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
                    model: 'appMis.model.ProcessTaskModel',
                    storeId: 'ProcessDefStore',
                    autoLoad: true,

                    proxy: {
                        type: 'ajax',
                        api: {
                            read: '/appMis/processTask/readProcessDef'
                        },
                        noCache: false,
                        actionMethods: {
                            read: 'POST'
                        },
                        reader: {
                            rootProperty: 'processDefs',
                            totalProperty: "totalCount"
                        },
                        writer: {type: 'json'}
                    },
                    listeners: {

                    }
                }, cfg)]);
        }
    });
                    
