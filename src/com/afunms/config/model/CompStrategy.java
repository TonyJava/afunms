package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CompStrategy extends BaseVo {
	private int id; // ����ID
	private String name; // ��������
	private String description;// ����
	private int type; // ����
	private int violateType;// ����Υ������
	private String groupId; // ����������ID����
	private String createBy; // ������
	private String createTime; // ����ʱ��
	private String lastModifiedBy;// ���һ���޸���
	private String lastModifiedTime;// �һ���޸�ʱ��

	public String getCreateBy() {
		return createBy;
	}

	public String getCreateTime() {
		return createTime;
	}

	public String getDescription() {
		return description;
	}

	public String getGroupId() {
		return groupId;
	}

	public int getId() {
		return id;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public String getLastModifiedTime() {
		return lastModifiedTime;
	}

	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public int getViolateType() {
		return violateType;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public void setLastModifiedTime(String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setViolateType(int violateType) {
		this.violateType = violateType;
	}

}
