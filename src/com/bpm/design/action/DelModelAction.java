package com.bpm.design.action;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.design.dao.DesignDao;
import com.bpm.system.action.BaseAction;

/**
 * 
 * Description: 删除流程模版 DelModelAction.java Create on 2012-11-14 下午3:56:03
 * 
 * @author hexinlin Copyright (c) 2012 DHCC Company,Inc. All Rights Reserved.
 */
@Controller
@Scope("prototype")
public class DelModelAction extends BaseAction {

	private String checkbox[];

	@Override
	public String execute() throws Exception {
		DesignDao dao = new DesignDao();

		return dao.delProcessModels(checkbox);
	}

	public String[] getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String[] checkbox) {
		this.checkbox = checkbox;
	}

}
