package com.afunms.ip.stationtype.model;

import com.afunms.common.base.BaseVo;

public class alltype extends BaseVo {

	private int id;
	private String backbone_name;
	private String loopback_begin;
	private String loopback_end;
	private String pe_begin;
	private String pe_end;
	private String pe_ce_begin;
	private String pe_ce_end;
	private String bus_begin;
	private String bus_end;

	public String getBackbone_name() {
		return backbone_name;
	}

	public String getBus_begin() {
		return bus_begin;
	}

	public String getBus_end() {
		return bus_end;
	}

	public int getId() {
		return id;
	}

	public String getLoopback_begin() {
		return loopback_begin;
	}

	public String getLoopback_end() {
		return loopback_end;
	}

	public String getPe_begin() {
		return pe_begin;
	}

	public String getPe_ce_begin() {
		return pe_ce_begin;
	}

	public String getPe_ce_end() {
		return pe_ce_end;
	}

	public String getPe_end() {
		return pe_end;
	}

	public void setBackbone_name(String backbone_name) {
		this.backbone_name = backbone_name;
	}

	public void setBus_begin(String bus_begin) {
		this.bus_begin = bus_begin;
	}

	public void setBus_end(String bus_end) {
		this.bus_end = bus_end;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLoopback_begin(String loopback_begin) {
		this.loopback_begin = loopback_begin;
	}

	public void setLoopback_end(String loopback_end) {
		this.loopback_end = loopback_end;
	}

	public void setPe_begin(String pe_begin) {
		this.pe_begin = pe_begin;
	}

	public void setPe_ce_begin(String pe_ce_begin) {
		this.pe_ce_begin = pe_ce_begin;
	}

	public void setPe_ce_end(String pe_ce_end) {
		this.pe_ce_end = pe_ce_end;
	}

	public void setPe_end(String pe_end) {
		this.pe_end = pe_end;
	}

}
