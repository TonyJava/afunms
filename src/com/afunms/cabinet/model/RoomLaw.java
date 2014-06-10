package com.afunms.cabinet.model;

import com.afunms.common.base.BaseVo;

/**
 * 机房管理制度
 * 
 * @author wxy
 * @version Dec 19, 2011 3:36:26 PM
 */
public class RoomLaw extends BaseVo {
	private int id;
	private int cabinetid;// 机房ID（与表nms_eqproom的id对应）
	private int userid;// 用户ID(与表system_user的id对应)
	private String name;// 管理制度名称
	private String location;// 机房位置
	private String filename;// 文件路径名
	private String dotime;// 操作时间
	private String description;// 表述

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getDotime() {
		return dotime;
	}

	public void setDotime(String dotime) {
		this.dotime = dotime;
	}

	public int getCabinetid() {
		return cabinetid;
	}

	public void setCabinetid(int cabinetid) {
		this.cabinetid = cabinetid;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
