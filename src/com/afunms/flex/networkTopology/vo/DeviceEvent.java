
package com.afunms.flex.networkTopology.vo;

public class DeviceEvent {
	private long date; // Milliseconds since the Epoch.
	private String description; // Description of the event.

	public long getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public void setDate(long d) {
		date = d;
	}

	public void setDescription(String d) {
		description = d;
	}
}