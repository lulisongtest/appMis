package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateGridViewportjs {
    def createGridViewportjs(String tablename){
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
        def list = DynamicTable.findAll(query)
        try{
            String filePath="D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\view\\"+tablename
            File myFilePath=new File(filePath);
            if (!myFilePath.exists()){
                myFilePath.mkdir()
            }
        }catch(Exception e){}
        try {
            StringBuilder sb=new StringBuilder();
            sb.append("""
var ENTERflag=0
function setENTERflag(x){//回车则失去焦点，防止再次调用Blur
        ENTERflag=x
        return
}
    Ext.define('salaryMis.view."""+tablename+"""."""+initcap2(tablename)+"""GridViewport' ,{
        extend: 'Ext.grid.Panel',
        alias : 'widget."""+tablename+"""gridviewport',
         disableSelection: false,
         loadMask: true,
         columnLines: true,
         stateful: true,
         //stateId: 'stateGrid',
         viewConfig: {
             margins: '0 0 0 0',
             trackOver: true,
             stripeRows: true
         },
        initComponent: function() {
           //var readAuth=Ext.getCmp('"""+tablename+"""ReadAuth').isHidden()//true为普通用户，只能查看数据，不能修改任何数据。
           var readAuth=true
           this.store = '"""+initcap2(tablename)+"""Store';
           this.selType = 'cellmodel'//单元格编辑模式,单元格编辑 Cell Editing
           this.plugins = [Ext.create('Ext.grid.plugin.CellEditing', {
           clicksToEdit: 1
        })]
        //this.selModel = Ext.create('Ext.selection.CheckboxModel', {mode: "SINGLE"}),//"SINGLE"/"SIMPLE"/"MULTI"有复选框
        this.selModel = Ext.create('Ext.selection.CheckboxModel'),//有复选框
            this.columns = [
                Ext.create('Ext.grid.RowNumberer',{
                    text:'序号',
                    width:75
                })""")
            for(int i=0;i<list.size();i++){
                if(((DynamicTable)list[i]).fieldType=="日期"){
                        sb.append("""
                  ,{header: '""" + ((DynamicTable) list[i]).fieldName + """',
                    dataIndex: '""" + ((DynamicTable) list[i]).fieldNameId + """',
                    width:100,
                    sortable: true,
                    renderer : Ext.util.Format.dateRenderer('Y年m月d日'),
                    editor: {
                        xtype: 'datefield',
                        readOnly : readAuth,
                        format: 'Y-m-d',
                        name : '""" + ((DynamicTable) list[i]).fieldNameId + """'
                    }
                  }
                """)
                }else{
                    if(((DynamicTable)list[i]).fieldType=="字符"){
                                sb.append("""
                  ,{header: '"""+((DynamicTable)list[i]).fieldName+"""',
                    dataIndex: '"""+((DynamicTable)list[i]).fieldNameId+"""',
                    width: 100,
                    editor: {
                        xtype: 'textfield',
                        readOnly : readAuth
                    }
                  }
                """)
                     }
                }
               if(((DynamicTable)list[i]).fieldType=="浮点数"){
                      sb.append("""
                  ,{header: '"""+((DynamicTable)list[i]).fieldName+"""',
                    dataIndex: '"""+((DynamicTable)list[i]).fieldNameId+"""',
                    width: 100,
                    editor: {
                        xtype: 'textfield',
                        readOnly : readAuth,
                        id: '"""+((DynamicTable)list[i]).fieldNameId+"""'
                    }
                }
                """)
               }
               if(((DynamicTable)list[i]).fieldType=="整数"){
                    sb.append("""
                  ,{header: '"""+((DynamicTable)list[i]).fieldName+"""',
                    dataIndex: '"""+((DynamicTable)list[i]).fieldNameId+"""',
                    width: 100,
                    editor: {
                        xtype: 'textfield',
                        readOnly : readAuth,
                        id: '"""+((DynamicTable)list[i]).fieldNameId+"""'
                    }
                }
                """)
               }
            }
            sb.append("""
            ];
             this.bbar = Ext.create('Ext.PagingToolbar', {
            store: '"""+initcap2(tablename)+"""Store',
            displayInfo: true,
            id: 'bbar"""+initcap2(tablename)+"""',
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            height: 42,
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
                    value: 10,
                    listeners: {
                       change: function (combo) {
                            var newvalue = combo.getValue();
                            if (newvalue == null) {
                                newvalue = 10
                            }//不选则每页10行数
                            var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                            panelStore.pageSize = newvalue;
                            var newvalue1 = (new Date(Ext.getCmp('current"""+initcap2(tablename)+"""Date').getValue()).pattern("yyyy-MM-dd"));
                            panelStore.proxy.api.read = '"""+tablename+"""/read"""+initcap2(tablename)+"""?current"""+initcap2(tablename)+"""Date=' + newvalue1;
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        });
            this.callParent(arguments);
        }
    });
           """)
            FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\view\\"+tablename+"\\"+initcap2(tablename)+"GridViewport.js");
            PrintWriter pw = new PrintWriter(fw);
            pw.println(sb);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    def  initcap2(String str) {//不带路径不带盘符的名字第一个字母改为大写
        int i=0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0]-32);
        return new String(ch);
    }
}
