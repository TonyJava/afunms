package com.afunms.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 * 用于表单中的文件上传
 * 
 * @author hkmw
 * 
 */

@SuppressWarnings("unchecked")
public class Fileupload {

	/**
	 * 保存的目录
	 */
	private String saveDirPath;

	/**
	 * 保存的目录File
	 */
	private File saveDirFile;

	/**
	 * 表单域的列表 现在还没有将表单域写成一个类 故表单域用List封装 第一个元素为字段类型(两种一个是表单类型 一个是文件类型) 第二个元素为字段名
	 * 第三个元素为字段值 (如果是文件类型 其值为文件名 , 文件名是绝对文件名)
	 * 
	 */
	private List formFieldList;

	/**
	 * 
	 */
	public Fileupload() {
		// TODO Auto-generated constructor stub
		init();
	}

	/**
	 * @param saveDirPath
	 */
	public Fileupload(String saveDirPath) {
		this.saveDirPath = saveDirPath;
		init();
	}

	public void doupload(HttpServletRequest request) {
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				DiskFileItemFactory dff = new DiskFileItemFactory();// 创建该对象
				dff.setRepository(this.saveDirFile);// 指定上传文件的临时目录
				dff.setSizeThreshold(1024000);// 指定在内存中缓存数据大小,单位为byte
				ServletFileUpload sfu = new ServletFileUpload(dff);// 创建该对象
				sfu.setFileSizeMax(5000000);// 指定单个上传文件的最大尺寸
				sfu.setSizeMax(10000000);// 指定一次上传多个文件的总尺寸
				List items = null;
				try {
					items = sfu.parseRequest(request);
				} catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Iterator iter = items == null ? null : items.iterator();
				while (iter.hasNext()) {
					List list = new ArrayList();
					FileItem fis = (FileItem) iter.next();// 从集合中获得一个文件流
					if (!fis.isFormField() && fis.getName().length() > 0) {// 过滤掉表单中非文件域
						String fileName = fis.getName().substring(fis.getName().lastIndexOf("\\"));// 获得上传文件的文件名
						BufferedInputStream in = new BufferedInputStream(fis.getInputStream());// 获得文件输入流
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(this.saveDirPath + fileName)));// 获得文件输出流
						Streams.copy(in, out, true);// 开始把文件写到你指定的上传文件夹
						list.add("file");
						list.add(fis.getFieldName());
						list.add(this.saveDirPath + fileName);
					} else if (fis.isFormField()) {
						String value = fis.getString();
						list.add("formField");
						list.add(fis.getFieldName());
						list.add(value);
					}
					formFieldList.add(list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void doupload(HttpServletRequest request, long size) {
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				DiskFileItemFactory dff = new DiskFileItemFactory();// 创建该对象
				dff.setRepository(this.saveDirFile);// 指定上传文件的临时目录
				dff.setSizeThreshold(1024000);// 指定在内存中缓存数据大小,单位为byte
				ServletFileUpload sfu = new ServletFileUpload(dff);// 创建该对象
				sfu.setFileSizeMax(size);// 指定单个上传文件的最大尺寸
				sfu.setSizeMax(10 * size);// 指定一次上传多个文件的总尺寸
				List items = null;
				try {
					items = sfu.parseRequest(request);
				} catch (org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Iterator iter = items == null ? null : items.iterator();
				while (iter.hasNext()) {
					List list = new ArrayList();
					FileItem fis = (FileItem) iter.next();// 从集合中获得一个文件流
					if (!fis.isFormField() && fis.getName().length() > 0) {// 过滤掉表单中非文件域
						String fileName = "";
						if (fis.getName().lastIndexOf("\\") != -1) {
							fileName = fis.getName().substring(fis.getName().lastIndexOf("\\"));// 获得上传文件的文件名
						} else {
							fileName = fis.getName();
						}
						BufferedInputStream in = new BufferedInputStream(fis.getInputStream());// 获得文件输入流
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(this.saveDirPath + fileName)));// 获得文件输出流
						Streams.copy(in, out, true);// 开始把文件写到你指定的上传文件夹
						list.add("file");
						list.add(fis.getFieldName());
						list.add(this.saveDirPath + fileName);
					} else if (fis.isFormField()) {
						String value = fis.getString();
						list.add("formField");
						list.add(fis.getFieldName());
						list.add(value);
					}
					formFieldList.add(list);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List getFormFieldList() {
		return formFieldList;
	}

	public File getSaveDirFile() {
		return saveDirFile;
	}

	public String getSaveDirPath() {
		return saveDirPath;
	}

	public void init() {
		formFieldList = new ArrayList();
		if (saveDirPath != null) {
			this.saveDirFile = new File(this.saveDirPath);
		}
		if (this.saveDirFile == null) {
			return;
		}
		if (!this.saveDirFile.isDirectory() || !this.saveDirFile.exists()) {
			this.saveDirFile.mkdirs();
		}
	}

	public void setFormFieldList(List formFieldList) {
		this.formFieldList = formFieldList;
	}

	public void setSaveDirFile(File saveDirFile) {
		this.saveDirFile = saveDirFile;
	}

	public void setSaveDirPath(String saveDirPath) {
		this.saveDirPath = saveDirPath;
		init();
	}

}
