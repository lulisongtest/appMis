
var newvalue1
var newvalue2
Ext.define('appMis.view.processTask.ProcessTaskContentRetireEmployeeViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processTaskContentRetireEmployeeViewport',
    defaults: {
        autoScroll: true,
        bodyPadding: 0,
        border: 0,
        padding: 0
    },
    height: 350,
    items: [
        {
            xtype: 'form',
            id: 'retireEmployeeform',
            //width: 1050,
            width: '100%',
            layout: 'column',
            defaults: {
                margins: '0 0 0 0',
                padding: 5
            },
            items: [
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'department',
                    fieldLabel:'单位名称',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'datefield',
                    columnWidth:0.25,
                    name: 'sbDate',
                    fieldLabel: '申报日期 ',
                    readOnly:true,
                    labelWidth:110,
                    format:'Y年m月d日',
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'shxm',
                    fieldLabel:'审核项目',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'employeeCode',
                    fieldLabel:'退休人员编码',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'name',
                    fieldLabel:'退休人员姓名',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'zbr',
                    fieldLabel:'制表人',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'shra',
                    fieldLabel:'单位审核人',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'shrb',
                    fieldLabel:'主管单位审核人',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'shrc',
                    fieldLabel:'区人社局审核人',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'shrd',
                    fieldLabel:'市人社局审核人',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },{
                    xtype: 'textfield',
                    hidden:true,
                    columnWidth:.25,
                    name : 'treeId'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.49,
                    name : 'bz',
                    fieldLabel:'备注',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },

                {
                    xtype: "button",
                    icon: 'tupian/search.png',
                    //padding: '3 0 0 0',
                    columnWidth: 0.15,
                    text: "查看退休前人员工资详细信息",
                    handler : function(){
                        //alert("procTaskContentVar====="+procTaskContentVar.zbr)
                       //从form中获取数据
                        var values=Ext.getCmp('retireEmployeeform').getValues()
                        values.sbDate = (values.sbDate).replace(/(年)|(月)/g, "-");
                        values.sbDate = (values.sbDate).replace(/(日)/g, "");
                        var departmentDetail=values.department//改为局部变量
                        var currentTreeNode="p"+obj.procTaskContentVar.treeId//改为局部变量
                        var departmentDetailTitle//改为局部变量
                        Ext.Ajax.request({
                            url: '/appMis/department/readDepartmentFullName?dep_tree_id=' + currentTreeNode.substr(1),
                            async: false,//同步
                            success://回调函数
                                function (resp, opts) {//成功后的回调方法
                                     var obj1 = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                     departmentDetailTitle=obj1.departmentFullNames//获取单位全名（带上级部门名称）
                                }
                        })
                        if(procInstId==""){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: "该事务已经被撤消中止，无法查看其它信息！",
                                align: "centre",
                                buttons: Ext.MessageBox.OK
                            })
                            return
                        }
                        Ext.create('Ext.window.Window',{
                            id: 'employeeDetailWin',
                            width: '100%',
                            height: '100%',
                            title: departmentDetailTitle+"__"+('显示当前职工【' + values.employeeCode + ' ' + values.name + ' 】的个人详细信息'),
                            autoShow: true,
                            modal: true,//创建模态窗口
                            layout:{
                                type: 'vbox',
                                pack: 'start',
                                align: 'stretch'
                            },
                            items :[
                                {
                                    xtype: 'panel',
                                    autoScroll: true,
                                    bodyPadding: 0,
                                    border: 0,
                                    flex:1,
                                    items: [{
                                        xtype: 'component',
                                        id: "displayEmployeeDetail",
                                        autoEl: {
                                            tag:'iframe',
                                            style:'height:100%;width:100%;border:none',
                                            src:'/appMis/employee/displayEmployeeDetail?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&employeeCode=' + values.employeeCode+ '&name=' + values.name+'&zbr='+values.zbr+'&shra='+values.shra+'&shrb='+values.shrb+'&shrc='+values.shrc+'&shrd='+values.shrd,
                                            //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                        }
                                    }]
                            }],
                            buttons:[
                                {
                                    text: '导出Excel文件',
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        Ext.MessageBox.buttonText.ok = "完成";
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + values.employeeCode+ values.name +"个人工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            align: "centre",
                                            buttons: Ext.MessageBox.OK
                                        })
                                    }
                                },
                                {
                                    text: '关闭',
                                    glyph: 'xf00d@FontAwesome',
                                    handler: function (button, e) {
                                        Ext.getCmp("employeeDetailWin").close()
                                    }
                                }],
                            listeners : {
                                'hide':function() {
                                    //win窗口关闭时，会执行本方法。 //删除职工详细信息生成Excel文件
                                    Ext.Ajax.request({
                                        url: '/appMis/employee/deleteExcelEmployeeDetail?employeeCode=' + values.employeeCode+'&name=' + values.name,
                                        ansync: false,
                                        method: 'POST',
                                        success://回调函数
                                            function (resp, opts) {
                                            }
                                    })
                                }
                            }
                        })
                    }
                },
                {
                    xtype:'label',
                    columnWidth: 0.015
                },
                {
                    xtype: "button",
                    icon: 'tupian/search.png',
                    //padding: '3 0 0 0',
                    columnWidth: 0.15,
                    text: "查看退休后工资详细信息",
                    handler : function(){
                        //alert("procTaskContentVar====="+procTaskContentVar.zbr)
                        //从form中获取数据
                        var values=Ext.getCmp('retireEmployeeform').getValues();
                        var departmentDetail=values.department;//改为局部变量
                        var currentTreeNode="p"+obj.procTaskContentVar.treeId;//改为局部变量
                        var departmentDetailTitle;//改为局部变量
                        Ext.Ajax.request({
                            url: '/appMis/department/readDepartmentFullName?dep_tree_id=' + currentTreeNode.substr(1),
                            async: false,//同步
                            success://回调函数
                                function (resp, opts) {//成功后的回调方法
                                    var obj1 = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                    departmentDetailTitle=obj1.departmentFullNames//获取单位全名（带上级部门名称）
                                }
                        });
                        Ext.create('Ext.window.Window',{
                            id: 'employeeRetireSalaryDetailWin',
                            width: '100%',
                            height: '100%',
                            title: departmentDetailTitle+"__"+('显示当前职工【' + values.employeeCode + ' ' + values.name + ' 】的个人退休工资详细信息'),
                            autoShow: true,
                            modal: true,//创建模态窗口
                            layout:{
                                type: 'vbox',
                                pack: 'start',
                                align: 'stretch'
                            },
                            items :[
                                {
                                    xtype: 'panel',
                                    autoScroll: true,
                                    bodyPadding: 0,
                                    border: 0,
                                    flex:1,
                                    items: [{
                                        xtype: 'component',
                                        id: "displayEmployeeRetireSalaryDetail",
                                        autoEl: {
                                            tag:'iframe',
                                            style:'height:100%;width:100%;border:none',
                                            src:'/appMis/employee/displayEmployeeRetireSalaryDetail?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&employeeCode=' + values.employeeCode+ '&name=' + values.name+'&zbr='+values.zbr+'&shra='+values.shra+'&shrb='+values.shrb+'&shrc='+values.shrc+'&shrd='+values.shrd,
                                            //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                        }
                                    }]
                                }],
                            buttons:[
                                {
                                    text: '导出Excel文件',
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        Ext.MessageBox.buttonText.ok = "完成";
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + values.employeeCode+ values.name +"个人退休工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            align: "centre",
                                            buttons: Ext.MessageBox.OK
                                        })
                                    }
                                },
                                {
                                    text: '关闭',
                                    glyph: 'xf00d@FontAwesome',
                                    handler: function (button, e) {
                                        Ext.getCmp("employeeRetireSalaryDetailWin").close()
                                    }
                                }],
                            listeners : {
                                'hide':function() {
                                    //win窗口关闭时，会执行本方法。 //删除职工详细信息生成Excel文件
                                    Ext.Ajax.request({
                                        url: '/appMis/employee/deleteExcelEmployeeRetireSalaryDetail?employeeCode=' + values.employeeCode+'&name=' + values.name,
                                        ansync: false,
                                        method: 'POST',
                                        success://回调函数
                                            function (resp, opts) {
                                            }
                                    })
                                }
                            }
                        })
                    }
                },
               {
                   xtype:"label",
                   columnWidth: 0.015
               },
               {
                   xtype: "button",
                   columnWidth: 0.18,
                   id:'reImportRetireSbExcel0',
                   text: '重新导入修改后的退休工资详细信息',
                  // hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",,//调用之前设置
                   icon: 'tupian/importFromExcel.png',
                   handler : function(){
                     // alert("重新导入修改后的退休工资详细信息====="+taskId)

                       //从form中获取数据
                      //  var values=Ext.getCmp('retireEmployeeform').getValues();
                      //  var departmentDetail=values.department;//改为局部变量
                      // var currentTreeNode="p"+obj.procTaskContentVar.treeId;//改为局部变量
                      //  var departmentDetailTitle;//改为局部变量

                       var form = Ext.getCmp('reImportRetireSbExcel')
                       if (form.form.isValid()) {
                           if (Ext.getCmp('retiresbexcelfilePath').getValue() == '') {
                               Ext.Msg.alert('系统提示', '请选择你要上传的文件');
                               return;
                           }
                           form.getForm().submit({
                               url: '/appMis/mainBusiness/reImportRetireExcel?taskId='+taskId+'&procDefId=' + procDefId+ '&executionId=' + executionId + '&procInstId=' + procInstId,

                              // url: '/appMis/mainBusiness/reImportRetireExcel?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&employeeCode=' + values.employeeCode+ '&name=' + values.name+'&zbr='+values.zbr+'&shra='+values.shra+'&shrb='+values.shrb+'&shrc='+values.shrc+'&shrd='+values.shrd,
                              //url: '/appMis/mainBusiness/reImportRetireExcel?dep_tree_id=' + currentTreeNode.substr(1),
                               method: "POST",
                               waitMsg: '正在从Excel表导入单位人员信息到数据库，请等待...',
                               waitTitle: '请等待',
                               success: function (fp, o) {
                                   if (o.result.success) {
                                       Ext.Msg.show({
                                           title: '操作提示 ',
                                           msg: "" + o.result.info + "",
                                           buttons: Ext.MessageBox.OK
                                       })
                                       setTimeout(function () {
                                           Ext.Msg.hide();
                                          // var panelStore = (Ext.getCmp("employeegrid")).getStore();
                                          // panelStore.loadPage(parseInt(panelStore.totalCount / panelStore.pageSize + 1));//定位grid到最后一页
                                           //panelStore.loadPage(1);//定位grid到第一页
                                       }, 5500)
                                   } else {
                                       Ext.Msg.show({
                                           title: '操作提示 ',
                                           msg: "" + o.result.info + "",
                                           buttons: Ext.MessageBox.OK
                                       })
                                       setTimeout(function () {
                                           Ext.Msg.hide();
                                       }, 3500);
                                   }
                               },
                               failure: function (fp, o) {
                                   //alert("警告", "" + o.result.info + "");
                                   Ext.Msg.show({
                                       title: '操作提示 ',
                                       msg: "" + o.result.info + "",
                                       buttons: Ext.MessageBox.OK
                                   })
                                   setTimeout(function () {
                                       Ext.Msg.hide();
                                   }, 3500)
                               }
                           })
                       }
                   }
               },
               {
                   xtype: 'form',
                   padding: '0 0 5 0',
                   id:'reImportRetireSbExcel',
                   columnWidth: 0.18,
                  // hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",//调用之前设置
                   height: 40,
                   url: "tmp",//上传服务器的地址
                   fileUpload: true,
                   items: [{
                       xtype: 'fileuploadfield',
                       width: 190,
                       labelWidth: 0,
                       // labelAlign: 'right',
                       //fieldLabel: '选择文件',
                       name: 'retiresbexcelfilePath',
                       id: 'retiresbexcelfilePath',
                       buttonText: '选择文件......',
                      // icon: 'tupian/search.png',
                       blankText: '文件名不能为空',
                       listeners: {
                           change: function (combo) {
                               //alert("value=============="+Ext.getCmp('employeeexcelfilePath').getValue())
                           }
                       }
                   }]
               }
            ]
        }
    ]
});
 
