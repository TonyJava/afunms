package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class WeblogicConfigDao extends BaseDao implements DaoInterface {

	public WeblogicConfigDao() {
		super("nms_weblogicconfig");
	}

	public boolean delete(String[] ids) {
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				delete(ids[i]);
			}
		}
		return true;
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			WeblogicConfig pvo = (WeblogicConfig) findByID(id + "");
			String ipstr = pvo.getIpAddress();
			String allipstr = SysUtil.doip(ipstr);

			CreateTableManager ctable = new CreateTableManager();

			ctable.deleteTable("weblogicping", allipstr, "weblogicping");// Ping
			ctable.deleteTable("weblgicphour", allipstr, "weblgicphour");// Ping
			ctable.deleteTable("weblgicpday", allipstr, "weblgicpday");// Ping
			conn.addBatch("delete from nms_weblogicconfig where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List getByFlag(Integer flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_weblogicconfig where mon_flag= ");
		sql.append(flag);
		return findByCriteria(sql.toString());
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		WeblogicConfig vo = new WeblogicConfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setAlias(rs.getString("name"));
			vo.setIpAddress(rs.getString("ipaddress"));
			vo.setCommunity(rs.getString("community"));
			vo.setPortnum(rs.getInt("portnum"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setNetid(rs.getString("netid"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setSupperid(rs.getInt("supperid"));
			vo.setServerName(rs.getString("servername"));
			vo.setServerAddr(rs.getString("serveraddr"));
			vo.setServerPort(rs.getString("serverport"));
			vo.setDomainName(rs.getString("domainname"));
			vo.setDomainPort(rs.getString("domainport"));
			vo.setDomainVersion(rs.getString("domainversion"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		boolean flag = true;
		WeblogicConfig vo1 = (WeblogicConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql
				.append("insert into nms_weblogicconfig(id,name,ipaddress,community,portnum,sendmobiles,mon_flag,netid,sendemail,sendphone,supperid,servername,serveraddr,serverport,domainname,domainport,domainversion) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getAlias());
		sql.append("','");
		sql.append(vo1.getIpAddress());
		sql.append("','");
		sql.append(vo1.getCommunity());
		sql.append("','");
		sql.append(vo1.getPortnum());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getMon_flag());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendphone());
		sql.append("','");
		sql.append(vo1.getSupperid());

		sql.append("','");
		sql.append(vo1.getServerName());
		sql.append("','");
		sql.append(vo1.getServerAddr());
		sql.append("','");
		sql.append(vo1.getServerPort());
		sql.append("','");
		sql.append(vo1.getDomainName());
		sql.append("','");
		sql.append(vo1.getDomainPort());
		sql.append("','");
		sql.append(vo1.getDomainVersion());

		sql.append("')");
		try {
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			String ip = vo1.getIpAddress();
			String allipstr = SysUtil.doip(ip);
			ctable.createTable("weblogicping", allipstr, "weblogicping");// Ping
			ctable.createTable("weblgicphour", allipstr, "weblgicphour");// Ping
			ctable.createTable("weblgicpday", allipstr, "weblgicpday");// Ping

		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	public boolean update(BaseVo vo) {
		boolean flag = true;
		WeblogicConfig vo1 = (WeblogicConfig) vo;
		WeblogicConfig pvo = (WeblogicConfig) findByID(vo1.getId() + "");
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_weblogicconfig set name='");
		sql.append(vo1.getAlias());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpAddress());
		sql.append("',community='");
		sql.append(vo1.getCommunity());
		sql.append("',portnum='");
		sql.append(vo1.getPortnum());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',mon_flag='");
		sql.append(vo1.getMon_flag());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo1.getSendphone());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());

		sql.append("',servername='");
		sql.append(vo1.getServerName());
		sql.append("',serveraddr='");
		sql.append(vo1.getServerAddr());
		sql.append("',serverport='");
		sql.append(vo1.getServerPort());
		sql.append("',domainname='");
		sql.append(vo1.getDomainName());
		sql.append("',domainport='");
		sql.append(vo1.getDomainPort());
		sql.append("',domainversion='");
		sql.append(vo1.getDomainVersion());
		sql.append("' where id=" + vo1.getId());

		try {
			saveOrUpdate(sql.toString());
			if (!vo1.getIpAddress().equals(pvo.getIpAddress())) {
				String ipstr = pvo.getIpAddress();
				String allipstr = SysUtil.doip(ipstr);
				CreateTableManager ctable = new CreateTableManager();
				try {
					ctable.deleteTable("weblogicping", allipstr, "weblogicping");// Ping
					ctable.deleteTable("weblgicphour", allipstr, "weblgicphour");// Ping
					ctable.deleteTable("weblgicpday", allipstr, "weblgicpday");// Ping

					// 测试生成表
					String ip = vo1.getIpAddress();
					allipstr = SysUtil.doip(ip);
					ctable = new CreateTableManager();
					ctable.createTable("weblogicping", allipstr, "weblogicping");// Ping
					ctable.createTable("weblgicphour", allipstr, "weblgicphour");// Ping
					ctable.createTable("weblgicpday", allipstr, "weblgicpday");// Ping
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;

	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(PingCollectEntity pingdata) {
		if (pingdata == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = pingdata.getIpaddress();
			if (pingdata.getRestype().equals("dynamic")) {
				String allipstr = "";
				allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";
				tablename = "weblogicping" + allipstr;
				String sql = "";
				if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','"
							+ pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit()
							+ "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "','" + time + "')";
				} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					sql = "insert into " + tablename + "(ipaddress,restype,category,entity,subentity,unit,chname,bak,count,thevalue,collecttime) " + "values('" + ip + "','"
							+ pingdata.getRestype() + "','" + pingdata.getCategory() + "','" + pingdata.getEntity() + "','" + pingdata.getSubentity() + "','" + pingdata.getUnit()
							+ "','" + pingdata.getChname() + "','" + pingdata.getBak() + "'," + pingdata.getCount() + ",'" + pingdata.getThevalue() + "',to_date('" + time
							+ "','YYYY-MM-DD HH24:MI:SS'))";
				}
				conn.executeUpdate(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			conn.close();

		}
		return true;
	}

	public List getWeblogicByBID(Vector bids) {
		StringBuffer sql = new StringBuffer();
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " where ( netid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or netid like '%," + bids.get(i) + ",%' ";
				}
			}
			wstr = wstr + ")";
		}
		sql.append("select * from nms_weblogicconfig " + wstr);
		return findByCriteria(sql.toString());
	}

	public int getidByIp(String ip) {
		String string = "select id from nms_weblogicconfig where ipaddress =" + "'" + ip + "'";
		int id = 0;
		ResultSet rSet = null;
		rSet = conn.executeQuery(string);
		try {
			while (rSet.next()) {
				id = rSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rSet != null) {
				try {
					rSet.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			conn.close();
		}
		return id;
	}

	public Hashtable getPingDataById(String ip, Integer id, String starttime, String endtime) {
		Hashtable hash = new Hashtable();
		if (!starttime.equals("") && !endtime.equals("")) {
			List list1 = new ArrayList();
			String sql = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.thevalue,a.collecttime from weblogicping" + ip.replace(".", "_") + " a where " + "(a.collecttime >= '" + starttime + "' and  a.collecttime <= '"
						+ endtime + "') order by id";
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.thevalue from weblogicping" + ip.replace(".", "_") + " a where " + " (a.collecttime >= " + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')"
						+ " and  a.collecttime <= " + "to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')" + ") order by id";
			}
			int i = 0;
			double curPing = 0;
			double avgPing = 0;
			double minPing = 0;
			rs = conn.executeQuery(sql);
			try {
				while (rs.next()) {
					i = i + 1;
					Vector v = new Vector();
					String thevalue = rs.getString("thevalue");
					String collecttime = rs.getString("collecttime");
					thevalue = String.valueOf(Integer.parseInt(thevalue));
					v.add(0, thevalue);
					v.add(1, collecttime);
					v.add(2, "%");
					avgPing = avgPing + Float.parseFloat(thevalue);
					curPing = Float.parseFloat(thevalue);
					if (curPing < minPing)
						minPing = curPing;
					list1.add(v);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
					if (conn != null)
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			hash.put("list", list1);
			if (list1 != null && list1.size() > 0) {
				hash.put("avgPing", CEIString.round(avgPing / list1.size(), 2) + "");
			} else {
				hash.put("avgPing", "0");
			}
			hash.put("minPing", minPing + "");
			hash.put("curPing", curPing + "");
		}
		return hash;
	}

}