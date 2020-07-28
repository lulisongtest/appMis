package com.app;
class EmployeeGanYfgz{
    String employeeCode;//职工编号
    String name;//姓名
    Date gzDate;//工资日期
    Double zwgwgz = 0.0;//职务工资
    Double jbxjgz = 0.0;//级别工资
    Double gdgz = 0.0;//高定工资
    Double shxbt = 0.0;//生活性补贴
    Double gzxjt = 0.0;//工作性津贴
    Double jbtjse = 0.0;//津补贴减少额
    Double bldqbt = 0.0;//保留地区补贴
    Double bldqbtbl = 0.0;//保留地区补贴保留
    Double jkbydqjt = 0.0;//艰苦边远地区津贴
    Double bjf = 0.0;//保健费
    Double gjjt = 0.0;//干警津贴,改为特殊岗位津贴
    Double jjjt = 0.0;//纪检津贴
    Double xfjt = 0.0;//信访津贴
    Double fgjt = 0.0;//法官津贴
    Double sjjt = 0.0;//审计津贴
    Double blgjce = 0.0;//保留岗级差额
    Double gzbt = 0.0;//工作补贴//注释
    Double ybjt = 0.0;//夜班津贴
    Double jbjdgz = 0.0;//加班加点工资
    Double nrjxdhgz = 0.0;//纳入绩效的活工资
    Double zxdfdgz = 0.0;//中学的浮动工资
    Double wsfyjt = 0.0;//卫生防疫津贴
    Double jhjt = 0.0;//教护津贴
    Double cksf = 0.0;//超课时费
    Double nzycxjj = 0.0;//年终一次性奖金
    Double jlgz = 0.0;//奖励工资
    Double zfgjj = 0.0;//住房公积金
    Double zfbt = 0.0;//住房补贴
    Double tzbt = 0.0;//提租补贴
    Double qnf = 0.0;//取暧费
    Double wyglf = 0.0;//物业管理费
    Double gcbt = 0.0;//公车补贴
    Double sxbjtbt = 0.0;//上下班交通补贴
    Double txfbt = 0.0;//通讯费补贴
    Double qtbt = 0.0;//其它补贴
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
    shxbt(blank:true,nullable:true);
    gzxjt(blank:true,nullable:true);
    jbtjse(blank:true,nullable:true);
    bldqbt(blank:true,nullable:true);
    bldqbtbl(blank:true,nullable:true);
    jkbydqjt(blank:true,nullable:true);
    bjf(blank:true,nullable:true);
    gjjt(blank:true,nullable:true);
    jjjt(blank:true,nullable:true);
    xfjt(blank:true,nullable:true);
    fgjt(blank:true,nullable:true);
    sjjt(blank:true,nullable:true);
    blgjce(blank:true,nullable:true);
    gzbt(blank:true,nullable:true);
    ybjt(blank:true,nullable:true);
    jbjdgz(blank:true,nullable:true);
    nrjxdhgz(blank:true,nullable:true);
    zxdfdgz(blank:true,nullable:true);
    wsfyjt(blank:true,nullable:true);
    jhjt(blank:true,nullable:true);
    cksf(blank:true,nullable:true);
    nzycxjj(blank:true,nullable:true);
    jlgz(blank:true,nullable:true);
    zfgjj(blank:true,nullable:true);
    zfbt(blank:true,nullable:true);
    tzbt(blank:true,nullable:true);
    qnf(blank:true,nullable:true);
    wyglf(blank:true,nullable:true);
    gcbt(blank:true,nullable:true);
    sxbjtbt(blank:true,nullable:true);
    txfbt(blank:true,nullable:true);
    qtbt(blank:true,nullable:true);
    bfgz(blank:true,nullable:true);
    wwgzbt(blank:true,nullable:true);
    wwgzbt1(blank:true,nullable:true);
    wwgzbt2(blank:true,nullable:true);
    xxgzzd(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

