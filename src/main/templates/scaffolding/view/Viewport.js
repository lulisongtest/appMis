/*<%=new Date()%>陆立松自动创建*/
var DynamicTableStore${className} = Ext.create('Ext.data.Store', {
    fields: ['id','tableNameId','tableName','fieldNameId','fieldName','fieldType','fieldLength'],
    autoSync : true,//这样修改完一个单元格后会自动保存数据
    autoLoad:  true,//是否与上一句是一个功能！？
    proxy: {
        type: 'ajax',
        api: {
            read :'/appMis/dynamicTable/readDynamicTableSelect?tableNameId=${propertyName}'
        },
        noCache: false,
        actionMethods: {
            read   : 'POST'
        },
        reader: {
            type: 'json',
            rootProperty: 'dynamictables',
            totalProperty:"totalCount"
        },
        writer:{
            type:'json'
        }
    }
});

/*Ext.define('FileRecord', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'fileid', type: 'string'},
        {name: 'filename', type: 'string'},
        {name: 'filesize', type: 'string'},
        {name: 'fileprogress', type: 'string'}
    ]
});*/

Ext.define('appMis.view.${propertyName}.${className}Viewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.${propertyName}viewport',
    requires: [
        'appMis.view.${propertyName}.SelectExport${className}Item',
        'appMis.view.${propertyName}.SelectImport${className}Item'
    ],
    layout: {
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
            height: 32,
            defaults: {
                margins: '0 0 0 0',
                bodyPadding: 0,
                border: 0,
                padding: '0 0 0 0'
            },
            items: [{
                text: '新增',
                hidden: (loginUserRole != '管理员'),
                //hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/edit_add.png',
                action: 'add'
            }, {
                text: '增加行',
                hidden: (loginUserRole != '管理员'),
                //hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/edit_remove.png',
                action: 'addRow'
            }, {
                text: '修改',
                hidden: (loginUserRole != '管理员'),
                // hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/pencil.png',
                action: 'modify'
            }, {
                text: '删除',
                hidden: (loginUserRole != '管理员'),
                //hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/cancel.png',
                action: 'delete'
            }, {
                text: '删除全部',
                hidden: (loginUserRole != '管理员'),
                // hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/cancel.png',
                action: 'deleteAll'
            },
                {
                    text: '打印',
                    hidden: (loginUserRole != '管理员'),
                    //hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                    icon: 'printer/printer.png',
                    action: 'print'
                },
                {
                    xtype: 'textfield',
                    id: 'truename${className}',
                    labelStyle: 'padding-left:10px',
                    labelAlign: 'left',
                    width: 160,
                    labelWidth: 40,
                    fieldLabel: '姓名 ',
                    emptyText: '请输入查询姓名',
                    listeners: {
                        focus: function (combo) {
                            Ext.getCmp('username${className}').setValue("");
                        },
                        change: function (field, e) {
                            var panelStore = (Ext.getCmp('${propertyName}grid')).getStore();
                            panelStore.proxy.api.read = '/appMis/${propertyName}/read?dep_tree_id=' + currentTreeNode.substr(1)+'&username='+Ext.getCmp('username${className}').getValue()+'&truename='+Ext.getCmp('truename${className}').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'username${className}',
                    labelStyle: 'padding-left:10px',
                    labelAlign: 'left',
                    width: 220,
                    labelWidth: 60,
                    fieldLabel: '职工编号 ',
                    emptyText: '请输入查询职工编号',
                    listeners: {
                        focus: function (combo) {
                            Ext.getCmp('truename${className}').setValue("");
                        },
                        change: function (field, e) {
                            var panelStore = (Ext.getCmp('${propertyName}grid')).getStore();
                            panelStore.proxy.api.read = '/appMis/${propertyName}/read?dep_tree_id=' + currentTreeNode.substr(1)+'&username='+Ext.getCmp('username${className}').getValue()+'&truename='+Ext.getCmp('truename${className}').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        },
        {
            xtype: 'toolbar',
            height: 32,
            defaults: {
                margins: '0 0 0 0',
                bodyPadding: 0,
                border: 0,
                padding: '0 0 1 0'
            },
            items: [
                {
                    text: '数据导出',
                    // hidden:(loginUserRole!= '管理员'),
                    icon: 'tupian/exportToExcel.png',
                    //action: 'exportToExcel'
                    handler: function (button, e) {
                        view = Ext.create('appMis.view.${propertyName}.SelectExport${className}Item')
                    }
                },
                {
                    text: '数据导入',
                    hidden: (loginUserRole != '管理员')&&(loginUserRole != '单位管理员'),
                    icon: 'tupian/importFromExcel.png',
                    handler: function (button, e) {
                        if (Ext.getCmp('${propertyName}excelfilePath').getValue() == '') {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '请选择你要上传的文件',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 2500);
                            return;
                        }
                        var s=(""+Ext.getCmp('${propertyName}excelfilePath').getValue()).toLowerCase();
                        if(s.substring(s.length-4)!='xlsx'){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '文件类型不对，请重新选择你要上传的xlsx文件！',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 2500);
                            return;
                        }
                        view = Ext.create('appMis.view.${propertyName}.SelectImport${className}Item')
                    }
                },
                {
                    xtype: 'form',
                    hidden: (loginUserRole != '管理员'),
                    id: 'selectImport${className}ItemForm',
                    // hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                    height: 40,
                    // baseCls: 'x-plain',
                    url: "tmp",//上传服务器的地址
                    fileUpload: true,
                    //defaultType: 'textfield',
                    items: [{
                        xtype: 'fileuploadfield',
                        width: 180,
                        labelWidth: 0,
                        // labelAlign: 'right',
                        //fieldLabel: '选择文件',
                        name: '${propertyName}excelfilePath',
                        id: '${propertyName}excelfilePath',
                        buttonText: '选择文件',
                        blankText: '文件名不能为空',
                        listeners: {
                            change: function (combo) {
                            }
                        }
                    }]
                }
            ]
        },
        {
            xtype: 'panel',
            flex: 1,
            layout: {
                type: 'vbox',
                pack: 'start',
                align: 'stretch'
            },
            items: [
                {
                    xtype: '${propertyName}gridviewport',
                    id: '${propertyName}grid',
                    flex: 1,
                    listeners: {
                        beforerender: function () {
                            var panelStore = (Ext.getCmp('${propertyName}grid')).getStore();
                            panelStore.pageSize = pageSize;
                            panelStore.proxy.api.read = '/appMis/${propertyName}/read?dep_tree_id=' + currentTreeNode.substr(1)+'&username='+Ext.getCmp('username${className}').getValue()+'&truename='+Ext.getCmp('truename${className}').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                }]
        },
    ],
    initComponent: function () {
        this.callParent(arguments);
    }
});
