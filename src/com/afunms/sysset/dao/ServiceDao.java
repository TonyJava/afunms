
package com.afunms.sysset.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.sysset.model.Service;


@SuppressWarnings("unchecked")
public class ServiceDao extends BaseDao implements DaoInterface {
	public ServiceDao() {
		super("nms_service");
	}

	public boolean isServiceExist(String port) {
		String sql = "select * from nms_service where port=" + port;
		boolean result = false;
		try {
			rs = conn.executeQuery(sql);
			if (rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Service vo = new Service();
		try {
			vo.setId(rs.getInt("id"));
			vo.setService(rs.getString("service"));
			vo.setPort(rs.getInt("port"));
			vo.setScan(rs.getInt("scan"));
			vo.setTimeOut(rs.getInt("time_out"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	
	public List loadService(int type) {
		List list = new ArrayList(10);
		try {
			if (type == 0) {
				rs = conn.executeQuery("select * from nms_service where scan=1 order by id");
			} else {
				rs = conn.executeQuery("select * from nms_service order by id");
			}
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean save(BaseVo baseVo) {
		// 应该保证端口不能重复
		Service vo = (Service) baseVo;
		StringBuffer sb = new StringBuffer(200);
		sb.append("insert into nms_service(id,service,port,scan,time_out)values(");
		sb.append(getNextID());
		sb.append(",'");
		sb.append(vo.getService());
		sb.append("',");
		sb.append(vo.getPort());
		sb.append(",");
		sb.append(vo.isScan());
		sb.append(",");
		sb.append(vo.getTimeOut());
		sb.append(")");

		return saveOrUpdate(sb.toString());
	}

	public boolean update(BaseVo baseVo) {
		Service vo = (Service) baseVo;
		StringBuffer sb = new StringBuffer(200);
		sb.append("update nms_service set service='");
		sb.append(vo.getService());
		sb.append("',port=");
		sb.append(vo.getPort());
		sb.append(",scan=");
		sb.append(vo.isScan());
		sb.append(",time_out=");
		sb.append(vo.getTimeOut());
		sb.append(" where id=");
		sb.append(vo.getId());
		return saveOrUpdate(sb.toString());
	}

	/**
	 * 批量更新扫描标志
	 */
	public boolean updateScan(String[] id) {
		boolean result = false;
		try {
			// 先把全部设为0
			conn.executeUpdate("update nms_service set scan=0");
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("update nms_service set scan=1 where id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
