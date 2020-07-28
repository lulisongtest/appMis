Ext.define('appMis.store.RequestmapStore', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'appMis.model.RequestmapModel',
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
            model: 'appMis.model.RequestmapModel',
            storeId: 'RequestmapStore',
            autoLoad:  true,
            proxy: {
                type: 'ajax',
                api: {
                    read :'/appMis/requestmap/readRequestmap'
                },
                noCache: false,
                actionMethods: {
                    read   : 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'requestmaps',
                    totalProperty:"totalCount"
                },
                writer:{
                    type:'json'
                }
            },
            listeners:{
                load : function(store, records, successful,operation,eOpts){//监听拦截器TimeoutInterceptor， 监听过滤器TimeOutFilter，且对Ajax起作用，r.totalCount=-1时，网页过时了
                    if(store.totalCount=='-1'){
                        timeout1(-1,"访问权限信息")//Session超时了
                    }
                },
                update:function(store,record){
                    Ext.Ajax.request({
                        url:'/appMis/requestmap/update',
                        params:{
                            id : record.get("id"),
                            url:record.get("url"),
                            configAttribute:record.get("configAttribute"),
                            chineseUrl:record.get("chineseUrl"),
                            roleList:record.get("roleList"),
                            httpMethod:record.get("httpMethod"),
                            treeId:record.get("treeId"),
                            glyph:record.get("glyph")
                        },
                        success:function(resp,opts) {
                            if(resp.responseText=='success'){
                                var panelStore = (Ext.getCmp('requestmapgrid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                            }else{
                                if(resp.responseText=='failure1'){
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '系统中已有此数据表的控制信息！',
                                        buttons: Ext.MessageBox.OK
                                    })
                                    setTimeout(function () {
                                        var panelStore = (Ext.getCmp('requestmapgrid')).getStore();
                                        panelStore.loadPage(panelStore.currentPage);
                                        Ext.Msg.hide();
                                    },1500);
                                }else{
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '系统中无此数据表！ ',
                                        buttons: Ext.MessageBox.OK
                                    })
                                    setTimeout(function () {
                                        var panelStore = (Ext.getCmp('requestmapgrid')).getStore();
                                        panelStore.loadPage(panelStore.currentPage);
                                        Ext.Msg.hide();
                                    },1500);
                                }
                            }
                        },
                        failure: function(resp,opts) {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据更新失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                var panelStore = (Ext.getCmp('requestmapgrid')).getStore();
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
