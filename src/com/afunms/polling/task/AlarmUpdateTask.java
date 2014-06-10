package com.afunms.polling.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import com.afunms.alertalarm.CreateAlarmXml;
import com.afunms.common.util.CommonUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.AlarmInfoDao;

public class AlarmUpdateTask extends TimerTask {

	// ȡ��ȥ10�����ڷ����ĸ澯��Ϣ
	@SuppressWarnings("unchecked")
	public void run() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar sendcalen = Calendar.getInstance();
		Date cc = sendcalen.getTime();
		String curdate = formatter.format(cc);

		String endTime = CommonUtil.getCurrentTime(); // ȡ��ǰʱ��
		String beginTime = CommonUtil.getLaterTenSecondTime(); // ȡ��ȥ10���ʱ��

		String fromTime = curdate + " " + beginTime;
		String toTime = curdate + " " + endTime;
		AlarmInfoDao alarmdao = new AlarmInfoDao();
		String queryStr = "";
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			queryStr = "select ipaddress,level1,content,type from nms_alarminfo where level1 = 2 and recordtime >='" + fromTime + "' and recordtime<='" + toTime + "' ";
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			queryStr = "select ipaddress,level1,content,type from nms_alarminfo where level1 = 2 and recordtime >=to_date('" + fromTime
					+ "','YYYY-MM-DD HH24:MI:SS')  and recordtime<=to_date('" + toTime + "','YYYY-MM-DD HH24:MI:SS')";
		}

		List list = new ArrayList();
		try {
			list = alarmdao.findByCriteria(queryStr);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				alarmdao.close();
			} catch (Exception exp) {
				exp.printStackTrace();
			}
		}

		// ���µ�ʱ��εı�����Ϣ�Ĳ�ѯ,�ݶ�Ϊȡ���ؼ���ĸ澯
		if (list != null && list.size() > 0) {
			CreateAlarmXml cax = new CreateAlarmXml();
			cax.createXml(list); // ���ɸ澯��Ϣ��XML�ļ�
		}
	}
}
