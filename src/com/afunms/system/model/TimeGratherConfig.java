/**
 * <p>Description:mapping table NMS_TIMEGRATHERCONFIG</p>
 * <p>Company: dhcc.com</p>
 * @author snow
 * @project afunms
 * @date 2010-05-14
 */

package com.afunms.system.model;

import com.afunms.common.base.BaseVo;

/**
 * �ɼ�ʱ��
 * 
 * @author snow
 * @since JDK1.5
 */
public class TimeGratherConfig extends BaseVo {
	/**
	 * ����
	 */
	private int id;

	/**
	 * �豸�����id Equipment or services id
	 */
	private String objectId;

	/**
	 * �豸��������� Equipment or services type
	 */
	private String objectType;
	/**
	 * ��Ϣ�ɼ���ʼʱ��
	 */
	private String beginTime;
	/**
	 * ��Ϣ�ɼ�����ʱ��
	 */
	private String endTime;

	/*
	 * ֻ������ʾ���������
	 */
	private String startHour;

	private String startMin;

	private String endHour;

	private String endMin;

	/**
	 * @return ��� beginTime
	 */
	public String getBeginTime() {
		return beginTime;
	}

	public String getEndHour() {
		return endHour;
	}

	public String getEndMin() {
		return endMin;
	}

	/**
	 * @return ��� endTime
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @return ��� id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return ��� objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @return ��� objectType
	 */
	public String getObjectType() {
		return objectType;
	}

	public String getStartHour() {
		return startHour;
	}

	public String getStartMin() {
		return startMin;
	}

	/**
	 * ����beginTime
	 * 
	 * @param beginTime
	 *            Ҫ���õ� beginTime
	 */
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}

	public void setEndMin(String endMin) {
		this.endMin = endMin;
	}

	/**
	 * ����endTime
	 * 
	 * @param endTime
	 *            Ҫ���õ� endTime
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setHourAndMin() {
		setStartHour(beginTime.split(":")[0]);
		setStartMin(beginTime.split(":")[1]);
		setEndHour(endTime.split(":")[0]);
		setEndMin(endTime.split(":")[1]);
	}

	/**
	 * ����id
	 * 
	 * @param id
	 *            Ҫ���õ� id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * ����objectId
	 * 
	 * @param objectId
	 *            Ҫ���õ� objectId
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	/**
	 * ����objectType
	 * 
	 * @param objectType
	 *            Ҫ���õ� objectType
	 */
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}

	public void setStartMin(String startMin) {
		this.startMin = startMin;
	}

}
