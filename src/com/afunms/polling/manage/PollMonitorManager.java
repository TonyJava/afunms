package com.afunms.polling.manage;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.xy.XYDataset;
import org.jrobin.core.RrdDb;
import org.jrobin.core.RrdDef;
import org.jrobin.core.RrdException;
import org.jrobin.core.Sample;
import org.jrobin.core.Util;
import org.jrobin.graph.RrdGraph;
import org.jrobin.graph.RrdGraphDef;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.Arith;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.DateE;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SnmpService;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.AclBaseDao;
import com.afunms.config.dao.AclDetailDao;
import com.afunms.config.dao.CfgBaseInfoDao;
import com.afunms.config.dao.ErrptlogDao;
import com.afunms.config.dao.GatherTelnetConfigDao;
import com.afunms.config.dao.NodeconfigDao;
import com.afunms.config.dao.PolicyInterfaceDao;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.dao.QueueInfoDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.AclBase;
import com.afunms.config.model.Errptlog;
import com.afunms.config.model.GatherTelnetConfig;
import com.afunms.config.model.Nodeconfig;
import com.afunms.config.model.Procs;
import com.afunms.config.model.Supper;
import com.afunms.detail.service.OtherInfo.OtherInfoService;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.systemInfo.SystemInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.EventReportDao;
import com.afunms.event.dao.SyslogDao;
import com.afunms.event.model.EventList;
import com.afunms.event.model.EventReport;
import com.afunms.event.model.Syslog;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataDayManager;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.DeviceCollectEntity;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.ProcessInfo;
import com.afunms.polling.om.SoftwareCollectEntity;
import com.afunms.polling.om.StorageCollectEntity;
import com.afunms.polling.om.SystemCollectEntity;
import com.afunms.polling.snmp.WindowsSnmp;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.report.jfree.JFreeChartBrother;
import com.afunms.system.model.User;
import com.afunms.topology.dao.DiskForAS400Dao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.JobForAS400Dao;
import com.afunms.topology.dao.SystemPoolForAS400Dao;
import com.afunms.topology.dao.SystemValueForAS400Dao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.SubsystemForAS400;
import com.afunms.topology.model.SystemValueForAS400;
import com.afunms.topology.util.TopoHelper;
import com.afunms.topology.util.XmlOperator;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.rtf.RtfWriter2;

@SuppressWarnings("unchecked")
public class PollMonitorManager extends BaseManager implements ManagerInterface {

	private static Hashtable device_Status = null;
	static {
		device_Status = new Hashtable();
		device_Status.put("1", "δ֪");
		device_Status.put("2", "����");
		device_Status.put("3", "�澯");
		device_Status.put("4", "����");
		device_Status.put("5", "ֹͣ");
	};
	private static Hashtable device_Type = null;
	static {
		device_Type = new Hashtable();
		device_Type.put("1.3.6.1.2.1.25.3.1.1", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.2", "δ֪");
		device_Type.put("1.3.6.1.2.1.25.3.1.3", "CPU");
		device_Type.put("1.3.6.1.2.1.25.3.1.4", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.5", "��ӡ��");
		device_Type.put("1.3.6.1.2.1.25.3.1.6", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.10", "�Կ�");
		device_Type.put("1.3.6.1.2.1.25.3.1.11", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.12", "Э������");
		device_Type.put("1.3.6.1.2.1.25.3.1.13", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.14", "���ƽ����");
		device_Type.put("1.3.6.1.2.1.25.3.1.15", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.16", "��ӡ��");
		device_Type.put("1.3.6.1.2.1.25.3.1.17", "����");
		device_Type.put("1.3.6.1.2.1.25.3.1.18", "�Ŵ�");
		device_Type.put("1.3.6.1.2.1.25.3.1.19", "ʱ��");
		device_Type.put("1.3.6.1.2.1.25.3.1.20", "��̬�ڴ�");
		device_Type.put("1.3.6.1.2.1.25.3.1.21", "�̶��ڴ�");
	};

	private static Hashtable storage_Type = null;
	static {
		storage_Type = new Hashtable();
		storage_Type.put("1.3.6.1.2.1.25.2.1.1", "����");
		storage_Type.put("1.3.6.1.2.1.25.2.1.2", "�����ڴ�");
		storage_Type.put("1.3.6.1.2.1.25.2.1.3", "�����ڴ�");
		storage_Type.put("1.3.6.1.2.1.25.2.1.4", "Ӳ��");
		storage_Type.put("1.3.6.1.2.1.25.2.1.5", "�ƶ�Ӳ��");
		storage_Type.put("1.3.6.1.2.1.25.2.1.6", "����");
		storage_Type.put("1.3.6.1.2.1.25.2.1.7", "����");
		storage_Type.put("1.3.6.1.2.1.25.2.1.8", "�ڴ���");
	};
	DateE datemanager = new DateE();
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	I_HostCollectData hostmanager = new HostCollectDataManager();
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String list() {
		HostNodeDao dao = new HostNodeDao();
		request.setAttribute("list", dao.loadNetwork(0));
		return "/topology/network/list.jsp";
	}

	private String speed() {
		String id = request.getParameter("id");
		String flag = request.getParameter("flag");
		request.setAttribute("id", id);
		request.setAttribute("flag", flag);
		return "/topology/network/netspeed.jsp";
	}

	private String netif() {
		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		String orderflag = getParaValue("orderflag");
		if (orderflag == null || orderflag.trim().length() == 0)
			orderflag = "index";
		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

		Vector vector = new Vector();
		String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed",
				"ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
		try {
			vector = hostlastmanager.getInterface_share(host.getIpAddress(),
					netInterfaceItem, orderflag, starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// �õ�ϵͳ����ʱ��
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
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
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(
				host.getIpAddress());
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

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		String orderflag = getParaValue("orderflag");
		if (orderflag == null || orderflag.trim().length() == 0)
			orderflag = "index";
		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

		Vector vector = new Vector();
		String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed",
				"ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
		try {
			vector = hostlastmanager.getInterface_share(host.getIpAddress(),
					netInterfaceItem, orderflag, starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// �õ�ϵͳ����ʱ��
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
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
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(
				host.getIpAddress());
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

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		String orderflag = getParaValue("orderflag");
		if (orderflag == null || orderflag.trim().length() == 0)
			orderflag = "index";
		String[] time = { "", "" };
		getTime(request, time);
		String starttime = time[0];
		String endtime = time[1];

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

		Vector vector = new Vector();
		String[] netInterfaceItem = { "index", "ifDescr", "ifSpeed",
				"ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
		try {
			vector = hostlastmanager.getInterface_share(host.getIpAddress(),
					netInterfaceItem, orderflag, starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// �õ�ϵͳ����ʱ��
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
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
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(
				host.getIpAddress());
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

	private String netenv() {
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
		return "/detail/net_env.jsp";
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
		String flag = request.getParameter("flag");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
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
		// ��collectdataȡcpu,�ڴ����ʷ����
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// String pingconavg ="";
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		// maxhash = new Hashtable();
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// �õ�ϵͳ����ʱ��
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		// ��order��������Ϣ����
		String order = "MemoryUtilization";
		((HostLastCollectDataManager) hostlastmanager).setHost(host);
		if ((request.getParameter("orderflag") != null)
				&& (!request.getParameter("orderflag").equals(""))) {
			order = request.getParameter("orderflag");
		}
		if (order.equalsIgnoreCase("CpuTime")) {
			((HostLastCollectDataManager) hostlastmanager).setCpuTime(true);
		}
		String runmodel = PollingEngine.getCollectwebflag();
		try {
			if ("0".equals(runmodel)) {
				// �ɼ�������Ǽ���ģʽ
				processhash = hostlastmanager.getProcess_share(host
						.getIpAddress(), "Process", order, starttime, endtime);
				Vector pingData = (Vector) ShareData.getPingdata().get(
						host.getIpAddress());
				if (pingData != null && pingData.size() > 0) {
					PingCollectEntity pingdata = (PingCollectEntity) pingData
							.get(0);
					Calendar tempCal = (Calendar) pingdata.getCollecttime();
					Date cc = tempCal.getTime();
					collecttime = sdf1.format(cc);
				}
			} else {
				// �ɼ�������Ƿ���ģʽ
				processhash = hostlastmanager.getProcess(host.getIpAddress(),
						"Process", order, starttime, endtime);
				OtherInfoService otherInfoService = new OtherInfoService(
						nodedto.getId() + "", nodedto.getType(), nodedto
								.getSubtype());
				collecttime = otherInfoService.getCollecttime();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Hashtable phash;// ԭ�����ڼ�����м����
		Hashtable newProcessHash = new Hashtable();// �洢������hashtable
		Hashtable detailHash = new Hashtable();
		Pattern p1 = Pattern.compile("(\\d+):(\\d+)");
		if (processhash != null) {
			for (int m = 0; m < processhash.size(); m++) {
				phash = (Hashtable) processhash.get(new Integer(m));
				if (phash != null) {
					String Name = ((String) phash.get("Name")).trim();
					if (newProcessHash.containsKey(Name)) {
						ProcessInfo totalProcess = (ProcessInfo) newProcessHash
								.get(Name);
						String CpuTime = (String) phash.get("CpuTime");
						String CpuUtilization = (String) phash
								.get("CpuUtilization");
						String Memory = (String) phash.get("Memory");
						String MemoryUtilization = (String) phash
								.get("MemoryUtilization");
						String threadCount = (String) phash.get("ThreadCount");
						String handleCount = (String) phash.get("HandleCount");
						if (CpuTime != null) {
							if (CpuTime.indexOf(":") != -1) {
								Matcher matcher = p1.matcher(CpuTime);
								if (matcher.find()) {
									String t1 = matcher.group(1);
									String t2 = matcher.group(2);
									float sumOfCPU = Float.parseFloat(t1) * 60
											+ Float.parseFloat(t2);
									totalProcess
											.setCpuTime(sumOfCPU
													+ (Float) totalProcess
															.getCpuTime());
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime
										.replace("��", ""));
								totalProcess.setCpuTime(sumOfCPU
										+ (Float) totalProcess.getCpuTime());
							}
						}
						Float sumOfCpuUtilization = 0f;
						if (CpuUtilization != null
								&& CpuUtilization.trim().length() > 0) {
							sumOfCpuUtilization = Float
									.parseFloat(CpuUtilization.substring(0,
											CpuUtilization.length() - 1));
							totalProcess.setCpuUtilization(sumOfCpuUtilization
									+ (Float) totalProcess.getCpuUtilization());
						} else {
							totalProcess.setCpuUtilization("-");
						}

						Float sumOfMem = Float.valueOf("0");
						if (Memory.trim().length() > 1) {
							sumOfMem = Float.parseFloat(Memory.substring(0,
									Memory.length() - 1));
						}
						totalProcess.setMemory(sumOfMem
								+ (Float) totalProcess.getMemory());
						NumberFormat numberFormat = new DecimalFormat();
						numberFormat.setMaximumFractionDigits(0);

						Float sumOfMemUtilization = Float.valueOf("0");
						if (MemoryUtilization.trim().length() > 1) {
							sumOfMemUtilization = Float
									.parseFloat(MemoryUtilization.substring(0,
											MemoryUtilization.length() - 1));
						}
						Float memoryUtilization = sumOfMemUtilization
								+ (Float) totalProcess.getMemoryUtilization();

						totalProcess.setMemoryUtilization(memoryUtilization);

						if (threadCount != null) {
							totalProcess.setThreadCount(threadCount);
						}
						if (handleCount != null) {
							totalProcess.setHandleCount(handleCount);
						}
						ProcessInfo processInfo = new ProcessInfo();
						processInfo.setName((String) phash.get("Name"));
						processInfo.setType((String) phash.get("Type"));
						processInfo.setStatus((String) phash.get("Status"));
						if (CpuTime != null) {
							if (CpuTime.indexOf(":") != -1) {
								Matcher matcher2 = p1.matcher(CpuTime);
								if (matcher2.find()) {
									String t1 = matcher2.group(1);
									String t2 = matcher2.group(2);
									float sumOfCPU = Float.parseFloat(t1) * 60
											+ Float.parseFloat(t2);
									processInfo.setCpuTime(sumOfCPU);
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime
										.replace("��", ""));
								processInfo.setCpuTime(sumOfCPU);
							}
						}

						processInfo.setUSER((String) phash.get("USER"));
						processInfo.setStartTime((String) phash
								.get("StartTime"));

						processInfo.setCpuUtilization(sumOfCpuUtilization);

						processInfo.setPid((String) phash.get("process_id"));
						processInfo.setMemoryUtilization(sumOfMemUtilization);
						processInfo.setMemory(sumOfMem);
						processInfo.setThreadCount(threadCount);
						processInfo.setHandleCount(handleCount);
						((Vector) detailHash.get(((String) phash.get("Name"))
								.trim())).add(processInfo);
					} else {
						ProcessInfo processInfo = new ProcessInfo();
						processInfo.setName(Name);
						processInfo.setUSER((String) phash.get("USER"));
						processInfo.setType((String) phash.get("Type"));
						processInfo.setStatus((String) phash.get("Status"));

						String CpuTime = (String) phash.get("CpuTime");
						if (CpuTime != null) {
							if (CpuTime.indexOf(":") != -1) {
								Matcher matcher = p1.matcher(CpuTime);
								if (matcher.find()) {
									String t1 = matcher.group(1);
									String t2 = matcher.group(2);
									float sumOfCPU = Float.parseFloat(t1) * 60
											+ Float.parseFloat(t2);
									processInfo.setCpuTime(sumOfCPU);
								}
							} else {
								float sumOfCPU = Float.parseFloat(CpuTime
										.replace("��", ""));
								processInfo.setCpuTime(sumOfCPU);
							}
						}

						String MemoryUtilization = (String) phash
								.get("MemoryUtilization");
						Float sumOfMemUtilization = Float.valueOf("0");
						if (MemoryUtilization.trim().length() > 1) {
							sumOfMemUtilization = Float
									.parseFloat(MemoryUtilization.substring(0,
											MemoryUtilization.length() - 1));
						}
						processInfo.setMemoryUtilization(sumOfMemUtilization);

						String Memory = (String) phash.get("Memory");
						Float sumOfMem = Float.valueOf("0");
						if (Memory.trim().length() > 1) {
							sumOfMem = Float.parseFloat(Memory.substring(0,
									Memory.length() - 1));
						}
						processInfo.setMemory(sumOfMem);

						String CpuUtilization = (String) phash
								.get("CpuUtilization");
						if (CpuUtilization != null
								&& CpuUtilization.trim().length() > 0) {
							Float sumOfCpuUtilization = Float
									.parseFloat(CpuUtilization.substring(0,
											CpuUtilization.length() - 1));
							processInfo.setCpuUtilization(sumOfCpuUtilization);
						} else {
							processInfo.setCpuUtilization("-");
						}
						processInfo.setPid((String) phash.get("process_id"));
						String threadCount = (String) phash.get("ThreadCount");
						processInfo.setThreadCount(threadCount);
						String handleCount = (String) phash.get("HandleCount");
						processInfo.setHandleCount(handleCount);
						ProcessInfo newProcessInfo = processInfo.clone();
						newProcessHash.put(Name, processInfo);
						Vector detailVect = new Vector();
						detailVect.add(newProcessInfo);
						detailHash.put(Name, detailVect);
					}
				}
			}
		}
		Enumeration newProEnu = newProcessHash.keys();
		while (newProEnu.hasMoreElements()) {
			String processName = (String) newProEnu.nextElement();
			ProcessInfo p = (ProcessInfo) newProcessHash.get(processName);
			Vector v = (Vector) detailHash.get(processName);
			p.setCount(v.size());
		}

		String processName = this.getParaValue("processName");

		Vector detailVect = null;
		String layer = this.getParaValue("layer");
		if (processName != null) {
			if (detailHash.containsKey(processName)) {
				detailVect = (Vector) detailHash.get(processName);
			}
		}

		List list = new ArrayList();
		String orderflag = this.getParaValue("orderflag");
		if (detailVect == null) {
			Collection collection = newProcessHash.values();
			Iterator it = collection.iterator();

			while (it.hasNext()) {
				ProcessInfo info = (ProcessInfo) it.next();
				list.add(info);
			}
		} else {
			list = detailVect;
		}
		String orderflag1 = this.getParaValue("orderflag1");
		boolean orderflagBoolean = true;
		System.out.println(orderflag1);
		if ("0".equals(orderflag1)) {
			orderflagBoolean = false;
			orderflag1 = "1";
		} else {
			orderflag1 = "0";
		}
		sort(list, orderflag, orderflagBoolean);
		dateFormat(list);
		request.setAttribute("flag", flag);
		request.setAttribute("orderflag1", orderflag1);
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
		request.setAttribute("newProcessHash", newProcessHash);
		request.setAttribute("detailVect", detailVect);
		request.setAttribute("list", list);
		request.setAttribute("layer", layer);

		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmiproc.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxproc.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxproc.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxproc.jsp";
		} else if ((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				|| host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host
				.getCollecttype() == SystemConstant.COLLECTTYPE_SSH)
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixproc.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solarisproc.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpproc.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& (host.getSysOid().indexOf("scounix") >= 0 || host
						.getSysOid().indexOf("scoopenserver") >= 0)) {
			// scounixware������,��ת��scounixware�ɼ�չʾҳ��
			return "/detail/host_scounixproc.jsp";
		} else
			return "/detail/host_proc.jsp";
	}

	private void dateFormat(List list) {
		for (int i = 0; i < list.size(); i++) {
			ProcessInfo processInfo = (ProcessInfo) list.get(i);
			Float CpuTime = (Float) processInfo.getCpuTime();
			int int_CpuTime = CpuTime.intValue();
			int fenzhong = int_CpuTime / 60;
			int miaozhong = int_CpuTime % 60;
			String s_CpuTime = fenzhong + ":" + miaozhong + "��";
			processInfo.setCpuTime(s_CpuTime);
			processInfo.setCpuUtilization(processInfo.getCpuUtilization()
					.toString()
					+ "%");
			processInfo.setMemory(processInfo.getMemory() + "K");
			processInfo.setMemoryUtilization(processInfo.getMemoryUtilization()
					+ "%");
		}
	}

	private List sort(List list, String type, final boolean order) {
		if (type.equals("CpuTime")) {
			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					ProcessInfo p1 = (ProcessInfo) o1;
					ProcessInfo p2 = (ProcessInfo) o2;
					if ((Float) p1.getCpuTime() <= (Float) p2.getCpuTime()
							&& order)
						return 1;
					else
						return -1;
				}
			});
		}
		if (type.equals("CpuUtilization")) {
			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					ProcessInfo p1 = (ProcessInfo) o1;
					ProcessInfo p2 = (ProcessInfo) o2;
					if ((Float) p1.getCpuUtilization() <= (Float) p2
							.getCpuUtilization()
							&& order)
						return 1;
					else
						return -1;
				}
			});
		}
		if (type.equals("Memory") || type.equals("")) {
			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					ProcessInfo p1 = (ProcessInfo) o1;
					ProcessInfo p2 = (ProcessInfo) o2;
					if ((Float) p1.getMemory() <= (Float) p2.getMemory()
							&& order)
						return 1;
					else
						return -1;
				}
			});
		}
		if (type.equals("MemoryUtilization")) {
			Collections.sort(list, new Comparator() {
				public int compare(Object o1, Object o2) {
					ProcessInfo p1 = (ProcessInfo) o1;
					ProcessInfo p2 = (ProcessInfo) o2;
					if ((Float) p1.getMemoryUtilization() <= (Float) p2
							.getMemoryUtilization()
							&& order)
						return 1;
					else
						return -1;
				}
			});
		}
		return list;
	}

	private String hostservice() {
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

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

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
		// ��collectdataȡcpu,�ڴ����ʷ����
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {

				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// �õ�ϵͳ����ʱ��
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		// ��order��������Ϣ����
		String order = "MemoryUtilization";
		if ((request.getParameter("orderflag") != null)
				&& (!request.getParameter("orderflag").equals(""))) {
			order = request.getParameter("orderflag");
		}
		try {
			processhash = hostlastmanager.getProcess_share(host.getIpAddress(),
					"Process", order, starttime, endtime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(
				host.getIpAddress());
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

		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmiservice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxservice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxservice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxservice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixservice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixservice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solarisservice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpunixservice.jsp";
		} else
			return "/detail/host_service.jsp";
	}

	private String hostarp() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Vector vector = new Vector();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			vector = (Vector) ipAllData.get("ipmac");
		}

		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("vector", vector);
		session.setAttribute("ipmacV", vector);
		request.setAttribute("userhash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmiarp.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxarp.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxarp.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxarp.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixarp.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solarisarp.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpunixarp.jsp";
		} else
			return "/detail/host_arp.jsp";
	}

	private String hostdevice() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Vector vector = new Vector();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			vector = (Vector) ipAllData.get("device");
		}

		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("vector", vector);
		request.setAttribute("userhash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		session.setAttribute("deviceV", vector);
		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmidevice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxdevice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxdevice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxdevice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixdevice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solarisdevice.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpunixdevice.jsp";
		} else
			return "/detail/host_device.jsp";
	}

	private String hoststorage() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Vector vector = new Vector();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			vector = (Vector) ipAllData.get("storage");
		}
		session.setAttribute("storageV", vector);
		// ��order��������Ϣ����

		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("vector", vector);
		request.setAttribute("userhash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));

		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmistorage.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxstorage.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxstorage.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxstorage.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixstorage.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solarisstorage.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpunixstorage.jsp";
		} else
			return "/detail/host_storage.jsp";
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

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		// ��collectdataȡcpu,�ڴ����ʷ����
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

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
			list = syslogdao.getQuery(host.getIpAddress(), starttime2, totime2,
					priorityname);
		} catch (Exception e) {
			e.printStackTrace();
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
		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmisyslog.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxsyslog.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxsyslog.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxsyslog.jsp";
		} else if ((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				|| host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host
				.getCollecttype() == SystemConstant.COLLECTTYPE_SSH)
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixsyslog.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solarissyslog.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpsyslog.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& (host.getSysOid().indexOf("scounix") >= 0 || host
						.getSysOid().indexOf("scoopenserver") >= 0)) {
			// scounixware������,��ת��scounixware�ɼ�չʾҳ��
			return "/detail/host_scounixsyslog.jsp";
		} else
			return "/detail/host_syslog.jsp";

	}

	private String hosterrpt() {
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		List list = new ArrayList();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		String sysname = "";

		String b_time = "";
		String t_time = "";

		Hashtable pagehash = new Hashtable();
		Hashtable paginghash = new Hashtable();
		Nodeconfig nodeconfig = new Nodeconfig();
		Vector cpuV = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);

		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		// ��collectdataȡcpu,�ڴ����ʷ����
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		String runmodel = PollingEngine.getCollectwebflag();
		if ("0".equals(runmodel)) {
			// �ɼ�������Ǽ���ģʽ
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				if (ipAllData.containsKey("cpu")) {
					cpuV = (Vector) ipAllData.get("cpu");
					if (cpuV != null && cpuV.size() > 0) {
						CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
						if (cpu != null && cpu.getThevalue() != null) {
							cpuvalue = new Double(cpu.getThevalue());
						}
					}
				}
				// �õ�ϵͳ����ʱ��
				if (ipAllData.containsKey("system")) {
					Vector systemV = (Vector) ipAllData.get("system");
					if (systemV != null && systemV.size() > 0) {
						for (int i = 0; i < systemV.size(); i++) {
							SystemCollectEntity systemdata = (SystemCollectEntity) systemV
									.get(i);
							if (systemdata.getSubentity().equalsIgnoreCase(
									"sysUpTime")) {
								sysuptime = systemdata.getThevalue();
							}
							if (systemdata.getSubentity().equalsIgnoreCase(
									"sysServices")) {
								sysservices = systemdata.getThevalue();
							}
							if (systemdata.getSubentity().equalsIgnoreCase(
									"sysDescr")) {
								sysdescr = systemdata.getThevalue();
							}
							if (systemdata.getSubentity().equalsIgnoreCase(
									"SysName")) {
								sysname = systemdata.getThevalue();
							}
						}
					}
				}
				if (ipAllData.containsKey("pagehash")) {
					pagehash = (Hashtable) ipAllData.get("pagehash");
				}
				if (ipAllData.containsKey("paginghash")) {
					paginghash = (Hashtable) ipAllData.get("paginghash");
				}
				if (ipAllData.containsKey("nodeconfig")) {
					nodeconfig = (Nodeconfig) ipAllData.get("nodeconfig");
				}
				if (ipAllData.containsKey("collecttime")) {
					collecttime = (String) ipAllData.get("collecttime");
				}
			}
		} else {
			CpuInfoService cpuInfoService = new CpuInfoService(nodedto.getId()
					+ "", nodedto.getType(), nodedto.getSubtype());
			cpuV = cpuInfoService.getCpuInfo();
			if (cpuV != null && cpuV.size() > 0) {
				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				if (cpu != null && cpu.getThevalue() != null) {
					cpuvalue = new Double(cpu.getThevalue());
				}
			}
			// �õ�ϵͳ����ʱ��
			SystemInfoService systemInfoService = new SystemInfoService(nodedto
					.getId()
					+ "", nodedto.getType(), nodedto.getSubtype());
			Vector systemV = systemInfoService.getSystemInfo();
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("SysName")) {
						sysname = systemdata.getThevalue();
					}
				}
			}
			OtherInfoService otherInfoService = new OtherInfoService(nodedto
					.getId()
					+ "", nodedto.getType(), nodedto.getSubtype());
			collecttime = otherInfoService.getCollecttime();
			paginghash = otherInfoService.getPaginghash();
			pagehash = otherInfoService.getPagehash();
			NodeconfigDao nodeconfigDao = new NodeconfigDao();
			try {
				nodeconfig = nodeconfigDao.getByNodeID(nodedto.getId() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodeconfigDao.close();
			}
		}
		list = getHosterrptList(tmp);
		// ��order��������Ϣ����
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
		String priorityname = getParaValue("priorityname");
		if (priorityname == null)
			priorityname = "all";

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}
		request.setAttribute("ConnectUtilizationhash", ConnectUtilizationhash);
		request.setAttribute("nodeconfig", nodeconfig);
		request.setAttribute("cpuV", cpuV);
		request.setAttribute("pagehash", pagehash);
		request.setAttribute("paginghash", paginghash);
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
		request.setAttribute("SysName", sysname);
		request.setAttribute("pingconavg", new Double(pingconavg));
		// Aix������,��ת��Aix�ɼ�չʾҳ��
		return "/detail/host_aixerrpt.jsp";

	}

	/**
	 * ��ȡ Hosterrpt ���б�
	 */
	private List getHosterrptList(String id) {
		List list = null;
		ErrptlogDao errptlogDao = new ErrptlogDao();
		try {
			list = errptlogDao.findByCondition(getHosterrptSQL(id));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			errptlogDao.close();
		}
		return list;
	}

	/**
	 * ��ȡ Hosterrpt �б� �� SQL ���
	 * 
	 * @return
	 */
	private String getHosterrptSQL(String id) {
		String startdate = getParaValue("startdate");
		String todate = getParaValue("todate");
		String errpttype = getParaValue("errpttype");
		String errptclass = getParaValue("errptclass");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (startdate == null) {
			startdate = sdf.format(new Date());
		}
		String startTime = startdate + " 00:00:00";
		if (todate == null) {
			todate = sdf.format(new Date());
		}
		String toTime = todate + " 23:59:59";

		String sql = "";
		if (SystemConstant.DBType.equals("mysql")) {
			sql = " where collettime>='" + startTime + "' and collettime<='"
					+ toTime + "' ";
		} else if (SystemConstant.DBType.equalsIgnoreCase("oracle")) {
			sql = " where collettime>=to_date('" + startTime
					+ "','yyyy-mm-dd hh24:mi:ss') and collettime<=to_date('"
					+ toTime + "','yyyy-mm-dd hh24:mi:ss') ";
		}
		if (errpttype == null || "all".equals(errpttype)) {
			errpttype = "all";
		} else {
			sql += " and errpttype='" + errpttype.toUpperCase() + "'";
		}

		if (errptclass == null || "all".equals(errptclass)) {
			errptclass = "all";
		} else {
			sql += " and errptclass='" + errptclass.toUpperCase() + "'";
		}
		sql = sql + " and hostid='" + id + "'";
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("errpttype", errpttype);
		request.setAttribute("errptclass", errptclass);
		return sql;
	}

	private String hosterrptDetail() {
		String errptlogId = getParaValue("errptlogId");
		Errptlog errptlog = null;
		ErrptlogDao errptlogDao = new ErrptlogDao();
		try {
			errptlog = (Errptlog) errptlogDao.findByID(errptlogId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			errptlogDao.close();
		}
		request.setAttribute("errptlog", errptlog);
		return "/detail/host_aixerrpt_detail.jsp";
	}

	private String hostsw() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable processhash = new Hashtable();
		Vector softwareV = null;

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());

		if (ipAllData != null) {
			try {
				softwareV = (Vector) ipAllData.get("software");

			} catch (Exception ex) {
				ex.printStackTrace();
			}
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

		request.setAttribute("softwareV", softwareV);

		session.setAttribute("softwareV", softwareV);
		return "/detail/host_sw.jsp";
	}

	private String hostroute() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable processhash = new Hashtable();
		List routelist = new ArrayList();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = "";
		String sysuptime = "";
		String sysservices = "";
		String sysdescr = "";

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

		SupperDao supperdao = new SupperDao();
		Supper supper = null;
		try {
			supper = (Supper) supperdao.findByID(host.getSupperid() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			supperdao.close();
		}
		request.setAttribute("supper", supper);

		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";

		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());

		if (ipAllData != null) {

			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {
				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				if (cpu != null && cpu.getThevalue() != null) {
					cpuvalue = new Double(cpu.getThevalue());
				}
			}
			// �õ�ϵͳ����ʱ��
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
			try {
				routelist = (List) ipAllData.get("routelist");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Hashtable ConnectUtilizationhash = new Hashtable();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(
				host.getIpAddress());
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
		request.setAttribute("routelist", routelist);
		return "/detail/host_aixroute.jsp";
	}

	private String hostwinservice() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();// "Cpu"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable processhash = new Hashtable();
		Vector serviceV = null;

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

		String[] time = { "", "" };
		getTime(request, time);
		String time1 = request.getParameter("begindate");
		if (time1 == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			time1 = sdf.format(new Date());
		}

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());

		if (ipAllData != null) {
			try {
				serviceV = (Vector) ipAllData.get("winservice");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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
		request.setAttribute("serviceV", serviceV);
		return "/detail/host_winservice.jsp";
	}

	private String refresh() {
		Vector softwareVector = new Vector();
		WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(id));
		String[] oids = null;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());

		SnmpService snmp = new SnmpService();
		softwareVector = new Vector();
		try {
			oids = new String[] { "1.3.6.1.2.1.25.6.3.1.2", // ����
					"1.3.6.1.2.1.25.6.3.1.3", // id
					"1.3.6.1.2.1.25.6.3.1.4", // ���
					"1.3.6.1.2.1.25.6.3.1.5" }; // ��װ����

			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(), host
						.getCommunity(), oids);
			} catch (Exception e) {
				e.printStackTrace();
				valueArray = null;
			}
			for (int i = 0; i < valueArray.length; i++) {
				SoftwareCollectEntity softwaredata = new SoftwareCollectEntity();
				String name = valueArray[i][0];
				String swid = valueArray[i][1];
				String type = valueArray[i][2];
				if (type.equalsIgnoreCase("4")) {
					type = "Ӧ�����";
				} else {
					type = "ϵͳ���";
				}
				String insdate = valueArray[i][3];
				String swdate = wins.getDate(insdate);
				softwaredata.setIpaddress(host.getIpAddress());
				softwaredata.setName(name);
				softwaredata.setSwid(swid);
				softwaredata.setType(type);
				softwaredata.setInsdate(swdate);
				softwareVector.addElement(softwaredata);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ipAllData.put("softwareV", softwareVector);
		ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		return "/monitor.do?action=hostsw&id=" + id + "&ipaddress="
				+ host.getIpAddress();
	}

	private String refreshhostarp() {
		Vector ipmacVector = new Vector();
		// WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(id));
		String[] oids = null;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());

		SnmpService snmp = new SnmpService();
		try {
			oids = new String[] { "1.3.6.1.2.1.4.22.1.1", // 1.ifIndex
					"1.3.6.1.2.1.4.22.1.2", // 2.mac
					"1.3.6.1.2.1.4.22.1.3", // 3.ip
					"1.3.6.1.2.1.4.22.1.4" }; // 4.type
			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(), host
						.getCommunity(), oids);
			} catch (Exception e) {
				valueArray = null;
				e.printStackTrace();
				SysLogger.error(host.getIpAddress() + "_CiscoSnmp");
			}
			for (int i = 0; i < valueArray.length; i++) {
				IpMac ipmac = new IpMac();
				for (int j = 0; j < 4; j++) {
					String sValue = valueArray[i][j];
					if (sValue == null)
						continue;
					if (j == 0) {
						ipmac.setIfindex(sValue);
					} else if (j == 1) {
						ipmac.setMac(sValue);
					} else if (j == 2) {
						ipmac.setIpaddress(sValue);
					}
				}
				ipmac.setIfband("0");
				ipmac.setIfsms("0");
				ipmac.setCollecttime(new GregorianCalendar());
				ipmac.setRelateipaddr(host.getIpAddress());
				ipmacVector.addElement(ipmac);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ipAllData.put("ipmac", ipmacVector);
		ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		return "/monitor.do?action=hostarp&id=" + id + "&ipaddress="
				+ host.getIpAddress();
	}

	private String refreshhostdevice() {
		Vector deviceVector = new Vector();
		String id = getParaValue("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(id));
		String[] oids = null;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());

		SnmpService snmp = new SnmpService();
		try {
			oids = new String[] { "1.3.6.1.2.1.25.3.2.1.1", // hrDeviceIndex
					"1.3.6.1.2.1.25.3.2.1.2", // hrDeviceType
					"1.3.6.1.2.1.25.3.2.1.3", // hrDeviceDescr
					"1.3.6.1.2.1.25.3.2.1.5" }; // hrDeviceStatus
			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(), host
						.getCommunity(), oids);
			} catch (Exception e) {
				valueArray = null;
				e.printStackTrace();
				SysLogger.error(host.getIpAddress() + "_HostSnmp");
			}
			DeviceCollectEntity devicedata = null;
			for (int i = 0; i < valueArray.length; i++) {
				devicedata = new DeviceCollectEntity();
				String devindex = valueArray[i][0];
				String type = valueArray[i][1];
				String name = valueArray[i][2];
				String status = valueArray[i][3];
				if (status == null)
					status = "";
				if (device_Status.containsKey(status))
					status = (String) device_Status.get(status);
				devicedata.setDeviceindex(devindex);
				devicedata.setIpaddress(host.getIpAddress());
				devicedata.setName(name);
				devicedata.setStatus(status);
				devicedata.setType((String) device_Type.get(type));
				deviceVector.addElement(devicedata);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ipAllData.put("device", deviceVector);
		ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		return "/monitor.do?action=hostdevice&id=" + id + "&ipaddress="
				+ host.getIpAddress();
	}

	private String refreshhoststorage() {
		Vector storageVector = new Vector();
		String id = getParaValue("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(id));
		String[] oids = null;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());

		SnmpService snmp = new SnmpService();
		try {
			oids = new String[] { "1.3.6.1.2.1.25.2.3.1.1", // hrStorageIndex
					"1.3.6.1.2.1.25.2.3.1.2", // hrStorageType
					"1.3.6.1.2.1.25.2.3.1.3", // hrStorageDescr
					"1.3.6.1.2.1.25.2.3.1.4", // hrStorageAllocationUnits
					"1.3.6.1.2.1.25.2.3.1.5" }; // hrStorageSize
			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(), host
						.getCommunity(), oids);
			} catch (Exception e) {
				valueArray = null;
				e.printStackTrace();
				SysLogger.error(host.getIpAddress() + "_HostSnmp");
			}
			StorageCollectEntity storagedata = null;
			for (int i = 0; i < valueArray.length; i++) {
				storagedata = new StorageCollectEntity();
				String storageindex = valueArray[i][0];
				String type = valueArray[i][1];
				String name = valueArray[i][2];
				String byteunit = valueArray[i][3];
				String cap = valueArray[i][4];
				int allsize = Integer.parseInt(cap.trim());

				float size = 0.0f;
				size = allsize * Long.parseLong(byteunit) * 1.0f / 1024 / 1024;
				String unit = "";
				if (size >= 1024.0f) {
					size = size / 1024;
					unit = "G";
				} else {
					unit = "M";
				}
				storagedata.setStorageindex(storageindex);
				storagedata.setIpaddress(host.getIpAddress());
				storagedata.setName(name);
				storagedata.setCap(Arith.floatToStr(size + "", 1, 0) + unit);
				storagedata.setType((String) storage_Type.get(type));
				storageVector.addElement(storagedata);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ipAllData.put("storage", storageVector);
		ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		return "/monitor.do?action=hoststorage&id=" + id + "&ipaddress="
				+ host.getIpAddress();
	}

	private String insertdb() {
		Vector softwareVector = new Vector();
		DBManager dbmanager = new DBManager();
		WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(id));
		String[] oids = null;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		String ip = host.getIpAddress();
		String allipstr = SysUtil.doip(ip);
		String tablename = "software" + allipstr;
		String delsql = "delete from " + tablename;
		try {
			dbmanager.executeUpdate(delsql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SnmpService snmp = new SnmpService();
		softwareVector = new Vector();
		try {
			oids = new String[] { "1.3.6.1.2.1.25.6.3.1.2", // ����
					"1.3.6.1.2.1.25.6.3.1.3", // id
					"1.3.6.1.2.1.25.6.3.1.4", // ���
					"1.3.6.1.2.1.25.6.3.1.5" }; // ��װ����

			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(), host
						.getCommunity(), oids);
			} catch (Exception e) {
				valueArray = null;
				SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
			}
			for (int i = 0; i < valueArray.length; i++) {
				SoftwareCollectEntity softwaredata = new SoftwareCollectEntity();
				String name = valueArray[i][0];
				String swid = valueArray[i][1];
				String type = valueArray[i][2];
				if (type.equalsIgnoreCase("4")) {
					type = "Ӧ�����";
				} else {
					type = "ϵͳ���";
				}
				String insdate = valueArray[i][3];
				String swdate = wins.getDate(insdate);
				softwaredata.setIpaddress(host.getIpAddress());
				softwaredata.setName(name);
				softwaredata.setSwid(swid);
				softwaredata.setType(type);
				softwaredata.setInsdate(swdate);
				softwareVector.addElement(softwaredata);
				String sql = "insert into " + tablename
						+ " (ipaddress,name,swid,type,insdate) " + "values(\""
						+ ip + "\",\"" + name + "\",\"" + swid + "\",\"" + type
						+ "\",\"" + swdate + "\")";
				try {
					dbmanager.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();

		}
		ipAllData.put("softwareV", softwareVector);
		ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		return "/monitor.do?action=hostsw&id=" + id + "&ipaddress="
				+ host.getIpAddress();
	}

	private String insertarpdb() {
		Vector softwareVector = new Vector();
		DBManager dbmanager = new DBManager();
		WindowsSnmp wins = new WindowsSnmp();
		String id = getParaValue("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(id));
		String[] oids = null;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		String ip = host.getIpAddress();
		String allipstr = SysUtil.doip(ip);
		String tablename = "software" + allipstr;
		String delsql = "delete from " + tablename;
		try {
			dbmanager.executeUpdate(delsql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		SnmpService snmp = new SnmpService();
		softwareVector = new Vector();
		try {
			oids = new String[] { "1.3.6.1.2.1.25.6.3.1.2", // ����
					"1.3.6.1.2.1.25.6.3.1.3", // id
					"1.3.6.1.2.1.25.6.3.1.4", // ���
					"1.3.6.1.2.1.25.6.3.1.5" }; // ��װ����

			String[][] valueArray = null;
			try {
				valueArray = snmp.getTableData(host.getIpAddress(), host
						.getCommunity(), oids);
			} catch (Exception e) {
				valueArray = null;
				SysLogger.error(host.getIpAddress() + "_WindowsSnmp");
			}
			for (int i = 0; i < valueArray.length; i++) {
				SoftwareCollectEntity softwaredata = new SoftwareCollectEntity();
				String name = valueArray[i][0];
				String swid = valueArray[i][1];
				String type = valueArray[i][2];
				if (type.equalsIgnoreCase("4")) {
					type = "Ӧ�����";
				} else {
					type = "ϵͳ���";
				}
				String insdate = valueArray[i][3];
				String swdate = wins.getDate(insdate);
				softwaredata.setIpaddress(host.getIpAddress());
				softwaredata.setName(name);
				softwaredata.setSwid(swid);
				softwaredata.setType(type);
				softwaredata.setInsdate(swdate);
				softwareVector.addElement(softwaredata);
				String sql = "insert into " + tablename
						+ " (ipaddress,name,swid,type,insdate) " + "values('"
						+ ip + "','" + name + "','" + swid + "','" + type
						+ "','" + swdate + "')";
				try {
					dbmanager.executeUpdate(sql);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbmanager.close();
		}
		ipAllData.put("softwareV", softwareVector);
		ShareData.getSharedata().put(host.getIpAddress(), ipAllData);
		return "/monitor.do?action=hostarp&id=" + id + "&ipaddress="
				+ host.getIpAddress();
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
		Hashtable hash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Hashtable processhash = new Hashtable();
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

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

			String[] time = { "", "" };
			getTime(request, time);
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			EventListDao dao = new EventListDao();
			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				list = dao.getQuery(starttime2, totime2, status + "", level1
						+ "", vo.getBusinessids(), host.getId());
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmievent.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxevent.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxevent.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxevent.jsp";
		} else if ((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				|| host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host
				.getCollecttype() == SystemConstant.COLLECTTYPE_SSH)
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixevent.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solarisevent.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpevent.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& (host.getSysOid().indexOf("scounix") >= 0 || host
						.getSysOid().indexOf("scoopenserver") >= 0)) {
			// scounixware������,��ת��scounixware�ɼ�չʾҳ��
			return "/detail/host_scounixevent.jsp";
		} else if (NodeUtil.isOnlyCollectPing(host)) {
			// ֻ�ɼ���ͨ�ʵ���Ϣʱ,����cpu���ڴ������
			return "/detail/host_event_onlyping.jsp";
		}
		return "/detail/host_event.jsp";

	}

	/**
	 * 
	 * @description �澯�´���
	 * @author wangxiangyong
	 * @date Aug 24, 2012 10:07:43 AM
	 * @return String
	 * @return
	 */
	private String hosteventlist() {
		int id = 0;
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		id = getParaIntValue("id");
		String nodetype = "";
		nodetype = getParaValue("nodetype");
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

			String[] time = { "", "" };
			getTime(request, time);
			String time1 = request.getParameter("begindate");
			if (time1 == null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				time1 = sdf.format(new Date());
			}

			String starttime2 = b_time + " 00:00:00";
			String totime2 = t_time + " 23:59:59";

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			EventListDao dao = new EventListDao();
			try {
				User user = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				if (nodetype != null) {
					if (nodetype.equals("net") || nodetype.equals("host")) {
						list = dao.getQuery(starttime2, totime2, status + "",
								level1 + "", user.getBusinessids(), id);
					} else if (nodetype.equals("db")) {
						list = dao.getQuery(starttime1, totime1, "db", status
								+ "", level1 + "", user.getBusinessids(), id);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				dao.close();
			}

			request.setAttribute("startdate", b_time);
			request.setAttribute("todate", t_time);
			request.setAttribute("list", list);
			request.setAttribute("nodetype", nodetype);
			request.setAttribute("id", id + "");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/detail/host_event_list.jsp";

	}

	private String hostutilhdx() {
		String tmp = "";
		tmp = request.getParameter("id");
		String flag = request.getParameter("flag");
		Host host = null;
		try {
			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("flag", flag);

		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmiutilhdx.jsp?id=" + tmp;
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxutilhdx.jsp?id=" + tmp;
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/topology/network/host_aixutilhdx.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/topology/network/host_aixutilhdx.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hputilhdx.jsp?id=" + tmp;
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42.2.1.1") >= 0)
			// SUN������,��ת��SUN�ɼ�չʾҳ��
			return "/detail/host_solarisutilhdx.jsp";
		else
			return "/detail/hostutilhdx.jsp?id=" + tmp;

	}

	private String netarp() {
		String ip = "";
		String tmp = "";
		try {
			tmp = request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		return "/detail/net_arp.jsp";
	}

	private String firewallarp() {
		Vector vector = new Vector();
		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {

			tmp = request.getParameter("id");

			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-MAC��ARP����Ϣ
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector) ipAllData.get("ipmac");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		return "/detail/firewall_arp.jsp";

	}

	private String firewallarpproxcy() {
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {

			tmp = request.getParameter("id");

			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-MAC��ARP����Ϣ
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector) ipAllData.get("arpproxy");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		// if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
		// return "/detail/firewalltos_arpproxcy.jsp";
		// }else
		return "/detail/firewall_arpproxcy.jsp";
	}

	private String firewallvlan() {
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {
			tmp = request.getParameter("id");
			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-MAC��ARP����Ϣ
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector) ipAllData.get("vlans");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		// if(host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.14331.1.4")){
		// return "/detail/firewalltos_vlan.jsp";
		// }else
		return "/detail/firewall_vlan.jsp";
	}

	private String firewalllogin() {
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {

			tmp = request.getParameter("id");

			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-MAC��ARP����Ϣ
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector) ipAllData.get("userlogin");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		return "/detail/firewall_login.jsp";
	}

	private String netfdb() {
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
		return "/detail/net_fdb.jsp";
	}

	private String f5poolinfo() {
		Vector vector = new Vector();
		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {

			tmp = request.getParameter("id");
			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-MAC��ARP����Ϣ
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector) ipAllData.get("vlans");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		if (host.getSysOid().startsWith("1.3.6.1.4.1.3375.2.1.3.4.")
				|| host.getSysOid().startsWith("1.3.6.1.4.1.7564")) {
			return "/detail/f5poolinfo.jsp";
		} else
			return "/detail/firewall_vlan.jsp";
	}

	private String f5rulesinfo() {
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Host host = null;
		try {

			tmp = request.getParameter("id");
			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-MAC��ARP����Ϣ
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector) ipAllData.get("userlogin");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		if (host.getSysOid().startsWith("1.3.6.1.4.1.3375.2.1.3.4.")
				|| host.getSysOid().startsWith("1.3.6.1.4.1.7564")) {
			return "/detail/f5rulesinfo.jsp";
		} else
			return "/detail/firewall_login.jsp";
	}

	private String netiplist() {
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
		return "/detail/net_iplist.jsp";
	}

	private String telnetCfg() {
		Vector vector = new Vector();
		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
		try {
			tmp = request.getParameter("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		ip = host.getIpAddress();

		String startdate = getParaValue("startdate");
		String todate = getParaValue("todate");
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		if (startdate == null) {
			starttime = time1 + " 00:00:00";
			startdate = time1;
		}
		if (todate == null) {
			totime = time1 + " 23:59:59";
			todate = time1;
		}
		CfgBaseInfoDao baseInfoDao = null;
		PolicyInterfaceDao interfaceDao = null;
		QueueInfoDao queueInfoDao = null;
		List policyInterfaceList = null;
		List queueList = null;

		List classList = null;
		List policyList = null;
		GatherTelnetConfigDao cfgDao = new GatherTelnetConfigDao();
		List<GatherTelnetConfig> cfgList = null;
		try {
			cfgList = cfgDao.loadAll();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			cfgDao.close();
		}
		Vector<String> ipVector = new Vector<String>();
		if (cfgList != null && cfgList.size() > 0) {

			for (int i = 0; i < cfgList.size(); i++) {
				GatherTelnetConfig config = cfgList.get(i);
				if (config != null) {
					String[] ips = config.getTelnetIps().split(",");
					if (ips != null) {
						for (int j = 0; j < ips.length; j++) {
							if (ips[j] != null && !ips[j].trim().equals("")) {
								ipVector.add(ips[j]);
							}
						}

					}
				}

			}

		}
		if (ipVector.contains(ip)) {
			try {
				String allipstr = SysUtil.doip(ip);
				baseInfoDao = new CfgBaseInfoDao(allipstr);
				interfaceDao = new PolicyInterfaceDao(allipstr);
				queueInfoDao = new QueueInfoDao(allipstr);
				classList = baseInfoDao.findByCondition(" where type='class'");
				baseInfoDao = new CfgBaseInfoDao(allipstr);
				policyList = baseInfoDao
						.findByCondition(" where type='policy' ");
				policyInterfaceList = interfaceDao
						.findByCondition(" where collecttime>='"
								+ starttime
								+ "' and collecttime<='"
								+ totime
								+ "' group by interfaceName,policyName,className,collecttime");
				queueList = queueInfoDao
						.findByCondition(" where collecttime>='" + starttime
								+ "' and collecttime<='" + totime + "'");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				baseInfoDao.close();
				interfaceDao.close();
				queueInfoDao.close();
			}
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("classList", classList);
		request.setAttribute("policyList", policyList);
		request.setAttribute("interfaceList", policyInterfaceList);
		request.setAttribute("queueList", queueList);

		request.setAttribute("vector", vector);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/net_telnetCfg.jsp";
	}

	private String telnetAcl() {
		Vector vector = new Vector();
		String ip = "";
		String tmp = "";
		double cpuvalue = 0;
		String pingconavg = "0";
		String time = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());
		try {

			tmp = request.getParameter("id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		ip = host.getIpAddress();

		String startdate = getParaValue("startdate");
		String todate = getParaValue("todate");
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		if (startdate == null) {
			starttime = time1 + " 00:00:00";
			startdate = time1;
		}
		if (todate == null) {
			totime = time1 + " 23:59:59";
			todate = time1;
		}
		AclDetailDao detailDao = null;
		List detailList = null;
		AclBaseDao baseDao = null;
		List<AclBase> baseList = null;
		try {
			baseDao = new AclBaseDao();
			baseList = baseDao.findByCondition(" where ipaddress='" + ip + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			baseDao.close();
		}
		StringBuffer ids = new StringBuffer();
		if (baseList != null && baseList.size() > 0) {
			for (int i = 0; i < baseList.size(); i++) {
				AclBase base = baseList.get(i);
				if (base != null) {
					ids.append(base.getId()).append(",");
				}
			}
		}
		String baseIds = "";
		if (ids.toString().length() > 1)
			baseIds = ids.toString().substring(0, ids.toString().length() - 1);
		try {
			detailDao = new AclDetailDao();
			if (!baseIds.equals(""))
				detailList = detailDao.findByCondition(" where collecttime>='"
						+ starttime + "' and collecttime<='" + totime
						+ "' and status=1 and baseId in(" + baseIds
						+ ") group by baseId,name,collecttime");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			detailDao.close();
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

		request.setAttribute("detailList", detailList);

		request.setAttribute("vector", vector);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", time);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/net_telnetAcl.jsp";
	}

	private String firewalliplist() {
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

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-MAC��ARP����Ϣ
			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {
					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
				vector = (Vector) ipAllData.get("ipmac");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		return "/detail/firewall_iplist.jsp";
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
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1
						+ "", vo.getBusinessids(), host.getId());
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

	private String firewallevent() {
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		Host host = null;
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
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1
						+ "", vo.getBusinessids(), host.getId());
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
		return "/detail/firewall_event.jsp";

	}

	private String firewallpolicy() {
		String ip = "";
		String tmp = "";
		int trustflag = 0;
		try {

			tmp = request.getParameter("id");
			trustflag = getParaIntValue("trustflag");
			request.setAttribute("trustflag", trustflag);
			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			Hashtable policys = ShareData.getPolicydata();
			Hashtable itValue = new Hashtable();

			if (policys != null && policys.size() > 0) {
				itValue = (Hashtable) policys.get(ip);
			}
			if (trustflag == 0) {
				request.setAttribute("trustList", (List) itValue
						.get("untotrust"));
			} else {
				request.setAttribute("trustList", (List) itValue
						.get("trusttoun"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("ipaddress", ip);
		request.setAttribute("id", tmp);

		return "/detail/firewall_policy.jsp";
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

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
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

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1
						+ "", vo.getBusinessids(), host.getId());
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

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
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

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1
						+ "", vo.getBusinessids(), host.getId());
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
			// ȡ�ñ�����ϸ��Ϣ
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

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";
			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1
						+ "", vo.getBusinessids(), host.getId());
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
			List hostlist = hostdao.loadNetwork(2);
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

			String title = "����24Сʱ�˿����ٹ鵵";
			String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
			String[] bandch3 = { "�������", "��������" };
			String reportname = title + "�ձ���";

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

			title = "�˿�����";

			// ɽ���ƶ�IDCȥ���±���2008-04-30
			if (perelement.equalsIgnoreCase("minutes")) {
				// ��������ʾ����
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index,
						banden3, bandch3, b_time, t_time, "UtilHdx");
				reportname = title + b_time + "��" + t_time + "����(��������ʾ)";
				p_drawchartMultiLineMonth(value, reportname, newip + category
						+ "_month", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + category
						+ "_month.png";
				imgurlhash.put("bandmonthspeed", url1);
			} else if (perelement.equalsIgnoreCase("hours")) {
				// ��Сʱ��ʾ����
				/* ɽ���ƶ�IDCȥ���걨��2008-04-30 */
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index,
						banden3, bandch3, b_time, t_time, "utilhdxhour");
				reportname = title + b_time + "��" + t_time + "����(��Сʱ�鵵��ʾ)";
				p_drawchartMultiLineYear(value, reportname, newip
						+ "ifspeed_year", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + "ifspeed"
						+ "_year.png";
				imgurlhash.put("bandmonthspeed", url1);
			} else {
				// ������ʾ����
				/* ɽ���ƶ�IDCȥ���걨��2008-04-30 */
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index,
						banden3, bandch3, b_time, t_time, "utilhdxday");
				reportname = title + b_time + "��" + t_time + "����(����鵵��ʾ)";
				p_drawchartMultiLineYear(value, reportname, newip
						+ "ifspeed_year", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + "ifspeed"
						+ "_year.png";
				imgurlhash.put("bandmonthspeed", url1);
			}

			imgurlhash.put("status", "resource/image/jfreechart/" + newip
					+ "IfStatus" + ".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private String showhostutilhdx() {
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
			List hostlist = hostdao.loadNetwork(2);
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
			String title = "����24Сʱ�˿����ٹ鵵";
			String[] banden3 = { "InBandwidthUtilHdx", "OutBandwidthUtilHdx" };
			String[] bandch3 = { "�������", "��������" };
			String reportname = title + "�ձ���";

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

			title = "�˿�����";

			// ɽ���ƶ�IDCȥ���±���2008-04-30
			if (perelement.equalsIgnoreCase("minutes")) {
				// ��������ʾ����
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index,
						banden3, bandch3, b_time, t_time, "UtilHdx");
				reportname = title + b_time + "��" + t_time + "����(��������ʾ)";
				p_drawchartMultiLineMonth(value, reportname, newip + category
						+ "_month", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + category
						+ "_month.png";
				imgurlhash.put("bandmonthspeed", url1);
			} else if (perelement.equalsIgnoreCase("hours")) {
				// ��Сʱ��ʾ����
				/* ɽ���ƶ�IDCȥ���걨��2008-04-30 */
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index,
						banden3, bandch3, b_time, t_time, "utilhdxhour");
				reportname = title + b_time + "��" + t_time + "����(��Сʱ�鵵��ʾ)";
				p_drawchartMultiLineYear(value, reportname, newip
						+ "ifspeed_year", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + "ifspeed"
						+ "_year.png";
				imgurlhash.put("bandmonthspeed", url1);
			} else {
				// ������ʾ����
				/* ɽ���ƶ�IDCȥ���걨��2008-04-30 */
				value = daymanager.getmultiHisHdx(ip, "ifspeed", index,
						banden3, bandch3, b_time, t_time, "utilhdxday");
				reportname = title + b_time + "��" + t_time + "����(����鵵��ʾ)";
				p_drawchartMultiLineYear(value, reportname, newip
						+ "ifspeed_year", 800, 200, "UtilHdx");
				String url1 = "resource/image/jfreechart/" + newip + "ifspeed"
						+ "_year.png";
				imgurlhash.put("bandmonthspeed", url1);
			}

			imgurlhash.put("status", "resource/image/jfreechart/" + newip
					+ "IfStatus" + ".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", hostnode.getAlias());
		request.setAttribute("ifname", ifname);

		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("begindate", time1);
		request.setAttribute("perelement", perelement);

		return "/detail/host_utilhdx.jsp";
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
			// ������
			String[] banden2 = { "InDiscardsPerc", "OutDiscardsPerc" };
			String[] bandch2 = { "��ڶ�����", "���ڶ�����" };
			Hashtable[] bandhashtable2 = hostmanager.getDiscardsPerc(ip, index,
					banden2, bandch2, starttime1, totime1);

			p_drawchartMultiLine(bandhashtable2[0], "������",
					newip + "ifdescperc", 800, 200, "DiscardsPerc");

			imgurlhash.put("ifdescperc", "resource/image/jfreechart/" + newip
					+ "ifdescperc" + ".png");
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
			// ���������ʣ�����ͼ
			String[] banden1 = { "InErrorsPerc", "OutErrorsPerc" };
			String[] bandch1 = { "��ڴ�����", "���ڴ�����" };
			Hashtable[] bandhashtable1 = hostmanager.getErrorsPerc(ip, index,
					banden1, bandch1, starttime1, totime1);
			p_drawchartMultiLine(bandhashtable1[0], "�˿ڴ�����", newip
					+ "errorperc", 800, 200, "ErrorsPerc");

			imgurlhash.put("errorperc", "resource/image/jfreechart/" + newip
					+ "errorperc" + ".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
		// request.setAttribute("ifip",ifip);
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
			String[] bandch2 = { "������ݰ�", "�������ݰ�" };
			Hashtable[] bandhashtable2 = hostmanager.getIfBand_Packs(ip, index,
					banden2, bandch2, starttime1, totime1);

			p_drawchartMultiLine(bandhashtable2[0], "�շ���Ϣ����",
					newip + "ifpacks", 800, 200, "Packs");
			imgurlhash.put("ifpacks", "resource/image/jfreechart/" + newip
					+ "ifpacks" + ".png");

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

	private String showinpacks() {
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
			String[] banden2 = { "ifInMulticastPkts", "ifInBroadcastPkts" };
			String[] bandch2 = { "��ڶಥ���ݰ�", "��ڹ㲥���ݰ�" };
			Hashtable[] bandhashtable2 = hostmanager.getIfBand_InPacks(ip,
					index, banden2, bandch2, starttime1, totime1);
			p_drawchartMultiLine(bandhashtable2[0], "������ݰ�", newip + "ifpacks",
					800, 200, "Packs");
			imgurlhash.put("ifpacks", "resource/image/jfreechart/" + newip
					+ "ifpacks" + ".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
		// request.setAttribute("ifip",ifip);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", hostnode.getAlias());
		request.setAttribute("ifname", ifname);
		request.setAttribute("begindate", time1);
		return "/detail/net_ifinpacks.jsp";
	}

	private String showoutpacks() {
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
			String[] banden2 = { "ifOutMulticastPkts", "ifOutBroadcastPkts" };
			String[] bandch2 = { "���ڶಥ���ݰ�", "���ڹ㲥���ݰ�" };
			Hashtable[] bandhashtable2 = hostmanager.getIfBand_OutPacks(ip,
					index, banden2, bandch2, starttime1, totime1);
			p_drawchartMultiLine(bandhashtable2[0], "�������ݰ�", newip + "ifpacks",
					800, 200, "Packs");
			imgurlhash.put("ifpacks", "resource/image/jfreechart/" + newip
					+ "ifpacks" + ".png");

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", hostnode.getAlias());
		request.setAttribute("ifname", ifname);
		request.setAttribute("begindate", time1);
		return "/detail/net_ifoutpacks.jsp";
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

			String[] netIfdetail = { "index", "ifDescr", "ifname", "ifType",
					"ifMtu", "ifSpeed", "ifPhysAddress", "ifOperStatus" };
			hash = hostlastmanager.getIfdetail_share(ip, index, netIfdetail,
					starttime, endtime);
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

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-ROUTE��ARP����Ϣ
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			vector = (Vector) _IpRouterHash.get(ip);

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
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
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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

	private String firewallroute() {
		Vector vector = new Vector();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String ip = "";
		String tmp = "";
		Host host = null;
		try {

			tmp = request.getParameter("id");

			host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();

			// ���ڴ��л�õ�ǰ�ĸ���IP��ص�IP-ROUTE��ARP����Ϣ
			Hashtable _IpRouterHash = ShareData.getIprouterdata();
			vector = (Vector) _IpRouterHash.get(ip);

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
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
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		return "/detail/firewall_route.jsp";

	}

	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp
					.get(Calendar.HOUR_OF_DAY),
					temp.get(Calendar.DAY_OF_MONTH),
					temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(ʱ��)", "y", title1, title2, w, h);
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

		// �����ڴ�
		Host host = (Host) PollingEngine.getInstance().getNodeByID(vo.getId());
		host.setAlias(vo.getAlias());
		host.setManaged(vo.isManaged());

		// �������ݿ�
		DaoInterface dao = new HostNodeDao();
		setTarget("/network.do?action=list");
		return update(dao, vo);
	}

	private String refreshsysname() {
		HostNodeDao dao = new HostNodeDao();
		String sysName = "";
		sysName = dao.refreshSysName(getParaIntValue("id"));

		// �����ڴ�
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				getParaIntValue("id"));
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

	private String neteventdelete() {

		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			// ����ɾ��
			EventListDao edao = new EventListDao();
			edao.delete(ids);
			edao.close();

		}

		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		EventListDao dao = new EventListDao();
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
		String starttime1 = b_time + " 00:00:00";
		String totime1 = t_time + " 23:59:59";
		String sql = "";
		try {
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // �û�����
			StringBuffer s = new StringBuffer();
			if (SystemConstant.DBType.equalsIgnoreCase("mysql")) {
				s.append("where recordtime>= '" + starttime1 + "' "
						+ "and recordtime<='" + totime1 + "'");
			} else if (SystemConstant.DBType.equalsIgnoreCase("oracle")) {
				s.append("where recordtime>=to_date( '" + starttime1
						+ "','yyyy-mm-dd hh24:mi:ss') "
						+ "and recordtime<=to_date('" + totime1
						+ "','yyyy-mm-dd hh24:mi:ss')");
			}
			if (!"99".equals(level1 + "")) {
				s.append(" and level1=" + level1);
			}
			if (!"99".equals(status + "")) {
				s.append(" and managesign=" + status);
			}
			String businessid = vo.getBusinessids();
			int flag = 0;
			if (businessid != null) {
				if (businessid != "-1") {
					String[] bids = businessid.split(",");
					if (bids.length > 0) {
						for (int i = 0; i < bids.length; i++) {
							if (bids[i].trim().length() > 0) {
								if (flag == 0) {
									s.append(" and ( businessid = ',"
											+ bids[i].trim() + ",' ");
									flag = 1;
								} else {
									s.append(" or businessid = ',"
											+ bids[i].trim() + ",' ");
								}
							}
						}
						s.append(") ");
					}

				}
			}
			sql = s.toString();
			sql = sql + " order by id desc";
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);

		setTarget("/monitor.do?action=netevent");
		return list(dao, sql);
	}

	private String add() {
		String ipAddress = getParaValue("ip_address");
		String alias = getParaValue("alias");
		String community = getParaValue("community");
		String writecommunity = getParaValue("writecommunity");
		int type = getParaIntValue("type");

		TopoHelper helper = new TopoHelper(); // �����������ݿ�͸����ڴ�
		int addResult = helper.addHost(ipAddress, alias, community,
				writecommunity, type); // ����һ̨������
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

		// 2.����xml
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
		xmlString = xmlString.replace("<?xml version=\"1.0\"?>",
				"<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		XmlOperator xmlOpr = new XmlOperator();
		if (vlanString != null && vlanString.equals("1")) {
			xmlOpr.setFile("networkvlan.jsp");
		} else
			xmlOpr.setFile("network.jsp");
		xmlOpr.saveImage(xmlString);

		return "/topology/network/save.jsp";
	}

	private String look_prohis() {

		String ip = request.getParameter("ipaddress");
		String pid = request.getParameter("pid");
		String pname = request.getParameter("pname");

		Hashtable imgurlhash = new Hashtable();
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("ip", ip);// yangjun add
		request.setAttribute("pid", pid);// yangjun add
		request.setAttribute("pname", pname);// yangjun add
		return "/detail/look_prohis.jsp";
	}

	private String addHostprocMonflag() {
		Procs vo = createProcess();
		ProcsDao dao = new ProcsDao();
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return hostproc();
	}

	private Procs createProcess() {

		Procs vo = new Procs();
		Calendar cal = new GregorianCalendar();

		String flag = getParaValue("flag");
		if (flag == null) {
			flag = "1";
		}
		vo.setFlag(Integer.valueOf(flag));

		String wbstatus = getParaValue("wbstatus");

		if (wbstatus == null) {
			wbstatus = flag;
		}

		String id = getParaValue("id");
		HostNode hostNode = null;
		HostNodeDao hostNodeDao = new HostNodeDao();
		try {
			hostNode = (HostNode) hostNodeDao.findByID(id);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			hostNodeDao.close();
		}

		vo.setNodeid(hostNode.getId());

		vo.setWbstatus(Integer.valueOf(wbstatus));

		vo.setIpaddress(hostNode.getIpAddress());

		vo.setProcname(getParaValue("procsname"));
		vo.setBak(getParaValue("procsbak"));
		vo.setCollecttime(cal);

		return vo;
	}

	private String changeHostprocWbstatus() {
		Procs vo = new Procs();
		ProcsDao dao = null;
		try {
			String id = getParaValue("procid");
			int wbstatus = getParaIntValue("wbstatus");
			dao = new ProcsDao();
			vo = (Procs) dao.findByID(id);
			vo.setWbstatus(wbstatus);
			dao = new ProcsDao();
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return hostproc();
	}

	private String changeHostprocMonflag() {
		Procs vo = new Procs();
		ProcsDao dao = null;
		try {
			String id = getParaValue("procid");
			int monflag = getParaIntValue("monflag");
			dao = new ProcsDao();
			vo = (Procs) dao.findByID(id);
			vo.setFlag(monflag);
			vo.setWbstatus(monflag);
			dao = new ProcsDao();
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return hostproc();
	}

	private String downloadsoftwarereport() {
		Vector softwareV = (Vector) session.getAttribute("softwareV");

		Hashtable reporthash = new Hashtable();
		if (softwareV != null) {
			reporthash.put("softwareV", softwareV);
		} else {
			softwareV = new Vector();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);

		report.createReport_softwarelist("/temp/softwarelist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
	}

	private String downloaddevicereport() {
		Vector deviceV = (Vector) session.getAttribute("deviceV");

		Hashtable reporthash = new Hashtable();
		if (deviceV != null) {
			reporthash.put("deviceV", deviceV);
		} else {
			deviceV = new Vector();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);

		report.createReport_devicelist("/temp/devicelist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
	}

	private String downloadstoragereport() {
		Vector storageV = (Vector) session.getAttribute("storageV");

		Hashtable reporthash = new Hashtable();
		if (storageV != null) {
			reporthash.put("storageV", storageV);
		} else {
			storageV = new Vector();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);

		report.createReport_storagelist("/temp/storagelist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
	}

	private String downloadipmacreport() {
		Vector ipmacV = (Vector) session.getAttribute("ipmacV");

		Hashtable reporthash = new Hashtable();
		if (ipmacV != null) {
			reporthash.put("list", ipmacV);
		} else {
			ipmacV = new Vector();
		}
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		report.createReport_ipmacall("/temp/ipmaclist_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/topology/network/downloadreport.jsp";
	}

	public String execute(String action) {
		if (action.equals("createImage"))
			return createImage();
		if (action.equals("speed")) {
			return speed();
		}
		if (action.equals("hostwindows")) {
			return hostwindows();
		}
		if (action.equals("createWord"))
			return createWord();
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
		if (action.equals("cpudetail"))
			return cpudetail();
		if (action.equals("memorydetail"))
			return memorydetail();
		if (action.equals("interfacedetail"))
			return interfacedetail();
		if (action.equals("portdetail"))
			return portdetail();
		if (action.equals("bandwidthdetail"))
			return bandwidthdetail();
		if (action.equals("netfdb"))
			return netfdb();
		if (action.equals("netenv"))
			return netenv();
		if (action.equals("hostcpu"))
			return hostcpu();
		if (action.equals("hostconfig"))
			return hostconfig();
		if (action.equals("hostutilhdx"))
			return hostutilhdx();
		if (action.equals("hostproc"))
			return hostproc();
		if (action.equals("hostservice"))
			return hostservice();
		if (action.equals("hostsyslog"))
			return hostsyslog();
		if (action.equals("hosterrpt"))
			return hosterrpt();
		if (action.equals("hosterrptDetail"))
			return hosterrptDetail();
		if (action.equals("hostarp"))
			return hostarp();
		if (action.equals("hostdevice"))
			return hostdevice();
		if (action.equals("hoststorage"))
			return hoststorage();
		if (action.equals("hostsw"))
			return hostsw();
		if (action.equals("downloadsoftwarereport"))
			return downloadsoftwarereport();
		if (action.equals("downloadipmacreport"))
			return downloadipmacreport();
		if (action.equals("downloaddevicereport"))
			return downloaddevicereport();
		if (action.equals("downloadstoragereport"))
			return downloadstoragereport();
		if (action.equals("hostwinservice"))
			return hostwinservice();
		if (action.equals("refresh"))
			return refresh();
		if (action.equals("refreshhostarp"))
			return refreshhostarp();
		if (action.equals("refreshhostdevice"))
			return refreshhostdevice();
		if (action.equals("refreshhoststorage"))
			return refreshhoststorage();
		if (action.equals("insertdb"))
			return insertdb();
		if (action.equals("insertarpdb"))
			return insertarpdb();
		if (action.equals("hostsyslogdetail"))
			return hostsyslogdetail();
		if (action.equals("netarp"))
			return netarp();
		if (action.equals("netevent"))
			return netevent();
		if (action.equals("hostevent"))
			return hostevent();
		if (action.equals("hosteventlist"))
			return hosteventlist();
		if (action.equals("accit"))
			return accit();
		if (action.equals("accfi"))
			return accfi();
		if (action.equals("fireport"))
			return fireport();
		if (action.equals("doreport"))
			return doreport();
		if (action.equals("gatewayqueue"))
			return gatewayqueue();
		if (action.equals("gatewayraid"))
			return gatewayraid();
		if (action.equals("gatewayenv"))
			return gatewayenv();
		if (action.equals("gatewayevent"))
			return gatewayevent();
		if (action.equals("nokiaimage"))
			return nokiaimage();
		if (action.equals("nokiaprocess"))
			return nokiaprocess();
		if (action.equals("nokiamirror"))
			return nokiamirror();
		if (action.equals("nokiaenv"))
			return nokiaenv();
		if (action.equals("nokiaevent"))
			return nokiaevent();
		if (action.equals("viewreport"))
			return viewreport();
		if (action.equals("show_hostutilhdx"))
			return showhostutilhdx();
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
		if (action.equals("show_inpacks"))
			return showinpacks();
		if (action.equals("show_outpacks"))
			return showoutpacks();
		if (action.equals("netroute"))
			return netroute();
		if (action.equals("netiplist"))
			return netiplist();
		if (action.equals("netping"))
			return netping();
		if (action.equals("hostping"))
			return hostping();
		if (action.equals("hostroute"))
			return hostroute();
		if (action.equals("ready_edit"))
			return readyEdit();
		if (action.equals("update"))
			return update();
		if (action.equals("refreshsysname"))
			return refreshsysname();
		if (action.equals("delete"))
			return delete();
		if (action.equals("neteventdelete"))
			return neteventdelete();
		if (action.equals("find"))
			return find();
		if (action.equals("ready_add"))
			return "/topology/network/add.jsp";
		if (action.equals("add"))
			return add();
		if (action.equals("save"))
			return save();
		if (action.equals("netresponsetime_report")) {
			return netresponsetime_report();
		}
		if (action.equals("hostresponsetime_report")) {
			return hostResponseTime_report();
		}
		if (action.equals("look_prohis"))
			return look_prohis();
		if (action.equals("netcpu_report")) {
			return netcpu_report();
		}
		if (action.equals("netping_report")) {
			return netping_report();
		}
		if (action.equals("hostping_report")) {
			return hostping_report();
		}
		if (action.equals("hostcpu_report")) {
			return hostcpu_report();
		}
		if (action.equals("hostmemory_report")) {
			return hostmemory_report();
		}
		if (action.equals("firewallpolicy")) {
			return firewallpolicy();
		}
		if (action.equals("firewallcpu")) {
			return firewallcpu();
		}
		if (action.equals("firewallarp")) {
			return firewallarp();
		}
		if (action.equals("firewallarpproxcy")) {
			return firewallarpproxcy();
		}
		if (action.equals("firewallvlan")) {
			return firewallvlan();
		}
		if (action.equals("firewallroute")) {
			return firewallroute();
		}
		if (action.equals("firewallping")) {
			return firewallping();
		}
		if (action.equals("firewalliplist")) {
			return firewalliplist();
		}
		if (action.equals("firewalllogin")) {
			return firewalllogin();
		}
		if (action.equals("firewallevent")) {
			return firewallevent();
		}
		if (action.equals("f5poolinfo")) {
			return f5poolinfo();
		}
		if (action.equals("f5rulesinfo")) {
			return f5rulesinfo();
		}
		if (action.equals("changeHostprocMonflag")) {
			return changeHostprocMonflag();
		}
		if (action.equals("changeHostprocWbstatus")) {
			return changeHostprocWbstatus();
		}
		if (action.equals("addHostprocMonflag")) {
			return addHostprocMonflag();
		}
		if (action.equals("AS400PoolDetail")) {
			return AS400PoolDetail();

		}

		if (action.equals("AS400SystemStatusDetail")) {
			return AS400SystemStatusDetail();

		}

		if (action.equals("AS400DiskDetail")) {
			return AS400DiskDetail();

		}

		if (action.equals("AS400JobsDetail")) {
			return AS400JobsDetail();
		}

		if (action.equals("AS400NetworkDetail")) {
			return AS400NetworkDetail();
		}

		if (action.equals("AS400HardwareDetail")) {
			return AS400HardwareDetail();
		}

		if (action.equals("AS400ServiceDetail")) {
			return AS400ServiceDetail();
		}

		if (action.equals("AS400EventDetail")) {
			return AS400EventDetail();
		}

		if (action.equals("AS400SubsystemDetail")) {
			return AS400SubstystemDetail();

		}

		if (action.equals("AS400JobsInSubsystemDetail")) {
			return AS400JobsInSubsystemDetail();

		}

		if (action.equals("networkview")) {
			return networkview();

		}
		if (action.equals("telnetCfg")) {
			return telnetCfg();

		}
		if (action.equals("telnetAcl")) {
			return telnetAcl();

		}
		if (action.equals("vpncpu")) {
			return vpncpu();
		}
		if (action.equals("vpnarp")) {
			String ip = "";
			String tmp = "";
			try {
				tmp = request.getParameter("id");
				Host host = (Host) PollingEngine.getInstance().getNodeByID(
						Integer.parseInt(tmp));
				ip = host.getIpAddress();
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("ipaddress", ip);
			request.setAttribute("id", tmp);
			return vpnarp();
		}
		if (action.equals("vpnevent")) {
			return vpnevent();
		}
		if (action.equals("vpnconfig")) {
			String ip = "";
			String tmp = "";
			try {
				tmp = request.getParameter("id");
				Host host = (Host) PollingEngine.getInstance().getNodeByID(
						Integer.parseInt(tmp));
				ip = host.getIpAddress();

			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("ipaddress", ip);
			request.setAttribute("id", tmp);
			return vpnconfig();
		}
		if (action.equals("vpnsession")) {
			String ip = "";
			String tmp = "";
			try {
				tmp = request.getParameter("id");
				Host host = (Host) PollingEngine.getInstance().getNodeByID(
						Integer.parseInt(tmp));
				ip = host.getIpAddress();
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("ipaddress", ip);
			request.setAttribute("id", tmp);
			return vpnsession();
		}
		if (action.equals("vpnconn")) {
			String ip = "";
			String tmp = "";
			try {

				tmp = request.getParameter("id");

				Host host = (Host) PollingEngine.getInstance().getNodeByID(
						Integer.parseInt(tmp));
				ip = host.getIpAddress();

			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("ipaddress", ip);
			request.setAttribute("id", tmp);
			return vpnconn();
		}
		if (action.equals("vpnweb")) {
			String ip = "";
			String tmp = "";
			try {
				tmp = request.getParameter("id");
				Host host = (Host) PollingEngine.getInstance().getNodeByID(
						Integer.parseInt(tmp));
				ip = host.getIpAddress();
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.setAttribute("ipaddress", ip);
			request.setAttribute("id", tmp);
			return vpnweb();
		}
		if (action.equals("datapacket"))// add by jiruifei 2011-3-15 16:16
			return datapacket();
		if (action.equals("multicastpacket"))// add by jiruifei 2011-3-15
			return multicastpacket();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String vpnconn() {
		return "/detail/vpn_conn.jsp";
	}

	private String vpnsession() {
		return "/detail/vpn_session.jsp";
	}

	private String vpnconfig() {
		return "/detail/vpn_config.jsp";
	}

	private String vpnweb() {
		return "/detail/vpn_web.jsp";
	}

	private String vpncpu() {
		return "/detail/vpn_cpu.jsp";
	}

	private String vpnarp() {
		return "/detail/vpn_arp.jsp";
	}

	private String gatewayenv() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		double cpuvalue = 0.0D;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = this.request.getParameter("id");

		this.request.setAttribute("imgurl", imgurlhash);
		this.request.setAttribute("hash", hash);
		this.request.setAttribute("max", maxhash);
		this.request.setAttribute("id", tmp);
		this.request.setAttribute("cpuvalue", Double.valueOf(cpuvalue));
		this.request.setAttribute("collecttime", collecttime);
		this.request.setAttribute("sysuptime", sysuptime);
		this.request.setAttribute("sysservices", sysservices);
		this.request.setAttribute("sysdescr", sysdescr);
		this.request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/gateway_env.jsp";
	}

	private String gatewayraid() {
		String ip = "";
		String tmp = "";
		try {
			tmp = this.request.getParameter("id");

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request.setAttribute("ipaddress", ip);
		this.request.setAttribute("id", tmp);
		return "/detail/gateway_raid.jsp";
	}

	private String nokiaevent() {
		Vector vector = new Vector();
		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		try {
			SimpleDateFormat sdf;
			tmp = this.request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			this.request.setAttribute("status", Integer.valueOf(status));
			this.request.setAttribute("level1", Integer.valueOf(level1));

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
			try {
				User vo = (User) this.session.getAttribute("current_user");

				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1
						+ "", vo.getBusinessids(), Integer
						.valueOf(host.getId()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request.setAttribute("vector", vector);
		this.request.setAttribute("ipaddress", ip);
		this.request.setAttribute("id", tmp);
		this.request.setAttribute("list", list);
		this.request.setAttribute("startdate", b_time);
		this.request.setAttribute("todate", t_time);
		return "/detail/nokia_event.jsp";
	}

	private String nokiaenv() {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable maxhash = new Hashtable();

		double cpuvalue = 0.0D;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = this.request.getParameter("id");

		this.request.setAttribute("imgurl", imgurlhash);
		this.request.setAttribute("hash", hash);
		this.request.setAttribute("max", maxhash);
		this.request.setAttribute("id", tmp);
		this.request.setAttribute("cpuvalue", Double.valueOf(cpuvalue));
		this.request.setAttribute("collecttime", collecttime);
		this.request.setAttribute("sysuptime", sysuptime);
		this.request.setAttribute("sysservices", sysservices);
		this.request.setAttribute("sysdescr", sysdescr);
		this.request.setAttribute("pingconavg", new Double(pingconavg));
		return "/detail/nokia_env.jsp";
	}

	private String nokiamirror() {
		String ip = "";
		String tmp = "";
		try {
			tmp = this.request.getParameter("id");

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request.setAttribute("ipaddress", ip);
		this.request.setAttribute("id", tmp);
		return "/detail/nokia_mirror.jsp";
	}

	private String nokiaprocess() {
		String ip = "";
		String tmp = "";
		try {
			tmp = this.request.getParameter("id");

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request.setAttribute("ipaddress", ip);
		this.request.setAttribute("id", tmp);
		return "/detail/nokia_process.jsp";
	}

	private String nokiaimage() {
		String ip = "";
		String tmp = "";
		try {
			tmp = this.request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request.setAttribute("ipaddress", ip);
		this.request.setAttribute("id", tmp);
		return "/detail/nokia_image.jsp";
	}

	private String gatewayevent() {
		Vector vector = new Vector();

		String ip = "";
		String tmp = "";
		List list = new ArrayList();
		int status = 99;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		try {
			SimpleDateFormat sdf;
			tmp = this.request.getParameter("id");
			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			this.request.setAttribute("status", Integer.valueOf(status));
			this.request.setAttribute("level1", Integer.valueOf(level1));

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			if (b_time == null) {
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				b_time = sdf.format(new Date());
			}
			if (t_time == null) {
				sdf = new SimpleDateFormat("yyyy-MM-dd");
				t_time = sdf.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
			try {
				User vo = (User) this.session.getAttribute("current_user");
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1
						+ "", vo.getBusinessids(), Integer
						.valueOf(host.getId()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request.setAttribute("vector", vector);
		this.request.setAttribute("ipaddress", ip);
		this.request.setAttribute("id", tmp);
		this.request.setAttribute("list", list);
		this.request.setAttribute("startdate", b_time);
		this.request.setAttribute("todate", t_time);
		return "/detail/gateway_event.jsp";
	}

	private String gatewayqueue() {
		String ip = "";
		String tmp = "";
		try {
			tmp = this.request.getParameter("id");
			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.request.setAttribute("ipaddress", ip);
		this.request.setAttribute("id", tmp);
		return "/detail/gateway_queue.jsp";
	}

	private String vpnevent() {
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
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));
			ip = host.getIpAddress();
			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime1, totime1, status + "", level1
						+ "", vo.getBusinessids(), host.getId());
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
		return "/detail/vpn_event.jsp";
	}

	private String networkview() {
		netcpuUtil("minute");
		String flag = request.getParameter("flag");
		String tmp1 = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp1));
		if (host.getSysOid().startsWith("1.3.6.1.4.1.1588.2.1.1")) {// ���ƽ�����
			return "/topology/network/networkview_brocade.jsp?flag=" + flag;
		}
		return "/topology/network/networkview.jsp?flag=" + flag;
	}

	/**
	 * @author zhubinhua
	 * @return ��createImage()�������ɵ�ͼƬ��������word�ĵ���Ȼ���ṩ���ͻ�������
	 */
	private String createWord() {
		// ȷ��·��
		String tempDir = "";
		if (request.getParameter("whattype").equals("net")) {
			tempDir = "/FlexImage/net/";
		} else {
			tempDir = "/FlexImage/host/";
		}
		File dirFile = new File(request.getSession().getServletContext()
				.getRealPath("")
				+ tempDir);

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.valueOf(getParaValue("id")));

		try {
			File file = new File(dirFile, "Flex.doc");
			// ����ֽ�Ŵ�С
			Document document = new Document(PageSize.A4);
			// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
			RtfWriter2.getInstance(document, new FileOutputStream(file));
			document.open();
			// ������������
			BaseFont bfChinese = BaseFont.createFont("Times-Roman", "",
					BaseFont.NOT_EMBEDDED);
			// ����������
			com.lowagie.text.Font titleFont = new com.lowagie.text.Font(
					bfChinese, 24, com.lowagie.text.Font.BOLD);
			// ����������
			com.lowagie.text.Font contextFont = new com.lowagie.text.Font(
					bfChinese, 12, com.lowagie.text.Font.BOLD);
			com.lowagie.text.Font contextFont1 = new com.lowagie.text.Font(
					bfChinese, 12, com.lowagie.text.Font.NORMAL);
			Paragraph title = new Paragraph(host.getAlias() + "���ܱ���", titleFont);

			// ���ñ����ʽ���뷽ʽ
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			// ���ڲ�������ʱ��
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String toTime = sdf.format(date);
			String startTime = sdf1.format(date) + " 00:00:00";
			String reportTime = startTime + " �� " + toTime;

			// �豸�������Ϣ
			Chunk chunkName = new Chunk("�豸���ƣ�", contextFont);
			Chunk chunkIp = new Chunk("�豸IP��", contextFont);
			Chunk chunkTime = new Chunk("����ʱ�䣺", contextFont);
			Chunk chunkName1 = new Chunk(host.getSysName(), contextFont1);
			Chunk chunkIp1 = new Chunk(host.getIpAddress(), contextFont1);
			Chunk chunkTime1 = new Chunk(reportTime, contextFont1);

			Paragraph name = new Paragraph();
			Paragraph ip = new Paragraph();
			Paragraph time = new Paragraph();
			Paragraph nullLine = new Paragraph();

			name.add(chunkName);
			name.add(chunkName1);
			ip.add(chunkIp);
			ip.add(chunkIp1);
			time.add(chunkTime);
			time.add(chunkTime1);

			// ��ӵ��ĵ���ȥ
			document.add(name);
			document.add(ip);
			document.add(time);
			document.add(nullLine);

			String[] imgFileNames = dirFile.list(new FilenameFilter() {
				// ���˵������ļ�
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".jpg");
				}

			});

			// ��ͼƬд��word�ĵ�
			Image imgs[] = new Image[imgFileNames.length];
			for (int i = 0; i < imgFileNames.length; i++) {
				imgs[i] = Image.getInstance(dirFile.getCanonicalPath() + "/"
						+ imgFileNames[i]);
				document.add(imgs[i]);
			}

			document.close();

			// ת������ҳ��
			System.out.println(file.getCanonicalPath());
			request.setAttribute("filename", file.getCanonicalPath());
			return "/topology/network/downloadreport.jsp";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @author zhubinhua
	 * @return �ڷ�����������һ��FlexͼƬ
	 */
	private String createImage() {

		// ȷ��·��
		String tempDir = "";
		if (request.getParameter("whattype").equals("net")) {
			tempDir = "/FlexImage/net/";
		} else {
			tempDir = "/FlexImage/host/";
		}
		String serverFileName = request.getSession().getServletContext()
				.getRealPath("")
				+ tempDir + request.getParameter("name") + ".jpg";

		// ��ʼ����jpeg�ļ�
		try {
			InputStream is = request.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedImage bufferedImage = ImageIO.read(bis);
			if (bufferedImage != null) {
				ImageIO.write(bufferedImage, "jpeg", new File(serverFileName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String AS400PoolDetail() {
		hostpingUtil("minute");

		String id = getParaValue("id");

		List list = null;
		SystemPoolForAS400Dao systemPoolForAS400Dao = new SystemPoolForAS400Dao();
		try {
			list = systemPoolForAS400Dao.findByNodeid(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			systemPoolForAS400Dao.close();

		}

		request.setAttribute("list", list);
		return "/detail/host_telnet_AS400pool.jsp";
	}

	private String AS400SystemStatusDetail() {
		hostpingUtil("minute");

		String id = getParaValue("id");

		List list = null;
		SystemValueForAS400Dao systemValueForAS400Dao = new SystemValueForAS400Dao();
		try {
			list = systemValueForAS400Dao.findByNodeid(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			systemValueForAS400Dao.close();

		}
		Hashtable systemStatushashtable = new Hashtable();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				SystemValueForAS400 systemValueForAS400 = (SystemValueForAS400) list
						.get(i);
				systemStatushashtable.put(systemValueForAS400.getCategory(),
						systemValueForAS400.getValue());
			}
		}
		request.setAttribute("systemStatushashtable", systemStatushashtable);
		return "/detail/host_telnet_AS400SystemStatus.jsp";
	}

	private String AS400DiskDetail() {
		hostpingUtil("minute");

		String id = getParaValue("id");
		List list = null;
		DiskForAS400Dao diskForAS400Dao = new DiskForAS400Dao();
		try {
			list = diskForAS400Dao.findByNodeid(id);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			diskForAS400Dao.close();
		}
		request.setAttribute("list", list);

		return "/detail/host_telnet_AS400Disk.jsp";
	}

	private String AS400ServiceDetail() {
		hostpingUtil("minute");

		@SuppressWarnings("unused")
		String id = getParaValue("id");
		List list = null;
		request.setAttribute("list", list);

		return "/detail/host_telnet_AS400Service.jsp";
	}

	private String AS400HardwareDetail() {
		hostpingUtil("minute");

		@SuppressWarnings("unused")
		String id = getParaValue("id");
		List list = null;
		request.setAttribute("list", list);

		return "/detail/host_telnet_AS400Hardware.jsp";
	}

	private String AS400NetworkDetail() {
		hostpingUtil("minute");

		String id = getParaValue("id");
		List list = null;
		DiskForAS400Dao diskForAS400Dao = new DiskForAS400Dao();
		try {
			list = diskForAS400Dao.findByNodeid(id);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			diskForAS400Dao.close();
		}
		request.setAttribute("list", list);

		return "/detail/host_telnet_AS400Network.jsp";
	}

	private String AS400JobsDetail() {

		String id = getParaValue("id");

		List jobList = getAS400JobList(id);

		request.setAttribute("jobList", jobList);

		return "/detail/host_telnet_AS400Jobs.jsp";
	}

	private List getAS400JobList(String nodeid) {
		List list = null;
		JobForAS400Dao jobForAS400Dao = new JobForAS400Dao();
		try {
			list = jobForAS400Dao.findByCondition(" where nodeid='" + nodeid
					+ "'" + getAS400JobListSQL());
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			jobForAS400Dao.close();
		}
		return list;
	}

	private String getAS400JobListSQL() {

		String name = getParaValue("jobName");

		String jobSubsystem = (String) request.getAttribute("jobSubsystem");

		String user = getParaValue("jobUser");

		String type = getParaValue("jobType");

		String subtype = getParaValue("jobSubtype");

		String activestatus = getParaValue("jobActivestatus");

		String sortField = getParaValue("jobSortField");

		String sortType = getParaValue("jobSortType");

		String where = "";

		if (name != null && name.trim().length() > 0) {
			where = where + " and name like '%" + name + "%'";
		}

		if (user != null && user.trim().length() > 0) {
			where = where + " and user like '%" + user + "%'";
		}

		if (jobSubsystem != null && jobSubsystem.trim().length() > 0) {
			where = where + " and subsystem='" + jobSubsystem + "'";
		}

		if (type == null || "-1".equals(type)) {
			type = "-1";
		} else {
			where = where + " and type='" + type + "'";
		}

		if (subtype == null || "-1".equals(subtype)) {
			subtype = "-1";
		} else {
			where = where + " and subtype='" + subtype + "'";
		}

		if (activestatus == null || "-1".equals(activestatus)) {
			activestatus = "-1";
		} else {
			where = where + " and active_status='" + activestatus + "'";
		}

		if (sortField == null || sortField.trim().length() == 0) {
			sortField = "name";
		}

		if (sortType == null || sortType.trim().length() == 0) {
			sortType = "asc";
		}
		String sortField_sql = sortField;
		if ("cpu_used_time".equals(sortField)) {
			sortField_sql = "CONVERT(" + sortField + ", SIGNED)";
		}

		where = where + " order by " + sortField_sql + " " + sortType;

		request.setAttribute("jobName", name);
		request.setAttribute("jobUser", user);
		request.setAttribute("jobType", type);
		request.setAttribute("jobSubtype", subtype);
		request.setAttribute("jobActivestatus", activestatus);
		request.setAttribute("jobSortField", sortField);
		request.setAttribute("jobSortType", sortType);
		request.setAttribute("jobSubsystem", jobSubsystem);

		return where;
	}

	private String AS400SubstystemDetail() {
		String id = getParaValue("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.valueOf(id));
		Hashtable hashtable = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		Hashtable jobHashtable = new Hashtable();
		if (hashtable != null) {
			List list = (List) hashtable.get("subSystem");
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					SubsystemForAS400 subsystemForAS400 = (SubsystemForAS400) list
							.get(i);
					JobForAS400Dao jobForAS400Dao = new JobForAS400Dao();
					List joblist = null;
					try {
						joblist = jobForAS400Dao.findByNodeidAndPath(id,
								subsystemForAS400.getPath());
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
					if (joblist == null) {
						joblist = new ArrayList();
					}
					jobHashtable.put(subsystemForAS400, joblist);
				}
			}
			request.setAttribute("list", list);
		}
		request.setAttribute("jobHashtable", jobHashtable);
		return "/detail/host_telnet_AS400Subsystem.jsp";
	}

	private String AS400JobsInSubsystemDetail() {
		String id = getParaValue("id");

		request.setAttribute("jobSubsystem", getParaValue("jobSubsystem"));

		List jobList = getAS400JobList(id);

		request.setAttribute("jobList", jobList);

		return "/detail/host_telnet_AS400JobsInSubsystem.jsp";
	}

	private String AS400EventDetail() {
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
		tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

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
			try {
				User vo = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				EventListDao dao = new EventListDao();
				list = dao.getQuery(starttime2, totime2, status + "", level1
						+ "", vo.getBusinessids(), host.getId());

			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}
			// ��order��������Ϣ����
			String order = "MemoryUtilization";
			if ((request.getParameter("orderflag") != null)
					&& (!request.getParameter("orderflag").equals(""))) {
				order = request.getParameter("orderflag");
			}
			try {
				processhash = hostlastmanager.getProcess_share(host
						.getIpAddress(), "Process", order, starttime, endtime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
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
		return "/detail/host_telnet_AS400Event.jsp";

	}

	private String netcpu() {

		netcpuUtil("minute");

		return "/detail/net_cpu.jsp";
	}

	private String cpudetail() {
		return "/detail/cpudetail.jsp";
	}

	private String memorydetail() {
		return "/detail/memorydetail.jsp";
	}

	private String interfacedetail() {
		return "/detail/interfacedetail.jsp";
	}

	private String portdetail() {
		return "/detail/portdetail.jsp";
	}

	private String bandwidthdetail() {
		return "/detail/bandwidthdetail.jsp";
	}

	// ׷�Ӷಥ���ݰ�
	private String multicastpacket() {
		return "/detail/multicastpacket.jsp";
	}

	// ׷�ӹ㲥���ݰ�
	private String datapacket() {
		return "/detail/datapacket.jsp";
	}

	private String firewallcpu() {
		netcpuUtil("minute");
		String tmp = request.getParameter("id");
		@SuppressWarnings("unused")
		Host host = (Host) getHostById(tmp);
		return "/detail/firewall_cpu.jsp";

	}

	private String netcpu_report() {
		String timeType = getParaValue("timeType");
		if (timeType == null) {
			timeType = "minute";
		}
		netcpuUtil(timeType);
		request.setAttribute("timeType", timeType);
		return "/detail/netcpu_report.jsp";
	}

	private String netresponsetime_report() {
		String timeType = request.getParameter("timeType");
		if (timeType == null) {
			timeType = "minute";
		}
		netpingUtil(timeType);
		return "/detail/netresponsetime_report.jsp";
	}

	private String hostResponseTime_report() {
		String timeType = request.getParameter("timeType");
		if (timeType == null) {
			timeType = "minute";
		}
		hostpingUtil(timeType);
		return "/detail/hostresponsetime_report.jsp";
	}

	private void netcpuUtil(String timeType) {
		String returnStr = "";
		String showpngPath = "";
		String showcpupngPath = "";
		String showresponsepngPath = "";
		String showrpingpngPath = "";

		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable maxhash = new Hashtable();

		String pingconavg = "0";
		String collecttime = null;

		String tmp = request.getParameter("id");
		Host host = (Host) getHostById(tmp);

		String newip = doip(host.getIpAddress());

		I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
		// ��ȡ���������������ֵ��ƽ����ֵ
		Hashtable allavgandmaxHash = new Hashtable();
		try {
			if (host.getCollecttype() != 3 && host.getCategory() != 9)
				allavgandmaxHash = daymanager.getAllAvgAndMaxHisHdx(host
						.getIpAddress(), getStartTime(), getToTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		String url1 = "";
		String allutilStr = "";
		imgurlhash.put("allutilhdx", url1);

		Hashtable cpuhash = new Hashtable();
		try {
			if (host.getCollecttype() != 3 && host.getCategory() != 9)
				cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
						"Utilization", getStartTime(), getToTime());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);

		p_draw_line(cpuhash, "CPU����������ͼ", newip + "cpuhistory", 300, 200);
		imgurlhash.put("cpu", "resource/image/jfreechart/" + newip + "cpuBy"
				+ timeType + ".png");

		collecttime = getCollectTime(host);


		request.setAttribute("collecttime", collecttime);
		request.setAttribute("startdate", getStartDate());
		request.setAttribute("todate", getToDate());
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("chartstr", returnStr);
		request.setAttribute("allutilStr", allutilStr);
		request.setAttribute("rrdstr", showpngPath);
		request.setAttribute("cpurrdstr", showcpupngPath);
		request.setAttribute("showresponsepngPath", showresponsepngPath);
		request.setAttribute("showrpingpngPath", showrpingpngPath);
		request.setAttribute("allavgandmaxHash", allavgandmaxHash);
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

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(
					"yyyy-M-d");
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

			java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(
					"yyyy-M-d");
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

	private void p_drawchartMultiLineMonth(Hashtable hash, String title1,
			String title2, int w, int h, String flag) {
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
					if (flag.equals("AllUtilHdx")) {
						unit = "y(kb/s)";
					} else {
						unit = "y(%)";
					}
					// ����
					for (int j = 0; j < value.length; j++) {
						String val = value[j];
						if (val != null && val.indexOf("&") >= 0) {
							String[] splitstr = val.split("&");
							String splittime = splitstr[0];
							Double v = new Double(splitstr[1]);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date da = sdf.parse(splittime);
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(da);
							Minute minute = new Minute(tempCal
									.get(Calendar.MINUTE), tempCal
									.get(Calendar.HOUR_OF_DAY), tempCal
									.get(Calendar.DAY_OF_MONTH), tempCal
									.get(Calendar.MONTH) + 1, tempCal
									.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(ʱ��)", unit, title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void p_drawchartMultiLine(Hashtable hash, String title1,
			String title2, int w, int h, String flag) {
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
						// �ۺϴ���������
						for (int j = 0; j < vector.size(); j++) {
						}
					} else if (flag.equals("AllUtilHdx")) {
						// �ۺ�����
						for (int j = 0; j < vector.size(); j++) {
							AllUtilHdx obj = (AllUtilHdx) vector.get(j);
							Double v = new Double(obj.getThevalue());
							Calendar temp = obj.getCollecttime();
							Minute minute = new Minute(temp
									.get(Calendar.MINUTE), temp
									.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp
									.get(Calendar.MONTH) + 1, temp
									.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("UtilHdxPerc")) {
						// ����������
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp
									.get(Calendar.MINUTE), temp
									.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp
									.get(Calendar.MONTH) + 1, temp
									.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}

					} else if (flag.equals("UtilHdx")) {
						// ����
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp
									.get(Calendar.MINUTE), temp
									.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp
									.get(Calendar.MONTH) + 1, temp
									.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("ErrorsPerc")) {
						// ����
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp
									.get(Calendar.MINUTE), temp
									.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp
									.get(Calendar.MONTH) + 1, temp
									.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("DiscardsPerc")) {
						// ����
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp
									.get(Calendar.MINUTE), temp
									.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp
									.get(Calendar.MONTH) + 1, temp
									.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					} else if (flag.equals("Packs")) {
						// ���ݰ�
						for (int j = 0; j < vector.size(); j++) {
							Vector obj = (Vector) vector.get(j);
							Double v = new Double((String) obj.get(0));
							String dt = (String) obj.get(1);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date time1 = sdf.parse(dt);
							Calendar temp = Calendar.getInstance();
							temp.setTime(time1);
							Minute minute = new Minute(temp
									.get(Calendar.MINUTE), temp
									.get(Calendar.HOUR_OF_DAY), temp
									.get(Calendar.DAY_OF_MONTH), temp
									.get(Calendar.MONTH) + 1, temp
									.get(Calendar.YEAR));
							ss.addOrUpdate(minute, v);
						}
					}
					s[i] = ss;
				}
				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	public void draw_column(Hashtable bighash, String title1, String title2,
			int w, int h) {
		if (bighash.size() != 0) {
			ChartGraph cg = new ChartGraph();
			int size = bighash.size();
			double[][] d = new double[1][size];
			String c[] = new String[size];
			double[] data1 = new double[size];
			double[] data2 = new double[size];
			String[] labels = new String[size];
			Hashtable hash;
			for (int j = 0; j < size; j++) {
				hash = (Hashtable) bighash.get(new Integer(j));
				c[j] = (String) hash.get("name");
				if (hash.get("Utilization" + "value") == null)
					continue;
				d[0][j] = Double.parseDouble((String) hash.get("Utilization"
						+ "value"));
				data1[j] = Double.parseDouble((String) hash.get("Utilization"
						+ "value"));
				data2[j] = 100 - Double.parseDouble((String) hash
						.get("Utilization" + "value"));
				labels[j] = (String) hash.get("name");
			}
			String rowKeys[] = { "����������" };
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
					rowKeys, c, d);
			cg.zhu(title1, title2, dataset, w, h);

		} else {
			draw_blank(title1, title2, w, h);
		}
		bighash = null;
	}

	public Hashtable createHashtable() {
		Hashtable hashtable = new Hashtable();
		return hashtable;
	}

	public String getStartDate() {
		String startdate = request.getParameter("startdate");
		if (startdate == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			startdate = sdf.format(new Date());
		}
		return startdate;
	}

	public String getToDate() {
		String toDate = request.getParameter("todate");
		if (toDate == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			toDate = sdf.format(new Date());
		}
		return toDate;
	}

	public String getStartTime() {
		String startTime = request.getParameter("startTime");
		if (startTime == null) {
			startTime = getStartDate() + " 00:00:00";
		}
		return startTime;
	}

	public String getToTime() {
		String toTime = request.getParameter("toTime");
		if (toTime == null) {
			toTime = getToDate() + " 23:59:59";
		}
		return toTime;
	}

	public Host getHostById(String id) {
		if (id == null) {
			id = request.getParameter("id");
		}

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(id));

		return host;
	}

	public void setParmarValue(Host host) {
		double cpuvalue = 0;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;
		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {
				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				cpuvalue = new Double(cpu.getThevalue());
			}
			// �õ�ϵͳ����ʱ��
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
						sysservices = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase("sysDescr")) {
						sysdescr = systemdata.getThevalue();
					}
				}
			}
		}
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
	}

	public String getCollectTime(Host host) {
		String collecttime = null;
		Vector pingData = (Vector) ShareData.getPingdata().get(
				host.getIpAddress());
		if (pingData != null && pingData.size() > 0) {
			PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
			Calendar tempCal = (Calendar) pingdata.getCollecttime();
			Date cc = tempCal.getTime();
			collecttime = sdf1.format(cc);
		}
		return collecttime;
	}

	public void choose_p_drawchartMultiLine_type(String timeType,
			Hashtable hash, String title1, String title2, int w, int h) {
		if (timeType == null) {
			timeType = "minute";
		}
		if ("minute".equals(timeType)) {
			p_drawchartMultiLine(hash, title1, title2, w, h);
		} else if ("hour".equals(timeType)) {
			p_drawchartMultiLineByHour(hash, title1, title2, w, h);
		} else if ("day".equals(timeType)) {
			p_drawchartMultiLineByDay(hash, title1, title2, w, h);
		} else if ("month".equals(timeType)) {
			p_drawchartMultiLineByMonth(hash, title1, title2, w, h);
		} else if ("year".equals(timeType)) {
			p_drawchartMultiLine(hash, title1, title2, w, h);
		} else {
			p_drawchartMultiLine(hash, title1, title2, w, h);
		}
	}

	public void p_drawchartMultiLine(Hashtable hash, String title1,
			String title2, int w, int h) {
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
					String namestr = "";
					if ("PhysicalMemory".equalsIgnoreCase(key)) {
						namestr = "�����ڴ�";
					} else if ("VirtualMemory".equalsIgnoreCase(key)) {
						namestr = "�����ڴ�";
					} else if ("SwapMemory".equalsIgnoreCase(key)) {
						namestr = "�����ڴ�";
					} else
						namestr = key;
					TimeSeries ss = new TimeSeries(namestr, Minute.class);
					Vector vector = (Vector) (hash.get(key));
					for (int j = 0; j < vector.size(); j++) {
						Vector obj = (Vector) vector.get(j);
						Double v = new Double((String) obj.get(0));
						String dt = (String) obj.get(1);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date time1 = sdf.parse(dt);
						Calendar temp = Calendar.getInstance();
						temp.setTime(time1);
						Minute minute = new Minute(temp.get(Calendar.MINUTE),
								temp.get(Calendar.HOUR_OF_DAY), temp
										.get(Calendar.DAY_OF_MONTH), temp
										.get(Calendar.MONTH) + 1, temp
										.get(Calendar.YEAR));
						ss.addOrUpdate(minute, v);
					}
					s[i] = ss;
				}
				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void p_drawchartMultiLineByHour(Hashtable hash, String title1,
			String title2, int w, int h) {
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
					TimeSeries ss = new TimeSeries(key, Hour.class);
					Vector vector = (Vector) (hash.get(key));
					for (int j = 0; j < vector.size(); j++) {
						Vector obj = (Vector) vector.get(j);
						Double v = new Double((String) obj.get(0));
						String dt = (String) obj.get(1);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date time1 = sdf.parse(dt);
						Calendar temp = Calendar.getInstance();
						temp.setTime(time1);
						Hour hour = new Hour(temp.get(Calendar.HOUR_OF_DAY),
								temp.get(Calendar.DAY_OF_MONTH), temp
										.get(Calendar.MONTH) + 1, temp
										.get(Calendar.YEAR));
						ss.addOrUpdate(hour, v);
					}
					s[i] = ss;
				}
				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void p_drawchartMultiLineByDay(Hashtable hash, String title1,
			String title2, int w, int h) {
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
					TimeSeries ss = new TimeSeries(key, Day.class);
					Vector vector = (Vector) (hash.get(key));
					for (int j = 0; j < vector.size(); j++) {
						Vector obj = (Vector) vector.get(j);
						Double v = new Double((String) obj.get(0));
						String dt = (String) obj.get(1);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date time1 = sdf.parse(dt);
						Calendar temp = Calendar.getInstance();
						temp.setTime(time1);
						Day day = new Day(temp.get(Calendar.DAY_OF_MONTH), temp
								.get(Calendar.MONTH) + 1, temp
								.get(Calendar.YEAR));
						ss.addOrUpdate(day, v);
					}
					s[i] = ss;
				}
				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void p_drawchartMultiLineByMonth(Hashtable hash, String title1,
			String title2, int w, int h) {
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
					TimeSeries ss = new TimeSeries(key, Month.class);
					Vector vector = (Vector) (hash.get(key));
					for (int j = 0; j < vector.size(); j++) {
						Vector obj = (Vector) vector.get(j);
						Double v = new Double((String) obj.get(0));
						String dt = (String) obj.get(1);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						Date time1 = sdf.parse(dt);
						Calendar temp = Calendar.getInstance();
						temp.setTime(time1);
						Month month = new Month(temp.get(Calendar.MONTH) + 1,
								temp.get(Calendar.YEAR));
						ss.addOrUpdate(month, v);
					}
					s[i] = ss;
				}
				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void p_drawchartMultiLineYear(Hashtable hash, String title1,
			String title2, int w, int h, String flag) {
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
					// ����
					for (int j = 0; j < value.length; j++) {
						String val = value[j];
						if (val != null && val.indexOf("&") >= 0) {
							String[] splitstr = val.split("&");
							String splittime = splitstr[0];
							Double v = new Double(splitstr[1]);
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date da = sdf.parse(splittime);
							Calendar tempCal = Calendar.getInstance();
							tempCal.setTime(da);
							ss.addOrUpdate(new org.jfree.data.time.Hour(da), v);
						}
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(ʱ��)", unit, title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	public void chooseDrawLineType(String timeType, Hashtable hash,
			String title1, String title2, int w, int h) {
		if (timeType == null) {
			timeType = "minute";
		}
		if ("minute".equals(timeType)) {
			p_draw_line(hash, title1, title2, w, h);
		} else if ("hour".equals(timeType)) {
			p_draw_lineByHour(hash, title1, title2, w, h);
		} else if ("day".equals(timeType)) {
			p_draw_lineByDay(hash, title1, title2, w, h);
		} else if ("month".equals(timeType)) {
			p_draw_lineByMonth(hash, title1, title2, w, h);
		} else if ("year".equals(timeType)) {
			p_draw_line(hash, title1, title2, w, h);
		} else {
			p_draw_line(hash, title1, title2, w, h);
		}
	}

	private String area_chooseDrawLineType(String timeType, Hashtable hash,
			Hashtable hash1, String[] brand1, String[] brand2, int w, int h,
			int range1, int range2) {
		String returnStr = "";
		if (timeType == null) {
			timeType = "minute";
		}
		if ("minute".equals(timeType)) {
			returnStr = area_p_draw_line(hash, hash1, brand1, brand2, w, h,
					range1, range2);
		} else if ("hour".equals(timeType)) {
			// p_draw_lineByHour(hash, title1, title2, w, h);
		} else if ("day".equals(timeType)) {
			// p_draw_lineByDay(hash, title1, title2, w, h);
		} else if ("month".equals(timeType)) {
			// p_draw_lineByMonth(hash,title1,title2,w,h);
		} else if ("year".equals(timeType)) {
			// p_draw_line(hash,title1,title2,w,h);
		} else {
			// p_draw_line(hash,title1,title2,w,h);
		}
		return returnStr;
	}

	private String area_chooseDrawMultiLineType(String timeType,
			Hashtable hash, String title, String[] bandch, String[] bandch1,
			int w, int h, int range1, int range2) {
		String returnStr = "";
		if (timeType == null) {
			timeType = "minute";
		}
		if ("minute".equals(timeType)) {
			returnStr = area_p_draw_multiline(hash, title, bandch, bandch1, w,
					h, range1, range2);
		} else if ("hour".equals(timeType)) {
			// p_draw_lineByHour(hash, title1, title2, w, h);
		} else if ("day".equals(timeType)) {
			// p_draw_lineByDay(hash, title1, title2, w, h);
		} else if ("month".equals(timeType)) {
			// p_draw_lineByMonth(hash,title1,title2,w,h);
		} else if ("year".equals(timeType)) {
			// p_draw_line(hash,title1,title2,w,h);
		} else {
			// p_draw_line(hash,title1,title2,w,h);
		}
		return returnStr;
	}

	private String area_chooseDrawLineType(String timeType, Hashtable hash,
			String title1, String title2, int w, int h) {
		String returnStr = "";
		if (timeType == null) {
			timeType = "minute";
		}
		if ("minute".equals(timeType)) {
			returnStr = ChartCreator.area_p_draw_line(hash, title1, title2, w,
					h);
		} else if ("hour".equals(timeType)) {
			p_draw_lineByHour(hash, title1, title2, w, h);
		} else if ("day".equals(timeType)) {
			p_draw_lineByDay(hash, title1, title2, w, h);
		} else if ("month".equals(timeType)) {
			p_draw_lineByMonth(hash, title1, title2, w, h);
		} else if ("year".equals(timeType)) {
			p_draw_line(hash, title1, title2, w, h);
		} else {
			p_draw_line(hash, title1, title2, w, h);
		}
		return returnStr;
	}

	public void p_draw_line(Hashtable hash, String title1, String title2,
			int w, int h) {
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
					Double d = new Double(String.valueOf(v.get(0)));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp
							.get(Calendar.HOUR_OF_DAY), temp
							.get(Calendar.DAY_OF_MONTH), temp
							.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void p_draw_line(List list, String title1, String title2, int w,
			int h) {

		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = "";
				ChartGraph cg = new ChartGraph();

				TimeSeries ss = new TimeSeries(title1, Minute.class);
				TimeSeries[] s = { ss };
				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp
							.get(Calendar.HOUR_OF_DAY), temp
							.get(Calendar.DAY_OF_MONTH), temp
							.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
					unit = (String) v.get(2);
				}
				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String area_p_draw_line(Hashtable hash, Hashtable hash1,
			String[] brand1, String[] brand2, int w, int h, int range1,
			int range2) {
		String seriesKey = SysUtil.getLongID();
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				seriesKey = "";
			} else {
				// ��ʼ������
				final JFreeChart chart = ChartFactory.createTimeSeriesChart(
						brand1[0] + brand1[1] + "����ͼ", "ʱ��", brand2[0],
						ChartCreator.createForceDataset(hash, brand1[0]), true,
						true, false);

				// ����ȫͼ����ɫΪ��ɫ
				chart.setBackgroundPaint(Color.WHITE);

				final XYPlot plot = chart.getXYPlot();
				plot.getDomainAxis().setLowerMargin(0.0);
				plot.getDomainAxis().setUpperMargin(0.0);
				plot.setRangeCrosshairVisible(true);
				plot.setDomainCrosshairVisible(true);
				plot.setBackgroundPaint(Color.WHITE);
				plot.setForegroundAlpha(0.8f);
				plot.setRangeGridlinesVisible(true);
				plot.setRangeGridlinePaint(Color.darkGray);
				plot.setDomainGridlinesVisible(true);
				plot.setDomainGridlinePaint(new Color(139, 69, 19));
				XYLineAndShapeRenderer render0 = (XYLineAndShapeRenderer) plot
						.getRenderer(0);
				render0.setSeriesPaint(0, Color.BLUE);

				// configure the range axis to display directions...
				final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
				rangeAxis.setAutoRangeIncludesZero(true);
				rangeAxis.setRange(0, range1);

				plot.setRangeAxis(rangeAxis);
				final ValueAxis axis2 = new NumberAxis(brand2[1]);
				axis2.setRange(0.0, range2);

				// �������ͼ��ɫ
				XYAreaRenderer xyarearenderer = new XYAreaRenderer();
				xyarearenderer.setSeriesPaint(1, new Color(0, 204, 0));
				xyarearenderer.setSeriesFillPaint(1, new Color(0, 206, 209));
				xyarearenderer.setPaint(new Color(0, 206, 209));
				plot.setDataset(1, ChartCreator.createForceDataset(hash1,
						brand1[1]));
				plot.setRenderer(1, xyarearenderer);
				plot.setRangeAxis(1, axis2);
				plot.mapDatasetToRangeAxis(1, 1);

				LegendTitle legend = chart.getLegend();
				legend.setItemFont(new Font("Verdena", 0, 9));
				JFreeChartBrother jfb = new JFreeChartBrother();
				jfb.setChart(chart);
				jfb.setWidth(w);
				jfb.setHeight(h);

				ResourceCenter.getInstance().getChartStorage().put(seriesKey,
						jfb);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seriesKey;
	}

	public String area_p_draw_multiline(Hashtable hash, String title,
			String[] bandch, String[] bandch1, int w, int h, int range1,
			int range2) {
		String seriesKey = SysUtil.getLongID();
		Vector datasetV = ChartCreator
				.createMultiDataset(hash, bandch, bandch1);
		try {
			if (datasetV == null || datasetV.size() == 0) {
				seriesKey = "";
			} else {
				// ��ʼ������
				final JFreeChart chart = ChartFactory.createTimeSeriesChart(
						title + "����ͼ", "ʱ��", bandch1[0], (XYDataset) datasetV
								.get(0), true, true, false);

				// ����ȫͼ����ɫΪ��ɫ
				chart.setBackgroundPaint(Color.WHITE);
				chart.setBorderPaint(new Color(30, 144, 255));
				chart.setBorderVisible(true);

				final XYPlot plot = chart.getXYPlot();
				plot.getDomainAxis().setLowerMargin(0.0);
				plot.getDomainAxis().setUpperMargin(0.0);
				plot.setRangeCrosshairVisible(true);
				plot.setDomainCrosshairVisible(true);
				plot.setBackgroundPaint(Color.WHITE);
				plot.setForegroundAlpha(0.8f);
				plot.setRangeGridlinesVisible(true);
				plot.setRangeGridlinePaint(Color.darkGray);
				plot.setDomainGridlinesVisible(true);
				plot.setDomainGridlinePaint(new Color(139, 69, 19));
				XYLineAndShapeRenderer render0 = (XYLineAndShapeRenderer) plot
						.getRenderer(0);
				render0.setSeriesPaint(0, Color.BLUE);

				// configure the range axis to display directions...
				final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
				rangeAxis.setAutoRangeIncludesZero(true);
				rangeAxis.setRange(0, range1);

				plot.setRangeAxis(rangeAxis);
				final ValueAxis axis2 = new NumberAxis(bandch1[1]);
				axis2.setRange(0.0, range2);

				// �������ͼ��ɫ
				XYAreaRenderer xyarearenderer = new XYAreaRenderer();
				xyarearenderer.setSeriesPaint(1, new Color(0, 204, 0));
				xyarearenderer.setSeriesFillPaint(1, Color.GREEN);
				xyarearenderer.setPaint(Color.GREEN);
				plot.setDataset(1, (XYDataset) datasetV.get(1));
				plot.setRenderer(1, xyarearenderer);
				plot.setRangeAxis(1, axis2);
				plot.mapDatasetToRangeAxis(1, 1);

				LegendTitle legend = chart.getLegend();
				legend.setItemFont(new Font("Verdena", 0, 9));
				JFreeChartBrother jfb = new JFreeChartBrother();
				jfb.setChart(chart);
				jfb.setWidth(w);
				jfb.setHeight(h);

				ResourceCenter.getInstance().getChartStorage().put(seriesKey,
						jfb);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return seriesKey;
	}

	private void p_draw_lineByHour(Hashtable hash, String title1,
			String title2, int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();
				TimeSeries ss = new TimeSeries(title1, Hour.class);
				TimeSeries[] s = { ss };
				System.out.println(list.size());
				Vector v0 = (Vector) list.get(0);
				String dt0 = (String) v0.get(1);
				SimpleDateFormat sdf0 = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date time0 = sdf0.parse(dt0);
				Calendar temp0 = Calendar.getInstance();
				temp0.setTime(time0);
				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);

					Hour hour = new Hour(temp.get(Calendar.HOUR_OF_DAY), temp
							.get(Calendar.DAY_OF_MONTH), temp
							.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					if (temp.get(Calendar.HOUR_OF_DAY) > temp0
							.get(Calendar.HOUR_OF_DAY)) {
						ss.addOrUpdate(hour, d);
					}

				}

				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void p_draw_lineByDay(Hashtable hash, String title1, String title2,
			int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();
				TimeSeries ss = new TimeSeries(title1, Day.class);
				TimeSeries[] s = { ss };

				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);

					Day day = new Day(temp.get(Calendar.DAY_OF_MONTH), temp
							.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));

					ss.addOrUpdate(day, d);
				}

				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void p_draw_lineByMonth(Hashtable hash, String title1,
			String title2, int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null)
					unit = "%";
				ChartGraph cg = new ChartGraph();
				TimeSeries ss = new TimeSeries(title1, Month.class);
				TimeSeries[] s = { ss };

				for (int j = 0; j < list.size(); j++) {
					Vector v = (Vector) list.get(j);
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);

					Month month = new Month(temp.get(Calendar.MONTH) + 1, temp
							.get(Calendar.YEAR));

					ss.addOrUpdate(month, d);
				}

				cg
						.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1,
								title2, w, h);
			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String hostping_report() {
		String timeType = request.getParameter("timeType");
		if (timeType == null) {
			timeType = "minute";
		}
		hostpingUtil(timeType);
		return "/detail/hostping_report.jsp";
	}

	private String hostping() {
		try {
			hostpingUtil("minute");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

		Hashtable diskhash = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		Date d = new Date();
		String startdate = sdf0.format(d);
		String todate = sdf0.format(d);
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		try {
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(),
					"Disk", starttime, totime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String newip = doip(host.getIpAddress());

		// ��ͼ����������ʾ�������ݵ���״ͼ��
		try {
			draw_column(diskhash, "����������", newip + "disk", 750, 150);
		} catch (Exception e) {

		}
		Hashtable imgurlhash = new Hashtable();
		imgurlhash.put("disk", "resource/image/jfreechart/" + newip + "disk"
				+ ".png");
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("newip", newip);
		request.setAttribute("Disk", diskhash);

		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/topology/network/hostwmiview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// Linux������,��ת��Linux�ɼ�չʾҳ��
			return "/topology/network/hostlinuxview.jsp";
			// }else if(host.getCollecttype() == SystemConstant.COLLECTTYPE_SNMP
			// && host.getSysOid().indexOf("1.3.6.1.4.1.8072")>=0){
			// //Linux������,��ת��Linux�ɼ�չʾҳ��
			// return "/topology/network/hostlinuxview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// Linux������,��ת��Linux�ɼ�չʾҳ��
			return "/topology/network/hostlinuxview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// Linux������,��ת��Linux�ɼ�չʾҳ��
			return "/topology/network/hostlinuxview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/topology/network/hostaixview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/topology/network/hostaixview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/topology/network/hostaixview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/topology/network/hostsolarisview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/topology/network/hosthpview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("scounix") >= 0) {
			// scounixware������,��ת��scounixware�ɼ�չʾҳ��
			return "/topology/network/hostscounixview.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("scoopenserver") >= 0) {
			// scoopenserver������,��ת��scoopenserver�ɼ�չʾҳ��
			return "/topology/network/hostscounixview.jsp";
		} else if ((host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host
				.getCollecttype() == SystemConstant.COLLECTTYPE_PING)
				&& host.getOstype() == 15) {
			// AS400������
			return "/topology/network/host_telnet_AS400view.jsp";
		} else
			return "/topology/network/hostview.jsp";

	}

	private void hostpingUtil(String timeType) {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash1 = new Hashtable();// "System","Ping"--current
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();
		String tmp = "";

		double cpuvalue = 0;
		String pingconavg = "0";
		String pingrespavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		try {
			tmp = request.getParameter("id");

			Host host = (Host) PollingEngine.getInstance().getNodeByID(
					Integer.parseInt(tmp));

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
				hash1 = hostlastmanager.getbyCategories_share(host
						.getIpAddress(), item1, starttime, endtime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			// pingƽ��ֵ
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			// ��collectdataȡcpu,�ڴ����ʷ����
			Hashtable cpuhash = new Hashtable();
			try {
				cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
						"Utilization", starttime1, totime1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				memhash = hostlastmanager.getMemory_share(host.getIpAddress(),
						"Memory", starttime, endtime);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Hashtable[] memoryhash = null;
			try {
				memoryhash = hostmanager.getMemory(host.getIpAddress(),
						"Memory", starttime1, totime1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// ��memory���ֵ
			memmaxhash = memoryhash[1];
			memavghash = memoryhash[2];

			String cpumax = "";
			if (cpuhash.get("max") != null) {
				cpumax = (String) cpuhash.get("max");
			}
			maxhash.put("cpu", cpumax);
			// cpuƽ��ֵ
			// maxhash = new Hashtable();
			String cpuavg = "";
			if (cpuhash.get("avgcpucon") != null) {
				cpuavg = (String) cpuhash.get("avgcpucon");
			}
			maxhash.put("cpuavg", cpuavg);

			imgurlhash.put("ConnectUtilization", "resource/image/jfreechart/"
					+ newip + "ConnectUtilization" + ".png");

			Hashtable ResponseTimehash = new Hashtable();
			try {
				ResponseTimehash = hostmanager.getCategory(host.getIpAddress(),
						"Ping", "ResponseTime", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ResponseTimehash.get("avgpingcon") != null)
				pingrespavg = (String) ResponseTimehash.get("avgpingcon");

			maxhash.put("avgrespcon", pingrespavg);

			imgurlhash.put("ResponseTime", "resource/image/jfreechart/" + newip
					+ "ResponseTime" + ".png");

			Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
					host.getIpAddress());
			if (ipAllData != null) {
				Vector cpuV = (Vector) ipAllData.get("cpu");
				if (cpuV != null && cpuV.size() > 0) {

					CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
					cpuvalue = new Double(cpu.getThevalue());
				}
				// �õ�ϵͳ����ʱ��
				Vector systemV = (Vector) ipAllData.get("system");
				if (systemV != null && systemV.size() > 0) {
					for (int i = 0; i < systemV.size(); i++) {
						SystemCollectEntity systemdata = (SystemCollectEntity) systemV
								.get(i);
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysUpTime")) {
							sysuptime = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysServices")) {
							sysservices = systemdata.getThevalue();
						}
						if (systemdata.getSubentity().equalsIgnoreCase(
								"sysDescr")) {
							sysdescr = systemdata.getThevalue();
						}
					}
				}
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			Vector pingData = (Vector) ShareData.getPingdata().get(
					host.getIpAddress());
			if (pingData != null && pingData.size() > 0) {
				PingCollectEntity pingdata = (PingCollectEntity) pingData.get(0);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				collecttime = sdf1.format(cc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("memmaxhash", memmaxhash);
		request.setAttribute("memavghash", memavghash);
		request.setAttribute("memhash", memhash);

		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash1", hash1);
		request.setAttribute("max", maxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("startdate", getStartDate());
		request.setAttribute("todate", getToDate());
		request.setAttribute("cpuvalue", cpuvalue);
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("sysuptime", sysuptime);
		request.setAttribute("sysservices", sysservices);
		request.setAttribute("sysdescr", sysdescr);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("timeType", timeType);

	}

	private String hostcpu_report() {
		String timeType = request.getParameter("timeType");
		if (timeType == null) {
			timeType = "minute";
		}
		hostcpuUtil(timeType);
		return "/detail/hostcpu_report.jsp";
	}

	private String hostmemory_report() {
		String timeType = request.getParameter("timeType");
		if (timeType == null) {
			timeType = "minute";
		}
		hostcpuUtil(timeType);
		return "/detail/hostmemory_report.jsp";
	}

	private String hostcpu() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		if (!NodeUtil.isOnlyCollectPing(host)) {
			hostcpuUtil("minute");
		}
		Hashtable diskhash = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		Date d = new Date();
		String startdate = sdf0.format(d);
		String todate = sdf0.format(d);
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		try {
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(),
					"Disk", starttime, totime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String newip = doip(host.getIpAddress());
		request.setAttribute("newip", newip);
		request.setAttribute("Disk", diskhash);
		if (NodeUtil.isOnlyCollectPing(host)) {
			// ֻ�ɼ���ͨ�ʵ���Ϣʱ,����cpu���ڴ������
			return "/detail/host_cpu_onlyping.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmicpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& (host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0 || host
						.getSysOid().indexOf("1.3.6.1.4.1.8072.3.2.10") >= 0)) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& (host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0 || host
						.getSysOid().indexOf("1.3.6.1.4.1.8072.3.2.10") >= 0)) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& (host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0 || host
						.getSysOid().indexOf("1.3.6.1.4.1.8072.3.2.10") >= 0)) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aixcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solariscpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("scounix") >= 0) {
			// scounixware������,��ת��scounixware�ɼ�չʾҳ��
			return "/detail/host_scounixcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("scoopenserver") >= 0) {
			// scoopenserver������,��ת��scoopenserver�ɼ�չʾҳ��
			return "/detail/host_scounixcpu.jsp";
		} else {
			return "/detail/host_cpu.jsp";
		}

	}

	private String hostwindows() {
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		if (!NodeUtil.isOnlyCollectPing(host)) {
			hostcpuUtil("minute");
		}
		Hashtable diskhash = new Hashtable();
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		Date d = new Date();
		String startdate = sdf0.format(d);
		String todate = sdf0.format(d);
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		try {
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(),
					"Disk", starttime, totime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String newip = doip(host.getIpAddress());
		request.setAttribute("newip", newip);
		request.setAttribute("Disk", diskhash);
		if (NodeUtil.isOnlyCollectPing(host)) {
			// ֻ�ɼ���ͨ�ʵ���Ϣʱ,����cpu���ڴ������
			return "/detail/host_cpu_onlyping.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS������,��ת��WMI�ɼ�չʾҳ��
			return "/detail/host_wmicpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linux.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux������,��ת��LINUX�ɼ�չʾҳ��
			return "/detail/host_linuxcpu.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aix.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aix.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix������,��ת��Aix�ɼ�չʾҳ��
			return "/detail/host_aix.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris������,��ת��Solaris�ɼ�չʾҳ��
			return "/detail/host_solaris.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix������,��ת��hpunix�ɼ�չʾҳ��
			return "/detail/host_hpunix.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("scounix") >= 0) {
			// scounixware������,��ת��scounixware�ɼ�չʾҳ��
			return "/detail/host_scounixware.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("scoopenserver") >= 0) {
			// scounixware������,��ת��scounixware�ɼ�չʾҳ��
			return "/detail/host_scounixware.jsp";
		} else {
			return "/detail/host_windows.jsp";
		}

	}

	private String hostconfig() {
		hostcpuUtil("minute");
		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));
		if (host.getCollecttype() == SystemConstant.COLLECTTYPE_WMI
				&& host.getSysOid().equalsIgnoreCase("1.3.6.1.4.1.311.1.1.3")) {
			// WINDOWS��u21153 ����u-28820 ��MI��u-26938 չu31034 ҳu-26782
			return "/detail/host_wmiconfig.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux��u21153 ����u-28820 ��INUX��u-26938 չu31034 ҳu-26782
			return "/detail/host_linuxconfig.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux��u21153 ����u-28820 ��INUX��u-26938 չu31034 ҳu-26782
			return "/detail/host_linuxconfig.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
			// linux��u21153 ����u-28820 ��INUX��u-26938 չu31034 ҳu-26782
			return "/detail/host_linuxconfig.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix��u21153 ����u-28820 ��ix��u-26938 չu31034 ҳu-26782
			return "/detail/host_aixconfig.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix��u21153 ����u-28820 ��ix��u-26938 չu31034 ҳu-26782
			return "/detail/host_aixconfig.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH
				&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
			// Aix��u21153 ����u-28820 ��ix��u-26938 չu31034 ҳu-26782
			return "/detail/host_aixconfig.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.42") >= 0) {
			// Solaris��u21153 ����u-28820 ��olaris��u-26938 չu31034 ҳu-26782
			return "/detail/host_solarisconfig.jsp";
		} else if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL
				&& host.getSysOid().indexOf("1.3.6.1.4.1.11") >= 0) {
			// hpunix��u21153 ����u-28820 ��punix��u-26938 չu31034 ҳu-26782
			return "/detail/host_hpunixconfig.jsp";
		} else
			return "/detail/hostwmiconfig.jsp";
	}

	private void hostcpuUtil(String timeType) {

		Hashtable imgurlhash = new Hashtable();
		Hashtable hash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Hashtable memhash = new Hashtable();
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();
		Hashtable memavghash = new Hashtable();

		double cpuvalue = 0;
		String pingconavg = "0";
		String collecttime = null;
		String sysuptime = null;
		String sysservices = null;
		String sysdescr = null;

		String tmp = request.getParameter("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(
				Integer.parseInt(tmp));

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
			memhash = hostlastmanager.getMemory_share(host.getIpAddress(),
					"Memory", starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			diskhash = hostlastmanager.getDisk_share(host.getIpAddress(),
					"Disk", starttime, endtime);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ��ͼ����������ʾ�������ݵ���״ͼ��
		try {
			draw_column(diskhash, "", newip + "disk", 750, 150);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Hashtable cpuhash = new Hashtable();
		try {
			cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
					"Utilization", starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hashtable[] memoryhash = null;
		try {
			memoryhash = hostmanager.getMemory(host.getIpAddress(), "Memory",
					starttime1, totime1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ��memory���ֵ
		memmaxhash = memoryhash[1];
		memavghash = memoryhash[2];
		// cpu���ֵ
		maxhash = new Hashtable();
		String cpumax = "";
		if (cpuhash.get("max") != null) {
			cpumax = (String) cpuhash.get("max");
		}
		maxhash.put("cpu", cpumax);
		// cpuƽ��ֵ
		String cpuavg = "";
		if (cpuhash.get("avgcpucon") != null) {
			cpuavg = (String) cpuhash.get("avgcpucon");
		}
		maxhash.put("cpuavg", cpuavg);
		// ��ͼ
		p_draw_line(cpuhash, "", newip + "cpu", 740, 120);

		imgurlhash.put("cpu", "resource/image/jfreechart/" + newip + "cpu"
				+ ".png");
		imgurlhash.put("memory", "resource/image/jfreechart/" + newip
				+ "memory" + ".png");
		imgurlhash.put("disk", "resource/image/jfreechart/" + newip + "disk"
				+ ".png");

		Hashtable ipAllData = (Hashtable) ShareData.getSharedata().get(
				host.getIpAddress());
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {
				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				if (cpu != null && cpu.getThevalue() != null) {
					cpuvalue = new Double(cpu.getThevalue());
				}
			}
			// �õ�ϵͳ����ʱ��
			Vector systemV = (Vector) ipAllData.get("system");
			if (systemV != null && systemV.size() > 0) {
				for (int i = 0; i < systemV.size(); i++) {
					SystemCollectEntity systemdata = (SystemCollectEntity) systemV
							.get(i);
					if (systemdata.getSubentity().equalsIgnoreCase("sysUpTime")) {
						sysuptime = systemdata.getThevalue();
					}
					if (systemdata.getSubentity().equalsIgnoreCase(
							"sysServices")) {
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
			ConnectUtilizationhash = hostmanager.getCategory(host
					.getIpAddress(), "Ping", "ConnectUtilization", starttime1,
					totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			pingconavg = pingconavg.replace("%", "");
		}

		Vector pingData = (Vector) ShareData.getPingdata().get(
				host.getIpAddress());
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
		request.setAttribute("startdate", getStartDate());
		request.setAttribute("todate", getToDate());
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
		request.setAttribute("timeType", timeType);
	}

	private String netping_report() {
		String timeType = request.getParameter("timeType");
		if (timeType == null) {
			timeType = "minute";
		}
		netpingUtil(timeType);
		return "/detail/netping_report.jsp";
	}

	private String netping() {
		String timeType = "minute";
		netpingUtil(timeType);
		return "/detail/net_ping.jsp";
	}

	private String firewallping() {
		String timeType = "minute";
		netpingUtil(timeType);
		String tmp = request.getParameter("id");
		@SuppressWarnings("unused")
		Host host = getHostById(tmp);
		return "/detail/firewall_ping.jsp";

	}

	private void netpingUtil(String timeType) {
		Hashtable imgurlhash = new Hashtable();
		Hashtable hash1 = new Hashtable();// "System","Ping"--current
		Hashtable maxhash = new Hashtable();// "Ping"--max
		Hashtable cpumaxhash = new Hashtable();// "CPU"--max
		Hashtable memorymaxhash = new Hashtable();// "Memory"--max
		String tmp = "";
		String pingconavg = "0";
		String pingrespavg = "0";
		String collecttime = null;
		String returnStr = "";
		String returnStr1 = "";
		String allutilStr = "";
		String memoryStr = "";
		try {
			tmp = request.getParameter("id");
			Host host = getHostById(tmp);
			String newip = doip(host.getIpAddress());
			String[] time = { "", "" };
			getTime(request, time);
			String starttime = time[0];
			String endtime = time[1];
			String[] item1 = { "System", "Ping" };
			I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
			try {
				hash1 = hostlastmanager.getbyCategories_share(host
						.getIpAddress(), item1, starttime, endtime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// ��collectdataȡcpu,�ڴ����ʷ����
			Hashtable cpuhash = new Hashtable();
			try {
				cpuhash = hostmanager.getCategory(host.getIpAddress(), "CPU",
						"Utilization", getStartTime(), getToTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// cpu���ֵ
			String cpumax = "";
			if (cpuhash.get("max") != null) {
				cpumax = (String) cpuhash.get("max");
			}
			cpumaxhash.put("cpu", cpumax);
			// cpuƽ��ֵ
			String cpuavg = "";
			if (cpuhash.get("avgcpucon") != null) {
				cpuavg = (String) cpuhash.get("avgcpucon");
			}
			cpumaxhash.put("cpuavg", cpuavg);

			// ��collectdataȡ�ڴ����ʷ����
			Hashtable memoryhash = new Hashtable();
			try {
				memoryhash = hostmanager.getCategory(host.getIpAddress(),
						"Memory", "Utilization", getStartTime(), getToTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// memory���ֵ
			String memerymax = "";
			if (memoryhash.get("max") != null) {
				memerymax = (String) memoryhash.get("max");
			}
			memorymaxhash.put("memory", memerymax);
			// cpuƽ��ֵ
			String memoryavg = "";
			if (memoryhash.get("avgmemory") != null) {
				cpuavg = (String) memoryhash.get("avgmemory");
			}
			memorymaxhash.put("memoryavg", memoryavg);
			// ����MemoryͼƬ
			memoryStr = area_chooseDrawLineType(timeType, memoryhash, "�ڴ�������",
					"�ڴ�������(%)", 350, 200);

			Hashtable ConnectUtilizationhash = new Hashtable();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(host
						.getIpAddress(), "Ping", "ConnectUtilization",
						getStartTime(), getToTime());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("avgpingcon") != null)
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			// ��ͼ
			imgurlhash.put("ConnectUtilization", "resource/image/jfreechart/"
					+ newip + "ConnectUtilization" + "by" + timeType + ".png");
			// pingƽ��ֵ
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			Hashtable ResponseTimehash = new Hashtable();
			try {
				ResponseTimehash = hostmanager.getCategory(host.getIpAddress(),
						"Ping", "ResponseTime", getStartTime(), getToTime());
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ResponseTimehash.get("avgpingcon") != null)
				pingrespavg = (String) ResponseTimehash.get("avgpingcon");
			// ��ͼ
			String[] bandch = { "��ͨ��", "��Ӧʱ��" };
			String[] bandch1 = { "��ͨ��(%)", "��Ӧʱ��(ms)" };
			returnStr = area_chooseDrawLineType(timeType,
					ConnectUtilizationhash, ResponseTimehash, bandch, bandch1,
					350, 200, 110, 20);

			returnStr1 = area_chooseDrawLineType(timeType, cpuhash, "CPU������",
					"CPU������(%)", 350, 200);

			// Responseƽ��ֵ
			maxhash.put("avgrespcon", pingrespavg);

			// imgurlhash
			imgurlhash.put("ResponseTime", "resource/image/jfreechart/" + newip
					+ "ResponseTime" + ".png");

			// imgurlhash

			setParmarValue(host);
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			collecttime = getCollectTime(host);

			Hashtable value = new Hashtable();
			I_HostCollectDataDay daymanager = new HostCollectDataDayManager();
			String[] banden3 = { "AllInBandwidthUtilHdx",
					"AllOutBandwidthUtilHdx" };
			String[] bandch3 = { "�������", "��������" };
			String[] bandch4 = { "�������(kb/s)", "��������(kb/s)" };
			try {
				// ��������ʾ����
				value = daymanager.getmultiHisHdx(host.getIpAddress(), "all",
						"", banden3, bandch3, getStartTime(), getToTime(),
						"AllUtilHdx");
				allutilStr = area_chooseDrawMultiLineType(timeType, value,
						"�ۺ�����", bandch3, bandch4, 350, 200, 100000, 100000);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("hash1", hash1);
		request.setAttribute("max", maxhash);
		request.setAttribute("cpumax", cpumaxhash);
		request.setAttribute("id", tmp);
		request.setAttribute("startdate", getStartDate());
		request.setAttribute("todate", getToDate());
		request.setAttribute("collecttime", collecttime);
		request.setAttribute("timeType", timeType);
		request.setAttribute("pingconavg", new Double(pingconavg));
		request.setAttribute("chartstr", returnStr);
		request.setAttribute("chartstr1", returnStr1);
		request.setAttribute("allutilStr", allutilStr);
		request.setAttribute("memoryStr", memoryStr);
	}

	private static final String filename = "complexdemo";

	private static String getPath(String ext) {
		return Util.getJRobinDemoPath(filename + "." + ext);
	}

	private static String getPath(int version, String ext) {
		return Util.getJRobinDemoPath(filename + version + "." + ext);
	}

	private static void println(String msg) {
		System.out.println(msg);
	}

	private static void createDatabase(String xmlPath) throws IOException,
			RrdException {
		String rrdPath = getPath("rrd");
		println("-- Importing XML file: " + xmlPath);
		println("-- to RRD file: " + rrdPath);
		// RrdDbPool pool = RrdDbPool.getInstance();
		// RrdDb rrd = pool.requestRrdDb(rrdPath, xmlPath);
		// println("-- RRD file created");
		// pool.release(rrd);
	}

	private static void createGraphs() throws RrdException, IOException {
		GregorianCalendar start, stop;
		RrdGraph graph = new RrdGraph(true);
		String rrdPath = getPath("rrd");
		String rrdFile = "demo.rrd";
		long _end = Util.getTime(), _start = _end - 1 * 86400; // ����ʱ��Ϊ��ǰʱ�䣬��ʼʱ��Ϊһ��ǰ

		println("-- Creating graph 1");
		start = new GregorianCalendar(2003, 7, 20);
		stop = new GregorianCalendar(2003, 7, 27);

		RrdGraphDef def = new RrdGraphDef(start, stop);

		// ���������ļ����壬������������������

		// ���������ļ����壬������������������
		RrdDef rrdDef = new RrdDef(rrdFile, _start, 300); // ���ݼ��Ϊ300��
		rrdDef.addDatasource("value1", "GAUGE", 600, Double.NaN, Double.NaN); // ��������Դ�����Զ�����
		// //��������Դ�����Զ�����

		// ���¶���鵵���ݣ�����α�������
		rrdDef.addArchive("AVERAGE", 0.5, 1, 288); // ���������Դ������ÿһ������������������288�����ݣ����������һ�������
		rrdDef.addArchive("AVERAGE", 0.5, 7, 288); // ÿ7�����ݣ�ȡƽ��ֵ��Ȼ�󱣴棬����288�����ݣ����������һ�ܵ�����
		RrdDb rrdDb = new RrdDb(rrdDef); // �������ݶ��崴�������ļ�

		for (long t = _start; t < _end; t += 300) {
			Sample sample = rrdDb.createSample(t);
			sample.setValue("value1", Math.random() * 100);
			sample.update();
		}
		String pngFile = "";

		def.setImageBorder(Color.GRAY, 1);
		def
				.setTitle("JRobinComplexDemo@Ldemo graph 1@r\nNetwork traffic overview");
		def.setVerticalLabel("bits per second");

		def.datasource("demo", rrdFile, "value1", "AVERAGE");

		def.datasource("ifInOctets", rrdPath, "ifInOctets", "AVERAGE");
		def.datasource("ifOutOctets", rrdPath, "ifOutOctets", "AVERAGE");
		def.datasource("bitIn", "ifInOctets,8,*");
		def.datasource("bitOut", "ifOutOctets,8,*");
		def.comment(" ");
		def.area("bitIn", new Color(0x00, 0xFF, 0x00), "Incoming traffic ");
		def.line("bitOut", new Color(0x00, 0x00, 0x33), "Outgoing traffic\n\n");
		def.gprint("bitIn", "MAX", "Max:   @6.1 @sbit/s");
		def.gprint("bitOut", "MAX", "      @6.1 @sbit/s\n");
		def.gprint("bitIn", "MIN", "Min:   @6.1 @sbit/s");
		def.gprint("bitOut", "MIN", "      @6.1 @sbit/s");
		def.comment("       Connection:  100 Mbit/s\n");
		def.gprint("bitIn", "AVG", "Avg:   @6.1 @sbit/s");
		def.gprint("bitOut", "AVG", "      @6.1 @sbit/s");
		def.comment("       Duplex mode: FD - fixed\n\n");
		def.gprint("bitIn", "LAST", "Cur:   @6.1 @sbit/s");
		def.gprint("bitOut", "LAST", "      @6.1 @sbit/s\n\n");
		def.comment("[ courtesy of www.cherrymon.org ]@L");
		def.comment("Generated: " + timestamp() + "  @r");

		graph.setGraphDef(def);
		pngFile = getPath(1, "png");
		graph.saveAsPNG(pngFile);
		String gifFile = getPath(1, "gif");
		graph.saveAsGIF(gifFile);
		String jpgFile = getPath(1, "jpg");
		graph.saveAsJPEG(jpgFile, 0.6F);

		// Create server load and cpu usage of a day
		println("-- Creating graph 2");
		start = new GregorianCalendar(2003, 7, 19);
		stop = new GregorianCalendar(2003, 7, 20);

		def = new RrdGraphDef(start, stop);
		def.setImageBorder(Color.GRAY, 1);
		def
				.setTitle("JRobinComplexDemo@Ldemo graph 2@r\nServer load and CPU utilization");
		def.datasource("load", rrdPath, "serverLoad", "AVERAGE");
		def.datasource("user", rrdPath, "serverCPUUser", "AVERAGE");
		def.datasource("nice", rrdPath, "serverCPUNice", "AVERAGE");
		def.datasource("system", rrdPath, "serverCPUSystem", "AVERAGE");
		def.datasource("idle", rrdPath, "serverCPUIdle", "AVERAGE");
		def.datasource("total", "user,nice,+,system,+,idle,+");
		def.datasource("busy", "user,nice,+,system,+,total,/,100,*");
		def.datasource("p25t50", "busy,25,GT,busy,50,LE,load,0,IF,0,IF");
		def.datasource("p50t75", "busy,50,GT,busy,75,LE,load,0,IF,0,IF");
		def.datasource("p75t90", "busy,75,GT,busy,90,LE,load,0,IF,0,IF");
		def.datasource("p90t100", "busy,90,GT,load,0,IF");
		def.comment("CPU utilization (%)\n ");
		def.area("load", new Color(0x66, 0x99, 0xcc), " 0 - 25%");
		def.area("p25t50", new Color(0x00, 0x66, 0x99), "25 - 50%@L");
		def.gprint("busy", "MIN", "Minimum:@5.1@s%");
		def.gprint("busy", "MAX", "Maximum:@5.1@s% @r ");
		def.area("p50t75", new Color(0x66, 0x66, 0x00), "50 - 75%");
		def.area("p75t90", new Color(0xff, 0x66, 0x00), "75 - 90%");
		def.area("p90t100", new Color(0xcc, 0x33, 0x00), "90 - 100%@L");
		def.gprint("busy", "AVG", " Average:@5.1@s%");
		def.gprint("busy", "LAST", "Current:@5.1@s% @r ");
		def.comment("\nServer load\n ");
		def.line("load", new Color(0x00, 0x00, 0x00), "Load average (5 min)@L");
		def.gprint("load", "MIN", "Minimum:@5.2@s%");
		def.gprint("load", "MAX", "Maximum:@5.2@s% @r ");
		def.gprint("load", "AVG", "Average:@5.2@s%");
		def.gprint("load", "LAST", "Current:@5.2@s% @r");
		def.comment("\n\n[ courtesy of www.cherrymon.org ]@L");
		def.comment("Generated: " + timestamp() + "  @r");

		graph.setGraphDef(def);
		pngFile = getPath(2, "png");
		graph.saveAsPNG(pngFile);
		gifFile = getPath(2, "gif");
		graph.saveAsGIF(gifFile);
		jpgFile = getPath(2, "jpg");
		graph.saveAsJPEG(jpgFile, 0.6F);

		// Create ftp graph for a month
		println("-- Creating graph 3");
		start = new GregorianCalendar(2003, 7, 19, 12, 00);
		stop = new GregorianCalendar(2003, 7, 20, 12, 00);

		def = new RrdGraphDef(start, stop);
		def.setImageBorder(Color.GRAY, 1);
		def.setFrontGrid(false);
		def.setTitle("JRobinComplexDemo@Ldemo graph 3@r\nFTP Usage");
		def.datasource("ftp", rrdPath, "ftpUsers", "AVERAGE");
		def.line("ftp", new Color(0x00, 0x00, 0x33), "FTP connections");
		def.gprint("ftp", "AVG", "( average: @0,");
		def.gprint("ftp", "MIN", "never below: @0 )\n\n");
		def.comment("  Usage spread:");
		def.area(new GregorianCalendar(2003, 7, 19, 17, 00), Double.MIN_VALUE,
				new GregorianCalendar(2003, 7, 19, 23, 00), Double.MAX_VALUE,
				Color.RED, "peak period");
		def.area(new GregorianCalendar(2003, 7, 20, 5, 00), Double.MIN_VALUE,
				new GregorianCalendar(2003, 7, 20, 8, 30), Double.MAX_VALUE,
				Color.LIGHT_GRAY, "quiet period\n");
		def.comment("  Rise/descend:");
		def.area("ftp", new Color(0x00, 0x00, 0x33), null);
		def.line(new GregorianCalendar(2003, 7, 19, 12, 00), 110,
				new GregorianCalendar(2003, 7, 19, 20, 30), 160, Color.PINK,
				"climb slope", 2);
		def.line(new GregorianCalendar(2003, 7, 19, 20, 30), 160,
				new GregorianCalendar(2003, 7, 20, 8, 00), 45, Color.CYAN,
				"fall-back slope\n", 2);
		def.vrule(new GregorianCalendar(2003, 7, 20), Color.YELLOW, null);
		def.comment("\n\n[ courtesy of www.cherrymon.org ]@L");
		def.comment("Generated: " + timestamp() + "  @r");

		graph.setGraphDef(def);
		pngFile = getPath(3, "png");
		graph.saveAsPNG(pngFile, 500, 300);
		gifFile = getPath(3, "gif");
		graph.saveAsGIF(gifFile, 500, 300);
		jpgFile = getPath(3, "jpg");
		graph.saveAsJPEG(jpgFile, 500, 300, 0.6F);
		println("-- Finished");
		println("**************************************");
		println("Check your " + Util.getJRobinDemoDirectory() + " directory.");
		println("You should see nine nice looking graphs starting with ["
				+ filename + "],");
		println("three different graphs, each in three different image formats");
		println("**************************************");
	}

	private static String timestamp() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return df.format(new Date());
	}

	public static void main(String[] args) throws IOException, RrdException {
		if (args.length == 0) {
			println("Usage: ComplexDemo [path to rrdtool_dump.xml file]");
			println("You can download separate rrdtool_dump.xml file from:");
			println("http://www.sourceforge.net/projects/jrobin");
			System.exit(-1);
		}
		long start = System.currentTimeMillis();

		println("********************************************************************");
		println("* JRobinComplexDemo                                                *");
		println("*                                                                  *");
		println("* This demo creates 3 separate graphs and stores them under        *");
		println("* several formats in 9 files.  Values are selected from a large    *");
		println("* RRD file that will be created by importing an XML dump           *");
		println("* of approx. 7 MB.                                                 *");
		println("*                                                                  *");
		println("* Graphs are created using real-life values, original RRD file     *");
		println("* provided by www.cherrymon.org. See the ComplexDemo               *");
		println("* sourcecode on how to create the graphs generated by this demo.   *");
		println("********************************************************************");

		createDatabase(args[0]);
		createGraphs();

		long stop = System.currentTimeMillis();
		println("-- Demo finished in " + ((stop - start) / 1000.0)
				+ " seconds.");
	}
}
