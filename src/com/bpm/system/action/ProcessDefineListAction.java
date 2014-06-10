package com.bpm.system.action;

/**
 *  Description:
 * 流程定义列表，即已经部署成功的流程列表
 * @author ywx
 *
 */
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.base.JspPage;
import com.bpm.system.service.SystemService;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessDefineListAction extends ActionSupport {
	private static final long serialVersionUID = -3369119189966248651L;
	@Resource
	private SystemService systemService;
	private String perpagenum = "";// 每页需要显示的记录数
	private JspPage jsppage = new JspPage();// 分页查询
	private String jp = ""; // 当前页
	private String result;

	@Override
	public String execute() throws Exception {
		jsppage.setCurrentPage(jp);// 设置当前页面参数
		jsppage.setPerPage(perpagenum);// 设置每页需要获取的记录数
		jsppage = systemService.findPdList(jsppage.getCurrentPage(), jsppage.getPerPage());
		return SUCCESS;
	}

	public String getPerpagenum() {
		return perpagenum;
	}

	public void setPerpagenum(String perpagenum) {
		this.perpagenum = perpagenum;
	}

	public JspPage getJsppage() {
		return jsppage;
	}

	public void setJsppage(JspPage jsppage) {
		this.jsppage = jsppage;
	}

	public String getJp() {
		return jp;
	}

	public void setJp(String jp) {
		this.jp = jp;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
