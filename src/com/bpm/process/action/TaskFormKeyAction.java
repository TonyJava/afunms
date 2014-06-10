package com.bpm.process.action;

/**
 *  Description:
 * 根据taskId，查看流程的form表单
 * @author ywx
 *
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.config.dao.KnowledgebaseDao;
import com.bpm.process.service.ProcessService;
import com.bpm.system.utils.ConstanceUtil;
import com.bpm.system.utils.StringUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
@SuppressWarnings("unchecked")
public class TaskFormKeyAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4003288897427750845L;
	@Resource
	private ProcessService processService;
	private String taskId;
	private String taskExtId;
	private String taskName;
	private String formKey;
	private List<ActivityImpl> backAIList;
	private String jspname;
	private ActivityImpl afterActivityImpl;// 后续流程发起人节点
	private String startTime;
	private Map<String, Object> map;// 流程变量
	private String flag;// 为first表示通过告警信息产生的流程的第一个任务，需 private String
						// afterActivityId;//流程最后环节ID号
	private String afterActivityId;// 流程最后环节ID号
	// private String isbanjie;//是否需要办结，0不需要，1需要
	private String isbanjiebutton;// 是否需要办结按钮，0不需要，1需要

	private void createFile() throws Exception {
		String basepath = ServletActionContext.getServletContext().getRealPath("/template");
		File dir = new File(basepath);
		File files[] = dir.listFiles();
		if (files.length > 200) {
			for (int i = 0; i < files.length; i++) {
				if ("task_form_key.jsp".equals(files[i].getName())) {
					continue;
				} else {
					files[i].delete();
				}
			}
		}
		jspname = Calendar.getInstance().getTime().getTime() + "temp" + ".jsp";
		File file = new File(basepath + "/" + jspname);
		if (!file.exists()) {
			file.createNewFile();
		}
		String path = basepath + "/task_form_key.jsp";
		FileInputStream fis = new FileInputStream(path);
		InputStreamReader ir = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(ir);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		String s;
		while ((s = br.readLine()) != null) {
			bw.write(s);
			bw.write("\r\n");
			bw.flush();
			if ("<!--embed-form-->".equals(s.trim())) {
				if (StringUtil.isNotBlank(formKey)) {
					bw.write(formKey);
					bw.write("\r\n");
					bw.flush();
				}
			}

		}

		bw.close();
		br.close();
	}

	@Override
	public String execute() throws Exception {
		formKey = processService.findFormKeyContent(taskId);
		// backAIList=processService.findBackAIList(taskId);
		if (ConstanceUtil.BEFORE_END.equals(taskName)) {
			backAIList = new ArrayList<ActivityImpl>();
			afterActivityImpl = null;
		} else {
			backAIList = processService.findBackAIList(taskId);
			afterActivityImpl = processService.findAfterActivityImpl(taskId);
		}
		startTime = processService.getProcessInstanceStartTime(taskId);
		map = processService.getProcessVariables(taskId);
		KnowledgebaseDao dao = new KnowledgebaseDao();
		String findselect = dao.selectcontent3();
		ActionContext.getContext().getSession().put("findselect", findselect);
		// afterActivityId=(String) map.get("afterActivityId");
		// 如果流程变量中无此值，再查询最后执行环节
		afterActivityId = (String) map.get("afterActivityId");
		// isbanjie=(String)map.get("isbanjie");
		isbanjiebutton = (String) map.get("isbanjiebutton");
		if (StringUtil.isBlank(afterActivityId)) {
			afterActivityImpl = processService.findAfterActivityImpl(taskId);
		}
		createFile();
		return SUCCESS;
	}

	public String getAfterActivityId() {
		return afterActivityId;
	}

	public ActivityImpl getAfterActivityImpl() {
		return afterActivityImpl;
	}

	public List<ActivityImpl> getBackAIList() {
		return backAIList;
	}

	public String getFlag() {
		return flag;
	}

	public String getFormKey() {
		return formKey;
	}

	public String getIsbanjiebutton() {
		return isbanjiebutton;
	}

	public String getJspname() {
		return jspname;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public String getStartTime() {
		return startTime;
	}

	public String getTaskExtId() {
		return taskExtId;
	}

	public String getTaskId() {
		return taskId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setAfterActivityId(String afterActivityId) {
		this.afterActivityId = afterActivityId;
	}

	public void setAfterActivityImpl(ActivityImpl afterActivityImpl) {
		this.afterActivityImpl = afterActivityImpl;
	}

	public void setBackAIList(List<ActivityImpl> backAIList) {
		this.backAIList = backAIList;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setFormKey(String formKey) {
		this.formKey = formKey;
	}

	public void setIsbanjiebutton(String isbanjiebutton) {
		this.isbanjiebutton = isbanjiebutton;
	}

	public void setJspname(String jspname) {
		this.jspname = jspname;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	/*
	 * public String getIsbanjie() { return isbanjie; }
	 * 
	 * public void setIsbanjie(String isbanjie) { this.isbanjie = isbanjie; }
	 */

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setTaskExtId(String taskExtId) {
		this.taskExtId = taskExtId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
