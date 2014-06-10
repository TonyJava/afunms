package com.afunms.topology.dao;

import java.sql.ResultSet;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.topology.model.CustomXml;

public class CustomXmlDao extends BaseDao implements DaoInterface {
	public CustomXmlDao() {
		super("topo_custom_xml");
	}

	public String delete(String id) {
		String xmlName = null;
		try {
			rs = conn.executeQuery("select * from topo_custom_xml where id=" + id);
			if (rs.next())
				xmlName = rs.getString("xml_name");
			conn.executeUpdate("delete from topo_custom_xml where id=" + id);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			conn.close();
		}
		return xmlName;
	}

	public boolean save(BaseVo baseVo) {
		CustomXml vo = (CustomXml) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into topo_custom_xml(id,xml_name,view_name)values(");
		sql.append(getNextID());
		sql.append(",'");
		sql.append(vo.getXmlName());
		sql.append("','");
		sql.append(vo.getViewName());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		CustomXml vo = (CustomXml) baseVo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update topo_custom_xml set view_name='");
		sql.append(vo.getViewName());
		sql.append("' where id=");
		sql.append(vo.getId());
		return saveOrUpdate(sql.toString());
	}

	public BaseVo loadFromRS(ResultSet rs) {
		CustomXml vo = new CustomXml();
		try {
			vo.setId(rs.getInt("id"));
			vo.setXmlName(rs.getString("xml_name"));
			vo.setViewName(rs.getString("view_name"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
}