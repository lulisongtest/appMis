

Ext.define('appMis.view.main.ProcessAdminMainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processadminmain',
    init: function () {
        var vm = this.getView().getViewModel();
        //vm.bind('{monetary.value}', 'onMonetaryChange', this) // 绑定金额单位修改过后需要去执行的程序
        this.control({
            'processadminmain > processadminmainleft > processadmintreemainmenu': {
                itemexpand:this.itemexpand1,
                itemclick:this.itemclick1
                // itemmousedown: this.loadMenu1
            }
        })
    },
    //单击单位目录树展开后
    itemexpand1: function (selModel, record) {
        //alert("单击单位目录树展开后")
    },
//单击目录树的节点后
    itemclick1: function (selModel, record) {
        if (((Roleflag == '主管单位审核人')||(Roleflag == '区人社局审核人'))&&((record.get('id').toString()=='p1000')||(record.get('id').toString().length<Treeidflag.length))){
            return//不能点击本级的上级目录
        }else{
            currentTreeNode = record.get('id').toString();
        }
       // var departmentTitle = "";
        var currentDepartment = Ext.create('appMis.model.DepartmentModel');
        var currentEmployee = Ext.create('appMis.model.EmployeeModel');
        var currentDepartment1, currentEmployee1;
        var main1 = Ext.getCmp("processAdmincontentpanel");
        var panel = Ext.getCmp(currentTreeNode + 'p');
        if (!panel) {
            main1.remove(main1.getActiveTab());
            if (currentTreeNode.substr(0, 1) == 'p') {//p表示是单位目录，s表示系统设置
                //alert("ppppp==="+'department/readDepartmentTitle?dep_tree_id=' + currentTreeNode.substr(1))
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
            //if(currentTreeNode=='p1000')departmentDetailTitle='全部';
            if(currentTreeNode=='p1000'||currentTreeNode=='p000')departmentDetailTitle='克拉玛依市';
            var panelemployee = Ext.create('appMis.view.processAdmin.ProcessAdminViewport', {
                title: departmentDetailTitle,
                glyph: 'xf007@FontAwesome',
                //cls:'x-btn-text-icon',
                // icon:'images/user/user_orange.png',
                id: currentTreeNode + 'p',
                iconCls: 'tabs',
                closable: true
            });
            main1.add(panelemployee);
            main1.setActiveTab(panelemployee);
        }else{
            main1.setActiveTab(panel);
        }

    }
});
