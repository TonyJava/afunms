package com.afunms.detail.reomte.model;

public class CpuPerfInfo {

	/**
	 * 索引
	 */
	private String sindex;

	/**
	 * %用户
	 */
	private String user;

	/**
	 * %系统
	 */
	private String sysRate;

	/**
	 * %io等待
	 */
	private String wioRate;

	/**
	 * %空闲
	 */
	private String idleRate;

	/**
	 * 物理
	 */
	private String physc;

	/**
	 * @return the idleRate
	 */
	public String getIdleRate() {
		return idleRate;
	}

	/**
	 * @return the physc
	 */
	public String getPhysc() {
		return physc;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @return the sysRate
	 */
	public String getSysRate() {
		return sysRate;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the wioRate
	 */
	public String getWioRate() {
		return wioRate;
	}

	/**
	 * @param idleRate
	 *            the idleRate to set
	 */
	public void setIdleRate(String idleRate) {
		this.idleRate = idleRate;
	}

	/**
	 * @param physc
	 *            the physc to set
	 */
	public void setPhysc(String physc) {
		this.physc = physc;
	}

	/**
	 * @param sindex
	 *            the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	/**
	 * @param sysRate
	 *            the sysRate to set
	 */
	public void setSysRate(String sysRate) {
		this.sysRate = sysRate;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @param wioRate
	 *            the wioRate to set
	 */
	public void setWioRate(String wioRate) {
		this.wioRate = wioRate;
	}

}
