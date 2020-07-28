
//不用了！！！！！！！！！！！！
Ext.define('appMis.view.processTask.DisplayAndCompleteProcessHistoryTask', {
    extend: 'Ext.window.Window',
    xtype: 'displayAndCompleteProcessHistoryTask',
    id: 'displayAndCompleteProcessHistoryTask',
    width: '100%',
    height: '100%',
    title: '查看当前任务',
    //layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    requires: [],
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
                height: 170, //图片高度
                id: 'processHistoryDiagramImage',
                columnWidth: 1,
                width: '100%',
                // width: 1200, //图片宽度
                autoShow: true,
                autoEl: {
                    tag: 'img',    //指定为img标签
                    style: "",//不加这行，图像不显示！不知为什么？
                    src: '/appMis/processDiagram/watch.png?rand=' + Math.random()   //指定url路径
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
});
           
