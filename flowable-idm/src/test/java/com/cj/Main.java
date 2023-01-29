package com.cj;

import org.apache.ibatis.io.Resources;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class Main {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Test
    public void test02(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        for (ProcessInstance processInstance : list) {

            runtimeService.deleteProcessInstance(processInstance.getId(),"no reason");
        }
    }

    @Test
    public void test03(){
                List<Deployment> list = repositoryService.createDeploymentQuery().list();
                for (Deployment deployment : list) {
                    repositoryService.deleteDeployment(deployment.getId());
                }
    }

    @Test
    public void test01() throws IOException {

        ClassPathResource classPathResource = new ClassPathResource("processes/UserTaskDemo02.bpmn20.xml");
        String filename = classPathResource.getFilename();

        // 部署
        Deployment deploy = repositoryService.createDeployment()
                .name("deployment的name")
                .category("category")
                .key("key字段")
                .addInputStream(filename, classPathResource.getInputStream()).deploy();

    }

    @Test
    public void test16(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("group","manager");
        runtimeService.startProcessInstanceByKey("UserTaskDemo", map);
    }

    @Test
    public void test15(){
//            runtimeService.startProcessInstanceByKey("UserTaskDemo");

        List<Task> list = taskService.createTaskQuery().taskCandidateUser("ww").list();
        for (Task task : list) {
            System.out.println(task.getName());
        }

        List<Task> list1 = taskService.createTaskQuery().taskCandidateGroup("manager").list();
        for (Task task : list1) {
            System.out.println(task.getName());
            taskService.complete(task.getId());
        }
    }

@Test
public void test12(){
    HashMap<String, Object> map = new HashMap<>();
    map.put("userIds","cc,zs,ww");
    runtimeService.startProcessInstanceByKey("UserTaskDemo", map);

//    List<Task> list = taskService.createTaskQuery().taskCandidateUser("ww").list();
//
//    for (Task task : list) {
//        taskService.claim(task.getId(),"ww");
//    }

//    List<Task> list = taskService.createTaskQuery().taskAssignee("zs").list();
//    for (Task task : list) {
//        // 回退任务   就是将 assignee 设置为 null
//        taskService.setAssignee(task.getId(),null);
//        // 认领任务
//        taskService.claim(task.getId(),"zs");
//        taskService.complete(task.getId());
//    }
//    List<Task> list = taskService.createTaskQuery().taskCandidateUser("ww").list();
//    for (Task task : list) {
//        taskService.claim(task.getId(),"ww");
//        taskService.complete(task.getId());
//    }
//    taskService.claim();
}


@Test
public void test14(){
    UserEntityImpl user = new UserEntityImpl();
    user.setRevision(0);
    user.setEmail("zs@qq.com");
    user.setPassword("123");
    user.setId("zs");
    identityService.saveUser(user);

    UserEntityImpl user1 = new UserEntityImpl();
    user1.setRevision(0);
    user1.setEmail("cc@qq.com");
    user1.setPassword("123");
    user1.setId("cc");
    identityService.saveUser(user1);


    UserEntityImpl user2 = new UserEntityImpl();
    user2.setRevision(0);
    user2.setEmail("ww@qq.com");
    user2.setPassword("123");
    user2.setId("ww");
    identityService.saveUser(user2);

    UserEntityImpl user3 = new UserEntityImpl();
    user3.setRevision(0);
    user3.setEmail("ls@qq.com");
    user3.setPassword("123");
    user3.setId("ls");
    identityService.saveUser(user3);


    GroupEntityImpl groupEntity = new GroupEntityImpl();
    groupEntity.setRevision(0);
    groupEntity.setName("经理");
    groupEntity.setId("manager");
    identityService.saveGroup(groupEntity);

    identityService.createMembership("ww","manager");
    identityService.createMembership("ls","manager");

//    identityService.saveUser(user);
}

@Test
public void test13(){
    List<Task> list = taskService.createTaskQuery().list();
    for (Task task : list) {
        // 添加候选人
//        taskService.addCandidateUser(task.getId(),"ls");
        // 删除候选人
        taskService.deleteCandidateUser(task.getId(),"ls");
    }
}

    @Autowired
    IdentityService identityService;

    @Test
    public void test08(){
        // 指定发起人
        Authentication.setAuthenticatedUserId("cc");
//        identityService.setAuthenticatedUserId("cc");
        runtimeService.startProcessInstanceByKey("UserTaskDemo");
    }

    @Test
    public void test09(){
        // 根据候选人查询任务   act_ru_identitylink
        List<Task> list = taskService.createTaskQuery().taskCandidateUser("cc").list();
        System.out.println("list.size() = " + list.size());
        for (Task task : list) {
            // 查到任务还需要认领任务，，， 认领的本质就是 act_ru_task 的 assignee 设置值
            // 认领任务
            taskService.claim(task.getId(),"zs");
            // 委派给其他人
            taskService.setAssignee(task.getId(),"ls");
        }
    }
    @Test
    public void test11(){
        List<Task> list = taskService.createTaskQuery().taskAssignee("zs").list();
        for (Task task : list) {
            taskService.complete(task.getId());
        }
    }
    
    @Test
    public void test10(){
        // 根据流程实例  查找对应的  执行用户
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("7dac8e6f-97a2-11ed-a9c5-f0b61e94ce8e").singleResult();
        List<IdentityLink> list = runtimeService.getIdentityLinksForProcessInstance(processInstance.getId());
        for (IdentityLink identityLink : list) {
            System.out.println(identityLink.getUserId());
        }

    }




    @Test
    public void test07(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("manager","ww");
        // map传递变量进去
        runtimeService.startProcessInstanceByKey("UserTaskDemo", map);
    }


    @Autowired
    TaskService taskService;

            @Test
            public void test06(){

                // UserTask 存在  act_ru_task

                List<Task> list = taskService.createTaskQuery().taskAssignee("cc").list();
                for (Task task : list) {
                    //  处理掉   或者  交给别人处理
//                    taskService.complete(task.getId());

                    // 为某一个task 设置 处理人
                    taskService.setAssignee(task.getId(),"zs");

                }
            }



    @Test
    public void test04(){
        Authentication.setAuthenticatedUserId("ww");
        ProcessInstance receiveTaskDemo = runtimeService.startProcessInstanceByKey("UserTaskDemo");
    }
    
    @Test
    public void test05(){
        // 得到这个 ReceiveTask
        List<Execution> list = runtimeService.createExecutionQuery().activityId("send_to_boss").list();

        for (Execution execution : list) {
            // 触发 ReceiveTask
            runtimeService.trigger(execution.getId());
        }
    }


}
