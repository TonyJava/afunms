package com.afunms.common.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AjaxManagerInterface {
	/**
	 * 关键方法,每个具体子类必须实现
	 */
	public void execute(String action);

	public void setRequest(HttpServletRequest req);

	public void setRequest(HttpServletRequest req, HttpServletResponse res);
}
