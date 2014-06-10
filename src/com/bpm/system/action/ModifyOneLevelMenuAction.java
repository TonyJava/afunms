package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class ModifyOneLevelMenuAction extends BaseAction {

	private int one_menu_id;

	private String one_menu_name;

	private String one_menu_url;
	private String result;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		return result = systemService.modifyOneLevelMenu(one_menu_name, one_menu_url, one_menu_id);
	}

	public int getOne_menu_id() {
		return one_menu_id;
	}

	public void setOne_menu_id(int one_menu_id) {
		this.one_menu_id = one_menu_id;
	}

	public String getOne_menu_name() {
		return one_menu_name;
	}

	public void setOne_menu_name(String one_menu_name) {
		this.one_menu_name = one_menu_name;
	}

	public String getOne_menu_url() {
		return one_menu_url;
	}

	public void setOne_menu_url(String one_menu_url) {
		this.one_menu_url = one_menu_url;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
