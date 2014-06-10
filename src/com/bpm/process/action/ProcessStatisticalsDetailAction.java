package com.bpm.process.action;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.base.JspPage;
import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessStatisticalsDetailAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8044134931883095043L;
	@Resource
	private ProcessService processService;
	private String type;// 参与方式 (任务和实例方式)，0任务，1实例
	private String person;// 部门和人员方式，0人员，1岗位
	private String startdate;
	private String todate;
	private String exectname;
	private String perpagenum = "";// 每页需要显示的记录数
	private JspPage jsppage = new JspPage();// 分页查询
	private String jp = ""; // 当前页

	@Override
	public String execute() throws Exception {
		jsppage.setCurrentPage(jp);// 设置当前页面参数
		jsppage.setPerPage(perpagenum);// 设置每页需要获取的记录数
		jsppage = processService.queryProcessStatisticalsDetail(exectname, type, person, startdate, todate, jsppage.getCurrentPage(), jsppage.getPerPage());
		return SUCCESS;
	}

	public String getExectname() {
		return exectname;
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

	public String getPerson() {
		return person;
	}

	public String getStartdate() {
		return startdate;
	}

	public String getTodate() {
		return todate;
	}

	public String getType() {
		return type;
	}

	public void setExectname(String exectname) {
		this.exectname = exectname;
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

	public void setPerson(String person) {
		this.person = person;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public void setTodate(String todate) {
		this.todate = todate;
	}

	public void setType(String type) {
		this.type = type;
	}

}
