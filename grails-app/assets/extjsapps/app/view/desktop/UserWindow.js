
Ext.define('appMis.view.desktop.UserWindow', {
    extend: 'Ext.ux.desktop.Module',
    requires: [
    ],
    id:'user',

    init : function(){
        this.launcher = {
            text: '个人信息',
            iconCls:'user'
        };
    },

    createWindow : function(){
        var me=this;
        var desktop = this.app.getDesktop();
        var win = desktop.getWindow('user');
        if(!win){
            win = desktop.createWindow({
                id: 'user',
                title:'个人信息',
               // width:'100%',
               // height:'95%',
                iconCls: 'user',
               // animCollapse:false,
               // resizable:false,
              //  minimizable:false,
              //  maximizable:false,
                // header:false,
               // border: false,
                //defaultFocus: 'notepad-editor', EXTJSIV-1300

                // IE has a bug where it will keep the iframe's background visible when the window
                // is set to visibility:hidden. Hiding the window via position offsets instead gets
                // around this bug.

                // hideMode: 'offsets',
                //layout: 'fit',
                items: [
                    {
                        xtype: 'app-usermain',
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
