package com.afunms.config.model;

public class CmdResult {
	private String ip;
	private String command;
	private String result;
	private String time;

	public String getCommand() {
		return command;
	}

	public String getIp() {
		return ip;
	}

	public String getResult() {
		return result;
	}

	public String getTime() {
		return time;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
