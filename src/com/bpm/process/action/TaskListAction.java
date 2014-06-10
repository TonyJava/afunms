package com.bpm.process.action;

/**
 *  Description:
 * @author ywx
 *
 *
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
@SuppressWarnings("unchecked")
public class TaskListAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2483572461569845677L;
	@Resource
	private ProcessService processService;
	private String perpagenum = "";// ÿҳ��Ҫ��ʾ�ļ�¼��
	private JspPage jsppage = new JspPage();// ��ҳ��ѯ
	private String jp = ""; // ��ǰҳ
	private Map<String, Object> session;

	@Override
	public String execute() throws Exception {
		session = ActionContext.getContext().getSession();
		User user = (User) session.get(SessionConstant.CURRENT_USER);
		if (user == null) {
			return ERROR;
		}
		jsppage.setCurrentPage(jp);// ���õ�ǰҳ�����
		jsppage.setPerPage(perpagenum);// ����ÿҳ��Ҫ��ȡ�ļ�¼��
		jsppage = processService.findTaskList(user.getUserid(), jsppage.getCurrentPage(), jsppage.getPerPage());
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
