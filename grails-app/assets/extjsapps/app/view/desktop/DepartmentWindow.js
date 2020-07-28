/*!
 * Ext JS Library
 * Copyright(c) 2006-2014 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */


Ext.define('appMis.view.desktop.DepartmentWindow', {
    extend: 'Ext.ux.desktop.Module',

    requires: [
      ],
    id:'department',

    init : function(){
        this.launcher = {
            text: '单位信息',
            iconCls:'department'
        }
    },
    createWindow : function(){
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('department');
        if(!win){
            win = desktop.createWindow({
                id: 'department',
                title:'单位信息',
                 width:'100%',
                 height:'95%',
                iconCls: 'department',
                animCollapse:false,
                resizable:false,
                minimizable:false,
                maximizable:false,
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
                        xtype: 'app-departmentmain',
                        listeners: {
                            beforerender: function () {

                                var mainleft = Ext.getCmp("departmentmainleft");
                                var main1 = Ext.getCmp("departmentcontentpanel");
                                main1.remove(main1.getActiveTab());
                                var maimdepartment=Ext.create('appMis.view.department.DepartmentMainpageViewport');
                                main1.add(maimdepartment);
                                main1.setActiveTab(maimdepartment);
                                mainleft.setTitle("单位信息")
                            }
                        }

                    }
                ]
            });
        }
        return win;
    }
});
