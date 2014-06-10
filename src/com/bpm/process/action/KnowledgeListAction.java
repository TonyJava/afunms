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
import com.afunms.config.dao.KnowledgebaseDao;
import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class KnowledgeListAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5426153601985990225L;
	@Resource
	private ProcessService processService;
	private String perpagenum = "";// 每页需要显示的记录数
	private JspPage jsppage = new JspPage();// 分页查询
	private String jp = ""; // 当前页
	private String categorycon;// 类别
	private String entitycon;// 类型
	private String subentitycon;// 指标
	private String wordkey;// 关键字

	@Override
	public String execute() throws Exception {
		jsppage.setCurrentPage(jp);// 设置当前页面参数
		jsppage.setPerPage(perpagenum);// 设置每页需要获取的记录数
		KnowledgebaseDao dao = new KnowledgebaseDao();
		String findselect = dao.selectcontent();
		ActionContext.getContext().getSession().put("findselect", findselect);
		jsppage = processService.queryKnowledge(categorycon, entitycon, subentitycon, wordkey, jsppage.getCurrentPage(), jsppage.getPerPage());
		return SUCCESS;
	}

	public String getCategorycon() {
		return categorycon;
	}

	public String getEntitycon() {
		return entitycon;
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

	public String getSubentitycon() {
		return subentitycon;
	}

	public String getWordkey() {
		return wordkey;
	}

	public void setCategorycon(String categorycon) {
		this.categorycon = categorycon;
	}

	public void setEntitycon(String entitycon) {
		this.entitycon = entitycon;
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

	public void setSubentitycon(String subentitycon) {
		this.subentitycon = subentitycon;
	}

	public void setWordkey(String wordkey) {
		this.wordkey = wordkey;
	}

}
