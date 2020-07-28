package com.user

import java.sql.Blob;
class Rules{
    String title;//标题
    String titleCode;//标题编码
    String jb;//级别
    String dep;//制定部门
    String scr;//上传人
    Date scDate;//上传日期
    String wjlx;//文件类型
    Blob wjnr;//文件内容
    static constraints ={
        title(blank:true,nullable:true);
        titleCode(blank:true,nullable:true);
        jb(blank:true,nullable:true);
        dep(blank:true,nullable:true);
        scr(blank:true,nullable:true);
        scDate(blank:true,nullable:true);
        wjlx(blank:true,nullable:true);
        wjnr(blank:true,nullable:true);
    }
    static searchable=true;//可以全文搜索
}

