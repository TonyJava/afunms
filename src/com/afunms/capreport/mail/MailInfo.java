package com.afunms.capreport.mail;

public class MailInfo {
	/**
	 * 收件人地址
	 */
	private String receiver;

	/**
	 * 抄送人地址
	 */
	private String copyReceiver;

	/**
	 * 暗送人地址
	 */
	private String hiddenReceiver;

	/**
	 * mail主题
	 */
	private String subject;

	/**
	 * mail内容
	 */
	private String content;

	/**
	 * mail附件的路径，多个附件之间用分号分隔
	 */
	private String affixPath;

	/**
	 * 
	 */
	public MailInfo() {

	}

	/**
	 * 设置收件人地址，多个收件人地址之间用分号分隔 时间
	 * 
	 * @author sunqichang/孙启昌
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	/**
	 * @author sunqichang/孙启昌
	 * @return
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * 设置抄送人地址，多个抄送人地址之间用分号分隔 时间
	 * 
	 * @author sunqichang/孙启昌
	 * @return String
	 */
	public void setCopyReceiver(String receiver) {
		this.copyReceiver = receiver;
	}

	/**
	 * 
	 * @return 抄送人地址
	 */
	public String getCopyReceiver() {
		return copyReceiver;
	}

	/**
	 * 设置暗送人地址，多个暗送人地址之间用分号分隔 时间
	 * 
	 * @author sunqichang/孙启昌
	 * @return String
	 */
	public void setHiddenReceiver(String receiver) {
		this.hiddenReceiver = receiver;
	}

	/**
	 * @return 暗送人地址
	 */
	public String getHiddenReceiver() {
		return hiddenReceiver;
	}

	/**
	 * 设置mail主题
	 * 
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return mail主题
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * 设置mail内容
	 * 
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return mail内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * 设置mail附件的路径，多个附件之间用分号分隔
	 * 
	 * @author sunqichang/孙启昌
	 * @return String
	 */
	public void setAffixPath(String affixPath) {
		this.affixPath = affixPath;
	}

	/**
	 * @return mail附件的路径，多个附件之间用分号分隔
	 */
	public String getAffixPath() {
		return affixPath;
	}

}
