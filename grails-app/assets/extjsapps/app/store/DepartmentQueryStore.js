Ext.define('appMis.store.DepartmentQueryStore', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'appMis.model.DepartmentModel',
        'Ext.grid.*',
        'Ext.toolbar.Paging',
        'Ext.data.*'
    ],
    remoteSort: true,
    constructor: function (cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            model: 'appMis.model.DepartmentModel',
            storeId: 'DepartmentQueryStore',
            autoLoad:  true,
            sorters: [{
                property: 'treeId',
                direction: 'ASC'
            }],
            proxy: {
                type: 'ajax',
                api: {
                    read: '/appMis/department/readDepartmentQuery'
                },
                noCache: true,
                actionMethods: {
                    read: 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'departments'
                }
            },
            listeners: {
                load: function (r, option, success) {//监听过滤器salarymis\TimeOutFilter.groovy，且对Ajax起作用，r.totalCount=-1时，网页过时了
                    //alert("已经监听职工基本信息");
                    timeout1(r.totalCount, "单位信息")//在App.js中timeout1判断网页是否超时
                }
            }
        }, cfg)]);
    }
});
