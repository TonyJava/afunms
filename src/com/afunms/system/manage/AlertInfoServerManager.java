package com.afunms.system.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.AlertInfoServerDao;
import com.afunms.system.model.AlertInfoServer;

@SuppressWarnings("unchecked")
public class AlertInfoServerManager extends BaseManager implements ManagerInterface {

	private String addalert() {
		AlertInfoServer vo = new AlertInfoServer();
		AlertInfoServerDao configdao = new AlertInfoServerDao();
		try {
			vo = (AlertInfoServer) configdao.findByID(getParaValue("id"));
			vo.setFlag(1);
			configdao = new AlertInfoServerDao();
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/alertinfo.do?action=list";
	}

	private String cancelalert() {
		AlertInfoServer vo = new AlertInfoServer();
		AlertInfoServerDao configdao = new AlertInfoServerDao();
		try {
			vo = (AlertInfoServer) configdao.findByID(getParaValue("id"));
			vo.setFlag(0);
			configdao = new AlertInfoServerDao();
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/alertinfo.do?action=list";
	}

	public String execute(String action) {
		if (action.equals("ready_add")) {
			return "/system/alertinfoserver/add.jsp";
		}
		if (action.equals("add")) {
			return save();
		}
		if (action.equals("update")) {
			return update();
		}

		if (action.equals("list")) {
			return list();
		}
		if (action.equals("addalert")) {
			return addalert();
		}
		if (action.equals("cancelalert")) {
			return cancelalert();
		}
		if (action.equals("delete")) {
			DaoInterface dao = new AlertInfoServerDao();
			setTarget("/alertinfo.do?action=list");
			return delete(dao);
		}
		if (action.equals("ready_edit")) {
			DaoInterface dao = new AlertInfoServerDao();
			setTarget("/system/alertinfoserver/edit.jsp");
			return readyEdit(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String list() {
		AlertInfoServerDao configdao = new AlertInfoServerDao();
		List list = configdao.loadAll();
		request.setAttribute("list", list);
		return "/system/alertinfoserver/list.jsp";
	}

	private String save() {
		AlertInfoServer vo = new AlertInfoServer();
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setPort(getParaValue("port"));
		vo.setDesc(getParaValue("desc"));
		vo.setFlag(getParaIntValue("flag"));
		AlertInfoServerDao dao = new AlertInfoServerDao();
		dao.save(vo);
		return "/alertinfo.do?action=list";
	}

	private String update() {
		AlertInfoServer vo = new AlertInfoServer();
		vo.setId(getParaIntValue("id"));
		vo.setIpaddress(getParaValue("ipaddress"));
		vo.setPort(getParaValue("port"));
		vo.setDesc(getParaValue("desc"));
		vo.setFlag(getParaIntValue("flag"));
		AlertInfoServerDao dao = new AlertInfoServerDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/alertinfo.do?action=list";
	}
}
