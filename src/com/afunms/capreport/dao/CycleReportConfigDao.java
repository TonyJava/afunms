package com.afunms.capreport.dao;

import java.sql.ResultSet;

import com.afunms.capreport.model.CycleReportConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;

public class CycleReportConfigDao extends BaseDao implements DaoInterface {
	public CycleReportConfigDao() {
		super("nms_cycle_report_config");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CycleReportConfig vo = new CycleReportConfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBids(rs.getString("bid"));
			vo.setCollectionOfdeviceId(rs.getString("collection_of_device_id"));
			vo.setCollectionOfGenerationTime(rs.getString("collection_of_generation_time"));
			vo.setCollectionOfRecieverId(rs.getString("reciever_id"));
		} catch (Exception e) {
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		int id = this.getNextID();
		CycleReportConfig config = (CycleReportConfig) vo;
		StringBuffer sql = new StringBuffer("insert into nms_cycle_report_config(id,reciever_id,bid,collection_of_device_id,collection_of_generation_time) values(");
		sql.append(id);
		sql.append(",'");
		sql.append(config.getCollectionOfRecieverId());
		sql.append("','");
		sql.append(config.getBids());
		sql.append("','");
		sql.append(config.getCollectionOfdeviceId());
		sql.append("','");
		sql.append(config.getCollectionOfGenerationTime());
		sql.append("')");
		config.setId(id);
		SysLogger.info(sql.toString());
		return this.saveOrUpdate(sql.toString());
	}

	public void commit() {
		this.conn.commit();
	}

	public boolean update(BaseVo vo) {
		return false;
	}

}
