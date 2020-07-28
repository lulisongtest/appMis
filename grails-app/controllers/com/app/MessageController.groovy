package com.app

import com.jacob.activeX.ActiveXComponent
import com.jacob.com.ComThread
import com.jacob.com.Dispatch
import com.jacob.com.Variant
import com.listener.SessionListener
import com.user.Department
import com.user.User
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import org.activiti.engine.ProcessEngine
import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.TaskService
import org.activiti.engine.task.Attachment
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.*
import org.grails.web.json.JSONObject
import org.springframework.web.multipart.MultipartFile

import javax.imageio.ImageIO
import javax.imageio.stream.FileImageInputStream
import javax.servlet.http.HttpSession
import java.awt.*
import java.awt.image.BufferedImage
import java.sql.Blob
import java.text.SimpleDateFormat

//import com.alibaba.fastjson.JSON;


@Transactional(readOnly = false)
class MessageController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]


    def springSecurityService
    //def grailsApplication //注入是为了直接执行原始的SQL语句def dataSource = grailsApplication.mainContext.getBean('dataSource')


    //判断新建群是否已经存在？
    @Transactional
   def readMessageGroup(){
        String messageGroup=params.messageGroup
        String receiveId=params.receiveId
        String query="from Message where messageGroup='"+messageGroup+"' OR  receiveId='"+receiveId+"'"
        def listMessageGroup=Message.findAll(query)
        if(listMessageGroup.size()==0){
            params.messageDate = new Date()
            params.content="新创建群"
            def messageInstance = new Message(params)
            try {
                Message i
                i = messageInstance.save(flush: true)//返回当前保存的记录
                if (i != null) {
                    render "success"
                    return
                } else {
                    render "failure"
                    return
                }
            } catch (e) {
                render "failure"
                return
            }
        }else{
            render "failure"
            return
        }
   }


//上传文件
    @Transactional
    def uploadFile()  {
        //println("uploadFile=====params===="+params.toString())
       // println("uploadFile=====params.sendId===="+params.sendId)
        //println("uploadFile=====params.sendName===="+params.sendName)
        //println("uploadFile=====params.messageGroup===="+params.messageGroup)
        try{
            String uploadfilename =params.name

            String uploadPath = getServletContext().getRealPath("/") + "photoD\\"+params.sendId+"\\"
             //println("uploadFile=====uploadPath===="+uploadPath)
            File up = new File(uploadPath);
            if (!up.exists()) {
                up.mkdir();
            }
            def f=request.getFile('file')
            // println("uploadFile=====f.getOriginalFilename()===="+f.getOriginalFilename());
            String originfile =uploadPath + uploadfilename
           // println("上传文件originfile===="+originfile)
            //String imagefile=uploadPath + params.employeeCode + "photo2.jpg"
            File file=new File(originfile)//file.length()是文件的大小
            f.transferTo(file)//上传的文件先存入临时文件//f.size上传文件的大小

           /* String employeeCode="",temp="";
            for(int i=0;i<str.length();i++){
                if(str.charAt(i)>=48 && str.charAt(i)<=57){
                    // employeeCode+=str.charAt(i);
                    employeeCode=(str.substring(i)).split("\\.")[0];//取照片名字后面的身份证号码
                    break;
                }
            }
            String query = "from Employee where employeeCode='" + employeeCode + "'"// println("query====="+query)
            Employee employeeInstance = Employee.findAll(query)[0]
            if (!employeeInstance) {
                return "success";//有照片，没有Employee信息
            }*/
           // String imagefile=uploadPath + "bak"+params.name//先创建一个临时文件
           // byte[] data = null;
           // FileImageInputStream input = null;
           // InputStream input1 = new FileInputStream(originfile)//原文件
           // OutputStream newoutput = new FileOutputStream(imagefile);//缩小后的文件
           // resizeImage(input1, newoutput, 170, "jpg")
            FileImageInputStream input = new FileImageInputStream(new File(originfile));//缩小后的文件存入数据库
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];//大小只影响转换速度
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            byte[] data = output.toByteArray()
            output.close();
            input.close();

            String messageGroup = params.messageGroup
            def listMessage = Message.findAll("from Message where messageGroup='"+messageGroup+"' AND content='新创建群'")//当天当前群的聊天记录
            params.receiveName=listMessage[0].receiveName
            params.receiveId=listMessage[0].receiveId

            params.messageDate = new Date()
            def messageInstance = new Message(params)
            messageInstance.setFileName(uploadfilename)
            messageInstance.setFileContent(new javax.sql.rowset.serial.SerialBlob(data))//文件导入Employee表中
            /*messageInstance.setSendId()
            messageInstance.setSendName()
            messageInstance.setReceiveId()
            messageInstance.setReceiveName()
            messageInstance.setContent()
            messageInstance.setMessageDate(new Date())
            messageInstance.setFileName()
            messageInstance.setFileContent(new javax.sql.rowset.serial.SerialBlob(data))//照片导入Employee表中
            messageInstance.setMessageGroup()
            messageInstance.setMessageGroup()*/
            messageInstance.save(flush: true)
            File filep = new File(originfile);//删除临时文件，【调试阶段暂时不删除】
            filep.delete();
        }catch (Exception e){
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

//下载文件
   def downloadFile(){
       //println("params.messageId==="+params.messageId)
       Message messageInstance=Message.findAllById(params.messageId)[0]
       String uploadPath = getServletContext().getRealPath("/") + "photoD\\"+params.loginUsername+"\\"
       String uploadFile = uploadPath+messageInstance.getFileName()
       File file = new File(uploadPath);
       if (!file.exists()) {
           file.mkdirs();
       }
       Blob blob = messageInstance.getFileContent();
       InputStream inputStream = blob.getBinaryStream();//从数据库中取出文件流

       FileOutputStream outputStream=new FileOutputStream(new File(uploadFile));
       byte[] buf = new byte[1024];//大小只影响转换速度
       int numBytesRead = 0;
       while ((numBytesRead = inputStream.read(buf)) != -1) {
           outputStream.write(buf, 0, numBytesRead);
       }
       outputStream.flush()
       outputStream.close()
       inputStream.close()

       render "success"
       return
   }


    //删除当前用户目录下的所有下载的文件,
    def deleteDownloadFile(){
        String folderPath = getServletContext().getRealPath("/") + "photoD\\"+params.loginUsername+"\\"
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
        render "success"
        return
    }
    //删除指定文件夹下所有文件    //param path 文件夹完整绝对路径
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件//递归
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }


   //读取当天消息
    def readMessage() {
        //println("=="+(new Date())+"读取当天消息")
        String sendId = params.sendId
        String messageGroup = params.messageGroup
        int dayCount=Integer.parseInt(params.dayCount)
        int messageGroupdayCount=Integer.parseInt(params.messageGroupdayCount)
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        java.util.List sessions1
        sessions1 = SessionListener.getSessions1();
       //println("sessions1.size()==="+sessions1.size().toString())
        /* for ( int j = 0; j < sessions1.size(); j++) {
            String username=((HttpSession) sessions1.get(j)).getAttribute("username")
            println("username=="+username)
            if(!username)((HttpSession) sessions1.get(j)).removeAttribute("username")
        }*/
        String query = ""
        //query="SELECT * FROM message WHERE (message_group like '%" + sendId + "%') AND (TIMESTAMPDIFF(DAY,message_date,NOW())<="+messageGroupdayCount+")  group by message_group order by message_date ASC"//先group 后order
        query="SELECT * FROM message WHERE (receive_id like '%" + sendId + "%') AND content='新创建群'   order by message_date ASC"//先group 后order
        // println(sendId+"=====>当天有登录用户所在的群列表query==="+query)
        def listMessageGroup = sql.rows(query)//当天有登录用户所在的群列表

      //*************************************************************************//
        //println(sendId+"=====>当天有登录用户所在的群列表listMessageGroup.size()==="+listMessageGroup.size())
        if(messageGroup==sendId){
            //刚打开即时通讯
             query = "from Message where (receiveId like'%" + messageGroup + "%' OR sendId like '%" + sendId + "%') AND  ((NOT(receiveStatus like '%" + messageGroup + "%')  OR ( TIMESTAMPDIFF(DAY,message_date,NOW())<="+dayCount+" ))) order by messageDate ASC"
        }else{
           //正常取聊天记录
            query = "from Message where  ( (messageGroup='" + messageGroup + "')) AND  ((NOT(receiveStatus like '%" + messageGroup + "%')  OR ( TIMESTAMPDIFF(DAY,message_date,NOW())<="+dayCount+" ))) order by messageDate ASC"
       }
      // println(sendId+"执行任务=====>111query==="+query)
        def listMessage = Message.findAll(query)//当天当前群的聊天记录
        if(messageGroup==sendId) {
            //刚打开即时通讯
            query = "UPDATE Message set receive_status=concat(receive_status"+",'_"+messageGroup+ "') where  (receive_id='" + messageGroup + "' OR send_id='"+sendId+"') AND  (NOT(receive_status like '%" + sendId + "%'))"
        }else{
            query = "UPDATE Message set receive_status=concat(receive_status"+",'_"+messageGroup+ "') where  (receive_id='" + messageGroup + "' OR send_id='"+sendId+"') AND  (NOT(receive_status like '%" + sendId + "%'))"
        }
        //println("222query==="+query)
        sql.execute(query)

       // println("query==="+query)
       // println("listMessage.size()==="+listMessage.size())
        render(contentType: "text/json") {
            messagegroups listMessageGroup.collect() {
                [
                        sendId       : it.send_id,
                        sendName     : it.send_name,
                        receiveId    : it.receive_id,
                        receiveName  : it.receive_name,
                        messageGroup : it.message_group,
                        receiveStatus: it.receive_status,
                ]
            }//当天有登录用户所在的群列表
            messages listMessage.collect() {
                [
                        id           : it.id,
                        sendId       : it.sendId,
                        sendName     : it.sendName,
                        receiveId    : it.receiveId,
                        receiveName  : it.receiveName,
                        content      : it.content,
                        messageDate  : it?.messageDate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.messageDate) : "",
                        fileName     : it.fileName,
                        messageGroup : it.messageGroup,
                        receiveStatus: it.receiveStatus,
                ]
            }//当天当前群的聊天记录
            onlineusers sessions1.collect(){
                onlineuser:  it.username
            }
            online sessions1.size().toString()//在线人数

        }


    }



    //即时通讯=>发送消息
    @Transactional
    def save() {
       /* println("即时通讯=>发送消息")
        String sendId = params.sendId
        String sendName = params.sendName
        String receiveId = params.receiveId
        String receiveName = params.receiveName
        String content = params.content
        Date messageDate = new Date()
        String fileName = params.content ? params.content : ""
        Blob fileContent;//上传文件内容
        String messageGroup = params.messageGroup
        String receiveStatus = params.receiveStatus
        println("params.content==========" + params.content)
        */
        String messageGroup = params.messageGroup
        def listMessage = Message.findAll("from Message where messageGroup='"+messageGroup+"' AND content='新创建群'")//当天当前群的聊天记录
        params.receiveName=listMessage[0].receiveName
        params.receiveId=listMessage[0].receiveId
        params.messageDate = new Date()
        def messageInstance = new Message(params)
        /* if (messageInstance == null) {
             notFound()
             return
         }
         if (messageInstance.hasErrors()) {
             respond messageInstance.errors, view: 'create'
             return
         }*/
        try {
            Message i
            i = messageInstance.save(flush: true)//返回当前保存的记录
            if (i != null) {
               // println("成功")
                render "success"
                return
            } else {
               // println("失败")
                render "failure"
                return
            }
        } catch (e) {
           // println("失败")
            render "failure"
            return
        }
    }

    //删除群，除管理员外，用户只能删除自己建立的群！！！
    @Transactional
    def deleteMessageGroup(){
       // println("群主params.sendId===="+params.sendId)
       // println("群名称params.messageGroup===="+params.messageGroup)
       // println("登录用户角色params.chineseAuthority===="+params.chineseAuthority)
        String query,query1
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        //if(params.sendId=="admin"){
        if(params.chineseAuthority=="管理员"){
             query="Delete  from message where message_group='"+params.messageGroup+"'"
             query1="from Message where messageGroup='"+params.messageGroup+"'"
        }else{
            query="Delete  from message where message_group='"+params.messageGroup+"' AND send_id='"+params.sendId+"'"
            query1="from Message where messageGroup='"+params.messageGroup+"' AND sendId='"+params.sendId+"'"
        }
        try{
            def list=Message.findAll(query1)
            if(list.size()>0){
                sql.execute(query)
                render "success"
                return
            }else{
                render "failure"
                return
            }
        }catch(e){
            render "failure"
            return
        }
    }

}
