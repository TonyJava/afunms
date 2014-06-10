package com.afunms.vmware.vim25.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.xpath.DefaultXPath;

/**
 * 常用工具类
 * 
 * @author LXL
 * 
 */
public class Util {

	private static final Log LOGGER = LogFactory.getLog(Util.class);

	// 无_数字
	public static final int NONE_INT = -1;

	// 真_数字
	public static final int TRUE_INT = 1;

	// 假_数字
	public static final int FALSE_INT = 0;

	// 广播地址
	public static final String BROADCAST_ADDR = "255.255.255.255";

	// 本地地址前缀
	public static final String LOCALHOST_PREFIX = "127.";

	// 文件名分隔符
	public static final String SEPARATOR = "-";

	// 逗号分隔符
	public static final String SEPARATOR_COMMA = ",";

	// 分号分隔符
	public static final String SEPARATOR_SEMICOLON = ";";

	// 句号分隔符
	public static final String SEPARATOR_PERIOD = ".";

	// 下划线分隔符
	public static final String SEPARATOR_UNDERLINE = "_";

	// 等号分隔符
	public static final String SEPARATOR_EQUAL = "=";

	// 单引号
	public static final String SINGLE_QUOTATION = "'";

	// 左括号
	public static final String LEFT_PARENTHESIS = "(";

	// 右括号
	public static final String RIGHT_PARENTHESIS = ")";

	// 空格
	public static final String SPACE = " ";

	// 文件扩展名
	public static final String FILENAME_EXTENSION = ".xml";

	// XML文件扩展名
	public static final String XMLFILENAME_EXTENSION = FILENAME_EXTENSION;

	// 备份文件扩展名
	public static final String BAKFILENAME_EXTENSION = ".bak";

	// 默认持续时间样式
	public static final String DEFAULTDURATION_PATTERN = "dd''天''HH''小时''mm''分钟''ss''秒''";

	// 默认日期样式
	public static final String DEFAULTDATE_PATTERN = "yyyy-MM-dd";
	private static final SimpleDateFormat DEFAULTDATESDF = new SimpleDateFormat(
			DEFAULTDATE_PATTERN);

	// 默认日期（无年）格式
	public static final String DEFAULTDATENOYEAR_PATTERN = "MM-dd";
	private static final SimpleDateFormat DEFAULTDATENOYEARSDF = new SimpleDateFormat(
			DEFAULTDATENOYEAR_PATTERN);

	// 默认时间格式
	public static final String DEFAULTTIME_PATTERN = "HH:mm:ss";
	private static final SimpleDateFormat DEFAULTTIMESDF = new SimpleDateFormat(
			DEFAULTTIME_PATTERN);

	// 默认时间样式
	public static final String DEFAULTDATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
	private static final SimpleDateFormat DEFAULTDATETIMESDF = new SimpleDateFormat(
			DEFAULTDATETIME_PATTERN);

	// 数据库时间样式
	public static final String DBDATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final SimpleDateFormat DBDATETIMESDF = new SimpleDateFormat(
			DBDATETIME_PATTERN);

	// 数据库时间样式从月份开始(无年)
	public static final String DBDATETIMENOYEAR_PATTERN = "MM-dd HH:mm:ss";
	private static final SimpleDateFormat DBDATETIMENOYEARSDF = new SimpleDateFormat(
			DBDATETIMENOYEAR_PATTERN);

	// 文件时间样式
	public static final String FILEDATETIME_PATTERN = "yyyyMMddHHmmss";
	private static final SimpleDateFormat FILEDATETIMESDF = new SimpleDateFormat(
			FILEDATETIME_PATTERN);

	// 无分隔符的时间样式
	// public static final String IDDATETIME_PATTERN = "yyyyMMddHHmmssSSS";
	// private static final SimpleDateFormat IDDATETIMESDF = new
	// SimpleDateFormat(
	// IDDATETIME_PATTERN);

	// 反射调用时的方法名
	public static final String REFLECT_METHODNAME = "process";

	// IP匹配的样式
	private static final Pattern ipPattern = Pattern
			.compile("^([0-9]{1,3}\\.){3}[0-9]{1,3}$");

	// XPATH集合
	private static ConcurrentHashMap<String, DefaultXPath> xPathMap = new ConcurrentHashMap<String, DefaultXPath>();

	// 默认百分比数字格式
	private static final DecimalFormat DEFAULTPERCENTDF = new DecimalFormat(
			"##.##%");

	// 2位不带百分号_百分比数字格式
	private static final DecimalFormat BITS2_NOPERCENT_PERCENTDF = new DecimalFormat(
			"##.##");

	// 默认百分比数字格式
	private static final DecimalFormat FOURDECIMAL_PERCENTDF = new DecimalFormat(
			"##.####%");

	// 2为小数点数字转换
	private static final DecimalFormat BITS2_DECIMALTDF = new DecimalFormat(
			"#.##");

	// 本地地址
	private static String localHost = "";

	// 秒到毫秒的倍数
	public static final int SECONDS_MILLISECONDS = 1000;

	// 分钟对应的毫秒数
	public static final long MINUTE_MILLISECONDS = 60 * SECONDS_MILLISECONDS;

	// 小时对应的毫秒数
	public static final long HOUR_MILLISECONDS = 60 * MINUTE_MILLISECONDS;

	// 8小时对应的毫秒数
	private static final long HOUR_8_MILLISECONDS = 8 * HOUR_MILLISECONDS;

	// 16小时对应的毫秒数
	// private static final long HOUR_16_MILLISECONDS = 16 * HOUR_MILLISECONDS;

	// 分钟到秒倍数
	public static final int MINUTE_SECONDS = 60;

	// 小时到秒倍数
	public static final int HOUR_SECONDS = 60 * MINUTE_SECONDS;

	// 小时到秒倍数
	public static final int DAY_SECONDS = 24 * HOUR_SECONDS;

	// 数字0
	// private static final int ZERO = 0;

	// 数字1
	// private static final int ONE = 1;

	// 兆对应的字节数
	public static final int M = 1024 * 1024;

	// 默认的兆数
	public static final int DEFAULT_M = 2;

	// http://www.websina.com/bugzero/kb/java-encoding-charset.html
	// UTF-8编码
	public static final String UTF8 = "UTF-8";

	// GB18030编码
	public static final String GB18030 = "GB18030";

	// ISO-8859-1编码
	public static final String ISO88591 = "ISO-8859-1";

	// 默认密码
	public static final String DEFAULT_PWD = "123456";

	/**
	 * 格式化字符串,避免出现null,将null转换为""
	 * 
	 * @param s
	 *            需要格式化的字符串
	 * @return 格式化后的字符串
	 */
	public static String normalizeString(String s) {
		return ((s == null) ? "" : s.trim());
	}

	/**
	 * 格式化字符串,避免出现null,将null转换为"",不trim()字符串
	 * 
	 * @param s
	 *            需要格式化的字符串
	 * @return 格式化后的字符串
	 */
	public static String normalizeStringNoTrim(String s) {
		return ((s == null) ? "" : s);
	}

	/**
	 * 格式化Document
	 * 
	 * @param doc
	 *            需要格式化的Document
	 * 
	 * @return 文本
	 */
	public static String normalizeDocument(Document doc) {
		return ((doc == null) ? "" : doc.asXML());
	}

	/**
	 * 格式化对象,避免出现null,将null转换为""
	 * 
	 * @param o
	 *            需要格式化的对象
	 * @return 格式化后的对象
	 */
	public static Object normalizeObject(Object o) {
		return ((o == null) ? "" : o);
	}

	/**
	 * 返回Map对象是否为null或者isEmpty
	 * 
	 * @param map
	 *            MAP
	 * 
	 * @return Map是否为空
	 */
	@SuppressWarnings("unchecked")
	public static boolean isMapEmpty(Map map) {
		return (map != null) && (!map.isEmpty());
	}

	/**
	 * String转换为Int，如果转换失败则返回默认值,如果isFix为真,并且转换结果<=0,则返回值为defaultValue
	 * 
	 * @param value
	 *            需要转换的字符串
	 * @param defaultValue
	 *            转换失败时的默认值
	 * @return 数值
	 */
	public static int string2Int(String value, int defaultValue, boolean isFix) {
		int result;
		try {
			result = Integer.parseInt(value);
			if ((isFix) && (result <= 0)) {
				result = defaultValue;
			}
		} catch (NumberFormatException nfe) {
			result = defaultValue;
		}
		return result;
	}

	/**
	 * String转换为Int，如果转换失败则返回默认值,如果isFix为真,并且转换结果<=0,则返回值为defaultValue
	 * 
	 * @param value
	 *            需要转换的字符串
	 * @param defaultValue
	 *            转换失败时的默认值
	 * @return 数值
	 */
	public static int string2Int(String value, int defaultValue) {
		return string2Int(value, defaultValue, false);
	}

	/**
	 * String转换为Long，如果转换失败则返回默认值,如果isFix为真,并且转换结果<=0,则返回值为defaultValue
	 * 
	 * @param value
	 *            需要转换的字符串
	 * @param defaultValue
	 *            转换失败时的默认值
	 * @return 数值
	 */
	public static long string2Long(String value, long defaultValue,
			boolean isFix) {
		long result;
		try {
			result = Long.parseLong(value);
			if ((isFix) && (result <= 0)) {
				result = defaultValue;
			}
		} catch (NumberFormatException nfe) {
			result = defaultValue;
		}
		return result;
	}

	/**
	 * String转换为Long，如果转换失败则返回默认值,如果isFix为真,并且转换结果<=0,则返回值为defaultValue
	 * 
	 * @param value
	 *            需要转换的字符串
	 * @param defaultValue
	 *            转换失败时的默认值
	 * @return 数值
	 */
	public static long string2Long(String value, long defaultValue) {
		return string2Long(value, defaultValue, false);
	}

	/**
	 * String转换为Float，如果转换失败则返回默认值
	 * 
	 * @param value
	 *            需要转换的字符串
	 * @param defaultValue
	 *            转换失败时的默认值
	 * @return 数值
	 */
	public static float string2Float(String value, float defaultValue) {
		float result;
		try {
			result = Float.parseFloat(value);
			if (result <= 0) {
				result = defaultValue;
			}
		} catch (Exception e) {
			result = defaultValue;
		}
		return result;
	}

	/**
	 * Document转换为文本
	 * 
	 * @param doc
	 *            Document
	 * 
	 * @return 文本
	 */
	public static String document2String(Document doc) {
		return document2String(doc, DEFAULT_M);
	}

	/**
	 * Document转换为文本
	 * 
	 * @param doc
	 *            Document
	 * @param mNumber
	 *            最大输出mNumber兆
	 * 
	 * @return 文本
	 */
	public static String document2String(Document doc, int mNumber) {
		if (doc != null) {
			String docStr = doc.asXML();
			return object2String(docStr, mNumber);
		} else {
			return "";
		}
	}

	/**
	 * Object转换为文本
	 * 
	 * @param obj
	 *            对象
	 * 
	 * @return 文本
	 */
	public static String object2String(Object obj) {
		return object2String(obj, DEFAULT_M);
	}

	/**
	 * Object转换为文本
	 * 
	 * @param obj
	 *            对象
	 * @param mNumber
	 *            最大输出mNumber兆
	 * 
	 * @return 文本
	 */
	public static String object2String(Object obj, int mNumber) {
		if (obj != null) {
			String objStr = obj.toString();
			if (objStr.length() > mNumber * M) {
				return objStr.substring(0, mNumber * M) + "...后续被忽略...";
			} else
				return objStr;
		} else {
			return "";
		}
	}

	/**
	 * 返回中文持续时间,如2小时3分15秒,1天5小时37秒等
	 * 
	 * @param time
	 *            时间(毫秒)
	 * @return 中文时间
	 */
//	public static String getDurationTime(long time) {
//		synchronized (DEFAULTDURATION_PATTERN) {
//			return DurationFormatUtils.formatDuration(time,
//					DEFAULTDURATION_PATTERN);
//		}
//	}

	/**
	 * 返回当前时间
	 * 
	 * @return 当前时间
	 */
	public static Date getCurrentDate() {
		return new Date();
	}

	/**
	 * 以默认日期样式返回当前日期
	 * 
	 * @return 默认样式的当前日期
	 */
	public static String getCurrentDate2() {
		synchronized (DEFAULTDATESDF) {
			return DEFAULTDATESDF.format(System.currentTimeMillis());
		}
	}

	/**
	 * 以默认样式返回当前时间
	 * 
	 * @return 默认样式的当前时间
	 */
	public static String getCurrentDateTime() {
		synchronized (DEFAULTDATETIMESDF) {
			return DEFAULTDATETIMESDF.format(System.currentTimeMillis());
		}
	}

	/**
	 * 以指定样式返回当前时间
	 * 
	 * @param pattern
	 *            样式
	 * @return 指定样式的当前时间
	 */
	public static String getCurrentDateTime(String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		synchronized (sdf) {
			return sdf.format(System.currentTimeMillis());
		}
	}

	/**
	 * 以默认样式返回指定时间
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 默认样式的指定时间
	 */
	public static String getDateTime(long dateTime) {
		synchronized (DEFAULTDATETIMESDF) {
			return DEFAULTDATETIMESDF.format(dateTime);
		}
	}

	/**
	 * 以默认样式返回指定时间
	 * 
	 * @param date
	 *            指定时间
	 * @return 默认样式的指定时间
	 */
	public static String getDateTime(Date date) {
		return getDateTime(date.getTime());
	}

	/**
	 * 以指定样式返回指定时间
	 * 
	 * @param dateTime
	 *            指定时间
	 * @param pattern
	 *            指定样式
	 * @return 指定样式的指定时间
	 */
	protected static String getDateTime(long dateTime, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		synchronized (sdf) {
			return sdf.format(dateTime);
		}
	}

	/**
	 * 以数据库样式(yyyy-MM-dd HH:mm:ss)返回当前时间
	 * 
	 * @return 数据库样式(yyyy-MM-dd HH:mm:ss)的当前时间
	 */
	public static String getCurrentDBDateTime() {
		return getDBDateTime(System.currentTimeMillis());
	}

	/**
	 * 以数据库样式(yyyy-MM-dd HH:mm:ss)返回指定时间
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 数据库样式(yyyy-MM-dd HH:mm:ss)的指定时间
	 */
	public static String getDBDateTime(long dateTime) {
		synchronized (DBDATETIMESDF) {
			return DBDATETIMESDF.format(dateTime);
		}
	}

	/**
	 * 以数据库样式(yyyy-MM-dd HH:mm:ss)返回指定时间 如果数值为小于等于0则输出空串
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 数据库样式(yyyy-MM-dd HH:mm:ss)的指定时间
	 */
	public static String getDBDataTimeMaybeEmpty(long dateTime) {
		return (dateTime <= 0) ? "" : getDBDateTime(dateTime);
	}

	/**
	 * 以数据库样式(MM-dd HH:mm:ss)返回指定时间
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 数据库样式(MM-dd HH:mm:ss)的指定时间
	 */
	public static String getDBDateTimeNoYear(long dateTime) {
		synchronized (DBDATETIMENOYEARSDF) {
			return DBDATETIMENOYEARSDF.format(dateTime);
		}
	}

	/**
	 * 以数据库样式(MM-dd)返回指定时间
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 数据库样式(MM-dd)的指定时间
	 */
	public static String getDateNoYear(long dateTime) {
		synchronized (DEFAULTDATENOYEARSDF) {
			return DEFAULTDATENOYEARSDF.format(dateTime);
		}
	}

	/**
	 * 以数据库样式(HH:mm:ss)返回指定时间
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 数据库样式(HH:mm:ss)的指定时间
	 */
	public static String getTimeMaybeEmpty(long dateTime) {
		synchronized (DEFAULTTIMESDF) {
			return (dateTime <= 0) ? "" : DEFAULTTIMESDF.format(dateTime);
		}
	}

	/**
	 * 以数据库样式(MM-dd HH:mm:ss)返回指定时间 如果数值为小于等于0则输出空串
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 数据库样式(MM-dd HH:mm:ss)的指定时间
	 */
	public static String getDBDataTimeNoYearMaybeEmpty(long dateTime) {
		return (dateTime <= 0) ? "" : getDBDateTimeNoYear(dateTime);
	}

	/**
	 * 以数据库样式(yyyy-MM-dd HH:mm:ss)返回指定时间
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 数据库样式(yyyy-MM-dd HH:mm:ss)的指定时间
	 */
	public static long getDBDateTime(String dbDateTime) {
		long result = 0;
		try {
			if (!dbDateTime.equals("")) {
				synchronized (DBDATETIMESDF) {
					result = DBDATETIMESDF.parse(dbDateTime).getTime();
				}
			}
		} catch (Exception e) {
			LOGGER.error("解析数据库样式时间='" + dbDateTime + "'时出现错误", e);
		}
		return result;
	}

	/**
	 * 以文件名样式(yyyymmddHHMMSS)返回当前时间
	 * 
	 * @return 文件名样式(yyyymmddHHMMSS)的指定时间
	 */
	public static String getCurrentFileDateTime() {
		return getFileDateTime(System.currentTimeMillis());
	}

	/**
	 * 以文件名样式(yyyymmddHHMMSS)返回指定时间
	 * 
	 * @param dateTime
	 *            指定时间
	 * @return 文件名样式(yyyymmddHHMMSS)的指定时间
	 */
	public static String getFileDateTime(long dateTime) {
		synchronized (FILEDATETIMESDF) {
			return FILEDATETIMESDF.format(dateTime);
		}
	}

	/**
	 * 返回离当前时间的最近的整interval时间 例如:时间间隔为10秒,当前时间为5分1秒,则返回5分0秒; 当前时间为5分8秒,则返回5分10秒
	 * 
	 * @param interval
	 *            时间间隔,单位为秒
	 * @return
	 */
	public static Date getIntervalDate(long interval) {
		Date date = null;
		long current = System.currentTimeMillis();
		long previous, next;

		previous = getPreviousInterval(current, interval);
		next = getNextInterval(current, interval);

		if ((current - previous) > (next - current)) {
			// 离后面的时间更近
			date = new Date(next);
		} else {
			date = new Date(previous);
		}
		// if (LOGGER.isDebugEnabled()) {
		// LOGGER.debug("当前时间=" + getDBDateTime(current) + ",current="
		// + current);
		// LOGGER.debug("前次间隔时间=" + getDBDateTime(previous) + ",previous="
		// + previous);
		// LOGGER.debug("后次间隔时间=" + getDBDateTime(next) + ",next=" + next);
		// LOGGER.debug("返回时间=" + getDBDateTime(date.getTime()));
		// }

		return date;
	}

	/**
	 * 返回小时级的初始化延时时间,<br>
	 * 任务传进来的interval都是3600秒(一小时的整倍数),<br>
	 * 从当天0点开始计算,得出第一个interal的整倍数并大于当前值的小时数就是第一次执行的时间,<br>
	 * 例如当前时间是3:23,interval是4小时,那么4:00就是执行时间,<br>
	 * 当前时间是15:30,interval是5小时,那么20:00就是执行时间
	 * 
	 * @param current
	 *            当前时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 初始化延时时间
	 */
	// public static long getInitialDelayHours(long current, long interval) {
	// // 时间间隔小时数
	// long intervalHour = (interval / HOUR_SECONDS);
	// // 当前时间
	// Calendar currCalendar = Calendar.getInstance();
	// currCalendar.setTime(new Date(current));
	// // 运行时间
	// Calendar runCalendar = (Calendar) currCalendar.clone();
	// // 设置分秒为0
	// runCalendar.set(Calendar.MINUTE, ZERO);
	// runCalendar.set(Calendar.SECOND, ZERO);
	// runCalendar.set(Calendar.MILLISECOND, ZERO);
	//
	// for (;;) {
	// // 小时数加1
	// runCalendar.add(Calendar.HOUR, ONE);
	// // 当小时数是时间间隔小时数的整倍数,或者时间已经第二天
	// if ((runCalendar.get(Calendar.HOUR_OF_DAY) % intervalHour == ZERO)
	// || (runCalendar.get(Calendar.DAY_OF_YEAR) != currCalendar
	// .get(Calendar.DAY_OF_YEAR))) {
	// break;
	// }
	// }
	//
	// return (runCalendar.getTimeInMillis() - currCalendar.getTimeInMillis())
	// / SECONDS_MILLISECONDS;
	// }
	/**
	 * 返回秒级的初始化延时时间
	 * 
	 * @param current
	 *            当前时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 秒级的初始化延时时间
	 */
	// public static long getInitialDelaySeconds(long current, long interval) {
	// return getInitialDelayMilliseconds(current, interval)
	// / SECONDS_MILLISECONDS;
	// }
	/**
	 * 返回毫秒级的初始化延时时间
	 * 
	 * @param current
	 *            当前时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 毫秒级的初始化延时时间
	 */
	public static long getInitialDelayMilliseconds(long current, long interval) {
		return (long) (getNextInterval(current, interval) - current);
	}

	/**
	 * 返回上一次执行的时间
	 * 
	 * @param current
	 *            当前时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 上一次执行的时间
	 */
	public static long getPreviousInterval(long current, long interval) {
		// 系统出现1970-01-01 08:00:00是因为时区导致,可以通过启动参数来解决
		// 时间间隔大约1小时,需要特殊计算,因为系统开始时间为1970-01-01
		// 08:00:00,按照小时级别计算间隔会有误差,目前发现5小时间隔的计算不准

		if (interval > HOUR_SECONDS) {
			return getPreviousInterval0(current + HOUR_8_MILLISECONDS, interval)
					- HOUR_8_MILLISECONDS;
		} else {
			return getPreviousInterval0(current, interval);
		}
	}

	/**
	 * 返回上一次执行的时间
	 * 
	 * @param current
	 *            当前时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 上一次执行的时间
	 */
	private static long getPreviousInterval0(long current, long interval) {
		return ((getCurrentIntervalQuotient(current, interval)) * interval * SECONDS_MILLISECONDS);
	}

	/**
	 * 返回下一次执行的时间
	 * 
	 * @param current
	 *            当前时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 下一次执行的时间
	 */
	public static long getNextInterval(long current, long interval) {
		// 时间间隔大约1小时,需要特殊计算,因为系统开始时间为1970-01-01
		// 08:00:00,按照小时级别计算间隔会有误差,目前发现5小时间隔的计算不准
		if (interval > HOUR_SECONDS) {
			return getNextInterval0(current + HOUR_8_MILLISECONDS, interval)
					- HOUR_8_MILLISECONDS;
		} else {
			return getNextInterval0(current, interval);
		}
	}

	/**
	 * 返回下一次执行的时间
	 * 
	 * @param current
	 *            当前时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 下一次执行的时间
	 */
	private static long getNextInterval0(long current, long interval) {
		return ((getCurrentIntervalQuotient(current, interval) + 1L) * interval * SECONDS_MILLISECONDS);
	}

	/**
	 * 返回当前时间与interval的商
	 * 
	 * @param current
	 *            当前时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 当前时间与interval的商
	 */
	private static long getCurrentIntervalQuotient(long current, long interval) {
		return (current / interval / SECONDS_MILLISECONDS);
	}

	/**
	 * 转换数据库时间为文件时间
	 * 
	 * @param dbDateTime
	 * 
	 * @return 文件格式的时间
	 */
	public static String convertDBDateTime2FileDateTime(String dbDateTime) {
		String result = dbDateTime;
		try {
			synchronized (FILEDATETIMESDF) {
				result = FILEDATETIMESDF
						.format(DBDATETIMESDF.parse(dbDateTime));
			}
		} catch (Exception e) {
			// 获取当前时间的文件时间
			result = getFileDateTime(System.currentTimeMillis());
			LOGGER.error("转换数据库样式时间='" + dbDateTime + "'为文件样式时间时出现错误", e);
		}
		return result;
	}

	/**
	 * 返回当前时间是否已经超过比较时间+时间间隔
	 * 
	 * @param compareTime
	 *            比较时间
	 * @param interval
	 *            时间间隔
	 * 
	 * @return 是否超时
	 */
	public static boolean isTimeout(long compareTime, long interval) {
		long current = System.currentTimeMillis();
		return current > (compareTime + interval);
	}

	/**
	 * 返回文件全路径
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名称
	 * @return 文件名全路径
	 */
	public static String getFileAllName(String filePath, String fileName) {
		return filePath + File.separator + convertFileName(fileName);
	}

	/**
	 * 将String保存为指定文件路径下的文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名称
	 * @param str
	 *            保存的文本
	 * @return 文件名
	 */
	public static String stringSaveFile(String filePath, String fileName,
			String str) {
		// 文件名
		String result = "";

		// 文件输出流
		FileOutputStream fos = null;
		// 文件全名
		String name = getFileAllName(filePath, fileName);
		try {
			File path = new File(filePath);
			if (!path.exists()) {
				if (!path.mkdirs()) {
					// 创建目录失败
				}
			}
			fos = new FileOutputStream(name);
			fos.write(str.getBytes(UTF8));
			fos.flush();
			result = new File(name).getCanonicalPath();
		} catch (FileNotFoundException fnfe) {
			LOGGER.error("文本保存为文件='" + name + "'时出现错误", fnfe);
		} catch (IOException ie) {
			LOGGER.error("文本保存为文件='" + name + "'时出现错误", ie);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ie) {
				LOGGER.error("文本保存为文件='" + name + "'关闭文件输出流时出现错误", ie);
			}
		}
		return result;
	}

	/**
	 * 将String保存为指定文件路径下的文件
	 * 
	 * @param fileAllName
	 *            文件全名
	 * @param str
	 *            保存的文本
	 * 
	 * @return 文件名
	 */
	public static boolean stringSaveFile(String fileAllName, String str) {
		return stringSaveFile(new File(fileAllName), str);
	}

	/**
	 * 将String保存为指定文件路径下的文件
	 * 
	 * @param file
	 *            文件
	 * @param str
	 *            保存的文本
	 * 
	 * @deprecated 此方法废弃,如果文件目录不存在不能自动创建
	 * 
	 * @return 文件名
	 */
	public static boolean stringSaveFile0(File file, String str) {
		// 处理标识
		boolean result = false;

		// 文件输出流
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(str.getBytes(UTF8));
			fos.flush();
			result = true;
		} catch (FileNotFoundException fnfe) {
			LOGGER.error("文本保存为文件='" + normalizeObject(file) + "'时出现错误", fnfe);
		} catch (IOException ie) {
			LOGGER.error("文本保存为文件='" + normalizeObject(file) + "'时出现错误", ie);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ie) {
				LOGGER.error("文本保存为文件='" + normalizeObject(file)
						+ "'关闭文件输出流时出现错误", ie);
			}
		}
		return result;
	}

	/**
	 * 将String保存为指定文件路径下的文件
	 * 
	 * @param file
	 *            文件
	 * @param str
	 *            保存的文本
	 * 
	 * @return 文件名
	 */
	public static boolean stringSaveFile(File file, String str) {
		// 处理标识
		boolean result = false;
		try {
			FileUtils.writeStringToFile(file, str, UTF8);
			result = true;
		} catch (IOException ie) {
			LOGGER.error("文本保存为文件='" + normalizeObject(file) + "'时出现错误", ie);
		}
		return result;
	}

	/**
	 * 将Object格式化后保存为指定文件路径下的文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名称
	 * @param obj
	 *            对象
	 * 
	 * @return 文件名
	 * 
	 * @throws Exception
	 */
	public static String xmlSaveFile(String filePath, String fileName,
			Object obj) {
		// 文件名
		String result = "";

		FileWriter fileWriter = null;
		XMLWriter xmlWriter = null;
		String name = getFileAllName(filePath, fileName);
		try {
			fileWriter = new FileWriter(name);
			// 缩进式
			OutputFormat outputFormat = OutputFormat.createPrettyPrint();
			outputFormat.setEncoding(UTF8);
			xmlWriter = new XMLWriter(fileWriter, outputFormat);
			if (obj instanceof String) {
				Document doc = DocumentHelper.parseText((String) obj);
				xmlWriter.write(doc);
			} else {
				xmlWriter.write(obj);
			}
			result = new File(name).getCanonicalPath();
		} catch (DocumentException de) {
			LOGGER.error("XML保存为文件='" + name + "'时出现错误", de);
		} catch (IOException ie) {
			LOGGER.error("XML保存为文件='" + name + "'时出现错误", ie);
		} finally {
			try {
				if (xmlWriter != null) {
					xmlWriter.close();
				}
			} catch (IOException ie) {
				LOGGER.error("XML保存为文件='" + name + "'关闭XMLWriter时出现错误", ie);
			}
			try {
				if (fileWriter != null) {
					fileWriter.close();
				}
			} catch (IOException ie) {
				LOGGER.error("XML保存为文件='" + name + "'关闭FileWriter时出现错误", ie);
			}
		}
		return result;
	}

	/**
	 * 转换文件名,文件名中包含非法字符,<br>
	 * 例如MOID中包含:
	 * 
	 * @param fileName
	 *            文件名
	 * @return 转换后的文件名
	 */
	public static String convertFileName(String fileName) {
		// Windows下的文件名不能包括\ / : * ? " < > |
		return fileName.replaceAll(":", "：");
	}

	/**
	 * 获取InetSocketAddress的信息
	 * 
	 * @param inetSocketAddress
	 *            地址
	 * 
	 * @return 信息
	 */
	public static String monitorInetSocketAddress(
			InetSocketAddress inetSocketAddress) {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("HostName=" + inetSocketAddress.getHostName() + ",");
		strBuff.append("Port=" + inetSocketAddress.getPort() + ",");
		strBuff.append("Address='"
				+ inetSocketAddress.getAddress().getCanonicalHostName() + ","
				+ inetSocketAddress.getAddress().getHostAddress() + ","
				+ inetSocketAddress.getAddress().getHostName() + "'");
		return strBuff.toString();
	}

	/**
	 * 返回本机IP地址
	 * 
	 * @return 本机IP地址
	 */
	public static String getLocalHost() {
		if (localHost.equals("")) {
			StringBuffer hostBuffer = new StringBuffer();
			try {
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
						.getNetworkInterfaces();

				while (networkInterfaces.hasMoreElements()) {
					NetworkInterface networkInterface = networkInterfaces
							.nextElement();
					Enumeration<InetAddress> inetAddresses = networkInterface
							.getInetAddresses();
					while (inetAddresses.hasMoreElements()) {
						InetAddress inetAddress = inetAddresses.nextElement();
						if (!inetAddress.getHostAddress().startsWith(
								LOCALHOST_PREFIX)) {
							hostBuffer.append(inetAddress.getHostAddress()
									+ SEPARATOR);
						}
					}
				}
			} catch (java.net.SocketException se) {
				LOGGER.error("获取本地地址时出现错误", se);
			}
			if (hostBuffer.length() > 0) {
				hostBuffer.deleteCharAt(hostBuffer.length() - 1);
			}
			localHost = hostBuffer.toString();
		}
		return localHost;
	}

	/**
	 * hostName是否是IP地址
	 * 
	 * @param hostName
	 *            主机名
	 * 
	 * @return 是否IP地址
	 */
	public static boolean isIPAddress(String hostName) {
		return ipPattern.matcher(hostName).matches();
	}

	/**
	 * 返回IP地址
	 * 
	 * @param hostName
	 *            主机名
	 * @return IP地址
	 */
	public static String getIPAddress(String hostName) {
		if (!normalizeString(hostName).equals("")) {
			if (isIPAddress(hostName)) {
				return hostName;
			} else {
				String ipAddress = "";
				try {
					InetAddress inetAddress = InetAddress.getByName(hostName);
					ipAddress = inetAddress.getHostAddress();
				} catch (UnknownHostException uhe) {
					LOGGER.error("获取主机名='" + hostName + "'的IP地址时出现错误", uhe);
				}
				return ipAddress;
			}
		} else {
			return "";
		}
	}

	/**
	 * 使用XPATH在对象中查询单节点
	 * 
	 * @param xPathStr
	 *            XPATH串
	 * @param obj
	 *            dom4j对象
	 * 
	 * @return dom4j节点
	 */
	public static Node selectSingleNode(String xPathStr, Object obj) {
		if ((!normalizeString(xPathStr).equals("")) && (obj != null)) {
			DefaultXPath defaultXPath = (DefaultXPath) xPathMap.get(xPathStr);
			if (defaultXPath == null) {
				defaultXPath = new DefaultXPath(xPathStr);
				xPathMap.put(xPathStr, defaultXPath);
			}
			Node node = defaultXPath.selectSingleNode(obj);
			if (LOGGER.isDebugEnabled()) {
				StringBuffer debugMsg = new StringBuffer();
				debugMsg.append("使用XPATH='" + xPathStr + "'在对象='" + obj
						+ "'中查询结果");
				if (node == null) {
					debugMsg.append("为空");
				} else {
					debugMsg.append("='" + node.asXML() + "'");
				}
				LOGGER.debug(debugMsg);
			}
			return node;
		} else {
			return null;
		}
	}

	/**
	 * 使用XPATH在对象中查询节点列表
	 * 
	 * @param xPathStr
	 *            XPATH串
	 * @param obj
	 *            dom4j对象
	 * 
	 * @return dom4j节点列表
	 */
	@SuppressWarnings("unchecked")
	public static List selectNodes(String xPathStr, Object obj) {
		if ((!normalizeString(xPathStr).equals("")) && (obj != null)) {
			DefaultXPath defaultXPath = (DefaultXPath) xPathMap.get(xPathStr);
			if (defaultXPath == null) {
				defaultXPath = new DefaultXPath(xPathStr);
				xPathMap.put(xPathStr, defaultXPath);
			}
			List nodeList = defaultXPath.selectNodes(obj);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("使用XPATH='" + xPathStr + "'在对象='" + obj
						+ "'中查询结果共'" + nodeList.size() + "'个");
			}
			return nodeList;
		} else {
			return new ArrayList();
		}
	}

	/**
	 * 返回百分比字符串
	 * 
	 * @param divisor
	 *            除数
	 * @param dividend
	 *            被除数
	 * @return 百分比字符串
	 */
	public static String getPercent(long divisor, long dividend) {
		if (dividend == 0) {
			return "";
		} else {
			return FOURDECIMAL_PERCENTDF
					.format((Double.valueOf(divisor) / dividend));
		}
	}

	/**
	 * 返回2位小数百分比字符串
	 * 
	 * @param divisor
	 *            除数
	 * @param dividend
	 *            被除数
	 * 
	 * @return 百分比字符串
	 */
	public static String getPercent2Bits(long divisor, long dividend) {
		if (dividend == 0) {
			return 0 + "";
		} else {
			return BITS2_NOPERCENT_PERCENTDF
					.format((Double.valueOf(divisor) / dividend));
		}
	}

	/**
	 * 转换2位小数百分比
	 * 
	 * @param percentStr
	 *            百分比字符串
	 * 
	 * @return 百分比
	 */
	public static double getPercent2Bits(String percentStr) {
		double percent = 0;
		try {
			Number percentNum = BITS2_NOPERCENT_PERCENTDF.parse(percentStr);
			percent = percentNum.doubleValue();
		} catch (Exception e) {
			LOGGER.error("getPercent2Bits error,percentStr='" + percentStr
					+ "'", e);
		}
		return percent;
	}

	/**
	 * 返回百分比字符串
	 * 
	 * @param percent
	 *            百分比
	 * 
	 * @return 百分比字符串
	 */
	public static String getPercent(double percent) {
		return DEFAULTPERCENTDF.format(percent);
	}

	/**
	 * 格式化输出浮点数,保留小数点后2位
	 * 
	 * @param number
	 *            浮点数
	 * 
	 * @return 保留小数点后2位的字符串
	 */
	public static String floatFormat2Bits(float number) {
		return BITS2_DECIMALTDF.format(number);
	}

	/**
	 * 返回配置文件流
	 * 
	 * @param resource
	 *            配置文件名
	 * 
	 * @return 配置文件流
	 */
	public static InputStream getResourceAsStream(String resource) {
		String stripped = resource.startsWith("/") ? resource.substring(1)
				: resource;

		InputStream is = null;
		URL url = null;

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader != null) {
			is = classLoader.getResourceAsStream(stripped);
			url = classLoader.getResource("");
		}

		if (is == null) {
			LOGGER
					.error("Thread.currentThread().getContextClassLoader().getResource(),路径='"
							+ getURLPath(url) + "'无法找到资源='" + stripped + "'");
			is = ClassLoader.getSystemResourceAsStream(stripped);
			url = ClassLoader.getSystemResource("");
		} else {
			LOGGER
					.info("Thread.currentThread().getContextClassLoader().getResource(),路径='"
							+ getURLPath(url) + "'找到资源='" + stripped + "'");
			return is;
		}

		if (is == null) {
			LOGGER.error("ClassLoader.getSystemResource(),路径='"
					+ getURLPath(url) + "'无法找到资源='" + stripped + "'");
			is = Util.class.getResourceAsStream(resource);
			url = Util.class.getResource("");
		} else {
			LOGGER.info("ClassLoader.getSystemResource(),路径='"
					+ getURLPath(url) + "'找到资源='" + stripped + "'");
			return is;
		}

		if (is == null) {
			LOGGER.error("Util.class.getResource(),路径='" + getURLPath(url)
					+ "'无法找到资源='" + resource + "'");
			is = Util.class.getClassLoader().getResourceAsStream(stripped);
			url = Util.class.getClassLoader().getResource("");
		} else {
			LOGGER.info("Util.class.getResource(),路径='" + getURLPath(url)
					+ "'找到资源='" + resource + "'");
			return is;
		}

		if (is == null) {
			LOGGER.error("Util.class.getClassLoader().getResource(),路径='"
					+ getURLPath(url) + "'无法找到资源='" + stripped + "'");
			LOGGER.error("所有资源路径都无法找到资源='" + stripped + "'");
		} else {
			LOGGER.info("Util.class.getClassLoader().getResource(),路径='"
					+ getURLPath(url) + "'找到资源='" + stripped + "'");
			return is;
		}
		return is;
	}

	/**
	 * 返回配置文件
	 * 
	 * @param resource
	 *            配置文件名
	 * 
	 * @return 配置文件
	 */
	public static URL getResource(String resource) {
		String stripped = resource.startsWith("/") ? resource.substring(1)
				: resource;

		URL url = null;
		URL pathUrl = null;
		String path = "";

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		if (classLoader != null) {
			url = classLoader.getResource(resource);
			pathUrl = classLoader.getResource("");
			if (pathUrl != null) {
				path = pathUrl.getPath();
			}
		}

		if (url == null) {
			LOGGER
					.error("Thread.currentThread().getContextClassLoader().getResource(),无法找到资源='"
							+ stripped + "',path='" + path + "'");
			url = ClassLoader.getSystemResource(resource);
			pathUrl = ClassLoader.getSystemResource("");
			if (pathUrl != null) {
				path = pathUrl.getPath();
			}
		} else {
			LOGGER
					.info("Thread.currentThread().getContextClassLoader().getResource(),找到资源='"
							+ getURLPath(url) + "'");
			return url;
		}

		if (url == null) {
			LOGGER.error("ClassLoader.getSystemResource(),无法找到资源='" + stripped
					+ "',path='" + path + "'");
			url = Util.class.getResource(resource);
			pathUrl = Util.class.getResource("");
			if (pathUrl != null) {
				path = pathUrl.getPath();
			}
		} else {
			LOGGER.info("ClassLoader.getSystemResource(),找到资源='"
					+ getURLPath(url) + "'");
			return url;
		}

		if (url == null) {
			LOGGER.error("Util.class.getResource(),无法找到资源='" + resource
					+ "',path='" + path + "'");
			url = Util.class.getClassLoader().getResource(resource);
			pathUrl = Util.class.getClassLoader().getResource("");
			if (pathUrl != null) {
				path = pathUrl.getPath();
			}
		} else {
			LOGGER.info("Util.class.getResource(),找到资源='" + getURLPath(url)
					+ "'");
			return url;
		}

		if (url == null) {
			LOGGER.error("Util.class.getClassLoader().getResource(),无法找到资源='"
					+ stripped + "',path='" + path + "'");
			LOGGER.error("所有资源路径都无法找到资源='" + stripped + "'");
		} else {
			LOGGER.info("Util.class.getClassLoader().getResource(),找到资源='"
					+ getURLPath(url) + "'");
			return url;
		}
		return url;
	}

	/**
	 * 返回配置文件,如果文件不存在,则创建一个文件对象
	 * 
	 * @param resource
	 *            配置文件名
	 * 
	 * @return 配置文件
	 */
	public static File getResourceFile(String resource) {
		File file = null;
		try {
			URL fileURL = getResource(resource);
			if (fileURL != null) {
				URI fileURI = fileURL.toURI();
				file = new File(fileURI);
			}
		} catch (Exception e) {
			LOGGER.error("无法将资源='" + resource + "'转换为File对象", e);
		} finally {
			if (file == null) {
				file = new File(resource);
			}
		}
		return file;
	}

	/*
	 * 返回URL路径
	 */
	private static String getURLPath(URL url) {
		if (url != null) {
			return url.getFile();
		} else {
			return "";
		}
	}

	/**
	 * 将ISO-8859-1转换为UTF-8
	 * 
	 * @param str
	 *            转换前的ISO编码字符串
	 * 
	 * @return UTF-8字符串
	 */
	public static String isoToUTF8(String str) {
		try {
			return new String(str.getBytes(ISO88591), UTF8);
		} catch (UnsupportedEncodingException uee) {
			return str;
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名称
	 * 
	 * @return 处理标识
	 */
	public static boolean deleteFile(String filePath, String fileName) {
		boolean result = false;
		File file = new File(getFileAllName(filePath, fileName));
		if ((file != null) && (file.exists()) && (file.isFile())) {
			result = file.delete();
		}
		return result;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 *            文件
	 * 
	 * @return 处理标识
	 */
	public static boolean deleteFile(File file) {
		if (file != null) {
			return file.delete();
		} else {
			return false;
		}
	}

	/**
	 * 清空目录中的文件
	 * 
	 * @param file
	 *            文件路径
	 * 
	 * @return 处理标识
	 */
	public static boolean cleanDirectory(File file) {
		// 处理标识
		boolean result = false;
		if (file != null) {
			try {
				FileUtils.cleanDirectory(file);
				result = true;
			} catch (IOException ie) {
				LOGGER.error("清空目录='" + normalizeObject(file) + "'中的文件时出现错误",
						ie);
			}
		}
		return result;
	}

	/**
	 * 字节数转换为显示字符串
	 * 
	 * @param size
	 *            字节数
	 * @return 显示字符串
	 */
	public static String byteCountToDisplaySize(long size) {
		return FileUtils.byteCountToDisplaySize(size);
	}

	/**
	 * 以UTF-8方式读取文件内容
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名称
	 * 
	 * @return 文件内容
	 */
	public static String readFileToString(String filePath, String fileName) {
		// 文件内容
		String text = "";
		// 文件全名
		String name = null;
		try {
			name = getFileAllName(filePath, fileName);
			File file = new File(name);
			text = FileUtils.readFileToString(file, UTF8);
		} catch (IOException ie) {
			LOGGER.error("读取文件='" + normalizeString(name) + "'时出现错误", ie);
		}
		return text;
	}

	/**
	 * 以UTF-8方式写入文件内容
	 * 
	 * @param filePath
	 *            文件路径
	 * @param fileName
	 *            文件名称
	 * @param data
	 *            文件内容
	 * 
	 * @return 处理标识
	 */
	public static boolean writeStringToFile(String filePath, String fileName,
			String data) {
		// 处理标识
		boolean result = false;
		// 文件全名
		String name = null;
		try {
			name = getFileAllName(filePath, fileName);
			File file = new File(name);
			FileUtils.writeStringToFile(file, data, UTF8);
			result = true;
		} catch (IOException ie) {
			LOGGER.error("写入文件='" + normalizeString(name) + "',内容='" + data
					+ "'时出现错误", ie);
		}
		return result;
	}

	/**
	 * 返回MD5串
	 * 
	 * @param str
	 *            源串
	 * @return MD5串
	 */
	public static String getMD5(String str) {
		String md5;
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes());
			byte digest[] = messageDigest.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				hexString.append(Integer.toHexString(0xFF & digest[i]));
			}
			md5 = hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			md5 = "MD5_" + str.hashCode();
		}
		return md5;
	}

	/**
	 * 返回是否是存在的文件
	 * 
	 * @param file
	 *            文件
	 * 
	 * @return 是否是存在的文件
	 */
	public static boolean isExistFile(File file) {
		try {
			return (file != null) && file.exists() && file.isFile();
		} catch (Exception e) {
			LOGGER.error("isExistFile error, file='" + normalizeObject(file)
					+ "', " + e);
			return false;
		}
	}

	/**
	 * 返回是否是存在并且有效(字节数大于0)的文件
	 * 
	 * @param file
	 *            文件
	 * 
	 * @return 是否是存在并且有效的文件
	 */
	public static boolean isExistValidFile(File file) {
		try {
			return (file != null) && file.exists() && file.isFile()
					&& file.length() > 0;
		} catch (Exception e) {
			LOGGER.error("isExistValidFile error, file='"
					+ normalizeObject(file) + "', " + e);
			return false;
		}
	}

	/**
	 * 根据splitCount来分割列表
	 * 
	 * @param allList
	 *            全列表
	 * @param splitCount
	 *            分割的个数
	 * @return 分割全列表的列表
	 */
	@SuppressWarnings("unchecked")
	public static List<List> splitList(List allList, int splitCount) {
		List<List> splitList = new ArrayList<List>();
		while (allList.size() > 0) {
			if (allList.size() > splitCount) {
				splitList.add(new ArrayList(allList.subList(0, splitCount)));
				for (int i = 0; i < splitCount; i++) {
					allList.remove(0);
				}
			} else {
				splitList.add(new ArrayList(allList));
				allList.clear();
			}
		}
		return splitList;
	}

	/**
	 * 原始串后增加空格,使字串长度达到预期长度
	 * 
	 * @param strBuffer
	 *            字符缓冲区
	 * @param src
	 *            原始串
	 * @param length
	 *            预期长度
	 * @param isLeftAdd
	 *            是否在左添加(右对齐)
	 * @param lastAttaString
	 *            后面附件字符串
	 */
	public static void stringAddSpace(StringBuffer strBuffer, String src,
			int length, boolean isLeftAdd, String lastAttaString) {
		if (isLeftAdd) {
			for (int i = 0; i < length - src.length(); i++) {
				strBuffer.append(SPACE);
			}
			strBuffer.append(src);
		} else {
			strBuffer.append(src);
			for (int i = 0; i < length - src.length(); i++) {
				strBuffer.append(SPACE);
			}
		}
		strBuffer.append(lastAttaString);
	}

	/**
	 * 循环增加length个原始串
	 * 
	 * @param strBuffer
	 *            字符缓冲区
	 * @param src
	 *            原始串
	 * @param length
	 *            长度
	 * @param lastAttaString
	 *            后面附件字符串
	 */
	public static void stringCircleAddString(StringBuffer strBuffer,
			String src, int length, String lastAttaString) {
		for (int i = 0; i < length; i++) {
			strBuffer.append(src);
		}
		strBuffer.append(lastAttaString);
	}

	/*
	 * 判断手机号码是否合法
	 */
	public static boolean isMobilePhone(String source) {
		String mask = "^13[0-9]{1}[0-9]{8}$";
		if (check(mask, source)) {
			return true;
		}

		mask = "^15[9]{1}[0-9]{8}$";
		if (check(mask, source)) {
			return true;
		}

		mask = "^18[9]{1}[0-9]{8}$";
		if (check(mask, source)) {
			return true;
		}

		mask = "^1[3,5]{1}[0-9]{1}[0-9]{8}$";
		return check(mask, source);

	}

	public static boolean check(String mask, String source) {
		if (source == null)
			return false;

		if ("".equals(source))
			return false;

		if (mask == null)
			return true;

		if ("".equals(mask))
			return true;

		Pattern p = Pattern.compile(mask);
		Matcher m = p.matcher(source);
		return m.find();

	}

	/**
	 * 是否为IPv4
	 * 
	 * @param source
	 * @return
	 */
	public static boolean isIPv4(String source) {
		String mask = "^(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])$";
		return check(mask, source);
	}

	/**
	 * 是否为IPv6
	 * 
	 * @param source
	 * @return
	 */
	public static boolean isIPv6(String source) {
		String mask = "^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^::([\\da-fA-F]{1,4}:){0,4}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){2}:([\\da-fA-F]{1,4}:){0,2}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){4}:((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$|^:((:[\\da-fA-F]{1,4}){1,6}|:)$|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)$|^([\\da-fA-F]{1,4}:){2}((:[\\da-fA-F]{1,4}){1,4}|:)$|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)$|^([\\da-fA-F]{1,4}:){4}((:[\\da-fA-F]{1,4}){1,2}|:)$|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?$|^([\\da-fA-F]{1,4}:){6}:$";
		return check(mask, source);
	}

	/**
	 * 利用UUID生成随机密码
	 * 
	 * @return
	 */
	public static String getRandomPwd() {
		String pwd = "";
		try {
			String uuid = UUID.randomUUID().toString();
			pwd = uuid.substring(0, uuid.indexOf("-"));
		} catch (Exception e) {
			LOGGER.error("getRandomPwd error, ", e);
			pwd = DEFAULT_PWD;
		} finally {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.info("getRandomPwd pwd='" + pwd + "'");
			}
		}
		return pwd;
	}

	/**
	 * 是否是一个存在的文件,并且文件大小相同
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isExistFile(File file, long size) {
		try {
			return file != null && file.exists() && file.isFile() && size > 0
					&& file.length() == size;
		} catch (Exception e) {
			LOGGER.error("isExistFile error, file='" + normalizeObject(file)
					+ "', size='" + size + "', " + e);
			return false;
		}
	}

	/**
	 * 关闭输入流
	 * 
	 * @param is
	 */
	public static void closeInputStream(InputStream is) {
		try {
			if (is != null) {
				is.close();
			}
		} catch (Exception e) {
			LOGGER.error("closeInputStream error, is='"
					+ Util.normalizeObject(is) + "'", e);
		}
	}

	/**
	 * 关闭输出流
	 * 
	 * @param is
	 */
	public static void closeOutputStream(OutputStream os) {
		try {
			if (os != null) {
				os.close();
			}
		} catch (Exception e) {
			LOGGER.error("closeOutputStream error, os='"
					+ Util.normalizeObject(os) + "'", e);
		}
	}
}
