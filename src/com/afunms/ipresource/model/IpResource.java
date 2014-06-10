
package com.afunms.ipresource.model;

import com.afunms.common.base.BaseVo;

public class IpResource extends BaseVo
{	
	private int id;	
	private String ipAddress;
	private long ipLong;	
	private String mac;	
	private String ifIndex;
	private String ifDescr;
	private int nodeId;
	
	private String node;
	private boolean port;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIfDescr() {
		return ifDescr;
	}
	public void setIfDescr(String ifDescr) {
		this.ifDescr = ifDescr;
	}
	public String getIfIndex() {
		return ifIndex;
	}
	public void setIfIndex(String ifIndex) {
		this.ifIndex = ifIndex;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public long getIpLong() {
		return ipLong;
	}
	public void setIpLong(long ipLong) {
		this.ipLong = ipLong;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public boolean isPort() {
		return port;
	}
	public void setPort(boolean port) {
		this.port = port;
	}
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}    
}