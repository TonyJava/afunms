package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class CompCheckRule extends BaseVo {
	private int id;
	private int strategyId;
	private int groupId;
	private int ruleId;
	private String ip;
	private int isViolation;// 是否违反规则
	private int relation;// //关系（与或）

	private int isContain;// //是否包含
	private String content;// 违反的规则内容

	public String getContent() {
		return content;
	}

	public int getGroupId() {
		return groupId;
	}

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public int getIsContain() {
		return isContain;
	}

	public int getIsViolation() {
		return isViolation;
	}

	public int getRelation() {
		return relation;
	}

	public int getRuleId() {
		return ruleId;
	}

	public int getStrategyId() {
		return strategyId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setIsContain(int isContain) {
		this.isContain = isContain;
	}

	public void setIsViolation(int isViolation) {
		this.isViolation = isViolation;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

	public void setStrategyId(int strategyId) {
		this.strategyId = strategyId;
	}

}
