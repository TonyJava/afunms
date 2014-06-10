/**
 * <p>Description:DiscoverManager</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.config.manage;

import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SysConfigFileUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.NetNodeCfgFileDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.Portconfig;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.system.dao.TFtpServerDao;
import com.afunms.system.model.TFtpServer;

@SuppressWarnings("unchecked")
public class NetNodeCfgFileManager extends BaseManager implements ManagerInterface {
	private String empty() {
		PortconfigDao dao = new PortconfigDao();
		dao.empty();
		dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
		return list(dao);
	}

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("nodelist")) {
			return nodelist();
		}
		if (action.equals("shownodelist")) {
			return shownodelist();
		}
		if (action.equals("getcfgfile")) {
			return getcfgfile();
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
		if (action.equals("updateport")) {
			return updateport();
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
			return "/config/portconfig/add.jsp";
		}
		if (action.equals("delete")) {
			DaoInterface dao = new PortconfigDao();
			setTarget("/portconfig.do?action=list");
			return delete(dao);
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String find() {
		String ipaddress = getParaValue("ipaddress");
		PortconfigDao dao = new PortconfigDao();
		request.setAttribute("ipaddress", ipaddress);
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/findlist.jsp");
		return list(dao, " where ipaddress = '" + ipaddress + "'");
	}

	private String getcfgfile() {
		NetNodeCfgFileDao dao = new NetNodeCfgFileDao();
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
		Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
		TFtpServerDao tftpdao = new TFtpServerDao();
		TFtpServer tftp = tftpdao.loadByFlage(1);
		SysConfigFileUtil cfgutil = new SysConfigFileUtil();
		if (host.getSysOid().startsWith("1.3.6.1.4.1.2011.") || host.getSysOid().startsWith("1.3.6.1.4.1.25506.")) {
			cfgutil.getH3cConfig(ipaddress, host.getCommunity(), host.getWritecommunity(), host.getSnmpversion(), SystemConstant.RUNNING2NET, tftp.getIp());
			cfgutil.getH3cConfig(ipaddress, host.getCommunity(), host.getWritecommunity(), host.getSnmpversion(), SystemConstant.STARTUP2NET, tftp.getIp());
		} else if (host.getSysOid().startsWith("1.3.6.1.4.1.9.")) {
			cfgutil.getCiscoConfig(ipaddress, host.getCommunity(), host.getWritecommunity(), host.getSnmpversion(), SystemConstant.RUNNING2CONFIG, tftp.getIp());
			cfgutil.getCiscoConfig(ipaddress, host.getCommunity(), host.getWritecommunity(), host.getSnmpversion(), SystemConstant.STARTUP2CONFIG, tftp.getIp());
		}
		setTarget("/topology/netnodecfgfile/list.jsp");
		return list(dao, " where ipaddress='" + ipaddress + "' order by id desc");
	}

	private String list() {
		NetNodeCfgFileDao dao = new NetNodeCfgFileDao();
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
		setTarget("/topology/netnodecfgfile/list.jsp");
		return list(dao, " where ipaddress='" + ipaddress + "' order by id desc");
	}

	private String monitornodelist() {
		NetNodeCfgFileDao dao = new NetNodeCfgFileDao();
		String ipaddress = getParaValue("ipaddress");
		setTarget("/topology/netnodecfgfile/cfgfilelist.jsp");
		return list(dao, " where ipaddress=" + ipaddress);
	}

	private String nodelist() {
		NetNodeCfgFileDao dao = new NetNodeCfgFileDao();
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
		setTarget("/topology/netnodecfgfile/nodelist.jsp");
		return list(dao, " where ipaddress='" + ipaddress + "' order by id desc");
	}

	private String readyEdit() {
		PortconfigDao dao = new PortconfigDao();
		Portconfig vo = new Portconfig();
		vo = dao.loadPortconfig(getParaIntValue("id"));
		request.setAttribute("vo", vo);
		return "/config/portconfig/edit.jsp";
	}

	private String shownodelist() {
		NetNodeCfgFileDao dao = new NetNodeCfgFileDao();
		String ipaddress = getParaValue("ipaddress");
		request.setAttribute("ipaddress", ipaddress);
		setTarget("/topology/netnodecfgfile/shownodelist.jsp");
		return list(dao, " where ipaddress='" + ipaddress + "' order by id desc");
	}

	private String update() {
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		PortconfigDao dao = new PortconfigDao();
		vo = dao.loadPortconfig(id);
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
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		return "/portconfig.do?action=list";
	}

	private String updateport() {
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		PortconfigDao dao = new PortconfigDao();
		vo = dao.loadPortconfig(id);
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
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new PortconfigDao();
		return "/portconfig.do?action=list";
	}

	private String updateselect() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		PortconfigDao dao = new PortconfigDao();
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		int id = getParaIntValue("id");
		Portconfig vo = new Portconfig();
		vo = dao.loadPortconfig(id);

		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);

		vo.setSms(sms);
		vo.setReportflag(reportflag);
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new PortconfigDao();
		setTarget("/config/portconfig/findlist.jsp");
		return list(dao, " where " + key + " = '" + value + "'");
	}
}
