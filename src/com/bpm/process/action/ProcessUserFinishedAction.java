package com.bpm.process.action;

/**
 * 获取已办工单
 */
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.base.JspPage;
import com.afunms.common.util.SessionConstant;
import com.afunms.system.model.User;
import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessUserFinishedAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8870471888420540776L;
	@Resource
	private ProcessService processService;
	private Map<String, Object> session;
	private String perpagenum = "";// 每页需要显示的记录数
	private JspPage jsppage = new JspPage();// 分页查询
	private String jp = ""; // 当前页

	@Override
	public String execute() throws Exception {
		session = ActionContext.getContext().getSession();
		User user = (User) session.get(SessionConstant.CURRENT_USER);
		if (user == null) {
			return ERROR;
		}
		jsppage.setCurrentPage(jp);// 设置当前页面参数
		jsppage.setPerPage(perpagenum);// 设置每页需要获取的记录数
		jsppage = processService.findHistoricProcessInstance(user.getUserid(), jsppage.getCurrentPage(), jsppage.getPerPage());
		return SUCCESS;
	}

	public String getJp() {
		return jp;
	}

	public JspPage getJsppage() {
		return jsppage;
	}

	public String getPerpagenum() {
		return perpagenum;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	public void setJp(String jp) {
		this.jp = jp;
	}

	public void setJsppage(JspPage jsppage) {
		this.jsppage = jsppage;
	}

	public void setPerpagenum(String perpagenum) {
		this.perpagenum = perpagenum;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
