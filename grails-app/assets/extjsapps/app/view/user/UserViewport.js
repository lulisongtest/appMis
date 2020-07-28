var view;
var view1;//选择单位弹出的窗口
Ext.define('appMis.view.user.UserViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userviewport',
    layout:{
        type: 'vbox',
        pack: 'start',
        align: 'stretch'
    },
    defaults: {
        autoScroll: true,
        bodyPadding: 0,
        border: 0,
        padding: 0
    },
    width: '100%',
    items: [
        {
            xtype: 'toolbar',
            height: 40,
            items: [
                {
                    text: '新增',
                    //glyph:'xf016@FontAwesome',
                    icon: 'tupian/edit_add.png',
                    action: 'add'
                }, {
                    text: '增加行',
                     //glyph:'xf022@FontAwesome',
                    icon: 'tupian/edit_remove.png',
                    action: 'addRow'
                }, {
                    text: '修改',
                    // glyph:'xf044@FontAwesome',
                    icon: 'tupian/pencil.png',
                    action: 'modify'
                }, {
                    text: '删除',
                    //glyph:'xf014@FontAwesome',
                    icon: 'tupian/cancel.png',
                    action: 'delete'
                },
                {
                    xtype: 'combobox',
                    fieldLabel: '按角色查询',
                    labelAlign: 'right',
                    width: 220,
                    labelWidth: 70,
                    emptyText: '请输入角色名称',
                    id: 'queryAuthority',
                    displayField: 'chineseAuthority',
                    queryMode: 'remote',
                    store: 'RoleQueryStore',
                    valueField: 'chineseAuthority',
                    value:'全部',
                    listeners: {
                        change: function (combo) {
                            var queryAuthority = Ext.getCmp("queryAuthority").getValue()
                            var queryDepartment = Ext.getCmp("queryDepartment").getValue()
                            var queryUsername = Ext.getCmp("queryTruename").getValue()
                            var panelStore = (Ext.getCmp("usergrid")).getStore();
                            panelStore.proxy.api.read = "/appMis/user/readUser?queryAuthority=" + queryAuthority + "&queryDepartment=" + queryDepartment + "&queryUsername=" + queryUsername
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'combobox',
                    fieldLabel: '按单位查询',
                    labelAlign: 'right',
                    width: 280,
                    labelWidth: 70,
                    emptyText: '请输入单位名称',
                    id: 'queryDepartment',
                    displayField: 'department',
                    queryMode: 'local',
                    store: 'DepartmentQueryStore',
                    valueField: 'department',
                    value:'全部',
                    listeners: {
                        change: function (combo) {
                            var queryAuthority = Ext.getCmp("queryAuthority").getValue()
                            var queryDepartment = Ext.getCmp("queryDepartment").getValue()
                            var queryUsername = Ext.getCmp("queryTruename").getValue()
                            var panelStore = (Ext.getCmp("usergrid")).getStore();
                            panelStore.proxy.api.read = "/appMis/user/readUser?queryAuthority=" + queryAuthority + "&queryDepartment=" + queryDepartment + "&queryUsername=" + queryUsername
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'queryTruename',
                    emptyText: '请输入真实姓名',
                    fieldLabel: '真实姓名',
                    labelAlign: 'right',
                    width: 190,
                    labelWidth: 70,
                    listeners: {
                        change: function (combo) {
                            var queryAuthority = Ext.getCmp("queryAuthority").getValue()
                            var queryDepartment = Ext.getCmp("queryDepartment").getValue()
                            var queryUsername = Ext.getCmp("queryTruename").getValue()
                            var panelStore = (Ext.getCmp("usergrid")).getStore();
                            panelStore.proxy.api.read = "/appMis/user/readUser?queryAuthority=" + queryAuthority + "&queryDepartment=" + queryDepartment + "&queryUsername=" + queryUsername
                            panelStore.loadPage(1);
                        }
                    }
                }

                ]
        },
        {
            xtype: 'toolbar',
            height: 33,
            items: [
                {
                    xtype: 'button',
                    width: 200,
                    text: '初次生成所有单位用户信息',
                    glyph: 'xf014@FontAwesome',
                    action: 'generateUser'
                },
                {
                    xtype: 'button',
                    width: 200,
                    text: '导出所有用户信息到Excel',
                    icon: 'images/tupian/exportToExcel.png',
                    action: 'exportToExcel'
                },
                {
                    xtype: 'button',
                    width: 200,
                    text: '从Excel导入所有用户信息',
                    icon: 'images/tupian/exportToExcel.png',
                    action: 'importFromExcel'
                },
                {
                    xtype: 'form',
                    height:40,
                    baseCls: 'x-plain',
                    url: "tmp",//上传服务器的地址
                    fileUpload: true,
                    id:'userexcelfilePath',//放在此可用Ext.getCmp('dynamicTableexcelfilePath').getForm().findField('dynamicTableexcelfilePath'))获取name: 'dynamicTableexcelfilePath',的值。
                    defaultType: 'textfield',
                    items: {
                        xtype: 'fileuploadfield',
                        width: 280,
                        labelWidth: 0,
                        labelAlign: 'right',
                        fieldLabel: '',
                        name: 'userexcelfilePath',
                        //id: 'dynamicTableexcelfilePath',//放在此，第二次进入时无法显示查找按钮
                        buttonText: '选择文件',
                        blankText: '文件名不能为空'
                    }
                }]
        },
        {
            xtype: 'panel',
            flex: 1,
            layout:{
                type: 'vbox',
                pack: 'start',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'usergridviewport',
                    id: 'usergrid',
                    flex: 1,
                    listeners: {
                        beforerender: function () {
                            var panelStore=Ext.getCmp("usergrid").getStore();
                            panelStore.pageSize=pageSize;
                            var queryAuthority = Ext.getCmp("queryAuthority").getValue()
                            var queryDepartment = Ext.getCmp("queryDepartment").getValue()
                            var queryUsername = Ext.getCmp("queryTruename").getValue()
                            panelStore.proxy.api.read = "/appMis/user/readUser?queryAuthority=" + queryAuthority + "&queryDepartment=" + queryDepartment + "&queryUsername=" + queryUsername
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        }
    ]
});