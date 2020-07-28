package com.app

import com.jacob.activeX.ActiveXComponent
import com.jacob.com.ComThread
import com.jacob.com.Dispatch
import com.jacob.com.Variant
import com.user.Department
import com.user.User
import grails.gorm.transactions.Transactional
import groovy.sql.Sql


//这是自动生成的controller

import org.activiti.bpmn.model.BpmnModel
import org.activiti.bpmn.model.FlowNode
import org.activiti.bpmn.model.SequenceFlow
import org.activiti.engine.*
import org.activiti.engine.history.HistoricActivityInstance
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity
import org.activiti.engine.repository.ProcessDefinition
import org.activiti.engine.task.Attachment
import org.activiti.engine.task.Comment
import org.activiti.engine.task.Task
import org.activiti.image.ProcessDiagramGenerator
import org.activiti.image.impl.DefaultProcessDiagramGenerator
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang.StringUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.util.FileCopyUtils

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.text.SimpleDateFormat

@Transactional(readOnly = false)
class ProcessTaskController {
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    //static String dep_tree_id = ""//点击树状菜单上的某个单位的tree_id

    def ProcessService
    def springSecurityService
    //def grailsApplication //注入是为了直接执行原始的SQL语句def dataSource = grailsApplication.mainContext.getBean('dataSource')

//查看某个任务的流程图
    def findProcessPic1() throws Exception {
        String procDefId = params.procDefId
        //println("procDefId==========" + procDefId)
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();

        String diagramResourceName = procDef.getDiagramResourceName();
        InputStream imageStream = repositoryService.getResourceAsStream(procDef.getDeploymentId(), diagramResourceName);

        def filePath = getServletContext().getRealPath("/") + "/processDiagram/"
        FileOutputStream out = new FileOutputStream(filePath + "watch.png");
        FileCopyUtils.copy(imageStream, out);
        render "true"
        return
    }

    //审批即完成任务
    def complete() throws Exception {
        String taskId = params.taskId
        //println("taskId=====================" + taskId)
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService()
        taskService.complete(taskId)
        render "true"
        return
    }



    //删除流程图（通过executionId）
    def deleteProcessPicByExecutionId() throws Exception {
        // println("删除流程图（通过executionId）"+params.executionId)
        String executionId = params.executionId
        def fileName = getServletContext().getRealPath("/") + "/processDiagram/" + executionId + "_watch.png"
        File file = new File(fileName)
        file.delete()
    }

    //取流程图=>流程（我的业务=>养老保险缴费基数申报（查养老保险缴费基数申报审核情况））
    def findProcessPicByYlbxjfjs() throws Exception {
        String procDefId = "", procInstId = "", executionId = "", taskId = "", procName = "", dep_tree_id = ""
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        dep_tree_id = params.dep_tree_id
        procName = "养老保险缴费基数申报流程"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar ylbxjfjsSalaryDate = Calendar.getInstance();
        ylbxjfjsSalaryDate.setTime(sdf.parse(params.currentSalaryDate + " 00:00:00"));
        int ylbxjfjsSalaryDateYear = ylbxjfjsSalaryDate.get(Calendar.YEAR);//获取年份
        String query = "SELECT * FROM  act_re_procdef where NAME_='" + procName + "' order by DEPLOYMENT_ID_ desc"//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + dep_tree_id+ylbxjfjsSalaryDateYear + "%'"//提取流程名称
        String queryHistory = "SELECT * FROM  act_hi_actinst where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + dep_tree_id+ylbxjfjsSalaryDateYear + "%' and (END_TIME_ IS NOT NULL) order by END_TIME_ DESC "//提取流程名称
        def listw
        try {
            listw=sql.rows(query)
            if(listw.size()>0){
                procInstId = listw[0].PROC_INST_ID_
                executionId = listw[0].EXECUTION_ID_
                taskId =listw[0].ID_
            }else{
                listw=sql.rows(queryHistory)
                procInstId = listw[0].PROC_INST_ID_
                executionId = listw[0].EXECUTION_ID_
                taskId =listw[0].ID_
            }

        } catch (e) {
            render "failure"
            return
        }

        //String procDefId = params.procDefId//流程定义ID
        //String executionId = params.executionId//
        //String procInstId = params.procInstId//流程实例ID
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        HistoryService historyService = processEngine.getHistoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        //获取历史流程实例x
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (historicProcessInstance != null) {
            // 获取流程定义
            // ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();//也可以
            // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(procInstId).orderByHistoricActivityInstanceId().asc().list();
            // 已执行的节点ID集合
            List<String> executedActivityIdList = new ArrayList<String>();
            int index = 1;            //logger.info("获取已经执行的节点ID");
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                executedActivityIdList.add(activityInstance.getActivityId());
                //logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
                index++;
            }
            //BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

            BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);//也可以

            // 已执行的线集合
            List<String> flowIds = new ArrayList<String>();
            // 获取流程走过的线 (getHighLightedFlows是下面的方法)
            flowIds = getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);
            /* println("流程图===线=====flowIds.size()=="+flowIds.size())
         println("流程图===线=====0=="+flowIds[0])
         println("流程图===线=====1=="+flowIds[1])
         println("流程图===线=====2=="+flowIds[2])
         println("流程图===线=====3=="+flowIds[3])
         println("流程图===线===8==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0]).size())
         println("流程图===线===8==size[0]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0])[0].getX())
         println("流程图===线===8==size[0]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0])[0].getY())
         println("流程图===线===8==size[1]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[1])[0].getX())
         println("流程图===线===8==size[1]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[1])[0].getY())
         println("流程图===线===9==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[2]).size())
         println("流程图===线===10==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[3]).size())*/
            //为获取在流程图上出现动态提示审核信息
            ArrayList list = new ArrayList<>();
            HashMap<String,String> list0 =new HashMap<String,String>()
            /*println("流程图===节点=====executedActivityIdList.size()=="+executedActivityIdList.size())
            println("流程图===节点0=====0=="+executedActivityIdList[0])
            println("流程图===节点0=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getX())
            println("流程图===节点0=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getY())
            println("流程图===节点0=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getHeight())
            println("流程图===节点0=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getWidth())*/
            list0.put("node",executedActivityIdList[0])
            list0.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getX())
            list0.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getY())
            list0.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getHeight())
            list0.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getWidth())
            list.add(list0)
            if(executedActivityIdList[1]){
                /*println("流程图===节点1=====1=="+executedActivityIdList[1])
                println("流程图===节点1=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getX())
                println("流程图===节点1=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getY())
                println("流程图===节点1=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getHeight())
               println("流程图===节点1=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getWidth())*/
                HashMap<String,String> list1 =new HashMap<String,String>()
                list1.put("node",executedActivityIdList[1])
                list1.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getX())
                list1.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getY())
                list1.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getHeight())
                list1.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getWidth())
                list.add(list1)
            }
            if(executedActivityIdList[2]){
                /*println("流程图===节点2=====2=="+executedActivityIdList[2])
                println("流程图===节点2=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getX())
                println("流程图===节点2=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getY())
                println("流程图===节点2=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getHeight())
                println("流程图===节点2=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getWidth())*/
                HashMap<String,String> list2 =new HashMap<String,String>()
                list2.put("node",executedActivityIdList[3])
                list2.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getX())
                list2.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getY())
                list2.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getHeight())
                list2.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getWidth())
                list.add(list2)
            }
            if(executedActivityIdList[3]){
                /*println("流程图===节点3=====3=="+executedActivityIdList[3])
                println("流程图===节点3=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getX())
                println("流程图===节点3=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getY())
                println("流程图===节点3=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getHeight())
                println("流程图===节点3=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getWidth())*/
                HashMap<String,String> list3 =new HashMap<String,String>()
                list3.put("node",executedActivityIdList[3])
                list3.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getX())
                list3.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getY())
                list3.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getHeight())
                list3.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getWidth())
                list.add(list3)
            }

            if(executedActivityIdList[4]) {
               // println("流程图===节点4=====4==" + executedActivityIdList[4])
                /*println("流程图===节点4=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getX())
                println("流程图===节点4=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getY())
                println("流程图===节点4=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getHeight())
                println("流程图===节点4=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getWidth())*/
                HashMap<String, String> list4 = new HashMap<String, String>()
                list4.put("node", executedActivityIdList[4])
                list4.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getX())
                list4.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getY())
                list4.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getHeight())
                list4.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getWidth())
                list.add(list4)
            }
            if(executedActivityIdList[5]) {
                //println("流程图===节点5=====5=="+executedActivityIdList[5])
                HashMap<String, String> list5 = new HashMap<String, String>()
                list5.put("node", executedActivityIdList[5])
                list5.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getX())
                list5.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getY())
                list5.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getHeight())
                list5.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getWidth())
                list.add(list5)
            }
            if(executedActivityIdList[6]) {
                HashMap<String, String> list6 = new HashMap<String, String>()
                list6.put("node", executedActivityIdList[6])
                list6.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getX())
                list6.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getY())
                list6.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getHeight())
                list6.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getWidth())
                list.add(list6)
            }
            if(executedActivityIdList[7]) {
                HashMap<String,String> list7 =new HashMap<String,String>()
                list7.put("node",executedActivityIdList[7])
                list7.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getX())
                list7.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getY())
                list7.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getHeight())
                list7.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getWidth())
                list.add(list7)
            }
            // 获取流程图图像字符流
            ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            //流程图图像显示方式一，已完成任务（包括线为红线）和当前执行任务为红框
            InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);
            //流程图图像显示方式二，已完成任务和当前执行任务为红框
            //InputStream imageStream = pec.generateDiagram(bpmnModel,"png", executedActivityIdList, new ArrayList(),"宋体","微软雅黑","黑体",null,2.0);//无高亮线，无字体设置则汉字显示有问题
            //流程图图像显示方式三，当前执行任务为红框
            //InputStream imageStream = pec.generateDiagram(bpmnModel,"png", runtimeService.getActiveActivityIds(procInstId), new ArrayList(),"宋体","微软雅黑","黑体",null,2.0);
            def filePath = getServletContext().getRealPath("/") + "/processDiagram/" + executionId
            FileOutputStream out = new FileOutputStream(filePath + "_watch.png");
            FileCopyUtils.copy(imageStream, out);
            //render "true"
            // println("findProcessPicByRydwbdEmployeeCode=====success:true,procDefId:'"+procDefId+"',procInstId:'"+procInstId+"',executionId:'"+executionId+"',taskId:'"+taskId+"'")
            render(contentType: "text/json") {
                success true
                procDefIds procDefId
                procInstIds procInstId
                executionIds executionId
                taskIds taskId
                nodes  list.collect(){
                    [
                            node: it.node,
                            x   : it.x,
                            y   : it.y,
                            h   : it.h,
                            w   : it.w,
                    ]
                }
                totalCount list.size()
            }
            //render "{success:true,procDefId:'" + procDefId + "',procInstId:'" + procInstId + "',executionId:'" + executionId + "',taskId:'" + taskId + "'}"
            return
        } else {
            //无已执行任务
            render "{success:true,procDefIds:'" + procDefId + "',procInstIds:'" + procInstId + "',executionIds:'" + executionId + "',taskIds:'" + taskId + "'}"
            return
        }
    }

    //删除【养老保险缴费基数申报】指定的任务所在的流程实例，删除已生成的流程图文件 processDiagram/' + executionId + '_watch.png
    @Transactional
    def deleteTaskYlbxjfjs() {
       // println("彻底删除当前任务!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        //  println("params.userRole====" + params.userRole)
        String query
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        // println("第二歩：清除该流程实例【"+params.procInstId+"】事务在当前任务中的信息")
        // println("taskId===========" + params.procInstId)
        /*if (params.userRole != "管理员") {
            println("1111params.userRole====" + params.userRole)
            ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
            //TaskService taskService = processEngine.getTaskService();
            RuntimeService runtimeService = processEngine.getRuntimeService()
            runtimeService.deleteProcessInstance(params.procInstId, "原因：不同意！")
            //删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
            //taskService.deleteTask(params.id,"原因：不同意！")
            //删除已生成的流程图文件 processDiagram/' + executionId + '_watch.png
            //  println("成功=======>删除【工资基金计划申报流程】指定的任务所在的流程实例" )
        } else {
        */
        //  println("管理员彻底删除任务")
      //  println("删除历史任务中相应的任务params.procInstId========" + params.procInstId)
        try {
           // println("如有当前任务则删除当前任务act_ru_task")
            query = "delete from act_ru_task where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
            //println("如有当前任务则删除当前任务act_ru_execution")
            query = "delete from act_ru_execution where PROC_INST_ID_='" + params.procInstId + "'"////这个有关联，无法删除！！！
            sql.execute(query)
        } catch (e) {}
        try {
           // println("如有当前任务则删除当前任务act_ru_identitylink")
            query = "delete from act_ru_identitylink where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
           // println("如有当前任务则删除当前任务act_ru_variable")
            query = "delete from act_ru_variable where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
            //println("删除历史任务act_hi_taskinst")
            query = "delete from act_hi_taskinst where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
           // println("删除历史任务act_hi_actinst")
            query = "delete from act_hi_actinst where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
           // println("删除历史任务act_hi_attachment")
            query = "delete from act_hi_attachment where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
           // println("删除历史任务act_hi_comment")
            query = "delete from act_hi_comment where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
           /// println("删除历史任务act_hi_detail")
            query = "delete from act_hi_detail where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
            //println("删除历史任务act_hi_identitylink")
            query = "delete from act_hi_identitylink where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
           /// println("删除历史任务act_hi_procinst")
            query = "delete from act_hi_procinst where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        try {
           // println("删除历史任务act_hi_varinst")
            query = "delete from act_hi_varinst where PROC_INST_ID_='" + params.procInstId + "'"
            sql.execute(query)
        } catch (e) {}
        /*}*/
        render "success"
        return
    }

    // //我的业务=>人员单位变动申报保存（查看申报人员单位变动审核情况）
    //获取 procDefId、executionId、procInstId、taskId。为取附件一调令扫描图片=>人员单位变动（通过EmployeeCode）
    def findProcessPicByRydwbdEmployeeCode() throws Exception {
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String employeeCode = params.employeeCode
      //  println("查看申报人员单位变动审核情况employeeCode==="+employeeCode)
        String procDefId = "", procInstId = "", executionId = "", taskId = ""
        String query = "SELECT * FROM  act_re_procdef where NAME_='人员单位变动申报流程' order by DEPLOYMENT_ID_ desc"//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + employeeCode + "%'"//提取流程名称
        String queryHistory = "SELECT * FROM  act_hi_actinst where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + employeeCode + "%' and (END_TIME_ IS NOT NULL) order by END_TIME_ DESC "//提取流程名称
        def listw
        try {
            listw=sql.rows(query)
            if(listw.size()>0){
                procInstId = listw[0].PROC_INST_ID_
                executionId = listw[0].EXECUTION_ID_
                taskId =listw[0].ID_
            }else{
                listw=sql.rows(queryHistory)
                procInstId = listw[0].PROC_INST_ID_
                executionId = listw[0].EXECUTION_ID_
                taskId =listw[0].ID_
            }

        } catch (e) {
            render "{success:true,procDefId:'" + procDefId + "',procInstId:'" + procInstId + "',executionId:'" + executionId + "',taskId:'" + taskId + "'}"
            return
        }






      /*  println("查看申报人员单位变动审核情况query==="+query)
        try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
            executionId = sql.rows(query)[0].EXECUTION_ID_
            taskId = sql.rows(query)[0].ID_
        } catch (e) {
            // render "failure"
            render "{success:true,procDefId:'" + procDefId + "',procInstId:'" + procInstId + "',executionId:'" + executionId + "',taskId:'" + taskId + "'}"
            return
        }*/
      //  println("000000查养老保险缴费基数申报审核情况findProcessPicByRydwbdEmployeeCode=====success:true,procDefId:'"+procDefId+"',procInstId:'"+procInstId+"',executionId:'"+executionId+"',taskId:'"+taskId+"'")

        //String procDefId = params.procDefId//流程定义ID
        //String executionId = params.executionId//
        //String procInstId = params.procInstId//流程实例ID
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        HistoryService historyService = processEngine.getHistoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        //获取历史流程实例x
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (historicProcessInstance != null) {
            // 获取流程定义
            // ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();//也可以
            // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(procInstId).orderByHistoricActivityInstanceId().asc().list();
            // 已执行的节点ID集合
            List<String> executedActivityIdList = new ArrayList<String>();
            int index = 1;            //logger.info("获取已经执行的节点ID");
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                executedActivityIdList.add(activityInstance.getActivityId());
                //logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
                index++;
            }
            //BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

            BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);//也可以
            // 已执行的线集合
            List<String> flowIds = new ArrayList<String>();
            // 获取流程走过的线 (getHighLightedFlows是下面的方法)
            flowIds = getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);
            /* println("流程图===线=====flowIds.size()=="+flowIds.size())
          println("流程图===线=====0=="+flowIds[0])
          println("流程图===线=====1=="+flowIds[1])
          println("流程图===线=====2=="+flowIds[2])
          println("流程图===线=====3=="+flowIds[3])
          println("流程图===线===8==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0]).size())
          println("流程图===线===8==size[0]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0])[0].getX())
          println("流程图===线===8==size[0]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0])[0].getY())
          println("流程图===线===8==size[1]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[1])[0].getX())
          println("流程图===线===8==size[1]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[1])[0].getY())
          println("流程图===线===9==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[2]).size())
          println("流程图===线===10==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[3]).size())*/
            //为获取在流程图上出现动态提示审核信息
            ArrayList list = new ArrayList<>();
            HashMap<String,String> list0 =new HashMap<String,String>()
            /*println("流程图===节点=====executedActivityIdList.size()=="+executedActivityIdList.size())
            println("流程图===节点0=====0=="+executedActivityIdList[0])
            println("流程图===节点0=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getX())
            println("流程图===节点0=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getY())
            println("流程图===节点0=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getHeight())
            println("流程图===节点0=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getWidth())*/
            list0.put("node",executedActivityIdList[0])
            list0.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getX())
            list0.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getY())
            list0.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getHeight())
            list0.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getWidth())
            list.add(list0)
            if(executedActivityIdList[1]){
                /*println("流程图===节点1=====1=="+executedActivityIdList[1])
                println("流程图===节点1=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getX())
                println("流程图===节点1=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getY())
                println("流程图===节点1=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getHeight())
               println("流程图===节点1=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getWidth())*/
                HashMap<String,String> list1 =new HashMap<String,String>()
                list1.put("node",executedActivityIdList[1])
                list1.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getX())
                list1.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getY())
                list1.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getHeight())
                list1.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getWidth())
                list.add(list1)
            }
            if(executedActivityIdList[2]){
                /*println("流程图===节点2=====2=="+executedActivityIdList[2])
                println("流程图===节点2=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getX())
                println("流程图===节点2=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getY())
                println("流程图===节点2=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getHeight())
                println("流程图===节点2=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getWidth())*/
                HashMap<String,String> list2 =new HashMap<String,String>()
                list2.put("node",executedActivityIdList[3])
                list2.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getX())
                list2.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getY())
                list2.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getHeight())
                list2.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getWidth())
                list.add(list2)
            }
            if(executedActivityIdList[3]){
                /*println("流程图===节点3=====3=="+executedActivityIdList[3])
                println("流程图===节点3=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getX())
                println("流程图===节点3=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getY())
                println("流程图===节点3=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getHeight())
                println("流程图===节点3=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getWidth())*/
                HashMap<String,String> list3 =new HashMap<String,String>()
                list3.put("node",executedActivityIdList[3])
                list3.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getX())
                list3.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getY())
                list3.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getHeight())
                list3.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getWidth())
                list.add(list3)
            }

            if(executedActivityIdList[4]) {
               // println("流程图===节点4=====4==" + executedActivityIdList[4])
                /*println("流程图===节点4=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getX())
                println("流程图===节点4=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getY())
                println("流程图===节点4=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getHeight())
                println("流程图===节点4=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getWidth())*/
                HashMap<String, String> list4 = new HashMap<String, String>()
                list4.put("node", executedActivityIdList[4])
                list4.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getX())
                list4.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getY())
                list4.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getHeight())
                list4.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getWidth())
                list.add(list4)
            }
            if(executedActivityIdList[5]) {
                //println("流程图===节点5=====5=="+executedActivityIdList[5])
                HashMap<String, String> list5 = new HashMap<String, String>()
                list5.put("node", executedActivityIdList[5])
                list5.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getX())
                list5.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getY())
                list5.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getHeight())
                list5.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getWidth())
                list.add(list5)
            }
            if(executedActivityIdList[6]) {
                HashMap<String, String> list6 = new HashMap<String, String>()
                list6.put("node", executedActivityIdList[6])
                list6.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getX())
                list6.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getY())
                list6.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getHeight())
                list6.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getWidth())
                list.add(list6)
            }
            if(executedActivityIdList[7]) {
                HashMap<String,String> list7 =new HashMap<String,String>()
                list7.put("node",executedActivityIdList[7])
                list7.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getX())
                list7.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getY())
                list7.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getHeight())
                list7.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getWidth())
                list.add(list7)
            }

            // 获取流程图图像字符流
            ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            //流程图图像显示方式一，已完成任务（包括线为红线）和当前执行任务为红框
            InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);
            //流程图图像显示方式二，已完成任务和当前执行任务为红框
            //InputStream imageStream = pec.generateDiagram(bpmnModel,"png", executedActivityIdList, new ArrayList(),"宋体","微软雅黑","黑体",null,2.0);//无高亮线，无字体设置则汉字显示有问题
            //流程图图像显示方式三，当前执行任务为红框
            //InputStream imageStream = pec.generateDiagram(bpmnModel,"png", runtimeService.getActiveActivityIds(procInstId), new ArrayList(),"宋体","微软雅黑","黑体",null,2.0);
            def filePath = getServletContext().getRealPath("/") + "/processDiagram/" + executionId
            FileOutputStream out = new FileOutputStream(filePath + "_watch.png");
            FileCopyUtils.copy(imageStream, out);
            //render "true"
          //  println("查养老保险缴费基数申报审核情况findProcessPicByRydwbdEmployeeCode=====success:true,procDefId:'"+procDefId+"',procInstId:'"+procInstId+"',executionId:'"+executionId+"',taskId:'"+taskId+"'")
            render(contentType: "text/json") {
                success true
                procDefIds procDefId
                procInstIds procInstId
                executionIds executionId
                taskIds taskId
                nodes  list.collect(){
                    [
                            node: it.node,
                            x   : it.x,
                            y   : it.y,
                            h   : it.h,
                            w   : it.w,
                    ]
                }
                totalCount list.size()
            }
            //render "{success:true,procDefId:'" + procDefId + "',procInstId:'" + procInstId + "',executionId:'" + executionId + "',taskId:'" + taskId + "'}"
            return
        } else {
            //无已执行任务
            render "{success:true,procDefIds:'" + procDefId + "',procInstIds:'" + procInstId + "',executionIds:'" + executionId + "',taskIds:'" + taskId + "'}"
            return
        }
    }

    //取流程图=>流程（通过EmployeeCode）
    def findProcessPicByEmployeeCode() throws Exception {
        String procDefId = "", procInstId = "", executionId = "", taskId = "", procName = "", employeeCode = ""
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        employeeCode = params.employeeCode
        procName = params.procName

        String query = "SELECT * FROM  act_re_procdef where NAME_='" + procName + "' order by DEPLOYMENT_ID_ desc"//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + employeeCode + "%'"//提取流程名称
        String queryHistory = "SELECT * FROM  act_hi_actinst where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + employeeCode + "%' and (END_TIME_ IS NOT NULL) order by END_TIME_ DESC "//提取流程名称
        def listw
        try {
            listw=sql.rows(query)
            if(listw.size()>0){
                procInstId = listw[0].PROC_INST_ID_
                executionId = listw[0].EXECUTION_ID_
                taskId =listw[0].ID_
            }else{
                listw=sql.rows(queryHistory)
                procInstId = listw[0].PROC_INST_ID_
                executionId = listw[0].EXECUTION_ID_
                taskId =listw[0].ID_
            }

        } catch (e) {
            render "failure"
            return
        }






       /* try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
            executionId = sql.rows(query)[0].EXECUTION_ID_
            taskId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }*/

        //String procDefId = params.procDefId//流程定义ID
        //String executionId = params.executionId//
        //String procInstId = params.procInstId//流程实例ID
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        HistoryService historyService = processEngine.getHistoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        //获取历史流程实例x
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (historicProcessInstance != null) {
            // 获取流程定义
            // ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();//也可以
            // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(procInstId).orderByHistoricActivityInstanceId().asc().list();
            // 已执行的节点ID集合
            List<String> executedActivityIdList = new ArrayList<String>();
            int index = 1;            //logger.info("获取已经执行的节点ID");
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                executedActivityIdList.add(activityInstance.getActivityId());
                //logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
                index++;
            }
            //BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());

            BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);//也可以

            // 已执行的线集合
            List<String> flowIds = new ArrayList<String>();
            // 获取流程走过的线 (getHighLightedFlows是下面的方法)
            flowIds = getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);
            /* println("流程图===线=====flowIds.size()=="+flowIds.size())
         println("流程图===线=====0=="+flowIds[0])
         println("流程图===线=====1=="+flowIds[1])
         println("流程图===线=====2=="+flowIds[2])
         println("流程图===线=====3=="+flowIds[3])
         println("流程图===线===8==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0]).size())
         println("流程图===线===8==size[0]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0])[0].getX())
         println("流程图===线===8==size[0]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0])[0].getY())
         println("流程图===线===8==size[1]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[1])[0].getX())
         println("流程图===线===8==size[1]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[1])[0].getY())
         println("流程图===线===9==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[2]).size())
         println("流程图===线===10==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[3]).size())*/
            //为获取在流程图上出现动态提示审核信息
            ArrayList list = new ArrayList<>();
            HashMap<String,String> list0 =new HashMap<String,String>()
            /*println("流程图===节点=====executedActivityIdList.size()=="+executedActivityIdList.size())
            println("流程图===节点0=====0=="+executedActivityIdList[0])
            println("流程图===节点0=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getX())
            println("流程图===节点0=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getY())
            println("流程图===节点0=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getHeight())
            println("流程图===节点0=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getWidth())*/
            list0.put("node",executedActivityIdList[0])
            list0.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getX())
            list0.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getY())
            list0.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getHeight())
            list0.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getWidth())
            list.add(list0)
            if(executedActivityIdList[1]){
                /*println("流程图===节点1=====1=="+executedActivityIdList[1])
                println("流程图===节点1=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getX())
                println("流程图===节点1=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getY())
                println("流程图===节点1=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getHeight())
               println("流程图===节点1=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getWidth())*/
                HashMap<String,String> list1 =new HashMap<String,String>()
                list1.put("node",executedActivityIdList[1])
                list1.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getX())
                list1.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getY())
                list1.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getHeight())
                list1.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getWidth())
                list.add(list1)
            }
            if(executedActivityIdList[2]){
                /*println("流程图===节点2=====2=="+executedActivityIdList[2])
                println("流程图===节点2=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getX())
                println("流程图===节点2=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getY())
                println("流程图===节点2=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getHeight())
                println("流程图===节点2=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getWidth())*/
                HashMap<String,String> list2 =new HashMap<String,String>()
                list2.put("node",executedActivityIdList[3])
                list2.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getX())
                list2.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getY())
                list2.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getHeight())
                list2.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getWidth())
                list.add(list2)
            }
            if(executedActivityIdList[3]){
                /*println("流程图===节点3=====3=="+executedActivityIdList[3])
                println("流程图===节点3=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getX())
                println("流程图===节点3=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getY())
                println("流程图===节点3=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getHeight())
                println("流程图===节点3=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getWidth())*/
                HashMap<String,String> list3 =new HashMap<String,String>()
                list3.put("node",executedActivityIdList[3])
                list3.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getX())
                list3.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getY())
                list3.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getHeight())
                list3.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getWidth())
                list.add(list3)
            }

            if(executedActivityIdList[4]) {
                println("流程图===节点4=====4==" + executedActivityIdList[4])
                /*println("流程图===节点4=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getX())
                println("流程图===节点4=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getY())
                println("流程图===节点4=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getHeight())
                println("流程图===节点4=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getWidth())*/
                HashMap<String, String> list4 = new HashMap<String, String>()
                list4.put("node", executedActivityIdList[4])
                list4.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getX())
                list4.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getY())
                list4.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getHeight())
                list4.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getWidth())
                list.add(list4)
            }
            if(executedActivityIdList[5]) {
                //println("流程图===节点5=====5=="+executedActivityIdList[5])
                HashMap<String, String> list5 = new HashMap<String, String>()
                list5.put("node", executedActivityIdList[5])
                list5.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getX())
                list5.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getY())
                list5.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getHeight())
                list5.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getWidth())
                list.add(list5)
            }
            if(executedActivityIdList[6]) {
                HashMap<String, String> list6 = new HashMap<String, String>()
                list6.put("node", executedActivityIdList[6])
                list6.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getX())
                list6.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getY())
                list6.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getHeight())
                list6.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getWidth())
                list.add(list6)
            }
            if(executedActivityIdList[7]) {
                HashMap<String,String> list7 =new HashMap<String,String>()
                list7.put("node",executedActivityIdList[7])
                list7.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getX())
                list7.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getY())
                list7.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getHeight())
                list7.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getWidth())
                list.add(list7)
            }
            // 获取流程图图像字符流
            ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            //流程图图像显示方式一，已完成任务（包括线为红线）和当前执行任务为红框
            InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);
            //流程图图像显示方式二，已完成任务和当前执行任务为红框
            //InputStream imageStream = pec.generateDiagram(bpmnModel,"png", executedActivityIdList, new ArrayList(),"宋体","微软雅黑","黑体",null,2.0);//无高亮线，无字体设置则汉字显示有问题
            //流程图图像显示方式三，当前执行任务为红框
            //InputStream imageStream = pec.generateDiagram(bpmnModel,"png", runtimeService.getActiveActivityIds(procInstId), new ArrayList(),"宋体","微软雅黑","黑体",null,2.0);
            def filePath = getServletContext().getRealPath("/") + "/processDiagram/" + executionId
            FileOutputStream out = new FileOutputStream(filePath + "_watch.png");
            FileCopyUtils.copy(imageStream, out);
            //render "true"
           // println("findProcessPicByRydwbdEmployeeCode=====success:true,procDefId:'"+procDefId+"',procInstId:'"+procInstId+"',executionId:'"+executionId+"',taskId:'"+taskId+"'")
            render(contentType: "text/json") {
                success true
                procDefIds procDefId
                procInstIds procInstId
                executionIds executionId
                taskIds taskId
                nodes  list.collect(){
                    [
                            node: it.node,
                            x   : it.x,
                            y   : it.y,
                            h   : it.h,
                            w   : it.w,
                    ]
                }
                totalCount list.size()
            }
            //render "{success:true,procDefId:'" + procDefId + "',procInstId:'" + procInstId + "',executionId:'" + executionId + "',taskId:'" + taskId + "'}"
            return
        } else {
            //无已执行任务
            render "{success:true,procDefIds:'" + procDefId + "',procInstIds:'" + procInstId + "',executionIds:'" + executionId + "',taskIds:'" + taskId + "'}"
            return
        }
    }


    // 获取流程走过的线 (getHighLightedFlows是下面的方法)
    public List<String> getHighLightedFlows(BpmnModel bpmnModel, ProcessDefinitionEntity processDefinitionEntity, List<HistoricActivityInstance> historicActivityInstances) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //24小时制
        List<String> highFlows = new ArrayList<String>();// 用以保存高亮的线flowId
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            // 对历史流程节点进行遍历 // 得到节点定义的详细信息
            FlowNode activityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(i).getActivityId());
            List<FlowNode> sameStartTimeNodes = new ArrayList<FlowNode>();// 用以保存后续开始时间相同的节点
            FlowNode sameActivityImpl1 = null;
            HistoricActivityInstance activityImpl_ = historicActivityInstances.get(i);// 第一个节点
            HistoricActivityInstance activityImp2_;
            for (int k = i + 1; k <= historicActivityInstances.size() - 1; k++) {
                activityImp2_ = historicActivityInstances.get(k);// 后续第1个节点
                if (activityImpl_.getActivityType().equals("userTask") && activityImp2_.getActivityType().equals("userTask") &&
                        df.format(activityImpl_.getStartTime()).equals(df.format(activityImp2_.getStartTime()))) {
                    //都是usertask，且主节点与后续节点的开始时间相同，说明不是真实的后继节点
                } else {
                    sameActivityImpl1 = (FlowNode) bpmnModel.getMainProcess().getFlowElement(historicActivityInstances.get(k).getActivityId());
//找到紧跟在后面的一个节点
                    break;
                }
            }
            sameStartTimeNodes.add(sameActivityImpl1); // 将后面第一个节点放在时间相同节点的集合里
            for (int j = i + 1; j < historicActivityInstances.size() - 1; j++) {
                HistoricActivityInstance activityImpl1 = historicActivityInstances.get(j);// 后续第一个节点
                HistoricActivityInstance activityImpl2 = historicActivityInstances.get(j + 1);// 后续第二个节点
                if (df.format(activityImpl1.getStartTime()).equals(df.format(activityImpl2.getStartTime()))) {
// 如果第一个节点和第二个节点开始时间相同保存
                    FlowNode sameActivityImpl2 = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityImpl2.getActivityId());
                    sameStartTimeNodes.add(sameActivityImpl2);
                } else {// 有不相同跳出循环
                    break;
                }
            }
            List<SequenceFlow> pvmTransitions = activityImpl.getOutgoingFlows(); // 取出节点的所有出去的线
            for (SequenceFlow pvmTransition : pvmTransitions) {// 对所有的线进行遍历
                FlowNode pvmActivityImpl = (FlowNode) bpmnModel.getMainProcess().getFlowElement(pvmTransition.getTargetRef());
// 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }

    //取流程图（通过procDefId、executionId、procInstId）
    def findProcessPic() throws Exception {
        String procDefId = params.procDefId//流程定义ID
        String executionId = params.executionId//
        String procInstId = params.procInstId//流程实例ID
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        HistoryService historyService = processEngine.getHistoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        //获取历史流程实例x
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInstId).singleResult();
        if (historicProcessInstance != null) {
            // 获取流程定义
            // ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity) ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(historicProcessInstance.getProcessDefinitionId());
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();//也可以
            // 获取流程历史中已执行节点，并按照节点在流程中执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(procInstId).orderByHistoricActivityInstanceId().asc().list();

            List<String> executedActivityIdList = new ArrayList<String>();//高亮的执行流程节点集合的获取; // 已执行的节点ID集合
            List<String> flowIds = new ArrayList<String>();//高亮流程连接线集合的获取; // 已执行的线集合
            int index = 1;            //logger.info("获取已经执行的节点ID");
            for (HistoricActivityInstance activityInstance : historicActivityInstanceList) {
                executedActivityIdList.add(activityInstance.getActivityId());
                //logger.info("第[" + index + "]个已执行节点=" + activityInstance.getActivityId() + " : " +activityInstance.getActivityName());
                index++;
            }
           // println("流程图===executedActivityIdList.size()======"+executedActivityIdList.size())
            //BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            BpmnModel bpmnModel = repositoryService.getBpmnModel(procDefId);//也可以
            // 获取流程走过的线 (getHighLightedFlows是下面的方法)
            flowIds = getHighLightedFlows(bpmnModel, processDefinition, historicActivityInstanceList);
           /* println("流程图===线=====flowIds.size()=="+flowIds.size())
            println("流程图===线=====0=="+flowIds[0])
            println("流程图===线=====1=="+flowIds[1])
            println("流程图===线=====2=="+flowIds[2])
            println("流程图===线=====3=="+flowIds[3])
            println("流程图===线===8==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0]).size())
            println("流程图===线===8==size[0]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0])[0].getX())
            println("流程图===线===8==size[0]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[0])[0].getY())
            println("流程图===线===8==size[1]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[1])[0].getX())
            println("流程图===线===8==size[1]=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[1])[0].getY())
            println("流程图===线===9==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[2]).size())
            println("流程图===线===10==size=="+bpmnModel.getFlowLocationGraphicInfo(flowIds[3]).size())*/
            //为获取在流程图上出现动态提示审核信息
            ArrayList list = new ArrayList<>();
            HashMap<String,String> list0 =new HashMap<String,String>()
            /*println("流程图===节点=====executedActivityIdList.size()=="+executedActivityIdList.size())
            println("流程图===节点0=====0=="+executedActivityIdList[0])
            println("流程图===节点0=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getX())
            println("流程图===节点0=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getY())
            println("流程图===节点0=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getHeight())
            println("流程图===节点0=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[0]).getWidth())*/
            list0.put("node",executedActivityIdList[0])
            list0.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getX())
            list0.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getY())
            list0.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getHeight())
            list0.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[0]).getWidth())
            list.add(list0)
            if(executedActivityIdList[1]){
                /*println("流程图===节点1=====1=="+executedActivityIdList[1])
                println("流程图===节点1=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getX())
                println("流程图===节点1=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getY())
                println("流程图===节点1=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getHeight())
               println("流程图===节点1=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[1]).getWidth())*/
                HashMap<String,String> list1 =new HashMap<String,String>()
                list1.put("node",executedActivityIdList[1])
                list1.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getX())
                list1.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getY())
                list1.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getHeight())
                list1.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[1]).getWidth())
                list.add(list1)
            }
            if(executedActivityIdList[2]){
                /*println("流程图===节点2=====2=="+executedActivityIdList[2])
                println("流程图===节点2=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getX())
                println("流程图===节点2=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getY())
                println("流程图===节点2=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getHeight())
                println("流程图===节点2=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[2]).getWidth())*/
                HashMap<String,String> list2 =new HashMap<String,String>()
                list2.put("node",executedActivityIdList[3])
                list2.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getX())
                list2.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getY())
                list2.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getHeight())
                list2.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[2]).getWidth())
                list.add(list2)
            }
            if(executedActivityIdList[3]){
                /*println("流程图===节点3=====3=="+executedActivityIdList[3])
                println("流程图===节点3=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getX())
                println("流程图===节点3=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getY())
                println("流程图===节点3=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getHeight())
                println("流程图===节点3=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[3]).getWidth())*/
                HashMap<String,String> list3 =new HashMap<String,String>()
                list3.put("node",executedActivityIdList[3])
                list3.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getX())
                list3.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getY())
                list3.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getHeight())
                list3.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[3]).getWidth())
                list.add(list3)
            }

            if(executedActivityIdList[4]) {
               // println("流程图===节点4=====4==" + executedActivityIdList[4])
                /*println("流程图===节点4=====x=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getX())
                println("流程图===节点4=====y=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getY())
                println("流程图===节点4=====getHeight=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getHeight())
                println("流程图===节点4=====getWidth=="+bpmnModel.getGraphicInfo(executedActivityIdList[4]).getWidth())*/
                HashMap<String, String> list4 = new HashMap<String, String>()
                list4.put("node", executedActivityIdList[4])
                list4.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getX())
                list4.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getY())
                list4.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getHeight())
                list4.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[4]).getWidth())
                list.add(list4)
            }
            if(executedActivityIdList[5]) {
                //println("流程图===节点5=====5=="+executedActivityIdList[5])
                HashMap<String, String> list5 = new HashMap<String, String>()
                list5.put("node", executedActivityIdList[5])
                list5.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getX())
                list5.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getY())
                list5.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getHeight())
                list5.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[5]).getWidth())
                list.add(list5)
            }
            if(executedActivityIdList[6]) {
                HashMap<String, String> list6 = new HashMap<String, String>()
                list6.put("node", executedActivityIdList[6])
                list6.put("x", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getX())
                list6.put("y", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getY())
                list6.put("h", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getHeight())
                list6.put("w", bpmnModel.getGraphicInfo(executedActivityIdList[6]).getWidth())
                list.add(list6)
            }
            if(executedActivityIdList[7]) {
               HashMap<String,String> list7 =new HashMap<String,String>()
               list7.put("node",executedActivityIdList[7])
               list7.put("x",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getX())
               list7.put("y",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getY())
               list7.put("h",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getHeight())
               list7.put("w",bpmnModel.getGraphicInfo(executedActivityIdList[7]).getWidth())
               list.add(list7)
            }

            // 获取流程图图像字符流
            ProcessDiagramGenerator pec = processEngine.getProcessEngineConfiguration().getProcessDiagramGenerator();
            //流程图图像显示方式一，已完成任务（包括线为红线）和当前执行任务为红框
            InputStream imageStream = pec.generateDiagram(bpmnModel, "png", executedActivityIdList, flowIds, "宋体", "微软雅黑", "黑体", null, 2.0);
            //流程图图像显示方式二，已完成任务和当前执行任务为红框
            //InputStream imageStream = pec.generateDiagram(bpmnModel,"png", executedActivityIdList, new ArrayList(),"宋体","微软雅黑","黑体",null,2.0);//无高亮线，无字体设置则汉字显示有问题
            //流程图图像显示方式三，当前执行任务为红框
            //InputStream imageStream = pec.generateDiagram(bpmnModel,"png", runtimeService.getActiveActivityIds(procInstId), new ArrayList(),"宋体","微软雅黑","黑体",null,2.0);
            def filePath = getServletContext().getRealPath("/") + "/processDiagram/" + executionId
            FileOutputStream out = new FileOutputStream(filePath + "_watch.png");
            FileCopyUtils.copy(imageStream, out);
            render(contentType: "text/json") {
                success true
                nodes  list.collect(){
                    [
                            node: it.node,
                            x   : it.x,
                            y   : it.y,
                            h   : it.h,
                            w   : it.w,
                    ]
                }
                totalCount list.size()
            }
           // render "{'success':true,'info':'"+list+")'}"
           // render "true"
            return
        } else {
            //无已执行任务
            render "{'success':false,'info':'无已执行任务'}"
           // render "false"
            return
        }
    }

//activiti6生成流程图代码
    /**
     * 根据流程实例Id,获取实时流程图片
     *
     * @param processInstanceId
     * @param outputStream
     * @return
     */
    //def getFlowImgByInstanceId() {
    def findProcessPicBak() throws Exception {
        String procDefId = params.procDefId//流程定义ID
        String executionId = params.executionId//
       // String procInstId = params.procInstId//流程实例ID
        String processInstanceId = params.procInstId//流程实例ID

        try {
            if (StringUtils.isEmpty(processInstanceId)) {
               // logger.error("processInstanceId is null");
                return;
            }
          //  String procDefId = params.procDefId//流程定义ID
        //    String executionId = params.executionId//


            ProcessEngine processEngineConfiguration = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
            RepositoryService repositoryService = processEngineConfiguration.getRepositoryService();
            //ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
            HistoryService historyService = processEngineConfiguration.getHistoryService();
            RuntimeService runtimeService = processEngineConfiguration.getRuntimeService()
            //获取历史流程实例x
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

            // 获取历史流程实例
          //  HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            // 获取流程中已经执行的节点，按照执行先后顺序排序
            List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId)
                    .orderByHistoricActivityInstanceId().asc().list();

            // 高亮已经执行流程节点ID集合
            List<String> highLightedActivitiIds = new ArrayList<>();
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstances) {
                highLightedActivitiIds.add(historicActivityInstance.getActivityId());
            }

            List<HistoricProcessInstance> historicFinishedProcessInstances = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).finished().list();
            ProcessDiagramGenerator processDiagramGenerator = null;
            // 如果还没完成，流程图高亮颜色为绿色，如果已经完成为红色
            if (!CollectionUtils.isEmpty(historicFinishedProcessInstances)) {
                // 如果不为空，说明已经完成
                processDiagramGenerator = processEngineConfiguration.getProcessDiagramGenerator();
            } else {
                //processDiagramGenerator = new CustomProcessDiagramGenerator();//20190424找不到这个类
                processDiagramGenerator = new DefaultProcessDiagramGenerator();
            }

            BpmnModel bpmnModel = repositoryService.getBpmnModel(historicProcessInstance.getProcessDefinitionId());
            // 高亮流程已发生流转的线id集合
            List<String> highLightedFlowIds = getHighLightedFlows(bpmnModel, historicActivityInstances);

            // 使用默认配置获得流程图表生成器，并生成追踪图片字符流
            InputStream imageStream = processDiagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitiIds, highLightedFlowIds, "宋体", "微软雅黑", "黑体", null, 2.0);

            def filePath = getServletContext().getRealPath("/") + "/processDiagram/" + executionId
            FileOutputStream out = new FileOutputStream(filePath + "_watch.png");
            FileCopyUtils.copy(imageStream, out);
            render "true"
            return

            OutputStream outputStream
            // 输出图片内容
            byte[] b = new byte[1024];
            int len;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                outputStream.write(b, 0, len);
            }

        } catch (Exception e) {
          //  logger.error("processInstanceId" + processInstanceId + "生成流程图失败，原因：" + e.getMessage(), e);
            render "false"
            return
        }

    }

    def start() {
        def infor = ProcessService.startProcessInstance()
        if (infor.information == 'success') {
            // println("流程实例ID instanceid===" + infor.instanceid)
            // println("流程定义ID definitionid===" + infor.definitionid)
            render(contentType: "text/json") {
                [
                        instanceid  : infor.instanceid,
                        definitionid: infor.definitionid
                ]
            }
            return
        } else {
            render "failure"
        }
    }

    @Transactional
    def readProcessHistoryTask() {
        //println("readProcessHistoryTask====procDefId========" + params.procDefId)
      //  println("readProcessTask=====params.dep_tree_id=========" + params.dep_tree_id)
        // println("readProcessTask=====params.department=========" + params.department)
        // println("readProcessTask=====params.procDefId=========" + params.procDefId)
       // println("readProcessTask=====params.currentProcessDate1=========" + params.currentProcessDate1)
     //   println("readProcessTask=====params.currentProcessDateRange=========" + params.currentProcessDateRange)
        def list1, list2,listx
        String queryStartDate,queryEndDate
        ArrayList<?> list3 = new ArrayList<>();
        ArrayList<?> list4 = new ArrayList<>();
        String dep_tree_id = (params.dep_tree_id) ? params.dep_tree_id : "";
        String procDefId = (params.procDefId) ? params.procDefId : "all";
        String dep = (params.department) ? params.department : "all";
        String status = (params.status) ? params.status : "完成审核";
        String currentProcessDateRange = (params.currentProcessDateRange) ? params.currentProcessDateRange : "当前两个月";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (dep_tree_id == "") {
            render(contentType: "text/json") {
                processHistoryTasks {}
                totalCount {}
            }
            return
        }

        String query
        User user
        try {
            user = User.get(springSecurityService.principal.id)
        } catch (e) {
            render(contentType: "text/json") {
                processHistoryTasks
                totalCount
            }
            return
        }



        Calendar currentProcessDate = Calendar.getInstance();
        currentProcessDate.setTime((params.currentProcessDate1) ? sdf.parse(params.currentProcessDate1 + " 00:00:00") : new Date());
        int currentProcessDateYear = currentProcessDate.get(Calendar.YEAR);//获取年份
        int currentProcessDateMonth = currentProcessDate.get(Calendar.MONTH)+1;//获取月份
        int endYear=currentProcessDateYear
        int endMonth=currentProcessDateMonth
        if(endMonth==12){
           endYear=endYear+1;endMonth=1
        }else{
           endMonth=endMonth+1
        }
        queryEndDate=""+endYear+"-"+endMonth+"-01"//查询结束日期
       // println("查询结束日期=="+queryEndDate)
        switch(currentProcessDateRange) {
            case  '当前月' :
                break;
            case  '当前两个月' :
                currentProcessDateMonth=currentProcessDateMonth-1
                break;
            case  '当前三个月' :
                currentProcessDateMonth=currentProcessDateMonth-2
                break;
            case  '当前六个月':
                currentProcessDateMonth=currentProcessDateMonth-5
                break;
            case  '当前一年':
                if(currentProcessDateMonth==12){
                    currentProcessDateMonth=1
                }else{
                    currentProcessDateYear=currentProcessDateYear-1
                    currentProcessDateMonth=currentProcessDateMonth+1
                }
                break;
        }

        if(currentProcessDateMonth<=0){
            currentProcessDateYear=currentProcessDateYear-1
            currentProcessDateMonth=currentProcessDateMonth+12
        }
        queryStartDate=""+currentProcessDateYear+"-"+currentProcessDateMonth+"-01"//查询开始日期
      //  println("readProcessTask====dep_tree_id=============" +dep_tree_id)
       // println("readProcessTask====currentProcessDateRange=============" +currentProcessDateRange)
      //  println("readProcessTask====currentProcessDateYear=============" +currentProcessDateYear)
      //  println("readProcessTask====currentProcessDateMonth=============" +currentProcessDateMonth)

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        HistoryService historyService = processEngine.getHistoryService()
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        //variables.put("shra",user.treeId+":"+user.username+":"+user.truename)
        //println("readProcessHistoryTask=======" + user.treeId + ":" + user.username + ":" + user.truename)

        params.offset = (params.start) ? params.start as int : 0
        params.max = (params.limit) ? params.limit as int : 4
        int len=dep_tree_id.length()

        if (user.chineseAuthority == '管理员') {
            // if ((dep_tree_id == '1000') || (dep_tree_id == '001') || (dep_tree_id == '000')) {
            if ((dep_tree_id == '1000') ||  (dep_tree_id == '000')) {
                query = "SELECT * FROM  act_hi_taskinst  where substring_index(ASSIGNEE_, ':', -1)<>'无'" //全部单位信息
            } else {
                //query = "SELECT * FROM  act_hi_taskinst where substring_index(ASSIGNEE_, ':', 1)='" + dep_tree_id + "' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
                query ="SELECT * FROM  act_hi_taskinst where SUBSTR(SUBSTRING_INDEX(SUBSTRING_INDEX(ASSIGNEE_, ':', 2),':',-1),1,"+len+")='"+dep_tree_id+"' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
            }
        } else {
            if ((dep_tree_id == '1000')  || (dep_tree_id == '000')) {
                query = "SELECT * FROM  act_hi_taskinst where substring_index(ASSIGNEE_, ':', 1)='" + user.treeId + "' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
            }else{
                query = "SELECT * FROM  act_hi_taskinst where substring_index(ASSIGNEE_, ':', 1)='" + user.treeId + "' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
                query = query +" and SUBSTR(SUBSTRING_INDEX(SUBSTRING_INDEX(ASSIGNEE_, ':', 2),':',-1),1,"+len+")='"+dep_tree_id+"'"
            }
            //query = "SELECT * FROM  act_ru_task where ASSIGNEE_='"+(user.treeId+":"+user.username+":"+user.truename)+"'"
        }
        //println("readProcessHistoryTask====query===" + query)
        if (procDefId != 'all') query = query + " and PROC_DEF_ID_='" + procDefId + "'"
       // query=query+ " and YEAR(END_TIME_)>="+currentProcessDateYear+" and MONTH(END_TIME_)>="+currentProcessDateMonth+" and YEAR(END_TIME_)<="+endYear+" and MONTH(END_TIME_)<="+endMonth
        query=query+ " and END_TIME_>=STR_TO_DATE('"+queryStartDate+ "','%Y-%m-%d') and  END_TIME_<STR_TO_DATE('"+queryEndDate+ "','%Y-%m-%d')"
       // println("readProcessHistoryTask====query======"+query)

       if(status=='完成审核'){
           query=query+" and (DELETE_REASON_ IS  NULL) "
       }
       if(status=='审核不通过'){
           query=query+" and (DELETE_REASON_ IS  NOT NULL) "
       }
       query=query+" and (END_TIME_ IS NOT NULL) "
      // println("readProcessTask==query==="+query)

        try {
            list1 = sql.rows(query)
            // list1=sql.execute(query)

        } catch (e) {
            render "failure"
            return
        }

        //println("list1.size()===========" + list1.size())
        // println("(params.offset)==========" + (params.offset))
        //println("(params.max)==========" + (params.max))
        // println("(params.offset+params.max)==========" + (params.offset + params.max))

       // for (int i = params.offset; ((i < (params.offset + params.max)) && i < list1.size()); i++) {
        for (int i = 0; i < list1.size(); i++) {
            //println("list1====111=======" + i + "==========" + list1[i].NAME_ + "=====" + list1[i].PROC_INST_ID_)
            query = "SELECT * FROM  act_re_procdef where ID_='" + list1[i].PROC_DEF_ID_ + "'"//提取流程名称
            try {
                list2 = sql.rows(query)
            } catch (e) {
                render "failure"
                return
            }

//*************
            def procTaskContentVarTemp,listGzzd
            String procJoinName=""
            String procJoinId=""
            List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(list1[i].PROC_INST_ID_).list()
            for (HistoricVariableInstance historicVariableInstance : list) {
                if (historicVariableInstance.getVariableName() == "gzllbdcontent") {//职务与岗位变动申报审核内容
                    procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                    procJoinName=procTaskContentVarTemp.getName()
                    listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                    procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                    break
                }
                if (historicVariableInstance.getVariableName() == "jjyjdEmployeecontent") {//机关事业工资晋级与晋档申报审核内容
                    procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                    procJoinName=""
                    procJoinId=""
                    break
                }
                if (historicVariableInstance.getVariableName() == "syqyJjyjdEmployeecontent") {//石油企业工资晋级与晋档审核内容
                    procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                    procJoinName=""
                    procJoinId=""
                    break
                }
                if (historicVariableInstance.getVariableName() == "newEmployeecontent") {//新增人员申报审核内容
                    procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                    procJoinName=procTaskContentVarTemp.getName()
                    listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                    procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                    break
                }
                if (historicVariableInstance.getVariableName() == "retireEmployeecontent") {//退休人员申报审核内容
                    procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                    procJoinName=procTaskContentVarTemp.getName()
                    listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                    procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                    break
                }
                if (historicVariableInstance.getVariableName() == "dimissionEmployeecontent") {//离职人员申报审核内容
                    procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                    procJoinName=procTaskContentVarTemp.getName()
                    listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                    procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                    break
                }
                if (historicVariableInstance.getVariableName() == "depMonthlySalarycontent") {//月工资基金申报审核内容
                    procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                    procJoinName=""
                    procJoinId=""
                    break
                }
                if (historicVariableInstance.getVariableName() == "ylbxjfjscontent") {//养老保险缴费基数申报审核内容
                    procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                    procJoinName=""
                    procJoinId=""
                    break
                }
                if (historicVariableInstance.getVariableName() == "nzycxjlSalarycontent") {//年终一次性奖励申报审核内容
                    procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                    procJoinName=""
                    procJoinId=""
                    break
                }
                if (historicVariableInstance.getVariableName() == "rydwbdcontent") {//人员单位变动申报审核内容
                    procTaskContentVarTemp = (EmployeeRydwbdVar) historicVariableInstance.getValue();
                    procJoinName=procTaskContentVarTemp.getName()
                    listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                    procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                    break
                }
            }


            list1[i].put("procJoinName", procJoinName)
            list1[i].put("procJoinId", procJoinId)
//**********************
            String treeid = list1[i].ASSIGNEE_.split(":")[1]
            treeid = treeid.substring(0, treeid.length() - 6)
            // println("query===="+"from Department where treeId='" + treeid + "'")
            Department depa = Department.findAll("from Department where treeId='" + treeid + "'")[0]
            // println("depa.department====="+(depa?depa.department:""))
            list1[i].put("PROC_NAME_", list2[0].NAME_)
            list1[i].put("department", (depa ? depa.department : ""))
            if ((dep != "all") && ((list1[i].get('department').toString().indexOf(dep)) == -1)) {
                continue
            }
            list3.add(list1[i])
            //println("list1====222=======" + i + "==========" + list1[i].NAME_ + "=====" + list1[i].PROC_INST_ID_)
        }
        // println("list1.size()===========" + list3.size())
        int n = params.offset + params.max
        for (int i = params.offset; (i < n) && (i < list3.size()); i++) {
            list4.add(list3[i])
        }
        render(contentType: "text/json") {
            processHistoryTasks list4.collect() {
                [
                        ID_            : it?.ID_,
                        PROC_NAME_     : it?.PROC_NAME_,//流程名称
                        PROC_DEF_ID_   : it?.PROC_DEF_ID_,
                        TASK_DEF_KEY_  : it?.TASK_DEF_KEY_,
                        PROC_INST_ID_  : it?.PROC_INST_ID_,
                        EXECUTION_ID_  : it?.EXECUTION_ID_,
                        NAME_          : it?.NAME_,
                        PARENT_TASK_ID_: it?.PARENT_TASK_ID_,
                        DESCRIPTION_   : it?.DESCRIPTION_,
                        OWNER_         : it?.OWNER_,
                        procJoinName     : it?.procJoinName,
                        procJoinId       : it?.procJoinId,
                        ASSIGNEE_      : it?.ASSIGNEE_,
                        department     : it?.department,
                        START_TIME_    : it?.START_TIME_ ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.START_TIME_) : "",
                        CLAIM_TIME_    : it?.CLAIM_TIME_ ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.CLAIM_TIME_) : "",
                        END_TIME_      : it?.END_TIME_ ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.END_TIME_) : "",
                        DURATION_      : it?.DURATION_,
                        DELETE_REASON_ : it?.DELETE_REASON_,
                        PRIORITY_      : it?.PRIORITY_,
                        DUE_DATE_      : it?.DUE_DATE_ ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.DUE_DATE_) : "",
                        FORM_KEY_      : it?.FORM_KEY_,
                        CATEGORY_      : it?.CATEGORY_,
                        TENANT_ID_     : it?.TENANT_ID_
                ]
            }
           // totalCount list1.size().toString()
            totalCount list3.size().toString()

        }
        return
    }

//ProcessTaskStore.js的初始值，
    @Transactional
    def readProcessTask() {
       // println("readProcessTask=====params.dep_tree_id=========" + params.dep_tree_id)
        // println("readProcessTask=====params.department=========" + params.department)
        // println("readProcessTask=====params.procDefId=========" + params.procDefId)
       // println("readProcessTask=====params.currentProcessDate1=========" + params.currentProcessDate1)
      //  println("readProcessTask=====params.currentProcessDateRange=========" + params.currentProcessDateRange)

        if (!(params.dep_tree_id)) {
            render(contentType: "text/json") {
                processTasks null
                totalCount 0
            }
            return
        }
        String queryStartDate,queryEndDate
        String dep_tree_id = (params.dep_tree_id) ? params.dep_tree_id : "000";
        String procDefId = (params.procDefId) ? params.procDefId : "all";
        String dep = (params.department) ? params.department : "all";
        String status = (params.status) ? params.status : "等待审核";
        String currentProcessDateRange = (params.currentProcessDateRange) ? params.currentProcessDateRange : "当前两个月";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar currentProcessDate = Calendar.getInstance();
        currentProcessDate.setTime((params.currentProcessDate1) ? sdf.parse(params.currentProcessDate1 + " 00:00:00") : new Date());
        int currentProcessDateYear = currentProcessDate.get(Calendar.YEAR);//获取年份
        int currentProcessDateMonth = currentProcessDate.get(Calendar.MONTH)+1;//获取月份
        int endYear=currentProcessDateYear
        int endMonth=currentProcessDateMonth
        if(endMonth==12){
            endYear=endYear+1;endMonth=1
        }else{
            endMonth=endMonth+1
        }
        queryEndDate=""+endYear+"-"+endMonth+"-01"//查询结束日期
        // println("查询结束日期=="+queryEndDate)
        switch(currentProcessDateRange) {
            case  '当前月' :
                break;
            case  '当前两个月' :
                currentProcessDateMonth=currentProcessDateMonth-1
                break;
            case  '当前三个月' :
                currentProcessDateMonth=currentProcessDateMonth-2
                break;
            case  '当前六个月':
                currentProcessDateMonth=currentProcessDateMonth-5
                break;
            case  '当前一年':
                if(currentProcessDateMonth==12){
                    currentProcessDateMonth=1
                }else{
                    currentProcessDateYear=currentProcessDateYear-1
                    currentProcessDateMonth=currentProcessDateMonth+1
                }
                break;
        }

        if(currentProcessDateMonth<=0){
            currentProcessDateYear=currentProcessDateYear-1
            currentProcessDateMonth=currentProcessDateMonth+12
        }
        queryStartDate=""+currentProcessDateYear+"-"+currentProcessDateMonth+"-01"//查询开始日期
       // println("readProcessTask====dep_tree_id=============" +dep_tree_id)
       // println("readProcessTask====currentProcessDateRange=============" +currentProcessDateRange)
       // println("readProcessTask====currentProcessDateYear=============" +currentProcessDateYear)
       // println("readProcessTask====currentProcessDateMonth=============" +currentProcessDateMonth)

        // println("readProcessTask====dep_tree_id=============" +dep_tree_id)
        // println("readProcessTask=====procDefId=========" + procDefId)
        //  println("readProcessTask=====dep_tree_id=========" + dep_tree_id)
        //  println("readProcessTask=====dep=========" + dep)

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        //println("=============readProcessTask！！！！！！！！！！！=============")
        def list1, list2
        ArrayList<?> list3 = new ArrayList<>();
        ArrayList<?> list4 = new ArrayList<>();

        String query, shra, shrb, shrc, shrd, shre, shrf, shrg
        // DepMonthlySalaryVar procTaskContentVarTemp
        def procTaskContentVarTemp
        User user = User.get(springSecurityService.principal.id)
        //variables.put("shra",user.treeId+":"+user.username+":"+user.truename)
        //println("readProcessTask=======" + user.treeId + ":" + user.username + ":" + user.truename)

        params.offset = (params.start) ? params.start as int : 0
        params.max = (params.limit) ? params.limit as int : 4

        //完成无人处理的任务
        query = "SELECT * FROM  act_ru_task where substring_index(ASSIGNEE_, ':', -1)='无'" //全部无人处理的任务
        list1 = sql.rows(query)
        for(int m=0;m<list1.size();m++){
            println("完成全部【无人】处理的任务m======"+m)
            taskService.complete(list1[m].ID_)//完成无人处理的任务
        }
       // if (list1) {
       //     taskService.complete(list1[0].ID_)
      //  }

        int len=dep_tree_id.length()
        if (user.chineseAuthority == '管理员') {
           // if ((dep_tree_id == '1000') || (dep_tree_id == '001') || (dep_tree_id == '000')) {
           if ((dep_tree_id == '1000') ||  (dep_tree_id == '000')) {
                query = "SELECT * FROM  act_ru_task  where substring_index(ASSIGNEE_, ':', -1)<>'无'" //全部单位信息
            } else {
               if ((dep_tree_id == '001')||(dep_tree_id == '002')||(dep_tree_id == '003')||(dep_tree_id == '004')||(dep_tree_id == '005')) {
                   query = "SELECT * FROM  act_ru_task where substring_index(ASSIGNEE_, ':', 1)='" + dep_tree_id + "' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
               }else{
                   query = "SELECT * FROM  act_ru_task where substring_index(ASSIGNEE_, ':', 1)='" + dep_tree_id + "' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
                   query = query +" and SUBSTR(SUBSTRING_INDEX(SUBSTRING_INDEX(ASSIGNEE_, ':', 2),':',-1),1,"+len+")='"+dep_tree_id+"'"
               }
            }
        } else {
            if ((user.chineseAuthority != '单位审核人')&&(user.chineseAuthority != '主管单位审核人')){
                //是区人社局审核人或市人社局审核人
                //println("是区人社局审核人或市人社局审核人dep_tree_id===="+dep_tree_id)
                if ((dep_tree_id == '1000')||(dep_tree_id == '000')||(dep_tree_id == '001')||(dep_tree_id == '002')||(dep_tree_id == '003')||(dep_tree_id == '004')||(dep_tree_id == '005')) {
                    query = "SELECT * FROM  act_ru_task where substring_index(ASSIGNEE_, ':', 1)='" + user.treeId + "' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
                }else{
                    query = "SELECT * FROM  act_ru_task where substring_index(ASSIGNEE_, ':', 1)='" + user.treeId + "' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
                    query = query +" and SUBSTR(SUBSTRING_INDEX(SUBSTRING_INDEX(ASSIGNEE_, ':', 2),':',-1),1,"+len+")='"+dep_tree_id+"'"
                }
            }else{
                //是主管单位审核人或单位审核人
                query = "SELECT * FROM  act_ru_task where substring_index(ASSIGNEE_, ':', 1)='" + user.treeId + "' and substring_index(ASSIGNEE_, ':', -1)<>'无'"
            }

        }
        if (procDefId != 'all') query = query + " and PROC_DEF_ID_='" + procDefId + "'"
        //query=query+ " and YEAR(CREATE_TIME_)>="+currentProcessDateYear+" and MONTH(CREATE_TIME_)>="+currentProcessDateMonth+" and YEAR(CREATE_TIME_)<="+endYear+" and MONTH(CREATE_TIME_)<="+endMonth
        query=query+ " and CREATE_TIME_>=STR_TO_DATE('"+queryStartDate+ "','%Y-%m-%d') and  CREATE_TIME_<STR_TO_DATE('"+queryEndDate+ "','%Y-%m-%d')"
        //println("readProcessTask==query==="+query)
        try {
            list1 = sql.rows(query)// list1=sql.execute(query)
        } catch (e) {
            render "failure"
            return
        }
       //  println("===readProcessTask==act_ru_task===query========" + query)

        //for (int i = params.offset; ((i < (params.offset + params.max)) && i < list1.size()); i++) {

        // int num=list1.size()
        // println("params.offset==="+params.offset)
        //  println("params.max==="+params.max)
       // println("list1.size()=========="+list1.size())
        for (int i = 0; i < list1.size(); i++) {
            //if(i>=n)break
            query = "SELECT * FROM  act_re_procdef where ID_='" + list1[i].PROC_DEF_ID_ + "'"//提取流程名称
            try {
                list2 = sql.rows(query)
            } catch (e) {
                render "failure"
                return
            }
            String treeid = list1[i].ASSIGNEE_.split(":")[1]
            treeid = treeid.substring(0, treeid.length() - 6)
            // println("query===="+"from Department where treeId='" + treeid + "'")
            Department depa = Department.findAll("from Department where treeId='" + treeid + "'")[0]
            //  println("depa.department====="+(depa?depa.department:""))
            list1[i].put("PROC_NAME_", list2[0].NAME_)

            list1[i].put("department", (depa ? depa.department : ""))
            // println("readProcessTask=====list1[i].get('department')=========" +list1[i].get('department'))
            //  println("readProcessTask=====list1[i].get('department')=========" +(list1[i].get('department').toString()).indexOf(params.department))
            if ((dep != "all") && ((list1[i].get('department').toString().indexOf(dep)) == -1)) {
                continue
            }

            //println("taskId=====readProcessTask====list1[i].ID_===="+list1[i].ID_)
            //println("taskId=====readProcessTask====list1[i].PROC_DEF_ID_===="+list1[i].PROC_DEF_ID_)
            //println("taskId=====readProcessTask====(list1[i].PROC_DEF_ID_).split(\":\")[0]===="+(list1[i].PROC_DEF_ID_).split(":")[0])
            String procJoinName=""
            String procJoinId=""
            def listGzzd
           // println("(list1[i].PROC_DEF_ID_).split(\":\")[0]====="+(list1[i].PROC_DEF_ID_).split(":")[0])
            try {
                switch ((list1[i].PROC_DEF_ID_).split(":")[0]) {
                    case 'ylbxjfjs'://养老保险缴费基数申报流程
                        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(list1[i].ID_, "ylbxjfjscontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=""
                        procJoinId=""
                        break;
                    case 'depMonthlySalary'://工资基金计划申报流程
                        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(list1[i].ID_, "depMonthlySalarycontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=""
                        procJoinId=""
                        break;
                    case 'nzycxjl'://年终一次性奖励申报流程
                        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(list1[i].ID_, "nzycxjlSalarycontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=""
                        procJoinId=""
                        break;
                    case 'gzllbdsb'://职务与岗位变动申报
                        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(list1[i].ID_, "gzllbdcontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=procTaskContentVarTemp.getName()
                        listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                        procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                        break;
                    case 'jjyjdEmployee'://机关事业工资晋级与晋档申报流程
                        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(list1[i].ID_, "jjyjdEmployeecontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=""
                        procJoinId=""
                        break;
                    case 'syqyJjyjdEmployee'://石油企业工资晋级与晋档申报流程
                        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(list1[i].ID_, "syqyJjyjdEmployeecontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=""
                        procJoinId=""
                        break;
                    case 'newEmployee'://新增人员申报流程
                        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(list1[i].ID_, "newEmployeecontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=procTaskContentVarTemp.getName()
                        listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                        procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                        break;
                    case 'dimissionEmployee'://离职人员申报流程
                        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(list1[i].ID_, "dimissionEmployeecontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=procTaskContentVarTemp.getName()
                        listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                        procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                        break;
                    case 'retireEmployee'://退休人员申报流程
                        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(list1[i].ID_, "retireEmployeecontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        procJoinName=procTaskContentVarTemp.getName()
                        listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                        procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                        break;
                    case 'rydwbd'://人员单位变动申报流程
                       /// println("==============人员单位变动申报流程===============")
                        procTaskContentVarTemp = (EmployeeRydwbdVar) taskService.getVariable(list1[i].ID_, "rydwbdcontent");
                        shra = procTaskContentVarTemp.getShra()
                        shrb = procTaskContentVarTemp.getShrb()
                        shrc = procTaskContentVarTemp.getShrc()
                        shrd = procTaskContentVarTemp.getShrd()
                        shre = procTaskContentVarTemp.getShre()
                        shrf = procTaskContentVarTemp.getShrf()
                        shrg = procTaskContentVarTemp.getShrg()
                        procJoinName=procTaskContentVarTemp.getName()
                        listGzzd=Employee.findAll("from Employee where employeeCode='"+procTaskContentVarTemp.getEmployeeCode()+"'")
                        procJoinId=(listGzzd.size()>0)?listGzzd[0].dagzzd:""
                        break;
                }
                // shrd = procTaskContentVarTemp.getShrd()
            } catch (e) {
                shra = "";
                shrb = "";
                shrc = "";
                shrd = "";
                shre = "";
                shrf = "";
                shrg = "";
            }

            list1[i].put("procJoinName", procJoinName)
            list1[i].put("procJoinId", procJoinId)
            //println("xxxxxxxxxxxxxxxx=======list1[i].get(\"NAME_\")==========="+list1[i].get("NAME_"))


            if(list1[i].get("NAME_")=='单位审核人'){
                list1[i].put("STATUS_", shra)
                if((status=='全部')||(shra.indexOf(status) != -1)) {
                    list3.add(list1[i])
                }
               // if ((shra.indexOf(status) == -1)) {
               //     continue
                //}
            }
            if(list1[i].get("NAME_")=='主管单位审核人'){
                list1[i].put("STATUS_", shrb)
                if((status=='全部')||(shrb.indexOf(status) != -1)) {
                    list3.add(list1[i])
                }
               // if ((shrb.indexOf(status) == -1)) {
               //     continue
              //  }
            }
            if(list1[i].get("NAME_")=='区人社局审核人'){
                list1[i].put("STATUS_", shrc)
                if((status=='全部')||(shrc.indexOf(status) != -1)) {
                    list3.add(list1[i])
                }
                //if ((shrc.indexOf(status) == -1)) {
               //     continue
              //  }
            }
            if(list1[i].get("NAME_")=='市人社局审核人'){
                list1[i].put("STATUS_", shrd)
                if((status=='全部')||(shrd.indexOf(status) != -1)) {
                    list3.add(list1[i])
                }
               // if ((shrd.indexOf(status) == -1)) {
               //     continue
               // }
            }
            if(list1[i].get("NAME_")=='调入区人社局审核人'){
                list1[i].put("STATUS_", shre)
                if((status=='全部')||(shre.indexOf(status) != -1)) {
                    list3.add(list1[i])
                }
                //if ((shrc.indexOf(status) == -1)) {
                //     continue
                //  }
            }
            if(list1[i].get("NAME_")=='调入主管单位审核人'){
                list1[i].put("STATUS_", shrf)
                if((status=='全部')||(shrf.indexOf(status) != -1)) {
                    list3.add(list1[i])
                }
                // if ((shrb.indexOf(status) == -1)) {
                //     continue
                //  }
            }
            if(list1[i].get("NAME_")=='调入单位审核人'){
                list1[i].put("STATUS_", shrg)
                if((status=='全部')||(shrg.indexOf(status) != -1)) {
                    list3.add(list1[i])
                }
                // if ((shra.indexOf(status) == -1)) {
                //     continue
                //}
            }

        }
        int n = params.offset + params.max
        for (int i = params.offset; (i < n) && (i < list3.size()); i++) {
            list4.add(list3[i])
        }
        render(contentType: "text/json") {
            processTasks list4.collect() {
                [
                        ID_              : it?.ID_,
                        REV_             : it?.REV_,
                        PROC_NAME_       : it?.PROC_NAME_,//流程名称
                        EXECUTION_ID_    : it?.EXECUTION_ID_,
                        PROC_INST_ID_    : it?.PROC_INST_ID_,
                        PROC_DEF_ID_     : it?.PROC_DEF_ID_,
                        NAME_            : it?.NAME_,
                        PARENT_TASK_ID_  : it?.PARENT_TASK_ID_,
                        DESCRIPTION_     : it?.DESCRIPTION_,
                        TASK_DEF_KEY_    : it?.TASK_DEF_KEY_,
                        OWNER_           : it?.OWNER_,
                        procJoinName     : it?.procJoinName,
                        procJoinId       : it?.procJoinId,
                        department       : it?.department,
                        ASSIGNEE_        : it?.ASSIGNEE_,
                        DELEGATION_      : it?.DELEGATION_,
                        PRIORITY_        : it?.PRIORITY_,
                        CREATE_TIME_     : it?.CREATE_TIME_ ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.CREATE_TIME_) : "",
                        DUE_DATE_        : it?.DUE_DATE_ ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.DUE_DATE_) : "",
                        CATEGORY_        : it?.CATEGORY_,
                        SUSPENSION_STATE_: it?.SUSPENSION_STATE_,
                        TENANT_ID_       : it?.TENANT_ID_,
                        FORM_KEY_        : it?.FORM_KEY_,
                        CLAIM_TIME_      : it?.CLAIM_TIME_ ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(it?.CLAIM_TIME_) : "",
                        STATUS_          : it?.STATUS_
                ]
            }
            totalCount list3.size().toString()
        }
        return
    }

    //获取流程实例名称的下拉列表
    @Transactional
    def readProcessDef() {
        ArrayList list3 = new ArrayList<>();
        ArrayList list2 = new ArrayList<>();
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String query = "SELECT distinct PROC_DEF_ID_ FROM act_ru_task  ORDER BY PROC_DEF_ID_ ASC"
        def list1 = sql.rows(query)
        list3.add("all&全部")
        for (int i = 0; i < list1.size; i++) {
            query = "SELECT NAME_ FROM  act_re_procdef where ID_='" + list1[i].PROC_DEF_ID_ + "'"//提取流程名称
            list2 = sql.rows(query)
            //println("list1[i].PROC_DEF_ID_===========" + list1[i].PROC_DEF_ID_)
            //println("list2[0].NAME_===========" + list2[0].NAME_)
            list3.add(list1[i].PROC_DEF_ID_ + "&" + list2[0].NAME_)
        }
        render(contentType: "text/json") {
            processDefs list3.collect() {
                [
                        NAME_       : it ? (it.toString()).split("&")[0] : "",
                        PROC_DEF_ID_: it ? (it.toString()).split("&")[1] : ""
                ]
            }
            // totalCount = Department.count().toString()
        }
    }

    //获取历史流程实例名称的下拉列表
    def readProcessHistoryDef = {
        ArrayList list3 = new ArrayList<>();
        ArrayList list2 = new ArrayList<>();
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String query = "SELECT distinct PROC_DEF_ID_ FROM act_hi_taskinst  ORDER BY PROC_DEF_ID_ ASC"
        def list1 = sql.rows(query)
        list3.add("all&全部")
        for (int i = 0; i < list1.size; i++) {
            query = "SELECT NAME_ FROM  act_re_procdef where ID_='" + list1[i].PROC_DEF_ID_ + "'"//提取流程名称
            list2 = sql.rows(query)
            //println("list1[i].PROC_DEF_ID_===========" + list1[i].PROC_DEF_ID_)
            //println("list2[0].NAME_===========" + list2[0].NAME_)
            list3.add(list1[i].PROC_DEF_ID_ + "&" + list2[0].NAME_)
        }
        render(contentType: "text/json") {
            processHistoryDefs list3.collect() {
                [
                        NAME_       : it ? (it.toString()).split("&")[0] : "",
                        PROC_DEF_ID_: it ? (it.toString()).split("&")[1] : ""
                ]
            }
            // totalCount = Department.count().toString()
        }
    }

    //取已经审核意见的内容,显示所有审核人的审核意见
    @Transactional
    def findProcessHistoryTaskComments() {
        String procInstId = params.procInstId//流程实例ID
        //println("0000职务与岗位变动申报审核内容====>>>>>procInstId===="+procInstId)
        String query = "SELECT * FROM  act_hi_taskinst  where PROC_INST_ID_='" + procInstId + "'";
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        def list1 = sql.rows(query)
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        HistoryService historyService = processEngine.getHistoryService()
        ////println("procTaskContentVarTemp========"+procTaskContentVarTemp.getKyxmName())
        //取任务的评论（取所有审核人的审核意见）
        TaskService taskService = processEngine.getTaskService();
        List<Comment> comments
        ArrayList list = new ArrayList<>();
        String c = ""
        for (int i = 0; i < list1.size(); i++) {
            //println("==="+i+"===="+list1[i].ID_);
            comments = taskService.getTaskComments(list1[i].ID_);
            list.add(((comments.size() > 0) ? (comments[0].getFullMessage()) : "无审核意见"))
          //  println("comments[0].getFullMessage()===="+((comments.size() > 0) ? (comments[0].getFullMessage()) : "无审核意见。"))
          //  c = c + "   " + ((comments.size() > 0) ? (comments[0].getFullMessage()) : "")//显示所有审核人的审核意见
            // println("c===="+c)
        }
        //  println("comments[0].getFullMessage()====="+((comments.size() > 0) ? (comments[0].getFullMessage()) : ""))
        render(contentType: "text/json") {
            commentss list
        }
        return
    }

    //在【我的业务】界面查看审核情况取任务的历史内容(取任务的变量)，显示所有审核人的审核意见
    @Transactional
    def findProcessHistoryTaskContentx() {
        //KyxmDetailVar kyxmDetailVarInstance
        String procInstId = params.procInstId//流程实例ID
        //println("0000职务与岗位变动申报审核内容====>>>>>procInstId===="+procInstId)
        String query = "SELECT * FROM  act_hi_taskinst  where PROC_INST_ID_='" + procInstId + "'";
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        def list1 = sql.rows(query)
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        HistoryService historyService = processEngine.getHistoryService()
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(params.procInstId).list()
        def procTaskContentVarTemp
        for (HistoricVariableInstance historicVariableInstance : list) {
            if (historicVariableInstance.getVariableName() == "gzllbdcontent") {//职务与岗位变动申报审核内容
                procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                // println("1111职务与岗位变动申报审核内容====>>>>>")
                break
            }
            if (historicVariableInstance.getVariableName() == "jjyjdEmployeecontent") {//机关事业工资晋级与晋档申报审核内容
                procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                break
            }
            if (historicVariableInstance.getVariableName() == "syqyJjyjdEmployeecontent") {//石油企业工资晋级与晋档审核内容
                procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                break
            }
            if (historicVariableInstance.getVariableName() == "newEmployeecontent") {//新增人员申报审核内容
                procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                break
            }
            if (historicVariableInstance.getVariableName() == "retireEmployeecontent") {//退休人员申报审核内容
                procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                //println("retireEmployeecontent======procTaskContentVarTemp.getName()=========="+procTaskContentVarTemp.getName())
                break
            }
            if (historicVariableInstance.getVariableName() == "dimissionEmployeecontent") {//离职人员申报审核内容
                procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                //println("retireEmployeecontent======procTaskContentVarTemp.getName()=========="+procTaskContentVarTemp.getName())
                break
            }
            if (historicVariableInstance.getVariableName() == "depMonthlySalarycontent") {//月工资基金申报审核内容
                procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                break
            }
            if (historicVariableInstance.getVariableName() == "ylbxjfjscontent") {//养老保险缴费基数申报审核内容
                procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                break
            }
            if (historicVariableInstance.getVariableName() == "nzycxjlSalarycontent") {//年终一次性奖励申报审核内容
                procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                break
            }
            if (historicVariableInstance.getVariableName() == "rydwbdcontent") {//人员单位变动申报审核内容
                procTaskContentVarTemp = (EmployeeRydwbdVar) historicVariableInstance.getValue();
                break
            }
        }



        ////println("procTaskContentVarTemp========"+procTaskContentVarTemp.getKyxmName())
        //取任务的评论（取所有审核人的审核意见）
        TaskService taskService = processEngine.getTaskService();
        List<Comment> comments
        String c = ""
        for (int i = 0; i < list1.size(); i++) {
            //println("==="+i+"===="+list1[i].ID_);
            comments = taskService.getTaskComments(list1[i].ID_);
            c = c + "   " + ((comments.size() > 0) ? (comments[0].getFullMessage()) : "")//显示所有审核人的审核意见
            // println("c===="+c)
        }
        //  println("comments[0].getFullMessage()====="+((comments.size() > 0) ? (comments[0].getFullMessage()) : ""))
        render(contentType: "text/json") {
            procTaskContentVar procTaskContentVarTemp
            commentss c
        }
        return
    }

    ////取任务的历史内容(取任务的变量)
    @Transactional
    def findProcessHistoryTaskContent() {
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        HistoryService historyService = processEngine.getHistoryService()
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(params.procInstId).list()
        def procTaskContentVarTemp
        switch (params.procName) {
            case '职务与岗位变动申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "gzllbdcontent") {
                        procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '机关事业工资晋级与晋档申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "jjyjdEmployeecontent") {
                        procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '石油企业工资晋级与晋档申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "syqyJjyjdEmployeecontent") {
                        procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '退休人员申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "retireEmployeecontent") {
                        procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '离职人员申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "dimissionEmployeecontent") {
                        procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '新增人员申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "newEmployeecontent") {
                        procTaskContentVarTemp = (NewEmployeeVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '人员单位变动申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "rydwbdcontent") {
                        procTaskContentVarTemp = (EmployeeRydwbdVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '工资基金计划申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "depMonthlySalarycontent") {
                        procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '养老保险缴费基数申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "ylbxjfjscontent") {
                        procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
            case '年终一次性奖励申报流程':
                for (HistoricVariableInstance historicVariableInstance : list) {
                    if (historicVariableInstance.getVariableName() == "nzycxjlSalarycontent") {
                        procTaskContentVarTemp = (DepMonthlySalaryVar) historicVariableInstance.getValue();
                        break
                    }
                }
                break;
        }

        //取任务的评论（审核意见）
        TaskService taskService = processEngine.getTaskService();
        List<Comment> comments = taskService.getTaskComments(params.taskId);
        String xx = ((comments.size() > 0) ? (comments[0].getFullMessage()) : "")
        render(contentType: "text/json") {
            procTaskContentVar procTaskContentVarTemp//因为procTaskContentVarTemp已经实现了implements Serializable
            commentss xx
        }
        return
    }

    ////取申报流程任务的内容(取任务的变量)
    @Transactional
    def findProcessTaskContent() {
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        String taskId = params.taskId; // 任务Id
        //取任务的内容
        def procTaskContentVarTemp
        switch (params.procName) {
            case '职务与岗位变动申报流程':
                procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "gzllbdcontent");
                break;
            case '机关事业工资晋级与晋档申报流程':
                procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "jjyjdEmployeecontent");
                break;
            case '石油企业工资晋级与晋档申报流程':
                procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "syqyJjyjdEmployeecontent");
                break;
            case '退休人员申报流程':
                procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "retireEmployeecontent");
                break;
            case '离职人员申报流程':
                procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "dimissionEmployeecontent");
                break;
            case '新增人员申报流程':
                procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "newEmployeecontent");
                break;
            case '人员单位变动申报流程':
                procTaskContentVarTemp = (EmployeeRydwbdVar) taskService.getVariable(taskId, "rydwbdcontent");
                break;
            case '工资基金计划申报流程':
                procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "depMonthlySalarycontent");
                break;
            case '养老保险缴费基数申报流程':
                procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "ylbxjfjscontent");
                break;
            case '年终一次性奖励申报流程':
                procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "nzycxjlSalarycontent");
                break;
        }
        //取任务的评论（审核意见）
        List<Comment> comments = taskService.getTaskComments(params.taskId);
        String xx = ((comments.size() > 0) ? (comments[0].getFullMessage()) : "")
        render(contentType: "text/json") {
            procTaskContentVar procTaskContentVarTemp//因为procTaskContentVarTemp已经实现了implements Serializable
            commentss xx
        }
        return
    }

    //取任务调令扫描的附件getAttachment
    def getAttachment() {
       // println("取任务调令扫描的附件=====params.taskId====" + params.taskId)
      //  println("取任务调令扫描的附件=====params.procInstId====" + params.procInstId)
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId(params.procInstId).singleResult()

        /*
        //依据流程实例PROC_INST_ID_在act_hi_attachment中取得附件attachment（在一个流程实例中附件调令只有一个！！！）
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String query = "SELECT * FROM  act_hi_attachment where PROC_INST_ID_='" + params.procInstId + "'"
       // println("取任务调令扫描的附件=====query===="+query)
        def list1 = sql.rows(query)[0]
        Attachment[] attachment = taskService.getTaskAttachments(list1.TASK_ID_)*/

        //Attachment[] attachment = taskService.getTaskAttachments(params.taskId)//查询某个taskId关联的所有附件对象
        Attachment[] attachment = taskService.getProcessInstanceAttachments(params.procInstId)//查询某个实例关联的所有附件对象

        if (params.num == "0") {
            // println("把附件一（调令扫描的附件）")
            InputStream inputStream0 = taskService.getAttachmentContent(attachment[0].getId())//取到时任务调令扫描的附件
            OutputStream outputStream0 = response.getOutputStream();
            BufferedImage dlwhPhoto = ImageIO.read(inputStream0);
            ImageIO.write(dlwhPhoto, "JPEG", outputStream0);//取出调令扫描的附件文件直接显示到网页上
            outputStream0.flush();
            outputStream0.close();
            inputStream0.close();
        } else {
            //把附件二（个人工资详细信息Excel文件）写到newExcelFile指定的位置
            InputStream newExcelFileInputStream
            FileOutputStream outputStream1
            String excelFile = ""
            String tmpPath = getServletContext().getRealPath("/") + "appTmp\\"
            String pdffile = tmpPath + params.taskId + "个人工资详细信息.pdf"
            String newExcelFile = tmpPath + params.taskId + params.departmentName + params.name + "个人工资详细信息.xlsx"
            if (attachment.size() == 1) {
                newExcelFileInputStream = taskService.getAttachmentContent(attachment[0].getId())//取到时任务附件(个人工资详细信息Excel文件)
                //println("attachment[0].getId()===="+attachment[0].getId())
            } else {
                newExcelFileInputStream = taskService.getAttachmentContent(attachment[1].getId())//取到时任务附件(个人工资详细信息Excel文件)
                // println("attachment[1].getId()===="+attachment[1].getId())
            }
            outputStream1 = new FileOutputStream(new File(newExcelFile));
            byte[] buf = new byte[1024];//大小只影响转换速度
            int numBytesRead = 0;
            while ((numBytesRead = newExcelFileInputStream.read(buf)) != -1) {
                //outputStream1.write(buf, 0, numBytesRead);
                outputStream1.write(buf, 0, numBytesRead);
            }
            outputStream1.flush()
            outputStream1.close()
            newExcelFileInputStream.close()


            int WORD2HTML = 8;// 8 代表word保存成html
            int WD2PDF = 17; // 17代表word保存成pdf
            int PPT2PDF = 32;
            int XLS2PDF = 0;
            OutputStream outputStream = response.getOutputStream();
            FileInputStream inputStream = null;
            byte[] data = null;
            ActiveXComponent app = null;
            try {
                ComThread.InitSTA(true);
                app = new ActiveXComponent("Excel.Application");
                app.setProperty("DisplayAlerts", "False");
                app.setProperty("Visible", false);
                app.setProperty("AutomationSecurity", new Variant(3));//禁用宏
                Dispatch excels = app.getProperty("Workbooks").toDispatch();
                // Dispatch excel = Dispatch.call(excels, "Open",excelFile,false,true).toDispatch();
                Dispatch excel = Dispatch.call(excels, "Open", newExcelFile, false, true).toDispatch();
                Dispatch currentSheet = Dispatch.get((Dispatch) excel, "ActiveSheet").toDispatch();
                Dispatch pageSetup = Dispatch.get(currentSheet, "PageSetup").toDispatch();
                Dispatch.put(pageSetup, "Orientation", new Variant(1));//1纵2横
                Dispatch.call(excel, "ExportAsFixedFormat", XLS2PDF, pdffile);
                Dispatch.call(excel, "Close");// 关闭Excel文件
                if (app != null) {
                    app.invoke("Quit");//退出进程对象
                    ComThread.Release()//释放进程
                    app = null;
                }
                inputStream = new FileInputStream(new File(pdffile));
                int size = inputStream.available()////println("文件大小==========jpg=============="+size)
                // ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                buf = new byte[1024];//大小只影响转换速度
                numBytesRead = 0;
                while ((numBytesRead = inputStream.read(buf)) != -1) {
                    //outputStream1.write(buf, 0, numBytesRead);
                    outputStream.write(buf, 0, numBytesRead);
                }
                response.addHeader("content-type", "application/pdf");
                outputStream.flush()
                outputStream.close();
                inputStream.close();

                File filep = new File(pdffile);//删除临时文件
                filep.delete();
                //filep = new File(newExcelFile);//删除临时文件
                // filep.delete();
            } catch (Exception ex1) {
                // ex1.printStackTrace();
            }
        }
        /* OutputStream os = new FileOutputStream(filePath + params.taskId + "Dlwh.jpg")
         int x;
         while ((x = is.read()) != -1) {
             os.write(x);
         }
         is.close();
         os.close();
         //System.out.println("文件复制成功！");
         render "success"//很重要，没有则后台出现错误信息！！
         return*/
    }

    //删除为显示附件二所生成的个人工资详细信息Excel临时文件
    def deleteAttachmentExcelEmployeeDetail() {
        String tmpPath = getServletContext().getRealPath("/") + "appTmp\\"
        String newExcelFile = tmpPath + params.taskId + params.departmentName + params.name + "个人工资详细信息.xlsx"
        File file = new File(newExcelFile)
        file.delete()
    }

    // 不用了，删除取任务调令扫描的附件getAttachment
    def deleteAttachmentPic() {
        // println("删除取任务调令扫描的附件getAttachment（通过executionId）"+params.executionId)
        String executionId = params.executionId
        def fileName = getServletContext().getRealPath("/") + "/photoD/" + executionId + "Dlwh.jpg"
        File file = new File(fileName)
        file.delete()
    }

    //审核通过
    @Transactional
    def agreeProcessTask() {
        switch (params.procName) {
            case '职务与岗位变动申报流程':
                agreeGzllbdEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '机关事业工资晋级与晋档申报流程':
                agreeJjyjdEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '石油企业工资晋级与晋档申报流程':
                agreeSyqyJjyjdEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '退休人员申报流程':
                agreeRetireEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '离职人员申报流程':
                agreeDimissionEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '新增人员申报流程':
                agreeNewEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '人员单位变动申报流程':
                agreeEmployeeRydwbd(params.taskId, params.procInstId, params.shyj)
                break;
            case '工资基金计划申报流程':
                agreeDepMonthlySalary(params.taskId, params.procInstId, params.shyj)
                break;
            case '养老保险缴费基数申报流程':
                agreeYlbxjfjs(params.taskId, params.procInstId, params.shyj)
                break;
            case '年终一次性奖励申报流程':
                agreeNzycxjlSalary(params.taskId, params.procInstId, params.shyj)
                break;
        }
        render "审核成功！！"
        return
    }
    //审核通过职务与岗位变动人员
    String agreeGzllbdEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, employeeCode
        NewEmployeeVar procTaskContentVarTemp
        def list
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        // println("审核通过职务与岗位变动人员shr===="+shr+"=====assignee==="+assignee)
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "gzllbdcontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核通过");
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核通过");
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核通过");//市人社局审核不再审核
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            procTaskContentVarTemp.setShrd(assignee + "已经审核通过");
        }
        //审核通过机关事业工资晋级与晋档人员全部完毕！机关事业工资晋级与晋档人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
        String query = "from Employee  where employeeCode='" + procTaskContentVarTemp.getEmployeeCode() + "'"//正常查询
        def dataListEmployee = Employee.findAll(query)
        employeeCode = dataListEmployee[0].employeeCode
        if (shra == shr) {
            setEmployeeGzllPassOrNot(employeeCode, "shra", assignee + "已经审核通过")
        }
        if (shrb == shr) {
            setEmployeeGzllPassOrNot(employeeCode, "shrb", assignee + "已经审核通过")
        }
        if (shrc == shr) {
            setEmployeeGzllPassOrNot(employeeCode, "shrc", assignee + "已经审核通过")//市人社局审核不再审核
        }
        if (shrd == shr) {
            setEmployeeGzllPassOrNot(employeeCode, "shrd", assignee + "已经审核通过")
        }

        taskService.setVariable(taskId, "gzllbdcontent", procTaskContentVarTemp);//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核通过机关事业工资晋级与晋档人员
    String agreeJjyjdEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, employeeCode
        NewEmployeeVar procTaskContentVarTemp
        def list
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                // assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        // println("审核通过机关事业工资晋级与晋档人员shr===="+shr+"=====assignee==="+assignee)
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "jjyjdEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核通过");
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核通过");
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核通过");
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            procTaskContentVarTemp.setShrd(assignee + "已经审核通过");
        }
        //审核通过机关事业工资晋级与晋档人员全部完毕！机关事业工资晋级与晋档人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
        String query = "from Employee  where treeId='" + procTaskContentVarTemp.getTreeId() + "' order by employeeCode Asc"//正常查询
        def dataListEmployee = Employee.findAll(query)
        for (int i = 0; i < dataListEmployee.size(); i++) {
            employeeCode = dataListEmployee[i].employeeCode
            if (shra == shr) {
                setEmployeeGztzPassOrNot(employeeCode, "shra", assignee + "已经审核通过")
            }
            if (shrb == shr) {
                setEmployeeGztzPassOrNot(employeeCode, "shrb", assignee + "已经审核通过")
            }
            if (shrc == shr) {
                setEmployeeGztzPassOrNot(employeeCode, "shrc", assignee + "已经审核通过")
            }
            if (shrd == shr) {
                setEmployeeGztzPassOrNot(employeeCode, "shrd", assignee + "已经审核通过")
            }
        }
        taskService.setVariable(taskId, "jjyjdEmployeecontent", procTaskContentVarTemp);//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核通过石油企业工资晋级与晋档人员
    String agreeSyqyJjyjdEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, employeeCode
        NewEmployeeVar procTaskContentVarTemp
        def list
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "syqyJjyjdEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核通过");
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核通过");
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核通过");
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            procTaskContentVarTemp.setShrd(assignee + "已经审核通过");
        }
        //审核通过石油企业工资晋级与晋档人员全部完毕！石油企业工资晋级与晋档人员工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
        String query = "from Employee  where treeId='" + procTaskContentVarTemp.getTreeId() + "' order by employeeCode Asc"//正常查询
        def dataListEmployee = Employee.findAll(query)
        for (int i = 0; i < dataListEmployee.size(); i++) {
            employeeCode = dataListEmployee[i].employeeCode
            if (shra == shr) {
                setEmployeeSyqyGztzPassOrNot(employeeCode, "shra", assignee + "已经审核通过")
            }
            if (shrb == shr) {
                setEmployeeSyqyGztzPassOrNot(employeeCode, "shrb", assignee + "已经审核通过")
            }
            if (shrc == shr) {
                setEmployeeSyqyGztzPassOrNot(employeeCode, "shrc", assignee + "已经审核通过")
            }
            if (shrd == shr) {
                setEmployeeSyqyGztzPassOrNot(employeeCode, "shrd", assignee + "已经审核通过")
            }
        }
        taskService.setVariable(taskId, "syqyJjyjdEmployeecontent", procTaskContentVarTemp);
        //再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核通过退休人员
    String agreeRetireEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                // assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "retireEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shra", assignee + "已经审核通过")
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrb", assignee + "已经审核通过")
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            //审核通过退休人员全部完毕！退休人员的单位信息为“退休人员+treeId”！
            //市人社局审核不再审核
            procTaskContentVarTemp.setShrc(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrc", assignee + "已经审核通过")
            Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            employeeInstance.setTreeId("退休人员" + employeeInstance.getTreeId())
            employeeInstance.setCurrentStatus(procTaskContentVarTemp.getBz().split("正式")[0])
            employeeInstance.save(flush: true)
            //取退休日期
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            //审核通过退休人员全部完毕！退休人员的单位信息为“退休人员+treeId”！
            procTaskContentVarTemp.setShrd(assignee + "已经审核通过")
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrd", assignee + "已经审核通过")
            Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            employeeInstance.setTreeId("退休人员" + employeeInstance.getTreeId())
            employeeInstance.setCurrentStatus(procTaskContentVarTemp.getBz().split("正式")[0])
            employeeInstance.save(flush: true)
            //取退休日期
        }
        taskService.setVariable(taskId, "retireEmployeecontent", procTaskContentVarTemp);//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核通过离职人员
    String agreeDimissionEmployee(taskId, procInstId, shyj) {
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg,query
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "dimissionEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shra", assignee + "已经审核通过")
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrb", assignee + "已经审核通过")
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            //审核通过离职人员全部完毕！离职人员的单位信息恢复正常！
            //市人社局审核不再审核
            procTaskContentVarTemp.setShrc(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrc", assignee + "已经审核通过")
            Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            //  println("employeeInstance.getTreeId()===="+employeeInstance.getTreeId())
            employeeInstance.setTreeId("离职人员" + employeeInstance.getTreeId())
            try{
                employeeInstance.setCurrentStatus(procTaskContentVarTemp.getBz().split("正式")[0])
                new Date(procTaskContentVarTemp.getBz().split("正式")[0])
            }catch(e){
                employeeInstance.setCurrentStatus("2019-01-01")
            }
            employeeInstance.save(flush: true)
            String employee_code1=procTaskContentVarTemp.employeeCode
            // println("employee_code1===="+employee_code1)
            try{
                query="update employee_xl  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                //println("query===="+query)
                sql.execute(query);
                query="update employee_gzll  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                //println("query===="+query)
                sql.execute(query);
                query="update employee_ndkh  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                // println("query===="+query)
                sql.execute(query);
                query="update employee_jbtxx  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                //println("query===="+query)
                sql.execute(query);
                query="update employee_jcxx  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                // println("query===="+query)
                sql.execute(query);
                query="update employee_jg_yfgz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                // println("query===="+query)
                sql.execute(query);
                query="update employee_sy_yfgz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                // println("query===="+query)
                sql.execute(query);
                query="update employee_syqy_yfgz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                // println("query===="+query)
                sql.execute(query);
                query="update employee_yez_yfgz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                //println("query===="+query)
                sql.execute(query);
                query="update employee_gztz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                // println("query===="+query)
                sql.execute(query);
                query="update employee_syqy_gztz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                // println("query===="+query)
                sql.execute(query);
                query="update employee  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                // println("query===="+query)
                sql.execute(query);
                // println("审核成功")
            }catch(e){
                return "审核异常！！"
            }
        }

        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            //审核通过离职人员全部完毕！离职人员的单位信息为“离职人员+treeId”！
            procTaskContentVarTemp.setShrd(assignee + "已经审核通过")
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrd", assignee + "已经审核通过")
            Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
          //  println("employeeInstance.getTreeId()===="+employeeInstance.getTreeId())
            employeeInstance.setTreeId("离职人员" + employeeInstance.getTreeId())
           try{
               employeeInstance.setCurrentStatus(procTaskContentVarTemp.getBz().split("正式")[0])
               new Date(procTaskContentVarTemp.getBz().split("正式")[0])
           }catch(e){
               employeeInstance.setCurrentStatus("2019-01-01")
           }
           employeeInstance.save(flush: true)
            String employee_code1=procTaskContentVarTemp.employeeCode
           // println("employee_code1===="+employee_code1)
            try{
                query="update employee_xl  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                //println("query===="+query)
                sql.execute(query);
                query="update employee_gzll  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                //println("query===="+query)
                sql.execute(query);
                query="update employee_ndkh  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
               // println("query===="+query)
                sql.execute(query);
                query="update employee_jbtxx  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                //println("query===="+query)
                sql.execute(query);
                query="update employee_jcxx  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
               // println("query===="+query)
                sql.execute(query);
                query="update employee_jg_yfgz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
               // println("query===="+query)
                sql.execute(query);
                query="update employee_sy_yfgz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
               // println("query===="+query)
                sql.execute(query);
                query="update employee_syqy_yfgz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
               // println("query===="+query)
                sql.execute(query);
                query="update employee_yez_yfgz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
                //println("query===="+query)
                sql.execute(query);
                query="update employee_gztz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
               // println("query===="+query)
                sql.execute(query);
                query="update employee_syqy_gztz  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
               // println("query===="+query)
                sql.execute(query);
                 query="update employee  set employee_code=CONCAT('离职人员变动','"+employee_code1+"') where employee_code='"+employee_code1+"'"
               // println("query===="+query)
                sql.execute(query);
               // println("审核成功")
            }catch(e){
                return "审核异常！！"
            }
        }
        taskService.setVariable(taskId, "dimissionEmployeecontent", procTaskContentVarTemp);
//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核通过新增人员
    String agreeNewEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                // assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "newEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shra", assignee + "已经审核通过")
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrb", assignee + "已经审核通过")
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            //审核通过新增人员全部完毕！新增人员的单位信息恢复正常！
            //市人社局审核不再审核
            procTaskContentVarTemp.setShrc(assignee + "已经审核通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrc", assignee + "已经审核通过")
            Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            employeeInstance.setTreeId(employeeInstance.getTreeId().substring(7))
            employeeInstance.save(flush: true)
        }

        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            //审核通过新增人员全部完毕！新增人员的单位信息恢复正常！
            procTaskContentVarTemp.setShrd(assignee + "已经审核通过")
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrd", assignee + "已经审核通过")
            Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            employeeInstance.setTreeId(employeeInstance.getTreeId().substring(7))
            employeeInstance.save(flush: true)
        }
        taskService.setVariable(taskId, "newEmployeecontent", procTaskContentVarTemp);//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)
        return "审核成功！！"
    }

    //设置Employee、EmployeeGzll、EmployeeGztz\、EmployeeSyqyGztz所有信息的审核信息（通过或不通过）
    String setEmployeeGzllPassOrNot(employeeCode, shr, passOrNot) {
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        //1-1、【职务与岗位变动申报】人员的employee中的currentStatus更改为“已申报职务与岗位变动”
        if (("shrd" == shr)||("shrc" == shr)){
            if (passOrNot.indexOf("不通过") == -1) {
                String query = "UPDATE employee set current_status='在职' where  current_status ='已申报职务与岗位变动' and employee_code='"+employeeCode+"'";
                if (sql.execute(query)) {
                    render "{'success':false,'info':'错误！申报职务与岗位变动审核失败'}"; return
                }
            }//【全部通过】后，职务与岗位变动人员的工作履历的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
        }

        //1-2、【职务与岗位变动申报】人员的employeeGzll中的employeeCode的“已新增职务与岗位变动+employeeCode”更改为“已申报职务与岗位变动+employeeCode”
        def lists = (EmployeeGzll.findAll("from EmployeeGzll where employeeCode='已申报职务与岗位变动" + employeeCode + "'"));
        for (EmployeeGzll s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) {
                s.setShrc(passOrNot)//市人社局审核不再审核
                if (passOrNot.indexOf("不通过") == -1) {
                    s.setEmployeeCode(employeeCode)
                    s.setBz("正常调动")
                }//【全部通过】后，职务与岗位变动人员的工作履历的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            if ("shrd" == shr) {
                s.setShrd(passOrNot)
                if (passOrNot.indexOf("不通过") == -1) {
                    s.setEmployeeCode(employeeCode)
                    s.setBz("正常调动")
                }//【全部通过】后，职务与岗位变动人员的工作履历的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            s.save(flush: true)
        }

        //1-3、【职务与岗位变动申报】人员的employeeGztz中的employeeCode的“已新增晋级晋档+employeeCode”更改为“已申报晋级晋档+employeeCode”
        lists = (EmployeeGztz.findAll("from EmployeeGztz where employeeCode='已申报晋级晋档" + employeeCode + "'"));
        for (EmployeeGztz s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr){
                s.setShrc(passOrNot)//市人社局审核不再审核
                if (passOrNot.indexOf("不通过") == -1) {
                    s.setEmployeeCode(employeeCode)
                    s.setBz("正常调动晋级晋档")
                }//【全部通过】后，职务与岗位变动人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            if ("shrd" == shr) {
                s.setShrd(passOrNot)
                if (passOrNot.indexOf("不通过") == -1) {
                    s.setEmployeeCode(employeeCode)
                    s.setBz("正常调动晋级晋档")
                }//【全部通过】后，职务与岗位变动人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            s.save(flush: true)
        }
        //1-4、【职务与岗位变动申报】人员的employeeSyqyGztz中的employeeCode的“已新增晋级晋档+employeeCode”更改为“已申报晋级晋档+employeeCode”
        lists = (EmployeeSyqyGztz.findAll("from EmployeeSyqyGztz where employeeCode='已申报晋级晋档" + employeeCode + "'"));
        for (EmployeeSyqyGztz s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) {
                s.setShrc(passOrNot)//市人社局审核不再审核
                if (passOrNot.indexOf("不通过") == -1) {
                    s.setEmployeeCode(employeeCode)
                    s.setBz("正常调动晋级晋档")
                }//【全部通过】后，职务与岗位变动人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            if ("shrd" == shr) {
                s.setShrd(passOrNot)
                if (passOrNot.indexOf("不通过") == -1) {
                    s.setEmployeeCode(employeeCode)
                    s.setBz("正常调动晋级晋档")
                }//【全部通过】后，职务与岗位变动人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            s.save(flush: true)
        }
    }

    //设置EmployeeGztz所有信息的审核信息（通过或不通过）
    String setEmployeeGztzPassOrNot(employeeCode, shr, passOrNot) {
        def lists = (EmployeeGztz.findAll("from EmployeeGztz where employeeCode='已申报晋级与晋档" + employeeCode + "'"));
        for (EmployeeGztz s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) {
                s.setShrc(passOrNot)//市人社局审核不再审核
                if (passOrNot.indexOf("不通过") == -1) s.setEmployeeCode(employeeCode)//机关事业工资晋级与晋档人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            if ("shrd" == shr) {
                s.setShrd(passOrNot)
                if (passOrNot.indexOf("不通过") == -1) s.setEmployeeCode(employeeCode)//机关事业工资晋级与晋档人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            s.save(flush: true)
        }
    }

    //设置EmployeeSyqyGztz所有信息的审核信息（通过或不通过）
    String setEmployeeSyqyGztzPassOrNot(employeeCode, shr, passOrNot) {
        def lists = (EmployeeSyqyGztz.findAll("from EmployeeSyqyGztz where employeeCode='已申报晋级与晋档" + employeeCode + "'"));
        for (EmployeeSyqyGztz s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) {
                s.setShrc(passOrNot)//市人社局审核不再审核
                if (passOrNot.indexOf("不通过") == -1) s.setEmployeeCode(employeeCode)//石油企业工资晋级与晋档人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            if ("shrd" == shr) {
                s.setShrd(passOrNot)
                if (passOrNot.indexOf("不通过") == -1) s.setEmployeeCode(employeeCode)//石油企业工资晋级与晋档人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
            }
            s.save(flush: true)
        }
    }

    //设置Employee所有信息的审核信息（通过或不通过）
    String setEmployeeXxPassOrNot(employeeCode, shr, passOrNot) {
        def lists = (EmployeeGzll.findAll("from EmployeeGzll where employeeCode='" + employeeCode + "'"));
        for (EmployeeGzll s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) s.setShrc(passOrNot)
            if ("shrd" == shr) s.setShrd(passOrNot)
            s.save(flush: true)
        }
        lists = (EmployeeJcxx.findAll("from EmployeeJcxx where employeeCode='" + employeeCode + "'"));
        for (EmployeeJcxx s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) s.setShrc(passOrNot)
            if ("shrd" == shr) s.setShrd(passOrNot)
            s.save(flush: true)
        }
        lists = (EmployeeJbtxx.findAll("from EmployeeJbtxx where employeeCode='" + employeeCode + "'"));
        for (EmployeeJbtxx s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) s.setShrc(passOrNot)
            if ("shrd" == shr) s.setShrd(passOrNot)
            s.save(flush: true)
        }
        lists = (EmployeeGztz.findAll("from EmployeeGztz where employeeCode='" + employeeCode + "'"));
        for (EmployeeGztz s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) s.setShrc(passOrNot)
            if ("shrd" == shr) s.setShrd(passOrNot)
            s.save(flush: true)
        }
        lists = (EmployeeSyqyGztz.findAll("from EmployeeSyqyGztz where employeeCode='" + employeeCode + "'"));
        for (EmployeeSyqyGztz s : lists) {
            if ("shra" == shr) s.setShra(passOrNot)
            if ("shrb" == shr) s.setShrb(passOrNot)
            if ("shrc" == shr) s.setShrc(passOrNot)
            if ("shrd" == shr) s.setShrd(passOrNot)
            s.save(flush: true)
        }
    }

    //审核通过人员单位变动
    String agreeEmployeeRydwbd(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        EmployeeRydwbdVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //EmployeeRydwbdVar employeeRydwbdVarInstance = (EmployeeRydwbdVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                // assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (EmployeeRydwbdVar) taskService.getVariable(taskId, "rydwbdcontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) procTaskContentVarTemp.setShra(assignee + "已经审核通过")
       //可能与调出单位的【主管单位审核人】重复
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) procTaskContentVarTemp.setShrb(assignee + "已经审核通过")
        //可能与调出单位的【区人社局单位审核人】重复
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) procTaskContentVarTemp.setShrc(assignee + "已经审核通过")

        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) procTaskContentVarTemp.setShrd(assignee + "已经审核通过")

        if((procTaskContentVarTemp.getShrd()).indexOf("已经审核通过")!=-1){
          // println("可能与调入单位的【区人社局单位审核人】重复,【市人社局审核人】已经审核通过后，再做以下判断！！")
            String shre = taskService.getVariable(taskId, "shre");
            if (shre == shr) procTaskContentVarTemp.setShre(assignee + "已经审核通过")
            //可能与调入单位的【主管单位审核人】重复
          //  println("可能与调入单位的【区人社局单位审核人】重复,【市人社局审核人】已经审核通过后，再做以下判断！！")
            String shrf = taskService.getVariable(taskId, "shrf");
            if (shrf == shr) procTaskContentVarTemp.setShrf(assignee + "已经审核通过")
        }else{
           // println("可能与调入单位的【区人社局单位审核人】重复,【市人社局审核人】还没有审核通过！！！！！")
        }

        shrg = taskService.getVariable(taskId, "shrg");
        if (shrg == shr) {
            procTaskContentVarTemp.setShrg(assignee + "已经审核通过")
            procTaskContentVarTemp.setBdzxDate(new Date())
        }
        taskService.setVariable(taskId, "rydwbdcontent", procTaskContentVarTemp);//再一次存入变量rydwbdcontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)

        if (shrg == shr) {
            //执行人员单位变动操作
            def deps = (Department.findAll("from Department where department='" + procTaskContentVarTemp.drgzdw + "'"));
            String drgzdw_treeId = deps[0].treeId
            Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            employeeInstance.setTreeId(drgzdw_treeId) //修改employee的treeId
            //employeeInstance.setName(employeeInstance.getName().substring(1)) //修改name,去掉前面的“#”
            // employeeInstance.setName(employeeInstance.getName())
            employeeInstance.setCurrentStatus("在职")
            employeeInstance.save(flush: true)
            //修改employeeGzll,新增一条记录，修改原单位为【调出单位名称employeeRydwbdInstance.ygzdw】
            def empgzlls = (EmployeeGzll.findAll("from EmployeeGzll where employeeCode='" + procTaskContentVarTemp.employeeCode + "' order by startDate DESC"));
            EmployeeGzll employeeGzllInstance = EmployeeGzll.findById(empgzlls[0].id)
            //EmployeeGzll employeeGzllInstance = EmployeeGzll.findById(empgzlls[0].id)
            EmployeeGzll newEmployeeGzllInstance = new EmployeeGzll()
            newEmployeeGzllInstance.setEmployeeCode(employeeGzllInstance.getEmployeeCode())
            newEmployeeGzllInstance.setStartDate(new Date())
            newEmployeeGzllInstance.setGzzd(employeeGzllInstance.getGzzd())
            newEmployeeGzllInstance.setRylx(employeeGzllInstance.getRylx())
            newEmployeeGzllInstance.setZwzcjsdj(employeeGzllInstance.getZwzcjsdj())
            newEmployeeGzllInstance.setXsdy(employeeGzllInstance.getXsdy())
            newEmployeeGzllInstance.setXsdysm(employeeGzllInstance.getXsdysm())
            newEmployeeGzllInstance.setEndDate(null)
            newEmployeeGzllInstance.setJz(employeeGzllInstance.getJz())
            newEmployeeGzllInstance.setYgzdw(procTaskContentVarTemp.drgzdw)
            newEmployeeGzllInstance.setRzfs(employeeGzllInstance.getRzfs())
            newEmployeeGzllInstance.setBdyy("正常调动")
            newEmployeeGzllInstance.setZmr("")
            newEmployeeGzllInstance.setShra("单位变动")
            newEmployeeGzllInstance.setShrb("单位变动")
            newEmployeeGzllInstance.setShrc("单位变动")
            newEmployeeGzllInstance.setShrd("单位变动")
            newEmployeeGzllInstance.setBz(sdf.format(new Date()) + '调入' + procTaskContentVarTemp.drgzdw)
            newEmployeeGzllInstance.save(flush: true)
            //（照片在数据库中了，不用移了，）移动个人照片到时相应的位置
            /* String fileName = procTaskContentVarTemp.name + procTaskContentVarTemp.employeeCode + ".jpg"
             String startPath = getServletContext().getRealPath("/") + "/images/photo/" + procTaskContentVarTemp.ygzdw + "/" + fileName
             String endPath = getServletContext().getRealPath("/") + "/images/photo/" + procTaskContentVarTemp.drgzdw + "/"
             try {
                 File startFile = new File(startPath);
                 File tmpFile = new File(endPath);//获取文件夹路径
                 if (!tmpFile.exists()) {//判断文件夹是否创建，没有创建则创建新文件夹
                     tmpFile.mkdirs();
                 }
                // System.out.println(endPath + startFile.getName());
                 if (startFile.renameTo(new File(endPath + startFile.getName()))) {
                    // System.out.println("File is moved successful!");
                     //log.info("文件移动成功！文件名：《{}》 目标路径：{}",fileName,endPath);
                 } else {
                    // System.out.println("File is failed to move!");
                     //log.info("文件移动失败！文件名：《{}》 起始路径：{}",fileName,startPath);
                 }
             } catch (Exception e) {
                 //log.info("文件移动异常！文件名：《{}》 起始路径：{}",fileName,startPath);
             }*/
        }
        return "审核成功！！"
    }

    //审核通过工资基金计划
    String agreeDepMonthlySalary(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        DepMonthlySalaryVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //EmployeeRydwbdVar employeeRydwbdVarInstance = (EmployeeRydwbdVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                // assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "depMonthlySalarycontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) procTaskContentVarTemp.setShra(assignee + "已经审核通过")
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) procTaskContentVarTemp.setShrb(assignee + "已经审核通过")
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr)procTaskContentVarTemp.setShrc(assignee + "已经审核通过")//市人社局审核不再审核
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) procTaskContentVarTemp.setShrd(assignee + "已经审核通过")
        taskService.setVariable(taskId, "depMonthlySalarycontent", procTaskContentVarTemp);//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核通过养老保险缴费基数
    String agreeYlbxjfjs(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        DepMonthlySalaryVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                // assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "ylbxjfjscontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) procTaskContentVarTemp.setShra(assignee + "已经审核通过")
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) procTaskContentVarTemp.setShrb(assignee + "已经审核通过")
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) procTaskContentVarTemp.setShrc(assignee + "已经审核通过")//市人社局审核不再审核
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) procTaskContentVarTemp.setShrd(assignee + "已经审核通过")
        taskService.setVariable(taskId, "ylbxjfjscontent", procTaskContentVarTemp);
        //再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)


        //显示上传养老保险缴费基数Excel文件相关信息
        Calendar salaryDate = Calendar.getInstance();
        salaryDate.setTime(procTaskContentVarTemp.gzDate);
        int salaryDateYear = salaryDate.get(Calendar.YEAR);//获取年份
        int salaryDateMonth = salaryDate.get(Calendar.MONTH);//获取月份
       // println("procTaskContentVarTemp.treeId======"+procTaskContentVarTemp.treeId)
       // println("procTaskContentVarTemp.department======"+procTaskContentVarTemp.department)
       // println("salaryDateYear======"+salaryDateYear)
        String tmpPath1=getServletContext().getRealPath("/") + "ylbxjfjs\\"
        String newExcelFile1=tmpPath1 +salaryDateYear+ procTaskContentVarTemp.treeId+ procTaskContentVarTemp.department + "养老保险缴费基数.xls"
      //  println("newExcelFile======"+newExcelFile1)
        //修改这个Excel文件
        //从事务附件中获取退休日期
        Workbook templatewb = new XSSFWorkbook(new FileInputStream(newExcelFile1))//创建模板Excel工作簿对象.xlsx
        Row row
        Cell cell
        String item
        Sheet templateSheet
        for (int sheet = 0; sheet < 7; sheet++) {
            templateSheet = templatewb.getSheetAt(sheet);//直接取模板文件第1个sheet对象[机关事业单位工作人员退休（职）老办法初始待遇核定表]
            for (int irow = 0; irow < 5000; irow++) {
                row = templateSheet.getRow(irow);
                if (!row) continue
                for (int icol = 0; icol < 3; icol++) {
                    item = ""//清空;
                    cell = row.getCell((short) icol);
                    if (!cell || cell == "") continue
                    if (cell.getCellTypeEnum() == CellType.STRING) {
                        item = cell.getStringCellValue()//取定义的替代字符，例：{printDate}
                    }
                    switch (item) {
                        case "单位负责人：":
                            row = templateSheet.createRow(irow+1);
                            cell = row.createCell((short) 0);
                            cell.setCellValue("单位审核人："+procTaskContentVarTemp.getShra()+"            主管单位审核人："+procTaskContentVarTemp.getShrb()+"            区人社局审核人："+procTaskContentVarTemp.getShrc()+"            市人社局审核人："+procTaskContentVarTemp.getShrd())
                            irow=5001
                    }
                }
            }
        }
        FileOutputStream fOut = new FileOutputStream(newExcelFile1); //设置输出流文件
        templatewb.write(fOut)
        fOut.flush();
        fOut.close(); //操作结束，关闭文件
        templatewb.close()
        return "审核成功！！"
    }

    //审核通过年终一次性奖励
    String agreeNzycxjlSalary(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        DepMonthlySalaryVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "nzycxjlSalarycontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) procTaskContentVarTemp.setShra(assignee + "已经审核通过")
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) procTaskContentVarTemp.setShrb(assignee + "已经审核通过")
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) procTaskContentVarTemp.setShrc(assignee + "已经审核通过")//市人社局审核不再审核
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) procTaskContentVarTemp.setShrd(assignee + "已经审核通过")
        taskService.setVariable(taskId, "nzycxjlSalarycontent", procTaskContentVarTemp);
        //再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核不通过
    @Transactional
    def disagreeProcessTask() {
        //println("params.shyj=================================="+params.shyj)
        //println("params.procInstId=================================="+params.procInstId)
        // println("params.procName=================================="+params.procName)
        switch (params.procName) {
            case '职务与岗位变动申报流程':
                disagreeGzllbdEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '机关事业工资晋级与晋档申报流程':
                disagreeJjyjdEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '石油企业工资晋级与晋档申报流程':
                disagreeSyqyJjyjdEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '退休人员申报流程':
                disagreeRetireEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '离职人员申报流程':
                disagreeDimissionEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '新增人员申报流程':
                disagreeNewEmployee(params.taskId, params.procInstId, params.shyj)
                break;
            case '人员单位变动申报流程':
                disagreeEmployeeRydwbd(params.taskId, params.procInstId, params.shyj)
                break;
            case '工资基金计划申报流程':
                disagreeDepMonthlySalary(params.taskId, params.procInstId, params.shyj)
                break;
            case '养老保险缴费基数申报流程':
                disagreeYlbxjfjs(params.taskId, params.procInstId, params.shyj)
                break;
            case '年终一次性奖励申报流程':
                disagreeNzycxjlSalary(params.taskId, params.procInstId, params.shyj)
                break;
        }
        render "审核成功！！"
        return
    }

//审核不通过职务与岗位变动人员
    String disagreeGzllbdEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, employeeCode
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "gzllbdcontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核不通过");
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核不通过");
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核不通过");
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            procTaskContentVarTemp.setShrd(assignee + "已经审核不通过");
        }
        //审核不通过职务与岗位变动人员
        String query = "from Employee  where employeeCode='" + procTaskContentVarTemp.getEmployeeCode() + "' order by employeeCode Asc"
//正常查询
        def dataListEmployee = Employee.findAll(query)
        employeeCode = dataListEmployee[0].employeeCode
        if (shra == shr) {
            setEmployeeGztzPassOrNot(employeeCode, "shra", assignee + "已经审核不通过")
        }
        if (shrb == shr) {
            setEmployeeGztzPassOrNot(employeeCode, "shrb", assignee + "已经审核不通过")
        }
        if (shrc == shr) {
            setEmployeeGztzPassOrNot(employeeCode, "shrc", assignee + "已经审核不通过")
        }
        if (shrd == shr) {
            setEmployeeGztzPassOrNot(employeeCode, "shrd", assignee + "已经审核不通过")
        }

        taskService.setVariable(taskId, "gzllbdcontent", procTaskContentVarTemp);//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        //taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核不通过机关事业工资晋级与晋档人员
    String disagreeJjyjdEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, employeeCode
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "jjyjdEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核不通过");
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核不通过");
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核不通过");
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            procTaskContentVarTemp.setShrd(assignee + "已经审核不通过");
        }
        //审核通过机关事业工资晋级与晋档人员全部完毕！机关事业工资晋级与晋档人员的工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
        String query = "from Employee  where treeId='" + procTaskContentVarTemp.getTreeId() + "' order by employeeCode Asc"
//正常查询
        def dataListEmployee = Employee.findAll(query)
        for (int i = 0; i < dataListEmployee.size(); i++) {
            employeeCode = dataListEmployee[i].employeeCode
            if (shra == shr) {
                setEmployeeGztzPassOrNot(employeeCode, "shra", assignee + "已经审核不通过")
            }
            if (shrb == shr) {
                setEmployeeGztzPassOrNot(employeeCode, "shrb", assignee + "已经审核不通过")
            }
            if (shrc == shr) {
                setEmployeeGztzPassOrNot(employeeCode, "shrc", assignee + "已经审核不通过")
            }
            if (shrd == shr) {
                setEmployeeGztzPassOrNot(employeeCode, "shrd", assignee + "已经审核不通过")
            }
        }
        taskService.setVariable(taskId, "jjyjdEmployeecontent", procTaskContentVarTemp);//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        //taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核不通过石油企业工资晋级与晋档人员
    String disagreeSyqyJjyjdEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, employeeCode
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "syqyJjyjdEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核不通过");
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核不通过");
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核不通过");
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            procTaskContentVarTemp.setShrd(assignee + "已经审核不通过");
        }
        //审核通过石油企业工资晋级与晋档人员全部完毕！石油企业工资晋级与晋档人员工资台帐的职工编号信息“已申报晋级与晋档+employeeCode”更改为“employeeCode”！
        String query = "from Employee  where treeId='" + procTaskContentVarTemp.getTreeId() + "' order by employeeCode Asc"
//正常查询
        def dataListEmployee = Employee.findAll(query)
        for (int i = 0; i < dataListEmployee.size(); i++) {
            employeeCode = dataListEmployee[i].employeeCode
            if (shra == shr) {
                setEmployeeSyqyGztzPassOrNot(employeeCode, "shra", assignee + "已经审核不通过")
            }
            if (shrb == shr) {
                setEmployeeSyqyGztzPassOrNot(employeeCode, "shrb", assignee + "已经审核不通过")
            }
            if (shrc == shr) {
                setEmployeeSyqyGztzPassOrNot(employeeCode, "shrc", assignee + "已经审核不通过")
            }
            if (shrd == shr) {
                setEmployeeSyqyGztzPassOrNot(employeeCode, "shrd", assignee + "已经审核不通过")
            }
        }
        taskService.setVariable(taskId, "syqyJjyjdEmployeecontent", procTaskContentVarTemp);
//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        //taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核不通过退休人员
    String disagreeRetireEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "retireEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shra", assignee + "已经审核不通过")
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrb", assignee + "已经审核不通过")
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrc", assignee + "已经审核不通过")
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            //审核不通过新增人员全部完毕！新增人员的单位信息不恢复正常！
            procTaskContentVarTemp.setShrd(assignee + "已经审核不通过")
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrd", assignee + "已经审核不通过")
            //Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            //employeeInstance.setTreeId(employeeInstance.getTreeId().substring(7))
            // employeeInstance.save(flush: true)
        }
        taskService.setVariable(taskId, "retireEmployeecontent", procTaskContentVarTemp);
//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        //taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核不通过离职人员
    String disagreeDimissionEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "dimissionEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shra", assignee + "已经审核不通过")
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrb", assignee + "已经审核不通过")
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrc", assignee + "已经审核不通过")
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            //审核不通过新增人员全部完毕！新增人员的单位信息不恢复正常！
            procTaskContentVarTemp.setShrd(assignee + "已经审核不通过")
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrd", assignee + "已经审核不通过")
            //Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            //employeeInstance.setTreeId(employeeInstance.getTreeId().substring(7))
            // employeeInstance.save(flush: true)
        }
        taskService.setVariable(taskId, "dimissionEmployeecontent", procTaskContentVarTemp);
//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        //taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核不通过新增人员
    String disagreeNewEmployee(taskId, procInstId, shyj) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr, shrg
        NewEmployeeVar procTaskContentVarTemp
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //NewEmployeeVar employeeRydwbdVarInstance = (NewEmployeeVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (NewEmployeeVar) taskService.getVariable(taskId, "newEmployeecontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) {
            procTaskContentVarTemp.setShra(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shra", assignee + "已经审核不通过")
        }
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) {
            procTaskContentVarTemp.setShrb(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrb", assignee + "已经审核不通过")
        }
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) {
            procTaskContentVarTemp.setShrc(assignee + "已经审核不通过");
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrc", assignee + "已经审核不通过")
        }
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) {
            //审核不通过新增人员全部完毕！新增人员的单位信息不恢复正常！
            procTaskContentVarTemp.setShrd(assignee + "已经审核不通过")
            setEmployeeXxPassOrNot(procTaskContentVarTemp.employeeCode, "shrd", assignee + "已经审核不通过")
            //Employee employeeInstance = (Employee.findAll("from Employee where employeeCode='" + procTaskContentVarTemp.employeeCode + "'"))[0];
            //employeeInstance.setTreeId(employeeInstance.getTreeId().substring(7))
            // employeeInstance.save(flush: true)
        }
        taskService.setVariable(taskId, "newEmployeecontent", procTaskContentVarTemp);//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());
        };
        //taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核不通过人员单位变动
    String disagreeEmployeeRydwbd(taskId, procInstId, shyj) {
        EmployeeRydwbdVar procTaskContentVarTemp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        //EmployeeRydwbdVar employeeRydwbdVarInstance = (EmployeeRydwbdVar) taskService.getVariable(taskId, "rydwbdcontent");
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (EmployeeRydwbdVar) taskService.getVariable(taskId, "rydwbdcontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) procTaskContentVarTemp.setShra(assignee + "已经审核不通过")
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) procTaskContentVarTemp.setShrb(assignee + "已经审核不通过")
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) procTaskContentVarTemp.setShrc(assignee + "已经审核不通过")
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) procTaskContentVarTemp.setShrd(assignee + "已经审核不通过")
        String shre = taskService.getVariable(taskId, "shre");
        if (shre == shr) procTaskContentVarTemp.setShre(assignee + "已经审核不通过")
        String shrf = taskService.getVariable(taskId, "shrf");
        if (shrf == shr) procTaskContentVarTemp.setShrf(assignee + "已经审核不通过")
        String shrg = taskService.getVariable(taskId, "shrg");
        if (shrg == shr) {
            procTaskContentVarTemp.setShrg(assignee + "已经审核不通过")
        }
        taskService.setVariable(taskId, "rydwbdcontent", procTaskContentVarTemp);//再一次存入变量rydwbdcontent
        // 添加任务评论（审核意见）
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());

        };
        //taskService.complete(taskId)
        return "审核成功！！"
    }

    //审核不通过工资基金计划
    String disagreeDepMonthlySalary(taskId, procInstId, shyj) {
        //println("shyj==========="+shyj)
        DepMonthlySalaryVar procTaskContentVarTemp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "depMonthlySalarycontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) procTaskContentVarTemp.setShra(assignee + "已经审核不通过")
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) procTaskContentVarTemp.setShrb(assignee + "已经审核不通过")
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) procTaskContentVarTemp.setShrc(assignee + "已经审核不通过")
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) procTaskContentVarTemp.setShrd(assignee + "已经审核不通过")
        taskService.setVariable(taskId, "depMonthlySalarycontent", procTaskContentVarTemp);
//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        // println("shyj==========="+shyj)
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());

        };
        //taskService.complete(taskId)

        return "审核成功！！"
    }

    //审核不通过养老保险缴费基数
    String disagreeYlbxjfjs(taskId, procInstId, shyj) {
        //println("shyj==========="+shyj)
        DepMonthlySalaryVar procTaskContentVarTemp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "ylbxjfjscontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) procTaskContentVarTemp.setShra(assignee + "已经审核不通过")
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) procTaskContentVarTemp.setShrb(assignee + "已经审核不通过")
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) procTaskContentVarTemp.setShrc(assignee + "已经审核不通过")
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) procTaskContentVarTemp.setShrd(assignee + "已经审核不通过")
        taskService.setVariable(taskId, "ylbxjfjscontent", procTaskContentVarTemp);
//再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        // println("shyj==========="+shyj)
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());

        };
        //taskService.complete(taskId)

        return "审核成功！！"
    }

    //审核不通过年终一次性奖励
    String disagreeNzycxjlSalary(taskId, procInstId, shyj) {
        //println("shyj==========="+shyj)
        DepMonthlySalaryVar procTaskContentVarTemp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String assignee, shr
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().list()
        for (Task task : tasks) {
            if (task.getId() == taskId) {
                shr = task.getAssignee()
                //assignee = shr.split(":")[3]
                assignee = shr.split(":")[(shr.split(":")).length - 1]
                break
            }
        }
        procTaskContentVarTemp = (DepMonthlySalaryVar) taskService.getVariable(taskId, "nzycxjlSalarycontent");
        String shra = taskService.getVariable(taskId, "shra");
        if (shra == shr) procTaskContentVarTemp.setShra(assignee + "已经审核不通过")
        String shrb = taskService.getVariable(taskId, "shrb");
        if (shrb == shr) procTaskContentVarTemp.setShrb(assignee + "已经审核不通过")
        String shrc = taskService.getVariable(taskId, "shrc");
        if (shrc == shr) procTaskContentVarTemp.setShrc(assignee + "已经审核不通过")
        String shrd = taskService.getVariable(taskId, "shrd");
        if (shrd == shr) procTaskContentVarTemp.setShrd(assignee + "已经审核不通过")
        taskService.setVariable(taskId, "nzycxjlSalarycontent", procTaskContentVarTemp);
        //再一次存入变量depMonthlySalarycontent
        // 添加任务评论（审核意见）
        // println("shyj==========="+shyj)
        if (shyj != "") {
            taskService.addComment(taskId, procInstId, assignee + ":" + shyj)
            // 取任务评论（审核意见）
            List<Comment> comments = taskService.getTaskComments(taskId);
            //System.out.println("审核意见数量：" + comments.size());
            //System.out.println("审核意见：" + comments[0].getFullMessage());

        };
        //taskService.complete(taskId)

        return "审核成功！！"
    }





//删除指定的任务所在的流程实例，删除已生成的流程图文件 processDiagram/' + executionId + '_watch.png
    @Transactional
    def deleteTask() {
       // println("管理员彻底删除当前任务!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
       // println("params.userRole====" + params.userRole)
        String query
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        // println("第二歩：清除该流程实例【"+params.procInstId+"】事务在当前任务中的信息")
      //  println("taskId===========" + params.procInstId)
        if (params.userRole != "管理员") {
           // println("1111params.userRole====" + params.userRole)
            ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
            //TaskService taskService = processEngine.getTaskService();
            RuntimeService runtimeService = processEngine.getRuntimeService()
            runtimeService.deleteProcessInstance(params.procInstId, "原因：不同意！")
            //删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
            //taskService.deleteTask(params.id,"原因：不同意！")
            //删除已生成的流程图文件 processDiagram/' + executionId + '_watch.png
            //  println("成功=======>删除【工资基金计划申报流程】指定的任务所在的流程实例" )
        } else {
          //  println("管理员彻底删除任务")
          //  println("删除历史任务中相应的任务params.procInstId========" + params.procInstId)
            try {
               // println("如有当前任务则删除当前任务")
                query = "delete from act_ru_task where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_ru_execution where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_ru_identitylink where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_ru_variable where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)


               // println("删除历史任务")
                query = "delete from act_hi_taskinst where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_actinst where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_attachment where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_comment where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_detail where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_identitylink where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_procinst where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_varinst where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)

            } catch (e) {

            }


        }
        render "success"
        return
    }

//删除指定的历史任务所在的流程实例，删除已生成的流程图文件 processDiagram/' + executionId + '_watch.png
    @Transactional
    def deleteHistoryTask() {
       // println("管理员彻底删除历史任务!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
       // println("params.userRole====" + params.userRole)
        String query
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        // println("第二歩：清除该流程实例【"+params.procInstId+"】事务在当前任务中的信息")
     //   println("taskId===========" + params.procInstId)
        if (params.userRole != "管理员") {
           // println("1111params.userRole====" + params.userRole)
            ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
            //TaskService taskService = processEngine.getTaskService();
            RuntimeService runtimeService = processEngine.getRuntimeService()
            runtimeService.deleteProcessInstance(params.procInstId, "原因：不同意！")
            //删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
            //taskService.deleteTask(params.id,"原因：不同意！")
            //删除已生成的流程图文件 processDiagram/' + executionId + '_watch.png
            //  println("成功=======>删除【工资基金计划申报流程】指定的任务所在的流程实例" )
        } else {
           // println("开始删除历史任务中相应的任务params.procInstId========" + params.procInstId)
            try {
                // query = "delete from act_hi_taskinst where PROC_INST_ID_='" + params.procInstId + "' and (END_TIME_ IS NOT NULL)"//删除已完成的任务
                query = "delete from act_hi_taskinst where PROC_INST_ID_='" + params.procInstId + "'"//删除任何任务
                sql.execute(query)
                // query = "delete from act_hi_actinst where PROC_INST_ID_='" + params.procInstId + "' and (END_TIME_ IS NOT NULL)"
                query = "delete from act_hi_actinst where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                // query = "delete from act_hi_procinst where PROC_INST_ID_='" + params.procInstId + "' and (END_TIME_ IS NOT NULL)"
                query = "delete from act_hi_procinst where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)

                query = "delete from act_hi_attachment where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_comment where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_detail where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_identitylink where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
                query = "delete from act_hi_varinst where PROC_INST_ID_='" + params.procInstId + "'"
                sql.execute(query)
            } catch (e) {
              //  println("失败=======删除历史任务")
                render "failure"
                return
            }
        }
       // println("成功===删除历史任务")
        render "success"
        return
    }

    //删除【机关事业工资晋级与晋档申报流程】指定的任务所在的流程实例
    @Transactional
    def deleteTaskJjyjdEmployeesb() {
        //println("删除【机关事业工资晋级与晋档申报流程】指定的任务所在的流程实例" )
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        // String employeeCode =params.employeeCode?params.employeeCode:"";
        // String s=dep_tree_id+":"+employeeCode
        String procDefId, procInstId
        String query = "SELECT * FROM  act_re_procdef where NAME_='机关事业工资晋级与晋档申报流程' order by DEPLOYMENT_ID_ desc"
//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%:" + dep_tree_id + ":%'"
//提取流程名称
        try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
        } catch (e) {
            render "failure"
            return
        }

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        //TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        runtimeService.deleteProcessInstance(procInstId, "原因：不同意！")//删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
        //taskService.deleteTask(params.id,"原因：不同意！")
        // println("成功=======>删除【机关事业工资晋级与晋档申报流程】指定的任务所在的流程实例" )
        render "success"
        return
    }

    //删除【石油企业工资晋级与晋档申报流程】指定的任务所在的流程实例
    @Transactional
    def deleteTaskSyqyJjyjdEmployeesb() {
        //println("删除【石油企业工资晋级与晋档申报流程】指定的任务所在的流程实例" )
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        //String employeeCode =params.employeeCode?params.employeeCode:"";
        // String s=dep_tree_id+":"+employeeCode
        String procDefId, procInstId
        String query = "SELECT * FROM  act_re_procdef where NAME_='石油企业工资晋级与晋档申报流程' order by DEPLOYMENT_ID_ desc"
//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%:" + dep_tree_id + ":%'"
//提取流程名称
        //println("删除【石油企业工资晋级与晋档申报流程】query===="+query)
        try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
        } catch (e) {
            render "failure"
            return
        }

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        //TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        runtimeService.deleteProcessInstance(procInstId, "原因：不同意！")//删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
        //taskService.deleteTask(params.id,"原因：不同意！")
        // println("成功=======>删除【石油企业工资晋级与晋档申报流程】指定的任务所在的流程实例" )
        render "success"
        return
    }

    //删除【职务与岗位变动申报流程】指定的任务所在的流程实例,
    @Transactional
    def deleteTaskGzllbdsb() {
        //println("删除【职务与岗位变动申报流程】指定的任务所在的流程实例" )
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        String employeeCode = params.employeeCode ? params.employeeCode : "";
        // println("params.currentSbDate===="+params.currentSbDate)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar sbDate = Calendar.getInstance();
        sbDate.setTime(params.currentSbDate ? sdf.parse(params.currentSbDate + " 00:00:00") : new Date());
        int sbDateYear = sbDate.get(Calendar.YEAR);//获取年份
        int sbDateMonth = sbDate.get(Calendar.MONTH) + 1;//获取月份
        String procDefId, procInstId
        String query = "SELECT * FROM  act_re_procdef where NAME_='职务与岗位变动申报流程' order by DEPLOYMENT_ID_ desc"//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%:" + employeeCode + ":%'"
//提取流程名称
        try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
        } catch (e) {
            render "failure"
            return
        }

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        //TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        runtimeService.deleteProcessInstance(procInstId, "原因：不同意！")//删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
        //taskService.deleteTask(params.id,"原因：不同意！")
        // println("成功=======>删除【职务与岗位变动申报流程】指定的任务所在的流程实例" )
        render "success"
        return
    }

    //删除【退休人员申报流程】指定的任务所在的流程实例
    @Transactional
    def deleteTaskRetireEmployeesb() {
        //println("删除【退休人员申报流程】指定的任务所在的流程实例" )
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        String employeeCode = params.employeeCode ? params.employeeCode : "";
        // String s=dep_tree_id+":"+employeeCode
        String procDefId, procInstId
        String query = "SELECT * FROM  act_re_procdef where NAME_='退休人员申报流程' order by DEPLOYMENT_ID_ desc"//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + employeeCode + "%'"
//提取流程名称
        try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
        } catch (e) {
            render "failure"
            return
        }

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        //TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        runtimeService.deleteProcessInstance(procInstId, "原因：不同意！")//删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
        //taskService.deleteTask(params.id,"原因：不同意！")
        // println("成功=======>删除【退休人员申报流程】指定的任务所在的流程实例" )
        render "success"
        return
    }

    //删除【离职人员申报流程】指定的任务所在的流程实例
    @Transactional
    def deleteTaskDimissionEmployeesb() {
        //println("删除【离职人员申报流程】指定的任务所在的流程实例" )
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        String employeeCode = params.employeeCode ? params.employeeCode : "";
        // String s=dep_tree_id+":"+employeeCode
        String procDefId, procInstId
        String query = "SELECT * FROM  act_re_procdef where NAME_='离职人员申报流程' order by DEPLOYMENT_ID_ desc"//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + employeeCode + "%'"
//提取流程名称
        try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
        } catch (e) {
            render "failure"
            return
        }

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        //TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        runtimeService.deleteProcessInstance(procInstId, "原因：不同意！")//删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
        //taskService.deleteTask(params.id,"原因：不同意！")
        render "success"
        return
    }

    //删除【新增人员申报流程】指定的任务所在的流程实例
    @Transactional
    def deleteTaskNewEmployeesb() {
        // println("删除【新增人员申报流程】指定的任务所在的流程实例" )
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        String employeeCode = params.employeeCode ? params.employeeCode : "";
        // String s=dep_tree_id+":"+employeeCode
        String procDefId, procInstId
        String query = "SELECT * FROM  act_re_procdef where NAME_='新增人员申报流程' order by DEPLOYMENT_ID_ desc"//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + employeeCode + "%'"
//提取流程名称
        try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
        } catch (e) {
            render "failure"
            return
        }

        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        //TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        runtimeService.deleteProcessInstance(procInstId, "原因：不同意！")//删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
        //taskService.deleteTask(params.id,"原因：不同意！")
        render "success"
        return
    }

    //删除【人员单位变动申报流程】指定的任务所在的流程实例
    @Transactional
    def deleteTaskRydwbdsb() {
        // println("删除【人员单位变动申报流程】指定的任务所在的流程实例" )
        def dataSource = grailsApplication.mainContext.getBean('dataSource') // 直接执行原始的SQL语句
        def sql = new Sql(dataSource)
        String dep_tree_id = params.dep_tree_id ? params.dep_tree_id : "";
        String employeeCode = params.employeeCode ? params.employeeCode : "";
        // String s=dep_tree_id+":"+employeeCode
        String procDefId, procInstId
        //先获取procDefId流程定义Id
        String query = "SELECT * FROM  act_re_procdef where NAME_='人员单位变动申报流程' order by DEPLOYMENT_ID_ desc"//提取流程名称
        try {
            procDefId = sql.rows(query)[0].ID_
        } catch (e) {
            render "failure"
            return
        }
        //先获取procInstId流程实例Id
        query = "SELECT * FROM  act_ru_task where PROC_DEF_ID_='" + procDefId + "' and  ASSIGNEE_ like '%" + employeeCode + "%'"
//提取流程名称
        try {
            procInstId = sql.rows(query)[0].PROC_INST_ID_
        } catch (e) {
            render "failure"
            return
        }

        // println("删除【人员单位变动申报流程】指定的当前任务所在的流程实例" )
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
        //TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService()
        runtimeService.deleteProcessInstance(procInstId, "原因：不同意！")//删除指定的流程实例,"原因：不同意调动单位！"存入了act_hi_actinst表中`
        //taskService.deleteTask(params.id,"原因：不同意！")
        render "success"
        return
    }

}