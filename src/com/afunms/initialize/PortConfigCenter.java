package com.afunms.initialize;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.util.DBManager;

@SuppressWarnings("unchecked")
public class PortConfigCenter {
	private static PortConfigCenter instance = new PortConfigCenter();

	public static PortConfigCenter getInstance() {
		return instance;
	}

	private Hashtable portHastable = new Hashtable();

	/**
	 * ��ȡ��Ҫ��ӵ�����ͳ���еĶ˿ڼ���
	 * 
	 * @return
	 */
	public Hashtable getPortHastable() {
		return portHastable;
	}

	/**
	 * ����ȫ�ֱ��� ��ȡ��Ҫ��ӵ�����ͳ���еĶ˿ڼ���
	 */
	public void setPortHastable() {
		portHastable = new Hashtable();
		DBManager dbManager = new DBManager();
		try {
			String sql = " select ipaddress,portindex,flag from system_portconfig where flag=1 ";
			ResultSet rs = dbManager.executeQuery(sql);
			try {
				while (rs.next()) {
					String ipaddress = rs.getString("ipaddress");
					int portindex = rs.getInt("portindex");
					String flag = rs.getString("flag");
					List list = new ArrayList();
					if (portHastable.containsKey(ipaddress)) {
						list = (List) portHastable.get(ipaddress);
						list.add("*" + portindex + ":" + flag);
					} else {
						list.add("*" + portindex + ":" + flag);
						portHastable.put(ipaddress, list);
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				rs.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbManager.close();
		}
	}
}
