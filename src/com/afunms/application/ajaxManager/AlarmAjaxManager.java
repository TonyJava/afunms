package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import montnets.SmsDao;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.SendSmsConfig;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;

public class AlarmAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public void execute(String action) {
		if (action.equals("getAlarmListByDate")) {
			getAlarmListByDate();
		} else if (action.equals("deleteEvents")) {
			deleteEvents();
		} else if (action.equals("getSmsAlarmListByDate")) {
			getSmsAlarmListByDate();
		} else if (action.equals("deleteSmsAlarm")) {
			deleteSmsAlarm();
		}
	}

	private void deleteSmsAlarm() {
		StringBuffer jsonString = new StringBuffer("É¾³ý¶ÌÐÅ¸æ¾¯");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		SmsDao SmsDao = new SmsDao();
		try {
			SmsDao.delete(ids);
			jsonString.append("³É¹¦");
		} catch (RuntimeException e) {
			e.printStackTrace();
			jsonString.append("Ê§°Ü");
		} finally {
			SmsDao.close();
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSmsAlarmListByDate() {
		String beginDate = getParaValue("beginDate");
		String endDate = getParaValue("endDate");
		if (beginDate == null || "".equals(beginDate)) {
			beginDate = sdf.format(new Date());
		}
		if (null == endDate || "".equals(endDate)) {
			endDate = sdf.format(new Date());
		}
		String startTime = beginDate + " 00:00:00";
		String toTime = endDate + " 23:59:59";

		StringBuffer sbSql = new StringBuffer(" where ");
		sbSql.append(" eventtime>'" + startTime + "'");
		sbSql.append(" and eventtime<'" + toTime + "'");
		SmsDao dao = new SmsDao();
		List smsAlarmList = new ArrayList();
		try {
			smsAlarmList = dao.findByCondition(sbSql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != smsAlarmList && smsAlarmList.size() > 0) {
			SendSmsConfig vo = null;
			for (int i = 0; i < smsAlarmList.size(); i++) {
				vo = (SendSmsConfig) smsAlarmList.get(i);
				jsonString.append("{\"smsAlarmId\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(vo.getName());
				jsonString.append("\",");

				jsonString.append("\"phone\":\"");
				jsonString.append(vo.getMobilenum());
				jsonString.append("\",");

				jsonString.append("\"alarmDescr\":\"");
				jsonString.append(vo.getEventlist());
				jsonString.append("\",");

				jsonString.append("\"happenTime\":\"");
				jsonString.append(df.format(vo.getEventtime().getTime()));
				jsonString.append("\"}");

				if (i != smsAlarmList.size() - 1) {
					jsonString.append(",");
				}
			}
			
		}
		jsonString.append("],total:" + smsAlarmList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteEvents() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		boolean result = false;
		if (ids != null && ids.length > 0) {
			// ½øÐÐÉ¾³ý
			EventListDao edao = new EventListDao();
			result = edao.delete(ids);
			edao.close();
		}
		String message = "É¾³ý³É¹¦";
		if (!result) {
			message = "É¾³ýÊ§°Ü";
		}
		out.println(message);
		out.flush();
	}

	private void getAlarmListByDate() {
		String beginDate = getParaValue("beginDate");
		String endDate = getParaValue("endDate");
		if (beginDate == null || "".equals(beginDate)) {
			beginDate = sdf.format(new Date());
		}
		if (null == endDate || "".equals(endDate)) {
			endDate = sdf.format(new Date());
		}
		String startTime = beginDate + " 00:00:00";
		String toTime = endDate + " 23:59:59";

		StringBuffer sbSql = new StringBuffer(" where ");
		sbSql.append(" recordtime>'" + startTime + "'");
		sbSql.append(" and recordtime<'" + toTime + "' order by id desc");
		List list = new ArrayList();
		EventListDao eventListDao = new EventListDao();
		try {
			list = eventListDao.findByCondition(sbSql.toString());
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}

		List<NodeDTO> allNodeDTOlist = new ArrayList<NodeDTO>();
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		try {
			allNodeDTOlist = nodeGatherIndicatorsUtil.getNodeListByTypeAndSubtype(Constant.ALL_TYPE, Constant.ALL_SUBTYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable<String, NodeDTO> nodeHt = new Hashtable<String, NodeDTO>();
		for (int i = 0; i < allNodeDTOlist.size(); i++) {
			nodeHt.put(allNodeDTOlist.get(i).getNodeid(), allNodeDTOlist.get(i));
		}
		EventList vo = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				vo = (EventList) list.get(i);
				jsonString.append("{\"id\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");
				
				jsonString.append("\"level\":\"");
				jsonString.append(vo.getLevel1());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				if (null != nodeHt.get(vo.getNodeid().toString())) {
					jsonString.append(nodeHt.get(vo.getNodeid().toString()).getName());
				} else {
					jsonString.append("null");
				}
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				if (null != nodeHt.get(vo.getNodeid().toString())) {
					jsonString.append(nodeHt.get(vo.getNodeid().toString()).getIpaddress());
				} else {
					jsonString.append("null");
				}
				jsonString.append("\",");

				jsonString.append("\"subType\":\"");
				jsonString.append(vo.getSubtype());
				jsonString.append("\",");

				jsonString.append("\"subEntity\":\"");
				jsonString.append(vo.getSubentity());
				jsonString.append("\",");

				jsonString.append("\"alarmInfo\":\"");
				jsonString.append(vo.getContent());
				jsonString.append("\",");

				jsonString.append("\"times\":\"");
				jsonString.append(vo.getHappenTimes());
				jsonString.append("\",");

				jsonString.append("\"startTime\":\"");
				jsonString.append(df.format(vo.getRecordtime().getTime()));
				jsonString.append("\",");

				jsonString.append("\"updateTime\":\"");
				jsonString.append(vo.getLasttime());
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

}
