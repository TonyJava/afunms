/*
 * Created on 2005-3-31
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.util.Timer;
import java.util.TimerTask;

import com.afunms.initialize.TimerListener;

public class MonitorTimer extends Timer {

	public MonitorTimer() {
		super();
	}

	/**
	 * @param isDaemon
	 */
	public MonitorTimer(boolean isDaemon) {
		super(isDaemon);
	}

	public void canclethis(boolean b) {
		if (b == true) {
			super.cancel();
		}
	}

	public void schedule(TimerTask task, long delay, long period) {
		super.scheduleAtFixedRate(task, delay, period);
		new TimerListener(task, delay, period).addTimerListener(this);// Ôö¼Ó¼àÌý
	}
}
