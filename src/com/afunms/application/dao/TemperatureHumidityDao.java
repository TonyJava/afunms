/*
 *	此类为 TemperatureHumidityConfig 类的dao类 对表 system_temperature_humidity 进行操作
 */

package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.TemperatureHumidityConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

@SuppressWarnings("unchecked")
public class TemperatureHumidityDao extends BaseDao implements DaoInterface {

	public TemperatureHumidityDao() {
		super("system_temperature_humidity");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		TemperatureHumidityConfig temperatureHumidityConfig = new TemperatureHumidityConfig();
		try {
			temperatureHumidityConfig.setId(rs.getInt("id"));
			temperatureHumidityConfig.setNode_id(rs.getString("node_id"));
			temperatureHumidityConfig.setTemperature(rs.getString("temperature"));
			temperatureHumidityConfig.setHumidity(rs.getString("humidity"));
			temperatureHumidityConfig.setTime(rs.getString("time"));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		return temperatureHumidityConfig;
	}

	public boolean save(BaseVo vo) {
		TemperatureHumidityConfig temperatureHumidityConfig = (TemperatureHumidityConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into system_temperature_humidity(id,node_id,temperature,humidity,time)values(");
		sql.append(temperatureHumidityConfig.getId());
		sql.append(",'");
		sql.append(temperatureHumidityConfig.getNode_id());
		sql.append("','");
		sql.append(temperatureHumidityConfig.getTemperature());
		sql.append("','");
		sql.append(temperatureHumidityConfig.getHumidity());
		sql.append("','");
		sql.append(temperatureHumidityConfig.getTime());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public List<TemperatureHumidityConfig> findByNodeId(String nodeId) {
		String sql = "select * from system_temperature_humidity where node_id = '" + nodeId + "' order by time desc";
		return findByCriteria(sql);
	}

	/**
	 * Get the list of temperatureHumidityConfig by node_id , start time , end
	 * time.
	 * 
	 * @param nodeId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<TemperatureHumidityConfig> findByNodeIdAndTime(String nodeId, String startTime, String endTime) {
		String sql = "select * from system_temperature_humidity where node_id = '" + nodeId + "' and time between '" + startTime + "' and '" + endTime + "' order by time desc";
		return findByCriteria(sql);
	}

	public boolean deleteByNodeId(String nodeId) {
		String sql = "delete from system_temperature_humidity where node_id='" + nodeId + "'";
		try {
			conn.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
