package com.afunms.inform.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SysUtil;
import com.afunms.inform.dao.AlarmDao;

@SuppressWarnings("unchecked")
public class AlarmManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list")) {
			AlarmDao dao = new AlarmDao();
			String snap = getParaValue("snap");

			if (snap == null) {
				request.setAttribute("list", dao.listByPage(getCurrentPage()));
				request.setAttribute("page", dao.getPage());
			} else {
				request.setAttribute("list", dao.listByCategory(snap, SysUtil.getCurrentDate()));
			}
			if (snap == null) {
				return "/inform/alarm/list.jsp";
			} else {
				return "/inform/alarm/list_one.jsp";
			}
		}
		if (action.equals("detail")) {
			AlarmDao dao = new AlarmDao();
			List list = dao.findByIP(getParaValue("ip"));

			request.setAttribute("list", list);
			return "/inform/alarm/list_one.jsp";
		}
		if (action.equals("delete")) {
			DaoInterface dao = new AlarmDao();
			setTarget("/alarm.do?action=list");
			return delete(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}