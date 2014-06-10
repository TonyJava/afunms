package com.afunms.system.model;

import java.util.List;

import com.afunms.common.base.BaseVo;

public class Node extends BaseVo {
	public String ID;

	public String Name;

	public String Desc;

	public List<Node> Children;

	public Node Parent;

	public String Pid;

	public List<Node> getChildren() {
		return Children;
	}

	public String getDesc() {
		return Desc;
	}

	public String getID() {
		return ID;
	}

	public String getName() {
		return Name;
	}

	public Node getParent() {
		return Parent;
	}

	public String getPid() {
		return Pid;
	}

	public void setChildren(List<Node> children) {
		Children = children;
	}

	public void setDesc(String desc) {
		Desc = desc;
	}

	public void setID(String id) {
		ID = id;
	}

	public void setName(String name) {
		Name = name;
	}

	public void setParent(Node parent) {
		Parent = parent;
	}

	public void setPid(String pid) {
		Pid = pid;
	}

}