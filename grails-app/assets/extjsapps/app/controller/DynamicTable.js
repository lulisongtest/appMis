

Ext.define('appMis.controller.DynamicTable', {
    extend: 'Ext.app.Controller',
    init:function(){
        this.control({
            'dynamictableadd  button[action=save]': {//Window中使用,不能使用'>',
                click: this.saveDynamicTable
            },
            'dynamictableviewport > toolbar > button[action=add]': {
                click: this.addDynamicTable
            },
            'dynamictableviewport > toolbar > button[action=addRow]': {
                click: this.addRowDynamicTable
            },
            'dynamictableedit  button[action=save]': {//Window中使用,不能使用'>',
                click: this.updateDynamicTable
            },
            'dynamictableviewport >  toolbar >button[action=modify]': {
                click: this.modifyDynamicTable
            },
            'dynamictableviewport >  toolbar >button[action=delete]': {
                click: this.deleteDynamicTable
            },
            'dynamictableviewport >  toolbar >button[action=createDomain]': {
                click: this.createDomain
            },
            'dynamictableviewport >  toolbar >button[action=createController]': {
                click: this.createController
            },
            'dynamictableviewport >  toolbar >button[action=completeView]': {
                click: this.completeView
            },
            'dynamictableviewport > toolbar > button[action=deleteAll]': {
                click: this.deleteAll
            },
            'dynamictableviewport > toolbar > button[action=batchDelete]': {
                click: this.batchDeleteDynamicTable
            },
            'dynamictableviewport >  toolbar >button[action=exportToExcel]': {
                click: this.exportExcelDynamicTable
            },
            'dynamictableviewport >  toolbar >button[action=importFromExcel]': {
                click: this.importExcelDynamicTable
            }

        })
    },

    //生成全部视图
    completeView:function(){
        if((!Ext.getCmp("dynamicTableName").getValue())||(Ext.getCmp("dynamicTableName").getValue()=="all")){
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '请选择表名！ ',
                buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                Ext.Msg.hide();
            },1500);
            return
        }
        Ext.Ajax.request({
            url:'/appMis/dynamicTable/completeView?dynamicTableNameId='+Ext.getCmp("dynamicTableName").getValue(),
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '文件生成全部完成！ ',
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);
                    }else{
                        if(resp.responseText=='failure1'){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '此表不适合自动生成相关文件！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500);
                        }else{
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '部分文件生成失败！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500);
                        }
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '部分文件生成失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }

        });
    },

    //生成控制器
    createController:function(){
        if((!Ext.getCmp("dynamicTableName").getValue())||(Ext.getCmp("dynamicTableName").getValue()=="all")){
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '请选择表名！ ',
                buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                Ext.Msg.hide();
            },1500);
            return
        }
        // alert("创建controller........")
        Ext.Ajax.request({
            url:'/appMis/dynamicTable/createController?dynamicTableNameId='+Ext.getCmp("dynamicTableName").getValue(),
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '创建控制器成功！ ',
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);
                    }else{
                        if(resp.responseText=='failure1'){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '此表不适合自动生成控制器！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500);
                        }else{
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '创建控制器失败！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500);
                        }
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '创建控制器失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }

        });
    },
    //生成域
    createDomain:function(record){
        if((!Ext.getCmp("dynamicTableName").getValue())||(Ext.getCmp("dynamicTableName").getValue()=="all")){
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '请选择表名！ ',
                buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                Ext.Msg.hide();
            },1500);
            return
        }
        Ext.Ajax.request({
            url:'/appMis/dynamicTable/createDomain?dynamicTableNameId='+Ext.getCmp("dynamicTableName").getValue(),
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '创建域成功！ ',
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);
                    }else{
                        if(resp.responseText=='failure1'){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '此表不适合自动生成域！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500);
                        }else{
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '创建域失败！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500);
                        }
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '创建域失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }

        });
    },



    //从Excel表导入到数据库
    importExcelDynamicTable:function(button){
        var panel1 = button.up('panel');
        form = panel1.down('form');
        if (form.form.isValid()) {
                if (Ext.getCmp('dynamicTableexcelfilePath').getForm().findField('dynamicTableexcelfilePath').getValue() == '') {//获取name: 'dynamicTableexcelfilePath'的值
                     Ext.Msg.alert('系统提示', '请选择你要上传的文件...');
                     return;
                }
            form.getForm().submit({
                url:'/appMis/dynamicTable/importExcelDynamicTable',
                method:"POST",
                waitMsg: '正在处理',
                waitTitle: '请等待',
                success: function(fp, o) {
                    //alert("温馨提示" + o.result.info+"")
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: ""+o.result.info+"",
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        Ext.getCmp('rbbardynamicTable').getStore().reload();
                        Ext.Msg.hide();
                    },3500)

                },
                failure: function(fp, o) {
                    //alert("警告", "" + o.result.info + "");
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: '文件导入失败！ ',
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        Ext.Msg.hide();
                    },3500)
                }

            })
        }
    },
    //把当前选择的数据导出到Excel表中，//直接导出到客户端下载
    exportExcelDynamicTable:function(record){
        var s="/appMis/dynamicTable/exportExcelDynamicTable?tableNameId=" + Ext.getCmp('dynamicTableName').getValue();
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

    //把当前选择的数据导出到Excel表中，  //先导出到服务器端，然后下载
    exportExcelDynamicTablebak:function(record){
        // var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在把当前选择的数据导出到Excel表中，请等待...", removeMask  : true});//不知道为什么Ext.getBody()不可以
        var myMask = new Ext.LoadMask(Ext.getCmp("dynamictablegrid"), {msg:"正在把当前选择的数据导出到Excel表中，请等待...", removeMask  : true});
        myMask.show();
        //Ext.Ajax.timeout=9000000;  //9000秒
        Ext.Ajax.request({
            url:"/appMis/dynamicTable/exportExcelDynamicTable?tableNameId=" + Ext.getCmp('dynamicTableName').getValue(),
            method : 'POST',
            success://回调函数
                function(resp,opts) {
                    {
                        // myMask.hide();
                        myMask.destroy()
                        Ext.MessageBox.buttonText.ok="完成";
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "<a href=\"http://"+window.location.host+"/appMis/static/tmp/"+resp.responseText+".xlsx\"><b>保存该EXCEL文件</b></a>",
                            align: "centre",
                            buttons: Ext.MessageBox.OK
                        })
                    }
                },
            failure: function(resp,opts) {
                myMask.hide();
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据导出到Excel失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }
        });
    },






    //删除系统数据表中所有信息
    batchDeleteDynamicTable:function(){
        Ext.MessageBox.show({
           title:"提示",
           msg:"请先导出系统数据表中所有信息到Excel文件!再进行删除系统数据表中所有信息。您要继续吗？",
           // buttons: Ext.Msg.OKCANCEL,// 配对是：if((e=="ok")){
           buttons: Ext.Msg.YESNO,
           fn:function(e){
                if((e=="yes")){
                    var myMask = new Ext.LoadMask(Ext.getCmp("dynamictablegrid"), {msg:"正在删除系统数据表中当前所选信息，请等待...", removeMask  : true});
                    myMask.show();
                    //Ext.Ajax.timeout=9000000;  //9000秒???
                    Ext.Ajax.request({
                        url:'/appMis/dynamicTable/batchDelete?dynamicTableNameId='+Ext.getCmp("dynamicTableName").getValue(),
                        method:'POST',
                        success:
                            function(resp,opts) {//成功后的回调方法
                                if(resp.responseText=='success'){
                                    // myMask.hide();
                                    myMask.destroy()
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '删除系统数据表中所有信息成功！ ',
                                        buttons: Ext.MessageBox.OK
                                    });
                                    setTimeout(function () {
                                        Ext.getCmp('rbbardynamicTable').getStore().reload();
                                        Ext.Msg.hide();
                                    },1500);

                                }else{
                                    // myMask.hide();
                                    myMask.destroy()
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '删除系统数据表中所有信息失败！ ',
                                        buttons: Ext.MessageBox.OK
                                    });
                                    setTimeout(function () {
                                        Ext.Msg.hide();
                                    },1500);
                                }
                            },
                        failure: function(resp,opts) {
                            // myMask.hide();
                            myMask.destroy()
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据删除失败！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500)
                        }
                    })
                }
            }
        })

    },

    //删除已生成的全部信息
    deleteAll:function(){
        if((Ext.getCmp("dynamicTableName").getValue()==null)||(Ext.getCmp("dynamicTableName").getValue()=="全部")){
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '请选择表名！ ',
                buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                Ext.Msg.hide();
            },1500);
            return
        }
        Ext.Ajax.request({
            url:'/appMis/dynamicTable/deleteAll?dynamicTableNameId='+Ext.getCmp("dynamicTableName").getValue(),
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '已生成的相关文件及信息全部删除完成！ ',
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);
                    }else{
                        if(resp.responseText=='failure1'){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '此表无需操作此功能！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500);
                        }else{
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '已生成的部分文件及信息删除失败！ ',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            },1500);
                        }
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '已生成的部分文件及信息删除失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }

        });
    },



    editDynamicTable:function(grid, record){//双击grid中的某个记录进行修改
        alert("暂时还不提供此功能");
        /*var view = Ext.widget('dynamictableedit');
         view.down('form').loadRecord(record);*/
    },

    modifyDynamicTable:function(){//按钮按下对grid中已选择的记录进行修改
        alert("暂时还不提供此功能");
        /*var record = (Ext.getCmp("dynamictablegrid")).getSelectionModel().getSelection();
         if(record.length != 1){
         Ext.MessageBox.show({
         title:"提示",
         msg:"请先选择您要修改的一行且只能选择一行数据!",
         buttons: Ext.MessageBox.OK
         //icon: Ext.MessageBox.INFO
         })
         setTimeout(function () {
         Ext.Msg.hide();
         },2500);
         return;
         }
         var view = Ext.widget('dynamictableedit');
         view.down('form').loadRecord(record[0]);*/
    },

    updateDynamicTable:function(button) {//编辑记录后进行保存更新
        var win    = button.up('window');
        form   = win.down('form');
        record = form.getRecord();
        values = form.getValues();
        record.set(values);
        win.close();
        // this.getDepartmentStoreStore().sync();
    },


    addDynamicTable:function(){//打开新增加的部门输入窗口
        alert("暂时还不提供此功能");
        /* var record = Ext.create('appMis.model.DynamicTableModel');
         //日期设初值
         var view = Ext.widget('dynamictableadd');
         view.down('form').loadRecord(record);*/
    },

    saveDynamicTable:function(button){//保存新增加的部门
        var win    = button.up('window');
        form   = win.down('form');
        record =  form.getRecord();//创建的新记录
        values = form.getValues();
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        this.add(record);
        win.close();
    },

    //增加新记录
    addRowDynamicTable:function(){//以增加行的方式增加一个部门
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 2,
            autoCancel: false
        });
        record = Ext.create('appMis.model.DynamicTableModel', { // Create a model instance
            tableNameId:'table999',
            tableName:'新表名',
            fieldNameId:'table1F999',
            fieldName:'新字段名',
            fieldType:'字符',
            fieldLength:'255'
        });
        this.add(record)//在最后增加一条记录
    },

    add:function(record){//insert和add都是调用它
        var panelStore = (Ext.getCmp("dynamictablegrid")).getStore();
        //panelStore.proxy.api.read="dynamicTable/listDynamicfield?newvalue="+'all';
       // panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
        Ext.Ajax.request({
            url:'/appMis/dynamicTable/save',
            params:{
                tableNameId:record.get("tableNameId"),
                tableName:record.get("tableName"),
                fieldNameId:record.get("fieldNameId"),
                fieldName:record.get("fieldName"),
                fieldType:record.get("fieldType"),
                fieldLength:record.get("fieldLength")
            },
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        //panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加成功！ ',
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            Ext.getCmp('dynamicTableName').setValue("新表名")
                            panelStore.proxy.api.read = "/appMis/dynamicTable/readDynamicTable?tableNameId=table999";
                            panelStore.pageSize = pageSize;
                            panelStore.loadPage(1);
                            Ext.Msg.hide();
                        },1500);
                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加失败！ ',
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据增加失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }

        });
    },



   //按钮按下对grid中已选择的记录进行删除
    deleteDynamicTable:function() {
        var record = (Ext.getCmp("dynamictablegrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("dynamictablegrid")).getStore();
        var currPage = panelStore.currentPage;
        var message = '';
        if (record.length == 0) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请先选择您要删除的行数据!",
                buttons: Ext.MessageBox.OK
                //icon: Ext.MessageBox.INFO
            });
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
        }else{
            if (record.length == 1) { // 如果只选择了一条
                message = ' 『' + record[0].get("tableName") + '表的' + record[0].get("fieldName") + '字段』 吗?';
            }else{ // 选择了多条记录
                message = '<ol>';
                Ext.Array.each(record, function (record) {
                    message += '<li>' + record.get("tableName") + '表的' + record.get("fieldName") + '字段' + '</li>';
                });
                message += '</ol>';
                message = '以下 ' + record.length + ' 条记录吗?' + message;
            }
            //var me = this ;//这也可以解决this.remove(panelStore, record[i]); 中this调用的问题否则this就是Ext.MessageBox
            Ext.MessageBox.confirm('确定删除', '确定要删除 <strong>' + '系统数据表' + '</strong> 中的' + message, function (btn) {
                if (btn == 'yes') {
                    for (var i = 0; i < record.length; i++) {
                        this.remove(panelStore, record[i]); //删除选中的记录
                    }
                    panelStore.loadPage(currPage);//刷新当前grid页面
                    if (((panelStore.totalCount - record.length) % panelStore.pageSize) == 0) {
                        currPage = currPage - 1;
                        if (currPage == 0)currPage = 1
                    }
                    panelStore.loadPage(currPage);//刷新当前grid页面
                }
            },this)//这时用this可以解决之前的this.remove(panelStore, record[i]); 中this调用的问题，否则this就是Ext.MessageBox
        }
    },

    remove:function(store,record){
        Ext.Ajax.request({
            url:'/appMis/dynamicTable/delete?id='+record.get("id"),
            method:'POST',
            success:
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.getCmp("rbbardynamicTable").getStore().reload()
                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据删除失败！ ',
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据删除失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }
        })
    }
});
