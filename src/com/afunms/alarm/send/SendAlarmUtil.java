package com.afunms.alarm.send;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmPort;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.NodeAlarmUtil;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.CheckEvent;
import com.afunms.event.model.EventList;
import com.afunms.util.DataGate;

public class SendAlarmUtil {

	public void sendAlarm(CheckEvent checkEvent, EventList eventList, AlarmIndicatorsNode alarmIndicatorsNode) {
		String alarmWayId = getAlarmWayId(eventList, alarmIndicatorsNode);
		// 默认情况下,需要生成系统事件
		SendPageAlarm sendPageAlarm = new SendPageAlarm();
		//系统告警数据入库
		sendPageAlarm.sendAlarm(eventList, "");
		NodeAlarmUtil.saveNodeAlarmInfo(eventList, alarmIndicatorsNode.getName());// 性能面板展现数据
		if (alarmWayId != null) {
			String alarmWayIdArray[] = alarmWayId.split(",");
			if (alarmWayIdArray != null && alarmWayIdArray.length > 0) {
				for (int j = 0; j < alarmWayIdArray.length; j++) {
					AlarmWay alarmWay = null;
					if (alarmWayIdArray[j] != null && alarmWayIdArray[j].trim().length() > 0) {
						try {
							alarmWay = getAlarmWay(alarmWayIdArray[j]);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (alarmWay != null) {
							List<AlarmWayDetail> list = getAlarmWayDetail(alarmWayIdArray[j]);
							if (list != null && list.size() > 0) {
								SendAlarmFilter sendAlarmFilter = new SendAlarmFilter();
								for (int i = 0; i < list.size(); i++) {
									AlarmWayDetail alarmWayDetail = list.get(i);
									boolean result = sendAlarmFilter.isSendAlarm(checkEvent, alarmIndicatorsNode, alarmWay, alarmWayDetail);
									if (result) {
										try {
											SendAlarmDispatcher.sendAlarm(eventList, alarmWayDetail);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * wupinlong add 文件监控发送告警
	 * 
	 * @param checkEvent
	 * @param eventList
	 * @param alarmIndicatorsNode
	 * @param alarmWayId
	 */
	public void sendAlarm(CheckEvent checkEvent, EventList eventList, AlarmIndicatorsNode alarmIndicatorsNode, String alarmWayId) {
		if (alarmWayId != null) {
			String alarmWayIdArray[] = alarmWayId.split(",");
			if (alarmWayIdArray != null && alarmWayIdArray.length > 0) {
				for (int j = 0; j < alarmWayIdArray.length; j++) {
					AlarmWay alarmWay = getAlarmWay(alarmWayIdArray[j]);
					if (alarmWay != null) {
						List<AlarmWayDetail> list = getAlarmWayDetail(alarmWayIdArray[j]);
						if (list != null && list.size() > 0) {
							SendAlarmFilter sendAlarmFilter = new SendAlarmFilter();
							for (int i = 0; i < list.size(); i++) {
								AlarmWayDetail alarmWayDetail = list.get(i);
								boolean result = sendAlarmFilter.isSendAlarm(checkEvent, alarmIndicatorsNode, alarmWay, alarmWayDetail);
								if (result) {
									SendAlarmDispatcher.sendAlarm(eventList, alarmWayDetail);
								}
							}
						}
					}
				}
			}
		}
		SendPageAlarm sendPageAlarm = new SendPageAlarm();
		sendPageAlarm.sendAlarm(eventList, "");
		// 默认情况下,都是向向声音告警表里写数据
		// 向声音告警表里写数据
		AlarmInfo alarminfo = new AlarmInfo();
		alarminfo.setContent(eventList.getContent());
		alarminfo.setIpaddress(eventList.getEventlocation());
		alarminfo.setLevel1(new Integer(2));
		alarminfo.setRecordtime(Calendar.getInstance());
		alarminfo.setType("");
		AlarmInfoDao alarmdao = new AlarmInfoDao();
		try {
			alarmdao.save(alarminfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmdao.close();
		}
	}

	public void sendPortAlarm(CheckEvent checkEvent, EventList eventList, int alarmLevel, AlarmPort portNode) {
		String alarmWayId = "";
		if (alarmLevel == 1) {
			alarmWayId = portNode.getWayin1();
		} else if (alarmLevel == 2) {
			alarmWayId = portNode.getWayin2();
		} else if (alarmLevel == 3) {
			alarmWayId = portNode.getWayin3();
		} else if (alarmLevel == 4) {
			alarmWayId = portNode.getWayout1();
		} else if (alarmLevel == 5) {
			alarmWayId = portNode.getWayout2();
		} else if (alarmLevel == 6) {
			alarmWayId = portNode.getWayout3();
		}
		AlarmWay alarmWay = null;
		if (alarmWayId != null && alarmWayId.trim().length() > 0) {
			try {
				alarmWay = getAlarmWay(alarmWayId);
			} catch (Exception e) {

			}
		}
		// 默认情况下,都是向向声音告警表里写数据
		// 向声音告警表里写数据
		AlarmInfo alarminfo = new AlarmInfo();
		alarminfo.setContent(eventList.getContent());
		alarminfo.setIpaddress(eventList.getEventlocation());
		alarminfo.setLevel1(new Integer(2));
		alarminfo.setRecordtime(Calendar.getInstance());
		alarminfo.setType("");
		AlarmInfoDao alarmdao = new AlarmInfoDao();
		try {
			alarmdao.save(alarminfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmdao.close();
		}
		AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
		alarmIndicatorsNode.setId(portNode.getId());
		alarmIndicatorsNode.setNodeid(portNode.getId() + "");
		alarmIndicatorsNode.setType(portNode.getType());
		alarmIndicatorsNode.setSubtype(portNode.getSubtype());
		alarmIndicatorsNode.setName(portNode.getName());
		if (alarmWay == null) {
			// 默认情况下,需要生成系统事件
			AlarmWayDetail alarmWayDetail = null;
			SendPageAlarm sendPageAlarm = new SendPageAlarm();
			sendPageAlarm.sendAlarm(eventList, alarmWayDetail);
		} else {
			if ("1".equals(alarmWay.getIsPageAlarm())) {
				AlarmWayDetail alarmWayDetail = null;
				SendPageAlarm sendPageAlarm = new SendPageAlarm();
				sendPageAlarm.sendAlarm(eventList, alarmWayDetail);
			}
			List<AlarmWayDetail> list = getAlarmWayDetail(alarmWayId);
			if (list == null || list.size() == 0) {
				// 无告警详细配置,不告警
			} else {
				SendAlarmFilter sendAlarmFilter = new SendAlarmFilter();
				for (int i = 0; i < list.size(); i++) {
					AlarmWayDetail alarmWayDetail = list.get(i);
					boolean result = sendAlarmFilter.isSendAlarm(checkEvent, alarmIndicatorsNode, alarmWay, alarmWayDetail);
					if (result) {
						SendAlarmDispatcher.sendAlarm(eventList, alarmWayDetail);
					}
				}
			}
		}
	}

	/**
	 * 发送告警,只需要单次发送告警信息,而不需要进行判断有几次告警产生,只要产生告警就发送则调用此方法
	 * 
	 * @param alarmWayId
	 * @param eventList
	 * @return
	 */
	public void sendAlarmNoIndicator(String alarmWayId, EventList eventList) {
		AlarmWay alarmWay = null;
		if (alarmWayId != null && alarmWayId.trim().length() > 0) {
			try {
				alarmWay = getAlarmWay(alarmWayId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 默认情况下,都是向向声音告警表里写数据
		// 向声音告警表里写数据
		AlarmInfo alarminfo = new AlarmInfo();
		alarminfo.setContent(eventList.getContent());
		alarminfo.setIpaddress(eventList.getEventlocation());
		alarminfo.setLevel1(new Integer(2));
		alarminfo.setRecordtime(Calendar.getInstance());
		alarminfo.setType("");
		AlarmInfoDao alarmdao = new AlarmInfoDao();
		try {
			alarmdao.save(alarminfo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmdao.close();
		}

		if (alarmWay == null) {
			// 默认情况下,需要生成系统事件
			AlarmWayDetail alarmWayDetail = null;
			SendPageAlarm sendPageAlarm = new SendPageAlarm();
			sendPageAlarm.sendAlarm(eventList, alarmWayDetail);
		} else {
			if ("1".equals(alarmWay.getIsPageAlarm())) {
				AlarmWayDetail alarmWayDetail = null;
				SendPageAlarm sendPageAlarm = new SendPageAlarm();
				sendPageAlarm.sendAlarm(eventList, alarmWayDetail);
			}
			List<AlarmWayDetail> list = getAlarmWayDetail(alarmWayId);
			if (list == null || list.size() == 0) {
				// 无告警详细配置,不告警
			} else {
				for (int i = 0; i < list.size(); i++) {
					AlarmWayDetail alarmWayDetail = list.get(i);
					SendAlarmDispatcher.sendAlarm(eventList, alarmWayDetail);
				}
			}
		}
	}

	/**
	 * 获取其告警方式的 id
	 * 
	 * @param eventList
	 * @param alarmIndicators
	 * @return
	 */
	private String getAlarmWayId(EventList eventList, AlarmIndicatorsNode alarmIndicatorsNode) {
		String alarmWayId = "";
		if (eventList.getLevel1() == 1) {
			alarmWayId = alarmIndicatorsNode.getWay0();
		} else if (eventList.getLevel1() == 2) {
			alarmWayId = alarmIndicatorsNode.getWay1();
		} else if (eventList.getLevel1() == 3) {
			alarmWayId = alarmIndicatorsNode.getWay2();
		}
		return alarmWayId;
	}

	/**
	 * 获取其告警方式的 id
	 * 
	 * @param eventList
	 * @param alarmIndicators
	 * @return
	 */
	private AlarmWay getAlarmWay(String alarmWayId) {
		AlarmWay alarmWay = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from nms_alarm_way where id=" + alarmWayId);
			if (rs.next()) {
				alarmWay = (AlarmWay) loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				DataGate.freeCon(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return alarmWay;
	}

	private BaseVo loadFromRS(ResultSet rs) {
		AlarmWay alarmWay = new AlarmWay();
		try {
			alarmWay.setId(rs.getInt("id"));
			alarmWay.setName(rs.getString("name"));
			alarmWay.setDescription(rs.getString("description"));
			alarmWay.setIsDefault(rs.getString("is_default"));
			alarmWay.setIsPageAlarm(rs.getString("is_page_alarm"));
			alarmWay.setIsSoundAlarm(rs.getString("is_sound_alarm"));
			alarmWay.setIsMailAlarm(rs.getString("is_mail_alarm"));
			alarmWay.setIsPhoneAlarm(rs.getString("is_phone_alarm"));
			alarmWay.setIsSMSAlarm(rs.getString("is_sms_alarm"));
			alarmWay.setIsDesktopAlarm(rs.getString("is_desktop_alarm"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alarmWay;
	}

	/**
	 * 获取其告警的方式的详细配置
	 * 
	 * @param eventList
	 * @param alarmIndicators
	 * @return
	 */
	private List<AlarmWayDetail> getAlarmWayDetail(String alarmWayId) {
		List<AlarmWayDetail> list = new ArrayList<AlarmWayDetail>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			String sql = "select * from nms_alarm_way_detail where alarm_way_id='" + alarmWayId + "'";
			rs = stmt.executeQuery(sql);
			if (rs == null)
				return null;
			while (rs.next()) {
				list.add((AlarmWayDetail) loadFromRS1(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				DataGate.freeCon(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	private BaseVo loadFromRS1(ResultSet rs) {
		AlarmWayDetail alarmWayDetail = new AlarmWayDetail();
		try {
			alarmWayDetail.setId(rs.getInt("id"));
			alarmWayDetail.setAlarmWayId(rs.getString("alarm_way_id"));
			alarmWayDetail.setAlarmCategory(rs.getString("alarm_category"));
			alarmWayDetail.setDateType(rs.getString("date_type"));
			alarmWayDetail.setSendTimes(rs.getString("send_times"));
			alarmWayDetail.setStartDate(rs.getString("start_date"));
			alarmWayDetail.setEndDate(rs.getString("end_date"));
			alarmWayDetail.setStartTime(rs.getString("start_time"));
			alarmWayDetail.setEndTime(rs.getString("end_time"));
			alarmWayDetail.setUserIds(rs.getString("user_ids"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alarmWayDetail;
	}
}
