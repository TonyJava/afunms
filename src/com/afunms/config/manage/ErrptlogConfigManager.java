package com.afunms.config.manage;

import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ShareData;
import com.afunms.config.dao.DiskConfigDao;
import com.afunms.config.dao.ErrptconfigDao;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.Errptconfig;

@SuppressWarnings("unchecked")
public class ErrptlogConfigManager extends BaseManager implements ManagerInterface {
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
		if (action.equals("showedit")) {
			return readyEdit();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("save")) {
			return save();
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

	private String save() {
		// »ñÈ¡TYPE
		String[] ps = getParaArrayValue("checkbox");
		String pri_str = "";
		if (ps != null && ps.length > 0) {
			for (int i = 0; i < ps.length; i++) {

				String pa = ps[i];
				pri_str = pri_str + pa + ",";
			}
		}

		String[] pt = getParaArrayValue("ccheckbox");
		String pc_str = "";
		if (pt != null && pt.length > 0) {
			for (int i = 0; i < pt.length; i++) {

				String p_t = pt[i];
				pc_str = pc_str + p_t + ",";
			}
		}

		String nodeid = getParaValue("nodeid");
		String alarmwayids = getParaValue("way0-id");
		if (alarmwayids == null) {
			alarmwayids = "";
		}
		ErrptconfigDao errptdao = new ErrptconfigDao();
		Errptconfig errptconfig = null;
		try {
			errptconfig = errptdao.loadErrptconfigByNodeid(Integer.parseInt(nodeid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			errptdao.close();
		}

		errptdao = new ErrptconfigDao();
		if (errptconfig == null) {
			errptconfig = new Errptconfig();
			errptconfig.setNodeid(Integer.parseInt(nodeid));
			errptconfig.setErrpttype(pri_str);
			errptconfig.setErrptclass(pc_str);
			errptconfig.setAlarmwayid(alarmwayids);
			try {
				errptdao.save(errptconfig);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				errptdao.close();
			}
		} else {
			errptconfig.setErrpttype(pri_str);
			errptconfig.setErrptclass(pc_str);
			errptconfig.setAlarmwayid(alarmwayids);
			try {
				errptdao.update(errptconfig);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				errptdao.close();
			}
		}

		return "/config/errptconfig/saveok.jsp";
	}

	private String toolbarlist() {
		Errptconfig errptconfig = new Errptconfig();
		ErrptconfigDao dao = new ErrptconfigDao();
		String nodeid = getParaValue("nodeid");
		try {
			errptconfig = dao.loadErrptconfigByNodeid(Integer.parseInt(nodeid));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		request.setAttribute("errptconfig", errptconfig);
		request.setAttribute("nodeid", nodeid);
		return "/config/errptconfig/toolbarlist.jsp";
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
