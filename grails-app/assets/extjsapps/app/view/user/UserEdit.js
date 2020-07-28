
Ext.define('appMis.view.user.UserEdit' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.useredit',
    width:750,
    height:'100%',
    title : '修改用户帐号信息',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
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
                             // id:'lulisong',
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
                             id:'edittreeId',
                             hidden:true
                         },
                         {
                             xtype: 'textfield',
                             fieldLabel: '部门名称',
                             name : 'department',
                             id:'editDepartment',
                             labelWidth:80,
                             width:480,
                             labelStyle:'padding:10px',
                             emptyText: '请选择部门名称',
                             validateBlank: true,
                             editable: false,
                             value:"",
                             //displayField: 'department',
                             // queryMode: 'remote',
                             //store: 'DepartmentStore1',
                             // valueField: 'department',
                             listeners: {
                                 focus: function () {
                                     //view = Ext.widget('selectDepartment');//view =Ext.create('appMis.view.user.SelectDepartment')//奇怪！！！随意修改以下两行或其中一行的数值，就可实现modal
                                     view1 =Ext.create('appMis.view.user.SelectDepartment',{id:"editSelectDepartment"});
                                     //document.getElementById('systemSetting').style.setPropertyValue('z-index',0);
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
                             store: 'RoleStore',
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
                            // width: 275
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
        this.buttons=[
            {
                text: '第一',
                glyph:'xf0c7@FontAwesome',
                action: 'first'
            },
            {
                text: '向前',
                glyph:'xf0c7@FontAwesome',
                action: 'preview'
            },
            {
                text: '向后',
                glyph:'xf00d@FontAwesome',
                action: 'next'
            },
            {
                text: '最后',
                glyph:'xf00d@FontAwesome',
                action: 'last'
            },
            {
                text: '保存',
                glyph:'xf0c7@FontAwesome',
                action: 'save'
            },
            {
                text: '关闭',
                glyph:'xf00d@FontAwesome',
                action: 'close'
               // scope: this,
               // handler: this.close
            }
        ]
        this.callParent(arguments);

    }
});

