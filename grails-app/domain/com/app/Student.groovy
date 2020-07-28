package com.app;
import java.sql.Blob;
class Student{
    String username;//用户名
    String truename;//真实姓名
    String email;//电子邮件
    String department;//班级
    String major;//专业
    String college;//院系
    String phone;//本人联系电话
    String homephone;//家长联系电话
    String idNumber;//身份证号
    Date birthDate;//出生日期
    String sex;//性别
    String race;//族别
    String politicalStatus;//政治面貌
    Date enrollDate;//入学日期
    String treeId;//单位码
    Blob photo;//照片
    String currentStatus;//当前状态
static constraints ={
    username(blank:true,nullable:true);
    truename(blank:true,nullable:true);
    email(blank:true,nullable:true);
    department(blank:true,nullable:true);
    major(blank:true,nullable:true);
    college(blank:true,nullable:true);
    phone(blank:true,nullable:true);
    homephone(blank:true,nullable:true);
    idNumber(blank:true,nullable:true);
    birthDate(blank:true,nullable:true);
    sex(blank:true,nullable:true);
    race(blank:true,nullable:true);
    politicalStatus(blank:true,nullable:true);
    enrollDate(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
    photo(blank:true,nullable:true);
    currentStatus(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

