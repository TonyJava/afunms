package com.afunms.common.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class SocketService {
	/**
	 * �������еķ���
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
	 * ���캯��,��ʼ��һ������
	 */
	public SocketService() {
	}

}