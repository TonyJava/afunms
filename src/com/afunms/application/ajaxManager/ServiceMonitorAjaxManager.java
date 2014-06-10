package com.afunms.application.ajaxManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import wfm.encode.MD5;

import com.afunms.application.dao.HostServiceGroupDao;
import com.afunms.application.model.HostServiceGroup;
import com.afunms.application.model.HostServiceGroupConfiguration;
import com.afunms.application.util.HostServiceGroupConfigurationUtil;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.polling.om.ServiceCollectEntity;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class ServiceMonitorAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {
		if (action.equals("getServiceMonitorList")) {
			getServiceMonitorList();
		} else if (action.equals("deleteServiceMonitorConfig")) {
			deleteServiceMonitorConfig();
		} else if (action.equals("getServiceListByIp")) {
			getServiceListByIp();
		} else if (action.equals("addServiceMonitor")) {
			addServiceMonitor();
		} else if (action.equals("beforeEditServiceMonitor")) {
			beforeEditServiceMonitor();
		} else if (action.equals("editServiceMonitor")) {
			editServiceMonitor();
		}

	}

	private void editServiceMonitor() {
		HostServiceGroup serviceGroup = new HostServiceGroup();
		serviceGroup.setIpaddress(getParaValue("ip_address"));
		serviceGroup.setId(getParaIntValue("serviceGroupId"));
		serviceGroup.setName(getParaValue("serviceGroup"));
		serviceGroup.setNodeid(getParaValue("nodeId"));
		serviceGroup.setMon_flag(getParaValue("isM"));
		serviceGroup.setAlarm_level(getParaValue("level"));

		List serviceGroupConfigurationList = new ArrayList();
		HostServiceGroupConfiguration serviceGroupConfiguration = new HostServiceGroupConfiguration();
		serviceGroupConfiguration.setName(getParaValue("service"));
		serviceGroupConfiguration.setStatus(getParaValue("state"));
		serviceGroupConfigurationList.add(serviceGroupConfiguration);

		HostServiceGroupConfigurationUtil hostservicegroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
		hostservicegroupConfigurationUtil.updatehostservicegroupAndConfiguration(serviceGroup, serviceGroupConfigurationList);

		StringBuffer jsonString = new StringBuffer("修改成功");
		out.print(jsonString.toString());
		out.flush();
	}

	private void beforeEditServiceMonitor() {
		String groupId = getParaValue("groupId");
		HostServiceGroupConfigurationUtil hostservicegroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
		List list = hostservicegroupConfigurationUtil.gethostservicegroupConfigurationByGroupId(groupId);

		HostServiceGroup serviceGroup = hostservicegroupConfigurationUtil.gethostservicegroup(groupId);
		HostServiceGroupConfiguration vo = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				vo = (HostServiceGroupConfiguration) list.get(i);
				if (String.valueOf(serviceGroup.getId()).equals(vo.getGroupId())) {
					jsonString.append("{\"ip_address\":\"");
					jsonString.append(serviceGroup.getIpaddress());
					jsonString.append("\",");

					jsonString.append("\"nodeId\":\"");
					jsonString.append(serviceGroup.getNodeid());
					jsonString.append("\",");

					jsonString.append("\"serviceGroupId\":\"");
					jsonString.append(serviceGroup.getId());
					jsonString.append("\",");

					jsonString.append("\"serviceGroup\":\"");
					jsonString.append(serviceGroup.getName());
					jsonString.append("\",");

					jsonString.append("\"isM\":\"");
					jsonString.append(serviceGroup.getMon_flag());
					jsonString.append("\",");

					jsonString.append("\"level\":\"");
					jsonString.append(serviceGroup.getAlarm_level());
					jsonString.append("\",");

					// 详细
					jsonString.append("\"service\":\"");
					jsonString.append(vo.getName());
					jsonString.append("\",");

					jsonString.append("\"state\":\"");
					jsonString.append(vo.getStatus());
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

	private void addServiceMonitor() {
		HostServiceGroup serviceGroup = new HostServiceGroup();
		serviceGroup.setIpaddress(getParaValue("ip_address"));
		serviceGroup.setName(getParaValue("serviceGroup"));
		serviceGroup.setNodeid(getParaValue("nodeId"));
		serviceGroup.setMon_flag(getParaValue("isM"));
		serviceGroup.setAlarm_level(getParaValue("level"));

		List serviceGroupConfigurationList = new ArrayList();
		HostServiceGroupConfiguration serviceGroupConfiguration = new HostServiceGroupConfiguration();
		serviceGroupConfiguration.setName(getParaValue("service"));
		serviceGroupConfiguration.setStatus(getParaValue("state"));
		serviceGroupConfigurationList.add(serviceGroupConfiguration);

		HostServiceGroupConfigurationUtil hostservicegroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
		hostservicegroupConfigurationUtil.savehostservicegroupAndConfiguration(serviceGroup, serviceGroupConfigurationList);

		StringBuffer jsonString = new StringBuffer("添加成功");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getServiceMonitorList() {
		List serviceGroupList = new ArrayList();
		List hostNodeList = new ArrayList();
		Hashtable<String, HostNode> hostNodeHt = new Hashtable<String, HostNode>();
		HostServiceGroupDao hostServiceGroupDao = new HostServiceGroupDao();
		HostNodeDao nodeDao = new HostNodeDao();
		String where = " where category=4";
		try {
			serviceGroupList = hostServiceGroupDao.loadAll();
			hostNodeList = nodeDao.findByCondition(where);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != hostServiceGroupDao) {
				hostServiceGroupDao.close();
			}
			if (null != nodeDao) {
				nodeDao.close();
			}
		}
		HostNode vo = null;
		if (null != hostNodeList && hostNodeList.size() > 0) {
			for (int i = 0; i < hostNodeList.size(); i++) {
				vo = (HostNode) hostNodeList.get(i);
				hostNodeHt.put(String.valueOf(vo.getId()), vo);
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		HostServiceGroup hostServiceGroupVo = null;
		if (null != serviceGroupList && serviceGroupList.size() > 0) {
			for (int i = 0; i < serviceGroupList.size(); i++) {
				hostServiceGroupVo = (HostServiceGroup) serviceGroupList.get(i);
				if (null == hostNodeHt.get(hostServiceGroupVo.getNodeid())) {
					continue;
				}
				jsonString.append("{\"nodeId\":\"");
				jsonString.append(hostServiceGroupVo.getNodeid());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				jsonString.append(hostNodeHt.get(hostServiceGroupVo.getNodeid()).getAlias());
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				jsonString.append(hostServiceGroupVo.getIpaddress());
				jsonString.append("\",");

				jsonString.append("\"level\":\"");
				jsonString.append(hostServiceGroupVo.getAlarm_level());
				jsonString.append("\",");

				jsonString.append("\"serviceGroup\":\"");
				jsonString.append(hostServiceGroupVo.getName());
				jsonString.append("\",");

				jsonString.append("\"groupId\":\"");
				jsonString.append(hostServiceGroupVo.getId());
				jsonString.append("\",");

				jsonString.append("\"isM\":\"");
				jsonString.append(hostServiceGroupVo.getMon_flag());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			if(jsonString.toString().contains(",")){
				jsonString.deleteCharAt(jsonString.length() - 1);
			}
		}
		jsonString.append("],total:" + serviceGroupList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteServiceMonitorConfig() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		HostServiceGroupConfigurationUtil hostservicegroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
		hostservicegroupConfigurationUtil.deletehostservicegroupAndConfiguration(ids);
		out.print("成功删除");
		out.flush();
	}

	private void getServiceListByIp() {
		String ip = getParaValue("ip");
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ip);
		int count = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != ipAllData) {
			// windows部分
			if (null != ipAllData.get("winservice")) {
				Vector windowsServiceVt = new Vector();
				windowsServiceVt = (Vector) ipAllData.get("winservice");
				if (null != windowsServiceVt) {
					count = windowsServiceVt.size();
					ServiceCollectEntity vo = null;
					Iterator it = windowsServiceVt.iterator();
					while (it.hasNext()) {
						vo = (ServiceCollectEntity) it.next();
						jsonString.append("{\"name\":\"");
						jsonString.append(vo.getName());
						jsonString.append("\",");

						jsonString.append("\"state\":\"");
						jsonString.append(vo.getOpstate());
						jsonString.append("\"}");

						jsonString.append(",");
					}
				}
			} else if (null != ipAllData.get("servicelist")) {
				// linux 和 aix部分
				List shellServiceList = new ArrayList();
				shellServiceList = (List) ipAllData.get("servicelist");
				if (null != shellServiceList && shellServiceList.size() > 0) {
					count = shellServiceList.size();
					Hashtable tempHt = new Hashtable();
					for (int i = 0; i < shellServiceList.size(); i++) {
						tempHt = (Hashtable) shellServiceList.get(i);

						jsonString.append("{\"name\":\"");
						jsonString.append(tempHt.get("name"));
						jsonString.append("\",");

						jsonString.append("\"state\":\"");
						jsonString.append(tempHt.get("state"));
						jsonString.append("\"}");

						jsonString.append(",");
					}

				}
			}
		}
		// 去掉最后一个逗号
		jsonString.substring(0, jsonString.length() - 1);
		jsonString.append("],total:" + count + "}");
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
