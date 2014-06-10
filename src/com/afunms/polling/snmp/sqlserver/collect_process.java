package com.afunms.polling.snmp.sqlserver;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.JdbcUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.monitor.executor.base.SnmpMonitor;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.gatherdb.GathersqlListManager;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class collect_process extends SnmpMonitor {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Hashtable collect_Data(NodeGatherIndicators nodeGatherIndicators) {
		Vector processVector = new Vector();
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
			String sql = "select distinct a.spid,a.waittime,a.lastwaittype,a.waitresource,b.name as dbname,c.name as " //
					+ "username,a.cpu,a.physical_io,a.memusage,a.login_time,a.last_batch,a.status,a.hostname," //
					+ "a.program_name,a.hostprocess,a.cmd,a.nt_domain,a.nt_username,a.net_library,a.loginame from " //
					+ "sysprocesses a,sysdatabases b,sysusers c where a.dbid= b.dbid and a.uid=c.uid";//
			String dburl = "jdbc:jtds:sqlserver://" + serverIp + ":" + port + ";DatabaseName=master;charset=GBK;SelectMethod=CURSOR";
			util = new JdbcUtil(dburl, userName, password);
			util.jdbc();
			rs = util.stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String col = rsmd.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else
						return_value.put(col.toLowerCase(), "--");
				}
				processVector.addElement(return_value);
				return_value = null;
			}
			// 写入内存
			if (sqlserverDataHash.get(serverIp) == null) {
				sqlserverDataHash.put(serverIp, new Hashtable());
			}
			Hashtable sqlserverDataHt = (Hashtable) sqlserverDataHash.get(serverIp);
			sqlserverDataHt.put("info_v", processVector);

			// 存入数据库
			try {
				if (processVector != null && processVector.size() > 0) {
					try {
						Hashtable infoHash = null;
						String insertSQL = "";
						String deleteSQL = "delete from nms_sqlserverinfo_v where serverip = '" + hex + ":" + dbNode.getAlias() + "'";
						GathersqlListManager.Addsql(deleteSQL);
						for (int i = 0; i < processVector.size(); i++) {
							infoHash = (Hashtable) processVector.get(i);
							insertSQL = addSqlserver_nmsinfo_v(hex + ":" + dbNode.getAlias(), infoHash);
							GathersqlListManager.Addsql(insertSQL);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
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

	/*
	 * 封装插入sql语句
	 */
	public String addSqlserver_nmsinfo_v(String serverip, Hashtable info) throws Exception {

		DBManager dbmanager = new DBManager();
		StringBuffer sBuffer = new StringBuffer();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = tempCal.getTime();
			String montime = sdf.format(cc);

			sBuffer.append("insert into nms_sqlserverinfo_v(serverip, spid, waittime, ");
			sBuffer.append("lastwaittype, waitresource, dbname,username,cpu,physical_io,memusage,login_time,last_batch,");
			sBuffer.append("status,hostname,program_name,hostprocess,cmd,nt_domain,nt_username,net_library,loginame,mon_time)");
			sBuffer.append(" values('");
			sBuffer.append(serverip);
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("spid")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("waittime")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("lastwaittype")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("waitresource")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("dbname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("username")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("cpu")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("physical_io")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("memusage")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("login_time")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("last_batch")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("status")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("hostname")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("program_name")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("hostprocess")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("cmd")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("nt_domain")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("nt_username")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("net_library")));
			sBuffer.append("','");
			sBuffer.append(String.valueOf(info.get("loginame")));
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
		} finally {
			dbmanager.close();
		}
		return sBuffer.toString();
	}

}
