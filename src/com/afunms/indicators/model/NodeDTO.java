package com.afunms.indicators.model;

import com.afunms.common.base.BaseVo;

/**
 * ����Ϊ��׼���ܼ��ָ��
 * 
 * @author Administrator
 * 
 */

public class NodeDTO extends BaseVo {

	private int id;

	private String nodeid; // �豸id

	private String name; // �豸����

	private String ipaddress; // �豸ipaddress

	private String type; // �豸����

	private String subtype; // �豸������

	private String sysOid; // ϵͳoid

	private String businessId; // ҵ��id

	private String businessName; // ҵ������

	/**
	 * @return the businessId
	 */
	public String getBusinessId() {
		return businessId;
	}

	/**
	 * @return the businessName
	 */
	public String getBusinessName() {
		return businessName;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the ipaddress
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @return the sysOid
	 */
	public String getSysOid() {
		return sysOid;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param businessId
	 *            the businessId to set
	 */
	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	/**
	 * @param businessName
	 *            the businessName to set
	 */
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param ipaddress
	 *            the ipaddress to set
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param nodeid
	 *            the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @param subtype
	 *            the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	/**
	 * @param sysOid
	 *            the sysOid to set
	 */
	public void setSysOid(String sysOid) {
		this.sysOid = sysOid;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
