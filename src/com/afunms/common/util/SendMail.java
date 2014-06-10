package com.afunms.common.util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.afunms.initialize.ResourceCenter;

public class SendMail {
	/**
	 * �ʼ����Ͳ���main
	 * 
	 * @param args
	 */

	public static void sendMail(String toAddr, String subject, String body, String fromAddr, Address[] ccAddress) throws RemoteException {
		try {
			try {
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.sohu.com"); // set host
				props.put("mail.smtp.auth", "true"); // set auth
				MyAuthenticator auth = new MyAuthenticator("donhukelei", "hukelei");
				Session sendMailSession = Session.getInstance(props, auth);
				sendMailSession.setDebug(true);

				MimeMessage message = new MimeMessage(sendMailSession);
				// ���÷�����
				message.setFrom(new InternetAddress(fromAddr));
				// ������
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
				// �ʼ�����
				message.setSubject(subject);
				message.setSentDate(new Date());
				MimeMultipart multi = new MimeMultipart();
				BodyPart textBodyPart = new MimeBodyPart(); // ��һ��BodyPart.��ҪдһЩһ����ż����ݡ�
				textBodyPart.setText("���������");
				multi.addBodyPart(textBodyPart);
				FileWriter fw = new FileWriter("aaa.html");
				PrintWriter pw = new PrintWriter(fw);
				pw.println(body);
				pw.close();
				fw.close();

				// tempFile.

				FileDataSource fds = new FileDataSource("aaa.html"); // ������ڵ��ĵ�������throw�쳣��
				BodyPart fileBodyPart = new MimeBodyPart(); // �ڶ���BodyPart
				fileBodyPart.setDataHandler(new DataHandler(fds)); // �ַ�����ʽװ���ļ�
				fileBodyPart.setFileName("fujian.html"); // �����ļ��������Բ���ԭ�����ļ�����
				// �����ˣ�ͬ��һ��BodyPart.
				multi.addBodyPart(fileBodyPart);
				// MimeMultPart��ΪContent����message
				message.setContent(multi);
				// �������ϵĹ������뱣�档
				message.saveChanges();
				// ���ͣ�����Transport�࣬����SMTP���ʼ�����Э�飬
				Transport transport = sendMailSession.getTransport("smtp");
				transport.connect("smtp.163.com", "rhythm333", "hukelei");
				transport.sendMessage(message, message.getAllRecipients());
				// transport.send(message);
				transport.close();

			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendMyMail(String toAddr, String subject, String body, String fromAddr, Address[] ccAddress) {
		try {
			try {
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.sina.com.cn"); // set host
				props.put("mail.smtp.auth", "true"); // set auth
				MyAuthenticator auth = new MyAuthenticator("supergzm", "6400891gzm");
				Session sendMailSession = Session.getInstance(props, auth);
				sendMailSession.setDebug(true);

				// ���� �ʼ���message��message����������ʼ��ڶ��еĲ��������Ƿ�װ����set����ȥ���õ�
				MimeMessage message = new MimeMessage(sendMailSession);
				// ���÷�����
				message.setFrom(new InternetAddress(fromAddr));
				// ������
				message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
				// �ʼ�����
				message.setSubject(subject);
				message.setSentDate(new Date());
				MimeMultipart multi = new MimeMultipart();
				BodyPart textBodyPart = new MimeBodyPart(); // ��һ��BodyPart.��ҪдһЩһ����ż����ݡ�
				textBodyPart.setText("���������");
				multi.addBodyPart(textBodyPart);
				FileWriter fw = new FileWriter("aaa.html");
				PrintWriter pw = new PrintWriter(fw);
				pw.println(body);
				pw.close();
				fw.close();

				FileDataSource fds = new FileDataSource("aaa.html"); // ������ڵ��ĵ�������throw�쳣��
				BodyPart fileBodyPart = new MimeBodyPart(); // �ڶ���BodyPart
				fileBodyPart.setDataHandler(new DataHandler(fds)); // �ַ�����ʽװ���ļ�
				fileBodyPart.setFileName("fujian.html"); // �����ļ��������Բ���ԭ�����ļ�����
				multi.addBodyPart(fileBodyPart);
				message.setContent(multi);
				message.saveChanges();
				Transport transport = sendMailSession.getTransport("smtp");
				transport.connect("smtp.163.com", "rhythm333", "hukelei");
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();

			} catch (Exception exc) {
				exc.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �����ʼ�����
	 */
	private String mailaddress;
	/**
	 * �����ʼ�����
	 */
	private String receiveAddress;
	private String sendmail;
	private String sendpasswd;
	private String toAddr;
	private String subject;

	private String body;

	private String fromAddr;

	private Address[] ccAddress;

	public SendMail() {
	}

	public SendMail(String mailaddress, String sendmail, String sendpasswd, String toAddr, String subject, String body, String fromAddr, Address[] ccAddress) {
		super();
		this.mailaddress = mailaddress;
		this.sendmail = sendmail;
		this.sendpasswd = sendpasswd;
		this.toAddr = toAddr;
		this.subject = subject;
		this.body = body;
		this.fromAddr = fromAddr;
		this.ccAddress = ccAddress;

	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return the ccAddress
	 */
	public Address[] getCcAddress() {
		return ccAddress;
	}

	/**
	 * @return the fromAddr
	 */
	public String getFromAddr() {
		return fromAddr;
	}

	/**
	 * �����ʼ����غ��û��� �� ��ȡ�����˵������ַ �� hongli@dhcc.com.cn
	 * 
	 * @return
	 */
	private synchronized String getFromEmailAddress() {
		StringBuffer fromEmailAddress = new StringBuffer();
		fromEmailAddress.append(sendmail);
		String[] gateArrays = { "mail.", "pop3.", "smtp.", "exchange.", "pop." };
		String tempMainAddress = mailaddress;
		for (String gate : gateArrays) {
			tempMainAddress = tempMainAddress.replaceAll(gate, "");
		}
		fromEmailAddress.append("@");
		fromEmailAddress.append(tempMainAddress);
		return fromEmailAddress.toString();
	}

	/**
	 * @return the mailaddress
	 */
	public String getMailaddress() {
		return mailaddress;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	/**
	 * @return the sendmail
	 */
	public String getSendmail() {
		return sendmail;
	}

	/**
	 * @return the sendpasswd
	 */
	public String getSendpasswd() {
		return sendpasswd;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @return the toAddr
	 */
	public String getToAddr() {
		return toAddr;
	}

	/**
	 * 
	 * @param recipients
	 *            �ʼ������ˣ���"konglingqi@dhcc.com.cn"
	 * @param subject
	 *            �ʼ�����
	 * @param message
	 *            �ʼ�����
	 * @param from
	 *            �ʼ������˵�ַ����"guzhiming@dhcc.com.cn"
	 * @param emailUserName
	 *            ���ʼ��� ��¼���� ��ʹ�õ��û�������"guzhiming@dhcc.com.cn"
	 * @param emailPwd
	 *            ���ʼ��� ��¼���� ��ʹ�õ��û��� ��Ӧ������
	 * @param smtpHost
	 *            ����smtp��ַ�����ʹ��dhcc���䷢���ʼ�����ò�����"mail.dhcc.com.cn"
	 * @throws MessagingException
	 */
	public void postMail(String recipients[], String subject, String message, String from, String emailUserName, String emailPwd, String smtpHost, String fileName)
			throws MessagingException {
		try {
			boolean debug = false;

			// Set the host smtp address
			Properties props = new Properties();
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.auth", "true");

			Authenticator auth = new SMTPAuthenticator(emailUserName, emailPwd);
			Session session = Session.getDefaultInstance(props, auth);

			session.setDebug(debug);

			// create a message
			MimeMessage msg = new MimeMessage(session);
			msg.setSubject(subject, "GB2312");
			// set the from and to address
			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(addressFrom);

			InternetAddress[] addressTo = new InternetAddress[recipients.length];
			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			MimeMultipart multi = new MimeMultipart();
			MimeBodyPart textBodyPart = new MimeBodyPart(); // ��һ��BodyPart.��ҪдһЩһ����ż����ݡ�
			textBodyPart.setContent(message, "text/plain;charset=GB2312");
			multi.addBodyPart(textBodyPart);

			if (fileName != null) {
				FileDataSource fds = new FileDataSource(fileName); // ������ڵ��ĵ�������throw�쳣��
				BodyPart fileBodyPart = new MimeBodyPart(); // �ڶ���BodyPart
				fileBodyPart.setDataHandler(new DataHandler(fds)); // �ַ�����ʽװ���ļ�
				fileBodyPart.setFileName("report.xls"); // �����ļ��������Բ���ԭ�����ļ�����
				multi.addBodyPart(fileBodyPart);
			}

			// MimeMultPart��ΪContent����message
			msg.setContent(multi, "text/plain;charset=gb2312");
			msg.saveChanges();
			Transport.send(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean sendmail() {
		try {
			Properties props = new Properties();
			// �����ʼ�������
			props.put("mail.smtp.host", mailaddress);// dhcc.com.cn
			// ���õ�¼ƾ֤
			props.put("mail.smtp.auth", "true");
			MyAuthenticator auth = new MyAuthenticator(sendmail, sendpasswd);
			Session sendMailSession = Session.getInstance(props, auth);

			MimeMessage message = new MimeMessage(sendMailSession);
			// ���÷�����
			String fromEmailAddress = getFromEmailAddress();
			message.setFrom(new InternetAddress(fromEmailAddress));
			// ������
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
			// �ʼ�����
			message.setSubject(subject);
			message.setSentDate(new Date());
			MimeMultipart multi = new MimeMultipart();
			BodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(body);
			multi.addBodyPart(textBodyPart);

			FileDataSource fds = new FileDataSource(ResourceCenter.getInstance().getSysPath() + "/ftpupload/ftpupload.txt");
			BodyPart fileBodyPart = new MimeBodyPart();
			fileBodyPart.setDataHandler(new DataHandler(fds));
			fileBodyPart.setFileName("alarm.txt");
			multi.addBodyPart(fileBodyPart);
			message.setContent(multi);

			message.saveChanges();
			Transport.send(message);
		} catch (Exception exc) {
			exc.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean sendmailNoFile() {
		try {
			this.postMail(new String[] { toAddr }, subject, body, fromAddr, sendmail, sendpasswd, mailaddress, null);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean sendmailWithFile(String fileName) {
		try {
			this.postMail(new String[] { toAddr }, subject, body, fromAddr, sendmail, sendpasswd, mailaddress, fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @param ccAddress
	 *            the ccAddress to set
	 */
	public void setCcAddress(Address[] ccAddress) {
		this.ccAddress = ccAddress;
	}

	/**
	 * @param fromAddr
	 *            the fromAddr to set
	 */
	public void setFromAddr(String fromAddr) {
		this.fromAddr = fromAddr;
	}

	/**
	 * @param mailaddress
	 *            the mailaddress to set
	 */
	public void setMailaddress(String mailaddress) {
		this.mailaddress = mailaddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	/**
	 * @param sendmail
	 *            the sendmail to set
	 */
	public void setSendmail(String sendmail) {
		this.sendmail = sendmail;
	}

	/**
	 * @param sendpasswd
	 *            the sendpasswd to set
	 */
	public void setSendpasswd(String sendpasswd) {
		this.sendpasswd = sendpasswd;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @param toAddr
	 *            the toAddr to set
	 */
	public void setToAddr(String toAddr) {
		this.toAddr = toAddr;
	}
}
