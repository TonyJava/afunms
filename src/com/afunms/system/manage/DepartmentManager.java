package com.afunms.system.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.system.model.Department;

public class DepartmentManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {

			DaoInterface dao = new DepartmentDao();
			setTarget("/system/department/list.jsp");
			return list(dao);
		}
		if (action.equals("ready_add")) {
			return "/system/department/add.jsp";
		}
		if (action.equals("add")) {
			Department vo = new Department();
			vo.setDept(getParaValue("dept"));
			vo.setMan(getParaValue("man"));
			vo.setTel(getParaValue("tel"));
			DaoInterface dao = new DepartmentDao();
			setTarget("/dept.do?action=list");
			return save(dao, vo);
		}
		if (action.equals("delete")) {
			DaoInterface dao = new DepartmentDao();
			setTarget("/dept.do?action=list");
			return delete(dao);
		}
		if (action.equals("update")) {
			Department vo = new Department();
			vo.setId(getParaIntValue("id"));
			vo.setDept(getParaValue("dept"));
			vo.setMan(getParaValue("man"));
			vo.setTel(getParaValue("tel"));

			DaoInterface dao = new DepartmentDao();
			setTarget("/dept.do?action=list");
			return update(dao, vo);
		}
		if (action.equals("ready_edit")) {
			DaoInterface dao = new DepartmentDao();
			setTarget("/system/department/edit.jsp");
			return readyEdit(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

}
