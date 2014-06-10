package com.afunms.webservice.model;

import java.io.Serializable;

/**
 * @description  Ù–‘
 * @author wangxiangyong
 * @date Jan 17, 2012 5:41:10 PM
 */
public class Attribute implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5599343918374939043L;
	private String moId;
	private String attributeName;

	public String getMoId() {
		return moId;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

}
