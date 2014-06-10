package com.afunms.application.ajaxManager;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.afunms.common.util.SnmpUtils;

public class SnmpCheckServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String ip = request.getParameter("ip");
		int port = stringToInteger(request.getParameter("port"));
		int version = stringToInteger(request.getParameter("version"));
		int timeOut = stringToInteger(request.getParameter("timeOut"));
		String community = request.getParameter("community");
		int retries = stringToInteger(request.getParameter("retries"));
		String oid = ".1.3.6.1.2.1.1.1.0";
		String outString = (String) null;
		try {
			outString = SnmpUtils.get(ip, community, oid, version, port, retries, timeOut);
			out.println("<span style='color:green;'>" + outString + "<span>");
		} catch (Exception e) {
			e.printStackTrace();
			out.println("<span style='color:red;'>¡¨Ω” ß∞‹£°</span>");
		}
		out.flush();
	}

	private int stringToInteger(String arg) {
		int rt = 0;
		if (null != arg && !arg.equals("")) {
			rt = Integer.parseInt(arg);
		}
		return rt;
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		execute(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		execute(request, response);
	}

	public void init() throws ServletException {
		// Put your code here
	}

}
