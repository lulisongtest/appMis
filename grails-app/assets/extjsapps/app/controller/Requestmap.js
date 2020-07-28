Ext.define('appMis.controller.Requestmap', {
    extend: 'Ext.app.Controller',
    init:function(){
        this.control({
            'requestmapgridviewport': {
//                itemdblclick: this.editRequestmap
            },
            'requestmapedit button[action=save]': {
                click: this.updateRequestmap
            },
            'requestmapadd  button[action=save]': {//Window中使用,不能使用'>',
                click: this.saveRequestmap
            },
            'requestmapviewport > toolbar >   button[action=add]': {
                click: this.addRequestmap
            },
            'requestmapviewport > toolbar >   button[action=addRow]': {
                click: this.addRowRequestmap
            },
            'requestmapviewport > toolbar >   button[action=modify]': {
                click: this.modifyRequestmap
            },
            'requestmapviewport > toolbar >   button[action=delete]': {
                click: this.deleteRequestmap
            }


        })
    },
    editRequestmap:function(rid, record){
        alert("暂时还不提供此功能")
       /* var view = Ext.widget('requestmapedit');
        view.down('form').loadRecord(record);*/
    },

    modifyRequestmap:function(){//按钮按下对grid中已选择的记录进行修改
        var record = (Ext.getCmp("requestmapgrid")).getSelectionModel().getSelection();
        if(record.length != 1){
            Ext.MessageBox.show({
                title:"提示",
                msg:"请选择您要修改的数据!" ,
                buttons:Ext.Msg.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },2500);
            return;
        }
        var view = Ext.widget('requestmapedit');
        view.down('form').loadRecord(record[0]);
    },
    addRequestmap:function(){
        record = Ext.create('appMis.model.RequestmapModel');
        var view = Ext.widget('requestmapadd');
        view.down('form').loadRecord(record);
    },

    addRowRequestmap:function(){
        record = Ext.create('appMis.model.RequestmapModel', {
            url:"/login/auth",
            configAttribute:"permitAll",
            chineseUrl:"登录",
            roleList:"普通用户",
            treeId:"0",
            glyph:0xf0c9

        });
        this.add(record)//在最后增加一条记录
    },
    saveRequestmap:function(button){
        var win    = button.up('window');
        form   = win.down('form');
        record =  form.getRecord();//创建的新记录
        values = form.getValues();
        record.set(values);//因为是新增记录,不能激活Store的update:function(store,record)
        this.add(record)
        win.close();
    },
    deleteRequestmap:function(){//按钮按下对grid中已选择的记录进行删除
        var record = (Ext.getCmp("requestmapgrid")).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp("requestmapgrid")).getStore();
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
                msg:"成功删除数据!",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            },1500);
        }
    },

    updateRequestmap:function(button) {
        var win  = button.up('window'),
            form   = win.down('form'),
            record = form.getRecord(),
            values = form.getValues();
            record.set(values);
            win.close();
    },
    add:function(record){//insert和add都是调用它
            var panelStore = (Ext.getCmp("requestmapgrid")).getStore();
            panelStore.loadPage(parseInt(panelStore.totalCount/panelStore.pageSize+1));//定位grid到最后一页
            Ext.Ajax.request({
                url:'/appMis/requestmap/save',
                params:{
                    url:record.get("url"),
                    configAttribute:record.get("configAttribute"),
                    chineseUrl:record.get("chineseUrl"),
                    roleList:record.get("roleList"),
                    treeId:record.get("treeId"),
                    glyph:record.get("glyph")
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
            url:'/appMis/requestmap/delete?id='+record.get("id"),
            method:'POST',
            success:
                function(resp,opts) {//成功后的回调方法
                    if(resp.responseText=='success'){
                        Ext.getCmp("bbarRequestmap").getStore().reload()
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
