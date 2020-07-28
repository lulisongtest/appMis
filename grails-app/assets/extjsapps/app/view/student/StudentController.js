/**
 * This class is the controller for the main view for the application. It is specified as
 * the "controller" of the Main view class.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */

Ext.define('appMis.view.student.StudentController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.student',

    studentEdit: function (sender, record) {
        selectUsername=record[0].get("username")
      //  alert("selectUsername==="+selectUsername)
        var mainPanel = Ext.create({
            xtype:'studentedit',
        });
        //Ext.getCmp("studentedit1").fillRecord(record[0]);
        Ext.getCmp("studentedit").setRecord(record[0])
        Ext.getCmp("myImage1").setSrc(URLA + '/appMis/student/displayPhoto?username=' + selectUsername + '&time=' + Math.random())
       //var mainPanel = Ext.create('appMis.view.student.StudentEdit');
       // Ext.Msg.confirm('Confirm', 'Are you sure?---student'+record[0].get("truename"), 'onConfirm', this);
    },

    saveStudentEdit:function () {
       // alert("111selectUsername===="+selectUsername)
        var form = this.getView();
        var record=Ext.create('appMis.model.StudentModel');
        form.fillRecord(record);
       // values = form.getValues();
       // record.set(values);
       //alert("11record.get(\"sex\")==="+record.get("sex"));
      // alert("11record.get(\"major\")==="+record.get("major"));
       // form.submit({//不行
        Ext.Ajax.request({
            url:  URLA + '/appMis/student/saveStudentEdit',
            params:{
                username:selectUsername,
                //username:record.get("username"),
                truename:record.get("truename"),
                email:record.get("email"),
                department:record.get("department"),
                major:record.get("major"),
                college:record.get("college"),
                phone:record.get("phone"),
                homephone:record.get("homephone"),
                idNumber:record.get("idNumber"),
                birthDate:(record.get('birthDate'))?(new Date(record.get('birthDate'))).pattern("yyyy-MM-dd")+" 00:00:00":"",
                sex:record.get("sex"),
                race:record.get("race"),
                politicalStatus:record.get("politicalStatus"),
                enrollDate:(record.get('enrollDate'))?(new Date(record.get('enrollDate'))).pattern("yyyy-MM-dd")+" 00:00:00":"",
                treeId:record.get("treeId"),
                currentStatus:record.get("currentStatus")
            },
            success: function () {
                Ext.Msg.alert('保存成功!');
            }
        });
    },


    onConfirm: function (choice) {
        if (choice === 'yes') {
            //
        }
    }
});
