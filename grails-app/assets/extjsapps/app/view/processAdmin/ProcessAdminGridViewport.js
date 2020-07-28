/*function b(v){
    alert(this.temp+" "+v);
}
b.call({temp:'abc'},'hello');

Function.prototype.createDelegate=function(scope,params){
    var _fun = this;
    return function(){
        _fun.apply(scope,params);
    };
}
var fun = b.createDelegate({temp:'abc'},['hello']);
fun();
*/
var ENTERflag = 0
function setENTERflag(x) {//回车则失去焦点，防止再次调用Blur
    ENTERflag = x
    return
}
Ext.define('appMis.view.processAdmin.ProcessAdminGridViewport', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.processAdmingridviewport',
    disableSelection: false,
    loadMask: true,
    columnLines: true,
    stateful: true,
   // stateId: 'stateGrid0',
    queryMode: 'remote',//会实时发ajax请求去load数据，这时默认会出现loadmask
    height: 500,//正文区的高度
    viewConfig: {
        margins: '0 0 30 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function () {
        this.height=500//正文区的高度
        //var readAuth=Ext.getCmp('processAdminReadAuth').isHidden()//true为普通用户，只能查看数据，不能修改任何数据。
        var readAuth =  ((Roleflag!="单位管理员")&&(Roleflag!="管理员"))
       // this.store = 'ProcessAdminStore';
        this.selType = 'cellmodel'//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })]
        //this.selModel = Ext.create('Ext.selection.CheckboxModel', {mode: "SINGLE"}),//"SINGLE"/"SIMPLE"/"MULTI"有复选框
        this.selModel = Ext.create('Ext.selection.CheckboxModel'),//有复选框
            this.columns = [

            ];
        this.bbar = Ext.create('Ext.PagingToolbar', {
            //store: 'ProcessAdminStore',
            displayInfo: true,
            id: 'bbarProcessAdmin',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            height: 40,
            items: [
                '-', '每页数据', {
                    xtype: 'combobox',
                    typeAhead: true,
                    triggerAction: 'all',
                    lazyRender: true,
                    mode: 'local',
                    width: 80,
                    store: [1, 5, 10, 15, 20, 25, 30, 50, 75, 100, 200, 300, 500],
                    enableKeyEvents: true,
                    editable: true,
                    value: 25,
                    listeners: {
                        beforerender: function (combo) {
                            // var panelStore = (Ext.getCmp('processAdmingrid')).getStore();
                            // combo.setValue(panelStore.pageSize)//初始化pageSize
                        },
                        afterrender: function (combo) {
                           // var panelStore = (Ext.getCmp('processAdmingrid')).getStore();
                           // combo.setValue(panelStore.pageSize)//初始化pageSize
                          //  panelStore.loadPage(1);
                        },
                        change: function (combo) {
                         //   pageSize=combo.getValue();
                         //   if (pageSize == null) {
                          //      pageSize = 25
                          //  }//不选则每页25行数据
                          //  var panelStore = (Ext.getCmp('processAdmingrid')).getStore();
                          //  panelStore.pageSize = pageSize;
                          //  var newvalue1 = (new Date(Ext.getCmp('currentProcessAdminDate').getValue()).pattern("yyyy-MM-dd"));
                          //  panelStore.proxy.api.read = 'processAdmin/readProcessAdmin?currentProcessAdminDate=' + newvalue1;
                          //  Ext.getCmp('processAdmingrid').refreshData;
                          //  panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});
           
