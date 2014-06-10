/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.afunms.discovery.DiscoverResource;
import com.afunms.sysset.model.Service;

@SuppressWarnings("unchecked")
public class NetworkUtil {
	/**
	 * ���һ��ip�Ƿ�Ϊ�Ϸ���ip
	 */
	public static boolean checkIp(String ipAddress) {
		boolean isValid = true;
		try {
			StringTokenizer st = new StringTokenizer(ipAddress, ".");
			int len = st.countTokens();
			if (len != 4) {
				return false;
			}

			int ipSegment = 0;
			for (int i = 0; i < len; i++) {
				ipSegment = Integer.parseInt(st.nextToken());
				if (ipSegment < 0 || ipSegment > 255) {
					isValid = false;
					break;
				}
			}
		} catch (Exception e) {
			SysLogger.error("��Ч��IP��ַ:" + ipAddress);
			isValid = false;
		}
		return isValid;
	}

	/**
	 * ȷ�����������еķ���
	 */

	public static boolean checkService(String ipAddress) {
		List list = DiscoverResource.getInstance().getServiceList();
		boolean result = false;
		for (int i = 0; i < list.size(); i++) {
			Service vo = (Service) list.get(i);
			if (vo.getPort() == 23) {
				continue; // �����telnet,��Ϊ�����豸Ҳ��telnet
			}

			Socket socket = new Socket();
			try {
				InetAddress addr = InetAddress.getByName(ipAddress);
				SocketAddress sockaddr = new InetSocketAddress(addr, vo.getPort());
				socket.connect(sockaddr, vo.getTimeOut());
				Service newVo = new Service();
				newVo.setPort(vo.getPort());
				newVo.setService(vo.getService());
				result = true;
				break;
			} catch (SocketTimeoutException ste) {
			} catch (IOException ioe) {
			} finally {
				try {
					socket.close();
				} catch (IOException ioe) {
				}
			}
		}
		return result;
	}

	public static boolean checkService(String ipAddress, int port) {
		boolean result = false;
		Socket socket = new Socket();
		try {
			InetAddress addr = InetAddress.getByName(ipAddress);
			SocketAddress sockaddr = new InetSocketAddress(addr, port);
			socket.connect(sockaddr, 500);
			result = true;
		} catch (SocketTimeoutException ste) {
			result = false;
		} catch (IOException ioe) {
			result = false;
		} finally {
			try {
				socket.close();
			} catch (IOException ioe) {
			}
		}
		return result;
	}

	/**
	 * ͨ����������ȡ�øö������й��ж��ٸ�IP��ַ
	 */
	public static int getIpTotalFromMask(String netMask) {
		int[] masks = parseIp(netMask);
		if (masks == null) {
			return 0;
		}

		int ipTotal = 0;
		for (int i = 0; i < 4; i++) {
			if (masks[i] != 255) {
				if (i == 2) {
					ipTotal = (255 - masks[i]) * 256 + 256;
				} else if (i == 3) {
					ipTotal = 256 - masks[i];
				}
				break;
			}
		}
		return ipTotal;
	}

	/**
	 * getTheNetworkAddr����
	 * 
	 * @param ipAddress
	 *            ��ַ;netMask ��������
	 * @return ͨ�������������IP������������������ַ,��IpAddressΪ192.168.1.200,maskΪ255.255.255.0
	 *         ��ö�����������ַΪ192.168.1.0
	 */
	public static String getNetAddress(String ipAddress, String netMask) {
		long ipAddrLong = ip2long(ipAddress);
		long netaskLong = ip2long(netMask);
		long tmpLong = ipAddrLong & netaskLong;

		return long2ip(tmpLong);
	}

	public static String getTheFdbOid(String mac) {
		String returnStr = "1.3.6.1.2.1.17.4.3.1.2";
		String[] macSegment = new String[6];

		try {
			StringTokenizer st = new StringTokenizer(mac, ":");
			for (int i = 0; i < 6; i++) {
				macSegment[i] = st.nextToken();
			}
		} catch (NoSuchElementException e) {
			macSegment = null;
		}
		if (macSegment == null) {
			return null;
		}

		for (int i = 0; i < 6; i++) {
			int tmpInt = transHexToInt(macSegment[i]);
			returnStr = returnStr + "." + tmpInt;
		}
		return returnStr;
	}

	/**
	 * �ѱ�׼ipת����ʮ����ip
	 */
	public static long ip2long(String ipAddress) {
		int[] ipSegment = parseIp(ipAddress);
		if (ipSegment == null) {
			return 0;
		}

		long longIp = 0;
		int k = 24;
		for (int i = 0; i < ipSegment.length; i++) {
			longIp += ((long) ipSegment[i]) << k;
			k -= 8;
		}
		return longIp;
	}

	/**
	 * ����ȷ��·�ɱ���һ���е�dest�������ַ������·���豸��ַ
	 */
	public static boolean isNetAddress(String ipAddress, String netMask) {
		int[] ips = parseIp(ipAddress);
		int[] masks = parseIp(netMask);
		String result = null;
		for (int i = 0; i < 4; i++) {
			if (result == null) {
				result = "" + (ips[i] & masks[i]);
			} else {
				result += "." + (ips[i] & masks[i]);
			}
		}
		boolean res = false;
		if (result.equals(ipAddress)) {
			res = true;
		}
		return res;
	}

	/**
	 * ȷ��ĳ����ַ�ǲ�����Ҫ���ε�������
	 */
	public static boolean isShieldAddress(String address) {
		if (DiscoverResource.getInstance().getShieldSet().size() == 0) {
			return false;
		}

		boolean result = false;
		Iterator iterator = DiscoverResource.getInstance().getShieldSet().iterator();
		while (iterator.hasNext()) {
			String netAddress = (String) iterator.next();
			if (address.indexOf(netAddress) != -1) {
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * ���һ��ip�Ƿ���һ����Ч��������ַ(�Ƿ�������ķ�Χ��) netAddress:����IP��ַ netMask:��������
	 * ipAddress:����IP��ַ
	 */
	public static boolean isValidIP(String netAddress, String netMask, String ipAddress) {
		boolean returnbool = false;
		long netiplong = ip2long(netAddress);
		long ipAddrlong = ip2long(ipAddress);
		long allIpNum = (new Long("4294967296")).longValue();
		long netmaskLong = ip2long(netMask);
		long NetmaskTotalIp = allIpNum - netmaskLong;
		if (ipAddrlong > netiplong && ipAddrlong < netiplong + NetmaskTotalIp) {
			returnbool = true;
		}
		return returnbool;
	}

	/**
	 * ��ʱֻ��255.255.255.*��������в���
	 */
	public static boolean isValidNetMask(String netMask) {
		if ("0.0.0.0".equals(netMask) || "255.255.255.255".equals(netMask)) {
			return false;
		}

		int[] ipSegment = parseIp(netMask);
		if (ipSegment == null) {
			return false;
		}

		if (ipSegment[0] == 255 && ipSegment[1] == 255) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ��ʮ����ipת���ɱ�׼��IP
	 */
	public static String long2ip(long ip) {
		int b[] = new int[4];
		b[0] = (int) (ip >> 24 & 255L);
		b[1] = (int) (ip >> 16 & 255L);
		b[2] = (int) (ip >> 8 & 255L);
		b[3] = (int) (ip & 255L);
		String strIP = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." + Integer.toString(b[3]);
		return strIP;
	}

	/**
	 * ����ip
	 */
	public static int[] parseIp(String ipAddress) {
		if (!checkIp(ipAddress)) {
			return null;
		}
		int[] ipSegment = new int[4];

		StringTokenizer st = new StringTokenizer(ipAddress, ".");
		for (int i = 0; i < 4; i++) {
			ipSegment[i] = Integer.parseInt(st.nextToken());
		}
		return ipSegment;
	}

	public static int ping(String ipAddress) {
		String line = null;
		String pingInfo = null;
		try {
			StringBuffer sb = new StringBuffer(300);
			Process process = Runtime.getRuntime().exec("ping -n 1 " + ipAddress);
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}

			process.destroy();
			in.close();
			pingInfo = sb.toString();
		} catch (IOException ioe) {
			pingInfo = null;
		}

		if (pingInfo == null || pingInfo.indexOf("Destination host unreachable") != -1 || pingInfo.indexOf("Unknown host") != -1 || pingInfo.indexOf("Request timed out.") != -1) {
			return 0;
		} else {
			return 1;
		}
	}

	public static String pingReport(String ipAddress) {
		String line = null;
		String pingInfo = null;
		try {
			StringBuffer sb = new StringBuffer(300);
			Process process = Runtime.getRuntime().exec("ping " + ipAddress);
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}

			process.destroy();
			in.close();
			pingInfo = sb.toString();
		} catch (IOException ioe) {
			pingInfo = null;
		}
		return pingInfo;
	}

	public static int transHexToInt(String hexStr) {
		int totalInt = 0;
		try {
			char tmpChars[] = hexStr.toLowerCase().toCharArray();
			if (tmpChars[0] == 'a') {
				totalInt += 10;
			}
			if (tmpChars[0] == 'b') {
				totalInt += 11;
			}
			if (tmpChars[0] == 'c') {
				totalInt += 12;
			}
			if (tmpChars[0] == 'd') {
				totalInt += 13;
			}
			if (tmpChars[0] == 'e') {
				totalInt += 14;
			}
			if (tmpChars[0] == 'f') {
				totalInt += 15;
			}
			if (tmpChars[0] < 'a') {
				totalInt += tmpChars[0] - 48;
			}
			totalInt *= 16;
			if (tmpChars[1] == 'a') {
				totalInt += 10;
			}
			if (tmpChars[1] == 'b') {
				totalInt += 11;
			}
			if (tmpChars[1] == 'c') {
				totalInt += 12;
			}
			if (tmpChars[1] == 'd') {
				totalInt += 13;
			}
			if (tmpChars[1] == 'e') {
				totalInt += 14;
			}
			if (tmpChars[1] == 'f') {
				totalInt += 15;
			}
			if (tmpChars[1] < 'a') {
				totalInt += tmpChars[1] - 48;
			}
		} catch (Exception exception) {
		}
		return totalInt;
	}

	public NetworkUtil() {
	}

	public List parseAllIp(String startip, String endip) {
		List list = new ArrayList();
		int[] sip = parseIp(startip);
		int[] eip = parseIp(endip);
		int num = eip[3] - sip[3] + 1;
		for (int i = 0; i < num; i++) {

			int ip4 = sip[3] + i;
			String ip = sip[0] + "." + sip[1] + "." + sip[2] + "." + ip4;
			list.add(ip);
		}
		return list;
	}
}
