package com.afunms.common.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.config.dao.HaweitelnetconfDao;
import com.afunms.config.model.Huaweitelnetconf;

/**
 * 
 * telnet ��ȡ�����ļ���vpn������Ϣ������
 * 
 * @author konglq
 * 
 */

@SuppressWarnings("unchecked")
public class Huawei3comtelnetUtil {

	public static Hashtable telnetconf = new Hashtable();

	/**
	 * ��ȡ�����ļ���������vpn�Ķ˿� ʹ�ýӿڵ�����Ϊkey ��vpn��������Ϊvalue
	 * 
	 * @param conf
	 *            �ɼ��������ַ���
	 * @return ����vpn�������б�
	 */
	public static Hashtable Getvpnlist(String conf) {
		Hashtable list = new Hashtable();
		if (null != conf) {
			// ���������ļ�
			String[] datelist = conf.split("\r\n");
			if (null != datelist && datelist.length > 0) {
				boolean flgvpn = false;// �ҵ��˿ڵı��
				String infname = "";
				for (int i = 0; i < datelist.length; i++) {
					// ���ҵ����ڣ�Ȼ����ݶ˿����ҵ���Ӧ�����ã����°Ѷ˿ڱ������Ϊfalse
					String inf = datelist[i].trim();
					if (inf.indexOf("interface") == 0) {
						infname = inf.replaceAll("interface", "").trim();
						flgvpn = true;
					}
					// ���Ҷ˿ڵ�vpn������Ϣ
					if (flgvpn && inf.indexOf("interface") == -1) {// �ӿ�
						if (inf.indexOf("ip binding vpn-instance") >= 0) {
							list.put(infname, inf.replace("ip binding vpn-instance", "").trim());
							flgvpn = false;
						}
					}
				}// ѭ������
			}
		}
		return list;
	}

	/**
	 * 
	 * ��ʼ��telnet���ӵ��ڴ��б� ������Ԫ��id��Ϊkey�����ݿ�����Ϊֵ
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
