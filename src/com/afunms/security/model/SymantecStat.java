/**
 * <p>Description: symantec_stat��¼</p>
 * <p>Company: ���������ϴ�����Ƽ��ɷ����޹�˾</p>
 * @author ������
 * @project ��������
 * @date 2005-3-15
 */

package com.afunms.security.model;

public class SymantecStat {
	private String begindate;
	private String content;
	private int total;

	public String getBeginDate() {
		return begindate;
	}

	public String getContent() {
		return content;
	}

	public int getTotal() {
		return total;
	}

	public void setBeginDate(String newBegindate) {
		begindate = newBegindate;
	}

	public void setContent(String newContent) {
		content = newContent;
	}

	public void setTotal(int newTotal) {
		total = newTotal;
	}
}
