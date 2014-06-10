package com.bpm.process.action;

import java.io.FileInputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.initialize.ResourceCenter;
import com.bpm.process.model.ProcessStatisticalsModel;
import com.bpm.process.service.ProcessService;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessStatisticalsPieImgAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5871942465416330297L;
	private String person;// 部门和人员方式，0人员，1岗位
	@Resource
	private ProcessService processService;
	private List<ProcessStatisticalsModel> list;
	private String imgname;
	FileInputStream is = null;
	ServletOutputStream os = null;

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		HttpServletResponse response = ServletActionContext.getResponse();
		list = (List<ProcessStatisticalsModel>) ActionContext.getContext().getSession().get("listPsm");
		imgname = processService.drawPie(list, person);
		String path = ResourceCenter.getInstance().getSysPath() + "resource\\image\\jfreechart\\" + imgname + ".png";
		response.setContentType("multipart/form-data");
		response.setHeader("Pragma", "No-Cache");
		response.setHeader("Cache-Control", "No-Cache");
		response.setDateHeader("Expires", 0);
		is = new FileInputStream(path);
		os = response.getOutputStream();
		if (is != null && os != null) {
			byte[] bt = new byte[1024];
			int len;
			while ((len = is.read(bt)) != -1) {
				os.write(bt, 0, len);
			}
			os.close();
			is.close();
		}

		return null;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

}
