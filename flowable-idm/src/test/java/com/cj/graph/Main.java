package com.cj.graph;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class Main {

    @Autowired
    RuntimeService runtimeService;
    @Autowired
    RepositoryService repositoryService;


    @Test
    public void test04() throws IOException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExclusiveGatewayDemo").latestVersion().singleResult();
        // bpmn : business process modeling notation
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        System.out.println("bpmnModel = " + bpmnModel);

        // 图形生成器
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();


        /**
         * param 1: 绘制历程图的对象，，，相当于 act_ge_bytearray 中的 xml文件
         * param 2： 缩放因子
         * param 3：是否在绘制线的时候，绘制对应的文本
         */
//        InputStream inputStream = generator.generatePngDiagram(bpmnModel, 1, true);
//        generator.generateDiagram(bpmnModel,"png",new ArrayList<>(),)

//        FileUtils.copyInputStreamToFile(inputStream,new File("C:\\Users\\Administrator\\Desktop\\image\\1.png"));

    }


    @Test
    public void test05() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("graph/hehe.txt");
//        System.out.println(classPathResourc);
        FileUtils.copyInputStreamToFile(classPathResource.getInputStream(),new File("C:\\Users\\Administrator\\Desktop\\image\\2.txt"));
    }
    @Test
    public void test03() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("graph/ExclusiveGatewayDemo.bpmn20.xml");
        repositoryService.createDeployment().addInputStream(classPathResource.getFilename(),classPathResource.getInputStream()).deploy();
    }

    @Test
    public void test01(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        System.out.println("list.size() = " + list.size());
        for (Execution execution : list) {
            runtimeService.deleteProcessInstance(execution.getId(),"123");
        }
    }
    
    @Test
    public void test02(){
        List<Deployment> list = repositoryService.createDeploymentQuery().list();
        for (Deployment deployment : list) {
            repositoryService.deleteDeployment(deployment.getId());
        }
    }
}
