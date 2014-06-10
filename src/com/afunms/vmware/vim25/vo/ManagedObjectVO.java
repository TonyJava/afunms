package com.afunms.vmware.vim25.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * ManagedObjectReference及动态属性的映射对象,主要为了保存ManagedObjectReference的父子关系
 * 
 * @author LXL
 * 
 */
public class ManagedObjectVO {

	private String id;
	private String name;
	private String type;

	private String parent;
	private String treepath;
	private List<ManagedObjectVO> children;

	public ManagedObjectVO(String id, String name, String type, String parent) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.parent = parent;

		this.treepath = "";
		this.children = new ArrayList<ManagedObjectVO>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getParent() {
		return parent;
	}

	public String getTreepath() {
		return treepath;
	}

	public void setTreepath(String treepath) {
		this.treepath = treepath;
	}

	public List<ManagedObjectVO> getChildren() {
		return children;
	}

	public void addChild(ManagedObjectVO vimRes) {
		children.add(vimRes);
	}
}
