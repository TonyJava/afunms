package com.afunms.common.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AjaxManagerInterface {
	/**
	 * �ؼ�����,ÿ�������������ʵ��
	 */
	public void execute(String action);

	public void setRequest(HttpServletRequest req);

	public void setRequest(HttpServletRequest req, HttpServletResponse res);
}
