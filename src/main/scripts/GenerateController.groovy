import org.grails.cli.interactive.completers.DomainClassCompleter

description( "Generates a controller that performs CRUD operations" ) {
  usage "grails generate-controller [DOMAIN CLASS]"
  completer DomainClassCompleter
  flag name:'force', description:"Whether to overwrite existing files"
}


if(args) {
  def classNames = args
  if(args[0] == '*') {
    classNames = resources("file:grails-app/domain/**/*.groovy")
                    .collect { className(it) }
  }

    def sourceClass = source(args[0])
    def overwrite = flag('force') ? true : false
    if(sourceClass) {
      def model1 = model(sourceClass)
      java.util.HashMap model=new java.util.HashMap()
      model.put("className",model1.getClassName())
      model.put("fullName",model1.getFullName())
      model.put("packageName",model1.getPackageName())
      model.put("packagePath",model1.getPackagePath())
      model.put("propertyName",model1.getPropertyName())
      model.put("lowerCaseName",model1.getLowerCaseName())
      model.put("domain",args)

      render template: template('scaffolding/Controller.groovy'),
              destination: file("grails-app/controllers/${model.packagePath}/${model.className}Controller.groovy"),
             //destination: file("grails-app/controllers/${model.packagePath}/${model.convention('Controller')}.groovy"),
             model: model,
              overwrite: true//overwrite: overwrite




      addStatus "Scaffolding completed for ${projectPath(sourceClass)}"                                         
    }
    else {
      error "Domain class not found for name $arg"
    }

}
else {
    error "No domain class specified"
}
