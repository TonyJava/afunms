package com.afunms.application.ajaxManager;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
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
import com.afunms.application.util.IpTranslation;
import com.afunms.application.util.ReportExport;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.model.ReportValue;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.ChartGraph;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.detail.service.cpuInfo.CpuInfoService;
import com.afunms.detail.service.diskInfo.DiskInfoService;
import com.afunms.detail.service.memoryInfo.MemoryInfoService;
import com.afunms.detail.service.pingInfo.PingInfoService;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.manage.PollMonitorManager;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.Pingcollectdata;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.lowagie.text.BadElementException;
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
import com.lowagie.text.pdf.codec.postscript.ParseException;
import com.lowagie.text.rtf.RtfWriter2;

public class DatabaseReportAjaxManager extends AjaxBaseManager implements
		AjaxManagerInterface {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	I_HostCollectData hostmanager = new HostCollectDataManager();
	I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();

	@Override
	public void execute(String action) {
		if (action.equals("getNodeListForPing")) {
			getNodeListForPing();
		} else if (action.equals("exportDatabasePingWord")) {
			exportDatabasePingWord();
		} else if (action.equals("exportDatabasePingExcel")) {
			exportDatabasePingExcel();
		} else if (action.equals("exportDatabasePingPdf")) {
			exportDatabasePingPdf();
		} else if (action.equals("getEventDetailList")) {
			getEventDetailList();
		} else if (action.equals("exportEventWord")) {
			exportEventWord();
		} else if (action.equals("exportEventExcel")) {
			exportEventExcel();
		} else if (action.equals("exportEventPdf")) {
			exportEventPdf();
		} else if (action.equals("exportDatabaseReportForOracle")) {
			exportDatabaseReportForOracle();
		} else if (action.equals("exportDatabaseReportForMysql")) {
			exportDatabaseReportForMysql();
		} else if (action.equals("exportOracleReportForType")) {
			exportOracleReportForType();
		} else if (action.equals("getPingDetailForDatabase")) {
			getPingDetailForDatabase();
		} else if (action.equals("exportMysqlReportForType")) {
			exportMysqlReportForType();
		} else if (action.equals("exportMysqlReportForAnalyse")) {
			exportMysqlReportForAnalyse();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getPingDetailForDatabase() {
		// ���ɱ�������Ҫ������
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String idss = getParaValue("ids");
		String[] ids = {};
		if (idss != null) {
			ids = idss.split(";");
		}
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null
				&& !getParaValue("orderflag").equals("")) {
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
						pinghash = hostmanager.getCategory(id, "ORAPing",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
						pinghash = hostmanager.getCategory(id, "SQLPing",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
						pinghash = hostmanager.getCategory(id, "DB2Ping",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {
						pinghash = hostmanager.getCategory(id, "SYSPing",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
						pinghash = hostmanager.getCategory(id, "INFORMIXPing",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {// HONGLI
						pinghash = hostmanager.getCategory(id, "MYPing",
								"ConnectUtilization", starttime, totime);
					}
				} catch (Exception e) {
					SysLogger.error("", e);
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("dbvo", vo);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("ipaddress",
						vo.getIpAddress() + "(" + vo.getAlias() + ")");
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping")
				|| orderflag.equalsIgnoreCase("downnum")) {
			returnList = (List) session.getAttribute("pinglist");
		} else {
			// ��orderList����theValue��������
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
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype()
								+ "");
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
						if (new Double(avgping.substring(0,
								avgping.length() - 2)).doubleValue() < new Double(
								_avgping.substring(0, _avgping.length() - 2))
								.doubleValue()) {
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
						if (new Double(downnum).doubleValue() < new Double(
								_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}
		//
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList,
				"ping");
		String pingpath = new ReportExport().makeJfreeChartData(
				pingReportValue.getListValue(), pingReportValue.getIpList(),
				"��ͨ��", "ʱ��", "");
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("pinglist", list);
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		StringBuffer jsonString = new StringBuffer("[{");
		jsonString.append("pic:[");
		jsonString.append("{\"pingChartDivStr\":\"");
		jsonString.append(pingChartDivStr);
		jsonString.append("\"}]");
		StringBuffer jsonStringNode = new StringBuffer("node:[{Rows:[");
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				List _pinglist = (List) list.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String dbname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String avgping = (String) _pinglist.get(4);
				String downnum = (String) _pinglist.get(5);

				jsonStringNode.append("{\"nodeid\":\"");
				jsonStringNode.append("");
				jsonStringNode.append("\",");

				jsonStringNode.append("\"ipaddress\":\"");
				jsonStringNode.append(ip);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"dbtype\":\"");
				jsonStringNode.append(dbtype);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"dbname\":\"");
				jsonStringNode.append(dbname);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"dbuse\":\"");
				jsonStringNode.append(dbuse);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"avg\":\"");
				jsonStringNode.append(avgping);
				jsonStringNode.append("\",");

				jsonStringNode.append("\"downnum\":\"");
				jsonStringNode.append(downnum);
				jsonStringNode.append("\"}");

				if (i != list.size() - 1) {
					jsonStringNode.append(",");
				}
			}
		}
		jsonStringNode.append("]}]");
		jsonString.append(",");
		jsonString.append(jsonStringNode);
		jsonString.append("}]");
		// jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private void exportMysqlReportForAnalyse() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		int downnum = 0;
		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// ��ǰ��ͨ��
		String pingmin = "0.0";// ��С��ͨ��
		String pingmax = "0.0";// �����ͨ��
		String runstr = "����ֹͣ";
		Hashtable dbValue = new Hashtable();
		// ���ݿ����еȼ�=====================
		String grade = "��";
		Hashtable mems = new Hashtable();// �ڴ���Ϣ
		Hashtable sysValue = new Hashtable();
		Hashtable spaceInfo = new Hashtable();
		Hashtable conn = new Hashtable();
		Hashtable poolInfo = new Hashtable();
		Hashtable log = new Hashtable();
		int count = 0;
		Vector Val = new Vector();
		int doneFlag = 0;
		List sessionlist = new ArrayList();
		Hashtable tablesHash = new Hashtable();
		Vector tableinfo_v = new Vector();
		List eventList = new ArrayList();// �¼��б�
		String fileName = "";
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
			String newip = SysUtil.doip(ip);
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
					// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
					String dbStr = dbs[k];
					if (ipData.containsKey(dbStr)) {
						Hashtable returnValue = new Hashtable();
						returnValue = (Hashtable) ipData.get(dbStr);
						if (returnValue != null && returnValue.size() > 0) {
							if (doneFlag == 0) {
								// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
								if (returnValue.containsKey("configVal")) {
									doneFlag = 1;
								}
								if (returnValue.containsKey("Val")) {
									Val = (Vector) returnValue.get("Val");
								}
							}
							if (returnValue.containsKey("sessionsDetail")) {
								// �������ݿ�������Ϣ
								sessionlist.add((List) returnValue
										.get("sessionsDetail"));
							}
							if (returnValue.containsKey("tablesDetail")) {
								// �������ݿ����Ϣ
								tablesHash.put(dbStr,
										(List) returnValue.get("tablesDetail"));
							}
							if (returnValue.containsKey("global_status")) {
								// �������ݿ����Ϣ
								tableinfo_v = (Vector) returnValue
										.get("global_status");
							}
						}
					}
				}

				runstr = (String) ipData.get("runningflag");
				if (runstr != null && runstr.contains("����ֹͣ")) {// ��<font
					runstr = "����ֹͣ";
				}
				if (runstr != null && runstr.contains("��������")) {
					pingnow = "100";
				}
			}
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager
						.getCategory(vo.getId() + "", "MYPing",
								"ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "0";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// ƽ����ͨ��
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = Integer.parseInt((String) ConnectUtilizationhash
						.get("downnum"));
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// �����ͨ��
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// �����ͨ��
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();
			p_draw_line(ConnectUtilizationhash, "��ͨ��", newip
					+ "ConnectUtilization", 740, 150);// ��ͼ
			// �õ����еȼ�
			DBTypeDao dbTypeDao = new DBTypeDao();
			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}
			// alex
			StringBuffer s = new StringBuffer();
			s.append("select * from system_eventlist where recordtime>= '"
					+ starttime + "' " + "and recordtime<='" + totime + "' ");
			s.append(" and nodeid=" + vo.getId());

			EventListDao eventdao1 = new EventListDao();
			List infolist = eventdao1.findByCriteria(s.toString());

			if (infolist != null && infolist.size() > 0) {

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (eventlist.getSubentity().equalsIgnoreCase("ping")) {
						downnum = downnum + 1;
					}
				}
			}
			if (count > 0 && count <= 3) {

				grade = "��";
			}
			if (downnum > 0 || count > 3) {
				grade = "��";
			}
			// �¼��б�
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			try {
				User user = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db",
							status + "", level1 + "", user.getBusinessids(),
							vo.getId());
				} catch (Exception e) {
					SysLogger.error("", e);
				} finally {
					eventdao.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Hashtable reporthash = new Hashtable();
			Hashtable maxping = new Hashtable();
			maxping.put("pingmax", pingmin + "%");// ��С��ͨ��
			maxping.put("pingnow", pingnow + "%");
			maxping.put("avgpingcon", avgpingcon + "%");// ƽ����ͨ��
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
			reporthash.put("dbname",
					typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
			reporthash.put("ip", vo.getIpAddress());

			String str = request.getParameter("str");// ��ҳ�淵���趨��strֵ�����жϣ�����excel�������word����
			if ("3".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				try {
					String file = "temp/dbmysql_reportcheck.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_mysqlNewDoc(fileName, "doc");// wordҵ�������
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			} else if ("4".equals(str)) {
				ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
						reporthash);
				try {
					String file = "temp/dbmysql_reportcheck.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
					fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
					report1.createReport_mysqlNewDoc(fileName, "pdf");// wordҵ�������
				} catch (DocumentException e) {
					SysLogger.error("", e);
				} catch (IOException e) {
					SysLogger.error("", e);
				}
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private void exportMysqlReportForType() {
		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf0.format(d);
		} else {
			try {
				startdate = sdf0.format(sdf.parse(getParaValue("startdate")));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf0.format(d);
		} else {
			try {
				todate = sdf0.format(sdf.parse(getParaValue("todate")));
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		String pingnow = "0.0";// ��ǰ��ͨ��
		String pingmin = "0.0";// ��С��ͨ��
		String pingmax = "0.0";// �����ͨ��
		String runstr = "����ֹͣ";
		Hashtable dbValue = new Hashtable();
		String downnum = "";
		// ���ݿ����еȼ�=====================
		String grade = "��";
		Hashtable mems = new Hashtable();// �ڴ���Ϣ
		Hashtable sysValue = new Hashtable();
		Hashtable spaceInfo = new Hashtable();
		Hashtable conn = new Hashtable();
		Hashtable poolInfo = new Hashtable();
		Hashtable log = new Hashtable();
		int count = 0;
		Vector Val = new Vector();
		int doneFlag = 0;
		List sessionlist = new ArrayList();
		Hashtable tablesHash = new Hashtable();
		Vector tableinfo_v = new Vector();
		List eventList = new ArrayList();// �¼��б�
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
			IpTranslation tranfer = new IpTranslation();
			String hex = tranfer.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			Hashtable ipData = dao.getMysqlDataByServerip(serverip);
			if (dao != null) {
				dao.close();
			}
			if (ipData != null && ipData.size() > 0) {
				String dbnames = vo.getDbName();
				String[] dbs = dbnames.split(",");
				for (int k = 0; k < dbs.length; k++) {
					// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
					// if(doneFlag == 1)break;
					String dbStr = dbs[k];
					if (ipData.containsKey(dbStr)) {
						Hashtable returnValue = new Hashtable();
						returnValue = (Hashtable) ipData.get(dbStr);
						if (returnValue != null && returnValue.size() > 0) {
							if (doneFlag == 0) {
								// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
								if (returnValue.containsKey("configVal")) {
									doneFlag = 1;
								}
								if (returnValue.containsKey("Val")) {
									Val = (Vector) returnValue.get("Val");
								}
							}
							if (returnValue.containsKey("sessionsDetail")) {
								// �������ݿ�������Ϣ
								sessionlist.add((List) returnValue
										.get("sessionsDetail"));
							}
							if (returnValue.containsKey("tablesDetail")) {
								// �������ݿ����Ϣ
								tablesHash.put(dbStr,
										(List) returnValue.get("tablesDetail"));
							}
							if (returnValue.containsKey("tablesDetail")) {
								// �������ݿ����Ϣ
								tableinfo_v = (Vector) returnValue
										.get("variables");
							}
						}
					}
				}
				runstr = (String) ipData.get("runningflag");
				if (runstr != null && runstr.contains("����ֹͣ")) {// ��<font
					runstr = "����ֹͣ";
				}
				if (runstr != null && runstr.contains("��������")) {
					pingnow = "100";
				}
			}
			String newip = SysUtil.doip(vo.getIpAddress());
			Hashtable ConnectUtilizationhash = new Hashtable();
			I_HostCollectData hostmanager = new HostCollectDataManager();
			try {
				ConnectUtilizationhash = hostmanager
						.getCategory(vo.getId() + "", "MYPing",
								"ConnectUtilization", starttime, totime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			String pingconavg = "0";
			if (ConnectUtilizationhash.get("avgpingcon") != null) {
				pingconavg = (String) ConnectUtilizationhash.get("avgpingcon");
			}
			if (pingconavg != null) {
				pingconavg = pingconavg.replace("%", "");// ƽ����ͨ��
			}
			if (ConnectUtilizationhash.get("downnum") != null) {
				downnum = (String) ConnectUtilizationhash.get("downnum");
			}
			if (ConnectUtilizationhash.get("pingMax") != null) {
				pingmax = (String) ConnectUtilizationhash.get("pingMax");// �����ͨ��
			}
			if (ConnectUtilizationhash.get("pingmax") != null) {
				pingmin = (String) ConnectUtilizationhash.get("pingmax");// �����ͨ��
			}
			avgpingcon = new Double(pingconavg + "").doubleValue();

			p_draw_line(ConnectUtilizationhash, "��ͨ��", newip
					+ "ConnectUtilization", 740, 150);// ��ͼ
			// �õ����еȼ�
			DBTypeDao dbTypeDao = new DBTypeDao();
			try {
				count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				dbTypeDao.close();
			}

			if (count > 0) {
				grade = "��";
			}
			if (!"0".equals(downnum)) {
				grade = "��";
			}
			// �¼��б�
			int status = getParaIntValue("status");
			int level1 = getParaIntValue("level1");
			if (status == -1)
				status = 99;
			if (level1 == -1)
				level1 = 99;
			try {
				User user = (User) session
						.getAttribute(SessionConstant.CURRENT_USER); // �û�����
				// SysLogger.info("user businessid===="+vo.getBusinessids());
				EventListDao eventdao = new EventListDao();
				try {
					eventList = eventdao.getQuery(starttime, totime, "db",
							status + "", level1 + "", user.getBusinessids(),
							vo.getId());
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
		maxping.put("pingmax", pingmin + "%");// ��С��ͨ��
		maxping.put("pingnow", pingnow + "%");
		maxping.put("avgpingcon", avgpingcon + "%");// ƽ����ͨ��
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
		reporthash.put("dbname", typevo.getDbtype() + "(" + vo.getIpAddress()
				+ ")");
		reporthash.put("ip", vo.getIpAddress());

		String fileName = "";
		String str = request.getParameter("str");// ��ҳ�淵���趨��strֵ�����жϣ�����excel�������word����
		if ("0".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/dbMySQLCldReport.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
			fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
			try {
				report1.createReport_MySQLCldDoc(fileName, "doc");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word�ۺϱ��������
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/dbMySQLCldReport.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
			fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
			try {
				report1.createReport_MySQLCldDoc(fileName, "pdf");
			} catch (IOException e) {
				SysLogger.error("", e);
			}// pdf�ۺϱ��������
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			String file = "temp/dbMySQLCldReport.xls";// ���浽��Ŀ�ļ����µ�ָ���ļ���
			fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
			try {
				report1.createReport_MySQLCldXls(fileName);
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}// word�ۺϱ��������
		}

		out.print(fileName);
		out.flush();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportOracleReportForType() {
		Date d = new Date();
		DBDao dao = null;
		Hashtable memValue = new Hashtable();
		String runstr = "����ֹͣ";
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String dbname = "";
		String dbnamestr = "";
		String typename = "ORACLE";
		Vector tableinfo_v = new Vector();
		Hashtable maxping = new Hashtable();// Ping--max
		Hashtable pingdata = ShareData.getPingdata();
		DBVo vo = null;
		String sid = "";
		String pingnow = "0.0";// HONGLI ADD ��ǰ��ͨ��
		String pingmin = "0.0";// HONGLI ADD ��С��ͨ��
		String pingconavg = "0.0";// HONGLI ADD ƽ����ͨ��
		List eventList = new ArrayList();// �¼��б�
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
				}
			}
			sid = vo.getId() + "";
			dbname = vo.getDbName() + "(" + ip + ")";
			dbnamestr = vo.getDbName();
			String newip = SysUtil.doip(ip);
			// ���ڴ���ȡ��sga����Ϣ
			dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + sid;
			Hashtable statusHashtable = dao.getOracle_nmsorastatus(serverip);// ȡ״̬��Ϣ
			String statusStr = String.valueOf(statusHashtable.get("status"));
			memValue = dao.getOracle_nmsoramemvalue(serverip);
			if ("1".equals(statusStr)) {
				runstr = "��������";
				pingnow = "100.0";// HONGLI ADD
			}
			Hashtable pinghash = hostmanager.getCategory(sid, "ORAPing",
					"ConnectUtilization", starttime, totime);
			p_draw_line(pinghash, "", newip + "ConnectUtilization", 740, 120);
			if (pinghash.get("avgpingcon") != null)
				pingconavg = (String) pinghash.get("avgpingcon");
			String ConnectUtilizationmax = "";
			maxping.put("avgpingcon", pingconavg);
			if (pinghash.get("max") != null) {
				ConnectUtilizationmax = (String) pinghash.get("max");
			}
			// HONGLI ADD START0
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
			SysLogger.error("", e);
		}
		Hashtable reporthash = new Hashtable();
		Vector pdata = (Vector) pingdata.get(ip);
		// ��ping�õ������ݼӽ�ȥ
		if (pdata != null && pdata.size() > 0) {
			for (int m = 0; m < pdata.size(); m++) {
				Pingcollectdata hostdata = (Pingcollectdata) pdata.get(m);
				if (hostdata.getSubentity().equals("ConnectUtilization")) {
					reporthash.put("time", hostdata.getCollecttime());
					reporthash.put("Ping", hostdata.getThevalue());
					reporthash.put("ping", maxping);
				}
			}
		} else {
			reporthash.put("ping", maxping);
		}
		// alex
		int count = 0;
		StringBuffer s = new StringBuffer();
		s.append("select * from system_eventlist where recordtime>= '"
				+ starttime + "' " + "and recordtime<='" + totime + "' ");
		s.append(" and nodeid=" + vo.getId());

		EventListDao eventdao1 = new EventListDao();
		List infolist = eventdao1.findByCriteria(s.toString());
		if (infolist != null && infolist.size() > 0) {

			for (int j = 0; j < infolist.size(); j++) {
				EventList eventlist = (EventList) infolist.get(j);
				if (eventlist.getContent() == null)
					eventlist.setContent("");
				String content = eventlist.getContent();
				if (eventlist.getSubentity().equalsIgnoreCase("pctbufgets")
						|| eventlist.getSubentity().equalsIgnoreCase(
								"buffercache")) {
					count = count + 1;
				}
			}
		}
		// end
		// ��oracle崻�����
		String downnum = "0";
		Hashtable pinghash = new Hashtable();
		try {
			pinghash = hostmanager.getCategory(vo.getId() + "", "ORAPing",
					"ConnectUtilization", starttime, totime);
			if (pinghash.get("downnum") != null)
				downnum = (String) pinghash.get("downnum");
		} catch (Exception e1) {
			SysLogger.error("", e1);
		}
		// ��ռ�==========�澯
		DBTypeDao dbTypeDao = new DBTypeDao();
		try {
			count = dbTypeDao.finddbcountbyip(ip) + count;
		} catch (Exception e) {
			SysLogger.error("", e);
		} finally {
			dbTypeDao.close();
		}
		// ���ݿ����еȼ�=====================
		String grade = "��";
		if (count > 0 && count <= 3) {
			grade = "��";
		}
		if (!"0".equals(downnum) || count > 3) {
			grade = "��";
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
		reporthash.put("pingnow", pingnow);// HONGLI ADD
		reporthash.put("pingmin", pingmin);// HONGLI ADD
		reporthash.put("pingconavg", pingconavg);// HONGLI ADD
		reporthash.put("vo", vo);// HONGLI ADD
		reporthash.put("ip", ip);
		// HONGLI START########
		Hashtable cursors = new Hashtable();
		Hashtable dbio = new Hashtable();
		Hashtable memPerfValue = new Hashtable();
		dao = new DBDao();
		IpTranslation tranfer = new IpTranslation();
		String hex = tranfer.formIpToHex(vo.getIpAddress());
		String serverip = hex + ":" + sid;
		Hashtable statusHashtable = new Hashtable();// ȡ״̬��Ϣ
		try {
			statusHashtable = dao.getOracle_nmsorastatus(serverip);// ȡ״̬��Ϣ
			memPerfValue = dao.getOracle_nmsoramemperfvalue(serverip);
			String statusStr = String.valueOf(statusHashtable.get("status"));
			memValue = dao.getOracle_nmsoramemvalue(serverip);
			dbio = dao.getOracle_nmsoradbio(serverip);
			tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
			cursors = dao.getOracle_nmsoracursors(serverip);
			if ("1".equals(statusStr)) {
				runstr = "��������";
				pingnow = "100.0";// HONGLI ADD
			}
			dao.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (runstr.equals("����ֹͣ")) {
			grade = "������ֹͣ";
		}
		reporthash.put("grade", grade);
		// HONGLI ADD START2
		// ȥ����λMB\KB
		String[] sysItem = { "shared_pool", "large_pool",
				"DEFAULT_buffer_cache", "java_pool",
				"aggregate_PGA_target_parameter", "total_PGA_allocated",
				"maximum_PGA_allocated" };
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
		reporthash.put("dbio", dbio);
		// �¼��б�
		int status = getParaIntValue("status");
		int level1 = getParaIntValue("level1");
		if (status == -1)
			status = 99;
		if (level1 == -1)
			level1 = 99;
		EventListDao eventdao = new EventListDao();
		try {
			User user = (User) session
					.getAttribute(SessionConstant.CURRENT_USER); // �û�����
			try {
				eventList = eventdao.getQuery(starttime, totime, "db", status
						+ "", level1 + "", user.getBusinessids(),
						Integer.parseInt(sid));
			} catch (Exception e) {
				SysLogger.error("", e);
			} finally {
				eventdao.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		reporthash.put("tableinfo_v", tableinfo_v);
		reporthash.put("list", eventList);
		reporthash.put("vo", vo);
		reporthash.put("memPerfValue", memPerfValue);
		reporthash.put("cursors", cursors);
		reporthash.put("memValue", memValue);
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);

		String fileName = "";
		String str = request.getParameter("str");// ��ҳ�淵���趨��strֵ�����жϣ�����excel�������word����
		if ("0".equals(str)) {
			report.createReport_ora("temp/dbora_report.xls");
			fileName = report.getFileName();
		} else if ("1".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/dbora_report.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
				fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
				report1.createReport_oraDoc(fileName);// word�ۺϱ���
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("2".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/dbora_report.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
				fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
				report1.createReport_oraPDF(fileName);// wordҵ�������
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("3".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/dbora_reportcheck.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
				fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
				report1.createReport_oraNewPDF(fileName, "doc");// wordҵ�������
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("4".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/dbora_reportcheck.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
				fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
				report1.createReport_oraNewPDF(fileName, "pdf");// wordҵ�������
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("5".equals(str)) {// HONGLI ADD START2
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/dbora_cld_report.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
				fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
				report1.createReportOracleCldPdf(fileName, "doc");// oracle���ۺ����ܱ���word��ʽ��ӡ
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("6".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/dbora_cld_report.xls";// ���浽��Ŀ�ļ����µ�ָ���ļ���
				report1.createReportOracleCldExcel(file);// oracle���ۺ����ܱ���excel��ʽ��ӡ
				fileName = report1.getFileName();
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		} else if ("7".equals(str)) {
			ExcelReport1 report1 = new ExcelReport1(new IpResourceReport(),
					reporthash);
			try {
				String file = "temp/dbora_cld_report.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
				fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
				report1.createReportOracleCldPdf(fileName, "pdf");// oracle���ۺ����ܱ���PDF��ʽ��ӡ
			} catch (DocumentException e) {
				SysLogger.error("", e);
			} catch (IOException e) {
				SysLogger.error("", e);
			}
		}

		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unused", "unchecked", "static-access" })
	private void exportDatabaseReportForMysql() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(";").length > 0) {
			String[] _ids = oids.split(";");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String dbname = "";
		String fileName = "";

		Hashtable hash = new Hashtable();// "Cpu",--current
		Hashtable memhash = new Hashtable();// mem--current
		Hashtable diskhash = new Hashtable();
		Hashtable memmaxhash = new Hashtable();// mem--max
		Hashtable memavghash = new Hashtable();// mem--avg
		Hashtable maxhash = new Hashtable();// "Cpu"--max
		Vector vector = new Vector();
		DBVo vo = new DBVo();
		DBTypeVo typevo = null;
		double avgpingcon = 0;
		String pingnow = "0.0";// ��ǰ��ͨ��
		String pingmin = "0.0";// ��С��ͨ��
		String pingmax = "0.0";// �����ͨ��
		String runstr = "����ֹͣ";
		String downnum = "";
		// ���ݿ����еȼ�=====================
		String grade = "��";
		Hashtable mems = new Hashtable();// �ڴ���Ϣ
		Hashtable sysValue = new Hashtable();
		List eventList = new ArrayList();// �¼��б�
		Hashtable spaceInfo = new Hashtable();
		// ���ݿ����еȼ�=====================
		Hashtable conn = new Hashtable();// ������Ϣ
		Hashtable poolInfo = new Hashtable();
		Hashtable log = new Hashtable();
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
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype()
								+ "");
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						typedao.close();
					}
					ip = vo.getIpAddress();
					dbname = vo.getDbName() + "(" + ip + ")";
					String newip = SysUtil.doip(ip);
					Hashtable reporthash = new Hashtable();
					Vector Val = new Vector();
					List sessionlist = new ArrayList();
					Hashtable tablesHash = new Hashtable();
					Vector tableinfo_v = new Vector();
					Hashtable dbValue = new Hashtable();
					IpTranslation tranfer = new IpTranslation();
					String hex = tranfer.formIpToHex(vo.getIpAddress());
					String serverip = hex + ":" + vo.getId();
					Hashtable ipData = dao.getMysqlDataByServerip(serverip);
					if (dao != null) {
						dao.close();
					}
					if (ipData != null && ipData.size() > 0) {
						String dbnames = vo.getDbName();
						String[] dbs = dbnames.split(",");
						for (int k = 0; k < dbs.length; k++) {
							// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
							String dbStr = dbs[k];
							if (ipData.containsKey(dbStr)) {
								Hashtable returnValue = new Hashtable();
								returnValue = (Hashtable) ipData.get(dbStr);
								if (returnValue != null
										&& returnValue.size() > 0) {
									if (doneFlag == 0) {
										// �ж��Ƿ��Ѿ���ȡ�˵�ǰ��������Ϣ
										if (returnValue
												.containsKey("configVal")) {
											doneFlag = 1;
										}
										if (returnValue.containsKey("Val")) {
											Val = (Vector) returnValue
													.get("Val");
										}
									}
									if (returnValue
											.containsKey("sessionsDetail")) {
										// �������ݿ�������Ϣ
										sessionlist.add((List) returnValue
												.get("sessionsDetail"));
									}
									if (returnValue.containsKey("tablesDetail")) {
										// �������ݿ����Ϣ
										tablesHash.put(dbStr,
												(List) returnValue
														.get("tablesDetail"));
									}
									if (returnValue.containsKey("tablesDetail")) {
										// �������ݿ����Ϣ
										tableinfo_v = (Vector) returnValue
												.get("variables");
									}
								}
							}
						}
						runstr = (String) ipData.get("runningflag");
						if (runstr != null && runstr.contains("����ֹͣ")) {// ��<font
							// color=red>����ֹͣ</font>
							// �滻
							runstr = "����ֹͣ";
						}
						if (runstr != null && runstr.contains("��������")) {
							pingnow = "100";
						}
					}
					Hashtable ConnectUtilizationhash = new Hashtable();
					I_HostCollectData hostmanager = new HostCollectDataManager();
					try {
						ConnectUtilizationhash = hostmanager.getCategory(
								vo.getId() + "", "MYPing",
								"ConnectUtilization", starttime, totime);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					String pingconavg = "0";
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconavg = (String) ConnectUtilizationhash
								.get("avgpingcon");
					}
					if (pingconavg != null) {
						pingconavg = pingconavg.replace("%", "");// ƽ����ͨ��
					}
					if (ConnectUtilizationhash.get("downnum") != null) {
						downnum = (String) ConnectUtilizationhash
								.get("downnum");
					}
					if (ConnectUtilizationhash.get("pingMax") != null) {
						pingmax = (String) ConnectUtilizationhash
								.get("pingMax");// �����ͨ��
					}
					if (ConnectUtilizationhash.get("pingmax") != null) {
						pingmin = (String) ConnectUtilizationhash
								.get("pingmax");// �����ͨ��
					}
					avgpingcon = new Double(pingconavg + "").doubleValue();
					p_draw_line(ConnectUtilizationhash, "��ͨ��", newip
							+ "ConnectUtilization", 740, 150);// ��ͼ
					// �õ����еȼ�
					DBTypeDao dbTypeDao = new DBTypeDao();
					try {
						count = dbTypeDao.finddbcountbyip(vo.getIpAddress());

					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}
					if (count > 0) {
						grade = "��";
					}
					if (!"0".equals(downnum)) {
						grade = "��";
					}
					// �¼��б�
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					try {
						User user = (User) session
								.getAttribute(SessionConstant.CURRENT_USER); // �û�����
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime,
									"db", status + "", level1 + "",
									user.getBusinessids(), vo.getId());
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					Hashtable maxping = new Hashtable();
					maxping.put("pingmax", pingmin + "%");// ��С��ͨ��
					maxping.put("pingnow", pingnow + "%");
					maxping.put("avgpingcon", avgpingcon + "%");// ƽ����ͨ��
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
					reporthash.put("dbname",
							typevo.getDbtype() + "(" + vo.getIpAddress() + ")");
					reporthash.put("ip", vo.getIpAddress());
					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
						allreporthash);
				report.createReport_mysqlall("/temp/mysqlall_report.xls");
				fileName = report.getFileName();
			}

		} catch (Exception e) {
			SysLogger.error("", e);
		}

		out.print(fileName);
		out.flush();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportDatabaseReportForOracle() {
		String oids = getParaValue("ids");
		if (oids == null)
			oids = "";
		Integer[] ids = null;
		if (oids.split(";").length > 0) {
			String[] _ids = oids.split(";");
			if (_ids != null && _ids.length > 0)
				ids = new Integer[_ids.length];
			for (int i = 0; i < _ids.length; i++) {
				ids[i] = new Integer(_ids[i]);
			}
		}

		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		String ip = "";
		String dbname = "";
		String sid = "";
		Hashtable maxping = new Hashtable();
		DBVo vo = null;
		String pingNow = "0.0";// HONGLI ADD ��ǰ��ͨ��
		String pingMin = "0.0";// HONGLI ADD ��С��ͨ��
		String pingconAvg = "0.0";// HONGLI ADD ƽ����ͨ��
		List eventList = new ArrayList();// �¼��б�
		Hashtable memValue = new Hashtable();
		Vector tableinfo_v = new Vector();
		Hashtable dbio = new Hashtable();
		Hashtable cursors = new Hashtable();
		String runstr = "����ֹͣ";
		String dbnamestr = "";
		String typename = "ORACLE";
		String filename = "";
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
					sid = vo.getId() + "";
					Hashtable ConnectUtilizationhash = hostmanager.getCategory(
							vo.getId() + "", "ORAPing", "ConnectUtilization",
							starttime, totime);
					if (ConnectUtilizationhash.get("avgpingcon") != null) {
						pingconAvg = (String) ConnectUtilizationhash
								.get("avgpingcon");
					}
					if (ConnectUtilizationhash.get("pingmax") != null) {
						pingMin = (String) ConnectUtilizationhash
								.get("pingmax");
					}
					String ConnectUtilizationmax = "";
					maxping.put("avgpingcon", pingconAvg);
					if (ConnectUtilizationhash.get("max") != null) {
						ConnectUtilizationmax = (String) ConnectUtilizationhash
								.get("max");
					}
					maxping.put("pingmax", ConnectUtilizationmax);
					Hashtable reporthash = new Hashtable();
					Hashtable memPerfValue = new Hashtable();
					dao = new DBDao();
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					String serverip = hex + ":" + sid;
					Hashtable statusHashtable = new Hashtable();// ȡ״̬��Ϣ
					try {
						statusHashtable = dao.getOracle_nmsorastatus(serverip);// ȡ״̬��Ϣ
						memPerfValue = dao
								.getOracle_nmsoramemperfvalue(serverip);
						String statusStr = String.valueOf(statusHashtable
								.get("status"));
						memValue = dao.getOracle_nmsoramemvalue(serverip);
						dbio = dao.getOracle_nmsoradbio(serverip);
						tableinfo_v = dao.getOracle_nmsoraspaces(serverip);
						cursors = dao.getOracle_nmsoracursors(serverip);
						if ("1".equals(statusStr)) {
							runstr = "��������";
							pingNow = "100.0";// HONGLI ADD
						}
						dao.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					// ȥ����λMB\KB
					String[] sysItem = { "shared_pool", "large_pool",
							"DEFAULT_buffer_cache", "java_pool",
							"aggregate_PGA_target_parameter",
							"total_PGA_allocated", "maximum_PGA_allocated" };
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
							memValue.put(sysItem[j],
									df.format(Double.parseDouble(value)));
						}
					}
					request.setAttribute("memValue", memValue);
					reporthash.put("dbio", dbio);
					// �¼��б�
					int status = getParaIntValue("status");
					int level1 = getParaIntValue("level1");
					if (status == -1)
						status = 99;
					if (level1 == -1)
						level1 = 99;
					try {
						User user = (User) session
								.getAttribute(SessionConstant.CURRENT_USER); // �û�����
						EventListDao eventdao = new EventListDao();
						try {
							eventList = eventdao.getQuery(starttime, totime,
									"db", status + "", level1 + "",
									user.getBusinessids(), vo.getId());
						} catch (Exception e) {
							SysLogger.error("", e);
						} finally {
							eventdao.close();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// ��oracle崻�����
					String downnum = "0";
					Hashtable pinghash = new Hashtable();
					try {
						pinghash = hostmanager.getCategory(vo.getId() + "",
								"ORAPing", "ConnectUtilization", starttime,
								totime);
						if (pinghash.get("downnum") != null)
							downnum = (String) pinghash.get("downnum");
					} catch (Exception e1) {

						e1.printStackTrace();
					}
					// ��ռ�==========�澯
					DBTypeDao dbTypeDao = new DBTypeDao();
					int count = 0;
					try {
						count = dbTypeDao.finddbcountbyip(ip);
					} catch (Exception e) {
						SysLogger.error("", e);
					} finally {
						dbTypeDao.close();
					}
					// ���ݿ����еȼ�=====================
					String grade = "��";
					if (count > 0) {
						grade = "��";
					}
					if (!"0".equals(downnum)) {
						grade = "��";
					}
					if (runstr.equals("����ֹͣ")) {
						grade = "������ֹͣ";
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
					reporthash.put("pingnow", pingNow);// HONGLI ADD
					reporthash.put("pingmin", pingMin);// HONGLI ADD
					reporthash.put("pingconavg", pingconAvg);// HONGLI ADD

					allreporthash.put(ip, reporthash);
				}
				ExcelReport1 report = new ExcelReport1(new IpResourceReport(),
						allreporthash);
				report.createReport_oraall2("/temp/oraall_report.xls");
				filename = report.getFileName();
			}
		} catch (Exception e) {
			SysLogger.error("", e);
		}

		out.print(filename);
		out.flush();
	}

	private void exportEventPdf() {
		String file = "/temp/dbevent.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
		try {
			createDocContexteventDatabase(fileName, "pdf");
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}

		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportEventExcel() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		List memlist = (List) session.getAttribute("eventlist");
		Hashtable reporthash = new Hashtable();
		reporthash.put("eventlist", memlist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		report.createReport_dbevent("/temp/dbevent_report.xls");

		out.print(report.getFileName());
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportEventWord() {
		String ipaddress = (String) request.getParameter("ipaddress");
		String typevo = (String) request.getParameter("typevo");
		String dbname = (String) request.getParameter("dbname");
		List ls = new ArrayList();
		ls.add(0, ipaddress);
		ls.add(1, typevo);
		ls.add(2, dbname);
		List list = ls;
		List _list = list;
		request.setAttribute("ls", _list);
		String file = "/temp/dbevent.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
		try {
			createDocContexteventDatabase(fileName, "doc");
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}

		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void getEventDetailList() {

		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";

		String[] ids = {};
		String idss = getParaValue("ids");
		if (idss != null) {
			ids = idss.split(";");
		}
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
		// �������־ȡ���˿����¼�¼���б�
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null
				&& !getParaValue("orderflag").equals("")) {
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
				// �õ��¼��б�
				int nodeid = vo.getId();
				StringBuffer s = new StringBuffer();
				s.append("select * from system_eventlist where recordtime>= '"
						+ starttime + "' " + "and recordtime<='" + totime
						+ "' ");
				s.append(" and nodeid=" + nodeid + " and subtype='db'");
				List infolist = eventdao.findByCriteria(s.toString());

				int pingvalue = 0;
				int level1 = 0;// ��ͨ�¼�
				int level2 = 0;// �����¼�
				int level3 = 0;// �����¼�

				for (int j = 0; j < infolist.size(); j++) {
					EventList eventlist = (EventList) infolist.get(j);
					if (eventlist.getContent() == null)
						eventlist.setContent("");
					String content = eventlist.getContent();
					if (content.indexOf("���ݿ����ֹͣ") > 0) {
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
				ipeventList.add(infolist.size());// index --8 �¼�����
				orderList.add(ipeventList);
				// }
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("ping")
				|| orderflag.equalsIgnoreCase("total")
				|| orderflag.equalsIgnoreCase("level1")
				|| orderflag.equalsIgnoreCase("level2")
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
						if (new Double(downnum).doubleValue() < new Double(
								_downnum).doubleValue()) {
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
				// �õ�������Subentity���б�
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}
		session.setAttribute("eventlist", list);
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				List _pinglist = (List) list.get(i);
				String ip = (String) _pinglist.get(0);
				String dbtype = (String) _pinglist.get(1);
				String dbname = (String) _pinglist.get(2);
				String dbuse = (String) _pinglist.get(3);
				String downnum = (String) _pinglist.get(4);
				String level1 = String.valueOf(_pinglist.get(5));
				String level2 = String.valueOf(_pinglist.get(6));
				String level3 = String.valueOf(_pinglist.get(7));
				String total = String.valueOf(_pinglist.get(8));

				jsonString.append("{\"nodeid\":\"");
				jsonString.append("");
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(ip);
				jsonString.append("\",");

				jsonString.append("\"dbtype\":\"");
				jsonString.append(dbtype);
				jsonString.append("\",");

				jsonString.append("\"dbname\":\"");
				jsonString.append(dbname);
				jsonString.append("\",");

				jsonString.append("\"dbuse\":\"");
				jsonString.append(dbuse);
				jsonString.append("\",");

				jsonString.append("\"sum\":\"");
				jsonString.append(total);
				jsonString.append("\",");

				jsonString.append("\"levelone\":\"");
				jsonString.append(level1);
				jsonString.append("\",");

				jsonString.append("\"leveltwo\":\"");
				jsonString.append(level2);
				jsonString.append("\",");

				jsonString.append("\"levelthree\":\"");
				jsonString.append(level3);
				jsonString.append("\",");

				jsonString.append("\"downnum\":\"");
				jsonString.append(downnum);
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();

	}

	@SuppressWarnings("rawtypes")
	private void getNodeListForPing() {
		String type = getParaValue("type");
		String ipaddress = getParaValue("content");
		String ip = "";
		if (ipaddress != null) {
			ip = ipaddress;
		}
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}

		String sql = "";
		if (ip != null && !ip.equals("")) {
			sql = " and ip_address like '%" + ip + "%'";
		}
		DBDao dao = new DBDao();
		List list = new ArrayList();
		if (type.equals("all")) {
			list = (List) dao.findByCondition(" where 1=1" + sql);
		} else if (type.equals("oracle")) {
			list = (List) dao.findByCondition(" where 1=1 and dbtype=1" + sql);
		} else if (type.equals("mysql")) {
			list = (List) dao.findByCondition(" where 1=1 and dbtype=4" + sql);
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		DBVo node = null;
		if (null != list && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				node = (DBVo) list.get(i);

				DBTypeDao typedao = new DBTypeDao();
				int dbtype = node.getDbtype();
				DBTypeVo typevo = (DBTypeVo) typedao.findByID(dbtype + "");
				typedao.close();

				jsonString.append("{\"nodeid\":\"");
				jsonString.append(node.getId());
				jsonString.append("\",");

				jsonString.append("\"ipaddress\":\"");
				jsonString.append(node.getIpAddress());
				jsonString.append("\",");

				jsonString.append("\"dbtype\":\"");
				if (null != typevo.getDbtype()) {
					jsonString.append(typevo.getDbtype());
				} else {
					jsonString.append(" ");
				}
				jsonString.append("\",");

				jsonString.append("\"dbname\":\"");
				if (null != node.getDbName()) {
					jsonString.append(node.getDbName());
				} else {
					jsonString.append(" ");
				}
				jsonString.append("\",");

				jsonString.append("\"dbuse\":\"");
				jsonString.append(node.getDbuse());
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total:" + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void exportDatabasePingWord() {
		
		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getDatabasePingDataForReport();
		}
		String file = "/temp/shujukuliantonglvbaobiao.doc";// ���浽��Ŀ�ļ����µ�ָ���ļ���
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
		try {
			createDBPingDocContext(fileName);
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}

		out.print(fileName);
		out.flush();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void exportDatabasePingExcel() {

		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getDatabasePingDataForReport();
		}

		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		String pingpath = (String) session.getAttribute("pingpath");

		List pinglist = (List) session.getAttribute("pinglist");// ������ͨ��
		Hashtable reporthash = new Hashtable();
		reporthash.put("pinglist", pinglist);
		reporthash.put("starttime", starttime);
		reporthash.put("totime", totime);
		reporthash.put("pingpath", pingpath);

		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(),
				reporthash);
		report.createReport_dbping("/temp/dbping_report.xls");

		out.print(report.getFileName());
		out.flush();
	}

	private void exportDatabasePingPdf() {

		String ids = getParaValue("id");
		
		if(ids != null && !ids.equals("create")){
			getDatabasePingDataForReport();
		}
		

		String file = "/temp/shujukuliantonglvbaobiao.pdf";// ���浽��Ŀ�ļ����µ�ָ���ļ���
		String fileName = ResourceCenter.getInstance().getSysPath() + file;// ��ȡϵͳ�ļ���·��
		try {
			createDatabasePingPdfContext(fileName);
		} catch (DocumentException e) {
			SysLogger.error("", e);
		} catch (IOException e) {
			SysLogger.error("", e);
		}

		out.print(fileName);
		out.flush();
	}

	private void getDatabasePingDataForReport() {
		Date d = new Date();
		String startdate = getParaValue("startdate");
		if (startdate == null) {
			startdate = sdf.format(d);
		}
		String todate = getParaValue("todate");
		if (todate == null) {
			todate = sdf.format(d);
		}
		String starttime = startdate + " 00:00:00";
		String totime = todate + " 23:59:59";
		session.setAttribute("starttime", starttime);
		session.setAttribute("totime", totime);
		String id1 = getParaValue("id");
		String[] ids = null;
		if (id1 != null) {
			ids = id1.split(";");
		}
		if (ids != null && ids.length > 0) {
			session.setAttribute("ids", ids);
		} else {
			ids = (String[]) session.getAttribute("ids");
		}
		request.setAttribute("ids", ids);
		String orderflag = "ipaddress";
		if (getParaValue("orderflag") != null
				&& !getParaValue("orderflag").equals("")) {
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
						pinghash = hostmanager.getCategory(id, "ORAPing",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
						pinghash = hostmanager.getCategory(id, "SQLPing",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
						pinghash = hostmanager.getCategory(id, "DB2Ping",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {
						pinghash = hostmanager.getCategory(id, "SYSPing",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
						pinghash = hostmanager.getCategory(id, "INFORMIXPing",
								"ConnectUtilization", starttime, totime);
					} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {// HONGLI
						pinghash = hostmanager.getCategory(id, "MYPing",
								"ConnectUtilization", starttime, totime);
					}
				} catch (Exception e) {
					SysLogger.error("", e);
				}
				Hashtable ipmemhash = new Hashtable();
				ipmemhash.put("dbvo", vo);
				ipmemhash.put("pinghash", pinghash);
				ipmemhash.put("ipaddress",
						vo.getIpAddress() + "(" + vo.getAlias() + ")");
				orderList.add(ipmemhash);
			}

		}
		List returnList = new ArrayList();
		if (orderflag.equalsIgnoreCase("avgping")
				|| orderflag.equalsIgnoreCase("downnum")) {
			returnList = (List) session.getAttribute("pinglist");
		} else {
			// ��orderList����theValue��������
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
						typevo = (DBTypeVo) typedao.findByID(vo.getDbtype()
								+ "");
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
						if (new Double(avgping.substring(0,
								avgping.length() - 2)).doubleValue() < new Double(
								_avgping.substring(0, _avgping.length() - 2))
								.doubleValue()) {
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
						if (new Double(downnum).doubleValue() < new Double(
								_downnum).doubleValue()) {
							returnList.remove(m);
							returnList.add(m, _ipdiskList);
							returnList.remove(n);
							returnList.add(n, ipdiskList);
							ipdiskList = _ipdiskList;
							_ipdiskList = null;
						}
					}
				}
				// �õ�������Subentity���б�
				list.add(ipdiskList);
				ipdiskList = null;
			}
		}
		//
		String pingChartDivStr = ReportHelper.getChartDivStr(orderList, "ping");
		ReportValue pingReportValue = ReportHelper.getReportValue(orderList,
				"ping");
		String pingpath = new ReportExport().makeJfreeChartData(
				pingReportValue.getListValue(), pingReportValue.getIpList(),
				"��ͨ��", "ʱ��", "");
		session.setAttribute("pingpath", pingpath);
		session.setAttribute("pinglist", list);
	}

	public void createDatabasePingPdfContext(String file)
			throws DocumentException, IOException {
		// ����ֽ�Ŵ�С
		Document document = new Document(PageSize.A4);
		// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		// ������������
		BaseFont bfChinese = BaseFont.createFont("STSong-Light",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);

		// ����������
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// ����������
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("���ݿ���ͨ�ʱ���", titleFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf1.format(cc);

		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("��������ʱ��:" + time, contextFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("����ͳ��ʱ���: " + starttime + " �� " + totime,
				contextFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		// ���� Table ���
		document.add(new Paragraph("\n"));
		Font fontChinese = new Font(bfChinese, 12, Font.NORMAL, Color.black);
		List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(7);
		setTableFormat(aTable);

		aTable.addCell(this.setCellFormat(new Phrase("����", contextFont), true));
		Cell cell1 = new Cell(new Phrase("IP��ַ", contextFont));
		Cell cell11 = new Cell(new Phrase("���ݿ�����", contextFont));
		Cell cell2 = new Cell(new Phrase("���ݿ�����", contextFont));
		Cell cell3 = new Cell(new Phrase("���ݿ�Ӧ��", contextFont));
		Cell cell4 = new Cell(new Phrase("ƽ����ͨ��", contextFont));
		Cell cell5 = new Cell(new Phrase("崻�����(��)", contextFont));
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
		// ������ͨ��
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
		img.scalePercent(75);
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	// ����������ͨ��word����
	@SuppressWarnings("rawtypes")
	public void createDBPingDocContext(String file) throws DocumentException,
			IOException {
		// ����ֽ�Ŵ�С
		Document document = new Document(PageSize.A4);
		// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
		RtfWriter2.getInstance(document, new FileOutputStream(file));
		document.open();
		// ������������
		BaseFont bfChinese = BaseFont.createFont("Times-Roman", "",
				BaseFont.NOT_EMBEDDED);
		// ����������
		Font titleFont = new Font(bfChinese, 14, Font.BOLD);
		// ����������
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("���ݿ���ͨ�ʱ���", titleFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf1.format(cc);

		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("��������ʱ��:" + time, contextFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		String starttime = (String) session.getAttribute("starttime");
		String totime = (String) session.getAttribute("totime");
		titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		contextFont = new Font(bfChinese, 10, Font.NORMAL);
		title = new Paragraph("����ͳ��ʱ���: " + starttime + " �� " + totime,
				contextFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);
		document.add(new Paragraph("\n"));
		// ���� Table ���
		List pinglist = (List) session.getAttribute("pinglist");
		Table aTable = new Table(7);
		int width[] = { 50, 50, 50, 70, 50, 50, 50 };
		aTable.setWidths(width);
		aTable.setWidth(100); // ռҳ���� 100%
		aTable.setAlignment(Element.ALIGN_CENTER);// ������ʾ
		aTable.setAutoFillEmptyCells(true); // �Զ�����
		aTable.setBorderWidth(1); // �߿���
		aTable.setBorderColor(new Color(0, 125, 255)); // �߿���ɫ
		aTable.setPadding(2);// �ľ࣬��Ч����֪��ʲô��˼��
		aTable.setSpacing(0);// ����Ԫ��֮��ļ��
		aTable.setBorder(2);// �߿�
		aTable.endHeaders();

		Cell cell = new Cell("���");
		this.setCellFormat(cell, true);
		aTable.addCell(cell);
		Cell cell1 = new Cell("IP��ַ");
		Cell cell11 = new Cell("���ݿ�����");
		Cell cell2 = new Cell("���ݿ�����");
		Cell cell3 = new Cell("���ݿ�Ӧ��");
		Cell cell15 = new Cell("ƽ����ͨ��");
		Cell cell4 = new Cell("崻�����(��)");
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
		// ������ͨ��
		String pingpath = (String) session.getAttribute("pingpath");
		Image img = Image.getInstance(pingpath);
		img.setAbsolutePosition(0, 0);
		img.setAlignment(Image.LEFT);// ����ͼƬ��ʾλ��
		document.add(img);
		document.add(aTable);
		document.add(new Paragraph("\n"));
		document.close();
	}

	@SuppressWarnings("rawtypes")
	private void p_drawchartMultiLine(Hashtable hash, String title1,
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
					TimeSeries ss = new TimeSeries(key, Minute.class);
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
								temp.get(Calendar.HOUR_OF_DAY),
								temp.get(Calendar.DAY_OF_MONTH),
								temp.get(Calendar.MONTH) + 1,
								temp.get(Calendar.YEAR));
						ss.addOrUpdate(minute, v);
					}
					s[i] = ss;
				}
				cg.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1, title2, w, h);
				hash = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			draw_blank(title1, title2, w, h);
		}
	}

	private void draw_blank(String title1, String title2, int w, int h) {
		ChartGraph cg = new ChartGraph();
		TimeSeries ss = new TimeSeries(title1, Minute.class);
		TimeSeries[] s = { ss };
		try {
			Calendar temp = Calendar.getInstance();
			Minute minute = new Minute(temp.get(Calendar.MINUTE),
					temp.get(Calendar.HOUR_OF_DAY),
					temp.get(Calendar.DAY_OF_MONTH),
					temp.get(Calendar.MONTH) + 1, temp.get(Calendar.YEAR));
			ss.addOrUpdate(minute, null);
			cg.timewave(s, "x(ʱ��)", "y", title1, title2, w, h);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	private void p_draw_line(Hashtable hash, String title1, String title2,
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
					Double d = new Double((String) v.get(0));
					String dt = (String) v.get(1);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date time1 = sdf.parse(dt);
					Calendar temp = Calendar.getInstance();
					temp.setTime(time1);
					Minute minute = new Minute(temp.get(Calendar.MINUTE),
							temp.get(Calendar.HOUR_OF_DAY),
							temp.get(Calendar.DAY_OF_MONTH),
							temp.get(Calendar.MONTH) + 1,
							temp.get(Calendar.YEAR));
					ss.addOrUpdate(minute, d);
				}
				cg.timewave(s, "x(ʱ��)", "y(" + unit + ")", title1, title2, w, h);

			}
			hash = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ñ���ʽ
	 * 
	 * @param aTable
	 */
	protected void setTableFormat(Table aTable) {
		aTable.setWidth(100);
		aTable.setAutoFillEmptyCells(true);
		aTable.setPadding(5);
		aTable.setAlignment(Element.ALIGN_CENTER);
	}

	/**
	 * ���õ�Ԫ��ĸ�ʽ
	 * 
	 * @param cell
	 *            ��Ԫ��
	 * @param flag
	 *            �Ƿ����û�ɫ����ɫ
	 * @return cell
	 */
	protected Cell setCellFormat(Object obj, boolean flag) {
		Cell cell = null;
		Phrase p = null;
		if (obj instanceof Cell) {
			cell = (Cell) obj;
		} else if (obj instanceof Phrase) {
			p = (Phrase) obj;
			try {
				cell = new Cell(p);
			} catch (BadElementException e) {
				SysLogger.error("", e);
			}
		}
		if (cell != null) {
			if (flag) {
				cell.setBackgroundColor(Color.LIGHT_GRAY);
			}
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		}
		return cell;
	}

	public void draw_column(Hashtable bighash, String title1, String title2,
			int w, int h) {
		if (bighash.size() != 0) {
			ChartGraph cg = new ChartGraph();
			int size = bighash.size();
			double[][] d = new double[1][size];
			String c[] = new String[size];
			Hashtable hash;
			for (int j = 0; j < size; j++) {
				hash = (Hashtable) bighash.get(new Integer(j));
				c[j] = (String) hash.get("name");
				d[0][j] = Double.parseDouble((String) hash.get("Utilization"
						+ "value"));
			}
			String rowKeys[] = { "" };
			CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
					rowKeys, c, d);// .createCategoryDataset(rowKeys,
			cg.zhu(title1, title2, dataset, w, h);
		} else {
			draw_blank(title1, title2, w, h);
		}
		bighash = null;
	}

	public void createDocContexteventDatabase(String file, String type)
			throws DocumentException, IOException {
		// ����ֽ�Ŵ�С
		Document document = new Document(PageSize.A4);
		// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������
		if ("pdf".equals(type)) {
			PdfWriter.getInstance(document, new FileOutputStream(file));
		} else {
			RtfWriter2.getInstance(document, new FileOutputStream(file));
		}
		document.open();
		// ������������
		BaseFont bfChinese = BaseFont.createFont("STSong-Light",
				"UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		// ����������
		Font titleFont = new Font(bfChinese, 12, Font.BOLD);
		// ����������
		Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
		Paragraph title = new Paragraph("���ݿ��¼�����", titleFont);
		// ���ñ����ʽ���뷽ʽ
		title.setAlignment(Element.ALIGN_CENTER);
		// title.setFont(titleFont);
		document.add(title);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String contextString = "��������ʱ��:"
				+ sdf.format(new Date())
				+ " \n"// ����
				+ "����ͳ��ʱ���:"
				+ String.valueOf(session.getAttribute("starttime")) + " �� "
				+ String.valueOf(session.getAttribute("totime"));
		Paragraph context = new Paragraph(contextString, contextFont);
		// ���ĸ�ʽ�����
		context.setAlignment(Element.ALIGN_LEFT);
		// context.setFont(contextFont);
		context.setSpacingBefore(5);
		// ���õ�һ�пյ�����
		context.setFirstLineIndent(5);
		document.add(context);
		// ���� Table ���
		List pinglist = null;
		pinglist = (List) session.getAttribute("eventlist");// HONGLI
		if (pinglist == null) {
			pinglist = (List) request.getAttribute("ls");
		}
		Table aTable = new Table(10);
		this.setTableFormat(aTable);
		aTable.endHeaders();

		aTable.addCell(this.setCellFormat(new Phrase("���", contextFont), true));
		Cell cell1 = new Cell(new Phrase("IP��ַ", contextFont));
		Cell cell11 = new Cell(new Phrase("���ݿ�����", contextFont));
		Cell cell2 = new Cell(new Phrase("���ݿ�����", contextFont));
		Cell cell3 = new Cell(new Phrase("���ݿ�Ӧ��", contextFont));
		Cell cell20 = new Cell(new Phrase("�¼�����", contextFont));
		Cell cell21 = new Cell(new Phrase("��ͨ�¼�", contextFont));
		Cell cell22 = new Cell(new Phrase("�����¼�", contextFont));
		Cell cell23 = new Cell(new Phrase("�����¼�", contextFont));
		Cell cell15 = new Cell(new Phrase("�����������ô���", contextFont));
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
}
