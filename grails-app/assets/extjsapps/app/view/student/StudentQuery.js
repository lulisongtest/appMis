Ext.define('appMis.view.student.StudentQuery' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.studentquery',
    width:1070,
    height: 330,
    title : '导入导出职工信息',
    autoShow: true,
    modal: true,//创建模态窗口
    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                height: 152,
                layout:'absolute',
                items: [
                    {
                        xtype: 'textfield',
                        name: 'studentCode',
                        x:5,y:5,
                        labelWidth: 60,
                        fieldLabel: '选择导入职工信息Excel文件',
                        allowBlank: false,
                        labelStyle: 'padding-left:10px',
                        emptyText: '请输入职工编码',
                        value: "未知",
                        blankText: '职工编码不能为空，请输入职工编码'
                    },
                    {
                        xtype: 'textfield',
                        name: 'name',
                        x:300,y:5,
                        labelWidth: 60,
                        labelStyle: 'padding-left:10px',
                        fieldLabel: '导出职工信息',
                        allowBlank: false,
                        blankText: '姓名不能为空，请输入姓名',//验证错误时提示信息
                        value: "未知",
                        emptyText: '请输入姓名'
                    }
                ]
            },
        ];
        this.buttons = [
            {
                text: '查询',
                glyph:'xf0c7@FontAwesome',
                action: 'save'
            },
            {
                text: '关闭',
                glyph:'xf00d@FontAwesome',
                scope: this,
                handler: this.close
            }
        ];
        this.callParent(arguments);
    }
});

