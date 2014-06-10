package com.afunms.application.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.afunms.application.model.DominoMail;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class DominoEmailDao extends BaseDao implements DaoInterface {
	public DominoEmailDao() {
		super("nms_dominomail_realtime");
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		DominoMail vo = new DominoMail();
		try {
			vo.setMailDead(rs.getString("MAILDEAD"));
			vo.setMailWaiting(rs.getString("MAILWAITING"));
			vo.setMailWaitingRecipients(rs.getString("MAILWAITINGRECIPIENTS"));
			vo.setMailDeliverRate(rs.getString("MAILDELIVERRATE"));
			vo.setMailTransferRate(rs.getString("MAILTRANSFERRATE"));
			vo.setMailDeliverThreadsMax(rs.getString("MAILDELIVERTHREADSMAX"));
			vo.setMailDeliverThreadsTotal(rs.getString("MAILDELIVERTHREADSTOTAL"));
			vo.setMailTransferThreadsMax(rs.getString("MAILTRANSFERTHREADSMAX"));
			vo.setMailTransferThreadsTotal(rs.getString("MAILTRANSFERTHREADSTOTAL"));
			vo.setMailAvgSize(rs.getString("MAILAVGSIZE"));
			vo.setMailAvgTime(rs.getString("MAILAVGTIME"));
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
			rs = conn.executeQuery("select * from nms_dominomail_realtime where ipaddress='" + ip + "'");
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