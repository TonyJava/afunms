/*
 * Created on 2005-4-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import org.apache.log4j.Logger;

import com.afunms.polling.api.I_HostCollectDataHour;
import com.afunms.polling.impl.HostCollectDataHourManager;

public class HostCollectHourTask extends MonitorTask {
	private Logger logger = Logger.getLogger(this.getClass());
	public HostCollectHourTask() {
		super();
	}

	public void run() {
		logger.info(" 开始执行小时归档任务 ");
		I_HostCollectDataHour hostdataManager = new HostCollectDataHourManager();
		try {
			hostdataManager.schemeTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
