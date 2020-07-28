package com.user



import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import com.jacob.activeX.ActiveXComponent
import com.jacob.com.ComThread
import com.jacob.com.Dispatch
import com.jacob.com.Variant


//这是自动生成的controller

import com.user.Department
import grails.gorm.transactions.Transactional
import groovy.sql.Sql

import org.grails.web.json.JSONObject

import java.awt.image.BufferedImage
import java.sql.Blob
import java.text.SimpleDateFormat
import static org.springframework.http.HttpStatus.*


@Transactional(readOnly = true)
class NoticeController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    String dep_tree_id = ""//点击树状菜单上的某个单位的tree_id
    def scaffold = Notice;
    
    def springSecurityService
    //def grailsApplication //注入是为了直接执行原始的SQL语句def dataSource = grailsApplication.mainContext.getBean('dataSource')
    //全文搜索
    def search = {
        if ((params.q) == "") {//显示所有单位
            String query1 = "from Notice"
            String jsonStr, query
            query = "from Notice"
            if (params.sort != null) {
                jsonStr = params.sort
                jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
                JSONObject obj = new JSONObject(jsonStr);
                params.sort = obj.get('property')
                params.order = obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true
            }
            params.offset = params.start ? params.start as int : 0
            params.max = params.limit ? params.limit as int : 4
            def list1
           /* if (dep_tree_id == '1000') {
                query = query //全部单位信息
            } else {
                if (dep_tree_id.length() <= 6) {
                    //   query = query + " where (substring(treeId, 1,3)='" + dep_tree_id + "') "//各区全部单位信息
                    query = query + " where substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'"
//各区或分类全部单位信息
                } else {
                    // query = query + " where substring(treeId, 1,(LENGTH(treeId)-3))='" + dep_tree_id + "'"//正常查询
                    query = query + " where treeId='" + dep_tree_id + "'"//正常查询
                }
            }*/
            int emp_size = (Notice.findAll(query)).size()
            query1 = new String(query)
            if (params.sort != null) query1 = query1 + " order by " + params.sort
            if (params.order != null) query1 = query1 + " " + params.order
            if (params.sort != null) {
                Class<?> demo = Notice.class;
                def f = demo.getDeclaredFields();
                for (int i = 0; i < f.size(); i++) {
                    if (f[i].getName() == "constraints") {
                        list1 = Notice.findAll(query, [max: params.max, offset: params.offset]);
                        break
                    }
                    if (f[i].getName() == params.sort) {
                        list1 = Notice.findAll(query1, [max: params.max, offset: params.offset])
                        break
                    }
                }
            }
            render(contentType: "text/json") {
                notices  list1.collect(){
                    [
                            id:it?.id,title:it?.title,
                                     titleCode:it?.titleCode,
                                     jb:it?.jb,
                                     dep:it?.dep,
                                     scr:it?.scr,
                                     scDate:it?.scDate?new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.scDate):"",
                                    wjlx:it?.wjlx
                    ]
                }
                totalCount  emp_size.toString()
                //totalCount = Notice.count().toString()
            }
        } else {
            params.max = new Integer(params.limit);
//在Notice全表中查询,Notice.search(String query,[offset:XX,max:XX])要重新定义max、offset
            params.offset = new Integer(params.start);
            params.q = params.q.trim()
            try {
                //def list2 = (Notice.search(params.q, params)).results//参数params缺省则默认 params.max=10
                def list2 = Notice.search(params.q, [offset: 0, max: Notice.count()]).results
//参数params缺省则默认 params.max=10//escape:true,用了带属性的查询失败！！！ //def listmap = employeejgyfgzs.search(params.q,[escape: true, offset: 0, max: employeejgyfgzs.count()])//参数params缺省则默认 params.max=10//escape:true,用了带属性的查询失败！！！
                ArrayList <Notice> list1 = new ArrayList<Notice>();
                ArrayList <Notice> list3 = new ArrayList<Notice>();
                for (int i = 0; i < list2.size; i++) {
                   // if ((dep_tree_id == '1000') || (dep_tree_id == (((Notice) list2[i] ).treeId ).substring(0, dep_tree_id.length()) ) ) {
                        list1.add(list2[i])
                  //  }
                }
                for (int i = params.offset; i < list1.size; i++) {
                    list3.add(list1[i])
                    if (i > (params.offset + params.max - 2)) break
                }
                render(contentType: "text/json") {
                    notices   list3.collect(){
                        [
                                id:it?.id,title:it?.title,
                                            titleCode:it?.titleCode,
                                            jb:it?.jb,
                                            dep:it?.dep,
                                            scr:it?.scr,
                                            scDate:it?.scDate?new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.scDate):"",
                                            wjlx:it?.wjlx
                        ]
                    }
                    totalCount   list1.size
                    //totalCount = list.total
                }
            } catch (e) {
                render(contentType: "text/json") {
                    notices   null
                    totalCount   0
                }
            };
        }
    }

//获取详细有上级单位关系的单位名称及本级单位的详细信息
      /*  def readNoticeByTreeId = {
            dep_tree_id = params.dep_tree_id
            render "success"
            return
        }*/

//获取详细有上级单位关系的单位名称
        def readNoticeById = {
            Notice noticeInstance = Notice.findById(params.id)
            render(contentType: "text/json") {
                notices   noticeInstance
            }
            return
        }
//NoticeStore.js的初始值，
        def readNotice= {
            ////println("params.dep_tree_id=====readNotice========="+params.dep_tree_id)
            Calendar NoticeDate
            int NoticeDateYear,NoticeDateMonth,NoticeDateDay
            if(!params.displaydate)params.displaydate="month"
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(params.currentNoticeDate){
                NoticeDate = Calendar.getInstance();
                NoticeDate.setTime(sdf.parse(params.currentNoticeDate + " 00:00:00"));
                NoticeDateYear = NoticeDate.get(Calendar.YEAR);//获取年份
                NoticeDateMonth = NoticeDate.get(Calendar.MONTH);//获取月份
                NoticeDateDay =  NoticeDate.get(Calendar.DAY_OF_MONTH);//获取日
            }
            String query="from Notice where 1=1";
            if (params.currentNoticeDate) {
                if(params.displaydate=="year"){query = query + " and YEAR(scDate)='" + NoticeDateYear + "'" }
                if(params.displaydate=="month"){query = query +" and YEAR(scDate)='" + NoticeDateYear +  "' and MONTH(scDate)='" + (NoticeDateMonth + 1) + "'" }
                if(params.displaydate=="day"){query = query + " and YEAR(scDate)='" + NoticeDateYear + "' and MONTH(scDate)='" + (NoticeDateMonth + 1) + "' and DAY(scDate)='" + NoticeDateDay + "'" }
            }

            /*if (dep_tree_id == '1000') {
                query = query //全部单位信息
                if(params.currentNoticeDate!= null)query = query +" where MONTH(buyDate)='"+(NoticeDateMonth+1)+"' and YEAR(buyDate)='"+NoticeDateYear+"'"
            } else {
                if (dep_tree_id.length() <= 6) {
                    //   query = query + " where (substring(treeId, 1,3)='" + dep_tree_id + "') "//各区全部单位信息
                    query = query + " where substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'"
//各区或分类全部单位信息
                } else {
                    // query = query + " where substring(treeId, 1,(LENGTH(treeId)-3))='" + dep_tree_id + "'"//正常查询
                    query = query + " where treeId='" + dep_tree_id + "'"//正常查询
                }
                if(params.currentNoticeDate!= null)query = query +" and MONTH(buyDate)='"+(NoticeDateMonth+1)+"' and YEAR(buyDate)='"+NoticeDateYear+"'"
            }*/
            //println("query====="+query)
            //query = query + " where treeId='" + dep_tree_id + "'"
            String jsonStr
            if (params.sort != null) {
                jsonStr = params.sort
                jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
                JSONObject obj = new JSONObject(jsonStr);
                params.sort = obj.get('property')
                params.order = obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true
            }
            params.offset = params.start ? params.start as int : 0
            params.max = params.limit ? params.limit as int : 4
            def list1
            int emp_size = (Notice.findAll(query)).size()
            String query1 = new String(query)
            if (params.sort != null) query1 = query1 + " order by " + params.sort
            if (params.order != null) query1 = query1 + " " + params.order
            /*//防止访问其它Store时再访问此Store时，ext自动后传的Params是前一个Store的数据
            if (params.sort != null) {
                Class<?> demo = Notice.class;
                def f = demo.getDeclaredFields();
                for (int i = 0; i < f.size(); i++) {
                    if (f[i].getName() == "constraints") {
                        list1 = Notice.findAll(query, [max: params.max, offset: params.offset]);
                        break
                    }
                    if (f[i].getName() == params.sort) {
                        list1 = Notice.findAll(query1, [max: params.max, offset: params.offset])
                        break
                    }
                }
            }*/
            list1 = Notice.findAll(query1, [max: params.max, offset: params.offset])
            render(contentType: "text/json") {
                notices   list1.collect(){
                    [
                                        id:it?.id,title:it?.title,
                                        titleCode:it?.titleCode,
                                        jb:it?.jb,
                                        dep:it?.dep,
                                        scr:it?.scr,
                                        scDate:it?.scDate?new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.scDate):"",
                                        wjlx:it?.wjlx
                    ]
                }
                totalCount   emp_size.toString()
                // totalCount = Notice.count().toString()
            }
        }


        @Transactional
        def save() {
           // params.treeId = dep_tree_id//dep_tree_id是这个类的静态变量，记录点击树状菜单上的某个单位的tree_id，
            params.titleCode= java.util.UUID.randomUUID().toString().replaceAll("-", "");//Java中产生唯一码的功能
            def noticeInstance = new Notice(params)
            if (noticeInstance == null) {
                notFound()
                return
            }
            if (noticeInstance.hasErrors()) {
                respond noticeInstance.errors, view: 'create'
                return
            }
            try {
                Notice i
                i = noticeInstance.save()//返回当前保存的记录
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
        }


        @Transactional
        def update() {
            Notice noticeInstance = Notice.findById(params.id)
            noticeInstance.setProperties(params)
            if (noticeInstance == null) {
                notFound()
                return
            }
            if (noticeInstance.hasErrors()) {
                return
            }
            try {
                Notice i
                i = noticeInstance.save(flush:true)//返回当前保存的记录
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
        }
//删除选定的一条数据
        @Transactional
        def delete() {
            def noticeInstance = Notice.get(params.id)
            /*def filePath = getServletContext().getRealPath("/") + "notice\\";
            File[] files = (new File(filePath)).listFiles();//取notice下的所有文件
            for (int i = 0; i < files.length; i++) {
                String filename = files[i].getName();//取notice下的第i个文件名
                if (filename.indexOf(noticeInstance.titleCode) != -1) {
                    files[i].delete();
                }
            }
            if (noticeInstance == null) {
                notFound()
                return
            }*/
            try {
                Notice i
                i = noticeInstance.delete()//返回当前保存的记录
                if (i == null) {
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
        }

//删除指定单位的所有数据
    /*    @Transactional
  def  deleteAllNotice() throws Exception {
     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     Calendar NoticeDate = Calendar.getInstance();
     NoticeDate.setTime(sdf.parse(params.currentNoticeDate + " 00:00:00"));
     int NoticeDateYear = NoticeDate.get(Calendar.YEAR);//获取年份
     int NoticeDateMonth = NoticeDate.get(Calendar.MONTH);//获取月份
     def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
     def sql = new Sql(dataSource)
     String  query
     if(dep_tree_id.length()<=6){
             query = "Delete FROM  Notice  where substring(tree_id, 1,LENGTH('"+dep_tree_id+"'))='" + dep_tree_id + "'"//各区或分类全部单位信息
     }else{
             query = "Delete FROM  Notice  where tree_id='" + dep_tree_id + "'"//正常查询
     }
    try {
         sql.execute(query)
         render "success"//删除成功
         return
    } catch (e) {
         render "failure"//删除失败
         return
    }
 }*/
//删除指定单位选定日期的所有数据
    /*@Transactional
    def  deleteMonthNotice() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar NoticeDate = Calendar.getInstance();
        NoticeDate.setTime(sdf.parse(params.currentNoticeDate + " 00:00:00"));
        int NoticeDateYear = NoticeDate.get(Calendar.YEAR);//获取年份
        int NoticeDateMonth = NoticeDate.get(Calendar.MONTH);//获取月份
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String  query
        if(dep_tree_id.length()<=6){
                query = "Delete FROM  Notice  where substring(tree_id, 1,LENGTH('"+dep_tree_id+"'))='" + dep_tree_id + "' and MONTH(buy_date)='" + (NoticeDateMonth + 1) + "' and  YEAR(buy_date)='" + NoticeDateYear+"'"//选定单位信息
        }else{
                query = "Delete FROM  Notice  where tree_id='" + dep_tree_id +"' and MONTH(buy_date)='" + (NoticeDateMonth + 1) + "' and  YEAR(buy_date)='" + NoticeDateYear+"'"//正常查询
        }
        try {
            sql.execute(query)
            render "success"//删除成功
            return
        } catch (e) {
            render "failure"//删除失败
            return
        }
    }*/

    @Transactional
    def checkFile() throws Exception {
        ////println("params.recId===="+params.recId)
        def noticeInstance = Notice.get(params.recId)
        String src=noticeInstance.titleCode+"_"+noticeInstance.title+"."+noticeInstance.wjlx;//文件名
        def filePath = getServletContext().getRealPath("/") + "notice\\";
        File[] files = (new File(filePath)).listFiles();//取notice下的所有文件
        for (int i = 0; i < files.length; i++) {
                String filename = files[i].getName();//取notice下的第i个文件名
                if (filename.indexOf(src) != -1) {
                    render "true"// //println("文件存在！！！")
                    return
                }
        }
        render "false" // //println("文件不存在！！！")
        return
    }

    @Transactional
    def importNotice1() throws Exception {
       // //println("params.recId===="+params.recId)
        def noticeInstance = Notice.get(params.recId)
        def f = request.getFile('noticefilePath')
        String fileName
        def filePath
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            noticeInstance.title=fileName.split('\\.')[0];  //特殊分隔符时前加\\
            noticeInstance.wjlx=fileName.split('\\.')[1];  //特殊分隔符时前加\\
            noticeInstance.scr=User.get(springSecurityService.principal.id).truename
            noticeInstance.dep=User.get(springSecurityService.principal.id).department=="全部"?"资产管理处":User.get(springSecurityService.principal.id).department
            filePath = getServletContext().getRealPath("/") + "notice\\";
            File[] files = (new File(filePath)).listFiles();//取notice下的所有文件
            for (int i = 0; i < files.length; i++) {
                String filename = files[i].getName();//取notice下的第i个文件名
                if (filename.indexOf(noticeInstance.titleCode) != -1) {
                    files[i].delete();
                }
            }
            try{
                //if ((fileName.toLowerCase().endsWith(".doc"))||(fileName.toLowerCase().endsWith(".ppt"))||(fileName.toLowerCase().endsWith(".xls"))||(fileName.toLowerCase().endsWith(".pdf")) ) {
                if(1){
                    f.transferTo(new File(filePath +noticeInstance.titleCode+"_"+ fileName))//把客户端上选的文件上传到服务器
                    noticeInstance.save()//返回当前保存的记录
               } else {
                    //println("")
                    render "{success:true,info:'错误！上传文件类型不对'}"//render "typeError"//导入的Excel文件类型不对
                    return
                }
            }catch (e){
                render "{success:true,info:'错误！文件上传失败'}"//render "typeError"//导入的Excel文件类型不对
                return
            }
        } else {
            render "{success:true,info:'错误！导入的Excel文件不存在'}" //render "failure"//导入的Excel文件不存在
            return
        }
        render "{success:true,info:'文件上传成功'}"
    }

    //导入《Notice通知》上传文件，非PDF文件转换为PDF文件。
    @Transactional
    def importNotice() {
        int WORD2HTML = 8;// 8 代表word保存成html
        int WD2PDF = 17; // 17代表word保存成pdf
        int PPT2PDF = 32;
        int XLS2PDF = 0;
        def noticeInstance = Notice.get(params.recId)
        def f = request.getFile('noticefilePath')
        def fileName
        def filePath
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            String[] file1 = ((fileName.toLowerCase()).toString()).split("\\.")
            String fileType = file1[file1.length - 1]  //取文件名的扩展名（不含.）
            filePath = getServletContext().getRealPath("/") + "photoD\\"            ////println("filePath======================="+filePath)
            try {
                if (!(new File(filePath).isDirectory())) {
                    new File(filePath).mkdir();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            String originfile =filePath + params.titleCode +  fileType
            String pdffile=filePath + params.titleCode + ".pdf"
            f.transferTo(new File(originfile))//上传的文件先存入临时文件
            byte[] data = null;
            FileInputStream inputStream = null;
            FileOutputStream outputStream = null;
            switch (fileType) {
                case "jpg"://jpg转换为pdf
                    try {
                        inputStream = new FileInputStream(new File(originfile));
                        outputStream = new FileOutputStream(new File(pdffile));
                        Document document = new Document();
                        PdfWriter.getInstance(document, outputStream)
                        Image image = Image.getInstance(originfile);
                        float imageHeight = image.getScaledHeight();
                        float imageWidth = image.getScaledWidth();
                        int i = 0;
                        //缩小图片文件
                        while (imageHeight > 500 || imageWidth > 500) {
                            image.scalePercent(100 - i);
                            i++;
                            imageHeight = image.getScaledHeight();
                            imageWidth = image.getScaledWidth();
                        }
                        image.setAlignment(Image.ALIGN_CENTER);
                        document.open();
                        document.add(image);
                        document.close();
                        outputStream.flush()
                        outputStream.close();
                        inputStream.close();
                        inputStream = new FileInputStream(new File(pdffile));
                        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];//大小只影响转换速度
                        int numBytesRead = 0;
                        while ((numBytesRead = inputStream.read(buf)) != -1) {
                            outputStream1.write(buf, 0, numBytesRead);
                        }
                        data = outputStream1.toByteArray()
                        outputStream1.close();
                        inputStream.close();
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                    break
                case "pdf":
                    try {
                        inputStream = new FileInputStream(new File(originfile));
                        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];//大小只影响转换速度
                        int numBytesRead = 0;
                        while ((numBytesRead = inputStream.read(buf)) != -1) {
                            outputStream1.write(buf, 0, numBytesRead);
                        }
                        data = outputStream1.toByteArray()
                        outputStream1.close();
                        inputStream.close();
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                    break
                case "doc":
                case "docx"://Docx文件转换为PDF文件
                    ActiveXComponent app = null;
                    try {
                        ComThread.InitSTA();
                        app = new ActiveXComponent("Word.Application");
                        app.setProperty("Visible", false);    // 设置word应用程序不可见// app.setProperty("Visible", new Variant(false));  // documents表示word程序的所有文档窗口，（word是多文档应用程序??
                        Dispatch docs = app.getProperty("Documents").toDispatch(); // 打开要转换的word文件
                        Dispatch doc = Dispatch.call(docs,"Open",originfile,  false, true).toDispatch();
                        Dispatch.call(doc, "ExportAsFixedFormat", pdffile, WD2PDF);
                        Dispatch.call(doc, "Close", false); // 关闭word文件
                        inputStream = new FileInputStream(new File(pdffile));
                        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];//大小只影响转换速度
                        int numBytesRead = 0;
                        while ((numBytesRead = inputStream.read(buf)) != -1) {
                            outputStream1.write(buf, 0, numBytesRead);
                        }
                        data = outputStream1.toByteArray()
                        outputStream1.close();
                        inputStream.close();
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                    break
                case "xls":
                case "xlsx"://Excel文件转换为PDF文件
                    ActiveXComponent app = null;
                    try {
                        ComThread.InitSTA(true);
                        app = new ActiveXComponent("Excel.Application");
                        app.setProperty("Visible", false);
                        app.setProperty("AutomationSecurity", new Variant(3));//禁用宏
                        Dispatch excels = app.getProperty("Workbooks").toDispatch();
                        /*Dispatch excel = Dispatch.invoke(excels, "Open", Dispatch.Method, new Object[]{
                                excelfile,
                                new Variant(false),
                                new Variant(false),
                        },new int[9]).toDispatch();*/
                        Dispatch excel = Dispatch.call(excels, "Open",originfile,false,true).toDispatch();
                        //转换格式ExportAsFixedFormat
                        /*Dispatch.invoke(excel, "ExportAsFixedFormat", Dispatch.Method, new Object[]{
                                new Variant(0),//pdf格式=0
                                pdffile,
                                new Variant(0)//0=标准(生成的pdf图片不会变模糊) 1=最小文件(生成的pdf图片模糊的一塌糊涂)
                        }, new int[1]);*/
                        Dispatch.call(excel, "ExportAsFixedFormat",XLS2PDF,pdffile);
                        Dispatch.call(excel, "Close", false);
                        if(app!=null){
                            app.invoke("Quit");
                            app=null;
                        }
                        inputStream = new FileInputStream(new File(pdffile));
                        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];//大小只影响转换速度
                        int numBytesRead = 0;
                        while ((numBytesRead = inputStream.read(buf)) != -1) {
                            outputStream1.write(buf, 0, numBytesRead);
                        }
                        data = outputStream1.toByteArray()
                        outputStream1.close();
                        inputStream.close();
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                    break
                case "ppt":
                case "pptx"://Excel文件转换为PDF文件
                    ActiveXComponent app = null;
                    try {
                        ComThread.InitSTA();
                        app = new ActiveXComponent("PowerPoint.Application");
                        //app.setProperty("Visible", false);//不知烦什么不能隐藏app.setProperty("Visible", false);Windows的PPT（Presentations）
                        Dispatch files = app.getProperty("Presentations").toDispatch();
                        Dispatch file = Dispatch.call(files, "open", originfile, true, true,false).toDispatch();//可以隐藏Windows的PPT（Presentations）
                        //Dispatch file = Dispatch.call(files, "open", pptfile, false, true).toDispatch();//不知烦什么不能隐藏app.setProperty("Visible", false);Windows的PPT（Presentations）
                        Dispatch.call(file, "SaveAs", pdffile, PPT2PDF);
                        Dispatch.call(file,"Close");
                        inputStream = new FileInputStream(new File(pdffile));
                        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];//大小只影响转换速度
                        int numBytesRead = 0;
                        while ((numBytesRead = inputStream.read(buf)) != -1) {
                            outputStream1.write(buf, 0, numBytesRead);
                        }
                        data = outputStream1.toByteArray()
                        outputStream1.close();
                        inputStream.close();
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                    break
                default:
                    //其它非jpg\doc\xls\pdf文件，不能转换为PDF文件，直接存入。
                    try {
                        inputStream = new FileInputStream(new File(originfile));
                        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];//大小只影响转换速度
                        int numBytesRead = 0;
                        while ((numBytesRead = inputStream.read(buf)) != -1) {
                            outputStream1.write(buf, 0, numBytesRead);
                        }
                        data = outputStream1.toByteArray()
                        outputStream1.close();
                        inputStream.close();
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                    noticeInstance.setTitleCode(params.titleCode)
                    noticeInstance.setTitle(fileName.split('\\.')[0])  //特殊分隔符时前加\\
                    //noticeInstance.wjlx=fileName.split('\\.')[1];  //特殊分隔符时前加\\
                    noticeInstance.setWjlx(fileType)
                    noticeInstance.setScr(User.get(springSecurityService.principal.id).truename)
                    noticeInstance.setDep(User.get(springSecurityService.principal.id).department=="全部"?"市人社局":User.get(springSecurityService.principal.id).department)
                    noticeInstance.setWjnr(new javax.sql.rowset.serial.SerialBlob(data))
                    noticeInstance.setScDate(new Date())
                    noticeInstance.save(flush: true)
                    File filep = new File(originfile);//删除临时文件
                    filep.delete();
                    filep = new File(pdffile);//删除临时文件
                    filep.delete();
                    render "{success:true,info:'导入《"+fileName.split('\\.')[0]+"》文件成功！'}"
                    return

                   /* File filep = new File(originfile);//删除临时文件
                    filep.delete();
                    render "{success:false,info:'错误！导入的"+params.ghzcWjmc+"文件类型不合适！'}" //render "failure"//导入的Excel文件不存在
                    return*/
            }
            noticeInstance.setTitleCode(params.titleCode)
            noticeInstance.setTitle(fileName.split('\\.')[0])  //特殊分隔符时前加\\
            //noticeInstance.wjlx=fileName.split('\\.')[1];  //特殊分隔符时前加\\
            noticeInstance.setWjlx("pdf")
            noticeInstance.setScr(User.get(springSecurityService.principal.id).truename)
            noticeInstance.setDep(User.get(springSecurityService.principal.id).department=="全部"?"市人社局":User.get(springSecurityService.principal.id).department)

            //noticeInstance.setGhzcName(params.ghzcName)

            //noticeInstance.setWjmc(params.ghzcWjmc)
            noticeInstance.setWjnr(new javax.sql.rowset.serial.SerialBlob(data))
            noticeInstance.setScDate(new Date())
           // noticeInstance.setGhzcWjshzt("未完成")
            noticeInstance.save(flush: true)

            File filep = new File(originfile);//删除临时文件
            filep.delete();
            filep = new File(pdffile);//删除临时文件
            filep.delete();
        } else {
            render "{success:false,info:'错误！导入的"+fileName.split('\\.')[0]+"文件不存在！'}" //render "failure"//导入的Excel文件不存在
            return
        }
        render "{success:true,info:'导入《"+fileName.split('\\.')[0]+"》文件成功！'}"
        return
    }

    //从数据库调出Notice文件
    def displayNotice() {
        //src:'/kzyghMis/notice/displayNotice?recId='+rec.get('id')+'&title='+rec.get('title') +'&time='+ new Date()//
        def noticeInstance = Notice.get(params.recId)
        String username = User.get(springSecurityService.principal.id).username
        String truename = User.get(springSecurityService.principal.id).truename
        String chineseAuthority = User.get(springSecurityService.principal.id).chineseAuthority
        String query
        //是管理者或是自己的项目,如已上传直接显示，否则必须是完成审核通过的文件才能显示。
        //if(params.ghzcFzrName==username||chineseAuthority=="管理员"||chineseAuthority=="科研管理员"||chineseAuthority=="科研负责人"){//是管理者或是自己的项目
       // query = "from Notice where ghzcCode='" + params.ghzcCode + "' and ghzcWjmc='" + params.ghzcWjmc + "' "
        //}else{
        //     query = "from GhzcDetail where ghzcCode='" + params.ghzcCode + "' and ghzcWjmc='" + params.ghzcWjmc + "' and ghzcWjshzt='已经完成审核'"
        // }
       // //println("query====="+query)
        OutputStream outputStream ;
        InputStream inputStream = null
        BufferedImage ghzcDetailSbsImage;
        String fileType
       // GhzcDetail ghzcDetailInstance = GhzcDetail.findAll(query)[0]
        if (noticeInstance && noticeInstance.getWjnr()) {
            ////println("yes")
            Blob blob = noticeInstance.getWjnr();
            inputStream = blob.getBinaryStream();
            fileType = noticeInstance.getWjlx()
            switch (fileType) {
                case "jpg":
                    /*从数据库中读出JPG文件以JPG文件显示
                    outputStream = response.getOutputStream();
                    ghzcDetailSbsImage = ImageIO.read(inputStream);
                    ImageIO.write(ghzcDetailSbsImage, "JPEG", response.getOutputStream());
                    outputStream.flush()
                    outputStream.close();
                    inputStream.close();
                    break*/
                case "pdf":
                    ////println("pdf++++++++++++++++++++++++++")
                    outputStream = response.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                    response.addHeader("content-type", "application/pdf");
                    outputStream.flush()
                    outputStream.close();
                    inputStream.close();
                    break
                case "doc":
                case "docx":
                    /*//从数据库中读出WODRD文件以PDF文件显示
                     outputStream = response.getOutputStream();
                     //def fileNameIn = getServletContext().getRealPath("/") + "/photoD/xxx.docx"
                     // def fileNameOut = getServletContext().getRealPath("/") + "/photoD/xxx.pdf"
                     //InputStream is = new FileInputStream(new File(fileNameIn));
                     //OutputStream os = new java.io.FileOutputStream(fileNameOut);
                     WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(inputStream);
                     Mapper fontMapper = new IdentityPlusMapper();
                     fontMapper.put("宋体", PhysicalFonts.get("SimSun"));
                     fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
                     fontMapper.put("微软雅黑", PhysicalFonts.get("Microsoft Yahei"));
                     fontMapper.put("黑体", PhysicalFonts.get("SimHei"));
                     fontMapper.put("楷体", PhysicalFonts.get("KaiTi"));
                     fontMapper.put("新宋体", PhysicalFonts.get("NSimSun"));
                     fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
                     fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
                     fontMapper.put("宋体扩展", PhysicalFonts.get("simsun-extB"));
                     fontMapper.put("仿宋", PhysicalFonts.get("FangSong"));
                     fontMapper.put("仿宋_GB2312", PhysicalFonts.get("FangSong_GB2312"));
                     fontMapper.put("幼圆", PhysicalFonts.get("YouYuan"));
                     fontMapper.put("华文宋体", PhysicalFonts.get("STSong"));
                     fontMapper.put("华文中宋", PhysicalFonts.get("STZhongsong"));
                     wordMLPackage.setFontMapper(fontMapper);
                     //FOSettings foSettings = Docx4J.createFOSettings();//也可以
                     //foSettings.setWmlPackage(wordMLPackage); ////foSettings.setApacheFopMime("images/pdf")
                     //Docx4J.toFO(foSettings, outputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);
                     Docx4J.toPDF(wordMLPackage, outputStream);
                     response.addHeader("content-type", "application/pdf");
                     outputStream.flush()
                     outputStream.close();
                     inputStream.close();
                     break*/
                case "xls":
                case "xlsx":
                    break
                default:
                    //从数据库取出数据非文件，然后下载到本地
                    String filePath = getServletContext().getRealPath("/") + "tmp\\"
                    String filename=noticeInstance.getTitle()
                    OutputStream outputStream1 = new FileOutputStream(new File(filePath+filename+"."+fileType));
                    byte[] buffer = new byte[1024];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream1.write(buffer, 0, len);
                    }
                    outputStream1.flush()
                    outputStream1.close();
                    inputStream.close();
                    render filename+"."+fileType
                    return
            /*try {
                // InputStream is = new FileInputStream(new File(docxPath));
                 WordprocessingMLPackage mlPackage = WordprocessingMLPackage.load(inStream);
                 Mapper fontMapper = new IdentityPlusMapper();
                 fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
                 fontMapper.put("宋体",PhysicalFonts.get("SimSun"));
                 fontMapper.put("微软雅黑",PhysicalFonts.get("Microsoft Yahei"));
                 fontMapper.put("黑体",PhysicalFonts.get("SimHei"));
                 fontMapper.put("楷体",PhysicalFonts.get("KaiTi"));
                 fontMapper.put("新宋体",PhysicalFonts.get("NSimSun"));
                 fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
                 fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
                 fontMapper.put("宋体扩展",PhysicalFonts.get("simsun-extB"));
                 fontMapper.put("仿宋",PhysicalFonts.get("FangSong"));
                 fontMapper.put("仿宋_GB2312",PhysicalFonts.get("FangSong_GB2312"));
                 fontMapper.put("幼圆",PhysicalFonts.get("YouYuan"));
                 fontMapper.put("华文宋体",PhysicalFonts.get("STSong"));
                 fontMapper.put("华文中宋",PhysicalFonts.get("STZhongsong"));

                 mlPackage.setFontMapper(fontMapper);

                // os = new java.io.FileOutputStream(pdfPath);

                 FOSettings foSettings = Docx4J.createFOSettings();
                 foSettings.setWmlPackage(mlPackage);
                 Docx4J.toFO(foSettings, outputStream, Docx4J.FLAG_EXPORT_PREFER_XSL);

                response.addHeader("content-type", "application/pdf");
                outputStream.flush()
                outputStream.close();
                inStream.close();
                break
             }catch(Exception ex){
                 ex.printStackTrace();
             }finally {
                 IOUtils.closeQuietly(outputStream);
             }*/
            }

        } else {
            ////println("no")return
            //还没有上传文件
            /* //默认显示一个PDF文件（xxx.pdf）
             inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\" + "xxx.pdf"));
             outputStream = response.getOutputStream();
             byte[] buffer = new byte[1024];
             int len = -1;
             while ((len = inputStream.read(buffer)) != -1) {
                 outputStream.write(buffer, 0, len);
             }
             response.addHeader("content-type", "application/pdf");
             outputStream.flush()
             outputStream.close();
             inputStream.close();*/
            String str = "《"+params.title+"》文件还没有上传或！请等待上传....";
            byte[] buffer = str.getBytes("utf-8")
            outputStream = response.getOutputStream();
            response.setContentType("text/html;charset=UTF-8");
            outputStream.write(buffer);
            outputStream.flush()
            outputStream.close();
        }
    }
    /*@Transactional
      def importNotice1() throws Exception {
           def f = request.getFile(initcap3("Notice")+"filePath")
           def fileName, filePath
           if (!f.empty) {
               fileName = f.getOriginalFilename()
               filePath = getServletContext().getRealPath("/") + "notice\\"+params.titleCode+File.separator;
               //println("filePath====="+filePath)
               File dirFile = new File(filePath);//建目录
               if (!dirFile.exists()) {
                   dirFile.mkdir();
               }

                   File[] files = dirFile.listFiles()
                   for(int i=0;i<files.length;i++) {
                       //删除文件
                       //println("files[i]==000=="+files[i].absolutePath)
                       File file = new File(files[i].absolutePath);
                       file.delete()
                       //println("files[i]==111=="+files[i].absolutePath)
                   }
               try{
                  if ((fileName.toLowerCase().endsWith(".doc"))||(fileName.toLowerCase().endsWith(".ppt"))||(fileName.toLowerCase().endsWith(".xls"))||(fileName.toLowerCase().endsWith(".pdf")) ) {
                      try {

                          ////println("===="+filePath + params.titleCode+"*.*")
                         // java.io.File myDelFile = new java.io.File(filePath + params.titleCode+"*.*");
                          //myDelFile.delete();
                      }
                      catch (Exception e) {
                         System.out.//println("删除文件操作出错");
                          //e.printStackTrace();
                      }
                      //println("===ssssssssssss======"+filePath + fileName)
                      f.transferTo(new File(filePath + fileName))//把客户端上选的文件上传到服务器
                      //println("===ssssssssssss======"+filePath + fileName)
                  } else {
                      render "{success:false,info:''错误！上传文件类型不对'}"//render "typeError"//导入的Excel文件类型不对
                      return
                  }
               }catch (e){
                   render "{success:false,info:''错误！文件上传失败'}"//render "typeError"//导入的Excel文件类型不对
                   return
               }
           } else {
               render "{success:false,info:''错误！导入的Excel文件不存在'}" //render "failure"//导入的Excel文件不存在
               return
           }
           render "{success:true,info:'文件上传成功'}"
       }*/

    /*  @Transactional
def selectExportNotice() throws Exception {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Calendar NoticeDate = Calendar.getInstance();
      NoticeDate.setTime(sdf.parse(params.currentNoticeDate + " 00:00:00"));
      int NoticeDateYear = NoticeDate.get(Calendar.YEAR);//获取年份
      int NoticeDateMonth = NoticeDate.get(Calendar.MONTH);//获取月份
      String[] fieldItemName = new String[params.fieldItem.size()]
      String query
      query = "from DynamicTable where tableNameId='"+initcap3("Notice")+"'"
      def list1 = DynamicTable.findAll(query);
      String tablename_cn = (String)list1[0].tableName
      tablename_cn = tablename_cn.substring(0,tablename_cn.length() - 1)//取中文表名
      def filePath = getServletContext().getRealPath("/") + "/tmp/"
      FileInputStream templatefin = new FileInputStream(filePath + tablename_cn+"模板.xls")   //用模板文件构造poi
      POIFSFileSystem templatefs = new POIFSFileSystem(templatefin);
      HSSFWorkbook templatewb = new HSSFWorkbook(templatefs); //创建模板工作表
      HSSFSheet templateSheet = templatewb.getSheetAt(0);//直接取模板文件第1个sheet对象
      int templateColumns = templateSheet.getRow((short) 1).getPhysicalNumberOfCells();//得到模板文件第1个sheet的总列数
      HSSFRow templateRow = templateSheet.getRow(1);
      //得到模板文件第1个sheet的第二行对象（标题行）（getRow(0)、getRow(1)、getRow(2)分别表示第一行、第二行、第三行....）为了得到模板样式
      HSSFCellStyle[] templatestyleArray = new HSSFCellStyle[templateColumns];//创建标题样式数组
      Integer[] templateColumnWidth = new Integer[templateColumns];//创建列宽数组
      for (int s = 0; s < templateColumns; s++) {  //一次性创建所有列的样式放在数组里
          templatestyleArray[s] = templateRow.getCell((short) s).getCellStyle() //样式数组实例获得标题每列的样式
      }
      HSSFRow templateRow1 = templateSheet.getRow(2);
      //得到模板文件第1个sheet的第三行对象（第一行数据）（getRow(0)、getRow(1)、getRow(2)分别表示第一行、第二行、第三行....）为了得到模板样式
      HSSFCellStyle[] templatestyleArray1 = new HSSFCellStyle[templateColumns];//创建数据样式数组
      for (int s = 0; s < templateColumns; s++) {  //一次性创建所有列的样式放在数组里
          templateColumnWidth[s] = templateSheet.getColumnWidth(s)//获得数据每列的宽度
          templatestyleArray1[s] = templateRow1.getCell((short) s).getCellStyle() //样式数组实例获得数据每列的样式
      }
      //取得中文字段名
      for (int i = 0; i < params.fieldItem.size(); i++) {
          for (int j = 0; j < list1.size(); j++) {
              //if (params.fieldItem[i].equals(list1[j].fieldNameId)) {
              if (((params.fieldItem[i].toString()).trim()).equals(list1[j].fieldNameId.trim())) {
                  fieldItemName[i] = list1[j].fieldName.trim()// params.fieldItem是字段名，fieldItemName是中文字段名  //
                  break
              }
          }
      }
      String outputFile = filePath + tablename_cn+ NoticeDateYear + "年" + (NoticeDateMonth + 1 ) +"月.xls"
      Class<?> demo
      def field
      HSSFWorkbook workwb = new HSSFWorkbook(); //创建工作表
      HSSFSheet workSheet = workwb.createSheet(tablename_cn);//创建Excel工作表对象
      int columns = params.fieldItem.size();//设置sheet的总列数
      ArrayList dataList
      demo = Notice.class;
      field = demo.getDeclaredFields();
      query = "from Notice where MONTH(buyDate)=" + (NoticeDateMonth + 1 ) +" and  YEAR(buyDate)=" + NoticeDateYear + " and    substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'))" +" order by "+initcap3("Notice")+"Code Asc";
      //query = query + " and ((employeeCode!='汇总'  and   substring(treeId, 1," + dep_tree_id.length() + ")='" + dep_tree_id + "')";
      // query = query + " or (employeeCode='汇总'  and   treeId='" + dep_tree_id + "'))" + " order by employeeCode Asc";
      dataList = Notice.findAll(query)
      HSSFCellStyle style = workwb.createCellStyle()//表头样式
      HSSFCellStyle style1 = workwb.createCellStyle()//数据样式
      HSSFFont font = workwb.createFont();//数据字体
      HSSFRow hssfRow = workSheet.createRow(0);//增加标题行
      HSSFCell cell0 = hssfRow.createCell((short) 0);
      style.cloneStyleFrom(templatestyleArray[0])//取标题样式
      cell0.setCellStyle(style);//设置标题样式
      cell0.setCellValue("tablename_cn");
      workSheet.addMergedRegion(new Region(0, (short) 0, 0, (short) (columns - 1)));//指定合并区域（行，列）
      HSSFCellUtil.setAlignment(cell0, workwb, HSSFCellStyle.ALIGN_CENTER);

      String department
      hssfRow = workSheet.createRow(1);//增加表头
      for (int columnId = 0; columnId < columns; columnId++) {
          HSSFCell cell = hssfRow.createCell((short) columnId);
          for (int i = 0; i < field.size(); i++) {
              //if (field[i].getName().equals(params.fieldItem[columnId])) {
              if ((field[i].getName().trim()).equals((params.fieldItem[columnId].toString()).trim())) {
                  style.cloneStyleFrom(templatestyleArray[i])//取表头样式
                  workSheet.setColumnWidth(columnId, templateColumnWidth[i])//表头每列宽度
                  cell.setCellStyle(style);//设置表头样式
                  cell.setCellValue(fieldItemName[columnId].trim())
                  break
              }
          }
      }
      for (int rowId = 2; rowId < dataList.size() + 2; rowId++) { //定位行，循环对每一个单元格进行赋值
          def valueList = dataList[rowId - 2];  //依次取第rowId行数据   每一个数据是valueList
          hssfRow = workSheet.createRow(rowId);   //创建新的rowId行
          for (int columnId = 0; columnId < columns; columnId++) {
              HSSFCell cell = hssfRow.createCell((short) columnId);   //创建新的rowId行   columnId列   单元格对象
              for (int i = 0; i < field.size(); i++) {
                  //if (field[i].getName().equals(params.fieldItem[columnId])) {
                  if ((field[i].getName().trim()).equals((params.fieldItem[columnId].toString()).trim())) {
                      font.setFontName("宋体");
                      font.setFontHeightInPoints((short) 14);
// 设置字体大小    // style1.cloneStyleFrom(templatestyleArray1[i]) //取出数据的样式style ,模板columnId列的样式//数据量大时，导出的Excel文件有异常，会进入保护模式
                      style1.setFont(font)
                      cell.setCellStyle(style1);//设置数据样式
                      if (field[i].getType() == (new Date()).getClass()) {//如果是日期型数据
                          cell.setCellValue(valueList[(params.fieldItem[columnId].toString()).trim()] ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(valueList[(params.fieldItem[columnId].toString()).trim()]) : "")
                      } else {
                          if (field[i].getName().trim() == "treeId") {//如果导出列为treeId则转换为department
                              department = Department.findAll("from Department where treeId='" + valueList.treeId + "'")[0].department
//取treeId对应的Ddepartment
                              cell.setCellValue(department)//最后一列是treeId，换成对应的Ddepartment
                              break
                          }
                          if (field[i].getType() == java.lang.String) {//如果是字符型数据
                              cell.setCellValue(valueList[(params.fieldItem[columnId].toString()).trim()] ? valueList[(params.fieldItem[columnId].toString()).trim()] : "");
                          } else {
                              cell.setCellValue(valueList[(params.fieldItem[columnId].toString()).trim()] ? valueList[(params.fieldItem[columnId].toString()).trim()] : 0.0);
                          }
                      }
                      break
                  }
              }
          }
      }
      FileOutputStream fOut = new FileOutputStream(outputFile); //设置输出流
      workwb.write(fOut); //将模板的内容写到输出文件上
      fOut.flush();
      fOut.close(); //操作结束，关闭文件
      render tablename_cn + NoticeDateYear +"年" + (NoticeDateMonth +1 ) +"月"
  }*/




    /*  @Transactional
def batchDelete() {
       String query = "from DynamicTable where tableNameId='" + initcap3("Notice") + "'"
       def list1 = DynamicTable.findAll(query);
       query = "from Notice where " + list1[0].fieldNameId + "='" + params.newvalue1 + "'"
       def listd = Notice.findAll(query);
       try {
           for (int i = 0; i < listd.size(); i++) {
               listd[i].delete()
           }
           render "success"
           return
       } catch (e) {
           render "failure"
           return
       }
   }*/

    //把grails的字段名制成Mysql的字段名
    String fieldNameChang(fieldName) {
        String cc, dd
        char c
        for (int iii = 0; iii < fieldName.length(); iii++) {
            c = fieldName.charAt(iii);
            if (Character.isUpperCase(c)) {
                cc = c
                dd = (char) (c + 32)
                fieldName = fieldName.replaceAll(cc, "_" + dd)
            }
        }
        return fieldName
    }

    def initcap2(String str) {//不带路径不带盘符的名字第一个字母改为大写
        int i = 0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0] - 32);
        return new String(ch);
    }

    def initcap3(String str) {//不带路径不带盘符的名字第一个字母改为小写
        int i = 0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'A' && ch[0] <= 'Z') ch[0] = (char) (ch[0] + 32);
        return new String(ch);
    }
}