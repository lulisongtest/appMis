function dateisnull(x){
               if((x=="")||(x==null)){
                   return null
               }else{
                   return (new Date(x)).pattern("yyyy-MM-dd")+" 00:00:00.0"
               }
           }
Ext.define('appMis.store.ProcessDiagramStore', { extend: 'Ext.data.Store',singleton: false,requires: ['appMis.model.ProcessDiagramModel','Ext.grid.*','Ext.toolbar.Paging','Ext.data.*'],
pageSize:25,
 remoteSort: true,
constructor: function(cfg) {
 var me = this;
 cfg = cfg || {};
 me.callParent([
 Ext.apply({
 model: 'appMis.model.ProcessDiagramModel',
 storeId: 'ProcessDiagramStore',
 autoLoad:  true,
 sorters: [{
    property: 'scDate',
    direction: 'ASC'
 }],
proxy: {
   type: 'ajax',
   api: {
   read :'/appMis/processDiagram/readProcessDiagram'
},
noCache: false,
actionMethods: {
    read:'POST'
},
reader: {
    rootProperty: 'processDiagrams',
    totalProperty:"totalCount"
},
writer:{type:'json'}
},
listeners:{
    load : function( r, option, success){
        timeout1(r.totalCount,"通知通告")
    },
update:function(store,record){
    Ext.Ajax.request({
    url:'/appMis/processDiagram/update',
    params:{
         id : record.get("id")
        ,title:record.get('title')
        ,titleCode:record.get('titleCode')
        ,jb:record.get('jb')
        ,dep:record.get('dep')
        ,scr:record.get('scr')
        ,scDate:dateisnull(record.get('scDate'))
        ,wjlx:record.get('wjlx')
        ,lczt:record.get('lczt')
    },
        success:function(resp,opts) {
            if(resp.responseText=='success'){
                   var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
                   panelStore.loadPage(panelStore.currentPage);
            }else{
                   Ext.Msg.show({
                        title: '操作提示 ',
                        msg: '数据更新失败！ ',
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
                        panelStore.loadPage(panelStore.currentPage);
                        Ext.Msg.hide();
                   },1500);
            }
        }
    });
}
}}, cfg)]);
    }
});
                    
