package com.app;
class EmployeeNdkh{
    String employeeCode;//职工编号
    String khnd;//考核年度
    String khjg;//考核结果
    String khyj;//考核意见
    String wcjkhyy;//未参加考核原因
    String bz;//备注
static constraints ={
    employeeCode(blank:true,nullable:true);
    khnd(blank:true,nullable:true);
    khjg(blank:true,nullable:true);
    khyj(blank:true,nullable:true);
    wcjkhyy(blank:true,nullable:true);
    bz(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

