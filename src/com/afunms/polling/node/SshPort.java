package com.afunms.polling.node;

/**
 * 
 * @descrition ¶Ë¿Ú
 * @author wangxiangyong
 * @date Jun 16, 2013 2:22:57 PM
 */
public class SshPort {
	private String port;
	private String media;
	private String targetID;
	private String status;
	private String speedA;
	private String speedC;
	private String topoC;
	private String pid;

	public String getMedia() {
		return media;
	}

	public String getPid() {
		return pid;
	}

	public String getPort() {
		return port;
	}

	public String getSpeedA() {
		return speedA;
	}

	public String getSpeedC() {
		return speedC;
	}

	public String getStatus() {
		return status;
	}

	public String getTargetID() {
		return targetID;
	}

	public String getTopoC() {
		return topoC;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setSpeedA(String speedA) {
		this.speedA = speedA;
	}

	public void setSpeedC(String speedC) {
		this.speedC = speedC;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTargetID(String targetID) {
		this.targetID = targetID;
	}

	public void setTopoC(String topoC) {
		this.topoC = topoC;
	}

}
