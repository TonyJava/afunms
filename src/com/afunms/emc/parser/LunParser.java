package com.afunms.emc.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.emc.model.Lun;

public class LunParser {

	public static List<Lun> parse(String str) {
		List<Lun> list = new ArrayList<Lun>();

		String regex = "";
		if (!str.contains("Histogram overflows")) {
			regex = "(Total Hard Errors:\\s*.*\\r\\n)" + "(Total Soft Errors:\\s*.*\\r\\n)" + "(Total Queue Length:\\s*.*\\r\\n)" + "(Name\\s*.*\\r\\n)"
					+ "Minimum latency reads N/A(\\s*\\r\\n)+" + "(RAID Type:\\s*.*\\r\\n)" + "(RAIDGroup ID:\\s*.*\\r\\n)" + "(State:\\s*.*\\r\\n)"
					+ "Stripe Crossing:\\s*.*\\r\\n" + "Element Size:\\s*.*\\r\\n" + "(Current owner:\\s*.*\\r\\n)" + "Offset:\\s*.*\\r\\n" + "Auto-trespass:\\s*.*\\r\\n"
					+ "Auto-assign:\\s*.*\\r\\n" + "(Write cache:\\s*.*\\r\\n)" + "(Read cache:\\s*.*\\r\\n)" + "Idle Threshold:\\s*.*\\r\\n" + "Idle Delay Time:\\s*.*\\r\\n"
					+ "Write Aside Size:\\s*.*\\r\\n" + "(Default Owner:\\s*.*\\r\\n)" + "Rebuild Priority:\\s*.*\\r\\n" + "Verify Priority:\\s*.*\\r\\n"
					+ "Prct Reads Forced Flushed:\\s*.*\\r\\n" + "Prct Writes Forced Flushed:\\s*.*\\r\\n" + "(Prct Rebuilt:\\s*.*\\r\\n)" + "(Prct Bound:\\s*.*\\r\\n)"
					+ "(LUN Capacity.Megabytes.:\\s*.*\\r\\n)" + "LUN Capacity.Blocks.:\\s*.*\\r\\n" + "(UID:\\s*.*\\r\\n(Bus \\d+ Enclosure \\d+  Disk \\d+.*\\r\\n)+)";
		} else {

			regex = "(Total Hard Errors:\\s*.*\\r\\n)" + "(Total Soft Errors:\\s*.*\\r\\n)" + "(Total Queue Length:\\s*.*\\r\\n)" + "(Name\\s*.*\\r\\n)"
					+ "Minimum latency reads N/A(\\r\\n)+" + "(Read Histogram\\[\\d+\\] \\d+\\r\\n)+" + "Read Histogram overflows \\d+(\\r\\n)+"
					+ "(Write Histogram\\[\\d+\\] \\d+\\r\\n)+" + "Write Histogram overflows \\d+(\\r\\n)+" + "Read Requests:\\s*.*\\r\\n" + "Write Requests:\\s*.*\\r\\n"
					+ "Blocks read:\\s*.*\\r\\n" + "Blocks written:\\s*.*\\r\\n" + "Read cache hits:\\s*.*\\r\\n" + "Read cache misses:\\s*.*\\r\\n"
					+ "Prefetched blocks:\\s*.*\\r\\n" + "Unused prefetched blocks:\\s*.*\\r\\n" + "Write cache hits:\\s*.*\\r\\n" + "Forced flushes:\\s*.*\\r\\n"
					+ "Read Hit Ratio:\\s*.*\\r\\n" + "Write Hit Ratio:\\s*.*\\r\\n" + "(RAID Type:\\s*.*\\r\\n)" + "(RAIDGroup ID:\\s*.*\\r\\n)" + "(State:\\s*.*\\r\\n)"
					+ "Stripe Crossing:\\s*.*\\r\\n" + "Element Size:\\s*.*\\r\\n" + "(Current owner:\\s*.*\\r\\n)" + "Offset:\\s*.*\\r\\n" + "Auto-trespass:\\s*.*\\r\\n"
					+ "Auto-assign:\\s*.*\\r\\n" + "(Write cache:\\s*.*\\r\\n)" + "(Read cache:\\s*.*\\r\\n)" + "Idle Threshold:\\s*.*\\r\\n" + "Idle Delay Time:\\s*.*\\r\\n"
					+ "Write Aside Size:\\s*.*\\r\\n" + "(Default Owner:\\s*.*\\r\\n)" + "Rebuild Priority:\\s*.*\\r\\n" + "Verify Priority:\\s*.*\\r\\n"
					+ "Prct Reads Forced Flushed:\\s*.*\\r\\n" + "Prct Writes Forced Flushed:\\s*.*\\r\\n" + "(Prct Rebuilt:\\s*.*\\r\\n)" + "(Prct Bound:\\s*.*\\r\\n)"
					+ "(LUN Capacity.Megabytes.:\\s*.*\\r\\n)" + "LUN Capacity.Blocks.:\\s*.*\\r\\n" + "(UID:\\s*.*\\r\\n(Bus \\d+ Enclosure \\d+  Disk \\d+.*\\r\\n)+)";

		}

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		int i = 0;
		if (!str.contains("Histogram overflows")) {
			while (m.find()) {
				i++;
				Lun lun = new Lun();
				lun.setTotalHardErrors(Integer.parseInt(m.group(1).substring(m.group(1).indexOf(":") + 1).trim()));
				lun.setTotalSoftErrors(Integer.parseInt(m.group(2).substring(m.group(2).indexOf(":") + 1).trim()));
				lun.setTotalQueueLength(Integer.parseInt(m.group(3).substring(m.group(3).indexOf(":") + 1).trim()));
				lun.setName(m.group(4).substring(4).trim());
				lun.setRAIDType(m.group(6).substring(m.group(6).indexOf(":") + 1).trim());
				lun.setRAIDGroupID(m.group(7).substring(m.group(7).indexOf(":") + 1).trim());
				lun.setState(m.group(8).substring(m.group(8).indexOf(":") + 1).trim());
				lun.setCurrentOwner(m.group(9).substring(m.group(9).indexOf(":") + 1).trim());
				lun.setWritecache(m.group(10).substring(m.group(10).indexOf(":") + 1).trim());
				lun.setReadcache(m.group(11).substring(m.group(11).indexOf(":") + 1).trim());
				lun.setDefaultOwner(m.group(12).substring(m.group(12).indexOf(":") + 1).trim());
				lun.setPrctRebuilt(m.group(13).substring(m.group(13).indexOf(":") + 1).trim());
				lun.setPrctBound(m.group(14).substring(m.group(14).indexOf(":") + 1).trim());
				lun.setLUNCapacity(m.group(15).substring(m.group(15).indexOf(":") + 1).trim());
				lun.setDisksList(subParseToList(m.group(16).trim()));
				list.add(lun);
			}
		} else {
			while (m.find()) {
				i++;
				Lun lun = new Lun();

				lun.setTotalHardErrors(Integer.parseInt(m.group(1).substring(m.group(1).indexOf(":") + 1).trim()));
				lun.setTotalSoftErrors(Integer.parseInt(m.group(2).substring(m.group(2).indexOf(":") + 1).trim()));
				lun.setTotalQueueLength(Integer.parseInt(m.group(3).substring(m.group(3).indexOf(":") + 1).trim()));
				lun.setName(m.group(4).substring(4).trim());
				lun.setRAIDType(m.group(10).substring(m.group(10).indexOf(":") + 1).trim());
				lun.setRAIDGroupID(m.group(11).substring(m.group(11).indexOf(":") + 1).trim());
				lun.setState(m.group(12).substring(m.group(12).indexOf(":") + 1).trim());
				lun.setCurrentOwner(m.group(13).substring(m.group(13).indexOf(":") + 1).trim());
				lun.setWritecache(m.group(14).substring(m.group(14).indexOf(":") + 1).trim());
				lun.setReadcache(m.group(15).substring(m.group(15).indexOf(":") + 1).trim());
				lun.setDefaultOwner(m.group(16).substring(m.group(16).indexOf(":") + 1).trim());
				lun.setPrctRebuilt(m.group(17).substring(m.group(17).indexOf(":") + 1).trim());
				lun.setPrctBound(m.group(18).substring(m.group(18).indexOf(":") + 1).trim());
				lun.setLUNCapacity(m.group(19).substring(m.group(19).indexOf(":") + 1).trim());
				lun.setDisksList(subParseToList(m.group(20).trim()));

				list.add(lun);
			}
		}
		return list;
	}

	private static List<String> subParseToList(String str) {
		List<String> diskList = new ArrayList<String>();
		Pattern p = Pattern.compile("Bus \\d+ Enclosure \\d+  Disk \\d+");
		Matcher m = p.matcher(str);
		Set<String> diskSet = new HashSet<String>();
		while (m.find()) {
			diskSet.add(m.group().trim());
		}
		if (diskSet.size() > 0) {
			Iterator<String> iter = diskSet.iterator();
			while (iter.hasNext()) {
				diskList.add(iter.next());
			}
		}
		return diskList;
	}
}
