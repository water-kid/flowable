package com.cj;

import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.*;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.FormType;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.form.DateFormType;
import org.flowable.engine.impl.form.EnumFormType;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.form.api.FormInfo;
import org.flowable.form.api.FormModel;
import org.flowable.form.model.FormField;
import org.flowable.form.model.SimpleFormModel;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class TimedTask {

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    ManagementService managementService;

    @Test
    public void test07(){

        //  移动到 act_ru_deadletter 中 取消定时任务
        managementService.moveJobToDeadLetterJob("54a99b79-9b94-11ed-9a24-f0b61e94ce8e");

    }
    
    @Test
    public void test09(){
        // 移动 act_ru_deadletter_job  到  act_ru_job
        managementService.moveDeadLetterJobToExecutableJob("jobId",3);
    }

    @Test
    public void test08(){
        // 移动到 act_ru_job   定时任务立即执行
        managementService.moveTimerToExecutableJob("54a99b79-9b94-11ed-9a24-f0b61e94ce8e");
    }

    @Test
    public void test05(){
        // 定时挂起
        repositoryService.suspendProcessDefinitionByKey("HistoryDemo01",true,new Date(System.currentTimeMillis()+240*1000));

        // 定时激活
        repositoryService.activateProcessDefinitionByKey("HistoryDemo01",true,new Date(System.currentTimeMillis()));
    }

    
    @Test
    public void test06(){
        runtimeService.startProcessInstanceByKey("HistoryDemo01");
    }

    @Test
    public void test03() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("hehe/DynamicFormDemo.bpmn20.xml");
        repositoryService.createDeployment().addInputStream(classPathResource.getFilename(),classPathResource.getInputStream())
                // 定时激活
//        .activateProcessDefinitionsOn(new Date(System.currentTimeMillis()+120*1000))
                .deploy();
    }
    @Autowired
    FormService formService;



    @Test
    public void test11(){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("DynamicFormDemo").latestVersion().singleResult();
        // 开始节点上的 表单定义信息
        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        // 表单的属性
        List<FormProperty> formProperties = startFormData.getFormProperties();
        for (FormProperty fp : formProperties) {
            FormType type = fp.getType();
            Object info = "";
            // 对于日期和枚举类型，还可以获取该字段的额外信息
            if (type instanceof EnumFormType){
                // 枚举类型
                info = type.getInformation("values");
            }else  if (type instanceof DateFormType){
                // 日期类型
                info = type.getInformation("datePattern");
            }

            System.out.println(fp.getId()+"--"+fp.getName()+"--"+fp.getValue()+"--"+fp.getType().getName()+"--"+info);
        }

    }

    @Test
    public void test12(){

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("DynamicFormDemo").latestVersion().singleResult();

        // 将表单实例当作 普通变量对待
        HashMap<String, String> map = new HashMap<>();
        map.put("days","10");
        map.put("reason","hehe");
        map.put("startTime","2023-1-24");
        map.put("endTime","2023-2-24");
        map.put("type","askforleave");
        Authentication.setAuthenticatedUserId("ww");

        // 提交表单    表单提交会对提交的数据进行限制，，required，无效的enum
        formService.submitStartFormData(processDefinition.getId(),map);
    }

    @Autowired
    TaskService taskService;
    @Test
    public void test13(){
        Task task = taskService.createTaskQuery().singleResult();
        // 根据taskId 查找 表单属性
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());

        List<FormProperty> formProperties = taskFormData.getFormProperties();
        System.out.println("formProperties = " + formProperties.size());
        for (FormProperty fp : formProperties) {
            FormType type = fp.getType();
            Object info = "";
            // 对于日期和枚举类型，还可以获取该字段的额外信息
            if (type instanceof EnumFormType){
                // 枚举类型
                info = type.getInformation("values");
            }else  if (type instanceof DateFormType){
                // 日期类型
                info = type.getInformation("datePattern");
            }

            System.out.println(fp.getId()+"--"+fp.getName()+"--"+fp.getValue()+"--"+fp.getType().getName()+"--"+info);
        }
    }

    @Test
    public void test14(){
        Task task = taskService.createTaskQuery().singleResult();
        // 修改 task 的form .. 会进行数据校验 ，，， 日期填写错误不会报错，会存入null
        Map<String, String> map = new HashMap<>();
        map.put("days","20");
        map.put("reason","hehe");
        map.put("startTime","2023-1-24");
        map.put("endTime","2023-2-24");
        map.put("type","askforleave");
        // 保存表单数据
        formService.saveFormData(task.getId(),map);
    }

    @Test
    public void test27(){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExtFormDemo").latestVersion().singleResult();
        runtimeService.startProcessInstanceById(processDefinition.getId());
    }
    @Test
    public void test26(){
        Task task = taskService.createTaskQuery().singleResult();
        // 获取UserTask 上的表单信息
        FormInfo formInfo = taskService.getTaskFormModel(task.getId());
        String key = formInfo.getKey();
        String id = formInfo.getId();
        String name = formInfo.getName();
        String description = formInfo.getDescription();
        int version = formInfo.getVersion();
        System.out.println(key+"---"+name+"---"+description+"---"+version);

        // 获取表单的 字段
        SimpleFormModel formModel = (SimpleFormModel) formInfo.getFormModel();
        List<FormField> fields = formModel.getFields();
        for (FormField field : fields) {
            String type = field.getType();
            Object value = field.getValue();
            String name1 = field.getName();
            String id1 = field.getId();
            String placeholder = field.getPlaceholder();
            System.out.println(id1+"---"+name1+"---"+value+"---"+type+"---"+placeholder);
        }
    }
    
    @Test
    public void test21() throws IOException {

        DeploymentBuilder builder = repositoryService.createDeployment().name("名字").category("分类").key("自定义工作流的key");
        // 必须是设置了 xxx.form 的 流程，启动才不会报错
        ClassPathResource ext = new ClassPathResource("hehe/ExtFormDemo01.bpmn20.xml");
        ClassPathResource form = new ClassPathResource("forms/application_form.form");
//        ClassPathResource leaderApproval = new ClassPathResource("hehe/leader_approval.html");
        // 流程实例 和 外置表单一起部署，，使用同一个  部署id
        builder.addInputStream(ext.getFilename(),ext.getInputStream());
//                .addInputStream(askLeave.getFilename(),askLeave.getInputStream())
//                .addInputStream(leaderApproval.getFilename(),leaderApproval.getInputStream());
//                    .addInputStream(form.getFilename(),form.getInputStream());

        builder.deploy();

    }
    
    
    @Test
    public void test28(){

    }

    @Test
    public void test22(){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExtFormDemo").singleResult();
        // 查询启动节点上，，外置表单的key
        String startFormKey = formService.getStartFormKey(processDefinition.getId());
        System.out.println("startFormKey = " + startFormKey);

        // 查询启动节点上，渲染之后的流程表单，，这个方法主要针对外置表单来使用
        String renderedStartForm = (String) formService.getRenderedStartForm(processDefinition.getId());
        System.out.println("renderedStartForm = " + renderedStartForm);

    }


    @Test
    public void test23(){
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("ExtFormDemo").latestVersion().singleResult();
        Map<String, String> map = new HashMap<>();
        map.put("days","30");
        map.put("reason","hehe");
        map.put("startTime","2023-1-24");
        map.put("endTime","2023-2-24");
        // 开启一个流程，，传入表单数据
        formService.submitStartFormData(processDefinition.getId(),map);
    }

    @Test
    public void test24(){
        Task task = taskService.createTaskQuery().singleResult();
        // 获取被渲染后的 外置表单,,这里会自动读取流程变量，并且将表单中的值渲染出来
        String renderedTaskForm = (String) formService.getRenderedTaskForm(task.getId());
        System.out.println("renderedTaskForm = " + renderedTaskForm);
    }

    @Test
    public void test25(){
        Task task = taskService.createTaskQuery().singleResult();
        Map<String, String> map = new HashMap<>();
        map.put("days","40");
        map.put("reason","hehe");
        map.put("startTime","2023-1-24");
        map.put("endTime","2023-2-24");
        // 流程审批 并修改表单内容
        formService.submitTaskFormData(task.getId(),map);
    }

    @Test
    public void test04(){
        Date date = new Date(System.currentTimeMillis());
        System.out.println("date = " + date);
    }
    @Test
    public void test01(){
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        for (ProcessInstance processInstance : list) {
            runtimeService.deleteProcessInstance(processInstance.getId(),"no reason");
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
