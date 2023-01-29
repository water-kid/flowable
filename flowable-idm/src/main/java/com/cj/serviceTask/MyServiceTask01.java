package com.cj.serviceTask;


        import org.flowable.common.engine.api.delegate.Expression;
        import org.flowable.engine.delegate.DelegateExecution;
        import org.flowable.engine.delegate.JavaDelegate;
        import org.springframework.stereotype.Component;

@Component
public class MyServiceTask01 implements JavaDelegate {


    Expression username;
    @Override
    public void execute(DelegateExecution execution) {
//        // 获取username的值
//        System.out.println("username.getExpressionText() = " + username.getExpressionText());
//        System.out.println("username.getValue(execution) = " + username.getValue(execution));
        System.out.println("======================");
    }
}
