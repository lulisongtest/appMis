package com.app;
import java.sql.Blob;
class Employee{
    String employeeCode;//职工编号
    String name;//姓名
    String idNumber;//身份证
    Date birthDate;//出生日期
    String sex;//性别
    String race;//族别
    String politicalStatus;//政治面貌
    Date workDate;//参加工作日期
    Date thatworkDate;//认定后参加工作日期
    String zylb;//专业类别
    String jblq;//艰边类区
    String sshy;//所属行业
    String dagzzd;//档案工资类型
    String xxgzzd;//现行工资类型
    String szbm;//所在部门
    String treeId;//单位码
    Blob photo;//照片
    String currentStatus;//当前状态
static constraints ={
    employeeCode(blank:true,nullable:true);
    name(blank:true,nullable:true);
    idNumber(blank:true,nullable:true);
    birthDate(blank:true,nullable:true);
    sex(blank:true,nullable:true);
    race(blank:true,nullable:true);
    politicalStatus(blank:true,nullable:true);
    workDate(blank:true,nullable:true);
    thatworkDate(blank:true,nullable:true);
    zylb(blank:true,nullable:true);
    jblq(blank:true,nullable:true);
    sshy(blank:true,nullable:true);
    dagzzd(blank:true,nullable:true);
    xxgzzd(blank:true,nullable:true);
    szbm(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
    photo(blank:true,nullable:true);
    currentStatus(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

