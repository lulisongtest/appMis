
Ext.define('appMis.view.processAdmin.ProcessAdminViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processAdminviewport',
    requires: [
        'appMis.view.processTask.ProcessStatusViewport',
        'appMis.view.processTask.ProcessStatusGridViewport',
        'appMis.view.processTask.ProcessTaskGridViewport',
        'appMis.view.processTask.DisplayAndCompleteProcessTask',
        'appMis.view.processTask.ProcessTaskContentEmployeeRydwbdViewport',
        'appMis.view.processTask.ProcessTaskContentDepMonthlySalaryViewport',
        'appMis.view.processTask.ProcessTaskContentYlbxjfjsViewport',
        'appMis.view.processTask.ProcessTaskContentNzycxjlSalaryViewport',
        'appMis.view.processTask.ProcessTaskContentNewEmployeeViewport',
        'appMis.view.processTask.ProcessTaskContentRetireEmployeeViewport',
        'appMis.view.processTask.ProcessTaskContentSyqyJjyjdEmployeeViewport',
        'appMis.view.processTask.ProcessTaskContentJjyjdEmployeeViewport',
        'appMis.view.processTask.ProcessTaskContentDimissionEmployeeViewport',
        'appMis.view.processTask.ProcessTaskContentGzllbdEmployeeViewport',
        'appMis.view.processTask.EmployeeSalaryExplain',
        'appMis.view.processTask.ProcessHistoryTaskGridViewport',
        'appMis.view.processTask.DisplayAndCompleteProcessHistoryTask'
    ],
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
            xtype: 'tabpanel',
            flex:1,
            id: 'processadmintabpanel',
            header: false,
            activeTab: 'displayTask1',
            //activeTab: 'displayProcess1',
            defaults: {
                autoScroll: true,
                bodyPadding: 0,
                border: 0,
                padding: 0
            },
            closable : true,
            items: [
                {
                    xtype: 'processDiagramviewport',
                    id: 'displayProcess1',
                    hidden: (Roleflag != "管理员"),  //慎重操作此项。只有管理员才能操作
                    glyph: 'xf022@FontAwesome',
                    title: '查看流程'
                },
                {
                    xtype: 'processTaskviewport',
                    id: 'displayTask1',
                    glyph: 'xf022@FontAwesome',
                    title: '查看当前任务'
                }, {
                    xtype: 'processHistoryTaskviewport',
                    id: 'displayHistoryTask1',
                    glyph: 'xf022@FontAwesome',
                    title: '查看已办理的任务'
                }, {
                    xtype: 'processStatusviewport',
                    hidden: (Roleflag != "管理员"),  //慎重操作此项。只有管理员才能操作
                    id: 'displayProcessStatus',
                    glyph: 'xf022@FontAwesome',
                    tooltip :'注意：此功能必须在【流程定义(名称)】下拉列表中选一个具体流程名！可能统计耗时较长！',
                    title: '查看单位事务申报状态'
                }
            ],
            listeners: {
                tabchange: function (tabPanel) {
                    switch (tabPanel.getActiveTab().title) {
                        case '查看流程' :
                            //alert("查看流程");
                            break;
                       case '查看当前任务' :
                           // alert("查看当前任务");
                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = '/appMis/processTask/readProcessTask?dep_tree_id=' + currentTreeNode.substr(1);
                            panelStore.loadPage(1);
                            break;
                        case '查看已办理的任务' :
                            //alert("查看已办理的任务");
                            var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.proxy.api.read = '/appMis/processTask/readProcessTask?dep_tree_id=' + currentTreeNode.substr(1);
                            panelStore.loadPage(1);
                            var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                            panelStore.proxy.api.read = '/appMis/processTask/readProcessHistoryTask?dep_tree_id=' + currentTreeNode.substr(1);
                            panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX
                            break;
                         /*case '查看单位事务申报状态' :
                            //alert("查看单位事务申报状态");
                            var panelStore = (Ext.getCmp('processStatusgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/department/readDepartmentSbStatus?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefIdStatus').getValue()+'&department=' +  Ext.getCmp('processTaskDepStatus').getValue()+'&status=' +  Ext.getCmp('processTaskStatusStatus').getValue();
                            panelStore.loadPage(1);
                            break;*/
                    }
                }
            }
         }
    ]
});
 
