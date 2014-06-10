package com.afunms.polling.node;

import java.util.List;

public class HP {
	private SystemInfo systemInfo;
	private ArrayInfo arrayInfo;
	private List<Enclosure> enclosures;
	private List<Controller> controllers;
	private List<Port> ports;
	private List<Disk> disks;
	private List<Lun> luns;
	private List<VFP> vfps;
	private SubSystemInfo subSystemInfo;

	public ArrayInfo getArrayInfo() {
		return arrayInfo;
	}

	public List<Controller> getControllers() {
		return controllers;
	}

	public List<Disk> getDisks() {
		return disks;
	}

	public List<Enclosure> getEnclosures() {
		return enclosures;
	}

	public List<Lun> getLuns() {
		return luns;
	}

	public List<Port> getPorts() {
		return ports;
	}

	public SubSystemInfo getSubSystemInfo() {
		return subSystemInfo;
	}

	public SystemInfo getSystemInfo() {
		return systemInfo;
	}

	public List<VFP> getVfps() {
		return vfps;
	}

	public void setArrayInfo(ArrayInfo arrayInfo) {
		this.arrayInfo = arrayInfo;
	}

	public void setControllers(List<Controller> controllers) {
		this.controllers = controllers;
	}

	public void setDisks(List<Disk> disks) {
		this.disks = disks;
	}

	public void setEnclosures(List<Enclosure> enclosures) {
		this.enclosures = enclosures;
	}

	public void setLuns(List<Lun> luns) {
		this.luns = luns;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}

	public void setSubSystemInfo(SubSystemInfo subSystemInfo) {
		this.subSystemInfo = subSystemInfo;
	}

	public void setSystemInfo(SystemInfo systemInfo) {
		this.systemInfo = systemInfo;
	}

	public void setVfps(List<VFP> vfps) {
		this.vfps = vfps;
	}
}