package com.cj;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class VariableTest {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Test
    public void test01() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("hehe/VariableDemo.bpmn20.xml");
        repositoryService.createDeployment().addInputStream(classPathResource.getFilename(),classPathResource.getInputStream()).deploy();

        
    }
    
    @Test
    public void test02(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("reason","no reason");
        map.put("startTime","2023-1-20");
        map.put("endTime","2023-1-21");
        runtimeService.startProcessInstanceByKey("VariableDemo", map);
    }
    
    @Test
    public void test03(){
        // 根据 执行实例id 查找变量  act_ru_variable
        List<Execution> list = runtimeService.createExecutionQuery().list();
        System.out.println("list.size() = " + list.size());
        for (Execution execution : list) {
            // 查找指定变量
            Object startTime = runtimeService.getVariable(execution.getId(), "startTime");
            System.out.println(execution.getName()+"==========="+startTime);

            Map<String, Object> map = runtimeService.getVariables(execution.getId());
            System.out.println("map = " + map);
        }
    }


    @Autowired
    TaskService taskService;
    @Test
        public void test04(){
        // task插入 也是 act_ru_variable  和 act_hi_varinst
        //  插入的时候 task_id 是 null，，， 设置全局流程变量
        Task task = taskService.createTaskQuery().taskAssignee("cc").singleResult();
        taskService.setVariable(task.getId(),"hehe","123");
        HashMap<String, Object> map = new HashMap<>();
        map.put("birthday","11-02");
        taskService.setVariables(task.getId(), map);
    }

    @Test
    public void test05(){
        // 根据task 查找变量： 根据taskId 找 执行实例Id ==》atc_ru_variable变量
        Task task = taskService.createTaskQuery().taskAssignee("cc").singleResult();
        Object birthday = taskService.getVariable(task.getId(), "birthday");
        System.out.println("birthday = " + birthday);
        Map<String, Object> map = taskService.getVariables(task.getId());
        System.out.println("map = " + map);

    }

    @Test
    public void test06(){
        Task task = taskService.createTaskQuery().taskAssignee("cc").singleResult();
        HashMap<String, Object> map = new HashMap<>();
        map.put("status","complete");
        taskService.complete(task.getId(), map);
    }


    @Test
    public void test07(){
        // 流程变量 ： 是跟当前 流程实例  绑定的
        List<Execution> list = runtimeService.createExecutionQuery().list();
        for (Execution execution : list) {
            runtimeService.setVariable(execution.getId(),"a","b");
        }
    }
    
    @Test
    public void test08(){
        Task task = taskService.createTaskQuery().taskAssignee("cc").singleResult();
        taskService.setVariableLocal(task.getId(),"sb","123");
    }

    @Test
    public void test09(){
//        List<Execution> list = runtimeService.createExecutionQuery().list();
//        for (Execution execution : list) {
//            Object sb = runtimeService.getVariable(execution.getId(), "sb");
//            System.out.println("sb = " + sb);
//        }

        Task task = taskService.createTaskQuery().taskAssignee("cc").singleResult();
//        // 涉及到多个sql
//        Object sb = taskService.getVariable(task.getId(), "sb");
//        System.out.println("sb = " + sb);
//        //
//        Object sb1 = taskService.getVariableLocal(task.getId(), "sb");

        // 完成流程实例去设置  本地流程变量
        HashMap<String, Object> map = new HashMap<>();
        map.put("status","完成");
        taskService.complete(task.getId(), map,true);
    }

    @Test
    public void test11(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            repositoryService.deleteDeployment(deployment.getId());
        }
    }

    @Test
    public void test12() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("hehe/VariableDemo02.bpmn20.xml");
        repositoryService.createDeployment().addInputStream(classPathResource.getFilename(),classPathResource.getInputStream()).deploy();

        runtimeService.startProcessInstanceByKey("VariableDemo");
    }
    @Test
    public void test13(){
        Task zs = taskService.createTaskQuery().taskAssignee("zs").singleResult();  // 08290429-98bd-11ed-95b1-f0b61e94ce8e
        System.out.println("zs.getExecutionId() = " + zs.getExecutionId()); //  08290429-98bd-11ed-95b1-f0b61e94ce8e
        Task cc = taskService.createTaskQuery().taskAssignee("cc").singleResult();
        System.out.println("cc.getExecutionId() = " + cc.getExecutionId());
    }


    @Test
    public void test14(){
        Task zs = taskService.createTaskQuery().taskAssignee("zs").singleResult();
        taskService.complete(zs.getId());

        Task cc = taskService.createTaskQuery().taskAssignee("cc").singleResult();
        System.out.println("cc.getExecutionId() = " + cc.getExecutionId());
    }


    @Test
    public void test15 (){
        Task cc = taskService.createTaskQuery().taskAssignee("cc").singleResult();
//        runtimeService.setVariableLocal(zs.getExecutionId(),"sb","123");
        taskService.complete(cc.getId());
    }
    
    @Test
    public void test16(){
        runtimeService.createProcessInstanceBuilder()
                // 设置临时变量
                .transientVariable("days",10)
                .transientVariables(new HashMap<String,Object>())
                .processDefinitionKey("VariableDemo")
                .start();
    }
    
    @Test
    public void test17(){
        Task task = taskService.createTaskQuery().taskAssignee("zs").singleResult();

        HashMap<String, Object> transientVariables = new HashMap<>();
        transientVariables.put("hehe","123");
        taskService.complete(task.getId(),null, transientVariables);
    }

    @Test
    public void test18(){
        Task task = taskService.createTaskQuery().taskAssignee("cc").singleResult();
        Object days = runtimeService.getVariable(task.getExecutionId(), "days");
        System.out.println("days = " + days);

        List<Execution> list = runtimeService.createExecutionQuery().list();
        System.out.println("list = " + list.size());
//        runtimeService.getVariable()
        for (Execution execution : list) {
            Object days1 = runtimeService.getVariable(execution.getId(), "days");
            System.out.println("days1 = " + days1);
        }
    }
}
