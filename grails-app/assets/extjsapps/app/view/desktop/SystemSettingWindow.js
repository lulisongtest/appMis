/*!
 * Ext JS Library
 * Copyright(c) 2006-2014 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */


Ext.define('appMis.view.desktop.SystemSettingWindow', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
      ],
    id:'systemSetting',

    init : function(){

        this.launcher = {
            text: '系统设置',
            iconCls:'systemSetting'
        }
    },
    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('systemSetting');
        if(!win){
            win = desktop.createWindow({
                id: 'systemSetting',
                title:'系统设置',
                 width:'100%',
                 height:'95%',
                iconCls: 'systemSetting',
               // animCollapse:false,
               // resizable:false,
               // minimizable:false,
              //  maximizable:false,
               // header:false,
                border: false,
                //defaultFocus: 'notepad-editor', EXTJSIV-1300

                // IE has a bug where it will keep the iframe's background visible when the window
                // is set to visibility:hidden. Hiding the window via position offsets instead gets
                // around this bug.

                // hideMode: 'offsets',

                layout: 'fit',
                items: [
                    {
                        xtype: 'systemSettingmain',
                       // title:'系统设置'
                        listeners: {
                            beforerender: function () {
                                var mainleft = Ext.getCmp("systemSettingleft");
                                var main1 = Ext.getCmp("systemSettingcenter");
                                main1.remove(main1.getActiveTab());
                                var mainsetting=Ext.create('Ext.panel.Panel',{
                                    xtype: 'panel',
                                    //id: 'a10',    //id不能是单独的数字
                                    html: '系统设置_____在管理系统运行过程中，需要及时地呈报报表，以便上级能及时了解管理系统运行状况，做出相应重要的决策。\r\n系统主要功能包括报表数据的导入、录入、修改、审核、批准、查询、组合查询、数据导入、备份，生成报表、查询报表、操作报表、打印、报表输出到Excel、Pdf、Html、Word等文件、报表导入、备份等；用户管理包括用户创建、用户删除、用户权限分配管理等。\r\n ',
                                    glyph: 'xf015@FontAwesome',
                                    closable:true,
                                    icon: 'images/application/application_form.png',
                                    title: '首页'
                                });
                                main1.add(mainsetting);
                                main1.setActiveTab(mainsetting);
                                mainleft.setTitle("系统设置")
                            }
                        }
                    }
                ]
            });
        }
        return win;
    }
});
