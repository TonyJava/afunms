package com.afunms.config.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/** @author Hibernate CodeGenerator */
public class PanelModel extends BaseVo implements Serializable {
	private Integer id;
	/** nullable persistent field */
	private String oid;

	/** nullable persistent field */
	private String width;

	/** identifier field */
	private String height;

	/**
	 * identifier field This attribute is used to identify the same oid
	 * networking, but different panels pictures
	 * 此属性用于标识具有相同oid的网络设备,其使用不同的面板模板图片
	 * 
	 * @author nielin add at 2010-01-07
	 */
	private String imageType;

	/** default constructor */
	public PanelModel() {
	}

	/** full constructor */
	public PanelModel(Integer id, String oid, String imageType, String width, String height) {
		this.id = id;
		this.oid = oid;
		this.imageType = imageType;
		this.width = width;
		this.height = height;
	}

	/**
	 * @return
	 */
	public String getHeight() {
		return height;
	}

	/**
	 * @return
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @return the imageType
	 */
	public String getImageType() {
		return imageType;
	}

	/**
	 * @return
	 */
	public String getOid() {
		return this.oid;
	}

	/**
	 * @return
	 */
	public String getWidth() {
		return this.width;
	}

	/**
	 * @param string
	 */
	public void setHeight(String height) {
		this.height = height;
	}

	/**
	 * @param string
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @param imageType
	 *            the imageType to set
	 */
	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	/**
	 * @param serializable
	 */
	public void setOid(String oid) {
		this.oid = oid;
	}

	/**
	 * @param calendar
	 */
	public void setWidth(String width) {
		this.width = width;
	}

}
