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
Ext.define('appMis.view.notice.NoticeGridViewport', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.noticegridviewport',
    disableSelection: false,
    loadMask: true,
    columnLines: true,
    stateful: true,
    stateId: 'stateGrid0',
    queryMode: 'remote',//会实时发ajax请求去load数据，这时默认会出现loadmask
    height: 500,//正文区的高度
    viewConfig: {
        margins: '0 0 30 0',
        trackOver: true,
        stripeRows: true
    },
    initComponent: function () {
        this.height=500//正文区的高度
        //var readAuth=Ext.getCmp('noticeReadAuth').isHidden()//true为普通用户，只能查看数据，不能修改任何数据。
        var readAuth =  ((Roleflag!="单位管理员")&&(Roleflag!="管理员"));
       // this.store = 'NoticeStore';
        this.selType = 'cellmodel';//单元格编辑模式,单元格编辑 Cell Editing
        this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 1
        })];
        //this.selModel = Ext.create('Ext.selection.CheckboxModel', {mode: "SINGLE"}),//"SINGLE"/"SIMPLE"/"MULTI"有复选框
        this.selModel = Ext.create('Ext.selection.CheckboxModel');//有复选框
            this.columns = [
                Ext.create('Ext.grid.RowNumberer', {
                    text: '序号',
                    width: 70
                })
                , {
                    header: '标题',
                    dataIndex: 'title',
                    width: 350
                    /*,editor: {
                        xtype: 'textfield',
                        readOnly: readAuth
                    }*/
                }

                , {
                    header: '标题编码',
                    dataIndex: 'titleCode',
                    hidden: true,
                    width: 150
                }
                , {
                    header: '级别',
                    dataIndex: 'jb',
                    width: 120,
                    editor: {
                        xtype: 'combobox',
                        readOnly: readAuth,
                        displayField: 'name',
                        store: Ext.create('Ext.data.Store', {
                            fields: ['name'],
                            data: [{'name': '院级'}, {'name': '市局级'}, {'name': '省部级'}, {'name': '国家级'}]
                        })
                    }
                }
                , {
                    header: '发布部门',
                    dataIndex: 'dep',
                    width: 260,
                    editor: {
                        xtype: 'textfield',
                        readOnly: readAuth
                    }
                }
                , {
                    header: '上传人',
                    dataIndex: 'scr',
                    width: 120,
                    editor: {
                        xtype: 'textfield',
                        readOnly: readAuth
                    }
                }

                , {
                    header: '上传日期',
                    dataIndex: 'scDate',
                    width: 130,
                    sortable: true,
                    renderer: Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        readOnly: readAuth,
                        format: 'Y-m-d',
                        name: 'scDate'
                    }
                }
                , {
                    header: '文件类型',
                    dataIndex: 'wjlx',
                    hidden:true,
                    width: 90
                    /*,editor: {
                        xtype: 'textfield',
                        readOnly: readAuth
                    }*/
                }
                , {
                    header: "上传文件",
                    hidden:((Roleflag!="区人社局审核人")&&(Roleflag!="市人社局审核人")&&(Roleflag!="管理员")),
                    renderer: function (value, meta, record) {
                        var formatStr = "<button  onclick='javscript:return false;' class='order_bit'>上传文件</button>";// var resultStr = String.prototype.format(formatStr);
                        return "<div class='controlBtn'>" + formatStr + "</div>";
                    },
                    css: "text-align:center;",
                    sortable: false
                },
                {
                    text: "查看下载文件",
                    width: 130,
                    align: "center",
                    renderer: function (value, cellmeta) {
                        var returnStr = "<INPUT type='button' value='查看文件'>";
                        return returnStr;
                    }
                }
            ];
        this.bbar = Ext.create('Ext.PagingToolbar', {
            //store: 'NoticeStore',
            displayInfo: true,
            id: 'bbarNotice',
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
                            // var panelStore = (Ext.getCmp('noticegrid')).getStore();
                            // combo.setValue(panelStore.pageSize)//初始化pageSize
                        },
                        afterrender: function (combo) {
                            var panelStore = (Ext.getCmp('noticegrid')).getStore();
                            combo.setValue(panelStore.pageSize)//初始化pageSize
                            panelStore.loadPage(1);
                        },
                        change: function (combo) {
                            pageSize=combo.getValue();
                            if (pageSize == null) {
                                pageSize = 25
                            }//不选则每页25行数据
                            var panelStore = (Ext.getCmp('noticegrid')).getStore();
                            panelStore.pageSize = pageSize;
                            var newvalue1 = (new Date(Ext.getCmp('currentNoticeDate').getValue()).pattern("yyyy-MM-dd"));
                            panelStore.proxy.api.read = '/appMis/notice/readNotice?currentNoticeDate=' + newvalue1;
                            Ext.getCmp('noticegrid').refreshData;
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
        this.callParent(arguments);
    }
});
           
