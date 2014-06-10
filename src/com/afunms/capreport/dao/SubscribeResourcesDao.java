package com.afunms.capreport.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.afunms.capreport.model.SubscribeResources;
import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;

public class SubscribeResourcesDao extends BaseDao implements DaoInterface {
	private static Logger log = Logger.getLogger(SubscribeResourcesDao.class);

	public SubscribeResourcesDao() {
		super();
	}

	@Override
	public BaseVo loadFromRS(ResultSet rs) {
		SubscribeResources vo = new SubscribeResources();
		try {
			vo.setSubscribe_id(rs.getInt("subscribe_id"));
			vo.setBidtext(rs.getString("BIDTEXT"));
			vo.setBID(rs.getString("BID"));
			vo.setUsername(rs.getString("USERNAME"));
			vo.setEmail(rs.getString("EMAIL"));
			vo.setEmailtitle(rs.getString("EMAILTITLE"));
			vo.setEmailcontent(rs.getString("EMAILCONTENT"));
			vo.setAttachmentformat(rs.getString("ATTACHMENTFORMAT"));
			vo.setReport_type(rs.getString("REPORT_TYPE"));
			vo.setReport_senddate(rs.getInt("REPORT_SENDDATE"));
			vo.setReport_sendfrequency(rs.getInt("REPORT_SENDFREQUENCY"));
			vo.setReport_time_month(rs.getString("REPORT_TIME_MONTH"));
			vo.setReport_time_week(rs.getString("REPORT_TIME_WEEK"));
			vo.setReport_time_day(rs.getString("REPORT_TIME_DAY"));
			vo.setReport_time_hou(rs.getString("REPORT_TIME_HOU"));
			vo.setReport_day_stop(rs.getString("REPORT_DAY_STOP"));
			vo.setReport_week_stop(rs.getString("REPORT_WEEK_STOP"));
			vo.setReport_month_stop(rs.getString("REPORT_MONTH_STOP"));
			vo.setReport_season_stop(rs.getString("REPORT_SEASON_STOP"));
			vo.setReport_year_stop(rs.getString("REPORT_YEAR_STOP"));
		} catch (Exception e) {
			log.error(e);
		}
		return vo;
	}

	public boolean save(BaseVo vo) {
		SubscribeResources sr = (SubscribeResources) vo;
		sr.getSubscribe_id();
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO sys_subscribe_resources (SUBSCRIBE_ID, BIDTEXT, BID  , USERNAME,EMAIL, ").append("	EMAILTITLE,	EMAILCONTENT, 	ATTACHMENTFORMAT, 	REPORT_TYPE, 	")
				.append("REPORT_SENDDATE, 	REPORT_SENDFREQUENCY, 	REPORT_TIME_MONTH,").append(" 	REPORT_TIME_WEEK, 	REPORT_TIME_DAY, 	REPORT_TIME_HOU, 	REPORT_DAY_STOP, 	")
				.append("REPORT_WEEK_STOP, 	REPORT_MONTH_STOP, 	REPORT_SEASON_STOP, 	REPORT_YEAR_STOP)	VALUES	(").append("").append(sr.getSubscribe_id()).append(",  '").append(
						sr.getBidtext()).append("'  , '").append(sr.getBID()).append("'  , '").append(sr.getUsername()).append("',	'").append(sr.getEmail()).append("','").append(
						sr.getEmailtitle()).append("', '").append(sr.getEmailcontent()).append("',").append(" '").append(sr.getAttachmentformat()).append("', ").append("	'")
				.append(sr.getReport_type()).append("', 	'").append(sr.getReport_senddate()).append("', 	'").append(sr.getReport_sendfrequency()).append("', 	'").append(
						sr.getReport_time_month()).append("', ").append("	'").append(sr.getReport_time_week()).append("', 	'").append(sr.getReport_time_day()).append("', 	'")
				.append(sr.getReport_time_hou()).append("', 	'").append(sr.getReport_day_stop()).append("', 	").append("'").append(sr.getReport_week_stop()).append("', 	'")
				.append(sr.getReport_month_stop()).append("', 	'").append(sr.getReport_season_stop()).append("', 	'").append(sr.getReport_year_stop()).append("'	)");
		return this.saveOrUpdate(sql.toString());
	}

	/**
	 * 端口对比报表配置资源信息表 by zhangys
	 * 
	 * @param vo
	 * @param table
	 * @return
	 */
	public boolean save(BaseVo vo, String table) {
		SubscribeResources sr = (SubscribeResources) vo;
		sr.getSubscribe_id();
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO " + table + " (SUBSCRIBE_ID, BIDTEXT, BID  , USERNAME,EMAIL, ").append("	EMAILTITLE,	EMAILCONTENT, 	ATTACHMENTFORMAT, 	REPORT_TYPE, 	").append(
				"REPORT_SENDDATE, 	REPORT_SENDFREQUENCY, 	REPORT_TIME_MONTH,").append(" 	REPORT_TIME_WEEK, 	REPORT_TIME_DAY, 	REPORT_TIME_HOU, 	REPORT_DAY_STOP, 	").append(
				"REPORT_WEEK_STOP, 	REPORT_MONTH_STOP, 	REPORT_SEASON_STOP, 	REPORT_YEAR_STOP)	VALUES	(").append("").append(sr.getSubscribe_id()).append(",  '").append(
				sr.getBidtext()).append("'  , '").append(sr.getBID()).append("'  , '").append(sr.getUsername()).append("',	'").append(sr.getEmail()).append("','").append(
				sr.getEmailtitle()).append("', '").append(sr.getEmailcontent()).append("',").append(" '").append(sr.getAttachmentformat()).append("', ").append("	'").append(
				sr.getReport_type()).append("', 	'").append(sr.getReport_senddate()).append("', 	'").append(sr.getReport_sendfrequency()).append("', 	'").append(
				sr.getReport_time_month()).append("', ").append("	'").append(sr.getReport_time_week()).append("', 	'").append(sr.getReport_time_day()).append("', 	'").append(
				sr.getReport_time_hou()).append("', 	'").append(sr.getReport_day_stop()).append("', 	").append("'").append(sr.getReport_week_stop()).append("', 	'").append(
				sr.getReport_month_stop()).append("', 	'").append(sr.getReport_season_stop()).append("', 	'").append(sr.getReport_year_stop()).append("'	)");
		return this.saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo vo) {
		return false;
	}

	/**
	 * @param id
	 * @return
	 */
	public SubscribeResources findById(String id) {
		SubscribeResources vo = new SubscribeResources();
		try {
			rs = conn.executeQuery("select * from sys_subscribe_resources where SUBSCRIBE_ID=" + id);
			while (rs.next())
				vo = (SubscribeResources) loadFromRS(rs);
		} catch (Exception e) {
			log.error(e);

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error(e);
				}
			}
			if (conn != null) {
				conn.close();
			}

		}
		return vo;
	}

	/**
	 * by zhangys
	 * 
	 * @param id
	 * @return
	 */
	public SubscribeResources findById(String table, String id) {
		SubscribeResources vo = new SubscribeResources();
		try {
			rs = conn.executeQuery("select * from " + table + " where SUBSCRIBE_ID=" + id);
			while (rs.next())
				vo = (SubscribeResources) loadFromRS(rs);
		} catch (Exception e) {
			log.error(e);

		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					log.error("", e);
				}
			}
			if (conn != null) {
				conn.close();
			}

		}
		return vo;
	}
}
