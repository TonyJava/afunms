package com.bpm.design.action;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.common.base.JspPage;
import com.bpm.design.dao.DesignDao;
import com.bpm.system.action.BaseAction;

/**
 * 
 * Description:��ȡ��������ļ����б� QueryDesignAction.java Create on 2012-11-9 ����10:36:19
 * 
 * @author hexinlin Copyright (c) 2012 DHCC Company,Inc. All Rights Reserved.
 */
@Controller
@Scope("prototype")
public class QueryDesignAction extends BaseAction {

	private String perpagenum = "";// ÿҳ��Ҫ��ʾ�ļ�¼��
	private JspPage jsppage = new JspPage();// ��ҳ��ѯ
	private String jp = ""; // ��ǰҳ

	@Override
	public String execute() throws Exception {
		jsppage.setCurrentPage(jp);// ���õ�ǰҳ�����
		jsppage.setPerPage(perpagenum);// ����ÿҳ��Ҫ��ȡ�ļ�¼��
		jsppage = new DesignDao().queryDesign(jsppage.getCurrentPage(), jsppage.getPerPage());
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
