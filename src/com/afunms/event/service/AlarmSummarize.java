package com.afunms.event.service;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.afunms.common.util.CreateAlarmMetersPic;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.topology.util.XmlOperator;

/*******************************************************************************
 * Title:AlarmSummarize Description:告警信息汇总service类 Company dhccs
 * 
 * @author zhangcw Mar 25, 2011 11:44:59 AM
 *         *******************************************************
 */

@SuppressWarnings("unchecked")
public class AlarmSummarize {
	private DBManager conn;
	private ResultSet rs;
	private double closedRatio = 0;// 关闭率

	public HashMap<String, ?> getData(String filename) {
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		conn = new DBManager();
		try {
			String[][] tabledata = gettableData();
			dataMap.put("tabledata", tabledata);
			String piedata = getpieData();
			dataMap.put("piedata", piedata);
			String columndata = getcolumnData();
			dataMap.put("columndata", columndata);
			String dayalarmData = getDayAlarmData();
			dataMap.put("dayalarmData", dayalarmData);
			String weekalarmData = getWeekAlarmData();
			dataMap.put("weekalarmData", weekalarmData);
			String closedAlarmPicFile = this.closedAlarmPic(filename);
			dataMap.put("closedAlarmPicFile", closedAlarmPicFile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return dataMap;
	}

	/**
	 * 生成报警表格数据
	 * 
	 * @return
	 */
	public String[][] gettableData() {
		String[][] dataStr = new String[][] { { "类别", "提示", "普通", "严重", "紧急" }, { "网络告警", "0", "0", "0", "0" }, { "设备告警", "0", "0", "0", "0" }, { "服务器告警", "0", "0", "0", "0" },
				{ "数据库告警", "0", "0", "0", "0" }, { "中间件告警", "0", "0", "0", "0" }, { "应用告警", "0", "0", "0", "0" }, { "存储告警", "0", "0", "0", "0" }, { "业务告警", "0", "0", "0", "0" },
				{ "安全告警", "0", "0", "0", "0" } };
		try {
			String subtype = "";
			int level = 0;
			// --修改为只查询当天数据
			StringBuilder sb = new StringBuilder();
			sb.append(" select subtype,level1,count(1) as cnt from system_eventlist ");
			if (SystemConstant.DBType.equals("mysql")) {
				sb.append(" where to_days(recordtime) = to_days(now()) ");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sb.append(" where to_char(recordtime,'yyyy-mm-dd') = to_char(sysdate,'yyyy-mm-dd')");
			}
			sb.append(" group by subtype,level1 ");
			rs = conn.executeQuery(sb.toString());
			while (rs.next()) {
				subtype = rs.getString("subtype");
				if (subtype.equalsIgnoreCase("net") || subtype.equalsIgnoreCase("dns")) {// 网络告警
					level = rs.getInt("level1");
					dataStr[1][level + 1] = String.valueOf((Integer.parseInt(dataStr[1][level + 1]) + Integer.parseInt(rs.getString("cnt"))));
				} else if (subtype.equalsIgnoreCase("host")) {// 服务器告警
					level = rs.getInt("level1");
					dataStr[3][level + 1] = String.valueOf((Integer.parseInt(dataStr[3][level + 1]) + Integer.parseInt(rs.getString("cnt"))));
				} else if (subtype.equalsIgnoreCase("db")) {// 数据库告警
					level = rs.getInt("level1");
					dataStr[4][level + 1] = rs.getString("cnt");
				} else if (subtype.equalsIgnoreCase("domino") || subtype.equalsIgnoreCase("tomcat") || subtype.equalsIgnoreCase("cics") || subtype.equalsIgnoreCase("mq")
						|| subtype.equalsIgnoreCase("wasserver") || subtype.equalsIgnoreCase("weblogic") || subtype.equalsIgnoreCase("iis") || subtype.equalsIgnoreCase("jboss")
						|| subtype.equalsIgnoreCase("apache")) { // 中间件告警
					level = rs.getInt("level1");
					dataStr[5][level + 1] = String.valueOf((Integer.parseInt(dataStr[5][level + 1]) + Integer.parseInt(rs.getString("cnt"))));
				}
				// 有待继续添加
				// 6应用告警
				// 7存储告警
				// 8业务告警
				else if (subtype.equalsIgnoreCase("bus")) {// 业务告警
					level = rs.getInt("level1");
					dataStr[8][level + 1] = rs.getString("cnt");
				}
				// 9安全告警

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataStr;
	}

	/**
	 * 生成告警状态饼图数据
	 * 
	 * @return
	 */
	public String getpieData() {
		StringBuffer dataStr = new StringBuffer();
		Map<String, String> map = new TreeMap<String, String>();
		// 初始值为0
		map.put("0", "0");
		map.put("1", "0");
		map.put("2", "0");
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" select managesign as sign,count(1) as cnt from system_eventlist ");
			if (SystemConstant.DBType.equals("mysql")) {
				sb.append(" where to_days(recordtime) = to_days(now()) ");
				sb.append(" group by (managesign) ; ");
			} else if (SystemConstant.DBType.equals("oracle")) {
				sb.append(" where to_char(recordtime,'yyyy-mm-dd') = to_char(sysdate,'yyyy-mm-dd')");
				sb.append(" group by managesign ");
			}
			rs = conn.executeQuery(sb.toString());
			while (rs.next())
				map.put(rs.getString("sign"), rs.getString("cnt"));
		} catch (Exception e) {
			SysLogger.error("AlarmSummarize:", e);
		}
		int unprocess = Integer.valueOf(map.get("0"));
		int process = Integer.valueOf(map.get("1"));
		int closed = Integer.valueOf(map.get("2"));
		if ((unprocess + process + closed) != 0) {
			this.closedRatio = closed / (unprocess + process + closed);
		}
		if (unprocess == 0 && process == 0 && closed == 0) {
			dataStr.append("0");
		} else {
			dataStr.append("未处理;").append(unprocess).append(";false;FFCC00\\n");
			dataStr.append("处理中;").append(process).append(";false;6666FF\\n");
			dataStr.append("关闭;").append(closed).append(";false;CC33FF\\n");
		}
		return dataStr.toString();
	}

	/**
	 * 生成当日第小时告警数
	 * 
	 * @return
	 */
	public String getDayAlarmData() {
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		for (int i = 0; i < 24; i++) {
			map.put(i, 0);
		}
		try {
			String sql = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql = "select HOUR(recordtime)  as h,count(1) as cnt from system_eventlist where  DATE(recordtime)=CURDATE() group by h;";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql = "select to_char(s.recordtime,'HH24') as h,count(*) as cnt from system_eventlist s where  to_char(s.recordtime,'YYYY-MM-DD')= to_char(sysdate) group by to_char(s.recordtime,'HH24')";
			}
			rs = conn.executeQuery(sql);
			while (rs.next())
				map.put(rs.getInt("h"), rs.getInt("cnt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer dataStr = new StringBuffer();
		for (int i = 0; i < 24; i++) {
			dataStr.append(i).append(";").append(map.get(i)).append("\\n");
		}
		return dataStr.toString();
	}

	/**
	 * 生成近一周告警条数 线图
	 * 
	 * @return
	 */
	public String getWeekAlarmData() {
		Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < 7; i++) {
			int day = cal.get(Calendar.DATE);
			map.put(day, 0);
			cal.add(Calendar.DATE, -1);
		}
		try {
			String sql = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql = "select DAY(recordtime)  as d,count(1) as cnt from system_eventlist where  DATEDIFF(CURDATE(),recordtime)<7 group by d;";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql = "select to_char(s.recordtime,'dd') as d ,count(*) as cnt from system_eventlist s  where to_char(s.recordtime,'d')<'7' group by s.recordtime";
			}
			rs = conn.executeQuery(sql);
			while (rs.next())
				map.put(rs.getInt("d"), rs.getInt("cnt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer dataStr = new StringBuffer();
		for (Integer date : map.keySet()) {
			dataStr.append(date).append(";").append(map.get(date)).append("\\n");
		}
		return dataStr.toString();
	}

	/**
	 * 近一周频率较高的报警数据 线图
	 * 
	 * @return
	 */
	public String getcolumnData() {
		String[] titleStr = new String[] { "网络告警", "设备告警", "服务器警告", "数据库警告", "中间件警告", "应用告警", "桌面告警", "存储告警", "业务告警", "安全告警" };
		String[] colorStr = new String[] { "#33CCFF", "#003366", "#33FF33", "#FF0033", "#9900FF", "#FFFF00", "#333399", "#0000FF", "#A52A2A", "#23f266" };
		String[] dataStr = new String[] { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" };
		try {
			String subtype = "";
			String sql = "";
			if (SystemConstant.DBType.equals("mysql")) {
				sql = "select subtype,count(1) as cnt from system_eventlist where DATEDIFF(CURDATE(),recordtime)<7 group by subtype;";
			} else if (SystemConstant.DBType.equals("oracle")) {
				sql = "select subtype ,count(*) as cnt from system_eventlist s  where to_char(s.recordtime,'d')<'7' group by subtype";
			}
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				subtype = rs.getString("subtype");

				if (subtype.equalsIgnoreCase("net") || subtype.equalsIgnoreCase("dns")) {// 1网络告警
					dataStr[0] = String.valueOf((Integer.parseInt(dataStr[0]) + Integer.parseInt(rs.getString("cnt"))));
				} else if (subtype.equalsIgnoreCase("host")) {// 3服务器告警
					dataStr[2] = rs.getString("cnt");
				} else if (subtype.equalsIgnoreCase("db")) {// 4数据库告警
					dataStr[3] = rs.getString("cnt");
				} else if (subtype.equalsIgnoreCase("domino") || subtype.equalsIgnoreCase("tomcat") || subtype.equalsIgnoreCase("cics") || subtype.equalsIgnoreCase("mq")
						|| subtype.equalsIgnoreCase("wasserver") || subtype.equalsIgnoreCase("weblogic") || subtype.equalsIgnoreCase("iis") || subtype.equalsIgnoreCase("jboss")
						|| subtype.equalsIgnoreCase("apache")) { // 5中间件告警
					dataStr[4] = String.valueOf((Integer.parseInt(dataStr[4]) + Integer.parseInt(rs.getString("cnt"))));
				} else if (subtype.equalsIgnoreCase("bus")) {// 8业务告警
					dataStr[7] = rs.getString("cnt");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<?xml version='1.0' encoding='gb2312'?>");
		xmlStr.append("<chart><series>");
		for (int i = 0; i < 10; i++) {
			xmlStr.append("<value xid='").append(i).append("'>").append(titleStr[i]).append("</value>");

		}
		xmlStr.append("</series><graphs>");
		xmlStr.append("<graph gid='0'>");
		for (int i = 0; i < 10; i++) {
			xmlStr.append("<value xid='").append(i).append("' color='").append(colorStr[i]).append("'>").append(dataStr[i]).append("</value>");
		}
		xmlStr.append("</graph>");
		xmlStr.append("</graphs></chart>");
		return xmlStr.toString();
	}

	/**
	 * 生成告警关闭率表盘数据
	 * 
	 * @param filename
	 *            生成表盘图片的文件名
	 * @return
	 */
	public String closedAlarmPic(String filename) {
		CreateAlarmMetersPic createAlarmMetersPic = new CreateAlarmMetersPic();
		String file = createAlarmMetersPic.createClosedAlarmPic(filename, this.closedRatio);
		return file;

	}

	/*public List<EventList> SendLastestEventListByComet(String bid,String xml){
		List nodelist = new ArrayList();
		try {
			//解析出xml拓扑资源文件中所有的设备节点
			XmlOperator xmlOpr = new XmlOperator();
			xmlOpr.setFile(xml);
			xmlOpr.init4updateXml();
			nodelist = xmlOpr.getAllNodes();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		//取得
		
	}*/
	public List<EventList> getRealTimeAlarm(String bid, String xml) {
		return null;
		
	}
	public List<EventList> getLastestEventList1(String bid, String xml) {
		List nodelist = new ArrayList();
		try {
			XmlOperator xmlOpr = new XmlOperator();
			xmlOpr.setFile(xml);
			xmlOpr.init4updateXml();
			nodelist = xmlOpr.getAllNodes();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		List sqllist = new ArrayList();
		List<EventList> list = null;
		// 查出当天的CheckEvent 告警等级是3的告警
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		if (bid != null) {
			if (bid != "-1") {
				String[] bids = bid.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (_flag == 0) {
								s.append(" and ( businessid like '%," + bids[i].trim() + ",%' ");
								_flag = 1;
							} else {
								s.append(" or businessid like '%," + bids[i].trim() + ",%' ");
							}
						}
					}
					s.append(") ");
				}
			}
		}
		String sql = " ";
		for (int j = 0; j < nodelist.size(); j++) {
			String nodeid = ((String) nodelist.get(j)).split(":")[0];
			String category = ((String) nodelist.get(j)).split(":")[1];
			com.afunms.polling.base.Node nodes = null;
			if (nodeid.indexOf("hin") < 0) {
				StringBuffer ss = new StringBuffer();
				if (nodeid.indexOf("dbs") != -1) {
					Node mnode = (Node) PollingEngine.getInstance().getNodeByCategory("dbs", Integer.valueOf(nodeid.substring(3)));
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mnode);
					if (nodeDTO != null) {
						ss.append(" and nodeid=" + mnode.getId() + " and subtype='" + nodeDTO.getType() + "' ");
					}
				} else if (nodeid.indexOf("net") != -1) {
					nodes = (Host) PollingEngine.getInstance().getNodeByCategory(category, Integer.valueOf(nodeid.substring(3)));
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(nodes);
					if (nodeDTO != null) {
						ss.append(" and nodeid=" + nodes.getId() + " and subtype='" + nodeDTO.getType() + "' ");
					}
				} else {
					Node mnode = (Node) PollingEngine.getInstance().getNodeByCategory(category, Integer.valueOf(nodeid.substring(3)));
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mnode);
					if (nodeDTO != null) {
						ss.append(" and nodeid=" + mnode.getId() + " and subtype='" + nodeDTO.getType() + "' ");
					}
				}
				sql = "select  *,count(distinct content) from system_eventlist where managesign=0  and date(now())= date(recordtime) " + s + ss
						+ " group by content order by id desc";
				sqllist.add(sql);
			}
		}
		if (sqllist != null && sqllist.size() > 0) {
			EventListDao dao = new EventListDao();
			try {
				list = dao.findByCriteria(sqllist);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		if (list == null) {
			List<EventList> listevent = new ArrayList<EventList>();
			return listevent;
		}
		return list;
	}

	/**
	 * 测试方法
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

	}
}
