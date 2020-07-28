
Ext.define('appMis.view.user.UserGridViewport' ,{
    extend: 'Ext.grid.Panel',
    alias : 'widget.usergridviewport',
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
    initComponent: function() {
        this.store = 'UserStore';
        this.selType = 'cellmodel';//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })];
        this.selModel = Ext.create('Ext.selection.CheckboxModel');//有复选框
        this.columns = [
                Ext.create('Ext.grid.RowNumberer', {
                    text: '序号',
                    locked:true,
                    width: 70
                }),
            {header: '用户名',
                dataIndex: 'username',
                width: 130,
                locked:true,
                // flex: 1,
               // renderer : function(value, metadata) { metadata.tdAttr = 'data-qtip="' + value + '"';return value;},
                editor: {
                    xtype: 'textfield'
                }
            },
            {header: '真实姓名',
                dataIndex: 'truename',
                width: 120,
                locked:true,
                // flex: 1,
               // renderer : function(value, metadata) { metadata.tdAttr = 'data-qtip="' + value + '"'; return value; },
                editor: {
                    xtype: 'textfield'
                }
            },
            {header: '角色',
             dataIndex: 'chineseAuthority',
                width: 130,
                // flex: 1,
             // renderer : function(value, metadata) { metadata.tdAttr = 'data-qtip="' + value + '"'; return value;},
             editor: {
                xtype: 'combobox',
                name : 'chineseAuthority',
                emptyText: '请选择所属角色',
                validateBlank: true,
                editable :false,
                displayField: 'chineseAuthority',
                queryMode: 'remote',
                //store: 'RoleStore2',
                valueField: 'chineseAuthority'
             }
             },

                 {
                     header: '部门名称',
                     titleAlign:'center',
                     dataIndex: 'department',
                     width: 350,
                   //  renderer : function(value, metadata) { metadata.tdAttr = 'data-qtip="' + value + '"'; return value; },
                     editor: {
                         xtype: 'combobox',
                         emptyText: '请选择部门名称',
                         validateBlank: true,
                         editable: false,
                         displayField: 'department',
                         queryMode: 'remote',
                         //store: 'DepartmentStore1',
                         valueField: 'department',
                         listeners: {
                             focus: function () {
                                 view1 =Ext.create('appMis.view.user.SelectDepartment',{id:"gridSelectDepartment"});
                                 document.getElementById('systemSetting').style.setPropertyValue('z-index',0);
                             }
                         }
                     }
                 },
                 {header: '联系电话',
                     dataIndex: 'phone',
                     width: 150,
                     // flex: 1,
                     renderer : Ext.util.Format.monetaryRenderer,
                     editor: {
                         xtype: 'textfield'
                     }
                 },
            {header: '电子邮件',
                dataIndex: 'email',
                width: 200,
                // flex: 1,
                //renderer : function(value, metadata) {metadata.tdAttr = 'data-qtip="' + value + '"';return value; },
                editor: {
                    xtype: 'textfield'
                }
            },
            {header: '用户密码(加密)',
                dataIndex: 'password',
                resizeDisabled:true,
                width: 130,
                // flex: 1,
                editor: {
                    xtype: 'textfield',
                    inputType:'password'
                }
            },
           {header: '创建日期',
                dataIndex: 'regdate',
                width: 210,
                sortable: true,
                readOnly:true,
                renderer : Ext.util.Format.dateRenderer('Y年m月d日 H时i分s秒')
             },
            {header: '最后一次</br>登录时间',
                dataIndex: 'lastlogindate',
                titleAlign:'center',
                width: 210,
                sortable: true,
                readOnly:true,
               // renderer : Ext.util.Format.dateRenderer,
                renderer : Ext.util.Format.dateRenderer('Y年m月d日 H时i分s秒')
            },
            {xtype:"checkcolumn",header:"帐号是</br>否激活",width:70,dataIndex:"enabled"},
            {xtype:"checkcolumn",header:"帐号是</br>否过期",width:70,dataIndex:"accountExpired"},
            {xtype:"checkcolumn",header:"帐号是</br>否锁定",width:70,dataIndex:"accountLocked"},
            {xtype:"checkcolumn",header:"密码是</br>否过期",width:70,dataIndex:"passwordExpired"}
        ];
        this.bbar=Ext.create('Ext.PagingToolbar', {
            store: 'UserStore',
            displayInfo: true,
            id:'bbarUser',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
           // dock: 'top',
           // height: 42,
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
                            var panelStore = (Ext.getCmp("usergrid")).getStore();
                            panelStore.pageSize=pageSize;
                            var queryAuthority = Ext.getCmp("queryAuthority").getValue();
                            var queryDepartment = Ext.getCmp("queryDepartment").getValue();
                            var queryUsername = Ext.getCmp("queryTruename").getValue();
                            panelStore.proxy.api.read = "/appMis/user/readUser?queryAuthority=" + queryAuthority + "&queryDepartment=" + queryDepartment + "&queryUsername=" + queryUsername
                            panelStore.loadPage(1);
                        }
                    }
                }
                ]
        });
        this.callParent(arguments);
    }
});


