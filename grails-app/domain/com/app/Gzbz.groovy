package com.app
class Gzbz {
    String gzzd="公务员类";
    String rylx="公务员";
    String xsdy="办事员";
    String xsdysm="";
    String xmmc="职务工资";
    Double bz=0.00;
    static constraints = {
        gzzd blank:false
        rylx blank:false
        xsdy blank:false
        xsdysm (blank:true,nullable:true);
        xmmc blank:false
        bz (scale:2)
    }
    static searchable=true;//可以全文搜索

}
