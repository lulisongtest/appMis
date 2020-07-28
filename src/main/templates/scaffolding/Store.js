/*<%=new Date()%>陆立松自动创建
*/

Ext.define('appMis.store.${className}Store', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'appMis.model.${className}Model',
        'Ext.grid.*',
        'Ext.toolbar.Paging',
        'Ext.data.*'
    ],
    pageSize:25,
    autoLoad:  true,
    remoteSort: true,

    constructor: function(cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            model: 'appMis.model.${className}Model',
            storeId: '${className}Store',
            sorters: [{
                property: 'id',
                direction: 'DESC'
            }],
            proxy: {
                type: 'ajax',
                api: {
                    read :'/appMis/${propertyName}/read?dep_tree_id='
                },
                async:true,//true异步、false同步
                noCache: true,
                actionMethods: {
                    read   : 'POST'
                },
                reader: {
                    type: 'json',
                    rootProperty: '${propertyName}s',
                    totalProperty:"totalCount"
                },
                writer:{
                    type:'json'
                }
            },
            listeners:{
                load: function (r, option, success) {//监听过滤器,且对Ajax起作用，r.totalCount=-1时，网页过时了!appmis下TimeOutinterceptor.groovy
                    timeout1(r.totalCount, "学生基本信息")//在App.js中timeout1判断网页是否超时
                },
                update:function(store,record){
                    Ext.Ajax.request({
                        url:'/appMis/${propertyName}/update',
                        params:{
                            id:record.get("id"),<%
                  for(int i=2;i<domain.size();i++){
                       if(domain[i]=="Date"){%>
                            ${domain[i+1]}:(record.get('${domain[i+1]}'))?(new Date(record.get('${domain[i+1]}'))).pattern("yyyy-MM-dd")+" 00:00:00.0":"",<%
                                    i++;i++;
                       }else{
                          if(domain[i]=="Blob"){
                                   i++;i++;continue
                          }else{%>
                            ${domain[i+1]}:record.get("${domain[i+1]}"),<%
                                    i++;i++;
                          }
                       }
                  }%>
                        },
                        success:
                            function(resp,opts) {//成功后的回调方法
                                if(resp.responseText=='success'){
                                    var panelStore = (Ext.getCmp('studentgrid')).getStore();
                                    panelStore.loadPage(panelStore.currentPage);
                                }else{
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '数据更新失败！可能原因代码重名或是某个字段为空了！ ',
                                        buttons: Ext.MessageBox.OK
                                    })
                                    setTimeout(function () {
                                        var panelStore = (Ext.getCmp('${propertyName}grid')).getStore();
                                        panelStore.loadPage(panelStore.currentPage);
                                        Ext.Msg.hide();
                                    },2500);
                                }
                            },
                        failure: function(resp,opts) {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                var panelStore = (Ext.getCmp('${propertyName}grid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                                Ext.Msg.hide();
                            },2500)
                        }
                    });
                }
            }}, cfg)]);
    }
});
