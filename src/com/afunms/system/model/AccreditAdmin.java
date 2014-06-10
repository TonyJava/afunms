package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

public class AccreditAdmin extends BaseVo {
	private int id;
	private String roleid;
	private String funcid;

	/**
	 * @return the funcid
	 */
	public String getFuncid() {
		return funcid;
	}

	/**
	 * @return the id
	 */
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the roleid
	 */
	public String getRoleid() {
		return roleid;
	}

	/**
	 * @param funcid
	 *            the funcid to set
	 */
	public void setFuncid(String funcid) {
		this.funcid = funcid;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param roleid
	 *            the roleid to set
	 */
	public void setRoleid(String roleid) {
		this.roleid = roleid;
	}

}
