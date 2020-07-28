

Ext.define('appMis.view.dynamicTable.DynamicTableViewport' , {
    extend: 'Ext.panel.Panel',
    alias: 'widget.dynamictableviewport',
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
    items:[
        {
                xtype: 'toolbar',
                height: 40,
                items: [
                    /*{
                        xtype: 'button',
                        text: '新增',
                        icon: 'tupian/edit_add.png',
                        action: 'add'
                    },*/
                    {
                        xtype: 'button',
                        text: '增加行',
                        icon: 'tupian/edit_remove.png',
                        action: 'addRow',
                        handler: function (button, e) {
                            Ext.getCmp('dynamicTableName').setValue("全部")
                        }
                    },
                    /*{
                        xtype: 'button',
                        text: '修改',
                        icon: 'tupian/pencil.png',
                        action: 'modify'
                    },*/
                    {
                        xtype: 'button',
                        text: '删除',
                        icon: 'tupian/cancel.png',
                        action: 'delete'
                    },
                    {
                        xtype: 'combobox',
                        id: 'dynamicTableName',
                        fieldLabel: '数据表名',
                        labelAlign: 'right',
                        name: 'tableName',
                        labelWidth: 70,
                        width: 260,
                        emptyText: '请输入数据表名',
                        validateBlank: true,
                        displayField: 'tableName',
                        queryMode: 'remote',//会实时发ajax请求去load数据，这时默认会出现loadmask
                        store: 'DynamicTableQueryStore',
                        valueField: 'tableNameId',
                        listeners: {
                            change: function (combo) {
                                var panelStore = (Ext.getCmp("dynamictablegrid")).getStore();
                                panelStore.proxy.api.read = "/appMis/dynamicTable/readDynamicTable?tableNameId=" + Ext.getCmp('dynamicTableName').getValue();
                                panelStore.loadPage(1);
                            },
                            expand: function (combo) {
                                Ext.getCmp("dynamicTableName").getStore().reload()//当展开下拉列表时，重新加载下拉列表的数据库数据
                            }
                        }
                    } ,
                    {
                        xtype: 'button',
                        width: 100,
                        text: '生成域',
                        glyph: 'xf016@FontAwesome',
                        action: 'createDomain'
                    },
                    {
                        xtype: 'button',
                       width: 120,
                        text: '生成控制器',
                        glyph: 'xf016@FontAwesome',
                        action: 'createController'
                    },
                    {
                        xtype: 'button',
                        width: 130,
                        text: '生成全部视图',
                        glyph: 'xf016@FontAwesome',
                        action: 'completeView'
                    },
                    {
                        xtype: 'button',
                        width: 190,
                        text: '删除已生成的全部信息',
                        glyph: 'xf014@FontAwesome',
                        action: 'deleteAll'
                    }
                    ]
            },
        {
            xtype: 'toolbar',
            height: 40,
            items: [
                 {
                    xtype: 'button',
                    width: 240,
                    text: '删除系统数据表中当前所选数据',
                    glyph: 'xf014@FontAwesome',
                    action: 'batchDelete'
                },
                {
                    xtype: 'button',
                    width: 170,
                    text: '导出当前数据到Excel',
                    icon: 'tupian/exportToExcel.png',
                    action: 'exportToExcel'
                },
                {
                    xtype: 'button',
                    width: 170,
                    text: '从Excel导入所有数据',
                    icon: 'tupian/exportToExcel.png',
                    action: 'importFromExcel'
                },
                {
                    xtype: 'form',
                    height:40,
                    baseCls: 'x-plain',
                    url: "tmp",//上传服务器的地址
                    fileUpload: true,
                    id:'dynamicTableexcelfilePath',//放在此可用Ext.getCmp('dynamicTableexcelfilePath').getForm().findField('dynamicTableexcelfilePath'))获取name: 'dynamicTableexcelfilePath',的值。
                    defaultType: 'textfield',
                    items: {
                        xtype: 'fileuploadfield',
                        width: 260,
                        labelWidth: 0,
                        labelAlign: 'right',
                        fieldLabel: '',
                        name: 'dynamicTableexcelfilePath',
                        //id: 'dynamicTableexcelfilePath',//放在此，第二次进入时无法显示查找按钮
                        buttonText: '选择文件',
                        blankText: '文件名不能为空'
                    }
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
                    xtype: 'dynamictablegridviewport',
                    id:'dynamictablegrid',
                    flex: 1,
                    listeners: {
                          beforerender:function(){
                              var panelStore = (Ext.getCmp("dynamictablegrid")).getStore();
                              panelStore.proxy.api.read = "/appMis/dynamicTable/readDynamicTable?tableNameId=" + Ext.getCmp('dynamicTableName').getValue();
                              panelStore.pageSize = pageSize;
                              panelStore.loadPage(1);
                         }
                     }
                  }
            ]
        }
    ]
});