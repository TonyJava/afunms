package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class Errptlog extends BaseVo {
	private int id;
	private String labels;
	private String identifier;
	private java.util.Calendar collettime;
	private int seqnumber;
	private String nodeid;
	private String machineid;
	private String errptclass;
	private String errpttype;
	private String resourcename;
	private String resourceclass;
	private String rescourcetype;
	private String locations;
	private String vpd;
	private String descriptions;
	private String hostid;

	/**
	 * @return the collettime
	 */
	public java.util.Calendar getCollettime() {
		return collettime;
	}

	/**
	 * @return the descriptions
	 */
	public String getDescriptions() {
		return descriptions;
	}

	/**
	 * @return the errptclass
	 */
	public String getErrptclass() {
		return errptclass;
	}

	/**
	 * @return the errpttype
	 */
	public String getErrpttype() {
		return errpttype;
	}

	public String getHostid() {
		return hostid;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the labels
	 */
	public String getLabels() {
		return labels;
	}

	/**
	 * @return the locations
	 */
	public String getLocations() {
		return locations;
	}

	/**
	 * @return the machineid
	 */
	public String getMachineid() {
		return machineid;
	}

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @return the rescourcetype
	 */
	public String getRescourcetype() {
		return rescourcetype;
	}

	/**
	 * @return the resourceclass
	 */
	public String getResourceclass() {
		return resourceclass;
	}

	/**
	 * @return the resourcename
	 */
	public String getResourcename() {
		return resourcename;
	}

	/**
	 * @return the seqnumber
	 */
	public int getSeqnumber() {
		return seqnumber;
	}

	/**
	 * @return the vpd
	 */
	public String getVpd() {
		return vpd;
	}

	/**
	 * @param collettime
	 *            the collettime to set
	 */
	public void setCollettime(java.util.Calendar collettime) {
		this.collettime = collettime;
	}

	/**
	 * @param descriptions
	 *            the descriptions to set
	 */
	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * @param errptclass
	 *            the errptclass to set
	 */
	public void setErrptclass(String errptclass) {
		this.errptclass = errptclass;
	}

	/**
	 * @param errpttype
	 *            the errpttype to set
	 */
	public void setErrpttype(String errpttype) {
		this.errpttype = errpttype;
	}

	public void setHostid(String hostid) {
		this.hostid = hostid;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @param labels
	 *            the labels to set
	 */
	public void setLabels(String labels) {
		this.labels = labels;
	}

	/**
	 * @param locations
	 *            the locations to set
	 */
	public void setLocations(String locations) {
		this.locations = locations;
	}

	/**
	 * @param machineid
	 *            the machineid to set
	 */
	public void setMachineid(String machineid) {
		this.machineid = machineid;
	}

	/**
	 * @param nodeid
	 *            the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @param rescourcetype
	 *            the rescourcetype to set
	 */
	public void setRescourcetype(String rescourcetype) {
		this.rescourcetype = rescourcetype;
	}

	/**
	 * @param resourceclass
	 *            the resourceclass to set
	 */
	public void setResourceclass(String resourceclass) {
		this.resourceclass = resourceclass;
	}

	/**
	 * @param resourcename
	 *            the resourcename to set
	 */
	public void setResourcename(String resourcename) {
		this.resourcename = resourcename;
	}

	/**
	 * @param seqnumber
	 *            the seqnumber to set
	 */
	public void setSeqnumber(int seqnumber) {
		this.seqnumber = seqnumber;
	}

	/**
	 * @param vpd
	 *            the vpd to set
	 */
	public void setVpd(String vpd) {
		this.vpd = vpd;
	}

}
