package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.TrapOIDConfig;

@SuppressWarnings("unchecked")
public class TrapOIDConfigDao extends BaseDao implements DaoInterface {
	public TrapOIDConfigDao() {
		super("nms_trapoid");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_trapoid where id=" + id[i]);
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

	@Override
	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_trapoid where id=" + id);
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
			rs = conn.executeQuery("select * from nms_trapoid order by id");
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

	public List loadByEnterpriseOID(String enterpriseoid) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_trapoid where enterpriseoid='" + enterpriseoid + "' order by orders");
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
		TrapOIDConfig vo = new TrapOIDConfig();
		try {
			vo.setId(rs.getString("id"));
			vo.setEnterpriseoid(rs.getString("enterpriseoid"));
			vo.setOrders(rs.getInt("orders"));
			vo.setOid(rs.getString("oid"));
			vo.setDesc(rs.getString("descr"));
			vo.setValue1(rs.getString("value1"));
			vo.setValue2(rs.getString("value2"));
			vo.setTransvalue1(rs.getString("transvalue1"));
			vo.setTransvalue2(rs.getString("transvalue2"));
			vo.setTransflag(rs.getInt("transflag"));
			vo.setCompareflag(rs.getInt("compareflag"));
			vo.setTraptype(rs.getString("traptype"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_trapoid(name,descr)values(");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		boolean result = false;
		StringBuffer sql = new StringBuffer();
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
