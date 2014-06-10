package com.bpm.system.action;

/**
 *  Description:
 * 上传表单
 * @author ywx
 *
 */
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class FormDepAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2334180981996043147L;
	@Resource

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
}
