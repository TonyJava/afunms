package com.gatherdb;

import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * 
 * 
 * 定时把队列里的数据插入数据库
 * 
 * @author Administrator
 * 
 */
public class GathersqlRun extends TimerTask {

	public Logger logger = Logger.getLogger(GathersqlRun.class);

	@Override
	public void run() {
		if (!GathersqlListManager.idbstatus) {
			GathersqlListManager.Addsql("DHCC-DB");
		}
	}

}
