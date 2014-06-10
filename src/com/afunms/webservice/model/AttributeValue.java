package com.afunms.webservice.model;

import java.io.Serializable;

/**
 * @description 属性值
 * @author wangxiangyong
 * @date Jan 17, 2012 5:40:40 PM
 */
public class AttributeValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8389771338283409172L;
	private String moid;
	private String attributeName;// 属性名
	private String value;// 属性值

	public String getMoid() {
		return moid;
	}

	public void setMoid(String moid) {
		this.moid = moid;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
