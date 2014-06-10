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
import java.util.GregorianCalendar;

public class DateE extends DateInformation {

	private static void p(String x) {
		System.out.println(x);
	}

	public DateE() {
		super();
	}

	public DateE(Calendar c) {
		this.calendar = c;
	}

	public DateE(String date) {
		int year = 0, month = 0, day = 0;
		year = Integer.parseInt(date.substring(0, date.indexOf("-")));
		month = Integer.parseInt(date.substring(date.indexOf("-") + 1, date.lastIndexOf("-")));
		day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1, date.length()));
		calendar.set(year, month - 1, day);
	}

	public Calendar getClone(Calendar date) {// 返回一个对象
		Calendar temp = new GregorianCalendar();
		temp.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date
				.get(Calendar.SECOND));
		return temp;
	}

	public String getDate(Calendar date) {
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = theDate.format(date.getTime());
		return dateString;
	}

	public String getDateDetail(Calendar date) {
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = theDate.format(date.getTime());
		return dateString;
	}

	public String getDateDetail2(Calendar date) {
		SimpleDateFormat theDate = new SimpleDateFormat("MM-dd HH:mm");
		String dateString = theDate.format(date.getTime());
		return dateString;
	}

	public String getDateDetail3(Calendar date) {
		SimpleDateFormat theDate = new SimpleDateFormat("HH:mm");
		String dateString = theDate.format(date.getTime());
		return dateString;
	}

	public String getDateString() {// 将传入日期转化成字符串
		String string1 = "";
		String string2 = "";
		String string3 = "";
		string1 = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		string2 = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		string3 = String.valueOf(calendar.get(Calendar.YEAR));
		return string3 + "-" + string1 + "-" + string2;
	}

	public int getDaysCount() {
		return getIntervalInOneMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
	}

	public String[] getDaysOfMonth() {
		int count = getIntervalInOneMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
		String[] days = new String[count];
		p(new Integer(days.length).toString());
		Calendar first = getFirstDay();
		for (int i = 0; i < count; i++) {
			days[i] = getDate(first);
			first.add(Calendar.DATE, 1);
		}
		return days;
	}

	public String[] getDaysOfWeek() {
		String days[] = new String[7];
		Calendar temp = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		for (int i = 0; i < 7; i++) {
			temp.add(Calendar.DATE, (-temp.get(Calendar.DAY_OF_WEEK) + i + 1));
			days[i] = getDate(temp);
			// p(days[i]);
		}
		return days;
	}

	public Calendar getFirstDay() {
		Calendar first = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		first.set(Calendar.DAY_OF_MONTH, 1);
		return first;
	}

	public Calendar getFirstDayOfWeek() {
		Calendar first = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		first.add(Calendar.DATE, (-calendar.get(Calendar.DAY_OF_WEEK) + 1));
		return first;
	}

	public String getFirstOfWeek() {// 得到本周第一天字符串。
		Calendar first = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		first.add(Calendar.DATE, (-calendar.get(Calendar.DAY_OF_WEEK) + 1));
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = theDate.format(first.getTime());
		return dateString;
	}

	public String getLastOfMonth() {// 得到本月最后一天字符串。
		Calendar temp = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		temp.add(Calendar.MONTH, 1);
		temp.add(Calendar.DATE, -(getDayOfMonth()));
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = theDate.format(temp.getTime());
		return dateString;
	}

	public String getLastOfWeek() {// 得到本周最后一天字符串。
		Calendar first = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		first.add(Calendar.DATE, (-calendar.get(Calendar.DAY_OF_WEEK) + 7));
		SimpleDateFormat theDate = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = theDate.format(first.getTime());
		return dateString;
	}
}