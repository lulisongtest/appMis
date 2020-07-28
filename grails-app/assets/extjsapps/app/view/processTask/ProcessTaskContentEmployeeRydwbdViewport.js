
Ext.define('appMis.view.processTask.ProcessTaskContentEmployeeRydwbdViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processTaskContentEmployeeRydwbdViewport',
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
            id: 'employeeRydwbdform',
            //width: 1050,
            width: '100%',
            layout: 'column',
            defaults: {
                margins: '0 0 0 0',
                padding: 1
            },
            items: [
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    readOnly:true,
                    name: 'employeeCode',
                    fieldLabel: '职工编号',
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'name',
                    readOnly:true,
                    fieldLabel: '姓名',
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'datefield',
                    columnWidth: .25,
                    format: 'Y年m月d日',
                    name: 'bdsqDate',
                    readOnly:true,
                    fieldLabel: '变动申请日期',
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .2,
                    name: 'employeeDlwh',
                    fieldLabel: '调令文号',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px',
                    listeners: {
                        /*focus: function (obj) {
                            employeeDlwh = obj.getValue()
                            view = Ext.create('appMis.view.employeeRyxxbd.EmployeeDisplayDlwh');
                            Ext.getCmp("employeeDisplayDlwh").getEl().dom.src = 'images/photoD/' + obj.getValue() + '.jpg?rand' + Math.random();// Ext.getCmp("employeeDisplayDlwh").src='images/photoD/'+obj.getValue()+'.jpg?rand'+Math.random();//这样不行
                            // view = Ext.widget('mainBusinessselectDepartment');//view =Ext.create('appMis.view.user.SelectDepartment')//奇怪！！！随意修改以下两行或其中一行的数值，就可实现modal
                            this.upper.style.setPropertyValue('z-index', 0);//可在最前层显示图片
                        }*/
                    }
                },

                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'ygzdw',
                    fieldLabel: '原工作单位',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'drgzdw',
                    fieldLabel: '调入工作单位',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'bdyy',
                    fieldLabel: '变动原因',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'shra',
                    fieldLabel: '单位审核人',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'shrb',
                    fieldLabel: '主管单位审核人',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'shrc',
                    fieldLabel: '区人社局审核人',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'shrd',
                    fieldLabel: '市人社局审核人',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'shre',
                    fieldLabel: '调入区人社局审核人',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'shrf',
                    fieldLabel: '调入主管单位审核人',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'shrg',
                    fieldLabel: '调入单位审核人',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'datefield',
                    columnWidth: .25,
                    format: 'Y年m月d日',
                    name: 'bdzxDate',
                    fieldLabel: '变动执行日期',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    hidden: true,
                    name: 'treeId',
                    fieldLabel: '单位码',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth: .25,
                    name: 'bz',
                    fieldLabel: '备注',
                    readOnly:true,
                    labelWidth: 110,
                    labelStyle: 'padding-left:10px'
                },
                {
                    xtype: "button",
                    columnWidth: 0.15,
                    text: "查看调令",
                    handler : function(){
                        //alert("取任务调令扫描的附件semployeeCode==="+semployeeCode)
                       // var procDefId="",executionId="",procInstId="",taskId=""
                        Ext.Ajax.request({
                            //url: '/appMis/processTask/findProcessPic?procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_'),
                            url: '/appMis/processTask/findProcessPicByRydwbdEmployeeCode?employeeCode='+semployeeCode,
                            async: false,//同步请求数据, true异步
                            success://回调函数
                                function (resp, opts) {//成功后的回调方法
                                    obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                    if (obj.success == true) {
                                        procDefId=obj.procDefIds;
                                        executionId=obj.executionIds;
                                        procInstId=obj.procInstIds;
                                        taskId=obj.taskIds;
                                       // return
                                    }
                                }
                        });
                      // alert("===procDefId==="+procDefId+"===procInstId==="+procInstId+"procTaskContentVar.employeeDlwh=="+procTaskContentVar.employeeDlwh)
                        if(procInstId==""){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: "该事务已经被撤消中止，无法查看其它信息！",
                                align: "centre",
                                buttons: Ext.MessageBox.OK
                            })
                            return
                        }
                        /*Ext.Ajax.request({
                            url: '/appMis/processTask/getAttachment?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId,
                            async: false,//同步shu
                            method: 'POST',
                            success://回调函数
                        function (resp, opts) { }
                        });*/
                        view1=Ext.create('Ext.window.Window',{
                            //width: 900,
                            //height: 600,
                            width: '100%',
                            height: '100%',
                            title: '调令文号:'+procTaskContentVar.employeeDlwh,
                            layout: 'fit',
                            autoShow: true,
                            modal: true,//创建模态窗口
                            items:[
                                {
                                    xtype: 'component',
                                    id: "displayDlwhContent",
                                    autoEl: {
                                        tag:'iframe',
                                        style:'height:100%;width:100%;border:none',
                                        src:'/appMis/processTask/getAttachment?num=0&taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId,
                                        //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                    }
                                }
                                /*{
                                        xtype: 'image',
                                        columnWidth:1,
                                        // src: '/appMis/static/photoD/642522Dlwh.jpg?rand' + Math.random(),
                                        src: '/appMis/static/photoD/' + taskId + 'Dlwh.jpg?rand' + Math.random(),
                                        autoShow : true
                                }*/
                                /*
                                {
                                                xtype: 'box', //或者xtype: 'component',
                                                id: 'photoImage',
                                                columnWidth: 1,
                                                width: 160, //图片宽度
                                                height: 170, //图片高度
                                                autoShow: true,
                                                autoEl: {
                                                    tag: 'img',    //指定为img标签
                                                   // src: '/appMis/images/photo/市人社局/李丹140423198606090826.jpg?rand=h9xqeI'   //指定url路径
                                                    src:'/appMis/employee/displayPhoto?employeeCode=' + semployeeCode+'&time=' + new Date()
                                                }
                                            }
                                */
                            ],
                            listeners :{
                                'hide':function() {
                                    //alert("win窗口关闭时，会执行本方法!!!!==="+s)
                                    //删除生成的调令附件文件
                                    /* Ext.Ajax.request({
                                         url: '/appMis/processTask/deleteAttachmentPic?executionId='+taskId,
                                         //url: '/appMis/mainBusiness/deleteExcelDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
                                         ansync: false,
                                         method: 'POST',
                                         success://回调函数
                                             function (resp, opts) { }
                                     })*/
                                }
                            }
                        });
                        //this.upper.style.setPropertyValue('z-index', 0);//可在最前层显示图片
                    }
                },
                {
                    xtype:'label',
                    columnWidth: .005
                },
                {
                    xtype: "button",
                    columnWidth: 0.15,
                    text: "查看个人工资详情",
                    handler : function(){
                        var procDefId="",executionId="",procInstId="",taskId=""
                        Ext.Ajax.request({
                            //url: '/appMis/processTask/findProcessPic?procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_'),
                            url: '/appMis/processTask/findProcessPicByRydwbdEmployeeCode?employeeCode='+semployeeCode,
                            async: false,//同步请求数据, true异步
                            success://回调函数
                                function (resp, opts) {//成功后的回调方法
                                    obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                    if (obj.success == true) {
                                        procDefId=obj.procDefIds;
                                        executionId=obj.executionIds;
                                        procInstId=obj.procInstIds;
                                        taskId=obj.taskIds;
                                        return
                                    }
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
                        /*Ext.Ajax.request({
                            url: '/appMis/processTask/getAttachment?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId,
                            async: false,//同步shu
                            method: 'POST',
                            success://回调函数
                        function (resp, opts) { }
                        });*/
                        view1=Ext.create('Ext.window.Window',{
                            id: "displayDlwhContent",
                            //width: 900,
                            //height: 600,
                            width: '100%',
                            height: '100%',
                            title: '个人工资详情',
                            layout: 'fit',
                            autoShow: true,
                            modal: true,//创建模态窗口
                            items:[
                                {
                                    xtype: 'component',
                                    //id: "displayDlwhContent",
                                    autoEl: {
                                        tag:'iframe',
                                        style:'height:100%;width:100%;border:none',
                                        src:'/appMis/processTask/getAttachment?num=1&taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+'&departmentName='+departmentName+'&name='+name,
                                        //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                    }
                                }
                            ],
                            buttons : [
                               {
                                    text: '导出Excel文件',
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        Ext.MessageBox.buttonText.ok = "完成";
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            // msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + employeeRecord.get('employeeCode')+ employeeRecord.get('name') +"个人工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + taskId +departmentName+name+"个人工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            align: "centre",
                                            buttons: Ext.MessageBox.OK
                                        })
                                    }
                                },
                                {
                                    text: '关闭',
                                    glyph: 'xf00d@FontAwesome',
                                    handler: function (button, e) {
                                        Ext.Ajax.request({
                                            url: '/appMis/processTask/deleteAttachmentExcelEmployeeDetail?taskId=' + taskId+'&departmentName='+departmentName+'&name='+name,
                                            //url: '/appMis/employee/deleteExcelEmployeeDetail?employeeCode=' + employeeRecord.get('employeeCode')+'&name='+ employeeRecord.get('name'),
                                            ansync: false,
                                            method: 'POST',
                                            success://回调函数
                                                function (resp, opts) {
                                                }
                                        });
                                        Ext.getCmp("displayDlwhContent").close()
                                    }
                                }
                            ],
                            listeners :{
                                'hide':function() {
                                    Ext.Ajax.request({
                                        url: '/appMis/processTask/deleteAttachmentExcelEmployeeDetail?taskId=' + taskId,
                                        //url: '/appMis/employee/deleteExcelEmployeeDetail?employeeCode=' + employeeRecord.get('employeeCode')+'&name='+ employeeRecord.get('name'),
                                        ansync: false,
                                        method: 'POST',
                                        success://回调函数
                                            function (resp, opts) {
                                            }
                                    });
                                }
                            }
                        });
                        //this.upper.style.setPropertyValue('z-index', 0);//可在最前层显示图片
                    }
                }
                /*,{
                    xtype:'label',
                    columnWidth: .005
                },
                {
                    xtype: "button",
                    columnWidth: 0.15,
                    text: "查看本人工资详细情况",
                    handler: function (button, e) {

                        //alert("semployeeCode===="+semployeeCode+"====name==="+name)
                        var RydwbdformValues = Ext.getCmp('employeeRydwbdform').getValues();//此处可以获取form对象的所有值
                        //alert("name====="+formValues['name'])
                        var shra=RydwbdformValues['shra'];
                        var shrb=RydwbdformValues['shrb'];
                        var shrc=RydwbdformValues['shrc'];
                        var shrd=RydwbdformValues['shrd'];
                        var shre=RydwbdformValues['shre'];
                        var shrf=RydwbdformValues['shrf'];
                        var shrg=RydwbdformValues['shrg'];
                        var formWin = Ext.create('Ext.window.Window', {
                            id: 'employeeDetailWin',
                            width: '100%',
                            height: '100%',
                            //title: ('显示当前职工【' + employeeRecord.get('employeeCode') + ' ' + employeeRecord.get('name') + ' 】的个人详细信息'),
                            title: ('显示当前职工【' + semployeeCode + ' ' + name + ' 】的个人详细信息'),
                            autoShow: true,
                            modal: true,//创建模态窗口
                            layout:{
                                type: 'vbox',
                                pack: 'start',
                                align: 'stretch'
                            },
                            items: [{
                                xtype: 'panel',
                                id:'employeeDisplayDetail',
                                autoScroll: true,
                                bodyPadding: 0,
                                border: 0,
                                flex:1,
                                items: [{
                                    xtype: 'component',
                                    id: "displayKyxmSbs",
                                    autoEl: {
                                        tag:'iframe',
                                        style:'height:100%;width:100%;border:none',
                                        src:'/appMis/employee/displayEmployeeDetail?employeeCode=' + semployeeCode+'&zbr='+loginTruename+'&shra='+shra+'&shrb='+shrb+'&shrc='+shrc+'&shrd='+shrd+'&shre='+shre+'&shrf='+shrf+'&shrg='+shrg
                                        //src:'/appMis/employee/displayEmployeeDetail?employeeCode=' + employeeRecord.get('employeeCode')+'&zbr='+loginTruename
                                        //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                    }
                                }]
                            }],
                            buttons : [

                                {
                                    text: '导出Excel文件',
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        Ext.MessageBox.buttonText.ok = "完成";
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                           // msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + employeeRecord.get('employeeCode')+ employeeRecord.get('name') +"个人工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + semployeeCode+name +"个人工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            align: "centre",
                                            buttons: Ext.MessageBox.OK
                                        })
                                    }
                                },
                                {
                                    text: '关闭',
                                    glyph: 'xf00d@FontAwesome',
                                    handler: function (button, e) {
                                        Ext.Ajax.request({
                                            url: '/appMis/employee/deleteExcelEmployeeDetail?employeeCode=' + semployeeCode+'&name='+ name,
                                            //url: '/appMis/employee/deleteExcelEmployeeDetail?employeeCode=' + employeeRecord.get('employeeCode')+'&name='+ employeeRecord.get('name'),
                                            ansync: false,
                                            method: 'POST',
                                            success://回调函数
                                                function (resp, opts) {
                                                }
                                        });
                                        Ext.getCmp("employeeDetailWin").close()
                                    }
                                }
                            ],
                            listeners : {
                                'hide':function() {
                                    //win窗口关闭时，会执行本方法。 //删除职工详细信息生成Excel文件
                                    Ext.Ajax.request({
                                        url: '/appMis/employee/deleteExcelEmployeeDetail?employeeCode=' + semployeeCode+'&name='+ name,
                                       // url: '/appMis/employee/deleteExcelEmployeeDetail?employeeCode=' + employeeRecord.get('employeeCode')+'&name='+ employeeRecord.get('name'),
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
                }*/
            ]
        }
    ]
});
 
