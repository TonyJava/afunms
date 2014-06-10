package com.afunms.webservice.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description …¡¥Ê–≈œ¢
 * @author wangxiangyong
 * @date Jan 14, 2012 3:44:46 PM
 */
@XmlRootElement(name = "flash")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceFlashInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5991089881137104914L;
	private String flashName;//…¡¥Ê√˚≥∆

	public String getFlashName() {
		return flashName;
	}

	public void setFlashName(String flashName) {
		this.flashName = flashName;
	}

}
