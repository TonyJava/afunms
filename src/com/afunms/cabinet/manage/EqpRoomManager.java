package com.afunms.cabinet.manage;

import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.util.CabinetXML;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;

public class EqpRoomManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			DaoInterface dao = new EqpRoomDao();
			setTarget("/cabinet/eqproom/list.jsp");
			String isTreeView = request.getParameter("isTreeView");
			request.setAttribute("isTreeView", isTreeView);
			return list(dao);
		}
		if (action.equals("ready_add")) {
			String isTreeView = request.getParameter("isTreeView");
			request.setAttribute("isTreeView", isTreeView);
			return "/cabinet/eqproom/add.jsp";
		}
		if (action.equals("add")) {
			EqpRoom vo = new EqpRoom();
			vo.setName(getParaValue("name"));
			vo.setDescr(getParaValue("descr"));
			vo.setBak(getParaValue("bak"));
			CabinetXML cxml = new CabinetXML();
			cxml.CreateCabinetXML();
			DaoInterface dao = new EqpRoomDao();
			setTarget("/eqproom.do?action=list");
			return save(dao, vo);
		}
		if (action.equals("delete")) {
			DaoInterface dao = new EqpRoomDao();
			CabinetXML cxml = new CabinetXML();
			cxml.CreateCabinetXML();
			setTarget("/eqproom.do?action=list");
			return delete(dao);
		}
		if (action.equals("update")) {
			EqpRoom vo = new EqpRoom();
			vo.setId(getParaIntValue("id"));
			vo.setName(getParaValue("name"));
			vo.setDescr(getParaValue("descr"));
			vo.setBak(getParaValue("bak"));
			DaoInterface dao = new EqpRoomDao();
			CabinetXML cxml = new CabinetXML();
			cxml.CreateCabinetXML();
			setTarget("/eqproom.do?action=list");
			return update(dao, vo);
		}
		if (action.equals("ready_edit")) {
			String isTreeView = request.getParameter("isTreeView");
			request.setAttribute("isTreeView", isTreeView);
			DaoInterface dao = new EqpRoomDao();

			setTarget("/cabinet/eqproom/edit.jsp");
			return readyEdit(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
