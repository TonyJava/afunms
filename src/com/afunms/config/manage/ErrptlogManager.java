package com.afunms.config.manage;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.DiskConfigDao;
import com.afunms.config.model.Diskconfig;

@SuppressWarnings("unchecked")
public class ErrptlogManager extends BaseManager implements ManagerInterface {
	private String empty() // yangjun
	{
		DiskConfigDao dao = new DiskConfigDao();
		dao.empty();
		dao = new DiskConfigDao();
		List ips = null;
		try {
			ips = dao.getIps();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("ips", ips);
		dao = new DiskConfigDao();
		setTarget("/config/diskconfig/list.jsp");
		return list(dao);
	}

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("monitornodelist")) {
			return monitornodelist();
		}
		if (action.equals("fromlasttoconfig")) {
			return fromlasttoconfig();
		}
		if (action.equals("showedit")) {
			return readyEdit();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("updatedisk")) {
			return updatedisk();
		}
		if (action.equals("find")) {
			return find();
		}
		if (action.equals("updateselect")) {
			return updateselect();
		}
		if (action.equals("empty")) {
			return empty();
		}
		if (action.equals("ready_add")) {
			return "/config/diskconfig/add.jsp";
		}
		if (action.equals("toolbarlist")) {
			return toolbarlist();
		}
		if (action.equals("toolbarrefresh")) {
			return toolbarrefresh();
		}
		if (action.equals("delete")) {
			DaoInterface dao = new DiskConfigDao();
			setTarget("/disk.do?action=list");
			return delete(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String find() {
		String ipaddress = getParaValue("ipaddress");
		DiskConfigDao dao = new DiskConfigDao();
		request.setAttribute("ipaddress", ipaddress);
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new DiskConfigDao();
		setTarget("/config/diskconfig/findlist.jsp");
		return list(dao, " where ipaddress = '" + ipaddress + "'");
	}

	private String fromlasttoconfig() {
		DiskConfigDao dao = new DiskConfigDao();
		DiskConfigDao _dao = new DiskConfigDao();
		try {
			dao.fromLastToDiskconfig();
			Hashtable allDiskAlarm = _dao.getByAlarmflag(new Integer(99));
			ShareData.setAlldiskalarmdata(allDiskAlarm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			_dao.close();
		}
		dao = new DiskConfigDao();
		List ips = null;
		try {
			ips = dao.getIps();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("ips", ips);
		dao = new DiskConfigDao();
		setTarget("/config/diskconfig/list.jsp");
		return list(dao);
	}

	private String list() {
		DiskConfigDao dao = new DiskConfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new DiskConfigDao();
		setTarget("/config/diskconfig/list.jsp");
		return list(dao);
	}

	private String monitornodelist() {
		DiskConfigDao dao = new DiskConfigDao();
		setTarget("/config/diskconfig/portconfiglist.jsp");
		return list(dao, " where managed=1");
	}

	private String readyEdit() {
		DiskConfigDao dao = new DiskConfigDao();
		Diskconfig vo = new Diskconfig();
		vo = dao.loadDiskconfig(getParaIntValue("id"));
		request.setAttribute("vo", vo);
		return "/config/diskconfig/edit.jsp";
	}

	private String toolbarlist() {
		DiskConfigDao dao = new DiskConfigDao();
		String nodeid = getParaValue("nodeid");
		String ipaddress = getParaValue("ipaddress");
		List disklist = new ArrayList();
		try {
			disklist = dao.findByCondition(" where ipaddress='" + ipaddress + "'");
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		request.setAttribute("list", disklist);
		request.setAttribute("nodeid", nodeid);
		request.setAttribute("ipaddress", ipaddress);
		return "/config/diskconfig/toolbarlist.jsp";
	}

	private String toolbarrefresh() {
		DiskConfigDao dao = new DiskConfigDao();
		DiskConfigDao _dao = new DiskConfigDao();
		String nodeid = getParaValue("nodeid");
		String ipaddress = getParaValue("ipaddress");
		try {
			dao.fromLastToDiskconfig(ipaddress);
			Hashtable allDiskAlarm = _dao.getByAlarmflag(new Integer(99));
			ShareData.setAlldiskalarmdata(allDiskAlarm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			_dao.close();
		}
		return "/disk.do?action=toolbarlist&nodeid=" + nodeid + "&ipaddress=" + ipaddress;
		// dao = new DiskconfigDao();
		// List ips = null;
		// try {
		// ips = dao.getIps();
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// dao.close();
		// }
		// request.setAttribute("ips", ips);
		// dao = new DiskconfigDao();
		// setTarget("/config/diskconfig/list.jsp");
		// return list(dao);
	}

	private String update() {
		Diskconfig vo = new Diskconfig();
		int id = getParaIntValue("id");
		DiskConfigDao dao = new DiskConfigDao();
		vo = dao.loadDiskconfig(id);
		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);
		if (sms > -1) {
			vo.setSms(sms);
		}
		if (reportflag > -1) {
			vo.setReportflag(reportflag);
		}
		dao = new DiskConfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new DiskConfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new DiskConfigDao();
		return "/disk.do?action=list";
	}

	private String updatedisk() {
		Diskconfig vo = new Diskconfig();
		int id = getParaIntValue("id");
		DiskConfigDao dao = new DiskConfigDao();
		vo = dao.loadDiskconfig(id);
		dao.close();
		int monflag = getParaIntValue("monflag");
		int limenvalue = getParaIntValue("limenvalue");
		int sms = getParaIntValue("sms");
		int limenvalue1 = getParaIntValue("limenvalue1");
		int sms1 = getParaIntValue("sms1");
		int limenvalue2 = getParaIntValue("limenvalue2");
		int sms2 = getParaIntValue("sms2");
		int reportflag = getParaIntValue("reportflag");
		vo.setMonflag(monflag);
		vo.setLimenvalue(limenvalue);
		vo.setSms(sms);
		vo.setLimenvalue1(limenvalue1);
		vo.setSms1(sms1);
		vo.setLimenvalue2(limenvalue2);
		vo.setSms2(sms2);
		vo.setReportflag(reportflag);
		dao = new DiskConfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/disk.do?action=list";
	}

	private String updateselect() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		DiskConfigDao dao = new DiskConfigDao();
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		int id = getParaIntValue("id");
		Diskconfig vo = new Diskconfig();
		vo = dao.loadDiskconfig(id);

		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);

		vo.setSms(sms);
		vo.setReportflag(reportflag);
		dao = new DiskConfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new DiskConfigDao();
		setTarget("/config/diskconfig/findlist.jsp");
		return list(dao, " where " + key + " = '" + value + "'");
	}
}
