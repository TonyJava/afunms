/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class DHCP extends Application {

	private int id;
	private String community;
	private int mon_flag;
	private String netid;
	private String dhcptype;

	public String getCommunity() {
		return community;
	}

	public String getDhcptype() {
		return dhcptype;
	}

	@Override
	public int getId() {
		return id;
	}

	public int getMon_flag() {
		return mon_flag;
	}

	public String getNetid() {
		return netid;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public void setDhcptype(String dhcptype) {
		this.dhcptype = dhcptype;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setMon_flag(int mon_flag) {
		this.mon_flag = mon_flag;
	}

	public void setNetid(String netid) {
		this.netid = netid;
	}

}