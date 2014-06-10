package com.afunms.cabinet.model;

import com.afunms.common.base.BaseVo;

public class OperCabinet extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private int id;
	/**
	 * 机房名称
	 */
	private String roomname;
	/**
	 * 机柜名称
	 */
	private String cabinetname;
	/**
	 * U单元
	 */
	private String useu;
	/**
	 * 设备名称
	 */
	private String equipmentname;
	/**
	 * IP地址
	 */
	private String ipaddress;
	/**
	 * 联系人
	 */
	private String contactname;
	/**
	 * 联系电话
	 */
	private String contactphone;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoomname() {
		return roomname;
	}

	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}

	public String getCabinetname() {
		return cabinetname;
	}

	public void setCabinetname(String cabinetname) {
		this.cabinetname = cabinetname;
	}

	public String getUseu() {
		return useu;
	}

	public void setUseu(String useu) {
		this.useu = useu;
	}

	public String getEquipmentname() {
		return equipmentname;
	}

	public void setEquipmentname(String equipmentname) {
		this.equipmentname = equipmentname;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
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

}
