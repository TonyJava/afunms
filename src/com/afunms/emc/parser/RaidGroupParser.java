package com.afunms.emc.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.emc.model.RaidGroup;

public class RaidGroupParser {

	public static List<RaidGroup> parse(String str) {
		List<RaidGroup> list = new ArrayList<RaidGroup>();
		String regex = "(RaidGroup ID:\\s*.*\\r\\n)" + "(RaidGroup Type:\\s*.*\\r\\n)" + "(RaidGroup State:(\\s*\\w+\\r\\n)+)"
				+ "(List of disks:(\\s*Bus \\d+ Enclosure \\d+  Disk \\d+\\r\\n)+)" + "(List of luns:\\s*.*\\r\\n)" + "(Max Number of disks:\\s*.*\\r\\n)"
				+ "(Max Number of luns:\\s*.*\\r\\n)" + "(Raw Capacity .*:\\s*.*\\r\\n)" + "(Logical Capacity .*:\\s*.*\\r\\n)" + "(Free Capacity .*:\\s*.*\\r\\n)"
				+ "(Free contiguous group of unbound segments:\\s*.*\\r\\n)"// ²»ÐèÒª
				+ "(Defrag/Expand priority:\\s*.*\\r\\n)";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			RaidGroup raidGroup = new RaidGroup();
			raidGroup.setRid(m.group(1).substring(m.group(1).indexOf(":") + 1).trim());
			raidGroup.setType(m.group(2).substring(m.group(2).indexOf(":") + 1).trim());
			raidGroup.setState(parseToArray(m.group(3).substring(m.group(3).indexOf(":") + 1).trim()));
			raidGroup.setDisksList(parseToList(m.group(5).substring(m.group(5).indexOf(":") + 1).trim()));
			raidGroup.setLunsList(parseToList(m.group(7).substring(m.group(7).indexOf(":") + 1).trim()));
			raidGroup.setMaxNumDisk(m.group(8).substring(m.group(8).indexOf(":") + 1).trim());
			raidGroup.setMaxNumLun(m.group(9).substring(m.group(9).indexOf(":") + 1).trim());
			raidGroup.setRawCapacity(m.group(10).substring(m.group(10).indexOf(":") + 1).trim());
			raidGroup.setLogicalCapacity(m.group(11).substring(m.group(11).indexOf(":") + 1).trim());
			raidGroup.setFreeCapacity(m.group(12).substring(m.group(12).indexOf(":") + 1).trim());
			raidGroup.setDefragPriority(m.group(14).substring(m.group(14).indexOf(":") + 1).trim());
			list.add(raidGroup);
		}
		return list;
	}

	private static String[] parseToArray(String str) {
		String[] array = str.split("\\r\\n");
		for (int i = 0; i < array.length; i++) {
			array[i] = array[i].trim();
		}
		return array;
	}

	private static List<String> parseToList(String str) {
		if ("Not Available".equals(str)) {
			return null;
		}
		String[] array = str.split("\\r\\n");
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i].trim());
		}
		return list;
	}
}
