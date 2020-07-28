/**
 * 左边的菜单区域，放树形菜单
 */
Ext.define('appMis.view.main.region.ProcessAdminLeft', {
    extend: 'Ext.panel.Panel',
    xtype: 'processadminmainleft',
    id: 'processadminmainleft',
    requires: [
        'appMis.view.main.menu.ProcessAdminTreeMainMenu'
    ],
    scrollable: true,
    //width: 200,
    width:'20%',
    title: '在职人员',
    header:false,
    items: [
        {
            xtype: 'button',
            text: '刷新单位',
            padding: '10px 2px 10px 2px',
            glyph: 'xf0c0@FontAwesome',
            listeners: {
                click: function (combo) {
                    Ext.Ajax.request({//根据用户角色的权限用来确定界面上的内容哪些隐藏或不隐藏,生成操作树。
                        url: '/appMis/authentication/userLogin?departmentName='+departmentName,
                        async:false,//同步
                        success://回调函数
                            function (resp, opts) {//成功后的回调方法
                                var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                var buttonMenu1 = resp.responseText.replace(/children/g, "menu");
                                buttonMenu1 = buttonMenu1.replace(/leaf : true/g, "action:'buttonMenu'");
                                var obj1 = eval('(' + buttonMenu1 + ')');//用/children/g可以把“children”全部换成"menu"，,再将获取的Json字符串转换为Json对象,
                                Ext.getCmp("processadminmainroot").getViewModel().set('systemButtonMenu',eval('(' + obj1.roots + ')'));//当前角色存入ViewModel
                                Ext.getCmp("processadminmainroot").getViewModel().set('systemTreeMenu',eval('(' + obj.roots + ')'));//当前角色存入ViewModel
                            }
                    });
                    Ext.getCmp("processadmintreemainmenu").setRootNode(Ext.getCmp("processadmintreemainmenu").up('app-processadminmain').getViewModel().get('systemTreeMenu'));//重新设置树状目录
                    Ext.getCmp("processadmintreemainmenu").expandPath("/p1000")//展开树状目录到path的位置
                }
            }
        },
        {
            xtype : 'processadmintreemainmenu',
            id:'processadmintreemainmenu',
            listeners: {
                beforerender: function (combo) {
                    this.expandPath("/p1000")//初始展开树状目录到path的位置
                }
            }
        }
    ]
})