package com.afunms.monitor.executor.base;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import com.afunms.common.util.SnmpService;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SnmpMonitor extends BaseMonitor {

	public SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public Calendar date = Calendar.getInstance();

	public static Hashtable Interface_IfType = null;
	public static Hashtable HOST_hrSWRun_hrSWRunType = null;
	public static Hashtable HOST_hrSWRun_hrSWRunStatus = null;
	public static Hashtable HOST_Type_Producter = null;
	protected static SnmpService snmp = new SnmpService();
	static {
		Interface_IfType = new Hashtable();
		Interface_IfType.put("1", "other(1)");
		Interface_IfType.put("6", "ethernetCsmacd(6)");
		Interface_IfType.put("23", "ppp(23)");
		Interface_IfType.put("28", "slip(28)");
		Interface_IfType.put("33", "Console port");
		Interface_IfType.put("53", "propVirtual(53)");
		Interface_IfType.put("117", "gigabitEthernet(117)");
		Interface_IfType.put("131", "tunnel(131)");
		Interface_IfType.put("135", "others(135)");
		Interface_IfType.put("136", "others(136)");
		Interface_IfType.put("142", "others(142)");
		Interface_IfType.put("54", "others(54)");
		Interface_IfType.put("5", "others(5)");

	}
	static {
		HOST_hrSWRun_hrSWRunType = new Hashtable();
		HOST_hrSWRun_hrSWRunType.put("1", "未知");
		HOST_hrSWRun_hrSWRunType.put("2", "操作系统");
		HOST_hrSWRun_hrSWRunType.put("3", "设备驱动");
		HOST_hrSWRun_hrSWRunType.put("4", "应用程序");
	}
	static {
		HOST_hrSWRun_hrSWRunStatus = new Hashtable();
		HOST_hrSWRun_hrSWRunStatus.put("1", "正在运行");
		HOST_hrSWRun_hrSWRunStatus.put("2", "等待");
		HOST_hrSWRun_hrSWRunStatus.put("3", "运行等待结果");
		HOST_hrSWRun_hrSWRunStatus.put("4", "有问题");
	}
	static {
		HOST_Type_Producter = new Hashtable();
		HOST_Type_Producter.put("1.3.6.1.4.1.9.1.248", "cisco");
		HOST_Type_Producter.put("2", "等待");
		HOST_Type_Producter.put("3", "运行等待结果");
		HOST_Type_Producter.put("4", "有问题");
	}

	public SnmpMonitor() {
	}

	protected int parseInt(String arg) {
		int rtInteger = -1;
		if (null != arg && !"null".equals(arg) && !"".equals(arg)) {
			rtInteger = Integer.parseInt(arg.trim());
		}
		return rtInteger;
	}

	protected String parseString(Object ob) {
		String rtString = "NaV";
		if (null != ob && !("null").equals(ob) && !("").equals(ob)) {
			rtString = (String) ob;
		}
		return rtString.trim();
	}

	/*
	 * molecular 分子 denominator 分母
	 */
	protected int divide(int molecular, int denominator) {
		int rtInteger = -1;
		if (denominator == 0) {
			rtInteger = 0;
		} else {
			rtInteger = molecular / denominator;
		}
		return rtInteger;
	}
}