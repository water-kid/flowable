package com.cj.service;

import com.cj.mapper.UserMapper;
import com.cj.model.Role;
import com.cj.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userMapper.loadUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        List<Role> roleList = userMapper.getRolesByUserId(user.getId());
        user.setRoleList(roleList);
        return user;
    }


    public List<User> getAllUsersExcludeCurrent() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.getAllUsersExcludeCurrent(user.getUsername());
    }
}
