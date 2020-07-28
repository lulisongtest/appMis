
Ext.define('appMis.view.main.${className}MainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.${propertyName}main',
    requires: [
    ],
    init: function () {

        var vm = this.getView().getViewModel();
        //vm.bind('{monetary.value}', 'onMonetaryChange', this) // 绑定金额单位修改过后需要去执行的程序
        this.control({
            '${propertyName}main > ${propertyName}mainleft > ${propertyName}treemainmenu': {
                itemexpand:this.itemexpand1,
                itemclick:this.itemclick1
            }
        })
    },
    //单击单位目录树展开后
    itemexpand1: function (selModel, record) {
    },
    //单击单位目录树后
    itemclick1: function (selModel, record) {
       if ((loginUserRole == '单位管理员')||(loginUserRole == '单位审核人')){
            currentTreeNode ="p"+loginUserDepartmentTreeid;
            Ext.getCmp("${propertyName}mainleft").hide();
        }else{
            if (((loginUserRole == '区临时聘用人员管理员')||(loginUserRole == '主管单位审核人')||(loginUserRole == '区人社局审核人'))&&((record.get('id').toString()=='p1000')||(record.get('id').toString().length<loginUserDepartmentTreeid.length))){
                return//不能点击本级的上级目录
            }else{
                currentTreeNode = record.get('id').toString();//取得新的单位节点
            }
        }
        var departmentTitle = "";
        var main1 = Ext.getCmp("${propertyName}contentpanel");
        var panel = Ext.getCmp('${propertyName}Details');
        if (!panel) {
             main1.remove(main1.getActiveTab());
             if (currentTreeNode.substr(0, 1) == 'p') {//p表示是单位目录，s表示系统设置
                Ext.Ajax.request({
                    url: '/appMis/department/readDepartmentTitle?dep_tree_id=' + currentTreeNode.substr(1),
                    async: false,//同步，等待AJAX返回值后，继续执行后续语句async: true,//异步
                    success://回调函数
                        function (resp, opts) {//成功后的回调方法
                             departmentTitle = resp.responseText;//为Json对象,详细有上级单位关系的单位名称
                        }
                })
             }
            departmentName=record.get('text');
            departmentDetailTitle=departmentTitle + departmentName;
            majorName=departmentTitle.split("==>")[1];
            collegeName=departmentTitle.split("==>")[0];
            if(currentTreeNode=='p1000'||currentTreeNode=='p000')departmentDetailTitle='克拉玛依市';
            panel = Ext.create('appMis.view.${propertyName}.${className}Viewport', {
                    title: departmentDetailTitle,
                    glyph: 'xf007@FontAwesome',
                    id: '${propertyName}Details',
                    iconCls: 'tabs',
                    closable: true
            });
            main1.add(panel);
            main1.setActiveTab(panel);
        }else{
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
            if ((currentTreeNode.length <= 4) || (currentTreeNode == 'p1000')) {//调整单位详细信息和下属单位信息显示界面
                //break;
            }
           // departmentDetail=record.get('text');
            departmentName=record.get('text');
            departmentDetailTitle=departmentTitle + departmentName;
            majorName=departmentTitle.split("==>")[1];
            collegeName=departmentTitle.split("==>")[0];
            if(currentTreeNode=='p1000'||currentTreeNode=='p000')departmentDetailTitle='克拉玛依市';
            panel.setTitle(departmentDetailTitle)
            var panelStore = (Ext.getCmp('${propertyName}grid')).getStore();
            panelStore.pageSize = pageSize;
            panelStore.proxy.api.read = '/appMis/${propertyName}/read?dep_tree_id=' + currentTreeNode.substr(1);
            panelStore.loadPage(1);
        }
    }
});
