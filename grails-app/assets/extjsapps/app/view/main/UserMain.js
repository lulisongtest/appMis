Ext.define('appMis.view.main.UserMain', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.usermain',
    xtype: 'app-usermain',
    id: 'usermainroot',
    requires: [

    ],
    initComponent: function () {
         this.callParent()
    },
   // controller: 'rulesmain',//指定控制器，否则就到app.js中查找
   // viewModel: 'main',
    ui: 'navigation',
    layout: {
       type: 'fit'
    },
    items: [
        {
            xtype: 'usermodify',
            id: 'usermodify',
            header: false,
            listeners: {
                beforerender: function () {
                    var me=this

                    Ext.Ajax.request({//根据用户角色的权限用来确定界面上的内容哪些隐藏或不隐藏,生成操作树。
                        url: '/appMis/user/readCurrentUser',
                        async:false,//同步请求数据, true异步
                        success://回调函数
                            function (resp, opts) {//成功后的回调方法
                                var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                var record = Ext.create('appMis.model.UserModel');//创建新的记录
                                var view = Ext.getCmp('usermodify');
                                var form   = view.down('form');
                                var values = form.getValues();
                                values=obj.users[0];
                                record.set(values);
                                view.down('form').loadRecord(record);//显示编辑视图
                            }
                     });
                }
            }

        }
    ]
});
