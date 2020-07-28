package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateDynamicTableStorejs {
    List list
    def createDynamicTableStorejs(String tablename){
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
        list = DynamicTable.findAll(query)
        String content = parseStorejs(tablename);
        try {
            FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\store\\DynamicTableStore"+initcap2(tablename)+".js");
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
        sb.append("""
Ext.define('salaryMis.store.DynamicTableStore"""+initcap2(tablename)+"""', {
    extend: 'Ext.data.Store',
    singleton: false,
    requires: [
        'salaryMis.model.DynamicTableModel',
        'Ext.grid.*',
        'Ext.toolbar.Paging',
        'Ext.data.*'
    ],
    pageSize:10,
    remoteSort: true,
    constructor: function(cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            model: 'salaryMis.model.DynamicTableModel',
            storeId: 'DynamicTableStore"""+initcap2(tablename)+"""',
            autoSync : true,//这样修改完一个单元格后会自动保存数据
            autoLoad:  true,//是否与上一句是一个功能！？
            proxy: {
                type: 'ajax',
                api: {
                    create: 'dynamicTable/save',
                    read :'dynamicTable/readDynamicTableSelect?tableNameId="""+tablename+"""'
                },
                noCache: false,
                actionMethods: {
                    create : 'POST',
                    read   : 'POST'
                },
                reader: {
                    type: 'json',
                    root: 'dynamictables',
                    totalProperty:"totalCount"
                },
                writer:{
                    type:'json'
                }
            }
        }, cfg)]);
    }
}); """);
        return sb.toString();
    }



    def  initcap2(String str) {//不带路径不带盘符的名字第一个字母改为大写
        int i=0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0]-32);
        return new String(ch);
    }

}
