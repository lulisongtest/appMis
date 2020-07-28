Ext.define('appMis.store.StudentStore', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'appMis.model.StudentModel',
        'Ext.grid.*',
        'Ext.toolbar.Paging',
        'Ext.data.*'
    ],
    pageSize:25,
    autoLoad:  true,
    remoteSort: true,

    constructor: function(cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            model: 'appMis.model.StudentModel',
            storeId: 'StudentStore',

            sorters: [{
                property: 'username',
                direction: 'ASC'
            }],
            proxy: {
                type: 'ajax',
                api: {
                     //read :'/appMis/student/readStudent?dep_tree_id=' + currentTreeNode.substr(1)
                    read :'/appMis/student/read?dep_tree_id='
                },
                async:true,//true异步、false同步
                noCache: true,
                actionMethods: {
                    read   : 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'students',
                    totalProperty:"totalCount"
                },
                writer:{
                    type:'json'
                }
            },
            listeners:{
                load: function (r, option, success) {//监听过滤器appmis\TimeOutFilter.groovy，且对Ajax起作用，r.totalCount=-1时，网页过时了
                    //alert("已经监听职工基本信息");
                    timeout1(r.totalCount, "学生基本信息")//在App.js中timeout1判断网页是否超时
                },
               update:function(store,record){
                   Ext.Ajax.request({
                        url:'/appMis/student/update',
                       // method:'POST',
                        params:{
                            id:record.get("id"),
                            username:record.get("username"),
                            truename:record.get("truename"),
                            email:record.get("email"),
                            department:record.get("department"),
                            major:record.get("major"),
                            college:record.get("college"),
                            phone:record.get("phone"),
                            homephone:record.get("homephone"),
                            idNumber:record.get("idNumber"),
                            birthDate:(record.get('birthDate'))?(new Date(record.get('birthDate'))).pattern("yyyy-MM-dd")+" 00:00:00.0":"",
                            sex:record.get("sex"),
                            race:record.get("race"),
                            politicalStatus:record.get("politicalStatus"),
                            enrollDate:(record.get('enrollDate'))?(new Date(record.get('enrollDate'))).pattern("yyyy-MM-dd")+" 00:00:00.0":"",
                            treeId:record.get("treeId"),
                            currentStatus:record.get("currentStatus")
                        },
                       success:
                           function(resp,opts) {//成功后的回调方法
                               if(resp.responseText=='success'){
                                   var panelStore = (Ext.getCmp('studentgrid')).getStore();
                                   panelStore.loadPage(panelStore.currentPage);
                              }else{
                                   Ext.Msg.show({
                                       title: '操作提示 ',
                                       msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                                       buttons: Ext.MessageBox.OK
                                   })
                                   setTimeout(function () {
                                       var panelStore = (Ext.getCmp('studentgrid')).getStore();
                                       panelStore.loadPage(panelStore.currentPage);
                                       Ext.Msg.hide();
                                   },2500);
                               }
                           },
                       failure: function(resp,opts) {
                           Ext.Msg.show({
                               title: '操作提示 ',
                               msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                               buttons: Ext.MessageBox.OK
                           })
                           setTimeout(function () {
                               var panelStore = (Ext.getCmp('studentgrid')).getStore();
                               panelStore.loadPage(panelStore.currentPage);
                               Ext.Msg.hide();
                           },2500)
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
