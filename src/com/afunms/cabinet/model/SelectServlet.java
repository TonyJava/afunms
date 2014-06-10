package com.afunms.cabinet.model;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SelectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SelectServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("GBK");
		response.setCharacterEncoding("UTF-8");
		String targetId = request.getParameter("id").toString();
		System.out.println(targetId);
		// ---调用Service--------------------------------
		DaoHangSerivce dhs = new DaoHangSerivce();
		List<?> cabinetlist = dhs.mcallId(targetId);
		// 获得请求中参数为id的值
		String xml_start = "<selects>";
		String xml_end = "</selects>";
		String xml = "";
		if (targetId.equalsIgnoreCase("0")) {
			xml = "<select><value>0</value><text>--请选择--</text></select>";
		}
		if (targetId.equalsIgnoreCase(targetId)) {
			for (int j = 0; j < cabinetlist.size(); j++) {
				MachineCabinet mc = (MachineCabinet) cabinetlist.get(j);
				xml += "<select><value>" + mc.getId() + "</value><text>" + mc.getName() + "</text></select>";
			}
		}
		String last_xml = xml_start + xml + xml_end;
		response.getWriter().write(last_xml);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void init() throws ServletException {
	}
}
