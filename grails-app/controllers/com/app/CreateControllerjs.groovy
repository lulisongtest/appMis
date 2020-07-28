package com.app

import com.app.DynamicTable

/**
 * Created by Adminlinken on 14-5-31.
 */
class CreateControllerjs {
    def createControllerjs(String tablename){
        String query="from DynamicTable where tableNameId='"+ tablename+"'"
        def list = DynamicTable.findAll(query)
        try {
            StringBuilder sb=new StringBuilder();
            sb.append("""
            var currentRecord=0
            var view=null
            var """+tablename+"""first=1//第一次或重新打开form
            Ext.define('salaryMis.controller."""+initcap2(tablename)+"""',{
                extend:'Ext.app.Controller',
                init:function(){
                    this.control({
                        'selectExport"""+initcap2(tablename)+"""Item   button[action=exportToExcel]':{//选择导出信息项目
                         click: this.exportExcel"""+initcap2(tablename)+"""
                       },
                        'selectImport"""+initcap2(tablename)+"""Item   button[action=importFromExcel]':{//选择导入信息项目
                         click: this.importExcel"""+initcap2(tablename)+"""
                       },

                        '"""+tablename+"""edit  button[action=save]': {
                            click: this.update"""+initcap2(tablename)+"""
                        },
                         '"""+tablename+"""add  button[action=save]': {
                            click: this.save"""+initcap2(tablename)+"""
                        },
                         '"""+tablename+"""viewport > toolbar >  button[action=add]': {
                            click: this.add"""+initcap2(tablename)+"""
                        },
                        '"""+tablename+"""viewport > toolbar >  button[action=addRow]': {
                            click: this.addRow"""+initcap2(tablename)+"""
                        },
                         '"""+tablename+"""viewport > toolbar >  button[action=addTodayBasicData]': {
                            click: this.addTodayBasicData"""+initcap2(tablename)+"""
                        },
                         '"""+tablename+"""viewport > toolbar >  button[action=modify]': {
                            click: this.modify"""+initcap2(tablename)+"""
                        },
                         '"""+tablename+"""viewport > toolbar >  button[action=delete]': {
                            click: this.delete"""+initcap2(tablename)+"""
                        },
                         '"""+tablename+"""viewport > toolbar >  button[action=batchDelete]': {
                            click: this.batchDelete"""+initcap2(tablename)+"""
                        },
                          '"""+tablename+"""viewport > toolbar >  button[action=generateTodayReport]': {
                            click: this.generateTodayReport"""+initcap2(tablename)+"""
                        },
                          '"""+tablename+"""viewport > toolbar >  button[action=exportReportToExcel]': {
                            click: this.exportReportToExcel"""+initcap2(tablename)+"""
                        },
                        '"""+tablename+"""viewport > toolbar >  button[action=exportToExcel]': {
                            click: this.exportExcel"""+initcap2(tablename)+"""
                        },
                        '"""+tablename+"""viewport > toolbar >  button[action=importFromExcel]': {
                            click: this.importExcel"""+initcap2(tablename)+"""
                        },
                         '"""+tablename+"""viewport > toolbar >  button[action=deleteMonth"""+initcap2(tablename)+"""]': {
                            click: this.deleteMonth"""+initcap2(tablename)+"""
                        }
                    })
                },

//从Excel表导入到数据库
 importExcel"""+initcap2(tablename)+""":function(button){
        var fieldItem=Ext.getCmp('itemselectorImport"""+initcap2(tablename)+"""').getValue()
        win_select=Ext.getCmp('selectImport"""+initcap2(tablename)+"""Item')
        //var panel1 = button.up.up('panel');
        form = Ext.getCmp('SelectImport"""+initcap2(tablename)+"""ItemForm');
        if (form.form.isValid()) {
            if (Ext.getCmp('"""+tablename+"""excelfilePath').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传的文件');
                return;
            }
            //alert("Ext.getCmp('"""+tablename+"""excelfilePath').getValue()===="+Ext.getCmp('"""+tablename+"""excelfilePath').getValue())
            form.getForm().submit({
                url: '"""+tablename+"""/selectImport"""+initcap2(tablename)+"""?current"""+initcap2(tablename)+"""Date=' + new Date(Ext.getCmp('current"""+initcap2(tablename)+"""Date').getValue()).pattern("yyyy-MM-dd"),
                method: "POST",
                params : {fieldItem:fieldItem},//要导入的项目
                waitMsg: '正在处理',
                waitTitle: '请等待',
                success: function (fp, o) {
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: "" + o.result.info + "",
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        Ext.Msg.hide();
                        var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                        //panelStore.loadPage(parseInt(panelStore.totalCount / panelStore.pageSize + 1));//定位grid到最后一页
                        panelStore.loadPage(1);//定位grid到第一页
                        win_select.close()
                    }, 2500)
                },
                failure: function (fp, o) {
                    //alert("警告", "" + o.result.info + "");
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: "" + o.result.info + "",
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        Ext.Msg.hide();
                    }, 2500)
                }

            })
        }
},

//把当前选择的数据导出到Excel表中
exportExcel"""+initcap2(tablename)+""": function (record) {
        // var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在把当前选择的数据导出到Excel表中，请等待..."});
        //myMask.show();
        var myMask = new Ext.LoadMask(Ext.getCmp('itemselectorExport"""+initcap2(tablename)+"""'), {msg:"正在把当前选择的数据导出到Excel表中，请等待..."});
        myMask.show();
        var fieldItem=Ext.getCmp('itemselectorExport"""+initcap2(tablename)+"""').getValue()
        if (Ext.getCmp('current"""+initcap2(tablename)+"""Date').getValue() == "") {
            Ext.MessageBox.show({title: "提示", msg: "请先选择您要导出单位当前月工资的日期!", buttons: Ext.MessageBox.OK})
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        Ext.Ajax.request({
            url: '"""+tablename+"""/selectExport"""+initcap2(tablename)+"""?current"""+initcap2(tablename)+"""Date=' + new Date(Ext.getCmp('current"""+initcap2(tablename)+"""Date').getValue()).pattern("yyyy-MM-dd"),
            method: 'POST',
            params : {fieldItem:fieldItem},
            success://回调函数
                function (resp, opts) {
                    {
                        myMask.destroy()
                        Ext.MessageBox.buttonText.ok = "完成"
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "<a href=\\"http://" + window.location.host + "/salaryMis/tmp/" + resp.responseText + ".xls\\"><b>保存该EXCEL文件</b></a>",
                            align: "centre",
                            buttons: Ext.MessageBox.OK
                        })
                    }
                },
            failure: function (resp, opts) {
                // myMask.hide();
                myMask.destroy()
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据导出到Excel失败！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 1500)
            }
        });
},
//双击grid中的某个记录进行修改
edit"""+initcap2(tablename)+""":function(grid, record){
                    var view = Ext.widget('"""+tablename+"""edit');
                    view.down('form').loadRecord(record);
},

//按钮按下对grid中已选择的记录进行修改
modify"""+initcap2(tablename)+""":function(){
        currentRecord=0
        """+tablename+"""first=1//第一次或重新打开form
        records = (Ext.getCmp('"""+tablename+"""grid')).getSelectionModel().getSelection();//不能用 var records 可能是局部变量，其它方法取不到值。
        if (records.length == 0) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请选择您要修改的数据!（注：必须至少选择一条数据）",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
         view = Ext.widget('"""+tablename+"""edit');//编辑窗口中的记录与Grid中选中的记录是同一条记录
       // view.down('form').loadRecord(record[0]);
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图
       // view.down('form').loadRecord(records[1]);//显示编辑视图
    },

    first"""+initcap2(tablename)+""":function(button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord=0
        view.down('form').loadRecord(records[0]);//显示编辑视图
    },
    preview"""+initcap2(tablename)+""":function(button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord=currentRecord-1
        if(currentRecord==-1)currentRecord=0
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图
    },
    next"""+initcap2(tablename)+""":function(button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord=currentRecord+1
        if(currentRecord==records.length)currentRecord=records.length-1
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图

    },
    last"""+initcap2(tablename)+""":function(button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord=records.length-1
        view.down('form').loadRecord(records[records.length-1]);//显示编辑视图
    },

//编辑记录后进行保存更新
update"""+initcap2(tablename)+""":function(button) {
                    var win    = button.up('window');
                    form   = win.down('form');
                    record = form.getRecord();
                    values = form.getValues();
                    values.gzDate=(values.gzDate).replace(/(年)|(月)/g,"-")
                    values.gzDate=(values.gzDate).replace(/(日)/g,"")
                   // values.useDate=(values.useDate).replace(/(年)|(月)/g,"-")
                    //values.useDate=(values.useDate).replace(/(日)/g,"")
                     if("""+tablename+"""first=1){
                          record.set(values);//因为此时与数据表记录关联,第一次保存可以激活Store的update:function(store,record)，第二次保存就不激活Store的update:function(store,record)，
                          """+tablename+"""first=0
                    }else{
                    """)
            sb.append("    Ext.Ajax.request({\r\n");
            sb.append("                        url:'"+tablename+"/update',\r\n");
            sb.append("                        params:{\r\n");
            sb.append("                            id : record.get(\"id\")\r\n");
            for(int i=0;i<list.size();i++){
                if(((DynamicTable)list[i]).fieldType=="日期"){
                    sb.append("                            ,"+((DynamicTable)list[i]).fieldNameId+":(new Date(record.get('"+((DynamicTable)list[i]).fieldNameId+"'))).pattern(\"yyyy-MM-dd\")+\" 00:00:00.0\"\r\n")
                }else{
                    sb.append("                            ,"+((DynamicTable)list[i]).fieldNameId+":record.get('"+((DynamicTable)list[i]).fieldNameId+"')\r\n")
                }
            }
            sb.append("""                        },
                        success: function(resp, opts) {
                                 if (resp.responseText == 'success') {
                                     var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                                     panelStore.loadPage(panelStore.currentPage);
                                 } else {
                                     Ext.Msg.show({
                                         title: '操作提示 ',
                                         msg: '数据更新失败！ ',
                                         buttons: Ext.MessageBox.OK
                                     })
                                     setTimeout(function() {
                                         var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                                         panelStore.loadPage(panelStore.currentPage);
                                         Ext.Msg.hide();
                                     }, 1500);
                                 }
                        }
                        });
                    }
                    win.close();
},

//打开新增加的部门输入窗口
add"""+initcap2(tablename)+""":function(){
        if((currentTreeNode=="p1000")||(currentTreeNode.length <=4)){
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '不能在分类名下增加信息，必须选择某个单位！！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500);
                return
        }
        var record = Ext.create('salaryMis.model.""" + initcap2(tablename) + """Model', { \r\n""")
            for (int i = 0; i < list.size(); i++) {
                if (i != (list.size() - 1)) {
                    if (((DynamicTable) list[i]).fieldType == "日期") {
                        sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":new Date(),\r\n")
                    } else {
                        if (((DynamicTable) list[i]).fieldType == "数值") {
                            sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":'0',\r\n");
                        } else {
                            sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":' ',\r\n");
                        }
                    }
                } else {
                    if (((DynamicTable) list[i]).fieldType == "日期") {
                        sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":new Date()\r\n")
                    } else {
                        if (((DynamicTable) list[i]).fieldType == "数值") {
                            sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":'0'\r\n");
                        } else {
                            sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":' '\r\n");
                        }
                    }
                }
            }
            sb.append("""
                    });
        //设置初值
        var view = Ext.widget('"""+tablename+"""add');
        view.down('form').loadRecord(record);
    },


//保存新增加的部门
save"""+initcap2(tablename)+""":function(button){
        var win = button.up('window');
        form = win.down('form');
        record = form.getRecord();//创建的新记录
        values = form.getValues();
        values.gzDate = (values.gzDate).replace(/(年)|(月)/g, "-")
        values.gzDate = (values.gzDate).replace(/(日)/g, "")
        //values.gzDate = values.gzDate + "01"
        //values.useDate = (values.useDate).replace(/(年)|(月)/g, "-")
       // values.useDate = (values.useDate).replace(/(日)/g, "")
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        this.add(record, win)
        //win.close();
    },


///以增加行的方式增加数据
addRow"""+initcap2(tablename)+""":function(){
        if((currentTreeNode=="p1000")||(currentTreeNode.length <=4)){
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '不能在分类名下增加信息，必须选择某个单位！！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500);
                return
        }
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 2,
            autoCancel: false
        });
        record = Ext.create('salaryMis.model.""" + initcap2(tablename) + """Model', { \r\n""")
            for (int i = 0; i < list.size(); i++) {
                if (i != (list.size() - 1)) {
                    if (((DynamicTable) list[i]).fieldType == "日期") {
                        sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":(new Date()).pattern(\"yyyy-MM-dd\"),\r\n")
                    } else {
                        if (((DynamicTable) list[i]).fieldType == "数值") {
                            sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":'0',\r\n");
                        } else {
                            sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":' ',\r\n");
                        }
                    }
                } else {
                    if (((DynamicTable) list[i]).fieldType == "日期") {
                        sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":(new Date()).pattern(\"yyyy-MM-dd\"),\r\n")
                    } else {
                        if (((DynamicTable) list[i]).fieldType == "数值") {
                            sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":'0'\r\n");
                        } else {
                            sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":' '\r\n");
                        }
                    }
                }
            }
            sb.append("""
                    });
        this.add(record)//在最后增加一条记录
},

//insert和add都是调用它
add: function (record, win) {//insert和add都是调用它
        Ext.Ajax.request({
            url: '"""+tablename+"""/save',
            params:{\r\n""")
            for (int i = 0; i < list.size(); i++) {
                if (i != (list.size() - 1)) {
                    if (((DynamicTable) list[i]).fieldType == "日期") {
                        sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":(new Date(record.get('" + ((DynamicTable) list[i]).fieldNameId + "'))).pattern(\"yyyy-MM-dd\")+\" 00:00:00.0\",\r\n")
                    } else {
                        sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":record.get('" + ((DynamicTable) list[i]).fieldNameId + "'),\r\n")
                    }
                } else {
                    if (((DynamicTable) list[i]).fieldType == "日期") {
                        sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":(new Date(record.get('" + ((DynamicTable) list[i]).fieldNameId + "'))).pattern(\"yyyy-MM-dd\")+\" 00:00:00.0\"\r\n")
                    } else {
                        sb.append("                          " + ((DynamicTable) list[i]).fieldNameId + ":record.get('" + ((DynamicTable) list[i]).fieldNameId + "')\r\n")
                    }
                }
            }
            sb.append("""
                        },
            method: 'POST',
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    if (resp.responseText == 'success') {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
                            panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
                            Ext.Msg.hide();
                        }, 1500);
                        //win.close();//数据增加成功后再关闭窗口
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        }, 2500);
                    }
                },
            failure: function (resp, opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据增加失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 2500)
            }

        });
},

//按钮按下对grid中已选择的记录进行删除
batchDelete"""+initcap2(tablename)+""":function(){
                    if((Ext.getCmp('"""+tablename+"""query1').getValue()=="")||(Ext.getCmp('"""+tablename+"""query1').getValue()==null)||(Ext.getCmp('"""+tablename+"""query1').getValue()=='全部')){
                        Ext.MessageBox.show({
                            title:"提示",
                            msg:"请先选择您要批量删除数据的生产日期!",
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },2500);
                        return;

                    }else{
                        newvalue1= (new Date(Ext.getCmp('"""+tablename+"""query1').getValue()).pattern("yyyy-MM-dd"));
                        var myMask = new Ext.LoadMask(Ext.getCmp('"""+tablename+"""grid'), {msg:"正在数据批量删除，请等待..."});
                        myMask.show();
                        Ext.Ajax.timeout=9000000;  //9000秒
                        Ext.Ajax.request({
                            url:'"""+tablename+"""/batchDelete?newvalue1='+newvalue1,
                            method:'POST',
                            success:
                                function(resp,opts) {//成功后的回调方法
                                    if(resp.responseText=='success'){
                                        myMask.hide();
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            msg: '数据批量删除成功！ ',
                                            buttons: Ext.MessageBox.OK
                                        })
                                        setTimeout(function () {
                                            Ext.getCmp('bbar"""+tablename+"""').getStore().reload()
                                            Ext.Msg.hide();
                                        },1500);

                                    }else{
                                        myMask.hide();
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            msg: '数据批量删除失败！ ',
                                            buttons: Ext.MessageBox.OK
                                        })
                                        setTimeout(function () {
                                            Ext.Msg.hide();
                                        },1500);
                                    }
                                },
                            failure: function(resp,opts) {
                                myMask.hide();
                                Ext.Msg.show({
                                    title: '操作提示 ',
                                    msg: '数据删除失败！ ',
                                    buttons: Ext.MessageBox.OK
                                })
                                setTimeout(function () {
                                    Ext.Msg.hide();
                                },1500)
                            }
                        })
                    }
},

//按钮按下对grid中已选择的记录进行删除
delete"""+initcap2(tablename)+""":function(){
        var record = (Ext.getCmp('"""+tablename+"""grid')).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
        var currPage = panelStore.currentPage;
        if (record.length == 0) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请先选择您要删除的行数据!",
                buttons: Ext.MessageBox.OK
                //icon: Ext.MessageBox.INFO
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        } else {
            for (var i = 0; i < record.length; i++) {
                this.remove(panelStore, record[i])//删除选中的记录
            }
            panelStore.loadPage(currPage);//刷新当前grid页面
            if (((panelStore.totalCount - record.length) % panelStore.pageSize) == 0) {
                currPage = currPage - 1
                if (currPage == 0)currPage = 1
            }
            panelStore.loadPage(currPage);//刷新当前grid页面
            Ext.MessageBox.show({
                title: "提示",
                msg: "成功删除" + record.length + "行数据!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 1500);

        }
},


remove: function (store, record) {
        Ext.Ajax.request({
            url:'"""+tablename+"""/delete?id='+record.get("id"),
            method: 'POST',
            success: function (resp, opts) {//成功后的回调方法
                if (resp.responseText == 'success') {
                    Ext.getCmp("bbar"""+initcap2(tablename)+"""").getStore().reload()
                } else {
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: '数据删除失败！ ',
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        Ext.Msg.hide();
                    }, 1500);
                }
            },
            failure: function (resp, opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据删除失败！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 1500)
            }
        })
},
  deleteMonth"""+initcap2(tablename)+""": function (button) {//删除单位当前月数据
        if (Ext.getCmp('current"""+initcap2(tablename)+"""Date').getValue() == "") {
            Ext.MessageBox.show({title: "提示", msg: "请先选择您要删除单位当前月数据的日期!", buttons: Ext.MessageBox.OK})
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        var panelStore = (Ext.getCmp('"""+tablename+"""grid')).getStore();
        var myMask = new Ext.LoadMask(Ext.getCmp('"""+tablename+"""grid'), {msg: "正在删除单位当前月数据，请等待..."});
        myMask.show();
        Ext.Ajax.timeout = 9000000;  //9000秒
        Ext.Ajax.request({
            url: '"""+tablename+"""/deleteMonth"""+initcap2(tablename)+"""?current"""+initcap2(tablename)+"""Date=' + new Date(Ext.getCmp('current"""+initcap2(tablename)+"""Date').getValue()).pattern("yyyy-MM-dd"),
            method: 'POST',
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    if (resp.responseText == 'success') {
                        myMask.hide();
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '删除单位当前月数据成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            panelStore.loadPage(panelStore.currentPage);
                            Ext.Msg.hide();
                        }, 1500);
                    } else {
                        if (resp.responseText == 'failure1') {
                            myMask.hide();
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '没有单位当前月数据了，删除单位当前月数据失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 1700);

                        } else {
                            myMask.hide();
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '删除单位当前月数据失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 1500);
                        }
                    }
                },
            failure: function (resp, opts) {
                myMask.hide();
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '删除单位当前月数据失败！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 1500)
            }
        });
    }
});""")
            FileWriter fw = new FileWriter("D:\\myPro\\salaryMis\\grails-app\\assets\\extjsapps\\app\\controller\\"+initcap2(tablename)+".js");
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
