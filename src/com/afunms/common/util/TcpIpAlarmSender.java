package com.afunms.common.util;

public class TcpIpAlarmSender {

	private static String converSendMsg(String message, String mobile) {
		StringBuffer str = new StringBuffer();
		String phoneNum = mobile;
		StringBuffer strMsg = new StringBuffer();
		strMsg.append("9000").append("|").append("0").append("|").append("").append("|").append(phoneNum).append("|").append(SysUtil.checkTel(phoneNum)).append("|").append(
				SysUtil.getStrByLength(message, 256)).append("|").append(SysUtil.getDay()).append("|").append(SysUtil.getSecond()).append("|").append(SysUtil.makeString(20))
				.append("|");
		int length = strMsg.length();
		String s = null;
		if (length < 1000) {
			s = "0" + String.valueOf(length);
		} else if (length < 100) {
			s = "00" + String.valueOf(length);
		} else {
			s = String.valueOf(length);
		}

		str.append(s).append(strMsg.toString());
		return str.toString();
	}

	public void sendAlarm(String message, String mobile) throws Exception {
		ConvertSocket.OrganizationMessage(converSendMsg(message, mobile));
	}
}