package com.cj.controller;

import org.apache.commons.io.FileUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloController {
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @GetMapping("/download")
    public void download() throws IOException {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExclusiveGatewayDemo").latestVersion().singleResult();

        // bpmn  :  business process modeling notation
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        //
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processDefinitionKey("ExclusiveGatewayDemo").singleResult();
        List<ActivityInstance> list = runtimeService.createActivityInstanceQuery().processInstanceId(processInstance.getId()).list();
        ArrayList<String> activities = new ArrayList<>();
        ArrayList<String> flows = new ArrayList<>();
        for (ActivityInstance activityInstance : list) {
            if (activityInstance.getActivityType().equals("sequenceFlow")){
                flows.add(activityInstance.getActivityId());
            }else{
                activities.add(activityInstance.getId());
            }
        }
//        new ArrayList<>()

//        InputStream inputStream = generator.generatePngDiagram(bpmnModel, 1.0, true);
        InputStream inputStream =  generator.generateDiagram(bpmnModel,"png",activities,flows,"宋体","宋体","宋体",null,1.0,true);

        FileUtils.copyInputStreamToFile(inputStream,new File("C:\\Users\\Administrator\\Desktop\\cj\\123.png"));
        inputStream.close();
    }
}
