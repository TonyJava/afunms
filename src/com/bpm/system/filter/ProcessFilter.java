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

import com.afunms.system.util.CreateMenuTableUtil;

public class ProcessFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		String uri = request.getRequestURI();
		if ("/afunms/controller/".equals(uri)) {
			return;
		}
		if (null != uri && (uri.indexOf("group.action") > 0 || uri.indexOf("groupDel.action") > 0 || uri.indexOf("groupModify.action") > 0)) {

		} else {
			CreateMenuTableUtil cmtu = new CreateMenuTableUtil();
			cmtu.createMenuTableUtil("/bpm/list.jsp", request);
		}
		arg2.doFilter(request, response);

	}

	public void init(FilterConfig filterConfig) throws ServletException {

	}

}
