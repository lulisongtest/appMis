package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateSelectExportjs {
    def createSelectExportjs(String tablename){
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
Ext.define('salaryMis.view."""+tablename+""".SelectExport"""+initcap2(tablename)+"""Item', {
    extend: 'Ext.window.Window',
    xtype: 'selectExport"""+initcap2(tablename)+"""Item',
    id: 'selectExport"""+initcap2(tablename)+"""Item',
    width: 570,
    height: 500,
    title: '选择导出职工应发工资信息字段，右列初值是必选项',
    layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    requires: [
        //'Ext.ux.form.ItemSelector'
    ],
    initComponent: function () {
        this.items = [
           {
               xtype: 'itemselector',
               id:'itemselectorExport"""+initcap2(tablename)+"""',
               store: 'DynamicTableStore"""+initcap2(tablename)+"""',
               displayField: 'fieldName',
               valueField: 'fieldNameId',
               hideNavIcons: false,
               cls: 'aria-itemselector',
               reference: 'itemselector"""+initcap2(tablename)+"""',
              // fieldLabel: '请选择要导出的字段',
               allowBlank: false,
               msgTarget: 'side',
               fromTitle: '待选择字段',
               toTitle: '已选择字段'//,
              // value: ['employeeCode', 'name','gzDate']//已选择的初值
           }
        ];
         this.buttons=[
         {
            text: '导出',
            glyph:'xf0c7@FontAwesome',
            action: 'exportToExcel',
         },{
            text: '完成',
            glyph:'xf0c7@FontAwesome',
            scope: this,
            handler: this.close
         },{
            text: '放弃',
            glyph:'xf00d@FontAwesome',
            scope: this,
            handler: this.close
         }
         ]
        this.callParent(arguments);
    }
});
           """)
            FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\view\\"+tablename+"\\SelectExport"+initcap2(tablename)+"Item.js");
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
