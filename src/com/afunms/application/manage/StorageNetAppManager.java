package com.afunms.application.manage;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.AlarmIndicatorsDao;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.emc.dao.raidDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.NetAppAggregate;
import com.afunms.polling.om.NetAppDisk;
import com.afunms.polling.om.NetAppDump;
import com.afunms.polling.om.NetAppEnvironment;
import com.afunms.polling.om.NetAppPlex;
import com.afunms.polling.om.NetAppQuota;
import com.afunms.polling.om.NetAppRaid;
import com.afunms.polling.om.NetAppRestore;
import com.afunms.polling.om.NetAppSnapshot;
import com.afunms.polling.om.NetAppSpare;
import com.afunms.polling.om.NetAppTree;
import com.afunms.polling.om.NetAppVolume;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.gathertask.BaskTask;

public class StorageNetAppManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {

		if ("system".equals(action)) {
			return system();
		} else if ("performance".equals(action)) {
			return performance();
		} else if ("raid".equals(action)) {
			return raidGroup();
		} else if ("interface".equals(action)) {
			return interfaceDetail();
		} else if ("vFiler".equals(action)) {
			return vFiler();
		} else if ("dump".equals(action)) {
			return dump();
		} else if (action.equals("environment")) {
			return environment();
		} else if (action.equals("event")) {
			return event();
		} else if (action.equals("eventquery")) {
			return eventQuery();
		} else if (action.equals("volume")) {
			return volume();
		} else if (action.equals("disk")) {
			return diskPer();
		} else if (action.equals("spare")) {
			return spare();
		} else if (action.equals("other")) {
			return other();
		} else if (action.equals("sychronizeData")) {
			return sychronizeData();
		}
		return null;
	}

	public String sychronizeData() {
		String nodeID = request.getParameter("id");
		String sql = "select * from nms_gather_indicators_node where nodeid='" + nodeID + "'";

		DBManager manager = null;
		List list = new ArrayList();
		ResultSet rs = null;
		try {
			manager = new DBManager();
			rs = manager.executeQuery(sql);
			while (rs.next()) {
				NodeGatherIndicators nodeGatherIndicators = new NodeGatherIndicators();
				nodeGatherIndicators.setNodeid(rs.getString("nodeid"));
				nodeGatherIndicators.setClasspath(rs.getString("classpath"));
				list.add(nodeGatherIndicators);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (manager != null)
				manager.close();
		}

		try {
			if (list.size() > 0) {
				NodeGatherIndicators nodeGatherIndicators = null;
				BaskTask btask = null;
				for (int i = 0; i < list.size(); i++) {
					btask = new BaskTask();
					nodeGatherIndicators = (NodeGatherIndicators) list.get(i);
					btask.setRunclasspath((String) nodeGatherIndicators.getClasspath());// 运行类得路径
					btask.setNodeid((String) nodeGatherIndicators.getNodeid());
					btask.setGather(nodeGatherIndicators);
					btask.run();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return system();
	}

	public String other() {
		Hashtable ipAllData = new Hashtable();
		String tmp = "";
		Vector<NetAppTree> treeVector = null;
		Vector<NetAppAggregate> aggregateVector = null;
		Vector<NetAppSnapshot> snapshotVector = null;
		Vector<NetAppQuota> quotaVector = null;
		Vector<NetAppPlex> plexVector = null;

		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				treeVector = (Vector<NetAppTree>) ipAllData.get("tree");
				aggregateVector = (Vector<NetAppAggregate>) ipAllData.get("aggregate");
				snapshotVector = (Vector<NetAppSnapshot>) ipAllData.get("snapshot");
				quotaVector = (Vector<NetAppQuota>) ipAllData.get("quota");
				plexVector = (Vector<NetAppPlex>) ipAllData.get("plex");
			} else {
				raidDao dao = new raidDao();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
			}
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("tree", treeVector);
		request.setAttribute("aggregate", aggregateVector);
		request.setAttribute("snapshot", snapshotVector);
		request.setAttribute("quota", quotaVector);
		request.setAttribute("plex", plexVector);
		return "/application/netapp/netAppOther.jsp";
	}

	public String spare() {
		Hashtable ipAllData = new Hashtable();
		String tmp = "";
		Vector<NetAppSpare> spareVector = null;
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				spareVector = (Vector<NetAppSpare>) ipAllData.get("spare");
			} else {
				raidDao dao = new raidDao();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
			}
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("spare", spareVector);
		return "/application/netapp/netAppSpare.jsp";
	}

	public String dump() {
		Hashtable ipAllData = new Hashtable();
		String tmp = "";
		Vector<NetAppDump> dump = null;
		Vector<NetAppRestore> restore = null;
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}

			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				dump = (Vector<NetAppDump>) ipAllData.get("dump");
				restore = (Vector<NetAppRestore>) ipAllData.get("restore");
			} else {
				raidDao dao = new raidDao();
				try {
				} catch (Exception e) {

				} finally {
					dao.close();
				}
			}
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("id", tmp);
		request.setAttribute("dump", dump);
		request.setAttribute("restore", restore);
		return "/application/netapp/netAppDump.jsp";
	}

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	public String system() {
		Hashtable ipAllData = new Hashtable();
		Vector systemVector = new Vector();
		Vector productVector = new Vector();
		String tmp = "";
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				systemVector = (Vector) ipAllData.get("system");
				productVector = (Vector) ipAllData.get("productInfo");
			} else {
			}

			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("system", systemVector);
		request.setAttribute("productInfo", productVector);
		return "/application/netapp/netAppSystem.jsp";
	}

	public String volume() {
		Hashtable ipAllData = new Hashtable();
		String tmp = "";
		Vector<NetAppVolume> volumeVector = null;
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				volumeVector = (Vector<NetAppVolume>) ipAllData.get("volume");
			} else {
				raidDao dao = new raidDao();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
			}
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("volume", volumeVector);
		return "/application/netapp/netAppVolume.jsp";
	}

	public String performance() {
		String tmp = "";
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		return "/application/netapp/netAppPerformance.jsp";
	}

	public String raidGroup() {
		Hashtable ipAllData = new Hashtable();
		String tmp = "";
		Vector<NetAppRaid> raid = null;
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				raid = (Vector<NetAppRaid>) ipAllData.get("raid");
			} else {
				raidDao dao = new raidDao();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
			}
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("raid", raid);
		return "/application/netapp/netAppRaid.jsp";
	}

	public String interfaceDetail() {

		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		// 时间设置begin
		String[] time = { "", "" };
		DateE datemanager = new DateE();
		Calendar current = new GregorianCalendar();
		current.set(Calendar.MINUTE, 59);
		current.set(Calendar.SECOND, 59);
		time[1] = datemanager.getDateDetail(current);
		current.add(Calendar.HOUR_OF_DAY, -1);
		current.set(Calendar.MINUTE, 0);
		current.set(Calendar.SECOND, 0);
		time[0] = datemanager.getDateDetail(current);
		String starttime = time[0];
		String endtime = time[1];

		String tmp = "";
		String showAllPortFlag = "";
		Vector vector = new Vector();
		Hashtable portConfigHash = new Hashtable();
		List<String> portIndexList = new ArrayList();
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}

			String ipaddress = host.getIpAddress();

			showAllPortFlag = request.getParameter("showAllPortFlag");// 显示所有端口
			// 0:显示所有端口
			// 1：显示关键端口
			portIndexList = new ArrayList<String>();// 关键端口的端口索引列表
			if (showAllPortFlag == null) {
				showAllPortFlag = "0";
			}
			if ("1".equals(showAllPortFlag)) {
				// 得到所有关键端口
				List list = new ArrayList();
				PortconfigDao dao = new PortconfigDao();
				try {
					list = dao.loadByIpaddress(ipaddress);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
				// 得到关键端口的端口索引列表
				com.afunms.config.model.Portconfig vo = null;
				for (int i = 0; i < list.size(); i++) {
					vo = (com.afunms.config.model.Portconfig) list.get(i);
					if (vo == null) {
						continue;
					}
					if (vo.getSms() == 1) {// 监视状态的端口
						portIndexList.add(String.valueOf(vo.getPortindex()));
					}
				}
			}

			String orderflag = request.getParameter("orderflag");
			if (orderflag == null || orderflag.trim().length() == 0)
				orderflag = "index";

			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
				String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdxPerc", "InBandwidthUtilHdxPerc", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
				try {
					vector = hostlastmanager.getInterface_share(host.getIpAddress(), netInterfaceItem, orderflag, starttime, endtime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// 采集与访问是分离模式
				I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
				String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdxPerc", "InBandwidthUtilHdxPerc", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
				try {
					vector = hostlastmanager.getInterface(host.getIpAddress(), netInterfaceItem, orderflag, starttime, endtime);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			PortconfigDao portdao = new PortconfigDao();
			portConfigHash = new Hashtable();
			try {
				portConfigHash = portdao.getIpsHash(host.getIpAddress());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				portdao.close();
			}
			if (portConfigHash == null)
				new Hashtable();

			if (vector == null || vector.size() == 0) {
				PortconfigDao dao = new PortconfigDao();
				List list = null;
				try {
					list = dao.loadByIpaddress(ipaddress);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
				if (list != null || list.size() > 0) {
					for (Object object : list) {
						com.afunms.config.model.Portconfig portconfig = (com.afunms.config.model.Portconfig) object;
						String[] strs = new String[8];
						strs[0] = String.valueOf(portconfig.getPortindex());
						strs[1] = portconfig.getName();
						strs[2] = portconfig.getSpeed();
						strs[3] = "0";
						strs[4] = "0";
						strs[5] = "0";
						strs[6] = "0";
						strs[7] = "0";
						vector.add(strs);
					}
				}
			}

			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("ifvector", vector);
		request.setAttribute("portIndexList", portIndexList);
		request.setAttribute("showAllPortFlag", showAllPortFlag);
		request.setAttribute("portConfigHash", portConfigHash);
		return "/application/netapp/netAppInterface.jsp";
	}

	public String vFiler() {
		Hashtable ipAllData = new Hashtable();
		String tmp = "";
		Vector vfilerVector = new Vector();
		Vector vfilerIpVector = new Vector();
		Vector vfilerPathVector = new Vector();
		Vector vfilerProtocolVector = new Vector();
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				vfilerVector = (Vector) ipAllData.get("vfiler");
				vfilerIpVector = (Vector) ipAllData.get("vfilerIp");
				vfilerPathVector = (Vector) ipAllData.get("vfilerPath");
				vfilerProtocolVector = (Vector) ipAllData.get("vfilerProtocol");
			} else {
				raidDao dao = new raidDao();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
			}
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("vfiler", vfilerVector);
		request.setAttribute("vfilerIp", vfilerIpVector);
		request.setAttribute("vfilerPath", vfilerPathVector);
		request.setAttribute("vfilerProtocol", vfilerProtocolVector);
		return "/application/netapp/netAppVFiler.jsp";
	}

	public String diskPer() {
		Hashtable ipAllData = new Hashtable();
		String tmp = "";
		Vector<NetAppDisk> diskVector = null;
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				diskVector = (Vector<NetAppDisk>) ipAllData.get("disk");
			} else {
			}

			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("disk", diskVector);
		return "/application/netapp/netAppDisk.jsp";
	}

	public String environment() {
		Hashtable ipAllData = new Hashtable();
		String tmp = "";
		NetAppEnvironment environment = null;
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByID(tmp);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			}
			ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				Vector envVector = (Vector) ipAllData.get("environment");
				if (envVector != null && envVector.size() > 0) {
					environment = (NetAppEnvironment) envVector.get(0);
				} else {
				}
			}
			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("environment", environment);
		return "/application/netapp/netAppEnvironment.jsp";
	}

	public String event() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		int alarmLevel = 0;
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
		if (nodeDTO != null) {
			String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}
		request.setAttribute("alarmLevel", alarmLevel);
		String b_time = "";
		String t_time = "";
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		EventListDao eldao = new EventListDao();
		List list = null;
		try {
			list = eldao.loadAll(" where nodeid = " + host.getId() + " and recordtime  <'" + t_time + " 23:59:00' and recordtime > '" + t_time + " 00:00:00'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eldao.close();
		}
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/application/netapp/netAppEvent.jsp";
	}

	public String listEvent() {
		String type = request.getParameter("type");
		String subtype = request.getParameter("subtype");
		String jsp = "/alarm/indicators/list.jsp";
		AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
		try {
			List list = alarmIndicatorsDao.getByTypeAndSubType(type, subtype);
			request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsp;
	}

	private String eventQuery() {
		Vector vector = new Vector();
		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		try {
			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

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

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao dao = new EventListDao();
				try {
					list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), host.getId());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			int alarmLevel = 0;
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
			if (nodeDTO != null) {
				String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
				if (checkEventHashtable != null) {
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
				}
			}
			request.setAttribute("alarmLevel", alarmLevel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector", vector);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);

		return "/application/netapp/netAppEvent.jsp";
	}

}