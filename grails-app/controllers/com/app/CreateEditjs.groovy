package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateEditjs {
    def createEditjs(String tablename){
            String query="from DynamicTable where tableNameId='"+ tablename+"'"
            def list = DynamicTable.findAll(query)
            try{
                String filePath="D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\view\\"+tablename//如没有目录则创建目录
                File myFilePath=new File(filePath);
                if (!myFilePath.exists()){
                    myFilePath.mkdir()
                }
            }catch(Exception e){}
            try {
                StringBuilder sb=new StringBuilder();
                sb.append("""
Ext.define('salaryMis.view."""+tablename+"""."""+initcap2(tablename)+"""Edit' ,{
    extend: 'Ext.window.Window',
    alias : 'widget."""+tablename+"""edit',
    title : '修改"""+list[0].tableName+"""信息',
   width:1100,
    x:260,
    y: 190,
    //layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    initComponent: function() {
        this.items = [
           {
                xtype: 'form',
                height: 450,
                width:1050,
                layout: 'column',
                defaults: {
                    margins: '0 0 0 0',
                    padding:5
                },
                items: [
           """)
                for(int i=0;i<list.size();i++){
                    if(list[i].fieldType=="日期"){
                        sb.append("""         {
                        xtype: 'datefield',
                        columnWidth:.25,
                        format:'Y年m月d日',
                        name : '"""+((DynamicTable)list[i]).fieldNameId+"""',
                        fieldLabel:'"""+((DynamicTable)list[i]).fieldName+"""',
                        labelWidth:110,
                        labelStyle:'padding-left:10px',
                        emptyText: '请输入',
                        allowBlank:false,
                        blankText:"不能为空，请输入"
                    }""")
                    }else{
                        sb.append("""                    {
                        xtype: 'textfield',
                        columnWidth:.25,
                        name : '"""+((DynamicTable)list[i]).fieldNameId+"""',
                        fieldLabel:'"""+((DynamicTable)list[i]).fieldName+"""',
                        labelWidth:110,
                        labelStyle:'padding-left:10px',
                        emptyText: '请输入',
                        allowBlank:false,
                        blankText:"不能为空，请输入"
                    }""")
                    }
                    if(i!=list.size()-1) sb.append(",\r\n")//最后一项的末尾不加逗号
                }
                sb.append("""
            ]
           }
        ];
        this.buttons = [
            {
                text: '保存',
                action: 'save'
            },
            {
                text: '取消',
                scope: this,
                handler: this.close
            }
        ];
        this.callParent(arguments);
    }
});
            """)
                FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\view\\"+tablename+"\\"+initcap2(tablename)+"Edit.js");
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