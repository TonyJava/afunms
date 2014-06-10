package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.event.dao.NetSyslogViewerDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.NetSyslog;
import com.afunms.event.model.NetSyslogViewer;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.NetSyslogNodeAlarmKeyDao;
import com.afunms.topology.dao.NetSyslogNodeRuleDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.NetSyslogNodeAlarmKey;
import com.afunms.topology.model.NetSyslogNodeRule;

public class SyslogAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void execute(String action) {
		if (action.equals("getSyslogAlarmListByDate")) {
			getSyslogAlarmListByDate();
		} else if (action.equals("deleteSyslogNodes")) {
			deleteSyslogNodes();
		} else if (action.equals("getSyslogDetailForNodes")) {
			getSyslogDetailForNodes();
		} else if (action.equals("getSyslogConfigListByDate")) {
			getSyslogConfigListByDate();
		} else if (action.equals("beforeEditSyslogConfig")) {
			beforeEditSyslogConfig();
		} else if (action.equals("updateSyslogConfig")) {
			updateSyslogConfig();
		} else if (action.equals("updateSyslogConfigAll")) {
			updateSyslogConfigAll();
		}

	}

	private void updateSyslogConfigAll() {

		String ids = request.getParameter("ids");
		String[] bids = ids.split(";");
		String syslogRule = getParaValue("sysLogLevels");
		String[] pt = syslogRule.split(";");

		boolean flagAll = true;
		boolean flag = true;
		if (bids != null && bids.length > 0) {
			// 进行修改
			for (int i = 0; i < bids.length; i++) {
				String hostid = bids[i];
				String pc_str = "";
				if (pt != null && pt.length > 0) {
					for (int j = 0; j < pt.length; j++) {
						String p_t = pt[j];
						pc_str = pc_str + p_t + ",";
					}
					NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
					NetSyslogNodeRule noderule = null;
					try {
						noderule = (NetSyslogNodeRule) ruledao.findByID(hostid + "");
						String strSql = "";
						if (noderule == null) {
							strSql = "insert into nms_netsyslogrule_node(id,nodeid,facility)values(0,'" + hostid + "','" + pc_str + "')";
							flag = ruledao.saveOrUpdate(strSql);
						} else {
							flag = ruledao.updateAlarmAll(pc_str, hostid);
						}
						if (!flag) {
							flagAll = false;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						ruledao.close();
					}
				}
			}
		}
		if (flagAll) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void updateSyslogConfig() {
		String id = getParaValue("id");
		String sysLogLevels = getParaValue("sysLogLevels");
		String[] fs = sysLogLevels.split(";");
		String faci_str = "";
		if (fs != null && fs.length > 0) {
			for (int i = 0; i < fs.length; i++) {

				String fa = fs[i];
				faci_str = faci_str + fa + ",";
			}
		}
		JSONArray keyConfigJsonArray = JSONArray.fromObject(getParaValue("keyConfigJson"));
		List keyConfigList = new ArrayList();
		JSONObject obj = null;
		NetSyslogNodeAlarmKey keyDetail = null;
		for (int i = 0; i < keyConfigJsonArray.size(); i++) {
			obj = (JSONObject) keyConfigJsonArray.get(i);
			keyDetail = new NetSyslogNodeAlarmKey();
			keyDetail.setNodeid(id);
			keyDetail.setKeywords(obj.get("keywords").toString());
			keyDetail.setLevel(obj.get("alarmlevel").toString());
			keyConfigList.add(keyDetail);
			keyDetail = null;
		}

		if (keyConfigList != null && keyConfigList.size() > 0) {
			// 保存告警关键字
			NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
			try {
				netSyslogNodeAlarmKeyDao.delete(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (netSyslogNodeAlarmKeyDao != null) {
					netSyslogNodeAlarmKeyDao.close();
				}
			}
			try {
				netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
				netSyslogNodeAlarmKeyDao.save(keyConfigList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (netSyslogNodeAlarmKeyDao != null) {
					netSyslogNodeAlarmKeyDao.close();
				}
			}
		}

		NetSyslogNodeRuleDao nodeRuleDao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule noderule = null;
		try {
			noderule = (NetSyslogNodeRule) nodeRuleDao.findByID(id + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String strSql = "";
		if (noderule == null) {
			strSql = "insert into nms_netsyslogrule_node(id,nodeid,facility)values(0,'" + id + "','" + faci_str + "')";
		} else {
			strSql = "update nms_netsyslogrule_node set facility='" + faci_str + "' where nodeid='" + id + "'";
		}
		boolean flag = true;
		try {
			flag = nodeRuleDao.saveOrUpdate(strSql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodeRuleDao.close();
		}
		if (flag) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void beforeEditSyslogConfig() {
		String id = getParaValue("id");
		NetSyslogNodeRuleDao noderuledao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule noderule = null;
		try {
			noderule = (NetSyslogNodeRule) noderuledao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			noderuledao.close();
		}
		String nodefacility = "";
		if (noderule != null) {
			nodefacility = noderule.getFacility();
			nodefacility = nodefacility.replace(",", ";");
		}

		NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
		List netSyslogNodeAlarmList = null;
		try {
			netSyslogNodeAlarmList = (ArrayList) netSyslogNodeAlarmKeyDao.findByCondition(" where nodeid = '" + id + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			netSyslogNodeAlarmKeyDao.close();
		}

		StringBuffer jsonStringKey = new StringBuffer(",keyConfigJson:[{Rows:[");
		if (netSyslogNodeAlarmList != null && netSyslogNodeAlarmList.size() > 0) {
			NetSyslogNodeAlarmKey vo = null;
			for (int i = 0; i < netSyslogNodeAlarmList.size(); i++) {
				vo = (NetSyslogNodeAlarmKey) netSyslogNodeAlarmList.get(i);
				jsonStringKey.append("{\"id\":\"");
				jsonStringKey.append(vo.getId());
				jsonStringKey.append("\",");

				jsonStringKey.append("\"nodeid\":\"");
				jsonStringKey.append(vo.getNodeid());
				jsonStringKey.append("\",");

				jsonStringKey.append("\"keywords\":\"");
				jsonStringKey.append(vo.getKeywords());
				jsonStringKey.append("\",");

				jsonStringKey.append("\"alarmlevel\":\"");
				jsonStringKey.append(vo.getLevel());
				jsonStringKey.append("\"}");

				if (i != netSyslogNodeAlarmList.size() - 1) {
					jsonStringKey.append(",");
				}
			}
		}
		jsonStringKey.append("]}]");
		StringBuffer jsonString = new StringBuffer("[{");
		jsonString.append("syslogrule:[");
		jsonString.append("{\"nodefacility\":\"");
		jsonString.append(nodefacility);
		jsonString.append("\",");

		String ischeck = "是";
		if (netSyslogNodeAlarmList.size() > 0) {
			ischeck = "是";
		} else {
			ischeck = "否";
		}
		jsonString.append("\"ischeck\":\"");
		jsonString.append(ischeck);
		jsonString.append("\"}]");
		jsonString.append(jsonStringKey);
		jsonString.append("}]");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSyslogConfigListByDate() {
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);

		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user != null) {
			if (current_user.getBusinessids() != null) {
				if (current_user.getBusinessids() != "-1") {
					String[] bids = current_user.getBusinessids().split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								if (_flag == 0) {
									s.append(" and ( bid like '%" + bids[i].trim() + ",%' ");
									_flag = 1;
								} else {
									s.append(" or bid like '%" + bids[i].trim() + ",%' ");
								}
							}
						}
						s.append(") ");
					}

				}
			}
		}
		HostNodeDao dao = new HostNodeDao();
		NetSyslogNodeRuleDao syslogdao = new NetSyslogNodeRuleDao();
		List list = new ArrayList();
		List sysloglist = new ArrayList();
		try {
			if (current_user.getRole() == 0) {
				list = dao.findByCondition(" where 1=1");
			} else {
				list = dao.findByCondition(" where 1=1" + s);
			}
			sysloglist = syslogdao.findByCondition(" where 1 = 1");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
			if (syslogdao != null) {
				syslogdao.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		int size = 0;
		if (list != null && list.size() > 0) {
			HostNode node = null;
			for (int i = 0; i < list.size(); i++) {
				node = (HostNode) list.get(i);
				String rules = "";
				if (sysloglist != null && sysloglist.size() > 0) {
					NetSyslogNodeRule logrule = null;
					for (int j = 0; j < sysloglist.size(); j++) {
						logrule = (NetSyslogNodeRule) sysloglist.get(j);
						List flist = new ArrayList();
						if (logrule.getNodeid() == null || logrule.getNodeid().equals("null")) {
							continue;
						}
						if (Integer.parseInt(logrule.getNodeid()) == node.getId()) {
							if (logrule != null) {
								String facility = logrule.getFacility();
								if (facility != null && facility.trim().length() > 0) {
									String[] facilitys = facility.split(",");
									if (facilitys != null && facilitys.length > 0) {
										for (int a = 0; a < facilitys.length; a++) {
											flist.add(facilitys[a]);
										}
									}
									String str0 = "";
									String str1 = "";
									String str2 = "";
									String str3 = "";
									String str4 = "";
									String str5 = "";
									String str6 = "";
									String str7 = "";
									if (flist != null && flist.size() > 0) {
										for (int k = 0; k < flist.size(); k++) {
											if ("0".equals(flist.get(k))) {
												str0 = "紧急";
											} else if ("1".equals(flist.get(k))) {
												str1 = "报警";
											} else if ("2".equals(flist.get(k))) {
												str2 = "关键";
											} else if ("3".equals(flist.get(k))) {
												str3 = "错误";
											} else if ("4".equals(flist.get(k))) {
												str4 = "警告";
											} else if ("5".equals(flist.get(k))) {
												str5 = "通知";
											} else if ("6".equals(flist.get(k))) {
												str6 = "提示";
											} else if ("7".equals(flist.get(k))) {
												str7 = "调试";
											}
										}
										rules = str0 + "  " + str1 + "  " + str2 + "  " + str3 + "  " + str4 + "  " + str5 + "  " + str6 + "  " + str7;
									}
								}
							}
							break;
						}
					}
				}
				jsonString.append("{\"id\":\"");
				jsonString.append(node.getId());
				jsonString.append("\",");

				jsonString.append("\"hostname\":\"");
				jsonString.append(node.getAlias());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(node.getIpAddress());
				jsonString.append("\",");

				jsonString.append("\"type\":\"");
				jsonString.append(node.getType());
				jsonString.append("\",");

				jsonString.append("\"rules\":\"");
				jsonString.append(rules);
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
			size = list.size();
		}
		jsonString.append("],total : " + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getSyslogDetailForNodes() {
		int perpage = 10000;
		int currentpage = 1;
		List list = new ArrayList();
		String b_time = getParaDate("beginDate");
		String t_time = getParaDate("endDate");
		String strclass = getParaValue("strclass");
		String ipaddress = getParaValue("ipaddress");
		String starttime = b_time + " 00:00:00";
		String totime = t_time + " 23:59:59";
		NetSyslogViewerDao dao = new NetSyslogViewerDao();
		list = dao.loadNetSyslogViewers(perpage, currentpage, starttime, totime, strclass, ipaddress);
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		int size = 0;
		if (list != null && list.size() > 0) {
			NetSyslogViewer viewer = null;
			for (int i = 0; i < list.size(); i++) {
				viewer = (NetSyslogViewer) list.get(i);
				jsonString.append("{\"id\":\"");
				jsonString.append(viewer.getId());
				jsonString.append("\",");

				jsonString.append("\"hostname\":\"");
				jsonString.append(viewer.getHostName());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(viewer.getIpaddress());
				jsonString.append("\",");

				jsonString.append("\"category\":\"");
				jsonString.append(com.afunms.common.util.SyslogFinals.devCategoryMap.get(viewer.getCategory()));
				jsonString.append("\",");

				jsonString.append("\"status\":\"");
				jsonString.append(viewer.getStatus());
				jsonString.append("\",");

				jsonString.append("\"errors\":\"");
				jsonString.append(viewer.getErrors());
				jsonString.append("\",");

				jsonString.append("\"warnings\":\"");
				jsonString.append(viewer.getWarnings());
				jsonString.append("\",");

				jsonString.append("\"failures\":\"");
				jsonString.append(viewer.getFailures());
				jsonString.append("\",");

				jsonString.append("\"others\":\"");
				jsonString.append(viewer.getOthers());
				jsonString.append("\",");

				jsonString.append("\"all\":\"");
				jsonString.append(viewer.getAll());
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
			size = list.size();
		}
		jsonString.append("],total : " + size + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteSyslogNodes() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		boolean result = false;
		if (ids != null && ids.length > 0) {
			// 进行删除
			NetSyslogDao edao = new NetSyslogDao();
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

	private String getParaDate(String date) {
		String rtnDate = getParaValue(date);
		if (rtnDate == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			rtnDate = sdf.format(new Date());
		}
		return rtnDate;
	}

	private void getSyslogAlarmListByDate() {
		int status = 99;
		String priority = "99";
		String ip = "";
		String b_time = "";
		String t_time = "";
		String content = "";
		String strclass = "-1";
		strclass = getParaValue("strclass");
		status = getParaIntValue("status");
		priority = getParaValue("priority");
		ip = getParaValue("ipaddress");
		if (status == -1)
			status = 99;

		content = getParaValue("content");
		if (content == null)
			content = "";

		b_time = getParaValue("beginDate");
		t_time = getParaValue("endDate");

		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = " ";
		try {
			StringBuffer s = new StringBuffer();
			if (!"-1".equals(strclass) && strclass != null && !"".equals(strclass) && !"null".equals(strclass)) {
				if ("1".equals(strclass)) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						s.append(" where category = 4 and recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "'");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						s.append(" where recordtime>= to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS') ");
					}

				} else if ("2".equals(strclass)) {
					if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
						s.append(" where category <> 4 and recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "'");
					} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
						s.append(" where category <> 4 and recordtime>= to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS')");
					}

				}
			} else {
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					s.append(" where recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "'");
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					s.append(" where recordtime>= to_date('" + starttime1 + "','YYYY-MM-DD HH24:MI:SS') " + "and recordtime<=to_date('" + totime1 + "','YYYY-MM-DD HH24:MI:SS')");
				}

			}
			if (!"-1".equals(ip) && ip != null) {
				s.append(" and ipaddress = '" + ip + "'");
			}
			if (priority != null && !"null".equals(priority) && !"".equals(priority) && !"8,1,2,3,4,5,6,7".equals(priority)) {
				if (priority.indexOf('8') != -1) {
					priority = priority.replace('8', '0');
				}
				s.append(" and priority in (" + priority + ")");
			}
			int flag = 0;
			if (content != null && content.trim().length() > 0) {
				s.append(" and message like '%" + content + "%'");
			}
			sql = s.toString() + " order by id desc";
		} catch (Exception e) {
			e.printStackTrace();
		}
		List list = new ArrayList();
		NetSyslogDao dao = new NetSyslogDao();
		try {
			list = dao.findByCondition(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		int size = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			NetSyslog syslog = null;
			for (int i = 0; i < list.size(); i++) {
				syslog = (NetSyslog) list.get(i);
				Date cc = syslog.getRecordtime().getTime();
				String id = syslog.getId().toString();
				String message = syslog.getMessage();
				String name = syslog.getHostname();
				String ipaddress = syslog.getIpaddress();
				String priorityname = syslog.getPriorityName();
				String rtime1 = df.format(cc);
				jsonString.append("{\"id\":\"");
				jsonString.append(id);
				jsonString.append("\",");

				jsonString.append("\"level\":\"");
				jsonString.append(priorityname);
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				jsonString.append(name + "(" + ipaddress + ")");
				jsonString.append("\",");

				jsonString.append("\"content\":\"");
				jsonString.append(message);
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(ipaddress);
				jsonString.append("\",");

				jsonString.append("\"rtime\":\"");
				jsonString.append(rtime1);
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

	private void getSyslogAlarmListByDate1() {
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
