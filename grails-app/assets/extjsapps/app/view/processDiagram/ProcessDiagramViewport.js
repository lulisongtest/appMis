
/*
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
*/

Ext.define('appMis.view.processDiagram.ProcessDiagramViewport', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.processDiagramviewport',
    requires: [
        'appMis.view.processDiagram.ProcessDiagramGridViewport',
        'appMis.view.processDiagram.ProcessDiagramEdit' ,
        'appMis.view.processDiagram.ProcessDiagramAdd',
        'appMis.view.processDiagram.ExportProcessDiagram',
        'appMis.view.processDiagram.ImportProcessDiagram'
    ],
    layout:{
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
            height: 40,
            defaults: {
                margins: '0 0 0 0',
                bodyPadding: 0,
                border: 0,
                padding: '4 0 4 0'
            },
            items: [
                {
                    text: '创建流程图',
                    //hidden:((loginUserRole!="单位管理员")&&(loginUserRole!="管理员")),
                    hidden:(loginUserRole!="管理员"),//只有管理员才能对流程进行操作
                    icon: 'tupian/edit_add.png',
                    action: 'createProcessDiagram'
                },{
                    text: '更新流程相关用户',
                    //hidden:((loginUserRole!="单位管理员")&&(loginUserRole!="管理员")),
                    hidden:(loginUserRole!="管理员"),//只有管理员才能对流程进行操作
                    icon: 'tupian/edit_add.png',
                    action: 'updateProcessUser'
                },

                {
                    text: '增加行',
                    //hidden:((loginUserRole!="单位管理员")&&(loginUserRole!="管理员")),
                    hidden:(loginUserRole!="管理员"),//只有管理员才能对流程进行操作
                    icon: 'tupian/edit_remove.png',
                    action: 'addRow'
                }, {
                    text: '修改',
                    //hidden:((loginUserRole!="单位管理员")&&(loginUserRole!="管理员")),
                    hidden:(loginUserRole!="管理员"),//只有管理员才能对流程进行操作
                    icon: 'tupian/pencil.png',
                    action: 'modify'
                }, {
                    text: '删除',
                    //hidden:((loginUserRole!="单位管理员")&&(loginUserRole!="管理员")),
                    hidden:(loginUserRole!="管理员"),//只有管理员才能对流程进行操作
                    icon: 'tupian/cancel.png',
                    action: 'delete'
                },{
                    text: '部署流程',
                    //hidden:((loginUserRole!="单位管理员")&&(loginUserRole!="管理员")),
                    hidden:(loginUserRole!="管理员"),//只有管理员才能对流程进行操作
                    icon: 'tupian/edit_add.png',
                    action: 'deployProcess'
                },{
                    text: '删除部署',
                    //hidden:((loginUserRole!="单位管理员")&&(loginUserRole!="管理员")),
                    hidden:(loginUserRole!="管理员"),//只有管理员才能对流程进行操作
                    icon: 'tupian/cancel.png',
                    action: 'delDeployProcess'
                },{
                    xtype: 'textfield',
                    id: 'keywordprocessDiagram',
                    fieldLabel: '关键字',
                    labelAlign: 'right',
                    width: 163,
                    labelWidth: 45
                }, {
                    xtype: 'button',
                    width: 70,
                    action: 'query',
                    icon: 'tupian/search.png',
                    handler: function (button, e) {
                        var newvalue = Ext.getCmp('keywordprocessDiagram').getValue();
                        var panelStore = (Ext.getCmp('processDiagramgrid')).getStore();
                        panelStore.proxy.api.read = '/appMis/processDiagram/search?q=' + newvalue;
                        panelStore.loadPage(1);
                    },
                    text: '搜索'
                }
            ]
        },
        {
            xtype: 'panel',
            flex: 1,
            layout:{
                type: 'vbox',
                pack: 'start',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'processDiagramgridviewport',
                    flex: 1,
                    id: 'processDiagramgrid',
                    listeners: {
                        beforerender:function () {
                            (Ext.getCmp('processDiagramgrid')).setStore('ProcessDiagramStore');
                            (Ext.getCmp('bbarProcessDiagram')).setStore('ProcessDiagramStore');//分页
                        },
                        cellclick: function(grid,td,cellIndex, record, tr, rowIndex, e, eOpts){       //alert("rowIndex===="+rowIndex); alert("cellIndex===="+cellIndex); alert("td===="+td); alert("tr===="+tr)// var fieldName = grid.getSelectionModel().getDataIndex(cellIndex); // 列名=fieldName；                          //var data = record.get('title');// alert("data=========="+data ); // alert("td.className===="+td.className) // alert("tr.className===="+tr.className)
                          rec=record
                          if(cellIndex==9){//上传文件
                              //alert("上传文件")
                              //recId=record.get('id');
                              view = Ext.widget('importProcessDiagram');   //view =Ext.create('appMis.view.user.SelectDepartment')//奇怪！！！随意修改以下两行或其中一行的数值，就可实现modal
                              document.getElementById('processDiagram').style.setPropertyValue('z-index',0);
                          }
                            //查看文件//判断文件是否存在 // var src=rec.get('titleCode')+"_"+rec.get('title')+"."+rec.get('wjlx');//文件名
                          if(cellIndex==10){
                              //alert("查看文件")
                              var wjlx=rec.get('wjlx');
                              var src="/appMis/static/processDiagram/"+rec.get('titleCode')+"_"+rec.get('title')+"."+wjlx;
                              //alert("src===="+src)
                              Ext.Ajax.request({//根据用户角色的权限用来确定界面上的内容哪些隐藏或不隐藏,生成操作树。
                                  url: '/appMis/processDiagram/checkFile?recId='+rec.get('id'),
                                  async:false,//同步请求数据, true异步
                                  success://回调函数
                                      function (resp, opts) {//成功后的回调方法
                                          if(resp.responseText=='true'){
                                              view = Ext.widget('exportProcessDiagram');   //view =Ext.create('appMis.view.user.SelectDepartment')//奇怪！！！随意修改以下两行或其中一行的数值，就可实现modal
                                              Ext.getCmp('diagramDisplay').setHtml("");
                                              Ext.getCmp('diagramDisplay').removeAll();
                                              var grxxxx = Ext.getCmp('diagramDisplay');
                                              var htmlx='<div id="canvas"  style="width:9050px; height:9050px;"></div>'
                                              htmlx=htmlx+'<button id="save-button" style="position: absolute;top:200px;right: 20px; border: solid 5px green;">保存流程图</button>'
                                              //htmlx=htmlx+'<script src="bower_components/bpmn-js/dist/bpmn-modeler.js"></script>'
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
                                              //alert("xml===="+xml) //加入这条则破坏了AJAX
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
                                                                  console.error('保存流程图失败', err);
                                                              } else {
                                                                  /* //信息写入浏览器的控制台
                                                                  console.info('另存流程图');
                                                                  console.info(xml);
                                                                  //信息写入本地文件
                                                                  var file = new File([xml], "保存流程图.bpmn", { type: "text/plain;charset=utf-8" });
                                                                  saveAs(file);//调用本文件最前面的代码，已经注释了
                                                                  */
                                                                  saveXml(xml)//访问内置函数
                                                              }
                                                              //alert('保存流程图 (see console (F12))');
                                                          });
                                                      });
                                                  }
                                                  importXML(xml);
                                              })(window.BpmnJS);
                                              //内置函数，把流程图写入服务器
                                              function saveXml(xml) {
                                                  Ext.Ajax.request({
                                                      url: '/appMis/processDiagram/saveDiagram?filename='+rec.get('titleCode')+"_"+rec.get('title')+"."+wjlx,
                                                      async:false,//同步请求数据, true异步
                                                      method: 'POST',//默认POST
                                                      params: {
                                                          xml:xml
                                                      },
                                                      success://回调函数
                                                          function (resp, opts) {
                                                              Ext.MessageBox.show({
                                                                  title: "提示",
                                                                  msg: "成功保存流程图！",
                                                                  buttons: Ext.MessageBox.OK
                                                              })
                                                              setTimeout(function () {
                                                                  Ext.Msg.hide();
                                                              }, 2500);
                                                              return;
                                                      }
                                                  })
                                              }

                                          }else{
                                              Ext.MessageBox.show({
                                                  title: "提示",
                                                  msg: "文件已不存在或已损坏, 请重新上传文件！",
                                                  buttons: Ext.MessageBox.OK
                                              })
                                              setTimeout(function () {
                                                  Ext.Msg.hide();
                                              }, 2500);
                                              return;
                                          }
                                      }
                              });
                          }
                          if(cellIndex==11){
                              var wjlx=rec.get('wjlx');
                              var src="/appMis/static/processDiagram/"+rec.get('titleCode')+"_"+rec.get('title')+"."+wjlx;

                          }
                        }
                     }
                }]
        }
    ]
});
 
