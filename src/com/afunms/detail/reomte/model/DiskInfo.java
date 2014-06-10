package com.afunms.detail.reomte.model;

public class DiskInfo {

	/**
	 * ��������(����)
	 */
	private String sindex;

	/**
	 * �ܴ�С(����)
	 */
	private String allSize;

	/**
	 * ��ʹ�ô�С
	 */
	private String usedSize;

	/**
	 * ������
	 */
	private String utilization;

	/**
	 * ������
	 */
	private String utilizationInc;

	/**
	 * ��ʹ�ô�С�ĵ�λ G , M ,....
	 */
	private String allSizeUnit;

	/**
	 * �ܴ�С(����)�ĵ�λ G , M ,....
	 */
	private String usedSizeUnit;

	/**
	 * �����ʵĵ�λ %
	 */
	private String utilizationUnit;

	/**
	 * @return the allSize
	 */
	public String getAllSize() {
		return allSize;
	}

	/**
	 * @return the allSizeUnit
	 */
	public String getAllSizeUnit() {
		return allSizeUnit;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	/**
	 * @return the usedSize
	 */
	public String getUsedSize() {
		return usedSize;
	}

	/**
	 * @return the usedSizeUnit
	 */
	public String getUsedSizeUnit() {
		return usedSizeUnit;
	}

	/**
	 * @return the utilization
	 */
	public String getUtilization() {
		return utilization;
	}

	/**
	 * @return the utilizationInc
	 */
	public String getUtilizationInc() {
		return utilizationInc;
	}

	/**
	 * @return the utilizationUnit
	 */
	public String getUtilizationUnit() {
		return utilizationUnit;
	}

	/**
	 * @param allSize
	 *            the allSize to set
	 */
	public void setAllSize(String allSize) {
		this.allSize = allSize;
	}

	/**
	 * @param allSizeUnit
	 *            the allSizeUnit to set
	 */
	public void setAllSizeUnit(String allSizeUnit) {
		this.allSizeUnit = allSizeUnit;
	}

	/**
	 * @param sindex
	 *            the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}

	/**
	 * @param usedSize
	 *            the usedSize to set
	 */
	public void setUsedSize(String usedSize) {
		this.usedSize = usedSize;
	}

	/**
	 * @param usedSizeUnit
	 *            the usedSizeUnit to set
	 */
	public void setUsedSizeUnit(String usedSizeUnit) {
		this.usedSizeUnit = usedSizeUnit;
	}

	/**
	 * @param utilization
	 *            the utilization to set
	 */
	public void setUtilization(String utilization) {
		this.utilization = utilization;
	}

	/**
	 * @param utilizationInc
	 *            the utilizationInc to set
	 */
	public void setUtilizationInc(String utilizationInc) {
		this.utilizationInc = utilizationInc;
	}

	/**
	 * @param utilizationUnit
	 *            the utilizationUnit to set
	 */
	public void setUtilizationUnit(String utilizationUnit) {
		this.utilizationUnit = utilizationUnit;
	}

}
