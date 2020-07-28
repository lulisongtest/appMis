/**
 * This class is the controller for the main view for the application. It is specified as
 * the "controller" of the Main view class.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */

Ext.define('appMis.view.main.SystemSettingMainController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.systemSettingmain',
    init: function () {
        var vm = this.getView().getViewModel();
        //vm.bind('{monetary.value}', 'onMonetaryChange', this) // 绑定金额单位修改过后需要去执行的程序
        this.control({
            'systemSettingmain > systemSettingmainleft > systemSettingtreemenu': {
                itemexpand:this.itemexpand1,
                itemclick:this.loadMenu1
                //itemmousedown: this.loadMenu1
            }
        })
    },
//单击目录树展开后
    itemexpand1: function (selModel, record) {
    },
//单击单位目录树后
    loadMenu1: function (selModel, record) {
        currentTreeNode = record.get('id').toString();
        if (loginUserRole != '管理员') return
        var main1 = Ext.getCmp("systemSettingcenter");
        var panel = Ext.getCmp(currentTreeNode + 'p');
        if (!panel) {
            main1.remove(main1.getActiveTab());
            switch(record.get('text')){
                case '用户管理' ://用户帐号管理
                    var  panel用户信息管理 = Ext.create('appMis.view.user.UserViewport',{
                        title:  record.get('text'),
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel用户信息管理);
                    main1.setActiveTab(panel用户信息管理);
                    break;
                case '角色管理' ://用户角色管理
                    var  panel用户角色管理 = Ext.create('appMis.view.role.RoleViewport',{
                        title:  record.get('text'),
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user_edit.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel用户角色管理);
                    main1.setActiveTab(panel用户角色管理);
                    break;
                case '权限管理' ://用户访问控制管理
                    var  panel13 = Ext.create('appMis.view.requestmap.RequestmapViewport',{
                        title:  record.get('text'),
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user_red.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel13);
                    main1.setActiveTab(panel13);
                    Ext.getCmp("requestmapgrid").getView().refresh();//自动调整列宽,必须放在下面语句之前！！
                    Ext.each(Ext.getCmp("requestmapgrid").columnManager.getColumns(), function (column) {
                        if (!column.resizeDisabled) {
                            column.autoSize();
                        }
                    });
                    break;
                case '数据表管理' ://用户访问控制管理
                    main1.remove(main1.getActiveTab());
                    var  panel13 = Ext.create('appMis.view.dynamicTable.DynamicTableViewport',{
                        title:  record.get('text'),
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user_red.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel13);
                    main1.setActiveTab(panel13);
                    break;
                case '享受待遇' ://用户访问控制管理
                    var  panel13 = Ext.create('appMis.view.requestmap.RequestmapViewport',{
                        title:  record.get('text'),
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user_red.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel13);
                    main1.setActiveTab(panel13);
                    Ext.getCmp("requestmapgrid").getView().refresh();//自动调整列宽,必须放在下面语句之前！！
                    Ext.each(Ext.getCmp("requestmapgrid").columnManager.getColumns(), function (column) {
                        if (!column.resizeDisabled) {
                            column.autoSize();
                        }
                    });
                    break;
                case '职级类型' ://用户访问控制管理
                    var  panel13 = Ext.create('appMis.view.requestmap.RequestmapViewport',{
                        title:  record.get('text'),
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user_red.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel13);
                    main1.setActiveTab(panel13);
                    Ext.getCmp("requestmapgrid").getView().refresh();//自动调整列宽,必须放在下面语句之前！！
                    Ext.each(Ext.getCmp("requestmapgrid").columnManager.getColumns(), function (column) {
                        if (!column.resizeDisabled) {
                            column.autoSize();
                        }
                    });
                    break;
                case '工种类型' ://用户访问控制管理
                    var  panel13 = Ext.create('appMis.view.requestmap.RequestmapViewport',{
                        title:  record.get('text'),
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user_red.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel13);
                    main1.setActiveTab(panel13);
                    Ext.getCmp("requestmapgrid").getView().refresh();//自动调整列宽,必须放在下面语句之前！！
                    Ext.each(Ext.getCmp("requestmapgrid").columnManager.getColumns(), function (column) {
                        if (!column.resizeDisabled) {
                            column.autoSize();
                        }
                    });
                    break;
               // case '系统设置' ://用户访问控制管理
                case '津贴类型' ://用户访问控制管理
                    var  panel13 = Ext.create('appMis.view.requestmap.RequestmapViewport',{
                        title:  record.get('text'),
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user_red.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel13);
                    main1.setActiveTab(panel13);
                    Ext.getCmp("requestmapgrid").getView().refresh();//自动调整列宽,必须放在下面语句之前！！
                    Ext.each(Ext.getCmp("requestmapgrid").columnManager.getColumns(), function (column) {
                        if (!column.resizeDisabled) {
                            column.autoSize();
                        }
                    });
                    break;
                case '公务员类' ://公务员类工资标准
                case '参照公务员法管理人员' ://公务员类工资标准
                case '其他事业类' ://事业编制类工资标准
                case '义务教育类' ://企业编制类工资标准
                case '基层医疗卫生类' ://企业编制类工资标准
                case '公安类' ://公安类工资标准
                case '员额制类' ://公务员类工资标准
                case '石油企业工资类' ://石油企业编制类工资标准
                case '其他津补贴' ://其他津补贴标准
                    Ext.Ajax.request({//获取当前点击的单位信息
                        url: '/appMis/gzbz/readGzbzByTreeId?gzzd='+record.get('text'),
                        async:false,//同步,true异步
                        success: function (resp, opts) { }//成功后的回调方法
                    })
                    var  panel13= Ext.create('appMis.view.gzbz.GzbzViewport',{
                        title:  record.get('text')+"_工资标准",
                        // glyph:'xf007@FontAwesome',
                        cls:'x-btn-text-icon',
                        icon:'images/user/user_red.png',
                        id: record.get('id')+'p',
                        iconCls: 'tabs',
                        closable: true
                    });
                    main1.add(panel13);
                    main1.setActiveTab(panel13);
                    // Ext.getCmp("gzbzgrid").getView().refresh();//自动调整列宽,必须放在下面语句之前！！
                    // Ext.each(Ext.getCmp("gzbzgrid").columnManager.getColumns(), function (column) {
                    //      if (!column.resizeDisabled) {
                    //          column.autoSize();
                    //      }
                    //     });
                    break;
                case '初始化数据' :
                    break;
                case '数据备份' :
                    //alert("数据备份")
                    Ext.Ajax.request({
                        url: '/appMis/dynamicTable/dataBbackup',
                        async:false,
                        method: 'POST',
                        success: function(resp,o) {
                            var obj = eval('(' + resp.responseText + ')');//一样var obj = Ext.decode(resp.responseText)将获取的Json字符串转换为Json对象
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: "正在生成备份文件，请稍候......",
                                align: "centre",
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                Ext.Msg.hide();
                                Ext.MessageBox.buttonText.ok="完成"
                                Ext.Msg.show({
                                    title: '操作提示 ',
                                    msg: "<a href=\"http://"+window.location.host+"/appMis/static/backupdata/"+obj.info+"\"><b>保存该备份文件文件</b></a>",
                                    align: "centre",
                                    buttons: Ext.MessageBox.OK
                                })
                            }, 9500)//这是程序的缺陷！！备份文件生成的进程已经结束，但备份文件生成有个过程，这里是估计了个时间，等待文件生成完毕后再下载，否则下载的文件不完整！
                        },
                        failure: function (resp, opts) {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据备份失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 1500)
                        }
                    });
                    break;
                case '数据还原' :
                    var dataRestore= Ext.create("Ext.window.Window", {
                        id: 'EmployeeDataRestore',
                        width: 800,
                        height: 150,
                        title: '数据还原',
                        layout: 'fit',
                        autoShow: true,
                        modal: true,//创建模态窗口
                        items : [
                            {
                                xtype: 'panel',
                                border:false,
                                id: 'employeeDataRestorePanel',
                                height: 37,
                                layout: 'column',
                                padding: "1 0 0 0" ,
                                items: [
                                    {
                                        xtype: 'label',
                                        html: '&nbsp&nbsp&nbsp&nbsp',
                                        columnWidth: 1
                                    },
                                    {
                                        xtype: 'label',
                                        html: '&nbsp&nbsp&nbsp&nbsp',
                                        columnWidth: 0.1
                                    },
                                    {
                                        xtype: 'button',
                                        text: '数据还原',
                                        id: 'employeeDataRestore',
                                        columnWidth: 0.15,
                                        height: 31,
                                        // icon: 'images/tupian/exportToExcel.png',
                                        handler: function () {
                                            var form = Ext.getCmp('employeeDataRestoreForm')
                                            if (form.form.isValid()) {
                                                if (Ext.getCmp('employeeDataRestorefilepath').getValue() == '') {
                                                    Ext.Msg.alert('系统提示', '请选择还原数据文件');
                                                    return;
                                                }
                                                form.getForm().submit({
                                                    url:'/appMis/dynamicTable/dataRestore',
                                                    method:"POST",
                                                    async:false,
                                                    waitMsg: '正在处理',
                                                    waitTitle: '请等待',
                                                    success: function(form, o) {
                                                        Ext.Msg.show({
                                                            title: '操作提示1 ',
                                                            msg: ""+o.result.info+"",
                                                            buttons: Ext.MessageBox.OK
                                                        })
                                                        setTimeout(function () {
                                                            Ext.Msg.hide();
                                                            //  Ext.getCmp('EmployeeDataRestore').close();
                                                        },2500)
                                                    },
                                                    failure: function(fp, o) {
                                                        //alert("警告", "" + o.result.info + "");
                                                        Ext.Msg.show({
                                                            title: '操作提示2 ',
                                                            msg: ""+o.result.info+"",
                                                            buttons: Ext.MessageBox.OK
                                                        })
                                                        setTimeout(function () {
                                                            Ext.Msg.hide();
                                                            Ext.getCmp('EmployeeDataRestore').close();
                                                        },2500)
                                                    }
                                                })
                                            }
                                        }
                                    },
                                    {
                                        xtype: 'form',
                                        id: 'employeeDataRestoreForm',
                                        height: 35,
                                        columnWidth: 0.50,
                                        // baseCls: 'x-plain',
                                        url: "tmp",//上传服务器的地址
                                        fileUpload: true,
                                        //defaultType: 'textfield',
                                        items: [{
                                            xtype: 'fileuploadfield',
                                            width: 300,
                                            height: 34,
                                            labelWidth: 0,
                                            name: 'employeeDataRestorefilepath',
                                            id: 'employeeDataRestorefilepath',
                                            buttonText: '请选择还原数据文件',
                                            blankText: '上传文件不能为空'
                                        }]
                                    },{
                                        xtype: 'label',
                                        html: '&nbsp&nbsp&nbsp&nbsp',
                                        columnWidth: 0.02
                                    },{
                                        xtype: 'button',
                                        text: '完成',
                                        // id: 'kyxmWjshzt',
                                        columnWidth: 0.1, handler: function () {
                                            this.up("window").close();
                                        }
                                    },{
                                        xtype: 'button',
                                        text: '取消',
                                        //  id: 'kyxmWjshzt1',
                                        columnWidth: 0.1,
                                        handler: function () {
                                            this.up("window").close();
                                        }
                                    }
                                ]
                            }

                        ]
                    })
                    break;
            }
        }else{
          main1.setActiveTab(panel);
        }
    }
});
