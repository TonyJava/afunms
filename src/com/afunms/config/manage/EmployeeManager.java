package com.afunms.config.manage;

import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.config.dao.EmployeeDao;
import com.afunms.config.model.Employee;

@SuppressWarnings("unchecked")
public class EmployeeManager extends BaseManager implements ManagerInterface {
	public void createLinexmlfile(Hashtable lineHash) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("line");
			chartxml.AddLineXML(lineHash);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createxmlfile(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("pie");
			chartxml.AddXML(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String execute(String action) {
		if (action.equals("ready_add")) {
			return readyAdd();
		}
		if (action.equals("add")) {
			return save();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("list")) {
			DaoInterface dao = new EmployeeDao();
			setTarget("/config/employee/list.jsp");
			return list(dao);
		}
		if (action.equals("delete")) {
			DaoInterface dao = new EmployeeDao();
			setTarget("/employee.do?action=list");
			return delete(dao);
		}
		if (action.equals("ready_edit")) {
			DaoInterface dao = new EmployeeDao();
			setTarget("/config/employee/edit.jsp");
			return readyEdit(dao);
		}
		if (action.equals("read")) {
			DaoInterface dao = new EmployeeDao();
			setTarget("/config/employee/read.jsp");
			return readyEdit(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String readyAdd() {
		return "/config/employee/add.jsp";
	}

	private String save() {
		Employee vo = new Employee();
		vo.setName(getParaValue("name"));
		vo.setSex(getParaIntValue("sex"));
		vo.setDept(getParaIntValue("dept"));
		vo.setPosition(getParaIntValue("position"));
		vo.setPhone(getParaValue("phone"));
		vo.setMobile(getParaValue("mobile"));
		vo.setEmail(getParaValue("email"));
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			String ids_str = ",";
			if (ids.length == 1) {
				vo.setBusinessids("," + ids[0] + ",");
			} else {
				for (int i = 0; i < ids.length; i++) {
					ids_str = ids_str + ids[i] + ",";
				}
				vo.setBusinessids(ids_str);
			}
		}
		EmployeeDao dao = new EmployeeDao();
		int result = dao.save(vo);

		String target = null;
		if (result == 0) {
			target = null;
			setErrorCode(ErrorMessage.USER_EXIST);
		} else if (result == 1) {
			target = "/employee.do?action=list";
		} else {
			target = null;
		}
		return target;
	}

	private String update() {
		Employee vo = new Employee();
		vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setId(getParaIntValue("id"));
		vo.setSex(getParaIntValue("sex"));
		vo.setDept(getParaIntValue("dept"));
		vo.setPosition(getParaIntValue("position"));
		vo.setPhone(getParaValue("phone"));
		vo.setMobile(getParaValue("mobile"));
		vo.setEmail(getParaValue("email"));
		EmployeeDao dao = new EmployeeDao();
		String target = null;
		if (dao.update(vo)) {
			target = "/employee.do?action=list";
		}
		return target;
	}
}
