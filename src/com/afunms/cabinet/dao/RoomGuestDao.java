package com.afunms.cabinet.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.cabinet.model.RoomGuest;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.SystemConstant;

/**
 * @description »ú·¿À´±öDAO
 * @author wangxiangyong
 * @date Dec 23, 2011
 */
public class RoomGuestDao extends BaseDao implements DaoInterface {
	public RoomGuestDao() {
		super("nms_cabinet_guest");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		RoomGuest guest = new RoomGuest();
		try {
			guest.setId(rs.getInt("id"));
			guest.setCabinetid(rs.getInt("cabinetid"));
			guest.setName(rs.getString("name"));
			guest.setUnit(rs.getString("unit"));
			guest.setPhone(rs.getString("phone"));
			guest.setMail(rs.getString("mail"));
			guest.setInTime(rs.getString("inTime"));
			guest.setOutTime(rs.getString("outTime"));
			guest.setDotime(rs.getString("dotime"));
			guest.setReason(rs.getString("reason"));
			guest.setAudit(rs.getString("audits"));
			guest.setBak(rs.getString("bak"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return guest;
	}

	public boolean save(BaseVo vo) {
		RoomGuest guest = (RoomGuest) vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("insert into nms_cabinet_guest(id,cabinetid,name,unit,phone,mail,inTime,outTime,dotime,reason,audits,bak)values(");
		sql.append(getNextID());
		sql.append(",");
		sql.append(guest.getCabinetid());
		sql.append(",'");
		sql.append(guest.getName());
		sql.append("','");
		sql.append(guest.getUnit());
		sql.append("','");
		sql.append(guest.getPhone());
		sql.append("','");
		sql.append(guest.getMail());
		sql.append("','");
		sql.append(guest.getInTime());
		sql.append("','");
		sql.append(guest.getOutTime());
		if (SystemConstant.DBType.equals("mysql")) {
			sql.append("','");
			sql.append(guest.getDotime());
			sql.append("','");
		} else if (SystemConstant.DBType.equals("oracle")) {
			sql.append("',to_date('");
			sql.append(guest.getDotime());
			sql.append("','yyyy-mm-dd hh24:mi:ss'),'");
		}
		sql.append(guest.getReason());
		sql.append("','");
		sql.append(guest.getAudit());
		sql.append("','");
		sql.append(guest.getBak());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		RoomGuest guest = (RoomGuest) vo;
		StringBuffer sql = new StringBuffer(200);
		sql.append("update nms_cabinet_guest ");
		sql.append("set cabinetid=");
		sql.append(guest.getCabinetid());
		sql.append(", name='");
		sql.append(guest.getName());
		sql.append("',unit='");
		sql.append(guest.getUnit());
		sql.append("',phone='");
		sql.append(guest.getPhone());
		sql.append("',mail='");
		sql.append(guest.getMail());
		sql.append("',inTime='");
		sql.append(guest.getInTime());
		sql.append("',outTime='");
		sql.append(guest.getOutTime());
		sql.append("',dotime='");
		sql.append(guest.getDotime());
		sql.append("',reason='");
		sql.append(guest.getReason());
		sql.append("',audits='");
		sql.append(guest.getAudit());
		sql.append("',bak='");
		sql.append(guest.getBak());
		sql.append("' where id=");
		sql.append(guest.getId());
		return saveOrUpdate(sql.toString());
	}

}
