package com.afunms.system.vo;

import java.io.Serializable;

public class RouteVo implements Serializable {

	private String ifindex;

	private String dest;

	private String nexthop;

	private String routetype;

	private String routeproto;

	private String mask;

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

	public String getMask() {
		return mask;
	}

	public String getNexthop() {
		return nexthop;
	}

	public String getRouteproto() {
		return routeproto;
	}

	public String getRoutetype() {
		return routetype;
	}

	public void setCollecttime(String collecttime) {
		this.collecttime = collecttime;
	}

	public void setDest(String dest) {
		this.dest = dest;
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

	public void setRouteproto(String routeproto) {
		this.routeproto = routeproto;
	}

	public void setRoutetype(String routetype) {
		this.routetype = routetype;
	}

}