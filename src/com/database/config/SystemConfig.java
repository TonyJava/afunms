package com.database.config;

import java.util.ResourceBundle;

//��ȡ�����ļ�
public class SystemConfig {

	public static String getConfigInfomation(String finename, String itemIndex) {
		try {
			ResourceBundle resource = ResourceBundle.getBundle(finename);
			return resource.getString(itemIndex);
		} catch (Exception e) {
			return "";
		}
	}

}
