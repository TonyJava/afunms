package com.afunms.common.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.dao.SendAlarmTimeDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.send.SendAlarmUtil;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.alarm.util.AlarmResourceCenter;
import com.afunms.application.dao.TracertsDao;
import com.afunms.application.dao.TracertsDetailDao;
import com.afunms.application.model.HostServiceGroup;
import com.afunms.application.model.HostServiceGroupConfiguration;
import com.afunms.application.model.JobForAS400Group;
import com.afunms.application.model.JobForAS400GroupDetail;
import com.afunms.application.model.JobForAS400SubSystem;
import com.afunms.application.model.ProcessGroup;
import com.afunms.application.model.ProcessGroupConfiguration;
import com.afunms.application.model.Tracerts;
import com.afunms.application.model.TracertsDetail;
import com.afunms.application.util.HostServiceGroupConfigurationUtil;
import com.afunms.application.util.JobForAS400GroupDetailUtil;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.BaseVo;
import com.afunms.config.dao.AclBaseDao;
import com.afunms.config.model.AclDetail;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.PolicyInterface;
import com.afunms.config.model.QueueInfo;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DHCP;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Mail;
import com.afunms.polling.node.TFtp;
import com.afunms.polling.node.Web;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.ProcessCollectEntity;
import com.afunms.polling.om.ServiceCollectEntity;
import com.afunms.toolService.traceroute.TraceRouteExecute;
import com.afunms.topology.model.JobForAS400;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.util.DataGate;

@SuppressWarnings("unchecked")
public class CheckEventUtil {

	/**
	 * ���ݸ澯ָ������ ��ȡ�澯����
	 * 
	 * @param target
	 *            �澯ָ��
	 * @param oldvalue
	 *            ��ֵ
	 * @param value
	 *            ��ֵ
	 * @return
	 */
	public synchronized static String getAlarmInfo(String target, Object oldvalue, Object valueObj) {
		String content = null;
		String value = null;
		if ("cpu".equalsIgnoreCase(target.trim())) {
			value = (String) valueObj;
			content = "CPU�����иı䣬֮ǰ��" + oldvalue + "����������" + value + "��";
		} else if ("diskSize".equalsIgnoreCase(target.trim())) {
			value = (String) valueObj;
			content = "���������иı䣬֮ǰ��" + oldvalue + "��������" + value;
		} else if ("diskArray".equalsIgnoreCase(target.trim())) {
			List<String> oldDiskArray = (ArrayList<String>) oldvalue;
			StringBuffer tempBuffer = new StringBuffer();
			tempBuffer.append("�����̷��иı䣬֮ǰ��");
			for (int i = 0; i < oldDiskArray.size(); i++) {
				tempBuffer.append(oldDiskArray.get(i));
				if (i != oldDiskArray.size() - 1) {
					tempBuffer.append(",");
				}
			}
			tempBuffer.append("�����ڵ��̷�Ϊ");
			List<String> diskArray = (ArrayList<String>) valueObj;
			for (int i = 0; i < diskArray.size(); i++) {
				tempBuffer.append(diskArray.get(i));
				if (i != diskArray.size() - 1) {
					tempBuffer.append(",");
				}
			}
			content = tempBuffer.toString();
		} else if ("PhysicalMemory".equalsIgnoreCase(target.trim())) {
			value = (String) valueObj;
			content = "�����ڴ��иı䣬֮ǰ��" + oldvalue + "��������" + value;
		}
		return content;
	}

	/**
	 * �澯ָ��
	 * 
	 * @param node
	 *            �澯�ڵ�
	 * @param content
	 *            �澯����
	 * @param target
	 *            �澯ָ������
	 */
	public synchronized static void saveEventList(Host node, String content, String target) {
		Calendar Cal = Calendar.getInstance();
		EventListDao dao = null;
		try {
			dao = new EventListDao();
			EventList vo = new EventList();
			vo.setEventtype("poll");
			vo.setEventlocation(node.getLocation());
			vo.setContent(content);
			vo.setLevel1(1);
			vo.setManagesign(1);
			vo.setBusinessid(node.getBid());
			vo.setManagesign(0);
			vo.setReportman("ϵͳ��ѯ");
			vo.setNodeid(node.getId());
			vo.setOid(0);
			vo.setRecordtime(Cal);
			vo.setSubtype("host");
			vo.setSubentity(target);
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
	}

	/**
	 * �澯ָ��
	 * 
	 * @param lr
	 *            �澯��·
	 * @param content
	 *            �澯����
	 * @param target
	 *            �澯ָ������
	 */
	public synchronized static void saveLinkEventList(LinkRoad lr, String content, String target) {
		Calendar Cal = Calendar.getInstance();
		EventListDao dao = null;
		try {
			dao = new EventListDao();
			EventList vo = new EventList();
			vo.setEventtype("poll");
			vo.setEventlocation(lr.getStartIp() + "-" + lr.getEndIp());
			vo.setContent(content);
			vo.setLevel1(3);
			vo.setManagesign(1);
			vo.setBusinessid("");
			vo.setManagesign(0);
			vo.setReportman("ϵͳ��ѯ");
			vo.setNodeid(lr.getId());
			vo.setOid(0);
			vo.setRecordtime(Cal);
			vo.setSubtype("link");
			vo.setSubentity(target);
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
	}

	public CheckEventUtil() {
	}

	/**
	 * ȷ���Ƿ�Ϊ�澯
	 * <p>
	 * �������¼��Ĵ������ڹ涨�Ĵ��� ����Ϊ�澯�������ظ澯�ĵȼ� ������澯�򷵻�
	 * <p>
	 * 
	 * @param node
	 *            �豸
	 * @param nm
	 *            ָ��
	 * @param value
	 *            ֵ
	 * @param name
	 *            �澯Ψһ��־
	 * @return
	 */
	private int checkAlarm(NodeDTO node, AlarmIndicatorsNode nm, double value, String name) {
		int alarmLevel = 0;
		int eventLevel = 0; // �¼��ȼ�
		int eventTimes = 0; // �¼�����
		double limenvalue0 = Double.parseDouble(nm.getLimenvalue0());// һ����ֵ
		double limenvalue1 = Double.parseDouble(nm.getLimenvalue1());// ������ֵ
		double limenvalue2 = Double.parseDouble(nm.getLimenvalue2());// ������ֵ
		// ����¼��ȼ�
		eventLevel = checkEventLevel(value, limenvalue0, limenvalue1, limenvalue2, nm.getCompare());
		// ����¼�����
		// ���������¼��ȼ�Ϊ 0 ��˵���¼��ָ� �������� �¼�����
		// ������ش������� 0 ��˵�����ڹ涨���¼����� ���¼�����Ϊ�澯 ������ش��������� 0 ��ֻ�ǽ��¼����� + 1;;
		eventTimes = checkEventTimes(nm, eventLevel, name);
		if (eventTimes > 0) {
			// ������� 0 ���¼��ȼ����� Ϊ �澯�ȼ�
			alarmLevel = eventLevel;
		}
		return alarmLevel;

	}

	/**
	 * 
	 * �ַ��������ж� ȷ���Ƿ�Ϊ�澯
	 * <p>
	 * �������¼��Ĵ������ڹ涨�Ĵ��� ����Ϊ�澯�������ظ澯�ĵȼ� ������澯�򷵻�
	 * <p>
	 * 
	 * @param node
	 *            �豸
	 * @param nm
	 *            ָ��
	 * @param value
	 *            ֵ
	 * @param name
	 *            �澯Ψһ��־
	 * @return
	 */
	private int checkAlarm(NodeDTO node, AlarmIndicatorsNode nm, String value, String name) {
		int alarmLevel = 0;
		int eventLevel = 0; // �¼��ȼ�
		int eventTimes = 0; // �¼�����
		String limenvalue0 = nm.getLimenvalue0();// һ����ֵ
		String limenvalue1 = nm.getLimenvalue1();// ������ֵ
		String limenvalue2 = nm.getLimenvalue2();// ������ֵ
		// ����¼��ȼ�
		eventLevel = checkEventLevel(value, limenvalue0, limenvalue1, limenvalue2, nm.getSms0(), nm.getSms1(), nm.getSms2(), nm.getCompare());
		// ����¼�����
		// ���������¼��ȼ�Ϊ 0 ��˵���¼��ָ� �������� �¼�����
		// ������ش������� 0 ��˵�����ڹ涨���¼����� ���¼�����Ϊ�澯 ������ش��������� 0 ��ֻ�ǽ��¼����� + 1;;
		eventTimes = checkEventTimes(nm, eventLevel, name);
		if (eventTimes > 0) {
			// ������� 0 ���¼��ȼ����� Ϊ �澯�ȼ�
			alarmLevel = eventLevel;
		}
		return alarmLevel;

	}

	public void checkData(Object vo, Object collectingData, String type, String subtype, AlarmIndicatorsNode alarmIndicatorsNode) {
		try {
			Node node = (Node) vo;
			// ���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
			deleteEvent(node.getId() + "", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(), null);
			Hashtable datahashtable = (Hashtable) collectingData;
			// ��ȡJOB,�ж�JOB����
			if ("1".equals(alarmIndicatorsNode.getEnabled())) {
				String indicators = alarmIndicatorsNode.getName();
				String value = "0";
				if ("droprate".equalsIgnoreCase(indicators)) {
					// ������
					List<PolicyInterface> interfaceList = new ArrayList<PolicyInterface>();
					if (datahashtable.get("policy") != null) {
						interfaceList = (List<PolicyInterface>) datahashtable.get("policy");
					}
					if (interfaceList != null && interfaceList.size() > 0) {
						for (int i = 0; i < interfaceList.size(); i++) {
							PolicyInterface interData = (PolicyInterface) interfaceList.get(i);
							value = interData.getDropRate() + "";
							if (value == null) {
								continue;
							}
							setAlarmEvent(vo, alarmIndicatorsNode, value, interData, indicators);
						}
					} else {
						return;
					}
				} else if ("dropbytes".equalsIgnoreCase(indicators)) {
					// ������
					List<QueueInfo> queueList = new ArrayList<QueueInfo>();
					if (datahashtable.get("queue") != null) {
						queueList = (List<QueueInfo>) datahashtable.get("queue");
					}
					if (queueList != null && queueList.size() > 0) {
						for (int i = 0; i < queueList.size(); i++) {
							QueueInfo queueInfo = (QueueInfo) queueList.get(i);
							value = queueInfo.getInputDrops() + "";
							if (value == null) {
								continue;
							}
							setAlarmEvent(vo, alarmIndicatorsNode, "input queue", queueInfo, indicators);
							value = queueInfo.getOutputDrops() + "";
							if (value == null) {
								continue;
							}
							setAlarmEvent(vo, alarmIndicatorsNode, "output queue", queueInfo, indicators);
						}
					}
				} else if ("matches".equalsIgnoreCase(indicators)) {
					List<AclDetail> detailList = new ArrayList<AclDetail>();
					if (datahashtable.get("detail") != null) {
						detailList = (List<AclDetail>) datahashtable.get("detail");
					}
					if (detailList != null && detailList.size() > 0) {
						AclBaseDao dao = null;
						HashMap<Integer, String> map = new HashMap<Integer, String>();
						try {
							dao = new AclBaseDao();
							map = dao.getDataByIp(node.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dao.close();
						}

						for (int i = 0; i < detailList.size(); i++) {
							AclDetail detail = (AclDetail) detailList.get(i);
							value = map.get(detail.getBaseId());
							setAlarmEvent(vo, alarmIndicatorsNode, value, detail, indicators);
						}
					}
				} else {
					return;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����̸澯��Ϣ
	 * 
	 * @param node
	 * @param diskVector
	 * @param nm
	 */
	public void checkDisk(Host node, Vector diskVector, AlarmIndicatorsNode nm) {
		if ("0".equals(nm.getEnabled())) {
			// �澯ָ��δ��� �����κ����� ����
			return;
		}
		if (diskVector == null || diskVector.size() == 0) {
			// δ�ɼ������� �����κ����� ����
			return;
		}
		for (int i = 0; i < diskVector.size(); i++) {
			DiskCollectEntity diskcollectdata = null;
			diskcollectdata = (DiskCollectEntity) diskVector.get(i);
			if (diskcollectdata.getEntity().equalsIgnoreCase("Utilization") && "diskperc".equals(nm.getName())) {
				// ������
				String diskname = diskcollectdata.getSubentity();
				if (node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
					diskname = diskcollectdata.getSubentity().substring(0, 3);
				}

				Hashtable alldiskalarmdata = null;
				try {
					alldiskalarmdata = ShareData.getAlldiskalarmdata();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (alldiskalarmdata == null) {
					alldiskalarmdata = new Hashtable();
				}
				Diskconfig diskconfig = null;
				if (node.getOstype() == 4 && node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
					diskconfig = (Diskconfig) alldiskalarmdata.get(node.getIpAddress() + ":" + diskcollectdata.getSubentity().substring(0, 3) + ":" + "��������ֵ");
				} else {
					diskconfig = (Diskconfig) alldiskalarmdata.get(node.getIpAddress() + ":" + diskcollectdata.getSubentity() + ":" + "��������ֵ");
				}
				if (diskconfig == null) {
					return;
				}
				int limevalue0 = diskconfig.getLimenvalue();
				int limevalue1 = diskconfig.getLimenvalue1();
				int limevalue2 = diskconfig.getLimenvalue2();
				nm.setLimenvalue0(limevalue0 + "");
				nm.setLimenvalue1(limevalue1 + "");
				nm.setLimenvalue2(limevalue2 + "");
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
				checkEvent(nodeDTO, nm, diskcollectdata.getThevalue(), diskname);
			} else if (diskcollectdata.getEntity().equalsIgnoreCase("UtilizationInc") && "diskinc".equals(nm.getName())) {
				// ������
				String diskname = diskcollectdata.getSubentity();
				if (node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
					diskname = diskcollectdata.getSubentity().substring(0, 3);
				}
				Hashtable alldiskalarmdata = null;
				try {
					alldiskalarmdata = ShareData.getAlldiskalarmdata();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (alldiskalarmdata == null) {
					alldiskalarmdata = new Hashtable();
				}
				Diskconfig diskconfig = null;
				if (node.getOstype() == 4 || node.getSysOid().startsWith("1.3.6.1.4.1.311.1.1.3")) {
					diskconfig = (Diskconfig) alldiskalarmdata.get(node.getIpAddress() + ":" + diskcollectdata.getSubentity().substring(0, 3) + ":" + "��������ֵ");
				} else {
					diskconfig = (Diskconfig) alldiskalarmdata.get(node.getIpAddress() + ":" + diskcollectdata.getSubentity() + ":" + "��������ֵ");
				}
				if (diskconfig == null) {
					return;
				}
				int limevalue0 = diskconfig.getLimenvalue();
				int limevalue1 = diskconfig.getLimenvalue1();
				int limevalue2 = diskconfig.getLimenvalue2();
				nm.setLimenvalue0(limevalue0 + "");
				nm.setLimenvalue1(limevalue1 + "");
				nm.setLimenvalue2(limevalue2 + "");
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(node);
				checkEvent(nodeDTO, nm, diskcollectdata.getThevalue(), diskname);
			}
		}
	}

	/**
	 * �������Ƿ�Ϊ�澯
	 * <p>
	 * �÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���
	 * <p>
	 * 
	 * @param node
	 *            �豸
	 * @param nm
	 *            ָ��
	 * @param value
	 *            ֵ
	 * @param sIndex
	 *            ���ֵʱ����Ϊ��־����
	 * @diskAlarmIndicatorType �澯���
	 */
	public void checkDiskEvent(BaseVo baseVo, AlarmIndicatorsNode nm, String value, String sIndex, String diskAlarmIndicatorType) {
		sIndex = diskAlarmIndicatorType + ":" + sIndex;
		checkEvent(baseVo, nm, value, sIndex);
	}

	/**
	 * ����Ƿ�Ϊ�澯
	 * <p>
	 * �÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ��� �÷���������
	 * </p>
	 * 
	 * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String
	 *      sIndex)</a>
	 *      <p>
	 * @param node
	 *            �豸
	 * @param nm
	 *            ָ��
	 * @param pingvalue
	 *            ֵ
	 */
	public void checkEvent(BaseVo baseVo, AlarmIndicatorsNode nm, String value) {
		NodeDTO node = null;
		if (!(baseVo instanceof NodeDTO)) {
			NodeUtil nodeUtil = new NodeUtil();
			node = nodeUtil.conversionToNodeDTO(baseVo);
		} else {
			node = (NodeDTO) baseVo;
		}
		// SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() +
		// "===nm===" + nm.getLimenvalue0() + "==value====" + value);
		checkEvent(node, nm, value, "");
		return;
	}

	public void checkEvent(BaseVo baseVo, AlarmIndicatorsNode nm, String value, String sIndex) {
		NodeDTO node = null;
		if (!(baseVo instanceof NodeDTO)) {
			NodeUtil nodeUtil = new NodeUtil();
			node = nodeUtil.conversionToNodeDTO(baseVo);
		} else {
			node = (NodeDTO) baseVo;
		}
		// �� name ��Ϊ �ø澯��Ψһ��ʶ��
		String name = node.getId() + ":" + node.getType() + ":" + node.getSubtype() + ":" + nm.getName();
		if (sIndex != null && sIndex.trim().length() > 0) {
			name = name + ":" + sIndex;
		}
		CheckEvent lastCheckEvent = deleteEvent(node.getId() + "", node.getType(), node.getSubtype(), nm.getName(), sIndex);
		// ���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
		// �澯ɾ���Ĵ�����ע�� ͬһ��澯�� ֻ�澯һ�� �����һָ�����������ʾ��Ϣ
		if (nm.getEnabled().equalsIgnoreCase("0")) {
			// �澯ָ��δ��� �����κ����� ����
			return;
		}
		if (value == null || value.trim().length() == 0) {
			// δ�ɼ�ֵ �����κ��� ֱ�ӷ���
			return;
		}
		// �ж��Ƿ��͸澯 ������� >0 ����
		int alarmLevel = 0; // �澯�ȼ�
		try {
			if (AlarmConstant.DATATYPE_NUMBER.equals(nm.getDatatype())) {
				// ��������
				alarmLevel = checkAlarm(node, nm, Double.valueOf(value), name);
			} else if (AlarmConstant.DATATYPE_STRING.equals(nm.getDatatype())) {
				// �ַ�������
				alarmLevel = checkAlarm(node, nm, value, name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (alarmLevel > 0) {
			// ��Ҫ���͸澯����
			try {
				Hashtable vmData = new Hashtable();
				try {
					if (nm.getSubtype().equalsIgnoreCase("vmware")) {
						vmData = (Hashtable) ShareData.getVmdata().get("getname");
						sIndex = vmData.get(nm.getSubentity()).toString();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				sendAlarm(node, nm, value, alarmLevel, sIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
			// if(ShareData.getSendAlarmTimes() != null &&
			// ShareData.getSendAlarmTimes().containsKey(name)){
			// SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
			// try {
			// sendAlarmTimeDao.delete(name);
			// } catch (Exception e) {
			// e.printStackTrace();
			// } finally {
			// sendAlarmTimeDao.close();
			// }
			// }
			// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼

			if (ShareData.getSendAlarmTimes() != null
					&& (ShareData.getSendAlarmTimes().containsKey(name) || ShareData.getSendAlarmTimes().containsKey(name + ":3")
							|| ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes().containsKey(name + ":1"))) {
				Connection conn = null;
				Statement stmt = null;
				try {
					conn = DataGate.getCon();
					conn.setAutoCommit(false);
					stmt = conn.createStatement();
					stmt.executeUpdate("delete from nms_send_alarm_time where name like '" + name + "%'");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (stmt != null) {
							stmt.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						DataGate.freeCon(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (ShareData.getSendAlarmTimes().containsKey(name)) {
					ShareData.getSendAlarmTimes().remove(name);
				}
				if (ShareData.getSendAlarmTimes().containsKey(name + ":3")) {
					ShareData.getSendAlarmTimes().remove(name + ":3");
				}
				if (ShareData.getSendAlarmTimes().containsKey(name + ":2")) {
					ShareData.getSendAlarmTimes().remove(name + ":2");
				}
				if (ShareData.getSendAlarmTimes().containsKey(name + ":1")) {
					ShareData.getSendAlarmTimes().remove(name + ":1");
				}
			}

			// �ж�֮ǰ�Ƿ��и澯,�������͸澯�ָ���Ϣ
			if (lastCheckEvent != null) {
				// ֮ǰ�и澯 �� ���͸澯�ָ���Ϣ
				// TODO ������ø澯�ָ���Ϣ�ķ���
				try {
					sendAlert(node, nm, value, alarmLevel, sIndex);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ����Ƿ�Ϊ�澯
	 * <p>
	 * �÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ��� �÷���������
	 * </p>
	 * 
	 * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String
	 *      sIndex)</a>
	 *      <p>
	 * @param node
	 *            �豸
	 * @param nm
	 *            ָ��
	 * @param pingvalue
	 *            ֵ
	 */
	public void checkEvent(Node node, AlarmIndicatorsNode nm, String value) {
		NodeDTO nodeDTO = null;
		NodeUtil nodeUtil = new NodeUtil();
		nodeDTO = nodeUtil.conversionToNodeDTO(node);
		// SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() +
		// "===nm===" + nm.getName() + "==value====" + value);
		// SysLogger.info("=====���Ping�ĸ澯===" + "===node===" + node.getId() +
		// "===nm===" + nm.getLimenvalue0() + "==value====" + value);
		checkEvent(nodeDTO, nm, value, "");
		return;
	}

	/**
	 * ����Ƿ�Ϊ�澯
	 * <p>
	 * �÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ��� �÷���������
	 * </p>
	 * 
	 * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String
	 *      sIndex)</a>
	 *      <p>
	 * @param node
	 *            �豸
	 * @param nm
	 *            ָ��
	 * @param pingvalue
	 *            ֵ
	 */
	public void checkEvent(Node node, AlarmIndicatorsNode nm, String value, String sIndex) {
		NodeDTO nodeDTO = null;
		NodeUtil nodeUtil = new NodeUtil();
		nodeDTO = nodeUtil.conversionToNodeDTO(node);
		checkEvent(nodeDTO, nm, value, sIndex);
		return;
	}

	/**
	 * 
	 * ����¼��ȼ�
	 * 
	 * <p>
	 * compare_type �ȽϷ�ʽ��1 Ϊ���� �����ڱȽϣ�0 Ϊ���� ��С�ڱȽ�
	 * </p>
	 * <p>
	 * ���ظ澯�ȼ� ������澯�򷵻� 0
	 * </p>
	 * 
	 * @author nielin
	 * @param value
	 *            ֵ
	 * @param limenvalue0
	 *            һ����ֵ
	 * @param limenvalue1
	 *            ������ֵ
	 * @param limenvalue2
	 *            ������ֵ
	 * @param compare_type
	 *            �ȽϷ�ʽ
	 * 
	 * @return level
	 */
	private int checkEventLevel(double value, double limenvalue0, double limenvalue1, double limenvalue2, int compare_type) {
		int level = 0; // ��Ҫ���صĵȼ�
		if (compare_type == 0) {
			// ����Ƚ�
			if (value <= limenvalue2) {
				level = 3;
			} else if (value <= limenvalue1) {
				level = 2;
			} else if (value <= limenvalue0) {
				level = 1;
			} else {
				level = 0;
			}
		} else {
			// ����Ƚ�
			if (value >= limenvalue2) {
				level = 3;
			} else if (value >= limenvalue1) {
				level = 2;
			} else if (value >= limenvalue0) {
				level = 1;
			} else {
				level = 0;
			}
		}
		return level;
	}

	/**
	 * 
	 * �ַ����Ƚ�
	 * 
	 * ����¼��ȼ�
	 * 
	 * �������¼��ȼ��Ƿ�����
	 * 
	 * <p>
	 * compare_type �ȽϷ�ʽ��1 Ϊ���� �����ڱȽϣ�0 Ϊ���� ��С�ڱȽ�
	 * </p>
	 * <p>
	 * ���ظ澯�ȼ� ������澯�򷵻� 0
	 * </p>
	 * 
	 * @author nielin
	 * @param value
	 *            ֵ
	 * @param limenvalue0
	 *            һ����ֵ
	 * @param limenvalue1
	 *            ������ֵ
	 * @param limenvalue2
	 *            ������ֵ
	 * @param isAlarm0
	 *            һ����ֵ�Ƿ����� 1 ����
	 * @param isAlarm1
	 *            ������ֵ�Ƿ����� 1 ����
	 * @param isAlarm2
	 *            ������ֵ�Ƿ����� 1 ����
	 * @param compare_type
	 *            �ȽϷ�ʽ
	 * 
	 * @return level
	 */
	private int checkEventLevel(String value, String limenvalue0, String limenvalue1, String limenvalue2, String isAlarm0, String isAlarm1, String isAlarm2, int compare_type) {
		int level = 0; // ��Ҫ���صĵȼ�
		if (compare_type == 2) {
			// ��ȱȽ�
			if (limenvalue2 != null && limenvalue2.trim().length() > 0 && !limenvalue2.trim().equalsIgnoreCase(value.trim()) && "1".equals(isAlarm2)) {
				level = 3;
			} else if (limenvalue1 != null && limenvalue1.trim().length() > 0 && !limenvalue1.trim().equalsIgnoreCase(value.trim()) && "1".equals(isAlarm1)) {
				level = 2;
			} else if (limenvalue0 != null && limenvalue0.trim().length() > 0 && !limenvalue0.trim().equalsIgnoreCase(value.trim()) && "1".equals(isAlarm0)) {
				level = 1;
			} else {
				level = 0;
			}
		}
		return level;
	}

	/**
	 * ���澯����
	 * <p>
	 * ������ڹ涨�ĸ澯����������һ������ 0���� ���򷵻� 0 ����ͬʱ���澯���� + 1
	 * <p>;
	 * 
	 * @param alarmIndicatorsNode
	 *            �澯ָ��
	 * @param alarmLevel
	 *            ��ǰ�澯�ȼ�
	 * @return
	 */
	private int checkEventTimes(AlarmIndicatorsNode alarmIndicatorsNode, int eventLevel, String name) {
		int eventTimes = 0; // �����¼��Ĵ���
		int defineTimes = 0; // ��������������¼�����
		int lastEventTimes = 0; // ֮ǰ�������¼�����
		if (eventLevel == 0) {
			// ����¼��ȼ� Ϊ 0 ˵���¼��ָ� ������¼�������Ϊ 0
			setEventTimes(name, 0);
			return eventTimes;
		}

		defineTimes = getTimesByLevel(alarmIndicatorsNode, eventLevel);
		lastEventTimes = getEventTimes(name, eventLevel);
		eventTimes = lastEventTimes + 1; // �ϴμ���� Ȼ�󱣴�
		setEventTimes(name, eventTimes);
		if (eventTimes < defineTimes) {
			// ���С�ڶ���Ĵ����򲻲����澯 ���� 0
			eventTimes = 0;
		}
		return eventTimes;
	}

	/**
	 * ����Ƿ�Ϊ�澯
	 * <p>
	 * �÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ��� �÷���������
	 * </p>
	 * 
	 * @see checkEvent(Host node,AlarmIndicatorsNode nm,String value, String
	 *      sIndex)</a>
	 *      <p>
	 * @param node
	 *            �豸
	 * @param nm
	 *            ָ��
	 * @param pingvalue
	 *            ֵ
	 */
	public void checkMiddlewareEvent(BaseVo node, AlarmIndicatorsNode nm, String value) {
		NodeDTO nodeDTO = null;
		NodeUtil nodeUtil = new NodeUtil();
		nodeDTO = nodeUtil.conversionToNodeDTO(node);
		checkMiddlewareEvent(nodeDTO, nm, value, "");
		return;
	}

	/**
	 * ����Ƿ�Ϊ�澯
	 * <p>
	 * �÷����Ժ������Ϊһ��ͨ�õķ��� ������Ƿ�Ϊһ���澯������ ����Ϊ�ɼ���ͳһ���
	 * <p>
	 * 
	 * @param node
	 *            �豸
	 * @param nm
	 *            ָ��
	 * @param value
	 *            ֵ
	 * @param sIndex
	 *            ���ֵʱ����Ϊ��־����
	 */
	public void checkMiddlewareEvent(BaseVo baseVo, AlarmIndicatorsNode nm, String value, String sIndex) {
		NodeDTO node = null;
		if (!(baseVo instanceof NodeDTO)) {
			NodeUtil nodeUtil = new NodeUtil();
			node = nodeUtil.conversionToNodeDTO(baseVo);
		} else {
			node = (NodeDTO) baseVo;
		}
		int alarmLevel = 0; // �澯�ȼ�
		// �� name ��Ϊ �ø澯��Ψһ��ʶ��
		String name = node.getId() + ":" + nm.getType() + ":" + nm.getSubtype() + ":" + nm.getName();
		if (sIndex != null && sIndex.trim().length() > 0) {
			name = name + ":" + sIndex;
		}
		// ���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
		CheckEvent lastCheckEvent = deleteEvent(node.getId() + "", node.getType(), node.getSubtype(), nm.getName(), sIndex);
		if (nm.getEnabled().equalsIgnoreCase("0")) {
			// �澯ָ��δ��� �����κ����� ����
			return;
		}
		if (!AlarmConstant.DATATYPE_NUMBER.equals(nm.getDatatype())) {
			// ���������͵ķ���
			return;
		}
		if (value == null || value.trim().length() == 0) {
			// δ�ɼ�ֵ �����κ��� ֱ�ӷ���
			return;
		}
		// �ж��Ƿ��͸澯 ������� >0 ����
		try {
			alarmLevel = checkAlarm(node, nm, Double.valueOf(value), name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (alarmLevel > 0) {
			// ��Ҫ���͸澯����
			try {
				sendMiddlewareAlarm(node, nm, value, alarmLevel, sIndex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
			SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
			try {
				sendAlarmTimeDao.delete(name);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sendAlarmTimeDao.close();
			}

			// �ж�֮ǰ�Ƿ��и澯,�������͸澯�ָ���Ϣ
			if (lastCheckEvent != null) {
				// ֮ǰ�и澯 �� ���͸澯�ָ���Ϣ
				// TODO ������ø澯�ָ���Ϣ�ķ���

			}
		}
	}

	/**
	 * ���ɸ澯�ָ��¼�
	 * 
	 * @param alarmIndicatorsNode
	 *            �澯ָ��
	 * @param vo
	 *            �豸VO
	 * @param value
	 *            ��ǰָ��ֵ
	 * @return
	 */
	public synchronized EventList createEvent(AlarmIndicatorsNode alarmIndicatorsNode, Object vo, String value, String checkEventName) {
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = null;
		Node node = (Node) vo;
		if (vo instanceof Web) {
			Web _web = (Web) PollingEngine.getInstance().getWebByID(node.getId());
			nodeDTO = nodeUtil.conversionToNodeDTO(_web);
		} else if (vo instanceof Host) {
			Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
			nodeDTO = nodeUtil.conversionToNodeDTO(host);
		}
		if (nodeDTO != null) {
			String unit = alarmIndicatorsNode.getThreshlod_unit();
			String eventtype = "poll";
			String eventlocation = nodeDTO.getName() + "(" + nodeDTO.getName() + ")";
			String bid = nodeDTO.getBusinessId();
			Hashtable checkEventHash = ShareData.getCheckEventHash();
			String time = null;// �澯����ʱ�䣬Ĭ�Ϸ���Ϊ��λ
			long timeLong = 0;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (checkEventHash != null && checkEventHash.size() > 0) {
				if (checkEventHash.containsKey(checkEventName)) {
					CheckEventDao checkEventDao = new CheckEventDao();
					CheckEvent checkEvent = null;
					try {
						checkEvent = (CheckEvent) checkEventDao.findCheckEventByName(node.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype(), alarmIndicatorsNode.getName(),
								null);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						checkEventDao.close();
					}
					String collecttime = checkEvent.getCollecttime();
					Date firstAlarmDate = null;
					try {
						firstAlarmDate = formatter.parse(collecttime);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (firstAlarmDate != null) {
						timeLong = new Date().getTime() - firstAlarmDate.getTime();
					}
				}
			}

			if (timeLong < 1000 * 60) {// С��1����,��
				time = timeLong / 1000 + "��";
			} else {// С��1Сʱ,��
				time = timeLong / (60 * 1000) + "��";
			}
			String name = alarmIndicatorsNode.getName();
			if (alarmIndicatorsNode.getName().equals("diskperc") || alarmIndicatorsNode.getName().equals("diskperc")) {
				int startNum = checkEventName.lastIndexOf(":");
				name = checkEventName.substring(startNum + 1);
			}
			String content = nodeDTO.getName() + "(IP: " + nodeDTO.getIpaddress() + ") " + name + "��ǰֵ:" + value + " " + unit + " �澯�ѻָ����澯����ʱ��" + time;
			Integer level1 = 0;
			String subtype = "";
			if ("service".equalsIgnoreCase(alarmIndicatorsNode.getType())) {
				subtype = alarmIndicatorsNode.getSubtype();
			} else {
				subtype = alarmIndicatorsNode.getType();
			}
			String subentity = alarmIndicatorsNode.getName();
			String objid = nodeDTO.getId() + "";
			EventList eventlist = new EventList();
			eventlist.setEventtype(eventtype);
			eventlist.setEventlocation(eventlocation);
			eventlist.setContent(content);
			eventlist.setLevel1(level1);
			eventlist.setManagesign(0);
			eventlist.setBak("");
			eventlist.setRecordtime(Calendar.getInstance());
			eventlist.setReportman("ϵͳ��ѯ");
			eventlist.setBusinessid(bid);
			eventlist.setNodeid(Integer.parseInt(objid));
			eventlist.setOid(0);
			eventlist.setSubtype(subtype);
			eventlist.setSubentity(subentity);
			return eventlist;
		}
		return null;
	}

	/**
	 * �����澯��
	 * 
	 * @param eventtype
	 * @param eventlocation
	 * @param bid
	 * @param content
	 * @param level1
	 * @param subtype
	 * @param subentity
	 * @param ipaddress
	 * @param objid
	 * @return
	 */
	private EventList createEvent(String eventtype, String eventlocation, String bid, String content, int level1, String subtype, String subentity, String ipaddress, String objid) {
		// �����¼�
		EventList eventlist = new EventList();
		eventlist.setEventtype(eventtype);
		eventlist.setEventlocation(eventlocation);
		eventlist.setContent(content);
		eventlist.setLevel1(level1);
		eventlist.setManagesign(0);
		eventlist.setBak("");
		eventlist.setRecordtime(Calendar.getInstance());
		eventlist.setReportman("ϵͳ��ѯ");
		eventlist.setBusinessid(bid);
		eventlist.setNodeid(Integer.parseInt(objid));
		eventlist.setOid(0);
		eventlist.setSubtype(subtype);
		eventlist.setSubentity(subentity);
		return eventlist;
	}

	/**
	 * �������������澯
	 * 
	 * @param ip
	 * @param hostServiceVector
	 * @return
	 */
	public List createHostServiceGroupEventList(String ip, Vector hostServiceVector, AlarmIndicatorsNode alarmIndicatorsNode) {
		if (alarmIndicatorsNode == null) {
			return null;
		}
		List returnList = new ArrayList();
		if (hostServiceVector == null || hostServiceVector.size() == 0) {
			return returnList;
		}

		try {

			Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);

			HostServiceGroupConfigurationUtil hostServiceGroupConfigurationUtil = new HostServiceGroupConfigurationUtil();
			List list = hostServiceGroupConfigurationUtil.gethostservicegroupByIpAndMonFlag(ip, "1");

			if (list == null || list.size() == 0) {
				return returnList;
			}

			for (int i = 0; i < list.size(); i++) {
				HostServiceGroup hostServiceGroup = (HostServiceGroup) list.get(i);
				List hostServiceList = hostServiceGroupConfigurationUtil.gethostservicegroupConfigurationByGroupId(String.valueOf(hostServiceGroup.getId()));

				if (hostServiceList == null || hostServiceList.size() == 0) {
					continue;
				}

				// �����������б�������Ҫ����������,��⵽����ͱ�����
				List whiteList = new ArrayList();
				// �����������б�������Ҫ������ܴ��,��⵽��ͱ�����
				List blackList = new ArrayList();

				// 1:�����������
				// 0:�������������
				for (int j = 0; j < hostServiceList.size(); j++) {
					HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) hostServiceList.get(j);
					String status = hostServiceGroupConfiguration.getStatus();
					boolean isLived = false;
					if (hostServiceVector != null) {
						for (int k = 0; k < hostServiceVector.size(); k++) {
							ServiceCollectEntity serviceEntity = (ServiceCollectEntity) hostServiceVector.get(k);
							if (hostServiceGroupConfiguration.getName().trim().equals(serviceEntity.getName())) {
								isLived = true;
								break;
							}
						}
					}
					if (!isLived && "0".equals(status)) {
						whiteList.add(hostServiceGroupConfiguration);
					}
					if (isLived && "1".equals(status)) {
						blackList.add(hostServiceGroupConfiguration);
					}
				}
				StringBuffer message = new StringBuffer();
				message.append(hostNode.getAlias()+"(");
				message.append(ip);
				message.append(") ����������[");
				message.append(hostServiceGroup.getName());
				message.append("]�澯,");
				if (whiteList.size() > 0 || blackList.size() > 0) {
					for (int j = 0; j < whiteList.size(); j++) {
						HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) whiteList.get(j);
						message.append("����[");
						message.append(hostServiceGroupConfiguration.getName());
						message.append("]��ֹͣ");
					}
					for (int j = 0; j < blackList.size(); j++) {
						HostServiceGroupConfiguration hostServiceGroupConfiguration = (HostServiceGroupConfiguration) blackList.get(j);
						message.append("����[");
						message.append(hostServiceGroupConfiguration.getName());
						message.append("]������");
					}
					EventList eventList = new EventList();
					eventList.setEventtype("poll");
					eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
					eventList.setContent(message.toString());
					eventList.setLevel1(Integer.parseInt(hostServiceGroup.getAlarm_level()));
					eventList.setManagesign(0);
					eventList.setRecordtime(Calendar.getInstance());
					eventList.setReportman("ϵͳ��ѯ");
					eventList.setNodeid(hostNode.getId());
					eventList.setBusinessid(hostNode.getBid());
					eventList.setSubtype("host");
					eventList.setSubentity("hostservice");
					returnList.add(eventList);
					try {
						sendAlarm(eventList, alarmIndicatorsNode, hostServiceGroup.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					String name = alarmIndicatorsNode.getNodeid() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
							+ alarmIndicatorsNode.getName() + ":" + hostServiceGroup.getName();
					this.deleteEvent(alarmIndicatorsNode.getNodeid() + "", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(),
							hostServiceGroup.getName());
					// �ڸ澯û����ȫ�ָ���������״̬������ɾ���澯ʱ��
					if (ShareData.getSendAlarmTimes() != null
							&& (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes()
									.containsKey(name + ":1"))) {

						SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
						try {
							sendAlarmTimeDao.deleteByName(name);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							sendAlarmTimeDao.close();
						}
						if (ShareData.getSendAlarmTimes().containsKey(name + ":3")) {
							ShareData.getSendAlarmTimes().remove(name + ":3");
						}
						if (ShareData.getSendAlarmTimes().containsKey(name + ":2")) {
							ShareData.getSendAlarmTimes().remove(name + ":2");
						}
						if (ShareData.getSendAlarmTimes().containsKey(name + ":1")) {
							ShareData.getSendAlarmTimes().remove(name + ":1");
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return returnList;
	}

	/**
	 * ����as400������澯
	 * 
	 * @param ip
	 * @param hostServiceVector
	 * @return
	 */
	public List createJobForAS400GroupEventList(String ip, List jobForAS400list, AlarmIndicatorsNode alarmIndicatorsNode) {
		if (alarmIndicatorsNode == null) {
			return null;
		}
		List returnList = new ArrayList();
		if (jobForAS400list == null || jobForAS400list.size() == 0) {
			return returnList;
		}

		try {

			Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
			JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
			List list = jobForAS400GroupDetailUtil.getJobForAS400GroupByIpAndMonFlag(ip, "1");

			if (list == null || list.size() == 0) {
				return returnList;
			}

			for (int i = 0; i < list.size(); i++) {
				try {
					JobForAS400Group jobForAS400Group = (JobForAS400Group) list.get(i);
					List jobForAS400DetailList = jobForAS400GroupDetailUtil.getJobForAS400GroupDetailByGroupId(String.valueOf(jobForAS400Group.getId()));

					if (jobForAS400DetailList == null || jobForAS400DetailList.size() == 0) {
						continue;
					}

					List wrongList = new ArrayList();

					for (int j = 0; j < jobForAS400DetailList.size(); j++) {
						JobForAS400GroupDetail jobForAS400GroupDetail = (JobForAS400GroupDetail) jobForAS400DetailList.get(j);

						boolean isLived = false;
						List jobForAS400List2 = new ArrayList();
						if (jobForAS400list != null) {
							for (int k = 0; k < jobForAS400list.size(); k++) {
								JobForAS400 jobForAS400 = (JobForAS400) jobForAS400list.get(k);
								if (jobForAS400GroupDetail.getName().trim().equals(jobForAS400.getName())) {
									jobForAS400List2.add(jobForAS400);
									isLived = true;
								}
							}
						}

						String eventMessage = "";

						Vector perVector = new Vector();
						if (jobForAS400GroupDetail.getStatus().equals("0") && isLived) {
							// ��� ��ҵ���� ���� ��ҵ�ļ��״̬Ϊ��������� ��澯
							perVector.add(jobForAS400GroupDetail);
							perVector.add("��ҵ��" + jobForAS400GroupDetail.getName() + " ���ֻ,�Ҹ���Ϊ��" + jobForAS400List2.size() + ";");
						} else if (jobForAS400GroupDetail.getStatus().equals("1") && !isLived) {
							// ��� ��ҵδ���� ���� ��ҵ�ļ��״̬Ϊ������� ��澯
							perVector.add(jobForAS400GroupDetail);
							perVector.add("��ҵ��" + jobForAS400GroupDetail.getName() + " δ�;");
						} else if (!jobForAS400GroupDetail.getStatus().equals("0") && isLived) {
							// ��� ��ҵ���� ���� ��ҵ�ļ��״̬Ϊ������� ���һ���ж�
							if (!"-1".equals(jobForAS400GroupDetail.getActiveStatusType())) {
								// ��� ��ҵ�Ļ�ļ��״̬���ǲ��� ������ж�

								try {
									int num = Integer.valueOf(jobForAS400GroupDetail.getNum());
									if (num > jobForAS400List2.size()) {
										eventMessage = "��ҵ��" + jobForAS400GroupDetail.getName() + " �����쳣,�������ڼ����Ŀ,��ʧ��" + (num - jobForAS400List2.size()) + "��";
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								String activeStatus = jobForAS400GroupDetail.getActiveStatus();
								if (activeStatus != null) {
									for (int m = 0; m < jobForAS400List2.size(); m++) {
										JobForAS400 jobForAS400 = (JobForAS400) jobForAS400List2.get(m);
										// �ж�ÿһ�����ֵ���ҵ
										if ("1".equals(jobForAS400GroupDetail.getActiveStatusType()) != (activeStatus.indexOf(jobForAS400.getActiveStatus()) != -1)) {
											// ��� ��ҵ�״̬����Ϊ������� �� �״̬�����ڵ�ǰ���״̬��
											// ��������Գ����쳣
											// ��� ��ҵ�״̬����Ϊ��������� ��
											// �״̬���ܳ����ڵ�ǰ���״̬�� �� ������� �����쳣
											eventMessage = eventMessage + "��ҵ��" + jobForAS400GroupDetail.getName() + " �����쳣״̬Ϊ; ��״̬Ϊ��" + jobForAS400.getActiveStatus() + ";";
										}
									}
								}

								if (eventMessage.trim().length() > 1) {
									perVector.add(jobForAS400GroupDetail);
									perVector.add(eventMessage);
								}
							}
						}
						if (perVector.size() > 1) {
							wrongList.add(perVector);
						}
					}
					if (wrongList.size() > 0) {
						String message = ip + " ����ҵ�飺" + jobForAS400Group.getName() + " �����쳣!";
						for (int j = 0; j < wrongList.size(); j++) {
							Vector perVector = (Vector) wrongList.get(j);
							message = message + perVector.get(1);

						}
						EventList eventList = new EventList();
						eventList.setEventtype("poll");
						eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")" + " ��ҵ��Ϊ��" + jobForAS400Group.getName());
						eventList.setContent(message);
						eventList.setLevel1(Integer.parseInt(jobForAS400Group.getAlarm_level()));
						eventList.setManagesign(0);
						eventList.setRecordtime(Calendar.getInstance());
						eventList.setReportman("ϵͳ��ѯ");
						eventList.setNodeid(hostNode.getId());
						eventList.setBusinessid(hostNode.getBid());
						eventList.setSubtype("host");
						eventList.setSubentity("jobForAS400Gourp");
						sendAlarm(eventList, alarmIndicatorsNode);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return returnList;
	}

	/**
	 * ����as400��ϵͳ�澯
	 * 
	 * @param ip
	 * @param hostServiceVector
	 * @return
	 */
	public List createJobForAS400SubSystemEventList(String ip, List jobForAS400list, List subSystemForAS400list, AlarmIndicatorsNode alarmIndicatorsNode) {
		if (alarmIndicatorsNode == null) {
			return null;
		}
		List returnList = new ArrayList();
		if (jobForAS400list == null || jobForAS400list.size() == 0 || subSystemForAS400list == null || subSystemForAS400list.size() == 0) {
			return returnList;
		}
		String path = "";
		try {
			Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);
			JobForAS400GroupDetailUtil jobForAS400GroupDetailUtil = new JobForAS400GroupDetailUtil();
			List list = jobForAS400GroupDetailUtil.getJobForAS400SubSystemByIpAndMonFlag(ip, "1");
			if (list == null || list.size() == 0) {
				return returnList;
			}
			for (int i = 0; i < list.size(); i++) {
				try {
					JobForAS400SubSystem jobForAS400SubSystem = (JobForAS400SubSystem) list.get(i);

					for (int j = 0; j < subSystemForAS400list.size(); j++) {
						SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400) subSystemForAS400list.get(j);
						if (subsystemForAS400.getName().equalsIgnoreCase(jobForAS400SubSystem.getName())) {
							path = subsystemForAS400.getPath();
							break;
						}
					}
					List wrongList = new ArrayList();
					List jobForAS400List2 = new ArrayList();
					if (jobForAS400list != null) {
						for (int k = 0; k < jobForAS400list.size(); k++) {
							JobForAS400 jobForAS400 = (JobForAS400) jobForAS400list.get(k);
							if (path.equals(jobForAS400.getSubsystem())) {
								jobForAS400List2.add(jobForAS400);
							}
						}
					}

					String eventMessage = "";

					Vector perVector = new Vector();

					if (!"-1".equals(jobForAS400SubSystem.getActive_status_type())) {
						// ��� ��ϵͳ��ҵ�Ļ�ļ��״̬���ǲ��� ������ж�
						try {
							int num = Integer.valueOf(jobForAS400SubSystem.getNum());
							if (num > jobForAS400List2.size()) {
								eventMessage = "��ϵͳ��" + jobForAS400SubSystem.getName() + " �����쳣,��ҵ�������ڼ����Ŀ,��ʧ��" + (num - jobForAS400List2.size()) + "��;";
							} else if (num < jobForAS400List2.size()) {
								eventMessage = "��ϵͳ��" + jobForAS400SubSystem.getName() + " �����쳣,��ҵ�������ڼ����Ŀ,���ӣ�" + (jobForAS400List2.size() - num) + "��;";
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						String activeStatus = jobForAS400SubSystem.getActive_status();

						if (activeStatus != null) {
							for (int m = 0; m < jobForAS400List2.size(); m++) {
								JobForAS400 jobForAS400 = (JobForAS400) jobForAS400List2.get(m);
								// �ж�ÿһ�����ֵ���ҵ
								if ("1".equals(jobForAS400SubSystem.getActive_status_type()) != (activeStatus.indexOf(jobForAS400.getActiveStatus()) != -1)) {
									// ��� ��ҵ�״̬����Ϊ������� �� �״̬�����ڵ�ǰ���״̬��
									// ��������Գ����쳣
									// ��� ��ҵ�״̬����Ϊ��������� �� �״̬���ܳ����ڵ�ǰ���״̬�� ��
									// ������� �����쳣
									eventMessage = eventMessage + "��ϵͳ��" + jobForAS400SubSystem.getName() + " ��ҵ��������쳣״̬Ϊ; ��״̬Ϊ��" + jobForAS400.getActiveStatus() + ";";
								}
							}
						}

						if (eventMessage.trim().length() > 1) {
							perVector.add(eventMessage);
						}
					}

					if (perVector.size() > 0) {
						wrongList.add(perVector);
					}
					if (wrongList.size() > 0) {
						String message = ip + " ����ϵͳ��" + jobForAS400SubSystem.getName() + " �����쳣!";
						for (int j = 0; j < wrongList.size(); j++) {
							Vector perVector1 = (Vector) wrongList.get(j);
							message = message + perVector1.get(0);

						}
						EventList eventList = new EventList();
						eventList.setEventtype("poll");
						eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")" + " ��ϵͳΪ��" + jobForAS400SubSystem.getName());
						eventList.setContent(message);
						eventList.setLevel1(Integer.parseInt(jobForAS400SubSystem.getAlarm_level()));
						eventList.setManagesign(0);
						eventList.setRecordtime(Calendar.getInstance());
						eventList.setReportman("ϵͳ��ѯ");
						eventList.setNodeid(hostNode.getId());
						eventList.setBusinessid(hostNode.getBid());
						eventList.setSubtype("host");
						eventList.setSubentity("subsystem");
						sendAlarm(eventList, alarmIndicatorsNode);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return returnList;
	}

	/**
	 * 
	 * �������������澯 nielin add
	 * 
	 * @date 2010-08-18
	 * @param ip
	 * @param proVector
	 */
	public List createProcessGroupEventList(String ip, Vector proVector, AlarmIndicatorsNode alarmIndicatorsNode) {
		if (alarmIndicatorsNode == null) {
			return null;
		}
		List retList = new ArrayList();
		if (proVector == null || proVector.size() == 0) {
			return retList;
		}
		try {
			Node hostNode = PollingEngine.getInstance().getNodeByIP(ip);

			ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
			List list = processGroupConfigurationUtil.getProcessGroupByIpAndMonFlag(ip, "1");

			if (list == null || list.size() == 0) {
				return null;
			}

			for (int i = 0; i < list.size(); i++) {
				ProcessGroup processGroup = (ProcessGroup) list.get(i);

				EventList eventList = new EventList();
				List wrongList = new ArrayList(); // �����б�

				List processGroupConfigurationList = processGroupConfigurationUtil.getProcessGroupConfigurationByGroupId(String.valueOf(processGroup.getId()));
				if (processGroupConfigurationList == null || processGroupConfigurationList.size() == 0) {
					continue;
				}

				for (int j = 0; j < processGroupConfigurationList.size(); j++) {
					int num = 0;
					ProcessGroupConfiguration processGroupConfiguration = (ProcessGroupConfiguration) processGroupConfigurationList.get(j);
					for (int k = 0; k < proVector.size(); k++) {
						ProcessCollectEntity processEntity = (ProcessCollectEntity) proVector.elementAt(k);
						if ("Name".equals(processEntity.getEntity())) {
							if (processGroupConfiguration.getName().trim().equals(processEntity.getThevalue().trim())) {
								num++;
							}
						}
					}
					int times = Integer.parseInt(processGroupConfiguration.getTimes());
					String status = processGroupConfiguration.getStatus();
					// �������������������٣�ֻҪ���־ͱ���
					if ("1".equals(status)) {
						eventList.setSubentity("proc:status1" + processGroupConfiguration.getName());
						if (num > 0) {
							// num = num - times;
							List wrongProlist = new ArrayList();
							wrongProlist.add(processGroupConfiguration.getName());
							wrongProlist.add(num);
							wrongProlist.add("black");
							wrongList.add(wrongProlist);
						}
					} else {
						eventList.setSubentity("proc:status2" + processGroupConfiguration.getName());
						// ������
						if (num < times) {
							// ��ʧ�ĸ���
							num = times - num;
							List wrongProlist = new ArrayList();
							wrongProlist.add(processGroupConfiguration.getName());
							wrongProlist.add(num);
							wrongProlist.add("lost");
							wrongProlist.add(times);
							wrongList.add(wrongProlist);
						} else if (num > times) {
							// ����ĸ���
							num = num - times;
							List wrongProlist = new ArrayList();
							wrongProlist.add(processGroupConfiguration.getName());
							wrongProlist.add(num);
							wrongProlist.add("more");
							wrongProlist.add(times);
							wrongList.add(wrongProlist);
						}
					}
				}

				if (wrongList.size() > 0) {
					String message =hostNode.getAlias()+"("+ ip + ") ����������[" + processGroup.getName() + "]�澯,";
					for (int j = 0; j < wrongList.size(); j++) {
						List wrongProList = (List) wrongList.get(j);
						String flag = (String) wrongProList.get(2);
						if ("black".equals(flag)) {
							message = message + "����������[" + wrongProList.get(0) + "]����,����=" + wrongProList.get(1);
						} else if ("lost".equals(flag)) {
							message = message + "����������[" + wrongProList.get(0) + "]��ʧ����=" + wrongProList.get(1) + ",ָ������=" + wrongProList.get(3);
						} else if ("more".equals(flag)) {
							message = message + "����������[" + wrongProList.get(0) + "]���Ӹ���=" + wrongProList.get(1) + ",ָ������=" + wrongProList.get(3);
						}
					}
					eventList.setEventtype("poll");
					eventList.setEventlocation(hostNode.getAlias() + "(" + ip + ")");
					eventList.setContent(message);
					eventList.setLevel1(Integer.valueOf(processGroup.getAlarm_level()));
					eventList.setManagesign(0);
					eventList.setRecordtime(Calendar.getInstance());
					eventList.setReportman("ϵͳ��ѯ");
					eventList.setNodeid(hostNode.getId());
					eventList.setBusinessid(hostNode.getBid());
					eventList.setSubtype("host");

					retList.add(eventList);
					try {
						sendAlarm(eventList, alarmIndicatorsNode, processGroup.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					String name = alarmIndicatorsNode.getNodeid() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
							+ alarmIndicatorsNode.getName() + ":" + processGroup.getName();
					this.deleteEvent(alarmIndicatorsNode.getNodeid() + "", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(),
							processGroup.getName());
					// �ڸ澯û����ȫ�ָ���������״̬������ɾ���澯ʱ��
					if (ShareData.getSendAlarmTimes() != null
							&& (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData.getSendAlarmTimes()
									.containsKey(name + ":1"))) {

						SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
						try {
							sendAlarmTimeDao.deleteByName(name);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							sendAlarmTimeDao.close();
						}
						if (ShareData.getSendAlarmTimes().containsKey(name + ":3")) {
							ShareData.getSendAlarmTimes().remove(name + ":3");
						}
						if (ShareData.getSendAlarmTimes().containsKey(name + ":2")) {
							ShareData.getSendAlarmTimes().remove(name + ":2");
						}
						if (ShareData.getSendAlarmTimes().containsKey(name + ":1")) {
							ShareData.getSendAlarmTimes().remove(name + ":1");
						}
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retList;
	}

	/**
	 * �� ���ݿ��� ɾ���ϴεĸ澯 ����������ҳ��� ��ɾ�������� �� �����������null
	 * <p>
	 * ���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
	 * <p>
	 * 
	 * @param name
	 * @return CheckEvent
	 */
	public CheckEvent deleteEvent(String nodeId, String type, String subtype, String indicatorsName, String sIndex) {
		// ���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
		CheckEvent checkEvent = null;
		String name = nodeId + ":" + type + ":" + subtype + ":" + indicatorsName;
		if (sIndex != null && sIndex.trim().length() > 0) {
			name = name + ":" + sIndex;
		}
		Hashtable checkEventHash = ShareData.getCheckEventHash();
		if (checkEventHash != null && checkEventHash.size() > 0) {
			if (checkEventHash.containsKey(name)) {
				checkEvent = new CheckEvent();
				checkEvent.setNodeid(nodeId);
				checkEvent.setIndicatorsName(indicatorsName);
				checkEvent.setType(type);
				checkEvent.setSubtype(subtype);
				checkEvent.setSindex(sIndex);
				checkEvent.setAlarmlevel((Integer) checkEventHash.get(name));
				Connection conn = null;
				Statement stmt = null;
				try {
					conn = DataGate.getCon();
					conn.setAutoCommit(false);
					stmt = conn.createStatement();
					if (sIndex != null && sIndex.length() > 0) {
						stmt.executeUpdate("delete from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='"
								+ indicatorsName + "' and sindex='" + sIndex + "'");
					} else {
						stmt.executeUpdate("delete from nms_checkevent where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subtype + "' and indicators_name='"
								+ indicatorsName + "'");
					}
					conn.commit();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (stmt != null) {
							stmt.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						DataGate.freeCon(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				checkEventHash.remove(name);
			}
		}
		return checkEvent;// ���� CheckEvent����
	}

	/**
	 * �õ�֮ǰ�ĸ澯����
	 * 
	 * @param name
	 * @param alarmLevel
	 * @return
	 */
	private int getEventTimes(String name, int alarmLevel) {
		int times = 0;
		try {
			String num = (String) AlarmResourceCenter.getInstance().getAttribute(name);
			if (num != null && num.length() > 0) {
				times = Integer.parseInt(num);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return times;
	}

	/**
	 * ����ָ��͸澯�ȼ����ط�ֵ
	 * 
	 * @param nm
	 * @param alarmLevel
	 * @return
	 */
	private String getThresholdByLevel(AlarmIndicatorsNode nm, int alarmLevel) {
		String threshold = "";
		if (alarmLevel == 1) {
			threshold = nm.getLimenvalue0();
		} else if (alarmLevel == 2) {
			threshold = nm.getLimenvalue1();
		} else if (alarmLevel == 3) {
			threshold = nm.getLimenvalue2();
		}
		return threshold;
	}

	/**
	 * ����ָ��͸澯�ȼ����ض���ĸ澯����
	 * 
	 * @param nm
	 * @param alarmLevel
	 * @return
	 */
	private int getTimesByLevel(AlarmIndicatorsNode nm, int eventLevel) {
		int times_int = 0;
		String times_str = "0";
		if (eventLevel == 1) {
			times_str = nm.getTime0();
		} else if (eventLevel == 2) {
			times_str = nm.getTime1();
		} else if (eventLevel == 3) {
			times_str = nm.getTime2();
		}
		try {
			times_int = Integer.parseInt(times_str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return times_int;
	}

	/**
	 * �����θ澯��Ϣ���浽���ݿ���
	 * <p>
	 * ���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
	 * <p>
	 * 
	 * @param name
	 */
	private void saveEvent(CheckEvent checkEvent) {
		// ���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		String name = checkEvent.getNodeid() + ":" + checkEvent.getType() + ":" + checkEvent.getSubtype() + ":" + checkEvent.getIndicatorsName();
		if (checkEvent.getSindex() != null && checkEvent.getSindex().trim().length() > 0) {
			name = name + ":" + checkEvent.getSindex();
		}
		checkEventHashtable.put(name, checkEvent.getAlarmlevel());
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			CheckEvent vo = (CheckEvent) checkEvent;
			StringBuffer sql = new StringBuffer(100);
			sql.append("insert into nms_checkevent(nodeid,indicators_name,sindex,type,subtype,alarmlevel,thevalue,collecttime,bid)values(");
			sql.append("'");
			sql.append(vo.getNodeid());
			sql.append("','");
			sql.append(vo.getIndicatorsName());
			sql.append("','");
			sql.append(vo.getSindex());
			sql.append("','");
			sql.append(vo.getType());
			sql.append("','");
			sql.append(vo.getSubtype());
			sql.append("',");
			sql.append(vo.getAlarmlevel());
			sql.append(",'");
			sql.append(vo.getThevalue());
			sql.append("','");
			sql.append(vo.getCollecttime());
			sql.append("','");
			sql.append(vo.getBid());
			sql.append("')");
			stmt.executeUpdate(sql.toString());
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				DataGate.freeCon(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ����澯����
	 * 
	 * @param node
	 *            �豸
	 * @param alarmIndicatorsNode
	 *            ָ��
	 * @param value
	 *            �ɼ�����ֵ
	 * @param alarmLevel
	 *            �澯�ȼ�
	 */
	public void sendAlarm(EventList eventList, AlarmIndicatorsNode alarmIndicatorsNode) {
		CheckEvent checkEvent = new CheckEvent();
		checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
		checkEvent.setContent(eventList.getContent());
		checkEvent.setNodeid(alarmIndicatorsNode.getNodeid() + "");
		checkEvent.setSindex("");
		checkEvent.setThevalue("");
		checkEvent.setBid(eventList.getBusinessid());
		checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
		checkEvent.setType(alarmIndicatorsNode.getType());
		checkEvent.setAlarmlevel(eventList.getLevel1());
		// ���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
		boolean flag = true;
		Hashtable checkEventHash = ShareData.getCheckEventHash();
		if (checkEventHash != null && checkEventHash.size() > 0) {
			if (checkEventHash.containsKey(eventList.getNodeid() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":"
					+ alarmIndicatorsNode.getName())) {
				flag = false;// ����ʱ��checkevent�����
			}
		}
		if (flag) {
			// ���ڴ��в����ڸ澯�������ǵ�һ�β����澯���������ݿ����
			checkEvent.setCollecttime(CommonUtil.getDateAndTime());
			saveEvent(checkEvent);
		}
		SendAlarmUtil alarmUtil = new SendAlarmUtil();
		alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
	}

	/**
	 * ����澯����
	 * 
	 * @param node
	 *            �豸
	 * @param alarmIndicatorsNode
	 *            ָ��
	 * @param value
	 *            �ɼ�����ֵ
	 * @param alarmLevel
	 *            �澯�ȼ�
	 */
	public void sendAlarm(EventList eventList, AlarmIndicatorsNode alarmIndicatorsNode, String sIndex) {
		CheckEvent checkEvent = new CheckEvent();
		checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
		checkEvent.setContent(eventList.getContent());
		checkEvent.setNodeid(eventList.getNodeid() + "");
		checkEvent.setSindex("");
		if (sIndex != null && sIndex.length() > 0) {
			checkEvent.setSindex(sIndex);
		}
		checkEvent.setThevalue("");
		checkEvent.setBid(eventList.getBusinessid());
		checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
		checkEvent.setType(alarmIndicatorsNode.getType());
		checkEvent.setAlarmlevel(eventList.getLevel1());
		boolean flag = true;
		Hashtable checkEventHash = ShareData.getCheckEventHash();
		String name = eventList.getNodeid() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":" + alarmIndicatorsNode.getName();
		if (sIndex != null && sIndex.trim().length() > 0) {
			name = name + ":" + sIndex;
		}
		if (checkEventHash != null && checkEventHash.size() > 0) {
			if (checkEventHash.containsKey(name)) {
				flag = false;// ����ʱ��checkevent�����
			}
		}
		if (flag) {
			checkEvent.setCollecttime(CommonUtil.getDateAndTime());
			saveEvent(checkEvent);
		}
		SendAlarmUtil alarmUtil = new SendAlarmUtil();
		alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
	}

	/**
	 * wupinlong add �ļ��澯
	 * 
	 * @param id
	 * @param ip
	 * @param eventtype
	 * @param eventlocation
	 * @param bid
	 * @param content
	 * @param level1
	 * @param type
	 * @param subtype
	 * @param subentity
	 * @param alarmWayId
	 * @param tag
	 */
	public void sendAlarm(int id, String ip, String eventtype, String eventlocation, String bid, String content, Integer level1, String type, String subtype, String subentity,
			String alarmWayId, String tag) {
		EventList eventlist = new EventList();
		eventlist.setNodeid(id);
		eventlist.setEventtype(eventtype);
		eventlist.setEventlocation(eventlocation);
		eventlist.setContent(content);
		eventlist.setLevel1(level1);
		eventlist.setManagesign(0);
		eventlist.setBak("");
		eventlist.setRecordtime(Calendar.getInstance());
		eventlist.setReportman("ϵͳ��ѯ");
		eventlist.setBusinessid(bid);
		eventlist.setOid(0);
		eventlist.setSubtype(subtype);
		eventlist.setSubentity(subentity);

		// �������¸澯��Ϣ
		CheckEvent checkEvent = new CheckEvent();
		checkEvent.setAlarmlevel(1); // �澯����
		checkEvent.setIndicatorsName(subentity);
		checkEvent.setType(type);
		checkEvent.setSubtype(subtype);
		checkEvent.setNodeid(id + "");
		checkEvent.setContent(content);
		checkEvent.setSindex("");
		checkEvent.setBid(bid);
		checkEvent.setThevalue("");
		try {
			if ("0".equals(tag)) {
				checkEvent.setCollecttime(CommonUtil.getDateAndTime());
				saveEvent(checkEvent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
		alarmIndicatorsNode.setNodeid(ip);
		alarmIndicatorsNode.setType(type);
		alarmIndicatorsNode.setName(subentity);

		SendAlarmUtil alarmUtil = new SendAlarmUtil();
		alarmUtil.sendAlarm(checkEvent, eventlist, alarmIndicatorsNode, alarmWayId);
	}

	/**
	 * ����澯����
	 * 
	 * @param node
	 *            �豸
	 * @param alarmIndicatorsNode
	 *            ָ��
	 * @param value
	 *            �ɼ�����ֵ
	 * @param alarmLevel
	 *            �澯�ȼ�
	 * @param checkflag
	 *            �ж����ַ��������ָ�ʽ,��Ϊ�ַ�,����Ҫ�Ѿ���ķ�ֵд���¼���ȥ,0:���� 1:�ַ�
	 */
	public void sendAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel, int checkflag) {
		String unit = alarmIndicatorsNode.getThreshlod_unit();
		String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
		String eventtype = "poll";
		String eventlocation = node.getName() + "(" + node.getName() + ")";
		String bid = node.getBusinessId();
		String content = "";
		if (checkflag == 0) {
			content = node.getName() + "(IP: " + node.getIpaddress() + ") " + alarmIndicatorsNode.getAlarm_info() + " ��ǰֵ:" + value + " " + unit + " ��ֵ:" + threshold + " " + unit;
		} else {
			content = node.getName() + "(IP: " + node.getIpaddress() + ") " + alarmIndicatorsNode.getAlarm_info();
		}

		Integer level1 = alarmLevel;
		String subtype = "";
		if ("service".equalsIgnoreCase(alarmIndicatorsNode.getType())) {
			subtype = alarmIndicatorsNode.getSubtype();
		} else {
			subtype = alarmIndicatorsNode.getType();
		}
		String subentity = alarmIndicatorsNode.getName();
		String objid = node.getId() + "";
		String ipaddress = node.getIpaddress();
		// ���� eventList
		EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
		// ���澯�жϣ��ж��ڴ����Ƿ��и澯��������򲻴����ݿ�
		CheckEvent checkEvent = new CheckEvent();
		checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
		checkEvent.setContent(eventList.getContent());
		checkEvent.setNodeid(eventList.getNodeid() + "");
		checkEvent.setSindex("");
		checkEvent.setThevalue(value);
		checkEvent.setBid(bid);
		checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
		checkEvent.setType(alarmIndicatorsNode.getType());
		checkEvent.setAlarmlevel(eventList.getLevel1());
		checkEvent.setAlarmlevel(alarmLevel);
		// ���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
		// �ж��Ƿ񱣴�checkevent�¼���Ϣ
		boolean flag = true;
		Hashtable checkEventHash = ShareData.getCheckEventHash();
		if (checkEventHash != null && checkEventHash.size() > 0) {
			if (checkEventHash.containsKey(node.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":" + alarmIndicatorsNode.getName())) {
				flag = false;// ����ʱ��checkevent�����
			}
		}
		if (flag) {
			// ���ڴ��в����ڸ澯�������ǵ�һ�β����澯���������ݿ����
			checkEvent.setCollecttime(CommonUtil.getDateAndTime());
			saveEvent(checkEvent);
		}
		SendAlarmUtil alarmUtil = new SendAlarmUtil();
		alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);

		// ���澯��Ϣ����澯��Ϣ������
		NodeAlarmUtil.saveNodeAlarmInfo(eventList, subentity);
	}

	/**
	 * ����澯����
	 * 
	 * @param node
	 *            �豸
	 * @param alarmIndicatorsNode
	 *            ָ��
	 * @param value
	 *            �ɼ�����ֵ
	 * @param alarmLevel
	 *            �澯�ȼ�
	 */
	public void sendAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel, String sIndex) {
		if (sIndex == null) {
			sIndex = "";
		}
		String unit = alarmIndicatorsNode.getThreshlod_unit();
		if ("��".equals(unit)) {
			unit = "";
		}
		String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
		String eventtype = "poll";
		String eventlocation = node.getName() + "(" + node.getIpaddress() + ")";
		String subentity = alarmIndicatorsNode.getName();
		if (sIndex.trim().length() > 0) {
			eventlocation = eventlocation + "(" + sIndex + ")";
			subentity = alarmIndicatorsNode.getName() + ":" + sIndex;
		}
		String bid = node.getBusinessId();
		String content = eventlocation + " " + sIndex + " " + alarmIndicatorsNode.getAlarm_info() + " ��ǰֵ:" + value + " " + unit + " ��ֵ:" + threshold + " " + unit;
		Integer level1 = alarmLevel;
		String subtype = alarmIndicatorsNode.getType();

		String objid = node.getId() + "";
		String ipaddress = node.getIpaddress();

		// �������¸澯��Ϣ
		CheckEvent checkEvent = new CheckEvent();
		checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
		checkEvent.setType(alarmIndicatorsNode.getType());
		checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
		checkEvent.setNodeid(node.getNodeid() + "");
		checkEvent.setAlarmlevel(alarmLevel);
		checkEvent.setContent(content);
		checkEvent.setSindex("");
		checkEvent.setBid(bid);
		checkEvent.setThevalue(value);
		checkEvent.setCollecttime(CommonUtil.getDateAndTime());
		if (sIndex != null && sIndex.length() > 0) {
			checkEvent.setSindex(sIndex);
		}
		// ���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
		saveEvent(checkEvent);

		if ("middleware".equalsIgnoreCase(subtype) || "service".equalsIgnoreCase(subtype)) {
			subtype = alarmIndicatorsNode.getSubtype();
		}

		EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
		SendAlarmUtil alarmUtil = new SendAlarmUtil();
		alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
	}

	/**
	 * ����澯����
	 * 
	 * @param node
	 *            �豸
	 * @param alarmIndicatorsNode
	 *            ָ��
	 * @param value
	 *            �ɼ�����ֵ
	 * @param value
	 *            ��ظ澯��Ϣ
	 * @param alarmLevel
	 *            �澯�ȼ�
	 * @param checkflag
	 *            �ж����ַ��������ָ�ʽ,��Ϊ�ַ�,����Ҫ�Ѿ���ķ�ֵд���¼���ȥ,0:���� 1:�ַ�
	 */
	public void sendAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, String alarminfo, int alarmLevel, int checkflag) {
		String content = "";
		String unit = alarmIndicatorsNode.getThreshlod_unit();
		String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
		String eventtype = "poll";
		String eventlocation = node.getName() + "(" + node.getName() + ")";
		String bid = node.getBusinessId();
		if (checkflag == 0) {
			content = node.getName() + "(IP: " + node.getIpaddress() + ") " + alarmIndicatorsNode.getAlarm_info() + " ��ǰֵ:" + value + " " + unit + " ��ֵ:" + threshold + " " + unit;
		} else if (checkflag == 1) {
			content = node.getName() + "(IP: " + node.getIpaddress() + ":" + alarminfo + ") " + alarmIndicatorsNode.getAlarm_info() + " ��ǰֵ:" + value + " " + unit + " ��ֵ:"
					+ threshold + " " + unit;
		} else {
			content = node.getName() + "(IP: " + node.getIpaddress() + ") " + alarmIndicatorsNode.getAlarm_info();
		}
		CheckEvent checkEvent = new CheckEvent();
		checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
		checkEvent.setType(alarmIndicatorsNode.getType());
		checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
		checkEvent.setNodeid(node.getNodeid() + "");
		checkEvent.setAlarmlevel(alarmLevel);
		checkEvent.setContent(content);
		checkEvent.setSindex("");
		checkEvent.setThevalue(value);
		checkEvent.setBid(bid);
		checkEvent.setAlarmlevel(alarmLevel);
		boolean flag = true;
		Hashtable checkEventHash = ShareData.getCheckEventHash();
		if (checkEventHash != null && checkEventHash.size() > 0) {
			if (checkEventHash.containsKey(node.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":" + alarmIndicatorsNode.getName())) {
				flag = false;// ����ʱ��checkevent�����
			}
		}
		if (flag) {
			// ���ڴ��в����ڸ澯�������ǵ�һ�β����澯���������ݿ����
			checkEvent.setCollecttime(CommonUtil.getDateAndTime());
			saveEvent(checkEvent);
		}
		Integer level1 = alarmLevel;
		String subtype = "";
		if ("service".equalsIgnoreCase(alarmIndicatorsNode.getType())) {
			subtype = alarmIndicatorsNode.getSubtype();
		} else {
			subtype = alarmIndicatorsNode.getType();
		}
		String subentity = alarmIndicatorsNode.getName();
		String objid = node.getId() + "";
		String ipaddress = node.getIpaddress();
		// ���� eventList
		EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
		SendAlarmUtil alarmUtil = new SendAlarmUtil();
		alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);

	}

	// /�澯�ָ���ʾ
	public void sendAlert(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel, String sIndex) {
		if (sIndex == null) {
			sIndex = "";
		}
		String unit = alarmIndicatorsNode.getThreshlod_unit();
		if ("��".equals(unit)) {
			unit = "";
		}
		String eventtype = "poll";
		String eventlocation = node.getName() + "(" + node.getIpaddress() + ")";
		String subentity = alarmIndicatorsNode.getName();
		if (sIndex.trim().length() > 0) {
			eventlocation = eventlocation + "(" + sIndex + ")";
			subentity = subentity + ":" + sIndex;
		}
		String bid = node.getBusinessId();
		String content = node.getIpaddress() + " " + sIndex + " " + alarmIndicatorsNode.getAlarm_info() + " �ĸ澯�ѻָ�";
		Integer level1 = alarmLevel;
		String subtype = alarmIndicatorsNode.getType();

		String objid = node.getId() + "";
		String ipaddress = node.getIpaddress();

		// �������¸澯��Ϣ
		CheckEvent checkEvent = new CheckEvent();
		checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
		checkEvent.setType(alarmIndicatorsNode.getType());
		checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
		checkEvent.setNodeid(node.getNodeid() + "");
		checkEvent.setAlarmlevel(alarmLevel);
		checkEvent.setContent(content);
		checkEvent.setSindex("");
		checkEvent.setThevalue(value);
		if (sIndex != null && sIndex.length() > 0) {
			checkEvent.setSindex(sIndex);
		}

		// ���� eventList
		EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
		SendAlarmUtil alarmUtil = new SendAlarmUtil();
		alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
	}

	/**
	 * ����澯����
	 * 
	 * @param node
	 *            �豸
	 * @param alarmIndicatorsNode
	 *            ָ��
	 * @param value
	 *            �ɼ�����ֵ
	 * @param alarmLevel
	 *            �澯�ȼ�
	 */
	public void sendMiddlewareAlarm(NodeDTO node, AlarmIndicatorsNode alarmIndicatorsNode, String value, int alarmLevel, String sIndex) {
		String unit = alarmIndicatorsNode.getThreshlod_unit();
		String threshold = getThresholdByLevel(alarmIndicatorsNode, alarmLevel);
		String eventtype = "poll";
		String eventlocation = node.getName() + "(" + node.getName() + ")";
		String bid = node.getBusinessId();
		String content = node.getIpaddress() + " " + sIndex + " " + alarmIndicatorsNode.getAlarm_info() + " ��ǰֵ:" + value + " " + unit + " ��ֵ:" + threshold + " " + unit;
		CheckEvent checkEvent = new CheckEvent();
		checkEvent.setNodeid(String.valueOf(node.getId()));
		checkEvent.setIndicatorsName(alarmIndicatorsNode.getName());
		checkEvent.setType(alarmIndicatorsNode.getType());
		checkEvent.setSubtype(alarmIndicatorsNode.getSubtype());
		checkEvent.setSindex(sIndex);
		checkEvent.setThevalue(value);
		checkEvent.setContent(content);
		checkEvent.setBid(bid);
		checkEvent.setAlarmlevel(alarmLevel);
		boolean flag = true;
		Hashtable checkEventHash = ShareData.getCheckEventHash();
		if (checkEventHash != null && checkEventHash.size() > 0) {
			if (checkEventHash.containsKey(node.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getSubtype() + ":" + alarmIndicatorsNode.getName())) {
				flag = false;// ����ʱ��checkevent�����
			}
		}
		if (flag) {
			// ���ڴ��в����ڸ澯�������ǵ�һ�β����澯���������ݿ����
			checkEvent.setCollecttime(CommonUtil.getDateAndTime());
			saveEvent(checkEvent);
		}
		// ���ø澯�Ա���Ϣ�ŵ��ڴ�ϵͳ��,������Ҫ�ŵ����ݿ����
		saveEvent(checkEvent);
		Integer level1 = alarmLevel;
		String subtype = alarmIndicatorsNode.getType();
		String subentity = alarmIndicatorsNode.getName();
		String objid = node.getId() + "";
		String ipaddress = node.getIpaddress();
		// ���� eventList
		EventList eventList = createEvent(eventtype, eventlocation, bid, content, level1, subtype, subentity, ipaddress, objid);
		SendAlarmUtil alarmUtil = new SendAlarmUtil();
		alarmUtil.sendAlarm(checkEvent, eventList, alarmIndicatorsNode);
	}

	private void setAlarmEvent(Object vo, AlarmIndicatorsNode alarmIndicatorsNode, String value, Object object, String objType) {
		String limvalue = "";
		Node node = (Node) vo;
		PolicyInterface pInterface = null;
		QueueInfo queueInfo = null;
		AclDetail detail = null;
		String key = "";
		String realVal = "";
		String alamInfo = "";
		if (objType.equals("droprate")) {
			pInterface = (PolicyInterface) object;
			key = String.valueOf(alarmIndicatorsNode.getId()) + ":" + pInterface.getInterfaceName() + ":" + pInterface.getClassName();
			alamInfo = pInterface.getInterfaceName() + ":" + pInterface.getClassName();
			realVal = value;
		} else if (objType.equals("dropbytes")) {
			queueInfo = (QueueInfo) object;
			if (value.equals("input queue")) {
				realVal = queueInfo.getInputDrops() + "";
				key = String.valueOf(node.getId() + ":" + alarmIndicatorsNode.getId()) + ":" + queueInfo.getEntity() + ":input";
				alamInfo = queueInfo.getEntity() + ":�������";
			} else if (value.equals("output queue")) {
				realVal = queueInfo.getOutputDrops() + "";
				key = String.valueOf(node.getId() + ":" + alarmIndicatorsNode.getId()) + ":" + queueInfo.getEntity() + ":output";
				alamInfo = queueInfo.getEntity() + ":�������";
			}
		} else if (objType.equals("matches")) {
			detail = (AclDetail) object;
			key = String.valueOf(node.getId() + ":" + alarmIndicatorsNode.getId()) + ":" + detail.getBaseId() + ":" + detail.getName();
			alamInfo = value + ":" + detail.getName();
			realVal = detail.getMatches() + "";
		}
		if (AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())) {
			try {
				double value_int = Double.valueOf(realVal);
				double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());
				double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());
				double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());

				String level = "";
				String alarmTimes = "";

				// �Ƿ񳬹���ֵ
				boolean result = true;

				if (alarmIndicatorsNode.getCompare() == 0) {
					// ����Ƚ�
					if (value_int <= Limenvalue2) {
						level = "3";
						alarmTimes = alarmIndicatorsNode.getTime2();
						limvalue = alarmIndicatorsNode.getLimenvalue2();
					} else if (value_int <= Limenvalue1) {
						level = "2";
						alarmTimes = alarmIndicatorsNode.getTime1();
						limvalue = alarmIndicatorsNode.getLimenvalue1();
					} else if (value_int <= Limenvalue0) {
						level = "1";
						alarmTimes = alarmIndicatorsNode.getTime0();
						limvalue = alarmIndicatorsNode.getLimenvalue0();
					} else {
						result = false;
					}
				} else {
					// ����Ƚ�
					if (value_int >= Limenvalue2) {
						level = "3";
						alarmTimes = alarmIndicatorsNode.getTime2();
						limvalue = alarmIndicatorsNode.getLimenvalue2();
					} else if (value_int >= Limenvalue1) {
						level = "2";
						alarmTimes = alarmIndicatorsNode.getTime1();
						limvalue = alarmIndicatorsNode.getLimenvalue1();
					} else if (value_int >= Limenvalue0) {
						level = "1";
						alarmTimes = alarmIndicatorsNode.getTime0();
						limvalue = alarmIndicatorsNode.getLimenvalue0();
					} else {
						result = false;
					}
				}

				// �澯��Դ�е� �¼�����

				String num = (String) AlarmResourceCenter.getInstance().getAttribute(key);

				if (num == null || "".equals(num)) {
					num = "0";
				}

				if (!result) {
					// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
					SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
					try {
						sendAlarmTimeDao.delete(node.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getName());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						sendAlarmTimeDao.close();
					}
					// �����ʱδ�����澯 �� ��������Ϊ 0 ��
					num = "0";
					AlarmResourceCenter.getInstance().setAttribute(key, num);
					return;
				}

				int num_int = 0;
				int alarmTimes_int = 0;
				try {
					num_int = Integer.valueOf(num); // ��ǰ�澯����
					alarmTimes_int = Integer.valueOf(alarmTimes); // ����ĸ澯����
				} catch (RuntimeException e1) {
					e1.printStackTrace();
				}

				if (num_int + 1 >= alarmTimes_int) {

					if (vo instanceof Host) {
						// �¼����������澯���������� ���ɸ澯
						Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
						host.setAlarm(true);
						host.getAlarmMessage().add(
								alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + realVal + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
										+ alarmIndicatorsNode.getThreshlod_unit());
						// ������֮ǰ�ĸ澯����,������󼶱�
						if (Integer.valueOf(level) > host.getStatus()) {
							host.setStatus(Integer.valueOf(level));
						}
						if (Integer.valueOf(level) > host.getAlarmlevel()) {
							host.setAlarmlevel(Integer.valueOf(level));
						}
						NodeUtil nodeUtil = new NodeUtil();
						NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
						sendAlarm(nodeDTO, alarmIndicatorsNode, realVal, alamInfo, Integer.valueOf(level), 1);
					}

				} else {
					num_int = num_int + 1;
					AlarmResourceCenter.getInstance().setAttribute(key, String.valueOf(num_int));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * ���ø澯����
	 * 
	 * @param name
	 * @param times
	 * @return
	 */
	private int setEventTimes(String name, int times) {
		try {
			AlarmResourceCenter.getInstance().setAttribute(name, String.valueOf(times));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return times;
	}

	// ����澯
	public void updateData(Host node, NodeGatherIndicators nodeGatherIndicators, String value) {
		updateData(node, nodeGatherIndicators, value, null);
	}

	// ����澯
	public void updateData(Host node, NodeGatherIndicators nodeGatherIndicators, String value, String sIndex) {
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(node.getId() + "", nodeGatherIndicators.getType(), nodeGatherIndicators.getSubtype(),
				nodeGatherIndicators.getName());
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode nm = (AlarmIndicatorsNode) list.get(i);
				checkEvent(node, nm, value, sIndex);
			}
		}
	}

	public void updateData(Object vo, Object collectingData, String type, String subtype, AlarmIndicatorsNode alarmIndicatorsNode) {
		try {
			Node node = (Node) vo;
			// ���ڴ�����صĸ澯���,����ʵ�ִ����ݿ����ɾ���������
			// �澯ɾ���Ĵ�����ע�� ͬһ��澯�� ֻ�澯һ�� �����һָ�����������ʾ��Ϣ
			Hashtable datahashtable = (Hashtable) collectingData;
			// ��ȡJOB,�ж�JOB����
			List joblist = new ArrayList();
			// ��ȡϵͳ��Ϣ,�õ�CPU������
			Hashtable systemStatushashtable = new Hashtable();
			if ("1".equals(alarmIndicatorsNode.getEnabled())) {
				String indicators = alarmIndicatorsNode.getName();
				String value = "0";
				String limvalue = "";
				if ("jobnumber".equals(indicators)) {
					if (datahashtable.get("Jobs") != null) {
						joblist = (List) datahashtable.get("Jobs");
					}
					if (joblist == null) {
						joblist = new ArrayList();
					}
					value = joblist.size() + "";
				} else if ("cpu".equals(indicators)) {
					// CUP������
					Vector cpuVector = new Vector();
					if (datahashtable.get("cpu") != null) {
						cpuVector = (Vector) datahashtable.get("cpu");
					}
					if (cpuVector != null && cpuVector.size() > 0) {

						CpuCollectEntity cpudata = (CpuCollectEntity) cpuVector.get(0);
						value = cpudata.getThevalue();
					}
					if (systemStatushashtable.get("cpu") != null) {
						value = (String) systemStatushashtable.get("cpu");
					}
				} else if ("memory".equals(indicators)) {
					// memory������
					Vector memoryVector = new Vector();
					if (datahashtable.get("memory") != null) {
						memoryVector = (Vector) datahashtable.get("memory");
					}
					if (memoryVector != null && memoryVector.size() > 0) {
						MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(0);
						value = memorydata.getThevalue();
					}
					if (systemStatushashtable.get("memory") != null) {
						value = (String) systemStatushashtable.get("memory");
					}
				} else if ("pagingusage".equals(indicators)) {
					// ��ҳ��������
					Hashtable paginghash = new Hashtable();
					if (datahashtable.get("pagingusage") != null) {
						paginghash = (Hashtable) datahashtable.get("pagingusage");
					}
					if (paginghash != null && paginghash.size() > 0) {
						if (paginghash.get("Percent_Used") != null) {
							value = ((String) paginghash.get("Percent_Used")).replaceAll("%", "");
						}

					}
					if (systemStatushashtable.get("cpu") != null) {
						value = (String) systemStatushashtable.get("cpu");
					}
				} else if ("physicalmemory".equals(indicators)) {
					// �����ڴ�������
					Vector memoryVector = new Vector();
					if (datahashtable.get("physicalmem") != null) {
						memoryVector = (Vector) datahashtable.get("physicalmem");
					}
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int i = 0; i < memoryVector.size(); i++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(i);
							if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && memorydata.getEntity().equalsIgnoreCase("Utilization")) {
								value = memorydata.getThevalue();
								break;
							}
						}
					} else {
						return;
					}
				} else if ("virtualmemory".equals(indicators)) {
					// �����ڴ�������
					Vector memoryVector = new Vector();
					if (datahashtable.get("virtalmem") != null) {
						memoryVector = (Vector) datahashtable.get("virtalmem");
					}
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int i = 0; i < memoryVector.size(); i++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(i);
							if ("VirtualMemory".equalsIgnoreCase(memorydata.getSubentity()) && memorydata.getEntity().equalsIgnoreCase("Utilization")) {
								value = memorydata.getThevalue();
								break;
							}
						}
					} else {
						return;
					}
				} else if ("swapmemory".equals(indicators)) {
					// �����ڴ�������
					Vector memoryVector = new Vector();
					if (datahashtable.get("swapmem") != null) {
						memoryVector = (Vector) datahashtable.get("swapmem");
					}
					if (memoryVector != null && memoryVector.size() > 0) {
						for (int i = 0; i < memoryVector.size(); i++) {
							MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(i);
							if (memorydata.getEntity().equalsIgnoreCase("Utilization")) {
								value = memorydata.getThevalue();
								break;
							}
						}
					} else {
						return;
					}
				} else if ("iowait".equals(indicators)) {
					// ioƽ���ȴ�ʱ��
					if (datahashtable.get("vmstat") != null) {
						Hashtable avwaiths = (Hashtable) datahashtable.get("vmstat");
						if (avwaiths != null && avwaiths.size() > 0) {
							value = (String) avwaiths.get("iw");
						} else {
							return;
						}
					} else {
						return;
					}
				} else if ("AllInBandwidthUtilHdx".equals(indicators) || "utilhdx".equals(indicators) || "AllOutBandwidthUtilHdx".equals(indicators)) {
					// ���������
					Vector allutilVector = new Vector();
					if (datahashtable.get("allutilhdx") != null) {
						allutilVector = (Vector) datahashtable.get("allutilhdx");
					}

					if (allutilVector != null && allutilVector.size() > 0) {

						for (int i = 0; i < allutilVector.size(); i++) {
							AllUtilHdx allutilhdx = (AllUtilHdx) allutilVector.get(i);
							if (allutilhdx.getEntity().equalsIgnoreCase(indicators)) {
								value = allutilhdx.getThevalue();
								break;
							}

						}

					} else {
						return;
					}
				} else if ("diskperc".equals(indicators)) {
					return;
				} else if ("send".equals(indicators)) {
					// �ʼ����ͷ���
					Vector mailVector = new Vector();
					if (datahashtable.get("mail") != null) {
						mailVector = (Vector) datahashtable.get("mail");
					}
					if (mailVector != null && mailVector.size() > 0) {
						for (int i = 0; i < mailVector.size(); i++) {
							Interfacecollectdata maildata = (Interfacecollectdata) mailVector.get(i);
							if (maildata.getEntity().equalsIgnoreCase("Send")) {
								value = maildata.getThevalue();
								break;
							}
						}
					} else {
						return;
					}
				} else if ("receieve".equalsIgnoreCase(indicators)) {
					// �ʼ����ͷ���
					Vector mailVector = new Vector();
					if (datahashtable.get("mail") != null) {
						mailVector = (Vector) datahashtable.get("mail");
					}
					if (mailVector != null && mailVector.size() > 0) {
						for (int i = 0; i < mailVector.size(); i++) {
							Interfacecollectdata maildata = (Interfacecollectdata) mailVector.get(i);
							if (maildata.getEntity().equalsIgnoreCase("receieve")) {
								value = maildata.getThevalue();
								break;
							}
						}
					} else {
						return;
					}
				} else if ("upload".equals(indicators)) {
					// FTP��TFTP����
					Vector ftpVector = new Vector();
					if ("ftp".equalsIgnoreCase(subtype)) {
						if (datahashtable.get("ftp") != null) {
							ftpVector = (Vector) datahashtable.get("ftp");
						}
						if (ftpVector != null && ftpVector.size() > 0) {
							for (int i = 0; i < ftpVector.size(); i++) {
								Interfacecollectdata ftpdata = (Interfacecollectdata) ftpVector.get(i);
								if (ftpdata.getEntity().equalsIgnoreCase("upload")) {
									value = ftpdata.getThevalue();
									break;
								}
							}
						} else {
							return;
						}
					} else if ("tftp".equalsIgnoreCase(subtype)) {
						if (datahashtable.get("tftp") != null) {
							ftpVector = (Vector) datahashtable.get("tftp");
						}
						if (ftpVector != null && ftpVector.size() > 0) {
							for (int i = 0; i < ftpVector.size(); i++) {
								Interfacecollectdata ftpdata = (Interfacecollectdata) ftpVector.get(i);
								if (ftpdata.getEntity().equalsIgnoreCase("upload")) {
									value = ftpdata.getThevalue();
									break;
								}
							}
						} else {
							return;
						}
					}

				} else if ("download".equalsIgnoreCase(indicators)) {
					// FTP��TFTP����
					Vector ftpVector = new Vector();
					if ("ftp".equalsIgnoreCase(subtype)) {
						if (datahashtable.get("ftp") != null) {
							ftpVector = (Vector) datahashtable.get("ftp");
						}
						if (ftpVector != null && ftpVector.size() > 0) {
							for (int i = 0; i < ftpVector.size(); i++) {
								Interfacecollectdata ftpdata = (Interfacecollectdata) ftpVector.get(i);
								if (ftpdata.getEntity().equalsIgnoreCase("download")) {
									value = ftpdata.getThevalue();
									break;
								}
							}
						} else {
							return;
						}
					} else if ("tftp".equalsIgnoreCase(subtype)) {
						if (datahashtable.get("tftp") != null) {
							ftpVector = (Vector) datahashtable.get("tftp");
						}
						if (ftpVector != null && ftpVector.size() > 0) {
							for (int i = 0; i < ftpVector.size(); i++) {
								Interfacecollectdata ftpdata = (Interfacecollectdata) ftpVector.get(i);
								if (ftpdata.getEntity().equalsIgnoreCase("download")) {
									value = ftpdata.getThevalue();
									break;
								}
							}
						} else {
							return;
						}
					}

				} else if ("socketping".equalsIgnoreCase(indicators)) {
					// SOCKET����
					Vector socketVector = new Vector();
					if (datahashtable.get("socket") != null) {
						socketVector = (Vector) datahashtable.get("socket");
					}
					if (socketVector != null && socketVector.size() > 0) {
						PingCollectEntity socketdata = (PingCollectEntity) socketVector.get(0);
						if (socketdata.getEntity().equalsIgnoreCase("Utilization")) {
							value = socketdata.getThevalue();
						}
					} else {
						return;
					}
				} else if ("webping".equalsIgnoreCase(indicators)) {
					// web����
					Vector webVector = new Vector();
					if (datahashtable.get("url") != null) {
						webVector = (Vector) datahashtable.get("url");
					}
					if (webVector != null && webVector.size() > 0) {
						for (int i = 0; i < webVector.size(); i++) {
							PingCollectEntity webdata = (PingCollectEntity) webVector.get(i);
							if (webdata.getEntity().equalsIgnoreCase("Utilization")) {
								value = webdata.getThevalue();
							}
						}
					} else {
						return;
					}
				} else if ("webresponsetime".equalsIgnoreCase(indicators)) {
					// web����
					Vector webVector = new Vector();
					if (datahashtable.get("url") != null) {
						webVector = (Vector) datahashtable.get("url");
					}
					if (webVector != null && webVector.size() > 0) {
						for (int i = 0; i < webVector.size(); i++) {
							PingCollectEntity webdata = (PingCollectEntity) webVector.get(i);
							if (webdata.getEntity().equalsIgnoreCase("webresponsetime")) {
								value = webdata.getThevalue();
							}
						}
					} else {
						return;
					}
				} else if ("webpagesize".equalsIgnoreCase(indicators)) {
					// WEB����
					Vector webVector = new Vector();
					if (datahashtable.get("url") != null) {
						webVector = (Vector) datahashtable.get("url");
					}
					if (webVector != null && webVector.size() > 0) {
						for (int i = 0; i < webVector.size(); i++) {
							PingCollectEntity webdata = (PingCollectEntity) webVector.get(i);
							if (webdata.getEntity().equalsIgnoreCase("webpagesize")) {
								value = webdata.getThevalue();
							}
						}
					} else {
						return;
					}
				} else if ("webkeyword".equalsIgnoreCase(indicators)) {
					// WEB�Ĺؼ��ּ�����
					Vector webVector = new Vector();
					if (datahashtable.get("url") != null) {
						webVector = (Vector) datahashtable.get("url");
					}
					if (webVector != null && webVector.size() > 0) {
						for (int i = 0; i < webVector.size(); i++) {
							PingCollectEntity webdata = (PingCollectEntity) webVector.get(i);
							if (webdata.getEntity().equalsIgnoreCase("webkeyword")) {
								value = webdata.getThevalue();
							}
						}
					} else {
						return;
					}
				} else if ("droprate".equalsIgnoreCase(indicators)) {
					// ������
					List<PolicyInterface> interfaceList = new ArrayList<PolicyInterface>();
					if (datahashtable.get("policy") != null) {
						interfaceList = (List<PolicyInterface>) datahashtable.get("policy");
					}
					if (interfaceList != null && interfaceList.size() > 0) {
						for (int i = 0; i < interfaceList.size(); i++) {
							PolicyInterface interData = (PolicyInterface) interfaceList.get(i);
							value = interData.getDropRate() + "";
						}
					} else {
						return;
					}
				} else if ("temperature".equals(indicators)) {
					// �¶�
					Vector temperatureVector = new Vector();
					if (datahashtable.get("temperature") != null) {
						temperatureVector = (Vector) datahashtable.get("temperature");
					}
					if (temperatureVector != null && temperatureVector.size() > 0) {
						Interfacecollectdata temperaturedata = (Interfacecollectdata) temperatureVector.get(0);
						value = temperaturedata.getThevalue();
					} else {
						return;
					}
				} else if ("ping".equalsIgnoreCase(indicators)) {
					// DHCP PING����
					Vector dhcpPingVector = new Vector();
					if (datahashtable.get("dhcpping") != null) {
						dhcpPingVector = (Vector) datahashtable.get("dhcpping");
					}
					if (dhcpPingVector != null && dhcpPingVector.size() > 0) {
						PingCollectEntity pingdata = (PingCollectEntity) dhcpPingVector.get(0);
						if (pingdata.getEntity().equalsIgnoreCase("Utilization")) {
							value = pingdata.getThevalue();
						}
					} else {
						return;
					}
				} else {
					return;
				}
				if (value == null) {
					return;
				}
				if (AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())) {
					try {
						double value_int = Double.valueOf(value);
						double Limenvalue2 = Double.valueOf(alarmIndicatorsNode.getLimenvalue2());
						double Limenvalue1 = Double.valueOf(alarmIndicatorsNode.getLimenvalue1());
						double Limenvalue0 = Double.valueOf(alarmIndicatorsNode.getLimenvalue0());
						String level = "";
						String alarmTimes = "";

						// �Ƿ񳬹���ֵ
						boolean result = true;

						if (alarmIndicatorsNode.getCompare() == 0) {
							// ����Ƚ�
							if (value_int <= Limenvalue2) {
								level = "3";
								alarmTimes = alarmIndicatorsNode.getTime2();
								limvalue = alarmIndicatorsNode.getLimenvalue2();
							} else if (value_int <= Limenvalue1) {
								level = "2";
								alarmTimes = alarmIndicatorsNode.getTime1();
								limvalue = alarmIndicatorsNode.getLimenvalue1();
							} else if (value_int <= Limenvalue0) {
								level = "1";
								alarmTimes = alarmIndicatorsNode.getTime0();
								limvalue = alarmIndicatorsNode.getLimenvalue0();
							} else {
								result = false;
							}
						} else {
							// ����Ƚ�
							if (value_int >= Limenvalue2) {
								level = "3";
								alarmTimes = alarmIndicatorsNode.getTime2();
								limvalue = alarmIndicatorsNode.getLimenvalue2();
							} else if (value_int >= Limenvalue1) {
								level = "2";
								alarmTimes = alarmIndicatorsNode.getTime1();
								limvalue = alarmIndicatorsNode.getLimenvalue1();
							} else if (value_int >= Limenvalue0) {
								level = "1";
								alarmTimes = alarmIndicatorsNode.getTime0();
								limvalue = alarmIndicatorsNode.getLimenvalue0();
							} else {
								result = false;
							}
						}

						// �澯��Դ�е� �¼�����
						String num = (String) AlarmResourceCenter.getInstance().getAttribute(String.valueOf(alarmIndicatorsNode.getId()));

						if (num == null || "".equals(num)) {
							num = "0";
						}

						if (!result) {
							// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
							String name = node.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getName();
							if (ShareData.getSendAlarmTimes() != null && ShareData.getSendAlarmTimes().containsKey(name)) {
								SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
								try {
									sendAlarmTimeDao.delete(name);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									sendAlarmTimeDao.close();
								}
							}

							// �����ʱδ�����澯 �� ��������Ϊ 0 ��
							num = "0";
							AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), num);
							// ��ǰָ���޸澯�������ж��ڴ����Ƿ��е�ǰָ��ĸ澯��Ϣ������������澯��Ϣ�����������κδ���
							Hashtable checkEventHash = ShareData.getCheckEventHash();
							if (checkEventHash != null && checkEventHash.size() > 0) {
								if (checkEventHash.containsKey(name)) {
									// ����澯�ѻָ����¼���Ϣ
									EventList eventList = createEvent(alarmIndicatorsNode, vo, value, name);
									EventListDao eventListDao = new EventListDao();
									try {
										eventListDao.save(eventList);
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										eventListDao.close();
									}
									// ɾ��checkEvent�澯��Ϣ
									deleteEvent(node.getId() + "", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(), null);
								}
							}
							return;
						}

						int num_int = 0;
						int alarmTimes_int = 0;
						try {
							num_int = Integer.valueOf(num); // ��ǰ�澯����
							alarmTimes_int = Integer.valueOf(alarmTimes); // ����ĸ澯����
						} catch (RuntimeException e1) {
							e1.printStackTrace();
						}

						if (num_int + 1 >= alarmTimes_int) {
							if (vo instanceof Web) {
								// �����ڴ���WEB��״̬
								Web _web = (Web) PollingEngine.getInstance().getWebByID(node.getId());
								_web.setAlarm(true);
								_web.getAlarmMessage().add(
										alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
												+ alarmIndicatorsNode.getThreshlod_unit());
								// ������֮ǰ�ĸ澯����,������󼶱�
								if (Integer.valueOf(level) > _web.getStatus()) {
									_web.setStatus(Integer.valueOf(level));
								}
								if (Integer.valueOf(level) > _web.getAlarmlevel()) {
									_web.setAlarmlevel(Integer.valueOf(level));
								}
								if ("webresponsetime".equalsIgnoreCase(indicators)) {
									if (_web.getTracertflag() == 2) {
										// ·�ɸ��ٳ�����ֵʱ����,����·�ɸ������ݲɼ�
										TraceRouteExecute tre = new TraceRouteExecute();
										List tracelist = new ArrayList();
										Hashtable tracertHash = new Hashtable();
										try {
											tracelist = tre.executeTracert("tracert -h 10 -w 5000 " + _web.getStr().split("//")[1], _web.getIpAddress());
										} catch (Exception e) {
											e.printStackTrace();
										}
										if (tracelist != null && tracelist.size() > 0) {
											Tracerts tracerts = new Tracerts();
											tracerts.setNodetype("url");
											tracerts.setConfigid(_web.getId());
											tracerts.setDotime(Calendar.getInstance());
											TracertsDao tradao = new TracertsDao();
											try {
												tradao.save(tracerts);
											} catch (Exception e) {

											} finally {
												tradao.close();
											}
											List dolist = new ArrayList();
											TracertsDetail detailvo = null;
											for (int j = 0; j < tracelist.size(); j++) {
												String cont = (String) tracelist.get(j);
												if (cont != null && cont.trim().length() > 0) {
													detailvo = new TracertsDetail();
													detailvo.setDetails(cont);
													detailvo.setNodetype("url");
													detailvo.setTracertsid(tracerts.getId());
													detailvo.setConfigid(node.getId());
													dolist.add(detailvo);
												}
											}
											TracertsDetailDao detaildao = new TracertsDetailDao();
											try {
												detaildao.save(dolist);
											} catch (Exception e) {
												e.printStackTrace();
											} finally {
												detaildao.close();
											}
											tracertHash.put("details", dolist);
											tracertHash.put("tracert", tracerts);
											// �ŵ��ڴ���
											if (ShareData.getAlltracertsdata() != null) {
												ShareData.getAlltracertsdata().put("url:" + _web.getId(), tracertHash);
											} else {
												Hashtable temphash = new Hashtable();
												temphash.put("url:" + _web.getId(), tracertHash);
												ShareData.setAlltracertsdata(temphash);
											}

										}
									}
								} else if ("webping".equalsIgnoreCase(indicators)) {
									// web����
									if (_web.getTracertflag() == 2) {
										// ·�ɸ��ٳ�����ֵʱ����,����·�ɸ������ݲɼ�
										TraceRouteExecute tre = new TraceRouteExecute();
										List tracelist = new ArrayList();
										Hashtable tracertHash = new Hashtable();
										try {
											tracelist = tre.executeTracert("tracert -h 10 -w 5000 " + _web.getStr().split("//")[1], _web.getIpAddress());
										} catch (Exception e) {
											e.printStackTrace();
										}
										if (tracelist != null && tracelist.size() > 0) {
											Tracerts tracerts = new Tracerts();
											tracerts.setNodetype("url");
											tracerts.setConfigid(_web.getId());
											tracerts.setDotime(Calendar.getInstance());
											TracertsDao tradao = new TracertsDao();
											try {
												tradao.save(tracerts);
											} catch (Exception e) {

											} finally {
												tradao.close();
											}
											List dolist = new ArrayList();
											TracertsDetail detailvo = null;
											for (int j = 0; j < tracelist.size(); j++) {
												String cont = (String) tracelist.get(j);
												if (cont != null && cont.trim().length() > 0) {
													detailvo = new TracertsDetail();
													detailvo.setDetails(cont);
													detailvo.setNodetype("url");
													detailvo.setTracertsid(tracerts.getId());
													detailvo.setConfigid(node.getId());
													dolist.add(detailvo);
												}
											}
											TracertsDetailDao detaildao = new TracertsDetailDao();
											try {
												detaildao.save(dolist);
											} catch (Exception e) {
												e.printStackTrace();
											} finally {
												detaildao.close();
											}
											tracertHash.put("details", dolist);
											tracertHash.put("tracert", tracerts);
											// �ŵ��ڴ���
											if (ShareData.getAlltracertsdata() != null) {
												ShareData.getAlltracertsdata().put("url:" + _web.getId(), tracertHash);
											} else {
												Hashtable temphash = new Hashtable();
												temphash.put("url:" + _web.getId(), tracertHash);
												ShareData.setAlltracertsdata(temphash);
											}

										}
									}
								}
								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_web);
								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level), 0);
							} else if (vo instanceof Host) {
								// �¼����������澯���������� ���ɸ澯
								Host host = (Host) PollingEngine.getInstance().getNodeByID(node.getId());
								host.setAlarm(true);
								host.getAlarmMessage().add(
										alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
												+ alarmIndicatorsNode.getThreshlod_unit());
								// ������֮ǰ�ĸ澯����,������󼶱�
								if (Integer.valueOf(level) > host.getStatus()) {
									host.setStatus(Integer.valueOf(level));
								}
								if (Integer.valueOf(level) > host.getAlarmlevel()) {
									host.setAlarmlevel(Integer.valueOf(level));
								}
								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level), 0);
							} else if (vo instanceof DHCP) {
								// �¼����������澯���������� ���ɸ澯
								DHCP dhcp = (DHCP) PollingEngine.getInstance().getDHCPByID(node.getId());
								dhcp.setAlarm(true);
								dhcp.getAlarmMessage().add(
										alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
												+ alarmIndicatorsNode.getThreshlod_unit());
								// ������֮ǰ�ĸ澯����,������󼶱�
								if (Integer.valueOf(level) > dhcp.getStatus()) {
									dhcp.setStatus(Integer.valueOf(level));
								}
								if (Integer.valueOf(level) > dhcp.getAlarmlevel()) {
									dhcp.setAlarmlevel(Integer.valueOf(level));
								}
								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dhcp);
								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level), 0);
							}

						} else {
							num_int = num_int + 1;
							AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), String.valueOf(num_int));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (AlarmConstant.DATATYPE_STRING.equals(alarmIndicatorsNode.getDatatype())) {
					try {
						String value_str = value;
						String Limenvalue2 = alarmIndicatorsNode.getLimenvalue2();
						String Limenvalue1 = alarmIndicatorsNode.getLimenvalue1();
						String Limenvalue0 = alarmIndicatorsNode.getLimenvalue0();

						SysLogger.info(alarmIndicatorsNode.getNodeid() + "==" + alarmIndicatorsNode.getName() + "==" + value_str + "==" + Limenvalue0 + "==" + Limenvalue1 + "=="
								+ Limenvalue2);

						String level = "";
						String alarmTimes = "";

						// �Ƿ񳬹���ֵ
						boolean result = true;

						if (alarmIndicatorsNode.getCompare() == 0) {
							// ����Ƚ�
							// ���ø÷�ֵ�ж�ָ��
							if ("1".equals(alarmIndicatorsNode.getEnabled())) {
								if (value_str.equalsIgnoreCase(Limenvalue2)) {
									level = "3";
									alarmTimes = alarmIndicatorsNode.getTime2();
									limvalue = alarmIndicatorsNode.getLimenvalue2();
								} else if (value_str.equalsIgnoreCase(Limenvalue1)) {
									level = "2";
									alarmTimes = alarmIndicatorsNode.getTime1();
									limvalue = alarmIndicatorsNode.getLimenvalue1();
								} else if (value_str.equalsIgnoreCase(Limenvalue0)) {
									level = "1";
									alarmTimes = alarmIndicatorsNode.getTime0();
									limvalue = alarmIndicatorsNode.getLimenvalue0();
								} else {
									result = false;
								}
							}
						} else {
							// ����Ƚ�
							// ���ø÷�ֵ�ж�ָ��
							if ("1".equals(alarmIndicatorsNode.getEnabled())) {
								if (value_str.equalsIgnoreCase(Limenvalue2)) {
									level = "3";
									alarmTimes = alarmIndicatorsNode.getTime2();
									limvalue = alarmIndicatorsNode.getLimenvalue2();
								} else if (value_str.equalsIgnoreCase(Limenvalue1)) {
									level = "2";
									alarmTimes = alarmIndicatorsNode.getTime1();
									limvalue = alarmIndicatorsNode.getLimenvalue1();
								} else if (value_str.equalsIgnoreCase(Limenvalue0)) {
									level = "1";
									alarmTimes = alarmIndicatorsNode.getTime0();
									limvalue = alarmIndicatorsNode.getLimenvalue0();
								} else {
									result = false;
								}
							}
						}

						// �澯��Դ�е� �¼�����
						String num = (String) AlarmResourceCenter.getInstance().getAttribute(String.valueOf(alarmIndicatorsNode.getId()));

						if (num == null || "".equals(num)) {
							num = "0";
						}

						if (!result) {
							// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
							String name = node.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getName();
							// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
							if (ShareData.getSendAlarmTimes() != null
									&& (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData
											.getSendAlarmTimes().containsKey(name + ":1"))) {

								SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
								try {
									sendAlarmTimeDao.deleteByName(name);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									sendAlarmTimeDao.close();
								}
								if (ShareData.getSendAlarmTimes().containsKey(name + ":3")) {
									ShareData.getSendAlarmTimes().remove(name + ":3");
								}
								if (ShareData.getSendAlarmTimes().containsKey(name + ":2")) {
									ShareData.getSendAlarmTimes().remove(name + ":2");
								}
								if (ShareData.getSendAlarmTimes().containsKey(name + ":1")) {
									ShareData.getSendAlarmTimes().remove(name + ":1");
								}
							}
							// �����ʱδ�����澯 �� ��������Ϊ 0 ��
							num = "0";
							AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), num);

							// ��ǰָ���޸澯�������ж��ڴ����Ƿ��е�ǰָ��ĸ澯��Ϣ������������澯��Ϣ�����������κδ���
							Hashtable checkEventHash = ShareData.getCheckEventHash();
							if (checkEventHash != null && checkEventHash.size() > 0) {
								if (checkEventHash.containsKey(name)) {
									// ����澯�ѻָ����¼���Ϣ
									EventList eventList = createEvent(alarmIndicatorsNode, vo, value, name);
									EventListDao eventListDao = new EventListDao();
									try {
										eventListDao.save(eventList);
									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										eventListDao.close();
									}
									// ɾ��checkEvent�澯��Ϣ
									SysLogger.info(node.getId() + "   " + alarmIndicatorsNode.getType() + "   " + alarmIndicatorsNode.getSubtype() + "   "
											+ alarmIndicatorsNode.getName());
									deleteEvent(node.getId() + "", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(), null);
								}
							}
							return;
						}

						int num_int = 0;
						int alarmTimes_int = 0;
						try {
							num_int = Integer.valueOf(num); // ��ǰ�澯����
							alarmTimes_int = Integer.valueOf(alarmTimes); // ����ĸ澯����
						} catch (RuntimeException e1) {
							e1.printStackTrace();
						}

						if (num_int + 1 >= alarmTimes_int) {
							// �¼����������澯���������� ���ɸ澯
							if (vo instanceof Mail) {
								// �����ڴ���MAIL��״̬
								Mail mail = (Mail) PollingEngine.getInstance().getMailByID(node.getId());
								mail.setAlarm(true);
								mail.getAlarmMessage().add(
										alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
												+ alarmIndicatorsNode.getThreshlod_unit());
								// ������֮ǰ�ĸ澯����,������󼶱�
								if (Integer.valueOf(level) > mail.getStatus()) {
									mail.setStatus(Integer.valueOf(level));
								}
								if (Integer.valueOf(level) > mail.getAlarmlevel()) {
									mail.setAlarmlevel(Integer.valueOf(level));
								}

								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mail);
								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level), 1);
							} else if (vo instanceof Ftp) {
								// �����ڴ���MAIL��״̬
								Ftp ftp = (Ftp) PollingEngine.getInstance().getFtpByID(node.getId());
								ftp.setAlarm(true);
								ftp.getAlarmMessage().add(
										alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
												+ alarmIndicatorsNode.getThreshlod_unit());
								// ������֮ǰ�ĸ澯����,������󼶱�
								if (Integer.valueOf(level) > ftp.getStatus()) {
									ftp.setStatus(Integer.valueOf(level));
								}
								if (Integer.valueOf(level) > ftp.getAlarmlevel()) {
									ftp.setAlarmlevel(Integer.valueOf(level));
								}

								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(ftp);
								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level), 1);
							} else if (vo instanceof TFtp) {
								// �����ڴ���TFTP��״̬
								TFtp tftp = (TFtp) PollingEngine.getInstance().getTftpByID(node.getId());
								tftp.setAlarm(true);
								tftp.getAlarmMessage().add(
										alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
												+ alarmIndicatorsNode.getThreshlod_unit());
								// ������֮ǰ�ĸ澯����,������󼶱�
								if (Integer.valueOf(level) > tftp.getStatus()) {
									tftp.setStatus(Integer.valueOf(level));
								}
								if (Integer.valueOf(level) > tftp.getAlarmlevel()) {
									tftp.setAlarmlevel(Integer.valueOf(level));
								}

								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(tftp);
								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level), 1);
							} else if (vo instanceof com.afunms.polling.node.SocketService) {
								// �����ڴ���SOCKET��״̬
								com.afunms.polling.node.SocketService _tnode = (com.afunms.polling.node.SocketService) PollingEngine.getInstance().getSocketByID(node.getId());
								_tnode.setAlarm(true);
								_tnode.getAlarmMessage().add(
										alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
												+ alarmIndicatorsNode.getThreshlod_unit());
								// ������֮ǰ�ĸ澯����,������󼶱�
								if (Integer.valueOf(level) > _tnode.getStatus()) {
									_tnode.setStatus(Integer.valueOf(level));
								}
								if (Integer.valueOf(level) > _tnode.getAlarmlevel()) {
									_tnode.setAlarmlevel(Integer.valueOf(level));
								}

								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_tnode);
								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level), 1);
							} else if (vo instanceof Web) {
								// �����ڴ���WEB��״̬
								Web _web = (Web) PollingEngine.getInstance().getWebByID(node.getId());
								_web.setAlarm(true);
								_web.getAlarmMessage().add(
										alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit() + " ��ֵΪ:" + limvalue
												+ alarmIndicatorsNode.getThreshlod_unit());
								// ������֮ǰ�ĸ澯����,������󼶱�
								if (Integer.valueOf(level) > _web.getStatus()) {
									_web.setStatus(Integer.valueOf(level));
								}
								if (Integer.valueOf(level) > _web.getAlarmlevel()) {
									_web.setAlarmlevel(Integer.valueOf(level));
								}

								if ("webresponsetime".equalsIgnoreCase(indicators)) {
									// ����URL���ӵ�·�ɸ��ٹ���
									TraceRouteExecute tre = new TraceRouteExecute();
									@SuppressWarnings("unused")
									List tracelist = new ArrayList();
									try {
										tracelist = tre.executeTracert("tracert -h 10 -w 5000 " + _web.getStr(), _web.getIpAddress());
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else if ("webping".equalsIgnoreCase(indicators)) {
									// web����
									// ����URL���ӵ�·�ɸ��ٹ���
									TraceRouteExecute tre = new TraceRouteExecute();
									@SuppressWarnings("unused")
									List tracelist = new ArrayList();
									try {
										tracelist = tre.executeTracert("tracert -h 10 -w 5000 " + _web.getStr(), _web.getIpAddress());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}

								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(_web);
								sendAlarm(nodeDTO, alarmIndicatorsNode, value, Integer.valueOf(level), 1);
							}
						} else {
							num_int = num_int + 1;
							AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()), String.valueOf(num_int));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} else if ("0".equals(alarmIndicatorsNode.getEnabled())) {
				if (AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())) {
					try {
						String name = node.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getName();
						// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
						if (ShareData.getSendAlarmTimes() != null
								&& (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData
										.getSendAlarmTimes().containsKey(name + ":1"))) {

							SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
							try {
								sendAlarmTimeDao.deleteByName(name);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								sendAlarmTimeDao.close();
							}
							if (ShareData.getSendAlarmTimes().containsKey(name + ":3")) {
								ShareData.getSendAlarmTimes().remove(name + ":3");
							}
							if (ShareData.getSendAlarmTimes().containsKey(name + ":2")) {
								ShareData.getSendAlarmTimes().remove(name + ":2");
							}
							if (ShareData.getSendAlarmTimes().containsKey(name + ":1")) {
								ShareData.getSendAlarmTimes().remove(name + ":1");
							}
						}
						// ��ǰָ���޸澯�������ж��ڴ����Ƿ��е�ǰָ��ĸ澯��Ϣ������������澯��Ϣ�����������κδ���
						Hashtable checkEventHash = ShareData.getCheckEventHash();
						if (checkEventHash != null && checkEventHash.size() > 0) {
							if (checkEventHash.containsKey(name)) {
								// ɾ��checkEvent�澯��Ϣ
								deleteEvent(node.getId() + "", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(), null);
							}
						}
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (AlarmConstant.DATATYPE_STRING.equals(alarmIndicatorsNode.getDatatype())) {
					try {
						// δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
						String name = node.getId() + ":" + alarmIndicatorsNode.getType() + ":" + alarmIndicatorsNode.getName();
						if (ShareData.getSendAlarmTimes() != null
								&& (ShareData.getSendAlarmTimes().containsKey(name + ":3") || ShareData.getSendAlarmTimes().containsKey(name + ":2") || ShareData
										.getSendAlarmTimes().containsKey(name + ":1"))) {

							SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
							try {
								sendAlarmTimeDao.deleteByName(name);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								sendAlarmTimeDao.close();
							}
							if (ShareData.getSendAlarmTimes().containsKey(name + ":3")) {
								ShareData.getSendAlarmTimes().remove(name + ":3");
							}
							if (ShareData.getSendAlarmTimes().containsKey(name + ":2")) {
								ShareData.getSendAlarmTimes().remove(name + ":2");
							}
							if (ShareData.getSendAlarmTimes().containsKey(name + ":1")) {
								ShareData.getSendAlarmTimes().remove(name + ":1");
							}
						}
						// ��ǰָ���޸澯�������ж��ڴ����Ƿ��е�ǰָ��ĸ澯��Ϣ������������澯��Ϣ�����������κδ���
						Hashtable checkEventHash = ShareData.getCheckEventHash();
						if (checkEventHash != null && checkEventHash.size() > 0) {
							if (checkEventHash.containsKey(name)) {
								deleteEvent(node.getId() + "", alarmIndicatorsNode.getType(), alarmIndicatorsNode.getSubtype(), alarmIndicatorsNode.getName(), null);
							}
						}
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
