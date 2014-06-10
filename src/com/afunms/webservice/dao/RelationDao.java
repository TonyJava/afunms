package com.afunms.webservice.dao;

import java.sql.ResultSet;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.webservice.model.MoAndCiRelation;

@SuppressWarnings("unchecked")
public class RelationDao extends BaseDao implements DaoInterface {
	public RelationDao() {
		super("nms_webservice_mo_ci");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		MoAndCiRelation vo = new MoAndCiRelation();
		try {
			vo.setId(rs.getInt("id"));
			vo.setMoId(rs.getInt("moId"));
			vo.setCiType(rs.getString("ciType"));

		} catch (Exception e) {
			SysLogger.error("diskioTempDao.loadFromRS()", e);
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		MoAndCiRelation relation = (MoAndCiRelation) vo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_webservice_mo_ci(id,moId,ciType)values(");
		sql.append(getNextID());
		sql.append(",");
		sql.append(relation.getMoId());
		sql.append(",'");
		sql.append(relation.getCiType());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public void addBatch(BaseVo vo) {
		MoAndCiRelation relation = (MoAndCiRelation) vo;
		StringBuffer sql = new StringBuffer(500);
		sql.append("insert into nms_webservice_mo_ci(moId,ciType)values(");
		sql.append(relation.getMoId());
		sql.append(",'");
		sql.append(relation.getCiType());
		sql.append("')");
		conn.addBatch(sql.toString());
	}

	public void delteAllData() {
		String sql = "delete from nms_webservice_mo_ci";
		conn.addBatch(sql);
	}

	public void executeBatch() {
		conn.executeBatch();
		conn.close();
	}

	public String[] getMoIdsByCiType(String ciType) {
		StringBuffer sql = new StringBuffer();
		if (ciType.startsWith("01") && ciType.length() > 4) {
			ciType = ciType.substring(0, 3);
		} else if (ciType.startsWith("02") && ciType.length() > 6) {
			ciType = ciType.substring(0, 5);
		}
		String[] moIds = null;
		sql.append(" where ciType like ");
		sql.append("'");
		sql.append(ciType);
		sql.append("%'");

		List<MoAndCiRelation> list = findByCondition(sql.toString());
		if (list != null && list.size() > 0) {
			moIds = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				MoAndCiRelation model = list.get(i);
				moIds[i] = model.getMoId() + "";
			}
		}
		return moIds;
	}
}
