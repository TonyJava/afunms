package com.afunms.alarm.send;

import java.text.SimpleDateFormat;

import montnets.SmsDao;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.SysLogger;
import com.afunms.event.model.EventList;
import com.afunms.event.model.SendSmsConfig;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.database.SqlServerDBManager;

public class SendSMSAlarm implements SendAlarm{
	
	public void sendAlarm(EventList eventList,AlarmWayDetail alarmWayDetail){
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SysLogger.info(">>>>> 发送短信告警");
		//向客户端写告警信息
		String[] ids = alarmWayDetail.getUserIds().split(",");
		if (ids != null && ids.length > 0) {
			for (int j = 0; j < ids.length; j++) {
				String oid = ids[j];
				User op = null;
				UserDao userdao = new UserDao();    
				try {
					op = (User) userdao.findByID(oid);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					userdao.close();
				}
				if (op == null) {
					continue;
				}
				//开始发送短信接口
				if (true) {
					SendSmsConfig ssc = new SendSmsConfig();
					ssc.setName(op.getName());
					ssc.setMobilenum(op.getMobile());
					ssc.setEventlist(eventList.getContent());
					SqlServerDBManager sqldao = SqlServerDBManager.getSqlServerManager();
					try {
						sqldao.save(ssc);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
					}
				}
			}
		}			
	}
	
}
