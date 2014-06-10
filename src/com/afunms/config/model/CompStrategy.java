package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CompStrategy extends BaseVo {
	private int id; // 策略ID
	private String name; // 策略名称
	private String description;// 描述
	private int type; // 类型
	private int violateType;// 策略违反类型
	private String groupId; // 关联规则组ID集合
	private String createBy; // 创建人
	private String createTime; // 创建时间
	private String lastModifiedBy;// 最近一次修改人
	private String lastModifiedTime;// 最精一次修改时间

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
