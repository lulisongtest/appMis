/*!
 * Ext JS Library
 * Copyright(c) 2006-2014 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */


Ext.define('appMis.view.desktop.NoticeWindow', {
    extend: 'Ext.ux.desktop.Module',
    requires: [
    ],
    id:'notice',
    init : function(){
        this.launcher = {
            text: '通知通告',
            iconCls:'notice'
        }
    },
    createWindow : function(){
        var desktop = this.app.getDesktop();
       
        var win = desktop.getWindow('notice');
        if(!win){
            win = desktop.createWindow({
                id: 'notice',
                title:'通知通告',
                width:'100%',
                height:'95%',
                iconCls: 'notice',
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
                        xtype: 'app-noticemain',
                        listeners: {
                            beforerender: function () {

                            }
                        }
                    }
                ]
            });
        }
        return win;
    }
});
