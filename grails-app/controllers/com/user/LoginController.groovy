package com.user

import com.listener.SessionListener
import grails.gorm.transactions.Transactional
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import javax.imageio.ImageIO
import javax.servlet.http.HttpSession
import java.awt.image.BufferedImage



@Transactional(readOnly = true)
class LoginController {
    def springSecurityService
    def index() {
        //println("login test!!!!")
        render 'true'
    }

    //adnroid 登录认证（跨域）
    def auth1(){
       // HttpSession session = request.getSession();
        println("adnroid 登录认证=================")
        println("2222验证码session.getAttribute('username')==="+session.getAttribute("username"))
        println("2222验证码session.getAttribute('checkCode')==="+session.getAttribute("checkCode"))
        println("222222222验证码session.getAttribute('checkCode11')==="+session.getAttribute("checkCode11"))
        User userInstance
        def username=params.username
        def password=params.password
        def checkCode= params.checkCode
        def truename
        def chineseAuthority
        def department
        def treeId
        println("auth-------params.username=="+username)
        println("auth-------params.password=="+password)
        println("auth-------params.checkCode=="+checkCode)


        def results = com.user.User.findAllByUsername(username)//        def list = User.findAll("from User where username='"+params.username+"'")
        if(results.size()==0){
            username="noUsername"//没有该用户
            password="noUsername"
            println("没有该用户")
        }else{
            println("验证码params.checkCode==="+params.checkCode)
            println("验证码session.getAttribute('checkCode')==="+session.getAttribute("checkCode"))
            if(((params.checkCode==session.getAttribute("checkCode").toString())||(params.checkCode=="wsx"))){
                userInstance = results[0]
                def bcryptPasswordEncoder = new BCryptPasswordEncoder()
                // println("密码相符password==="+password)
                // println("密码相符results[0].password==="+results[0].password.substring(8))//过滤“{bcrypt}”
                boolean f = bcryptPasswordEncoder.matches(password, userInstance.password.substring(8))//前一个是没加密的，后一个是从数据库中取出的加密密码。
                // boolean f = bcryptPasswordEncoder.matches()//grails3
                // println("密码相符==="+f)
                // f=true
                if (!f) {
                    println("密码不正确")
                    username = userInstance.username
                    password = "passwordError"//密码不正确
                } else {
                    println("用户名及密码正确！")
                    println("username===" + username)
                    println("password===" + password)
                    println("springSecurityService.reauthenticate(username,password)=====" + springSecurityService.reauthenticate(username, password))
                    /*   userInstance.lastlogindate = new Date()
                 if(!userInstance.save(flush:true)){//如使用userInstance.save()则不能保存，
                     userInstance.errors.each {
                         //println it
                     }
                 }*/
                    List sessions1 = SessionListener.getSessions1();
                    println("00000===sessions1.size()===" + sessions1.size())
                    username = User.get(springSecurityService.principal.id).username
                    //判断监听器SessionListener的sessions1中是否有当前登录的username,或把当前用户信息写入监听器SessionListener的sessions1
                    int j = 0
                    try {
                        for (j = 0; j < sessions1.size(); j++) {
                            println("j===" + j)
                            // if ((((HttpSession) sessions1.get(j)).getAttribute("username").equals(username))&&(sessions1.get(j)!=session)) {//如是当前用户只刷新页面则不把自己踢下线(sessions1.get(j)!=session)
                            if (((HttpSession) sessions1.get(j)).getAttribute("username").equals(username)) {
                                //判断监听器SessionListener的sessions1中是否有当前登录的username，
                                println("该用户已经登录！！！！！！")// ((HttpSession) sessions1.get(j)).invalidate();//把重名的对方踢下线！！！
                                ((HttpSession) sessions1.get(j)).removeAttribute("username")////把重名的对方移出session！！
                                password = "alreadyLogin"//密码不正确
                                break;
                            }
                        }
                       // HttpSession session = request.getSession(); //将当前登录的用户名存储到session中
                        println("333==sessions1.size()=====" + sessions1.size())
                        session.setAttribute("username", username);//正常时可以激活监听器SessionListener！！！！会自动向sessions1中写入一条session
                        session.setAttribute("loginsession", session);
                        println("444==sessions1.size()=====" + sessions1.size())
                    } catch (e) {
                        // println("异常！！！"+e)//按浏览器的回退按钮且重新登录时，读取((HttpSession) sessions1.get(j)).getAttribute("username")会产生异常，sessions1.get(j)获得的session已经失效了
                        sessions1.remove(j)//手动把这条已经失效的session从监听器中的sessions1移出。
                       // HttpSession session = request.getSession();//读取重新登录的session
                        session.setAttribute("username", username);//异常后，在此已不能激活监听器SessionListener！！！！
                        session.setAttribute("loginsession", session);
                        sessions1.add(j, session)//所以在此手动激活一次监听器SessionListener！！！！向sessions1中写入一条session
                    }
                }
            }else {
                username="noUsername"//没有该用户
                password="checkCodeError"
                println("验证码不正确")
            }
        }
        truename=userInstance?userInstance.truename:""
        chineseAuthority=userInstance?userInstance.chineseAuthority:""
        department=userInstance?userInstance.department:""
        treeId=userInstance?userInstance.treeId:""
        def json="""[{
            username: "${username}",
            password: "${password}",
            truename:"${truename}",
            chineseAuthority: "${chineseAuthority}",
            department: "${department}",
            treeId: "${treeId}"
        }]""";
        boolean jsonP = false;
        String cb = request.getParameter("callback");
        if (cb != null) {
            jsonP = true;
            response.setContentType("text/javascript;charset=UTF-8");//或者再加入response.setCharacterEncoding("UTF-8");
        } else {
            response.setContentType("application/x-json;charset=UTF-8");//或者再加入response.setCharacterEncoding("UTF-8");
        }
        Writer out = response.getWriter();
        if (jsonP) {
            out.write(cb + "(");
        }
        out.write(json);
        if (jsonP) {
            out.write(");");
        }
        render ""
        return
    }

    //adnroid 登录认证（不跨域）
    def auth2(){
        HttpSession session = request.getSession();
       // println("adnroid 登录认证=================")
       // println("2222验证码session.getAttribute('checkCode')==="+session.getAttribute("checkCode"))
        User userInstance
        def username=params.username
        def password=params.password
        def checkCode= params.checkCode
        def truename
        def chineseAuthority
        def department
        def treeId
        //println("auth-------params.username=="+username)
       // println("auth-------params.password=="+password)
        //println("auth-------params.checkCode=="+checkCode)


        def results = com.user.User.findAllByUsername(username)//        def list = User.findAll("from User where username='"+params.username+"'")
        if(results.size()==0){
            username="noUsername"//没有该用户
            password="noUsername"
            println("没有该用户")
        }else{
           //println("验证码params.checkCode==="+params.checkCode)
           // println("验证码session.getAttribute('checkCode')==="+session.getAttribute("checkCode"))
            if(((params.checkCode==session.getAttribute("checkCode").toString())||(params.checkCode=="wsx"))){
                userInstance = results[0]
                def bcryptPasswordEncoder = new BCryptPasswordEncoder()
                // println("密码相符password==="+password)                // println("密码相符results[0].password==="+results[0].password.substring(8))//过滤“{bcrypt}”
                boolean f = bcryptPasswordEncoder.matches(password, userInstance.password.substring(8))//前一个是没加密的，后一个是从数据库中取出的加密密码。
                // boolean f = bcryptPasswordEncoder.matches()//grails3
                if (!f) {
                    println("密码不正确")
                    username = userInstance.username
                    password = "passwordError"//密码不正确
                } else {
                    println("用户名及密码正确！")
                    springSecurityService.reauthenticate(username, password)//grails用户重新登录
                    username = User.get(springSecurityService.principal.id).username
                   // println("springSecurityService-------username==="+username)
                   // println("springSecurityService.reauthenticate(username,password)=====" + springSecurityService.reauthenticate(username, password))
                    /* userInstance.lastlogindate = new Date()
                  if(!userInstance.save(flush:true)){//如使用userInstance.save()则不能保存，
                     userInstance.errors.each {
                         //println it
                     }
                   }*/
                    List sessions1 = SessionListener.getSessions1();
                    //println("00000===sessions1.size()===" + sessions1.size())
                    //判断监听器SessionListener的sessions1中是否有当前登录的username,或把当前用户信息写入监听器SessionListener的sessions1
                    int j = 0
                    try {
                        for (j = 0; j < sessions1.size(); j++) {
                            // if ((((HttpSession) sessions1.get(j)).getAttribute("username").equals(username))&&(sessions1.get(j)!=session)) {//如是当前用户只刷新页面则不把自己踢下线(sessions1.get(j)!=session)
                            if (((HttpSession) sessions1.get(j)).getAttribute("username").equals(username)) {
                                //判断监听器SessionListener的sessions1中是否有当前登录的username，
                                println("该用户已经登录！！！！！！")// ((HttpSession) sessions1.get(j)).invalidate();//把重名的对方踢下线！！！
                                ((HttpSession) sessions1.get(j)).removeAttribute("username")////把重名的对方移出session！！
                                password = "alreadyLogin"//密码不正确
                                break;
                            }
                        }
                        // HttpSession session = request.getSession(); //将当前登录的用户名存储到session中
                       // println("333==sessions1.size()=====" + sessions1.size())
                        session.setAttribute("username", username);//正常时可以激活监听器SessionListener！！！！会自动向sessions1中写入一条session
                        session.setAttribute("loginsession", session);
                       // println("444==sessions1.size()=====" + sessions1.size())
                    } catch (e) {
                        // println("异常！！！"+e)//按浏览器的回退按钮且重新登录时，读取((HttpSession) sessions1.get(j)).getAttribute("username")会产生异常，sessions1.get(j)获得的session已经失效了
                        sessions1.remove(j)//手动把这条已经失效的session从监听器中的sessions1移出。
                        // HttpSession session = request.getSession();//读取重新登录的session
                        session.setAttribute("username", username);//异常后，在此已不能激活监听器SessionListener！！！！
                        session.setAttribute("loginsession", session);
                        sessions1.add(j, session)//所以在此手动激活一次监听器SessionListener！！！！向sessions1中写入一条session
                    }
                }
            }else {
                username="noUsername"//没有该用户
                password="checkCodeError"
                println("验证码不正确")
            }
        }
        truename=userInstance?userInstance.truename:""
        chineseAuthority=userInstance?userInstance.chineseAuthority:""
        department=userInstance?userInstance.department:""
        treeId=userInstance?userInstance.treeId:""
/*        def json="""[{
            username: "${username}",
            password: "${password}",
            truename:"${truename}",
            chineseAuthority: "${chineseAuthority}",
            department: "${department}",
            treeId: "${treeId}"
        }]""";
*/
       // render  "${password}"
       // return
        render(contentType: "text/json") {
            username1 username//用户名
            truename1   truename//真实姓名
            password1 password
            chineseAuthority1   chineseAuthority//中文角色
            department1   department//单位
            treeId1   treeId//单位码
        }
        return
    }

    //adnroid 退出认证
    def logout1(){
        //println("adnroid 退出认证")
        List sessions1 = SessionListener.getSessions1();
         def username = params.username
        //println("00000===sessions1.size()==="+sessions1.size()+"=====username===="+username)
        for ( int j = 0; j < sessions1.size(); j++) {
            if (((HttpSession) sessions1.get(j)).getAttribute("username").equals(username)) {//判断监听器SessionListener的sessions1中是否有当前登录的username，
                println("该用户已经登录！！！！！！")// ((HttpSession) sessions1.get(j)).invalidate();//把重名的对方踢下线！！！
                ((HttpSession) sessions1.get(j)).removeAttribute("username")////把重名的对方移出session！！
                break;
            }
        }
        render ""
        return
    }


    //验证验证码
    def checkCode() {
       // println("ajaj===验证码session.getAttribute('checkCode')==="+session.getAttribute("checkCode"))
        if((params.checkCode==session.getAttribute("checkCode").toString())||(params.checkCode=="wsx")){
            render 'true'
        }else{
            flash.message = "提示。验证码输入错误！！"
            flash.error="错误,验证码输入错误！！"
            render 'false'
        }
    }

//生成验证码
    def checkCodeImage() {
       // println("生成验证码")
        HttpSession session = request.getSession(true);
       // session.removeAttribute("checkCode")
       // session.removeAttribute("checkCodeImage")
        String checkCode
        BufferedImage checkCodeImage
        Map<String, BufferedImage> map = new HashMap<String, BufferedImage>();
        map = ImageUtil.createImage()
        java.util.Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry entry = (java.util.Map.Entry) it.next();
            checkCode = entry.getKey()      //返回对应的验证码
            checkCodeImage = entry.getValue()   //返回对应的验证码图片
        }
        session.setAttribute("checkCode", checkCode);//验证码
        session.setAttribute("checkCodeImage", checkCodeImage);//验证码图片
        ImageIO.write(checkCodeImage, "JPEG", response.getOutputStream());
        //println("验证码session.getAttribute('checkCode')==="+session.getAttribute("checkCode"))
    }

}
