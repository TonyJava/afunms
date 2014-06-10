package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.model.UserModel;
import com.bpm.system.service.SystemService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 获取用户列表
 * 
 * @author ywx
 * 
 */
@Controller
@Scope("prototype")
public class UserJsonAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8498694269361354654L;
	private List<UserModel> userList;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		userList = systemService.queryAllUser();
		return SUCCESS;
	}

	public List<UserModel> getUserList() {
		return userList;
	}

	public void setUserList(List<UserModel> userList) {
		this.userList = userList;
	}

}
