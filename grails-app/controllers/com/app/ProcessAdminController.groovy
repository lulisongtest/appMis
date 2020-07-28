package com.app

import io.micronaut.spring.tx.annotation.Transactional
import org.activiti.engine.FormService
import org.activiti.engine.HistoryService
import org.activiti.engine.IdentityService
import org.activiti.engine.ManagementService
import org.activiti.engine.ProcessEngine
import org.activiti.engine.RepositoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.task.Task


//这是自动生成的controller


import java.text.SimpleDateFormat

@Transactional(readOnly = false)
class ProcessAdminController {
    def ProcessService
    def index() {
        //println("MyshController!!!!")
        // ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine()
        //创建一个流程引擎对象（为了便于多册测试，修改 name="databaseSchemaUpdate" value="create-drop"  默认为ture）
        ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();

        //println("processEngine")
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //println("runtimeService")
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //println("repositoryService")
        TaskService taskService = processEngine.getTaskService();
        //println("taskService")
        ManagementService managementService = processEngine.getManagementService();
        //println("managementService")
        IdentityService identityService = processEngine.getIdentityService();
        //println("identityService")
        HistoryService historyService = processEngine.getHistoryService();
        //println("historyService")
        FormService formService = processEngine.getFormService();
        //println("formService")

        render "success"
        return
    }


    def findPersonTask() {

        def infor = ProcessService.findPersonTask()
        List<Task> list=infor.list
        if (infor.information == 'success'){
           // //println("流程实例ID instanceid==="+infor.list)
            render(contentType: "text/json") {
                [
                        list: infor.list,
                        //list: "aaaa",

                ]
            }
            return
        }else{
            render "failure"
        }
    }

}
