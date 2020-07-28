
Ext.define('appMis.view.desktop.StudentWindow', {
    extend: 'Ext.ux.desktop.Module',
    requires: [

      ],
    id:'student',
    init : function(){
        this.launcher = {
            text: '在校学生',
            iconCls:'student'
        }
    },
    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('student');
        if(!win){
            win = desktop.createWindow({
                id: 'student',
                title:'在校学生',
                 width:'100%',
                 height:'95%',
                iconCls: 'student',
                animCollapse:false,
                resizable:false,
                minimizable:false,
                maximizable:false,
               // header:false,
                border: false,
                 layout: 'fit',
                items: [
                    {
                        xtype: 'app-studentmain',
                        listeners: {
                            beforerender: function () {
                                authRead=false//控制表可读写,   //如authRead=true//控制表不可读写，则【在职人员】无法显示！！！！！！！
                                var mainleft = Ext.getCmp("studentmainleft");
                                var main1 = Ext.getCmp("studentcontentpanel");
                                main1.remove(main1.getActiveTab());
                                //if((Roleflag=="单位管理员")||(Roleflag=="单位审核人")||(Roleflag=="主管单位审核人")){
                                if((loginUserRole=="单位管理员")||(loginUserRole=="单位审核人")) {
                                    currentTreeNode ="p"+loginUserDepartmentTreeid;
                                    Ext.getCmp("studentmainleft").hide();//隐藏左边的单位目录树
                                    var departmentTitle = "";
                                    var mainstudent;
                                    var panel = Ext.getCmp('studentDetails');
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
                                        mainstudent = Ext.create('appMis.view.student.StudentViewport', {
                                            title: departmentDetailTitle,
                                            glyph: 'xf007@FontAwesome',//cls:'x-btn-text-icon', // icon:'images/user/user_orange.png',
                                            id: 'studentDetails',
                                            iconCls: 'tabs',
                                            closable: false
                                        });
                                        (Ext.getCmp('studentgrid')).setStore('StudentStore');
                                        (Ext.getCmp('bbarEmp')).setStore('StudentStore');//分页
                                        var panelStore = (Ext.getCmp('studentgrid')).getStore();
                                        panelStore.proxy.api.read = "/appMis/student/readStudent?dep_tree_id=" + currentTreeNode.substr(1);
                                        panelStore.sort('studentCode', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                                   }
                                }else{
                                    // mainstudent=Ext.create('appMis.view.student.StudentMainpageViewport');
                                }
                                main1.add(mainstudent);
                                main1.setActiveTab(mainstudent);
                                mainleft.setTitle("在校学生")
                            }
                        }
                    }
                ]
            });
        }
        return win;
    }
});
