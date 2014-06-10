package com.afunms.ipaccounting.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.ipaccounting.model.IpAccountingBase;

@SuppressWarnings("unchecked")
public class IpAccountingBaseDao extends BaseDao {

	public IpAccountingBaseDao() {
		super("nms_ipaccountips");
	}

	public boolean save(BaseVo baseVo) {
		IpAccountingBase vo = (IpAccountingBase) baseVo;
		StringBuffer sql = new StringBuffer(100);
		vo.setId(getNextID());
		sql.append("insert into nms_ipaccountips(id,nodeid,srcip,destip,protocol)values(");
		sql.append(vo.getId());
		sql.append(",");
		sql.append(vo.getNodeid());
		sql.append(",'");
		sql.append(vo.getSrcip());
		sql.append("','");
		sql.append(vo.getDestip());
		sql.append("','");
		sql.append(vo.getProtocol());
		sql.append("')");
		try {
			conn.executeUpdate(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean save(List list) {
		boolean result = false;
		try {
			for (int i = 0; i < list.size(); i++) {
				IpAccountingBase vo = (IpAccountingBase) list.get(i);
				StringBuffer sql = new StringBuffer(100);
				sql.append("insert into nms_ipaccountips(id,nodeid,srcip,destip,protocol)values(");
				sql.append(getNextID());
				sql.append(",");
				sql.append(vo.getNodeid());
				sql.append(",'");
				sql.append(vo.getSrcip());
				sql.append("','");
				sql.append(vo.getDestip());
				sql.append("','");
				sql.append(vo.getProtocol());
				sql.append("')");
				conn.addBatch(sql.toString());
			}
			conn.executeBatch();
			return true;
		} catch (Exception ex) {
			conn.rollback();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public boolean delete(String[] nodeid) {
		boolean result = false;
		try {
			for (int i = 0; i < nodeid.length; i++) {
				conn.addBatch("delete from nms_ipaccountips where nodeid=" + nodeid[i]);

			}
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		IpAccountingBase vo = new IpAccountingBase();
		try {
			vo.setId(rs.getInt("id"));
			vo.setNodeid(rs.getInt("nodeid"));
			vo.setSrcip(rs.getString("srcip"));
			vo.setDestip(rs.getString("destip"));
			vo.setProtocol(rs.getString("protocol"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

}
