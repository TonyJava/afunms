package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

/**
 * @description 动力环境告警配置
 * @author wangxiangyong
 * @date Dec 27, 2011
 */
public class EnvConfig extends BaseVo {
	private Integer id;
	private String ipaddress;// 设备IP
	private String name; // 模块名称
	private Integer enabled;// 是否发送告警
	private Integer alarmvalue;// 告警限值
	private String alarmlevel;// 告警级别（1：普通；2：严重；3：紧急）
	private Integer alarmtimes;// 告警次数
	private String entity;
	private String bak;// 备注

	public String getAlarmlevel() {
		return alarmlevel;
	}

	public Integer getAlarmtimes() {
		return alarmtimes;
	}

	public Integer getAlarmvalue() {
		return alarmvalue;
	}

	public String getBak() {
		return bak;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public String getEntity() {
		return entity;
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

	public void setAlarmlevel(String alarmlevel) {
		this.alarmlevel = alarmlevel;
	}

	public void setAlarmtimes(Integer alarmtimes) {
		this.alarmtimes = alarmtimes;
	}

	public void setAlarmvalue(Integer alarmvalue) {
		this.alarmvalue = alarmvalue;
	}

	public void setBak(String bak) {
		this.bak = bak;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public void setEntity(String entity) {
		this.entity = entity;
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

}
