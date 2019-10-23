package com.egovalley.service;

import com.egovalley.domain.EgoUser;

import java.util.List;

public interface UserService {

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    EgoUser doLogin(String username, String password);

    /**
     * 用户注册
     * @param egoUser
     */
    void doRegister(EgoUser egoUser);

}
