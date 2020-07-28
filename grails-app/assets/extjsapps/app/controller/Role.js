Ext.define('appMis.controller.Role', {
    extend: 'Ext.app.Controller',
    init:function(){
        this.control({
            'rolegridviewport': {
//                itemdblclick: this.editRole//双击grid中的某个记录进行修改，暂时不用
            },
            'roleedit  button[action=save]': {//Window中使用,不能使用'>',
                click: this.updateRole
            },
            'roleadd  button[action=save]': {//Window中使用,不能使用'>',
                click: this.saveRole
            },
            'roleviewport > toolbar >  button[action=add]': {
                click: this.addRole
            },
            'roleviewport > toolbar >  button[action=addRow]': {
                click: this.addRowRole
            },
            'roleviewport > toolbar >  button[action=modify]': {
                click: this.modifyRole
            },
            'roleviewport > toolbar >  button[action=delete]': {
                click: this.deleteRole
            }
        })
    },

    modifyRole:function(){//[按钮]按下对grid中已选择的记录进行修改
        var record = (Ext.getCmp("rolegrid")).getSelectionModel().getSelection();
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
        var view = Ext.widget('roleedit');
        view.down('form').loadRecord(record[0]);//显示编辑视图
    },
    updateRole:function(button) {//编辑【编辑窗口】的记录后进行保存更新
        var win    = button.up('window');
        form   = win.down('form');
        record = form.getRecord();//选中编辑的记录
        values = form.getValues();
        record.set(values);//更新后激活Store的update:function(store,record)
        win.close();
    },
    addRole:function(){//打开新增加的输入窗口
        var record = Ext.create('appMis.model.RoleModel');//创建新的记录
        var view = Ext.widget('roleadd');
        view.down('form').loadRecord(record);
    },
    saveRole:function(button){//保存新增窗口 的新增加的基础数据
        var win    = button.up('window');
        form   = win.down('form');
        record =  form.getRecord();//创建的新记录
        values = form.getValues();
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        this.add(record)
        win.close();
    },


    deleteRole:function(){//按钮按下对grid中已选择的记录进行删除
        var record = (Ext.getCmp("rolegrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("rolegrid")).getStore();
        var currPage = panelStore.currentPage;
        if(record.length == 0){
            Ext.MessageBox.show({
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
            panelStore.loadPage(currPage);//刷新当前grid页面
            if(((panelStore.totalCount-record.length) % panelStore.pageSize)==0){
                currPage=currPage-1
                if(currPage==0)currPage=1
            }
            panelStore.loadPage(currPage);//刷新当前grid页面
            Ext.MessageBox.show({
                title:"提示",
                msg:"恭喜您，数据删除成功!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },1500);
        }
    },
    addRowRole:function(){//以增加行的方式增加一个部门
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 2,
            autoCancel: false
        });
        var record = Ext.create('appMis.model.RoleModel', { // Create a model instance
            authority:"ROLE_USER"+Math.floor(Math.random()*1000),
            chineseAuthority:'普通用户'+Math.floor(Math.random()*1000),
        });
        this.add(record)
    },

    add:function(record){//insert和add都是调用它
     Ext.Ajax.request({
            url:'/appMis/role/save',
            params:{
                authority:record.get("authority"),
                chineseAuthority:record.get("chineseAuthority")
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
                            var panelStore = (Ext.getCmp("rolegrid")).getStore();
                            panelStore.proxy.api.read="/appMis/role/listAsJsonPage2?newvalue="+'all';
                            Ext.getCmp("rolegrid").refreshData;//使上一条命令起作用。
                            panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
                            Ext.Msg.hide();
                        },1500);
                    }else{
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据增加失败！ ',
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
                    msg: '数据增加失败！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                },1500)
            }

        });
    },
    remove:function(store,record){
        Ext.Ajax.request({
            url:'/appMis/role/delete?id='+record.get("id"),
            method:'POST',
            success:
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.getCmp("bbarRole").getStore().reload()
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
    }
});
