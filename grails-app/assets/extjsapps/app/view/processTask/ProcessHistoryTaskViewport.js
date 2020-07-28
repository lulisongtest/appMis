var obj,record,view
var gzDate=""//工资基金计划申报日期
var taskId,procDefId,executionId,procInstId
var procTaskContentVar//
Ext.define('appMis.view.processTask.ProcessHistoryTaskViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processHistoryTaskviewport',
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
                    text: '删除历史任务(流程实例)',
                    //hidden: ((Roleflag != "单位管理员") && (Roleflag != "管理员")),
                    hidden: (Roleflag != "管理员"),  //慎重操作此项。正常情况下应从【我的业务】中取消申报的流程！
                    icon: 'tupian/cancel.png',
                    action: 'deleteHistory'
                },
                {
                    xtype: 'combobox',
                    fieldLabel:'流程定义(名称)',
                    displayField: 'PROC_DEF_ID_',
                    id: 'processHistoryTaskProcDefId',
                    valueField:'NAME_',
                    labelAlign: 'right',
                    value:'all',
                    width: 370,
                    labelWidth: 90,
                    store: 'ProcessHistoryDefStore',
                    listeners: {
                        change: function (combo) {
                            var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processHistoryTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processHistoryTaskDep').getValue()+'&status=' +  Ext.getCmp('processHistoryTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentHistoryProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentHistoryProcessDateRange').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'processHistoryTaskDep',
                    fieldLabel: '申报单位',
                    labelAlign: 'right',
                    width: 250,
                    labelWidth:65,
                    listeners: {
                        specialkey:function(field,e){
                            if(e.getKey()==Ext.EventObject.ENTER){
                                var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                                panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processHistoryTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processHistoryTaskDep').getValue()+'&status=' +  Ext.getCmp('processHistoryTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentHistoryProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentHistoryProcessDateRange').getValue();
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
                    id:'processHistoryTaskStatus',
                    fieldLabel: '审核状态',
                    width: 210,
                    labelWidth: 65,
                    value:'完成审核',
                    displayField: 'name',
                    store: Ext.create('Ext.data.Store', {
                        fields: ['name'],
                        data: [{'name': '全部'},{'name': '完成审核'}, {'name': '审核不通过'}]
                    }),
                    listeners: {

                        change: function (combo) {
                            if((combo.getValue()==null)||(combo.getValue()==""))combo.setValue('等待审核');
                            var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processHistoryTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processHistoryTaskDep').getValue()+'&status=' +  Ext.getCmp('processHistoryTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentHistoryProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentHistoryProcessDateRange').getValue();
                            panelStore.loadPage(1);
                        }
                    }

                } ,
                {
                    xtype: 'datefield',
                    id: 'currentHistoryProcessDate1',
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
                            var newvalue1 = (new Date(Ext.getCmp('currentHistoryProcessDate1').getValue()).pattern("yyyy-MM-dd"));
                            currentProcessDates=new Date(newvalue1)
                            var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processHistoryTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processHistoryTaskDep').getValue()+'&status=' +  Ext.getCmp('processHistoryTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentHistoryProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentHistoryProcessDateRange').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'combobox',
                    id:'currentHistoryProcessDateRange',
                    fieldLabel: '事务审核完成日期范围',
                    width: 310,
                    labelWidth: 150,
                    value:'当前两个月',
                    displayField: 'name',
                    store: Ext.create('Ext.data.Store', {
                        fields: ['name'],
                        data: [{'name': '当前月'},{'name': '当前两个月'},{'name': '当前三个月'},{'name': '当前六个月'},{'name': '当前一年'}]
                    }),
                    listeners: {
                        change: function (combo) {
                            if((combo.getValue()==null)||(combo.getValue()==""))combo.setValue('当前月');
                            var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processHistoryTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processHistoryTaskDep').getValue()+'&status=' +  Ext.getCmp('processHistoryTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentHistoryProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentHistoryProcessDateRange').getValue();
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
                    xtype: 'processHistoryTaskgridviewport',
                    flex: 1,
                    id: 'processHistoryTaskgrid',
                    listeners: {
                        beforerender: function () {
                            //(Ext.getCmp('processHistoryTaskgrid')).setStore('ProcessHistoryTaskStore');
                            //(Ext.getCmp('bbarProcessHistoryTask')).setStore('ProcessHistoryTaskStore');//分页
                            var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                           // panelStore.proxy.api.read = '/appMis/processTask/readProcessHistoryTask?dep_tree_id=' + currentTreeNode.substr(1);
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' +  Ext.getCmp('processHistoryTaskProcDefId').getValue();

                            panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX
                        },
                        cellclick: function (grid, td, cellIndex, record1, tr, rowIndex, e, eOpts) {       //alert("rowIndex===="+rowIndex); alert("cellIndex===="+cellIndex); alert("td===="+td); alert("tr===="+tr)// var fieldName = grid.getSelectionModel().getDataIndex(cellIndex); // 列名=fieldName；                          //var data = record.get('title');// alert("data=========="+data ); // alert("td.className===="+td.className) // alert("tr.className===="+tr.className)
                            //查看流程图且审批//
                            record=record1//获取点击按钮所在的记录！
                            taskId=record.get('ID_');
                            procDefId=record.get('PROC_DEF_ID_')
                            executionId= record.get('EXECUTION_ID_')
                            procInstId=record.get('PROC_INST_ID_')
                            if (cellIndex == 3) {
                                /*if((record1.get("DELETE_REASON_"))&&(record1.get("DELETE_REASON_")!="")){
                                    Ext.MessageBox.show({
                                        title: "提示",
                                        msg: "该事务中途已经中止！",
                                        buttons: Ext.MessageBox.OK
                                    })
                                    setTimeout(function () {
                                        Ext.Msg.hide();
                                    }, 2500);
                                    return
                                }*/
                                 //查看历史任务各项信息
                                 Ext.create('Ext.window.Window', {
                                    id: 'displayAndCompleteProcessHistoryTask',
                                    title:"查看当前流程："+record.get('PROC_NAME_')+" 执行任务的情况",
                                    width: "100%",
                                    height: "100%",
                                    autoShow: true,
                                    modal: true,//创建模态窗口
                                    layout: {
                                        type: 'vbox',
                                        pack: 'start',
                                        align: 'stretch'
                                    },
                                    items: [{
                                        xtype: 'panel',
                                        height: 670,
                                        id: 'proccesshistorytaskcontentDisplay',
                                        autoScroll: true,
                                        defaults: {
                                            margins: '10 0 0 0',
                                            bodyPadding: 0,
                                            border: 1,
                                            padding: '10 5 0 5'
                                        },
                                        items: [
                                            {
                                                xtype: 'box',
                                                height: 170, //流程图图片高度
                                                id: 'processHistoryDiagramImage',
                                                columnWidth: 1,
                                                width: '100%',
                                                // width: 1200, //图片宽度
                                                autoShow: true,
                                                autoEl: {
                                                    tag: 'img',    //指定为img标签
                                                    style: "",//不加这行，图像不显示！不知为什么？
                                                    src: '/appMis/processDiagram/watch.png?rand=' + Math.random()   //指定url路径
                                                },
                                                OnImageMouseOver: function () {
                                                    var me = this;
                                                    // alert("====x1======"+x1+"====x2======"+x2)
                                                    var wev = window.event;
                                                    var x = wev.offsetX;
                                                    var y = wev.offsetY;
                                                    var el = me.getEl().dom;
                                                    el.setAttribute("title","")
                                                    f=(f=="")?" ":""
                                                    if((x>=x1)&&(x<=x1+w1)&&(y>=y1)&&(y<=y1+h1)){
                                                        /* Ext.create('Ext.tip.ToolTip', {
                                                             target: 'processDiagramImage',
                                                             trackMouse: true,
                                                             html: "单位审核人:"+comment[0]
                                                         });*/

                                                        //el.setAttribute("title", "单位审核人"+comment[0]+"===x=" + x +",x1=" + x1 + ",y=" + y + ",y1=" + y1+ ",w1=" + w1 + ",h1=" + h1);//<img src="..." width="100" height="62" title="提示信息">
                                                        el.setAttribute("title", "单位审核人:"+comment[0]+f)
                                                    }
                                                    if((x>=x2)&&(x<=x2+w2)&&(y>=y2)&&(y<=y2+h2)){
                                                        // el.setAttribute("title", "主管单位审核人"+comment[1]+"===x=" + x +",x2=" + x2 + ",y=" + y + ",y2=" + y2+ ",w2=" + w2 + ",h2=" + h2);//<img src="..." width="100" height="62" title="提示信息">
                                                        el.setAttribute("title", "主管单位审核人:"+comment[1]+f)
                                                    }
                                                    if((x>=x3)&&(x<=x3+w3)&&(y>=y3)&&(y<=y3+h3)){
                                                        //el.setAttribute("title", "区人社局审核人"+comment[2]+"===x=" + x +",x3=" + x3 + ",y=" + y + ",y3=" + y3+ ",w3=" + w3 + ",h3=" + h3);//<img src="..." width="100" height="62" title="提示信息">
                                                        el.setAttribute("title", "区人社局审核人:"+comment[2]+f)
                                                    }
                                                    if((x>=x4)&&(x<=x4+w4)&&(y>=y4)&&(y<=y4+h4)){
                                                        //el.setAttribute("title", "市人社局审核人"+comment[3]+"===x=" + x +",x4=" + x4 + ",y=" + y + ",y4=" + y4+ ",w4=" + w4 + ",h4=" + h4);//<img src="..." width="100" height="62" title="提示信息">
                                                        el.setAttribute("title", "市人社局审核人:"+comment[3]+f)
                                                    }
                                                    if((x>=x5)&&(x<=x5+w5)&&(y>=y5)&&(y<=y5+h5)){
                                                        // el.setAttribute("title", "调入区人社局审核人"+comment[4]+"===x=" + x +",x5=" + x5 + ",y=" + y + ",y5=" + y5+ ",w5=" + w5 + ",h5=" + h5);//<img src="..." width="100" height="62" title="提示信息">
                                                        el.setAttribute("title", "调入区人社局审核人:"+comment[4]+f)
                                                    }
                                                    if((x>=x6)&&(x<=x6+w6)&&(y>=y6)&&(y<=y6+h6)){
                                                        // el.setAttribute("title", "调入主管单位审核人"+comment[5]+"===x=" + x +",x6=" + x6 + ",y=" + y + ",y6=" + y6+ ",w6=" + w6 + ",h6=" + h6);//<img src="..." width="100" height="62" title="提示信息">
                                                        el.setAttribute("title", "调入主管单位审核人:"+comment[5]+f)
                                                    }
                                                    if((x>=x7)&&(x<=x7+w7)&&(y>=y7)&&(y<=y7+h7)){
                                                        // el.setAttribute("title", "调入单位审核人"+comment[6]+"===x=" + x +",x7=" + x7 + ",y=" + y + ",y7=" + y7+ ",w7=" + w7 + ",h7=" + h7);//<img src="..." width="100" height="62" title="提示信息">
                                                        el.setAttribute("title", "调入单位审核人:"+comment[6]+f)
                                                    }
                                                },
                                                listeners: {
                                                    boxready: function () {
                                                        var me = this;
                                                        Ext.getCmp('processHistoryDiagramImage').el.on("mousemove", me.OnImageMouseOver, me)//有作用
                                                        /*// me.el.on("mouseover", me.OnImageMouseOver, me);//鼠标掠过事件
                                                        //me.el.on("mousemove", me.OnImageMouseOver11, me);//鼠标移动事件，不能用onmousemove
                                                        // me.getEl().on("mousemove", me.OnImageMouseOver11, me);//getEl() = el 就是一个组件的顶级的元素....可以理解为最外层的那个div...
                                                        // me.el.addListener("mousemove", me.OnImageMouseOver11, me)//等同on(String eventName, Function fn, [Object scope], [Object options])
                                                        // Ext.get('processDiagramImage').addListener("mousemove", me.OnImageMouseOver, me)
                                                        // Ext.get('processDiagramImage').on("mousemove", me.OnImageMouseOver, me)
                                                        // Ext.getCmp('processDiagramImage').on("mousemove", me.OnImageMouseOver, me)//没作用
                                                        //给Ext.get()上添加事件会最终把事件绑定到dom元素上（element DOM）（最终是调用addEventListener or attachEvent or on)
                                                        //但是Ext.getCmp()添加事件只是把一个事件绑定到EXTJS的组件上而不是DOM元素上;（component）*/
                                                    }
                                                }
                                            },
                                            {
                                                xtype: 'panel',
                                                height: 100,
                                                width: '100%',
                                                layout: 'column',
                                                items: [
                                                    {
                                                        xtype: "textarea",
                                                        name:'shyj',
                                                        id:'shyjHistory',
                                                        columnWidth: 0.93,
                                                        fieldLabel: "审核意见",
                                                        labelSeparator: "：",
                                                        labelWidth: 180,
                                                        labelAlign: 'right'
                                                    }
                                                ]
                                            }
                                        ]
                                    }],
                                     listeners :{
                                         'hide':function() {
                                             var executionId=(Ext.getCmp("processHistoryDiagramImage").getEl().dom.src).toString()//http://localhost:8888/appMis/static/processDiagram/647513_watch.png?rand=0.1306391792218201
                                             executionId=(executionId.split("processDiagram/")[1]).split("_watch.png")[0]
                                             //alert("win窗口关闭时，会执行本方法!!!!==="+s)
                                             //删除生成的流程图文件
                                             Ext.Ajax.request({
                                                 url: '/appMis/processTask/deleteProcessPicByExecutionId?executionId='+executionId,
                                                 //url: '/appMis/mainBusiness/deleteExcelDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
                                                 ansync: false,
                                                 method: 'POST',
                                                 success://回调函数
                                                     function (resp, opts) { }
                                             })
                                         }
                                     }
                                })
                                //取流程图
                                /*Ext.Ajax.request({
                                    url: '/appMis/processTask/findProcessPic?procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_'),
                                    async: false,//同步请求数据, true异步
                                    success://回调函数
                                        function (resp, opts) {//成功后的回调方法
                                            if (resp.responseText == 'true') {
                                                Ext.getCmp("processHistoryDiagramImage").getEl().dom.src = '/appMis/static/processDiagram/' + record.get('EXECUTION_ID_') + '_watch.png?rand=' + Math.random();
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
                                });*/
                                Ext.Ajax.request({
                                    url: '/appMis/processTask/findProcessPic?procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_'),
                                    async: false,//同步请求数据, true异步
                                    success://回调函数
                                        function (resp, opts) {//成功后的回调方法
                                            obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                            if (obj.success) {
                                                Ext.getCmp("processHistoryDiagramImage").getEl().dom.src = '/appMis/static/processDiagram/' + record.get('EXECUTION_ID_') + '_watch.png?rand=' + Math.random();
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

                                //取任务的内容  //taskId=record.get('ID_')
                                Ext.Ajax.request({
                                    url: '/appMis/processTask/findProcessHistoryTaskContent?taskId='+record.get('ID_')+'&procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_')+ '&procName=' + record.get('PROC_NAME_'),
                                    async: false,//同步请求数据, true异步
                                    success://回调函数
                                        function (resp, opts) {//成功后的回调方法
                                            obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                             procTaskContentVar=obj.procTaskContentVar;//取任务的内容
                                            //alert("history___取任务的内容 procTaskContentVar.gzDate==="+ procTaskContentVar.gzDate)
                                            //alert("procTaskContentVar====="+procTaskContentVar.zbr)
                                           // alert("record.get('PROC_NAME_')===="+record.get('PROC_NAME_'))
                                            //取任务的内容
                                            switch(record.get('PROC_NAME_')) {
                                                case '职务与岗位变动申报流程' :
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
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
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
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
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
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
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentRetireEmployeeViewport'//在'appMis.view.processAdmin.ProcessAdminViewport',文件中引入的
                                                        }
                                                    );//增加显示任务的内容
                                                    var recordRetireEmployee = Ext.create('appMis.model.NewEmployeeModel');//创建的新记录，不用事先引入！
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    procTaskContentVar.sbDate=new Date(procTaskContentVar.sbDate);//字符串日期要转化为日期型
                                                    recordRetireEmployee.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('retireEmployeeform').loadRecord(recordRetireEmployee);
                                                    //不可以重新导入附件
                                                    Ext.getCmp("reImportRetireSbExcel0").setHidden(true)
                                                    Ext.getCmp("reImportRetireSbExcel").setHidden(true)
                                                    break;
                                                case '离职人员申报流程' :
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
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
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
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
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentEmployeeRydwbdViewport'
                                                        });//增加显示任务的内容
                                                    var recordEmployeeRydwbd = Ext.create('appMis.model.EmployeeRydwbdModel');//创建的新记录
                                                    semployeeCode=procTaskContentVar.employeeCode
                                                    name=procTaskContentVar.name
                                                    procTaskContentVar.bdsqDate=new Date(procTaskContentVar.bdsqDate);//字符串日期要转化为日期型
                                                    procTaskContentVar.bdzxDate=procTaskContentVar.bdzxDate?new Date(procTaskContentVar.bdzxDate):"";//字符串日期要转化为日期型
                                                    recordEmployeeRydwbd.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('employeeRydwbdform').loadRecord(recordEmployeeRydwbd);
                                                    break;
                                                case '工资基金计划申报流程' :
                                                    gzDate=procTaskContentVar.gzDate//工资基金计划申报日期
                                                    var recordDepMonthlySalary = Ext.create('appMis.model.DepMonthlySalaryModel');//创建的新记录
                                                    procTaskContentVar.gzDate=new Date(procTaskContentVar.gzDate);//字符串日期要转化为日期型
                                                    recordDepMonthlySalary.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
                                                           {
                                                               xtype:'processTaskContentDepMonthlySalaryViewport'
                                                           }
                                                    );//增加显示任务的内容
                                                    Ext.getCmp('depMonthlySalaryform').loadRecord(recordDepMonthlySalary);
                                                    break;
                                                case '养老保险缴费基数申报流程' :
                                                    gzDate=procTaskContentVar.gzDate//工资基金计划申报日期
                                                    var recordDepMonthlySalary = Ext.create('appMis.model.DepMonthlySalaryModel');//创建的新记录
                                                    procTaskContentVar.gzDate=new Date(procTaskContentVar.gzDate);//字符串日期要转化为日期型
                                                    recordDepMonthlySalary.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentYlbxjfjsViewport'
                                                        }
                                                    );//增加显示任务的内容
                                                    Ext.getCmp('ylbxjfjsform').loadRecord(recordDepMonthlySalary);
                                                    break;
                                                case '年终一次性奖励申报流程' :
                                                    gzDate=procTaskContentVar.gzDate//工资基金计划申报日期
                                                    var recordDepMonthlySalary = Ext.create('appMis.model.DepMonthlySalaryModel');//创建的新记录
                                                    procTaskContentVar.gzDate=new Date(procTaskContentVar.gzDate);//字符串日期要转化为日期型
                                                    recordDepMonthlySalary.set(procTaskContentVar);//必须是Record格式的数据
                                                    Ext.getCmp('proccesshistorytaskcontentDisplay').add(
                                                        {
                                                            xtype:'processTaskContentNzycxjlSalaryViewport'
                                                        }
                                                    );//增加显示任务的内容
                                                    Ext.getCmp('depMonthlySalaryform').loadRecord(recordDepMonthlySalary);
                                                    break;
                                            }
                                            Ext.getCmp('shyjHistory').setValue(obj.commentss);//审核意见
                                            Ext.getCmp('proccesshistorytaskcontentDisplay').doLayout();
                                        }
                                });
                                return
                            }

                        }
                    }
                }]
        }]
});
 
