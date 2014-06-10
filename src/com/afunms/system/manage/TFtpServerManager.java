package com.afunms.system.manage;

import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.system.dao.TFtpServerDao;
import com.afunms.system.model.TFtpServer;

@SuppressWarnings("unchecked")
public class TFtpServerManager extends BaseManager implements ManagerInterface {

	private String addalert() {
		TFtpServer vo = new TFtpServer();
		TFtpServerDao configdao = new TFtpServerDao();
		try {
			vo = (TFtpServer) configdao.findByID(getParaValue("id"));
			vo.setUsedflag(1);
			configdao = new TFtpServerDao();
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/tftpserver.do?action=list";
	}

	private String cancelalert() {
		TFtpServer vo = new TFtpServer();
		TFtpServerDao configdao = new TFtpServerDao();
		try {
			vo = (TFtpServer) configdao.findByID(getParaValue("id"));
			vo.setUsedflag(0);
			configdao = new TFtpServerDao();
			configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/tftpserver.do?action=list";
	}

	public void createLinexmlfile(Hashtable lineHash) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("line");
			chartxml.AddLineXML(lineHash);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createxmlfile(List list) {
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("pie");
			chartxml.AddXML(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String execute(String action) {
		if (action.equals("ready_add")) {
			return "/system/tftpserver/add.jsp";
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
			DaoInterface dao = new TFtpServerDao();
			setTarget("/tftpserver.do?action=list");
			return delete(dao);
		}
		if (action.equals("ready_edit")) {
			DaoInterface dao = new TFtpServerDao();
			setTarget("/system/tftpserver/edit.jsp");
			return readyEdit(dao);
		}
		if (action.equals("read")) {
			DaoInterface dao = new TFtpServerDao();
			setTarget("/system/tftpserver/read.jsp");
			return readyEdit(dao);
		}

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String list() {
		TFtpServerDao configdao = new TFtpServerDao();
		List list = configdao.loadAll();
		request.setAttribute("list", list);
		return "/system/tftpserver/list.jsp";
	}

	private String save() {
		TFtpServer vo = new TFtpServer();
		vo.setIp(getParaValue("ip"));
		vo.setUsedflag(getParaIntValue("usedflag"));
		TFtpServerDao dao = new TFtpServerDao();
		dao.save(vo);
		return "/tftpserver.do?action=list";
	}

	private String update() {
		TFtpServer vo = new TFtpServer();
		vo.setId(getParaIntValue("id"));
		vo.setIp(getParaValue("ip"));
		vo.setUsedflag(getParaIntValue("usedflag"));

		TFtpServerDao dao = new TFtpServerDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/tftpserver.do?action=list";
	}
}
