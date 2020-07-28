package com.app;
import java.sql.Blob;
class EmployeeSb{
    String employeeCode;//职工编号
    String name;//姓名
    String idNumber;//身份证号
    String employeeSbh;//社保号
    Date adjustDate;//调整日期
    Double employeeSbjs = 0.0;//社保基数
    Double employeeYanglaoPart = 0.0;//个人养老比例
    Double employeeShiyiePart = 0.0;//个人失业比例
    Double employeeYiliaoPart = 0.0;//个人医疗比例
    Double enterpriseYanglaoPart = 0.0;//企业养老比例
    Double enterpriseShiyiePart = 0.0;//企业失业比例
    Double enterpriseYiliaoPart = 0.0;//企业医疗比例
    Double enterpriseGongshangPart = 0.0;//企业工伤比例
    Double enterpriseshengyuPart = 0.0;//企业生育比例
    Double enterpriseBuchongYiliaoPart = 0.0;//企业补充医疗比例
    String treeId;//单位编码
static constraints ={
    employeeCode(blank:true,nullable:true);
    name(blank:true,nullable:true);
    idNumber(blank:true,nullable:true);
    employeeSbh(blank:true,nullable:true);
    adjustDate(blank:true,nullable:true);
    employeeSbjs(blank:true,nullable:true);
    employeeYanglaoPart(blank:true,nullable:true);
    employeeShiyiePart(blank:true,nullable:true);
    employeeYiliaoPart(blank:true,nullable:true);
    enterpriseYanglaoPart(blank:true,nullable:true);
    enterpriseShiyiePart(blank:true,nullable:true);
    enterpriseYiliaoPart(blank:true,nullable:true);
    enterpriseGongshangPart(blank:true,nullable:true);
    enterpriseshengyuPart(blank:true,nullable:true);
    enterpriseBuchongYiliaoPart(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

