package com.afunms.detail.reomte.model;

public class MemoryConfigInfo {

	/**
	 * 索引
	 */
	private String sindex;

	/**
	 * 类型
	 */
	private String type;

	/**
	 * 大小
	 */
	private String size;

	/**
	 * 大小的单位
	 */
	private String unit;

	/**
	 * 中文描述
	 */
	private String descr_cn;

	/**
	 * @return the descr_cn
	 */
	public String getDescr_cn() {
		return descr_cn;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param descr_cn
	 *            the descr_cn to set
	 */
	public void setDescr_cn(String descr_cn) {
		this.descr_cn = descr_cn;
	}

	/**
	 * @param sindex
	 *            the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
