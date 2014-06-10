/*
 * Created on 2005-4-22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.Arith;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.UtilHdx;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.Link;

@SuppressWarnings("unchecked")
public class CheckLinkTask extends MonitorTask {
	private Logger logger = Logger.getLogger(this.getClass());

	public void run() {
		logger.info(" 开始检查链路 ");
		try {
			List linkList = PollingEngine.getInstance().getLinkList();
			ThreadPool threadPool = null;
			if (linkList != null && linkList.size() > 0) {
				threadPool = new ThreadPool(linkList.size());
				if (ShareData.getRunflag() == 0) {
					ShareData.setRunflag(1);
				} else {
					// 先集中采集所有链路节点的数据集合存到内存中 线程中不再做单个链路节点的数据采集
					getLinknodeInterfaceData(linkList);
					for (int i = 0; i < linkList.size(); i++) {
						threadPool.runTask(createTask((LinkRoad) linkList.get(i)));
					}
				}
				threadPool.join();
				threadPool.close();
			}
			threadPool = null;

			// 批量保存link数据
			processLinkData();
		} catch (Exception e) {
		}
	}

	/**
	 * 先集中采集所有链路节点的数据集合存到内存中
	 * 
	 * @param linkList
	 */
	public static Hashtable getLinknodeInterfaceData(List linkList) {
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "ifOutBroadcastPkts", "ifInBroadcastPkts", "ifOutMulticastPkts", "ifInMulticastPkts",
				"OutBandwidthUtilHdx", "InBandwidthUtilHdx", "InBandwidthUtilHdxPerc", "OutBandwidthUtilHdxPerc" };
		List<String> ipList = new ArrayList<String>();
		for (int m = 0; m < linkList.size(); m++) {
			Object obj = linkList.get(m);
			Host host1 = null;
			Host host2 = null;
			if (obj instanceof LinkRoad) {
				LinkRoad lr = (LinkRoad) linkList.get(m);
				host1 = (Host) PollingEngine.getInstance().getNodeByID(lr.getStartId());
				host2 = (Host) PollingEngine.getInstance().getNodeByID(lr.getEndId());
			}
			if (obj instanceof Link) {
				Link lr = (Link) linkList.get(m);
				host1 = (Host) PollingEngine.getInstance().getNodeByID(lr.getStartId());
				host2 = (Host) PollingEngine.getInstance().getNodeByID(lr.getEndId());
			}
			if (host1 != null) {
				ipList.add(host1.getIpAddress());
			}
			if (host2 != null) {
				ipList.add(host2.getIpAddress());
			}
		}
		// 排除重复元素
		HashSet h = new HashSet(ipList);
		ipList.clear();
		ipList.addAll(h);

		Hashtable interfaceHash = hostlastmanager.getInterfaces(ipList, netInterfaceItem, "index", "", "");
		// 存入内存
		ShareData.setAllLinknodeInterfaceData(interfaceHash);
		return interfaceHash;
	}

	/**
	 * 批量保存link数据
	 */
	private void processLinkData() {
		if (ShareData.getAllLinkData() != null && !ShareData.getAllLinkData().isEmpty()) {
			LinkDao linkDao = null;
			try {
				linkDao = new LinkDao();
				linkDao.processlinkData();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (linkDao != null) {
					linkDao.close();
				}
			}
		}
	}

	/**
	 * 创建任务
	 */
	private Runnable createTask(final LinkRoad linkroad) {
		return new Runnable() {
			public void run() {
				// 开始运行任务
				try {
					// 需要做分布式判断
					String runmodel = PollingEngine.getCollectwebflag();
					// 链路节点的数据集合
					Hashtable interfaceHash = ShareData.getAllLinknodeInterfaceData();
					int flag = 0;
					LinkRoad lr = linkroad;
					Host host1 = (Host) PollingEngine.getInstance().getNodeByID(lr.getStartId());
					Host host2 = (Host) PollingEngine.getInstance().getNodeByID(lr.getEndId());

					String start_inutilhdx = "0";
					String start_oututilhdx = "0";
					String start_inutilhdxperc = "0";
					String start_oututilhdxperc = "0";
					String end_inutilhdx = "0";
					String end_oututilhdx = "0";
					String end_inutilhdxperc = "0";
					String end_oututilhdxperc = "0";
					String starOper = "";
					String endOper = "";
					if (host1 != null && host2 != null) {
						I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

						Vector vector1 = new Vector();
						Vector vector2 = new Vector();
						String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "ifOutBroadcastPkts", "ifInBroadcastPkts", "ifOutMulticastPkts",
								"ifInMulticastPkts", "OutBandwidthUtilHdx", "InBandwidthUtilHdx", "InBandwidthUtilHdxPerc", "OutBandwidthUtilHdxPerc" };
						if ("0".equals(runmodel)) {
							// 采集与访问是集成模式
							vector1 = hostlastmanager.getInterface_share(host1.getIpAddress(), netInterfaceItem, "index", "", "");
							vector2 = hostlastmanager.getInterface_share(host2.getIpAddress(), netInterfaceItem, "index", "", "");
						} else {
							// 采集与访问是分离模式
							if (interfaceHash != null && interfaceHash.containsKey(host1.getIpAddress())) {
								vector1 = (Vector) interfaceHash.get(host1.getIpAddress());
							}
							if (interfaceHash != null && interfaceHash.containsKey(host2.getIpAddress())) {
								vector2 = (Vector) interfaceHash.get(host2.getIpAddress());
							}
						}

						if (vector1 != null && vector1.size() > 0) {
							for (int k = 0; k < vector1.size(); k++) {
								String[] strs = (String[]) vector1.get(k);
								String index = strs[0];
								if (index.equalsIgnoreCase(lr.getStartIndex())) {
									starOper = strs[3];
									start_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("Kb/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");// yangjun
									start_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("Kb/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");// yangjun
									start_oututilhdxperc = strs[10].replaceAll("%", "");
									start_inutilhdxperc = strs[11].replaceAll("%", "");
								}
							}
						}
						if (vector2 != null && vector2.size() > 0) {
							for (int k = 0; k < vector2.size(); k++) {
								String[] strs = (String[]) vector2.get(k);
								String index = strs[0];
								if (index.equalsIgnoreCase(lr.getEndIndex())) {
									endOper = strs[3];
									end_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("Kb/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");// yangjun
									end_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("Kb/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");// yangjun
									end_oututilhdxperc = strs[10].replaceAll("%", ""); 
									end_inutilhdxperc = strs[11].replaceAll("%", ""); 
								}
							}
						}
						boolean flags = isNumberic(start_oututilhdx);
						if (!flags)
							start_oututilhdx = "0";
						flags = isNumberic(end_inutilhdx);
						if (!flags)
							end_inutilhdx = "0";
						flags = isNumberic(start_inutilhdx);
						if (!flags)
							start_inutilhdx = "0";
						flags = isNumberic(end_oututilhdx);
						if (!flags)
							end_oututilhdx = "0";
						int downspeed = (Integer.parseInt(start_oututilhdx) + Integer.parseInt(end_inutilhdx.replaceAll("KB/秒", "").replaceAll("Kb/秒", "").replaceAll("kb/s", "")
								.replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;
						int upspeed = (Integer.parseInt(start_inutilhdx) + Integer.parseInt(end_oututilhdx.replaceAll("KB/秒", "").replaceAll("Kb/秒", "").replaceAll("kb/s", "")
								.replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;
						double downperc = 0;
						try {
							if (start_oututilhdxperc != null && start_oututilhdxperc.trim().length() > 0 && end_inutilhdxperc != null && end_inutilhdxperc.trim().length() > 0)
								downperc = Arith.div((Double.parseDouble(start_oututilhdxperc) + Double.parseDouble(end_inutilhdxperc)), 2);
						} catch (Exception e) {
							e.printStackTrace();
						}
						double upperc = 0;

						flags = isNumberic(start_inutilhdxperc);
						if (!flags)
							start_inutilhdxperc = "0";
						flags = isNumberic(end_oututilhdxperc);
						if (!flags)
							end_oututilhdxperc = "0";
						try {
							if (start_inutilhdxperc != null && start_inutilhdxperc.trim().length() > 0 && end_oututilhdxperc != null && end_oututilhdxperc.trim().length() > 0)
								upperc = Arith.div((Double.parseDouble(start_inutilhdxperc) + Double.parseDouble(end_oututilhdxperc)), 2);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (starOper != null) {
							if ("down".equalsIgnoreCase(starOper) || "".equalsIgnoreCase(starOper)) {
								lr.setStarOper("down");
								lr.setAlarm(true);
								lr.setLevels(2);
								flag = 1;
							} else {
								lr.setStarOper("up");
							}
						} else {
							lr.setStarOper("down");
							lr.setAlarm(true);
							lr.setLevels(1);
							flag = 1;
						}
						if (endOper != null) {
							if ("down".equalsIgnoreCase(endOper) || "".equalsIgnoreCase(endOper)) {
								lr.setEndOper("down");
								lr.setAlarm(true);
								lr.setLevels(2);
								flag = 1;
							} else {
								lr.setEndOper("up");
							}
						} else {
							lr.setEndOper("down");
							lr.setAlarm(true);
							lr.setLevels(1);
							flag = 1;
						}

						// 将链路状态插入数据库
						Calendar date = Calendar.getInstance();
						int linkflag = 100;
						if ("down".equalsIgnoreCase(starOper) || "down".equalsIgnoreCase(endOper)) {
							linkflag = 0;
							downspeed = 0;
							upspeed = 0;
							upperc = 0;
							downperc = 0;
						}
						Interfacecollectdata interfacelink = new Interfacecollectdata();
						interfacelink.setIpaddress("");
						interfacelink.setCollecttime(date);
						interfacelink.setCategory("Ping");
						interfacelink.setEntity("Utilization");
						interfacelink.setSubentity("ConnectUtilization");
						interfacelink.setRestype("dynamic");
						interfacelink.setUnit("%");
						interfacelink.setChname("");
						interfacelink.setThevalue(linkflag + "");
						Vector linkstatusv = new Vector();
						linkstatusv.add(interfacelink);

						Vector v = new Vector();
						UtilHdx uputilhdx = new UtilHdx();
						uputilhdx.setIpaddress("");
						uputilhdx.setCollecttime(date);
						uputilhdx.setCategory("");
						uputilhdx.setEntity("UP");
						uputilhdx.setSubentity("");
						uputilhdx.setRestype("dynamic");
						uputilhdx.setUnit("Kb/秒");
						uputilhdx.setChname("");
						DecimalFormat df = new DecimalFormat("#.##");
						uputilhdx.setThevalue(df.format(upspeed));
						v.add(uputilhdx);

						UtilHdx downutilhdx = new UtilHdx();
						downutilhdx.setIpaddress("");
						downutilhdx.setCollecttime(date);
						downutilhdx.setCategory("");
						downutilhdx.setEntity("DOWN");
						downutilhdx.setSubentity("");
						downutilhdx.setRestype("dynamic");
						downutilhdx.setUnit("Kb/秒");
						downutilhdx.setChname("");
						downutilhdx.setThevalue(df.format(downspeed));
						v.add(downutilhdx);

						String startif = lr.getStartDescr();// 添加链路流速超过阀值告警、带宽占用率超过阀值告警，加入告警至告警列表中
						String endif = lr.getEndDescr();
						String content = "链路" + lr.getStartIp() + ":" + startif + "_" + lr.getEndIp() + ":" + endif;
						String target = "";
						String alarmMessage = "";

						if (lr.getMaxSpeed() != null && !"".equals(lr.getMaxSpeed()) && !"null".equals(lr.getMaxSpeed())) {
							if (upspeed > Integer.parseInt(lr.getMaxSpeed())) {
								// 有告警产生
								lr.setAlarm(true);
								lr.setLevels(1);
								flag = 1;
								alarmMessage = "";
								alarmMessage = content + "，上行流速超过阀值，当前" + upspeed + "KB/s," + "阀值" + lr.getMaxSpeed() + "KB/s";
								target = "speed";
								CheckEventUtil.saveLinkEventList(lr, alarmMessage, target);
							}
							if (downspeed > Integer.parseInt(lr.getMaxSpeed())) {
								// 有告警产生
								lr.setAlarm(true);
								lr.setLevels(1);
								flag = 1;
								alarmMessage = "";
								alarmMessage = content + "，下行流速超过阀值，当前" + downspeed + "KB/s," + "阀值" + lr.getMaxSpeed() + "KB/s";
								target = "speed";
								CheckEventUtil.saveLinkEventList(lr, alarmMessage, target);
							}
						} else {// 链路没有设定阀值的情况下，由端口阀值获得

						}

						if (lr.getMaxPer() != null && !"".equals(lr.getMaxPer()) && !"null".equals(lr.getMaxPer())) {
							if (upperc >= 100) {
								upperc = upperc / 10;
							}
							if (upperc >= 100) {
								upperc = upperc / 10;
							}
							if (upperc > Double.parseDouble(lr.getMaxPer())) {
								// 有告警产生
								lr.setAlarm(true);
								lr.setLevels(1);
								flag = 1;
								alarmMessage = "";
								alarmMessage = content + "，上行带宽利用率超过阀值，当前" + upperc + "%,阀值" + lr.getMaxPer() + "%";
								target = "linkperc";
								CheckEventUtil.saveLinkEventList(lr, alarmMessage, target);
							}
							if (downperc >= 100) {
								downperc = downperc / 10;
							}
							if (downperc >= 100) {
								downperc = downperc / 10;
							}
							if (downperc > Double.parseDouble(lr.getMaxPer())) {
								// 有告警产生
								lr.setAlarm(true);
								lr.setLevels(1);
								flag = 1;
								alarmMessage = "";
								alarmMessage = content + "，下行带宽利用率超过阀值，当前" + downperc + "%,阀值" + lr.getMaxPer() + "%";
								target = "linkperc";
								CheckEventUtil.saveLinkEventList(lr, alarmMessage, target);
							}
						} else {// 链路没有设定阀值的情况下，由端口阀值获得

						}

						Interfacecollectdata utilhdxperc = new Interfacecollectdata();
						utilhdxperc.setIpaddress("");
						utilhdxperc.setCollecttime(date);
						utilhdxperc.setCategory("");
						utilhdxperc.setEntity("");
						utilhdxperc.setSubentity("");
						utilhdxperc.setRestype("dynamic");
						utilhdxperc.setUnit("%");
						utilhdxperc.setChname("");
						utilhdxperc.setThevalue(df.format(upperc + downperc));
						Vector utilhdxpercv = new Vector();
						utilhdxpercv.add(utilhdxperc);

						Hashtable linkdataHash = new Hashtable();
						linkdataHash.put("util", v);
						linkdataHash.put("linkstatus", linkstatusv);
						linkdataHash.put("linkutilperc", utilhdxpercv);
						linkdataHash.put("linkid", lr.getId());
						ShareData.getAllLinkData().put(lr.getId(), linkdataHash);
					}
					if (flag == 0) {
						lr.setAlarm(false);
						lr.setLevels(0);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	/**
	 * 
	 * 判断是否为数字
	 */
	public boolean isNumberic(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (Character.isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

}
