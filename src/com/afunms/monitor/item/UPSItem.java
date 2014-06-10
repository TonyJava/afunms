package com.afunms.monitor.item;

import java.util.List;

import com.afunms.monitor.item.base.MonitoredItem;
import com.afunms.topology.model.NodeMonitor;

@SuppressWarnings("unchecked")
public class UPSItem extends MonitoredItem {
	private List phasesList;
	private int devicesNumber; // �����豸��
	private int batteryLevel; // ������
	private int batteryVoltage; // ��ص�ѹ
	private int upsLoad; // ups���أ�Ϊ������ฺ��֮��(����W)
	private int upsRatedLoad; // �����(����W)
	private int batteryTime; // �ϵ�ʱ��ups�ܹ����ʱ��
	private int lowBatteryLevel; // ����½������ٺ�ups׼���ر�
	private boolean batteryFault; // ����Ƿ��й���
	private boolean batteryLow; // ��ص͵���
	private boolean batteryChargerFault; // ��س�����Ƿ��й���
	// ---------����Ҫ����������--------------
	private boolean outputOnBattery; // ����������,˵���е�ϵ�
	private boolean outputOnByPass; // �����·����,˵��UPS�й���
	private boolean overLoad; // ���(����)�Ƿ���

	public UPSItem() {
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}

	public int getBatteryTime() {
		return batteryTime;
	}

	public int getBatteryVoltage() {
		return batteryVoltage;
	}

	public int getDevicesNumber() {
		return devicesNumber;
	}

	public int getLowBatteryLevel() {
		return lowBatteryLevel;
	}

	public List getPhasesList() {
		return phasesList;
	}

	public int getUpsLoad() {
		return upsLoad;
	}

	public int getUpsRatedLoad() {
		return upsRatedLoad;
	}

	public boolean isBatteryChargerFault() {
		return batteryChargerFault;
	}

	public boolean isBatteryFault() {
		return batteryFault;
	}

	public boolean isBatteryLow() {
		return batteryLow;
	}

	public boolean isOutputOnBattery() {
		return outputOnBattery;
	}

	public boolean isOutputOnByPass() {
		return outputOnByPass;
	}

	public boolean isOverLoad() {
		return overLoad;
	}

	@Override
	public void loadSelf(NodeMonitor nm) {
		loadCommon(nm);
	}

	public void setBatteryChargerFault(boolean batteryChargerFault) {
		this.batteryChargerFault = batteryChargerFault;
	}

	public void setBatteryFault(boolean batteryFault) {
		this.batteryFault = batteryFault;
	}

	public void setBatteryLevel(int batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public void setBatteryLow(boolean batteryLow) {
		this.batteryLow = batteryLow;
	}

	public void setBatteryTime(int batteryTime) {
		this.batteryTime = batteryTime;
	}

	public void setBatteryVoltage(int batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public void setDevicesNumber(int devicesNumber) {
		this.devicesNumber = devicesNumber;
	}

	public void setLowBatteryLevel(int lowBatteryLevel) {
		this.lowBatteryLevel = lowBatteryLevel;
	}

	public void setOutputOnBattery(boolean outputOnBattery) {
		this.outputOnBattery = outputOnBattery;
	}

	public void setOutputOnByPass(boolean outputOnByPass) {
		this.outputOnByPass = outputOnByPass;
	}

	public void setOverLoad(boolean overLoad) {
		this.overLoad = overLoad;
	}

	public void setPhasesList(List phasesList) {
		this.phasesList = phasesList;
	}

	public void setUpsLoad(int upsLoad) {
		this.upsLoad = upsLoad;
	}

	public void setUpsRatedLoad(int upsRatedLoad) {
		this.upsRatedLoad = upsRatedLoad;
	}
}
