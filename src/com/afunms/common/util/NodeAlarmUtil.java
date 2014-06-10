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
 * @version 创建时间：Aug 11, 2011 2:38:59 PM 类说明:设备告警信息统计工具类
 */
public class NodeAlarmUtil {

	/**
	 * @param obj
	 *            根据设备，删除其在设备指标监控面板中的数据
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
	 * 根据类型和ID删除多条数据
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
	 * 将告警信息添加到性能面板的数据库中
	 * 
	 * @param eventList
	 *            事件对象
	 * @param alarmIndicatorName
	 *            告警指标名称
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
			// 增加新信息到告警信息数据库
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
	 * 删除性能面板，告警信息列表中的该指标告警记录
	 * 
	 * @param deviceId
	 *            设备ID
	 * @param deviceType
	 *            设备类型
	 * @param indicatorName
	 *            指标名称
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
