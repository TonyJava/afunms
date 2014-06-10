/**
 * <p>Description:snmp tool</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-12
 */

package com.afunms.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

public class ConvertSocket {
	private static Properties p = new Properties();
	private static Socket connection;

	public static void closeConnection() {
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Socket connect() {
		try {
			String filePath = CommonAppUtil.getAppName() + "/task/OracleProjectProertipes.txt";
			FileInputStream fin = new FileInputStream(new File(filePath));
			p.load(fin);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String ip = p.getProperty("ip");
		String port = p.getProperty("port");
		try {
			connection = new Socket(ip, Integer.valueOf(port).intValue());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return connection;
	}

	public static String OrganizationMessage(String str) {
		connect();
		try {
			sendMsg2Server(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		closeConnection();
		return null;
	}

	static String read() throws IOException {
		String str = "111";
		if (connection.isClosed()) {
			connection = connect();
		}
		InputStream is = connection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, "gb2312");
		isr.read();

		System.out.println(str);
		return str;
	}

	public static void sendMsg2Server(String sendMsg) throws IOException {
		try {
			write(sendMsg);
		} catch (IOException e) {
			throw e;
		}
	}

	private static void write(String str) throws IOException {
		OutputStream output = connection.getOutputStream();
		output.write(str.getBytes());
		connection.getOutputStream().flush();
		connection.getOutputStream().close();
	}
}