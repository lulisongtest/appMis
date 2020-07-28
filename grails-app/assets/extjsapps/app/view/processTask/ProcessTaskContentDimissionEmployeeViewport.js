
var newvalue1
var newvalue2
Ext.define('appMis.view.processTask.ProcessTaskContentDimissionEmployeeViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processTaskContentDimissionEmployeeViewport',
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
            id: 'dimissionEmployeeform',
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
                    fieldLabel:'离职人员编码',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'name',
                    fieldLabel:'离职人员姓名',
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
                    columnWidth:.25,
                    name : 'bz',
                    fieldLabel:'备注',
                    readOnly:true,
                    labelWidth:110,
                    labelStyle:'padding-left:10px'
                },{
                    xtype: "button",
                    columnWidth: 0.18,
                    text: "查看离职人员详细信息",
                    handler : function(){
                        //alert("procTaskContentVar====="+procTaskContentVar.zbr)
                       //从form中获取数据
                        var values=Ext.getCmp('dimissionEmployeeform').getValues()
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
                    columnWidth: 0.02
                },
                {
                    xtype: "button",
                    columnWidth: 0.18,
                    text: "查看离职工资详细信息",
                    handler : function(){
                        //alert("查看离职工资详细信息")
                        //从form中获取数据
                        var values=Ext.getCmp('dimissionEmployeeform').getValues();
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
                            id: 'employeeDimissionSalaryDetailWin',
                            width: '100%',
                            height: '100%',
                            title: departmentDetailTitle+"__"+('显示当前职工【' + values.employeeCode + ' ' + values.name + ' 】的个人离职工资详细信息'),
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
                                        id: "displayEmployeeDimissionSalaryDetail",
                                        autoEl: {
                                            tag:'iframe',
                                            style:'height:100%;width:100%;border:none',
                                            src:'/appMis/employee/displayEmployeeDimissionSalaryDetail?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&employeeCode=' + values.employeeCode+ '&name=' + values.name+'&zbr='+values.zbr+'&shra='+values.shra+'&shrb='+values.shrb+'&shrc='+values.shrc+'&shrd='+values.shrd,
                                            //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                        }
                                    }]
                                    /*items: [{
                                        xtype: 'component',
                                        autoEl: {
                                            tag:'iframe',
                                            style:'height:100%;width:100%;border:none',
                                             //src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&procTaskContentVar=' + procTaskContentVar
                                            src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?taskId='+record.get('ID_')+'&procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_')+ '&procName=' + record.get('PROC_NAME_')+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
                                           // src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate
                                            //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                        }
                                    }]*/
                                }],
                            buttons:[
                                {
                                    text: '导出Excel文件',
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        Ext.MessageBox.buttonText.ok = "完成";
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + values.employeeCode+ values.name +"个人离职工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            align: "centre",
                                            buttons: Ext.MessageBox.OK
                                        })
                                    }
                                },
                                {
                                    text: '关闭',
                                    glyph: 'xf00d@FontAwesome',
                                    handler: function (button, e) {
                                        Ext.getCmp("employeeDimissionSalaryDetailWin").close()
                                    }
                                }],
                            listeners : {
                                'hide':function() {
                                    //win窗口关闭时，会执行本方法。 //删除职工详细信息生成Excel文件
                                    Ext.Ajax.request({
                                        url: '/appMis/employee/deleteExcelEmployeeDimissionSalaryDetail?employeeCode=' + values.employeeCode+'&name=' + values.name,
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
                }
            ]
        }
    ]
});
 
