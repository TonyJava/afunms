
package com.afunms.monitor.item;

public class UPSPhase {
	private int index;
	private int voltage; // 电压
	private int frequency; // 频率
	private int current; // 电流
	private int load; // 负载
	private int loadPercent; // 负载%
	private int io; // 1=输入,0=输出

	public int getCurrent() {
		return current;
	}

	public int getFrequency() {
		return frequency;
	}

	public int getIndex() {
		return index;
	}

	public int getIo() {
		return io;
	}

	public int getLoad() {
		return load;
	}

	public int getLoadPercent() {
		return loadPercent;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setIo(int io) {
		this.io = io;
	}

	public void setLoad(int load) {
		this.load = load;
	}

	public void setLoadPercent(int loadPercent) {
		this.loadPercent = loadPercent;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}
}