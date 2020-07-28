/*function b(v){
    alert(this.temp+" "+v);
}
b.call({temp:'abc'},'hello');

Function.prototype.createDelegate=function(scope,params){
    var _fun = this;
    return function(){
        _fun.apply(scope,params);
    };
}
var fun = b.createDelegate({temp:'abc'},['hello']);
fun();
*/
var ENTERflag = 0
function setENTERflag(x) {//回车则失去焦点，防止再次调用Blur
    ENTERflag = x
    return
}
Ext.define('appMis.view.processTask.ProcessHistoryTaskGridViewport', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.processHistoryTaskgridviewport',
    disableSelection: false,
    loadMask: true,
    columnLines: true,
    stateful: true,
   // stateId: 'stateGrid0',
    queryMode: 'remote',//会实时发ajax请求去load数据，这时默认会出现loadmask
    //height: 500,//正文区的高度
    viewConfig: {
        margins: '0 0 30 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function () {
        //this.height=500//正文区的高度
        //var readAuth=Ext.getCmp('processHistoryTaskReadAuth').isHidden()//true为普通用户，只能查看数据，不能修改任何数据。
        var readAuth =  ((Roleflag!="单位管理员")&&(Roleflag!="管理员"))
        this.store = 'ProcessHistoryTaskStore';
        this.selType = 'cellmodel'//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })]
        //this.selModel = Ext.create('Ext.selection.CheckboxModel', {mode: "SINGLE"}),//"SINGLE"/"SIMPLE"/"MULTI"有复选框
        this.selModel = Ext.create('Ext.selection.CheckboxModel')//有复选框
            this.columns = [
                Ext.create('Ext.grid.RowNumberer', {
                    text: '序号',
                    width: 70
                })
                , {
                    header: '流程名称',
                    dataIndex: 'PROC_NAME_',
                    width: 180

                },{
                    text: "查看流程图",
                    width: 160,
                    align: "center",
                    renderer: function (value, cellmeta) {
                        var returnStr = "<INPUT type='button' value='查看流程内容'>";
                        return returnStr;
                    }
                }
                , {
                    header: '任务ID',
                    hidden:true,
                    dataIndex: 'ID_',
                    width: 80
                }, {
                    header: '任务执行名称',
                    //header: '任务名称',
                    dataIndex: 'NAME_',
                    width: 140

                }, {
                    header: '流程实例</br>PROC_INST_ID_',
                    hidden:true,
                    titleAlign:'center',
                    dataIndex: 'PROC_INST_ID_',
                    width: 120

                }, {
                    header: '流程定义</br>PROC_DEF_ID_',
                    hidden:true,
                    titleAlign:'center',
                    dataIndex: 'PROC_DEF_ID_',
                    width: 200

                }, {
                    header: '节点定义</br>TASK_DEF_KEY_',
                    hidden:true,
                    titleAlign:'center',
                    dataIndex: 'TASK_DEF_KEY_',
                    width: 120

                }, {
                    header: '执行实例</br>EXECUTION_ID_',
                    hidden:true,
                    titleAlign:'center',
                    dataIndex: 'EXECUTION_ID_',
                    width: 120
                }, {
                    header: '涉及职工姓名',
                    titleAlign:'center',
                    dataIndex: 'procJoinName',
                    width: 120
                },  {
                    header: '涉及职工身份',
                    titleAlign:'center',
                    dataIndex: 'procJoinId',
                    width: 140
                }, {
                    header: '任务申报单位',
                    dataIndex: 'department',
                    width: 260
                }, {
                    header: '任务执行人',
                    dataIndex: 'ASSIGNEE_',
                    width: 160,
                    renderer : function(value) {
                        return value.split(":")[value.split(":").length-1] ;
                    }
                }, {
                    header: '任务开始时间',
                    dataIndex: 'START_TIME_',
                    width: 120,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        name: 'START_TIME_'
                    }
                }, {
                    header: '任务删除原因',
                    dataIndex: 'DELETE_REASON_',
                    width: 120
                },{
                    header: '任务提醒时间',
                    dataIndex: 'CLAIM_TIME_',
                    width: 120,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        name: 'CLAIM_TIME_'
                    }

                }, {
                    header: '任务结束时间',
                    dataIndex: 'END_TIME_',
                    width: 120,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        name: 'END_TIME_'
                    }

                }, {
                    header: '任务耗时',
                    dataIndex: 'DURATION_',
                    width: 120
                }, {
                    header: '任务过期时间',
                    dataIndex: 'DUE_DATE_',
                    width: 120,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        name: 'DUE_DATE_'
                    }
                }
            ];
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: 'ProcessHistoryTaskStore',
            displayInfo: true,
            id: 'bbarProcessHistoryTask',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            height: 40,
            items: [
                '-', '每页数据', {
                    xtype: 'combobox',
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender: true,
                    mode: 'local',
                    width: 80,
                    store: [1, 5, 10, 15, 20, 25, 30, 50, 75, 100, 200, 300, 500],
                    enableKeyEvents: true,
                    editable: true,
                    value: 25,
                    listeners: {
                        beforerender: function (combo) {
                            var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                           // panelStore.proxy.api.read = '/appMis/processTask/readProcessHistoryTask?dep_tree_id=' + currentTreeNode.substr(1);
                           // panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' +  Ext.getCmp('processHistoryTaskProcDefId').getValue();

                           // panelStore.loadPage(1);
                        },
                        afterrender: function (combo) {
                            //var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                            //combo.setValue(panelStore.pageSize)//初始化pageSize
                           // panelStore.loadPage(1);
                        },
                        change: function (combo) {
                            pageSize=combo.getValue();
                            if (pageSize == null) {
                                pageSize = 25
                            }//不选则每页25行数据
                            var panelStore = (Ext.getCmp('processHistoryTaskgrid')).getStore();
                            panelStore.pageSize = pageSize;
                            //panelStore.proxy.api.read = 'processTask/readProcessHistoryTask';
                           // panelStore.proxy.api.read = '/appMis/processTask/readProcessHistoryTask?dep_tree_id=' + currentTreeNode.substr(1);
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessHistoryTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processHistoryTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processHistoryTaskDep').getValue()+'&status=' +  Ext.getCmp('processHistoryTaskStatus').getValue()+'&currentProcessDate1='+ (new Date(Ext.getCmp('currentHistoryProcessDate1').getValue()).pattern("yyyy-MM-dd"))+'&currentProcessDateRange='+  Ext.getCmp('currentHistoryProcessDateRange').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});
           
