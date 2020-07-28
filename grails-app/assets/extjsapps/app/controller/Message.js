


Ext.define('appMis.controller.Message', {
    extend: 'Ext.app.Controller',
    init: function () {
        this.control({
            'messagemaincenter >panel > toolbar >  button[action=send]': {
                click: this.sendMessage//发送消息
            },
            'messagemaincenter >panel > toolbar >  button[action=displayMore]': {
                click: this.displayMore//发送消息
            }, 'messagemaincenter >panel > toolbar >  button[action=uploadFile]': {
                click: this.uploadFile//发送消息
            }
        })
    },
    //上传文件
    uploadFile: function (button){
        if(!Ext.getCmp("messagecontentpanel").getActiveTab())return
        //receiveName=Ext.getCmp("messagecontentpanel").getActiveTab().getTitle()
       // messageGroup=Ext.getCmp("messagecontentpanel").getActiveTab().getId()
        messageGroup=Ext.getCmp("messagecontentpanel").getActiveTab().getTitle()
        //alert("receiveName=="+receiveName)
       // alert("messageGroup=="+messageGroup)
       var content=Ext.getCmp("messageContent").getValue()
        var formWin = Ext.create('Ext.window.Window', {
            title: '上传文件',
            layout: {
                type: 'vbox',
                pack: 'start',
                align: 'stretch'
            },
            width: 950,
            height: 600,
            closable: true,
            modal: true,
            autoShow: true,
            id: 'backupsFormWin',
            items: [
                {
                    xtype: 'panel',
                    id: 'container',
                    flex: 1,
                    layout: {
                        type: 'vbox',
                        pack: 'start',
                        align: 'stretch'
                    },
                    defaults: {
                        autoScroll: true,
                        bodyPadding: 0,
                        border: 0,
                        padding: 0
                    },
                    width: '100%',
                    items: [
                        {
                            xtype: 'toolbar',
                            height: 32,
                            items: [
                                {
                                    text: '选择上传文件',
                                    id: 'selectFiles',
                                    icon: 'tupian/edit_add.png',
                                    handler: function (button, e) {
                                        // uploader.start();
                                    }
                                },
                                {
                                    text: '开始上传文件',
                                    id: 'uploadfiles',
                                    icon: 'tupian/edit_remove.png',
                                    handler: function (button, e) {
                                    }
                                },
                                {
                                    text: '删除上传文件',
                                    id: 'deleteuploadfiles',
                                    icon: 'tupian/cancel.png',
                                    handler: function (button, e) {
                                        var record = (Ext.getCmp("filesgrid")).getSelectionModel().getSelection();
                                        var panelStore = (Ext.getCmp("filesgrid")).getStore();
                                        var currPage = panelStore.currentPage;
                                        if (record.length == 0) {
                                            Ext.MessageBox.show({
                                                title: "提示",
                                                msg: "请先选择您要删除的上传文件!",
                                                buttons: Ext.MessageBox.OK
                                                //icon: Ext.MessageBox.INFO
                                            })
                                            setTimeout(function () {
                                                Ext.Msg.hide();
                                            }, 2500);
                                            return;
                                        } else {
                                            for (var i = 0; i < record.length; i++) {
                                                //uploader.removeFile(record[i].get("filename"));//不行uploader.removeFile(文件名)
                                                uploader.removeFile(uploader.getFile(record[i].get("fileid")));//可以uploader.removeFile(文件对象)
                                                panelStore.remove(record[i])//删除选中的记录
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

                                            }, 2500);
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            flex: 8,
                            layout: {
                                type: 'vbox',
                                pack: 'start',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    xtype: 'gridpanel',
                                    id: 'filesgrid',
                                    flex: 1,
                                    store: {
                                        model: FileRecord,
                                        autoLoad: true
                                    },
                                    selModel: Ext.create('Ext.selection.CheckboxModel'),//有复选框
                                    columns: [
                                        Ext.create('Ext.grid.RowNumberer', {
                                            text: '序号',
                                            width: 70
                                        }),
                                        {
                                            header: '文件id',
                                            dataIndex: 'fileid',
                                            hidden: true
                                        },
                                        {
                                            header: '上传文件名',
                                            titleAlign: 'center',
                                            dataIndex: 'filename',
                                            width: 400
                                        },
                                        {
                                            header: '上传大小',
                                            titleAlign: 'center',
                                            dataIndex: 'filesize',
                                            width: 130
                                        },
                                        {
                                            header: '上传文件进度',
                                            titleAlign: 'center',
                                            dataIndex: 'fileprogress',
                                            width: 130
                                        },
                                        {
                                            text: "删除上传文件",
                                            width: 130,
                                            titleAlign: 'center',
                                            renderer: function (value, meta, record) {
                                                var returnStr = "<INPUT type='button' value='删除'>";
                                                return returnStr;
                                            }

                                        }
                                    ],
                                    listeners: {
                                        cellclick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
                                            if (cellIndex == 6) {//删除上传文件
                                                //var panelStore = (Ext.getCmp("filesgrid")).getStore();
                                                var panelStore = grid.getStore();
                                                uploader.removeFile(uploader.getFile(record.get("fileid")));//可以uploader.removeFile(文件对象)
                                                panelStore.remove(record)//删除选中的记录
                                            }

                                        }
                                    }
                                }]
                        },

                        {
                            xtype: 'container',
                            text: "进度",
                            flex: 1,
                            //width: 745,
                            // height:30,
                            scrollable: true,
                            id: 'progress'
                        },
                        {
                            xtype: 'container',
                            text: "结果",
                            flex: 1,
                            // width: 745,
                            // height:30,
                            scrollable: true,
                            id: 'result'
                        }
                    ]
                }
            ]
        });
        uploader = new plupload.Uploader({
            runtimes: 'html5,silverlight,flash,html4',//设置运行环境，会按设置的顺序，可以选择的值有html5,gears,flash,silverlight,browserplus,html
            flash_swf_url: '/appMis/assets/js/Moxie.swf',
            silverlight_xap_url: '/appMis/assets/js/Moxie.xap',
            // flash_swf_url : '/appMis/static/js/Moxie.swf',
            // silverlight_xap_url : '/appMis/static/js/Moxie.xap',
            // flash_swf_url : 'D:/myPro/appMis/src/main/webapp/js/Moxie.swf',
            // silverlight_xap_url : 'D:/myPro/appMis/src/main/webapp/js/Moxie.xap',
             //url: encodeURI('/appMis/message/uploadFile?departmentDetail=' + departmentDetail),//上传文件路径,encodeURI是解决汉字乱码
            url: encodeURI('/appMis/message/uploadFile?sendId='+loginUsername+'&sendName='+loginTruename+'&receiveId=&receiveName=&content='+content+'&messageGroup='+messageGroup),//上传文件路径,encodeURI是解决汉字乱码
            //url: encodeURI('/appMis/message/uploadFile?sendId='+loginUsername+'&sendName='+loginTruename+'&receiveId='+messageGroup+'&content='+content+'&messageGroup='+messageGroup),//上传文件路径,encodeURI是解决汉字乱码
            //uploadpath: '/appMis/static/photoD',
            max_file_size: '3gb',//100b, 10kb, 10mb, 1gb
            chunk_size: '5mb',//分块大小，小于这个大小的不分块，本软件设计没有分块，所以chunk_size尽量设置大些！
            unique_names: true,//生成唯一文件名
            //browse_button : 'selectFiles',
            browse_button: 'selectFiles',
            container: document.getElementById('container'),
            filters: [{
                title: 'Image files',
                extensions: 'txt,doc,docx,xls,xlsx,ppt,pptx,jpg,gif,png'//允许上传文件类型
            }, {
                title: 'Zip files',
                extensions: 'zip,7z,rar'
            }],
            multipart: true,//为true时将以multipart/form-data的形式来上传文件，
            init: {
                PostInit: function () {
                    //document.getElementById('filelist').innerHTML = '';
                    document.getElementById('uploadfiles').onclick = function () {
                        if (uploader.files.length > 0) {
                            if (uploader.files.length > 45) {
                                Ext.Msg.show({
                                    title: '操作提示 ',
                                    msg: '一次选择上传文件数目太多，超过【45】个文件不能上传！！！ ',
                                    buttons: Ext.MessageBox.OK
                                });
                            } else {
                                uploader.start();//alert("uploader.files.length===="+uploader.files.length)//已经选择上传文件的个数
                            }
                        } else {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '没有选择上传文件！！！ ',
                                buttons: Ext.MessageBox.OK
                            });
                        }
                        return false;
                    };
                },
                FilesAdded: function (up, files) {
                    var filegridStore = Ext.getCmp('filesgrid').getStore();
                    plupload.each(files, function (file) {
                        var rec = Ext.create('FileRecord', {
                            fileid: file.id,
                            filename: file.name,
                            filesize: '<div>(' + plupload.formatSize(file.size) + ') <b></b></div>',
                            fileprogress: '<div id="' + file.id + '"> <b></b></div>'
                        });
                        filegridStore.add(rec) //alert("rec.get('filename')===="+rec.get('filename'))// alert("rec.get('filesize')===="+rec.get('filesize')) // filegridStore.reload()// filegridStore.loadPage(1)//Ext.getCmp('filegrid').getView().refresh()
                    });
                    // uploader.start();
                    return false;
                },
                FileUploaded: function (up, file, info) {//文件上传完毕触发
                    console.log("单独文件上传完毕");
                },
                UploadComplete: function (uploader, files) {
                    console.log("所有文件上传完毕");
                    // alert("所有文件上传完毕")
                    Ext.getCmp('progress').setHtml("上传进度为：所有文件上传完毕");
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg: '所有文件上传完毕 ',
                        buttons: Ext.MessageBox.OK,
                        // buttons: Ext.Msg.OKCANCEL,
                        // buttonText: {ok:'确认',cancel:'取消'},
                        fn: function (btn) {
                            if (btn === 'ok') {
                               // updateMessage()
                                Ext.getCmp("messageContent").setValue("")
                                Ext.getCmp('backupsFormWin').close()
                            } else {
                                // console.log('取消删除');
                            }
                        }
                    });
                    // setTimeout(function () {
                    //     Ext.Msg.hide();
                    //     Ext.getCmp('backupsFormWin').close()
                    //  }, 6500)
                    document.getElementById(file.id).getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";
                },
                UploadProgress: function (uploader, file) {
                    //     $("#progress").html("上传进度为：" + file.percent + "%");
                    document.getElementById(file.id).getElementsByTagName('b')[0].innerHTML = '<span>' + file.percent + "%</span>";
                    Ext.getCmp('progress').setHtml("上传进度为：" + file.name + "------------" + file.percent + "%")
                    console.log(file.percent);
                }
            }
        });
        uploader.init();
    },


    //发送消息
    sendMessage: function (button) {
        //alert("发送消息")
      if(!Ext.getCmp("messagecontentpanel").getActiveTab())return
     // receiveName=Ext.getCmp("messagecontentpanel").getActiveTab().getTitle()
     // messageGroup=Ext.getCmp("messagecontentpanel").getActiveTab().getId()
      messageGroup=Ext.getCmp("messagecontentpanel").getActiveTab().getTitle()//获取群名称
      Ext.Ajax.request({
            url: '/appMis/message/save',
            async: false,//同步请求数据, true异步
            params: {
                 sendId:loginUsername,
                 sendName:loginTruename,
                 receiveId:"",
                 receiveName:"",
                 content:Ext.getCmp("messageContent").getValue(),
                 //messageDate:(new Date().pattern("yyyy-MM-dd HH:mm:ss")),
                 fileName:"",
                 fileContent:"",
                 messageGroup:messageGroup,
                 receiveStatus:"",
            },
            method: 'POST',
            success://回调函数
                function (resp, opts) {
                    if(resp.responseText=="success"){
                       Ext.getCmp("messageContent").setValue("")
                       updateMessage()
                    }
                },
            failure: function (resp, opts) {
            }
      });
    },

    //查看更多信息
    displayMore: function (button){
        dayCount=dayCount+1
        updateMessage()
    },
});
