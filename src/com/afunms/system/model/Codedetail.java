package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class Codedetail extends BaseVo {

	private String id;
	private String name;
	private String code;
	private String desp;
	private int seq;
	private String typeid;

	public String getCode() {
		return code;
	}

	public String getDesp() {
		return desp;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getSeq() {
		return seq;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

}
