package com.afunms.detail.reomte.model;

public class InterfaceInfo {

	/**
	 * ����
	 */
	private String sindex;

	/**
	 * ����
	 */
	private String ifDescr;

	/**
	 * ����Ӧ��
	 */
	private String LinkUse;

	/**
	 * ÿ���ֽ���(M)
	 */
	private String ifSpeed;

	/**
	 * ״̬
	 */
	private String ifOperStatus;

	/**
	 * ���ڹ㲥���ݰ�
	 */
	private String ifOutBroadcastPkts;

	/**
	 * ��ڹ㲥���ݰ�
	 */
	private String ifInBroadcastPkts;

	/**
	 * ���ڶಥ���ݰ�
	 */
	private String ifOutMulticastPkts;

	/**
	 * ��ڶಥ���ݰ�
	 */
	private String ifInMulticastPkts;
	/**
	 * ��������
	 */
	private String outBandwidthUtilHdx;

	/**
	 * �������
	 */
	private String inBandwidthUtilHdx;

	// ######################HONG ADD
	/**
	 * �˿�������
	 */
	private String allBandwidthUtilHdx;

	/**
	 * ����
	 */
	private String ifType;

	/**
	 * ������ݰ�
	 */
	private String ifMtu;

	/**
	 * Ԥ��״̬
	 */
	private String ifAdminStatus;

	/**
	 * 86�˿���ڴ���������
	 */
	private String InBandwidthUtilHdxPerc;

	/**
	 * 86�˿ڳ��ڴ���������
	 */
	private String OutBandwidthUtilHdxPerc;

	public String getAllBandwidthUtilHdx() {
		return allBandwidthUtilHdx;
	}

	public String getIfAdminStatus() {
		return ifAdminStatus;
	}

	/**
	 * @return the ifDescr
	 */
	public String getIfDescr() {
		return ifDescr;
	}

	/**
	 * @return the ifInBroadcastPkts
	 */
	public String getIfInBroadcastPkts() {
		return ifInBroadcastPkts;
	}

	/**
	 * @return the ifInMulticastPkts
	 */
	public String getIfInMulticastPkts() {
		return ifInMulticastPkts;
	}

	public String getIfMtu() {
		return ifMtu;
	}

	/**
	 * @return the ifOperStatus
	 */
	public String getIfOperStatus() {
		return ifOperStatus;
	}

	/**
	 * @return the ifOutBroadcastPkts
	 */
	public String getIfOutBroadcastPkts() {
		return ifOutBroadcastPkts;
	}

	/**
	 * @return the ifOutMulticastPkts
	 */
	public String getIfOutMulticastPkts() {
		return ifOutMulticastPkts;
	}

	/**
	 * @return the ifSpeed
	 */
	public String getIfSpeed() {
		return ifSpeed;
	}

	public String getIfType() {
		return ifType;
	}

	/**
	 * @return the inBandwidthUtilHdx
	 */
	public String getInBandwidthUtilHdx() {
		return inBandwidthUtilHdx;
	}

	public String getInBandwidthUtilHdxPerc() {
		return InBandwidthUtilHdxPerc;
	}

	/**
	 * @return the linkUse
	 */
	public String getLinkUse() {
		return LinkUse;
	}

	/**
	 * @return the outBandwidthUtilHdx
	 */
	public String getOutBandwidthUtilHdx() {
		return outBandwidthUtilHdx;
	}

	public String getOutBandwidthUtilHdxPerc() {
		return OutBandwidthUtilHdxPerc;
	}

	/**
	 * @return the sindex
	 */
	public String getSindex() {
		return sindex;
	}

	public void setAllBandwidthUtilHdx(String allBandwidthUtilHdx) {
		this.allBandwidthUtilHdx = allBandwidthUtilHdx;
	}

	public void setIfAdminStatus(String ifAdminStatus) {
		this.ifAdminStatus = ifAdminStatus;
	}

	/**
	 * @param ifDescr
	 *            the ifDescr to set
	 */
	public void setIfDescr(String ifDescr) {
		this.ifDescr = ifDescr;
	}

	/**
	 * @param ifInBroadcastPkts
	 *            the ifInBroadcastPkts to set
	 */
	public void setIfInBroadcastPkts(String ifInBroadcastPkts) {
		this.ifInBroadcastPkts = ifInBroadcastPkts;
	}

	/**
	 * @param ifInMulticastPkts
	 *            the ifInMulticastPkts to set
	 */
	public void setIfInMulticastPkts(String ifInMulticastPkts) {
		this.ifInMulticastPkts = ifInMulticastPkts;
	}

	public void setIfMtu(String ifMtu) {
		this.ifMtu = ifMtu;
	}

	/**
	 * @param ifOperStatus
	 *            the ifOperStatus to set
	 */
	public void setIfOperStatus(String ifOperStatus) {
		this.ifOperStatus = ifOperStatus;
	}

	/**
	 * @param ifOutBroadcastPkts
	 *            the ifOutBroadcastPkts to set
	 */
	public void setIfOutBroadcastPkts(String ifOutBroadcastPkts) {
		this.ifOutBroadcastPkts = ifOutBroadcastPkts;
	}

	/**
	 * @param ifOutMulticastPkts
	 *            the ifOutMulticastPkts to set
	 */
	public void setIfOutMulticastPkts(String ifOutMulticastPkts) {
		this.ifOutMulticastPkts = ifOutMulticastPkts;
	}

	/**
	 * @param ifSpeed
	 *            the ifSpeed to set
	 */
	public void setIfSpeed(String ifSpeed) {
		this.ifSpeed = ifSpeed;
	}

	public void setIfType(String ifType) {
		this.ifType = ifType;
	}

	/**
	 * @param inBandwidthUtilHdx
	 *            the inBandwidthUtilHdx to set
	 */
	public void setInBandwidthUtilHdx(String inBandwidthUtilHdx) {
		this.inBandwidthUtilHdx = inBandwidthUtilHdx;
	}

	public void setInBandwidthUtilHdxPerc(String inBandwidthUtilHdxPerc) {
		InBandwidthUtilHdxPerc = inBandwidthUtilHdxPerc;
	}

	/**
	 * @param linkUse
	 *            the linkUse to set
	 */
	public void setLinkUse(String linkUse) {
		LinkUse = linkUse;
	}

	/**
	 * @param outBandwidthUtilHdx
	 *            the outBandwidthUtilHdx to set
	 */
	public void setOutBandwidthUtilHdx(String outBandwidthUtilHdx) {
		this.outBandwidthUtilHdx = outBandwidthUtilHdx;
	}

	public void setOutBandwidthUtilHdxPerc(String outBandwidthUtilHdxPerc) {
		OutBandwidthUtilHdxPerc = outBandwidthUtilHdxPerc;
	}

	/**
	 * @param sindex
	 *            the sindex to set
	 */
	public void setSindex(String sindex) {
		this.sindex = sindex;
	}
}
