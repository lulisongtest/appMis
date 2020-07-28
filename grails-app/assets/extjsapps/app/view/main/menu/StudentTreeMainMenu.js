/**
 * 树状菜单，显示在主界面的左边
 */
Ext.define('appMis.view.main.menu.StudentTreeMainMenu', {
			extend : 'Ext.tree.Panel',
			alias : 'widget.studenttreemainmenu',
			rootVisible : true,
			lines : false,
	        width:400,
	        expanded : true, // 在树形菜单中是否展开,id:mainroot1在desktop/App.js中定义的
	    	initComponent : function() {
				//this.root = this.up('app-main').getViewModel().get('systemTreeMenu');//很奇怪，按钮菜单要加上[],而树则不能加[]
				this.root = Ext.getCmp("viewModelDataRoot").getViewModel().get('systemTreeMenu');//很奇怪，按钮菜单要加上[],而树则不能加[]
               //Ext.getCmp("studenttreemainmenu").setRootNode(Ext.getCmp("studenttreemainmenu").up('app-studentmain').getViewModel().get('systemTreeMenu'))//重新设置树状目录
               // this.root.expandPath("/p1000")//展开树状目录到path的位置
                this.callParent(arguments);
			}
		})

//viewModelDataRoot在App.js文件中Ext.override(Ext.ux.desktop.Desktop, {    viewModel: 'main',    id: 'viewModelDataRoot',