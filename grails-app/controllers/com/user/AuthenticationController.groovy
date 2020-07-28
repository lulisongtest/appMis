package com.user


import com.listener.SessionListener
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.ui.AbstractS2UiController
import groovy.sql.Sql


import javax.servlet.http.HttpSession
import java.text.SimpleDateFormat

@Transactional
class AuthenticationController extends AbstractS2UiController{
    def springSecurityService

    //登录入口
    def userLogin(){
        println("管理系统————登录入口")
        //departmentName=params.departmentName
        String dataTreeStoreInfo,query
        String processTask=""
        int treeIdm
        String username, message
        def truename
        def department
        def treeid
        def chineseAuthority
        def userObj = springSecurityService?.currentUser?.id
        if(userObj){
            User userInstance = User.get(userObj)
            userInstance.lastlogindate = new Date()
            if(!userInstance.save(flush:true)){//如使用userInstance.save()则不能保存，
                userInstance.errors.each {
                    //println it
                }
            }
            List sessions1 = SessionListener.getSessions1();
            username = User.get(springSecurityService.principal.id).username
            //判断监听器SessionListener的sessions1中是否有当前登录的username,或把当前用户信息写入监听器SessionListener的sessions1
            int  j=0
            try {
                for ( j = 0; j < sessions1.size(); j++) {
                    // if ((((HttpSession) sessions1.get(j)).getAttribute("username").equals(username))&&(sessions1.get(j)!=session)) {//如是当前用户只刷新页面则不把自己踢下线(sessions1.get(j)!=session)
                    if (((HttpSession) sessions1.get(j)).getAttribute("username").equals(username)) {//判断监听器SessionListener的sessions1中是否有当前登录的username，
                        // ((HttpSession) sessions1.get(j)).invalidate();//把重名的对方踢下线！！！
                        ((HttpSession) sessions1.get(j)).removeAttribute("username")////把重名的对方移出session！！
                        break;
                    }
                }
                HttpSession session = request.getSession(); //将当前登录的用户名存储到session中
                // println("333==sessions1.size()====="+sessions1.size())
                session.setAttribute("username", username);//正常时可以激活监听器SessionListener！！！！会自动向sessions1中写入一条session
                session.setAttribute("loginsession", session);
                // println("444==sessions1.size()====="+sessions1.size())
            } catch (e) {
                // println("异常！！！"+e)//按浏览器的回退按钮且重新登录时，读取((HttpSession) sessions1.get(j)).getAttribute("username")会产生异常，sessions1.get(j)获得的session已经失效了
                sessions1.remove(j)//手动把这条已经失效的session从监听器中的sessions1移出。
                HttpSession session = request.getSession();//读取重新登录的session
                session.setAttribute("username", username);//异常后，在此已不能激活监听器SessionListener！！！！
                session.setAttribute("loginsession", session);
                sessions1.add(j,session)//所以在此手动激活一次监听器SessionListener！！！！向sessions1中写入一条session
                // println("异常444==sessions1.size()====="+sessions1.size())
            }
            truename=""+User.get(springSecurityService.principal.id).truename
            chineseAuthority=""+User.get(springSecurityService.principal.id).chineseAuthority
            department=""+User.get(springSecurityService.principal.id).department
            treeid=""+User.get(springSecurityService.principal.id).treeId
        }else{
            //println("没有登录,返回重新登录！！！")
            render(contentType: "text/json") {
                roots2  ""   //根据登录用户筛选过单位用户名,可复选，在此不能做运算。
                roots1  ""  //没筛选过全部用户名,可复选，在此不能做运算。
                roots   ""  //筛选过单位名，在此不能做运算。        // 先序遍历，拼接JSON字符串
                username1 "没有登录"
                truename1   "没有登录"//真实姓名
                chineseAuthority1   ""//中文角色
                department1   ""//单位
                treeid1   ""//单位码
                processTasks  processTask//事务提醒
            }
            return
        }
        // println("登录入口")
        List dataList
        if(department!='全部'){//是超级管理员
            String treeId1=treeid
            query="from Department  where  substring(treeId, 1,LENGTH('"+treeId1+"'))='" +treeId1 + "'"//基本和子单位
            for(int i=1;i<=(treeId1.length()/3-1);i++){
                query=query+" OR treeId='" +treeId1.substring(0,i*3) + "'"//本单位的上级单位
            }
        }else{
            query="from Department"
        }
        dataList=Department.findAll(query)
        // 节点列表（散列表，用于临时存储节点对象）
        HashMap nodeList = new HashMap();
        Node noderoot = new Node();
        noderoot.id ="1000"   //根节点号
        // noderoot.text =(departmentName=="全部"?"克拉玛依市":departmentName)//单位名称
        noderoot.text = "克拉玛依市"//单位名称
        noderoot.glyph= '0xf0c9'
        noderoot.parentId =""
        nodeList.put(noderoot.id, noderoot);
        // 根节点
        Node root = null;
        // 根据结果集构造节点列表（存入散列表）
        String streeId
        for (Iterator it = dataList.iterator(); it.hasNext();) {
            Department dataRecord = (Department) it.next();
            // println("treeId====" + dataRecord.treeId)
            streeId=dataRecord.treeId;
            Node node = new Node();
            node.id = (String)streeId;
            node.text = (String) dataRecord.department;
            if(dataRecord.glyph=="0"){
                node.glyph="0xf0c9"
            }else{
                node.glyph=dataRecord.glyph
            }
            if(streeId.length()==3){
                node.parentId ="1000"
            }else{
                node.parentId = streeId.substring(0,streeId.length()-3);
            }
            nodeList.put(node.id, node);
            // println(" node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
        }

// 构造无序的多叉树
        Set entrySet = nodeList.entrySet();
        for (Iterator it = entrySet.iterator(); it.hasNext();) {
            Node node = (Node) ((Map.Entry) it.next()).getValue();
            // println(" ======node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
            if (node.parentId == null || node.parentId.equals("")) {
                root = node;
            } else {
                // println("node.parentId==="+node.parentId)
                ((Node) nodeList.get(node.parentId)).addChild(node);
            }
        }

        // 输出无序的树形菜单的JSON字符串
        // 对多叉树进行横向排序
        root.sortChildren();
        // 输出有序的树形菜单的JSON字符串//
        //println("输出有序的树形菜单的JSON字符串")
        // println("root.toString()======="+root.toString())
        // String r=(department!='全部')?root.toString().replaceAll('false','true'):root.toString() // 先序遍历，拼接JSON字符串
        String r=root.toString() // department='全部'先序遍历，拼接JSON字符串
        // println("r======="+r)

        String r1="",r2=""

        //事务提醒
        //取事务
        // println("2222登录入口=========事务提醒")
        try{
            if(chineseAuthority=='单位管理员'){
               // processTask=readDisagreeProcessTask(treeid)
            }else{
                processTask=""
            }
        }catch(e){
            processTask=""
        }



        // println("33333登录入口=========事务提醒")

        render(contentType: "text/json") {
            roots2 r2   //根据登录用户筛选过单位用户名,可复选，在此不能做运算。
            roots1 r1 //没筛选过全部用户名,可复选，在此不能做运算。
            roots r
            username1 username//用户名
            truename1   truename//真实姓名
            chineseAuthority1   chineseAuthority//中文角色
            department1   department//单位
            treeid1   treeid//单位码
            processTasks  processTask//事务提醒
        }
        return
    }









    def authenticate() {
        springSecurityService.clearCachedRequestmaps()//Requestmap 规则项是被缓存的。但是缓存会影响运行期配置的灵活性。如果你创建、编辑或者删除了一条规则，缓存必须被刷新来保证与数据库中数据的一致性。
        render springSecurityService.authentication.authorities
        return
    }
    def checkUser(){
        String s=params.username;
        String query=""
        if(s==null){
            render "failure"
            return
        }else{
            query = "from User where username='"+s+"'"
        }
        def list1 = User.findAll(query)
        if(list1.size()>0){
            render "success"
            return
        }else{
            render "failure"
            return}

    }

    def test(){//判断有无访问该表数据的权限
        render "success"
        return
    }
    def userlogin2(){//判断有无访问该表数据的权限
        render "success"
        return
    }
    def userlogin3(){//判断有无访问该表数据的权限
        render "success"
        return
    }
    def userlogin(){
        String dataTreeStoreInfo
        int treeIdm
        def truename
        def department
        def chineseAuthority
        def userObj = springSecurityService?.currentUser?.id
        if(userObj){
            User userInstance = User.get(userObj)
            userInstance.lastlogindate = new Date()
            /* //以下代码也可以
               String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName
               def user = lookupUserClass().findWhere((usernameFieldName): userInstance.username)
               user.lastlogindate = new Date()
               if(!user.save(flush:true)){
            */
            if(!userInstance.save(flush:true)){//如使用userInstance.save()则不能保存，
                userInstance.errors.each {
                   // println it
                }
            }
            truename=""+User.get(springSecurityService.principal.id).truename
            chineseAuthority=""+User.get(springSecurityService.principal.id).chineseAuthority
            department=""+User.get(springSecurityService.principal.id).department
        }else{
            truename = "普通用户"//真实姓名
            chineseAuthority= "普通用户"//中文角色
            department="未知单位"
        }
        // springSecurityService.reauthenticate(springSecurityService.authentication.name)//如当前用户权限修改后，可立即生效。//切换用户操作时不能调用
        // println("springSecurityService.currentUser.properties.get('truename')==="+springSecurityService.currentUser.properties.get('truename'))
        // println("User.get(springSecurityService.principal.id).truename==="+User.get(springSecurityService.principal.id).truename)
        // render User.get(springSecurityService.principal.id).truename
        // println("============="+ User.get(springSecurityService.principal.id).truename+"  "+User.get(springSecurityService.principal.id).chineseAuthority)
        //  dataTreeStoreInfo=""+User.get(springSecurityService.principal.id).truename+" "+ User.get(springSecurityService.principal.id).chineseAuthority

        //取到表树结点号
        String  query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%'))  and treeId>0 and LENGTH(treeId)>1 and LENGTH(treeId)<3"
        // String query="from Requestmap WHERE (roleList LIKE '%普通用户%' or roleList LIKE '%"+chineseAuthority+"%') and treeId<10 and treeId>0"
        def list0=Requestmap.findAll(query)
        query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%'))  and treeId>0 and LENGTH(treeId)>2  order by url desc"
        // String query="from Requestmap WHERE (roleList LIKE '%普通用户%' or roleList LIKE '%"+chineseAuthority+"%') and treeId<10 and treeId>0"
        def list1=Requestmap.findAll(query)
        query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%')) and treeId>0 and LENGTH(treeId)>2 and LENGTH(treeId)<5 and substring(treeId,1,2)='01'"
        //  query="from Requestmap WHERE (roleList LIKE '%普通用户%' or roleList LIKE '%"+chineseAuthority+"%') and treeId>30"
        def list2=Requestmap.findAll(query)
        query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%')) and treeId>0 and LENGTH(treeId)>2 and LENGTH(treeId)<5 and substring(treeId,1,2)='04'"
        // query="from Requestmap WHERE (roleList LIKE '%普通用户%' or roleList LIKE '%"+chineseAuthority+"%') and treeId>=10 and treeId<30"
        def list3=Requestmap.findAll(query)
        //取得所有树结点
        query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%'))  and treeId>0 and LENGTH(treeId)>=2  order by url desc"
        List dataList=Requestmap.findAll(query)
        // 节点列表（散列表，用于临时存储节点对象）
        HashMap nodeList = new HashMap();
        Node noderoot = new Node();
        noderoot.id ="1000"   //根节点号
        noderoot.text ="克拉玛依市单位信息"
        noderoot.glyph= '0xf0c9'//节点前的小图标，在此没有起作用，在菜单下有作用
        noderoot.parentId =""
        nodeList.put(noderoot.id, noderoot);
        // 根节点
        Node root = null;
        // 根据结果集构造节点列表（存入散列表）
        String streeId
        for (Iterator it = dataList.iterator(); it.hasNext();) {
            Requestmap dataRecord = (Requestmap) it.next();
            //  println("treeId====" + dataRecord.treeId)
            streeId=dataRecord.treeId;
            Node node = new Node();
            node.id = (String)streeId;
            node.text = (String) dataRecord.chineseUrl;
            if(dataRecord.glyph=="0"){
                node.glyph="0xf0c9"
            }else{
                node.glyph=dataRecord.glyph
            }
            if(streeId.length()==2){
                node.parentId ="1000"
            }else{
                node.parentId = streeId.substring(0,streeId.length()-2);
            }
            nodeList.put(node.id, node);
            //  println(" node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
        }
// 构造无序的多叉树
        Set entrySet = nodeList.entrySet();
        for (Iterator it = entrySet.iterator(); it.hasNext();) {
            Node node = (Node) ((Map.Entry) it.next()).getValue();
            // println(" node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
            if (node.parentId == null || node.parentId.equals("")) {
                root = node;
            } else {
                // println("node.parentId==="+node.parentId)
                ((Node) nodeList.get(node.parentId)).addChild(node);
            }
        }
        // 输出无序的树形菜单的JSON字符串
        //   System.out.println("root====="+root.toString());
        // 对多叉树进行横向排序
        root.sortChildren();
        // 输出有序的树形菜单的JSON字符串
        //  System.out.println("root====="+root.toString());
        render(contentType: "text/json") {
            roots root.toString() // 先序遍历，拼接JSON字符串
            truename1 truename//真实姓名
            chineseAuthority1 chineseAuthority//中文角色
            department1 department//单位
        }
        return
    }

    def userloginmodifydepartment() {
        // println("userlogin1==========================")
        String dataTreeStoreInfo, query
        int treeIdm
        String username, message
        def truename
        def department
        def treeid
        def chineseAuthority
        def userObj = springSecurityService?.currentUser?.id
        if (userObj) {
            User userInstance = User.get(userObj)
            userInstance.lastlogindate = new Date()
            /* //以下代码也可以
               String usernameFieldName = SpringSecurityUtils.securityConfig.userLookup.usernamePropertyName
               def user = lookupUserClass().findWhere((usernameFieldName): userInstance.username)
               user.lastlogindate = new Date()
               if(!user.save(flush:true)){
            */
            if (!userInstance.save(flush: true)) {//如使用userInstance.save()则不能保存，
                userInstance.errors.each {
                   // println it
                }
            }
            //List sessions = SessionListener.getSessions();
            // List sessions1 = SessionListener.getSessions1();
            username = User.get(springSecurityService.principal.id).username
            //  HttpSession session = request.getSession(); //将当前登录的用户名存储到session中
            //   session.setAttribute("username", username);

            //    for (int j = 0; j < sessions1.size(); j++) {
            //       if (((HttpSession) sessions1.get(j)).getAttribute("username").equals(username)) {
            //           ((HttpSession) sessions1.get(j)).invalidate();//把重名的对方踢下线！！！
            //            break;
            //       }
            //   }

            // println("sessions.size()====" + sessions.size())
            /*if (sessions.size() > 0) {
                for (int i = 0; i < sessions.size() - 1; i++) {
                    if (sessions.get(i).equals(username)) {
                        //有重名登录的
                        for (int j = 0; j < sessions1.size(); j++) {
                            if (((HttpSession) sessions1.get(j)).getAttribute("username").equals(username)) {
                                ((HttpSession) sessions1.get(j)).invalidate();//把重名的对方踢下线！！！
                                break;
                            }
                        }
                        // render "relogin"
                        // return
                    }
                }
             }*/
            //  session.setAttribute("loginsession", session);
            // response.setCharacterEncoding("UTF=8");
            //response.setContentType("text/html;charset=UTF-8");
            //使用request对象的getSession()获取session，如果session不存在则创建一个
            //  HttpSession session = request.getSession(); //将当前登录的用户名存储到session中
            //  session.setAttribute("username", User.get(springSecurityService.principal.id).username);
            // println("userlogin1=========================="+User.get(springSecurityService.principal.id).username)
            truename = "" + User.get(springSecurityService.principal.id).truename
            chineseAuthority = "" + User.get(springSecurityService.principal.id).chineseAuthority
            department = "" + User.get(springSecurityService.principal.id).department
            treeid = "" + User.get(springSecurityService.principal.id).treeId
        } else {
            truename = "普通用户"//真实姓名
            chineseAuthority = "普通用户"//中文角色
            department = "未知单位"
            treeid = ""
        }
        // springSecurityService.reauthenticate(springSecurityService.authentication.name)//如当前用户权限修改后，可立即生效。//切换用户操作时不能调用
        // println("springSecurityService.currentUser.properties.get('truename')==="+springSecurityService.currentUser.properties.get('truename'))
        // println("User.get(springSecurityService.principal.id).truename==="+User.get(springSecurityService.principal.id).truename)
        // render User.get(springSecurityService.principal.id).truename
        // println("============="+ User.get(springSecurityService.principal.id).truename+"  "+User.get(springSecurityService.principal.id).chineseAuthority)
        //  dataTreeStoreInfo=""+User.get(springSecurityService.principal.id).truename+" "+ User.get(springSecurityService.principal.id).chineseAuthority

        //取到表树结点号
        //   String  query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%'))  and treeId>0 and LENGTH(treeId)>1 and LENGTH(treeId)<3"
        // String query="from Requestmap WHERE (roleList LIKE '%普通用户%' or roleList LIKE '%"+chineseAuthority+"%') and treeId<10 and treeId>0"
        // def list0=Requestmap.findAll(query)
        //   query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%'))  and treeId>0 and LENGTH(treeId)>2  order by url desc"
        // String query="from Requestmap WHERE (roleList LIKE '%普通用户%' or roleList LIKE '%"+chineseAuthority+"%') and treeId<10 and treeId>0"
        //  def list1=Requestmap.findAll(query)
        //  query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%')) and treeId>0 and LENGTH(treeId)>2 and LENGTH(treeId)<5 and substring(treeId,1,2)='01'"
        //  query="from Requestmap WHERE (roleList LIKE '%普通用户%' or roleList LIKE '%"+chineseAuthority+"%') and treeId>30"
        // def list2=Requestmap.findAll(query)
        // query="from Requestmap WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%')) and treeId>0 and LENGTH(treeId)>2 and LENGTH(treeId)<5 and substring(treeId,1,2)='04'"
        // query="from Requestmap WHERE (roleList LIKE '%普通用户%' or roleList LIKE '%"+chineseAuthority+"%') and treeId>=10 and treeId<30"
        // def list3=Requestmap.findAll(query)
        //取得所有树结点

        // println("userlogin1=============22222===department=========="+department)
        // query="from Department WHERE ((roleList LIKE '%"+chineseAuthority+"%') or (roleList LIKE '%普通用户%'))  and treeId>0 and LENGTH(treeId)>=2  order by url desc"
        List dataList
        if (department != '全部') {//是超级管理员
            // query="from Department  where treeId='"+treeid+"'"
            // println("query======0000======="+query)
            // dataList=Department.findAll(query)
            // String treeId1=dataList[0].treeId
            String treeId1 = treeid

            // query="from Department  where LENGTH(treeId)<=6  OR  substring(treeId, 1,LENGTH('"+treeId1+"'))='" +treeId1 + "'"//基本和子单位
            query = "from Department  where  substring(treeId, 1,LENGTH('" + treeId1 + "'))='" + treeId1 +
                    "'"//基本和子单位
            for (int i = 1; i <= (treeId1.length() / 3 - 1); i++) {
                // query=query+" OR substring(treeId, 1,"+(6+i*3)+")='" +treeId1.substring(0,6+i*3) + "'"
                query = query + " OR treeId='" + treeId1.substring(0, i * 3) + "'"//本单位的上级单位
            }
        } else {
            query = "from Department"
        }

        //  println("query======11111======="+query)
        dataList = Department.findAll(query)
        //  println("dataList======11111======="+dataList.size())

        // 节点列表（散列表，用于临时存储节点对象）
        HashMap nodeList = new HashMap();
        Node noderoot = new Node();
        noderoot.id = "1000"   //根节点号
        //noderoot.text = departmentName//单位名称
        noderoot.text = "克拉玛依市"//单位名称
        noderoot.glyph = '0xf0c9'
        noderoot.parentId = ""
        nodeList.put(noderoot.id, noderoot);
        // 根节点
        Node root = null;
        // 根据结果集构造节点列表（存入散列表）
        String streeId
        for (Iterator it = dataList.iterator(); it.hasNext();) {
            Department dataRecord = (Department) it.next();
            //println("treeId====" + dataRecord.treeId)
            streeId = dataRecord.treeId;
            Node node = new Node();
            node.id = (String) streeId;
            node.text = (String) dataRecord.department;
            if (dataRecord.glyph == "0") {
                node.glyph = "0xf0c9"
            } else {
                node.glyph = dataRecord.glyph
            }
            if (streeId.length() == 3) {
                node.parentId = "1000"
            } else {
                node.parentId = streeId.substring(0, streeId.length() - 3);
            }
            nodeList.put(node.id, node);
            //println(" node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
        }

// 构造无序的多叉树
        Set entrySet = nodeList.entrySet();
        for (Iterator it = entrySet.iterator(); it.hasNext();) {
            Node node = (Node) ((Map.Entry) it.next()).getValue();
            //println(" ======node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
            if (node.parentId == null || node.parentId.equals("")) {
                root = node;
            } else {
                // println("node.parentId==="+node.parentId)
                ((Node) nodeList.get(node.parentId)).addChild(node);
            }
        }

        // 输出无序的树形菜单的JSON字符串
        // 对多叉树进行横向排序
        root.sortChildren();
        // 输出有序的树形菜单的JSON字符串//
        // println("root.toString()======="+root.toString())
        String xx=(department != '全部') ? root.toString().replaceAll('false', 'true') : root.toString()// 先序遍历，拼接JSON字符串

        render(contentType: "text/json") {
            roots xx
            truename1   truename//真实姓名
            chineseAuthority1   chineseAuthority//中文角色
            department1  department//单位
            treeid1   treeid//单位码
        }
        return
    }

    def userloginAll(){
        //println("userlogin1==========================")
        String dataTreeStoreInfo,query
        int treeIdm
        String username, message
        def truename
        def department
        def treeid
        def chineseAuthority
        List dataList
        query="from Department"
        dataList=Department.findAll(query)

        // 节点列表（散列表，用于临时存储节点对象）
        HashMap nodeList = new HashMap();
        Node noderoot = new Node();
        noderoot.id ="1000"   //根节点号
       // noderoot.text =departmentName//单位名称
        noderoot.text = "克拉玛依市"//单位名称
        noderoot.glyph= '0xf0c9'
        noderoot.parentId =""
        nodeList.put(noderoot.id, noderoot);
        // 根节点
        Node root = null;
        // 根据结果集构造节点列表（存入散列表）
        String streeId
        for (Iterator it = dataList.iterator(); it.hasNext();) {
            Department dataRecord = (Department) it.next();
            //println("treeId====" + dataRecord.treeId)
            streeId=dataRecord.treeId;
            Node node = new Node();
            node.id = (String)streeId;
            node.text = (String) dataRecord.department;
            if(dataRecord.glyph=="0"){
                node.glyph="0xf0c9"
            }else{
                node.glyph=dataRecord.glyph
            }
            if(streeId.length()==3){
                node.parentId ="1000"
            }else{
                node.parentId = streeId.substring(0,streeId.length()-3);
            }
            nodeList.put(node.id, node);
            //println(" node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
        }

// 构造无序的多叉树
        Set entrySet = nodeList.entrySet();
        for (Iterator it = entrySet.iterator(); it.hasNext();) {
            Node node = (Node) ((Map.Entry) it.next()).getValue();
            //println(" ======node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
            if (node.parentId == null || node.parentId.equals("")) {
                root = node;
            } else {
                // println("node.parentId==="+node.parentId)
                ((Node) nodeList.get(node.parentId)).addChild(node);
            }
        }

        // 输出无序的树形菜单的JSON字符串
        // 对多叉树进行横向排序
        root.sortChildren();
        // 输出有序的树形菜单的JSON字符串//
        //println("root.toString()======="+root.toString())
        render(contentType: "text/json") {
            roots root.toString() // 先序遍历，拼接JSON字符串
            //truename1=truename//真实姓名
            // chineseAuthority1=chineseAuthority//中文角色
            // department1=department//单位
            // treeid1=treeid//单位码
        }
        return
    }









//即时通讯，取用户
    def userMessage(){
        String departmentName=params.departmentName
        String dataTreeStoreInfo,query
        int treeIdm
        List sessions1
        String username, message
        def truename
        def department
        def treeid
        def chineseAuthority
        def userObj = springSecurityService?.currentUser?.id
        if(userObj){
            User userInstance = User.get(userObj)
            userInstance.lastlogindate = new Date()
            if(!userInstance.save(flush:true)){//如使用userInstance.save()则不能保存，
                userInstance.errors.each {
                   // println it
                }
            }
            sessions1 = SessionListener.getSessions1();
            username = User.get(springSecurityService.principal.id).username
            //判断监听器SessionListener的sessions1中是否有当前登录的username,或把当前用户信息写入监听器SessionListener的sessions1
            int  j=0
            try {
                for ( j = 0; j < sessions1.size(); j++) {
                    // if ((((HttpSession) sessions1.get(j)).getAttribute("username").equals(username))&&(sessions1.get(j)!=session)) {//如是当前用户只刷新页面则不把自己踢下线(sessions1.get(j)!=session)
                    if (((HttpSession) sessions1.get(j)).getAttribute("username").equals(username)) {//判断监听器SessionListener的sessions1中是否有当前登录的username，
                        // ((HttpSession) sessions1.get(j)).invalidate();//把重名的对方踢下线！！！
                        ((HttpSession) sessions1.get(j)).removeAttribute("username")////把重名的对方移出session！！
                        break;
                    }
                }
                HttpSession session = request.getSession(); //将当前登录的用户名存储到session中
                // println("333==sessions1.size()====="+sessions1.size())
                session.setAttribute("username", username);//正常时可以激活监听器SessionListener！！！！会自动向sessions1中写入一条session
                session.setAttribute("loginsession", session);
                // println("444==sessions1.size()====="+sessions1.size())
            } catch (e) {
                // println("异常！！！"+e)//按浏览器的回退按钮且重新登录时，读取((HttpSession) sessions1.get(j)).getAttribute("username")会产生异常，sessions1.get(j)获得的session已经失效了
                sessions1.remove(j)//手动把这条已经失效的session从监听器中的sessions1移出。
                HttpSession session = request.getSession();//读取重新登录的session
                session.setAttribute("username", username);//异常后，在此已不能激活监听器SessionListener！！！！
                session.setAttribute("loginsession", session);
                sessions1.add(j,session)//所以在此手动激活一次监听器SessionListener！！！！向sessions1中写入一条session
                // println("异常444==sessions1.size()====="+sessions1.size())
            }
            truename=""+User.get(springSecurityService.principal.id).truename
            chineseAuthority=""+User.get(springSecurityService.principal.id).chineseAuthority
            department=""+User.get(springSecurityService.principal.id).department
            treeid=""+User.get(springSecurityService.principal.id).treeId
        }else{
            //println("没有登录,返回重新登录！！！")
            render(contentType: "text/json") {
                roots2  ""   //根据登录用户筛选过单位用户名,可复选，在此不能做运算。
                roots1  ""  //没筛选过全部用户名,可复选，在此不能做运算。
                roots   ""  //筛选过单位名，在此不能做运算。        // 先序遍历，拼接JSON字符串
                username1 "没有登录"
                truename1   "没有登录"//真实姓名
                chineseAuthority1   ""//中文角色
                department1   ""//单位
                treeid1   ""//单位码
            }
            return
        }

//println("sessions1.size()====="+sessions1.size())
//println("username====="+((HttpSession) sessions1.get(0)).getAttribute("username"))


//不进行筛选，全部单位用户可复选
        List dataList1
        /*if (department != '全部') {//是超级管理员
            String treeId1 = treeid
            query = "from Department  where  substring(treeId, 1,LENGTH('" + treeId1 + "'))='" + treeId1 +
                    "'"//基本和子单位
            for (int i = 1; i <= (treeId1.length() / 3 - 1); i++) {
                // query=query+" OR substring(treeId, 1,"+(6+i*3)+")='" +treeId1.substring(0,6+i*3) + "'"
                query = query + " OR treeId='" + treeId1.substring(0, i * 3) + "'"//本单位的上级单位
            }
        } else {
            query = "from Department"
        }*/
        query = "from Department"
        dataList1 = Department.findAll(query)
        // 节点列表（散列表，用于临时存储节点对象）
        HashMap nodeList1 = new HashMap();
        Node noderoot1 = new Node();
        noderoot1.id = "1000"   //根节点号
       // noderoot1.text = "<font color=\"#FF0000\">克拉玛依市</font> "//红色字体
        noderoot1.text = "克拉玛依市"
        noderoot1.glyph = '0xf0c9'
        noderoot1.parentId = ""
        nodeList1.put(noderoot1.id, noderoot1);

        String streeId1
        //增加管理员
        query = "from User where treeId='000'"///User 不能用user只能用User
        def listUser=User.findAll(query)
        streeId1="000"
        for(int i=0;i<listUser.size();i++){
            Node node = new Node();
            if(i<10){
                node.id = (String) streeId1+"00"+i;
            }else{
                if(i<100){
                    node.id = (String) streeId1+"0"+i;
                }else{
                    node.id = (String) streeId1+i;
                }
            }
            // node.text = ""+listUser[i].username+":"+listUser[i].truename;
           // node.text = ""+listUser[i].truename;
           // node.text = "<font color=\"#00AB00\"><strong>"+listUser[i].truename+"</strong></font> "//加粗绿色字体
            node.text = "<span class=\"admin\">"+listUser[i].truename+"</span>"//加粗绿色字体
           // node.text = "<font color=\"#00AB00\">"+listUser[i].truename+"</font> "//加粗绿色字体
            node.userId=""+listUser[i].username
            node.glyph = "0xf0c9"
            node.parentId = "1000"
            //if(streeId1=='001') println("================开始加用户名=====node.id=="+node.id)
            nodeList1.put(node.id, node);
        }
        //结束增加管理员




        // 根节点
        Node root1 = null;
        // 根据结果集构造节点列表（存入散列表）

        for (Iterator it = dataList1.iterator(); it.hasNext();) {
            Department dataRecord = (Department) it.next();
            //println("treeId====" + dataRecord.treeId)
            streeId1 = dataRecord.treeId;
            Node node = new Node();
            node.id = (String) streeId1;
            node.text = (String) dataRecord.department;
            if (dataRecord.glyph == "0") {
                node.glyph = "0xf0c9"
            } else {
                node.glyph = dataRecord.glyph
            }
            if (streeId1.length() == 3) {
                node.parentId = "1000"
            } else {
                node.parentId = streeId1.substring(0, streeId1.length() - 3);
            }
            nodeList1.put(node.id, node);
            // println(" node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)


            query = "from User where treeId='"+streeId1+"'"///User 不能用user只能用User
            listUser=User.findAll(query)
            //def listUser = User.list();
            for(int i=0;i<listUser.size();i++){
                node = new Node();
                if(i<10){
                    node.id = (String) streeId1+"00"+i;
                }else{
                    if(i<100){
                        node.id = (String) streeId1+"0"+i;
                    }else{
                        node.id = (String) streeId1+i;
                    }
                }

               // node.text = ""+listUser[i].username+":"+listUser[i].truename;
                //是否用户在线
                node.text = ""+listUser[i].truename;//用户不在线
                for ( int j = 0; j < sessions1.size(); j++) {
                    if (((HttpSession) sessions1.get(j)).getAttribute("username").equals(listUser[i].username)) {//判断监听器SessionListener的sessions1中是否有当前登录的username，
                        node.text = "<span class=\"onlineuser\">"+listUser[i].truename+"</span> "//用户在线加粗红色字体
                        break;
                    }
                }

               // node.text = "<font color=\"#FF0000\"><strong>"+listUser[i].truename+"</strong></font> "//加粗红色字体
              //  node.text = ""+listUser[i].truename;

                node.userId=""+listUser[i].username
                if (dataRecord.glyph == "0") {
                    node.glyph = "0xf0c9"
                } else {
                    node.glyph = dataRecord.glyph
                }
                if (streeId1.length() == 3) {
                    node.parentId = "1000"
                } else {
                    node.parentId = streeId1.substring(0, streeId1.length());
                }
               // if(streeId1=='001') println("================开始加用户名=====node.id=="+node.id)
                nodeList1.put(node.id, node);
            }
        }
        // println("node is over!!!!!")

// 构造无序的多叉树
        Set entrySet1 = nodeList1.entrySet();
        for (Iterator it = entrySet1.iterator(); it.hasNext();) {
            Node node = (Node) ((Map.Entry) it.next()).getValue();
            //println(" ======node.id====" +  node.id+"---------node.text==="+ node.text+"---------node.parentId==="+ node.parentId)
            if (node.parentId == null || node.parentId.equals("")) {
                root1 = node;
            } else {
                // println("node.parentId==="+node.parentId)
                ((Node) nodeList1.get(node.parentId)).addChild(node);
            }
        }

        // 输出无序的树形菜单的JSON字符串
        // 对多叉树进行横向排序
        root1.sortChildren();
        // 输出有序的树形菜单的JSON字符串//
        //println("root.toString()======="+root.toString())
        //String r1=(department != '全部') ? root1.toString1().replaceAll('false', 'true') : root1.toString1()
        String r1= root1.toString1(username)

        //println("root1.toString()======="+root1.toString())




        render(contentType: "text/json") {

            roots1 r1  //没筛选过全部用户名,可复选，在此不能做运算。

            username1 username//用户名
            truename1   truename//真实姓名
            chineseAuthority1   chineseAuthority//中文角色
            department1   department//单位
            treeid1   treeid//单位码
        }
        return
    }

    //即时通讯，取在线用户数
    def online(){
        List sessions1
        sessions1 = SessionListener.getSessions1();
       // println("sessions1.size()==="+sessions1.size().toString())
        render(contentType: "text/json") {
            online  sessions1.size().toString()
        }
        return
     }



    def userlogout(){
        render true
        return
    }
}


/**
 * 节点类
 */
class Node {
    //节点编号
    public String id;
    //节点内容
    public String text;
    //节点内容1(如是用户，则存入帐号)
    public String userId;
    // 父节点编号
    public String parentId;
    //结点的小图标
    public String glyph;;
    // 孩子节点列表
    private Children children = new Children();

    // 先序遍历，拼接JSON字符串
    public String toString() {
        String result = "{"+ "id:'p" + id + "'"+ ",text:'" + text + "'"+ ",userId:'" + userId + "'";//id为纯数字，新版Extjs不认，所以重新在id前加入“p”
        // String result = "{"+ "id:'p" + id + "'"+ ",text:'" + text + "', expanded : true, ";//id为纯数字，新版Extjs不认，所以重新在id前加入“p”
        //String result = "{"        + "id : 'p" + id + "'"        + ", text : '" + text + "'"+",glyph : eval('(' + '"+glyph+"' + ')')";//id为纯数字，新版Extjs不认，所以重新在id前加入“p”//glyph在菜单中起作用
        //println("dep ========="+dep)
        if (children != null && children.getSize() != 0) {
            result += ", children : " + children.toString();
            if(text=="克拉玛依市"){//使一级树状菜单展开
                result += ",expanded:true,";
            }else{
                result += ",expanded:false, ";
            }
        } else {
            //result += ", leaf : true";//省略这行说明每个项目都可以是叶子或树干
        }
        //println("result ========="+result + "}" )
        return result + "}";
    }

    // 先序遍历，拼接JSON字符串
    public String toString1(String username) {
        String result = "{" + "id:'p" + id + "'" + ",text:'" + text + "'"+ ",userId:'" + userId + "'";//id为纯数字，新版Extjs不认，所以重新在id前加入“p”
        // String result = "{"+ "id:'p" + id + "'"+ ",text:'" + text + "', expanded : true, ";//id为纯数字，新版Extjs不认，所以重新在id前加入“p”
        //String result = "{"        + "id : 'p" + id + "'"        + ", text : '" + text + "'"+",glyph : eval('(' + '"+glyph+"' + ')')";//id为纯数字，新版Extjs不认，所以重新在id前加入“p”//glyph在菜单中起作用
        //println("dep ========="+dep)
        if (children != null && children.getSize() != 0) {
            result += ", children : " + children.toString1(username);
            if (text == "克拉玛依市") {//使一级树状菜单展开
                result += ",expanded:true,";
            } else {
                result += ",expanded:false, ";
            }
        } else {
            //result += ", leaf : true";//省略这行说明每个项目都可以是叶子或树干
            if(text!='其它'){
                if(userId.equals(username)){
                    result += ",checked:true";//注意这个checked,有这个checked了在界面上就会出现一个复选框
                }else{
                    result += ",checked:false";//注意这个checked,有这个checked了在界面上就会出现一个复选框
                }
            }
        }
        //println("result ========="+result + "}" )
        return result + "}";
    }




    // 兄弟节点横向排序
    public void sortChildren() {
        if (children != null && children.getSize() != 0) {
            children.sortChildren();
        }
    }

    // 添加孩子节点
    public void addChild(Node node) {
        this.children.addChild(node);
    }
}

/**
 * 孩子列表类
 */
class Children {
    private List list = new ArrayList();

    public int getSize() {
        return list.size();
    }

    public void addChild(Node node) {
        list.add(node);
    }

    // 拼接孩子节点的JSON字符串
    public String toString() {
        String result = "[";
        for (Iterator it = list.iterator(); it.hasNext();) {
            result += ((Node) it.next()).toString();
            result += ",";
        }
        // println("result=====addChild===="+result)
        result = result.substring(0, result.length() - 1);
        result += "]";
        return result;
    }

    // 拼接孩子节点的JSON字符串
    public String toString1(String username) {
        String result = "[";
        for (Iterator it = list.iterator(); it.hasNext();) {
            result += ((Node) it.next()).toString1(username);
            result += ",";
        }
        // println("result=====addChild===="+result)
        result = result.substring(0, result.length() - 1);
        result += "]";
        return result;
    }



    // 孩子节点排序
    public void sortChildren() {
        // 对本层节点进行排序
        // 可根据不同的排序属性，传入不同的比较器，这里传入ID比较器
        Collections.sort(list, new NodeIDComparator());//list是树中同级的node，即同级孩子
        // 对每个节点的下一层节点进行排序
        for (Iterator it = list.iterator(); it.hasNext();) {
            ((Node) it.next()).sortChildren();
        }
    }
}

/**
 * 节点比较器
 */
class NodeIDComparator implements Comparator {
    // 按照节点编号比较
    public int compare(Object o1, Object o2) {
        //  int j1 = Integer.parseInt(((Node)o1).id);
        // int j2 = Integer.parseInt(((Node)o2).id);//把字符串treeId转换为整形数进行比较排序
        String j1 = ((Node)o1).id;
        String j2 =((Node)o2).id;//直接用字符串treeId进行比较排序
        return (j1 < j2 ? -1 : (j1 == j2 ? 0 : 1));
    }
}
