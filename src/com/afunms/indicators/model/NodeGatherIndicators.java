package com.afunms.indicators.model;

import com.afunms.common.base.BaseVo;

/**
 * 此类为基准性能监控指标
 * 
 * @author Administrator
 * 
 */

public class NodeGatherIndicators extends BaseVo {

	private int id;

	private String nodeid; // 设备id

	private String name; // 指标名称

	private String type; // 所属类型

	private String subtype; // 所属子类型

	private String alias; // 别名

	private String description; // 描述

	private String category; // 所属种类

	private String isDefault; // 是否用于默认应用

	private String isCollection; // 是否采集

	private String poll_interval; // 采集间隔

	private String interval_unit; // 采集间隔单位

	private String classpath; // 指标的采集类的路径 如cpu ->
										// com.afunms.polling.snmp.cpu.CiscoCpuSnmp

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	public String getClasspath() {
		return classpath;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the interval_unit
	 */
	public String getInterval_unit() {
		return interval_unit;
	}

	/**
	 * @return the isCollection
	 */
	public String getIsCollection() {
		return isCollection;
	}

	/**
	 * @return the isDefault
	 */
	public String getIsDefault() {
		return isDefault;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the nodeid
	 */
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * @return the poll_interval
	 */
	public String getPoll_interval() {
		return poll_interval;
	}

	/**
	 * @return the subtype
	 */
	public String getSubtype() {
		return subtype;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param interval_unit
	 *            the interval_unit to set
	 */
	public void setInterval_unit(String interval_unit) {
		this.interval_unit = interval_unit;
	}

	/**
	 * @param isCollection
	 *            the isCollection to set
	 */
	public void setIsCollection(String isCollection) {
		this.isCollection = isCollection;
	}

	/**
	 * @param isDefault
	 *            the isDefault to set
	 */
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param nodeid
	 *            the nodeid to set
	 */
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * @param poll_interval
	 *            the poll_interval to set
	 */
	public void setPoll_interval(String poll_interval) {
		this.poll_interval = poll_interval;
	}

	/**
	 * @param subtype
	 *            the subtype to set
	 */
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
