package com.afunms.monitor.executor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

import com.afunms.application.util.DBPool;
import com.afunms.monitor.executor.base.BaseMonitor;
import com.afunms.monitor.executor.base.MonitorInterface;
import com.afunms.monitor.item.CommonItem;
import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.DBNode;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class MySQLSession extends BaseMonitor implements MonitorInterface {
	public MySQLSession() {
	}

	public Hashtable collect_Data(HostNode node) {
		return null;
	}

	public void collectData(HostNode node) {

	}

	/**
	 * Threads_connected是整个mysql的当前连接数，而我们要求的是被监视db的连接数
	 */
	public void collectData(Node node, MonitoredItem monitoredItem) {
		DBNode dbNode = (DBNode) node;
		CommonItem item = (CommonItem) monitoredItem;
		Connection conn = DBPool.getInstance().getConnection(node.getId());
		if (conn == null) {
			item.setSingleResult(-1);
			return;
		}

		int session = 0;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("show processlist");
			while (rs.next()) {
				if (rs.getString("db").equals(dbNode.getDbName())) {
					session++;
				}
			}
		} catch (Exception e) {
			session = -1;
		} finally {
			DBPool.getInstance().close(stmt, rs);
		}
		item.setSingleResult(session);
	}
}