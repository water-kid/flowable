package com.cj.listener;

import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;

public class MyTaskListener implements TaskListener {

    // 任务被创建的时候，这个方法会被触发
    @Override
    public void notify(DelegateTask delegateTask) {
        // 相当于任务的代理
        // 设置任务处理人
//        delegateTask.setAssignee("zl");

        // 监听器设置委托人
        delegateTask.addCandidateUser("zs");
        delegateTask.addCandidateUser("ww");
    }
}
