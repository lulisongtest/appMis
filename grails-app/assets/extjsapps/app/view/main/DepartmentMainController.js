
Ext.define('appMis.view.main.DepartmentMainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.departmentmain',
    requires: [
    ],
    init: function () {
        var vm = this.getView().getViewModel();
        //vm.bind('{monetary.value}', 'onMonetaryChange', this) // 绑定金额单位修改过后需要去执行的程序
        this.control({
            'departmentmain > departmentmainleft > departmenttreemainmenu': {
                itemexpand:this.itemexpand1,
                itemclick:this.itemclick1
                // itemmousedown: this.loadMenu1
            }
        })
    },
    itemexpand1: function (selModel, record) {
    },
//单击单位目录树后
    itemclick1: function (selModel, record) {
        currentTreeNode = record.get('id').toString();
        if ((loginUserRole != '管理员') && (currentTreeNode.substr(1) != loginUserDepartmentTreeid) &&(((""+loginUserRole).substring((""+loginUserRole).length-3))!="审核人"))return;
        var departmentTitle = "";
        var currentDepartment = Ext.create('appMis.model.DepartmentModel');
        var currentDepartment1;
        var main1 = Ext.getCmp("departmentcontentpanel");
        var mainleft = Ext.getCmp("departmentmainleft");  //  alert("loadMenu1==loadMenu1=="+"===="+record.get('id')+"====="+record.get('text')+"====="+mainleft.getTitle())
        var panel = Ext.getCmp('departmentDetails');
        if (!panel) {
            main1.remove(main1.getActiveTab());
            if (currentTreeNode.substr(0, 1) == 'p') {//p表示是单位目录，s表示系统设置
                Ext.Ajax.request({
                    url: '/appMis/department/readDepartmentTitle?dep_tree_id=' + currentTreeNode.substr(1),
                    async: false,//同步
                    success://回调函数
                        function (resp, opts) {//成功后的回调方法
                              departmentTitle = resp.responseText;//为Json对象,详细有上级单位关系的单位名称
                        }
                })
            }
            departmentDetail=record.get('text');
            departmentName=record.get('text');
            var  departmentDetailTitle=departmentTitle + departmentDetail;
            if(currentTreeNode=='p1000'||currentTreeNode=='p000')departmentDetailTitle='克拉玛依市';
            if ((currentTreeNode.length <= 7) || (currentTreeNode == 'p1000')) {
                //单位分类
                var panel = Ext.create('appMis.view.department.DepartmentViewport', {
                    title: departmentDetailTitle,
                    glyph: 'xf007@FontAwesome',//cls:'x-btn-text-icon',// icon:'images/user/user_orange.png',
                    id: 'departmentDetails',
                    iconCls: 'tabs',
                    closable: true
                });
                var panelStore =(Ext.getCmp('departmentgrid')).getStore();
                panelStore.proxy.api.read = "/appMis/department/readDepartment?dep_tree_id=" + currentTreeNode.substr(1)
                panelStore.sort('treeId', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                if ((currentTreeNode.toString()).length <= 7) {//调整单位详细信息和下属单位信息显示界面
                    if (Ext.getCmp("deparmentDetail"))Ext.getCmp("deparmentDetail").setHidden(true)
                } else {
                    if (Ext.getCmp("deparmentDetail"))Ext.getCmp("deparmentDetail").setHidden(false)
                }
                main1.add(panel);
                main1.setActiveTab(panel);

            } else {
                //具体单位
                 Ext.Ajax.request({
                    url: '/appMis/department/readDepartmentByTreeId?dep_tree_id=' + currentTreeNode.substr(1),
                    async: false,//同步,true异步
                    success://回调函数
                        function (resp, opts) {//成功后的回调方法
                           var obj1 = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象// alert("==="+(obj1.department2)[0].department)
                           currentDepartment1 = (obj1.department2)[0];//为Json对象,本级单位的详细信息
                        }
                })
                var panel = Ext.create('appMis.view.department.DepartmentViewport', {
                    title: departmentDetailTitle,
                    glyph: 'xf007@FontAwesome',//cls:'x-btn-text-icon',// icon:'images/user/user_orange.png',
                    id: 'departmentDetails',
                    iconCls: 'tabs',
                    closable: true
                });
                var panelStore =(Ext.getCmp('departmentgrid')).getStore();
                panelStore.proxy.api.read = "/appMis/department/readDepartment?dep_tree_id=" + currentTreeNode.substr(1)
                panelStore.sort('treeId', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                form = panel.down('form');//把当前点击的单位信息填入form中
                if ((record.get('id').toString()).length <= 9) {//调整单位详细信息和下属单位信息显示界面
                    if (Ext.getCmp("deparmentDetail"))Ext.getCmp("deparmentDetail").setHidden(true)
                } else {
                    if (Ext.getCmp("deparmentDetail"))Ext.getCmp("deparmentDetail").setHidden(false)
                }
                currentDepartment.set(currentDepartment1);
                form.loadRecord(currentDepartment);
               /* panelStore =(Ext.getCmp('departmentJxkhgrid')).getStore();
                panelStore.proxy.api.read = '/appMis/department/readDepartmentJxkh?dep_tree_id=' + currentTreeNode.substr(1);
                panelStore.sort('nd', 'DESC');
                panelStore.loadPage(1)*/

                main1.add(panel);
                main1.setActiveTab(panel);
            }
        } else {
            if (currentTreeNode.substr(0, 1) == 'p') {//p表示是单位目录，s表示系统设置
                Ext.Ajax.request({
                    url: '/appMis/department/readDepartmentTitle?dep_tree_id=' + currentTreeNode.substr(1),
                    async: false,//同步
                    success://回调函数
                        function (resp, opts) {//成功后的回调方法
                            departmentTitle = resp.responseText;//为Json对象,详细有上级单位关系的单位名称
                        }
                })
            }
            departmentDetail=record.get('text');
            departmentName=record.get('text');
            var  departmentDetailTitle=departmentTitle + departmentDetail;
           // if(currentTreeNode=='p1000')departmentDetailTitle='全部';
            if(currentTreeNode=='p1000'||currentTreeNode=='p000')departmentDetailTitle='克拉玛依市';
            if ((currentTreeNode.length <= 7) || (currentTreeNode == 'p1000')) {
                panel.setTitle(departmentDetailTitle)
                var panelStore =(Ext.getCmp('departmentgrid')).getStore();
                panelStore.proxy.api.read = "/appMis/department/readDepartment?dep_tree_id=" + currentTreeNode.substr(1)
                panelStore.sort('treeId', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                if ((currentTreeNode.toString()).length <= 7) {//调整单位详细信息和下属单位信息显示界面
                    if (Ext.getCmp("deparmentDetail"))Ext.getCmp("deparmentDetail").setHidden(true)
                } else {
                    if (Ext.getCmp("deparmentDetail"))Ext.getCmp("deparmentDetail").setHidden(false)
                }
            } else {
                Ext.Ajax.request({//获取当前点击的单位信息
                    url: '/appMis/department/readDepartmentByTreeId?dep_tree_id=' + currentTreeNode.substr(1),
                    async: false,//同步,true异步
                    success://回调函数
                        function (resp, opts) {//成功后的回调方法
                            var obj1 = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象// alert("==="+(obj1.department2)[0].department)
                            currentDepartment1 = (obj1.department2)[0];//为Json对象,本级单位的详细信息
                        }
                })
                panel.setTitle(departmentDetailTitle)
                var panelStore = (Ext.getCmp('departmentgrid')).getStore();
                panelStore.proxy.api.read = "/appMis/department/readDepartment?dep_tree_id=" + currentTreeNode.substr(1)
                panelStore.sort('treeId', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                form = panel.down('form');//把当前点击的单位信息填入form中
                if ((record.get('id').toString()).length <= 9) {//调整单位详细信息和下属单位信息显示界面
                    if (Ext.getCmp("deparmentDetail")) Ext.getCmp("deparmentDetail").setHidden(true)
                } else {
                    if (Ext.getCmp("deparmentDetail")) Ext.getCmp("deparmentDetail").setHidden(false)
                }
                currentDepartment.set(currentDepartment1);
                form.loadRecord(currentDepartment);
               /* panelStore =(Ext.getCmp('departmentJxkhgrid')).getStore();
                panelStore.proxy.api.read = '/appMis/department/readDepartmentJxkh?dep_tree_id=' + currentTreeNode.substr(1);
                panelStore.sort('nd', 'DESC');
                panelStore.loadPage(1)*/
            }
        }
    }
})
