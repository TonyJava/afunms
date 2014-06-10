package com.afunms.polling.node;

public class Port {

	private String name;
	private String portID;
	private String behavior;
	private String topology;
	private String queueFullThreshold;
	private String dataRate;

	public String getBehavior() {
		return behavior;
	}

	public String getDataRate() {
		return dataRate;
	}

	public String getName() {
		return name;
	}

	public String getPortID() {
		return portID;
	}

	public String getQueueFullThreshold() {
		return queueFullThreshold;
	}

	public String getTopology() {
		return topology;
	}

	public void setBehavior(String behavior) {
		this.behavior = behavior;
	}

	public void setDataRate(String dataRate) {
		this.dataRate = dataRate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPortID(String portID) {
		this.portID = portID;
	}

	public void setQueueFullThreshold(String queueFullThreshold) {
		this.queueFullThreshold = queueFullThreshold;
	}

	public void setTopology(String topology) {
		this.topology = topology;
	}

}
