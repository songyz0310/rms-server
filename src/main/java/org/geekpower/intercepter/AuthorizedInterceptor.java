package org.geekpower.intercepter;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geekpower.common.CurrentContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限拦截器
 * 
 * @author songyz
 * @createTime 2020-05-31 12:05:12
 */
public class AuthorizedInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CurrentContext.init(request);
        if (Objects.isNull(CurrentContext.getUser())) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        else {
            return true;
        }
    }

}
