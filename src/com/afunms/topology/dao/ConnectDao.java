package com.afunms.topology.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.topology.model.Connect;

@SuppressWarnings("unchecked")
public class ConnectDao extends BaseDao implements DaoInterface {

	public ConnectDao() {
		super("nms_connect");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Connect vo = new Connect();
		try {
			vo.setId(rs.getLong("id"));
			vo.setNodeid(rs.getLong("nodeid"));
			vo.setUsername(rs.getString("username"));
			vo.setPwd(rs.getString("pwd"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setIpaddress("ipaddress");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		Connect vmware = (Connect) vo;
		StringBuffer addsql = new StringBuffer(200);
		addsql.append("insert into nms_connect(nodeid,username,pwd,type,subtype,ipaddress)values(");
		addsql.append(vmware.getNodeid());
		addsql.append(",'");
		addsql.append(vmware.getUsername());
		addsql.append("','");
		addsql.append(vmware.getPwd());
		addsql.append("','");
		addsql.append(vmware.getType());
		addsql.append("','");
		addsql.append(vmware.getSubtype());
		addsql.append("','");
		addsql.append(vmware.getIpaddress());
		addsql.append("')");
		return saveOrUpdate(addsql.toString());
	}

	public boolean update(BaseVo vo) {
		Connect vo_c = (Connect) vo;
		StringBuffer updatesql = new StringBuffer(200);
		updatesql.append("update nms_connect set nodeid=");
		updatesql.append(vo_c.getNodeid());
		updatesql.append(",username='");
		updatesql.append(vo_c.getUsername());
		updatesql.append("',pwd='");
		updatesql.append(vo_c.getPwd());
		updatesql.append("',type='");
		updatesql.append(vo_c.getType());
		updatesql.append("',subtype='");
		updatesql.append(vo_c.getSubtype());
		updatesql.append("' where id=");
		updatesql.append(vo_c.getId());
		return saveOrUpdate(updatesql.toString());
	}

	public boolean delete(BaseVo vo) {
		Connect vo_c = (Connect) vo;
		boolean b = false;
		String delsql = "delete from nms_connect where id=" + vo_c.getId();
		try {
			conn.executeUpdate(delsql);
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	public boolean delete(Long node) {
		boolean b = false;
		String delsql = "delete from nms_connect where nodeid=" + node;
		try {
			conn.executeUpdate(delsql);
			b = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	public List queryall() {
		List listall = new ArrayList();
		String queryallsql = "select * from nms_connect";
		try {
			rs = conn.executeQuery(queryallsql);
			while (rs.next()) {
				listall.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listall;
	}

	public List getbynodeid(Long node) {
		List list = new ArrayList();
		String queryonesql = "select * from nms_connect where nodeid=" + node;
		try {
			rs = conn.executeQuery(queryonesql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}