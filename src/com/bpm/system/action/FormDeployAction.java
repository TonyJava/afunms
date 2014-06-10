package com.bpm.system.action;

/**
 *  Description:
 * 上传表单
 * @author ywx
 *
 */
import java.io.File;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.bpm.system.service.SystemService;
import com.opensymphony.xwork2.ActionSupport;

@Controller
@Scope("prototype")
public class FormDeployAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7095877951158779944L;
	private File file;// 上传文件
	private String fileContentType;// 类型
	private String fileFileName;// 文件名
	private File filepic;// 图片
	private String filepicContentType;// 类型
	private String filepicFileName;// 文件名
	private String result;

	@Resource
	private SystemService systemService;

	@Override
	public String execute() throws Exception {
		result = systemService.deployForm(file, fileFileName, filepic, filepicFileName);
		return result;
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

	public File getFilepic() {
		return filepic;
	}

	public void setFilepic(File filepic) {
		this.filepic = filepic;
	}

	public String getFilepicContentType() {
		return filepicContentType;
	}

	public void setFilepicContentType(String filepicContentType) {
		this.filepicContentType = filepicContentType;
	}

	public String getFilepicFileName() {
		return filepicFileName;
	}

	public void setFilepicFileName(String filepicFileName) {
		this.filepicFileName = filepicFileName;
	}

}
