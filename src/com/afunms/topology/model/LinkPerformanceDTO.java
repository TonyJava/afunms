package com.afunms.topology.model;

import com.afunms.common.base.BaseVo;

public class LinkPerformanceDTO extends BaseVo {

	private int id;

	private String name;

	private String startNode;

	private String stratIndex;

	private String endNode;

	private String endIndex;

	private String downlinkSpeed;

	private String uplinkSpeed;

	private String downlinkSpeedColor;

	private String uplinkSpeedColor;

	private String pingValue;

	private String allSpeedRate;

	private String arisName;

	/**
	 * @return the allSpeedRate
	 */
	public String getAllSpeedRate() {
		return allSpeedRate;
	}

	public String getArisName() {
		return arisName;
	}

	/**
	 * @return the downlinkSpeed
	 */
	public String getDownlinkSpeed() {
		return downlinkSpeed;
	}

	/**
	 * @return the downlinkSpeedColor
	 */
	public String getDownlinkSpeedColor() {
		return downlinkSpeedColor;
	}

	/**
	 * @return the endIndex
	 */
	public String getEndIndex() {
		return endIndex;
	}

	/**
	 * @return the endNode
	 */
	public String getEndNode() {
		return endNode;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the pingValue
	 */
	public String getPingValue() {
		return pingValue;
	}

	/**
	 * @return the startNode
	 */
	public String getStartNode() {
		return startNode;
	}

	/**
	 * @return the stratIndex
	 */
	public String getStratIndex() {
		return stratIndex;
	}

	/**
	 * @return the uplinkSpeed
	 */
	public String getUplinkSpeed() {
		return uplinkSpeed;
	}

	/**
	 * @return the uplinkSpeedColor
	 */
	public String getUplinkSpeedColor() {
		return uplinkSpeedColor;
	}

	/**
	 * @param allSpeedRate
	 *            the allSpeedRate to set
	 */
	public void setAllSpeedRate(String allSpeedRate) {
		this.allSpeedRate = allSpeedRate;
	}

	public void setArisName(String arisName) {
		this.arisName = arisName;
	}

	/**
	 * @param downlinkSpeed
	 *            the downlinkSpeed to set
	 */
	public void setDownlinkSpeed(String downlinkSpeed) {
		this.downlinkSpeed = downlinkSpeed;
	}

	/**
	 * @param downlinkSpeedColor
	 *            the downlinkSpeedColor to set
	 */
	public void setDownlinkSpeedColor(String downlinkSpeedColor) {
		this.downlinkSpeedColor = downlinkSpeedColor;
	}

	/**
	 * @param endIndex
	 *            the endIndex to set
	 */
	public void setEndIndex(String endIndex) {
		this.endIndex = endIndex;
	}

	/**
	 * @param endNode
	 *            the endNode to set
	 */
	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param pingValue
	 *            the pingValue to set
	 */
	public void setPingValue(String pingValue) {
		this.pingValue = pingValue;
	}

	/**
	 * @param startNode
	 *            the startNode to set
	 */
	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

	/**
	 * @param stratIndex
	 *            the stratIndex to set
	 */
	public void setStratIndex(String stratIndex) {
		this.stratIndex = stratIndex;
	}

	/**
	 * @param uplinkSpeed
	 *            the uplinkSpeed to set
	 */
	public void setUplinkSpeed(String uplinkSpeed) {
		this.uplinkSpeed = uplinkSpeed;
	}

	/**
	 * @param uplinkSpeedColor
	 *            the uplinkSpeedColor to set
	 */
	public void setUplinkSpeedColor(String uplinkSpeedColor) {
		this.uplinkSpeedColor = uplinkSpeedColor;
	}
}
