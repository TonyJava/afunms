package com.afunms.detail.reomte.model;

public class UserConfigInfo {

	/**
	 * 索引
	 */
	private String sindex;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 用户组
	 */
	private String userGroup;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @return the userGroup
	 */
	public String getUserGroup() {
		return userGroup;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param sindex
	 *            the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	/**
	 * @param userGroup
	 *            the userGroup to set
	 */
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

}
