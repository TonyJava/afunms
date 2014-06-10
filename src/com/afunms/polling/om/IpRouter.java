package com.afunms.polling.om;

import java.io.Serializable;

/** @author Hibernate CodeGenerator */
public class IpRouter implements Serializable {
	private Long id;
	private String relateipaddr;// 路由表对应的父IP
	private String ifindex; // ipIndex
	private String dest; // ipRouterDest
	private String nexthop; // ipRouterNextHop
	private Long type; // ipRouterType
	private Long proto; // ipRouterProto
	private String mask; // ipRouterMask
	private String physaddress;
	private java.util.Calendar collecttime;

	/** default constructor */
	public IpRouter() {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IpRouter)) {
			return false;
		}

		IpRouter that = (IpRouter) obj;
		if (this.getDest().equals(that.getDest()) && this.getNexthop().equals(that.getNexthop())) {
			return true;
		} else {
			return false;
		}
	}

	public java.util.Calendar getCollecttime() {
		return collecttime;
	}

	public String getDest() {
		return dest;
	}

	/**
	 * @return
	 */
	public Long getId() {
		return id;
	}

	public String getIfindex() {
		return ifindex;
	}

	public String getMask() {
		return mask;
	}

	public String getNexthop() {
		return nexthop;
	}

	public String getPhysaddress() {
		return physaddress;
	}

	public Long getProto() {
		return proto;
	}

	public String getRelateipaddr() {
		return relateipaddr;
	}

	public Long getType() {
		return type;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = result * 31 + this.getDest().hashCode();
		result = result * 31 + this.getNexthop().hashCode();
		return result;
	}

	public void setCollecttime(java.util.Calendar collecttime) {
		this.collecttime = collecttime;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	/**
	 * @param integer
	 */
	public void setId(Long l) {
		id = l;
	}

	public void setIfindex(String ifindex) {
		this.ifindex = ifindex;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public void setNexthop(String nexthop) {
		this.nexthop = nexthop;
	}

	public void setPhysaddress(String physaddress) {
		this.physaddress = physaddress;
	}

	public void setProto(Long proto) {
		this.proto = proto;
	}

	public void setRelateipaddr(String relateipaddr) {
		this.relateipaddr = relateipaddr;
	}

	public void setType(Long type) {
		this.type = type;
	}
}
