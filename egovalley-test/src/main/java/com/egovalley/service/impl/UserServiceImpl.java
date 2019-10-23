package com.egovalley.service.impl;

import com.egovalley.domain.EgoUser;
import com.egovalley.mapper.UserMapper;
import com.egovalley.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public EgoUser doLogin(String username, String password) {
        return userMapper.selectUserByUsernameAndPassword(username, password);
    }

    /**
     * 用户注册
     * @param egoUser
     */
    @Override
    public void doRegister(EgoUser egoUser) {
        userMapper.insertUser(egoUser);
    }

}
