package com.afunms.webservice.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @description 设备硬件（配件）基本信息（CPU、内存、磁盘、闪存）
 * @author wangxiangyong
 * @date Jan 14, 2012 3:44:46 PM
 */
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceHardwareInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1661351691098145935L;
	private String ipAddress; // 设备的IP地址
	private DeviceCpuInfo cpu;
	private List<DeviceDiskInfo> disk;
	private List<DeviceMemoryInfo> memeory;
	private List<DeviceFlashInfo> flash;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public DeviceCpuInfo getCpu() {
		return cpu;
	}

	public void setCpu(DeviceCpuInfo cpu) {
		this.cpu = cpu;
	}

	public List<DeviceDiskInfo> getDisk() {
		return disk;
	}

	public void setDisk(List<DeviceDiskInfo> disk) {
		this.disk = disk;
	}

	public List<DeviceMemoryInfo> getMemeory() {
		return memeory;
	}

	public void setMemeory(List<DeviceMemoryInfo> memeory) {
		this.memeory = memeory;
	}

	public List<DeviceFlashInfo> getFlash() {
		return flash;
	}

	public void setFlash(List<DeviceFlashInfo> flash) {
		this.flash = flash;
	}

}
