package com.bpm.system.action;

/**
 *  Description:
 * 部署流程
 * @author ywx
 *
 */
import java.io.File;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class ProcessDepAction extends ActionSupport {
	private static final long serialVersionUID = -5186406541037629319L;

	private File file;// 上传文件
	private String fileContentType;// 类型
	private String fileFileName;// 文件名
	private String result;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
