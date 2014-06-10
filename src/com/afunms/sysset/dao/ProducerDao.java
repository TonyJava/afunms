package com.afunms.sysset.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.sysset.model.Producer;

public class ProducerDao extends BaseDao implements DaoInterface {
	public ProducerDao() {
		super("nms_producer");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_producer where id=" + id[i]);
				conn.addBatch("delete from nms_device_type where producer=" + id[i]);
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

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Producer vo = new Producer();
		try {
			vo.setId(rs.getInt("id"));
			vo.setProducer(rs.getString("producer"));
			vo.setEnterpriseOid(rs.getString("enterprise_oid"));
			vo.setWebsite(rs.getString("website"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		Producer vo = (Producer) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_producer(id,producer,enterprise_oid,website)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getProducer());
		sql.append("','");
		sql.append(vo.getEnterpriseOid());
		sql.append("','");
		sql.append(vo.getWebsite());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		Producer vo = (Producer) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_producer set producer='");
		sql.append(vo.getProducer());
		sql.append("',enterprise_oid='");
		sql.append(vo.getEnterpriseOid());
		sql.append("',website='");
		sql.append(vo.getWebsite());
		sql.append("' where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}
}
