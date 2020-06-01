package org.geekpower.intercepter;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geekpower.common.CurrentContext;
import org.geekpower.common.ParameterValidator;
import org.geekpower.common.RpcResponse;
import org.geekpower.common.Tuple;
import org.geekpower.service.IAuthorityService;
import org.geekpower.utils.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限拦截器
 * 
 * @author songyz
 * @createTime 2020-05-31 12:05:12
 */
@Component
public class AuthorizedInterceptor implements HandlerInterceptor {

    private static Logger logger = LoggerFactory.getLogger(AuthorizedInterceptor.class);

    @Autowired
    private IAuthorityService authorityService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        try {
            CurrentContext.init(request, authorityService);
            if (Objects.isNull(CurrentContext.getUser())) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return false;
            }
            else {
                return true;
            }
        }
        catch (Exception e) {
            Tuple.Pair<Integer, String> error = ParameterValidator.onException(e);

            response.setStatus(HttpStatus.OK.value());
            response.getWriter().write(GsonUtil.toJson(new RpcResponse<>(error.getFirst(), error.getSecond())));

            return false;
        }
    }

}
