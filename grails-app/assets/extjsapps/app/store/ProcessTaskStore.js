function dateisnull(x) {
    if ((x == "") || (x == null)) {
        return null
    } else {
        return (new Date(x)).pattern("yyyy-MM-dd") + " 00:00:00.0"
    }
}

Ext.define('appMis.store.ProcessTaskStore',
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
                    storeId: 'ProcessTaskStore',
                    autoLoad: true,

                    proxy: {
                        type: 'ajax',
                        api: {
                           // read: 'processTask/readProcessTask'
                            read: '/appMis/processTask/readProcessTask'
                        },
                        noCache: false,
                        actionMethods: {
                            read: 'POST'
                        },
                        reader: {
                            rootProperty: 'processTasks',
                            totalProperty: "totalCount"
                        },
                        writer: {type: 'json'}
                    },
                    listeners: {
                        /* load : function( r, option, success){
                             timeout1(r.totalCount,"流程任务")
                         },*/
                        /*update:function(store,record){
                            Ext.Ajax.request({
                            url:'/appMis/processTask/update',
                            params:{
                                ID_ : record.get("id"),
                                REV_ : record.get('REV_') ,
                                EXECUTION_ID_ : record.get('EXECUTION_ID_') ,
                                PROC_INST_ID_ : record.get('PROC_INST_ID_') ,
                                PROC_DEF_ID_ : record.get('PROC_DEF_ID_') ,
                                NAME_ : record.get('NAME_'),
                                PARENT_TASK_ID_ : record.get('PARENT_TASK_ID_') ,
                                DESCRIPTION_ : record.get('DESCRIPTION_') ,
                                TASK_DEF_KEY_ : record.get('TASK_DEF_KEY_') ,
                                OWNER_ : record.get('OWNER_') ,
                                ASSIGNEE_ : record.get('ASSIGNEE_') ,
                                DELEGATION_ : record.get('DELEGATION_') ,
                                PRIORITY_ : record.get('PRIORITY_') ,
                                CREATE_TIME_:dateisnull(record.get('CREATE_TIME_')),
                                DUE_DATE_:dateisnull(record.get('DUE_DATE_')),
                                CATEGORY_ : record.get('CATEGORY_') ,
                                SUSPENSION_STATE_ : record.get('SUSPENSION_STATE_') ,
                                TENANT_ID_ : record.get('TENANT_ID_') ,
                                FORM_KEY_ : record.get('FORM_KEY_') ,
                                CLAIM_TIME_:dateisnull(record.get('CLAIM_TIME_'))

                        },
                                success:function(resp,opts) {
                                    if(resp.responseText=='success'){
                                           var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                                           panelStore.loadPage(panelStore.currentPage);
                                    }else{
                                           Ext.Msg.show({
                                                title: '操作提示 ',
                                                msg: '数据更新失败！ ',
                                                buttons: Ext.MessageBox.OK
                                            })
                                            setTimeout(function () {
                                                var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                                                panelStore.loadPage(panelStore.currentPage);
                                                Ext.Msg.hide();
                                           },1500);
                                    }
                                }
                            });
                        }*/
                    }
                }, cfg)]);
        }
    });
                    
