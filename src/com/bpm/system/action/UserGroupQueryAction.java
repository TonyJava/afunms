package com.bpm.system.action;

import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class UserGroupQueryAction extends BaseAction {

	private List<GroupEntity> groups;
	private String userId;
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		groups = systemService.getGroupsByUser(userId);
		return SUCCESS;
	}

	public List<GroupEntity> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupEntity> groups) {
		this.groups = groups;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}