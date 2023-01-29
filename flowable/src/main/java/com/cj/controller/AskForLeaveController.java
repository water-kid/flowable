package com.cj.controller;

import com.cj.model.AskForLeaveVO;
import com.cj.model.RespBean;
import com.cj.model.TaskVO;
import com.cj.model.User;
import com.cj.service.AskForLeaveService;
import com.cj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
public class AskForLeaveController {

    @Autowired
    AskForLeaveService askForLeaveService;

    @Autowired
    UserService userService;

    /**
     * 提交请假申请，，开启一个流程
     * @param askForLeaveVO
     */
    @PostMapping("/askForLeave")
    public RespBean askForLeave(@RequestBody AskForLeaveVO askForLeaveVO){
            return askForLeaveService.askForLeave(askForLeaveVO);
    }


    @GetMapping("/users")
    public List<User> getAllUsers(){
        return userService.getAllUsersExcludeCurrent();
    }

    /**
     * 当前登录用户发起的 流程实例
     * @return
     */
    @GetMapping("/unapprove")
    public List<AskForLeaveVO> getunapproveProcess(){
        return askForLeaveService.getunapproveProcess();
    }


    /**
     * 根据 流程实例id 返回图片
     * @param processId  流程实例id
     */
    @GetMapping("/image/{processId}")
    public void processImage(@PathVariable String processId, HttpServletResponse response) throws IOException {
        askForLeaveService.processImage(processId,response);
    }

    /**
     * 当前登录用户要处理的 task
     */
    @GetMapping("/tasks")
    public List<TaskVO> getCurrentUserAllTask(){
        return  askForLeaveService.getCurrentUserAllTask();
    }

    /**
     * 审批 task
     * @param taskVO  taskId ，，approval：是否同意
     * @return
     */
    @PostMapping("/approve")
    public RespBean approve(@RequestBody TaskVO taskVO){
        // 任务id ，， 是否同意
        return askForLeaveService.approve(taskVO);
    }


    /**
     * 历史的流程实例
     * @return
     */
    @GetMapping("/history")
    public List<AskForLeaveVO> getHistoryProcess(){
        return askForLeaveService.getHistoryProcess();
    }

}
