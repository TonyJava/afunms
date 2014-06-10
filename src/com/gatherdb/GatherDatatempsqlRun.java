package com.gatherdb;

import java.util.TimerTask;
import java.util.Vector;

@SuppressWarnings("unchecked")
public class GatherDatatempsqlRun extends TimerTask {

	@Override
	public void run() {
		Vector alldata = null;
		if (!GathersqlListManager.idbdatatempstatus) {
			GathersqlListManager.AdddateTempsql("DHCC-DB", alldata);
		}
	}
}
