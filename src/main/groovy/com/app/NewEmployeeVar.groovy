//通用
package com.app

import java.sql.Blob;

class NewEmployeeVar implements Serializable {
    private static final long serialVersionUID = 212869101766070345L;
    private String department;//单位名称
    private Date sbDate;//申报日期
    private String employeeCode;//新增职工编号
    private String name;//新增职工姓名
    private String shxm;//审核项目
    private String zbr;//制表人
    private String shra;//审核人A
    private String shrb;//审核人B
    private String shrc;//审核人C
    private String shrd;//审核人D
    private String bz;//备注
    private Blob sbContent;//申报文件内容
    private String treeId;//单位码
    String getDepartment() {
        return department
    }

    void setDepartment(String department) {
        this.department = department
    }

    Date getSbDate() {
        return sbDate
    }

    void setSbDate(Date sbDate) {
        this.sbDate = sbDate
    }

    String getEmployeeCode() {
        return employeeCode
    }

    void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getShxm() {
        return shxm
    }

    void setShxm(String shxm) {
        this.shxm = shxm
    }

    String getZbr() {
        return zbr
    }

    void setZbr(String zbr) {
        this.zbr = zbr
    }

    String getShra() {
        return shra
    }

    void setShra(String shra) {
        this.shra = shra
    }

    String getShrb() {
        return shrb
    }

    void setShrb(String shrb) {
        this.shrb = shrb
    }

    String getShrc() {
        return shrc
    }

    void setShrc(String shrc) {
        this.shrc = shrc
    }

    String getShrd() {
        return shrd
    }

    void setShrd(String shrd) {
        this.shrd = shrd
    }

    String getBz() {
        return bz
    }

    void setBz(String bz) {
        this.bz = bz
    }

    Blob getSbContent() {
        return sbContent
    }

    void setSbContent(Blob sbContent) {
        this.sbContent = sbContent
    }

    String getTreeId() {
        return treeId
    }

    void setTreeId(String treeId) {
        this.treeId = treeId
    }
}

