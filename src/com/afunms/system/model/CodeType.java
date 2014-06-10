package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

/**
 * 数据字典一级类型
 * 
 * @author HXL
 * 
 */
public class CodeType extends BaseVo {

	private String id;
	private String name;
	private String code;
	private String desp;
	private int seq;
	private String type;

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

	public String getType() {
		return type;
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

	public void setType(String type) {
		this.type = type;
	}

}
