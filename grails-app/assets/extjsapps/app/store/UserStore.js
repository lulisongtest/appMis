Ext.define('appMis.store.UserStore', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'appMis.model.UserModel',
        'Ext.grid.*',
        'Ext.toolbar.Paging',
        'Ext.data.*'
    ],
    pageSize: 25,
    remoteSort: true,
    constructor: function (cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            model: 'appMis.model.UserModel',
            storeId: 'UserStore',
            sorters: [{
                property: 'username',
                direction: 'ASC'
            }],
            autoLoad: true,
            proxy: {
                type: 'ajax',
                api: {
                    //  create: 'user/save',
                    read: '/appMis/user/readUser'
                },
                async: false,//同步
                timeout: 0,//无限时间
                noCache: false,
                actionMethods: {
                    //  create : 'POST',
                    read: 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'users',
                    totalProperty: "totalCount"
                },
                writer: {
                    type: 'json'
                }
            },
            listeners: {
                load: function (store, records, successful, operation, eOpts) {//监听拦截器TimeoutInterceptor， 监听过滤器TimeOutFilter，且对Ajax起作用，r.totalCount=-1时，网页过时了
                    if (store.totalCount == '-1') {
                        timeout1(-1, "用户信息")//Session超时了
                    }
                },
                update: function (store, record) {
                    // alert("stroe___record.get('phone')======"+record.get('phone')),
                    Ext.Ajax.request({
                        url: '/appMis/user/update',
                        params: {
                            id: record.get("id"),
                            username: record.get("username"),
                            password: record.get("password"),
                            enabled: record.get("enabled"),
                            accountExpired: record.get("accountExpired"),
                            accountLocked: record.get("accountLocked"),
                            passwordExpired: record.get("passwordExpired"),
                            truename: record.get("truename"),
                            chineseAuthority: record.get("chineseAuthority"),
                            department: record.get("department"),
                            treeId: record.get("treeId"),
                            email: record.get("email"),
                            phone: record.get("phone")
                        },
                        success:
                            function (resp, opts) {//成功后的回调方法
                                if (resp.responseText == 'success') {
                                    var panelStore = (Ext.getCmp('usergrid')).getStore();
                                    panelStore.loadPage(panelStore.currentPage);
                                    /*Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '数据更新成功！ ',
                                        buttons: Ext.MessageBox.OK
                                    })
                                    setTimeout(function () {
                                        var panelStore = (Ext.getCmp('usergrid')).getStore();
                                        panelStore.loadPage(panelStore.currentPage);
                                        Ext.Msg.hide();
                                    },1500);*/
                                } else {
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '数据更新失败！ ',
                                        buttons: Ext.MessageBox.OK
                                    })
                                    setTimeout(function () {
                                        var panelStore = (Ext.getCmp('usergrid')).getStore();
                                        panelStore.loadPage(panelStore.currentPage);
                                        Ext.Msg.hide();
                                    }, 1500);
                                }
                            },
                        failure: function (resp, opts) {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据更新失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                var panelStore = (Ext.getCmp('usergrid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                                Ext.Msg.hide();
                            }, 1500);

                        }
                    });
                },
                sorters: [{
                    property: 'id',
                    direction: 'DESC'
                }]
            }
        }, cfg)]);
    }
});
