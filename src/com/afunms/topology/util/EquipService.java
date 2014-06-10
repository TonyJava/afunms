package com.afunms.topology.util;

import java.util.HashMap;

import com.afunms.common.util.ShareData;
import com.afunms.topology.model.EquipImage;

@SuppressWarnings("unchecked")
public class EquipService {

	private HashMap EquipMap;

	public EquipService() {
		EquipMap = new HashMap();
		EquipMap = ShareData.getAllequpimgs();
	}

	private EquipImage getEquipImage(int id) {
		if (EquipMap.get(id) != null)
			return (EquipImage) EquipMap.get(id);
		else {
			return null;
		}
	}

	public String getTopoImage(int id) {
		return getEquipImage(id).getTopoImage();
	}

	public String getAlarmImage(int id) {
		return getEquipImage(id).getAlarmImage();
	}
}
