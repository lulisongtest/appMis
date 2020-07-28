//查看申报事务的情况
var ENTERflag = 0
function setENTERflag(x) {//回车则失去焦点，防止再次调用Blur
    ENTERflag = x
    return
}

var DepartmentSbStatusStore = Ext.create('Ext.data.Store', {
    fields: ['id', 'department', 'dwjc','dwtjsl','gzzescbm','departmentCode','lsgx','lsxt','xzhq','dwxz','czbkxs',
        'jblq','dwjb','sshy','cgqk','zgbmqk','sjgzzgbm','bzs','syrs','bzqk','bzpwqk','hsxz','leader','contacts','phone','fax','postcode','address','treeId','glyph'],
    autoSync : true,//这样修改完一个单元格后会自动保存数据
    autoLoad:  true,//是否与上一句是一个功能！？
    pageSize: 25,
    remoteSort: true,
    sorters: [{
        property: 'treeId',
        direction: 'ASC'
    }],
    proxy: {
        type: 'ajax',
        api: {
            read: '/appMis/department/readDepartmentSbStatus?dep_tree_id=',
        },
        noCache: false,
        actionMethods: {
            read   : 'POST'
        },
        reader: {
            type: 'json',
            rootProperty: 'departments',
            totalProperty: "totalCount"
        },
        writer:{
            type:'json'
        }
    }
});







Ext.define('appMis.view.processTask.ProcessStatusGridViewport', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.processStatusgridviewport',
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
        //var readAuth=Ext.getCmp('processTaskReadAuth').isHidden()//true为普通用户，只能查看数据，不能修改任何数据。
        var readAuth =  ((Roleflag!="单位管理员")&&(Roleflag!="管理员"))
        this.store= DepartmentSbStatusStore,
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
                }) , {
                    header: '申报单位名称',
                    dataIndex: 'department',
                    width: 180

                } , {
                    header: '流程名称',
                    hidden:true,
                    dataIndex: 'PROC_NAME_',
                    width: 180

                } ,{header: '单位审核人',
                    dataIndex: 'shra',
                    width: 180
                }

                ,{header: '主管单位审核人',
                    dataIndex: 'shrb',
                    width: 180
                }

                ,{header: '区人社局审核人',
                    dataIndex: 'shrc',
                    width: 180
                }

                ,{header: '市人社局审核人',
                    dataIndex: 'shrd',
                    width: 180
                } ,{header: '区人社局审核人',
                    dataIndex: 'shre',
                    width: 180
                } ,{header: '主管单位审核人',
                    dataIndex: 'shrf',
                    width: 180
                } ,{header: '单位审核人',
                    dataIndex: 'shrg',
                    width: 180
                }
              /*  , {
                    header: '流程名称',
                    dataIndex: 'PROC_NAME_',
                    width: 180

                },{
                    text: "查看流程图且审批",
                    width: 160,
                    align: "center",
                    renderer: function (value, cellmeta) {
                        if((loginUserRole == "管理员")||(loginUserRole == "单位管理员")){
                            var returnStr = "<INPUT type='button' value='查看流程内容'>";
                        }else{
                            var returnStr = "<INPUT type='button' value='查看流程内容且审批'>";
                        }
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
                    width: 120
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
                    width: 190

                }, {
                    header: '任务申报单位',
                    dataIndex: 'department',
                    width: 200
                },{
                    header: '任务执行人',
                    dataIndex: 'ASSIGNEE_',
                    width: 100,
                    renderer : function(value) {
                        return value.split(":")[value.split(":").length-1] ;
                    }
                }, {
                    header: '审核状态',
                    dataIndex: 'STATUS_',
                    width: 230
                }, {
                    header: '任务创建时间',
                    dataIndex: 'CREATE_TIME_',
                    width: 120,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        name: 'CREATE_TIME_'
                    }
                }, {
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
                    header: '任务过期时间',
                    dataIndex: 'DUE_DATE_',
                    width: 120,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        name: 'DUE_DATE_'
                    }
                }*/
            ];
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: DepartmentSbStatusStore,
            displayInfo: true,
            id: 'bbarProcessStatus',
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
                   // value: 500,
                    value: 25,
                    listeners: {
                        beforerender: function (combo) {

                        },
                        afterrender: function (combo) {
                            //var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            //panelStore.proxy.api.read = 'processTask/readProcessTask?dep_tree_id=' + currentTreeNode.substr(1);
                           // panelStore.loadPage(1);
                        },
                        change: function (combo) {
                            var pageSize=combo.getValue();
                            if (pageSize == null) {
                                pageSize = 25
                            }
                            /* var panelStore = (Ext.getCmp('processTaskgrid')).getStore();
                            panelStore.pageSize = pageSize;
                            panelStore.proxy.api.read = "/appMis/processTask/readProcessTask?dep_tree_id="+currentTreeNode.substr(1)+'&procDefId=' + Ext.getCmp('processTaskProcDefId').getValue()+'&department=' +  Ext.getCmp('processTaskDep').getValue()+'&status=' +  Ext.getCmp('processTaskStatus').getValue();
                            panelStore.loadPage(1);*/
                            var panelStore = (Ext.getCmp('processStatusgrid')).getStore();
                            panelStore.pageSize = pageSize;
                            panelStore.proxy.api.read = "/appMis/department/readDepartmentSbStatus?dep_tree_id="+currentTreeNode.substr(1)+'&currentProcessDate=' + (new Date(Ext.getCmp('currentProcessDate').getValue()).pattern("yyyy-MM-dd"))+'&procDefId=' + Ext.getCmp('processTaskProcDefIdStatus').getValue()+'&department=' +  Ext.getCmp('processTaskDepStatus').getValue()+'&status=' +  Ext.getCmp('processTaskStatusStatus').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});
           
