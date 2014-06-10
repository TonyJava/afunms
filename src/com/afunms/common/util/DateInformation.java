/**
 * <p>Description:network utilities</p>
 * <p>Company: dhcc.com</p>
 * @author afunms
 * @project afunms
 * @date 2006-08-11
 */

package com.afunms.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * detail:������ �������ڣ�(2003-3-27 15:30:41)
 * 
 * @author:zhoulf
 */
@SuppressWarnings("deprecation")
public class DateInformation {

	public static void main(String args[]) {
		DateInformation db = new DateInformation();
		p("time: " + db.getTime());
		p(db.getLastDayOfMonth());
		p(db.getIntervalTime(-3));
		p("currenttime by intdddd " + db.getCurrentTime(1113408000));
		p("currenttime by int " + db.getCurrentTime(1073984400));
		p("currenttime by int " + db.getCurrentTime(1074049200));
		p("" + new Date(104, 1, 12, 23, 59, 59));
		p("" + new Date(104, 1, 12, 0, 0, 0).getTime() / 1000);
		p("" + new Date(104, 1, 12, 23, 59, 59).getTime() / 1000);
		p(Integer.toString(db.getIntervalInOneMonth(2003, 12)));
		p(db.getFirstDayOfWeek("2004-2-29"));
		p(db.getEndDayOfWeek("2004-01-01"));
		db.getAllDayOfWeek("2004-02-18");
	}

	private static void p(String x) {
		System.out.println(x);
	}

	Calendar calendar = null;

	public DateInformation() {
		calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		Date trialTime = new Date();
		calendar.setTime(trialTime);
	}

	/**
	 * ���ݴ�������ڼ�������ܵ������������ �������ڣ�(2004-2-10 10:10:34)
	 * 
	 * @return java.lang.String[] ���ܵ������������
	 * @param param
	 *            java.lang.String ���������
	 */
	public String[] getAllDayOfWeek(String date) {
		String weeks[] = new String[7];
		int year = 0, month = 0, day = 0;
		year = Integer.parseInt(date.substring(0, date.indexOf("-")));
		month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
		day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, date.length()));
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date(year - 1900, month - 1, day));
		for (int i = 0; i < 7; i++) {
			cal.add(Calendar.DATE, (-cal.get(Calendar.DAY_OF_WEEK) + i + 1));
			weeks[i] = (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + cal.getTime().getDate();
		}

		return weeks;
	}

	public int getAMPM() {
		return calendar.get(Calendar.AM_PM);
	}

	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * ȡ�õ�ǰʱ�䡣 �������ڣ�(2003-12-30 14:58:43)
	 * 
	 * @return java.lang.String
	 */
	public String getCurrentTime() {
		Calendar cal1 = new GregorianCalendar();
		System.out.println(cal1.getTime());
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = theDate.format(cal1.getTime());
		return dateString;
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2004-1-8 19:02:56)
	 * 
	 * @return java.lang.String
	 * @param timeaddbyseconds
	 *            int
	 */
	public String getCurrentTime(int timeaddbyseconds) {
		long timeLong = timeaddbyseconds;
		Date d = new Date(timeLong * 1000);
		String timeFormat = "yyyy-MM-dd HH:mm:ss";
		java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);
		return timeFormatter.format(d);
	}

	public String getDate() {
		StringBuffer temp = new StringBuffer("");
		temp.append(getYear());
		if (getMonthInt() > 9) {
			temp.append("-");
		} else {
			temp.append("-0");
		}
		temp.append(getMonthInt());

		if (getDayOfMonth() > 9) {
			temp.append("-");
		} else {
			temp.append("-0");
		}
		temp.append(getDayOfMonth());
		return temp.toString();

	}

	public String getDay() {
		int x = getDayOfWeek();
		String[] days = new String[] { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

		if (x > 7) {
			return "Unknown to Man";
		}

		return days[x - 1];

	}

	public int getDayOfMonth() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getDayOfWeek() {
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public int getDayOfYear() {
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public int getDSTOffset() {
		return calendar.get(Calendar.DST_OFFSET) / (60 * 60 * 1000);
	}

	/**
	 * ���ݴ�������ڼ�������ܵ����һ������� �������ڣ�(2003-12-2 13:47:15)
	 * 
	 * @return java.lang.String ���ܵ����һ�������
	 */
	public String getEndDayOfWeek(String date) {
		int year = 0, month = 0, day = 0;
		year = Integer.parseInt(date.substring(0, date.indexOf("-")));
		month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
		day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, date.length()));
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date(year - 1900, month - 1, day));
		cal.add(Calendar.DATE, (-cal.get(Calendar.DAY_OF_WEEK) + 7));
		return (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + cal.getTime().getDate();
	}

	public int getEra() {
		return calendar.get(Calendar.ERA);
	}

	/**
	 * �õ����µ�һ�졣�� �������ڣ�(2003-12-2 13:47:15)
	 * 
	 * @return java.lang.String
	 */
	public String getFirstDayOfMonth() {
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-01");
		String dateString = theDate.format(calendar.getTime());
		return dateString;
	}

	public String getFirstDayOfMonth(String year, String month) {
		String dateString = year + "-" + month + "-" + "01";
		return dateString;
	}

	/**
	 * ���ݴ�������ڼ�������ܵĵ�һ������� �������ڣ�(2003-12-2 13:47:15)
	 * 
	 * @return java.lang.String ���ܵĵ�һ�������
	 */
	public String getFirstDayOfWeek(String date) {
		int year = 0, month = 0, day = 0;
		year = Integer.parseInt(date.substring(0, date.indexOf("-")));
		month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
		day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, date.length()));
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date(year - 1900, month - 1, day));
		cal.add(Calendar.DATE, (-cal.get(Calendar.DAY_OF_WEEK) + 1));
		return (cal.getTime().getYear() + 1900) + "-" + (cal.getTime().getMonth() + 1) + "-" + cal.getTime().getDate();
	}

	public int getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * ���ݴ����ƫ��������뵱ǰ����ƫ������� �������ڣ�(2003-8-8 15:25:34)
	 * 
	 * @return java.lang.String
	 */
	public String getIntervalDate(int Interval) {
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, Interval);
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = theDate.format(cal.getTime());

		return dateString;
	}

	/**
	 * ���㵱ǰʱ���������ж����졣 �������ڣ�(2003-12-2 15:53:08)
	 * 
	 * @return int
	 * @param param
	 *            java.lang.String
	 * @param param1
	 *            java.lang.String
	 */
	public int getIntervalInOneMonth() {
		DateInformation dinf = new DateInformation();

		Calendar cal1 = new GregorianCalendar();
		cal1.add(Calendar.DATE, (-(dinf.getDayOfMonth()) + 1));

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -(dinf.getDayOfMonth()));
		return cal.getTime().getDate() - cal1.getTime().getDate() + 1;
	}

	/**
	 * ���ݴ�����������ĳ���ĳ�¹��ж����졣 �������ڣ�(2003-12-2 15:53:08)
	 * 
	 * @return int
	 * @param param
	 *            java.lang.String
	 * @param param1
	 *            java.lang.String
	 */
	public int getIntervalInOneMonth(int year, int month) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date(year - 1900, month - 1, 1));
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return cal.getTime().getDate();
	}

	/**
	 * �����뵱ǰʱ���ƫ��ʱ�䡣 �������ڣ�(2003-12-30 14:45:13)
	 * 
	 * @return java.lang.String
	 * @param starttime
	 *            int
	 */
	public String getIntervalTime(int starttime) {
		Calendar cal1 = new GregorianCalendar();
		cal1.add(Calendar.HOUR, starttime);
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateString = theDate.format(cal1.getTime());
		return dateString;
	}

	/**
	 * �õ��������һ�졣 �������ڣ�(2003-12-2 13:50:12)
	 * 
	 * @return java.lang.String
	 * @param Interval
	 *            int
	 */
	public String getLastDayOfMonth() {
		Calendar cal = new GregorianCalendar();
		DateInformation dinf = new DateInformation();
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -(dinf.getDayOfMonth()));
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = theDate.format(cal.getTime());
		return dateString;
	}

	public String getLastDayOfMonth(String year, String month) {
		String returnVal = "";
		int year_i = Integer.valueOf(year).intValue();
		int month_i = Integer.valueOf(month).intValue();
		int i = getIntervalInOneMonth(year_i, month_i);
		returnVal = year + "-" + month + "-" + i;
		return returnVal;
	}

	public int getMinute() {
		return calendar.get(Calendar.MINUTE);
	}

	public String getMonth() {
		int m = getMonthInt();
		String[] months = new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
		if (m > 12) {
			return "Unknown to Man";
		}

		return months[m - 1];

	}

	public int getMonthInt() {
		return 1 + calendar.get(Calendar.MONTH);
	}

	public int getSecond() {
		return calendar.get(Calendar.SECOND);
	}

	public String getTime() {
		StringBuffer temp = new StringBuffer("");
		temp.append(getHour());
		if (getMinute() > 9) {
			temp.append(":");
		} else {
			temp.append(":0");
		}
		temp.append(getMinute());

		if (getSecond() > 9) {
			temp.append(":");
		} else {
			temp.append(":0");
		}
		temp.append(getSecond());
		return temp.toString();
	}

	public int getWeekOfMonth() {
		return calendar.get(Calendar.WEEK_OF_MONTH);
	}

	public int getWeekOfYear() {
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}

	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * @param calendar
	 */
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}
}
