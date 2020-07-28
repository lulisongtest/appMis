
Ext.define('appMis.view.requestmap.RequestmapViewport' ,{
    extend: 'Ext.panel.Panel',
    alias : 'widget.requestmapviewport',
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
           height:40,
           items: [{
                text : '新增',
                // glyph:'xf016@FontAwesome',
                icon:'tupian/edit_add.png',
                action: 'add'
           },{
                text: '增加行',
                // glyph:'xf022@FontAwesome',
                icon:'tupian/edit_remove.png',
                action: 'addRow'
           },{
                text : '修改',
                //glyph:'xf044@FontAwesome',
                icon:'tupian/pencil.png',
                action: 'modify'
           },{
                text : '删除',
                // glyph:'xf014@FontAwesome',
                icon:'tupian/cancel.png',
                action: 'delete'
            }]
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
                    xtype: 'requestmapgridviewport',
                    id:'requestmapgrid',
                    flex: 1,
                    listeners: {
                        beforerender:function(){//初始化时，显示所有数据
                            (Ext.getCmp("requestmapgrid")).getStore().proxy.api.read="/appMis/requestmap/readRequestmap";
                            (Ext.getCmp("requestmapgrid")).getStore().loadPage(1)
                        }
                    }
                }]
        }]

});