package com.afunms.initialize;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class ExecuteCollectSyslog implements Runnable {
	public static int sport = 514;// 服务器端端口514；

	private Logger logger = Logger.getLogger(this.getClass());
	DatagramSocket socket;// 收发数据；
	int processId;// 进程id
	String processName;// 进程名
	String processIdStr = "";// 进程id字符
	int facility;// 事件来源编码
	int priority;// 优先级编码
	String facilityName;// 事件来源名称
	String priorityName;// 优先级名称
	String hostname;// 主机名称
	String username;// 登陆用户
	Calendar timestamp;// 时间戳
	String message;// 得消息内容
	String ipaddress;// IP地址
	String businessid;// 业务ID
	boolean sign = false;

	int eventid;// 事件ID

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ExecuteCollectSyslog() {
		try {
			socket = new DatagramSocket(sport);
			logger.info("已启动监听syslog端口：" + socket.getLocalPort());
		} catch (SocketException e) {
			logger.error("Syslog监听程序启动失败，请确认端口是否在使用", e);
		}
	}

	public void close() {
		try {
			if (socket != null) {
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		if (socket == null) {
			return;
		}
		ProcessSyslog syslog = null;
		while (true) {
			byte[] b = new byte[1024];
			DatagramPacket packet = new DatagramPacket(b, b.length);// 生成一个接收数据的；
			syslog = new ProcessSyslog();
			try {
				socket.receive(packet);// 接收数据；
				syslog.createTask(packet);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				syslog = null;
			}
		}
	}

}
