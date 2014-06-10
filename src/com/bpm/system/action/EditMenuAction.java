package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class EditMenuAction extends BaseAction {

	private String result;
	private int edit_menu_id;
	private String edit_menu_name;
	private String edit_menu_url;
	private int edit_parent_id;
	private int del_menu_id;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		result = systemService.editSecMenu(edit_menu_id, edit_menu_name, edit_menu_url, edit_parent_id, del_menu_id);
		return SUCCESS;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public int getEdit_menu_id() {
		return edit_menu_id;
	}

	public void setEdit_menu_id(int edit_menu_id) {
		this.edit_menu_id = edit_menu_id;
	}

	public String getEdit_menu_name() {
		return edit_menu_name;
	}

	public void setEdit_menu_name(String edit_menu_name) {
		this.edit_menu_name = edit_menu_name;
	}

	public String getEdit_menu_url() {
		return edit_menu_url;
	}

	public void setEdit_menu_url(String edit_menu_url) {
		this.edit_menu_url = edit_menu_url;
	}

	public int getEdit_parent_id() {
		return edit_parent_id;
	}

	public void setEdit_parent_id(int edit_parent_id) {
		this.edit_parent_id = edit_parent_id;
	}

	public int getDel_menu_id() {
		return del_menu_id;
	}

	public void setDel_menu_id(int del_menu_id) {
		this.del_menu_id = del_menu_id;
	}

}
