//工资关系介绍信
Ext.define('appMis.view.processTask.EmployeeSalaryExplain', {
    extend: 'Ext.window.Window',
    alias: 'widget.employeeSalaryExplain',
    width: "100%",
    height: "100%",
    title: "",
    layout: 'fit',
    autoScroll: true,
    autoShow: true,
    modal: true,//创建模态窗口
    initComponent: function() {
        //this.title=departmentAll+" 工资基金计划申报";//这一条也可以
        this.setTitle(departmentDetail+" ： "+employeeRecord.get('name')+" 工资关系介绍信");
        this.items = [
        {
            xtype: 'panel',
            autoScroll: true,
            header:false,
            bodyPadding: 0,
            border: 0,
            listeners: {
                beforerender: function () {
                    var style = heredoc(function(){/*
         <div id="employeeSalaryExplain" >
         <style id="工资关系介绍信_30242_Styles">

         </style>
         */ });
                    var tmp15 = heredoc(function(){/*
                         工资关系介绍信
         </div>
         */});
                    this.setHtml(style+departmentDetail+"===="+employeeRecord.get('name')+"===="+tmp15);
                }
            }
        }
    ];
    this.buttons= [
        {
            text: '打印',
            icon: 'images/printer/printer.png',
            action: 'printEmployeeSalaryExplain'
        },
        {
            text: '关闭',
            glyph: 'xf00d@FontAwesome',
            scope: this,
            handler: this.close
        }
        ];
        this.callParent(arguments);
    }
});


