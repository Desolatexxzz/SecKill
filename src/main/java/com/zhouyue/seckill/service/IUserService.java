package com.zhouyue.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.vo.LoginVo;
import com.zhouyue.seckill.vo.RespBean;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zhouyue
 * @since 2022-07-24
 */
public interface IUserService extends IService<User> {

    /**
     * 登录
     * @param loginVo
     * @return
     */
    RespBean doLogin(LoginVo loginVo);
}
