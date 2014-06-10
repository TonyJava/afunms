package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.afunms.application.model.ProcessGroup;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

@SuppressWarnings("unchecked")
public class ProcessGroupDao extends BaseDao implements DaoInterface {

	public ProcessGroupDao() {
		super("nms_process_group");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		ProcessGroup processGroup = new ProcessGroup();
		try {
			processGroup.setId(rs.getInt("id"));
			processGroup.setIpaddress(rs.getString("ipaddress"));
			processGroup.setName(rs.getString("name"));
			processGroup.setNodeid(rs.getString("nodeid"));
			processGroup.setMon_flag(rs.getString("mon_flag"));
			processGroup.setAlarm_level(rs.getString("alarm_level"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return processGroup;
	}

	public boolean save(BaseVo vo) {
		ProcessGroup processGroup = (ProcessGroup) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_process_group(id,ipaddress,name,nodeid,mon_flag,alarm_level)values('");
		sql.append(processGroup.getId());
		sql.append("','");
		sql.append(processGroup.getIpaddress());
		sql.append("','");
		sql.append(processGroup.getName());
		sql.append("','");
		sql.append(processGroup.getNodeid());
		sql.append("','");
		sql.append(processGroup.getMon_flag());
		sql.append("','");
		sql.append(processGroup.getAlarm_level());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public synchronized int getNextId() {
		return super.getNextID();
	}

	public List findByIp(String ipaddress) {
		String sql = " where ipaddress='" + ipaddress + "'";
		return findByCondition(sql);
	}

	public List findByNodeid(String nodeid) {
		String sql = " where nodeid='" + nodeid + "'";
		return findByCondition(sql);
	}

	public boolean update(BaseVo vo) {
		ProcessGroup processGroup = (ProcessGroup) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_process_group set ipaddress='");
		sql.append(processGroup.getIpaddress());
		sql.append("',name='");
		sql.append(processGroup.getName());
		sql.append("',nodeid='");
		sql.append(processGroup.getNodeid());
		sql.append("',mon_flag='");
		sql.append(processGroup.getMon_flag());
		sql.append("',alarm_level='");
		sql.append(processGroup.getAlarm_level());
		sql.append("' where id=");
		sql.append(processGroup.getId());
		return saveOrUpdate(sql.toString());
	}

}