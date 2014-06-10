package com.afunms.polling.snmp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class MySqlDataCollector {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Calendar cal = Calendar.getInstance();

	@SuppressWarnings("rawtypes")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicator) {
		Hashtable gatherHash = new Hashtable();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		try {
			// ��ȡ�����õ�MYSQL���б�����ָ��
			monitorItemList = indicatorsdao.getByInterval(1, "db", "mysql");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
		}
		int id = Integer.parseInt(nodeGatherIndicator.getNodeid());
		DBDao dbDao = new DBDao();
		try {
			Hashtable monitorValue = new Hashtable();
			DBNode dbNode = (DBNode) PollingEngine.getInstance().getDbByID(id);
			if (null == dbNode) {
				return null;
			}
			dbNode.setAlarm(false);
			dbNode.setStatus(0);
			dbNode.setLastTime(sdf.format(cal.getTime()));
			dbNode.getAlarmMessage().clear();

			String serverIp = dbNode.getIpAddress();
			String userName = dbNode.getUser();
			String passWord = EncryptUtil.decode(dbNode.getPassword());
			String dbName = dbNode.getDbName();
			String port=dbNode.getPort();
			String linkStatue = "0";
			boolean mysqlIsOK = false;
			// JDBC�ɼ���ʽ
			try {
				mysqlIsOK = dbDao.getMySqlIsOk(serverIp,port, userName, passWord, dbName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!mysqlIsOK) {
				dbNode = (DBNode) PollingEngine.getInstance().getDbByID(id);
				dbNode.setAlarm(true);
				dbNode.setStatus(3);
				dbNode.getAlarmMessage().add("����ֹͣ");
			} else {
				linkStatue = "100";
				Hashtable returnValue = new Hashtable();
				try {
					returnValue = dbDao.getMYSQLData(serverIp,port, userName, passWord, dbName, gatherHash);
				} catch (Exception e) {
					e.printStackTrace();
				}
				monitorValue.put(dbName, returnValue);
			}

			PingCollectEntity hostdata = new PingCollectEntity();
			hostdata.setId(Long.parseLong(id + ""));
			hostdata.setIpaddress(serverIp);
			Calendar date = Calendar.getInstance();
			hostdata.setCollecttime(date);
			hostdata.setCategory("MYPing");
			hostdata.setEntity("Utilization");
			hostdata.setSubentity("ConnectUtilization");
			hostdata.setRestype("dynamic");
			hostdata.setUnit("%");
			hostdata.setThevalue(linkStatue);

			try {
				dbDao.createHostData(hostdata);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (linkStatue.equals("100")) {
				monitorValue.put("runningflag", "��������");
			} else {
				monitorValue.put("runningflag", "����ֹͣ");
			}

			if (monitorValue != null && monitorValue.size() > 0) {
				ShareData.setMySqlmonitordata(serverIp, monitorValue);
			}

			String hex = IpTranslation.formIpToHex(serverIp);
			String sip = hex + ":" + id;
			if (linkStatue.equals("100")) {
				updateData(dbNode, ShareData.getMySqlmonitordata());
				// ����IP��ַ���ԭ�е���Ϣ
				dbDao.clearTableData("nms_mysqlinfo", sip);
				// ����ɼ���Mysql������Ϣ
				dbDao.addMysql_nmsinfo(sip, monitorValue, dbName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null)
				dbDao.close();
		}
		
		return null;
	}

	/**
	 * ���¸澯��Ϣ HONGLI
	 * 
	 * @param vo
	 *            ���ݿ�ʵ��
	 * @param collectingData
	 *            ���ݿ�ʵ���еĸ���������Ϣ
	 */
	public void updateData(Object vo, Object collectingData) {
		DBNode mysql = (DBNode) vo;
		Hashtable monitorValueHashtable = (Hashtable) ((Hashtable) collectingData).get(mysql.getIpAddress());
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mysql);
		String[] dbs = mysql.getDbName().split(",");
		for (int k = 0; k < dbs.length; k++) {
			Hashtable mysqldHashtable = (Hashtable) monitorValueHashtable.get(dbs[k]);
			Vector val = (Vector) mysqldHashtable.get("Val");// ���ݿ���ϸ��Ϣ
			java.util.Iterator iterator = val.iterator();// ����

			String maxUsedConnections = ""; // ��������Ӧ�����������
			String threadsConnected = "";// ��ǰ�򿪵����ӵ�����
			String threadsCreated = "";// ���������������ӵ��߳���
			String openTables = "";// ��ǰ�򿪵ı������
			while (iterator.hasNext()) {
				Hashtable tempHashtable = (Hashtable) iterator.next();
				String variableName = (String) tempHashtable.get("variable_name");
				if (("Max_used_connections").equals(variableName)) {
					maxUsedConnections = (String) tempHashtable.get("value");
				}
				if (("Threads_connected").equals(variableName)) {
					threadsConnected = (String) tempHashtable.get("value");
				}
				if (("Threads_created").equals(variableName)) {
					threadsCreated = (String) tempHashtable.get("value");
				}
				if (("Open_tables").equals(variableName)) {
					openTables = (String) tempHashtable.get("value");
				}
			}
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(mysql.getId()), AlarmConstant.TYPE_DB, "mysql");// ��ȡ�ɼ�ָ���б�
			for (int i = 0; i < list.size(); i++) {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
				if ("1".equals(alarmIndicatorsNode.getEnabled())) {
					String indicators = alarmIndicatorsNode.getName();
					String value = "";// value ��ָʵ�����ݿ��е�ֵ���� ������������ HONGLI
					if ("max_used_connections".equals(indicators)) {
						value = maxUsedConnections;// key ��DBDao
					} else if ("threads_connected".equals(indicators)) {
						value = threadsConnected;
					} else if ("threads_created".equals(indicators)) {
						value = threadsCreated;
					} else if ("open_tables".equals(indicators)) {
						value = openTables;
					} else {
						continue;
					}
					if (value == null) {
						continue;
					}
					checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, value, null);
				}

			}
		}
	}
}
