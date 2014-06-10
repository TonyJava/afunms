package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.CfgBaseInfo;

public class CfgBaseInfoDao extends BaseDao implements DaoInterface {
	public CfgBaseInfoDao(String allipstr) {
		super("baseInfo" + allipstr);
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CfgBaseInfo vo = new CfgBaseInfo();
		try {
			vo.setId(rs.getInt("id"));
			vo.setPolicyName(rs.getString("policyName"));
			vo.setName(rs.getString("name"));
			vo.setValue(rs.getString("value"));
			vo.setType(rs.getString("type"));
			vo.setCollecttime(rs.getString("collecttime"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		StringBuffer sql = new StringBuffer();
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		// TODO Auto-generated method stub
		return false;
	}

}
