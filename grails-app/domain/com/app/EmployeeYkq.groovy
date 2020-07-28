package com.app;
import java.sql.Blob;
class EmployeeYkq{
    String employeeCode;//职工编号
    String name;//姓名
    Date gzDate;//月考勤日期

    String a26;//26日
    String a27;//27日
    String a28;//28日
    String a29;//29日
    String a30;//30日
    String a31;//31日
    String a1;//1日
    String a2;//2日
    String a3;//3日
    String a4;//4日
    String a5;//5日
    String a6;//6日
    String a7;//7日
    String a8;//8日
    String a9;//9日
    String a10;//10日
    String a11;//11日
    String a12;//12日
    String a13;//13日
    String a14;//14日
    String a15;//15日
    String a16;//16日
    String a17;//17日
    String a18;//18日
    String a19;//19日
    String a20;//20日
    String a21;//21日
    String a22;//22日
    String a23;//23日
    String a24;//24日
    String a25;//25日
    Double cq = 0.0;//出勤合计
    Double qq = 0.0;//缺勤合计
    Double bj = 0.0;//病假合计
    Double sj = 0.0;//事假合计
    Double cj = 0.0;//产假合计
    Double tqj = 0.0;//探亲假合计
    Double hj = 0.0;//婚假合计
    Double sangj = 0.0;//丧假合计
    Double hlj = 0.0;//护理假合计
    Double xj = 0.0;//休假合计
    Double kg = 0.0;//旷工合计
    Double gs = 0.0;//工伤合计
    Double jhsyj = 0.0;//计划生育假合计
    Double wwgzbt1 = 0.0;//法定工作日之外加班
    Double wwgzbt2 = 0.0;//法定节假日天数
    Double zb = 0.0;//值班合计
    Double yb = 0.0;//夜班合计
    String treeId;//单位
    String bz;//备注
static constraints ={
    employeeCode(blank:true,nullable:true);
    name(blank:true,nullable:true);
    gzDate(blank:true,nullable:true);

    a26(blank:true,nullable:true);
    a27(blank:true,nullable:true);
    a28(blank:true,nullable:true);
    a29(blank:true,nullable:true);
    a30(blank:true,nullable:true);
    a31(blank:true,nullable:true);
    a1(blank:true,nullable:true);
    a2(blank:true,nullable:true);
    a3(blank:true,nullable:true);
    a4(blank:true,nullable:true);
    a5(blank:true,nullable:true);
    a6(blank:true,nullable:true);
    a7(blank:true,nullable:true);
    a8(blank:true,nullable:true);
    a9(blank:true,nullable:true);
    a10(blank:true,nullable:true);
    a11(blank:true,nullable:true);
    a12(blank:true,nullable:true);
    a13(blank:true,nullable:true);
    a14(blank:true,nullable:true);
    a15(blank:true,nullable:true);
    a16(blank:true,nullable:true);
    a17(blank:true,nullable:true);
    a18(blank:true,nullable:true);
    a19(blank:true,nullable:true);
    a20(blank:true,nullable:true);
    a21(blank:true,nullable:true);
    a22(blank:true,nullable:true);
    a23(blank:true,nullable:true);
    a24(blank:true,nullable:true);
    a25(blank:true,nullable:true);
     cq(blank:true,nullable:true);
     qq(blank:true,nullable:true);
     bj(blank:true,nullable:true);
     sj(blank:true,nullable:true);
     cj(blank:true,nullable:true);
     tqj(blank:true,nullable:true);
     hj(blank:true,nullable:true);
     sangj(blank:true,nullable:true);
     hlj(blank:true,nullable:true);
     xj(blank:true,nullable:true);
     kg(blank:true,nullable:true);
     gs(blank:true,nullable:true);
     jhsyj(blank:true,nullable:true);
     wwgzbt1(blank:true,nullable:true);
     wwgzbt2(blank:true,nullable:true);
     zb(blank:true,nullable:true);
     yb(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
    bz(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

