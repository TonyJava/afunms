package com.bpm.process.action;

/**
 *  Description:
 * 已结束的流程列表
 * @author ywx
 *
 */
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.base.JspPage;
import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class HistoricListAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3580781028603344307L;
	@Resource
	private ProcessService processService;
	private String perpagenum = "";// 每页需要显示的记录数
	private JspPage jsppage = new JspPage();// 分页查询
	private String jp = ""; // 当前页

	@Override
	public String execute() throws Exception {
		jsppage.setCurrentPage(jp);// 设置当前页面参数
		jsppage.setPerPage(perpagenum);// 设置每页需要获取的记录数
		jsppage = processService.findhistoricProcessInstanceList(jsppage.getCurrentPage(), jsppage.getPerPage());
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

	public void setJp(String jp) {
		this.jp = jp;
	}

	public void setJsppage(JspPage jsppage) {
		this.jsppage = jsppage;
	}

	public void setPerpagenum(String perpagenum) {
		this.perpagenum = perpagenum;
	}

}
