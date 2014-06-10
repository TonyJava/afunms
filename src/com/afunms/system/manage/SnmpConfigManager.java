
package com.afunms.system.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.SnmpConfigDao;
import com.afunms.system.model.SnmpConfig;


@SuppressWarnings("unchecked")
public class SnmpConfigManager extends BaseManager implements ManagerInterface {
	/**
	 * 覆盖父类同名方法
	 */
	private String delete() {
		SnmpConfigDao dao = new SnmpConfigDao();

		if (dao.delete(getParaValue("id"))) {
			return "/snmp.do?action=list";
		} else {
			return null;
		}

	}

	public String execute(String action) {
		if (action.equals("ready_add")) {
			return "/system/snmpconfig/add.jsp";
		}
		if (action.equals("add")) {
			SnmpConfig vo = new SnmpConfig();
			vo.setName(getParaValue("name"));
			vo.setSnmpversion(getParaIntValue("snmpversion"));
			vo.setReadcommunity(getParaValue("readcommunity"));
			vo.setWritecommunity(getParaValue("writecommunity"));
			vo.setTimeout(getParaIntValue("timeout"));
			vo.setTrytime(getParaIntValue("trytime"));
			SnmpConfigDao dao = new SnmpConfigDao();
			dao.save(vo);
			return "/snmp.do?action=list";
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("update")) {
			int id = getParaIntValue("id");
			SnmpConfig vo = new SnmpConfig();
			SnmpConfigDao dao = new SnmpConfigDao();
			vo = (SnmpConfig) dao.findByID(id + "");
			vo.setName(getParaValue("name"));
			vo.setSnmpversion(getParaIntValue("snmpversion"));
			vo.setReadcommunity(getParaValue("readcommunity"));
			vo.setWritecommunity(getParaValue("writecommunity"));
			vo.setTimeout(getParaIntValue("timeout"));
			vo.setTrytime(getParaIntValue("trytime"));
			dao = new SnmpConfigDao();
			dao.update(vo);
			return "/snmp.do?action=list";
		}
		if (action.equals("list")) {
			SnmpConfigDao dao = new SnmpConfigDao();
			List list = dao.loadAll();
			request.setAttribute("list", list);
			return "/system/snmpconfig/list.jsp";
		}
		if (action.equals("ready_edit")) {
			SnmpConfigDao dao = new SnmpConfigDao();
			int id = getParaIntValue("id");
			SnmpConfig vo = new SnmpConfig();
			vo = (SnmpConfig) dao.findByID(id + "");
			request.setAttribute("vo", vo);
			return "/system/snmpconfig/edit.jsp";
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}
