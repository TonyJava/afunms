package com.afunms.polling.om;

import java.io.Serializable;
import java.util.Calendar;

import com.afunms.common.base.BaseVo;

public class NetAppSnapshot extends BaseVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ipaddress;

	private Calendar collectTime;

	private String slVIndex;

	private String slVMonth;

	private String slVDay;

	private String slVHour;

	private String slVMinutes;

	private String slVName; // 快照名字

	private String slVVolume; // 包含此快照的卷

	private String slVNumber;// 卷序号

	private String slVVolumeName;// 包含此快照的卷名

	private String slVType;// 包含此快照的卷类型

	public Calendar getCollectTime() {
		return collectTime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public String getSlVDay() {
		return slVDay;
	}

	public String getSlVHour() {
		return slVHour;
	}

	public String getSlVIndex() {
		return slVIndex;
	}

	public String getSlVMinutes() {
		return slVMinutes;
	}

	public String getSlVMonth() {
		return slVMonth;
	}

	public String getSlVName() {
		return slVName;
	}

	public String getSlVNumber() {
		return slVNumber;
	}

	public String getSlVType() {
		return slVType;
	}

	public String getSlVVolume() {
		return slVVolume;
	}

	public String getSlVVolumeName() {
		return slVVolumeName;
	}

	public void setCollectTime(Calendar collectTime) {
		this.collectTime = collectTime;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public void setSlVDay(String slVDay) {
		this.slVDay = slVDay;
	}

	public void setSlVHour(String slVHour) {
		this.slVHour = slVHour;
	}

	public void setSlVIndex(String slVIndex) {
		this.slVIndex = slVIndex;
	}

	public void setSlVMinutes(String slVMinutes) {
		this.slVMinutes = slVMinutes;
	}

	public void setSlVMonth(String slVMonth) {
		this.slVMonth = slVMonth;
	}

	public void setSlVName(String slVName) {
		this.slVName = slVName;
	}

	public void setSlVNumber(String slVNumber) {
		this.slVNumber = slVNumber;
	}

	public void setSlVType(String slVType) {
		this.slVType = slVType;
	}

	public void setSlVVolume(String slVVolume) {
		this.slVVolume = slVVolume;
	}

	public void setSlVVolumeName(String slVVolumeName) {
		this.slVVolumeName = slVVolumeName;
	}

}
