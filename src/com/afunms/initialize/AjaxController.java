package com.afunms.initialize;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.base.ManagerFactory;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;

public class AjaxController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		if (null != user) {
			String uri = req.getRequestURI();
			int lastSeparator = uri.lastIndexOf("/") + 1;
			int dotSeparator = uri.lastIndexOf(".");
			String manageClass = uri.substring(lastSeparator, dotSeparator);
			String action = req.getParameter("action");
			AjaxManagerInterface manager = ManagerFactory.getAjaxManager(manageClass);
			manager.setRequest(req, resp);
			manager.execute(action);
		} 
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
