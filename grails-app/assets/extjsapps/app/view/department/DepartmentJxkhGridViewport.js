var ENTERflag = 1
function setENTERflag(x) {//回车则失去焦点，防止再次调用Blur
    ENTERflag = x
    return
}
Ext.define('appMis.view.department.DepartmentJxkhGridViewport', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.departmentJxkhgridviewport',
    disableSelection: false,
    loadMask: true,
    columnLines: true,
    height: 110,
    stateful: true,
   // stateId: 'stateGrid',
    viewConfig: {
        margins: '0 0 0 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function () {
         this.store = 'DepartmentJxkhStore';
        //this.store =Ext.create('appMis.store.DepartmentJxkhStore')//重复创建时，store中的配置全部重新初始化,不受前次创建的store相互影响
        this.selType = 'cellmodel';//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })]
       this.selModel = Ext.create('Ext.selection.CheckboxModel'),//有复选框
       this.columns = [
               {
                    header: '年度',
                    dataIndex: 'nd',
                    width: 120,
                    // flex: 1,
                    sortable: true,
                    //locked:true,
                    editor: {
                        xtype: 'textfield'
                    }
                }, {header: '核定绩效工资总量',
                    dataIndex: 'hdjxgzzl',
                    sortable: true,
                    width: 150,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                }, {
                    header: '按比例核定的奖励性绩效工资（30%,40%）',
                    dataIndex: 'ablhddjlxjxgz',
                    width: 290,
                    // flex: 1,
                    sortable: true,
                    editor: {
                        xtype: 'textfield'
                    }
                }, {
                    header: '绩效工资核增量',
                    dataIndex: 'jxgzhzl',
                    width: 150,
                    sortable: true,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{
                    header: '纳入绩效的活工资',
                    dataIndex: 'nrjxdhgz',
                    sortable: true,
                    width: 150,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{
                    header: '年终一次性奖金',
                    dataIndex: 'nzycxjj',
                    sortable: true,
                    width: 150,
                    // flex: 1,
                    editor: {
                        xtype: 'textfield'
                    }
                },{header: '单位码',
                    dataIndex: 'treeId',
                   // sortable: true,
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

       this.callParent(arguments);
    }
});

