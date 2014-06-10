package com.afunms.system.vo;

import java.io.Serializable;

public class IpVo implements Serializable {

	private String index;

	private String descr;

	private String speed;

	private String aliasip;

	private String statue;

	private String types;

	public String getAliasip() {
		return aliasip;
	}

	public String getDescr() {
		return descr;
	}

	public String getIndex() {
		return index;
	}

	public String getSpeed() {
		return speed;
	}

	public String getStatue() {
		return statue;
	}

	public String getTypes() {
		return types;
	}

	public void setAliasip(String aliasip) {
		this.aliasip = aliasip;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public void setStatue(String statue) {
		this.statue = statue;
	}

	public void setTypes(String types) {
		this.types = types;
	}

}