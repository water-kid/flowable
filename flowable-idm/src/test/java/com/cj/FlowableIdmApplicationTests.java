package com.cj;

import org.flowable.engine.IdentityService;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class FlowableIdmApplicationTests {

    // 用户相关的操作，，添加，删除，修改用户，用户组等
    @Autowired
    IdentityService identityService;

    @Test
    void contextLoads() {
        UserEntityImpl user = new UserEntityImpl();
        user.setId("qq");
        user.setDisplayName("中文");
        user.setPassword("123");
        user.setFirstName("j");
        user.setLastName("c");
        // 添加用户 设置 为 0
        // flowable 的用户表使用了乐观锁，，revision是配合乐观锁的字段
        user.setRevision(0);
        identityService.saveUser(user);
    }


    @Test
    public void test01(){
        UserEntityImpl user = new UserEntityImpl();
        user.setId("zs");
        user.setPassword("666");
        user.setEmail("111@qq.com");

        // 修改的时候，确保revision 和数据库中 revision 一致，，默认的revision为1
        user.setRevision(2);
        // saveUser() 可以更新用户信息，但是不能更新密码，，每更新一次 revision 会自增 1
        identityService.saveUser(user);
    }
    
    @Test
    public void test02(){
        // 先查询后修改
        User user = identityService.createUserQuery().userId("cc").singleResult();
//        System.out.println("user = " + user.getEmail());
        user.setEmail("333@qq.com");
        user.setPassword("111");
        identityService.updateUserPassword(user);
//        identityService.saveUser(user);
    }

    @Test
    public void test03(){
        identityService.deleteUser("cc");
    }
    
    @Test
    public void test04(){
//        List<User> list = identityService.createUserQuery().userDisplayNameLike("%water%").list();
        List<User> list = identityService.createUserQuery().orderByUserId().desc().list();
        System.out.println("list = " + list);
        for (User user : list) {
            System.out.println(user.getId()+"--"+user.getDisplayName());
        }
    }

    @Test
    public void test05(){
        List<User> list = identityService.createNativeUserQuery().sql("select * from act_id_user where email_=#{email}").parameter("email", "hehe@qq.com").list();
        for (User user : list) {
            System.out.println(user.getId()+"--"+user.getDisplayName());
        }
    }


    @Test
    public void test06(){
        GroupEntityImpl group = new GroupEntityImpl();
        group.setName("组长");
        group.setId("cc");
        group.setRevision(0);
        identityService.saveGroup(group);
        
       
    }
    
    @Test
    public void test07(){
        // 删除组的时候，，还要删除 组和用户的关系 ACT_ID_MEMBERSHIP，，和组
//        identityService.deleteGroup("cc");
//        identityService.createNativeGroupQuery().sql("")

        // 创建组 和 用户 关系
        identityService.createMembership("cc","manager");
    }

    @Test
    public void test08(){
        // 更新之后再查询，，使用乐观锁 revision
//        Group group = identityService.createGroupQuery().groupId("manager").singleResult();
//        group.setName("CEO");
//        identityService.saveGroup(group);

        List<Group> list = identityService.createGroupQuery().groupName("CEO").list();
        for (Group group : list) {
            System.out.println(group.getId());
        }
    }

    @Test
    public void test09(){
        // 按照 用户 查找  用户组
//        List<Group> list = identityService.createGroupQuery().groupMember("cc").list();
//        for (Group group : list) {
//            System.out.println(group.getId()+"---"+group.getName());
//        }

        // 底层是 mybatis ，占位符使用 #{}
//        identityService.createNativeGroupQuery().sql("").parameter("")


    }
}
