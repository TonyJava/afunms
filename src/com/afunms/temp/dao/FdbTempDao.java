package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SystemConstant;
import com.afunms.temp.model.FdbNodeTemp;

public class FdbTempDao extends BaseDao implements DaoInterface {

	public FdbTempDao() {
		super("nms_fdb_data_temp");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from " + "nms_fdb_data_temp" + " where nodeid=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		}
		return result;
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_fdb_data_temp where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	/**
	 * É¾³ýÒ»Ìõ¼ÇÂ¼
	 */
	public boolean deleteByNodeId(String nodeid) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_fdb_data_temp where nodeid='" + nodeid + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<FdbNodeTemp> getFdbNodeTempList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		FdbNodeTemp vo = new FdbNodeTemp();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setIp(rs.getString("ip"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setIfindex(rs.getString("ifindex"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setMac(rs.getString("mac"));
			vo.setIfband(rs.getString("ifband"));
			vo.setIfsms(rs.getString("ifsms"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public void refresh() {
		String sql1 = "";
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql1 = "update nms_fdb_table set collecttime=now() where (nodeid,ipaddress,mac,bak) not in (select nodeid,ipaddress,mac,bak from (select A.nodeid,A.ip,A.ipaddress,A.mac,A.bak from nms_fdb_data_temp A ,nms_fdb_table B where A.ipaddress=B.ipaddress and A.bak=B.bak and A.mac=B.mac) as t)";
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql1 = "update nms_fdb_table set collecttime=sysdate where (nodeid,ipaddress,mac,bak) not in (select nodeid,ipaddress,mac,bak from (select A.nodeid,A.ip,A.ipaddress,A.mac,A.bak from nms_fdb_data_temp A ,nms_fdb_table B where A.ipaddress=B.ipaddress and A.bak=B.bak and A.mac=B.mac))";
		}
		this.conn.executeUpdate(sql1);
		String sql2 = "";
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql2 = "insert into nms_fdb_table(nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak) select nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak from nms_fdb_data_temp  where (nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak) not in (select nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak from (select A.nodeid,A.ip,A.type,A.subtype,A.ifindex,A.ipaddress,A.mac,A.ifband,A.ifsms,A.collecttime,A.bak from nms_fdb_data_temp A ,nms_fdb_table B where A.ipaddress=B.ipaddress and A.bak=B.bak and A.mac=B.mac) as t)";
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql2 = "insert into nms_fdb_table(nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak) select nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak from nms_fdb_data_temp  where (nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak) not in (select nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak from (select A.nodeid,A.ip,A.type,A.subtype,A.ifindex,A.ipaddress,A.mac,A.ifband,A.ifsms,A.collecttime,A.bak from nms_fdb_data_temp A ,nms_fdb_table B where A.ipaddress=B.ipaddress and A.bak=B.bak and A.mac=B.mac) )";
		}
		this.conn.executeUpdate(sql2);
	}

	public boolean save(BaseVo baseVo) {
		FdbNodeTemp vo = (FdbNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_fdb_data_temp(nodeid,ip,type,subtype,ifindex,ipaddress,mac,ifband,ifsms,collecttime,bak)values('");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getIp());
		sql.append("','");
		sql.append(vo.getType());
		sql.append("','");
		sql.append(vo.getSubtype());
		sql.append("','");
		sql.append(vo.getIfindex());
		sql.append("','");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getMac());
		sql.append("','");
		sql.append(vo.getIfband());
		sql.append("','");
		sql.append(vo.getIfsms());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("','')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}
}
