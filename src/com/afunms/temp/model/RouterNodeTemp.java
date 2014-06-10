package com.afunms.temp.model;

import com.afunms.common.base.BaseVo;

public class RouterNodeTemp extends BaseVo {

	private String nodeid;

	private String ip;

	private String type;

	private String subtype;

	private String ifindex;

	private String nexthop;

	private String proto;

	private String rtype;

	private String mask;

	private String physaddress;

	private String dest;

	private String collecttime;

	public String getCollecttime() {
		return collecttime;
	}

	public String getDest() {
		return dest;
	}

	public String getIfindex() {
		return ifindex;
	}

	public String getIp() {
		return ip;
	}

	public String getMask() {
		return mask;
	}

	public String getNexthop() {
		return nexthop;
	}

	public String getNodeid() {
		return nodeid;
	}

	public String getPhysaddress() {
		return physaddress;
	}

	public String getProto() {
		return proto;
	}

	public String getRtype() {
		return rtype;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getType() {
		return type;
	}

	public void setCollecttime(String string) {
		this.collecttime = string;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public void setIfindex(String ifindex) {
		this.ifindex = ifindex;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public void setNexthop(String nexthop) {
		this.nexthop = nexthop;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public void setPhysaddress(String physaddress) {
		this.physaddress = physaddress;
	}

	public void setProto(String proto) {
		this.proto = proto;
	}

	public void setRtype(String rtype) {
		this.rtype = rtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public void setType(String type) {
		this.type = type;
	}

}
