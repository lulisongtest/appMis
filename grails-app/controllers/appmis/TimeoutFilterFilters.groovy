package appmis
//Grails3.0以后已经不使用过滤器了！！！！！！！！！
class TimeoutFilterFilters {
    /*def filters = {
        //all(controller:'articles', action:'*') {
        //all(controller:'*',controllerExclude:'/login', action:'*',actionExclude:'auth',uriExclude: 'login/auth') {//controllerExclude不起作用
        all(controller:'articles|articlesUse|notice|rules|department|user|role|requestmap', action:'*') {
            before = {
                println("Filters======"+session.getAttribute("username"))
                if (!session.getAttribute("username")) {
                    //println "no__Tracing action ${actionUri}"
                   // render(contentType: "text/json") {
                    //    totalCount = -1
                   // }
                    render  "{totalCount:'-1'}"
                    println("time is over")
                    return false

                }
                //println "yes___Tracing action ${actionUri}"
            }
            after = { Map model ->
            }
            afterView = { Exception e ->
            }
        }
    }*/
}
