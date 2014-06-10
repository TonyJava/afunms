package com.afunms.business.manage;

import com.afunms.business.dao.BusCollectTypeDao;
import com.afunms.business.model.BusCollectType;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;

public class BusCollectTypeManage extends BaseManager implements ManagerInterface {

	public String list() {
		BusCollectTypeDao dao = new BusCollectTypeDao();
		setTarget("/business/buscollecttype/list.jsp");
		return list(dao);
	}

	private String update() {
		BusCollectType vo = new BusCollectType();
		vo.setId(getParaIntValue("id"));
		vo.setCollecttype(getParaValue("collecttype"));
		vo.setBct_desc(getParaValue("bctdesc"));
		BusCollectTypeDao dao = new BusCollectTypeDao();
		String target = null;
		try {
			if (dao.update(vo))
				target = "/buscolltype.do?action=list";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}

	private String save() {
		BusCollectType vo = new BusCollectType();
		vo.setCollecttype(getParaValue("collecttype"));
		vo.setBct_desc(getParaValue("desc"));
		BusCollectTypeDao dao = new BusCollectTypeDao();
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/buscolltype.do?action=list&jp=1";
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();

		if (action.equals("add"))
			return save();

		if (action.equals("delete")) {
			DaoInterface dao = new BusCollectTypeDao();
			setTarget("/buscolltype.do?action=list&jp=1");
			return delete(dao);
		}
		if (action.equals("ready_add")) {
			return "/business/buscollecttype/add.jsp";
		}

		if (action.equals("ready_edit")) {
			DaoInterface dao = new BusCollectTypeDao();
			setTarget("/business/buscollecttype/edit.jsp");
			return readyEdit(dao);
		}
		if (action.equals("update")) {
			return update();
		}
		return null;
	}

}
