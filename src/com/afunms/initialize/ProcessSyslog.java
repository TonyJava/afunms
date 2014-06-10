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
	static public int sport = 514;// �������˶˿�514��
	List<String> facilityList = new ArrayList<String>();
	int processId;// ����id
	String processName;// ������
	String processIdStr = "";// ����id�ַ�
	int facility;// �¼���Դ����
	int priority;// ���ȼ�����
	String facilityName;// �¼���Դ����
	String priorityName;// ���ȼ�����
	String hostName;// ��������
	String userName;// ��½�û�
	Calendar timeStamp;// ʱ���
	String message;// ����Ϣ����
	String ipAddress;// IP��ַ
	String businessId;// ҵ��ID
	boolean sign = true;
	int eventid;// �¼�ID

	private void createSyslogAlarm(String message) {
		Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipAddress);
		if (null == host) {
			return;
		} else {
			NetSyslogNodeAlarmKeyDao netSyslogNodeAlarmKeyDao = new NetSyslogNodeAlarmKeyDao();
			boolean isContainsKeyword = false;// �Ƿ�����ؼ���
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
							if (alarmLevel < tempLevel) {// ȡ�澯�����нϴ��һ��
								alarmLevel = tempLevel;
							}
						}
					}
				}
			}
			// ��������ؼ��� �����syslog�澯
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
				eventList.setReportman("ϵͳ��ѯ");
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
		InetAddress address = packet.getAddress(); // ��ȡ�ͻ��˵�IP��ַ
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
				// �����豸��SYSLOG
				this.processNetMessage(sfc.trim(), vo.getCategory());
			} else {
				this.processMessage(sfc.trim());
			}
		}
	}

	public synchronized void processMessage(String message) {
		int lbIdx = message.indexOf('<');
		int rbIdx = message.indexOf('>');
		// ��������ȼ�������Ϣ��ʽ���Ϸ�
		if (lbIdx < 0 || rbIdx < 0 || lbIdx >= (rbIdx - 1)) {
			return;
		}
		// �Ƿ����ȼ��ǺϷ�����
		int priCode = 0;
		String priStr = message.substring(lbIdx + 1, rbIdx);
		try {
			priCode = Integer.parseInt(priStr);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return;
		}
		// ���¼���Դ���¼��ȼ�
		int facility = SyslogDefs.extractFacility(priCode);
		int priority = SyslogDefs.extractPriority(priCode);
		// ����Ϣ����
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
						// ��õ�½�û���Ϣ
						userName = allMessages[2].trim();
						if (message.indexOf("Ŀ���û���") >= 0 && message.indexOf("Ŀ����") >= 0 && message.indexOf("Ŀ���¼") >= 0) {
							// �жϵ�½�û�
							String bname = message.substring(message.indexOf("Ŀ���û���") + 6, message.indexOf("Ŀ����")).trim();
							String dname = message.substring(message.indexOf("Ŀ����") + 4, message.indexOf("Ŀ���¼")).trim();
							userName = dname + "\\" + bname;
						} else {
							if (message.indexOf("�û���: ��: ��¼ ID:") < 0) {
								if (message.indexOf("�û���:") >= 0 && message.indexOf("��:") >= 0 && message.indexOf("��¼ ID:") >= 0) {
									String bname = message.substring(message.indexOf("�û���:") + 4, message.indexOf("��:")).trim();
									String dname = message.substring(message.indexOf("��:") + 2, message.indexOf("��¼ ID:")).trim();
									userName = dname + "\\" + bname;
								}
							}
						}
					}
				} else {
					if (message.split(":").length == 3) {
						// windows��SYSLOG
						processName = allMessages[0];
						processId = Integer.parseInt(allMessages[1].trim());
						eventid = Integer.parseInt(allMessages[1].trim());
						message = allMessages[2];
					} else if (message.split(":").length == 5) {
						// AIXϵͳ
						processName = allMessages[3];
						message = allMessages[4];
					} else if (message.split(":").length == 4) {
						processName = allMessages[0];
						eventid = Integer.parseInt(allMessages[1].trim());
						processId = Integer.parseInt(allMessages[1].trim());
						message = allMessages[3];
					} else if (message.split(":").length == 6) {
						// AIXϵͳ��SYSLOG
						String proc = allMessages[3];
						lbIdx = proc.indexOf('[');
						rbIdx = proc.indexOf(']');
						// ��ΪFTP,�򲻴��������Ϣ
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
						// ��Զ��":"�����
						processName = allMessages[0];// ��Դ,����������
						processId = Integer.parseInt(allMessages[1].trim());// ����ID
						eventid = Integer.parseInt(allMessages[1].trim());// �¼�ID
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

			// ִ�б���
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
		// ��������ȼ�������Ϣ��ʽ���Ϸ�
		if (lbIdx < 0 || rbIdx < 0 || lbIdx >= (rbIdx - 1)) {
			return;
		}
		// �Ƿ����ȼ��ǺϷ�����
		int priCode = 0;
		String priStr = message.substring(lbIdx + 1, rbIdx);
		try {
			priCode = Integer.parseInt(priStr);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return;
		}
		// ���¼���Դ���¼��ȼ�
		int facility = SyslogDefs.extractFacility(priCode);
		int priority = SyslogDefs.extractPriority(priCode);
		if (sign && facilityList.contains(priority + "")) {
			// ����Ϣ����
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
			// ����syslog�澯
			createSyslogAlarm(message);
		}
		timeStamp = Calendar.getInstance();
	}
}
