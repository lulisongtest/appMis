Ext.define('appMis.view.role.RoleViewport' ,{
    extend: 'Ext.panel.Panel',
    alias : 'widget.roleviewport',
    layout:{
        type: 'vbox',
        pack: 'start',
        align: 'stretch'
    },
    defaults: {
        autoScroll: true,
        bodyPadding: 0,
        border: 0,
        padding: 0
    },
    width: '100%',
    items: [
        {
            xtype: 'toolbar',
            border:0,
            height:40,
            padding:0,
           items: [{
                text : '新增',
               // glyph:'xf016@FontAwesome',
                icon:'tupian/edit_add.png',
                action: 'add'
            },{
                text: '增加行',
                //glyph:'xf022@FontAwesome',
                icon:'tupian/edit_remove.png',
                action: 'addRow'
            },{
                text : '修改',
               // glyph:'xf044@FontAwesome',
                icon:'tupian/pencil.png',
                action: 'modify'
            },{
                text : '删除',
                //glyph:'xf014@FontAwesome',
                icon:'tupian/cancel.png',
                action: 'delete'
            },{
                    xtype: 'combobox',
                    id:'queryAuthority1',
                    fieldLabel: '中文角色',
                    labelAlign: 'right',
                    name : 'chineseAuthority',
                    labelWidth: 60,
                    emptyText: '所有中文角色',
                   // value:'所有中文角色',//在IE中第一次进入就下拉
                    validateBlank: true,
                    displayField: 'chineseAuthority',
                    queryMode: 'remote',//会实时发ajax请求去load数据，这时默认会出现loadmask
                    loadMask:true,
                   // store: 'RoleStore1',
                    valueField: 'chineseAuthority',
                    listeners: {
                        change: function(combo) {
                            var newvalue = combo.getValue();
                            if((newvalue==null)||(newvalue=='全部')){newvalue="all"}//不选单位则是全部单位显示
                            var panelStore = (Ext.getCmp("rolegrid")).getStore();
                            panelStore.proxy.api.read="/appMis/role/listAsJsonPage2?newvalue="+newvalue;//解决IE的URL传中文参数乱码问题
                            panelStore.loadPage(1);
                        },
                        expand: function(combo) {
                           Ext.getCmp("queryAuthority1").getStore().reload()//当展开下拉列表时，重新加载下拉列表的数据库数据
                        }
                    }
                }
            ]
        },
        {
            xtype: 'panel',
            flex: 1,
            layout:{
                type: 'vbox',
                pack: 'start',
                align: 'stretch'
            },
            items: [
             {
               xtype: 'rolegridviewport',
               id:'rolegrid',
               flex: 1,
               listeners: {
                beforerender:function(){//初始化时，显示所有数据
                    (Ext.getCmp("rolegrid")).getStore().proxy.api.read="/appMis/role/readRole";
                    (Ext.getCmp("rolegrid")).getStore().loadPage(1)
                }
             }
         }]
}]
});