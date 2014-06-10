package com.gatherResulttosql;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.MemoryCollectEntity;
import com.gatherdb.GathersqlListManager;

@SuppressWarnings("unchecked")
public class HostPhysicalMemoryResulttosql {

	/**
	 * 
	 * 根据采集结果生成对应的sql放入到内存列表中
	 */
	public void CreateResultTosql(Hashtable ipdata, String ip) {

		if (ipdata.containsKey("memory")) {
			MemoryCollectEntity memorydata;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Vector memoryVector = (Vector) ipdata.get("memory");
			String allipstr = SysUtil.doip(ip);
			String usedSize = "";
			for (int i = 0; i < memoryVector.size(); i++) {
				memorydata = (MemoryCollectEntity) memoryVector.get(i);
				if (memorydata.getEntity().equalsIgnoreCase("UsedSize")) {
					usedSize = memorydata.getThevalue();
					break;
				}
			}
			if (memoryVector != null && memoryVector.size() > 0 && !usedSize.equals("-1.0")) {
				for (int si = 0; si < memoryVector.size(); si++) {
					memorydata = (MemoryCollectEntity) memoryVector.elementAt(si);
					if (memorydata.getRestype().equals("dynamic")) {
						Calendar tempCal = (Calendar) memorydata.getCollecttime();
						Date cc = tempCal.getTime();
						String time = sdf.format(cc);
						String tablename = "memory" + allipstr;
						String sql = "";
						if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
									+ "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity()
									+ "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'"
									+ memorydata.getThevalue() + "','" + time + "')";
						} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
									+ "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity()
									+ "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'"
									+ memorydata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
						} else if ("dm".equalsIgnoreCase(SystemConstant.DBType)) {
							sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip
									+ "','" + memorydata.getRestype() + "','" + memorydata.getCategory() + "','" + memorydata.getEntity() + "','" + memorydata.getSubentity()
									+ "','" + memorydata.getUnit() + "','" + memorydata.getChname() + "','" + memorydata.getBak() + "'," + memorydata.getCount() + ",'"
									+ memorydata.getThevalue() + "',to_date('" + time + "','YYYY-MM-DD HH24:MI:SS'))";
						}

						GathersqlListManager.Addsql(sql);
						sql = null;
						tablename = null;
						time = null;
						cc = null;
						tempCal = null;

					}
				}
			}
			memoryVector = null;
		}
	}
}
