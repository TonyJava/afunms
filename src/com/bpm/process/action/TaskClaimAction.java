package com.bpm.process.action;

/**
 *  Description:
 * 根据taskId签收任务
 * @author ywx
 *
 */
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class TaskClaimAction extends ActionSupport {
	private static final long serialVersionUID = -8887163811822202916L;
	@Resource
	private ProcessService processService;
	private String taskId;
	private Map<String, Object> session;

	@Override
	public String execute() throws Exception {
		session = ActionContext.getContext().getSession();
		User user = (User) session.get(SessionConstant.CURRENT_USER);
		if (user == null) {
			return ERROR;
		}
		processService.claimTask(taskId, user.getUserid());
		return SUCCESS;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
