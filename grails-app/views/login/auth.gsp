<g:set var='securityConfig' value='${applicationContext.springSecurityService.securityConfig}'/>
<!doctype html>
<html class="no-js" lang="">
<head>
    <meta charset="utf-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="x-ua-compatible" content="ie=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <s2ui:stylesheet src='spring-security-ui'/>
    <s2ui:title messageCode='spring.security.ui.login.title'/>
    <asset:stylesheet src='spring-security-ui-auth.css'/>



    <script language="javascript" type="text/javascript">
        //验证码验证
        function formCheckCode() {
           // alert("验证码验证")
            var passPattern = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[~!@#$%^&*()_+`\-={}:";'<>?,.\/]).{8,64}$/;
            var oldpassword = document.getElementById("password").value
            var oldusername = document.getElementById("username").value
            var oldcheckCode =document.getElementById("checkCode").value
            if ((oldpassword == "") || (oldusername = "")) {
                if(!document.getElementById("xx")){
                    $(function() {
                        $.jGrowl('<span class="icon icon_info" id="xx">请输入用户名或密码</span>', {life: 10000});
                    });
                }else{
                    $("#xx").text("请输入用户名或密码！");// $("#xx").html("xxqq请输入用户名或密码！");//都可以
                    //document.getElementById("xx").innerText ="xx请输入用户名或密码！";
                }
                document.getElementById("login_message").innerText = "请输入用户名或密码！";			//document.getElementById("login_message").innerHTML="验证码输入错误！"
                return false;
            }
            if (oldcheckCode == "") {
                if(!document.getElementById("xx")){
                    $(function() {
                        $.jGrowl('<span class="icon icon_info" id="xx">请输入验证码</span>', {life: 10000});
                    });
                }else{
                    document.getElementById("xx").innerText = "请输入验证码！";
                }
                document.getElementById("login_message").innerText = "请输入验证码！";			//document.getElementById("login_message").innerHTML="验证码输入错误！"
                return false;
            }
            if (passPattern.test(oldpassword)) {
            } else {
                if(!document.getElementById("xx")){
                    $(function() {
                        $.jGrowl('<span class="icon icon_info" id="xx">密码组成不符合安全要求，密码必须至少有一个字母, 数字, 和特殊字符（!@#$%^&*），长度8-64位。请重置密码!!!</span>', {life: 10000});
                    });
                }else{
                    document.getElementById("xx").innerText = "密码组成不符合安全要求，密码必须至少有一个字母, 数字, 和特殊字符（!@#$%^&*），长度8-64位。请重置密码!!!！";
                }
                document.getElementById("login_message").innerText = "密码组成不符合安全要求，密码必须至少有一个字母, 数字, 和特殊字符（!@#$%^&*），长度8-64位。请重置密码!!!";			//document.getElementById("login_message").innerHTML="验证码输入错误！"
                return false;
            }

            var ajax = new XMLHttpRequest();
            ajax.open('post', '/appMis/login/checkCode', false);//true为异步 false为同步
            ajax.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            ajax.send('checkCode=' + document.getElementById("checkCode").value);
            var resault = ajax.responseText;
            if (resault == 'true') {
                return true;
            } else {
                if(!document.getElementById("xx")){
                    $(function() {
                        $.jGrowl('<span class="icon icon_info" id="xx">错误,验证码输入错误！！</span>', {life: 10000});
                    });
                }else{
                    document.getElementById("xx").innerText = "错误,验证码输入错误！！！";
                }
                document.getElementById("login_message").innerText = "错误,验证码输入错误！！";			//document.getElementById("login_message").innerHTML="验证码输入错误！"
                return false;
            }
        }

        //生成验证码
        function flushCode() {
            //alert("===="+"${postUrl}")
            // 每次刷新的时候获取当前时间，防止浏览器缓存刷新失败
            var time = new Date();
            //document.getElementById("checkCodeImage").src = "<%=request.getContextPath()%>/getCode?time=" + time;
            //document.getElementById("login_message").innerText="";
            document.getElementById("checkCodeImage").src = '/appMis/login/checkCodeImage?time=' + time;
        }

    </script>
</head>

<body>

<div id='s2ui_header_body'>
    <div id='s2ui_header_title'><g:message code='spring.security.ui.defaultTitle'/></div>
    <g:render template='/includes/ajaxLogin'/>
</div>

<div id="s2ui_main">
    <div id="s2ui_content">
        <div class="login s2ui_center ui-corner-all" style='text-align:center;'>
            <div class="login-inner">
                <h2><g:message code='spring.security.ui.login.signin'/></h2>
                <s2ui:form type='login' focus=''>
                    <div class='login_message' id="login_message">${flash.message}${flash.error}</div>
                    <div class="sign-in">
                        <table>
                            <tr>&nbsp;</tr><tr>&nbsp;</tr><tr>
                            <tr>
                                <td><label for="username"><g:message
                                        code='spring.security.ui.login.username'/></label></td>
                                <td><input type="text" name="${securityConfig.apf.usernameParameter}"
                                           id="username" class='formLogin' size="20"/></td>
                            </tr>
                            <tr>
                                <td><label for="password"><g:message
                                        code='spring.security.ui.login.password'/></label></td>
                                <td><input type="password" name="${securityConfig.apf.passwordParameter}"
                                           id="password" class="formLogin" size="20"/></td>
                            </tr>
                            <tr>
                                <td><label for="checkCode"><g:message
                                        code='spring.security.ui.login.checkCode'/></label></td>
                                <td><input type='text' class='text_1' name='j_checkCode'
                                           id='checkCode' class="formLogin" size="27"/></td>
                                <td>
                                    <span class="checkCode-link">
                                        <div class='checkCode'>
                                            <img alt="验证码" id="checkCodeImage" src='/appMis/login/checkCodeImage'>
                                            <a href="#" onclick="javascript:flushCode();">看不清?</a>
                                        </div>
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <td colspan='2'>
                                    <input type="checkbox" class="checkbox"
                                           name="${securityConfig.rememberMe.parameter}" id="remember_me"
                                           checked="checked"/>
                                    <label for='remember_me'><g:message
                                            code='spring.security.ui.login.rememberme'/></label> |
                                    <span class="forgot-link">
                                        <g:link controller='register' action='forgotPassword'><g:message
                                                code='spring.security.ui.login.forgotPassword'/></g:link>
                                    </span>
                                    <span class="forgot-link">
                                        <g:link controller='register' action='resetPasswordx'><g:message
                                                code='spring.security.ui.login.resetPassword'/></g:link>
                                    </span>
                                    <span class="forgot-link">
                                        <g:link controller='register' action='register'><g:message
                                                code='spring.security.ui.login.register'/></g:link>
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <td colspan='2'>
                                    <!--<s2ui:linkButton elementId='register' controller='register'
                                                         messageCode='spring.security.ui.login.register'/>-->
                                    <s2ui:submitButton elementId='loginButton'
                                                       messageCode='spring.security.ui.login.login'/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </s2ui:form>
            </div>
        </div>
    </div>
</div>

<asset:javascript src='spring-security-ui.js'/>
<s2ui:showFlash/><!-- 动画及延时-->
<s2ui:deferredScripts/>


</body>
</html>
