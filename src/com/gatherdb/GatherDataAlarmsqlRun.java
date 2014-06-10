package com.gatherdb;

import java.util.TimerTask;

@SuppressWarnings("unchecked")
public class GatherDataAlarmsqlRun extends TimerTask {
	@Override
	public void run() {
		if (!GathersqlListManager.idbstatus_alarm) {
			GathersqlListManager.Addsql_alarm("DHCC-DB");
		}
	}
}
