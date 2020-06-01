package org.geekpower.common;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.geekpower.common.dto.UserDTO;
import org.geekpower.service.IAuthorityService;

/**
 * 当前线程上下文的状态参数，主要是当前用户身份信息
 * 
 * @author songyz
 * @createTime 2020-01-16 18:48:58
 */
public final class CurrentContext {

    private static ThreadLocal<Context> threadLocal = new InheritableThreadLocal<Context>();

    private CurrentContext() {
    }

    private static Context getContext() {
        Context context = threadLocal.get();
        if (Objects.isNull(context)) {
            context = new Context();
            threadLocal.set(context);
        }

        return context;
    }

    public static void init(HttpServletRequest request, IAuthorityService authorityService) {
        Context context = getContext();
        context.checkSession(request, authorityService);
    }

    public static Context get() {
        return getContext();
    }

    public static void setUser(UserDTO user) {
        Context context = getContext();
        context.user = user;
    }

    public static UserDTO getUser() {
        return getContext().user;
    }

    public static void setSession(HttpSession session) {
        Context context = getContext();
        context.session = session;
    }

    public static HttpSession getSession() {
        return getContext().session;
    }

    public static class Context {
        HttpSession session;
        String path = "";
        UserDTO user;

        void checkSession(HttpServletRequest request, IAuthorityService authorityService) {
            this.path = request.getRequestURI();

            String token = request.getHeader("token");
            this.user = authorityService.checkSession(token);
        }
    }

}
