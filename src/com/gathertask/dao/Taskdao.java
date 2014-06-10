package com.gathertask.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.util.DataGate;
import com.database.config.SystemConfig;

public class Taskdao {

	Logger logger = Logger.getLogger(Taskdao.class);

	/**
	 * 获取需要采集的采集指标 当agentid 的id为自然数的时候，就人物是agent的模式采集， 当agent的id为-1 的时候
	 * 
	 * @return 采集的任务列表
	 */
	@SuppressWarnings("unchecked")
	public Hashtable GetRunTaskList() {

		String sql = "";
		List sqlList = new ArrayList();
		// Agent模式
		int agentid = -1;
		String Systemtype = SystemConfig.getConfigInfomation("Agentconfig", "Systemtype");
		if (Systemtype.trim().equals("agent")) {// agent 采集机器
			try {
				agentid = Integer.parseInt(SystemConfig.getConfigInfomation("Agentconfig", "AGENTID"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			sql = "select b.* from topo_host_node a ,nms_gather_indicators_node b ,nms_node_agent c where a.id=b.nodeid and a.managed=1 and b.classpath like 'com%' and c.nodeid=b.nodeid and c.agentid='"
					+ agentid + "'";
			sqlList.add(sql);
			sql = "select b.* from app_db_node a ,nms_gather_indicators_node b ,nms_node_agent c where a.id=b.nodeid and a.managed=1 and b.classpath like 'com%' and c.nodeid=b.nodeid and c.agentid='"
					+ agentid + "'";
			sqlList.add(sql);
			sql = "select b.* from nms_urlconfig a ,nms_gather_indicators_node b ,nms_node_agent c where a.id=b.nodeid and a.flag=1 and b.classpath like 'com%' and c.nodeid=b.nodeid and c.agentid='"
					+ agentid + "'";
			sqlList.add(sql);
			sql = "select b.* from app_tomcat_node a ,nms_gather_indicators_node b ,nms_node_agent c where a.id=b.nodeid and a.monflag=1 and b.classpath like 'com%' and c.nodeid=b.nodeid and c.agentid='"
					+ agentid + "'";
			sqlList.add(sql);
			sql = "select b.* from nms_wasconfig a ,nms_gather_indicators_node b ,nms_node_agent c where a.id=b.nodeid and a.mon_flag=1 and b.classpath like 'com%' and c.nodeid=b.nodeid and c.agentid='"
					+ agentid + "'";
			sqlList.add(sql);
			sql = "select b.* from nms_weblogicconfig a ,nms_gather_indicators_node b ,nms_node_agent c where a.id=b.nodeid and a.mon_flag=1 and b.classpath like 'com%' and c.nodeid=b.nodeid and c.agentid='"
					+ agentid + "'";
			sqlList.add(sql);
			sql = "select b.* from nms_iisconfig a ,nms_gather_indicators_node b ,nms_node_agent c where a.id=b.nodeid and a.mon_flag=1 and b.classpath like 'com%' and c.nodeid=b.nodeid and c.agentid='"
					+ agentid + "'";
			sqlList.add(sql);
		} else if (Systemtype.trim().equals("standalone")) {// standalone 单机版本
			sql = "select b.* from topo_host_node a ,nms_gather_indicators_node b where a.id=b.nodeid and a.managed=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from app_db_node a ,nms_gather_indicators_node b where a.id=b.nodeid and a.managed=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_urlconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_ftpmonitorconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.monflag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from app_tomcat_node a ,nms_gather_indicators_node b where a.id=b.nodeid and a.monflag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from app_resin_node a ,nms_gather_indicators_node b where a.id=b.nodeid and a.monflag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_wasconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.mon_flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_jbossconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_weblogin a ,nms_gather_indicators_node b where a.id=b.nodeid and a.flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_portservice a ,nms_gather_indicators_node b where a.id=b.nodeid and a.monflag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_emailmonitorconf a ,nms_gather_indicators_node b where a.id=b.nodeid and a.monflag=1 and b.classpath like 'com%'";
			sqlList.add(sql);;
			sql = "select b.* from nms_tftpmonitorconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.monflag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_apacheconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_dhcpconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.monflag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_weblogicconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.mon_flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_iisconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.mon_flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_dnsconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
			sql = "select b.* from nms_mqconfig a ,nms_gather_indicators_node b where a.id=b.nodeid and a.mon_flag=1 and b.classpath like 'com%'";
			sqlList.add(sql);
		} else if (Systemtype.trim().equals("webserver")) {
			sql = "";
		}

		Hashtable list = new Hashtable();
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			for (int i = 0; i < sqlList.size(); i++) {
				String tempsql = (String) sqlList.get(i);
				if (tempsql == null || "".equals(tempsql))
					continue;
				rs = stmt.executeQuery(tempsql);
				while (rs.next()) {
					NodeGatherIndicators nodeGatherIndicators = new NodeGatherIndicators();
					nodeGatherIndicators.setId(rs.getInt("id"));
					nodeGatherIndicators.setNodeid(rs.getString("nodeid"));
					nodeGatherIndicators.setName(rs.getString("name"));
					nodeGatherIndicators.setType(rs.getString("type"));
					nodeGatherIndicators.setSubtype(rs.getString("subtype"));
					nodeGatherIndicators.setAlias(rs.getString("alias"));
					nodeGatherIndicators.setDescription(rs.getString("description"));
					nodeGatherIndicators.setCategory(rs.getString("category"));
					nodeGatherIndicators.setIsDefault(rs.getString("isDefault"));
					nodeGatherIndicators.setIsCollection(rs.getString("isCollection"));
					nodeGatherIndicators.setPoll_interval(rs.getString("poll_interval"));
					nodeGatherIndicators.setInterval_unit(rs.getString("interval_unit"));
					nodeGatherIndicators.setClasspath(rs.getString("classpath"));
					list.put(rs.getInt("id") + "", nodeGatherIndicators);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
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
}
