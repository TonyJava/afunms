package com.afunms.event.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.model.AlarmInfo;

@SuppressWarnings("unchecked")
public class AlarmInfoDao extends BaseDao implements DaoInterface {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public AlarmInfoDao() {
		super("nms_alarminfo");
	}

	public List getByTime(String starttime, String totime) throws Exception {
		List alarminfoList = new ArrayList();
		Session session = null;
		try {
			Query query = session.createQuery("from AlarmInfo e where e.recordtime>= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') " + "and e.recordtime<=to_date('"
					+ totime + "','YYYY-MM-DD HH24:MI:SS') order by e.recordtime desc");
			alarminfoList = query.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alarminfoList;
	}

	public String ipchange(String ipalias) {

		List list = new ArrayList();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select ipaddress from topo_ipalias where aliasip=");
			sql.append("'" + ipalias + "'");
			rs = conn.executeQuery(sql.toString());
			while (rs.next()) {
				list.add(rs.getString("ipaddress"));
			}

		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		if (list != null && list.size() > 0) {
			String ip_address = (String) list.get(0);
			return ip_address;
		} else {
			return ipalias;
		}

	}

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_alarminfo order by id");
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
		AlarmInfo vo = new AlarmInfo();
		try {
			vo.setContent(rs.getString("content"));
			vo.setLevel1(rs.getInt("level1"));
			Calendar cal = Calendar.getInstance();
			Date newdate = new Date();
			cal.setTime(newdate);
			vo.setRecordtime(cal);
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setType(rs.getString("type"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		AlarmInfo vo = (AlarmInfo) baseVo;
		Calendar tempCal = vo.getRecordtime();
		Date cc = tempCal.getTime();
		String recordtime = sdf.format(cc);
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_alarminfo(content,ipaddress,level1,recordtime,type)values(");
		sql.append("'");
		sql.append(vo.getContent());
		sql.append("','");
		sql.append(vo.getIpaddress());
		sql.append("',");
		sql.append(vo.getLevel1());
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append(",'");
			sql.append(recordtime);
			sql.append("','");
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql.append(",");
			sql.append("to_date('" + recordtime + "','YYYY-MM-DD HH24:MI:SS')");
			sql.append(",'");
		}
		sql.append(vo.getType());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		boolean result = false;
		return result;
	}

}