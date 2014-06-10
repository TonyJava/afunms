package com.afunms.config.model;

import com.afunms.common.base.BaseVo;

public class DistrictConfig extends BaseVo {
	private int id;
	private String name; // �û���
	private String desc; // ����
	private String descolor; // ��ɫ

	public String getDesc() {
		return desc;
	}

	public String getDescolor() {
		return descolor;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setDescolor(String descolor) {
		this.descolor = descolor;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
