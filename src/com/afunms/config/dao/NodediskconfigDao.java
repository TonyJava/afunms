package com.afunms.config.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.Nodediskconfig;

@SuppressWarnings("unchecked")
public class NodediskconfigDao extends BaseDao implements DaoInterface {

	public NodediskconfigDao() {
		super("nms_nodediskconfig");
	}

	public boolean delete(String id) {
		boolean result = false;
		try {
			conn.addBatch("delete from nms_nodediskconfig where id=" + id);
			conn.executeBatch();
			result = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Nodediskconfig nodediskconfig = new Nodediskconfig();
		try {
			if (rs != null) {
				nodediskconfig.setId(rs.getInt("id"));
				nodediskconfig.setNodeid(rs.getInt("nodeid"));
				nodediskconfig.setBytesPerSector(rs.getString("bytesPerSector"));
				nodediskconfig.setCaption(rs.getString("caption"));
				nodediskconfig.setInterfaceType(rs.getString("interfaceType"));
				nodediskconfig.setSize(rs.getString("sizes"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nodediskconfig;
	}

	public boolean save(BaseVo vo) {
		Nodediskconfig nodediskconfig = (Nodediskconfig) vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_nodediskconfig(nodeid,bytesPerSector,caption,interfaceType,sizes)values(");
		sql.append("'");
		sql.append(nodediskconfig.getNodeid());
		sql.append("','");
		sql.append(nodediskconfig.getBytesPerSector());
		sql.append("','");
		sql.append(nodediskconfig.getCaption());
		sql.append("','");
		sql.append(nodediskconfig.getInterfaceType());
		sql.append("','");
		sql.append(nodediskconfig.getSize());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		Nodediskconfig nodediskconfig = (Nodediskconfig) vo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("update nms_nodediskconfig set nodeid='");
		sql.append(nodediskconfig.getNodeid());
		sql.append("',bytesPerSector='");
		sql.append(nodediskconfig.getBytesPerSector());
		sql.append("',caption='");
		sql.append(nodediskconfig.getCaption());
		sql.append("',interfaceType='");
		sql.append(nodediskconfig.getInterfaceType());
		sql.append("',sizes='");
		sql.append(nodediskconfig.getSize());
		sql.append("'where id=");
		sql.append(nodediskconfig.getId());
		return saveOrUpdate(sql.toString());
	}
}
