package com.app

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import org.apache.commons.fileupload.FileItemIterator
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.util.Streams;
import org.grails.web.json.JSONObject
import com.user.Department
import grails.persistence.Event
import groovy.json.JsonBuilder
import groovy.json.StreamingJsonBuilder

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress

import groovy.sql.Sql
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.MultipartResolver
import org.springframework.web.multipart.commons.CommonsMultipartResolver
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest

import javax.imageio.ImageIO
import javax.imageio.stream.FileImageInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
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

import grails.gorm.transactions.Transactional

@Transactional(readOnly = false)
class StudentController {
    def springSecurityService
    StudentService studentService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
//update执行没问题,delete: "POST"执行没问题 // static allowedMethods = [save: "POST", update: "POST", delete: "DELETE"]//update执行没问题,delete: "DELETE"执行有问题    //static allowedMethods = [save: "POST", update: "PUT"]//update:PUT执行有问题 PUT对UPDATE不行

    //导入EXCEL信息
    @Transactional
    def selectImport() throws Exception {
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : ""
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //准备department<=>treeId
        HashMap<String, String> deplist = new HashMap<String, String>()
        def dep = Department.findAll("from Department")//取treeId对应的Ddepartment
        for (int i = 0; i < dep.size(); i++) {
            deplist.put(dep[i].department, dep[i].treeId)
        }
        //取到选择字段名的中文名字fieldItemName,字段名fieldItem，字段的类型fieldItemType
        int n = params.fieldItem.size()//所选择要导出的字段数量

        String[] fieldItem = params.fieldItem//所选择字段名
        String[] fieldItemName = new String[n]//所选择字段的中文名字
        String[] fieldItemType = new String[n]//所选择字段的类型
        Integer[] fieldItemLength = new Integer[n]//所选择字段的类型
        String[] fieldItem1 = new String[n]//所选择字段名(按Excel表头的顺序重新排序后)
        //String[] fieldItemName1 = new String[n]//所选择字段的中文名字
        String[] fieldItemType1 = new String[n]//所选择字段的类型(按Excel表头的顺序重新排序后)
        //Integer[] fieldItemLength1 = new Integer[n]//所选择字段的类型
        Integer[] m = new Integer[n]//所选择字段(按Excel表头的顺序重新排序后)在Excel中的列序号
        String query = "from DynamicTable where tableNameId='student'"
        def list1 = DynamicTable.findAll(query);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < list1.size(); j++) {
                if (fieldItem[i].equals(list1[j].fieldNameId)) {
                    fieldItemName[i] = list1[j].fieldName
                    fieldItemType[i] = list1[j].fieldType
                    fieldItemLength[i] = list1[j].fieldLength
                    break
                }
            }
            //println("fieldItemName==="+fieldItemName[i]+"-----fieldItem==="+fieldItem[i]+"-----fieldItemType==="+fieldItemType[i]+"-----fieldItemLength==="+fieldItemLength[i])
        }
        def f = request.getFile('studentexcelfilePath')
        def fileName, filePath
        if (!f.empty) {
            fileName = f.getOriginalFilename()
            filePath = getServletContext().getRealPath("/") + "/tmp/"
            if (fileName.toLowerCase().endsWith(".xlsx")) {
                f.transferTo(new File(filePath + dep_tree_id + fileName))//把客户端上选的文件xlsx上传到服务器
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
            wb = WorkbookFactory.create(new File(filePath + dep_tree_id + fileName));
//Using a File object allows for lower memory consumptionit's very easy to use one or the other:
            Sheet sheet = wb.getSheetAt(0);//第一张Sheet
            def rowcount = sheet.getLastRowNum();//取得Excel文件的总行数
            int columns = sheet.getRow((short) 1).getPhysicalNumberOfCells();//取得Excel文件的总列数
            Row row, row_bt
            Cell cell, cell_bt
            //取表头,Excel的第一行是标题，第二行是表头，判断所选字段是否都含在选择的Excel文件中
            row_bt = sheet.getRow(1);
            String columnsss = "version"
            int x = 0
            for (int jj = 0; jj < columns; jj++) {
                //println("jj==="+jj)
                cell_bt = row_bt.getCell((short) jj);
                if (jj == 0) {
                    if (fieldItemName[0] != (cell_bt.getStringCellValue().trim())) {
                        render "{success:false,info:'错误！Excel第一列字段名不等于Domain中数据表的第一个字段名（关键字段）则导入失败'}"
                        return
                    }
                }
                int j = 0
                for (j = 0; j < n; j++) {
                    //println("j==="+j+"======"+fieldItemName[j]+"========"+cell_bt.getStringCellValue().trim())
                    if (fieldItemName[j].equals(cell_bt.getStringCellValue().trim())) {
                        columnsss = columnsss + "," + fieldNameChang(fieldItem[j])//准备用于新增记录的字段的列表
                        m[x] = jj//所选择字段在Excel中的列序号
                        fieldItemType1[x] = fieldItemType[j] //fieldItemName1[x]=fieldItemName[j]
                        fieldItem1[x++] = fieldNameChang(fieldItem[j])
                        break
                    }
                }
            }
            //println("m.size()==="+m.size()+"---------n="+n)
            if (m.size() != n) {
                render "{success:false,info:'错误！ 导入所选字段（数据库）与Excel字段数量或名称不相符'}"
//  render "failure"//导入所选字段（数据库）与Excel字段不相符
                return
            }
            columnsss = "(" + columnsss + ",tree_id" + ")"//准备用于新增记录的字段的列表
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
                String department = ""
                String values = "0", values_update = "version=0", keyName = "", treeId
//values用于新增记录的字段值集，values_update用于对已有记录更新字段集。
                for (i = 0; i < n; i++) {//依次取所选字段
                    cell = row.getCell((short) m[i]);
                    if (i == 0) {
                        //取关键字段
                        if (cell.getCellType() == 1) {//不管是字符还是数字，如为空则文件导入结束！！
                            keyName = cell.getStringCellValue().trim()//第一个字段为关键字段.Excel中第一列为关键字段
                        } else {
                            keyName = ("" + cell.getNumericCellValue()).trim()//第一个字段为关键字段.Excel中第一列为关键字段
                        }
                    }
                    //println("i====="+i+"-------fieldItemType1[i]===="+fieldItemType1[i]+"------m[i]===="+m[i])
                    //println("i====="+i+"-------values===="+values)
                    //println("i====="+i+"-------values_update===="+values_update)
                    switch (fieldItemType1[i]) {
                        case "日期":
                            if (!cell) {
                                values = values + ",Null"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "Null"
                                //throw new Exception("日期错误");//不抛出异常，则日期为NULL
                                continue
                            }
                            String ddt1 = ""
                            if (cell.getCellType() == 1) {
                                //cell.getCellType():1字符型，0日期型。字符型单元格放的是日期型  //println("=1字符型单元格放的是日期型==")   //println("=1字符型单元格放的是日期型=="+cell.getStringCellValue())    //params.(field[i].getName()) = ((cell.getStringCellValue())?(simpleDateFormat.parse(cell.getStringCellValue().replaceAll("/","-") + " 00:00:00.0")):"")  // println("=1字符型单元格放的是日期型==")
                                ddt1 = cell.getStringCellValue()
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
                                ddt1 = simpleDateFormat.format(cell.dateCellValue)
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
                                if (fieldItem1[i] == "department") department = ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "")
                                values = values + ",'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                                values_update = values_update + "," + fieldItem1[i] + "=" + "'" + ((cell.getStringCellValue()) ? (cell.getStringCellValue().trim()) : "") + "'"
                            } else {
                                //以数值形式放的字符型数据
                                if (cell.getCellType() == 3) {
//CellType类型值：CELL_TYPE_NUMERIC 数值型 0、CELL_TYPE_STRING 字符串型 1、CELL_TYPE_FORMULA 公式型 2、CELL_TYPE_BLANK 空值 3、CELL_TYPE_BOOLEAN 布尔型 4、CELL_TYPE_ERROR 错误 5
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
                                values_update = values_update + "," + fieldItem1[i] + "=" + (cell.getNumericCellValue() ? cell.getNumericCellValue() : "0")
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
                                if (cell.getCellType() == 0) {
                                    values = values + "," + (cell.getNumericCellValue() ? cell.getNumericCellValue() : "0.0")
                                    values_update = values_update + "," + fieldItem1[i] + "=" + (cell.getNumericCellValue() ? cell.getNumericCellValue() : "0.0")
                                } else {
                                    values = values + "," + (((cell.getStringCellValue()).trim() != "") ? cell.getStringCellValue() : "0.0")
                                    values_update = values_update + "," + fieldItem1[i] + "=" + (((cell.getStringCellValue()).trim() != "") ? cell.getStringCellValue() : "0.0")
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
                String d = deplist.get(department)//由单位中文名称获取单位编码
                if (d && (d == dep_tree_id)) {
                    //必须有单位且与在树状目录选中的单位相同
                    values = values + ",'" + d + "'"
                    values_update = values_update + ",tree_id='" + d + "'"
                } else {
                    render "{success:false,info:'错误！导入数据库失败，可能单位名称有错误！'}"  // render "failure"
                    return
                }
                //values用于新增记录的字段值集，values_update用于对已有记录更新字段集。//println("1111-------values===="+values)    //println("1111-------values_update===="+values_update)
                String fieldNameKey = fieldNameChang(fieldItem1[0])
//Domain中数据表中第一个字段为关键字段,fieldNameChang(field[0].getName() == fieldItem1[0]
                query = "FROM  Student where " + fieldItem1[0] + "='" + keyName + "'"
                def list8 = Student.findAll(query)
                if (list8.size() != 0) {
                    query = "UPDATE student  set " + values_update + " where " + fieldNameKey + "='" + keyName + "'"
                } else {
                    query = "insert into student " + columnsss + " values (" + values + ")";
                }
                //println("2222-------query===="+query)
                if (sql.execute(query)) {
                    render "{success:false,info:'错误！导入数据库失败，可能工资日期错误'}"  // render "failure"
                    return
                }
                a++
            }
        } catch (Exception e) {
            // println("可能工资日期错误或某些数据类型错误..................")
            wb.close()
            File fs = new File(filePath + dep_tree_id + fileName)//删除上传的文件
            fs.delete()
            render "{success:true,info:'错误！读取导入的Excel文件异常，可能工资日期错误或某些数据类型错误'}" //render "failure"//读取导入的Excel文件异常
            return
        }
        // println("filename.................."+(filePath +dep_tree_id+fileName))
        wb.close()//先关闭wb，再删除文件
        File fs = new File(filePath + dep_tree_id + fileName)//删除上传的文件
        fs.delete()
        render "{success:true,info:'Excel数据全部导入成功'}"
        return
    }

    //导出EXCEL信息
    def selectExport() throws Exception {
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : ""
        //取到选择字段名的中文名字fieldItemName,字段名fieldItem，字段的类型fieldItemType,字段的类型长度fieldItemLength
        int n = params.p.size()//所选择要导出的字段数量
        String[] fieldItem = params.p//所选择字段名
        // println("fieldItem====="+fieldItem)
        String[] fieldItemName = new String[n]//所选择字段的中文名字
        String[] fieldItemType = new String[n]//所选择字段的类型
        Integer[] fieldItemLength = new Integer[n]//所选择字段的类型
        String query = "from DynamicTable where tableNameId='student'"
        def list1 = DynamicTable.findAll(query);
        String tableName = list1[0].tableName//导出表的中文名
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < list1.size(); j++) {
                if (fieldItem[i].equals(list1[j].fieldNameId)) {
                    fieldItemName[i] = list1[j].fieldName
                    fieldItemType[i] = list1[j].fieldType
                    fieldItemLength[i] = list1[j].fieldLength
                    break
                }
            }
            //println("fieldItemName==="+fieldItemName[i]+"-----fieldItem==="+fieldItem[i]+"-----fieldItemType==="+fieldItemType[i]+"-----fieldItemLength==="+fieldItemLength[i])
        }
        query = "from Student where 1=1 "
        if (dep_tree_id == '1000') {
            query = query //全部单位信息
        } else {
            if (dep_tree_id.length() <= 6) {
                query = query + " and substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'"
//各区或分类全部单位信息
            } else {
                query = query + " and treeId='" + dep_tree_id + "'"//正常查询
            }
        }
        if (!params.truename) params.truename = ""
        if (params.truename != "") query = query + " and name like '%" + params.truename + "%'"
        // println("query==="+query)
        def list = Student.findAll(query)
        // println("list.size()===="+list.size())
        try {
            SXSSFWorkbook templatewb = new SXSSFWorkbook(100)//可以存储大数据（>10000行）Excel工作簿对象.xlsx
            templatewb.dispose();//临时文件需要我们手动清除
            templatewb.setCompressTempFiles(true);//you can tell SXSSF to use gzip compression
            Sheet templateSheet = templatewb.createSheet(tableName);
            //设置Excel表的各列宽
            int b = 0
            n.times {
                templateSheet.setColumnWidth((short) b, (short) fieldItemLength[b] * 100);
                b++
            }
            //创建标题(合并单元格)
            Row row = templateSheet.createRow((short) 0);
            row.setHeightInPoints((short) 30)
            Cell cell
            b = 0
            n.times {
                row.createCell((short) b).setCellValue("");
                b++
            }
            cell = row.createCell((short) 0)
            cell.setCellValue(tableName);
            //设置样式
            CellStyle cellStyle = templatewb.createCellStyle()
            cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER)//垂直
            cell.setCellStyle(cellStyle)
            //合并单元格
            CellRangeAddress region = new CellRangeAddress(0, 0, 0, n - 1);
            templateSheet.addMergedRegion(region)
            //创建表头
            row = templateSheet.createRow((short) 1);
            row.setHeightInPoints((short) 25)
            b = 0
            n.times {
                row.createCell((short) b).setCellValue(fieldItemName[b]);
                b++
            }
            //设置Excel工作表的值
            int i = 0;
            list.each {
                row = templateSheet.createRow((short) i + 2);
                row.setHeightInPoints((short) 22)
                b = 0
                n.times {
                    switch (fieldItemType[b]) {
                        case "日期":
                            row.createCell((short) b).setCellValue((list[i].getAt(fieldItem[b])) ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(list[i].getAt(fieldItem[b])) : "")
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
            String file_name = tableName + ".xlsx"
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            //响应类型为application/octet-stream浏览器无法确定文件类型时,不直接显示内容，attachment提示以附件方式下载,Content-Disposition确定浏览器弹出下载对话框
            file_name = new String(file_name.getBytes(), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + file_name);
            response.setHeader("Cache-Control", "No-cache");
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

    //读取student信息
    def read() {
        println("params.dep_tree_id====" + params.dep_tree_id)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : ""
        String truename = params.truename ? params.truename : ""
        String username = params.username ? params.username : ""
        int len = dep_tree_id.length()
        //如params.tableNameId从网页传来，则params.tableNameId可能为"null"，所以要判断(params.tableNameId=="null")
        if ((!params.dep_tree_id) || (params.dep_tree_id == "") || (params.dep_tree_id == "null")) {
            render(contentType: "text/json") {
                students null
                totalCount 0
            }
            return
        }
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        println("dep_tree_id====" + dep_tree_id)
        def list = Student.createCriteria().list(params) {
            if ((dep_tree_id == '1000') || (dep_tree_id == '000')) {
                println("11dep_tree_id====" + dep_tree_id)
                sqlRestriction "(SUBSTRING(tree_id, 1,3)='001' OR SUBSTRING(tree_id, 1,3)='002' OR SUBSTRING(tree_id, 1,3)='003' OR SUBSTRING(tree_id, 1,3)='004' OR SUBSTRING(tree_id, 1,3)='005') and username like '%" + username + "%' and truename like '%" + truename + "%'"
            } else {
                if (dep_tree_id.length() <= 6) {
                    println("22dep_tree_id====" + dep_tree_id)
                    sqlRestriction "substring(tree_id, 1,${len})='${dep_tree_id}' and username like '%" + username + "%' and truename like '%" + truename + "%'"
//Use arbitrary SQL to modify the resultset
                } else {
                    println("33dep_tree_id====" + dep_tree_id)
                    sqlRestriction "tree_id='${dep_tree_id}' and username like '%" + username + "%' and truename like '%" + truename + "%'"
                }
            }
        }
        render(contentType: "text/json") {
            students list.collect() {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            }
            totalCount list.totalCount
        }
    }

    //跨域访问读取student信息Android
    def reada() {
       // println("params.dep_tree_id==qqqq==" + params.dep_tree_id)
        // int len = dep_tree_id.length()
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        // println("dep_tree_id===="+dep_tree_id)
        //def list = Student.createCriteria().list(params) {}
        def list = Student.findAll("from Student")
        def json1
        if (list.size() == 0) {
            json1 = null
        } else {
            json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        }
       // println("json1===" + json1)
       // println("json1.getClass()===" + json1.getClass())
        String objStr222 = JSON.toJSONString(json1);//将 JSON 对象或 JSON 数组转化为字符串
      //  println("JSON 子字符串objStr222===" + objStr222)

        // response.addHeader('Access-Control-Allow-Origin:*');//允许所有来源访问
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
        response.addHeader("Access-Control-Max-Age", "3600000")
        // 设置允许跨域
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        // 是否允许后续请求携带认证信息（cookies）,该值只能是true,否则不返回
        response.setHeader("Access-Control-Allow-Credentials", "true");

        def s = list.collect() {
            [
                    id             : it.id,
                    username       : '"' + it.username + '"',
                    truename       : '"' + it.truename + '"',
                    email          : '"' + it.email + '"',
                    department     : '"' + it.department + '"',
                    major          : '"' + it.major + '"',
                    college        : '"' + it.college + '"',
                    phone          : '"' + it.phone + '"',
                    homephone      : '"' + it.homephone + '"',
                    idNumber       : '"' + it.idNumber + '"',
                    birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "null",
                    sex            : '"' + it.sex + '"',
                    race           : '"' + it.race + '"',
                    politicalStatus: '"' + it.politicalStatus,
                    enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "null",
                    treeId         : '"' + it.treeId + '"',
                    currentStatus  : '"' + it.currentStatus + '"'
            ]
        }
      //  println("s===" + s)
      //  println("s.getClass()===" + s.getClass())

       // String objStr2222 = JSON.toJSONString(jsona);//将 JSON 对象或 JSON 数组转化为字符串
      //  println("JSON 子字符串objStr2222===" + objStr2222)



        def ss = list.collect() {
            [
                    id             : it.id,
                    username       : it.username,
                    truename       : it.truename,
                    email          : it.email,
                    department     : it.department,
                    major          : it.major,
                    college        : it.college,
                    phone          : it.phone,
                    homephone      : it.homephone,
                    idNumber       : it.idNumber,
                    birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "null",
                    sex            : it.sex,
                    race           : it.race,
                    politicalStatus: it.politicalStatus,
                    enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "null",
                    treeId         : it.treeId,
                    currentStatus  : it.currentStatus
            ]
        }
       // println("数组===ss===" + ss)
       // println("ss.getClass()===" + ss.getClass())
        String objStr22 = JSON.toJSONString(ss);//将 JSON 对象或 JSON 数组转化为字符串
      //  println("JSON 子字符串objStr22===" + objStr22)


        def jsonBuilder = new JsonBuilder()
        def json2 = jsonBuilder {
            students(list.collect {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "null",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "null",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            })
        }
       // println("json2===" + json2)
      //  println("jsonBuilder===" + jsonBuilder)
       // println("jsonBuilder.getClass()===" + jsonBuilder.getClass())
       // println("json2.getClass()===" + json2.getClass())

       // println("JSON.toJSONString(json2[0])===" + JSON.toJSONString(json2[0]))
      //  println("JSON.toJSONString(json2)====" + JSON.toJSONString(json2))//将 JSON 对象或 JSON 数组转化为字符串
        JSONObject obj = JSON.parseObject(jsonBuilder.toPrettyString())//从字符串解析 JSON 对象,com.alibaba.fastjson.JSON
      //  println("JSON 对象====obj===" + obj)
        String objStr1 = JSON.toJSONString(obj);//将 JSON 对象或 JSON 数组转化为字符串
       // println("JSON 字符串===objStr1===" + objStr1)

        JSONArray obj1 = obj.get("students")//com.alibaba.fastjson.JSONArray
      //  println("数组===JSON 子数组obj1===" + obj1)
      //  println("obj1.getClass()===" + obj1.getClass())
        String objStr2 = JSON.toJSONString(obj1);//将 JSON 对象或 JSON 数组转化为字符串
      //  println("JSON 子字符串objStr2===" + objStr2)
/*
json1===[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, photo:null, idNumber:null, enrollDate:, birthDate:, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, photo:null, idNumber:null, enrollDate:, birthDate:, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, photo:null, idNumber:null, enrollDate:, birthDate:, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, photo:null, idNumber:null, enrollDate:, birthDate:, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, photo:null, idNumber:null, enrollDate:, birthDate:, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]
json1.getClass()===class java.util.ArrayList
JSON 子字符串objStr222===[{"truename":"陆立松","enrollDate":"","birthDate":"","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"","birthDate":"","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"","birthDate":"","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"","birthDate":"","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"","birthDate":"","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]
s===[[id:88, username:"admin", truename:"陆立松", email:"lulisongtest@63.com", department:"null", major:"null", college:"null", phone:"15309923872", homephone:"null", idNumber:"null", birthDate:null, sex:"null", race:"null", politicalStatus:"null, enrollDate:null, treeId:"null", currentStatus:"null"], [id:89, username:"yangliehui", truename:"杨烈辉", email:"lulisongtest123@63.com", department:"null", major:"null", college:"null", phone:"18116859252", homephone:"null", idNumber:"null", birthDate:null, sex:"null", race:"null", politicalStatus:"null, enrollDate:null, treeId:"null", currentStatus:"null"], [id:90, username:"llsylh", truename:"张基数", email:"123@456.com", department:"null", major:"null", college:"null", phone:"12345678", homephone:"null", idNumber:"null", birthDate:null, sex:"null", race:"null", politicalStatus:"null, enrollDate:null, treeId:"null", currentStatus:"null"], [id:91, username:"aser", truename:"王定军", email:"345@123.com", department:"null", major:"null", college:"null", phone:"987654332", homephone:"null", idNumber:"null", birthDate:null, sex:"null", race:"null", politicalStatus:"null, enrollDate:null, treeId:"null", currentStatus:"null"], [id:92, username:"zzzz", truename:"李昌虽", email:"ddd@123.com", department:"null", major:"null", college:"null", phone:"222222222", homephone:"null", idNumber:"null", birthDate:null, sex:"null", race:"null", politicalStatus:"null, enrollDate:null, treeId:"null", currentStatus:"null"]]
s.getClass()===class java.util.ArrayList
数组===ss===[[id:88, username:admin, truename:陆立松, email:lulisongtest@63.com, department:null, major:null, college:null, phone:15309923872, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:89, username:yangliehui, truename:杨烈辉, email:lulisongtest123@63.com, department:null, major:null, college:null, phone:18116859252, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:90, username:llsylh, truename:张基数, email:123@456.com, department:null, major:null, college:null, phone:12345678, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:91, username:aser, truename:王定军, email:345@123.com, department:null, major:null, college:null, phone:987654332, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:92, username:zzzz, truename:李昌虽, email:ddd@123.com, department:null, major:null, college:null, phone:222222222, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null]]
ss.getClass()===class java.util.ArrayList
JSON 子字符串objStr22===[{"id":88,"username":"admin","truename":"陆立松","email":"lulisongtest@63.com","phone":"15309923872","birthDate":"null","enrollDate":"null"},{"id":89,"username":"yangliehui","truename":"杨烈辉","email":"lulisongtest123@63.com","phone":"18116859252","birthDate":"null","enrollDate":"null"},{"id":90,"username":"llsylh","truename":"张基数","email":"123@456.com","phone":"12345678","birthDate":"null","enrollDate":"null"},{"id":91,"username":"aser","truename":"王定军","email":"345@123.com","phone":"987654332","birthDate":"null","enrollDate":"null"},{"id":92,"username":"zzzz","truename":"李昌虽","email":"ddd@123.com","phone":"222222222","birthDate":"null","enrollDate":"null"}]
json2===[students:[[id:88, username:admin, truename:陆立松, email:lulisongtest@63.com, department:null, major:null, college:null, phone:15309923872, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:89, username:yangliehui, truename:杨烈辉, email:lulisongtest123@63.com, department:null, major:null, college:null, phone:18116859252, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:90, username:llsylh, truename:张基数, email:123@456.com, department:null, major:null, college:null, phone:12345678, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:91, username:aser, truename:王定军, email:345@123.com, department:null, major:null, college:null, phone:987654332, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:92, username:zzzz, truename:李昌虽, email:ddd@123.com, department:null, major:null, college:null, phone:222222222, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null]]]
jsonBuilder==={"students":[{"id":88,"username":"admin","truename":"\u9646\u7acb\u677e","email":"lulisongtest@63.com","department":null,"major":null,"college":null,"phone":"15309923872","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":89,"username":"yangliehui","truename":"\u6768\u70c8\u8f89","email":"lulisongtest123@63.com","department":null,"major":null,"college":null,"phone":"18116859252","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":90,"username":"llsylh","truename":"\u5f20\u57fa\u6570","email":"123@456.com","department":null,"major":null,"college":null,"phone":"12345678","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":91,"username":"aser","truename":"\u738b\u5b9a\u519b","email":"345@123.com","department":null,"major":null,"college":null,"phone":"987654332","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":92,"username":"zzzz","truename":"\u674e\u660c\u867d","email":"ddd@123.com","department":null,"major":null,"college":null,"phone":"222222222","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null}]}
jsonBuilder.getClass()===class groovy.json.JsonBuilder
json2.getClass()===class java.util.LinkedHashMap
JSON.toJSONString(json2[0])===null
JSON.toJSONString(json2)===={"students":[{"id":88,"username":"admin","truename":"陆立松","email":"lulisongtest@63.com","phone":"15309923872","birthDate":"null","enrollDate":"null"},{"id":89,"username":"yangliehui","truename":"杨烈辉","email":"lulisongtest123@63.com","phone":"18116859252","birthDate":"null","enrollDate":"null"},{"id":90,"username":"llsylh","truename":"张基数","email":"123@456.com","phone":"12345678","birthDate":"null","enrollDate":"null"},{"id":91,"username":"aser","truename":"王定军","email":"345@123.com","phone":"987654332","birthDate":"null","enrollDate":"null"},{"id":92,"username":"zzzz","truename":"李昌虽","email":"ddd@123.com","phone":"222222222","birthDate":"null","enrollDate":"null"}]}
JSON 对象====obj===[students:[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]]
JSON 字符串===objStr1==={"students":[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]}
数组===JSON 子数组obj1===[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]
obj1.getClass()===class com.alibaba.fastjson.JSONArray
JSON 子字符串objStr2===[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]
 */

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

        /*json1="""[{
         id: 1,
         username: "1Ed Spencer",
         truename: "1Ed Spencer",
         email: "1ed@sencha.com",
         phone: "115309923872"
     },
     {
         id: 2,
         username: "2Ed Spencer",
         truename: "2Ed Spencer",
         email: "2ed@sencha.com",
         phone: "215309923872"
     }]""";
        json1="""
         [{id:88, username:"admin", truename:"陆立松", email:"lulisongtest@63.com", department:"null", major:"null", college:"null", phone:null, homephone:"null", idNumber:"null", birthDate:null, sex:"null", race:"null", politicalStatus:"null", enrollDate:null, treeId:"null", currentStatus:"null"}, {id:89, username:"yangliehui", truename:"杨烈辉", email:"lulisongtest123@63.com", department:"null", major:"null", college:"null", phone:"18116859252", homephone:"null", idNumber:"null", birthDate:"null", sex:"null", race:"null", politicalStatus:"null", enrollDate:"null", treeId:"null", currentStatus:"null"}]
         """;*/
        //out.print(s.toJsonString());
        // out.write(json1);
        // out.write(objStr2);
        //out.write(objStr22);
        out.write(objStr222);
        // out.write(s);
        if (jsonP) {
            out.write(");");
        }
        render ""
        return

    }

    //ajax访问读取student信息Android
    def readb() {
        def json1
       // println("params.dep_tree_id==qqqq==" + params.dep_tree_id)
        // int len = dep_tree_id.length()
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        // println("dep_tree_id===="+dep_tree_id)
        def list = Student.createCriteria().list(params) {}
        json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        /*def list = Student.findAll("from Student")
        if (list.size() == 0) {
            json1 = null
        } else {
            json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        }*/

        // response.addHeader('Access-Control-Allow-Origin:*');//允许所有来源访问
        // response.addHeader('Access-Control-Allow-Method:POST,GET');//允许访问的方式
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
        response.addHeader("Access-Control-Max-Age", "3600000")
        // 设置允许跨域
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        // 是否允许后续请求携带认证信息（cookies）,该值只能是true,否则不返回
        response.setHeader("Access-Control-Allow-Credentials", "true");

        def jsonBuilder = new JsonBuilder()
        def json2 = jsonBuilder {
            students(list.collect {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "null",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "null",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            })
        }
        //println("json2===" + json2)
       // println("jsonBuilder===" + jsonBuilder)
       // println("jsonBuilder.getClass()===" + jsonBuilder.getClass())
       // println("json2.getClass()===" + json2.getClass())

       // println("JSON.toJSONString(json2[0])===" + JSON.toJSONString(json2[0]))
       // println("JSON.toJSONString(json2)====" + JSON.toJSONString(json2))//将 JSON 对象或 JSON 数组转化为字符串
        JSONObject obj = JSON.parseObject(jsonBuilder.toPrettyString())//从字符串解析 JSON 对象,com.alibaba.fastjson.JSON
       // println("JSON 对象====obj===" + obj)
        String objStr1 = JSON.toJSONString(obj);//将 JSON 对象或 JSON 数组转化为字符串
        //println("JSON 字符串===objStr1===" + objStr1)

        JSONArray obj1 = obj.get("students")//com.alibaba.fastjson.JSONArray
        //println("JSON 子数组obj1===" + obj1)
        //println("obj1.getClass()===" + obj1.getClass())
        String objStr2 = JSON.toJSONString(obj1);//将 JSON 对象或 JSON 数组转化为字符串
        //println("JSON 子字符串objStr2===" + objStr2)
/*
json2===[students:[[id:88, username:admin, truename:陆立松, email:lulisongtest@63.com, department:null, major:null, college:null, phone:15309923872, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:89, username:yangliehui, truename:杨烈辉, email:lulisongtest123@63.com, department:null, major:null, college:null, phone:18116859252, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:90, username:llsylh, truename:张基数, email:123@456.com, department:null, major:null, college:null, phone:12345678, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:91, username:aser, truename:王定军, email:345@123.com, department:null, major:null, college:null, phone:987654332, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:92, username:zzzz, truename:李昌虽, email:ddd@123.com, department:null, major:null, college:null, phone:222222222, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null]]]
jsonBuilder==={"students":[{"id":88,"username":"admin","truename":"\u9646\u7acb\u677e","email":"lulisongtest@63.com","department":null,"major":null,"college":null,"phone":"15309923872","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":89,"username":"yangliehui","truename":"\u6768\u70c8\u8f89","email":"lulisongtest123@63.com","department":null,"major":null,"college":null,"phone":"18116859252","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":90,"username":"llsylh","truename":"\u5f20\u57fa\u6570","email":"123@456.com","department":null,"major":null,"college":null,"phone":"12345678","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":91,"username":"aser","truename":"\u738b\u5b9a\u519b","email":"345@123.com","department":null,"major":null,"college":null,"phone":"987654332","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":92,"username":"zzzz","truename":"\u674e\u660c\u867d","email":"ddd@123.com","department":null,"major":null,"college":null,"phone":"222222222","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null}]}
jsonBuilder.getClass()===class groovy.json.JsonBuilder
json2.getClass()===class java.util.LinkedHashMap
JSON.toJSONString(json2[0])===null
JSON.toJSONString(json2)===={"students":[{"id":88,"username":"admin","truename":"陆立松","email":"lulisongtest@63.com","phone":"15309923872","birthDate":"null","enrollDate":"null"},{"id":89,"username":"yangliehui","truename":"杨烈辉","email":"lulisongtest123@63.com","phone":"18116859252","birthDate":"null","enrollDate":"null"},{"id":90,"username":"llsylh","truename":"张基数","email":"123@456.com","phone":"12345678","birthDate":"null","enrollDate":"null"},{"id":91,"username":"aser","truename":"王定军","email":"345@123.com","phone":"987654332","birthDate":"null","enrollDate":"null"},{"id":92,"username":"zzzz","truename":"李昌虽","email":"ddd@123.com","phone":"222222222","birthDate":"null","enrollDate":"null"}]}
JSON 对象====obj===[students:[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]]
JSON 字符串===objStr1==={"students":[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]}
JSON 子数组obj1===[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]
obj1.getClass()===class com.alibaba.fastjson.JSONArray
JSON 子字符串objStr2===[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]
 */
        render objStr1
        //render  jsonBuilder.toPrettyString()//也可以

/*//也可以
        render(contentType: "text/json") {
            students list.collect() {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            }
        }
*/
    }

    //ajax访问读取student信息Android
    def readStudentAjax1() {
        def json1
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        def queryResult = {
            if((params.queryDepartment)&&(params.queryDepartment != "全部")){
                like("department","%"+params.queryDepartment+"%")
            }
            if(params.queryUsername){
                like("truename","%"+params.queryUsername+"%")
            }
        }
        def result = Student.createCriteria().list(params,queryResult)
        render(contentType: "application/json") {
            students result.collect() {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "null",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "null",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            }
            totalCount  result.totalCount
            // totalCount  result.size()
        }
        return





        json1 = json(result) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        /*def list = Student.findAll("from Student")
        if (list.size() == 0) {
            json1 = null
        } else {
            json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        }*/

        // response.addHeader('Access-Control-Allow-Origin:*');//允许所有来源访问
        // response.addHeader('Access-Control-Allow-Method:POST,GET');//允许访问的方式
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
        response.addHeader("Access-Control-Max-Age", "3600000")
        // 设置允许跨域
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        // 是否允许后续请求携带认证信息（cookies）,该值只能是true,否则不返回
        response.setHeader("Access-Control-Allow-Credentials", "true");

        def jsonBuilder = new JsonBuilder()
        def json2 = jsonBuilder {
            students(result.collect {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "null",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "null",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            })
        }
        //println("json2===" + json2)
        // println("jsonBuilder===" + jsonBuilder)
        // println("jsonBuilder.getClass()===" + jsonBuilder.getClass())
        // println("json2.getClass()===" + json2.getClass())

        // println("JSON.toJSONString(json2[0])===" + JSON.toJSONString(json2[0]))
        // println("JSON.toJSONString(json2)====" + JSON.toJSONString(json2))//将 JSON 对象或 JSON 数组转化为字符串
        JSONObject obj = JSON.parseObject(jsonBuilder.toPrettyString())//从字符串解析 JSON 对象,com.alibaba.fastjson.JSON
        // println("JSON 对象====obj===" + obj)
        String objStr1 = JSON.toJSONString(obj);//将 JSON 对象或 JSON 数组转化为字符串
        //println("JSON 字符串===objStr1===" + objStr1)

        JSONArray obj1 = obj.get("students")//com.alibaba.fastjson.JSONArray
        //println("JSON 子数组obj1===" + obj1)
        //println("obj1.getClass()===" + obj1.getClass())
        String objStr2 = JSON.toJSONString(obj1);//将 JSON 对象或 JSON 数组转化为字符串
        //println("JSON 子字符串objStr2===" + objStr2)
/*
json2===[students:[[id:88, username:admin, truename:陆立松, email:lulisongtest@63.com, department:null, major:null, college:null, phone:15309923872, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:89, username:yangliehui, truename:杨烈辉, email:lulisongtest123@63.com, department:null, major:null, college:null, phone:18116859252, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:90, username:llsylh, truename:张基数, email:123@456.com, department:null, major:null, college:null, phone:12345678, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:91, username:aser, truename:王定军, email:345@123.com, department:null, major:null, college:null, phone:987654332, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null], [id:92, username:zzzz, truename:李昌虽, email:ddd@123.com, department:null, major:null, college:null, phone:222222222, homephone:null, idNumber:null, birthDate:null, sex:null, race:null, politicalStatus:null, enrollDate:null, treeId:null, currentStatus:null]]]
jsonBuilder==={"students":[{"id":88,"username":"admin","truename":"\u9646\u7acb\u677e","email":"lulisongtest@63.com","department":null,"major":null,"college":null,"phone":"15309923872","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":89,"username":"yangliehui","truename":"\u6768\u70c8\u8f89","email":"lulisongtest123@63.com","department":null,"major":null,"college":null,"phone":"18116859252","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":90,"username":"llsylh","truename":"\u5f20\u57fa\u6570","email":"123@456.com","department":null,"major":null,"college":null,"phone":"12345678","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":91,"username":"aser","truename":"\u738b\u5b9a\u519b","email":"345@123.com","department":null,"major":null,"college":null,"phone":"987654332","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null},{"id":92,"username":"zzzz","truename":"\u674e\u660c\u867d","email":"ddd@123.com","department":null,"major":null,"college":null,"phone":"222222222","homephone":null,"idNumber":null,"birthDate":"null","sex":null,"race":null,"politicalStatus":null,"enrollDate":"null","treeId":null,"currentStatus":null}]}
jsonBuilder.getClass()===class groovy.json.JsonBuilder
json2.getClass()===class java.util.LinkedHashMap
JSON.toJSONString(json2[0])===null
JSON.toJSONString(json2)===={"students":[{"id":88,"username":"admin","truename":"陆立松","email":"lulisongtest@63.com","phone":"15309923872","birthDate":"null","enrollDate":"null"},{"id":89,"username":"yangliehui","truename":"杨烈辉","email":"lulisongtest123@63.com","phone":"18116859252","birthDate":"null","enrollDate":"null"},{"id":90,"username":"llsylh","truename":"张基数","email":"123@456.com","phone":"12345678","birthDate":"null","enrollDate":"null"},{"id":91,"username":"aser","truename":"王定军","email":"345@123.com","phone":"987654332","birthDate":"null","enrollDate":"null"},{"id":92,"username":"zzzz","truename":"李昌虽","email":"ddd@123.com","phone":"222222222","birthDate":"null","enrollDate":"null"}]}
JSON 对象====obj===[students:[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]]
JSON 字符串===objStr1==={"students":[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]}
JSON 子数组obj1===[[college:null, truename:陆立松, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:15309923872, id:88, department:null, email:lulisongtest@63.com, username:admin], [college:null, truename:杨烈辉, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:18116859252, id:89, department:null, email:lulisongtest123@63.com, username:yangliehui], [college:null, truename:张基数, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:12345678, id:90, department:null, email:123@456.com, username:llsylh], [college:null, truename:王定军, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:987654332, id:91, department:null, email:345@123.com, username:aser], [college:null, truename:李昌虽, homephone:null, race:null, currentStatus:null, politicalStatus:null, sex:null, idNumber:null, enrollDate:null, birthDate:null, treeId:null, major:null, phone:222222222, id:92, department:null, email:ddd@123.com, username:zzzz]]
obj1.getClass()===class com.alibaba.fastjson.JSONArray
JSON 子字符串objStr2===[{"truename":"陆立松","enrollDate":"null","birthDate":"null","phone":"15309923872","id":88,"email":"lulisongtest@63.com","username":"admin"},{"truename":"杨烈辉","enrollDate":"null","birthDate":"null","phone":"18116859252","id":89,"email":"lulisongtest123@63.com","username":"yangliehui"},{"truename":"张基数","enrollDate":"null","birthDate":"null","phone":"12345678","id":90,"email":"123@456.com","username":"llsylh"},{"truename":"王定军","enrollDate":"null","birthDate":"null","phone":"987654332","id":91,"email":"345@123.com","username":"aser"},{"truename":"李昌虽","enrollDate":"null","birthDate":"null","phone":"222222222","id":92,"email":"ddd@123.com","username":"zzzz"}]
 */
        render objStr1
        //render  jsonBuilder.toPrettyString()//也可以

/*//也可以
        render(contentType: "text/json") {
            students list.collect() {
                [
                        id             : it.id,
                        username       : it.username,
                        truename       : it.truename,
                        email          : it.email,
                        department     : it.department,
                        major          : it.major,
                        college        : it.college,
                        phone          : it.phone,
                        homephone      : it.homephone,
                        idNumber       : it.idNumber,
                        birthDate      : it?.birthDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.birthDate) : "",
                        sex            : it.sex,
                        race           : it.race,
                        politicalStatus: it.politicalStatus,
                        enrollDate     : it?.enrollDate ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.enrollDate) : "",
                        treeId         : it.treeId,
                        currentStatus  : it.currentStatus
                ]
            }
        }
*/
    }

    //读取student信息
    def readBak() {
        //println("params.dep_tree_id===="+params.dep_tree_id)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : ""
        String truename = params.truename ? params.truename : ""
        String username = params.username ? params.username : ""
        int len = dep_tree_id.length()
        //如params.tableNameId从网页传来，则params.tableNameId可能为"null"，所以要判断(params.tableNameId=="null")
        if ((!params.dep_tree_id) || (params.dep_tree_id == "") || (params.dep_tree_id == "null")) {
            render(contentType: "text/json") {
                students null
                totalCount 0
            }
            return
        }
        toparams(params) //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数,这样可以通过params.offse、params.max来控制分页
        def list = Student.createCriteria().list(params) {
            if ((dep_tree_id == '1000') || (dep_tree_id == '000')) {
                sqlRestriction "(SUBSTRING(tree_id, 1,3)='001' OR SUBSTRING(tree_id, 1,3)='002' OR SUBSTRING(tree_id, 1,3)='003' OR SUBSTRING(tree_id, 1,3)='004' OR SUBSTRING(tree_id, 1,3)='005') and username like '%" + username + "%' and truename like '%" + truename + "%'"
            } else {
                if (dep_tree_id.length() <= 6) {
                    sqlRestriction "substring(tree_id, 1,${len})='${dep_tree_id}' and username like '%" + username + "%' and truename like '%" + truename + "%'"
//Use arbitrary SQL to modify the resultset
                } else {
                    sqlRestriction "tree_id='${dep_tree_id}' and username like '%" + username + "%' and truename like '%" + truename + "%'"
                }
            }
        }
        def json1
        if (list.totalCount == 0) {
            json1 = null
        } else {
            json1 = json(list) //把从数据库中取的list数据转换为ArrayList，以json数据格式返回给extjs的网页
        }
        render(contentType: "text/json") {
            students json1
            totalCount list.totalCount
        }
    }


    //保存新增人员信息
    @Transactional
    def save() {
        Student studentInstance
        // println("saveStudent=== params.dep_tree_id=========="+ params.dep_tree_id)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        String query = "from Student where username='" + params.username + "'"
        //println("query==="+query)
        def dataListStudent = Student.findAll(query)
        // println("dataListStudent.size()==="+dataListStudent.size())
        if (dataListStudent.size() > 0) {
            // println("保存过")
            studentInstance = Student.findById(dataListStudent[0].getId())
            studentInstance.setProperties(params)
        } else {
            // println("没有保存过")
            params.treeId = (dep_tree_id)//dep_tree_id是这个类的静态变量，记录点击树状菜单上的某个单位的tree_id，
            params.username = params.idNumber
            studentInstance = new Student(params)
            if (studentInstance == null) {
                notFound()
                return
            }
            if (studentInstance.hasErrors()) {
                respond studentInstance.errors, view: 'create'
                return
            }
        }
        try {
            //studentInstance.save flush:true
            Student i
            i = studentInstance.save()//返回当前保存的记录
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
        Student studentInstance = Student.findById(params.id)
        if (studentInstance.getIdNumber() && (studentInstance.getIdNumber().length() == 18) && (studentInstance.getIdNumber().toString() != params.idNumber) && (studentInstance.getBirthDate().toString() == params.birthDate)) {
//修改了工资制度
            String idNumber = params.idNumber
            if (idNumber.length() == 18) {
                params.birthDate = idNumber.substring(6, 10) + "-" + idNumber.substring(10, 12) + "-" + idNumber.substring(12, 14) + " 00:00:00.0"
            } else {
                params.birthDate = "19" + idNumber.substring(6, 8) + "-" + idNumber.substring(8, 10) + "-" + idNumber.substring(10, 12) + " 00:00:00.0"
            }
            params.username = idNumber
        }
        studentInstance.setProperties(params)
        if (studentInstance == null) {
            notFound()
            return
        }
        if (studentInstance.hasErrors()) {
            return
        }
        //println("-------------studentInstance.currentStatus==============="+studentInstance.currentStatus)
        try {
            Student i
            i = studentInstance.save(flush: true)//返回当前保存的记录
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
        def studentInstance = Student.get(params.id)
        if (studentInstance == null) {
            notFound()
            return
        }
        try {
            Student i
            i = studentInstance.delete(flush: true)//返回当前保存的记录
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
            query = "FROM  Student"; //全部人员信息
            studentquery = "Delete FROM  student"//删除所有人员信息
        } else {
            if (dep_tree_id.length() <= 6) {
                query = query + " where substring(treeId, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'"//各区或分类全部单位人员信息
                studentquery = "Delete FROM  student  where substring(tree_id, 1,LENGTH('" + dep_tree_id + "'))='" + dep_tree_id + "'"//删除各区或分类全部单位人员信息
            } else {
                query = query + " where treeId='" + dep_tree_id + "'"//指定单位人员信息
                studentquery = "Delete FROM  student  where tree_id='" + dep_tree_id + "'"//删除指定单位人员信息
            }
        }*/
        if (dep_tree_id.length() <= 6) {
            render "failure1"//只能删除某个具体单位的所有人员信息
            return
        }
        String query = "from Student where treeId='" + dep_tree_id + "'";
        def list = Student.findAll(query)
        if (list.size() == 0) {
            render "failure1"//没有人员信息
            return
        }
        query = "Delete FROM  student where tree_id='" + dep_tree_id + "'";
        try {
            sql.execute(query)
            render "success"//删除成功
            return
        } catch (e) {
            render "failure"
            return
        }
    }


    //把单张照片导入到Student表中的photo字段中
    @Transactional
    def importPhoto() {
        // println("导入Student照片importPhoto======params.studentCode=========" + params.studentCode);
        String query = "from Student where studentCode='" + params.studentCode + "'"// println("query====="+query)
        Student studentInstance = Student.findAll(query)[0]
        if (!studentInstance) {
            studentInstance = new Student()
        }
        def f = request.getFile('studentPhoto')//f.size上传文件的大小，如是File f 对象则f.length()是文件的大小
        def fileName
        def filePath
        //if (!f.empty) {
        if (f && !f.empty) {
            if (f.size > 8192000) {
                render "{success:false,info:'错误！导入Student照片大小超过8M的限制！'}" //导入后文件被裁剪大约小于10
                return
            }
            fileName = f.getOriginalFilename()
            String[] file1 = ((fileName.toLowerCase()).toString()).split("\\.")
            String fileType = file1[file1.length - 1]  //取文件名的扩展名（不含.）
            if (fileType != 'jpg' && fileType != 'png' && fileType != 'bmp') {
                render "{success:false,info:'错误！导入照片格式不对！'}" //render "failure"//导入的Excel文件不存在
                return
            }
            filePath = getServletContext().getRealPath("/") + "photoD\\"
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
            studentInstance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))
            studentInstance.save(flush: true)
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

    //Add窗口中把单张照片导入到Student表中的photo字段中
    @Transactional
    def importPhotoAdd() {
        //println("导入Student照片importPhoto======params.studentCode=========" + params.studentCode);
        String query = "from Student where username='" + params.username + "'"// println("query====="+query)
        //println("importPhotoAdd===query==="+query)
        Student studentInstance = Student.findAll(query)[0]
        if (!studentInstance) {
            studentInstance = new Student(params)
        }
        def f = request.getFile('studentPhoto1')//f.size上传文件的大小，如是File f 对象则f.length()是文件的大小
        def fileName
        def filePath
        //if (!f.empty) {
        if (f && !f.empty) {
            if (f.size > 8192000) {
                render "{success:false,info:'错误！导入Student照片大小超过8M的限制！'}" //导入后文件被裁剪大约小于10
                return
            }
            fileName = f.getOriginalFilename()
            String[] file1 = ((fileName.toLowerCase()).toString()).split("\\.")
            String fileType = file1[file1.length - 1]  //取文件名的扩展名（不含.）
            if (fileType != 'jpg' && fileType != 'png' && fileType != 'bmp') {
                render "{success:false,info:'错误！导入照片格式不对！'}" //render "failure"//导入的Excel文件不存在
                return
            }
            filePath = getServletContext().getRealPath("/") + "photoD\\"
            //临时上传照片的位置，存入数据库后，就删除照片          //println("filePath======================="+filePath)
            try {
                if (!(new File(filePath).isDirectory())) {
                    new File(filePath).mkdir();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
            String originfile = filePath + params.username + "photo1." + fileType
            // String imagefile=filePath + params.studentCode + "photo2." + fileType
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
            studentInstance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))
            studentInstance.save(flush: true)
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
    //Edit窗口中把单张照片导入到Student表中的photo字段中,android

    @Transactional
    def saveStudentEdit() {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String query = "from Student where username='" + params.username + "'"// println("query====="+query)
        println("saveStudentEdit===query==="+query)
        Student studentInstance = Student.findAll(query)[0]
        params.birthDate=(params.birthDate) ? sdf.parse(params.birthDate)  : "null"
        params.enrollDate=(params.enrollDate) ? sdf.parse(params.enrollDate)  : "null"
        studentInstance.setProperties(params)
        /*studentInstance.setUsername(params.username);
        studentInstance.setTruename(params.truename);
        studentInstance.setEmail(params.email);
        studentInstance.setDepartment(params.department);
        studentInstance.setMajor(params.major);
        studentInstance.setCollege(params.college);
        studentInstance.setPhone(params.phone);
        studentInstance.setHomephone(params.homephone);
        studentInstance.setIdNumber(params.idNumber);
        studentInstance.setBirthDate((params.birthDate) ? sdf.parse(params.birthDate)  : "null");
        studentInstance.setSex(params.sex);
        studentInstance.setRace(params.race);
        studentInstance.setPoliticalStatus(params.politicalStatus);
        studentInstance.setEnrollDate((params.enrollDate) ? sdf.parse(params.enrollDate) : "null");
        studentInstance.setTreeId(params.treeId);
        studentInstance.setCurrentStatus(params.currentStatus);*/

       // studentInstance.save(flush:true)
        try {
            Student i
            i = studentInstance.save(flush: true)//返回当前保存的记录
            if (i != null) {
                println("success")
                // response.getWriter().write("success0");//也可以
                render "success"
                return
            } else {
                println("failure111")
                // response.getWriter().write("failure0");//也可以
                render "failure"
                return
            }
        } catch (e) {
            println("failure2222")
            render "failure"
            return
        }
        render "{success:true,info:'导入照片成功！'}"
        return
    }
  //上传照片android
   // @Transactional
    def savePhotoEdit(HttpServletRequest request) throws Exception {
        println("savePhotoEdit======params=========" + params);
        println("savePhotoEdit======params.username=========" + params.username);
        println("savePhotoEdit======params.file=========" + params.file);
        String uploadPath = getServletContext().getRealPath("/") + "photoD\\"
        MultipartFile file1 = params.file;
        String fileName = (file1.getOriginalFilename()).split("\\?")[0];
        System.out.println("000fileName = " + fileName)
        int split = fileName.lastIndexOf(".");
        System.out.println("1111fileName = " + fileName)
        String originfile = uploadPath +  "photo1.jpg"
        String imagefile = uploadPath +  "photo2.jpg"
        File file = new File(originfile)
        file1.transferTo(file)




        /*InputStream stream1 =  new FileInputStream(originfile)
        //方法一  上传到本地
        BufferedInputStream inputStream= new BufferedInputStream(stream1);//
        // 获得文件输入流
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(uploadPath + fileName)));// 获得文件输出流
        Streams.copy(inputStream, out, true);// 开始把文件写到你指定的上传文件夹
        */
        //方法二。。上传到图服务器得到二进制文件          //上传到图片服务器，已二进制文件上传。
       // BufferedInputStream stream= new FileInputStream(originfile)
        InputStream stream =  new FileInputStream(originfile)
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4099];
        int len = -1;
        while ((len = stream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] buff=outStream.toByteArray();//二进制文件都得到了，直接调用你们自己方法上传到图片服务器。

        String query = "from Student where username='" + params.username + "'"// println("query====="+query)
        println("importPhotoEdit===query==="+query)
        Student studentInstance = Student.findAll(query)[0]
        studentInstance.setPhoto(new javax.sql.rowset.serial.SerialBlob(buff))
        studentInstance.save(flush: true)
        println("ok！！！！")

        render "{success:true,info:'导入照片成功！'}"
        return






/*        Iterator<String> iterator = request.getFileNames();
        while (iterator.hasNext()) {
            MultipartFile file = request.getFile(iterator.next());
            String fileNames = file.getOriginalFilename();
            int split = fileNames.lastIndexOf(".");
            System.out.println("fileNames = " + fileNames);
        }
        println("iter.hasNext()===0000")
*/

      /*  try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iter = upload.getItemIterator(request);
            println("iter.hasNext()===1111")
            while (iter.hasNext()) {
                println("iter.hasNext()===2222")
                FileItemStream item = iter.next();
                String fileName = item.getName();
                println("savePhotoEdit======fileName=========" + fileName);
                InputStream stream = item.openStream();
               //方法一  上传到本地
                BufferedInputStream inputStream= new BufferedInputStream(stream);//
                // 获得文件输入流
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(uploadPath + fileName)));// 获得文件输出流
                Streams.copy(inputStream, out, true);// 开始把文件写到你指定的上传文件夹


                //方法二。。上传到图服务器得到二进制文件          //上传到图片服务器，已二进制文件上传。
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[4099];
                int len = -1;
                while ((len = stream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                byte[] buff=outStream.toByteArray();//二进制文件都得到了，直接调用你们自己方法上传到图片服务器。

                String query = "from Student where username='" + params.username + "'"// println("query====="+query)
                println("importPhotoEdit===query==="+query)
                Student studentInstance = Student.findAll(query)[0]
                studentInstance.setPhoto(new javax.sql.rowset.serial.SerialBlob(buff))
                studentInstance.save(flush: true)
                println("ok！！！！")
            }
        } catch (Exception e) {
            println("Exception！！")
        }*/

        render "{success:true,info:'导入照片成功！'}"
        return














       /* StandardMultipartHttpServletRequest httpServletRequest = new StandardMultipartHttpServletRequest();
        httpServletRequest=params.file1
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        System.out.println("-------------------ParameterNames------------------");
        while(parameterNames.hasMoreElements()){
            String key = parameterNames.nextElement();
            String value = httpServletRequest.getParameter(key);
            System.out.println("key = " + key);
            System.out.println("value = " + value);
        }
        System.out.println("-------------------AttributeNames------------------");
        Enumeration<String> attributeNames = httpServletRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()){
            String key = attributeNames.nextElement();
            System.out.println("key = " + key);
        }
        System.out.println("-------------------HeaderNames------------------");
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = httpServletRequest.getHeader(key);
            System.out.println(String.format("key: %s, value:%s",key,value));
        }
        System.out.println("-------------------FileNames------------------");
        Iterator<String> iterator = httpServletRequest.getFileNames();
        while (iterator.hasNext()) {
            MultipartFile file = httpServletRequest.getFile(iterator.next());
            String fileNames = file.getOriginalFilename();
            int split = fileNames.lastIndexOf(".");
            System.out.println("fileNames = " + fileNames);
        }*/

        println("ok")
        render "{success:true,info:'导入照片成功！'}"
        return



        /*int read = 0;
        byte[] bytes = new byte[4096];
        while ((read = inputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }*/
        /*String query = "from Student where username='" + params.username + "'"// println("query====="+query)
        println("importPhotoEdit===query==="+query)

        Student studentInstance = Student.findAll(query)[0]
        studentInstance.setPhoto(new javax.sql.rowset.serial.SerialBlob(inputStream))
        studentInstance.save(flush: true)
        */
        render "{success:true,info:'导入照片成功！'}"
        return
    }

    //Edit窗口中把单张照片导入到Student表中的photo字段中
    @Transactional
    def importPhotoEdit() {
        //println("导入Student照片importPhoto======params.studentCode=========" + params.studentCode);
        String query = "from Student where username='" + params.username + "'"// println("query====="+query)
        // println("importPhotoEdit===query==="+query)
        Student studentInstance = Student.findAll(query)[0]
        if (!studentInstance) {
            studentInstance = new Student()
        }
        def f = request.getFile('studentPhoto1')//f.size上传文件的大小，如是File f 对象则f.length()是文件的大小
        def fileName
        def filePath
        //if (!f.empty) {
        if (f && !f.empty) {
            if (f.size > 8192000) {
                render "{success:false,info:'错误！导入Student照片大小超过8M的限制！'}" //导入后文件被裁剪大约小于10
                return
            }
            fileName = f.getOriginalFilename()
            String[] file1 = ((fileName.toLowerCase()).toString()).split("\\.")
            String fileType = file1[file1.length - 1]  //取文件名的扩展名（不含.）
            if (fileType != 'jpg' && fileType != 'png' && fileType != 'bmp') {
                render "{success:false,info:'错误！导入照片格式不对！'}" //render "failure"//导入的Excel文件不存在
                return
            }
            filePath = getServletContext().getRealPath("/") + "photoD\\"
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
            studentInstance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))
            studentInstance.save(flush: true)
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

    //批量上传照片文件，然后导入Student表中，【调试阶段暂时不删除批量上传的照片文件】
    @Transactional
    def uploadFile() {
        // println("uploadFile=====params.departmentDetail===="+params.departmentDetail)
        try {
            String uploadPath = getServletContext().getRealPath("/") + "photoD\\" + params.departmentDetail + "\\"
            // println("uploadFile=====uploadPath===="+uploadPath)
            File up = new File(uploadPath);
            if (!up.exists()) {
                up.mkdir();
            }
            def f = request.getFile('file')
            // println("uploadFile=====f.getOriginalFilename()===="+f.getOriginalFilename());
            String originfile = uploadPath + params.name
            //String imagefile=uploadPath + params.studentCode + "photo2.jpg"
            File file = new File(originfile)//file.length()是文件的大小
            f.transferTo(file)//上传的文件先存入临时文件//f.size上传文件的大小
            String str = params.name
            String studentCode = "", temp = "";
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    // studentCode+=str.charAt(i);
                    studentCode = (str.substring(i)).split("\\.")[0];//取照片名字后面的身份证号码
                    break;
                }
            }
            String query = "from Student where studentCode='" + studentCode + "'"// println("query====="+query)
            Student studentInstance = Student.findAll(query)[0]
            if (!studentInstance) {
                return "success";//有照片，没有Student信息
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
            studentInstance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))//照片导入Student表中
            studentInstance.save(flush: true)
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
        String filePath = getServletContext().getRealPath("/") + "photoD\\"
        String imagefile = filePath + params.studentCode + "photo2.jpg"
        //println("imagefile==="+imagefile)
        String query
        //是管理者或是自己的项目,如已上传直接显示，否则必须是完成审核通过的文件才能显示。

        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = null
        BufferedImage studentImage;
        String fileType

        // if (studentInstance&&studentInstance.getPhoto()) {
        try {
            //println("有照片")
            //inputStream =new InputStream(imagefile)
            //从数据库中读出JPG文件以JPG文件显示
            outputStream = response.getOutputStream();
            studentImage = ImageIO.read(new FileImageInputStream(new File(imagefile)));
            ImageIO.write(studentImage, "JPEG", response.getOutputStream());
            outputStream.flush()
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            // println("无照片")
            //还没有上传文件
            //默认显示一个文件（未知名.png）
            // if(studentInstance&&studentInstance.getSex()=="男"){
            //      inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\" + "male.gif"));
            //  }else{
            inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\" + "female.gif"));
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
        //println("从数据库调出职工的照片照片displayStudentImage======params.studentCode=========" + params.studentCode);
         println("displayPhotoparams.username========" +params.username);
        // println("登录用户usernamechineseAuthority========" + chineseAuthority);
        String query
        //是管理者或是自己的项目,如已上传直接显示，否则必须是完成审核通过的文件才能显示。
        query = "from Student where username='" + params.username + "'"
        println("displayPhoto   query====="+query)
        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = null
        BufferedImage studentImage;
        String fileType
        Student studentInstance = Student.findAll(query)[0]
        try {
            println("有照片")
            Blob blob = studentInstance.getPhoto();
            inputStream = blob.getBinaryStream();
            //从数据库中读出JPG文件以JPG文件显示
            outputStream = response.getOutputStream();
            studentImage = ImageIO.read(inputStream);
            ImageIO.write(studentImage, "JPEG", response.getOutputStream());
            outputStream.flush()
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
             println("无照片sex====="+studentInstance.getSex())
            //还没有上传文件
            //默认显示一个文件（未知名.png）
            if (studentInstance && studentInstance.getSex() == "男") {
                inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\" + "male.gif"));
            } else {
                inputStream = new FileInputStream(new File(getServletContext().getRealPath("/") + "photoD\\" + "female.gif"));
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

//---------------------------------------------------------------------------------------------------
    //从Excel中导入职工信息时，同时导入已经上传到时服务器\photoD\单位简称\姓名+身份证.jpg的照片文件// 上传本职工的照片且自动处理为3-10k大小
    def importBatchPhoto(String name, String studentCode, String department) {
        //  println("上传本单位职工的照片importBatchPhoto")
        String sfile = name + studentCode
        String query = "from Student where studentCode='" + studentCode + "'"// println("query====="+query)
        Student studentInstance = Student.findAll(query)[0]
        if (!studentInstance) {
            studentInstance = new Student()
        }
        // println("0000000000上传本单位职工的照片importBatchPhoto")
        try {
            def fileName = sfile + ".jpg"
            def filePath = getServletContext().getRealPath("/") + "photoD\\" + department + "\\"
            //临时上传照片的位置，存入数据库后，就删除照片          //println("filePath======================="+filePath)
            // println("照片文件==========="+filePath + sfile + ".jpg")
            String imagefile = filePath + "bak" + sfile + ".jpg"
            byte[] data = null;
            FileImageInputStream input = null;
            InputStream input1 = new FileInputStream(filePath + sfile + ".jpg");
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
            studentInstance.setPhoto(new javax.sql.rowset.serial.SerialBlob(data))
            //  println("1111222上传本单位职工的照片importBatchPhoto")
            studentInstance.save(flush: true)
            // studentInstance.save()
            //   println("2222222上传本单位职工的照片importBatchPhoto")
            File filep = new File(imagefile);//删除临时文件
            filep.delete();
            //println("导入照片成功！")
            // render "{success:true,info:'导入照片成功！'}"
            return
        } catch (FileNotFoundException ex1) {
            // ex1.printStackTrace();
            // println("导入照片失败，照片不存在！")
            // render "{success:false,info:'错误！导入照片不存在！'}" //render "failure"//导入的Excel文件不存在
            return
        }
        catch (IOException ex1) {
            // ex1.printStackTrace();
            // println("导入照片失败，照片不存在！")
            // render "{success:false,info:'错误！导入照片不存在！'}" //render "failure"//导入的Excel文件不存在
            return
        }
        //  println("导入照片成功！")
        // render "{success:true,info:'导入照片成功！'}"
        //  return
        /*int temp;
        InputStream iss
        OutputStream oss
        try {
            if (!(new File(filePath).isDirectory())) {
                new File(filePath).mkdir();
            }
            iss = new FileInputStream("D:/salarydata/" + department + "/" + sfile + ".jpg");
            oss = new FileOutputStream(filePath + sfile + ".jpg");
            resizeImage(iss, oss, 170, "jpg")
            //File filep = new File(filePath + "photo"+ sfile);
            // filep.delete();
            // while((temp=iss.read())!=(-1)){oss.write(temp);}//iss 写入oss
            // oss.flush();oss.close();iss.close();
        } catch (FileNotFoundException e) {
            println("本单位的" + sfile + ".jpg 照片文件不存在！")
            // e.printStackTrace();
        }*/
    }


//----------------------------------------------------------------------------------------------------
    //将extjs页面传来的分页、排序等参数转换为数据库所需要的相关参数
    def toparams(params) {
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
    def json(list) {
        SimpleDateFormat f = new java.text.SimpleDateFormat("yyyy-MM-dd")
        List<?> students = new ArrayList();
        //从定义的类中取出有效字段数n
        def list1 = Student.getDeclaredFields()
        int n = 0
        for (int i = 0; i < list1.size(); i++) if (list1[i].getName() != "constraints") {
            n++
        } else {
            break
        }
        //从定义的类中取出有效字段名
        String[] fieldName = new String[n]
        String[] fieldType = new String[n]
        for (int i = 0; i < n; i++) {
            fieldName[i] = list1[i].getName();
            fieldType[i] = list1[i].getType()
            //println("fieldName["+i+"]===="+fieldName[i]+"--------fieldType["+i+"]===="+fieldType[i]+"====fieldType[i]==java.util.Date===="+(list1[i].getType()==java.util.Date))            //println("fieldName["+i+"]===="+fieldName[i]+"--------fieldType["+i+"]===="+fieldType[i]+"====fieldType[i]==java.util.Date===="+(fieldType[i]=="class java.util.Date"))
        }
        //从数据库中取出数据生成返回的ArrayList数据，以json格式返回
        for (int j = 0; j < (list.size() - 1); j++) {
            Map<String, String> map = new HashMap<>()
            map.put("id", list[j].id)
            for (int i = 0; i < n - 1; i++) {
                if (fieldType[i] == "class java.util.Date") {
                    def t = list[j].(fieldName[i])
                    map.put(fieldName[i], t ? f.format(t) : "")
                } else {
                    map.put(fieldName[i], list[j].(fieldName[i]))
                }
            }
            if (fieldType[n - 1] == "class java.util.Date") {
                def t = list[j].(fieldName[n - 1])
                map.put(fieldName[n - 1], t ? f.format(t) : "")
            } else {
                map.put(fieldName[n - 1], list[j].(fieldName[n - 1]))
            }

            students.add(map)
        }
        Map<String, String> map = new HashMap<>()
        map.put("id", list[list.size() - 1].id)
        for (int i = 0; i < n - 1; i++) {
            if (fieldType[i] == "class java.util.Date") {
                def t = list[list.size() - 1].(fieldName[i])
                map.put(fieldName[i], t ? f.format(t) : "")
            } else {
                map.put(fieldName[i], list[list.size() - 1].(fieldName[i]))
            }
        }
        if (fieldType[n - 1] == "class java.util.Date") {
            def t = list[list.size() - 1].(fieldName[n - 1])
            map.put(fieldName[n - 1], t ? f.format(t) : "")
        } else {
            map.put(fieldName[n - 1], list[list.size() - 1].(fieldName[n - 1]))
        }
        students.add(map)
        return students
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

    //--------------------------------------------------------------------------------------
    //模板练习
    def template() {
        def binding = [
                firstname: "Grace",
                lastname : "Hopper",
                accepted : true,
                title    : 'Groovy for COBOL programmers'
        ]
        binding.put("name", "lulisong")
        String entityName = "com.app.Student"
        Class entityClass
        try {
            entityClass = grailsApplication.getClassForName(entityName)
        } catch (Exception e) {
            throw new RuntimeException("Failed to load class with name '${entityName}'", e)
        }
        def s = entityClass.getDeclaredFields()
        binding.put("s", s)
        for (int i; i < s.size(); i++) {
            if (!(s[i].name == "constraints")) {
                // println "------Property '${s[i].name}' is of type '${s[i].type}'"
            } else {
                break
            }
        }
        entityClass.metaClass.getProperties().each() {
            // println "Property '${it.name}' is of type '${it.type}'"
        }

        def engine = new groovy.text.SimpleTemplateEngine()
        def text = '''\
 Dear <%= firstname %> $lastname,
<%=name%>
name====${name}
 We <% if (accepted) print 'are pleased' else print 'regret' %> \
 to inform you that your paper entitled
 '$title' was ${ accepted ? 'accepted' : 'rejected' }.
<%
 for(int i;i<s.size();i++){
        if(!(s[i].name=="constraints")){
         print s[i].name+"---"
        }else{
            break
        }
    }
%>




 The conference committee.
 '''
        def template = engine.createTemplate(text).make(binding)
        println template.toString()
        render "success"
        return

    }

    def json1() {
        def studentClass = grailsApplication.getArtefact("Domain", "com.app.Student")
        def cps = studentClass.getConstrainedProperties()
        def properties = [:]
        studentClass.getProperties().each {
            println("it====" + it)
        }
        cps.getProperties().each {
            println("------it====" + it)
        }


        def results = Student.list()
        render(contentType: "application/json") {
            students(results) { Student b ->
                username b.username
                truename b.truename
            }
        }
    }

    def json2() {
        String entityName = "com.app.Student"
        Class entityClass
        try {
            entityClass = grailsApplication.getClassForName(entityName)
        } catch (Exception e) {
            throw new RuntimeException("Failed to load class with name '${entityName}'", e)
        }
        def s = entityClass.getDeclaredFields()
        for (int i; i < s.size(); i++) {
            if (!(s[i].name == "constraints")) {
                println "------Property '${s[i].name}' is of type '${s[i].type}'"
            } else {
                break
            }
        }
        entityClass.metaClass.getProperties().each() {
            println "Property '${it.name}' is of type '${it.type}'"
        }

        def results = Student.list()
        render(contentType: "application/json") {
            students(results) { Student b ->
                username b.username
                truename b.truename
            }
        }
    }

    def json3() {
        def results = Student.list()
        render(contentType: "application/json") {
            students(results) { Student b ->
                username b.username
                truename b.truename
            }
        }
    }


}
