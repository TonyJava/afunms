package com.bpm.process.action;

/**
 *  Description:
 * 根据taskId,提交表单请求处理（同意或者驳回任务）
 * @author ywx
 *
 */
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.bpm.process.service.ProcessService;
import com.bpm.system.utils.ConstanceUtil;
import com.bpm.system.utils.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
@SuppressWarnings("unchecked")
public class TaskFormKeySubmitAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9039024333469979953L;
	@Resource
	private ProcessService processService;
	private String taskId;
	private String taskExtId;
	private String taskFormType;
	private String result;
	private String backActivityId;
	private String processInstanceId;
	private String afterActivityId;
	private HttpServletRequest request;
	private String ordersolution;
	private String orderwarntype;// 告警类型如ping
	private Map<String, Object> session;
	private String ordertype;// 节点类型如host
	private String ordernodetype;// 节点操作系统类型

	@Override
	public String execute() throws Exception {
		if (result.equals("驳回")) {
			request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			String reason = request.getParameter("reject");
			processService.backProcess(taskId, backActivityId, reason);
		} else if (result.equals("办结")) {
			if (result.equals(ConstanceUtil.PRO_HALF_MESSAGE)) {
				if (ConstanceUtil.FORM_WRITE.equals(taskFormType)) {
					Map map = new HashMap();
					request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
					Enumeration<String> en = request.getParameterNames();
					String tempkey = "";
					while (en.hasMoreElements()) {
						tempkey = en.nextElement();
						map.put(tempkey, request.getParameter(tempkey));
					}
					map.put("reject", null);
					processService.afterProcess(taskId, afterActivityId, map, ConstanceUtil.FORM_WRITE);
					if (StringUtil.isNotBlank(ordersolution)) {
						session = ActionContext.getContext().getSession();
						User user = (User) session.get(SessionConstant.CURRENT_USER);
						processService.stroeOrderSolution(ordernodetype, ordersolution, orderwarntype, ordertype, user.getUserid());
					}
				}

				else if (ConstanceUtil.FORM_READ_WRITE.equals(taskFormType)) {
					Map map = new HashMap();
					request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
					Enumeration<String> en = request.getParameterNames();
					String tempkey = "";
					while (en.hasMoreElements()) {
						tempkey = en.nextElement();
						map.put(tempkey, request.getParameter(tempkey));
					}
					map.put("reject", null);
					processService.afterProcess(taskId, afterActivityId, map, ConstanceUtil.FORM_READ_WRITE);
					if (StringUtil.isNotBlank(ordersolution)) {
						session = ActionContext.getContext().getSession();
						User user = (User) session.get(SessionConstant.CURRENT_USER);
						processService.stroeOrderSolution(ordernodetype, ordersolution, orderwarntype, ordertype, user.getUserid());
					}
				} else {
					processService.afterProcess(taskId, afterActivityId, null, ConstanceUtil.FORM_READ);
				}

			}
		} else if (ConstanceUtil.FORM_READ.equals(taskFormType))// 仅读
		{
			processService.taskFormKeyComplete(taskId, taskExtId);

		} else if (ConstanceUtil.FORM_READ_WRITE.equals(taskFormType)) {// 读和写
			// 获取需要写入的信息
			Map map = new HashMap();
			request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			Enumeration<String> en = request.getParameterNames();
			String tempkey = "";
			while (en.hasMoreElements()) {
				tempkey = en.nextElement();
				map.put(tempkey, request.getParameter(tempkey));
			}
			map.put("reject", null);
			map.put("isbanjie", "1");
			processService.taskFormKeyComplete(taskId, taskExtId, map);
			if (StringUtil.isNotBlank(ordersolution)) {
				session = ActionContext.getContext().getSession();
				User user = (User) session.get(SessionConstant.CURRENT_USER);
				processService.stroeOrderSolution(ordernodetype, ordersolution, orderwarntype, ordertype, user.getUserid());
			}

		} else if (ConstanceUtil.FORM_WRITE.equals(taskFormType))// 仅写
		{
			Map map = new HashMap();
			request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
			Enumeration<String> en = request.getParameterNames();
			String tempkey = "";
			while (en.hasMoreElements()) {
				tempkey = en.nextElement();
				map.put(tempkey, request.getParameter(tempkey));
			}
			map.put("reject", null);
			map.put("isbanjiebutton", ConstanceUtil.Process_BANJIE);// 0是不需要办结按钮
			map.put("ordersolution", null);
			processService.taskFormKeyComplete(taskId, map);
			if (StringUtil.isNotBlank(ordersolution)) {
				session = ActionContext.getContext().getSession();
				User user = (User) session.get(SessionConstant.CURRENT_USER);
				processService.stroeOrderSolution(ordernodetype, ordersolution, orderwarntype, ordertype, user.getUserid());
			}
		} else {
			return "error";
		}

		return SUCCESS;
	}

	public String getAfterActivityId() {
		return afterActivityId;
	}

	public String getBackActivityId() {
		return backActivityId;
	}

	public String getOrdernodetype() {
		return ordernodetype;
	}

	public String getOrdersolution() {
		return ordersolution;
	}

	public String getOrdertype() {
		return ordertype;
	}

	public String getOrderwarntype() {
		return orderwarntype;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public String getResult() {
		return result;
	}

	public String getTaskExtId() {
		return taskExtId;
	}

	public String getTaskFormType() {
		return taskFormType;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setAfterActivityId(String afterActivityId) {
		this.afterActivityId = afterActivityId;
	}

	public void setBackActivityId(String backActivityId) {
		this.backActivityId = backActivityId;
	}

	public void setOrdernodetype(String ordernodetype) {
		this.ordernodetype = ordernodetype;
	}

	public void setOrdersolution(String ordersolution) {
		this.ordersolution = ordersolution;
	}

	public void setOrdertype(String ordertype) {
		this.ordertype = ordertype;
	}

	public void setOrderwarntype(String orderwarntype) {
		this.orderwarntype = orderwarntype;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setTaskExtId(String taskExtId) {
		this.taskExtId = taskExtId;
	}

	public void setTaskFormType(String taskFormType) {
		this.taskFormType = taskFormType;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

}
