package com.afunms.application.manage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.OracleLockInfo;
import com.afunms.application.util.DBPool;
import com.afunms.application.util.DBRefreshHelper;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DateE;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.OracleJdbcUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.Node;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.report.jfree.ChartCreator;
import com.afunms.system.model.User;
import com.afunms.topology.util.KeyGenerator;
import com.lowagie.text.DocumentException;

@SuppressWarnings("unchecked")
public class OracleManager extends BaseManager implements ManagerInterface {
	DateE datemanager = new DateE();
	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
	I_HostCollectData hostmanager = new HostCollectDataManager();
	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String add() {
		DBVo vo = new DBVo();
		vo.setId(KeyGenerator.getInstance().getNextKey());
		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		String allbid = "";
		String[] businessids = getParaArrayValue("checkbox");
		if (businessids != null && businessids.length > 0) {
			for (int i = 0; i < businessids.length; i++) {
				String bid = businessids[i];
				allbid = allbid + bid + ",";
			}
		}
		vo.setBid(allbid);
		vo.setManaged(getParaIntValue("managed"));
		vo.setDbtype(getParaIntValue("dbtype"));
		DBDao dao = new DBDao();
		try {
			dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/db.do?action=list";
	}

	private String addmanage() {
		OracleEntity vo = new OracleEntity();
		OraclePartsDao dao = new OraclePartsDao();
		int sid = getParaIntValue("sid");
		try {
			vo = (OracleEntity) dao.getOracleById(sid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		vo.setManaged(1);
		dao = new OraclePartsDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/db.do?action=list";
	}

	// oracle可用性报表
	private String cancelmanage() {
		OracleEntity vo = new OracleEntity();
		OraclePartsDao dao = new OraclePartsDao();
		int sid = getParaIntValue("sid");
		try {
			vo = (OracleEntity) dao.getOracleById(sid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		vo.setManaged(0);
		dao = new OraclePartsDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/db.do?action=list";
	}

	// oracle 性能报表
	private String dboraReportdown() {
		Date d = new Date();
		DBVo vo = new DBVo();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		Vector tableinfo_v = new Vector();
		Hashtable maxping = new Hashtable();
		Hashtable ConnectUtilizationhash = new Hashtable();
		DBDao dao = new DBDao();
		String id = (String) session.getAttribute("id");
		String sid = (String) session.getAttribute("sid");
		String pingconavg = "0";
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		Hashtable reporthash = new Hashtable();
		try {
			request.setAttribute("id", id);
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo typevo = null;
		try {
			typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		OraclePartsDao oracledao = new OraclePartsDao();
		try {
			OracleEntity oracle = (OracleEntity) oracledao.getOracleById(Integer.parseInt(sid));
			vo.setDbName(oracle.getSid());
			vo.setCollecttype(oracle.getCollectType());
			vo.setManaged(oracle.getManaged());
			vo.setBid(oracle.getBid());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			oracledao.close();
		}
		I_HostCollectData hostmanager = new HostCollectDataManager();
		try {
			ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime, totime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (ConnectUtilizationhash.get("avgpingcon") != null) {
			pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");// 平均连通率
		}
		if (pingconavg != null) {
			pingconavg = pingconavg.replace("%", "");
		}
		pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最小连通率
		String newip = SysUtil.doip(vo.getIpAddress());
		// 画图
		p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);
		Hashtable dbio = new Hashtable();
		String hex = IpTranslation.formIpToHex(vo.getIpAddress());
		String serverip = hex + ":" + sid;
		try {
			dao = new DBDao();
			// 取状态信息
			dbio = dao.getOracle_nmsoradbio(serverip);
			tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
			Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
			String status = String.valueOf(statusHashtable.get("status"));
			if ("1".equals(status)) {
				pingnow = "100";
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", pingconavg + "%");// 平均连通率
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("dbio", dbio);
		reporthash.put("tableinfo_v", tableinfo_v);
		reporthash.put("ip", vo.getIpAddress());
		reporthash.put("ping", maxping);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			report.createReport_ora("temp/dbora_report.xls");
			request.setAttribute("filename", report.getFileName());
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_oraDoc(fileName);// word综合报表
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_oraPDF(fileName);// word业务分析表
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_oraNewDoc(fileName);// word业务分析表
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_oraNewPDF(fileName, "pdf");// word业务分析表
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "/capreport/db/download.jsp";
	}

	// oracle 可用性报表
	private String dboraReportdownusable() {
		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String id = (String) session.getAttribute("id");
		String sid = (String) session.getAttribute("sid");
		String pingmin = "";// 最小连通率
		String pingnow = "0.0";// 当前连通率
		try {
			DBDao dao = new DBDao();
			try {
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + id;
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				sysValue = dao.getOracle_nmsorasys(serverip);
				String statusStr = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
				if ("1".equals(statusStr)) {
					runstr = "正在运行";
					pingnow = "100.0";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			request.setAttribute("cursors", cursors);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("runstr", runstr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			session.setAttribute("Mytime1", time1);
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			session.setAttribute("Mystarttime1", time1);// HONGLI MODIFY
			session.setAttribute("Mytotime1", time1);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(id, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			pingmin = (String) ConnectUtilizationhash.get("pingmax");

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
			// 画图
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("tableinfo_v", tableinfo_v);
		request.setAttribute("dbio", dbio);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		request.setAttribute("pingmin", pingmin);// HONGLI ADD 最小连通率
		request.setAttribute("pingnow", pingnow);// HONGLI ADD 当前连通率
		return "/capreport/db/showDbPingReport.jsp";
	}

	private String dboraReportdownusableload() {
		Date d = new Date();
		DBDao dao = null;
		Hashtable memValue = new Hashtable();
		String runstr = "服务停止";
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String dbname = "";
		String dbnamestr = "";
		String typename = "ORACLE";
		Hashtable hash = new Hashtable();
		Hashtable memhash = new Hashtable();
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();
		Hashtable memavghash = new Hashtable();
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Vector vector = new Vector();
		DBVo vo = null;
		String sid = "";
		String pingmin = "0.0%";// HONG ADD 最小连通率
		String pingnow = "0.0%";// HONGLI ADD 当前连通率
		try {
			ip = getParaValue("ipaddress");
			dao = new DBDao();
			vo = (DBVo) dao.findByCondition("ip_address", ip, 1).get(0);
			OraclePartsDao oracledao = new OraclePartsDao();
			List sidlist = new ArrayList();
			try {
				sidlist = oracledao.findOracleParts(vo.getId());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				oracledao.close();
			}
			if (sidlist != null) {
				for (int j = 0; j < sidlist.size(); j++) {
					OracleEntity ora = (OracleEntity) sidlist.get(j);
					sid = ora.getId() + "";
					break;
				}
			}
			dbname = vo.getDbName() + "(" + ip + ")";
			dbnamestr = vo.getDbName();
			String newip = doip(ip);
			request.setAttribute("newip", newip);
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				String statusStr = String.valueOf(statusHashtable.get("status"));
				if ("1".equals(statusStr)) {
					runstr = "正在运行";
					pingnow = "100.0%";// HONGLI ADD
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			Hashtable pinghash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime, totime);
			p_draw_line(pinghash, "", newip + "ConnectUtilization", 740, 120);
			String pingconavg = "";
			if (pinghash.get("avgpingcon") != null) {
				pingconavg = (String) pinghash.get("avgpingcon");
				pingmin = (String) pinghash.get("pingmax") + "%";
				maxping.put("pingmin", pingmin);
			}
			maxping.put("pingnow", pingnow);// 增加当前连通率
			p_draw_line(pinghash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (pinghash.get("max") != null) {
				ConnectUtilizationmax = (String) pinghash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("hash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("memmaxhash", memmaxhash);
		request.setAttribute("memavghash", memavghash);
		request.setAttribute("diskhash", diskhash);
		request.setAttribute("memhash", memhash);
		Hashtable reporthash = new Hashtable();
		Vector pdata = (Vector) pingdata.get(ip);
		if (pdata != null && pdata.size() > 0) {
			for (int m = 0; m < pdata.size(); m++) {
				PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
				if (hostdata.getSubentity().equals("ConnectUtilization")) {
					reporthash.put("time", hostdata.getCollecttime());
					reporthash.put("Ping", hostdata.getThevalue());
					reporthash.put("ping", maxping);
				}
			}
		} else {
			reporthash.put("ping", maxping);
		}
		// 求oracle宕机次数
		String downnum = "0";
		Hashtable pinghash = new Hashtable();
		try {
			pinghash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime, totime);
			if (pinghash.get("downnum") != null) {
				downnum = (String) pinghash.get("downnum");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		DBTypeDao dbTypeDao = new DBTypeDao();
		int count = 0;
		try {
			count = dbTypeDao.finddbcountbyip(ip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbTypeDao.close();
		}
		String grade = "优";
		if (count > 0) {
			grade = "良";
		}
		if (!"0".equals(downnum)) {
			grade = "差";
		}
		reporthash.put("dbname", dbname);
		reporthash.put("dbnamestr", dbnamestr);
		reporthash.put("starttime", starttime);// HONGLI MODIFY
		reporthash.put("totime", totime);// HONGLI MODIFY
		reporthash.put("memvalue", memValue);
		reporthash.put("typename", typename);
		reporthash.put("runstr", runstr);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count + "");
		reporthash.put("grade", grade);
		reporthash.put("ip", ip);
		if (vector == null) {
			vector = new Vector();
		}
		reporthash.put("tableinfo_v", vector);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			report.createReportusa_oraXls("temp/dborausa_report.xls");
			request.setAttribute("filename", report.getFileName());
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dborausa_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReportusa_oraDoc(fileName);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dborausa_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReportusa_oraPDF(fileName);
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * oracle 跳转到性能报表
	 * 
	 * @return
	 */
	private String dbReport() {
		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String id = (String) session.getAttribute("id");
		String sid = (String) session.getAttribute("sid");
		String pingnow = "0.0";// HONGLI ADD 当前连通率
		try {
			DBDao dao = new DBDao();
			try {
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				sysValue = dao.getOracle_nmsorasys(serverip);
				String statusStr = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				dbio = dao.getOracle_nmsoradbio(serverip);
				tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
				if ("1".equals(statusStr)) {
					runstr = "正在运行";
					pingnow = "100.0";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			request.setAttribute("cursors", cursors);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("sysvalue", sysValue);
			request.setAttribute("runstr", runstr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			session.setAttribute("Mytime1", time1);
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			session.setAttribute("Mystarttime1", time1);
			session.setAttribute("Mytotime1", time1);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(id, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");// 平均连通率
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			String pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最小连通率
			// 画图
			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			request.setAttribute("avgpingcon", avgpingcon);
			request.setAttribute("pingnow", pingnow);// 当前连通率
			request.setAttribute("pingmin", pingmin);// 最小连通率
			request.setAttribute("notpingcon", 100 - avgpingcon);
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("tableinfo_v", tableinfo_v);
		request.setAttribute("dbio", dbio);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);// 平均连通率
		return "/capreport/db/showDbReport.jsp";
	}

	public String delete() {
		String id = getParaValue("radio");
		DBDao dao = new DBDao();
		try {
			dao.delete(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		int nodeId = Integer.parseInt(id);
		PollingEngine.getInstance().deleteNodeByID(nodeId);
		DBPool.getInstance().removeConnect(nodeId);

		return "/db.do?action=list";
	}

	private String doip(String ip) {
		ip = SysUtil.doip(ip);
		return ip;
	}

	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(时间)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String execute(String action) {
		if (action.equals("oraclebackup")) {
			return oraclebackup();
		}
		if (action.equals("oracleasmclient")) {
			return oracleasmclient();
		}
		if (action.equals("oracledisk")) {
			return oracledisk();
		}
		if (action.equals("oraclerac")) {
			return oraclerac();
		}
		if (action.equals("oracleraccrstatus")) {
			return oracleraccrstatus();
		}
		if (action.equals("oracleracevent")) {
			return oracleracevent();
		}
		if (action.equals("oracle")) {
			return oracle();
		}
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("ready_add")) {
			return "/application/db/add.jsp";
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("oracledelete")) {
			return oracledelete();
		}
		if (action.equals("ready_edit")) {
			DaoInterface dao = new DBDao();
			setTarget("/application/db/edit.jsp");
			return readyEdit(dao);
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("cancelmanage")) {
			return cancelmanage();
		}
		if (action.equals("addmanage")) {
			return addmanage();
		}
		if (action.equals("oracleping")) {
			return oracleping();
		}
		if (action.equals("oraclespace")) {
			return oraclespace();
		}
		if (action.equals("oracletopsql")) {
			return oracletopsql();
		}
		if (action.equals("oraclesession")) {
			return oraclesession();
		}
		if (action.equals("oraclerollback")) {
			return oraclerollback();
		}

		if (action.equals("oracletable")) {
			return oracletable();
		}
		if (action.equals("oraclelock")) {
			return oraclelock();
		}
		if (action.equals("oraclemem")) {
			return oraclemem();
		}
		if (action.equals("oracleevent")) {
			return oracleevent();
		}
		if (action.equals("oracleuser")) {
			return oracleuser();
		}
		if (action.equals("oraclewait")) {
			return oraclewait();
		}
		if (action.equals("oraclejob")) {
			return oraclejob();
		}
		if (action.equals("isOracleOK")) {
			return isOracleOK();
		}
		if (action.equals("sychronizeData")) {
			return sychronizeData();
		}
		if (action.equals("dboraReportdownusable")) {
			return dboraReportdownusable();
		}
		if (action.equals("oracleCldReport")) {
			return oracleCldReport();
		}
		if (action.equals("dbReport")) {
			return dbReport();
		}
		if (action.equals("dboraReportdown")) {
			return dboraReportdown();
		}
		if (action.equals("oraEventReport")) {
			return oraEventReport();
		}
		if (action.equals("oracleManagerEventReport")) {
			return oracleManagerEventReport();
		}
		if (action.equals("oracleManagerEventReportQuery")) {
			return oracleManagerEventReportQuery();
		}
		if (action.equals("oraclebaseinfo")) {
			return oraclebaseinfo();
		}
		if (action.equals("dboraReportdownusableload")) {
			return dboraReportdownusableload();
		}

		if (action.equals("getrealtimeoratop")) {
			return getrealtimeoratop();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	/**
	 * 根据ip地址和端口得到oracle的sysvalue信息
	 * 
	 * @param ipAddress
	 * @param port
	 * @return
	 * @author makewen
	 * @date Apr 13, 2011
	 */
	public Hashtable geHashtable(String ipAddress, String sid) {
		Hashtable sysValue = new Hashtable();
		DBDao dao = new DBDao();
		String hex = IpTranslation.formIpToHex(ipAddress);
		String serverip = hex + ":" + sid;
		try {
			sysValue = dao.getOracle_nmsorasys(serverip);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return sysValue;
	}

	/**
	 * 获取占内存的前10条语句实时信息
	 */
	public String getrealtimeoratop() {
		String dbid = (String) request.getParameter("dbid");
		String startdate = getParaValue("startdate");
		String todate = getParaValue("todate");

		String last = getParaValue("last");

		String starttime = null;
		String totime = null;
		if ("false".equals(last)) {
			starttime = getParaValue("starttime");
			totime = getParaValue("totime");
		}

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		if (startdate == null || startdate.equals("")) {
			startdate = sdf1.format(new Date());
		}
		if (todate == null || todate.equals("")) {
			todate = sdf1.format(new Date());
		}
		if (starttime == null || starttime.equals("")) {
			starttime = sdf2.format(new Date(new Date().getTime() - 60 * 1000 * 5));
		}
		if (totime == null || totime.equals("")) {
			totime = sdf2.format(new Date());
		}

		String starttime1 = startdate + " " + starttime;
		String totime1 = todate + " " + totime;

		DBDao dbdao = new DBDao();
		DBVo dbvo = null;
		try {
			dbvo = (DBVo) dbdao.findByID(dbid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbdao.close();
		}
		OracleJdbcUtil util = null;
		ResultSet rs = null;
		Vector sqltop = new Vector();
		try {
			String dburl = "jdbc:oracle:thin:@" + dbvo.getIpAddress() + ":" + dbvo.getPort() + ":" + dbvo.getDbName();
			util = new OracleJdbcUtil(dburl, dbvo.getUser(), EncryptUtil.decode(dbvo.getPassword()));
			util.jdbc();
			String sqlTsql = "select sql_text,pct_bufgets,username,last_active_time from (select rank() over(order by disk_reads " + "desc) as rank_bufgets,to_char(100 * ratio_to_report(disk_reads) over(), '999.99') "
					+ "pct_bufgets,sql_text,b.username as username,last_active_time from " + "v$sqlarea,dba_users b where b.user_id = PARSING_USER_ID and " + "last_active_time >= to_date('" + starttime1 + "','yyyy-mm-dd hh24:mi:ss') "
					+ "and last_active_time <= to_date('" + totime1 + "','yyyy-mm-dd hh24:mi:ss')) " + "where rank_bufgets < 11 and ROWNUM <= 10";
			rs = util.stmt.executeQuery(sqlTsql);
			ResultSetMetaData rsmd6 = rs.getMetaData();
			while (rs.next()) {
				Hashtable return_value = new Hashtable();
				for (int i = 1; i <= rsmd6.getColumnCount(); ++i) {
					String col = rsmd6.getColumnName(i);
					if (rs.getString(i) != null) {
						String tmp = rs.getString(i).toString();
						return_value.put(col.toLowerCase(), tmp);
					} else {
						return_value.put(col.toLowerCase(), "--");
					}
				}
				sqltop.addElement(return_value);
				return_value = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				util.closeStmt();
				util.closeConn();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		request.setAttribute("dbid", dbid);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("sqltop", sqltop);
		return "/application/db/oracletoprealtime.jsp";
	}

	private String isOracleOK() {
		DBDao dbDao = null;
		String id = request.getParameter("id");
		String myport = request.getParameter("myport");
		int port = Integer.parseInt(myport);
		String myUser = request.getParameter("myUser");
		String myPassword = request.getParameter("myPassword");
		String sid = request.getParameter("sid");
		boolean isOK = false;
		try {
			dbDao = new DBDao();
			if (sid != null && sid.trim().length() > 0) {
				// ORACLE数据库
				DBVo vo = (DBVo) dbDao.findByID(id);
				try {
					isOK = dbDao.getOracleIsOK(vo.getIpAddress(), port, vo.getDbName(), vo.getUser(), EncryptUtil.decode(vo.getPassword()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				request.setAttribute("dbtype", "Oracle");
				request.setAttribute("sid", vo.getDbName());
			} else {
				// 其他数据库
				DBVo vo = (DBVo) dbDao.findByID(id);
				DBTypeDao typeDao = new DBTypeDao();
				DBTypeVo typevo = null;
				try {
					typevo = (DBTypeVo) typeDao.findByID(vo.getDbtype() + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					typeDao.close();
				}
				if (typevo.getDbtype().equalsIgnoreCase("mysql")) {
					isOK = dbDao.getMySqlIsOk(vo.getIpAddress(),myport, myUser, myPassword, vo.getDbName());
				} else if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
					isOK = dbDao.getSqlserverIsOk(vo.getIpAddress(), myUser, myPassword);
				} else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {
					isOK = dbDao.getSysbaseIsOk(vo.getIpAddress(), myUser, myPassword, port);
				} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
					isOK = dbDao.getInformixIsOk(vo.getIpAddress(), port + "", myUser, myPassword, vo.getDbName(), vo.getAlias());
				} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
					isOK = dbDao.getDB2IsOK(vo.getIpAddress(), port, vo.getDbName(), myUser, myPassword);
				}
				request.setAttribute("dbtype", typevo.getDbtype());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbDao != null) {
				dbDao.close();
			}
		}
		request.setAttribute("oracleIsOK", isOK);
		return "/tool/oracleisok.jsp";
	}

	private String list() {
		DBDao dao = new DBDao();
		List list = new ArrayList();
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		for (int i = 0; i < list.size(); i++) {
			DBVo vo = (DBVo) list.get(i);
			Node DBNode = PollingEngine.getInstance().getNodeByID(vo.getId());
			if (DBNode == null) {
				vo.setStatus(0);
			} else {
				vo.setStatus(DBNode.getStatus());
			}
		}
		request.setAttribute("list", list);
		return "/application/db/list.jsp";
	}

	private String oracle() {
		DBVo vo = new DBVo();

		Hashtable sysValue = new Hashtable();
		Hashtable memPerfValue = new Hashtable();
		Hashtable maxhash = new Hashtable();

		Vector contrFile_v = new Vector();
		Vector logFile_v = new Vector();
		Vector keepObj_v = new Vector();
		Vector extent_v = new Vector();
		Hashtable isArchive_h = new Hashtable();
		Hashtable cursors = new Hashtable();
		Hashtable memValue = new Hashtable();
		Vector tableinfo = new Vector();

		String lstrnStatu = "";
		String pingconavg = "0";
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			String sid = getParaValue("id");
			request.setAttribute("dbtye", typevo.getDbdesc());

			request.setAttribute("db", vo);
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				contrFile_v = dao.getOracle_nmsoracontrfile(serverip);
				logFile_v = dao.getOracle_nmsoralogfile(serverip);
				extent_v = dao.getOracle_nmsoraextent(serverip);
				keepObj_v = dao.getOracle_nmsorakeepobj(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
				memValue = dao.getOracle_nmsoramemvalue(serverip);
				tableinfo = dao.getOracle_nmsoraspaces(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			double avgpingcon = new Double(pingconavg + "").doubleValue();
			String chart1 = null;
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
			request.setAttribute("sid", sid);
			request.setAttribute("max", maxhash);
			request.setAttribute("chart1", chart1);
			request.setAttribute("sysvalue", sysValue);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("contrFile_v", contrFile_v);
			request.setAttribute("logFile_v", logFile_v);
			request.setAttribute("extent_v", extent_v);
			request.setAttribute("keepObj_v", keepObj_v);
			request.setAttribute("avgpingcon", avgpingcon);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memValue", memValue);
			request.setAttribute("tableinfo", tableinfo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/db/oracle.jsp";
	}

	private String oracleasmclient() {
		Hashtable cursors = new Hashtable();
		String lstrnStatu = "";
		DBVo vo = new DBVo();
		Hashtable isArchive_h = new Hashtable();
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector asmclient_v = new Vector();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				asmclient_v = dao.getOracle_nmsoraasmclient(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("asmclient_v", asmclient_v);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oracleasmclient.jsp";
	}

	private String oraclebackup() {
		Hashtable cursors = new Hashtable();
		String lstrnStatu = "";
		DBVo vo = new DBVo();
		Hashtable isArchive_h = new Hashtable();
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector backup_v = new Vector();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			// 2010-HONGLI
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				backup_v = dao.getOracle_nmsorabackup(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("backup_v", backup_v);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oraclebackup.jsp";
	}

	private String oraclebaseinfo() {
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		DBVo vo = new DBVo();
		Hashtable sysValue = new Hashtable();
		Hashtable baseinfoHash = new Hashtable();
		String pingconavg = "0";
		String chart1 = null;
		String sid = getParaValue("id");
		double avgpingcon = 0;
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				baseinfoHash = dao.getOracle_nmsorabaseinfo(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("chart1", chart1);
		request.setAttribute("baseinfoHash", baseinfoHash);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oraclebaseinfo.jsp";
	}

	/**
	 * @author HONGLI MODIFY 2010-10-28 oracle 综合报表展现
	 */
	private String oracleCldReport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
			request.setAttribute("startdate", startdate);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
			request.setAttribute("todate", todate);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";

		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = getParaValue("id");
		Hashtable memValue = new Hashtable();// HONGLI ADD 数据库的内存配置信息
		String pingnow = "0.0";// HONGLI ADD 当前连通率
		String pingmin = "0.0";// HONGLI ADD 最小连通率
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String statusStr = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				statusStr = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				memValue = dao.getOracle_nmsoramemvalue(serverip);
				dbio = dao.getOracle_nmsoradbio(serverip);
				tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(statusStr)) {
				runstr = "正在运行";
				pingnow = "100.0";
			}
			// 去除单位MB\KB
			String[] sysItem = { "shared_pool", "large_pool", "buffer_cache", "java_pool", "aggregate_PGA_target_parameter", "total_PGA_allocated", "maximum_PGA_allocated" };
			DecimalFormat df = new DecimalFormat("#.##");
			if (memValue != null) {
				for (int i = 0; i < sysItem.length; i++) {
					String value = "";
					if (memValue.get(sysItem[i]) != null) {
						value = (String) memValue.get(sysItem[i]);
					}
					if (!value.equals("")) {
						if (value.indexOf("MB") != -1) {
							value = value.replace("MB", "");
						}
						if (value.indexOf("KB") != -1) {
							value = value.replace("KB", "");
						}
					} else {
						value = "0";
					}
					memValue.put(sysItem[i], df.format(Double.parseDouble(value)));
				}
			}
			request.setAttribute("memValue", memValue);
			request.setAttribute("cursors", cursors);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("sysvalue", sysValue);
			request.setAttribute("runstr", runstr);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
				pingmin = (String) ConnectUtilizationhash.get("pingmax");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
				pingmin = pingmin.replace("%", "");
			}
			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

			// 求oracle告警次数
			String downnum = "0";
			Hashtable pinghash = new Hashtable();
			try {
				pinghash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime, totime);
				if (pinghash.get("downnum") != null) {
					downnum = (String) pinghash.get("downnum");
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			DBTypeDao dbTypeDao = new DBTypeDao();
			int count = 0;
			try {
				count = dbTypeDao.finddbcountbyip(ip);
				request.setAttribute("count", count);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbTypeDao.close();
			}
			String grade = "优";
			if (count > 0) {
				grade = "良";
			}

			if (!"0".equals(downnum)) {
				grade = "差";
			}
			request.setAttribute("downnum", downnum);
			request.setAttribute("grade", grade);
			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1) {
				status = 99;
			}
			if (level1 == -1) {
				level1 = 99;
			}
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), Integer.parseInt(sid));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					eventdao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		String newip = SysUtil.doip(vo.getIpAddress());// HONGLI ADD
		request.setAttribute("list", eventList);
		request.setAttribute("newip", newip);// HONGLI ADD
		request.setAttribute("ipaddresid", vo.getIpAddress() + ":" + sid);// HONGLI
		// ADD
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("tableinfo_v", tableinfo_v);
		request.setAttribute("dbio", dbio);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon + "");
		request.setAttribute("pingmin", pingmin);// HONGLI ADD 最小连通率
		request.setAttribute("pingnow", pingnow);// HONGLI ADD 当前连通率

		return "/capreport/db/showDbOracleCldReport.jsp";
	}

	private String oracledelete() {
		String[] ids = getParaArrayValue("checkbox");
		if (ids != null && ids.length > 0) {
			// 进行删除
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
		if (status == -1) {
			status = 99;
		}
		if (level1 == -1) {
			level1 = 99;
		}
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
			User vo = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			StringBuffer s = new StringBuffer();
			s.append("where recordtime>= '" + starttime1 + "' " + "and recordtime<='" + totime1 + "'");
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
									s.append(" and ( businessid = '," + bids[i].trim() + ",' ");
									flag = 1;
								} else {
									s.append(" or businessid = '," + bids[i].trim() + ",' ");
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

		setTarget("/oracle.do?action=oracleevent");
		return list(dao, sql);
	}

	private String oracledisk() {
		Hashtable cursors = new Hashtable();
		String lstrnStatu = "";
		DBVo vo = new DBVo();
		Hashtable isArchive_h = new Hashtable();
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector sessioninfo_v = new Vector();
		Vector disk_v = new Vector();
		Vector asmclient_v = new Vector();
		Vector asmdiskgroup_v = new Vector();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				disk_v = dao.getOracle_nmsoradiskdata(serverip);
				sessioninfo_v = dao.getOracle_nmsorasessiondata(serverip);
				asmclient_v = dao.getOracle_nmsoraasmclient(serverip);
				asmdiskgroup_v = dao.getOracle_nmsoraasmdiskgroup(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("sessioninfo_v", sessioninfo_v);
		request.setAttribute("disk_v", disk_v);
		request.setAttribute("asmclient_v", asmclient_v);
		request.setAttribute("asmdiskgroup_v", asmdiskgroup_v);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oracledisk.jsp";
	}

	private String oracleevent() {
		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		String lstrnStatu = "";
		Hashtable isArchive_h = new Hashtable();
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Hashtable memValue = new Hashtable();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		List list = new ArrayList();
		int status = -1;
		int level1 = 99;
		String b_time = "";
		String t_time = "";
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String statusStr = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				statusStr = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(statusStr)) {
				runstr = "正在运行";
			}
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			b_time = getParaValue("startdate");
			t_time = getParaValue("todate");

			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			if (b_time == null) {
				b_time = sdf1.format(new Date());
			}
			if (t_time == null) {
				t_time = sdf1.format(new Date());
			}
			String starttime1 = b_time + " 00:00:00";
			String totime1 = t_time + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

			status = getParaIntValue("status");
			level1 = getParaIntValue("level1");
			if (level1 == -1) {
				level1 = 99;
			}
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao eventdao = new EventListDao();
				try {
					list = eventdao.getQuery(starttime1, totime1, "db", status + "", level1 + "", user.getBusinessids(), Integer.parseInt(sid));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					eventdao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("oramem", memValue);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("avgpingcon", avgpingcon);

		return "/application/db/oracleevent.jsp";
	}

	private String oraclejob() {
		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector jobv = new Vector();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";

			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);// ?
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
				jobv = dao.getOracle_nmsorajobs(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("job_v", jobv);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oraclejob.jsp";
	}

	private String oraclelock() {
		Hashtable cursors = new Hashtable();
		String lstrnStatu = "";
		DBVo vo = new DBVo();
		Hashtable isArchive_h = new Hashtable();
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector lockinfo_v = new Vector();
		OracleLockInfo oracleLockInfo = null;
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String id = "";
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";

			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				lockinfo_v = dao.getOracle_nmsoralock(serverip);
				oracleLockInfo = dao.getOracle_nmsoralockinfo(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("oracleLockInfo", oracleLockInfo);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);

			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", id);
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("lockinfo_v", lockinfo_v);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oraclelock.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-17 事件报表
	 * @return
	 */
	public String oracleManagerEventReport() {
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		} else {
			try {
				startdate = sdf0.format(sdf0.parse(getParaValue("startdate")));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		} else {
			try {
				todate = sdf0.format(sdf0.parse(getParaValue("todate")));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		request.setAttribute("startdate", starttime);
		request.setAttribute("todate", totime);
		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		String id = (String) session.getAttribute("id");
		String sid = (String) session.getAttribute("sid");
		String downnum = "";
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();
			int count = 0;
			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
				request.setAttribute("count", count);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbTypeDao.close();
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1) {
				status = 99;
			}
			if (level1 == -1) {
				level1 = 99;
			}
			request.setAttribute("status", status);
			request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), Integer.parseInt(sid));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					eventdao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		request.setAttribute("list", eventList);
		request.setAttribute("downnum", downnum);
		request.setAttribute("vo", vo);
		request.setAttribute("typevo", typevo);
		return "/capreport/db/showOraEventReport.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-17 事件报表 按日期查询
	 * @return
	 */
	public String oracleManagerEventReportQuery() {
		return oracleManagerEventReport();
	}

	private String oraclemem() {
		DBVo vo = new DBVo();

		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Hashtable memValue = new Hashtable();
		String pingconavg = "0";
		String sid = getParaValue("sid");
		String chart1 = null;
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(getParaValue("id"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			OraclePartsDao oracledao = new OraclePartsDao();
			try {
				OracleEntity oracle = (OracleEntity) oracledao.getOracleById(Integer.parseInt(sid));
				vo.setDbName(oracle.getSid());
				vo.setCollecttype(oracle.getCollectType());
				vo.setManaged(oracle.getManaged());
				vo.setBid(oracle.getBid());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				oracledao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String statusStr = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				sysValue = dao.getOracle_nmsorasys(serverip);
				statusStr = String.valueOf(statusHashtable.get("status"));
				memValue = dao.getOracle_nmsoramemvalue(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(statusStr)) {
				runstr = "正在运行";
			}
			request.setAttribute("runstr", runstr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			double avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("oramem", memValue);
		request.setAttribute("sysvalue", sysValue);
		return "/application/db/oraclemem.jsp";
	}

	private String oracleping() {
		DBVo vo = new DBVo();

		Hashtable sysValue = new Hashtable();
		Hashtable memPerfValue = new Hashtable();
		Hashtable maxhash = new Hashtable();

		Vector contrFile_v = new Vector();
		Vector logFile_v = new Vector();
		Vector keepObj_v = new Vector();
		Vector extent_v = new Vector();
		Hashtable isArchive_h = new Hashtable();
		Hashtable cursors = new Hashtable();
		Hashtable memValue = new Hashtable();
		Vector tableinfo = new Vector();

		String lstrnStatu = "";

		String pingconavg = "0";
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}
			String sid = getParaValue("id");
			request.setAttribute("dbtye", typevo.getDbdesc());

			request.setAttribute("db", vo);
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				contrFile_v = dao.getOracle_nmsoracontrfile(serverip);
				logFile_v = dao.getOracle_nmsoralogfile(serverip);
				extent_v = dao.getOracle_nmsoraextent(serverip);
				keepObj_v = dao.getOracle_nmsorakeepobj(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
				memValue = dao.getOracle_nmsoramemvalue(serverip);
				tableinfo = dao.getOracle_nmsoraspaces(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			double avgpingcon = new Double(pingconavg + "").doubleValue();
			String chart1 = null;
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
			request.setAttribute("sid", sid);
			request.setAttribute("max", maxhash);
			request.setAttribute("chart1", chart1);
			request.setAttribute("sysvalue", sysValue);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("contrFile_v", contrFile_v);
			request.setAttribute("logFile_v", logFile_v);
			request.setAttribute("extent_v", extent_v);
			request.setAttribute("keepObj_v", keepObj_v);
			request.setAttribute("avgpingcon", avgpingcon);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memValue", memValue);
			request.setAttribute("tableinfo", tableinfo);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/application/db/oracleping.jsp";
	}

	private String oraclerac() {
		DBVo vo = new DBVo();
		Hashtable returnHash = new Hashtable();
		DBDao dao = new DBDao();
		try {
			String id = getParaValue("id");
			request.setAttribute("id", id);
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		try {
			dao = new DBDao();
			returnHash = dao.getOracle_racstatus(vo.getIpAddress());
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}

		request.setAttribute("vo", vo);
		request.setAttribute("returnHash", returnHash);
		return "/application/db/oracleRacDetail.jsp";
	}

	private String oracleraccrstatus() {
		DBVo vo = new DBVo();
		Hashtable returnHash = new Hashtable();
		DBDao dao = new DBDao();
		try {
			String id = getParaValue("id");
			request.setAttribute("id", id);
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		try {
			dao = new DBDao();
			returnHash = dao.getOracle_racstatus(vo.getIpAddress());
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}

		Vector raccrstatusVector = null;
		try {
			dao = new DBDao();
			raccrstatusVector = dao.getOracle_raccrstatus(vo.getIpAddress());
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}

		request.setAttribute("vo", vo);
		request.setAttribute("returnHash", returnHash);
		request.setAttribute("raccrstatusVector", raccrstatusVector);

		return "/application/db/oracleRaccrstatus.jsp";
	}

	private String oracleracevent() {
		DBVo vo = new DBVo();
		Hashtable returnHash = new Hashtable();
		DBDao dao = new DBDao();
		try {
			String id = getParaValue("id");
			request.setAttribute("id", id);
			vo = (DBVo) dao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		try {
			dao = new DBDao();
			returnHash = dao.getOracle_racstatus(vo.getIpAddress());
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time1 = sdf.format(new Date());

		String starttime1 = time1 + " 00:00:00";
		String totime1 = time1 + " 23:59:59";
		String strStartDay = getParaValue("startdate");
		String strToDay = getParaValue("todate");
		if (strStartDay != null && !"".equals(strStartDay)) {
			starttime1 = strStartDay + " 00:00:00";
		}
		if (strToDay != null && !"".equals(strToDay)) {
			totime1 = strToDay + " 23:59:59";
		}
		List list = new ArrayList();
		try {
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			EventListDao eventdao = new EventListDao();
			try {
				list = eventdao.getQuery(starttime1, totime1, "db", "-1", "3", user.getBusinessids(), vo.getId());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				eventdao.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		String b_time = "";
		String t_time = "";

		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		if (b_time == null) {
			b_time = sdf1.format(new Date());
		}
		if (t_time == null) {
			t_time = sdf1.format(new Date());
		}
		request.setAttribute("vo", vo);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		request.setAttribute("returnHash", returnHash);
		return "/application/db/oracleRacEvent.jsp";

	}

	private String oraclerollback() {
		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector rollbackinfo_v = new Vector();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);// ?
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
				rollbackinfo_v = dao.getOracle_nmsorarollback(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("rollbackinfo_v", rollbackinfo_v);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oraclerollback.jsp";
	}

	private String oraclesession() {
		Hashtable cursors = new Hashtable();
		String lstrnStatu = "";
		DBVo vo = new DBVo();
		Hashtable isArchive_h = new Hashtable();
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector sessioninfo_v = new Vector();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				sessioninfo_v = dao.getOracle_nmsorasessiondata(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);

			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("sessioninfo_v", sessioninfo_v);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oraclesession.jsp";
	}

	private String oraclespace() {
		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = this.getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
				dbio = dao.getOracle_nmsoradbio(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("cursors", cursors);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("sysvalue", sysValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);

		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("tableinfo_v", tableinfo_v);
		request.setAttribute("dbio", dbio);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oraclespace.jsp";
	}

	private String oracletable() {// yangjun
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable cursors = new Hashtable();
		DBVo vo = new DBVo();
		Hashtable sysValue = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector table_v = new Vector();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				table_v = dao.getOracle_nmsoratables(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("table_v", table_v);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oracletable.jsp";

	}

	private String oracletopsql() {
		DBVo vo = new DBVo();
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		Hashtable sysValue = new Hashtable();
		Hashtable imgurlhash = new Hashtable();
		Hashtable maxhash = new Hashtable();
		Vector sql_v = new Vector();
		Vector sql_readwrite_v = new Vector();
		Vector sql_sort_v = new Vector();
		String pingconavg = "0";
		double avgpingcon = 0;
		String chart1 = null;
		String sid = this.getParaValue("id");
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				sql_v = dao.getOracle_nmsoratopsql(serverip);
				sql_readwrite_v = dao.getOracle_nmsoratopsql_readwrite(serverip);
				sql_sort_v = dao.getOracle_nmsoratopsql_sort(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("sysvalue", sysValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());

			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}
			maxhash = new Hashtable();
			maxhash.put("avgpingcon", pingconavg);
			avgpingcon = new Double(pingconavg + "").doubleValue();

			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("imgurl", imgurlhash);
		request.setAttribute("max", maxhash);
		request.setAttribute("chart1", chart1);
		request.setAttribute("sql_v", sql_v);
		request.setAttribute("sql_readwrite_v", sql_readwrite_v);
		request.setAttribute("sql_sort_v", sql_sort_v);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oracletopsql.jsp";
	}

	private String oracleuser() {
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		DBVo vo = new DBVo();
		Hashtable sysValue = new Hashtable();
		Hashtable userinfo_h = new Hashtable();
		String pingconavg = "0";
		String chart1 = null;
		String sid = getParaValue("id");
		double avgpingcon = 0;
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				userinfo_h = dao.getOracle_nmsorauserinfo(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("chart1", chart1);
		request.setAttribute("userinfo_h", userinfo_h);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oracleuser.jsp";
	}

	private String oraclewait() {
		Hashtable cursors = new Hashtable();
		Hashtable isArchive_h = new Hashtable();
		String lstrnStatu = "";
		DBVo vo = new DBVo();
		Hashtable sysValue = new Hashtable();
		Vector waitv = new Vector();
		String pingconavg = "0";
		String chart1 = null;
		String sid = getParaValue("id");
		double avgpingcon = 0;
		try {
			DBDao dao = new DBDao();
			try {
				String id = getParaValue("id");
				request.setAttribute("id", id);
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			DBTypeVo typevo = null;
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				typedao.close();
			}

			request.setAttribute("db", vo);
			request.setAttribute("dbtye", typevo.getDbdesc());
			String managed = "被管理";
			if (vo.getManaged() == 0) {
				managed = "未管理";
			}
			request.setAttribute("managed", managed);
			String runstr = "服务停止";
			// 2010-HONGLI
			Hashtable memPerfValue = new Hashtable();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			String status = "0";
			try {
				dao = new DBDao();
				Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
				memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
				sysValue = dao.getOracle_nmsorasys(serverip);
				status = String.valueOf(statusHashtable.get("status"));
				lstrnStatu = String.valueOf(statusHashtable.get("lstrnstatu"));
				isArchive_h = dao.getOracle_nmsoraisarchive(serverip);
				waitv = dao.getOracle_nmsorawait(serverip);
				cursors = dao.getOracle_nmsoracursors(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			if ("1".equals(status)) {
				runstr = "正在运行";
			}
			request.setAttribute("lstrnStatu", lstrnStatu);
			request.setAttribute("isArchive_h", isArchive_h);
			request.setAttribute("cursors", cursors);
			request.setAttribute("memPerfValue", memPerfValue);
			request.setAttribute("runstr", runstr);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String time1 = sdf.format(new Date());
			String starttime1 = time1 + " 00:00:00";
			String totime1 = time1 + " 23:59:59";

			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime1, totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
			}

			// ping平均值
			avgpingcon = new Double(pingconavg + "").doubleValue();
			DefaultPieDataset dpd = new DefaultPieDataset();
			dpd.setValue("可用率", avgpingcon);
			dpd.setValue("不可用率", 100 - avgpingcon);
			chart1 = ChartCreator.createPieChart(dpd, "", 130, 130);
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("sid", sid);
		request.setAttribute("chart1", chart1);
		request.setAttribute("waitv", waitv);
		request.setAttribute("sysvalue", sysValue);
		request.setAttribute("avgpingcon", avgpingcon);
		return "/application/db/oraclewait.jsp";
	}

	// jhl add 事件展现
	private String oraEventReport() {
		String id = request.getParameter("id");
		request.setAttribute("id", id);

		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
			request.setAttribute("startdate", startdate);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
			request.setAttribute("todate", todate);
		}
		Sdbevent(startdate, todate, id);
		return "/capreport/db/showOraEventReport.jsp";
	}

	private void p_draw_line(Hashtable hash, String title1, String title2, int w, int h) {
		List list = (List) hash.get("list");
		try {
			if (list == null || list.size() == 0) {
				draw_blank(title1, title2, w, h);
			} else {
				String unit = (String) hash.get("unit");
				if (unit == null) {
					unit = "%";
				}
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
					Minute minute = new Minute(temp.get(Calendar.MINUTE), temp.get(Calendar.HOUR_OF_DAY), temp.get(Calendar.DAY_OF_MONTH), temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				cg.timewave(s, "x(时间)", "y(" + unit + ")", title1, title2, w, h);

			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void Sdbevent(String str1, String str2, String id) {
		String startdate = str1;
		String todate = str2;
		String ids = id;
		String ip = null;
		String tyname = null;
		int pingvalue = 0;
		String dbname = null;
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		// 按排序标志取各端口最新记录的列表
		DBTypeDao typedao = new DBTypeDao();
		DBDao dao = new DBDao();
		DBVo vo = null;
		DBTypeVo typevo = null;
		try {
			vo = (DBVo) dao.findByID(ids);
			typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			tyname = typevo.getDbtype();
			ip = vo.getIpAddress();
			dbname = vo.getDbName();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
			typedao.close();
		}
		EventListDao eventdao = new EventListDao();
		// 得到事件列表
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
		s.append(" and nodeid=" + vo.getId());
		List infolist = eventdao.findByCriteria(s.toString());
		if (infolist != null && infolist.size() > 0) {
			for (int j = 0; j < infolist.size(); j++) {
				EventList eventlist = (EventList) infolist.get(j);
				if (eventlist.getContent() == null) {
					eventlist.setContent("");
				}
				String content = eventlist.getContent();
				if (content.indexOf("数据库服务停止") > 0) {
					pingvalue = pingvalue + 1;
				}
			}
		}
		session.setAttribute("_tyname", tyname);
		session.setAttribute("_ip", ip);
		session.setAttribute("_dbname", dbname);
		session.setAttribute("_pingvalue", pingvalue);
	}

	private String sychronizeData() {
		DBRefreshHelper dbRefreshHelper = new DBRefreshHelper();

		String dbvoId = request.getParameter("id");
		String dbPage = request.getParameter("dbPage");
		DBDao dbDao = new DBDao();
		DBVo dbVo = (DBVo) dbDao.findByID(dbvoId);
		dbRefreshHelper.execute(dbVo);
		if (dbPage.equals("oracleping")) {
			return oracleping();
		}
		if (dbPage.equals("oraclelock")) {
			return oraclelock();
		}
		if (dbPage.equals("oraclesession")) {
			return oraclesession();
		}
		if (dbPage.equals("oraclerollback")) {
			return oraclerollback();
		}
		if (dbPage.equals("oracletable")) {
			return oracletable();
		}
		if (dbPage.equals("oraclespace")) {
			return oraclespace();
		}
		if (dbPage.equals("oracletopsql")) {
			return oracletopsql();
		}
		if (dbPage.equals("oracleuser")) {
			return oracleuser();
		}
		if (dbPage.equals("oraclewait")) {
			return oraclewait();
		}
		if (dbPage.equals("oracleevent")) {
			return oracleevent();
		}
		return "/application/db/oracleping";
	}

	private String update() {
		DBVo vo = new DBVo();
		vo.setId(getParaIntValue("id"));
		vo.setUser(getParaValue("user"));
		vo.setPassword(getParaValue("password"));
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		String allbid = "";
		String sid = getParaValue("sid");
		String[] businessids = getParaArrayValue("checkbox");
		if (businessids != null && businessids.length > 0) {
			for (int i = 0; i < businessids.length; i++) {

				String bid = businessids[i];
				allbid = allbid + bid + ",";
			}
		}
		vo.setBid(allbid);
		vo.setManaged(getParaIntValue("managed"));
		vo.setDbtype(getParaIntValue("dbtype"));
		OracleEntity oracle = new OracleEntity();
		oracle.setAlias(vo.getAlias());
		oracle.setCollectType(vo.getCollecttype());
		oracle.setDbid(vo.getId());
		oracle.setGzerid(vo.getSendemail());
		oracle.setId(Integer.parseInt(sid));
		oracle.setManaged(vo.getManaged());
		oracle.setPassword(vo.getPassword());
		oracle.setSid(vo.getDbName());
		oracle.setUser(vo.getUser());
		oracle.setBid(vo.getBid());
		OraclePartsDao oraDao = new OraclePartsDao();
		try {
			oraDao.update(oracle);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			oraDao.close();
		}
		return "/db.do?action=list";
	}
}