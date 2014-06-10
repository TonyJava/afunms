package com.afunms.topology.manage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContextFactory;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.dao.IndicatorsTopoRelationDao;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.IndicatorsTopoRelation;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.util.ProcessGroupConfigurationUtil;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.CreateTableManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.Speak;
import com.afunms.common.util.SysUtil;
import com.afunms.config.dao.IpAliasDao;
import com.afunms.config.dao.IpaddressPanelDao;
import com.afunms.config.dao.NetNodeCfgFileDao;
import com.afunms.config.dao.PanelModelDao;
import com.afunms.config.dao.PortconfigDao;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.IpAlias;
import com.afunms.config.model.IpaddressPanel;
import com.afunms.config.model.PanelModel;
import com.afunms.config.model.Portconfig;
import com.afunms.config.model.Supper;
import com.afunms.discovery.DiscoverDataHelper;
import com.afunms.discovery.RepairLink;
import com.afunms.event.dao.CheckEventDao;
import com.afunms.event.dao.EventListDao;
import com.afunms.event.dao.NetSyslogDao;
import com.afunms.event.model.EventList;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Bussiness;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.IfEntity;
import com.afunms.polling.om.ARP;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.task.UpdateXmlTaskTest;
import com.afunms.portscan.dao.PortScanDao;
import com.afunms.system.model.User;
import com.afunms.temp.model.Objbean;
import com.afunms.topology.dao.ARPDao;
import com.afunms.topology.dao.CommonDao;
import com.afunms.topology.dao.ConnectTypeConfigDao;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.dao.EquipImageDao;
import com.afunms.topology.dao.HintItemDao;
import com.afunms.topology.dao.HintNodeDao;
import com.afunms.topology.dao.HostInterfaceDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.IpMacBaseDao;
import com.afunms.topology.dao.IpMacChangeDao;
import com.afunms.topology.dao.IpMacDao;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.dao.NodeEquipDao;
import com.afunms.topology.dao.RelationDao;
import com.afunms.topology.dao.RepairLinkDao;
import com.afunms.topology.dao.TreeNodeDao;
import com.afunms.topology.model.EquipImage;
import com.afunms.topology.model.HintLine;
import com.afunms.topology.model.HintNode;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.model.NodeEquip;
import com.afunms.topology.model.Relation;
import com.afunms.topology.model.TreeNode;
import com.afunms.topology.service.TopoNodeInfoService;
import com.afunms.topology.util.EquipService;
import com.afunms.topology.util.ManageXmlOperator;
import com.afunms.topology.util.NodeHelper;
import com.afunms.topology.util.TopoUI;
import com.afunms.topology.util.XmlOperator;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

@SuppressWarnings("unchecked")
public class SubMapManager extends BaseManager implements ManagerInterface {

	// 获取图片大小
	private static String getImageSize(String url) {
		File file = new File(url);
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
			return "";
		}
		BufferedImage sourceImg = null;
		try {
			sourceImg = javax.imageio.ImageIO.read(is);
		} catch (IOException e1) {
			e1.printStackTrace();
			return "";
		}
		System.out.println("width = " + sourceImg.getWidth() + "height = " + sourceImg.getHeight());

		return sourceImg.getWidth() + ":" + sourceImg.getHeight();

	}

	// 创建子图，保存已选图元信息
	public synchronized static List<IfEntity> getSortListByHash(Hashtable<String, IfEntity> orignalHash) {
		if (orignalHash == null) {
			return null;
		}
		List<IfEntity> retList = new ArrayList<IfEntity>();
		Iterator<String> iterator = orignalHash.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			retList.add(orignalHash.get(key));
		}
		Collections.sort(retList);
		return retList;
	}

	public static void main(String[] args) {
		String xx = "/afunms/resource/image/topo/webservice/3.gif";
		String images = xx.substring(17, xx.lastIndexOf("/") + 1) + "alarm.gif";
		System.out.println(xx.substring(17));
		System.out.println(images);
	}

	public Speak speak;

	public void addApp(String xmlName, Node node, String id1, String id2) {
		XmlOperator xmlOperator = new XmlOperator();
		xmlOperator.setFile(xmlName);
		xmlOperator.init4updateXml();
		String eleImage = null;
		int index = 1;
		boolean bool = false;
		eleImage = NodeHelper.getTopoImage(node.getCategory());
		if (!xmlOperator.isNodeExist(id2)) {
			xmlOperator.addNode(id2, node.getCategory(), eleImage, node.getIpAddress(), node.getAlias(), String.valueOf(index * 30), "15");
			bool = true;
		}
		xmlOperator.writeXml();
		// 保存示意链路
		ManageXmlOperator mxmlOpr = new ManageXmlOperator();
		mxmlOpr.setFile(xmlName);
		mxmlOpr.init4updateXml();
		int lineId = mxmlOpr.findMaxDemoLineId();
		if (bool) {
			Hashtable xyHash = mxmlOpr.getAllXY();
			String xy = "";
			if (xyHash != null && xyHash.containsKey(id1)) {
				xy = (String) xyHash.get(id1);
			}
			HintLine hintLine = new HintLine();
			hintLine.setChildId(id1);
			hintLine.setChildXy("30,50");
			hintLine.setFatherId(id1.substring(3));
			hintLine.setFatherXy(xy);
			hintLine.setXmlfile(xmlName);
			hintLine.setLineName("autoline");
			hintLine.setWidth(1);
			hintLine.setLineId("hl" + lineId);
			LineDao lineDao = new LineDao();
			if (lineDao.save(hintLine)) {
				lineDao = new LineDao();
				HintLine vo = lineDao.findById("hl" + lineId, xmlName);
				mxmlOpr.addLine(vo.getId(), "hl" + lineId, id1, id2, "1");
				mxmlOpr.writeXml();
			}
		}
	}

	// 将主机关联的应用、中间件添加到拓扑图上
	private String addApplications() {

		String xmlName = getParaValue("xml");
		String nodeid = getParaValue("node");
		String ip = getParaValue("ip");

		Node node_db = PollingEngine.getInstance().getDbByIP(ip);
		if (node_db != null) {
			addApp(xmlName, node_db, nodeid, "dbs" + node_db.getId());
		}
		Node node_cics = PollingEngine.getInstance().getCicsByIP(ip);
		if (node_cics != null) {
			addApp(xmlName, node_cics, nodeid, "cic" + node_cics.getId());
		}
		Node node_domino = PollingEngine.getInstance().getDominoByIP(ip);
		if (node_domino != null) {
			addApp(xmlName, node_domino, nodeid, "dom" + node_domino.getId());
		}
		Node node_ftp = PollingEngine.getInstance().getFtpByIP(ip);
		if (node_ftp != null) {
			addApp(xmlName, node_ftp, nodeid, "ftp" + node_ftp.getId());
		}
		Node node_tftp = PollingEngine.getInstance().getTftpByIP(ip);
		if (node_tftp != null) {
			addApp(xmlName, node_tftp, nodeid, "tft" + node_tftp.getId());
		}
		Node node_dhcp = PollingEngine.getInstance().getDHCPByIP(ip);
		if (node_dhcp != null) {
			addApp(xmlName, node_dhcp, nodeid, "dhc" + node_dhcp.getId());
		}
		Node node_iis = PollingEngine.getInstance().getIisByIP(ip);
		if (node_iis != null) {
			addApp(xmlName, node_iis, nodeid, "iis" + node_iis.getId());
		}
		Node node_mail = PollingEngine.getInstance().getMailByIP(ip);
		if (node_mail != null) {
			addApp(xmlName, node_mail, nodeid, "mai" + node_mail.getId());
		}
		Node node_mq = PollingEngine.getInstance().getMqByIP(ip);
		if (node_mq != null) {
			addApp(xmlName, node_mq, nodeid, "mqs" + node_mq.getId());
		}
		Node node_tomcat = PollingEngine.getInstance().getTomcatByIP(ip);
		if (node_tomcat != null) {
			addApp(xmlName, node_tomcat, nodeid, "tom" + node_tomcat.getId());
		}
		Node node_was = PollingEngine.getInstance().getWasByIP(ip);
		if (node_was != null) {
			addApp(xmlName, node_was, nodeid, "was" + node_was.getId());
		}
		Node node_web = PollingEngine.getInstance().getWebByIP(ip);
		if (node_web != null) {
			addApp(xmlName, node_web, nodeid, "wes" + node_web.getId());
		}
		Node node_weblogic = PollingEngine.getInstance().getWeblogicByIP(ip);
		if (node_weblogic != null) {
			addApp(xmlName, node_weblogic, nodeid, "web" + node_weblogic.getId());
		}
		request.setAttribute("fresh", "fresh");
		return "/topology/network/save.jsp";

	}

	// 从设备树向根图添加实体设备
	public String addEquipToMap(String xmlName, String node, String category, HttpSession httpSession) {
		ManageXmlOperator mxmlOpr = new ManageXmlOperator();
		mxmlOpr.setFile(xmlName);
		mxmlOpr.init4editNodes();
		boolean exist = mxmlOpr.isIdExist(node);
		if (exist) {
			return "success";
		}
		mxmlOpr.addNode(node, 1, category);
		mxmlOpr.writeXml();
		if (xmlName.indexOf("businessmap") != -1) {// 如果是业务视图
			NodeDependDao nodeDependDao = new NodeDependDao();
			try {
				if (!nodeDependDao.isNodeExist(node, xmlName)) {// 判断节点是否存在于nms_node_depend表中，不存在则添加
					Node fnode = PollingEngine.getInstance().getNodeByCategory(category, Integer.parseInt(node.substring(3)));
					NodeDepend nodeDepend = new NodeDepend();
					nodeDepend.setAlias(fnode.getAlias());
					nodeDepend.setLocation("10px,10px");
					nodeDepend.setNodeId(node);
					nodeDepend.setXmlfile(xmlName);
					NodeDependDao nodeDependDaos = new NodeDependDao();
					nodeDependDaos.save(nodeDepend);
				}
			} catch (NumberFormatException e1) {
				e1.printStackTrace();
			} finally {
				nodeDependDao.close();
			}

			session = WebContextFactory.get().getSession();
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			ManageXmlDao mXmlDao = new ManageXmlDao();
			List xmlList = new ArrayList();
			try {
				xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mXmlDao.close();
			}
			try {
				ChartXml chartxml;
				chartxml = new ChartXml("tree");
				chartxml.addViewTree(xmlList);
			} catch (Exception e) {
				e.printStackTrace();
			}

			ManageXmlDao subMapDao = new ManageXmlDao();
			ManageXml manageXml = (ManageXml) subMapDao.findByXml(xmlName);
			if (manageXml != null) {
				NodeDependDao nodeDepenDao = new NodeDependDao();
				try {
					List list = nodeDepenDao.findByXml(xmlName);
					ChartXml chartxml;
					chartxml = new ChartXml("NetworkMonitor", "/" + xmlName.replace("jsp", "xml"));
					chartxml.addBussinessXML(manageXml.getTopoName(), list);
					ChartXml chartxmlList;
					chartxmlList = new ChartXml("NetworkMonitor", "/" + xmlName.replace("jsp", "xml").replace("businessmap", "list"));
					chartxmlList.addListXML(manageXml.getTopoName(), list);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					nodeDepenDao.close();
				}
			}
		}
		return "success";
	}

	// 保存示意图元
	public String addHintMeta(String[] returnValue) {
		String result = "error";
		String str = "";
		String equipName = "";
		int nodeId = 0;
		if (returnValue.length == 4) {
			ManageXmlOperator mXmlOpr = new ManageXmlOperator();
			mXmlOpr.setFile(returnValue[2]);
			mXmlOpr.init4editNodes();
			str = returnValue[3];
			equipName = returnValue[0];
			nodeId = mXmlOpr.findMaxNodeId();
			HintNodeDao hintNodeDao = new HintNodeDao();
			HintNode hintNode = new HintNode();
			hintNode.setName(equipName);
			hintNode.setNodeId("hin" + String.valueOf(nodeId));
			hintNode.setXmlfile(returnValue[2]);
			hintNode.setImage(returnValue[1]);
			hintNode.setAlias(equipName);
			if (hintNodeDao.save(hintNode) && returnValue[1] != null && !"".equals(returnValue[1])) {
				mXmlOpr.addDemoNode("hin" + String.valueOf(nodeId), str, returnValue[1].substring(17), equipName, String.valueOf(1 * 30), "15");
				result = "hin" + String.valueOf(nodeId);
				if(ShareData.getAllhintlinks()!=null){
			    	ShareData.getAllhintlinks().put(hintNode.getNodeId() + ":" + hintNode.getXmlfile(), hintNode);
			    }else{
			    	Hashtable hinthash = new Hashtable();
			    	hinthash.put(hintNode.getNodeId() + ":" + hintNode.getXmlfile(), hintNode);
			    	ShareData.setAllhintlinks(hinthash);
			    }
			}
			mXmlOpr.writeXml();
		}
		return result;
	}

	// 在子图上创建示意链路
	private String addLines() {
		String xml = getParaValue("xml");

		ManageXmlOperator mxmlOpr = new ManageXmlOperator();
		mxmlOpr.setFile(xml);
		mxmlOpr.init4updateXml();
		String id1 = getParaValue("id1");
		String id2 = getParaValue("id2");
		int lineId = mxmlOpr.findMaxDemoLineId();
		mxmlOpr.addLine("hl" + lineId, id1, id2);
		mxmlOpr.writeXml();

		return "/topology/submap/change.jsp?submapview=" + xml;
	}

	// 保存实体链路
	public String addLink(String direction1, String linkName, String maxSpeed, String maxPer, String xml, String start_id, String start_index, String end_id, String end_index,
			String linetext, String interf) {

		String returns = "error";
		String startIndex = "";
		String endIndex = "";
		String start_Id = "";
		String end_Id = "";

		if (direction1 != null && direction1.equals("1")) {// 上行设备
			startIndex = start_index;
			endIndex = end_index;
			start_Id = start_id;
			end_Id = end_id;
		} else {// 下行设备
			startIndex = end_index;
			endIndex = start_index;
			start_Id = end_id;
			end_Id = start_id;
		}
		if (!"".equals(start_Id) && !"".equals(end_Id)) {
			int startId = Integer.parseInt(start_Id.substring(3));
			int endId = Integer.parseInt(end_Id.substring(3));

			LinkDao dao = new LinkDao();
			try {
				String exist = dao.linkExists(startId, startIndex, endId, endIndex);
				XmlOperator xopr = new XmlOperator();
				xopr.setFile(xml);
				xopr.init4updateXml();
				if (exist.split(":").length > 1 && exist.split(":")[1].equals("1")) {
					if (xopr.isLinkExist(exist.split(":")[0])) {
						setErrorCode(ErrorMessage.LINK_EXIST);
						dao.close();
						return "error1";
					} else if (xopr.isAssLinkExist(exist.split(":")[0])) {
						setErrorCode(ErrorMessage.LINK_EXIST);
						dao.close();
						return "error1";
					} else {
						if (exist.split(":")[2].equals("0")) {
							if (xopr.isNodeExist(start_Id) && xopr.isNodeExist(end_Id)) {
								xopr.addLine(linkName, String.valueOf(exist.split(":")[0]), start_Id, end_Id);
							}
							xopr.writeXml();
						} else if (exist.split(":")[2].equals("1")) {
							if (xopr.isNodeExist(start_Id) && xopr.isNodeExist(end_Id)) {
								xopr.addAssistantLine(linkName, String.valueOf(exist.split(":")[0]), start_Id, end_Id);
							}
							xopr.writeXml();
						}
						return exist.split(":")[0] + ":" + exist.split(":")[2];
					}
				}
				if (exist.split(":").length > 3 && exist.split(":")[1].equals("2")) {
					if (exist.split(":")[0].equals("2")) {
						if ((xopr.isLinkExist(exist.split(":")[2]) && xopr.isAssLinkExist(exist.split(":")[4]))
								|| (xopr.isLinkExist(exist.split(":")[4]) && xopr.isAssLinkExist(exist.split(":")[2]))) {
							setErrorCode(ErrorMessage.DOUBLE_LINKS);
							dao.close();
							return "error2";
						} else {
							if (exist.split(":")[3].equals("0") && !xopr.isLinkExist(exist.split(":")[2])) {
								if (xopr.isNodeExist(start_Id) && xopr.isNodeExist(end_Id)) {
									xopr.addLine(linkName, String.valueOf(exist.split(":")[2]), start_Id, end_Id);
								}
								xopr.writeXml();
								return exist.split(":")[2] + ":" + exist.split(":")[3];
							} else if (exist.split(":")[3].equals("1") && !xopr.isAssLinkExist(exist.split(":")[2])) {
								if (xopr.isNodeExist(start_Id) && xopr.isNodeExist(end_Id)) {
									xopr.addAssistantLine(linkName, String.valueOf(exist.split(":")[2]), start_Id, end_Id);
								}
								xopr.writeXml();
								return exist.split(":")[2] + ":" + exist.split(":")[3];
							}
							if (exist.split(":")[5].equals("0") && !xopr.isLinkExist(exist.split(":")[4])) {
								if (xopr.isNodeExist(start_Id) && xopr.isNodeExist(end_Id)) {
									xopr.addLine(linkName, String.valueOf(exist.split(":")[4]), start_Id, end_Id);
								}
								xopr.writeXml();
								return exist.split(":")[4] + ":" + exist.split(":")[5];
							} else if (exist.split(":")[5].equals("1") && !xopr.isAssLinkExist(exist.split(":")[4])) {
								if (xopr.isNodeExist(start_Id) && xopr.isNodeExist(end_Id)) {
									xopr.addAssistantLine(linkName, String.valueOf(exist.split(":")[4]), start_Id, end_Id);
								}
								xopr.writeXml();
								return exist.split(":")[4] + ":" + exist.split(":")[5];
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			}
			Host startHost = (Host) PollingEngine.getInstance().getNodeByID(startId);
			IfEntity if1 = startHost.getIfEntityByIndex(startIndex);
			Host endHost = (Host) PollingEngine.getInstance().getNodeByID(endId);
			IfEntity if2 = endHost.getIfEntityByIndex(endIndex);

			Link link = new Link();
			link.setLinkName(linkName);
			link.setMaxSpeed(maxSpeed);
			link.setMaxPer(maxPer);
			link.setStartId(startId);
			link.setEndId(endId);
			link.setStartIndex(startIndex);
			link.setEndIndex(endIndex);
			link.setStartIp(if1.getIpAddress());
			link.setEndIp(if2.getIpAddress());
			link.setStartDescr(if1.getDescr());
			link.setEndDescr(if2.getDescr());
			link.setType(Integer.parseInt(linetext));
			link.setShowinterf(Integer.parseInt(interf));
			Link newLink = null;
			try {
				newLink = dao.save(link);
			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			} finally {
				dao.close();
			}
			if (newLink != null) {
				// 更新所有拓扑图xml
				try {
					XmlOperator xopr = new XmlOperator();
					xopr.setFile(xml);
					xopr.init4updateXml();
					if (xopr.isNodeExist(start_Id) && xopr.isNodeExist(end_Id)) {
						if (newLink.getAssistant() == 0) {
							xopr.addLine(linkName, String.valueOf(newLink.getId()), start_Id, end_Id);
						} else {
							xopr.addAssistantLine(linkName, String.valueOf(newLink.getId()), start_Id, end_Id);
						}
					}
					xopr.writeXml();
				} catch (Exception e) {
					e.printStackTrace();
					return "error";
				}

				// 链路信息实时更新
				LinkRoad lr = new LinkRoad();
				lr.setId(newLink.getId());
				lr.setLinkName(linkName);
				lr.setMaxSpeed(maxSpeed);
				lr.setMaxPer(maxPer);
				lr.setStartId(startId);
				if ("".equals(if1.getIpAddress())) {
					lr.setStartIp(startHost.getIpAddress());
				} else {
					lr.setStartIp(if1.getIpAddress());
				}
				lr.setStartIndex(startIndex);
				lr.setStartDescr(if1.getDescr());

				if ("".equals(if2.getIpAddress())) {
					lr.setEndIp(endHost.getIpAddress());
				} else {
					lr.setEndIp(if2.getIpAddress());
				}
				lr.setEndId(endId);
				lr.setEndIndex(endIndex);
				lr.setEndDescr(if2.getDescr());
				lr.setAssistant(newLink.getAssistant());
				lr.setType(newLink.getType());
				lr.setShowinterf(newLink.getShowinterf());
				PollingEngine.getInstance().getLinkList().add(lr);
				returns = newLink.getId() + ":" + newLink.getAssistant();
			}
		}
		return returns;
	}

	private String backup() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Calendar tempCal = Calendar.getInstance();
		Date cc = tempCal.getTime();
		String time = sdf.format(cc);
		String[] ids = getParaArrayValue("radio");
		ManageXmlDao dao = null;
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				try {
					dao = new ManageXmlDao();
					ManageXml vo = (ManageXml) dao.findByID(ids[i]);
					copyFile(vo.getXmlName(), time + "_" + vo.getXmlName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	// 取消关联节点
	private String cancelRelation() {
		String fileName = getParaValue("xml");
		RelationDao dao = new RelationDao();
		dao.deleteByNode(getParaValue("nodeId"), fileName);
		ManageXmlOperator mXmlOpr = new ManageXmlOperator();
		mXmlOpr.setFile(fileName);
		mXmlOpr.init4updateXml();
		if (mXmlOpr.isNodeExist(getParaValue("nodeId"))) {
			mXmlOpr.updateNode(getParaValue("nodeId"), "relationMap", "");
		}
		mXmlOpr.writeXml();
		return null;

	}

	// 告警确认
	public String confirmAlarm(String xmlName, String nodeid, String category) {
		String returns = "error";
		String id = nodeid.substring(3);

		if (xmlName.indexOf("businessmap") != -1) {
			// yangjun
			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			ManageXmlDao mXmlDao = new ManageXmlDao();
			List xmlList = new ArrayList();
			try {
				xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mXmlDao.close();
			}
			try {
				ChartXml chartxml;
				chartxml = new ChartXml("tree");
				chartxml.addViewTree(xmlList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			Host host = null;
			Node node = null;
			if ("net".equals(nodeid.subSequence(0, 3))) {
				Hashtable checkEventHashtable = ShareData.getCheckEventHash();
				// 网络设备和服务器
				String typestr = "";
				try {
					host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(id));
					if (host.getCategory() < 4 || host.getCategory() == 6 || host.getCategory() == 7 || host.getCategory() == 8) {
						// 网络设备
						typestr = "net";
					} else if (host.getCategory() == 4) {
						// 服务器
						typestr = "host";
					}
					NodeDTO _node = null;
					NodeUtil nodeUtil = new NodeUtil();
					_node = nodeUtil.conversionToNodeDTO(host);
					String name = _node.getNodeid() + ":" + _node.getType() + ":" + _node.getSubtype() + ":";
					if (checkEventHashtable != null && checkEventHashtable.size() > 0) {
						for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
							String key = (String) it.next();
							if (key.startsWith(name)) {
								checkEventHashtable.remove(key);
							}
						}
						CheckEventDao checkeventdao = new CheckEventDao();
						try {
							checkeventdao.deleteByNodeType(id, typestr);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							checkeventdao.close();
						}
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
					String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
					EventListDao eventListDao = new EventListDao();
					try {
						List list = eventListDao.getEventlist(startTime, endTime, "0", "0", host.getBid(), Integer.parseInt(id));
						if (list != null && list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								EventList vo = (EventList) list.get(i);
								String time = null;// 告警持续时间，默认分钟为单位
								long timeLong = 0;
								Calendar tempCal = (Calendar) vo.getRecordtime();
								Date cc = tempCal.getTime();
								String collecttime = sdf.format(cc);
								Date firstAlarmDate = null;
								try {
									firstAlarmDate = sdf.parse(collecttime);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								if (firstAlarmDate != null) {
									timeLong = new Date().getTime() - firstAlarmDate.getTime();
								}
								if (timeLong < 1000 * 60) {// 小于1分钟,秒
									time = timeLong / 1000 + "秒";
								} else {// 小于1小时,分
									time = timeLong / (60 * 1000) + "分";
								}
								eventListDao.update(endTime, "0", vo.getContent() + " (告警已恢复，告警持续时间" + time + ")", vo.getId() + "");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						eventListDao.close();
					}
					host.getAlarmMessage().clear();
					host.setAlarm(false);
					host.setStatus(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if ("dbs".equals(nodeid.subSequence(0, 3))) {
				Hashtable checkEventHashtable = ShareData.getCheckEventHash();
				// 网络设备和服务器
				String typestr = "db";
				try {
					node = (Node) PollingEngine.getInstance().getDbByID(Integer.parseInt(id));
					NodeDTO _node = null;
					NodeUtil nodeUtil = new NodeUtil();
					_node = nodeUtil.conversionToNodeDTO(node);
					String name = _node.getNodeid() + ":" + _node.getType() + ":" + _node.getSubtype() + ":";
					if (checkEventHashtable != null && checkEventHashtable.size() > 0) {
						for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
							String key = (String) it.next();
							if (key.startsWith(name)) {
								checkEventHashtable.remove(key);
							}
						}
						CheckEventDao checkeventdao = new CheckEventDao();
						try {
							checkeventdao.deleteByNodeType(id, typestr);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							checkeventdao.close();
						}
					}
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + " 00:00:00";
					String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
					EventListDao eventListDao = new EventListDao();
					try {
						List list = eventListDao.getEventlist(startTime, endTime, "0", "0", node.getBid(), Integer.parseInt(id));
						if (list != null && list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								EventList vo = (EventList) list.get(i);
								String time = null;// 告警持续时间，默认分钟为单位
								long timeLong = 0;
								Calendar tempCal = (Calendar) vo.getRecordtime();
								Date cc = tempCal.getTime();
								String collecttime = sdf.format(cc);
								Date firstAlarmDate = null;
								try {
									firstAlarmDate = sdf.parse(collecttime);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								if (firstAlarmDate != null) {
									timeLong = new Date().getTime() - firstAlarmDate.getTime();
								}
								if (timeLong < 1000 * 60) {// 小于1分钟,秒
									time = timeLong / 1000 + "秒";
								} else {// 小于1小时,分
									time = timeLong / (60 * 1000) + "分";
								}
								eventListDao.update(endTime, "0", vo.getContent() + " (告警已恢复，告警持续时间" + time + ")", vo.getId() + "");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						eventListDao.close();
					}
					node.getAlarmMessage().clear();
					node.setAlarm(false);
					node.setStatus(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String imgPath = "";
			String imgstr = "";
			if ("net".equals(nodeid.subSequence(0, 3))) {
				if (host != null) {
					ManageXmlOperator mxmlOpr = new ManageXmlOperator();
					mxmlOpr.setFile(xmlName);
					mxmlOpr.init4editNodes();

					NodeEquipDao nodeEquipDao = new NodeEquipDao();
					NodeEquip Vo = null;
					try {
						Vo = (NodeEquip) nodeEquipDao.findByNodeAndXml(nodeid, xmlName);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						nodeEquipDao.close();
					}
					if (Vo != null) {
						EquipImageDao equipImageDao = new EquipImageDao();
						EquipImage equipImage = null;
						try {
							equipImage = (EquipImage) equipImageDao.findImageById(Vo.getEquipId());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							equipImageDao.close();
						}
						if (equipImage != null) {
							imgPath = equipImage.getPath();
						}
					}

					if (imgPath != null && imgPath.trim().length() > 0) {
						if (mxmlOpr.isIdExist(nodeid)) {
							mxmlOpr.updateNode(nodeid, "img", host.getCategory() == 4 ? NodeHelper.getServerTopoImage(host.getSysOid()) : imgPath.substring(17));
						}
					} else {
						if (mxmlOpr.isIdExist(nodeid)) {
							mxmlOpr.updateNode(nodeid, "img", host.getCategory() == 4 ? NodeHelper.getServerTopoImage(host.getSysOid()) : NodeHelper.getTopoImage(host
									.getCategory()));
						}
					}
					mxmlOpr.writeXml();
				}
				if (imgPath != null && imgPath.trim().length() > 0) {
					imgstr = host.getCategory() == 4 ? NodeHelper.getServerTopoImage(host.getSysOid()) : imgPath.substring(17);
				} else {
					imgstr = host.getCategory() == 4 ? NodeHelper.getServerTopoImage(host.getSysOid()) : NodeHelper.getTopoImage(host.getCategory());
				}
			} else if ("dbs".equals(nodeid.subSequence(0, 3))) {
				if (node != null) {
					ManageXmlOperator mxmlOpr = new ManageXmlOperator();
					mxmlOpr.setFile(xmlName);
					mxmlOpr.init4editNodes();

					NodeEquipDao nodeEquipDao = new NodeEquipDao();
					NodeEquip Vo = null;
					try {
						Vo = (NodeEquip) nodeEquipDao.findByNodeAndXml(nodeid, xmlName);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						nodeEquipDao.close();
					}
					if (Vo != null) {
						EquipImageDao equipImageDao = new EquipImageDao();
						EquipImage equipImage = null;
						try {
							equipImage = (EquipImage) equipImageDao.findImageById(Vo.getEquipId());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							equipImageDao.close();
						}
						if (equipImage != null) {
							imgPath = equipImage.getPath();
						}
					}

					if (imgPath != null && imgPath.trim().length() > 0) {
						if (mxmlOpr.isIdExist(nodeid)) {
							mxmlOpr.updateNode(nodeid, "img", imgPath.substring(17));
						}
					} else {
						if (mxmlOpr.isIdExist(nodeid)) {
							mxmlOpr.updateNode(nodeid, "img", NodeHelper.getTopoImage(node.getCategory()));
						}
					}

					mxmlOpr.writeXml();
				}

				imgstr = NodeHelper.getTopoImage(node.getCategory());
			}

			returns = nodeid + ":" + imgstr;
		} catch (Exception e) {
			returns = "error";
			e.printStackTrace();
		}
		return returns;
	}

	public void copyFile(String xml, String xml_bak) {
		try {
			String cmd = "cmd   /c   copy   " + ResourceCenter.getInstance().getSysPath() + "resource\\xml\\" + xml + " " + ResourceCenter.getInstance().getSysPath()
					+ "resource\\xml\\" + xml_bak;
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 删除实体设备
	private String deleteEquipFromSubMap() {

		String nodeid = getParaValue("node");
		String category = getParaValue("category");
		String id = nodeid.substring(3);
		String xmlName = getParaValue("xml");
		if (nodeid.indexOf("dbs") != -1) {
			category = "dbs";
		}
		PollingEngine.getInstance().deleteNodeByCategory(category, Integer.parseInt(id));// 按照节点类型和id更新内存

		// 更新数据库
		TreeNodeDao treeNodeDao = new TreeNodeDao();
		TreeNode tvo = (TreeNode) treeNodeDao.findByName(category);
		if (tvo != null && tvo.getTableName() != null && !"".equals(tvo.getTableName())) {// 根据表名
			if ("net".equals(nodeid.subSequence(0, 3))) {
				CommonDao dao = new CommonDao(tvo.getTableName());
				HostNode host = (HostNode) dao.findByID(id);
				dao.close();
				String ip = host.getIpAddress();
				String allipstr = SysUtil.doip(ip);

				CreateTableManager ctable = new CreateTableManager();
				try {
					if (host.getCategory() < 4 || host.getCategory() == 6 || host.getCategory() == 7 || host.getCategory() == 8) {
						// 先删除网络设备表
						// 连通率
						try {
							ctable.deleteTable("ping", allipstr, "ping");// Ping
							ctable.deleteTable("pinghour", allipstr, "pinghour");// Ping
							ctable.deleteTable("pingday", allipstr, "pingday");// Ping
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 内存
						try {
							ctable.deleteTable("memory", allipstr, "mem");// 内存
							ctable.deleteTable("memoryhour", allipstr, "memhour");// 内存
							ctable.deleteTable("memoryday", allipstr, "memday");// 内存
						} catch (Exception e) {
							e.printStackTrace();
						}

						ctable.deleteTable("flash", allipstr, "flash");// 闪存
						ctable.deleteTable("flashhour", allipstr, "flashhour");// 闪存
						ctable.deleteTable("flashday", allipstr, "flashday");// 闪存

						ctable.deleteTable("buffer", allipstr, "buffer");// 缓存
						ctable.deleteTable("bufferhour", allipstr, "bufferhour");// 缓存
						ctable.deleteTable("bufferday", allipstr, "bufferday");// 缓存

						ctable.deleteTable("fan", allipstr, "fan");// 风扇
						ctable.deleteTable("fanhour", allipstr, "fanhour");// 风扇
						ctable.deleteTable("fanday", allipstr, "fanday");// 风扇

						ctable.deleteTable("power", allipstr, "power");// 电源
						ctable.deleteTable("powerhour", allipstr, "powerhour");// 电源
						ctable.deleteTable("powerday", allipstr, "powerday");// 电源

						ctable.deleteTable("vol", allipstr, "vol");// 电压
						ctable.deleteTable("volhour", allipstr, "volhour");// 电压
						ctable.deleteTable("volday", allipstr, "volday");// 电压

						// CPU
						try {
							ctable.deleteTable("cpu", allipstr, "cpu");// CPU
							ctable.deleteTable("cpuhour", allipstr, "cpuhour");// CPU
							ctable.deleteTable("cpuday", allipstr, "cpuday");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 带宽利用率
						try {
							ctable.deleteTable("utilhdxperc", allipstr, "hdperc");
							ctable.deleteTable("hdxperchour", allipstr, "hdperchour");
							ctable.deleteTable("hdxpercday", allipstr, "hdpercday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 端口状态
						try {
							ctable.deleteTable("portstatus", allipstr, "port");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 流速
						try {
							ctable.deleteTable("utilhdx", allipstr, "hdx");
							ctable.deleteTable("utilhdxhour", allipstr, "hdxhour");
							ctable.deleteTable("utilhdxday", allipstr, "hdxday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 综合流速
						try {
							ctable.deleteTable("allutilhdx", allipstr, "allhdx");
							ctable.deleteTable("autilhdxh", allipstr, "ahdxh");
							ctable.deleteTable("autilhdxd", allipstr, "ahdxd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 丢包率
						try {
							ctable.deleteTable("discardsperc", allipstr, "dcardperc");
							ctable.deleteTable("dcarperh", allipstr, "dcarperh");
							ctable.deleteTable("dcarperd", allipstr, "dcarperd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 错误率
						try {
							ctable.deleteTable("errorsperc", allipstr, "errperc");
							ctable.deleteTable("errperch", allipstr, "errperch");
							ctable.deleteTable("errpercd", allipstr, "errpercd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 数据包
						try {
							ctable.deleteTable("packs", allipstr, "packs");
							ctable.deleteTable("packshour", allipstr, "packshour");
							ctable.deleteTable("packsday", allipstr, "packsday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 入口数据包
						try {
							ctable.deleteTable("inpacks", allipstr, "inpacks");
							ctable.deleteTable("ipacksh", allipstr, "ipacksh");
							ctable.deleteTable("ipackd", allipstr, "ipackd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 出口数据包
						try {
							ctable.deleteTable("outpacks", allipstr, "outpacks");
							ctable.deleteTable("opackh", allipstr, "opackh");
							ctable.deleteTable("opacksd", allipstr, "opacksd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 温度
						try {
							ctable.deleteTable("temper", allipstr, "temper");
							ctable.deleteTable("temperh", allipstr, "temperh");
							ctable.deleteTable("temperd", allipstr, "temperd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						DiscoverCompleteDao dcDao = new DiscoverCompleteDao();
						try {
							dcDao.deleteMonitor(host.getId(), host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dcDao.close();
						}

						EventListDao eventdao = new EventListDao();
						try {
							// 同时删除事件表里的相关数据
							eventdao.delete(host.getId(), "network");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							eventdao.close();
						}

						PortconfigDao portconfigdao = new PortconfigDao();
						try {
							// 同时删除端口配置表里的相关数据
							portconfigdao.deleteByIpaddress(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portconfigdao.close();
						}

						// 删除nms_ipmacchange表里的对应的数据
						IpMacChangeDao macchangebasedao = new IpMacChangeDao();
						try {
							macchangebasedao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macchangebasedao.close();
						}

						// 删除网络设备配置文件表里的对应的数据
						NetNodeCfgFileDao configdao = new NetNodeCfgFileDao();
						try {
							configdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							configdao.close();
						}

						// 删除网络设备SYSLOG接收表里的对应的数据
						NetSyslogDao syslogdao = new NetSyslogDao();
						try {
							syslogdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							syslogdao.close();
						}

						// 删除网络设备端口扫描表里的对应的数据
						PortScanDao portscandao = new PortScanDao();
						try {
							portscandao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							portscandao.close();
						}

						// 删除网络设备面板图表里的对应的数据
						IpaddressPanelDao addresspaneldao = new IpaddressPanelDao();
						try {
							addresspaneldao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							addresspaneldao.close();
						}

						// 删除网络设备接口表里的对应的数据
						HostInterfaceDao interfacedao = new HostInterfaceDao();
						try {
							interfacedao.deleteByHostId(host.getId() + "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							interfacedao.close();
						}

						// 删除网络设备IP别名表里的对应的数据
						IpAliasDao ipaliasdao = new IpAliasDao();
						try {
							ipaliasdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ipaliasdao.close();
						}

						// 删除网络设备手工配置的链路表里的对应的数据
						RepairLinkDao repairdao = new RepairLinkDao();
						try {
							repairdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							repairdao.close();
						}

						// 删除网络设备IPMAC表里的对应的数据
						IpMacDao ipmacdao = new IpMacDao();
						try {
							ipmacdao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							ipmacdao.close();
						}

						// 删除该设备的采集指标
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "net", "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							gatherdao.close();
						}

						// 删除网络设备指标采集表里的对应的数据
						AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
						try {
							indicatdao.deleteByNodeId(host.getId() + "", "net");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							indicatdao.close();
						}

						// 删除IP-MAC-BASE表里的对应的数据
						IpMacBaseDao macbasedao = new IpMacBaseDao();
						try {
							macbasedao.deleteByHostIp(host.getIpAddress());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							macbasedao.close();
						}
					} else if (host.getCategory() == 4) {
						// 删除主机服务器
						try {
							ctable.deleteTable("pro", allipstr, "pro");// 进程
							ctable.deleteTable("prohour", allipstr, "prohour");// 进程小时
							ctable.deleteTable("proday", allipstr, "proday");// 进程天
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("log", allipstr, "log");// 进程天
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("memory", allipstr, "mem");// 内存
							ctable.deleteTable("memoryhour", allipstr, "memhour");// 内存
							ctable.deleteTable("memoryday", allipstr, "memday");// 内存
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("cpu", allipstr, "cpu");// CPU
							ctable.deleteTable("cpuhour", allipstr, "cpuhour");// CPU
							ctable.deleteTable("cpuday", allipstr, "cpuday");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("cpudtl", allipstr, "cpudtl");// CPU
							ctable.deleteTable("cpudtlhour", allipstr, "cpudtlhour");// CPU
							ctable.deleteTable("cpudtlday", allipstr, "cpudtlday");// CPU
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("disk", allipstr, "disk");// disk
							ctable.deleteTable("diskhour", allipstr, "diskhour");// disk
							ctable.deleteTable("diskday", allipstr, "diskday");// disk
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("diskincre", allipstr, "diskincre");// 磁盘增长
							ctable.deleteTable("diskinch", allipstr, "diskincrehour");// 磁盘增长
							ctable.deleteTable("diskincd", allipstr, "diskincd");// 磁盘增长
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("ping", allipstr, "ping");
							ctable.deleteTable("pinghour", allipstr, "pinghour");
							ctable.deleteTable("pingday", allipstr, "pingday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("utilhdxperc", allipstr, "hdperc");
							ctable.deleteTable("hdxperchour", allipstr, "hdperchour");
							ctable.deleteTable("hdxpercday", allipstr, "hdpercday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("utilhdx", allipstr, "hdx");
							ctable.deleteTable("utilhdxhour", allipstr, "hdxhour");
							ctable.deleteTable("utilhdxday", allipstr, "hdxday");
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							ctable.deleteTable("software", allipstr, "software");
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 温度
						try {
							ctable.deleteTable("temper", allipstr, "temper");
							ctable.deleteTable("temperh", allipstr, "temperh");
							ctable.deleteTable("temperd", allipstr, "temperd");
						} catch (Exception e) {
							e.printStackTrace();
						}

						ctable.deleteTable("allutilhdx", allipstr, "allhdx");
						ctable.deleteTable("autilhdxh", allipstr, "ahdxh");
						ctable.deleteTable("autilhdxd", allipstr, "ahdxd");

						ctable.deleteTable("discardsperc", allipstr, "dcardperc");
						ctable.deleteTable("dcarperh", allipstr, "dcarperh");
						ctable.deleteTable("dcarperd", allipstr, "dcarperd");

						ctable.deleteTable("errorsperc", allipstr, "errperc");
						ctable.deleteTable("errperch", allipstr, "errperch");
						ctable.deleteTable("errpercd", allipstr, "errpercd");

						ctable.deleteTable("packs", allipstr, "packs");
						ctable.deleteTable("packshour", allipstr, "packshour");
						ctable.deleteTable("packsday", allipstr, "packsday");

						ctable.deleteTable("inpacks", allipstr, "inpacks");
						ctable.deleteTable("ipacksh", allipstr, "ipacksh");
						ctable.deleteTable("ipackd", allipstr, "ipackd");

						ctable.deleteTable("outpacks", allipstr, "outpacks");
						ctable.deleteTable("opackh", allipstr, "opackh");
						ctable.deleteTable("opacksd", allipstr, "opacksd");

						if (host.getOstype() == 15) {
							// AS400
							ctable.deleteTable("systemasp", allipstr, "systemasp");
							ctable.deleteTable("systemasphour", allipstr, "systemasphour");
							ctable.deleteTable("systemaspday", allipstr, "systemaspday");

							ctable.deleteTable("dbcapability", allipstr, "dbcapability");
							ctable.deleteTable("dbcaphour", allipstr, "dbcaphour");
							ctable.deleteTable("dbcapday", allipstr, "dbcapday");
						}

						if (host.getOstype() == 6) {
							// AIX系统,增加页使用率
							ctable.deleteTable("page", allipstr, "page");// page
							ctable.deleteTable("pagehour", allipstr, "pagehour");// page
							ctable.deleteTable("pageday", allipstr, "pageday");// page
						}

						// 删除该设备的采集指标
						NodeGatherIndicatorsDao gatherdao = new NodeGatherIndicatorsDao();
						try {
							gatherdao.deleteByNodeIdAndTypeAndSubtype(host.getId() + "", "host", "");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							gatherdao.close();
						}

						// 删除服务器指标采集表里的对应的数据
						AlarmIndicatorsNodeDao indicatdao = new AlarmIndicatorsNodeDao();
						try {
							indicatdao.deleteByNodeId(host.getId() + "", "host");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							indicatdao.close();
						}

						EventListDao eventdao = new EventListDao();
						try {
							// 同时删除事件表里的相关数据
							eventdao.delete(host.getId(), "host");
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							eventdao.close();
						}

						// 删除diskconfig
						String[] otherTempData = new String[] { "nms_diskconfig" };
						String[] ipStrs = new String[] { host.getIpAddress() };
						ctable.clearTablesData(otherTempData, "ipaddress", ipStrs);
						// 删除进程组的数据
						ProcessGroupConfigurationUtil processGroupConfigurationUtil = new ProcessGroupConfigurationUtil();
						processGroupConfigurationUtil.deleteProcessGroupAndConfigurationByNodeid(host.getId() + "");
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				try {
					// 同时删除事件表里的相关数据
					EventListDao eventdao = new EventListDao();
					eventdao.delete(Integer.parseInt(id), category);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			CommonDao cdao = new CommonDao(tvo.getTableName());
			cdao.delete(id);
			String[] ids = { id };
			// 删除设备在临时表里中存储的数据
			String[] nmsTempDataTables = { "nms_cpu_data_temp", "nms_device_data_temp", "nms_disk_data_temp", "nms_diskperf_data_temp", "nms_envir_data_temp", "nms_fdb_data_temp",
					"nms_fibrecapability_data_temp", "nms_fibreconfig_data_temp", "nms_flash_data_temp", "nms_interface_data_temp", "nms_lights_data_temp", "nms_memory_data_temp",
					"nms_other_data_temp", "nms_ping_data_temp", "nms_process_data_temp", "nms_route_data_temp", "nms_sercice_data_temp", "nms_software_data_temp",
					"nms_storage_data_temp", "nms_system_data_temp", "nms_user_data_temp", "nms_nodeconfig", "nms_nodecpuconfig", "nms_nodediskconfig", "nms_nodememconfig" };
			CreateTableManager createTableManager = new CreateTableManager();
			createTableManager.clearNmsTempDatas(nmsTempDataTables, ids);
		}

		// 2.更新xml
		// 主机服务器
		XmlOperator opr1 = new XmlOperator();
		opr1.setFile("server.jsp");
		opr1.init4updateXml();
		if (opr1.isNodeExist(id)) {
			opr1.deleteNodeByID(id);
		}
		opr1.writeXml();
		// 更新所有拓扑图
		ManageXmlDao mdao = new ManageXmlDao();
		List<ManageXml> list = mdao.loadAll();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				ManageXml manageXml = list.get(i);
				XmlOperator xopr = new XmlOperator();
				xopr.setFile(manageXml.getXmlName());
				xopr.init4updateXml();
				if (xopr.isNodeExist(nodeid)) {
					xopr.deleteNodeByID(nodeid);
				}
				xopr.writeXml();
			}
		}
		// 删除关联拓扑图表的数据
		RelationDao rdao = new RelationDao();
		Relation vo = (Relation) rdao.findByNodeId(id, xmlName);
		if (vo != null) {
			rdao.deleteByNode(id, xmlName);
		} else {
			rdao.close();
		}
		// 删除关联图元表的数据
		NodeEquipDao nodeEquipDao = new NodeEquipDao();
		if (nodeEquipDao.findByNode(nodeid) != null) {
			nodeEquipDao.deleteByNode(nodeid);
		} else {
			nodeEquipDao.close();
		}
		// 如果是业务视图，则删除相关表数据
		if (xmlName.indexOf("businessmap") != -1) {
			LineDao lineDao = new LineDao();
			lineDao.deleteByidXml(nodeid, xmlName);
			NodeDependDao nodeDependDao = new NodeDependDao();
			if (nodeDependDao.isNodeExist(nodeid, xmlName)) {
				nodeDependDao.deleteByIdXml(nodeid, xmlName);
			} else {
				nodeDependDao.close();
			}

			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			ManageXmlDao mXmlDao = new ManageXmlDao();
			List xmlList = new ArrayList();
			try {
				xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mXmlDao.close();
			}
			try {
				ChartXml chartxml;
				chartxml = new ChartXml("tree");
				chartxml.addViewTree(xmlList);
			} catch (Exception e) {
				e.printStackTrace();
			}

			ManageXmlDao subMapDao = new ManageXmlDao();
			ManageXml manageXml = (ManageXml) subMapDao.findByXml(xmlName);
			if (manageXml != null) {
				NodeDependDao nodeDepenDao = new NodeDependDao();
				try {
					List lists = nodeDepenDao.findByXml(xmlName);
					ChartXml chartxml;
					chartxml = new ChartXml("NetworkMonitor", "/" + xmlName.replace("jsp", "xml"));
					chartxml.addBussinessXML(manageXml.getTopoName(), lists);
					ChartXml chartxmlList;
					chartxmlList = new ChartXml("NetworkMonitor", "/" + xmlName.replace("jsp", "xml").replace("businessmap", "list"));
					chartxmlList.addListXML(manageXml.getTopoName(), lists);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					nodeDepenDao.close();
				}
			}
		}
		ConnectTypeConfigDao connectTypeConfigDao = new ConnectTypeConfigDao();
		try {
			connectTypeConfigDao.deleteByNodeId(id);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			connectTypeConfigDao.close();
		}
		return null;
	}

	// 删除示意设备
	private String deleteHintMeta() {
		String id = getParaValue("nodeId");
		String fileName = getParaValue("xml");

		HintNodeDao hintNodeDao = new HintNodeDao();
		LineDao lineDao = new LineDao();
		XmlOperator opr = new XmlOperator();
		opr.setFile(fileName);
		opr.init4updateXml();
		if (hintNodeDao.deleteByXml(id, fileName) && lineDao.deleteByidXml(id, fileName)) {
			if (fileName.indexOf("businessmap") != -1) {// 如果是业务视图，则删除节点父子关系表数据
				NodeDependDao nodeDependDao = new NodeDependDao();
				if (nodeDependDao.isNodeExist(id, fileName)) {
					nodeDependDao.deleteByIdXml(id, fileName);
				} else {
					nodeDependDao.close();
				}
			}
			opr.deleteNodeById(id);
		}
		opr.writeXml();
		if(ShareData.getAllhintlinks()!=null){
	    	ShareData.getAllhintlinks().remove(id + ":" + fileName);
	    }
		// 删除关联拓扑图表的数据
		RelationDao rdao = new RelationDao();
		Relation vo = (Relation) rdao.findByNodeId(id, fileName);
		if (vo != null) {
			rdao.deleteByNode(id, fileName);
		} else {
			rdao.close();
		}
		return null;
	}

	// 删除子图上的示意链路
	private String deleteLines() {
		String id = getParaValue("id");
		String xml = getParaValue("xml");
		// 删除示意链路表数据
		LineDao lineDao = new LineDao();
		// 更新xml
		if (!"".equals(id) && !"".equals(xml)) {
			ManageXmlOperator mxmlOpr = new ManageXmlOperator();
			mxmlOpr.setFile(xml);
			mxmlOpr.init4updateXml();
			if (lineDao.delete(id, xml)) {
				NodeDependDao nodeDependDao = new NodeDependDao();
				if (nodeDependDao.isNodeExist(id, xml)) {
					nodeDependDao.deleteByIdXml(id, xml);
				} else {
					nodeDependDao.close();
				}
				mxmlOpr.deleteDemoLinesByID(id);
			}
			mxmlOpr.writeXml();
		}
		return "/topology/submap/change.jsp?submapview=" + xml;
	}

	// 删除实体链路
	private String deleteLink() {
		String id = getParaValue("lineId");
		// 更新数据库
		LinkDao dao = new LinkDao();
		dao.delete(id);
		// 更新所有拓扑图
		ManageXmlDao mdao = new ManageXmlDao();
		List<ManageXml> list = mdao.loadAll();
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				ManageXml manageXml = list.get(i);
				XmlOperator xopr = new XmlOperator();
				xopr.setFile(manageXml.getXmlName());
				xopr.init4updateXml();
				if (xopr.isLinkExist(id)) {
					xopr.deleteLineByID(id);
				}
				if (xopr.isAssLinkExist(id)) {
					xopr.deleteAssLineByID(id);
				}
				xopr.writeXml();
			}
		}

		// 更新内存
		PollingEngine.getInstance().deleteLinkByID(Integer.parseInt(id));
		return null;
	}

	// 删除子图
	private String deleteSubMap() {
		String fileName = (String) getParaValue("xml");
		ManageXmlDao dao = new ManageXmlDao();
		ManageXml map = (ManageXml) dao.delete(fileName);// 删除子图表的数据
		if (map != null) {
			XmlOperator xmlOpr = new XmlOperator();
			xmlOpr.setFile(fileName);
			xmlOpr.deleteXml();// 删除文件夹下对应子图的xml文件
			RelationDao rdao = new RelationDao();
			String str = rdao.delete(map.getId() + "");// 删除关联拓扑图表的数据
			if (str != null && !"".equals(str)) {
				String node[] = str.split(",");
				String nodeId = node[0];
				String xmlName = node[1];
				ManageXmlOperator mXmlOpr = new ManageXmlOperator();
				mXmlOpr.setFile(xmlName);
				mXmlOpr.init4updateXml();
				if (mXmlOpr.isNodeExist(nodeId)) {
					mXmlOpr.updateNode(nodeId, "relationMap", "");
				}
				mXmlOpr.writeXml();
			}
			if (map != null && map.getTopoType() == 1) {
				PollingEngine.getInstance().deleteBusByID(map.getId());// 按照节点类型和id更新内存20100513
			}

			// 删除节点与图元图片关联表的数据
			NodeEquipDao nodeEquipDao = new NodeEquipDao();
			if (nodeEquipDao.findByXml(fileName) != null) {
				nodeEquipDao.deleteByXml(fileName);
			} else {
				nodeEquipDao.close();
			}
			// 删除业务视图
			if (map.getTopoType() == 1) {
				xmlOpr = new XmlOperator();
				xmlOpr.setfile(fileName.replace("jsp", "xml"));
				xmlOpr.deleteXml();// 删除文件夹下对应业务视图的xml文件
				xmlOpr.setfile(fileName.replace("jsp", "xml").replace("businessmap", "list"));
				xmlOpr.deleteXml();// 删除文件夹下对应业务视图的xml文件

				User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
				ManageXmlDao mXmlDao = new ManageXmlDao();
				List xmlList = new ArrayList();
				try {
					xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mXmlDao.close();
				}
				try {
					ChartXml chartxml;
					chartxml = new ChartXml("tree");
					chartxml.addViewTree(xmlList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// 删除关联表数据
			NodeDependDao nodeDependDao = new NodeDependDao();
			HintNodeDao hintNodeDao = new HintNodeDao();
			LineDao lineDao = new LineDao();
			try {
				nodeDependDao.deleteByXml(fileName);
				hintNodeDao.deleteByXml(fileName);
				lineDao.deleteByXml(fileName);
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				nodeDependDao.close();
				hintNodeDao.close();
				lineDao.close();
			}
		}
		return null;
	}

	// 向拓扑图添加实体设备
	public String doinporthost() {
		String xmlName = (String) session.getAttribute(SessionConstant.CURRENT_TOPO_VIEW);
		ARPDao arpdao = new ARPDao();
		String[] ids = getParaArrayValue("checkbox");
		String nodeId = getParaValue("nodeId");
		if (ids != null && ids.length > 0) {
			// 进行修改
			try {
				for (int i = 0; i < ids.length; i++) {
					String id = ids[i];
					ARP arp = (ARP) arpdao.findByID(id);
					// 判断系统里是否有该设备
					HostNodeDao hostnodedao = new HostNodeDao();
					HostNode hostNode = hostnodedao.findByIpaddress(arp.getIpaddress());
					if (hostNode == null) {
						// 判断是否存在别名
						IpAliasDao ipaliasdao = new IpAliasDao();
						IpAlias ipalias = ipaliasdao.getByIp(arp.getIpaddress());
						if (ipalias != null) {
							hostNode = hostnodedao.findByIpaddress(ipalias.getIpaddress());
							if (hostNode != null) {
								// 写拓扑图文件
								ManageXmlOperator mxmlOpr = new ManageXmlOperator();
								mxmlOpr.setFile(xmlName);
								// 保存节点信息
								mxmlOpr.init4editNodes();
								boolean exist = mxmlOpr.isIdExist("net" + hostNode.getId());
								if (exist) {
									return "/topology/network/saveok.jsp?flag=0";
								}
								if (hostNode.getCategory() == 4) {
									mxmlOpr.addNode("net" + hostNode.getId(), 1, "net_server");
								} else if (hostNode.getCategory() < 4) {
									mxmlOpr.addNode("net" + hostNode.getId(), 1, "net_router");
								}
								mxmlOpr.writeXml();
								Host host2 = (Host) PollingEngine.getInstance().getNodeByID(hostNode.getId());
								List<IfEntity> endHostIfentityList = getSortListByHash(host2.getInterfaceHash());
								String end_index = "";
								for (IfEntity ifObj : endHostIfentityList) {
									if (ifObj.getType() == 6 && ifObj.getOperStatus() == 1) {
										end_index = ifObj.getIndex();
									}
								}
								try {
									addLink("1", "链路", "100000", "50", xmlName, nodeId, arp.getIfindex(), "net" + hostNode.getId(), end_index, "1", "0");
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					} else {
						// 写拓扑图文件
						ManageXmlOperator mxmlOpr = new ManageXmlOperator();
						mxmlOpr.setFile(xmlName);
						// 保存节点信息
						mxmlOpr.init4editNodes();
						boolean exist = mxmlOpr.isIdExist("net" + hostNode.getId());
						if (exist) {
							return "/topology/network/saveok.jsp?flag=0";
						}
						if (hostNode.getCategory() == 4) {
							mxmlOpr.addNode("net" + hostNode.getId(), 1, "net_server");
						} else if (hostNode.getCategory() < 4) {
							mxmlOpr.addNode("net" + hostNode.getId(), 1, "net_router");
						}
						mxmlOpr.writeXml();

						Host host2 = (Host) PollingEngine.getInstance().getNodeByID(hostNode.getId());
						List<IfEntity> endHostIfentityList = getSortListByHash(host2.getInterfaceHash());
						String end_index = "";
						for (IfEntity ifObj : endHostIfentityList) {
							if (ifObj.getType() == 6 && ifObj.getOperStatus() == 1) {
								end_index = ifObj.getIndex();
							}
						}
						try {
							addLink("1", "链路", "100000", "50", xmlName, nodeId, arp.getIfindex(), "net" + hostNode.getId(), end_index, "1", "0");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				arpdao.close();
			}
		}
		return "/topology/network/saveok.jsp?flag=1";
	}

	private String editLink() {
		String direction1 = getParaValue("direction1");
		String startIndex = "";
		String endIndex = "";
		int startId = 0;
		int endId = 0;
		if (direction1 != null && direction1.equals("1")) {// 上行设备
			startIndex = getParaValue("end_index");
			endIndex = getParaValue("start_index");
			startId = getParaIntValue("end_id");
			endId = getParaIntValue("start_id");
		} else {// 下行设备
			startIndex = getParaValue("start_index");
			endIndex = getParaValue("end_index");
			startId = getParaIntValue("start_id");
			endId = getParaIntValue("end_id");
		}
		String linkName = getParaValue("link_name");
		String maxSpeed = getParaValue("max_speed");
		String maxPer = getParaValue("max_per");
		String linetext = getParaValue("linetext");
		String interf = getParaValue("interf");
		String id = getParaValue("id");
		if (startId == endId) {
			setErrorCode(ErrorMessage.DEVICES_SAME);
			return null;
		}

		LinkDao dao = new LinkDao();
		RepairLinkDao repairdao = new RepairLinkDao();
		Link formerLink = (Link) dao.findByID(id);
		String formerStartIndex = formerLink.getStartIndex();
		String formerEndIndex = formerLink.getEndIndex();

		Host startHost = (Host) PollingEngine.getInstance().getNodeByID(startId);
		IfEntity if1 = startHost.getIfEntityByIndex(startIndex);
		Host endHost = (Host) PollingEngine.getInstance().getNodeByID(endId);
		IfEntity if2 = endHost.getIfEntityByIndex(endIndex);

		RepairLink repairLink = null;
		repairLink = repairdao.loadLink(startHost.getIpAddress(), formerStartIndex, endHost.getIpAddress(), formerEndIndex);

		formerLink.setLinkName(linkName);
		formerLink.setMaxSpeed(maxSpeed);
		formerLink.setMaxPer(maxPer);
		formerLink.setStartId(startId);
		formerLink.setEndId(endId);
		formerLink.setStartIndex(startIndex);
		formerLink.setEndIndex(endIndex);
		formerLink.setStartIp(if1.getIpAddress());
		formerLink.setEndIp(if2.getIpAddress());
		formerLink.setStartDescr(if1.getDescr());
		formerLink.setEndDescr(if2.getDescr());
		formerLink.setType(Integer.parseInt(linetext));
		formerLink.setShowinterf(Integer.parseInt(interf));
		dao = new LinkDao();
		dao.update(formerLink);

		// 对新修改的连接关系进行原始备份
		if (repairLink == null) {
			// 需要再判断该连接关系是否已经被修改过
			repairLink = repairdao.loadRepairLink(startHost.getIpAddress(), formerStartIndex, endHost.getIpAddress(), formerEndIndex);
			if (repairLink == null) {
				// 说明是第一次修改
				repairLink = new RepairLink();
				repairLink.setStartIp(startHost.getIpAddress());
				repairLink.setStartIndex(formerStartIndex);
				repairLink.setNewStartIndex(formerLink.getStartIndex());
				repairLink.setEndIp(endHost.getIpAddress());
				repairLink.setEndIndex(formerEndIndex);
				repairLink.setNewEndIndex(formerLink.getEndIndex());
				repairdao.save(repairLink);
			} else {
				// 曾经被修改过
				repairLink.setNewStartIndex(formerLink.getStartIndex());
				repairLink.setNewEndIndex(formerLink.getEndIndex());
				repairdao.update(repairLink);
			}
		} else {
			repairLink.setNewStartIndex(formerLink.getStartIndex());
			repairLink.setNewEndIndex(formerLink.getEndIndex());
			repairdao.update(repairLink);
		}

		LinkRoad lr = new LinkRoad();
		lr.setId(formerLink.getId());
		lr.setLinkName(linkName);
		lr.setMaxSpeed(maxSpeed);
		lr.setMaxPer(maxPer);
		lr.setStartId(startId);
		if ("".equals(if1.getIpAddress())) {
			lr.setStartIp(startHost.getIpAddress());
		} else {
			lr.setStartIp(if1.getIpAddress());
		}
		lr.setStartIndex(startIndex);
		lr.setStartDescr(if1.getDescr());

		if ("".equals(if2.getIpAddress())) {
			lr.setEndIp(endHost.getIpAddress());
		} else {
			lr.setEndIp(if2.getIpAddress());
		}
		lr.setEndId(endId);
		lr.setEndIndex(endIndex);
		lr.setEndDescr(if2.getDescr());
		lr.setAssistant(formerLink.getAssistant());
		lr.setType(formerLink.getType());
		lr.setShowinterf(formerLink.getShowinterf());
		PollingEngine.getInstance().deleteLinkByID(lr.getId());
		PollingEngine.getInstance().getLinkList().add(lr);

		return null;
	}

	// 保存编辑子图属性
	private String editSubMap() {
		User uservo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		ManageXmlDao mxDao = new ManageXmlDao();
		if (getParaIntValue("topo_type") == 1) {
			mxDao.updateBusView(uservo.getBusinessids());
		} else {
			mxDao.updateView(uservo.getBusinessids());
		}
		mxDao.close();
		ManageXml vo = new ManageXml();
		String bid = ",";
		String arr[] = getParaArrayValue("checkbox");
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				bid = bid + arr[i] + ",";
			}
		}
		vo.setId(getParaIntValue("id"));
		vo.setXmlName(getParaValue("xml_name"));
		vo.setTopoName(getParaValue("topo_name"));
		vo.setAliasName(getParaValue("alias_name"));
		vo.setTopoTitle(getParaValue("topo_title"));
		vo.setTopoArea(getParaValue("topo_area"));
		vo.setTopoBg(getParaValue("topo_bg"));
		vo.setTopoType(getParaIntValue("topo_type"));
		vo.setUtilhdx(getParaValue("utilhdx"));
		vo.setUtilhdxperc(getParaValue("utilhdxperc"));
		if (getParaValue("home_view") != null && Integer.parseInt(getParaValue("home_view")) == 1) {
			if (Integer.parseInt(getParaValue("topo_type")) == 1) {
				vo.setBus_home_view(1);
				vo.setHome_view(0);
			} else {
				vo.setHome_view(1);
				vo.setBus_home_view(0);
			}
			vo.setPercent(Float.parseFloat(getParaValue("zoom_percent")));
		} else {
			vo.setBus_home_view(0);
			vo.setHome_view(0);
			vo.setPercent(1);
		}
		vo.setBid(bid);
		DaoInterface dao = new ManageXmlDao();
		update(dao, vo);
		return null;

	}

	// 实体图元属性
	private String equipProperty() {
		String nodeid = getParaValue("nodeId");
		String nodeId = nodeid.substring(3);
		String category = getParaValue("category");
		String type = getParaValue("type");

		Node node = PollingEngine.getInstance().getNodeByCategory(type, Integer.parseInt(nodeId));

		if (category == null || "".equals(category)) {
			category = node.getCategory() + "";
		}
		String galleryPanel = "";

		EquipImageDao equipImageDao = new EquipImageDao();
		List<String> list = equipImageDao.getGalleryListing();

		TopoUI topoUI = new TopoUI();
		String gallery = topoUI.createGallery(list);

		Map map = equipImageDao.getGallery(Integer.parseInt(category));
		if (map != null) {
			Object obj = map.get(Integer.parseInt(category));
			if (obj instanceof List) {
				List iconList = (List) obj;
				galleryPanel = topoUI.getEquipGalleryPanel(iconList);
			}
		}
		equipImageDao.close();
		request.setAttribute("equipName", node.getAlias());
		request.setAttribute("category", category);
		request.setAttribute("galleryPanel", galleryPanel);
		request.setAttribute("gallery", gallery);
		request.setAttribute("nodeId", nodeid);
		request.setAttribute("type", type);

		return "/topology/network/editEquip.jsp";

	}

	public String execute(String action) {
		if (action.equals("createSubMap")) {
			String objEntityStr = getParaValue("objEntityStr");
			String linkStr = getParaValue("linkStr");
			String asslinkStr = getParaValue("asslinkStr");
			request.setAttribute("objEntityStr", objEntityStr);
			request.setAttribute("linkStr", linkStr);
			request.setAttribute("asslinkStr", asslinkStr);
			return "/topology/submap/createSubMap.jsp";
		}
		if (action.equals("relationList")) {
			String fileName = (String) session.getAttribute("fatherXML");
			ManageXmlDao dao = new ManageXmlDao();
			List list = dao.loadAll();
			String nodeId = getParaValue("nodeId");
			String category = getParaValue("category");
			RelationDao rdao = new RelationDao();
			Relation vo1 = (Relation) rdao.findByNodeId(nodeId, fileName);
			if (vo1 != null) {
				request.setAttribute("mapId", vo1.getMapId());
			} else {
				request.setAttribute("mapId", "-1");
			}
			request.setAttribute("list", list);
			request.setAttribute("category", category);
			request.setAttribute("nodeId", nodeId);
			return "/topology/submap/relation.jsp";
		}
		if (action.equals("readybackup")) {
			return readybackup();
		}
		if (action.equals("backup")) {
			return backup();
		}
		if (action.equals("resume")) {
			return resume();
		}
		if (action.equals("readyresume")) {
			return readyresume();
		}
		if (action.equals("save")) {
			return save();
		}
		if (action.equals("save_relation_node")) {
			return relationMap();
		}
		if (action.equals("cancel_relation_node")) {
			return cancelRelation();
		}
		if (action.equals("readyAddLink")) {
			return readyAddLink();
		}
		if (action.equals("deleteLink")) {
			return deleteLink();
		}
		if (action.equals("editLink")) {
			return editLink();
		}
		if (action.equals("readyEditLink")) {
			return readyEditLink();
		}
		if (action.equals("addLines")) {
			return addLines();
		}
		if (action.equals("deleteLines")) {
			return deleteLines();
		}
		if (action.equals("deleteSubMap")) {
			return deleteSubMap();
		}
		if (action.equals("saveSubMap")) {
			return saveSubMap();
		}
		if (action.equals("readyEditSubMap")) {
			return readyEditSubMap();
		}
		if (action.equals("editSubMap")) {
			return editSubMap();
		}
		if (action.equals("reBuild")) {
			return reBuildMap();
		}
		if (action.equals("reBuildSubMap")) {
			return reBuildSubMap();
		}
		if (action.equals("deleteEquipFromSubMap")) {
			return deleteEquipFromSubMap();
		}
		if (action.equals("removeEquipFromSubMap")) {
			return removeEquipFromSubMap();
		}
		if (action.equals("hintProperty")) {
			return hintProperty();
		}
		if (action.equals("replacePic")) {
			return replacePic();
		}
		if (action.equals("equipProperty")) {
			return equipProperty();
		}
		if (action.equals("inporthost")) {
			return inporthost();
		}
		if (action.equals("replaceEquipPic")) {
			return replaceEquipPic();
		}
		if (action.equals("showTree")) {
			return showTree();
		}
		if (action.equals("readyAddHintMeta")) {
			return readyAddHintMeta();
		}
		if (action.equals("deleteHintMeta")) {
			return deleteHintMeta();
		}
		if (action.equals("readyEditMap")) {
			return readyEditMap();
		}
		if (action.equals("linkProperty")) {
			return linkProperty();
		}
		if (action.equals("saveLinkProperty")) {
			return saveLinkProperty();
		}
		if (action.equals("addApplications")) {
			return addApplications();
		}
		if (action.equals("showpanel")) {
			return showPanel();
		}
		if (action.equals("savetree")) {
			return saveTree();
		}
		if (action.equals("doinporthost")) {
			return doinporthost();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	private File[] getFile(String path) {
		File file = new File(path);
		File[] array = file.listFiles();
		return array;
	}

	private String getInfo(Hashtable relationhashtable, Hashtable managexmlhashtable, String keyString) {
		String infoStr = "";
		if (relationhashtable != null && relationhashtable.get(keyString) != null) {
			int mapid = Integer.parseInt((String) relationhashtable.get(keyString));
			if (managexmlhashtable != null && managexmlhashtable.get(mapid) != null) {
				ManageXml vo = (ManageXml) managexmlhashtable.get(mapid);
				String xmlname = vo.getXmlName();
				String supperid = vo.getSupperid();
				if (!"0".equals(supperid)) {
					Supper suppervo = null;
					SupperDao dao = new SupperDao();
					try {
						suppervo = (Supper) dao.findByID(supperid);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					if (suppervo != null) {
						infoStr = infoStr + "<b>开发单位名称:</b>" + suppervo.getSu_name() + "<br><b>联系人的姓名:</b>" + suppervo.getSu_person() + "<br><b>联系人电话:</b>"
								+ suppervo.getSu_phone() + "<br>";
					}
				}
				XmlOperator xmlOpr = new XmlOperator();
				xmlOpr.setFile(xmlname);
				xmlOpr.init4updateXml();
				List list = xmlOpr.getAllNodes();
				for (int j = 0; j < list.size(); j++) {
					try {
						String nodeid = ((String) list.get(j)).split(":")[0];
						String category = ((String) list.get(j)).split(":")[1];
						com.afunms.polling.base.Node nodes = null;
						if (nodeid.indexOf("hin") < 0) {
							if (nodeid.indexOf("dbs") != -1) {
								Node mnode = (Node) PollingEngine.getInstance().getNodeByCategory("dbs", Integer.valueOf(nodeid.substring(3)));
								NodeUtil nodeUtil = new NodeUtil();
								NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mnode);
								if (nodeDTO != null) {
									Hashtable oracleHash = (Hashtable) ShareData.getSharedata().get(mnode.getIpAddress() + ":" + mnode.getId());
									Vector tablespace_v = null;
									if (oracleHash != null) {
										tablespace_v = (Vector) oracleHash.get("tableinfo_v");
									}
									if (tablespace_v != null && tablespace_v.size() > 0) {
										OraspaceconfigDao oraspaceconfigManager = new OraspaceconfigDao();
										Hashtable oraspaces = null;
										try {
											oraspaces = oraspaceconfigManager.getByAlarmflag(1);
										} catch (Exception e1) {
											e1.printStackTrace();
										} finally {
											oraspaceconfigManager.close();
										}
										for (int k = 0; k < tablespace_v.size(); k++) {
											try {
												Hashtable ht = (Hashtable) tablespace_v.get(k);
												String tablespace = ht.get("tablespace").toString();
												String percent = ht.get("percent_free").toString();
												if (oraspaces != null && oraspaces.containsKey(nodeDTO.getIpaddress() + ":" + nodeDTO.getId() + ":" + tablespace)) {
													// 存在需要告警的表空间
													Integer free = 0;
													try {
														free = new Float(percent).intValue();
													} catch (Exception e) {
														e.printStackTrace();
													}
													infoStr = infoStr + "<b>" + mnode.getAlias() + "数据表空间利用率：</b><br>";
													infoStr = infoStr + tablespace + "：" + (100 - free) + "%<br>";
												}
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							} else if (nodeid.indexOf("net") != -1) {
								nodes = (Host) PollingEngine.getInstance().getNodeByCategory(category, Integer.valueOf(nodeid.substring(3)));
								if (nodes.getCategory() == 4) {
									List portconfiglist = new ArrayList();
									Portconfig portconfig = null;
									Hashtable allportconfighash = ShareData.getPortConfigHash();
									;
									if (allportconfighash != null && allportconfighash.size() > 0) {
										if (allportconfighash.containsKey(nodes.getIpAddress())) {
											portconfiglist = (List) allportconfighash.get(nodes.getIpAddress());
										}
									}
									Hashtable portconfigHash = new Hashtable();
									if (portconfiglist != null && portconfiglist.size() > 0) {
										for (int i = 0; i < portconfiglist.size(); i++) {
											portconfig = (Portconfig) portconfiglist.get(i);
											portconfigHash.put(portconfig.getPortindex() + "", portconfig);
										}
									}
									Hashtable returnHash = (Hashtable) ShareData.getSharedata().get(nodes.getIpAddress());
									if (returnHash != null) {
										Vector interfaceVector = (Vector) returnHash.get("interface");
										if (interfaceVector != null && interfaceVector.size() > 0) {
											for (int k = 0; k < interfaceVector.size(); k++) {
												try {
													Interfacecollectdata interfacedata = (Interfacecollectdata) interfaceVector.get(k);
													if ("ifOperStatus".equalsIgnoreCase(interfacedata.getEntity())) {
														if (portconfigHash.containsKey(interfacedata.getSubentity())) {
															portconfig = (Portconfig) portconfigHash.get(interfacedata.getSubentity());
															if (portconfig != null && interfacedata.getThevalue() != null && !"".equalsIgnoreCase(interfacedata.getThevalue())) {
																int num = (int) (Math.random() * 100);
																infoStr = infoStr + "<b>业务流量：</b>" + num * 50 + "kb/s<br>";
																break;
															}
														}
													}
												} catch (Exception e) {
													e.printStackTrace();
												}
											}
										}
									}
								}
							} else {

							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return infoStr;
	}

	// 获取拓扑图设备右键菜单
	public String getShowMenu(String Id, String category) {
		String menuItem = "";
		String nodeId=Id;
		if(nodeId.indexOf("hin")!=-1){
			menuItem = "<a class=\"deleteline_menu_out\" onmouseover=\"deleteMenuOver();\" onmouseout=\"deleteMenuOut();\" "
				+ "onclick=\"deleteHintMeta('"
				+ nodeId
				+ "')"
				+ "\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;删除设备</a><br/>"
				+

				// "<a class=\"property_menu_out\" "
				// +"onmouseover=\"propertyMenuOver();\"
				// onmouseout=\"propertyMenuOut();\" " +
				// "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=hintProperty&nodeId="+nodeId+"',"
				// +
				// " window, 'dialogwidth:500px; dialogheight:300px; status:no;
				// help:no;resizable:0');\"" +">&nbsp;&nbsp;&nbsp;&nbsp;图元属性
				// </a><br/>"+

				"<a class=\"relationmap_menu_out\" onmouseover=\"relationMapMenuOver();\" onmouseout=\"relationMapMenuOut();\" "
				+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=relationList&nodeId="
				+ nodeId
				+ "',"
				+ " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\""
				+ ">&nbsp;&nbsp;&nbsp;&nbsp;关联拓扑图 </a><br/>";
			return menuItem;
		}
		Host host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeId.substring(3)));
		String sysoid="";
		String width="";
		String type="";
		String subtype="";
		int height=0;
		if(host!=null){
			String ip=host.getIpAddress();
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
			type=nodedto.getType();
			subtype=nodedto.getSubtype();
			sysoid=host.getSysOid();
			IpaddressPanelDao ipaddressPanelDao = new IpaddressPanelDao();
			IpaddressPanel ipaddressPanel = null;
			try {
				ipaddressPanel = ipaddressPanelDao.loadIpaddressPanel(ip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ipaddressPanelDao.close();
			}
			if(ipaddressPanel!=null){
			PanelModelDao panelModelDao = new PanelModelDao();
			PanelModel panelModel = null;
			try {
				panelModel = (PanelModel)panelModelDao.loadPanelModel(sysoid,ipaddressPanel.getImageType());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				panelModelDao.close();
			}
			if(panelModel!=null){
				width=panelModel.getWidth();
				height=Integer.parseInt(panelModel.getHeight());
				}
			}
			if(host.getCategory()==4){
				menuItem = "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials3" + nodeId + "')\" onmouseout=\"hidemenu('tutorials3" + nodeId + "')\">" + "<a class=\"detail_mainmenu_out\" onmouseover=\"detailMainMenuOver();\" onmouseout=\"detailMainMenuOut();\"" + " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;信息查看&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

				+ "<table class=\"menu\" id=\"tutorials3" + nodeId + "\" width=\"135\" border=\"0\">"

				+ "<tr><td class=\"menu\">" + "<a class=\"detail_menu_out\" onmouseover=\"detailMenuOver();\" onmouseout=\"detailMenuOut();\" " + "onclick=\"javascript:window.open('/afunms/detail/dispatcher.jsp?id=" + nodeId + "','window', " + "'toolbar=no,height=670,width=1024,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看信息 </a><br/>" + "</td></tr>"

				+ "</table></td><tr></table>"


					+	"<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials1" + nodeId + "')\" onmouseout=\"hidemenu('tutorials1" + nodeId + "')\">" + "<a class=\"manage_mainmenu_out\" onmouseover=\"manageMainMenuOver();\" onmouseout=\"manageMainMenuOut();\"" + " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备管理&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

						+ "<table class=\"menu\" id=\"tutorials1" + nodeId + "\" width=\"135\" border=\"0\">" + "<tr><td class=\"menu\">" + "<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" " + "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=equipProperty&type=" + category + "&nodeId=" + nodeId + "'," + " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;图元属性 </a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"relationmap_menu_out\" onmouseover=\"relationMapMenuOver();\" onmouseout=\"relationMapMenuOut();\" " + "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=relationList&nodeId=" + nodeId + "&category=" + category + "'," + " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;关联拓扑图 </a><br/>" + "</td></tr>" + "<tr><td class=\"menu\">" + "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" " + "onclick=\"deleteEquip('" + nodeId + "','" + category + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统删除设备</a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" " + "onclick=\"removeEquip('" + nodeId + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;拓扑图删除设备</a><br/>" + "</td></tr>"

						+ "</table></td><tr></table>"

						+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials2" + nodeId + "')\" onmouseout=\"hidemenu('tutorials2" + nodeId + "')\">" + "<a class=\"tool_menu_out\" onmouseover=\"toolMenuOver();\" onmouseout=\"toolMenuOut();\"" + " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;工&nbsp;&nbsp;&nbsp;&nbsp;具&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

						+ "<table class=\"menu\" id=\"tutorials2" + nodeId + "\" width=\"135\" border=\"0\">"

						+ "<tr><td class=\"menu\"><a class=\"ping_menu_out\" onmouseover=\"pingMenuOver();\" onmouseout=\"pingMenuOut();\"" + " onclick=\"javascript:resetProcDlg();window.showModelessDialog('/afunms/tool/ping.jsp?ipaddress=" + ip + "'," + " window, 'dialogHeight:500px;dialogWidth:500px;status:0;help:0;edge:sunken;center:yes;scroll:0');" + "timingCloseProDlg(8000);\" title=\"ping " + ip + "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ping </a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"telnet_menu_out\" onmouseover=\"telnetMenuOver();\" onmouseout=\"telnetMenuOut();\" " + "onclick=\"javascript:window.open('/afunms/network.do?action=telnet&&ipaddress=" + ip + "','window', " + "'toolbar=no,height=400,width=500,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Telnet</a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"trace_menu_out\" onmouseover=\"traceMenuOver();\" onmouseout=\"traceMenuOut();\" " + "onclick=\"javascript:window.open('/afunms/tool/tracerouter.jsp?ipaddress=" + ip + "','window', " + "'toolbar=no,height=400,width=500,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Traceroute</a><br/>" + "</td></tr>"

						+ "</table></td><tr></table>"

						+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials5" + nodeId + "')\" onmouseout=\"hidemenu('tutorials5" + nodeId + "')\">" + "<a class=\"alarm_menu_out\" onmouseover=\"alarmMenuOver();\" onmouseout=\"alarmMenuOut();\"" + " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警信息 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

						+ "<table class=\"menu\" id=\"tutorials5" + nodeId + "\" width=\"135\" border=\"0\">"

						+ "<tr><td class=\"menu\"><a class=\"confirmAlarm_menu_out\" onmouseover=\"confirmAlarmMenuOver();\" onmouseout=\"confirmAlarmMenuOut();\" " + "onclick=\"confirmAlarm('" + nodeId + "','" + category + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警确认</a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"list_menu_out\" onmouseover=\"listMenuOver();\" onmouseout=\"listMenuOut();\" " + "onclick=\"javascript:window.open('/afunms/monitor.do?action=hosteventlist&nodetype=" + category.substring(0, 3) + "&id=" + nodeId.substring(3) + "','window', " + "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警列表</a><br/>" + "</td></tr>" + "</table></td><tr></table>";

			}else{
				menuItem = "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials3" + nodeId + "')\" onmouseout=\"hidemenu('tutorials3" + nodeId + "')\">" + "<a class=\"detail_mainmenu_out\" onmouseover=\"detailMainMenuOver();\" onmouseout=\"detailMainMenuOut();\"" + " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;信息查看&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

				+ "<table class=\"menu\" id=\"tutorials3" + nodeId + "\" width=\"135\" border=\"0\">"

				+ "<tr><td class=\"menu\">" + "<a class=\"detail_menu_out\" onmouseover=\"detailMenuOver();\" onmouseout=\"detailMenuOut();\" " + "onclick=\"javascript:window.open('/afunms/detail/dispatcher.jsp?id=" + nodeId + "','window', " + "'toolbar=no,height=670,width=1024,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看信息 </a><br/>" + "</td></tr>"

				+ "</table></td><tr></table>"


					+	"<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials1" + nodeId + "')\" onmouseout=\"hidemenu('tutorials1" + nodeId + "')\">" + "<a class=\"manage_mainmenu_out\" onmouseover=\"manageMainMenuOver();\" onmouseout=\"manageMainMenuOut();\"" + " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;设备管理&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

						+ "<table class=\"menu\" id=\"tutorials1" + nodeId + "\" width=\"135\" border=\"0\">" + "<tr><td class=\"menu\">" + "<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" " + "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=equipProperty&type=" + category + "&nodeId=" + nodeId + "'," + " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;图元属性 </a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"relationmap_menu_out\" onmouseover=\"relationMapMenuOver();\" onmouseout=\"relationMapMenuOut();\" " + "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=relationList&nodeId=" + nodeId + "&category=" + category + "'," + " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;关联拓扑图 </a><br/>" + "</td></tr>" + "<tr><td class=\"menu\">" + "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" " + "onclick=\"deleteEquip('" + nodeId + "','" + category + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统删除设备</a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" " + "onclick=\"removeEquip('" + nodeId + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;拓扑图删除设备</a><br/>" + "</td></tr>"

						+ "</table></td><tr></table>"

						+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials2" + nodeId + "')\" onmouseout=\"hidemenu('tutorials2" + nodeId + "')\">" + "<a class=\"tool_menu_out\" onmouseover=\"toolMenuOver();\" onmouseout=\"toolMenuOut();\"" + " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;工&nbsp;&nbsp;&nbsp;&nbsp;具&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

						+ "<table class=\"menu\" id=\"tutorials2" + nodeId + "\" width=\"135\" border=\"0\">"

						+ "<tr><td class=\"menu\"><a class=\"ping_menu_out\" onmouseover=\"pingMenuOver();\" onmouseout=\"pingMenuOut();\"" + " onclick=\"javascript:resetProcDlg();window.showModelessDialog('/afunms/jsp/tool/ping.jsp?ip=" + ip + "'," + " window, 'dialogHeight:380px;dialogWidth:650px;status:0;help:0;edge:sunken;center:yes;scroll:0');" + "timingCloseProDlg(8000);\" title=\"ping " + ip + "\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ping </a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"telnet_menu_out\" onmouseover=\"telnetMenuOver();\" onmouseout=\"telnetMenuOut();\" " + "onclick=\"javascript:window.open('/afunms/jsp/tool/webTelnet.jsp?ip=" + ip + "','window', " + "'toolbar=no,height=450,width=630,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Telnet</a><br/>" + "</td></tr>"

						+ "</table></td><tr></table>"

						+ "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials5" + nodeId + "')\" onmouseout=\"hidemenu('tutorials5" + nodeId + "')\">" + "<a class=\"alarm_menu_out\" onmouseover=\"alarmMenuOver();\" onmouseout=\"alarmMenuOut();\"" + " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警信息 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"

						+ "<table class=\"menu\" id=\"tutorials5" + nodeId + "\" width=\"135\" border=\"0\">"

						+ "<tr><td class=\"menu\"><a class=\"confirmAlarm_menu_out\" onmouseover=\"confirmAlarmMenuOver();\" onmouseout=\"confirmAlarmMenuOut();\" " + "onclick=\"confirmAlarm('" + nodeId + "','" + category + "')" + "\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警确认</a><br/>" + "</td></tr>"

						+ "<tr><td class=\"menu\">" + "<a class=\"list_menu_out\" onmouseover=\"listMenuOver();\" onmouseout=\"listMenuOut();\" " + "onclick=\"javascript:window.open('/afunms/monitor.do?action=hosteventlist&nodetype=" + category.substring(0, 3) + "&id=" + nodeId.substring(3) + "','window', " + "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\"" + ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警列表</a><br/>" + "</td></tr>" + "</table></td><tr></table>";

			}
		} else {
			menuItem = "<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials3"+nodeId+"')\" onmouseout=\"hidemenu('tutorials3"+nodeId+"')\">"
			+"<a class=\"detail_mainmenu_out\" onmouseover=\"detailMainMenuOver();\" onmouseout=\"detailMainMenuOut();\""
			+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;信息查看&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"
			
			+"<table class=\"menu\" id=\"tutorials3"+nodeId+"\" width=\"135\" border=\"0\">"
			
			+"<tr><td class=\"menu\">"
			+"<a class=\"detail_menu_out\" onmouseover=\"detailMenuOver();\" onmouseout=\"detailMenuOut();\" "
			+ "onclick=\"javascript:window.open('/afunms/detail/dispatcher.jsp?id="
			+nodeId
			+ "','window', "
			+ "'toolbar=no,height=670,width=1024,scrollbars=yes,center=yes,screenY=0')\""
			+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;查看信息 </a><br/>"
			+"</td></tr>" 
			+"</table></td><tr></table>"			
			
			+"<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials1"+nodeId+"')\" onmouseout=\"hidemenu('tutorials1"+nodeId+"')\">"
			+"<a class=\"manage_mainmenu_out\" onmouseover=\"manageMainMenuOver();\" onmouseout=\"manageMainMenuOut();\""
			+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;资源管理&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"
			
			+"<table class=\"menu\" id=\"tutorials1"+nodeId+"\" width=\"135\" border=\"0\">"
			+"<tr><td class=\"menu\">"
			+"<a class=\"property_menu_out\" onmouseover=\"propertyMenuOver();\" onmouseout=\"propertyMenuOut();\" "
			+ "onclick=\"javascript:window.showModalDialog('/afunms/submap.do?action=equipProperty&type="
			+ category
			+ "&nodeId="
			+ nodeId
			+ "',"
			+ " window, 'dialogwidth:500px; dialogheight:300px; status:no; help:no;resizable:0');\""
			+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;图元属性 </a><br/>"
			+"</td></tr>" 
			
			+"<tr><td class=\"menu\">"
			+"<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" "
			+ "onclick=\"deleteEquip('"
			+ nodeId
			+ "','"
			+ category
			+ "')"
			+ "\""
			+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;系统删除设备</a><br/>"
			+"</td></tr>" 
			
			+"<tr><td class=\"menu\">"
			+ "<a class=\"deleteEquip_menu_out\" onmouseover=\"deleteEquipMenuOver();\" onmouseout=\"deleteEquipMenuOut();\" "
			+ "onclick=\"removeEquip('"
			+ nodeId
			+ "')"
			+ "\""
			+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;拓扑图删除设备</a><br/>"
			+"</td></tr>"
			
			+"</table></td><tr></table>"
			
			+"<table width=\"135\" border=\"0\"><tr><td onmouseover=\"showmenu('tutorials5"+nodeId+"')\" onmouseout=\"hidemenu('tutorials5"+nodeId+"')\">"
			+"<a class=\"alarm_menu_out\" onmouseover=\"alarmMenuOver();\" onmouseout=\"alarmMenuOut();\""
			+ " onclick=\"#\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警信息 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;>></a>"
			
			+"<table class=\"menu\" id=\"tutorials5"+nodeId+"\" width=\"135\" border=\"0\">"
			
			+"<tr><td class=\"menu\"><a class=\"confirmAlarm_menu_out\" onmouseover=\"confirmAlarmMenuOver();\" onmouseout=\"confirmAlarmMenuOut();\" "
			+ "onclick=\"confirmAlarm('"
			+ nodeId
			+ "','"
			+ category
			+ "')"
			+ "\""
			+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警确认</a><br/>"
			+"</td></tr>"
			
			+"<tr><td class=\"menu\">"
			+"<a class=\"list_menu_out\" onmouseover=\"listMenuOver();\" onmouseout=\"listMenuOut();\" "
			+ "onclick=\"javascript:window.open('/afunms/monitor.do?action=hosteventlist&nodetype="+category.substring(0, 3)+"&id="
			+ nodeId.substring(3)
			+ "','window', "
			+ "'toolbar=no,height=600,width=900,scrollbars=yes,center=yes,screenY=0')\""
			+ ">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;告警列表</a><br/>"
			+"</td></tr>" 				
			+"</table></td><tr></table>";
			
		}

		return menuItem;
	}

	// 获取拓扑图设备实时数据
	public String getShowMessage(String Id, String category, String xmlname) {
		//
		try {
			Calendar date = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date c1 = date.getTime();
			String recordtime = sdf.format(c1);
			Hashtable checkEventHashtable = ShareData.getCheckEventHash();
			Hashtable relationhashtable = ShareData.getRelationhashtable();
			Hashtable managexmlhashtable = ShareData.getManagexmlhashtable();
			Hashtable managexmlhash = ShareData.getManagexmlhash();
			Hashtable hinthash = ShareData.getAllhintlinks();
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			if (managexmlhash == null) {
				return "获取数据失败";
			}
			ManageXml mvo = (ManageXml) managexmlhash.get(xmlname);
			String keyString = Id + ":" + xmlname;
			int ismapalarm = isMapalarm(checkEventHashtable, relationhashtable, managexmlhashtable, keyString);
			if (Id.indexOf("hin") >= 0) {
				// 示意设备
				HintNode vo = null;
				try {
					if (hinthash.containsKey(keyString)) {
						vo = (HintNode) hinthash.get(keyString);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				String infoStr = getInfo(relationhashtable, managexmlhashtable, keyString);
				if (vo == null) {
					return "示意设备";
				}
				if (ismapalarm > 0) {
					return "<b>" + vo.getAlias() + "</b>" + TopoNodeInfoService.getInstance().getBusClor("100") + TopoNodeInfoService.getInstance().getBusClor1("80") + infoStr
							+ "<font color='red'>--报警信息:--</font><br>子图有告警<br>更新时间：" + recordtime;
				}
				return "<b>" + vo.getAlias() + "</b>" + TopoNodeInfoService.getInstance().getBusClor("100") + TopoNodeInfoService.getInstance().getBusClor1("100") + infoStr
						+ "<font color='green'>--无报警信息:--</font>";
			}
			if (Id.indexOf("bus") >= 0) {
				// 示意设备 暂不考虑
				return "业务节点";
			}
			String nodeTag = Id.substring(0, 3);
			String nodeid = Id.substring(3);

			NodeUtil nodeUtil = new NodeUtil();
			List<BaseVo> list = nodeUtil.getByNodeTag(nodeTag, null);
			NodeDTO node = null;
			for (BaseVo baseVo : list) {
				NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(baseVo);
				if (nodeid.equals(nodeDTO.getNodeid())) {
					node = nodeDTO;
					break;
				}
			}
			if (node == null) {
				return "该节点已被删除";
			}
			Host nodes = null;
			if (Id.indexOf("dbs") != -1) {
				Node mnode = (Node) PollingEngine.getInstance().getNodeByCategory("dbs", Integer.valueOf(nodeid));
				List listalarm = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(node.getId() + "", node.getType(), node.getSubtype());
				String returns = TopoNodeInfoService.getInstance().getOtherNodeInfo(listalarm, mnode);
				return mnode.getShowMessage() + "<br>" + returns;
			} else if (Id.indexOf("net") != -1) {
				if ("net_virtual".equalsIgnoreCase(category)) {
					category = "net_vmware";
					nodes = (Host) PollingEngine.getInstance().getNodeByCategory(category, Integer.valueOf(nodeid));
				} else {
					nodes = (Host) PollingEngine.getInstance().getNodeByCategory(category, Integer.valueOf(nodeid));
				}

			} else {
				Node mnode = (Node) PollingEngine.getInstance().getNodeByCategory(category, Integer.valueOf(nodeid));
				List listalarm = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(node.getId() + "", node.getType(), node.getSubtype());
				String returns = TopoNodeInfoService.getInstance().getOtherNodeInfo(listalarm, mnode);
				return mnode.getShowMessage() + "<br>" + returns;
			}

			Hashtable tophash = ShareData.getToprelation();

			Hashtable alarmnodehash = ShareData.getAllalarmindicators();

			List<IndicatorsTopoRelation> list1 = new ArrayList();
			if (tophash.containsKey(mvo.getId() + ":" + nodeid)) {
				list1 = (List) tophash.get(mvo.getId() + ":" + nodeid);
			}
			List moidList = new ArrayList();
			for (int k = 0; k < list1.size(); k++) {
				IndicatorsTopoRelation nm = (IndicatorsTopoRelation) list1.get(k);
				if (nm != null) {
					AlarmIndicatorsNode alarmIndicatorsNode = null;
					if (alarmnodehash.containsKey(nm.getIndicatorsId() + ":" + nodeid)) {
						alarmIndicatorsNode = (AlarmIndicatorsNode) alarmnodehash.get(nm.getIndicatorsId() + ":" + nodeid);
						if (alarmIndicatorsNode != null) {
							moidList.add(alarmIndicatorsNode);
						}
					}
				}
			}
			String returns = TopoNodeInfoService.getInstance().getNodeInfo(moidList, nodes);
			if (ismapalarm > 0) {
				returns = returns + "<br><font color='red'>--报警信息:--</font><br>子图有告警";
			}
			return returns;
		} catch (Exception e) {
			e.printStackTrace();
			return "正在获取数据";
		}

	}

	// 示意图元属性
	private String hintProperty() {

		String nodeId = getParaValue("nodeId");
		Node node = PollingEngine.getInstance().getNodeByID(Integer.parseInt(nodeId));
		String galleryPanel = "";
		String resTypeName = getParaValue("resTypeName");
		if (resTypeName == null || "".equals(resTypeName)) {
			resTypeName = "路由器";
		} else {
			try {
				resTypeName = new String(resTypeName.getBytes("ISO-8859-1"), "gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		HintItemDao hintItemDao = new HintItemDao();
		TopoUI topoUI = new TopoUI();
		List<String> list = hintItemDao.getGalleryListing();
		String gallery = topoUI.createGallery(list, -1, 1);

		Map map = hintItemDao.getGallery(resTypeName);
		if (map != null) {
			Object obj = map.get(resTypeName);
			if (obj instanceof List) {
				List iconList = (List) obj;
				galleryPanel = topoUI.getGalleryPanel(iconList);
			}
		}
		hintItemDao.close();
		request.setAttribute("resTypeName", resTypeName);
		if (node != null) {
			request.setAttribute("equipName", node.getAlias());
		} else {
			request.setAttribute("equipName", "");
		}
		request.setAttribute("galleryPanel", galleryPanel);
		request.setAttribute("gallery", gallery);
		request.setAttribute("nodeId", nodeId);
		return "/topology/submap/editEquip.jsp";
	}

	// 倒入该网络设备关联的服务器
	private String inporthost() {
		String nodeid = getParaValue("nodeId");
		String nodeId = nodeid.substring(3);
		String category = getParaValue("category");
		String type = getParaValue("type");
		List arpList = new ArrayList();
		HostNodeDao nodedao = new HostNodeDao();
		HostNode node = null;
		try {
			node = (HostNode) nodedao.findByID(nodeId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodedao.close();
		}
		String equipName = "";
		ARPDao arpDao = new ARPDao();
		try {
			arpList = arpDao.loadARPByNodeId(node.getId());
		} catch (Exception e) {
			arpDao.close();
		}
		if (category == null || "".equals(category)) {
			category = node.getCategory() + "";
		}
		equipName = node.getAlias();
		String galleryPanel = "";
		EquipImageDao equipImageDao = new EquipImageDao();
		List<String> list = new ArrayList();
		Map map = null;
		String gallery = "";
		TopoUI topoUI = new TopoUI();
		try {
			list = equipImageDao.getGalleryListing();
			gallery = topoUI.createGallery(list);
			map = equipImageDao.getGallery(Integer.parseInt(category));
			if (map != null) {
				Object obj = map.get(Integer.parseInt(category));
				if (obj instanceof List) {
					List iconList = (List) obj;
					galleryPanel = topoUI.getEquipGalleryPanel(iconList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			equipImageDao.close();
		}
		request.setAttribute("equipName", equipName);
		request.setAttribute("category", category);
		request.setAttribute("galleryPanel", galleryPanel);
		request.setAttribute("gallery", gallery);
		request.setAttribute("nodeId", nodeid);
		request.setAttribute("type", type);
		request.setAttribute("arpList", arpList);
		return "/topology/network/inporthost.jsp";

	}

	// 判断设备关联子图是否有告警
	private int isMapalarm(Hashtable checkEventHashtable, Hashtable relationhashtable, Hashtable managexmlhashtable, String keyString) {
		int isalarm = 0;
		if (relationhashtable != null && relationhashtable.get(keyString) != null) {
			int mapid = Integer.parseInt((String) relationhashtable.get(keyString));
			if (managexmlhashtable != null && managexmlhashtable.get(mapid) != null) {
				ManageXml vo = (ManageXml) managexmlhashtable.get(mapid);
				String xmlname = vo.getXmlName();
				XmlOperator xmlOpr = new XmlOperator();
				xmlOpr.setFile(xmlname);
				xmlOpr.init4updateXml();
				List list = xmlOpr.getAllNodes();
				for (int j = 0; j < list.size(); j++) {
					String nodeid = ((String) list.get(j)).split(":")[0];
					String category = ((String) list.get(j)).split(":")[1];
					com.afunms.polling.base.Node nodes = null;
					if (nodeid.indexOf("hin") < 0) {
						if (nodeid.indexOf("dbs") != -1) {
							Node mnode = (Node) PollingEngine.getInstance().getNodeByCategory("dbs", Integer.valueOf(nodeid.substring(3)));
							NodeUtil nodeUtil = new NodeUtil();
							NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mnode);
							if (nodeDTO != null) {
								String chexkname = nodeid.substring(3) + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
								if (checkEventHashtable != null) {
									for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
										String key = (String) it.next();
										if (key.startsWith(chexkname)) {
											isalarm++;
										}
									}
								}
							}
						} else if (nodeid.indexOf("net") != -1) {
							nodes = (Host) PollingEngine.getInstance().getNodeByCategory(category, Integer.valueOf(nodeid.substring(3)));
							NodeUtil nodeUtil = new NodeUtil();
							NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(nodes);
							if (nodeDTO != null) {
								String chexkname = nodeid.substring(3) + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
								if (checkEventHashtable != null) {
									for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
										String key = (String) it.next();
										if (key.startsWith(chexkname)) {
											isalarm++;
										}
									}
								}
							}
						} else {
							Node mnode = (Node) PollingEngine.getInstance().getNodeByCategory(category, Integer.valueOf(nodeid.substring(3)));
							NodeUtil nodeUtil = new NodeUtil();
							NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(mnode);
							if (nodeDTO != null) {
								String chexkname = nodeid.substring(3) + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
								if (checkEventHashtable != null) {
									for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
										String key = (String) it.next();
										if (key.startsWith(chexkname)) {
											isalarm++;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return isalarm;
	}

	// 链路样式
	private String linkProperty() {
		String lineId = getParaValue("lineId");
		request.setAttribute("lineId", lineId);
		return "/topology/submap/editLine.jsp";
	}

	// 确认声音告警 hukelei add
	public String playSoundAlarm(String content, String flag, String id) {
		String message;
		int volumeValue = 50;
		int rateValue = 0;
		message = content;
		if (message != null && !"null".equalsIgnoreCase(message)) {
			ActiveXComponent sap = new ActiveXComponent("Sapi.SpVoice");
			Dispatch sapo = sap.getObject();
			sap.setProperty("Volume", new Variant(volumeValue));
			sap.setProperty("Rate", new Variant(rateValue));
			Dispatch.call(sapo, "Speak", new Variant(message));
		}
		String returns = "error";
		return returns;
	}

	// 子图上创建示意图元
	private String readyAddHintMeta() {

		String galleryPanel = "";
		String resTypeName = getParaValue("resTypeName");
		String fileName = getParaValue("xml");
		if (resTypeName == null || "".equals(resTypeName)) {
			resTypeName = "路由器";
		} else {
			try {
				resTypeName = new String(resTypeName.getBytes("ISO-8859-1"), "gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		HintItemDao hintItemDao = new HintItemDao();
		TopoUI topoUI = new TopoUI();
		List<String> list = hintItemDao.getGalleryListing();
		String gallery = topoUI.createGallery(list, -1, 1);

		Map map = hintItemDao.getGallery(resTypeName);
		if (map != null) {
			Object obj = map.get(resTypeName);
			if (obj instanceof List) {
				List iconList = (List) obj;
				galleryPanel = topoUI.getGalleryPanel(iconList);
			}
		}
		hintItemDao.close();
		request.setAttribute("resTypeName", resTypeName);
		request.setAttribute("galleryPanel", galleryPanel);
		request.setAttribute("gallery", gallery);
		request.setAttribute("fileName", fileName);
		return "/topology/submap/gallery.jsp";
	}

	// 子图上创建实体链路yangjun add
	private String readyAddLink() {
		HostNodeDao dao = new HostNodeDao();
		List list = dao.loadAll();

		String fileName = getParaValue("xml");
		String start_id = getParaValue("start_id");
		String end_id = getParaValue("end_id");
		int id1 = Integer.parseInt(start_id.substring(3));
		int id2 = Integer.parseInt(end_id.substring(3));
		String alias1 = "";
		String ipAddress1 = "";
		String alias2 = "";
		String ipAddress2 = "";
		String link_name = "";
		for (int i = 0; i < list.size(); i++) {
			HostNode node = (HostNode) list.get(i);
			if (node.getId() == id1) {
				alias1 = node.getAlias();
				ipAddress1 = node.getIpAddress();
			}
			if (node.getId() == id2) {
				alias2 = node.getAlias();
				ipAddress2 = node.getIpAddress();
			}
		}
		link_name = ipAddress1 + "/" + ipAddress2;
		Host host1 = (Host) PollingEngine.getInstance().getNodeByID(id1);
		request.setAttribute("start_if", host1.getInterfaceHash().values().iterator());
		Host host2 = (Host) PollingEngine.getInstance().getNodeByID(id2);
		request.setAttribute("end_if", host2.getInterfaceHash().values().iterator());
		request.setAttribute("alias_start", alias1);
		request.setAttribute("ipAddress_start", ipAddress1);
		request.setAttribute("alias_end", alias2);
		request.setAttribute("ipAddress_end", ipAddress2);
		request.setAttribute("start_id", start_id);
		request.setAttribute("end_id", end_id);
		request.setAttribute("link_name", link_name);
		request.setAttribute("xml", fileName);

		return "/topology/submap/addLink.jsp";
	}

	private String readybackup() {
		ManageXmlDao dao = new ManageXmlDao();
		List list = dao.loadAll();
		request.setAttribute("list", list);
		return "/topology/submap/backuplist.jsp";
	}

	// 编辑实体链路
	private String readyEditLink() {
		HostNodeDao dao = new HostNodeDao();
		List list = dao.loadAll();

		String lineId = getParaValue("lineId");

		LinkDao linkdao = new LinkDao();
		Link link = (Link) linkdao.findByID(lineId);
		linkdao.close();
		int startId = link.getStartId();
		int endId = link.getEndId();
		String startIndex = link.getStartIndex();
		String endIndex = link.getEndIndex();

		String alias_start = "";
		String ipAddress_start = "";
		String alias_end = "";
		String ipAddress_end = "";
		String link_name = link.getLinkName();
		String max_speed = link.getMaxSpeed();
		String max_per = link.getMaxPer();
		String linetext = link.getType() + "";
		String showinterf = link.getShowinterf() + "";
		for (int i = 0; i < list.size(); i++) {
			HostNode node = (HostNode) list.get(i);
			if (node.getId() == startId) {
				alias_start = node.getAlias();
				ipAddress_start = node.getIpAddress();
			}
			if (node.getId() == endId) {
				alias_end = node.getAlias();
				ipAddress_end = node.getIpAddress();
			}
		}
		Host host1 = (Host) PollingEngine.getInstance().getNodeByID(startId);
		Host host2 = (Host) PollingEngine.getInstance().getNodeByID(endId);

		request.setAttribute("start_if", host1.getInterfaceHash().values().iterator());
		request.setAttribute("end_if", host2.getInterfaceHash().values().iterator());
		request.setAttribute("alias_start", alias_start);
		request.setAttribute("ipAddress_start", ipAddress_start);
		request.setAttribute("alias_end", alias_end);
		request.setAttribute("ipAddress_end", ipAddress_end);
		request.setAttribute("start_id", new Integer(startId));
		request.setAttribute("end_id", new Integer(endId));
		request.setAttribute("start_index", startIndex);
		request.setAttribute("end_index", endIndex);
		request.setAttribute("link_name", link_name);
		request.setAttribute("max_speed", max_speed);
		request.setAttribute("max_per", max_per);
		request.setAttribute("linetext", linetext);
		request.setAttribute("showinterf", showinterf);
		request.setAttribute("id", lineId);

		return "/topology/submap/editLink.jsp";
	}

	// 准备编辑根图属性
	private String readyEditMap() {
		ManageXmlDao dao = new ManageXmlDao();
		ManageXml vo = (ManageXml) dao.findByXml("network.jsp");
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return "/topology/submap/editSubMap.jsp";
	}

	// 准备编辑子图属性
	private String readyEditSubMap() {
		String fileName = (String) getParaValue("xml");
		ManageXmlDao dao = new ManageXmlDao();
		ManageXml vo = (ManageXml) dao.findByXml(fileName);
		if (vo != null) {
			request.setAttribute("vo", vo);
		}
		return "/topology/submap/editSubMap.jsp";
	}

	private String readyresume() {
		String filename = getParaValue("xml");
		List list = new ArrayList();
		File[] array = getFile(ResourceCenter.getInstance().getSysPath() + "resource\\xml\\");
		for (int i = 0; i < array.length; i++) {
			if (array[i].isFile()) {
				if (array[i].getName().indexOf(filename) != -1) {
					list.add(array[i].getName());
				}
			}
		}
		request.setAttribute("list", list);
		request.setAttribute("filename", filename);
		return "/topology/submap/resumelist.jsp";
	}

	// 重建拓扑图，将子图和关联拓扑图都删除
	public String reBuildMap() {
		DiscoverDataHelper helper = new DiscoverDataHelper();
		try {
			helper.DB2NetworkXml();
			helper.DB2ServerXml();
		} catch (Exception e) {
			e.printStackTrace();
		}

		LineDao lineDao = new LineDao();
		lineDao.deleteByXml("network.jsp");

		HintNodeDao hintNodeDao = new HintNodeDao();
		hintNodeDao.deleteByXml("network.jsp");
		// 清空节点与图元图片关联表的数据
		NodeEquipDao nodeEquipDao = new NodeEquipDao();
		nodeEquipDao.deleteAll();
		return null;
	}

	public String reBuildSubMap() {
		String xmlname = getParaValue("xml");
		DiscoverDataHelper helper = new DiscoverDataHelper();
		try {
			helper.createLinkXml(xmlname);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Objbean[] refreshImage(String xml, Objbean obj[]) {
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		Hashtable relationhashtable = ShareData.getRelationhashtable();
		Hashtable managexmlhashtable = ShareData.getManagexmlhashtable();
		Hashtable hinthash = ShareData.getAllhintlinks();
		Hashtable nodeequiphash = ShareData.getAllnodeequps();
		if (obj != null && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {
				try {
					Objbean objbean = obj[i];
					String Id = objbean.getId().replaceAll("node_", "");
					String keyString = Id + ":" + xml;
					// 判断节点关联子图是否有告警
					int ismapalarm = isMapalarm(checkEventHashtable, relationhashtable, managexmlhashtable, keyString);// 暂时取消子图告警传递
					if (objbean.getId().indexOf("bus") >= 0) {
						// 业务节点设备 暂不考虑.
						objbean.setImage(objbean.getImage().substring(objbean.getImage().indexOf("resource/") + 9));
						continue;
					}
					if (objbean.getId().indexOf("was") >= 0) {
						objbean.setImage(objbean.getImage().substring(objbean.getImage().indexOf("resource/") + 9));
						continue;
					}
					if (objbean.getId().indexOf("hin") >= 0) {
						HintNode vo = null;
						try {
							if (hinthash != null && hinthash.containsKey(keyString)) {
								vo = (HintNode) hinthash.get(keyString);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (vo == null) {
							continue;
						}
						String images = vo.getImage().substring(17);
						if (ismapalarm > 0) {// 暂时去掉告警传递图片闪烁
							images = vo.getImage().substring(17, vo.getImage().lastIndexOf("/") + 1) + "alarm.gif";
						}
						objbean.setImage(images);
						continue;
					}
					String nodeTag = Id.substring(0, 3);
					String nodeid = Id.substring(3);
					NodeUtil nodeUtil = new NodeUtil();
					List<BaseVo> list = nodeUtil.getByNodeTag(nodeTag, null);
					NodeDTO node = null;
					for (BaseVo baseVo : list) {
						NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(baseVo);
						if (nodeid.equals(nodeDTO.getNodeid())) {
							node = nodeDTO;
							break;
						}
					}

					if (node == null) {
						continue;
					}
					int alarmLevel = 0;
					String chexkname = nodeid + ":" + node.getType() + ":" + node.getSubtype() + ":";
					for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
						String key = (String) it.next();
						if (key.startsWith(chexkname)) {
							if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
								alarmLevel = (Integer) checkEventHashtable.get(key);
							}
						}
					}
					NodeEquip vo = null;
					try {
						if (nodeequiphash.containsKey(Id + ":" + objbean.getXmlname())) {
							vo = (NodeEquip) nodeequiphash.get(Id + ":" + objbean.getXmlname());
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					if (Constant.TYPE_HOST.equals(node.getType())) {
						if (vo != null) {
							if (alarmLevel > 0 || ismapalarm > 0) {// 报警
								EquipService equipservice = new EquipService();
								objbean.setImage("image/topo/" + equipservice.getAlarmImage(vo.getEquipId()));
							} else {
								EquipService equipservice = new EquipService();
								objbean.setImage("image/topo/" + equipservice.getTopoImage(vo.getEquipId()));
							}
						} else {
							if (alarmLevel > 0 || ismapalarm > 0) { // 报警
								objbean.setImage(NodeHelper.getServerAlarmImage(node.getSysOid()));
							} else {
								objbean.setImage(NodeHelper.getServerTopoImage(node.getSysOid()));
							}
						}

					} else {
						if (vo != null) {
							if (alarmLevel > 0 || ismapalarm > 0) { // 报警
								EquipService equipservice = new EquipService();
								objbean.setImage("image/topo/" + equipservice.getAlarmImage(vo.getEquipId()));
							} else {
								EquipService equipservice = new EquipService();
								objbean.setImage("image/topo/" + equipservice.getTopoImage(vo.getEquipId()));
							}
						} else {
							if (alarmLevel > 0) {// 设备本身报警
								objbean.setImage(NodeHelper.getAlarmImage(objbean.getCategory()));
							} else if (ismapalarm > 0) { // 关联子图报警
								objbean.setImage(NodeHelper.getAlarmImage1(objbean.getCategory()));
							} else {
								objbean.setImage(NodeHelper.getTopoImage(objbean.getCategory()));
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return obj;
	}

	// 保存关联拓扑图的节点
	private String relationMap() {
		String fileName = (String) getParaValue("xml");
		RelationDao dao = new RelationDao();
		Relation vo = new Relation();
		vo.setXmlName(fileName);
		vo.setNodeId(getParaValue("nodeId"));// 节点id
		vo.setCategory(getParaValue("category"));// 节点类型
		vo.setMapId(getParaValue("radio"));// 子图的id
		Relation vo1 = (Relation) dao.findByNodeId(getParaValue("nodeId"), fileName);
		if (vo1 != null) {
			vo.setId(vo1.getId());
			update(dao, vo);
		} else {
			save(dao, vo);
		}
		ManageXmlDao mdao = new ManageXmlDao();
		ManageXml manageXml = (ManageXml) mdao.findByID(getParaValue("radio"));
		ManageXmlOperator mXmlOpr = new ManageXmlOperator();
		mXmlOpr.setFile(fileName);
		mXmlOpr.init4updateXml();
		if (mXmlOpr.isNodeExist(getParaValue("nodeId"))) {
			mXmlOpr.updateNode(getParaValue("nodeId"), "relationMap", manageXml.getXmlName());
		}
		mXmlOpr.writeXml();
		mdao.close();
		return null;
	}

	// 从拓扑图移除实体设备
	private String removeEquipFromSubMap() {
		String xmlName = getParaValue("xml");
		String node = getParaValue("node");
		if (xmlName.indexOf("businessmap") != -1) {
			LineDao lineDao = new LineDao();
			lineDao.deleteByidXml(node, xmlName);
			NodeDependDao nodeDependDao = new NodeDependDao();
			if (nodeDependDao.isNodeExist(node, xmlName)) {
				nodeDependDao.deleteByIdXml(node, xmlName);
			} else {
				nodeDependDao.close();
			}

			User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
			ManageXmlDao mXmlDao = new ManageXmlDao();
			List xmlList = new ArrayList();
			try {
				xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mXmlDao.close();
			}
			try {
				ChartXml chartxml;
				chartxml = new ChartXml("tree");
				chartxml.addViewTree(xmlList);
			} catch (Exception e) {
				e.printStackTrace();
			}

			ManageXmlDao subMapDao = new ManageXmlDao();
			ManageXml manageXml = (ManageXml) subMapDao.findByXml(xmlName);
			if (manageXml != null) {
				NodeDependDao nodeDepenDao = new NodeDependDao();
				try {
					List list = nodeDepenDao.findByXml(xmlName);
					ChartXml chartxml;
					chartxml = new ChartXml("NetworkMonitor", "/" + xmlName.replace("jsp", "xml"));
					chartxml.addBussinessXML(manageXml.getTopoName(), list);
					ChartXml chartxmlList;
					chartxmlList = new ChartXml("NetworkMonitor", "/" + xmlName.replace("jsp", "xml").replace("businessmap", "list"));
					chartxmlList.addListXML(manageXml.getTopoName(), list);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					nodeDepenDao.close();
				}
			}
		}
		ManageXmlOperator mxmlOpr = new ManageXmlOperator();
		mxmlOpr.setFile(xmlName);
		mxmlOpr.init4editNodes();
		if (mxmlOpr.isIdExist(node)) {
			mxmlOpr.deleteNodeByID(node);
		}
		mxmlOpr.writeXml();
		return "/" + xmlName;
	}

	// 替换实体设备图元图片
	private String replaceEquipPic() {
		String fileName = "";
		String equipName = "";
		String imageId = "";
		String equipType = "";
		String nodeid = getParaValue("nodeId");
		String nodeId = nodeid.substring(3);
		String returnValue[] = getParaArrayValue("returnValue");
		String arr[] = returnValue[0].split(",");
		if (arr.length == 4) {
			try {
				equipName = new String(arr[0].getBytes("ISO-8859-1"), "gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			imageId = arr[1];
			fileName = arr[2];
			equipType = arr[3];
		}
		String imgPath = "";
		if (!"".equals(imageId)) {
			EquipImageDao equipImageDao = new EquipImageDao();
			EquipImage equipImage = (EquipImage) equipImageDao.findImageById(Integer.parseInt(imageId));
			imgPath = equipImage.getPath();
			equipImageDao.close();
		}
		if (!"".equals(equipName) && !"".equals(nodeId) && !"".equals(fileName) && !"".equals(equipType)) {
			TreeNodeDao treeNodeDao = new TreeNodeDao();
			TreeNode vo = (TreeNode) treeNodeDao.findByName(equipType);
			// 更新数据库
			if (vo != null && vo.getTableName() != null && !"".equals(vo.getTableName())) {
				CommonDao dao = new CommonDao(vo.getTableName());
				dao.updateAliasById(equipName, nodeId);
			}
			// 更新内存
			Node node = PollingEngine.getInstance().getNodeByCategory(equipType, Integer.parseInt(nodeId));
			if (node != null) {// 内存中有，更新内存
				node.setAlias(equipName);
				node.setManaged(true);
			} else {
				return "";
			}

			if (!"".equals(imageId)) {

				NodeEquipDao nodeEquipDao = new NodeEquipDao();
				NodeEquip Vo = (NodeEquip) nodeEquipDao.findByNodeAndXml(nodeid, fileName);
				NodeEquip VO = new NodeEquip();
				VO.setEquipId(Integer.parseInt(imageId));
				VO.setNodeId(nodeid);
				VO.setXmlName(fileName);
				if (Vo == null) {
					save(nodeEquipDao, VO);
				} else {
					VO.setId(Vo.getId());
					update(nodeEquipDao, VO);
				}
				nodeEquipDao = new NodeEquipDao();
				Hashtable nodeequiphash = new Hashtable();
				try {
					List nodeequiplist = nodeEquipDao.loadAll();
					if (nodeequiplist != null && nodeequiplist.size() > 0) {
						for (int i = 0; i < nodeequiplist.size(); i++) {
							NodeEquip nodeequip = (NodeEquip) nodeequiplist.get(i);
							nodeequiphash.put(nodeequip.getNodeId() + ":" + nodeequip.getXmlName(), nodeequip);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					nodeEquipDao.close();
				}
				ShareData.setAllnodeequps(nodeequiphash);
			}

			ManageXmlOperator mXmlOpr = new ManageXmlOperator();
			mXmlOpr.setFile(fileName);
			mXmlOpr.init4updateXml();
			if (mXmlOpr.isNodeExist(nodeid)) {
				if (!"".equals(imgPath)) {
					String sizeString = getImageSize(ResourceCenter.getInstance().getSysPath() + "resource/" + imgPath.substring(17));
					mXmlOpr.updateNode(nodeid, "width", sizeString.split(":")[0]);
					mXmlOpr.updateNode(nodeid, "height", sizeString.split(":")[1]);
					mXmlOpr.updateNode(nodeid, "img", imgPath.substring(17));
				}
				mXmlOpr.updateNode(nodeid, "alias", equipName);
			}
			mXmlOpr.writeXml();
		}

		return null;
	}

	// 替换示意设备图片
	private String replacePic() {
		String iconPath[] = {};
		String nodeId = getParaValue("nodeId");
		String fileName = (String) getParaValue("xml");
		String returnValue[] = getParaArrayValue("returnValue");
		String equipName = "";

		iconPath = returnValue[0].split(",");
		if (iconPath.length == 2) {
			try {
				equipName = new String(iconPath[0].getBytes("ISO-8859-1"), "gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			ManageXmlOperator mXmlOpr = new ManageXmlOperator();
			mXmlOpr.setFile(fileName);
			mXmlOpr.init4updateXml();
			if (mXmlOpr.isNodeExist(nodeId)) {
				if (iconPath[1] != null && !"".equals(iconPath[1])) {
					String sizeString = getImageSize(ResourceCenter.getInstance().getSysPath() + "resource/" + iconPath[1].substring(17));
					mXmlOpr.updateNode(nodeId, "img", iconPath[1].substring(17));
					mXmlOpr.updateNode(nodeId, "width", sizeString.split(":")[0]);
					mXmlOpr.updateNode(nodeId, "height", sizeString.split(":")[1]);
				}
				mXmlOpr.updateNode(nodeId, "alias", equipName);
			}
			mXmlOpr.writeXml();
		}
		return null;
	}

	private String resume() {
		String radio = getParaValue("radio");
		String filename = getParaValue("xml");
		try {
			copyFile(radio, filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 子图上的保存按钮，保存子图上图元的位置
	private String save() {
		String fileName = (String) session.getAttribute(SessionConstant.CURRENT_SUBMAP_VIEW);
		String xmlString = request.getParameter("hidXml");// 从showMap.jsp传入的数据信息字符串
		xmlString = xmlString.replace("<?xml version=\"1.0\"?>", "<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		XmlOperator xmlOpr = new XmlOperator();
		xmlOpr.setFile(fileName);
		xmlOpr.saveImage(xmlString);
		saveBusXML(fileName);// 保存业务视图文件
		request.setAttribute("fresh", "fresh");
		return "/topology/submap/save.jsp";
	}

	// 保存业务视图xml文件
	private String saveBusXML(String filename) {
		if (filename != null && !"".equalsIgnoreCase(filename) && filename.indexOf("businessmap") != -1) {
			NodeDependDao nodeDependDao = null;
			nodeDependDao = new NodeDependDao();
			List list = nodeDependDao.findByXml(filename);
			ManageXmlOperator mXmlOpr = new ManageXmlOperator();
			mXmlOpr.setFile(filename);
			mXmlOpr.init4updateXml();
			Hashtable hash = mXmlOpr.getAllXY();
			if (hash != null && hash.size() > 0) {
				for (int i = 0; i < hash.size(); i++) {
					for (int j = 0; j < list.size(); j++) {
						NodeDepend nvo = (NodeDepend) list.get(j);
						if (hash.get(nvo.getNodeId()) != null) {
							nodeDependDao = new NodeDependDao();
							nodeDependDao.updateById(nvo.getNodeId(), (String) hash.get(nvo.getNodeId()), filename);
							nodeDependDao.close();
						}
					}
				}
			}

			ManageXmlDao subMapDao = new ManageXmlDao();
			ManageXml vo = null;
			try {
				vo = (ManageXml) subMapDao.findByXml(filename);
			} catch (RuntimeException e1) {
				e1.printStackTrace();
			} finally {
				subMapDao.close();
			}
			nodeDependDao = new NodeDependDao();
			String xml = filename.replace("jsp", "xml");
			try {
				List lists = nodeDependDao.findByXml(filename);
				ChartXml chartxml;
				chartxml = new ChartXml("NetworkMonitor", "/" + xml);
				chartxml.addBussinessXML(vo.getTopoName(), lists);
				ChartXml chartxmlList;
				chartxmlList = new ChartXml("NetworkMonitor", "/" + xml.replace("businessmap", "list"));
				chartxmlList.addListXML(vo.getTopoName(), lists);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodeDependDao.close();
			}
		}
		return "";
	}

	// 保存链路样式
	private String saveLinkProperty() {

		String lineId = getParaValue("lineId");
		String fileName = getParaValue("xml");
		String link_name = getParaValue("link_name");
		String link_width = getParaValue("link_width");
		String linkAliasName = link_name;
		if ("".equals(linkAliasName.trim())) {
			linkAliasName = "#.#";
		}
		ManageXmlOperator mXmlOpr = new ManageXmlOperator();
		mXmlOpr.setFile(fileName);
		mXmlOpr.init4updateXml();
		if (mXmlOpr.isLinkExist(lineId)) {
			mXmlOpr.updateLine(lineId, "lineWidth", link_width);
			mXmlOpr.updateLine(lineId, "alias", linkAliasName);
		} else if (mXmlOpr.isAssLinkExist(lineId)) {
			mXmlOpr.updateAssLine(lineId, "lineWidth", link_width);
			mXmlOpr.updateAssLine(lineId, "alias", linkAliasName);
		} else if (mXmlOpr.isDemoLinkExist(lineId)) {
			mXmlOpr.updateDemoLine(lineId, "lineWidth", link_width);
			mXmlOpr.updateDemoLine(lineId, "alias", linkAliasName);
			if (!"".equals(link_name)) {
				mXmlOpr.updateDemoLine(lineId, "lineInfo", link_name);
			}
		}
		mXmlOpr.writeXml();
		return null;
	}

	private synchronized String saveSubMap() {

		User uservo = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		ManageXml vo = new ManageXml();
		String xmlName = SysUtil.getCurrentLongTime() + ".jsp";
		if (Integer.parseInt(getParaValue("topo_type")) == 4) {
			xmlName = "submap" + xmlName;
		} else if (Integer.parseInt(getParaValue("topo_type")) == 3) {
			xmlName = "breviarymap" + xmlName;
		} else if (Integer.parseInt(getParaValue("topo_type")) == 2) {
			xmlName = "hintmap" + xmlName;
		} else if (Integer.parseInt(getParaValue("topo_type")) == 1) {
			xmlName = "businessmap" + xmlName;
		} else {
			xmlName = "topmap" + xmlName;
		}
		ManageXmlDao mxDao = new ManageXmlDao();
		if (Integer.parseInt(getParaValue("topo_type")) == 1) {
			mxDao.updateBusView(uservo.getBusinessids());
		} else {
			mxDao.updateView(uservo.getBusinessids());
		}
		mxDao.close();
		String bid = ",";
		String arr[] = getParaArrayValue("checkbox");
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				bid = bid + arr[i] + ",";
			}
		}
		vo.setXmlName(xmlName);
		vo.setTopoName(getParaValue("topo_name"));
		vo.setAliasName(getParaValue("alias_name"));
		vo.setTopoTitle(getParaValue("topo_title"));
		vo.setTopoArea(getParaValue("topo_area"));
		vo.setTopoBg(getParaValue("topo_bg"));
		vo.setTopoType(Integer.parseInt(getParaValue("topo_type")));
		if (getParaValue("home_view") != null && Integer.parseInt(getParaValue("home_view")) == 1) {
			if (Integer.parseInt(getParaValue("topo_type")) == 1) {
				vo.setBus_home_view(1);
				vo.setHome_view(0);
			} else {
				vo.setHome_view(1);
				vo.setBus_home_view(0);
			}
			vo.setPercent(Float.parseFloat(getParaValue("zoom_percent")));
		} else {
			vo.setBus_home_view(0);
			vo.setHome_view(0);
			vo.setPercent(1);
		}
		vo.setBid(bid);
		DaoInterface dao = new ManageXmlDao();
		save(dao, vo);

		User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		ManageXmlDao mXmlDao = new ManageXmlDao();
		List xmlList = new ArrayList();
		try {
			xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mXmlDao.close();
		}
		try {
			ChartXml chartxml;
			chartxml = new ChartXml("tree");
			chartxml.addViewTree(xmlList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 将业务节点加到内存中
		if (Integer.parseInt(getParaValue("topo_type")) == 1) {
			ManageXmlDao manageXmlDao = new ManageXmlDao();
			ManageXml manageXml = (ManageXml) manageXmlDao.findByXml(xmlName);
			Bussiness bus = new Bussiness();
			bus.setId(manageXml.getId());
			bus.setBid(manageXml.getBid());
			bus.setName(manageXml.getTopoName());
			bus.setAlias(manageXml.getTopoName());
			bus.setCategory(80);
			bus.setStatus(0);
			bus.setType("业务");
			PollingEngine.getInstance().addBus(bus);
		}
		String[] values = getParaValue("objEntityStr").split(",");
		String[] lineValues = getParaValue("linkStr").split(";");
		String[] asslineValues = getParaValue("asslinkStr").split(";");

		XmlOperator xmlOpr = new XmlOperator();
		xmlOpr.setFile(vo.getXmlName());
		xmlOpr.init4createXml();
		xmlOpr.createXml();
		xmlOpr.writeXml();

		if (values.length > 0 && !"".equals(values[0])) {

			ManageXmlOperator mxmlOpr = new ManageXmlOperator();
			mxmlOpr.setFile(xmlName);
			// 保存节点信息
			mxmlOpr.init4editNodes();
			int index = 0;
			for (int i = 0; i < values.length; i++) {
				if (!mxmlOpr.isNodeExist(values[i])) {
					mxmlOpr.addNode(values[i], ++index);
				}
			}
			mxmlOpr.deleteNodes();
			mxmlOpr.writeXml();
			// 保存链路信息
			LinkDao linkDao = new LinkDao();
			mxmlOpr.init4editLines();
			if (lineValues.length > 0 && !"".equals(lineValues[0])) {
				for (int i = 0; i < lineValues.length; i++) {
					String line[] = lineValues[i].split(",");
					int loc = line[0].indexOf("_");
					int tag = 0;
					String id1 = line[0].substring(0, loc);
					String id2 = line[0].substring(loc + 1);
					String tempValues = id2 + "_" + id1 + "," + line[1];
					for (int j = i + 1; j < lineValues.length; j++) {
						if (lineValues[j].equals(tempValues) || lineValues[j].equals(lineValues[i])) {
							tag++;
						}
					}
					if (tag == 0) {
						Link link = (Link) linkDao.findByID(line[1]);
						String startid = "";
						String endid = "";
						if (id1.indexOf(String.valueOf(link.getStartId())) != -1) {
							startid = id1;
							endid = id2;
						} else {
							startid = id2;
							endid = id1;
						}
						mxmlOpr.addLine(link.getLinkName(), String.valueOf(line[1]), startid, endid);
					}
				}
			}
			if (asslineValues.length > 0 && !"".equals(asslineValues[0])) {
				for (int i = 0; i < asslineValues.length; i++) {
					String line[] = asslineValues[i].split(",");
					int loc = line[0].indexOf("_");
					int tag = 0;
					String id1 = line[0].substring(0, loc);
					String id2 = line[0].substring(loc + 1);
					String tempValues = id2 + "_" + id1 + "," + line[1];
					for (int j = i + 1; j < asslineValues.length; j++) {
						if (asslineValues[j].equals(tempValues) || asslineValues[j].equals(asslineValues[i])) {
							tag++;
						}
					}
					if (tag == 0) {
						Link link = (Link) linkDao.findByID(line[1]);
						String startid = "";
						String endid = "";
						if (id1.indexOf(String.valueOf(link.getStartId())) != -1) {
							startid = id1;
							endid = id2;
						} else {
							startid = id2;
							endid = id1;
						}
						mxmlOpr.addAssistantLine(link.getLinkName(), String.valueOf(line[1]), startid, endid);
					}
				}

			}
			mxmlOpr.writeXml();
			linkDao.close();
		}
		return null;
	}

	private String saveTree() {
		String[] ids = getParaArrayValue("check");
		String topoid = getParaValue("topoid");
		String sindex = getParaValue("sindex");
		String filename = getParaValue("filename");
		if (sindex == null) {
			sindex = "";
		}
		List<IndicatorsTopoRelation> relationlist = new ArrayList<IndicatorsTopoRelation>();
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				IndicatorsTopoRelation indicatorsTopoRelation = new IndicatorsTopoRelation();
				indicatorsTopoRelation.setIndicatorsId(ids[i].split(":")[0]);
				indicatorsTopoRelation.setNodeid(ids[i].split(":")[1]);
				indicatorsTopoRelation.setSIndex(sindex);
				indicatorsTopoRelation.setTopoId(topoid);
				relationlist.add(indicatorsTopoRelation);
			}
		}

		IndicatorsTopoRelationDao indicatorsTopoRelationDao = new IndicatorsTopoRelationDao();
		try {
			indicatorsTopoRelationDao.deleteByTopoId(topoid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsTopoRelationDao.close();
		}

		indicatorsTopoRelationDao = new IndicatorsTopoRelationDao();
		try {
			indicatorsTopoRelationDao.save(relationlist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsTopoRelationDao.close();
		}

		indicatorsTopoRelationDao = new IndicatorsTopoRelationDao();
		Hashtable tophash = new Hashtable();
		try {
			List list = indicatorsTopoRelationDao.loadAll();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					IndicatorsTopoRelation relation = (IndicatorsTopoRelation) list.get(i);
					if (tophash.containsKey(relation.getTopoId() + ":" + relation.getNodeid())) {
						((List) tophash.get(relation.getTopoId() + ":" + relation.getNodeid())).add(relation);
					} else {
						List tlist = new ArrayList();
						tlist.add(relation);
						tophash.put(relation.getTopoId() + ":" + relation.getNodeid(), tlist);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsTopoRelationDao.close();
		}
		ShareData.setToprelation(tophash);

		request.setAttribute("topoid", topoid);
		request.setAttribute("sindex", sindex);
		request.setAttribute("filename", filename);
		request.setAttribute("fresh", "fresh");
		return "/topology/network/isave.jsp";
	}

	// 设备面板图展示
	private String showPanel() {
		String target = "";
		String ip = getParaValue("ip");
		// 更新设备面板图 HONGLI ADD
		UpdateXmlTaskTest updateXmlTaskTest = UpdateXmlTaskTest.getInstance();
		updateXmlTaskTest.updatePanel(ip);
		// 更新设备面板图 END
		IpaddressPanelDao ipaddressPanelDao = new IpaddressPanelDao();
		IpaddressPanel panel = ipaddressPanelDao.loadIpaddressPanel(ip);
		Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
		if (panel == null) {
			target = "/panel/view/blank.jsp";
		} else {
			String filename = SysUtil.doip(ip);
			String oid = host.getSysOid();
			target = "/panel/view/custom.jsp?filename=" + filename + "&oid=" + oid + "&imageType=" + panel.getImageType();
		}
		return target;
	}

	// 分类展现设备树
	private String showTree() {
		String treeFlag = getParaValue("treeFlag");
		String equiptype = getParaValue("equiptype");
		String typeStr = "";
		TreeNodeDao treeNodeDao = new TreeNodeDao();
		TreeNode vo = (TreeNode) treeNodeDao.findByName(equiptype);
		List nodeList = PollingEngine.getInstance().getAllTypeMap().get(equiptype);
		List<Node> list = new ArrayList<Node>();
		if (nodeList != null && nodeList.size() > 0) {
			if (equiptype.indexOf("net") == 0) {// 网络设备的情况下
				if (vo != null && vo.getCategory() != null && !"".equals(vo.getCategory())) {
					String cate[] = vo.getCategory().split(",");
					if (cate.length > 0) {
						for (int i = 0; i < nodeList.size(); i++) {
							Node node = (Node) nodeList.get(i);
							for (int j = 0; j < cate.length; j++) {
								if (node.getCategory() == Integer.parseInt(cate[j])) {
									list.add(node);
								}
							}
						}
					}
				}
			} else {
				for (int i = 0; i < nodeList.size(); i++) {
					Node node = (Node) nodeList.get(i);
					list.add(node);
				}
			}
		}
		if (vo != null) {
			typeStr = vo.getText();
		}
		request.setAttribute("equiptype", equiptype);
		request.setAttribute("typeStr", typeStr);
		request.setAttribute("nodeList", list);
		return "/topology/network/tree.jsp?treeflag=" + treeFlag;

	}
}
