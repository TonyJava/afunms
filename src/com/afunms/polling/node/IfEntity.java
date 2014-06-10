/**
 * <p>Description:host,including server and exchange device</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-09-14
 */

package com.afunms.polling.node;

public class IfEntity implements Comparable<IfEntity> {
	private int id;
	private String alias;
	private String index;
	private int type;
	private String descr;
	private String ipAddress;
	private String physAddress;
	private String port;

	private long speed;
	private long inOctets;
	private long outOctets;
	private long errorPkts;
	private long discardPkts;
	private long inspeed;
	private long outspeed;

	private float rxUtilization;
	private float txUtilization;
	private long rxTraffic;
	private long txTraffic;
	private int errors;
	private int discards;
	private int operStatus;

	private int chassis;
	private int slot;
	private int uport;

	public IfEntity() {
		index = null;
		descr = null;
		ipAddress = null;
		physAddress = null;
		port = null;
	}

	public int compareTo(IfEntity ifEntity) {
		if (ifEntity == null) {
			return -1;
		}
		int thisIndex = Integer.parseInt(this.getIndex());
		int ifEntityIndex = Integer.parseInt(ifEntity.getIndex());
		if (thisIndex > ifEntityIndex) {
			return 1;
		} else if (thisIndex < ifEntityIndex) {
			return -1;
		} else {
			return 0;
		}
	}

	public String getAlias() {
		return alias;
	}

	public int getChassis() {
		return chassis;
	}

	public String getDescr() {
		return descr;
	}

	public long getDiscardPkts() {
		return discardPkts;
	}

	public int getDiscards() {
		return discards;
	}

	public long getErrorPkts() {
		return errorPkts;
	}

	public int getErrors() {
		return errors;
	}

	public int getId() {
		return id;
	}

	public String getIndex() {
		return index;
	}

	public long getInOctets() {
		return inOctets;
	}

	public long getInspeed() {
		return inspeed;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getOperStatus() {
		return operStatus;
	}

	public long getOutOctets() {
		return outOctets;
	}

	public long getOutspeed() {
		return outspeed;
	}

	public String getPhysAddress() {
		return physAddress;
	}

	public String getPort() {
		return port;
	}

	/**
	 * @return the rxTraffic
	 */
	public long getRxTraffic() {
		return rxTraffic;
	}

	public float getRxUtilization() {
		return rxUtilization;
	}

	public int getSlot() {
		return slot;
	}

	public long getSpeed() {
		return speed;
	}

	/**
	 * @return the txTraffic
	 */
	public long getTxTraffic() {
		return txTraffic;
	}

	public float getTxUtilization() {
		return txUtilization;
	}

	public int getType() {
		return type;
	}

	public int getUport() {
		return uport;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void setChassis(int chassis) {
		this.chassis = chassis;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setDiscardPkts(long discardPkts) {
		this.discardPkts = discardPkts;
	}

	public void setDiscards(int discards) {
		this.discards = discards;
	}

	public void setErrorPkts(long errorPkts) {
		this.errorPkts = errorPkts;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public void setInOctets(long inOctets) {
		this.inOctets = inOctets;
	}

	public void setInspeed(long inspeed) {
		this.inspeed = inspeed;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setOperStatus(int operStatus) {
		this.operStatus = operStatus;
	}

	public void setOutOctets(long outOctets) {
		this.outOctets = outOctets;
	}

	public void setOutspeed(long outspeed) {
		this.outspeed = outspeed;
	}

	public void setPhysAddress(String physAddress) {
		this.physAddress = physAddress;
	}

	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @param rxTraffic
	 *            the rxTraffic to set
	 */
	public void setRxTraffic(long rxTraffic) {
		this.rxTraffic = rxTraffic;
	}

	public void setRxUtilization(float rxUtilization) {
		this.rxUtilization = rxUtilization;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	/**
	 * @param txTraffic
	 *            the txTraffic to set
	 */
	public void setTxTraffic(long txTraffic) {
		this.txTraffic = txTraffic;
	}

	public void setTxUtilization(float txUtilization) {
		this.txUtilization = txUtilization;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setUport(int uport) {
		this.uport = uport;
	}
}