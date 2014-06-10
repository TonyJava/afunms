package com.afunms.application.ajaxManager;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.InterfaceNode;

public class InterfaceThresHoldAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {
		if (action.equals("getInterfaceThresHoldList")) {
			getInterfaceThresHoldList();
		} else if (action.equals("beforeEditInterfaceThresHold")) {
			beforeEditInterfaceThresHold();
		} else if (action.equals("editInterfaceThresHold")) {
			editInterfaceThresHold();
		} else if (action.equals("batchEable")) {
			batchEable();
		} else if (action.equals("batchDisable")) {
			batchDisable();
		} else if (action.equals("batchReport")) {
			batchReport();
		} else if (action.equals("batchDisReport")) {
			batchDisReport();
		} else if (action.equals("batchSms")) {
			batchSms();
		} else if (action.equals("batchDisSms")) {
			batchDisSms();
		}
	}

	private void batchEable() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_portconfig set flag=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("启用失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("启用成功");
		out.flush();
	}

	private void batchDisable() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_portconfig set flag=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("禁用失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("禁用成功");
		out.flush();
	}

	private void batchReport() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_portconfig set reportflag=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("启用显示报表失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("启用显示报表成功");
		out.flush();
	}

	private void batchDisReport() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_portconfig set reportflag=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("显示报表禁用失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("显示报表禁用成功");
		out.flush();
	}

	private void batchSms() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_portconfig set sms=1 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("启用显示报表失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("启用显示报表成功");
		out.flush();
	}

	private void batchDisSms() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DBManager dbOp = new DBManager();
		String sql = null;
		try {
			for (int i = 0; i < ids.length; i++) {
				sql = "update system_portconfig set sms=0 where id=" + ids[i];
				dbOp.addBatch(sql);
			}
			dbOp.executeBatch();
		} catch (Exception e) {
			e.printStackTrace();
			dbOp.rollback();
			out.print("短信告警禁用失败");
		} finally {
			if (null != dbOp) {
				dbOp.close();
			}
		}
		out.print("短信告警禁用成功");
		out.flush();
	}

	private void editInterfaceThresHold() {
		Portconfig vo = new Portconfig();
		PortconfigDao dao = new PortconfigDao();
		StringBuffer jsonString = new StringBuffer("修改");
		try {
			int id = getParaIntValue("interfaceThresHoldId");
			if (id != -1) {
				vo = dao.loadPortconfig(id);
			}
			vo.setFlag(getParaValue("isA"));
			vo.setSms(getParaIntValue("isSM"));
			vo.setReportflag(getParaIntValue("isRPT"));
			vo.setLinkuse(getParaValue("remark"));

			vo.setOutportalarm(getParaValue("outAlarmVlaue"));
			vo.setInportalarm(getParaValue("inAlarmVlaue"));

			dao = new PortconfigDao();
			dao.update(vo);
			jsonString.append("成功");
		} catch (Exception e) {
			e.printStackTrace();
			jsonString.append("失败");
		} finally {
			dao.close();
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private void beforeEditInterfaceThresHold() {
		String interfaceThresHoldId = getParaValue("interfaceThresHoldId");

		PortconfigDao portconfigDao = new PortconfigDao();
		Portconfig vo = null;

		try {
			vo = (Portconfig) portconfigDao.findByID(interfaceThresHoldId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			portconfigDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != vo) {
			jsonString.append("{\"interfaceThresHoldId\":\"");
			jsonString.append(vo.getId());
			jsonString.append("\",");

			jsonString.append("\"ip\":\"");
			jsonString.append(vo.getIpaddress());
			jsonString.append("\",");

			jsonString.append("\"interfaceName\":\"");
			jsonString.append(vo.getName());
			jsonString.append("\",");

			jsonString.append("\"isA\":\"");
			jsonString.append(vo.getFlag());
			jsonString.append("\",");

			jsonString.append("\"isRPT\":\"");
			jsonString.append(vo.getReportflag());
			jsonString.append("\",");

			jsonString.append("\"isSM\":\"");
			jsonString.append(vo.getSms());
			jsonString.append("\",");

			jsonString.append("\"outAlarmVlaue\":\"");
			jsonString.append(vo.getOutportalarm());
			jsonString.append("\",");

			jsonString.append("\"inAlarmVlaue\":\"");
			jsonString.append(vo.getInportalarm());
			jsonString.append("\",");

			jsonString.append("\"remark\":\"");
			jsonString.append(vo.getLinkuse());
			jsonString.append("\"}");
		}
		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getInterfaceThresHoldList() {
		// 初始化
		InitializableInterfaceThresHold();
		PortconfigDao portconfigDao = new PortconfigDao();
		HostNodeDao nodeDao = new HostNodeDao();
		List interfaceThresHoldList = new ArrayList();
		List nodeList = new ArrayList();
		try {
			interfaceThresHoldList = portconfigDao.loadAll();
			nodeList = nodeDao.loadall();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != portconfigDao) {
				portconfigDao.close();
			}
		}
		Hashtable<String, HostNode> nodeHt = new Hashtable<String, HostNode>();
		HostNode nodeVo = null;
		if (null != nodeList && nodeList.size() > 0) {
			for (int i = 0; i < nodeList.size(); i++) {
				nodeVo = (HostNode) nodeList.get(i);
				nodeHt.put(nodeVo.getIpAddress(), nodeVo);
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != interfaceThresHoldList && interfaceThresHoldList.size() > 0) {
			Portconfig vo = null;
			for (int i = 0; i < interfaceThresHoldList.size(); i++) {
				vo = (Portconfig) interfaceThresHoldList.get(i);
				jsonString.append("{\"interfaceThresHoldId\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"ip\":\"");
				jsonString.append(vo.getIpaddress());
				jsonString.append("\",");

				jsonString.append("\"alias\":\"");
				if (null != nodeHt.get(vo.getIpaddress())) {
					jsonString.append(nodeHt.get(vo.getIpaddress()).getAlias());
				} else {
					jsonString.append("未知");
				}
				jsonString.append("\",");

				jsonString.append("\"interfaceName\":\"");
				jsonString.append(vo.getName());
				jsonString.append("\",");

				jsonString.append("\"isA\":\"");
				jsonString.append(vo.getFlag());
				jsonString.append("\",");

				jsonString.append("\"isRPT\":\"");
				jsonString.append(vo.getReportflag());
				jsonString.append("\",");

				jsonString.append("\"isSM\":\"");
				jsonString.append(vo.getSms());
				jsonString.append("\",");

				jsonString.append("\"alarmLevel\":\"");
				jsonString.append(vo.getAlarmlevel());
				jsonString.append("\",");

				jsonString.append("\"outAlarmVlaue\":\"");
				jsonString.append(vo.getOutportalarm());
				jsonString.append("\",");

				jsonString.append("\"inAlarmValue\":\"");
				jsonString.append(vo.getInportalarm());
				jsonString.append("\",");

				jsonString.append("\"remark\":\"");
				jsonString.append(vo.getLinkuse());
				jsonString.append("\",");

				jsonString.append("\"speed\":\"");
				jsonString.append(vo.getSpeed());
				jsonString.append("\"}");

				if (i != interfaceThresHoldList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + interfaceThresHoldList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void InitializableInterfaceThresHold() {
		List allPortConfigList = new ArrayList();
		Hashtable allPortConfigHt = new Hashtable();
		Hashtable tpPortConfigHt = new Hashtable();
		Portconfig portconfig = null;
		InterfaceNode interfaceNode = null;
		try {
			User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			StringBuffer s = new StringBuffer();
			if (current_user.getBusinessids() != null) {
				if (current_user.getBusinessids() != "-1") {
					String[] bids = current_user.getBusinessids().split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								s.append(" bid like '%").append(bids[i]).append("%' ");
								if (i != bids.length - 1) {
									s.append(" or ");
								}
							}
						}
					}
				}
			}

			Hashtable ifHt = new Hashtable();
			Hashtable heapDataHt = ShareData.getSharedata();
			Enumeration key = heapDataHt.keys();
			String ipAddress = (String) null;
			Hashtable entityDataHt = new Hashtable();
			while (key.hasMoreElements()) {
				ipAddress = (String) key.nextElement();
				entityDataHt = (Hashtable) heapDataHt.get(ipAddress);
				if (entityDataHt != null) {
					Vector vector = (Vector) entityDataHt.get("interface");
					if (vector != null && vector.size() > 0) {
						Interfacecollectdata vo = null;
						Hashtable tempHt = new Hashtable();
						for (int k = 0; k < vector.size(); k++) {
							vo = (Interfacecollectdata) vector.get(k);
							tempHt = new Hashtable();
							if ("ifDescr".equalsIgnoreCase(vo.getEntity())) {
								tempHt.put("ifDescr", vo.getThevalue());
							} else if ("ifSpeed".equalsIgnoreCase(vo.getEntity())) {
								tempHt.put("ifSpeed", vo.getThevalue());
							}
							ifHt.put(ipAddress + ":" + vo.getSubentity(), tempHt);
						}
					}
				}
			}
			// 从端口配置表里获取列表
			PortconfigDao portconfigdao = new PortconfigDao();
			try {
				allPortConfigList = portconfigdao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				portconfigdao.close();
			}
			if (allPortConfigList != null && allPortConfigList.size() > 0) {
				for (int i = 0; i < allPortConfigList.size(); i++) {
					portconfig = (Portconfig) allPortConfigList.get(i);
					allPortConfigHt.put(portconfig.getIpaddress() + ":" + portconfig.getPortindex(), portconfig);
				}
			}
			// 拓扑图链路端口
			HostInterfaceDao hostInterfaceDao = new HostInterfaceDao();
			int id = 1000;
			try {
				id = hostInterfaceDao.getNextID();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				hostInterfaceDao = new HostInterfaceDao();
				allPortConfigList = hostInterfaceDao.loadAll();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				hostInterfaceDao.close();
			}

			try {
				if (allPortConfigList != null && allPortConfigList.size() > 0) {
					Node node = null;
					for (int i = 0; i < allPortConfigList.size(); i++) {
						interfaceNode = (InterfaceNode) allPortConfigList.get(i);
						node = PollingEngine.getInstance().getNodeByID(interfaceNode.getNode_id());
						tpPortConfigHt.put(node.getIpAddress() + ":" + interfaceNode.getEntity(), interfaceNode);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Enumeration ifE = ifHt.keys();
			DBManager dbManager = new DBManager();
			try {
				String keyString = (String) null;
				Hashtable tempHt = new Hashtable();
				String[] keyArray = null;
				Node node = null;
				while (ifE.hasMoreElements()) {
					keyString = (String) ifE.nextElement();
					tempHt = (Hashtable) ifHt.get(keyString);
					try {
						if (!allPortConfigHt.containsKey(keyString)) {
							keyArray = keyString.split(":");
							portconfig = new Portconfig();
							portconfig.setBak("");
							portconfig.setIpaddress(keyArray[0]);
							portconfig.setLinkuse("");
							portconfig.setName((String) tempHt.get("ifDescr"));
							portconfig.setPortindex(Integer.parseInt(keyArray[1]));
							portconfig.setSms(new Integer(0));// 0：不发送短信
							portconfig.setReportflag(new Integer(0));// 0：不存在于报表
							portconfig.setInportalarm("2000");// 默认入口流速阀值
							portconfig.setOutportalarm("2000");// 默认出口流速阀值
							portconfig.setAlarmlevel("1");
							portconfig.setFlag("1");
							StringBuffer sql = new StringBuffer(100);
							sql.append("insert into system_portconfig(ipaddress,name,portindex,linkuse,sms,bak,reportflag,inportalarm,outportalarm,speed,alarmlevel,flag)values(");
							sql.append("'");
							sql.append(portconfig.getIpaddress());
							sql.append("','");
							sql.append(portconfig.getName());
							sql.append("',");
							sql.append(portconfig.getPortindex());
							sql.append(",'");
							sql.append(portconfig.getLinkuse());
							sql.append("',");
							sql.append(portconfig.getSms());
							sql.append(",'");
							sql.append(portconfig.getBak());
							sql.append("',");
							sql.append(portconfig.getReportflag());
							sql.append(",'");
							sql.append(portconfig.getInportalarm());
							sql.append("','");
							sql.append(portconfig.getOutportalarm());
							sql.append("','");
							sql.append((String) tempHt.get("ifSpeed"));
							sql.append("','");
							sql.append(portconfig.getAlarmlevel());
							sql.append("','");
							sql.append(portconfig.getFlag());
							sql.append("')");
							try {
								dbManager.addBatch(sql.toString());
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 拓扑图链路端口同步
					try {
						if (!tpPortConfigHt.containsKey(keyString)) {
							keyArray = keyString.split(":");
							portconfig = new Portconfig();
							portconfig.setIpaddress(keyArray[0]);
							portconfig.setPortindex(Integer.parseInt(keyArray[1]));
							node = PollingEngine.getInstance().getNodeByIP(keyArray[0]);
							StringBuffer sql = new StringBuffer(100);
							sql.append("insert into topo_interface(id,node_id,entity,descr,port,speed,phys_address,ip_address)values(");
							sql.append(id++);
							sql.append(",");
							sql.append(node.getId());
							sql.append(",'");
							sql.append(portconfig.getPortindex());
							sql.append("','");
							sql.append((String) tempHt.get("ifDescr"));
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append(2000);
							sql.append("','");
							sql.append("");
							sql.append("','");
							sql.append("')");
							try {
								dbManager.addBatch(sql.toString());
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					dbManager.executeBatch();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbManager.close();
				}
			}
			PortconfigDao configDao = new PortconfigDao();
			try {
				configDao.RefreshPortconfigs();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configDao.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
