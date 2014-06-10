package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class TimingBackupCondition extends BaseVo {
	private int id;
	private int timingId;
	private int isContain;
	private String content;

	public String getContent() {
		return content;
	}

	public int getId() {
		return id;
	}

	public int getIsContain() {
		return isContain;
	}

	public int getTimingId() {
		return timingId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIsContain(int isContain) {
		this.isContain = isContain;
	}

	public void setTimingId(int timingId) {
		this.timingId = timingId;
	}

}
