package com.bpm.system.action;

/**
 *  Description:
 * 根据流程的ID号，启动流程
 * @author ywx
 *
 */
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.bpm.system.service.SystemService;
import com.bpm.system.utils.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessDefineStartAction extends ActionSupport {
	private static final long serialVersionUID = -3786487011613224892L;
	@Resource
	private SystemService systemService;
	private String processId;
	private Map<String, Object> session;
	private String result;

	@Override
	public String execute() throws Exception {
		if (StringUtil.isBlank(processId))
			return ERROR;
		session = ActionContext.getContext().getSession();
		User user = (User) session.get(SessionConstant.CURRENT_USER);
		if (user == null)
			return ERROR;
		result = systemService.startPdById(processId, user.getUserid());
		return SUCCESS;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
