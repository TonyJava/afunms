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

import com.afunms.application.model.Tomcat;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CEIString;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.NetworkUtil;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class TomcatDao extends BaseDao implements DaoInterface {
	public TomcatDao() {
		super("app_tomcat_node");
	}

	public List getTomcatAll() {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from app_tomcat_node ");
		return findByCriteria(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		boolean flag = true;
		Tomcat vo = (Tomcat) baseVo;
		Tomcat pvo = (Tomcat) findByID(vo.getId() + "");

		StringBuffer sql = new StringBuffer();
		sql.append("update app_tomcat_node set alias='");
		sql.append(vo.getAlias());
		sql.append("',ip_address='");
		sql.append(vo.getIpAddress());
		sql.append("',ip_long=");
		sql.append(NetworkUtil.ip2long(vo.getIpAddress()));
		sql.append(",port='");
		sql.append(vo.getPort());
		sql.append("',users='");
		sql.append(vo.getUser());
		sql.append("',password='");
		sql.append(vo.getPassword());
		sql.append("',bid='");
		sql.append(vo.getBid());
		sql.append("',sendemail='");
		sql.append(vo.getSendemail());
		sql.append("',sendmobiles='");
		sql.append(vo.getSendmobiles());
		sql.append("',sendphone='");
		sql.append(vo.getSendphone());
		sql.append("',monflag=");
		sql.append(vo.getMonflag());
		sql.append(",version='");
		sql.append(vo.getVersion());
		sql.append("',jvmversion='");
		sql.append(vo.getJvmversion());
		sql.append("',jvmvender='");
		sql.append(vo.getJvmvender());
		sql.append("',os='");
		sql.append(vo.getOs());
		sql.append("',osversion='");
		sql.append(vo.getVersion());
		sql.append("',supperid='");
		sql.append(vo.getSupperid());
		sql.append("' where id=");
		sql.append(vo.getId());

		try {
			saveOrUpdate(sql.toString());
			if (!vo.getIpAddress().equals(pvo.getIpAddress())) {
				String ipstr = pvo.getIpAddress();
				String allipstr = SysUtil.doip(ipstr);
				CreateTableManager ctable = new CreateTableManager();
				ctable.deleteTable("tomcat_jvm", allipstr, "tomcat_jvm");// Ping
				ctable.deleteTable("tomcatping", allipstr, "tomcatping");// Ping
				ctable.deleteTable("tompingh", allipstr, "tompingh");// Ping
				ctable.deleteTable("tompingd", allipstr, "tompingd");// Ping
				String ip = vo.getIpAddress();
				allipstr = SysUtil.doip(ip);

				ctable = new CreateTableManager();
				ctable.createTable("tomcat_jvm", allipstr, "tomcat_jvm");// Ping
				ctable.createTable("tomcatping", allipstr, "tomcatping");// Ping
				ctable.createTable("tompingh", allipstr, "tompingh");// Ping
				ctable.createTable("tompingd", allipstr, "tompingd");// Ping
			}

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return flag;
	}

	public boolean save(BaseVo baseVo) {
		boolean flag = true;
		Tomcat vo = (Tomcat) baseVo;
		StringBuffer sql = new StringBuffer();
		sql
				.append("insert into app_tomcat_node(id,alias,ip_address,ip_long,port,users,password,monflag,bid,sendemail,sendmobiles,sendphone,version,jvmversion,jvmvender,os,osversion)values(");
		sql.append(vo.getId());
		sql.append(",'");
		sql.append(vo.getAlias());
		sql.append("','");
		sql.append(vo.getIpAddress());
		sql.append("',");
		sql.append(NetworkUtil.ip2long(vo.getIpAddress()));
		sql.append(",'");
		sql.append(vo.getPort());
		sql.append("','");
		sql.append(vo.getUser());
		sql.append("','");
		sql.append(vo.getPassword());
		sql.append("','");
		sql.append(vo.getMonflag());
		sql.append("','");
		sql.append(vo.getBid());
		sql.append("','");
		sql.append(vo.getSendemail());
		sql.append("','");
		sql.append(vo.getSendmobiles());
		sql.append("',");
		sql.append(vo.getSendphone());
		sql.append(",'");
		sql.append(vo.getVersion());
		sql.append("','");
		sql.append(vo.getJvmversion());
		sql.append("','");
		sql.append(vo.getJvmvender());
		sql.append("','");
		sql.append(vo.getOs());
		sql.append("','");
		sql.append(vo.getOsversion());
		sql.append("')");
		try {
			conn = new DBManager();
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			String ip = vo.getIpAddress();
			String allipstr = SysUtil.doip(ip);
			ctable.createTable("tomcat_jvm", allipstr, "tomcat_jvm");// Ping
			ctable.createTable("tomcatping", allipstr, "tomcatping");// Ping
			ctable.createTable("tompingh", allipstr, "tompingh");// Ping
			ctable.createTable("tompingd", allipstr, "tompingd");// Ping
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			conn.close();
		}
		return flag;
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			Tomcat pvo = (Tomcat) findByID(id + "");
			String ipstr = pvo.getIpAddress();
			String allipstr = SysUtil.doip(ipstr);

			CreateTableManager ctable = new CreateTableManager();
			ctable.deleteTable("tomcat_jvm", allipstr, "tomcat_jvm");// Ping
			ctable.deleteTable("tomcatping", allipstr, "tomcatping");// Ping
			ctable.deleteTable("tompingh", allipstr, "tompingh");// Ping
			ctable.deleteTable("tompingd", allipstr, "tompingd");// Ping

			conn.addBatch("delete from app_tomcat_node where id=" + id);
			conn.addBatch("delete from topo_node_single_data where node_id=" + id);
			conn.addBatch("delete from topo_node_multi_data where node_id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	// 处理Ping得到的数据，放到历史表里
	public synchronized boolean createHostData(PingCollectEntity pingdata) {
		if (pingdata == null)
			return false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String ip = pingdata.getIpaddress();
			if (pingdata.getRestype().equals("dynamic")) {
				String allipstr = SysUtil.doip(ip);
				Calendar tempCal = (Calendar) pingdata.getCollecttime();
				Date cc = tempCal.getTime();
				String time = sdf.format(cc);
				String tablename = "";
				String type = pingdata.getCategory();
				if ("TomcatPing".equals(type)) {
					tablename = "tomcatping" + allipstr;
				} else if ("tomcat_jvm".equals(type)) {
					tablename = "tomcat_jvm" + allipstr;
				}
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

	public List getTomcatByBID(Vector bids) {
		StringBuffer sql = new StringBuffer();
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " where ( bid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or bid like '%," + bids.get(i) + ",%' ";
				}
			}
			wstr = wstr + ")";
		}
		sql.append("select * from app_tomcat_node " + wstr);
		return findByCriteria(sql.toString());
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Tomcat vo = new Tomcat();
		try {
			vo.setId(rs.getInt("id"));
			vo.setAlias(rs.getString("alias"));
			vo.setIpAddress(rs.getString("ip_address"));
			vo.setPort(rs.getString("port"));
			vo.setUser(rs.getString("users"));
			vo.setPassword(rs.getString("password"));
			vo.setBid(rs.getString("bid"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setMonflag(rs.getInt("monflag"));
			vo.setVersion(rs.getString("version"));
			vo.setJvmversion(rs.getString("jvmversion"));
			vo.setJvmvender(rs.getString("jvmvender"));
			vo.setOs(rs.getString("os"));
			vo.setOsversion(rs.getString("osversion"));
			vo.setSupperid(rs.getInt("supperid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public int getidByIp(String ip) {
		String string = "select id from app_tomcat_node where ip_address =" + "'" + ip + "'";
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

	/**
	 * 从数据库中获取tomcat的采集的数据信息
	 */
	public Hashtable getTomcatDataHashtable(String nodeid) throws SQLException {
		Hashtable hm = new Hashtable();
		try {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select * from nms_tomcat_temp where nodeid = '");
			sqlBuffer.append(nodeid);
			sqlBuffer.append("'");
			rs = conn.executeQuery(sqlBuffer.toString());
			while (rs.next()) {
				String entity = rs.getString("entity");
				String value = rs.getString("value");
				hm.put(entity, value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return hm;
	}

	// 插入数据验证查询
	public List getListCheckData(Tomcat tomcat) {
		String sql = "select * from app_tomcat_node where ip_address ='" + tomcat.getIpAddress() + "'";
		sql += " and port='" + tomcat.getPort() + "'";
		conn = new DBManager();
		rs = conn.executeQuery(sql);
		List list = new ArrayList();
		try {
			while (rs.next()) {
				Tomcat _tomcat = new Tomcat();
				_tomcat.setAlias(rs.getString("alias"));
				list.add(_tomcat);
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
		return list;
	}

	public Hashtable getPingDataById(String ip, Integer id, String starttime, String endtime) {
		Hashtable hash = new Hashtable();
		if (!starttime.equals("") && !endtime.equals("")) {
			List list1 = new ArrayList();
			String sql = "";
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.thevalue,a.collecttime from tomcatping" + ip.replace(".", "_") + " a where " + "(a.collecttime >= '" + starttime + "' and  a.collecttime <= '"
						+ endtime + "') order by id";
				;
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				sql = "select a.thevalue from tomcatping" + ip.replace(".", "_") + " a where " + " (a.collecttime >= " + "to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS')"
						+ " and  a.collecttime <= " + "to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS')" + ") order by id";
				;
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