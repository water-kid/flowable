package com.cj;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.DataObject;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class ActRuTests {

    // 流程运行时数据表
    @Autowired
    RuntimeService runtimeService;


    // 当一个流程执行完后，流程的数据 act_ru_xxx  将会被清空，，你只能从历史表中查询以前的数据
    @Test
    void test() {
        // 设置流程发起人
        Authentication.setAuthenticatedUserId("ww");

        // processDefinitionKey : 数据库中的 key字段，，xml文件的 流程id
        // 启动一个流程,获取流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("leave");

        // 流程定义的id 需要自己查询，，，
//        runtimeService.startProcessInstanceById("")

        System.out.println(processInstance.getId()+"---"+processInstance.getName());

    }


    @Autowired
    IdentityService identityService;

    @Autowired
    RepositoryService repositoryService;

    @Test
    public void test06(){
        // 设置流程发起人
        identityService.setAuthenticatedUserId("zl");

        // 流程定义可以修改，发布很多个版本，，根据key查找会有很多个,,, 要查找 latestVersion()
        ProcessDefinition leave = repositoryService.createProcessDefinitionQuery().processDefinitionKey("leave").latestVersion().singleResult();

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(leave.getId());
        System.out.println(processInstance.getId()+"--"+processInstance.getStartUserId());
    }

@Autowired
    TaskService taskService;

    @Test
    public void test03(){
        // 查询某个人 需要处理的任务
        List<Task> list = taskService.createTaskQuery().taskAssignee("lisi").list();
        for (Task task : list) {
            System.out.println(task.getId()+"---"+task.getName());

            // 完成任务  ，， act_ru_task 先添加新的任务，然后删除完成的任务，
            taskService.complete(task.getId());
        }

    }

    @Test
    public void test04(){
          // act_ru_xxx  是否空了  act_ru_execution 的 PROC_INST_ID_
        // ACT_RU_EXECUTION    ACT_RE_PROCDEF
        String processId = "4f452a00-96f5-11ed-bebc-f0b61e94ce8e";
        // 流程实例只有一个 ，，， ACT_RU_EXECUTION存的是执行实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        System.out.println("processInstance = " + processInstance);
    }


    @Test
    public void test05(){
        // 查看当前流程走到哪一步了
        List<Execution> list = runtimeService.createExecutionQuery().list();
        for (Execution execution : list) {
            // 查询一个执行实例的  活动节点
            List<String> activeActivityIds = runtimeService.getActiveActivityIds(execution.getId());
            // act_ru_execution 的 act_id_
            System.out.println("activeActivityIds = " + activeActivityIds);
        }
    }

    @Test
    public void test07(){
        runtimeService.deleteProcessInstance("4f452a00-96f5-11ed-bebc-f0b61e94ce8e","delete reason");
    }

    @Test
    public void test08(){
        // 查询流程定义
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();

        for (ProcessDefinition processDefinition : list) {
            // 挂起的流程定义 是无法开启流程实例的，，，    判断流程定义是否挂起
            boolean processDefinitionSuspended = repositoryService.isProcessDefinitionSuspended(processDefinition.getId());
            // 挂起一个 流程定义   修改 act_re_procdef 的 SUSPENSION_STATE_ 字段： 2:suspend   1：没有挂起
            repositoryService.suspendProcessDefinitionById(processDefinition.getId());

            // 激活流程定义
            repositoryService.activateProcessDefinitionById(processDefinition.getId());
        }

    }
    
    @Test
    public void test09(){

        // 对于一个挂起的流程实例，，我们是无法执行相应的 Task
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : list) {
            // suspendProcessInstance : 是否挂起 这个流程定义对应的  流程实例，，，，   suspensionDate：挂起时间，到时间之后才会挂起，，null表示立即挂起，，
            // 流程实例被挂起，，，流程定义也被挂起
            // 涉及的表 ： act_ru_execution   act_ru_task  act_re_procdef
//            repositoryService.suspendProcessDefinitionById(processDefinition.getId(),true,null);

            // 激活  流程实例
            repositoryService.activateProcessDefinitionById(processDefinition.getId(),true,null);
        }

    }


    @Test
    public void test10(){
//        Authentication.setAuthenticatedUserId("ww");
//
//        ProcessInstance leave = runtimeService.startProcessInstanceByKey("leave");

        List<Task> list02 = taskService.createTaskQuery().taskAssignee("ww").list();
        for (Task task : list02) {
            taskService.complete(task.getId());
        }

    }

    @Test
    public void test11(){
        // DataObject : 设置流程的一些全局属性，，，  整个流程的`数据对象`
        // <dataObject> 节点

        // 查询一个DataObject
        //  dataObject： act_ru_variable表的name字段
        DataObject dataObject = runtimeService.getDataObject("7e02f91d-9708-11ed-a857-f0b61e94ce8e", "绘制时间");
        System.out.println(dataObject.getName()+"-------"+dataObject.getValue());

        // 查询多个DataObject
        List<Execution> list = runtimeService.createExecutionQuery().list();
        for (Execution execution : list) {
            // key: DataObject的name ，，， value是DataObject
            Map<String, DataObject> dataObjects = runtimeService.getDataObjects(execution.getId());
            Set<String> keys = dataObjects.keySet();
            for (String key : keys) {
                System.out.println(key+"----"+dataObjects.get(key).getValue());
            }
        }
    }
    
    @Test
    public void test12(){
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
        for (ProcessDefinition processDefinition : list) {
            repositoryService.deleteDeployment(processDefinition.getDeploymentId());
        }

//        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
//        for (ProcessInstance processInstance : list) {
//        runtimeService.deleteProcessInstance(processInstance.getProcessInstanceId(),"123");
//        }
    }

    @Test
    public void test13(){
//        Authentication.setAuthenticatedUserId("ww");
//        // 启动指定 tenantId
//        runtimeService.startProcessInstanceByKeyAndTenantId("leave","cc");

        List<Task> list = taskService.createTaskQuery().taskTenantId("cc").list();

    }

    @Test
    public void test20() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("ReceiveTaskDemo.bpmn20.xml");
        String filename = classPathResource.getFilename();
        System.out.println("filename = " + filename);
        System.out.println("classPathResource = " + classPathResource.getInputStream());
    }
}
