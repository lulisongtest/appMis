
Ext.define('appMis.view.requestmap.RequestmapAdd' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.requestmapadd',
    width:450,
    title : '增加requestmap信息',
    layout: 'fit',
    autoShow: true,

    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                items: [
                    {
                        xtype: 'textfield',
                        name : 'url',
                        fieldLabel: '可访问的Url：',
                        emptyText: '请输入url',
                        allowBlank:false,
                        labelStyle:'padding:10px',
                       // editable:false,
                        blankText:"url不能为空，请输入url",
                        labelWidth: 190,
                        labelAlign: 'right',
                        width:420
                    },
                    {
                        xtype: 'textfield',
                        name : 'configAttribute',
                        fieldLabel: '可访问Url的用户角色列表：',
                        emptyText: 'permitAll',
                        allowBlank:false,
                        labelStyle:'padding:10px',
                        editable:false,
                        blankText:"用户角色列表不能为空，请输入用户角色列表",
                        labelWidth: 190,
                        labelAlign: 'right',
                        width:420
                    },
                    {
                        xtype: 'textfield',
                        name : 'chineseUrl',
                        fieldLabel: '可访问的Url中文描述：',
                        emptyText: '请输入Url中文描述',
                        value:"Url中文描述",
                        allowBlank:false,
                        labelStyle:'padding:10px',
                       // editable:false,
                        blankText:"Url中文描述不能为空，请输入Url中文描述",
                        labelWidth: 190,
                        labelAlign: 'right',
                        width:420
                    },
                    {
                        xtype: 'combobox',
                        fieldLabel: '可访问Url的中文用户角色列表：',
                        triggerAction:'all',
                        name : 'roleList',
                        multiSelect:true,
                        id:'requestmaproleListadd',
                        emptyText: '普通用户',
                        value:'普通用户',
                        validateBlank: true,
                        displayField: 'chineseAuthority',
                        queryMode: 'remote',//会实时发ajax请求去load数据，这时默认会出现loadmask
                        store: 'RoleStore1',
                        valueField: 'chineseAuthority',
                        labelWidth: 190,
                        labelAlign: 'right',
                        width:420,
                        listeners:{
                            focus:function(){
                                if(Ext.getCmp("requestmaproleListadd").getValue()!=null){
                                // ""+是强制转换为字符串后再转换为数组，方便选择或取消选择!!
                                    Ext.getCmp("requestmaproleListadd").setValue((""+Ext.getCmp("requestmaproleListadd").getValue()).split(","))
                                    setENTERflag(1)
                                }
                            },
                            change:function(){
                                setENTERflag(0)
                                if((Ext.getCmp("requestmaproleListadd").getValue()==null)||(Ext.getCmp("requestmaproleListadd").getValue()==""))Ext.getCmp("requestmaproleListadd").setValue("普通用户")
                            },
                            blur:function(){
                                if((Ext.getCmp("requestmaproleListadd").getValue()==null)||(Ext.getCmp("requestmaproleListadd").getValue()==""))Ext.getCmp("requestmaproleListadd").setValue("普通用户")
                                if (ENTERflag==0){//回车则失去焦点，防止再次调用Blur
                                    //alert('修改数据后必须敲回车，方可正常保存数据！！！ ')
                                    setENTERflag(1)
                                }
                            },
                            specialkey: function(field,e){//注意事项：多选完成后鼠标应离开下拉列表的项，然后敲回车键的事件
                                if((Ext.getCmp("requestmaproleListadd").getValue()==null)||(Ext.getCmp("requestmaproleListadd").getValue()==""))Ext.getCmp("requestmaproleListadd").setValue("普通用户")
                                if (e.getKey()==Ext.EventObject.ENTER){
                                    alert("请收起下拉列表后再敲回车完成选择。或用鼠标点击下拉列表以外区域完成选择！！！")
                                    setENTERflag(1)
                                }else{
                                    alert("请在下拉列表中选择或取消选择！！！")
                                }
                            }
                        }
                    },
                    {
                        xtype: 'textfield',
                        name : 'treeId',
                        fieldLabel: '树结点号：',
                        emptyText: '0',
                        value:0,
                        allowBlank:true,
                        labelStyle:'padding:10px',
                        //editable:false,
                        blankText:"请输入树结点号",
                        labelWidth: 190,
                        labelAlign: 'right',
                        width:420
                    },
                    {
                        xtype: 'textfield',
                        name : 'glyph',
                        fieldLabel: '小图标号：',
                        emptyText: '请输入小图标号',
                        allowBlank:true,
                        labelStyle:'padding:10px',
                        editable:false,
                        blankText:"请输入树结点号",
                        labelWidth: 190,
                        labelAlign: 'right',
                        width:420
                    }
                ]
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

