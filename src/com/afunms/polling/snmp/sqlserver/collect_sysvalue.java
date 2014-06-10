package com.afunms.polling.snmp.sqlserver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.JdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class collect_sysvalue {
	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Hashtable systemHt = new Hashtable();
		Hashtable sqlserverDataHash = ShareData.getSqlserverdata();

		int id = Integer.parseInt(nodeGatherIndicators.getNodeid());
		DBNode dbNode = (DBNode) PollingEngine.getInstance().getDbByID(id);
		if (dbNode == null)
			return null;

		// 采集数据
		JdbcUtil util = null;
		ResultSet rs = null;
		try {
			String serverIp = dbNode.getIpAddress();
			String userName = dbNode.getUser();
			String password = EncryptUtil.decode(dbNode.getPassword());
			String port = dbNode.getPort();
			String hex = IpTranslation.formIpToHex(serverIp);

			String dbUrl = "jdbc:jtds:sqlserver://" + serverIp + ":" + port + ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
			String sql = "SELECT SERVERPROPERTY ('productlevel') as productlevel, @@VERSION as VERSION,SERVERPROPERTY('MACHINENAME') as MACHINENAME,SERVERPROPERTY('IsSingleUser') as IsSingleUser,SERVERPROPERTY('ProcessID') as ProcessID,SERVERPROPERTY('IsIntegratedSecurityOnly') as IsIntegratedSecurityOnly,SERVERPROPERTY('IsClustered') as IsClustered";

			util = new JdbcUtil(dbUrl, userName, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("productlevel") != null) {
					systemHt.put("productlevel", rs.getString("productlevel"));
				}
				if (rs.getString("VERSION") != null) {
					systemHt.put("VERSION", rs.getString("VERSION"));
				}
				if (rs.getString("MACHINENAME") != null) {
					systemHt.put("MACHINENAME", rs.getString("MACHINENAME"));
				}
				if (rs.getString("IsSingleUser") != null) {
					String IsSingleUser = rs.getString("IsSingleUser");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						systemHt.put("IsSingleUser", "单个用户");
					} else {
						systemHt.put("IsSingleUser", "非单个用户");
					}
				}
				if (rs.getString("ProcessID") != null) {
					systemHt.put("ProcessID", rs.getString("ProcessID"));
				}
				if (rs.getString("IsIntegratedSecurityOnly") != null) {
					String IsSingleUser = rs.getString("IsIntegratedSecurityOnly");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						systemHt.put("IsIntegratedSecurityOnly", "集成安全性");
					} else {
						systemHt.put("IsIntegratedSecurityOnly", "非集成安全性");
					}
				}
				if (rs.getString("IsClustered") != null) {
					String IsSingleUser = rs.getString("IsClustered");
					if (IsSingleUser.equalsIgnoreCase("1")) {
						systemHt.put("IsClustered", "群集");
					} else {
						systemHt.put("IsClustered", "非群集");
					}
				}
				// 写入内存
				if (sqlserverDataHash.get(serverIp) == null) {
					sqlserverDataHash.put(serverIp, new Hashtable());
				}
				Hashtable sqlserverDataHt = (Hashtable) sqlserverDataHash.get(serverIp);
				sqlserverDataHt.put("sysValue", systemHt);
			}
			// 存入数据库
			try {
				String deleteSQL = "delete from nms_sqlserversysvalue where serverip ='" + hex + ":" + dbNode.getAlias() + "'";
				GathersqlListManager.Addsql(deleteSQL);
				String insertSQL = addSqlserver_nmssysvalue(hex + ":" + dbNode.getAlias(), systemHt);
				GathersqlListManager.Addsql(insertSQL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			util.closeStmt();
			util.closeConn();
		}
		return sqlserverDataHash;
	}

	public String addSqlserver_nmssysvalue(String serverip, Hashtable sysvalue) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sBuffer = new StringBuffer();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);

			sBuffer.append("insert into nms_sqlserversysvalue(serverip, productlevel, version, " + "machinename, issingleuser, processid,isintegratedsecurityonly,isclustered,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("productlevel")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("VERSION")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("MACHINENAME")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("IsSingleUser")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("ProcessID")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("IsIntegratedSecurityOnly")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(sysvalue.get("IsClustered")));
			if (SystemConstant.DBType.equals("mysql")) {
				sBuffer.append("','");
				sBuffer.append(montime);
				sBuffer.append("')");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sBuffer.append("',to_date('" + montime + "','yyyy-mm-dd hh24:mi:ss'))");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sBuffer.toString();
	}
}
