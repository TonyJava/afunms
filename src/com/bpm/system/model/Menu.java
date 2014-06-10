package com.bpm.system.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.stereotype.Component;

@Component
@Entity(name = "bpm_menu")
public class Menu implements Serializable {

	private int menu_id;
	private String menu_name;

	private String menu_url;
	private int menu_seq;

	private int sort;

	private Menu parent;
	private int childrennum;

	@Id
	public int getMenu_id() {
		return menu_id;
	}

	public void setMenu_id(int menu_id) {
		this.menu_id = menu_id;
	}

	@Column(length = 32)
	public String getMenu_name() {
		return menu_name;
	}

	public void setMenu_name(String menu_name) {
		this.menu_name = menu_name;
	}

	@Column(length = 100)
	public String getMenu_url() {
		return menu_url;
	}

	public void setMenu_url(String menu_url) {
		this.menu_url = menu_url;
	}

	public int getMenu_seq() {
		return menu_seq;
	}

	public void setMenu_seq(int menu_seq) {
		this.menu_seq = menu_seq;
	}

	@ManyToOne
	@JoinColumn(name = "parent_id")
	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getChildrennum() {
		return childrennum;
	}

	public void setChildrennum(int childrennum) {
		this.childrennum = childrennum;
	}

}
