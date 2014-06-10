package com.afunms.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Vector;

import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class PingUtil {
	private String ipaddress = "1.1.1.1";
	private String osname;

	/**
	 * 
	 */
	public PingUtil(String ip) {
		super();
		ipaddress = ip;
		osname = System.getProperty("os.name");
	}

	public Vector addhis(Integer[] packet) {// 向历史表添加
		Vector returnVector = new Vector();
		try {
			PingCollectEntity hostdata = null;
			Calendar date = Calendar.getInstance();
			hostdata = new PingCollectEntity();
			hostdata.setIpaddress(ipaddress);
			hostdata.setCollecttime(date);
			hostdata.setCategory("Ping");
			hostdata.setEntity("Utilization");
			hostdata.setSubentity("ConnectUtilization");
			hostdata.setRestype("dynamic");
			hostdata.setUnit("%");
			if (packet[0] == null) {
				hostdata.setThevalue("0");
			} else {
				hostdata.setThevalue(packet[0].toString());
			}
			returnVector.addElement(hostdata);
			hostdata = new PingCollectEntity();
			hostdata.setIpaddress(ipaddress);
			hostdata.setCollecttime(date);
			hostdata.setCategory("Ping");
			hostdata.setEntity("ResponseTime");
			hostdata.setSubentity("ResponseTime");
			hostdata.setRestype("dynamic");
			hostdata.setUnit("毫秒");
			if (packet[1] != null) {
				hostdata.setThevalue(packet[1].toString());
			} else {
				hostdata.setThevalue("0");
			}
			returnVector.addElement(hostdata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnVector;
	}

	public String initCMD() {
		String PING_CMD = "";
		String s1;
		if (osname.startsWith("SunOS") || osname.startsWith("Solaris")) {
			PING_CMD = "/usr/sbin/ping ";
			s1 = new String(PING_CMD + " ");
		} else if (osname.startsWith("Linux")) {
			if (System.getProperty("pingcommand") != null) {
				PING_CMD = System.getProperty("pingcommand");
			} else {
				PING_CMD = "/bin/ping -c 3 -i 10 ";
			}
			s1 = new String(PING_CMD + " ");
		} else if (osname.startsWith("FreeBSD")) {
			PING_CMD = "/sbin/ping -c 3";
			s1 = new String(PING_CMD + " ");
		} else if (osname.startsWith("Windows")) {
			PING_CMD = "ping -n 3 -w 10000 ";
			s1 = new String(PING_CMD + " ");
		} else {
			s1 = new String(PING_CMD + " ");
		}
		return s1;
	}

	public Integer[] manageLine(String line) {
		String[] lines = line.split(",");
		Integer[] packet = new Integer[lines.length];
		if (lines.length >= 3) {
			int connect = 0;
			if (osname.startsWith("Windows")) {
				String values0 = lines[0].substring(lines[0].indexOf("=") + 1, lines[0].length()).trim();
				String values1 = lines[1].substring(lines[1].indexOf("=") + 1, lines[1].length()).trim();
				connect = Integer.parseInt(values1) * 100 / Integer.parseInt(values0);
				packet[0] = new Integer(connect);
			}
			if (osname.startsWith("Linux")) {
				String values0 = lines[0].substring(0, lines[0].indexOf("packets") - 1).trim();
				String values1 = lines[1].substring(0, lines[1].indexOf("received") - 1).trim();
				connect = Integer.parseInt(values1) * 100 / Integer.parseInt(values0);
				packet[0] = new Integer(connect);
			}

		}
		return packet;
	}

	public Integer[] manageResponseLine(String line) {
		String[] lines = line.split(",");
		Integer[] packet = new Integer[lines.length];

		if (lines.length >= 3) {
			int connect = 0;
			int responseTime = 0;
			if (osname.startsWith("Windows")) {
				String values2 = lines[2].substring(lines[2].indexOf("=") + 1, lines[2].length()).trim();
				if (values2 != null) {
					values2 = values2.replaceAll("ms", "");
					responseTime = Integer.parseInt(values2.trim());
				}
				packet[0] = new Integer(responseTime);
			}
			if (osname.startsWith("Linux")) {
				String values0 = lines[0].substring(0, lines[0].indexOf("packets") - 1).trim();
				String values1 = lines[1].substring(0, lines[1].indexOf("received") - 1).trim();
				connect = Integer.parseInt(values1) * 100 / Integer.parseInt(values0);
				packet[0] = new Integer(connect);
			}

		}
		return packet;
	}

	public Integer[] ping() {
		Integer[] packet = null;
		BufferedReader br = null;
		Runtime runtime;
		Process process;
		InputStream is = null;
		InputStreamReader isr = null;
		String cmd = initCMD();
		String pingCommand = cmd + ipaddress;
		try {
			runtime = Runtime.getRuntime();
			process = runtime.exec(pingCommand);

			is = process.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line = null;
			int sign = 0;
			try {
				while ((line = br.readLine()) != null) {

					line = line.trim();
					if (line.length() == 0) {
						continue;
					}
					line = line.trim().replaceAll("数据包", "Packets").replaceAll("已发送", "Sent").replaceAll("已接收", "Received").replaceAll("丢失", "Lost").replaceAll("最短", "Minimum")
							.replaceAll("最长", "Maximum").replaceAll("平均", "Average").replaceAll("，", ",").replaceAll("往返行程的估计时间(以毫秒为单位):",
									"Approximate round trip times in milli-seconds:").replaceAll("TTL 传输中过期。", "TTL expired in transit.").replaceAll("目标网无法访问。",
									"Destination net unreachable.").replaceAll("请求超时。", "Request timed out.").replaceAll("无法访问目标主机。", "Destination net unreachable.");
					if (line.indexOf("expired in transit") != -1 || line.indexOf("unreachable") != -1) {
						sign = 1;
					}
					if (line.toLowerCase().indexOf("packets") != -1) {
						if (sign == 1) {
							int a = line.indexOf("Received = ");
							String str = line.substring(0, a + 10);
							String str1 = line.substring(a + 12);
							line = str + "0" + str1;

						}
						packet = manageLine(line);
					} else {
						int connect = 0;
						String[] packetLine = line.split(" ");
						if (packetLine.length > 3 && packetLine[1].equalsIgnoreCase("packets") && packetLine[2].equalsIgnoreCase("transmitted,")) {
							connect = Integer.parseInt(packetLine[3]) * 100 / Integer.parseInt(packetLine[0]);
							packet[0] = new Integer(connect);
						}
					}
					if (line.contains("=")) {
						String[] lines = line.split(",");
						if (lines.length >= 3) {
							String values0 = lines[0].substring(0, lines[0].indexOf("=")).trim();

							if (values0.equalsIgnoreCase("Minimum")) {
								Integer[] _packet = null;
								_packet = manageResponseLine(line);
								packet[1] = new Integer(_packet[0] + "");
							}
						}
					} else {
						String[] lines = line.split(" ");
						if (lines.length >= 3) {
							if (lines[0].trim().equalsIgnoreCase("rtt")) {
								String[] avgtime = lines[3].trim().split("/");
								packet[1] = new Integer(avgtime[1] + "");
							}
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				process.destroy();
				is.close();
				isr.close();
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packet;
	}

}
