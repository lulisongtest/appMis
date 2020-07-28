package appmis

import com.RegisterController.RegisterCommand
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.ui.RegistrationCode
import grails.plugin.springsecurity.ui.ResetPasswordCommand
import groovy.sql.Sql


@Transactional
class SpringSecurityUiService extends grails.plugin.springsecurity.ui.SpringSecurityUiService{
    def springSecurityService
    def serviceMethod() {

    }
    //注册创建新用户,增加truename
    def createUser(RegisterCommand command) {
        uiPropertiesStrategy.setProperties(
                email: command.email, username: command.username, truename: command.truename,accountLocked: true, enabled: true, User, null)
    }
//注册新用户
    @Transactional
    RegistrationCode register(user, String password) {
        //println("password=="+password)
       // println("p=="+p)
        String p=encodePassword(password)
        user.setRegdate(new Date())//首次注册的日期
        user.setLastlogindate(new Date())
        user.setChineseAuthority("普通用户")
        user.setDepartment("未知")
        user.setTreeId("999")
        save password: p, user, 'register', transactionStatus
        if (user.hasErrors()) {
            return
        }
        String username = uiPropertiesStrategy.getProperty(user, 'username')

        //Activiti中的act_id_user表中保存用户信息
        // println("activiti用户管理____判断Activiti中的act_id_user是否存在ID_=="+username)
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String query ="select * from act_id_user where ID_='"+username+"'"
        if (sql.firstRow(query)){
            query ="Update  `act_id_user`  set  ID_='"+user.username+"', LAST_='"+user.truename+"', EMAIL_='"+user.email+"' where ID_='"+username+"'"
            //println("修改update=="+query)
            if (sql.execute(query)) {
                return
            }
        }else{
            query = "INSERT INTO `act_id_user`  (ID_, REV_,LAST_,EMAIL_,PWD_)  VALUES('"+user.username+"',1,'"+user.truename+"','"+user.email+"','123')"
            //println("增加insert===="+query)
            if (sql.execute(query)) {
                return
            }
        }

        //向RegistrationCode表中保存邮件验证信息

        save username: username, RegistrationCode, 'register', transactionStatus
    }

    @Transactional
    def verifyRegistration(String token) {
        def conf = SpringSecurityUtils.securityConfig
        RegistrationCode registrationCode = token ? RegistrationCode.findByToken(token) : null
        def registerPostRegisterUrl = conf.ui.register.postRegisterUrl ?: ''
        def successHandlerDefaultTargetUrl = conf.successHandler.defaultTargetUrl ?: '/'

        if (!registrationCode) {
            return [flashType:'error',flashmsg:'spring.security.ui.register.badCode',redirectmsg:successHandlerDefaultTargetUrl]
        }
        def user = this.finishRegistration(registrationCode)
        if (!user || user.hasErrors()) {
            return [flashType:'error',flashmsg:'spring.security.ui.register.badCode',redirectmsg:successHandlerDefaultTargetUrl]
        }
        [flashType:'message', flashmsg:'spring.security.ui.register.complete',redirectmsg:registerPostRegisterUrl ?: successHandlerDefaultTargetUrl]
    }

    @Transactional
    def finishRegistration(RegistrationCode registrationCode) {

        def user = findUserByUsername(registrationCode.username)
        if (!user) {
            return
        }

        save accountLocked: false, user, 'finishRegistration', transactionStatus
        if (!user.hasErrors()) {
            addRoles user, registerDefaultRoleNames
            delete registrationCode, 'finishRegistration', transactionStatus
            springSecurityService.reauthenticate registrationCode.username
        }

        user
    }

    @Transactional
    def resetPassword(ResetPasswordCommand command, RegistrationCode registrationCode) {


        def user = findUserByUsername(registrationCode.username)
        save password: encodePassword(command.password), user, 'resetPassword', transactionStatus

        if (!user.hasErrors()) {
            delete registrationCode, 'resetPassword', transactionStatus
            springSecurityService.reauthenticate registrationCode.username
        }

        user
    }

    String encodePassword(String password) {
        //println("0000encodePassword---encodePassword=="+encodePassword)
        //println("0000encodePassword----springSecurityService.encodePassword(password)=="+springSecurityService.encodePassword(password))
        if (encodePassword) {
            password = springSecurityService.encodePassword(password)
        }
        password
    }
}
