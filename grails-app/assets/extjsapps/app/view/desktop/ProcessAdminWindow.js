/*!
 * Ext JS Library
 * Copyright(c) 2006-2014 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */


Ext.define('appMis.view.desktop.ProcessAdminWindow', {
    extend: 'Ext.ux.desktop.Module',
    requires: [
    ],
    id:'processAdmin',
    init : function(){
        this.launcher = {
            text: '事务管理',
            iconCls:'processAdmin'
        }
    },
    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('processAdmin');
        if(!win){
            win = desktop.createWindow({
                id: 'processAdmin',
                title:'事务管理',
                width:'100%',
                height:'95%',
                iconCls: 'processAdmin',
                animCollapse:false,
                resizable:false,
                minimizable:false,
                maximizable:false,
                // header:false,
                border: false,
                //defaultFocus: 'notepad-editor', EXTJSIV-1300

                // IE has a bug where it will keep the iframe's background visible when the window
                // is set to visibility:hidden. Hiding the window via position offsets instead gets
                // around this bug.

                // hideMode: 'offsets',
                layout: 'fit',
                items: [
                    {
                        xtype: 'app-processadminmain',
                        listeners: {
                                beforerender: function () {
                                    //alert("loginUserDepartment====="+loginUserDepartment)
                                    var mainleft = Ext.getCmp("processadminmainleft");
                                    var main1 = Ext.getCmp("processAdmincontentpanel");
                                    main1.remove(main1.getActiveTab());
                                    if((loginUserRole=="单位管理员")||(loginUserRole=="单位审核人")) {
                                        currentTreeNode ="p"+loginUserDepartmentTreeid;
                                        Ext.getCmp("processadminmainleft").hide();
                                    }else{
                                        //currentTreeNode = record.get('id').toString();
                                        if(loginUserDepartmentTreeid=='001'){
                                            currentTreeNode="p1000"  ////针对市人社局审核人，因是tree_id='001',首次是对全市单位
                                        }else{
                                            currentTreeNode ="p"+loginUserDepartmentTreeid;
                                        }

                                        //currentTreeNode =loginUserDepartmentTreeid
                                    }
                                  //  var main1 = Ext.getCmp("processAdmincontentpanel");
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
                                        //departmentDetail=record.get('text');
                                        departmentDetail=loginUserDepartment
                                        //var  departmentDetailTitle=departmentTitle + departmentDetail;
                                        var  departmentDetailTitle=loginUserDepartment;
                                        //if(currentTreeNode=='p1000')departmentDetailTitle='全部';
                                        if(currentTreeNode=='p1000'||currentTreeNode=='p000')departmentDetailTitle='克拉玛依市';
                                        var panelemployee = Ext.create('appMis.view.processAdmin.ProcessAdminViewport', {
                                            title: departmentDetailTitle,
                                            glyph: 'xf007@FontAwesome',
                                            //cls:'x-btn-text-icon',
                                            // icon:'images/user/user_orange.png',
                                            id: currentTreeNode + 'p',
                                            iconCls: 'tabs',
                                            closable: false
                                        });
                                        main1.add(panelemployee);
                                        main1.setActiveTab(panelemployee);
                                    }else{
                                        main1.setActiveTab(panel);
                                    }


                                }



                            /*beforerender: function () {
                                currentTreeNode ="p"+Treeidflag;
                                var mainleft = Ext.getCmp("processadminmainleft");
                                var main1 = Ext.getCmp("processAdmincontentpanel");//来自于'appMis.view.main.region.ProcessAdminCenter'
                                main1.remove(main1.getActiveTab());
                                //if((Roleflag=="单位管理员")||(Roleflag=="单位审核人")||(Roleflag=="主管单位审核人")){
                                if((loginUserRole=="单位管理员")||(loginUserRole=="单位审核人")) {
                                    //currentTreeNode ="p"+Treeidflag;
                                    Ext.getCmp("processadminmainleft").hide();//隐藏左边的单位目录树
                                    //var departmentTitle = "";
                                    var mainprocessadmin;
                                    var panel = Ext.getCmp(currentTreeNode + 'p');
                                    if (!panel) {
                                        main1.remove(main1.getActiveTab());
                                        if (currentTreeNode.substr(0, 1) == 'p') {//p表示是单位目录，s表示系统设置
                                            Ext.Ajax.request({
                                                url: '/appMis/department/readDepartmentTitle?dep_tree_id=' + currentTreeNode.substr(1),
                                                async: false,//同步
                                                success://回调函数
                                                    function (resp, opts) {//成功后的回调方法
                                                        var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                                        departmentTitle = obj.departmentTitled2;//为Json对象,详细有上级单位关系的单位名称
                                                    }
                                            })
                                        }

                                        Ext.Ajax.request({//获取当前用户的单位信息的名称,
                                            url: '/appMis/department/readDepartmentNameByTreeId?dep_tree_id=' + currentTreeNode.substr(1),
                                            async: false,//同步
                                            success://回调函数
                                                function (resp, opts) {//成功后的回调方法
                                                    var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                                    departmentDetail = obj.departmentNames;//为Json对象,详细有上级单位关系的单位名称
                                                }
                                        });
                                        //departmentDetail = record.get('text');
                                        departmentAll=departmentTitle + departmentDetail;
                                        mainprocessadmin = Ext.create('appMis.view.processAdmin.ProcessAdminViewport', {
                                            title: departmentAll,
                                            glyph: 'xf007@FontAwesome', //cls:'x-btn-text-icon',// icon:'images/user/user_orange.png',
                                            id: currentTreeNode + 'p',
                                            iconCls: 'tabs',
                                            closable: true
                                        });
                                    }
                                }else{
                                    mainprocessadmin=Ext.create('appMis.view.processAdmin.ProcessAdminViewport', {
                                        title: "全部",
                                        glyph: 'xf007@FontAwesome', //cls:'x-btn-text-icon',// icon:'images/user/user_orange.png',
                                        id: currentTreeNode + 'p',
                                        iconCls: 'tabs',
                                        closable: true
                                    });
                                }
                                main1.add(mainprocessadmin);
                                main1.setActiveTab(mainprocessadmin);
                                mainleft.setTitle("事务管理")
                            }*/
                        }
                    }
                ]
            });
        }
        return win;
    }
});
