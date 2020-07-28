/**
 * 折叠式(accordion)菜单，样式可以自己用css进行美化
 */

Ext.define('appMis.view.main.menu.AccordionMainMenu', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.mainmenuaccordion',
			title : '系统菜单',

			layout : {
				type : 'accordion',
				animate : true
			},

			initComponent : function() {
				this.items = [];
				var menus = this.up('app-main').getViewModel().get('systemMenu');
				for (var i in menus) {
					var menugroup = menus[i];
					var accpanel = {
						menuAccordion : true,
						xtype : 'panel',
						title : menugroup.text,
						bodyStyle : {
							padding : '10px'
						},
						layout : 'fit',
						dockedItems : [{
									dock : 'left',
									xtype : 'toolbar',
									items : []
								}],
						glyph : menugroup.glyph
					};
					for (var j in menugroup.items) {
						var menumodule = menugroup.items[j];
						accpanel.dockedItems[0].items.push({
									xtype : 'buttontransparent',
									text : this.addSpace(menumodule.text, 12),
									glyph : menumodule.glyph,
									handler : 'onMainMenuClick'
								});
					}
					this.items.push(accpanel);
				}
				this.callParent(arguments);
			},

			addSpace : function(text, len) {
				var result = text;
				for (var i = text.length; i < len; i++) {
					result += '　';
				}
				return result;
			}

		})