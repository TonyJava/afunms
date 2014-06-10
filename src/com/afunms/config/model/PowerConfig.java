package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

/**
 * @description ��Դ����
 * @author wangxiangyong
 * @date Dec 27, 2011
 */
public class PowerConfig extends BaseVo {
	private Integer id;
	private String ipaddress;// �豸IP
	private String name; // ��Դģ������
	private Integer enabled;// �Ƿ��͸澯
	private Integer alarmvalue;// �澯��ֵ
	private String alarmlevel;// �澯����1����ͨ��2�����أ�3��������
	private Integer alarmtimes;// �澯����
	private String bak;// ��ע

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
