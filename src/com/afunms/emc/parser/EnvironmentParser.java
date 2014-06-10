package com.afunms.emc.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.afunms.emc.model.Array;
import com.afunms.emc.model.Environment;
import com.afunms.emc.model.MemModel;

public class EnvironmentParser {

	private static Array arrayParse(String str) {
		Array array = new Array();
		Pattern p = Pattern.compile("Array\\r\\n\\s*\\r\\n" + "Input Power\\r\\n" + "Status:\\s*(\\w+)\\r\\n" + "Present.watts.:\\s*(\\w+)\\r\\n"
				+ "Rolling Average.watts.:\\s*(\\w+)\\r\\n");
		Matcher m = p.matcher(str);
		if (m.find()) {
			array.setStatus(m.group(1).trim());
			array.setPresentWatts(m.group(2).trim());
			array.setAveragewatts(m.group(3).trim());
		}
		return array;
	}

	private static List<MemModel> bakPowerParse(String str) {
		List<MemModel> memModelList = new ArrayList<MemModel>();
		Pattern p = Pattern.compile("(Bus \\d+ Enclosure \\d+ SPS \\w+)\\s*\\r\\n\\s*\\r\\n" + "Input Power\\r\\n" + "Status:\\s*(\\w+)\\r\\n" + "Present.watts.:\\s*(\\w+)\\r\\n"
				+ "Rolling Average.watts.:\\s*(\\w+)\\r\\n\\w*\\r\\n");
		Matcher m = p.matcher(str);
		while (m.find()) {
			MemModel memModel = new MemModel();
			memModel.setName(m.group(1).trim());
			memModel.setPowerStatus(m.group(2).trim());
			memModel.setPresentWatts(m.group(3).trim());
			memModel.setAverageWatts(m.group(4).trim());
			memModelList.add(memModel);
		}
		return memModelList;
	}

	/**
	 * @param args
	 */

	private static List<MemModel> memParse(String str) {
		List<MemModel> memModelList = new ArrayList<MemModel>();
		Pattern p = Pattern.compile("(\\w+ Bus \\d+ Enclosure \\d+)\\s*\\r\\n\\s*\\r\\n" + "Input Power\\r\\n" + "Status:\\s*(\\w+)\\r\\n" + "Present.watts.:\\s*(\\w+)\\r\\n"
				+ "Rolling Average.watts.:\\s*(\\w+)\\r\\n\\w*\\r\\n" + "Air Inlet Temperature\\r\\n" + "Status:\\s*(\\w+)\\r\\n" + "Present.degree C.:\\s*(\\w+)\\r\\n"
				+ "Rolling Average.degree C.:\\s*(\\w+)\\r\\n");
		Matcher m = p.matcher(str);
		while (m.find()) {
			MemModel memModel = new MemModel();
			memModel.setName(m.group(1).trim());
			memModel.setPowerStatus(m.group(2).trim());
			memModel.setPresentWatts(m.group(3).trim());
			memModel.setAverageWatts(m.group(4).trim());
			memModel.setAirStatus(m.group(5).trim());
			memModel.setPresentDegree(m.group(6).trim());
			memModel.setAverageDegree(m.group(7).trim());
			memModelList.add(memModel);
		}
		return memModelList;
	}

	public static Environment parse(String str) {
		Environment environment = new Environment();
		environment.setArray(arrayParse(str));
		environment.setMemList(memParse(str));
		environment.setBakPowerList(bakPowerParse(str));
		return environment;
	}
}
