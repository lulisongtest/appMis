
Ext.define('appMis.view.student.StudentGridViewport', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.studentgridviewport',
    disableSelection: false,
    loadMask: true,
    columnLines: true,
    stateful: false,
    viewConfig: {
        margins: '0 0 0 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function () {
        this.store = 'StudentStore';
        this.selType = 'cellmodel';//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 2
        })];
        this.selModel = Ext.create('Ext.selection.CheckboxModel', {mode: "SINGLE"});//"SINGLE"/"SIMPLE"/"MULTI"有复选框
            this.columns = [
                Ext.create('Ext.grid.RowNumberer', {
                    text: '序号',
                    locked:true,
                    width: 70
                }),
               {
                    header: '用户名',
                    titleAlign:'center',//titleAlign :  'left' / 'center' / 'right'
                    dataIndex: 'username',
                    width: 130,
                    locked:true
                }, {
                    header: '真实姓名',
                    titleAlign:'center',//titleAlign :  'left' / 'center' / 'right'
                    dataIndex: 'truename',
                    sortable: true,
                    width: 160,
                    locked:true,
                },
                {
                    header: '电子邮件',
                    dataIndex: 'email',
                    sortable: true,
                    width: 160,
                    editor: {
                        xtype: 'textfield',
                   }
                },
                {
                    header: '班级',
                    dataIndex: 'department',
                    sortable: true,
                    width: 160,
                    editor: {
                        xtype: 'textfield',
                    }
                },
                {
                    header: '专业',
                    dataIndex: 'major',
                    sortable: true,
                    width: 160,
                    editor: {
                        xtype: 'textfield',
                    }
                },
                {
                    header: '院系',
                    dataIndex: 'college',
                    sortable: true,
                    width: 160,
                    editor: {
                        xtype: 'textfield',
                    }
                },
                {
                    header: '本人联系电话',
                    dataIndex: 'phone',
                    sortable: true,
                    width: 160,
                    editor: {
                        xtype: 'textfield',
                    }
                },
                {
                    header: '家长联系电话',
                    dataIndex: 'homephone',
                    sortable: true,
                    width: 160,
                    editor: {
                        xtype: 'textfield',
                    }
                },
                {
                    header: '身份证号',
                    titleAlign:'center',//titleAlign :  'left' / 'center' / 'right'
                    dataIndex: 'idNumber',
                    sortable: true,
                    width: 160,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield',
                    }
                }, {
                    header: '出生日期',
                    titleAlign:'center',//titleAlign :  'left' / 'center' / 'right'
                    dataIndex: 'birthDate',
                    width: 150,
                    sortable: true,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        name: 'birthDate',
                    }
                }, {
                    header: '性别',
                    dataIndex: 'sex',
                    sortable: true,
                    width: 50,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField: 'name',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['name'],
                            data: [{'name': '男'}, {'name': '女'}]
                        }),

                    }
                }, {
                    header: '族别',
                    dataIndex: 'race',
                    sortable: true,
                    width: 120,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField: 'name',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['name'],
                            data: [{'name': '汉族'}, {'name': '回族'}, {'name': '维吾尔族'}, {'name': '哈萨克族'}, {'name': '俄罗斯族'}, {'name': '柯尔克孜族'},
                                {'name': '乌兹别克族'}, {'name': '塔塔尔族'}, {'name': '蒙古族'}, {'name': '锡伯族'}, {'name': '塔吉克族'}, {'name': '达擀尔族'}, {'name': '羌族'},
                                {'name': '鄂温克族'}, {'name': '藏族'}, {'name': '土家族'}, {'name': '土族'}, {'name': '壮族'}, {'name': '苗族'}, {'name': '彝族'}, {'name': '傣族'}, {'name': '东乡族'},
                                {'name': '纳西族'}, {'name': '景颇族'}, {'name': '满族'}, {'name': '白族'}, {'name': '高山族'}, {'name': '佤族'}, {'name': '傈僳族'}, {'name': '哈尼族'}, {'name': '黎族'},
                                {'name': '布依族'}, {'name': '赫哲族'}, {'name': '瑶族'}, {'name': '朝鲜族'}, {'name': '侗族'}, {'name': '畲族'}, {'name': '鄂伦春族'}, {'name': '仫佬族'}, {'name': '布朗族'},
                                {'name': '怒族'}, {'name': '普米族'}, {'name': '毛南族'}, {'name': '撒拉族'}, {'name': '仡佬族'}, {'name': '阿昌族'}, {'name': '拉祜族'}, {'name': '水族'}, {'name': '德昂族'},
                                {'name': '京族'}, {'name': '门巴族'}, {'name': '独龙族'}, {'name': '裕固族'}, {'name': '保安族'}, {'name': '珞巴族'}, {'name': '基诺族'}, {'name': '外籍人士'}, {'name': '其他'}]
                        }),

                    }
                }, {
                    header: '政治面貌',
                    titleAlign:'center',//titleAlign :  'left' / 'center' / 'right'
                    dataIndex: 'politicalStatus',
                    sortable: true,
                    width: 80,
                    // flex: 1,
                    editor: {
                        xtype: 'combobox',
                        displayField: 'name',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['name'],
                            data: [{'name': '中共党员'}, {'name': '中共预备党员'}, {'name': '共青团员'},
                                {'name': '民建会员'}, {'name': '无党派民主人士'}, {'name': '台盟盟员'},
                                {'name': '群众'}, {'name': '致工党党员'}, {'name': '民进会员'},
                                {'name': '农工党党员'}, {'name': '民盟盟员'}, {'name': '九三学社社员'},
                                {'name': '民革会员'}, {'name': '其它'}]
                        }),

                    }
                },{
                    header: '入学日期',
                    dataIndex: 'enrollDate',
                    width: 150,
                    sortable: true,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        format: 'Y-m-d',
                        name: 'enrollDate',
                    }
                },
                {header: '单位码',
                    dataIndex: 'treeId',
                    sortable: true,
                    hidden:true,
                    editor: {
                        xtype: 'textfield'
                    }
                },
                {header: 'id',
                    dataIndex: 'id',
                    sortable: true,
                    hidden:true
                },
                {header: '当前状态',
                    dataIndex: 'currentStatus',
                    sortable: true,
                    //hidden:true
                },
            ];
        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: 'StudentStore',
            displayInfo: true,
            id: 'bbarEmp',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            // height: 39,//80%,100%
            height: 39,
            items: [
                '-', '每页数据', {
                    xtype: 'combobox',
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender: true,
                    mode: 'local',
                    width: 80,
                    store: [1,  5, 10, 15, 20, 25, 30, 50, 75, 100, 200, 300, 500],
                    enableKeyEvents: true,
                    editable: true,
                    value: 25,
                    listeners: {
                        beforerender: function (combo) {
                        },
                        change: function (combo) {
                            pageSize=combo.getValue();
                            if (pageSize == null) {
                                pageSize = 25
                            }//不选则每页25行数据
                            var panelStore = (Ext.getCmp('studentgrid')).getStore();
                            panelStore.pageSize = pageSize;
                            panelStore.proxy.api.read = '/appMis/student/read?dep_tree_id=' + currentTreeNode.substr(1)+'&username='+Ext.getCmp('usernameStudent').getValue()+'&truename='+Ext.getCmp('truenameStudent').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});

