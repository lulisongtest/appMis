package com.app;
import java.sql.Blob;
class EmployeeGjj{
    String employeeCode;//职工编号
    String name;//姓名
    String idNumber;//身份证号
    Date adjustDate;//调整日期
    Double employeeGjjjs = 0.0;//公积金基数
    Double employeePart = 0.0;//个人比例
    Double enterprisePart = 0.0;//企业比例
    String treeId;//单位编码
static constraints ={
    employeeCode(blank:true,nullable:true);
    name(blank:true,nullable:true);
    idNumber(blank:true,nullable:true);
    adjustDate(blank:true,nullable:true);
    employeeGjjjs(blank:true,nullable:true);
    employeePart(blank:true,nullable:true);
    enterprisePart(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

