package com.app;
class EmployeeGzll{
   String employeeCode;//职工编号
    Date startDate;//起始日期
    String gzzd;//工资类型
    String rylx;//人员类型
    String zwzcjsdj;//职务/职称/技术等级
    String xsdy;//享受待遇
    String xsdysm;//享受待遇说明
    Date endDate;//终止日期
    String jz;//是否为双肩挑人员
    String ygzdw;//原工作单位
    String rzfs;//入职方式
    String bdyy;//变动原因
    String zmr;//证明人
    String shra;//审核人A
    String shrb;//审核人B
    String shrc;//审核人C
    String shrd;//审核人D
    String bz;//备注
static constraints ={
    employeeCode(blank:true,nullable:true);
    startDate(blank:true,nullable:true);
    gzzd(blank:true,nullable:true);
    rylx(blank:true,nullable:true);
    zwzcjsdj(blank:true,nullable:true);
    xsdy(blank:true,nullable:true);
    xsdysm(blank:true,nullable:true);
    endDate(blank:true,nullable:true);
    jz(blank:true,nullable:true);
    ygzdw(blank:true,nullable:true);
    rzfs(blank:true,nullable:true);
    bdyy(blank:true,nullable:true);
    zmr(blank:true,nullable:true);
    shra(blank:true,nullable:true);
    shrb(blank:true,nullable:true);
    shrc(blank:true,nullable:true);
    shrd(blank:true,nullable:true);
    bz(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

