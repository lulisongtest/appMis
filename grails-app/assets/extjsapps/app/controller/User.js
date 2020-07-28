var currentRecord=0
var oldpassword=""
var userfirst=1//第一次或重新打开form////第一次按保存时，激活Store的update:function(store,record)更新成功,接着修改了某个字段，第二次按保存时不激活Store的update，无法更新，不知为什么？用userfirst解决。
Ext.define('appMis.controller.User', {
    extend: 'Ext.app.Controller',
    alias: 'controller.user',
    init:function(){
        this.control({
            'selectDepartment   treepanel':{
                //itemmousedown: this.loadMenu2//注意itemmousedown与itemclick区别
                itemclick: this.changUserDepartment//更换用户的单位
            },
            'usergridviewport': {
//                itemdblclick: this.editUser//双击grid中的某个记录进行修改，暂时不用
            },
            'useredit   button[action=first]': {//Window中使用,不能使用'>'符号,
                click: this.firstUser
            },
            'useredit   button[action=preview]': {//Window中使用,不能使用'>'符号,
                click: this.previewUser
            },
            'useredit   button[action=next]': {//Window中使用,不能使用'>'符号,
                click: this.nextUser
            },
            'useredit   button[action=last]': {//Window中使用,不能使用'>'符号,
                click: this.lastUser
            },
            'useredit   button[action=save]': {//Window中使用,不能使用'>'符号,
                click: this.updateUser
            },
            'useredit   button[action=close]': {//Window中使用,不能使用'>'符号,
                click: this.closeUserEdit
            },
            'useradd    button[action=save]': {//Window中使用,不能使用'>'符号,
                click: this.saveUser
            },
            // 'usermodify   button[action=userModifyClose]': {//Window中使用,不能使用'>'符号,
            //      click: this.userModifyClose
            // },
            // 'usermodify   button[action=userModifySave]': {//Window中使用,不能使用'>'符号,
            //     click: this.userModifySave
            // },
            'userviewport > toolbar > button[action=add]': {
                click: this.addUser
            },
            'userviewport > toolbar > button[action=addRow]': {
                click: this.addRowUser
            },
            'userviewport > toolbar > button[action=modify]': {
                click: this.modifyUser
            },
            'userviewport > toolbar > button[action=delete]': {
                click: this.deleteUser
            },
            //初次生成所有单位用户信息
            'userviewport > toolbar > button[action=generateUser]': {
                click: this.generateUser
            },
            'userviewport >  toolbar >button[action=exportToExcel]': {
                click: this.exportExcelUser
            },
            'userviewport >  toolbar >button[action=importFromExcel]': {
                click: this.importExcelUser
            },
            'usermodify   button[action=userModifyClose]': {//Window中使用,不能使用'>'符号,
                click: this.userModifyClose
            },
            'usermodify   button[action=userModifySave]': {//Window中使用,不能使用'>'符号,
                click: this.userModifySave
            },
            'moifyPassword   button[action=userModifyClose]': {//Window中使用,不能使用'>'符号,
                click: this.moifyPasswordClose
            },
            'moifyPassword   button[action=userModifySave]': {//Window中使用,不能使用'>'符号,
                click: this.moifyPasswordSave
            }
        })
    },
    //更换用户的单位
    changUserDepartment:function(selModel, record1){
        if(Ext.getCmp("gridSelectDepartment")){
            record= (Ext.getCmp("usergrid")).getSelectionModel().getSelection()[0];//取得选择的记录
            Ext.Ajax.request({
                url:'/appMis/user/changUserDepartment',
                params:{
                    id : record.get("id"),
                    username:record.get("username"),
                    password:record.get("password"),
                    enabled:record.get("enabled"),
                    accountExpired:record.get("accountExpired"),
                    accountLocked:record.get("accountLocked"),
                    passwordExpired:record.get("passwordExpired"),
                    truename:record.get("truename"),
                    chineseAuthority:record.get("chineseAuthority"),
                    department:record1.get('text'),
                    treeId:record1.get('id').substr(1),
                    email:record.get("email"),
                    phone:record.get("phone")
                },
                success:
                    function(resp,opts) {//成功后的回调方法
                        if(resp.responseText=='success'){
                            var panelStore = (Ext.getCmp('usergrid')).getStore();
                            panelStore.loadPage(panelStore.currentPage);
                        }else{
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据更新失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                var panelStore = (Ext.getCmp('usergrid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                                Ext.Msg.hide();
                            },1500);
                        }
                    },
                failure: function(resp,opts) {
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: '数据更新失败！ ',
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        var panelStore = (Ext.getCmp('usergrid')).getStore();
                        panelStore.loadPage(panelStore.currentPage);
                        Ext.Msg.hide();
                    },1500);
                }
            });
            //view.close()
        }else{
            Ext.Ajax.request({
                url: '/appMis/department/readDepartmentTitle?dep_tree_id=' + record1.get('id').substr(1),
                async: false,//同步
                success://回调函数
                    function (resp, opts) {//成功后的回调方法
                        departmentTitle =resp.responseText
                    }
            });
            if(Ext.getCmp("addSelectDepartment")){
               // alert("在Add中点击")
                Ext.getCmp("addDepartment").setValue(departmentTitle+record1.get('text'));
                Ext.getCmp("addtreeId").setValue(record1.get('id').substr(1))
            }else{
               // alert("在Edit中点击")//
                Ext.getCmp("editDepartment").setValue(departmentTitle+record1.get('text'));
                Ext.getCmp("edittreeId").setValue(record1.get('id').substr(1))
            }

        }
        // Ext.getCmp("selectDepartment").close()
        view1.close()
    },




    moifyPasswordClose:function(button){
        Ext.getCmp('moifyPassword').destroy()//'user'在'appMis.view.desktop.UserWindow'中
    },
    moifyPasswordSave:function(button){
       // alert("save moifyPasswordSave!!!")
        form1   = Ext.getCmp('moifyPassword').down('form');
        var record = form1.getRecord();//选中编辑的记录
        var values = form1.getValues();
        if((values.newpassword==values.repassword)&&(values.newpassword!="")){
           this.userMessageSave(values.newpassword);//要加this.
        }else{
            Ext.Msg.show({
                title: '操作提示 ',
                msg: '二次输入密码不一致！修改密码失败！ ',
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },1500);
        }
        Ext.getCmp('moifyPassword').destroy();//'user'在'appMis.view.desktop.UserWindow'中
    },

    userModifyClose:function(button){
        Ext.getCmp('user').destroy()//'user'在'appMis.view.desktop.UserWindow'中
    },

    //编辑修改个人信息【编辑窗口】的记录后进行保存更新
    userModifySave:function(){
        this.userMessageSave("")
    },

    //修改个人信息
    userMessageSave:function(newPassword){
        var form   = Ext.getCmp('usermodify').down('form');
        var record = form.getRecord();//选中编辑的记录
        var values = form.getValues();
        if(values.enabled==null){//复选框如为“不选择”，则values.enabled获取的值为null
            values.enabled=false
        }
        if(values.accountExpired==null){//复选框如为“不选择”，则values.accountExpired获取的值为null
            values.accountExpired=false
        }
        if(values.accountLocked==null){//复选框如为“不选择”，则values.accountLocked获取的值为null
            values.accountLocked=false
        }
        if(values.passwordExpired==null){//复选框如为“不选择”，则values.passwordExpired获取的值为null
            values.passwordExpired=false
        }
        var passPattern = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*()_+`\-={}:";'<>?,.\/]).{8,64}$/; //密码至少包含 数字和英文，长度6-20
        if((newPassword=="") || passPattern.test(newPassword)){
            //没有修改密码，或者修改了密码就必须至少包含 数字和英文，长度6-20
        }else{
            Ext.Msg.show({
                title:"提示",
                msg:"密码组成不符合安全要求，密码必须至少有一个字母, 数字, 和特殊字符（!@#$%^&*），长度8-64位",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },3500);
            return false;
        }
        record.set(values);////第一次按保存时，激活Store的update:function(store,record)更新成功,接着修改了某个字段，第二次按保存时不激活Store的update，无法更新，不知为什么？
        Ext.Ajax.request({
            url:'/appMis/user/update',
            params:{
                id : record.get("id"),
                username:record.get("username"),
                password:newPassword,//更新的密码
                enabled:record.get("enabled"),
                accountExpired:record.get("accountExpired"),
                accountLocked:record.get("accountLocked"),
                passwordExpired:record.get("passwordExpired"),
                truename:record.get("truename"),
                chineseAuthority:record.get("chineseAuthority"),
                department:record.get("department"),
                treeId:record.get("treeId"),
                email:record.get("email"),
                phone:record.get("phone")
            },
            success:
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新成功！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);
                        // Ext.getCmp('user').destroy()

                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            Ext.Msg.hide();
                        },1500);
                    }
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据更新失败！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500);
            }
        });
    },



    //初次生成所有单位用户信息
    generateUser:function(button){
        var myMask = new Ext.LoadMask(Ext.getCmp("usergrid"), {msg:"正在初次生成所有单位用户信息，时间稍长，请耐心等待...", removeMask  : true});
        myMask.show();
        Ext.Ajax.timeout=0;  //为0，代表永不超时 // Ext.Ajax.timeout=9000000000000;  //9000秒
        Ext.Ajax.request({
            url:'/appMis/user/generateUser',
            method : 'POST',
            async:false,  //timeout:0,  //timeout: 30000, //超时时间：30秒，为0，代表永不超时
            success://回调函数
                function(resp,opts){
                    var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                    myMask.destroy();
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: obj.info,
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        var panelStore=Ext.getCmp("usergrid").getStore();
                        panelStore.proxy.api.read = "/appMis/user/readUser";
                        panelStore.loadPage(1)
                        Ext.Msg.hide();
                    },2500)
                },
            failure: function(resp,opts) {
               // myMask.destroy();
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '数据导出到Excel失败！!!!!!!! ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }
        });
    },

    //从Excel表导入到数据库
    importExcelUser:function(button){
        var panel1 = button.up('panel');
        form = panel1.down('form');
        if (form.form.isValid()) {
            if (Ext.getCmp('userexcelfilePath').getForm().findField('userexcelfilePath').getValue() == '') {//获取name: 'userexcelfilePath'的值
                Ext.Msg.alert('系统提示', '请选择你要上传的文件...');
                return;
            }
            form.getForm().submit({
                url:'/appMis/user/importExcelUser',
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
                        var panelStore = (Ext.getCmp('usergrid')).getStore();
                        panelStore.loadPage(panelStore.currentPage);
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
    //把当前选择的数据导出到Excel表中
    exportExcelUser:function(record){
        // var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在把当前选择的数据导出到Excel表中，请等待...", removeMask  : true});//不知道为什么Ext.getBody()不可以
        var myMask = new Ext.LoadMask(Ext.getCmp("usergrid"), {msg:"正在把当前选择的数据导出到Excel表中，请等待...", removeMask  : true});
        myMask.show();
        Ext.Ajax.timeout=0;  //9000秒
        Ext.Ajax.request({
            url:'/appMis/user/exportExcelUser',
            method : 'POST',
            async: false,
            success://回调函数
                function(resp,opts) {
                    {
                        // myMask.hide();
                        myMask.destroy()
                        Ext.MessageBox.buttonText.ok="完成";
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "<a href=\"http://"+window.location.host+"/appMis/static/tmp/"+resp.responseText+".xls\"><b>保存该EXCEL文件</b></a>",
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

    //删除表中所有信息
    batchDeleteUser:function(){
        Ext.MessageBox.show({
            title:"提示",
            msg:"请先导出表中所有信息到Excel文件!再进行删除表中所有信息。您要继续吗？",
            // buttons: Ext.Msg.OKCANCEL,// 配对是：if((e=="ok")){
            buttons: Ext.Msg.YESNO,
            fn:function(e){
                if((e=="yes")){
                    var myMask = new Ext.LoadMask(Ext.getCmp("usergrid"), {msg:"正在删除数据表中当前所选信息，请等待...", removeMask  : true});
                    myMask.show();
                    //Ext.Ajax.timeout=9000000;  //9000秒???
                    Ext.Ajax.request({
                        url:'/appMis/user/batchDelete?userNameId='+Ext.getCmp("userName").getValue(),
                        method:'POST',
                        success:
                            function(resp,opts) {//成功后的回调方法
                                if(resp.responseText=='success'){
                                    // myMask.hide();
                                    myMask.destroy()
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '删除数据表中所有信息成功！ ',
                                        buttons: Ext.MessageBox.OK
                                    });
                                    setTimeout(function () {
                                        var panelStore = (Ext.getCmp('usergrid')).getStore();
                                        panelStore.loadPage(panelStore.currentPage);
                                        Ext.Msg.hide();
                                    },1500);

                                }else{
                                    // myMask.hide();
                                    myMask.destroy()
                                    Ext.Msg.show({
                                        title: '操作提示 ',
                                        msg: '删除数据表中所有信息失败！ ',
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

    /* userModifyClose:function(button){
         Ext.getCmp('user').destroy()
     },
     userModifySave:function(button){//编辑【编辑窗口】的记录后进行保存更新
         form   = view.down('form');
         var record = form.getRecord();//选中编辑的记录
         var values = form.getValues();
         if(values.enabled==null){//复选框如为“不选择”，则values.enabled获取的值为null
             values.enabled=false
         }
         if(values.accountExpired==null){//复选框如为“不选择”，则values.accountExpired获取的值为null
             values.accountExpired=false
         }
         if(values.accountLocked==null){//复选框如为“不选择”，则values.accountLocked获取的值为null
             values.accountLocked=false
         }
         if(values.passwordExpired==null){//复选框如为“不选择”，则values.passwordExpired获取的值为null
             values.passwordExpired=false
         }
         //form.updateRecord()////第一次按保存时，激活Store的update:function(store,record)更新成功,接着修改了某个字段，第二次按保存时不激活Store的update，无法更新，不知为什么？
         record.set(values);////第一次按保存时，激活Store的update:function(store,record)更新成功,接着修改了某个字段，第二次按保存时不激活Store的update，无法更新，不知为什么？

         // if(userfirst!=1){
         Ext.Ajax.request({
             url:'/appMis/user/update',
             params:{
                 id : record.get("id"),
                 username:record.get("username"),
                 password:record.get("password"),
                 enabled:record.get("enabled"),
                 accountExpired:record.get("accountExpired"),
                 accountLocked:record.get("accountLocked"),
                 passwordExpired:record.get("passwordExpired"),
                 truename:record.get("truename"),
                 chineseAuthority:record.get("chineseAuthority"),
                 department:record.get("department"),
                 treeId:record.get("treeId"),
                 email:record.get("email"),
                 phone:record.get("phone")
             },
             success:
                 function(resp,opts) {//成功后的回调方法
                     if(resp.responseText=='success'){
                         Ext.Msg.show({
                             title: '操作提示 ',
                             msg: '数据更新成功！ ',
                             buttons: Ext.MessageBox.OK
                         })
                         setTimeout(function () {
                             Ext.Msg.hide();
                         },1500);
                         // Ext.getCmp('user').destroy()

                     }else{
                         Ext.Msg.show({
                             title: '操作提示 ',
                             msg: '数据更新失败！ ',
                             buttons: Ext.MessageBox.OK
                         })
                         setTimeout(function () {
                             Ext.Msg.hide();
                         },1500);
                     }
                 },
             failure: function(resp,opts) {
                 Ext.Msg.show({
                     title: '操作提示 ',
                     msg: '数据更新失败！ ',
                     buttons: Ext.MessageBox.OK
                 })
                 setTimeout(function () {
                     Ext.Msg.hide();
                 },1500);
             }
         });
         //  }
         //  userfirst=0
         // win.close();
         // (Ext.getCmp("usergrid")).getSelectionModel().deselectAll();//修改结束后，清除选择。
         //(Ext.getCmp("usergrid")).getSelectionModel().clearSelections();//修改结束后，清除选择。
     },*/


    modifyUser:function(){//[按钮]按下对grid中已选择的记录进行修改
        currentRecord=0
        userfirst=1//第一次或重新打开form
        records= (Ext.getCmp("usergrid")).getSelectionModel().getSelection();//取得选择的记录
        if(records.length == 0){
            Ext.MessageBox.show({
                title:"提示",
                msg:"请选择您要修改的数据!（注：必须至少选择一条数据）",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },2500);
            return;
        }

        view = Ext.widget('useredit');
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图

        // Ext.getCmp("lulisong").focus()
    },


    firstUser:function(button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord=0
        view.down('form').loadRecord(records[0]);//显示编辑视图
    },
    previewUser:function(button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord=currentRecord-1
        if(currentRecord==-1)currentRecord=0
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图
    },
    nextUser:function(button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord=currentRecord+1
        if(currentRecord==records.length)currentRecord=records.length-1
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图

    },
    lastUser:function(button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord=records.length-1
        view.down('form').loadRecord(records[records.length-1]);//显示编辑视图
    },
    //编辑【编辑窗口】的记录后进行保存更新
    updateUser:function(button) {
        var win    = button.up('window');
        form   = win.down('form');
        // form   = view.down('form');//也可以
        var record = form.getRecord();//选中编辑的记录
        var values = form.getValues();

        //密码至少包含 数字和英文，长度6-20
       // var passPattern = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/;
        var passPattern = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*()_+`\-={}:";'<>?,.\/]).{8,64}$/;
       if(values.password.substr(0,8)=="{bcrypt}"){
            values.password=""
        }else{
            if(!passPattern.test(values.password)){
                // alert("密码至少包含 数字和英文，长度6-20位")
                Ext.Msg.show({
                    title:"提示",
                    msg:"密码组成不符合安全要求，密码必须至少有一个字母, 数字, 和特殊字符（!@#$%^&*），长度8-64位",
                    buttons: Ext.MessageBox.OK
                })
                // setTimeout(function () {
                //     Ext.Msg.hide();
                // },2500);
                return false;
            }
        }


        if(values.enabled==null){//复选框如为“不选择”，则values.enabled获取的值为null
            values.enabled=false
        }
        if(values.accountExpired==null){//复选框如为“不选择”，则values.accountExpired获取的值为null
            values.accountExpired=false
        }
        if(values.accountLocked==null){//复选框如为“不选择”，则values.accountLocked获取的值为null
            values.accountLocked=false
        }
        if(values.passwordExpired==null){//复选框如为“不选择”，则values.passwordExpired获取的值为null
            values.passwordExpired=false
        }
        //form.updateRecord()////第一次按保存时，激活Store的update:function(store,record)更新成功,接着修改了某个字段，第二次按保存时不激活Store的update，无法更新，不知为什么？用userfirst解决。
        record.set(values);////第一次按保存时，激活Store的update:function(store,record)更新成功,接着修改了某个字段，第二次按保存时不激活Store的update，无法更新，不知为什么？用userfirst解决。
        if(userfirst!=1){
            Ext.Ajax.request({
                url:'/appMis/user/update',
                params:{
                    id : record.get("id"),
                    username:record.get("username"),
                    password:record.get("password"),
                    enabled:record.get("enabled"),
                    accountExpired:record.get("accountExpired"),
                    accountLocked:record.get("accountLocked"),
                    passwordExpired:record.get("passwordExpired"),
                    truename:record.get("truename"),
                    chineseAuthority:record.get("chineseAuthority"),
                    department:record.get("department"),
                    treeId:record.get("treeId"),
                    email:record.get("email"),
                    phone:record.get("phone")
                },
                success:
                    function(resp,opts) {//成功后的回调方法
                        if(resp.responseText=='success'){
                            var panelStore = (Ext.getCmp('usergrid')).getStore();
                            panelStore.loadPage(panelStore.currentPage);
                        }else{
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '数据更新失败！ ',
                                buttons: Ext.MessageBox.OK
                            })
                            setTimeout(function () {
                                var panelStore = (Ext.getCmp('usergrid')).getStore();
                                panelStore.loadPage(panelStore.currentPage);
                                Ext.Msg.hide();
                            },1500);
                        }
                    },
                failure: function(resp,opts) {
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: '数据更新失败！ ',
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        var panelStore = (Ext.getCmp('usergrid')).getStore();
                        panelStore.loadPage(panelStore.currentPage);
                        Ext.Msg.hide();
                    },1500);
                }
            });
        }
        userfirst=0
        // win.close();
        // (Ext.getCmp("usergrid")).getSelectionModel().deselectAll();//修改结束后，清除选择。
        //(Ext.getCmp("usergrid")).getSelectionModel().clearSelections();//修改结束后，清除选择。
    },
    closeUserEdit:function() {//关闭UserEdit窗口
        //(Ext.getCmp("usergrid")).getSelectionModel().deselectAll();//修改结束后，清除选择。
        (Ext.getCmp("usergrid")).getSelectionModel().clearSelections();//修改结束后，清除选择。
        (Ext.getCmp("usergrid")).getView().refresh()//以上命令行放在view.close()后就无效。不知为什么？
        view.close()
        //(Ext.getCmp("usergrid")).getSelectionModel().deselectAll();//修改结束后，清除选择。放在view.close()后就无效。
        //(Ext.getCmp("usergrid")).getSelectionModel().clearSelections();//修改结束后，清除选择。放在view.close()后就无效。
        //(Ext.getCmp("usergrid")).getView().refresh()//以上命令行放在view.close()后就无效。不知为什么？

    },
    //打开新增加的输入窗口
    addUser:function(){
        var  record = Ext.create('appMis.model.UserModel');//创建新的记录
         //var view = Ext.widget('useradd');
        var view =Ext.create('appMis.view.user.UserAdd');
        view.down('form').loadRecord(record);
    },

    //保存新增窗口 的新增加的基础数据
    saveUser:function(button){
        var win    = button.up('window');
        form   = win.down('form');
        var record =  form.getRecord();//创建的新记录
        var values = form.getValues()

        //密码至少包含 数字和英文，长度6-20
       // var passPattern = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/;
        var passPattern = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*()_+`\-={}:";'<>?,.\/]).{8,64}$/;
        if(passPattern.test(values.password)){
        }else{
            // alert("密码至少包含 数字和英文，长度6-20位")
            Ext.Msg.show({
                title:"提示",
                msg:"密码组成不符合安全要求，密码必须至少有一个字母, 数字, 和特殊字符（!@#$%^&*），长度8-64位",
                buttons: Ext.MessageBox.OK
            })
            // setTimeout(function () {
            //     Ext.Msg.hide();
            // },2500);
            return false;
        }

        if(values.enabled==null){//复选框如为“不选择”，则values.enabled获取的值为null
            values.enabled=false
        }
        if(values.accountExpired==null){//复选框如为“不选择”，则values.accountExpired获取的值为null
            values.accountExpired=false
        }
        if(values.accountLocked==null){//复选框如为“不选择”，则values.accountLocked获取的值为null
            values.accountLocked=false
        }
        if(values.passwordExpired==null){//复选框如为“不选择”，则values.passwordExpired获取的值为null
            values.passwordExpired=false
        }
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        this.add(record)
        win.close();

    },
    deleteUser:function(){//按钮按下对grid中已选择的记录进行删除

        var record = (Ext.getCmp("usergrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("usergrid")).getStore();
        var currPage = panelStore.currentPage;
        if(record.length == 0){
            Ext.Msg.show({
                title:"提示",
                msg:"请先选择您要删除的行数据!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },2500);
            return;
        }else{
            for(var i = 0; i < record.length; i++){
                this.remove(panelStore,record[i])//删除选中的记录
            }
            Ext.Msg.show({
                title:"提示",
                msg:"恭喜您，" + record.length + "成功删除数据!",
                buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                if (((panelStore.totalCount) % panelStore.pageSize) == 0) {
                    if (((panelStore.totalCount) / panelStore.pageSize) >=currPage ){
                        currPage = currPage;
                    }else{
                        currPage = currPage-1;
                    }
                    if (currPage == 0)currPage = 1
                }
                panelStore.loadPage(currPage);//刷新当前grid页面
                //panelStore.loadPage(1);//刷新当前grid页面
                /*if((panelStore.totalCount % panelStore.pageSize)==0){
                    currPage=currPage-1
                    if(currPage==0)currPage=1
                }
                panelStore.loadPage(currPage);//刷新当前grid页面*/
                Ext.Msg.hide();
            },1500);
        }
    },
    remove:function(store,record){
        Ext.Ajax.request({
            url:'/appMis/user/delete?id='+record.get("id"),
            async: false,//同步
            method:'POST',
            success:
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.getCmp("bbarUser").getStore().reload()//重新刷新，总记录数已经变化
                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据删除失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
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
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }
        })
    },

    addRowUser:function(){//以增加行的方式增加一个部门
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 2,
            autoCancel: false
        });
        var record = Ext.create('appMis.model.UserModel', { // Create a model instance
            username:'~username'+Math.floor(Math.random()*1000),
            password:'123',
            enabled:1,
            accountExpired:0,
            accountLocked:0,
            passwordExpired:0,
            truename:'新用户',
            chineseAuthority:'普通用户',
            department:'新部门',
            treeId:'',
            email:'387861782@qq.com',
            phone:'15309923872'
        });
        this.add(record)
    },

    add:function(record){//insert和add都是调用它
        Ext.Ajax.request({
            url:'/appMis/user/save',
            async:false,
            params:{
                username:record.get("username"),
                password:record.get("password"),
                enabled:record.get("enabled"),
                accountExpired:record.get("accountExpired"),
                accountLocked:record.get("accountLocked"),
                passwordExpired:record.get("passwordExpired"),
                truename:record.get("truename"),
                chineseAuthority:record.get("chineseAuthority"),
                department:record.get("department"),
                treeId:record.get("treeId"),
                email:record.get("email"),
                phone:record.get("phone")
            },
            method : 'POST',
            success://回调函数
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加成功！ ',
                            buttons: Ext.MessageBox.OK
                        });
                        setTimeout(function () {
                            var panelStore =(Ext.getCmp("usergrid")).getStore();
                            //panelStore.proxy.api.read="/appMis/user/queryData?queryAuthority="+""+"&queryDepartment="+""+"&queryUsername="+"";
                            panelStore.proxy.api.read="/appMis/user/readUser"
                            //panelStore.loadPage(1);//定位grid到第一页
                            panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
                            Ext.Msg.hide();
                        },1500);
                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '00数据增加失败！ ',
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
                    msg: '11数据增加失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }

        });
    }

});
