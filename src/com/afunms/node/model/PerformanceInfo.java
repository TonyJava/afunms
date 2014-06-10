package com.afunms.node.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/**
 * ClassName: PerformanceInfo.java
 * <p>
 * {@link PerformanceInfo} 性能信息
 * 
 * @author 聂林
 * @version v1.01
 * @since v1.01
 * @Date Jan 11, 2013 3:35:12 PM
 */
public class PerformanceInfo extends BaseVo implements Serializable {

	/**
	 * serialVersionUID:
	 * <p>
	 * 序列化 ID
	 * 
	 * @since v1.01
	 */
	private static final long serialVersionUID = -278893407699066678L;

	/**
	 * id:
	 * <p>
	 * id
	 * 
	 * @since v1.01
	 */
	private int id;

	/**
	 * ipAddress:
	 * <p>
	 * ip 地址
	 * 
	 * @since v1.01
	 */
	private String ipAddress;

	/**
	 * restype:
	 * <p>
	 * 数据状态
	 * 
	 * @since v1.01
	 */
	private String restype;

	/**
	 * category:
	 * <p>
	 * 
	 * @since v1.01
	 */
	private String category;

	/**
	 * entity:
	 * <p>
	 * 实体类别
	 * 
	 * @since v1.01
	 */
	private String entity;

	/**
	 * subentity:
	 * <p>
	 * 子实体类别
	 * 
	 * @since v1.01
	 */
	private String subentity;

	/**
	 * thevalue:
	 * <p>
	 * 值
	 * 
	 * @since v1.01
	 */
	private String thevalue;

	/**
	 * collecttime:
	 * <p>
	 * 采集时间
	 * 
	 * @since v1.01
	 */
	private String collecttime;

	/**
	 * unit:
	 * <p>
	 * 单位
	 * 
	 * @since v1.01
	 */
	private String unit;

	/**
	 * count:
	 * <p>
	 * 次数
	 * 
	 * @since v1.01
	 */
	private String count;

	/**
	 * bak:
	 * <p>
	 * 备注
	 * 
	 * @since v1.01
	 */
	private String bak;

	/**
	 * chname:
	 * <p>
	 * 中文
	 * 
	 * @since v1.01
	 */
	private String chname;

	/**
	 * getBak:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getBak() {
		return bak;
	}

	/**
	 * getCategory:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * getChname:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getChname() {
		return chname;
	}

	/**
	 * getCollecttime:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getCollecttime() {
		return collecttime;
	}

	/**
	 * getCount:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getCount() {
		return count;
	}

	/**
	 * getEntity:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * getId:
	 * <p>
	 * 
	 * @return int -
	 * @since v1.01
	 */
	public int getId() {
		return id;
	}

	/**
	 * getIpAddress:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * getRestype:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getRestype() {
		return restype;
	}

	/**
	 * getSubentity:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getSubentity() {
		return subentity;
	}

	/**
	 * getThevalue:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getThevalue() {
		return thevalue;
	}

	/**
	 * getUnit:
	 * <p>
	 * 
	 * @return String -
	 * @since v1.01
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * setBak:
	 * <p>
	 * 
	 * @param bak -
	 * @since v1.01
	 */
	public void setBak(String bak) {
		this.bak = bak;
	}

	/**
	 * setCategory:
	 * <p>
	 * 
	 * @param category -
	 * @since v1.01
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * setChname:
	 * <p>
	 * 
	 * @param chname -
	 * @since v1.01
	 */
	public void setChname(String chname) {
		this.chname = chname;
	}

	/**
	 * setCollecttime:
	 * <p>
	 * 
	 * @param collecttime -
	 * @since v1.01
	 */
	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	/**
	 * setCount:
	 * <p>
	 * 
	 * @param count -
	 * @since v1.01
	 */
	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * setEntity:
	 * <p>
	 * 
	 * @param entity -
	 * @since v1.01
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * setId:
	 * <p>
	 * 
	 * @param id -
	 * @since v1.01
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * setIpAddress:
	 * <p>
	 * 
	 * @param ipAddress -
	 * @since v1.01
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * setRestype:
	 * <p>
	 * 
	 * @param restype -
	 * @since v1.01
	 */
	public void setRestype(String restype) {
		this.restype = restype;
	}

	/**
	 * setSubentity:
	 * <p>
	 * 
	 * @param subentity -
	 * @since v1.01
	 */
	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}

	/**
	 * setThevalue:
	 * <p>
	 * 
	 * @param thevalue -
	 * @since v1.01
	 */
	public void setThevalue(String thevalue) {
		this.thevalue = thevalue;
	}

	/**
	 * setUnit:
	 * <p>
	 * 
	 * @param unit -
	 * @since v1.01
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
