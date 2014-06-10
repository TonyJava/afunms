package com.afunms.polling.snmp.weblogic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.application.weblogicmonitor.WeblogicJdbc;
import com.afunms.application.weblogicmonitor.WeblogicSnmp;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.gatherdb.GathersqlListManager;

/**
 * weblogic jdbc ²É¼¯
 * 
 * @author yangjun 2013/3/18
 * 
 */
public class JdbcSnmp extends SnmpMonitor {

	public JdbcSnmp() {
	}

	@SuppressWarnings("unchecked")
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable returndata = new Hashtable();
		WeblogicConfig weblogicconf = null;
		String id = nodeGatherIndicators.getNodeid();
		try {
			WeblogicConfigDao dao = new WeblogicConfigDao();
			try {
				weblogicconf = (WeblogicConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			List jdbcValue = new ArrayList();
			WeblogicSnmp weblogicsnmp = null;
			try {
				weblogicsnmp = new WeblogicSnmp(weblogicconf.getIpAddress(), weblogicconf.getCommunity(), weblogicconf.getPortnum());
				jdbcValue = weblogicsnmp.collectJdbcData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (jdbcValue != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				returndata.put("jdbcValue", jdbcValue);
				if (!(ShareData.getWeblogicdata().containsKey(weblogicconf.getIpAddress()))) {
					ShareData.getWeblogicdata().put(weblogicconf.getIpAddress(), returndata);
				} else {
					Hashtable hash = (Hashtable) ShareData.getWeblogicdata().get(weblogicconf.getIpAddress());
					hash.put("jdbcValue", returndata.get("jdbcValue"));
				}

				Calendar tempCal = Calendar.getInstance();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);

				String nodeid = id;
				List jdbcValuesList = jdbcValue;
				String deleteSql = "delete from nms_weblogic_jdbc where nodeid='" + nodeid + "'";
				GathersqlListManager.Addsql(deleteSql);
				if (jdbcValuesList != null && jdbcValuesList.size() > 0) {
					for (int i = 0; i < jdbcValuesList.size(); i++) {
						WeblogicJdbc weblogicJdbc = (WeblogicJdbc) jdbcValuesList.get(i);
						try {
							StringBuffer sql = new StringBuffer(500);
							sql.append("insert into nms_weblogic_jdbc(nodeid, jdbcConnectionPoolName, ConPoolRunActConnsCurCount, ");
							sql.append("ConPoolRunVerJDBCDriver, ConPoolRunMaxCapacity, ");
							sql.append("ConPoolRunActConsAvgCount, ConPoolRunHighestNumAvai,Leaked,WaitMaxTime,WaitCurrent, collecttime)values('");
							sql.append(nodeid);
							sql.append("','");
							sql.append(weblogicJdbc.getJdbcConnectionPoolName());// jdbcConnectionPoolName
							sql.append("','");
							sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeActiveConnectionsCurrentCount());// jdbcConnectionPoolRuntimeActiveConnectionsCurrentCount
							sql.append("','");
							sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeVersionJDBCDriver());// jdbcConnectionPoolRuntimeVersionJDBCDriver
							sql.append("','");
							sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeMaxCapacity());// jdbcConnectionPoolRuntimeMaxCapacity
							sql.append("','");
							sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeActiveConnectionsAverageCount());// jdbcConnectionPoolRuntimeActiveConnectionsAverageCount
							sql.append("','");
							sql.append(weblogicJdbc.getJdbcConnectionPoolRuntimeHighestNumAvailable());// jdbcConnectionPoolRuntimeHighestNumAvailable
							sql.append("',");
							sql.append(weblogicJdbc.getJdbcLeaked());// jdbcLeaked
							sql.append("',");
							sql.append(weblogicJdbc.getJdbcWaitMaxTime());// jdbcWaitMaxTime
							sql.append("',");
							sql.append(weblogicJdbc.getJdbcWaitCurrent());// jdbcWaitCurrent
							sql.append("',");
							if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("'");
								sql.append(time);// time
								sql.append("'");
							} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
								sql.append("to_date('" + time + "','YYYY-MM-DD HH24:MI:SS')");// time
							}
							sql.append(")");
							GathersqlListManager.Addsql(sql.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}
			weblogicsnmp = null;
			jdbcValue = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returndata;
	}
}
