package com.cj;

import org.flowable.common.engine.api.history.HistoricData;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.ProcessInstanceHistoryLog;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class HistoryTest {

    @Autowired
    RepositoryService repositoryService;

    @Test
    public void test01() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("hehe/HistoryDemo01.bpmn20.xml");
        repositoryService.createDeployment().addInputStream(classPathResource.getFilename(),classPathResource.getInputStream()).deploy();
    }
    @Autowired
    RuntimeService runtimeService;

    @Test
    public void test02(){
        Authentication.setAuthenticatedUserId("cc");
        runtimeService.startProcessInstanceByKey("HistoryDemo01");
    }



    @Autowired
    TaskService taskService;

    @Test
    public void test03(){
        List<Task> list = taskService.createTaskQuery().taskAssignee("cc").list();
        for (Task task : list) {

            HashMap<String, Object> map = new HashMap<>();
            map.put("days",10);
            map.put("reason","heeh123");
            taskService.complete(task.getId(), map);
        }
    }

    @Test
    public void test04(){
        Task zs = taskService.createTaskQuery().taskAssignee("zs").singleResult();
        HashMap<String, Object> map = new HashMap<>();
        map.put("status","complete...");
        taskService.complete(zs.getId(),map);
    }

    @Autowired
    HistoryService historyService;

    @Test
    public void test05(){
        // 查询已经执行完成的流程信息，，， act_hi_procinst 没有endtime表示没有执行完， duration：耗时时间
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();

        for (HistoricProcessInstance historicProcessInstance : list) {
            System.out.println(historicProcessInstance.getName()+"---"+historicProcessInstance.getEndTime());
        }
    }

    @Test
    public void test06(){
        // 没有执行完的
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().unfinished().list();


        for (HistoricProcessInstance historicProcessInstance : list) {
            System.out.println(historicProcessInstance.getName()+"----------"+historicProcessInstance.getStartTime());
        }
    }

    @Test
    public void test07(){
        // 查找所有
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance historicProcessInstance : list) {
            if (historicProcessInstance.getEndTime() == null){
                // 没有执行完
            }else {
                // 执行完了
            }
        }
    }
    
    @Test
    public void test08(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee("cc").list();
        List<HistoricTaskInstance> finish = historyService.createHistoricTaskInstanceQuery().taskAssignee("cc").finished().list();
        List<HistoricTaskInstance> unfinish = historyService.createHistoricTaskInstanceQuery().taskAssignee("cc").unfinished().list();

        // endTime 是否为null，判断是否完成
        System.out.println("list.size() = " + list.size());
        System.out.println("finish.size() = " + finish.size());
        System.out.println("unfinish.size() = " + unfinish.size());
    }
    
    @Test
    public void test10(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();

        for (HistoricProcessInstance historicProcessInstance : list) {
            // 根据 流程实例id(processInstanceId) 查找task
            List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).finished().list();
            System.out.println(taskList.size());
            System.out.println("================");
        }
        // 查询跟流程相关的
//        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processDefinitionId("").finished().list();

    }


    @Test
    public void test11(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();
        for (HistoricProcessInstance historicProcessInstance : list) {
            List<HistoricActivityInstance> list1 = historyService.createHistoricActivityInstanceQuery()
                    // 流程实例id查询
                    .processInstanceId(historicProcessInstance.getId())
                    // 开始时间排序
                    .orderByHistoricActivityInstanceStartTime()
                    // 升序
                    .asc()
                    .list();
            for (HistoricActivityInstance activityInstance : list1) {
                System.out.println("activityInstance = " + activityInstance.getStartTime());
            }
        }
    }
    
    @Test
    public void test12(){
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().activityType("startEvent")
                .orderByHistoricActivityInstanceStartTime()
                .desc()
                .list();
        for (HistoricActivityInstance historicActivityInstance : list) {
            System.out.println(historicActivityInstance.getActivityType()+historicActivityInstance.getStartTime());
        }
    }
    
    
    @Test
    public void test13() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("hehe/HistoryDemo01.bpmn20.xml");
        repositoryService.createDeployment().addInputStream(classPathResource.getFilename(),classPathResource.getInputStream()).deploy();

//        runtimeService.startProcessInstanceByKey("HistoryDemo01")
//        runtimeService.startProcessInstanceByKey()
    }
    
    @Test
    public void test14(){
        Authentication.setAuthenticatedUserId("ww");
        runtimeService.startProcessInstanceByKey("HistoryDemo01");
    }

    @Test
    public void test15(){
        Task task = taskService.createTaskQuery().taskAssignee("ww").singleResult();
        HashMap<String, Object> map = new HashMap<>();
        map.put("status","first complete");
        map.put("birthday","11-02");
        taskService.complete(task.getId(), map);
    }

    @Test
    public void test16(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        System.out.println("list.size() = " + list.size());

        for (HistoricProcessInstance historicProcessInstance : list) {
            List<HistoricVariableInstance> list1 = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).variableName("birthday").list();
            for (HistoricVariableInstance historicVariableInstance : list1) {
                System.out.println("historicVariableInstance = " + historicVariableInstance.getValue());
            }
        }




    }


    @Test
    public void test17(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance historicProcessInstance : list) {

            ProcessInstanceHistoryLog processInstanceHistoryLog = historyService.createProcessInstanceHistoryLogQuery(historicProcessInstance.getId())
                    // HistoricData ： 中包含 variable
                    .includeVariables()
                    // HistoricData 中包含 task
                    .includeTasks()
                    .singleResult();

            System.out.println(processInstanceHistoryLog.getStartTime()+"---"+processInstanceHistoryLog.getId());
            List<HistoricData> historicData = processInstanceHistoryLog.getHistoricData();
            for (HistoricData data : historicData) {
                if (data instanceof HistoricVariableInstance){
                    // 是 variable
                    HistoricVariableInstance historicVariableInstance = (HistoricVariableInstance) data;
                    System.out.println("historicVariableInstance = " + historicVariableInstance.getValue());
                }else if (data instanceof HistoricTaskInstance){
                    // 是task
                    HistoricTaskInstance historicTaskInstance = (HistoricTaskInstance) data;
                    System.out.println("historicTaskInstance = " + historicTaskInstance.getName());
                }
            }
        }
    }
    
    @Test
    public void test18(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance historicProcessInstance : list) {
            //  查询 参与者，处理人   act_hi_identitylink
            List<HistoricIdentityLink> links = historyService.getHistoricIdentityLinksForProcessInstance(historicProcessInstance.getId());
            for (HistoricIdentityLink link : links) {
                System.out.println("link.getUserId() = " + link.getUserId());
            }
        }
    }

    @Test
    public void test19(){
     String taskName = "组长审批";
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskName(taskName).singleResult();
        // 查找 指定task的处理人
        List<HistoricIdentityLink> links = historyService.getHistoricIdentityLinksForTask(task.getId());

        for (HistoricIdentityLink link : links) {
            System.out.println("link.getUserId() = " + link.getUserId());
        }
    }


    @Test
    public void test20(){
        // 查询执行完的流程的  任务信息
        List<HistoricProcessInstance> list = historyService.createNativeHistoricProcessInstanceQuery().sql("").list();

        for (HistoricProcessInstance historicProcessInstance : list) {
            // 根据流程id ，， 查找对应的 task
            List<HistoricTaskInstance> taskInstanceList = historyService.createNativeHistoricTaskInstanceQuery().sql("").parameter("", "").list();

        }
    }
    
    @Test
    public void test21(){
        
    }

}
