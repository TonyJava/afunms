package com.afunms.capreport.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.EventReportDao;
import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.EventReport;
import com.afunms.event.model.Syslog;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataDayManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;

@SuppressWarnings("unchecked")
public class CapReportManager extends BaseManager implements ManagerInterface {
	DateE datemanager = new DateE();
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	I_HostCollectData hostmanager = new HostCollectDataManager();
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String list() {
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadNetwork(0));
		return "/topology/network/list.jsp";
	}

	private String netif() {
		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		String orderflag = getParaValue("orderflag");
		if (orderflag == null || orderflag.trim().length() == 0)
			orderflag = "index";
		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

		Vector vector = new Vector();
		String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
		try {
			vector = hostlastmanager.getInterface_share(host.getIpAddress(), netInterfaceItem, orderflag, starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// 得到系统启动时间
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}

		request.setAttribute("vector", vector);
		request.setAttribute("id", tmp);
		request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		setTarget("/topology/network/read.jsp");
		return "/detail/net_if.jsp";
	}

	private String netdetail() {
		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		String orderflag = getParaValue("orderflag");
		if (orderflag == null || orderflag.trim().length() == 0)
			orderflag = "index";
		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

		Vector vector = new Vector();
		String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
		try {
			vector = hostlastmanager.getInterface_share(host.getIpAddress(), netInterfaceItem, orderflag, starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// 得到系统启动时间
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}

		request.setAttribute("vector", vector);
		request.setAttribute("id", tmp);
		request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/net_infodetail.jsp";
	}

	private String netinterface() {
		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		String orderflag = getParaValue("orderflag");
		if (orderflag == null || orderflag.trim().length() == 0)
			orderflag = "index";
		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

		Vector vector = new Vector();
		String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
		try {
			vector = hostlastmanager.getInterface_share(host.getIpAddress(), netInterfaceItem, orderflag, starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// 得到系统启动时间
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}

		request.setAttribute("vector", vector);
		request.setAttribute("id", tmp);
		request.setAttribute("ipaddress", host.getIpAddress());
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));

		setTarget("/topology/network/read.jsp");
		return "/detail/net_interface.jsp";
	}

	private String netcpu() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));

		String newip = doip(host.getIpAddress());
		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}
		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		// 从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU", "Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu最大值
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpu平均值
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);
		// 画图
		p_draw_line(cpuhash, "", newip + "cpu", 740, 120);
		imgurlhash.put("cpu", "resource/image/jfreechart/" + newip + "cpu" + ".png");
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// 得到系统启动时间
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}

		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/net_cpu.jsp";
	}

	private String hostcpu() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		String newip = doip(host.getIpAddress());
		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		try {
			memhash = hostlastmanager.getMemory_share(host.getIpAddress(), "Memory", starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(), "Disk", starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU", "Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable[] memoryhash = null;
		try {
			memoryhash = hostmanager.getMemory(host.getIpAddress(), "Memory", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 各memory最大值
		memmaxhash = memoryhash[1];
		memavghash = memoryhash[2];
		// cpu最大值
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpu平均值
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);
		// 画图
		p_draw_line(cpuhash, "", newip + "cpu", 740, 120);

		// 画图（磁盘是显示最新数据的柱状图）
		draw_column(diskhash, "", newip + "disk", 750, 150);
		p_drawchartMultiLine(memoryhash[0], "", newip + "memory", 750, 150);
		imgurlhash.put("cpu", "resource/image/jfreechart/" + newip + "cpu" + ".png");
		imgurlhash.put("memory", "resource/image/jfreechart/" + newip + "memory" + ".png");
		imgurlhash.put("disk", "resource/image/jfreechart/" + newip + "disk" + ".png");

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// 得到系统启动时间
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}

		request.setAttribute("memmaxhash", memmaxhash);
		request.setAttribute("memavghash", memavghash);
		request.setAttribute("diskhash", diskhash);
		request.setAttribute("memhash", memhash);

		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/host_cpu.jsp";
	}

	private String hostproc() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable processhash = new Hashtable();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));

		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		// 从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU", "Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu最大值
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpu平均值
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// 得到系统启动时间
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		// 按order将进程信息排序
		String order = "MemoryUtilization";
		if ((request.getParameter("orderflag") != null) && (!request.getParameter("orderflag").equals(""))) {
			order = request.getParameter("orderflag");
		}
		try {
			processhash = hostlastmanager.getProcess_share(host.getIpAddress(), "Process", order, starttime, endtime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}

		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("userhash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("processhash", processhash);
		return "/detail/host_proc.jsp";
	}

	private String hostsyslog() {
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		List list = new ArrayList();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String b_time = "";
		String t_time = "";

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));

		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		// 从collectdata取cpu,内存的历史数据
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU", "Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu最大值
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpu平均值
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// 得到系统启动时间
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		String starttime2 = b_time + " 00:00:00";
		String totime2 = t_time + " 23:59:59";
		String priorityname = getParaValue("priorityname");
		if (priorityname == null)
			priorityname = "all";
		SyslogDao syslogdao = new SyslogDao();
		try {
			list = syslogdao.getQuery(host.getIpAddress(), starttime2, totime2, priorityname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}

		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);

		request.setAttribute("hash", hash);
		request.setAttribute("userhash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/host_syslog.jsp";
	}

	private String hostsyslogdetail() {
		try {
			String ip = getParaValue("ipaddress");
			int id = getParaIntValue("id");
			Syslog syslog = new Syslog();
			SyslogDao dao = new SyslogDao();
			syslog = dao.getSyslogData(id, ip);
			request.setAttribute("syslog", syslog);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/detail/host_syslogdetail.jsp";
	}

	private String hostevent() {
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable processhash = new Hashtable();
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";

		try {
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}

			String collecttime = null;
			String sysuptime = null;
			String sysservices = null;
			String sysdescr = null;

			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			String[] time = { "", "" };
			getTime(request, time);
			String starttime = time[0];
			String endtime = time[1];
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			// 从collectdata取cpu,内存的历史数据
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime2, totime2, status + "", level1 + "", vo.getBusinessids(), host.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// 得到系统启动时间
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}
			// 按order将进程信息排序
			String order = "MemoryUtilization";
			if ((request.getParameter("orderflag") != null) && (!request.getParameter("orderflag").equals(""))) {
				order = request.getParameter("orderflag");
			}
			try {
				processhash = hostlastmanager.getProcess_share(host.getIpAddress(), "Process", order, starttime, endtime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}

			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
			request.setAttribute("list", list);

			request.setAttribute("hash", hash);
			request.setAttribute("userhash", hash);
			request.setAttribute("max", maxhash);
			request.setAttribute("id", tmp);
			request.setAttribute("cpuvalue", cpuvalue);
			request.setAttribute("collecttime", collecttime);
			request.setAttribute("sysuptime", sysuptime);
			request.setAttribute("sysservices", sysservices);
			request.setAttribute("sysdescr", sysdescr);
			request.setAttribute("pingconavg", new Double(pingconavg));
			request.setAttribute("processhash", processhash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/detail/host_event.jsp";
	}

	private String netping() {

		Hashtable imgurlhash = new Hashtable();
		Hashtable hash1 = new Hashtable();// "System","Ping"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		String tmp = "";

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		try {
			tmp = request.getParameter("id");

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));

			String newip = doip(host.getIpAddress());
			String[] time = { "", "" };
			getTime(request, time);
			String starttime = time[0];
			String endtime = time[1];
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			String[] item1 = { "System", "Ping" };
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			try {
				hash1 = hostlastmanager.getbyCategories_share(host.getIpAddress(), item1, starttime, endtime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			// 画图
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			// imgurlhash
			imgurlhash.put("ConnectUtilization", "resource/image/jfreechart/" + newip + "ConnectUtilization" + ".png");

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// 得到系统启动时间
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash1", hash1);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);

		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/net_ping.jsp";
	}

	private String hostping() {

		Hashtable imgurlhash = new Hashtable();
		Hashtable hash1 = new Hashtable();// "System","Ping"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		String tmp = "";

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		try {
			tmp = request.getParameter("id");

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));

			String newip = doip(host.getIpAddress());
			String[] time = { "", "" };
			getTime(request, time);
			String starttime = time[0];
			String endtime = time[1];
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			String[] item1 = { "System", "Ping" };
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			try {
				hash1 = hostlastmanager.getbyCategories_share(host.getIpAddress(), item1, starttime, endtime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			// 画图
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			// imgurlhash
			imgurlhash.put("ConnectUtilization", "resource/image/jfreechart/" + newip + "ConnectUtilization" + ".png");

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// 得到系统启动时间
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash1", hash1);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);

		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/topology/network/hostview.jsp";
	}

	private String netarp() {
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		try {

			tmp = request.getParameter("id");

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// 从内存中获得当前的跟此IP相关的IP-MAC的ARP表信息
			Hashtable _relateIpMacHash = ShareData.getRelateipmacdata();
			vector = (Vector) _relateIpMacHash.get(ip);
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// 得到系统启动时间
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				time = sdf1.format(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector", vector);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/net_arp.jsp";
	}

	private String netevent() {
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		try {

			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}


			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();


			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), host.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector", vector);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return "/detail/net_event.jsp";
	}

	private String accit() {
		String eventid = "";

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventList event = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList) dao.findByID(eventid);

			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
		return "/detail/net_accitevent.jsp";
	}

	private String accfi() {
		String eventid = "";
		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventList event = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList) dao.findByID(eventid);
			event.setManagesign(new Integer(1));
			dao = new EventListDao();
			dao.update(event);
			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}


			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();

			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), host.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
		return "/detail/net_event.jsp";
	}

	private String fireport() {
		String eventid = "";

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventList event = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList) dao.findByID(eventid);
			// event.setManagesign(new Integer(1));
			Integer nowstatus = event.getManagesign();
			dao = new EventListDao();
			dao.update(event);
			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);
			request.setAttribute("nowstatus", nowstatus);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();


		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
		return "/detail/net_accitevent.jsp";
	}

	private String doreport() {
		String eventid = "";

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventList event = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList) dao.findByID(eventid);
			event.setManagesign(new Integer(2));
			dao = new EventListDao();
			dao.update(event);
			EventReport eventreport = new EventReport();
			Date d = sdf0.parse(getParaValue("deal_time"));
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			eventreport.setDeal_time(c);
			eventreport.setReport_content(getParaValue("report_content"));
			eventreport.setReport_man(getParaValue("report_man"));
			eventreport.setReport_time(Calendar.getInstance());
			eventreport.setEventid(Integer.valueOf(eventid));
			EventReportDao reportdao = new EventReportDao();
			reportdao.save(eventreport);
			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), host.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
		return "/detail/net_event.jsp";
	}

	private String viewreport() {
		String eventid = "";

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventList event = null;
		String deal_time = "";
		String report_time = "";
		EventReport eventreport = null;
		try {
			eventid = getParaValue("eventid");
			EventListDao dao = new EventListDao();
			event = (EventList) dao.findByID(eventid);
			// 取得报告详细信息
			EventReportDao rdao = new EventReportDao();
			eventreport = (EventReport) rdao.findByEventId(eventid);

			deal_time = sdf0.format(eventreport.getDeal_time().getTime());
			report_time = sdf0.format(eventreport.getReport_time().getTime());

			tmp = request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}


			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();

			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			try {
				User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1 + "", vo.getBusinessids(), host.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("eventdetail", event);
		request.setAttribute("eventreport", eventreport);
		request.setAttribute("deal_time", deal_time);
		request.setAttribute("report_time", report_time);
		return "/detail/net_accitevent.jsp";
	}

	private String showutilhdx() {
		Hashtable imgurlhash = new Hashtable();

		List yearlist = new ArrayList();
		List monthlist = new ArrayList();

		Hashtable value = new Hashtable();

		String ip = "";
		String index = "";
		String ifname = "";
		String time1 = "";
		String b_time = "";
		String t_time = "";
		String perelement = "";

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();

		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");// request.getParameter("ipaddress");
			index = getParaValue("ifindex");// request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");
			perelement = getParaValue("perelement");
			if (perelement == null || perelement.trim().length() == 0)
				perelement = "minutes";

			HostNodeDao hostdao = new HostNodeDao();
			List hostlist = hostdao.loadNetwork(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode tempHost = (HostNode) hostlist.get(i);
					if (tempHost.getIpAddress().equalsIgnoreCase(ip)) {
						hostnode = tempHost;
						break;
					}
				}
			}
			hostdao.close();

			if (b_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}

			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			String category = "UtilHdx";

			String newip = doip(ip) + index;
			String title = "当天24小时端口流速归档";
			String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
			String[] bandch3 = { "入口流速", "出口流速" };
			String reportname = title + "日报表";

			String year = request.getParameter("year");
			int index1 = 0;
			Calendar now = Calendar.getInstance();
			int yearint = now.get(Calendar.YEAR);

			for (int i = 4; i >= 0; i--) {
				String tmp = String.valueOf(yearint - i);
				yearlist.add(index1, tmp);
				index1++;
			}
			for (int i = 0; i < 12; i++) {
				monthlist.add(i, String.valueOf(i + 1));
			}
			if (year == null) {
				year = new Integer(yearint).toString();
			}

			title = "端口流速";

			// 山西移动IDC去掉月报表2008-04-30
			if (perelement.equalsIgnoreCase("minutes")) {
				// 按分钟显示报表
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index, banden3, bandch3, b_time, t_time, "UtilHdx");
				reportname = title + b_time + "至" + t_time + "报表(按分钟显示)";
				p_drawchartMultiLineMonth(value, reportname, newip + category + "_month", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + category + "_month.png";
				imgurlhash.put("bandmonthspeed", url1);
			} else if (perelement.equalsIgnoreCase("hours")) {
				// 按小时显示报表
				/* 山西移动IDC去掉年报表2008-04-30 */
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index, banden3, bandch3, b_time, t_time, "utilhdxhour");
				reportname = title + b_time + "至" + t_time + "报表(按小时归档显示)";
				p_drawchartMultiLineYear(value, reportname, newip + "ifspeed_year", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + "ifspeed" + "_year.png";
				imgurlhash.put("bandmonthspeed", url1);
			} else {
				// 按天显示报表
				/* 山西移动IDC去掉年报表2008-04-30 */
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index, banden3, bandch3, b_time, t_time, "utilhdxday");
				reportname = title + b_time + "至" + t_time + "报表(按天归档显示)";
				p_drawchartMultiLineYear(value, reportname, newip + "ifspeed_year", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + "ifspeed" + "_year.png";
				imgurlhash.put("bandmonthspeed", url1);
			}

			imgurlhash.put("status", "resource/image/jfreechart/" + newip + "IfStatus" + ".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
		// request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", hostnode.getAlias());
		request.setAttribute("ifname", ifname);

		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("begindate", time1);
		request.setAttribute("perelement", perelement);

		return "/detail/net_utilhdx.jsp";
	}

	private String showdiscardsperc() {
		Hashtable imgurlhash = new Hashtable();
		String ip = "";
		String index = "";
		String ifname = "";
		String time1 = "";
		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");// request.getParameter("ipaddress");
			index = getParaValue("ifindex");// request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			HostNodeDao hostdao = new HostNodeDao();
			List hostlist = hostdao.loadNetwork(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode tempHost = (HostNode) hostlist.get(i);
					if (tempHost.getIpAddress().equalsIgnoreCase(ip)) {
						hostnode = tempHost;
						break;
					}
				}
			}
			hostdao.close();

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			String newip = doip(ip) + index;
			// 丢包率
			String[] banden2 = { "InDiscardsPerc", "OutDiscardsPerc" };
			String[] bandch2 = { "入口丢包率", "出口丢包率" };
			Hashtable[] bandhashtable2 = hostmanager.getDiscardsPerc(ip, index, banden2, bandch2, starttime1, totime1);

			p_drawchartMultiLine(bandhashtable2[0], "丢包率", newip + "ifdescperc", 800, 200, "DiscardsPerc");

			imgurlhash.put("ifdescperc", "resource/image/jfreechart/" + newip + "ifdescperc" + ".png");
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", hostnode.getAlias());
		request.setAttribute("ifname", ifname);
		request.setAttribute("begindate", time1);
		return "/detail/net_discardsperc.jsp";
	}

	private String showerrorsperc() {
		Hashtable imgurlhash = new Hashtable();
		String ip = "";
		String index = "";
		String ifname = "";
		String time1 = "";


		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");// request.getParameter("ipaddress");
			index = getParaValue("ifindex");// request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
				// mForm.setBegindate(time1);
			}

			HostNodeDao hostdao = new HostNodeDao();
			List hostlist = hostdao.loadNetwork(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode tempHost = (HostNode) hostlist.get(i);
					if (tempHost.getIpAddress().equalsIgnoreCase(ip)) {
						hostnode = tempHost;
						break;
					}
				}
			}
			hostdao.close();

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			String newip = doip(ip) + index;
			// 带宽利用率，流速图
			String[] banden1 = { "InErrorsPerc", "OutErrorsPerc" };
			String[] bandch1 = { "入口错误率", "出口错误率" };
			Hashtable[] bandhashtable1 = hostmanager.getErrorsPerc(ip, index, banden1, bandch1, starttime1, totime1);
			p_drawchartMultiLine(bandhashtable1[0], "端口错误率", newip + "errorperc", 800, 200, "ErrorsPerc");

			imgurlhash.put("errorperc", "resource/image/jfreechart/" + newip + "errorperc" + ".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", hostnode.getAlias());
		request.setAttribute("ifname", ifname);
		request.setAttribute("begindate", time1);
		return "/detail/net_iferrorperc.jsp";
	}

	private String showpacks() {
		Hashtable imgurlhash = new Hashtable();
		String ip = "";
		String index = "";
		String ifname = "";
		String time1 = "";

		HostNode hostnode = null;
		try {
			ip = getParaValue("ipaddress");// request.getParameter("ipaddress");
			index = getParaValue("ifindex");// request.getParameter("index");
			ifname = getParaValue("ifname");
			time1 = getParaValue("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			HostNodeDao hostdao = new HostNodeDao();
			List hostlist = hostdao.loadNetwork(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode tempHost = (HostNode) hostlist.get(i);
					if (tempHost.getIpAddress().equalsIgnoreCase(ip)) {
						hostnode = tempHost;
						break;
					}
				}
			}
			hostdao.close();

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			String newip = doip(ip) + index;
			String[] banden2 = { "InCastPkts", "OutCastPkts" };
			String[] bandch2 = { "入口数据包", "出口数据包" };
			Hashtable[] bandhashtable2 = hostmanager.getIfBand_Packs(ip, index, banden2, bandch2, starttime1, totime1);

			p_drawchartMultiLine(bandhashtable2[0], "收发信息包数", newip + "ifpacks", 800, 200, "Packs");
			imgurlhash.put("ifpacks", "resource/image/jfreechart/" + newip + "ifpacks" + ".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", hostnode.getAlias());
		request.setAttribute("ifname", ifname);
		request.setAttribute("begindate", time1);
		return "/detail/net_ifpacks.jsp";
	}

	private String ifdetail() {
		HostNode hostnode = null;

		String ip = "";
		String index = "";
		String ifname = "";

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

		String orderflag = getParaValue("orderflag");
		if (orderflag == null || orderflag.trim().length() == 0)
			orderflag = "index";
		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];

		Hashtable hash = new Hashtable();

		try {
			ip = getParaValue("ipaddress");// request.getParameter("ipaddress");
			index = getParaValue("ifindex");// request.getParameter("index");
			ifname = getParaValue("ifname");

			String[] netIfdetail = { "index", "ifDescr", "ifname", "ifType", "ifMtu", "ifSpeed", "ifPhysAddress", "ifOperStatus" };
			hash = hostlastmanager.getIfdetail_share(ip, index, netIfdetail, starttime, endtime);

			HostNodeDao hostdao = new HostNodeDao();
			List hostlist = hostdao.loadNetwork(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode tempHost = (HostNode) hostlist.get(i);
					if (tempHost.getIpAddress().equalsIgnoreCase(ip)) {
						hostnode = tempHost;
						break;
					}
				}
			}
			hostdao.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", hostnode.getAlias());
		request.setAttribute("ifname", ifname);

		request.setAttribute("hash", hash);

		return "/detail/net_ifdetail.jsp";
	}

	private String netroute() {
		Vector vector = new Vector();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String ip = "";
		String tmp = "";
		try {

			tmp = request.getParameter("id");

			Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// 从内存中获得当前的跟此IP相关的IP-ROUTE的ARP表信息
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			vector = (Vector) _IpRouterHash.get(ip);

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// 得到系统启动时间
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host.getIpAddress(), "Ping", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(host.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("vector", vector);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);

		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));

		return "/detail/net_route.jsp";
	}

	private void p_draw_line(Hashtable hash, String title1, String title2, int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();

				TimeSeries ss = new TimeSeries(title1, Minute.class);
				TimeSeries[] s = { ss };
				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp
							.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);

			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp
					.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(时间)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String readyEdit() {
		DaoInterface dao = new HostNodeDao();
		setTarget("/topology/network/edit.jsp");
		return readyEdit(dao);
	}

	private String update() {
		HostNode vo = new HostNode();
		vo.setId(getParaIntValue("id"));
		vo.setAlias(getParaValue("alias"));
		vo.setManaged(getParaIntValue("managed") == 1 ? true : false);

		// 更新内存
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		host.setAlias(vo.getAlias());
		host.setManaged(vo.isManaged());

		// 更新数据库
		DaoInterface dao = new HostNodeDao();
		setTarget("/network.do?action=list");
		return update(dao, vo);
	}

	private String refreshsysname() {
		HostNodeDao dao = new HostNodeDao();
		String sysName = "";
		sysName = dao.refreshSysName(getParaIntValue("id"));

		// 更新内存
		Host host = (Host) PollingEngine.getInstance().getNodeByID(getParaIntValue("id"));
		if (host != null) {
			host.setSysName(sysName);
			host.setAlias(sysName);
		}

		return "/network.do?action=list";
	}

	private String delete() {
		String id = getParaValue("radio");

		PollingEngine.getInstance().deleteNodeByID(Integer.parseInt(id));
		HostNodeDao dao = new HostNodeDao();
		dao.delete(id);
		return "/network.do?action=list";
	}

	private String add() {
		String ipAddress = getParaValue("ip_address");
		String alias = getParaValue("alias");
		String community = getParaValue("community");
		String writecommunity = getParaValue("writecommunity");
		int type = getParaIntValue("type");

		TopoHelper helper = new TopoHelper(); // 包括更新数据库和更新内存
		int addResult = helper.addHost(ipAddress, alias, community, writecommunity, type); // 加入一台服务器
		if (addResult == 0) {
			setErrorCode(ErrorMessage.ADD_HOST_FAILURE);
			return null;
		}
		if (addResult == -1) {
			setErrorCode(ErrorMessage.IP_ADDRESS_EXIST);
			return null;
		}
		if (addResult == -2) {
			setErrorCode(ErrorMessage.PING_FAILURE);
			return null;
		}
		if (addResult == -3) {
			setErrorCode(ErrorMessage.SNMP_FAILURE);
			return null;
		}

		// 2.更新xml
		XmlOperator opr = new XmlOperator();
		opr.setFile("network.jsp");
		opr.init4updateXml();
		opr.addNode(helper.getHost());
		opr.writeXml();

		return "/network.do?action=list";
	}

	private String find() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.findByCondition(key, value));

		return "/topology/network/find.jsp";
	}

	private String save() {
		String xmlString = request.getParameter("hidXml");
		String vlanString = request.getParameter("vlan");
		xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		XmlOperator xmlOpr = new XmlOperator();
		if (vlanString != null && vlanString.equals("1")) {
			xmlOpr.setFile("networkvlan.jsp");
		} else
			xmlOpr.setFile("network.jsp");
		xmlOpr.saveImage(xmlString);

		return "/topology/network/save.jsp";
	}


	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("netif"))
			return netif();
		if (action.equals("netdetail"))
			return netdetail();
		if (action.equals("netinterface"))
			return netinterface();
		if (action.equals("netcpu"))
			return netcpu();
		if (action.equals("hostcpu"))
			return hostcpu();
		if (action.equals("hostproc"))
			return hostproc();
		if (action.equals("hostsyslog"))
			return hostsyslog();
		if (action.equals("hostsyslogdetail"))
			return hostsyslogdetail();
		if (action.equals("netarp"))
			return netarp();
		if (action.equals("netevent"))
			return netevent();
		if (action.equals("hostevent"))
			return hostevent();
		if (action.equals("accit"))
			return accit();
		if (action.equals("accfi"))
			return accfi();
		if (action.equals("fireport"))
			return fireport();
		if (action.equals("doreport"))
			return doreport();
		if (action.equals("viewreport"))
			return viewreport();
		if (action.equals("show_utilhdx"))
			return showutilhdx();
		if (action.equals("read_detail"))
			return ifdetail();
		if (action.equals("show_discardsperc"))
			return showdiscardsperc();
		if (action.equals("show_errorsperc"))
			return showerrorsperc();
		if (action.equals("show_packs"))
			return showpacks();
		if (action.equals("netroute"))
			return netroute();
		if (action.equals("netping"))
			return netping();
		if (action.equals("hostping"))
			return hostping();
		if (action.equals("ready_edit"))
			return readyEdit();
		if (action.equals("update"))
			return update();
		if (action.equals("refreshsysname"))
			return refreshsysname();
		if (action.equals("delete"))
			return delete();
		if (action.equals("find"))
			return find();
		if (action.equals("ready_add"))
			return "/topology/network/add.jsp";
		if (action.equals("add"))
			return add();
		if (action.equals("save"))
			return save();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private void getTime(HttpServletRequest request, String[] time) {
		Calendar current = new GregorianCalendar();
		if (getParaValue("beginhour") == null) {
			Integer hour = new Integer(current.get(Calendar.HOUR_OF_DAY));
			request.setAttribute("beginhour", new Integer(hour.intValue() - 1));
			request.setAttribute("endhour", hour);
		}
		if (getParaValue("begindate") == null) {
			current.set(Calendar.MINUTE, 59);
			current.set(Calendar.SECOND, 59);
			time[1] = datemanager.getDateDetail(current);
			current.add(Calendar.HOUR_OF_DAY, -1);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			time[0] = datemanager.getDateDetail(current);

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			String begindate = "";
			begindate = timeFormatter.format(new java.util.Date());
			request.setAttribute("begindate", begindate);
			request.setAttribute("enddate", begindate);
		} else {
			String temp = getParaValue("begindate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("enddate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}
		if (getParaValue("startdate") == null) {
			current.set(Calendar.MINUTE, 59);
			current.set(Calendar.SECOND, 59);
			time[1] = datemanager.getDateDetail(current);
			current.add(Calendar.HOUR_OF_DAY, -1);
			current.set(Calendar.MINUTE, 0);
			current.set(Calendar.SECOND, 0);
			time[0] = datemanager.getDateDetail(current);

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat("yyyy-M-d");
			String startdate = "";
			startdate = timeFormatter.format(new java.util.Date());
			request.setAttribute("startdate", startdate);
			request.setAttribute("todate", startdate);
		} else {
			String temp = getParaValue("startdate");
			time[0] = temp + " " + getParaValue("beginhour") + ":00:00";
			temp = getParaValue("todate");
			time[1] = temp + " " + getParaValue("endhour") + ":59:59";
		}

	}

	private String doip(String ip) {
		String allipstr = SysUtil.doip(ip);
		return allipstr;
	}

	private void p_drawchartMultiLineMonth(Hashtable hash, String title1, String title2, int w, int h, String flag) {
		if (hash.size() != 0) {
			String unit = "";
			String[] keys = (String[]) hash.get("key");
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s = new TimeSeries[keys.length];
			try {
				for (int i = 0; i < keys.length; i++) {
					String key = keys[i];
					TimeSeries ss = new TimeSeries(key, Minute.class);
					String[] value = (String[]) hash.get(key);
					if (flag.equals("UtilHdx")) {
						unit = "y(kb/s)";
					} else {
						unit = "y(%)";
					}
					// 流速
					for (int j = 0; j < value.length; j++) {
						String val = value[j];
						if (val != null && val.indexOf("&") >= 0) {
							String[] splitstr = val.split("&");
							String splittime = splitstr[0];
							Double v = new Double(splitstr[1]);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date da = sdf.parse(splittime);
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(da);
							Minute minute = new Minute(tempCal.get(Calendar.MINUTE), tempCal.get(Calendar.HOUR_OF_DAY), tempCal.get(Calendar.DAY_OF_MONTH), tempCal
									.get(Calendar.MONTH) + 1, tempCal.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", unit, title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void p_drawchartMultiLineYear(Hashtable hash, String title1, String title2, int w, int h, String flag) {
		if (hash.size() != 0) {
			String unit = "";
			String[] keys = (String[]) hash.get("key");
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s = new TimeSeries[keys.length];
			try {
				for (int i = 0; i < keys.length; i++) {
					String key = keys[i];
					TimeSeries ss = new TimeSeries(key, Hour.class);
					String[] value = (String[]) hash.get(key);
					if (flag.equals("UtilHdx")) {
						unit = "y(kb/s)";
					} else {
						unit = "y(%)";
					}
					// 流速
					for (int j = 0; j < value.length; j++) {
						String val = value[j];
						if (val != null && val.indexOf("&") >= 0) {
							String[] splitstr = val.split("&");
							String splittime = splitstr[0];
							Double v = new Double(splitstr[1]);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date da = sdf.parse(splittime);
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(da);
							ss.addOrUpdate(new org.jfree.data.time.Hour(da), v);
						}
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", unit, title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}


	private void p_drawchartMultiLine(Hashtable hash, String title1, String title2, int w, int h, String flag) {
		if (hash.size() != 0) {
			String unit = (String) hash.get("unit");
			hash.remove("unit");
			String[] keys = (String[]) hash.get("key");
			if (keys == null) {
				draw_blank(title1, title2, w, h);
				return;
			}
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s = new TimeSeries[keys.length];
			try {
				for (int i = 0; i < keys.length; i++) {
					String key = keys[i];
					TimeSeries ss = new TimeSeries(key, Minute.class);
					Vector vector = (Vector) (hash.get(key));
					if (flag.equals("AllUtilHdxPerc")) {
						// 综合带宽利用率
					} else if (flag.equals("AllUtilHdx")) {
						// 综合流速
						for (int j = 0; j < vector.size(); j++) {
							AllUtilHdx obj = (AllUtilHdx) vector.get(j);
							Double v = new Double(obj.getThevalue());
							Calendar temp = obj.getCollecttime();
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("UtilHdxPerc")) {
						// 带宽利用率
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}

					} else if (flag.equals("UtilHdx")) {
						// 流速
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("ErrorsPerc")) {
						// 流速
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("DiscardsPerc")) {
						// 流速
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("Packs")) {
						// 数据包
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1,
									temp.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}


	public void draw_column(Hashtable bighash, String title1, String title2, int w, int h) {
		if (bighash.size() != 0) {
			ChartGraph cg = new ChartGraph();
			int size = bighash.size();
			double[][] d = new double[1][size];
			String c[] = new String[size];
			Hashtable hash;
			for (int j = 0; j < size; j++) {
				hash = (Hashtable) bighash.get(new Integer(j));
				c[j] = (String) hash.get("name");
				d[0][j] = Double.parseDouble((String) hash.get("Utilization" + "value"));
			}
			String rowKeys[] = { "" };
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, c, d); 
			cg.zhu(title1, title2, dataset, w, h);
		} else {
			draw_blank(title1, title2, w, h);
		}
		bighash = null;
	}

	private void p_drawchartMultiLine(Hashtable hash, String title1, String title2, int w, int h) {
		if (hash.size() != 0) {
			String unit = (String) hash.get("unit");
			hash.remove("unit");
			String[] keys = (String[]) hash.get("key");
			if (keys == null) {
				draw_blank(title1, title2, w, h);
				return;
			}
			ChartGraph cg = new ChartGraph();
			TimeSeries[] s = new TimeSeries[keys.length];
			try {
				for (int i = 0; i < keys.length; i++) {
					String key = keys[i];
					TimeSeries ss = new TimeSeries(key, Minute.class);
					Vector vector = (Vector) (hash.get(key));
					for (int j = 0; j < vector.size(); j++) {
						Vector obj = (Vector) vector.get(j);
						Double v = new Double((String) obj.get(0));
						String dt = (String) obj.get(1);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						Date time1 = sdf.parse(dt);
						Calendar temp = Calendar.getInstance();
						temp.setTime(time1);
						Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp
								.get(Calendar.YEAR));
						ss.addOrUpdate(minute, v);
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}
}
