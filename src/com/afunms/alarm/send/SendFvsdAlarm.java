package com.afunms.alarm.send;

import java.util.Date;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;

import com.afunms.event.model.EventList;
import com.dhcc.itsm.webservice.server.databinding.Alarm;
import com.dhcc.itsm.webservice.server.service.alarm.AlarmPushService;

public class SendFvsdAlarm {
	private Logger logger = Logger.getLogger(this.getClass());
	private static String withinIP = "10.10.152.63";

	public boolean sendFVSDAlarm(EventList eventList) {
		boolean result = true;
		try {
			String content = eventList.getContent();
			Alarm alarm = new Alarm();
			alarm.setMessage(content);
			alarm.setOccurTime(eventList.getRecordtime().getTime());
			alarm.setAlarmType(1);
			alarm.setAlarmId(String.valueOf(eventList.getEventlocation()));
			alarm.setMoType("8a8a18bf257c7bfb01257caae5570018");
			alarm.setSeverity("1");
			JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
			factory.setServiceClass(AlarmPushService.class);
			factory.setAddress("http://" + withinIP + ":8080/fvsd/services/AlarmPushService");
			AlarmPushService service = (AlarmPushService) factory.create();
			logger.info("开始调用服务台发送接口发送告警信息");
			service.sendAlarm(alarm);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public void alarmClientTest() {
		Alarm alarm = new Alarm();
		alarm.setMessage("告警测试");
		alarm.setOccurTime(new Date());
		alarm.setAlarmType(1);
		alarm.setAlarmId("33333");
		alarm.setMoType("8a8a18bf257c7bfb01257caae5570018");
		alarm.setSeverity("1");

		Alarm alarm1 = new Alarm();
		alarm1.setMessage("告警测试1");
		alarm1.setOccurTime(new Date());
		alarm1.setAlarmType(2);
		alarm1.setAlarmId("44444");
		alarm1.setMoType("8a8a18bf257c7bfb01257caae5570018");
		alarm1.setSeverity("3");

		Alarm[] alarms = { alarm, alarm1 };

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(AlarmPushService.class);
		factory.setAddress("http://10.18.37.100:8080/fvsd_3.4.1/services/AlarmPushService");
		AlarmPushService alarmPush = (AlarmPushService) factory.create();
		try {
			alarmPush.bulkSendAlarm(alarms);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
