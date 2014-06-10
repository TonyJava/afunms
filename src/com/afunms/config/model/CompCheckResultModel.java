package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CompCheckResultModel extends BaseVo {

	private int id;

	private int strategyId;

	private String strategyName;

	private String ip;

	private int groupId;

	private String groupName;

	private int ruleId;

	private String ruleName;

	private String description;

	private int violationSeverity;// 规则违反度(普通：0;重要：1；严重：2)

	private int isViolation;// 是否违反规则
	private String checkTime;

	public String getCheckTime() {
		return checkTime;
	}

	public String getDescription() {
		return description;
	}

	public int getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public int getIsViolation() {
		return isViolation;
	}

	public int getRuleId() {
		return ruleId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public int getStrategyId() {
		return strategyId;
	}

	public String getStrategyName() {
		return strategyName;
	}

	public int getViolationSeverity() {
		return violationSeverity;
	}

	public void setCheckTime(String checkTime) {
		this.checkTime = checkTime;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setIsViolation(int isViolation) {
		this.isViolation = isViolation;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public void setStrategyId(int strategyId) {
		this.strategyId = strategyId;
	}

	public void setStrategyName(String strategyName) {
		this.strategyName = strategyName;
	}

	public void setViolationSeverity(int violationSeverity) {
		this.violationSeverity = violationSeverity;
	}
}
