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
		logger.info(" ��ʼ�����· ");
		try {
			List linkList = PollingEngine.getInstance().getLinkList();
			ThreadPool threadPool = null;
			if (linkList != null && linkList.size() > 0) {
				threadPool = new ThreadPool(linkList.size());
				if (ShareData.getRunflag() == 0) {
					ShareData.setRunflag(1);
				} else {
					// �ȼ��вɼ�������·�ڵ�����ݼ��ϴ浽�ڴ��� �߳��в�����������·�ڵ�����ݲɼ�
					getLinknodeInterfaceData(linkList);
					for (int i = 0; i < linkList.size(); i++) {
						threadPool.runTask(createTask((LinkRoad) linkList.get(i)));
					}
				}
				threadPool.join();
				threadPool.close();
			}
			threadPool = null;

			// ��������link����
			processLinkData();
		} catch (Exception e) {
		}
	}

	/**
	 * �ȼ��вɼ�������·�ڵ�����ݼ��ϴ浽�ڴ���
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
		// �ų��ظ�Ԫ��
		HashSet h = new HashSet(ipList);
		ipList.clear();
		ipList.addAll(h);

		Hashtable interfaceHash = hostlastmanager.getInterfaces(ipList, netInterfaceItem, "index", "", "");
		// �����ڴ�
		ShareData.setAllLinknodeInterfaceData(interfaceHash);
		return interfaceHash;
	}

	/**
	 * ��������link����
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
	 * ��������
	 */
	private Runnable createTask(final LinkRoad linkroad) {
		return new Runnable() {
			public void run() {
				// ��ʼ��������
				try {
					// ��Ҫ���ֲ�ʽ�ж�
					String runmodel = PollingEngine.getCollectwebflag();
					// ��·�ڵ�����ݼ���
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
							// �ɼ�������Ǽ���ģʽ
							vector1 = hostlastmanager.getInterface_share(host1.getIpAddress(), netInterfaceItem, "index", "", "");
							vector2 = hostlastmanager.getInterface_share(host2.getIpAddress(), netInterfaceItem, "index", "", "");
						} else {
							// �ɼ�������Ƿ���ģʽ
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
									start_oututilhdx = strs[8].replaceAll("KB/��", "").replaceAll("Kb/��", "").replaceAll("kb/s", "").replaceAll("kb/��", "").replaceAll("KB/S", "");// yangjun
									start_inutilhdx = strs[9].replaceAll("KB/��", "").replaceAll("Kb/��", "").replaceAll("kb/s", "").replaceAll("kb/��", "").replaceAll("KB/S", "");// yangjun
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
									end_oututilhdx = strs[8].replaceAll("KB/��", "").replaceAll("Kb/��", "").replaceAll("kb/s", "").replaceAll("kb/��", "").replaceAll("KB/S", "");// yangjun
									end_inutilhdx = strs[9].replaceAll("KB/��", "").replaceAll("Kb/��", "").replaceAll("kb/s", "").replaceAll("kb/��", "").replaceAll("KB/S", "");// yangjun
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
						int downspeed = (Integer.parseInt(start_oututilhdx) + Integer.parseInt(end_inutilhdx.replaceAll("KB/��", "").replaceAll("Kb/��", "").replaceAll("kb/s", "")
								.replaceAll("kb/��", "").replaceAll("KB/S", ""))) / 2;
						int upspeed = (Integer.parseInt(start_inutilhdx) + Integer.parseInt(end_oututilhdx.replaceAll("KB/��", "").replaceAll("Kb/��", "").replaceAll("kb/s", "")
								.replaceAll("kb/��", "").replaceAll("KB/S", ""))) / 2;
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

						// ����·״̬�������ݿ�
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
						uputilhdx.setUnit("Kb/��");
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
						downutilhdx.setUnit("Kb/��");
						downutilhdx.setChname("");
						downutilhdx.setThevalue(df.format(downspeed));
						v.add(downutilhdx);

						String startif = lr.getStartDescr();// �����·���ٳ�����ֵ�澯������ռ���ʳ�����ֵ�澯������澯���澯�б���
						String endif = lr.getEndDescr();
						String content = "��·" + lr.getStartIp() + ":" + startif + "_" + lr.getEndIp() + ":" + endif;
						String target = "";
						String alarmMessage = "";

						if (lr.getMaxSpeed() != null && !"".equals(lr.getMaxSpeed()) && !"null".equals(lr.getMaxSpeed())) {
							if (upspeed > Integer.parseInt(lr.getMaxSpeed())) {
								// �и澯����
								lr.setAlarm(true);
								lr.setLevels(1);
								flag = 1;
								alarmMessage = "";
								alarmMessage = content + "���������ٳ�����ֵ����ǰ" + upspeed + "KB/s," + "��ֵ" + lr.getMaxSpeed() + "KB/s";
								target = "speed";
								CheckEventUtil.saveLinkEventList(lr, alarmMessage, target);
							}
							if (downspeed > Integer.parseInt(lr.getMaxSpeed())) {
								// �и澯����
								lr.setAlarm(true);
								lr.setLevels(1);
								flag = 1;
								alarmMessage = "";
								alarmMessage = content + "���������ٳ�����ֵ����ǰ" + downspeed + "KB/s," + "��ֵ" + lr.getMaxSpeed() + "KB/s";
								target = "speed";
								CheckEventUtil.saveLinkEventList(lr, alarmMessage, target);
							}
						} else {// ��·û���趨��ֵ������£��ɶ˿ڷ�ֵ���

						}

						if (lr.getMaxPer() != null && !"".equals(lr.getMaxPer()) && !"null".equals(lr.getMaxPer())) {
							if (upperc >= 100) {
								upperc = upperc / 10;
							}
							if (upperc >= 100) {
								upperc = upperc / 10;
							}
							if (upperc > Double.parseDouble(lr.getMaxPer())) {
								// �и澯����
								lr.setAlarm(true);
								lr.setLevels(1);
								flag = 1;
								alarmMessage = "";
								alarmMessage = content + "�����д��������ʳ�����ֵ����ǰ" + upperc + "%,��ֵ" + lr.getMaxPer() + "%";
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
								// �и澯����
								lr.setAlarm(true);
								lr.setLevels(1);
								flag = 1;
								alarmMessage = "";
								alarmMessage = content + "�����д��������ʳ�����ֵ����ǰ" + downperc + "%,��ֵ" + lr.getMaxPer() + "%";
								target = "linkperc";
								CheckEventUtil.saveLinkEventList(lr, alarmMessage, target);
							}
						} else {// ��·û���趨��ֵ������£��ɶ˿ڷ�ֵ���

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
	 * �ж��Ƿ�Ϊ����
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
