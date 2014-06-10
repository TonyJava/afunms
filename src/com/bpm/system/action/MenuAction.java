package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.model.Menu;
import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class MenuAction extends BaseAction {

	private List<Menu> list;
	private String result;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		list = systemService.queryOneLevelMenu();
		return SUCCESS;
	}

	public List<Menu> getList() {
		return list;
	}

	public String getResult() {
		return result;
	}

	public void setList(List<Menu> list) {
		this.list = list;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
