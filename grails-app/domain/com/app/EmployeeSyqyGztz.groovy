package com.app;
class EmployeeSyqyGztz{
    String employeeCode;//职工编号
    String gzbdyy;//工资变动原因
    Date startDate;//起始日期
    Date declareDate;//申报日期
    String gzzd;//工资类型
    String rylx;//人员类型
    String zwgw;//享受待遇
    String dcgj;//档次/岗级
    Double zwgwgz = 0.0;//岗位工资
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
    dcgj(blank:true,nullable:true);
    zwgwgz(blank:true,nullable:true);
    xcjdDate(blank:true,nullable:true);
    shra(blank:true,nullable:true);
    shrb(blank:true,nullable:true);
    shrc(blank:true,nullable:true);
    shrd(blank:true,nullable:true);
    bz(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

