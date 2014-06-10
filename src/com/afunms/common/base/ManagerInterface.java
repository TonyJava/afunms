/**
 * <p>Description:manager interface,define a method(execute) of all dao classes</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.common.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ManagerInterface {
	/**
	 * �ؼ�����,ÿ�������������ʵ��
	 */
	public String execute(String action);

	public int getErrorCode();

	public void setRequest(HttpServletRequest req);

	public void setRequest(HttpServletRequest req, HttpServletResponse res);
}