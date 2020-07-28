var obj,record,view
var taskId,procDefId,executionId,procInstId
var gzDate=""//工资基金计划申报日期
var procTaskContentVar
var x1=0;y1=0;h1=0;w1=0;x2=0;y2=0;h2=0;w2=0;x3=0;y3=0;h3=0;w3=0;x4=0;y4=0;h4=0;w4=0;x5=0;y5=0;h5=0;w5=0;x6=0;y6=0;h6=0;w6=0;x7=0;y7=0;h7=0;w7=0
var comment=new Array()
Ext.define('appMis.view.processTask.ProcessTaskViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processTaskviewport',
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
                    text: '删除任务(流程实例)',
                    //hidden: ((Roleflag != "单位管理员") && (Roleflag != "管理员")),
                    hidden: (Roleflag != "管理员"),  //慎重操作此项。正常情况下应从【我的业务】中取消申报的流程！
                    icon: 'tupian/cancel.png',
                    action: 'delete'
                },
                {
                    xtype: 'combobox',
                    id: 'processTaskProcDefId',
                    fieldLabel:'流程定义(名称)',
                    displayField: 'PROC_DEF_ID_',
                    valueField:'NAME_',
                    labelAlign: 'right',
                    value:'all',
                    width: 360,
                    labelWidth: 90,
                    store: 'ProcessDefStore',
                    listeners: {
                        change: function (combo) {
                           // Ext.getCmp('keywordprocessTask').setValue("");
                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'processTaskDep',
                    fieldLabel: '申报单位',
                    labelAlign: 'right',
                    width: 250,
                    labelWidth:65,
                    listeners: {
                        specialkey:function(field,e){
                           if(e.getKey()==Ext.EventObject.ENTER){
                                var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                               panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();
                               panelStore.loadPage(1);
                            }else{
                                return
                            }
                        }
                    }
                }
            ]
        },
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
                    xtype: 'combobox',
                    id:'processTaskStatus',
                    fieldLabel: '审核状态',
                    width: 210,
                    labelWidth: 65,
                    value:'等待审核',
                    displayField: 'name',
                    store: Ext.create('Ext.data.Store', {
                        fields: ['name'],
                        data: [{'name': '全部'},{'name': '等待审核'}, {'name': '审核不通过'}]
                    }),
                    listeners: {

                        change: function (combo) {
                            if((combo.getValue()==null)||(combo.getValue()==""))combo.setValue('等待审核');
                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();
                            panelStore.loadPage(1);
                        }
                    }

                } ,
                {
                    xtype: 'datefield',
                    id: 'currentProcessDate1',
                    fieldLabel: '当前日期',
                    // renderer : Ext.util.Format.dateRenderer('Y年m月d日'),
                    format: 'Y年m月',
                    labelAlign: 'right',
                    width: 220,
                    value: currentProcessDates,
                    labelWidth: 65,
                    listeners: {
                        beforerender: function () {
                            Ext.getCmp('currentProcessDate1').setValue(currentProcessDates)
                        },
                        change: function (combo) {
                            var newvalue1 = (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"));
                            currentProcessDates=new Date(newvalue1)
                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+  (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'combobox',
                    id:'currentProcessDateRange',
                    fieldLabel: '事务申报日期范围',
                    width: 280,
                    labelWidth: 120,
                    value:'当前两个月',
                    displayField: 'name',
                    store: Ext.create('Ext.data.Store', {
                        fields: ['name'],
                        data: [{'name': '当前月'},{'name': '当前两个月'},{'name': '当前三个月'},{'name': '当前六个月'},{'name': '当前一年'}]
                    }),
                    listeners: {
                        change: function (combo) {
                            if((combo.getValue()==null)||(combo.getValue()==""))combo.setValue('当前月');
                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+  (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();
                            panelStore.loadPage(1);
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
                    xtype: 'processTaskgridviewport',
                    flex: 1,
                    id: 'processTaskgrid',
                    listeners: {
                        beforerender: function () {
                            //(Ext.getCmp('processTaskgrid')).setStore('ProcessTaskStore');
                            //(Ext.getCmp('bbarProcessTask')).setStore('ProcessTaskStore');//分页

                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();
                            panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX
                        },
                        cellclick: function (grid, td, cellIndex, record1, tr, rowIndex, e, eOpts) {       //alert("rowIndex===="+rowIndex); alert("cellIndex===="+cellIndex); alert("td===="+td); alert("tr===="+tr)// var fieldName = grid.getSelectionModel().getDataIndex(cellIndex); // 列名=fieldName；                          //var data = record.get('title');// alert("data=========="+data ); // alert("td.className===="+td.className) // alert("tr.className===="+tr.className)
                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();
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
                                            obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                            if (obj.success) {
                                                Ext.getCmp("processDiagramImage").getEl().dom.src = '/appMis/static/processDiagram/' + record.get('EXECUTION_ID_') + '_watch.png?rand=' + Math.random();
                                              //  alert("====obj.success======"+obj.success)
                                              //  alert("====obj.nodes======"+(obj.nodes)[0].node+"====obj.nodes======"+(obj.nodes)[1].node+"====obj.nodes======"+(obj.nodes)[2].node+"====obj.nodes======"+(obj.nodes)[3].node+"====obj.nodes======"+(obj.nodes)[4].node)
                                               // alert("====obj.nodes======"+(obj.nodes)[0].x+"====obj.nodes======"+(obj.nodes)[1].x+"====obj.nodes======"+(obj.nodes)[2].x+"====obj.nodes======"+(obj.nodes)[3].x+"====obj.nodes======"+(obj.nodes)[4].x)
                                                if((obj.nodes)[1]){
                                                    x1=(obj.nodes)[1].x;
                                                    y1=(obj.nodes)[1].y;
                                                    h1=(obj.nodes)[1].h;
                                                    w1=(obj.nodes)[1].w;
                                                }
                                                if((obj.nodes)[2]){
                                                    x2=(obj.nodes)[2].x;
                                                    y2=(obj.nodes)[2].y;
                                                    h2=(obj.nodes)[2].h;
                                                    w2=(obj.nodes)[2].w;
                                                }
                                                if((obj.nodes)[3]){
                                                    x3=(obj.nodes)[3].x;
                                                    y3=(obj.nodes)[3].y;
                                                    h3=(obj.nodes)[3].h;
                                                    w3=(obj.nodes)[3].w;
                                                }
                                                if((obj.nodes)[4]){
                                                    x4=(obj.nodes)[4].x;
                                                    y4=(obj.nodes)[4].y;
                                                    h4=(obj.nodes)[4].h;
                                                    w4=(obj.nodes)[4].w;
                                                }
                                                if((obj.nodes)[5]){
                                                    x5=(obj.nodes)[5].x;
                                                    y5=(obj.nodes)[5].y;
                                                    h5=(obj.nodes)[5].h;
                                                    w5=(obj.nodes)[5].w;
                                                }
                                                if((obj.nodes)[6]) {
                                                    x6 = (obj.nodes)[6].x;
                                                    y6 = (obj.nodes)[6].y;
                                                    h6 = (obj.nodes)[6].h;
                                                    w6 = (obj.nodes)[6].w;
                                                }
                                                if((obj.nodes)[7]){
                                                    x7=(obj.nodes)[7].x;
                                                    y7=(obj.nodes)[7].y;
                                                    h7=(obj.nodes)[7].h;
                                                    w7=(obj.nodes)[7].w;
                                                }
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
                                //取已经审核意见的内容
                                Ext.Ajax.request({
                                    url: '/appMis/processTask/findProcessHistoryTaskComments?taskId=' + record.get('ID_') + '&procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_') + '&procName=' + record.get('PROC_NAME_'),
                                    async: false,//同步请求数据, true异步
                                    success://回调函数
                                        function (resp, opts) {//成功后的回调方法
                                            obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                            for(var i=0;i<(obj.commentss).length;i++){
                                                comment[i]=obj.commentss[i]
                                               // alert("========"+obj.commentss[i])
                                            }
                                        }
                                });






                              // Ext.getCmp('processDiagramImage').html(' <area class="hotspot" shape="rect" coords="0,0,100,190" href="#">')
                               /* Ext.create('Ext.tip.ToolTip', {
                                    target: 'processDiagramImage',
                                    trackMouse: true,
                                    html: '审批流程图'
                                });*/

                                //取任务的内容，  //取审核意见的内容
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
                                                    //不可以重新导入附件
                                                    Ext.getCmp("reImportGzllSbExcel0").setHidden(true)
                                                    Ext.getCmp("reImportGzllSbExcel").setHidden(true)
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
                                                    //不可以重新导入附件
                                                    Ext.getCmp("reImportRetireSbExcel0").setHidden(true)
                                                    Ext.getCmp("reImportRetireSbExcel").setHidden(true)
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
                                                case '养老保险缴费基数申报流程' :
                                                    gzDate=procTaskContentVar.gzDate//工资基金计划申报日期
                                                    Ext.getCmp('proccesstaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentYlbxjfjsViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入'appMis.view.processTask.ProcessTaskContentYlbxjfjsViewport',
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordDepMonthlySalary = Ext.create('appMis.model.DepMonthlySalaryModel');//创建的新记录，不用事先引入！
                                                    procTaskContentVar.gzDate=new Date(procTaskContentVar.gzDate);//字符串日期要转化为日期型
                                                    recordDepMonthlySalary.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('ylbxjfjsform').loadRecord(recordDepMonthlySalary);
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

                        }
                    }
                }]
        }]
});
 
