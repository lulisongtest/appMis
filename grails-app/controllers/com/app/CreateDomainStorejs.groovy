package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateDomainStorejs {
    List list
    def createDomainStorejs(String tablename){
        println("tablename======"+tablename)
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
        list = DynamicTable.findAll(query)
        String content = parseStorejs(tablename);
        try {
            FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\store\\"+initcap2(tablename)+"Store.js");
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    def  parseStorejs(String tablename) {
        StringBuffer sb = new StringBuffer();
        sb.append("""function dateisnull(x){
               if((x=="")||(x==null)){
                   return null
               }else{
                   return (new Date(x)).pattern("yyyy-MM-dd")+" 00:00:00.0"
               }
           }\r\n""")
        sb.append("Ext.define('salaryMis.store." + initcap2(tablename) + "Store', { extend: 'Ext.data.Store',singleton: false,")
        sb.append("requires: ['salaryMis.model."+initcap2(tablename)+"Model',");
        sb.append("'Ext.grid.*','Ext.toolbar.Paging','Ext.data.*'], \r\n");
        sb.append("pageSize:15,\r\n ");
        sb.append("remoteSort: true,\r\n");
        sb.append("constructor: function(cfg) {\r\n ");
        sb.append("var me = this;\r\n");
        sb.append(" cfg = cfg || {};\r\n");
        sb.append(" me.callParent([\r\n");
        sb.append(" Ext.apply({\r\n");
        sb.append(" model: 'salaryMis.model."+initcap2(tablename)+"Model',\r\n");
        sb.append(" storeId: '" + initcap2(tablename) +"Store', \r\n");
        sb.append(" autoLoad:  true,\r\n");
        sb.append(" sorters: [{\r\n");
        sb.append("    property: '"+((DynamicTable)list[0]).fieldNameId+"',\r\n");
        sb.append("    direction: 'ASC'\r\n");
        sb.append(" }],\r\n");
        sb.append("proxy: {\r\n");
        sb.append("   type: 'ajax',\r\n");
        sb.append("   api: {\r\n");
        sb.append("   read :'/salaryMis/"+tablename+"/read"+initcap2(tablename)+"?current"+initcap2(tablename)+"Date=' + new Date().pattern(\"yyyy-MM-dd\")\r\n");
        sb.append("},\r\n");
        sb.append("noCache: false,\r\n");
        sb.append("actionMethods: {\r\n");
        sb.append("    read:'POST'\r\n");
        sb.append("},\r\n");
        sb.append("reader: {\r\n");
        sb.append("    root: '"+tablename+"s',\r\n");
        sb.append("    totalProperty:\"totalCount\"\r\n");
        sb.append("},\r\n");
        sb.append("writer:{type:'json'}\r\n");
        sb.append("},\r\n");
        sb.append("listeners:{\r\n");
        sb.append("update:function(store,record){\r\n");
        sb.append("    Ext.Ajax.request({\r\n");
        sb.append("    url:'/salaryMis/"+tablename+"/update',\r\n");
        sb.append("    params:{\r\n");
        sb.append("         id : record.get(\"id\")\r\n");
        for(int i=0;i<list.size();i++){
            if(((DynamicTable)list[i]).fieldType=="日期"){
                sb.append("        ,"+((DynamicTable)list[i]).fieldNameId+":dateisnull(record.get('"+((DynamicTable)list[i]).fieldNameId+"'))\r\n")
            }else{
                sb.append("        ,"+((DynamicTable)list[i]).fieldNameId+":record.get('"+((DynamicTable)list[i]).fieldNameId+"')\r\n")
            }
        }
        sb.append("""    },
        success:function(resp,opts) {
            if(resp.responseText=='success'){
                   var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                   panelStore.loadPage(panelStore.currentPage);
            }else{
                   Ext.Msg.show({
                        title: '操作提示 ',
                        msg: '数据更新失败！ ',
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
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
                    """);
        return sb.toString();
    }



    def  initcap2(String str) {//不带路径不带盘符的名字第一个字母改为大写
        int i=0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0]-32);
        return new String(ch);
    }

}
