package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

/**
 * @description vpn拓扑图
 * @author wangxiangyong
 * @date Feb 8, 2012 4:39:51 PM
 */
public class Vpn extends BaseVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5211137773136790968L;
	private int id;
	// private String linkName;
	private String sourceIp;// 源IP
	private int sourceId;// 源设备Id
	private String sourcePortIndex;// 源设备端口索引
	private String sourcePortName;// 源设备端口名称
	private String desIp;// 目标IP
	private int desId;// 目标设备Id
	private String desPortIndex;// 目标设备端口索引
	private String desPortName;// 目标设备端口名称

	public int getDesId() {
		return desId;
	}

	public String getDesIp() {
		return desIp;
	}

	public String getDesPortIndex() {
		return desPortIndex;
	}

	public String getDesPortName() {
		return desPortName;
	}

	public int getId() {
		return id;
	}

	public int getSourceId() {
		return sourceId;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public String getSourcePortIndex() {
		return sourcePortIndex;
	}

	public String getSourcePortName() {
		return sourcePortName;
	}

	public void setDesId(int desId) {
		this.desId = desId;
	}

	public void setDesIp(String desIp) {
		this.desIp = desIp;
	}

	public void setDesPortIndex(String desPortIndex) {
		this.desPortIndex = desPortIndex;
	}

	public void setDesPortName(String desPortName) {
		this.desPortName = desPortName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	public void setSourcePortIndex(String sourcePortIndex) {
		this.sourcePortIndex = sourcePortIndex;
	}

	public void setSourcePortName(String sourcePortName) {
		this.sourcePortName = sourcePortName;
	}

}
