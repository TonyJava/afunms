package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class TrapAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void execute(String action) {
		if (action.equals("getTrapAlarmListByDate")) {
			getTrapAlarmListByDate();
		} else if (action.equals("deleteTrapNodes")) {
			deleteTrapNodes();
		}

	}

	private void deleteTrapNodes() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		boolean result = false;
		if (ids != null && ids.length > 0) {
			// 进行删除
			EventListDao edao = new EventListDao();
			result = edao.delete(ids);
			edao.close();
		}
		String message = "删除成功";
		if (!result) {
			message = "删除失败";
		}
		out.println(message);
		out.flush();
	}

	private void getTrapAlarmListByDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

		int status = 99;
		int level1 = 99;
		String content = "";
		status = getParaIntValue("status");
		level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		content = getParaValue("content");
		if (content == null)
			content = "";
		StringBuffer sbSql = new StringBuffer(" where ");
		sbSql.append(" recordtime>'" + startTime + "'");
		sbSql.append(" and recordtime<'" + toTime + "'");
		sbSql.append(" and eventtype = 'trap' ");
		if (!"99".equals(level1 + "")) {
			sbSql.append("and level1=" + level1);
		}
		if (!"99".equals(status + "")) {
			sbSql.append(" and managesign=" + status);
		}
		if (content != null && content.trim().length() > 0) {
			sbSql.append(" and content like '%" + content + "%'");
		}
		sbSql.append(" order by id desc");
		List list = new ArrayList();
		EventListDao eventListDao = new EventListDao();
		try {
			list = eventListDao.findByCondition(sbSql.toString());
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}

		List nodeList = new ArrayList();
		HostNodeDao dao = new HostNodeDao();
		try {
			nodeList = dao.loadall();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		int size = 0;
		EventList vo = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				vo = (EventList) list.get(i);
				Date cc = vo.getRecordtime().getTime();
				int id = vo.getId();
				String contents = vo.getContent();
				String level = String.valueOf(vo.getLevel1());
				String statuss = String.valueOf(vo.getManagesign());
				if ("1".equals(level)) {
					level = "普通事件";
				}
				if ("2".equals(level)) {
					level = "严重事件";
				}
				if ("3".equals(level)) {
					level = "紧急事件";
				}
				if ("0".equals(statuss)) {
					statuss = "未处理";
				}
				if ("1".equals(statuss)) {
					statuss = "处理中";
				}
				if ("2".equals(statuss)) {
					statuss = "处理完成";
				}
				String rptman = vo.getReportman();
				String rtime1 = sdf.format(cc);
				jsonString.append("{\"id\":\"");
				jsonString.append(id);
				jsonString.append("\",");

				jsonString.append("\"level\":\"");
				jsonString.append(level);
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				String nodename = "";
				String ip = "";
				if (nodeList != null && nodeList.size() > 0) {
					for (int j = 0; j < nodeList.size(); j++) {
						HostNode node = (HostNode) nodeList.get(j);
						if (vo.getNodeid() == node.getId()) {
							nodename = node.getAlias();
							ip = node.getIpAddress();
							break;
						}
					}
				}
				jsonString.append(nodename + "(" + ip + ")");
				jsonString.append("\",");

				jsonString.append("\"content\":\"");
				jsonString.append(contents);
				jsonString.append("\",");

				jsonString.append("\"rtime\":\"");
				jsonString.append(rtime1);
				jsonString.append("\",");

				jsonString.append("\"rptman\":\"");
				jsonString.append(rptman);
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(statuss);
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
			size = list.size();
		}
		jsonString.append("],total:" + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

}
