package com.afunms.topology.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.base.PanelNodeCategory;

@SuppressWarnings("unchecked")
public class PanelNodeHelper {
	private static HashMap categoryMap;
	private static List categoryList;
	static {
		categoryMap = new HashMap();
		categoryList = new ArrayList();
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/panel-category.xml"));
			List list = doc.getRootElement().getChildren("category");
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Element ele = (Element) it.next();
				PanelNodeCategory category = new PanelNodeCategory();
				String id = ele.getAttributeValue("id");
				category.setId(Integer.parseInt(id));
				category.setCnName(ele.getChildText("cn_name"));
				category.setEnName(ele.getChildText("en_name"));
				category.setUpUpImage("image/" + ele.getChildText("up_up_image"));
				category.setDownUpImage("image/" + ele.getChildText("down_up_image"));
				category.setUpDownImage("image/" + ele.getChildText("up_down_image"));
				category.setDownDownImage("image/" + ele.getChildText("down_down_image"));
				categoryMap.put(id, category);
				categoryList.add(category);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������������
	 */
	public static String getAlarmLevelDescr(int level) {
		String descr = null;
		if (level == 1) {
			descr = "ע��";
		} else if (level == 2) {
			descr = "����";
		} else if (level == 3) {
			descr = "����";
		}
		return descr;
	}

	/**
	 * ���������־
	 */
	public static String getAlarmLevelImage(int level) {
		String image = null;
		if (level == 1) {
			image = "alarm_level_1.gif";
		} else if (level == 2) {
			image = "alarm_level_2.gif";
		} else if (level == 3) {
			image = "alarm_level_3.gif";
		}
		return "image/topo/" + image;
	}

	public static List getAllCategorys() {
		return categoryList;
	}

	private static PanelNodeCategory getCategory(int id) {
		if (categoryMap.get(String.valueOf(id)) != null) {
			return (PanelNodeCategory) categoryMap.get(String.valueOf(id));
		} else {
			return (PanelNodeCategory) categoryMap.get("1000");
		}
	}

	public static String getDownDownImage(int category) {
		return getCategory(category).getDownDownImage();
	}

	public static String getDownUpImage(int category) {
		return getCategory(category).getDownUpImage();
	}

	public static String getHostOS(String sysOid) {
		String os = null;
		if (sysOid.startsWith("1.3.6.1.4.1.311.")) {
			os = "windows";
		} else if (sysOid.equals("1.3.6.1.4.1.2021.250.10") || sysOid.equals("1.3.6.1.4.1.8072.3.2.10")) {
			os = "linux";
		} else if (sysOid.startsWith("1.3.6.1.4.1.42.")) {
			os = "solaris";
		} else if (sysOid.startsWith("1.3.6.1.4.1.2.")) {
			os = "aix";
		} else if (sysOid.startsWith("1.3.6.1.4.1.36.")) {
			os = "tru64";
		} else if (sysOid.startsWith("1.3.6.1.4.1.9.")) {
			os = "cisco";
		} else {
			os = "";
		}
		return os;
	}

	/**
	 * �ڵ�������ͼ�ϵı���ʱ��ͼ��
	 */
	public static String getLostImage(int category) {
		return getCategory(category).getLostImage();
	}

	public static String getMenuItem(String index, String ip) {
		String menuItem =

		"<a class=\"panel_manage_menu_out\" onmouseover=\"panelmanageMenuOver();\" onmouseout=\"panelmanageMenuOut();\" "
				+ "onclick=\"javascript:window.open('/afunms/panel.do?action=show_portreset&ifindex=" + index + "&ipaddress=" + ip + "','window', "
				+ "'toolbar=no,height=300,width=600,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;����˿�</a><br/>" +

				"<a class=\"panel_detail_menu_out\" onmouseover=\"paneldetailMenuOver();\" onmouseout=\"paneldetailMenuOut();\" "
				+ "onclick=\"javascript:window.open('/afunms/monitor.do?action=show_utilhdx&ifindex=" + index + "&ipaddress=" + ip + "','window', "
				+ "'toolbar=no,height=500,width=600,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;�˿�ʵʱ��Ϣ</a><br/>";

		return menuItem;
	}

	/**
	 * �ڵ����(��������)
	 */
	public static String getNodeCategory(int category) {
		return getCategory(category).getCnName();
	}

	/**
	 * �ڵ����(Ӣ������)
	 */
	public static String getNodeEnCategory(int category) {
		return getCategory(category).getEnName();
	}

	/**
	 * ����sysOid�õ�������������ͼ�ϵı���ʱͼ��
	 */
	public static String getServerAlarmImage(String sysOid) {
		String fileName = null;
		if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.1")) {
			fileName = "win_xp_alarm.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.2") || sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.3")) {
			fileName = "win_2000_alarm.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1")) {
			fileName = "win_nt_alarm.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.2021.250.10") || sysOid.equals("1.3.6.1.4.1.8072.3.2.10")) {
			fileName = "linux_alarm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.42.")) {
			fileName = "solaris_alarm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.2.")) {
			fileName = "ibm_alarm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.11.")) {
			fileName = "hp_alarm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.36.")) {
			fileName = "compaq_alarm.gif";
		} else {
			fileName = "server_alarm.gif";
		}
		return "image/topo/" + fileName;
	}

	/**
	 * ����sysOid�õ�������������ͼ�ϵ�ͼ��
	 */
	public static String getServerTopoImage(String sysOid) {
		String fileName = null;
		if (sysOid == null) {
			fileName = "server.gif";
			return "image/topo/" + fileName;
		}
		if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.1")) {
			fileName = "win_xp.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.2") || sysOid.equals("1.3.6.1.4.1.311.1.1.3.1.3")) {
			fileName = "win_2000.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.311.1.1.3.1")) {
			fileName = "win_nt.gif";
		} else if (sysOid.equals("1.3.6.1.4.1.2021.250.10") || sysOid.equals("1.3.6.1.4.1.8072.3.2.10")) {
			fileName = "linux.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.42.")) {
			fileName = "solaris.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.2.")) {
			fileName = "ibm.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.11.")) {
			fileName = "hp.gif";
		} else if (sysOid.startsWith("1.3.6.1.4.1.36.")) {
			fileName = "compaq.gif";
		} else {
			fileName = "server.gif";
		}
		return "image/topo/" + fileName;
	}

	/**
	 * ϵͳ����״̬��־
	 */
	public static String getSnapStatusImage(int status) {
		String image = null;
		if (status == 1) {
			image = "status5.png";
		} else if (status == 2) {
			image = "status2.png";
		} else if (status == 3) {
			image = "status1.png";
		}
		return "image/topo/" + image;
	}

	/**
	 * ϵͳ����״̬��־
	 */
	public static String getSnapStatusImage(int status, int category) {
		String image = null;
		if (category == 1) {
			// ·����
			if (status == 2) {
				image = "Drouter-R-24.gif";
			} else {
				image = "Drouter-B-24.gif";
			}
		} else if (category == 2) {
			// ·����
			if (status == 2) {
				image = "Switch-R-32.gif";
			} else {
				image = "Switch-B-32.gif";
			}
		} else if (category == 3) {
			// ·����
			if (status == 2) {
				image = "server-R-24.gif";
			} else {
				image = "server-B-24.gif";
			}
		}
		return "image/topo/" + image;
	}

	/**
	 * �ڵ�״̬����
	 */
	public static String getStatusDescr(int status) {
		String descr = null;
		if (status == 1) {
			descr = "����";
		} else if (status == 2) {
			descr = "�豸æ";
		} else if (status == 3) {
			descr = "�ػ�";
		} else {
			descr = "��������";
		}
		return descr;
	}

	/**
	 * �ڵ�״̬��־
	 */
	public static String getStatusImage(int status) {
		String image = null;
		if (status == 1) {
			image = "status_ok.gif";
		} else if (status == 2) {
			image = "status_busy.gif";
		} else if (status == 3) {
			image = "status_down.gif";
		} else {
			image = "unmanaged.gif";
		}
		return "image/topo/" + image;
	}

	public static String getUpDownImage(int category) {
		return getCategory(category).getUpDownImage();
	}

	/**
	 * �ڵ�������ͼ�ϵ�ͼ��
	 */
	public static String getUpUpImage(int category) {
		return getCategory(category).getUpUpImage();
	}
}