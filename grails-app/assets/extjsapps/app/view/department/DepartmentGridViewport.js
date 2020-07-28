var ENTERflag = 1
function setENTERflag(x) {//回车则失去焦点，防止再次调用Blur
    ENTERflag = x
    return
}
Ext.define('appMis.view.department.DepartmentGridViewport', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.departmentgridviewport',
    disableSelection: false,
    loadMask: true,
    columnLines: true,
    stateful: true,
    //stateId: 'stateGrid',
    viewConfig: {
        margins: '0 0 0 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function () {
        this.store = 'DepartmentStore';
        this.selType = 'cellmodel';//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })];
        this.selModel = Ext.create('Ext.selection.CheckboxModel');//有复选框
            this.columns = [
                Ext.create('Ext.grid.RowNumberer', {
                    text: '序号',
                    locked:true,
                    width: 75
                }),
                {
                    header: '单位名称',
                    dataIndex: 'department',
                    width: 180,
                    locked:true,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                }, {header: '单位简称',
                    dataIndex: 'dwjc',
                    sortable: true,
                    width: 180,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                }, {
                    header: '单位统计数量',
                    dataIndex: 'dwtjsl',
                    width: 160,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                }, {
                    header: '工资总额手册编码',
                    dataIndex: 'gzzescbm',
                    width: 160,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{
                    header: '组织机构代码',
                    dataIndex: 'departmentCode',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{
                    header: '隶属关系',
                    dataIndex: 'lsgx',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'党委'},{'name':'人大'},{'name':'政府'},{'name':'政协'},{'name':'法院'},{'name':'检察院'},{'name':'民主党派和工商联'},{'name':'党群和社团'},{'name':'事业单位'},{'name':'其它'}]
                        })
                    }
                },{
                    header: '隶属系统',
                    dataIndex: 'lsxt',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'中央'},{'name':'省（区、市）'},{'name':'地（市、州、盟）'},{'name':'县（市、区、旗）'},{'name':'乡（镇）'}]
                        })
                    }
                },{
                    header: '行政划区',
                    dataIndex: 'xzhq',
                    sortable: true,
                    width: 225,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'新疆维吾尔自治区克拉玛依市'}]
                        })
                    }
                }, {header: '单位性质',
                    dataIndex: 'dwxz',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'机关单位'},{'name':'全额事业（参公）'},{'name':'公益一类事业'},{'name':'公益二类事业'},{'name':'企业化管理'},{'name':'义务教育单位'},{'name':'基层医疗卫生单位'}]
                        })
                    }
                },{header: '财政拨款形式',
                    dataIndex: 'czbkxs',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'全额拨款'},{'name':'差额拨款'},{'name':'自收自支'},{'name':'其它'}]
                        })
                    }
                },{header: '艰边类区',
                    dataIndex: 'jblq',
                    sortable: true,
                    width: 160,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'一类'},{'name':'二类'},{'name':'三类'},{'name':'四类'},{'name':'五类'},{'name':'六类'}]
                        })
                    }
                },  {header: '单位级别',
                    dataIndex: 'dwjb',
                    sortable: true,
                    width: 130,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'正省级'},{'name':'副省级'},{'name':'正厅级'},{'name':'副厅级'},{'name':'正处级'},{'name':'副处级'},{'name':'正科级'},{'name':'副科级'},{'name':'股级'},{'name':'无级别'}]
                        })
                    }
                },{header: '所属行业',
                    dataIndex: 'sshy',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'农林牧渔'},{'name':'采矿'},{'name':'制造'},{'name':'电力热力燃气及水生产和供应'},{'name':'建筑'},
                                {'name':'批发和零售'},{'name':'交通运输仓储和邮政'},{'name':'住宿和餐饮'},{'name':'信息传输软件和信息技术服务'},
                                {'name':'金融'}, {'name':'房地产'},{'name':'租赁和商务服务'},{'name':'科研和技术服务'},
                                {'name':'水利环境和公共设施管理'},{'name':'居民服务修复和其它服务'},{'name':'教育'},{'name':'卫生和社会工作'},
                                {'name':'文化体育和娱乐业'},{'name':'公共管理社会保障和社会组织'},{'name':'国际组织'},{'name':'其它行业'}]
                        })
                    }
                },{header: '垂管情况',
                    dataIndex: 'cgqk',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'垂管主管部门'},{'name':'被垂管单位'}]
                        })
                    }
                }, {header: '主管部门情况',
                    dataIndex: 'zgbmqk',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'主管部门'},{'name':'所属下级单位'}]
                        })
                    }
                },{header: '上级工资主管部门',
                    dataIndex: 'sjgzzgbm',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'克拉玛依市工资科'}]
                        })
                    }
                },{
                    header: '编制情况',
                    dataIndex: 'bzqk',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField: 'name',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['name'],
                            data: [{'name': '正式编制'}, {'name': '临时编制'}]
                        })
                    }
                },{
                    header: '编制批文情况',
                    dataIndex: 'bzpwqk',
                    sortable: true,
                    width: 260,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{
                    header: '编制数',
                    dataIndex: 'bzs',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{
                    header: '实用人数',
                    dataIndex: 'syrs',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{header: '核算性质',
                    dataIndex: 'hsxz',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField:'name',
                        store:Ext.create('Ext.data.Store',{
                            fields:['name'],
                            data:[{'name':'集中核算'},{'name':'独立核算'}]
                        }),
                    }
                },{header: '单位负责人',
                    dataIndex: 'leader',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{header: '工资经办人',
                    dataIndex: 'contacts',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{header: '经办人电话',
                    dataIndex: 'phone',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{header: '传真号码',
                    dataIndex: 'fax',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                }, {header: '邮政编码',
                    dataIndex: 'postcode',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },  {header: '单位地址',
                    dataIndex: 'address',
                    sortable: true,
                    width: 240,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{header: '单位码',
                    dataIndex: 'treeId',
                    sortable: true,
                    hidden:true,
                     // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{header: '单位图标',
                    dataIndex: 'glyph',
                    sortable: true,
                    hidden:true,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{header: 'id',
                    dataIndex: 'id',
                    sortable: true,
                    hidden:true,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                }
            ];
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: 'DepartmentStore',
            displayInfo: true,
            id: 'bbarDep',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            //dock: 'top',
            height: 42,
            items: [
                '-', '每页数据', {
                    xtype: 'combobox',
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender: true,
                    mode: 'local',
                    width: 80,
                    store: [2,5, 10, 15, 20, 25, 30, 50, 75, 100, 200, 300, 500],
                    enableKeyEvents: true,
                    editable: true,
                    value: 25,
                    listeners: {
                        beforerender:function(combo){
                           // var panelStore = (Ext.getCmp('departmentgrid')).getStore();
                            //panelStore.sort('treeId', 'ASC'); //panelStore.sorters=null不排序
                           // combo.setValue(panelStore.pageSize)//初始化pageSize
                        },
                        change: function (combo) {
                            pageSize=combo.getValue();
                            if (pageSize == null) {
                                pageSize = 25
                            }//不选则每页25行数据
                            var panelStore = (Ext.getCmp('departmentgrid')).getStore();
                            panelStore.pageSize = pageSize;
                            //panelStore.sort('treeId', 'ASC'); //panelStore.sorters=null不排序
                            panelStore.proxy.api.read = "/appMis/department/readDepartment?dep_tree_id=" + currentTreeNode.substr(1)
                            //Ext.getCmp('departmentgrid').refreshData;
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});

