Ext.define('appMis.view.user.ModifyPassword', {
    extend: 'Ext.window.Window',
    alias: 'widget.moifyPassword',
    title: '修改个人密码',
    width: '40%',
    height: '35%',
    autoShow: true,
    id:'moifyPassword',

    modal: true,//创建模态窗口
    initComponent: function () {
        this.items = [
            {
                xtype: 'form',
                bodyPadding: 20,
                items: [
                    {
                        xtype: 'textfield',
                        name: 'oldpassword',
                        fieldLabel: '原密码',
                        hidden:true,
                        labelWidth: 110,
                        width: 380,
                        labelStyle: 'padding-left:10px',
                        inputType: 'password'
                    },
                    {
                        xtype: 'textfield',
                        name: 'newpassword',
                        fieldLabel: '新密码',
                        labelWidth: 110,
                        width: 380,
                        labelStyle: 'padding-left:10px',
                        inputType: 'password'
                    },
                    {
                        xtype: 'textfield',
                        name: 'repassword',
                        fieldLabel: '再次输入新密码',
                        labelWidth: 110,
                        width: 380,
                        labelStyle: 'padding-left:10px',
                        inputType: 'password'
                    }]
            },
            {
                xtype: 'button',
                text: '保存',
                glyph: 'xf0c7@FontAwesome',
                style : 'margin-left:140px',//离左边一个按钮的距离多加100px
                action: 'userModifySave'
            },
            {
                xtype: 'button',
                text: '取消',
                glyph: 'xf00d@FontAwesome',
                style : 'margin-left:10px',//离左边一个按钮的距离多加100px
                //scope: this, //handler: this.close
                action: 'userModifyClose'
            }
        ];
        this.callParent(arguments);
    }
});
            
