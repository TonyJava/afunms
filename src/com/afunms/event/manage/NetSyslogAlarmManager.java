package com.afunms.event.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.NetSyslogNodeRuleDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.NetSyslogNodeRule;


@SuppressWarnings("unchecked")
public class NetSyslogAlarmManager extends BaseManager implements ManagerInterface {
	private String editall() {
		String[] ids = getParaArrayValue("checkbox");
		String hostid = "";
		if (ids != null && ids.length > 0) {
			// 进行修改
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				HostNodeDao dao = new HostNodeDao();
				HostNode host = null;
				try {
					host = (HostNode) dao.findByID(id);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					dao.close();
				}
				hostid = hostid + host.getId() + ",";

			}
			request.setAttribute("hostid", hostid);
		}
		return "/alarm/syslog/editall.jsp";
	}

	public String execute(String action) {

		if (action.equals("list")) {
			return list();
		}
		if (action.equals("editall")) {
			return editall();
		}
		if (action.equals("toolbarsaveall")) {
			return toolbarsaveall();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;

	}

	private String list() {
		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);

		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (current_user.getBusinessids() != null) {
			if (current_user.getBusinessids() != "-1") {
				String[] bids = current_user.getBusinessids().split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( bid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								// flag = 1;
								s.append(" or bid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}

			}
		}

		List flist = new ArrayList();
		NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
		NetSyslogNodeRule logrule = (NetSyslogNodeRule) ruledao.findByID(String.valueOf(1));
		if (logrule != null) {
			String facility = logrule.getFacility();
			String[] facilitys = facility.split(",");

			if (facilitys != null && facilitys.length > 0) {
				for (int a = 0; a < facilitys.length; a++) {
					flist.add(facilitys[a]);
				}
			}
		}

		flist = (List) request.getAttribute("facilitys");

		request.setAttribute("actionlist", "list");
		setTarget("/alarm/syslog/alarmlist.jsp");
		HostNodeDao dao = new HostNodeDao();
		if (current_user.getRole() == 0) {
			return list(dao, "where 1=1 ");
		} else {
			return list(dao, "where 1=1 " + s);
		}

	}

	private String toolbarsaveall() {
		String ids = request.getParameter("ids");
		String[] bids = ids.split(",");
		String[] pt = getParaArrayValue("fcheckbox");

		if (bids != null && bids.length > 0) {
			// 进行修改
			for (int i = 0; i < bids.length; i++) {
				String hostid = bids[i];
				String pc_str = "";
				if (pt != null && pt.length > 0) {
					for (int j = 0; j < pt.length; j++) {

						String p_t = pt[j];
						pc_str = pc_str + p_t + ",";
					}
					NetSyslogNodeRuleDao ruledao = new NetSyslogNodeRuleDao();
					try {
						ruledao.updateAlarmAll(pc_str, hostid);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						ruledao.close();
					}
				}
			}
		}
		return "/netsyslogalarm.do?action=list";
	}
}
