package com.bpm.system.action;

import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.afunms.initialize.ResourceCenter;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class FormImgAction extends ActionSupport {
	private String id;// 部门和人员方式，0人员，1岗位
	FileInputStream is = null;
	ServletOutputStream os = null;

	@Override
	public String execute() throws Exception {
		String[] data = id.split("\\.");
		if (data.length < 2)
			return null;
		HttpServletResponse response = ServletActionContext.getResponse();
		String path = ResourceCenter.getInstance().getSysPath() + "images\\bpm\\" + data[0] + ".jpg";
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
