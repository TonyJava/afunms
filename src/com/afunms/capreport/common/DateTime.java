package com.afunms.capreport.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author sunqichang 公用的日期时间处理类
 * 
 */
public class DateTime {

	/**
	 * 时区的偏移数，默认为中国所在的时区：东八区。
	 */
	private String timeZoneOffSet = "GMT+08:00";

	/**
	 * 日历
	 */
	private GregorianCalendar calendar;

	/**
	 * 毫秒/天
	 */
	public final static long oneDayValue = 24 * 60 * 60 * 1000;

	/**
	 * 毫秒/时
	 */
	public final static long oneHourValue = 60 * 60 * 1000;

	/**
	 * 毫秒/分
	 */
	public final static long oneMinuteValue = 60 * 1000;

	/**
	 * 毫秒/秒
	 */
	public final static long oneSecondValue = 1000;

	/**
	 * 日期时间_格式_1：20100101130101
	 */
	public final static String Datetime_Format_1 = "yyyyMMddHHmmss";

	/**
	 * 日期时间_格式_2：2010-01-01 13：01：01
	 */
	public final static String Datetime_Format_2 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 日期时间_格式_3：2010年01月01日 13时01分01秒
	 */
	public final static String Datetime_Format_3 = "yyyy年MM月dd日 HH时mm分ss秒";

	/**
	 * 日期时间_格式_4：20100101
	 */
	public final static String Datetime_Format_4 = "yyyyMMdd";

	/**
	 * 日期时间_格式_14：2010010122
	 */
	public final static String Datetime_Format_14 = "yyyyMMddHH";

	/**
	 * 日期时间_格式_5：2010-01-01
	 */
	public final static String Datetime_Format_5 = "yyyy-MM-dd";

	/**
	 * 日期时间_格式_6：2010年01月01日
	 */
	public final static String Datetime_Format_6 = "yyyy年MM月dd日";

	/**
	 * 日期时间_格式_7：13时01分01秒
	 */
	public final static String Datetime_Format_7 = "HH时mm分ss秒";

	/**
	 * 日期时间_格式_8：2010年
	 */
	public final static String Datetime_Format_8 = "yyyy";

	/**
	 * 日期时间_格式_9：08月
	 */
	public final static String Datetime_Format_9 = "MM";

	/**
	 * 日期时间_格式_10：22日
	 */
	public final static String Datetime_Format_10 = "dd";

	/**
	 * 日期时间_格式_11：13时
	 */
	public final static String Datetime_Format_11 = "HH";

	/**
	 * 日期时间_格式_12：24分
	 */
	public final static String Datetime_Format_12 = "mm";

	/**
	 * 日期时间_格式_13：20秒
	 */
	public final static String Datetime_Format_13 = "ss";

	/**
	 * 构造函数 默认当前时间
	 */
	public DateTime() {
		this.calendar = new GregorianCalendar();
		this.calendar.setTime(new Date());
		this.buildTimeZone();
	}

	/**
	 * 构造函数。
	 * 
	 * @param date
	 */
	public DateTime(Date date) {
		this.calendar = new GregorianCalendar();
		this.calendar.setTime(date);
		this.buildTimeZone();
	}

	/**
	 * 设置时间区域。
	 */
	private void buildTimeZone() {
		this.calendar.setTimeZone(TimeZone.getTimeZone(timeZoneOffSet));
	}

	public String getTimeZoneOffSet() {
		return timeZoneOffSet;
	}

	public void setTimeZoneOffSet(String timeZoneOffSet) {
		this.timeZoneOffSet = timeZoneOffSet;
	}

	/**
	 * 把日期字符化。
	 * 
	 * @param pattern
	 *            输出的日期时间_格式_。
	 * @return 日期字符化
	 */
	public String toString(String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(this.calendar.getTime());
	}

	/**
	 * 离指定的时间天数。
	 * 
	 * @param dateValue
	 *            指定的时间的整数值。
	 * @return 天数。
	 */
	public int distanceDayFrom(long dateValue) {
		long dintanceValue = this.getValue() - dateValue;
		int distanceDay = (int) (dintanceValue / oneDayValue);
		return distanceDay;

	}

	/**
	 * 获取时间的整数值。
	 */
	public long getValue() {
		return this.calendar.getTimeInMillis();
	}

	/**
	 * 获取该时间在此年中的第几周。
	 * 
	 * @return 在此年中的第几周
	 */
	public int getWeekOfYear() {
		return this.calendar.get(Calendar.WEEK_OF_YEAR);
	}

	/**
	 * 获取年，范围1900-9999。
	 * 
	 * @return Returns the year.
	 */
	public int getYear() {
		return this.calendar.get(Calendar.YEAR);
	}

	/**
	 * 获取月份，范围1-12。
	 * 
	 * @return Returns the month.
	 */
	public int getMonth() {
		return this.calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取一月中的第几天取值范围1-31。
	 * 
	 * @return Returns the date.
	 */
	public int getDate() {
		return this.calendar.get(Calendar.DATE);
	}

	/**
	 * 取一个星期的第几天。 周末为一个星期的第一天；
	 * 
	 * @return 一个星期的第几天
	 */
	public int getDay() {
		return this.calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取小时，范围0-23。
	 * 
	 * @return Returns the hours.
	 */
	public int getHours() {
		return this.calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * 获取分钟，范围0-59。
	 * 
	 * @return Returns the minutes.
	 */
	public int getMinutes() {
		return this.calendar.get(Calendar.MINUTE);
	}

	/**
	 * 获取秒，范围0-59。
	 * 
	 * @return Returns the seconds.
	 */
	public int getSeconds() {
		return this.calendar.get(Calendar.SECOND);
	}

	/**
	 * 获取年月日时分秒中的一个值。
	 * 
	 * @return 年月日时分秒中的一个值
	 */
	public int get(String style) {
		return Integer.parseInt(this.toString(style));
	}

	/**
	 * 返回startDate前一天
	 * 
	 * @return
	 */
	public String getLastDay() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTimeInMillis(this.calendar.getTimeInMillis() - oneDayValue);
		end.setTimeInMillis(this.calendar.getTimeInMillis());
		return DateTime.getMyDateTime(start.getTime(), DateTime.Datetime_Format_5) + "~" + DateTime.getMyDateTime(end.getTime(), DateTime.Datetime_Format_5);
	}

	/**
	 * 返回startDate前一周
	 * 
	 * @param startweekday
	 *            0：从周日开始，1：从周一开始
	 * @return 例如：2009-03-01~2009-03-08（startDate:2009-03-11）
	 */
	public String getLastWeek(int startweekday) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		int dayofweek = this.calendar.get(Calendar.DAY_OF_WEEK);
		if (0 == startweekday) {
			start.setTimeInMillis(this.calendar.getTimeInMillis() - (dayofweek + 6) * oneDayValue);
			end.setTimeInMillis(this.calendar.getTimeInMillis() - (dayofweek - 1) * oneDayValue);
		} else if (1 == startweekday) {
			start.setTimeInMillis(this.calendar.getTimeInMillis() - (dayofweek + 5) * oneDayValue);
			end.setTimeInMillis(this.calendar.getTimeInMillis() - (dayofweek - 2) * oneDayValue);
		}
		return DateTime.getMyDateTime(start.getTime(), DateTime.Datetime_Format_5) + "~" + DateTime.getMyDateTime(end.getTime(), DateTime.Datetime_Format_5);
	}

	/**
	 * 返回startDate上个月的时间
	 * 
	 * @return 例如：2009-02-01~2009-03-01（startDate：2009-03-11）
	 */
	public String getLastMonth() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		int year = this.calendar.get(Calendar.YEAR);
		int month = this.calendar.get(Calendar.MONTH);
		end.set(year, month, 1);
		if (month < 1) {
			year = this.calendar.get(Calendar.YEAR) - 1;
			month = 12;
		}
		start.set(year, month - 1, 1);
		return DateTime.getMyDateTime(start.getTime(), DateTime.Datetime_Format_5) + "~" + DateTime.getMyDateTime(end.getTime(), DateTime.Datetime_Format_5);
	}

	/**
	 * 返回startDate的前一季度的时间
	 * 
	 * @return startDate的前一季度的时间 例如：2008-10-01~2009-01-01（endDate:2009-03-11）
	 */
	public String getLastSeason() {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		int month = this.calendar.get(Calendar.MONTH);
		int year = this.calendar.get(Calendar.YEAR);
		if (month == Calendar.JANUARY || month == Calendar.FEBRUARY || month == Calendar.MARCH) {
			// 上年10-01 ~~ 当年01-01
			start.set(year - 1, Calendar.OCTOBER, 1);
			end.set(year, Calendar.JANUARY, 1);
		} else if (month == Calendar.APRIL || month == Calendar.MAY || month == Calendar.JUNE) {
			// 当年01-01 ~~ 当年04-01
			start.set(year, Calendar.JANUARY, 1);
			end.set(year, Calendar.APRIL, 1);
		} else if (month == Calendar.JULY || month == Calendar.AUGUST || month == Calendar.SEPTEMBER) {
			// 当年04-01 ~~ 当年07-01
			start.set(year, Calendar.APRIL, 1);
			end.set(year, Calendar.JULY, 1);
		} else {
			// 当年07-01 ~~ 当年10-01
			start.set(year, Calendar.JULY, 1);
			end.set(year, Calendar.OCTOBER, 1);
		}
		return DateTime.getMyDateTime(start.getTime(), DateTime.Datetime_Format_5) + "~" + DateTime.getMyDateTime(end.getTime(), DateTime.Datetime_Format_5);
	}

	/**
	 * 返回startDate上年一月一号到当年一月一号
	 * 
	 * @return 例如：2008-01-01~2009-01-01（startDate:2009-03-11）
	 */
	public String getLastYear() {
		int year = this.calendar.get(Calendar.YEAR);
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.set(year - 1, 0, 1);
		end.set(year, 0, 1);
		return DateTime.getMyDateTime(start.getTime(), DateTime.Datetime_Format_5) + "~" + DateTime.getMyDateTime(end.getTime(), DateTime.Datetime_Format_5);
	}

	/**
	 * 将秒转换成自然的描述，例如3661秒的自然描述是1小时1分1秒。
	 * 
	 * @param ms
	 *            秒数
	 * @param isShowZero
	 *            是否显示为0. 真：0秒；假：少于1秒
	 * @return 负数时不验证返回负时间值
	 */
	public static String getTimeExpression(long ms, boolean isShowZero) {
		long day;
		long hour;
		long minutes;
		long second;
		long ONE_SECOND = 1000;

		long ONE_MINUTE = ONE_SECOND * 60;
		long ONE_HOUR = ONE_MINUTE * 60;
		long ONE_DAY = ONE_HOUR * 24;

		day = (long) (ms / ONE_DAY);
		long leftHour = ms - ONE_DAY * day;

		hour = (long) (leftHour / ONE_HOUR);
		long leftMinutes = leftHour - hour * ONE_HOUR;

		minutes = (long) (leftMinutes / ONE_MINUTE);
		long leftSecond = (long) (leftMinutes - minutes * ONE_MINUTE);

		second = (long) (leftSecond / ONE_SECOND);
		StringBuffer exp = new StringBuffer();
		if (day != 0) {
			exp.append(day + "天");
		}
		if (hour != 0) {
			exp.append(hour + "小时");
		}
		if (minutes != 0) {
			exp.append(minutes + "分");
		}
		if (second != 0) {
			exp.append(second + "秒");
		} else {
			if (exp.length() == 0) {
				if (isShowZero) {
					exp.append("0秒");
				} else {
					exp.append("少于1秒");
				}
			}
		}
		return exp.toString();
	}

	/**
	 * 返回自定义格式的时间
	 * 
	 * @param 时间格式
	 * @return 自定义格式的时间
	 */
	public String getMyDateTime(String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(this.calendar.getTime());
	}

	/**
	 * 返回自定义格式的时间
	 * 
	 * @param date
	 *            日期对象
	 * @param pattern
	 *            时间格式
	 * @return 自定义格式的时间
	 */
	public static String getMyDateTime(Date date, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public void setYear(int year, int month, int date) {
		this.calendar.set(year, month - 1, date);
	}

	public void setHour(int hour, int minute, int second) {
		this.calendar.set(getYear(), getMonth() - 1, getDate(), hour, minute, second);
	}

	/**
	 * 以指定的格式去格式化日期
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	public static String format(String value, String format) {
		if (value == null || format == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(value);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sdf.format(date);
	}

	/**
	 * 获取一个星期中的某天(文字型)。
	 * 
	 * @return
	 */
	public String getDayOfWeek() {
		int week = this.calendar.get(Calendar.DAY_OF_WEEK);
		String strweek = null;
		switch (week) {
		case 0:
			strweek = "一";
			break;
		case 1:
			strweek = "二";
			break;
		case 2:
			strweek = "三";
			break;
		case 3:
			strweek = "四";
			break;
		case 4:
			strweek = "五";
			break;
		case 5:
			strweek = "六";
			break;
		case 6:
			strweek = "日";
			break;
		default:
		}
		return strweek;
	}

	/**
	 * 把字符串转换为日期
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	public static Date stringToDate(String value, String format) {
		Date date = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			date = simpleDateFormat.parse(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 把日期转换为字符串
	 * 
	 * @param value
	 * @param format
	 * @return
	 */
	public static String dateToString(Date value, String format) {
		String date = "";
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			date = simpleDateFormat.format(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 是否是有效的时间
	 * 
	 * @param time
	 * @param format
	 * @return
	 */
	public static boolean isValidTime(String time, String format) {
		boolean valid;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			simpleDateFormat.parse(time);
			valid = true;
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}

	/**
	 * 比较realTime是否比baseTime晚,如果是则返回true,如果否则返回false
	 * 
	 * @param Calendar
	 *            baseTime 基准时间
	 * @param Calendar
	 *            realTime 比较时间
	 * @return
	 */
	public static boolean after(Calendar baseTime, Calendar realTime) {
		boolean after = false;
		try {
			if (realTime.after(baseTime)) {
				after = true;
			}
		} catch (Exception e) {
			after = false;
		}
		return after;
	}

	/**
	 * 比较realTime是否比baseTime晚,如果是则返回true,如果否则返回false
	 * 
	 * @param Date
	 *            baseTime 基准时间
	 * @param Date
	 *            realTime 比较时间
	 * @return
	 */
	public static boolean after(Date baseTime, Date realTime) {
		boolean after = false;
		try {
			if (realTime.after(baseTime)) {
				after = true;
			}
		} catch (Exception e) {
			after = false;
		}
		return after;
	}

	/**
	 * 用于获得当前时间的13位毫秒值
	 * 
	 * @return long 当前时间的13位毫秒值
	 * 
	 */
	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	/**
	 * 计算两个日期相差的时间（天、时、秒、……）
	 * 
	 * @param begin
	 *            开始时间
	 * @param end
	 *            结束时间
	 * @param format
	 *            时间格式
	 * @param type
	 *            相差时间的单位
	 * @return
	 */
	public static int getBetweenDays(String begin, String end, String format, long type) {
		int days = 0;
		if (begin == null || end == null) {
			return days;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			Date sdate = sdf.parse(begin);
			Date edate = sdf.parse(end);
			long times = edate.getTime() - sdate.getTime();
			days = (int) (times / type);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}
		return days;
	}

	/**
	 * @param date
	 *            时间
	 * @param type
	 *            格式
	 * @return 13位毫秒值
	 */
	public static long getMillisecond(String date, String type) {
		long millisecond = 0;
		String typedefult = "yyyy-MM-dd";
		try {
			if (type != null && "".equals(type)) {
				type = typedefult;
			} else if ("YYYYMMDD".equals(type)) {
				date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
				type = "yyyy-MM-dd";
			}
			SimpleDateFormat formatter = new SimpleDateFormat(type);
			millisecond = formatter.parse(date).getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return millisecond;
	}

	/**
	 * @param Millisecond
	 *            13位毫秒值
	 * @param type
	 *            要返回的时间格式
	 * @return 时间字符串
	 */
	public static String getDateFromMillisecond(String Millisecond, String type) {
		long millisecond = 0;
		String result = "";
		String typedefult = "yyyy-MM-dd";
		try {
			if (type != null && "".equals(type)) {
				type = typedefult;
			}
			SimpleDateFormat formatter = new SimpleDateFormat(type);
			millisecond = Long.parseLong(Millisecond);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(millisecond);
			result = formatter.format(calendar.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * *************************************************************************
	 * ************************************* 用于测试
	 * *******************************
	 * *******************************************
	 * ************************************
	 */
	// public static void main(String[] args) {
	// DateTime cdt = new DateTime(new Date());
	//
	// int str;
	// str = cdt.getWeekOfYear();
	// System.out.println(str);
	//
	// int date = cdt.getDate();
	// int day = cdt.getDay();
	// int horus = cdt.getHours();
	// int minutes = cdt.getMinutes();
	// int seconds = cdt.getSeconds();
	// System.out.println(date + "|" + day + "|" + horus + "|" + minutes + "|" +
	// seconds);
	// int x = cdt.get("MM");
	// System.out.println(x);
	//
	// System.out.println(DateTime.getTimeExpression(-111110, true));
	//
	// System.out.println(cdt.distanceDayFrom(cdt.getValue() - oneDayValue *
	// 2));
	//
	// System.out.println(cdt.get(DateTime.Datetime_Format_10));
	//
	// System.out.println(cdt.getMyDateTime("yyyy"));
	// System.out.println(DateTime.getBetweenDays("2010年01月01日", "2010年01月02日",
	// DateTime.Datetime_Format_6,
	// DateTime.oneHourValue));
	// }
}
