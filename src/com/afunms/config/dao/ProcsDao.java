package com.afunms.config.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.Procs;

@SuppressWarnings("unchecked")
public class ProcsDao extends BaseDao implements DaoInterface {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ProcsDao() {
		super("nms_procs");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_procs where id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteall() {
		boolean result = false;
		try {
			conn.addBatch("delete from system_nodetobusiness");
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteallbyNE(int nodeid, String eletype) {
		boolean result = false;
		try {
			conn.addBatch("delete from system_nodetobusiness where nodeid=" + nodeid + " and elementtype='" + eletype + "'");
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	@Override
	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_procs where id=" + id);
			if (rs.next()) {
				vo = loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	public BaseVo findByProcName(String procname) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_procs where procname='" + procname + "'");
			if (rs.next()) {
				vo = loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public List groupByIp() {
		List list = new ArrayList(5);
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				rs = conn.executeQuery("select id,nodeid,wbstatus,flag,ipaddress,procname,chname,bak, collecttime,supperid from nms_procs group by ipaddress order by ipaddress");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				rs = conn.executeQuery("select id,nodeid,wbstatus,flag,ipaddress,procname,chname,bak, collecttime,supperid from nms_procs order by ipaddress");
			}
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_procs order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public List loadByIp(String ip) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_procs where ipaddress='" + ip + "' order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public List loadByIpAndName(String ip, String name) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_procs where ipaddress='" + ip + "' and procname = '" + name + "' order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Procs vo = new Procs();
		try {
			vo.setId(rs.getInt("id"));
			vo.setNodeid(rs.getInt("nodeid"));
			vo.setWbstatus(rs.getInt("wbstatus"));
			vo.setFlag(rs.getInt("flag"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setProcname(rs.getString("procname"));
			vo.setChname(rs.getString("chname"));
			vo.setBak(rs.getString("bak"));
			Calendar cal = Calendar.getInstance();
			Date newdate = new Date();
			newdate.setTime(rs.getTimestamp("collecttime").getTime());
			cal.setTime(newdate);
			vo.setCollecttime(cal);
			vo.setSupperid(rs.getInt("supperid"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		Procs vo = (Procs) baseVo;
		Calendar tempCal = vo.getCollecttime();
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_procs(id,nodeid,wbstatus,flag,ipaddress,procname,chname,bak,collecttime,supperid)values(");
		sql.append(vo.getId());
		sql.append(",");
		sql.append(vo.getNodeid());
		sql.append(",");
		sql.append(vo.getWbstatus());
		sql.append(",");
		sql.append(vo.getFlag());
		sql.append(",'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getProcname());
		sql.append("','");
		sql.append(vo.getChname());
		sql.append("','");
		sql.append(vo.getBak());
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("','");
			sql.append(recordtime);
			sql.append("','");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',");
			sql.append("to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",'");
		}

		sql.append(vo.getSupperid());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		Procs vo = (Procs) baseVo;
		Calendar tempCal = vo.getCollecttime();
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_procs set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',procname='");
		sql.append(vo.getProcname());
		sql.append("',chname='");
		sql.append(vo.getChname());
		sql.append("',nodeid=");
		sql.append(vo.getNodeid());
		sql.append(",wbstatus=");
		sql.append(vo.getWbstatus());
		sql.append(",flag=");
		sql.append(1);
		sql.append(",bak='");
		sql.append(vo.getBak());
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',collecttime='");
			sql.append(recordtime);
			sql.append("',supperid='");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append("',collecttime=");
			sql.append("to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",supperid='");
		}
		sql.append(vo.getSupperid());
		sql.append("' where id=");
		sql.append(vo.getId());
		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}
}
