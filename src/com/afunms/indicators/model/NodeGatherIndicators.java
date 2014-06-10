package com.afunms.indicators.model;

import com.afunms.common.base.BaseVo;

/**
 * ����Ϊ��׼���ܼ��ָ��
 * 
 * @author Administrator
 * 
 */

public class NodeGatherIndicators extends BaseVo {

	private int id;

	private String nodeid; // �豸id

	private String name; // ָ������

	private String type; // ��������

	private String subtype; // ����������

	private String alias; // ����

	private String description; // ����

	private String category; // ��������

	private String isDefault; // �Ƿ�����Ĭ��Ӧ��

	private String isCollection; // �Ƿ�ɼ�

	private String poll_interval; // �ɼ����

	private String interval_unit; // �ɼ������λ

	private String classpath; // ָ��Ĳɼ����·�� ��cpu ->
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
