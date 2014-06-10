package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class DetailCompRule extends BaseVo {
	private int id;
	private int ruleId;
	private int relation;// 关系（与或）
	private int isContain;// 是否包含
	private String expression;// 需要比对的内容（正则表达式或行内容）
	private String beginBlock;// 起始块
	private String endBlock;// 结束块
	private int isExtraContain;// 是否包括附加块(0:不包含；1：包含；-1：无)
	private String extraBlock;// 附加块

	public String getBeginBlock() {
		return beginBlock;
	}

	public String getEndBlock() {
		return endBlock;
	}

	public String getExpression() {
		return expression;
	}

	public String getExtraBlock() {
		return extraBlock;
	}

	public int getId() {
		return id;
	}

	public int getIsContain() {
		return isContain;
	}

	public int getIsExtraContain() {
		return isExtraContain;
	}

	public int getRelation() {
		return relation;
	}

	public int getRuleId() {
		return ruleId;
	}

	public void setBeginBlock(String beginBlock) {
		this.beginBlock = beginBlock;
	}

	public void setEndBlock(String endBlock) {
		this.endBlock = endBlock;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public void setExtraBlock(String extraBlock) {
		this.extraBlock = extraBlock;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIsContain(int isContain) {
		this.isContain = isContain;
	}

	public void setIsExtraContain(int isExtraContain) {
		this.isExtraContain = isExtraContain;
	}

	public void setRelation(int relation) {
		this.relation = relation;
	}

	public void setRuleId(int ruleId) {
		this.ruleId = ruleId;
	}

}
