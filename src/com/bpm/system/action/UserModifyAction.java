package com.bpm.system.action;

import javax.annotation.Resource;

import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;
import com.opensymphony.xwork2.ModelDriven;

@Controller
@Scope("prototype")
public class UserModifyAction extends BaseAction implements ModelDriven<UserEntity> {

	private UserEntity model = new UserEntity();
	private String result;
	private String flag;
	private String[] addgroups;
	private String[] modifygroups;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		return result = systemService.saveOrUpdateUser(model, addgroups, modifygroups, flag);
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public UserEntity getModel() {
		return model;
	}

	public void setModel(UserEntity model) {
		this.model = model;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String[] getAddgroups() {
		return addgroups;
	}

	public void setAddgroups(String[] addgroups) {
		this.addgroups = addgroups;
	}

	public String[] getModifygroups() {
		return modifygroups;
	}

	public void setModifygroups(String[] modifygroups) {
		this.modifygroups = modifygroups;
	}

}
