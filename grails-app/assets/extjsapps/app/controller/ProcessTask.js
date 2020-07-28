
Ext.define('appMis.controller.ProcessTask', {
    extend: 'Ext.app.Controller',
    init: function () {
        this.control({
            'displayAndCompleteProcessTask > panel > panel >   toolbar > button[action=agree]': {
                 click: this.agree
            },
            'displayAndCompleteProcessTask > panel > panel >  toolbar >  button[action=disagree]': {//panel中的按钮
                click: this.disagree
            },
            'employeeSalaryExplain  button[action=printEmployeeSalaryExplain]': {//window中的按钮
                click: this.printEmployeeSalaryExplain
            },
            'processTaskviewport > toolbar >  button[action=delete]': {
                click: this.deleteTask
            },
            'processHistoryTaskviewport > toolbar >  button[action=deleteHistory]': {
                 click: this.deleteTaskHistory
            }
        })
    },


    //打印工资关系介绍信
    printEmployeeSalaryExplain: function (button) {
        var LODOP=getLodop();
        LODOP.PRINT_INIT();
        LODOP.ADD_PRINT_HTM('1%','1%','98%','98%',document.getElementById("employeeSalaryExplain").innerHTML);
        LODOP.SET_PRINT_MODE("FULL_WIDTH_FOR_OVERFLOW",true);
        LODOP.SET_PRINT_MODE("FULL_HEIGHT_FOR_OVERFLOW",true);
        LODOP.SET_PREVIEW_WINDOW(1,0,0,0,0,"");
        LODOP.PREVIEW();
    },

    //删除指定的任务所在的流程实例
    deleteTaskHistory: function (button) {
      // alert("删除指定的历史任务所在的流程实例")
        var records = (Ext.getCmp("processHistoryTaskgrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("processHistoryTaskgrid")).getStore();
        var currPage = panelStore.currentPage;
        if (records.length == 0) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请先选择您要删除的行数据!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        } else {
            for (var i = 0; i < records.length; i++) {
                this.removeHistory(records[i].get('PROC_INST_ID_'))//删除选中的记录
            }
            Ext.MessageBox.show({
                title: "提示",
                msg: "成功删除" + records.length + "条任务!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
               // panelStore.proxy.api.read = '/appMis/processTask/readProcessHistoryTask?dep_tree_id=' + currentTreeNode.substr(1);
            panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' +  Ext.getCmp('processHistoryTaskProcDefId').getValue();
              //  panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();

                panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX
                Ext.Msg.hide();
            }, 3500);

        }

    },
    removeHistory: function (procInstId) {
        Ext.Ajax.request({
            url:'/appMis/processTask/deleteHistoryTask?procInstId=' + procInstId+'&userRole='+loginUserRole,
            method: 'POST',
            success: function (resp, opts) {//成功后的回调方法

            },
            failure: function (resp, opts) {

            }
        })
    },



//删除指定的任务所在的流程实例
    deleteTask: function (button) {
        var records = (Ext.getCmp("processTaskgrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("processTaskgrid")).getStore();
        var currPage = panelStore.currentPage;
        if (records.length == 0) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请先选择您要删除的行数据!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        } else {
            for (var i = 0; i < records.length; i++) {
                this.remove(records[i].get('PROC_INST_ID_'))//删除选中的记录
            }
           /* panelStore.loadPage(currPage);//刷新当前grid页面
            if (((panelStore.totalCount - records.length) % panelStore.pageSize) == 0) {
                currPage = currPage - 1
                if (currPage == 0)currPage = 1
            }
            panelStore.loadPage(currPage);//刷新当前grid页面
            */
            Ext.MessageBox.show({
                title: "提示",
                msg: "成功删除" + records.length + "条任务!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
               // panelStore.proxy.api.read = '/appMis/processTask/readProcessTask?dep_tree_id=' + currentTreeNode.substr(1);
                panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentProcessDateRange').getValue();
                panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX
                Ext.Msg.hide();
            }, 3500);

        }
    },
    remove: function (procInstId) {
        Ext.Ajax.request({
            url:'/appMis/processTask/deleteTask?procInstId=' + procInstId+'&userRole='+loginUserRole,
            method: 'POST',
            success: function (resp, opts) {//成功后的回调方法

            },
            failure: function (resp, opts) {

            }
        })
    },

    //审核同意
    agree: function (button) {
        Ext.Ajax.request({
            url:'/appMis/processTask/agreeProcessTask?taskId='+record.get('ID_')+'&procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_')+'&shyj='+Ext.query('[name=shyj]')[0].value+'&procName='+record.get('PROC_NAME_'),
            success: function (resp, opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: "成功审核数据! ",
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    view.close()
                    var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                   // panelStore.proxy.api.read = '/appMis/processTask/readProcessTask?dep_tree_id=' + currentTreeNode.substr(1);
                    panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue();

                    panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX
                    Ext.Msg.hide();
                }, 2500);
            }
        });
    },

    //审核不同意
    disagree: function (button) {
        Ext.Ajax.request({
            url:'/appMis/processTask/disagreeProcessTask?taskId='+record.get('ID_')+'&procDefId=' + record.get('PROC_DEF_ID_') + '&executionId=' + record.get('EXECUTION_ID_') + '&procInstId=' + record.get('PROC_INST_ID_')+'&shyj='+Ext.query('[name=shyj]')[0].value+'&procName='+record.get('PROC_NAME_'),
            success: function (resp, opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: "成功审核数据! ",
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {

                    var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                   // panelStore.proxy.api.read = '/appMis/processTask/readProcessTask?dep_tree_id=' + currentTreeNode.substr(1);
                    panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue();

                    panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX
                    Ext.Msg.hide();
                    view.close()
                }, 2500);
            }
        });
    }
});
