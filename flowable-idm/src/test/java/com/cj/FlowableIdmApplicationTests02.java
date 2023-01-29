package com.cj;

import org.flowable.common.engine.api.management.TableMetaData;
import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.IdmManagementService;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootTest
class FlowableIdmApplicationTests02 {

    // 用户相关的操作，，添加，删除，修改用户，用户组等
    @Autowired
    IdentityService identityService;


    @Autowired
    IdmManagementService idmManagementService;
    @Test
    void contextLoads() {
        // 获取系统信息
        Map<String, String> properties = idmManagementService.getProperties();
        Set<String> key = properties.keySet();
        for (String s : key) {
            System.out.println(s+"--------"+properties.get(s));
        }

        // 获取表的信息
        String groupTableName = idmManagementService.getTableName(Group.class);
        System.out.println("groupTableName = " + groupTableName);

        TableMetaData tableMetaData = idmManagementService.getTableMetaData(groupTableName);
        System.out.println(tableMetaData.getColumnNames());
        System.out.println(tableMetaData.getColumnTypes());
        String tableName = tableMetaData.getTableName();


    }





}
