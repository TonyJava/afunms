package com.afunms.system.manage;

import java.util.UUID;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ManagerInterface;
import com.afunms.system.dao.CodedetailDao;
import com.afunms.system.model.Codedetail;

public class CodeDetailManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		if (action.equals("list")) {
			CodedetailDao dao = new CodedetailDao();
			setTarget("/system/codedetail/list.jsp");
			String pid = getParaValue("pid");
			request.setAttribute("pid", pid);
			return list(dao, "where TYPEID='" + pid + "' order by seq asc ");
		} else if (action.equals("update")) {
			return update();
		} else if (action.equals("add")) {
			return save();
		} else if (action.equals("delete")) {
			String[] id = getParaArrayValue("checkbox");
			String target = null;
			CodedetailDao dao = new CodedetailDao();
			if (dao.delete(id)) {
				target = "/code.do?action=list";
			}
			return target;
		}
		return null;
	}

	private String save() {
		Codedetail vo = new Codedetail();
		vo.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		vo.setName(getParaValue("name"));
		vo.setSeq(getParaIntValue("seq"));
		vo.setDesp(getParaValue("desp"));
		vo.setCode(getParaValue("code"));
		vo.setTypeid(getParaValue("pid"));
		DaoInterface dao = new CodedetailDao();
		String target = null;
		if (dao.save(vo)) {
			target = "/code.do?action=list";
		}
		return target;
	}

	private String update() {
		Codedetail vo = new Codedetail();
		vo.setId(getParaValue("id"));
		vo.setName(getParaValue("name"));
		vo.setSeq(getParaIntValue("seq"));
		vo.setDesp(getParaValue("desp"));
		vo.setTypeid(getParaValue("pid"));
		CodedetailDao dao = new CodedetailDao();
		String target = null;
		if (dao.update(vo)) {
			target = "/code.do?action=list&pid=" + vo.getTypeid();
		}
		return target;
	}

}
