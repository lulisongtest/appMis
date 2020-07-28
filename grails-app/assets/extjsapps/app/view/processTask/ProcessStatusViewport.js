
//查看申报事务的情况
var obj,record,view;
var taskId,procDefId,executionId,procInstId;
var gzDate="";//工资基金计划申报日期
var procTaskContentVar;
Ext.define('appMis.view.processTask.ProcessStatusViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processStatusviewport',
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
            defaults: {
                margins: '0 0 0 0',
                bodyPadding: 0,
                border: 0,
                padding: '4 0 4 0'
            },
            items: [
                {
                    xtype: 'datefield',
                    id: 'currentProcessDate',
                    fieldLabel: '当前日期',
                    format: 'Y年m月',
                    labelAlign: 'right',
                    width: 213,
                    value: currentGzjsDate,
                    labelWidth: 85,
                    listeners: {
                        change: function (combo) {
                            var panelStore = (Ext.getCmp('processStatusgrid')).getStore();
                            //panelStore.proxy.async=false;
                           // panelStore.proxy.timeout=999000000000;
                            panelStore.proxy.api.read = "/appMis/department/readDepartmentSbStatus?dep_tree_id="+currentTreeNode.substr(1)+'&currentProcessDate=' + (new Date(Ext.getCmp('currentProcessDate').getValue()).pattern("yyyy-MM-dd"))+'&procDefId=' + Ext.getCmp('processTaskProcDefIdStatus').getValue()+'&department=' +  Ext.getCmp('processTaskDepStatus').getValue()+'&status=' +  Ext.getCmp('processTaskStatusStatus').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'combobox',
                    id: 'processTaskProcDefIdStatus',
                    fieldLabel:'流程定义(名称)',
                    displayField: 'PROC_DEF_ID_',
                    valueField:'NAME_',
                    labelAlign: 'right',
                    value:'all',
                    width: 340,
                    labelWidth: 90,
                    store: 'ProcessDefStore',
                    listeners: {
                        change: function (combo) {
                            var panelStore = (Ext.getCmp('processStatusgrid')).getStore();
                           // panelStore.proxy.async=false;
                           // panelStore.proxy.timeout=999000000000;
                           // panelStore.setAutoSync(true);
                            panelStore.proxy.api.read = "/appMis/department/readDepartmentSbStatus?dep_tree_id="+currentTreeNode.substr(1)+'&currentProcessDate=' + (new Date(Ext.getCmp('currentProcessDate').getValue()).pattern("yyyy-MM-dd"))+'&procDefId=' + Ext.getCmp('processTaskProcDefIdStatus').getValue()+'&department=' +  Ext.getCmp('processTaskDepStatus').getValue()+'&status=' +  Ext.getCmp('processTaskStatusStatus').getValue();
                            panelStore.loadPage(1);

                           // alert("combo.displayField===="+Ext.getCmp('processTaskProcDefIdStatus').valueField.getValue())
                           // alert("combo.displayField===="+Ext.getCmp('processTaskProcDefIdStatus').getValue())
                           // Ext.getCmp('keywordprocessTask').setValue("");
                            //var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                           // panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' +  Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue();
                           // panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'processTaskDepStatus',
                    hidden:true,
                    fieldLabel: '申报单位',
                    labelAlign: 'right',
                    width: 220,
                    labelWidth: 60,
                    listeners: {
                        specialkey:function(field,e){
                           if(e.getKey()==Ext.EventObject.ENTER){
                               var panelStore = (Ext.getCmp('processStatusgrid')).getStore();
                               panelStore.proxy.api.read = "/appMis/department/readDepartmentSbStatus?dep_tree_id="+currentTreeNode.substr(1)+'&currentProcessDate=' + (new Date(Ext.getCmp('currentProcessDate').getValue()).pattern("yyyy-MM-dd"))+'&procDefId=' + Ext.getCmp('processTaskProcDefIdStatus').getValue()+'&department=' +  Ext.getCmp('processTaskDepStatus').getValue()+'&status=' +  Ext.getCmp('processTaskStatusStatus').getValue();
                               panelStore.loadPage(1);
                              //  var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                              //  panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue();
                              //  panelStore.loadPage(1);
                            }else{
                                return
                            }
                        }
                    }
                }, {
                    xtype: 'combobox',
                    id:'processTaskStatusStatus',
                    fieldLabel: '审核状态',
                    width: 220,
                    labelWidth: 60,
                    value:'全部',
                    displayField: 'name',
                    store: Ext.create('Ext.data.Store', {
                        fields: ['name'],
                        data: [{'name': '全部'},{'name': '等待审核中'},{'name': '审核不通过'},{'name': '全部审核通过'},{'name': '没有申报此业务'}]
                    }),
                    listeners: {
                        change: function (combo) {
                            var panelStore = (Ext.getCmp('processStatusgrid')).getStore();
                            //panelStore.proxy.async=false;
                           // panelStore.proxy.timeout=999000000000;
                           panelStore.proxy.api.read = "/appMis/department/readDepartmentSbStatus?dep_tree_id="+currentTreeNode.substr(1)+'&currentProcessDate=' + (new Date(Ext.getCmp('currentProcessDate').getValue()).pattern("yyyy-MM-dd"))+'&procDefId=' + Ext.getCmp('processTaskProcDefIdStatus').getValue()+'&department=' +  Ext.getCmp('processTaskDepStatus').getValue()+'&status=' +  Ext.getCmp('processTaskStatusStatus').getValue();
                            panelStore.loadPage(1);

                          //  if((combo.getValue()==null)||(combo.getValue()==""))combo.setValue('等待审核');
                          //  var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                          //  panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue();
                          //  panelStore.loadPage(1);
                        }
                    }

                }
            ]
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
                    xtype: 'processStatusgridviewport',
                    flex: 1,
                    id: 'processStatusgrid',
                    listeners: {
                        beforerender: function () {
                            //(Ext.getCmp('processTaskgrid')).setStore('ProcessTaskStore');
                            //(Ext.getCmp('bbarProcessTask')).setStore('ProcessTaskStore');//分页
                            var panelStore = (Ext.getCmp('processStatusgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/department/readDepartmentSbStatus?dep_tree_id="+currentTreeNode.substr(1)+'&currentProcessDate=' + (new Date(Ext.getCmp('currentProcessDate').getValue()).pattern("yyyy-MM-dd"))+'&procDefId=' + Ext.getCmp('processTaskProcDefIdStatus').getValue()+'&department=' +  Ext.getCmp('processTaskDepStatus').getValue()+'&status=' +  Ext.getCmp('processTaskStatusStatus').getValue();
                           // panelStore.pageSize = 500;
                            panelStore.loadPage(1);
                        },
                        cellclick: function (grid, td, cellIndex, record1, tr, rowIndex, e, eOpts) {       //alert("rowIndex===="+rowIndex); alert("cellIndex===="+cellIndex); alert("td===="+td); alert("tr===="+tr)// var fieldName = grid.getSelectionModel().getDataIndex(cellIndex); // 列名=fieldName；                          //var data = record.get('title');// alert("data=========="+data ); // alert("td.className===="+td.className) // alert("tr.className===="+tr.className)
/*
                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue();
                           // panelStore.proxy.api.read = '/appMis/processTask/readProcessTask?dep_tree_id=' + currentTreeNode.substr(1);
                            //panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX

                            //查看流程图且审批//
                            record=record1
                            //src:'/appMis/mainBusiness/displayHistoryDepMonthlySalary?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&procName=工资基金计划申报流程'+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
                            taskId=record.get('ID_');
                            procDefId=record.get('PROC_DEF_ID_')
                            executionId= record.get('EXECUTION_ID_')
                            procInstId=record.get('PROC_INST_ID_')

                            if (cellIndex == 3) {
                                view = Ext.widget('displayAndCompleteProcessTask');   //view =Ext.create('appMis.view.processTask.DisplayAndCompleteProcessTask',)//奇怪！！！随意修改以下两行或其中一行的数值，就可实现modal
                                //view.title="查看当前流程："+record.get('PROC_NAME_')+" 执行任务的情况";//这样不行，下一条可以。why???
                                view.setTitle("查看当前流程："+record.get('PROC_NAME_')+" 执行任务的情况")
                                //取流程图
                                Ext.Ajax.request({
                                    url: '/appMis/processTask/findProcessPic?procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_'),
                                    async: false,//同步请求数据, true异步
                                    success://回调函数
                                        function (resp, opts) {//成功后的回调方法
                                            if (resp.responseText == 'true') {
                                                Ext.getCmp("processDiagramImage").getEl().dom.src = '/appMis/static/processDiagram/' + record.get('EXECUTION_ID_') + '_watch.png?rand=' + Math.random();
                                                return
                                            } else {
                                                Ext.MessageBox.show({
                                                    title: "提示",
                                                    msg: "该流程已不存在！",
                                                    buttons: Ext.MessageBox.OK
                                                })
                                                setTimeout(function () {
                                                    Ext.Msg.hide();
                                                }, 2500);
                                                return;
                                            }
                                        }
                                });
                                //取任务的内容
                                Ext.Ajax.request({
                                    url: '/appMis/processTask/findProcessTaskContent?taskId='+record.get('ID_')+'&procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_')+ '&procName=' + record.get('PROC_NAME_'),
                                    async: false,//同步请求数据, true异步
                                    success://回调函数
                                        function (resp, opts) {//成功后的回调方法
                                            obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                            procTaskContentVar=obj.procTaskContentVar;//取任务的内容
                                            //取任务的内容
                                            switch(record.get('PROC_NAME_')){
                                                case '职务与岗位变动申报流程' :
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentGzllbdEmployeeViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入'appMis.view.processTask.ProcessTaskContentJjyjdEmployeeViewport',
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordGzllbdEmployee = Ext.create('appMis.model.NewEmployeeModel');//创建的新记录，不用事先引入！
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    procTaskContentVar.sbDate=new Date(procTaskContentVar.sbDate);//字符串日期要转化为日期型
                                                    recordGzllbdEmployee.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('gzllbdEmployeeform').loadRecord(recordGzllbdEmployee);
                                                    break;
                                                case '机关事业工资晋级与晋档申报流程' :
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentJjyjdEmployeeViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入'appMis.view.processTask.ProcessTaskContentJjyjdEmployeeViewport',
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordJjyjdEmployee = Ext.create('appMis.model.NewEmployeeModel');//创建的新记录，不用事先引入！
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    procTaskContentVar.sbDate=new Date(procTaskContentVar.sbDate);//字符串日期要转化为日期型
                                                    recordJjyjdEmployee.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('jjyjdEmployeeform').loadRecord(recordJjyjdEmployee);
                                                    break;
                                                case '石油企业工资晋级与晋档申报流程' :
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentSyqyJjyjdEmployeeViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入'appMis.view.processTask.ProcessTaskContentSyqyJjyjdEmployeeViewport',
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordSyqyJjyjEmployee = Ext.create('appMis.model.NewEmployeeModel');//创建的新记录，不用事先引入！
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    procTaskContentVar.sbDate=new Date(procTaskContentVar.sbDate);//字符串日期要转化为日期型
                                                    recordSyqyJjyjEmployee.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('syqyJjyjdEmployeeform').loadRecord(recordSyqyJjyjEmployee);
                                                    break;
                                                case '退休人员申报流程' :
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                              xtype:'processTaskContentRetireEmployeeViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入的
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordRetireEmployee = Ext.create('appMis.model.NewEmployeeModel');//创建的新记录，不用事先引入！
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    procTaskContentVar.sbDate=new Date(procTaskContentVar.sbDate);//字符串日期要转化为日期型
                                                    recordRetireEmployee.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('retireEmployeeform').loadRecord(recordRetireEmployee);
                                                    break;
                                                case '离职人员申报流程' :
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentDimissionEmployeeViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入的
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordDimissionEmployee = Ext.create('appMis.model.NewEmployeeModel');//创建的新记录，不用事先引入！
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    procTaskContentVar.sbDate=new Date(procTaskContentVar.sbDate);//字符串日期要转化为日期型
                                                    recordDimissionEmployee.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('dimissionEmployeeform').loadRecord(recordDimissionEmployee);
                                                    break;
                                                case '新增人员申报流程' :
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentNewEmployeeViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入的
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordNewEmployee = Ext.create('appMis.model.NewEmployeeModel');//创建的新记录，不用事先引入！
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    procTaskContentVar.sbDate=new Date(procTaskContentVar.sbDate);//字符串日期要转化为日期型
                                                    recordNewEmployee.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('newEmployeeform').loadRecord(recordNewEmployee);
                                                    break;
                                                case '人员单位变动申报流程' :
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentEmployeeRydwbdViewport'
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordEmployeeRydwbd = Ext.create('appMis.model.EmployeeRydwbdModel');//创建的新记录，不用事先引入！
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    name=procTaskContentVar.name
                                                    procTaskContentVar.bdsqDate=new Date(procTaskContentVar.bdsqDate);//字符串日期要转化为日期型
                                                    procTaskContentVar.bdzxDate=procTaskContentVar.bdzxDate?new Date(procTaskContentVar.bdzxDate):"";//字符串日期要转化为日期型
                                                    recordEmployeeRydwbd.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('employeeRydwbdform').loadRecord(recordEmployeeRydwbd);
                                                    break;
                                                case '工资基金计划申报流程' :
                                                    gzDate=procTaskContentVar.gzDate//工资基金计划申报日期
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentDepMonthlySalaryViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入'appMis.view.processTask.ProcessTaskContentDepMonthlySalaryViewport',
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordDepMonthlySalary = Ext.create('appMis.model.DepMonthlySalaryModel');//创建的新记录，不用事先引入！
                                                    procTaskContentVar.gzDate=new Date(procTaskContentVar.gzDate);//字符串日期要转化为日期型
                                                    recordDepMonthlySalary.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('depMonthlySalaryform').loadRecord(recordDepMonthlySalary);
                                                    break;
                                                case '年终一次性奖励申报流程' :
                                                    gzDate=procTaskContentVar.gzDate//工资基金计划申报日期
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentNzycxjlSalaryViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入'appMis.view.processTask.ProcessTaskContentNzycxjlSalaryViewport',
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordDepMonthlySalary = Ext.create('appMis.model.DepMonthlySalaryModel');//创建的新记录，不用事先引入！
                                                    procTaskContentVar.gzDate=new Date(procTaskContentVar.gzDate);//字符串日期要转化为日期型
                                                    recordDepMonthlySalary.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('depMonthlySalaryform').loadRecord(recordDepMonthlySalary);
                                                    break;
                                            }
                                            Ext.getCmp('shyj').setValue(obj.commentss);//审核意见
                                            Ext.getCmp('proccesstaskcontentDisplay').doLayout();
                                        }
                                });
                                return
                            }
*/
                        }
                    }
                }]
        }
    ]
});
 
