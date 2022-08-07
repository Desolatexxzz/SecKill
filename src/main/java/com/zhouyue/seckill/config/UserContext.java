package com.zhouyue.seckill.config;

import com.zhouyue.seckill.pojo.User;

public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}
