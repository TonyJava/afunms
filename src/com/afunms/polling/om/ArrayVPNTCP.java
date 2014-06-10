package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

public class ArrayVPNTCP implements Serializable {

	private int id;

	private String ipaddress;

	private String type;

	private String subtype;

	private Calendar Collecttime;

	private int ctcpActiveOpens;

	private long ctcpPassiveOpens;

	private int ctcpAttemptFails;

	private long ctcpEstabResets;

	private int ctcpCurrEstab;

	private long ctcpInSegs;

	private long ctcpOutSegs;

	private long ctcpRetransSegs;

	private int ctcpInErrs;

	private long ctcpOutRsts;

	public Calendar getCollecttime() {
		return Collecttime;
	}

	public int getCtcpActiveOpens() {
		return ctcpActiveOpens;
	}

	public int getCtcpAttemptFails() {
		return ctcpAttemptFails;
	}

	public int getCtcpCurrEstab() {
		return ctcpCurrEstab;
	}

	public long getCtcpEstabResets() {
		return ctcpEstabResets;
	}

	public int getCtcpInErrs() {
		return ctcpInErrs;
	}

	public long getCtcpInSegs() {
		return ctcpInSegs;
	}

	public long getCtcpOutRsts() {
		return ctcpOutRsts;
	}

	public long getCtcpOutSegs() {
		return ctcpOutSegs;
	}

	public long getCtcpPassiveOpens() {
		return ctcpPassiveOpens;
	}

	public long getCtcpRetransSegs() {
		return ctcpRetransSegs;
	}

	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public void setCollecttime(Calendar collecttime) {
		Collecttime = collecttime;
	}

	public void setCtcpActiveOpens(int ctcpActiveOpens) {
		this.ctcpActiveOpens = ctcpActiveOpens;
	}

	public void setCtcpAttemptFails(int ctcpAttemptFails) {
		this.ctcpAttemptFails = ctcpAttemptFails;
	}

	public void setCtcpCurrEstab(int ctcpCurrEstab) {
		this.ctcpCurrEstab = ctcpCurrEstab;
	}

	public void setCtcpEstabResets(long ctcpEstabResets) {
		this.ctcpEstabResets = ctcpEstabResets;
	}

	public void setCtcpInErrs(int ctcpInErrs) {
		this.ctcpInErrs = ctcpInErrs;
	}

	public void setCtcpInSegs(long ctcpInSegs) {
		this.ctcpInSegs = ctcpInSegs;
	}

	public void setCtcpOutRsts(long ctcpOutRsts) {
		this.ctcpOutRsts = ctcpOutRsts;
	}

	public void setCtcpOutSegs(long ctcpOutSegs) {
		this.ctcpOutSegs = ctcpOutSegs;
	}

	public void setCtcpPassiveOpens(long ctcpPassiveOpens) {
		this.ctcpPassiveOpens = ctcpPassiveOpens;
	}

	public void setCtcpRetransSegs(long ctcpRetransSegs) {
		this.ctcpRetransSegs = ctcpRetransSegs;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

}
