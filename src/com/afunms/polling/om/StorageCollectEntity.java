package com.afunms.polling.om;

import java.io.Serializable;

public class StorageCollectEntity implements Serializable {
	private Integer id;
	private String ipaddress;
	private String name;
	private String storageindex;
	private String type;
	private String cap;

	public StorageCollectEntity() {
	}

	public StorageCollectEntity(Integer id, String ipaddress, String name, String storageindex, String type, String cap) {
		this.id = id;
		this.ipaddress = ipaddress;
		this.name = name;
		this.storageindex = storageindex;
		this.type = type;
		this.cap = cap;
	}

	public String getCap() {
		return cap;
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

	public String getStorageindex() {
		return storageindex;
	}

	public String getType() {
		return type;
	}

	public void setCap(String cap) {
		this.cap = cap;
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

	public void setStorageindex(String storageindex) {
		this.storageindex = storageindex;
	}

	public void setType(String type) {
		this.type = type;
	};

}
