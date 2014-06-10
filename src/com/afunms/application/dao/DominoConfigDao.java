package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.DominoConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class DominoConfigDao extends BaseDao implements DaoInterface {

	public DominoConfigDao() {
		super("nms_dominoconfig");
	}

	public boolean delete(String[] ids) {
		boolean result = true;
		try {
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					try {
						DominoConfig pvo = (DominoConfig) findByID(ids[i]);
						String ipstr = pvo.getIpaddress();
						String allipstr = SysUtil.doip(ipstr);
						conn = new DBManager();
						CreateTableManager ctable = new CreateTableManager();
						ctable.deleteTable("dominoping", allipstr, "dominoping");// Ping
						ctable.deleteTable("dompinghour", allipstr, "dompinghour");// Ping
						ctable.deleteTable("dompingday", allipstr, "dompingday");// Ping

						ctable.deleteTable("dominocpu", allipstr, "cpu");// cpu
						ctable.deleteTable("domcpuhour", allipstr, "domcpuhour");// cpu
						ctable.deleteTable("domcpuday", allipstr, "domcpuday");// cpu

						ctable.deleteTable("domstatus", allipstr, "domstatus");// 状态
						// IMAP/LDAP/POP3/SMTP
						ctable.deleteTable("domstshour", allipstr, "domstshour");// status
						ctable.deleteTable("domstatusday", allipstr, "domstatusday");// status

						ctable.deleteTable("domservmem", allipstr, "domservmem");// 服务器内存利用率
						ctable.deleteTable("domsemehour", allipstr, "domsemehour");// domservmem
						ctable.deleteTable("domsemeday", allipstr, "domsemeday");// domservmem

						ctable.deleteTable("domplatmem", allipstr, "domplatmem");// 平台内存利用率
						ctable.deleteTable("dopltmehour", allipstr, "dopltmehour");// domplatmem
						ctable.deleteTable("dopltmeday", allipstr, "dopltmeday");// domplatmem

						ctable.deleteTable("domdisk", allipstr, "domdisk");// 磁盘利用率
						ctable.deleteTable("domdiskhour", allipstr, "domdiskhour");// disk
						ctable.deleteTable("domdskmday", allipstr, "domdskmday");// disk

						conn.addBatch("delete from nms_dominoconfig where id=" + ids[i]);
						conn.executeBatch();
						result = true;
					} catch (Exception e) {
						SysLogger.error("DominoConfigDao.delete()", e);
					}
				}
			}
		} catch (Exception e) {
		} finally {
			conn.close();
		}
		return result;
	}

	@Override
	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_dominoconfig where id=" + id);
			if (rs.next())
				vo = loadFromRS(rs);
		} catch (Exception e) {
			SysLogger.error("DominoConfigDao.findByID()", e);
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		DominoConfig vo = new DominoConfig();

		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setCommunity(rs.getString("community"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setNetid(rs.getString("netid"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setSupperid(rs.getInt("supperid"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		boolean flag = true;
		DominoConfig vo1 = (DominoConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_dominoconfig(id,name,ipaddress,community,sendmobiles,mon_flag,netid,sendemail,sendphone,supperid) values(");
		sql.append(vo1.getId());
		sql.append(",'");
		sql.append(vo1.getName());
		sql.append("','");
		sql.append(vo1.getIpaddress());
		sql.append("','");
		sql.append(vo1.getCommunity());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("',");
		sql.append(vo1.getMon_flag());
		sql.append(",'");
		sql.append(vo1.getNetid());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendphone());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("')");
		try {
			saveOrUpdate(sql.toString());
			CreateTableManager ctable = new CreateTableManager();
			String ip = vo1.getIpaddress();
			String allipstr = SysUtil.doip(ip);
			try {
				conn = new DBManager();
				ctable.createTable("dominoping", allipstr, "dominoping");// Ping
				ctable.createTable("dompinghour", allipstr, "dompinghour");// Ping
				ctable.createTable("dompingday", allipstr, "dompingday");// Ping

				ctable.createTable("dominocpu", allipstr, "cpu");// cpu
				ctable.createTable("domcpuhour", allipstr, "domcpuhour");// cpu
				ctable.createTable("domcpuday", allipstr, "domcpuday");// cpu

				ctable.createTable("domstatus", allipstr, "domstatus");// 状态
				// IMAP/LDAP/POP3/SMTP
				ctable.createTable("domstshour", allipstr, "domstshour");// status
				ctable.createTable("domstatusday", allipstr, "domstatusday");// status

				ctable.createTable("domservmem", allipstr, "domservmem");// 服务器内存利用率
				ctable.createTable("domsemehour", allipstr, "domsemehour");// domservmem
				ctable.createTable("domsemeday", allipstr, "domsemeday");// domservmem

				ctable.createTable("domplatmem", allipstr, "domplatmem");// 平台内存利用率
				ctable.createTable("dopltmehour", allipstr, "dopltmehour");// domplatmem
				ctable.createTable("dopltmeday", allipstr, "dopltmeday");// domplatmem

				ctable.createTable("domdisk", allipstr, "domdisk");// 磁盘利用率
				ctable.createTable("domdiskhour", allipstr, "domdiskhour");// disk
				ctable.createTable("domdskmday", allipstr, "domdskmday");// disk
			} catch (Exception e) {

			}

		} catch (Exception e) {
			flag = false;
		} finally {
			try {
				conn.executeBatch();
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn.close();
		}
		return flag;

	}

	public boolean update(BaseVo vo) {
		boolean flag = true;
		DominoConfig vo1 = (DominoConfig) vo;
		DominoConfig pvo = (DominoConfig) findByID(vo1.getId() + "");
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_dominoconfig set name='");
		sql.append(vo1.getName());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpaddress());
		sql.append("',community='");
		sql.append(vo1.getCommunity());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',mon_flag=");
		sql.append(vo1.getMon_flag());
		sql.append(",netid='");
		sql.append(vo1.getNetid());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo1.getSendphone());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("'where id=");
		sql.append(vo1.getId());
		try {
			conn = new DBManager();
			saveOrUpdate(sql.toString());

			if (!vo1.getIpaddress().equals(pvo.getIpaddress())) {
				String ipstr = pvo.getIpaddress();
				String allipstr = SysUtil.doip(ipstr);
				conn = new DBManager();
				CreateTableManager ctable = new CreateTableManager();
				ctable.deleteTable("dominoping", allipstr, "dominoping");// Ping
				ctable.deleteTable("dompinghour", allipstr, "dompinghour");// Ping
				ctable.deleteTable("dompingday", allipstr, "dompingday");// Ping

				ctable.deleteTable("dominocpu", allipstr, "cpu");// cpu
				ctable.deleteTable("domcpuhour", allipstr, "domcpuhour");// cpu
				ctable.deleteTable("domcpuday", allipstr, "domcpuday");// cpu

				ctable.deleteTable("domstatus", allipstr, "domstatus");// 状态
				// IMAP/LDAP/POP3/SMTP
				ctable.deleteTable("domstshour", allipstr, "domstshour");// status
				ctable.deleteTable("domstatusday", allipstr, "domstatusday");// status

				ctable.deleteTable("domservmem", allipstr, "domservmem");// 服务器内存利用率
				ctable.deleteTable("domsemehour", allipstr, "domsemehour");// domservmem
				ctable.deleteTable("domsemeday", allipstr, "domsemeday");// domservmem

				ctable.deleteTable("domplatmem", allipstr, "domplatmem");// 平台内存利用率
				ctable.deleteTable("dopltmehour", allipstr, "dopltmehour");// domplatmem
				ctable.deleteTable("dopltmeday", allipstr, "dopltmeday");// domplatmem

				ctable.deleteTable("domdisk", allipstr, "domdisk");// 磁盘利用率
				ctable.deleteTable("domdiskhour", allipstr, "domdiskhour");// disk
				ctable.deleteTable("domdskmday", allipstr, "domdskmday");// disk
				String ip = vo1.getIpaddress();
				allipstr = SysUtil.doip(ip);

				ctable = new CreateTableManager();
				ctable.createTable("dominoping", allipstr, "dominoping");// Ping
				ctable.createTable("dompinghour", allipstr, "dompinghour");// Ping
				ctable.createTable("dompingday", allipstr, "dompingday");// Ping

				ctable.createTable("dominocpu", allipstr, "cpu");// cpu
				ctable.createTable("domcpuhour", allipstr, "domcpuhour");// cpu
				ctable.createTable("domcpuday", allipstr, "domcpuday");// cpu

				ctable.createTable("domstatus", allipstr, "domstatus");// 状态
				// IMAP/LDAP/POP3/SMTP
				ctable.createTable("domstshour", allipstr, "domstshour");// status
				ctable.createTable("domstatusday", allipstr, "domstatusday");// status

				ctable.createTable("domservmem", allipstr, "domservmem");// 服务器内存利用率
				ctable.createTable("domsemehour", allipstr, "domsemehour");// domservmem
				ctable.createTable("domsemeday", allipstr, "domsemeday");// domservmem

				ctable.createTable("domplatmem", allipstr, "domplatmem");// 平台内存利用率
				ctable.createTable("dopltmehour", allipstr, "dopltmehour");// domplatmem
				ctable.createTable("dopltmeday", allipstr, "dopltmeday");// domplatmem

				ctable.createTable("domdisk", allipstr, "domdisk");// 磁盘利用率
				ctable.createTable("domdiskhour", allipstr, "domdiskhour");// disk
				ctable.createTable("domdskmday", allipstr, "domdskmday");// disk
			}

		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			try {
				conn.executeBatch();
			} catch (Exception e) {

			}
			conn.close();
		}
		return flag;
	}

	public List getDominoByBID(Vector bids) {
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
		sql.append("select * from nms_dominoconfig " + wstr);
		return findByCriteria(sql.toString());
	}

	public List getDominoByFlag(int flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_dominoconfig where mon_flag = " + flag);
		return findByCriteria(sql.toString());
	}

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
				tablename = "dominoping" + allipstr;
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
}
