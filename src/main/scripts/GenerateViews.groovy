import org.grails.cli.interactive.completers.DomainClassCompleter

description( "Generates GSP views for the specified domain class" ) {
    usage "grails generate-views [DOMAIN CLASS]|*"
    argument name:'Domain Class', description:"The name of the domain class, or '*' for all", required:true
    completer DomainClassCompleter
    flag name:'force', description:"Whether to overwrite existing files"
}

if(args) {
    def classNames = args
    if(args[0] == '*') {
        classNames = resources("file:grails-app/domain/**/*.groovy").collect { className(it) }
    }


    def viewNames = resources("file:src/main/templates/scaffolding/*.js")
                .collect {
        it.filename
    }
    if(!viewNames) {
       viewNames = resources("classpath*:META-INF/templates/scaffolding/*.js")
                   .collect {
            it.filename
       } 
    }

        def sourceClass = source(args[0])
        def overwrite = flag('force') ? true : false
        if(sourceClass) {
            def model1 = model(sourceClass)
            java.util.Map model=new java.util.HashMap()
            model.put("className",model1.getClassName())
            model.put("fullName",model1.getFullName())
            model.put("packageName",model1.getPackageName())
            model.put("packagePath",model1.getPackagePath())
            model.put("propertyName",model1.getPropertyName())
            model.put("lowerCaseName",model1.getLowerCaseName())
            model.put("domain",args)
           // viewNames.each {
                //创建Model.js
                      render   template: template('scaffolding/Model.js'),//template: new java.io.File('scaffolding/'+it),
                                destination: file("grails-app/assets/extjsapps/app/model/${model.className}Model.js"),
                                model: model,
                               overwrite: true//overwrite: overwrite


                //创建Store.js
                       render   template: template('scaffolding/Store.js'),//template: new java.io.File('scaffolding/'+it),
                                destination: file("grails-app/assets/extjsapps/app/store/${model.className}Store.js"),
                                model: model,
                                overwrite: true//overwrite: overwrite

               //创建Controller.js
                        render   template: template('scaffolding/Controller.js'),//template: new java.io.File('scaffolding/'+it),
                                 destination: file("grails-app/assets/extjsapps/app/controller/${model.className}.js"),
                                 model: model,
                                 overwrite: true//overwrite: overwrite

            //创建Controller.js
            render   template: template('scaffolding/Controller.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/controller/${model.className}.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite

            //SelectExportItem.js
            render   template: template('scaffolding/view/SelectExportItem.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/view/${model.propertyName}/SelectExport${model.className}Item.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite

               //SelectImportItem.js
               render   template: template('scaffolding/view/SelectImportItem.js'),//template: new java.io.File('scaffolding/'+it),
                       destination: file("grails-app/assets/extjsapps/app/view/${model.propertyName}/SelectImport${model.className}Item.js"),
                       model: model,
                       overwrite: true//overwrite: overwrite

              //Viewport.js
               render   template: template('scaffolding/view/Viewport.js'),//template: new java.io.File('scaffolding/'+it),
                       destination: file("grails-app/assets/extjsapps/app/view/${model.propertyName}/${model.className}Viewport.js"),
                       model: model,
                       overwrite: true//overwrite: overwrite

          //GridViewport.js
             render   template: template('scaffolding/view/GridViewport.js'),//template: new java.io.File('scaffolding/'+it),
                     destination: file("grails-app/assets/extjsapps/app/view/${model.propertyName}/${model.className}GridViewport.js"),
                     model: model,
                     overwrite: true//overwrite: overwrite

             //Add.js
             render   template: template('scaffolding/view/Add.js'),//template: new java.io.File('scaffolding/'+it),
                     destination: file("grails-app/assets/extjsapps/app/view/${model.propertyName}/${model.className}Add.js"),
                     model: model,
                     overwrite: true//overwrite: overwrite

              //Edit.js
            render   template: template('scaffolding/view/Edit.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/view/${model.propertyName}/${model.className}Edit.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite

            //创建桌面Window.js
            render   template: template('scaffolding/view/desktop/Window.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/view/desktop/${model.className}Window.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite

            //创建main下Main.js
            render   template: template('scaffolding/view/main/Main.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/view/main/${model.className}Main.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite

           //创建main下MainController.js
            render   template: template('scaffolding/view/main/MainController.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/view/main/${model.className}MainController.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite

             //创建main下region下Center.js
            render   template: template('scaffolding/view/main/region/Center.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/view/main/region/${model.className}Center.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite

            //创建main下region下Left.js
            render   template: template('scaffolding/view/main/region/Left.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/view/main/region/${model.className}Left.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite

            //创建main下menu下TreeMainMenu.js
            render   template: template('scaffolding/view/main/menu/TreeMainMenu.js'),//template: new java.io.File('scaffolding/'+it),
                    destination: file("grails-app/assets/extjsapps/app/view/main/menu/${model.className}TreeMainMenu.js"),
                    model: model,
                    overwrite: true//overwrite: overwrite






           // }

            addStatus "Views generated for ${projectPath(sourceClass)}"
        } else {
            error "Domain class not found for name $arg"
        }

} else {
    error "No domain class specified"
}
