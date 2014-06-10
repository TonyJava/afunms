package com.afunms.servlet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.SnmpUtils;
import com.afunms.query.QueryService;

/**
 * 可用性,检测工具类,基于DWR
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class AvailabilityCheckUtil {

	/**
	 * @param dbType
	 * @param ip
	 * @param iOrn
	 * @param port
	 * @param user
	 * @param password
	 * @return 返回测试结果 [字符串]
	 */
	public String dbCheck(String dbType, String ip, String iOrn, String port, String user, String password) {
		StringBuffer sb = new StringBuffer("连接");
		String driver = (String) null;
		String url = (String) null;
		String pwd = (String) null;
		try {
			pwd = EncryptUtil.decode(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (dbType.equalsIgnoreCase("mysql")) {
			driver = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://" + ip + ":" + port + "/" + iOrn + "?" + "useUnicode=true&characterEncoding=utf-8";
		} else if (dbType.equalsIgnoreCase("oracle")) {
			driver = "oracle.jdbc.driver.OracleDriver";
			url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + iOrn;
		} else if (dbType.equalsIgnoreCase("sqlserver")) {
			driver = "net.sourceforge.jtds.jdbc.Driver";
			url = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";DatabaseName=model;charset=GBK;SelectMethod=CURSOR";
		}
		QueryService service = new QueryService();
		boolean isSuccess = service.testConnection(driver, url, user, pwd);
		if (isSuccess) {
			sb.append("成功");
		} else {
			sb.append("失败!");
		}
		return sb.toString();
	}

	/**
	 * @param ip
	 * @param port
	 * @param version
	 * @param timeOut
	 * @param community
	 * @param retries
	 * @return
	 */
	public String snmpCheck(String ip, String port, String version, String timeOut, String community, String retries) {
		StringBuffer sb = new StringBuffer("");
		String oid = ".1.3.6.1.2.1.1.1.0";
		try {
			sb.append(SnmpUtils.get(ip, community, oid, stringToInteger(version), stringToInteger(port), stringToInteger(retries), stringToInteger(timeOut)));
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("连接失败");
		}
		return sb.toString();
	}

	private int stringToInteger(String arg) {
		int rt = 0;
		if (null != arg && !arg.equals("")) {
			rt = Integer.parseInt(arg);
		}
		return rt;
	}

	/**
	 * @param ip
	 * @param port
	 * @param user
	 * @param password
	 * @return
	 */
	public String tomcatCheck(String ip, String port, String user, String password) {
		StringBuffer sb = new StringBuffer("连接");
		Map map = new HashMap();
		String[] credentials = new String[] { user, password };
		map.put("jmx.remote.credentials", credentials);
		JMXConnector connector = null;
		String jmxURL = "service:jmx:rmi:///jndi/rmi://" + ip + ":" + port + "/jmxrmi";
		JMXServiceURL serviceURL;
		try {
			serviceURL = new JMXServiceURL(jmxURL);
			try {
				connector = JMXConnectorFactory.connect(serviceURL, map);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException ue) {
			ue.printStackTrace();
		}
		if (null != connector) {
			sb.append("成功");
		} else {
			sb.append("无法远程访问Tomcat JMX ");
		}
		return sb.toString();
	}

	public String weblogicCheck(String ip, String port, String version, String timeOut, String community, String retries) {
		StringBuffer sb = new StringBuffer("");
		String[] oid = { ".1.3.6.1.4.1.140.625.740.1.10" };
		String[][] rtArray = null;
		try {
			rtArray = SnmpUtils.getList(ip, community, oid, stringToInteger(version), stringToInteger(port), stringToInteger(retries), stringToInteger(timeOut));
			if (null != rtArray && rtArray.length > 0) {
				sb.append("连接成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			sb.append("连接失败");
		}
		return sb.toString();
	}
}
