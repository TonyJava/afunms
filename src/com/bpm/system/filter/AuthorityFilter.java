package com.bpm.system.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bpm.system.utils.ConstanceUtil;
import com.bpm.system.utils.StringUtil;


public class AuthorityFilter implements Filter {

	protected FilterConfig filterConfig = null;
	private String redirectURL = null;
	private String sessionKey = null;

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		HttpSession session = request.getSession();
		if (request.getRequestURL().indexOf(ConstanceUtil.LOGINACTION) != -1) {
			filterChain.doFilter(request, response);
			return;
		}

		if (sessionKey == null) {
			filterChain.doFilter(request, response);
			return;
		}

		if (StringUtil.isBlank((String) session.getAttribute(sessionKey))) {

			response.sendRedirect(request.getContextPath() + redirectURL);
			return;
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void destroy() {

	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		redirectURL = filterConfig.getInitParameter("redirectURL");
		sessionKey = filterConfig.getInitParameter("checkSessionKey");

	}
}
