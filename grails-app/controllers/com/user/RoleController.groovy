package com.user

import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import org.grails.web.json.JSONObject


@Transactional(readOnly = true)
class RoleController extends grails.plugin.springsecurity.ui.RoleController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def readRolequery(){
      // 方法0
       ArrayList<Map> list2 = new ArrayList<Map>();
        String query = "from Role GROUP BY chineseAuthority order by chineseAuthority  DESC"
        def list1 = Role.findAll(query);
        list2.add([authority:"all",chineseAuthority:'全部'])
        for(int i=0;i<list1.size();i++){
            list2.add([authority:list1[i].authority,chineseAuthority:list1[i].chineseAuthority])
        }
        render(contentType: "text/json") {
            authoritys   list2.collect() {
                [
                        authority:it?.authority,
                        chineseAuthority:it?.chineseAuthority
                ]
            }
        }


     /*   //方法一
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String query = "SELECT * FROM role group by authority"//取记录数
        def list2=sql.rows(query)
        println("list2.class==="+list2.class)
        println("list2==="+list2)
        render(contentType: "text/json") {
            authoritys   list2.collect() {
                [
                        authority:it?.authority,
                        chineseAuthority:it?.chinese_authority
                ]
            }
        }
        */

       /* //方法二
        def c = Role.createCriteria()
        def list2= c.list { projections { distinct(["chineseAuthority","authority"])} }
        render(contentType: "text/json") {
            authoritys   list2.collect() {
                [
                        chineseAuthority:it[0],
                        authority:it[1]
                ]
            }
        }*/

    }
    //读取用户权限信息
    def readRole(){
       // println( " readRole  execute params.sort============!!!!!!!!!!!!!!!!!!!!!")
        String jsonStr
        if(params.sort!=null){
            jsonStr=params.sort
            jsonStr=jsonStr.substring(1,jsonStr.length()-1)
            JSONObject obj = new JSONObject(jsonStr);
            params.sort=obj.get('property')
            params.order= obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true
           // println( "readRole  params.sort============"+obj.get('property'))
        }
        params.offset = params.start as int
        params.max = params.limit as int
        def list1
        try{
            list1 = Role.list(params);
        }catch(e){
            params.sort=null
            params.order=null
            list1 = Role.list(params);
        }
        render(contentType: "text/json") {
            authoritys   list1.collect(){
                [
                        id:it.id,
                        authority:it?.authority,
                        chineseAuthority:it?.chineseAuthority
                ]
            }
            totalCount   Role.count()
        }
    }







    def listRolecombobox(){
        String query = "from Role GROUP BY chineseAuthority order by chineseAuthority  DESC"
        def list1 = Role.findAll(query);
        Role all1=new Role()//把[all、全部]放在第一，原来第一个记录信息放在最后
        all1.chineseAuthority=list1[0].chineseAuthority
        list1.add(all1)
        list1[0].chineseAuthority="全部"
        render(contentType: "text/json") {
            roles   list1.collect() {
                [
                        id:it.id,
                        authority:it?.authority,
                        chineseAuthority:it?.chineseAuthority
                ]
            }
            totalCount   Role.count()
        }
    }
    def listRolecombobox2(){
        String query = "from Role GROUP BY chineseAuthority order by chineseAuthority  DESC"
        def list1 = Role.findAll(query);
        render(contentType: "text/json") {
            roles   list1.collect() {
                [
                        id:it.id,
                        authority:it?.authority,
                        chineseAuthority:it?.chineseAuthority
                ]
            }
            totalCount   Role.count()
        }
    }

    def test(){//判断有无访问该表数据的权限
        render "success"
        return
    }
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Role.list(params), model: [authorityInstanceCount: Role.count()]
    }

    def show(Role authorityInstance) {
        respond authorityInstance
    }

    def create() {
        respond new Role(params)
    }

    @Transactional
    def save(Role authorityInstance) {
        if (authorityInstance == null) {
            notFound()
            return
        }

        if (authorityInstance.hasErrors()) {
            authorityInstance.errors.each{
                println it
            }
            respond authorityInstance.errors, view: 'create'
            return
        }
        try{
            if(authorityInstance.save(flush: true)){
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

    def edit(Role authorityInstance) {
        respond authorityInstance
    }

    @Transactional
    def update(Role authorityInstance) {
        if (authorityInstance == null) {
            notFound()
            return
        }

        if (authorityInstance.hasErrors()) {
            authorityInstance.errors.each {
                println it
            }
            respond authorityInstance.errors, view: 'edit'
            return
        }

        try{
            if(authorityInstance.save(flush: true)){
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

    @Transactional
    def delete() {
        def authorityInstance = Role.get(params.id)
        if (authorityInstance == null) {
            notFound()
            return
        }
        try{
            if(authorityInstance.delete()==null){
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
    def listAsJsonPage2={//查询中文角色
        String s=params.newvalue;
        String query=""
        if((params.newvalue)=="all"){//显示所有中文角色
            query="from Role"
        }else{
            query="from Role  where chineseAuthority='"+s+"'"
        }

        String jsonStr
        def list1
       // println( " listAsJsonPage2  execute params.sort============!!!!!!!!!!!!!!!!!!!!!")
        if(params.sort!=null){
            jsonStr=params.sort
            jsonStr=jsonStr.substring(1,jsonStr.length()-1)
            JSONObject obj = new JSONObject(jsonStr);
            try{
                 list1 = Role.findAll(query+" order by "+obj.get('property')+" "+obj.get('direction'),[max:new Integer(params.limit),offset:new Integer(params.start)]);
            }catch (e) {
                  list1 = Role.findAll(query,[max:new Integer(params.limit),offset:new Integer(params.start)]);
            }
          //  println( "listAsJsonPage2  params.sort============"+obj.get('property'))
        }else{
            list1 = Role.findAll(query,[max:new Integer(params.limit),offset:new Integer(params.start)]);
        }
        def list = Role.findAll(query);
       // def list1 = Role.findAll(query,[max:new Integer(params.limit),offset:new Integer(params.start)]);
        params.remove("sort")
        render(contentType: "text/json") {
            authoritys   list1.collect(){
                [
                        id:it.id,
                        authority:it.authority,
                        chineseAuthority:it.chineseAuthority
                ]
            }
            totalCount  (list.size()).toString()
        }
    }

}
