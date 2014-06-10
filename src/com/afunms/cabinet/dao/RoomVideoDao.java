package com.afunms.cabinet.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.cabinet.model.RoomVideo;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

/**
 * 机房安全视频DAO
 * 
 * @author wxy
 * @version Dec 22, 2011 10:29:53 AM
 */
public class RoomVideoDao extends BaseDao implements DaoInterface {

	public RoomVideoDao() {
		super("nms_cabinet_video");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		RoomVideo vo = new RoomVideo();
		try {
			vo.setId(rs.getInt("id"));
			vo.setUserid(rs.getInt("userid"));
			vo.setCabinetid(rs.getInt("cabinetid"));
			vo.setFilename(rs.getString("filename"));
			vo.setName(rs.getString("name"));
			vo.setDotime(rs.getString("dotime"));
			vo.setDescription(rs.getString("description"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		RoomVideo law = (RoomVideo) vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_cabinet_video(id,userid,cabinetid,name,filename,dotime,description)values(");
		sql.append(getNextID());
		sql.append(",");
		sql.append(law.getUserid());
		sql.append(",'");
		sql.append(law.getCabinetid());
		sql.append("','");
		sql.append(law.getName());
		sql.append("','");
		sql.append(law.getFilename());
		sql.append("','");
		sql.append(law.getDotime());
		sql.append("','");
		sql.append(law.getDescription());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		RoomVideo law = (RoomVideo) vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_cabinet_video set userid=");
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

	public boolean deleteById(String id) {
		boolean result = false;

		try {
			conn.addBatch("delete from nms_cabinet_video where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return result;
	}

	public List<String> findByIds(String where) {
		List<String> list = new ArrayList<String>();
		try {
			rs = conn.executeQuery("select filename from nms_cabinet_video order by id");
			if (rs == null)
				return null;
			while (rs.next())
				list.add(rs.getString("filename"));
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
			e.printStackTrace();
		}

		return list;
	}
}
