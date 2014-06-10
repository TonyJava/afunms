package com.afunms.ip.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.ip.stationtype.dao.backbonestorageDao;
import com.afunms.ip.stationtype.dao.bussinessstorageDao;
import com.afunms.ip.stationtype.dao.bussinesstypeDao;
import com.afunms.ip.stationtype.dao.encryptiontypeDao;
import com.afunms.ip.stationtype.model.bussinesstype;

@SuppressWarnings("unchecked")
public class ipBussinessManager extends BaseManager implements ManagerInterface {

	public String add() {
		bussinesstypeDao ipconfigdao = new bussinesstypeDao();
		bussinesstype vo = new bussinesstype();
		String name = request.getParameter("addzt");
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

	public String backbonelist() {
		DaoInterface ip_backbone = new backbonestorageDao();
		setTarget("");
		return list(ip_backbone);
	}

	public String delete() {
		DaoInterface ipconfigdao = new bussinesstypeDao();
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
		if (action.equals("ready_add")) {
			return ready_add();
		}
		return null;
	}

	public String list() {
		DaoInterface ipconfigdao = new bussinesstypeDao();
		setTarget("/ipconfig/bussinesstype/bussinesstypelist.jsp");
		return list(ipconfigdao);
	}

	public String ready_add() {
		DaoInterface ip_backbone = new backbonestorageDao();
		List backbonelist = ip_backbone.loadAll();
		DaoInterface ip_bussinessStorage = new bussinessstorageDao();
		List bussinesslist = ip_bussinessStorage.loadAll();
		DaoInterface encryption = new encryptiontypeDao();
		DaoInterface bussinesstype = new bussinesstypeDao();
		List bussiness = bussinesstype.loadAll();
		request.setAttribute("backbonelist", backbonelist);
		request.setAttribute("bussinesslist", bussinesslist);
		request.setAttribute("bussiness", bussiness);
		request.setAttribute("encryption", encryption);
		return "/ipconfig/bussinessstorage/add.jsp";
	}

	public String ready_edit() {
		DaoInterface ipconfigdao = new bussinesstypeDao();
		String id1 = request.getParameter("id");
		bussinesstype list = (bussinesstype) ipconfigdao.findByID(id1);
		request.setAttribute("list", list);
		return "/ipconfig/bussinesstype/edit.jsp";
	}

	protected String update() {
		bussinesstype vo = new bussinesstype();
		String name = request.getParameter("name");
		String id1 = request.getParameter("id");
		String descr = request.getParameter("descr");
		String bak = request.getParameter("bak");
		int id = Integer.parseInt(id1);
		vo.setBak(bak);
		vo.setDescr(descr);
		vo.setId(id);
		vo.setName(name);
		DaoInterface dao = new bussinesstypeDao();
		dao.update(vo);
		return list();
	}

}
