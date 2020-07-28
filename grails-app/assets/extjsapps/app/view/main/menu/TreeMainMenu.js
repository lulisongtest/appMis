/**
 * 树状菜单，显示在主界面的左边
 */
Ext.define('appMis.view.main.menu.TreeMainMenu', {
			extend : 'Ext.tree.Panel',
			alias : 'widget.treemainmenu',
			rootVisible : true,
			lines : false,
	        width:400,
	        expanded : true, // 在树形菜单中是否展开
	    	initComponent : function() {
				this.root = this.up('app-main').getViewModel().get('systemTreeMenu');//很奇怪，按钮菜单要加上[],而树则不能加[]
				this.callParent(arguments);
			}
		})