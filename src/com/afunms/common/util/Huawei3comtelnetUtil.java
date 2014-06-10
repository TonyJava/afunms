package com.afunms.common.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.model.Huaweitelnetconf;

/**
 * 
 * telnet 读取配置文件把vpn配置信息工具类
 * 
 * @author konglq
 * 
 */

@SuppressWarnings("unchecked")
public class Huawei3comtelnetUtil {

	public static Hashtable telnetconf = new Hashtable();

	/**
	 * 读取配置文件找配置有vpn的端口 使用接口的名称为key ，vpn的名称作为value
	 * 
	 * @param conf
	 *            采集的配置字符串
	 * @return 返回vpn的配置列表
	 */
	public static Hashtable Getvpnlist(String conf) {
		Hashtable list = new Hashtable();
		if (null != conf) {
			// 解析配置文件
			String[] datelist = conf.split("\r\n");
			if (null != datelist && datelist.length > 0) {
				boolean flgvpn = false;// 找到端口的标记
				String infname = "";
				for (int i = 0; i < datelist.length; i++) {
					// 先找到网口，然后根据端口再找到对应的配置，重新把端口标记设置为false
					String inf = datelist[i].trim();
					if (inf.indexOf("interface") == 0) {
						infname = inf.replaceAll("interface", "").trim();
						flgvpn = true;
					}
					// 查找端口的vpn配置信息
					if (flgvpn && inf.indexOf("interface") == -1) {// 接口
						if (inf.indexOf("ip binding vpn-instance") >= 0) {
							list.put(infname, inf.replace("ip binding vpn-instance", "").trim());
							flgvpn = false;
						}
					}
				}// 循环结束
			}
		}
		return list;
	}

	/**
	 * 
	 * 初始化telnet连接的内存列表 是用网元的id做为key，数据库表对象为值
	 * 
	 */
	public static void inittelnetlist() {
		HaweitelnetconfDao dao = new HaweitelnetconfDao();
		List list = new ArrayList();
		try {
			list = dao.loadEnableVpn();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (null != list) {
			for (int i = 0; i < list.size(); i++) {
				Huaweitelnetconf mo = new Huaweitelnetconf();
				mo = (Huaweitelnetconf) list.get(i);
				telnetconf.put(mo.getId(), mo);
			}
		}
	}

}
