package org.geekpower.common;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.geekpower.common.dto.UserDTO;
import org.geekpower.utils.JwtUtils;

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

	public static void init(HttpServletRequest request) {
		Context context = getContext();
		context.setPath(request);
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

		void setPath(HttpServletRequest request) {
			this.path = request.getRequestURI();
//            this.session = request.getSession();
			String token = request.getHeader("token");

			UserDTO userInfo = null;
			try {
				userInfo = JwtUtils.getInfoFromToken(token);
				this.user = userInfo;
			} catch (Exception e) {
				e.printStackTrace();
			}
//            BeanUtils.copyProperties(userInfo,user);
//            this.user = (User)userInfo;
//            this.user = (User) request.getSession().getAttribute("currentUser");
		}
	}

}
