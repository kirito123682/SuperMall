package com.aoyamananam1.supermall.cart.interceptor;

import com.aoyamananam1.common.constant.AuthServerConstant;
import com.aoyamananam1.common.constant.CartConstant;
import com.aoyamananam1.common.vo.MemberRespVO;
import com.aoyamananam1.supermall.cart.to.UserInfoTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

/**
 * 在执行目标方法之前
 * 判断用户的登录状态，并封装传递给controller目标请求
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTO> threadLocal = new ThreadLocal<>();

    /***
     * 目标方法执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTO userInfoTO = new UserInfoTO();
        HttpSession session = request.getSession();
        MemberRespVO member = (MemberRespVO) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (member != null){
            //用户登陆了
            userInfoTO.setUserId(member.getId());
        }else {
            //用户没登陆

        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {

                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoTO.setUserKey(cookie.getValue());
                    userInfoTO.setTempUser(true);
                }
            }
        }

        //如果没有临时用户，则需要分配一个
        if (!StringUtils.hasLength(userInfoTO.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTO.setUserKey(uuid);
        }

        //目标方法执行之前
        threadLocal.set(userInfoTO);
        return true;
    }

    /**
     * 业务执行之后，让浏览器保存cookie
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        UserInfoTO userInfoTO = threadLocal.get();
        if (userInfoTO.isTempUser()){
            return;
        }
        Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTO.getUserKey());
        cookie.setDomain("supermall00.com");
        cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
        response.addCookie(cookie);
    }


}
