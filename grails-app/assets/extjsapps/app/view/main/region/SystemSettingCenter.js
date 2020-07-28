/**
 * 系统界面的主区域,是一个tabpanel,可以有多个tab页面，用来放置各个模块。
 */
Ext.define('appMis.view.main.region.SystemSettingCenter', {
	extend : 'Ext.tab.Panel',
	xtype: 'systemSettingmaincenter',
	border: false,
	//id: 'systemSettingcontentpanel',
	defaults: {
		autoScroll: true,
		bodyPadding: 5
	},
	closable : true,
	header: false,
	//items:Ext.create('salaryMis.view.employee.EmployeemainpageViewport'),//会发生死机
	plugins: Ext.create('Ext.ux.TabCloseMenu', {
		closeTabText: '关闭当前',
		closeOthersTabsText: '关闭其他',
		closeAllTabsText: '关闭所有',
		extraItemsTail: [
			'-',
			{
				text: '可关闭',
				checked: true,
				hideOnClick: true,
				handler: function (item) {
					currentItem.tab.setClosable(item.checked);
				}
			},
			'-',
			{
				text: '可使用',
				checked: true,
				hideOnClick: true,
				handler: function (item) {
					currentItem.tab.setDisabled(!item.checked);
				}
			}
		],
		listeners: {
			beforemenu: function (menu, item) {
				var enabled = menu.child('[text="可使用"]');
				menu.child('[text="可关闭"]').setChecked(item.closable);
				if (item.tab.active) {
					enabled.disable();
				} else {
					enabled.enable();
					enabled.setChecked(!item.tab.isDisabled());
				}
				currentItem = item;
			}
		}
	}),
	initComponent : function() {
			//this.items=Ext.create('salaryMis.view.articles.ArticlesMainpageViewport')
		   // this.items=Ext.create('salaryMis.view.department.DepartmentMainpageViewport')
			this.callParent();
	}
})