/*
 * Created on 2010-06-24
 *
 */
package com.afunms.polling.task;

import com.afunms.cabinet.util.CabinetXML;

/**
 * 3D�������ݸ���task
 */
public class CabinetTask extends MonitorTask {

	public void run() {
		try {
			// ��������3D��������xml�ļ�
			CabinetXML cxml = new CabinetXML();
			cxml.CreateCabinetXML();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
