package com.bpm.system.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.base.JspPage;
import com.bpm.system.service.SystemService;

@Controller
@Scope("prototype")
public class FormListAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3349484350058498445L;

	private String perpagenum = "";// 每页需要显示的记录数
	private JspPage jsppage = new JspPage();// 分页查询
	private String jp = ""; // 当前页
	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		jsppage.setCurrentPage(jp);// 设置当前页面参数
		jsppage.setPerPage(perpagenum);// 设置每页需要获取的记录数
		jsppage = systemService.findFormList(jsppage.getCurrentPage(), jsppage.getPerPage());
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

}
