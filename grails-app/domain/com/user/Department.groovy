package com.user;
class Department{
    String department;//单位名称
    String dwjc;//单位全称
    Integer dwtjsl = 0;//单位统计数量
    String gzzescbm;//工资总额手册编码
    String departmentCode;//组织机构代码
    String lsgx;//隶属关系
    String lsxt;//隶属系统
    String xzhq;//行政划区
    String dwxz;//单位性质
    String czbkxs;//财政拨款形式
    String jblq;//艰边类区
    String dwjb;//单位级别
    String sshy;//所属行业
    String cgqk;//垂管情况
    String zgbmqk;//主管部门情况
    String sjgzzgbm;//上级工资主管部门
    String bzqk;//编制情况
    String bzpwqk;//编制批文情况
    Integer bzs = 0;//编制数
    Integer syrs = 0;//实用人数
    String hsxz;//核算性质
    String leader;//单位负责人
    String contacts;//工资经办人
    String phone;//经办人联系电话
    String fax;//传真号码
    String postcode;//邮政编码
    String address;//单位地址
    String treeId;//单位码
    String glyph;//单位图码
static constraints ={
    department(blank:true,nullable:true);
    dwjc(blank:true,nullable:true);
    dwtjsl(blank:true,nullable:true);
    gzzescbm(blank:true,nullable:true);
    departmentCode(blank:true,nullable:true);
    lsgx(blank:true,nullable:true);
    lsxt(blank:true,nullable:true);
    xzhq(blank:true,nullable:true);
    dwxz(blank:true,nullable:true);
    czbkxs(blank:true,nullable:true);
    jblq(blank:true,nullable:true);
    dwjb(blank:true,nullable:true);
    sshy(blank:true,nullable:true);
    cgqk(blank:true,nullable:true);
    zgbmqk(blank:true,nullable:true);
    sjgzzgbm(blank:true,nullable:true);
    bzqk(blank:true,nullable:true);
    bzpwqk(blank:true,nullable:true);
    bzs(blank:true,nullable:true);
    syrs(blank:true,nullable:true);
    hsxz(blank:true,nullable:true);
    leader(blank:true,nullable:true);
    contacts(blank:true,nullable:true);
    phone(blank:true,nullable:true);
    fax(blank:true,nullable:true);
    postcode(blank:true,nullable:true);
    address(blank:true,nullable:true);
    treeId(blank:true,nullable:true);
    glyph(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

