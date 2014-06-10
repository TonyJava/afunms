/**
 * <p>Description:mapping table NMS_POSITION</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-07
 */

package com.afunms.cabinet.model;

import com.afunms.common.base.BaseVo;

public class EquipmentReport extends BaseVo {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String equipmentname;
	private String equipmentdesc;
	private String operation;
	private String contactname;
	private String contactphone;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEquipmentname() {
		return equipmentname;
	}

	public void setEquipmentname(String equipmentname) {
		this.equipmentname = equipmentname;
	}

	public String getEquipmentdesc() {
		return equipmentdesc;
	}

	public void setEquipmentdesc(String equipmentdesc) {
		this.equipmentdesc = equipmentdesc;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

}
