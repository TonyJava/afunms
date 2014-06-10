package com.afunms.common.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.afunms.application.dao.NodeIndicatorAlarmDao;
import com.afunms.application.dao.PerformancePanelDao;
import com.afunms.application.model.NodeIndicatorAlarm;
import com.afunms.event.model.EventList;
import com.afunms.topology.model.HostNode;
import com.afunms.util.DataGate;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version ����ʱ�䣺Aug 11, 2011 2:38:59 PM ��˵��:�豸�澯��Ϣͳ�ƹ�����
 */
public class NodeAlarmUtil {

	/**
	 * @param obj
	 *            �����豸��ɾ�������豸ָ��������е�����
	 * @return
	 */
	public synchronized static boolean deleteByDeviceIdAndDeviceType(Object obj) {
		boolean flag = false;
		if (obj == null) {
			return flag;
		}
		String deviceType = null;
		String deviceId = null;
		if (obj instanceof HostNode) {
			HostNode host = (HostNode) obj;
			if ((host.getCategory() == 4)) {
				deviceType = "host";
			}
			if (host.getCategory() < 4 || host.getCategory() == 7 || host.getCategory() == 8 || host.getCategory() == 9) {
				deviceType = "net";
			}
			deviceId = host.getId() + "";
		}
		if (deviceType != null && deviceId != null) {
			flag = deleteByDeviceIdAndDeviceType(deviceId, deviceType);
		}
		return flag;
	}

	/**
	 * �������ͺ�IDɾ����������
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @return
	 */
	public synchronized static boolean deleteByDeviceIdAndDeviceType(String deviceId, String deviceType) {
		boolean flag = false;
		NodeIndicatorAlarmDao nodeIndicatorAlarmDao = new NodeIndicatorAlarmDao();
		PerformancePanelDao performancePanelDao = new PerformancePanelDao();
		try {
			nodeIndicatorAlarmDao.deleteByIdAndType(deviceId, deviceType);
			performancePanelDao.deleteByIdAndType(deviceId, deviceType);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodeIndicatorAlarmDao.close();
			performancePanelDao.close();
		}
		return flag;
	}

	/**
	 * ���澯��Ϣ��ӵ������������ݿ���
	 * 
	 * @param eventList
	 *            �¼�����
	 * @param alarmIndicatorName
	 *            �澯ָ������
	 */
	public synchronized static void saveNodeAlarmInfo(EventList eventList, String alarmIndicatorName) {
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			NodeIndicatorAlarm nodeIndicatorAlarm = new NodeIndicatorAlarm();
			nodeIndicatorAlarm.setAlarmDesc(eventList.getContent());
			if (eventList.getLevel1() == null) {
				nodeIndicatorAlarm.setAlarmLevel("");
			} else {
				nodeIndicatorAlarm.setAlarmLevel(eventList.getLevel1().intValue() + "");
			}
			nodeIndicatorAlarm.setDeviceId(eventList.getNodeid() + "");
			nodeIndicatorAlarm.setDeviceType(eventList.getSubtype());
			nodeIndicatorAlarm.setIndicatorName(alarmIndicatorName);
			StringBuffer sql = new StringBuffer();
			sql.append("select * from node_indicator_alarm where deviceId = '");
			sql.append(nodeIndicatorAlarm.getDeviceId());
			sql.append("' and deviceType = '");
			sql.append(nodeIndicatorAlarm.getDeviceType());
			sql.append("' and indicatorName = '");
			sql.append(nodeIndicatorAlarm.getIndicatorName());
			sql.append("'");
			boolean flag = false;
			rs = stmt.executeQuery(sql.toString());
			if (rs != null && rs.next()) {
				flag = true;
			}
			// ��������Ϣ���澯��Ϣ���ݿ�
			if (flag) {
				sql = new StringBuffer();
				sql.append("update node_indicator_alarm set alarmLevel = '");
				sql.append(nodeIndicatorAlarm.getAlarmLevel());
				sql.append("', alarmDesc = '");
				sql.append(nodeIndicatorAlarm.getAlarmDesc());
				sql.append("' where deviceId = '");
				sql.append(nodeIndicatorAlarm.getDeviceId());
				sql.append("' and indicatorName = '");
				sql.append(nodeIndicatorAlarm.getIndicatorName());
				sql.append("'");
				stmt.executeUpdate(sql.toString());
			} else {
				NodeIndicatorAlarm performancePanelIndicatorsModel = nodeIndicatorAlarm;
				sql = new StringBuffer();
				sql.append("insert into node_indicator_alarm (deviceId,deviceType,indicatorName,alarmLevel,alarmDesc) values ('");
				sql.append(performancePanelIndicatorsModel.getDeviceId());
				sql.append("','");
				sql.append(performancePanelIndicatorsModel.getDeviceType());
				sql.append("','");
				sql.append(performancePanelIndicatorsModel.getIndicatorName());
				sql.append("','");
				sql.append(performancePanelIndicatorsModel.getAlarmLevel());
				sql.append("','");
				sql.append(performancePanelIndicatorsModel.getAlarmDesc());
				sql.append("')");
				stmt.executeUpdate(sql.toString());
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
	 * ɾ��������壬�澯��Ϣ�б��еĸ�ָ��澯��¼
	 * 
	 * @param deviceId
	 *            �豸ID
	 * @param deviceType
	 *            �豸����
	 * @param indicatorName
	 *            ָ������
	 * @return
	 */
	public synchronized boolean deleteByDeviceIdAndDeviceTypeAndIndicatorName(String deviceId, String deviceType, String indicatorName) {
		boolean flag = false;
		NodeIndicatorAlarmDao nodeIndicatorAlarmDao = new NodeIndicatorAlarmDao();
		try {
			nodeIndicatorAlarmDao.deleteByIdAndTypeAndIndicatorName(deviceId, deviceType, indicatorName);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodeIndicatorAlarmDao.close();
		}
		return flag;
	}
}
