package com.cj.mapper;

import com.cj.model.Role;
import com.cj.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User loadUserByUsername(String username);

    List<Role> getRolesByUserId(Integer id);


    List<User> getAllUsersExcludeCurrent(String username);
}
