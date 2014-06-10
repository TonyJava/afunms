package com.afunms.alarm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import com.afunms.alarm.model.AlarmThresholdDefault;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

@SuppressWarnings("unchecked")
public class AlarmThresholdDefaultDao extends BaseDao implements DaoInterface {
	public AlarmThresholdDefaultDao() {
		super("nms_alarm_threshold_default");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		AlarmThresholdDefault alarmThresholdDefault = new AlarmThresholdDefault();
		try {
			alarmThresholdDefault.setId(rs.getInt("id"));
			alarmThresholdDefault.setType(rs.getString("type"));
			alarmThresholdDefault.setSubtype(rs.getString("subtype"));
			alarmThresholdDefault.setIndicators(rs.getString("indicators"));
			alarmThresholdDefault.setDatatype(rs.getString("datatype"));
			alarmThresholdDefault.setLevel(rs.getString("level"));
			alarmThresholdDefault.setAlarmTimes(rs.getString("alarmTimes"));
			alarmThresholdDefault.setThresholdValue(rs.getString("thresholdValue"));
			alarmThresholdDefault.setThresholdUnit(rs.getString("thresholdUnit"));
			alarmThresholdDefault.setIsAlarm(rs.getString("isAlarm"));
			alarmThresholdDefault.setIsSendSMS(rs.getString("isSendSMS"));
			alarmThresholdDefault.setDescription(rs.getString("description"));
			alarmThresholdDefault.setBak(rs.getString("bak"));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return alarmThresholdDefault;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean saveAlarmThresholdDefaultList(List alarmThresholdDefaultList) {
		try {
			Iterator iterator = alarmThresholdDefaultList.iterator();
			while (iterator.hasNext()) {
				AlarmThresholdDefault alarmThresholdDefault = (AlarmThresholdDefault) iterator.next();
				StringBuffer sql = new StringBuffer();
				sql
						.append("insert into nms_alarm_threshold_default(type, subtype, indicators, datatype, level, alarmTimes, thresholdValue, thresholdUnit, isAlarm, isSendSMS, description, bak) values('");
				sql.append(alarmThresholdDefault.getType());
				sql.append("','");
				sql.append(alarmThresholdDefault.getSubtype());
				sql.append("','");
				sql.append(alarmThresholdDefault.getIndicators());
				sql.append("','");
				sql.append(alarmThresholdDefault.getDatatype());
				sql.append("','");
				sql.append(alarmThresholdDefault.getLevel());
				sql.append("','");
				sql.append(alarmThresholdDefault.getAlarmTimes());
				sql.append("','");
				sql.append(alarmThresholdDefault.getThresholdValue());
				sql.append("','");
				sql.append(alarmThresholdDefault.getThresholdUnit());
				sql.append("','");
				sql.append(alarmThresholdDefault.getIsAlarm());
				sql.append("','");
				sql.append(alarmThresholdDefault.getIsSendSMS());
				sql.append("','");
				sql.append(alarmThresholdDefault.getDescription());
				sql.append("','");
				sql.append(alarmThresholdDefault.getBak());
				sql.append("')");
				try {
					conn.executeUpdate(sql.toString());
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public boolean update(BaseVo vo) {
		return false;
	}

}