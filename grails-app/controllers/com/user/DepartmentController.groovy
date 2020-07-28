package com.user



import com.app.PingYinUtil
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.grails.web.json.JSONObject

import java.text.SimpleDateFormat


@Transactional(readOnly = true)
class DepartmentController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    // ArrayList<Employee> list3 = new ArrayList<Employee>();//可以用于打印
    def scaffold = Department;
   //单位列表
   def readDepartmentQuery(){
       ArrayList<Map> list2 = new ArrayList<Map>();
       String query = 'from Department where LENGTH(treeId)>3  order by treeId ASC';
       def list1 = Department.findAll(query)
       list2.add([department:'全部',treeId:"000"])
       for(int i=0;i<list1.size();i++){
           list2.add([department:list1[i].department,treeId:list1[i].treeId])
       }
       render(contentType: "text/json") {
           departments   list2.collect() {
               [
                       department    : it.department,
                       treeId        : it.treeId
               ]
           }
       }
   }
    //获取详细有上级单位关系的单位名称及本级单位的详细信息
    def readDepartmentTitle = {
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        ////println("params.dep_tree_id=====readDepartmentTitle========="+dep_tree_id)
        String departmentTitled = ""
        String s = dep_tree_id
        Department department1 = new Department()
        for (int i = 1; i < (s.length() / 3); i++) {
            def deps = (Department.findAll("from Department where treeId='" + s.substring(0, i * 3) + "'"));
            if(deps.size()>0){
                department1=deps[0]
                departmentTitled = departmentTitled + department1.department + "==>"
            }
        }
        render departmentTitled//本级单位的详细信息
        return
    }

    //查看申报事务的情况
    def readDepartmentSbStatus = {
        //println("params.procDefId===="+params.procDefId)
        //println("params.procDefId===="+params.procDefId)
        //println("params.max====="+params.max)
        //println("params.offset====="+params.offset)
        if((!(params.procDefId))||params.procDefId=="all"){
            render(contentType: "text/json") {
                departments   null
                totalCount   0
            }
            return
        }
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:"000"
        ArrayList<?> list3 = new ArrayList<>();


        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar currentProcessDate = Calendar.getInstance();
        currentProcessDate.setTime(params.currentProcessDate ? sdf.parse(params.currentProcessDate + " 00:00:00") : new Date());
        int currentProcessDateYear = currentProcessDate.get(Calendar.YEAR);//获取年份
        int currentProcessDateMonth = currentProcessDate.get(Calendar.MONTH)+1;//获取月份
        String yearmonth,query,status
        if(currentProcessDateMonth>9){
            yearmonth = "" + currentProcessDateYear + "" + currentProcessDateMonth
        }else{
            yearmonth = "" + currentProcessDateYear + "0" + currentProcessDateMonth
        }

        /* println("params.dep_tree_id=====readDepartment========="+params.dep_tree_id)
         println("params.currentProcessDate=====readDepartment========="+params.currentProcessDate)
         println("params.procDefId=====readDepartment========="+params.procDefId)
         println("params.department=====readDepartment========="+params.department)
         println("params.status=====readDepartment========="+params.status)
         println("currentProcessDateYear==="+currentProcessDateYear)
         println("currentProcessDateMonth==="+currentProcessDateMonth)*/

        query = 'from Department';
        String querySize = "SELECT COUNT(*) AS TOTAL FROM `department`"//取记录数
        String jsonStr
        /* if (params.sort != null) {
             jsonStr = params.sort
             jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
             JSONObject obj = new JSONObject(jsonStr);
             params.sort = obj.get('property')
             params.order = obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true
         }*/
        params.offset = params.start ? params.start as int : 0
        params.max = params.limit ? params.limit as int : 4

        def list1
        if ((dep_tree_id == '1000')||(dep_tree_id == '000')){
            query = query + " where LENGTH(treeId)>6"//全部单位信息
            querySize = querySize + " where LENGTH(tree_id)>6"//全部单位信息
        } else {
            if (dep_tree_id.length() <= 6) {
                query = query + " where ((substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "') AND  (LENGTH(treeId)>6))"//各区或分类全部单位信息
                querySize = querySize  + " where ((substring(tree_id, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "') AND  (LENGTH(tree_id)>6))"
            } else {
                //query = query + " where substring(treeId, 1,(LENGTH(treeId)-3))='" + dep_tree_id + "'"//正常查询 //正常查询
                query = query + " where treeId='" + dep_tree_id + "' OR substring(treeId, 1,(LENGTH(treeId)-3))='" + dep_tree_id + "'"//正常查询 //正常查询
                querySize = querySize +" where tree_id='" + dep_tree_id + "' OR substring(tree_id, 1,(LENGTH(tree_id)-3))='" + dep_tree_id + "'"
            }
        }

        // println("query=====readDepartment========="+query)
        // int dep_size = (Department.findAll(query)).size()
        int dep_size = sql.rows(querySize)[0].TOTAL//取总记录数
        // query_read = query  //使用从 def readArticles 中获取的query查询结果,也可以用于打印或导出Excel文件
        //if (params.sort != null) query = query + " order by " + params.sort
        // if (params.order != null) query = query + " " + params.order
        list1 = Department.findAll(query, [max: params.max, offset: params.offset])
        //println("params.max====="+params.max)
        // println("params.offset====="+params.offset)
        // println("query==dep====="+query)
        // println("list1.size()==dep====="+list1.size())
        def list2,list4
        // Map<String,String> list5 = new IdentityHashMap<>()
        for(int i=0;i<list1.size();i++){
            // println("i====="+i+"======list1[i].department====="+list1[i].department)
            // list4[i]= new IdentityHashMap<>()
            // query = "SELECT *  from act_hi_taskinst where PROC_DEF_ID_='" + params.procDefId + "' AND ASSIGNEE_ LIKE '%"+yearmonth+":%' AND ASSIGNEE_ LIKE '%"+list1[i].treeId+":%'  AND (DELETE_REASON_ IS NULL)  group by PROC_INST_ID_"
            query = "SELECT *  from act_hi_taskinst where PROC_DEF_ID_='" + params.procDefId + "' AND ASSIGNEE_ LIKE '%"+list1[i].treeId+yearmonth+":%' AND (DELETE_REASON_ IS NULL)  group by PROC_INST_ID_"
            // println("query======="+query)
            list4 = sql.rows(query)
            if(list4.size()==0){
                Map<String, String> map = new HashMap<>();
                map.put("department",(list1[i].department))
                map.put("shr0","没有申报此业务")
                list3.add(map)
            }
            // println("list4.size()======="+list4.size())
            for(int p=0;p<list4.size();p++){
                query = "SELECT *  from act_hi_taskinst where PROC_INST_ID_='" + list4[p].PROC_INST_ID_ + "' order by START_TIME_  ASC"
                // println("query======="+query)
                list2 = sql.rows(query)
                // println("list2.size()======="+list2.size())
                if(list2.size()>0){
                    list2[0].put("department", list1[i].department)
                    status=(list2[0].ASSIGNEE_).split(":")[-1]
                    if(list2[0].END_TIME_ ){
                        if(list2[0].DELETE_REASON_){
                            status=status+"_已审核不通过"
                        }else{
                            status=status+"_已审核通过"
                        }
                    }else{
                        status=status+"_等待审核中"
                    }
                    list2[0].put("shr0", status)

                    if(list2[1]){
                        status=(list2[1].ASSIGNEE_).split(":")[-1]
                        if(list2[1].END_TIME_ ){
                            if(list2[1].DELETE_REASON_){
                                status=status+"_已审核不通过"
                            }else{
                                status=((status=="无")?status:(status+"_已审核通过"))
                            }
                        }else{
                            status=status+"_等待审核中"
                        }
                        list2[0].put("shr1", status)
                    }else{
                        list2[0].put("shr1", "")
                    }

                    if(list2[2]){
                        status=(list2[2].ASSIGNEE_).split(":")[-1]
                        if(list2[2].END_TIME_ ){
                            if(list2[2].DELETE_REASON_){
                                status=status+"_已审核不通过"
                            }else{
                                status=((status=="无")?status:(status+"_已审核通过"))
                            }
                        }else{
                            status=status+"_等待审核中"
                        }
                        list2[0].put("shr2", status)
                    }else{
                        list2[0].put("shr2", "")
                    }

                    if(list2[3]){
                        status=(list2[3].ASSIGNEE_).split(":")[-1]
                        if(list2[3].END_TIME_ ){
                            if(list2[3].DELETE_REASON_){
                                status=status+"_已审核不通过"
                            }else{
                                status=((status=="无")?status:(status+"_已审核通过"))
                            }
                        }else{
                            status=status+"_等待审核中"
                        }
                        list2[0].put("shr3", status)
                    }else{
                        list2[0].put("shr3", "")
                    }

                    if(list2[4]){
                        status=(list2[4].ASSIGNEE_).split(":")[-1]
                        if(list2[4].END_TIME_ ){
                            if(list2[4].DELETE_REASON_){
                                status=status+"_已审核不通过"
                            }else{
                                status=((status=="无")?status:(status+"_已审核通过"))
                            }
                        }else{
                            status=status+"_等待审核中"
                        }
                        list2[0].put("shr4", status)
                    }else{
                        list2[0].put("shr4", "")
                    }

                    if(list2[5]){
                        status=(list2[5].ASSIGNEE_).split(":")[-1]
                        if(list2[5].END_TIME_ ){
                            if(list2[5].DELETE_REASON_){
                                status=status+"_已审核不通过"
                            }else{
                                status=((status=="无")?status:(status+"_已审核通过"))
                            }
                        }else{
                            status=status+"_等待审核中"
                        }
                        list2[0].put("shr5", status)
                    }else{
                        list2[0].put("shr5", "")
                    }

                    if(list2[6]){
                        status=(list2[6].ASSIGNEE_).split(":")[-1]
                        if(list2[6].END_TIME_ ){
                            if(list2[6].DELETE_REASON_){
                                status=status+"_已审核不通过"
                            }else{
                                status=((status=="无")?status:(status+"_已审核通过"))
                            }
                        }else{
                            status=status+"_等待审核中"
                        }
                        list2[0].put("shr6", status)
                    }else{
                        list2[0].put("shr6", "")
                    }
                    /*list2[1]?(list2[0].put("shr1", (list2[1].ASSIGNEE_).split(":")[-1])):list2[0].put("shr1", "")
                    list2[2]?(list2[0].put("shr2", (list2[2].ASSIGNEE_).split(":")[-1])):list2[0].put("shr2", "")
                    list2[3]?(list2[0].put("shr3", (list2[3].ASSIGNEE_).split(":")[-1])):list2[0].put("shr3", "")
                    list2[4]?(list2[0].put("shr4", (list2[4].ASSIGNEE_).split(":")[-1])):list2[0].put("shr4", "")
                    list2[5]?(list2[0].put("shr5", (list2[5].ASSIGNEE_).split(":")[-1])):list2[0].put("shr5", "")
                    list2[6]?(list2[0].put("shr6", (list2[6].ASSIGNEE_).split(":")[-1])):list2[0].put("shr6", "")*/
                }
                if(list2.size()>0){
                    list3.add(list2[0])
                }
            }
        }
        int num=0
        switch(params.status){
            case "全部":
                break;
            case "等待审核中":
                for(int i = 0;i < list3.size(); i++){
                    //println(list3.get(i).get("department"));
                    if((list3.get(i).get("shr0"))!="没有申报此业务"){
                        if(((list3.get(i).get("shr0")).indexOf("等待审核中"))==-1 && ((list3.get(i).get("shr1")).indexOf("等待审核中"))==-1 && ((list3.get(i).get("shr2")).indexOf("等待审核中"))==-1 && ((list3.get(i).get("shr3")).indexOf("等待审核中"))==-1 && ((list3.get(i).get("shr4")).indexOf("等待审核中"))==-1 && ((list3.get(i).get("shr5")).indexOf("等待审核中"))==-1 && ((list3.get(i).get("shr6")).indexOf("等待审核中"))==-1){
                            list3.remove(i--); num++;
                        }
                    }else{
                        list3.remove(i--); num++;
                    }
                }
                break;
            case "审核不通过":
                for(int i = 0;i < list3.size(); i++){
                    //println(list3.get(i).get("department"));
                    if((list3.get(i).get("shr0"))!="没有申报此业务"){
                        if(((list3.get(i).get("shr0")).indexOf("审核不通过"))==-1 && ((list3.get(i).get("shr1")).indexOf("审核不通过"))==-1 && ((list3.get(i).get("shr2")).indexOf("审核不通过"))==-1 && ((list3.get(i).get("shr3")).indexOf("审核不通过"))==-1 && ((list3.get(i).get("shr4")).indexOf("审核不通过"))==-1 && ((list3.get(i).get("shr5")).indexOf("审核不通过"))==-1 && ((list3.get(i).get("shr6")).indexOf("审核不通过"))==-1){
                            list3.remove(i--); num++;
                        }
                    }else{
                        list3.remove(i--); num++;
                    }
                }
                break;
            case "全部审核通过":
                for(int i = 0;i < list3.size(); i++){
                    //println(list3.get(i).get("department"));
                    if((list3.get(i).get("shr0"))!="没有申报此业务"){
                        if((list3.get(i).get("shr6"))==""){
                            if(((list3.get(i).get("shr3")).indexOf("审核通过"))==-1){
                                list3.remove(i--); num++;
                            }
                        }else{
                            if(((list3.get(i).get("shr6")).indexOf("审核通过"))==-1){
                                list3.remove(i--); num++;
                            }
                        }
                    }else{
                        list3.remove(i--); num++;
                    }
                }
                break;
            case "没有申报此业务":
                for(int i = 0;i < list3.size(); i++){
                    //println(list3.get(i).get("department"));
                    if((list3.get(i).get("shr0"))!="没有申报此业务"){
                        list3.remove(i--); num++;
                    }
                }
                break;
        }
        // println("dep_size===="+dep_size)
        // println("num===="+num)

        render(contentType: "text/json") {
            departments   list3.collect() {
                [
                        //id            : it.id,
                        department    : (it.department)?(it.department):'',
                        PROC_NAME_    : "",
                        shra        : (it.shr0)?(it.shr0):'',
                        shrb        :  (it.shr1)?(it.shr1):'',
                        shrc         :  (it.shr2)?(it.shr2):'',
                        shrd         :  (it.shr3)?(it.shr3):'',
                        shre        :  (it.shr4)?(it.shr4):'',
                        shrf        :  (it.shr5)?(it.shr5):'',
                        shrg        :  (it.shr6)?(it.shr6):'',

                ]
            }
            totalCount   (dep_size-num).toString()  //totalCount = Department.count().toString()
        }
    }

   //DepartmentStore.js的初始值，//获取下级单位的详细信息
    def readDepartment = {
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        // println("params.dep_tree_id=====readDepartment========="+params.dep_tree_id)
        if(!(params.dep_tree_id)){
            // println("params.dep_tree_id=====readDepartment=====退出===="+params.dep_tree_id)
            render(contentType: "text/json") {
                departments ""
                totalCount   0  //totalCount = Department.count().toString()
            }
            return
        }
        String query = 'from Department';
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
        if (dep_tree_id == '1000') {
            query = query + " where LENGTH(treeId)>6"//全部单位信息
        } else {
            if (dep_tree_id.length() <= 6) {
                query = query + " where ((substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "') AND  (LENGTH(treeId)>6))"//各区或分类全部单位信息
            } else {
                query = query + " where substring(treeId, 1,(LENGTH(treeId)-3))='" + dep_tree_id + "'"//正常查询 //正常查询
            }
        }
        int dep_size = (Department.findAll(query)).size()
        // query_read = query  //使用从 def readArticles 中获取的query查询结果,也可以用于打印或导出Excel文件
        if (params.sort != null) query = query + " order by " + params.sort
        if (params.order != null) query = query + " " + params.order

        list1 = Department.findAll(query, [max: params.max, offset: params.offset])
        println("query==dep====="+query)
        println("list1==dep====="+list1.size())
        render(contentType: "text/json") {
            departments   list1.collect() {
                [
                        id            : it.id,
                        department    : it.department,
                        dwjc          : it.dwjc,
                        dwtjsl        : it.dwtjsl,
                        gzzescbm      : it.gzzescbm,
                        departmentCode: it.departmentCode,
                        lsgx          : it.lsgx,
                        lsxt          : it.lsxt,
                        xzhq          : it.xzhq,
                        dwxz          : it.dwxz,
                        czbkxs        : it.czbkxs,
                        jblq          : it.jblq,
                        dwjb          : it.dwjb,
                        sshy          : it.sshy,
                        cgqk          : it.cgqk,
                        zgbmqk        : it.zgbmqk,
                        sjgzzgbm      : it.sjgzzgbm,
                        bzs           : it.bzs,
                        syrs          : it.syrs,
                        bzqk          : it.bzqk,
                        bzpwqk        : it.bzpwqk,
                        hsxz          : it.hsxz,
                        leader        : it.leader,
                        contacts      : it.contacts,
                        phone         : it.phone,
                        fax           : it.fax,
                        postcode      : it.postcode,
                        address       : it.address,
                        treeId        : it.treeId,
                        glyph         : it.glyph
                ]
            }
            totalCount   dep_size.toString()  //totalCount = Department.count().toString()
        }
    }
//=========================================================









    def listAsJson = {
        def list1 = Department.findAll();
        render list1 as JSON;
    }
    //单位下拉菜单使用
    def  readDepartmentNames2(){
        // //println("params.dep_tree_id=====readDepartment========="+dep_tree_id)
        ArrayList<Map> list2 = new ArrayList<Map>();
        String query = 'from Department where LENGTH(treeId)>3  order by treeId ASC';
        def list1 = Department.findAll(query)
        list2.add([department:'全部',syrs:""])
        for(int i=0;i<list1.size();i++){
            list2.add([department:list1[i].department])
        }
        render(contentType: "application/json") {
            departments list2.collect() {
                [
                        department: it?.department,
                ]
            }
        }
    }

    def listDepartmentcombobox = {//用于下拉列表
        String query = 'from Department where LENGTH(treeId)>6  order by department DESC'
//分组去掉重复字段，调试期间有重复字段，最后应该没有重复字段。
        def list1 = Department.findAll(query);
        if (list1.size() > 0) {
            Department all1 = new Department()//把[all、全部]放在第一，原来第一个记录信息放在最后
            all1.department = list1[0].department
            list1.add(all1)
            list1[0].department = "全部"
        }
        render(contentType: "text/json") {
            departments   list1.collect() {
                [
                        id            : it.id,
                        department    : it.department,
                        dwjc          : it.dwjc,
                        dwtjsl        : it.dwtjsl,
                        gzzescbm      : it.gzzescbm,
                        departmentCode: it.departmentCode,
                        lsgx          : it.lsgx,
                        lsxt          : it.lsxt,
                        xzhq          : it.xzhq,
                        dwxz          : it.dwxz,
                        czbkxs        : it.czbkxs,
                        jblq          : it.jblq,
                        dwjb          : it.dwjb,
                        sshy          : it.sshy,
                        cgqk          : it.cgqk,
                        zgbmqk        : it.zgbmqk,
                        sjgzzgbm      : it.sjgzzgbm,
                        bzs           : it.bzs,
                        syrs          : it.syrs,
                        bzqk          : it.bzqk,
                        bzpwqk        : it.bzpwqk,
                        hsxz          : it.hsxz,
                        leader        : it.leader,
                        contacts      : it.contacts,
                        phone         : it.phone,
                        fax           : it.fax,
                        postcode      : it.postcode,
                        address       : it.address,
                        treeId        : it.treeId,
                        glyph         : it.glyph
                ]
            }
            totalCount   Department.count().toString()
        }
    }


//获取详细有上级单位关系的单位名称及本级单位的详细信息
    def readDepartmentFullName = {
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        String departmentFullName = ""
        String s = dep_tree_id
        Department department1 = new Department()
        for (int i = 1; i < (s.length() / 3); i++) {
            def deps = (Department.findAll("from Department where treeId='" + s.substring(0, i * 3) + "'"));
            if (deps.size() > 0) {
                department1 = deps[0]
                departmentFullName = departmentFullName + department1.department + "==>"
            }
        }
        departmentFullName = departmentFullName+Department.findAll("from Department where treeId='" +dep_tree_id+"'")[0].department
        render(contentType: "text/json") {
            departmentFullNames   departmentFullName//详细有上级单位关系的单位名称
        }
        return
    }

    def readDepartmentNameByTreeId = {//获取本级单位的详细名称
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        String departmentName = ""
        def deps = (Department.findAll("from Department where treeId='" + dep_tree_id + "'"));
        if (deps.size() > 0) {
            departmentName = deps[0].department
        }
        render(contentType: "text/json") {
            departmentNames   departmentName//详细有上级单位关系的单位名称
        }
        return
    }


    def readDepartmentByTreeId = {//获取详细有上级单位关系的单位名称及本级单位的详细信息
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
     //println("dep_tree_id===="+dep_tree_id)
       // String dep_tree_id='001001001'
       // Department department1 = new Department()
        def deps = Department.findAll("from Department where treeId='" + dep_tree_id + "'")
        //if (deps.size() > 0) {
        //    department1 = deps[0]
       // }
        //java向前台传一个记录对象DepartmentMainController 。前台 var obj1 = eval('(' + resp.responseText + ')');currentDepartment1 = (obj1.department2)[0];//currentDepartment1就是这个对象（department1）
      render(contentType: "text/json") {
            department2  deps[0].collect() {
                [
                        id            : it.id,
                        department    : it.department,
                        dwjc          : it.dwjc,
                        dwtjsl        : it.dwtjsl,
                        gzzescbm      : it.gzzescbm,
                        departmentCode: it.departmentCode,
                        lsgx          : it.lsgx,
                        lsxt          : it.lsxt,
                        xzhq          : it.xzhq,
                        dwxz          : it.dwxz,
                        czbkxs        : it.czbkxs,
                        jblq          : it.jblq,
                        dwjb          : it.dwjb,
                        sshy          : it.sshy,
                        cgqk          : it.cgqk,
                        zgbmqk        : it.zgbmqk,
                        sjgzzgbm      : it.sjgzzgbm,
                        bzs           : it.bzs,
                        syrs          : it.syrs,
                        bzqk          : it.bzqk,
                        bzpwqk        : it.bzpwqk,
                        hsxz          : it.hsxz,
                        leader        : it.leader,
                        contacts      : it.contacts,
                        phone         : it.phone,
                        fax           : it.fax,
                        postcode      : it.postcode,
                        address       : it.address,
                        treeId        : it.treeId,
                        glyph         : it.glyph
                ]
            }
        }
         return
    }

    def readDepartmentById = {//获取详细有上级单位关系的单位名称
        Department department1 = Department.findById(params.id)
        render(contentType: "text/json") {
            department2   department1
        }
        return
    }

    def readDepartmentChart = {
        // println("params.dep_tree_id=====readDepartment========="+dep_tree_id)
        String query = 'from Department where LENGTH(treeId)>3  order by treeId ASC';
        def list1 = Department.findAll(query)
        render(contentType: "text/json") {
            list1.collect() {
                [
                        department: it?.department,
                        syrs      : it?.syrs,
                ]
            }
            // totalCount = Department.count().toString()
        }
    }





    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Department.list(params), model: [departmentInstanceCount: Department.count()]
    }

    def show(Department departmentInstance) {
        respond departmentInstance
    }

    def create() {
        respond new Department(params)
    }


    def edit(Department departmentInstance) {
        respond departmentInstance
    }


    @Transactional
    def save() {
        println("save Department!!!")
        String[] chars = ["0", "1", "2", "3", "4", "5", "6","7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K","L", "M", "N", "P",
                          "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","a", "b", "c", "d",
                          "e", "f", "g", "h", "i", "j", "k",  "m", "n", "p", "q", "r",
                          "s", "t", "u", "v", "w", "x", "y", "z"];
        int depSize=5//取单位名称汉字拚音首个字母的个数,depSize+1=5个随机数组成用户名

        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        String s = dep_tree_id//dep_tree_id是这个类的静态变量，记录点击树状菜单上的某个单位的tree_id，
        //以下是生成新增单位的tree_id,保证treeId是按顺序由小到大设置
        String new_treeId = ""//新增单位的单位码
        String query = "from Department where substring(treeId, 1,(LENGTH(treeId)-3))='" + s + "' order by treeId"
        def list1 = Department.findAll(query)
        if (list1.size() != 0) {
            int j = 0
            for (int i = 0; i < list1.size(); i++) {
                j = Integer.valueOf((list1[i].treeId).substring((list1[i].treeId).size() - 3))
                if (j != (i + 1)) {
                    j = j - 2; break;
                }
            }
            // new_treeId=(list1[0].treeId).substring(0,(list1[0].treeId).size()-3)
            new_treeId = dep_tree_id
            j = j + 1
            if (j <= 9) {
                new_treeId = new_treeId + "00" + j
            } else {
                if (j <= 99) {
                    new_treeId = new_treeId + "0" + j
                } else {
                    new_treeId = new_treeId + j
                }
            }
        } else {
            new_treeId = dep_tree_id + "001"
        }
        params.treeId = new_treeId
        def departmentInstance = new Department(params)
        if (departmentInstance == null) {
            notFound()
            return
        }
        if (departmentInstance.hasErrors()) {
            respond departmentInstance.errors, view: 'create'
            return
        }

        try {
            //departmentInstance.save flush:true
           if (departmentInstance.save(flush:true) != null) {
               println("新增单位的单位码new_treeId===="+new_treeId)
                //增加User、act_id_user中的真实姓名
                //增加Activiti中的act_id_user的Last_(真实姓名)
               //取详细单位名称
                String departmentTitled = ""
                for (int i = 1; i < (new_treeId.length() / 3); i++) {
                    def deps = (Department.findAll("from Department where treeId='" + new_treeId.substring(0, i * 3) + "'"));
                    if (deps.size() > 0) {
                        departmentTitled = departmentTitled + deps[0].department + "==>"
                    }
                }
                departmentTitled = departmentTitled + departmentInstance.department
                StringBuffer sb = new StringBuffer();
                User userInstance=new User()
                String usernamegly,usernameshr
                //生成管理员用户的用户usernamegly
                String firstSpell= PingYinUtil.getFirstSpell(departmentInstance.department)//取单位名称汉字拚音首个字母
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
                params.treeId=new_treeId;
                params.truename=departmentInstance.contacts?departmentInstance.contacts:"未知"
                sb= sb.append("-")
                for (int j = 0; j <= (depSize+n); j++) {
                    int r = ran.nextInt(chars.length);
                    sb.append(chars[r]);// depSize+1=5个随机数
                }
               usernamegly=firstSpell.subSequence(0,((firstSpell.size()>=depSize)?depSize:firstSpell.size()))+sb//单位名称汉字拚音首个字母+随机数=9
                params.username=usernamegly
                params.chineseAuthority="单位管理员";
                query = "FROM  User where truename='" + params.truename + "' and chineseAuthority='单位管理员' and treeId='"+params.treeId+"'"
                def list8 = User.findAll(query)
                    if (list8.size() != 0) {
                        //判断是替换
                        userInstance=list8[0]
                        userInstance.setProperties(params)
                        userInstance.save(flush:true)
                    } else {
                        //判断是增加
                        userInstance=new User(params)
                        userInstance.save(flush:true)
                    }
               //生成审核员用户的用户usernameshr
                sb.delete(0,sb.size())
                User userInstance1=new User()
                firstSpell= PingYinUtil.getFirstSpell(departmentInstance.department)//取单位名称汉字拚音首个字母
                n=((depSize-firstSpell.size())>0)?(depSize-firstSpell.size()):0
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
               params.treeId=new_treeId;
               params.truename=departmentInstance.leader?departmentInstance.leader:"未知"
               sb= sb.append("-")
               for (int j = 0; j <= (depSize+n); j++) {
                   int r = ran.nextInt(chars.length);
                   sb.append(chars[r]);// depSize+1=5个随机数
               }
               usernameshr=firstSpell.subSequence(0,((firstSpell.size()>=depSize)?depSize:firstSpell.size()))+sb//单位名称汉字拚音首个字母+随机数=9
               println("单位审核员username===="+usernameshr)
               params.username=usernameshr
               params.chineseAuthority="单位审核人";
               query = "FROM  User where truename='" + params.truename + "' and chineseAuthority='单位审核人' and treeId='"+params.treeId+"'"
               list8 = User.findAll(query)
               if (list8.size() != 0) {
                   //判断是替换
                   userInstance1=list8[0]
                   userInstance1.setProperties(params)
                   userInstance1.save(flush:true)
               } else {
                   //判断是增加
                   userInstance1=new User(params)
                   userInstance1.save(flush:true)
               }
               //增加activiti用户
                def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
                def sql = new Sql(dataSource)
                query ="INSERT INTO act_id_user  (ID_,PWD_,LAST_)  VALUES ('"+usernamegly+"','123','"+departmentInstance.contacts+"')"
                sql.execute(query)
                query ="INSERT INTO act_id_user (ID_,PWD_,LAST_) VALUES ('"+usernameshr+"','123','"+departmentInstance.leader+"')"
                sql.execute(query)
                render "success"
                return
            } else {
                // response.getWriter().write("failure0");//也可以
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
        println("update Department!!!")
        String[] chars = ["0", "1", "2", "3", "4", "5", "6","7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "J", "K","L", "M", "N", "P",
                          "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z","a", "b", "c", "d",
                          "e", "f", "g", "h", "i", "j", "k",  "m", "n", "p", "q", "r",
                          "s", "t", "u", "v", "w", "x", "y", "z"];
        int depSize=5//取单位名称汉字拚音首个字母的个数,depSize+1=5个随机数组成用户名
        Department departmentInstance = Department.findById(params.id)
        String oldDepartment=departmentInstance.department
        String newDepartment=params.department
        println("oldDepartment=="+oldDepartment)
        println("newDepartment=="+newDepartment)
        departmentInstance.setProperties(params)
        if (departmentInstance == null) {
            // println("notFound")
            render "failure"
            return
        }
        if (departmentInstance.hasErrors()) {
            render "failure"
            return
        }
        try {
            if (departmentInstance.save(flush:true) != null) {
                String departmentTitled = ""
                for (int i = 1; i < ((departmentInstance.treeId).length() / 3); i++) {
                    def deps = (Department.findAll("from Department where treeId='" + (departmentInstance.treeId).substring(0, i * 3) + "'"));
                    if (deps.size() > 0) {
                        departmentTitled = departmentTitled + deps[0].department + "==>"
                    }
                }
                departmentTitled = departmentTitled + departmentInstance.department
                //更新User、act_id_user中的真实姓名
                //修改Activiti中的act_id_user的Last_(真实姓名)
                StringBuffer sb = new StringBuffer();
                String usernamegly,usernameshr,query
                Random ran = new Random();
                //如单位名称变动，则重新生成管理员用户的用户usernamegly
               if(oldDepartment!=newDepartment){
                   String firstSpell= PingYinUtil.getFirstSpell(departmentInstance.department)//取单位名称汉字拚音首个字母
                   int n=((depSize-firstSpell.size())>0)?(depSize-firstSpell.size()):0
                   sb= sb.append("-")
                   for (int j = 0; j <= (depSize+n); j++) {
                       int r = ran.nextInt(chars.length);
                       sb.append(chars[r]);// depSize+1=5个随机数
                   }
                   usernamegly=firstSpell.subSequence(0,((firstSpell.size()>=depSize)?depSize:firstSpell.size()))+sb//单位名称汉字拚音首个字母+随机数=9
                   println("usernamegly=="+usernamegly)
               }
                //如单位名称变动，则重新生成审核人用户的用户usernameshr
                if(oldDepartment!=newDepartment){
                    sb.delete(0,sb.size())
                    String firstSpell= PingYinUtil.getFirstSpell(departmentInstance.department)//取单位名称汉字拚音首个字母
                    int n=((depSize-firstSpell.size())>0)?(depSize-firstSpell.size()):0
                    sb= sb.append("-")
                    for (int j = 0; j <= (depSize+n); j++) {
                        int r = ran.nextInt(chars.length);
                        sb.append(chars[r]);// depSize+1=5个随机数
                    }
                    usernameshr=firstSpell.subSequence(0,((firstSpell.size()>=depSize)?depSize:firstSpell.size()))+sb//单位名称汉字拚音首个字母+随机数=9
                    println("usernameshr=="+usernameshr)
                }
                //取变更之前的单位管理员的用户名、单位审核人的用户名
                query = "FROM  User where chineseAuthority='单位管理员' and treeId='"+params.treeId+"'"
                String oldUsernamegly=User.findAll(query)[0].username
                println("oldUsernamegly=="+oldUsernamegly)
                query = "FROM  User where chineseAuthority='单位审核人' and treeId='"+params.treeId+"'"
                String oldUsernameshr=User.findAll(query)[0].username
                println("oldUsernameshr=="+oldUsernameshr)

                def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
                def sql = new Sql(dataSource)
                if(oldDepartment!=newDepartment) {
                    //不重新更换【单位管理员】帐号
                    //query = "Update user set  department='" + departmentTitled + "', username='" + usernamegly + "',truename='" + departmentInstance.contacts + "' where tree_id='" + departmentInstance.treeId + "' AND chinese_authority='单位管理员'"
                    query = "Update user set  department='" + departmentTitled + "' where tree_id='" + departmentInstance.treeId + "' AND chinese_authority='单位管理员'"
                    sql.execute(query)
                }
                query = "Update user set  truename='" + departmentInstance.contacts + "' where tree_id='" + departmentInstance.treeId + "' AND chinese_authority='单位管理员'"
                println("1query=="+query)
                sql.execute(query)

                if(oldDepartment!=newDepartment) {
                    //不重新更换【单位管理员】帐号
                    //query ="Update act_id_user set ID_='"+usernamegly+"' ,LAST_='"+departmentInstance.contacts+"' where ID_='"+oldUsernamegly+"'"
                }
                query ="Update act_id_user set LAST_='"+departmentInstance.contacts+"' where ID_='"+oldUsernamegly+"'"
                println("2query=="+query)
                sql.execute(query)


                if(oldDepartment!=newDepartment) {
                    //不重新更换【单位审核人】帐号
                    //query ="Update user set  department='"+departmentTitled+"', username='"+usernameshr+"',truename='"+departmentInstance.leader+"' where tree_id='"+departmentInstance.treeId+"' AND chinese_authority='单位审核人'"
                    query ="Update user set  department='"+departmentTitled+"' where tree_id='"+departmentInstance.treeId+"' AND chinese_authority='单位审核人'"
                    sql.execute(query)
                }
                query ="Update user set  truename='"+departmentInstance.leader+"' where tree_id='"+departmentInstance.treeId+"' AND chinese_authority='单位审核人'"
                println("3query=="+query)
                sql.execute(query)


                if(oldDepartment!=newDepartment) {
                    //不重新更换【单位审核人】帐号
                    //query ="Update act_id_user set ID_='"+usernameshr+"' ,LAST_='"+departmentInstance.leader+"' where ID_='"+oldUsernameshr+"'"
                }
                query ="Update act_id_user set LAST_='"+departmentInstance.leader+"' where ID_='"+oldUsernameshr+"'"
                println("4query=="+query)
                sql.execute(query)

                if(oldDepartment!=newDepartment) {
                    //不重新更换【主管单位审核人】帐号
                    //query ="Update user set  department='"+departmentTitled+"', username='"+usernameshr+"',truename='"+departmentInstance.leader+"' where tree_id='"+departmentInstance.treeId+"' AND chinese_authority='主管单位审核人'"
                    query ="Update user set  department='"+departmentTitled+"' where tree_id='"+departmentInstance.treeId+"' AND chinese_authority='主管单位审核人'"
                    sql.execute(query)
                }
                query ="Update user set  truename='"+departmentInstance.leader+"' where tree_id='"+departmentInstance.treeId+"' AND chinese_authority='主管单位审核人'"
                println("3query=="+query)
                sql.execute(query)


                if(oldDepartment!=newDepartment) {
                    //不重新更换【单位审核人】帐号
                    //query ="Update act_id_user set ID_='"+usernameshr+"' ,LAST_='"+departmentInstance.leader+"' where ID_='"+oldUsernameshr+"'"
                }
                query ="Update act_id_user set LAST_='"+departmentInstance.leader+"' where ID_='"+oldUsernameshr+"'"
                println("4query=="+query)
                sql.execute(query)

                render "success"
                return
            } else {
                // response.getWriter().write("failure0");//也可以
                render "failure"
                return
            }
        } catch (e) {
            render "failure"
            return
        }
    }

    @Transactional
    def delete() {
        def departmentInstance = Department.get(params.id)
        //println("departmentInstance.treeId========="+departmentInstance.treeId)
        String s = departmentInstance.treeId
        String query = "from Department where (substring(treeId, 1,LENGTH('" + s + "'))='" + s + "') and (LENGTH(treeId)>LENGTH('" + s + "'))"
        def list1 = Department.findAll(query)
        if (list1.size() > 0) {//删除的单位有下属单位，不能删除！
            render "failure"
            return
        } else {
            query = "from Employee where treeId='" + s + "'"
            def list2 = Employee.findAll(query)
            if (list2.size() > 0) { //删除的单位职工信息，不能删除！
                render "failure"
                return
            }
        }
        if (departmentInstance == null) {
            notFound()
            return
        }
        try {
            Department i
            i = departmentInstance.delete()//返回当前保存的记录
            if (i == null) {
                // response.getWriter().write("success0");//也可以
                render "success"
                return
            } else {
                // response.getWriter().write("failure0");//也可以
                render "failure"
                return
            }
        } catch (e) {
            render "failure"
            return
        }
    }

    @Transactional
    def deleteAllDepartment() {
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        String query = "FROM  Department";
        String employeequery
        query = 'from Department';
        if (dep_tree_id == '1000') {
            query = query + " where LENGTH(treeId)>6"//全部单位信息
            employeequery = "from Employee"
            def list2 = Employee.findAll(employeequery)
            if (list2.size() > 0) { //删除的单位有职工信息，不能删除！
                render "failure"
                return
            }
        } else {
            if (dep_tree_id.length() <= 3) {
                query = query + " where (substring(treeId, 1,3)='" + dep_tree_id + "') AND  (LENGTH(treeId)>6)"
//各区全部单位信息
                employeequery = "from Employee  where (substring(treeId, 1,3)='" + dep_tree_id + "') AND  (LENGTH(treeId)>6)"
//各区全部单位信息
                def list2 = Employee.findAll(employeequery)
                if (list2.size() > 0) { //删除的单位有职工信息，不能删除！
                    render "failure"
                    return
                }
            } else {
                query = query + " where substring(treeId, 1,(LENGTH(treeId)-3))='" + dep_tree_id + "'"//正常查询
                employeequery = "from Employee  where substring(treeId, 1,(LENGTH(treeId)-3))='" + dep_tree_id + "') AND  (LENGTH(treeId)>6)"
//各区全部单位信息
                def list2 = Employee.findAll(employeequery)
                if (list2.size() > 0) { //删除的单位有职工信息，不能删除！
                    render "failure"
                    return
                }
            }
        }

        def list8 = Department.findAll(query)
        if (list8.size() == 0) {
            render "failure1"//没经有单位信息I
            return
        }
        query = "Delete FROM  department";
        if (dep_tree_id == '1000') {
            query = query + " where LENGTH(tree_id)>6"//全部单位信息
        } else {
            if (dep_tree_id.length() <= 3) {
                query = query + " where (substring(tree_id, 1,3)='" + dep_tree_id + "') AND  (LENGTH(tree_id)>6)"
//各区全部单位信息
            } else {
                query = query + " where substring(tree_id, 1,(LENGTH(tree_id)-3))='" + dep_tree_id + "'"//正常查询
            }
        }
        //println("query========="+query)
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        if (sql.execute(query)) {
            render "failure"//删除失败
            return
        } else {
            render "success"//删除成功
            return
        }
    }

    //从Excel文件中导入数据
    @Transactional
    def importExcelDepartment() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        def f = request.getFile('employeeDepartmentexcelfilePath')
        def fileName
        def filePath
        String columnsss = "version"
        String values_update = "version=0"
        String values = "0"
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            filePath = getServletContext().getRealPath("/") + "/tmp/"
            if ((fileName.toLowerCase().endsWith(".xls")) || (fileName.toLowerCase().endsWith(".xlsx"))) {
                f.transferTo(new File(filePath + fileName))//把客户端上选的文件上传到服务器
            } else {
                render "{success:false,info:'错误！导入的Excel文件类型不对'}"//render "typeError"//导入的Excel文件类型不对
                return
            }
        } else {
            render "{success:false,info:'错误！导入的Excel文件不存在'}" //render "failure"//导入的Excel文件不存在
            return
        }
        HSSFWorkbook wb = null
        POIFSFileSystem fs = null
        try {
            fs = new POIFSFileSystem(new FileInputStream(filePath + fileName));
            wb = new HSSFWorkbook(fs); //读取导入的Excel文件
        }
        catch (IOException e) {
            render "failure"//读取导入的Excel文件异常
            return
        }
        Class<?> demo = Department.class;
        def field = demo.getDeclaredFields();
        HSSFSheet sheet = wb.getSheetAt(0);//直接取导入的Excel文件的第1个sheet对象  // println("===sheet.getSheetName()======================="+sheet.getSheetName())
        demo = Department.class;
        field = demo.getDeclaredFields();
        def rowcount = sheet.getLastRowNum();
        int columns = sheet.getRow((short) 1).getPhysicalNumberOfCells();//取得Excel文件的总列数
        for (int j = 0; j < columns; j++) {
            columnsss = columnsss + "," + fieldNameChang(field[j].getName())    //准备用于新增记录的字段的列表
        }
        columnsss = "(" + columnsss + ")"//准备用于新增记录的字段的列表
        HSSFRow row
        HSSFCell cell
        int a = 2;//从Excel的第三行开始读取数据
        while (a <= rowcount) {
            row = sheet.getRow(a);
            if (!row) break;
            cell = row.getCell((short) 0);
            if (!cell.getStringCellValue()) break;
            int i = 0
            try {
                for (i = 0; i < columns; i++) {
                    cell = row.getCell((short) i);
                    if (field[i].getType() == (new Date()).getClass()) { //如果是日期型数据
                        if (!cell) {  //如是空
                            params.(field[i].getName()) = ""
                            values = values + ",Null"
                            values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "Null"
                            continue
                        }
                        if (cell.getCellType() == 1) {            //字符型单元格放的是日期型
                            params.(field[i].getName()) = ((cell.getStringCellValue()) ? (simpleDateFormat.parse(cell.getStringCellValue().replaceAll("/", "-") + " 00:00:00.0")) : "")
                            if (((cell.getStringCellValue()) ? (simpleDateFormat.parse(cell.getStringCellValue().replaceAll("/", "-"))) : "") != "") {
                                values = values + ",STR_TO_DATE('" + cell.getStringCellValue().replaceAll('/', '-') + "','%Y-%m-%d')"
                                values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "STR_TO_DATE('" + cell.getStringCellValue().replaceAll('/', '-') + "','%Y-%m-%d')"
                            } else {
                                values = values + ",NULL"
                                values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "Null"
                            }
                        } else {              //日期型单元格放日期型数据
                            params.(field[i].getName()) = cell.dateCellValue ? simpleDateFormat.format(cell.dateCellValue) : ""
                            if (((cell.dateCellValue) ? (simpleDateFormat.format(cell.dateCellValue)) : "") != "") {
                                values = values + ",STR_TO_DATE('" + (simpleDateFormat.format(cell.dateCellValue)) + "','%Y-%m-%d')"
                                values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "STR_TO_DATE('" + (simpleDateFormat.format(cell.dateCellValue)) + "','%Y-%m-%d')"
                            } else {
                                values = values + ",NULL"
                                values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "Null"
                            }
                        }
                    } else {
                        if (field[i].getType() == (new String()).getClass()) {           //如果是字符型数据
                            if (!cell) {
                                params.(field[i].getName()) = ""
                                values = values + ",''"
                                values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "''"
                                continue
                            }
                            if (cell.getCellType() == 1) {   //以字符形式放的字符型数据
                                params.(field[i].getName()) = cell.getStringCellValue() ? cell.getStringCellValue() : ""
                                values = values + ",'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                                values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                            } else {   //以数值形式放的字符型数据
                                if (cell.getCellType() == 3) {
//CellType类型值：CELL_TYPE_NUMERIC 数值型 0、CELL_TYPE_STRING 字符串型 1、CELL_TYPE_FORMULA 公式型 2、CELL_TYPE_BLANK 空值 3、CELL_TYPE_BOOLEAN 布尔型 4、CELL_TYPE_ERROR 错误 5
                                    params.(field[i].getName()) = ""
                                    values = values + ",''"
                                    values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "''"
                                } else {
                                    params.(field[i].getName()) = cell.getNumericCellValue() ? (long) cell.getNumericCellValue() : ""
                                    values = values + "," + cell.getNumericCellValue()
                                    values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + (long) cell.getNumericCellValue()
                                }
                            }
                        } else {
                            if (field[i].getType() == (new Double(0.0)).getClass()) {           //如果是浮点型数据
                                if (!cell) {
                                    params.(field[i].getName()) = "0.0"
                                    values = values + ",0.0"
                                    values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "0.0"
                                    continue
                                }
                                if (cell.getCellType() == 1) {   //以字符形式放的浮点型数据
                                    params.(field[i].getName()) = cell.getStringCellValue() ? cell.getStringCellValue() : ""
                                    values = values + ",'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                                    values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                                } else {
                                    params.(field[i].getName()) = cell.getNumericCellValue() ? cell.getNumericCellValue() : ""
                                    values = values + "," + cell.getNumericCellValue()
                                    values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + cell.getNumericCellValue()

                                }
                            } else {  //如果是整型数据
                                if (!cell) {
                                    params.(field[i].getName()) = "0"
                                    values = values + ",0"
                                    values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "0"
                                    continue
                                }
                                if (cell.getCellType() == 1) {   //以字符形式放的整型数据
                                    params.(field[i].getName()) = cell.getStringCellValue() ? cell.getStringCellValue() : ""
                                    values = values + ",'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                                    values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + "'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                                } else {
                                    params.(field[i].getName()) = cell.getNumericCellValue() ? cell.getNumericCellValue() : ""
                                    values = values + "," + cell.getNumericCellValue()
                                    values_update = values_update + "," + fieldNameChang(field[i].getName()) + "=" + cell.getNumericCellValue()
                                }
                            }
                        }
                    }
                }
            } catch (e) {
                render "{failure:true,info:'Excel数据导入失败！'}"
                return
            }
            params.glyph = "0xf0c9"; // params.treeId = dep_tree_id
            //values用于新增记录的字段值集，values_update用于对已有记录更新字段集。
            String query = "FROM  Department where treeId='" + params.treeId + "'"
            def list8 = Department.findAll(query)
            if (list8.size() != 0) {//判断是增加还是替换
                query = "UPDATE department  set " + values_update + " where  tree_id ='" + params.treeId + "'";//是替换
            } else {
                query = "insert into department " + columnsss + " values (" + values + ")";//是增加
            }
            //println("columnsss======"+columnsss);println("values======"+values);println("values_update======"+values_update);println("query======"+query)
            if (sql.execute(query)) {
                render "{success:false,info:'错误！导入数据库失败'}"
                return
            }
            values_update = "version=0"
            values = 0
            a++
        }
        render "{success:true,info:'Excel数据全部导入成功'}"
    }

    //把数据导出到Excel文件中
    /**
     *   生成一个Excel文件POI
     * @param inputFile 输入模板文件路径
     * @param outputFile 输入文件存放于服务器路径
     * @param dataList 待导出数据
     * @throws Exception
     * @roseuid:
     */
    def exportExcelDepartment() {
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        def filePath = getServletContext().getRealPath("/") + "/tmp/"
        String inputFile = filePath + "单位信息模板.xls"
        String outputFile = filePath + "单位信息.xls"
        Class<?> demo
        def field
        String query
        FileInputStream fin = new FileInputStream(inputFile)   //用模板文件构造poi
        POIFSFileSystem fs = new POIFSFileSystem(fin);
        HSSFWorkbook templatewb = new HSSFWorkbook(fs); //创建Excel模板文件
        // for (int sheetNum = 0; sheetNum < templatewb.numberOfSheets; sheetNum++) {
        HSSFSheet templateSheet = templatewb.getSheetAt(0);//直接取模板文件第sheetNum（范围从0到templatewb.numberOfSheets-1）个sheet对象
        HSSFRow templateRow = templateSheet.getRow(2);
        //得到模板文件第sheetNum个sheet的第三行对象（getRow(0)、getRow(1)、getRow(2)分别表示第一行、第二行、第三行....）为了得到模板样式
        int columns = templateSheet.getRow((short) 1).getPhysicalNumberOfCells();//得到模板文件第sheetNum个sheet的总列数
        HSSFCellStyle[] styleArray = new HSSFCellStyle[columns];//创建样式数组
        ArrayList dataList
        for (int s = 0; s < columns; s++) {  //一次性创建所有列的样式放在数组里
            styleArray[s] = templateRow.getCell((short) s).getCellStyle() //样式数组实例获得每列的样式
        }
        /*switch (templateSheet.getSheetName()) {
            case '单位信息'://实际只有一个Sheet
                demo = Department.class;
                field = demo.getDeclaredFields();
                query =query_read  //使用从 def readDepartment 中获取的query查询结果,也可以用于打印或导出Excel文件
                dataList = Department.findAll(query)
                break
        }*/
        demo = Department.class;
        field = demo.getDeclaredFields();



        // println("params.dep_tree_id=====readDepartment========="+params.dep_tree_id)
        query = 'from Department';
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
        if (dep_tree_id == '1000') {
            query = query + " where LENGTH(treeId)>6"//全部单位信息
        } else {
            if (dep_tree_id.length() <= 6) {
                query = query + " where ((substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "') AND  (LENGTH(treeId)>6))"
//各区或分类全部单位信息
            } else {
                query = query + " where substring(treeId, 1,(LENGTH(treeId)-3))='" + dep_tree_id + "'"//正常查询 //正常查询
            }
        }
        int dep_size = (Department.findAll(query)).size()
        // query_read = query  //使用从 def readArticles 中获取的query查询结果,也可以用于打印或导出Excel文件
        // query = query_read  //使用从 def readDepartment 中获取的query查询结果,也可以用于打印或导出Excel文件
        dataList = Department.findAll(query)
        for (int rowId = 2; rowId < dataList.size() + 2; rowId++) { //定位行，循环对每一个单元格进行赋值
            def valueList = dataList[rowId - 2];  //依次取第rowId行数据   每一个数据是valueList
            HSSFRow hssfRow = templateSheet.createRow(rowId);   //创建新的rowId行
            for (int columnId = 0; columnId < columns; columnId++) {
                //定位列，依次取出对应与colunmId列的值 // println("=====columnId=========="+columnId+"======"+field[columnId].getName()+"======="+field[columnId].getType())//String dataValue = (String) valueList[field[columnId].getName()]; //每一个单元格的值
                HSSFCellStyle style = styleArray[columnId]; //取出colunmId列的的style ,模板columnId列的样式
                HSSFCell cell = hssfRow.createCell((short) columnId);   //创建新的rowId行   columnId列   单元格对象
                /*if (templateCell.getCellStyle().getLocked() == false) { //如果对应的模板单元格   样式为非锁定
                style.setLocked(false); //设置此列style为非锁定
                cell.setCellStyle(style); //设置到新的单元格上
            }else {//否则样式为锁定
                style.setLocked(true); //设置此列style为锁定
                cell.setCellStyle(style); //设置到新单元格上
            }*/
                cell.setCellStyle(style);
                //cell.setEncoding(HSSFCell.ENCODING_UTF_16); //设置编码   统一为String，但setEncoding不认
                if (field[columnId].getType() == (new Date()).getClass()) {//如果是日期型数据
                    cell.setCellValue(valueList[field[columnId].getName()] ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(valueList[field[columnId].getName()]) : "")
                } else {
                    if (field[columnId].getType() == java.lang.String) {//如果是字符型数据
                        cell.setCellValue(valueList[field[columnId].getName()] ? valueList[field[columnId].getName()] : "");
                    } else {
                        if (field[columnId].getType() == java.lang.Integer) {//如果是整型数据
                            cell.setCellValue(valueList[field[columnId].getName()] ? valueList[field[columnId].getName()] : 0);
                        } else {//如果是浮点型数据
                            cell.setCellValue(valueList[field[columnId].getName()] ? valueList[field[columnId].getName()] : 0.0);
                        }
                    }
                }
            }
        }
        // }
        fin.close();//关闭输入流
        FileOutputStream fOut = new FileOutputStream(outputFile); //设置输出流
        templatewb.write(fOut); //将模板的内容写到输出文件上
        fOut.flush();
        fOut.close(); //操作结束，关闭文件
        render "单位信息"
        return
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


    def readDepartmentJxkh = {//DepartmentJxkhStore.js的初始值，//获取下级单位的详细信息
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
       //  println("readDepartmentJxkh=====dep_tree_id======"+dep_tree_id)
        String query = 'from DepartmentJxkh';
        String jsonStr
        if (params.sort != null) {
            jsonStr = params.sort
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
            JSONObject obj = new JSONObject(jsonStr);
            params.sort = obj.get('property')
            params.order = obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true
        }
        params.offset = params.start ? params.start as int : 0
        params.max = params.limit ? params.limit as int : 20
        def list1
        query = query + " where treeId='" + dep_tree_id + "' order by nd Desc"//正常查询
        //query = query + " where treeId='" + dep_tree_id + "'"//正常查询
        // println("query==jxkh======"+query)
        int dep_size = (DepartmentJxkh.findAll(query)).size()

        list1 = DepartmentJxkh.findAll(query, [max: params.max, offset: params.offset])
        render(contentType: "text/json") {
            departments   list1.collect() {
                [
                        id           : it.id,
                        department   : it.department,
                        nd           : it.nd,
                        hdjxgzzl     : it.hdjxgzzl,
                        ablhddjlxjxgz: it.ablhddjlxjxgz,
                        jxgzhzl      : it.jxgzhzl,
                        nrjxdhgz     : it.nrjxdhgz,
                        nzycxjj      : it.nzycxjj,
                        treeId       : it.treeId
                ]
            }
            totalCount   dep_size.toString()  //totalCount = DepartmentJxkh.count().toString()
        }
    }


}
