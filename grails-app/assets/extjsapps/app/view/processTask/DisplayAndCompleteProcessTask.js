var f=""
Ext.define('appMis.view.processTask.DisplayAndCompleteProcessTask', {
    extend: 'Ext.window.Window',
    xtype: 'displayAndCompleteProcessTask',
    //id: 'displayAndCompleteProcessTask',
    width: '100%',
    height: '100%',
    title: '查看当前任务',
    //layout: 'fit',
    autoShow: true,
    modal: true,//创建模态窗口
    requires: [],
    items: [{
        xtype: 'panel',
        height: 670,
        id: 'proccesstaskcontentDisplay',
        autoScroll: true,
        defaults: {
            margins: '10 0 0 0',
            bodyPadding: 0,
            border: 1,
            padding: '10 5 0 5'
        },
        items: [
            {
                xtype: 'box',
                height: 170, //图片高度
                id: 'processDiagramImage',
                columnWidth: 1,
                width: '100%',
                autoShow: true,
                autoEl: {
                    tag: 'img',    //指定为img标签
                    style: "",//不加这条显示太大！
                    complete : 'off',
                    src: '/appMis/processDiagram/watch.png?rand=' + Math.random(),//指定url路径
                },
               /* onBoxReady: function () {
                    var me = this;
                   // me.el.on("mouseover", me.OnImageMouseOver, me);//鼠标掠过事件
                    me.el.on("mousemove", me.OnImageMouseOver, me);//鼠标移动事件，不能用onmousemove
               },*/
                OnImageMouseOver: function () {
                    var me = this;
                   // alert("====x1======"+x1+"====x2======"+x2)
                    var wev = window.event;
                    var x = wev.offsetX;
                    var y = wev.offsetY;
                    var el = me.getEl().dom;
                    el.setAttribute("title","")
                    f=(f=="")?" ":""
                    if((x>=x1)&&(x<=x1+w1)&&(y>=y1)&&(y<=y1+h1)){
                       /* Ext.create('Ext.tip.ToolTip', {
                            target: 'processDiagramImage',
                            trackMouse: true,
                            html: "单位审核人:"+comment[0]
                        });*/

                       //el.setAttribute("title", "单位审核人"+comment[0]+"===x=" + x +",x1=" + x1 + ",y=" + y + ",y1=" + y1+ ",w1=" + w1 + ",h1=" + h1);//<img src="..." width="100" height="62" title="提示信息">
                        el.setAttribute("title", "单位审核人:"+comment[0]+f)
                    }
                    if((x>=x2)&&(x<=x2+w2)&&(y>=y2)&&(y<=y2+h2)){
                      // el.setAttribute("title", "主管单位审核人"+comment[1]+"===x=" + x +",x2=" + x2 + ",y=" + y + ",y2=" + y2+ ",w2=" + w2 + ",h2=" + h2);//<img src="..." width="100" height="62" title="提示信息">
                        el.setAttribute("title", "主管单位审核人:"+comment[1]+f)
                    }
                    if((x>=x3)&&(x<=x3+w3)&&(y>=y3)&&(y<=y3+h3)){
                        //el.setAttribute("title", "区人社局审核人"+comment[2]+"===x=" + x +",x3=" + x3 + ",y=" + y + ",y3=" + y3+ ",w3=" + w3 + ",h3=" + h3);//<img src="..." width="100" height="62" title="提示信息">
                        el.setAttribute("title", "区人社局审核人:"+comment[2]+f)
                    }
                    if((x>=x4)&&(x<=x4+w4)&&(y>=y4)&&(y<=y4+h4)){
                        //el.setAttribute("title", "市人社局审核人"+comment[3]+"===x=" + x +",x4=" + x4 + ",y=" + y + ",y4=" + y4+ ",w4=" + w4 + ",h4=" + h4);//<img src="..." width="100" height="62" title="提示信息">
                        el.setAttribute("title", "市人社局审核人:"+comment[3]+f)
                    }
                    if((x>=x5)&&(x<=x5+w5)&&(y>=y5)&&(y<=y5+h5)){
                       // el.setAttribute("title", "调入区人社局审核人"+comment[4]+"===x=" + x +",x5=" + x5 + ",y=" + y + ",y5=" + y5+ ",w5=" + w5 + ",h5=" + h5);//<img src="..." width="100" height="62" title="提示信息">
                        el.setAttribute("title", "调入区人社局审核人:"+comment[4]+f)
                    }
                    if((x>=x6)&&(x<=x6+w6)&&(y>=y6)&&(y<=y6+h6)){
                       // el.setAttribute("title", "调入主管单位审核人"+comment[5]+"===x=" + x +",x6=" + x6 + ",y=" + y + ",y6=" + y6+ ",w6=" + w6 + ",h6=" + h6);//<img src="..." width="100" height="62" title="提示信息">
                        el.setAttribute("title", "调入主管单位审核人:"+comment[5]+f)
                    }
                    if((x>=x7)&&(x<=x7+w7)&&(y>=y7)&&(y<=y7+h7)){
                       // el.setAttribute("title", "调入单位审核人"+comment[6]+"===x=" + x +",x7=" + x7 + ",y=" + y + ",y7=" + y7+ ",w7=" + w7 + ",h7=" + h7);//<img src="..." width="100" height="62" title="提示信息">
                        el.setAttribute("title", "调入单位审核人:"+comment[6]+f)
                    }
                },
               listeners: {
                   boxready: function () {
                        var me = this;
                        // me.el.on("mouseover", me.OnImageMouseOver, me);//鼠标掠过事件
                        //me.el.on("mousemove", me.OnImageMouseOver11, me);//鼠标移动事件，不能用onmousemove
                        // me.getEl().on("mousemove", me.OnImageMouseOver11, me);//getEl() = el 就是一个组件的顶级的元素....可以理解为最外层的那个div...
                        // me.el.addListener("mousemove", me.OnImageMouseOver11, me)//等同on(String eventName, Function fn, [Object scope], [Object options])
                        // Ext.get('processDiagramImage').addListener("mousemove", me.OnImageMouseOver, me)
                        // Ext.get('processDiagramImage').on("mousemove", me.OnImageMouseOver, me)
                        // Ext.getCmp('processDiagramImage').on("mousemove", me.OnImageMouseOver, me)//没作用
                         Ext.getCmp('processDiagramImage').el.on("mousemove", me.OnImageMouseOver, me)//有作用
                        //给Ext.get()上添加事件会最终把事件绑定到dom元素上（element DOM）（最终是调用addEventListener or attachEvent or on)
                        //但是Ext.getCmp()添加事件只是把一个事件绑定到EXTJS的组件上而不是DOM元素上;（component）
                    }
               }
            },
            /*{
                xtype: 'panel',
                //测试canvas的使用方法
                id: 'processDiagramImage2',
                height: 110,
                width:"100%",
                columnWidth: 1,
                html: '<canvas id="canvas" width="1000" height="110"></canvas>',
                childEls: ['canvas'],
                OnImageMouseOverxx: function () {
                    var me = this;
                    var wev = window.event;
                    var x = wev.offsetX;
                    var y = wev.offsetY;
                    var el = me.getEl().dom;
                    el.setAttribute("title", "x=" + x + "y=" + y);//<img src="..." width="100" height="62" title="提示信息">
                },
                onDraw: function() {
                   // this.canvas = document.getElementById('canvas');
                    this.canvas = Ext.get("canvas").dom
                    this.ctx = this.canvas.getContext("2d");
                    // create a blank image data
                    var canvas2Data = this.ctx.createImageData(this.canvas.width, this.canvas.height);
                    for ( var x = 0; x < canvas2Data.width; x++) {
                        for ( var y = 0; y < canvas2Data.height; y++) {
                            // Index of the pixel in the array
                            var idx = (x + y * canvas2Data.width) * 4;
                           // assign gray scale value
                            var distance = Math.sqrt((x - canvas2Data.width / 2) * (x - canvas2Data.width / 2) + (y - canvas2Data.height / 2) * (y - canvas2Data.height / 2));
                            var cvalue = (128.0 + (128.0 * Math.sin(distance / 8.0)));
                            canvas2Data.data[idx + 0] = cvalue; // Red channel
                            canvas2Data.data[idx + 1] = cvalue; // Green channel
                            canvas2Data.data[idx + 2] = cvalue; // Blue channel
                            canvas2Data.data[idx + 3] = 255; // Alpha channel
                        }
                    }
                    this.ctx.putImageData(canvas2Data, 0, 0); // at coords 0,0

                    // draw author infomation
                    this.ctx.fillStyle = "red";
                    this.ctx.font = "24px Times New Roman";
                    this.ctx.fillText("HTML5 Demo - by gloomyfish ", 50, 60);

                },
                drawPreviewImage: function(){
                    var  me = this;
                    //canvas = document.getElementById('canvas');
                    this.canvas = Ext.get("canvas").dom
                   var context = this.canvas.getContext("2d");
                   //  this.context = canvas.getContext("2d");
                     var img = new Image();
                    // img.src= '/appMis/static/processDiagram/watch.png';//指定url路径,
                    img.src= '/appMis/static/processDiagram/'+executionId+'_watch.png?rand=' + Math.random();//指定url路径,
                    // img.src = canvas.toDataURL('/appMis/static/processDiagram/watch.png?rand=' + Math.random());//指定url路径
                   // me.drawText(canvas,"aaaa");
                     img.onload = function(){
                        // drawImage在图片渲染完成后执行
                         var imgWidth = img.naturalWidth;
                             screenWidth  = me.getWidth();
                             scaleX = 1;
                         if (imgWidth > screenWidth)
                             scaleX = screenWidth/imgWidth;
                         var imgHeight = img.naturalHeight;
                             screenHeight = me.getHeight();
                             scaleY = 1;
                         if (imgHeight > screenHeight)
                             scaleY = screenHeight/imgHeight;
                         var scale = scaleY;
                         if(scaleX < scaleY)
                             scale = scaleX;
                         if(scale < 1){
                             imgHeight = imgHeight*scale;
                             imgWidth = imgWidth*scale;
                         }
                         canvas.height = imgHeight;
                         canvas.width = imgWidth;
                        //this.context.drawImage(img, 0, 0, img.naturalWidth, img.naturalHeight);
                         context.drawImage(img, 0, 0, img.naturalWidth, img.naturalHeight, 0,0, imgWidth, imgHeight);
                         context.font ='16px Arial';
                         context.fillStyle = '#0099CC';
                         context.fillText('克拉玛依市工资系统',0,20);
                         context.strokeStyle = '#ff4174';
                         context.strokeText('工资业务申报',0,40);
                     };
                    var wev = window.event;
                    var x = wev.offsetX;
                    var y = wev.offsetY;
                    var el = me.getEl().dom;
                    el.setAttribute("title", "x=" + x + "y=" + y);//<img src="..." width="100" height="62" title="提示信息">
                },
                listeners: {
                   afterrender:function(){
                        var me = this
                       //Ext.get("canvas").addListener("mousemove", me.drawPreviewImage, me)
                       //Ext.get("canvas").on("mousemove", me.drawPreviewImage, me)
                      // Ext.get("canvas").on("mousemove", me.OnImageMouseOverxx, me)
                      me.el.on("mousemove", me.drawPreviewImage, me)
                      // me.el.on("mousemove", me.onDraw, me)
                      // me.el.on("mousemove", me.OnImageMouseOverxx, me)
                   }
                }
            },*/
            {
                xtype: 'panel',
                height: 110,
                width: '100%',
                layout: 'column',
                items: [
                    {
                        xtype: "textarea",
                        name:'shyj',
                        id:'shyj',
                        columnWidth:0.8,
                        fieldLabel: "审核意见",
                        labelSeparator: "：",
                        labelWidth: 180,
                        labelAlign: 'right'
                    },
                    {
                        xtype: 'toolbar',
                        columnWidth:0.2,
                        items: [
                            {
                                text: '同意',
                               // glyph: 'xf0c7@FontAwesome',
                                hidden: ((loginUserRole == "单位管理员") || (loginUserRole == "管理员")),
                                icon: 'tupian/accept.png',
                                action: 'agree'
                            }, {
                                text: '不同意',
                                //glyph: 'xf00d@FontAwesome',
                                hidden: ((loginUserRole == "单位管理员") || (loginUserRole == "管理员")),
                                icon: 'tupian/cancel.png',
                                action: 'disagree'
                            }
                        ]
                    },
                    /*{
                        xtype: "button",
                        columnWidth: 0.08,
                        hidden: ((loginUserRole == "单位管理员") || (loginUserRole == "管理员")),
                        text: "同意",
                        action: 'agree'
                    },
                    {
                        xtype:'label',
                        columnWidth: 0.02,
                        text:"_____"
                    },
                    {
                        xtype: "button",
                        columnWidth: 0.08,
                        hidden: ((loginUserRole == "单位管理员") || (loginUserRole == "管理员")),
                        text: "不同意",
                        action: 'disagree'
                    },*/
                    {
                        xtype: "button",
                        columnWidth: 0.1,
                       // hidden: ((loginUserRole!= "市人社局审核人")),
                        hidden:true,
                        text: "生成工资介绍信",
                        handler : function(){
                            Ext.Ajax.request({
                                url: '/appMis/employee/readEmployeeByEmployeeCode?employeeCode=' + semployeeCode ,
                                async: false,//同步请求数据, true异步
                                success://回调函数
                                    function (resp, opts) {//成功后的回调方法
                                        var obj1 = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                        var currentEmployee1 = obj1.employee2;//为Json对象
                                        employeeRecord= Ext.create('appMis.model.EmployeeModel');
                                        currentEmployee1.birthDate = (new Date(currentEmployee1.birthDate)).pattern("yyyy-MM-dd");//日期型数据要转换
                                        currentEmployee1.workDate = (new Date(currentEmployee1.workDate)).pattern("yyyy-MM-dd");//日期型数据要转换
                                        currentEmployee1.thatworkDate = (new Date(currentEmployee1.thatworkDate)).pattern("yyyy-MM-dd");//日期型数据要转换
                                        employeeRecord.set(currentEmployee1);
                                    }
                            });
                            Ext.Ajax.request({
                                url: '/appMis/department/readDepartmentByTreeId?dep_tree_id=' + employeeRecord.get('treeId') ,
                                async: false,//同步请求数据, true异步
                                success://回调函数
                                    function (resp, opts) {//成功后的回调方法
                                        var obj1 = eval('(' + resp.responseText + ')');//将获取的Json字符串转换为Json对象
                                        var currentDepartment1 = obj1.department2;//为Json对象
                                        departmentDetail=currentDepartment1.department
                                    }
                            });

                            var view = Ext.widget('employeeSalaryExplain');//编辑窗口中的记录与Grid中选中的记录是同一条记录
                            this.upper.style.setPropertyValue('z-index', 0);//可在最前层显示图片
                        }
                    }
                ]
            }
        ]
    }],
    listeners :{
        'hide':function() {
            var executionId=(Ext.getCmp("processDiagramImage").getEl().dom.src).toString()//http://localhost:8888/appMis/static/processDiagram/647513_watch.png?rand=0.1306391792218201
            executionId=(executionId.split("processDiagram/")[1]).split("_watch.png")[0]
            //alert("win窗口关闭时，会执行本方法!!!!==="+s)
            //删除生成的流程图文件
             Ext.Ajax.request({
                  url: '/appMis/processTask/deleteProcessPicByExecutionId?executionId='+executionId,
                  //url: '/appMis/mainBusiness/deleteExcelDepMonthlySalary?dep_tree_id=' + currentTreeNode.substr(1)+'&departmentDetail='+departmentDetail+'&currentDepMonthlySalaryDate='+currentDepMonthlySalaryDate,
                  async: false,//同步
                  method: 'POST',
                  success://回调函数
                  function (resp, opts) { }
             })
        }
    }
});
           
