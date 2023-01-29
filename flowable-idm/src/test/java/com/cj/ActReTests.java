package com.cj;

import org.flowable.engine.IdentityService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ActReTests {

    // 查询流程定义
    @Autowired
    RepositoryService repositoryService;

    @Test
    void contextLoads() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
//        for (ProcessDefinition processDefinition : list) {
//
//        }

        // 查看最新版本
        List<ProcessDefinition> list1 = repositoryService.createProcessDefinitionQuery().latestVersion().list();
        for (ProcessDefinition processDefinition : list1) {
            System.out.println("processDefinition.getName() = " + processDefinition.getId());
        }

        List<ProcessDefinition> list2 = repositoryService.createProcessDefinitionQuery().processDefinitionKey("").desc().list();

        List<ProcessDefinition> list3 = repositoryService.createNativeProcessDefinitionQuery().sql("").parameter("", "").list();
    }


    @Test
    public void test02(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
//        repositoryService.createDeploymentQuery().deploymentCategory("")

        // 根据 流程部署的id  查找流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId("").singleResult();


        List<Deployment> list1 = repositoryService.createNativeDeploymentQuery().sql("").parameter("", "").list();
    }

    @Test
    public void test03(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            // 删除 。。涉及到流程定义的表，都会被删除
            repositoryService.deleteDeployment(deployment.getId());
        }
    }



}
