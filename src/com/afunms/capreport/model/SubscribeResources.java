/**
 * @author sunqichang/孙启昌
 * Created on May 16, 2011 3:15:20 PM
 */
package com.afunms.capreport.model;

import com.afunms.common.base.BaseVo;

public class SubscribeResources extends BaseVo {
	private int subscribe_id;

	private String bidtext;// 业务名称

	private String BID; // 业务ID

	private String username;// 用户名

	private String email;

	private String emailtitle;

	private String emailcontent;

	private String attachmentformat;

	private String report_type;

	private int report_senddate;

	private int report_sendfrequency;

	private String report_time_month;

	private String report_time_week;

	private String report_time_day;

	private String report_time_hou;

	private String report_day_stop;

	private String report_week_stop;

	private String report_month_stop;

	private String report_season_stop;

	private String report_year_stop;

	public int getSubscribe_id() {
		return subscribe_id;
	}

	public void setSubscribe_id(int subscribe_id) {
		this.subscribe_id = subscribe_id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailtitle() {
		return emailtitle;
	}

	public void setEmailtitle(String emailtitle) {
		this.emailtitle = emailtitle;
	}

	public String getEmailcontent() {
		return emailcontent;
	}

	public void setEmailcontent(String emailcontent) {
		this.emailcontent = emailcontent;
	}

	public String getAttachmentformat() {
		return attachmentformat;
	}

	public void setAttachmentformat(String attachmentformat) {
		this.attachmentformat = attachmentformat;
	}

	public String getReport_type() {
		return report_type;
	}

	public void setReport_type(String report_type) {
		this.report_type = report_type;
	}

	public int getReport_senddate() {
		return report_senddate;
	}

	public void setReport_senddate(int report_senddate) {
		this.report_senddate = report_senddate;
	}

	public int getReport_sendfrequency() {
		return report_sendfrequency;
	}

	public void setReport_sendfrequency(int report_sendfrequency) {
		this.report_sendfrequency = report_sendfrequency;
	}

	public String getReport_time_month() {
		return report_time_month;
	}

	public void setReport_time_month(String report_time_month) {
		this.report_time_month = report_time_month;
	}

	public String getReport_time_week() {
		return report_time_week;
	}

	public void setReport_time_week(String report_time_week) {
		this.report_time_week = report_time_week;
	}

	public String getReport_time_day() {
		return report_time_day;
	}

	public void setReport_time_day(String report_time_day) {
		this.report_time_day = report_time_day;
	}

	public String getReport_time_hou() {
		return report_time_hou;
	}

	public void setReport_time_hou(String report_time_hou) {
		this.report_time_hou = report_time_hou;
	}

	public String getReport_day_stop() {
		return report_day_stop;
	}

	public void setReport_day_stop(String report_day_stop) {
		this.report_day_stop = report_day_stop;
	}

	public String getReport_week_stop() {
		return report_week_stop;
	}

	public void setReport_week_stop(String report_week_stop) {
		this.report_week_stop = report_week_stop;
	}

	public String getReport_month_stop() {
		return report_month_stop;
	}

	public void setReport_month_stop(String report_month_stop) {
		this.report_month_stop = report_month_stop;
	}

	public String getReport_season_stop() {
		return report_season_stop;
	}

	public void setReport_season_stop(String report_season_stop) {
		this.report_season_stop = report_season_stop;
	}

	public String getReport_year_stop() {
		return report_year_stop;
	}

	public void setReport_year_stop(String report_year_stop) {
		this.report_year_stop = report_year_stop;
	}

	public String getBID() {
		return BID;
	}

	public void setBID(String bid) {
		BID = bid;
	}

	public String getBidtext() {
		return bidtext;
	}

	public void setBidtext(String bidtext) {
		this.bidtext = bidtext;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
