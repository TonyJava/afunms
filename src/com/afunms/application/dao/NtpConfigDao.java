package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.NtpConfig;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Ntp;

@SuppressWarnings("unchecked")
public class NtpConfigDao extends BaseDao implements DaoInterface {

	public NtpConfigDao() {
		super("nms_ntpconfig");
	}

	public boolean delete(String[] ids) {
		if (ids != null && ids.length > 0) {
			NtpConfigDao webdao = new NtpConfigDao();
			List list = webdao.loadAll();
			if (list == null)
				list = new ArrayList();
			ShareData.setNtpconfiglist(list);
			clearRubbish(list);

		}
		return super.delete(ids);
	}

	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getNtpList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Ntp) {
				Ntp node = (Ntp) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						NtpConfig hostNode = (NtpConfig) baseVoList.get(j);
						if (node.getId() == hostNode.getId()) {
							flag = true;
						}
					}
					if (!flag) {
						nodeList.remove(node);
					}
				}
			}
		}
	}

	public BaseVo loadFromRS(ResultSet rs) {
		NtpConfig vo = new NtpConfig();

		try {
			vo.setId(rs.getInt("id"));
			vo.setFlag(rs.getInt("flag"));
			vo.setPort(rs.getString("port"));
			vo.setMon_flag(rs.getInt("mon_flag"));
			vo.setAlias(rs.getString("alias"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setNetid(rs.getString("netid"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setIpAddress(rs.getString("ipAddress"));
			vo.setSupperid(rs.getInt("supperid"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		NtpConfig vo1 = (NtpConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into nms_ntpconfig(id,ipaddress,port,alias,flag,sendmobiles,sendemail,sendphone,supperid,netid) values('");
		sql.append(vo1.getId());
		sql.append("','");
		sql.append(vo1.getIpAddress());
		sql.append("','");
		sql.append(vo1.getPort());
		sql.append("','");
		sql.append(vo1.getAlias());
		sql.append("','");
		sql.append(vo1.getFlag());
		sql.append("','");
		sql.append(vo1.getSendmobiles());
		sql.append("','");
		sql.append(vo1.getSendemail());
		sql.append("','");
		sql.append(vo1.getSendphone());
		sql.append("','");
		sql.append(vo1.getSupperid());
		sql.append("','");
		sql.append(vo1.getNetid());
		sql.append("')");
		boolean result = saveOrUpdate(sql.toString());
		CreateTableManager ctable = new CreateTableManager();
		ctable.createTable("ping", vo1.getId() + "", "ping");
		conn.executeBatch();
		conn.close();
		return result;

	}

	public List getNtpByBID(Vector bids) {
		StringBuffer sql = new StringBuffer();
		String wstr = "";
		if (bids != null && bids.size() > 0) {
			for (int i = 0; i < bids.size(); i++) {
				if (wstr.trim().length() == 0) {
					wstr = wstr + " where ( netid like '%," + bids.get(i) + ",%' ";
				} else {
					wstr = wstr + " or netid like '%," + bids.get(i) + ",%' ";
				}

			}
			wstr = wstr + ")";
		}
		sql.append("select * from nms_ntpconfig " + wstr);
		return findByCriteria(sql.toString());
	}

	public List getNtpByFlag(int flag) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from nms_ntpconfig where flag = " + flag);
		return findByCriteria(sql.toString());
	}

	public boolean update(BaseVo vo) {
		NtpConfig vo1 = (NtpConfig) vo;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_ntpconfig set alias ='");
		sql.append(vo1.getAlias());
		sql.append("',flag='");
		sql.append(vo1.getFlag());
		sql.append("',port='");
		sql.append(vo1.getPort());
		sql.append("',netid='");
		sql.append(vo1.getNetid());
		sql.append("',sendmobiles='");
		sql.append(vo1.getSendmobiles());
		sql.append("',sendemail='");
		sql.append(vo1.getSendemail());
		sql.append("',sendphone='");
		sql.append(vo1.getSendphone());
		sql.append("',ipaddress='");
		sql.append(vo1.getIpAddress());
		sql.append("',supperid='");
		sql.append(vo1.getSupperid());
		sql.append("' where id=" + vo1.getId());
		return saveOrUpdate(sql.toString());
	}
}