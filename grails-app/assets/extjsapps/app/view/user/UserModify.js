
Ext.define('appMis.view.user.UserModify' ,{
    extend: 'Ext.panel.Panel',
    alias : 'widget.usermodify',
    requires: [

    ],
    width:'100%',
    height:'95%',
    title : '修改用户帐号信息',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    header: false,
    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                bodyPadding: 1,
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
                            readOnly:true,
                            labelWidth:80,
                            labelStyle:'padding:10px',
                            allowBlank:false,
                            validateBlank: true,
                            //minLength:3,
                           // maxLength:50,
                            emptyText: '请输入用户名',
                            fieldLabel: '用户名'
                        },
                        /*{
                            xtype: 'textfield',
                            name : 'password',
                            id:'password',
                            fieldLabel: '密码',
                            hidden:true,
                            labelWidth:80,
                            width:380,
                            labelStyle:'padding-left:10px',
                            inputType:'password'
                        },*/
                        {
                            xtype: 'button',
                            text: '修改密码',
                            width:100,
                            //glyph:'xf0c7@FontAwesome',
                            icon: 'tupian/pencil.png',
                            handler: function (button, e) {
                                view1 = Ext.create('appMis.view.user.ModifyPassword')
                            }
                        },
                        {
                            xtype: 'textfield',
                            name : 'truename',
                            readOnly:true,
                            labelWidth:80,
                            width:380,
                            labelStyle:'padding:10px',
                            emptyText: '请输入真实姓名',
                           // minLength:3,
                           // maxLength:20,
                            allowBlank:false,
                            fieldLabel: '真实姓名'
                        },
                        {
                            xtype: 'combobox',
                            fieldLabel: '部门名称',
                            editable :false,
                            readOnly:true,
                            labelWidth:80,
                            width:480,
                            labelStyle:'padding:10px',
                            name : 'department'//,
                            // mode: 'remote',//指定数据来源为远程
                            //   hiddenName:'department',//重要属性，后台获取值时需要
                            // typeAhead: true,
                            //mode: 'remote',//指定数据来源为远程
                            //  triggerAction: 'all',
                          //  validateBlank: true,
                          //  queryMode: 'remote',
                         //   displayField: 'department',
                         //   store: 'DepartmentStore1',
                         //   valueField: 'department',
                        //    listeners: {
                        //   }
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
                            maxLength:40,
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
                            readOnly:true,
                            editable :false,
                            labelWidth:80,
                            labelStyle:'padding-left:10px'//,
                           // validateBlank: true,
                           // displayField: 'chineseAuthority',
                            //queryMode: 'remote',
                           // store: 'RoleStore',
                          //  valueField: 'chineseAuthority',
                          //  listeners: {
                         //  }
                        },
                        {
                            xtype: 'checkbox',
                            name : "enabled",
                            hidden:true,
                            labelWidth:100,
                            fieldLabel: '帐号是否激活'
                        },
                        {
                            xtype: 'checkbox',
                            name : "accountExpired",
                            hidden:true,
                            labelWidth:100,
                            fieldLabel: '帐号是否过期'
                        },
                        {
                            xtype: 'checkbox',
                            name : "accountLocked",
                            hidden:true,
                            labelWidth:100,
                            fieldLabel: '帐号是否锁定'
                        },
                        {
                            xtype: 'checkbox',
                            name : "passwordExpired",
                            hidden:true,
                            labelWidth:100,
                            fieldLabel: '密码是否过期'
                        }
                    ]

                },
                    {
                        xtype: 'button',
                        text: '保存',
                        glyph:'xf0c7@FontAwesome',
                        action: 'userModifySave'
                    },
                    {
                        xtype: 'button',
                        text: '关闭',
                        glyph:'xf00d@FontAwesome',
                        style : 'margin-left:10px',//离左边一个按钮的距离多加100px
                        //scope: this, //handler: this.close
                        action: 'userModifyClose'


                    }
                ]
            }
        ];
        this.callParent(arguments);

    }
});

