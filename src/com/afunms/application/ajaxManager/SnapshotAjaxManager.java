package com.afunms.application.ajaxManager;

import com.afunms.application.dao.DBDao;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.inform.util.SystemSnap;
import com.afunms.system.manage.UserManager;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;

public class SnapshotAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	private void getSnapshotData() {
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		UserManager mOp = new UserManager();
		String bids = user.getBusinessids();
		if (user.getRole() == 0) {
			bids = "-1";
		}
		HostNodeDao nodedao = new HostNodeDao();
		DBDao dbDao = new DBDao();
		int routeSize = 0;
		int switchSize = 0;
		int dbSize = 0;
		int secureSize = 0;
		int storageSize = 0;
		int hostSize = 0;
		int midSize = 0;

		try {
			routeSize = nodedao.loadNetworkByBidAndCategory(1, bids).size();
			switchSize = nodedao.loadNetworkByBidAndCategory(2, bids).size();
			dbSize = dbDao.getDbByMonFlag(1).size();
			secureSize = nodedao.loadNetworkByBid(8, bids).size();
			storageSize = nodedao.loadNetworkByBid(14, bids).size();
			hostSize = nodedao.loadNetworkByBid(4, bids).size();
			midSize = mOp.getMiddleService(user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int routerStatus = SystemSnap.getRouterStatus(user.getBusinessids());
		int switchStatus = SystemSnap.getSwitchStatus(user.getBusinessids());
		int serverStatus = SystemSnap.getServerStatus(user.getBusinessids());
		int dbStatus = SystemSnap.getDbStatus(user.getBusinessids());
		int secureStatus = SystemSnap.getFirewallStatus(user.getBusinessids());
		int storageStatus = SystemSnap.getStorageStatus(user.getBusinessids());
		int midStatus = SystemSnap.getMiddleStatus(user.getBusinessids());

		String routePath = "/perform.do?action=monitornodelist&flag=1&category=net_router";
		String switchPath = "/perform.do?action=monitornodelist&flag=1&category=net_switch";
		String serverPath = "/perform.do?action=monitornodelist&flag=1&category=net_server";
		String dbPath = "/db.do?action=list&flag=1";
		String securePath = "/perform.do?action=monitornodelist&flag=1&category=safeequip";
		String midPath = "/middleware.do?action=list&flag=1&category=middleware";
		String storagePath = "/perform.do?action=monitornodelist&flag=1&category=net_storage";

		StringBuffer jsonString = new StringBuffer("{Rows:[");

		// 路由器
		jsonString.append("{\"name\":\"");
		jsonString.append("路由器(" + routeSize + ")");
		jsonString.append("\",");

		jsonString.append("\"url\":\"");
		jsonString.append("router-alarm-" + routerStatus + ".gif");
		jsonString.append("\",");

		jsonString.append("\"href\":\"");
		jsonString.append(routePath);
		jsonString.append("\"}");
		jsonString.append(",");

		// 交换机
		jsonString.append("{\"name\":\"");
		jsonString.append("交换机(" + switchSize + ")");
		jsonString.append("\",");

		jsonString.append("\"url\":\"");
		jsonString.append("switch-alarm-" + switchStatus + ".gif");
		jsonString.append("\",");

		jsonString.append("\"href\":\"");
		jsonString.append(switchPath);
		jsonString.append("\"}");
		jsonString.append(",");

		// 服务器
		jsonString.append("{\"name\":\"");
		jsonString.append("服务器(" + hostSize + ")");
		jsonString.append("\",");

		jsonString.append("\"url\":\"");
		jsonString.append("server-alarm-" + serverStatus + ".gif");
		jsonString.append("\",");

		jsonString.append("\"href\":\"");
		jsonString.append(serverPath);
		jsonString.append("\"}");
		jsonString.append(",");

		// 数据库
		jsonString.append("{\"name\":\"");
		jsonString.append("数据库(" + dbSize + ")");
		jsonString.append("\",");

		jsonString.append("\"url\":\"");
		jsonString.append("db-alarm-" + dbStatus + ".gif");
		jsonString.append("\",");

		jsonString.append("\"href\":\"");
		jsonString.append(dbPath);
		jsonString.append("\"}");
		jsonString.append(",");

		// 安全设备
		jsonString.append("{\"name\":\"");
		jsonString.append("安全(" + secureSize + ")");
		jsonString.append("\",");

		jsonString.append("\"url\":\"");
		jsonString.append("firewall-alarm-" + secureStatus + ".gif");
		jsonString.append("\",");

		jsonString.append("\"href\":\"");
		jsonString.append(securePath);
		jsonString.append("\"}");
		jsonString.append(",");

		// 中间件
		jsonString.append("{\"name\":\"");
		jsonString.append("中间件(" + midSize + ")");
		jsonString.append("\",");

		jsonString.append("\"url\":\"");
		jsonString.append("middleware-alarm-" + midStatus + ".gif");
		jsonString.append("\",");

		jsonString.append("\"href\":\"");
		jsonString.append(midPath);
		jsonString.append("\"}");
		jsonString.append(",");

		// 服务
//		jsonString.append("{\"name\":\"");
//		jsonString.append("服务(" + serviceSize + ")");
//		jsonString.append("\",");
//
//		jsonString.append("\"url\":\"");
//		jsonString.append("service-alarm-" + serviceStatus + ".gif");
//		jsonString.append("\",");
//
//		jsonString.append("\"href\":\"");
//		jsonString.append(servicePath);
//		jsonString.append("\"}");
//		jsonString.append(",");

		// 存储
		jsonString.append("{\"name\":\"");
		jsonString.append("存储(" + storageSize + ")");
		jsonString.append("\",");

		jsonString.append("\"url\":\"");
		jsonString.append("storage-alarm-" + storageStatus + ".gif");
		jsonString.append("\",");

		jsonString.append("\"href\":\"");
		jsonString.append(storagePath);
		jsonString.append("\"}");

		jsonString.append("],total:7}");
		out.print(jsonString.toString());
		out.flush();

	}

	private String getFromSession(String arg) {
		String rt = "0";
		if (session.getAttribute(arg) != null) {
			rt = (String) session.getAttribute(arg);
		}
		return rt;
	}

	public void execute(String action) {
		if (action.equals("getSnapshotData")) {
			getSnapshotData();
		}
	}
}
