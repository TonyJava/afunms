package com.afunms.webservice.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.webservice.model.CiType;

@SuppressWarnings("unchecked")
public class CiTypeDao extends BaseDao implements DaoInterface {
	public CiTypeDao() {
		super("nms_webservice_ciType");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		CiType vo = new CiType();
		try {
			vo.setId(rs.getInt("id"));
			vo.setCiType(rs.getString("ciType"));
			vo.setFatherType(rs.getString("fatherId"));
			vo.setAttributeName(rs.getString("attributeName"));
			vo.setDescription(rs.getString("description"));

		} catch (Exception e) {
			SysLogger.error("diskioTempDao.loadFromRS()", e);
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		CiType type = (CiType) vo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_webservice_ciType(id,ciType,fatherType,attributeName,description)values('");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(type.getCiType());
		sql.append("','");
		sql.append(type.getFatherType());
		sql.append("','");
		sql.append(type.getAttributeName());
		sql.append("','");
		sql.append(type.getDescription());
		sql.append("')");

		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public CiType[] getAllCiType() {
		List<CiType> list = loadAll();
		CiType[] types = null;
		if (list != null && list.size() > 0) {
			types = new CiType[list.size()];
			for (int i = 0; i < list.size(); i++) {
				types[i] = list.get(i);
			}
		}
		return types;
	}

}
