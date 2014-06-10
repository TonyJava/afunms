package com.afunms.common.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.afunms.discovery.AtInterface;
import com.afunms.discovery.BridgeStpInterface;
import com.afunms.discovery.CdpCachEntryInterface;
import com.afunms.discovery.DiscoverResource;
import com.afunms.discovery.IfEntity;
import com.afunms.discovery.IpAddress;
import com.afunms.discovery.IpRouter;
import com.afunms.discovery.SubNet;
import com.afunms.discovery.TemporaryLink;

@SuppressWarnings("unchecked")
public class SnmpUtil {
	private static SnmpService snmp;
	private static SnmpUtil instance = new SnmpUtil();

	public static synchronized SnmpUtil getInstance() {
		return instance;
	}

	public SnmpUtil() {
		snmp = new SnmpService();
	}

	public boolean _setSysGroup(String address, String community, int version, Hashtable mibvalues) {
		boolean flag = true;
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.1.4.0", // 4.sysContact
					"1.3.6.1.2.1.1.5.0", // 5.sysName
					"1.3.6.1.2.1.1.6.0" }; // 6.sysLocation
			String[] _mibvalue = new String[3];
			_mibvalue[0] = (String) mibvalues.get("sysContact");
			_mibvalue[1] = (String) mibvalues.get("sysName");
			_mibvalue[2] = (String) mibvalues.get("sysLocation");

			flag = snmp._setMibValues(address, community, version, oids, _mibvalue);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return flag;
	}

	/**
	 * identify a device is router or switch 0=unknown,1=router,
	 * 2=route_switch,3=switch,4=server,5=printer,20=other
	 */
	public int checkDevice(String address, String community, String sysOid) {
		String switchtemp = null;
		String forwardtemp = null;
		int deviceType = 0;

		if (sysOid == null) {
			return deviceType;
		}

		try // 从现在源资中判断设备的类型
		{
			if (DiscoverResource.getInstance().getDeviceType() != null) {
				if (DiscoverResource.getInstance().getDeviceType().get(sysOid) != null) {
					deviceType = ((Integer) DiscoverResource.getInstance().getDeviceType().get(sysOid)).intValue();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			deviceType = 0;
		}
		if (deviceType != 0) {
			return deviceType;
		}

		try {
			if (getIfNumber(address, community) < 2) {
				return 0; // 未知
			}
			String[][] ipArray = snmp.getTableData(address, community, new String[] { "1.3.6.1.2.1.4.20.1.1" });
			if (ipArray == null) {
				return 0;
			}

			int isSwitch = -1, ipForward = -1;
			switchtemp = snmp.getMibValue(address, community, "1.3.6.1.2.1.17.1.2.0"); // Bridge－Mib
			if (switchtemp == null) {
				switchtemp = snmp.getMibValue(address, community, "1.3.6.1.2.1.17.1.2"); // Bridge－Mib
			}
			if (switchtemp != null) {
				if (switchtemp.equalsIgnoreCase("noSuchObject")) {
					isSwitch = 0;
				} else {
					isSwitch = Integer.parseInt(switchtemp);
				}
			}

			forwardtemp = snmp.getMibValue(address, community, "1.3.6.1.2.1.4.1.0"); // ipForwording
			if (forwardtemp == null) {
				forwardtemp = snmp.getMibValue(address, community, "1.3.6.1.2.1.4.1"); // ipForwording
			}
			if (forwardtemp != null) {
				ipForward = Integer.parseInt(forwardtemp);
			}

			if (ipForward == 1 && isSwitch == 0) {
				return 1; // 路由
			}
			if (ipForward == 1 && isSwitch > 0) {
				return 2; // 路由交换;
			}
			if (ipForward != 1 && isSwitch > 0) {
				return 3; // 二层交换;
			}
		} catch (Exception e) {
			e.printStackTrace();
			deviceType = 0;
		}
		return deviceType;
	}

	public String ciscoIP2IP(String ciscoip) {
		String[] s = ciscoip.split(":");
		if (4 == s.length) {
			return "" + Integer.parseInt(s[0], 16) + "." + Integer.parseInt(s[1], 16) + "." + Integer.parseInt(s[2], 16) + "." + Integer.parseInt(s[3], 16);
		}

		return "";
	}

	/**
	 * find all possible links between two switches,or router and switch if them
	 * are router and switch,we should put router first.
	 */
	public List findLinks(int id1, String ip1, String community1, int id2, String ip2, String community2) {
		String[] fdbOids = new String[] { "1.3.6.1.2.1.17.4.3.1.1", // 1.mac
				"1.3.6.1.2.1.17.4.3.1.2", // 2.port
				"1.3.6.1.2.1.17.4.3.1.3" }; // 3.type
		String[] ifOids = new String[] { "1.3.6.1.2.1.2.2.1.1", // index
				"1.3.6.1.2.1.2.2.1.6" }; // mac
		String[] portOids = new String[] { "1.3.6.1.2.1.17.1.4.1.2", // 1.index
				"1.3.6.1.2.1.17.1.4.1.1" }; // 2.port

		List links = new ArrayList();
		try {
			String[][] ifTable1 = snmp.getTableData(ip1, community1, ifOids);
			String[][] fdbTable2 = snmp.getTableData(ip2, community2, fdbOids);
			String[][] port2 = snmp.getTableData(ip2, community2, portOids);

			if (ifTable1 == null || fdbTable2 == null || port2 == null) {
				return null;
			}

			HashMap portMap = new HashMap();
			for (int i = 0; i < port2.length; i++) {
				portMap.put(port2[i][1], port2[i][0]);
			}

			HashMap fdbMap = new HashMap();
			for (int j = 0; j < fdbTable2.length; j++) {
				if ("3".equals(fdbTable2[j][2])) {
					fdbMap.put(fdbTable2[j][0], fdbTable2[j][1]);
				}
			}
			for (int i = 0; i < ifTable1.length; i++) {
				if (fdbMap.get(ifTable1[i][1]) != null) {
					String port = (String) fdbMap.get(ifTable1[i][1]);
					String index = (String) portMap.get(port);
					TemporaryLink newLink = null;
					if (id1 > id2) {
						newLink = new TemporaryLink(id2, index, id1, ifTable1[i][0]);
					} else {
						newLink = new TemporaryLink(id1, ifTable1[i][0], id2, index);
					}
					if (!links.contains(newLink)) {
						links.add(newLink);
					}
				}
			}
			if (links.size() == 0) {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			links = null;
		}
		return links;
	}

	/**
	 * 得到所有IpNetToMedia,即直接与该设备连接的ip
	 */
	public List getAtInterfaceTable(String address, String community) {
		List tableValues = null;
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.4.22.1.1", // 1.ifIndex
					"1.3.6.1.2.1.4.22.1.2", // 2.mac
					"1.3.6.1.2.1.4.22.1.3", // 3.ip
					"1.3.6.1.2.1.4.22.1.4" }; // 4.type

			String[][] ipArray = snmp.getTableData(address, community, oids);
			if (ipArray == null) {
				return null;
			}

			tableValues = new ArrayList();
			AtInterface at = new AtInterface();
			for (int i = 0; i < ipArray.length; i++) {
				if (!"3".equals(ipArray[i][3])) {
					continue;
				}
				if (ipArray[i][1].length() != 17) {
					continue;
				}
				at = new AtInterface();
				at.setIfindex(Integer.parseInt(ipArray[i][0]));
				at.setMacAddress(ipArray[i][1]);
				at.setIpAddress(ipArray[i][2]);
				tableValues.add(at);
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * 交换机本身mac
	 */
	public String getBridgeAddress(String address, int snmpversion, String community, int securityLevel, String securityName, int v3_ap, String authPassPhrase, int v3_privacy, String privacyPassPhrase) {
		String bridge = null;
		try {
			bridge = snmp.getMibValue(address, snmpversion, community, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase, "1.3.6.1.2.1.17.1.1.0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bridge;
	}

	/**
	 * 交换机本身mac
	 */
	public String getBridgeAddress(String address, String community) {
		String bridge = null;
		try {
			bridge = snmp.getMibValue(address, community, "1.3.6.1.2.1.17.1.1.0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bridge;
	}

	/**
	 * get stp from router table
	 */
	public List getBridgeStpList(String address, String community) {
		// 现在还没判断是否需要增加Portstate状态的判断
		List tableValues = new ArrayList();
		Hashtable portToIfIndex = new Hashtable();
		String[][] stpArray = null;
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.17.1.4.1.1", // 1.dot1dBasePort
					"1.3.6.1.2.1.17.1.4.1.2" // 2.dot1dBasePortIfIndex
			};

			stpArray = snmp.getTableData(address, community, oids);
			if (stpArray == null) {
				return null;
			}

			for (int i = 0; i < stpArray.length; i++) {
				if (stpArray[i][0] == null) {
					continue;
				}
				portToIfIndex.put(stpArray[i][0], stpArray[i][1]);
			}

			oids = new String[] { "1.3.6.1.2.1.17.2.15.1.1", // 1.dot1dStpPort
					"1.3.6.1.2.1.17.2.15.1.3", // 3.dot1dStpPortState
					"1.3.6.1.2.1.17.2.15.1.8", // 8.dot1dStpPortDesignatedBridge
					"1.3.6.1.2.1.17.2.15.1.9" // 9.dot1dStpPortDesignatedPort
			};

			stpArray = snmp.getTableData(address, community, oids);
			if (stpArray == null) {
				return null;
			}

			for (int i = 0; i < stpArray.length; i++) {
				if (stpArray[i][0] == null) {
					continue;
				}
				if (stpArray[i][2].equalsIgnoreCase("00:00:00:00:00:00:00:00")) {
					continue;
				}
				if (stpArray[i][3].equalsIgnoreCase("00:00")) {
					continue;
				}

				BridgeStpInterface bstp = new BridgeStpInterface();
				bstp.setPort((String) portToIfIndex.get(stpArray[i][0]));
				bstp.setBridge(stpArray[i][2]);
				bstp.setBridgeport(stpArray[i][3]);
				bstp.setIfindex((String) portToIfIndex.get(stpArray[i][0]));
				tableValues.add(bstp);
			}
		} catch (Exception e) {
			stpArray = null;
			e.printStackTrace();
			tableValues = null;
		}
		if (stpArray != null) {
			stpArray = null;
		}
		return tableValues;
	}

	/**
	 * get cdp from router/switch table
	 */
	public List getCiscoCDPList(String address, String community) {
		// 现在还没判断是否需要增加Portstate状态的判断
		List tableValues = new ArrayList();
		String[][] cdpArray1 = null;
		try {
			String[] oids1 = new String[] { "1.3.6.1.4.1.9.9.23.1.2.1.1.4", // 1.cdpCacheAddress
					// "1.3.6.1.4.1.9.9.23.1.2.1.1.5", //3.cdpCacheVersion
					// "1.3.6.1.4.1.9.9.23.1.2.1.1.6", //8.cdpCacheDeviceId
					"1.3.6.1.4.1.9.9.23.1.2.1.1.7" // 9.cdpCacheDevicePort
			// "1.3.6.1.4.1.9.9.23.1.2.1.1.1" //cdpCacheIfIndex
			};

			cdpArray1 = snmp.getTableData(address, community, oids1);
			if (cdpArray1 == null) {
				return null;
			}
			CdpCachEntryInterface cdp = new CdpCachEntryInterface();
			for (int i = 0; i < cdpArray1.length; i++) {
				cdp = new CdpCachEntryInterface();
				if (cdpArray1[i][0] == null) {
					continue;
				}
				cdp.setIp(ciscoIP2IP(cdpArray1[i][0]));
				cdp.setPortdesc(cdpArray1[i][1]);
				tableValues.add(cdp);
			}
		} catch (Exception e) {
			cdpArray1 = null;
			e.printStackTrace();
			tableValues = null;
		}
		if (cdpArray1 != null) {
			cdpArray1 = null;
		}
		return tableValues;
	}

	/**
	 * 得到CISCO的结果表
	 */
	public List getCiscoConfigResultTable(String address, String community) {
		List tableValues = null;
		try {
			String[] oids = new String[] { "1.3.6.1.4.1.9.9.96.1.1.1.1.10" // ccCopyState
			// 1:waiting
			// 2:running
			// 3:successful 4:failed
			};

			String[][] ipArray = snmp.getCpuTableData(address, community, oids);
			if (ipArray == null) {
				return null;
			}

			tableValues = new ArrayList();
			for (int i = 0; i < ipArray.length; i++) {
				List alist = new ArrayList();
				if (ipArray[i][0] != null && ipArray[i][0].trim().length() > 0 && ipArray[i][1] != null && ipArray[i][1].trim().length() > 0) {
					alist.add(0, ipArray[i][0]);
					alist.add(1, ipArray[i][1]);
				}
				tableValues.add(alist);
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * get cisco vlan from router/switch table
	 */
	public Hashtable<Long, Long> getCiscoIIDVlanIdValue(String address, String community) {
		/*
		 * 应该在各个设备的类中实现本函数。 cisco设备 先从CISCO-VLAN-MEMBERSHIP-MIB的
		 * .iso.org.dod.internet.private.enterprises.cisco.ciscoMgmt.ciscoVlanMembershipMIB.ciscoVlanMembershipMIBObjects.vmMembership.vmMembershipTable.vmMembershipEntry.vmVlan
		 * .1.3.6.1.4.1.9.9.68.1.2.2.1.2 节点获取端口id与vlan
		 * id的映射关系。如果成功获取则直接返回，否则执行下一步： 从CISCO-VLAN-IFTABLE-RELATIONSHIP-MIB
		 * .iso.org.dod.internet.private.enterprises.cisco.ciscoMgmt.ciscoVlanIfTableRelationshipMIB.cviMIBObjects.cviGlobals.cviVlanInterfaceIndexTable.cviVlanInterfaceIndexEntry.cviRoutedVlanIfIndex
		 * .1.3.6.1.4.1.9.9.128.1.1.1.1.3 节点获取vlan
		 * id与端口id的映射关系（返回之前要调换位置！！）。如果成功获取则直接返回,失败返回空
		 * 
		 * 返回节点获取端口id与vlan id的映射关系。
		 * 
		 * 其他设备参见SNMP-Info-1.04\SNMP-Info-1.04 下的i_vlan函数的实现。
		 */
		Hashtable<Long, Long> result = new Hashtable<Long, Long>();
		try {
			String[] oids1 = new String[] { "1.3.6.1.4.1.9.9.68.1.2.2.1.2" // vmVlan
			};

			String[][] vlanArray1 = null;
			try {
				vlanArray1 = snmp.getCiscoVlanTableData(address, community, oids1);
				if (vlanArray1 == null) {
					return null;
				}
				for (int i = 0; i < vlanArray1.length; i++) {
					result.put(new Long(vlanArray1[i][0]), new Long(vlanArray1[i][1]));// 端口ID:VLANID
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (result != null && result.size() > 0) {
				return result;
			}
			oids1 = new String[] { "1.3.6.1.4.1.9.9.128.1.1.1.1.3" // cviRoutedVlanIfIndex
			};
			try {
				vlanArray1 = snmp.getCiscoVlanTableData(address, community, oids1);
				if (vlanArray1 == null) {
					return null;
				}

				for (int i = 0; i < vlanArray1.length; i++) {
					result.put(new Long(vlanArray1[i][0]), new Long(vlanArray1[i][1]));// 端口ID:VLANID
				}
			} catch (Exception ex) {

			}
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}
		return result;
	}

	/**
	 * 确定community
	 */
	public String getCommunity(String address) {
		String community = null;
		String sysOid = null;
		try // 确定是否有特定的community
		{
			community = (String) DiscoverResource.getInstance().getSpecifiedCommunity().get(address);
		} catch (Exception e) {
			community = null;
		}
		if (community != null) {
			return community;
		}
		Iterator communityList = null;
		if (DiscoverResource.getInstance().getCommunitySet() != null) {
			communityList = DiscoverResource.getInstance().getCommunitySet().iterator();
			if (communityList != null) {
				while (communityList.hasNext()) {
					community = (String) communityList.next();
					sysOid = getSysOid(address, community);
					if (sysOid != null) {
						break;
					}
				}
			}
		}
		if (sysOid == null) {
			return DiscoverResource.getInstance().getCommunity();// 用缺省的团体名称
		} else {
			return community;// 返回能取到值的团体名称
		}
	}

	public HashMap getDtpFdbTable(String address, String community) {
		String[] oids1 = new String[] { "1.3.6.1.2.1.17.4.3.1.1", // 1.mac
				"1.3.6.1.2.1.17.4.3.1.2", // 2.port
				"1.3.6.1.2.1.17.4.3.1.3" }; // 3.type

		String[][] ipArray1 = null;
		HashMap<Integer, Set<String>> portMacs = new HashMap<Integer, Set<String>>();
		try {
			Set<String> macs = new HashSet<String>();

			ipArray1 = snmp.getTableData(address, community, oids1);
			for (int i = 0; i < ipArray1.length; i++) {
				if (ipArray1[i][0] == null) {
					continue;
				}
				if (ipArray1[i][1] == null) {
					continue;
				}

				if (!"3".equals(ipArray1[i][2])) {
					continue; // only type=learned
				}
				if (portMacs.containsKey(new Integer(ipArray1[i][1]))) {
					macs = portMacs.get(new Integer(ipArray1[i][1]));
				}
				macs.add(ipArray1[i][0]);
				portMacs.put(new Integer(ipArray1[i][1]), macs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return portMacs;
	}

	public List getFdbTable(String address, String community) {
		String[] oids1 = new String[] { "1.3.6.1.2.1.17.4.3.1.1", // 1.mac
				"1.3.6.1.2.1.17.4.3.1.2", // 2.port
				"1.3.6.1.2.1.17.4.3.1.3" }; // 3.type

		String[] oids2 = new String[] { "1.3.6.1.2.1.17.1.4.1.2", // 1.index
				"1.3.6.1.2.1.17.1.4.1.1" }; // 2.port

		String[][] ipArray1 = null;
		String[][] ipArray2 = null;
		List tableValues = new ArrayList(30);
		try {
			HashMap portMap = new HashMap();
			ipArray2 = snmp.getTableData(address, community, oids2);
			for (int i = 0; i < ipArray2.length; i++) {
				portMap.put(ipArray2[i][1], ipArray2[i][0]);
			}

			ipArray1 = snmp.getTableData(address, community, oids1);
			String[] item = null;
			for (int i = 0; i < ipArray1.length; i++) {
				if (!"3".equals(ipArray1[i][2])) {
					continue; // only type=learned
				}
				if (portMap.get(ipArray1[i][1]) == null) {
					continue;
				}

				String ifIndex = (String) portMap.get(ipArray1[i][1]);
				item = new String[2];
				item[0] = ifIndex;
				item[1] = ipArray1[i][0];
				tableValues.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableValues;
	}

	/**
	 * 得到H3C的结果表
	 */
	public List getH3cConfigResultTable(String address, String community) {
		List tableValues = null;
		try {
			String[] oids = new String[] { "1.3.6.1.4.1.2011.10.2.4.1.2.5.1.2", // 1.resultOptIndex
					"1.3.6.1.4.1.2011.10.2.4.1.2.5.1.4" }; // OperateState

			String[][] ipArray = snmp.getTableData(address, community, oids);
			if (ipArray == null) {
				return null;
			}

			tableValues = new ArrayList();
			for (int i = 0; i < ipArray.length; i++) {
				List alist = new ArrayList();
				if (ipArray[i][0] != null && ipArray[i][0].trim().length() > 0 && ipArray[i][1] != null && ipArray[i][1].trim().length() > 0) {
					alist.add(0, ipArray[i][0]);
					alist.add(1, ipArray[i][1]);
				}
				tableValues.add(alist);
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * 被监视对象本身mac
	 */
	public String getHostBridgeAddress(String address, String community) {
		StringBuffer sb = new StringBuffer();
		Set bs = new HashSet();
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.2.2.1.6" };
			String[][] valueArray = (String[][]) null;
			try {
				valueArray = SnmpUtils.getTable(address, community, oids, 0, 3, 10 * 1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (valueArray != null) {
				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i][0] == null || valueArray[i][0].length() == 0 || !valueArray[i][0].contains(":") || valueArray[i][0].startsWith("00:00:00:00:00:00:00")) {
						continue;
					} else {
						bs.add(valueArray[i][0].trim());
					}
				}
				Iterator it = bs.iterator();
				while (it.hasNext()) {
					sb.append(it.next());
					sb.append(",");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.substring(0, sb.length() - 1);
	}

	public List getIfEntityList(String address, List<String> vlanCommunities) {
		List allIfs = new ArrayList();
		for (String temp : vlanCommunities) {
			List ifs = SnmpUtil.getInstance().getIfEntityList("10.10.10.252", temp, 2);
			if (ifs != null) {
				allIfs.addAll(ifs);
			}
		}
		return allIfs;
	}

	/**
	 * 得到所有接口的相关信息(2006.06.30) 增加category,如果是路由器，if_index=if_port(2007.01.16)
	 */
	public List getIfEntityList(String address, String community, int category) {
		List tableValues = new ArrayList(50);
		Hashtable<String, IfEntity> ifHash = new Hashtable<String, IfEntity>();
		IfEntity ifEntity = new IfEntity();

		String[][] ifTableArray = null;
		String[] ifTableOids = new String[] { "1.3.6.1.2.1.2.2.1.1", // index
				"1.3.6.1.2.1.2.2.1.2",// descr
				"1.3.6.1.2.1.2.2.1.3",// type
				"1.3.6.1.2.1.2.2.1.5", // speed
				"1.3.6.1.2.1.2.2.1.6", // mac
				"1.3.6.1.2.1.2.2.1.8"// operstatus
		};

		String[][] dot1dBasePortTableArray = null;
		Hashtable<String, String> dot1dBasePortTableHt = new Hashtable<String, String>();
		String[] dot1dBasePortTableOids = new String[] { "1.3.6.1.2.1.17.1.4.1.2", // 1.index
				"1.3.6.1.2.1.17.1.4.1.1" }; // 2.port

		String[][] ipAddrTableArray = null;
		String[] ipAddrTableOids = new String[] { "1.3.6.1.2.1.4.20.1.2", // 1.index
				"1.3.6.1.2.1.4.20.1.1" }; // 2.ip
		try {
			ifTableArray = SnmpUtils.getTable(address, community, ifTableOids, 0, 3, 10 * 1000);
			dot1dBasePortTableArray = SnmpUtils.getTable(address, community, dot1dBasePortTableOids, 0, 3, 10 * 1000);
			ipAddrTableArray = SnmpUtils.getTable(address, community, ipAddrTableOids, 0, 3, 10 * 1000);

			if (dot1dBasePortTableArray != null && dot1dBasePortTableArray.length > 0) {
				for (int i = 0; i < dot1dBasePortTableArray.length; i++) {
					dot1dBasePortTableHt.put(dot1dBasePortTableArray[i][0], dot1dBasePortTableArray[i][1]);
				}
			}

			if (ifTableArray != null && ifTableArray.length > 0) {
				String Descr = (String) null;
				String ifIndex = (String) null;
				String[] position = (String[]) null;

				for (int i = 0; i < ifTableArray.length; i++) {
					ifEntity = new IfEntity();
					ifIndex = ifTableArray[i][0];
					Descr = ifTableArray[i][1];
					if (ifIndex == null) {
						continue;
					}
					ifEntity.setIndex(ifIndex);
					ifEntity.setDescr(Descr);
					if (Descr.toLowerCase().indexOf("ethernet") > 0) {
						position = Descr.substring(Descr.lastIndexOf("t") + 1).split("/");
						if (position.length == 3) {
							try {
								ifEntity.setChassis(Integer.parseInt(position[0]));
								ifEntity.setSlot(Integer.parseInt(position[1]));
								ifEntity.setUport(Integer.parseInt(position[2]));
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

					if (category == 1) {
						ifEntity.setPort(ifIndex);
					} else {
						ifEntity.setPort(dot1dBasePortTableHt.get(ifIndex));
					}
					ifEntity.setType(Integer.parseInt(ifTableArray[i][2]));
					ifEntity.setOperStatus(Integer.parseInt(ifTableArray[i][5]));
					ifEntity.setSpeed(ifTableArray[i][3]);
					ifEntity.setPhysAddress(ifTableArray[i][4]);
					ifHash.put(ifIndex, ifEntity);
				}
			}

			if (ipAddrTableArray != null && ipAddrTableArray.length > 0) {
				for (int i = 0; i < ipAddrTableArray.length; i++) {
					if (ifHash.get(ipAddrTableArray[i][0]) != null) {
						ifEntity = ifHash.get(ipAddrTableArray[i][0]);
						// 一个接口多个IP的问题
						if (ifEntity.getIpAddress() == null) {
							ifEntity.setIpAddress(ipAddrTableArray[i][1]);
							ifEntity.setIpList(ipAddrTableArray[i][1]);
						} else {
							ifEntity.setIpList(ifEntity.getIpList() + "," + ipAddrTableArray[i][1]);
						}
					}
				}
			}
			Enumeration e = ifHash.elements();
			while (e.hasMoreElements()) {
				tableValues.add(e.nextElement());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tableValues;
	}

	/**
	 * 博科 得到所有接口的相关信息(2006.06.30) 增加category,如果是路由器，if_index=if_port(2007.01.16)
	 */
	public List getIfEntityList_brocade(String address, String community, int category) {
		List tableValues = new ArrayList(50);
		String[] oids1 = new String[] { "1.3.6.1.4.1.1588.2.1.1.1.6.2.1.1", // index
				"1.3.6.1.4.1.1588.2.1.1.1.6.2.1.36", // descr
				"1.3.6.1.2.1.2.2.1.5", // speed
				"1.3.6.1.2.1.2.2.1.6", // mac
				"1.3.6.1.4.1.1588.2.1.1.1.6.2.1.4", // operstatus
				"1.3.6.1.4.1.1588.2.1.1.1.6.2.1.2" }; // type

		String[] oids2 = new String[] { "1.3.6.1.4.1.1588.2.1.1.1.6.2.1.1", // 1.index
				"1.3.6.1.2.1.17.1.4.1.1" }; // 2.port

		String[] oids3 = new String[] { "1.3.6.1.4.1.1588.2.1.1.1.6.2.1.1", // 1.index
				"1.3.6.1.2.1.4.20.1.1" }; // 2.ip
		try {
			String[][] ipArray = snmp.getTableData(address, community, oids1);
			if (ipArray == null) {
				return null;
			}

			Hashtable ifHash = new Hashtable();
			for (int i = 0; i < ipArray.length; i++) {
				if (ipArray[i][0] == null) {
					continue; // (2006.08.30)
				}

				IfEntity ifEntity = new IfEntity();
				if (ipArray[i][0] == null) {
					ifEntity.setIndex("");
				} else {
					ifEntity.setIndex(ipArray[i][0]);
				}
				if (ipArray[i][1].length() < 50) {
					ifEntity.setDescr(ipArray[i][1]);
				} else {
					ifEntity.setDescr(ipArray[i][1].substring(0, 50));
				}
				// 依据DESCR处理设备面板
				if (ifEntity.getDescr() != null) {
					String descr = ifEntity.getDescr();
					if (descr.indexOf("GigabitEthernet") >= 0) {
						String allchassis = descr.substring(descr.lastIndexOf("t") + 1);
						String[] chassis = allchassis.split("/");
						if (chassis.length == 3) {
							String str_chassis = chassis[0];
							String slot = chassis[1];
							String uport = chassis[2];
							try {
								ifEntity.setChassis(Integer.parseInt(str_chassis));
							} catch (Exception chassex) {
								ifEntity.setChassis(-1);
							}
							try {
								ifEntity.setSlot(Integer.parseInt(slot));
							} catch (Exception chassex) {
								ifEntity.setSlot(-1);
							}
							try {
								ifEntity.setUport(Integer.parseInt(uport));
							} catch (Exception chassex) {
								ifEntity.setUport(-1);
							}
						}
					} else if (descr.indexOf("Ethernet") == 0) {
						String allchassis = descr.substring(descr.lastIndexOf("t") + 1);
						String[] chassis = allchassis.split("/");
						if (chassis.length == 3) {
							String str_chassis = chassis[0];
							String slot = chassis[1];
							String uport = chassis[2];
							try {
								ifEntity.setChassis(Integer.parseInt(str_chassis));
							} catch (Exception chassex) {
								ifEntity.setChassis(-1);
							}
							try {
								ifEntity.setSlot(Integer.parseInt(slot));
							} catch (Exception chassex) {
								ifEntity.setSlot(-1);
							}
							try {
								ifEntity.setUport(Integer.parseInt(uport));
							} catch (Exception chassex) {
								ifEntity.setUport(-1);
							}
						}
					}
				}
				ifEntity.setSpeed(ipArray[i][2]);
				ifEntity.setPhysAddress(ipArray[i][3]);
				if ("1".equals(ipArray[i][4])) {
					ifEntity.setOperStatus(1);
				} else {
					ifEntity.setOperStatus(2);
				}
				ifEntity.setIpAddress("");
				ifEntity.setIpList("");
				if (category == 1) {
					ifEntity.setPort(ifEntity.getIndex());
				} else {
					ifEntity.setPort("");
				}
				ifEntity.setType(Integer.parseInt(ipArray[i][5]));
				tableValues.add(ifEntity);
				ifHash.put(ipArray[i][0], ifEntity);
			}

			if (category != 1) {
				String[][] ipArray2 = null;
				try {
					ipArray2 = snmp.getTableData(address, community, oids2);
				} catch (Exception e) {

				}
				if (ipArray2 == null) {
					return null;
				}

				for (int i = 0; i < ipArray2.length; i++) {
					if (ipArray2[i][0] == null) {
						continue;
					}

					if (ifHash.get(ipArray2[i][0]) == null) {
						continue;
					}
					IfEntity ifEntity = (IfEntity) ifHash.get(ipArray2[i][0]);
					ifEntity.setPort(ipArray2[i][1]);
				}
			}
			String[][] ipArray3 = null;
			try {
				ipArray3 = snmp.getTableData(address, community, oids3);
			} catch (Exception e) {

			}
			if (ipArray3 == null) {
				return null;
			}

			for (int i = 0; i < ipArray3.length; i++) {
				if (ipArray3[i][0] == null) {
					continue;
				}
				if (ifHash.get(ipArray3[i][0]) == null) {
					continue;
				}
				IfEntity ifEntity = (IfEntity) ifHash.get(ipArray3[i][0]);
				if (ipArray3[i][1].startsWith("127")) {
					continue; // 过滤掉本地IP
				}
				if (ifEntity.getIpAddress().equals("")) // 解决一个接口多个IP的问题
				{
					ifEntity.setIpAddress(ipArray3[i][1]);
					ifEntity.setIpList(ipArray3[i][1]);
				} else {
					ifEntity.setIpList(ifEntity.getIpList() + "," + ipArray3[i][1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * 得到所有接口的相关信息(2011.09.7) 增加category,如果是路由器，if_index=if_port(2007.01.16)
	 * konglq
	 */
	public List getIfEntityList2(String address, String community, int category, int snmpversion) {
		List tableValues = new ArrayList(50);

		String[] oids0 = new String[] { "1.3.6.1.2.1.2.2.1.1", // index
				"1.3.6.1.2.1.2.2.1.2", // descr
				"1.3.6.1.2.1.2.2.1.5" // speed
		}; // type
		String[] oids1 = new String[] { "1.3.6.1.2.1.2.2.1.6", // mac
				"1.3.6.1.2.1.2.2.1.8", // operstatus
				"1.3.6.1.2.1.2.2.1.3" }; // type

		String[] oids2 = new String[] { "1.3.6.1.2.1.17.1.4.1.2", // 1.index
				"1.3.6.1.2.1.17.1.4.1.1" }; // 2.port

		String[] oids3 = new String[] { "1.3.6.1.2.1.4.20.1.2", // 1.index
				"1.3.6.1.2.1.4.20.1.1" }; // 2.ip
		try {
			String[][] ipArray = SnmpUtils.getTableData(address, community, oids0, snmpversion, 3, 1000 * 60);
			String[][] ipArray4 = SnmpUtils.getTableData(address, community, oids1, snmpversion, 3, 1000 * 60);

			if (ipArray == null) {
				return null;
			}

			Hashtable ifHash = new Hashtable();
			for (int i = 0; i < ipArray.length; i++) {
				if (ipArray[i][0] == null) {
					continue; // (2006.08.30)
				}

				IfEntity ifEntity = new IfEntity();
				if (ipArray[i][0] == null) {
					ifEntity.setIndex("");
				} else {
					ifEntity.setIndex(ipArray[i][0]);
				}
				if (ipArray[i][1].length() < 50) {
					ifEntity.setDescr(ipArray[i][1]);
				} else {
					ifEntity.setDescr(ipArray[i][1].substring(0, 50));
				}
				// 依据DESCR处理设备面板
				if (ifEntity.getDescr() != null) {
					String descr = ifEntity.getDescr();
					if (descr.indexOf("GigabitEthernet") >= 0) {
						String allchassis = descr.substring(descr.lastIndexOf("t") + 1);
						String[] chassis = allchassis.split("/");
						if (chassis.length == 3) {
							String str_chassis = chassis[0];
							String slot = chassis[1];
							String uport = chassis[2];
							try {
								ifEntity.setChassis(Integer.parseInt(str_chassis));
							} catch (Exception chassex) {
								ifEntity.setChassis(-1);
							}
							try {
								ifEntity.setSlot(Integer.parseInt(slot));
							} catch (Exception chassex) {
								ifEntity.setSlot(-1);
							}
							try {
								ifEntity.setUport(Integer.parseInt(uport));
							} catch (Exception chassex) {
								ifEntity.setUport(-1);
							}
						}
					} else if (descr.indexOf("Ethernet") == 0) {
						String allchassis = descr.substring(descr.lastIndexOf("t") + 1);
						String[] chassis = allchassis.split("/");
						if (chassis.length == 3) {
							String str_chassis = chassis[0];
							String slot = chassis[1];
							String uport = chassis[2];
							try {
								ifEntity.setChassis(Integer.parseInt(str_chassis));
							} catch (Exception chassex) {
								ifEntity.setChassis(-1);
							}
							try {
								ifEntity.setSlot(Integer.parseInt(slot));
							} catch (Exception chassex) {
								ifEntity.setSlot(-1);
							}
							try {
								ifEntity.setUport(Integer.parseInt(uport));
							} catch (Exception chassex) {
								ifEntity.setUport(-1);
							}
						}
					}
				}
				ifEntity.setSpeed(ipArray[i][2]);
				ifEntity.setPhysAddress(ipArray4[i][0]);
				if ("1".equals(ipArray4[i][1])) {
					ifEntity.setOperStatus(1);
				} else {
					ifEntity.setOperStatus(2);
				}
				ifEntity.setIpAddress("");
				ifEntity.setIpList("");
				if (category == 1) {
					ifEntity.setPort(ifEntity.getIndex());
				} else {
					ifEntity.setPort("");
				}
				ifEntity.setType(Integer.parseInt(ipArray4[i][2]));
				tableValues.add(ifEntity);
				ifHash.put(ipArray[i][0], ifEntity);
			}

			if (category != 1) {
				String[][] ipArray2 = null;
				try {
					ipArray2 = snmp.getTableData(address, community, oids2);
				} catch (Exception e) {

				}
				if (ipArray2 == null) {
					return null;
				}

				for (int i = 0; i < ipArray2.length; i++) {
					if (ipArray2[i][0] == null) {
						continue;
					}

					if (ifHash.get(ipArray2[i][0]) == null) {
						continue;
					}
					IfEntity ifEntity = (IfEntity) ifHash.get(ipArray2[i][0]);
					ifEntity.setPort(ipArray2[i][1]);
				}
			}

			String[][] ipArray3 = null;
			try {
				ipArray3 = snmp.getTableData(address, community, oids3);
			} catch (Exception e) {

			}
			if (ipArray3 == null) {
				return null;
			}

			for (int i = 0; i < ipArray3.length; i++) {
				if (ipArray3[i][0] == null) {
					continue;
				}
				if (ifHash.get(ipArray3[i][0]) == null) {
					continue;
				}
				IfEntity ifEntity = (IfEntity) ifHash.get(ipArray3[i][0]);
				if (ipArray3[i][1].startsWith("127")) {
					continue; // 过滤掉本地IP
				}
				if (ifEntity.getIpAddress().equals("")) // 解决一个接口多个IP的问题
				{
					ifEntity.setIpAddress(ipArray3[i][1]);
					ifEntity.setIpList(ipArray3[i][1]);
				} else {
					ifEntity.setIpList(ifEntity.getIpList() + "," + ipArray3[i][1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * 得到一个设备接口数
	 */
	public int getIfNumber(String address, String community) {
		int ifNumber = 0;
		try {
			ifNumber = Integer.parseInt(snmp.getMibValue(address, community, "1.3.6.1.2.1.2.1.0"));
		} catch (Exception e) {
			ifNumber = 0;
		}
		return ifNumber;
	}

	/**
	 * 得到所有IpNetToMedia,即直接与该设备连接的ip
	 */
	public List getIpNetToMediaTable(String address, String community) {
		List tableValues = null;
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.4.22.1.1", // 1.ifIndex
					"1.3.6.1.2.1.4.22.1.2", // 2.mac
					"1.3.6.1.2.1.4.22.1.3", // 3.ip
					"1.3.6.1.2.1.4.22.1.4" }; // 4.type

			String[][] ipArray = snmp.getTableData(address, community, oids);
			if (ipArray == null) {
				return null;
			}

			tableValues = new ArrayList();
			IpAddress ipAddress = null;
			for (int i = 0; i < ipArray.length; i++) {
				if (!"3".equals(ipArray[i][3])) {
					continue;
				}
				if (ipArray[i][1].length() != 17) {
					continue;
				}
				ipAddress = new IpAddress();
				ipAddress.setIfIndex(ipArray[i][0]);
				ipAddress.setPhysAddress(ipArray[i][1]);
				ipAddress.setIpAddress(ipArray[i][2]);
				tableValues.add(ipAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * 得到ip router table,用于Tool
	 */
	public List getIPRouterTable(String address, String community) {
		List tableValues = null;
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.4.21.1.1", // 1.ipRouterDest
					"1.3.6.1.2.1.4.21.1.7", // 7.ipRouterNextHop
					"1.3.6.1.2.1.4.21.1.8", // 8.ipRouterType
					"1.3.6.1.2.1.4.21.1.11", // 11.ipRouterMask
					"1.3.6.1.2.1.4.21.1.2" }; // 0.if index

			String[][] ipArray = snmp.getTableData(address, community, oids);
			if (ipArray == null) {
				return null;
			}

			tableValues = new ArrayList();
			IpRouter ipRouter = null;
			for (int i = 0; i < ipArray.length; i++) {
				int ipType = 0;
				try {
					ipType = Integer.parseInt(ipArray[i][2]);
				} catch (NumberFormatException e) {
					ipType = 0;
				}
				ipRouter = new IpRouter();
				ipRouter.setDest(ipArray[i][0]);
				ipRouter.setNextHop(ipArray[i][1]);
				ipRouter.setType(ipType);
				ipRouter.setMask(ipArray[i][3]);
				ipRouter.setIfIndex(ipArray[i][4]);
				tableValues.add(ipRouter);
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * mac
	 */
	public String getMacAddress(String address, String community) {
		String bridge = "";
		String[] oids = new String[] { "1.3.6.1.2.1.2.2.1.6" }; // ifPhysAddress
		try {
			String[][] macArray = snmp.getTableData(address, community, oids);
			if (macArray == null) {
				return null;
			}
			String mac = "";
			List maclist = new ArrayList();
			for (int i = 0; i < macArray.length; i++) {
				mac = macArray[i][0];
				if ("00:00:00:00:00:00".equalsIgnoreCase(mac)) {
					continue;
				}
				if (maclist.contains(mac)) {
					continue;
				}
				maclist.add(mac);
				bridge = bridge + "|" + mac;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bridge;
	}

	public String[][] getMacIPTable(String address, String community) {
		String[] oids = new String[] { "1.3.6.1.2.1.4.22.1.2", // mac
				"1.3.6.1.2.1.4.22.1.3" }; // ip

		String[][] ipArray = null;
		try {
			ipArray = snmp.getTableData(address, community, oids);
		} catch (Exception e) {
			SysLogger.error("getMacIPTable(),ip=" + address + ",community=" + community);
		}
		return ipArray;
	}

	/**
	 * 得到Huawei的NDP信息
	 */
	public Hashtable getNDPTable(String address, String community) {
		Hashtable tableValues = null;
		try {
			String[] oids = new String[] { "1.3.6.1.4.1.2011.6.7.5.6.1.1", // 1.hwNDPPortNbDeviceId
					"1.3.6.1.4.1.2011.6.7.5.6.1.2" }; // 2.hwNDPPortNbPortName
			// "1.3.6.1.4.1.2011.6.7.5.6.1.3", // 3.hwNDPPortNbDeviceName
			// "1.3.6.1.4.1.2011.6.7.5.6.1.4" }; // 4.hwNDPPortNbPortMode

			String[] oidsNew = new String[] { "1.3.6.1.4.1.25506.8.7.5.6.1.1", // 1.hwNDPPortNbDeviceId
					"1.3.6.1.4.1.25506.8.7.5.6.1.2" }; // 2.hwNDPPortNbPortName
			// "1.3.6.1.4.1.25506.8.7.5.6.1.3", // 3.hwNDPPortNbDeviceName
			// "1.3.6.1.4.1.25506.8.7.5.6.1.4" }; // 4.hwNDPPortNbPortMode

			String[][] ipArray = null;

			try {
				ipArray = SnmpUtils.walkTable(address, community, oids, 0, 1, "", 0, "", 0, "", 3, 1000 * 30);
			} catch (Exception e) {
				ipArray = null;
			}

			if (ipArray == null) {
				try {
					ipArray = SnmpUtils.walkTable(address, community, oids, 1, 1, "", 0, "", 0, "", 3, 1000 * 30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (ipArray == null) {
				try {
					ipArray = SnmpUtils.walkTable(address, community, oidsNew, 1, 1, "", 0, "", 0, "", 3, 1000 * 30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (ipArray == null) {
				try {
					ipArray = SnmpUtils.walkTable(address, community, oidsNew, 1, 1, "", 0, "", 0, "", 3, 1000 * 30);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (ipArray == null) {
				return null;
			}

			tableValues = new Hashtable();
			for (int i = 0; i < ipArray.length; i++) {
				if (ipArray[i][0] == null || ipArray[i][1] == null) {
					continue;
				}
				tableValues.put(ipArray[i][0], ipArray[i][1]);
				SysLogger.info(address + " DeviceId:" + ipArray[i][0] + " PortName:" + ipArray[i][1]);
			}

		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * 得到端口号
	 */
	public String getPort(String address, String community, String mac) {
		if (mac == null) {
			return null;
		}

		return snmp.getMibValue(address, community, NetworkUtil.getTheFdbOid(mac));
	}

	/**
	 * 从ip router table中得到与该设备相连的路由器
	 */
	public List getRouterList(String address, String community) {
		String[] oids = new String[] { "1.3.6.1.2.1.4.21.1.2", // 0.if index
				"1.3.6.1.2.1.4.21.1.1", // 1.ipRouterDest
				"1.3.6.1.2.1.4.21.1.7", // 7.ipRouterNextHop
				"1.3.6.1.2.1.4.21.1.8", // 8.ipRouterType
				"1.3.6.1.2.1.4.21.1.9", // 9.ipRouterProto
				"1.3.6.1.2.1.4.21.1.11", "1.3.6.1.2.1.4.21.1.3" }; // 11.ipRouterMetric1

		List tableValues = new ArrayList();
		try {
			String[][] ipArray = snmp.getTableData(address, community, oids);
			if (ipArray == null) {
				return null;
			}

			Hashtable macHash = null;
			String[] oids2 = new String[] { "1.3.6.1.2.1.4.22.1.3", // 1.ip
					"1.3.6.1.2.1.4.22.1.2" }; // 2.mac
			String[][] macArray = snmp.getTableData(address, community, oids2);
			macHash = new Hashtable();
			for (int i = 0; i < macArray.length; i++) {
				macHash.put(macArray[i][0], macArray[i][1]);
			}
			IpRouter ipr = new IpRouter();
			for (int i = 0; i < ipArray.length; i++) {
				if (ipArray[i][5].equals("0.0.0.0") || ipArray[i][1].equals("0.0.0.0") || ipArray[i][1].startsWith("127.0")) {
					continue;
				}
				if (Integer.parseInt(ipArray[i][6]) == -1) {
					continue;
				}
				ipr = new IpRouter();
				ipr.setIfIndex(ipArray[i][0]);
				ipr.setDest(ipArray[i][1]);
				ipr.setNextHop(ipArray[i][2]);
				ipr.setType(Integer.parseInt(ipArray[i][3]));
				ipr.setProto(Integer.parseInt(ipArray[i][4]));
				ipr.setMask(ipArray[i][5]);
				ipr.setMetric(Integer.parseInt(ipArray[i][6]));
				if (!tableValues.contains(ipr)) // 不存在则加入
				{
					if (macHash.get(ipArray[i][2]) != null) {
						ipr.setPhysAddress((String) macHash.get(ipArray[i][2]));
					}
					tableValues.add(ipr);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * get subnet from router table
	 */
	public List getSubNetList(String address, String community) {
		List tableValues = new ArrayList();
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.4.21.1.2", // 0.if
					// index
					"1.3.6.1.2.1.4.21.1.1", // 1.ipRouterDest
					"1.3.6.1.2.1.4.21.1.7", // 7.ipRouterNextHop
					"1.3.6.1.2.1.4.21.1.8", // 8.ipRouterType
					"1.3.6.1.2.1.4.21.1.11" }; // 11.ipRouterMask

			String[][] ipArray = snmp.getTableData(address, community, oids);
			if (ipArray == null) {
				return null;
			}

			for (int i = 0; i < ipArray.length; i++) {
				if (!"3".equals(ipArray[i][3])) {
					continue; // 不是direct的是不处理
				}
				if (ipArray[i][4].equals("255.255.255.255") || ipArray[i][4].equals("0.0.0.0") || ipArray[i][2].startsWith("127.0") || ipArray[i][1].startsWith("127.0") || ipArray[i][2].equals("0.0.0.0")) {
					continue;
				}

				if (NetworkUtil.isNetAddress(ipArray[i][1], ipArray[i][4])) {
					SubNet subNet = new SubNet();
					subNet.setIfIndex(ipArray[i][0]);
					subNet.setNetAddress(ipArray[i][1]);
					subNet.setIpAddress(ipArray[i][2]);
					subNet.setNetMask(ipArray[i][4]);
					if (!tableValues.contains(subNet)) {
						tableValues.add(subNet);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * 得到系统描述
	 */
	public String getSysDescr(String address, String community) {
		return snmp.getMibValue(address, community, "1.3.6.1.2.1.1.1.0");
	}

	/**
	 * get subnet from router table
	 */
	public Hashtable getSysGroup(String address, int snmpversion, String community, int securityLevel, String securityName, int v3_ap, String authPassPhrase, int v3_privacy, String privacyPassPhrase) {
		Hashtable tableValues = new Hashtable();
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.1.1", // 0.sysDescr
					"1.3.6.1.2.1.1.2", // 1.objectId
					"1.3.6.1.2.1.1.3", // 2.sysUpTime
					"1.3.6.1.2.1.1.4", // 3.sysContact
					"1.3.6.1.2.1.1.5", // 4.sysName
					"1.3.6.1.2.1.1.6", // 5.sysLocation
					"1.3.6.1.2.1.1.7" }; // 6.sysService

			String[][] ipArray = SnmpUtils.getTableData(address, community, oids, snmpversion, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase, 3, 1000 * 30);
			if (ipArray == null) {
				return null;
			}
			if (ipArray[0][0] != null) {
				tableValues.put("sysDescr", ipArray[0][0]);
			} else {
				tableValues.put("sysDescr", "");
			}
			if (ipArray[0][1] != null) {
				tableValues.put("objectId", ipArray[0][1]);
			} else {
				tableValues.put("objectId", "");
			}
			if (ipArray[0][2] != null) {
				tableValues.put("sysUpTime", ipArray[0][2]);
			} else {
				tableValues.put("sysUpTime", "");
			}
			if (ipArray[0][3] != null) {
				tableValues.put("sysContact", ipArray[0][3]);
			} else {
				tableValues.put("sysContact", "");
			}
			if (ipArray[0][4] != null) {
				tableValues.put("sysName", ipArray[0][4]);
			} else {
				tableValues.put("sysName", "");
			}
			if (ipArray[0][5] != null) {
				tableValues.put("sysLocation", ipArray[0][5]);
			} else {
				tableValues.put("sysLocation", "");
			}
			if (ipArray[0][6] != null) {
				tableValues.put("sysService", ipArray[0][6]);
			} else {
				tableValues.put("sysService", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * get subnet from router table
	 */
	public Hashtable getSysGroup(String address, String community) {
		Hashtable tableValues = new Hashtable();
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.1.1", // 0.sysDescr
					"1.3.6.1.2.1.1.2", // 1.objectId
					"1.3.6.1.2.1.1.3", // 2.sysUpTime
					"1.3.6.1.2.1.1.4", // 3.sysContact
					"1.3.6.1.2.1.1.5", // 4.sysName
					"1.3.6.1.2.1.1.6", // 5.sysLocation
					"1.3.6.1.2.1.1.7" }; // 6.sysService

			String[][] ipArray = snmp.getTableData(address, community, oids);
			if (ipArray == null) {
				return null;
			}
			if (ipArray[0][0] != null) {
				tableValues.put("sysDescr", ipArray[0][0]);
			} else {
				tableValues.put("sysDescr", "");
			}
			if (ipArray[0][1] != null) {
				tableValues.put("objectId", ipArray[0][1]);
			} else {
				tableValues.put("objectId", "");
			}
			if (ipArray[0][2] != null) {
				tableValues.put("sysUpTime", ipArray[0][2]);
			} else {
				tableValues.put("sysUpTime", "");
			}
			if (ipArray[0][3] != null) {
				tableValues.put("sysContact", ipArray[0][3]);
			} else {
				tableValues.put("sysContact", "");
			}
			if (ipArray[0][4] != null) {
				tableValues.put("sysName", ipArray[0][4]);
			} else {
				tableValues.put("sysName", "");
			}
			if (ipArray[0][5] != null) {
				tableValues.put("sysLocation", ipArray[0][5]);
			} else {
				tableValues.put("sysLocation", "");
			}
			if (ipArray[0][6] != null) {
				tableValues.put("sysService", ipArray[0][6]);
			} else {
				tableValues.put("sysService", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			tableValues = null;
		}
		return tableValues;
	}

	/**
	 * 得到系统名字
	 */
	public String getSysName(String address, String community) {
		String sysname = null;
		try {
			sysname = snmp.getMibValue(address, community, "1.3.6.1.2.1.1.5.0");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sysname;
	}

	/**
	 * 得到system oid
	 */
	public String getSysOid(String address, int snmpversion, String community, int securityLevel, String securityName, int v3_ap, String authPassPhrase, int v3_privacy, String privacyPassPhrase) {
		return snmp.getMibValue(address, snmpversion, community, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase, "1.3.6.1.2.1.1.2.0");
	}

	/**
	 * 得到system oid
	 */
	public String getSysOid(String address, String community) {
		return snmp.getMibValue(address, community, "1.3.6.1.2.1.1.2.0");
	}

	/**
	 * 得到系统描述
	 */
	public int getSysServices(String address, String community) {
		int result = 0;
		String temp = snmp.getMibValue(address, community, "1.3.6.1.2.1.1.7.0");
		if (temp != null) {
			result = Integer.parseInt(temp);
		}
		return result;
	}

	public List<String> getVlanCommunities(String ip, String communtiy) {
		String[] vlanOids = new String[] { "1.3.6.1.4.1.9.9.68.1.2.2.1" };
		List<String> vlanCommunities = new ArrayList<String>();
		try {
			String[][] vlan = snmp.getTableData(ip, communtiy, vlanOids);
			for (int i = 0; i < vlan.length; i++) {
				String temp = communtiy + "@" + vlan[i][0];
				if (!vlanCommunities.contains(temp)) {
					vlanCommunities.add(temp);
				}
			}
		} catch (Exception e) {
			SysLogger.error("getLinks()", e);
		}
		return vlanCommunities;
	}

	@SuppressWarnings("unused")
	private Hashtable<String, String[]> getVLanIPAndReadCommunity(String address, String community) {
		/*
		 * String[]中数组依次为IP地址，端口
		 * .iso.org.dod.internet.mgmt.mib-2.entityMIB.entityMIBObjects.entityLogical.entLogicalTable.entLogicalEntry.entLogicalTAddress
		 * .1.3.6.1.2.1.47.1.2.1.1.5 snmp读团体
		 * .iso.org.dod.internet.mgmt.mib-2.entityMIB.entityMIBObjects.entityLogical.entLogicalTable.entLogicalEntry.entLogicalCommunity
		 * .1.3.6.1.2.1.47.1.2.1.1.4
		 */
		Hashtable<String, String[]> m_VlanIPAndCommunity = new Hashtable<String, String[]>();
		try {

			String[] columnoids = { ".1.3.6.1.2.1.47.1.2.1.1.5", ".1.3.6.1.2.1.47.1.2.1.1.4" };
			String[][] result = null;
			result = snmp.getTableData(address, community, columnoids);

			for (int i = 0; i < result.length; ++i) {
				String[] tmp = CommonUtil.IPPort2String(result[i][0], 4);
				String[] ip_port_comm = new String[3];
				ip_port_comm[0] = tmp[0];
				ip_port_comm[1] = tmp[1];
				ip_port_comm[2] = result[i][1];

				if (null == m_VlanIPAndCommunity.put(ip_port_comm[0] + ip_port_comm[1] + ip_port_comm[2], ip_port_comm)) {

					System.out.println(address + " getVLanIPAndReadCommunity:" + result[i][0] + " " + result[i][1] + " to " + ip_port_comm[0] + " " + ip_port_comm[1] + " " + ip_port_comm[2]);

				} else {

					System.out.println(address + " already getVLanIPAndReadCommunity:" + result[i][0] + " " + result[i][1] + " to " + ip_port_comm[0] + " " + ip_port_comm[1] + " " + ip_port_comm[2]);

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// snmputil.destory();
		}

		return m_VlanIPAndCommunity;

	}

	public boolean macInFdbTable(String mac, String address, String community) {
		String[] oids = new String[] { "1.3.6.1.2.1.17.4.3.1.1", // 1.mac
				"1.3.6.1.2.1.17.4.3.1.2", // 2.port
				"1.3.6.1.2.1.17.4.3.1.3" }; // 3.type

		boolean result = false;
		try {
			String[][] ipArray = snmp.getTableData(address, community, oids);
			for (int i = 0; i < ipArray.length; i++) {
				if (mac.equals(ipArray[i][0])) {
					result = true;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 得到Huawei的NDP信息
	 */
	public boolean setSysGroup(String address, String community, int version, Hashtable mibvalues) {
		try {
			String[] oids = new String[] { "1.3.6.1.2.1.1.4.0", // 4.sysContact
					"1.3.6.1.2.1.1.5.0", // 5.sysName
					"1.3.6.1.2.1.1.6.0" }; // 6.sysLocation
			String[] _mibvalue = new String[3];
			_mibvalue[0] = (String) mibvalues.get("sysContact");
			_mibvalue[1] = (String) mibvalues.get("sysName");
			_mibvalue[2] = (String) mibvalues.get("sysLocation");

			snmp.setMibValues(address, community, version, oids, _mibvalue);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
