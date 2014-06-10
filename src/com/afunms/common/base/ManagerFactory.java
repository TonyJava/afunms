package com.afunms.common.base;

import java.util.Hashtable;

import com.afunms.initialize.ResourceCenter;

public class ManagerFactory {
	public static AjaxManagerInterface getAjaxManager(String bean) {
		Hashtable ajaxManagerMap = ResourceCenter.getInstance().getAjaxManagerMap();
		AjaxManagerInterface manager = null;
		try {
			manager = (AjaxManagerInterface) ajaxManagerMap.get(bean);
		} catch (Exception e) {
			e.printStackTrace();
			manager = null;
		}
		return manager;
	}

	public static ManagerInterface getManager(String bean) {
		Hashtable managerMap = ResourceCenter.getInstance().getManagerMap();
		ManagerInterface manager = null;
		try {
			manager = (ManagerInterface) managerMap.get(bean);
		} catch (Exception e) {
			e.printStackTrace();
			manager = null;
		}
		return manager;
	}

	private ManagerFactory() {
	}
}
