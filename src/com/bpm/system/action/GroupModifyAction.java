package com.bpm.system.action;

import javax.annotation.Resource;

import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;
import com.opensymphony.xwork2.ModelDriven;

@Controller
@Scope("prototype")
public class GroupModifyAction extends BaseAction implements ModelDriven<GroupEntity> {

	private GroupEntity model = new GroupEntity();
	private String result;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {

		return result = systemService.addOrUpdateGroup(model);
	}

	public GroupEntity getModel() {
		return model;
	}

	public void setModel(GroupEntity model) {
		this.model = model;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
