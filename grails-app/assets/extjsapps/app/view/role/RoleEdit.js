Ext.define('appMis.view.role.RoleEdit' ,{
    extend: 'Ext.window.Window',
    alias : 'widget.roleedit',
    width:250,
    title : '修改用户角色信息',
    layout: 'fit',
    autoShow: true,

    initComponent: function() {
        this.items = [
            {
                xtype: 'form',
                items: [
                    {
                        xtype: 'textfield',
                        name : 'authority',
                        labelStyle:'padding:10px',
                        labelWidth:60,
                        allowBlank:false,
                        emptyText: '请输入角色',
                        blankText:"角色不能为空，请填写角色",
                        fieldLabel: '角  色'
                    },
                    {
                        xtype: 'textfield',
                        name : 'chineseAuthority',
                        labelWidth:60,
                        allowBlank:false,
                        emptyText: '请输入中文角色',
                        blankText:"中文角色不能为空，请填写中文角色汉化权限",
                        fieldLabel: '中文角色'

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

