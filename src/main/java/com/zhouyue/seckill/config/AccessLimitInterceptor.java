package com.zhouyue.seckill.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhouyue.seckill.pojo.User;
import com.zhouyue.seckill.service.IUserService;
import com.zhouyue.seckill.utils.CookieUtil;
import com.zhouyue.seckill.validator.AccessLimit;
import com.zhouyue.seckill.vo.RespBean;
import com.zhouyue.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
            User user = getUser(request, response);
            UserContext.setUser(user);
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null){
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if (needLogin){
                if (user == null){
                    render(response, RespBeanEnum.SESSIONERROR);
                }
                key += ":" + user.getId();
            }
            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(key);
            if (count == null){
                valueOperations.set(key, 1, second, TimeUnit.SECONDS);
            }else if (count < maxCount){
                valueOperations.increment(key);
            }else {
                render(response, RespBeanEnum.ACCESSLIMITREAHCED);
                return false;
            }

        }
        return true;

    }

    /**
     * 构建返回对象
     * @param response
     * @param respBeanEnum
     */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }

    /**
     * 获取当前登录用户
     * @param request
     * @param response
     * @return
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(ObjectUtils.isEmpty(ticket)){
            return null;
        }
        return userService.getUserByCookie(ticket, request, response);
    }
}
