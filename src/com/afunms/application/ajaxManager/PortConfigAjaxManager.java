package com.afunms.application.ajaxManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.initialize.PortConfigCenter;

@SuppressWarnings("unchecked")
public class PortConfigAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	public void execute(String action) {
		if (action.equals("updateflag")) {
			updateflag();
		}
		if (action.equals("updateflag2")) {
			updateflag2();
		}
	}

	public void updateflag() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String ipaddress = request.getParameter("ipaddress");
			String tempport = request.getParameter("portArray");
			PortconfigDao dao = new PortconfigDao();
			String[] portArray = {};
			// �������ݿ�
			if (tempport == null || "".equals(tempport)) {

			} else {
				tempport = tempport.substring(0, tempport.length() - 1);
				portArray = tempport.split(",");
			}
			dao.updateportflag(ipaddress, portArray);
			// �����ڴ�
			Hashtable ht = PortConfigCenter.getInstance().getPortHastable();
			ArrayList list = new ArrayList();
			if (portArray != null && portArray.length > 0) {
				for (int i = 0; i < portArray.length; i++) {
					list.add("*" + portArray[i] + ":1");
				}
			}
			if (ht.containsKey(ipaddress)) {
				ht.remove(ipaddress);
				ht.put(ipaddress, list);
			} else {
				ht.put(ipaddress, list);
			}
			map.put("isSuccess", "1");
		} catch (Exception e) {
			map.put("isSuccess", "2");
			e.printStackTrace();
		}

		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}

	public void updateflag2() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String tempipaddress = request.getParameter("ipaddress");
			String tempport = request.getParameter("portArray");
			String flag = request.getParameter("flag");
			PortconfigDao dao = new PortconfigDao();
			String[] portArray = {};
			String[] ipArray = {};
			// �������ݿ�
			if (tempport == null || "".equals(tempport)) {

			} else {
				tempport = tempport.substring(0, tempport.length() - 1);
				portArray = tempport.split(",");
			}
			if (tempipaddress == null || "".equals(tempipaddress)) {

			} else {
				tempipaddress = tempipaddress.substring(0, tempipaddress.length() - 1);
				ipArray = tempipaddress.split(",");
			}
			Hashtable<String, List<String>> ipPortHt = new Hashtable<String, List<String>>();
			if (ipArray != null && ipArray.length > 0) {
				for (int i = 0; i < ipArray.length; i++) {
					if (ipPortHt.containsKey(ipArray[i])) {
						List<String> portList = (List) ipPortHt.get(ipArray[i]);
						if (portArray != null && portArray.length > 0) {
							portList.add(portArray[i]);
						}
						ipPortHt.remove(ipArray[i]);
						ipPortHt.put(ipArray[i], portList);
					} else {
						List<String> portList = new ArrayList<String>();
						if (portArray != null && portArray.length > 0) {
							portList.add(portArray[i]);
						}
						ipPortHt.put(ipArray[i], portList);
					}
				}
			}

			if (ipPortHt != null && ipPortHt.size() > 0) {
				for (Iterator it = ipPortHt.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					List<String> value = ipPortHt.get(key);
					String[] portS = new String[value.size()];
					for (int j = 0; j < value.size(); j++) {
						portS[j] = value.get(j);
					}
					if ("1".equals(flag)) {
						dao.updateportflag(key, portS, flag);
					} else {
						dao.updateportflag(key, portS);
					}
				}
			}
			map.put("isSuccess", "1");
		} catch (Exception e) {
			map.put("isSuccess", "2");
			e.printStackTrace();
		}

		JSONObject json = JSONObject.fromObject(map);
		out.print(json);
		out.flush();
	}
}
