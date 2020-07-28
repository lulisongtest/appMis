

var newvalue1
var newvalue2
Ext.define('appMis.view.processTask.ProcessTaskContentDepMonthlySalaryViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processTaskContentDepMonthlySalaryViewport',
    defaults: {
        autoScroll: true,
        bodyPadding: 0,
        border: 0,
        padding: 0
    },
    height: 300,
    items: [
        {
            xtype: 'form',
            id: 'depMonthlySalaryform',
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
                    columnWidth:.25,
                    format:'Y年m月d日',
                    name : 'gzDate',
                    fieldLabel:'工资日期',
                    readOnly:true,
                    labelWidth:110,
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
                },
                {
                    xtype: "button",
                    columnWidth: 0.15,
                    text: "查看本月工资基金申报表",
                    handler : function(){
                        //从form中获取数据
                        var values=Ext.getCmp('depMonthlySalaryform').getValues()
                        var departmentDetail=values.department//改为局部变量
                        values.gzDate = (values.gzDate).replace(/(年)|(月)/g, "-");
                        values.gzDate = (values.gzDate).replace(/(日)/g, "");
                        var currentDepMonthlySalaryDate=new Date(values.gzDate).pattern("yyyy-MM-dd")//改为局部变量
                        var currentTreeNode="p"+values.treeId//改为局部变量
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
                        //3-4、打开窗口显示申报【月工资基金计划表】的详细内容
                        Ext.create('Ext.window.Window',{
                            id:'displayHistoryDepMonthlySalary',
                            width: "100%",
                            height: "100%",
                            autoShow: true,
                            title:departmentDetailTitle+"__"+(new Date(values.gzDate).pattern("yyyy年MM月"))+" 工资基金计划申报",
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
                                        autoEl: {
                                            tag:'iframe',
                                            style:'height:100%;width:100%;border:none',
                                            src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&procName=工资基金计划申报流程'+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
                                            // src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate
                                            //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                            //src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&procTaskContentVar=' + procTaskContentVar
                                        }
                                    }]
                                }],
                            buttons:[
                                {
                                    text: '导出Excel文件',
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        var year=new Date(currentDepMonthlySalaryDate).getFullYear()
                                        var month=new Date(currentDepMonthlySalaryDate).getMonth()+1
                                        Ext.MessageBox.buttonText.ok = "完成";
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + taskId+departmentDetail +year+"年"+month+"月工资基金月报.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            align: "centre",
                                            buttons: Ext.MessageBox.OK
                                        })
                                    }
                                },
                                {
                                    text: '关闭',
                                    glyph: 'xf00d@FontAwesome',
                                    handler: function (button, e) {
                                        Ext.getCmp("displayHistoryDepMonthlySalary").close()
                                    }
                                }],
                            listeners : {
                                'hide':function() {
                                    //win窗口关闭时，会执行本方法
                                    Ext.Ajax.request({
                                        url: '/appMis/mainBusiness/deleteExcelDepMonthlySalary?taskId='+taskId+'&dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
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
                    xtype: 'label',
                    columnWidth:.005
                },
                {
                    xtype: "button",
                    columnWidth: 0.2,
                    text: "查看本月之前一个月的工资基金申报表",
                    handler : function(){
                        //从form中获取数据
                        var values=Ext.getCmp('depMonthlySalaryform').getValues()
                        var departmentDetail=values.department//改为局部变量
                        values.gzDate = (values.gzDate).replace(/(年)|(月)/g, "-");
                        values.gzDate = (values.gzDate).replace(/(日)/g, "");
                        //var currentDepMonthlySalaryDate=new Date(values.gzDate).pattern("yyyy-MM-dd")//改为局部变量
                       // alert("new currentDepMonthlySalaryDate"+currentDepMonthlySalaryDate)
                        var currentDepMonthlySalaryDate=new Date(values.gzDate)
                         var xyear=currentDepMonthlySalaryDate.getFullYear() ;
                         var xmonth = currentDepMonthlySalaryDate.getMonth();
                         if(xmonth==0){
                             xyear=xyear-1
                             xmonth=12
                         }
                        var xday = currentDepMonthlySalaryDate.getDate();
                        currentDepMonthlySalaryDate=new Date(""+xyear+"-"+xmonth+"-"+xday).pattern("yyyy-MM-dd")
                       // alert("new currentDepMonthlySalaryDate"+currentDepMonthlySalaryDate)
                        var currentTreeNode="p"+values.treeId//改为局部变量
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
                        //取上个月的【月工资基金计划表】
                        var taskId, procInstId,executionId, procDefId
                        Ext.Ajax.request({
                            url:'/appMis/mainBusiness/displayHistoryDepMonthlySalaryPriorMonth?dep_tree_id=' + currentTreeNode.substr(1)+ '&procName=工资基金计划申报流程'+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
                            async: false,//同步
                            success://回调函数
                                function (resp, opts) {//成功后的回调方法
                                    var obj1 = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                    taskId=obj1.taskId
                                    procInstId=obj1.procInstId
                                    executionId=obj1.executionId
                                    procDefId=obj1.procDefId
                                }
                        })
                        //alert("procInstId===="+procInstId)
                        if((procInstId=="")||(!procInstId)){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: "无该事务或已经被撤消中止，无法查看其它信息！",
                                align: "centre",
                                buttons: Ext.MessageBox.OK
                            })
                            return
                        }

                        //3-4、打开窗口显示申报【月工资基金计划表】的详细内容
                        Ext.create('Ext.window.Window',{
                            id:'displayHistoryDepMonthlySalary',
                            width: "100%",
                            height: "100%",
                            autoShow: true,
                            title:departmentDetailTitle+"__"+""+xyear+"年"+xmonth+"月"+" 工资基金计划申报",
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
                                        autoEl: {
                                            tag:'iframe',
                                            style:'height:100%;width:100%;border:none',
                                            src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&procName=工资基金计划申报流程'+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate
                                            // src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate
                                            //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                            //src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&procTaskContentVar=' + procTaskContentVar
                                        }
                                    }]
                                }],
                            buttons:[
                                {
                                    text: '导出Excel文件',
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        var year=new Date(currentDepMonthlySalaryDate).getFullYear()
                                        var month=new Date(currentDepMonthlySalaryDate).getMonth()+1
                                        Ext.MessageBox.buttonText.ok = "完成";
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + taskId+departmentDetail +year+"年"+month+"月工资基金月报.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            align: "centre",
                                            buttons: Ext.MessageBox.OK
                                        })
                                    }
                                },
                                {
                                    text: '关闭',
                                    glyph: 'xf00d@FontAwesome',
                                    handler: function (button, e) {
                                        Ext.getCmp("displayHistoryDepMonthlySalary").close()
                                    }
                                }],
                            listeners : {
                                'hide':function() {
                                    //win窗口关闭时，会执行本方法
                                    Ext.Ajax.request({
                                        url: '/appMis/mainBusiness/deleteExcelDepMonthlySalary?taskId='+taskId+'&dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
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
 
