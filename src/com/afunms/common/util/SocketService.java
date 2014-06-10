package com.afunms.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class SocketService {
	/**
	 * 测试运行的服务
	 */
	public static boolean checkService(String ipAddress, int port, int timeout) {
		boolean result = false;

		Socket socket = new Socket();
		try {
			InetAddress addr = InetAddress.getByName(ipAddress);
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			socket.connect(sockaddr, timeout);
			result = true;
		} catch (SocketTimeoutException ste) {
			ste.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 构造函数,初始化一个对象
	 */
	public SocketService() {
	}

}