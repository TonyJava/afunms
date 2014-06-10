package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import wfm.encode.MD5;

import com.afunms.application.dao.ProcessGroupDao;
import com.afunms.application.model.ProcessGroup;
import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class ProcessMonitorAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {
		if (action.equals("getProcessMonitorList")) {
			getProcessMonitorList();
		} else if (action.equals("getHostList")) {
			getHostList();
		} else if (action.equals("getProcessListByIp")) {
			getProcessListByIp();
		} else if (action.equals("addProcessMonitor")) {
			addProcessMonitor();
		} else if (action.equals("deleteProcessMonitorConfig")) {
			deleteProcessMonitorConfig();
		} else if (action.equals("beforeEditProcessMonitor")) {
			beforeEditProcessMonitor();
		}else if (action.equals("editProcessMonitor")) {
			editProcessMonitor();
		}

	}

	private void getProcessMonitorList() {
		List processGroupList = new ArrayList();
		List hostNodeList = new ArrayList();
		Hashtable<String, HostNode> hostNodeHt = new Hashtable<String, HostNode>();
		ProcessGroupDao processGroupDao = new ProcessGroupDao();
		HostNodeDao nodeDao = new HostNodeDao();
		String where = " where category=4";
		try {
			processGroupList = processGroupDao.loadAll();
			hostNodeList = nodeDao.findByCondition(where);
		} catch (Exception e) {
			e.printStackTrace();
		}
		HostNode vo = null;
		if (null != hostNodeList && hostNodeList.size() > 0) {
			for (int i = 0; i < hostNodeList.size(); i++) {
				vo = (HostNode) hostNodeList.get(i);
				hostNodeHt.put(String.valueOf(vo.getId()), vo);
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		ProcessGroup processGroupVo = null;
		if (null != processGroupList && processGroupList.size() > 0) {
			for (int i = 0; i < processGroupList.size(); i++) {
				processGroupVo = (ProcessGroup) processGroupList.get(i);
				jsonString.append("{\"nodeId\":\"");
				jsonString.append(processGroupVo.getNodeid());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				jsonString.append(hostNodeHt.get(processGroupVo.getNodeid()).getAlias());
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				jsonString.append(processGroupVo.getIpaddress());
				jsonString.append("\",");

				jsonString.append("\"level\":\"");
				jsonString.append(processGroupVo.getAlarm_level());
				jsonString.append("\",");

				jsonString.append("\"processGroup\":\"");
				jsonString.append(processGroupVo.getName());
				jsonString.append("\",");

				jsonString.append("\"groupId\":\"");
				jsonString.append(processGroupVo.getId());
				jsonString.append("\",");

				jsonString.append("\"isM\":\"");
				jsonString.append(processGroupVo.getMon_flag());
				jsonString.append("\"}");

				if (i != processGroupList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + processGroupList.size() + "}");
		out.print(jsonString.toString());
		out.flush();

	}

	private void getHostList() {
		List hostNodeList = getNodeListByBid();
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		HostNode vo = null;
		if (null != hostNodeList && hostNodeList.size() > 0) {
			for (int i = 0; i < hostNodeList.size(); i++) {
				vo = (HostNode) hostNodeList.get(i);
				jsonString.append("{\"nodeId\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				jsonString.append(vo.getAlias());
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				jsonString.append(vo.getIpAddress());
				jsonString.append("\",");

				jsonString.append("\"isM\":\"");
				jsonString.append(vo.isManaged());
				jsonString.append("\"}");

				if (i != hostNodeList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + hostNodeList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getProcessListByIp() {
		String ip = getParaValue("ip");
		String date = getParaValue("begindate");
		if (date == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.format(new Date());
		}
		String startTime = date + " 00:00:00";
		String toTime = date + " 23:59:59";
		List hostNodeList = getNodeListByBid();
		Hashtable processHt = null;
		String order = "MemoryUtilization";
		I_HostLastCollectData hostLastCollectDataManager = new HostLastCollectDataManager();
		try {
			processHt = hostLastCollectDataManager.getProcess_share(ip, "Process", order, startTime, toTime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		Hashtable tempHt = new Hashtable();
		Enumeration e = processHt.elements();
		while (e.hasMoreElements()) {
			tempHt = (Hashtable) e.nextElement();
			if (tempHt != null || tempHt.size() > 0) {
				jsonString.append("{\"name\":\"");
				jsonString.append(tempHt.get("Name").toString().trim());
				jsonString.append("\",");

				jsonString.append("\"state\":\"");
				jsonString.append(tempHt.get("Status").toString().trim());
				jsonString.append("\"}");

				jsonString.append(",");
			}
		}
		// 去掉最后一个逗号
		jsonString.substring(0, jsonString.length() - 1);
		jsonString.append("],total:" + hostNodeList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void addProcessMonitor() {
		ProcessGroup processGroup = new ProcessGroup();
		processGroup.setIpaddress(getParaValue("ip_address"));
		processGroup.setName(getParaValue("processGroup"));
		processGroup.setNodeid(getParaValue("nodeId"));
		processGroup.setMon_flag(getParaValue("isM"));
		processGroup.setAlarm_level(getParaValue("level"));

		List processGroupConfigurationList = new ArrayList();
		ProcessGroupConfiguration processGroupConfiguration = new ProcessGroupConfiguration();
		processGroupConfiguration.setName(getParaValue("process"));
		processGroupConfiguration.setStatus(getParaValue("state"));
		processGroupConfiguration.setTimes(getParaValue("processNumber"));
		processGroupConfigurationList.add(processGroupConfiguration);

		ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
		processGroupConfigurationUtil.saveProcessGroupAndConfiguration(processGroup, processGroupConfigurationList);

		StringBuffer jsonString = new StringBuffer("添加成功");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteProcessMonitorConfig() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
		processGroupConfigurationUtil.deleteProcessGroupAndConfiguration(ids);
		out.print("成功删除");
		out.flush();
	}

	private void beforeEditProcessMonitor() {
		String groupId = getParaValue("groupId");
		ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
		List list = processGroupConfigurationUtil.getProcessGroupConfigurationByGroupId(groupId);

		ProcessGroup processGroup = processGroupConfigurationUtil.getProcessGroup(groupId);
		ProcessGroupConfiguration vo = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				vo = (ProcessGroupConfiguration) list.get(i);
				if (String.valueOf(processGroup.getId()).equals(vo.getGroupId())) {
					jsonString.append("{\"ip_address\":\"");
					jsonString.append(processGroup.getIpaddress());
					jsonString.append("\",");

					jsonString.append("\"nodeId\":\"");
					jsonString.append(processGroup.getNodeid());
					jsonString.append("\",");

					jsonString.append("\"processGroupId\":\"");
					jsonString.append(processGroup.getId());
					jsonString.append("\",");

					jsonString.append("\"processGroup\":\"");
					jsonString.append(processGroup.getName());
					jsonString.append("\",");

					jsonString.append("\"isM\":\"");
					jsonString.append(processGroup.getMon_flag());
					jsonString.append("\",");

					jsonString.append("\"level\":\"");
					jsonString.append(processGroup.getAlarm_level());
					jsonString.append("\",");

					// 详细
					jsonString.append("\"process\":\"");
					jsonString.append(vo.getName());
					jsonString.append("\",");

					jsonString.append("\"state\":\"");
					jsonString.append(vo.getStatus());
					jsonString.append("\",");

					jsonString.append("\"processNumber\":\"");
					jsonString.append(vo.getTimes());
					jsonString.append("\"}");

					if (i != list.size() - 1) {
						jsonString.append(",");
					}
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}
	
	private void editProcessMonitor() {
		ProcessGroup processGroup = new ProcessGroup();
		processGroup.setIpaddress(getParaValue("ip_address"));
		processGroup.setId(getParaIntValue("processGroupId"));
		processGroup.setName(getParaValue("processGroup"));
		processGroup.setNodeid(getParaValue("nodeId"));
		processGroup.setMon_flag(getParaValue("isM"));
		processGroup.setAlarm_level(getParaValue("level"));

		List processGroupConfigurationList = new ArrayList();
		ProcessGroupConfiguration processGroupConfiguration = new ProcessGroupConfiguration();
		processGroupConfiguration.setName(getParaValue("process"));
		processGroupConfiguration.setStatus(getParaValue("state"));
		processGroupConfiguration.setTimes(getParaValue("processNumber"));
		processGroupConfigurationList.add(processGroupConfiguration);

		ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
		processGroupConfigurationUtil.updateProcessGroupAndConfiguration(processGroup, processGroupConfigurationList);

		StringBuffer jsonString = new StringBuffer("修改成功");
		out.print(jsonString.toString());
		out.flush();
	}

	// 获取网元
	private List getNodeListByBid() {
		List nodeList = new ArrayList();
		String where = " where category=4 and managed=1";
		where = where + getBidSql();
		HostNodeDao dao = new HostNodeDao();
		try {
			nodeList = dao.findByCondition(where);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return nodeList;
	}

	// 获取指定用户业务SQL
	private String getBidSql() {
		MD5 md = new MD5();
		String pwd = md.getMD5ofStr("admin");
		UserDao dao = new UserDao();
		User vo = null;
		try {
			vo = dao.findByLogin("admin", pwd);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		session.setAttribute(SessionConstant.CURRENT_USER, vo); // 用户姓名
		User currentUser = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		StringBuffer bidSQL = new StringBuffer();
		// 拼接标志
		int flag = 0;
		if (currentUser.getBusinessids() != null) {
			if (currentUser.getBusinessids() != "-1") {
				String[] bids = currentUser.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								bidSQL.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								flag = 1;
							} else {
								bidSQL.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					bidSQL.append(") ");
				}
			}
		}
		if (currentUser.getRole() == 0) {
			// 超级管理员
			return "";
		} else {
			return bidSQL.toString();
		}
	}

}
