/**
 * <p>Description:utility class,includes some methods which are often used</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-06
 */

package com.afunms.common.util;

import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import com.afunms.system.dao.AlertEmailDao;
import com.afunms.system.model.AlertEmail;

@SuppressWarnings("unchecked")
public class SendMailManager {
	/**
	 * 构造函数
	 */
	public SendMailManager() {
	}

	public boolean SendMail(String receivemailaddr, String body) {
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try {
			list = emaildao.getByFlage(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emaildao.close();
		}
		if (list != null && list.size() > 0) {
			vo = (AlertEmail) list.get(0);
		}
		if (vo == null) {
			return false;
		}
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		boolean flag = false;
		try {
			Address[] ccAddress = { new InternetAddress("hukelei@dhcc.com.cn"), new InternetAddress("rhythm333@163.com") };
			String fromAddr = "";
			SendMail sendmail = new SendMail(mailsmtp, mailaddr, mailpassword, receivemailaddr, "网管服务告警邮件", body, fromAddr, ccAddress);

			try {
				flag = sendmail.sendmail();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}

	public boolean SendMailNoFile(String fromAddress, String receivemailaddr, String body) {
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try {
			list = emaildao.getByFlage(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emaildao.close();
		}
		if (list != null && list.size() > 0) {
			vo = (AlertEmail) list.get(0);
		}
		if (vo == null) {
			return false;
		}
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		boolean flag = false;
		try {
			Address[] ccAddress = { new InternetAddress("hukelei@dhcc.com.cn"), new InternetAddress("rhythm333@163.com") };
			String fromAddr = fromAddress;
			SendMail sendmail = new SendMail(mailsmtp, mailaddr, mailpassword, receivemailaddr, "网管服务告警邮件", body, fromAddr, ccAddress);

			try {
				flag = sendmail.sendmailNoFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}

	public boolean SendMailWithFile(String fromAddress, String receivemailaddr, String body, String fileName) {
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try {
			list = emaildao.getByFlage(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emaildao.close();
		}
		if (list != null && list.size() > 0) {
			vo = (AlertEmail) list.get(0);
		}
		if (vo == null) {
			return false;
		}
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		boolean flag = false;
		try {
			Address[] ccAddress = { new InternetAddress("hukelei@dhcc.com.cn"), new InternetAddress("rhythm333@163.com") };
			String fromAddr = fromAddress;
			SendMail sendmail = new SendMail(mailsmtp, mailaddr, mailpassword, receivemailaddr, "网管服务告警邮件", body, fromAddr, ccAddress);
			try {
				flag = sendmail.sendmailWithFile(fileName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}

	public boolean SendMyMail(String receivemailaddr, String body) {
		AlertEmail vo = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try {
			list = emaildao.getByFlage(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emaildao.close();
		}
		if (list != null && list.size() > 0) {
			vo = (AlertEmail) list.get(0);
		}
		if (vo == null) {
			return false;
		}
		String mailaddr = vo.getUsername();
		String mailpassword = vo.getPassword();
		String mailsmtp = vo.getSmtp();
		boolean flag = false;
		try {
			Address[] ccAddress = { new InternetAddress("hukelei@dhcc.com.cn"), new InternetAddress("rhythm333@163.com") };
			String fromAddr = "";
			SendMail sendmail = new SendMail(mailsmtp, mailaddr, mailpassword, receivemailaddr, "网管服务告警邮件", body, fromAddr, ccAddress);
			try {
				flag = sendmail.sendmail();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return flag;
	}

}