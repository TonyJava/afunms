package com.afunms.temp.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.temp.model.RouterNodeTemp;

public class RouteTempDao extends BaseDao implements DaoInterface {

	public RouteTempDao() {
		super("nms_route_data_temp");
	}

	/**
	 * É¾³ýÒ»Ìõ¼ÇÂ¼
	 */
	public boolean deleteByNodeId(String nodeid) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_route_data_temp where nodeid='" + nodeid + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean deleteByIp(String ip) {
		boolean result = false;
		try {
			conn.executeUpdate("delete from nms_route_data_temp where ip='" + ip + "'");
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		RouterNodeTemp vo = new RouterNodeTemp();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setIp(rs.getString("ip"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setIfindex(rs.getString("ifindex"));
			vo.setNexthop(rs.getString("nexthop"));
			vo.setProto(rs.getString("proto"));
			vo.setRtype(rs.getString("rtype"));
			vo.setMask(rs.getString("mask"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setPhysaddress(rs.getString("physaddress"));
			vo.setDest(rs.getString("dest"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		RouterNodeTemp vo = (RouterNodeTemp) baseVo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_route_data_temp(nodeid,ip,type,subtype,ifindex,nexthop,proto,rtype,mask,collecttime,physaddress,dest)values('");
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
		sql.append(vo.getNexthop());
		sql.append("','");
		sql.append(vo.getProto());
		sql.append("','");
		sql.append(vo.getRtype());
		sql.append("','");
		sql.append(vo.getMask());
		sql.append("','");
		sql.append(vo.getCollecttime());
		sql.append("','");
		sql.append(vo.getPhysaddress());
		sql.append("','");
		sql.append(vo.getDest());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<RouterNodeTemp> getNodeTempList(String nodeid, String type, String subtype) {
		StringBuffer sql = new StringBuffer();
		sql.append(" where nodeid='" + nodeid + "' and type='" + type + "' and subtype='" + subtype + "'");
		return findByCondition(sql.toString());
	}

}
