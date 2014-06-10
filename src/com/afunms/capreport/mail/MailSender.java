/**
 * @author sunqichang/孙启昌
 * Created on 2011-5-12 下午02:44:37
 */
package com.afunms.capreport.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.afunms.system.dao.AlertEmailDao;
import com.afunms.system.model.AlertEmail;

/**
 * 简便的Mail发送功能，支持多附件及身份验证
 */

@SuppressWarnings("unchecked")
public class MailSender {
	private final static Log log = LogFactory.getLog(MailSender.class);

	/**
	 * 使用smtp发送邮件
	 * 
	 * @param mail
	 * @throws MessagingException
	 *             mail发送失败
	 */

	public static boolean send(MailInfo mail) {
		try {
			Properties properties = new Properties();
			AlertEmail em = null;// 告警邮箱对象
			AlertEmailDao emaildao = null;
			List list = null;// 告警邮箱列表
			try {
				emaildao = new AlertEmailDao();
				list = emaildao.getByFlage(1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (emaildao != null) {
					emaildao.close();
				}
			}
			if (list != null && list.size() > 0) {
				em = (AlertEmail) list.get(0);
			}
			if (em == null) {
				return false;
			}
			properties.put("mail.smtp.auth", "true"); // set auth
			properties.put("mail.smtp.host", em.getSmtp());
			properties.put("mail.from", em.getMailAddress());
			properties.put("mail.smtp.user", em.getUsername());
			properties.put("mail.smtp.password", em.getPassword());
			properties.put("mail.smtp.port", "25");

			MailProperties.loadMailProperies(properties);

			Session session = null;
			String auth = properties.getProperty("mail.smtp.auth");

			// 获得邮件会话对象
			if ("true".equalsIgnoreCase(auth)) {
				session = Session.getInstance(properties, new Authenticator() {
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(MailProperties.getUser(), MailProperties.getPassword());
					}
				});
			} else {
				session = Session.getInstance(properties, null);
			}

			MimeMessage mimeMsg = new MimeMessage(session);// 创建MIME邮件对象

			if (MailProperties.getSender() != null)// 设置发件人地址
				mimeMsg.setFrom(new InternetAddress(MailProperties.getSender()));

			if (mail.getReceiver() != null)// 设置收件人地址
				mimeMsg.setRecipients(Message.RecipientType.TO, parse(mail.getReceiver()));

			if (mail.getCopyReceiver() != null)// 设置抄送人地址
				mimeMsg.setRecipients(Message.RecipientType.CC, parse(mail.getCopyReceiver()));

			if (mail.getHiddenReceiver() != null)// 设置暗送人地址
				mimeMsg.setRecipients(Message.RecipientType.BCC, parse(mail.getHiddenReceiver()));

			if (mail.getSubject() != null)// 设置邮件主题
				mimeMsg.setSubject(mail.getSubject(), "UTF-8");

			MimeBodyPart part = new MimeBodyPart();// mail内容部分
			part.setText(mail.getContent() == null ? "" : mail.getContent(), "UTF-8");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(part);// 在 Multipart 中增加mail内容部分

			if (!isNull(mail.getAffixPath()))// mail附件部分
			{
				ArrayList<MimeBodyPart> parts = parsePath(mail.getAffixPath());
				for (int i = 0; i < parts.size(); i++) {
					multipart.addBodyPart((MimeBodyPart) parts.get(i));
				}
			}

			mimeMsg.setContent(multipart);// 增加 Multipart 到信息体
			mimeMsg.setSentDate(new Date());// 设置发送日期
			try {
				Transport.send(mimeMsg, mimeMsg.getAllRecipients());// 发送邮件
			} catch (SendFailedException e) {
				log.error("邮件发送异常:", e);
				if (mail != null) {
					log.error("发件人:" + MailProperties.getSender() + "\n" + "收件人:" + mail.getReceiver() + "\n" + "主题:" + mail.getSubject() + "\n" + "内容:" + mail.getContent() + "\n"
							+ "附件:" + mail.getAffixPath());
				}
				if (mail.getReceiver().length() > 1 && e.toString().indexOf("<") != -1) {
					resend(e.toString(), mail);
				} else if (mail.getReceiver().length() == 0) {
					return false;
				} else if (e.toString().indexOf("javax.mail.AuthenticationFailedException") != -1) {
					log.error("发送邮件失败，您的邮件服务配置不正确，请核对。");
					return false;
				}
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("邮件发送失败！", e);
			return false;
		}

	}

	/**
	 * 重新解析地址集合字符串（去掉不合法email地址）
	 * 
	 * @param address
	 * @param mail
	 * @throws AddressException
	 */
	public static void resend(String address, MailInfo mail) throws AddressException {
		String email[] = address.split(">");
		String receiver = mail.getReceiver(); // 原数据
		String remail[] = receiver.replaceAll(",", ";").split(";"); // 原数据
		StringBuffer receivers = new StringBuffer();
		int index = 0;// 取“<” 的位置
		for (int j = 0; j < email.length; j++) {
			if (email[j].indexOf("<") > -1) {
				index = email[j].indexOf("<");
				String errorEmail = email[j].substring(index + 1);
				for (int i = 0; i < remail.length; i++) {
					if (errorEmail.equals(remail[i])) {
						log.error(errorEmail + " 邮件地址不存在;");
						remail[i] = "error";
					}
				}
			}
		}

		for (int p = 0; p < remail.length; p++) {
			if (!"error".equals(remail[p])) {
				if (p > 0) {
					receivers.append(";" + remail[p]);
				} else {
					receivers.append(remail[p]);
				}
			}
		}
		mail.setReceiver(receivers.toString());
		send(mail);
	}

	/**
	 * 解析地址集合字符串
	 * 
	 * @param addressSet
	 * @return
	 * @throws AddressException
	 */
	private static InternetAddress[] parse(String addressSet) throws AddressException {
		ArrayList<InternetAddress> list = new ArrayList<InternetAddress>();
		addressSet = addressSet.replaceAll(",", ";");
		StringTokenizer tokens = new StringTokenizer(addressSet, ";");
		while (tokens.hasMoreTokens()) {
			list.add(new InternetAddress(tokens.nextToken().trim()));
		}

		InternetAddress[] addressArray = new InternetAddress[list.size()];
		list.toArray(addressArray);
		return addressArray;
	}

	/**
	 * 解析附件路径字符串
	 * 
	 * @param affixPath
	 * @param copyPath
	 * @return
	 * @throws MessagingException
	 */
	private static ArrayList<MimeBodyPart> parsePath(String affixPath) throws MessagingException {
		ArrayList<MimeBodyPart> list = new ArrayList<MimeBodyPart>();
		StringTokenizer tokens = new StringTokenizer(affixPath, ";");
		while (tokens.hasMoreTokens()) {
			MimeBodyPart part = new MimeBodyPart();
			FileDataSource source = new FileDataSource(tokens.nextToken().trim());
			part.setDataHandler(new DataHandler(source));
			try {
				part.setFileName(MimeUtility.encodeText(source.getName(), "GB2312", "B"));
			} catch (java.io.UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			list.add(part);
		}
		return list;
	}

	/**
	 * 发送邮件时复制文件，把附件标题改成中文名字发送邮件
	 * 
	 * @param affixPath
	 * @param copyPath
	 */
	public static void fileCopy(String affixPath, String copyPath) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			String nullpath = affixPath.substring(0, affixPath.length() - 11); // 由于订阅文件名是由7位的缓存ID生成，所以可以取到文件路径
			String nullname = affixPath.substring(affixPath.length() - 11); // 取得订阅生成的文件名
			File file = new File(affixPath);
			File nullfile = new File(nullpath + "nullltable" + nullname);
			int i = 0;
			while (!file.exists()) {
				log.error("文件" + affixPath + "还未生成！等待" + i + "次");
				if (nullfile.exists()) {
					i = 7;
				}
				if (i > 6) {
					break;
				}
				Thread.sleep(3000);
				i++;
			}
			byte[] iobuff = new byte[1024];
			int bytes;
			fis = new FileInputStream(affixPath);
			fos = new FileOutputStream(copyPath);
			while ((bytes = fis.read(iobuff)) != -1) {
				fos.write(iobuff, 0, bytes);
			}
			if (affixPath != null && copyPath != null && affixPath.substring(0, affixPath.lastIndexOf("\\") + 1).equals(copyPath.substring(0, copyPath.lastIndexOf("\\") + 1))) {
				log.info("成功copy文件" + affixPath.substring(affixPath.lastIndexOf("\\") + 1, affixPath.length()) + "，更名为："
						+ copyPath.substring(copyPath.lastIndexOf("\\") + 1, copyPath.length()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return true:字符串为空 false:不为空
	 */
	public static boolean isNull(String str) {
		boolean result = false;
		if (str != null && !"".equals(str) && !"null".equalsIgnoreCase(str) && !"NaN".equalsIgnoreCase(str)) {
			result = false;
		} else {
			result = true;
		}
		return result;
	}
}
