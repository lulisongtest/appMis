package com.user

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.app.PingYinUtil
import com.app.Student
import com.listener.SessionListener
import grails.gorm.transactions.Transactional
import groovy.json.JsonBuilder
import groovy.sql.Sql
import org.grails.web.json.JSONObject
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import javax.servlet.http.HttpSession
import java.text.SimpleDateFormat

class UserController extends grails.plugin.springsecurity.ui.UserController {
    def SaveUserService
    def springSecurityService





   //登录用户个人修改用户基本信息
   def readCurrentUser(){
      // println("readCurrentUser")
       User user
       user = User.get(springSecurityService.principal.id)
      // println("readCurrentUser==="+user.truename)
       render(contentType: "application/json") {
           users user.collect() {
               [
                       id              : it.id,
                       username        : it?.username,
                       enabled         : it?.enabled,
                       accountExpired  : it?.accountExpired,
                       accountLocked   : it?.accountLocked,
                       passwordExpired : it?.passwordExpired,
                       truename        : it?.truename,
                       chineseAuthority: it?.chineseAuthority,
                       department      : it?.department,
                       treeId          : it?.treeId,
                       email           : it?.email,
                       phone           : it?.phone,
                       password        : it?.password,
                       regdate         : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.regdate),
                       lastlogindate   : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.lastlogindate)
                       // regdate : it?.regdate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.regdate) : "",
                       // lastlogindate : it?.lastlogindate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.lastlogindate) : "",
               ]
           }
       }
   }
   //增加用户
   @Transactional
   def save() {
       //调用SaveUserService服务，保存不成功时，进行事物回滚
       // //println("save=====================================")
       params.put('oldusername',params.username)
       def infor = SaveUserService.saveUser(params)
       if (infor.information == 'success'){
           render "success"
       }else{
           render "failure"
       }
   }
   //修改用户
   @Transactional
   def update() {
       String oldusername=(User.findById(params.id)).username
       params.put('oldusername',oldusername)
       def infor = SaveUserService.saveUser(params)
       if (infor.information == 'success'){
           render "success"
       }else{
           render "failure"
       }
   }
   //删除用户
   @Transactional
   def delete(){
       if(params.username=="admin"){
           render "failure"
           return
       }
       def userInstance = User.get(params.id)
       def userRoleInstance = UserRole.findByUser(userInstance)
       if (userInstance == null) {
           notFound()
           return
       }
       try{
           if (userRoleInstance){
               userRoleInstance.delete()
           }
           if(userInstance.delete()==null){
               def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
               def sql = new Sql(dataSource)
               String query ="select * from act_id_user where ID_='"+userInstance.username+"'"
               // println("1111delete====query==="+query)
               if (sql.firstRow(query)){
                   //修改update
                   query ="Delete from  `act_id_user` where ID_='"+userInstance.username+"'"
                   //  println("2222delete====query==="+query)
                   if (sql.execute(query)) {
                       println("failure")
                       render "failure"
                       return
                   }else{
                       println("success")
                       render "success"
                       return
                   }
               }else{
                   render "success"
                   return
               }
           }else{
               render "failure"
               return
           }
       }catch(e){
           render "failure"
           return
       }
   }

    //ajax访问读取user信息Android
    def readUserAjax() {
        // println("params.dep_tree_id==qqqq==" + params.dep_tree_id)
        // int len = dep_tree_id.length()
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        // println("dep_tree_id===="+dep_tree_id)
        //def list = Student.createCriteria().list(params) {}
        def list = User.findAll("from User")
        def json1
        if (list.size() == 0) {
            json1 = null
        } else {
            json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        }

        // response.addHeader('Access-Control-Allow-Origin:*');//允许所有来源访问
        // response.addHeader('Access-Control-Allow-Method:POST,GET');//允许访问的方式
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
        response.addHeader("Access-Control-Max-Age", "3600000")
        // 设置允许跨域
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        // 是否允许后续请求携带认证信息（cookies）,该值只能是true,否则不返回
        response.setHeader("Access-Control-Allow-Credentials", "true");

        def jsonBuilder = new JsonBuilder()
        def json2 = jsonBuilder {
            users(list.collect {
                [
                        id              : it.id,
                        username        : it?.username,
                        enabled         : it?.enabled,
                        accountExpired  : it?.accountExpired,
                        accountLocked   : it?.accountLocked,
                        passwordExpired : it?.passwordExpired,
                        truename        : it?.truename,
                        chineseAuthority: it?.chineseAuthority,
                        department      : it?.department,
                        treeId          : it?.treeId,
                        email           : it?.email,
                        phone           : it?.phone,
                        password        : it?.password,
                        regdate : it?.regdate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.regdate) : "null",
                        lastlogindate : it?.lastlogindate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.lastlogindate) : "null",

                ]
            })
        }
        //println("json2===" + json2)
        // println("jsonBuilder===" + jsonBuilder)
        // println("jsonBuilder.getClass()===" + jsonBuilder.getClass())
        // println("json2.getClass()===" + json2.getClass())

        // println("JSON.toJSONString(json2[0])===" + JSON.toJSONString(json2[0]))
        // println("JSON.toJSONString(json2)====" + JSON.toJSONString(json2))//将 JSON 对象或 JSON 数组转化为字符串
        JSONObject obj = JSON.parseObject(jsonBuilder.toPrettyString())//从字符串解析 JSON 对象,com.alibaba.fastjson.JSON
        // println("JSON 对象====obj===" + obj)
        String objStr1 = JSON.toJSONString(obj);//将 JSON 对象或 JSON 数组转化为字符串
        //println("JSON 字符串===objStr1===" + objStr1)

        JSONArray obj1 = obj.get("users")//com.alibaba.fastjson.JSONArray
        //println("JSON 子数组obj1===" + obj1)
        //println("obj1.getClass()===" + obj1.getClass())
        String objStr2 = JSON.toJSONString(obj1);//将 JSON 对象或 JSON 数组转化为字符串
        //println("JSON 子字符串objStr2===" + objStr2)
/*
json2===[students:[[id:88, username:admin, truename:陆立松, email:lulisongtest@63.com, department:null, major:null, college:null, phone:15309923872, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:89, username:yangliehui, truename:杨烈辉, email:lulisongtest123@63.com, department:null, major:null, college:null, phone:18116859252, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:90, username:llsylh, truename:张基数, email:123@456.com, department:null, major:null, college:null, phone:12345678, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:91, username:aser, truename:王定军, email:345@123.com, department:null, major:null, college:null, phone:987654332, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:92, username:zzzz, truename:李昌虽, email:ddd@123.com, department:null, major:null, college:null, phone:222222222, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null]]]
jsonBuilder==={"students":[{"id":88,"username":"admin","truename":"\u9646\u7acb\u677e","email":"lulisongtest@63.com","department":null,"major":null,"college":null,"phone":"15309923872","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":89,"username":"yangliehui","truename":"\u6768\u70c8\u8f89","email":"lulisongtest123@63.com","department":null,"major":null,"college":null,"phone":"18116859252","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":90,"username":"llsylh","truename":"\u5f20\u57fa\u6570","email":"123@456.com","department":null,"major":null,"college":null,"phone":"12345678","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":91,"username":"aser","truename":"\u738b\u5b9a\u519b","email":"345@123.com","department":null,"major":null,"college":null,"phone":"987654332","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":92,"username":"zzzz","truename":"\u674e\u660c\u867d","email":"ddd@123.com","department":null,"major":null,"college":null,"phone":"222222222","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null}]}
jsonBuilder.getClass()===class groovy.json.JsonBuilder
json2.getClass()===class java.util.LinkedHashMap
JSON.toJSONString(json2[0])===null
JSON.toJSONString(json2)===={"students":[{"id":88,"username":"admin","truename":"陆立松","email":"lulisongtest@63.com","phone":"15309923872","birthDate":"null","enrollDate":"null"},{"id":89,"username":"yangliehui","truename":"杨烈辉","email":"lulisongtest123@63.com","phone":"18116859252","birthDate":"null","enrollDate":"null"},{"id":90,"username":"llsylh","truename":"张基数","email":"123@456.com","phone":"12345678","birthDate":"null","enrollDate":"null"},{"id":91,"username":"aser","truename":"王定军","email":"345@123.com","phone":"987654332","birthDate":"null","enrollDate":"null"},{"id":92,"username":"zzzz","truename":"李昌虽","email":"ddd@123.com","phone":"222222222","birthDate":"null","enrollDate":"null"}]}
JSON 对象====obj===[students:[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]]
JSON 字符串===objStr1==={"students":[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]}
JSON 子数组obj1===[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]
obj1.getClass()===class com.alibaba.fastjson.JSONArray
JSON 子字符串objStr2===[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]
 */
        render objStr1
        //render  jsonBuilder.toPrettyString()//也可以

/*//也可以
        render(contentType: "text/json") {
            students list.collect() {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            }
        }
*/
    }

    //ajax访问读取user信息Android
    def readUserAjax1() {
        println("ajax访问读取user信息Android")
        String jsonStr
        if(params.sort!=null){
            jsonStr=params.sort
            jsonStr=jsonStr.substring(1,jsonStr.length()-1)
            JSONObject obj = new JSONObject(jsonStr);
            params.sort=obj.get('property')
            params.order= obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true //  //println( "queryData  params.sort============"+obj.get('property'))
        }
        params.offset = params.start ? params.start as int : 0
        params.max = params.limit ? params.limit as int : 10
        println("params===="+params)

        def queryResult = {
            if((params.queryAuthority)&&(params.queryAuthority != "全部")){
                like("chineseAuthority","%"+params.queryAuthority+"%")
            }
            if((params.queryDepartment)&&(params.queryDepartment != "全部")){
                like("department","%"+params.queryDepartment+"%")
            }
            if(params.queryUsername){
                like("truename","%"+params.queryUsername+"%")
            }
        }
        println("queryResult==="+queryResult)
        def result = User.createCriteria().list(params,queryResult)//这样可以通过params.offse、params.max来控制分页
       // def result = User.findAll("from User")
       // println("result.size()==="+result.size())
        println("result.totalCount==="+result.totalCount)
        render(contentType: "application/json") {
            users result.collect() {
                [
                        id              : it.id,
                        username        : it?.username,
                        enabled         : it?.enabled,
                        accountExpired  : it?.accountExpired,
                        accountLocked   : it?.accountLocked,
                        passwordExpired : it?.passwordExpired,
                        truename        : it?.truename,
                        chineseAuthority: it?.chineseAuthority,
                        department      : it?.department,
                        treeId          : it?.treeId,
                        email           : it?.email,
                        phone           : it?.phone,
                        password        : it?.password,
                        // regdate         : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.regdate),
                        //lastlogindate   : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.lastlogindate)
                        regdate : it?.regdate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.regdate)  : "null",
                        lastlogindate : it?.lastlogindate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.lastlogindate)  : "null",
                ]
            }
            totalCount  result.totalCount
           // totalCount  result.size()
        }
        return





        // println("params.dep_tree_id==qqqq==" + params.dep_tree_id)
        // int len = dep_tree_id.length()
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        // println("dep_tree_id===="+dep_tree_id)
        //def list = Student.createCriteria().list(params) {}
        def list = User.findAll("from User")
        def json1
        if (list.size() == 0) {
            json1 = null
        } else {
            json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        }

        // response.addHeader('Access-Control-Allow-Origin:*');//允许所有来源访问
        // response.addHeader('Access-Control-Allow-Method:POST,GET');//允许访问的方式
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
        response.addHeader("Access-Control-Max-Age", "3600000")
        // 设置允许跨域
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        // 是否允许后续请求携带认证信息（cookies）,该值只能是true,否则不返回
        response.setHeader("Access-Control-Allow-Credentials", "true");

        def jsonBuilder = new JsonBuilder()
        def json2 = jsonBuilder {
            users(list.collect {
                [
                        id              : it.id,
                        username        : it?.username,
                        enabled         : it?.enabled,
                        accountExpired  : it?.accountExpired,
                        accountLocked   : it?.accountLocked,
                        passwordExpired : it?.passwordExpired,
                        truename        : it?.truename,
                        chineseAuthority: it?.chineseAuthority,
                        department      : it?.department,
                        treeId          : it?.treeId,
                        email           : it?.email,
                        phone           : it?.phone,
                        password        : it?.password,
                        regdate : it?.regdate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.regdate) : "null",
                        lastlogindate : it?.lastlogindate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.lastlogindate) : "null",

                ]
            })
        }
        //println("json2===" + json2)
        // println("jsonBuilder===" + jsonBuilder)
        // println("jsonBuilder.getClass()===" + jsonBuilder.getClass())
        // println("json2.getClass()===" + json2.getClass())

        // println("JSON.toJSONString(json2[0])===" + JSON.toJSONString(json2[0]))
        // println("JSON.toJSONString(json2)====" + JSON.toJSONString(json2))//将 JSON 对象或 JSON 数组转化为字符串
        JSONObject obj = JSON.parseObject(jsonBuilder.toPrettyString())//从字符串解析 JSON 对象,com.alibaba.fastjson.JSON
        // println("JSON 对象====obj===" + obj)
        String objStr1 = JSON.toJSONString(obj);//将 JSON 对象或 JSON 数组转化为字符串
        //println("JSON 字符串===objStr1===" + objStr1)

        JSONArray obj1 = obj.get("users")//com.alibaba.fastjson.JSONArray
        //println("JSON 子数组obj1===" + obj1)
        //println("obj1.getClass()===" + obj1.getClass())
        String objStr2 = JSON.toJSONString(obj1);//将 JSON 对象或 JSON 数组转化为字符串
        //println("JSON 子字符串objStr2===" + objStr2)
/*
json2===[students:[[id:88, username:admin, truename:陆立松, email:lulisongtest@63.com, department:null, major:null, college:null, phone:15309923872, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:89, username:yangliehui, truename:杨烈辉, email:lulisongtest123@63.com, department:null, major:null, college:null, phone:18116859252, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:90, username:llsylh, truename:张基数, email:123@456.com, department:null, major:null, college:null, phone:12345678, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:91, username:aser, truename:王定军, email:345@123.com, department:null, major:null, college:null, phone:987654332, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:92, username:zzzz, truename:李昌虽, email:ddd@123.com, department:null, major:null, college:null, phone:222222222, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null]]]
jsonBuilder==={"students":[{"id":88,"username":"admin","truename":"\u9646\u7acb\u677e","email":"lulisongtest@63.com","department":null,"major":null,"college":null,"phone":"15309923872","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":89,"username":"yangliehui","truename":"\u6768\u70c8\u8f89","email":"lulisongtest123@63.com","department":null,"major":null,"college":null,"phone":"18116859252","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":90,"username":"llsylh","truename":"\u5f20\u57fa\u6570","email":"123@456.com","department":null,"major":null,"college":null,"phone":"12345678","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":91,"username":"aser","truename":"\u738b\u5b9a\u519b","email":"345@123.com","department":null,"major":null,"college":null,"phone":"987654332","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":92,"username":"zzzz","truename":"\u674e\u660c\u867d","email":"ddd@123.com","department":null,"major":null,"college":null,"phone":"222222222","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null}]}
jsonBuilder.getClass()===class groovy.json.JsonBuilder
json2.getClass()===class java.util.LinkedHashMap
JSON.toJSONString(json2[0])===null
JSON.toJSONString(json2)===={"students":[{"id":88,"username":"admin","truename":"陆立松","email":"lulisongtest@63.com","phone":"15309923872","birthDate":"null","enrollDate":"null"},{"id":89,"username":"yangliehui","truename":"杨烈辉","email":"lulisongtest123@63.com","phone":"18116859252","birthDate":"null","enrollDate":"null"},{"id":90,"username":"llsylh","truename":"张基数","email":"123@456.com","phone":"12345678","birthDate":"null","enrollDate":"null"},{"id":91,"username":"aser","truename":"王定军","email":"345@123.com","phone":"987654332","birthDate":"null","enrollDate":"null"},{"id":92,"username":"zzzz","truename":"李昌虽","email":"ddd@123.com","phone":"222222222","birthDate":"null","enrollDate":"null"}]}
JSON 对象====obj===[students:[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]]
JSON 字符串===objStr1==={"students":[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]}
JSON 子数组obj1===[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]
obj1.getClass()===class com.alibaba.fastjson.JSONArray
JSON 子字符串objStr2===[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]
 */
        render objStr1
        //render  jsonBuilder.toPrettyString()//也可以

/*//也可以
        render(contentType: "text/json") {
            students list.collect() {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            }
        }
*/
    }




    //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数
    def toparams(params) {
        String jsonStr
        if (params.sort != null) {
            jsonStr = params.sort
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
            JSONObject obj = new JSONObject(jsonStr);
            params.sort = obj.get('property')
            params.order = obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true
        }
        params.offset = params.start ? params.start as int : 0
        params.max = params.limit ? params.limit as int : 25
        return
    }
    //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
    def json(list) {
        SimpleDateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd")
        List<?> users = new ArrayList();
        //从定义的类中取出有效字段数n
        def list1 = User.getDeclaredFields()
        int n = 0
        for (int i = 0; i < list1.size(); i++) if (list1[i].getName() != "constraints") {
            n++
        } else {
            break
        }
        //从定义的类中取出有效字段名
        String[] fieldName = new String[n]
        String[] fieldType = new String[n]
        for (int i = 0; i < n; i++) {
            fieldName[i] = list1[i].getName();
            fieldType[i] = list1[i].getType()
            //println("fieldName["+i+"]===="+fieldName[i]+"--------fieldType["+i+"]===="+fieldType[i]+"====fieldType[i]==java.util.Date===="+(list1[i].getType()==java.util.Date))            //println("fieldName["+i+"]===="+fieldName[i]+"--------fieldType["+i+"]===="+fieldType[i]+"====fieldType[i]==java.util.Date===="+(fieldType[i]=="class java.util.Date"))
        }
        //从数据库中取出数据生成返回的ArrayList数据，以json格式返回
        for (int j = 0; j < (list.size() - 1); j++) {
            Map<String, String> map = new HashMap<>()
            map.put("id", list[j].id)
            for (int i = 0; i < n - 1; i++) {
                if (fieldType[i] == "class java.util.Date") {
                    def t = list[j].(fieldName[i])
                    map.put(fieldName[i], t ? f.format(t) : "")
                } else {
                    map.put(fieldName[i], list[j].(fieldName[i]))
                }
            }
            if (fieldType[n - 1] == "class java.util.Date") {
                def t = list[j].(fieldName[n - 1])
                map.put(fieldName[n - 1], t ? f.format(t) : "")
            } else {
                map.put(fieldName[n - 1], list[j].(fieldName[n - 1]))
            }

            users.add(map)
        }
        Map<String, String> map = new HashMap<>()
        map.put("id", list[list.size() - 1].id)
        for (int i = 0; i < n - 1; i++) {
            if (fieldType[i] == "class java.util.Date") {
                def t = list[list.size() - 1].(fieldName[i])
                map.put(fieldName[i], t ? f.format(t) : "")
            } else {
                map.put(fieldName[i], list[list.size() - 1].(fieldName[i]))
            }
        }
        if (fieldType[n - 1] == "class java.util.Date") {
            def t = list[list.size() - 1].(fieldName[n - 1])
            map.put(fieldName[n - 1], t ? f.format(t) : "")
        } else {
            map.put(fieldName[n - 1], list[list.size() - 1].(fieldName[n - 1]))
        }
        users.add(map)
        return users
    }


   //查询数据
   def readUser(){
       String jsonStr
       if(params.sort!=null){
           jsonStr=params.sort
           jsonStr=jsonStr.substring(1,jsonStr.length()-1)
           JSONObject obj = new JSONObject(jsonStr);
           params.sort=obj.get('property')
           params.order= obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true //  //println( "queryData  params.sort============"+obj.get('property'))
       }
       params.offset = params.start as int
       params.max = params.limit as int
       def queryResult = {
           if((params.queryAuthority)&&(params.queryAuthority != "全部")){
               like("chineseAuthority","%"+params.queryAuthority+"%")
           }
           if((params.queryDepartment)&&(params.queryDepartment != "全部")){
               like("department","%"+params.queryDepartment+"%")
           }
           if(params.queryUsername){
               like("truename","%"+params.queryUsername+"%")
           }
       }
       def result = User.createCriteria().list(params,queryResult)//这样可以通过params.offse、params.max来控制分页
       render(contentType: "application/json") {
           users result.collect() {
               [
                       id              : it.id,
                       username        : it?.username,
                       enabled         : it?.enabled,
                       accountExpired  : it?.accountExpired,
                       accountLocked   : it?.accountLocked,
                       passwordExpired : it?.passwordExpired,
                       truename        : it?.truename,
                       chineseAuthority: it?.chineseAuthority,
                       department      : it?.department,
                       treeId          : it?.treeId,
                       email           : it?.email,
                       phone           : it?.phone,
                       password        : it?.password,
                      // regdate         : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.regdate),
                       //lastlogindate   : new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.lastlogindate)
                        regdate : it?.regdate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.regdate) : new Date(),
                        lastlogindate : it?.lastlogindate ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(it?.lastlogindate) : new Date(),
               ]
           }
           totalCount  result.totalCount
       }

   }

   //改变用户所在单位
   @Transactional
   def changUserDepartment() {
       String subtreeId,department1=""
       if(params.treeId=='1000'){
           department1="全部"
       }else{
           for(int i=0;i<((params.treeId).toString()).length()/3;i++){
               subtreeId=((params.treeId).toString()).substring(0,3+i*3)
               if(department1!="")department1=department1+"=>"
               department1 = department1+(Department.findAll("from Department where treeId='" + subtreeId + "'"))[0].department;
           }
       }
       params.department=department1//单位名称含上级单位名称
       def infor = SaveUserService.saveUser(params)
       if (infor.information == 'success'){
           render "success"
       }else{
           render "failure"
       }
   }

   //初次生成所有单位用户信息
   @Transactional
   def generateUser(){
       String[] chars = ["0", "1", "2", "3", "4", "5", "6","7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K","L", "M", "N", "P",
                         "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","a", "b", "c", "d",
                         "e", "f", "g", "h", "i", "j", "k",  "m", "n", "p", "q", "r",
                         "s", "t", "u", "v", "w", "x", "y", "z"];
       int depSize=5//取单位名称汉字拚音首个字母的个数,depSize+1=5个随机数组成用户名
       String departmentTitled = "",s,firstSpell,username,query
       StringBuffer sb = new StringBuffer();
       User userInstance=new User()
       def deps
       def depList = Department.findAll("from Department where LENGTH(treeId)>6")
       //for(int j=0;j<55;j++){
       for(int j=0;j<depList.size();j++){
           departmentTitled = ""
           s=depList[j].treeId
           sb.delete(0,sb.size())
           for (int i = 1; i < (s.length() / 3); i++) {
               deps = (Department.findAll("from Department where treeId='" + s.substring(0, i * 3) + "'"));
               if (deps.size() > 0) {
                   departmentTitled = departmentTitled + deps[0].department + "==>"
               }
           }
           departmentTitled = departmentTitled + depList[j].department
           // println("departmentTitled详细有上级单位关系的单位名称======"+departmentTitled)
           firstSpell=PingYinUtil.getFirstSpell(depList[j].department)//取单位名称汉字拚音首个字母
           int n=((depSize-firstSpell.size())>0)?(depSize-firstSpell.size()):0
           Random ran = new Random();
           params.password="123456"
           params.email="123@123.123"
           params.department=departmentTitled
           params.phone="0990-123456"
           params.regdate=new Date();
           params.lastlogindate=new Date();
           params.enabled = 1
           params.accountExpired=0
           params.accountLocked=0
           params.passwordExpired=0
           params.treeId=s;
           params.truename=depList[j].contacts?depList[j].contacts:"未知"
           sb= sb.append("-")
           for (int i = 0; i <= (depSize+n); i++) {
               int r = ran.nextInt(chars.length);
               sb.append(chars[r]);// depSize+1=5个随机数
           }
           username=firstSpell.subSequence(0,((firstSpell.size()>=depSize)?depSize:firstSpell.size()))+sb//单位名称汉字拚音首个字母+随机数=9
           // println("生成单位管理员username======"+username)
           //生成单位管理员
           params.username=username
           params.chineseAuthority="单位管理员";
           //String query = "FROM  User where username='" + params.username + "'"
           query = "FROM  User where truename='" + params.truename + "' and chineseAuthority='"+params.chineseAuthority+ "' and treeId='"+params.treeId+"'"

           //println(" query==="+ query)
           def list8 = User.findAll(query)
           // println(" list8.size()===="+ list8.size())
           try{
               if (list8.size() != 0) {
                   //判断是替换
                   userInstance=list8[0]
                   userInstance.setProperties(params)
                   userInstance.save(flush:true)
                   userRoleSave(userInstance)

               } else {
                   //判断是增加
                   userInstance=new User(params)
                   userInstance.save(flush:true)
                   userRoleSave(userInstance)
               }
           } catch (e) {
               render "{success:false,info:'初次生成所有单位用户信息失败！'}"
               return
           }
           sb.delete(0,sb.size())
           params.truename=depList[j].leader?depList[j].leader:"未知"
           sb= sb.append("-")
           for (int i = 0; i <= (depSize+n); i++) {
               int r = ran.nextInt(chars.length);
               sb.append(chars[r]);// depSize+1=5个随机数
           }

           username=firstSpell.subSequence(0,((firstSpell.size()>=depSize)?depSize:firstSpell.size()))+sb//单位名称汉字拚音首个字母+随机数=9
           // println("生成单位审核人username======"+username)
           //生成单位审核人
           params.username=username
           params.chineseAuthority="单位审核人";
           query = "FROM  User where truename='" + params.truename + "' and chineseAuthority='"+params.chineseAuthority+ "' and treeId='"+params.treeId+"'"
           list8 = User.findAll(query)
           try{
               if (list8.size() != 0) {
                   //判断是替换
                   userInstance=list8[0]
                   userInstance.setProperties(params)
                   userInstance.save(flush:true)
                   userRoleSave(userInstance)
               } else {
                   //判断是增加
                   userInstance=new User(params)
                   userInstance.save(flush:true)
                   userRoleSave(userInstance)
               }
           } catch (e) {
               render "{success:false,info:'初次生成所有单位用户信息失败！'}"
               return
           }

           sb.delete(0,sb.size())            // println("treeId======"+s)           // println("departmentTitled详细有上级单位关系的单位名称======"+departmentTitled)// println("sb======"+sb)// println("getFullSpell======"+firstSpell)// println("getFullSpell======"+firstSpell.subSequence(0,((firstSpell.size()>=depSize)?depSize:firstSpell.size())))
           deps = (Department.findAll("from Department where substring(treeId, 1,(LENGTH(treeId)-3))='" + s + "'"));
           sb= sb.append("-")
           if(deps.size()>1){
               for (int i = 0; i <= (depSize+n); i++) {
                   int r = ran.nextInt(chars.length);
                   sb.append(chars[r]);// depSize+1=5个随机数
               }
               username=firstSpell.subSequence(0,((firstSpell.size()>=depSize)?depSize:firstSpell.size()))+sb//单位名称汉字拚音首个字母+随机数=9
               // println("=====主管单位审核人username======"+username)
               //生成主管单位审核人
               params.username=username
               params.chineseAuthority="主管单位审核人";
               query = "FROM  User where truename='" + params.truename + "' and chineseAuthority='"+params.chineseAuthority+ "' and treeId='"+params.treeId+"'"
               list8 = User.findAll(query)
               try{
                   if (list8.size() != 0) {
                       //判断是替换
                       userInstance=list8[0]
                       userInstance.setProperties(params)
                       userInstance.save(flush:true)
                       userRoleSave(userInstance)
                   } else {
                       //判断是增加
                       userInstance=new User(params)
                       userInstance.save(flush:true)
                       userRoleSave(userInstance)
                   }
               } catch (e) {
                   render "{success:false,info:'初次生成所有单位用户信息失败！'}"//Json字符串
                   return
               }
           }
       }

       //同时用系统用户更新（增加）Activiti的用户信息
       def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
       def sql = new Sql(dataSource)
       query ="Delete  from  `act_id_user` where ID_<>'admin' "
       sql.execute(query)
       query = "INSERT INTO `act_id_user`  (ID_, LAST_)  SELECT username, truename  FROM `user`  WHERE username<>'admin'"
       if (sql.execute(query)) {
           render "{success:false,info:'初次生成所有单位用户信息（Activiti的用户信息）失败！'}"//Json字符串
           return
       }
       query = "Update  `act_id_user`  set  PWD_='123'"
       if (sql.execute(query)) {
           render "{success:false,info:'初次生成所有单位用户信息（Activiti的用户信息）失败！'}"//Json字符串
           return
       }
       render "{success:true,info:'初次生成所有单位用户信息成功！'}"//Json字符串
       return;
   }

   def userRoleSave(userInstance){
       def userRoleInstance = UserRole.findByUser(userInstance)
       if(userRoleInstance){
           //已有这条记录，先删除，后面再新建,应放在userInstance.save(flush: true)后再做删除才有效，否则无效！
            userRoleInstance.delete(flush: true)
           //println("00000 old userRoleInstance==save==success-------------------")
       }
           userRoleInstance = new UserRole()
           if(userInstance.chineseAuthority ==null){
               userRoleInstance.role = Role.findByAuthority("ROLE_USER")
           }else{
               userRoleInstance.role = Role.findByChineseAuthority(userInstance.chineseAuthority)
           }
           userRoleInstance.user = User.findById(userInstance.id)
           userRoleInstance.save(flush: true)
           //println("00000 new  userRoleInstance==save==success-------------------")

   }





}
