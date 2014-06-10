package com.afunms.system.manage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.UserAuditDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.UserAudit;

@SuppressWarnings("unchecked")
public class UserAuditManager extends BaseManager implements ManagerInterface {

	private String add() {
		UserAuditDao userAuditDao = null;
		boolean result = false;
		try {
			userAuditDao = new UserAuditDao();
			UserAudit userAudit = createUserAudit();
			result = userAuditDao.save(userAudit);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			userAuditDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/system/useraudit/fail.jsp";
		}

	}

	private UserAudit createUserAudit() {
		int userId = getParaIntValue("userid");
		String action = getParaValue("operation");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		Date date = new Date();
		UserAudit userAudit = new UserAudit();
		userAudit.setUserId(userId);
		userAudit.setAction(action);
		userAudit.setTime(simpleDateFormat.format(date));
		return userAudit;
	}

	private String delete() {
		UserAuditDao userAuditDao = null;
		boolean result = false;
		try {
			userAuditDao = new UserAuditDao();
			String[] ids = request.getParameterValues("checkbox");
			if (ids != null && ids.length > 0) {
				result = userAuditDao.delete(ids);
			}
		} catch (Exception e) {
			result = false;
		} finally {
			userAuditDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/system/useraudit/fail.jsp";
		}

	}

	public String execute(String action) {
		if ("list".equals(action)) {
			return list();
		} else if ("delete".equals(action)) {
			return delete();
		} else if ("ready_add".equals(action)) {
			return ready_add();
		} else if ("add".equals(action)) {
			return add();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String list() {
		UserAuditDao userAuditDao = null;
		String jsp = null;
		try {
			userAuditDao = new UserAuditDao();
			setTarget("/system/useraudit/list.jsp");
			jsp = list(userAuditDao, "order by time desc");
		} catch (Exception e) {

		} finally {
			userAuditDao.close();
		}
		List userList = null;
		UserDao userDao = new UserDao();
		try {
			userList = userDao.loadAll();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			userDao.close();
		}
		request.setAttribute("userList", userList);
		return jsp;
	}

	private String ready_add() {
		return "/system/useraudit/add.jsp";
	}

}
