package com.afunms.emc.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.emc.model.HardCrus;

public class CrusParser {

	public static List<HardCrus> parse(String str) {
		List<HardCrus> list = new ArrayList<HardCrus>();
		String regex = "(\\w+ Bus \\d+ Enclosure \\d+)\\s*\\r\\n" + "((SP [A-Z] State:\\s*\\w+\\r\\n)+)" + "((Bus \\d+ Enclosure \\d+ Power [A-Z] State:\\s*\\w+\\r\\n)+)"
				+ "((Bus \\d+ Enclosure \\d+ SPS [A-Z] State:\\s*\\w+\\r\\n)+)" + "((Bus \\d+ Enclosure \\d+ SPS [A-Z] Cabling State:\\s*\\w+\\r\\n)+)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			HardCrus crus = new HardCrus();
			crus.setName(m.group(1));
			crus.setSpState(slsParse(m.group(2), "(SP [A-Z] State):\\s*(\\w+)\\r\\n"));
			crus.setPowerState(slsParse(m.group(4), "(Bus \\d+ Enclosure \\d+ Power [A-Z] State):\\s*(\\w+)\\r\\n"));
			crus.setSpsState(slsParse(m.group(6), "(Bus \\d+ Enclosure \\d+ SPS [A-Z] State):\\s*(\\w+)\\r\\n"));
			crus.setSpsCablingState(slsParse(m.group(8), "(Bus \\d+ Enclosure \\d+ SPS [A-Z] Cabling State):\\s*(\\w+)\\r\\n"));
			list.add(crus);
		}

		regex = "(\\w+ Bus \\d+ Enclosure \\d+)\\s*\\r\\n" + "((Bus \\d+ Enclosure \\d+ Power [A-Z] State:\\s*\\w+\\r\\n)+)"
				+ "((Bus \\d+ Enclosure \\d+ LCC [A-Z] State:\\s*\\w+\\r\\n)+)";
		p = Pattern.compile(regex);
		m = p.matcher(str);
		while (m.find()) {
			HardCrus crus = new HardCrus();
			crus.setName(m.group(1));
			crus.setPowerState(slsParse(m.group(2), "(Bus \\d+ Enclosure \\d+ Power [A-Z] State):\\s*(\\w+)\\r\\n"));
			crus.setLccState(slsParse(m.group(4), "(Bus \\d+ Enclosure \\d+ LCC [A-Z] State):\\s*(\\w+)\\r\\n"));
			list.add(crus);
		}
		return list;
	}

	private static Map<String, String> slsParse(String str, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		Map<String, String> map = new HashMap<String, String>();
		while (m.find()) {
			map.put(m.group(1).trim(), m.group(2).trim());
		}
		return map;
	}

}
