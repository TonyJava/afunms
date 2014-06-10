/**
 * @author sunqichang/ËïÆô²ý
 * Created on May 13, 2011 4:50:21 PM
 */
package com.afunms.capreport.subscribe;

import java.util.Timer;

public class SubscribeTimer {

	/**
	 * Æô¶¯¶©ÔÄ¼ÆÊ±Æ÷
	 */
	public static void startupSubscribe() {
		Timer timer = new Timer("Subscribe", true);
		timer.schedule(new SubscribeTask(), 0, 1000 * 60 * 30);
	}

}
