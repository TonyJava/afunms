package com.afunms.mq;

import com.icss.ro.de.connector.mqimpl.MQNode;

public class MqVO implements java.io.Serializable {
	private static final long serialVersionUID = -1875824988729803429L;

	public static MqVO fromMQNode(MQNode node) {
		MqVO qm = new MqVO();
		qm.setHost(node.getHost());
		qm.setQmPort(node.getPort());
		qm.setQmName(node.getQmanager());
		return qm;
	}

	private String qmName;
	private String qmPort;
	private String host;
	private String user;

	private String pass;

	public String getHost() {
		return host;
	}

	public String getPass() {
		return pass;
	}

	public String getQmName() {
		return qmName;
	}

	public String getQmPort() {
		return qmPort;
	}

	public String getUser() {
		return user;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setQmName(String qmName) {
		this.qmName = qmName;
	}

	public void setQmPort(String qmPort) {
		this.qmPort = qmPort;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
