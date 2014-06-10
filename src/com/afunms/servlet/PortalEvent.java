package com.afunms.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.afunms.common.util.SessionConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.system.model.User;

public class PortalEvent extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setCharacterEncoding("UTF-8");
		res.setContentType("text/html;charset=UTF-8");
		PrintWriter out = res.getWriter();
		try {
			int start = 0;
			if("null".equals(req.getParameter("start")) 
					|| "".equals(req.getParameter("start")) ||req.getParameter("start") == null){
				start = 0;
			}else{
				start = Integer.parseInt(req.getParameter("start"));
			}
			int limit = 0;
			if("null".equals(req.getParameter("limit")) 
					|| "".equals(req.getParameter("limit")) ||req.getParameter("limit") == null){
				limit = 10;
			}else{
				limit = Integer.parseInt(req.getParameter("limit"));
			}
			
			int iStart = start;
			int iLimit = limit;
			String search = req.getParameter("searchMsg");
			if(search == null){
				search = "";
			}
			
			String condition = new String(search.getBytes("ISO-8859-1"), "UTF-8");

			EventListDao eventdao = new EventListDao();
			String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String timeFormat = "MM-dd HH:mm:ss";
			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);

			List<EventList> eventList = new ArrayList<EventList>();

			User current_user = (User)req.getSession().getAttribute(SessionConstant.CURRENT_USER);
			try {
				String businessid = "";
				if (current_user == null) {
					return;
				} else {
					businessid = current_user.getBusinessids();
					eventList = eventdao.getQueryForEventList(startTime, endTime, businessid, condition); // 获取事件列表
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (eventdao != null) {
					eventdao.close();
				}
			}

			if (eventList == null || eventList.size() == 0) {
				return;
			}

			int count = eventList.size();

			if (count > iStart + iLimit) {
				count = iStart + iLimit;
			}

			StringBuffer jsonString = new StringBuffer("{total:" + eventList.size() + ",EventList:[");
			for (int i = iStart; i < count; i++) {
				EventList event = (EventList) eventList.get(i);
				jsonString.append("{level:'");
				jsonString.append(event.getLevel1());
				jsonString.append("',");

				jsonString.append("eventLocation:'");
				jsonString.append(event.getEventlocation());
				jsonString.append("',");

				jsonString.append("content:'");
				jsonString.append(event.getContent());
				jsonString.append("',");

				jsonString.append("times:'");
				jsonString.append(event.getHappenTimes());
				jsonString.append("',");

				jsonString.append("lastTime:'");
				jsonString.append(event.getLasttime().substring(5));
				jsonString.append("',");

				jsonString.append("nodeId:'");
				jsonString.append(event.getNodeid());
				jsonString.append("',");

				jsonString.append("subentity:'");
				jsonString.append(event.getSubentity());
				jsonString.append("',");

				jsonString.append("recordTime:'");
				jsonString.append(timeFormatter.format(event.getRecordtime().getTime()));
				jsonString.append("'}");

				if (i != count - 1) {
					jsonString.append(",");
				}
			}
			jsonString.append("]}");
			out.print(jsonString.toString());
			out.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}

}
