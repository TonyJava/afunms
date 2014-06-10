package com.afunms.polling.node;

/**
 * 
 * @descrition ¸½¼þ
 * @author wangxiangyong
 * @date Jun 16, 2013 4:46:51 PM
 */
public class SshEnclosure {
	private String type;
	private String number;
	private String status;
	private String fruPN;
	private String fruSN;
	private String addData;

	public String getAddData() {
		return addData;
	}

	public String getFruPN() {
		return fruPN;
	}

	public String getFruSN() {
		return fruSN;
	}

	public String getNumber() {
		return number;
	}

	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public void setAddData(String addData) {
		this.addData = addData;
	}

	public void setFruPN(String fruPN) {
		this.fruPN = fruPN;
	}

	public void setFruSN(String fruSN) {
		this.fruSN = fruSN;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

}
