package com.app
import com.app.DynamicTable
import grails.web.context.ServletContextHolder

/**
 * Created by Adminlinken on 14-5-31.
 */
class ModifyAppjs {
    def modifyAppjs(String tablename){
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
        def list = DynamicTable.findAll(query)
        String fileName = ServletContextHolder.getServletContext().getRealPath("/")
        fileName=fileName.split("src")[0]+"grails-app\\assets\\extjsapps\\app.js"
        println("filePath==="+filePath)

        try {
            FileReader fr = new FileReader(fileName);
            StringBuilder sb=new StringBuilder();
            String line=null;
            while((line=fr.readLine()) != null) {
                if(line.trim()==""){
                    continue
                }
                sb.append(line+"\n");
                if(line.trim()=="controllers:["){
                    sb.append("        '"+initcap2(tablename)+"',\r\n")
                };
                if(line.trim()=="stores:["){
                    sb.append("        DynamicTableStore'"+initcap2(tablename)+"Store',\r\n")
                    sb.append("        '"+initcap2(tablename)+"Store',\r\n")
                    sb.append("        '"+initcap2(tablename)+"Store1',\r\n")
                    sb.append("        '"+initcap2(tablename)+"Store2',\r\n")
                    sb.append("        '"+initcap2(tablename)+"Store3',\r\n")
                };
                if(line.trim()=="views:["){
                    sb.append("      '"+tablename+"."+initcap2(tablename)+"Add',\r\n");
                    sb.append("      '"+tablename+"."+initcap2(tablename)+"Edit',\r\n");
                    sb.append("      '"+tablename+"."+initcap2(tablename)+"Viewport',\r\n");
                    sb.append("      '"+tablename+"."+initcap2(tablename)+"GridViewport',\r\n");
                }
            }
            fr.close();
            FileWriter fw = new FileWriter(fileName);
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
