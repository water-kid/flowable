package com.cj;

import com.cj.model.RespBean;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class ProcessDeployController {

    // 操作流程存储表  act_re_xxx
    @Autowired
    RepositoryService repositoryService;

    // procdef#category : 流程的分类，，xml中的 targetNameSpace
    // tenantId : 租户id
    @PostMapping("/deploy")
    public RespBean deploy(MultipartFile file,String tenantId) throws IOException {
        System.out.println("tenantId = " + tenantId);
        // act_re_deployment
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name("deployment的name字段")
                .category("实例的分类，deployment的category")
                .key("deployment的key字段")
                .tenantId(tenantId)
//                .addString()
                // 变成 ByteOutputStream#toByteArray
//                .addBytes()
                // 设置文件的输入流，通过这个输入流，自动读取文件
                // 资源名称不能随便取值，建议最好和文件名保持一致  ,,不然act_re_procdef 不添加数据
                .addInputStream(file.getOriginalFilename(), file.getInputStream());

//        System.out.println("file.getOriginalFilename() = " + file.getOriginalFilename());
//        System.out.println("file.getName() = " + file.getName());
        // 完成部署
        Deployment deploy = deploymentBuilder.deploy();
        return RespBean.ok("部署成功",deploy.getId());
    }

}
