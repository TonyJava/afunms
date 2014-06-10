package com.afunms.node.model;

import java.io.Serializable;

import com.afunms.common.base.BaseVo;

/**
 * ClassName: Column.java
 * <p>
 * {@link Column} �豸�����б��е���
 * 
 * @author ����
 * @version v1.01
 * @since v1.01
 * @Date Dec 21, 2012 6:37:02 PM
 */
public class Column extends BaseVo implements Serializable {

	/**
	 * serialVersionUID:
	 * <p>
	 * ���л� Id
	 * 
	 * @since v1.01
	 */
	private static final long serialVersionUID = -6505868871572525679L;

	/**
	 * id:
	 * <p>
	 * id
	 * 
	 * @since v1.01
	 */
	private int id;

	/**
	 * name:
	 * <p>
	 * ����
	 * 
	 * @since v1.01
	 */
	private String name;

	/**
	 * type:
	 * <p>
	 * �ֶ�����
	 * 
	 * @since v1.01
	 */
	private String type;

	/**
	 * length:
	 * <p>
	 * ����
	 * 
	 * @since v1.01
	 */
	private int length;

	/**
	 * notNull:
	 * <p>
	 * ��Ϊ��
	 * 
	 * @since v1.01
	 */
	private boolean notNull;

	/**
	 * autoIncrement:
	 * <p>
	 * ������
	 * 
	 * @since v1.01
	 */
	private boolean autoIncrement;

	/**
	 * getId:
	 * <p>
	 * ��ȡ id
	 * 
	 * @return {@link Integer} - id
	 * @since v1.01
	 */
	public int getId() {
		return id;
	}

	/**
	 * getLength:
	 * <p>
	 * ��ȡ����
	 * 
	 * @return {@link Integer} - ����
	 * @since v1.01
	 */
	public int getLength() {
		return length;
	}

	/**
	 * getName:
	 * <p>
	 * ��ȡ����
	 * 
	 * @return {@link String} - ����
	 * @since v1.01
	 */
	public String getName() {
		return name;
	}

	/**
	 * getType:
	 * <p>
	 * ��ȡ����
	 * 
	 * @return {@link String} - ����
	 * @since v1.01
	 */
	public String getType() {
		return type;
	}

	/**
	 * isAutoIncrement:
	 * <p>
	 * ���Ϊ���������򷵻�Ϊ <code>true</code> �����򷵻�Ϊ <code>false</code>
	 * 
	 * @return {@link Boolean} - ���Ϊ���������򷵻�Ϊ <code>true</code> �����򷵻�Ϊ
	 *         <code>false</code>
	 * @since v1.01
	 */
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	/**
	 * isNotNull:
	 * <p>
	 * ���������Ϊ�գ��򷵻�Ϊ <code>true</code> �����򷵻�Ϊ <code>false</code>
	 * 
	 * @return {@link Boolean} - ���������Ϊ�գ��򷵻�Ϊ <code>true</code> �����򷵻�Ϊ
	 *         <code>false</code>
	 * @since v1.01
	 */
	public boolean isNotNull() {
		return notNull;
	}

	/**
	 * setAutoIncrement:
	 * <p>
	 * ���Ϊ��������������Ϊ <code>true</code> ����������Ϊ <code>false</code>
	 * 
	 * @param autoIncrement -
	 *            ���Ϊ��������������Ϊ <code>true</code> ����������Ϊ <code>false</code>
	 * @since v1.01
	 */
	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	/**
	 * setId:
	 * <p>
	 * ���� id
	 * 
	 * @param id -
	 *            id
	 * @since v1.01
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * setLength:
	 * <p>
	 * ���ó���
	 * 
	 * @param length -
	 *            ����
	 * @since v1.01
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * setName:
	 * <p>
	 * ��������
	 * 
	 * @param name -
	 *            ����
	 * @since v1.01
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * setNotNull:
	 * <p>
	 * ���������Ϊ�գ�������Ϊ <code>true</code> ����������Ϊ <code>false</code>
	 * 
	 * @param notNull -
	 *            ���������Ϊ�գ�������Ϊ <code>true</code> ����������Ϊ <code>false</code>
	 * @since v1.01
	 */
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	/**
	 * setType:
	 * <p>
	 * ��������
	 * 
	 * @param type -
	 *            ����
	 * @since v1.01
	 */
	public void setType(String type) {
		this.type = type;
	}

}
