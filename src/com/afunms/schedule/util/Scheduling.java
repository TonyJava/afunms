package com.afunms.schedule.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.afunms.schedule.model.Worker;

@SuppressWarnings("unchecked")
public class Scheduling {
	private static List<String> workers = new ArrayList<String>();;
	private static String[] periods = null;
	private static String[] positions = null;
	private static Map<String, Integer> map = new HashMap<String, Integer>();
	private static Map<String, List<Worker>> workerMap = new HashMap<String, List<Worker>>();

	public Scheduling(String periodids, String positionids) {
		periods = new String[periodids.split(",").length];
		for (int i = 0; i < periodids.split(",").length; i++) {
			periods[i] = periodids.split(",")[i];
		}

		positions = new String[positionids.split(",").length];
		for (int i = 0; i < positionids.split(",").length; i++) {
			positions[i] = positionids.split(",")[i];
		}
	}

	public List<Worker> doSchedule(String userids, int days) {
		List<Worker> list = new ArrayList<Worker>();

		label1: while (true) {
			workers.clear();
			workerMap.clear();
			String[] users = userids.split(",");
			for (int i = 0; i < users.length; i++) {
				workers.add(users[i]);
				workerMap.put(users[i], new ArrayList<Worker>());
			}

			map.clear();
			list.clear();

			Calendar rightNow = Calendar.getInstance();
			firstDay(list, rightNow);
			otherDay(list, days, rightNow);
			rightNow.setTime(new Date());
			int count = 0;
			for (int i = 0; i < list.size(); i++) {
				Worker worker = list.get(i);
				if (i != 0 && i % 4 == 0) {
					rightNow.add(Calendar.DAY_OF_WEEK, 1);
				}
				if (worker.getName().equals("worker0")) {
					count++;
				}
			}

			boolean flag = false;
			if (count <= 2) {
				Set<Map.Entry<String, Integer>> set = map.entrySet();
				for (int i = 1; i <= list.size(); i++) {
					Worker worker = list.get(i - 1);
					if (worker.getName().equals("worker0")) {
						String place = worker.getPlace();
						String shift = worker.getWorkTime();
						for (Iterator<Map.Entry<String, Integer>> iterator = set.iterator(); iterator.hasNext();) {
							Map.Entry<String, Integer> entry = iterator.next();
							String key = entry.getKey();
							int value = entry.getValue();
							if (value == 7) {
								iterator.remove();
								continue;
							}
							String workername = key.split("_")[0];
							String workerplace = key.split("_")[1];
							String workershift = key.split("_")[2];
							if (place.equalsIgnoreCase(workerplace) && shift.equalsIgnoreCase(workershift)) {
								worker.setName(workername);
								map.put(key, value + 1);
								break;
							}
						}
					}
					if (i % 4 == 0) {
						List<String> tempList = new ArrayList<String>();
						for (int j = i - 4; j < i; j++) {
							if (!tempList.contains(list.get(j).getName())) {
								tempList.add(list.get(j).getName());
							} else {
								flag = true;
								continue label1;
							}
						}
					}
				}
				if (!flag) {
					break label1;
				}
			}
		}
		return list;
	}

	private static void firstDay(List<Worker> list, Calendar c) {
		List<Integer> indexs = new ArrayList<Integer>();
		Random random = new Random();
		int index0 = random.nextInt(workers.size());
		Worker worker0 = new Worker(workers.get(index0));
		worker0.setPlace(positions[0]);
		worker0.setWorkTime(periods[0]);
		worker0.setIndex(1);
		indexs.add(index0);
		list.add(worker0);
		map.put(worker0.getName() + "_" + worker0.getPlace() + "_" + worker0.getWorkTime(), 1);
		workerMap.get(worker0.getName()).add(worker0);

		while (true) {
			int index = random.nextInt(workers.size());
			if (!indexs.contains(index)) {
				Worker worker = new Worker(workers.get(index));
				worker.setIndex(1);
				worker.setPlace(positions[0]);
				worker.setWorkTime(periods[1]);
				indexs.add(index);
				list.add(worker);
				map.put(worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime(), 1);
				workerMap.get(worker.getName()).add(worker);
				break;
			}
		}

		while (true) {
			int index = random.nextInt(workers.size());
			if (!indexs.contains(index)) {
				Worker worker = new Worker(workers.get(index));
				worker.setIndex(1);
				worker.setPlace(positions[1]);
				worker.setWorkTime(periods[0]);
				indexs.add(index);
				list.add(worker);
				map.put(worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime(), 1);
				workerMap.get(worker.getName()).add(worker);
				break;
			}
		}

		while (true) {
			int index = random.nextInt(workers.size());
			if (!indexs.contains(index)) {
				Worker worker = new Worker(workers.get(index));
				worker.setIndex(1);
				worker.setPlace(positions[1]);
				worker.setWorkTime(periods[1]);
				indexs.add(index);
				list.add(worker);
				map.put(worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime(), 1);
				workerMap.get(worker.getName()).add(worker);
				break;
			}
		}

	}

	private static void otherDay(List<Worker> list, int loops, Calendar c) {
		Set<String> indexs = new HashSet<String>();
		Random random = new Random();
		Integer count = null;
		Worker temp = null;

		for (int i = 2; i <= loops; i++) {
			c.add(Calendar.DAY_OF_WEEK, 1);
			sortMap(positions[0], periods[0]);
			for (int index = 0; index < workers.size(); index++) {
				int ranindex = random.nextInt(workers.size() / 2);
				Worker worker = new Worker(workers.get(ranindex));
				if (!indexs.contains(worker.getName())) {
					boolean isContinuous = isContinuous(worker, i);
					boolean isNightShift = isNightShift(worker, i);
					worker.setPlace(positions[0]);
					worker.setWorkTime(periods[0]);
					count = getWorkCount(worker);
					if (!isContinuous && !isNightShift && count < 7) {
						worker.setIndex(i);
						temp = worker;
						list.add(worker);
						map.put(worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime(), ++count);
						workerMap.get(worker.getName()).add(worker);
						indexs.add(worker.getName());
						break;
					}
				}
			}
			if (null == temp) {
				list.add(new Worker(i, "worker0", positions[0], periods[0]));
			}

			temp = null;
			sortMap(positions[0], periods[1]);
			for (int index = 0; index < workers.size(); index++) {
				int ranindex = random.nextInt(workers.size() / 2);
				Worker worker = new Worker(workers.get(ranindex));
				if (!indexs.contains(worker.getName())) {
					boolean isNightShift = isNightShift(worker, i);
					worker.setPlace(positions[0]);
					worker.setWorkTime(periods[1]);

					count = getWorkCount(worker);
					if (!isNightShift && count < 7) {
						worker.setIndex(i);
						temp = worker;
						list.add(worker);
						map.put(worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime(), ++count);
						workerMap.get(worker.getName()).add(worker);
						indexs.add(worker.getName());
						break;
					}
				}
			}
			if (null == temp) {
				list.add(new Worker(i, "worker0", positions[0], periods[1]));
			}

			temp = null;
			sortMap(positions[1], periods[0]);
			for (int index = 0; index < workers.size(); index++) {
				int ranindex = random.nextInt(workers.size() / 2);
				Worker worker = new Worker(workers.get(ranindex));
				if (!indexs.contains(worker.getName())) {
					boolean isContinuous = isContinuous(worker, i);
					boolean isNightShift = isNightShift(worker, i);
					worker.setPlace(positions[1]);
					worker.setWorkTime(periods[0]);
					count = getWorkCount(worker);
					if (!isContinuous && !isNightShift && count < 7) {
						worker.setIndex(i);
						temp = worker;
						list.add(worker);
						map.put(worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime(), ++count);
						workerMap.get(worker.getName()).add(worker);
						indexs.add(worker.getName());
						break;
					}
				}
			}
			if (null == temp) {
				list.add(new Worker(i, "worker0", positions[1], periods[0]));
			}

			temp = null;
			sortMap(positions[1], periods[1]);
			for (int index = 0; index < workers.size(); index++) {
				int ranindex = random.nextInt(workers.size() / 2);
				Worker worker = new Worker(workers.get(ranindex));
				if (!indexs.contains(worker.getName())) {
					boolean isNightShift = isNightShift(worker, i);
					worker.setPlace(positions[1]);
					worker.setWorkTime(periods[1]);

					count = getWorkCount(worker);
					if (!isNightShift && count < 7) {
						worker.setIndex(i);
						temp = worker;
						list.add(worker);
						map.put(worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime(), ++count);
						workerMap.get(worker.getName()).add(worker);
						indexs.add(worker.getName());
						break;
					}
				}
			}
			if (null == temp) {
				list.add(new Worker(i, "worker0", positions[1], periods[1]));
			}
			temp = null;
			indexs.clear();
		}
	}

	/**
	 * 判断是否白班连班
	 * 
	 * @param worker
	 * @param index
	 * @return true:上次值班是白班连班 false：不是白班连班
	 */
	private static boolean isContinuous(Worker worker, int index) {
		List<Worker> list = workerMap.get(worker.getName());
		if (list.size() > 0) {
			Worker lastWorker = list.get(list.size() - 1);
			// 首先上次是白班
			if (periods[0].equals(lastWorker.getWorkTime())) {
				boolean isContinuous = lastWorker.isContinuous();
				if (isContinuous && lastWorker.getIndex() == index - 1) {
					return true;
				}
				if (isContinuous && lastWorker.getIndex() < index - 1) {
					lastWorker.setContinuous(false);
					return false;
				}
				if (!isContinuous && lastWorker.getIndex() == index - 1) {
					lastWorker.setContinuous(true);
					return false;
				}
			}
		}

		return false;
	}

	/**
	 * 判断选定worker的上个班是不是夜班，另外是否跟当前要排的班连续
	 * 
	 * @param worker
	 * @return true：昨天值夜班，休息24h，不能再排班了，不论是白班还是夜班 true表示不能接着排班，false能继续排班
	 */
	private static boolean isNightShift(Worker worker, int index) {
		List<Worker> list = workerMap.get(worker.getName());
		if (list.size() > 0) {
			Worker workman = list.get(list.size() - 1);
			String workTime = workman.getWorkTime();
			if (workTime.equals(periods[1])) {
				if (workman.getIndex() == index - 1) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * @param worker
	 * @return
	 */
	private static int getWorkCount(Worker worker) {
		Integer count = null;
		if (null == worker.getPlace()) {
			count = 0;
		} else {
			count = map.get(worker.getName() + "_" + worker.getPlace() + "_" + worker.getWorkTime());
			count = (count == null ? 0 : count.intValue());

		}
		return count;
	}


	private static List<String> sortMap() {
		List<String> sortList = new ArrayList<String>();
		if (null != map) {
			Set<Map.Entry<String, Integer>> set = map.entrySet();
			for (Map.Entry<String, Integer> entry : set) {
				String key = entry.getKey();
				int value = entry.getValue();
				key = key.split("_")[0];
				if (value > 0) {
					for (int i = 0; i < value; i++) {
						sortList.add(key);
					}
				}
			}

			Map<String, Integer> m = new HashMap<String, Integer>();
			for (int i = 0; i < workers.size(); i++) {
				int count = Collections.frequency(sortList, workers.get(i));
				m.put(workers.get(i), count);
			}

			ArrayList<Map.Entry<String, Integer>> l = new ArrayList<Map.Entry<String, Integer>>(m.entrySet());
			Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o1.getValue() - o2.getValue());
				}
			});

			sortList.clear();
			for (int i = 0; i < l.size(); i++) {
				Map.Entry<String, Integer> entry = l.get(i);
				sortList.add(entry.getKey());
			}
		}
		return sortList;
	}

	/**
	 * private static Map<String,Integer> map = new HashMap<String,Integer>();
	 */
	private static void sortMap(String... str) {
		List<String> sortList = new ArrayList<String>();
		if (null != map) {
			Set<Map.Entry<String, Integer>> set = map.entrySet();
			for (Map.Entry<String, Integer> entry : set) {
				String key = entry.getKey();
				int value = entry.getValue();
				String workername = key.split("_")[0];
				String place = key.split("_")[1];
				String shift = key.split("_")[2];
				if (str.length == 1) {// 按地点排序
					if (place.equalsIgnoreCase(str[0])) {
						if (value > 0) {
							for (int i = 0; i < value; i++) {
								sortList.add(workername);
							}
						}
					}
				} else if (str.length == 2) {// 按地点、班次排序
					if (place.equalsIgnoreCase(str[0]) && shift.equalsIgnoreCase(str[1])) {
						if (value > 0) {
							for (int i = 0; i < value; i++) {
								sortList.add(workername);
							}
						}
					}
				}
			}

			Map<String, Integer> m = new HashMap<String, Integer>();
			for (int i = 0; i < workers.size(); i++) {
				int count = Collections.frequency(sortList, workers.get(i));
				m.put(workers.get(i), count);
			}

			ArrayList<Map.Entry<String, Integer>> l = new ArrayList<Map.Entry<String, Integer>>(m.entrySet());
			Collections.sort(l, new Comparator<Map.Entry<String, Integer>>() {
				public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
					return (o1.getValue() - o2.getValue());
				}
			});

			List<String> list2 = new ArrayList<String>();
			for (int i = 0; i < l.size(); i++) {
				Map.Entry<String, Integer> entry = l.get(i);
				list2.add(entry.getKey());
			}

			List<String> sList = sortMap();
			List<String> list3 = new ArrayList<String>();
			for (int i = 4; i < sList.size(); i++) {
				list3.clear();
				list3.addAll(sList.subList(0, i));
				list3.retainAll(list2.subList(0, i));
				if (list3.size() == 4) {
					break;
				}
			}

			workers.clear();
			workers.addAll(list3);
			for (int i = 0; i < sList.size(); i++) {
				if (!workers.contains(sList.get(i))) {
					workers.add(sList.get(i));
				}
			}
		}
	}
}
