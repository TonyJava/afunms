package com.afunms.schedule.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * 四个人一个地点 白班 夜班均衡 周末班次数均衡 不能连班：白+夜
 * 
 * @author Administrator
 * 
 */

@SuppressWarnings("unchecked")
public class Scheduling3 {

	private static String[] periods = null;
	private static String position = null;

	public Scheduling3(String periodids, String positionid) {
		periods = new String[periodids.split(",").length];
		for (int i = 0; i < periodids.split(",").length; i++) {
			periods[i] = periodids.split(",")[i];
		}

		position = positionid;
	}

	public static List<String> init(Calendar c) {
		List<String> list = new ArrayList<String>();
		String period = null;
		String workerStr = null;
		String str = "1234234124134243123124324123214314321324313421421341341243123124324123123423412413423421421341341221431432132431";
		for (int i = 0; i < str.length(); i++) {
			if (i % 2 == 0) {
				period = periods[0];
			} else {
				period = periods[1];
			}
			workerStr = str.charAt(i) + "_" + period + "_" + position;
			list.add(workerStr);
		}

		int day = c.get(Calendar.DAY_OF_WEEK);
		switch (day) {
		case 1:
			list.add(list.get(2));
			list.add(list.get(1));
			list.add(list.get(0));
			list.add(list.get(3));
			list.add(list.get(4));
			list.add(list.get(5));
			list.add(list.get(6));
			list.add(list.get(7));
			list.add(list.get(8));
			list.add(list.get(9));
			list.add(list.get(10));
			list.add(list.get(11));
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			break;
		case 2:
			break;
		case 3:
			list.add(list.get(1));// 第一个人白班变夜班
			list.add(list.get(0));// 第二个人夜班变白班
			list.remove(0);
			list.remove(0);
			break;
		case 4:
			list.add(list.get(2));
			list.add(list.get(1));
			list.add(list.get(0));
			list.add(list.get(3));
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			break;
		case 5:
			list.add(list.get(2));
			list.add(list.get(1));
			list.add(list.get(0));
			list.add(list.get(3));
			list.add(list.get(4));
			list.add(list.get(5));
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			break;
		case 6:
			list.add(list.get(2));
			list.add(list.get(1));
			list.add(list.get(0));
			list.add(list.get(3));
			list.add(list.get(4));
			list.add(list.get(5));
			list.add(list.get(6));
			list.add(list.get(7));
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			list.remove(0);
			break;
		case 7:
			Collections.reverse(list);
			list = convert(list, true, true);
			break;
		}

		return list;
	}

	private static List<String> convert(List<String> list, boolean periodFlag, boolean positionFlag) {
		List<String> rList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i);
			String[] keys = str.split("_");
			if (periodFlag) {
				if (keys[1].equals(periods[0])) {
					keys[1] = periods[1];
				} else {
					keys[1] = periods[0];
				}
			}
			str = String.format("%s_%s_%s", keys[0], keys[1], position);
			rList.add(str);
		}
		return rList;
	}

	public List<String> doSchedule(String userids, Calendar c) {
		List<String> list = new ArrayList<String>();
		String[] users = userids.split(",");
		List<String> userList = new ArrayList<String>();
		if (users.length == 4) {
			userList = Arrays.asList(users);
		}
		Collections.shuffle(userList);

		list = init(c);
		list = replace(list, userList, new String[] { "1", "2", "3", "4" });
		return list;
	}

	private List<String> replace(List<String> list, List<String> subList, Object[] strings) {
		List<String> rList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			String str = list.get(i);
			String keys[] = str.split("_");
			if (keys[0].equals(strings[0])) {
				keys[0] = subList.get(0);
			} else if (keys[0].equals(strings[1])) {
				keys[0] = subList.get(1);
			} else if (keys[0].equals(strings[2])) {
				keys[0] = subList.get(2);
			} else if (keys[0].equals(strings[3])) {
				keys[0] = subList.get(3);
			}
			str = String.format("%s_%s_%s", keys[0], keys[1], keys[2]);
			rList.add(str);
		}
		return rList;
	}

}
