package com.app;

class ProcessDiagram {
    String title;//流程名称
    String titleCode;//流程名称
    String jb;//流程级别
    String dep;//流程发布部门
    String scr;//流程上传人
    Date scDate;//流程上传日期
    String wjlx;//流程文件类型
    String lczt;//流程文件状态
static constraints ={
    title(blank:true,nullable:true);
    titleCode(blank:true,nullable:true);
    jb(blank:true,nullable:true);
    dep(blank:true,nullable:true);
    scr(blank:true,nullable:true);
    scDate(blank:true,nullable:true);
    wjlx(blank:true,nullable:true);
    lczt(blank:true,nullable:true);
}
static searchable=true;//可以全文搜索
}

