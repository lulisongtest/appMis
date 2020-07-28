var uploader
var DynamicTableStoreStudent = Ext.create('Ext.data.Store', {
    fields: ['id','tableNameId','tableName','fieldNameId','fieldName','fieldType','fieldLength'],
    autoSync : true,//这样修改完一个单元格后会自动保存数据
    autoLoad:  true,//是否与上一句是一个功能！？
    proxy: {
        type: 'ajax',
        api: {
            read :'/appMis/dynamicTable/readDynamicTableSelect?tableNameId=student'
        },
        noCache: false,
        actionMethods: {
            read   : 'POST'
        },
        reader: {
            type: 'json',
            rootProperty: 'dynamictables',
            totalProperty:"totalCount"
        },
        writer:{
            type:'json'
        }
    }
});

Ext.define('FileRecord', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'fileid', type: 'string'},
        {name: 'filename', type: 'string'},
        {name: 'filesize', type: 'string'},
        {name: 'fileprogress', type: 'string'}
    ]
});

Ext.define('appMis.view.student.StudentViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.studentviewport',
    requires: [
        'appMis.view.student.SelectExportStudentItem',
        'appMis.view.student.SelectImportStudentItem'
    ],
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
            defaults: {
                margins: '0 0 0 0',
                bodyPadding: 0,
                border: 0,
                padding: '0 0 0 0'
            },
            items: [{
                text: '新增',
                hidden: (loginUserRole != '管理员'),
                //hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/edit_add.png',
                action: 'add'
            }, {
                text: '增加行',
                hidden: (loginUserRole != '管理员'),
                //hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/edit_remove.png',
                action: 'addRow'
            }, {
                text: '修改',
                hidden: (loginUserRole != '管理员'),
                // hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/pencil.png',
                action: 'modify'
            }, {
                text: '删除',
                hidden: (loginUserRole != '管理员'),
                //hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/cancel.png',
                action: 'delete'
            }, {
                text: '删除全部',
                hidden: (loginUserRole != '管理员'),
                // hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                icon: 'tupian/cancel.png',
                action: 'deleteAll'
            },
                {
                    text: '打印',
                    hidden: (loginUserRole != '管理员'),
                    //hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                    icon: 'printer/printer.png',
                    action: 'print'
                },
                {
                    text: '导入照片信息',
                    //hidden:(loginUserRole!= '单位管理员'),
                    hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                    icon: 'tupian/pencil.png',
                    handler: function (button, e) {
                        Ext.getCmp("empltabpanel").setHidden(true)
                        if (currentTreeNode.length <= 7) {
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '请选择要导入照片的单位 ',
                                buttons: Ext.MessageBox.OK, // buttons: Ext.Msg.OKCANCEL, // buttonText: {ok:'确认',cancel:'取消'},
                                fn: function (btn) {
                                    if (btn === 'ok') {
                                    } else {
                                    }
                                }
                            });
                            return
                        }
                        var formWin = Ext.create('Ext.window.Window', {
                            title: '批量导入照片',
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
                                                            /*handler: function (button, e) {
                                                                alert("删除")
                                                                var record = (Ext.getCmp("filesgrid")).getSelectionModel().getSelection();
                                                                var panelStore = (Ext.getCmp("filesgrid")).getStore();
                                                                //uploader.removeFile(record[i].get("filename"));//不行uploader.removeFile(文件名)
                                                                uploader.removeFile(uploader.getFile(record[0].get("fileid")));//可以uploader.removeFile(文件对象)
                                                                panelStore.remove(record[0])//删除选中的记录
                                                            }*/
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
                                        /*{
                                            xtype: 'container',
                                            text: "选择的文件列表",
                                            width: 745,
                                            height:100,
                                            scrollable: true,
                                            id:'filelist'
                                        },*/
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
                            url: encodeURI('/appMis/student/uploadFile?departmentDetail=' + departmentDetail),//上传文件路径,encodeURI是解决汉字乱码
                            //uploadpath: '/appMis/static/photoD',
                            max_file_size: '3gb',//100b, 10kb, 10mb, 1gb
                            chunk_size: '5mb',//分块大小，小于这个大小的不分块，本软件设计没有分块，所以chunk_size尽量设置大些！
                            unique_names: true,//生成唯一文件名
                            //browse_button : 'selectFiles',
                            browse_button: 'selectFiles',
                            container: document.getElementById('container'),
                            filters: [{
                                title: 'Image files',
                                extensions: 'jpg,gif,png'
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
                                        //document.getElementById('filelist').innerHTML += '<div id="' + file.id + '">' +  file.name+"&nbsp;&nbsp;" + ' (' + plupload.formatSize(file.size) + ') <b></b></div>';
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
                    }
                },
                {
                    text: '个人详细信息',
                    icon: 'tupian/pencil.png',
                    handler: function (button, e) {
                        Ext.getCmp("empltabpanel").setHidden(true)
                        var record1 = (Ext.getCmp("studentgrid")).getSelectionModel().getSelection();
                        if (record1.length != 1) {
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
                        studentRecord = record1[0];
                        sstudentCode = studentRecord.get("studentCode");
                        name = studentRecord.get("name");
                        var formWin = Ext.create('Ext.window.Window', {
                            id: 'studentDetailWin',
                            width: '100%',
                            height: '100%',
                            title: ('显示当前职工【' + studentRecord.get('studentCode') + ' ' + studentRecord.get('name') + ' 】的个人详细信息'),
                            autoShow: true,
                            modal: true,//创建模态窗口
                            layout: {
                                type: 'vbox',
                                pack: 'start',
                                align: 'stretch'
                            },
                            items: [{
                                xtype: 'panel',
                                id: 'studentDisplayDetail',
                                autoScroll: true,
                                bodyPadding: 0,
                                border: 0,
                                flex: 1,
                                items: [{
                                    xtype: 'component',
                                    id: "displayKyxmSbs",
                                    autoEl: {
                                        tag: 'iframe',
                                        style: 'height:100%;width:100%;border:none',
                                        src: '/appMis/student/displayStudentCurrentDetail?studentCode=' + studentRecord.get('studentCode') + '&name=' + studentRecord.get('name') + '&zbr=' + loginTruename
                                        //src: 'http://' + window.location.host + '/kzykyMis/static/photoD/xxx.pdf'
                                    }
                                }]
                            }],
                            buttons: [
                                /*{
                                    text: '打印',
                                    hidden:true,
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        var LODOP = getLodop();
                                        LODOP.PRINT_INIT();
                                        LODOP.ADD_PRINT_HTM('1%', '1%', '98%', '98%', document.getElementById("studentDisplayDetail").innerHTML);
                                        LODOP.SET_PRINT_MODE("FULL_WIDTH_FOR_OVERFLOW", true);
                                        LODOP.SET_PREVIEW_WINDOW(1, 0, 0, 0, 0, "");
                                        LODOP.PREVIEW();
                                    }
                                },*/
                                {
                                    text: '导出Excel文件',
                                    icon: 'images/printer/printer.png',
                                    handler: function (button, e) {
                                        Ext.MessageBox.buttonText.ok = "完成";
                                        Ext.Msg.show({
                                            title: '操作提示 ',
                                            msg: "<a href=\"http://" + window.location.host + "/appMis/static/appTmp/" + studentRecord.get('studentCode') + studentRecord.get('name') + "个人工资详细信息.xlsx\"><b>保存该EXCEL文件</b></a>",
                                            align: "centre",
                                            buttons: Ext.MessageBox.OK
                                        })
                                    }
                                },
                                {
                                    text: '关闭',
                                    glyph: 'xf00d@FontAwesome',
                                    handler: function (button, e) {
                                        Ext.Ajax.request({
                                            url: '/appMis/student/deleteExcelStudentDetail?studentCode=' + studentRecord.get('studentCode') + '&name=' + studentRecord.get('name'),
                                            ansync: false,
                                            method: 'POST',
                                            success://回调函数
                                                function (resp, opts) {
                                                }
                                        });
                                        Ext.getCmp("studentDetailWin").close()
                                    }
                                }
                            ],
                            listeners: {
                                'hide': function () {
                                    //win窗口关闭时，会执行本方法。 //删除职工详细信息生成Excel文件
                                    Ext.Ajax.request({
                                        url: '/appMis/student/deleteExcelStudentDetail?studentCode=' + studentRecord.get('studentCode') + '&name=' + studentRecord.get('name'),
                                        ansync: false,
                                        method: 'POST',
                                        success://回调函数
                                            function (resp, opts) {
                                            }
                                    })
                                }
                            }
                        })
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'truenameStudent',
                    labelStyle: 'padding-left:10px',
                    labelAlign: 'left',
                    width: 160,
                    labelWidth: 40,
                    fieldLabel: '姓名 ',
                    emptyText: '请输入查询姓名',
                    listeners: {
                        focus: function (combo) {
                            Ext.getCmp('usernameStudent').setValue("");
                        },
                        change: function (field, e) {
                            var panelStore = (Ext.getCmp('studentgrid')).getStore();
                            panelStore.proxy.api.read = '/appMis/student/read?dep_tree_id=' + currentTreeNode.substr(1)+'&username='+Ext.getCmp('usernameStudent').getValue()+'&truename='+Ext.getCmp('truenameStudent').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'usernameStudent',
                    labelStyle: 'padding-left:10px',
                    labelAlign: 'left',
                    width: 220,
                    labelWidth: 60,
                    fieldLabel: '职工编号 ',
                    emptyText: '请输入查询职工编号',
                    listeners: {
                        focus: function (combo) {
                            Ext.getCmp('truenameStudent').setValue("");
                        },
                        change: function (field, e) {
                            var panelStore = (Ext.getCmp('studentgrid')).getStore();
                            panelStore.proxy.api.read = '/appMis/student/read?dep_tree_id=' + currentTreeNode.substr(1)+'&username='+Ext.getCmp('usernameStudent').getValue()+'&truename='+Ext.getCmp('truenameStudent').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                }
            ]
        },
        {
            xtype: 'toolbar',
            height: 32,
            defaults: {
                margins: '0 0 0 0',
                bodyPadding: 0,
                border: 0,
                padding: '0 0 1 0'
            },
            items: [
                {
                    text: '数据导出',
                    // hidden:(loginUserRole!= '管理员'),
                    icon: 'tupian/exportToExcel.png',
                    //action: 'exportToExcel'
                    handler: function (button, e) {
                        view = Ext.create('appMis.view.student.SelectExportStudentItem')
                    }
                },
                {
                    text: '数据导入',
                    hidden: (loginUserRole != '管理员')&&(loginUserRole != '单位管理员'),
                    icon: 'tupian/importFromExcel.png',
                    handler: function (button, e) {
                        if (Ext.getCmp('studentexcelfilePath').getValue() == '') {
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
                        var s=(""+Ext.getCmp('studentexcelfilePath').getValue()).toLowerCase();
                        if(s.substring(s.length-4)!='xlsx'){
                            Ext.Msg.show({
                                title: '操作提示 ',
                                msg: '文件类型不对，请重新选择你要上传的xlsx文件！',
                                buttons: Ext.MessageBox.OK
                            });
                            setTimeout(function () {
                                Ext.Msg.hide();
                            }, 2500);
                            return;
                        }
                        view = Ext.create('appMis.view.student.SelectImportStudentItem')
                    }
                },
                {
                    xtype: 'form',
                    hidden: (loginUserRole != '管理员'),
                    id: 'selectImportStudentItemForm',
                    // hidden: (("" + loginUserRole).substring(("" + loginUserRole).length - 3)) != "管理员",
                    height: 40,
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
                        name: 'studentexcelfilePath',
                        id: 'studentexcelfilePath',
                        buttonText: '选择文件',
                        blankText: '文件名不能为空',
                        listeners: {
                            change: function (combo) {
                            }
                        }
                    }]
                }
            ]
        },
        {
            xtype: 'panel',
            flex: 1,
            layout: {
                type: 'vbox',
                pack: 'start',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'studentgridviewport',
                    id: 'studentgrid',
                    flex: 1,
                    listeners: {
                        beforerender: function () {
                            var panelStore = (Ext.getCmp('studentgrid')).getStore();
                            panelStore.pageSize = pageSize;
                            panelStore.proxy.api.read = '/appMis/student/read?dep_tree_id=' + currentTreeNode.substr(1)+'&username='+Ext.getCmp('usernameStudent').getValue()+'&truename='+Ext.getCmp('truenameStudent').getValue();
                            panelStore.loadPage(1);
                        }
                    }
                }]
        },
    ],
    initComponent: function () {
        this.callParent(arguments);
    }
});
