Ext.define('appMis.store.DepartmentStore', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'appMis.model.DepartmentModel',
        'Ext.grid.*',
        'Ext.toolbar.Paging',
        'Ext.data.*'
    ],
    pageSize: 25,

    remoteSort: true,
    // sortOnLoad : false,

    reloadtreemenu:function (){//重新刷新树状目录
        Ext.Ajax.request({//根据用户角色的权限用来确定界面上的内容哪些隐藏或不隐藏,生成操作树。
            url: '/appMis/authentication/userlogin1',
            async:false,//同步
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                    var buttonMenu1 = resp.responseText.replace(/children/g, "menu");
                    buttonMenu1 = buttonMenu1.replace(/leaf : true/g, "action:'buttonMenu'");
                    var obj1 = eval('(' + buttonMenu1 + ')');//用/children/g可以把“children”全部换成"menu"，,再将获取的Json字符串转换为Json对象,
                    Ext.getCmp("departmentmainroot").getViewModel().set('systemButtonMenu',eval('(' + obj1.roots + ')'));//当前角色存入ViewModel
                    Ext.getCmp("departmentmainroot").getViewModel().set('systemTreeMenu',eval('(' + obj.roots + ')'));//当前角色存入ViewModel
                }
        });
        var path="";
        if(currentTreeNode!="p1000"){
            var i;
            for(i=1;i<currentTreeNode.length;i=i+3){
                path=path+"/p"+currentTreeNode.substring(1,i+3)
            }
            path="/p1000"+path
        }else{
            path="p1000"
        }
        // Ext.getCmp("treemainmenu").setRootNode(Ext.getCmp("treemainmenu").up('app-main').getViewModel().get('systemTreeMenu'))//重新设置树状目录
        // Ext.getCmp("treemainmenu").expandPath(path)//展开树状目录到path的位置
        Ext.getCmp("departmenttreemainmenu").setRootNode(Ext.getCmp("departmenttreemainmenu").up('app-departmentmain').getViewModel().get('systemTreeMenu'));//重新设置树状目录
        Ext.getCmp("departmenttreemainmenu").expandPath(path);//展开树状目录到path的位置
        // Ext.getCmp("departmenttreemainmenu").expandPath("/p1000");//展开树状目录到path的位置
    },

    constructor: function (cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            model: 'appMis.model.DepartmentModel',
            storeId: 'DepartmentStore',
            autoLoad:  true,
            sorters: [{
                property: 'treeId',
                direction: 'ASC'
            }],

            /* filters: [{
             property: 'firstName',
             value: /Peter/
             }]*/

            proxy: {
                type: 'ajax',
                api: {
                    read: '/appMis/department/readDepartment?params.dep_tree_id='
                },
                noCache: true,
                actionMethods: {
                    read: 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: 'departments',
                    totalProperty: "totalCount"
                },
                writer: {
                    type: 'json'
                }
            },
            listeners: {
                load: function (r, option, success) {//监听过滤器salarymis\TimeOutFilter.groovy，且对Ajax起作用，r.totalCount=-1时，网页过时了
                    //alert("已经监听职工基本信息");
                    timeout1(r.totalCount, "单位信息")//在App.js中timeout1判断网页是否超时
                },
                update: function (store, record) {
                    var me=this;
                    Ext.Ajax.request({
                        url: '/appMis/department/update',
                        params: {
                            id: record.get("id"),
                            department: record.get("department"),
                            dwjc: record.get("dwjc"),
                            dwtjsl: record.get("dwtjsl"),
                            gzzescbm: record.get("gzzescbm"),
                            departmentCode: record.get("departmentCode"),
                            lsgx: record.get("lsgx"),
                            lsxt: record.get("lsxt"),
                            xzhq: record.get("xzhq"),
                            dwxz: record.get("dwxz"),
                            czbkxs: record.get("czbkxs"),
                            jblq: record.get("jblq"),
                            dwjb: record.get("dwjb"),
                            sshy: record.get("sshy"),
                            cgqk: record.get("cgqk"),
                            zgbmqk: record.get("zgbmqk"),
                            sjgzzgbm: record.get("sjgzzgbm"),
                            bzs: record.get("bzs"),
                            syrs: record.get("syrs"),
                            bzqk: record.get("bzqk"),
                            bzpwqk: record.get("bzpwqk"),
                            hsxz: record.get("hsxz"),
                            leader: record.get("leader"),
                            contacts: record.get("contacts"),
                            phone: record.get("phone"),
                            fax: record.get("fax"),
                            postcode: record.get("postcode"),
                            address: record.get("address"),
                            treeId: record.get("treeId"),
                            glyph: record.get("glyph")
                        },
                        success: function (resp, opts) {//成功后的回调方法
                            if (resp.responseText == 'success') {
                                me.reloadtreemenu();//有滞后！！！！
                                var panelStore = (Ext.getCmp('departmentgrid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                            } else {
                                Ext.Msg.show({
                                    title: '操作提示 ',
                                    msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                                    buttons: Ext.MessageBox.OK
                                })
                                setTimeout(function () {
                                    var panelStore = (Ext.getCmp('departmentgrid')).getStore();
                                    panelStore.loadPage(panelStore.currentPage);
                                    Ext.Msg.hide();
                                }, 2500);
                            }
                        },
                        failure: function (resp, opts) {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                var panelStore = (Ext.getCmp('departmentgrid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                                Ext.Msg.hide();
                            }, 2500)
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
