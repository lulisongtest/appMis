var ENTERflag=1
function setENTERflag(x){//回车则失去焦点，防止再次调用Blur
    ENTERflag=x
    return
}
Ext.define('appMis.view.requestmap.RequestmapGridViewport' ,{
    extend: 'Ext.grid.Panel',
    alias : 'widget.requestmapgridviewport',
    disableSelection: false,
    loadMask: true,
    columnLines: true,
    stateful: true,
    //stateId: 'stateGrid',
    viewConfig: {
        margins: '0 0 30 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function() {
        this.store = 'RequestmapStore';
        this.selType='cellmodel'//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins=[Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })]
        this.selModel=Ext.create('Ext.selection.CheckboxModel'),//有复选框
        this.columns = [
             Ext.create('Ext.grid.RowNumberer',{
                 text:'序号',
               //  width:40
             }),
            {header: '可访问的Url',
                dataIndex: 'url',
                //hidden:true,
               // flex: 1,
                editor: {
                    xtype: 'textfield',
                    listeners:{
                       select : function(combo, record,index){
                          isEdit = true;
                       }}
                }
            },
            {header: '可访问Url 的用户角色列表',
               dataIndex: 'configAttribute',
              // hidden:true,
             //  flex: 1,
               editor: {
                   xtype: 'textfield',
                   listeners:{
                      select : function(combo, record,index){
                      isEdit = true;
                   }}
               }
            },
             {header: '可访问的Url 中文描述',
                 dataIndex: 'chineseUrl',
                // flex: 1,
                 editor: {
                     xtype: 'textfield',
                     listeners:{
                         select : function(combo, record,index){
                             isEdit = true;
                         }}
                 }
             },
           {header: '可访问Url 的中文用户角色列表',
                dataIndex: 'roleList',//multiSelect
               // flex:1,
                editor: {
                    xtype: 'combobox',
                    multiSelect:true,
                    //forceSelection: true,//每次强行重新选择
                    id:'requestmaproleList',
                    emptyText: '普通用户',
                    value:'permitAll',
                    validateBlank: true,
                    name : 'chineseAuthority',
                    displayField: 'chineseAuthority',
                    queryMode: 'remote',//会实时发ajax请求去load数据，这时默认会出现loadmask
                    store: 'RoleStore2',
                    valueField: 'chineseAuthority',
                    listeners:{
                        focus:function(){
                            println("focus")
                            if(Ext.getCmp("requestmaproleList").getValue()!=null){
                                // ""+是强制转换为字符串后再转换为数组，方便选择或取消选择!!
                                Ext.getCmp("requestmaproleList").setValue((""+Ext.getCmp("requestmaproleList").getValue()).split(","))
                                setENTERflag(1)
                            }
                        },
                        change:function(){
                            setENTERflag(0)
                            if((Ext.getCmp("requestmaproleList").getValue()==null)||(Ext.getCmp("requestmaproleList").getValue()==""))Ext.getCmp("requestmaproleList").setValue("普通用户")
                        },
                        blur:function(){
                            if((Ext.getCmp("requestmaproleList").getValue()==null)||(Ext.getCmp("requestmaproleList").getValue()==""))Ext.getCmp("requestmaproleList").setValue("普通用户")
                            if (ENTERflag==0){//回车则失去焦点，防止再次调用Blur
                                //alert('修改数据后必须敲回车，方可正常保存数据！！！ ')
                                setENTERflag(1)
                            }
                        },
                        specialkey: function(field,e){//注意事项：多选完成后鼠标应离开下拉列表的项，然后敲回车键的事件
                            if((Ext.getCmp("requestmaproleList").getValue()==null)||(Ext.getCmp("requestmaproleList").getValue()==""))Ext.getCmp("requestmaproleList").setValue("普通用户")
                            if (e.getKey()==Ext.EventObject.ENTER){
                                alert("请收起下拉列表后再敲回车完成选择。或用鼠标点击下拉列表以外区域完成选择！！！")
                                setENTERflag(1)
                            }else{
                                alert("请在下拉列表中选择或取消选择！！！")
                            }
                        }
                    }
                }
           },
           {header: '树结点号',
                 dataIndex: 'treeId',
                // flex: 1,
                 editor: {
                     xtype: 'textfield',
                     listeners:{
                         select : function(combo, record,index){
                             isEdit = true;
                         }}
                 }
             },
            {header: '小图标号',
                dataIndex: 'glyph',
               // flex: 1,
                editor: {
                    xtype: 'textfield',
                    listeners:{
                        select : function(combo, record,index){
                            isEdit = true;
                        }}
                }
            }
        ];
        this.bbar=Ext.create('Ext.PagingToolbar', {
            store: 'RequestmapStore',
            displayInfo: true,
            id:'bbarRequestmap',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
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
                            var panelStore = (Ext.getCmp("requestmapgrid")).getStore();
                            panelStore.pageSize=pageSize;
                            panelStore.proxy.api.read="/appMis/requestmap/readRequestmap";
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});
