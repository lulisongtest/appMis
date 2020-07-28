package com.app;
class EmployeeJhxx{
    String employeeCode;//职工编号
    Date startDate;//起始日期
    String gzzd;//工资类型
    String rylx;//人员类型
    String xsdy;//享受待遇
    String xsdysm;//享受待遇说明
    String xmmc;//项目名称
    Date endDate;//终止日期
    String bz;//备注
static constraints ={
    employeeCode(blank:true,nullable:true);
    startDate(blank:true,nullable:true);
    gzzd(blank:true,nullable:true);
    rylx(blank:true,nullable:true);
    xsdy(blank:true,nullable:true);
    xsdysm(blank:true,nullable:true);
    xmmc(blank:true,nullable:true);
    endDate(blank:true,nullable:true);
    bz(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

