Ext.define('appMis.controller.Department',{
    extend:'Ext.app.Controller',
    init:function(){
        this.control({
            'departmentviewport > panel > toolbar >  button[action=save1]': {
                click: this.saveDepartment1
            },
            'departmentviewport > panel > toolbar >  button[action=cancel1]': {
                click: this.cancelDepartment1
            },
            'departmentviewport > panel > toolbar >  button[action=jxkhaddRow]': {
                click: this.jxkhaddRowDepartment
            },
            'departmentviewport > panel > toolbar >  button[action=jxkhdelete]': {
                click: this.jxkhdeleteDepartment
            },
           'departmentedit  button[action=save]': {
                   click: this.updateDepartment
            },
            'departmentadd  button[action=save]': {
                click: this.saveDepartment
            },
            'departmentviewport > toolbar >  button[action=add]': {
                click: this.addDepartment
            },
            'departmentviewport >  toolbar > button[action=addRow]': {
                click: this.addRowDepartment
            },
            'departmentviewport >  toolbar > button[action=modify]': {
                click: this.modifyDepartment
            },
            'departmentviewport > toolbar >  button[action=delete]': {
                click: this.deleteDepartment
            },
            'departmentviewport > toolbar >  button[action=deleteDepartment]': {
                click: this.deleteAllDepartment
            },
            'departmentviewport >  toolbar > button[action=exportToExcel]': {
                click: this.exportExcelDepartment
            },
            'departmentviewport >  toolbar > button[action=importFromExcel]': {
                click: this.importExcelDepartment
            }
        })
    },
    cancelDepartment1:function(){
        var currentDepartment1
        form=Ext.getCmp("deparmentDetail").down('form')
        record =  form.getRecord();//创建的新记录
        values = form.getValues();
        Ext.Ajax.request({
            url:'/appMis/department/readDepartmentById?id='+values.id,
            async:false,//同步
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    var obj = eval('(' + resp.responseText + ')')//将获取的Json字符串转换为Json对象
                    currentDepartment1= obj.department2//为Json对象
                }
        })
        currentDepartment1.zjgzStartdate=(new Date(currentDepartment1.zjgzStartdate)).pattern("yyyy-MM-dd"),
        record.set(currentDepartment1);
        form.loadRecord(record);

    },
    saveDepartment1:function(){
        var me=this
       form=Ext.getCmp("deparmentDetail").down('form')
       //var record = Ext.create('appMis.model.DepartmentModel');//创建的新记录,与下一条相同效果
        record =  form.getRecord();//创建的新记录
        values = form.getValues();
        record.set(values);//因为此时与数据表记录还无关联,不能激活Store的update:function(store,record)
        Ext.Ajax.request({
            url:'/appMis/department/update',
            async:false,//同步
            params:{
                id: record.get("id"),
                department: record.get("department"),
                dwjc: record.get("dwjc"),
                dwtjsl: record.get("dwtjsl"),
                gzzescbm: record.get("gzzescbm"),
                departmentCode: record.get("departmentCode"),
                lsgx: record.get("lsgx"),
                lsxt: record.get("lsxt"),
                xzhq: record.get("xzhq"),
                dwxz: record.get("dwxz"),
                czbkxs: record.get("czbkxs"),
                jblq: record.get("jblq"),
                dwjb: record.get("dwjb"),
                sshy: record.get("sshy"),
                cgqk: record.get("cgqk"),
                zgbmqk: record.get("zgbmqk"),
                sjgzzgbm: record.get("sjgzzgbm"),
                bzs: record.get("bzs"),
                syrs: record.get("syrs"),
                bzqk: record.get("bzqk"),
                bzpwqk: record.get("bzpwqk"),
                hsxz: record.get("hsxz"),
                leader: record.get("leader"),
                contacts: record.get("contacts"),
                phone: record.get("phone"),
                fax: record.get("fax"),
                postcode: record.get("postcode"),
                address: record.get("address"),
                treeId: record.get("treeId"),
                glyph: record.get("glyph")
            },
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        //panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                            me.reloadtreemenu()
                        },1500);
                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },2500);
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },2500)
            }
        });

    },

    saveDepartment:function(button){//保存新增加的部门
        var win    = button.up('window');
        form   = win.down('form');
        record =  form.getRecord();//创建的新记录
        values = form.getValues();
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        this.add(record,win)
        win.close();
    },

     modifyDepartment:function(){//按钮按下对grid中已选择的记录进行修改
        var record = (Ext.getCmp("departmentgrid")).getSelectionModel().getSelection();
        if(record.length != 1){
            Ext.MessageBox.show({
                title:"提示",
                msg:"请选择您要修改的数据!（注：只能选择一条数据）",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },2500);
            return;
        }
        var view = Ext.widget('departmentedit');//编辑窗口中的记录与Grid中选中的记录是同一条记录
        view.down('form').loadRecord(record[0]);
    },

    updateDepartment:function(button) {//在编辑窗口中编辑记录后进行保存更新
        var me=this
        var win    = button.up('window');
        form   = win.down('form');
        record = ((Ext.getCmp("departmentgrid")).getSelectionModel().getSelection())[0];//解决第二次保存就不激活Store的update:function(store,record)，的问题
       // record = form.getRecord();//无法解决第二次保存就不激活Store的update:function(store,record)，的问题
        values = form.getValues();
        record.set(values);//因为此时与数据表记录关联,第一次保存可以激活Store的update:function(store,record)，第二次保存就不激活Store的update:function(store,record)，
        Ext.Msg.show({
            title: '操作提示 ',
            msg: '数据更新成功！ ',
            buttons: Ext.MessageBox.OK
        })
        setTimeout(function () {
            Ext.Msg.hide();
            me.reloadtreemenu()
        },1500);
        // win.close();//在编辑窗口中编辑记录后，保存后窗口关闭或不关闭
    },

    addDepartment:function(){//打开新增加的单位输入窗口
       // alert("currentTreeNode===="+currentTreeNode)
        if((currentTreeNode=="p1000")||(currentTreeNode.length <=4)){
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '不能在行政区下增加单位，必须选择分类！！ ',
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },1500);
            return
        }
        var record = Ext.create('appMis.model.DepartmentModel',{
            department:'克拉玛依'+Math.floor(Math.random()*10000),
            dwjc: '',
            dwtjsl: '1',
            gzzescbm: '',
            departmentCode:'650201'+Math.floor(Math.random()*10000),
            lsgx: '',
            lsxt: '',
            xzhq: '',
            dwxz: '',
            czbkxs: '',
            jblq: '三类',
            dwjb:'县（市、区、旗）',
            sshy: '',
            cgqk: '',
            zgbmqk: '',
            sjgzzgbm: '',
            bzs: '1',
            syrs: '1',
            bzqk: '',
            bzpwqk: '',
            hsxz: '',
            leader:'',
            contacts: '未知',
            phone: '0990-',
            fax: '0990-',
            postcode:'833600',
            address:'克拉玛依市',
            glyph:'0xf0c9',
            treeId:''+Math.floor(Math.random()*10)+Math.floor(Math.random()*10)
        });
        //设置初值
        var view = Ext.widget('departmentadd');
        view.down('form').loadRecord(record);
    },

    addRowDepartment:function(){//以增加行的方式增加一个部门
        if((currentTreeNode=="p1000")||(currentTreeNode.length <=4)){
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '不能在行政区下增加单位，必须选择分类！！ ',
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
        var dep='克拉玛依'+Math.floor(Math.random()*10000),
        record = Ext.create('appMis.model.DepartmentModel', { // Create a model instance
            department:dep,
            dwjc: dep,
            dwtjsl: '1',
            gzzescbm:'klmys001',
            departmentCode:'650201'+Math.floor(Math.random()*10000),
            lsgx: '1',
            lsxt: '1',
            xzhq: '1',
            dwxz: '1',
            czbkxs: '1',
            jblq: '三类',
            dwjb:'县（市、区、旗）',
            sshy: '1',
            cgqk: '1',
            zgbmqk: '1',
            sjgzzgbm: '1',
            bzs: '10',
            syrs: '10',
            bzqk: '正式编制',
            bzpwqk: '',
            hsxz: '',
            leader: '未知负责人',
            contacts: '未知管理员',
            phone: '0990-',
            fax: '0990-',
            postcode:'833600',
            address:'克拉玛依市',
            glyph:'0xf0c9',
            treeId:''+Math.floor(Math.random()*10)+Math.floor(Math.random()*10)
        });

        this.add(record)//在最后增加一条记录

    },
    add:function(record,win){//insert和add都是调用它
         var panelStore = (Ext.getCmp("departmentgrid")).getStore();
      //  panelStore.proxy.api.read="department/listAsJsonPage2?newvalue="+'all';//解决IE的URL传中文参数乱码问题
      //  panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
        Ext.Ajax.request({
            url:'/appMis/department/save?dep_tree_id=' + currentTreeNode.substr(1),
            async:false,//同步
            params:{
               // id:record.get("id"),
                department: record.get("department"),
                dwjc: record.get("dwjc"),
                dwtjsl: record.get("dwtjsl"),
                gzzescbm: record.get("gzzescbm"),
                departmentCode: record.get("departmentCode"),
                lsgx: record.get("lsgx"),
                lsxt: record.get("lsxt"),
                xzhq: record.get("xzhq"),
                dwxz: record.get("dwxz"),
                czbkxs: record.get("czbkxs"),
                jblq: record.get("jblq"),
                dwjb: record.get("dwjb"),
                sshy: record.get("sshy"),
                cgqk: record.get("cgqk"),
                zgbmqk: record.get("zgbmqk"),
                sjgzzgbm: record.get("sjgzzgbm"),
                bzs: record.get("bzs"),
                syrs: record.get("syrs"),
                bzqk: record.get("bzqk"),
                bzpwqk: record.get("bzpwqk"),
                hsxz: record.get("hsxz"),
                leader: record.get("leader"),
                contacts: record.get("contacts"),
                phone: record.get("phone"),
                fax: record.get("fax"),
                postcode: record.get("postcode"),
                address: record.get("address"),
                treeId: record.get("treeId"),
                glyph: record.get("glyph")
            },
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);


                       // win.close();//数据增加成功后再关闭窗口
                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },2500);
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },2500)
            }

        });
        this.reloadtreemenu()
    },
    deleteDepartment:function(){//按钮按下对grid中已选择的记录进行删除
        var record = (Ext.getCmp("departmentgrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("departmentgrid")).getStore();
        var currPage = panelStore.currentPage;
        if(record.length == 0){
            Ext.MessageBox.show({
                title:"提示",
                msg:"请先选择您要删除的行数据!",
                buttons: Ext.MessageBox.OK
                //icon: Ext.MessageBox.INFO
            });
            setTimeout(function () {
                  Ext.Msg.hide();
            },2500);
            return;
        }else{
            //alert("panelStore.totalCount=11111=="+panelStore.totalCount)
            for(var i = 0; i < record.length; i++){
                if(this.remove(panelStore,record[i])==0){//删除选中的记录,返回0为删除失败，1为成功
                    return
                }
            }

            Ext.MessageBox.show({
                   title:"提示",
                   msg:"成功删除"+record.length+"行数据!",
                   buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                Ext.Msg.hide();
                if(((panelStore.totalCount) % panelStore.pageSize)==0){
                    currPage=currPage-1;
                    if(currPage==0)currPage=1
                }
                //alert("currPage=222==="+currPage)
                panelStore.loadPage(currPage);//刷新当前grid页面
            },1500);
        }
        this.reloadtreemenu()
    },
    remove:function(store,record){
        var ss=1
        Ext.Ajax.request({
            url:'/appMis/department/delete?id='+record.get("id"),
            async:false,//同步,true异步 async:true,//同步,true异步
            method:'POST',
            success:
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.getCmp("bbarDep").getStore().reload()
                        ss= 1
                    }else{

                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据删除失败！可能原因是：1、有下属单位，2、本单位下有职工信息 ！！！',
                            buttons: Ext.MessageBox.OK,
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },2000);
                        ss=0;
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据删除失败！可能原因是：1、有下属单位，2、本单位下有职工信息 ！！！',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },2000)
                ss= 0
            }
        })
        return ss
    },

    deleteAllDepartment: function (button) {//删除单位当前月工资
        var panelStore = (Ext.getCmp('departmentgrid')).getStore();
        var myMask = new Ext.LoadMask(Ext.getCmp('departmentgrid'), {msg: "正在删除所有单位信息，请等待..."});
        myMask.show();
        // Ext.Ajax.timeout = 900000000;  //9000秒
        Ext.Ajax.request({
            url:'/appMis/department/deleteAllDepartment',
            method: 'POST',
            async: false,
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    if (resp.responseText == 'success') {
                        //myMask.hide();
                        myMask.destroy()
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '删除单位所有单位信息成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            panelStore.loadPage(panelStore.currentPage);
                            Ext.Msg.hide();
                        }, 1500);
                    } else {
                        if (resp.responseText == 'failure1') {
                            //myMask.hide();
                            myMask.destroy()
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '没有单位信息了，删除单位所有单位信息失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 1700);

                        } else {
                            //myMask.hide();
                            myMask.destroy()
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '删除单位所有单位信息失败！可能原因是：1、有下属单位，2、单位下有职工信息 ！！！',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 1500);
                        }
                    }
                },
            failure: function (resp, opts) {
                //myMask.hide();
                myMask.destroy()
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '删除单位所有单位信息失败！可能原因是：1、有下属单位，2、单位下有职工信息 ！！！',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 1500)
            }
        });
        this.reloadtreemenu()
    },
    importExcelArticles: function (button) {
        var fieldItem = Ext.getCmp('itemselectorImportArticles').getValue();
        win_select = Ext.getCmp('selectImportArticlesItem');
        //var panel1 = button.up.up('panel');
        form = Ext.getCmp('SelectImportArticlesItemForm');
        if (form.form.isValid()) {
            if (Ext.getCmp('articlesexcelfilePath').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传的文件');
                return;
            }
            //alert("Ext.getCmp('articlesexcelfilePath').getValue()===="+Ext.getCmp('articlesexcelfilePath').getValue())
            form.getForm().submit({
                url:'/appMis/articles/selectImportArticles?currentArticlesDate=' + new Date(Ext.getCmp('currentArticlesDate').getValue()).pattern("yyyy-MM-dd"),
                method: "POST",
                params: {fieldItem: fieldItem},//要导入的项目
                waitMsg: '正在处理',
                waitTitle: '请等待',
                success: function (fp, o) {
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: "" + o.result.info + "",
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        Ext.Msg.hide();
                        var panelStore = (Ext.getCmp('articlesgrid')).getStore();
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
                    });
                    setTimeout(function () {
                        Ext.Msg.hide();
                    }, 2500)
                }

            })
        }
    },
    //从Excel表导入到数据库
    importExcelDepartment: function (button) {
        // var panel1 = button.up('panel');
        // form = panel1.down('form');
        var me=this;
        form = Ext.getCmp('departmentform');
        if (form.form.isValid()) {
            if (Ext.getCmp('employeeDepartmentexcelfilePath').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传的文件');
                return;
            }
            //alert("Ext.getCmp('employeeJgYfgzexcelfilePath').getValue()===="+Ext.getCmp('employeeJgYfgzexcelfilePath').getValue())
            form.getForm().submit({
                url:'/appMis/department/importExcelDepartment',
                method: "POST",
                async:false,//同步
                waitMsg: '正在处理',
                waitTitle: '请等待',
                success: function (fp, o) {
                    Ext.Msg.show({
                        title: '操作提示eee ',
                        msg: ""+ o.result.info + "",
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        Ext.Msg.hide();
                        var panelStore = (Ext.getCmp("departmentgrid")).getStore();
                        panelStore.loadPage(1);//定位grid到第一页   //panelStore.loadPage(parseInt(panelStore.totalCount / panelStore.pageSize + 1));//定位grid到最后一页
                        me.reloadtreemenu();//有滞后！！！！
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
    exportExcelDepartment: function (record) {
        //把当前选择的数据导出到Excel表中
        // var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在把当前选择的数据导出到Excel表中，请等待..."});
        var myMask = new Ext.LoadMask(Ext.getCmp('departmentgrid'), {msg: "正在导出所有职工基本信息，请等待..."});
        myMask.show();
        //Ext.Ajax.timeout=9000000;  //9000秒
        Ext.Ajax.request({
            // url:'/appMis/employee/exportExcelEmployee?newvalue1='+newvalue1+"&newvalue2="+newvalue2+"&newvalue3="+newvalue3,
            url:'/appMis/department/exportExcelDepartment',
            method: 'POST',
            success://回调函数
                function (resp, opts) {
                    {
                        //myMask.hide();
                        myMask.destroy()
                        Ext.MessageBox.buttonText.ok = "完成"
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/tmp/" + resp.responseText + ".xls\"><b>保存该EXCEL文件</b></a>",
                            align: "centre",
                            buttons: Ext.MessageBox.OK
                        })
                    }
                },
            failure: function (resp, opts) {
                //myMask.hide();
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

    reloadtreemenu:function (){//重新刷新树状目录
        Ext.Ajax.request({//根据用户角色的权限用来确定界面上的内容哪些隐藏或不隐藏,生成操作树。
           // url:'/appMis/authentication/userlogin1?departmentName='+departmentName,
            url:'/appMis/authentication/userlogin1?departmentName=克拉玛依市',
            async:false,//同步
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                    var buttonMenu1 = resp.responseText.replace(/children/g, "menu");
                    buttonMenu1 = buttonMenu1.replace(/leaf : true/g, "action:'buttonMenu'");
                    var obj1 = eval('(' + buttonMenu1 + ')');//用/children/g可以把“children”全部换成"menu"，,再将获取的Json字符串转换为Json对象,
                    Ext.getCmp("departmentmainroot").getViewModel().set('systemButtonMenu',eval('(' + obj1.roots + ')'));//当前角色存入ViewModel
                    Ext.getCmp("departmentmainroot").getViewModel().set('systemTreeMenu',eval('(' + obj.roots + ')'));//当前角色存入ViewModel
                }
        });
         var path="";
        if(currentTreeNode!="p1000"){
            var i;
            for(i=1;i<currentTreeNode.length;i=i+3){
                path=path+"/p"+currentTreeNode.substring(1,i+3)
            }
            path="/p1000"+path
        }else{
            path="p1000"
        }
       // Ext.getCmp("treemainmenu").setRootNode(Ext.getCmp("treemainmenu").up('app-main').getViewModel().get('systemTreeMenu'))//重新设置树状目录
       // Ext.getCmp("treemainmenu").expandPath(path)//展开树状目录到path的位置
        Ext.getCmp("departmenttreemainmenu").setRootNode(Ext.getCmp("departmenttreemainmenu").up('app-departmentmain').getViewModel().get('systemTreeMenu'));//重新设置树状目录
        Ext.getCmp("departmenttreemainmenu").expandPath(path);//展开树状目录到path的位置
       // Ext.getCmp("departmenttreemainmenu").expandPath("/p1000");//展开树状目录到path的位置
    },

    //以增加行的方式增加单位的绩效年考核
    jxkhaddRowDepartment:function(){
        //alert("以增加行的方式增加单位的绩效年考核")
        if((currentTreeNode=="p1000")||(currentTreeNode.length <=4)){
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '不能在行政区下增加单位，必须选择分类！！ ',
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
        record = Ext.create('appMis.model.DepartmentJxkhModel', { // Create a model instance
            department:'克拉玛依'+Math.floor(Math.random()*10000),
            nd: '',
            hdjxgzzl: '0',
            ablhddjlxjxgz: '0',
            jxgzhzl: '0',
            nrjxdhgz: '0',
            nzycxjj: '0',
            treeId:''+Math.floor(Math.random()*10)+Math.floor(Math.random()*10)
        });
        this.jxkhadd(record)//在最后增加一条记录

    },
    jxkhadd:function(record,win){//insert和add都是调用它
        var panelStore = (Ext.getCmp("departmentJxkhgrid")).getStore();
        Ext.Ajax.request({
            url:'/appMis/department/saveJxkh?dep_tree_id=' + currentTreeNode.substr(1),
            async:false,//同步
            params:{
                // id:record.get("id"),
                department: record.get("department"),
                nd: record.get("nd"),
                hdjxgzzl: record.get("hdjxgzzl"),
                ablhddjlxjxgz: record.get("ablhddjlxjxgz"),
                jxgzhzl: record.get("jxgzhzl"),
                nrjxdhgz: record.get("nrjxdhgz"),
                nzycxjj: record.get("nzycxjj"),
                treeId: record.get("treeId")
            },
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                         Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                            (Ext.getCmp('departmentJxkhgrid')).setStore('DepartmentJxkhStore');
                            var panelStore =(Ext.getCmp('departmentJxkhgrid')).getStore();
                            panelStore.proxy.api.read = '/appMis/department/readDepartmentJxkh?dep_tree_id=' + currentTreeNode.substr(1);
                            panelStore.sort('nd', 'DESC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                            panelStore.loadPage(1);//刷新当前grid页面
                        },1500);


                        // win.close();//数据增加成功后再关闭窗口
                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },2500);
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据更新失败！可能原因：单位名称、代码、简称、组织机构代码重名或是某个字段为空了！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },2500)
            }

        });
    },
    jxkhdeleteDepartment:function(){//按钮按下对grid中已选择的记录进行删除
        var record = (Ext.getCmp("departmentJxkhgrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("departmentJxkhgrid")).getStore();
        var currPage = panelStore.currentPage;
        if(record.length == 0){
            Ext.MessageBox.show({
                title:"提示",
                msg:"请先选择您要删除的行数据!",
                buttons: Ext.MessageBox.OK
                //icon: Ext.MessageBox.INFO
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },2500);
            return;
        }else{
            //alert("panelStore.totalCount=11111=="+panelStore.totalCount)
            for(var i = 0; i < record.length; i++){
                if(this.jxkhremove(panelStore,record[i])==0){//删除选中的记录,返回0为删除失败，1为成功
                    return
                }
            }

            Ext.MessageBox.show({
                title:"提示",
                msg:"成功删除"+record.length+"行数据!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
                if(((panelStore.totalCount) % panelStore.pageSize)==0){
                    currPage=currPage-1
                    if(currPage==0)currPage=1
                }
                //alert("currPage=222==="+currPage)
                panelStore.loadPage(currPage);//刷新当前grid页面
            },1500);
        };

    },
    jxkhremove:function(store,record){
        var ss=1
        Ext.Ajax.request({
            url:'/appMis/department/deleteJxkh?id='+record.get("id"),
            async:false,//同步,true异步 async:true,//同步,true异步
            method:'POST',
            success:
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                       // Ext.getCmp("bbarDep").getStore().reload()
                        ss= 1
                    }else{

                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据删除失败！可能原因是：1、有下属单位，2、本单位下有职工信息 ！！！',
                            buttons: Ext.MessageBox.OK,
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },2000);
                        ss=0;
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据删除失败！可能原因是：1、有下属单位，2、本单位下有职工信息 ！！！',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },2000)
                ss= 0
            }
        })
        return ss
    }
});

