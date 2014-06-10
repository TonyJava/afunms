package com.afunms.initialize;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SyslogDefs;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.event.model.NetSyslog;
import com.afunms.event.model.Syslog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.NetSyslogNodeAlarmKeyDao;
import com.afunms.topology.model.NetSyslogNodeAlarmKey;

@SuppressWarnings("rawtypes")
public class ProcessSyslog {
	static public int sport = 514;// 服务器端端口514；
	List<String> facilityList = new ArrayList<String>();
	int processId;// 进程id
	String processName;// 进程名
	String processIdStr = "";// 进程id字符
	int facility;// 事件来源编码
	int priority;// 优先级编码
	String facilityName;// 事件来源名称
	String priorityName;// 优先级名称
	String hostName;// 主机名称
	String userName;// 登陆用户
	Calendar timeStamp;// 时间戳
	String message;// 得消息内容
	String ipAddress;// IP地址
	String businessId;// 业务ID
	boolean sign = true;
	int eventid;// 事件ID

	private void createSyslogAlarm(String message) {
		Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipAddress);
		if (null == host) {
			return;
		} else {
			NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
			boolean isContainsKeyword = false;// 是否包含关键字
			int alarmLevel = 0;
			int tempLevel = 0;
			List netSyslogNodeAlarmList = new ArrayList();
			try {
				netSyslogNodeAlarmList = netSyslogNodeAlarmKeyDao.findByCondition(" where nodeid = '" + host.getId() + "'");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				netSyslogNodeAlarmKeyDao.close();
			}
			if (null != netSyslogNodeAlarmList && netSyslogNodeAlarmList.size() > 0) {
				NetSyslogNodeAlarmKey netSyslogNodeAlarmKey = null;
				for (int i = 0; i < netSyslogNodeAlarmList.size(); i++) {
					netSyslogNodeAlarmKey = (NetSyslogNodeAlarmKey) netSyslogNodeAlarmList.get(i);
					if (netSyslogNodeAlarmKey != null) {
						String keywords = netSyslogNodeAlarmKey.getKeywords();
						if (keywords != null && !keywords.equals("") && message.contains(keywords)) {
							isContainsKeyword = true;
							tempLevel = Integer.parseInt(netSyslogNodeAlarmKey.getLevel());
							if (alarmLevel < tempLevel) {// 取告警级别中较大的一个
								alarmLevel = tempLevel;
							}
						}
					}
				}
			}
			// 如果包含关键字 则产生syslog告警
			if (isContainsKeyword) {
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
				CheckEvent checkEvent = new CheckEvent();
				checkEvent.setNodeid(String.valueOf(host.getId()));
				checkEvent.setIndicatorsName("syslog");
				checkEvent.setType(nodedto.getType());
				checkEvent.setSubtype(nodedto.getSubtype());
				checkEvent.setSindex("");
				checkEvent.setThevalue("");
				checkEvent.setContent(message);
				checkEvent.setCollecttime(CommonUtil.getDateAndTime());
				checkEvent.setAlarmlevel(alarmLevel);
				CheckEventDao checkeventdao = new CheckEventDao();
				try {
					checkeventdao.save(checkEvent);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null != checkeventdao)
						checkeventdao.close();
				}
				host.setStatus(alarmLevel);
				host.setAlarmlevel(alarmLevel);
				EventList eventList = new EventList();
				eventList.setLevel1(alarmLevel);
				eventList.setEventtype("syslog");
				eventList.setEventlocation(host.getAlias() + "(" + host.getIpAddress() + ")");
				eventList.setLevel1(alarmLevel);
				eventList.setManagesign(0);
				eventList.setBak("");
				eventList.setRecordtime(Calendar.getInstance());
				eventList.setReportman("系统轮询");
				eventList.setBusinessid(host.getBid());
				eventList.setNodeid(host.getId());
				eventList.setSubtype(nodedto.getSubtype());
				eventList.setSubentity("syslog");
				eventList.setContent(message);
				EventListDao eventListDao = new EventListDao();
				try {
					eventListDao.save(eventList);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (null != eventListDao)
						eventListDao.close();
				}

				AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
				List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(host.getId() + "", "net", null, "syslog");
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						AlarmIndicatorsNode nm = (AlarmIndicatorsNode) list.get(i);
						SendAlarmUtil alarmUtil = new SendAlarmUtil();
						try {
							alarmUtil.sendAlarm(checkEvent, eventList, nm);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void createTask(final DatagramPacket packet) {
		InetAddress address = packet.getAddress(); // 获取客户端的IP地址
		ipAddress = address.getHostAddress();

		Host vo = (Host) PollingEngine.getInstance().getNodeByIP(ipAddress);
		if (vo == null) {
			if (ShareData.getAllipaliasVSip() != null) {
				if (ShareData.getAllipaliasVSip().containsKey(ipAddress)) {
					String _ipaddress = (String) ShareData.getAllipaliasVSip().get(ipAddress);
					ipAddress = _ipaddress;
					try {
						vo = (Host) PollingEngine.getInstance().getNodeByIP(ipAddress);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			hostName = vo.getAlias();
			businessId = vo.getBid();
			facilityList.clear();
			String sysLogRuleString = (String) null;
			if (null != ShareData.getSyslogruleNode() && ShareData.getSyslogruleNode().containsKey(String.valueOf(vo.getId()))) {
				sysLogRuleString = (String) ShareData.getSyslogruleNode().get(String.valueOf(vo.getId()));
			}
			if (null == sysLogRuleString) {
				sign = false;
			} else {
				if (sysLogRuleString != null && sysLogRuleString.trim().length() > 0) {
					String[] nodeFacilityArray = sysLogRuleString.split(",");
					if (nodeFacilityArray != null && nodeFacilityArray.length > 0) {
						for (int i = 0; i < nodeFacilityArray.length; i++) {
							facilityList.add(nodeFacilityArray[i]);
						}
					}
				}
			}
			String sfc = (String) null;
			try {
				sfc = new String(packet.getData(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (vo.getCategory() == 1 || vo.getCategory() == 2 || vo.getCategory() == 3 || vo.getCategory() == 7 || vo.getCategory() == 8) {
				// 网络设备的SYSLOG
				this.processNetMessage(sfc.trim(), vo.getCategory());
			} else {
				this.processMessage(sfc.trim());
			}
		}
	}

	public synchronized void processMessage(String message) {
		int lbIdx = message.indexOf('<');
		int rbIdx = message.indexOf('>');
		// 如果无优先级，则消息格式不合法
		if (lbIdx < 0 || rbIdx < 0 || lbIdx >= (rbIdx - 1)) {
			return;
		}
		// 是否优先级是合法数字
		int priCode = 0;
		String priStr = message.substring(lbIdx + 1, rbIdx);
		try {
			priCode = Integer.parseInt(priStr);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return;
		}
		// 得事件来源和事件等级
		int facility = SyslogDefs.extractFacility(priCode);
		int priority = SyslogDefs.extractPriority(priCode);
		// 得消息内容
		if (sign && facilityList.contains(String.valueOf(priority))) {
			message = message.substring(rbIdx + 1, (message.length()));
			message = message.replaceAll("\"", "'");
			String[] allMessages = new String[message.split(":").length];
			allMessages = message.split(":");
			try {
				if (allMessages[0].trim().equals("Security") || allMessages[0].trim().equals("W3SVC")) {
					processName = allMessages[0];
					processId = 0;
					eventid = Integer.parseInt(allMessages[1].trim());
					message = message.substring(allMessages[0].length() + allMessages[1].length() + 2);
					if (allMessages[0].trim().equals("Security")) {
						// 获得登陆用户信息
						userName = allMessages[2].trim();
						if (message.indexOf("目标用户名") >= 0 && message.indexOf("目标域") >= 0 && message.indexOf("目标登录") >= 0) {
							// 判断登陆用户
							String bname = message.substring(message.indexOf("目标用户名") + 6, message.indexOf("目标域")).trim();
							String dname = message.substring(message.indexOf("目标域") + 4, message.indexOf("目标登录")).trim();
							userName = dname + "\\" + bname;
						} else {
							if (message.indexOf("用户名: 域: 登录 ID:") < 0) {
								if (message.indexOf("用户名:") >= 0 && message.indexOf("域:") >= 0 && message.indexOf("登录 ID:") >= 0) {
									String bname = message.substring(message.indexOf("用户名:") + 4, message.indexOf("域:")).trim();
									String dname = message.substring(message.indexOf("域:") + 2, message.indexOf("登录 ID:")).trim();
									userName = dname + "\\" + bname;
								}
							}
						}
					}
				} else {
					if (message.split(":").length == 3) {
						// windows的SYSLOG
						processName = allMessages[0];
						processId = Integer.parseInt(allMessages[1].trim());
						eventid = Integer.parseInt(allMessages[1].trim());
						message = allMessages[2];
					} else if (message.split(":").length == 5) {
						// AIX系统
						processName = allMessages[3];
						message = allMessages[4];
					} else if (message.split(":").length == 4) {
						processName = allMessages[0];
						eventid = Integer.parseInt(allMessages[1].trim());
						processId = Integer.parseInt(allMessages[1].trim());
						message = allMessages[3];
					} else if (message.split(":").length == 6) {
						// AIX系统的SYSLOG
						String proc = allMessages[3];
						lbIdx = proc.indexOf('[');
						rbIdx = proc.indexOf(']');
						// 若为FTP,则不处理相关信息
						if (allMessages[4].trim().equals("ftp")) {
							return;
						}
						if (lbIdx > -1) {
							processName = proc.substring(0, lbIdx);
							processId = Integer.parseInt(proc.substring(lbIdx + 1, rbIdx));
							eventid = Integer.parseInt(proc.substring(lbIdx + 1, rbIdx));
						} else {
							processName = proc;
							processId = 0;
							eventid = 0;
						}
						message = allMessages[5];
					} else {
						// 针对多个":"的情况
						processName = allMessages[0];// 来源,即进程名称
						processId = Integer.parseInt(allMessages[1].trim());// 进程ID
						eventid = Integer.parseInt(allMessages[1].trim());// 事件ID
						if (allMessages.length >= 2) {
							for (int k = 2; k < allMessages.length; k++) {
								message = message + allMessages[k];
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			timeStamp = Calendar.getInstance();
			Syslog syslog = new Syslog();
			syslog.setFacility(facility);
			syslog.setPriority(priority);
			syslog.setFacilityName(SyslogDefs.getFacilityName(facility));
			syslog.setPriorityName(SyslogDefs.getPriorityName(priority));
			syslog.setRecordtime(timeStamp);
			syslog.setProcessid(processId);
			syslog.setProcessidstr(processIdStr);
			syslog.setProcessname(processName);
			syslog.setHostname(hostName);
			if (userName != null && userName.trim().length() > 0) {
				syslog.setUsername(userName);
			} else {
				syslog.setUsername(hostName);
			}
			syslog.setMessage(message);
			syslog.setIpaddress(ipAddress);
			syslog.setEventid(eventid);

			// 执行保存
			SyslogDao syslogDao = new SyslogDao();
			try {
				syslogDao.createSyslogData(syslog);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				syslogDao.close();
			}

			NetSyslog netSyslog = new NetSyslog();
			netSyslog.setFacility(facility);
			netSyslog.setPriority(priority);
			netSyslog.setFacilityName(SyslogDefs.getFacilityName(facility));
			netSyslog.setPriorityName(SyslogDefs.getPriorityName(priority));
			netSyslog.setRecordtime(timeStamp);
			netSyslog.setHostname(hostName);
			netSyslog.setMessage(message);
			netSyslog.setIpaddress(ipAddress);
			netSyslog.setBusinessid(businessId);
			netSyslog.setCategory(4);
			NetSyslogDao netSyslogDao = new NetSyslogDao();
			try {
				netSyslogDao.save(netSyslog);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				netSyslogDao.close();
			}
			createSyslogAlarm(message);
		}
	}

	public synchronized void processNetMessage(String message, int category) {
		int lbIdx = message.indexOf('<');
		int rbIdx = message.indexOf('>');
		// 如果无优先级，则消息格式不合法
		if (lbIdx < 0 || rbIdx < 0 || lbIdx >= (rbIdx - 1)) {
			return;
		}
		// 是否优先级是合法数字
		int priCode = 0;
		String priStr = message.substring(lbIdx + 1, rbIdx);
		try {
			priCode = Integer.parseInt(priStr);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return;
		}
		// 得事件来源和事件等级
		int facility = SyslogDefs.extractFacility(priCode);
		int priority = SyslogDefs.extractPriority(priCode);
		if (sign && facilityList.contains(priority + "")) {
			// 得消息内容
			timeStamp = Calendar.getInstance();
			message = message.substring(rbIdx + 1, (message.length()));
			NetSyslog syslog = new NetSyslog();
			syslog.setFacility(facility);
			syslog.setPriority(priority);
			syslog.setFacilityName(SyslogDefs.getFacilityName(facility));
			syslog.setPriorityName(SyslogDefs.getPriorityName(priority));
			syslog.setRecordtime(timeStamp);
			syslog.setHostname(hostName);
			syslog.setMessage(message);
			syslog.setIpaddress(ipAddress);
			syslog.setBusinessid(businessId);
			syslog.setCategory(category);
			NetSyslogDao sdao = new NetSyslogDao();
			try {
				sdao.save(syslog);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sdao.close();
			}
			// 产生syslog告警
			createSyslogAlarm(message);
		}
		timeStamp = Calendar.getInstance();
	}
}
