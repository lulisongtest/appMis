/**
 * 左边的菜单区域，放树形菜单
 */
Ext.define('appMis.view.main.region.SystemSettingLeft', {
    extend: 'Ext.panel.Panel',
    xtype: 'systemSettingmainleft',
    id: 'systemSettingmainleft',
   requires: [
       'appMis.view.main.menu.SystemSettingTreeMenu'
    ],
    scrollable: true,
    //width: 200,
    width:'20%',
    title: '系统设置',
    header:false,
    items: [
        {
            xtype : 'systemSettingtreemenu',
            id:'systemSettingtreemenu'
        }
    ]
})