package com.afunms.ip.util;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import net.sf.json.JSONObject;

import com.afunms.application.dao.ClusterDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.UpAndDownMachineDao;
import com.afunms.application.model.Cluster;
import com.afunms.application.model.UpAndDownMachine;
import com.afunms.application.util.RemoteClientInfo;
import com.afunms.application.util.ReportHelper;
import com.afunms.capreport.dao.UtilReportDao;
import com.afunms.capreport.model.ReportValue;
import com.afunms.capreport.model.StatisNumer;
import com.afunms.capreport.model.UtilReport;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.PingUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.ip.stationtype.dao.alltypeDao;
import com.afunms.ip.stationtype.dao.fieldDao;
import com.afunms.ip.stationtype.model.alltype;
import com.afunms.ip.stationtype.model.field;
import com.afunms.polling.api.I_HostCollectData;
import com.afunms.polling.impl.HostCollectDataManager;

@SuppressWarnings("unchecked")
public class ipAjax extends AjaxBaseManager implements AjaxManagerInterface {

	public void execute(String action) {
		if (action.equals("station")) {
			station();
		}
		if (action.equals("backbone")) {
			backbone();
		}
		if (action.equals("check")) {
			check();
		}

	}

	/**
	 * 资源|端口对比预览图
	 */

	public void showMovement() {
		int id = this.getParaIntValue("clusterId");
		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		List list = dao.loadClusterList(id);
		int[] step = new int[list.size()];
		String back = "";
		for (int i = 0; i < list.size(); i++) {
			UpAndDownMachine v = (UpAndDownMachine) list.get(i);
			PingUtil pu = new PingUtil(v.getIpaddress());
			int t = (pu.ping()[0] == 100) ? 2 : 1;
			step[i] = t;
			back += step[i] + ",";
		}
		back = back.substring(0, back.length() - 1);
		out.print(back);
		out.flush();
	}

	public void editClusterName() {
		int id = getParaIntValue("id");
		int clusterId = getParaIntValue("clusterId");

		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		boolean isSucess = dao.updateClusterIdById(clusterId, id);
		HashMap<String, String> map = new HashMap<String, String>();

		ClusterDao clusterDao = new ClusterDao();
		Cluster cluster = (Cluster) clusterDao.findByID(clusterId + "");
		String name = cluster.getName();
		if (isSucess) {
			map.put("value", name);
		} else {
			map.put("value", "0");
		}
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	public void rebootAll() {

		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		int id = getParaIntValue("clusterId");
		List list = dao.loadClusterList(id);
		dao.close();
		UpAndDownMachineDao udao = new UpAndDownMachineDao();
		boolean flag = true;
		Integer[] packet = null;
		for (int j = 0; j < list.size(); j++) {
			UpAndDownMachine machine = (UpAndDownMachine) list.get(j);
			RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(machine.getIpaddress());
			if (info != null) {

				String serverType = machine.getServerType();

				if (serverType.equals("windows")) {

					info.executeCmd("shutdown -r -f -t 0");

				}
				if (serverType.equals("linux") || serverType.equals("unix")) {
					info.executeCmd("shutdown -r  now");

				}
				if (serverType.equals("aix")) {
					info.executeCmd("shutdown –Fr");

				}
				try {
					while (flag) {
						PingUtil pingU = new PingUtil(machine.getIpaddress());
						packet = pingU.ping();
						if (packet[0] != null) {
							flag = false;
						} else {
							Thread.sleep(5000);
						}

					}
					flag = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				info.closeConnection();
				machine.setMonitorStatus(0);
				machine.setLasttime(new Timestamp(new Date().getTime()));

				udao.addBatchUpdateAllTime(machine);

				ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
			}
		}
		udao.executeBatch();
		udao.close();
	}

	public void shutdownAll() {

		UpAndDownMachineDao dao = new UpAndDownMachineDao();
		int id = getParaIntValue("clusterId");
		List list = dao.loadClusterList(id);
		dao.close();
		UpAndDownMachineDao udao = new UpAndDownMachineDao();
		// int second=0;
		boolean flag = true;
		Integer[] packet = null;
		for (int j = 0; j < list.size(); j++) {
			UpAndDownMachine machine = (UpAndDownMachine) list.get(j);
			RemoteClientInfo info = ShareData.getIp_clientInfoHash().get(machine.getIpaddress());
			if (info != null) {
				String serverType = machine.getServerType();
				if (serverType.equals("windows")) {
					info.executeCmd("shutdown -f -t 0");
				}
				if (serverType.equals("linux") || serverType.equals("unix")) {
					info.executeCmd(" shutdown  now");
				}
				if (serverType.equals("aix")) {
					info.executeCmd("shutdown -F");

				}
				if (serverType.equals("as400")) {
					info.executeCmd("PWRDWNSYS *IMMED");

				}
				try {
					while (flag) {
						PingUtil pingU = new PingUtil(machine.getIpaddress());
						packet = pingU.ping();
						if (packet[0] != null) {
							flag = false;
						} else {
							Thread.sleep(5000);
						}

					}
					flag = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			info.closeConnection();
			machine.setMonitorStatus(0);
			machine.setLasttime(new Timestamp(new Date().getTime()));

			udao.addBatchUpdateAllTime(machine);

			ShareData.getIp_clientInfoHash().remove(machine.getIpaddress());
		}
		udao.executeBatch();
		udao.close();

	}

	public void updateSequence() {
		DBManager dbManager = new DBManager();
		String sql = "";
		int id = 0;
		int sequence = 0;
		String ids = getParaValue("ids");
		String values = getParaValue("values");
		String[] idsArr = new String[ids.split(".").length];
		String[] valuesArr = new String[values.split(".").length];
		idsArr = ids.split("\\.");
		valuesArr = values.split("\\.");

		try {

			for (int i = 0; i < idsArr.length; i++) {
				id = Integer.parseInt(idsArr[i]);
				sequence = Integer.parseInt(valuesArr[i]);
				sql = "update nms_remote_up_down_machine set sequence=" + sequence + " where id=" + id;
				System.out.println(sql);
				dbManager.addBatch(sql);
			}
			dbManager.executeBatch();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			dbManager.close();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("data", "保存成功！！");

		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	public void execMidReport() {

		SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd");

		I_HostCollectData hostmanager = new HostCollectDataManager();
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

		String ids = getParaValue("ids");
		if (ids == null || ids.equals("") || ids.equals("null")) {
			String id = request.getParameter("id");
			if (id.equals("null"))
				return;
			UtilReport report = new UtilReport();
			UtilReportDao dao = new UtilReportDao();
			report = (UtilReport) dao.findByBid(id);
			ids = report.getIds();
		}
		String pingdata = "";
		String jvmdata = "";
		String[] idValue = null;
		if (ids != null && !ids.equals("null") && !ids.equals("")) {
			idValue = new String[ids.split(",").length];
			idValue = ids.split(",");
		}

		Hashtable pinghash = new Hashtable();
		Hashtable jvmhash = new Hashtable();
		Hashtable curhash = new Hashtable();
		List<List> pingList = new ArrayList<List>();
		List<List> jvmList = new ArrayList<List>();
		List<String> pingipList = new ArrayList<String>();
		List<String> jvmipList = new ArrayList<String>();
		StringBuffer pingHtml = new StringBuffer();
		StringBuffer jvmHtml = new StringBuffer();

		if (idValue != null && idValue.length > 0) {
			pingHtml
					.append("<table  border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=100 height=21>IP</td><td  class='body-data-title' height=21>当前连通率</td><td align='center' class='body-data-title'  height=21>最小连通率</td><td align='center' class='body-data-title'  height=21>平均连通率</td></tr>");
			jvmHtml
					.append("<table  border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=100 height=21>IP</td><td  class='body-data-title' height=21>当前利用率</td><td align='center' class='body-data-title'  height=21>最大利用率</td><td align='center' class='body-data-title'  height=21>平均利用率</td></tr>");

			for (int i = 0; i < idValue.length; i++) {

				String realValue = idValue[i];
				String[] idRelValue = new String[realValue.split("\\*").length];
				idRelValue = realValue.split("\\*");
				if (idRelValue.length < 4)
					continue;
				String type = idRelValue[0].trim();
				String item = idRelValue[1].trim();
				String ip = idRelValue[3].trim();
				try {
					if (type.equalsIgnoreCase("tomcat")) {
						if (item.equals("ping")) {
							pinghash = getCategory(ip, "TomcatPing", "ConnectUtilization", starttime, totime, "");
							curhash = hostmanager.getCurByCategory(ip, "TomcatPing", "ConnectUtilization");

							if (pinghash != null && pinghash.size() > 0) {
								List pingDataList = (List) pinghash.get("list");
								String pingCur = (String) curhash.get("pingCur");
								String pingAvg = (String) pinghash.get("avgpingcon");

								String pingMin = (String) pinghash.get("pingmax");

								setInnerHtml(pingHtml, ip, pingCur, pingMin, pingAvg, "%");
								if (pingDataList != null && pingDataList.size() > 0)
									pingList.add(pingDataList);
							}
							pingipList.add(ip);
						} else if (item.equals("jvm")) {
							jvmhash = getCategory(ip, "tomcat_jvm", "jvm_utilization", starttime, totime, "");
							Hashtable curJvm = hostmanager.getCurByCategory(ip, "tomcat_jvm", "jvm_utilization");
							if (jvmhash != null && jvmhash.size() > 0) {
								List jvmDataList = (List) jvmhash.get("list");
								String jvmCur = (String) curJvm.get("pingCur");// 当前虚拟利用率
								String jvmAvg = (String) jvmhash.get("avg_tomcat_jvm");

								String jvmMin = (String) jvmhash.get("max");

								setInnerHtml(jvmHtml, ip, jvmCur, jvmMin, jvmAvg, "%");
								if (jvmDataList != null && jvmDataList.size() > 0)
									jvmList.add(jvmDataList);

							}
							jvmipList.add(ip);
						}

					} else if (type.equalsIgnoreCase("iis")) {
						if (item.equals("ping")) {
							pinghash = getCategory(ip, "IISPing", "ConnectUtilization", starttime, totime, "");
							curhash = hostmanager.getCurByCategory(ip, "IISPing", "ConnectUtilization");

							if (pinghash != null && pinghash.size() > 0) {
								List pingDataList = (List) pinghash.get("list");
								String pingCur = (String) curhash.get("pingCur");
								String pingAvg = (String) pinghash.get("avgpingcon");

								String pingMin = (String) pinghash.get("pingmax");

								setInnerHtml(pingHtml, ip, pingCur, pingMin, pingAvg, "%");
								if (pingDataList != null && pingDataList.size() > 0)
									pingList.add(pingDataList);
							}
							pingipList.add(ip);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			// 组合amcharts数据
			pingdata = makeAmChartData(pingList, pingipList);

			// jvm
			jvmdata = makeAmChartData(jvmList, jvmipList);

		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("pingdata", pingdata);
		map.put("pingHtml", pingHtml.toString());
		map.put("jvmdata", jvmdata);
		map.put("jvmHtml", jvmHtml.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	}

	/**
	 * 以表格的形式显示当前、平均、最大的利用率
	 * 
	 * @param html
	 * @param ip
	 * @param cur
	 * @param min
	 * @param avg
	 */
	private void setInnerHtml(StringBuffer html, String ip, String cur, String min, String avg, String unit) {
		html.append("<tr bgcolor='#FFFFFF'><td align='center'>" + ip + "</td>");
		html.append("<td align='center' height=21>" + cur.replace("%", "") + unit + "</td>");
		html.append("<td align='center' height=21>" + min.replace("%", "") + unit + "</td>");
		html.append("<td align='center' height=21>" + avg.replace("%", "") + unit + "</td></tr>");
	}

	/**
	 * 以表格的形式显示当前的利用率
	 * 
	 * @param html
	 * @param ip
	 * @param cur
	 */
	private void setInnerHtml(StringBuffer html, String ip, String cur, String unit) {
		html.append("<tr bgcolor='#FFFFFF'><td align='center' colspan=2>" + ip + "</td>");
		html.append("<td align='center' height=21 colspan=2>" + cur.replace("%", "") + unit + "</td>");
	}

	/**
	 * 组合amcharts数据格式
	 * 
	 * @param dataList
	 * @param ipList
	 * @return
	 */
	private String makeAmChartData(List dataList, List ipList) {
		StringBuffer sb = new StringBuffer();
		String data = "";
		if (dataList != null && dataList.size() > 0) {

			sb.append("<chart><series>");

			List eachDataList = (List) dataList.get(0);
			for (int k = 0; k < eachDataList.size(); k++) {
				Vector v = new Vector();
				v = (Vector) eachDataList.get(k);
				sb.append("<value xid='");
				sb.append(k);
				sb.append("'>");
				sb.append(v.get(1));
				sb.append("</value>");

			}

			sb.append("</series><graphs>");
			for (int j = 0; j < dataList.size(); j++) {

				List dataList1 = (List) dataList.get(j);
				sb.append("<graph title='" + (String) ipList.get(j) + "' bullet='round_outlined' bullet_size='4'>");
				if (dataList1 != null && dataList1.size() > 0) {

					for (int m = 0; m < dataList1.size(); m++) {
						Vector v = new Vector();
						v = (Vector) dataList1.get(m);
						sb.append("<value xid='");
						sb.append(m);
						sb.append("'>");
						sb.append(v.get(0));
						sb.append("</value>");

					}
				}
				sb.append("</graph>");
			}

			sb.append("</graphs></chart>");

			data = sb.toString();

		} else {
			data = "0";
		}

		return data;
	}

	public Hashtable getCategory(String ip, String category, String subentity, String starttime, String endtime, String time) throws Exception {
		Hashtable hash = new Hashtable();

		DBManager dbmanager = new DBManager();
		ResultSet rs = null;
		try {
			if (!starttime.equals("") && !endtime.equals("")) {

				String allipstr = SysUtil.doip(ip);
				String sql = "";
				StringBuffer sb = new StringBuffer();
				if (category.equals("TomcatPing")) {
					sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcatping" + time + allipstr + " h where ");
				}
				if (category.equals("tomcat_jvm")) {
					sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from tomcat_jvm" + allipstr + " h where ");
				}
				if (category.equals("IISPing")) {
					sb.append(" select h.thevalue,DATE_FORMAT(h.collecttime,'%Y-%m-%d %H:%i:%s') as collecttime,h.unit from iisping" + allipstr + " h where ");
				}
				sb.append(" h.category='");
				sb.append(category);
				sb.append("' and h.subentity='");
				sb.append(subentity);
				sb.append("' and h.collecttime >= '");
				sb.append(starttime);
				sb.append("' and h.collecttime <= '");
				sb.append(endtime);
				sb.append("' order by h.collecttime");
				sql = sb.toString();
				rs = dbmanager.executeQuery(sql);
				List list1 = new ArrayList();
				String unit = "";
				double tempfloat = 0;
				double pingcon = 0;
				double tomcat_jvm_con = 0;
				int downnum = 0;
				int i = 0;
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					v.add(0, emitStr(thevalue));
					v.add(1, collecttime);
					v.add(2, rs.getString("unit"));
					if ((category.equals("TomcatPing") || category.equals("IISPing")) && subentity.equalsIgnoreCase("ConnectUtilization")) {
						pingcon = pingcon + getfloat(thevalue);
						if (thevalue.equals("0")) {
							downnum = downnum + 1;
						}
					}

					if (subentity.equalsIgnoreCase("ConnectUtilization")) {
						if (i == 1)
							tempfloat = getfloat(thevalue);
						if (tempfloat > getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else if (category.equalsIgnoreCase("tomcat_jvm")) {
						tomcat_jvm_con = tomcat_jvm_con + getfloat(thevalue);
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					} else {
						if (tempfloat < getfloat(thevalue))
							tempfloat = getfloat(thevalue);
					}
					list1.add(v);
				}
				rs.close();
				// stmt.close();

				Integer size = new Integer(0);
				hash.put("list", list1);
				if (list1.size() != 0) {
					size = new Integer(list1.size());
					if (list1.get(0) != null) {
						Vector tempV = (Vector) list1.get(0);
						unit = (String) tempV.get(2);
					}
				}
				if ((category.equals("TomcatPing") || category.equals("IISPing")) && subentity.equalsIgnoreCase("ConnectUtilization")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avgpingcon", CEIString.round(pingcon / list1.size(), 2) + unit);
						hash.put("pingmax", tempfloat + "");
						hash.put("downnum", downnum + "");
					} else {
						hash.put("avgpingcon", "0.0");
						hash.put("pingmax", "0.0");
						hash.put("downnum", "0");
					}
				}
				if (category.equals("tomcat_jvm")) {
					if (list1 != null && list1.size() > 0) {
						hash.put("avg_tomcat_jvm", CEIString.round(tomcat_jvm_con / list1.size(), 2) + unit);
					} else {
						hash.put("avg_tomcat_jvm", "0.0%");
					}
				}
				hash.put("size", size);
				hash.put("max", CEIString.round(tempfloat, 2) + unit);
				hash.put("unit", unit);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			dbmanager.close();
		}

		return hash;
	}

	private String emitStr(String num) {
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
		}
		return num;
	}

	private double getfloat(String num) {
		double snum = 0.0;
		if (num != null) {
			if (num.indexOf(".") >= 0) {
				if (num.substring(num.indexOf(".") + 1).length() > 7) {
					String tempStr = num.substring(num.indexOf(".") + 1);
					num = num.substring(0, num.indexOf(".") + 1) + tempStr.substring(0, 7);
				}
			}
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = new Double(inum / 100.0).doubleValue();
		}
		return snum;
	}

	/**
	 * @author wxy
	 * @date 2011-5-10 生成网络设备、服务器报表
	 */
	public void executeReport() {

		String ids = request.getParameter("ids");
		if (ids == null || ids.equals("") || ids.equals("null")) {
			String id = request.getParameter("id");
			if (id.equals("null"))
				return;
			UtilReport report = new UtilReport();
			UtilReportDao dao = new UtilReportDao();
			report = (UtilReport) dao.findByBid(id);
			ids = report.getIds();
		}
		String startTime = request.getParameter("startdate");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String toTime = request.getParameter("todate");
		int beginHour = getParaIntValue("beginHour");
		int endHour = getParaIntValue("endHour");
		String begin = " " + beginHour + ":00:00";
		String end = " " + endHour + ":59:59";
		if (beginHour < 10) {
			begin = " 0" + beginHour + ":00:00";
		}
		if (endHour < 10) {
			end = " 0" + endHour + ":59:59";
		}
		if (startTime == null) {
			startTime = sdf.format(new Date()) + " 00:00:00";
		} else {
			startTime = startTime + begin;
		}
		if (toTime == null) {
			toTime = sdf.format(new Date()) + " 23:59:59";
		} else {
			toTime = toTime + end;
		}
		List<List> pingList = new ArrayList<List>();
		List<List> list = new ArrayList<List>();
		List<List> memList = new ArrayList<List>();
		List<List> utilInList = new ArrayList<List>();
		List<List> utilOutList = new ArrayList<List>();
		List<String> pingipList = new ArrayList<String>();
		List<String> ipList = new ArrayList<String>();
		List<String> memipList = new ArrayList<String>();
		List<String> portipList = new ArrayList<String>();
		StringBuffer pingHtml = new StringBuffer();
		StringBuffer cpuHtml = new StringBuffer();
		StringBuffer memHtml = new StringBuffer();
		StringBuffer portHtml = new StringBuffer();
		StringBuffer diskHtml = new StringBuffer();
		Vector<String> diskIpVec = new Vector<String>();
		List<StatisNumer> diskSta = new ArrayList<StatisNumer>();

		ReportHelper helper = new ReportHelper();
		HashMap valueMap = helper.getAllValue(ids, startTime, toTime);
		List<StatisNumer> gridList = new ArrayList<StatisNumer>();
		gridList = (List<StatisNumer>) valueMap.get("gridVlue");
		if (gridList != null && gridList.size() > 0) {
			pingHtml
					.append("<table  border=1 bordercolor='#C0C0C0' ><tr><td align='center'class='body-data-title' width=100 height=21>IP</td><td  class='body-data-title' height=21>当前连通率</td><td align='center' class='body-data-title'  height=21>最小连通率</td><td align='center' class='body-data-title'  height=21>平均连通率</td></tr>");
			cpuHtml
					.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>IP</td><td align='center' class='body-data-title'  height=21>当前利用率</td><td align='center' class='body-data-title'  height=21>最大利用率</td><td align='center' class='body-data-title'  height=21>平均利用率率</td></tr>");
			memHtml
					.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>IP</td><td align='center' class='body-data-title'  height=21>当前利用率</td><td align='center' class='body-data-title'  height=21>最大利用率</td><td align='center' class='body-data-title'  height=21>平均利用率</td></tr>");
			portHtml
					.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21>端口名称</td><td align='center' class='body-data-title'  height=21>当前流速</td><td align='center' class='body-data-title'  height=21>最大流速</td><td align='center' class='body-data-title'  height=21>平均流速</td></tr>");
			diskHtml
					.append("<table   border=1 bordercolor='#C0C0C0'><tr><td align='center' class='body-data-title' height=21 colspan=2>IP(磁盘名称)</td><td align='center' class='body-data-title'  height=21 colspan=2>当前利用率</td></tr>");

			String ip = "";
			String cur = "0";
			String max = "0";
			String min = "0";
			String avg = "0";
			String sname = "0";

			for (int j = 0; j < gridList.size(); j++) {
				StatisNumer number = new StatisNumer();
				number = (StatisNumer) gridList.get(j);
				String type = number.getType();
				ip = number.getIp();
				cur = number.getCurrent();
				min = number.getMininum();
				max = number.getMaximum();
				avg = number.getAverage();
				sname = number.getName();
				if (type.equals("gridPing")) {

					setInnerHtml(pingHtml, ip, cur, min, avg, "%");
				} else if (type.equals("gridCpu")) {
					setInnerHtml(cpuHtml, ip, cur, max, avg, "%");
				} else if (type.equals("gridMem")) {
					setInnerHtml(memHtml, ip, cur, max, avg, "%");
				} else if (type.equals("gridPortIn")) {
					setInnerHtml(portHtml, ip + "(" + sname + "入口)", cur, max, avg, "kb/s");
				} else if (type.equals("gridPortOut")) {
					setInnerHtml(portHtml, ip + "(" + sname + "出口)", cur, max, avg, "kb/s");
				} else if (type.equals("gridDisk")) {
					setInnerHtml(diskHtml, ip + "(" + sname + ")", cur, "%");
					diskSta.add(number);
					if (!diskIpVec.contains(ip))
						diskIpVec.add(ip);
				}
			}

			pingHtml.append("</table>");
			cpuHtml.append("</table>");
			memHtml.append("</table>");
			portHtml.append("</table>");
			diskHtml.append("</table>");
		}
		ReportValue pingValue = new ReportValue();
		pingValue = (ReportValue) valueMap.get("ping");

		pingipList = pingValue.getIpList();
		pingList = pingValue.getListValue();

		ReportValue memValue = new ReportValue();
		memValue = (ReportValue) valueMap.get("mem");
		memipList = memValue.getIpList();
		memList = memValue.getListValue();

		ReportValue cpuValue = new ReportValue();
		cpuValue = (ReportValue) valueMap.get("cpu");
		ipList = cpuValue.getIpList();
		list = cpuValue.getListValue();

		ReportValue portValue = new ReportValue();
		portValue = (ReportValue) valueMap.get("port");
		portipList = portValue.getIpList();
		utilInList = portValue.getListValue();
		utilOutList = portValue.getListTemp();
		String netdata = "";
		netdata = makeAmChartData(list, ipList);
		String pingdata = "";
		pingdata = makeAmChartData(pingList, pingipList);
		String memdata = makeAmChartData(memList, memipList);
		String portdata = "";
		StringBuffer portsb = new StringBuffer();

		if ((utilInList != null && utilInList.size() > 0) || (utilOutList != null && utilOutList.size() > 0)) {

			for (int k = 0; k < utilInList.size(); k++) {
				List portdataList = (List) utilInList.get(k);
				if (portdataList != null && portdataList.size() > 0) {
					portsb.append("<chart><series>");
					for (int i = 0; i < portdataList.size(); i++) {
						Vector v = new Vector();
						v = (Vector) portdataList.get(i);
						portsb.append("<value xid='");
						portsb.append(i);
						portsb.append("'>");
						portsb.append(v.get(1));
						portsb.append("</value>");
					}
					portsb.append("</series><graphs>");
					break;
				}
			}

			if (utilInList != null && utilInList.size() > 0) {
				for (int j = 0; j < utilInList.size(); j++) {

					List portdataList1 = (List) utilInList.get(j);
					if (portdataList1 != null && portdataList1.size() > 0) {
						portsb.append("<graph title='" + (String) portipList.get(j) + "-入口' bullet='round' bullet_size='4'>");
						for (int i = 0; i < portdataList1.size(); i++) {

							Vector v = new Vector();
							v = (Vector) portdataList1.get(i);
							portsb.append("<value xid='");
							portsb.append(i);
							portsb.append("'>");
							portsb.append(v.get(0));
							portsb.append("</value>");
						}

						portsb.append("</graph>");
					}
				}
			}
			if (utilOutList != null && utilOutList.size() > 0) {
				for (int j = 0; j < utilOutList.size(); j++) {
					List portdataList1 = (List) utilOutList.get(j);
					if (portdataList1 != null && portdataList1.size() > 0) {
						portsb.append("<graph title='" + (String) portipList.get(j) + "-出口' bullet='round' bullet_size='4'>");
						for (int i = 0; i < portdataList1.size(); i++) {

							Vector v = new Vector();
							v = (Vector) portdataList1.get(i);
							portsb.append("<value xid='");
							portsb.append(i);
							portsb.append("'>");
							portsb.append(v.get(0));
							portsb.append("</value>");
						}

						portsb.append("</graph>");
					}
				}

			}
			if (portsb.toString().equals("") || portsb.toString() == null) {

			} else {
				portsb.append("</graphs></chart>");
				portdata = portsb.toString();
			}

		} else {
			portdata = "0";
		}

		String diskdata = getDiskAmData(diskSta, diskIpVec);
		Map<String, String> map = new HashMap<String, String>();
		map.put("pingdata", pingdata);
		map.put("netdata", netdata);
		map.put("memdata", memdata);
		map.put("portdata", portdata);
		map.put("diskdata", diskdata);

		map.put("pingHtml", pingHtml.toString());
		map.put("cpuHtml", cpuHtml.toString());
		map.put("memHtml", memHtml.toString());
		map.put("portHtml", portHtml.toString());
		map.put("diskHtml", diskHtml.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	private String getDiskAmData(List<StatisNumer> list, Vector<String> ipVector) {
		String diskData = "0";
		StringBuffer diskBuffer = new StringBuffer();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (ipVector != null && ipVector.size() > 0) {
			for (int i = 0; i < ipVector.size(); i++) {
				if (map.get(ipVector.get(i)) == null) {
					map.put(ipVector.get(i), i);
				}
			}
		}

		String[] colorStr = new String[] { "#FF6600", "#FCD202", "#B0DE09", "#0D8ECF", "#A52A2A", "#33FF33", "#FF0033", "#9900FF", "#FFFF00", "#0000FF", "#A52A2A", "#23f266" };
		if (list != null && list.size() > 0) {
			diskBuffer.append("<chart><series>");
			for (int i = 0; i < list.size(); i++) {
				StatisNumer numer = new StatisNumer();
				numer = list.get(i);
				String name1 = numer.getName();

				diskBuffer.append("<value xid='" + i + "'>");
				diskBuffer.append(name1 + "</value>");

			}
			diskBuffer.append("</series><graphs>");
			diskBuffer.append("<graph >");
			for (int i = 0; i < list.size(); i++) {
				StatisNumer numer = new StatisNumer();
				numer = list.get(i);
				String ip1 = numer.getIp();
				String cur1 = numer.getCurrent().replace("%", "");

				diskBuffer.append("<value xid='" + i + "' color='" + colorStr[map.get(ip1)] + "'>");
				diskBuffer.append(cur1 + "</value>");

			}
			diskBuffer.append("</graph>");
			diskData = diskBuffer.toString();
		} else {
			diskData = "0";
		}
		return diskData;
	}

	/**
	 * 获取 list
	 * 
	 * @param <code>DBVo</code>
	 * @return
	 */
	public List getList(String[] bids) {
		List list = new ArrayList();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		int flag = 0;
		if (bids != null && bids.length > 0) {
			for (int i = 0; i < bids.length; i++) {
				if (bids[i].trim().length() > 0) {
					if (flag == 0) {
						// s.append(" and ( bid like '%," + bids[i].trim() +
						// ",%' ");
						s2.append(" and ( bid like '%" + bids[i].trim() + "%' ");
						flag = 1;
					} else {
						// flag = 1;
						// s.append(" or bid like '%," + bids[i].trim() + ",%'
						// ");
						s2.append(" or bid like '%" + bids[i].trim() + "%' ");
					}
				}
			}
			s2.append(") ");
		}
		sql2.append("select * from app_db_node where 1=1 " + s2.toString());

		DBDao dao = new DBDao();
		try {
			list = dao.findByCriteria(sql2.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return list;
	}

	private void station() {
		String select = request.getParameter("select");
		DaoInterface field = new fieldDao();
		List list = field.loadAll(" where backbone_id = '" + select + "'");

		System.out.println("###############" + select);

		field vo = null;
		StringBuffer sb = new StringBuffer();
		// if(select.equals("1") || select.equals("2")){
		sb.append("<b>场站名：</b>&nbsp;&nbsp;&nbsp;<select id='id1' size=1 name='select1' size=50 style='width:125px;' onchange='unionSelect()'>");
		for (int i = 0; i < list.size(); i++) {
			vo = (field) list.get(i);
			// if(select.equals(vo.getSection())){
			sb.append("<option value='" + vo.getId() + "' >" + vo.getName() + "</option>");
			// } else if(select.equals(vo.getSection())){
			// sb.append("<option value='"+vo.getId()+"'
			// >"+vo.getName()+"</option>"); }
		}
		sb.append("<INPUT type='button' class='formStyle' value='查询' onclick=' return doQuery()'>");
		// }else{
		// sb.append("该平面下，无对应的场站！");
		// }
		Map<String, String> map = new HashMap<String, String>();
		map.put("option", sb.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	private void backbone() {
		String select = request.getParameter("select");
		DaoInterface alltype = new alltypeDao();
		List list = alltype.loadAll(" where section = '" + select + "'");
		alltype vo = null;
		StringBuffer sb = new StringBuffer();
		if (list != null && list.size() != 0) {
			sb.append("<b>厂 站：</b>&nbsp;&nbsp;&nbsp;<select id='id2' size=1 name='backbone' style='width:125px;' >");
			sb.append("<option value=0>请选择</option>");
			for (int i = 0; i < list.size(); i++) {
				vo = (alltype) list.get(i);
				if (select.equals("")) {
					sb.append("<option value='" + vo.getId() + "' >" + vo.getBackbone_name() + "</option>");
				} else if (select.equals("")) {
					sb.append("<option value='" + vo.getId() + "' >" + vo.getBackbone_name() + "</option>");
				}
			}
		} else {
			sb.append("该平面下，无对应的场站！");
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("option", sb.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	public void check() {
		StringBuffer sb = new StringBuffer();
		Map<String, String> map = new HashMap<String, String>();
		map.put("option", sb.toString());
		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();

	}
}
