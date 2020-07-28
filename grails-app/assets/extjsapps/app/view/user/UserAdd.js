Ext.define('appMis.view.user.UserAdd' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.useradd',
    width:750,
    height:'100%',
    title : '添加用户信息',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                bodyPadding: 0,
                items:[{
                    xtype:'fieldset',
                    title:'主要信息',
                    defaults: {
                        labelSeparator: "：",
                        labelWidth: 65,
                        border:true,
                        width: 275
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            name : 'username',
                            labelWidth:80,
                            labelStyle:'padding:10px',
                            allowBlank:false,
                            validateBlank: true,
                            minLength:2,
                            maxLength:50,
                            emptyText: '请输入用户名',
                            fieldLabel: '用户名'
                        },
                        {
                            xtype: 'textfield',
                            name : 'password',
                            fieldLabel: '密码',
                            labelWidth:80,
                            labelStyle:'padding-left:10px',
                            inputType:'password'
                        },
                        {
                            xtype: 'textfield',
                            name : 'truename',
                            labelWidth:80,
                            width:380,
                            labelStyle:'padding:10px',
                            emptyText: '请输入真实姓名',
                            minLength:2,
                            maxLength:20,
                            allowBlank:false,
                            fieldLabel: '真实姓名'
                        },
                        {
                            xtype: 'textfield',
                            name : 'treeId',
                            id:'addtreeId',
                            hidden:true
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: '部门名称',
                            name : 'department',
                            id:'addDepartment',
                            labelWidth:80,
                            width:480,
                            labelStyle:'padding:10px',
                            emptyText: '请选择部门名称',
                            validateBlank: true,
                            editable: false,
                            value:"",
                            //displayField: 'department',
                           // queryMode: 'remote',
                          // store: 'DepartmentQueryStore',
                           // valueField: 'department',
                            listeners: {
                                focus: function () {
                                    view1 =Ext.create('appMis.view.user.SelectDepartment',{id:"addSelectDepartment"});
                                    document.getElementById('selectDepartment').style.setPropertyValue('z-index',19000);
                                }
                            }
                        }
                    ]
                },{
                    xtype:'fieldset',
                    title:'附加信息',
                    titleCollapse:true ,
                    collapsible:true,
                    bodyPadding: 1,
                    defaults: {
                        labelSeparator: "：",
                        labelWidth: 65,
                        width: 275
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            name : 'email',
                            labelWidth:80,
                            width:480,
                            labelStyle:'padding-left:10px',
                            fieldLabel: '电子邮件'

                        },
                        {
                            xtype: 'textfield',
                            name : 'phone',
                            labelWidth:80,
                            width:380,
                            minLength:6,
                            maxLength:12,
                            labelStyle:'padding:10px',
                            allowBlank:false,
                            blankText:"联系电话不能为空，请输入联系电话",
                            emptyText: '请输入联系电话',
                            fieldLabel: '联系电话'
                        },
                        {
                            xtype: 'combobox',
                            name : 'chineseAuthority',
                            fieldLabel: '所属权限',
                            editable :false,
                            labelWidth:80,
                            labelStyle:'padding-left:10px',
                            validateBlank: true,
                            displayField: 'chineseAuthority',
                            queryMode: 'remote',
                            store: 'RoleQueryStore',
                            valueField: 'chineseAuthority',
                            listeners: {
                                /*  expand:function(){
                                 if(i==1){
                                 this.collapse();
                                 i++
                                 Ext.getCmp("lulisong").focus()
                                 }
                                 }*/
                            }

                        }]},

                        {
                            xtype:'fieldset',
                            title:'帐户控制信息',
                            layout: 'column',
                            defaults: {
                                labelSeparator: "：",
                               // labelWidth: 65,
                                border:true,
                                //width: 275
                            },
                            items: [

                        {
                            xtype: 'checkbox',
                            name : "enabled",
                            columnWidth: 0.25,
                            labelWidth:100,
                            fieldLabel: '帐号是否激活'
                        },
                        {
                            xtype: 'checkbox',
                            name : "accountExpired",
                            columnWidth: 0.25,
                            labelWidth:100,
                            fieldLabel: '帐号是否过期'
                        },
                        {
                            xtype: 'checkbox',
                            name : "accountLocked",
                            columnWidth: 0.25,
                            labelWidth:100,
                            fieldLabel: '帐号是否锁定'
                        },
                        {
                            xtype: 'checkbox',
                            name : "passwordExpired",
                            columnWidth: 0.25,
                            labelWidth:100,
                            fieldLabel: '密码是否过期'
                        }
                    ]
                }]
            }
        ];
        this.buttons = [
            {
                text: '保存',
                glyph:'xf0c7@FontAwesome',
                action: 'save'
            },
            {
                text: '取消',
                glyph:'xf00d@FontAwesome',
                scope: this,
                handler: this.close
            }
        ];
        this.callParent(arguments);
    }
});

