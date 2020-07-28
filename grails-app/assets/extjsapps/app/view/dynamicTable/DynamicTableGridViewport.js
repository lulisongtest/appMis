var ENTERflag = 1
function setENTERflag(x) {//回车则失去焦点，防止再次调用Blur
    ENTERflag = x
    return
}
Ext.define('fieldType1Model', {
    extend: 'Ext.data.Model',
    fields: [
        {type: 'string', name: 'fieldType1'}
    ]
});
// The data for all states
var fieldType1Data = [
    {"fieldType1": "公式"},
    {"fieldType1": "字符"},
    {"fieldType1": "整数"},
    {"fieldType1": "浮点数"},
    {"fieldType1": "布尔"},
    {"fieldType1": "日期"},
    {"fieldType1": "字节流"}
];

// The data store holding the states; shared by each of the ComboBox examples below
var fieldType1Store = Ext.create('Ext.data.Store', {
    model: 'fieldType1Model',
    data: fieldType1Data
});

Ext.define('appMis.view.dynamicTable.DynamicTableGridViewport', {
    extend: 'Ext.grid.Panel',
    disableSelection: false,
    columnLines: true,
    stateful: true,
    alias: 'widget.dynamictablegridviewport',
    //stateId: 'stateGriddynamictablegridviewport',
    viewConfig: {
        margins: '0 0 0 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function () {
        this.store = 'DynamicTableStore';
        this.selType = 'cellmodel'//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })]
        //this.selModel = Ext.create('Ext.selection.CheckboxModel', {mode: "SINGLE"}),//"SINGLE"/"SIMPLE"/"MULTI"有复选框
        this.selModel = Ext.create('Ext.selection.CheckboxModel'),//有复选框
            this.columns = [
                Ext.create('Ext.grid.RowNumberer', {
                    text: '序号',
                    width: 70
                }),
                {
                    header: '数据表中文名称',
                    dataIndex: 'tableName',
                    width: 260,
                    editor: {
                        xtype: 'combobox',
                        id: 'tableName',
                        name: 'tableName',
                        emptyText: '请选择数据表名称',
                        validateBlank: true,
                        displayField: 'tableName',
                        queryMode: 'remote',
                        store: 'DynamicTableQueryStore',
                        valueField: 'tableName',
                        listeners: {
                            specialkey: function (field, e) {//敲回车键的事件
                                this.collapse()//回车后首先关闭下拉框架,防止下拉框中的默认值被选中。
                            }
                        }
                    }
                },
                {
                    header: '数据表名称',
                    dataIndex: 'tableNameId',
                    width: 260,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },
                {
                    header: '字段中文名称',
                    dataIndex: 'fieldName',
                    width: 260,
                    editor: {
                        xtype: 'textfield',
                        listeners: {
                        }
                    }
                },
                {
                    header: '字段名称',
                    dataIndex: 'fieldNameId',
                    width: 260,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },
                {
                    header: '字段类型',
                    dataIndex: 'fieldType',
                    width: 160,
                    editor: {
                        xtype: 'combobox',
                        emptyText: '数据类型',
                        validateBlank: true,
                        displayField: 'fieldType1',
                        valueField: 'fieldType1',
                        forceSelection: true,//设置必须从下拉框中选择一个值
                        queryMode: 'local',
                        store: fieldType1Store,
                        listeners: {
                        }
                    }
                },
                {
                    header: '字段长度',
                    dataIndex: 'fieldLength',
                    width: 160,
                    editor: {
                        xtype: 'textfield',
                        listeners: {

                        }
                    }
                }
            ];

        this.bbar = Ext.create('Ext.PagingToolbar', {
            store: 'DynamicTableStore',
            displayInfo: true,
            id: 'rbbardynamicTable',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            //dock: 'top',
            //height: 39,
            items: [
                "-", "每页数据", {
                    xtype: 'combobox',
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender: true,
                    mode: 'local',
                    width: 80,
                    store: [5, 10, 15, 20, 25, 30, 50, 75, 100, 200, 300, 500],
                    enableKeyEvents: true,
                    editable: true,
                    value: 25,
                    listeners: {
                        beforerender: function (combo) {
                            var panelStore = (Ext.getCmp('dynamictablegrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/dynamicTable/readDynamicTable?tableNameId=" + Ext.getCmp('dynamicTableName').getValue();
                            combo.setValue(panelStore.pageSize)//初始化pageSize
                        },
                        change: function (combo) {
                            pageSize=combo.getValue();
                            if (pageSize == null) {
                                pageSize = 25
                            }//不选则每页25行数据
                            var panelStore = (Ext.getCmp("dynamictablegrid")).getStore();
                            panelStore.proxy.api.read = "/appMis/dynamicTable/readDynamicTable?tableNameId=" + Ext.getCmp('dynamicTableName').getValue();
                            panelStore.pageSize = pageSize;
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});

