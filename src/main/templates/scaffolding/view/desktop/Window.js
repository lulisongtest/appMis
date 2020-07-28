
Ext.define('appMis.view.desktop.${className}Window', {
    extend: 'Ext.ux.desktop.Module',
    requires: [

      ],
    id:'${propertyName}',
    init : function(){
        this.launcher = {
            text: '${domain[1]}',
            iconCls:'${propertyName}'
        }
    },
    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('${propertyName}');
        if(!win){
            win = desktop.createWindow({
                id: '${propertyName}',
                title:'${domain[1]}',
                width:'100%',
                height:'95%',
                iconCls: '${propertyName}',
                animCollapse:false,
                resizable:false,
                minimizable:false,
                maximizable:false,
               // header:false,
                border: false,
                 layout: 'fit',
                items: [
                    {
                        xtype: 'app-${propertyName}main',
                        listeners: {
                            beforerender: function () {
                                authRead=false//控制表可读写,   //如authRead=true//控制表不可读写，则【在职人员】无法显示！！！！！！！
                                var mainleft = Ext.getCmp("${propertyName}mainleft");
                                var main1 = Ext.getCmp("${propertyName}contentpanel");
                                main1.remove(main1.getActiveTab());
                                //if((Roleflag=="单位管理员")||(Roleflag=="单位审核人")||(Roleflag=="主管单位审核人")){
                                if((loginUserRole=="单位管理员")||(loginUserRole=="单位审核人")) {
                                    currentTreeNode ="p"+loginUserDepartmentTreeid;
                                    Ext.getCmp("${propertyName}mainleft").hide();//隐藏左边的单位目录树
                                    var departmentTitle = "";
                                    var main${propertyName};
                                    var panel = Ext.getCmp('${propertyName}Details');
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
                                        departmentDetail=departmentName
                                        var  departmentDetailTitle=departmentTitle + departmentDetail;
                                        if(currentTreeNode=='p1000'||currentTreeNode=='p000')departmentDetailTitle='克拉玛依市';
                                        main${propertyName} = Ext.create('appMis.view.${propertyName}.${className}Viewport', {
                                            title: departmentDetailTitle,
                                            glyph: 'xf007@FontAwesome',//cls:'x-btn-text-icon', // icon:'images/user/user_orange.png',
                                            id: '${propertyName}Details',
                                            iconCls: 'tabs',
                                            closable: false
                                        });
                                        (Ext.getCmp('${propertyName}grid')).setStore('${className}Store');
                                        (Ext.getCmp('bbarEmp')).setStore('${className}Store');//分页
                                        var panelStore = (Ext.getCmp('${propertyName}grid')).getStore();
                                        panelStore.proxy.api.read = "/appMis/${propertyName}/read${className}?dep_tree_id=" + currentTreeNode.substr(1);
                                        panelStore.sort('${propertyName}Code', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                                   }
                                }else{
                                    // main${propertyName}=Ext.create('appMis.view.${propertyName}.${className}MainpageViewport');
                                }
                                main1.add(main${propertyName});
                                main1.setActiveTab(main${propertyName});
                                mainleft.setTitle("${domain[1]}")
                            }
                        }
                    }
                ]
            });
        }
        return win;
    }
});
