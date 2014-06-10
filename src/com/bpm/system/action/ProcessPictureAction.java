package com.bpm.system.action;

/**
 *  Description:
 * 单个流程图片
 * @author ywx
 *
 */
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;
import com.bpm.system.utils.StringUtil;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessPictureAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4480171212078509176L;
	private String deploymentId;
	private String resourceName;

	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		if (StringUtil.isBlank(deploymentId) || StringUtil.isBlank(resourceName)) {
			return null;
		}
		HttpServletResponse response = ServletActionContext.getResponse();
		systemService.findSourcebyPdId(deploymentId, resourceName, response);
		return null;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

}
