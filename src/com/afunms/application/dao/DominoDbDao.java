package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.application.model.DominoDb;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class DominoDbDao extends BaseDao implements DaoInterface {
	public DominoDbDao() {
		super("nms_dominodb_realtime");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		DominoDb vo = new DominoDb();
		try {
			vo.setDbBuffPoolMax(rs.getString("DBBUFFPOOLMAX"));
			vo.setDbBuffPoolPeak(rs.getString("DBBUFFPOOLPEAK"));
			vo.setDbBuffPoolReads(rs.getString("DBBUFFPOOLREADS"));
			vo.setDbBuffPoolWrites(rs.getString("DBBUFFPOOLWRITES"));
			vo.setDbBuffPoolReadHit(rs.getString("DBBUFFPOOLREADHIT"));
			vo.setDbCacheEntry(rs.getString("DBCACHEENTRY"));
			vo.setDbCacheWaterMark(rs.getString("DBCACHEWATERMARK"));
			vo.setDbCacheHit(rs.getString("DBCACHEHIT"));
			vo.setDbCacheDbOpen(rs.getString("DBCACHEDBOPEN"));
			vo.setDbNifPoolPeak(rs.getString("DBNIFPOOLPEAK"));
			vo.setDbNifPoolUse(rs.getString("DBNIFPOOLUSE"));
			vo.setDbNsfPoolPeak(rs.getString("DBNSFPOOLPEAK"));
			vo.setDbNsfPoolUse(rs.getString("DBNSFPOOLUSE"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		return false;
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	public BaseVo findByIp(String ip) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_dominodb_realtime where ipaddress='" + ip + "'");
			if (rs.next())
				vo = loadFromRS(rs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		if (conn != null)
			conn.close();
		return vo;
	}
}