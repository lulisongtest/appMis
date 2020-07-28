package com.RegisterController

import grails.plugin.springsecurity.ui.RegistrationCode
import grails.plugin.springsecurity.ui.ResetPasswordCommand
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

//把class RegisterCommand放在此文件中

class RegisterController extends grails.plugin.springsecurity.ui.RegisterController{
    def SpringSecurityUiService
    def SaveUserService
    static final usernameValidator = { String password, command ->
        def results = com.user.User.findAllByUsername(command.username)
        if(results.size()==0){
            return 'command.resetPassword.nousername'//没有该用户
        }
    }
    static final oldPasswordValidator = { String password, command ->
        def results = com.user.User.findAllByUsername(command.username)
        if(results.size()==0){
             return 'command.nousername'//没有该用户
        }else{
            def bcryptPasswordEncoder = new BCryptPasswordEncoder()
           // println("密码相符password==="+password)
           // println("密码相符results[0].password==="+results[0].password.substring(8))//过滤“{bcrypt}”
            boolean f = bcryptPasswordEncoder.matches(password,results[0].password.substring(8))//前一个是没加密的，后一个是从数据库中取出的加密密码。
            // boolean f = bcryptPasswordEncoder.matches()
           // println("密码相符==="+f)
           // f=true
            if(!f){
                return 'command.nopassword.error.nousername'//原密码不正确
            }
        }
    }

    def register(RegisterCommand registerCommand) {
        //以下uiRegistrationCodeStrategy改为SpringSecurityUiService
        if (!request.post) {
            return [registerCommand: new RegisterCommand()]
        }

        if (registerCommand.hasErrors()) {
            return [registerCommand: registerCommand]
        }

         //def user = uiRegistrationCodeStrategy.createUser(registerCommand)
         def user = SpringSecurityUiService.createUser(registerCommand)

        RegistrationCode registrationCode = SpringSecurityUiService.register(user, registerCommand.password)

        if (registrationCode == null || registrationCode.hasErrors()) {
            // null means problem creating the user
            flash.error = message(code: 'spring.security.ui.register.miscError')
            return [registerCommand: registerCommand]
        }


        if( requireEmailValidation  ) {
            sendVerifyRegistrationMail registrationCode, user, registerCommand.email
            [emailSent: true, registerCommand: registerCommand]
        } else {
            redirectVerifyRegistration(SpringSecurityUiService.verifyRegistration(registrationCode.token))
        }
    }

    def verifyRegistration() {
        redirectVerifyRegistration(SpringSecurityUiService.verifyRegistration(params.t))
    }

    def resetPassword(ResetPasswordCommand resetPasswordCommand) {

        String token = params.t

        def registrationCode = token ? RegistrationCode.findByToken(token) : null
        if (!registrationCode) {
            flash.error = message(code: 'spring.security.ui.resetPassword.badCode')
            redirect uri: successHandlerDefaultTargetUrl
            return
        }

        if (!request.post) {
            return [token: token, resetPasswordCommand: new ResetPasswordCommand()]
        }

        resetPasswordCommand.username = registrationCode.username
        resetPasswordCommand.validate()
        if (resetPasswordCommand.hasErrors()) {
            return [token: token, resetPasswordCommand: resetPasswordCommand]
        }

        //def user = uiRegistrationCodeStrategy.resetPassword(resetPasswordCommand, registrationCode)
        def user = SpringSecurityUiService.resetPassword(resetPasswordCommand, registrationCode)


        if (user.hasErrors()) {
            // expected to be handled already by ErrorsStrategy.handleValidationErrors
        }

        flash.message = message(code: 'spring.security.ui.resetPassword.success')

        redirect uri: registerPostResetUrl ?: successHandlerDefaultTargetUrl
    }

    def resetPasswordxSuccess(){
       // println("resetPasswordxSuccess====params.t==="+params.t)
        return [token:"return"]
    }

    def resetPasswordx(ResetPasswordxCommand resetPasswordxCommand) {
       // println("resetPasswordx====params.t===="+params.t )
        if(!params.t){
           // println("resetPasswordx   first ")
            return [token: "ready", resetPasswordxCommand: new ResetPasswordxCommand()]//刚进入
        }
        if(params.t=="return"){
            redirect uri: "/"  //返回登录界面
            return
        }
        resetPasswordxCommand.username = params.username
        resetPasswordxCommand.validate()
        if (resetPasswordxCommand.hasErrors()) {
          //println("resetPasswordx  重置密码失败 validate====failure ")
            return [token: 'failure', resetPasswordxCommand: resetPasswordxCommand]//填写重置密码相关信息不符合要求
        }else{
            // println("resetPasswordx 重置密码成功  validate====success ")
            def results = com.user.User.findAllByUsername(params.username)
            results[0].setPassword(params.password)
            results[0].setAccountExpired(false)
            results[0].setAccountLocked(false)
            results[0].setEnabled(true)
            results[0].setPasswordExpired(false)
            def infor = SaveUserService.saveUser(results[0])
            if (infor.information == 'success'){
                redirect (uri:'/register/resetPasswordxSuccess')
                return
            }else{
               // println("resetPasswordx  重置密码失败 validate====failure ")
                return [token: 'failure', resetPasswordxCommand: resetPasswordxCommand]//填写重置密码相关信息不符合要求
            }

        }
    }




}

class RegisterCommand extends grails.plugin.springsecurity.ui.RegisterCommand {

   // protected static Class<?> User//取消
   // protected static String usernamePropertyName//取消

    String username
    String truename
    String email
    String password
    String password2

    static constraints = {
        username validator: { value, command ->
            if (!value) {
                return
            }
            //是否已经有该用户
            if (User.findWhere((usernamePropertyName): value)) {
                return 'registerCommand.username.unique'
            }
        }
        truename truename: true
        email email: true
        password validator: grails.plugin.springsecurity.ui.RegisterController.passwordValidator
        password2 nullable: true, validator: grails.plugin.springsecurity.ui.RegisterController.password2Validator
    }
}

class ResetPasswordxCommand extends grails.plugin.springsecurity.ui.ResetPasswordCommand {

    String username
    String oldPassword
    String password
    String password2


    static constraints = {
        username nullable: true, validator: RegisterController.usernameValidator
        oldPassword nullable: true, validator: RegisterController.oldPasswordValidator
        password validator: RegisterController.passwordValidator
        password2 nullable: true, validator: RegisterController.password2Validator
    }
}
