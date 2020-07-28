

var newvalue1
var newvalue2
Ext.define('appMis.view.processTask.ProcessTaskContentYlbxjfjsViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processTaskContentYlbxjfjsViewport',
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
            id: 'ylbxjfjsform',
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
                },{
                    xtype: "button",
                    columnWidth: 0.18,
                    text: "查看单位个人本年养老保险缴费基数",
                    handler : function(){
                        //从form中获取数据
                        var values=Ext.getCmp('ylbxjfjsform').getValues()
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
                        //判断养老保险缴费基数申报上传EXCEL文件是否存在
                        var currentSalaryDate=new Date(values.gzDate).pattern("yyyy-MM-dd")//改为局部变量
                        Ext.Ajax.request({//获取当前点击的职工的单位信息
                            url: '/appMis/mainBusiness/trueYlbxjfjsExcel?dep_tree_id=' + currentTreeNode.substr(1) + '&currentSalaryDate=' + currentSalaryDate + '&departmentName=' + departmentDetail,
                            async: false,//同步,true异步
                            // async: true,//同步,true异步
                            success: function (resp, opts) {
                                var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                               // alert("判断养老保险缴费基数申报上传EXCEL文件是否存在obj.success======"+obj.success)
                                if (!(obj.success)) {
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: "养老保险缴费基数申报上传EXCEL文件已经不存在了，该事务已经被撤消中止，无法查看其它信息！",
                                        align: "centre",
                                        buttons: Ext.MessageBox.OK
                                    })
                                    return
                                }else{
                                    //3-4、打开窗口显示申报养老保险缴费基数申报上传EXCEL文件的详细内容
                                    Ext.create('Ext.window.Window',{
                                        id:'displayHistoryYlbxjfjs',
                                        width: "100%",
                                        height: "100%",
                                        autoShow: true,
                                        title:departmentDetailTitle+"__"+(new Date(values.gzDate).pattern("yyyy年MM月"))+" 养老保险缴费基数申报",
                                        modal: true,//创建模态窗口
                                        layout:{
                                            type: 'vbox',
                                            pack: 'start',
                                            align: 'stretch'
                                        },
                                        items :[{
                                                xtype: 'panel',
                                               id:'ss',
                                                autoScroll: true,
                                                bodyPadding: 0,
                                                border: 0,
                                                flex:1,
                                                items: [{
                                                    xtype: 'component',
                                                    autoEl: {
                                                        tag:'iframe',
                                                        style:'height:100%;width:100%;border:none',
                                                        src:'/appMis/mainBusiness/displayHistoryYlbxjfjs?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&procName=养老保险缴费基数申报流程'+'&departmentDetailTitle='+departmentDetailTitle+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate+'&dep_tree_id='+currentTreeNode.substr(1),
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
                                                        //msg: "<a href=\"http://" + window.location.host + "/appMis/static/ylbxjfjs/" + currentDepMonthlySalaryDate.substr(0,4)+currentTreeNode.substr(1)+departmentDetail +"养老保险缴费基数.xlsx\"><b>保存该EXCEL文件</b></a>",
                                                        msg: "<a href=\"http://" + window.location.host + "/appMis/static/ylbxjfjs/" + currentDepMonthlySalaryDate.substr(0,4)+currentTreeNode.substr(1)+departmentDetail +"养老保险缴费基数.xls\"><b>保存该EXCEL文件</b></a>",
                                                        align: "centre",
                                                        buttons: Ext.MessageBox.OK
                                                    })
                                                }
                                            },
                                            {
                                                text: '关闭',
                                                glyph: 'xf00d@FontAwesome',
                                                handler: function (button, e) {
                                                    Ext.getCmp("displayHistoryYlbxjfjs").close()
                                                }
                                            }],
                                        listeners : {
                                            'hide':function() {
                                                //win窗口关闭时，会执行本方法
                                                /* Ext.Ajax.request({
                                                     url: '/appMis/mainBusiness/deleteExcelYlbxjfjs?taskId='+taskId+'&dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
                                                     ansync: false,
                                                     method: 'POST',
                                                     success://回调函数
                                                         function (resp, opts) {
                                                         }
                                                 })*/
                                            }
                                        }
                                    })
                                }
                            }
                        }) ;

                    }

                }
            ]
        }
    ]
});
 
