package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.model.Menu;
import com.bpm.system.service.SystemService;

 
@Controller
@Scope("prototype")
public class QuerySecondMenuAction extends BaseAction {

	private int parent_id;
	private List<Menu> list;
	@Resource
	private SystemService systemService;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		list = systemService.querysecmenu(parent_id);
		return SUCCESS;
	}

	public int getParent_id() {
		return parent_id;
	}

	public void setParent_id(int parent_id) {
		this.parent_id = parent_id;
	}

	public List<Menu> getList() {
		return list;
	}

	public void setList(List<Menu> list) {
		this.list = list;
	}

}
