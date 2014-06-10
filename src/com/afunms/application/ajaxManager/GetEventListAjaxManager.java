package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.system.model.User;
import com.afunms.system.vo.EventVo;

@SuppressWarnings("unchecked")
public class GetEventListAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	public void execute(String action) {
		if (action.equals("ajaxUpdate_eventflow")) {
			ajaxUpdate_eventflow();
		}
		if (action.equals("ajaxGetEventList")) {
			ajaxGetEventList();
		}
	}

	private void ajaxGetEventList() {
		try {

			int iStart = getParaIntValue("start");

			int iLimit = getParaIntValue("limit");

			String condition =new String( getParaValue("searchMsg").getBytes("ISO-8859-1"),"UTF-8");

			EventListDao eventdao = new EventListDao();
			String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
			String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			String timeFormat = "MM-dd HH:mm:ss";
			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);

			List<EventList> eventList = new ArrayList<EventList>();
			try {
				User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
				String businessid = "";
				if (current_user == null) {
					return;
				}else{
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

	public void ajaxUpdate_eventflow() {
		ArrayList<EventVo> flexDataList = new ArrayList<EventVo>();
		List rpceventlist = new ArrayList();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		EventListDao eventdao = new EventListDao();
		String timeFormat = "HH:mm:ss";
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
		try {

			this.request.setCharacterEncoding("utf-8");
			int page = Integer.parseInt(request.getParameter("page"));
			int total = 50;

			StringBuilder json = new StringBuilder();
			json.append("{\n");
			json.append("page:" + page + ",\n");
			json.append("total:" + total + ",\n");
			json.append("rows:[");
			boolean rc = false;
			rpceventlist = eventdao.getQuery_flex(startTime, endTime, "99", "99", "-1", 99);
			if (rpceventlist != null && rpceventlist.size() > 0) {
				for (int i = 0; i < rpceventlist.size(); i++) {
					EventVo Vo = new EventVo();
					EventList event = (EventList) rpceventlist.get(i);
					Vo.setContent(event.getContent());
					Vo.setEventlocation(event.getEventlocation());
					Date d2 = event.getRecordtime().getTime();
					String time = timeFormatter.format(d2);
					Vo.setRecordtime(time);
					String level = String.valueOf(event.getLevel1());
					if ("0".equals(level)) {
						level = "提示信息";
					}
					if ("1".equals(level)) {
						level = "普通告警";
					}
					if ("2".equals(level)) {
						level = "严重告警";
					}
					if ("3".equals(level)) {
						level = "紧急告警";
					}
					Vo.setLevel1(level);
					Vo.setNodeid(event.getNodeid());
					flexDataList.add(Vo);
					if (rc)
						json.append(",");

					json.append("\n{");
					json.append("id:'");
					json.append(i);
					json.append("',");

					json.append("cell:['");
					json.append(level);
					json.append("'");

					json.append(",'");
					json.append(Vo.getEventlocation());
					json.append("'");

					json.append(",'");
					json.append(Vo.getContent());
					json.append("'");

					json.append(",'");
					json.append(Vo.getRecordtime());
					json.append("']");
					json.append("}");
					rc = true;

				}
			}
			json.append("]\n");
			json.append("}");
			this.response.getWriter().write(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventdao.close();
		}
	}
}
