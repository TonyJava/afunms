package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class Knowledgebase extends BaseVo {
	private int id;
	private String category;
	private String entity;
	private String subentity;
	private String titles;
	private String contents;
	private String bak;
	private String attachfiles;
	private String userid;
	private String ktime;

	public String getAttachfiles() {
		return attachfiles;
	}

	public String getBak() {
		return bak;
	}

	public String getCategory() {
		return category;
	}

	public String getContents() {
		return contents;
	}

	public String getEntity() {
		return entity;
	}

	public int getId() {
		return id;
	}

	public String getKtime() {
		return ktime;
	}

	public String getSubentity() {
		return subentity;
	}

	public String getTitles() {
		return titles;
	}

	public String getUserid() {
		return userid;
	}

	public void setAttachfiles(String attachfiles) {
		this.attachfiles = attachfiles;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setKtime(String ktime) {
		this.ktime = ktime;
	}

	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}

	public void setTitles(String titles) {
		this.titles = titles;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
