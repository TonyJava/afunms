package com.afunms.polling;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.afunms.application.manage.PerformancePanelManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.DiskConfigDao;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.system.dao.SystemConfigDao;

@SuppressWarnings("unchecked")
public class PollingEngine {
	private Logger logger = Logger.getLogger(this.getClass());
	private List nodeList;
	private List dbList;
	private List tomcatList;
	private List resinList;
	private List weblogicList;
	private List webloginList;
	private List mqList;
	private List dominoList;
	private List wasList;
	private List cicsList;
	private List linkList;
	private List webList;
	private List dpList;
	private List ggsciList;
	private List nasList;
	private List ntpList;
	private List mailList;
	private List dhcpList;
	private List ftpList;
	private List tftpList;
	private List iisList;
	private List socketList;
	private List busList;
	private List proList;
	private List intfceList;
	private List jbossList;
	private List dnsList;
	private List apacheList;
	private List tuxedoList;
	private List cmtsList;
	// 浙江移动专有设备
	private List ggsnList;
	private List sgsnList;
	private List panelList;
	private List xmlList;

	private List upslist;
	private List airlist;

	private int nodesTotal; // 当前最后一个线程中的节点数
	private int threadsTotal; // 线程总数
	private String currentDate;
	private int runningDays; // 系统连续运行的天数
	private static String collectwebflag = "0"; // 系统运行模式 0：采集与访问集成 1：采集与访问分离
	private static String treeshowflag = "0"; // 资源显示模式 0:隐藏无设备节点 1：显示无设备节点
	private static String sendReportFlag = "0"; // 发送报表模式 0:不发送 1：发送
	private static PollingEngine instance = null;
	private static List addiplist = new ArrayList();
	private HashMap<String, List> AllTypeMap; 

	public static synchronized PollingEngine  getInstance() {
		if (instance == null)
			instance = new PollingEngine();
		return instance;
	}

	public String getCurrentDate() {
		return currentDate;
	}

	public static List getAddiplist() {
		return addiplist;
	}

	public static void setAddiplist(List addiplist) {
		PollingEngine.addiplist = addiplist;
	}

	private PollingEngine() {
		setCurrentDate(SysUtil.getCurrentDate());
		SystemConfigDao configdao = new SystemConfigDao();
		try {
			setCollectwebflag(configdao.getSystemCollectByVariablename("collectwebflag"));
			setTreeshowflag(configdao.getSystemCollectByVariablename("treeshowflag"));
			setSendReportFlag(configdao.getSystemCollectByVariablename("sendReportFlag"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			configdao.close();
		}
		nodeList = new ArrayList();
		dbList = new ArrayList();
		resinList = new ArrayList();
		tomcatList = new ArrayList();
		weblogicList = new ArrayList();
		mqList = new ArrayList();
		dominoList = new ArrayList();
		wasList = new ArrayList();
		cicsList = new ArrayList();
		ntpList = new ArrayList();
		webList = new ArrayList();
		dpList = new ArrayList();
		nasList = new ArrayList();
		ntpList = new ArrayList();
		ggsciList = new ArrayList();
		mailList = new ArrayList();
		ftpList = new ArrayList();
		tftpList = new ArrayList();
		dhcpList = new ArrayList();
		iisList = new ArrayList();
		socketList = new ArrayList();
		busList = new ArrayList();
		proList = new ArrayList();
		intfceList = new ArrayList();
		jbossList = new ArrayList();
		dnsList = new ArrayList();
		apacheList = new ArrayList();
		ggsnList = new ArrayList();
		sgsnList = new ArrayList();
		airlist = new ArrayList();
		upslist = new ArrayList();
		tuxedoList = new ArrayList();
		cmtsList = new ArrayList();
		webloginList = new ArrayList();
		AllTypeMap = new HashMap<String, List>();// yangjun add
	}

	public static String getCollectwebflag() {
		return collectwebflag;
	}

	public static void setCollectwebflag(String collectwebflag) {
		PollingEngine.collectwebflag = collectwebflag;
	}

	public synchronized void setCurrentDate(String cd) {
		currentDate = cd;
		runningDays++;
		logger.info("----------------系统运行 第 " + runningDays + " 天----------------");
	}

	public static String getTreeshowflag() {
		return treeshowflag;
	}

	public static void setTreeshowflag(String treeshowflag) {
		PollingEngine.treeshowflag = treeshowflag;
	}

	public static String getSendReportFlag() {
		return sendReportFlag;
	}

	public static void setSendReportFlag(String sendReportFlag) {
		PollingEngine.sendReportFlag = sendReportFlag;
	}

	public List getJBossList() {
		return jbossList;
	}

	public List getNodeList() {
		return nodeList;
	}

	public List getDbList() {
		return dbList;
	}

	public List getResinList() {
		return resinList;
	}

	public List getTomcatList() {
		return tomcatList;
	}

	public List getWeblogicList() {
		return weblogicList;
	}

	public List getTuxedoList() {
		return tuxedoList;
	}

	public List getCmtsList() {
		return cmtsList;
	}

	public List getMqList() {
		return mqList;
	}

	public List getDominoList() {
		return dominoList;
	}

	public List getWasList() {
		return wasList;
	}

	public List getCicsList() {
		return cicsList;
	}

	public List getWebList() {
		return webList;
	}

	public List getDpList() {
		return dpList;
	}

	public List getNasList() {
		return nasList;
	}

	public List getNtpList() {
		return ntpList;
	}

	public List getGgsciList() {
		return ggsciList;
	}

	public List getMailList() {
		return mailList;
	}

	public List getDHCPList() {
		return dhcpList;
	}

	public List getFtpList() {
		return ftpList;
	}

	public List getTftpList() {
		return tftpList;
	}

	public List getIisList() {
		return iisList;
	}

	public List getSocketList() {
		return socketList;
	}

	public List getBusList() {
		return busList;
	}

	public List getProList() {
		return proList;
	}

	public List getIntfceList() {
		return intfceList;
	}

	public List getDnsList() {
		return dnsList;
	}

	public List getApacheList() {
		return apacheList;
	}

	public List getGgsnList() {
		return ggsnList;
	}

	public List getSgsnList() {
		return sgsnList;
	}

	public List getUpsList() {
		return upslist;
	}

	public List getairList() {
		return airlist;
	}

	public static List loaderList;

	public void mapRefresh() {
		try {
			if (PollingEngine.loaderList != null) {
				List list = PollingEngine.loaderList;
				Iterator it = list.iterator();
				while (it.hasNext()) {
					try {
						Element element = (Element) it.next();
						NodeLoader loader = (NodeLoader) Class.forName(element.getChildText("class")).newInstance();
						try {
							loader.loading();
						} catch (Exception e) {
							e.printStackTrace();
						}  
						loader.notify();
						loader.getNmDao().close();
						loader = null;
					} catch (Exception e) {

					}
				}
			} else {
				logger.info("未加载/WEB-INF/classes/node-loader.xml");
			}
		} catch (Exception e) {
			logger.error(e);
		}
		// 初始化性能面板中的告警信息
		PerformancePanelManager.getInstance().init();
	}

	/**
	 * 开始轮询
	 */
	public void doPolling() {
		SAXBuilder builder = new SAXBuilder();
		try {
			logger.info("----------------加载节点-----------------");
			Document doc = builder.build(new File(ResourceCenter.getInstance().getSysPath() + "WEB-INF/classes/node-loader.xml"));
			List list = doc.getRootElement().getChildren("loader");
			PollingEngine.loaderList = list;
			Iterator it = list.iterator();
			while (it.hasNext()) {
				try {
					Element element = (Element) it.next();
					logger.info("--------------" + element.getChildText("descr") + "----------------");
					NodeLoader loader = (NodeLoader) Class.forName(element.getChildText("class")).newInstance();
					try {
						loader.loading();
					} catch (Exception e) {
						e.printStackTrace();
					} 
					loader.notify();
					loader.getNmDao().close();
					loader = null;
				} catch (Exception e) {

				}
			}
			logger.info("--------------加载节点完成！----------------");
		} catch (Exception e) {
			logger.error(e);
		}
		logger.info("--------------轮询开始！----------------");
		logger.info("--------------共有 " + threadsTotal + " 个轮询线程," + nodesTotal + " 个被监视节点----------------");
		DiskConfigDao diskdao = new DiskConfigDao();
		try {
			ShareData.setAlldiskalarmdata(diskdao.getByAlarmflag(99));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			diskdao.close();
		}
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getNodeByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = nodeList.indexOf(tmp);
		if (index != -1) {
			return (Node) nodeList.get(index);
		} else {
			return null;
		}
	}

	/**
	 * 按Ip查找某个节点
	 */
	public Node getNodeByIp(String ip) {
		Node tmp = new Node();
		Node tmp1 = new Node();
		for (int i = 0; i < nodeList.size(); i++) {
			tmp1 = (Node) nodeList.get(i);
			if (ip.equals(tmp1.getIpAddress())) {
				tmp.setId(tmp1.getId());
				break;
			}
		}
		int index = nodeList.indexOf(tmp);
		if (index != -1)
			return (Node) nodeList.get(index);
		else
			return null;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getDbByID(int id) {
		Node node = null;
		if (dbList != null && dbList.size() > 0) {
			for (int k = 0; k < dbList.size(); k++) {
				node = null;
				Node tnode = (Node) dbList.get(k);
				if (tnode != null) {
					if (tnode.getId() == id) {
						node = tnode;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getWeblogicByID(int id) {
		Node node = null;
		if (weblogicList != null && weblogicList.size() > 0) {
			for (int k = 0; k < weblogicList.size(); k++) {
				Node _node = (Node) weblogicList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {

						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找DHCP节点
	 */
	public Node getDHCPByID(int id) {
		Node node = null;
		if (dhcpList != null && dhcpList.size() > 0) {
			for (int k = 0; k < dhcpList.size(); k++) {
				Node _node = (Node) dhcpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {

						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getTomcatByID(int id) {
		Node node = null;
		if (tomcatList != null && tomcatList.size() > 0) {
			for (int k = 0; k < tomcatList.size(); k++) {
				Node _node = (Node) tomcatList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getMqByID(int id) {
		Node node = null;
		if (mqList != null && mqList.size() > 0) {
			for (int k = 0; k < mqList.size(); k++) {
				Node _node = (Node) mqList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getDominoByID(int id) {
		Node node = null;
		if (dominoList != null && dominoList.size() > 0) {
			for (int k = 0; k < dominoList.size(); k++) {
				Node _node = (Node) dominoList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getWasByID(int id) {
		Node node = null;
		if (wasList != null && wasList.size() > 0) {
			for (int k = 0; k < wasList.size(); k++) {
				Node _node = (Node) wasList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getCicsByID(int id) {
		Node node = null;
		if (cicsList != null && cicsList.size() > 0) {
			for (int k = 0; k < cicsList.size(); k++) {
				Node _node = (Node) cicsList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getWebByID(int id) {
		Node node = null;
		if (webList != null && webList.size() > 0) {
			for (int k = 0; k < webList.size(); k++) {
				Node _node = (Node) webList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getDpByID(int id) {
		Node node = null;
		if (dpList != null && dpList.size() > 0) {
			for (int k = 0; k < dpList.size(); k++) {
				Node _node = (Node) dpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getGgsciByID(int id) {
		Node node = null;
		if (ggsciList != null && ggsciList.size() > 0) {
			for (int k = 0; k < ggsciList.size(); k++) {
				Node _node = (Node) ggsciList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getNasByID(int id) {
		Node node = null;
		if (nasList != null && nasList.size() > 0) {
			for (int k = 0; k < nasList.size(); k++) {
				Node _node = (Node) nasList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点 ntp
	 */
	public Node getNtpByID(int id) {
		Node node = null;
		if (ntpList != null && ntpList.size() > 0) {
			for (int k = 0; k < ntpList.size(); k++) {
				Node _node = (Node) ntpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getMailByID(int id) {
		Node node = null;
		if (mailList != null && mailList.size() > 0) {
			for (int k = 0; k < mailList.size(); k++) {
				Node _node = (Node) mailList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getBusByID(int id) {
		Node node = null;
		if (busList != null && busList.size() > 0) {
			for (int k = 0; k < busList.size(); k++) {
				Node _node = (Node) busList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getProByID(int id) {
		Node node = null;
		if (proList != null && proList.size() > 0) {
			for (int k = 0; k < proList.size(); k++) {
				Node _node = (Node) proList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getIntfaceByID(int id) {
		Node node = null;
		if (intfceList != null && intfceList.size() > 0) {
			for (int k = 0; k < intfceList.size(); k++) {
				Node _node = (Node) intfceList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getFtpByID(int id) {
		Node node = null;
		if (ftpList != null && ftpList.size() > 0) {
			for (int k = 0; k < ftpList.size(); k++) {
				Node _node = (Node) ftpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getResinByID(int id) {
		Node node = null;
		if (resinList != null && resinList.size() > 0) {
			for (int k = 0; k < resinList.size(); k++) {
				Node _node = (Node) resinList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getWebLoginByID(int id) {
		Node node = null;
		if (webloginList != null && webloginList.size() > 0) {
			for (int k = 0; k < webloginList.size(); k++) {
				Node _node = (Node) webloginList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getTftpByID(int id) {
		Node node = null;
		if (tftpList != null && tftpList.size() > 0) {
			for (int k = 0; k < tftpList.size(); k++) {
				Node _node = (Node) tftpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getIisByID(int id) {
		Node node = null;
		if (iisList != null && iisList.size() > 0) {
			for (int k = 0; k < iisList.size(); k++) {
				Node _node = (Node) iisList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getSocketByID(int id) {
		Node node = null;
		if (socketList != null && socketList.size() > 0) {
			for (int k = 0; k < socketList.size(); k++) {
				Node _node = (Node) socketList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getNodeByIP(String ip) {
		Node node = null;
		if (nodeList != null && nodeList.size() > 0) {
			for (int k = 0; k < nodeList.size(); k++) {
				Node _node = (Node) nodeList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按IP查找某个节点
	 */
	public Node getDbByIP(String ip) {
		Node node = null;
		if (dbList != null && dbList.size() > 0) {
			for (int k = 0; k < dbList.size(); k++) {
				Node _node = (Node) dbList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getResinByIP(String ip) {
		Node node = null;
		if (resinList != null && resinList.size() > 0) {
			for (int k = 0; k < resinList.size(); k++) {
				Node _node = (Node) resinList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getTomcatByIP(String ip) {
		Node node = null;
		if (tomcatList != null && tomcatList.size() > 0) {
			for (int k = 0; k < tomcatList.size(); k++) {
				Node _node = (Node) tomcatList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getWeblogicByIP(String ip) {
		Node node = null;
		if (weblogicList != null && weblogicList.size() > 0) {
			for (int k = 0; k < weblogicList.size(); k++) {
				Node _node = (Node) weblogicList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getMqByIP(String ip) {
		Node node = null;
		if (mqList != null && mqList.size() > 0) {
			for (int k = 0; k < mqList.size(); k++) {
				Node _node = (Node) mqList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getDominoByIP(String ip) {
		Node node = null;
		if (dominoList != null && dominoList.size() > 0) {
			for (int k = 0; k < dominoList.size(); k++) {
				Node _node = (Node) dominoList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getWasByIP(String ip) {
		Node node = null;
		if (wasList != null && wasList.size() > 0) {
			for (int k = 0; k < wasList.size(); k++) {
				Node _node = (Node) wasList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getCicsByIP(String ip) {
		Node node = null;
		if (cicsList != null && cicsList.size() > 0) {
			for (int k = 0; k < cicsList.size(); k++) {
				Node _node = (Node) cicsList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getWebByIP(String ip) {
		Node node = null;
		if (webList != null && webList.size() > 0) {
			for (int k = 0; k < webList.size(); k++) {
				Node _node = (Node) webList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getDpByIP(String ip) {
		Node node = null;
		if (dpList != null && dpList.size() > 0) {
			for (int k = 0; k < dpList.size(); k++) {
				Node _node = (Node) dpList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getNasByIP(String ip) {
		Node node = null;
		if (nasList != null && nasList.size() > 0) {
			for (int k = 0; k < nasList.size(); k++) {
				Node _node = (Node) nasList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getNtpByIP(String ip) {
		Node node = null;
		if (ntpList != null && ntpList.size() > 0) {
			for (int k = 0; k < ntpList.size(); k++) {
				Node _node = (Node) ntpList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getMailByIP(String ip) {
		Node node = null;
		if (mailList != null && mailList.size() > 0) {
			for (int k = 0; k < mailList.size(); k++) {
				Node _node = (Node) mailList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getFtpByIP(String ip) {
		Node node = null;
		if (ftpList != null && ftpList.size() > 0) {
			for (int k = 0; k < ftpList.size(); k++) {
				Node _node = (Node) ftpList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getTftpByIP(String ip) {
		Node node = null;
		if (tftpList != null && tftpList.size() > 0) {
			for (int k = 0; k < tftpList.size(); k++) {
				Node _node = (Node) tftpList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getDHCPByIP(String ip) {
		Node node = null;
		if (dhcpList != null && dhcpList.size() > 0) {
			for (int k = 0; k < dhcpList.size(); k++) {
				Node _node = (Node) dhcpList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getIisByIP(String ip) {
		Node node = null;
		if (iisList != null && iisList.size() > 0) {
			for (int k = 0; k < iisList.size(); k++) {
				Node _node = (Node) iisList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getSocketByIP(String ip) {
		Node node = null;
		if (socketList != null && socketList.size() > 0) {
			for (int k = 0; k < socketList.size(); k++) {
				Node _node = (Node) socketList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public LinkRoad getLinkByID(int id) {
		LinkRoad link = null;
		for (int i = 0; i < linkList.size(); i++) {
			LinkRoad temp = (LinkRoad) linkList.get(i);
			if (temp.getId() == id) {
				link = temp;
				break;
			}
		}
		return link;
	}

	public void deleteLinkByID(int id) {
		int start = linkList.size() - 1;
		for (int i = start; i >= 0; i--) {
			LinkRoad temp = (LinkRoad) linkList.get(i);
			if (temp.getId() == id) {
				linkList.remove(temp);
				break;
			}
		}
	}

	/**
	 * 按ID删除某个节点
	 */
	public void deleteNodeByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = nodeList.indexOf(tmp);
		if (index != -1) {
			((Node) nodeList.get(index)).setManaged(false);
		}
		refreshAllTypeMap("net_wireless", nodeList);
		refreshAllTypeMap("net_switch_router", nodeList);
		refreshAllTypeMap("net_server", nodeList);
		refreshAllTypeMap("net_router", nodeList);
		refreshAllTypeMap("net_switch", nodeList);
		refreshAllTypeMap("net_firewall", nodeList);
		refreshAllTypeMap("net_gateway", nodeList);
		refreshAllTypeMap("net_atm", nodeList);
		refreshAllTypeMap("net_cmts", nodeList);
		refreshAllTypeMap("net_storage", nodeList);
		refreshAllTypeMap("net_f5", nodeList);
		refreshAllTypeMap("net_vpn", nodeList);
	}

	/**
	 * 按ID删除某个节点
	 */
	public void deleteDbByID(int id) {
		Node node = null;
		if (dbList != null && dbList.size() > 0) {
			for (int k = 0; k < dbList.size(); k++) {
				Node _node = (Node) dbList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						node.setManaged(false);
						dbList.remove(node);
						break;
					}
				}
			}
		}
		refreshAllTypeMap("dbs", dbList);
	}

	public void deleteResinByID(int id) {
		Node node = null;
		if (resinList != null && resinList.size() > 0) {
			for (int k = 0; k < resinList.size(); k++) {
				Node _node = (Node) resinList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						node.setManaged(false);
						resinList.remove(node);
						break;
					}
				}
			}
		}
		refreshAllTypeMap("resin", resinList);
	}

	public void deleteTomcatByID(int id) {
		Node node = null;
		if (tomcatList != null && tomcatList.size() > 0) {
			for (int k = 0; k < tomcatList.size(); k++) {
				Node _node = (Node) tomcatList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						node.setManaged(false);
						tomcatList.remove(node);
						break;
					}
				}
			}
		}
		refreshAllTypeMap("tomcat", tomcatList);
	}

	public void deleteFtpByID(int id) {
		if (ftpList != null && ftpList.size() > 0) {
			for (int k = 0; k < ftpList.size(); k++) {
				Node _node = (Node) ftpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) ftpList.get(index)).setManaged(false);
							ftpList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("ftp", ftpList);
	}

	public void deleteTftpByID(int id) {
		if (tftpList != null && tftpList.size() > 0) {
			for (int k = 0; k < tftpList.size(); k++) {
				Node _node = (Node) tftpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) tftpList.get(index)).setManaged(false);
							tftpList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("tftp", tftpList);
	}

	public void deleteIisByID(int id) {
		if (iisList != null && iisList.size() > 0) {
			for (int k = 0; k < iisList.size(); k++) {
				Node _node = (Node) iisList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) iisList.get(index)).setManaged(false);
							iisList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("iis", iisList);
	}

	public void deleteSocketByID(int id) {
		if (socketList != null && socketList.size() > 0) {
			for (int k = 0; k < socketList.size(); k++) {
				Node _node = (Node) socketList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) socketList.get(index)).setManaged(false);
							socketList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("socket", socketList);
	}

	public void deleteMailByID(int id) {
		if (mailList != null && mailList.size() > 0) {
			for (int k = 0; k < mailList.size(); k++) {
				Node _node = (Node) mailList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) mailList.get(index)).setManaged(false);
							mailList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("mail", mailList);
	}

	public void deleteWebByID(int id) {
		if (webList != null && webList.size() > 0) {
			for (int k = 0; k < webList.size(); k++) {
				Node _node = (Node) webList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) webList.get(index)).setManaged(false);
							webList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("wes", webList);
	}

	public void deleteDpByID(int id) {
		if (dpList != null && dpList.size() > 0) {
			for (int k = 0; k < dpList.size(); k++) {
				Node _node = (Node) dpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) dpList.get(index)).setManaged(false);
							dpList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("dp", dpList);
	}

	public void deleteGgsciByID(int id) {
		if (ggsciList != null && ggsciList.size() > 0) {
			for (int k = 0; k < ggsciList.size(); k++) {
				Node _node = (Node) ggsciList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) ggsciList.get(index)).setManaged(false);
							ggsciList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("ggsci", ggsciList);
	}

	public void deleteNasByID(int id) {
		if (nasList != null && nasList.size() > 0) {
			for (int k = 0; k < nasList.size(); k++) {
				Node _node = (Node) nasList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) nasList.get(index)).setManaged(false);
							nasList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("nas", nasList);
	}

	public void deleteNtpByID(int id) {
		if (ntpList != null && ntpList.size() > 0) {
			for (int k = 0; k < ntpList.size(); k++) {
				Node _node = (Node) ntpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) ntpList.get(index)).setManaged(false);
							ntpList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("ntp", ntpList);
	}

	public void deleteMqByID(int id) {
		if (mqList != null && mqList.size() > 0) {
			for (int k = 0; k < mqList.size(); k++) {
				Node _node = (Node) mqList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) mqList.get(index)).setManaged(false);
							mqList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("mqs", mqList);
	}

	public void deleteDominoByID(int id) {
		if (dominoList != null && dominoList.size() > 0) {
			for (int k = 0; k < dominoList.size(); k++) {
				Node _node = (Node) dominoList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) dominoList.get(index)).setManaged(false);
							dominoList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("domino", dominoList);
	}

	public void deleteWeblogicByID(int id) {
		if (weblogicList != null && weblogicList.size() > 0) {
			for (int k = 0; k < weblogicList.size(); k++) {
				Node _node = (Node) weblogicList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) weblogicList.get(index)).setManaged(false);
							weblogicList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("weblogic", weblogicList);
	}

	public void deleteDHCPByID(int id) {
		if (dhcpList != null && dhcpList.size() > 0) {
			for (int k = 0; k < dhcpList.size(); k++) {
				Node _node = (Node) dhcpList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) dhcpList.get(index)).setManaged(false);
							dhcpList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("dhcp", dhcpList);
	}

	public void deleteWasByID(int id) {
		if (wasList != null && wasList.size() > 0) {
			for (int k = 0; k < wasList.size(); k++) {
				Node _node = (Node) wasList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) wasList.get(index)).setManaged(false);
							wasList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("was", wasList);
	}

	public void deleteCicsByID(int id) {
		if (cicsList != null && cicsList.size() > 0) {
			for (int k = 0; k < cicsList.size(); k++) {
				Node _node = (Node) cicsList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) cicsList.get(index)).setManaged(false);
							cicsList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("cics", cicsList);
	}

	public void deleteBusByID(int id) {
		if (busList != null && busList.size() > 0) {
			for (int k = 0; k < busList.size(); k++) {
				Node _node = (Node) busList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) busList.get(index)).setManaged(false);
							busList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("bussiness", busList);
	}

	public void deleteProByID(int id) {
		if (proList != null && proList.size() > 0) {
			for (int k = 0; k < proList.size(); k++) {
				Node _node = (Node) proList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) proList.get(index)).setManaged(false);
							proList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("process", proList);
	}

	public void deleteIntfaceByID(int id) {
		if (intfceList != null && intfceList.size() > 0) {
			for (int k = 0; k < intfceList.size(); k++) {
				Node _node = (Node) intfceList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						int index = k;
						if (index != -1) {
							((Node) intfceList.get(index)).setManaged(false);
							intfceList.remove(index);
						}
						break;
					}
				}
			}
		}
		refreshAllTypeMap("interface", intfceList);
	}

	/**
	 * 按类型统计节点的个数
	 */
	public int getExchangeTotal() {
		int total = 0;
		for (int i = 0; i < nodeList.size(); i++) {
			if (((Node) nodeList.get(i)).getCategory() < 4 || ((Node) nodeList.get(i)).getCategory() == 10 || ((Node) nodeList.get(i)).getCategory() == 8
					|| ((Node) nodeList.get(i)).getCategory() == 9 || ((Node) nodeList.get(i)).getCategory() == 11)
				total++;
		}
		return total;
	}

	/**
	 * 按类型统计节点的个数
	 */
	public int getNodesTotal(int category) {
		int total = 0;
		for (int i = 0; i < nodeList.size(); i++) {
			if (((Node) nodeList.get(i)).getCategory() == category)
				total++;
		}
		return total;
	}

	/**
	 * 把节点加入轮询线程
	 */
	public void addNode(Node node) {

		nodeList.add(node);
		nodesTotal++;
		refreshAllTypeMap("net_wireless", nodeList);
		refreshAllTypeMap("net_switch_router", nodeList);
		refreshAllTypeMap("net_server", nodeList);
		refreshAllTypeMap("net_router", nodeList);
		refreshAllTypeMap("net_switch", nodeList);
		refreshAllTypeMap("net_firewall", nodeList);
		refreshAllTypeMap("net_gateway", nodeList);
		refreshAllTypeMap("net_f5", nodeList);
		refreshAllTypeMap("net_atm", nodeList);
		refreshAllTypeMap("net_cmts", nodeList);
		refreshAllTypeMap("net_storage", nodeList);
		refreshAllTypeMap("net_vpn", nodeList);
	}

	public void addResin(Node node) {
		resinList.add(node);
		refreshAllTypeMap("resin", resinList);
	}

	public void addTomcat(Node node) {
		tomcatList.add(node);
		refreshAllTypeMap("tomcat", tomcatList);
	}

	public void addTuxedo(Node node) {
		tuxedoList.add(node);
		refreshAllTypeMap("tuxedo", tuxedoList);
	}

	public void addDb(Node node) {
		dbList.add(node);
		refreshAllTypeMap("dbs", dbList);
	}

	public void addWeblogic(Node node) {
		weblogicList.add(node);
		refreshAllTypeMap("weblogic", weblogicList);
	}

	public void addDHCP(Node node) {
		dhcpList.add(node);
		refreshAllTypeMap("dhcp", dhcpList);
	}

	public void addMq(Node node) {
		mqList.add(node);
		refreshAllTypeMap("mqs", mqList);
	}

	public void addWas(Node node) {
		wasList.add(node);
		refreshAllTypeMap("was", wasList);
	}

	public void addCics(Node node) {
		cicsList.add(node);
		refreshAllTypeMap("cics", cicsList);
	}

	public void addDomino(Node node) {
		dominoList.add(node);
		refreshAllTypeMap("domino", dominoList);
	}

	public void addWeb(Node node) {
		webList.add(node);
		refreshAllTypeMap("wes", webList);
	}

	public void addDp(Node node) {
		dpList.add(node);
		refreshAllTypeMap("dp", dpList);
	}

	public void addGgsci(Node node) {
		ggsciList.add(node);
		refreshAllTypeMap("ggsci", ggsciList);
	}

	public void addNas(Node node) {
		nasList.add(node);
		refreshAllTypeMap("nas", nasList);
	}

	public void addNtp(Node node) {
		ntpList.add(node);
		refreshAllTypeMap("ntp", ntpList);
	}

	public void addMail(Node node) {
		mailList.add(node);
		refreshAllTypeMap("mail", mailList);
	}

	public void addFtp(Node node) {
		ftpList.add(node);
		refreshAllTypeMap("ftp", ftpList);
	}

	public void addWebLogin(Node node) {
		webloginList.add(node);
		refreshAllTypeMap("wblogin", webloginList);
	}

	public void addTftp(Node node) {
		tftpList.add(node);
		refreshAllTypeMap("tftp", tftpList);
	}

	public void addIis(Node node) {
		iisList.add(node);
		refreshAllTypeMap("iis", iisList);
	}

	public void addSocket(Node node) {
		socketList.add(node);
		refreshAllTypeMap("socket", socketList);
	}

	public void addBus(Node node) {
		busList.add(node);
		refreshAllTypeMap("bussiness", busList);
	}

	public void addPro(Node node) {
		proList.add(node);
		refreshAllTypeMap("process", proList);
	}

	public void addIntface(Node node) {
		intfceList.add(node);
		refreshAllTypeMap("interface", intfceList);
	}

	public List getLinkList() {
		return linkList;
	}

	public void setLinkList(List linkList) {
		this.linkList = linkList;
	}

	public List getPanelList() {
		return panelList;
	}

	public void setPanelList(List panelList) {
		this.panelList = panelList;
	}

	public List getXmlList() {
		return xmlList;
	}

	public void setXmlList(List xmlList) {
		this.xmlList = xmlList;
	}

	/**
	 * <p>
	 * Task：未完成
	 * </p>
	 * <p>
	 * 根据类型和ID到数据库中查找该node
	 * </p>
	 * 
	 * @author HONGLI
	 * @param category
	 * @param id
	 * @return
	 */
	public Node getNodeListByCategory(String category, int id) {
		Node node = null;
		List retList = null;
		if ("node".equals(category)) {
			retList = nodeList;

		} else if ("tomcat".equals(category)) {
			retList = tomcatList;
		} else if ("resin".equals(category)) {
			retList = resinList;
		} else if ("dbs".equals(category)) {
			retList = dbList;
		} else if ("weblogic".equals(category)) {
			retList = weblogicList;
		} else if ("mqs".equals(category)) {
			retList = mqList;
		} else if ("was".equals(category)) {
			retList = wasList;
		} else if ("cics".equals(category)) {
			retList = cicsList;
		} else if ("domino".equals(category)) {
			retList = dominoList;
		} else if ("wes".equals(category)) {
			retList = webList;
		} else if ("dp".equals(category)) {
			retList = dpList;
		} else if ("nas".equals(category)) {
			retList = nasList;
		} else if ("ntp".equals(category)) {
			retList = ntpList;
		} else if ("ggsci".equals(category)) {
			retList = ggsciList;
		} else if ("mail".equals(category)) {
			retList = mailList;
		} else if ("ftp".equals(category)) {
			retList = ftpList;
		} else if ("tftp".equals(category)) {
			retList = tftpList;
		} else if ("dhcp".equals(category)) {
			retList = dhcpList;
		} else if ("iis".equals(category)) {
			retList = iisList;
		} else if ("socket".equals(category)) {
			retList = socketList;
		} else if ("bussiness".equals(category)) {
			retList = busList;
		} else if ("process".equals(category)) {
			retList = proList;
		} else if ("interface".equals(category)) {
			retList = intfceList;
		}
		// 更新内存
		refreshAllTypeMap(category, retList);
		return node;
	}

	public HashMap<String, List> getAllTypeMap() {
		return AllTypeMap;
	}

	public void addToAllTypeMap(String key, List list) {
		AllTypeMap.put(key, list);
	}

	// 通过类型和id查找设备
	public Node getNodeByCategory(String category, int id) {
		List list = new ArrayList();
		list = AllTypeMap.get(category);
		Node node = null;
		if (list != null && list.size() > 0) {
			for (int k = 0; k < list.size(); k++) {
				Node _node = (Node) list.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}

		return node;
	}

	// 通过类型和id删除设备
	public void deleteNodeByCategory(String category, int id) {
		List list = AllTypeMap.get(category);
		if (list != null && list.size() > 0) {
			for (int k = 0; k < list.size(); k++) {
				Node _node = (Node) list.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						list.remove(_node);
						break;
					}
				}
			}
		}
		refreshAllTypeMap(category, list);
	}

	// 更新map
	public void refreshAllTypeMap(String category, List list) {
		if (AllTypeMap.containsKey(category)) {
			AllTypeMap.remove(category);
		}
		addToAllTypeMap(category, list);
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getJBossByID(int id) {
		Node node = null;
		if (jbossList != null && jbossList.size() > 0) {
			for (int k = 0; k < jbossList.size(); k++) {
				Node _node = (Node) jbossList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getJBossByIP(String ip) {
		Node node = null;
		if (jbossList != null && jbossList.size() > 0) {
			for (int k = 0; k < jbossList.size(); k++) {
				Node _node = (Node) jbossList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public void deleteJBossByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = jbossList.indexOf(tmp);
		if (index != -1) {
			((Node) jbossList.get(index)).setManaged(false);
			jbossList.remove(index);
		}
		refreshAllTypeMap("jboss", jbossList);
	}

	public void addJBoss(Node node) {
		jbossList.add(node);
		refreshAllTypeMap("jboss", jbossList);
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getDnsByID(int id) {
		Node node = null;
		if (dnsList != null && dnsList.size() > 0) {
			for (int k = 0; k < dnsList.size(); k++) {
				Node _node = (Node) dnsList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getDnsByIP(String ip) {
		Node node = null;
		if (dnsList != null && dnsList.size() > 0) {
			for (int k = 0; k < dnsList.size(); k++) {
				Node _node = (Node) dnsList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public void deleteDnsByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = dnsList.indexOf(tmp);
		if (index != -1) {
			((Node) dnsList.get(index)).setManaged(false);
			dnsList.remove(index);
		}
		refreshAllTypeMap("dns", dnsList);
	}

	public void addDns(Node node) {
		dnsList.add(node);
		refreshAllTypeMap("dns", dnsList);
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getApacheByID(int id) {
		Node node = null;
		if (apacheList != null && apacheList.size() > 0) {
			for (int k = 0; k < apacheList.size(); k++) {
				Node _node = (Node) apacheList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	/**
	 * 按ID查找某个节点
	 */
	public Node getTuxedoById(int id) {
		Node node = null;
		if (tuxedoList != null && tuxedoList.size() > 0) {
			for (int k = 0; k < tuxedoList.size(); k++) {
				Node _node = (Node) tuxedoList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getApacheByIP(String ip) {
		Node node = null;
		if (apacheList != null && apacheList.size() > 0) {
			for (int k = 0; k < apacheList.size(); k++) {
				Node _node = (Node) apacheList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getTuxdeoByIP(String ip) {
		Node node = null;
		if (tuxedoList != null && tuxedoList.size() > 0) {
			for (int k = 0; k < tuxedoList.size(); k++) {
				Node _node = (Node) tuxedoList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public void deleteTuxedoByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = tuxedoList.indexOf(tmp);
		if (index != -1) {
			((Node) tuxedoList.get(index)).setManaged(false);
			tuxedoList.remove(index);
		}
		refreshAllTypeMap("tuxedo", tuxedoList);
	}

	public void deleteApacheByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = apacheList.indexOf(tmp);
		if (index != -1) {
			((Node) apacheList.get(index)).setManaged(false);
			apacheList.remove(index);
		}
		refreshAllTypeMap("apache", apacheList);
	}

	public void addApache(Node node) {
		apacheList.add(node);
		refreshAllTypeMap("apache", apacheList);
	}

	public Node getSgsnByID(int id) {
		Node node = null;
		if (sgsnList != null && sgsnList.size() > 0) {
			for (int k = 0; k < sgsnList.size(); k++) {
				Node _node = (Node) sgsnList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getSgsnByIP(String ip) {
		Node node = null;
		if (sgsnList != null && sgsnList.size() > 0) {
			for (int k = 0; k < sgsnList.size(); k++) {
				Node _node = (Node) sgsnList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public void deleteSgsnByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = sgsnList.indexOf(tmp);
		if (index != -1) {
			((Node) sgsnList.get(index)).setManaged(false);
			sgsnList.remove(index);
		}
		refreshAllTypeMap("sgsn", sgsnList);
	}

	public void addSgsn(Node node) {
		sgsnList.add(node);
		refreshAllTypeMap("sgsn", sgsnList);
	}

	public Node getGgsnByID(int id) {
		Node node = null;
		if (ggsnList != null && ggsnList.size() > 0) {
			for (int k = 0; k < ggsnList.size(); k++) {
				Node _node = (Node) ggsnList.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public void addUps(Node node) {
		upslist.add(node);
		refreshAllTypeMap("ups", upslist);
	}

	public Node getUpsByID(int id) {
		Node node = null;
		if (upslist != null && upslist.size() > 0) {
			for (int k = 0; k < upslist.size(); k++) {
				Node _node = (Node) upslist.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getUpsByIP(String ip) {
		Node node = null;
		if (upslist != null && upslist.size() > 0) {
			for (int k = 0; k < upslist.size(); k++) {
				Node _node = (Node) upslist.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public void addAir(Node node) {
		airlist.add(node);
		refreshAllTypeMap("air", airlist);
	}

	public Node getAirByID(int id) {
		Node node = null;
		if (airlist != null && airlist.size() > 0) {
			for (int k = 0; k < airlist.size(); k++) {
				Node _node = (Node) airlist.get(k);
				if (_node != null) {
					if (_node.getId() == id) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public Node getAirByIP(String ip) {
		Node node = null;
		if (airlist != null && airlist.size() > 0) {
			for (int k = 0; k < airlist.size(); k++) {
				Node _node = (Node) airlist.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public void deleteAirByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = airlist.indexOf(tmp);
		if (index != -1) {
			((Node) airlist.get(index)).setManaged(false);
			airlist.remove(index);
		}
		refreshAllTypeMap("air", airlist);
	}

	public void deleteUpsByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = upslist.indexOf(tmp);
		if (index != -1) {
			((Node) upslist.get(index)).setManaged(false);
			upslist.remove(index);
		}
		refreshAllTypeMap("ups", upslist);
	}

	public Node getGgsnByIP(String ip) {
		Node node = null;
		if (ggsnList != null && ggsnList.size() > 0) {
			for (int k = 0; k < ggsnList.size(); k++) {
				Node _node = (Node) ggsnList.get(k);
				if (_node != null) {
					if (_node.getIpAddress().equalsIgnoreCase(ip)) {
						node = _node;
						break;
					}
				}
			}
		}
		return node;
	}

	public void deleteGgsnByID(int id) {
		Node tmp = new Node();
		tmp.setId(id);
		int index = ggsnList.indexOf(tmp);
		if (index != -1) {
			((Node) ggsnList.get(index)).setManaged(false);
			ggsnList.remove(index);
		}
		refreshAllTypeMap("ggsn", ggsnList);
	}

	public void addGgsn(Node node) {
		ggsnList.add(node);
		refreshAllTypeMap("ggsn", ggsnList);
	}

	public List getWebloginList() {
		return webloginList;
	}

}