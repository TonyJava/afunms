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
import com.afunms.config.model.NetNodeCfgFile;

@SuppressWarnings("unchecked")
public class NetNodeCfgFileDao extends BaseDao implements DaoInterface {
	public NetNodeCfgFileDao() {
		super("nms_netnodecfgfile");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_netnodecfgfile where id=" + id[i]);
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

	public boolean deleteByHostIp(String hostip) {
		String sql = "delete from nms_netnodecfgfile where ipaddress='" + hostip + "'";
		return saveOrUpdate(sql);
	}

	@Override
	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_netnodecfgfile where id=" + id);
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

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_netnodecfgfile order by id desc");
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

	public List loadByIpaddress(String ip) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_netnodecfgfile where ipaddress='" + ip + "' order by id");
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
		NetNodeCfgFile vo = new NetNodeCfgFile();
		try {
			Calendar tempCal = Calendar.getInstance();
			Date cc = new Date();
			cc.setTime(rs.getTimestamp("recordtime").getTime());
			tempCal.setTime(cc);
			vo.setId(rs.getLong("id"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setName(rs.getString("name"));
			vo.setRecordtime(tempCal);
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public NetNodeCfgFile loadNetNodeCfgFile(int id) {
		List portconfigList = findByCriteria("select * from nms_netnodecfgfile where id=" + id);
		if (portconfigList != null && portconfigList.size() > 0) {
			NetNodeCfgFile cfg = (NetNodeCfgFile) portconfigList.get(0);
			return cfg;

		}
		return null;
	}

	public boolean save(BaseVo baseVo) {
		NetNodeCfgFile vo = (NetNodeCfgFile) baseVo;
		StringBuffer sql = new StringBuffer(100);
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = theDate.format(vo.getRecordtime().getTime());
		sql.append("insert into nms_netnodecfgfile(ipaddress,name,recordtime)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(dateString);
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		NetNodeCfgFile vo = (NetNodeCfgFile) baseVo;
		boolean result = false;
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = theDate.format(vo.getRecordtime().getTime());
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_netnodecfgfile set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',name='");
		sql.append(vo.getName());
		sql.append("',recordtime='");
		sql.append(dateString);
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
