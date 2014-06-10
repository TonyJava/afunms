package com.afunms.cabinet.model;

import com.afunms.common.base.BaseVo;

public class MachineCabinet extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private int id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 机柜X位置
	 */
	private String machinex;
	/**
	 * 机柜Y位置
	 */
	private String machiney;
	/**
	 * 机柜Z位置
	 */
	private String machinez;
	/**
	 * U
	 */
	private String uselect;
	/**
	 * 机房
	 */
	private String motorroom;
	/**
	 * 机架规格
	 */
	private String standards;
	/**
	 * 电源个数
	 */
	private String powers;
	/**
	 * 高度
	 */
	private String heights;
	/**
	 * 宽度
	 */
	private String widths;
	/**
	 * 深度
	 */
	private String depths;
	/**
	 * 机柜编号
	 */
	private String nos;

	private String icon = "../resource/image/menu/jgpz.gif";

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

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

	public String getMachinex() {
		return machinex;
	}

	public void setMachinex(String machinex) {
		this.machinex = machinex;
	}

	public String getMachiney() {
		return machiney;
	}

	public void setMachiney(String machiney) {
		this.machiney = machiney;
	}

	public String getMachinez() {
		return machinez;
	}

	public void setMachinez(String machinez) {
		this.machinez = machinez;
	}

	public String getUselect() {
		return uselect;
	}

	public void setUselect(String uselect) {
		this.uselect = uselect;
	}

	public String getMotorroom() {
		return motorroom;
	}

	public void setMotorroom(String motorroom) {
		this.motorroom = motorroom;
	}

	public String getStandards() {
		return standards;
	}

	public void setStandards(String standards) {
		this.standards = standards;
	}

	public String getPowers() {
		return powers;
	}

	public void setPowers(String powers) {
		this.powers = powers;
	}

	public String getHeights() {
		return heights;
	}

	public void setHeights(String heights) {
		this.heights = heights;
	}

	public String getWidths() {
		return widths;
	}

	public void setWidths(String widths) {
		this.widths = widths;
	}

	public String getDepths() {
		return depths;
	}

	public void setDepths(String depths) {
		this.depths = depths;
	}

	public String getNos() {
		return nos;
	}

	public void setNos(String nos) {
		this.nos = nos;
	}

}
