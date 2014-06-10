package com.afunms.system.manage;

import java.util.UUID;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.DictionaryDao;
import com.afunms.system.model.CodeType;

public class DictionaryManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list")) {
			DaoInterface dao = new DictionaryDao();
			setTarget("/system/code/list.jsp");
			return list(dao, "order by type,seq asc ");
		} else if (action.equals("update")) {
			return update();
		} else if (action.equals("add")) {
			return save();
		} else if (action.equals("delete")) {
			String[] id = getParaArrayValue("checkbox");
			String target = null;
			DaoInterface dao = new DictionaryDao();
			if (dao.delete(id)) {
				target = "/dic.do?action=list";
			}
			return target;
		}
		return null;
	}

	private String save() {
		CodeType vo = new CodeType();
		vo.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		vo.setName(getParaValue("name"));
		vo.setSeq(getParaIntValue("seq"));
		vo.setDesp(getParaValue("desp"));
		vo.setCode(getParaValue("code"));
		vo.setType(getParaValue("type"));
		DaoInterface dao = new DictionaryDao();
		String target = null;
		if (dao.save(vo)) {
			target = "/dic.do?action=list";
		}
		return target;
	}

	private String update() {
		CodeType vo = new CodeType();
		vo.setId(getParaValue("id"));
		vo.setName(getParaValue("name"));
		vo.setSeq(getParaIntValue("seq"));
		vo.setDesp(getParaValue("desp"));
		DaoInterface dao = new DictionaryDao();
		String target = null;
		if (dao.update(vo)) {
			target = "/dic.do?action=list";
		}
		return target;
	}

}
