package com.cj.task;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RejectServiceTask implements JavaDelegate {
    private static  final Logger logger = LoggerFactory.getLogger(RejectServiceTask.class);
    @Override
    public void execute(DelegateExecution execution) {
        Map<String, Object> variables = execution.getVariables();
        logger.error("{} 请假 {} 天，不通过",variables.get("name"),variables.get("days"));
    }
}
