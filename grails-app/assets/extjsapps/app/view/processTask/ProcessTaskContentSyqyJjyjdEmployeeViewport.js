
var newvalue1
var newvalue2
Ext.define('appMis.view.processTask.ProcessTaskContentSyqyJjyjdEmployeeViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processTaskContentSyqyJjyjdEmployeeViewport',
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
            id: 'syqyJjyjdEmployeeform',
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
                /*{
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'employeeCode',
                    fieldLabel:'晋级与晋档人员编码',
                    readOnly:true,
                    labelWidth:130,
                    labelStyle:'padding-left:10px'
                },
                {
                    xtype: 'textfield',
                    columnWidth:.25,
                    name : 'name',
                    fieldLabel:'晋级与晋档人员姓名',
                    readOnly:true,
                    labelWidth:130,
                    labelStyle:'padding-left:10px'
                },*/
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
                    text: "查看晋级与晋档人员申报详细信息",
                    handler : function(){
                        //alert("procTaskContentVar====="+procTaskContentVar.zbr)
                       //从form中获取数据
                        var values=Ext.getCmp('syqyJjyjdEmployeeform').getValues()
                        values.sbDate = (values.sbDate).replace(/(年)|(月)/g, "-");
                        values.sbDate = (values.sbDate).replace(/(日)/g, "");
                        var departmentDetail=values.department//改为局部变量
                        departmentName=departmentDetail
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
                            id: 'SyqyJjyjdsbDetailWin',
                            width: '100%',
                            height: '100%',
                            title: ('显示当前【'+departmentName+ (new Date(values.sbDate).pattern("yyyy年MM月"))+'】单位批量晋级与晋档自动申报表'),
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
                                    id:'displaySyqyJjyjdsbx',
                                    autoScroll: true,
                                    bodyPadding: 0,
                                    border: 0,
                                    flex:1,
                                    items: [{
                                        xtype: 'component',
                                        id: "displayKyxmSbs",
                                        autoEl: {
                                            tag:'iframe',
                                            height:"100%",
                                            width:"100%",
                                            //style:'height:100%;width:100%;border:none',
                                            src:'/appMis/employeeSyqyGztz/displaySyqyJjyjdsb?taskId='+taskId+'&procDefId=' + procDefId + '&executionId=' + executionId + '&procInstId=' + procInstId+ '&dep_tree_id='+currentTreeNode.substr(1)+'&displaySyqyJjyjdStatus=已申报&departmentName='+departmentName+'&zbr='+values.zbr+'&shra='+values.shra+'&shrb='+values.shrb+'&shrc='+values.shrc+'&shrd='+values.shrd+'&currentSbDate=' +  (new Date(values.sbDate).pattern("yyyy-MM-dd")),
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
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + departmentName+(new Date(values.sbDate).pattern("yyyy年M月"))+"石油企业单位晋级与晋档申报表.xlsx\"><b>保存该EXCEL文件</b></a>",

                                           // msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + values.employeeCode+ values.name +"个人工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
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
                                            url: '/appMis/employeeSyqyGztz/deleteExcelSyqyJjyjdsb?departmentName='+departmentName+'&currentSbDate=' +  (new Date(values.sbDate).pattern("yyyy-MM-dd")),
                                            ansync: false,
                                            method: 'POST',
                                            success://回调函数
                                                function (resp, opts) {
                                                }
                                        });
                                        Ext.getCmp("SyqyJjyjdsbDetailWin").close()
                                    }
                                }],
                            listeners : {
                                'hide':function() {
                                    //win窗口关闭时，会执行本方法。 //删除职工详细信息生成Excel文件
                                    Ext.Ajax.request({
                                        url: '/appMis/employeeSyqyGztz/deleteExcelSyqyJjyjdsb?departmentName='+departmentName+'&currentSbDate=' +  (new Date(values.sbDate).pattern("yyyy-MM-dd")),
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
 
