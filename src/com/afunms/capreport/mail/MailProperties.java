package com.afunms.capreport.mail;

import java.io.FileInputStream;
import java.util.Properties;

public final class MailProperties {
	private static String sender; // 发件人地址

	private static String smtpHost; // 邮件发送服务器（smtp）

	private static String user; // 登录用户名

	private static String password; // 登录密码

	private static String authentication; // 是否需要身份验证

	private static String port; // smtp端口

	private static Properties mailproperties;

	/**
	 * 邮件配置信息读取
	 * 
	 * @param mail
	 */
	public static void MailConfig(String mailPath) {
		try {
			mailproperties = new Properties();
			Properties properties = new Properties();
			FileInputStream stream = new FileInputStream(mailPath);
			if (stream != null) {
				properties.load(stream);
				smtpHost = properties.getProperty("mail.smtp.host");
				sender = properties.getProperty("mail.from");
				authentication = properties.getProperty("mail.smtp.auth");
				user = properties.getProperty("mail.smtp.user");
				password = properties.getProperty("mail.smtp.password");
				port = properties.getProperty("mail.smtp.port");
				mailproperties.put("mail.smtp.host", smtpHost); // 设置smtp主机
				mailproperties.put("mail.from", sender); // 设置发送邮箱
				mailproperties.put("mail.smtp.auth", authentication); // 设置smtp身份验证
				mailproperties.put("mail.smtp.user", user); // 设置用户
				mailproperties.put("mail.smtp.password", password); // 设置口令
				mailproperties.put("mail.smtp.port", port); // 设置smtp端口
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取邮件配置信息
	 * 
	 * @param mailPath
	 *            邮件配置文件路径
	 * @return
	 */
	public static Properties getMailproperties(String mailPath) {
		if (mailproperties == null) {
			MailConfig(mailPath);
		}
		return mailproperties;
	}

	public static String getAuthentication() {
		return authentication;
	}

	public static void setAuthentication(String authentication) {
		MailProperties.authentication = authentication;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		MailProperties.password = password;
	}

	public static String getPort() {
		return port;
	}

	public static void setPort(String port) {
		MailProperties.port = port;
	}

	public static String getSender() {
		return sender;
	}

	public static void setSender(String sender) {
		MailProperties.sender = sender;
	}

	public static String getSmtpHost() {
		return smtpHost;
	}

	public static void setSmtpHost(String smtpHost) {
		MailProperties.smtpHost = smtpHost;
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		MailProperties.user = user;
	}

	/**
	 * 加载properties文件到mail属性文件模型类
	 * @param properties
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static synchronized void loadMailProperies(Properties properties) {
		MailProperties mailProperties = new MailProperties();
		smtpHost = properties.getProperty("mail.smtp.host");
		sender = properties.getProperty("mail.from");
		authentication = properties.getProperty("mail.smtp.auth");
		user = properties.getProperty("mail.smtp.user");
		password = properties.getProperty("mail.smtp.password");
		port = properties.getProperty("mail.smtp.port");
		mailProperties.setAuthentication(authentication);
		mailProperties.setPassword(password);
		mailProperties.setPort(port);
		mailProperties.setSender(sender);
		mailProperties.setSmtpHost(smtpHost);
		mailProperties.setUser(user);
	}
}
