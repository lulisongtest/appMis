

/*
Wed Feb 26 00:17:19 CST 2020陆立松自动创建
//可以使用的参数
${className}=Teacher
${fullName}=com.app.Teacher
${packageName}=com.app
${packagePath}=com\app
${propertyName}=teacher
${lowerCaseName}=teacher
${domain}=[com.app.Teacher, 教师基本信息, String, username, 用户名, String, truename, 真实姓名, String, email, 电子邮件, String, department, 单位, String, major, 专业, String, college, 院系, String, phone, 本人联系电话, String, homephone, 家长联系电话, String, idNumber, 身份证号, Date, birthDate, 出生日期, String, sex, 性别, String, race, 族别, String, politicalStatus, 政治面貌, Date, workDate, 工作日期, String, treeId, 单位码, Blob, photo, 照片, String, currentStatus, 当前状态]
//字段数==教师基本信息
*/

Ext.define('appMis.model.TeacherModel', {
    extend: 'Ext.data.Model',
    requires: [
        'Ext.data.Field'
    ],
    fields: ['id','username','truename','email','department','major','college','phone','homephone','idNumber',
        { name:'birthDate',dateFormat:'Y-m-d',type:'date'},'sex','race','politicalStatus',
        { name:'workDate',dateFormat:'Y-m-d',type:'date'},'treeId','currentStatus',
    ]
});

