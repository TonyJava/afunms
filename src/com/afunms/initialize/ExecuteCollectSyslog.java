package com.afunms.initialize;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class ExecuteCollectSyslog implements Runnable {
	public static int sport = 514;// �������˶˿�514��

	private Logger logger = Logger.getLogger(this.getClass());
	DatagramSocket socket;// �շ����ݣ�
	int processId;// ����id
	String processName;// ������
	String processIdStr = "";// ����id�ַ�
	int facility;// �¼���Դ����
	int priority;// ���ȼ�����
	String facilityName;// �¼���Դ����
	String priorityName;// ���ȼ�����
	String hostname;// ��������
	String username;// ��½�û�
	Calendar timestamp;// ʱ���
	String message;// ����Ϣ����
	String ipaddress;// IP��ַ
	String businessid;// ҵ��ID
	boolean sign = false;

	int eventid;// �¼�ID

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ExecuteCollectSyslog() {
		try {
			socket = new DatagramSocket(sport);
			logger.info("����������syslog�˿ڣ�" + socket.getLocalPort());
		} catch (SocketException e) {
			logger.error("Syslog������������ʧ�ܣ���ȷ�϶˿��Ƿ���ʹ��", e);
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
			DatagramPacket packet = new DatagramPacket(b, b.length);// ����һ���������ݵģ�
			syslog = new ProcessSyslog();
			try {
				socket.receive(packet);// �������ݣ�
				syslog.createTask(packet);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				syslog = null;
			}
		}
	}

}
