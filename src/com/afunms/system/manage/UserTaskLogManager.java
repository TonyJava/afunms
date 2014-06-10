package com.afunms.system.manage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.dao.UserTaskLogDao;
import com.afunms.system.model.User;
import com.afunms.system.model.UserTaskLog;

public class UserTaskLogManager extends BaseManager implements ManagerInterface {
	private String add() {
		UserTaskLogDao userTaskLogDao = null;
		boolean result = false;
		try {
			userTaskLogDao = new UserTaskLogDao();
			UserTaskLog userTaskLog = createUserTaskLog();
			result = userTaskLogDao.save(userTaskLog);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			userTaskLogDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/system/usertasklog/saveFail.jsp";
		}

	}

	private UserTaskLog createUserTaskLog() {
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String id = request.getParameter("id");
		String currentDate = request.getParameter("date");
		if (currentDate == null || currentDate == "") {
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			currentDate = sdf.format(date);
		}
		String taskLogContent = request.getParameter("content");
		int userId = user.getId();
		UserTaskLog userTaskLog = new UserTaskLog();
		if (id != null && (!"".equals(id.trim()))) {
			userTaskLog.setId(Integer.valueOf(id));
		}
		userTaskLog.setUserId(userId);
		userTaskLog.setContent(taskLogContent);
		userTaskLog.setTime(currentDate);
		return userTaskLog;
	}

	private String delete() {
		String id = request.getParameter("id");
		UserTaskLogDao userTaskLogDao = null;
		boolean result = false;
		try {
			userTaskLogDao = new UserTaskLogDao();
			result = userTaskLogDao.deleteById(id);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			userTaskLogDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/system/usertasklog/saveFail.jsp";
		}
	}

	private String edit() {
		UserTaskLogDao userTaskLogDao = null;
		boolean result = false;
		try {
			userTaskLogDao = new UserTaskLogDao();
			UserTaskLog userTaskLog = createUserTaskLog();
			result = userTaskLogDao.update(userTaskLog);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			userTaskLogDao.close();
		}
		if (result) {
			return list();
		} else {
			return "/system/usertasklog/saveFail.jsp";
		}
	}

	public String execute(String action) {
		if ("list".equals(action)) {
			return list();
		} else if ("add".equals(action)) {
			return add();
		} else if ("delete".equals(action)) {
			return delete();
		} else if ("edit".equals(action)) {
			return edit();
		} else if ("listType".equals(action)) {
			return listType();
		}
		return null;
	}

	private String list() {
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		UserTaskLogDao userTaskLogDao = null;
		boolean result = false;
		List<UserTaskLog> userTaskLogList = null;
		try {
			userTaskLogDao = new UserTaskLogDao();
			userTaskLogList = userTaskLogDao.findByUserId(String.valueOf(user.getId()));
			result = true;
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		} finally {
			userTaskLogDao.close();
		}
		request.setAttribute("userTaskLogList", userTaskLogList);
		if (result) {
			return "/common/usertasklog/list.jsp";
		} else {
			return "/system/usertasklog/saveFail.jsp";
		}

	}

	private String listType() {
		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		UserTaskLogDao dao = new UserTaskLogDao();
		setTarget("/common/usertasklog/listType.jsp");
		return super.list(dao, " where userid = " + user.getId() + " order by time desc");
	}
}
