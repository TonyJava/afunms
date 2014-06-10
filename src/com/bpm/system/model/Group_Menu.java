package com.bpm.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.springframework.stereotype.Component;

@Component
@Entity(name = "group_menu")
public class Group_Menu {

	private String priKey;
	private String groupId;
	private int menu_id;

	@Id
	public String getPriKey() {
		return priKey;
	}

	public void setPriKey(String priKey) {
		this.priKey = priKey;
	}

	@Column(length = 32)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(int menu_id) {
		this.menu_id = menu_id;
	}

}
