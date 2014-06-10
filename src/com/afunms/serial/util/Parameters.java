package com.afunms.serial.util;

public class Parameters {

	/**
	 * the serialPortId of the serial to be used. COM1, COM2, etc.
	 */
	private String serialPortId;

	/**
	 * the number of Baud Rate(For Exemaple: 9600)
	 */
	private int baudRate;

	/**
	 * the number of data bit
	 */
	private int databits;

	/**
	 * the number of STOP bits
	 */
	private int stopbits;

	/**
	 * the number of Parity
	 */
	private int parity;

	/**
	 * @return the baudRate
	 */
	public int getBaudRate() {
		return baudRate;
	}

	/**
	 * @return the databits
	 */
	public int getDatabits() {
		return databits;
	}

	/**
	 * @return the parity
	 */
	public int getParity() {
		return parity;
	}

	/**
	 * @return the serialPortId
	 */
	public String getSerialPortId() {
		return serialPortId;
	}

	/**
	 * @return the stopbits
	 */
	public int getStopbits() {
		return stopbits;
	}

	/**
	 * @param baudRate
	 *            the baudRate to set
	 */
	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	/**
	 * @param databits
	 *            the databits to set
	 */
	public void setDatabits(int databits) {
		this.databits = databits;
	}

	/**
	 * @param parity
	 *            the parity to set
	 */
	public void setParity(int parity) {
		this.parity = parity;
	}

	/**
	 * @param serialPortId
	 *            the serialPortId to set
	 */
	public void setSerialPortId(String serialPortId) {
		this.serialPortId = serialPortId;
	}

	/**
	 * @param stopbits
	 *            the stopbits to set
	 */
	public void setStopbits(int stopbits) {
		this.stopbits = stopbits;
	}

}
