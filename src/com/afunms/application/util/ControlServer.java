package com.afunms.application.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;

import com.afunms.application.dao.UpAndDownMachineDao;
import com.afunms.application.model.UpAndDownMachine;

public class ControlServer {
	private ServerSocket serverSocket;
	private Socket socket;
	Hashtable<String, RemoteClientInfo> ip_clientInfoHash;

	public ControlServer(Hashtable<String, RemoteClientInfo> ip_threadHash) {
		this.ip_clientInfoHash = ip_threadHash;
	}

	@SuppressWarnings("unchecked")
	public void initServer() {
		UpAndDownMachineDao dao = null;
		try {
			serverSocket = new ServerSocket(10000);
			while (true) {
				socket = serverSocket.accept(); // 等待客户连接
				dao = new UpAndDownMachineDao();
				List list = dao.findByCriteria("select * from nms_remote_up_down_machine where ipaddress='" + socket.getInetAddress().toString().substring(1) + "'");
				if (list.size() != 0) {
					dao = new UpAndDownMachineDao();
					UpAndDownMachine machine = (UpAndDownMachine) list.get(0);
					machine.setMonitorStatus(1);
					dao.update(machine);
				}
				RemoteClientInfo info = new RemoteClientInfo(socket);
				ip_clientInfoHash.put(socket.getInetAddress().toString().substring(1), info);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
	}

	public void closeServerSocket() {
		try {
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}