package com.cj.service;

import com.cj.mapper.UserMapper;
import com.cj.model.AskForLeaveVO;
import com.cj.model.RespBean;
import com.cj.model.TaskVO;
import com.cj.model.User;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class AskForLeaveService {
    @Autowired
    RuntimeService runtimeService;

    @Transactional
    public RespBean askForLeave(AskForLeaveVO askForLeaveVO) {

     try {
         // 谁登录，谁是申请人
         User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

         HashMap<String, Object> map = new HashMap<>();
         map.put("days",askForLeaveVO.getDays());
         // 要和流程图中定义的变量一致
         map.put("approveUser",askForLeaveVO.getApproveUser());
         map.put("endTime",askForLeaveVO.getEndTime());
         map.put("startTime",askForLeaveVO.getStartTime());
         map.put("reason",askForLeaveVO.getReason());
         // 申请人
         map.put("applicant",user.getUsername());
         ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("askforleave_demo", map);

         // 正常执行，流程开启成功
         return RespBean.ok("请假申请已提交");
     }catch (Exception e){
            e.printStackTrace();
     }

        return RespBean.error("请假申请提交失败");
    }



    @Autowired
    TaskService taskService;

    public List<AskForLeaveVO> getunapproveProcess() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 当前用户发起的  所有的执行实例
        List<Execution> list = runtimeService.createExecutionQuery().startedBy(user.getUsername()).list();

        ArrayList<AskForLeaveVO> askForLeaveList = new ArrayList<>();
        for (Execution execution : list) {
            // 获取 提交的信息
            String executionId = execution.getId();
            Integer days = (Integer) runtimeService.getVariable(executionId, "days");
            String reason = (String) runtimeService.getVariable(executionId, "reason");
            Date startTime = (Date) runtimeService.getVariable(executionId, "startTime");
            Date endTime = (Date) runtimeService.getVariable(executionId, "endTime");
            String approveUser = (String) runtimeService.getVariable(executionId, "approveUser");

            askForLeaveList.add(new AskForLeaveVO(days,reason,endTime,startTime,approveUser,execution.getProcessInstanceId()));
        }
        return askForLeaveList;
    }

    @Autowired
    RepositoryService repositoryService;
    public void processImage(String processId, HttpServletResponse response) throws IOException {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey("askforleave_demo").latestVersion().singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

        ArrayList<String> activities = new ArrayList<>();
        ArrayList<String> flows = new ArrayList<>();

        // historyService 里面包括了： 执行完的和没有执行完的
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).list();
//        List<ActivityInstance> list = runtimeService.createActivityInstanceQuery().processInstanceId(processId).list();
        for (HistoricActivityInstance activityInstance : list) {
            if (activityInstance.getActivityType().equals("sequenceFlow")){
                // 连线
                flows.add(activityInstance.getActivityId());
            }else {
                activities.add(activityInstance.getActivityId());
            }
        }

        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();

        InputStream inputStream = generator.generateDiagram(bpmnModel, "png", activities, flows, "宋体", "宋体", "宋体", null, 1.0, true);


        // 设置contentType，，告诉返回的是一张图片
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        // 将输入流 写到输出流，，返回输出字节数
        FileCopyUtils.copy(inputStream,response.getOutputStream());
    }

    public List<TaskVO> getCurrentUserAllTask() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Task> list = taskService.createTaskQuery().taskAssignee(user.getUsername()).list();

        ArrayList<TaskVO> taskVOList = new ArrayList<>();
        for (Task task : list) {
            Integer days = (Integer) taskService.getVariable(task.getId(), "days");
            String reason = (String) taskService.getVariable(task.getId(), "reason");
            Date startTime = (Date) taskService.getVariable(task.getId(), "startTime");
            Date endTime = (Date) taskService.getVariable(task.getId(), "endTime");
            // 申请人 : 谁提出的申请
            String applicant = (String) taskService.getVariable(task.getId(), "applicant");

            TaskVO taskVO = new TaskVO(days,reason,endTime,startTime,user.getUsername(),task.getId(),null,applicant);
            taskVOList.add(taskVO);
        }
        return taskVOList;
    }

    public RespBean approve(TaskVO taskVO) {
    try{
        HashMap<String, Object> map = new HashMap<>();
        // 流程图设置的变量 approval
        map.put("approval",taskVO.getApproval());
        taskService.complete(taskVO.getTaskId(), map);
        return RespBean.ok("审批成功");
    }catch (Exception e){
        e.printStackTrace();
    }
        return RespBean.error("审批失败");
    }

    @Autowired
    HistoryService historyService;

    public List<AskForLeaveVO> getHistoryProcess() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // .finished() 已经处理完了的
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().startedBy(user.getUsername()).finished().list();

        ArrayList<AskForLeaveVO> askForLeaveList = new ArrayList<>();

        for (HistoricProcessInstance historicProcessInstance : list) {
            // 获取变量
            List<HistoricVariableInstance> list1 = historyService.createHistoricVariableInstanceQuery().processInstanceId(historicProcessInstance.getId()).list();

            AskForLeaveVO askForLeaveVO = new AskForLeaveVO();
            for (HistoricVariableInstance historicVariableInstance : list1) {
                String variableName = historicVariableInstance.getVariableName();
                Object value = historicVariableInstance.getValue();
                askForLeaveVO.setProcessId(historicProcessInstance.getId());
                if ("days".equals(variableName)){
                    askForLeaveVO.setDays((Integer) value);
                }else if ("reason".equals(variableName)){
                    askForLeaveVO.setReason((String) value);
                }else if ("startTime".equals(variableName)){
                    askForLeaveVO.setStartTime((Date) value);

                }else if ("endTime".equals(variableName)){
                    askForLeaveVO.setEndTime((Date) value);
                }else if ("approveUser".equals(variableName)){
                    askForLeaveVO.setApproveUser((String) value);
                }else if ("approval".equals(variableName)){
                    askForLeaveVO.setApproval((Boolean) value);
                }

            }

            askForLeaveList.add(askForLeaveVO);

        }
        return askForLeaveList;
    }
}
