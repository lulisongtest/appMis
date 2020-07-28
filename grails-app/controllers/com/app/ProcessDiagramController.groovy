package com.app

import com.user.User
import grails.encoders.XMLEncoder
import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import org.grails.web.json.JSONObject

//这是自动生成的controller


import java.text.SimpleDateFormat

@Transactional(readOnly = false)
class ProcessDiagramController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    //static String dep_tree_id = ""//点击树状菜单上的某个单位的tree_id
    def scaffold = ProcessDiagram;
    def ProcessService
    def springSecurityService
    //def grailsApplication //注入是为了直接执行原始的SQL语句def dataSource = grailsApplication.mainContext.getBean('dataSource')
    //全文搜索
    def search = {
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : ""
        if ((params.q) == "") {//显示所有单位
            String query1 = "from ProcessDiagram"
            String jsonStr, query
            query = "from ProcessDiagram"
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
            int emp_size = (ProcessDiagram.findAll(query)).size()
            query1 = new String(query)
            if (params.sort != null) query1 = query1 + " order by " + params.sort
            if (params.order != null) query1 = query1 + " " + params.order
            if (params.sort != null) {
                Class<?> demo = ProcessDiagram.class;
                def f = demo.getDeclaredFields();
                for (int i = 0; i < f.size(); i++) {
                    if (f[i].getName() == "constraints") {
                        list1 = ProcessDiagram.findAll(query, [max: params.max, offset: params.offset]);
                        break
                    }
                    if (f[i].getName() == params.sort) {
                        list1 = ProcessDiagram.findAll(query1, [max: params.max, offset: params.offset])
                        break
                    }
                }
            }
            render(contentType: "text/json") {
                processDiagrams   list1.collect() {
                    [
                            id       : it?.id, title: it?.title,
                            titleCode: it?.titleCode,
                            jb       : it?.jb,
                            dep      : it?.dep,
                            scr      : it?.scr,
                            scDate   : it?.scDate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.scDate) : "",
                            wjlx     : it?.wjlx,
                            lczt     : it?.lczt
                    ]
                }
                totalCount   emp_size.toString()
                //totalCount = ProcessDiagram.count().toString()
            }
        } else {
            params.max = new Integer(params.limit);//在ProcessDiagram全表中查询,ProcessDiagram.search(String query,[offset:XX,max:XX])要重新定义max、offset
            params.offset = new Integer(params.start);
            params.q = params.q.trim()
            try {
                //def list2 = (ProcessDiagram.search(params.q, params)).results//参数params缺省则默认 params.max=10
                def list2 = ProcessDiagram.search(params.q, [offset: 0, max: ProcessDiagram.count()]).results//参数params缺省则默认 params.max=10//escape:true,用了带属性的查询失败！！！ //def listmap = employeejgyfgzs.search(params.q,[escape: true, offset: 0, max: employeejgyfgzs.count()])//参数params缺省则默认 params.max=10//escape:true,用了带属性的查询失败！！！
                ArrayList<ProcessDiagram> list1 = new ArrayList<ProcessDiagram>();
                ArrayList<ProcessDiagram> list3 = new ArrayList<ProcessDiagram>();
                for (int i = 0; i < list2.size; i++) {
                    // if ((dep_tree_id == '1000') || (dep_tree_id == (((ProcessDiagram) list2[i] ).treeId ).substring(0, dep_tree_id.length()) ) ) {
                    list1.add(list2[i])
                    //  }
                }
                for (int i = params.offset; i < list1.size; i++) {
                    list3.add(list1[i])
                    if (i > (params.offset + params.max - 2)) break
                }
                render(contentType: "text/json") {
                    processDiagrams   list3.collect() {
                        [
                                id       : it?.id, title: it?.title,
                                titleCode: it?.titleCode,
                                jb       : it?.jb,
                                dep      : it?.dep,
                                scr      : it?.scr,
                                scDate   : it?.scDate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.scDate) : "",
                                wjlx     : it?.wjlx,
                                lczt     : it?.lczt
                        ]
                    }
                    totalCount   list1.size
                    //totalCount = list.total
                }
            } catch (e) {
                render(contentType: "text/json") {
                    processDiagrams   null
                    totalCount   0
                }
            };
        }
    }

    @Transactional
    def readProcessDiagramById() {
        ProcessDiagram processDiagramInstance = ProcessDiagram.findById(params.id)
        render(contentType: "text/json") {
            processDiagrams   processDiagramInstance
        }
        return
    }
   //部署流程
    def deploy() {
       // println("=============部署流程==========="+params.id.size())
        def infor
        String title,filename
        def filePath = getServletContext().getRealPath("/") + "processDiagram\\"; // println("=============filePath==========="+filePath)
        for (int i = 1; i < params.id.size(); i++) {
            ProcessDiagram processDiagramInstance = ProcessDiagram.findById(params.id[i])
            title=processDiagramInstance.title
            filename=filePath+processDiagramInstance.titleCode+"_"+title+"."+processDiagramInstance.wjlx// println("====deploy=========filename==========="+filename)
            println("=============title==========="+title)
            println("=============filename==========="+filename)
            infor = ProcessService.deploymentProcessDefinition(title,filename)//部署流程：指定名字和流程图文件名
            if (infor.information == 'success') {
               // println("id===" + infor.id)
               // println("name===" + infor.name)
                processDiagramInstance.lczt=infor.id
                processDiagramInstance.save()
                /*render(contentType: "text/json") {
                    [
                            id  : infor.id,
                            name: infor.name
                    ]
                }*/
            } else {
                render "failure"
                return
            }
        }
        render "success"
        return
    }
//删除部署流程
    def delDeploy() {
        //println("=============删除部署流程==========="+params.id.size())
        def infor
       // String title,filename
        //def filePath = getServletContext().getRealPath("/") + "processDiagram\\";
       // println("=============filePath==========="+filePath)
        for (int i = 1; i < params.id.size(); i++) {
            //  println(i+"===="+params.id[i]);println(i+"===="+params.shra[i]); println(i+"===="+params.shrb[i]); println(i+"===="+params.shrc[i])
            ProcessDiagram processDiagramInstance = ProcessDiagram.findById(params.id[i])
            //title=processDiagramInstance.title
           // filename=filePath+processDiagramInstance.titleCode+"_"+title+"."+processDiagramInstance.wjlx
           // println("====deploy=========filename==========="+filename)
            if(processDiagramInstance.lczt=="未部署")continue;
            infor = ProcessService.delDeploymentProcessDefinition(processDiagramInstance.lczt)
            if (infor.information == 'success') {
               // println("id===" + infor.id)
               // println("name===" + infor.name)
                processDiagramInstance.lczt="未部署"
                processDiagramInstance.save()
                /*render(contentType: "text/json") {
                    [
                            id  : infor.id,
                            name: infor.name
                    ]
                }*/
            } else {
                render "failure"
                return
            }
        }
        render "success"
        return
    }

//启动部署流程
    def start() {
        //println("=============启动部署流程==========="+params.id.size())
        def infor
        String pedId
        for (int i = 1; i < params.id.size(); i++) {
            ProcessDiagram processDiagramInstance = ProcessDiagram.findById(params.id[i])
            pedId=processDiagramInstance.lczt

            infor = ProcessService.startProcessInstance(pedId)
            if (infor.information == 'success') {
                //println("流程实例ID instanceid===" + infor.instanceid)
               // println("流程定义ID definitionid===" + infor.definitionid)
                render(contentType: "text/json") {
                    [
                            instanceid  : infor.instanceid,
                            definitionid: infor.definitionid
                    ]
                }

            } else {
                render "failure"
            }
        }
        render "success"
        return
    }



//ProcessDiagramStore.js的初始值，读取流程图的相关信息
    @Transactional
    def readProcessDiagram() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String query = "from ProcessDiagram ";
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
        int emp_size = (ProcessDiagram.findAll(query)).size()
        //println("query====readProcessDiagram============="+query)
        String query1 = new String(query)
        if (params.sort != null) query1 = query1 + " order by " + params.sort
        if (params.order != null) query1 = query1 + " " + params.order
        list1 = ProcessDiagram.findAll(query1, [max: params.max, offset: params.offset])
        render(contentType: "text/json") {
            processDiagrams   list1.collect() {
                [
                        id       : it?.id, title: it?.title,
                        titleCode: it?.titleCode,
                        jb       : it?.jb,
                        dep      : it?.dep,
                        scr      : it?.scr,
                        scDate   : it?.scDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.scDate) : "",
                        wjlx     : it?.wjlx,
                        lczt     : it?.lczt
                ]
            }
            totalCount   emp_size.toString()
            // totalCount = ProcessDiagram.count().toString()
        }
    }


    @Transactional
    def save() {
        // params.treeId = dep_tree_id//dep_tree_id是这个类的静态变量，记录点击树状菜单上的某个单位的tree_id，
        params.titleCode = java.util.UUID.randomUUID().toString().replaceAll("-", "");//Java中产生唯一码的功能
        def processDiagramInstance = new ProcessDiagram(params)
        if (processDiagramInstance == null) {
            notFound()
            return
        }
        if (processDiagramInstance.hasErrors()) {
            respond processDiagramInstance.errors, view: 'create'
            return
        }
        try {
            ProcessDiagram i
            i = processDiagramInstance.save(flush:true)//返回当前保存的记录
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
        ProcessDiagram processDiagramInstance = ProcessDiagram.findById(params.id)
        processDiagramInstance.setProperties(params)
        if (processDiagramInstance == null) {
            notFound()
            return
        }
        if (processDiagramInstance.hasErrors()) {
            return
        }
        try {
            ProcessDiagram i
            i = processDiagramInstance.save()//返回当前保存的记录
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
        def processDiagramInstance = ProcessDiagram.get(params.id)
        def filePath = getServletContext().getRealPath("/") + "processDiagram\\";
        File[] files = (new File(filePath)).listFiles();//取processDiagram下的所有文件
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();//取processDiagram下的第i个文件名
            if (filename.indexOf(processDiagramInstance.titleCode) != -1) {
                files[i].delete();
            }
        }
        if (processDiagramInstance == null) {
            notFound()
            return
        }
        try {
            ProcessDiagram i
            i = processDiagramInstance.delete()//返回当前保存的记录
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
  def  deleteAllProcessDiagram() throws Exception {
     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     Calendar ProcessDiagramDate = Calendar.getInstance();
     ProcessDiagramDate.setTime(sdf.parse(params.currentProcessDiagramDate + " 00:00:00"));
     int ProcessDiagramDateYear = ProcessDiagramDate.get(Calendar.YEAR);//获取年份
     int ProcessDiagramDateMonth = ProcessDiagramDate.get(Calendar.MONTH);//获取月份
     def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
     def sql = new Sql(dataSource)
     String  query
     if(dep_tree_id.length()<=6){
             query = "Delete FROM  ProcessDiagram  where substring(tree_id, 1,LENGTH('"+dep_tree_id+"'))='" + dep_tree_id + "'"//各区或分类全部单位信息
     }else{
             query = "Delete FROM  ProcessDiagram  where tree_id='" + dep_tree_id + "'"//正常查询
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
    def  deleteMonthProcessDiagram() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar ProcessDiagramDate = Calendar.getInstance();
        ProcessDiagramDate.setTime(sdf.parse(params.currentProcessDiagramDate + " 00:00:00"));
        int ProcessDiagramDateYear = ProcessDiagramDate.get(Calendar.YEAR);//获取年份
        int ProcessDiagramDateMonth = ProcessDiagramDate.get(Calendar.MONTH);//获取月份
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String  query
        if(dep_tree_id.length()<=6){
                query = "Delete FROM  ProcessDiagram  where substring(tree_id, 1,LENGTH('"+dep_tree_id+"'))='" + dep_tree_id + "' and MONTH(buy_date)='" + (ProcessDiagramDateMonth + 1) + "' and  YEAR(buy_date)='" + ProcessDiagramDateYear+"'"//选定单位信息
        }else{
                query = "Delete FROM  ProcessDiagram  where tree_id='" + dep_tree_id +"' and MONTH(buy_date)='" + (ProcessDiagramDateMonth + 1) + "' and  YEAR(buy_date)='" + ProcessDiagramDateYear+"'"//正常查询
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
        //println("params.recId===="+params.recId)
        def processDiagramInstance = ProcessDiagram.get(params.recId)
        String src = processDiagramInstance.titleCode + "_" + processDiagramInstance.title + "." + processDiagramInstance.wjlx;//文件名
        def filePath = getServletContext().getRealPath("/") + "processDiagram\\";
        File[] files = (new File(filePath)).listFiles();//取processDiagram下的所有文件
        for (int i = 0; i < files.length; i++) {
            String filename = files[i].getName();//取processDiagram下的第i个文件名
            if ((filename.indexOf(src) != -1) && ((processDiagramInstance.wjlx == 'bpmn') || (processDiagramInstance.wjlx == 'xml'))) {
                render "true"// println("文件存在！！！")
                return
            }
        }

        render "false" // println("文件不存在！！！")
        return
    }
//上传流程图文件, //通过网页建立的流程图文件，前三个字节(buffer1[0]==-17)、(buffer1[1]==-69)、(buffer1[2]==-65是多余的，影响流程图的部署，所以要跳过这三个字节
    @Transactional
    def importProcessDiagram() throws Exception {
        // println("params.recId===="+params.recId)
        def processDiagramInstance = ProcessDiagram.get(params.recId)
        def f = request.getFile(initcap3("ProcessDiagram") + "filePath")
        String fileName
        def filePath
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            ArrayList<String> list = new ArrayList<String>();
            list = fileName.split('\\.')
            if (list.size() > 2) {
                processDiagramInstance.title = list[0] + "." + list[1];
            } else {
                processDiagramInstance.title = list[0];
            }
            //特殊分隔符时前加\\
            processDiagramInstance.wjlx = list[list.size() - 1];  //特殊分隔符时前加\\
            processDiagramInstance.scr = User.get(springSecurityService.principal.id).truename
            processDiagramInstance.dep = User.get(springSecurityService.principal.id).department == "全部" ? "市人社局" : User.get(springSecurityService.principal.id).department
            filePath = getServletContext().getRealPath("/") + "processDiagram\\";
            File[] files = (new File(filePath)).listFiles();//取processDiagram下的所有文件
            for (int i = 0; i < files.length; i++) {
                String filename = files[i].getName();//取processDiagram下的第i个文件名
                if (filename.indexOf(processDiagramInstance.titleCode) != -1) {
                    files[i].delete();
                }
            }
            try {
                    f.transferTo(new File(filePath + processDiagramInstance.titleCode + "_" + fileName))//把客户端上选的文件上传到服务器
                    processDiagramInstance.save()//返回当前保存的记录
            } catch (e) {
                render "{success:true,info:'错误！文件上传失败'}"//render "typeError"//导入的Excel文件类型不对
                return
            }
        } else {
            render "{success:true,info:'错误！导入的Excel文件不存在'}" //render "failure"//导入的Excel文件不存在
            return
        }
        render "{success:true,info:'文件上传成功'}"
    }

    //(暂时不用)上传流程图文件, //通过网页建立的流程图文件，前三个字节(buffer1[0]==-17)、(buffer1[1]==-69)、(buffer1[2]==-65是多余的，影响流程图的部署，所以要跳过这三个字节
    @Transactional
    def importProcessDiagrambak() throws Exception {
        // println("params.recId===="+params.recId)
        def processDiagramInstance = ProcessDiagram.get(params.recId)
        def f = request.getFile(initcap3("ProcessDiagram") + "filePath")
        String fileName
        def filePath
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            ArrayList<String> list = new ArrayList<String>();
            list = fileName.split('\\.')
            if (list.size() > 2) {
                processDiagramInstance.title = list[0] + "." + list[1];
            } else {
                processDiagramInstance.title = list[0];
            }
            //特殊分隔符时前加\\
            processDiagramInstance.wjlx = list[list.size() - 1];  //特殊分隔符时前加\\
            processDiagramInstance.scr = User.get(springSecurityService.principal.id).truename
            processDiagramInstance.dep = User.get(springSecurityService.principal.id).department == "全部" ? "市人社局" : User.get(springSecurityService.principal.id).department
            filePath = getServletContext().getRealPath("/") + "processDiagram\\";
            File[] files = (new File(filePath)).listFiles();//取processDiagram下的所有文件
            for (int i = 0; i < files.length; i++) {
                String filename = files[i].getName();//取processDiagram下的第i个文件名
                if (filename.indexOf(processDiagramInstance.titleCode) != -1) {
                    files[i].delete();
                }
            }
            try {
                //if ((fileName.toLowerCase().endsWith(".doc"))||(fileName.toLowerCase().endsWith(".ppt"))||(fileName.toLowerCase().endsWith(".xls"))||(fileName.toLowerCase().endsWith(".pdf")) ) {
                if (1) {
                    String result
                    f.transferTo(new File(filePath + processDiagramInstance.titleCode + "_" + fileName))//把客户端上选的文件上传到服务器
                    f.transferTo(new File(filePath + processDiagramInstance.titleCode + "1_" + fileName))//把客户端上选的文件上传到服务器

                    FileInputStream isResult = new FileInputStream(new File(filePath + processDiagramInstance.titleCode + "1_" + fileName));
                    byte[]   buffer1   =   new   byte[isResult.available()];
                    isResult.read(buffer1);
                    if((buffer1[0]==-17)||(buffer1[1]==-69)||(buffer1[2]==-65)){
                        println("通过网页建立的流程图文件，前三个字节(buffer1[0]==-17)、(buffer1[1]==-69)、(buffer1[2]==-65是多余的，影响流程图的部署，所以要跳过这三个字节")
                        byte[]  buffer=new byte[buffer1.size()-3]
                        int j=0
                        for(int i=0;i<buffer1.size();i++){
                            if(((buffer1[0]==-17)||(buffer1[1]==-69)||(buffer1[2]==-65))&&(i<3)){
                                //println("buffer1[i]====="+buffer1[i])
                            }else{
                                buffer[j++]=buffer1[i]
                            }
                        }
                        result   =   new   String(buffer);
                        isResult.close();
                        File bpmnFile = new File(filePath + processDiagramInstance.titleCode + "_" + fileName);
                        try {
                            bpmnFile.createNewFile();
                            FileOutputStream txtfile = new FileOutputStream(bpmnFile);
                            PrintStream p = new PrintStream(txtfile,true,"Unicode");
                            //DataOutputStream p = new DataOutputStream(txtfile);
                            p.println(result);
                            txtfile.close();
                            p.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        processDiagramInstance.save()//返回当前保存的记录
                        new File(filePath + processDiagramInstance.titleCode + "1_" + fileName).delete()
                    }else{
                        /* result   =   new   String(buffer1);
                         isResult.close();
                         File bpmnFile = new File(filePath + processDiagramInstance.titleCode + "_" + fileName);
                         try {
                             bpmnFile.createNewFile();
                             FileOutputStream txtfile = new FileOutputStream(bpmnFile);
                             PrintStream p = new PrintStream(txtfile,true,"Unicode");
                            // DataOutputStream p = new DataOutputStream(txtfile);
                             p.println(result);
                             txtfile.close();
                             p.close();
                         } catch (IOException e) {
                             e.printStackTrace();
                         }*/
                        println("不是通过网页建立的流程图文件，")
                        f.transferTo(new File(filePath + processDiagramInstance.titleCode + "_" + fileName))//把客户端上选的文件上传到服务器
                        processDiagramInstance.save()//返回当前保存的记录
                        new File(filePath + processDiagramInstance.titleCode + "1_" + fileName).delete()
                    }

                } else {
                    //println("")
                    render "{success:true,info:'错误！上传文件类型不对'}"//render "typeError"//导入的Excel文件类型不对
                    return
                }
            } catch (e) {
                render "{success:true,info:'错误！文件上传失败'}"//render "typeError"//导入的Excel文件类型不对
                return
            }
        } else {
            render "{success:true,info:'错误！导入的Excel文件不存在'}" //render "failure"//导入的Excel文件不存在
            return
        }
        render "{success:true,info:'文件上传成功'}"
    }


//保存流程图
    @Transactional
    def saveDiagram(){
       // println("params.xml====="+params.xml)
       // println("params.xml====="+(params.xml).toString())
        String filePath = getServletContext().getRealPath("/") + "/processDiagram/"
        String fileName=params.filename
        /*//方法一
        File f=new File(filePath + fileName)
        f.append(params.xml)
        */
       //方法二
        File bpmnFile = new File(filePath + fileName);
        try {
            bpmnFile.createNewFile();
            FileOutputStream txtfile = new FileOutputStream(bpmnFile);
            PrintStream p = new PrintStream(txtfile);
            p.println(params.xml);
            txtfile.close();
            p.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        render "success"
    }

    //更新流程相关用户
    @Transactional
    def updateProcessUser(){
        String query
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        query ="Delete  from  `act_id_user` where ID_<>'admin'"
        sql.execute(query)

        query = "INSERT INTO `act_id_user`  (ID_, LAST_)  SELECT username, truename  FROM `user`  WHERE username<>'admin'"
        if (sql.execute(query)) {
            render "failure"//Json字符串
            return
        }
        render "success"//Json字符串
        return;
    }


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