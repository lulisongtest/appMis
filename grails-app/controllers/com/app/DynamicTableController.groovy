package com.app

import com.app.DynamicTable
import com.user.Requestmap
import grails.gorm.transactions.Transactional
import grails.web.context.ServletContextHolder
import groovy.sql.Sql
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.grails.web.json.JSONObject
import org.springframework.http.MediaType

import java.text.SimpleDateFormat


@Transactional(readOnly = false)
class DynamicTableController {
    def springSecurityService
    //def grailsApplication// 注入是为了直接执行原始的SQL语句def dataSource = grailsApplication.mainContext.getBean('dataSource')
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]//已生成的相关文件及信息全部删除

    //修改DesktopApp.js
    def modifyDesktopAppjs(domain) {
        String query = "from DynamicTable where tableNameId='" + domain + "'"
        def list = DynamicTable.findAll(query)
        String s = list[0].tableName
        s = s.substring(0, (s.length() - 1))//中文表名
        String fileName = ServletContextHolder.getServletContext().getRealPath("/")
        fileName = fileName.split("src")[0] + "grails-app\\assets\\extjsapps\\app\\view\\desktop\\App.js"
        try {
            FileReader fr = new FileReader(fileName)
            StringBuilder sb = new StringBuilder()
            String line = null
            while ((line = fr.readLine()) != null) {
                if (line.trim() == "") {
                    continue
                }
                sb.append(line + "\n")
                if (line.trim() == "requires: [") {
                    sb.append("                    'appMis.view.desktop." + initcap2(domain) + "Window',\r\n")
                    sb.append("                    'appMis.view.main." + initcap2(domain) + "Main',\r\n"); continue
                } else {
                    if (line.indexOf(initcap2(domain)) != -1) {
                        fr.close(); return//已经修改过了
                    }
                }
                if (line.trim() == "data0 = [") {
                    sb.append("                                new appMis.view.desktop." + initcap2(domain) + "Window(),\r\n")
                    continue
                }
                if (line.trim() == "data1 = [") {
                    sb.append("                                {name: '" + s + "', nameId: '" + domain + "',iconCls: 'employee-shortcut',module: '" + domain + "'},\r\n")
                    continue
                }
                if (line.trim() == "data2 = [") {
                    sb.append("                                {name: '" + s + "', iconCls: 'emplyee-shortcut', module: '" + domain + "'},\r\n")
                    continue
                }
            }
            fr.close()
            FileWriter fw = new FileWriter(fileName)
            PrintWriter pw = new PrintWriter(fw)
            pw.println(sb)
            pw.flush()
            pw.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    //修改app.js
    def modifyAppjs(domain) {
        // String query="from DynamicTable where tableNameId='"+ domain+"'"
        // def list = DynamicTable.findAll(query)
        String fileName = ServletContextHolder.getServletContext().getRealPath("/")
        fileName = fileName.split("src")[0] + "grails-app\\assets\\extjsapps\\app.js"
        try {
            FileReader fr = new FileReader(fileName)
            StringBuilder sb = new StringBuilder()
            String line = null
            while ((line = fr.readLine()) != null) {
                if (line.trim() == "") {
                    continue
                }
                sb.append(line + "\n")
                if (line.trim() == "controllers:[") {
                    sb.append("        '" + initcap2(domain) + "',\r\n"); continue
                } else {
                    if (line.indexOf(initcap2(domain)) != -1) {
                        fr.close(); return//已经修改过了
                    }
                }
                if (line.trim() == "stores:[") {
                    sb.append("        '" + initcap2(domain) + "Store',\r\n"); continue
                }
                if (line.trim() == "views:[") {
                    sb.append("        '" + domain + "." + initcap2(domain) + "Add',\r\n")
                    sb.append("        '" + domain + "." + initcap2(domain) + "Edit',\r\n")
                    sb.append("        '" + domain + "." + initcap2(domain) + "Viewport',\r\n")
                    sb.append("        '" + domain + "." + initcap2(domain) + "GridViewport',\r\n");  continue
                }
            }
            fr.close()
            FileWriter fw = new FileWriter(fileName)
            PrintWriter pw = new PrintWriter(fw)
            pw.println(sb)
            pw.flush()
            pw.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    //从数据库中获取【字段类型 字段名 字段中文名】
    String fields(domain) {
        String query = "from DynamicTable where tableNameId='" + domain + "'"
        def list = DynamicTable.findAll(query)
        if (list[0].fieldLength == 0) return null
        String s = list[0].tableName//中文表名
        s = s.substring(0, (s.length() - 1))
        for (int i = 0; i < list.size(); i++) {
            switch (list[i].fieldType) {
                case "字符":
                    s = s + " String "
                    break
                case "公式":
                    s = s + " String "
                    break
                case "浮点数":
                    s = s + " Double "
                    break
                case "整数":
                    s = s + " Integer "
                    break
                case "日期":
                    s = s + " Date "
                    break
                case "布尔":
                    s = s + " Boolen "
                    break
                case "字节流":
                    s = s + " Blob "
                    break
            }
            s = s + list[i].fieldNameId + " " + list[i].fieldName
        }
        return s
    }

    //利用Template创建表的域Domain.Groovy
    def createDomain() {
        String domain = params.dynamicTableNameId
        def dataSource = grailsApplication.mainContext.getBean('dataSource')// 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String s = fields(domain)
        if (!s) {
            render "failure1"//此表无字段，不适合自动生成Domain！！！
            return
        }
        try {
            Runtime.getRuntime().exec("cmd   /E:ON   /c   start   C:/Java/jdk1.8.0_151/bin/java.exe -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:CICompilerCount=3 -Djline.WindowsTerminal.directConsole=false -Dfile.encoding=UTF-8 -classpath C:\\grails-4.0.1\\dist\\classpath.jar org.grails.cli.GrailsCli generate-domain com.app." + initcap2(domain) + " " + s + " --plain-output")
        } catch (IOException e) {
            e.printStackTrace()
        }

        //如修改表字段后，探测数据表是否有多余字段？如有，则删除多余的字段。
        String query = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE table_name = '" + domain + "' AND table_schema = 'appmis'"
        def listold = sql.rows(query)//取MYSQL数据表中的字段名
        if (listold.size() == 0) {
            render "success"//第一生成，数据库中还没有该表
            return
        }
        Class demo = Class.forName('com.app.' + (initcap2(domain)))//此处一定要包名！
        def field = demo.getDeclaredFields()
        int num = 0
        ArrayList<String> fieldName = new ArrayList<String>()//可以自由向数组中增加元素
        String cc, dd
        char c
        for (int ii = 0; ii < field.size(); ii++) {
            fieldName[ii] = field[ii].getName().toString()
            if (fieldName[ii] == "constraints") {//只读取字段名称
                num = ii//有效字段数量
                break
            }
            for (int iii = 0; iii < fieldName[ii].length(); iii++) {//解决Domain中的字段名与Mysql中字段名不一致的问题
                c = fieldName[ii].charAt(iii)
                if (Character.isUpperCase(c)) {
                    cc = c
                    dd = (char) (c + 32)
                    fieldName[ii] = fieldName[ii].replaceAll(cc, "_" + dd)
                }
            }
        }
        String oldfield
        for (int i = 0; i < listold.size(); i++) {//表结构修改后，查找数据表中多余的字段并且删除它
            int j = 0
            for (j = 0; j < num; j++) {
                oldfield = listold[i].get("COLUMN_NAME")
                if (oldfield.equals((fieldName[j]).toString())) {
                    break
                }
            }
            if (j == num && (listold[i].COLUMN_NAME != "id") && (listold[i].COLUMN_NAME != "version")) {
                //listold[i].COLUMN_NAME是多余字段，删除它(除id、version外)
                query = "ALTER TABLE " + domain + " DROP COLUMN " + (listold[i].COLUMN_NAME)
                sql.execute(query)
            }
        }
        render "success"     //  render "动态创建Domain并编译成功"
    }

    //利用创建文件的形式创建表的域
    def createDomainBak() {
        String domain = params.dynamicTableNameId
        def dataSource = grailsApplication.mainContext.getBean('dataSource')// 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String currentDir = getServletContext().getRealPath("/")  //生成的currentDir为D:\myPro4\appMis\src\main\webapp\
        //String currentDir = org.codehaus.groovy.grails.web.context.ServletContextHolder.servletContext.getRealPath('/')//获取的是D:\appMis\web-app\
        currentDir = currentDir.substring(0, currentDir.length() - 16)//获取的是D:\myPro4\appMis\
        String query = "from DynamicTable where tableNameId='" + domain + "'"
        // println("query ===000==="+query)
        def list = DynamicTable.findAll(query)
        if (list[0].fieldLength == 0) {
            render "failure1"//此表无字段，不适合自动生成Domain！！！
            return
        }
        //createDomain1(currentDir + "\\grails-app\\domain\\appMis\\" + params.dynamicTableNameId);//在IDEAL环境中
        //createDomain1(currentDir + "\\grails-app\\domain\\com\\app\\" + params.dynamicTableNameId);//在IDEAL环境中
        //CreateDomain("c:\\appMis\\"+ params.dynamicTableName);//在发布到Tomcat环境中

        String tablename = currentDir + "\\grails-app\\domain\\com\\app\\" + domain
        //render "success"
        // return


        StringBuffer sb = new StringBuffer()
        sb.append("package com.app;\r\n")
        sb.append("import java.sql.Blob;\r\n")

        sb.append("class " + initcap1(tablename) + "{\r\n")
        for (int i = 0; i < list.size(); i++) {
            if (((DynamicTable) list[i]).fieldType == "字符") sb.append("    String " + ((DynamicTable) list[i]).fieldNameId + ";//" + ((DynamicTable) list[i]).fieldName + "\r\n")
            if (((DynamicTable) list[i]).fieldType == "公式") sb.append("    String " + ((DynamicTable) list[i]).fieldNameId + ";//" + ((DynamicTable) list[i]).fieldName + "\r\n")
            if (((DynamicTable) list[i]).fieldType == "浮点数") sb.append("    Double " + ((DynamicTable) list[i]).fieldNameId + " = 0.0;//" + ((DynamicTable) list[i]).fieldName + "\r\n")
            if (((DynamicTable) list[i]).fieldType == "整数") sb.append("    Integer " + ((DynamicTable) list[i]).fieldNameId + " = 0;//" + ((DynamicTable) list[i]).fieldName + "\r\n")
            if (((DynamicTable) list[i]).fieldType == "日期") sb.append("    Date " + ((DynamicTable) list[i]).fieldNameId + ";//" + ((DynamicTable) list[i]).fieldName + "\r\n")
            if (((DynamicTable) list[i]).fieldType == "布尔") sb.append("    Boolean " + ((DynamicTable) list[i]).fieldNameId + ";//" + ((DynamicTable) list[i]).fieldName + "\r\n")
            if (((DynamicTable) list[i]).fieldType == "字节流") sb.append("    Blob " + ((DynamicTable) list[i]).fieldNameId + ";//" + ((DynamicTable) list[i]).fieldName + "\r\n")
        }
        sb.append("static constraints ={\r\n")
        for (int i = 0; i < list.size(); i++) {
            sb.append("    " + ((DynamicTable) list[i]).fieldNameId + "(blank:true,nullable:true);\r\n")
        }
        sb.append("}\r\n")
        sb.append("static searchable=true;//可以全文搜索\r\n")
        sb.append("}\r\n")

        try {
            FileWriter fw = new FileWriter(initcap(tablename) + ".groovy")//如已有文
            PrintWriter pw = new PrintWriter(fw)
            pw.println(sb.toString())
            pw.flush()
            pw.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
        try {
            // Runtime.getRuntime().exec( "cmd   /E:ON   /c   start   C:\\Groovy\\Groovy-2.2.2\\bin\\groovyc.exe   -classpath  C:\\apache-tomcat-8.0.5\\webapps\\appMis\\WEB-INF\\classes\\appMis  -d  C:\\apache-tomcat-8.0.5\\webapps\\appMis\\WEB-INF\\classes  c:\\appMis\\Test_123.groovy");
        } catch (IOException e) {
            e.printStackTrace()
        }
        //如修改表字段后，探测数据表是否有多余字段？如有，则删除多余的字段。
        query = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE table_name = '" + domain + "' AND table_schema = 'appmis'"
        def listold = sql.rows(query)//取MYSQL数据表中的字段名
        if (listold.size() == 0) {
            render "success"//第一生成，数据库中还没有该表
            return
        }
        Class demo = Class.forName('com.app.' + (initcap2(domain)))//此处一定要包名！
        def field = demo.getDeclaredFields()
        int num = 0
        ArrayList<String> fieldName = new ArrayList<String>()//可以自由向数组中增加元素
        String cc, dd
        char c
        for (int ii = 0; ii < field.size(); ii++) {
            fieldName[ii] = field[ii].getName().toString()
            if (fieldName[ii] == "constraints") {//只读取字段名称
                num = ii//有效字段数量
                break
            }
            for (int iii = 0; iii < fieldName[ii].length(); iii++) {//解决Domain中的字段名与Mysql中字段名不一致的问题
                c = fieldName[ii].charAt(iii)
                if (Character.isUpperCase(c)) {
                    cc = c
                    dd = (char) (c + 32)
                    fieldName[ii] = fieldName[ii].replaceAll(cc, "_" + dd)
                }
            }
        }
        String oldfield
        for (int i = 0; i < listold.size(); i++) {//表结构修改后，查找数据表中多余的字段并且删除它
            int j = 0
            for (j = 0; j < num; j++) {
                oldfield = listold[i].get("COLUMN_NAME")
                if (oldfield.equals((fieldName[j]).toString())) {
                    break
                }
            }
            if (j == num && (listold[i].COLUMN_NAME != "id") && (listold[i].COLUMN_NAME != "version")) {
                //listold[i].COLUMN_NAME是多余字段，删除它(除id、version外)
                query = "ALTER TABLE " + domain + " DROP COLUMN " + (listold[i].COLUMN_NAME)
                sql.execute(query)
            }
        }
        render "success"     //  render "动态创建Domain并编译成功"
    }
    //创建表的控制器
    def createController() {
        String domain = params.dynamicTableNameId
        String s = fields(domain)
        if (!s) {
            render "failure1"//此表无字段，不适合自动生成Domain！！！
            return
        }
        /*String query = "from DynamicTable where tableNameId='" +domain + "'"
        def list = DynamicTable.findAll(query)
        if (list[0].fieldLength == 0) {
            render "failure1"//此表不适合自动生成Controller！！！
            return
        }
        String entityName = "com.app."+initcap2(domain)
        Class entityClass
        try {
            entityClass = grailsApplication.getClassForName(entityName)
        } catch (Exception e) {
            throw new RuntimeException("Failed to load class with name '${entityName}'", e)
        }
        //从定义的类中取出有效字段数n
        def list1=entityClass.getDeclaredFields()
        int n = 0
        for (int i = 0; i < list1.size(); i++) if (list1[i].getName() != "constraints") {
            n++
        } else {
            break
        }
        String s=n
        for (int i = 0; i < n; i++) {
            s=s+" "+list1[i].getName()+" "+(list1[i].getType().toString().split("\\.")[-1])
         }*/
        try {
            // Runtime.getRuntime().exec("cmd   /E:ON   /c   start   C:\\Java\\jdk1.8.0_20\\bin\\java -Dgrails.home=C:\\grails-2.4.4 -Dtools.jar=C:\\Java\\jdk1.8.0_20\\lib\\tools.jar -Dgroovy.starter.conf=C:\\grails-2.4.4/conf/groovy-starter.conf -Djline.WindowsTerminal.directConsole=false -Dbase.dir=D:\\rkSalaryMis -Dfile.encoding=UTF-8 -classpath C:\\grails-2.4.4\\lib\\org.codehaus.groovy\\groovy-all\\jars\\groovy-all-2.3.7.jar;C:\\grails-2.4.4\\dist\\grails-bootstrap-2.4.4.jar org.codehaus.groovy.grails.cli.support.GrailsStarter --main org.codehaus.groovy.grails.cli.GrailsScriptRunner --conf C:\\grails-2.4.4/conf/groovy-starter.conf \"generate-controller appMis." + initcap2(params.dynamicTableNameId) + " -plain-output");
            // Runtime.getRuntime().exec("cmd   /E:ON   /c   start   C:\\Java\\jdk1.8.0_20\\bin\\java -Dgrails.home=C:\\grails-2.4.4 -Dbase.dir=D:\\appMis -Dtools.jar=C:\\Java\\jdk1.8.0_20\\lib\\tools.jar -Dgroovy.starter.conf=C:\\grails-2.4.4/conf/groovy-starter.conf -Djline.WindowsTerminal.directConsole=false -Dfile.encoding=UTF-8 -classpath C:\\grails-2.4.4\\lib\\org.codehaus.groovy\\groovy-all\\jars\\groovy-all-2.3.7.jar;C:\\grails-2.4.4\\dist\\grails-bootstrap-2.4.4.jar org.codehaus.groovy.grails.cli.support.GrailsStarter --main org.codehaus.groovy.grails.cli.GrailsScriptRunner --conf C:\\grails-2.4.4/conf/groovy-starter.conf \"generate-controller com.user." + initcap2(params.dynamicTableNameId) + " -plain-output");
            // Runtime.getRuntime().exec("cmd   /E:ON   /c   start   C:/Java/jdk1.8.0_151/bin/java -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:CICompilerCount=3 -Djline.WindowsTerminal.directConsole=false -Dfile.encoding=UTF-8 -classpath C:\\grails-3.3.2\\dist\\classpath.jar org.grails.cli.GrailsCli generate-controller com.app." + initcap2(params.dynamicTableNameId));
            Runtime.getRuntime().exec("cmd   /E:ON   /c   start   C:/Java/jdk1.8.0_151/bin/java.exe -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:CICompilerCount=3 -Djline.WindowsTerminal.directConsole=false -Dfile.encoding=UTF-8 -classpath C:\\grails-4.0.1\\dist\\classpath.jar org.grails.cli.GrailsCli generate-controller com.app." + initcap2(domain) + " " + s + " --plain-output")
        } catch (IOException e) {
            e.printStackTrace()
        }
        //修改、创建访问控制
        String query = "from Requestmap where url='/" + params.dynamicTableNameId + "/**'"
        def rlist = Requestmap.findAll(query)
        if (rlist.size() > 1) {
            for (int i = 1; i < rlist.size(); i++) {//删除该表多余的控制记录信息
                try {
                    if (rlist[i].delete()) { //先删除requestmap中的相关记录
                        //springSecurityService.clearCachedRequestmaps()//让requestmap修改后生效
                    } else {
                        rlist[i].clearErrors()
                    }
                } catch (e) {
                    rlist[i].clearErrors()
                }
            }
        }
        if (rlist.size() == 0) {//没有该表多余的控制记录信息，则增加一条该表新的控制记录信息
            Requestmap requestmapInstance = new Requestmap()//删除后再增加requestmap中的相关记录
            requestmapInstance.setConfigAttribute("permitAll")
            requestmapInstance.setUrl("/" + params.dynamicTableNameId + "/**")
            requestmapInstance.setChineseUrl(list[0].tableName)
            // requestmapInstance.setTableName(list[0].tableName)
            requestmapInstance.setRoleList("普通用户,管理员")
            requestmapInstance.setTreeId("0")
            requestmapInstance.setGlyph("0xf0c9")
            // println("修改、创建访问控制" + list[0].tableName)
            try {
                if (requestmapInstance.save(flush: true)) {
                    springSecurityService.clearCachedRequestmaps()//让requestmap修改后生效
                    // println("修改、创建访问控制-----成功！")
                } else {
                    requestmapInstance.errors.each {
                        //  println("修改、创建访问控制-----失败！")
                        println it
                    }
                    render "failure"
                    return
                }
            } catch (e) {
                render "failure"
                return
            }
        }
        //如修改表字段后，探测数据表是否有多余字段？
        /*       query="SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE table_name = '"+params.dynamicTableNameId+"' AND table_schema = 'appMis'"
        ApplicationContext ctx = (ApplicationContext) ApplicationHolder.getApplication().getMainContext();
        def dataSource = ctx.getBean('dataSource')*/
        query = "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE table_name = '" + params.dynamicTableNameId + "' AND table_schema = 'appmis'"
        def dataSource = grailsApplication.mainContext.getBean('dataSource')// 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        def listold = sql.rows(query)//取MYSQL数据表中的字段名
        Class demo = Class.forName('com.app.' + (initcap2(params.dynamicTableNameId)))//此处一定要包名！
        def field = demo.getDeclaredFields()
        //println("field[0].getName().toString()=====" + field[0].getName().toString())
        int num = 0
        ArrayList<String> fieldName = new ArrayList<String>()//可以自由向数组中增加元素
        String cc, dd
        char c
        for (int ii = 0; ii < field.size(); ii++) {
            fieldName[ii] = field[ii].getName().toString()
            if (fieldName[ii] == "constraints") {//只读取字段名称
                num = ii//有效字段数量
                break
            }
            for (int iii = 0; iii < fieldName[ii].length(); iii++) {//解决Domain中的字段名与Mysql中字段名不一致的问题
                c = fieldName[ii].charAt(iii)
                if (Character.isUpperCase(c)) {
                    cc = c
                    dd = (char) (c + 32)
                    fieldName[ii] = fieldName[ii].replaceAll(cc, "_" + dd)
                }
            }
            //println("fieldName["+ii+"]===="+fieldName[ii])
        }
        String oldfield
        for (int i = 0; i < listold.size(); i++) {//表结构修改后，查找数据表中多余的字段并且删除它
            int j = 0
            for (j = 0; j < num; j++) {
                oldfield = listold[i].get("COLUMN_NAME")
                if (oldfield.equals((fieldName[j]).toString())) {
                    break
                }
            }
            if (j == num && (listold[i].COLUMN_NAME != "id") && (listold[i].COLUMN_NAME != "version")) {
                //listold[i].COLUMN_NAME是多余字段，删除它(除id、version外)
                query = "ALTER TABLE " + params.dynamicTableNameId + " DROP COLUMN " + (listold[i].COLUMN_NAME)
                sql.execute(query)
            }
        }
        //println("last!!!!!!!!!!!修改、创建访问控制-----成功！")
        render "success"        //render "动态创建Controller并编译成功"
    }
    //创建与表相关的所有Js文件
    def completeView() {
        String domain = params.dynamicTableNameId
        String s = fields(domain)
        if (!s) {
            render "failure1"//此表无字段，不适合自动生成Domain！！！
            return
        }
        /* String query = "from DynamicTable where tableNameId='" + domain + "'"
         def list = DynamicTable.findAll(query)
         if (list[0].fieldLength == 0) {
             render "failure1"//此表不适合自动生成相关文件！！！
             return
         }
         String entityName = "com.app."+initcap2(domain)
         Class entityClass
         try {
             entityClass = grailsApplication.getClassForName(entityName)
         } catch (Exception e) {
             throw new RuntimeException("Failed to load class with name '${entityName}'", e)
         }
         //从定义的类中取出有效字段数n
         def list1=entityClass.getDeclaredFields()
         int n = 0
         for (int i = 0; i < list1.size(); i++) if (list1[i].getName() != "constraints") {
             n++
         } else {
             break
         }
         //从定义的类中取出有效字段名
         String[] fieldName = new String[n]
         String[] fieldType = new String[n]

         String s=n
         for (int i = 0; i < n; i++) {
             s=s+" "+list1[i].getName()+" "+(list1[i].getType().toString().split("\\.")[-1])
            // fieldName[i] = list1[i].getName();
            // fieldType[i] = list1[i].getType()
            // println("fieldName["+i+"]===="+fieldName[i]+"--------fieldType["+i+"]===="+fieldType[i]+"====fieldType[i]==java.util.Date===="+(list1[i].getType()==java.util.Date))            //println("fieldName["+i+"]===="+fieldName[i]+"--------fieldType["+i+"]===="+fieldType[i]+"====fieldType[i]==java.util.Date===="+(fieldType[i]=="class java.util.Date"))
         }*/
        try {
            Runtime.getRuntime().exec("cmd   /E:ON   /c   start   C:/Java/jdk1.8.0_151/bin/java.exe -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:CICompilerCount=3 -Djline.WindowsTerminal.directConsole=false -Dfile.encoding=UTF-8 -classpath C:\\grails-4.0.1\\dist\\classpath.jar org.grails.cli.GrailsCli generate-views com.app." + initcap2(domain) + " " + s + " --plain-output")
        } catch (IOException e) {
            e.printStackTrace()
        }

        //修改App.js
        modifyAppjs(domain)

        //修改DesktopApp.js
        modifyDesktopAppjs(domain)

        render "success"
        return


        // println("正在创建表名Model.js.......")
        CreateDomainModeljs createDomainModeljs = new CreateDomainModeljs()
        createDomainModeljs.createDomainModeljs(params.dynamicTableNameId)//在IDEAL环境中 //创建 表名Model.js
        // println("表名Model.js生成成功")

        CreateDomainStorejs createDomainStorejs = new CreateDomainStorejs()
        createDomainStorejs.createDomainStorejs(params.dynamicTableNameId)//在IDEAL环境中//创建 表名Stroe.js
        // println("表名Stroe.js生成成功")


        CreateDomainStore1js createDomainStore1js = new CreateDomainStore1js()
        createDomainStore1js.createDomainStore1js(params.dynamicTableNameId)//在IDEAL环境中//创建 表名Stroe1.js
        //  println("表名Stroe1.js生成成功")
        CreateDomainStore2js createDomainStore2js = new CreateDomainStore2js()
        createDomainStore2js.createDomainStore2js(params.dynamicTableNameId)//在IDEAL环境中//创建 表名Stroe2.js
        //  println("表名Stroe2.js生成成功")
        CreateDomainStore3js createDomainStore3js = new CreateDomainStore3js()
        createDomainStore3js.createDomainStore3js(params.dynamicTableNameId)//在IDEAL环境中//创建 表名Stroe3.js
        // println("表名Stroe3.js生成成功")


        CreateDynamicTableStorejs createDynamicTableStorejs = new CreateDynamicTableStorejs()
        createDynamicTableStorejs.createDynamicTableStorejs(params.dynamicTableNameId)//在IDEAL环境中//创建 表名Stroe.js
        //  println("DynamicTableStore表名.js生成成功")

        CreateControllerjs createControllerjs = new CreateControllerjs()
        createControllerjs.createControllerjs(params.dynamicTableNameId)//创建控制器 表名.js
        //  println("控制器 表名.js生成成功")

        CreateViewportjs createViewportjs = new CreateViewportjs()
        createViewportjs.createViewportjs(params.dynamicTableNameId)//创建视图View  表名Viewport.js
        println("表名Viewport.js生成成功")

        CreateGridViewportjs createGridViewportjs = new CreateGridViewportjs()
        createGridViewportjs.createGridViewportjs(params.dynamicTableNameId)//创建视图View  表名GridViewport.js
        // println("表名GridViewport.js生成成功")
        CreateAddjs createAddjs = new CreateAddjs()
        createAddjs.createAddjs(params.dynamicTableNameId)//创建视图View  表名Add.js
        // println("表名Add.js生成成功")
        CreateEditjs createEditjs = new CreateEditjs()
        createEditjs.createEditjs(params.dynamicTableNameId)//创建视图View  表名Edit.js
        //  println("表名Edit.js生成成功")


        CreateSelectImportjs createSelectImportjs = new CreateSelectImportjs()
        createSelectImportjs.createSelectImportjs(params.dynamicTableNameId)//创建视图View  表名Edit.js
        // println("SelectImport表名Item.js生成成功")
        CreateSelectExportjs createSelectExportjs = new CreateSelectExportjs()
        createSelectExportjs.createSelectExportjs(params.dynamicTableNameId)//创建视图View  表名Edit.js
        // println("SelectExport表名Item.js生成成功")
        CreateMainpageViewportjs createMainpageViewportjs = new CreateMainpageViewportjs()
        createMainpageViewportjs.createMainpageViewportjs(params.dynamicTableNameId)//创建视图View  表名Edit.js
        // println("表名MainpageViewport.js生成成功")
        render "success"
        return


        /*  ModifyMainjs modifyMainjs = new ModifyMainjs()
          int treeId = modifyMainjs.modifyMainjs(params.dynamicTableNameId)//修改MainController.js
          if (treeId == -1) {
              println("无需修改MainController.js文件")
          } else {
              println("修改MainController.js生成成功")
          }
          */
        //返回树结点号，然后修改Requestmap中表的对应树结点号
        query = "from Requestmap where url='/" + params.dynamicTableNameId + "/**'"
        def rlist = Requestmap.findAll(query)
        if (rlist.size() > 0) {
            //if ((rlist.size() > 0) && (treeId != -1)) {
//treeId=-1表示在Main.js中已有该表的控制信息，rlist.size()>0，treeId的值决定了该表操作项所处在菜单的位置，现在暂时存入的是“0”，之后在系统中修改。
            Requestmap requestmapInstance = rlist[0]
            //requestmapInstance.setTreeId("" + treeId)
            requestmapInstance.setTreeId("" + 0)
            //println("修改、创建访问控制" + list[0].tableName + "的树结点号")
            try {
                if (requestmapInstance.save(flush: true)) {
                    springSecurityService.clearCachedRequestmaps()//让requestmap修改后生效
                    // println("修改、创建访问控制-----成功")
                } else {
                    requestmapInstance.errors.each {
                        println it
                    }
                    render "failure"
                    return
                }
            } catch (e) {
                render "failure"
                return
            }
        } else {
            // if (treeId == -1) {
            // println("无需修改Main.js文件")
            // } else {
            //    println("请先重新创建该表的控制器，再执行此操作！")
            //  }
        }

        //删除classes下的所有文件和文件夹
        String file = "D:\\appMis\\target\\classes"
        File myFile1 = new File(file)
        DeleteFolder deleteFolder1 = new DeleteFolder()
        deleteFolder1.deleteFile(myFile1)
        //println("D:\\appMis\\target\\classes下的所有文件和文件夹删除成功")

        // ModifyDataTreeStorejs modifyDataTreeStorejs=new ModifyDataTreeStorejs()
        //  modifyDataTreeStorejs.modifyDataTreeStorejs(params.dynamicTableNameId)//修改DataTreeStore.js
        //  println("修改DataTreeStore.js生成成功")
        render "success"   //render "动态创建Controller并编译成功"
    }


    def readDynamicTable() {
        //如params.tableNameId从网页传来，则params.tableNameId可能为"null"，所以要判断(params.tableNameId=="null")
        if ((!params.tableNameId) || (params.tableNameId == "") || (params.tableNameId == "null")) {
            render(contentType: "text/json") {
                dynamictables ""
                totalCount 0
            }
            return
        }
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        def list = DynamicTable.createCriteria().list(params) {
            if (params.tableNameId != "all") like("tableNameId", "%" + params.tableNameId + "%")
        }
        def json1
        if (list.totalCount == 0) {
            json1 = null
        } else {
            json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        }
        render(contentType: "text/json") {
            dynamictables json1
            totalCount list.totalCount
        }
    }

    //初次显示SelectImportStudentItem信息 DynamicTableStoreStudent列表
    def readDynamicTableSelect() {
        //如params.tableNameId从网页传来，则params.tableNameId可能为"null"，所以要判断(params.tableNameId=="null")
        if ((!params.tableNameId) || (params.tableNameId == "") || (params.tableNameId == "null")) {
            render(contentType: "text/json") {
                dynamictables ""
                totalCount 0
            }
            return
        }
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        def list = DynamicTable.createCriteria().list(params) {
            if (params.tableNameId != "all") like("tableNameId", "%" + params.tableNameId + "%")
            ne("fieldType", "字节流")
            ne("fieldNameId", "treeId")
        }
        def json1
        if (list.totalCount == 0) {
            json1 = null
        } else {
            json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        }
        render(contentType: "text/json") {
            dynamictables json1
            totalCount list.totalCount
        }
    }

    //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数
    def toparams(params) {
        String jsonStr
        if (params.sort != null) {
            jsonStr = params.sort
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
            JSONObject obj = new JSONObject(jsonStr)
            params.sort = obj.get('property')
            params.order = obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true
        }
        params.offset = params.start ? params.start as int : 0
        params.max = params.limit ? params.limit as int : 25
        return
    }
    //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
    def json(list) {
        List<?> dynamicTables = new ArrayList()
        //从定义的类中取出有效字段数n
        def list1 = DynamicTable.getDeclaredFields()
        int n = 0
        for (int i = 0; i < list1.size(); i++) if (list1[i].getName() != "constraints") {
            n++
        } else {
            break
        }
        //从定义的类中取出有效字段名
        String[] fieldName = new String[n]
        for (int i = 0; i < n; i++) {
            fieldName[i] = list1[i].getName()
        }// println("fieldName["+i+"]===="+fieldName[i])
        //从数据库中取出数据生成返回的ArrayList数据，以json格式返回

        for (int j = 0; j < (list.size() - 1); j++) {
            Map<String, String> map = new HashMap<>()
            map.put("id", list[j].id)
            for (int i = 0; i < n - 1; i++) map.put(fieldName[i], list[j].(fieldName[i]))
            map.put(fieldName[n - 1], list[j].(fieldName[n - 1]))
            dynamicTables.add(map)
        }
        Map<String, String> map = new HashMap<>()
        map.put("id", list[list.size() - 1].id)
        for (int i = 0; i < n - 1; i++) map.put(fieldName[i], list[list.size() - 1].(fieldName[i]))
        map.put(fieldName[n - 1], list[list.size() - 1].(fieldName[n - 1]))
        dynamicTables.add(map)
        return dynamicTables
    }


    //表名列表, 数据表名下拉列表
    def readDynamicTableQuery() {
        ArrayList<Map> list2 = new ArrayList<Map>()
        String query = "from DynamicTable GROUP BY tableName order by tableName,fieldNameId  ASC "
        def list1 = DynamicTable.findAll(query)
        list2.add([tableName: '全部', tableNameId: "all"])
        for (int i = 0; i < list1.size(); i++) {
            list2.add([tableName: list1[i].tableName, tableNameId: list1[i].tableNameId])
        }
        render(contentType: "text/json") {
            dynamictables list2.collect() {
                [
                        tableName  : it?.tableName,
                        tableNameId: it?.tableNameId,
                ]
            }
        }
    }
    //把数据表导入到Excel文件中
    def importExcelDynamicTable() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
        def f = request.getFile('dynamicTableexcelfilePath')
        def fileName, filePath
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            filePath = getServletContext().getRealPath("/") + "/tmp/" //生成的filepath为src/main/webapp/tmp
            if (fileName.toLowerCase().endsWith(".xls") || fileName.toLowerCase().endsWith(".xlsx")) {
                f.transferTo(new File(filePath + fileName))//先把文件上传到服务器的/tmp下，xls及xlsx都可以！
            } else {
                render "failure"//typeError
                return
            }
        } else {
            render "failure"
            return
        }
        Workbook wb
        try {
            // wb = WorkbookFactory.create(new FileInputStream(filePath + fileName)); // Use an InputStream, needs more memory
            wb = WorkbookFactory.create(new File(filePath + fileName))
//Using a File object allows for lower memory consumptionit's very easy to use one or the other:
        } catch (IOException e) {
            wb.close()//先关闭wb，再删除文件
            File fs = new File(filePath + fileName)//删除上传的文件
            fs.delete()
            render "failure"
            return
        }
        org.apache.poi.sl.usermodel.Sheet sheet = wb.getSheetAt(0)//第一张Sheet
        def rowcount = sheet.getLastRowNum()//取得Excel文件的总行数
        int columns = sheet.getRow((short) 1).getPhysicalNumberOfCells()//取得Excel文件的总列数
        String query
        def list1
        Row row
        Cell cell
        //生成数据表字段名序列，并对字段名进行转换：除首字母外，大写字母变小写且前面加一个下划线。
        Class<?> demo = DynamicTable.class
        def field = demo.getDeclaredFields()
        String fieldName, cc, dd, columnsss = "version"
        char c
        for (int ii = 0; ii < columns; ii++) {
            fieldName = field[ii].getName().toString()
            for (int iii = 0; iii < fieldName.length(); iii++) {
                c = fieldName.charAt(iii)
                if (Character.isUpperCase(c)) {
                    cc = c
                    dd = (char) (c + 32)
                    fieldName = fieldName.replaceAll(cc, "_" + dd)
                }
            }
            columnsss = columnsss + "," + fieldName
        }
        columnsss = "(" + columnsss + ")"
        int a = 1
        String tableNameId1 = '', tableNameId2 = '', tableName1 = '', tableName2 = ''

        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        while (a <= rowcount) {
            row = sheet.getRow(a)//获取Sheet的第a行
            if (!row) break
            cell = row.getCell((short) 0)//获取Sheet的第a行第一列
            if (!cell.getStringCellValue()) break//这里没有判断cell的类型，假想是字符
            tableNameId1 = cell.getStringCellValue()//Sheet的第a行第一列是【表名】
            cell = row.getCell((short) 1)//获取Sheet的第a行第一列
            if (!cell.getStringCellValue()) break//这里没有判断cell的类型，假想是字符
            tableName1 = cell.getStringCellValue()//Sheet的第a行第一列是【中文表名】
            query = "from DynamicTable where tableNameId='" + tableNameId1 + "' OR tableName='" + tableName1 + "'"
            list1 = DynamicTable.findAll(query)
            if (list1.size() > 0 && (!tableNameId1.equals(tableNameId2)) && (!tableName1.equals(tableName2))) {
                render "{success:true,info:'系统数据表中已有将要导入表的数据，应先删除数据表中该表的所有数据，再进行导入操作，本次Excel数据导入失败！'}"
                return
            }
            tableNameId2 = tableNameId1//保证每次导入是同一张表的数据 ！！
            tableName2 = tableName1//保证每次导入是同一张表的数据 ！！
            int i = 0
            String values = "0"//是version的值
            for (i = 0; i < columns; i++) {
                cell = row.getCell((short) i)
                if (field[i].getType() == (new Date()).getClass()) {//如果字段是日期型数据
                    int year1 = 0, month1 = 0
                    Calendar dynamicTableDate = Calendar.getInstance()
                    if (!cell) {
                        values = values + ",Null"
                        continue
                    }
                    if (cell.getCellType() == cell.CELL_TYPE_STRING) {  //字符型单元格放的是日期型
                        if (((cell.getStringCellValue()) ? (simpleDateFormat.parse(cell.getStringCellValue().replaceAll("/", "-"))) : "") != "") {
                            values = values + ",STR_TO_DATE('" + cell.getStringCellValue().replaceAll('/', '-') + "','%Y-%m-%d')"
                            dynamicTableDate.setTime(simpleDateFormat.parse(cell.getStringCellValue().replaceAll("/", "-")))
                        } else {
                            values = values + ",NULL"
                        }
                    } else {
                        if ((cell.getCellType() == cell.CELL_TYPE_NUMERIC) && (DateUtil.isCellDateFormatted(cell))) {
//日期型单元格放的是日期型
                            values = values + ",STR_TO_DATE('" + (simpleDateFormat.format(cell.dateCellValue)) + "','%Y-%m-%d')"
                            dynamicTableDate.setTime(cell.dateCellValue)
                        } else {
                            values = values + ",NULL"
                        }
                    }
                } else {
                    if (field[i].getType() == (new String("")).getClass()) {//如果字段是字符型数据
                        if (!cell) {
                            values = values + ",''"
                            continue
                        }

                        if (cell.getCellType() == cell.CELL_TYPE_STRING) {  //字符型单元格放的是字符型数据
                            values = values + ",'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                        } else {
                            if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {//字符型单元格放的是数值型数据
                                values = values + ",'" + String.valueOf(cell.getNumericCellValue()) + "'"
                            } else {
                                values = values + ",''"
                            }
                        }
                    } else {
                        if ((field[i].getType() == (new Double(0.0)).getClass()) || (field[i].getType() == int) || (field[i].getType() == Integer)) {
//如果是数值型（Double、int，Integer）数据
                            if (!cell) {
                                values = values + ",0.0"
                                continue
                            }
                            try {
                                values = values + "," + cell.getNumericCellValue()
                            } catch (e) {
                                values = values + ",0.0"
                            }
                        } else {
                            values = values + ",''"
                        }
                    }

                }
            }
            query = "insert into Dynamic_table " + columnsss + " values (" + values + ")"
            //println("columnss========="+columnsss)
            // println("values========="+values)
            //println("query========="+query)
            if (sql.execute(query)) {
                render "{failure:true,info:'系统数据表中已有将要导入表的数据，Excel数据导入失败！'}"//导入失败
                return
            }
            a++
        }

        wb.close()//先关闭wb，再删除文件
        File fs = new File(filePath + fileName)//删除上传的文件
        fs.delete()
        render "{success:true,info:'Excel数据导入成功'}"
    }

    //把数据表导出到Excel文件中， //直接导出到客户端下载
    def exportExcelDynamicTable() {
        // int startTime=System.currentTimeSeconds()
        def list
        String tableName
        String query = 'from DynamicTable'
        if (!params.tableNameId || (params.tableNameId == "all") || (params.tableNameId == "")) {
            query = query
            list = DynamicTable.findAll(query)//要导出Excel的所有记录放入List中
            tableName = "全部数据结构表"
        } else {
            query = query + " where tableNameId='" + params.tableNameId + "'"
            list = DynamicTable.findAll(query)//要导出Excel的所有记录放入List中
            tableName = list[0].tableName + "数据结构表"
        }
        // def filePath = getServletContext().getRealPath("\\") + "\\tmp\\"  //生成的filepath为src/main/webapp/tmp
        try {
            // Workbook wb = WorkbookFactory.create(filePath+ "系统数据表.xls");
            //Workbook wb = new HSSFWorkbook();//创建Excel工作簿对象.xls
            // Workbook wb = new XSSFWorkbook();//创建Excel工作簿对象.xlsx
            //Sheet sheet = wb.createSheet(tableName);//创建Excel工作表对象
            SXSSFWorkbook templatewb = new SXSSFWorkbook(100)//可以存储大数据（>10000行）Excel工作簿对象.xlsx
            templatewb.dispose()//临时文件需要我们手动清除
            templatewb.setCompressTempFiles(true)//you can tell SXSSF to use gzip compression
            org.apache.poi.sl.usermodel.Sheet templateSheet = templatewb.createSheet(tableName)//
            int b = 0
            6.times {
                templateSheet.setColumnWidth((short) b, (short) 4000)
                b++
            }
            Row row = templateSheet.createRow((short) 0)
            row.setHeightInPoints((short) 22)
            row.createCell((short) 0).setCellValue("数据表名") //设置Excel工作表的值
            row.createCell((short) 1).setCellValue("数据表中文名称") //设置Excel工作表的值
            row.createCell((short) 2).setCellValue("字段名称") //设置Excel工作表的值
            row.createCell((short) 3).setCellValue("字段中文名称") //设置Excel工作表的值
            row.createCell((short) 4).setCellValue("字段类型") //设置Excel工作表的值
            row.createCell((short) 5).setCellValue("字段长度") //设置Excel工作表的值
            //  }
            int i = 0
            list.each {
                row = templateSheet.createRow((short) i + 1)
                row.setHeightInPoints((short) 22)
                row.createCell((short) 0).setCellValue(list[i].tableNameId) //设置Excel工作表的值
                row.createCell((short) 1).setCellValue(list[i].tableName) //设置Excel工作表的值
                row.createCell((short) 2).setCellValue(list[i].fieldNameId) //设置Excel工作表的值
                row.createCell((short) 3).setCellValue(list[i].fieldName) //设置Excel工作表的值
                row.createCell((short) 4).setCellValue(list[i].fieldType) //设置Excel工作表的值
                row.createCell((short) 5).setCellValue(list[i].fieldLength) //设置Excel工作表的值
                i++
            }
            //直接导出到客户端下载
            String file_name = tableName + ".xlsx"
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            //响应类型为application/octet-stream浏览器无法确定文件类型时,不直接显示内容，attachment提示以附件方式下载,Content-Disposition确定浏览器弹出下载对话框
            file_name = new String(file_name.getBytes(), "ISO-8859-1")
            response.setHeader("Content-Disposition", "attachment;filename=" + file_name)
            response.setHeader("Cache-Control", "No-cache")
            response.flushBuffer()
            templatewb.write(response.getOutputStream()) //将模板的内容写到客户端上下载
            templatewb.close()
            // int endTime=System.currentTimeSeconds()
            templatewb.dispose()
            return
        } catch (e) {
            render "错误！系统数据表信息"
            return
        }
    }
    //把数据表导出到Excel文件中，//先导出到服务器端，然后下载
    def exportExcelDynamicTablebak() {
        //println("exportExcelDynamicTable===========")
        def list
        String tableName
        String query = 'from DynamicTable'
        if (!params.tableNameId || (params.tableNameId == "all") || (params.tableNameId == "")) {
            query = query
            list = DynamicTable.findAll(query)//要导出Excel的所有记录放入List中
            tableName = "全部数据结构表"
        } else {
            query = query + " where tableNameId='" + params.tableNameId + "'"
            list = DynamicTable.findAll(query)//要导出Excel的所有记录放入List中
            tableName = list[0].tableName + "数据结构表"
        }

        def filePath = getServletContext().getRealPath("\\") + "\\tmp\\"  //生成的filepath为src/main/webapp/tmp
        try {
            // Workbook wb = WorkbookFactory.create(filePath+ "系统数据表.xls");
            //Workbook wb = new HSSFWorkbook();//创建Excel工作簿对象.xls
            Workbook wb = new XSSFWorkbook()//创建Excel工作簿对象.xlsx
            org.apache.poi.sl.usermodel.Sheet sheet = wb.createSheet(tableName)//创建Excel工作表对象
            int b = 0
            6.times {
                sheet.setColumnWidth((short) b, (short) 4000)
                b++
            }
            // println("params===="+params)
            // if(!params.biaotou){
            Row row = sheet.createRow((short) 0)
            row.setHeightInPoints((short) 22)
            row.createCell((short) 0).setCellValue("数据表名") //设置Excel工作表的值
            row.createCell((short) 1).setCellValue("数据表中文名称") //设置Excel工作表的值
            row.createCell((short) 2).setCellValue("字段名称") //设置Excel工作表的值
            row.createCell((short) 3).setCellValue("字段中文名称") //设置Excel工作表的值
            row.createCell((short) 4).setCellValue("字段类型") //设置Excel工作表的值
            row.createCell((short) 5).setCellValue("字段长度") //设置Excel工作表的值
            //  }
            int i = 0
            list.each {
                row = sheet.createRow((short) i + 1)
                row.setHeightInPoints((short) 22)
                row.createCell((short) 0).setCellValue(list[i].tableNameId) //设置Excel工作表的值
                row.createCell((short) 1).setCellValue(list[i].tableName) //设置Excel工作表的值
                row.createCell((short) 2).setCellValue(list[i].fieldNameId) //设置Excel工作表的值
                row.createCell((short) 3).setCellValue(list[i].fieldName) //设置Excel工作表的值
                row.createCell((short) 4).setCellValue(list[i].fieldType) //设置Excel工作表的值
                row.createCell((short) 5).setCellValue(list[i].fieldLength) //设置Excel工作表的值
                i++
            }
            // FileOutputStream fileOut = new FileOutputStream(filePath + "系统数据表.xls");
            FileOutputStream fileOut = new FileOutputStream(filePath + tableName + ".xlsx")
            wb.write(fileOut)
            fileOut.close()
            render tableName
            return
        } catch (e) {
            render "failure"
            return
        }
    }

    //删除所选的系统数据表中所有信息
    @Transactional
    def batchDelete() {
        String query
        if (params.dynamicTableNameId == 'all') {
            query = "from DynamicTable"
        } else {
            query = "from DynamicTable where tableNameId='" + params.dynamicTableNameId + "'"
        }
        def listd = DynamicTable.findAll(query)
        if (listd.size() == 0) {
            render "failure"//没有职工基本信息I
            return
        }
        if (params.dynamicTableNameId == 'all') {
            query = "Delete FROM  dynamic_table"
        } else {
            query = "Delete FROM  dynamic_table where table_name_id='" + params.dynamicTableNameId + "'"
        }
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        if (sql.execute(query)) {
            render "failure"//删除失败
            return
        } else {
            render "success"//删除成功
            return
        }
    }

    @Transactional
    def save() {
        def dynamicTableInstance = new DynamicTable(params)
        if (dynamicTableInstance == null) {
            notFound()
            return
        }
        if (dynamicTableInstance.hasErrors()) {
            dynamicTableInstance.errors.each {
                println it
            }
            respond dynamicTableInstance.errors, view: 'create'
            return
        }
        try {
            if (dynamicTableInstance.save(flush: true)) {
                render "success"
                return
            } else {
                dynamicTableInstance.errors.each {
                    println it
                }
                render "failure"
                return
            }
        } catch (e) {
            render "failure"
            return
        }
    }

    @Transactional
    def update() {
        def listnew1
        DynamicTable dynamicTableInstance = DynamicTable.findById(params.id)
        String oldtablename = dynamicTableInstance.tableName
        String newtablename = params.tableName
        String oldfieldtype = dynamicTableInstance.fieldType
        String newfieldtype = params.fieldType
        String oldfieldname = dynamicTableInstance.fieldName
        String newfieldname = params.fieldName
        String oldfieldnameid = dynamicTableInstance.fieldNameId
        String newfieldnameid = params.fieldNameId
        // println("=================from DynamicTable where tableName='" +params.tableName+"' and (fieldName='" + params.fieldName + "' or fieldNameId= '" + params.fieldNameId + "')")
        dynamicTableInstance.setFieldLength(Integer.parseInt(params.fieldLength))//更新字段长度
        if ((oldfieldname != newfieldname) && (oldfieldtype.equals("公式"))) {//修改了字段名且字段类型为[公式],则字段名字和字段Id要相对应修改
            listnew1 = DynamicTable.findAll("from DynamicTable where tableName='" + newtablename + "' and fieldName= '" + (params.fieldName).split("公式")[0] + "'")
            //println("listnew1[0].fieldNameId==="+listnew1[0].fieldNameId)
            if (listnew1.size() > 0) params.fieldNameId = listnew1[0].fieldNameId + "g"
            params.fieldName = (params.fieldName).split("公式")[0] + "公式"
            listnew1 = DynamicTable.findAll("from DynamicTable where tableName='" + params.tableName + "' and (fieldName='" + params.fieldName + "')")
            if (listnew1.size() == 0) {
                dynamicTableInstance.setFieldName(params.fieldName)//更新字段名字
                dynamicTableInstance.setFieldNameId(params.fieldNameId)//更新字段Id
            } else {
                //否则同表中出现同名字段，不更新！
            }

        }
        if ((oldfieldname != newfieldname) && !(oldfieldtype.equals("公式"))) {//修改了字段名且字段类型为[非公式],
            listnew1 = DynamicTable.findAll("from DynamicTable where tableName='" + params.tableName + "' and (fieldName='" + params.fieldName + "')")
            if (listnew1.size() == 0) {
                dynamicTableInstance.setFieldName(params.fieldName)//更新字段名字
            } else {
                //否则同表中出现同名字段，不更新！
            }

        }

        //
        if ((oldfieldnameid != newfieldnameid) && (oldfieldtype.equals("公式"))) {//修改了字段名且字段类型为[公式],则字段名字和字段Id要相对应修改
            listnew1 = DynamicTable.findAll("from DynamicTable where tableName='" + params.tableName + "' and (fieldNameId= '" + params.fieldNameId + "')")
            if (listnew1.size() == 0) {
                dynamicTableInstance.setFieldNameId(params.fieldNameId)//更新字段Id,必须以g结尾
            } else {
                //否则同表中出现同名字段，不更新！
            }

        }
        if ((oldfieldnameid != newfieldnameid) && !(oldfieldtype.equals("公式"))) {//修改了字段名且字段类型为[非公式],
            listnew1 = DynamicTable.findAll("from DynamicTable where tableName='" + params.tableName + "' and (fieldNameId= '" + params.fieldNameId + "')")
            if (listnew1.size() == 0) {
                dynamicTableInstance.setFieldNameId(params.fieldNameId)//更新字段名字
            } else {
                //否则同表中出现同名字段，不更新！
            }

        }
        //


        if ((oldfieldtype != newfieldtype) && (newfieldtype.equals("公式"))) {//修改了字段类型且变为[公式]类型,则字段名字和字段Id要相对应修改
            params.fieldNameId = params.fieldNameId + "g"
            params.fieldName = (params.fieldName).split("公式")[0] + "公式"
            dynamicTableInstance.setFieldName(params.fieldName)//更新字段名字
            dynamicTableInstance.setFieldType(params.fieldType)//更新字段类型
            dynamicTableInstance.setFieldNameId(params.fieldNameId)//更新字段Id
        }
        if ((oldfieldtype != newfieldtype) && !(newfieldtype.equals("公式"))) {//修改了字段类型且变为[非公式]类型,则字段名字和字段Id要相对应修改
            params.fieldNameId = (params.fieldNameId).split("g")[0]
            params.fieldName = (params.fieldName).split("公式")[0]
            dynamicTableInstance.setFieldName(params.fieldName)//更新字段名字
            dynamicTableInstance.setFieldType(params.fieldType)//更新字段类型
            dynamicTableInstance.setFieldNameId(params.fieldNameId)//更新字段Id
        }

        if (oldtablename != newtablename) {//修改了表名
            def listnew = DynamicTable.findAll("from DynamicTable where tableName='" + newtablename + "' and fieldType <> '公式' order by fieldNameId DESC ")
            // println("listnew.size()==========="+listnew.size())
            if (listnew.size() > 0) {//已有表名
                dynamicTableInstance.setTableNameId(listnew[0].tableNameId)//更新表名Id
                /*
                int max = 0
                for (int i = 0; i < listnew.size(); i++) {
                    if (max < Integer.parseInt((listnew[i].fieldNameId).split("F")[1])) max = Integer.parseInt((listnew[i].fieldNameId).split("F")[1])
                }
                String fieldNameIdnew = dynamicTableInstance.tableNameId + "F" + (max + 1)
                dynamicTableInstance.setFieldNameId(fieldNameIdnew)//更新字段Id
                */
                dynamicTableInstance.setTableName(newtablename)//更新表名
            } else {//新表名
                def listnewtable = DynamicTable.findAll "from DynamicTable  where tableNameId like 'table%' GROUP BY tableName order by tableNameId DESC "
                dynamicTableInstance.setTableName(newtablename)//更新表名
                if (listnewtable.size() > 0) {
                    listnew = DynamicTable.findAll("from DynamicTable where tableNameId='" + params.tableNameId + "'")
                    def listnew99 = DynamicTable.findAll "from DynamicTable where tableNameId='table999'"
                    if ((listnew.size() != 1) || (listnew99.size() == 1)) {
                        int max = 0
                        for (int i = 0; i < listnewtable.size(); i++) {
                            if ((max < Integer.parseInt(listnewtable[i].tableNameId.substring(5))) && !(listnewtable[i].tableNameId.substring(5).equals("999"))) max = Integer.parseInt(listnewtable[i].tableNameId.substring(5))
                        }
                        dynamicTableInstance.setTableNameId("table" + (max + 1))//更新表名Id
                    }
                } else {
                    dynamicTableInstance.setTableNameId("table1")//更新表名Id
                }
                //dynamicTableInstance.setFieldNameId(dynamicTableInstance.tableNameId + "F1")//更新字段Id
            }
        }

        if (dynamicTableInstance == null) {
            notFound()
            return
        }
        if (dynamicTableInstance.hasErrors()) {
            dynamicTableInstance.errors.each {
                println it
            }
            return
        }
        try {
            if (dynamicTableInstance.save(flush: true)) {//必须加参数flush: true，否则 render "success"返回异常
                render "success"
                return
            } else {
                dynamicTableInstance.errors.each {
                    println it
                }
                render "failure"
                return
            }
        } catch (e) {
            render "failure"
            return
        }
    }

    @Transactional
    def delete() {
        def dynamicTableInstance = DynamicTable.get(params.id)
        if (dynamicTableInstance == null) {
            notFound()
            return
        }
        try {
            if (dynamicTableInstance.delete() == null) {
                render "success"
                return
            } else {
                dynamicTableInstance.errors.each {
                    println it
                }
                render "failure"
                return
            }
        } catch (e) {
            render "failure"
            return
        }
    }


    //删除系统自动生成的若干文件
    def deleteAll() {
        //int treeIdm=0//记录某个表树结点号
        String query = "from DynamicTable where tableNameId='" + params.dynamicTableNameId + "'"
        def list = DynamicTable.findAll(query)
        if (list[0].fieldLength == 0) {
            render "failure1"//此表无需操作此功能！！！
            return
        }
        String file = "d:\\appMis\\web-app\\app\\model\\" + initcap2(list[0].tableNameId) + "Model.js"
        File myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            // println("表名Model.js删除成功")
        }
        file = "d:\\appMis\\web-app\\app\\controller\\" + initcap2(list[0].tableNameId) + ".js"
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            // println("控制器 表名.js删除成功")
        }
        file = "d:\\appMis\\web-app\\app\\store\\" + initcap2(list[0].tableNameId) + "Store.js"
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            // println("表名Stroe.js删除成功")
        }
        file = "d:\\appMis\\web-app\\app\\store\\" + initcap2(list[0].tableNameId) + "Store1.js"
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            // println("表名Stroe1.js删除成功")
        }
        file = "d:\\appMis\\web-app\\app\\store\\" + initcap2(list[0].tableNameId) + "Store2.js"
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            // println("表名Stroe2.js删除成功")
        }
        file = "d:\\appMis\\web-app\\app\\store\\" + initcap2(list[0].tableNameId) + "Store3.js"
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            //println("表名Stroe3.js删除成功")
        }
        file = "d:\\appMis\\web-app\\app\\view\\" + list[0].tableNameId
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.deleteDir()
            println("表名Add.js、表名Edit.js、表名Viewport.js、表名GridViewport.js、SelectExport表名Item.js、SelectImport表名Item.js删除成功")
        }
        file = "d:\\appMis\\grails-app\\controllers\\appMis\\" + initcap2(list[0].tableNameId) + "Controller.groovy"
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            //println("表名Controllers.groovy删除成功")
        }
        file = "D:\\appMis\\test\\unit\\appMis\\" + initcap2(list[0].tableNameId) + "ControllerSpec.groovy"
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            //println("表名ControllerSpec.groovy删除成功")
        }

        /* file="D:\\appMis\\target\\classes"
        File myFile1=new File(file);
        DeleteFolder deleteFolder1=new DeleteFolder();
        deleteFolder1.deleteFile(myFile1);
        println("D:\\appMis\\target\\classesy下的所有文件和文件夹删除成功")
*/

        file = "d:\\appMis\\grails-app\\domain\\appMis\\" + initcap2(list[0].tableNameId) + ".groovy"
        myFile = new File(file)
        if (myFile.exists()) {
            myFile.delete()
            // println("域文件表名.groovy删除成功")
        }
        FileReader fr
        StringBuilder sb
        String line, line1
        FileWriter fw
        PrintWriter pw
        try {
            fr = new FileReader("D:\\appMis\\web-app\\app.js")
            sb = new StringBuilder()
            line = null
            while ((line = fr.readLine()) != null) {
                if (line.trim() == "") {
                    line = fr.readLine(); if (line == null) break
                }
                if ((line.indexOf(initcap2(((DynamicTable) list[0]).tableNameId)) > 0) || (line.indexOf(((DynamicTable) list[0]).tableNameId) > 0)) {
                } else {
                    if (line.trim() != "") sb.append(line + "\n")
                }
            }
            fr.close()
            fw = new FileWriter("D:\\appMis\\web-app\\app.js")
            pw = new PrintWriter(fw)
            pw.println(sb)
            pw.flush()
            pw.close()
            // println("App.js中的相关信息删除成功")
        } catch (IOException e) {
            e.printStackTrace()
        }

        //取到表树结点号、汉字表名chinese_url
        query = "from Requestmap where url='/" + params.dynamicTableNameId + "/**'"
        def rlistm = Requestmap.findAll(query)
        if (rlistm.size() > 0) {
            // Requestmap requestmapInstance=rlistm[0]
            // treeIdm=Integer.valueOf(requestmapInstance.getTreeId()).intValue();
            try {
                fr = new FileReader("d:\\appMis\\web-app\\app\\view\\main\\MainController.js")
                sb = new StringBuilder()
                line = null
                while ((line = fr.readLine()) != null) {
                    if (line.trim() == "") {
                        line = fr.readLine(); if (line == null) break
                    }
                    if (line.indexOf("case '" + rlistm[0].chineseUrl + "'") > 0) {
                        for (int i = 1; i < 12; i++) {//跳过case idnum ：这个菜单项共12行
                            line = fr.readLine()
                            if (line.trim() == "") {
                                line = fr.readLine(); if (line == null) break
                            }
                        }
                    } else {
                        if (line.trim() != "") sb.append(line + "\n")
                    }
                }
                fr.close()
                fw = new FileWriter("d:\\appMis\\web-app\\app\\view\\main\\MainController.js")
                pw = new PrintWriter(fw)
                pw.println(sb)
                pw.flush()
                pw.close()
                // println("MainController.js中的相关信息删除成功")
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
        //删除requestmap中的相关记录
        // query="from Requestmap where url='/"+ params.dynamicTableNameId+"/**'"
        // def rlist=Requestmap.findAll(query)
        for (int i = 0; i < rlistm.size(); i++) {
            try {
                if (rlistm[i].delete()) {
                    springSecurityService.clearCachedRequestmaps()//让requestmap修改后生效
                    // println("数据表中相应字段删除成功")
                } else {
                    rlistm[i].clearErrors()
                }
            } catch (e) {
                rlistm[i].clearErrors()
            }
        }
        render "success"
    }

//================================================================
    //不用了----------------用于产生导出Excel的选项，显示DynamicTable信息 DynamicTableStoreXXXX
    @Transactional
    def readDynamicTableSelectBak() {
        //println("******params.tableNameId======"+params.tableNameId)
        String s = params.tableNameId
        String query = "from DynamicTable where tableNameId='" + s + "' and fieldType<>'字节流'"
        //query = "from DynamicTable where tableNameId='" + s + "' and fieldType<>'字节流'"+ " and fieldNameId<>'treeId'"
        if (params.tableNameId == "kyjf") {
            query = query + " and fieldNameId<>'treeId'"
        }
        def list1 = DynamicTable.findAll(query)
        render(contentType: "text/json") {
            dynamictables list1.collect() {
                [
                        id         : it.id,
                        tableNameId: it?.tableNameId,
                        tableName  : it?.tableName,
                        fieldNameId: it?.fieldNameId,
                        fieldName  : it?.fieldName,
                        fieldType  : it?.fieldType,
                        fieldLength: it?.fieldLength
                ]
            }
            totalCount DynamicTable.count()
        }
    }


    def createDomain1(String tablename) {
        // String content = parse(tablename);//生成域文件的内容
        /*File file=new File(initcap(tablename) + ".groovy");
        if(file.exists())//如果域文件已经存在，则删除它！
        {
            try {
                file.delete()
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    def parse(String tablename) {//生成域文件的内容

    }

    def initcap(String str) {//不带路径带盘符的名字第一个字母改为大写
        int i = 0
        char[] ch = str.toCharArray()
        for (i = ch.length - 1; i > 0; i--) if (ch[i] == '\\') break
        if (ch[i + 1] >= 'a' && ch[i + 1] <= 'z') ch[i + 1] = (char) (ch[i + 1] - 32)
        return new String(ch)
    }

    def initcap1(String str) {//带路径的名字第一个字母改为大写
        int i = 0
        char[] ch = str.toCharArray()
        for (i = ch.length - 1; i > 0; i--) if (ch[i] == '\\') break
        if (ch[i + 1] >= 'a' && ch[i + 1] <= 'z') ch[i + 1] = (char) (ch[i + 1] - 32)
        return (new String(ch)).substring(i + 1, ch.length)
    }

    def initcap2(String str) {//不带路径不带盘符的名字第一个字母改为大写
        int i = 0
        char[] ch = str.toCharArray()
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0] - 32)
        return new String(ch)
    }

    def initcap3(String str) {//不带路径不带盘符的名字第一个字母改为小写
        int i = 0
        char[] ch = str.toCharArray()
        if (ch[0] >= 'A' && ch[0] <= 'Z') ch[0] = (char) (ch[0] + 32)
        return new String(ch)
    }

    def test() {//判断有无访问该表数据的权限
        render "success"
        return
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond DynamicTable.list(params), model: [dynamicTableInstanceCount: DynamicTable.count()]
    }

    def show(DynamicTable dynamicTableInstance) {
        respond dynamicTableInstance
    }

    def create() {
        respond new DynamicTable(params)
    }


    def edit(DynamicTable dynamicTableInstance) {
        respond dynamicTableInstance
    }
    def listAsJsonPage = {//查询
        String s = params.newvalue
        String query = "from DynamicTable where tableName='" + s + "' order by tableName,fieldNameId  ASC"
        def list = DynamicTable.findAll(query)
        render(contentType: "text/json") {
            dynamictables list.collect() {
                [
                        id         : it.id,
                        tableNameId: it?.tableNameId,
                        tableName  : it?.tableName,
                        fieldNameId: it?.fieldNameId,
                        fieldName  : it?.fieldName,
                        fieldType  : it?.fieldType,
                        fieldLength: it?.fieldLength
                ]
            }
            totalCount(list.size()).toString()
        }
    }
    def listAsJsonPage2 = {
        String s = params.newvalue
        String query = ""
        if ((params.newvalue) == "all") {//显示所有单位
            query = "from DynamicTable order by tableName,fieldNameId  ASC"
        } else {
            query = "from DynamicTable where tableName='" + s + "' order by tableName,fieldNameId  ASC"
        }
        def list = DynamicTable.findAll(query)
        def list1 = DynamicTable.findAll(query, [max: new Integer(params.limit), offset: new Integer(params.start)])
        render(contentType: "text/json") {
            dynamictables list1.collect() {
                [
                        id         : it.id,
                        tableNameId: it?.tableNameId,
                        tableName  : it?.tableName,
                        fieldNameId: it?.fieldNameId,
                        fieldName  : it?.fieldName,
                        fieldType  : it?.fieldType,
                        fieldLength: it?.fieldLength
                ]
            }
            totalCount(list.size()).toString()
        }
    }


    @Transactional
    def readDynamicTableSelectKyxmZjys() {
        String s = params.tableNameId
        String query = "from DynamicTable where tableNameId='" + s + "' and fieldType<>'字节流'  and fieldNameId<>'kyxmFyjbr'"
        //query = "from DynamicTable where tableNameId='" + s + "' and fieldType<>'字节流'"+ " and fieldNameId<>'treeId'"
        def list1 = DynamicTable.findAll(query)
        render(contentType: "text/json") {
            dynamictables list1.collect() {
                [
                        id         : it.id,
                        tableNameId: it?.tableNameId,
                        tableName  : it?.tableName,
                        fieldNameId: it?.fieldNameId,
                        fieldName  : it?.fieldName,
                        fieldType  : it?.fieldType,
                        fieldLength: it?.fieldLength
                ]
            }
            totalCount DynamicTable.count()
        }
    }

    @Transactional
    def readDynamicTableSelectKyxmZjsy() {
        String s = params.tableNameId
        String query = "from DynamicTable where tableNameId='" + s + "' and fieldType<>'字节流'"
        //query = "from DynamicTable where tableNameId='" + s + "' and fieldType<>'字节流'"+ " and fieldNameId<>'treeId'"
        def list1 = DynamicTable.findAll(query)
        render(contentType: "text/json") {
            dynamictables list1.collect() {
                [
                        id         : it.id,
                        tableNameId: it?.tableNameId,
                        tableName  : it?.tableName,
                        fieldNameId: it?.fieldNameId,
                        fieldName  : it?.fieldName,
                        fieldType  : it?.fieldType,
                        fieldLength: it?.fieldLength
                ]
            }
            totalCount DynamicTable.count()
        }
    }
    //用于产生查询下拉菜单，"全部" 显示DynamicTable信息 DynamicTableStore1
    def readTableNamesCombobox = {
        ArrayList<Map> list2 = new ArrayList<Map>()
        // String s=params.newvalue;
        String query = "from DynamicTable GROUP BY tableName order by tableName,fieldNameId  ASC "
        def list1 = DynamicTable.findAll(query)
        list2.add([tableNameId: '', tableName: "全部"])
        for (int i = 0; i < list1.size(); i++) {
            list2.add([tableNameId: list1[i].tableNameId, tableName: list1[i].tableName])
        }
        render(contentType: "text/json") {
            dynamictables list2.collect() {
                [
                        tableNameId: it?.tableNameId, //只要查询下拉菜单的信息，其它字段信息不要
                        tableName  : it?.tableName,
                ]
            }
            totalCount DynamicTable.count()
        }
    }
    //用于产生查询下拉菜单，显示DynamicTable信息 DynamicTableStore1
    def readTableNamesCombobox2 = {

        // String s=params.newvalue;
        String query = "from DynamicTable GROUP BY tableName order by tableName,fieldNameId  ASC "
        def list1 = DynamicTable.findAll(query)

        render(contentType: "text/json") {
            dynamictables list1.collect() {
                [
                        tableNameId: it?.tableNameId, //只要查询下拉菜单的信息，其它字段信息不要
                        tableName  : it?.tableName,
                ]
            }
            // totalCount  DynamicTable.count()
        }
    }
    //查询(数据表中文名称)字段，分页
    def readDynamicfield = {
        String query
        if ((params.newvalue) == "all") {
            query = "from DynamicTable"
        } else {
            query = "from DynamicTable where tableNameId='" + params.newvalue + "'"
        }
        String jsonStr
        def list1
        if (params.sort != null) {
            jsonStr = params.sort
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
            JSONObject obj = new JSONObject(jsonStr)
            list1 = DynamicTable.findAll(query + " order by " + obj.get('property') + " " + obj.get('direction'), [max: new Integer(params.limit), offset: new Integer(params.start)])
        } else {
            list1 = DynamicTable.findAll(query, [max: new Integer(params.limit), offset: new Integer(params.start)])
        }
        def list = DynamicTable.findAll(query)
        //  def list1 = DynamicTable.findAll(query,[max:new Integer(params.limit),offset:new Integer(params.start)]);
        render(contentType: "text/json") {
            dynamictables list1.collect() {
                [
                        id         : it.id,
                        tableNameId: it?.tableNameId,
                        tableName  : it?.tableName,
                        fieldNameId: it?.fieldNameId,
                        fieldName  : it?.fieldName,
                        fieldType  : it?.fieldType,
                        fieldLength: it?.fieldLength
                ]
            }
            totalCount(list.size()).toString()
        }
    }


    //系统数据还原
    @Transactional
    def dataRestore() {
        // println("数据还原")
        def f = request.getFile('employeeDataRestorefilepath')
        def fileName, filePath, buacupFilename
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            filePath = getServletContext().getRealPath("/") + "/backupdata/"
            buacupFilename = getServletContext().getRealPath("/") + "/backupdata/" + f.getOriginalFilename()
            // println("buacupFilename====="+buacupFilename)
            if (fileName.toLowerCase().endsWith(".sql")) {
                f.transferTo(new File(buacupFilename))//把客户端上选的文件上传到服务器
            } else {
                render "{success:false,info:'错误！数据还原文件类型不对'}"//render "typeError"//导入的Excel文件类型不对
                return
            }
        } else {
            render "{success:false,info:'错误！数据还原文件不存在'}" //render "failure"//导入的Excel文件不存在
            return
        }

        String cmd1 = "C:/Program Files/MySQL/MySQL Server 5.5/bin/mysql"
        try {
            // Runtime.getRuntime().exec( "cmd   /E:ON   /c   start  C://Program Files//MySQL//MySQL Server 5.5//bin//mysqldump.exe -uroot -p15309923872  gzmis > c://gzmis.sql").waitFor()
            //println("start数据还原")
            //Runtime.getRuntime().exec("cmd   /E:ON   /c   start   C:\\Program Files\\MySQL\\MySQL Server 5.5\\bin\\mysqldump -uroot -p15309923872  gzmis > C:\\gzmis.sql").waitFor()
            Runtime.getRuntime().exec("cmd   /E:ON   /c   start /b \"\" \"" + cmd1 + "\" -uroot -p15309923872 salarymis < " + buacupFilename).waitFor()
            // Runtime.getRuntime().exec("c:/nircmd.exe elevate C:\\mysqldump -uroot -p15309923872  gzmis > d:\\abc\\gzmis.sql")//不能正常执行
            //Runtime.getRuntime().exec("c:/nircmd.exe elevate winword C:\\abc\\gzmis.docx")//打开WORD成功
            //String cmd = "c:\\mysqldump -hlocalhost -uroot -p15309923872 gzmis > d:\\abc\\gzmis.sql" ;
            //Process process = Runtime.getRuntime().exec("cmd /c start /b " + cmd).waitFor();
            // println("end数据还原")
            File fs = new File(buacupFilename)//删除上传的文件
            fs.delete()
            render "{success:true,info:'数据还原成功'}"
            return
        } catch (IOException e) {
            e.printStackTrace()
            render "{success:false,info:'错误！数据还原失败'}"
            return
        }
    }

    //系统数据备份
    @Transactional
    def dataBbackup() {
        Calendar employeeDate
        int employeeDateYear, employeeDateMonth, employeeDateDay
        employeeDate = Calendar.getInstance()
        employeeDate.setTime(new Date())
        employeeDateYear = employeeDate.get(Calendar.YEAR)//获取年份
        employeeDateMonth = employeeDate.get(Calendar.MONTH)//获取月份
        employeeDateDay = employeeDate.get(Calendar.DAY_OF_MONTH)//获取天
        String buacupFilename = getServletContext().getRealPath("/") + "/backupdata/appmis" + employeeDateYear + "年" + (employeeDateMonth + 1) + "月" + employeeDateDay + "日.sql"
        File fs = new File(buacupFilename)//删除上传的文件
        fs.delete()
        String cmd1 = "C:/Program Files/MySQL/MySQL Server 5.5/bin/mysqldump"
        try {
            // Runtime.getRuntime().exec( "cmd   /E:ON   /c   start  C://Program Files//MySQL//MySQL Server 5.5//bin//mysqldump.exe -uroot -p15309923872  gzmis > c://gzmis.sql").waitFor()
            // println("start数据备份")
            //Runtime.getRuntime().exec("cmd   /E:ON   /c   start   C:\\Program Files\\MySQL\\MySQL Server 5.5\\bin\\mysqldump -uroot -p15309923872  gzmis > C:\\gzmis.sql").waitFor()
            int status = Runtime.getRuntime().exec("cmd   /E:ON   /c  start /b  \"\" \"" + cmd1 + "\" -hlocalhost -uroot -p15309923872  salarymis > " + buacupFilename).waitFor()
            // println("status===="+status)//waitFor()等待进程结束，//这是程序的缺陷！！备份文件生成的进程已经结束，但备份文件生成有个过程，这里是估计了个时间，等待文件生成完毕后再下载，否则下载的文件不完整！
            // Runtime.getRuntime().exec("c:/nircmd.exe elevate C:\\mysqldump -uroot -p15309923872  gzmis > d:\\abc\\gzmis.sql")//不能正常执行
            //Runtime.getRuntime().exec("c:/nircmd.exe elevate winword C:\\abc\\gzmis.docx")//打开WORD成功
            //String cmd = "c:\\mysqldump -hlocalhost -uroot -p15309923872 gzmis > d:\\abc\\gzmis.sql" ;
            //Process process = Runtime.getRuntime().exec("cmd /c start /b " + cmd).waitFor();
            // println("end数据备份")
            // render "gzmis"+employeeDateYear + "年" + (employeeDateMonth + 1) + "月"+employeeDateDay+"日.sql"
            //println("{success:true,info: 'gzmis"+employeeDateYear + "年" + (employeeDateMonth + 1) + "月"+employeeDateDay+"日.sql'}")
            render "{success:true,info: 'salarymis" + employeeDateYear + "年" + (employeeDateMonth + 1) + "月" + employeeDateDay + "日.sql'}"
            // render "{success:true,info:'数据还原成功'}"
            //render "success"
            return
        } catch (IOException e) {
            e.printStackTrace()
            // render "failure"
            render "{success:false,info:'错误！数据还原失败'}"
            return
        }

        /* def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
         def sql = new Sql(dataSource)
        String  query = "mysqldump -uroot -p15309923872 gzmis > c:/gzmis1111.sql"
         if (sql.execute(query)) {
             render "failure"
             return
         }else{
             render "success"
             return
         }*/


    }


}