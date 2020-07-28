
Ext.define('appMis.view.rules.RulesViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.rulesviewport',
    defaults: {
        autoScroll: true,
        bodyPadding: 0,
        border: 0,
        padding: 0
    },
    items: [
        {
            xtype: 'toolbar',
            height: 48,
            defaults: {
                margins: '0 0 0 0',
                bodyPadding: 0,
                border: 0,
                padding: '4 0 4 0'
            },
            items: [
                {
                    xtype: 'datefield',
                    id: 'currentRulesDate',
                    fieldLabel: '当前日期',
                    format: 'Y年m月d日',
                    labelAlign: 'right',
                    width: 230,
                    value: new Date(),
                    labelWidth: 60,
                    listeners: {
                        change: function (combo) {
                            var panelStore = (Ext.getCmp('rulesgrid')).getStore();
                            if(!(Ext.getCmp('currentRulesDate').getValue())||(Ext.getCmp('currentRulesDate').getValue()=='')){
                                panelStore.proxy.api.read = '/appMis/rules/readRules?currentRulesDate='+"&displaydate="+Ext.getCmp('displaydaterules').value;
                                panelStore.loadPage(1);
                            }else{
                                panelStore.proxy.api.read = '/appMis/rules/readRules?currentRulesDate='+ (new Date(Ext.getCmp('currentRulesDate').getValue()).pattern("yyyy-MM-dd"))+"&displaydate="+Ext.getCmp('displaydaterules').value;
                                panelStore.loadPage(1);
                            }
                        }
                    }
                },
                {
                    xtype: 'combobox',
                    fieldLabel:'显示日期',
                    id:'displaydaterules',
                    displayField: 'name',
                    valueField:'value',
                    labelAlign: 'right',
                    value:'month',
                    width: 145,
                    labelWidth: 60,
                    store: Ext.create('Ext.data.Store', {
                        fields: ['name'],
                        data: [{'name': '按年',value:'year'}, {'name': '按月',value:'month'},{'name': '按日',value:'day'}]
                    }),
                    listeners: {
                        change: function (combo) {
                            var panelStore = (Ext.getCmp('rulesgrid')).getStore();
                            panelStore.proxy.api.read = "/appMis/rules/readRules?currentRulesDate="+ (new Date(Ext.getCmp('currentRulesDate').getValue()).pattern("yyyy-MM-dd"))+"&displaydate="+Ext.getCmp('displaydaterules').value;
                            panelStore.loadPage(1);
                        }
                    }
                },
                /*    {
                 text: '删除单位当前月数据',
                 icon: 'images/tupian/cancel.png',
                 action: 'deleteMonthRules'
                 },*/
                /*{
                    text: '新增',
                    hidden:((Roleflag!="科研负责人")&&(Roleflag!="科研管理员")&&(Roleflag!="管理员")),
                    icon: 'tupian/edit_add.png',
                    action: 'add'
                }, */
                {
                    text: '增加行',
                    hidden:((Roleflag!="科研负责人")&&(Roleflag!="科研管理员")&&(Roleflag!="管理员")),
                    icon: 'tupian/edit_remove.png',
                    action: 'addRow'
                },
                /*{
                    text: '修改',
                    hidden:((Roleflag!="科研负责人")&&(Roleflag!="科研管理员")&&(Roleflag!="管理员")),
                    icon: 'tupian/pencil.png',
                    action: 'modify'
                }, */
                {
                    text: '删除',
                    hidden:((Roleflag!="科研负责人")&&(Roleflag!="科研管理员")&&(Roleflag!="管理员")),
                    icon: 'tupian/cancel.png',
                    action: 'delete'
                },{
                    xtype: 'textfield',
                    id: 'keywordrules',
                    fieldLabel: '关键字',
                    labelAlign: 'right',
                    width: 163,
                    labelWidth: 45
                }, {
                    xtype: 'button',
                    width: 70,
                    action: 'query',
                    icon: 'tupian/search.png',
                    handler: function (button, e) {
                        var newvalue = Ext.getCmp('keywordrules').getValue();
                        var panelStore = (Ext.getCmp('rulesgrid')).getStore();
                        panelStore.proxy.api.read = '/appMis/rules/search?q=' + newvalue;
                        Ext.getCmp('rulesgrid').refreshData;
                        panelStore.loadPage(1);
                    },
                    text: '搜索'
                }
            ]
        },
        {
            xtype: 'panel',
            items: [
                {
                    xtype: 'rulesgridviewport',
                    id: 'rulesgrid',
                    // height: 500,//正文区的高度
                    listeners: {
                        beforerender:function () {
                            this.height=500;
                            (Ext.getCmp('rulesgrid')).setStore('RulesStore');
                            (Ext.getCmp('bbarRules')).setStore('RulesStore');//分页
                            var panelStore =(Ext.getCmp('rulesgrid')).getStore();
                            panelStore.sort('scDate', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                            //panelStore.loadPage(1);//上条命令已经会向后台发送一次AJAX
                        },
                        cellclick: function(grid,td,cellIndex, record, tr, rowIndex, e, eOpts){       //alert("rowIndex===="+rowIndex); alert("cellIndex===="+cellIndex); alert("td===="+td); alert("tr===="+tr)// var fieldName = grid.getSelectionModel().getDataIndex(cellIndex); // 列名=fieldName；                          //var data = record.get('title');// alert("data=========="+data ); // alert("td.className===="+td.className) // alert("tr.className===="+tr.className)
                            rec=record
                            if(cellIndex==9){//上传文件
                                //recId=record.get('id');
                                view = Ext.widget('importRules');   //view =Ext.create('appMis.view.user.SelectDepartment')//奇怪！！！随意修改以下两行或其中一行的数值，就可实现modal
                                document.getElementById('rules').style.setPropertyValue('z-index',0);
                            }
                            if(cellIndex==10){//查看文件//判断文件是否存在 // var src=rec.get('titleCode')+"_"+rec.get('title')+"."+rec.get('wjlx');//文件名
                                if(rec.get('wjlx')=='pdf'){
                                    //alert("显示PDF文件内容")
                                    view = Ext.widget('exportRules');   //view =Ext.create('appMis.view.user.SelectDepartment')//奇怪！！！随意修改以下两行或其中一行的数值，就可实现modal
                                    Ext.getCmp('exportRules').setTitle("查看《"+rec.get("title")+"》文件")
                                }else {
                                    //alert("下载文件")
                                    Ext.Ajax.request({
                                        url:'/appMis/rules/displayRules?recId='+rec.get('id')+'&title='+rec.get('title'),
                                        method : 'POST',
                                        success://回调函数
                                            function(resp,opts) {
                                                {
                                                    Ext.MessageBox.buttonText.ok="完成";
                                                    Ext.Msg.show({
                                                        title: '操作提示 ',
                                                        msg: "<a href=\"http://"+window.location.host+"/appMis/static/tmp/"+resp.responseText+"\"><b>保存该文件</b></a>",
                                                        align: "centre",
                                                        buttons: Ext.MessageBox.OK
                                                    })
                                                }
                                            },
                                        failure: function(resp,opts) {
                                            //myMask.hide();
                                            Ext.Msg.show({
                                                title: '操作提示 ',
                                                msg: '文件导出到Excel失败！ ',
                                                buttons: Ext.MessageBox.OK
                                            });
                                            setTimeout(function () {
                                                Ext.Msg.hide();
                                            },1500)
                                        }
                                    });
                                }





                            }
                        }
                    }
                }]
        }]
});
 
