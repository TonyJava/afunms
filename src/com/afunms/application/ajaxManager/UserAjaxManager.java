package com.afunms.application.ajaxManager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import wfm.encode.MD5;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.system.dao.PositionDao;
import com.afunms.system.dao.RoleDao;
import com.afunms.system.dao.SysLogDao;
import com.afunms.system.dao.UserAuditDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.Department;
import com.afunms.system.model.Position;
import com.afunms.system.model.Role;
import com.afunms.system.model.SysLog;
import com.afunms.system.model.User;
import com.afunms.system.model.UserAudit;

public class UserAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	@Override
	public void execute(String action) {
		if (action.equals("list")) {
			list();
		} else if (action.equals("delete")) {
			delete();
		} else if (action.equals("add")) {
			add();
		} else if (action.equals("getRole")) {
			getRole();
		} else if (action.equals("deleteRole")) {
			deleteRole();
		} else if (action.equals("addRole")) {
			addRole();
		} else if (action.equals("addDept")) {
			addDept();
		} else if (action.equals("addPosition")) {
			addPosition();
		} else if (action.equals("editRole")) {
			editRole();
		} else if (action.equals("editDept")) {
			editDept();
		} else if (action.equals("editPosition")) {
			editPosition();
		} else if (action.equals("getDept")) {
			getDept();
		} else if (action.equals("deleteDept")) {
			deleteDept();
		} else if (action.equals("getPosition")) {
			getPosition();
		} else if (action.equals("deletePosition")) {
			deletePosition();
		} else if (action.equals("beforeEditUser")) {
			beforeEditUser();
		} else if (action.equals("beforeEditRole")) {
			beforeEditRole();
		} else if (action.equals("beforeEditDept")) {
			beforeEditDept();
		} else if (action.equals("beforeEditPosition")) {
			beforeEditPosition();
		} else if (action.equals("editUser")) {
			editUser();
		} else if (action.equals("listUserAudit")) {
			listUserAudit();
		} else if (action.equals("deleteUserAudit")) {
			deleteUserAudit();
		} else if (action.equals("listSystemSyslog")) {
			listSystemSyslog();
		} else if (action.equals("deleteSystemSyslog")) {
			deleteSystemSyslog();
		}
	}

	private void deleteSystemSyslog() {
		String idString = getParaValue("idString");
		String[] ids = null;
		if (idString != null && idString.length() > 0) {
			ids = idString.split(";");
		}
		SysLogDao dao = new SysLogDao();
		boolean result = true;
		if (ids != null && ids.length > 0) {
			result = dao.delete(ids);
		}
		if (result) {
			out.print("删除成功");
		} else {
			out.print("删除失败");
		}
		out.flush();
	}

	private void listSystemSyslog() {
		SysLogDao dao = new SysLogDao();
		List list = new ArrayList();
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		SysLog syslog = new SysLog();
		for (int i = 0; i < list.size(); i++) {
			syslog = (SysLog) list.get(i);
			jsonString.append("{\"id\":\"");
			jsonString.append(syslog.getId());
			jsonString.append("\",");

			jsonString.append("\"user\":\"");
			jsonString.append(syslog.getUser());
			jsonString.append("\",");

			jsonString.append("\"event\":\"");
			jsonString.append(syslog.getEvent());
			jsonString.append("\",");

			jsonString.append("\"time\":\"");
			jsonString.append(syslog.getLogTime());
			jsonString.append("\",");

			jsonString.append("\"ipaddress\":\"");
			jsonString.append(syslog.getIp());
			jsonString.append("\"}");

			if (i != list.size() - 1) {
				jsonString.append(",");
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteUserAudit() {
		UserAuditDao userAuditDao = null;
		boolean result = false;
		String[] ids = null;
		String idString = getParaValue("idString");
		if (idString != null && idString.length() > 0) {
			ids = idString.split(";");
		}
		try {
			userAuditDao = new UserAuditDao();
			if (ids != null && ids.length > 0) {
				result = userAuditDao.delete(ids);
			}
		} catch (Exception e) {
			result = false;
		} finally {
			userAuditDao.close();
		}
		if (result) {
			out.print("删除成功");
		} else {
			out.print("删除失败");
		}
		out.flush();
	}

	public void list() {
		UserDao dao = new UserDao();
		List list = new ArrayList();
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != dao) {
				dao.close();
			}
		}
		Hashtable<Integer, String> roleHt = new Hashtable<Integer, String>();
		RoleDao rdao = new RoleDao();
		try {
			List roleList = rdao.loadAll();
			if (null != roleList && roleList.size() > 0) {
				Role role = new Role();
				for (int i = 0; i < roleList.size(); i++) {
					role = (Role) roleList.get(i);
					roleHt.put(role.getId(), role.getRole());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != rdao) {
				rdao.close();
			}
		}
		Hashtable<Integer, String> positionHt = new Hashtable<Integer, String>();
		PositionDao pdao = new PositionDao();
		try {
			List positionList = pdao.loadAll();
			if (null != positionList && positionList.size() > 0) {
				Position pos = new Position();
				for (int i = 0; i < positionList.size(); i++) {
					pos = (Position) positionList.get(i);
					positionHt.put(pos.getId(), pos.getName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != pdao) {
				pdao.close();
			}
		}

		Hashtable<Integer, String> departmentHt = new Hashtable<Integer, String>();
		DepartmentDao ddao = new DepartmentDao();
		try {
			List deptList = ddao.loadAll();
			if (null != deptList && deptList.size() > 0) {
				Department dept = new Department();
				for (int i = 0; i < deptList.size(); i++) {
					dept = (Department) deptList.get(i);
					departmentHt.put(dept.getId(), dept.getDept());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != ddao) {
				ddao.close();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != list && list.size() > 0) {
			User user = new User();
			for (int i = 0; i < list.size(); i++) {
				user = (User) list.get(i);
				jsonString.append("{\"id\":\"");
				jsonString.append(user.getId());
				jsonString.append("\",");

				jsonString.append("\"userId\":\"");
				jsonString.append(user.getUserid());
				jsonString.append("\",");

				jsonString.append("\"name\":\"");
				jsonString.append(user.getName());
				jsonString.append("\",");

				jsonString.append("\"sex\":\"");
				jsonString.append(user.getSex());
				jsonString.append("\",");

				jsonString.append("\"role\":\"");
				jsonString.append(roleHt.get(user.getRole()));
				jsonString.append("\",");

				jsonString.append("\"dept\":\"");
				jsonString.append(departmentHt.get(user.getDept()));
				jsonString.append("\",");

				jsonString.append("\"position\":\"");
				jsonString.append(positionHt.get(user.getPosition()));
				jsonString.append("\",");

				jsonString.append("\"phone\":\"");
				jsonString.append(user.getPhone());
				jsonString.append("\",");

				jsonString.append("\"email\":\"");
				jsonString.append(user.getEmail());
				jsonString.append("\",");

				jsonString.append("\"mobile\":\"");
				jsonString.append(user.getMobile());
				jsonString.append("\",");

				jsonString.append("\"business\":\"");
				jsonString.append(user.getBusinessids());
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void delete() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		UserDao dao = new UserDao();
		dao.delete(ids);
		out.print("成功删除");
		out.flush();
	}

	private void add() {
		User vo = new User();
		vo.setName(getParaValue("name"));
		vo.setUserid(getParaValue("userId"));
		vo.setSex(1);
		vo.setDept(getParaIntValue("dept"));
		vo.setPosition(getParaIntValue("position"));
		vo.setRole(getParaIntValue("role"));
		vo.setPhone(getParaValue("phone"));
		vo.setMobile(getParaValue("mobile"));
		vo.setEmail(getParaValue("email"));
		vo.setBusinessids(getParaValue("bid"));
		MD5 md = new MD5();
		vo.setPassword(md.getMD5ofStr(getParaValue("password")));
		UserDao dao = new UserDao();
		int result = dao.save(vo);
		if (result == 0) {
			out.print("用户已存在");
			out.flush();
		} else if (result == 1) {
			out.print("添加成功");
			out.flush();
		} else {
			out.print("添加失败");
			out.flush();
		}
	}

	private void beforeEditUser() {
		String id = getParaValue("id");
		User user = new User();
		Role role = new Role();
		Department dept = new Department();
		Position pos = new Position();

		UserDao dao = new UserDao();
		RoleDao roleDao = new RoleDao();
		DepartmentDao deptDao = new DepartmentDao();
		PositionDao positionDao = new PositionDao();
		try {
			user = (User) dao.findByID(id);
			role = (Role) roleDao.findByID(user.getRole() + "");
			dept = (Department) deptDao.findByID(user.getDept() + "");
			pos = (Position) positionDao.findByID(user.getPosition() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != dao) {
				dao.close();
			}
			if (null != roleDao) {
				roleDao.close();
			}
			if (null != deptDao) {
				deptDao.close();
			}
			if (null != positionDao) {
				positionDao.close();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		String rolename = "";
		String roleid = "";
		String positionname = "";
		String positionid = "";
		String deptname = "";
		String deptid = "";
		if (role != null) {
			rolename = role.getRole();
			roleid = role.getId() + "";
		}
		if (pos != null) {
			positionname = pos.getName();
			positionid = pos.getId() + "";
		}
		if (dept != null) {
			deptname = dept.getDept();
			deptid = dept.getId() + "";
		}
		List allbuss = new ArrayList();
		List bidlist = new ArrayList();
		String bid = "";
		BusinessDao bussdao = new BusinessDao();
		try {
			allbuss = bussdao.loadAll();
			bid = user.getBusinessids();
			String ids[] = bid.split(",");
			bidlist = new ArrayList();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					bidlist.add(ids[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bussdao != null) {
				bussdao.close();
			}
		}
		String bussName = "";
		if (allbuss.size() > 0) {
			for (int i = 0; i < allbuss.size(); i++) {
				Business buss = (Business) allbuss.get(i);
				if (bidlist.contains(buss.getId() + "")) {
					bussName = bussName + ',' + buss.getName();
				}
			}
		}
		jsonString.append("{\"id\":\"");
		jsonString.append(user.getId());
		jsonString.append("\",");

		jsonString.append("\"userId\":\"");
		jsonString.append(user.getUserid());
		jsonString.append("\",");

		jsonString.append("\"name\":\"");
		jsonString.append(user.getName());
		jsonString.append("\",");

		jsonString.append("\"sex\":\"");
		jsonString.append(user.getSex());
		jsonString.append("\",");

		jsonString.append("\"role\":\"");
		jsonString.append(rolename);
		jsonString.append("\",");

		jsonString.append("\"roleId\":\"");
		jsonString.append(roleid);
		jsonString.append("\",");

		jsonString.append("\"dept\":\"");
		jsonString.append(deptname);
		jsonString.append("\",");

		jsonString.append("\"deptId\":\"");
		jsonString.append(deptid);
		jsonString.append("\",");

		jsonString.append("\"position\":\"");
		jsonString.append(positionname);
		jsonString.append("\",");

		jsonString.append("\"positionId\":\"");
		jsonString.append(positionid);
		jsonString.append("\",");

		jsonString.append("\"phone\":\"");
		jsonString.append(user.getPhone());
		jsonString.append("\",");

		jsonString.append("\"email\":\"");
		jsonString.append(user.getEmail());
		jsonString.append("\",");

		jsonString.append("\"mobile\":\"");
		jsonString.append(user.getMobile());
		jsonString.append("\",");

		jsonString.append("\"businessId\":\"");
		jsonString.append(user.getBusinessids());
		jsonString.append("\",");

		jsonString.append("\"business\":\"");
		jsonString.append(bussName);
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void editUser() {
		User vo = new User();
		vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setUserid(getParaValue("userId"));
		vo.setDept(getParaIntValue("dept"));
		vo.setPosition(getParaIntValue("position"));
		vo.setRole(getParaIntValue("role"));
		vo.setPhone(getParaValue("phone"));
		vo.setMobile(getParaValue("mobile"));
		vo.setEmail(getParaValue("email"));
		if (getParaValue("bid") == null || getParaValue("bid").equals("notSet") || getParaValue("bid").equals("")) {
			vo.setBusinessids(getParaValue("bids"));
		} else {
			vo.setBusinessids(getParaValue("bid"));
		}
		String pwd = getParaValue("password");
		if (!pwd.equals("") && pwd != null && !pwd.equals("null")) {
			MD5 md = new MD5();
			vo.setPassword(md.getMD5ofStr(pwd));
		} else {
			vo.setPassword(null);
		}

		UserDao dao = new UserDao();
		String target = null;
		if (dao.update(vo)) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void addRole() {
		Role vo = new Role();
		vo.setRole(getParaValue("role"));
		RoleDao dao = new RoleDao();
		if (dao.save(vo)) {
			out.print("添加成功");
		} else {
			out.print("添加失败");
		}
		out.flush();
	}

	private void beforeEditRole() {
		String roleId = getParaValue("id");
		Role role = new Role();
		RoleDao dao = new RoleDao();
		role = (Role) dao.findByID(roleId);
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"roleId\":\"");
		jsonString.append(role.getId());
		jsonString.append("\",");

		jsonString.append("\"role\":\"");
		jsonString.append(role.getRole());
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void editRole() {
		Role vo = new Role();
		vo.setId(getParaIntValue("roleId"));
		vo.setRole(getParaValue("role"));
		RoleDao dao = new RoleDao();
		if (dao.update(vo)) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void getRole() {
		RoleDao dao = new RoleDao();
		List list = new ArrayList();
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		for (int i = 0; i < list.size(); i++) {
			Role role = new Role();
			role = (Role) list.get(i);
			jsonString.append("{\"roleId\":\"");
			jsonString.append(role.getId());
			jsonString.append("\",");

			jsonString.append("\"role\":\"");
			jsonString.append(role.getRole());
			jsonString.append("\"}");

			if (i != list.size() - 1) {
				jsonString.append(",");
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteRole() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		RoleDao dao = new RoleDao();
		dao.delete(ids);
		out.print("成功删除");
		out.flush();
	}

	private void addDept() {
		Department vo = new Department();
		vo.setDept(getParaValue("dept"));
		vo.setMan(getParaValue("man"));
		vo.setTel(getParaValue("tel"));
		DepartmentDao dao = new DepartmentDao();
		if (dao.save(vo)) {
			out.print("添加成功");
		} else {
			out.print("添加失败");
		}
		out.flush();
	}

	private void beforeEditDept() {
		String deptId = getParaValue("id");
		Department dept = new Department();
		DepartmentDao dao = new DepartmentDao();
		dept = (Department) dao.findByID(deptId);
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"deptId\":\"");
		jsonString.append(dept.getId());
		jsonString.append("\",");

		jsonString.append("\"man\":\"");
		jsonString.append(dept.getMan());
		jsonString.append("\",");

		jsonString.append("\"tel\":\"");
		jsonString.append(dept.getTel());
		jsonString.append("\",");

		jsonString.append("\"dept\":\"");
		jsonString.append(dept.getDept());
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void editDept() {
		Department vo = new Department();
		vo.setId(getParaIntValue("deptId"));
		vo.setDept(getParaValue("dept"));
		vo.setMan(getParaValue("man"));
		vo.setTel(getParaValue("tel"));
		DepartmentDao dao = new DepartmentDao();
		if (dao.update(vo)) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void getDept() {
		DepartmentDao dao = new DepartmentDao();
		List list = new ArrayList();
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (dao != null) {
				dao.close();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		for (int i = 0; i < list.size(); i++) {
			Department dept = new Department();
			dept = (Department) list.get(i);
			jsonString.append("{\"deptId\":\"");
			jsonString.append(dept.getId());
			jsonString.append("\",");

			jsonString.append("\"man\":\"");
			jsonString.append(dept.getMan());
			jsonString.append("\",");

			jsonString.append("\"tel\":\"");
			jsonString.append(dept.getTel());
			jsonString.append("\",");

			jsonString.append("\"dept\":\"");
			jsonString.append(dept.getDept());
			jsonString.append("\"}");

			if (i != list.size() - 1) {
				jsonString.append(",");
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteDept() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		DepartmentDao dao = new DepartmentDao();
		dao.delete(ids);
		out.print("成功删除");
		out.flush();
	}

	private void addPosition() {
		Position vo = new Position();
		vo.setName(getParaValue("position"));
		PositionDao dao = new PositionDao();
		if (dao.save(vo)) {
			out.print("添加成功");
		} else {
			out.print("添加失败");
		}
		out.flush();
	}

	private void beforeEditPosition() {
		String positionId = getParaValue("id");
		Position position = new Position();
		PositionDao dao = new PositionDao();
		position = (Position) dao.findByID(positionId);
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"positionId\":\"");
		jsonString.append(position.getId());
		jsonString.append("\",");

		jsonString.append("\"position\":\"");
		jsonString.append(position.getName());
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void editPosition() {
		Position vo = new Position();
		vo.setId(getParaIntValue("positionId"));
		;
		vo.setName(getParaValue("position"));
		PositionDao dao = new PositionDao();
		if (dao.update(vo)) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void getPosition() {
		PositionDao dao = new PositionDao();
		List list = new ArrayList();
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		for (int i = 0; i < list.size(); i++) {
			Position position = new Position();
			position = (Position) list.get(i);
			jsonString.append("{\"positionId\":\"");
			jsonString.append(position.getId());
			jsonString.append("\",");

			jsonString.append("\"position\":\"");
			jsonString.append(position.getName());
			jsonString.append("\"}");

			if (i != list.size() - 1) {
				jsonString.append(",");
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deletePosition() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		PositionDao dao = new PositionDao();
		dao.delete(ids);
		out.print("成功删除");
		out.flush();
	}

	private void listUserAudit() {
		UserAuditDao dao = new UserAuditDao();
		UserDao userdao = new UserDao();
		List list = new ArrayList();
		List userList = new ArrayList();
		try {
			list = dao.loadAll();
			userList = userdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
			if (userdao != null) {
				userdao.close();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		User user = null;
		for (int i = 0; i < list.size(); i++) {
			UserAudit ua = new UserAudit();
			ua = (UserAudit) list.get(i);
			String username = "";
			for (int j = 0; j < userList.size(); j++) {
				user = (User) userList.get(j);
				if (user.getId() == ua.getUserId()) {
					username = user.getName();
				}
				user = null;
			}
			jsonString.append("{\"id\":\"");
			jsonString.append(ua.getId());
			jsonString.append("\",");

			jsonString.append("\"username\":\"");
			jsonString.append(username);
			jsonString.append("\",");

			jsonString.append("\"action\":\"");
			jsonString.append(ua.getAction());
			jsonString.append("\",");

			jsonString.append("\"time\":\"");
			jsonString.append(ua.getTime());
			jsonString.append("\"}");

			if (i != list.size() - 1) {
				jsonString.append(",");
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}
}
