/*
 * Created on 2005-4-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import org.apache.log4j.Logger;

import com.afunms.polling.api.I_HostCollectDataDay;
import com.afunms.polling.impl.HostCollectDataDayManager;

public class HostCollectDayTask extends MonitorTask {
	private Logger logger = Logger.getLogger(this.getClass());
	public HostCollectDayTask() {
		super();
	}

	public void run() {
		logger.info(" ��ʼִ�а���鵵���� ");
		I_HostCollectDataDay hostdataManager = new HostCollectDataDayManager();
		try {
			hostdataManager.schemeTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
