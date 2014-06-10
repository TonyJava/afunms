package com.afunms.system.vo;

import java.io.Serializable;

public class InterfaceVo implements Serializable {

	private String index;

	private String alias;

	private String app;

	private String kbs;

	private String statue;

	private String outPerc;

	private String inPerc;

	private String outs;

	private String ins;

	public String getAlias() {
		return alias;
	}

	public String getApp() {
		return app;
	}

	public String getIndex() {
		return index;
	}

	public String getInPerc() {
		return inPerc;
	}

	public String getIns() {
		return ins;
	}

	public String getKbs() {
		return kbs;
	}

	public String getOutPerc() {
		return outPerc;
	}

	public String getOuts() {
		return outs;
	}

	public String getStatue() {
		return statue;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setInPerc(String inPerc) {
		this.inPerc = inPerc;
	}

	public void setIns(String ins) {
		this.ins = ins;
	}

	public void setKbs(String kbs) {
		this.kbs = kbs;
	}

	public void setOutPerc(String outPerc) {
		this.outPerc = outPerc;
	}

	public void setOuts(String outs) {
		this.outs = outs;
	}

	public void setStatue(String statue) {
		this.statue = statue;
	}

}