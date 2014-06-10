/*
 * Created on 2010-06-24
 *
 */
package com.afunms.polling.task;

import com.afunms.cabinet.util.CabinetXML;

/**
 * 3D机房数据更新task
 */
public class CabinetTask extends MonitorTask {

	public void run() {
		try {
			// 重新生成3D机房数据xml文件
			CabinetXML cxml = new CabinetXML();
			cxml.CreateCabinetXML();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
