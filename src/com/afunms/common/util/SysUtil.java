package com.afunms.common.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@SuppressWarnings("unchecked")
public class SysUtil {
	public static List checkSize(String sizestr) {
		List rvalue = new ArrayList();
		float size = Float.parseFloat(sizestr);
		String unit = "";
		float _size = 0.0f;
		_size = size * 1.0f / 1024;
		unit = "K";
		if (_size >= 1024.0f) {
			_size = _size / 1024;
			unit = "M";
			if (_size >= 1024.0f) {
				_size = _size / 1024;
				unit = "G";
			}
		}
		rvalue.add(0, Math.round(_size) + "");
		rvalue.add(1, unit);
		return rvalue;
	}

	public static String CheckStr(String str, int length) {
		if (str.getBytes().length > length) {
			return "告警信息过长";
		}
		return str;
	}

	public static int checkTel(String str) {
		if (str.length() != 11) {
			return -1;
		}

		if ((str.startsWith("130")) || (str.startsWith("131")) || (str.startsWith("132")) || (str.startsWith("133"))) {
			return 1;
		}

		return 0;
	}

	/**
	 * 把一个形如"yyyy-MM-dd"日期型转换成一个长整数
	 */
	public static long dateToLong(String date) {
		long timeLong = 0;
		try {
			SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
			Date dateTime = dateFormat.parse(date);
			timeLong = dateTime.getTime() / 1000;
		} catch (ParseException e) {
			timeLong = 0;
		}
		return timeLong;
	}

	/**
	 * 计算两个时间点间的时间差
	 */
	public static String diffTwoTime(String time1, String time2) {
		if (time1 == null || time2 == null) {
			return "";
		}

		StringBuffer timeStr = new StringBuffer(10);
		long diffTime = timeToLong(time2) - timeToLong(time1);
		long hh24 = diffTime / 3600;
		long surplus = diffTime % 3600;

		long mi = surplus / 60;
		long ss = surplus % 60;

		if (hh24 < 10) {
			timeStr.append("0");
		}
		timeStr.append(hh24);

		timeStr.append(":");
		if (mi < 10) {
			timeStr.append("0");
		}
		timeStr.append(mi);

		timeStr.append(":");
		if (ss < 10) {
			timeStr.append("0");
		}
		timeStr.append(ss);

		return timeStr.toString();
	}

	public static String doip(String ip) {
		ip = ip.replaceAll("\\.", "_");
		return ip;
	}

	public static double formatDouble(double a, double b) {
		if (b == 0) {
			return 0;
		}

		DecimalFormat df = new DecimalFormat("#.00");
		return Double.parseDouble(df.format(a / b));
	}

	public static String formatString(String s) {
		if (s == null || s.equals("")) {
			return s;
		}

		StringBuffer stringbuffer = new StringBuffer();
		for (int i = 0; i <= s.length() - 1; i++) {
			if (s.charAt(i) == '\r') {
				stringbuffer = stringbuffer.append("<br>");
			} else {
				stringbuffer = stringbuffer.append(s.substring(i, i + 1));
			}
		}

		return stringbuffer.toString();
	}

	/**
	 * 转为中文
	 */
	public static String getChinese(String ss) {
		String strpa = "";
		try {
			if (ss != null) {
				strpa = new String(ss.getBytes("ISO-8859-1"), "GB2312");
			} else {
				strpa = ss;
			}
		} catch (Exception e) {
			strpa = "";
		}
		return strpa;
	}

	/**
	 * 得到当前日期
	 */
	public static String getCurrentDate() {
		SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd");
		String currentDate = timeFormatter.format(new java.util.Date());
		return currentDate;
	}

	/**
	 * 得到当前小时
	 */
	public static int getCurrentHour() {
		Calendar cal = Calendar.getInstance();
		int curHour = cal.get(Calendar.HOUR_OF_DAY);
		return curHour;
	}

	/**
	 * 得到现在的时间,用长形型表示
	 */
	public static long getCurrentLongTime() {
		return (new java.util.Date()).getTime() / 1000;
	}

	/**
	 * 得到当前月份
	 */
	public static int getCurrentMonth() {
		Calendar cal = new GregorianCalendar();
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 得到当前时间
	 */
	public static String getCurrentTime() {
		SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = timeFormatter.format(new java.util.Date());
		return currentTime;
	}

	/**
	 * 得到当前年份
	 */
	public static int getCurrentYear() {
		Calendar cal = new GregorianCalendar();
		return cal.get(Calendar.YEAR);
	}

	/**
	 * 得到某天的日期 interval 距今天的天数
	 */
	public static String getDate(int interval) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, interval);
		String date = new java.sql.Date(cal.getTimeInMillis()).toString();
		return date;
	}

	public static String getDay() {
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		String str = s.format(Long.valueOf(System.currentTimeMillis()));
		return str;
	}

	/**
	 * 计算两个日期之间的天数
	 */
	public static int getDaysBetweenTwoDates(long beginDate, long endDate) {
		long result = 0;
		if (beginDate > endDate) {
			result = (beginDate - endDate) / 86400;
		} else {
			result = (endDate - beginDate) / 86400;
		}
		return (int) result;
	}

	/**
	 * 一个月有几天
	 */
	public static int getDaysOfMonth(int year, int month) {
		if (year < 1000 || year > 3000 || month < 1 || month > 12) {
			return 0;
		}

		int days = 0;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
			days = 31;
		} else if (month == 4 || month == 6 || month == 9 || month == 11) {
			days = 30;
		} else // month==2
		{
			if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) {
				days = 29;
			} else {
				days = 28;
			}
		}
		return days;
	}

	public static String getLongID() {
		return String.valueOf((long) ((new java.util.Date()).getTime() * Math.random()));
	}

	/**
	 * 根据告警次数，得到业务系统运行情况
	 * 
	 * @param levelone
	 * @param leveltwo
	 * @param levelthree
	 * @return
	 */
	public static synchronized String getRunAppraise(int levelone, int leveltwo, int levelthree) {
		if ((levelone + leveltwo + levelthree) > 0 && (levelone + leveltwo + levelthree) < 3) {
			return "良";
		}
		if ((levelone + leveltwo + levelthree) > 3) {
			return "差";
		}
		if ((levelone + leveltwo + levelthree) == 0) {
			return "优";
		}
		return "优";
	}

	public static String getSecond() {
		SimpleDateFormat s = new SimpleDateFormat("HHmmss");
		String str = s.format(Long.valueOf(System.currentTimeMillis()));
		return str;
	}

	// 吉林中行短信

	public static String getStrByLength(String strParameter, int limitLength) {
		String return_str = strParameter;
		int temp_int = 0;
		int cut_int = 0;
		byte[] b = strParameter.getBytes();

		for (int i = 0; i < b.length; ++i) {
			if (b[i] >= 0) {
				++temp_int;
			} else {
				temp_int += 2;
				++i;
			}
			++cut_int;

			if (temp_int >= limitLength) {
				if ((temp_int % 2 != 0) && (b[(temp_int - 1)] < 0)) {
					--cut_int;
				}

				return_str = return_str.substring(0, cut_int);
				break;
			}
		}
		return return_str;
	}

	/**
	 * 把null替换成""
	 */
	public static String ifNull(String str) {
		if (str == null || str.equals("null")) {
			return "";
		} else {
			return str;
		}
	}

	/**
	 * 把null替换成别的字符
	 */
	public static String ifNull(String str, String replaceStr) {
		if (str == null || "".equals(str)) {
			return replaceStr;
		} else {
			return str;
		}
	}

	/**
	 * 把一个长整数转换成时间
	 */
	public static String longToTime(long timeLong) {
		Date date = new Date(timeLong);
		SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return timeFormatter.format(date);
	}

	public static String makeString(int length) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < length; ++i) {
			s.append("");
		}

		return s.toString();
	}

	/**
	 * 把一个形如"yyyy-mm-dd hh24:mi:ss"时间型转换成一个长整数
	 */
	public static long timeToLong(String time) {
		long timeLong = 0;
		try {
			SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date = dateFormat.parse(time);
			timeLong = date.getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeLong;
	}

	private SysUtil() {
	}
}
