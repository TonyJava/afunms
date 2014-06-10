package com.bpm.process.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.base.JspPage;
import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 订单综合查询
 * 
 * @author HXL 2012-12-13
 */

@Controller
@Scope("prototype")
public class HistoricProcessInstanceQueryAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6658956154977827081L;
	private String status;// 状态
	private String startuser;// 启动者
	private String starttime1;// 开始时间1
	private String starttime2;// 开始时间2
	private String perpagenum = "";// 每页需要显示的记录数
	private JspPage jsppage = new JspPage();// 分页查询
	private String jp = ""; // 当前页
	@Resource
	private ProcessService processService;

	@Override
	public String execute() throws Exception {
		jsppage.setCurrentPage(jp);// 设置当前页面参数
		jsppage.setPerPage(perpagenum);// 设置每页需要获取的记录数
		jsppage = processService.queryHistoricProcessInstance(status, startuser, starttime1, starttime2, jsppage.getCurrentPage(), jsppage.getPerPage());
		return SUCCESS;
	}

	public String getJp() {
		return jp;
	}

	public JspPage getJsppage() {
		return jsppage;
	}

	public String getStarttime1() {
		return starttime1;
	}

	public String getStarttime2() {
		return starttime2;
	}

	public String getStartuser() {
		return startuser;
	}

	public String getStatus() {
		return status;
	}

	public void setJp(String jp) {
		this.jp = jp;
	}

	public void setJsppage(JspPage jsppage) {
		this.jsppage = jsppage;
	}

	public void setStarttime1(String starttime1) {
		this.starttime1 = starttime1;
	}

	public void setStarttime2(String starttime2) {
		this.starttime2 = starttime2;
	}

	public void setStartuser(String startuser) {
		this.startuser = startuser;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
