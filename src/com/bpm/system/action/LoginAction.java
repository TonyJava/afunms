package com.bpm.system.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.model.Menu;
import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class LoginAction extends BaseAction implements SessionAware {

	private String username;
	private String password;
	private Map<String, Object> session;
	@Resource
	private SystemService systemService;

	private String result;
	private List<Menu> list;

	@Override
	public String execute() throws Exception {
		result = systemService.UserLogin(username, password, session);
		list = new ArrayList<Menu>();
		if ("success".equals(result)) {
			list = systemService.queryAllMenu();
		}
		return result;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public List<Menu> getList() {
		return list;
	}

	public void setList(List<Menu> list) {
		this.list = list;
	}

}
