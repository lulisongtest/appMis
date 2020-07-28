package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateDomainStore2js {
    List list
    def createDomainStore2js(String tablename){
           String query="from DynamicTable where tableNameId='"+ tablename+"'"
           list = DynamicTable.findAll(query)
        String content = parseStore2js(tablename);
        try {
            FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\store\\"+initcap2(tablename)+"Store2.js");
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    def  parseStore2js(String tablename) {
        StringBuffer sb = new StringBuffer();
        sb.append("Ext.define('salaryMis.store." + initcap2(tablename) + "Store2', { extend: 'Ext.data.Store',singleton: false,\r\n")
        sb.append("requires: ['salaryMis.model."+initcap2(tablename)+"Model',");
        sb.append("'Ext.grid.*','Ext.toolbar.Paging','Ext.data.*'], \r\n");
        sb.append("pageSize:10,\r\nremoteSort: true,\r\nconstructor: function(cfg) {\r\nvar me = this; \r\ncfg = cfg || {};\r\n me.callParent([Ext.apply({\r\nmodel: 'salaryMis.model."+initcap2(tablename)+"Model',\r\nstoreId: '" + initcap2(tablename) +"Store2',\r\nautoLoad:  true,")
        sb.append("proxy: {\r\n");
        sb.append("   type: 'ajax',\r\n");
        sb.append("   api: {\r\n");
        sb.append("   read :'/salaryMis/"+tablename+"/read"+initcap2(tablename)+"Names2'\r\n");

        sb.append("""
        },
        noCache:false,actionMethods:{read:'POST'},
        reader:{
             root:'"""+tablename+"""s',
            totalProperty:"totalCount"
        }}}, cfg)]);}});
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
