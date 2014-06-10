package com.afunms.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.afunms.application.dao.DBDao;
import com.afunms.common.util.SessionConstant;
import com.afunms.inform.util.SystemSnap;
import com.afunms.system.manage.UserManager;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;

public class PortalSnapshot extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setCharacterEncoding("UTF-8");
		res.setContentType("text/html;charset=UTF-8");
		PrintWriter out = res.getWriter();

		UserManager mOp = new UserManager();
		User current_user = (User) req.getSession().getAttribute(SessionConstant.CURRENT_USER);
		String bids = null;
		try {
			if (current_user == null) {
				return;
			}
			bids = current_user.getBusinessids();
			if (current_user.getRole() == 0) {
				bids = "-1";
			}
			bids = "-1";
		} catch (Exception e) {
			e.printStackTrace();
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
			midSize = mOp.getMiddleService(current_user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int routerStatus = SystemSnap.getRouterStatus(current_user.getBusinessids());
		int switchStatus = SystemSnap.getSwitchStatus(current_user.getBusinessids());
		int serverStatus = SystemSnap.getServerStatus(current_user.getBusinessids());
		int dbStatus = SystemSnap.getDbStatus(current_user.getBusinessids());
		int secureStatus = SystemSnap.getFirewallStatus(current_user.getBusinessids());
		int storageStatus = SystemSnap.getStorageStatus(current_user.getBusinessids());
		int midStatus = SystemSnap.getMiddleStatus(current_user.getBusinessids());

		String routePath = "/perform.do?action=monitornodelist&flag=1&category=net_router";
		String switchPath = "/perform.do?action=monitornodelist&flag=1&category=net_switch";
		String serverPath = "/perform.do?action=monitornodelist&flag=1&category=net_server";
		String dbPath = "/db.do?action=list&flag=1";
		String securePath = "/perform.do?action=monitornodelist&flag=1&category=safeequip";
		String midPath = "/middleware.do?action=list&flag=1&category=middleware";
		String storagePath = "/perform.do?action=monitornodelist&flag=1&category=net_storage";

		try {
			StringBuffer jsonString = new StringBuffer("{total:7,Rows:[");
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

			jsonString.append("]}");
			out.print(jsonString.toString());
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	@Override
	public void destroy() {
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		super.init();
	}
}
