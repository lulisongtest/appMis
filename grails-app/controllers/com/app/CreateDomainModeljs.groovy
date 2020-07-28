package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateDomainModeljs {
    List list
   def createDomainModeljs(String tablename){
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
         list = DynamicTable.findAll(query)
            String content = parseModeljs(tablename);
            try {
                FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\model\\"+initcap2(tablename)+"Model.js");
                PrintWriter pw = new PrintWriter(fw);
                pw.println(content);
                pw.flush();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    def  parseModeljs(String tablename) {
            StringBuffer sb = new StringBuffer();
            sb.append("Ext.define('salaryMis.model." + initcap2(tablename) + "Model', {\r\n extend: 'Ext.data.Model', \r\nrequires: ['Ext.data.Field'],\r\n");
            sb.append("fields: [\r\n'id'")
            for(int i=0;i<list.size();i++){
                if(((DynamicTable)list[i]).fieldType=="日期"){
                    sb.append(" ,\r\n{name : '"+((DynamicTable)list[i]).fieldNameId+"', dateFormat: 'Y-m-d',type : 'date'}\r\n")
                }else{
                    sb.append(",'"+((DynamicTable)list[i]).fieldNameId+"'");
                }
            }
            sb.append("\r\n]});\r\n");
            return sb.toString();
        }

    def  initcap(String str) {//不带路径带盘符的名字第一个字母改为大写
        int i=0;
        char[] ch = str.toCharArray();
        for(i=ch.length-1;i>0;i--)	if(ch[i]=='\\')break;
        if (ch[i+1] >= 'a' && ch[i+1] <= 'z') ch[i+1] = (char) (ch[i+1]-32);
        return new String(ch);
    }
    def initcap1(String str) {//带路径的名字第一个字母改为大写
        int i=0;
        char[] ch = str.toCharArray();
        for(i=ch.length-1;i>0;i--)	if(ch[i]=='\\')break;
        if (ch[i+1] >= 'a' && ch[i+1] <= 'z') ch[i+1] = (char) (ch[i+1]-32);
        return (new String(ch)).substring(i+1, ch.length);
    }

    def  initcap2(String str) {//不带路径不带盘符的名字第一个字母改为大写
        int i=0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0]-32);
        return new String(ch);
    }
}
