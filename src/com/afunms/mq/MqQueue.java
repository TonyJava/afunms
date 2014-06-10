package com.afunms.mq;

public class MqQueue implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1004302532706853719L;
	private String qname;
	private String qtype;
	private String persistent;
	private String usage;
	private String qdepth;

	private String remoteQName;
	private String remoteQM;
	private String xmitQName;

	public String getPersistent() {
		return persistent;
	}

	public String getQdepth() {
		return qdepth;
	}

	public String getQname() {
		return qname;
	}

	public String getQtype() {
		return qtype;
	}

	public String getRemoteQM() {
		return remoteQM;
	}

	public String getRemoteQName() {
		return remoteQName;
	}

	public String getUsage() {
		return usage;
	}

	public String getXmitQName() {
		return xmitQName;
	}

	public void setPersistent(String persistent) {
		this.persistent = persistent;
	}

	public void setQdepth(String qdepth) {
		this.qdepth = qdepth;
	}

	public void setQname(String qname) {
		this.qname = qname;
	}

	public void setQtype(String qtype) {
		this.qtype = qtype;
	}

	public void setRemoteQM(String remoteQM) {
		this.remoteQM = remoteQM;
	}

	public void setRemoteQName(String remoteQName) {
		this.remoteQName = remoteQName;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public void setXmitQName(String xmitQName) {
		this.xmitQName = xmitQName;
	}

}
