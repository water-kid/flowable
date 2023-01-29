package com.cj.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Map;
// 审批通过的服务类
public class ApproveServiceTask implements JavaDelegate {
    private static  final Logger logger = LoggerFactory.getLogger(ApproveServiceTask.class);
    @Override
    public void execute(DelegateExecution execution) {
        //  获取流程中所有变量
        Map<String, Object> variables = execution.getVariables();
        logger.info("{} --请假{}天的申请通过",variables.get("name"),variables.get("days"));
    }
}
