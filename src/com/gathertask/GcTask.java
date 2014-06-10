package com.gathertask;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.config.model.Portconfig;
import com.afunms.util.DataGate;

@SuppressWarnings("unchecked")
public class GcTask extends TimerTask {
	private Logger logger = Logger.getLogger(this.getClass());
	@Override
	public void run() {
		logger.info(" À¬»ø»ØÊÕ ");
		System.gc();
		List portconfiglist = new ArrayList();
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		Portconfig portconfig = null;
		String tempsql = "select * from system_portconfig h where h.sms = 1 ";
		Hashtable portconfigHash = new Hashtable();
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(tempsql);
			while (rs.next()) {
				portconfiglist.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				DataGate.freeCon(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (portconfiglist != null && portconfiglist.size() > 0) {
			for (int i = 0; i < portconfiglist.size(); i++) {
				portconfig = (Portconfig) portconfiglist.get(i);
				if (portconfigHash.containsKey(portconfig.getIpaddress())) {
					List portlist = (List) portconfigHash.get(portconfig.getIpaddress());
					portlist.add(portconfig);
					portconfigHash.put(portconfig.getIpaddress(), portlist);
				} else {
					List portlist = new ArrayList();
					portlist.add(portconfig);
					portconfigHash.put(portconfig.getIpaddress(), portlist);
				}
			}
		}
		ShareData.setPortConfigHash(portconfigHash);
	}

	private BaseVo loadFromRS(ResultSet rs) {
		Portconfig vo = new Portconfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBak(rs.getString("bak"));
			vo.setIpaddress(rs.getString("ipaddress"));
			if (rs.getString("linkuse") == null || "null".equalsIgnoreCase(rs.getString("linkuse"))) {
				vo.setLinkuse("");
			} else {
				vo.setLinkuse(rs.getString("linkuse"));
			}
			vo.setName(rs.getString("name"));
			vo.setPortindex(rs.getInt("portindex"));
			vo.setReportflag(rs.getInt("reportflag"));
			vo.setSms(rs.getInt("sms"));
			vo.setInportalarm(rs.getString("inportalarm"));
			vo.setOutportalarm(rs.getString("outportalarm"));
			vo.setSpeed(rs.getString("speed"));
			vo.setAlarmlevel(rs.getString("alarmlevel"));
			vo.setFlag(rs.getString("flag"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

}
