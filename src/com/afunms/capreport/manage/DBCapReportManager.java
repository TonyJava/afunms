package com.afunms.capreport.manage;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.SybaseVO;
import com.afunms.application.util.IpTranslation;
import com.afunms.application.util.ReportExport;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.model.ReportValue;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.DateE;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.RtfWriter2;

@SuppressWarnings("unchecked")
public class DBCapReportManager extends BaseManager implements ManagerInterface {

	DateE datemanager = new DateE();

	SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

	I_HostCollectData hostmanager = new HostCollectDataManager();

	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String list() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		// 装入数据库列表
		DBDao dao = new DBDao();
		List list = dao.loadAll();
		request.setAttribute("list", list);
		return "/capreport/db/list.jsp";
	}

	/**
	 * @date 2011-4-19
	 * @author wxy add
	 * @数据库报表，按照业务查询
	 * @return
	 */
	private String find() {

		int dbflag = 0;
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		String bid = getParaValue("bidtext");
		dbflag = getParaIntValue("dbflag");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		if (bid != null && !bid.equals("")) {
			DBDao dao = new DBDao();
			request.setAttribute("list", dao.getDbNodeByBID(dbflag, bid));
		} else {
			request.setAttribute("list", new ArrayList());
		}
		if (dbflag == 0) {
			return "/capreport/db/list.jsp";
		} else if (dbflag == 1) {
			return "/capreport/db/oraclelist.jsp";
		} else if (dbflag == 2) {
			return "/capreport/db/sqlserverlist.jsp";
		} else if (dbflag == 4) {
			return "/capreport/db/mysqllist.jsp";
		} else if (dbflag == 5) {
			return "/capreport/db/db2list.jsp";
		} else if (dbflag == 6) {
			return "/capreport/db/sybaselist.jsp";
		} else if (dbflag == 7) {
			return "/capreport/db/informixlist.jsp";
		} else if (dbflag == 10) {
			return "/capreport/db/eventlist.jsp";
		} else {
			return "/capreport/db/list.jsp";
		}

	}

	private String dbReport() {

		DBDao dao = new DBDao();
		List list = dao.loadAll();
		request.setAttribute("list", list);
		return "/capreport/db/dbReport.jsp";
	}

	private String eventlist() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

		DBDao dao = new DBDao();
		List list = dao.loadAll();
		request.setAttribute("list", list);
		return "/capreport/db/eventlist.jsp";
	}

	// oracle 列表
	private String oraclelist() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

		DBDao dao = new DBDao();
		List list = dao.getOracleList();
		request.setAttribute("list", list);
		return "/capreport/db/oraclelist.jsp";
	}

	// db2列表
	private String db2list() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

		DBDao dao = new DBDao();
		List list = dao.getDB2List();
		request.setAttribute("list", list);
		return "/capreport/db/db2list.jsp";
	}

	// sqlserver列表
	private String sqlserverlist() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

		DBDao dao = new DBDao();
		List list = dao.getSqlserverList();

		request.setAttribute("list", list);
		return "/capreport/db/sqlserverlist.jsp";
	}

	// sybase列表
	private String sybaselist() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

		DBDao dao = new DBDao();
		List list = dao.getSybaseList();

		request.setAttribute("list", list);
		return "/capreport/db/sybaselist.jsp";
	}

	//	
	private String informixlist() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

		DBDao dao = new DBDao();
		List list = dao.getInformixList();

		request.setAttribute("list", list);
		return "/capreport/db/informixlist.jsp";
	}

	private String mysqllist() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);

		DBDao dao = new DBDao();
		List list = dao.getMySQLList();

		request.setAttribute("list", list);
		return "/capreport/db/mysqllist.jsp";
	}

	private String dbping() {
		Date d = new Date();
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

		String[] ids = getParaArrayValue("checkbox");
		StringBuffer idsStr = new StringBuffer();
		if (ids == null) {
			String str = String.valueOf(getParaValue("ids"));
			if (str != null && !"".equals(str)) {
				ids = str.split(",");
			}
		}
		for (int i = 0; i < ids.length; i++) {
			String temp = "";
			if (i != ids.length - 1) {
				temp = ids[i] + ',';
			} else {
				temp = ids[i];
			}
			idsStr.append(temp);
		}

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null && ids.length > 0 && !ids[0].equals("null")) {
			for (int i = 0; i < ids.length; i++) {
				DBDao dao = new DBDao();
				DBVo vo = null;
				try {
					vo = (DBVo) dao.findByID(ids[i]);
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					dao.close();
				}
				DBTypeDao typedao = new DBTypeDao();
				DBTypeVo typevo = null;
				try {
					typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					typedao.close();
				}

				Hashtable pinghash = new Hashtable();
				String id = vo.getId() + "";
				try {
					if (typevo.getDbtype().equalsIgnoreCase("oracle")) {
						pinghash = hostmanager.getCategory(id, "ORAPing", "ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
						pinghash = hostmanager.getCategory(id, "SQLPing", "ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
						pinghash = hostmanager.getCategory(id, "DB2Ping", "ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {
						pinghash = hostmanager.getCategory(id, "SYSPing", "ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
						pinghash = hostmanager.getCategory(id, "INFORMIXPing", "ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {// HONGLI
						pinghash = hostmanager.getCategory(id, "MYPing", "ConnectUtilization", starttime, totime);
					}
				} catch (Exception e) {
					SysLogger.error("", e);
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("dbvo", vo);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("ipaddress", vo.getIpAddress() + "(" + vo.getAlias() + ")");
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping") || orderflag.equalsIgnoreCase("downnum")) {
			returnList = (List) session.getAttribute("pinglist");
		} else {
			List pinglist = orderList;
			if (pinglist != null && pinglist.size() > 0) {
				for (int i = 0; i < pinglist.size(); i++) {
					Hashtable _pinghash = (Hashtable) pinglist.get(i);
					DBVo vo = (DBVo) _pinghash.get("dbvo");
					Hashtable pinghash = (Hashtable) _pinghash.get("pinghash");
					if (pinghash == null)
						continue;
					DBTypeDao typedao = new DBTypeDao();
					DBTypeVo typevo = null;
					try {
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						typedao.close();
					}
					String equname = vo.getAlias();
					String ip = vo.getIpAddress();
					String dbuse = vo.getDbuse();

					String pingconavg = "";
					String downnum = "";
					if (pinghash.get("avgpingcon") != null)
						pingconavg = (String) pinghash.get("avgpingcon");
					if (pinghash.get("downnum") != null)
						downnum = (String) pinghash.get("downnum");
					List ipdiskList = new ArrayList();
					ipdiskList.add(ip);
					ipdiskList.add(typevo.getDbtype());
					ipdiskList.add(equname);
					ipdiskList.add(dbuse);
					ipdiskList.add(pingconavg);
					ipdiskList.add(downnum);
					returnList.add(ipdiskList);
				}
			}
		}
		List list = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipdiskList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipdiskList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("avgping")) {
						String avgping = "";
						if (ipdiskList.get(4) != null) {
							avgping = (String) ipdiskList.get(4);
						}
						String _avgping = "";
						if (ipdiskList.get(4) != null) {
							_avgping = (String) _ipdiskList.get(4);
						}
						if (new Double(avgping.substring(0, avgping.length() - 2)).doubleValue() < new Double(_avgping.substring(0, _avgping.length() - 2)).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if (orderflag.equalsIgnoreCase("downnum")) {
						String downnum = "";
						if (ipdiskList.get(5) != null) {
							downnum = (String) ipdiskList.get(5);
						}
						String _downnum = "";
						if (ipdiskList.get(5) != null) {
							_downnum = (String) _ipdiskList.get(5);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// 得到排序后的Subentity的列表
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}
		//
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList, "ping");
		String pingpath = new ReportExport().makeJfreeChartData(pingReportValue.getListValue(), pingReportValue.getIpList(), "连通率", "时间", "");

		request.setAttribute("pingChartDivStr", pingChartDivStr);
		session.setAttribute("pingpath", pingpath);
		request.setAttribute("ids", idsStr.toString());
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		session.setAttribute("pinglist", list);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("pinglist", list);
		return "/capreport/db/dbping.jsp";
	}

	private String dbevent() {
		Date d = new Date();
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

		String[] ids = getParaArrayValue("checkbox");
		StringBuffer idsStr = new StringBuffer();
		if (ids == null) {
			String str = String.valueOf(getParaValue("ids"));
			if (str != null && !"".equals(str)) {
				ids = str.split(",");
			}
		}
		for (int i = 0; i < ids.length; i++) {
			String temp = "";
			if (i != ids.length - 1) {
				temp = ids[i] + ',';
			} else {
				temp = ids[i];
			}
			idsStr.append(temp);
		}

		// 按排序标志取各端口最新记录的列表
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null && !getParaValue("orderflag").equals("")) {
			orderflag = getParaValue("orderflag");
		}

		List orderList = new ArrayList();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				DBDao dao = new DBDao();
				DBVo vo = null;
				try {
					vo = (DBVo) dao.findByID(ids[i]);
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					dao.close();
				}
				if (vo == null)
					continue;
				DBTypeDao typeDao = new DBTypeDao();
				DBTypeVo typeVo = null;
				try {
					typeVo = (DBTypeVo) typeDao.findByID(vo.getDbtype() + "");
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					typeDao.close();
				}
				EventListDao eventdao = new EventListDao();
				// 得到事件列表
				int nodeid = vo.getId();
				if (typeVo != null && "oracle".equalsIgnoreCase(typeVo.getDbtype())) {
					OraclePartsDao oraclePartsDao = new OraclePartsDao();
					try {
						nodeid = oraclePartsDao.getOracleSidById(nodeid);
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						oraclePartsDao.close();
					}
				}
				StringBuffer s = new StringBuffer();
				s.append("select * from system_eventlist where recordtime>= '" + starttime + "' " + "and recordtime<='" + totime + "' ");
				s.append(" and nodeid=" + nodeid);
				List infolist = eventdao.findByCriteria(s.toString());

				int pingvalue = 0;
				int level1 = 0;// 普通事件
				int level2 = 0;// 严重事件
				int level3 = 0;// 紧急事件

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (content.indexOf("数据库服务停止") > 0) {
						pingvalue = pingvalue + 1;
					}
					int level = eventlist.getLevel1();
					if (level == 1) {
						level1++;
					} else if (level == 2) {
						level2++;
					} else {
						level3++;
					}
				}
				DBTypeDao typedao = new DBTypeDao();
				DBTypeVo typevo = null;
				try {
					typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					typedao.close();
				}
				String equname = vo.getAlias();
				String ip = vo.getIpAddress();
				String dbuse = vo.getDbuse();
				List ipeventList = new ArrayList();
				ipeventList.add(ip);
				ipeventList.add(typevo.getDbtype());
				ipeventList.add(equname);
				ipeventList.add(dbuse);
				ipeventList.add(pingvalue + "");
				ipeventList.add(level1);// index --5
				ipeventList.add(level2);// index --6
				ipeventList.add(level3);// index --7
				ipeventList.add(infolist.size());// index --8 事件总数
				orderList.add(ipeventList);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("ping") || orderflag.equalsIgnoreCase("total") || orderflag.equalsIgnoreCase("level1") || orderflag.equalsIgnoreCase("level2")
				|| orderflag.equalsIgnoreCase("level3")) {
			returnList = (List) session.getAttribute("eventlist");
		} else {
			returnList = orderList;
		}

		List list = new ArrayList();
		if (returnList != null && returnList.size() > 0) {
			for (int m = 0; m < returnList.size(); m++) {
				List ipdiskList = (List) returnList.get(m);
				for (int n = m + 1; n < returnList.size(); n++) {
					List _ipdiskList = (List) returnList.get(n);
					if (orderflag.equalsIgnoreCase("ipaddress")) {
					} else if (orderflag.equalsIgnoreCase("ping")) {
						String downnum = "";
						if (ipdiskList.get(4) != null) {
							downnum = (String) ipdiskList.get(4);
						}
						String _downnum = "";
						if (ipdiskList.get(4) != null) {
							_downnum = (String) _ipdiskList.get(4);
						}
						if (new Double(downnum).doubleValue() < new Double(_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if ("total".equalsIgnoreCase(orderflag)) {
						int total = -1;
						int _total = -1;
						if (ipdiskList.get(8) != null) {
							total = (Integer) ipdiskList.get(8);
						}
						if (_ipdiskList.get(8) != null) {
							_total = (Integer) _ipdiskList.get(8);
						}
						if (total < _total) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if ("level1".equalsIgnoreCase(orderflag)) {
						int level1 = -1;
						int _level1 = -1;
						if (ipdiskList.get(5) != null) {
							level1 = (Integer) ipdiskList.get(5);
						}
						if (_ipdiskList.get(5) != null) {
							_level1 = (Integer) _ipdiskList.get(5);
						}
						if (level1 < _level1) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if ("level2".equalsIgnoreCase(orderflag)) {
						int level2 = -1;
						int _level2 = -1;
						if (ipdiskList.get(6) != null) {
							level2 = (Integer) ipdiskList.get(6);
						}
						if (_ipdiskList.get(6) != null) {
							_level2 = (Integer) _ipdiskList.get(6);
						}
						if (level2 < _level2) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					} else if ("level3".equalsIgnoreCase(orderflag)) {
						int level3 = -1;
						int _level3 = -1;
						if (ipdiskList.get(7) != null) {
							level3 = (Integer) ipdiskList.get(7);
						}
						if (_ipdiskList.get(7) != null) {
							_level3 = (Integer) _ipdiskList.get(7);
						}
						if (level3 < _level3) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// 得到排序后的Subentity的列表
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}

		request.setAttribute("ids", idsStr.toString());
		request.setAttribute("starttime", starttime);
		request.setAttribute("totime", totime);
		request.setAttribute("startdate", startdate);
		request.setAttribute("todate", todate);
		request.setAttribute("eventlist", list);
		session.setAttribute("eventlist", list);
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		return "/capreport/db/dbevent.jsp";
	}

	private String downloaddbpingreport() {
		Date d = new Date();
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
		List memlist = (List) session.getAttribute("pinglist");// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Hashtable reporthash = new Hashtable();
		reporthash.put("pinglist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_dbping("/temp/dbping_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/db/download.jsp";
	}

	// jhl add SQLServer event report pdf
	private String ceateSServerEventPdf() {
		String ip = request.getParameter("ipaddress");
		String typevo = request.getParameter("typevo");
		String pingvalue = request.getParameter("pingvalue") + "";
		String dbname = request.getParameter("dbname");
		String file = "/temp/dbevent.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createSServerEventPdfDownLoad(fileName, ip, typevo, pingvalue, dbname);
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}
		request.setAttribute("filename", fileName);
		return "/capreport/db/download.jsp";
	}

	public void createSServerEventPdfDownLoad(String file, String _ip, String _typevo, String _pingvalue, String _dbname) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// 标题字体风格
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("数据库事件报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);
		// 设置 Table 表格
		document.add(new Paragraph("\n"));
		PdfPTable aTable = new PdfPTable(6);
		int width[] = { 30, 70, 50, 70, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidthPercentage(100);

		aTable.addCell(new Phrase(""));
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", contextFont));
		PdfPCell cell11 = new PdfPCell(new Phrase("数据库类型", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("数据库名称", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("数据库应用", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("服务器不可用次数", contextFont));

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);

		String ip = _ip;
		String dbtype = _typevo;
		String equname = _typevo;
		String dbuse = "afunms";
		String downnum = _pingvalue;

		PdfPCell cell15 = new PdfPCell(new Phrase(1 + ""));
		PdfPCell cell6 = new PdfPCell(new Phrase(ip));
		PdfPCell cell7 = new PdfPCell(new Phrase(dbtype));
		PdfPCell cell8 = new PdfPCell(new Phrase(equname, contextFont));
		PdfPCell cell9 = new PdfPCell(new Phrase(dbuse));
		PdfPCell cell10 = new PdfPCell(new Phrase(downnum));

		aTable.addCell(cell15);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		aTable.addCell(cell9);
		aTable.addCell(cell10);

		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	private String createSQLServerEventWord() {
		String ip = request.getParameter("ipaddress");
		String typevo = request.getParameter("typevo");
		String pingvalue = request.getParameter("pingvalue") + "";
		String dbname = request.getParameter("dbname");
		String file = "/temp/dbevent.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createSQLServerEventDownload(fileName, ip, typevo, pingvalue, dbname);
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}
		request.setAttribute("filename", fileName);
		return "/capreport/db/download.jsp";
	}

	private void createSQLServerEventDownload(String file, String _ip, String _typevo, String _pingvalue, String _dbname) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		// 标题字体风格
		Paragraph title = new Paragraph("数据库事件报表");
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);
		// 设置 Table 表格
		Table aTable = new Table(6);
		int width[] = { 50, 50, 50, 70, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();

		aTable.addCell(new Cell(""));
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("数据库类型");
		Cell cell2 = new Cell("数据库名称");
		Cell cell3 = new Cell("数据库应用");
		Cell cell15 = new Cell("服务器不可用次数");
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell15);

		String ip = _ip;
		String dbtype = _typevo;
		String equname = _typevo;
		String dbuse = "afunms";
		String downnum = _pingvalue;

		Cell cell5 = new Cell(1 + "");
		Cell cell6 = new Cell(ip);
		Cell cell7 = new Cell(dbtype);
		Cell cell8 = new Cell(equname);
		Cell cell9 = new Cell(dbuse);
		Cell cell10 = new Cell(downnum);
		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		aTable.addCell(cell9);
		aTable.addCell(cell10);

		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// SqlServer event excel
	private String downloadSQLServerEventReport() {
		String ip = request.getParameter("ipaddress");
		String typevo = request.getParameter("typevo");
		String pingvalue = request.getParameter("pingvalue") + "";
		String dbname = request.getParameter("dbname");
		Date d = new Date();
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
		List memlist = new ArrayList();
		memlist.add(0, ip);
		memlist.add(1, typevo);
		memlist.add(2, pingvalue);
		memlist.add(3, dbname);
		Hashtable reporthash = new Hashtable();
		reporthash.put("eventlist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_SQLServerevent("/temp/dbevent_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/db/download.jsp";
	}

	private String createOraEventPdf() {
		String file = "/temp/dbevent.pdf";// 保存到项目文件夹下的指定文件夹

		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		String ipaddress = (String) request.getParameter("ipaddress");
		String typevo = (String) request.getParameter("typevo");
		String dbname = (String) request.getParameter("dbname");
		int p = (Integer) session.getAttribute("_pingvalue");

		String startdate = getParaValue("startdate");
		Date d = new Date();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		Hashtable dbData = new Hashtable();
		dbData.put("fileName", fileName);
		dbData.put("ipaddress", ipaddress);
		dbData.put("typevo", typevo);
		dbData.put("dbname", dbname);
		dbData.put("_pingvalue", p);
		dbData.put("starttime", starttime);
		dbData.put("totime", totime);
		try {
			createOraEventDownloadPdf(dbData);// HONGLI MODIFY
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}
		request.setAttribute("filename", fileName);
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI ADD 2010-11-2
	 * @param dbDate
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void createOraEventDownloadPdf(Hashtable dbData) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream((String) dbData.get("fileName")));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("数据库事件报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String contextString = "报表生成时间:" + sdf.format(new Date()) + " \n"// 换行
				+ "数据统计时间段:" + dbData.get("starttime") + " 至 " + dbData.get("totime");

		Paragraph context = new Paragraph(contextString, contextFont);
		// 正文格式左对齐
		context.setAlignment(Element.ALIGN_LEFT);
		// context.setFont(contextFont);
		// 离上一段落（标题）空的行数
		context.setSpacingBefore(5);
		// 设置第一行空的列数
		context.setFirstLineIndent(5);
		document.add(context);
		document.add(new Paragraph("\n"));
		// 设置 Table 表格
		document.add(new Paragraph("\n"));
		PdfPTable aTable = new PdfPTable(6);
		int width[] = { 30, 70, 50, 70, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidthPercentage(100);

		aTable.addCell(new Phrase(""));
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", contextFont));
		PdfPCell cell11 = new PdfPCell(new Phrase("数据库类型", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("数据库名称", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("数据库应用", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("服务器不可用次数", contextFont));

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		String _pvalue = (Integer) dbData.get("_pingvalue") + "";
		PdfPCell cell15 = new PdfPCell(new Phrase(1 + ""));
		PdfPCell cell6 = new PdfPCell(new Phrase((String) dbData.get("ipaddress")));
		PdfPCell cell7 = new PdfPCell(new Phrase((String) dbData.get("typevo")));
		PdfPCell cell8 = new PdfPCell(new Phrase((String) dbData.get("dbname"), contextFont));
		PdfPCell cell9 = new PdfPCell(new Phrase((String) dbData.get("dbname")));
		PdfPCell cell10 = new PdfPCell(new Phrase(_pvalue));

		aTable.addCell(cell15);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		aTable.addCell(cell9);
		aTable.addCell(cell10);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}


	// jhl end oracle event report pdf

	// jhl add oracle event report excel
	private String downlooraeventreport() {
		String ipaddress = (String) request.getParameter("ipaddress");
		String typevo = (String) request.getParameter("typevo");
		String dbname = (String) request.getParameter("dbname");
		int p = (Integer) session.getAttribute("_pingvalue");
		Date d = new Date();
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

		List memlist = new ArrayList();
		memlist.add(0, ipaddress);
		memlist.add(1, typevo);
		memlist.add(2, dbname);
		memlist.add(3, p);
		Hashtable reporthash = new Hashtable();
		reporthash.put("eventlist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReportOra_event("/temp/dbevent_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/db/download.jsp";
	}

	private String downloaddbeventreport() {
		Date d = new Date();
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

		List memlist = (List) session.getAttribute("eventlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("eventlist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_dbevent("/temp/dbevent_report.xls");
		request.setAttribute("filename", report.getFileName());
		return "/capreport/db/download.jsp";
		// return mapping.findForward("report_info");
	}

	// ora==========================================================
	private String downloadoraselfreport() {
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
		Vector tableinfo_v = new Vector();
		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		DBVo vo = null;
		String sid = "";
		String pingnow = "0.0";// HONGLI ADD 当前连通率
		String pingmin = "0.0";// HONGLI ADD 最小连通率
		String pingconavg = "0.0";// HONGLI ADD 平均连通率
		List eventList = new ArrayList();// 事件列表
		try {
			ip = getParaValue("ipaddress");
			String[] ips = ip.split(":");
			ip = ips[0];
			sid = ips[1];

			dao = new DBDao();
			vo = (DBVo) dao.findByCondition("ip_address", ip, 1).get(0);

			OraclePartsDao oracledao = new OraclePartsDao();
			List sidlist = new ArrayList();
			try {
				sidlist = oracledao.findOracleParts(vo.getId());
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				oracledao.close();
			}
			if (sidlist != null) {
				for (int j = 0; j < sidlist.size(); j++) {
					OracleEntity ora = (OracleEntity) sidlist.get(j);
					sid = ora.getId() + "";
					break;
					// ips.add(dbmonitorlist.getIpAddress() + ":" +
					// ora.getSid());
				}
			}

			dbname = vo.getDbName() + "(" + ip + ")";
			dbnamestr = vo.getDbName();
			String newip = doip(ip);
			// 从内存中取出sga等信息

			dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
			String statusStr = String.valueOf(statusHashtable.get("status"));
			memValue = dao.getOracle_nmsoramemvalue(serverip);
			if ("1".equals(statusStr)) {
				runstr = "正在运行";
				pingnow = "100.0";// HONGLI ADD
			}
			Hashtable pinghash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime, totime);
			p_draw_line(pinghash, "", newip + "ConnectUtilization", 740, 120);
			if (pinghash.get("avgpingcon") != null)
				pingconavg = (String) pinghash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (pinghash.get("max") != null) {
				ConnectUtilizationmax = (String) pinghash.get("max");
			}
			if (pinghash.get("avgpingcon") != null) {
				pingconavg = (String) pinghash.get("avgpingcon");
				pingmin = (String) pinghash.get("pingmax");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");
				pingmin = pingmin.replace("%", "");
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
		// 把ping得到的数据加进去
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
			pinghash = hostmanager.getCategory(vo.getIpAddress() + ":" + sid, "ORAPing", "ConnectUtilization", starttime, totime);
			if (pinghash.get("downnum") != null)
				downnum = (String) pinghash.get("downnum");
		} catch (Exception e1) {
			SysLogger.error("", e1);
		}
		DBTypeDao dbTypeDao = new DBTypeDao();
		int count = 0;
		try {
			count = dbTypeDao.finddbcountbyip(ip);
		} catch (Exception e) {
			SysLogger.error("", e);
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
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("memvalue", memValue);
		reporthash.put("typename", typename);
		reporthash.put("runstr", runstr);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count + "");
		reporthash.put("grade", grade);
		reporthash.put("pingnow", pingnow);// HONGLI ADD
		reporthash.put("pingmin", pingmin);// HONGLI ADD
		reporthash.put("pingconavg", pingconavg);// HONGLI ADD
		reporthash.put("vo", vo);// HONGLI ADD
		reporthash.put("ip", ip);
		Hashtable cursors = new Hashtable();
		Hashtable dbio = new Hashtable();
		Hashtable memPerfValue = new Hashtable();
		dao = new DBDao();
		String hex = IpTranslation.formIpToHex(vo.getIpAddress());
		String serverip = hex + ":" + sid;
		Hashtable statusHashtable = new Hashtable();// 取状态信息
		try {
			statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
			memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
			String statusStr = String.valueOf(statusHashtable.get("status"));
			memValue = dao.getOracle_nmsoramemvalue(serverip);
			dbio = dao.getOracle_nmsoradbio(serverip);
			tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
			cursors = dao.getOracle_nmsoracursors(serverip);
			if ("1".equals(statusStr)) {
				runstr = "正在运行";
				pingnow = "100.0";// HONGLI ADD
			}
			dao.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// HONGLI ADD START2
		// 去除单位MB\KB
		String[] sysItem = { "shared_pool", "large_pool", "DEFAULT_buffer_cache", "java_pool", "aggregate_PGA_target_parameter", "total_PGA_allocated", "maximum_PGA_allocated" };
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
		reporthash.put("dbio", dbio);
		// Hashtable memPerfValue = new Hashtable();
		// if(iporacledata.containsKey("memPerfValue"))
		// {
		// memPerfValue=(Hashtable)iporacledata.get("memPerfValue");
		// }

		// 事件列表
		int status = getParaIntValue("status");
		int level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		request.setAttribute("status", status);
		request.setAttribute("level1", level1);
		try {
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
			// SysLogger.info("user businessid===="+vo.getBusinessids());
			EventListDao eventdao = new EventListDao();
			try {
				eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), Integer.parseInt(sid));
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				eventdao.close();
			}
			// ConnectUtilizationhash =
			// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		reporthash.put("tableinfo_v", tableinfo_v);
		reporthash.put("list", eventList);
		reporthash.put("vo", vo);
		reporthash.put("memPerfValue", memPerfValue);
		reporthash.put("cursors", cursors);
		reporthash.put("memValue", memValue);
		// HONGLI ADD END2
		// HONGLI END########
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
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_oraPDF(fileName);// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_oraNewPDF(fileName, "doc");// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_oraNewPDF(fileName, "pdf");// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("5".equals(str)) {// HONGLI ADD START2
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_cld_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReportOracleCldPdf(fileName, "doc");// oracle的综合性能报表word格式打印

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("6".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_cld_report.xls";// 保存到项目文件夹下的指定文件夹

				report1.createReportOracleCldExcel(file);// oracle的综合性能报表excel格式打印

				request.setAttribute("filename", report1.getFileName());
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("7".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbora_cld_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReportOracleCldPdf(fileName, "pdf");// oracle的综合性能报表PDF格式打印
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} // HONGLI ADD END2
		return "/capreport/db/download.jsp";
	}

	private String downloaddb2selfreport() {
		Date d = new Date();
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
		String typename = "DB2";
		String hostnamestr = "";
		String runstr = "有数据库服务停止";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable sysValue = new Hashtable();
		Hashtable returnhash = new Hashtable();
		String downnum = "0";
		Hashtable vector = new Hashtable();
		DBVo vo = null;
		try {
			ip = getParaValue("ipaddress");
			DBDao dao = new DBDao();
			vo = (DBVo) dao.findByCondition("ip_address", ip, 5).get(0);
			dbname = vo.getDbName() + "(" + ip + ")";
			hostnamestr = vo.getDbName();
			String newip = doip(ip);
			Hashtable pinghash = hostmanager.getCategory(vo.getId() + "", "DB2Ping", "ConnectUtilization", starttime, totime);
			p_draw_line(pinghash, "", newip + "ConnectUtilization", 740, 120);
			String pingconavg = "";
			if (pinghash.get("avgpingcon") != null)
				pingconavg = (String) pinghash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (pinghash.get("max") != null) {
				ConnectUtilizationmax = (String) pinghash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			// p_draw_line(cpuhash,"",newip+"cpu",750,150);
			// draw_column(diskhash,"",newip+"disk",750,150);
			// p_drawchartMultiLine(memoryhash[0],"",newip+"memory",750,150);
			// 运行状态
			String[] dbs = vo.getDbName().split(",");
			int allFlag = 0;
			for (int k = 0; k < dbs.length; k++) {
				boolean db2IsOK = false;
				dao = new DBDao();
				try {
					db2IsOK = dao.getDB2IsOK(vo.getIpAddress(), Integer.parseInt(vo.getPort()), vo.getDbName(), vo.getUser(), EncryptUtil.decode(vo.getPassword()));
				} catch (Exception e) {
					SysLogger.error("", e);
					db2IsOK = false;
				} finally {
					dao.close();
				}
				if (!db2IsOK) {
					allFlag = 1;
				}
			}
			if (allFlag == 0) {
				runstr = "正在运行";
			}
			// end运行状态
			// jingcheng message
			dao = new DBDao();
			try {
				sysValue = dao.getDB2Sys(vo.getIpAddress(), Integer.parseInt(vo.getPort()), vo.getDbName(), vo.getUser(), EncryptUtil.decode(vo.getPassword()));
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}

			Hashtable pinghash1 = new Hashtable();
			try {
				pinghash1 = hostmanager.getCategory(vo.getIpAddress(), "DB2Ping", "ConnectUtilization", starttime, totime);
				if (pinghash1.get("downnum") != null)
					downnum = (String) pinghash.get("downnum");
			} catch (Exception e1) {

				e1.printStackTrace();
			}
			// ========end downnum
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		// request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("memmaxhash", memmaxhash);
		request.setAttribute("memavghash", memavghash);
		request.setAttribute("diskhash", diskhash);
		request.setAttribute("memhash", memhash);

		Hashtable reporthash = new Hashtable();

		Vector pdata = (Vector) pingdata.get(ip);
		// Vector pdata = (Vector) ShareData.getOraspacedata();
		// 把ping得到的数据加进去
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

		String username = vo.getUser();
		String userpw = vo.getPassword();
		String servername = vo.getDbName();
		int serverport = Integer.parseInt(vo.getPort());
		DBDao dao = null;
		try {
			dao = new DBDao();
			vector = dao.getDB2Space(ip, serverport, servername, username, userpw);

		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			dao.close();
		}
		dao = new DBDao();
		try {
			returnhash = dao.getDB2Space(vo.getIpAddress(), Integer.parseInt(vo.getPort()), vo.getDbName(), vo.getUser(), EncryptUtil.decode(vo.getPassword()));
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			dao.close();
		}
		// 表空间==========告警
		DBTypeDao dbTypeDao = new DBTypeDao();
		int count = 0;
		try {
			count = dbTypeDao.finddbcountbyip(ip);
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			dbTypeDao.close();
		}
		// 数据库运行等级=====================
		String grade = "优";
		if (count > 0) {
			grade = "良";
		}

		if (!"0".equals(downnum)) {
			grade = "差";
		}
		reporthash.put("servername", servername);
		reporthash.put("dbname", dbname);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("typename", typename);
		reporthash.put("dbnamestr", hostnamestr);
		reporthash.put("runstr", runstr);
		reporthash.put("sqlsys", sysValue);
		reporthash.put("returnhash", returnhash);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count + "");
		reporthash.put("grade", grade);
		// reporthash.put("memmaxhash",memmaxhash);
		// reporthash.put("memavghash",memavghash);
		reporthash.put("ip", ip);
		if (vector == null)
			vector = new Hashtable();
		reporthash.put("tableinfo_v", vector);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			report.createReport_db2("/temp/dbdb2_report.xls");
			request.setAttribute("filename", report.getFileName());
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbdb2_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_db2Doc(fileName);// word综合报表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbdb2_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_db2PDF(fileName);// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbdb2_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_db2NewDoc(fileName, "doc");// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbdb2_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_db2NewDoc(fileName, "pdf");// word业务分析表
				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		}
		return "/capreport/db/download.jsp";
	}

	// private String downloadsqlselfreport()
	// {
	// Date d = new Date();
	// String startdate = getParaValue("startdate");
	// if(startdate==null){
	// startdate = sdf0.format(d);
	// }
	// String todate = getParaValue("todate");
	// if(todate==null){
	// todate = sdf0.format(d);
	// }
	// String starttime = startdate + " 00:00:00";
	// String totime = todate + " 23:59:59";
	// Hashtable allcpuhash = new Hashtable();
	// String ip = "";
	// String dbname = "";
	//		
	// Hashtable hash = new Hashtable();//"Cpu",--current
	// Hashtable memhash = new Hashtable();//mem--current
	// Hashtable diskhash = new Hashtable();
	// Hashtable memmaxhash = new Hashtable();//mem--max
	// Hashtable memavghash = new Hashtable();//mem--avg
	// Hashtable maxhash = new Hashtable();//"Cpu"--max
	// Hashtable maxping = new Hashtable();//Ping--max
	// Hashtable pingdata = ShareData.getPingdata();
	// // Hashtable sharedata = ShareData.getSharedata();
	// Hashtable vector = new Hashtable();
	// DBVo vo = null;
	// try {
	// ip=getParaValue("ipaddress");
	// DBDao dao = new DBDao();
	// vo = (DBVo)dao.findByCondition("ip_address", ip, 2).get(0);
	// dbname = vo.getDbName()+"("+ip+")";
	// String remoteip=request.getRemoteAddr();
	// String newip=doip(ip);
	// Hashtable pinghash =
	// hostmanager.getCategory(ip,"SQLPing","ConnectUtilization",starttime,totime);
	// //Hashtable ConnectUtilizationhash =
	// hostmanager.getCategory(ip,"Ping","ConnectUtilization",starttime,totime);
	// p_draw_line(pinghash,"",newip+"ConnectUtilization",740,120);
	// String pingconavg ="";
	// if (pinghash.get("avgpingcon")!=null)
	// pingconavg = (String)pinghash.get("avgpingcon");
	// String ConnectUtilizationmax = "";
	// maxping.put("avgpingcon",pingconavg);
	// if(pinghash.get("max")!=null){
	// ConnectUtilizationmax = (String)pinghash.get("max");
	// }
	// maxping.put("pingmax",ConnectUtilizationmax);
	//			
	// //p_draw_line(cpuhash,"",newip+"cpu",750,150);
	// //draw_column(diskhash,"",newip+"disk",750,150);
	// //p_drawchartMultiLine(memoryhash[0],"",newip+"memory",750,150);
	//			
	//			
	// }
	// catch (Exception e) {
	// SysLogger.error("",e);
	// }
	// //request.setAttribute("imgurl",imgurlhash);
	// request.setAttribute("hash",hash);
	// request.setAttribute("max",maxhash);
	// request.setAttribute("memmaxhash",memmaxhash);
	// request.setAttribute("memavghash",memavghash);
	// request.setAttribute("diskhash",diskhash);
	// request.setAttribute("memhash",memhash);
	//		
	//		
	// Hashtable reporthash = new Hashtable();
	//	   
	// Vector pdata = (Vector)pingdata.get(ip);
	// // Vector pdata = (Vector) ShareData.getOraspacedata();
	// //把ping得到的数据加进去
	// if (pdata != null && pdata.size()>0){
	// for(int m=0;m<pdata.size();m++){
	// Pingcollectdata hostdata = (Pingcollectdata)pdata.get(m);
	// if(hostdata.getSubentity().equals("ConnectUtilization")){
	// reporthash.put("time",hostdata.getCollecttime());
	// reporthash.put("Ping",hostdata.getThevalue());
	// reporthash.put("ping", maxping);
	// }
	// }
	// }else{
	// reporthash.put("ping", maxping);
	// }
	//		
	// String username = vo.getUser();
	// String userpw = vo.getPassword();
	// String servername = vo.getDbName();
	// int serverport = Integer.parseInt(vo.getPort());
	//		
	// try {
	// DBDao dao = new DBDao();
	// vector = dao.getSqlserverDB(ip,username,userpw);
	// dao.close();
	// } catch (Exception e) {
	// SysLogger.error("",e);
	// }
	//		
	// reporthash.put("dbname", dbname);
	// reporthash.put("starttime", starttime);
	// reporthash.put("totime", totime);
	//
	// //reporthash.put("memmaxhash",memmaxhash);
	// //reporthash.put("memavghash",memavghash);
	// reporthash.put("ip",ip);
	// if(vector == null)vector = new Hashtable();
	// reporthash.put("tableinfo_v", vector);
	//	   
	// AbstractionReport1 report = new ExcelReport1(new
	// IpResourceReport(),reporthash);
	// report.createReport_sql("/temp/dbora_report.xls");
	// request.setAttribute("filename", report.getFileName());
	// return "/capreport/db/download.jsp";
	// }

	// private String downloadsybaseselfreport()
	// {
	// Date d = new Date();
	// String startdate = getParaValue("startdate");
	// if(startdate==null){
	// startdate = sdf0.format(d);
	// }
	// String todate = getParaValue("todate");
	// if(todate==null){
	// todate = sdf0.format(d);
	// }
	// String starttime = startdate + " 00:00:00";
	// String totime = todate + " 23:59:59";
	// Hashtable allcpuhash = new Hashtable();
	// String ip = "";
	// String dbname = "";
	//		
	// Hashtable hash = new Hashtable();//"Cpu",--current
	// Hashtable memhash = new Hashtable();//mem--current
	// Hashtable diskhash = new Hashtable();
	// Hashtable memmaxhash = new Hashtable();//mem--max
	// Hashtable memavghash = new Hashtable();//mem--avg
	// Hashtable maxhash = new Hashtable();//"Cpu"--max
	// Hashtable maxping = new Hashtable();//Ping--max
	// Hashtable pingdata = ShareData.getPingdata();
	// // Hashtable sharedata = ShareData.getSharedata();
	// Vector vector = new Vector();
	// DBVo vo = null;
	// try {
	// ip=getParaValue("ipaddress");
	// DBDao dao = new DBDao();
	// vo = (DBVo)dao.findByCondition("ip_address", ip, 6).get(0);
	// dbname = vo.getDbName()+"("+ip+")";
	// String remoteip=request.getRemoteAddr();
	// String newip=doip(ip);
	// Hashtable pinghash =
	// hostmanager.getCategory(ip,"ORAPing","ConnectUtilization",starttime,totime);
	// //Hashtable ConnectUtilizationhash =
	// hostmanager.getCategory(ip,"Ping","ConnectUtilization",starttime,totime);
	// p_draw_line(pinghash,"",newip+"ConnectUtilization",740,120);
	// String pingconavg ="";
	// if (pinghash.get("avgpingcon")!=null)
	// pingconavg = (String)pinghash.get("avgpingcon");
	// String ConnectUtilizationmax = "";
	// maxping.put("avgpingcon",pingconavg);
	// if(pinghash.get("max")!=null){
	// ConnectUtilizationmax = (String)pinghash.get("max");
	// }
	// maxping.put("pingmax",ConnectUtilizationmax);
	//			
	// //p_draw_line(cpuhash,"",newip+"cpu",750,150);
	// //draw_column(diskhash,"",newip+"disk",750,150);
	// //p_drawchartMultiLine(memoryhash[0],"",newip+"memory",750,150);
	//			
	//			
	// }
	// catch (Exception e) {
	// SysLogger.error("",e);
	// }
	// //request.setAttribute("imgurl",imgurlhash);
	// request.setAttribute("hash",hash);
	// request.setAttribute("max",maxhash);
	// request.setAttribute("memmaxhash",memmaxhash);
	// request.setAttribute("memavghash",memavghash);
	// request.setAttribute("diskhash",diskhash);
	// request.setAttribute("memhash",memhash);
	//		
	//		
	// Hashtable reporthash = new Hashtable();
	// SybaseVO sysbaseVO = new SybaseVO();
	// Hashtable sysValue = new Hashtable();
	// Hashtable sValue = new Hashtable();
	//		
	//	   
	// Vector pdata = (Vector)pingdata.get(ip);
	// //把ping得到的数据加进去
	// if (pdata != null && pdata.size()>0){
	// for(int m=0;m<pdata.size();m++){
	// Pingcollectdata hostdata = (Pingcollectdata)pdata.get(m);
	// if(hostdata.getSubentity().equals("ConnectUtilization")){
	// reporthash.put("time",hostdata.getCollecttime());
	// reporthash.put("Ping",hostdata.getThevalue());
	// reporthash.put("ping", maxping);
	// }
	// }
	// }else{
	// reporthash.put("ping", maxping);
	// }
	//		
	//		
	// String username = vo.getUser();
	// String userpw = vo.getPassword();
	// String servername = vo.getDbName();
	// int serverport = Integer.parseInt(vo.getPort());
	//		
	// sysValue = ShareData.getSysbasedata();
	// if(sysValue.get(ip) != null)
	// sValue = (Hashtable)sysValue.get(ip);
	// if(sValue.get("sysbaseVO") != null)
	// sysbaseVO = (SybaseVO)sValue.get("sysbaseVO");
	//		
	// reporthash.put("dbname", dbname);
	// reporthash.put("starttime", starttime);
	// reporthash.put("totime", totime);
	//
	// //reporthash.put("memmaxhash",memmaxhash);
	// //reporthash.put("memavghash",memavghash);
	// reporthash.put("ip",ip);
	// if(vector == null)vector = new Vector();
	// reporthash.put("sysbaseVO", sysbaseVO);
	//	   
	// AbstractionReport1 report = new ExcelReport1(new
	// IpResourceReport(),reporthash);
	// report.createReport_syb("/temp/dbora_report.xls");
	// request.setAttribute("filename", report.getFileName());
	// return "/capreport/db/download.jsp";
	// }

	// ====================sql===========================
	private String downloadsqlselfreport() {
		Date d = new Date();
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
		String typename = "SQL SERVER";
		String runstr = "服务停止";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		DBDao dao = null;
		Vector process_v = new Vector();
		Hashtable dbValue = new Hashtable();
		String hostnamestr = "";
		DBVo vo = null;
		try {
			ip = getParaValue("ipaddress");
			dao = new DBDao();
			try {
				vo = (DBVo) dao.findByCondition("ip_address", ip, 2).get(0);
			} catch (Exception e) {

			} finally {
				dao.close();
			}
			dbname = vo.getDbName() + "(" + ip + ")";
			hostnamestr = vo.getDbName();
			String newip = doip(ip);
			Hashtable pinghash = hostmanager.getCategory(vo.getId() + "", "SQLPing", "ConnectUtilization", starttime, totime);
			p_draw_line(pinghash, "", newip + "ConnectUtilization", 740, 120);
			String pingconavg = "";
			if (pinghash.get("avgpingcon") != null)
				pingconavg = (String) pinghash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (pinghash.get("max") != null) {
				ConnectUtilizationmax = (String) pinghash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);


			Hashtable reporthash = new Hashtable();

			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去
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


			// 连通率事件次数
			String downnum = "0";
			Hashtable pinghash1 = new Hashtable();
			try {
				pinghash1 = hostmanager.getCategory(vo.getIpAddress(), "SQLPing", "ConnectUtilization", starttime, totime);
				if (pinghash1.get("downnum") != null)
					downnum = (String) pinghash1.get("downnum");
			} catch (Exception e1) {

				e1.printStackTrace();
			}

			DBTypeDao dbTypeDao = new DBTypeDao();
			int count = 0;
			try {
				count = dbTypeDao.finddbcountbyip(ip);
			} catch (Exception e) {
				SysLogger.error("", e);
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
			reporthash.put("downnum", downnum);
			reporthash.put("dbname", dbname);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			reporthash.put("typename", typename);

			reporthash.put("hostnamestr", hostnamestr);

			reporthash.put("count", count + "");
			reporthash.put("grade", grade);
			reporthash.put("ip", ip);

			// end mem
			DBDao dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getAlias();
			Hashtable sysValue = new Hashtable();
			Hashtable statusHash = dbDao.getSqlserver_nmsstatus(serverip);
			Hashtable pages = dbDao.getSqlserver_nmspages(serverip);
			Hashtable mems = dbDao.getSqlserver_nmsmems(serverip);
			sysValue = dbDao.getSqlserver_nmssysvalue(serverip);
			process_v = dao.getSqlserver_nmsinfo_v(serverip);
			dbValue = dao.getSqlserver_nmsdbvalue(serverip);
			Hashtable detailValue = new Hashtable();
			detailValue.put("pages", pages);
			detailValue.put("mems", mems);
			reporthash.put("tableinfo_v", dbValue);
			reporthash.put("dbValue", dbValue);
			reporthash.put("sqlsys", sysValue);
			reporthash.put("tableinfo_v", process_v);
			reporthash.put("sqlValue", detailValue);
			String p_status = (String) statusHash.get("status");
			if (p_status != null && p_status.length() > 0) {
				if ("1".equalsIgnoreCase(p_status)) {
					runstr = "正在运行";
				}
			}
			dao.close();
			reporthash.put("runstr", runstr);
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				report.createReport_sql("/temp/dbsql_report.xls");
				request.setAttribute("filename", report.getFileName());
				SysLogger.info("filename" + report.getFileName());
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_sqlDoc(fileName);// word综合报表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					SysLogger.error("", e);
				} catch (IOException e) {

					SysLogger.error("", e);
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_sqlPDF(fileName);// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					SysLogger.error("", e);
				} catch (IOException e) {

					SysLogger.error("", e);
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_sqlNewDoc(fileName, "doc");// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					SysLogger.error("", e);
				} catch (IOException e) {

					SysLogger.error("", e);
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_sqlNewDoc(fileName, "pdf");// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					SysLogger.error("", e);
				} catch (IOException e) {

					SysLogger.error("", e);
				}
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	// =====================sby=============================
	private String downloadsybaseselfreport() {
		Date d = new Date();
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
		String typename = "SYBASE";
		String hostnamestr = "";
		String downnum = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		// Hashtable sharedata = ShareData.getSharedata();
		Vector vector = new Vector();
		DBVo vo = null;
		try {
			ip = getParaValue("ipaddress");
			DBDao dao = new DBDao();
			vo = (DBVo) dao.findByCondition("ip_address", ip, 6).get(0);
			dbname = vo.getDbName() + "(" + ip + ")";
			hostnamestr = vo.getDbName();
			String newip = doip(ip);
			Hashtable pinghash = hostmanager.getCategory(vo.getId() + "", "SYSPing", "ConnectUtilization", starttime, totime);
			p_draw_line(pinghash, "", newip + "ConnectUtilization", 740, 120);
			String pingconavg = "";
			if (pinghash.get("avgpingcon") != null)
				pingconavg = (String) pinghash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (pinghash.get("max") != null) {
				ConnectUtilizationmax = (String) pinghash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

			request.setAttribute("hash", hash);
			request.setAttribute("max", maxhash);
			request.setAttribute("memmaxhash", memmaxhash);
			request.setAttribute("memavghash", memavghash);
			request.setAttribute("diskhash", diskhash);
			request.setAttribute("memhash", memhash);

			Hashtable reporthash = new Hashtable();
			SybaseVO sysbaseVO = new SybaseVO();

			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去
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


			String runstr = "服务停止";
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex + ":" + vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip);
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
				status = (String) tempStatusHashtable.get("status");
			}
			if (status.equals("1")) {
				runstr = "正在运行";
			}
			if (dao != null) {
				dao.close();
			}
			Hashtable pinghash1 = new Hashtable();
			try {
				pinghash1 = hostmanager.getCategory(vo.getIpAddress(), "SQLPing", "ConnectUtilization", starttime, totime);
				if (pinghash1.get("downnum") != null)
					downnum = (String) pinghash1.get("downnum");
			} catch (Exception e1) {

				e1.printStackTrace();
			}
			// 表空间事件
			DBTypeDao dbTypeDao = new DBTypeDao();
			int count = 0;
			try {
				count = dbTypeDao.finddbcountbyip(ip);
			} catch (Exception e) {
				SysLogger.error("", e);
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
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			reporthash.put("sysbaseVO", sysbaseVO);
			reporthash.put("runstr", runstr);
			reporthash.put("typename", typename);
			reporthash.put("downnum", downnum);
			reporthash.put("hostnamestr", hostnamestr);
			reporthash.put("count", count + "");
			reporthash.put("grade", grade);
			// reporthash.put("memmaxhash",memmaxhash);
			// reporthash.put("memavghash",memavghash);
			reporthash.put("ip", ip);
			if (vector == null)
				vector = new Vector();
			reporthash.put("sysbaseVO", sysbaseVO);

			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			report.createReport_syb("/temp/dbora_report.xls");
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				report.createReport_syb("/temp/dbsybase_report.xls");
				request.setAttribute("filename", report.getFileName());
				SysLogger.info("filename" + report.getFileName());
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_sybDoc(fileName);// word综合报表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					SysLogger.error("", e);
				} catch (IOException e) {

					SysLogger.error("", e);
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_sybPDF(fileName);// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					SysLogger.error("", e);
				} catch (IOException e) {

					SysLogger.error("", e);
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsyb_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_sybaseNewDoc(fileName);// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					SysLogger.error("", e);
				} catch (IOException e) {

					SysLogger.error("", e);
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsyb_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_sybaseNewPDF(fileName);// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {

					SysLogger.error("", e);
				} catch (IOException e) {

					SysLogger.error("", e);
				}
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	/*
	 * mysql运行分析报表
	 */
	public String downloadmysqlselfreport() {
		Date d = new Date();
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
		String downnum = "";

		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String grade = "优";
		int count = 0;
		Vector Val = new Vector();
		int doneFlag = 0;
		List sessionlist = new ArrayList();
		Hashtable tablesHash = new Hashtable();
		Vector tableinfo_v = new Vector();
		List eventList = new ArrayList();// 事件列表
		try {
			ip = getParaValue("ipaddress");
			DBDao dao = new DBDao();
			vo = (DBVo) dao.findByCondition("ip_address", ip, 4).get(0);
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String newip = doip(ip);
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			Hashtable ipData = dao.getMysqlDataByServerip(serverip);
			if (dao != null) {
				dao.close();
			}
			if (ipData != null && ipData.size() > 0) {
				String dbnames = vo.getDbName();
				String[] dbs = dbnames.split(",");
				for (int k = 0; k < dbs.length; k++) {
					// 判断是否已经获取了当前的配置信息
					// if(doneFlag == 1)break;
					String dbStr = dbs[k];
					if (ipData.containsKey(dbStr)) {
						Hashtable returnValue = new Hashtable();
						returnValue = (Hashtable) ipData.get(dbStr);
						if (returnValue != null && returnValue.size() > 0) {
							if (doneFlag == 0) {
								// 判断是否已经获取了当前的配置信息
								if (returnValue.containsKey("configVal")) {
									doneFlag = 1;
								}
								if (returnValue.containsKey("Val")) {
									Val = (Vector) returnValue.get("Val");
								}
							}
							if (returnValue.containsKey("sessionsDetail")) {
								// 存在数据库连接信息
								sessionlist.add((List) returnValue.get("sessionsDetail"));
							}
							if (returnValue.containsKey("tablesDetail")) {
								// 存在数据库表信息
								tablesHash.put(dbStr, (List) returnValue.get("tablesDetail"));
							}
							if (returnValue.containsKey("global_status")) {
								// 存在数据库表信息
								tableinfo_v = (Vector) returnValue.get("global_status");
							}
						}
					}
				}

				runstr = (String) ipData.get("runningflag");
				if (runstr != null && runstr.contains("服务停止")) {// 将<font
					// color=red>服务停止</font>
					// 替换
					runstr = "服务停止";
				}
				if (runstr != null && runstr.contains("正在运行")) {
					pingnow = "100";
				}
			}
			// }
			// request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "MYPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "0";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			if (count > 0) {
				grade = "良";
			}
			if (!"0".equals(downnum)) {
				grade = "差";
			}
			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Hashtable reporthash = new Hashtable();
			Hashtable maxping = new Hashtable();
			maxping.put("pingmax", pingmin + "%");// 最小连通率
			maxping.put("pingnow", pingnow + "%");
			maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
			reporthash.put("list", eventList);
			reporthash.put("pingmin", pingmin);
			reporthash.put("pingnow", pingnow);
			reporthash.put("pingmax", pingmax);
			reporthash.put("pingconavg", avgpingcon + "");
			reporthash.put("tablesHash", tablesHash);
			reporthash.put("sessionlist", sessionlist);
			reporthash.put("Val", Val);
			reporthash.put("downnum", downnum);
			reporthash.put("count", count + "");
			reporthash.put("grade", grade);
			reporthash.put("vo", vo);
			reporthash.put("runstr", runstr);
			reporthash.put("typevo", typevo);
			reporthash.put("dbValue", dbValue);
			reporthash.put("typename", typevo.getDbtype());
			reporthash.put("hostnamestr", vo.getDbName());
			reporthash.put("tableinfo_v", tableinfo_v);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			reporthash.put("ping", maxping);
			reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
			reporthash.put("ip", vo.getIpAddress());

			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbmysql_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_mysqlNewDoc(fileName, "doc");// word业务分析表
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbmysql_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_mysqlNewDoc(fileName, "pdf");// word业务分析表
					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	private String downloadmultiorareport() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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

		Hashtable pingdata = ShareData.getPingdata();
		DBVo vo = null;
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {

					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "ORAPing", "ConnectUtilization", starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					Hashtable maxping = new Hashtable();// Ping--max
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					Hashtable reporthash = new Hashtable();

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					} else {
						reporthash.put("ping", maxping);
					}
					// 数据库空间
					Vector tableinfo_v = new Vector();
					try {
						DBDao dao1 = new DBDao();
						tableinfo_v = dao1.getOracleTableinfo(ip, Integer.parseInt(vo.getPort()), vo.getDbName(), vo.getUser(), EncryptUtil.decode(vo.getPassword()));
						dao1.close();
					} catch (Exception e) {
						SysLogger.error("", e);
					}

					reporthash.put("tableinfo_v", tableinfo_v);
					reporthash.put("dbname", dbname);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					allreporthash.put(ip, reporthash);
				}
				AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_oraall("/temp/oraall_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * 
	 * @return
	 */
	private String downloadmultiorareport2() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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
		String sid = "";
		Hashtable maxping = new Hashtable();// Ping--max
		DBVo vo = null;
		String pingnow = "0.0";// HONGLI ADD 当前连通率
		String pingmin = "0.0";// HONGLI ADD 最小连通率
		String pingconavg = "0.0";// HONGLI ADD 平均连通率
		List eventList = new ArrayList();// 事件列表
		Hashtable memValue = new Hashtable();
		Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		Hashtable cursors = new Hashtable();
		String runstr = "服务停止";
		String dbnamestr = "";
		String typename = "ORACLE";
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {

					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";
					dbnamestr = vo.getDbName();
					OraclePartsDao oracledao = new OraclePartsDao();
					List sidlist = new ArrayList();
					try {
						sidlist = oracledao.findOracleParts(vo.getId());
					} catch (Exception e) {
						SysLogger.error("", e);
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

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(sid, "ORAPing", "ConnectUtilization", starttime, totime);
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					}
					if (ConnectUtilizationhash.get("pingmax") != null) {
						pingmin = (String) ConnectUtilizationhash.get("pingmax");
					}
					String ConnectUtilizationmax = "";
					// Hashtable maxping = new Hashtable();//Ping--max
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					Hashtable reporthash = new Hashtable();
					Hashtable memPerfValue = new Hashtable();
					dao = new DBDao();
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					String serverip = hex + ":" + sid;
					Hashtable statusHashtable = new Hashtable();// 取状态信息
					try {
						statusHashtable = dao.getOracle_nmsorastatus(serverip);// 取状态信息
						memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
						String statusStr = String.valueOf(statusHashtable.get("status"));
						memValue = dao.getOracle_nmsoramemvalue(serverip);
						dbio = dao.getOracle_nmsoradbio(serverip);
						tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
						cursors = dao.getOracle_nmsoracursors(serverip);
						if ("1".equals(statusStr)) {
							runstr = "正在运行";
							pingnow = "100.0";// HONGLI ADD
						}
						dao.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					// 去除单位MB\KB
					String[] sysItem = { "shared_pool", "large_pool", "DEFAULT_buffer_cache", "java_pool", "aggregate_PGA_target_parameter", "total_PGA_allocated",
							"maximum_PGA_allocated" };
					DecimalFormat df = new DecimalFormat("#.##");
					if (memValue != null) {
						for (int j = 0; j < sysItem.length; j++) {
							String value = "";
							if (memValue.get(sysItem[j]) != null) {
								value = (String) memValue.get(sysItem[j]);
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
							memValue.put(sysItem[j], df.format(Double.parseDouble(value)));
						}
					}
					request.setAttribute("memValue", memValue);
					reporthash.put("dbio", dbio);

					// 事件列表
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					request.setAttribute("status", status);
					request.setAttribute("level1", level1);
					try {
						User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
						// SysLogger.info("user
						// businessid===="+vo.getBusinessids());
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), Integer.parseInt(sid));
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
						// ConnectUtilizationhash =
						// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					// 求oracle宕机次数
					String downnum = "0";
					Hashtable pinghash = new Hashtable();
					try {
						pinghash = hostmanager.getCategory(vo.getIpAddress() + ":" + sid, "ORAPing", "ConnectUtilization", starttime, totime);
						if (pinghash.get("downnum") != null)
							downnum = (String) pinghash.get("downnum");
					} catch (Exception e1) {

						e1.printStackTrace();
					}
					// ========end downnum
					// 表空间==========告警
					DBTypeDao dbTypeDao = new DBTypeDao();
					int count = 0;
					try {
						count = dbTypeDao.finddbcountbyip(ip);
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}
					// 数据库运行等级=====================
					String grade = "优";
					if (count > 0) {
						grade = "良";
					}

					if (!"0".equals(downnum)) {
						grade = "差";
					}

					reporthash.put("tableinfo_v", tableinfo_v);
					reporthash.put("list", eventList);
					reporthash.put("vo", vo);
					reporthash.put("memPerfValue", memPerfValue);
					reporthash.put("cursors", cursors);
					reporthash.put("memValue", memValue);

					reporthash.put("tableinfo_v", tableinfo_v);
					reporthash.put("dbname", dbname);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);

					reporthash.put("dbnamestr", dbnamestr);
					reporthash.put("starttime", starttime);
					reporthash.put("typename", typename);
					reporthash.put("runstr", runstr);
					reporthash.put("downnum", downnum);
					reporthash.put("count", count + "");
					reporthash.put("grade", grade);
					reporthash.put("pingnow", pingnow);// HONGLI ADD
					reporthash.put("pingmin", pingmin);// HONGLI ADD
					reporthash.put("pingconavg", pingconavg);// HONGLI ADD

					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_oraall2("/temp/oraall_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	private String downloadmultidb2report() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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
		Hashtable pingdata = ShareData.getPingdata();
		DBVo vo = null;
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";
					String newip = doip(ip);

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "DB2Ping", "ConnectUtilization", starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					Hashtable maxping = new Hashtable();// Ping--max
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

					Hashtable reporthash = new Hashtable();
					Hashtable returnhash = new Hashtable();

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					} else {
						reporthash.put("ping", maxping);
					}
					// 数据库空间
					DBDao dao1 = null;
					try {
						dao1 = new DBDao();
						returnhash = dao1.getDB2Space(ip, Integer.parseInt(vo.getPort()), vo.getDbName(), vo.getUser(), EncryptUtil.decode(vo.getPassword()));
						dao1.close();
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao1.close();
					}
					reporthash.put("db2space", returnhash);
					reporthash.put("dbname", dbname);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					allreporthash.put(ip, reporthash);
				}
				AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_db2all("/temp/db2all_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	private String downloadmultidb2report2() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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

		Hashtable pingdata = ShareData.getPingdata();
		DBVo vo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		String grade = "优";
		Hashtable mems = new Hashtable();// 内存信息
		Hashtable sysValue = new Hashtable();
		Hashtable spaceInfo = new Hashtable();
		Hashtable conn = new Hashtable();
		Hashtable poolInfo = new Hashtable();
		Hashtable log = new Hashtable();
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		DBTypeVo typevo = null;
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					DBTypeDao typedao = new DBTypeDao();
					try {
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						typedao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";
					String newip = doip(ip);

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "DB2Ping", "ConnectUtilization", starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					Hashtable maxping = new Hashtable();// Ping--max
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

					Hashtable reporthash = new Hashtable();
					Hashtable returnhash = new Hashtable();

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					} else {
						reporthash.put("ping", maxping);
					}
					// 数据库空间
					DBDao dao1 = null;
					try {
						dao1 = new DBDao();
						returnhash = dao1.getDB2Space(ip, Integer.parseInt(vo.getPort()), vo.getDbName(), vo.getUser(), EncryptUtil.decode(vo.getPassword()));
						dao1.close();
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao1.close();
					}
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					String sip = hex + ":" + vo.getId();
					Hashtable monitorValue = dao.getDB2DataByServerip(sip);
					Hashtable alldb2data = (Hashtable) monitorValue.get("allDb2Data");
					Hashtable ipdb2data = new Hashtable();
					if (alldb2data != null && alldb2data.size() > 0) {
						if (alldb2data.containsKey(vo.getIpAddress())) {
							ipdb2data = (Hashtable) alldb2data.get(vo.getIpAddress());
							if (ipdb2data.containsKey("status")) {
								String p_status = (String) ipdb2data.get("status");
								if (p_status != null && p_status.length() > 0) {
									if ("1".equalsIgnoreCase(p_status)) {
										runstr = "正在运行";
										pingnow = "100.0";
									}
								}
							}
							if (ipdb2data.containsKey("dbValue")) {
								dbValue = (Hashtable) ipdb2data.get("dbValue");
							}
							if (ipdb2data.containsKey("retValue")) {
								mems = (Hashtable) ((Hashtable) ipdb2data.get("retValue")).get("mems");
							}
							if (ipdb2data.containsKey("sysValue")) {
								sysValue = (Hashtable) ipdb2data.get("sysValue");
							}
							if (ipdb2data.containsKey("spaceInfo")) {
								spaceInfo = (Hashtable) ipdb2data.get("spaceInfo");
							}
							if (ipdb2data.containsKey("conn")) {
								conn = (Hashtable) ipdb2data.get("conn");
							}
							if (ipdb2data.containsKey("poolInfo")) {
								poolInfo = (Hashtable) ipdb2data.get("poolInfo");
							}
							if (ipdb2data.containsKey("log")) {
								log = (Hashtable) ipdb2data.get("log");
							}
						}
					}
					// request.setAttribute("newip", newip);
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					}
					if (pingconavg != null) {
						pingconavg = pingconavg.replace("%", "");// 平均连通率
					}
					if (ConnectUtilizationhash.get("downnum") != null) {
						downnum = (String) ConnectUtilizationhash.get("downnum");
					}
					if (ConnectUtilizationhash.get("pingMax") != null) {
						pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
					}
					if (ConnectUtilizationhash.get("pingmax") != null) {
						pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
					}
					avgpingcon = new Double(pingconavg + "").doubleValue();

					p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

					// 得到运行等级
					DBTypeDao dbTypeDao = new DBTypeDao();

					try {
						count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}

					if (count > 0) {
						grade = "良";
					}
					if (!"0".equals(downnum)) {
						grade = "差";
					}

					// 事件列表
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					// request.setAttribute("status", status);
					// request.setAttribute("level1", level1);
					try {
						User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
						// SysLogger.info("user
						// businessid===="+vo.getBusinessids());
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
						// ConnectUtilizationhash =
						// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					reporthash.put("list", eventList);
					maxping.put("pingmax", pingmin + "%");// 最小连通率
					maxping.put("pingnow", pingnow + "%");
					maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
					reporthash.put("pingmin", pingmin);
					reporthash.put("pingnow", pingnow);
					reporthash.put("pingmax", pingmax);
					reporthash.put("pingconavg", avgpingcon + "");
					reporthash.put("sysValue", sysValue);
					reporthash.put("spaceInfo", spaceInfo);
					reporthash.put("conn", conn);
					reporthash.put("poolInfo", poolInfo);
					reporthash.put("log", log);
					reporthash.put("sqlsys", sysValue);
					reporthash.put("mems", mems);
					reporthash.put("downnum", downnum);
					reporthash.put("count", count);
					reporthash.put("grade", grade);
					reporthash.put("vo", vo);
					reporthash.put("runstr", runstr);
					reporthash.put("typevo", typevo);
					reporthash.put("dbValue", dbValue);
					reporthash.put("typename", typevo.getDbtype());
					reporthash.put("hostnamestr", vo.getDbName());
					reporthash.put("tableinfo_v", dbValue);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					reporthash.put("ping", maxping);
					reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
					reporthash.put("ip", vo.getIpAddress());

					reporthash.put("db2space", returnhash);
					reporthash.put("dbname", dbname);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_db2all2("/temp/db2all_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	private String downloadmultisqlreport() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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
		Hashtable pingdata = ShareData.getPingdata();
		DBVo vo = null;
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SQLPing", "ConnectUtilization", starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					Hashtable maxping = new Hashtable();// Ping--max
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					Hashtable reporthash = new Hashtable();
					Hashtable dbValue = new Hashtable();

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					} else {
						reporthash.put("ping", maxping);
					}
					// 数据库空间
					DBDao dao1 = null;
					try {
						dao1 = new DBDao();
						dbValue = dao1.getSqlserverDB(ip, vo.getUser(), EncryptUtil.decode(vo.getPassword()));
						// dao1.close();
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao1.close();
					}
					if (dbValue == null)
						dbValue = new Hashtable();
					reporthash.put("dbValue", dbValue);
					reporthash.put("dbname", dbname);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					allreporthash.put(ip, reporthash);
				}
				AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_sqlall("/temp/hostnms_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	private String downloadmultisqlreport2() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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
		DBVo vo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		String grade = "优";
		Hashtable mems = new Hashtable();// 内存信息
		Hashtable sysValue = new Hashtable();
		int count = 0;
		DBTypeVo typevo = null;
		List eventList = new ArrayList();// 事件列表
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					ip = vo.getIpAddress();
					String newip = doip(ip);
					DBTypeDao typedao = new DBTypeDao();
					try {
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						typedao.close();
					}
					DBDao dbDao = new DBDao();
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					String serverip = hex + ":" + vo.getAlias();
					Hashtable statusHash = dbDao.getSqlserver_nmsstatus(serverip);
					sysValue = dbDao.getSqlserver_nmssysvalue(serverip);
					dbValue = dbDao.getSqlserver_nmsdbvalue(serverip);
					mems = dbDao.getSqlserver_nmsmems(serverip);
					sysValue = dbDao.getSqlserver_nmssysvalue(serverip);
					String p_status = (String) statusHash.get("status");
					if (p_status != null && p_status.length() > 0) {
						if ("1".equalsIgnoreCase(p_status)) {
							runstr = "正在运行";
							pingnow = "100.0";
						}
					}
					dbDao.close();
					// request.setAttribute("newip", newip);
					Hashtable ConnectUtilizationhash = new Hashtable();
					I_HostCollectData hostmanager = new HostCollectDataManager();
					try {
						ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SQLPing", "ConnectUtilization", starttime, totime);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					}
					if (pingconavg != null) {
						pingconavg = pingconavg.replace("%", "");// 平均连通率
					}
					if (ConnectUtilizationhash.get("downnum") != null) {
						downnum = (String) ConnectUtilizationhash.get("downnum");
					}
					pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
					pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最小连通率
					avgpingcon = new Double(pingconavg + "").doubleValue();

					p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

					// 得到运行等级
					DBTypeDao dbTypeDao = new DBTypeDao();

					try {
						count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}

					if (count > 0) {
						grade = "良";
					}
					if (!"0".equals(downnum)) {
						grade = "差";
					}

					// 事件列表
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					// request.setAttribute("status", status);
					// request.setAttribute("level1", level1);
					try {
						User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
						// SysLogger.info("user
						// businessid===="+vo.getBusinessids());
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
						// ConnectUtilizationhash =
						// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					Hashtable maxping = new Hashtable();
					maxping.put("pingmax", pingmin + "%");// 最小连通率
					maxping.put("pingnow", pingnow + "%");
					maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
					Hashtable reporthash = new Hashtable();
					reporthash.put("list", eventList);
					reporthash.put("pingmin", pingmin);
					reporthash.put("pingnow", pingnow);
					reporthash.put("pingmax", pingmax);
					reporthash.put("pingconavg", avgpingcon + "");
					reporthash.put("sysValue", sysValue);
					reporthash.put("sqlsys", sysValue);
					reporthash.put("mems", mems);
					reporthash.put("downnum", downnum);
					reporthash.put("count", count);
					reporthash.put("grade", grade);
					reporthash.put("vo", vo);
					reporthash.put("runstr", runstr);
					reporthash.put("typevo", typevo);
					reporthash.put("dbValue", dbValue);
					reporthash.put("typename", typevo.getDbtype());
					reporthash.put("hostnamestr", vo.getDbName());
					reporthash.put("tableinfo_v", dbValue);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					reporthash.put("ping", maxping);
					reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
					reporthash.put("ip", vo.getIpAddress());

					if (dbValue == null)
						dbValue = new Hashtable();
					reporthash.put("dbValue", dbValue);
					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_sqlall2("/temp/hostnms_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	private String downloadmultisybasereport() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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


		Hashtable pingdata = ShareData.getPingdata();
		DBVo vo = null;
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";

					Hashtable ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SYSPing", "ConnectUtilization", starttime, totime);
					String pingconavg = "";
					if (ConnectUtilizationhash.get("avgpingcon") != null)
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					String ConnectUtilizationmax = "";
					Hashtable maxping = new Hashtable();// Ping--max
					maxping.put("avgpingcon", pingconavg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);

					Hashtable reporthash = new Hashtable();

					Vector pdata = (Vector) pingdata.get(ip);
					// 把ping得到的数据加进去
					if (pdata != null && pdata.size() > 0) {
						for (int m = 0; m < pdata.size(); m++) {
							PingCollectEntity hostdata = (PingCollectEntity) pdata.get(m);
							if (hostdata != null) {
								if (hostdata.getSubentity() != null) {
									if (hostdata.getSubentity().equals("ConnectUtilization")) {
										reporthash.put("time", hostdata.getCollecttime());
										reporthash.put("Ping", hostdata.getThevalue());
										reporthash.put("ping", maxping);
									}
								} else {
									reporthash.put("time", hostdata.getCollecttime());
									reporthash.put("Ping", hostdata.getThevalue());
									reporthash.put("ping", maxping);

								}
							} else {
								reporthash.put("time", hostdata.getCollecttime());
								reporthash.put("Ping", hostdata.getThevalue());
								reporthash.put("ping", maxping);

							}
						}
					} else {
						reporthash.put("ping", maxping);
					}
					// 数据库空间
					SybaseVO sysbaseVO = new SybaseVO();
					// 获取sybase信息
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					dao = new DBDao();
					String serverip = hex + ":" + vo.getId();
					sysbaseVO = dao.getSybaseDataByServerip(serverip);
					if (dao != null) {
						dao.close();
					}

					if (sysbaseVO == null)
						sysbaseVO = new SybaseVO();

					reporthash.put("sysbaseVO", sysbaseVO);
					reporthash.put("dbname", dbname);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					allreporthash.put(ip, reporthash);
				}
				AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_syball("/temp/hostnms_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	private String downloadmultisybasereport2() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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

		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		String grade = "优";
		Hashtable mems = new Hashtable();// 内存信息
		Hashtable sysValue = new Hashtable();
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";
					String newip = doip(ip);
					DBTypeDao typedao = new DBTypeDao();
					try {
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						typedao.close();
					}
					Hashtable reporthash = new Hashtable();

					// 获取sybase信息
					SybaseVO sysbaseVO = new SybaseVO();
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					dao = new DBDao();
					String serverip = hex + ":" + vo.getId();
					sysbaseVO = dao.getSybaseDataByServerip(serverip);
					String statusStr = "0";
					Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
					if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
						statusStr = (String) tempStatusHashtable.get("status");
					}
					if (statusStr.equals("1")) {
						runstr = "正在运行";
						pingnow = "100.0";
					}
					if (dao != null) {
						dao.close();
					}

					if (sysbaseVO == null)
						sysbaseVO = new SybaseVO();
					// Hashtable allsqlserverdata = ShareData.getSysbasedata();
					// Hashtable ipsqlserverdata = new Hashtable();
					//					
					// if(allsqlserverdata != null &&
					// allsqlserverdata.size()>0){
					// if(allsqlserverdata.containsKey(vo.getIpAddress())){
					// ipsqlserverdata =
					// (Hashtable)allsqlserverdata.get(vo.getIpAddress());
					// if(ipsqlserverdata.containsKey("status")){
					// String p_status = (String)ipsqlserverdata.get("status");
					// if(p_status != null && p_status.length()>0){
					// if("1".equalsIgnoreCase(p_status)){
					// runstr = "正在运行";
					// pingnow = "100.0";
					// }
					// }
					// }
					// if(ipsqlserverdata.containsKey("dbValue")){
					// dbValue = (Hashtable)ipsqlserverdata.get("dbValue");
					// }
					// if(ipsqlserverdata.containsKey("retValue")){
					// mems =
					// (Hashtable)((Hashtable)ipsqlserverdata.get("retValue")).get("mems");
					// }
					// if(ipsqlserverdata.containsKey("sysValue")){
					// sysValue = (Hashtable)ipsqlserverdata.get("sysValue");
					// }
					// //数据库信息
					// if(ipsqlserverdata.containsKey("dbValue")){
					// dbValue = (Hashtable)ipsqlserverdata.get("dbValue");
					// }
					// if(ipsqlserverdata.containsKey("sysbaseVO")){
					// sysbaseVO = (SybaseVO)ipsqlserverdata.get("sysbaseVO");
					// }
					// }
					// }
					Hashtable ConnectUtilizationhash = new Hashtable();
					I_HostCollectData hostmanager = new HostCollectDataManager();
					try {
						ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SYSPing", "ConnectUtilization", starttime, totime);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					String pingconavg = "0";
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					}
					if (pingconavg != null) {
						pingconavg = pingconavg.replace("%", "");// 平均连通率
					}
					if (ConnectUtilizationhash.get("downnum") != null) {
						downnum = (String) ConnectUtilizationhash.get("downnum");
					}
					if (ConnectUtilizationhash.get("pingMax") != null) {
						pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
					}
					if (ConnectUtilizationhash.get("pingmax") != null) {
						pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
					}
					avgpingcon = new Double(pingconavg + "").doubleValue();

					p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

					// 得到运行等级
					DBTypeDao dbTypeDao = new DBTypeDao();

					try {
						count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}

					if (count > 0) {
						grade = "良";
					}
					if (!"0".equals(downnum)) {
						grade = "差";
					}

					// 事件列表
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					// request.setAttribute("status", status);
					// request.setAttribute("level1", level1);
					try {
						User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
						// SysLogger.info("user
						// businessid===="+vo.getBusinessids());
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
						// ConnectUtilizationhash =
						// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					Hashtable maxping = new Hashtable();
					maxping.put("pingmax", pingmin + "%");// 最小连通率
					maxping.put("pingnow", pingnow + "%");
					reporthash.put("sysbaseVO", sysbaseVO);
					maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
					reporthash.put("list", eventList);
					reporthash.put("pingmin", pingmin);
					reporthash.put("pingnow", pingnow);
					reporthash.put("pingmax", pingmax);
					reporthash.put("pingconavg", avgpingcon + "");
					reporthash.put("sysValue", sysValue);
					reporthash.put("sqlsys", sysValue);
					reporthash.put("mems", mems);
					reporthash.put("downnum", downnum);
					reporthash.put("count", count);
					reporthash.put("grade", grade);
					reporthash.put("vo", vo);
					reporthash.put("runstr", runstr);
					reporthash.put("typevo", typevo);
					reporthash.put("dbValue", dbValue);
					reporthash.put("typename", typevo.getDbtype());
					reporthash.put("hostnamestr", vo.getDbName());
					reporthash.put("tableinfo_v", dbValue);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					reporthash.put("ping", maxping);
					reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
					reporthash.put("ip", vo.getIpAddress());
					reporthash.put("sysbaseVO", sysbaseVO);
					reporthash.put("dbname", dbname);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);

					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_syball2("/temp/hostnms_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * 多张informix综合报表打印
	 * 
	 * @return
	 */
	private String downloadmultiinformixreport() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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
		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";

		String downnum = "";
		String grade = "优";
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					DBTypeDao typedao = new DBTypeDao();
					try {
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						typedao.close();
					}
					ip = vo.getIpAddress();
					String newip = doip(ip);
					Hashtable reporthash = new Hashtable();
					Hashtable dbValue = new Hashtable();
					Hashtable mems = new Hashtable();// 内存信息
					Hashtable sysValue = new Hashtable();
					int count = 0;
					List eventList = new ArrayList();// 事件列表
					dao = new DBDao();
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					String serverip = hex + ":" + vo.getDbName();
					String statusStr = String.valueOf(((Hashtable) dao.getInformix_nmsstatus(serverip)).get("status"));
					List sessionList = dao.getInformix_nmssession(serverip);
					List lockList = dao.getInformix_nmslock(serverip);
					List logList = dao.getInformix_nmslog(serverip);
					List spaceList = dao.getInformix_nmsspace(serverip);
					List ioList = dao.getInformix_nmsio(serverip);
					dao.close();
					if ("1".equalsIgnoreCase(statusStr)) {
						runstr = "正在运行";
						pingnow = "100";
					}
					dbValue.put("sessionList", sessionList);
					dbValue.put("lockList", lockList);
					dbValue.put("informixspaces", spaceList);
					dbValue.put("informixlog", logList);
					dbValue.put("iolist", ioList);
					// request.setAttribute("newip", newip);
					Hashtable ConnectUtilizationhash = new Hashtable();
					I_HostCollectData hostmanager = new HostCollectDataManager();
					try {
						ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "INFORMIXPing", "ConnectUtilization", starttime, totime);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					String pingconavg = "0";
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					}
					if (pingconavg != null) {
						pingconavg = pingconavg.replace("%", "");// 平均连通率
					}
					if (ConnectUtilizationhash.get("downnum") != null) {
						downnum = (String) ConnectUtilizationhash.get("downnum");
					}
					if (ConnectUtilizationhash.get("pingMax") != null) {
						pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
					}
					if (ConnectUtilizationhash.get("pingmax") != null) {
						pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
					}
					avgpingcon = new Double(pingconavg + "").doubleValue();

					p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

					// 得到运行等级
					DBTypeDao dbTypeDao = new DBTypeDao();

					try {
						count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}

					if (count > 0) {
						grade = "良";
					}
					if (!"0".equals(downnum)) {
						grade = "差";
					}
					// 事件列表
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					// request.setAttribute("status", status);
					// request.setAttribute("level1", level1);
					try {
						User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
						// SysLogger.info("user
						// businessid===="+vo.getBusinessids());
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
						// ConnectUtilizationhash =
						// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					Hashtable maxping = new Hashtable();
					reporthash.put("list", eventList);
					maxping.put("pingmax", pingmin + "%");// 最小连通率
					maxping.put("pingnow", pingnow + "%");
					maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
					reporthash.put("pingmin", pingmin);
					reporthash.put("pingnow", pingnow);
					reporthash.put("pingmax", pingmax);
					reporthash.put("pingconavg", avgpingcon + "");
					reporthash.put("sysValue", sysValue);
					reporthash.put("sqlsys", sysValue);
					reporthash.put("mems", mems);
					reporthash.put("downnum", downnum);
					reporthash.put("count", count);
					reporthash.put("grade", grade);
					reporthash.put("vo", vo);
					reporthash.put("runstr", runstr);
					reporthash.put("typevo", typevo);
					reporthash.put("typename", typevo.getDbtype());
					reporthash.put("hostnamestr", vo.getDbName());
					reporthash.put("dbValue", dbValue);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					reporthash.put("ping", maxping);
					reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
					reporthash.put("ip", vo.getIpAddress());

					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_informixall("/temp/informixall_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * 多张mysql综合运行分析报表打印
	 * 
	 * @return
	 */
	private String downloadmultimysqlreport() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(",").length > 0) {
			String[] _ids = oids.split(",");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
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
		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		String downnum = "";
		String grade = "优";
		List eventList = new ArrayList();// 事件列表
		int doneFlag = 0;
		int count = 0;
		try {
			Hashtable allreporthash = new Hashtable();
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					DBDao dao = new DBDao();
					try {
						vo = (DBVo) dao.findByID(String.valueOf(ids[i]));
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dao.close();
					}
					DBTypeDao typedao = new DBTypeDao();
					try {
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						typedao.close();
					}
					ip = vo.getIpAddress();
					String newip = doip(ip);
					Hashtable reporthash = new Hashtable();
					Vector Val = new Vector();
					List sessionlist = new ArrayList();
					Hashtable tablesHash = new Hashtable();
					Vector tableinfo_v = new Vector();
					Hashtable dbValue = new Hashtable();
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					String serverip = hex + ":" + vo.getId();
					Hashtable ipData = dao.getMysqlDataByServerip(serverip);
					if (dao != null) {
						dao.close();
					}
					if (ipData != null && ipData.size() > 0) {
						String dbnames = vo.getDbName();
						String[] dbs = dbnames.split(",");
						for (int k = 0; k < dbs.length; k++) {
							// 判断是否已经获取了当前的配置信息
							// if(doneFlag == 1)break;
							String dbStr = dbs[k];
							if (ipData.containsKey(dbStr)) {
								Hashtable returnValue = new Hashtable();
								returnValue = (Hashtable) ipData.get(dbStr);
								if (returnValue != null && returnValue.size() > 0) {
									if (doneFlag == 0) {
										// 判断是否已经获取了当前的配置信息
										if (returnValue.containsKey("configVal")) {
											doneFlag = 1;
										}
										if (returnValue.containsKey("Val")) {
											Val = (Vector) returnValue.get("Val");
										}
									}
									if (returnValue.containsKey("sessionsDetail")) {
										// 存在数据库连接信息
										sessionlist.add((List) returnValue.get("sessionsDetail"));
									}
									if (returnValue.containsKey("tablesDetail")) {
										// 存在数据库表信息
										tablesHash.put(dbStr, (List) returnValue.get("tablesDetail"));
									}
									if (returnValue.containsKey("tablesDetail")) {
										// 存在数据库表信息
										tableinfo_v = (Vector) returnValue.get("variables");
									}
								}
							}
						}

						runstr = (String) ipData.get("runningflag");
						if (runstr != null && runstr.contains("服务停止")) {// 将<font
							// color=red>服务停止</font>
							// 替换
							runstr = "服务停止";
						}
						if (runstr != null && runstr.contains("正在运行")) {
							pingnow = "100";
						}
					}
					// }
					// request.setAttribute("newip", newip);
					Hashtable ConnectUtilizationhash = new Hashtable();
					I_HostCollectData hostmanager = new HostCollectDataManager();
					try {
						ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "MYPing", "ConnectUtilization", starttime, totime);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					String pingconavg = "0";
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
					}
					if (pingconavg != null) {
						pingconavg = pingconavg.replace("%", "");// 平均连通率
					}
					if (ConnectUtilizationhash.get("downnum") != null) {
						downnum = (String) ConnectUtilizationhash.get("downnum");
					}
					if (ConnectUtilizationhash.get("pingMax") != null) {
						pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
					}
					if (ConnectUtilizationhash.get("pingmax") != null) {
						pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
					}
					avgpingcon = new Double(pingconavg + "").doubleValue();

					p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

					// 得到运行等级
					DBTypeDao dbTypeDao = new DBTypeDao();

					try {
						count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}

					if (count > 0) {
						grade = "良";
					}
					if (!"0".equals(downnum)) {
						grade = "差";
					}
					// 事件列表
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					// request.setAttribute("status", status);
					// request.setAttribute("level1", level1);
					try {
						User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
						// SysLogger.info("user
						// businessid===="+vo.getBusinessids());
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
						// ConnectUtilizationhash =
						// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					Hashtable maxping = new Hashtable();
					maxping.put("pingmax", pingmin + "%");// 最小连通率
					maxping.put("pingnow", pingnow + "%");
					maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
					reporthash.put("list", eventList);
					reporthash.put("pingmin", pingmin);
					reporthash.put("pingnow", pingnow);
					reporthash.put("pingmax", pingmax);
					reporthash.put("pingconavg", avgpingcon + "");
					reporthash.put("tablesHash", tablesHash);
					reporthash.put("sessionlist", sessionlist);
					reporthash.put("Val", Val);
					reporthash.put("downnum", downnum);
					reporthash.put("count", count);
					reporthash.put("grade", grade);
					reporthash.put("vo", vo);
					reporthash.put("runstr", runstr);
					reporthash.put("typevo", typevo);
					reporthash.put("dbValue", dbValue);
					reporthash.put("typename", typevo.getDbtype());
					reporthash.put("hostnamestr", vo.getDbName());
					reporthash.put("tableinfo_v", tableinfo_v);
					reporthash.put("starttime", starttime);
					reporthash.put("totime", totime);
					reporthash.put("ping", maxping);
					reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
					reporthash.put("ip", vo.getIpAddress());
					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(), allreporthash);
				report.createReport_mysqlall("/temp/mysqlall_report.xls");
				request.setAttribute("filename", report.getFileName());

			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";
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
			SysLogger.error("", e);
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
			SysLogger.error("", e);
		}
	}





	public void createDocContext(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// 正文字体风格
		Paragraph title = new Paragraph("数据库连通率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(7);
		int width[] = { 50, 50, 50, 70, 50, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();

		Cell cell = new Cell("序号");
		this.setCellFormat(cell, true);
		aTable.addCell(cell);
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("数据库类型");
		Cell cell2 = new Cell("数据库名称");
		Cell cell3 = new Cell("数据库应用");
		Cell cell15 = new Cell("平均连通率");
		Cell cell4 = new Cell("宕机次数(个)");
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell3, true);
		this.setCellFormat(cell15, true);
		this.setCellFormat(cell4, true);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell15);
		aTable.addCell(cell4);

		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String equname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String avgping = (String) _pinglist.get(4);
				String downnum = (String) _pinglist.get(5);

				Cell cell5 = new Cell(i + 1 + "");
				Cell cell6 = new Cell(ip);
				Cell cell7 = new Cell(dbtype);
				Cell cell8 = new Cell(equname);
				Cell cell9 = new Cell(dbuse);
				Cell cell10 = new Cell(avgping);
				Cell cell13 = new Cell(downnum);
				this.setCellFormat(cell5, false);
				this.setCellFormat(cell6, false);
				this.setCellFormat(cell7, false);
				this.setCellFormat(cell8, false);
				this.setCellFormat(cell9, false);
				this.setCellFormat(cell10, false);
				this.setCellFormat(cell13, false);
				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell13);

			}
		}
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// HONGLI
	// jhl add sqlServer 性能报表下载

	public String createSqlServerDoc() {
		Date d = new Date();
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
		String dbname = "";
		String typename = "SQL SERVER";
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		DBDao dao = null;
		String hostnamestr = "";
		DBVo vo = null;
		try {
			String newip_ = request.getParameter("newip");
			String ip = request.getParameter("ip");
			dao = new DBDao();
			try {
				vo = (DBVo) dao.findByCondition("ip_address", ip, 2).get(0);
			} catch (Exception e) {

			} finally {
				dao.close();
			}
			dbname = vo.getDbName() + "(" + ip + ")";
			hostnamestr = vo.getDbName();
			String newip = newip_;
			Hashtable pinghash = hostmanager.getCategory(vo.getId() + "", "SQLPing", "ConnectUtilization", starttime, totime);
			p_draw_line(pinghash, "", newip + "ConnectUtilization", 740, 120);
			String pingconavg = "";
			if (pinghash.get("avgpingcon") != null)
				pingconavg = (String) pinghash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (pinghash.get("max") != null) {
				ConnectUtilizationmax = (String) pinghash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);
			Hashtable reporthash = new Hashtable();

			Vector pdata = (Vector) pingdata.get(ip);
			// 把ping得到的数据加进去
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


			// 连通率事件次数
			String downnum = "0";
			Hashtable pinghash1 = new Hashtable();
			try {
				pinghash1 = hostmanager.getCategory(vo.getIpAddress(), "SQLPing", "ConnectUtilization", starttime, totime);
				if (pinghash1.get("downnum") != null)
					downnum = (String) pinghash1.get("downnum");
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			DBTypeDao dbTypeDao = new DBTypeDao();
			int count = 0;
			try {
				count = dbTypeDao.finddbcountbyip(ip);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			// 数据库运行等级=====================
			String grade = "优";
			if (count > 0) {
				grade = "良";
			}

			if (!"0".equals(downnum)) {
				grade = "差";
			}
			reporthash.put("downnum", downnum);
			reporthash.put("dbname", dbname);
			reporthash.put("starttime", starttime);
			reporthash.put("totime", totime);
			reporthash.put("typename", typename);

			reporthash.put("hostnamestr", hostnamestr);

			reporthash.put("count", count + "");
			reporthash.put("grade", grade);
			reporthash.put("ip", ip);

			// end mem
			AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
			String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
			if ("0".equals(str)) {
				report.createReport_sql("/temp/dbsql_report.xls");
				request.setAttribute("filename", report.getFileName());
				SysLogger.info("filename" + report.getFileName());
				request.setAttribute("filename", report.getFileName());
			} else if ("1".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_report.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_sqlDoc(fileName);// word综合报表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("2".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_report.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
					report1.createReport_sqlPDF(fileName);// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_sqlNewDoc(fileName, "doc");// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
				try {
					String file = "temp/dbsql_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
					String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

					report1.createReport_sqlNewPDF(fileName);// word业务分析表

					request.setAttribute("filename", fileName);
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		return "/capreport/db/download.jsp";

	}

	// 调用主机连通率报表zhushouzhi
	public String createdoc() {
		String file = "/temp/shujukuliantonglvbaobiao.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContext(fileName);
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi--------------------db--pdf
	public void createContextpdf(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// 标题字体风格
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("数据库连通率报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		document.add(new Paragraph("\n"));
		List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(7);
		setTableFormat(aTable);

		aTable.addCell(this.setCellFormat(new Phrase("标题", contextFont), true));
		Cell cell1 = new Cell(new Phrase("IP地址", contextFont));
		Cell cell11 = new Cell(new Phrase("数据库类型", contextFont));
		Cell cell2 = new Cell(new Phrase("数据库名称", contextFont));
		Cell cell3 = new Cell(new Phrase("数据库应用", contextFont));
		Cell cell4 = new Cell(new Phrase("平均连通率", contextFont));
		Cell cell5 = new Cell(new Phrase("宕机次数(个)", contextFont));
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell3, true);
		this.setCellFormat(cell4, true);
		this.setCellFormat(cell5, true);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell4);
		aTable.addCell(cell5);

		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String equname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String avgping = (String) _pinglist.get(4);
				String downnum = (String) _pinglist.get(5);

				Cell cell15 = new Cell(new Phrase(i + 1 + ""));
				Cell cell6 = new Cell(new Phrase(ip));
				Cell cell7 = new Cell(new Phrase(dbtype));
				Cell cell8 = new Cell(new Phrase(equname, contextFont));
				Cell cell9 = new Cell(new Phrase(dbuse));
				Cell cell10 = new Cell(new Phrase(avgping));
				Cell cell16 = new Cell(new Phrase(downnum));
				this.setCellFormat(cell15, false);
				this.setCellFormat(cell16, false);
				this.setCellFormat(cell10, false);
				this.setCellFormat(cell9, false);
				this.setCellFormat(cell8, false);
				this.setCellFormat(cell7, false);
				this.setCellFormat(cell6, false);
				aTable.addCell(cell15);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell10);
				aTable.addCell(cell16);

			}
		}
		// 导出连通率
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// 设置图片显示位置
		img.scalePercent(75);
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// 调用数据库连通率报表zhushouzhi
	public String createpdf() {
		String file = "/temp/shujukuliantonglvbaobiao.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createContextpdf(fileName);
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// zhushouzhi-----------------------数据库pdf 报表
	// db event report
	// zhushouzhi--------------------db--pdf
	public void createContexteventpdf(String file) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// 标题字体风格
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("数据库事件报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String contextString = "报表生成时间:" + sdf.format(new Date()) + " \n"// 换行
				+ "数据统计时间段:" + String.valueOf(session.getAttribute("starttime")) + " 至 " + String.valueOf(session.getAttribute("totime"));
		Paragraph context = new Paragraph(contextString, contextFont);
		// 正文格式左对齐
		context.setAlignment(Element.ALIGN_LEFT);
		// context.setFont(contextFont);
		// 离上一段落（标题）空的行数
		context.setSpacingBefore(5);
		// 设置第一行空的列数
		context.setFirstLineIndent(5);
		document.add(context);
		document.add(new Paragraph("\n"));

		// 设置 Table 表格
		document.add(new Paragraph("\n"));
		List pinglist = (List) session.getAttribute("eventlist");
		PdfPTable aTable = new PdfPTable(10);
		int width[] = { 30, 70, 50, 70, 50, 50, 50, 50, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidthPercentage(100);

		aTable.addCell(new Phrase(""));
		PdfPCell cell1 = new PdfPCell(new Phrase("IP地址", contextFont));
		PdfPCell cell11 = new PdfPCell(new Phrase("数据库类型", contextFont));
		PdfPCell cell2 = new PdfPCell(new Phrase("数据库名称", contextFont));
		PdfPCell cell3 = new PdfPCell(new Phrase("数据库应用", contextFont));
		PdfPCell cell12 = new PdfPCell(new Phrase("事件总数", contextFont));
		PdfPCell cell13 = new PdfPCell(new Phrase("普通事件", contextFont));
		PdfPCell cell14 = new PdfPCell(new Phrase("严重事件", contextFont));
		PdfPCell cell16 = new PdfPCell(new Phrase("紧急事件", contextFont));
		PdfPCell cell4 = new PdfPCell(new Phrase("服务器不可用次数", contextFont));

		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell12.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell13.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell14.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell16.setHorizontalAlignment(Element.ALIGN_CENTER);

		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell12);// 事件总数
		aTable.addCell(cell13);// 普通事件个数
		aTable.addCell(cell14);// 严重事件个数
		aTable.addCell(cell16);// 紧急事件个数
		aTable.addCell(cell4);
		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String equname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				String level1 = String.valueOf(_pinglist.get(5));
				String level2 = String.valueOf(_pinglist.get(6));
				String level3 = String.valueOf(_pinglist.get(7));
				String total = String.valueOf(_pinglist.get(8));

				PdfPCell cell15 = new PdfPCell(new Phrase(i + 1 + ""));
				PdfPCell cell6 = new PdfPCell(new Phrase(ip));
				PdfPCell cell7 = new PdfPCell(new Phrase(dbtype));
				PdfPCell cell8 = new PdfPCell(new Phrase(equname, contextFont));
				PdfPCell cell9 = new PdfPCell(new Phrase(dbuse));
				PdfPCell cell10 = new PdfPCell(new Phrase(downnum));

				PdfPCell cell17 = new PdfPCell(new Phrase(total));
				PdfPCell cell18 = new PdfPCell(new Phrase(level1));
				PdfPCell cell19 = new PdfPCell(new Phrase(level2));
				PdfPCell cell20 = new PdfPCell(new Phrase(level3));

				aTable.addCell(cell15);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell17);// 总数
				aTable.addCell(cell18);// 普通事件个数
				aTable.addCell(cell19);// 严重事件个数
				aTable.addCell(cell20);// 紧急事件个数
				aTable.addCell(cell10);
			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// 调用数据库连通率报表zhushouzhi
	public String createeventpdf() {
		String file = "/temp/dbevent.pdf";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextevent(fileName, "pdf");
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	// 之下zhushouzhidb连通率报表
	public void createDocContextevent(String file, String type) throws DocumentException, IOException {
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		if ("pdf".equals(type)) {
			PdfWriter.getInstance(document, new FileOutputStream(file));
		} else {
			RtfWriter2.getInstance(document, new FileOutputStream(file));
		}
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("数据库事件报表", titleFont);
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String contextString = "报表生成时间:" + sdf.format(new Date()) + " \n"// 换行
				+ "数据统计时间段:" + String.valueOf(session.getAttribute("starttime")) + " 至 " + String.valueOf(session.getAttribute("totime"));
		Paragraph context = new Paragraph(contextString, contextFont);
		// 正文格式左对齐
		context.setAlignment(Element.ALIGN_LEFT);
		// context.setFont(contextFont);
		// 离上一段落（标题）空的行数
		context.setSpacingBefore(5);
		// 设置第一行空的列数
		context.setFirstLineIndent(5);
		document.add(context);

		// 设置 Table 表格
		List pinglist = null;
		pinglist = (List) session.getAttribute("eventlist");// HONGLI
		if (pinglist == null) {
			pinglist = (List) request.getAttribute("ls");
		}
		Table aTable = new Table(10);
		this.setTableFormat(aTable);
		aTable.endHeaders();

		aTable.addCell(this.setCellFormat(new Phrase("序号", contextFont), true));
		Cell cell1 = new Cell(new Phrase("IP地址", contextFont));
		Cell cell11 = new Cell(new Phrase("数据库类型", contextFont));
		Cell cell2 = new Cell(new Phrase("数据库名称", contextFont));
		Cell cell3 = new Cell(new Phrase("数据库应用", contextFont));
		Cell cell20 = new Cell(new Phrase("事件总数", contextFont));
		Cell cell21 = new Cell(new Phrase("普通事件", contextFont));
		Cell cell22 = new Cell(new Phrase("严重事件", contextFont));
		Cell cell23 = new Cell(new Phrase("紧急事件", contextFont));
		Cell cell15 = new Cell(new Phrase("服务器不可用次数", contextFont));
		this.setCellFormat(cell1, true);
		this.setCellFormat(cell11, true);
		this.setCellFormat(cell2, true);
		this.setCellFormat(cell3, true);
		this.setCellFormat(cell20, true);
		this.setCellFormat(cell21, true);
		this.setCellFormat(cell22, true);
		this.setCellFormat(cell23, true);
		this.setCellFormat(cell15, true);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell20);
		aTable.addCell(cell21);
		aTable.addCell(cell22);
		aTable.addCell(cell23);
		aTable.addCell(cell15);
		/*
		 * if (pinglist != null && pinglist.size() > 0) { for (int i = 0; i <
		 * pinglist.size(); i++) { List _pinglist = (List) pinglist.get(i);
		 * String ip = (String) _pinglist.get(0); String equname = (String)
		 * _pinglist.get(1); String osname = (String) _pinglist.get(2); String
		 * avgping = (String) _pinglist.get(3); String downnum = (String)
		 * _pinglist.get(4);
		 */

		if (pinglist != null && pinglist.size() > 0) {
			for (int i = 0; i < pinglist.size(); i++) {
				List _pinglist = (List) pinglist.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String equname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				String level1 = String.valueOf(_pinglist.get(5));
				String level2 = String.valueOf(_pinglist.get(6));
				String level3 = String.valueOf(_pinglist.get(7));
				String total = String.valueOf(_pinglist.get(8));

				Cell cell5 = new Cell(i + 1 + "");
				Cell cell6 = new Cell(ip);
				Cell cell7 = new Cell(dbtype);
				Cell cell8 = new Cell(equname);
				Cell cell9 = new Cell(dbuse);
				Cell cell24 = new Cell(total);
				Cell cell25 = new Cell(level1);
				Cell cell26 = new Cell(level2);
				Cell cell27 = new Cell(level3);
				Cell cell10 = new Cell(downnum);
				this.setCellFormat(cell5, false);
				this.setCellFormat(cell6, false);
				this.setCellFormat(cell7, false);
				this.setCellFormat(cell8, false);
				this.setCellFormat(cell9, false);
				this.setCellFormat(cell24, false);
				this.setCellFormat(cell25, false);
				this.setCellFormat(cell26, false);
				this.setCellFormat(cell27, false);
				this.setCellFormat(cell10, false);

				aTable.addCell(cell5);
				aTable.addCell(cell6);
				aTable.addCell(cell7);
				aTable.addCell(cell8);
				aTable.addCell(cell9);
				aTable.addCell(cell24);
				aTable.addCell(cell25);
				aTable.addCell(cell26);
				aTable.addCell(cell27);
				aTable.addCell(cell10);

			}
		}
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// HONGLI
	// jhl add oracle event report
	public String createOraEventDoc() {
		String ipaddress = (String) request.getParameter("ipaddress");
		String typevo = (String) request.getParameter("typevo");
		String dbname = (String) request.getParameter("dbname");
		int p = (Integer) session.getAttribute("_pingvalue");

		String file = "/temp/dbevent.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		// HONGLI ADD START1
		String startdate = getParaValue("startdate");
		Date d = new Date();
		if (startdate == null) {
			startdate = sdf0.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		Hashtable dbDate = new Hashtable();
		dbDate.put("fileName", fileName);
		dbDate.put("ipaddress", ipaddress);
		dbDate.put("dbtype", typevo);
		dbDate.put("downnum", p);
		dbDate.put("_dbname", dbname);
		dbDate.put("starttime", starttime);
		dbDate.put("totime", totime);
		// HONGLI ADD END1
		try {
			// createOraEventReport(fileName,ipaddress,typevo,p,dbname);
			createOraEventReport(dbDate);// HONGLI MODIFY
		} catch (DocumentException es) {
			es.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	/**
	 * @author HONGLI ADD 2010-11-2
	 * @param dbDate
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void createOraEventReport(Hashtable dbDate) throws DocumentException, IOException {
		String _filename = (String) dbDate.get("fileName");
		String ip = (String) dbDate.get("ipaddress");
		String dbtype = (String) dbDate.get("dbtype");
		String downnum = (Integer) dbDate.get("downnum") + "";
		String _dbname = (String) dbDate.get("_dbname");
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(_filename));
		document.open();
		// 设置中文字体
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "", BaseFont.NOT_EMBEDDED);
		// 标题字体风格
		// 正文字体风格
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("数据库事件报表");
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		String startdate = (String) dbDate.get("starttime");
		String todate = (String) dbDate.get("totime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String contextString = "报表生成时间:" + sdf.format(new Date()) + " \n"// 换行
				+ "数据统计时间段:" + startdate + " 至 " + todate;
		Paragraph context = new Paragraph(contextString, contextFont);
		// 正文格式左对齐
		context.setAlignment(Element.ALIGN_LEFT);
		// context.setFont(contextFont);
		// 离上一段落（标题）空的行数
		context.setSpacingBefore(5);
		// 设置第一行空的列数
		context.setFirstLineIndent(5);
		document.add(context);

		// 设置 Table 表格
		List pinglist = null;
		pinglist = (List) session.getAttribute("eventlist");
		if (pinglist == null) {
			pinglist = (List) request.getAttribute("ls");
		}
		Table aTable = new Table(6);
		int width[] = { 50, 50, 50, 70, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();

		aTable.addCell(new Cell(""));
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("数据库类型");
		Cell cell2 = new Cell("数据库名称");
		Cell cell3 = new Cell("数据库应用");
		Cell cell15 = new Cell("服务器不可用次数");
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell15);

		Cell cell5 = new Cell(1 + "");
		Cell cell6 = new Cell(ip);
		Cell cell7 = new Cell(dbtype);
		Cell cell8 = new Cell(_dbname);
		Cell cell9 = new Cell("afunms");
		Cell cell10 = new Cell(downnum);

		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		aTable.addCell(cell9);
		aTable.addCell(cell10);

		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	public void createOraEventReport(String filename, String _ip, String type, int pvalue, String dbname) throws DocumentException, IOException {
		String ip = _ip;
		String dbtype = type;
		String downnum = pvalue + "";
		String _dbname = dbname;
		// 设置纸张大小
		Document document = new Document(PageSize.A4);
		// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
		RtfWriter2.getInstance(document, new FileOutputStream(filename));
		document.open();
		// 正文字体风格
		Paragraph title = new Paragraph("数据库事件报表");
		// 设置标题格式对齐方式
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		// 设置 Table 表格
		List pinglist = null;
		pinglist = (List) session.getAttribute("eventlist");
		if (pinglist == null) {
			pinglist = (List) request.getAttribute("ls");
		}
		Table aTable = new Table(6);
		int width[] = { 50, 50, 50, 70, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // 占页面宽度 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// 居中显示
		aTable.setAutoFillEmptyCells(true); // 自动填满
		aTable.setBorderWidth(1); // 边框宽度
		aTable.setBorderColor(new Color(0, 125, 255)); // 边框颜色
		aTable.setPadding(2);// 衬距，看效果就知道什么意思了
		aTable.setSpacing(0);// 即单元格之间的间距
		aTable.setBorder(2);// 边框
		aTable.endHeaders();

		aTable.addCell(new Cell(""));
		Cell cell1 = new Cell("IP地址");
		Cell cell11 = new Cell("数据库类型");
		Cell cell2 = new Cell("数据库名称");
		Cell cell3 = new Cell("数据库应用");
		Cell cell15 = new Cell("服务器不可用次数");
		cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell11.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		aTable.addCell(cell1);
		aTable.addCell(cell11);
		aTable.addCell(cell2);
		aTable.addCell(cell3);
		aTable.addCell(cell15);

		Cell cell5 = new Cell(1 + "");
		Cell cell6 = new Cell(ip);
		Cell cell7 = new Cell(dbtype);
		Cell cell8 = new Cell(_dbname);
		Cell cell9 = new Cell("afunms");
		Cell cell10 = new Cell(downnum);

		aTable.addCell(cell5);
		aTable.addCell(cell6);
		aTable.addCell(cell7);
		aTable.addCell(cell8);
		aTable.addCell(cell9);
		aTable.addCell(cell10);

		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// jhl end oracle event report

	// 调用主机连通率报表zhushouzhi
	public String createeventdoc() {
		// HONGLI START0
		String ipaddress = (String) request.getParameter("ipaddress");
		String typevo = (String) request.getParameter("typevo");
		String dbname = (String) request.getParameter("dbname");
		// String db = (String)request.getParameter("db");
		// int p = (Integer)session.getAttribute("_pingvalue");
		// int pingvalue = (int)request.getParameter("pingvalue");
		List ls = new ArrayList();
		ls.add(0, ipaddress);
		ls.add(1, typevo);
		ls.add(2, dbname);
		// ls.add(3,db);
		// ls.add(4,pingvalue);
		List list = ls;
		List _list = list;
		// HONGLI END0
		request.setAttribute("ls", _list);
		String file = "/temp/dbevent.doc";// 保存到项目文件夹下的指定文件夹
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
		try {
			createDocContextevent(fileName, "doc");
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}
		request.setAttribute("filename", fileName);
		return "/capreport/net/download.jsp";
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("find"))
			return find();
		if (action.equals("dbReport"))
			return dbReport();
		if (action.equals("eventlist"))
			return eventlist();
		if (action.equals("oraclelist"))
			return oraclelist();
		if (action.equals("db2list"))
			return db2list();
		if (action.equals("sqlserverlist"))
			return sqlserverlist();
		if (action.equals("sybaselist"))
			return sybaselist();
		if (action.equals("informixlist"))
			return informixlist();
		if (action.equals("mysqllist")) {// HONGLI ADD
			return mysqllist();
		}
		// if(action.equals("netmultilist"))
		// return netmultilist();
		if (action.equals("dbping"))
			return dbping();
		if (action.equals("dbevent"))
			return dbevent();
		if (action.equals("downloaddbpingreport"))
			return downloaddbpingreport();
		if (action.equals("downloaddbeventreport"))
			return downloaddbeventreport();
		// 生成单个报表
		// HONGLI START1
		if (action.equals("createSqlServerDoc"))
			return createSqlServerDoc();
		if (action.equals("downloadSQLServerEventReport"))
			return downloadSQLServerEventReport();
		if (action.equals("createSQLServerEventWord"))
			return createSQLServerEventWord();
		if (action.equals("ceateSServerEventPdf"))
			return ceateSServerEventPdf();
		if (action.equals("createOraEventPdf"))
			return createOraEventPdf();
		if (action.equals("downlooraeventreport"))
			return downlooraeventreport();
		if (action.equals("createOraEventDoc"))
			// HONGLI END1
			return createOraEventDoc();
		if (action.equals("downloadoraselfreport"))
			return downloadoraselfreport();
		// zhushouzhi--------
		if (action.equals("downloadinforselfreport"))
			return downloadinforselfreport();
		// zhushouzhi--------------
		if (action.equals("downloaddb2selfreport"))
			return downloaddb2selfreport();
		if (action.equals("downloadsqlselfreport"))
			return downloadsqlselfreport();
		if (action.equals("downloadsybaseselfreport"))
			return downloadsybaseselfreport();
		if (action.equals("downloadmysqlselfreport")) {// HONGLI ADD
			return downloadmysqlselfreport();
		}
		// 生成多张报表
		if (action.equals("downloadmultiorareport"))
			return downloadmultiorareport();
		if (action.equals("downloadmultidb2report"))
			return downloadmultidb2report();
		if (action.equals("downloadmultisqlreport"))
			return downloadmultisqlreport();
		if (action.equals("downloadmultisybasereport"))
			return downloadmultisybasereport();
		// HONGLI ADD START0
		if (action.equals("downloadmultiorareport2")) {
			return downloadmultiorareport2();
		}
		if (action.equals("downloadmultidb2report2")) {
			return downloadmultidb2report2();
		}
		if (action.equals("downloadmultisqlreport2")) {
			return downloadmultisqlreport2();
		}
		if (action.equals("downloadmultisybasereport2")) {
			return downloadmultisybasereport2();
		}
		if (action.equals("downloadmultiinformixreport")) {
			return downloadmultiinformixreport();
		}
		if (action.equals("downloadmultimysqlreport")) {
			return downloadmultimysqlreport();
		}
		// HONGLI ADD END0
		if (action.equals("createdoc"))
			return createdoc();
		if (action.equals("createpdf"))
			return createpdf();
		if (action.equals("createeventdoc"))
			return createeventdoc();
		if (action.equals("createeventpdf"))
			return createeventpdf();
		if (action.equals("downloaddb2selfreport"))
			return downloaddb2selfreport();
		// HONGLI ADD START
		if (action.equals("createSServerPingReport")) {
			return createSServerPingReport();
		}
		if (action.equals("createSServerSelfReport")) {
			return createSServerSelfReport();
		}
		if (action.equals("createSServerCldReport")) {
			try {
				return createSServerCldReport();
			} catch (DocumentException e) {
				SysLogger.error("", e);
			}
		}
		if (action.equals("createSServerEventReport")) {
			return createSServerEventReport();
		}
		if (action.equals("createDB2PingReport")) {
			return createDB2PingReport();
		}
		if (action.equals("createDB2SelfReport")) {
			return createDB2SelfReport();
		}
		if (action.equals("createDB2CldReport")) {
			return createDB2CldReport();
		}
		if (action.equals("createDB2EventReport")) {
			return createDB2EventReport();
		}
		if (action.equals("createSybasePingReport")) {
			return createSybasePingReport();
		}
		if (action.equals("createSybaseSelfReport")) {
			return createSybaseSelfReport();
		}
		if (action.equals("createSybaseCldReport")) {
			return createSybaseCldReport();
		}
		if (action.equals("createSybaseEventReport")) {
			return createSybaseEventReport();
		}
		if (action.equals("createInformixPingReport")) {
			return createInformixPingReport();
		}
		if (action.equals("createInformixSelfReport")) {
			return createInformixSelfReport();
		}
		if (action.equals("createInformixCldReport")) {
			return createInformixCldReport();
		}
		if (action.equals("createInformixEventReport")) {
			return createInformixEventReport();
		}
		if (action.equals("createMySQLPingReport")) {
			return createMySQLPingReport();
		}
		if (action.equals("createMySQLSelfReport")) {
			return createMySQLSelfReport();
		}
		if (action.equals("createMySQLCldReport")) {
			return createMySQLCldReport();
		}
		if (action.equals("createMySQLEventReport")) {
			return createMySQLEventReport();
		}
		if (action.equals("createOracleEventReport")) {
			return createOracleEventReport();
		}

		// HONGLI ADD END
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}


	private String doip(String ip) {
		// String newip="";
		// for(int i=0;i<3;i++){
		// int p=ip.indexOf(".");
		// newip+=ip.substring(0,p);
		// ip=ip.substring(p+1);
		// }
		// newip+=ip;
		String allipstr = SysUtil.doip(ip);
		// System.out.println("newip="+newip);
		return allipstr;
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
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, c, d);// .createCategoryDataset(rowKeys,
			// columnKeys,
			// data);
			cg.zhu(title1, title2, dataset, w, h);
		} else {
			draw_blank(title1, title2, w, h);
		}
		bighash = null;
	}


	// ----------------informix报表 start
	private String downloadinforselfreport() {
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
		String typename = "INFORMIX";
		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		Hashtable dbValue = new Hashtable();
		Vector vector = new Vector();
		DBVo vo = null;
		try {
			ip = getParaValue("ipaddress");
			dao = new DBDao();
			vo = (DBVo) dao.findByCondition("ip_address", ip, 7).get(0);

			dbname = vo.getDbName() + "(" + ip + ")";
			dbnamestr = vo.getDbName();
			String newip = doip(ip);
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getDbName();
			String status = String.valueOf(((Hashtable) dao.getInformix_nmsstatus(serverip)).get("status"));
			List databaseList = dao.getInformix_nmsdatabase(serverip);
			List sessionList = dao.getInformix_nmssession(serverip);
			List lockList = dao.getInformix_nmslock(serverip);
			List logList = dao.getInformix_nmslog(serverip);
			List aboutList = dao.getInformix_nmsabout(serverip);
			List spaceList = dao.getInformix_nmsspace(serverip);
			List configList = dao.getInformix_nmsconfig(serverip);
			if ("1".equalsIgnoreCase(status)) {
				runstr = "正在运行";
			}
			dbValue.put("databaselist", databaseList);
			dbValue.put("sessionList", sessionList);
			dbValue.put("lockList", lockList);
			dbValue.put("informixspaces", spaceList);
			dbValue.put("informixlog", logList);
			dbValue.put("configList", configList);
			dbValue.put("aboutlist", aboutList);

			// sysValue = ShareData.getInformixmonitordata();
			// sValue = (Hashtable)sysValue.get(vo.getIpAddress());

			/* dbValue = (Hashtable)sValue.get(vo.getDbName()); */

			// end
			dao = new DBDao();
			try {
				if (dao.getInformixIsOk(vo.getIpAddress(), vo.getPort() + "", vo.getUser(), EncryptUtil.decode(vo.getPassword()), vo.getDbName(), vo.getAlias())) {
					runstr = "正在运行";
				}
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			Hashtable pinghash = hostmanager.getCategory(vo.getId() + "", "INFORMIXPing", "ConnectUtilization", starttime, totime);
			// Hashtable ConnectUtilizationhash =
			// hostmanager.getCategory(ip,"Ping","ConnectUtilization",starttime,totime);
			p_draw_line(pinghash, "", newip + "ConnectUtilization", 740, 120);
			String pingconavg = "";
			if (pinghash.get("avgpingcon") != null)
				pingconavg = (String) pinghash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (pinghash.get("max") != null) {
				ConnectUtilizationmax = (String) pinghash.get("max");
			}
			maxping.put("pingmax", ConnectUtilizationmax);

		} catch (Exception e) {
			SysLogger.error("", e);
		}
		// request.setAttribute("imgurl",imgurlhash);
		request.setAttribute("hash", hash);
		request.setAttribute("max", maxhash);
		request.setAttribute("memmaxhash", memmaxhash);
		request.setAttribute("memavghash", memavghash);
		request.setAttribute("diskhash", diskhash);
		request.setAttribute("memhash", memhash);

		Hashtable reporthash = new Hashtable();

		Vector pdata = (Vector) pingdata.get(ip);
		// Vector pdata = (Vector) ShareData.getOraspacedata();
		// 把ping得到的数据加进去
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
			pinghash = hostmanager.getCategory(vo.getIpAddress(), "INFORMIXPing", "ConnectUtilization", starttime, totime);
			if (pinghash.get("downnum") != null)
				downnum = (String) pinghash.get("downnum");
		} catch (Exception e1) {

			e1.printStackTrace();
		}

		// 表空间==========告警
		DBTypeDao dbTypeDao = new DBTypeDao();
		int count = 0;
		try {
			count = dbTypeDao.finddbcountbyip(ip);
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			dbTypeDao.close();
		}
		// 数据库运行等级=====================
		String grade = "优";
		if (count > 0) {
			grade = "良";
		}

		if (!"0".equals(downnum)) {
			grade = "差";
		}
		reporthash.put("dbname", dbname);
		reporthash.put("dbnamestr", dbnamestr);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("memvalue", memValue);
		reporthash.put("typename", typename);
		reporthash.put("runstr", runstr);
		reporthash.put("downnum", downnum);
		reporthash.put("dbValue", dbValue);
		reporthash.put("dbnamestr", dbnamestr);
		reporthash.put("count", count + "");
		reporthash.put("grade", grade);
		reporthash.put("ip", ip);
		if (vector == null)
			vector = new Vector();
		reporthash.put("tableinfo_v", vector);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			report.createReport_infor("dbinfor_report.xls");
			request.setAttribute("filename", report.getFileName());
			SysLogger.info("filename" + report.getFileName());
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbinfor_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_inforDoc(fileName);// word综合报表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbinfor_report.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_inforPDF(fileName);// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbinfor_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_inforNewDoc(fileName, "doc");// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbinfor_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径

				report1.createReport_inforNewDoc(fileName, "pdf");// word业务分析表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-08 sqlserver连通率报表打印
	 * @return
	 */
	public String createSServerPingReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());
			DBDao dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getAlias();
			Hashtable statusHash = dbDao.getSqlserver_nmsstatus(serverip);
			String p_status = (String) statusHash.get("status");
			if (p_status != null && p_status.length() > 0) {
				if ("1".equalsIgnoreCase(p_status)) {
					pingnow = "100.0";
				}
			}
			dao.close();
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SQLPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最小连通率
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("avgpingcon", avgpingcon + "");
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax);
		reporthash.put("dbtype", typevo.getDbtype());
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerPing_report.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			// report1.createReport_dbping(fileName);
			// report1.createReportSqlServerPingDoc(fileName);
			report1.createReportPingDoc(fileName);
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerPing_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingPdf(fileName);
			request.setAttribute("filename", fileName);
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerPing_report.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingExcel(fileName);
			request.setAttribute("filename", fileName);
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-08 sqlserver性能报表打印
	 * @return
	 */
	public String createSServerSelfReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		Hashtable dbValue = new Hashtable();
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());
			DBDao dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getAlias();
			Hashtable statusHash = dbDao.getSqlserver_nmsstatus(serverip);
			dbValue = dbDao.getSqlserver_nmsdbvalue(serverip);
			String p_status = (String) statusHash.get("status");
			if (p_status != null && p_status.length() > 0) {
				if ("1".equalsIgnoreCase(p_status)) {
					pingnow = "100.0";
				}
			}
			dao.close();
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SQLPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最小连通率
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率

		reporthash.put("tableinfo_v", dbValue);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		if ("0".equals(str)) {
			report.createReport_sql("/temp/dbsql_report.xls");
			request.setAttribute("filename", report.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbsql_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReport_sqlDoc(fileName);// word综合报表

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbsql_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_sqlPDF(fileName);
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word业务分析表
			request.setAttribute("filename", fileName);
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbsql_reportcheck.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_sqlNewDoc(fileName, "doc");
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// word业务分析表
			request.setAttribute("filename", fileName);
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbsql_reportcheck.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_sqlNewPDF(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// word业务分析表
			request.setAttribute("filename", fileName);
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * createDB2CldReport
	 * 
	 * @author HONGLI date 2010-11-10 sqlserver综合报表打印
	 * @return
	 */
	public String createSServerCldReport() throws DocumentException {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		// 数据库运行等级=====================
		String grade = "优";
		Hashtable mems = new Hashtable();// 内存信息
		Hashtable sysValue = new Hashtable();
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		String ip = "";
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
				if (vo == null) {
					ip = getParaValue("ipaddress");
					vo = (DBVo) dao.findByCondition("ip_address", ip, 2).get(0);
				}
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			// request.setAttribute("db", vo);
			// request.setAttribute("IpAddress", vo.getIpAddress());
			// request.setAttribute("dbtye", typevo.getDbdesc());
			// Hashtable allsqlserverdata = ShareData.getSqlserverdata();
			// Hashtable ipsqlserverdata = new Hashtable();
			//			
			// if(allsqlserverdata != null && allsqlserverdata.size()>0){
			// if(allsqlserverdata.containsKey(vo.getIpAddress())){
			// ipsqlserverdata =
			// (Hashtable)allsqlserverdata.get(vo.getIpAddress());
			// if(ipsqlserverdata.containsKey("status")){
			// String p_status = (String)ipsqlserverdata.get("status");
			// if(p_status != null && p_status.length()>0){
			// if("1".equalsIgnoreCase(p_status)){
			// runstr = "正在运行";
			// pingnow = "100.0";
			// }
			// }
			// }
			// if(ipsqlserverdata.containsKey("dbValue")){
			// dbValue = (Hashtable)ipsqlserverdata.get("dbValue");
			// }
			// if(ipsqlserverdata.containsKey("retValue")){
			// mems =
			// (Hashtable)((Hashtable)ipsqlserverdata.get("retValue")).get("mems");
			// }
			// if(ipsqlserverdata.containsKey("sysValue")){
			// sysValue = (Hashtable)ipsqlserverdata.get("sysValue");
			// }
			// //数据库信息
			// if(ipsqlserverdata.containsKey("dbValue")){
			// dbValue = (Hashtable)ipsqlserverdata.get("dbValue");
			// }
			// }
			// }
			DBDao dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getAlias();
			Hashtable statusHash = dbDao.getSqlserver_nmsstatus(serverip);
			sysValue = dbDao.getSqlserver_nmssysvalue(serverip);
			dbValue = dbDao.getSqlserver_nmsdbvalue(serverip);
			mems = dbDao.getSqlserver_nmsmems(serverip);
			sysValue = dbDao.getSqlserver_nmssysvalue(serverip);
			String p_status = (String) statusHash.get("status");
			if (p_status != null && p_status.length() > 0) {
				if ("1".equalsIgnoreCase(p_status)) {
					runstr = "正在运行";
					pingnow = "100.0";
				}
			}
			dbDao.close();
			String newip = SysUtil.doip(vo.getIpAddress());
			// request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SQLPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}
			pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最小连通率
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			if (count > 0) {
				grade = "良";
			}
			if (!"0".equals(downnum)) {
				grade = "差";
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
		reporthash.put("list", eventList);
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax);
		reporthash.put("pingconavg", avgpingcon + "");
		reporthash.put("sysValue", sysValue);
		reporthash.put("sqlsys", sysValue);
		reporthash.put("mems", mems);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("grade", grade);
		reporthash.put("vo", vo);
		reporthash.put("runstr", runstr);
		reporthash.put("typevo", typevo);
		reporthash.put("dbValue", dbValue);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("tableinfo_v", dbValue);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerCldReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_SqlServerCldDoc(fileName, "doc");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerCldReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_SqlServerCldDoc(fileName, "pdf");
			} catch (IOException e) {
				SysLogger.error("", e);
			}
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerCldReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_SqlServerCldXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 sqlserver 事件报表打印
	 * @return
	 */
	public String createSServerEventReport() {
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
		String downnum = "";
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SQLPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		reporthash.put("list", eventList);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("vo", vo);
		reporthash.put("typevo", typevo);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventDoc(fileName);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventPdf(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSqlServerEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 DB2连通率报表打印
	 * @return
	 */
	public String createDB2PingReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String sip = hex + ":" + vo.getId();
			dao = new DBDao();
			Hashtable monitorValue = null;
			Hashtable alldb2data = null;
			Hashtable ipdb2data = new Hashtable();
			try {
				monitorValue = dao.getDB2DataByServerip(sip);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			if (monitorValue != null && monitorValue.containsKey("allDb2Data")) {
				ipdb2data = (Hashtable) monitorValue.get("allDb2Data");
			}
			if (alldb2data != null && alldb2data.size() > 0) {
				if (alldb2data.containsKey(vo.getIpAddress())) {
					ipdb2data = (Hashtable) alldb2data.get(vo.getIpAddress());
					if (ipdb2data.containsKey("status")) {
						String p_status = (String) ipdb2data.get("status");
						if (p_status != null && p_status.length() > 0) {
							if ("1".equalsIgnoreCase(p_status)) {
								pingnow = "100.0";
							}
						}
					}
				}
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "DB2Ping", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("avgpingcon", avgpingcon + "");
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax);
		reporthash.put("dbtype", typevo.getDbtype());
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2Ping_report.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			// report1.createReport_dbping(fileName);
			report1.createReportPingDoc(fileName);
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2Ping_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingPdf(fileName);
			request.setAttribute("filename", fileName);
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2Ping_report.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingExcel(fileName);
			request.setAttribute("filename", fileName);
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-08 DB2性能报表打印
	 * @return
	 */
	public String createDB2SelfReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		Hashtable spaceInfo = new Hashtable();
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String sip = hex + ":" + vo.getId();
			dao = new DBDao();
			Hashtable monitorValue = null;
			Hashtable alldb2data = null;
			Hashtable ipdb2data = new Hashtable();
			try {
				monitorValue = dao.getDB2DataByServerip(sip);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			if (monitorValue != null && monitorValue.containsKey("allDb2Data")) {
				alldb2data = (Hashtable) monitorValue.get("allDb2Data");
			}
			if (alldb2data != null && alldb2data.size() > 0) {
				if (alldb2data.containsKey(vo.getIpAddress())) {
					ipdb2data = (Hashtable) alldb2data.get(vo.getIpAddress());
					if (ipdb2data.containsKey("status")) {
						String p_status = (String) ipdb2data.get("status");
						if (p_status != null && p_status.length() > 0) {
							if ("1".equalsIgnoreCase(p_status)) {
								pingnow = "100.0";
							}
						}
					}
					if (ipdb2data.containsKey("spaceInfo")) {
						spaceInfo = (Hashtable) ipdb2data.get("spaceInfo");
					}
				}
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "DB2Ping", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率

		reporthash.put("spaceInfo", spaceInfo);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			report1.createReportDB2SelfExcel("/temp/dbDB2Self_report.xls");// createReportDB2SelfExcel
			request.setAttribute("filename", report1.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbDB2Self_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReportDB2SelfDoc(fileName);// word性能报表
				// createReportDB2SelfDoc

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2Self_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReportDB2SelfPdf(fileName);// createReportDB2SelfPdf
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// pdf性能报表
			request.setAttribute("filename", fileName);
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-10 db2综合报表打印
	 * @return
	 */
	public String createDB2CldReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		// 数据库运行等级=====================
		String grade = "优";
		Hashtable mems = new Hashtable();// 内存信息
		Hashtable sysValue = new Hashtable();
		Hashtable spaceInfo = new Hashtable();
		Hashtable conn = new Hashtable();
		Hashtable poolInfo = new Hashtable();
		Hashtable log = new Hashtable();
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		String ip = "";
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
				if (vo == null) {
					ip = getParaValue("ipaddress");
					vo = (DBVo) dao.findByCondition("ip_address", ip, 5).get(0);
				}
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String sip = hex + ":" + vo.getId();
			dao = new DBDao();
			Hashtable monitorValue = null;
			Hashtable alldb2data = null;
			Hashtable ipdb2data = new Hashtable();
			try {
				monitorValue = dao.getDB2DataByServerip(sip);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			if (monitorValue != null && monitorValue.containsKey("allDb2Data")) {
				alldb2data = (Hashtable) monitorValue.get("allDb2Data");
			}
			if (alldb2data != null && alldb2data.size() > 0) {
				if (alldb2data.containsKey(vo.getIpAddress())) {
					ipdb2data = (Hashtable) alldb2data.get(vo.getIpAddress());
					if (ipdb2data.containsKey("status")) {
						String p_status = (String) ipdb2data.get("status");
						if (p_status != null && p_status.length() > 0) {
							if ("1".equalsIgnoreCase(p_status)) {
								runstr = "正在运行";
								pingnow = "100.0";
							}
						}
					}
					if (ipdb2data.containsKey("dbValue")) {
						dbValue = (Hashtable) ipdb2data.get("dbValue");
					}
					if (ipdb2data.containsKey("retValue")) {
						mems = (Hashtable) ((Hashtable) ipdb2data.get("retValue")).get("mems");
					}
					if (ipdb2data.containsKey("sysValue")) {
						sysValue = (Hashtable) ipdb2data.get("sysValue");
					}
					if (ipdb2data.containsKey("spaceInfo")) {
						spaceInfo = (Hashtable) ipdb2data.get("spaceInfo");
					}
					if (ipdb2data.containsKey("conn")) {
						conn = (Hashtable) ipdb2data.get("conn");
					}
					if (ipdb2data.containsKey("poolInfo")) {
						poolInfo = (Hashtable) ipdb2data.get("poolInfo");
					}
					if (ipdb2data.containsKey("log")) {
						log = (Hashtable) ipdb2data.get("log");
					}
				}
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			// request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "DB2Ping", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "0";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			if (count > 0) {
				grade = "良";
			}
			if (!"0".equals(downnum)) {
				grade = "差";
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		reporthash.put("list", eventList);
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax);
		reporthash.put("pingconavg", avgpingcon + "");
		reporthash.put("sysValue", sysValue);
		reporthash.put("spaceInfo", spaceInfo);
		reporthash.put("conn", conn);
		reporthash.put("poolInfo", poolInfo);
		reporthash.put("log", log);
		reporthash.put("sqlsys", sysValue);
		reporthash.put("mems", mems);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("grade", grade);
		reporthash.put("vo", vo);
		reporthash.put("runstr", runstr);
		reporthash.put("typevo", typevo);
		reporthash.put("dbValue", dbValue);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("tableinfo_v", dbValue);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2CldReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_DB2CldDoc(fileName, "doc");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2CldReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_DB2CldDoc(fileName, "pdf");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2CldReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_DB2CldXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 DB2 事件报表打印
	 * @return
	 */
	public String createDB2EventReport() {
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
		String downnum = "";
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "DB2Ping", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		reporthash.put("list", eventList);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("vo", vo);
		reporthash.put("typevo", typevo);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2EventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventDoc(fileName);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2EventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventPdf(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbDB2EventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 sybase连通率报表打印
	 * @return
	 */
	public String createSybasePingReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());
			// 获取sybase信息
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex + ":" + vo.getId();
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
				status = (String) tempStatusHashtable.get("status");
			}
			if (status.equals("1")) {
				pingnow = "100.0";
			}
			if (dao != null) {
				dao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SYSPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			if (pingmin != null) {
				pingmin = pingmin.replace("%", "");// 最小连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("avgpingcon", avgpingcon + "");
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax + "");
		reporthash.put("dbtype", typevo.getDbtype());
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybasePing_report.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			// report1.createReport_dbping(fileName);
			report1.createReportPingDoc(fileName);
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybasePing_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingPdf(fileName);
			request.setAttribute("filename", fileName);
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybasePing_report.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingExcel(fileName);
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 sybase性能报表打印
	 * @return
	 */
	public String createSybaseSelfReport() {
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
		// request.setAttribute("startdate", starttime);
		// request.setAttribute("todate", totime);
		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		String id = (String) session.getAttribute("id");
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		Hashtable dbValue = new Hashtable();
		SybaseVO sysbaseVO = new SybaseVO();
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex + ":" + vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip);
			String status = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
				status = (String) tempStatusHashtable.get("status");
			}
			if (status.equals("1")) {
				pingnow = "100.0";
			}
			if (dao != null) {
				dao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SYSPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
		reporthash.put("sysbaseVO", sysbaseVO);
		reporthash.put("tableinfo_v", dbValue);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			report1.createReportSybaseSelfExcel("/temp/dbSybaseSelf_report.xls");// createReportDB2SelfExcel
			request.setAttribute("filename", report1.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbSybaseSelf_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReportSybaseSelfDoc(fileName);// word性能报表
				// createReportDB2SelfDoc

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybaseSelf_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReportSybaseSelfPdf(fileName);// createReportDB2SelfPdf
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// pdf性能报表
			request.setAttribute("filename", fileName);
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 sybase综合报表打印
	 * @return
	 */
	public String createSybaseCldReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		String grade = "优";
		Hashtable mems = new Hashtable();// 内存信息
		Hashtable sysValue = new Hashtable();
		SybaseVO sysbaseVO = new SybaseVO();
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		String ip = "";
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
				if (vo == null) {
					ip = getParaValue("ipaddress");
					vo = (DBVo) dao.findByCondition("ip_address", ip, 6).get(0);
				}
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			dao = new DBDao();
			String serverip = hex + ":" + vo.getId();
			sysbaseVO = dao.getSybaseDataByServerip(serverip);
			String statusStr = "0";
			Hashtable tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
				statusStr = (String) tempStatusHashtable.get("status");
			}
			if (statusStr.equals("1")) {
				runstr = "正在运行";
				pingnow = "100.0";
			}
			if (dao != null) {
				dao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			// request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SYSPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "0";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			if (count > 0) {
				grade = "良";
			}
			if (!"0".equals(downnum)) {
				grade = "差";
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		reporthash.put("sysbaseVO", sysbaseVO);
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
		reporthash.put("list", eventList);
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax);
		reporthash.put("pingconavg", avgpingcon + "");
		reporthash.put("sysValue", sysValue);
		reporthash.put("sqlsys", sysValue);
		reporthash.put("mems", mems);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("grade", grade);
		reporthash.put("vo", vo);
		reporthash.put("runstr", runstr);
		reporthash.put("typevo", typevo);
		reporthash.put("dbValue", dbValue);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("tableinfo_v", dbValue);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybaseCldReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_SybaseCldDoc(fileName);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybaseCldReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_SybaseCldPdf(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybaseCldReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_SybaseCldXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 sybase 事件报表打印
	 * @return
	 */
	public String createSybaseEventReport() {
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
		String downnum = "";
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "SYSPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		reporthash.put("list", eventList);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("vo", vo);
		reporthash.put("typevo", typevo);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybaseEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventDoc(fileName);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybaseEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventPdf(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbSybaseEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-12 Informix连通率报表打印
	 * @return
	 */
	public String createInformixPingReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());
			// Hashtable informixData=new Hashtable();
			// Hashtable mino=new Hashtable();
			// Hashtable sysValue = new Hashtable();
			// sysValue = ShareData.getInformixmonitordata();
			// if(sysValue!=null&&sysValue.size()>0){
			// if(sysValue.containsKey(vo.getIpAddress())){
			// mino=(Hashtable)sysValue.get(vo.getIpAddress());
			// if(mino.contains(vo.getDbName())){
			// informixData=(Hashtable)mino.get(vo.getDbName());
			// }
			// if(informixData.containsKey("status")){
			// String p_status = (String)informixData.get("status");
			// if(p_status != null && p_status.length()>0){
			// if("1".equalsIgnoreCase(p_status)){
			// // runstr = "正在运行";
			// pingnow = "100";
			// }
			// }
			// }
			// }
			// }
			dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getDbName();
			String status = String.valueOf(((Hashtable) dao.getInformix_nmsstatus(serverip)).get("status"));
			dao.close();
			if ("1".equalsIgnoreCase(status)) {
				pingnow = "100";
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "INFORMIXPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			if (pingmin != null) {
				pingmin = pingmin.replace("%", "");// 最小连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("avgpingcon", avgpingcon + "");
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax + "");
		reporthash.put("dbtype", typevo.getDbtype());
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixPing_report.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			// report1.createReport_dbping(fileName);
			report1.createReportPingDoc(fileName);
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixPing_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingPdf(fileName);
			request.setAttribute("filename", fileName);
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixPing_report.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingExcel(fileName);
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-12 Informix性能报表打印
	 * @return
	 */
	public String createInformixSelfReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		Hashtable dbValue = new Hashtable();
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());

			// Hashtable informixData=new Hashtable();
			// Hashtable mino=new Hashtable();
			// Hashtable sysValue = new Hashtable();
			// sysValue = ShareData.getInformixmonitordata();
			// if(sysValue!=null&&sysValue.size()>0){
			// if(sysValue.containsKey(vo.getIpAddress())){
			// mino=(Hashtable)sysValue.get(vo.getIpAddress());
			// if(mino.contains(vo.getDbName())){
			// informixData=(Hashtable)mino.get(vo.getDbName());
			// }
			// if(informixData.containsKey("status")){
			// String p_status = (String)informixData.get("status");
			// if(p_status != null && p_status.length()>0){
			// if("1".equalsIgnoreCase(p_status)){
			// // runstr = "正在运行";
			// pingnow = "100";
			// }
			// }
			// }
			// if(informixData.containsKey("informix")){
			// dbValue = (Hashtable)informixData.get("informix");
			// }
			// }
			// }
			dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getDbName();
			String status = String.valueOf(((Hashtable) dao.getInformix_nmsstatus(serverip)).get("status"));
			List sessionList = dao.getInformix_nmssession(serverip);
			List lockList = dao.getInformix_nmslock(serverip);
			List spaceList = dao.getInformix_nmsspace(serverip);
			dao.close();
			if ("1".equalsIgnoreCase(status)) {
				pingnow = "100";
			}
			dbValue.put("sessionList", sessionList);
			dbValue.put("lockList", lockList);
			dbValue.put("informixspaces", spaceList);

			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "INFORMIXPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率

		reporthash.put("dbValue", dbValue);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			report1.createReportInformixSelfExcel("/temp/dbInformixSelf_report.xls");// createReportDB2SelfExcel
			request.setAttribute("filename", report1.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbInformixSelf_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReportInformixSelfDoc(fileName);// word性能报表
				// createReportDB2SelfDoc

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixSelf_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReportInformixSelfPdf(fileName);// createReportDB2SelfPdf
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// pdf性能报表
			request.setAttribute("filename", fileName);
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-12 Informix综合报表打印
	 * @return
	 */
	public String createInformixCldReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		// 数据库运行等级=====================
		String grade = "优";
		Hashtable mems = new Hashtable();// 内存信息
		Hashtable sysValue = new Hashtable();
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		String ip = "";
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
				if (vo == null) {
					ip = getParaValue("ipaddress");
					vo = (DBVo) dao.findByCondition("ip_address", ip, 7).get(0);
				}
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getDbName();
			String statusStr = String.valueOf(((Hashtable) dao.getInformix_nmsstatus(serverip)).get("status"));
			List sessionList = dao.getInformix_nmssession(serverip);
			List lockList = dao.getInformix_nmslock(serverip);
			List logList = dao.getInformix_nmslog(serverip);
			List spaceList = dao.getInformix_nmsspace(serverip);
			List ioList = dao.getInformix_nmsio(serverip);
			dao.close();
			if ("1".equalsIgnoreCase(statusStr)) {
				runstr = "正在运行";
				pingnow = "100";
			}
			dbValue.put("sessionList", sessionList);
			dbValue.put("lockList", lockList);
			dbValue.put("informixspaces", spaceList);
			dbValue.put("informixlog", logList);
			dbValue.put("iolist", ioList);
			String newip = SysUtil.doip(vo.getIpAddress());
			// request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "INFORMIXPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "0";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			if (count > 0) {
				grade = "良";
			}
			if (!"0".equals(downnum)) {
				grade = "差";
			}
			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		reporthash.put("list", eventList);
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax);
		reporthash.put("pingconavg", avgpingcon + "");
		reporthash.put("sysValue", sysValue);
		reporthash.put("sqlsys", sysValue);
		reporthash.put("mems", mems);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("grade", grade);
		reporthash.put("vo", vo);
		reporthash.put("runstr", runstr);
		reporthash.put("typevo", typevo);
		reporthash.put("dbValue", dbValue);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("dbValue", dbValue);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixCldReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_InformixCldDoc(fileName, "doc");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixCldReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_InformixCldDoc(fileName, "pdf");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixCldReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_InformixCldXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-12 Informix 事件报表打印
	 * @return
	 */
	public String createInformixEventReport() {
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
		String downnum = "";
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "INFORMIXPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		reporthash.put("list", eventList);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("vo", vo);
		reporthash.put("typevo", typevo);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventDoc(fileName);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventPdf(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbInformixEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 MySQL连通率报表打印
	 * @return
	 */
	public String createMySQLPingReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());
			// Hashtable allData = ShareData.getMySqlmonitordata();
			// Hashtable ipData = ShareData.getMySqlmonitordata();
			// if(allData != null && allData.size()>0){
			// ipData = (Hashtable)allData.get(vo.getIpAddress());
			// if(ipData != null && ipData.size()>0){
			// String runstr = (String)ipData.get("runningflag");
			// if("正在运行".equals(runstr)){
			// pingnow = "100";
			// }
			// }
			// }
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			Hashtable statusHashtable = dao.getMysql_nmsstatus(serverip);
			String status = (String) statusHashtable.get("status");
			if ("1".equals(status)) {
				pingnow = "100";
			}
			if (dao != null) {
				dao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "MYPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("avgpingcon", avgpingcon + "");
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax);
		reporthash.put("dbtype", typevo.getDbtype());
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLPing_report.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			// report1.createReport_dbping(fileName);
			report1.createReportPingDoc(fileName);
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLPing_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingPdf(fileName);
			request.setAttribute("filename", fileName);
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLPing_report.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			report1.createReportPingExcel(fileName);
			request.setAttribute("filename", fileName);
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-08 MySQL性能报表打印
	 * @return
	 */
	public String createMySQLSelfReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		Vector Val = new Vector();
		int doneFlag = 0;
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();

			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			request.setAttribute("db", vo);
			request.setAttribute("IpAddress", vo.getIpAddress());
			request.setAttribute("dbtye", typevo.getDbdesc());
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			Hashtable ipData = dao.getMysqlDataByServerip(serverip);
			if (dao != null) {
				dao.close();
			}
			if (ipData != null && ipData.size() > 0) {
				String dbnames = vo.getDbName();
				String[] dbs = dbnames.split(",");
				for (int k = 0; k < dbs.length; k++) {
					// 判断是否已经获取了当前的配置信息
					// if(doneFlag == 1)break;
					String dbStr = dbs[k];
					if (ipData.containsKey(dbStr)) {
						Hashtable returnValue = new Hashtable();
						returnValue = (Hashtable) ipData.get(dbStr);
						if (returnValue != null && returnValue.size() > 0) {
							if (doneFlag == 0) {
								// 判断是否已经获取了当前的配置信息
								if (returnValue.containsKey("configVal")) {
									doneFlag = 1;
								}
								if (returnValue.containsKey("Val")) {
									Val = (Vector) returnValue.get("Val");
								}
							}
						}
					}
				}

				String runstr = (String) ipData.get("runningflag");
				if ("正在运行".equals(runstr)) {
					pingnow = "100";
				}
			}
			// }
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newIp", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "MYPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率

		reporthash.put("Val", Val);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());
		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			report1.createReportMySQLSelfExcel("/temp/dbMySQLSelf_report.xls");// createReportMySQLSelfExcel
			request.setAttribute("filename", report1.getFileName());
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			try {
				String file = "temp/dbMySQLSelf_report.doc";// 保存到项目文件夹下的指定文件夹
				String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
				report1.createReportMySQLSelfDoc(fileName);// word性能报表
				// createReportMySQLSelfDoc

				request.setAttribute("filename", fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLSelf_report.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReportMySQLSelfPdf(fileName);// createReportMySQLSelfPdf
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// pdf性能报表
			request.setAttribute("filename", fileName);
		}

		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-10 MySQL综合报表打印
	 * @return
	 */
	public String createMySQLCldReport() {
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
		double avgpingcon = 0;
		String pingnow = "0.0";// 当前连通率
		String pingmin = "0.0";// 最小连通率
		String pingmax = "0.0";// 最大连通率
		String runstr = "服务停止";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		String grade = "优";
		int count = 0;
		Vector Val = new Vector();
		int doneFlag = 0;
		List sessionlist = new ArrayList();
		Hashtable tablesHash = new Hashtable();
		Vector tableinfo_v = new Vector();
		List eventList = new ArrayList();// 事件列表
		String ip = "";
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
				if (vo == null) {
					ip = getParaValue("ipaddress");
					vo = (DBVo) dao.findByCondition("ip_address", ip, 4).get(0);
				}
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			Hashtable ipData = dao.getMysqlDataByServerip(serverip);
			if (dao != null) {
				dao.close();
			}
			if (ipData != null && ipData.size() > 0) {
				String dbnames = vo.getDbName();
				String[] dbs = dbnames.split(",");
				for (int k = 0; k < dbs.length; k++) {
					// 判断是否已经获取了当前的配置信息
					String dbStr = dbs[k];
					if (ipData.containsKey(dbStr)) {
						Hashtable returnValue = new Hashtable();
						returnValue = (Hashtable) ipData.get(dbStr);
						if (returnValue != null && returnValue.size() > 0) {
							if (doneFlag == 0) {
								// 判断是否已经获取了当前的配置信息
								if (returnValue.containsKey("configVal")) {
									doneFlag = 1;
								}
								if (returnValue.containsKey("Val")) {
									Val = (Vector) returnValue.get("Val");
								}
							}
							if (returnValue.containsKey("sessionsDetail")) {
								// 存在数据库连接信息
								sessionlist.add((List) returnValue.get("sessionsDetail"));
							}
							if (returnValue.containsKey("tablesDetail")) {
								// 存在数据库表信息
								tablesHash.put(dbStr, (List) returnValue.get("tablesDetail"));
							}
							if (returnValue.containsKey("tablesDetail")) {
								// 存在数据库表信息
								tableinfo_v = (Vector) returnValue.get("variables");
							}
						}
					}
				}

				runstr = (String) ipData.get("runningflag");
				if (runstr != null && runstr.contains("服务停止")) { 
					runstr = "服务停止";
				}
				if (runstr != null && runstr.contains("正在运行")) {
					pingnow = "100";
				}
			}
			// }
			String newip = SysUtil.doip(vo.getIpAddress());
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "MYPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "0";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// 平均连通率
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// 最大连通率
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// 最大连通率
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "连通率", newip + "ConnectUtilization", 740, 150);// 画图

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			if (count > 0) {
				grade = "良";
			}
			if (!"0".equals(downnum)) {
				grade = "差";
			}
			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		maxping.put("pingmax", pingmin + "%");// 最小连通率
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// 平均连通率
		reporthash.put("list", eventList);
		reporthash.put("pingmin", pingmin);
		reporthash.put("pingnow", pingnow);
		reporthash.put("pingmax", pingmax);
		reporthash.put("pingconavg", avgpingcon + "");
		reporthash.put("tablesHash", tablesHash);
		reporthash.put("sessionlist", sessionlist);
		reporthash.put("Val", Val);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("grade", grade);
		reporthash.put("vo", vo);
		reporthash.put("runstr", runstr);
		reporthash.put("typevo", typevo);
		reporthash.put("dbValue", dbValue);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("tableinfo_v", tableinfo_v);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLCldReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_MySQLCldDoc(fileName, "doc");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLCldReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_MySQLCldDoc(fileName, "pdf");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// pdf综合报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLCldReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_MySQLCldXls(fileName);
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word综合报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-11 MySQL 事件报表打印
	 * @return
	 */
	public String createMySQLEventReport() {
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
		String downnum = "";
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				typedao.close();
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			request.setAttribute("newip", newip);
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager.getCategory(vo.getId() + "", "MYPing", "ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}

			// 得到运行等级
			DBTypeDao dbTypeDao = new DBTypeDao();

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		reporthash.put("list", eventList);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("vo", vo);
		reporthash.put("typevo", typevo);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventDoc(fileName);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventPdf(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbMySQLEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

	/**
	 * @author HONGLI date 2010-11-17 oracle 事件报表打印
	 * @return
	 */
	public String createOracleEventReport() {
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
		int count = 0;
		List eventList = new ArrayList();// 事件列表
		try {
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(id);
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dao.close();
			}
			DBTypeDao typedao = new DBTypeDao();
			try {
				typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
			} catch (Exception e) {
				SysLogger.error("", e);
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

			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			// 事件列表
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			// request.setAttribute("status", status);
			// request.setAttribute("level1", level1);
			try {
				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER); // 用户姓名
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db", status + "", level1 + "", user.getBusinessids(), Integer.parseInt(sid));
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
				// ConnectUtilizationhash =
				// hostmanager.getCategory(host.getIpAddress(),"Ping","ConnectUtilization",starttime1,totime1);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		Hashtable reporthash = new Hashtable();
		Hashtable maxping = new Hashtable();
		reporthash.put("list", eventList);
		reporthash.put("downnum", downnum);
		reporthash.put("count", count);
		reporthash.put("vo", vo);
		reporthash.put("typevo", typevo);
		reporthash.put("typename", typevo.getDbtype());
		reporthash.put("hostnamestr", vo.getDbName());
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("ping", maxping);
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
		reporthash.put("ip", vo.getIpAddress());

		String str = request.getParameter("str");// 从页面返回设定的str值进行判断，生成excel报表或者word报表
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbOracleEventReport.doc";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventDoc(fileName);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbOracleEventReport.pdf";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventPdf(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// pdf事件报表分析表
			request.setAttribute("filename", fileName);
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(), reporthash);
			String file = "temp/dbOracleEventReport.xls";// 保存到项目文件夹下的指定文件夹
			String fileName = ResourceCenter.getInstance().getSysPath() + file;// 获取系统文件夹路径
			try {
				report1.createReport_EventXls(fileName);
			} catch (DocumentException e) {

				SysLogger.error("", e);
			} catch (IOException e) {

				SysLogger.error("", e);
			}// xls事件报表分析表
			request.setAttribute("filename", fileName);
		}
		return "/capreport/db/download.jsp";
	}

}
