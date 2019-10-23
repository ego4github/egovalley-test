package com.egovalley.mapper;

import com.egovalley.domain.EgoUser;

public interface UserMapper {

    /**
     * 用户登录
     * @param username
     * @param password
     * @return
     */
    EgoUser selectUserByUsernameAndPassword(String username, String password);

    /**
     * 用户注册
     * @param egoUser
     */
    void insertUser(EgoUser egoUser);

}
