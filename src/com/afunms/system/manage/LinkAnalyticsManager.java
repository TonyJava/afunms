package com.afunms.system.manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.LinkSnmpUtil;
import com.afunms.common.util.SnmpUtil;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.discovery.AtInterface;
import com.afunms.discovery.BridgeStpInterface;
import com.afunms.discovery.CdpCachEntryInterface;
import com.afunms.discovery.DiscoverComplete_SOLO;
import com.afunms.discovery.DiscoverDataHelper;
import com.afunms.discovery.DiscoverEngine;
import com.afunms.discovery.DiscoverInitialize;
import com.afunms.discovery.DiscoverMonitor;
import com.afunms.discovery.DiscoverResource;
import com.afunms.discovery.Host;
import com.afunms.discovery.IPRouterProbeThread_SOLO_Runable;
import com.afunms.discovery.IfEntity;
import com.afunms.discovery.IpAddress;
import com.afunms.discovery.IpRouter;
import com.afunms.discovery.Link;
import com.afunms.discovery.MacToNodeLink;
import com.afunms.discovery.NodeToNodeLink;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.task.ThreadPool;
import com.afunms.topology.dao.DiscoverCompleteDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class LinkAnalyticsManager extends BaseManager implements ManagerInterface {

	private static List hostlist = new ArrayList();
	private static List linkList = new ArrayList();

	private static List routelinkList = new ArrayList();

	private static List maclinkList = new ArrayList();

	/**
	 * 创建任务
	 */
	private static Runnable createTask(final HostNode hostnode) {
		return new Runnable() {
			public void run() {
				try {

					// 获取路由表
					List routerList = SnmpUtil.getInstance().getRouterList(hostnode.getIpAddress(), hostnode.getCommunity());
					// MAC地址表
					List ipNetTable = SnmpUtil.getInstance().getIpNetToMediaTable(hostnode.getIpAddress(), hostnode.getCommunity());
					// NDP地址表
					Hashtable ndphash1 = SnmpUtil.getInstance().getNDPTable(hostnode.getIpAddress(), hostnode.getCommunity());
					// CDP地址表
					List cdplist1 = SnmpUtil.getInstance().getCiscoCDPList(hostnode.getIpAddress(), hostnode.getCommunity());
					// STP地址表
					List stplist1 = SnmpUtil.getInstance().getBridgeStpList(hostnode.getIpAddress(), hostnode.getCommunity());
					// AT地址表
					List atlist1 = SnmpUtil.getInstance().getAtInterfaceTable(hostnode.getIpAddress(), hostnode.getCommunity());

					List ifEntityList1 = SnmpUtil.getInstance().getIfEntityList(hostnode.getIpAddress(), hostnode.getCommunity(), hostnode.getCategory());

					Host host = new Host();
					host.setId(hostnode.getId());
					host.setIpAddress(hostnode.getIpAddress());
					host.setCommunity(hostnode.getCommunity());
					host.setCategory(hostnode.getCategory());
					host.setSysOid(hostnode.getSysOid());

					host.setRouteList(routerList);
					host.setIpNetTable(ipNetTable);
					host.setNdpHash(ndphash1);
					host.setCdpList(cdplist1);
					host.setAtInterfaces(atlist1);
					host.setBridgestpList(stplist1);
					host.setIfEntityList(ifEntityList1);
					hostlist.add(host);
				} catch (Exception exc) {

				}
			}
		};
	}

	/**
	 * 创建任务
	 */
	private static Runnable createTask(final int id, final String ip, final int version, final String commu) {
		return new Runnable() {
			public void run() {
				try {
					Host host1 = new Host();
					host1.setId(id);
					host1.setIpAddress(ip);
					host1.setCommunity(commu);

					LinkSnmpUtil snmputil1 = new LinkSnmpUtil();
					String sysOid = "";
					int deviceType = 0;
					sysOid = snmputil1.getSysOid(ip, commu);
					deviceType = snmputil1.checkDevice(ip, commu, sysOid);
					host1.setCategory(deviceType);
					host1.setSysOid(sysOid);

					// 获取路由表
					List routerlist1 = snmputil1.getIPRouterTable(ip, commu);
					// MAC地址表
					List maclist1 = snmputil1.getIpNetToMediaTable(ip, commu);
					// NDP地址表
					Hashtable ndphash1 = snmputil1.getNDPTable(ip, commu);
					// CDP地址表
					List cdplist1 = snmputil1.getCiscoCDPList(ip, commu);
					// STP地址表
					List stplist1 = snmputil1.getBridgeStpList(ip, commu);
					// AT地址表
					List atlist1 = snmputil1.getAtInterfaceTable(ip, commu);

					List ifEntityList1 = snmputil1.getIfEntityList(ip, commu, host1.getCategory());

					host1.setRouteList(routerlist1);
					host1.setIpNetTable(maclist1);
					host1.setNdpHash(ndphash1);
					host1.setCdpList(cdplist1);
					host1.setAtInterfaces(atlist1);
					host1.setBridgestpList(stplist1);
					host1.setIfEntityList(ifEntityList1);
					hostlist.add(host1);
				} catch (Exception exc) {

				}
			}
		};
	}

	private Snmp snmp = null;

	private Address targetAddress = null;

	private List<NodeToNodeLink> links = new ArrayList<NodeToNodeLink>();

	private List<MacToNodeLink> maclinks = new ArrayList<MacToNodeLink>();

	private HashMap<Integer, Host> bridgeNodes = new HashMap<Integer, Host>();

	private List<Host> routerNodes = new ArrayList<Host>();

	private List<Host> cdpNodes = new ArrayList<Host>();

	private List<Host> ndpNodes = new ArrayList<Host>();
	private List<Host> atNodes = new ArrayList<Host>();

	private List<String> macsParsed = new ArrayList<String>();

	private List<String> macsExcluded = new ArrayList<String>();

	private Map<String, List<AtInterface>> macToAtinterface = new HashMap<String, List<AtInterface>>();

	private void addLinks(Set<String> macs, int nodeid, int ifindex) {
		if (macs == null || macs.isEmpty()) {
		} else {
			Iterator<String> mac_ite = macs.iterator();

			while (mac_ite.hasNext()) {
				String curMacAddress = mac_ite.next();
				if (macsParsed.contains(curMacAddress)) {
					continue;
				}

				if (macsExcluded.contains(curMacAddress)) {
					continue;
				}

				if (macToAtinterface.containsKey(curMacAddress)) {
					List<AtInterface> ats = macToAtinterface.get(curMacAddress);
					Iterator<AtInterface> ite = ats.iterator();
					while (ite.hasNext()) {
						AtInterface at = ite.next();

						NodeToNodeLink lNode = new NodeToNodeLink(at.getNodeId(), at.getIfindex());
						lNode.setNodeparentid(nodeid);
						lNode.setParentifindex(ifindex);

						addNodetoNodeLink(lNode);
					}
				} else {
					MacToNodeLink lMac = new MacToNodeLink(curMacAddress);
					lMac.setNodeparentid(nodeid);
					lMac.setParentifindex(ifindex);
					maclinks.add(lMac);
				}
				macsParsed.add(curMacAddress);
			}
		}
	}

	private void addNodetoNodeLink(NodeToNodeLink nnlink) {
		if (nnlink == null) {
			return;
		}
		if (!links.isEmpty()) {
			Iterator<NodeToNodeLink> ite = links.iterator();
			while (ite.hasNext()) {
				NodeToNodeLink curNnLink = ite.next();
				if (curNnLink.equals(nnlink)) {
					return;
				}
			}
		}

		int assitantLink = countNodetoNodeLink(nnlink);
		if (assitantLink == 0) {
			// 不存在该连接,则添加
			links.add(nnlink);
		} else if (assitantLink == 1) {
			// 已经存在一条,则要把该连接设置为辅助连接
			nnlink.setAssistant(1);
			links.add(nnlink);
		} else {
			return;
		}
	}

	private void analyseTopoLinks() {
		Iterator<Host> ite = null;

		if (hostlist != null && hostlist.size() > 0) {
			for (int i = 0; i < hostlist.size(); i++) {
				try {
					Host host = (Host) hostlist.get(i);
					if (host == null) {
						continue;
					}
					// 过滤掉非网络设备
					if (host.getCategory() != 1 && host.getCategory() != 2 && host.getCategory() != 3 && host.getCategory() != 7) {
						continue;
					}
					if (host.getBridgestpList() != null && host.getBridgestpList().size() > 0) {
						bridgeNodes.put(new Integer(host.getId()), host);
					}
					if (host.getCdpList() != null && host.getCdpList().size() > 0) {
						cdpNodes.add(host);
					}
					if (host.getNdpHash() != null && host.getNdpHash().size() > 0) {
						ndpNodes.add(host);
					}
					if (host.getRouteList() != null && host.getRouteList().size() > 0) {
						routerNodes.add(host);
					}

					if (host.getAtInterfaces() != null && host.getAtInterfaces().size() > 0) {
						List atInterfaces = host.getAtInterfaces();
						List atList = new ArrayList();
						for (int k = 0; k < atInterfaces.size(); k++) {
							AtInterface at = (AtInterface) atInterfaces.get(k);
							AtInterface _at = new AtInterface(host.getId(), at.getIpAddress(), at.getMacAddress(), at.getIfindex());
							atList.add(_at);
						}
						if (atList != null && atList.size() > 0) {
							host.setAtInterfaces(atList);
						}
						atNodes.add(host);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		ite = atNodes.iterator();
		while (ite.hasNext()) {
			Host host = ite.next();
			List atInterfaces = host.getAtInterfaces();
			if (atInterfaces != null && atInterfaces.size() > 0) {
				for (int k = 0; k < atInterfaces.size(); k++) {
					try {
						AtInterface at = (AtInterface) atInterfaces.get(k);
						String macAddress = at.getMacAddress();
						if (isMacIdentifierOfBridgeNode(macAddress)) {
							macsExcluded.add(macAddress);
							continue;
						}
						List<AtInterface> ats = macToAtinterface.get(macAddress);
						if (ats == null) {
							ats = new ArrayList<AtInterface>();
						}
						ats.add(at);
						macToAtinterface.put(macAddress, ats);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			try {
				Host curNode = ite.next();
				List curNodeStpList = curNode.getBridgestpList();
				if (curNodeStpList != null && curNodeStpList.size() > 0) {
					for (int k = 0; k < curNodeStpList.size(); k++) {
						try {
							BridgeStpInterface bstp = (BridgeStpInterface) curNodeStpList.get(k);
							if (curNode.isBridgeIdentifier(bstp.getBridge().substring(5))) {
								SysLogger.info("运行: STP designated root is the bridge itself. Skipping");
								continue;
							}
							Host designatedNode = getNodeFromMacIdentifierOfBridgeNode(bstp.getBridge().substring(5));
							if (designatedNode == null) {
								continue;
							}
							// if port is a backbone port continue
							SysLogger.info(curNode.getIpAddress() + "   Port " + bstp.getPort());
							if (curNode.isBackBoneBridgePort(Integer.parseInt(bstp.getPort()))) {
								SysLogger.info("运行: bridge port " + bstp.getPort() + " already found .... Skipping");
								continue;
							}
							String stpPortDesignatedPort = bstp.getBridgeport();
							stpPortDesignatedPort = stpPortDesignatedPort.replace(":", "");
							SysLogger.info(curNode.getIpAddress() + "   designatedbridgeport " + Integer.parseInt(stpPortDesignatedPort.substring(1), 16));
							int designatedbridgeport = Integer.parseInt(stpPortDesignatedPort.substring(1), 16);

							int designatedifindex = -1;
							if (designatedNode.getIfEntityByPort(designatedbridgeport + "") != null) {
								designatedifindex = Integer.parseInt(designatedNode.getIfEntityByPort(designatedbridgeport + "").getIndex());
							} else {
								SysLogger.info("运行: got invalid ifindex on designated node");
								continue;
							}

							if (designatedifindex == -1 || designatedifindex == 0) {
								SysLogger.info("运行: got invalid ifindex on designated node");
								continue;
							}

							SysLogger.info("run: backbone port found for node " + curNode.getId() + ". Adding to bridge" + bstp.getPort());

							curNode.addBackBoneBridgePorts(Integer.parseInt(bstp.getPort()));
							bridgeNodes.put(new Integer(curNode.getId()), curNode);

							SysLogger.info("run: backbone port found for node " + designatedNode.getId() + " .Adding to helper class bb port " + " bridge port "
									+ designatedbridgeport);

							// test if there are other bridges between this link
							// USING MAC ADDRESS FORWARDING TABLE

							if (!isNearestBridgeLink(curNode, Integer.parseInt(bstp.getPort()), designatedNode, designatedbridgeport)) {
								SysLogger.info("run: other bridge found between nodes. No links to save!");
								continue; // no saving info if no nodeid
							}
							int curIfIndex = Integer.parseInt(curNode.getIfEntityByPort(bstp.getPort() + "").getIndex());

							if (curIfIndex == -1 || curIfIndex == 0) {
								SysLogger.info("运行: got invalid ifindex");
								continue;
							}
							designatedNode.addBackBoneBridgePorts(designatedbridgeport);
							bridgeNodes.put(new Integer(designatedNode.getId()), designatedNode);

							SysLogger.info("run: adding links on bb bridge port " + designatedbridgeport);

							addLinks(getMacsOnBridgeLink(curNode, Integer.parseInt(bstp.getPort()), designatedNode, designatedbridgeport), curNode.getId(), curIfIndex);

							// writing to db using class
							// DbDAtaLinkInterfaceEntry
							NodeToNodeLink lk = new NodeToNodeLink(curNode.getId(), curIfIndex);
							lk.setFindtype(SystemConstant.ISBridge);
							lk.setNodeparentid(designatedNode.getId());
							lk.setParentifindex(designatedifindex);
							addNodetoNodeLink(lk);
						} catch (Exception e) {
							e.printStackTrace();
							SysLogger.error(e.getMessage());
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				SysLogger.error(e.getMessage());
			}
		}

		// fourth find inter router links,
		// this part could have several special function to get inter router
		// links, but at the moment we worked much on switches.
		// In future we can try to extend this part.
		SysLogger.info("运行: try to found  not ethernet links on Router nodes");

		// List routeLinkList = DiscoverEngine.getInstance().getRouteLinkList();
		if (routelinkList != null && routelinkList.size() > 0) {
			for (int k = 0; k < routelinkList.size(); k++) {
				try {
					Link link = (Link) routelinkList.get(k);
					// Saving link also when ifindex = -1 (not found)
					NodeToNodeLink lk = new NodeToNodeLink(link.getEndId(), Integer.parseInt(link.getEndIndex()));
					lk.setFindtype(SystemConstant.ISRouter);
					lk.setNodeparentid(link.getStartId());
					lk.setParentifindex(Integer.parseInt(link.getStartIndex()));
					SysLogger.info("添加连接: ##########################");
					SysLogger.info("添加连接: " + link.getStartIp() + " --- " + link.getEndIp());
					addNodetoNodeLink(lk);
				} catch (Exception e) {
					e.printStackTrace();
					SysLogger.error(e.getMessage());
				}
			}
		}
		// 将maclinklist中有连接,而在上面的CDP/NDP/STP/ROUTER计算中没有的连接加进去
		// List macLinks = DiscoverEngine.getInstance().getMacLinkList();
		if (maclinkList != null && maclinkList.size() > 0) {
			for (int k = 0; k < maclinkList.size(); k++) {
				try {
					Link maclink = (Link) maclinkList.get(k);
					if (!NodeToNodeLinkExist(maclink)) {
						// 若不存在该连接,则添加进去
						NodeToNodeLink lk = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						lk.setFindtype(SystemConstant.ISMac);
						lk.setNodeparentid(maclink.getStartId());
						lk.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						SysLogger.info("添加连接: ##########################");
						SysLogger.info("添加连接: " + maclink.getStartIp() + " --- " + maclink.getEndIp());
						addNodetoNodeLink(lk);
					}
				} catch (Exception e) {
					e.printStackTrace();
					SysLogger.error(e.getMessage());
				}
			}
		}

		// List allLinks = DiscoverEngine.getInstance().getLinkList();
		Hashtable existNode = new Hashtable();
		// DiscoverEngine.getInstance().getLinkList().clear();;
		if (links != null && links.size() > 0) {
			for (int i = 0; i < links.size(); i++) {
				try {
					NodeToNodeLink link = links.get(i);
					SysLogger.info("连接: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
					Host startNode = null;
					// hostlist.getHostByID(link.getNodeparentid());
					Host endNode = null;
					for (int k = 0; k < hostlist.size(); k++) {
						Host newHost = (Host) hostlist.get(k);
						if (newHost.getId() == link.getNodeparentid()) {
							startNode = newHost;
							continue;
						}
						if (newHost.getId() == link.getNodeId()) {
							endNode = newHost;
							continue;
						}
					}
					if (endNode == null || startNode == null) {
						continue;
					}
					// DiscoverEngine.getInstance().getHostByID(link.getNodeId());
					SysLogger.info("#### " + link.getParentifindex() + "   #### " + link.getIfindex());
					IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex() + "");
					IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex() + "");
					Link addlink = new Link();
					addlink.setStartId(link.getNodeparentid());
					addlink.setStartIndex(link.getParentifindex() + "");
					addlink.setStartIp(startNode.getIpAddress());
					addlink.setStartDescr(startIfEntity.getDescr());
					addlink.setStartPort(startIfEntity.getPort());
					addlink.setStartPhysAddress(startNode.getBridgeAddress());

					addlink.setEndId(link.getNodeId());
					addlink.setEndIndex(link.getIfindex() + "");
					addlink.setEndIp(endNode.getIpAddress());
					addlink.setEndDescr(endIfEntity.getDescr());
					addlink.setEndPort(endIfEntity.getPort());
					addlink.setEndPhysAddress(endNode.getBridgeAddress());

					addlink.setAssistant(link.getAssistant());
					addlink.setFindtype(link.getFindtype());
					addlink.setLinktype(0);
					// DiscoverEngine.getInstance().getLinkList().add(addlink);
					dealLinkList(addlink);
					// linkList.add(addlink);
					if (!existNode.containsKey(addlink.getStartId())) {
						existNode.put(addlink.getStartId(), addlink.getStartId());
					}
					if (!existNode.containsKey(addlink.getEndId())) {
						existNode.put(addlink.getEndId(), addlink.getEndId());
					}
					// allLinks.add(addlink);
				} catch (Exception e) {
					e.printStackTrace();
					SysLogger.error(e.getMessage());
				}
			}
		}

		// 判断是否有没有连接的Node
		// 将没有产生连接的孤立的接点用逻辑连接代替
		// List macLinkList = DiscoverEngine.getInstance().getMacLinkList();
		if (maclinkList != null && maclinkList.size() > 0) {
			for (int k = 0; k < maclinkList.size(); k++) {
				try {
					Link maclink = (Link) maclinkList.get(k);
					if (!existNode.containsKey(maclink.getStartId()) || !existNode.containsKey(maclink.getEndId())) {
						// 若有个端点不在已经存在的连接列表里
						// Saving link also when ifindex = -1 (not found)
						NodeToNodeLink link = new NodeToNodeLink(maclink.getEndId(), Integer.parseInt(maclink.getEndIndex()));
						link.setFindtype(SystemConstant.ISMac);
						link.setNodeparentid(maclink.getStartId());
						link.setParentifindex(Integer.parseInt(maclink.getStartIndex()));
						SysLogger.info("添加连接: ##########################");
						SysLogger.info("添加连接: " + maclink.getStartIp() + " --- " + maclink.getEndIp());
						addNodetoNodeLink(link);
						// NodeToNodeLink link = (NodeToNodeLink)links.get(i);
						SysLogger.info("连接: " + link.getNodeparentid() + " " + link.getParentifindex() + " " + link.getNodeId() + " " + link.getIfindex());
						Host startNode = DiscoverEngine.getInstance().getHostByID(link.getNodeparentid());
						Host endNode = DiscoverEngine.getInstance().getHostByID(link.getNodeId());
						IfEntity startIfEntity = startNode.getIfEntityByIndex(link.getParentifindex() + "");
						IfEntity endIfEntity = endNode.getIfEntityByIndex(link.getIfindex() + "");
						Link addlink = new Link();
						addlink.setStartId(link.getNodeparentid());
						addlink.setStartIndex(link.getParentifindex() + "");
						addlink.setStartIp(startNode.getIpAddress());
						addlink.setStartDescr(startIfEntity.getDescr());
						addlink.setStartPort(startIfEntity.getPort());
						addlink.setStartPhysAddress(startNode.getBridgeAddress());

						addlink.setEndId(link.getNodeId());
						addlink.setEndIndex(link.getIfindex() + "");
						addlink.setEndIp(endNode.getIpAddress());
						addlink.setEndDescr(endIfEntity.getDescr());
						addlink.setEndPort(endIfEntity.getPort());
						addlink.setEndPhysAddress(endNode.getBridgeAddress());

						addlink.setAssistant(link.getAssistant());
						addlink.setFindtype(link.getFindtype());
						addlink.setLinktype(0);// 物理连接
						// DiscoverEngine.getInstance().getLinkList().add(addlink);
						linkList.add(addlink);
						// if(!existNode.containsKey(addlink.getStartId())){
						// existNode.put(addlink.getStartId(),
						// addlink.getStartId());
						// }
						// if(!existNode.containsKey(addlink.getEndId())){
						// existNode.put(addlink.getEndId(),
						// addlink.getEndId());
						// }
					}
				} catch (Exception e) {
					e.printStackTrace();
					SysLogger.error(e.getMessage());
				}
			}
		}

		// 将路由连接添加进去
		if (routelinkList != null && routelinkList.size() > 0) {
			for (int k = 0; k < routelinkList.size(); k++) {
				try {
					Link routelink = (Link) routelinkList.get(k);
					routelink.setLinktype(-1);// 物理连接
					dealLinkList(routelink);
				} catch (Exception e) {
					e.printStackTrace();
					SysLogger.error(e.getMessage());
				}
			}
		}
		// 将VLAN连接添加进去
		if (maclinkList != null && maclinkList.size() > 0) {
			for (int k = 0; k < maclinkList.size(); k++) {
				try {
					Link maclink = (Link) maclinkList.get(k);
					maclink.setLinktype(-1);// 物理连接
					dealLinkList(maclink);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 从单个设备开始发现设备和链路
	 * 
	 * @return
	 */
	public String analyticsFromNode() {
		String id = "53";
		// 获取当前网络设备
		HostNodeDao dao = new HostNodeDao();
		// List nodeList = new ArrayList();
		HostNode hostnode = null;
		try {
			hostnode = (HostNode) dao.findByID(id);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		Host host = new Host();
		host.setCategory(hostnode.getCategory());
		host.setCommunity(hostnode.getCommunity());
		host.setWritecommunity(hostnode.getWriteCommunity());
		// host.setSnmpversion(hostnode.getSnmpversion());
		host.setSysOid(hostnode.getSysOid());
		host.setRouter(true);
		host.setSuperNode(-1);
		host.setLocalNet(1);
		host.setIpAddress(hostnode.getIpAddress());
		host.setLayer(hostnode.getLayer() + 1);

		DiscoverInitialize discoverInit = new DiscoverInitialize();
		discoverInit.init();
		String snmpversion = "";
		snmpversion = ResourceCenter.getInstance().getSnmpversion();
		int default_version = 0;
		if (snmpversion.equals("v1")) {
			default_version = org.snmp4j.mp.SnmpConstants.version1;
		} else if (snmpversion.equals("v2")) {
			default_version = org.snmp4j.mp.SnmpConstants.version2c;
		} else if (snmpversion.equals("v1+v2")) {
			default_version = org.snmp4j.mp.SnmpConstants.version1;
		} else if (snmpversion.equals("v2+v1")) {
			default_version = org.snmp4j.mp.SnmpConstants.version2c;
		}
		host.setSnmpversion(default_version);
		DiscoverEngine.getInstance().setSnmpversion(default_version);
		DiscoverResource.getInstance().getCommunitySet().add(hostnode.getCommunity());
		String bid = getParaValue("45");
		DiscoverEngine.getInstance().setDiscover_bid(bid);

		DiscoverEngine.getInstance().addHost_SOLO(host, null);

		// 生成线程池
		ThreadPool threadPool = new ThreadPool(2);
		try {
			threadPool.runTask(IPRouterProbeThread_SOLO_Runable.createTask(host));
			List ipNetTable = SnmpUtil.getInstance().getIpNetToMediaTable(host.getIpAddress(), host.getCommunity());

			if (ipNetTable != null && ipNetTable.size() > 0) // important
			{
				threadPool.runTask(IPRouterProbeThread_SOLO_Runable.createTask(host));
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		threadPool.join();
		threadPool.close();
		threadPool = null;
		try {
			// 开始分析链路关系
			analyseTopoLinks();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private int countNodetoNodeLink(NodeToNodeLink nnlink) {
		int counts = 0;
		if (nnlink == null) {
			return counts;
		}
		if (!links.isEmpty()) {
			Iterator<NodeToNodeLink> ite = links.iterator();
			while (ite.hasNext()) {
				NodeToNodeLink curNnLink = ite.next();
				if (curNnLink.assistantequals(nnlink)) {
					counts = counts + 1;
				}
			}
		}
		return counts;
	}

	public synchronized void dealLinkList(Link link) {
		if (linkList != null && linkList.size() > 0) {
			for (int i = 0; i < linkList.size(); i++) {
				Link newlink = (Link) linkList.get(i);
				if (newlink.equals(link)) {
					break;
				} else {
					linkList.add(link);
				}
			}
		} else {
			linkList.add(link);
		}
	}

	public synchronized void dealMacLinkList(Link link) {
		if (maclinkList != null && maclinkList.size() > 0) {
			for (int i = 0; i < maclinkList.size(); i++) {
				Link newlink = (Link) maclinkList.get(i);
				if (newlink.equals(link)) {
					break;
				} else {
					maclinkList.add(link);
				}
			}
		} else {
			maclinkList.add(link);
		}
	}

	public synchronized void dealRouteLinkList(Link link) {
		if (routelinkList != null && routelinkList.size() > 0) {
			for (int i = 0; i < routelinkList.size(); i++) {
				Link newlink = (Link) routelinkList.get(i);
				if (newlink.equals(link)) {
					break;
				} else {
					routelinkList.add(link);
				}
			}
		} else {
			routelinkList.add(link);
		}
	}

	public String execute(String action) {
		if (action.equals("linkanalytics")) {
			return linkanalytics();
		}
		if (action.equals("linkAnalyticsAll")) {
			return linkAnalyticsAll();
		}
		if (action.equals("analyticsFromNode")) {
			return analyticsFromNode();
		}
		return null;
	}

	public synchronized Host getHostByAliasIP(String ip) {
		Host host = null;
		for (int i = 0; i < hostlist.size(); i++) {
			if (((Host) hostlist.get(i)).getAliasIPs() == null) {
				continue;
			}
			if (((Host) hostlist.get(i)).getAliasIPs().contains(ip)) {
				host = (Host) hostlist.get(i);
				break;
			}
		}
		return host;
	}

	private Set<String> getMacsOnBridgeLink(Host bridge1, int bp1, Host bridge2, int bp2) {

		Set<String> macsOnLink = new HashSet<String>();

		Set<String> macsOnBridge1 = bridge1.getMacAddressesOnBridgePort(bp1);

		Set<String> macsOnBridge2 = bridge2.getMacAddressesOnBridgePort(bp2);

		if (macsOnBridge2 == null || macsOnBridge1 == null) {
			return null;
		}

		if (macsOnBridge2.isEmpty() || macsOnBridge1.isEmpty()) {
			return null;
		}

		Iterator<String> macsonbridge1_ite = macsOnBridge1.iterator();

		while (macsonbridge1_ite.hasNext()) {
			String curMacOnBridge1 = macsonbridge1_ite.next();
			if (bridge2.isBridgeIdentifier(curMacOnBridge1)) {
				continue;
			}
			if (macsOnBridge2.contains(curMacOnBridge1)) {
				macsOnLink.add(curMacOnBridge1);
			}
		}
		return macsOnLink;
	}

	private Host getNodeFromMacIdentifierOfBridgeNode(String macAddress) {
		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (curNode.isBridgeIdentifier(macAddress)) {
				return curNode;
			}
		}
		return null;
	}

	public void initComm(String ip) throws IOException {

		// 设置Agent方的IP和端口

		targetAddress = GenericAddress.parse("udp:" + ip + "/161");

		TransportMapping transport = new DefaultUdpTransportMapping();

		snmp = new Snmp(transport);

		transport.listen();

	}

	private boolean isMacIdentifierOfBridgeNode(String macAddress) {
		Iterator<Host> ite = bridgeNodes.values().iterator();
		while (ite.hasNext()) {
			Host curNode = ite.next();
			if (curNode.isBridgeIdentifier(macAddress)) {
				return true;
			}
		}
		return false;
	}

	private boolean isNearestBridgeLink(Host bridge1, int bp1, Host bridge2, int bp2) {

		boolean hasbridge2forwardingRule = false;
		Set<String> macsOnBridge2 = bridge2.getMacAddressesOnBridgePort(bp2);

		Set<String> macsOnBridge1 = bridge1.getMacAddressesOnBridgePort(bp1);

		if (macsOnBridge2 == null || macsOnBridge1 == null) {
			return false;
		}

		if (macsOnBridge2.isEmpty() || macsOnBridge1.isEmpty()) {
			return false;
		}

		Iterator<String> macsonbridge1_ite = macsOnBridge1.iterator();

		while (macsonbridge1_ite.hasNext()) {
			String curMacOnBridge1 = macsonbridge1_ite.next();

			if (bridge2.isBridgeIdentifier(curMacOnBridge1)) {
				hasbridge2forwardingRule = true;
				continue;
			}
			if (bridge1.isBridgeIdentifier(curMacOnBridge1)) {
				continue;
			}
			if (macsOnBridge2.contains(curMacOnBridge1) && isMacIdentifierOfBridgeNode(curMacOnBridge1)) {
				return false;
			}
		}

		return hasbridge2forwardingRule;
	}

	public Hashtable linkana(String ip1, String commu1, int version1, String ip2, String commu2, int version2) {
		Hashtable returnHash = new Hashtable();
		// 生成线程池,数目依据被监视的设备多少
		ThreadPool threadPool = new ThreadPool(2);
		// 运行任务
		String ip = "";
		int version = 0;
		String commu = "";
		for (int i = 0; i < 2; i++) {
			if (i == 0) {
				ip = ip1;
				version = version1;
				commu = commu1;
			} else if (i == 1) {
				ip = ip2;
				version = version2;
				commu = commu2;
			}
			threadPool.runTask(createTask(i + 1, ip, version, commu));
		}
		// 关闭线程池并等待所有任务完成
		threadPool.join();
		threadPool.close();
		threadPool = null;
		Host host1 = (Host) hostlist.get(0);
		Host host2 = (Host) hostlist.get(1);
		// 进行CDP协议分析
		if (host1.getCdpList() != null && host1.getCdpList().size() > 0 && host2.getCdpList() != null && host2.getCdpList().size() > 0) {
			try {
				int curCdpNodeId = host1.getId();
				List executedPort = new ArrayList();
				Iterator<CdpCachEntryInterface> sub_ite = host1.getCdpList().iterator();
				while (sub_ite.hasNext()) {
					try {
						CdpCachEntryInterface cdpIface = sub_ite.next();
						String targetIpAddr = cdpIface.getIp();
						Host targetHost = null;
						if (host2.getAliasIPs().contains(targetIpAddr)) {
							targetHost = host2;
						} else {
							continue;
						}
						// 获取cdpIfIndex
						int cdpIfIndex = -1;
						if (targetHost.getCdpList() != null && targetHost.getCdpList().size() > 0) {
							Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
							while (target_ite.hasNext()) {
								CdpCachEntryInterface targetcdpIface = target_ite.next();
								if (host1.getAliasIPs().contains(targetcdpIface.getIp())) {
									// 需要加如当前PORTDESC是否已经被处理过的条件
									if (executedPort.contains(targetcdpIface.getPortdesc())) {
										continue;
									}
									if (host1.getIfEntityByDesc(targetcdpIface.getPortdesc()) != null) {
										cdpIfIndex = Integer.parseInt(host1.getIfEntityByDesc(targetcdpIface.getPortdesc()).getIndex());
										executedPort.add(targetcdpIface.getPortdesc());
										break;
									}
								}
							}
						}
						int targetCdpNodeId = targetHost.getId();
						if (cdpIfIndex <= 0) {
							// 用逻辑端口代替
							cdpIfIndex = Integer.parseInt(host1.getIfEntityByIP(host1.getIpAddress()).getIndex());
						}
						int cdpDestIfindex = -1;
						if (targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null) {
							cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
							if (cdpDestIfindex < 0) {
								continue;
							}
						} else {
							continue;
						}
						boolean add = false;
						add = true;
						if (add) {
							NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId, cdpDestIfindex);
							lk.setFindtype(SystemConstant.ISCDP);
							lk.setNodeparentid(curCdpNodeId);
							lk.setParentifindex(cdpIfIndex);
							addNodetoNodeLink(lk);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 分析第二台设备的CDP协议
			try {
				int curCdpNodeId = host2.getId();
				List executedPort = new ArrayList();
				Iterator<CdpCachEntryInterface> sub_ite = host2.getCdpList().iterator();
				while (sub_ite.hasNext()) {
					try {
						CdpCachEntryInterface cdpIface = sub_ite.next();
						String targetIpAddr = cdpIface.getIp();
						Host targetHost = null;
						if (host1.getAliasIPs().contains(targetIpAddr)) {
							targetHost = host1;
						} else {
							continue;
						}
						// 获取cdpIfIndex
						int cdpIfIndex = -1;
						if (targetHost.getCdpList() != null && targetHost.getCdpList().size() > 0) {
							Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
							while (target_ite.hasNext()) {
								CdpCachEntryInterface targetcdpIface = target_ite.next();
								if (host2.getAliasIPs().contains(targetcdpIface.getIp())) {
									// 需要加如当前PORTDESC是否已经被处理过的条件
									if (executedPort.contains(targetcdpIface.getPortdesc())) {
										continue;
									}
									if (host2.getIfEntityByDesc(targetcdpIface.getPortdesc()) != null) {
										cdpIfIndex = Integer.parseInt(host2.getIfEntityByDesc(targetcdpIface.getPortdesc()).getIndex());
										executedPort.add(targetcdpIface.getPortdesc());
										break;
									}
								}
							}
						}
						int targetCdpNodeId = targetHost.getId();
						if (cdpIfIndex <= 0) {
							// 用逻辑端口代替
							cdpIfIndex = Integer.parseInt(host1.getIfEntityByIP(host1.getIpAddress()).getIndex());
						}
						int cdpDestIfindex = -1;
						if (targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null) {
							cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
							if (cdpDestIfindex < 0) {
								continue;
							}
						} else {
							continue;
						}

						boolean add = false;
						add = true;
						if (add) {
							NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId, cdpDestIfindex);
							lk.setFindtype(SystemConstant.ISCDP);
							lk.setNodeparentid(curCdpNodeId);
							lk.setParentifindex(cdpIfIndex);
							addNodetoNodeLink(lk);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 进行NDP协议分析
		if (host1.getNdpHash() != null && host1.getNdpHash().size() > 0 && host2.getNdpHash() != null && host2.getNdpHash().size() > 0) {
			try {
				if (host1.getNdpHash() != null && host1.getNdpHash().size() > 0) {
					Iterator<String> sub_ite = host1.getNdpHash().keySet().iterator();
					while (sub_ite.hasNext()) {
						try {
							String endndpMac = sub_ite.next();
							String endndpDescr = (String) host1.getNdpHash().get(endndpMac);
							// 若MAC不存在，则继续循环
							if (!host2.getMac().equalsIgnoreCase(endndpMac)) {
								continue;
							}
							Host endNode = host2;
							if (endNode == null) {
								continue;
							}
							IfEntity endIfEntity = endNode.getIfEntityByDesc(endndpDescr);
							IfEntity startIfEntity = null;
							Hashtable endNodeNdpHash = endNode.getNdpHash();
							if (endNodeNdpHash == null) {
								endNodeNdpHash = new Hashtable();
							}
							if (host1.getMac() == null) {
								continue;
							}
							if (endNodeNdpHash.containsKey(host1.getMac())) {
								String ndpDescr = (String) endNodeNdpHash.get(host1.getMac());
								startIfEntity = endNode.getIfEntityByDesc(ndpDescr);
							}
							if (startIfEntity == null) {
								startIfEntity = host1.getIfEntityByIP(host1.getIpAddress());
								if (startIfEntity == null) {
									continue;
								}
							}
							if (endIfEntity == null) {
								// 从FDB表里找
								endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
								if (endIfEntity == null) {
									continue;
								}
							}
							boolean add = false;
							add = true;
							if (add) {
								NodeToNodeLink lk = new NodeToNodeLink(endNode.getId(), Integer.parseInt(endIfEntity.getIndex()));
								lk.setFindtype(SystemConstant.ISNDP);
								lk.setNodeparentid(host1.getId());
								lk.setParentifindex(Integer.parseInt(startIfEntity.getIndex()));
								addNodetoNodeLink(lk);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 分析第二台设备的NDP协议
			try {
				int curCdpNodeId = host2.getId();
				List executedPort = new ArrayList();
				Iterator<CdpCachEntryInterface> sub_ite = host2.getCdpList().iterator();
				while (sub_ite.hasNext()) {
					try {
						CdpCachEntryInterface cdpIface = sub_ite.next();
						String targetIpAddr = cdpIface.getIp();
						Host targetHost = null;
						if (host1.getAliasIPs().contains(targetIpAddr)) {
							targetHost = host1;
						} else {
							continue;
						}
						int cdpIfIndex = -1;
						if (targetHost.getCdpList() != null && targetHost.getCdpList().size() > 0) {
							Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
							while (target_ite.hasNext()) {
								CdpCachEntryInterface targetcdpIface = target_ite.next();
								if (host1.getAliasIPs().contains(targetcdpIface.getIp())) {
									if (executedPort.contains(targetcdpIface.getPortdesc())) {
										continue;
									}
									if (host1.getIfEntityByDesc(targetcdpIface.getPortdesc()) != null) {
										cdpIfIndex = Integer.parseInt(host1.getIfEntityByDesc(targetcdpIface.getPortdesc()).getIndex());
										executedPort.add(targetcdpIface.getPortdesc());
										break;
									}
								}
							}
						}
						int targetCdpNodeId = targetHost.getId();
						if (cdpIfIndex <= 0) {
							// 用逻辑端口代替
							cdpIfIndex = Integer.parseInt(host2.getIfEntityByIP(host2.getIpAddress()).getIndex());
							// continue;
						}
						int cdpDestIfindex = -1;
						if (targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null) {
							cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
							if (cdpDestIfindex < 0) {
								continue;
							}
						} else {
							continue;
						}
						boolean add = false;
						add = true;
						if (add) {
							NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId, cdpDestIfindex);
							lk.setFindtype(SystemConstant.ISCDP);
							lk.setNodeparentid(curCdpNodeId);
							lk.setParentifindex(cdpIfIndex);
							addNodetoNodeLink(lk);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List routerlist1 = host1.getRouteList();
		List routerlist2 = host2.getRouteList();
		// 分析第一台设备的路由表
		for (int i = 0; i < routerlist1.size(); i++) {
			IpRouter ipr = (IpRouter) routerlist1.get(i);
			IfEntity ifEntity = null;
			if (host1.getIfEntityList() != null) {
				for (int j = 0; j < host1.getIfEntityList().size(); j++) {
					ifEntity = (IfEntity) host1.getIfEntityList().get(j);
					if (ifEntity.getIndex().equals(ipr.getIfIndex())) {
						break;
					}
				}
			}
			if (host2.getAliasIPs() != null && host2.getAliasIPs().size() > 0) {
				if (host2.getAliasIPs().contains(ipr.getNextHop())) {
					IfEntity endIfEntity = host2.getIfEntityByIP(ipr.getNextHop());
					Link link = new Link();
					link.setStartId(host1.getId());
					link.setStartIndex(ifEntity.getIndex());
					link.setStartIp(ifEntity.getIpAddress());
					link.setStartPhysAddress(ifEntity.getPhysAddress()); // 记录下起点的mac
					link.setStartDescr(ifEntity.getDescr());
					link.setVlanStartIndex(ifEntity.getIndex());
					link.setEndIp(host2.getIpAddress());
					link.setEndId(host2.getId());
					link.setEndIndex(endIfEntity.getIndex());
					link.setEndDescr(endIfEntity.getDescr());
					link.setEndPhysAddress(endIfEntity.getPhysAddress());
					link.setVlanEndIp(ipr.getNextHop());// 设置当前VLAN地址
					link.setVlanEndIndex(endIfEntity.getIndex());
					link.setFindtype(SystemConstant.ISRouter);// 利用MAC发现
					link.setSublinktype(SystemConstant.NONEPHYSICALLINK);// 是逻辑连接关系
					dealRouteLinkList(link);
				}
			}
		}
		// 分析第二台设备的路由表
		for (int i = 0; i < routerlist2.size(); i++) {
			IpRouter ipr = (IpRouter) routerlist2.get(i);
			IfEntity ifEntity = null;
			if (host2.getIfEntityList() != null) {
				for (int j = 0; j < host2.getIfEntityList().size(); j++) {
					ifEntity = (IfEntity) host2.getIfEntityList().get(j);
					if (ifEntity.getIndex().equals(ipr.getIfIndex())) {
						break;
					}
				}
			}
			if (host1.getAliasIPs() != null && host1.getAliasIPs().size() > 0) {
				// 若第二台设备存在该IP，则进行连接
				if (host1.getAliasIPs().contains(ipr.getNextHop())) {
					IfEntity endIfEntity = host1.getIfEntityByIP(ipr.getNextHop());
					Link link = new Link();
					link.setStartId(host2.getId());
					link.setStartIndex(ifEntity.getIndex());
					link.setStartIp(ifEntity.getIpAddress());
					link.setStartPhysAddress(ifEntity.getPhysAddress()); // 记录下起点的mac
					link.setStartDescr(ifEntity.getDescr());
					link.setVlanStartIndex(ifEntity.getIndex());
					link.setEndIp(host1.getIpAddress());
					link.setEndId(host1.getId());
					link.setEndIndex(endIfEntity.getIndex());
					link.setEndDescr(endIfEntity.getDescr());
					link.setEndPhysAddress(endIfEntity.getPhysAddress());
					link.setVlanEndIp(ipr.getNextHop());// 设置当前VLAN地址
					link.setVlanEndIndex(endIfEntity.getIndex());
					link.setFindtype(SystemConstant.ISRouter);// 利用MAC发现
					link.setSublinktype(SystemConstant.NONEPHYSICALLINK);// 是逻辑连接关系
					dealRouteLinkList(link);
				}
			}
		}
		List maclist1 = host1.getMacs();
		List maclist2 = host2.getMacs();
		// 对第一台设备的IPMAC进行分析
		if (maclist1 != null && maclist1.size() > 0) {
			for (int i = 0; i < maclist1.size(); i++) {
				try {
					IpAddress ipAddr = (IpAddress) maclist1.get(i);
					IfEntity ifEntity = null;
					if (host1.getIfEntityList() != null) {
						for (int j = 0; j < host1.getIfEntityList().size(); j++) {
							ifEntity = (IfEntity) host1.getIfEntityList().get(j);
							if (ifEntity.getIndex().equals(ipAddr.getIfIndex())) {
								break;
							}
						}
					}
					if (host2.getAliasIPs() != null && host2.getAliasIPs().size() > 0) {
						// 若第二台设备存在该IP，则进行连接
						if (host2.getAliasIPs().contains(ipAddr.getIpAddress())) {
							Link link = new Link();
							link.setStartId(host1.getId());
							link.setStartIndex(ifEntity.getIndex());
							link.setStartIp(ifEntity.getIpAddress());
							link.setStartPhysAddress(ifEntity.getPhysAddress()); // 记录下起点的mac
							link.setStartDescr(ifEntity.getDescr());
							link.setVlanStartIndex(ifEntity.getIndex());
							link.setEndIp(host2.getIpAddress());
							link.setEndId(host2.getId());
							IfEntity endIfEntity = host2.getIfEntityByIP(ipAddr.getIpAddress());

							link.setEndIndex(endIfEntity.getIndex());
							link.setEndDescr(endIfEntity.getDescr());
							link.setEndPhysAddress(endIfEntity.getPhysAddress());
							link.setVlanEndIp(ipAddr.getIpAddress());// 设置当前VLAN地址
							link.setVlanEndIndex(endIfEntity.getIndex());
							link.setFindtype(SystemConstant.ISMac);// 利用MAC发现
							link.setSublinktype(SystemConstant.NONEPHYSICALLINK);// 是逻辑连接关系
							dealMacLinkList(link);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		// 对第二台设备的IPMAC进行分析
		if (maclist2 != null && maclist2.size() > 0) {
			for (int i = 0; i < maclist2.size(); i++) {
				try {
					IpAddress ipAddr = (IpAddress) maclist2.get(i);
					IfEntity ifEntity = null;
					if (host2.getIfEntityList() != null) {
						for (int j = 0; j < host2.getIfEntityList().size(); j++) {
							// 默认情况下,需要一个缺省的连接关系,当找不到物理链路的时候,用VLAN的接口来计算
							ifEntity = (IfEntity) host2.getIfEntityList().get(j);
							if (ifEntity.getIndex().equals(ipAddr.getIfIndex())) {
								break;
							}
						}
					}
					if (host1.getAliasIPs() != null && host1.getAliasIPs().size() > 0) {
						if (host1.getAliasIPs().contains(ipAddr.getIpAddress())) {
							Link link = new Link();
							link.setStartId(host2.getId());
							link.setStartIndex(ifEntity.getIndex());
							link.setStartIp(ifEntity.getIpAddress());
							link.setStartPhysAddress(ifEntity.getPhysAddress()); // 记录下起点的mac
							link.setStartDescr(ifEntity.getDescr());
							link.setVlanStartIndex(ifEntity.getIndex());
							link.setEndIp(host1.getIpAddress());
							link.setEndId(host1.getId());
							IfEntity endIfEntity = host1.getIfEntityByIP(ipAddr.getIpAddress());

							link.setEndIndex(endIfEntity.getIndex());
							link.setEndDescr(endIfEntity.getDescr());
							link.setEndPhysAddress(endIfEntity.getPhysAddress());
							link.setVlanEndIp(ipAddr.getIpAddress());// 设置当前VLAN地址
							link.setVlanEndIndex(endIfEntity.getIndex());
							link.setFindtype(SystemConstant.ISMac);// 利用MAC发现
							link.setSublinktype(SystemConstant.NONEPHYSICALLINK);// 是逻辑连接关系
							dealMacLinkList(link);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		analyseTopoLinks();
		return returnHash;
	}

	public Hashtable linkanaAll() {
		Hashtable returnHash = new Hashtable();
		if (hostlist != null && hostlist.size() > 0) {
			for (int i = 0; i < hostlist.size(); i++) {
				Host host1 = (Host) hostlist.get(i);
				for (int k = 0; k < hostlist.size(); k++) {
					Host host2 = (Host) hostlist.get(k);
					if (host1.getId() == host2.getId()) {
						continue;
					}

					// 进行CDP协议分析
					if (host1.getCdpList() != null && host1.getCdpList().size() > 0 && host2.getCdpList() != null && host2.getCdpList().size() > 0) {
						try {
							int curCdpNodeId = host1.getId();
							List executedPort = new ArrayList();
							Iterator<CdpCachEntryInterface> sub_ite = host1.getCdpList().iterator();
							while (sub_ite.hasNext()) {
								try {
									CdpCachEntryInterface cdpIface = sub_ite.next();
									String targetIpAddr = cdpIface.getIp();
									Host targetHost = null;
									if (host2.getAliasIPs().contains(targetIpAddr)) {
										targetHost = host2;
									} else {
										continue;
									}
									int cdpIfIndex = -1;
									if (targetHost.getCdpList() != null && targetHost.getCdpList().size() > 0) {
										Iterator<CdpCachEntryInterface> target_ite = targetHost.getCdpList().iterator();
										while (target_ite.hasNext()) {
											CdpCachEntryInterface targetcdpIface = target_ite.next();
											if (host1.getAliasIPs().contains(targetcdpIface.getIp())) {
												// 需要加如当前PORTDESC是否已经被处理过的条件
												if (executedPort.contains(targetcdpIface.getPortdesc())) {
													continue;
												}
												if (host1.getIfEntityByDesc(targetcdpIface.getPortdesc()) != null) {
													cdpIfIndex = Integer.parseInt(host1.getIfEntityByDesc(targetcdpIface.getPortdesc()).getIndex());
													executedPort.add(targetcdpIface.getPortdesc());
													break;
												}
											}
										}
									}
									int targetCdpNodeId = targetHost.getId();
									if (cdpIfIndex <= 0) {
										// 用逻辑端口代替
										cdpIfIndex = Integer.parseInt(host1.getIfEntityByIP(host1.getIpAddress()).getIndex());
									}
									int cdpDestIfindex = -1;
									if (targetHost.getIfEntityByDesc(cdpIface.getPortdesc()) != null) {
										cdpDestIfindex = Integer.parseInt(targetHost.getIfEntityByDesc(cdpIface.getPortdesc()).getIndex());
										if (cdpDestIfindex < 0) {
											continue;
										}
									} else {
										continue;
									}

									boolean add = false;
									add = true;
									if (add) {
										NodeToNodeLink lk = new NodeToNodeLink(targetCdpNodeId, cdpDestIfindex);
										lk.setFindtype(SystemConstant.ISCDP);
										lk.setNodeparentid(curCdpNodeId);
										lk.setParentifindex(cdpIfIndex);
										addNodetoNodeLink(lk);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					// 进行NDP协议分析
					if (host1.getNdpHash() != null && host1.getNdpHash().size() > 0 && host2.getNdpHash() != null && host2.getNdpHash().size() > 0) {
						try {
							if (host1.getNdpHash() != null && host1.getNdpHash().size() > 0) {
								Iterator<String> sub_ite = host1.getNdpHash().keySet().iterator();
								while (sub_ite.hasNext()) {
									try {
										String endndpMac = sub_ite.next();
										String endndpDescr = (String) host1.getNdpHash().get(endndpMac);
										if (!host2.getMac().equalsIgnoreCase(endndpMac)) {
											continue;
										}

										Host endNode = host2;
										if (endNode == null) {
											continue;
										}
										IfEntity endIfEntity = endNode.getIfEntityByDesc(endndpDescr);
										IfEntity startIfEntity = null;
										// 默认情况下,endNode的NdpHash不为空
										Hashtable endNodeNdpHash = endNode.getNdpHash();
										if (endNodeNdpHash == null) {
											endNodeNdpHash = new Hashtable();
										}
										if (host1.getMac() == null) {
											continue;
										}
										if (endNodeNdpHash.containsKey(host1.getMac())) {
											String ndpDescr = (String) endNodeNdpHash.get(host1.getMac());
											startIfEntity = endNode.getIfEntityByDesc(ndpDescr);
										}

										if (startIfEntity == null) {

											startIfEntity = host1.getIfEntityByIP(host1.getIpAddress());
											if (startIfEntity == null) {
												continue;
											}

										}
										if (endIfEntity == null) {
											// 从FDB表里找
											endIfEntity = endNode.getIfEntityByIP(endNode.getIpAddress());
											if (endIfEntity == null) {
												continue;
											}

										}

										boolean add = false;
										add = true;
										if (add) {
											NodeToNodeLink lk = new NodeToNodeLink(endNode.getId(), Integer.parseInt(endIfEntity.getIndex()));
											lk.setFindtype(SystemConstant.ISNDP);
											lk.setNodeparentid(host1.getId());
											lk.setParentifindex(Integer.parseInt(startIfEntity.getIndex()));
											addNodetoNodeLink(lk);
										}
									} catch (Exception e) {
										e.printStackTrace();
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
		analyseTopoLinks();

		return returnHash;
	}

	/**
	 * 检测两台设备间的链路
	 * 
	 * @return
	 */
	public String linkanalytics() {
		String ip1 = getParaValue("ipaddress1");
		String name1 = getParaValue("name1");
		int version1 = getParaIntValue("version1");

		String ip2 = getParaValue("ipaddress2");
		String name2 = getParaValue("name2");
		int version2 = getParaIntValue("version2");

		request.setAttribute("ipaddress1", ip1);
		request.setAttribute("name1", name1);
		request.setAttribute("version1", version1);

		request.setAttribute("ipaddress2", ip2);
		request.setAttribute("name2", name2);
		request.setAttribute("version2", version2);

		com.afunms.polling.node.Host host1 = (com.afunms.polling.node.Host) PollingEngine.getInstance().getNodeByIP(ip1);
		com.afunms.polling.node.Host host2 = (com.afunms.polling.node.Host) PollingEngine.getInstance().getNodeByIP(ip2);

		try {
			this.initComm(ip1);
			int flag1 = this.sendPDU(name1, version1);
			this.initComm(ip2);
			int flag2 = this.sendPDU(name2, version2);
			if (flag1 != 1 && flag2 != 1) {
				// 两个设备SNMP都没开,则停止分析
				return "/tool/linkanalyticslist.jsp?flag=all";
			} else if (flag1 != 1) {
				// 第一个IP没开SNMP
				return "/tool/linkanalyticslist.jsp?flag=1";
			} else if (flag2 != 1) {
				// 第二个IP没开SNMP
				return "/tool/linkanalyticslist.jsp?flag=2";
			}
			// 判断是否为网络设备

			// 开始分析链路关系
			linkana(ip1, name1, version1, ip2, name2, version2);

			if (linkList != null && linkList.size() > 0) {

			}
			request.setAttribute("start_if", host1.getInterfaceHash().values().iterator());
			request.setAttribute("end_if", host2.getInterfaceHash().values().iterator());
			request.setAttribute("linkList", linkList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "/tool/linkanalyticslist.jsp?version=" + version1;
	}

	/**
	 * 检测设备间的链路
	 * 
	 * @return
	 */
	public String linkAnalyticsAll() {
		// 获取所有网络设备
		HostNodeDao dao = new HostNodeDao();
		List nodeList = new ArrayList();
		try {
			nodeList = dao.loadNetwork(-1);
		} catch (Exception e) {

		} finally {
			dao.close();
		}
		if (nodeList != null && nodeList.size() > 0) {
			// 对所有网络设备进行数据采集
			// 生成线程池,数目依据被监视的设备多少
			ThreadPool threadPool = new ThreadPool(nodeList.size());
			for (int i = 0; i < nodeList.size(); i++) {
				HostNode hostnode = (HostNode) nodeList.get(i);
				threadPool.runTask(createTask(hostnode));
			}
			// 关闭线程池并等待所有任务完成
			threadPool.join();
			threadPool.close();
			threadPool = null;
		}
		DiscoverCompleteDao nodeDao = new DiscoverCompleteDao();
		try {
			// 开始分析链路关系
			linkanaAll();
			DiscoverDataHelper helper = new DiscoverDataHelper();
			LinkDao linkdao = new LinkDao();
			List linklist = new ArrayList();
			try {
				linklist = linkdao.loadAll();
				linkdao = new LinkDao();
				linkdao.delete(linklist);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				linkdao.close();
			}

			nodeDao.addLinkData(linkList);
			helper.DB2NetworkXml();
			helper.DB2NetworkVlanXml();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodeDao.close();
		}
		return "/tool/isok.jsp";
	}

	private boolean NodeToNodeLinkExist(Link link) {
		boolean flag = false;
		for (int i = 0; i < links.size(); i++) {
			NodeToNodeLink nodelink = links.get(i);
			if (nodelink.getNodeId() == link.getStartId() && nodelink.getNodeparentid() == link.getEndId()) {
				flag = true;
				break;
			}

			if (nodelink.getNodeparentid() == link.getStartId() && nodelink.getNodeId() == link.getEndId()) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public int sendPDU(String name, int version) throws IOException {

		int flag = 1;

		CommunityTarget target = new CommunityTarget();

		target.setCommunity(new OctetString(name));

		target.setAddress(targetAddress);

		// 通信不成功时的重试次数

		target.setRetries(2);

		// 超时时间

		target.setTimeout(1500);

		target.setVersion(version);

		// 创建 PDU

		PDU pdu = new PDU();

		pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1, 1, 1, 0 })));

		// MIB的访问方式

		pdu.setType(PDU.GET);

		// 向Agent发送PDU，并接收Response

		ResponseEvent respEvnt = snmp.send(pdu, target);

		// 解析Response

		if (respEvnt != null && respEvnt.getResponse() != null) {

		} else {
			flag = 0;
		}
		return flag;

	}
}

class ThreadProbe_SOLO extends Thread {
	private DiscoverEngine engine;

	public ThreadProbe_SOLO(DiscoverEngine engine) {
		this.engine = engine;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(50000);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		while (!engine.isDiscovered())// 这里可能会进入死循环
		{
			try {
				Thread.sleep(30000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		DiscoverMonitor.getInstance().setEndTime(SysUtil.getCurrentTime());
		DiscoverMonitor.getInstance().setCompleted(true);

		DiscoverComplete_SOLO dc = new DiscoverComplete_SOLO();
		dc.completed(true);
	}
}
