package appmis

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        //"/"(view:"/index")
        "/"(redirect:'/assets/index.html')
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
