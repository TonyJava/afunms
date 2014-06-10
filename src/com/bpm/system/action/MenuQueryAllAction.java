package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.model.Menu;
import com.bpm.system.service.SystemService;

 
@Controller
@Scope("prototype")
public class MenuQueryAllAction extends BaseAction {

	private List<Menu> list; 
	@Resource
	private SystemService systemService;

	public String execute() throws Exception {
		list = systemService.queryAllMenu();
		return SUCCESS;
	}

	public List<Menu> getList() {
		return list;
	}

	public void setList(List<Menu> list) {
		this.list = list;
	}

}
