package com.afunms.ip.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.ip.stationtype.dao.stationtypeDao;
import com.afunms.ip.stationtype.model.stationtype;

public class ipStationManager extends BaseManager implements ManagerInterface {

	public String add() {
		stationtypeDao ipconfigdao = new stationtypeDao();
		stationtype vo = new stationtype();
		String name = request.getParameter("name");
		String descr = request.getParameter("descr");
		String bak = request.getParameter("bak");
		vo.setName(name);
		vo.setDescr(descr);
		vo.setBak(bak);
		try {
			ipconfigdao.saveCZ(vo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ipconfigdao.close();
		}
		return list();
	}

	public String delete() {
		DaoInterface ipconfigdao = new stationtypeDao();
		String[] id = request.getParameterValues("checkbox");
		ipconfigdao.delete(id);
		return list();
	}

	public String execute(String action) {
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("update")) {
			return update();
		}
		return null;
	}

	public String list() {
		DaoInterface ipconfigdao = new stationtypeDao();
		setTarget("/ipconfig/stationtype/stationtypelist.jsp");
		return list(ipconfigdao);
	}

	public String ready_edit() {
		DaoInterface ipconfigdao = new stationtypeDao();
		String id1 = request.getParameter("id");
		stationtype list = (stationtype) ipconfigdao.findByID(id1);
		request.setAttribute("list", list);
		return "/ipconfig/stationtype/edit.jsp";
	}

	protected String update() {
		stationtype vo = new stationtype();
		String name = request.getParameter("name");
		String id1 = request.getParameter("id");
		String descr = request.getParameter("descr");
		String bak = request.getParameter("bak");
		int id = Integer.parseInt(id1);
		vo.setBak(bak);
		vo.setDescr(descr);
		vo.setId(id);
		vo.setName(name);
		DaoInterface dao = new stationtypeDao();
		dao.update(vo);
		return list();
	}

}
