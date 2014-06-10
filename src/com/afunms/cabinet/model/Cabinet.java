package com.afunms.cabinet.model;

import com.afunms.common.base.BaseVo;

public class Cabinet extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private int id;
	/**
	 * 机柜名称
	 */
	private String name;
	/**
	 * 总U数
	 */
	private String allu;
	/**
	 * 已用u数
	 */
	private String useu;
	/**
	 * 空闲U数
	 */
	private String tempu;
	/**
	 * U使用率
	 */
	private String rateu;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAllu() {
		return allu;
	}

	public void setAllu(String allu) {
		this.allu = allu;
	}

	public String getUseu() {
		return useu;
	}

	public void setUseu(String useu) {
		this.useu = useu;
	}

	public String getTempu() {
		return tempu;
	}

	public void setTempu(String tempu) {
		this.tempu = tempu;
	}

	public String getRateu() {
		return rateu;
	}

	public void setRateu(String rateu) {
		this.rateu = rateu;
	}

}
