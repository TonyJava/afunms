package com.afunms.flex;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.EmailHistoryDao;
import com.afunms.application.dao.FtpHistoryDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.dao.TemperatureHumidityDao;
import com.afunms.application.dao.Urlmonitor_historyDao;
import com.afunms.application.manage.PortServiceTypeManager;
import com.afunms.application.manage.TemperatureHumidityManager;
import com.afunms.application.manage.TomcatManager;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.SybaseVO;
import com.afunms.application.model.TablesVO;
import com.afunms.application.model.TemperatureHumidityConfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.business.dao.BusinessNodeDao;
import com.afunms.business.model.BusinessNode;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.NodeconfigDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.IpAlias;
import com.afunms.config.model.Nodeconfig;
import com.afunms.detail.net.service.NetService;
import com.afunms.detail.service.serviceInfo.ServiceInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Syslog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.IpRouter;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.ProcessInfo;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.system.model.User;
import com.afunms.system.vo.ArpVo;
import com.afunms.system.vo.Db2Vo;
import com.afunms.system.vo.EventVo;
import com.afunms.system.vo.EventsVo;
import com.afunms.system.vo.FdbVo;
import com.afunms.system.vo.FlexVo;
import com.afunms.system.vo.FluxVo;
import com.afunms.system.vo.InformixVo;
import com.afunms.system.vo.InterfaceVo;
import com.afunms.system.vo.IpVo;
import com.afunms.system.vo.MemoryVo;
import com.afunms.system.vo.ProcessVo;
import com.afunms.system.vo.RouteVo;
import com.afunms.system.vo.ServiceVo;
import com.afunms.system.vo.SyslogVo;
import com.afunms.system.vo.Vo;
import com.afunms.system.vo.oraVo;
import com.afunms.temp.model.FdbNodeTemp;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.TreeNodeDao;
import com.afunms.topology.model.TreeNode;
import com.afunms.topology.util.NodeHelper;

import flex.messaging.FlexContext;
import flex.messaging.FlexSession;

@SuppressWarnings("unchecked")
public class FlexService extends BaseManager implements ManagerInterface {

	public FlexSession session;

	public FlexService() {

	}

	private void dateFormat(List list) {
		for (int i = 0; i < list.size(); i++) {
			ProcessInfo processInfo = (ProcessInfo) list.get(i);
			Float CpuTime = (Float) processInfo.getCpuTime();
			int int_CpuTime = CpuTime.intValue();
			int fenzhong = int_CpuTime / 60;
			int miaozhong = int_CpuTime % 60;
			String s_CpuTime = fenzhong + ":" + miaozhong + "秒";
			processInfo.setCpuTime(s_CpuTime);
			processInfo.setCpuUtilization(processInfo.getCpuUtilization().toString() + "%");
			processInfo.setMemory(processInfo.getMemory() + "K");
			processInfo.setMemoryUtilization(processInfo.getMemoryUtilization() + "%");
		}
	}

	public String execute(String action) {
		// if (action.equals("getInfo")) {
		// String node = getParaValue("node");
		// return getInfo(node);
		// }
		return null;
	}

	// 获取业务节点数据
	// public ArrayList getBusinessByDate(String bid,String dateStr,String
	// time){
	// ArrayList flexDataList = new ArrayList();
	// String datestr[] = dateStr.split("=");
	// String starttime = datestr[0] + " 00:00:00";
	// String endTime = datestr[1] + " 23:59:59";
	// List list = new ArrayList();
	// I_HostCollectData hostmanager=new HostCollectDataManager();
	// BusinessNodeDao businessNodeDao = new BusinessNodeDao();
	// list = businessNodeDao.findByBid(bid);
	// if(list!=null&&list.size()>0){
	// Hashtable hash[] = new Hashtable[list.size()];
	// String name[] = new String[list.size()];
	// for(int i=0;i<list.size();i++){
	// BusinessNode vo = (BusinessNode) list.get(i);
	// try {
	// name[i] = "子节点"+vo.getId();
	// hash[i] = (Hashtable) hostmanager.getBusCategory(vo.getId()+"", bid,
	// starttime, endTime, time);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// int x=0;
	// int tag=0;
	// for(int j = 0;j<hash.length;j++){
	// List hlist=new ArrayList();
	// hlist = (List) hash[j].get("list");
	// if(hlist!=null&&hlist.size()>x){
	// x = hlist.size();
	// tag = j;
	// }
	// }
	// List mlist=new ArrayList();
	// mlist = (List) hash[tag].get("list");
	// for(int k =0;k<mlist.size();k++){
	// Vector v = (Vector) mlist.get(k);
	// Hashtable h =new Hashtable();
	// h.put("date", v.get(2));
	// for(int l=0;l<hash.length;l++){
	// List hlist=new ArrayList();
	// hlist = (List) hash[l].get("list");
	// for(int i =0;i<hlist.size();i++){
	// Vector vector = new Vector();
	// vector = (Vector) hlist.get(i);
	// if(vector.get(2).equals(v.get(2))){
	// h.put(name[l], v.get(1));
	// break;
	// }
	// }
	// }
	// flexDataList.add(h);
	// }
	// }
	// return flexDataList;
	//		
	// }
	// 业务视图-获取所有业务子节点信息
	public ArrayList getAllBusData(String bid) {
		List list1 = new ArrayList();
		List list = new ArrayList();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		I_HostCollectData hostmanager = new HostCollectDataManager();
		BusinessNodeDao businessNodeDao = new BusinessNodeDao();
		list = businessNodeDao.findByBid(bid);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				try {
					ArrayList<Vo> flexDataList = new ArrayList<Vo>();
					BusinessNode bvo = (BusinessNode) list.get(i);
					Hashtable hash = (Hashtable) hostmanager.getBusCategory(bvo.getId() + "", bid, startTime, endTime, "");
					List hlist = new ArrayList();
					hlist = (List) hash.get("list");
					for (int j = 0; j < hlist.size(); j++) {
						Vector vector = new Vector();
						vector = (Vector) hlist.get(j);
						Vo vo = new Vo();
						vo.setObjectName((String) vector.get(2));
						vo.setObjectNumber1((String) vector.get(1));
						vo.setObjectNumber2(Float.parseFloat((String) vector.get(0)) * 100 + "");
						flexDataList.add(vo);
					}
					list1.add(flexDataList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (ArrayList) list1;

	}

	// 业务视图-事件查询
	public ArrayList<EventVo> getAllEventList(String date, String subtype, String level, String status, String nodeid) {
		ArrayList<EventVo> flexDataList = new ArrayList<EventVo>();
		List rpceventlist = new ArrayList();
		session = FlexContext.getFlexSession();
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String bids = current_user.getBusinessids();
		if (current_user.getRole() == 0 || current_user.getRole() == 1) {
			bids = "-1";
		}
		String datestr[] = date.split("=");
		if (datestr.length == 2) {
			if (java.sql.Date.valueOf(datestr[0]).after(java.sql.Date.valueOf(datestr[1]))) {
				System.out.println("起始日期大于截止日期!");
			} else {
				String startTime = datestr[0] + " 00:00:00";
				String endTime = datestr[1] + " 23:59:59";
				EventListDao eventdao = new EventListDao();
				String timeFormat = "MM-dd HH:mm:ss";
				java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
				try {
					rpceventlist = eventdao.getQuery(startTime, endTime, subtype, status, level, bids, Integer.parseInt(nodeid));
					if (rpceventlist != null && rpceventlist.size() > 0) {
						for (int i = 0; i < rpceventlist.size(); i++) {
							EventVo Vo = new EventVo();
							EventList event = (EventList) rpceventlist.get(i);
							Vo.setContent(event.getContent());
							Vo.setEventlocation(event.getEventlocation());
							Date d2 = event.getRecordtime().getTime();
							String time = timeFormatter.format(d2);
							Vo.setRecordtime(time);
							Vo.setLevel1("../resource/" + NodeHelper.getStatusImage(event.getLevel1()));
							flexDataList.add(Vo);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					eventdao.close();
				}
			}
		}
		return flexDataList;

	}

	// 获取arp表信息
	public ArrayList<ArpVo> getArpList(String node) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		ArrayList<ArpVo> flexDataList = new ArrayList<ArpVo>();
		Vector ipmacvector = new Vector();
		// 从内存中获取节点信息
		Host fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = (Host) PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(fnode);
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(fnode.getIpAddress());
			// 得到IPMAC信息
			ipmacvector = (Vector) ipAllData.get("ipmac");
			if (ipmacvector == null) {
				ipmacvector = new Vector();
			}
		} else {
			List arpList = new ArrayList();
			try {
				arpList = new NetService(fnode.getId() + "", nodedto.getType(), nodedto.getSubtype()).getARPInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (arpList != null && arpList.size() > 0) {
				for (int i = 0; i < arpList.size(); i++) {
					IpMac ipmac = (IpMac) arpList.get(i);
					ipmacvector.add(ipmac);
				}
			}
		}
		try {
			for (int i = 0; i < ipmacvector.size(); i++) {
				IpMac ipmac = (IpMac) ipmacvector.get(i);
				Date cc = ipmac.getCollecttime().getTime();
				ArpVo Vo = new ArpVo();
				Vo.setCollecttime(sdf.format(cc));
				Vo.setIndex(ipmac.getIfindex());
				Vo.setIp(ipmac.getIpaddress());
				Vo.setMac(ipmac.getMac());
				flexDataList.add(Vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flexDataList;

	}

	public String getAvgBusByDate(String id, String dateStr, String time) {
		String datestr[] = dateStr.split("=");
		String starttime = datestr[0] + " 00:00:00";
		String endTime = datestr[1] + " 23:59:59";
		BusinessNodeDao businessNodeDao = new BusinessNodeDao();
		BusinessNode bvo = null;
		try {
			bvo = (BusinessNode) businessNodeDao.findByID(id);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			businessNodeDao.close();
		}
		I_HostCollectData hostmanager = new HostCollectDataManager();
		try {
			Hashtable hash = (Hashtable) hostmanager.getBusCategory(id, bvo.getBid() + "", starttime, endTime, time);
			if (hash != null) {
				String respStr = (String) hash.get("avgresponcon").toString();
				String pingStr = (String) hash.get("avgpingcon").toString();
				return respStr + "," + pingStr;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getAvgTCByDate(String id, String dateStr) {
		String TH = "";
		TemperatureHumidityManager temperatureHumidityManager = new TemperatureHumidityManager();
		String datestr[] = dateStr.split("=");
		String starttime = datestr[0] + " 00:00:00";
		String endTime = datestr[1] + " 23:59:59";
		Hashtable hs = new Hashtable();
		hs = temperatureHumidityManager.getStatistics(id, starttime, endTime);
		if (hs != null) {
			TH = hs.get("avgTemperature") + "," + hs.get("avgHumidity");
		}
		return TH;

	}

	// 业务视图-获取业务节点响应时间和连通率数据
	public ArrayList<Vo> getBusByDate(String id, String dateStr, String time) {
		ArrayList<Vo> flexDataList = new ArrayList<Vo>();
		String datestr[] = dateStr.split("=");
		String starttime = datestr[0] + " 00:00:00";
		String endTime = datestr[1] + " 23:59:59";
		BusinessNodeDao businessNodeDao = new BusinessNodeDao();
		BusinessNode bvo = null;
		try {
			bvo = (BusinessNode) businessNodeDao.findByID(id);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			businessNodeDao.close();
		}
		I_HostCollectData hostmanager = new HostCollectDataManager();
		try {
			Hashtable hash = (Hashtable) hostmanager.getBusCategory(id, bvo.getBid() + "", starttime, endTime, time);
			List hlist = new ArrayList();
			hlist = (List) hash.get("list");
			for (int i = 0; i < hlist.size(); i++) {
				Vector vector = new Vector();
				vector = (Vector) hlist.get(i);
				Vo vo = new Vo();
				vo.setObjectName((String) vector.get(2));
				vo.setObjectNumber1((String) vector.get(1));
				vo.setObjectNumber2(Float.parseFloat((String) vector.get(0)) * 100 + "");
				flexDataList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flexDataList;

	}

	// 业务视图-获取业务子节点
	public String getBusnode(String bid) {
		String str = "";
		List list = new ArrayList();
		BusinessNodeDao businessNodeDao = new BusinessNodeDao();
		list = businessNodeDao.findByBid(bid);
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				BusinessNode vo = (BusinessNode) list.get(i);
				str = str + vo.getId() + "," + vo.getName();
				if (i != list.size() - 1) {
					str = str + ";";
				}
			}
		}
		return str;
	}

	/**
	 * 业务视图-获取相应ip地址的cpu在当天各个时间段的值
	 */
	public ArrayList<FlexVo> getCPUByDay(String date, String ipAddress) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		String startTime = date + " 00:00:00";
		String endTime = date + " 23:59:59";
		;
		Hashtable cpuHash = new Hashtable();
		try {
			cpuHash = hostManager.getCategory(ipAddress, "CPU", "Utilization", startTime, endTime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		List cpuList = new ArrayList();
		if (cpuHash.get("list") != null) {
			ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
			cpuList = (ArrayList) cpuHash.get("list");
			FlexVo fVo;
			for (int i = 0; i < cpuList.size(); i++) {
				Vector cpuVector = new Vector();
				fVo = new FlexVo();
				cpuVector = (Vector) cpuList.get(i);
				if (cpuVector != null || cpuVector.size() > 0) {
					fVo.setObjectNumber((String) cpuVector.get(0));
					fVo.setObjectName((String) cpuVector.get(1));
					flexDataList.add(fVo);
				}
			}
			return flexDataList;
		}
		return null;
	}

	/*
	 * 业务视图-获取一台设备过去一周中每天CPU利用率的平均值
	 */
	public ArrayList<FlexVo> getCPUByWeek(String ipAddress) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		for (int j = 6; j >= 0; j--) {
			Calendar curDate_s = Calendar.getInstance();
			curDate_s.add(Calendar.DAY_OF_MONTH, -j);
			String date = new SimpleDateFormat("yyyy-MM-dd").format(curDate_s.getTime());
			String starttime = date + " 00:00:00";
			String endTime = date + " 23:59:59";
			String avgcpucon = "0%";
			Hashtable cpuHash = new Hashtable();
			try {
				cpuHash = hostManager.getCategory(ipAddress, "CPU", "Utilization", starttime, endTime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (cpuHash.get("avgcpucon") != null) {
				avgcpucon = (String) cpuHash.get("avgcpucon");
			}
			FlexVo fVo = new FlexVo();
			fVo.setObjectName(date);
			fVo.setObjectNumber(avgcpucon.replace("%", ""));
			flexDataList.add(fVo);
		}
		return flexDataList;

	}

	public ArrayList<Db2Vo> getDB2TableSpace(String ipAddress, String port, String name, String user, String pwd) {
		Hashtable returnhash = new Hashtable();
		ArrayList<Db2Vo> flexDataList = new ArrayList<Db2Vo>();
		DBDao dao = new DBDao();
		try {
			returnhash = dao.getDB2Space(ipAddress, Integer.parseInt(port), name, user, pwd);
			Enumeration dbs = returnhash.keys();
			List retList = new ArrayList();
			while (dbs.hasMoreElements()) {
				String obj = (String) dbs.nextElement();
				retList = (List) returnhash.get(obj);
				for (int i = 0; i < retList.size(); i++) {
					Hashtable ht = (Hashtable) retList.get(i);
					if (ht == null) {
						continue;
					}
					String spacename = "";
					if (ht.get("tablespace_name") != null) {
						spacename = ht.get("tablespace_name").toString();
					}
					String size = "";
					if (ht.get("totalspac") != null) {
						size = ht.get("totalspac").toString();
					}
					String free = "";
					if (ht.get("usablespac") != null) {
						free = ht.get("usablespac").toString();
					}
					String percent = "";
					if (ht.get("usableper") != null) {
						percent = ht.get("usableper").toString();
					}
					if ("".equals(percent)) {
						percent = "0.0";
					}
					if (size.equals(free)) {
						percent = "100.0";
					}

					Db2Vo vo = new Db2Vo();
					vo.setTablespace(spacename + "(" + percent + "%)");
					vo.setSize(size);
					vo.setPercent(percent);
					vo.setFree(free);
					flexDataList.add(vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return flexDataList;

	}

	// 业务视图-数据库当天的可用性
	public ArrayList<FlexVo> getDbPieByDay(String ipAddress, String id) {
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable ConnectUtilizationhash = new Hashtable();
		I_HostCollectData hostmanager = new HostCollectDataManager();
		String category = "";
		String ip = "";
		try {
			com.afunms.polling.base.Node node = PollingEngine.getInstance().getNodeByCategory("dbs", Integer.parseInt(id));
			String type = NodeHelper.getNodeEnCategory(node.getCategory());
			if ("mysql".equalsIgnoreCase(type)) {
				category = "MYPing";
				ip = ipAddress;
			} else if ("oracle".equalsIgnoreCase(type)) {
				category = "ORAPing";
				OraclePartsDao partdao = new OraclePartsDao();
				List<OracleEntity> oracleparts = new ArrayList<OracleEntity>();
				try {
					oracleparts = partdao.findOracleParts(Integer.parseInt(id), 1);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (partdao != null) {
						partdao.close();
					}
				}
				if (oracleparts != null && oracleparts.size() > 0) {
					OracleEntity oracle = oracleparts.get(0);
					ip = ipAddress + ":" + oracle.getId();
				}
			} else if ("sql-server".equalsIgnoreCase(type)) {
				category = "SQLPing";
				ip = ipAddress;
			} else if ("sybase".equalsIgnoreCase(type)) {
				category = "SYSPing";
				ip = ipAddress;
			} else if ("informix".equalsIgnoreCase(type)) {
				category = "INFORMIXPing";
				ip = ipAddress;
			} else if ("db2".equalsIgnoreCase(type)) {
				category = "DB2Ping";
				ip = ipAddress;
			}
			ConnectUtilizationhash = hostmanager.getCategory(ip, category, "ConnectUtilization", startTime, endTime, "");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String avgping = (String) ConnectUtilizationhash.get("avgpingcon");
		if (avgping != null && !"".equals(avgping)) {
			FlexVo fVo1 = new FlexVo();
			fVo1.setObjectName("可用率");
			fVo1.setObjectNumber(Float.parseFloat(avgping.replace("%", "")) + "");
			flexDataList.add(fVo1);
			FlexVo fVo2 = new FlexVo();
			fVo2.setObjectName("不可用率");
			fVo2.setObjectNumber((100 - Float.parseFloat(avgping.replace("%", ""))) + "");
			flexDataList.add(fVo2);
		}
		// System.out.println("--------"+flexDataList.size());
		return flexDataList;
	}

	// 业务视图-获取数据库连通率信息
	public ArrayList<FlexVo> getDbPingByDay(String ipAddress, String time, String id) {
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable ConnectUtilizationhash = new Hashtable();
		I_HostCollectData hostmanager = new HostCollectDataManager();
		String category = "";
		String ip = "";
		try {
			com.afunms.polling.base.Node node = PollingEngine.getInstance().getNodeByCategory("dbs", Integer.parseInt(id));
			String type = NodeHelper.getNodeEnCategory(node.getCategory());
			if ("mysql".equalsIgnoreCase(type)) {
				category = "MYPing";
				ip = ipAddress;
			} else if ("oracle".equalsIgnoreCase(type)) {
				category = "ORAPing";
				OraclePartsDao partdao = new OraclePartsDao();
				List<OracleEntity> oracleparts = new ArrayList<OracleEntity>();
				try {
					oracleparts = partdao.findOracleParts(Integer.parseInt(id), 1);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (partdao != null) {
						partdao.close();
					}
				}
				if (oracleparts != null && oracleparts.size() > 0) {
					OracleEntity oracle = oracleparts.get(0);
					ip = ipAddress + ":" + oracle.getId();
				}
			} else if ("sql-server".equalsIgnoreCase(type)) {
				category = "SQLPing";
				ip = ipAddress;
			} else if ("sybase".equalsIgnoreCase(type)) {
				category = "SYSPing";
				ip = ipAddress;
			} else if ("informix".equalsIgnoreCase(type)) {
				category = "INFORMIXPing";
				ip = ipAddress;
			} else if ("db2".equalsIgnoreCase(type)) {
				category = "DB2Ping";
				ip = ipAddress;
			}

			ConnectUtilizationhash = hostmanager.getCategory(ip, category, "ConnectUtilization", startTime, endTime, time);
			List pingList = new ArrayList();
			pingList = (List) ConnectUtilizationhash.get("list");
			if (pingList != null && pingList.size() > 0) {
				int num = pingList.size();
				for (int i = 0; i < num; i++) {
					FlexVo fVo = new FlexVo();
					Vector pingVector = new Vector();
					pingVector = (Vector) pingList.get(i);
					if (pingVector != null) {
						fVo.setObjectName((String) pingVector.get(1));
						fVo.setObjectNumber((String) pingVector.get(0));
						flexDataList.add(fVo);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return flexDataList;
	}

	// 业务视图-主机磁盘信息
	public ArrayList<Vo> getDiskByDay(String ipAddress) {
		Hashtable ipAllData = null;
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			ipAllData = (Hashtable) ShareData.getSharedata().get(ipAddress);
		} else {
			// 采集与访问是分离模式
			ipAllData = (Hashtable) ShareData.getAllNetworkData().get(ipAddress);
		}
		ArrayList<Vo> flexDataList = new ArrayList<Vo>();
		String util = "";
		String name = "";
		String allSize = "";
		String usedSize = "";
		DecimalFormat df1 = new DecimalFormat("#");
		DecimalFormat df2 = new DecimalFormat("#.#");
		if (ipAllData != null) {
			Vector diskVector = (Vector) ipAllData.get("disk");
			if (diskVector != null && diskVector.size() > 0) {
				for (int si = 0; si < diskVector.size(); si++) {
					DiskCollectEntity diskdata = (DiskCollectEntity) diskVector.elementAt(si);
					if (diskdata != null) {
						if (diskdata.getEntity().equalsIgnoreCase("Utilization")) {
							util = util + df1.format(Double.parseDouble(diskdata.getThevalue())) + ";";
						} else if (diskdata.getEntity().equalsIgnoreCase("AllSize")) {
							name = name + subString(diskdata.getSubentity()) + ";";
							allSize = allSize + df2.format(Double.parseDouble(diskdata.getThevalue())) + ";";
						} else if (diskdata.getEntity().equalsIgnoreCase("UsedSize")) {
							usedSize = usedSize + df2.format(Double.parseDouble(diskdata.getThevalue())) + ";";
						}
					}
				}
			}
			String arrName[] = name.split(";");
			String arrUtil[] = util.split(";");
			String arrAll[] = allSize.split(";");
			String arrUsed[] = usedSize.split(";");
			for (int i = 0; i < arrName.length; i++) {
				Vo vo = new Vo();
				vo.setObjectName(arrName[i] + "(" + arrUtil[i] + "%)");
				vo.setObjectNumber1(arrAll[i]);
				vo.setObjectNumber2(arrUsed[i]);
				flexDataList.add(vo);
			}

		}
		return flexDataList;
	}

	/*
	 * 获取Email服务在指定时间段的连通率
	 */
	public ArrayList<FlexVo> getEmailPing(String id, String dateStr, String timeStr) {
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String date[] = dateStr.split("=");
		String time[] = timeStr.split("=");
		String startTime = date[0] + " " + time[0] + ":00:00";
		String endTime = date[1] + " " + time[1] + ":59:59";
		String conn[] = new String[2];
		EmailHistoryDao historydao = new EmailHistoryDao();
		try {
			conn = historydao.getAvailability(Integer.parseInt(id), startTime, endTime, "is_canconnected");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
		}
		if (conn[0] != null && !"".equals(conn[0]) && conn[1] != null && !"".equals(conn[1])) {
			FlexVo fVo1 = new FlexVo();
			fVo1.setObjectName("连通");
			fVo1.setObjectNumber(conn[0]);
			flexDataList.add(fVo1);
			FlexVo fVo2 = new FlexVo();
			fVo2.setObjectName("未连通");
			fVo2.setObjectNumber(conn[1]);
			flexDataList.add(fVo2);
		}
		return flexDataList;
	}

	// 业务视图-获取事件列表
	public ArrayList<EventVo> getEventByNode(String node) {
		ArrayList<EventVo> flexDataList = new ArrayList<EventVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String timeFormat = "MM-dd HH:mm:ss";
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
		// 从内存中获取节点信息
		Node fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		EventListDao eventListDao = new EventListDao();
		List alarmList = null;
		try {
			if (fnode != null) {
				alarmList = eventListDao.getQuery(startTime, endTime, fnode.getId());
			}
			if (alarmList != null && alarmList.size() > 0) {
				for (int i = 0; i < alarmList.size(); i++) {
					EventVo Vo = new EventVo();
					EventList event = (EventList) alarmList.get(i);
					Vo.setContent(event.getContent());
					Vo.setEventlocation(event.getEventlocation());
					Date d2 = event.getRecordtime().getTime();
					String time = timeFormatter.format(d2);
					Vo.setRecordtime(time);
					Vo.setLevel1("../resource/" + NodeHelper.getStatusImage(event.getLevel1()));
					flexDataList.add(Vo);
					if (i == 5) {
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		return flexDataList;

	}

	// 获取事件列表信息
	public ArrayList<EventsVo> getEventList(String node, String startTime, String endTime) {
		ArrayList<EventsVo> flexDataList = new ArrayList<EventsVo>();
		if (java.sql.Date.valueOf(startTime.substring(0, startTime.indexOf(" "))).after(java.sql.Date.valueOf(endTime.substring(0, endTime.indexOf(" "))))) {
			System.out.println("起始日期大于截止日期!");
		} else {
			String timeFormat = "MM-dd HH:mm:ss";
			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
			// 从内存中获取节点信息
			Node fnode = null;
			TreeNodeDao ftreeNodeDao = new TreeNodeDao();
			TreeNode fvo = null;
			try {
				fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
			} catch (RuntimeException e1) {
				e1.printStackTrace();
			} finally {
				ftreeNodeDao.close();
			}
			if (fvo != null) {
				fnode = PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
			}
			EventListDao eventListDao = new EventListDao();
			List alarmList = null;
			try {
				if (fnode != null) {
					alarmList = eventListDao.getQuery(startTime, endTime, fnode.getId());
				}
				if (alarmList != null && alarmList.size() > 0) {
					for (int i = 0; i < alarmList.size(); i++) {
						EventsVo Vo = new EventsVo();
						EventList event = (EventList) alarmList.get(i);
						Vo.setContent(event.getContent());
						Vo.setRptman(event.getReportman());
						Date d2 = event.getRecordtime().getTime();
						String time = timeFormatter.format(d2);
						Vo.setRtime1(time);
						String level = "";
						if (event.getLevel1() == 0) {
							level = "提示信息";
						}
						if (event.getLevel1() == 1) {
							level = "普通告警";
						}
						if (event.getLevel1() == 2) {
							level = "严重告警";
						}
						if (event.getLevel1() == 3) {
							level = "紧急告警";
						}
						Vo.setLevel1(level);
						flexDataList.add(Vo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventListDao.close();
			}
		}
		return flexDataList;

	}

	// 获取fdb表信息
	public ArrayList<FdbVo> getFdbList(String node) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		ArrayList<FdbVo> flexDataList = new ArrayList<FdbVo>();
		Vector ipmacvector = new Vector();
		// 从内存中获取节点信息
		Host fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = (Host) PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(fnode);
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(fnode.getIpAddress());
			// 得到fdb信息
			ipmacvector = (Vector) ipAllData.get("fdb");
			if (ipmacvector == null) {
				ipmacvector = new Vector();
			}
		} else {
			List fdbList = new ArrayList();
			try {
				fdbList = new NetService(fnode.getId() + "", nodedto.getType(), nodedto.getSubtype()).getFDBInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (fdbList != null && fdbList.size() > 0) {
				for (int i = 0; i < fdbList.size(); i++) {
					FdbNodeTemp fdb = (FdbNodeTemp) fdbList.get(i);
					IpMac ipmac = new IpMac();
					ipmac.setIfband(fdb.getIfband());
					ipmac.setIfsms(fdb.getIfsms());
					SimpleDateFormat _sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date da = null;
					try {
						da = _sdf.parse(fdb.getCollecttime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					Calendar tempCal = Calendar.getInstance();
					tempCal.setTime(da);
					ipmac.setCollecttime(tempCal);
					ipmac.setRelateipaddr(fnode.getIpAddress());
					ipmac.setIpaddress(fdb.getIpaddress());
					ipmac.setMac(fdb.getMac());
					ipmac.setBak(fdb.getBak());
					ipmac.setIfindex(fdb.getIfindex());
					ipmacvector.addElement(ipmac);
				}
			}
		}
		try {
			for (int i = 0; i < ipmacvector.size(); i++) {
				IpMac ipmac = (IpMac) ipmacvector.get(i);
				Date cc = ipmac.getCollecttime().getTime();
				FdbVo Vo = new FdbVo();
				Vo.setCollecttime(sdf.format(cc));
				Vo.setIfindex(ipmac.getIfindex());
				Vo.setPort(ipmac.getBak());
				Vo.setMac(ipmac.getMac());
				Vo.setIpadress(ipmac.getIpaddress());
				flexDataList.add(Vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return flexDataList;

	}

	/*
	 * 获取一台设备在指定时间段综合流速值
	 */
	public ArrayList<FluxVo> getFluxByDate(String ipAddress, String dateStr, String time) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		ArrayList<FluxVo> flexDataList = new ArrayList<FluxVo>();
		String datestr[] = dateStr.split("=");
		if (datestr != null && datestr.length == 2) {
			if (java.sql.Date.valueOf(datestr[0]).after(java.sql.Date.valueOf(datestr[1]))) {
				System.out.println("起始日期大于截止日期!");
			} else {
				String starttime = datestr[0] + " 00:00:00";
				String endTime = datestr[1] + " 23:59:59";
				// 获取流速历史数据平均值
				Hashtable inutilHash = new Hashtable();
				try {
					inutilHash = hostManager.getAllutilhdx(ipAddress, "AllInBandwidthUtilHdx", starttime, endTime, "", time);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Hashtable oututilHash = new Hashtable();
				try {
					oututilHash = hostManager.getAllutilhdx(ipAddress, "AllOutBandwidthUtilHdx", starttime, endTime, "", time);
				} catch (Exception e) {
					e.printStackTrace();
				}
				List inutilList = new ArrayList();
				inutilList = (List) inutilHash.get("list");
				List oututilList = new ArrayList();
				oututilList = (List) oututilHash.get("list");
				if (inutilList != null && inutilList.size() > 0 && oututilList != null && oututilList.size() > 0) {
					int num = (inutilList.size() > oututilList.size() ? oututilList.size() : inutilList.size());
					for (int i = 0; i < num; i++) {
						FluxVo fVo = new FluxVo();
						Vector inHdxVector = new Vector();
						inHdxVector = (Vector) inutilList.get(i);
						Vector outHdxVector = new Vector();
						outHdxVector = (Vector) oututilList.get(i);
						if (inHdxVector != null && outHdxVector != null) {
							fVo.setDate((String) inHdxVector.get(1));
							fVo.setFluxin((String) inHdxVector.get(0));
							fVo.setFluxout((String) outHdxVector.get(0));
							flexDataList.add(fVo);
						}
					}
				}
			}
		}
		return flexDataList;
	}

	/*
	 * 业务视图-获取一台网络设备当天中各时间段的综合流速值
	 */
	public ArrayList<FluxVo> getFluxByDay(String ipAddress) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		ArrayList<FluxVo> flexDataList = new ArrayList<FluxVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		// 获取流速历史数据平均值
		Hashtable inutilHash = new Hashtable();
		try {
			inutilHash = hostManager.getAllutilhdx(ipAddress, "AllInBandwidthUtilHdx", startTime, endTime, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable oututilHash = new Hashtable();
		try {
			oututilHash = hostManager.getAllutilhdx(ipAddress, "AllOutBandwidthUtilHdx", startTime, endTime, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		List inutilList = new ArrayList();
		inutilList = (List) inutilHash.get("list");
		List oututilList = new ArrayList();
		oututilList = (List) oututilHash.get("list");
		if (inutilList != null && inutilList.size() > 0 && oututilList != null && oututilList.size() > 0) {
			int num = (inutilList.size() > oututilList.size() ? oututilList.size() : inutilList.size());
			for (int i = 0; i < num; i++) {
				FluxVo fVo = new FluxVo();
				Vector inHdxVector = new Vector();
				inHdxVector = (Vector) inutilList.get(i);
				Vector outHdxVector = new Vector();
				outHdxVector = (Vector) oututilList.get(i);
				if (inHdxVector != null && outHdxVector != null) {
					fVo.setDate((String) inHdxVector.get(1));
					fVo.setFluxin((String) inHdxVector.get(0));
					fVo.setFluxout((String) outHdxVector.get(0));
					flexDataList.add(fVo);
				}
			}
		}
		return flexDataList;

	}

	/*
	 * 获取一台设备过去十天的综合流速值
	 */
	public ArrayList<FluxVo> getFluxByDays(String ipAddress, String time) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		ArrayList<FluxVo> flexDataList = new ArrayList<FluxVo>();
		Calendar curDate_s = Calendar.getInstance();
		curDate_s.add(Calendar.DAY_OF_MONTH, -9);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(curDate_s.getTime());
		String starttime = date + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		// 获取流速历史数据平均值
		Hashtable inutilHash = new Hashtable();
		try {
			inutilHash = hostManager.getAllutilhdx(ipAddress, "AllInBandwidthUtilHdx", starttime, endTime, "", time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable oututilHash = new Hashtable();
		try {
			oututilHash = hostManager.getAllutilhdx(ipAddress, "AllOutBandwidthUtilHdx", starttime, endTime, "", time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List inutilList = new ArrayList();
		inutilList = (List) inutilHash.get("list");
		List oututilList = new ArrayList();
		oututilList = (List) oututilHash.get("list");
		if (inutilList != null && inutilList.size() > 0 && oututilList != null && oututilList.size() > 0) {
			int num = (inutilList.size() > oututilList.size() ? oututilList.size() : inutilList.size());
			for (int i = 0; i < num; i++) {
				FluxVo fVo = new FluxVo();
				Vector inHdxVector = new Vector();
				inHdxVector = (Vector) inutilList.get(i);
				Vector outHdxVector = new Vector();
				outHdxVector = (Vector) oututilList.get(i);
				if (inHdxVector != null && outHdxVector != null) {
					fVo.setDate((String) inHdxVector.get(1));
					fVo.setFluxin((String) inHdxVector.get(0));
					fVo.setFluxout((String) outHdxVector.get(0));
					flexDataList.add(fVo);
				}
			}
		}
		return flexDataList;
	}

	/*
	 * 获取Ftp服务在指定时间段的连通率
	 */
	public ArrayList<FlexVo> getFtpPing(String id, String dateStr, String timeStr) {
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String date[] = dateStr.split("=");
		String time[] = timeStr.split("=");
		String startTime = date[0] + " " + time[0] + ":00:00";
		String endTime = date[1] + " " + time[1] + ":59:59";
		String conn[] = new String[2];
		FtpHistoryDao historydao = new FtpHistoryDao();
		try {
			conn = historydao.getAvailability(Integer.parseInt(id), startTime, endTime, "is_canconnected");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
		}
		if (conn[0] != null && !"".equals(conn[0]) && conn[1] != null && !"".equals(conn[1])) {
			FlexVo fVo1 = new FlexVo();
			fVo1.setObjectName("连通");
			fVo1.setObjectNumber(conn[0]);
			flexDataList.add(fVo1);
			FlexVo fVo2 = new FlexVo();
			fVo2.setObjectName("未连通");
			fVo2.setObjectNumber(conn[1]);
			flexDataList.add(fVo2);
		}
		return flexDataList;
	}

	// 获取设备基本信息
	public String getInfo(String node) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		String flexDataList = "";
		String collecttime = "";
		String sysuptime = "";
		String sysdescr = "";
		String syslocation = "";
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 从内存中获取节点信息
		Host fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = (Host) PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(fnode);
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(fnode.getIpAddress());
			if (ipAllData != null) {
				// 得到系统启动时间
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysLocation")) {
							syslocation = systemdata.getThevalue();
						}
					}
				}
			}
			Vector pingData = (Vector) ShareData.getPingdata().get(fnode.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}
		} else {
			// 采集与访问是分离模式
			List systemList = new ArrayList();
			List pingList = new ArrayList();
			try {
				systemList = new NetService(fnode.getId() + "", nodedto.getType(), nodedto.getSubtype()).getSystemInfo();
				pingList = new NetService(fnode.getId() + "", nodedto.getType(), nodedto.getSubtype()).getCurrPingInfo();
			} catch (Exception e) {
			}
			// 5.
			if (systemList != null && systemList.size() > 0) {
				for (int i = 0; i < systemList.size(); i++) {
					NodeTemp nodetemp = (NodeTemp) systemList.get(i);
					if ("sysUpTime".equals(nodetemp.getSindex())) {
						sysuptime = nodetemp.getThevalue();
					}
					if ("sysDescr".equals(nodetemp.getSindex())) {
						sysdescr = nodetemp.getThevalue();
					}
					if ("sysLocation".equals(nodetemp.getSindex())) {
						syslocation = nodetemp.getThevalue();
					}
				}
			}
			if (pingList != null && pingList.size() > 0) {
				for (int i = 0; i < pingList.size(); i++) {
					NodeTemp nodetemp = (NodeTemp) pingList.get(i);
					if ("ConnectUtilization".equals(nodetemp.getSindex())) {
						collecttime = nodetemp.getCollecttime();
					}
				}
			}
		}
		try {
			if (fnode != null) {
				flexDataList = fnode.getAlias() + ";" + fnode.getSysName() + ";" + "../resource/" + NodeHelper.getCurrentStatusImage(fnode.getStatus()) + ";"
						+ fnode.getIpAddress() + ";" + sysuptime + ";" + fnode.getMac() + ";" + fnode.getAssetid() + ";" + fnode.getNetMask() + ";"
						+ NodeHelper.getNodeCategory(fnode.getCategory()) + ";" + fnode.getType() + ";" + sysdescr + ";" + collecttime + ";" + fnode.getSysOid() + ";"
						+ syslocation;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flexDataList;

	}

	// 获得Informix数据库信息
	public ArrayList<InformixVo> getInformixInfo(String ipAddress, String dbname) {
		ArrayList<InformixVo> flexDataList = new ArrayList<InformixVo>();
		DBDao dao = new DBDao();
		String hex = IpTranslation.formIpToHex(ipAddress);
		String serverip = hex + ":" + dbname;
		List dbspaces = new ArrayList();
		try {
			dbspaces = dao.getInformix_nmsspace(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		DecimalFormat df = new DecimalFormat("#.##");
		if (dbspaces != null) {
			if (dbspaces != null && dbspaces.size() > 0) {
				for (int i = 0; i < dbspaces.size(); i++) {
					Hashtable tablesVO = (Hashtable) dbspaces.get(i);
					InformixVo vo = new InformixVo();
					vo.setDbspace(tablesVO.get("dbspace") + "(" + df.format(100 - Float.parseFloat(tablesVO.get("percent_free") + "")) + "%)");
					vo.setFname(tablesVO.get("fname") + "");
					vo.setOwner(tablesVO.get("owner") + "");
					vo.setPages_free(tablesVO.get("pages_free") + "");
					vo.setPages_size(tablesVO.get("pages_size") + "");
					vo.setPages_used(tablesVO.get("pages_used") + "");
					flexDataList.add(vo);
				}
			}
		}
		return flexDataList;
	}

	// 获取单个设备接口信息
	public ArrayList<InterfaceVo> getInterfaceList(String node) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		ArrayList<InterfaceVo> flexDataList = new ArrayList<InterfaceVo>();
		Vector vector = new Vector();
		// 从内存中获取节点信息
		Node fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
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
		String orderflag = "";
		if (orderflag == null || orderflag.trim().length() == 0) {
			orderflag = "index";
		}
		if ("0".equals(runmodel)) {
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdxPerc", "InBandwidthUtilHdxPerc", "OutBandwidthUtilHdx",
					"InBandwidthUtilHdx" };
			try {
				vector = hostlastmanager.getInterface_share(fnode.getIpAddress(), netInterfaceItem, orderflag, starttime, endtime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdxPerc", "InBandwidthUtilHdxPerc", "OutBandwidthUtilHdx",
					"InBandwidthUtilHdx" };
			try {
				vector = hostlastmanager.getInterface(fnode.getIpAddress(), netInterfaceItem, orderflag, starttime, endtime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		PortconfigDao portdao = new PortconfigDao();
		Hashtable hash = new Hashtable();
		try {
			hash = portdao.getIpsHash(fnode.getIpAddress());
		} catch (Exception e) {
		} finally {
			portdao.close();
		}
		if (hash == null) {
			new Hashtable();
		}
		try {
			for (int i = 0; i < vector.size(); i++) {
				String[] strs = (String[]) vector.get(i);
				if (strs != null && strs.length > 0) {
					InterfaceVo Vo = new InterfaceVo();
					String index = strs[0];
					Vo.setIndex(index);
					Vo.setAlias(strs[1]);
					Vo.setKbs(strs[2]);
					String linkuse = "";
					if (hash != null && hash.size() > 0) {
						if (hash.get(fnode.getIpAddress() + ":" + index) != null) {
							linkuse = (String) hash.get(fnode.getIpAddress() + ":" + index);
						}
					} else {
						linkuse = "";
					}
					Vo.setApp(linkuse);
					String status = strs[3];
					String url = "";
					if (status.equals("up")) {
						url = "../resource/image/topo/up.gif";
					} else if (status.equals("down")) {
						url = "../resource/image/topo/down.gif";
					} else {
						url = "../resource/image/topo/testing.gif";
					}
					Vo.setStatue(url);
					Vo.setOuts(strs[6] + " kb/s");
					Vo.setIns(strs[7] + " kb/s");
					Vo.setInPerc(strs[5] + " %");
					Vo.setOutPerc(strs[4] + " %");
					flexDataList.add(Vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return flexDataList;

	}

	// 业务视图-获取单个业务节点的信息
	public ArrayList<Vo> getIntFaceByDate(String id) {
		ArrayList<Vo> flexDataList = new ArrayList<Vo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		I_HostCollectData hostmanager = new HostCollectDataManager();
		BusinessNodeDao businessNodeDao = new BusinessNodeDao();
		BusinessNode bvo = null;
		try {
			bvo = (BusinessNode) businessNodeDao.findByID(id);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			businessNodeDao.close();
		}
		try {
			Hashtable hash = (Hashtable) hostmanager.getBusCategory(id, bvo.getBid() + "", startTime, endTime, "");
			List hlist = new ArrayList();
			hlist = (List) hash.get("list");
			for (int j = 0; j < hlist.size(); j++) {
				Vector vector = new Vector();
				vector = (Vector) hlist.get(j);
				Vo vo = new Vo();
				vo.setObjectName((String) vector.get(2));
				vo.setObjectNumber1((String) vector.get(1));
				vo.setObjectNumber2(Float.parseFloat((String) vector.get(0)) * 100 + "");
				flexDataList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flexDataList;
	}

	// 获取IP列表信息
	public ArrayList<IpVo> getIpList(String node) {
		ArrayList<IpVo> flexDataList = new ArrayList<IpVo>();
		// 从内存中获取节点信息
		Host fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = (Host) PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		IpAliasDao ipdao = new IpAliasDao();
		List iplist = ipdao.loadByIpaddress(fnode.getIpAddress());
		if (iplist == null) {
			iplist = new ArrayList();
		}
		ipdao.close();
		try {
			if (iplist == null) {
				iplist = new ArrayList();
			}
			for (int i = 0; i < iplist.size(); i++) {
				IpAlias vo = (IpAlias) iplist.get(i);
				if (vo == null) {
					continue;
				}
				if (vo.getDescr() == null) {
					vo.setDescr("");
				}
				String url = "../resource/image/topo/testing.gif";
				Hashtable aliasip = new Hashtable();
				if (ShareData.getRelateippingdata() != null) {
					aliasip = (Hashtable) ShareData.getRelateippingdata();
					if (aliasip != null && aliasip.size() > 0) {
						if (aliasip.containsKey(vo.getAliasip())) {
							Vector aliasping = (Vector) aliasip.get(vo.getAliasip());
							if (aliasping != null && aliasping.size() > 0) {
								PingCollectEntity pingdata = (PingCollectEntity) aliasping.get(0);
								if ("0".equalsIgnoreCase(pingdata.getThevalue())) {
									// PING不通
									url = "../resource/image/topo/down.gif";
								} else {
									// PING通
									url = "../resource/image/topo/up.gif";
								}
							}
						}
					}
				}
				IpVo Vo = new IpVo();
				Vo.setAliasip(vo.getAliasip());
				Vo.setDescr(vo.getDescr());
				Vo.setIndex(vo.getIndexs());
				Vo.setSpeed(vo.getSpeed());
				Vo.setStatue(url);
				Vo.setTypes(vo.getTypes());
				flexDataList.add(Vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return flexDataList;
	}

	/*
	 * 业务视图-获取服务器当天中各时间段的内存利用率
	 */
	public ArrayList<MemoryVo> getMemoryByDay(String ipAddress) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		ArrayList<MemoryVo> flexDataList = new ArrayList<MemoryVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DecimalFormat df = new DecimalFormat("#.##");
		// 获取内存利用率历史数据平均值
		Hashtable virtualHash = new Hashtable();
		try {
			virtualHash = hostManager.getMemory(ipAddress, "VirtualMemory", startTime, endTime, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		List virtualList = new ArrayList();
		if (virtualHash != null && virtualHash.size() > 0) {
			virtualList = (List) virtualHash.get("list");
			try {
				if (virtualList == null || virtualList.size() == 0) {
					virtualHash = hostManager.getMemory(ipAddress, "SwapMemory", startTime, endTime, "");
					virtualList = (List) virtualHash.get("list");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Hashtable physicalHash = new Hashtable();
		try {
			physicalHash = hostManager.getMemory(ipAddress, "PhysicalMemory", startTime, endTime, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		List physicalList = new ArrayList();
		physicalList = (List) physicalHash.get("list");
		if ((virtualList != null && virtualList.size() > 0) || (physicalList != null && physicalList.size() > 0)) {
			try {
				int v_size = virtualList != null ? virtualList.size() : 0;
				int p_size = physicalList != null ? physicalList.size() : 0;
				if (v_size > p_size) {
					int num = v_size;
					for (int i = 0; i < num; i++) {
						try {
							MemoryVo mVo = new MemoryVo();
							Vector virtualVector = new Vector();
							virtualVector = (Vector) virtualList.get(i);
							Vector physicalVector = null;
							if (physicalList != null && physicalList.size() > 0 && i < physicalList.size()) {
								physicalVector = (Vector) physicalList.get(i);
							}
							mVo.setDate((String) virtualVector.get(1));
							mVo.setVirtualMemory(df.format(Double.parseDouble((String) virtualVector.get(0))));
							if (physicalVector != null && physicalVector.size() > 0) {
								if (physicalVector.get(0) != null) {
									mVo.setPhysicalMemory(df.format(Double.parseDouble((String) physicalVector.get(0))));
								} else {
									mVo.setPhysicalMemory("0");
								}
							} else {
								mVo.setPhysicalMemory("0");
							}
							flexDataList.add(mVo);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					int num = p_size;
					for (int i = 0; i < num; i++) {
						try {
							MemoryVo mVo = new MemoryVo();
							Vector physicalVector = new Vector();
							physicalVector = (Vector) physicalList.get(i);
							Vector virtualVector = null;
							if (virtualList != null && virtualList.size() > 0 && i < virtualList.size()) {
								virtualVector = (Vector) virtualList.get(i);
							}
							mVo.setDate((String) physicalVector.get(1));
							mVo.setPhysicalMemory(df.format(Double.parseDouble((String) physicalVector.get(0))));
							if (virtualVector != null && virtualVector.size() > 0) {
								if (virtualVector.get(0) != null) {
									mVo.setVirtualMemory(df.format(Double.parseDouble((String) virtualVector.get(0))));
								} else {
									mVo.setVirtualMemory("0");
								}

							} else {
								mVo.setVirtualMemory("0");
							}
							flexDataList.add(mVo);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flexDataList;

	}

	/*
	 * 获取网络设备在指定时间段的内存利用率 20100825
	 */
	public ArrayList<FlexVo> getNetMemoryByDate(String ipAddress, String dateStr, String time) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		ArrayList flexDataList = new ArrayList();
		ArrayList list = new ArrayList();
		String date[] = null;
		String startTime = "";
		String endTime = "";
		if ("".equals(dateStr)) {
			startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
			endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		} else {
			date = dateStr.split("=");
			startTime = date[0] + " 00:00:00";
			endTime = date[1] + " 23:59:59";
		}
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(ipAddress);
		String name = "";
		if (ipAllData != null) {
			Vector memoryVector = (Vector) ipAllData.get("memory");
			if (memoryVector != null && memoryVector.size() > 0) {
				for (int si = 0; si < memoryVector.size(); si++) {
					MemoryCollectEntity memodata = (MemoryCollectEntity) memoryVector.elementAt(si);
					if (memodata != null) {
						if (memodata.getEntity().equalsIgnoreCase("Utilization")) {
							name = name + memodata.getSubentity() + ";";
						}
					}
				}
			}
		}
		String memoname = "";
		String arrName[] = name.split(";");
		// 获取各内存利用率历史数据值
		if (arrName != null && arrName.length > 0) {
			Hashtable virtualHash[] = new Hashtable[arrName.length];
			for (int i = 0; i < arrName.length; i++) {
				memoname = memoname + "内存" + arrName[i];
				if (i != arrName.length - 1) {
					memoname = memoname + ",";
				}
				try {
					virtualHash[i] = hostManager.getMemory(ipAddress, arrName[i], startTime, endTime, time);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			flexDataList.add(memoname);
			if (virtualHash[0] != null) {
				List dlist = new ArrayList();
				dlist = (List) virtualHash[0].get("list");
				int dlist_len = dlist.size();
				for (int i = 0; i < dlist_len; i++) {
					Vector v = (Vector) dlist.get(i);
					Hashtable h = new Hashtable();
					h.put("objectName", v.get(1));
					for (int j = 0; j < virtualHash.length; j++) {
						List virtualList = new ArrayList();
						virtualList = (List) virtualHash[j].get("list");
						for (int k = 0; k < virtualList.size(); k++) {
							Vector vector = new Vector();
							vector = (Vector) virtualList.get(i);
							if (vector.get(1).equals(v.get(1))) {
								h.put("内存" + arrName[j], vector.get(0));
								break;
							}
						}
					}
					list.add(h);
				}
			}
			flexDataList.add(list);
		}
		return flexDataList;
	}

	public String getNodebyId(String id, String category, String time) {
		Node fnode = null;
		fnode = PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
		if (fnode.getCategory() == 4) {
			return fnode.getAlias() + ":" + fnode.getIpAddress() + ":net_server";
		} else {
			return fnode.getAlias() + ":" + fnode.getIpAddress() + ":net_";
		}
	}

	// 获取oracle表空间信息
	public ArrayList<oraVo> getOraTabSpaceInfo(String ipAddress, String id) {
		ArrayList<oraVo> flexDataList = new ArrayList<oraVo>();
		OraclePartsDao partdao = new OraclePartsDao();
		String ip = "";
		List<OracleEntity> oracleparts = new ArrayList<OracleEntity>();
		try {
			oracleparts = partdao.findOracleParts(Integer.parseInt(id), 1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (partdao != null) {
				partdao.close();
			}
		}
		if (oracleparts != null && oracleparts.size() > 0) {
			OracleEntity oracle = oracleparts.get(0);
			ip = ipAddress + ":" + oracle.getId();
		}
		Vector tableinfo_v = new Vector();
		DBDao dao = new DBDao();
		String hex = IpTranslation.formIpToHex(ip.split(":")[0]);
		String serverip = hex + ":" + ip.split(":")[1];
		try {
			tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < tableinfo_v.size(); i++) {
			oraVo oravo = new oraVo();
			Hashtable ht = (Hashtable) tableinfo_v.get(i);
			String filename = ht.get("file_name").toString();
			String tablespace = ht.get("tablespace").toString();
			String size = ht.get("size_mb").toString();
			String free = ht.get("free_mb").toString();
			String percent = ht.get("percent_free").toString();
			String status = ht.get("status").toString();
			oravo.setFile_name(filename);
			oravo.setTablespace(tablespace);
			oravo.setSize(size);
			oravo.setFree(free);
			oravo.setPercent(percent);
			oravo.setStatus(status);
			flexDataList.add(oravo);
		}
		return flexDataList;

	}

	// 业务视图-获取主机进程信息
	public String getPidByDate(String ip, String name) {
		String str = "";
		String pid = "";
		String cputimes = "";
		String memoryuse = "";
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable processhash = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		try {
			processhash = hostlastmanager.getProcess_share(ip, "Process", "MemoryUtilization", startTime, endTime);
			Hashtable phash;
			for (int m = 0; m < processhash.size(); m++) {
				phash = (Hashtable) processhash.get(m);
				if (name.equalsIgnoreCase(phash.get("Name").toString().trim())) {
					pid = pid + (String) phash.get("pid");
					cputimes = cputimes + (String) phash.get("CpuTime");
					memoryuse = memoryuse + (String) phash.get("Memory");
					if (m != processhash.size() - 1) {
						pid = pid + ",";
						cputimes = cputimes + ",";
						memoryuse = memoryuse + ",";
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		str = pid + ";" + cputimes + ";" + memoryuse;
		return str;
	}

	/*
	 * 业务视图-主机网络设备连通性
	 */
	public ArrayList<FlexVo> getPingByDay(String ipAddress, String time) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable pingHash = new Hashtable();
		try {
			pingHash = hostManager.getPingData(ipAddress, "ConnectUtilization", startTime, endTime, "", time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List pingList = new ArrayList();
		pingList = (List) pingHash.get("list");
		if (pingList != null && pingList.size() > 0) {
			int num = pingList.size();
			for (int i = 0; i < num; i++) {
				FlexVo fVo = new FlexVo();
				Vector pingVector = new Vector();
				pingVector = (Vector) pingList.get(i);
				if (pingVector != null) {
					fVo.setObjectName((String) pingVector.get(1));
					fVo.setObjectNumber((String) pingVector.get(0));
					flexDataList.add(fVo);
				}
			}
		}
		return flexDataList;
	}

	/*
	 * 获取端口服务在指定时间段的连通率
	 */
	public ArrayList<FlexVo> getPortPing(String ip, String dateStr, String port) {
		PortServiceTypeManager portServiceTypeManager = new PortServiceTypeManager();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String date[] = dateStr.split("=");
		String starttime = date[0] + " 00:00:00";
		String endTime = date[1] + " 23:59:59";
		Hashtable pingHash = new Hashtable();
		try {
			pingHash = portServiceTypeManager.getCategory(ip, "SOCKETPing", "ConnectUtilization", starttime, endTime, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String avg = (String) pingHash.get("avgpingcon");
		if (avg != null && !"".equals(avg)) {
			float ping = Float.parseFloat(avg.replace("%", ""));
			FlexVo fVo1 = new FlexVo();
			fVo1.setObjectName("连通");
			fVo1.setObjectNumber(ping + "");
			flexDataList.add(fVo1);
			FlexVo fVo2 = new FlexVo();
			fVo2.setObjectName("未连通");
			fVo2.setObjectNumber((100.0 - ping) + "");
			flexDataList.add(fVo2);
		}
		return flexDataList;
	}

	public ArrayList getProByDate(String ip, String pids[]) {
		ArrayList list = new ArrayList();
		I_HostCollectData hostmanager = new HostCollectDataManager();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		DecimalFormat df = new DecimalFormat("#.##");
		try {
			for (int j = 0; j < pids.length; j++) {
				if (pids[j] != null && !"".equalsIgnoreCase(pids[j])) {
					ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
					Hashtable hash = hostmanager.getCategory(ip, "Process", pids[j], startTime, endTime, "");
					List memList = new ArrayList();
					memList = (List) hash.get("list");
					if (memList != null && memList.size() > 0) {
						int num = memList.size();
						for (int i = 0; i < num; i++) {
							FlexVo fVo = new FlexVo();
							Vector pingVector = new Vector();
							pingVector = (Vector) memList.get(i);
							if (pingVector != null) {
								fVo.setObjectName((String) pingVector.get(1));
								fVo.setObjectNumber(df.format(Double.parseDouble((String) pingVector.get(0))));
								flexDataList.add(fVo);
							}
						}
					}
					list.add(flexDataList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// 获取进程表信息
	public ArrayList<ProcessVo> getProcessList(String node) {
		DateE datemanager = new DateE();
		ArrayList<ProcessVo> flexDataList = new ArrayList<ProcessVo>();
		// 从内存中获取节点信息
		Host fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = (Host) PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		Calendar current = new GregorianCalendar();
		current.set(Calendar.MINUTE, 59);
		current.set(Calendar.SECOND, 59);
		String endtime = datemanager.getDateDetail(current);
		current.add(Calendar.HOUR_OF_DAY, -1);
		current.set(Calendar.MINUTE, 0);
		current.set(Calendar.SECOND, 0);
		String starttime = datemanager.getDateDetail(current);
		Hashtable processhash = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		String order = "MemoryUtilization";
		String runmodel = PollingEngine.getCollectwebflag();
		try {
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				processhash = hostlastmanager.getProcess_share(fnode.getIpAddress(), "Process", order, starttime, endtime);
			} else {
				// 采集与访问是分离模式
				processhash = hostlastmanager.getProcess(fnode.getIpAddress(), "Process", order, starttime, endtime);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		Hashtable phash;// 原，用于计算的中间变量
		Hashtable newProcessHash = new Hashtable();// 存储排序后的hashtable
		Hashtable detailHash = new Hashtable();
		Pattern p1 = Pattern.compile("(\\d+):(\\d+)");
		if (processhash != null) {
			for (int m = 0; m < processhash.size(); m++) {
				phash = (Hashtable) processhash.get(new Integer(m));
				if (phash != null) {
					String Name = ((String) phash.get("Name")).trim();
					if (newProcessHash.containsKey(Name)) {
						ProcessInfo totalProcess = (ProcessInfo) newProcessHash.get(Name);
						String CpuTime = (String) phash.get("CpuTime");
						String CpuUtilization = (String) phash.get("CpuUtilization");
						String Memory = (String) phash.get("Memory");
						String MemoryUtilization = (String) phash.get("MemoryUtilization");
						String threadCount = (String) phash.get("ThreadCount");
						String handleCount = (String) phash.get("HandleCount");
						if (CpuTime != null) {
							if (CpuTime.indexOf(":") != -1) {
								Matcher matcher = p1.matcher(CpuTime);
								if (matcher.find()) {
									String t1 = matcher.group(1);
									String t2 = matcher.group(2);
									float sumOfCPU = Float.parseFloat(t1) * 60 + Float.parseFloat(t2);
									totalProcess.setCpuTime(sumOfCPU + (Float) totalProcess.getCpuTime());
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
								totalProcess.setCpuTime(sumOfCPU + (Float) totalProcess.getCpuTime());
							}
						}
						Float sumOfCpuUtilization = 0f;
						if (CpuUtilization != null && CpuUtilization.trim().length() > 0) {
							sumOfCpuUtilization = Float.parseFloat(CpuUtilization.substring(0, CpuUtilization.length() - 1));
							totalProcess.setCpuUtilization(sumOfCpuUtilization + (Float) totalProcess.getCpuUtilization());
						} else {
							totalProcess.setCpuUtilization("-");
						}

						Float sumOfMem = Float.valueOf("0");
						if (Memory.trim().length() > 1) {
							sumOfMem = Float.parseFloat(Memory.substring(0, Memory.length() - 1));
						}
						totalProcess.setMemory(sumOfMem + (Float) totalProcess.getMemory());
						NumberFormat numberFormat = new DecimalFormat();
						numberFormat.setMaximumFractionDigits(0);

						Float sumOfMemUtilization = Float.valueOf("0");
						if (MemoryUtilization.trim().length() > 1) {
							sumOfMemUtilization = Float.parseFloat(MemoryUtilization.substring(0, MemoryUtilization.length() - 1));
						}
						Float memoryUtilization = sumOfMemUtilization + (Float) totalProcess.getMemoryUtilization();

						totalProcess.setMemoryUtilization(memoryUtilization);

						if (threadCount != null) {
							totalProcess.setThreadCount(threadCount);
						}
						if (handleCount != null) {
							totalProcess.setHandleCount(handleCount);
						}
						ProcessInfo processInfo = new ProcessInfo();
						processInfo.setName((String) phash.get("Name"));
						processInfo.setType((String) phash.get("Type"));
						processInfo.setStatus((String) phash.get("Status"));
						if (CpuTime != null) {
							if (CpuTime.indexOf(":") != -1) {
								Matcher matcher2 = p1.matcher(CpuTime);
								if (matcher2.find()) {
									String t1 = matcher2.group(1);
									String t2 = matcher2.group(2);
									float sumOfCPU = Float.parseFloat(t1) * 60 + Float.parseFloat(t2);
									processInfo.setCpuTime(sumOfCPU);
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
								processInfo.setCpuTime(sumOfCPU);
							}
						}

						processInfo.setUSER((String) phash.get("USER"));
						processInfo.setStartTime((String) phash.get("StartTime"));

						processInfo.setCpuUtilization(sumOfCpuUtilization);

						processInfo.setPid((String) phash.get("process_id"));
						processInfo.setMemoryUtilization(sumOfMemUtilization);
						processInfo.setMemory(sumOfMem);
						processInfo.setThreadCount(threadCount);
						processInfo.setHandleCount(handleCount);
						((Vector) detailHash.get(((String) phash.get("Name")).trim())).add(processInfo);

					} else {
						ProcessInfo processInfo = new ProcessInfo();
						processInfo.setName(Name);
						processInfo.setUSER((String) phash.get("USER"));
						processInfo.setType((String) phash.get("Type"));
						processInfo.setStatus((String) phash.get("Status"));

						String CpuTime = (String) phash.get("CpuTime");
						if (CpuTime != null) {
							if (CpuTime.indexOf(":") != -1) {
								Matcher matcher = p1.matcher(CpuTime);
								if (matcher.find()) {
									String t1 = matcher.group(1);
									String t2 = matcher.group(2);
									float sumOfCPU = Float.parseFloat(t1) * 60 + Float.parseFloat(t2);
									processInfo.setCpuTime(sumOfCPU);
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime.replace("秒", ""));
								processInfo.setCpuTime(sumOfCPU);
							}
						}

						String MemoryUtilization = (String) phash.get("MemoryUtilization");
						Float sumOfMemUtilization = Float.valueOf("0");
						if (MemoryUtilization.trim().length() > 1) {
							sumOfMemUtilization = Float.parseFloat(MemoryUtilization.substring(0, MemoryUtilization.length() - 1));
						}
						processInfo.setMemoryUtilization(sumOfMemUtilization);

						String Memory = (String) phash.get("Memory");
						Float sumOfMem = Float.valueOf("0");
						if (Memory.trim().length() > 1) {
							sumOfMem = Float.parseFloat(Memory.substring(0, Memory.length() - 1));
						}
						processInfo.setMemory(sumOfMem);

						String CpuUtilization = (String) phash.get("CpuUtilization");
						if (CpuUtilization != null && CpuUtilization.trim().length() > 0) {
							Float sumOfCpuUtilization = Float.parseFloat(CpuUtilization.substring(0, CpuUtilization.length() - 1));
							processInfo.setCpuUtilization(sumOfCpuUtilization);
						} else {
							processInfo.setCpuUtilization("-");
						}
						processInfo.setPid((String) phash.get("process_id"));
						String threadCount = (String) phash.get("ThreadCount");
						processInfo.setThreadCount(threadCount);
						String handleCount = (String) phash.get("HandleCount");
						processInfo.setHandleCount(handleCount);
						ProcessInfo newProcessInfo = processInfo.clone();
						newProcessHash.put(Name, processInfo);
						Vector detailVect = new Vector();
						detailVect.add(newProcessInfo);
						detailHash.put(Name, detailVect);
					}
				}
			}
		}
		Enumeration newProEnu = newProcessHash.keys();
		while (newProEnu.hasMoreElements()) {
			String processName = (String) newProEnu.nextElement();
			ProcessInfo p = (ProcessInfo) newProcessHash.get(processName);
			Vector v = (Vector) detailHash.get(processName);
			p.setCount(v.size());
		}

		String processName = null;
		Vector detailVect = null;
		if (processName != null) {
			if (detailHash.containsKey(processName)) {
				detailVect = (Vector) detailHash.get(processName);
			}
		}
		List list = new ArrayList();
		if (detailVect == null) {
			Collection collection = newProcessHash.values();
			Iterator it = collection.iterator();

			while (it.hasNext()) {
				ProcessInfo info = (ProcessInfo) it.next();
				list.add(info);
			}
		} else {
			list = detailVect;
		}
		dateFormat(list);
		ProcessInfo processInfo = null;
		try {
			for (int i = 0; i < list.size(); i++) {
				processInfo = (ProcessInfo) list.get(i);
				ProcessVo Vo = new ProcessVo();
				Vo.setCount(processInfo.getCount() + "");
				Vo.setCputime(processInfo.getCpuTime().toString());
				Vo.setCpuUtilization(processInfo.getCpuUtilization().toString());
				Vo.setMemory(processInfo.getMemory().toString());
				Vo.setMemoryUtilization(processInfo.getMemoryUtilization().toString());
				Vo.setName(processInfo.getName());
				Vo.setStatus(processInfo.getStatus());
				Vo.setTypes(processInfo.getType());
				flexDataList.add(Vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flexDataList;
	}

	/*
	 * 业务视图-获取一台网络设备在当天响应时间的值
	 */
	public ArrayList<FlexVo> getResponseTimeByDay(String ipAddress) {
		I_HostCollectData hostManager = new HostCollectDataManager();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable responseHash = new Hashtable();
		try {
			responseHash = hostManager.getPingData(ipAddress, "ResponseTime", startTime, endTime, "", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		List responseList = new ArrayList();
		responseList = (List) responseHash.get("list");
		if (responseList != null && responseList.size() > 0) {
			int num = responseList.size();
			for (int i = 0; i < num; i++) {
				FlexVo fVo = new FlexVo();
				Vector responseVector = new Vector();
				responseVector = (Vector) responseList.get(i);
				if (responseVector != null) {
					fVo.setObjectName((String) responseVector.get(1));
					fVo.setObjectNumber((String) responseVector.get(0));
					flexDataList.add(fVo);
				}
			}
		}
		return flexDataList;

	}

	// 获取路由表信息
	public ArrayList<RouteVo> getRouteList(String node) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ArrayList<RouteVo> flexDataList = new ArrayList<RouteVo>();
		String[] iproutertype = { "", "", "", "direct(3)", "indirect(4)" };
		String[] iprouterproto = { "", "other(1)", "local(2)", "netmgmt(3)", "icmp(4)", "egp(5)", "ggp(6)", "hello(7)", "rip(8)", "is-is(9)", "es-is(10)", "ciscoIgrp(11)",
				"bbnSpfIgp(12)", "ospf(13)", "bgp(14)" };

		Vector iproutervector = new Vector();
		// 从内存中获取节点信息
		Host fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = (Host) PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			// 从内存中获得当前的跟此IP相关的IP-ROUTE的ARP表信息
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			iproutervector = (Vector) _IpRouterHash.get(fnode.getIpAddress());
		} else {

		}
		try {
			if (iproutervector != null) {
				for (int i = 0; i < iproutervector.size(); i++) {
					IpRouter iprouter = (IpRouter) iproutervector.get(i);
					Date cc = iprouter.getCollecttime().getTime();
					String routetype = "";
					routetype = iproutertype[Integer.parseInt(iprouter.getType().longValue() + "")];
					String routeproto = "";
					routeproto = iprouterproto[Integer.parseInt(iprouter.getProto().longValue() + "")];
					RouteVo Vo = new RouteVo();
					Vo.setCollecttime(sdf1.format(cc));
					Vo.setIfindex(iprouter.getIfindex());
					Vo.setDest(iprouter.getDest());
					Vo.setNexthop(iprouter.getNexthop());
					Vo.setRouteproto(routeproto);
					Vo.setRoutetype(routetype);
					Vo.setMask(iprouter.getMask());
					flexDataList.add(Vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return flexDataList;

	}

	// 获取service表信息
	public ArrayList<ServiceVo> getServiceList(String node) {
		String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
		ArrayList<ServiceVo> flexDataList = new ArrayList<ServiceVo>();
		// 从内存中获取节点信息
		Host fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = (Host) PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(fnode);
		List servicelist = new ArrayList();
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(fnode.getIpAddress());
			// 得到IPMAC信息
			servicelist = (List) ipAllData.get("servicelist");
			if (servicelist == null) {
				servicelist = new ArrayList();
			}
		} else {
			ServiceInfoService serviceInfoService = new ServiceInfoService(nodedto.getId() + "", nodedto.getType(), nodedto.getSubtype());
			servicelist = serviceInfoService.getServicelistInfo();
		}
		try {
			if (servicelist != null && servicelist.size() > 0) {
				Hashtable phash = new Hashtable();
				for (int i = 0; i < servicelist.size(); i++) {
					phash = (Hashtable) servicelist.get(i);
					ServiceVo Vo = new ServiceVo();
					Vo.setGroup((String) phash.get("groupstr"));
					Vo.setName((String) phash.get("DisplayName"));
					Vo.setPid((String) phash.get("pid"));
					Vo.setStatus((String) phash.get("State"));
					flexDataList.add(Vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return flexDataList;

	}

	// 获得sybase数据库信息
	public ArrayList<Vo> getSybaseInfo(String ipAddress) {
		ArrayList<Vo> flexDataList = new ArrayList<Vo>();
		// 获取sybase信息
		SybaseVO sysbaseVO = new SybaseVO();
		String hex = IpTranslation.formIpToHex(ipAddress);
		DBDao dao = new DBDao();
		DBVo dbvo = (DBVo) dao.findByCondition("ip_address", ipAddress, 6).get(0);
		String serverip = hex + ":" + dbvo.getId();
		sysbaseVO = dao.getSybaseDataByServerip(serverip);
		dao.close();
		if (sysbaseVO != null) {
			List dbsizelist = (List) sysbaseVO.getDbInfo();
			if (dbsizelist != null && dbsizelist.size() > 0) {
				for (int i = 0; i < dbsizelist.size(); i++) {
					TablesVO tablesVO = (TablesVO) dbsizelist.get(i);
					Vo vo = new Vo();
					vo.setObjectName(tablesVO.getDb_name() + "(" + tablesVO.getDb_usedperc() + "%)");
					vo.setObjectNumber1(tablesVO.getDb_size());
					vo.setObjectNumber2(tablesVO.getDb_freesize());
					flexDataList.add(vo);
				}
			}
		}
		return flexDataList;
	}

	// 获取syslog表信息
	public ArrayList<SyslogVo> getSyslogList(String node) {
		String runmodel = PollingEngine.getCollectwebflag();
		String hostname = "";
		Nodeconfig nodeconfig = new Nodeconfig();
		ArrayList<SyslogVo> flexDataList = new ArrayList<SyslogVo>();
		// 从内存中获取节点信息
		Host fnode = null;
		TreeNodeDao ftreeNodeDao = new TreeNodeDao();
		TreeNode fvo = null;
		try {
			fvo = (TreeNode) ftreeNodeDao.findByNodeTag(node.substring(0, 3));
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			ftreeNodeDao.close();
		}
		if (fvo != null) {
			fnode = (Host) PollingEngine.getInstance().getNodeByCategory(fvo.getName(), Integer.parseInt(node.substring(3)));
		}
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(fnode);
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(fnode.getIpAddress());
			if (ipAllData != null) {
				nodeconfig = (Nodeconfig) ipAllData.get("nodeconfig");
				if (nodeconfig != null) {
					hostname = nodeconfig.getHostname();
				}
			}
		} else {
			NodeconfigDao nodeconfigDao = new NodeconfigDao();
			try {
				nodeconfig = nodeconfigDao.getByNodeID(nodedto.getId() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodeconfigDao.close();
			}
			if (nodeconfig != null) {
				hostname = nodeconfig.getHostname();
			}
		}
		List list = new ArrayList();
		String b_time = null;
		String t_time = null;
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime2 = b_time + " 00:00:00";
		String totime2 = t_time + " 23:59:59";
		String priorityname = "all";
		SyslogDao syslogdao = new SyslogDao();
		try {
			list = syslogdao.getQuery(fnode.getIpAddress(), starttime2, totime2, priorityname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd HH:mm");
		if (list == null) {
			list = new ArrayList();
		}
		for (int i = 0; i < list.size(); i++) {
			Syslog syslog = (Syslog) list.get(i);
			Date cc = syslog.getRecordtime().getTime();
			SyslogVo Vo = new SyslogVo();
			Vo.setBak(syslog.getUsername());
			Vo.setCtime(sdf.format(cc));
			Vo.setEventid(syslog.getEventid() + "");
			Vo.setHostname(hostname);
			Vo.setPriorityName(syslog.getPriorityName());
			Vo.setProcessName(syslog.getProcessname());
			flexDataList.add(Vo);
		}
		return flexDataList;
	}

	// 按日期获取温湿度信息
	public ArrayList<Vo> getTCByDate(String id, String dateStr) {
		ArrayList<Vo> flexDataList = new ArrayList<Vo>();
		String datestr[] = dateStr.split("=");
		if (datestr.length == 2) {
			if (java.sql.Date.valueOf(datestr[0]).after(java.sql.Date.valueOf(datestr[1]))) {
				System.out.println("起始日期大于截止日期!");
			} else {
				String starttime = datestr[0] + " 00:00:00";
				String endTime = datestr[1] + " 23:59:59";
				TemperatureHumidityDao temperatureHumidityDao = new TemperatureHumidityDao();
				List list = null;
				try {
					list = temperatureHumidityDao.findByNodeIdAndTime(id, starttime, endTime);
				} catch (RuntimeException e) {
					e.printStackTrace();
				} finally {
					temperatureHumidityDao.close();
				}
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						Vo vo = new Vo();
						TemperatureHumidityConfig tvo = (TemperatureHumidityConfig) list.get(i);
						vo.setObjectName(tvo.getTime());
						vo.setObjectNumber1(tvo.getTemperature());
						vo.setObjectNumber2(tvo.getHumidity());
						flexDataList.add(vo);
					}
				}
			}
		}
		return flexDataList;
	}

	// 业务视图-获取tomcatJVM内存利用率数据
	public ArrayList<FlexVo> getTomcatJvmByDay(String ipAddress, String time) {
		TomcatManager tomcatManager = new TomcatManager();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable pingHash = new Hashtable();
		try {
			pingHash = tomcatManager.getCategory(ipAddress, "tomcat_jvm", "jvm_utilization", startTime, endTime, time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List pingList = new ArrayList();
		pingList = (List) pingHash.get("list");
		if (pingList != null && pingList.size() > 0) {
			int num = pingList.size();
			for (int i = 0; i < num; i++) {
				FlexVo fVo = new FlexVo();
				Vector pingVector = new Vector();
				pingVector = (Vector) pingList.get(i);
				if (pingVector != null) {
					fVo.setObjectName((String) pingVector.get(1));
					fVo.setObjectNumber((String) pingVector.get(0));
					flexDataList.add(fVo);
				}
			}
		}
		return flexDataList;

	}

	// 业务视图-获取tomcat当天可用性
	public ArrayList<FlexVo> getTomcatPieByDay(String ipAddress) {
		TomcatManager tomcatManager = new TomcatManager();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable pingHash = new Hashtable();
		try {
			pingHash = tomcatManager.getCategory(ipAddress, "TomcatPing", "ConnectUtilization", startTime, endTime, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String avgping = (String) pingHash.get("avgpingcon");
		if (avgping != null && !"".equals(avgping)) {
			FlexVo fVo1 = new FlexVo();
			fVo1.setObjectName("可用");
			fVo1.setObjectNumber(avgping.replace("%", ""));
			flexDataList.add(fVo1);
			FlexVo fVo2 = new FlexVo();
			fVo2.setObjectName("不可用");
			fVo2.setObjectNumber((100 - Float.parseFloat(avgping.replace("%", ""))) + "");
			flexDataList.add(fVo2);
		}
		return flexDataList;

	}

	// 业务视图-获取tomcat连通率数据
	public ArrayList<FlexVo> getTomcatPingByDay(String ipAddress, String time) {
		TomcatManager tomcatManager = new TomcatManager();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
		String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		Hashtable pingHash = new Hashtable();
		try {
			pingHash = tomcatManager.getCategory(ipAddress, "TomcatPing", "ConnectUtilization", startTime, endTime, time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List pingList = new ArrayList();
		pingList = (List) pingHash.get("list");
		if (pingList != null && pingList.size() > 0) {
			int num = pingList.size();
			for (int i = 0; i < num; i++) {
				FlexVo fVo = new FlexVo();
				Vector pingVector = new Vector();
				pingVector = (Vector) pingList.get(i);
				if (pingVector != null) {
					fVo.setObjectName((String) pingVector.get(1));
					fVo.setObjectNumber((String) pingVector.get(0));
					flexDataList.add(fVo);
				}
			}
		}
		return flexDataList;

	}

	/*
	 * 业务视图-获取web服务在指定时间段的可用性
	 */
	public ArrayList<FlexVo> getWebPieByDate(String id, String dateStr) {
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String date[] = dateStr.split("=");
		String startTime = date[0] + " 00:00:00";
		String endTime = date[1] + " 23:59:59";
		String conn[] = new String[2];
		Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();
		try {
			conn = historydao.getAvailability(Integer.parseInt(id), startTime, endTime, "is_canconnected");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			historydao.close();
		}
		if (conn[0] != null && !"".equals(conn[0]) && conn[1] != null && !"".equals(conn[1])) {
			FlexVo fVo1 = new FlexVo();
			fVo1.setObjectName("可用");
			fVo1.setObjectNumber(conn[0]);
			flexDataList.add(fVo1);
			FlexVo fVo2 = new FlexVo();
			fVo2.setObjectName("不可用");
			fVo2.setObjectNumber(conn[1]);
			flexDataList.add(fVo2);
		}
		return flexDataList;
	}

	// 业务视图-web连通性曲线
	public ArrayList<FlexVo> getWebPingByDate(String id, String dateStr) {
		Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String datestr[] = dateStr.split("=");
		if (datestr.length == 2) {
			if (java.sql.Date.valueOf(datestr[0]).after(java.sql.Date.valueOf(datestr[1]))) {
				System.out.println("起始日期大于截止日期!");
			} else {
				String starttime = datestr[0] + " 00:00:00";
				String endTime = datestr[1] + " 23:59:59";
				Hashtable pingHash = new Hashtable();
				try {
					pingHash = historydao.getPingData(Integer.parseInt(id), starttime, endTime, "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					historydao.close();
				}
				List pingList = new ArrayList();
				pingList = (List) pingHash.get("list");
				if (pingList != null && pingList.size() > 0) {
					int num = pingList.size();
					for (int i = 0; i < num; i++) {
						FlexVo fVo = new FlexVo();
						Vector pingVector = new Vector();
						pingVector = (Vector) pingList.get(i);
						if (pingVector != null) {
							fVo.setObjectName((String) pingVector.get(1));
							fVo.setObjectNumber((String) pingVector.get(0) + "00");
							flexDataList.add(fVo);
						}
					}
				}
			}
		}
		return flexDataList;
	}

	// 业务视图-web延时
	public ArrayList<FlexVo> getWebResponseByDate(String id, String dateStr) {
		Urlmonitor_historyDao historydao = new Urlmonitor_historyDao();
		ArrayList<FlexVo> flexDataList = new ArrayList<FlexVo>();
		String datestr[] = dateStr.split("=");
		if (datestr.length == 2) {
			if (java.sql.Date.valueOf(datestr[0]).after(java.sql.Date.valueOf(datestr[1]))) {
				System.out.println("起始日期大于截止日期!");
			} else {
				String starttime = datestr[0] + " 00:00:00";
				String endTime = datestr[1] + " 23:59:59";
				Hashtable pingHash = new Hashtable();
				try {
					pingHash = historydao.getPingData(Integer.parseInt(id), starttime, endTime, "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					historydao.close();
				}
				List pingList = new ArrayList();
				pingList = (List) pingHash.get("list");
				if (pingList != null && pingList.size() > 0) {
					int num = pingList.size();
					for (int i = 0; i < num; i++) {
						FlexVo fVo = new FlexVo();
						Vector pingVector = new Vector();
						pingVector = (Vector) pingList.get(i);
						if (pingVector != null) {
							fVo.setObjectName((String) pingVector.get(1));
							fVo.setObjectNumber((String) pingVector.get(2));
							flexDataList.add(fVo);
						}
					}
				}
			}
		}
		return flexDataList;
	}

	private String subString(String str) {
		int len = str.length();
		if (len > 0 && str.indexOf(":") != -1) {
			return str.substring(0, 3);
		} else {
			return str;
		}
	}

}
