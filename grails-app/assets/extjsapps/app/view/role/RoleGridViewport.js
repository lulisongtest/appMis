var ENTERflag=1
function setENTERflag(x){//回车则失去焦点，防止再次调用Blur
    ENTERflag=x
    return
}
Ext.define('appMis.view.role.RoleGridViewport' ,{
    extend: 'Ext.grid.Panel',
    alias : 'widget.rolegridviewport',
    disableSelection: false,
    loadMask: true,
    columnLines: true,
    stateful: true,
   // stateId: 'stateGrid',
    viewConfig: {
        margins: '0 0 30 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function() {
        this.store = 'RoleStore';
        this.selType='cellmodel'//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins=[Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })]
        this.selModel=Ext.create('Ext.selection.CheckboxModel'),//有复选框
       // Ext.create('Go.form.field.DateTime');//放在这没有问题
        this.columns = [
                Ext.create('Ext.grid.RowNumberer',{
                    header:'序号',
                    width:80
                }),
                {header: '角色',
                    dataIndex: 'authority',
                    flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },
                {header: '中文角色',
                    dataIndex: 'chineseAuthority',
                    flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                }
            ];
        this.bbar=Ext.create('Ext.PagingToolbar', {
            store: 'RoleStore',
            displayInfo: true,
            id:'bbarRole',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            //dock: 'top',
            //height: 45,
            items:
                [
                    "-", "每页数据",{
                    xtype: 'combobox',
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender: true,
                    mode: 'local',
                    width: 70,
                    store:[5, 10, 15, 20, 25, 30, 50, 75, 100, 200, 300, 500],
                    enableKeyEvents: true,
                    editable: true,
                    value:25,
                    listeners: {
                        change: function(combo) {
                            pageSize=combo.getValue();
                            if (pageSize == null) {
                                pageSize = 25
                            }//不选则每页25行数据
                            var panelStore = (Ext.getCmp("rolegrid")).getStore();
                            panelStore.pageSize=pageSize;
                            panelStore.proxy.api.read="/appMis/role/readRole";
                            panelStore.loadPage(1);
                        }
                    }
                }
                ]
        });
        this.callParent(arguments);
    }
});


