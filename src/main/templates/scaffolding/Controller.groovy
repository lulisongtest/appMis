/*<%=new Date()%>陆立松自动创建
*/


<%=packageName ? "package ${packageName}" : ''%>

import com.user.Department
import grails.gorm.transactions.Transactional
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.grails.web.json.JSONObject
import groovy.sql.Sql
import org.springframework.http.MediaType
import javax.imageio.ImageIO
import javax.imageio.stream.FileImageInputStream
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.sql.Blob
import java.text.SimpleDateFormat
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

class ${className}Controller {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    //导入EXCEL信息
    @Transactional
    def selectImport() throws Exception {
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //准备department<=>treeId
        HashMap<String,String> deplist =new HashMap<String,String>()
        def dep = Department.findAll("from Department")//取treeId对应的Ddepartment
        for(int i=0;i<dep.size();i++){
            deplist.put(dep[i].department,dep[i].treeId)
        }
        //取到选择字段名的中文名字fieldItemName,字段名fieldItem，字段的类型fieldItemType
        int n=params.fieldItem.size()//所选择要导出的字段数量

        String[] fieldItem = params.fieldItem//所选择字段名
        String[] fieldItemName = new String[n]//所选择字段的中文名字
        String[] fieldItemType = new String[n]//所选择字段的类型
        Integer[] fieldItemLength = new Integer[n]//所选择字段的类型
        String[] fieldItem1 = new String[n]//所选择字段名(按Excel表头的顺序重新排序后)
        //String[] fieldItemName1 = new String[n]//所选择字段的中文名字
        String[] fieldItemType1 = new String[n]//所选择字段的类型(按Excel表头的顺序重新排序后)
        //Integer[] fieldItemLength1 = new Integer[n]//所选择字段的类型
        Integer[] m = new Integer[n]//所选择字段(按Excel表头的顺序重新排序后)在Excel中的列序号
        String  query = "from DynamicTable where tableNameId='${propertyName}'"
        def list1 = DynamicTable.findAll(query);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < list1.size(); j++) {
                if (fieldItem[i].equals(list1[j].fieldNameId)) {
                    fieldItemName[i] = list1[j].fieldName
                    fieldItemType[i] = list1[j].fieldType
                    fieldItemLength[i]=list1[j].fieldLength
                    break
                }
            }
            //println("fieldItemName==="+fieldItemName[i]+"-----fieldItem==="+fieldItem[i]+"-----fieldItemType==="+fieldItemType[i]+"-----fieldItemLength==="+fieldItemLength[i])
        }
        def f = request.getFile('${propertyName}excelfilePath')
        def fileName, filePath
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            filePath = getServletContext().getRealPath("/") + "/tmp/"
            if  (fileName.toLowerCase().endsWith(".xlsx")) {
                f.transferTo(new File(filePath +dep_tree_id+ fileName))//把客户端上选的文件xlsx上传到服务器
            } else {
                render "{success:false,info:'错误！导入的Excel文件xlsx类型不对'}"//render "typeError"//导入的Excel文件类型不对
                return
            }
        } else {
            render "{success:false,info:'错误！导入的Excel文件xlsx类型不存在'}" //render "failure"//导入的Excel文件不存在
            return
        }
        Workbook wb
        try {
            wb = WorkbookFactory.create(new File(filePath+dep_tree_id + fileName));//Using a File object allows for lower memory consumptionit's very easy to use one or the other:
            Sheet sheet = wb.getSheetAt(0);//第一张Sheet
            def rowcount = sheet.getLastRowNum();//取得Excel文件的总行数
            int columns = sheet.getRow((short) 1).getPhysicalNumberOfCells();//取得Excel文件的总列数
            Row row, row_bt
            Cell cell, cell_bt
            //取表头,Excel的第一行是标题，第二行是表头，判断所选字段是否都含在选择的Excel文件中
            row_bt = sheet.getRow(1);
            String columnsss = "version"
            int x=0
            for (int jj = 0; jj < columns; jj++) {
                //println("jj==="+jj)
                cell_bt = row_bt.getCell((short) jj);
                if(jj==0){
                    if (fieldItemName[0] != (cell_bt.getStringCellValue().trim())) {
                        render "{success:false,info:'错误！Excel第一列字段名不等于Domain中数据表的第一个字段名（关键字段）则导入失败'}"
                        return
                    }
                }
                int j=0
                for (j = 0; j < n; j++) {
                    //println("j==="+j+"======"+fieldItemName[j]+"========"+cell_bt.getStringCellValue().trim())
                    if (fieldItemName[j].equals(cell_bt.getStringCellValue().trim())) {
                        columnsss = columnsss + "," + fieldNameChang(fieldItem[j])//准备用于新增记录的字段的列表
                        m[x]=jj//所选择字段在Excel中的列序号
                        fieldItemType1[x]=fieldItemType[j] //fieldItemName1[x]=fieldItemName[j]
                        fieldItem1[x++]=fieldNameChang(fieldItem[j])
                        break
                    }
                }
            }
            //println("m.size()==="+m.size()+"---------n="+n)
            if(m.size()!=n){
                render "{success:false,info:'错误！ 导入所选字段（数据库）与Excel字段数量或名称不相符'}"//  render "failure"//导入所选字段（数据库）与Excel字段不相符
                return
            }
            columnsss = "(" + columnsss+",tree_id" + ")"//准备用于新增记录的字段的列表
            // println("columnsss====="+columnsss)
            int a = 2;//从Excel的第三行开始读取数据
            while (a <= rowcount) {
                row = sheet.getRow(a);
                if (!row) break;//Excel表的行结束
                cell = row.getCell((short) 0);
                if (cell.getCellType() == 1) {//不管是字符还是数字，如为空则文件导入结束！！
                    if (!cell.getStringCellValue()) break;//Excel表的行结束
                } else {
                    if (!cell.getNumericCellValue()) break;//Excel表的行结束
                }
                int i = 0
                String department=""
                String values = "0", values_update = "version=0", keyName = "", treeId//values用于新增记录的字段值集，values_update用于对已有记录更新字段集。
                for (i = 0; i < n; i++) {//依次取所选字段
                    cell = row.getCell((short) m[i]);
                    if (i == 0) {
                        //取关键字段
                        if (cell.getCellType() == 1) {//不管是字符还是数字，如为空则文件导入结束！！
                            keyName = cell.getStringCellValue().trim()//第一个字段为关键字段.Excel中第一列为关键字段
                        } else {
                            keyName = (""+cell.getNumericCellValue()).trim()//第一个字段为关键字段.Excel中第一列为关键字段
                        }
                    }
                    switch (fieldItemType1[i]){
                        case "日期":
                            if (!cell) {
                                values = values + ",Null"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "Null"
                                //throw new Exception("日期错误");//不抛出异常，则日期为NULL
                                continue
                            }
                            String  ddt1=""
                            if (cell.getCellType() == 1) {
                                //cell.getCellType():1字符型，0日期型。字符型单元格放的是日期型  //println("=1字符型单元格放的是日期型==")   //println("=1字符型单元格放的是日期型=="+cell.getStringCellValue())    //params.(field[i].getName()) = ((cell.getStringCellValue())?(simpleDateFormat.parse(cell.getStringCellValue().replaceAll("/","-") + " 00:00:00.0")):"")  // println("=1字符型单元格放的是日期型==")
                                ddt1=cell.getStringCellValue()
                                if ((ddt1 ? (simpleDateFormat.parse(ddt1.replaceAll("/", "-"))) : "") != "") {
                                    values = values + ",STR_TO_DATE('" + ddt1.replaceAll('/', '-') + "','%Y-%m-%d')"
                                    values_update = values_update + "," + fieldItem1[i] + "=" + "STR_TO_DATE('" + ddt1.replaceAll('/', '-') + "','%Y-%m-%d')"
                                } else {
                                    values = values + ",NULL"
                                    values_update = values_update + "," + fieldItem1[i] + "=" + "Null"
                                    //throw new Exception("工资日期错误");
                                }
                            } else {
                                //日期型单元格 // println("=2日期型单元格=="+cell.dateCellValue)  // params.(field[i].getName()) = ((cell.dateCellValue)?(simpleDateFormat.format(cell.dateCellValue)):"") // println("==2日期型单元格="+field[i].getName()+"======"+params.(field[i].getName()))                                    //println("=2日期型单元格=="+cell.dateCellValue)
                                ddt1=simpleDateFormat.format(cell.dateCellValue)
                                if (((cell.dateCellValue) ? (simpleDateFormat.format(cell.dateCellValue)) : "") != "") {
                                    values = values + ",STR_TO_DATE('" + (simpleDateFormat.format(cell.dateCellValue)) + "','%Y-%m-%d')"
                                    values_update = values_update + "," + fieldItem1[i] + "=" + "STR_TO_DATE('" + (simpleDateFormat.format(cell.dateCellValue)) + "','%Y-%m-%d')"
                                } else {
                                    values = values + ",NULL"
                                    values_update = values_update + "," + fieldItem1[i] + "=" + "Null"
                                    // throw new Exception("工资日期错误");
                                }
                            }
                            break;
                        case "字符":
                            if (!cell) {
                                values = values + ",''"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "''"
                                continue
                            }
                            if (cell.getCellType() == 1) {
                                //以字符形式放的字符型数据
                                if(fieldItem1[i]=="department") department=((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()):"")
                                values = values + ",'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                            } else {
                                //以数值形式放的字符型数据
                                if (cell.getCellType() == 3) {//CellType类型值：CELL_TYPE_NUMERIC 数值型 0、CELL_TYPE_STRING 字符串型 1、CELL_TYPE_FORMULA 公式型 2、CELL_TYPE_BLANK 空值 3、CELL_TYPE_BOOLEAN 布尔型 4、CELL_TYPE_ERROR 错误 5
                                    values = values + ",''"
                                    values_update = values_update + "," + fieldItem1[i] + "=" + "''"
                                } else {
                                    values = values + "," + (long) cell.getNumericCellValue()
                                    values_update = values_update + "," + fieldItem1[i] + "=" + (long) cell.getNumericCellValue()
                                }
                            }
                            break;
                        case "整数":
                            if (!cell) {
                                values = values + ",0"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "0"
                                continue
                            }
                            try {
                                values = values + "," + cell.getNumericCellValue()
                                values_update = values_update + "," + fieldItem1[i] + "=" + (cell.getNumericCellValue()?cell.getNumericCellValue():"0")
                            } catch (e) {
                                values = values + ",0"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "0"
                            }
                            break;
                        case "浮点数":
                            if (!cell) {
                                values = values + ",0.0"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "0.0"
                                continue
                            }
                            try {
                                if(cell.getCellType()==0){
                                    values = values + "," + (cell.getNumericCellValue()?cell.getNumericCellValue():"0.0")
                                    values_update = values_update + "," + fieldItem1[i] + "=" + (cell.getNumericCellValue()?cell.getNumericCellValue():"0.0")
                                }else{
                                    values = values + "," + (((cell.getStringCellValue()).trim()!="")?cell.getStringCellValue():"0.0")
                                    values_update = values_update + "," + fieldItem1[i] + "=" +  (((cell.getStringCellValue()).trim()!="")?cell.getStringCellValue():"0.0")
                                }
                            } catch (e) {
                                values = values + ",0.0"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "0.0"
                            }
                            break;
                        case "布尔":
                        case "公式":
                        case "字节流":
                            break;
                        default:
                            break
                    }
                }
                String d=deplist.get(department)//由单位中文名称获取单位编码
                if(d&&(d==dep_tree_id)){
                    //必须有单位且与在树状目录选中的单位相同
                    values = values + ",'" + d + "'"
                    values_update = values_update + ",tree_id='" + d + "'"
                }else{
                    render "{success:false,info:'错误！导入数据库失败，可能单位名称有错误！'}"  // render "failure"
                    return
                }
                //values用于新增记录的字段值集，values_update用于对已有记录更新字段集。//println("1111-------values===="+values)    //println("1111-------values_update===="+values_update)
                String fieldNameKey = fieldNameChang(fieldItem1[0])//Domain中数据表中第一个字段为关键字段,fieldNameChang(field[0].getName() == fieldItem1[0]
                query = "FROM  ${className} where " + fieldItem1[0] + "='" + keyName+"'"
                def list8 = ${className}.findAll(query)
                if (list8.size() != 0) {
                    query = "UPDATE ${propertyName}  set " + values_update + " where " + fieldNameKey + "='" + keyName+"'"
                } else {
                    query = "insert into ${propertyName} " + columnsss + " values (" + values + ")";
                }
                //println("2222-------query===="+query)
                if (sql.execute(query)) {
                    render "{success:false,info:'错误！导入数据库失败，可能工资日期错误'}"  // render "failure"
                    return
                }
                a++
            }
        }catch (Exception e) {
            // println("可能工资日期错误或某些数据类型错误..................")
            wb.close()
            File fs = new File(filePath +dep_tree_id+  fileName)//删除上传的文件
            fs.delete()
            render "{success:true,info:'错误！读取导入的Excel文件异常，可能工资日期错误或某些数据类型错误'}" //render "failure"//读取导入的Excel文件异常
            return
        }
        // println("filename.................."+(filePath +dep_tree_id+fileName))
        wb.close()//先关闭wb，再删除文件
        File fs = new File(filePath +dep_tree_id+fileName)//删除上传的文件
        fs.delete()
        render "{success:true,info:'Excel数据全部导入成功'}"
        return
    }

    //导出EXCEL信息
    def selectExport() throws Exception {
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        //取到选择字段名的中文名字fieldItemName,字段名fieldItem，字段的类型fieldItemType,字段的类型长度fieldItemLength
        int n=params.p.size()//所选择要导出的字段数量
        String[] fieldItem = params.p//所选择字段名
        // println("fieldItem====="+fieldItem)
        String[] fieldItemName = new String[n]//所选择字段的中文名字
        String[] fieldItemType = new String[n]//所选择字段的类型
        Integer[] fieldItemLength = new Integer[n]//所选择字段的类型
        String  query = "from DynamicTable where tableNameId='${propertyName}'"
        def list1 = DynamicTable.findAll(query);
        String tableName=list1[0].tableName//导出表的中文名
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < list1.size(); j++) {
                if (fieldItem[i].equals(list1[j].fieldNameId)) {
                    fieldItemName[i] = list1[j].fieldName
                    fieldItemType[i] = list1[j].fieldType
                    fieldItemLength[i]=list1[j].fieldLength
                    break
                }
            }
            //println("fieldItemName==="+fieldItemName[i]+"-----fieldItem==="+fieldItem[i]+"-----fieldItemType==="+fieldItemType[i]+"-----fieldItemLength==="+fieldItemLength[i])
        }
        query="from ${className} where 1=1 "
        if (dep_tree_id == '1000') {
            query = query //全部单位信息
        } else {
            if (dep_tree_id.length() <= 6) {
                query = query + " and substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'"//各区或分类全部单位信息
            } else {
                query = query + " and treeId='" + dep_tree_id + "'"//正常查询
            }
        }
        if(!params.truename)params.truename=""
        if(params.truename!="")query = query + " and name like '%" + params.truename + "%'"
        // println("query==="+query)
        def list = ${className}.findAll(query)
        // println("list.size()===="+list.size())
        try {
            SXSSFWorkbook templatewb = new SXSSFWorkbook(100)//可以存储大数据（>10000行）Excel工作簿对象.xlsx
            templatewb.dispose();//临时文件需要我们手动清除
            templatewb.setCompressTempFiles(true);//you can tell SXSSF to use gzip compression
            Sheet templateSheet = templatewb.createSheet(tableName);
            //设置Excel表的各列宽
            int b = 0
            n.times {
                templateSheet.setColumnWidth((short) b, (short) fieldItemLength[b]*100);
                b++
            }
            //创建标题(合并单元格)
            Row row = templateSheet.createRow((short) 0);
            row.setHeightInPoints((short) 30)
            Cell cell
            b=0
            n.times {
                row.createCell((short) b).setCellValue("");
                b++
            }
            cell=row.createCell((short) 0)
            cell.setCellValue(tableName);
            //设置样式
            CellStyle cellStyle = templatewb.createCellStyle()
            cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER)//垂直
            cell.setCellStyle(cellStyle)
            //合并单元格
            CellRangeAddress region = new CellRangeAddress(0, 0, 0, n-1);
            templateSheet.addMergedRegion(region)
            //创建表头
            row = templateSheet.createRow((short)1);
            row.setHeightInPoints((short) 25)
            b=0
            n.times {
                row.createCell((short) b).setCellValue(fieldItemName[b]);
                b++
            }
            //设置Excel工作表的值
            int i = 0;
            list.each {
                row = templateSheet.createRow((short) i + 2);
                row.setHeightInPoints((short) 22)
                b=0
                n.times {
                    switch (fieldItemType[b]){
                        case "日期":
                            row.createCell((short) b).setCellValue((list[i].getAt(fieldItem[b]))? new java.text.SimpleDateFormat("yyyy-MM-dd").format(list[i].getAt(fieldItem[b])) : "")
                            break;
                        case "公式":
                        case "字符":
                        case "整数":
                        case "浮点数":
                        case "布尔":
                        case "字节流":
                            row.createCell((short) b).setCellValue(list[i].getAt(fieldItem[b]));
                            break;
                        default:
                            row.createCell((short) b).setCellValue(list[i].getAt(fieldItem[b]));
                            break
                    }
                    b++
                }
                i++
            }
            //直接导出到客户端下载
            String file_name=tableName+".xlsx"
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE); //响应类型为application/octet-stream浏览器无法确定文件类型时,不直接显示内容，attachment提示以附件方式下载,Content-Disposition确定浏览器弹出下载对话框
            file_name = new String(file_name.getBytes(), "ISO-8859-1");
            response.setHeader("Content-Disposition","attachment;filename="+file_name);
            response.setHeader("Cache-Control","No-cache");
            response.flushBuffer()
            templatewb.write(response.getOutputStream()); //将模板的内容写到客户端上下载
            templatewb.close()
            // int endTime=System.currentTimeSeconds()
            templatewb.dispose()
            return
        } catch (e) {
            return
        }
    }

    //读取${propertyName}信息
    def read() {
        String dep_tree_id=params.dep_tree_id?params.dep_tree_id:""
        String truename=params.truename?params.truename:""
        String username=params.username?params.username:""
        int len=dep_tree_id.length()
        //如params.tableNameId从网页传来，则params.tableNameId可能为"null"，所以要判断(params.tableNameId=="null")
        if ((!params.dep_tree_id)||(params.dep_tree_id=="")||(params.dep_tree_id=="null")){
            render(contentType: "text/json") {
                ${propertyName}s  null
                totalCount 0
            }
            return
        }
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        def list = ${className}.createCriteria().list(params) {
            if ((dep_tree_id == '1000')||(dep_tree_id == '000')) {
                sqlRestriction "(SUBSTRING(tree_id, 1,3)='001' OR SUBSTRING(tree_id, 1,3)='002' OR SUBSTRING(tree_id, 1,3)='003' OR SUBSTRING(tree_id, 1,3)='004' OR SUBSTRING(tree_id, 1,3)='005') and username like '%"+username+"%' and truename like '%"+truename+"%'"
            } else {
                if (dep_tree_id.length() <= 6) {
                    sqlRestriction "substring(tree_id, 1,\${len})='\${dep_tree_id}' and username like '%"+username+"%' and truename like '%"+truename+"%'"//Use arbitrary SQL to modify the resultset
                } else {
                    sqlRestriction  "tree_id='\${dep_tree_id}' and username like '%"+username+"%' and truename like '%"+truename+"%'"
                }
            }
        }
        render(contentType: "text/json") {
            ${propertyName}s  list.collect() {
                [
                        id: it.id,<%
           for(int i=2;i<domain.size();i++){
                if(domain[i]=="Date"){%>
                        ${domain[i+1]}: it?.${domain[i+1]} ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.${domain[i+1]}) : "",<%
                        i++;i++;
                }else{
                     if(domain[i]=="Blob"){
                         i++;i++;continue
                     }else{%>
                        ${domain[i+1]}: it.${domain[i+1]},<%
                            i++;i++;
                     }
                }
           }%>
                ]
            }
            totalCount      list.totalCount
        }
    }

    //保存新增人员信息
    @Transactional
    def save() {
        ${className} ${propertyName}Instance
        // println("save${className}=== params.dep_tree_id=========="+ params.dep_tree_id)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        String query = "from ${className} where username='" + params.username + "'"
        //println("query==="+query)
        def dataList${className} = ${className}.findAll(query)
        // println("dataList${className}.size()==="+dataList${className}.size())
        if (dataList${className}.size() > 0) {
            // println("保存过")
            params.truename= params.truename?params.truename:"未知"
            ${propertyName}Instance = ${className}.findById(dataList${className}[0].getId())
            ${propertyName}Instance.setProperties(params)
        } else {
            // println("没有保存过")
            params.treeId = (dep_tree_id)//dep_tree_id是这个类的静态变量，记录点击树状菜单上的某个单位的tree_id，
            params.username= params.idNumber?params.idNumber:params.username
            params.truename= params.truename?params.truename:"未知"
            ${propertyName}Instance = new ${className}(params)
            if (${propertyName}Instance == null) {
                notFound()
                return
            }
            if (${propertyName}Instance.hasErrors()) {
                respond ${propertyName}Instance.errors, view: 'create'
                return
            }
        }
        try {
            //${propertyName}Instance.save flush:true
            ${className} i
            i = ${propertyName}Instance.save()//返回当前保存的记录
            if (i != null) {
                // response.getWriter().write("success0");//也可以
                render "success"
                return
            } else {
                // response.getWriter().write("failure0");//也可以
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
        ${className} ${propertyName}Instance = ${className}.findById(params.id)
        if (${propertyName}Instance.getIdNumber()&&(${propertyName}Instance.getIdNumber().length()==18)&&(${propertyName}Instance.getIdNumber().toString() != params.idNumber) && (${propertyName}Instance.getBirthDate().toString() == params.birthDate)) {//修改了工资制度
            String idNumber = params.idNumber
            if (idNumber.length() == 18) {
                params.birthDate = idNumber.substring(6, 10) + "-" + idNumber.substring(10, 12) + "-" + idNumber.substring(12, 14) + " 00:00:00.0"
            } else {
                params.birthDate = "19" + idNumber.substring(6, 8) + "-" + idNumber.substring(8, 10) + "-" + idNumber.substring(10, 12) + " 00:00:00.0"
            }
            params.username = idNumber
        }
        ${propertyName}Instance.setProperties(params)
        if (${propertyName}Instance == null) {
            notFound()
            return
        }
        if (${propertyName}Instance.hasErrors()) {
            return
        }
        //println("-------------${propertyName}Instance.currentStatus==============="+${propertyName}Instance.currentStatus)
        try {
            ${className} i
            i = ${propertyName}Instance.save(flush: true)//返回当前保存的记录
            if (i != null) {
                // response.getWriter().write("success0");//也可以
                render "success"
                return
            } else {
                // response.getWriter().write("failure0");//也可以
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
        def ${propertyName}Instance = ${className}.get(params.id)
        if (${propertyName}Instance == null) {
            notFound()
            return
        }
        try {
            ${className} i
            i = ${propertyName}Instance.delete(flush: true)//返回当前保存的记录
            render "success"//删除成功
            return
        } catch (e) {
            render "failure"//删除失败
            return
        }
    }

    //删除dep_tree_id的所有信息
    @Transactional
    def deleteAll() {
        String dep_tree_id = (params.dep_tree_id) ? params.dep_tree_id : "";
        //println("dep_tree_id=============="+dep_tree_id)        //println("params.departmentName=============="+params.departmentName)
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        /*//可以删除分类
        if (dep_tree_id == '1000') {
            query = "FROM  ${className}"; //全部人员信息
            ${propertyName}query = "Delete FROM  ${propertyName}"//删除所有人员信息
        } else {
            if (dep_tree_id.length() <= 6) {
                query = query + " where substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'"//各区或分类全部单位人员信息
                ${propertyName}query = "Delete FROM  ${propertyName}  where substring(tree_id, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'"//删除各区或分类全部单位人员信息
            } else {
                query = query + " where treeId='" + dep_tree_id + "'"//指定单位人员信息
                ${propertyName}query = "Delete FROM  ${propertyName}  where tree_id='" + dep_tree_id + "'"//删除指定单位人员信息
            }
        }*/
        if (dep_tree_id.length() <= 6) {
            render "failure1"//只能删除某个具体单位的所有人员信息
            return
        }
        String query = "from ${className} where treeId='" + dep_tree_id + "'";
        def list = ${className}.findAll(query)
        if (list.size() == 0) {
            render "failure1"//没有人员信息
            return
        }
        query = "Delete FROM  ${propertyName} where tree_id='" + dep_tree_id + "'";
        try {
            sql.execute(query)
            render "success"//删除成功
            return
        } catch (e) {
            render "failure"
            return
        }
    }


    //把单张照片导入到${className}表中的photo字段中
    @Transactional
    def importPhoto() {
        // println("导入${className}照片importPhoto======params.${propertyName}Code=========" + params.${propertyName}Code);
        String query = "from ${className} where ${propertyName}Code='" + params.${propertyName}Code + "'"// println("query====="+query)
        ${className} ${propertyName}Instance = ${className}.findAll(query)[0]
        if (!${propertyName}Instance) {
            ${propertyName}Instance = new ${className}()
        }
        def f = request.getFile('${propertyName}Photo')//f.size上传文件的大小，如是File f 对象则f.length()是文件的大小
        def fileName
        def filePath
        //if (!f.empty) {
        if (f && !f.empty) {
            if (f.size > 8192000) {
                render "{success:false,info:'错误！导入${className}照片大小超过8M的限制！'}" //导入后文件被裁剪大约小于10
                return
            }
            fileName = f.getOriginalFilename()
            String[] file1 = ((fileName.toLowerCase()).toString()).split("\\\\.")
            String fileType = file1[file1.length - 1]  //取文件名的扩展名（不含.）
            if (fileType != 'jpg' && fileType != 'png' && fileType != 'bmp') {
                render "{success:false,info:'错误！导入照片格式不对！'}" //render "failure"//导入的Excel文件不存在
                return
            }
            filePath = getServletContext().getRealPath("/") + "photoD\\\"
            //临时上传照片的位置，存入数据库后，就删除照片          //println("filePath======================="+filePath)
            try {
                if (!(new File(filePath).isDirectory())) {
                    new File(filePath).mkdir();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            String originfile = filePath + params.${propertyName}Code + "photo1." + fileType
            String imagefile = filePath + params.${propertyName}Code + "photo2." + fileType
            File file = new File(originfile)//file.length()是文件的大小
            f.transferTo(file)//上传的文件先存入临时文件//f.size上传文件的大小
            byte[] data = null;
            FileImageInputStream input = null;
            InputStream input1 = new FileInputStream(originfile)//原文件
            OutputStream newoutput = new FileOutputStream(imagefile);//缩小后的文件
            try {
                resizeImage(input1, newoutput, 170, "jpg")
                input = new FileImageInputStream(new File(imagefile));//缩小后的文件存入数据库
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];//大小只影响转换速度
                int numBytesRead = 0;
                while ((numBytesRead = input.read(buf)) != -1) {
                    output.write(buf, 0, numBytesRead);
                }
                data = output.toByteArray()
                output.close();
                input.close();
            } catch (FileNotFoundException ex1) {
                ex1.printStackTrace();
            }
            catch (IOException ex1) {
                ex1.printStackTrace();
            }
            ${propertyName}Instance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))
            ${propertyName}Instance.save(flush: true)
            File filep = new File(originfile);//删除临时文件
            filep.delete();
            filep = new File(imagefile);//删除临时文件
            filep.delete();
        } else {
            render "{success:false,info:'错误！导入照片不存在！'}" //render "failure"//导入的Excel文件不存在
            return
        }
        render "{success:true,info:'导入照片成功！'}"
        return
    }

    //Add窗口中把单张照片导入到${className}表中的photo字段中
    @Transactional
    def importPhotoAdd() {
        //println("导入${className}照片importPhoto======params.${propertyName}Code=========" + params.${propertyName}Code);
        String query = "from ${className} where username='" + params.username + "'"// println("query====="+query)
        //println("importPhotoAdd===query==="+query)
        ${className} ${propertyName}Instance = ${className}.findAll(query)[0]
        if (!${propertyName}Instance) {
            ${propertyName}Instance = new ${className}(params)
        }
        def f = request.getFile('${propertyName}Photo1')//f.size上传文件的大小，如是File f 对象则f.length()是文件的大小
        def fileName
        def filePath
        //if (!f.empty) {
        if (f && !f.empty) {
            if (f.size > 8192000) {
                render "{success:false,info:'错误！导入${className}照片大小超过8M的限制！'}" //导入后文件被裁剪大约小于10
                return
            }
            fileName = f.getOriginalFilename()
            String[] file1 = ((fileName.toLowerCase()).toString()).split("\\\\.")
            String fileType = file1[file1.length - 1]  //取文件名的扩展名（不含.）
            if (fileType != 'jpg' && fileType != 'png' && fileType != 'bmp') {
                render "{success:false,info:'错误！导入照片格式不对！'}" //render "failure"//导入的Excel文件不存在
                return
            }
            filePath = getServletContext().getRealPath("/") + "photoD\\\"
            //临时上传照片的位置，存入数据库后，就删除照片          //println("filePath======================="+filePath)
            try {
                if (!(new File(filePath).isDirectory())) {
                    new File(filePath).mkdir();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            String originfile = filePath + params.username + "photo1." + fileType
            // String imagefile=filePath + params.${propertyName}Code + "photo2." + fileType
            String imagefile = filePath + params.username + "photo2.jpg"
            File file = new File(originfile)//file.length()是文件的大小
            f.transferTo(file)//上传的文件先存入临时文件//f.size上传文件的大小
            byte[] data = null;
            FileImageInputStream input = null;
            InputStream input1 = new FileInputStream(originfile)//原文件
            OutputStream newoutput = new FileOutputStream(imagefile);//缩小后的文件
            try {
                resizeImage(input1, newoutput, 170, "jpg")
                input = new FileImageInputStream(new File(imagefile));//缩小后的文件存入数据库
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];//大小只影响转换速度
                int numBytesRead = 0;
                while ((numBytesRead = input.read(buf)) != -1) {
                    output.write(buf, 0, numBytesRead);
                }
                data = output.toByteArray()
                output.close();
                input.close();
            } catch (FileNotFoundException ex1) {
                ex1.printStackTrace();
            }
            catch (IOException ex1) {
                ex1.printStackTrace();
            }
            ${propertyName}Instance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))
            String truename= ${propertyName}Instance.getTruename()
            ${propertyName}Instance.setTruename(truename?truename:"未知")
            ${propertyName}Instance.save(flush: true)
            File filep = new File(originfile);//删除临时文件
            filep.delete();
            filep = new File(imagefile);//删除临时文件
            filep.delete();
        } else {
            render "{success:false,info:'错误！导入照片不存在！'}" //render "failure"//导入的Excel文件不存在
            return
        }
        render "{success:true,info:'导入照片成功！'}"
        return
    }

    //Edit窗口中把单张照片导入到${className}表中的photo字段中
    @Transactional
    def importPhotoEdit() {
        //println("导入${className}照片importPhoto======params.${propertyName}Code=========" + params.${propertyName}Code);
        String query = "from ${className} where username='" + params.username + "'"// println("query====="+query)
        // println("importPhotoEdit===query==="+query)
        ${className} ${propertyName}Instance = ${className}.findAll(query)[0]
        if (!${propertyName}Instance) {
            ${propertyName}Instance = new ${className}()
        }
        def f = request.getFile('${propertyName}Photo1')//f.size上传文件的大小，如是File f 对象则f.length()是文件的大小
        def fileName
        def filePath
        //if (!f.empty) {
        if (f && !f.empty) {
            if (f.size > 8192000) {
                render "{success:false,info:'错误！导入${className}照片大小超过8M的限制！'}" //导入后文件被裁剪大约小于10
                return
            }
            fileName = f.getOriginalFilename()
            String[] file1 = ((fileName.toLowerCase()).toString()).split("\\\\.")
            String fileType = file1[file1.length - 1]  //取文件名的扩展名（不含.）
            if (fileType != 'jpg' && fileType != 'png' && fileType != 'bmp') {
                render "{success:false,info:'错误！导入照片格式不对！'}" //render "failure"//导入的Excel文件不存在
                return
            }
            filePath = getServletContext().getRealPath("/") + "photoD\\\"
            //临时上传照片的位置，存入数据库后，就删除照片          //println("filePath======================="+filePath)
            try {
                if (!(new File(filePath).isDirectory())) {
                    new File(filePath).mkdir();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            String originfile = filePath + params.username + "photo1." + fileType
            String imagefile = filePath + params.username + "photo2." + fileType
            File file = new File(originfile)//file.length()是文件的大小
            f.transferTo(file)//上传的文件先存入临时文件//f.size上传文件的大小
            byte[] data = null;
            FileImageInputStream input = null;
            InputStream input1 = new FileInputStream(originfile)//原文件
            OutputStream newoutput = new FileOutputStream(imagefile);//缩小后的文件
            try {
                resizeImage(input1, newoutput, 170, "jpg")
                input = new FileImageInputStream(new File(imagefile));//缩小后的文件存入数据库
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];//大小只影响转换速度
                int numBytesRead = 0;
                while ((numBytesRead = input.read(buf)) != -1) {
                    output.write(buf, 0, numBytesRead);
                }
                data = output.toByteArray()
                output.close();
                input.close();
            } catch (FileNotFoundException ex1) {
                ex1.printStackTrace();
            }
            catch (IOException ex1) {
                ex1.printStackTrace();
            }
            ${propertyName}Instance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))
            ${propertyName}Instance.save(flush: true)
            File filep = new File(originfile);//删除临时文件
            filep.delete();
            filep = new File(imagefile);//删除临时文件
            filep.delete();
        } else {
            render "{success:false,info:'错误！导入照片不存在！'}" //render "failure"//导入的Excel文件不存在
            return
        }
        render "{success:true,info:'导入照片成功！'}"
        return
    }

    /**
     * 改变图片的大小到宽为size，然后高随着宽等比例变化
     * @param is 上传的图片的输入流
     * @param os 改变了图片的大小后，把图片的流输出到目标OutputStream
     * @param size 新图片的宽
     * @param format 新图片的格式
     * @throws IOException
     */
    public static void resizeImage(InputStream is, OutputStream os, int size, String format) throws IOException {
        try {
            BufferedImage prevImage = ImageIO.read(is);
            double width = prevImage.getWidth();
            double height = prevImage.getHeight();
            double percent = size / width;
            int newWidth = (int) (width * percent);
            int newHeight = (int) (height * percent);
            BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_BGR);
            Graphics graphics = image.createGraphics();
            graphics.drawImage(prevImage, 0, 0, newWidth, newHeight, null);
            ImageIO.write(image, format, os);
        } catch (Exception e) {
            e.printStackTrace();
            // println("照片文件异常！不能转换！！")
            // while((temp=iss.read())!=(-1)){oss.write(temp);}//iss 写入oss  // oss.flush();// iss.close(); // oss.close();
        } finally {
            os.flush();
            is.close();
            os.close();
        }
    }

    //批量上传照片文件，然后导入${className}表中，【调试阶段暂时不删除批量上传的照片文件】
    @Transactional
    def uploadFile() {
        // println("uploadFile=====params.departmentDetail===="+params.departmentDetail)
        try {
            String uploadPath = getServletContext().getRealPath("/") + "photoD\\\" + params.departmentDetail + "\\\"
            // println("uploadFile=====uploadPath===="+uploadPath)
            File up = new File(uploadPath);
            if (!up.exists()) {
                up.mkdir();
            }
            def f = request.getFile('file')
            // println("uploadFile=====f.getOriginalFilename()===="+f.getOriginalFilename());
            String originfile = uploadPath + params.name
            //String imagefile=uploadPath + params.${propertyName}Code + "photo2.jpg"
            File file = new File(originfile)//file.length()是文件的大小
            f.transferTo(file)//上传的文件先存入临时文件//f.size上传文件的大小
            String str = params.name
            String ${propertyName}Code = "", temp = "";
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    // ${propertyName}Code+=str.charAt(i);
                    ${propertyName}Code = (str.substring(i)).split("\\\\.")[0];//取照片名字后面的身份证号码
                    break;
                }
            }
            String query = "from ${className} where ${propertyName}Code='" + ${propertyName}Code + "'"// println("query====="+query)
            ${className} ${propertyName}Instance = ${className}.findAll(query)[0]
            if (!${propertyName}Instance) {
                return "success";//有照片，没有${className}信息
            }
            String imagefile = uploadPath + "bak" + params.name//先创建一个临时文件
            byte[] data = null;
            FileImageInputStream input = null;
            InputStream input1 = new FileInputStream(originfile)//原文件
            OutputStream newoutput = new FileOutputStream(imagefile);//缩小后的文件
            resizeImage(input1, newoutput, 170, "jpg")
            input = new FileImageInputStream(new File(imagefile));//缩小后的文件存入数据库
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];//大小只影响转换速度
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray()
            output.close();
            input.close();
            ${propertyName}Instance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))//照片导入${className}表中
            ${propertyName}Instance.save(flush: true)
            // File filep = new File(originfile);//删除临时文件，【调试阶段暂时不删除】
            // filep.delete();
            File filep = new File(imagefile);//删除临时文件
            filep.delete();
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    //从数据库中导出职工的照片
    def displayPhotoAdd() {
        String filePath = getServletContext().getRealPath("/") + "photoD\\\"
        String imagefile = filePath + params.${propertyName}Code + "photo2.jpg"
        //println("imagefile==="+imagefile)
        String query
        //是管理者或是自己的项目,如已上传直接显示，否则必须是完成审核通过的文件才能显示。

        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = null
        BufferedImage ${propertyName}Image;
        String fileType

        // if (${propertyName}Instance&&${propertyName}Instance.getPhoto()) {
        try {
            //println("有照片")
            //inputStream =new InputStream(imagefile)
            //从数据库中读出JPG文件以JPG文件显示
            outputStream = response.getOutputStream();
            ${propertyName}Image = ImageIO.read(new FileImageInputStream(new File(imagefile)));
            ImageIO.write(${propertyName}Image, "JPEG", response.getOutputStream());
            outputStream.flush()
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            // println("无照片")
            //还没有上传文件
            //默认显示一个文件（未知名.png）
            // if(${propertyName}Instance&&${propertyName}Instance.getSex()=="男"){
            //      inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\\" + "male.gif"));
            //  }else{
            inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\\" + "female.gif"));
            //  }
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            response.addHeader("content-type", "application/jpg");
            outputStream.flush()
            outputStream.close();
            inputStream.close();
            /* String str = "照片还没有上传！请等待上传....";
             byte[] buffer = str.getBytes("utf-8")
             outputStream = response.getOutputStream();
             response.setContentType("text/html;charset=UTF-8");
             outputStream.write(buffer);
             outputStream.flush()
             outputStream.close();*/
        }
    }

    //从数据库中导出职工的照片
    def displayPhoto() {
        // String username = User.get(springSecurityService.principal.id).username
        // String truename = User.get(springSecurityService.principal.id).truename
        // String chineseAuthority = User.get(springSecurityService.principal.id).chineseAuthority
        //println("从数据库调出职工的照片照片display${className}Image======params.${propertyName}Code=========" + params.${propertyName}Code);
        // println("登录用户username========" + username);
        // println("登录用户usernamechineseAuthority========" + chineseAuthority);
        String query
        //是管理者或是自己的项目,如已上传直接显示，否则必须是完成审核通过的文件才能显示。
        query = "from ${className} where username='" + params.username + "'"
        //println("displayPhoto   query====="+query)
        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = null
        BufferedImage ${propertyName}Image;
        String fileType
        ${className} ${propertyName}Instance = ${className}.findAll(query)[0]
        try {
            //println("有照片")
            Blob blob = ${propertyName}Instance.getPhoto();
            inputStream = blob.getBinaryStream();
            //从数据库中读出JPG文件以JPG文件显示
            outputStream = response.getOutputStream();
            ${propertyName}Image = ImageIO.read(inputStream);
            ImageIO.write(${propertyName}Image, "JPEG", response.getOutputStream());
            outputStream.flush()
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            // println("无照片sex====="+${propertyName}Instance.getSex())
            //还没有上传文件
            //默认显示一个文件（未知名.png）
            if (${propertyName}Instance && ${propertyName}Instance.getSex() == "男") {
                inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\\" + "male.gif"));
            } else {
                inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\\" + "female.gif"));
            }
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            response.addHeader("content-type", "application/jpg");
            outputStream.flush()
            outputStream.close();
            inputStream.close();
            /* String str = "照片还没有上传！请等待上传....";
            byte[] buffer = str.getBytes("utf-8")
            outputStream = response.getOutputStream();
            response.setContentType("text/html;charset=UTF-8");
            outputStream.write(buffer);
            outputStream.flush()
            outputStream.close();*/
        }
    }



//----------------------------------------------------------------------------------------------------
    //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数
    def toparams(params){
        String jsonStr
        if (params.sort != null) {
            jsonStr = params.sort
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1)
            JSONObject obj = new JSONObject(jsonStr);
            params.sort = obj.get('property')
            params.order = obj.get('direction')//params.ignoreCase - 排序的时候，是否忽视大小写，默认为true
        }
        params.offset = params.start ? params.start as int : 0
        params.max = params.limit ? params.limit as int : 25
        return
    }
    //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
    def json(list){
        SimpleDateFormat f=new java.text.SimpleDateFormat("yyyy-MM-dd")
        List<?> ${propertyName}s = new ArrayList();
        //从定义的类中取出有效字段数n
        def list1=${className}.getDeclaredFields()
        int n=0
        for(int i=0;i<list1.size();i++)if(list1[i].getName()!="constraints"){n++}else{break}
        //从定义的类中取出有效字段名
        String[] fieldName=new String[n]
        String[] fieldType=new String[n]
        for(int i=0;i<n;i++){
            fieldName[i]=list1[i].getName();
            fieldType[i]=list1[i].getType()            //println("fieldName["+i+"]===="+fieldName[i]+"--------fieldType["+i+"]===="+fieldType[i]+"====fieldType[i]==java.util.Date===="+(list1[i].getType()==java.util.Date))            //println("fieldName["+i+"]===="+fieldName[i]+"--------fieldType["+i+"]===="+fieldType[i]+"====fieldType[i]==java.util.Date===="+(fieldType[i]=="class java.util.Date"))
        }
        //从数据库中取出数据生成返回的ArrayList数据，以json格式返回
        for(int j=0;j<(list.size()-1);j++){
            Map<String,String> map=new HashMap<>()
            map.put("id",list[j].id)
            for(int i=0;i<n-1;i++){
                if(fieldType[i]=="class java.util.Date"){
                    def t=list[j].(fieldName[i])
                    map.put(fieldName[i],t?f.format(t) : "")
                }else{
                    map.put(fieldName[i],list[j].(fieldName[i]))
                }
            }
            if(fieldType[n-1]=="class java.util.Date"){
                def t=list[j].(fieldName[n-1])
                map.put(fieldName[n-1],t?f.format(t) : "")
            }else{
                map.put(fieldName[n-1],list[j].(fieldName[n-1]))
            }

            ${propertyName}s.add(map)
        }
        Map<String,String> map=new HashMap<>()
        map.put("id",list[list.size()-1].id)
        for(int i=0;i<n-1;i++){
            if(fieldType[i]=="class java.util.Date"){
                def t=list[list.size()-1].(fieldName[i])
                map.put(fieldName[i],t?f.format(t) :"")
            }else{
                map.put(fieldName[i],list[list.size()-1].(fieldName[i]))
            }
        }
        if(fieldType[n-1]=="class java.util.Date"){
            def t=list[list.size()-1].(fieldName[n-1])
            map.put(fieldName[n-1],t?f.format(t) : "")
        }else{
            map.put(fieldName[n-1],list[list.size()-1].(fieldName[n-1]))
        }
        ${propertyName}s.add(map)
        return ${propertyName}s
    }
    //不带路径不带盘符的名字第一个字母改为大写
    def initcap2(String str) {
        int i = 0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') ch[0] = (char) (ch[0] - 32);
        return new String(ch);
    }
    //不带路径不带盘符的名字第一个字母改为小写
    def initcap3(String str) {
        int i = 0;
        char[] ch = str.toCharArray();
        if (ch[0] >= 'A' && ch[0] <= 'Z') ch[0] = (char) (ch[0] + 32);
        return new String(ch);
    }
    //把grails的字段名制成Mysql的字段名
    String fieldNameChang(fieldName) {
        String cc, dd
        char c
        for (int iii = 0; iii < fieldName.length(); iii++) {
            c = fieldName.charAt(iii);
            if (Character.isUpperCase(c)) {
                cc = c
                dd = (char) (c + 32)
                fieldName = fieldName.replaceAll(cc, "_" + dd)
            }
        }
        return fieldName
    }
}
