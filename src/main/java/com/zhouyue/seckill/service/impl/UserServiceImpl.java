package com.zhouyue.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhouyue.seckill.mapper.UserMapper;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IUserService;
import com.zhouyue.seckill.utils.MD5Util;
import com.zhouyue.seckill.utils.ValidatorUtil;
import com.zhouyue.seckill.vo.LoginVo;
import com.zhouyue.seckill.vo.RespBean;
import com.zhouyue.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

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

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录
     * @param loginVo
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
//        if (ObjectUtils.isEmpty(mobile) || ObjectUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGINERROR);
//        }
//        if (ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILEERROR);
//        }
        User user = userMapper.selectById(mobile);
        if (user == null){
            return RespBean.error(RespBeanEnum.LOGINERROR);
        }
        //判断密码是否正确
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            return RespBean.error(RespBeanEnum.LOGINERROR);
        }
        return RespBean.success();
    }
}
