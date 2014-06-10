package com.afunms.polling.om;

import java.io.Serializable;

public class DeviceCollectEntity implements Serializable {
	private Integer id;
	private String ipaddress;
	private String name;
	private String deviceindex;
	private String type;
	private String status;

	public DeviceCollectEntity() {
	}

	public DeviceCollectEntity(Integer id, String ipaddress, String name, String deviceindex, String type, String status) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.deviceindex = deviceindex;
		this.type = type;
		this.status = status;
	}

	public String getDeviceindex() {
		return deviceindex;
	}

	public Integer getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getName() {
		return name;
	}

	public String getStatus() {
		return status;
	}

	public String getType() {
		return type;
	}

	public void setDeviceindex(String deviceindex) {
		this.deviceindex = deviceindex;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	};

}
