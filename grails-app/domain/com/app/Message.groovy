package com.app

import java.sql.Blob;

class Message {
    String sendId;//发送Id
    String sendName;//发送姓名
    String receiveId;//接收Id
    String receiveName;//接收姓名
    String content;//发送内容
    Date messageDate;//发送信息日期
    String fileName;//上传文件名称
    Blob fileContent;//上传文件内容
    String messageGroup;//群
    String receiveStatus;//当前状态
static constraints ={
    sendId(blank:true,nullable:true);
    sendName(blank:true,nullable:true);
    receiveId(blank:true,nullable:true);
    receiveName(blank:true,nullable:true);
    content(blank:true,nullable:true);
    messageDate(blank:true,nullable:true);
    fileName(blank:true,nullable:true);
    fileContent(blank:true,nullable:true);
    messageGroup(blank:true,nullable:true);
    receiveStatus(blank:true,nullable:true);
}
}

