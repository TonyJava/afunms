package com.afunms.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.afunms.application.dao.DBDao;
import com.afunms.application.manage.DataBaseManager;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MonitorDBDTO;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.manage.PerformanceManager;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.MonitorNodeDTO;

public class PortalResource extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setCharacterEncoding("UTF-8");
		res.setContentType("text/html;charset=UTF-8");
		PrintWriter out = res.getWriter();
		try {
			int start = 0;
			if("null".equals(req.getParameter("start")) 
					|| "".equals(req.getParameter("start")) ||req.getParameter("start") == null){
				start = 0;
			}else{
				start = Integer.parseInt(req.getParameter("start"));
			}
			int limit = 0;
			if("null".equals(req.getParameter("limit")) 
					|| "".equals(req.getParameter("limit")) ||req.getParameter("limit") == null){
				limit = 10;
			}else{
				limit = Integer.parseInt(req.getParameter("limit"));
			}
			
			int iStart = start;
			int iLimit = limit;
			
			String search = req.getParameter("searchMsg");
			if(search == null){
				search = "";
			}
			
			String condition = new String(search.getBytes("ISO-8859-1"), "UTF-8");

			List rsList = new ArrayList();
			User current_user = (User)req.getSession().getAttribute(SessionConstant.CURRENT_USER);
			if (current_user == null) {
				return;
			}
			String[] bids = null;
			if (current_user.getBusinessids() != null) {
				if (current_user.getBusinessids() != "-1") {
					String bidString = current_user.getBusinessids().substring(1);
					bids = bidString.split(",");
				}
			}

			// 数据库
			DataBaseManager dbManager = new DataBaseManager();
			List dbList = getList(bids, condition);
			for (int i = 0; i < dbList.size(); i++) {
				DBVo vo = (DBVo) dbList.get(i);
				MonitorDBDTO monitorDBDTO = dbManager.getMonitorDBDTOByDBVo(vo, 0);
				rsList.add(monitorDBDTO);
			}
			HostNodeDao hostNodeDao = null;
			List<HostNode> monitorNodeList = new ArrayList();
			List<HostNode> tempList = new ArrayList();
			try {
				hostNodeDao = new HostNodeDao();
				// 服务器
				tempList = hostNodeDao.loadMonitorByMonCategoryForPortal(1, 4, bids, condition);
				monitorNodeList.addAll(tempList);
				// 网络设备
				hostNodeDao = new HostNodeDao();
				tempList = hostNodeDao.loadMonitorByMonCategoryForPortal(1, 1, bids, condition);
				monitorNodeList.addAll(tempList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (hostNodeDao != null) {
					hostNodeDao.close();
				}
			}
			PerformanceManager performanceManager = new PerformanceManager();
			for (int i = 0; i < monitorNodeList.size(); i++) {
				HostNode hostNode = monitorNodeList.get(i);
				MonitorNodeDTO monitorNodeDTO = new MonitorNodeDTO();
				monitorNodeDTO = performanceManager.getMonitorNodeDTOByHostNode(hostNode);
				rsList.add(monitorNodeDTO);
			}

			int count = rsList.size();
			if (count > iStart + iLimit) {
				count = iStart + iLimit;
			}

			StringBuffer jsonString = new StringBuffer("{total:" + rsList.size() + ",monitorNodeList:[");
			for (int i = iStart; i < count; i++) {
				Object ob = rsList.get(i);
				if (ob instanceof MonitorNodeDTO) {
					MonitorNodeDTO vo = (MonitorNodeDTO) ob;
					jsonString.append("{ipAddress:'");
					jsonString.append(vo.getIpAddress());
					jsonString.append("',");

					jsonString.append("nodeId:'");
					jsonString.append(vo.getId());
					jsonString.append("',");

					jsonString.append("type:'");
					jsonString.append(vo.getType());
					jsonString.append("',");

					jsonString.append("alias:'");
					jsonString.append(vo.getAlias());
					jsonString.append("',");

					jsonString.append("status:'");
					jsonString.append(vo.getStatus());
					jsonString.append("',");

					jsonString.append("category:'");
					jsonString.append(vo.getCategory());
					jsonString.append("',");

					jsonString.append("pingValue:'");
					jsonString.append(vo.getPingValue());
					jsonString.append("',");

					jsonString.append("cpuValue:'");
					jsonString.append(vo.getCpuValue());
					jsonString.append("',");

					jsonString.append("memoryValue:'");
					jsonString.append(vo.getMemoryValue());
					jsonString.append("',");

					jsonString.append("inutilhdxValue:'");
					jsonString.append(vo.getInutilhdxValue());
					jsonString.append("',");

					jsonString.append("oututilhdxValue:'");
					jsonString.append(vo.getOututilhdxValue());
					jsonString.append("'}");
				} else if (ob instanceof MonitorDBDTO) {
					MonitorDBDTO vo = (MonitorDBDTO) ob;
					jsonString.append("{ipAddress:'");
					jsonString.append(vo.getIpAddress());
					jsonString.append("',");

					jsonString.append("alias:'");
					jsonString.append(vo.getAlias());
					jsonString.append("',");

					jsonString.append("status:'");
					jsonString.append(vo.getStatus());
					jsonString.append("',");

					jsonString.append("dbType:'");
					jsonString.append(vo.getDbtype());
					jsonString.append("',");

					jsonString.append("category:'");
					jsonString.append(vo.getDbtype());
					jsonString.append("',");

					jsonString.append("pingValue:'");
					jsonString.append(vo.getPingValue());
					jsonString.append("',");

					jsonString.append("cpuValue:'");
					jsonString.append("0");
					jsonString.append("',");

					jsonString.append("memoryValue:'");
					jsonString.append("0");
					jsonString.append("',");

					jsonString.append("inutilhdxValue:'");
					jsonString.append("0");
					jsonString.append("',");

					jsonString.append("oututilhdxValue:'");
					jsonString.append("0");
					jsonString.append("'}");
				}
				if (i != count - 1) {
					jsonString.append(",");
				}
			}
			jsonString.append("]}");
			out.print(jsonString.toString());
			out.flush();
		} catch (Exception e1) {
			e1.printStackTrace();
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

	public List getList(String[] bids, String condition) {
		List list = new ArrayList();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		int flag = 0;
		if (bids != null && bids.length > 0) {
			for (int i = 0; i < bids.length; i++) {
				if (bids[i].trim().length() > 0) {
					if (flag == 0) {
						s2.append(" and ( bid like '%" + bids[i].trim() + "%' ");
						flag = 1;
					} else {
						s2.append(" or bid like '%" + bids[i].trim() + "%' ");
					}
				}
			}
			s2.append(") ");
		}
		sql2.append("select * from app_db_node where 1=1 " + s2.toString());
		sql2.append(" and ip_address like '%" + condition + "%' or alias like '%" + condition + "%'");
		DBDao dao = new DBDao();
		try {
			list = dao.findByCriteria(sql2.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return list;
	}

}
