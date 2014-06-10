package com.afunms.detail.reomte.model;

public class FibreCapabilityInfo {

	/**
	 * 索引
	 */
	private String sindex;

	/**
	 * 光口名称
	 */
	private String portName;

	/**
	 * 当前状态
	 */
	private String portPhysOperStatus;

	/**
	 * 入口帧数
	 */
	private String portC3InFrames;

	/**
	 * 出口帧数
	 */
	private String portC3OutFrames;

	/**
	 * 入口帧字节数
	 */
	private String portC3InOctets;

	/**
	 * 出口帧字节数
	 */
	private String portC3OutOctets;

	/**
	 * 丢包数
	 */
	private String portC3Discards;

	/**
	 * @return the portC3Discards
	 */
	public String getPortC3Discards() {
		return portC3Discards;
	}

	/**
	 * @return the portC3InFrames
	 */
	public String getPortC3InFrames() {
		return portC3InFrames;
	}

	/**
	 * @return the portC3InOctets
	 */
	public String getPortC3InOctets() {
		return portC3InOctets;
	}

	/**
	 * @return the portC3OutFrames
	 */
	public String getPortC3OutFrames() {
		return portC3OutFrames;
	}

	/**
	 * @return the portC3OutOctets
	 */
	public String getPortC3OutOctets() {
		return portC3OutOctets;
	}

	/**
	 * @return the portName
	 */
	public String getPortName() {
		return portName;
	}

	/**
	 * @return the portPhysOperStatus
	 */
	public String getPortPhysOperStatus() {
		return portPhysOperStatus;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @param portC3Discards
	 *            the portC3Discards to set
	 */
	public void setPortC3Discards(String portC3Discards) {
		this.portC3Discards = portC3Discards;
	}

	/**
	 * @param portC3InFrames
	 *            the portC3InFrames to set
	 */
	public void setPortC3InFrames(String portC3InFrames) {
		this.portC3InFrames = portC3InFrames;
	}

	/**
	 * @param portC3InOctets
	 *            the portC3InOctets to set
	 */
	public void setPortC3InOctets(String portC3InOctets) {
		this.portC3InOctets = portC3InOctets;
	}

	/**
	 * @param portC3OutFrames
	 *            the portC3OutFrames to set
	 */
	public void setPortC3OutFrames(String portC3OutFrames) {
		this.portC3OutFrames = portC3OutFrames;
	}

	/**
	 * @param portC3OutOctets
	 *            the portC3OutOctets to set
	 */
	public void setPortC3OutOctets(String portC3OutOctets) {
		this.portC3OutOctets = portC3OutOctets;
	}

	/**
	 * @param portName
	 *            the portName to set
	 */
	public void setPortName(String portName) {
		this.portName = portName;
	}

	/**
	 * @param portPhysOperStatus
	 *            the portPhysOperStatus to set
	 */
	public void setPortPhysOperStatus(String portPhysOperStatus) {
		this.portPhysOperStatus = portPhysOperStatus;
	}

	/**
	 * @param sindex
	 *            the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

}
