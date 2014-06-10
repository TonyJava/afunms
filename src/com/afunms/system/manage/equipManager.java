package com.afunms.system.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.system.dao.UserDao;
import com.afunms.system.dao.equipDao;
import com.afunms.system.model.equip;

@SuppressWarnings("unchecked")
public class equipManager extends BaseManager implements ManagerInterface {
	public void createequipXmlfile(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("equip");
			chartxml.addequipXML(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createEquipXmlfile(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("jianshi");
			chartxml.addEquipXML(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createOneXmlfile(String filename, String name, String id, String i) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("yewu" + i);
			chartxml.addViewXML(filename, name, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 业务视图权限控制xml
	public void createxmlfile(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("yewu");
			chartxml.addViewXML(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String execute(String action) {
		if (action == null) {
			action = "";
		}

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
			DaoInterface dao = new equipDao();
			setTarget("/system/equip_pic/list.jsp");
			return list(dao);
		}
		if (action.equals("delete")) {
			boolean result = false;
			String jsp = "/equip.do?action=list";
			String[] id = getParaArrayValue("checkbox");
			UserDao dao = new UserDao();
			try {
				result = dao.delete(id);
				if (result) {
					equipDao userAuditDao = new equipDao();
					try {
						for (int i = 0; i < id.length; i++) {
							userAuditDao.deleteById(id[i]);
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						userAuditDao.close();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			return jsp;
		}
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("read")) {
			return read();
		}

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String read() {
		String targetJsp = "/system/equip_pic/read.jsp";
		equip vo = null;
		equipDao dao = new equipDao();
		try {
			vo = (equip) dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return targetJsp;
	}

	private String ready_edit() {

		String targetJsp = "/system/equip_pic/editEquip.jsp";
		equip vo = null;
		equipDao dao = new equipDao();
		try {
			vo = (equip) dao.findByID(getParaValue("id"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return targetJsp;
	}

	private String readyAdd() {

		String result = request.getParameter("result");
		if (result != null && "".equals(result)) {
			request.setAttribute("result", "1");
		}
		equipDao equip = new equipDao();
		List list = equip.query("select category,cn_name,en_name from topo_equip_pic group by category,cn_name,en_name order by category asc");
		Map map = new HashMap();
		equip vo = null;

		for (int i = 0; i < list.size(); i++) {
			vo = (equip) list.get(i);
			map.put(vo.getCategory(), vo.getCn_name());
		}
		request.setAttribute("map", map);
		request.setAttribute("list", list);
		return "/system/equip_pic/add.jsp";
	}

	private String save() {
		equip vo = new equip();
		vo.setCategory(getParaIntValue("category1"));
		vo.setCn_name(getParaValue("cn_name"));
		vo.setEn_name(getParaValue("en_name"));
		vo.setTopo_image(getParaValue("topo_image"));
		vo.setLost_image(getParaValue("lost_image"));
		vo.setAlarm_image(getParaValue("alarm_image"));
		vo.setPath(getParaValue("path"));

		equipDao dao = new equipDao();
		dao.save(vo);
		String target = "/equip.do?action=list";
		return target;
	}

	private String update() {
		equip vo = new equip();
		vo.setId(getParaIntValue("id"));
		vo.setCategory(getParaIntValue("category"));
		vo.setCn_name(getParaValue("cn_name"));
		vo.setEn_name(getParaValue("en_name"));
		vo.setTopo_image(getParaValue("topo_image"));
		vo.setLost_image(getParaValue("lost_image"));
		vo.setAlarm_image(getParaValue("alarm_image"));
		vo.setPath(getParaValue("path"));
		equipDao dao = new equipDao();
		String target = null;
		if (dao.update(vo)) {
			target = "/equip.do?action=list";
		}
		return target;
	}

}