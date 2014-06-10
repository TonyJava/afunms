package com.afunms.cabinet.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.cabinet.dao.EqpRoomDao;
import com.afunms.cabinet.dao.MachineCabinetDao;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.MachineCabinet;
import com.afunms.cabinet.util.CabinetXML;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.topology.util.KeyGenerator;

@SuppressWarnings("unchecked")
public class MachineCabinetManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("edit")) {
			return edit();
		}
		if (action.equals("ready_add")) {
			return ready_add();
		}
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("toDetail")) {
			return toDetail();
		}
		if (action.equals("loadByRoomID")) {
			return loadByRoomID();
		}
		if (action.equals("delete")) {
			String jsp = "/maccabinet.do?action=list";
			String[] id = getParaArrayValue("checkbox");
			MachineCabinetDao dao = new MachineCabinetDao();
			try {
				dao.delete(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			CabinetXML cxml = new CabinetXML();
			cxml.CreateCabinetXML();
			return jsp;
		}
		if (action.equals("toSelect")) {
			return toSelect();
		}
		return null;
	}

	private String loadByRoomID() {
		String id = request.getParameter("id");
		request.setAttribute("id", id);
		String isTreeView = request.getParameter("isTreeView");
		request.setAttribute("isTreeView", isTreeView);
		String jsp = "/cabinet/cabinetconfig/list.jsp";
		setTarget(jsp);
		EqpRoomDao roomdao = new EqpRoomDao();
		Hashtable roomHash = new Hashtable();
		List roomlist = new ArrayList();
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {

		} finally {
			roomdao.close();
		}
		if (roomlist != null && roomlist.size() > 0) {
			for (int i = 0; i < roomlist.size(); i++) {
				EqpRoom room = (EqpRoom) roomlist.get(i);
				roomHash.put(room.getId() + "", room.getName());
			}
		}
		request.setAttribute("roomhash", roomHash);
		MachineCabinetDao dao = new MachineCabinetDao();
		return list(dao, " where motorroom = " + id);
	}

	public String list() {

		String jsp = "/cabinet/cabinetconfig/list.jsp";
		String isTreeView = request.getParameter("isTreeView");
		request.setAttribute("isTreeView", isTreeView);
		setTarget(jsp);
		EqpRoomDao roomdao = new EqpRoomDao();
		Hashtable roomHash = new Hashtable();
		List roomlist = new ArrayList();
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {

		} finally {
			roomdao.close();
		}
		if (roomlist != null && roomlist.size() > 0) {
			for (int i = 0; i < roomlist.size(); i++) {
				EqpRoom room = (EqpRoom) roomlist.get(i);
				roomHash.put(room.getId() + "", room.getName());
			}
		}

		request.setAttribute("roomhash", roomHash);
		MachineCabinetDao dao = new MachineCabinetDao();
		return list(dao);
	}

	public String edit() {
		MachineCabinet vo = new MachineCabinet();
		vo.setId(getParaIntValue("id"));
		vo.setName(getParaValue("name"));
		vo.setMotorroom(getParaValue("motorroom"));
		vo.setMachinex(getParaValue("machinex"));
		vo.setMachiney(getParaValue("machiney"));
		vo.setMachinez(getParaValue("machinez"));
		vo.setUselect(getParaValue("uselect"));
		vo.setStandards(getParaValue("standards"));
		vo.setPowers(getParaValue("powers"));
		vo.setHeights(getParaValue("heights"));
		vo.setWidths(getParaValue("widths"));
		vo.setDepths(getParaValue("depths"));
		vo.setNos(getParaValue("nos"));
		MachineCabinetDao machineCabinetDao = new MachineCabinetDao();
		try {
			machineCabinetDao.update(vo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			machineCabinetDao.close();
		}
		CabinetXML cxml = new CabinetXML();
		cxml.CreateCabinetXML();
		return list();
	}

	/**
	 * 机房管理 编辑
	 * 
	 * @return
	 */
	private String ready_edit() {
		String isTreeView = request.getParameter("isTreeView");
		request.setAttribute("isTreeView", isTreeView);
		String jsp = "/cabinet/cabinetconfig/edit.jsp";
		MachineCabinetDao dao = new MachineCabinetDao();
		MachineCabinet vo = null;
		try {
			vo = (MachineCabinet) dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);

		EqpRoomDao roomdao = new EqpRoomDao();
		List roomlist = new ArrayList();
		try {
			roomlist = roomdao.loadAll();
		} catch (Exception e) {

		} finally {
			roomdao.close();
		}
		Hashtable roomhash = new Hashtable();
		if (roomlist != null && roomlist.size() > 0) {
			EqpRoom room = null;
			for (int i = 0; i < roomlist.size(); i++) {
				room = (EqpRoom) roomlist.get(i);
				roomhash.put(room.getId(), room.getName());
			}
		}
		request.setAttribute("roomhash", roomhash);
		request.setAttribute("list", roomlist);
		return jsp;
	}

	/**
	 * 机房管理 详细信息
	 * 
	 * @return
	 */
	private String toDetail() {
		String isTreeView = request.getParameter("isTreeView");
		request.setAttribute("isTreeView", isTreeView);
		String jsp = "/cabinet/cabinetconfig/read.jsp";
		MachineCabinetDao dao = new MachineCabinetDao();
		MachineCabinet vo = null;
		try {
			vo = (MachineCabinet) dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("vo", vo);
		return jsp;
	}

	public String ready_add() {
		EqpRoomDao dao = new EqpRoomDao();
		List roomlist = new ArrayList();
		String isTreeView = request.getParameter("isTreeView");
		request.setAttribute("isTreeView", isTreeView);
		try {
			roomlist = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("list", roomlist);
		return "/cabinet/cabinetconfig/add.jsp";

	}

	public String add() {
		MachineCabinet vo = new MachineCabinet();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setName(getParaValue("name"));
		vo.setMotorroom(getParaValue("motorroom"));
		vo.setMachinex(getParaValue("machinex"));
		vo.setMachiney(getParaValue("machiney"));
		vo.setMachinez(getParaValue("machinez"));
		vo.setUselect(getParaValue("uselect"));
		vo.setStandards(getParaValue("standards"));
		vo.setPowers(getParaValue("powers"));
		vo.setHeights(getParaValue("heights"));
		vo.setWidths(getParaValue("widths"));
		vo.setDepths(getParaValue("depths"));
		vo.setNos(getParaValue("nos"));

		MachineCabinetDao machineCabinetDao = new MachineCabinetDao();
		try {
			machineCabinetDao.save(vo);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			machineCabinetDao.close();
		}
		CabinetXML cxml = new CabinetXML();
		cxml.CreateCabinetXML();
		return list();
	}

	public String toSelect() {

		String jsp = "/cabinet/cabinetconfig/select.jsp";
		List list = new ArrayList();
		MachineCabinetDao dao = new MachineCabinetDao();

		try {
			list = dao.selectById(getParaValue("roomid"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("list", list);
		return jsp;
	}
}