package com.afunms.detail.reomte.model;

public class PagePerfInfo {

	/**
	 * 索引
	 */
	private String sindex;

	/**
	 * 页面调度程序输入/输出列表
	 */
	private String re;

	/**
	 * 内存页面调进数
	 */
	private String pi;

	/**
	 * 内存页面调出数
	 */
	private String po;

	/**
	 * 释放的页数
	 */
	private String fr;

	/**
	 * 扫描的页
	 */
	private String sr;

	/**
	 * 时钟周期
	 */
	private String cy;

	/**
	 * @return the cy
	 */
	public String getCy() {
		return cy;
	}

	/**
	 * @return the fr
	 */
	public String getFr() {
		return fr;
	}

	/**
	 * @return the pi
	 */
	public String getPi() {
		return pi;
	}

	/**
	 * @return the po
	 */
	public String getPo() {
		return po;
	}

	/**
	 * @return the re
	 */
	public String getRe() {
		return re;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @return the sr
	 */
	public String getSr() {
		return sr;
	}

	/**
	 * @param cy
	 *            the cy to set
	 */
	public void setCy(String cy) {
		this.cy = cy;
	}

	/**
	 * @param fr
	 *            the fr to set
	 */
	public void setFr(String fr) {
		this.fr = fr;
	}

	/**
	 * @param pi
	 *            the pi to set
	 */
	public void setPi(String pi) {
		this.pi = pi;
	}

	/**
	 * @param po
	 *            the po to set
	 */
	public void setPo(String po) {
		this.po = po;
	}

	/**
	 * @param re
	 *            the re to set
	 */
	public void setRe(String re) {
		this.re = re;
	}

	/**
	 * @param sindex
	 *            the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	/**
	 * @param sr
	 *            the sr to set
	 */
	public void setSr(String sr) {
		this.sr = sr;
	}

}
