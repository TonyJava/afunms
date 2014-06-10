package com.afunms.topology.manage;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.polling.om.IpMacBase;
import com.afunms.topology.dao.IpMacBaseDao;
import com.afunms.topology.dao.IpMacDao;

public class IpMacBaseManager extends BaseManager implements ManagerInterface {
	private String list() {
		IpMacBaseDao dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/list.jsp");
		return list(dao);
	}

	private String monitornodelist() {
		IpMacDao dao = new IpMacDao();
		setTarget("/config/ipmacbase/ipmaclist.jsp");
		return list(dao, " where managed=1");
	}

	private String readyEdit() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		DaoInterface dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/edit.jsp");
		return readyEdit(dao);
	}

	private String update() {
		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		IpMacBaseDao dao = new IpMacBaseDao();
		vo = (IpMacBase) dao.findByID(id + "");
		String ifemail = getParaValue("ifemail");
		String ifsms = getParaValue("ifsms");
		String iftel = getParaValue("iftel");
		int flag = 0;
		if (ifemail != null && ifemail.trim().length() > 0) {
			vo.setIfemail(ifemail);
			flag = 1;
		}
		if (ifsms != null && ifsms.trim().length() > 0) {
			vo.setIfsms(ifsms);
			flag = 1;
		}
		if (iftel != null && iftel.trim().length() > 0) {
			vo.setIftel(iftel);
			flag = 1;
		}
		if (flag == 1) {
			dao = new IpMacBaseDao();
			dao.update(vo);
			dao.close();
		}
		dao.close();
		return "/ipmacbase.do?action=list&jp=1";
	}

	private String updateemployee() {
		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		IpMacBaseDao dao = new IpMacBaseDao();
		vo = (IpMacBase) dao.findByID(id + "");
		int employee_id = getParaIntValue("employee_id");
		vo.setEmployee_id(employee_id);
		dao = new IpMacBaseDao();
		dao.update(vo);
		dao.close();
		return "/ipmacbase.do?action=list&jp=1";
	}

	private String selupdateemployee() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		IpMacBaseDao dao = new IpMacBaseDao();
		vo = (IpMacBase) dao.findByID(id + "");
		int employee_id = getParaIntValue("employee_id");
		vo.setEmployee_id(employee_id);
		dao = new IpMacBaseDao();
		dao.update(vo);
		dao.close();
		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/findlist.jsp");
		return list(dao, " where " + key + " = '" + value + "'");
	}

	private String updateselect() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		IpMacBaseDao dao = new IpMacBaseDao();
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		int id = getParaIntValue("id");

		IpMacBase vo = (IpMacBase) dao.findByID(id + "");
		String ifemail = getParaValue("ifemail");
		String ifsms = getParaValue("ifsms");
		String iftel = getParaValue("iftel");
		int flag = 0;
		if (ifemail != null && ifemail.trim().length() > 0) {
			vo.setIfemail(ifemail);
			flag = 1;
		}
		if (ifsms != null && ifsms.trim().length() > 0) {
			vo.setIfsms(ifsms);
			flag = 1;
		}
		if (iftel != null && iftel.trim().length() > 0) {
			vo.setIftel(iftel);
			flag = 1;
		}
		if (flag == 1) {
			dao = new IpMacBaseDao();
			dao.update(vo);
			dao.close();
		}

		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/findlist.jsp");
		return list(dao, " where " + key + " = '" + value + "'");
	}

	private String setipmacbase() {
		IpMacBaseDao dao = new IpMacBaseDao();

		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		int flag = getParaIntValue("flag");
		vo = (IpMacBase) dao.findByID(id + "");

		dao = new IpMacBaseDao();
		vo.setIfband(flag);// 0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		try {
			if (flag == -1) {
				String[] ids = new String[1];
				ids[0] = vo.getId() + "";
				dao.delete(ids);
			} else
				dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			flag = 0;
		}
		return "/ipmacbase.do?action=list&jp=1&flag=0";
	}

	private String selsetipmacbase() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		IpMacBaseDao dao = new IpMacBaseDao();
		request.setAttribute("key", key);
		request.setAttribute("value", value);

		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		int flag = getParaIntValue("flag");
		vo = (IpMacBase) dao.findByID(id + "");

		dao = new IpMacBaseDao();
		vo.setIfband(flag);// 0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		try {
			if (flag == -1) {
				String[] ids = new String[1];
				ids[0] = vo.getId() + "";
				dao.delete(ids);
			} else
				dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		flag = 2;
		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/findlist.jsp");
		return list(dao, " where " + key + " = '" + value + "'");
	}

	private String cancelipmacbase() {
		IpMacBaseDao dao = new IpMacBaseDao();

		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		int flag = getParaIntValue("flag");
		vo = (IpMacBase) dao.findByID(id + "");
		dao = new IpMacBaseDao();
		vo.setIfband(flag);// 0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		try {
			if (flag == -1) {
				String[] ids = new String[1];
				ids[0] = vo.getId() + "";
				dao.delete(ids);
			} else
				dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/ipmacbase.do?action=list&jp=1";
	}

	private String selcancelipmacbase() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		IpMacBaseDao dao = new IpMacBaseDao();
		request.setAttribute("key", key);
		request.setAttribute("value", value);

		IpMacBase vo = new IpMacBase();
		int id = getParaIntValue("id");
		int flag = getParaIntValue("flag");
		vo = (IpMacBase) dao.findByID(id + "");
		dao = new IpMacBaseDao();
		vo.setIfband(flag);// 0:设为基准表 1:IP-MAC 2;端口-MAC 3:IP-端口-MAC
		try {
			if (flag == -1) {
				String[] ids = new String[1];
				ids[0] = vo.getId() + "";
				dao.delete(ids);
			} else
				dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/findlist.jsp");
		return list(dao, " where " + key + " = '" + value + "'");
	}

	private String find() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		IpMacBaseDao dao = new IpMacBaseDao();
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		setTarget("/config/ipmacbase/findlist.jsp");
		return list(dao, " where " + key + " like '%" + value + "%'");
	}

	private String deleteall() {
		IpMacBaseDao dao = new IpMacBaseDao();
		dao.deleteall();
		dao.close();
		dao = new IpMacBaseDao();
		setTarget("/config/ipmacbase/list.jsp");
		return list(dao);
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("monitornodelist"))
			return monitornodelist();
		if (action.equals("ready_edit"))
			return readyEdit();
		if (action.equals("update"))
			return update();
		if (action.equals("deleteall"))
			return deleteall();
		if (action.equals("updateemployee"))
			return updateemployee();
		if (action.equals("selupdateemployee"))
			return selupdateemployee();
		if (action.equals("find"))
			return find();
		if (action.equals("updateselect"))
			return updateselect();
		if (action.equals("setipmacbase"))
			return setipmacbase();
		if (action.equals("selsetipmacbase"))
			return selsetipmacbase();
		if (action.equals("cancelipmacbase"))
			return cancelipmacbase();
		if (action.equals("selcancelipmacbase"))
			return selcancelipmacbase();
		if (action.equals("ready_add"))
			return "/topology/network/add.jsp";
		if (action.equals("delete")) {
			DaoInterface dao = new IpMacBaseDao();
			setTarget("/ipmacbase.do?action=list");
			return delete(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
