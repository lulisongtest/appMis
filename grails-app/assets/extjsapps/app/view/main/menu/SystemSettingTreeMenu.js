/**
 * 树状菜单，显示在主界面的左边
 */
Ext.define('appMis.view.main.menu.SystemSettingTreeMenu', {
    extend: 'Ext.tree.Panel',
    xtype: 'systemSettingtreemenu',
    alias: 'widget.systemSettingtreemenu',
    rootVisible: true,
    lines: false,
    width: 400,
    expanded: true, // 在树形菜单中是否展开
    initComponent : function() {
        //this.root = this.up('app-main').getViewModel().get('sysSettingMainMenu');//很奇怪，按钮菜单要加上[],而树则不能加[]
        //id:viewModelDataRoot是在桌面App.js的 Ext.define('salaryMis.view.desktop.App', {中的
        // init的 Ext.override(Ext.ux.desktop.Desktop, {  viewModel: 'main', id: 'viewModelDataRoot',//为了获取树状目录的值
        this.root = Ext.getCmp("viewModelDataRoot").getViewModel().get('sysSettingMainMenu');//很奇怪，按钮菜单要加上[],而树则不能加[]
        this.callParent(arguments);
    }
 })