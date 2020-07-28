package com.app;
class EmployeeGztz{
    String employeeCode;//职工编号
    String gzbdyy;//工资变动原因
    Date startDate;//起始日期
    Date declareDate;//申报日期
    String gzzd;//工资类型
    String rylx;//人员类型
    String zwgw;//享受待遇
    String zwsm;//享受待遇说明
    String jbxj;//级别/薪级
    String jbdc;//级别档次/岗位档次
    Double zwgwgz = 0.0;//职务/岗位/技术等级(工资)
    Double jbxjgz = 0.0;//级别/薪级/工人岗位(工资)
    Date xcjjjxDate;//下次晋级/晋薪时间
    Date xcjdDate;//下次晋档时间
    String shra;//审核人A
    String shrb;//审核人B
    String shrc;//审核人C
    String shrd;//审核人D
    String bz;//备注
static constraints ={
    employeeCode(blank:true,nullable:true);
    gzbdyy(blank:true,nullable:true);
    startDate(blank:true,nullable:true);
    declareDate(blank:true,nullable:true);
    gzzd(blank:true,nullable:true);
    rylx(blank:true,nullable:true);
    zwgw(blank:true,nullable:true);
    zwsm(blank:true,nullable:true);
    jbxj(blank:true,nullable:true);
    jbdc(blank:true,nullable:true);
    zwgwgz(blank:true,nullable:true);
    jbxjgz(blank:true,nullable:true);
    xcjjjxDate(blank:true,nullable:true);
    xcjdDate(blank:true,nullable:true);
    shra(blank:true,nullable:true);
    shrb(blank:true,nullable:true);
    shrc(blank:true,nullable:true);
    shrd(blank:true,nullable:true);
    bz(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

