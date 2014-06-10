package com.bpm.system.action;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.utils.StringUtil;

@Controller
@Scope("prototype")
public class ForwardJspAction extends BaseAction {

	private String jsp;

	@Override
	public String execute() throws Exception {
		jsp = StringUtil.isBlank(jsp) ? "error" : jsp;
		return jsp;
	}

	public String getJsp() {
		return jsp;
	}

	public void setJsp(String jsp) {
		this.jsp = jsp;
	}

}
