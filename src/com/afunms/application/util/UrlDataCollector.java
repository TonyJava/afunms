package com.afunms.application.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.afunms.application.model.Urlmonitor_realtime;
import com.afunms.application.model.WebConfig;

@SuppressWarnings("unchecked")
public class UrlDataCollector {

	private static Properties props;

	static FileInputStream fis;

	public UrlDataCollector() {
	}

	public static Urlmonitor_realtime getUrlmonitor_realtime(WebConfig urlconf, boolean old, String s) throws Exception {
		return getUrlmonitor_realtime(urlconf, true, old, s);
	}

	public static Urlmonitor_realtime getUrlmonitor_realtime(WebConfig urlconf, boolean flag, boolean old, String str) throws Exception {
		String s = urlconf.getStr();

		int k = 1;
		String s3 = urlconf.getAvailability_string();
		String s4 = urlconf.getUnavailability_string();
		int v = urlconf.getVerify();
		boolean flag1 = false;
		if (v == 1) {
			flag1 = true;
		}
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(s);

		long starttime = 0;
		long endtime = 0;
		long condelay = 0;
		int conflag = 1;
		try {

			URL url = new URL(urlconf.getStr());
			starttime = System.currentTimeMillis();
			URLConnection con = url.openConnection();

			con.setConnectTimeout(urlconf.getTimeout());
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			con.setAllowUserInteraction(true);
			String s7 = "";

			try {
				InputStreamReader _read = new InputStreamReader(con.getInputStream(), "GBK");
				BufferedReader _breader = new BufferedReader(_read);
				String _oneRow = "";

				while ((_oneRow = _breader.readLine()) != null) {// 读取当前日期的日志文件，要根据读取行数定位。
					s7 += _oneRow + "\n";
				}
				_breader.close();
				_read.close();
			} catch (Exception e) {
				conflag = 0;
				e.printStackTrace();
			}

			if (k != 0 && s3 != null && s3.length() > 0) {
				if (!doAvailabilityCheck(s7, s3, true)) {
					k = 0;
				}
			}
			if (k != 0 && s4 != null && s4.length() > 0) {
				if (!doAvailabilityCheck(s7, s4, false)) {
					k = 0;
				}
			}
			if (flag && flag1 && k == 0)
				return getUrlmonitor_realtime(urlconf, false, old, str);

			Urlmonitor_realtime ur = new Urlmonitor_realtime();

			ur.setIs_canconnected(conflag);
			ur.setIs_valid(new Integer(k));
			ur.setCondelay(new Integer(condelay + ""));
			if (old == true) {
				if (s7.equals(str)) {
					ur.setIs_refresh(new Integer(0));
					ur.setPage_context(s7);
				} else {
					ur.setIs_refresh(new Integer(1));
					ur.setPage_context(str);
				}
			} else {
				ur.setIs_refresh(new Integer(0));
				ur.setPage_context(s7);

			}
			ur.setReason("返回：");
			ur.setMon_time(Calendar.getInstance());

			// 使用系统提供的默认的恢复策略
			get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			// 执行getMethod
			int statusCode = client.executeMethod(get);
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + get.getStatusLine());
			}
			// 读取内容
			byte[] responseBody = get.getResponseBody();
			// 处理内容
			String charset_str = get.getResponseCharSet();// 获得编码信息
			String newStr = new String(responseBody, charset_str);
			ur.setPagesize((newStr.length() / 1024) + "");
			ur.setPage_context(newStr);
			ur.setKey_exist("");
			return ur;
		} catch (HttpException httpException) {
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;

			httpException.printStackTrace();
			Urlmonitor_realtime ur3 = new Urlmonitor_realtime();
			ur3.setIs_canconnected(conflag);
			ur3.setIs_valid(new Integer(0));
			ur3.setIs_refresh(new Integer(0));
			ur3.setPage_context(str);
			ur3.setReason(props.getProperty("600"));
			ur3.setMon_time(Calendar.getInstance());
			ur3.setCondelay(new Integer("" + condelay));
			ur3.setPagesize("0");
			ur3.setKey_exist("");
			return ur3;
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;

			if (flag && flag1) {
				return getUrlmonitor_realtime(urlconf, false, old, str);
			} else {
				Urlmonitor_realtime ur1 = new Urlmonitor_realtime();
				ur1.setIs_canconnected(conflag);
				ur1.setIs_valid(new Integer(0));
				ur1.setIs_refresh(new Integer(0));
				ur1.setPage_context(str);

				ur1.setReason("页面不能连接");
				ur1.setMon_time(Calendar.getInstance());
				ur1.setCondelay(new Integer("" + condelay));
				ur1.setPagesize("0");
				ur1.setKey_exist("");
				return ur1;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;

			if (flag && flag1) {
				return getUrlmonitor_realtime(urlconf, false, old, str);
			} else {
				exception.printStackTrace();
				Urlmonitor_realtime ur6 = new Urlmonitor_realtime();

				ur6.setIs_canconnected(conflag);
				ur6.setIs_valid(new Integer(0));
				ur6.setIs_refresh(new Integer(0));
				ur6.setPage_context(str);
				ur6.setReason(" Exception while trying to acces the url " + exception.toString());
				ur6.setMon_time(Calendar.getInstance());
				ur6.setCondelay(new Integer("" + condelay));
				ur6.setPagesize("0");
				ur6.setKey_exist("");
				return ur6;
			}
		} finally {
			get.releaseConnection();
		}
	}

	public static String[] getUrlmonitor_realtime(String urlstr, boolean flag, boolean old, String str) throws Exception {
		String[] retValue = new String[2];
		GetMethod get = new GetMethod(urlstr);
		long starttime = 0;
		long endtime = 0;
		long condelay = 0;
		try {
			URL url = new URL(urlstr);
			starttime = System.currentTimeMillis();
			URLConnection con = url.openConnection();
			con.setConnectTimeout(10000);
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			con.setAllowUserInteraction(true);
			InputStreamReader _read = new InputStreamReader(con.getInputStream(), "GBK");
			BufferedReader _breader = new BufferedReader(_read);
			String _oneRow = "";
			String s7 = "";
			while ((_oneRow = _breader.readLine()) != null) {// 读取当前日期的日志文件，要根据读取行数定位。
				s7 += _oneRow + "\n";
			}
			_breader.close();
			_read.close();
			retValue[0] = "1";
			retValue[1] = condelay + "";
		} catch (HttpException httpException) {
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			retValue[0] = "0";
			retValue[1] = condelay + "";
			httpException.printStackTrace();

			httpException.printStackTrace();
		} catch (IOException ioexception) {
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			retValue[0] = "0";
			retValue[1] = condelay + "";
			ioexception.printStackTrace();

		} catch (Exception exception) {
			endtime = System.currentTimeMillis();
			condelay = endtime - starttime;
			retValue[0] = "0";
			retValue[1] = condelay + "";
			exception.printStackTrace();

		} finally {
			get.releaseConnection();
		}
		return retValue;
	}

	private static boolean doAvailabilityCheck(String s, String s1, boolean flag) {
		ArrayList arraylist = getStrings(s1);
		for (int i = 0; i < arraylist.size(); i++) {
			String s2 = (String) arraylist.get(0);
			if (s.indexOf(s2) == -1) {
				if (flag)
					return false;
			} else if (!flag)
				return false;
		}

		return true;
	}

	private static ArrayList getStrings(String s) {
		char ac[] = s.toCharArray();
		boolean flag = true;
		String s1 = "";
		ArrayList arraylist = new ArrayList();
		for (int i = 0; i < ac.length; i++) {
			char c = ac[i];
			if (c != '"' && c != ' ')
				s1 = s1 + String.valueOf(c);
			if (c == '"') {
				if (flag) {
					flag = false;
					if (!s1.trim().equals("")) {
						arraylist.add(s1.trim());
						s1 = "";
					}
				} else {
					flag = true;
				}
			} else if (c == ' ') {
				if (flag && !s1.trim().equals("")) {
					arraylist.add(s1.trim());
					s1 = "";
				}
				s1 = s1 + String.valueOf(c);
			}
		}

		if (!s1.trim().equals("")) {
			arraylist.add(s1.trim());
			s1 = "";
		}
		return arraylist;
	}

}