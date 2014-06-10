package com.bpm.system.action;

/**
 *  Description:
 * 根据部署的ID号，删除流程
 * @author ywx
 *
 */
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;
import com.bpm.system.utils.StringUtil;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessDefineDelAction extends ActionSupport {
	private static final long serialVersionUID = -3786487011613224892L;
	@Resource
	private SystemService systemService;
	private String deploymentId;

	@Override
	public String execute() throws Exception {
		if (StringUtil.isBlank(deploymentId))
			return ERROR;
		systemService.deletePdById(deploymentId);
		return SUCCESS;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

}
