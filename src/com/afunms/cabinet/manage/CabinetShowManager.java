package com.afunms.cabinet.manage;

import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;

public class CabinetShowManager extends BaseManager implements ManagerInterface {
	public String execute(String action) {
		if (action.equals("list")) {
			String roomId = getParaValue("roomId");
			if (roomId == null || "".equals(roomId)) {
				roomId = "1";
			}
			request.setAttribute("roomId", roomId);
			return "/cabinet/cabinetshow/list.jsp";
		}
		if (action.equals("list3d")) {
			String roomId = getParaValue("roomId");
			request.setAttribute("roomId", roomId);
			return "/cabinet/cabinetshow/list.jsp";
		}
		if (action.equals("ready_add"))
			return "/cabinet/eqproom/add.jsp";
		if (action.equals("add")) {
			EqpRoom vo = new EqpRoom();
			vo.setName(getParaValue("name"));
			vo.setDescr(getParaValue("descr"));
			vo.setBak(getParaValue("bak"));
			DaoInterface dao = new EqpRoomDao();
			setTarget("/eqproom.do?action=list");
			return save(dao, vo);
		}
		if (action.equals("delete")) {
			DaoInterface dao = new EqpRoomDao();
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
			setTarget("/eqproom.do?action=list");
			return update(dao, vo);
		}
		if (action.equals("ready_edit")) {
			DaoInterface dao = new EqpRoomDao();
			setTarget("/cabinet/eqproom/edit.jsp");
			return readyEdit(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
