package com.app;
class EmployeeLtryYfgz{
    String employeeCode;//职工编号
    String name;//姓名
    Date gzDate;//工资日期
    Double zwgwgz = 0.0;//职务工资
    Double gljt = 0.0;//工龄津贴
    Double jbjt = 0.0;//基本津贴
    Double dqbt = 0.0;//地区补贴
    Double bjdqbljt = 0.0;//边疆地区保留津贴
    Double blbt = 0.0;//保留补贴
    Double zjtxf = 0.0;//增加退休费
    Double txbt = 0.0;//退休补贴
    Double shbt = 0.0;//生活补贴
    Double cnbt = 0.0;//采暖补贴
    Double jsjt = 0.0;//教师津贴
    Double hsjt = 0.0;//护士津贴
    Double jisjt = 0.0;//技师津贴
    Double jxjt = 0.0;//警衔津贴
    Double tjjsjt = 0.0;//特级教师津贴
    Double txf = 0.0;//通讯费
    Double qtbt = 0.0;//其他补贴
    String treeId;//单位
static constraints ={
    employeeCode(blank:true,nullable:true);
    name(blank:true,nullable:true);
    gzDate(blank:true,nullable:true);
    zwgwgz(blank:true,nullable:true);
    gljt(blank:true,nullable:true);
    jbjt(blank:true,nullable:true);
    dqbt(blank:true,nullable:true);
    bjdqbljt(blank:true,nullable:true);
    blbt(blank:true,nullable:true);
    zjtxf(blank:true,nullable:true);
    txbt(blank:true,nullable:true);
    shbt(blank:true,nullable:true);
    cnbt(blank:true,nullable:true);
    jsjt(blank:true,nullable:true);
    hsjt(blank:true,nullable:true);
    jisjt(blank:true,nullable:true);
    jxjt(blank:true,nullable:true);
    tjjsjt(blank:true,nullable:true);
    txf(blank:true,nullable:true);
    qtbt(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

