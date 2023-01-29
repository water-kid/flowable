package com.cj;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class ServiceTaskTest {

    @Test
    public void test03(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            repositoryService.deleteDeployment(deployment.getId());
        }
    }

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Test
    public void test01() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("processes/InclusiveGatewayDemo.bpmn20.xml");
        // 部署
        repositoryService.createDeployment()
                .name("deployment的name")
                .category("category")
                .key("key字段")
                .addInputStream(classPathResource.getFilename(),classPathResource.getInputStream()).deploy();
    }
    
    @Test
    public void test02(){


        HashMap<String, Object> map = new HashMap<>();
        map.put("money",1000);
        runtimeService.startProcessInstanceByKey("InclusiveGatewayDemo", map);
    }
    @Test
    public void test06(){
        taskService.complete("5f92c1e6-97ed-11ed-a182-f0b61e94ce8e");
    }

    @Autowired
    TaskService taskService;

    @Test
    public void test04(){
        List<Task> list = taskService.createTaskQuery().list();
        for (Task task : list) {
            taskService.complete(task.getId());
        }
    }
}
