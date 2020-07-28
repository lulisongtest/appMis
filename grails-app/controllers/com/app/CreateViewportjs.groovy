package com.app
import com.app.DynamicTable
/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateViewportjs {
    def createViewportjs(String tablename){
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
Ext.define('salaryMis.view."""+tablename+"""."""+initcap2(tablename)+"""Viewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget."""+tablename+"""viewport',
    defaults: {
        autoScroll: true,
        bodyPadding: 0,
        border: 0,
        padding: 0
    },
    items: [
        {
            xtype: 'toolbar',
            height: 40,
            items: [
                {
                    xtype: 'datefield',
                    id: 'current"""+initcap2(tablename)+"""Date',
                    fieldLabel: '当前日期',
                    format: 'Y年m月d日',
                    labelAlign: 'right',
                    width: 245,
                    value: new Date(),
                    labelWidth: 90,
                    listeners: {
                        change: function (combo) {
                            var newvalue1 = (new Date(Ext.getCmp('current"""+initcap2(tablename)+"""Date').getValue()).pattern("yyyy-MM-dd"));
                            var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                            panelStore.proxy.api.read = '"""+tablename+"""/read"""+initcap2(tablename)+"""?current"""+initcap2(tablename)+"""Date=' + newvalue1;
                            panelStore.loadPage(1);
                        }
                    }
                },  {
                    text: '删除单位当前月数据',
                    icon: 'tupian/cancel.png',
                    action: 'deleteMonth"""+initcap2(tablename)+"""'
                }, {
                    text: '新增',
                    icon: 'images/tupian/edit_add.png',
                    action: 'add'
                }, {
                    text: '增加行',
                    icon: 'tupian/edit_remove.png',
                    action: 'addRow'
                }, {
                    text: '修改',
                    icon: 'tupian/pencil.png',
                    action: 'modify'
                }
            ]
        },
        {
            xtype: 'toolbar',
            height: 40,
            items: [
                {
                    text: '数据导出',
                    icon: 'tupian/exportToExcel.png',
                    // action: 'exportToExcel'
                    handler: function (button, e) {
                        view = Ext.create('salaryMis.view."""+tablename+""".SelectExport"""+initcap2(tablename)+"""Item')
                    }
                }, {
                    text: '数据导入',
                    icon: 'tupian/exportToExcel.png',
                    //action: 'importFromExcel'
                    handler: function (button, e) {
                    if (Ext.getCmp('"""+tablename+"""excelfilePath').getValue() == '') {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '请选择你要上传的文件',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 2500);
                            return;
                        }
                        var s=(""+Ext.getCmp('"""+tablename+"""excelfilePath').getValue()).toLowerCase();
                        if((s.substring(s.length-3)!='xls')&&(s.substring(s.length-4)!='xlsx')){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '文件类型不对，请重新选择你要上传的文件！',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 2500);
                            return;
                        }
                        view = Ext.create('salaryMis.view."""+tablename+""".SelectImport"""+initcap2(tablename)+"""Item')
                    }
                }, {
                    xtype: 'form',
                    height: 40,
                    id: 'SelectImport"""+initcap2(tablename)+"""ItemForm',
                    // baseCls: 'x-plain',
                    url: "tmp",//上传服务器的地址
                    fileUpload: true,
                    //defaultType: 'textfield',
                    items: [{
                        xtype: 'fileuploadfield',
                        width: 180,
                        labelWidth: 0,
                        // labelAlign: 'right',
                        //fieldLabel: '选择文件',
                        name: '"""+tablename+"""excelfilePath',
                        id: '"""+tablename+"""excelfilePath',
                        buttonText: '选择文件',
                        blankText: '文件名不能为空'
                    }]
                },{
                    xtype: 'textfield',
                    id: 'keyword"""+tablename+"""',
                    fieldLabel: '关键字',
                    labelAlign: 'right',
                    width: 163,
                    labelWidth: 45
                }, {
                    xtype: 'button',
                    width: 70,
                    action: 'query',
                    icon: 'images/tupian/search.png',
                    handler: function (button, e) {
                        var newvalue = Ext.getCmp('keyword"""+tablename+"""').getValue();
                        var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                        panelStore.proxy.api.read = '"""+tablename+"""/search?q=' + newvalue;
                        panelStore.loadPage(1);
                    },
                    text: '搜索'
                }
            ]
        },
        {
            xtype: 'panel',
            height: 420,
            items: [
                {
                    xtype: '"""+tablename+"""gridviewport',
                    id: '"""+tablename+"""grid',
                    listeners: {
                        beforerender: function () {
                            this.height = 410;
                            (Ext.getCmp('"""+tablename+"""grid')).setStore('"""+tablename+"""Store');
                            (Ext.getCmp('bbar"""+tablename+"""')).setStore('"""+tablename+"""Store');//分页
                            var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                            panelStore.sort('employeeCode', 'ASC');//此命令还会向后台发送一次请求排序数据
                            panelStore.loadPage(1);
                        }
                    }
                }]
        }]
});
 """)
            FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\view\\"+tablename+"\\"+initcap2(tablename)+"Viewport.js");
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
