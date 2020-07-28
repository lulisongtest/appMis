package appmis

import com.user.Role
import com.user.User
import com.user.UserRole
import grails.gorm.transactions.Transactional
import groovy.sql.Sql

@Transactional
class SaveUserService {
    boolean transactional = true;
    def grailsApplication
    def saveUser(params) {
        //println("SaveUserService=================saveUser==============================params.treeId======="+ params.treeId)
        def information,userInstance
        String oldusername
        try{
            if(params.oldusername){
                oldusername=params.oldusername//可能是管理员操作新增用户，要修改系统自动生成初始帐号，
            }else{
                oldusername=params.username
            }
        }catch(e){
            oldusername=params.username
        }
        if(params.id){
            userInstance = User.findById(params.id as Long)
            if(!params.password){
                params.remove("password") //没有修改密码，则不更新password
            }else{
                userInstance.setPassword(params.password)
            }
        }else{
            userInstance = new User()
            userInstance.setPassword(params.password)
        }
        //User.withTransaction { status ->
        //println("params.enabled======"+params.enabled)////Extjs网页传来的是字符"false"、"true"
        if((!params.enabled)||(params.enabled == null)||(params.enabled == "false")||(params.enabled == "0")){
            userInstance.setEnabled(false)
        }else{
            userInstance.setEnabled(true)
        }
        if((!params.passwordExpired)||(params.passwordExpired == null)||(params.passwordExpired == "false")||(params.passwordExpired == "0")){
            userInstance.setPasswordExpired(false)
        }else{
            userInstance.setPasswordExpired(true)
        }
        if((!params.accountExpired)||(params.accountExpired == null)||(params.accountExpired == "false")||(params.accountExpired == "0")){
            userInstance.setAccountExpired(false)
        }else{
            userInstance.setAccountExpired(true)
        }
        if((!params.accountLocked)||(params.accountLocked == null)||(params.accountLocked == "false")||(params.accountLocked == "0")){
            userInstance.setAccountLocked(false)
        }else{
            userInstance.setAccountLocked(true)
        }
        if(!userInstance.regdate){
            userInstance.setRegdate(new Date())
        }
        if(!userInstance.lastlogindate){
            userInstance.setLastlogindate(new Date())
        }
        if(params.truename == null){
            userInstance.setTruename("未知")
        }else{
            userInstance.setTruename(params.truename)
        }
        if(params.phone == null){
            userInstance.setPhone("未知")
        }else{
            userInstance.setPhone(params.phone)
        }
        userInstance.setUsername(params.username)
        if(params.department == null){
            userInstance.setDepartment("未知")
        }else{
            userInstance.setDepartment(params.department)
        }
        if(params.chineseAuthority == null){
            userInstance.setChineseAuthority("普通用户")
        }else{
            userInstance.setChineseAuthority(params.chineseAuthority)
        }
        if(params.email ==null){
            userInstance.setEmail("lulisongtest@163.com")
        }else{
            userInstance.setEmail(params.email)
        }
        if((params.treeId == null)||(params.treeId == "")){
            userInstance.setTreeId("999")
        }else{
            userInstance.setTreeId(params.treeId)
            if((params.chineseAuthority=="管理员")&&(params.department=="全部")){
                userInstance.setTreeId("000")
            }
            if((params.chineseAuthority=="市人社局审核人")&&(params.department=="全部")){
                userInstance.setTreeId("001")
            }
        }
        try{
            /*if(userInstance.id){//已有用户，是修改
                def userRoleObj = UserRole.findByUser(userInstance)
                if(userRoleObj){userRoleObj.delete(flush: true)}//先删除，后面再新建,但不行，应放在userInstance.save(flush: true)后再做删除才有效!!
            }*/
            if(userInstance.save(flush: true)){
                //println("00000  userInstance==save==success-------------------")
                def userRoleInstance = UserRole.findByUser(userInstance)
                if(userRoleInstance){
                    //已有这条记录，先删除，后面再新建,应放在userInstance.save(flush: true)后再做删除才有效，否则无效！
                     userRoleInstance.delete(flush: true)
                    //println("00000 old userRoleInstance==save==success-------------------")
                    information = "success"
                }
                    userRoleInstance = new UserRole()
                    if(params.chineseAuthority ==null){
                        userRoleInstance.role = Role.findByAuthority("ROLE_USER")
                    }else{
                        userRoleInstance.role = Role.findByChineseAuthority(params.chineseAuthority)
                    }
                    userRoleInstance.user = User.findById(userInstance.id)
                    userRoleInstance.save(flush: true)
                    //println("00000 new  userRoleInstance==save==success-------------------")
                    information = "success"


                // println("activiti用户管理____判断Activiti中的act_id_user是否存在ID_=="+oldusername)
                def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
                def sql = new Sql(dataSource)
                String query ="select * from act_id_user where ID_='"+oldusername+"'"
                if (sql.firstRow(query)){
                    query ="Update  `act_id_user`  set  ID_='"+params.username+"', LAST_='"+params.truename+"', EMAIL_='"+params.email+"' where ID_='"+oldusername+"'"
                    //println("修改update=="+query)
                    if (sql.execute(query)) {
                        information = "failure"
                        return [information:information]
                    }else{
                        information = "success"
                        return [information:information]
                    }
                }else{
                    query = "INSERT INTO `act_id_user`  (ID_, REV_,LAST_,EMAIL_,PWD_)  VALUES('"+params.username+"',1,'"+params.truename+"','"+params.email+"','123')"
                    //println("增加insert===="+query)
                    if (sql.execute(query)) {
                        information = "failure"
                        return [information:information]
                    }else{
                        information = "success"
                        return [information:information]
                    }
                }
            }else{
                ///println("00000user==save==failure")
                userInstance.errors.each{
                    //println it
                }
                information = "failure"
                return [information:information]
            }
        }catch(e){
            //println "saveUser=============exception!!!!"
            information = "failure"
            return [information:information]
        }
        //}
    }
}
