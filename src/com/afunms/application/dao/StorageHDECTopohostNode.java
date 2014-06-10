package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.Storage;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

/**
 * 
 * 此类为存储的Dao类 , 对表 nms_storage 进行操作
 * 
 */

@SuppressWarnings("unchecked")
public class StorageHDECTopohostNode extends BaseDao implements DaoInterface {

	public StorageHDECTopohostNode() {
		super("topo_host_node");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Storage storage = new Storage();
		try {
			storage.setId(rs.getInt("id"));
			storage.setIpaddress(rs.getString("ipaddress"));
			storage.setName(rs.getString("name"));
			storage.setUsername(rs.getString("username"));
			storage.setSnmpversion(rs.getString("snmpversion"));
			storage.setMon_flag(rs.getString("mon_flag"));
			storage.setStatus(rs.getString("status"));
			storage.setCollecttype(rs.getString("collecttype"));
			storage.setCommunity(rs.getString("community"));
			storage.setType(rs.getString("type"));
			storage.setSerialNumber(rs.getString("serial_number"));
			storage.setBid(rs.getString("bid"));
			storage.setCollectTime(rs.getString("collectTime"));
			storage.setSupperid(rs.getString("supperid"));
			storage.setSendemail(rs.getString("sendemail"));
			storage.setSendmobiles(rs.getString("sendmobiles"));
			storage.setSendphone(rs.getString("sendphone"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return storage;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo vo) {
		Storage storage = (Storage) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_storagehd set ipaddress='");
		sql.append(storage.getId());
		sql.append("',ipaddress='");
		sql.append(storage.getIpaddress());
		sql.append("',name='");
		sql.append(storage.getName());
		sql.append("',username='");
		sql.append(storage.getUsername());
		sql.append("',snmpversion='");
		sql.append(storage.getSnmpversion());
		sql.append("',status='");
		sql.append(storage.getStatus());
		sql.append("',mon_flag='");
		sql.append(storage.getMon_flag());
		sql.append("',collecttype='");
		sql.append(storage.getCollecttype());
		sql.append("',collecttype='");
		sql.append(storage.getCommunity());
		sql.append("',type='");
		sql.append(storage.getType());
		sql.append("',serial_number='");
		sql.append(storage.getSerialNumber());
		sql.append("',bid='");
		sql.append(storage.getBid());
		sql.append("',collecttime='");
		sql.append(storage.getCollectTime());
		sql.append("',supperid='");
		sql.append(storage.getSupperid());
		sql.append("',sendemail='");
		sql.append(storage.getSendemail());
		sql.append("',sendmobiles='");
		sql.append(storage.getSendmobiles());
		sql.append("',sendphone='");
		sql.append(storage.getSendphone());
		sql.append("' where id=");
		sql.append(storage.getId());
		return saveOrUpdate(sql.toString());
	}

	public List findByMon_flagtopo(String managed) {
		String sql = "select * from topo_host_node where managed='" + managed + "'";
		return findByCriteria(sql);
	}

	public boolean updateMon_flagtopo(String managed, String id) {
		String sql = "update topo_host_node set managed='" + managed + "' where id ='" + id + "'";
		return saveOrUpdate(sql.toString());
	}

	/**
	 * 删除一批记录
	 */
	public String topodelete(String id) {
		String sql = "delete  from  topo_host_node where id='" + id + "'";
		return sql;
	}

	/**
	 * 删除一批记录
	 */
	public String nmsdelete(String nodeid) {
		String sql = "delete  from  nms_gather_indicators_node where nodeid='" + nodeid + "'";
		return sql;
	}

	/**
	 * 删除一批记录
	 */
	public String nmsalarmindicatorsnodedelete(String nodeid) {
		String sql = "delete  from  nms_alarm_indicators_node where nodeid='" + nodeid + "'";
		return sql;
	}

}