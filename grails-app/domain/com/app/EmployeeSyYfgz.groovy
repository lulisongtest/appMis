package com.app;
class EmployeeSyYfgz{
    String employeeCode;//职工编号
    String name;//姓名
    Date gzDate;//工资日期
    Double zwgwgz = 0.0;//职务工资
    Double jbxjgz = 0.0;//级别工资
    Double gdgz = 0.0;//高定工资
    Double shxbthgzxjt70 = 0.0;//生活性补贴和工作性津贴70%
    Double blgjce = 0.0;//保留岗级差额
    Double bldqbt = 0.0;//保留地区补贴
    Double bldqbtblbf = 0.0;//保留地区补贴保留部分
    Double shxbt = 0.0;//生活性补贴
    Double gwxjt = 0.0;//岗位性津贴
    Double bzrjt = 0.0;//班主任津贴
    Double shxbthgzxjt30 = 0.0;//生活性补贴和工作性津贴30%
    Double yykhff = 0.0;//月预考核发放
    Double xqkhff = 0.0;//学期考核发放
    Double ndkhff = 0.0;//年度考核发放
    Double nrjxdhgz = 0.0;//纳入绩效的活工资
    Double hzjxgz = 0.0;//核增绩效工资
    Double ynzycxjj = 0.0;//原年终一次性奖金
    Double ynzycxjjble = 0.0;//原年终一次性奖金保留额
    Double jxgzjse = 0.0;//绩效工资减少额
    Double jkbydqjt = 0.0;//艰苦边远地区津贴
    Double jktzjt = 0.0;//艰苦台站津贴
    Double ywzyjt = 0.0;//野外作业津贴，改为特殊岗位津贴
    Double wsjt = 0.0;//卫生津贴
    Double gkzyjt = 0.0;//高空作业津贴
    Double jhljt = 0.0;//教护龄津贴
    Double rcjt = 0.0;//人才津贴
    Double tjjsjt = 0.0;//特级教师津贴
    Double ysjt = 0.0;//院士津贴
    Double zftsjt = 0.0;//政府特殊津贴
    Double bjf = 0.0;//保健费
    Double gzbt = 0.0;//工作补贴
    Double blgjce1 = 0.0;//保留岗级差额1
    Double ybjt = 0.0;//夜班津贴
    Double jbjdgz = 0.0;//加班加点工资
    Double nrjxdhgz1 = 0.0;//纳入绩效的活工资1
    Double zxdfdgz = 0.0;//中学的浮动工资
    Double wsfyjt = 0.0;//卫生防疫津贴
    Double jhjt = 0.0;//教护津贴
    Double cksf = 0.0;//超课时费
    Double zfgjj = 0.0;//住房公积金
    Double zfbt = 0.0;//住房补贴
    Double tzbt = 0.0;//提租补贴
    Double qnf = 0.0;//取暧费
    Double wyglf = 0.0;//物业管理费
    Double gcbt = 0.0;//公车补贴
    Double sxbjtbt = 0.0;//上下班交通补贴
    Double txfbt = 0.0;//通讯费补贴
    Double qtbt = 0.0;//其它补贴
    Double jlgz = 0.0;//奖励工资
    Double bfgz = 0.0;//补发工资
    Double wwgzbt;//ww工作补助
    Double wwgzbt1;//法定工作日之外加班补助
    Double wwgzbt2;//法定节假日加班补助
    String xxgzzd;//现行工资制度
    String treeId;//单位
static constraints ={
    employeeCode(blank:true,nullable:true);
    name(blank:true,nullable:true);
    gzDate(blank:true,nullable:true);
    zwgwgz(blank:true,nullable:true);
    jbxjgz(blank:true,nullable:true);
    gdgz(blank:true,nullable:true);
    shxbthgzxjt70(blank:true,nullable:true);
    blgjce(blank:true,nullable:true);
    bldqbt(blank:true,nullable:true);
    bldqbtblbf(blank:true,nullable:true);
    shxbt(blank:true,nullable:true);
    gwxjt(blank:true,nullable:true);
    bzrjt(blank:true,nullable:true);
    shxbthgzxjt30(blank:true,nullable:true);
    yykhff(blank:true,nullable:true);
    xqkhff(blank:true,nullable:true);
    ndkhff(blank:true,nullable:true);
    nrjxdhgz(blank:true,nullable:true);
    hzjxgz(blank:true,nullable:true);
    ynzycxjj(blank:true,nullable:true);
    ynzycxjjble(blank:true,nullable:true);
    jxgzjse(blank:true,nullable:true);
    jkbydqjt(blank:true,nullable:true);
    jktzjt(blank:true,nullable:true);
    ywzyjt(blank:true,nullable:true);
    wsjt(blank:true,nullable:true);
    gkzyjt(blank:true,nullable:true);
    jhljt(blank:true,nullable:true);
    rcjt(blank:true,nullable:true);
    tjjsjt(blank:true,nullable:true);
    ysjt(blank:true,nullable:true);
    zftsjt(blank:true,nullable:true);
    bjf(blank:true,nullable:true);
    gzbt(blank:true,nullable:true);
    blgjce1(blank:true,nullable:true);
    ybjt(blank:true,nullable:true);
    jbjdgz(blank:true,nullable:true);
    nrjxdhgz1(blank:true,nullable:true);
    zxdfdgz(blank:true,nullable:true);
    wsfyjt(blank:true,nullable:true);
    jhjt(blank:true,nullable:true);
    cksf(blank:true,nullable:true);
    zfgjj(blank:true,nullable:true);
    zfbt(blank:true,nullable:true);
    tzbt(blank:true,nullable:true);
    qnf(blank:true,nullable:true);
    wyglf(blank:true,nullable:true);
    gcbt(blank:true,nullable:true);
    sxbjtbt(blank:true,nullable:true);
    txfbt(blank:true,nullable:true);
    qtbt(blank:true,nullable:true);
    jlgz(blank:true,nullable:true);
    bfgz(blank:true,nullable:true);
    wwgzbt(blank:true,nullable:true);
    wwgzbt1(blank:true,nullable:true);
    wwgzbt2(blank:true,nullable:true);
    xxgzzd(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

