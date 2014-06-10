
package com.afunms.flex.networkTopology;

import com.afunms.flex.networkTopology.vo.Device;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;

public class FeedThread extends Thread {
	public boolean running = false;

	private DataSource _dataSource;
	private static int _FEED_INTERVAL = 3000; // Interval in milliseconds to push the data.
	private static String _DESTINATION_NAME = "feed";

	public void setDataSource(DataSource dataSource) {
		_dataSource = dataSource;
	}

	public void run() {
		MessageBroker msgBroker = MessageBroker.getMessageBroker(null);
		String clientID = UUIDUtils.createUUID(false);

		while (running && !_dataSource.equals(null)) {
			Device[] devices = _dataSource.getDevices();

			// Loop through the devices and update the properties.
			// Since the data is not coming from a database the values are
			// updated randomly.
			for (int i = 0; i < devices.length; i++) {
				Device device = devices[i];

				// create a cpu value.

				int cpu = device.getCpu();
				device.setCpu(cpu);

				// create a memory value.
				int memory = device.getMemory();
				device.setMemory(memory);

				// create an incoming value.
				float incoming = device.getIncoming();
				device.setIncoming(incoming);

				// create an outgoing value.
				float outgoing = device.getOutgoing();
				device.setOutgoing(outgoing);
			}

			AsyncMessage msg = new AsyncMessage();
			msg.setDestination(_DESTINATION_NAME);
			msg.setClientId(clientID);
			msg.setMessageId(UUIDUtils.createUUID(false));
			msg.setTimestamp(System.currentTimeMillis());
			msg.setBody(devices);
			msgBroker.routeMessageToService(msg, null);

			try {
				Thread.sleep(_FEED_INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}