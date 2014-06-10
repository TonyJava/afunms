package com.afunms.detail.reomte.model;

public class DiskPerfInfo {

	/**
	 * ��������
	 */
	private String disklebel;

	/**
	 * ��æ(%)
	 */
	private String busyRate;

	/**
	 * ƽ�����
	 */
	private String avque;

	/**
	 * ��д����/��
	 */
	private String readAndWriteBlockPerSecond;

	/**
	 * ��д�ֽڣ�K��/��
	 */
	private String readAndWriteBytePerSecond;

	/**
	 * ƽ���ȴ�ʱ��(ms)
	 */
	private String avwait;

	/**
	 * ƽ��ִ��ʱ��(ms)
	 */
	private String avserv;

	/**
	 * @return the avque
	 */
	public String getAvque() {
		return avque;
	}

	/**
	 * @return the avserv
	 */
	public String getAvserv() {
		return avserv;
	}

	/**
	 * @return the avwait
	 */
	public String getAvwait() {
		return avwait;
	}

	/**
	 * @return the busyRate
	 */
	public String getBusyRate() {
		return busyRate;
	}

	/**
	 * @return the disklebel
	 */
	public String getDisklebel() {
		return disklebel;
	}

	/**
	 * @return the readAndWriteBlockPerSecond
	 */
	public String getReadAndWriteBlockPerSecond() {
		return readAndWriteBlockPerSecond;
	}

	/**
	 * @return the readAndWriteBytePerSecond
	 */
	public String getReadAndWriteBytePerSecond() {
		return readAndWriteBytePerSecond;
	}

	/**
	 * @param avque
	 *            the avque to set
	 */
	public void setAvque(String avque) {
		this.avque = avque;
	}

	/**
	 * @param avserv
	 *            the avserv to set
	 */
	public void setAvserv(String avserv) {
		this.avserv = avserv;
	}

	/**
	 * @param avwait
	 *            the avwait to set
	 */
	public void setAvwait(String avwait) {
		this.avwait = avwait;
	}

	/**
	 * @param busyRate
	 *            the busyRate to set
	 */
	public void setBusyRate(String busyRate) {
		this.busyRate = busyRate;
	}

	/**
	 * @param disklebel
	 *            the disklebel to set
	 */
	public void setDisklebel(String disklebel) {
		this.disklebel = disklebel;
	}

	/**
	 * @param readAndWriteBlockPerSecond
	 *            the readAndWriteBlockPerSecond to set
	 */
	public void setReadAndWriteBlockPerSecond(String readAndWriteBlockPerSecond) {
		this.readAndWriteBlockPerSecond = readAndWriteBlockPerSecond;
	}

	/**
	 * @param readAndWriteBytePerSecond
	 *            the readAndWriteBytePerSecond to set
	 */
	public void setReadAndWriteBytePerSecond(String readAndWriteBytePerSecond) {
		this.readAndWriteBytePerSecond = readAndWriteBytePerSecond;
	}

}
