package com.cj.graph;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class ProcessImageTest {

    @Autowired
    RepositoryService repositoryService;

    @Test
    public void test02(){
        System.out.println("repositoryService = " + repositoryService);
//        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();
//        System.out.println("list.size() = " + list.size());
    }

    @Test
    public void test01() throws IOException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExclusiveGatewayDemo").latestVersion().singleResult();

        // 画图的主要参数     bpmn： business  process modeling  notation
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        // 绘制图片生成器
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        /**
         * 1. 绘制流程图对象： xml的内容都在这里面
         * 2. 缩放因子
         * 3. 绘制连接线的时候是否绘制文本
         */
        InputStream inputStream = generator.generatePngDiagram(bpmnModel, 1.0, true);

//        FileUtils.copyInputStreamToFile(inputStream,new File("C:\\Users\\Administrator\\Desktop\\cj\\111.png"));
        FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\cj\\123.png");

        int len = 0;
        byte[] buffer = new byte[1024];

        while ((len = inputStream.read(buffer)) != -1){
            out.write(buffer,0,len);
        }

        inputStream.close();
        out.close();
    }
    
    
    @Test
    public void test03() throws IOException {
        FileInputStream inputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\cj\\1.png");
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\cj\\123.png");
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer))!=-1){
            outputStream.write(buffer,0,len);
        }

    }
    
    
    @Test
    public void test05() throws IOException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExclusiveGatewayDemo").latestVersion().singleResult();

        // 画图的主要参数     bpmn： business  process modeling  notation
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        // 绘制图片生成器
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();


        // 所有已经执行的活动的id，，保存在这个集合
        ArrayList<String> highLightedActivities = new ArrayList<>();

        // 所有已经执行过的线条
        ArrayList<String> highLightedFlows = new ArrayList<>();


        // 查询当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().singleResult();

        if (processInstance == null){
            // 没有流程执行
            return;
        }

        // 查找所有活动信息
        List<ActivityInstance> list = runtimeService.createActivityInstanceQuery().processInstanceId(processInstance.getId()).list();


        for (ActivityInstance ai : list) {
            if (ai.getActivityType().equals("sequenceFlow")){
                // 是连线
                highLightedFlows.add(ai.getActivityId());
            }else{
                highLightedActivities.add(ai.getActivityId());
            }
        }


//        InputStream inputStream = generator.generateDiagram(bpmnModel, "png", highLightedActivities, highLightedFlows, 1.0, true);

        InputStream inputStream = generator.generateDiagram(bpmnModel, "png", highLightedActivities, highLightedFlows, "宋体","宋体","宋体",null,1.0,true);

        FileUtils.copyInputStreamToFile(inputStream,new File("C:\\Users\\Administrator\\Desktop\\cj\\111.png"));

        inputStream.close();
    }

    @Autowired
    RuntimeService runtimeService;

    @Test
    public void test04(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("days",1);
        runtimeService.startProcessInstanceByKey("ExclusiveGatewayDemo", map);

    }
    
//    @Test
//    public void test05(){
//        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExclusiveGatewayDemo").latestVersion().singleResult();
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
//        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processDefinitionKey("ExclusiveGatewayDemo").singleResult();
//        List<ActivityInstance> list = runtimeService.createActivityInstanceQuery().processInstanceId(processInstance.getId()).list();
//
//
//    }

    @Autowired
    TaskService taskService;

    @Test
    public void test06(){
        List<Task> list = taskService.createTaskQuery().list();
        for (Task task : list) {
            taskService.complete(task.getId());
        }
    }


    @Autowired
    HistoryService historyService;

    @Test
    public void test07() throws IOException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExclusiveGatewayDemo").latestVersion().singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        // act_hi_procinst
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processDefinitionKey("ExclusiveGatewayDemo").singleResult();

        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list();
        ArrayList<String> activities = new ArrayList<>();
        ArrayList<String> flows = new ArrayList<>();
        for (HistoricActivityInstance historicActivityInstance : list) {
            if (historicActivityInstance.getActivityType().equals("sequenceFlow")){
                flows.add(historicActivityInstance.getActivityId());
            }else{
                activities.add(historicActivityInstance.getActivityId());
            }
        }
        InputStream inputStream = generator.generateDiagram(bpmnModel, "png", activities, flows,"宋体","宋体","宋体",null, 1.0, true);

        FileUtils.copyInputStreamToFile(inputStream,new File("C:\\Users\\Administrator\\Desktop\\cj\\333.png"));
        inputStream.close();


    }

}
