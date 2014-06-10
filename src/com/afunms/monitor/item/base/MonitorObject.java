
package com.afunms.monitor.item.base;

import com.afunms.common.base.BaseVo;

public class MonitorObject extends BaseVo {
	private static final long serialVersionUID = 472967150279L;

	private int id;
	private String moid;
	private String name;
	private String descr;
	private String category;
	private boolean isDefault;
	private int threshold;
	private String unit;
	private int compare;
	private int compareType;
	private int upperTimes;
	private String alarmInfo;
	private boolean enabled;
	private int alarmLevel;
	private int pollInterval;
	private String intervalUnit;
	private int resultType;
	private boolean showInTopo;
	private String nodetype;
	private String subentity;
	private int limenvalue0;
	private int limenvalue1;
	private int limenvalue2;
	private int time0;
	private int time1;
	private int time2;
	private int sms0;
	private int sms1;
	private int sms2;

	public String getAlarmInfo() {
		return alarmInfo;
	}

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public String getCategory() {
		return category;
	}

	public int getCompare() {
		return compare;
	}

	public int getCompareType() {
		return compareType;
	}

	public String getDescr() {
		return descr;
	}

	public int getId() {
		return id;
	}

	public String getIntervalUnit() {
		return intervalUnit;
	}

	public int getLimenvalue0() {
		return limenvalue0;
	}

	public int getLimenvalue1() {
		return limenvalue1;
	}

	public int getLimenvalue2() {
		return limenvalue2;
	}

	public String getMoid() {
		return moid;
	}

	public String getName() {
		return name;
	}

	public String getNodetype() {
		return nodetype;
	}

	public int getPollInterval() {
		return pollInterval;
	}

	public int getResultType() {
		return resultType;
	}

	public int getSms0() {
		return sms0;
	}

	public int getSms1() {
		return sms1;
	}

	public int getSms2() {
		return sms2;
	}

	public String getSubentity() {
		return subentity;
	}

	public int getThreshold() {
		return threshold;
	}

	public int getTime0() {
		return time0;
	}

	public int getTime1() {
		return time1;
	}

	public int getTime2() {
		return time2;
	}

	public String getUnit() {
		return unit;
	}

	public int getUpperTimes() {
		return upperTimes;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isShowInTopo() {
		return showInTopo;
	}

	public void setAlarmInfo(String alarmInfo) {
		this.alarmInfo = alarmInfo;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCompare(int compare) {
		this.compare = compare;
	}

	public void setCompareType(int compareType) {
		this.compareType = compareType;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIntervalUnit(String intervalUnit) {
		this.intervalUnit = intervalUnit;
	}

	public void setLimenvalue0(int limenvalue0) {
		this.limenvalue0 = limenvalue0;
	}

	public void setLimenvalue1(int limenvalue1) {
		this.limenvalue1 = limenvalue1;
	}

	public void setLimenvalue2(int limenvalue2) {
		this.limenvalue2 = limenvalue2;
	}

	public void setMoid(String moid) {
		this.moid = moid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}

	public void setPollInterval(int pollInterval) {
		this.pollInterval = pollInterval;
	}

	public void setResultType(int resultType) {
		this.resultType = resultType;
	}

	public void setShowInTopo(boolean showInTopo) {
		this.showInTopo = showInTopo;
	}

	public void setSms0(int sms0) {
		this.sms0 = sms0;
	}

	public void setSms1(int sms1) {
		this.sms1 = sms1;
	}

	public void setSms2(int sms2) {
		this.sms2 = sms2;
	}

	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}

	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	public void setTime0(int time0) {
		this.time0 = time0;
	}

	public void setTime1(int time1) {
		this.time1 = time1;
	}

	public void setTime2(int time2) {
		this.time2 = time2;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setUpperTimes(int upperTimes) {
		this.upperTimes = upperTimes;
	}
}
