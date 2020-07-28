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
        classNames = resources("file:grails-app/domain/**/*.groovy")
                .collect { className(it) }
    }


    def overwrite = flag('force') ? true : false
    def model1 = model(args[0])
    java.util.HashMap model=new java.util.HashMap()
    model.put("className",model1.getClassName())
    model.put("fullName",model1.getFullName())
    model.put("packageName",model1.getPackageName())
    model.put("packagePath",model1.getPackagePath())
    model.put("propertyName",model1.getPropertyName())
    model.put("lowerCaseName",model1.getLowerCaseName())
    model.put("domain",args)

            /*java.util.Map model=new java.util.HashMap()
            model.put("className","Teacher")
            model.put("fullName","Teacher")
            model.put("packageName","com.app")
            model.put("packagePath","com/app")
            model.put("propertyName","teacher")
            model.put("lowerCaseName","teacher")
            model.put("domain",args)*/

            render  template: template('scaffolding/Domain.groovy'),
                    destination: file("grails-app/domain/${model.packagePath}/${model.className}.groovy"),
                    model: model,
                    overwrite: true//overwrite: overwrite

           // }

            addStatus "Views generated for ${projectPath(sourceClass)}"




} else {
    error "No domain class specified"
}
