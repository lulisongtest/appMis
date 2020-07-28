
var ENTERflag = 0
function setENTERflag(x) {//回车则失去焦点，防止再次调用Blur
    ENTERflag = x
    return
}
Ext.define('appMis.view.processAdmin.ProcessAdminContentViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processadmincontentviewport',
    stateful: true,
   // stateId: 'stateGrid0',
    height: 1900,//正文区的高度
    viewConfig: {
        margins: '0 0 30 0',
        trackOver: true,
        stripeRows: true
    },
    //items: [
      //  {
            //xtype: 'processDiagramviewport',
           // id:'processDiagram'
       // }
   // ],
    initComponent: function () {
        this.callParent(arguments);
    }
});
           
