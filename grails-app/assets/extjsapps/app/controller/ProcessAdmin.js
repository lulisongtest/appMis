'use strict'

var currentRecord = 0;
var view = null;
var processAdminfirst = 1;//第一次或重新打开form
Ext.define('appMis.controller.ProcessAdmin', {
    extend: 'Ext.app.Controller',

    init: function () {
        this.control({
           'processAdminviewport > toolbar >  button[action=displayProcess]': {
                click: this.displayProcess
           },
           'processAdminviewport > toolbar >  button[action=designProcess]': {
                click: this.designProcess
            },
            'processAdminviewport > toolbar >  button[action=displayProcessDiagram]': {
                click: this.displayProcessDiagram
            },
            'processAdminviewport > toolbar >  button[action=findPersonTask]': {
                click: this.findPersonTask
            },
            'processAdminviewport > toolbar >  button[action=displayTask]': {
                click: this.displayTask
            }


        })
    },
    //显示已定义的流程
    displayProcess: function (button) {
        //alert("显示已定义的流程")
        //Ext.getCmp('processadmincontent').removeAll();
       // var xx=Ext.create('appMis.view.processDiagram.ProcessDiagramViewport');
       // Ext.getCmp('processadmincontent').add(xx)
    },
    //显示当前任务
    displayTask: function (button) {
        //alert("显示当前任务")
        //Ext.getCmp('processadmincontent').removeAll();
       // var xx=Ext.create('appMis.view.processTask.ProcessTaskViewport');
        //Ext.getCmp('processadmincontent').add(xx)
    },
    //查询办理人的任务
    findPersonTask: function (button) {
        Ext.Ajax.request({
            url:'/appMis/processAdmin/findPersonTask',
            async:false,//同步
            //params:{           },
            method : 'POST',
            success://回调函数
                function (resp, opts) {//成功后的回调方法
                    var obj = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                    Ext.Msg.show({
                        title: '操作提示 ',
                        // msg: '部署流程定义成功！ ',
                        msg: '查询办理人的任务成功！ '+"流程实例ID：" +obj.list,
                        buttons: Ext.MessageBox.OK
                    });
                    setTimeout(function () {
                        Ext.Msg.hide();
                    },1500);
                },
            failure: function(resp,opts) {
                Ext.Msg.show({
                    title: '操作提示 ',
                    msg: '查询办理人的任务失败！ ',
                    buttons: Ext.MessageBox.OK
                });
                setTimeout(function () {
                    Ext.Msg.hide();
                },2500)
            }
        });
    },

    designProcess: function (button) {
        Ext.getCmp('processadmincontent').setHtml("");
        Ext.getCmp('processadmincontent').removeAll();
        Ext.getCmp('processadmincontent').add(new Ext.Panel({
            items:{
                // xtype: 'myshviewport',
                //xtype: 'depMonthlySalaryviewport',
                // id:'mysh',
                // listeners: {
                //   beforerender: function (combo) {
                //      setHeight1();
                //      this.height = height1-27;
                //   }
                // }
            }
        }));
        //Ext.getCmp('mysh').setTitle("我的审核");
        var grxxxx = Ext.getCmp('processadmincontent');
        var htmlx='<div id="canvas"  style="width:1050px; height:1050px;"></div>'
        htmlx=htmlx+'<button id="save-button" style="position: absolute;top:20px;right: 20px; border: solid 5px green;">保存流程图</button>'
       // htmlx=htmlx+'<script src="bower_components/bpmn-js/dist/bpmn-modeler.js"></script>'
        grxxxx.setHtml(htmlx);
        var xml=""
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function() {
            if ((xhr.readyState == 4)&&(xhr.status == 200)) {
                 xml=xhr.responseText
            }
        };
       xhr.open('GET', 'processDiagram/人员单位变动流程.bpmn.xml', false );
        ///xhr.open('GET', 'process/financialReport.bpmn20.xml', false );
       // xhr.open('GET', 'process/ccc.bpmn.xml', false );
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
                            console.error('diagram save failed', err);
                        } else {
                           // alert("xml===="+xml)
                            console.info('diagram saved');
                            console.info(xml);
                        }

                        alert('diagram saved (see console (F12))');
                    });
                });
            }
            importXML(xml);
        })(window.BpmnJS);
    },

    displayProcessDiagram: function (button) {
        Ext.getCmp('processadmincontent').setHtml("");
        Ext.getCmp('processadmincontent').removeAll();
        Ext.getCmp('processadmincontent').add(new Ext.Panel({
            items:{
                // xtype: 'myshviewport',
                //xtype: 'depMonthlySalaryviewport',
                // id:'mysh',
                // listeners: {
                //   beforerender: function (combo) {
                //      setHeight1();
                //      this.height = height1-27;
                //   }
                // }
            }
        }));
        //Ext.getCmp('mysh').setTitle("我的审核");
        var grxxxx = Ext.getCmp('processadmincontent');
        var htmlx='<div id="canvas"  style="width:2000px; height:2000px;"></div>'
        htmlx=htmlx+'<button id="save-button" style="position: absolute;top:20px;right: 20px; border: solid 5px green;">保存流程图</button>'
        //htmlx=htmlx+'<script src="bower_components/bpmn-js/dist/bpmn-modeler.js"></script>'
        grxxxx.setHtml(htmlx);
        //(function(BpmnModeler) {
            // create modeler
            var bpmnModeler = new window.BpmnJS({
                container: '#canvas'
            });
            // import function
            function importXML(xml) {
                // import diagram
                bpmnModeler.importXML(xml, function(err) {
                    if (err) {
                        return console.error('could not import BPMN 2.0 diagram', err);
                    }
                    var canvas = bpmnModeler.get('canvas');
                    // zoom to fit full viewport
                    canvas.zoom('fit-viewport');
                });
                // save diagram on button click
                var saveButton = document.querySelector('#save-button');
                saveButton.addEventListener('click', function() {
                    // get the diagram contents
                    bpmnModeler.saveXML({ format: true }, function(err, xml) {
                        if (err) {
                            console.error('diagram save failed', err);
                        } else {
                            console.info('diagram saved');
                            console.info(xml);
                        }

                        alert('diagram saved (see console (F12))');
                    });
                });
            }
            // a diagram to display
            // see index-async.js on how to load the diagram asynchronously from a url.
            // (requires a running webserver)

        var tmp6 = heredoc(function () {/*
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:activiti="http://activiti.org/bpmn" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC" xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI" xmlns:tns="http://www.activiti.org/test" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" expressionLanguage="http://www.w3.org/1999/XPath" id="m1507024345299" name="" targetNamespace="http://www.activiti.org/test" typeLanguage="http://www.w3.org/2001/XMLSchema">
  <process id="myProcess_1" isClosed="false" isExecutable="true" processType="None">
    <startEvent id="_2" name="请假申请"/>
    <userTask activiti:exclusive="true" id="_3" name="部门负责人审核"/>
    <sequenceFlow id="_4" sourceRef="_2" targetRef="_3"/>
    <userTask activiti:exclusive="true" id="_7" name="UserTask"/>
    <sequenceFlow id="_8" sourceRef="_3" targetRef="_7"/>
    <endEvent id="_9" name="请假结束"/>
    <sequenceFlow id="_10" sourceRef="_7" targetRef="_9"/>
  </process>
  <bpmndi:BPMNDiagram documentation="background=#FFFFFF;count=1;horizontalcount=1;orientation=0;width=842.4;height=1195.2;imageableWidth=832.4;imageableHeight=1185.2;imageableX=5.0;imageableY=5.0" id="Diagram-_1" name="New Diagram">
    <bpmndi:BPMNPlane bpmnElement="myProcess_1">
      <bpmndi:BPMNShape bpmnElement="_2" id="Shape-_2">
        <omgdc:Bounds height="32.0" width="32.0" x="240.0" y="250.0"/>
        <bpmndi:BPMNLabel>
          <omgdc:Bounds height="32.0" width="32.0" x="230.0" y="290.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape bpmnElement="_3" id="Shape-_3">
        <omgdc:Bounds height="55.0" width="120.0" x="410.0" y="240.0"/>
        <bpmndi:BPMNLabel>
          <omgdc:Bounds height="55.0" width="120.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape bpmnElement="_7" id="Shape-_7">
        <omgdc:Bounds height="55.0" width="85.0" x="625.0" y="235.0"/>
        <bpmndi:BPMNLabel>
          <omgdc:Bounds height="55.0" width="85.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNShape bpmnElement="_9" id="Shape-_9">
        <omgdc:Bounds height="32.0" width="32.0" x="855.0" y="260.0"/>
        <bpmndi:BPMNLabel>
          <omgdc:Bounds height="32.0" width="32.0" x="845.0" y="300.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNShape>
      <bpmndi:BPMNEdge bpmnElement="_4" id="BPMNEdge__4" sourceElement="_2" targetElement="_3">
        <omgdi:waypoint x="272.0" y="266.0"/>
        <omgdi:waypoint x="410.0" y="267.5"/>
        <bpmndi:BPMNLabel>
          <omgdc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge bpmnElement="_8" id="BPMNEdge__8" sourceElement="_3" targetElement="_7">
        <omgdi:waypoint x="530.0" y="267.5"/>
        <omgdi:waypoint x="625.0" y="262.5"/>
        <bpmndi:BPMNLabel>
          <omgdc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
      <bpmndi:BPMNEdge bpmnElement="_10" id="BPMNEdge__10" sourceElement="_7" targetElement="_9">
        <omgdi:waypoint x="710.0" y="262.5"/>
        <omgdi:waypoint x="855.0" y="276.0"/>
        <bpmndi:BPMNLabel>
          <omgdc:Bounds height="0.0" width="0.0" x="0.0" y="0.0"/>
        </bpmndi:BPMNLabel>
      </bpmndi:BPMNEdge>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</definitions>
        */});


        var tmp7 = heredoc(function () {/*
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<semantic:definitions id="_1275940932088" targetNamespace="http://www.trisotech.com/definitions/_1275940932088" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:semantic="http://www.omg.org/spec/BPMN/20100524/MODEL">
    <semantic:message id="_1275940932310"/>
    <semantic:message id="_1275940932433"/>
    <semantic:process isExecutable="false" id="_6-1">
        <semantic:laneSet id="ls_6-438">
            <semantic:lane name="clerk" id="_6-650">
                <semantic:flowNodeRef>_6-450</semantic:flowNodeRef>
                <semantic:flowNodeRef>_6-652</semantic:flowNodeRef>
                <semantic:flowNodeRef>_6-674</semantic:flowNodeRef>
                <semantic:flowNodeRef>_6-695</semantic:flowNodeRef>
            </semantic:lane>
            <semantic:lane name="pizza 主厨 chef" id="_6-446">
                <semantic:flowNodeRef>_6-463</semantic:flowNodeRef>
            </semantic:lane>
            <semantic:lane name="delivery boy" id="_6-448">
                <semantic:flowNodeRef>_6-514</semantic:flowNodeRef>
                <semantic:flowNodeRef>_6-565</semantic:flowNodeRef>
                <semantic:flowNodeRef>_6-616</semantic:flowNodeRef>
            </semantic:lane>
        </semantic:laneSet>
        <semantic:startEvent name="接收订单" id="_6-450">
            <semantic:outgoing>_6-630</semantic:outgoing>
            <semantic:messageEventDefinition messageRef="_1275940932310"/>
        </semantic:startEvent>
        <semantic:parallelGateway gatewayDirection="Unspecified" name="" id="_6-652">
            <semantic:incoming>_6-630</semantic:incoming>
            <semantic:outgoing>_6-691</semantic:outgoing>
            <semantic:outgoing>_6-693</semantic:outgoing>
        </semantic:parallelGateway>
        <semantic:intermediateCatchEvent parallelMultiple="false" name="&#8222;我的披萨在哪?&#8220;" id="_6-674">
            <semantic:incoming>_6-691</semantic:incoming>
            <semantic:incoming>_6-746</semantic:incoming>
            <semantic:outgoing>_6-748</semantic:outgoing>
            <semantic:messageEventDefinition messageRef="_1275940932433"/>
        </semantic:intermediateCatchEvent>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="Calm customer" id="_6-695">
            <semantic:incoming>_6-748</semantic:incoming>
            <semantic:outgoing>_6-746</semantic:outgoing>
        </semantic:task>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="Bake the pizza" id="_6-463">
            <semantic:incoming>_6-693</semantic:incoming>
            <semantic:outgoing>_6-632</semantic:outgoing>
        </semantic:task>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="Deliver the pizza" id="_6-514">
            <semantic:incoming>_6-632</semantic:incoming>
            <semantic:outgoing>_6-634</semantic:outgoing>
        </semantic:task>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="Receive payment" id="_6-565">
            <semantic:incoming>_6-634</semantic:incoming>
            <semantic:outgoing>_6-636</semantic:outgoing>
        </semantic:task>
        <semantic:endEvent name="" id="_6-616">
            <semantic:incoming>_6-636</semantic:incoming>
            <semantic:terminateEventDefinition/>
        </semantic:endEvent>
        <semantic:sequenceFlow sourceRef="_6-450" targetRef="_6-652" name="" id="_6-630"/>
        <semantic:sequenceFlow sourceRef="_6-463" targetRef="_6-514" name="" id="_6-632"/>
        <semantic:sequenceFlow sourceRef="_6-514" targetRef="_6-565" name="" id="_6-634"/>
        <semantic:sequenceFlow sourceRef="_6-565" targetRef="_6-616" name="" id="_6-636"/>
        <semantic:sequenceFlow sourceRef="_6-652" targetRef="_6-674" name="" id="_6-691"/>
        <semantic:sequenceFlow sourceRef="_6-652" targetRef="_6-463" name="" id="_6-693"/>
        <semantic:sequenceFlow sourceRef="_6-695" targetRef="_6-674" name="" id="_6-746"/>
        <semantic:sequenceFlow sourceRef="_6-674" targetRef="_6-695" name="" id="_6-748"/>
    </semantic:process>
    <semantic:message id="_1275940932198"/>
    <semantic:process isExecutable="false" id="_6-2">
        <semantic:startEvent name="开始披萨订单" id="_6-61">
            <semantic:outgoing>_6-125</semantic:outgoing>
        </semantic:startEvent>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="选择披萨" id="_6-74">
            <semantic:incoming>_6-125</semantic:incoming>
            <semantic:outgoing>_6-178</semantic:outgoing>
        </semantic:task>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="订购披萨" id="_6-127">
            <semantic:incoming>_6-178</semantic:incoming>
            <semantic:outgoing>_6-420</semantic:outgoing>
        </semantic:task>
        <semantic:eventBasedGateway eventGatewayType="Exclusive" instantiate="false" gatewayDirection="Unspecified" name="" id="_6-180">
            <semantic:incoming>_6-420</semantic:incoming>
            <semantic:incoming>_6-430</semantic:incoming>
            <semantic:outgoing>_6-422</semantic:outgoing>
            <semantic:outgoing>_6-424</semantic:outgoing>
        </semantic:eventBasedGateway>
        <semantic:intermediateCatchEvent parallelMultiple="false" name="收到披萨" id="_6-202">
            <semantic:incoming>_6-422</semantic:incoming>
            <semantic:outgoing>_6-428</semantic:outgoing>
            <semantic:messageEventDefinition messageRef="_1275940932198"/>
        </semantic:intermediateCatchEvent>
        <semantic:intermediateCatchEvent parallelMultiple="false" name="60 分钟" id="_6-219">
            <semantic:incoming>_6-424</semantic:incoming>
            <semantic:outgoing>_6-426</semantic:outgoing>
            <semantic:timerEventDefinition>
                <semantic:timeDate/>
            </semantic:timerEventDefinition>
        </semantic:intermediateCatchEvent>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="查询披萨" id="_6-236">
            <semantic:incoming>_6-426</semantic:incoming>
            <semantic:outgoing>_6-430</semantic:outgoing>
        </semantic:task>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="付披萨款" id="_6-304">
            <semantic:incoming>_6-428</semantic:incoming>
            <semantic:outgoing>_6-434</semantic:outgoing>
        </semantic:task>
        <semantic:task completionQuantity="1" isForCompensation="false" startQuantity="1" name="吃披萨" id="_6-355">
            <semantic:incoming>_6-434</semantic:incoming>
            <semantic:outgoing>_6-436</semantic:outgoing>
        </semantic:task>
        <semantic:endEvent name="Hunger satisfied" id="_6-406">
            <semantic:incoming>_6-436</semantic:incoming>
        </semantic:endEvent>
        <semantic:sequenceFlow sourceRef="_6-61" targetRef="_6-74" name="" id="_6-125"/>
        <semantic:sequenceFlow sourceRef="_6-74" targetRef="_6-127" name="" id="_6-178"/>
        <semantic:sequenceFlow sourceRef="_6-127" targetRef="_6-180" name="" id="_6-420"/>
        <semantic:sequenceFlow sourceRef="_6-180" targetRef="_6-202" name="" id="_6-422"/>
        <semantic:sequenceFlow sourceRef="_6-180" targetRef="_6-219" name="" id="_6-424"/>
        <semantic:sequenceFlow sourceRef="_6-219" targetRef="_6-236" name="" id="_6-426"/>
        <semantic:sequenceFlow sourceRef="_6-202" targetRef="_6-304" name="" id="_6-428"/>
        <semantic:sequenceFlow sourceRef="_6-236" targetRef="_6-180" name="" id="_6-430"/>
        <semantic:sequenceFlow sourceRef="_6-304" targetRef="_6-355" name="" id="_6-434"/>
        <semantic:sequenceFlow sourceRef="_6-355" targetRef="_6-406" name="" id="_6-436"/>
    </semantic:process>
    <semantic:collaboration id="C1275940932557">
        <semantic:participant name="披萨用户" processRef="_6-2" id="_6-53"/>
        <semantic:participant name="披萨供应商" processRef="_6-1" id="_6-438"/>
        <semantic:messageFlow name="订购披萨" sourceRef="_6-127" targetRef="_6-450" id="_6-638"/>
        <semantic:messageFlow name="" sourceRef="_6-236" targetRef="_6-674" id="_6-642"/>
        <semantic:messageFlow name="receipt" sourceRef="_6-565" targetRef="_6-304" id="_6-646"/>
        <semantic:messageFlow name="money" sourceRef="_6-304" targetRef="_6-565" id="_6-648"/>
        <semantic:messageFlow name="pizza" sourceRef="_6-514" targetRef="_6-202" id="_6-640"/>
        <semantic:messageFlow name="" sourceRef="_6-695" targetRef="_6-236" id="_6-750"/>
    </semantic:collaboration>
    <bpmndi:BPMNDiagram documentation="" id="Trisotech.Visio-_6" name="Untitled Diagram" resolution="96.00000267028808">
        <bpmndi:BPMNPlane bpmnElement="C1275940932557">
            <bpmndi:BPMNShape bpmnElement="_6-53" isHorizontal="true" id="Trisotech.Visio__6-53">
                <dc:Bounds height="294.0" width="1044.0" x="12.0" y="12.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-438" isHorizontal="true" id="Trisotech.Visio__6-438">
                <dc:Bounds height="337.0" width="905.0" x="12.0" y="372.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-650" isHorizontal="true" id="Trisotech.Visio__6__6-650">
                <dc:Bounds height="114.0" width="875.0" x="42.0" y="372.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-446" isHorizontal="true" id="Trisotech.Visio__6__6-446">
                <dc:Bounds height="114.0" width="875.0" x="42.0" y="486.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-448" isHorizontal="true" id="Trisotech.Visio__6__6-448">
                <dc:Bounds height="109.0" width="875.0" x="42.0" y="600.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-450" id="Trisotech.Visio__6__6-450">
                <dc:Bounds height="30.0" width="30.0" x="79.0" y="405.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-652" id="Trisotech.Visio__6__6-652">
                <dc:Bounds height="42.0" width="42.0" x="140.0" y="399.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-674" id="Trisotech.Visio__6__6-674">
                <dc:Bounds height="32.0" width="32.0" x="218.0" y="404.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-695" id="Trisotech.Visio__6__6-695">
                <dc:Bounds height="68.0" width="83.0" x="286.0" y="386.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-463" id="Trisotech.Visio__6__6-463">
                <dc:Bounds height="68.0" width="83.0" x="252.0" y="521.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-514" id="Trisotech.Visio__6__6-514">
                <dc:Bounds height="68.0" width="83.0" x="464.0" y="629.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-565" id="Trisotech.Visio__6__6-565">
                <dc:Bounds height="68.0" width="83.0" x="603.0" y="629.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-616" id="Trisotech.Visio__6__6-616">
                <dc:Bounds height="32.0" width="32.0" x="722.0" y="647.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-61" id="Trisotech.Visio__6__6-61">
                <dc:Bounds height="30.0" width="30.0" x="66.0" y="96.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-74" id="Trisotech.Visio__6__6-74">
                <dc:Bounds height="68.0" width="83.0" x="145.0" y="77.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-127" id="Trisotech.Visio__6__6-127">
                <dc:Bounds height="68.0" width="83.0" x="265.0" y="77.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-180" id="Trisotech.Visio__6__6-180">
                <dc:Bounds height="42.0" width="42.0" x="378.0" y="90.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-202" id="Trisotech.Visio__6__6-202">
                <dc:Bounds height="32.0" width="32.0" x="647.0" y="95.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-219" id="Trisotech.Visio__6__6-219">
                <dc:Bounds height="32.0" width="32.0" x="448.0" y="184.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-236" id="Trisotech.Visio__6__6-236">
                <dc:Bounds height="68.0" width="83.0" x="517.0" y="166.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-304" id="Trisotech.Visio__6__6-304">
                <dc:Bounds height="68.0" width="83.0" x="726.0" y="77.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-355" id="Trisotech.Visio__6__6-355">
                <dc:Bounds height="68.0" width="83.0" x="834.0" y="77.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNShape bpmnElement="_6-406" id="Trisotech.Visio__6__6-406">
                <dc:Bounds height="32.0" width="32.0" x="956.0" y="95.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNShape>
            <bpmndi:BPMNEdge bpmnElement="_6-640" messageVisibleKind="initiating" id="Trisotech.Visio__6__6-640">
                <di:waypoint x="506.0" y="629.0"/>
                <di:waypoint x="506.0" y="384.0"/>
                <di:waypoint x="663.0" y="384.0"/>
                <di:waypoint x="663.0" y="127.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-630" id="Trisotech.Visio__6__6-630">
                <di:waypoint x="109.0" y="420.0"/>
                <di:waypoint x="140.0" y="420.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-691" id="Trisotech.Visio__6__6-691">
                <di:waypoint x="182.0" y="420.0"/>
                <di:waypoint x="200.0" y="420.0"/>
                <di:waypoint x="218.0" y="420.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-648" messageVisibleKind="initiating" id="Trisotech.Visio__6__6-648">
                <di:waypoint x="754.0" y="145.0"/>
                <di:waypoint x="754.0" y="408.0"/>
                <di:waypoint x="630.0" y="408.0"/>
                <di:waypoint x="631.0" y="629.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-422" id="Trisotech.Visio__6__6-422">
                <di:waypoint x="420.0" y="111.0"/>
                <di:waypoint x="438.0" y="111.0"/>
                <di:waypoint x="647.0" y="111.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-646" messageVisibleKind="non_initiating" id="Trisotech.Visio__6__6-646">
                <di:waypoint x="658.0" y="629.0"/>
                <di:waypoint x="658.0" y="432.0"/>
                <di:waypoint x="782.0" y="432.0"/>
                <di:waypoint x="782.0" y="145.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-428" id="Trisotech.Visio__6__6-428">
                <di:waypoint x="679.0" y="111.0"/>
                <di:waypoint x="726.0" y="111.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-748" id="Trisotech.Visio__6__6-748">
                <di:waypoint x="250.0" y="420.0"/>
                <di:waypoint x="268.0" y="420.0"/>
                <di:waypoint x="286.0" y="420.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-420" id="Trisotech.Visio__6__6-420">
                <di:waypoint x="348.0" y="111.0"/>
                <di:waypoint x="366.0" y="111.0"/>
                <di:waypoint x="378.0" y="111.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-636" id="Trisotech.Visio__6__6-636">
                <di:waypoint x="686.0" y="663.0"/>
                <di:waypoint x="704.0" y="663.0"/>
                <di:waypoint x="722.0" y="663.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-750" id="Trisotech.Visio__6__6-750">
                <di:waypoint x="328.0" y="386.0"/>
                <di:waypoint x="328.0" y="348.0"/>
                <di:waypoint x="572.0" y="348.0"/>
                <di:waypoint x="572.0" y="234.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-436" id="Trisotech.Visio__6__6-436">
                <di:waypoint x="918.0" y="111.0"/>
                <di:waypoint x="936.0" y="111.0"/>
                <di:waypoint x="956.0" y="111.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-632" id="Trisotech.Visio__6__6-632">
                <di:waypoint x="335.0" y="555.0"/>
                <di:waypoint x="353.0" y="555.0"/>
                <di:waypoint x="353.0" y="663.0"/>
                <di:waypoint x="464.0" y="663.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-634" id="Trisotech.Visio__6__6-634">
                <di:waypoint x="548.0" y="663.0"/>
                <di:waypoint x="603.0" y="663.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-125" id="Trisotech.Visio__6__6-125">
                <di:waypoint x="96.0" y="111.0"/>
                <di:waypoint x="114.0" y="111.0"/>
                <di:waypoint x="145.0" y="111.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-430" id="Trisotech.Visio__6__6-430">
                <di:waypoint x="600.0" y="200.0"/>
                <di:waypoint x="618.0" y="200.0"/>
                <di:waypoint x="618.0" y="252.0"/>
                <di:waypoint x="576.0" y="252.0"/>
                <di:waypoint x="549.0" y="252.0"/>
                <di:waypoint x="360.0" y="252.0"/>
                <di:waypoint x="360.0" y="111.0"/>
                <di:waypoint x="378.0" y="111.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-642" id="Trisotech.Visio__6__6-642">
                <di:waypoint x="545.0" y="234.0"/>
                <di:waypoint x="545.0" y="324.0"/>
                <di:waypoint x="234.0" y="324.0"/>
                <di:waypoint x="234.0" y="404.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-424" id="Trisotech.Visio__6__6-424">
                <di:waypoint x="399.0" y="132.0"/>
                <di:waypoint x="399.0" y="200.0"/>
                <di:waypoint x="448.0" y="200.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-638" messageVisibleKind="initiating" id="Trisotech.Visio__6__6-638">
                <di:waypoint x="306.0" y="145.0"/>
                <di:waypoint x="306.0" y="252.0"/>
                <di:waypoint x="94.0" y="252.0"/>
                <di:waypoint x="94.0" y="405.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-426" id="Trisotech.Visio__6__6-426">
                <di:waypoint x="480.0" y="200.0"/>
                <di:waypoint x="498.0" y="200.0"/>
                <di:waypoint x="517.0" y="200.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-693" id="Trisotech.Visio__6__6-693">
                <di:waypoint x="161.0" y="441.0"/>
                <di:waypoint x="161.0" y="556.0"/>
                <di:waypoint x="252.0" y="555.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-178" id="Trisotech.Visio__6__6-178">
                <di:waypoint x="228.0" y="111.0"/>
                <di:waypoint x="265.0" y="111.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-746" id="Trisotech.Visio__6__6-746">
                <di:waypoint x="370.0" y="420.0"/>
                <di:waypoint x="386.0" y="420.0"/>
                <di:waypoint x="386.0" y="474.0"/>
                <di:waypoint x="191.0" y="474.0"/>
                <di:waypoint x="191.0" y="420.0"/>
                <di:waypoint x="218.0" y="420.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
            <bpmndi:BPMNEdge bpmnElement="_6-434" id="Trisotech.Visio__6__6-434">
                <di:waypoint x="810.0" y="111.0"/>
                <di:waypoint x="834.0" y="111.0"/>
                <bpmndi:BPMNLabel/>
            </bpmndi:BPMNEdge>
        </bpmndi:BPMNPlane>
    </bpmndi:BPMNDiagram>
</semantic:definitions>


        */})


            importXML(tmp6);
        }
      //)(window.BpmnJS);
  // }

});
