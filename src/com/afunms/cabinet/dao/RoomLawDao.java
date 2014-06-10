package com.afunms.cabinet.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.cabinet.model.RoomLaw;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SystemConstant;

/**
 * 机房管理制度DAO
 * 
 * @author wxy
 * @version Dec 19, 2011 3:45:30 PM
 */
public class RoomLawDao extends BaseDao implements DaoInterface {

	public RoomLawDao() {
		super("nms_cabinet_law");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		RoomLaw vo = new RoomLaw();
		try {
			vo.setId(rs.getInt("id"));
			vo.setUserid(rs.getInt("userid"));
			vo.setCabinetid(rs.getInt("cabinetid"));
			vo.setName(rs.getString("name"));
			vo.setFilename(rs.getString("filename"));
			vo.setDotime(rs.getString("dotime"));
			vo.setDescription(rs.getString("description"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		RoomLaw law = (RoomLaw) vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_cabinet_law(id,userid,cabinetid,name,filename,dotime,description)values(");
		sql.append(getNextID());
		sql.append(",");
		sql.append(law.getUserid());
		sql.append(",'");
		sql.append(law.getCabinetid());
		sql.append("','");
		sql.append(law.getName());
		sql.append("','");
		sql.append(law.getFilename());

		if (SystemConstant.DBType.equals("mysql")) {
			sql.append("','");
			sql.append(law.getDotime());
			sql.append("','");
		} else if (SystemConstant.DBType.equals("oracle")) {
			sql.append("',to_date('");
			sql.append(law.getDotime());
			sql.append("','yyyy-mm-dd hh24:mi:ss'),'");
		}
		sql.append(law.getDescription());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		RoomLaw law = (RoomLaw) vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_cabinet_law set userid=");
		sql.append(law.getUserid());
		sql.append(",cabinetid=");
		sql.append(law.getCabinetid());
		sql.append(",name='");
		sql.append(law.getName());
		sql.append("',filename='");
		sql.append(law.getFilename());
		sql.append("',dotime='");
		sql.append(law.getDotime());
		sql.append("',description='");
		sql.append(law.getDescription());
		sql.append("' where id=");
		sql.append(law.getId());
		return saveOrUpdate(sql.toString());
	}

	public List<String> findByIds(String where) {
		List<String> list = new ArrayList<String>();
		try {
			rs = conn.executeQuery("select filename from nms_cabinet_law order by id");
			if (rs == null)
				return null;
			while (rs.next())
				list.add(rs.getString("filename"));
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}

		return list;
	}
}
