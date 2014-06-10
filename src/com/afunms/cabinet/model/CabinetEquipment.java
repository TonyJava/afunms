/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.cabinet.model;

import com.afunms.common.base.BaseVo;

public class CabinetEquipment extends BaseVo {
	private int id;
	private int cabinetid;
	private int nodeid;
	private String nodename;
	private String nodedescr;
	private String unmubers;
	private int operid;
	private String contactname;
	private String contactphone;
	private String contactemail;
	private int roomid;
	private int businessid;
	private String businessName;
	private String icon = "../resource/image/menu/jgsb.gif";

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCabinetid() {
		return cabinetid;
	}

	public void setCabinetid(int cabinetid) {
		this.cabinetid = cabinetid;
	}

	public int getNodeid() {
		return nodeid;
	}

	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}

	public String getNodename() {
		return nodename;
	}

	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	public String getNodedescr() {
		return nodedescr;
	}

	public void setNodedescr(String nodedescr) {
		this.nodedescr = nodedescr;
	}

	public String getUnmubers() {
		return unmubers;
	}

	public void setUnmubers(String unmubers) {
		this.unmubers = unmubers;
	}

	public int getOperid() {
		return operid;
	}

	public void setOperid(int operid) {
		this.operid = operid;
	}

	public String getContactname() {
		return contactname;
	}

	public void setContactname(String contactname) {
		this.contactname = contactname;
	}

	public String getContactphone() {
		return contactphone;
	}

	public void setContactphone(String contactphone) {
		this.contactphone = contactphone;
	}

	public String getContactemail() {
		return contactemail;
	}

	public void setContactemail(String contactemail) {
		this.contactemail = contactemail;
	}

	public int getRoomid() {
		return roomid;
	}

	public void setRoomid(int roomid) {
		this.roomid = roomid;
	}

	public int getBusinessid() {
		return businessid;
	}

	public void setBusinessid(int businessid) {
		this.businessid = businessid;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

}
