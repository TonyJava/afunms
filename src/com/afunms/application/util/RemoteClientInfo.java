package com.afunms.application.util;

import java.io.PrintWriter;
import java.net.Socket;

public class RemoteClientInfo {
	private Socket client;
	private PrintWriter out;

	public RemoteClientInfo(Socket s) {
		try {
			client = s;
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void executeCmd(String cmd) {
		out.println(cmd);
		out.flush();
	}

	public void closeConnection() {
		try {
			this.client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
