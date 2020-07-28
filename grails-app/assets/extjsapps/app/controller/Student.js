var procDefId = "", executionId = "", procInstId = "", taskId = ""


Ext.define('appMis.controller.Student', {
    extend: 'Ext.app.Controller',
    init: function () {
        this.control({
            'selectExportStudentItem   button[action=exportToExcel]':{//选择导出信息项目
                click: this.exportExcel
            },
            'selectImportStudentItem   button[action=importFromExcel]':{//选择导入信息项目
                click: this.importExcel
            },
            'studentviewport >  toolbar > button[action=modify]': {
                click: this.modify
            },
           'studentedit  button[action=save]': {
                click: this.update
            },
            'studentedit  button[action=importPhotoEdit]': {
                click: this.importPhotoEdit//在编辑窗口中导入个人照片
            },
            'studentadd  button[action=save]': {
                click: this.save
            },
            'studentadd  button[action=importPhotoAdd]': {
                click: this.importPhotoAdd//在新增窗口中导入个人照片
            },
            'studentviewport > toolbar >  button[action=add]': {
                click: this.add
            },
            'studentviewport >  toolbar > button[action=addRow]': {
                click: this.addRow
            },
            'studentviewport > toolbar >  button[action=delete]': {
                click: this.delete
            },
            'studentviewport > toolbar >  button[action=deleteAll]': {
                click: this.deleteAll//删除整个单位的所有学生
            },
            //------------------------------------------
            'studentviewport > toolbar >  button[action=print]': {
                click: this.print
            },
           'studentDisplayDetail  button[action=print]': {
                click: this.printDisplayDetaill
            },
        })
    },
    //导入EXCEL,从Excel表导入到数据库
    importExcel: function (button) {
       // alert("导入EXCEL,从Excel表导入到数据库.................")
        var fieldItem=Ext.getCmp('itemselectorImportStudent').getValue();
        win_select=Ext.getCmp('selectImportStudentItem');
        form = Ext.getCmp('selectImportStudentItemForm');
        if (form.form.isValid()) {
            if (Ext.getCmp('studentexcelfilePath').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传的文件');
                return;
            }
            form.getForm().submit({
                url:'/appMis/student/selectImport',
                method: "POST",
                params : {
                    dep_tree_id:currentTreeNode.substr(1),
                    fieldItem:fieldItem//要导入的项目
                },
                waitMsg: '正在处理',
                waitTitle: '请等待',
                success: function (fp, o) {
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: "" + o.result.info + "",
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        var panelStore = (Ext.getCmp("studentgrid")).getStore();
                        panelStore.proxy.api.read = '/appMis/student/read?dep_tree_id=' + currentTreeNode.substr(1);
                        panelStore.loadPage(1);//定位grid到第一页
                        Ext.Msg.hide();
                        win_select.close()
                    }, 2500)
                },
                failure: function (fp, o) {
                    //alert("警告", "" + o.result.info + "");
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: "" + o.result.info + "",
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        Ext.Msg.hide();
                    }, 2500)
                }

            })
        }
    },

    //导出EXCEL应发工资,把当前选择的数据导出到Excel表中
    exportExcel: function (record){
        var fieldItem=new Array()
        fieldItem=Ext.getCmp('itemselectorExportStudent').getValue();//所选择要导出的字段
        var p="";
        for(var i=0;i<fieldItem.length;i++){
            p=p+'&p='+fieldItem[i]//所选择要导出的字段重新组成url中传递的数组参数
        }
        var s="/appMis/student/selectExport?dep_tree_id="+currentTreeNode.substr(1)+p;
        var win = Ext.create('Ext.window.Window',{
            title:'下载系统数据表信息',
            layout: 'fit',    //设置布局模式为fit，能让frame自适应窗体大小
            modal: true,    //打开遮罩层
            height: 120,    //初始高度
            width: 800,  //初始宽度
            border: 0,    //无边框
            frame: false,    //去除窗体的panel框架
            html: '<iframe frameborder=0 width="100%" height="100%" allowtransparency="true" scrolling=auto src="'+s+'"></iframe>',
            items:[{xtype:'panel',layout: 'fit',id:"t1"}]
        });
        win.show();    //显示窗口
        var myMask = new Ext.LoadMask(Ext.getCmp('t1'), {msg: "正在把当前选择的数据导出到Excel表中，请等待...。下载完毕后，请关闭该窗口！"});
        myMask.show();
    },

    //按钮按下对grid中已选择的记录进行修改
    modify: function () {
        var record = (Ext.getCmp("studentgrid")).getSelectionModel().getSelection();
        if (record.length != 1) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请选择您要修改的数据!（注：只能选择一条数据）",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        var view = Ext.widget('studentedit');//编辑窗口中的记录与Grid中选中的记录是同一条记录
        view.down('form').loadRecord(record[0]);
        selectUsername=record[0].get("username");//选中的用户名
        Ext.getCmp("photoEdit").setSrc('/appMis/student/displayPhoto?username=' + selectUsername+'&time=' + new Date());//显示用户的照片
    },

   //在编辑窗口中编辑记录后进行保存更新
    update: function (button) {
        var win = button.up('window');
        form = win.down('form');
        record = ((Ext.getCmp("studentgrid")).getSelectionModel().getSelection())[0];
        values = form.getValues();
        values.birthDate = (values.birthDate).replace(/(年)|(月)/g, "-");
        values.birthDate = (values.birthDate).replace(/(日)/g, "");
        values.enrollDate = (values.enrollDate).replace(/(年)|(月)/g, "-");
        values.enrollDate = (values.enrollDate).replace(/(日)/g, "");
        record.set(values);
        win.close();
    },

    //在新增加的人员输入窗口按【保存】
    save: function (button) {
        var win = button.up('window');
        form = win.down('form');
        record = form.getRecord();//创建的新记录
        values = form.getValues();
        values.birthDate = (values.birthDate).replace(/(年)|(月)/g, "-");
        values.birthDate = (values.birthDate).replace(/(日)/g, "");
        values.enrollDate = (values.enrollDate).replace(/(年)|(月)/g, "-");
        values.enrollDate = (values.enrollDate).replace(/(日)/g, "");
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        this.insert(record, win);
        win.close();
    },
    //insert增加信息
    insert: function (record, win) {
        var panelStore = (Ext.getCmp("studentgrid")).getStore();
        Ext.Ajax.request({
            url: '/appMis/student/save?dep_tree_id=' + currentTreeNode.substr(1),
            params: {
                //id:record.get("id"),
                username:record.get("username"),
                truename:record.get("truename"),
                email:record.get("email"),
                department:record.get("department"),
                major:record.get("major"),
                college:record.get("college"),
                phone:record.get("phone"),
                homephone:record.get("homephone"),
                idNumber:record.get("idNumber"),
                birthDate:(record.get('birthDate'))?(new Date(record.get('birthDate'))).pattern("yyyy-MM-dd")+" 00:00:00.0":"",
                sex:record.get("sex"),
                race:record.get("race"),
                politicalStatus:record.get("politicalStatus"),
                enrollDate:(record.get('enrollDate'))?(new Date(record.get('enrollDate'))).pattern("yyyy-MM-dd")+" 00:00:00.0":"",
                treeId:record.get("treeId"),
                currentStatus:record.get("currentStatus")
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
                        //  win.close();//数据增加成功后再关闭窗口
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
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
                    msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 2500)
            }
        });
    },

    //打开直接新增加的人员输入窗口
    add: function () {
        if ((currentTreeNode == "p1000") || (currentTreeNode.length <= 7)) {
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '不能在行政区或分类下增加人员信息，必须选择某个单位！！ ',
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 1500);
            return
        }
        var record = Ext.create('appMis.model.StudentModel', {
            username:'klmya' + Math.floor(Math.random() * 10000),
            truename:'张' + Math.floor(Math.random() * 10000),
            email:'123@123.com',
            department:departmentName,
            major:majorName,
            college:collegeName,
            phone:"",
            homephone:"",
            idNumber:'6502013' + Math.floor(Math.random() * 10000),
            birthDate:(new Date()).pattern("yyyy-MM-dd"),
            sex: '男',
            race: '汉',
            politicalStatus:"",
            enrollDate:(new Date()).pattern("yyyy-MM-dd"),
            treeId: currentTreeNode.substr(1),
            currentStatus:"在校"
        });
        //设置初值
        var view = Ext.widget('studentadd');
        view.down('form').loadRecord(record);
    },

    //初始应用,直接新增加的人员
    addRow: function () {
        if ((currentTreeNode == "p1000") || (currentTreeNode.length <= 7)) {
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '不能在行政区或分类下增加人员信息，必须选择某个单位！！ ',
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 1500);
            return
        }
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 2,
            autoCancel: false
        });
        record = Ext.create('appMis.model.StudentModel', { // Create a model instance
            username:'klmya' + Math.floor(Math.random() * 10000),
            truename:'张' + Math.floor(Math.random() * 10000),
            email:'123@123.com',
            department:departmentName,
            major:majorName,
            college:collegeName,
            phone:"",
            homephone:"",
            idNumber:'6502013' + Math.floor(Math.random() * 10000),
            birthDate:(new Date()).pattern("yyyy-MM-dd"),
            sex: '男',
            race: '汉',
            politicalStatus:"",
            enrollDate:(new Date()).pattern("yyyy-MM-dd"),
            treeId: currentTreeNode.substr(1),
            currentStatus:"在校"
        });
        this.insert(record,null)//在最后增加一条记录
    },

    //直接删除职工
    delete: function () {
        var record = (Ext.getCmp("studentgrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("studentgrid")).getStore();
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
                if (currPage == 0) currPage = 1
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
            url: '/appMis/student/delete?id=' + record.get("id"),
            method: 'POST',
            success: function (resp, opts) {//成功后的回调方法
                if (resp.responseText == 'success') {
                    Ext.getCmp("bbarEmp").getStore().reload()
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

    //删除单位所有人员信息，包括照片文件
    deleteAll: function (button) {
        if ((currentTreeNode == "p1000") || (currentTreeNode.length <= 7)) {
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '不能在分类下删除人员信息，必须选择某个单位！！ ',
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 1500);
            return
        }
        var panelStore = (Ext.getCmp('studentgrid')).getStore();
        var myMask = new Ext.LoadMask(Ext.getCmp('studentgrid'), {msg: "正在删除所有单位人员信息，请等待..."});
        myMask.show();
        Ext.Ajax.timeout = 0;
        Ext.Ajax.request({
            url: '/appMis/student/deleteAll?dep_tree_id=' + currentTreeNode.substr(1)+'&departmentName='+departmentName,
            method: 'POST',
            async: false,
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    if (resp.responseText == 'success') {
                        //myMask.hide();
                        myMask.destroy()
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '删除所有人员信息成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            panelStore.loadPage(panelStore.currentPage);
                            Ext.Msg.hide();
                        }, 1500);
                    } else {
                        //myMask.hide();
                        myMask.destroy()
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '删除所有人员信息失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        }, 1700);
                    }
                },
            failure: function (resp, opts) {
                //myMask.hide();
                myMask.destroy()
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '删除所有人员信息失败！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 1500)
            }
        });
    },

    //在增加窗口中导入照片
    importPhotoAdd: function (button) {
        //alert("先保存要增加人员的内容")
        var win = button.up('window');
        form = win.down('form');
        record = form.getRecord();//创建的新记录
        values = form.getValues();
        values.birthDate = (values.birthDate).replace(/(年)|(月)/g, "-");
        values.birthDate = (values.birthDate).replace(/(日)/g, "");
        values.enrollDate = (values.enrollDate).replace(/(年)|(月)/g, "-");
        values.enrollDate = (values.enrollDate).replace(/(日)/g, "");
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        selectUsername=record.get("username")

        form = Ext.getCmp('studentPhotoForm1');
        if (form.form.isValid()) {
            if (Ext.getCmp('studentPhoto1').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传照片的文件');
                return;
            }
            form.getForm().submit({
                // url: '/appMis/student/importPhotoAdd?dep_tree_id=' + currentTreeNode.substr(1),
                url: '/appMis/student/importPhotoAdd',
                method: "POST",
                async: false,
                params: {
                    //id:record.get("id"),
                    username:record.get("username"),
                    truename:record.get("truename"),
                    email:record.get("email"),
                    department:record.get("department"),
                    major:record.get("major"),
                    college:record.get("college"),
                    phone:record.get("phone"),
                    homephone:record.get("homephone"),
                    idNumber:record.get("idNumber"),
                    birthDate:(new Date(record.get('birthDate'))).pattern("yyyy-MM-dd")+" 00:00:00.0",
                    sex:record.get("sex"),
                    race:record.get("race"),
                    politicalStatus:record.get("politicalStatus"),
                    enrollDate:(new Date(record.get('enrollDate'))).pattern("yyyy-MM-dd")+" 00:00:00.0",
                    treeId:record.get("treeId"),
                    currentStatus:record.get("currentStatus")
                },

                waitMsg: '正在导入个人照片的文件，请等待...',
                waitTitle: '请等待',
                success: function (fp, o) {
                    if (o.result.success) {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "" + o.result.info + "",
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                            //Ext.getCmp("photoAdd").getEl().dom.src = '/appMis/student/displayPhoto?username=' + selectUsername + '&time=' + new Date()//显示用户的照片，也可以
                            Ext.getCmp("photoAdd").setSrc('/appMis/student/displayPhoto?username=' + selectUsername + '&time=' + new Date())//更好，显示用户的照片
                                (Ext.getCmp("studentgrid")).getStore().loadPage(1);//定位grid到第一页
                            win.close()//新增人员上传了照片后，则立即关闭窗口，否则修改身份证后会产生新的记录！！！
                        }, 1500)
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '照片文件导入失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        }, 1500);
                    }
                    //alert("温馨提示" + o.result.info+"")


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
                    }, 3500)
                }
            })
        }
    },
    //在编辑窗口中导入照片
    importPhotoEdit: function (button) {
        //alert("importPhoto!!!!==="+Ext.getCmp('studentPhoto1').getValue())
        form = Ext.getCmp('studentPhotoForm1');
        if (form.form.isValid()) {
            if (Ext.getCmp('studentPhoto1').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传照片的文件');
                return;
            }
            form.getForm().submit({
                url: '/appMis/student/importPhotoEdit?username=' + selectUsername,
                method: "POST",
                async: false,
                waitMsg: '正在导入个人照片的文件，请等待...',
                waitTitle: '请等待',
                success: function (fp, o) {
                    if (o.result.success) {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "" + o.result.info + "",
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                            //Ext.getCmp("photoEdit").getEl().dom.src = '/appMis/student/displayPhoto?username=' + selectUsername + '&time=' + new Date()//也可以
                            Ext.getCmp("photoEdit").setSrc('/appMis/student/displayPhoto?username=' + selectUsername+'&time=' + new Date());//更好，显示用户的照片
                        }, 2500)
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '照片文件导入失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        }, 1500);
                    }
                    //alert("温馨提示" + o.result.info+"")


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
                    }, 3500)
                }
            })
        }
    },

 //----------------------------------------------------------------------------------------

    //insert和add都是调用它
    addbak: function (record, win) {
        var panelStore = (Ext.getCmp("studentgrid")).getStore();
        //  panelStore.proxy.api.read="student/listAsJsonPage2?newvalue="+'all';//解决IE的URL传中文参数乱码问题
        //  panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
        Ext.Ajax.request({
            url: '/appMis/student/save?dep_tree_id=' + currentTreeNode.substr(1),
            params: {
                // id:record.get("id"),
                username:record.get("username"),
                truename:record.get("truename"),
                email:record.get("email"),
                department:record.get("department"),
                major:record.get("major"),
                college:record.get("college"),
                phone:record.get("phone"),
                homephone:record.get("homephone"),
                idNumber:record.get("idNumber"),
                birthDate:(new Date(record.get('birthDate'))).pattern("yyyy-MM-dd")+" 00:00:00.0",
                sex:record.get("sex"),
                race:record.get("race"),
                politicalStatus:record.get("politicalStatus"),
                enrollDate:(new Date(record.get('enrollDate'))).pattern("yyyy-MM-dd")+" 00:00:00.0",
                treeId:record.get("treeId"),
                currentStatus:record.get("currentStatus")
            },
            method: 'POST',
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    if (resp.responseText == 'success') {
                        Ext.getCmp('displayNewEempolyeeStatus').setValue("未申报");
                        panelStore.loadPage(parseInt(panelStore.totalCount / panelStore.pageSize + 1));//定位grid到最后一页
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        }, 1500);
                        //  win.close();//数据增加成功后再关闭窗口
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
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
                    msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 2500)
            }
        });
    },

    //把单张照片导入到数据库
    importPhoto: function (button) {
        //alert("importPhoto!!!!==="+Ext.getCmp('studentPhoto').getValue())
        form = Ext.getCmp('studentPhotoForm')
        if (form.form.isValid()) {
            if (Ext.getCmp('studentPhoto').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传照片的文件');
                return;
            }
            form.getForm().submit({
                url: '/appMis/student/importPhoto?studentCode=' + sstudentCode,
                method: "POST",
                async: false,
                waitMsg: '正在导入职工个人照片的文件，请等待...',
                waitTitle: '请等待',
                success: function (fp, o) {
                    if (o.result.success) {
                        // panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
                        // Ext.getCmp("photoImageEdit").getEl().dom.src ='images/photo/'+departmentDetail+'/'+sstudentCode+name+'.jpg?rand='+Math.random();
                        // Ext.getCmp("photoImage").getEl().dom.src = 'images/photo/' + departmentDetail + '/'+ name + sstudentCode  + '.jpg?rand=' + Math.random();
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "" + o.result.info + "",
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                            Ext.getCmp("photoImage").getEl().dom.src = '/appMis/student/displayPhoto?studentCode=' + sstudentCode + '&time=' + new Date()
                        }, 1500)
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '照片文件导入失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        }, 1500);
                    }
                    //alert("温馨提示" + o.result.info+"")


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
                    }, 3500)
                }
            })
        }
    },

    //打印个人详细信息
    printDisplayDetaill: function (button) {
        var LODOP = getLodop();
        LODOP.PRINT_INIT();
        LODOP.ADD_PRINT_HTM('1%', '1%', '98%', '98%', document.getElementById("studentDisplayDetail").innerHTML);
        // LODOP.ADD_PRINT_HTM('1%', '1%', '98%', '98%', document.getElementById("grxxxx").innerHTML);
        //  LODOP.NewPage();
        //  LODOP.ADD_PRINT_HTM('1%', '1%', '98%', '98%', document.getElementById("grxxxx1").innerHTML);
        //  LODOP.NewPage();
        // LODOP.ADD_PRINT_HTM('1%', '1%', '98%', '98%', document.getElementById("grxxxx2").innerHTML);
        LODOP.SET_PRINT_MODE("FULL_WIDTH_FOR_OVERFLOW", true);
        // LODOP.SET_PRINT_MODE("FULL_HEIGHT_FOR_OVERFLOW",true);
        LODOP.SET_PREVIEW_WINDOW(1, 0, 0, 0, 0, "");
        LODOP.PREVIEW();
    },

    diaplayDetailStudent11: function () {//显示当前职工个人详细信息(old)
        var record = (Ext.getCmp("studentgrid")).getSelectionModel().getSelection();
        if (record.length != 1) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请选择您要详细显示的个人数据!（注：只能选择一条数据）",
                buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        var view = Ext.widget('studentDisplayDetail');//编辑窗口中的记录与Grid中选中的记录是同一条记录
        //view.down('form').loadRecord(record[0]);
        Ext.getCmp('studentJbxx').loadRecord(record[0]);
        //以下在WEB重新启动后可以读出数据，否则第一次View中无数据
        var panelStore = (Ext.getCmp('studentDisplayXlgrid')).getStore();
        panelStore.pageSize = 25;
        panelStore.sort('graduateDate', 'DESC');//此命令还会向后台发送一次请求排序数据

        panelStore = (Ext.getCmp('studentDisplayGzllgrid')).getStore();
        panelStore.pageSize = 25;
        panelStore.sort('startDate', 'DESC');//此命令还会向后台发送一次请求排序数据

        panelStore = (Ext.getCmp('studentDisplayNdkhgrid')).getStore();
        panelStore.pageSize = 25;
        panelStore.sort('khnd', 'DESC');//此命令还会向后台发送一次请求排序数据

        panelStore = (Ext.getCmp('studentDisplayJcxxgrid')).getStore();
        panelStore.pageSize = 25;
        panelStore.sort('startDate', 'DESC');//此命令还会向后台发送一次请求排序数据

        panelStore = (Ext.getCmp('studentDisplayJbtxxgrid')).getStore();
        panelStore.pageSize = 25;
        panelStore.sort('startDate', 'DESC');//此命令还会向后台发送一次请求排序数据

        panelStore = (Ext.getCmp('studentDisplayGztzgrid')).getStore();
        panelStore.pageSize = 25;
        // panelStore.sort('startDate', 'DESC');//此命令还会向后台发送一次请求排序数据

        panelStore.sorters[0] = {property: "declareDate", direction: "DESC"};
        panelStore.sorters[1] = {property: "jbxjgz", direction: "DESC"};
        // panelStore.sort('startDate', 'DESC');
        // panelStore.sort('jbxjgz', 'DESC');
        panelStore.loadPage(1);

        panelStore = (Ext.getCmp('studentDisplaySyqyGztzgrid')).getStore();
        panelStore.pageSize = 25;
        panelStore.sort('stadeclareDatertDate', 'DESC');//此命令还会向后台发送一次请求排序数据

        view.setTitle('显示当前职工【' + (record[0]).get('studentCode') + ' ' + (record[0]).get('name') + ' 】的个人详细信息');
    },

    print: function (button) {
        Ext.Ajax.request({
            url: '/appMis/student/printStudent?dep_tree_id=' + currentTreeNode.substr(1),
            method: 'POST',
            success://回调函数
                function (resp, opts) {
                    {
                        var data1 = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                        //定义模板 使用标签tpl和操作符for
                        var tpl1 = new Ext.XTemplate(
                            '<div id="div11">',
                            '<table class="printtable" border=1  cellpadding=0 cellspacing = 0>',
                            '<caption><b>职工基本信息</b></caption>',
                            '<caption><b>所在部门：<SPAN>' + (data1.users[0].department == '全部' ? '克拉玛依市' : data1.users[0].department) + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;所在部门：<SPAN>' + (data1.users[0].department == '全部' ? '克拉玛依市' : data1.users[0].department) + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;打 印 人：<SPAN >' + data1.users[0].truename + '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;打印日期：' + (new Date().pattern("yyyy年MM月dd日")) + '</b></caption>',
                            '<thead>',
                            '<tr>',
                            '<td width=130 >职工编号</td><td width=130 >姓名</td>',
                            '<td width=100 >身份证</td><td width=60 >出生日期</td>',
                            '<td width=100 >性别</td><td width=50 >族别</td>',
                            '<td width=50 >政治面貌</td><td width=50 >工作日期</td>',
                            '<td width=50 >认定工作日期</td><td width=100 >专业类别</td>',
                            '<td width=100 >艰边类区</td><td width=100 >所属行业</td>',
                            '<td width=50 >档案工资制度</td><td width=100 >现行工资制度</td>',
                            '<td width=60 >单位</td><td width=60 >所在部门</td>',
                            '</tr>',
                            '</thead>',
                            '<tpl for=".">',
                            '<tr>',
                            '<td>{studentCode}</td><td>{name}</td>',
                            '<td>{idNumber}</td><td>{birthDate}</td>',
                            '<td>{sex}</td><td>{race}</td>',
                            '<td>{politicalStatus}</td><td>{workDate}</td>',
                            '<td>{thatworkDate}</td><td>{zylb}</td>',
                            '<td>{jblq}</td><td>{sshy}</td>',
                            '<td>{dagzzd}</td><td>{xxgzzd}</td>',
                            '<td>{treeId}</td><td>{szbm}</td>',
                            '</tr>',
                            '</tpl>',
                            '<tfoot>',
                            /*
                             '<tr>',
                             '<th width="35%" colspan="8" align="left" >本页数量小计:<font  tdata="SubCount" format="#" color="blue">###</font></th>',
                             '<th width=250 tdata="SubSum" format="#,##0.00" align="right" ><font color="blue" id="id01">￥###元</font></th>',
                             '<th colspan="12"><font color="blue"></font></th>',
                             '</tr>',
                             '<tr>',
                             '<th width="35%" colspan="8" align="left" >全表数量总计:<font  tdata="AllCount" format="#" color="blue">###</font></th>',
                             '<th width=250 tdata="AllSum" format="#,##0.00" align="right" ><font color="blue" id="id01">￥###元</font></th>',
                             '<th colspan="12"><font color="blue"></font></th>',
                             '</tr>',
                             */
                            '<tr>',
                            '<th width="100%" colspan="21" tindex="1">当前是第<font tdata="PageNO" format="ChineseNum" color="blue">##</font>页</span>/共<font tdata="PageCount" format="ChineseNum" color="blue">##</font></span>页，&nbsp;&nbsp;&nbsp;&nbsp;本页数<font color="blue" format="00" tdata="SubCount">##</font>，本页从第<font color="blue" format="00" tdata="Count-SubCount+1">##</font>行到第<font color="blue" tdata="Count">##</font>行</th>',
                            '</tr>',
                            '</tfoot>',
                            '</table>',
                            '</div>'
                        );
                        document.getElementById("tpl-1").innerHTML = '';//重新打印时清空!!
                        //模板值和模板进行组合并将新生成的节点插入到id为'tpl-table'的元素中
                        tpl1.append('tpl-1', data1.students);
                        var LODOP = getLodop();
                        // LODOP.PRINT_INIT("打印控件功能演示_Lodop功能_分页打印综合表格");
                        LODOP.PRINT_INIT();
                        // LODOP. SET_PRINT_PAGESIZE(1, 0, 0, "A3 旋转");
                        // LODOP.SET_PRINT_STYLE("FontSize",15);

                        // LODOP.SET_PRINT_STYLEA(2,"FontSize",25);
                        // LODOP.ADD_PRINT_TEXT(20,230,"95%",25,"低值易耗品");
                        //  LODOP.ADD_PRINT_HTM(444,"5%","90%",54,tpl);
                        // LODOP.ADD_PRINT_HTM(40,"5%","95%","95%",document.getElementById("div0").innerHTML);

                        LODOP.ADD_PRINT_TABLE('5%', '5%', '90%', '80%', document.getElementById("div0").innerHTML);

                        // LODOP.ADD_PRINT_TABLE(40,"5%",1500,840,document.getElementById("div11").innerHTML);
                        //  LODOP.ADD_PRINT_TABLE('30', '45', '1800', '1000', document.getElementById("div11").innerHTML);
                        //  LODOP.ADD_PRINT_HTM(444,"5%","90%",54,table);
                        // LODOP.ADD_PRINT_TABLE(10,10,300,100,document.getElementById("div0").innerHTML);
                        LODOP.PREVIEW();
                    }
                },
            failure: function (resp, opts) {
                // myMask.hide();
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据读取失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 1500)
            }
        });


    },
});
