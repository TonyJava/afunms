package com.afunms.config.manage;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.dao.PanelModelDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.model.IpaddressPanel;
import com.afunms.config.model.PanelModel;
import com.afunms.config.model.Portconfig;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.PanelXmlOperator;

@SuppressWarnings("unchecked")
public class PanelModelManager extends BaseManager implements ManagerInterface {
	public static void creatXml(Hashtable has, String name, Hashtable hat, String width, String height) {
		int flag = 0;
		PanelXmlOperator panelxmlOpr = new PanelXmlOperator();
		try {
			panelxmlOpr.setFile(name + ".jsp", 1);
			panelxmlOpr.setOid(name);
			// 写XML
			panelxmlOpr.init4createXml();
			panelxmlOpr.createModelXml(has, hat);

		} catch (Exception e) {
			e.printStackTrace();
			flag = 1;
		}
		if (flag == 0) {
			// 写入数据库
			PanelModel model = new PanelModel();
			name = name.replaceAll("-", "\\.");
			model.setOid(name);
			model.setHeight(height);
			model.setWidth(width);

		}
	}

	/**
	 * The mothed is used for creating Xml Return flag == 1 is to create XML
	 * successful , else is false;
	 * 
	 * @author nielin add at 2010-01-07
	 * @param name,has,hat,width,height
	 * @return flag<code>{@link int}</code>
	 */
	public static int creatXml(String name, Hashtable has, Hashtable hat) {
		int flag = 0;
		PanelXmlOperator panelxmlOpr = new PanelXmlOperator();
		try {
			panelxmlOpr.setFile(name + ".jsp", 1);
			// 写XML
			panelxmlOpr.init4createXml();
			flag = panelxmlOpr.createModelXml(has, hat);
		} catch (Exception e) {
			flag = 0;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * @author nielin modify 2010-01-14
	 * @return
	 */
	private String createpanel() {
		String rvalue = "设置面板成功";
		int result = 0;
		try {
			String ipaddress = getParaValue("ipaddress");
			String imageType = getParaValue("imageType");
			IpaddressPanel ipaddressPanel = new IpaddressPanel();
			ipaddressPanel.setImageType(imageType);
			ipaddressPanel.setIpaddress(ipaddress);
			ipaddressPanel.setStatus("1");
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
			String oid = host.getSysOid();
			oid = oid.replaceAll("\\.", "-");
			PanelXmlOperator panelxmlOpr = new PanelXmlOperator();
			String filename = SysUtil.doip(host.getIpAddress()) + ".jsp";
			panelxmlOpr.setFile(filename, 2);
			panelxmlOpr.setOid(oid);
			panelxmlOpr.setImageType(imageType);
			panelxmlOpr.setIpaddress(host.getIpAddress());
			// 写XML
			panelxmlOpr.init4createXml();
			result = panelxmlOpr.createXml(1);
			if (result == 1) {
				boolean flag = false;
				IpaddressPanelDao ipaddressPanelDao = new IpaddressPanelDao();
				try {
					flag = ipaddressPanelDao.save(ipaddressPanel);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ipaddressPanelDao.close();
				}
				if (!flag) {
					result = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
			rvalue = "设置面板失败";
		}
		request.setAttribute("rvalue", rvalue);
		request.setAttribute("result", result);
		return panelnodelist();
	}

	/**
	 * @author nielin modify at 2010-01-07
	 * @return
	 */
	private String createpanelmodel() {
		String soid = getParaValue("soid");
		String height = getParaValue("height");
		String width = getParaValue("width");
		String addxyid = getParaValue("panelxml");
		String select = getParaValue("select");
		String imageType = getParaValue("imageType");
		String[] str = addxyid.split(";");
		String[] sel = select.split(";");
		Hashtable ht = new Hashtable();
		Hashtable hs = new Hashtable();
		for (int i = 0; i < str.length; i++) {
			ht.put(i, str[i]);
			hs.put(i, sel[i]);
		}
		try {
			String name = soid.replaceAll("\\.", "-");
			int falg = creatXml(name + "_" + imageType, ht, hs);
			if (falg == 1) {
				PanelModel model = new PanelModel();
				model.setOid(soid);
				model.setImageType(imageType);
				model.setHeight(height);
				model.setWidth(width);
				boolean result = false;
				PanelModelDao dao = new PanelModelDao();
				try {
					result = dao.save(model);
				} catch (Exception ex) {
					ex.printStackTrace();
					result = false;
				} finally {
					dao.close();
				}
				if (("1".equals(imageType)) && result) {
					defaultPanelModel(soid, imageType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("rvalue", "生成面板摸板成功！");
		return panelmodellist();
	}

	/**
	 * This method is used to make the network device with the same oid use a
	 * default template type 这个方法用于使具有相同oid的网络设备使用一个默认的模板类型
	 * 
	 * @author nielin add 2010-01-08
	 * @param oid
	 * @param imageType
	 * @return {@link Boolean}
	 */
	private boolean defaultPanelModel(String oid, String imageType) {
		HostNodeDao hostNodeDao = new HostNodeDao();
		try {
			// 获取具有相同oid的网络设备 get
			List list = hostNodeDao.loadHostByOid(oid);
			if (list != null && list.size() > 0) {
				List ipaddressPanelList = new ArrayList();
				for (int i = 0; i < list.size(); i++) {
					HostNode host = (HostNode) list.get(i);
					IpaddressPanel ipaddressPanel = new IpaddressPanel();
					ipaddressPanel.setIpaddress(host.getIpAddress());
					ipaddressPanel.setStatus("1");
					ipaddressPanel.setImageType(imageType);
					ipaddressPanelList.add(ipaddressPanel);
				}
				IpaddressPanelDao ipaddressPanelDao = new IpaddressPanelDao();
				try {
					ipaddressPanelDao.save(ipaddressPanelList);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				} finally {
					ipaddressPanelDao.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			hostNodeDao.close();
		}
		return true;
	}

	private String empty() {
		PortconfigDao dao = new PortconfigDao();
		dao.empty();
		dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
		return list(dao);
	}

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("createpanel")) {
			return createpanel();
		}
		if (action.equals("show_portreset")) {
			return showportreset();
		}
		if (action.equals("panelmodellist")) {
			return panelmodellist();
		}
		if (action.equals("showaddpanel")) {
			return showaddpanel();
		}
		if (action.equals("upload")) {
			return upload();
		}
		if (action.equals("createpanelmodel")) {
			return createpanelmodel();
		}
		if (action.equals("showedit")) {
			return readyEdit();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("updateport")) {
			return updateport();
		}
		if (action.equals("find")) {
			return find();
		}
		if (action.equals("updateselect")) {
			return updateselect();
		}
		if (action.equals("empty")) {
			return empty();
		}
		if (action.equals("ready_add")) {
			return "/config/portconfig/add.jsp";
		}
		if (action.equals("delete")) {
			DaoInterface dao = new PortconfigDao();
			setTarget("/portconfig.do?action=list");
			return delete(dao);
		}

		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private String find() {
		String ipaddress = getParaValue("ipaddress");
		PortconfigDao dao = new PortconfigDao();
		request.setAttribute("ipaddress", ipaddress);
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/findlist.jsp");
		return list(dao, " where ipaddress = '" + ipaddress + "'");
	}

	private int getImageType(String imgPath, String oid, int imageType) {
		File file = new File(imgPath + oid + "_" + imageType + ".jpg");
		if (file.exists()) {
			imageType++;

			imageType = getImageType(imgPath, oid, imageType);
		}
		return imageType;

	}

	public void inputPort(String equiptype, List list) {
		Hashtable porth = new Hashtable();
		Hashtable pp = new Hashtable();
		for (int i = 0; i < list.size(); i++) {
			String[] str = (String[]) list.get(i);
			pp.put(i, str[0] + ";" + str[1]);
		}
		porth.put(equiptype, pp);
	}

	private String list() {
		PortconfigDao dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		setTarget("/config/portconfig/list.jsp");
		return list(dao);
	}

	/**
	 * @author nielin add 2010-01-14
	 * @return
	 */
	private String panelmodellist() {
		String jsp = "/panel/view/panelmodellist.jsp";
		PanelModelDao panelModelDao = new PanelModelDao();
		try {
			setTarget(jsp);
			jsp = list(panelModelDao, " order by oid,imagetype");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			panelModelDao.close();
		}
		return jsp;
	}

	private String panelnodelist() {
		HostNodeDao dao = new HostNodeDao();
		setTarget("/topology/network/panelnodelist.jsp");
		return list(dao, " where managed=1 and (category<4 or category=7 or category=8 )");
	}

	private String readyEdit() {
		PortconfigDao dao = new PortconfigDao();
		Portconfig vo = new Portconfig();
		vo = dao.loadPortconfig(getParaIntValue("id"));
		request.setAttribute("vo", vo);
		return "/config/portconfig/edit.jsp";
	}

	/**
	 * @author nielin modify 2010-01-14
	 * @return
	 */
	private String showaddpanel() {
		HostNodeDao hostdao = new HostNodeDao();
		List iplist = null;
		try {
			iplist = hostdao.loadNetwork(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hostdao.close();
		}
		request.setAttribute("iplist", iplist);
		return "/panel/view/upload.jsp";
	}

	private String showportreset() {
		HostNode hostnode = null;
		String ip = "";
		String index = "";
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		Hashtable hash = new Hashtable();
		try {
			ip = getParaValue("ipaddress");// request.getParameter("ipaddress");
			index = getParaValue("ifindex");// request.getParameter("index");
			String[] netIfdetail = { "index", "ifDescr", "ifname", "ifType", "ifMtu", "ifSpeed", "ifPhysAddress", "ifOperStatus", "ifAdminStatus" };
			hash = hostlastmanager.getIfdetail_share(ip, index, netIfdetail, "", "");
			HostNodeDao hostdao = new HostNodeDao();
			List hostlist = hostdao.loadNetwork(1);
			if (hostlist != null && hostlist.size() > 0) {
				for (int i = 0; i < hostlist.size(); i++) {
					HostNode tempHost = (HostNode) hostlist.get(i);
					if (tempHost.getIpAddress().equalsIgnoreCase(ip)) {
						hostnode = tempHost;
						break;
					}
				}
			}
			hostdao.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String alias = "";
		if (hostnode != null) {
			alias = hostnode.getAlias();
		}
		request.setAttribute("index", index);
		request.setAttribute("ipaddress", ip);
		request.setAttribute("hostname", alias);

		request.setAttribute("hash", hash);
		return "/panel/view/portreset.jsp";
	}

	private String update() {
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		PortconfigDao dao = new PortconfigDao();
		vo = dao.loadPortconfig(id);
		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);
		if (sms > -1) {
			vo.setSms(sms);
		}
		if (reportflag > -1) {
			vo.setReportflag(reportflag);
		}
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new PortconfigDao();
		List ips = dao.getIps();
		request.setAttribute("ips", ips);
		dao = new PortconfigDao();
		return "/portconfig.do?action=list";
	}

	private String updateport() {
		Portconfig vo = new Portconfig();
		int id = getParaIntValue("id");
		PortconfigDao dao = new PortconfigDao();
		vo = dao.loadPortconfig(id);
		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);
		if (sms > -1) {
			vo.setSms(sms);
		}
		if (reportflag > -1) {
			vo.setReportflag(reportflag);
		}
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new PortconfigDao();

		return "/portconfig.do?action=list";
	}

	private String updateselect() {
		String key = getParaValue("key");
		String value = getParaValue("value");
		PortconfigDao dao = new PortconfigDao();
		request.setAttribute("key", key);
		request.setAttribute("value", value);
		int id = getParaIntValue("id");
		Portconfig vo = new Portconfig();
		vo = dao.loadPortconfig(id);

		dao.close();
		String linkuse = getParaValue("linkuse");
		int sms = getParaIntValue("sms");
		int reportflag = getParaIntValue("reportflag");
		vo.setLinkuse(linkuse);

		vo.setSms(sms);
		vo.setReportflag(reportflag);
		dao = new PortconfigDao();
		try {
			dao.update(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao = new PortconfigDao();
		setTarget("/config/portconfig/findlist.jsp");
		return list(dao, " where " + key + " = '" + value + "'");
	}

	/**
	 * This method is used to handle the name of upload pictures
	 * 
	 * @author nielin add for panelmodel 2010-01-07
	 * @return
	 */
	private String upload() {
		String ipaddress = request.getParameter("ipaddress");
		Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
		request.setAttribute("host", host);
		String oid = host.getSysOid();
		String imgPath = ResourceCenter.getInstance().getSysPath() + "panel/view/image/";
		oid = oid.replaceAll("\\.", "-");
		int imageType = 1;
		imageType = getImageType(imgPath, oid, imageType);
		request.setAttribute("imageType", String.valueOf(imageType));
		String fileName = ".." + request.getContextPath() + "/panel/view/image/" + oid + "_" + imageType + ".jpg";
		request.setAttribute("fileName", fileName);

		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		Vector vector = new Vector();
		String[] netInterfaceItem = { "index", "ifDescr", "ifType", "ifSpeed", "ifOperStatus", "OutBandwidthUtilHdx", "InBandwidthUtilHdx" };
		try {
			vector = hostlastmanager.getInterface_share(ipaddress, netInterfaceItem, "index", "", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		List indexlist = new ArrayList();
		Hashtable hs = new Hashtable();
		int p = 0;
		if (vector != null && vector.size() > 0) {
			for (int k = 0; k < vector.size(); k++) {
				String[] strs = (String[]) vector.get(k);
				String ifname = strs[1];
				String index = strs[0];
				String iftype = strs[2];
				SysLogger.info("ip:" + ipaddress + "---index:" + index + "---ifname:" + ifname + "---iftype:" + iftype);
				if ((iftype.equalsIgnoreCase("ethernetCsmacd(6)") || (iftype.equalsIgnoreCase("gigabitEthernet(117)")) || (iftype.equalsIgnoreCase("ppp(23)"))
						|| (iftype.equalsIgnoreCase("sonet(39)")) || (iftype.equalsIgnoreCase("0.0")))
						&& !"LoopBack0".equalsIgnoreCase(ifname)) {
					hs.put(p, index + ";" + ifname);
					SysLogger.info("ip:" + ipaddress + "===index:" + index + "===iftype:" + iftype);
					indexlist.add(p, index + "");
					p = p + 1;
				}
			}
		}
		request.setAttribute("hs", hs);
		request.setAttribute("indexlist", indexlist);
		return "/panel/view/panel.jsp";
	}
}
