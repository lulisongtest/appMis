var currentRecord = 0;
var view = null;
var processDiagramfirst = 1;//第一次或重新打开form

//保存新建流程图文件到本地
var saveAs = saveAs || (function(view) {
    "use strict";
    // IE <10 is explicitly unsupported
    if (typeof view === "undefined" || typeof navigator !== "undefined" && /MSIE [1-9]\./.test(navigator.userAgent)) {
        return;
    }
    var
        doc = view.document
        // only get URL when necessary in case Blob.js hasn't overridden it yet
        , get_URL = function() {
            return view.URL || view.webkitURL || view;
        }
        , save_link = doc.createElementNS("http://www.w3.org/1999/xhtml", "a")
        , can_use_save_link = "download" in save_link
        , click = function(node) {
            var event = new MouseEvent("click");
            node.dispatchEvent(event);
        }
        , is_safari = /constructor/i.test(view.HTMLElement) || view.safari
        , is_chrome_ios =/CriOS\/[\d]+/.test(navigator.userAgent)
        , throw_outside = function(ex) {
            (view.setImmediate || view.setTimeout)(function() {
                throw ex;
            }, 0);
        }
        , force_saveable_type = "application/octet-stream"
        // the Blob API is fundamentally broken as there is no "downloadfinished" event to subscribe to
        , arbitrary_revoke_timeout = 1000 * 40 // in ms
        , revoke = function(file) {
            var revoker = function() {
                if (typeof file === "string") { // file is an object URL
                    get_URL().revokeObjectURL(file);
                } else { // file is a File
                    file.remove();
                }
            };
            setTimeout(revoker, arbitrary_revoke_timeout);
        }
        , dispatch = function(filesaver, event_types, event) {
            event_types = [].concat(event_types);
            var i = event_types.length;
            while (i--) {
                var listener = filesaver["on" + event_types[i]];
                if (typeof listener === "function") {
                    try {
                        listener.call(filesaver, event || filesaver);
                    } catch (ex) {
                        throw_outside(ex);
                    }
                }
            }
        }
        , auto_bom = function(blob) {
            // prepend BOM for UTF-8 XML and text/* types (including HTML)
            // note: your browser will automatically convert UTF-16 U+FEFF to EF BB BF
            if (/^\s*(?:text\/\S*|application\/xml|\S*\/\S*\+xml)\s*;.*charset\s*=\s*utf-8/i.test(blob.type)) {
                return new Blob([String.fromCharCode(0xFEFF), blob], {type: blob.type});
            }
            return blob;
        }
        , FileSaver = function(blob, name, no_auto_bom) {
            if (!no_auto_bom) {
                blob = auto_bom(blob);
            }
            // First try a.download, then web filesystem, then object URLs
            var
                filesaver = this
                , type = blob.type
                , force = type === force_saveable_type
                , object_url
                , dispatch_all = function() {
                    dispatch(filesaver, "writestart progress write writeend".split(" "));
                }
                // on any filesys errors revert to saving with object URLs
                , fs_error = function() {
                    if ((is_chrome_ios || (force && is_safari)) && view.FileReader) {
                        // Safari doesn't allow downloading of blob urls
                        var reader = new FileReader();
                        reader.onloadend = function() {
                            var url = is_chrome_ios ? reader.result : reader.result.replace(/^data:[^;]*;/, 'data:attachment/file;');
                            var popup = view.open(url, '_blank');
                            if(!popup) view.location.href = url;
                            url=undefined; // release reference before dispatching
                            filesaver.readyState = filesaver.DONE;
                            dispatch_all();
                        };
                        reader.readAsDataURL(blob);
                        filesaver.readyState = filesaver.INIT;
                        return;
                    }
                    // don't create more object URLs than needed
                    if (!object_url) {
                        object_url = get_URL().createObjectURL(blob);
                    }
                    if (force) {
                        view.location.href = object_url;
                    } else {
                        var opened = view.open(object_url, "_blank");
                        if (!opened) {
                            // Apple does not allow window.open, see https://developer.apple.com/library/safari/documentation/Tools/Conceptual/SafariExtensionGuide/WorkingwithWindowsandTabs/WorkingwithWindowsandTabs.html
                            view.location.href = object_url;
                        }
                    }
                    filesaver.readyState = filesaver.DONE;
                    dispatch_all();
                    revoke(object_url);
                }
            ;
            filesaver.readyState = filesaver.INIT;

            if (can_use_save_link) {
                object_url = get_URL().createObjectURL(blob);
                setTimeout(function() {
                    save_link.href = object_url;
                    save_link.download = name;
                    click(save_link);
                    dispatch_all();
                    revoke(object_url);
                    filesaver.readyState = filesaver.DONE;
                });
                return;
            }

            fs_error();
        }
        , FS_proto = FileSaver.prototype
        , saveAs = function(blob, name, no_auto_bom) {
            return new FileSaver(blob, name || blob.name || "download", no_auto_bom);
        }
    ;
    // IE 10+ (native saveAs)
    if (typeof navigator !== "undefined" && navigator.msSaveOrOpenBlob) {
        return function(blob, name, no_auto_bom) {
            name = name || blob.name || "download";

            if (!no_auto_bom) {
                blob = auto_bom(blob);
            }
            return navigator.msSaveOrOpenBlob(blob, name);
        };
    }

    FS_proto.abort = function(){};
    FS_proto.readyState = FS_proto.INIT = 0;
    FS_proto.WRITING = 1;
    FS_proto.DONE = 2;

    FS_proto.error =
        FS_proto.onwritestart =
            FS_proto.onprogress =
                FS_proto.onwrite =
                    FS_proto.onabort =
                        FS_proto.onerror =
                            FS_proto.onwriteend =
                                null;

    return saveAs;
}(
    typeof self !== "undefined" && self
    || typeof window !== "undefined" && window
    || this.content
));
// `self` is undefined in Firefox for Android content script context
// while `this` is nsIContentFrameMessageManager
// with an attribute `content` that corresponds to the window

if (typeof module !== "undefined" && module.exports) {
    module.exports.saveAs = saveAs;
} else if ((typeof define !== "undefined" && define !== null) && (define.amd !== null)) {
    define("FileSaver.js", function() {
        return saveAs;
    });
}






Ext.define('appMis.controller.ProcessDiagram', {
    extend: 'Ext.app.Controller',
    init: function () {
        this.control({
           /*'exportProcessDiagram   button[action=exportToFile]': {//选择导出信息项目
                click: this.exportExcelProcessDiagram
            },*/
            'importProcessDiagram   button[action=importFromFile]': {//选择导入信息项目
                click: this.importFromFile
            },

            'processDiagramedit  button[action=save]': {
                click: this.updateProcessDiagram
            },
            'processDiagramadd  button[action=save]': {
                click: this.saveProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=createProcessDiagram]': {
            click: this.createProcessDiagram
           },
           'processDiagramviewport > toolbar >  button[action=updateProcessUser]': {
                  click: this.updateProcessUser
           },
           'processDiagramviewport > toolbar >  button[action=add]': {
                click: this.addProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=addRow]': {
                click: this.addRowProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=modify]': {
                click: this.modifyProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=delete]': {
                click: this.deleteProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=deployProcess]': {
                click: this.deployProcess
            },
            'processDiagramviewport > toolbar >  button[action=delDeployProcess]': {
                click: this.delDeployProcess
            },
            'processDiagramviewport > toolbar >  button[action=startProcessInstance]': {
                click: this.startProcessInstance
            }
           /* 'processDiagramviewport > toolbar >  button[action=batchDelete]': {
                click: this.batchDeleteProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=generateTodayReport]': {
                click: this.generateTodayReportProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=exportReportToExcel]': {
                click: this.exportReportToExcelProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=exportToExcel]': {
                click: this.exportExcelProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=importFromExcel]': {
                click: this.importExcelProcessDiagram
            },
            'processDiagramviewport > toolbar >  button[action=deleteMonthProcessDiagram]': {
                click: this.deleteMonthProcessDiagram
            }*/
        })
    },
    // 更新流程相关用户
    updateProcessUser: function (button) {
        Ext.Ajax.request({
            url:'/appMis/processDiagram/updateProcessUser',
            method: 'POST',
            ansyc:false,
            success://回调函数
                function (resp, opts) {
                    {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "成功更新流程相关用户",
                            align: "centre",
                            buttons: Ext.MessageBox.OK
                        }),
                        setTimeout(function () {
                                Ext.Msg.hide();
                        }, 2500)
                    }
                },
            failure: function (resp, opts) {
               Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '更新流程相关用户失败！ ',
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 2500)
            }
        });
    },

   // 创建新流程图
    createProcessDiagram: function (button) {
            var src="/appMis/static/processDiagram/空流程图模板.bpmn";
            //alert("src=="+src)
            view = Ext.widget('exportProcessDiagram');   //view =Ext.create('appMis.view.user.SelectDepartment')//奇怪！！！随意修改以下两行或其中一行的数值，就可实现modal
            Ext.getCmp('diagramDisplay').setHtml("");
            Ext.getCmp('diagramDisplay').removeAll();

            var grxxxx = Ext.getCmp('diagramDisplay');
            var htmlx='<div id="canvas"  style="width:9050px; height:9050px;"></div>'
            htmlx=htmlx+'<button id="save-button" style="position: absolute;top:200px;right: 20px; border: solid 5px green;">另存流程图</button>'

            grxxxx.setHtml(htmlx);
            var xml=""
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                    if ((xhr.readyState == 4)&&(xhr.status == 200)) {
                            xml=xhr.responseText
                    }
            };
            //xhr.open('GET', 'processDiagram/人员单位变动流程.bpmn.xml', false );
            xhr.open('GET', src, false );//src为流程文件
            xhr.send(null);
          // alert("xml===="+xml) //加入这条则破坏了AJAX
           (function(BpmnModeler) {
                                var bpmnModeler = new BpmnModeler({
                                    container: '#canvas'
                                });
                                function importXML(xml) {
                                    bpmnModeler.importXML(xml, function(err) {
                                        if (err) {
                                            return console.error('could not import BPMN 2.0 diagram', err);
                                        }
                                        var canvas = bpmnModeler.get('canvas');
                                        canvas.zoom('fit-viewport');
                                    });
                                    var saveButton = document.querySelector('#save-button');
                                    saveButton.addEventListener('click', function() {
                                        bpmnModeler.saveXML({ format: true }, function(err, xml) {
                                            if (err) {
                                                console.error('另存流程图失败', err);
                                            } else {
                                                console.info('另存流程图');
                                                console.info(xml);
                                                /*alert("xml[0]==="+xml[0])
                                                alert("xml[1]==="+xml[1])
                                                alert("xml[2]==="+xml[2])
                                                alert("xml[3]==="+xml[3])
                                                alert("xml[4]==="+xml[4])
                                                alert("xml[5]==="+xml[5])
                                                alert("xml[6]==="+xml[6])
                                                alert("xml[7]==="+xml[7])
                                                alert("xml[8]==="+xml[8])*/
                                                // if(isEmpty(xml)) {
                                                //     xml = '';
                                                //  }
                                               //alert("xml=="+xml)
                                                var file = new File([xml], "新建流程图.bpmn", { type: "text/plain;charset=utf-8" });
                                                saveAs(file);//新建流程图.bpmn保存到本地
                                               // saveXml(xml) //内置函数，把流程图写入服务器
                                            }
                                            //alert('保存流程图 (see console (F12))');
                                        });
                                    });
                                }
                                importXML(xml);
                            })(window.BpmnJS);
                           /* //内置函数，把流程图写入服务器D:\myPro\appMis\src\main\webapp\processDiagram
                            function saveXml(xml) {
                                Ext.Ajax.request({
                                    url: '/appMis/processDiagram/saveDiagram?filename=新建流程图.bpmn',
                                    async:false,//同步请求数据, true异步
                                    method : 'post',
                                    params: {
                                        xml:xml
                                    },
                                    success://回调函数
                                        function (resp, opts) {
                                            //alert("save Diagram ok!!!")
                                        }
                                })
                            }*/
    },

    //启动流程
    startProcessInstance: function (button) {
        //alert("start流程")
        var idArr = new Array();
        idArr.push('0');
        var record = (Ext.getCmp("processDiagramgrid")).getSelectionModel().getSelection();
        if (record.length == 0) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请选择您要启动的流程!（注：可以一次启动多个流程）",
                buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        for (var i = 0; i < record.length; i++) {
            idArr.push(record[i].get("id"));
        }
        Ext.Ajax.request({
            url:'/appMis/processDiagram/start',
            async:false,
            params: {
                id: idArr
            },
            success: function (resp, opts) {
                if (resp.responseText == 'success'){
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg:  "成功启动流程! ",
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        var panelStore =(Ext.getCmp('processDiagramgrid')).getStore();
                        panelStore.sort('scDate', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                        Ext.Msg.hide();
                    }, 2500);
                }else{
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg:  "启动流程失败! ",
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        Ext.Msg.hide();
                    }, 2500)
                }
            },
            failure: function (fp, o) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg:  "启动流程失败! ",
                    buttons: Ext.MessageBox.OK
                }),
                    setTimeout(function () {
                        Ext.Msg.hide();
                    }, 2500)
            }
        });
    },


    // 部署流程
    deployProcess: function (button) {
        //alert("部署流程")
        var count = 0
        var idArr = new Array();
        idArr.push('0');
        var record = (Ext.getCmp("processDiagramgrid")).getSelectionModel().getSelection();
        if (record.length == 0) {
            Ext.MessageBox.show({
                title: "提示",
                msg: "请选择您要部署的流程!（注：可以一次部署多个流程）",
                buttons: Ext.MessageBox.OK
            });
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        for (var i = 0; i < record.length; i++) {
            idArr.push(record[i].get("id"));
        }
        Ext.Ajax.request({
            url:'/appMis/processDiagram/deploy',
            async:false,
            params: {
                id: idArr
             },
            success: function (resp, opts) {
                if (resp.responseText == 'success'){
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg:  "成功部署流程! ",
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        var panelStore =(Ext.getCmp('processDiagramgrid')).getStore();
                        panelStore.sort('scDate', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                        Ext.Msg.hide();
                    }, 2500);
                }else{
                    Ext.Msg.show({
                        title: '操作提示 ',
                        msg:  "部署流程失败! ",
                        buttons: Ext.MessageBox.OK
                    })
                    setTimeout(function () {
                        Ext.Msg.hide();
                    }, 2500)
                }
            },
            failure: function (fp, o) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg:  "部署流程失败! ",
                    buttons: Ext.MessageBox.OK
                }),
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 2500)
            }
        });
    },


// 删除部署流程
    delDeployProcess: function (button) {
    //alert("部署流程")
    var count = 0
    var idArr = new Array();
    idArr.push('0');
    var record = (Ext.getCmp("processDiagramgrid")).getSelectionModel().getSelection();
    if (record.length == 0) {
        Ext.MessageBox.show({
            title: "提示",
            msg: "请选择您要删除部署的流程!（注：可以一次删除部署多个流程）",
            buttons: Ext.MessageBox.OK
        });
        setTimeout(function () {
            Ext.Msg.hide();
        }, 2500);
        return;
    }
    for (var i = 0; i < record.length; i++) {
        idArr.push(record[i].get("id"));
    }
    Ext.Ajax.request({
        url:'/appMis/processDiagram/delDeploy',
        async:false,
        params: {
            id: idArr
        },
        success: function (resp, opts) {
            if (resp.responseText == 'success'){
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg:  "成功删除部署流程! ",
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    var panelStore =(Ext.getCmp('processDiagramgrid')).getStore();
                    panelStore.sort('scDate', 'ASC');//会向后台发送一次AJAX// panelStore.autoLoad=true;//会向后台发送一次AJAX
                    Ext.Msg.hide();
                }, 2500);
            }else{
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg:  "删除部署流程失败! ",
                    buttons: Ext.MessageBox.OK
                })
                setTimeout(function () {
                    Ext.Msg.hide();
                }, 2500)
            }

        },
        failure: function (fp, o) {
            Ext.Msg.show({
                title: '操作提示 ',
                msg:  "删除部署流程失败! ",
                buttons: Ext.MessageBox.OK
            })
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500)
        }
    });
},

//从Excel表导入到数据库
    importFromFile: function (button) {
        //var fieldItem = Ext.getCmp('itemselectorImportProcessDiagram').getValue()
        win = Ext.getCmp('importProcessDiagram')
        //var panel1 = button.up.up('panel');
        form = Ext.getCmp('importForm');
        if (form.form.isValid()) {
            if (Ext.getCmp('processDiagramfilePath').getValue() == '') {
                Ext.Msg.alert('系统提示', '请选择你要上传的文件');
                return;
            }
            //alert("Ext.getCmp('processDiagramfilePath').getValue()====="+Ext.getCmp('processDiagramfilePath').getValue())
            form.getForm().submit({
                url:'/appMis/processDiagram/importProcessDiagram?recId='+rec.get('id'),
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
                        var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
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
    exportExcelProcessDiagram: function (record) {
        // var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"正在把当前选择的数据导出到Excel表中，请等待..."});
        //myMask.show();
        var myMask = new Ext.LoadMask(Ext.getCmp('itemselectorExportProcessDiagram'), {msg: "正在把当前选择的数据导出到Excel表中，请等待..."});
        myMask.show();
        var fieldItem = Ext.getCmp('itemselectorExportProcessDiagram').getValue()
        if (Ext.getCmp('currentProcessDiagramDate').getValue() == "") {
            Ext.MessageBox.show({title: "提示", msg: "请先选择您要导出单位当前月工资的日期!", buttons: Ext.MessageBox.OK})
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        Ext.Ajax.request({
            url:'/appMis/processDiagram/selectExportProcessDiagram?currentProcessDiagramDate=' + new Date(Ext.getCmp('currentProcessDiagramDate').getValue()).pattern("yyyy-MM-dd"),
            method: 'POST',
            params: {fieldItem: fieldItem},
            success://回调函数
                function (resp, opts) {
                    {
                        myMask.destroy()
                        Ext.MessageBox.buttonText.ok = "完成"
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: "<a href=\"http://" + window.location.host + "/kzydzyhpMis/tmp/" + resp.responseText + ".xls\"><b>保存该EXCEL文件</b></a>",
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
    editProcessDiagram: function (grid, record) {
        var view = Ext.widget('processDiagramedit');
        view.down('form').loadRecord(record);
    },

//按钮按下对grid中已选择的记录进行修改
    modifyProcessDiagram: function () {
        currentRecord = 0
        processDiagramfirst = 1//第一次或重新打开form
        records = (Ext.getCmp('processDiagramgrid')).getSelectionModel().getSelection();//不能用 var records 可能是局部变量，其它方法取不到值。
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
        view = Ext.widget('processDiagramedit');//编辑窗口中的记录与Grid中选中的记录是同一条记录
        // view.down('form').loadRecord(record[0]);
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图
        // view.down('form').loadRecord(records[1]);//显示编辑视图
    },

    firstProcessDiagram: function (button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord = 0
        view.down('form').loadRecord(records[0]);//显示编辑视图
    },
    previewProcessDiagram: function (button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord = currentRecord - 1
        if (currentRecord == -1)currentRecord = 0
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图
    },
    nextProcessDiagram: function (button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord = currentRecord + 1
        if (currentRecord == records.length)currentRecord = records.length - 1
        view.down('form').loadRecord(records[currentRecord]);//显示编辑视图

    },
    lastProcessDiagram: function (button) {//编辑【编辑窗口】的记录后进行保存更新
        currentRecord = records.length - 1
        view.down('form').loadRecord(records[records.length - 1]);//显示编辑视图
    },

//编辑记录后进行保存更新
    updateProcessDiagram: function (button) {
        var win = button.up('window');
        form = win.down('form');
        record = form.getRecord();
        values = form.getValues();
        values.scDate = (values.scDate).replace(/(年)|(月)/g, "-");
        values.scDate = (values.scDate).replace(/(日)/g, "");
        if (processDiagramfirst = 1) {
            record.set(values);//因为此时与数据表记录关联,第一次保存可以激活Store的update:function(store,record)，第二次保存就不激活Store的update:function(store,record)，
            processDiagramfirst = 0
        } else {
            Ext.Ajax.request({
                url:'/appMis/processDiagram/update',
                params: {
                    id: record.get("id")
                    , title: record.get('title')
                    , titleCode: record.get('titleCode')
                    , jb: record.get('jb')
                    , dep: record.get('dep')
                    , scr: record.get('scr')
                    , scDate: (new Date(record.get('scDate'))).pattern("yyyy-MM-dd") + " 00:00:00.0"
                    , wjlx: record.get('wjlx')
                    , lczt: record.get('lczt')
                },
                success: function (resp, opts) {
                    if (resp.responseText == 'success') {
                        var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
                        panelStore.loadPage(panelStore.currentPage);
                    } else {
                        Ext.Msg.show({
                            title: '操作提示 ',
                            msg: '数据更新失败！ ',
                            buttons: Ext.MessageBox.OK
                        })
                        setTimeout(function () {
                            var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
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
    addProcessDiagram: function () {
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
        var record = Ext.create('appMis.model.ProcessDiagramModel', {
            title: '新文件 ',
            titleCode: ' ',
            jb: '市局级',
            dep: ' ',
            scr: ' ',
            scDate: new Date(),
            wjlx: '',
            lczt:'未部署'

        });
        //设置初值
        var view = Ext.widget('processDiagramadd');
        view.down('form').loadRecord(record);
    },


//保存新增加的部门
    saveProcessDiagram: function (button) {
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
    addRowProcessDiagram: function () {
        var rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 2,
            autoCancel: false
        });
        record = Ext.create('appMis.model.ProcessDiagramModel', {
            title: '新文件 ',
            titleCode: ' ',
            jb: '市局级',
            dep: ' ',
            scr: ' ',
            scDate: (new Date()).pattern("yyyy-MM-dd"),
            wjlx: ' ',
            lczt:'未部署'
        });
        this.add(record)//在最后增加一条记录
    },

//insert和add都是调用它
    add: function (record, win) {//insert和add都是调用它
        var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
        Ext.Ajax.request({
            url:'/appMis/processDiagram/save',
            params: {
                title: record.get('title'),
                titleCode: record.get('titleCode'),
                jb: record.get('jb'),
                dep: record.get('dep'),
                scr: record.get('scr'),
                scDate: (new Date(record.get('scDate'))).pattern("yyyy-MM-dd") + " 00:00:00.0",
                wjlx: record.get('wjlx'),
                lczt: record.get('lczt')

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
    batchDeleteProcessDiagram: function () {
        if ((Ext.getCmp('processDiagramquery1').getValue() == "") || (Ext.getCmp('processDiagramquery1').getValue() == null) || (Ext.getCmp('processDiagramquery1').getValue() == '全部')) {
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
            newvalue1 = (new Date(Ext.getCmp('processDiagramquery1').getValue()).pattern("yyyy-MM-dd"));
            var myMask = new Ext.LoadMask(Ext.getCmp('processDiagramgrid'), {msg: "正在数据批量删除，请等待..."});
            myMask.show();
            Ext.Ajax.timeout = 9000000;  //9000秒
            Ext.Ajax.request({
                url:'/appMis/processDiagram/batchDelete?newvalue1=' + newvalue1,
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
                            Ext.getCmp('bbarprocessDiagram').getStore().reload()
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
    deleteProcessDiagram: function () {
        var record = (Ext.getCmp('processDiagramgrid')).getSelectionModel().getSelection();
        var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
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
            url:'/appMis/processDiagram/delete?id=' + record.get("id"),
            method: 'POST',
            success: function (resp, opts) {//成功后的回调方法
                if (resp.responseText == 'success') {
                    Ext.getCmp("bbarProcessDiagram").getStore().reload()
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
    deleteMonthProcessDiagram: function (button) {//删除单位当前月数据
        if (Ext.getCmp('currentProcessDiagramDate').getValue() == "") {
            Ext.MessageBox.show({title: "提示", msg: "请先选择您要删除单位当前月数据的日期!", buttons: Ext.MessageBox.OK})
            setTimeout(function () {
                Ext.Msg.hide();
            }, 2500);
            return;
        }
        var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
        var myMask = new Ext.LoadMask(Ext.getCmp('processDiagramgrid'), {msg: "正在删除单位当前月数据，请等待..."});
        myMask.show();
        Ext.Ajax.timeout = 9000000;  //9000秒
        Ext.Ajax.request({
            url:'/appMis/processDiagram/deleteMonthProcessDiagram?currentProcessDiagramDate=' + new Date(Ext.getCmp('currentProcessDiagramDate').getValue()).pattern("yyyy-MM-dd"),
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
