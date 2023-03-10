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
        // ???????????????????????????????????????????????? act_hi_procinst ??????endtime???????????????????????? duration???????????????
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();

        for (HistoricProcessInstance historicProcessInstance : list) {
            System.out.println(historicProcessInstance.getName()+"---"+historicProcessInstance.getEndTime());
        }
    }

    @Test
    public void test06(){
        // ??????????????????
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().unfinished().list();


        for (HistoricProcessInstance historicProcessInstance : list) {
            System.out.println(historicProcessInstance.getName()+"----------"+historicProcessInstance.getStartTime());
        }
    }

    @Test
    public void test07(){
        // ????????????
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().list();
        for (HistoricProcessInstance historicProcessInstance : list) {
            if (historicProcessInstance.getEndTime() == null){
                // ???????????????
            }else {
                // ????????????
            }
        }
    }
    
    @Test
    public void test08(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee("cc").list();
        List<HistoricTaskInstance> finish = historyService.createHistoricTaskInstanceQuery().taskAssignee("cc").finished().list();
        List<HistoricTaskInstance> unfinish = historyService.createHistoricTaskInstanceQuery().taskAssignee("cc").unfinished().list();

        // endTime ?????????null?????????????????????
        System.out.println("list.size() = " + list.size());
        System.out.println("finish.size() = " + finish.size());
        System.out.println("unfinish.size() = " + unfinish.size());
    }
    
    @Test
    public void test10(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();

        for (HistoricProcessInstance historicProcessInstance : list) {
            // ?????? ????????????id(processInstanceId) ??????task
            List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(historicProcessInstance.getId()).finished().list();
            System.out.println(taskList.size());
            System.out.println("================");
        }
        // ????????????????????????
//        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().processDefinitionId("").finished().list();

    }


    @Test
    public void test11(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();
        for (HistoricProcessInstance historicProcessInstance : list) {
            List<HistoricActivityInstance> list1 = historyService.createHistoricActivityInstanceQuery()
                    // ????????????id??????
                    .processInstanceId(historicProcessInstance.getId())
                    // ??????????????????
                    .orderByHistoricActivityInstanceStartTime()
                    // ??????
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
                    // HistoricData ??? ????????? variable
                    .includeVariables()
                    // HistoricData ????????? task
                    .includeTasks()
                    .singleResult();

            System.out.println(processInstanceHistoryLog.getStartTime()+"---"+processInstanceHistoryLog.getId());
            List<HistoricData> historicData = processInstanceHistoryLog.getHistoricData();
            for (HistoricData data : historicData) {
                if (data instanceof HistoricVariableInstance){
                    // ??? variable
                    HistoricVariableInstance historicVariableInstance = (HistoricVariableInstance) data;
                    System.out.println("historicVariableInstance = " + historicVariableInstance.getValue());
                }else if (data instanceof HistoricTaskInstance){
                    // ???task
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
            //  ?????? ?????????????????????   act_hi_identitylink
            List<HistoricIdentityLink> links = historyService.getHistoricIdentityLinksForProcessInstance(historicProcessInstance.getId());
            for (HistoricIdentityLink link : links) {
                System.out.println("link.getUserId() = " + link.getUserId());
            }
        }
    }

    @Test
    public void test19(){
     String taskName = "????????????";
        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskName(taskName).singleResult();
        // ?????? ??????task????????????
        List<HistoricIdentityLink> links = historyService.getHistoricIdentityLinksForTask(task.getId());

        for (HistoricIdentityLink link : links) {
            System.out.println("link.getUserId() = " + link.getUserId());
        }
    }


    @Test
    public void test20(){
        // ???????????????????????????  ????????????
        List<HistoricProcessInstance> list = historyService.createNativeHistoricProcessInstanceQuery().sql("").list();

        for (HistoricProcessInstance historicProcessInstance : list) {
            // ????????????id ?????? ??????????????? task
            List<HistoricTaskInstance> taskInstanceList = historyService.createNativeHistoricTaskInstanceQuery().sql("").parameter("", "").list();

        }
    }
    
    @Test
    public void test21(){
        
    }

}
