Ext.define('appMis.store.RoleQueryStore', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'appMis.model.RoleModel',
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
            model: 'appMis.model.RoleModel',
            storeId: 'RoleQueryStore',
            autoLoad:  true,
            proxy: {
                type: 'ajax',
                api: {
                    read :'/appMis/role/readRolequery'
                },
                noCache: false,
                actionMethods: {
                    read   : 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'authoritys',
                },
                writer:{
                    type:'json'
                }
            },
            listeners:{
                load : function(store, records, successful,operation,eOpts){//监听拦截器TimeoutInterceptor， 监听过滤器TimeOutFilter，且对Ajax起作用，r.totalCount=-1时，网页过时了
                    if(store.totalCount=='-1'){
                        timeout1(-1,"角色信息")//Session超时了
                    }
                },
                update:function(store,record){
                    Ext.Ajax.request({
                        url:'/appMis/role/update',
                        params:{
                            id : record.get("id"),
                            authority:record.get("authority"),
                            chineseAuthority:record.get("chineseAuthority")
                        },
                        success:
                            function(resp,opts) {//成功后的回调方法
                                if(resp.responseText=='success'){
                                    var panelStore = (Ext.getCmp('rolegrid')).getStore();
                                    panelStore.loadPage(panelStore.currentPage);
                                }else{
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '数据更新失败！ ',
                                        buttons: Ext.MessageBox.OK
                                    })
                                    setTimeout(function () {
                                        var panelStore = (Ext.getCmp('rolegrid')).getStore();
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
                                var panelStore = (Ext.getCmp('rolegrid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                                Ext.Msg.hide();
                            },1500);

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
