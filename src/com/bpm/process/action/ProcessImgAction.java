package com.bpm.process.action;

/**
 * Description:Á÷³ÌÍ¼Æ¬
 * @author ywx
 *
 */

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessImgAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6126438217128220606L;
	@Resource
	private ProcessService processService;
	private String processInstanceId;
	private String processDefinitionId;

	@Override
	public String execute() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		processService.getImageStream(processDefinitionId, processInstanceId, response);
		return null;
	}

	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

}
