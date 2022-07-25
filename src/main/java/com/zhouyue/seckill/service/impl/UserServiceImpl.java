package com.zhouyue.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyue.seckill.mapper.UserMapper;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
