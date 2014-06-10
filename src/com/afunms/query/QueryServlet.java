package com.afunms.query;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.afunms.common.util.EncryptUtil;

public class QueryServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		String driver = "";
		String url = "";
		String dbType = request.getParameter("dbType");
		String dbName = request.getParameter("dbName");
		String ip = request.getParameter("ip");
		String port = request.getParameter("port");
		String user = request.getParameter("user");
		String pwd = "";
		try {
			pwd = EncryptUtil.decode(request.getParameter("pwd"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (dbType.equalsIgnoreCase("mysql")) {
			driver = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName + "?" + "useUnicode=true&characterEncoding=utf-8";
		} else if (dbType.equalsIgnoreCase("oracle")) {
			driver = "oracle.jdbc.driver.OracleDriver";
			url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbName;
		} else if (dbType.equalsIgnoreCase("sqlserver")) {
			driver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
			url = "jdbc:microsoft:sqlserver://" + ip + ":" + port + ";DatabaseName=model";
		}
		QueryService service = new QueryService();
		boolean isSuccess = service.testConnection(driver, url, user, pwd);
		if (isSuccess) {
			out.println("<span style='color:green;'>测试连接成功<span>");
		} else {
			out.println("<span style='color:red;'>连接失败！请检查连接参数</span>");
		}
		out.flush();
	}

	public QueryServlet() {
		super();
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
