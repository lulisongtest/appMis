package com.app;
class EmployeeXl{
    String employeeCode;//职工编号
    Date graduateDate;//毕业日期
    String xl;//学历
    String xw;//学位
    String yxmc;//院校名称
    String xlxz;//学历性质
    String zy;//专业
    String bz;//备注
static constraints ={
    employeeCode(blank:true,nullable:true);
    graduateDate(blank:true,nullable:true);
    xl(blank:true,nullable:true);
    xw(blank:true,nullable:true);
    yxmc(blank:true,nullable:true);
    xlxz(blank:true,nullable:true);
    zy(blank:true,nullable:true);
    bz(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

