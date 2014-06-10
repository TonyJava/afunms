package com.afunms.polling;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class PollingThread extends Thread {
	private List nodeList;

	public PollingThread() {
		nodeList = new ArrayList(20);
	}

	public List getNodeList() {
		return nodeList;
	}

	public void run() {
	}
}