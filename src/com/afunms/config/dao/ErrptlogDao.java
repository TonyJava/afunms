package com.afunms.config.dao;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.config.model.Diskconfig;
import com.afunms.config.model.Errptlog;

@SuppressWarnings("unchecked")
public class ErrptlogDao extends BaseDao implements DaoInterface {

	public ErrptlogDao() {
		super("nms_errptlog");
	}

	@Override
	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_errptlog where id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	@Override
	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_errptlog order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		Errptlog vo = new Errptlog();
		try {
			vo.setId(rs.getInt("id"));
			vo.setLabels(rs.getString("labels"));
			vo.setIdentifier(rs.getString("identifier"));
			Calendar cal = Calendar.getInstance();
			Date newdate = new Date();
			newdate.setTime(rs.getTimestamp("collettime").getTime());
			cal.setTime(newdate);
			vo.setCollettime(cal);
			vo.setSeqnumber(rs.getInt("seqnumber"));
			vo.setNodeid(rs.getString("nodeid"));
			vo.setMachineid(rs.getString("machineid"));
			vo.setErrptclass(rs.getString("errptclass"));
			vo.setErrpttype(rs.getString("errpttype"));
			vo.setResourcename(rs.getString("resourcename"));
			vo.setResourceclass(rs.getString("resourceclass"));
			vo.setRescourcetype(rs.getString("resourcetype"));
			vo.setLocations(rs.getString("locations"));
			vo.setVpd(rs.getString("vpd"));
			vo.setDescriptions(rs.getString("descriptions"));
			vo.setHostid(rs.getString("hostid"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		Errptlog vo = (Errptlog) baseVo;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		StringBuffer sql = new StringBuffer(100);
		Calendar tempCal = vo.getCollettime();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		sql
				.append("insert into nms_errptlog(labels,identifier,collettime,seqnumber,nodeid,machineid,errptclass,errpttype,resourcename,resourceclass,resourcetype,locations,vpd,descriptions,hostid) values('");
		sql.append(vo.getLabels());
		sql.append("','");
		sql.append(vo.getIdentifier());
		sql.append("','");
		sql.append(time);
		sql.append("',");
		sql.append(vo.getSeqnumber());
		sql.append(",'");
		sql.append(vo.getNodeid());
		sql.append("','");
		sql.append(vo.getMachineid());
		sql.append("','");
		sql.append(vo.getErrptclass());
		sql.append("','");
		sql.append(vo.getErrpttype());
		sql.append("','");
		sql.append(vo.getResourcename());
		sql.append("','");
		sql.append(vo.getResourceclass());
		sql.append("','");
		sql.append(vo.getRescourcetype());
		sql.append("','");
		sql.append(vo.getLocations());
		sql.append("','");
		sql.append(vo.getVpd());
		sql.append("','");
		sql.append(vo.getDescriptions());
		sql.append("','");
		sql.append(vo.getHostid());
		sql.append("')");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		Diskconfig vo = (Diskconfig) baseVo;
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_errptlog set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',name='");
		sql.append(vo.getName());
		sql.append("',diskindex=");
		sql.append(vo.getDiskindex());
		sql.append(",linkuse='");
		if (vo.getLinkuse() != null) {
			sql.append(vo.getLinkuse());
		} else {
			sql.append("");
		}
		sql.append("',sms=");
		sql.append(vo.getSms());
		sql.append(",bak='");
		sql.append(vo.getBak());
		sql.append("',monflag=");
		sql.append(vo.getMonflag());
		sql.append(",reportflag=");
		sql.append(vo.getReportflag());
		sql.append(",sms1=");
		sql.append(vo.getSms1());
		sql.append(",sms2=");
		sql.append(vo.getSms2());
		sql.append(",sms3=");
		sql.append(vo.getSms3());
		sql.append(",limenvalue=");
		sql.append(vo.getLimenvalue());
		sql.append(",limenvalue1=");
		sql.append(vo.getLimenvalue1());
		sql.append(",limenvalue2=");
		sql.append(vo.getLimenvalue2());
		sql.append(" where id=");
		sql.append(vo.getId());

		try {
			conn.executeUpdate(sql.toString());
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

}
