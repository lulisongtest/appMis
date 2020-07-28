var currentRecord = 0;
var view = null;
var rulesfirst = 1;//第一次或重新打开form
Ext.define('appMis.controller.Rules', {
    extend: 'Ext.app.Controller',
    init: function () {
        this.control({
            /*'exportRules   button[action=exportToFile]': {//选择导出信息项目
             click: this.exportExcelRules
             },*/
            'importRules   button[action=importFromFile]': {//选择导入信息项目
                click: this.importFromFile
            },

            'rulesedit  button[action=save]': {
                click: this.updateRules
            },
            'rulesadd  button[action=save]': {
                click: this.saveRules
            },
            'rulesviewport > toolbar >  button[action=add]': {
                click: this.addRules
            },
            'rulesviewport > toolbar >  button[action=addRow]': {
                click: this.addRowRules
            },
            'rulesviewport > toolbar >  button[action=modify]': {
                click: this.modifyRules
            },
            'rulesviewport > toolbar >  button[action=delete]': {
                click: this.deleteRules
            }
            /* 'rulesviewport > toolbar >  button[action=batchDelete]': {
             click: this.batchDeleteRules
             },
             'rulesviewport > toolbar >  button[action=generateTodayReport]': {
             click: this.generateTodayReportRules
             },
             'rulesviewport > toolbar >  button[action=exportReportToExcel]': {
             click: this.exportReportToExcelRules
             },
             'rulesviewport > toolbar >  button[action=exportToExcel]': {
             click: this.exportExcelRules
             },
             'rulesviewport > toolbar >  button[action=importFromExcel]': {
             click: this.importExcelRules
             },
             'rulesviewport > toolbar >  button[action=deleteMonthRules]': {
             click: this.deleteMonthRules
             }*/
        })
    },

//从Excel表导入到数据库
    importFromFile: function (button) {
        //var fieldItem = Ext.getCmp('itemselectorImportRules').getValue()
        win = Ext.getCmp('importRules')
        //var panel1 = button.up.up('panel');
        form = Ext.getCmp('importForm1');
        if (form.form.isValid()) {
            if (Ext.getCmp('rulesfilePath1').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传的文件');
                return;
            }
            //alert("Ext.getCmp('rulesfilePath').getValue()====="+Ext.getCmp('rulesfilePath').getValue())
            form.getForm().submit({
                url: '/appMis/rules/importRules?recId='+rec.get('id')+'&titleCode='+rec.get('titleCode'),
                method: "POST",
                //  params: {fieldItem: fieldItem},//要导入的项目
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
                        var panelStore = (Ext.getCmp('rulesgrid')).getStore();
                        //panelStore.loadPage(parseInt(panelStore.totalCount / panelStore.pageSize + 1));//定位grid到最后一页
                        panelStore.loadPage(1);//定位grid到第一页
                        win.close()
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
    exportExcelRules: function (record) {
        // var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在把当前选择的数据导出到Excel表中，请等待..."});
        //myMask.show();
        var myMask = new Ext.LoadMask(Ext.getCmp('itemselectorExportRules'), {msg: "正在把当前选择的数据导出到Excel表中，请等待..."});
        myMask.show();
        var fieldItem = Ext.getCmp('itemselectorExportRules').getValue()
        if (Ext.getCmp('currentRulesDate').getValue() == "") {
            Ext.MessageBox.show({title: "提示", msg: "请先选择您要导出单位当前月工资的日期!", buttons: Ext.MessageBox.OK})
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        Ext.Ajax.request({
            url: '/appMis/rules/selectExportRules?currentRulesDate=' + new Date(Ext.getCmp('currentRulesDate').getValue()).pattern("yyyy-MM-dd"),
            method: 'POST',
            params: {fieldItem: fieldItem},
            success://回调函数
                function (resp, opts) {
                    {
                        myMask.destroy()
                        Ext.MessageBox.buttonText.ok = "完成";
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/tmp/" + resp.responseText + ".xls\"><b>保存该EXCEL文件</b></a>",
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
    editRules: function (grid, record) {
        var view = Ext.widget('rulesedit');
        view.down('form').loadRecord(record);
    },

//按钮按下对grid中已选择的记录进行修改
    modifyRules: function () {
        currentRecord = 0
        rulesfirst = 1//第一次或重新打开form
        records = (Ext.getCmp('rulesgrid')).getSelectionModel().getSelection();//不能用 var records 可能是局部变量，其它方法取不到值。
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
        view = Ext.widget('rulesedit');//编辑窗口中的记录与Grid中选中的记录是同一条记录
        // view.down('form').loadRecord(record[0]);
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图
        // view.down('form').loadRecord(records[1]);//显示编辑视图
    },

    firstRules: function (button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord = 0
        view.down('form').loadRecord(records[0]);//显示编辑视图
    },
    previewRules: function (button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord = currentRecord - 1
        if (currentRecord == -1)currentRecord = 0
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图
    },
    nextRules: function (button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord = currentRecord + 1
        if (currentRecord == records.length)currentRecord = records.length - 1
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图

    },
    lastRules: function (button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord = records.length - 1
        view.down('form').loadRecord(records[records.length - 1]);//显示编辑视图
    },

//编辑记录后进行保存更新
    updateRules: function (button) {
        var win = button.up('window');
        form = win.down('form');
        record = form.getRecord();
        values = form.getValues();
        values.scDate = (values.scDate).replace(/(年)|(月)/g, "-");
        values.scDate = (values.scDate).replace(/(日)/g, "");
        if (rulesfirst = 1) {
            record.set(values);//因为此时与数据表记录关联,第一次保存可以激活Store的update:function(store,record)，第二次保存就不激活Store的update:function(store,record)，
            rulesfirst = 0
        } else {
            Ext.Ajax.request({
                url: '/appMis/rules/update',
                params: {
                    id: record.get("id")
                    , title: record.get('title')
                    , titleCode: record.get('titleCode')
                    , jb: record.get('jb')
                    , dep: record.get('dep')
                    , scr: record.get('scr')
                    , scDate: (new Date(record.get('scDate'))).pattern("yyyy-MM-dd") + " 00:00:00.0"
                    , wjlx: record.get('wjlx')
                },
                success: function (resp, opts) {
                    if (resp.responseText == 'success') {
                        var panelStore = (Ext.getCmp('rulesgrid')).getStore();
                        panelStore.loadPage(panelStore.currentPage);
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            var panelStore = (Ext.getCmp('rulesgrid')).getStore();
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
    addRules: function () {
        /* if ((currentTreeNode == "p1000") || (currentTreeNode.length <= 4)) {
         Ext.Msg.show({
         title: '操作提示 ',
         msg: '不能在分类名下增加信息，必须选择某个单位！！ ',
         buttons: Ext.MessageBox.OK
         })
         setTimeout(function () {
         Ext.Msg.hide();
         }, 1500);
         return
         }*/
        var record = Ext.create('appMis.model.RulesModel', {
            title: '新文件 ',
            titleCode: ' ',
            jb: '院级',
            dep: ' ',
            scr: ' ',
            scDate: new Date(),
            wjlx: ' '

        });
        //设置初值
        var view = Ext.widget('rulesadd');
        view.down('form').loadRecord(record);
    },


//保存新增加的部门
    saveRules: function (button) {
        var win = button.up('window');
        form = win.down('form');
        record = form.getRecord();//创建的新记录
        values = form.getValues();
        values.scDate = (values.scDate).replace(/(年)|(月)/g, "-");
        values.scDate = (values.scDate).replace(/(日)/g, "");
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        this.add(record, win);
        //win.close();
    },


///以增加行的方式增加数据
    addRowRules: function () {
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 2,
            autoCancel: false
        });
        record = Ext.create('appMis.model.RulesModel', {
            title: '新文件 ',
            titleCode: ' ',
            jb: '院级',
            dep: ' ',
            scr: ' ',
            scDate: (new Date()).pattern("yyyy-MM-dd"),
            wjlx: ' '

        });
        this.add(record)//在最后增加一条记录
    },

//insert和add都是调用它
    add: function (record, win) {//insert和add都是调用它
        var panelStore = (Ext.getCmp('rulesgrid')).getStore();
        Ext.Ajax.request({
            url: '/appMis/rules/save',
            params: {
                title: record.get('title'),
                titleCode: record.get('titleCode'),
                jb: record.get('jb'),
                dep: record.get('dep'),
                scr: record.get('scr'),
                scDate: (new Date(record.get('scDate'))).pattern("yyyy-MM-dd") + " 00:00:00.0",
                wjlx: record.get('wjlx')

            },
            method: 'POST',
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    if (resp.responseText == 'success') {
                        panelStore.loadPage(parseInt(panelStore.totalCount / panelStore.pageSize + 1));//定位grid到最后一页
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        }, 1500);
                        win.close();//数据增加成功后再关闭窗口
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加失败！ ',
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
                    msg: '数据增加失败！',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 2500)
            }

        });
    },

//按钮按下对grid中已选择的记录进行删除
    batchDeleteRules: function () {
        if ((Ext.getCmp('rulesquery1').getValue() == "") || (Ext.getCmp('rulesquery1').getValue() == null) || (Ext.getCmp('rulesquery1').getValue() == '全部')) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请先选择您要批量删除数据的生产日期!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;

        } else {
            newvalue1 = (new Date(Ext.getCmp('rulesquery1').getValue()).pattern("yyyy-MM-dd"));
            var myMask = new Ext.LoadMask(Ext.getCmp('rulesgrid'), {msg: "正在数据批量删除，请等待..."});
            myMask.show();
            Ext.Ajax.timeout = 9000000;  //9000秒
            Ext.Ajax.request({
                url: '/appMis/rules/batchDelete?newvalue1=' + newvalue1,
                method: 'POST',
                success: function (resp, opts) {//成功后的回调方法
                    if (resp.responseText == 'success') {
                        myMask.hide();
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据批量删除成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.getCmp('bbarrules').getStore().reload()
                            Ext.Msg.hide();
                        }, 1500);

                    } else {
                        myMask.hide();
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据批量删除失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        }, 1500);
                    }
                },
                failure: function (resp, opts) {
                    myMask.hide();
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
        }
    },

//按钮按下对grid中已选择的记录进行删除
    deleteRules: function () {
        var record = (Ext.getCmp('rulesgrid')).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp('rulesgrid')).getStore();
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
            url: '/appMis/rules/delete?id=' + record.get("id"),
            method: 'POST',
            success: function (resp, opts) {//成功后的回调方法
                if (resp.responseText == 'success') {
                    Ext.getCmp("bbarRules").getStore().reload()
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
    deleteMonthRules: function (button) {//删除单位当前月数据
        if (Ext.getCmp('currentRulesDate').getValue() == "") {
            Ext.MessageBox.show({title: "提示", msg: "请先选择您要删除单位当前月数据的日期!", buttons: Ext.MessageBox.OK})
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        var panelStore = (Ext.getCmp('rulesgrid')).getStore();
        var myMask = new Ext.LoadMask(Ext.getCmp('rulesgrid'), {msg: "正在删除单位当前月数据，请等待..."});
        myMask.show();
        Ext.Ajax.timeout = 9000000;  //9000秒
        Ext.Ajax.request({
            url: '/appMis/rules/deleteMonthRules?currentRulesDate=' + new Date(Ext.getCmp('currentRulesDate').getValue()).pattern("yyyy-MM-dd"),
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
});
