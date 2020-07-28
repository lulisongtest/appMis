package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class ModifyDataTreeStorejs {
    def modifyDataTreeStorejs(String tablename){
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
        def list = DynamicTable.findAll(query)
        try {
            FileReader fr = new FileReader("d:\\salaryMis\\web-app\\app\\store\\DataTreeStore.js");
            StringBuilder sb=new StringBuilder();
            String line=null;
            String line1=null;
            while((line=fr.readLine()) != null) {
                sb.append(line+"\n");
                if(line.indexOf(initcap2(((DynamicTable)list[0]).tableName))>0){
                    fr.close();
                    return    //已有该表的菜单项，则不修改文件，退出
                }
                if(line.trim()=="children:["){
                    line=fr.readLine()
                    if(line.indexOf(initcap2(((DynamicTable)list[0]).tableName))>0){
                        fr.close();
                        return  //已有该表的菜单项，则不修改文件，退出
                    }
                    sb.append("                   {text:'"+(((DynamicTable)list[0]).tableName).trim()+"',\r\n");
                    line1=fr.readLine()
                    if(line1.trim()==""){
                        // println("Wonder!!!!!!!!!!!!!!")//已经有该项目时，不知为什么第一次读是空行的。要读二次才读出？？
                        line1=fr.readLine()
                    }
                    sb.append("                       id:'"+ (Integer.parseInt((line1.split("'"))[1])+1)+"',\r\n")
                    sb.append("                       cls:'x-btn-text-icon',\r\n")
                    sb.append("                       icon:'images/application/application_form_edit.png',\r\n")
                    sb.append("                       leaf:true\r\n")
                    sb.append("                    },\r\n")
                    sb.append(line+"\n");
                    sb.append(line1+"\n");
                };
            }
            fr.close();
            FileWriter fw = new FileWriter("d:\\salaryMis\\web-app\\app\\store\\DataTreeStore.js");
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
