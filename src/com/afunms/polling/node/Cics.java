/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-27
 */

package com.afunms.polling.node;


public class Cics extends Application {

	private int id;

	private String ipaddress;

	private String port_listener;

	private String network_protocol;

	private int conn_timeout;

	private String alias;

	private String region_name;

	private String sendmobiles;

	private String netid;

	private String sendemail;

	private int flag;

	private String gateway;

	@Override
	public String getAlias() {
		return alias;
	}

	public int getConn_timeout() {
		return conn_timeout;
	}

	public int getFlag() {
		return flag;
	}

	public String getGateway() {
		return gateway;
	}

	@Override
	public int getId() {
		return id;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getNetid() {
		return netid;
	}

	public String getNetwork_protocol() {
		return network_protocol;
	}

	public String getPort_listener() {
		return port_listener;
	}

	public String getRegion_name() {
		return region_name;
	}

	@Override
	public String getSendemail() {
		return sendemail;
	}

	@Override
	public String getSendmobiles() {
		return sendmobiles;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setConn_timeout(int conn_timeout) {
		this.conn_timeout = conn_timeout;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setNetid(String netid) {
		this.netid = netid;
	}

	public void setNetwork_protocol(String network_protocol) {
		this.network_protocol = network_protocol;
	}

	public void setPort_listener(String port_listener) {
		this.port_listener = port_listener;
	}

	public void setRegion_name(String region_name) {
		this.region_name = region_name;
	}

	@Override
	public void setSendemail(String sendemail) {
		this.sendemail = sendemail;
	}

	@Override
	public void setSendmobiles(String sendmobiles) {
		this.sendmobiles = sendmobiles;
	}

}