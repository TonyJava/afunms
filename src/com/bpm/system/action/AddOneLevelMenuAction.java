package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.model.Menu;
import com.bpm.system.service.SystemService;
import com.opensymphony.xwork2.ModelDriven;

@Controller
@Scope("prototype")
public class AddOneLevelMenuAction extends BaseAction implements ModelDriven<Menu> {

	private String result;
	@Resource
	private SystemService systemService;
	private Menu menu = new Menu();

	@Override
	public String execute() throws Exception {
		result = systemService.addOneLevelMenu(menu);
		return result;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public Menu getModel() {

		return menu;
	}

}
