package com.app;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

public class EmployeeRydwbdVar implements Serializable {
    private static final long serialVersionUID = 361866001729020143L;
    private String employeeCode;//职工编号
    private String name;//姓名
    private Date bdsqDate;//变动申请日期
    private String employeeDlwh;//调令文号
    //private Blob employeeDlwhContent;//调令内容
    private String ygzdw;//原工作单位
    private String drgzdw;//调入工作单位
    private String bdyy;//变动原因
    private String shra;//审核人A
    private String shrb;//审核人B
    private String shrc;//审核人C
    private String shrd;//审核人D
    private String shre;//审核人E
    private String shrf;//审核人F
    private String shrg;//审核人G
    private Date bdzxDate;//变动执行日期
    private String bz;//备注
    //private Blob sbContent;//申报文件内容
    private String treeId;//单位码

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBdsqDate() {
        return bdsqDate;
    }

    public void setBdsqDate(Date bdsqDate) {
        this.bdsqDate = bdsqDate;
    }

    public String getEmployeeDlwh() {
        return employeeDlwh;
    }

    public void setEmployeeDlwh(String employeeDlwh) {
        this.employeeDlwh = employeeDlwh;
    }



    public String getYgzdw() {
        return ygzdw;
    }

    public void setYgzdw(String ygzdw) {
        this.ygzdw = ygzdw;
    }

    public String getDrgzdw() {
        return drgzdw;
    }

    public void setDrgzdw(String drgzdw) {
        this.drgzdw = drgzdw;
    }

    public String getBdyy() {
        return bdyy;
    }

    public void setBdyy(String bdyy) {
        this.bdyy = bdyy;
    }

    public String getShra() {
        return shra;
    }

    public void setShra(String shra) {
        this.shra = shra;
    }

    public String getShrb() {
        return shrb;
    }

    public void setShrb(String shrb) {
        this.shrb = shrb;
    }

    public String getShrc() {
        return shrc;
    }

    public void setShrc(String shrc) {
        this.shrc = shrc;
    }

    public String getShrd() {
        return shrd;
    }

    public void setShrd(String shrd) {
        this.shrd = shrd;
    }

    public String getShre() {
        return shre;
    }

    public void setShre(String shre) {
        this.shre = shre;
    }

    public String getShrf() {
        return shrf;
    }

    public void setShrf(String shrf) {
        this.shrf = shrf;
    }

    public String getShrg() {
        return shrg;
    }

    public void setShrg(String shrg) {
        this.shrg = shrg;
    }

    public Date getBdzxDate() {
        return bdzxDate;
    }

    public void setBdzxDate(Date bdzxDate) {
        this.bdzxDate = bdzxDate;
    }



    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }



    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }


}
