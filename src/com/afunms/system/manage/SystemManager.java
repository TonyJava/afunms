package com.afunms.system.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.dao.DepartmentDao;
import com.afunms.system.dao.FunctionDao;
import com.afunms.system.model.Department;
import com.afunms.system.model.Function;
import com.afunms.system.model.User;
import com.afunms.system.util.CreateRoleFunctionTable;

public class SystemManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			Function root = null;
			FunctionDao functionDao = null;
			try {
				functionDao = new FunctionDao();
				root = (Function) functionDao.findByID("70");
			} catch (Exception e) {

			} finally {
				functionDao.close();
			}

			CreateRoleFunctionTable crft = new CreateRoleFunctionTable();
			List<Function> functionRoleList = crft.getRoleFunctionListByRoleId(String.valueOf(user.getRole()));
			List<Function> functionList = crft.getAllFuctionChildByRoot(root, functionRoleList);
			request.setAttribute("root", root);
			request.setAttribute("functionList", functionList);
			return "/system/manage/list.jsp";
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
