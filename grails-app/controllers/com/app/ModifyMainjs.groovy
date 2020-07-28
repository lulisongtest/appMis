package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class ModifyMainjs {
    def modifyMainjs(String tablename){
        int treeId=-1//TreeId树结点号，设置无需修改Main.js文件的标志
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
        def list = DynamicTable.findAll(query)
        try {
            FileReader fr = new FileReader("d:\\salaryMis\\web-app\\app\\view\\main\\MainController.js");
            StringBuilder sb=new StringBuilder();
            String line=null,line1=null,line2=null,line3=null,line4=null,line5=null;
            while((line=fr.readLine()) != null) {
                if(line.trim()==""){line=fr.readLine();if(line==null)break;}
                if(line.indexOf(initcap2(((DynamicTable)list[0]).tableNameId)+'Viewport')>0){//已有该表的项目则不修改
                    fr.close();
                    return -1//无需修改Main.js文件
                }
                sb.append(line+"\n");
                if(line.trim()=="switch((record.get('text'))){"){
                    line=fr.readLine();if(line.trim()==""){line=fr.readLine();if(line==null)break;}
                    //treeId=(Integer.parseInt((line.trim().split(" "))[1])+1)//获取TreeId树结点号
                    treeId=0
                    sb.append("                    case '"+((DynamicTable)list[0]).tableName+"' ://新数据表\r\n")
                    line1=fr.readLine();if(line1.trim()==""){line1=fr.readLine();if(line1==null)break;}// println("Wonder!!!!!!!!!!!!!!")//已经有该项目时，不知为什么第一次读是空行的。要读二次才读出？？
                    if(line1.indexOf(initcap2(((DynamicTable)list[0]).tableNameId)+'Viewport')>0){//已有该表的项目则不修改
                        fr.close();
                        return -1//无需修改Main.js文件
                    }
                    sb.append("                            var  panel"+((DynamicTable)list[0]).tableName+"=Ext.create('salaryMis.view."+tablename+"."+initcap2(((DynamicTable)list[0]).tableNameId)+"Viewport',{\r\n")
                    sb.append("                                     title:record.get('text'),\r\n")
                    sb.append("                                     id: record.get('id')+'p',\r\n")
                    sb.append("                                     cls:'x-btn-text-icon',\r\n")
                    sb.append("                                     icon:'images/application/application_form.png',\r\n")
                    sb.append("                                     iconCls: 'tabs',\r\n")
                    sb.append("                                     closable: true\r\n")
                    sb.append("                            });\r\n")
                    sb.append("                            main1.add(panel"+((DynamicTable)list[0]).tableName+");\r\n")
                    sb.append("                            main1.setActiveTab(panel"+((DynamicTable)list[0]).tableName+");\r\n")
                    sb.append("                            break;\r\n")
                    sb.append(line+"\n");
                    sb.append(line1+"\n");
                };
            }
            fr.close();
            FileWriter fw = new FileWriter("d:\\salaryMis\\web-app\\app\\view\\main\\MainController.js");
            PrintWriter pw = new PrintWriter(fw);
            pw.println(sb);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return treeId
    }
    def  initcap2(String str) {//不带路径不带盘符的名字第一个字母改为大写
        int i=0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0]-32);
        return new String(ch);
    }
}
