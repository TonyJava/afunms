package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.FlashCollectEntity;
import com.gatherdb.GathersqlListManager;

@SuppressWarnings("unchecked")
public class NetflashResultTosql {

	/**
	 * 
	 * 把采集数据生成sql放入的内存列表中
	 */
	public void CreateResultTosql(Hashtable ipdata, String ip) {

		if (ipdata.containsKey("flash")) {
			Vector flashVector = (Vector) ipdata.get("flash");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String allipstr = SysUtil.doip(ip);

			if (flashVector != null && flashVector.size() > 0) {
				for (int i = 0; i < flashVector.size(); i++) {
					FlashCollectEntity flashdata = (FlashCollectEntity) flashVector.elementAt(i);
					if (flashdata.getSubentity().equals("unknown")) {
						// 未采集到数据不能进行存储
						return;
					}
					if (flashdata.getRestype().equals("dynamic")) {
						Calendar tempCal = (Calendar) flashdata.getCollecttime();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						String tablename = "flash" + allipstr;
						long count = 0;
						if (flashdata.getCount() != null) {
							count = flashdata.getCount();
						}
						StringBuffer sBuffer = new StringBuffer(200);
						sBuffer.append("insert into ");
						sBuffer.append(tablename);
						sBuffer.append("(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) ");
						sBuffer.append("values('");
						sBuffer.append(ip);
						sBuffer.append("','");
						sBuffer.append(flashdata.getRestype());
						sBuffer.append("','");
						sBuffer.append(flashdata.getCategory());
						sBuffer.append("','");
						sBuffer.append(flashdata.getEntity());
						sBuffer.append("','");
						sBuffer.append(flashdata.getSubentity());
						sBuffer.append("','");
						sBuffer.append(flashdata.getUnit());
						sBuffer.append("','");
						sBuffer.append(flashdata.getChname());
						sBuffer.append("','");
						sBuffer.append(flashdata.getBak());
						sBuffer.append("','");
						sBuffer.append(count);
						sBuffer.append("','");
						sBuffer.append(flashdata.getThevalue());
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("','");
							sBuffer.append(time);
							sBuffer.append("')");
						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("',");
							sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
							sBuffer.append(")");
						} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
							sBuffer.append("',");
							sBuffer.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");
							sBuffer.append(")");
						}
						GathersqlListManager.Addsql(sBuffer.toString());
						sBuffer = null;
					}
					flashdata = null;
				}
			}
			flashVector = null;
		}
	}
}
