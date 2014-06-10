package com.afunms.application.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CreateServerThread extends Thread {
	private Socket client;
	private PrintWriter out;

	public CreateServerThread(Socket s) {
		try {
			client = s;
			out = new PrintWriter(client.getOutputStream(), true);
			start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			String cmd;
			BufferedReader localReader = new BufferedReader(new InputStreamReader(System.in));
			while ((cmd = localReader.readLine()) != null) {
				out.println(cmd);
				out.flush();
				if (cmd.equals("shutdown"))
					break;
			}
			this.client.close();
		} catch (IOException e) {
		}
	}

}