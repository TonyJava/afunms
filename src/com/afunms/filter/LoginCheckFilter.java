package com.afunms.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class LoginCheckFilter implements Filter {
	private String loginPage = "";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String url = httpServletRequest.getServletPath();
		if (url.indexOf("login.jsp") >= 0) {
			chain.doFilter(request, response);
		} else {
			HttpSession session = httpServletRequest.getSession();
			// �ж��û�session�Ƿ���ڻ��Ƿ��¼
			if (session != null && session.getAttribute("current_user") != null) {
				chain.doFilter(request, response);
			} else {
				// ת���¼ҳ��
				RequestDispatcher rd = request.getRequestDispatcher(loginPage);
				rd.forward(request, response);
			}
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.loginPage = filterConfig.getInitParameter("loginPage");
	}

	public void destroy() {
	}
}
