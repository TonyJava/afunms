package com.afunms.application.ajaxManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.system.dao.AlertEmailDao;
import com.afunms.system.model.AlertEmail;

public class EmailAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public void execute(String action) {
		if (action.equals("getEmailList")) {
			getEmailList();
		} else if (action.equals("addEmail")) {
			addEmail();
		} else if (action.equals("deleteEmail")) {
			deleteEmail();
		} else if (action.equals("beforeEditEmail")) {
			beforeEditEmail();
		} else if (action.equals("addAlertEmail")) {
			addAlertEmail();
		} else if (action.equals("cancelAlertEmail")) {
			cancelAlertEmail();
		} else if (action.equals("updateEmail")) {
			updateEmail();
		}
	}

	private void updateEmail() {
		AlertEmail vo = new AlertEmail();
		vo.setId(getParaIntValue("id"));
		vo.setUsername(getParaValue("name"));
		vo.setPassword(getParaValue("pwd"));
		vo.setSmtp(getParaValue("smtp"));
		vo.setUsedflag(getParaIntValue("usedflag"));
		vo.setMailAddress(getParaValue("emailaddress"));

		AlertEmailDao dao = new AlertEmailDao();
		boolean flag = true;
		try {
			flag = dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}

		if (flag) {
			out.print("修改成功");
		} else {
			out.print("修改失败");
		}
		out.flush();
	}

	private void cancelAlertEmail() {
		AlertEmail vo = new AlertEmail();
		AlertEmailDao configdao = new AlertEmailDao();
		boolean flag = true;
		try {
			vo = (AlertEmail) configdao.findByID(getParaValue("id"));
			vo.setUsedflag(0);
			configdao = new AlertEmailDao();
			flag = configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (configdao != null) {
				configdao.close();
			}
		}
		if (flag) {
			out.print("取消启用成功");
		} else {
			out.print("取消启用失败");
		}
		out.flush();
	}

	private void addAlertEmail() {
		AlertEmail vo = new AlertEmail();
		AlertEmailDao configdao = new AlertEmailDao();
		boolean flag = true;
		try {
			vo = (AlertEmail) configdao.findByID(getParaValue("id"));
			vo.setUsedflag(1);
			configdao = new AlertEmailDao();
			flag = configdao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (configdao != null) {
				configdao.close();
			}
		}
		if (flag) {
			out.print("启用成功");
		} else {
			out.print("启用失败");
		}
		out.flush();
	}

	private void beforeEditEmail() {
		String id = getParaValue("id");
		AlertEmail vo = new AlertEmail();

		if (id != null) {
			AlertEmailDao dao = new AlertEmailDao();
			try {
				vo = (AlertEmail) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (vo != null) {
			String usedflag = "启用";
			if (vo.getUsedflag() == 0)
				usedflag = "未启用";
			jsonString.append("{\"id\":\"");
			jsonString.append(vo.getId());
			jsonString.append("\",");

			jsonString.append("\"username\":\"");
			jsonString.append(vo.getUsername());
			jsonString.append("\",");

			jsonString.append("\"pwd\":\"");
			jsonString.append(vo.getPassword());
			jsonString.append("\",");

			jsonString.append("\"smtp\":\"");
			jsonString.append(vo.getSmtp());
			jsonString.append("\",");

			jsonString.append("\"emailaddress\":\"");
			String mailAddress = vo.getMailAddress();
			if (mailAddress == null || mailAddress.equals("")) {
				mailAddress = "";
			}
			jsonString.append(mailAddress);
			jsonString.append("\",");

			jsonString.append("\"usedflagvalue\":\"");
			jsonString.append(vo.getUsedflag());
			jsonString.append("\",");

			jsonString.append("\"usedflag\":\"");
			jsonString.append(usedflag);
			jsonString.append("\"}");
		}
		jsonString.append("],total : 1 }");
		out.print(jsonString.toString());
		out.flush();

	}

	private void addEmail() {
		String name = getParaValue("name");
		String smtp = getParaValue("smtp");
		String pwd = getParaValue("pwd");
		int usedflag = getParaIntValue("usedflag");
		String emailaddress = getParaValue("emailaddress");
		AlertEmail vo = new AlertEmail();
		vo.setUsername(name);
		vo.setPassword(pwd);
		vo.setSmtp(smtp);
		vo.setUsedflag(usedflag);
		vo.setMailAddress(emailaddress);
		AlertEmailDao dao = new AlertEmailDao();
		int result = 0;
		try {
			result = dao.save(vo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		if (result > 0) {
			out.print("添加成功");
		} else {
			out.print("添加失败");
		}
		out.flush();
	}

	private void deleteEmail() {
		String idString = getParaValue("idString");
		String[] ids = null;
		if (idString != null) {
			ids = idString.split(";");
		}
		boolean result = false;
		if (ids != null & ids.length > 0) {
			AlertEmailDao dao = new AlertEmailDao();
			try {
				result = dao.delete(ids);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}
		}
		if (result) {
			out.print("删除成功");
		} else {
			out.print("删除失败");
		}
		out.flush();
	}

	private void getEmailList() {
		AlertEmailDao configdao = new AlertEmailDao();
		List list = new ArrayList();
		try {
			list = configdao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (configdao != null) {
				configdao.close();
			}
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		AlertEmail vo = null;
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				vo = (AlertEmail) list.get(i);
				String usedflag = "启用";
				if (vo.getUsedflag() == 0)
					usedflag = "未启用";
				jsonString.append("{\"id\":\"");
				jsonString.append(vo.getId());
				jsonString.append("\",");

				jsonString.append("\"username\":\"");
				jsonString.append(vo.getUsername());
				jsonString.append("\",");

				jsonString.append("\"smtp\":\"");
				jsonString.append(vo.getSmtp());
				jsonString.append("\",");

				jsonString.append("\"usedflag\":\"");
				jsonString.append(usedflag);
				jsonString.append("\"}");

				if (i != list.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total : " + list.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}
}
