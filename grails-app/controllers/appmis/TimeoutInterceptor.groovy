package appmis



class TimeoutInterceptor {
    public TimeoutInterceptor() {
        match(controller:"department|user|role|requestmap", action:"*")
                 .excludes(controller:"login")
    }
    boolean before() {
        //println("before")
       // println("before----Interceptor======"+session.getAttribute("username"))
        // int totalCount1=0
        if (!session.getAttribute("username")) {
           /* StringWriter writer = new StringWriter()
            StreamingJsonBuilder builder = new StreamingJsonBuilder(writer)
            builder{
                totalCount  "-1"
            }
            render JsonOutput.prettyPrint(writer.toString())*/
            render(contentType: "application/json") {
                totalCount  "-1"
            }
          // render "{totalCount:'-1'}"
           // println("time is over")
            return false
        }else{
           // println("time is not over")
            true
        }
    }

    boolean after() {
        true
    }

    void afterView() {
        // no-op
    }


    boolean xxxxbeforebak() {
       // println("Interceptor======"+session.getAttribute("username"))
        // int totalCount1=0
        if (!session.getAttribute("username")) {
            /*StringWriter writer = new StringWriter()
            StreamingJsonBuilder builder = new StreamingJsonBuilder(writer)
            builder{
                totalCount  "-1"
            }
            render JsonOutput.prettyPrint(writer.toString())*/
            //或者
            def json="""[{
                 username: "",
                  password: "-1",
                  truename:"",
                  chineseAuthority: "",
                  department: "",
                  treeId: ""
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
            render "{password:'-1'}"
          //  println("time is over")
            return false
        }else{
           // println("time is not over")
            render ""
            true

            /*render "{password:'timeout'}"//密码不正确
            //println("time is over")
             true*/
        }
        true
    }


}
