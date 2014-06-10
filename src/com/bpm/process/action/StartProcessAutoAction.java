package com.bpm.process.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.bpm.process.service.ProcessService;
import com.bpm.system.utils.ConstanceUtil;
import com.bpm.system.utils.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 通过告警信息生成告警信息流程单
 * 
 * @author HXL 2011-12-11
 */
@Controller
@Scope("prototype")
@SuppressWarnings("unchecked")
public class StartProcessAutoAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5995736972568443273L;
	private String nodeId;// 节点ID
	private String content;// 告警内容
	private String subtype;// 类型
	private String subentity;// 告警类型
	private String level;// 告警级别
	private String processId;// 流程ID

	private Map session;// session对象
	private String message;// 处理结果
	private String eventid;// 用于是否生成订单
	private String manual;// 是否为手动启动工单.值为1表示为手动填写工单流程。
	@Resource
	private ProcessService processService;

	@Override
	public String execute() throws Exception {
		if (StringUtil.isBlank(processId)) {
			return ERROR;
		}
		session = ActionContext.getContext().getSession();
		User user = (User) session.get(SessionConstant.CURRENT_USER);

		if (user == null) {
			return ERROR;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isbanjiebutton", ConstanceUtil.NO_Process_BANJIE);// 0是不需要办结按钮
		/*
		 * map.put("processId",processId ); map.put("userId", user.getUserid());
		 * map.put("nodeId", nodeId); map.put("content", content);
		 * map.put("subtype", subtype); map.put("subentity", subentity);
		 * map.put("level", level); map.put("ostype",
		 * processService.getNodeOstypeById(nodeId));
		 */

		/*
		 * if("1".equals(manual)) {
		 * //result=processService.isStart(processId,user.getUserid());
		 * map.put("userId", user.getUserid()); map.put("warnprocess", "false");
		 * }else {
		 */
		map.put("processId", processId);
		map.put("userId", user.getUserid());
		map.put("nodeId", nodeId);
		map.put("content", content);
		map.put("subtype", subtype);
		map.put("subentity", subentity);
		map.put("level", level);
		map.put("ostype", processService.getNodeOstypeById(nodeId));
		map.put("warnprocess", "true");
		// }
		message = processService.startProcessAuto(processId, map);
		if (StringUtil.isNotBlank(eventid)) {
			processService.setEventOrderFlag(eventid);
		}
		return SUCCESS;
	}

	public String getContent() {
		return content;
	}

	public String getEventid() {
		return eventid;
	}

	public String getLevel() {
		return level;
	}

	public String getManual() {
		return manual;
	}

	public String getMessage() {
		return message;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getProcessId() {
		return processId;
	}

	public ProcessService getProcessService() {
		return processService;
	}

	public String getSubentity() {
		return subentity;
	}

	public String getSubtype() {
		return subtype;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setEventid(String eventid) {
		this.eventid = eventid;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setManual(String manual) {
		this.manual = manual;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public void setProcessService(ProcessService processService) {
		this.processService = processService;
	}

	public void setSubentity(String subentity) {
		this.subentity = subentity;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

}
