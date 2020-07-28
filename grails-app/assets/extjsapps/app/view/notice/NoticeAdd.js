
Ext.define('appMis.view.notice.NoticeAdd' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.noticeadd',
    title : '增加通知通告信息表信息',
    width:1100,
    x:160,
    y: 90,
    //layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    initComponent: function() {
        this.items = [
           {
                xtype: 'form',
                height: 250,
                width:1050,
                layout: 'column',
                defaults: {
                    margins: '0 0 0 0',
                    padding:5
                },
                items: [{
                        xtype: 'textfield',
                        columnWidth:.25,
                        name : 'title',
                        fieldLabel:'标题',
                        readOnly: true,
                        labelWidth:60,
                        labelStyle:'padding-left:10px'
                    },
                    {
                        xtype: 'textfield',
                        columnWidth:.25,
                        hidden:true,
                        name : 'titleCode',
                        fieldLabel:'标题编码',
                        labelWidth:110,
                        labelStyle:'padding-left:10px'
                    },
                    {
                        xtype: 'combobox',
                        name: 'jb',
                        columnWidth:.25,
                        labelWidth: 90,
                        // width: 115,
                        fieldLabel: '级别',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入级别',
                        value: "未知",
                        blankText: '级别不能为空，请输入级别',
                        displayField:'name',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['name'],
                            data: [{'name': '院级'}, {'name': '市局级'}, {'name': '省部级'}, {'name': '国家级'}]
                        })
                    },
                    /*{
                        xtype: 'textfield',
                        columnWidth:.25,
                        name : 'jb',
                        fieldLabel:'级别',
                        labelWidth:110,
                        labelStyle:'padding-left:10px',
                        emptyText: '请输入',
                        allowBlank:false,
                        blankText:"不能为空，请输入"
                    },*/
                    {
                        xtype: 'textfield',
                        columnWidth:.25,
                        name : 'dep',
                        fieldLabel:'发布部门',
                        readOnly: true,
                        labelWidth:110,
                        labelStyle:'padding-left:10px'
                    },
                    {
                        xtype: 'textfield',
                        columnWidth:.25,
                        name : 'scr',
                        fieldLabel:'上传人',
                        readOnly: true,
                        labelWidth:110,
                        labelStyle:'padding-left:10px'
                    },
         {
                        xtype: 'datefield',
                        columnWidth:.25,
                        format:'Y年m月d日',
                        name : 'scDate',
                        fieldLabel:'上传日期',
                        labelWidth:60,
                        labelStyle:'padding-left:10px',
                        emptyText: '请输入',
                        allowBlank:false,
                        blankText:"不能为空，请输入"
                    },
                    {
                        xtype: 'textfield',
                        columnWidth:.25,
                        name : 'wjlx',
                        fieldLabel:'文件类型',
                        readOnly: true,
                        labelWidth:110,
                        labelStyle:'padding-left:10px'
                    }
                    /* ,{
                        xtype:'button',
                        text: '上传文件',
                        columnWidth:.10,
                        icon: 'images/tupian/folder_table.png',   //action: 'importFromFileAdd'
                        listeners: {
                            click: function() {
                                alert("!!!!!importFromFileAdd!!!!")
                            }
                        }
                    }
                  ,{
                        xtype: 'form',
                        id:'importForm1',
                        columnWidth:.40,
                        //height:60,
                        // baseCls: 'x-plain',
                        url:"notice",//上传服务器的地址
                        fileUpload: true,
                        //defaultType: 'textfield',
                        items: [{
                            xtype: 'fileuploadfield',
                            width:400,
                            labelWidth: 0,
                            // labelAlign: 'right',
                            //fieldLabel: '选择文件',
                            name: 'noticefilePath',
                            id: 'noticefilePath1',
                            buttonText: '选择文件',
                            blankText: '文件名不能为空'
                        }]
                    }*/
            ]
           }
        ];
        this.buttons = [
            {
                text: '保存',
                action: 'save'
            },
            {
                text: '取消',
                scope: this,
                handler: this.close
            }
        ];
        this.callParent(arguments);
    }
});
            
