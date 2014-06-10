package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class DetailCompRule extends BaseVo {
	private int id;
	private int ruleId;
	private int relation;// ��ϵ�����
	private int isContain;// �Ƿ����
	private String expression;// ��Ҫ�ȶԵ����ݣ�������ʽ�������ݣ�
	private String beginBlock;// ��ʼ��
	private String endBlock;// ������
	private int isExtraContain;// �Ƿ�������ӿ�(0:��������1��������-1����)
	private String extraBlock;// ���ӿ�

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
