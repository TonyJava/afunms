package com.afunms.polling.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.Portconfig;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.loader.HostLoader;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.AllUtilHdx;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.polling.om.InPkts;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.IpMac;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.OutPkts;
import com.afunms.polling.om.ProcessCollectEntity;
import com.afunms.polling.om.UtilHdx;
import com.afunms.polling.om.UtilHdxPerc;
import com.afunms.polling.snmp.Hostlastcollectdata;
import com.afunms.polling.snmp.SnmpMibConstants;
import com.afunms.temp.dao.DiskTempDao;
import com.afunms.temp.dao.InterfaceTempDao;
import com.afunms.temp.dao.MemoryTempDao;
import com.afunms.temp.dao.ProcessTempDao;
import com.afunms.temp.model.NodeTemp;
import com.afunms.topology.dao.HostLastCollectDataDao;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class HostLastCollectDataManager implements I_HostLastCollectData {
	private Host host;
	private boolean isCpuTime = false;

	public HostLastCollectDataManager() {
		super();
	}

	public boolean createHostData(Hostlastcollectdata hostdata) throws Exception {
		return true;
	}

	public synchronized boolean createHostData(String ip, Vector hostdatavec) throws Exception {
		return true;
	}

	public boolean createHostData(Vector hostdatavec) throws Exception {
		return true;
	}

	public boolean deleteHostData(String deletetime) throws Exception {
		return false;
	}

	private String dofloat(String num) {
		String snum = "0.0";
		if (num != null) {
			int inum = (int) (Float.parseFloat(num) * 100);
			snum = Double.toString(inum / 100.0);
		}
		return snum;
	}

	public Hashtable getAllParam() throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getAllParam_share() throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public List getAllPingdata(String thevalue) throws Exception {
		List list = new ArrayList();
		return list;
	}

	public Vector getAllUtilHdxInterface(String ip, String starttime, String endtime) throws Exception {
		Vector allutilhdxVector = new Vector();
		try {
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByIpaddress(ip);
				} catch (Exception e) {
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			}
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);

			// 从数据库查出所有相关的接口数据
			List allinterfacelist = new ArrayList();

			InterfaceTempDao interfacedao = new InterfaceTempDao();
			try {
				allinterfacelist = interfacedao.getNodeTempList(host.getId() + "", nodedto.getType(), nodedto.getSubtype(), null);
				if (allinterfacelist != null && allinterfacelist.size() > 0) {
					AllUtilHdx allutilhdx = new AllUtilHdx();
					for (int i = 0; i < allinterfacelist.size(); i++) {
						NodeTemp vo = (NodeTemp) allinterfacelist.get(i);
						if ("AllInBandwidthUtilHdx".equals(vo.getSindex()) || "AllOutBandwidthUtilHdx".equals(vo.getSindex())) {
							allutilhdx = new AllUtilHdx();
							allutilhdx.setIpaddress(host.getIpAddress());
							allutilhdx.setCollecttime(null);
							allutilhdx.setCategory("Interface");
							allutilhdx.setEntity(vo.getSubentity());
							allutilhdx.setSubentity(vo.getSindex());
							allutilhdx.setRestype("dynamic");
							allutilhdx.setUnit(vo.getUnit());
							if ("AllInBandwidthUtilHdx".equals(vo.getSubentity())) {
								allutilhdx.setChname("入口");
							} else {
								allutilhdx.setChname("出口");
							}
							allutilhdx.setThevalue(vo.getThevalue());
							allutilhdxVector.addElement(allutilhdx);
						}

					}
				}
			} catch (Exception e) {

			} finally {
				interfacedao.close();
			}
		} catch (Exception e) {

		}
		return allutilhdxVector;
	}

	public Hashtable getBand(String ip, String index, String starttime, String endtime) throws Exception {// 端口当前
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getBand_share(String ip, String index, String starttime, String endtime) throws Exception {// 端口当前
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getbyCategories(String ip, String[] category, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		DBManager dbmanager = new DBManager();
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			sb.append("select * from Hostlastcollectdata h where h.ipaddress='");
			sb.append(ip);
			sb.append("' and (");
			for (int i = 0; i < category.length; i++) {
				if (i != 0) {
					sb.append(" or ");
				}
				sb.append("h.category='" + category[i] + "'");
			}
			sb.append(") ");
			if (!starttime.equals("") && !starttime.equals("")) {
				sb.append(" and h.collecttime>='" + starttime + "' and h.collecttime <='" + endtime + "' ");
			}
			sql = sb.toString();
			Vector vector = new Vector();

			HostLastCollectDataDao lastdao = new HostLastCollectDataDao();
			List list = lastdao.findByCriteria(sql);
			if (list == null) {
				list = new ArrayList();
			}
			if (list.size() == 0) {
				lastdao.close();
				lastdao = new HostLastCollectDataDao();
				sql = "select * from Hostlastcollectdata h where h.ipaddress='" + ip + "' and h.category='System'";
				list = lastdao.findByCriteria(sql);
				hash.put("flag", new Integer(0));
			} else {
				hash.put("flag", new Integer(1));
			}

			for (int i = 0; i < list.size(); i++) {
				Hostlastcollectdata obj = (Hostlastcollectdata) list.get(i);
				if (obj == null) {
					continue;
				}
				String c = obj.getCategory();
				if (obj.getThevalue() == null || obj.getThevalue().equals("")) {
					continue;
				} else {
					String value = obj.getThevalue();
					String unit = obj.getUnit();
					if (c.equalsIgnoreCase("user")) {
						vector.add(value);
						continue;
					}
					if (c.equalsIgnoreCase("cpu")) {
						hash.put("cpu", dofloat(value) + unit);
						continue;
					}
					if (c.equalsIgnoreCase("ping")) {
						String subentity = obj.getSubentity();
						if (subentity.equalsIgnoreCase("responsetime")) {
							hash.put("response", dofloat(value) + unit);
							continue;
						}
						if (subentity.equalsIgnoreCase("ConnectUtilization")) {
							hash.put("ConnectUtilization", dofloat(value) + unit);
							hash.put("time", obj.getCollecttime());
							continue;
						}
					} else if (c.equalsIgnoreCase("system")) {
						hash.put(obj.getSubentity(), value);
						continue;
					}
				}
			}
			hash.put("user", vector);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			dbmanager.close();
		}

		return hash;
	}

	public Hashtable getbyCategories_share(String ip, String[] category, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hostlastcollectdata getByHostdataid(Integer hostdataid) throws Exception {
		return null;
	}

	public List getByIpaddress(String ipaddress) throws Exception {
		return null;
	}

	public List getByIpaddress(String ipaddress, String time) throws Exception {
		return null;
	}

	public Hostlastcollectdata getByipandsubentity(String ipaddress, String index) throws Exception {
		return null;
	}

	public List getByIpCategory(String ipaddress, String Category, String time) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List getByIpCategoryEntity(String ipaddress, String Category, String entity, String time) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List getBySearch(String searchfield, String searchkeyword) throws Exception {
		return null;
	}

	public List getBysubentity(String thevalue) throws Exception {
		return null;
	}

	public Hashtable getCategory(String ip, String category, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	// disk没有时间限制
	public Hashtable getDisk(String ip, String category, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		try {
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByIpaddress(ip);
				} catch (Exception e) {
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			}
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);

			DiskTempDao diskdao = new DiskTempDao();
			List alldisklist = new ArrayList();
			try {
				alldisklist = diskdao.getNodeTemp(host.getId() + "", "host", nodedto.getSubtype());
			} catch (Exception e) {

			} finally {
				diskdao.close();
			}
			Vector diskVector = new Vector();
			if (alldisklist != null && alldisklist.size() > 0) {
				DiskCollectEntity diskdata = null;
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (int i = 0; i < alldisklist.size(); i++) {
					NodeTemp nodetemp = (NodeTemp) alldisklist.get(i);
					diskdata = new DiskCollectEntity();
					diskdata.setIpaddress(host.getIpAddress());
					Date d = sdf1.parse(nodetemp.getCollecttime());
					Calendar c = Calendar.getInstance();
					c.setTime(d);
					diskdata.setCollecttime(c);
					diskdata.setCategory(nodetemp.getEntity());
					diskdata.setEntity(nodetemp.getSubentity());
					diskdata.setSubentity(nodetemp.getSindex());
					diskdata.setRestype(nodetemp.getRestype());
					diskdata.setUnit(nodetemp.getUnit());
					diskdata.setThevalue(nodetemp.getThevalue());
					diskVector.addElement(diskdata);
				}
			}

			List list1 = new ArrayList();
			List list2 = new ArrayList();
			Vector sdata = diskVector;
			if (category.toLowerCase().equalsIgnoreCase("disk")) {
				if (sdata != null && sdata.size() > 0) {
					for (int i = 0; i < sdata.size(); i++) {
						DiskCollectEntity hdata = (DiskCollectEntity) sdata.get(i);
						if (hdata.getCategory().equals(category)) {
							if (!list1.contains(hdata.getSubentity())) {
								list1.add(hdata.getSubentity());
							}
							list2.add(hdata);
						}
					}
				}

				if (list1.size() != 0) {
					String[] key = new String[list1.size()];
					Hashtable[] hashs = new Hashtable[list1.size()];
					for (int i = 0; i < list1.size(); i++) {
						key[i] = (String) (list1.get(i));
						hashs[i] = new Hashtable();
					}
					for (int j = 0; j < list2.size(); j++) {
						DiskCollectEntity h = (DiskCollectEntity) list2.get(j);
						for (int m = 0; m < list1.size(); m++) {
							if (h.getSubentity().equals(key[m])) {
								String s = "";
								if (h.getEntity().equalsIgnoreCase("Utilization")) {
									s = h.getThevalue();
									hashs[m].put(h.getEntity() + "value", s);
								}
								if (h.getEntity().equalsIgnoreCase("INodeUtilization")) {
									s = h.getThevalue();
									hashs[m].put(h.getEntity() + "value", s);
								}
								s = dofloat(h.getThevalue()) + h.getUnit();
								hashs[m].put(h.getEntity(), s);
								break;
							}
						}
					}
					for (int i = 0; i < list1.size(); i++) {
						hashs[i].put("name", key[i]);
						hash.put(new Integer(i), hashs[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	public Hashtable getDisk_share(String ip, String category, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		try {
			List list1 = new ArrayList();
			List list2 = new ArrayList();
			Hashtable sharedata = ShareData.getSharedata();
			Hashtable ipdata = (Hashtable) sharedata.get(ip);
			if (ipdata != null && ipdata.size() > 0) {
				Vector sdata = (Vector) ipdata.get(category.toLowerCase());
				if (category.toLowerCase().equalsIgnoreCase("disk")) {
					if (sdata != null && sdata.size() > 0) {
						for (int i = 0; i < sdata.size(); i++) {
							DiskCollectEntity hdata = (DiskCollectEntity) sdata.get(i);
							if (hdata.getCategory().equals(category)) {
								if (!list1.contains(hdata.getSubentity())) {
									list1.add(hdata.getSubentity());
								}
								list2.add(hdata);
							}
						}
					}

					if (list1.size() != 0) {
						String[] key = new String[list1.size()];
						Hashtable[] hashs = new Hashtable[list1.size()];
						for (int i = 0; i < list1.size(); i++) {
							key[i] = (String) (list1.get(i));
							hashs[i] = new Hashtable();
						}
						for (int j = 0; j < list2.size(); j++) {
							DiskCollectEntity h = (DiskCollectEntity) list2.get(j);
							for (int m = 0; m < list1.size(); m++) {
								if (h.getSubentity().equals(key[m])) {
									String s = "";
									if (h.getEntity().equalsIgnoreCase("Utilization")) {
										s = h.getThevalue();
										hashs[m].put(h.getEntity() + "value", s);
									}
									if (h.getEntity().equalsIgnoreCase("INodeUtilization")) {
										s = h.getThevalue();
										hashs[m].put(h.getEntity() + "value", s);
									}
									s = dofloat(h.getThevalue()) + h.getUnit();
									hashs[m].put(h.getEntity(), s);
									break;
								}
							}
						}
						for (int i = 0; i < list1.size(); i++) {
							hashs[i].put("name", key[i]);
							hash.put(new Integer(i), hashs[i]);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	/**
	 * @author hipo 作用：采集不到fdb数据的时候从数据库获取历史数据
	 */
	public Vector getFDB(String ip) throws Exception {
		Vector vector = new Vector();
		Hashtable ipmacHashtable;
		IpMac ipMac = new IpMac();
		String sql = "";
		if (ShareData.getSharedata().containsKey(ip)) {
			ipmacHashtable = (Hashtable) ShareData.getSharedata().get(ip);
			if (ipmacHashtable != null) {
				vector = (Vector) ipmacHashtable.get("fdb");
				if (vector != null && !((IpMac) vector.get(0)).getIfindex().equals("unknown")) {
					return vector;
				} else {
					vector = new Vector();
					DBManager dbm = new DBManager();
					Calendar calendar = new GregorianCalendar();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sql = "select * from nms_fdb_data_temp where ip='" + ip + "'";
					ResultSet rs = dbm.executeQuery(sql);
					while (rs.next()) {
						ipMac = new IpMac();
						Date date = sdf.parse(rs.getString("COLLECTTIME"));
						calendar.setTime(date);
						ipMac.setBak(rs.getString("BAK"));
						ipMac.setCollecttime(calendar);
						ipMac.setIfband(rs.getString("IFBAND"));
						ipMac.setIfindex(rs.getString("IFINDEX"));
						ipMac.setIfsms(rs.getString("IFSMS"));
						ipMac.setIpaddress(rs.getString("IPADDRESS"));
						ipMac.setMac(rs.getString("MAC"));
						ipMac.setRelateipaddr(rs.getString("ip"));

						vector.add(ipMac);
					}
				}
			} else {
				vector = new Vector();
				DBManager dbm = new DBManager();
				Calendar calendar = new GregorianCalendar();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sql = "select * from nms_fdb_data_temp where ip='" + ip + "'";
				ResultSet rs = dbm.executeQuery(sql);
				while (rs.next()) {
					ipMac = new IpMac();
					Date date = sdf.parse(rs.getString("COLLECTTIME"));
					calendar.setTime(date);
					ipMac.setBak(rs.getString("BAK"));
					ipMac.setCollecttime(calendar);
					ipMac.setIfband(rs.getString("IFBAND"));
					ipMac.setIfindex(rs.getString("IFINDEX"));
					ipMac.setIfsms(rs.getString("IFSMS"));
					ipMac.setIpaddress(rs.getString("IPADDRESS"));
					ipMac.setMac(rs.getString("MAC"));
					ipMac.setRelateipaddr(rs.getString("RELATEIPADDR"));

					vector.add(ipMac);
				}
			}

		}
		return vector;
	}

	public List getHostcollectdata() throws Exception {
		return null;
	}

	public List getHostcollectdata(String time) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	// 网络设备的某端口详情
	public Hashtable getIfdetail(String ip, String index, String[] key, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		try {
			String sql = "";
			StringBuffer sb = new StringBuffer();
			sb.append(" and(");
			for (int i = 0; i < key.length; i++) {
				if (i != 0) {
					sb.append("or");
				}
				sb.append(" h.entity='");
				sb.append(key[i]);
				sb.append("' ");
			}
			sb.append(")");

			String timelimit = " and h.collecttime >= to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and h.collecttime <= to_date('" + endtime + "','YYYY-MM-DD HH24:MI:SS') ";
			List list = new ArrayList();
			HostLastCollectDataDao dao = new HostLastCollectDataDao();
			sql = "select * from interfacedata" + ip.replace(".", "_") + " h where h.ipaddress='" + ip + "'" + " and h.subentity='" + index + "'" + sb.toString() + timelimit;
			System.out.println(sql);
			list = dao.findByCriteria(sql);
			if (list.size() != 0) {
				for (int j = 0; j < list.size(); j++) {
					Hostlastcollectdata h = (Hostlastcollectdata) list.get(j);
					String value = h.getThevalue();
					if (h.getEntity().equalsIgnoreCase("InBandwidthUtilHdxPerc") || h.getEntity().equalsIgnoreCase("OutBandwidthUtilHdxPerc")) {
						value = dofloat(value);
					}
					if (h.getUnit() != null) {
						hash.put(h.getEntity(), value + h.getUnit());
					} else {
						hash.put(h.getEntity(), value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}

	public Hashtable getIfdetail_share(String ip, String index, String[] key, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		try {
			List list = new ArrayList();
			String runmodel = PollingEngine.getCollectwebflag();// 采集与访问模式
			Hashtable ipdata = null;
			ResultSet rs = null;
			Vector interfaceVector = null;
			Interfacecollectdata interfacecollectdata = null;
			DBManager dao = new DBManager();
			if ("0".equals(runmodel)) {
				// 采集与访问是集成模式
				ipdata = (Hashtable) ShareData.getSharedata().get(ip);
			} else {
				// 采集与访问是分离模式
				try {
					interfaceVector = new Vector();
					rs = dao.executeQuery("select distinct * from interfacedata" + ip.replace(".", "_"));
					if (rs != null) {
						while (rs.next()) {
							String sindex = rs.getString("sindex");
							String subentity = rs.getString("subentity");
							String thevalue = rs.getString("thevalue");
							String chname = rs.getString("chname");
							String restype = rs.getString("restype");
							String unit = rs.getString("unit");
							String category = rs.getString("entity");
							if (unit == null) {
								unit = "";
							}
							interfacecollectdata = new Interfacecollectdata();
							interfacecollectdata.setSubentity(sindex);
							interfacecollectdata.setChname(chname);
							interfacecollectdata.setRestype(restype);
							interfacecollectdata.setThevalue(thevalue);
							interfacecollectdata.setUnit(unit);
							interfacecollectdata.setCategory(category);
							interfacecollectdata.setEntity(subentity);
							interfaceVector.add(interfacecollectdata);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
				}
				ipdata = new Hashtable();
				ipdata.put("interface", interfaceVector);

			}
			if (ipdata != null && ipdata.size() > 0) {
				Vector sdata = (Vector) ipdata.get("interface");
				if (sdata != null && sdata.size() > 0) {
					if (sdata != null && sdata.size() > 0) {
						for (int i = 0; i < sdata.size(); i++) {
							Interfacecollectdata hdata = (Interfacecollectdata) sdata.get(i);
							if (hdata.getSubentity() != null && hdata.getSubentity().equals(index)) {
								for (int k = 0; k < key.length; k++) {
									if (hdata.getEntity() != null && hdata.getEntity().equals(key[k])) {
										list.add(hdata);
									}
								}
							}
						}
					}

					if (list.size() != 0) {
						for (int j = 0; j < list.size(); j++) {
							Interfacecollectdata h = (Interfacecollectdata) list.get(j);
							String value = h.getThevalue();
							if (h.getEntity().equalsIgnoreCase("InBandwidthUtilHdxPerc") || h.getEntity().equalsIgnoreCase("OutBandwidthUtilHdxPerc")) {
								value = dofloat(value);
							}
							hash.put(h.getEntity(), value + h.getUnit());
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	public Hashtable getIfOctets(String ip) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getIfOctets_share(String ip) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getIfStatus(String ip, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Vector getInterface(String ip, String[] InterfaceItem, String orderflag, String starttime, String endtime) throws Exception {
		Vector vector = new Vector();
		try {
			List list = new ArrayList();

			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByIpaddress(ip);
				} catch (Exception e) {
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			}
			Hashtable t = ShareData.getAllNetworkData();
			Hashtable ipAllData = (Hashtable) ShareData.getAllNetworkData().get(ip);
			System.out.println(ip+"data="+ipAllData);
			// 从数据库查出所有相关的接口数据
			Vector interfacevector = (Vector) ipAllData.get("interfacevector");
			Vector utilhdxVector = (Vector) ipAllData.get("utilhdx");
			Vector inpksVector = (Vector) ipAllData.get("inpacks");
			Vector outpksVector = (Vector) ipAllData.get("outpacks");
			Vector utilhdxpercVector = (Vector) ipAllData.get("utilhdxperc");

			// 全部
			Hashtable hash2 = new Hashtable();
			Vector subentity = new Vector();

			if (interfacevector != null && interfacevector.size() > 0) {
				for (int i = 0; i < interfacevector.size(); i++) {
					Interfacecollectdata interfacedata = (Interfacecollectdata) interfacevector.elementAt(i);
					Hostlastcollectdata lastdata = new Hostlastcollectdata();
					lastdata.setIpaddress(interfacedata.getIpaddress());
					lastdata.setCategory(interfacedata.getCategory());
					lastdata.setEntity(interfacedata.getEntity());
					lastdata.setSubentity(interfacedata.getSubentity());
					lastdata.setThevalue(interfacedata.getThevalue());
					lastdata.setCollecttime(interfacedata.getCollecttime());
					lastdata.setRestype(interfacedata.getRestype());
					lastdata.setUnit(interfacedata.getUnit());
					lastdata.setChname(interfacedata.getChname());
					lastdata.setBak(interfacedata.getBak());
					subentity.add(lastdata);
				}
			}

			if (utilhdxVector != null && utilhdxVector.size() > 0) {
				for (int i = 0; i < utilhdxVector.size(); i++) {
					UtilHdx utilhdx = (UtilHdx) utilhdxVector.elementAt(i);
					Hostlastcollectdata lastdata = new Hostlastcollectdata();
					lastdata.setIpaddress(utilhdx.getIpaddress());
					lastdata.setCategory(utilhdx.getCategory());
					lastdata.setEntity(utilhdx.getEntity());
					lastdata.setSubentity(utilhdx.getSubentity());
					lastdata.setThevalue(utilhdx.getThevalue());
					lastdata.setCollecttime(utilhdx.getCollecttime());
					lastdata.setRestype(utilhdx.getRestype());
					lastdata.setUnit(utilhdx.getUnit());
					lastdata.setChname(utilhdx.getChname());
					lastdata.setBak(utilhdx.getBak());
					subentity.add(lastdata);
				}
			}

			if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
				for (int i = 0; i < utilhdxpercVector.size(); i++) {
					UtilHdxPerc utilhdx = (UtilHdxPerc) utilhdxpercVector.elementAt(i);
					Hostlastcollectdata lastdata = new Hostlastcollectdata();
					lastdata.setIpaddress(utilhdx.getIpaddress());
					lastdata.setCategory(utilhdx.getCategory());
					lastdata.setEntity(utilhdx.getEntity());
					lastdata.setSubentity(utilhdx.getSubentity());
					lastdata.setThevalue(utilhdx.getThevalue());
					lastdata.setCollecttime(utilhdx.getCollecttime());
					lastdata.setRestype(utilhdx.getRestype());
					lastdata.setUnit(utilhdx.getUnit());
					lastdata.setChname(utilhdx.getChname());
					lastdata.setBak(utilhdx.getBak());
					subentity.add(lastdata);
				}
			}

			if (inpksVector != null && inpksVector.size() > 0) {
				for (int i = 0; i < inpksVector.size(); i++) {
					InPkts inpacks = (InPkts) inpksVector.elementAt(i);
					Hostlastcollectdata lastdata = new Hostlastcollectdata();
					lastdata.setIpaddress(inpacks.getIpaddress());
					lastdata.setCategory(inpacks.getCategory());
					lastdata.setEntity(inpacks.getEntity());
					lastdata.setSubentity(inpacks.getSubentity());
					lastdata.setThevalue(inpacks.getThevalue());
					lastdata.setCollecttime(inpacks.getCollecttime());
					lastdata.setRestype(inpacks.getRestype());
					lastdata.setUnit(inpacks.getUnit());
					lastdata.setChname(inpacks.getChname());
					lastdata.setBak(inpacks.getBak());
					subentity.add(lastdata);
				}
			}

			if (outpksVector != null && outpksVector.size() > 0) {
				for (int i = 0; i < outpksVector.size(); i++) {
					OutPkts outpacks = (OutPkts) outpksVector.elementAt(i);
					Hostlastcollectdata lastdata = new Hostlastcollectdata();
					lastdata.setIpaddress(outpacks.getIpaddress());
					lastdata.setCategory(outpacks.getCategory());
					lastdata.setEntity(outpacks.getEntity());
					lastdata.setSubentity(outpacks.getSubentity());
					lastdata.setThevalue(outpacks.getThevalue());
					lastdata.setCollecttime(outpacks.getCollecttime());
					lastdata.setRestype(outpacks.getRestype());
					lastdata.setUnit(outpacks.getUnit());
					lastdata.setChname(outpacks.getChname());
					lastdata.setBak(outpacks.getBak());
					subentity.add(lastdata);
				}
			}
			if (subentity != null && subentity.size() > 0) {
				for (int i = 0; i < subentity.size(); i++) {
					Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
					if (hdata.getCategory().equals("Interface") && hdata.getEntity().equals("index")) {
						list.add(hdata.getSubentity());
					}
				}
			}
			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					hash2.put(list.get(i).toString(), "");
				}

				// 有出口流速的端口
				list = null;
				list = new ArrayList();
				List orderList = new ArrayList();
				if (subentity != null && subentity.size() > 0) {
					for (int i = 0; i < subentity.size(); i++) {
						Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
						if (hdata.getEntity().equals(orderflag)) {
							if (list.contains(hdata.getSubentity())) {
								continue;
							}
							list.add(hdata.getSubentity());
							orderList.add(hdata);
						}
					}
				}
				// 对orderList根据theValue进行排序
				list = null;
				list = new ArrayList();
				if (orderList != null && orderList.size() > 0) {
					for (int m = 0; m < orderList.size(); m++) {
						Hostlastcollectdata hdata = (Hostlastcollectdata) orderList.get(m);
						for (int n = m + 1; n < orderList.size(); n++) {
							Hostlastcollectdata hosdata = (Hostlastcollectdata) orderList.get(n);
							if (orderflag.equalsIgnoreCase("index")) {
								if (new Double(hdata.getThevalue()).doubleValue() > new Double(hosdata.getThevalue()).doubleValue()) {
									orderList.remove(m);
									orderList.add(m, hosdata);
									orderList.remove(n);
									orderList.add(n, hdata);
									hdata = hosdata;
									hosdata = null;
								}
							} else {
								if (new Double(hdata.getThevalue()).doubleValue() < new Double(hosdata.getThevalue()).doubleValue()) {
									orderList.remove(m);
									orderList.add(m, hosdata);
									orderList.remove(n);
									orderList.add(n, hdata);
									hdata = hosdata;
									hosdata = null;
								}
							}
						}
						// 得到排序后的Subentity的列表
						list.add(hdata.getSubentity());
						hdata = null;
					}
				}

				Vector order = new Vector();
				for (int i = 0; i < list.size(); i++) {
					hash2.remove(list.get(i).toString());
					if (order.contains(list.get(i).toString())) {
						continue;
					}
					order.add(list.get(i).toString());
				}
				Set key = hash2.keySet();
				Iterator it = key.iterator();
				while (it.hasNext()) {
					String ss = (String) it.next();
					order.add(ss);
				}
				List list2 = new ArrayList();
				Hashtable hash = new Hashtable();
				if (subentity != null && subentity.size() > 0) {
					for (int i = 0; i < subentity.size(); i++) {
						Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
						if (hdata.getCategory().equals("Interface")) {
							for (int k = 0; k < InterfaceItem.length; k++) {
								if (hdata.getEntity() != null && hdata.getEntity().equals(InterfaceItem[k])) {
									list2.add(hdata);
									break;
								}
							}
						}
					}
				}
				if (list2.size() != 0) {
					for (int i = 0; i < list2.size(); i++) {
						Hostlastcollectdata obj = (Hostlastcollectdata) (list2.get(i));
						String value = obj.getThevalue();
						if (obj.getEntity().equalsIgnoreCase("OutBandwidthUtilHdxPerc") || obj.getEntity().equalsIgnoreCase("InBandwidthUtilHdxPerc")) {
							value = dofloat(value);
							hash.put(obj.getEntity() + obj.getSubentity(), value + obj.getUnit());
						} else if (obj.getEntity().equalsIgnoreCase("ifSpeed")) {
							hash.put(obj.getEntity() + obj.getSubentity(), value + obj.getUnit());
						} else {
							hash.put(obj.getEntity() + obj.getSubentity(), value);
						}
					}
				}
				for (int j = 0; j < order.size(); j++) {
					String[] strs = new String[InterfaceItem.length];
					for (int i = 0; i < strs.length; i++) {
						strs[i] = "";

						if (i == 2 || i == 8 || i == 9) {
							strs[i] = "0kb/s";
						}
						if (i == 4 || i == 5 || i == 6 || i == 7) {
							strs[i] = "0";
						}
					}
					String index = order.get(j).toString();
					for (int i = 0; i < InterfaceItem.length; i++) {
						if (hash.get(InterfaceItem[i] + index) != null) {
							String value = hash.get(InterfaceItem[i] + index).toString();
							strs[i] = value;
						}
					}
					vector.add(strs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}

	public Vector getInterface_share(String ip, String[] InterfaceItem, String orderflag, String starttime, String endtime) throws Exception {
		Vector vector = new Vector();

		try {
			List list = new ArrayList();
			// 全部
			Hashtable hash2 = new Hashtable();
			Hashtable sharedata = ShareData.getSharedata();
			Hashtable ipdata = (Hashtable) sharedata.get(ip);
			Vector subentity = new Vector();
			if (ipdata != null && ipdata.size() > 0) {
				Vector tempv = (Vector) ipdata.get("interface");
				if (tempv != null && tempv.size() > 0) {
					for (int i = 0; i < tempv.size(); i++) {
						Interfacecollectdata interfacedata = (Interfacecollectdata) tempv.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(interfacedata.getIpaddress());
						lastdata.setCategory(interfacedata.getCategory());
						lastdata.setEntity(interfacedata.getEntity());
						lastdata.setSubentity(interfacedata.getSubentity());
						lastdata.setThevalue(interfacedata.getThevalue());
						lastdata.setCollecttime(interfacedata.getCollecttime());
						lastdata.setRestype(interfacedata.getRestype());
						lastdata.setUnit(interfacedata.getUnit());
						lastdata.setChname(interfacedata.getChname());
						lastdata.setBak(interfacedata.getBak());
						subentity.add(lastdata);
					}
				}

				tempv = (Vector) ipdata.get("utilhdx");
				if (tempv != null && tempv.size() > 0) {
					for (int i = 0; i < tempv.size(); i++) {
						UtilHdx utilhdx = (UtilHdx) tempv.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(utilhdx.getIpaddress());
						lastdata.setCategory(utilhdx.getCategory());
						lastdata.setEntity(utilhdx.getEntity());
						lastdata.setSubentity(utilhdx.getSubentity());
						lastdata.setThevalue(utilhdx.getThevalue());
						lastdata.setCollecttime(utilhdx.getCollecttime());
						lastdata.setRestype(utilhdx.getRestype());
						lastdata.setUnit(utilhdx.getUnit());
						lastdata.setChname(utilhdx.getChname());
						lastdata.setBak(utilhdx.getBak());
						subentity.add(lastdata);
					}
				}

				tempv = (Vector) ipdata.get("utilhdxperc");
				if (tempv != null && tempv.size() > 0) {
					for (int i = 0; i < tempv.size(); i++) {
						UtilHdxPerc utilhdx = (UtilHdxPerc) tempv.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(utilhdx.getIpaddress());
						lastdata.setCategory(utilhdx.getCategory());
						lastdata.setEntity(utilhdx.getEntity());
						lastdata.setSubentity(utilhdx.getSubentity());
						lastdata.setThevalue(utilhdx.getThevalue());
						lastdata.setCollecttime(utilhdx.getCollecttime());
						lastdata.setRestype(utilhdx.getRestype());
						lastdata.setUnit(utilhdx.getUnit());
						lastdata.setChname(utilhdx.getChname());
						lastdata.setBak(utilhdx.getBak());
						subentity.add(lastdata);
					}
				}

				tempv = (Vector) ipdata.get("inpacks");
				if (tempv != null && tempv.size() > 0) {
					for (int i = 0; i < tempv.size(); i++) {
						InPkts inpacks = (InPkts) tempv.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(inpacks.getIpaddress());
						lastdata.setCategory(inpacks.getCategory());
						lastdata.setEntity(inpacks.getEntity());
						lastdata.setSubentity(inpacks.getSubentity());
						lastdata.setThevalue(inpacks.getThevalue());
						lastdata.setCollecttime(inpacks.getCollecttime());
						lastdata.setRestype(inpacks.getRestype());
						lastdata.setUnit(inpacks.getUnit());
						lastdata.setChname(inpacks.getChname());
						lastdata.setBak(inpacks.getBak());
						subentity.add(lastdata);
					}
				}

				tempv = (Vector) ipdata.get("outpacks");
				if (tempv != null && tempv.size() > 0) {
					for (int i = 0; i < tempv.size(); i++) {
						OutPkts outpacks = (OutPkts) tempv.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(outpacks.getIpaddress());
						lastdata.setCategory(outpacks.getCategory());
						lastdata.setEntity(outpacks.getEntity());
						lastdata.setSubentity(outpacks.getSubentity());
						lastdata.setThevalue(outpacks.getThevalue());
						lastdata.setCollecttime(outpacks.getCollecttime());
						lastdata.setRestype(outpacks.getRestype());
						lastdata.setUnit(outpacks.getUnit());
						lastdata.setChname(outpacks.getChname());
						lastdata.setBak(outpacks.getBak());
						subentity.add(lastdata);
					}
				}

			} else {
				// 设备连接不上,获取端口配置表里的数据
				List portlist = new ArrayList();
				if (ShareData.getAllportconfigsbyIP() != null) {
					if (ShareData.getAllportconfigsbyIP().containsKey(ip)) {
						portlist = (List) (ShareData.getAllportconfigsbyIP().get(ip));
					}
				}
				Calendar date = Calendar.getInstance();
				final String[] desc = SnmpMibConstants.NetWorkMibInterfaceDesc0;
				final String[] unit = SnmpMibConstants.NetWorkMibInterfaceUnit0;
				final String[] chname = SnmpMibConstants.NetWorkMibInterfaceChname0;
				final int[] scale = SnmpMibConstants.NetWorkMibInterfaceScale0;
				if (portlist != null && portlist.size() > 0) {
					for (int i = 0; i < portlist.size(); i++) {
						// 端口配置
						Portconfig portconfig = (Portconfig) portlist.get(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();

						for (int j = 0; j < 10; j++) {
							// 把预期状态和ifLastChange过滤掉
							lastdata = new Hostlastcollectdata();
							if (j == 8) {
								continue;
							}
							String[] valueArray = { portconfig.getPortindex() + "", portconfig.getName() + "", "0", "0", portconfig.getSpeed(), "0", "0", "0", "0", "0", "0", };
							String sValue = valueArray[j];
							lastdata.setIpaddress(ip);
							lastdata.setCollecttime(date);
							lastdata.setCategory("Interface");
							lastdata.setEntity(desc[j]);
							lastdata.setSubentity(portconfig.getPortindex() + "");
							// 端口状态不保存，只作为静态数据放到临时表里
							if (j == 7) {
								lastdata.setRestype("static");
							} else {
								lastdata.setRestype("static");
							}
							lastdata.setUnit(unit[j]);

							if ((j == 4) && sValue != null) {// 流速
							}
							if ((j == 6 || j == 7) && sValue != null) {// 预期状态和当前状态
								lastdata.setThevalue("down");
							} else if ((j == 2) && sValue != null) {// 断口类型
								// 需要加上端口类型
								lastdata.setThevalue("");
							} else {
								if (j == 1) {
									lastdata.setThevalue(sValue);
								} else {
									if (scale[j] == 0) {
										lastdata.setThevalue(sValue);
									} else {
										lastdata.setThevalue(Long.toString(Long.parseLong(sValue) / scale[j]));
									}
								}

							}
							lastdata.setChname(chname[j]);
							subentity.add(lastdata);
						} // end for j

						// 流速
						lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(ip);
						lastdata.setCategory("Interface");
						lastdata.setEntity("InBandwidthUtilHdx");
						lastdata.setSubentity(portconfig.getPortindex() + "");
						lastdata.setThevalue("0");
						lastdata.setCollecttime(date);
						lastdata.setRestype("");
						lastdata.setUnit("kb/s");
						lastdata.setChname(portconfig.getPortindex() + "端口入口流速");
						lastdata.setBak("dynamic");
						subentity.add(lastdata);

						lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(ip);
						lastdata.setCategory("Interface");
						lastdata.setEntity("OutBandwidthUtilHdx");
						lastdata.setSubentity(portconfig.getPortindex() + "");
						lastdata.setThevalue("0");
						lastdata.setCollecttime(date);
						lastdata.setRestype("");
						lastdata.setUnit("kb/s");
						lastdata.setChname(portconfig.getPortindex() + "端口出口流速");
						lastdata.setBak("dynamic");
						subentity.add(lastdata);

						// 带宽
						lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(ip);
						lastdata.setCategory("Interface");
						lastdata.setEntity("InBandwidthUtilHdxPerc");
						lastdata.setSubentity(portconfig.getPortindex() + "");
						lastdata.setThevalue("0");
						lastdata.setCollecttime(date);
						lastdata.setRestype("dynamic");
						lastdata.setUnit("%");
						lastdata.setChname(portconfig.getPortindex() + "端口入口带宽利用率");
						lastdata.setBak("");
						subentity.add(lastdata);

						lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(ip);
						lastdata.setCategory("Interface");
						lastdata.setEntity("OutBandwidthUtilHdxPerc");
						lastdata.setSubentity(portconfig.getPortindex() + "");
						lastdata.setThevalue("0");
						lastdata.setCollecttime(date);
						lastdata.setRestype("dynamic");
						lastdata.setUnit("%");
						lastdata.setChname(portconfig.getPortindex() + "端口出口带宽利用率");
						lastdata.setBak("");
						subentity.add(lastdata);

						// 入口数据包
						lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(ip);
						lastdata.setCategory("Interface");
						lastdata.setEntity("ifInUcastPkts");
						lastdata.setSubentity(portconfig.getPortindex() + "");
						lastdata.setThevalue("");
						lastdata.setCollecttime(date);
						lastdata.setRestype("dynamic");
						lastdata.setUnit("个");
						lastdata.setChname("单向");
						lastdata.setBak("");
						subentity.add(lastdata);

						lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(ip);
						lastdata.setCategory("Interface");
						lastdata.setEntity("ifInNUcastPkts");
						lastdata.setSubentity(portconfig.getPortindex() + "");
						lastdata.setThevalue("");
						lastdata.setCollecttime(date);
						lastdata.setRestype("dynamic");
						lastdata.setUnit("个");
						lastdata.setChname("非单向");
						lastdata.setBak("");
						subentity.add(lastdata);

						// 出口数据包
						lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(ip);
						lastdata.setCategory("Interface");
						lastdata.setEntity("ifOutUcastPkts");
						lastdata.setSubentity(portconfig.getPortindex() + "");
						lastdata.setThevalue("");
						lastdata.setCollecttime(date);
						lastdata.setRestype("dynamic");
						lastdata.setUnit("个");
						lastdata.setChname("单向");
						lastdata.setBak("");
						subentity.add(lastdata);

						lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(ip);
						lastdata.setCategory("Interface");
						lastdata.setEntity("ifOutNUcastPkts");
						lastdata.setSubentity(portconfig.getPortindex() + "");
						lastdata.setThevalue("");
						lastdata.setCollecttime(date);
						lastdata.setRestype("dynamic");
						lastdata.setUnit("个");
						lastdata.setChname("非单向");
						lastdata.setBak("");
						subentity.add(lastdata);
					}
				}

			}

			if (subentity != null && subentity.size() > 0) {
				for (int i = 0; i < subentity.size(); i++) {
					Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
					if (hdata.getCategory().equals("Interface") && hdata.getEntity().equals("index")) {
						list.add(hdata.getSubentity());
					}
				}
			}

			if (list != null && list.size() != 0) {
				for (int i = 0; i < list.size(); i++) {
					hash2.put(list.get(i).toString(), "");
				}

				// 有出口流速的端口
				list = null;
				list = new ArrayList();
				List orderList = new ArrayList();
				if (subentity != null && subentity.size() > 0) {
					for (int i = 0; i < subentity.size(); i++) {
						Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
						if (hdata.getEntity().equals(orderflag)) {
							if (list.contains(hdata.getSubentity())) {
								continue;
							}
							list.add(hdata.getSubentity());
							orderList.add(hdata);
						}
					}
				}
				// 对orderList根据theValue进行排序
				list = null;
				list = new ArrayList();
				if (orderList != null && orderList.size() > 0) {
					for (int m = 0; m < orderList.size(); m++) {
						Hostlastcollectdata hdata = (Hostlastcollectdata) orderList.get(m);
						for (int n = m + 1; n < orderList.size(); n++) {
							Hostlastcollectdata hosdata = (Hostlastcollectdata) orderList.get(n);
							if (orderflag.equalsIgnoreCase("index")) {
								if (new Double(hdata.getThevalue()).doubleValue() > new Double(hosdata.getThevalue()).doubleValue()) {
									orderList.remove(m);
									orderList.add(m, hosdata);
									orderList.remove(n);
									orderList.add(n, hdata);
									hdata = hosdata;
									hosdata = null;
								}
							} else {
								if (new Double(hdata.getThevalue()).doubleValue() < new Double(hosdata.getThevalue()).doubleValue()) {
									orderList.remove(m);
									orderList.add(m, hosdata);
									orderList.remove(n);
									orderList.add(n, hdata);
									hdata = hosdata;
									hosdata = null;
								}
							}
						}
						// 得到排序后的Subentity的列表
						list.add(hdata.getSubentity());
						hdata = null;
					}
				}

				Vector order = new Vector();
				for (int i = 0; i < list.size(); i++) {
					hash2.remove(list.get(i).toString());
					if (order.contains(list.get(i).toString())) {
						continue;
					}
					order.add(list.get(i).toString());
				}
				Set key = hash2.keySet();
				Iterator it = key.iterator();
				while (it.hasNext()) {
					String ss = (String) it.next();
					order.add(ss);
				}
				List list2 = new ArrayList();
				Hashtable hash = new Hashtable();
				if (subentity != null && subentity.size() > 0) {
					for (int i = 0; i < subentity.size(); i++) {
						Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
						if (hdata.getCategory().equals("Interface")) {
							for (int k = 0; k < InterfaceItem.length; k++) {
								if (hdata.getEntity() != null && hdata.getEntity().equals(InterfaceItem[k])) {
									list2.add(hdata);
									break;
								}
							}
						}
					}
				}
				if (list2.size() != 0) {
					for (int i = 0; i < list2.size(); i++) {
						Hostlastcollectdata obj = (Hostlastcollectdata) (list2.get(i));
						String value = obj.getThevalue();
						if (obj.getEntity().equalsIgnoreCase("OutBandwidthUtilHdxPerc") || obj.getEntity().equalsIgnoreCase("InBandwidthUtilHdxPerc")) {
							value = dofloat(value);
						}

						hash.put(obj.getEntity() + obj.getSubentity(), value + obj.getUnit());
					}
				}
				for (int j = 0; j < order.size(); j++) {
					String[] strs = new String[InterfaceItem.length];
					for (int i = 0; i < strs.length; i++) {
						strs[i] = "";

						if (i == 2 || i == 8 || i == 9) {
							strs[i] = "0kb/s";
						}
						if (i == 4 || i == 5 || i == 6 || i == 7) {
							strs[i] = "0";
						}
					}
					String index = order.get(j).toString();
					for (int i = 0; i < InterfaceItem.length; i++) {
						if (hash.get(InterfaceItem[i] + index) != null) {
							String value = hash.get(InterfaceItem[i] + index).toString();
							strs[i] = value;
						}
					}
					vector.add(strs);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vector;
	}

	/**
	 * 获取所有链路的端口信息集合
	 * 
	 * @param ipList
	 *            链路ip集合
	 * @param netInterfaceItem
	 * @param string
	 * @param string2
	 * @param string3
	 * @return
	 */
	public Hashtable getInterfaces(List<String> ipList, String[] netInterfaceItem, String orderflag, String starttime, String endtime) {
		Hashtable retHashtable = new Hashtable();
		InterfaceTempDao interfacedao = new InterfaceTempDao();
		Hashtable nodeListHash = null;
		try {
			nodeListHash = interfacedao.getNodeTempListHash(ipList, netInterfaceItem);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			interfacedao.close();
		}
		for (int f = 0; f < ipList.size(); f++) {
			String ip = ipList.get(f);
			Vector vector = new Vector();
			try {
				List list = new ArrayList();
				Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
				if (host == null) {
					// 从数据库里获取
					HostNodeDao hostdao = new HostNodeDao();
					HostNode node = null;
					try {
						node = (HostNode) hostdao.findByIpaddress(ip);
					} catch (Exception e) {
					} finally {
						hostdao.close();
					}
					HostLoader loader = new HostLoader();
					loader.loadOne(node);
					host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
				}
				// 从数据库查出所有相关的接口数据
				List allinterfacelist = new ArrayList();
				Vector interfacevector = new Vector();
				Vector utilhdxVector = new Vector();
				Vector inpksVector = new Vector();
				Vector outpksVector = new Vector();
				Vector utilhdxpercVector = new Vector();

				try {
					if (nodeListHash != null && nodeListHash.containsKey(ip)) {
						allinterfacelist = (ArrayList) nodeListHash.get(ip);
					}
					if (allinterfacelist != null && allinterfacelist.size() > 0) {
						Interfacecollectdata interfacedata = null;
						UtilHdx utilhdx = new UtilHdx();
						InPkts inpacks = new InPkts();
						OutPkts outpacks = new OutPkts();
						UtilHdxPerc utilhdxperc = new UtilHdxPerc();
						for (int i = 0; i < allinterfacelist.size(); i++) {
							NodeTemp vo = (NodeTemp) allinterfacelist.get(i);
							if ("AllInBandwidthUtilHdx".equals(vo.getSindex()) || "AllOutBandwidthUtilHdx".equals(vo.getSindex()) || "AllBandwidthUtilHdx".equals(vo.getSindex())) {
								continue;
							}
							if ("index".equals(vo.getSubentity()) || "ifDescr".equals(vo.getSubentity()) || "ifType".equals(vo.getSubentity()) || "ifMtu".equals(vo.getSubentity()) || "ifSpeed".equals(vo.getSubentity())
									|| "ifPhysAddress".equals(vo.getSubentity()) || "ifAdminStatus".equals(vo.getSubentity()) || "ifOperStatus".equals(vo.getSubentity()) || "ifname".equals(vo.getSubentity())) {
								// 接口配置数据
								interfacedata = new Interfacecollectdata();
								interfacedata.setIpaddress(host.getIpAddress());
								interfacedata.setCollecttime(null);
								interfacedata.setCategory("Interface");
								interfacedata.setSubentity(vo.getSindex());
								interfacedata.setRestype("static");
								interfacedata.setUnit(vo.getUnit());
								interfacedata.setThevalue(vo.getThevalue());
								interfacedata.setChname(vo.getChname());
								interfacedata.setBak("");
								interfacedata.setEntity(vo.getSubentity());
								interfacevector.add(interfacedata);
							} else if ("ifInMulticastPkts".equals(vo.getSubentity()) || "ifInBroadcastPkts".equals(vo.getSubentity())) {
								inpacks = new InPkts();
								inpacks.setIpaddress(host.getIpAddress());
								inpacks.setCollecttime(null);
								inpacks.setCategory("Interface");
								inpacks.setEntity(vo.getSubentity());
								inpacks.setSubentity(vo.getSindex());
								inpacks.setRestype("dynamic");
								inpacks.setUnit("");
								if ("ifInMulticastPkts".equals(vo.getSubentity())) {
									inpacks.setChname("多播");
								} else {
									inpacks.setChname("广播");
								}
								inpacks.setThevalue(vo.getThevalue());
								inpksVector.addElement(inpacks);
							} else if ("ifOutMulticastPkts".equals(vo.getSubentity()) || "ifOutBroadcastPkts".equals(vo.getSubentity())) {
								outpacks = new OutPkts();
								outpacks.setIpaddress(host.getIpAddress());
								outpacks.setCollecttime(null);
								outpacks.setCategory("Interface");
								outpacks.setEntity(vo.getSubentity());
								outpacks.setSubentity(vo.getSindex());
								outpacks.setRestype("dynamic");
								outpacks.setUnit("");
								if ("ifOutMulticastPkts".equals(vo.getSubentity())) {
									outpacks.setChname("多播");
								} else {
									outpacks.setChname("广播");
								}
								outpacks.setThevalue(vo.getThevalue());
								outpksVector.addElement(outpacks);
							} else if ("InBandwidthUtilHdx".equals(vo.getSubentity()) || "OutBandwidthUtilHdx".equals(vo.getSubentity())) {
								utilhdx = new UtilHdx();
								utilhdx.setIpaddress(host.getIpAddress());
								utilhdx.setCollecttime(null);
								utilhdx.setCategory("Interface");
								utilhdx.setEntity(vo.getSubentity());
								utilhdx.setSubentity(vo.getSindex());
								utilhdx.setRestype("dynamic");
								utilhdx.setUnit(vo.getUnit());
								if ("InBandwidthUtilHdx".equals(vo.getSubentity())) {
									utilhdx.setChname("入口");
								} else {
									utilhdx.setChname("出口");
								}
								utilhdx.setThevalue(vo.getThevalue());
								utilhdxVector.addElement(utilhdx);
							} else if ("InBandwidthUtilHdxPerc".equals(vo.getSubentity()) || "OutBandwidthUtilHdxPerc".equals(vo.getSubentity())) {
								utilhdxperc = new UtilHdxPerc();
								utilhdxperc.setIpaddress(host.getIpAddress());
								utilhdxperc.setCollecttime(null);
								utilhdxperc.setCategory("Interface");
								utilhdxperc.setEntity(vo.getSubentity());
								utilhdxperc.setSubentity(vo.getSindex());
								utilhdxperc.setRestype("dynamic");
								utilhdxperc.setUnit(vo.getUnit());
								if ("InBandwidthUtilHdxPerc".equals(vo.getSubentity())) {
									utilhdxperc.setChname("入口");
								} else {
									utilhdxperc.setChname("出口");
								}
								utilhdxperc.setThevalue(vo.getThevalue());
								utilhdxpercVector.addElement(utilhdxperc);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 全部
				Hashtable hash2 = new Hashtable();
				Vector subentity = new Vector();

				if (interfacevector != null && interfacevector.size() > 0) {
					for (int i = 0; i < interfacevector.size(); i++) {
						Interfacecollectdata interfacedata = (Interfacecollectdata) interfacevector.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(interfacedata.getIpaddress());
						lastdata.setCategory(interfacedata.getCategory());
						lastdata.setEntity(interfacedata.getEntity());
						lastdata.setSubentity(interfacedata.getSubentity());
						lastdata.setThevalue(interfacedata.getThevalue());
						lastdata.setCollecttime(interfacedata.getCollecttime());
						lastdata.setRestype(interfacedata.getRestype());
						lastdata.setUnit(interfacedata.getUnit());
						lastdata.setChname(interfacedata.getChname());
						lastdata.setBak(interfacedata.getBak());
						subentity.add(lastdata);
					}
				}

				if (utilhdxVector != null && utilhdxVector.size() > 0) {
					for (int i = 0; i < utilhdxVector.size(); i++) {
						UtilHdx utilhdx = (UtilHdx) utilhdxVector.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(utilhdx.getIpaddress());
						lastdata.setCategory(utilhdx.getCategory());
						lastdata.setEntity(utilhdx.getEntity());
						lastdata.setSubentity(utilhdx.getSubentity());
						lastdata.setThevalue(utilhdx.getThevalue());
						lastdata.setCollecttime(utilhdx.getCollecttime());
						lastdata.setRestype(utilhdx.getRestype());
						lastdata.setUnit(utilhdx.getUnit());
						lastdata.setChname(utilhdx.getChname());
						lastdata.setBak(utilhdx.getBak());
						subentity.add(lastdata);
					}
				}

				if (utilhdxpercVector != null && utilhdxpercVector.size() > 0) {
					for (int i = 0; i < utilhdxpercVector.size(); i++) {
						UtilHdxPerc utilhdx = (UtilHdxPerc) utilhdxpercVector.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(utilhdx.getIpaddress());
						lastdata.setCategory(utilhdx.getCategory());
						lastdata.setEntity(utilhdx.getEntity());
						lastdata.setSubentity(utilhdx.getSubentity());
						lastdata.setThevalue(utilhdx.getThevalue());
						lastdata.setCollecttime(utilhdx.getCollecttime());
						lastdata.setRestype(utilhdx.getRestype());
						lastdata.setUnit(utilhdx.getUnit());
						lastdata.setChname(utilhdx.getChname());
						lastdata.setBak(utilhdx.getBak());
						subentity.add(lastdata);
					}
				}

				if (inpksVector != null && inpksVector.size() > 0) {
					for (int i = 0; i < inpksVector.size(); i++) {
						InPkts inpacks = (InPkts) inpksVector.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(inpacks.getIpaddress());
						lastdata.setCategory(inpacks.getCategory());
						lastdata.setEntity(inpacks.getEntity());
						lastdata.setSubentity(inpacks.getSubentity());
						lastdata.setThevalue(inpacks.getThevalue());
						lastdata.setCollecttime(inpacks.getCollecttime());
						lastdata.setRestype(inpacks.getRestype());
						lastdata.setUnit(inpacks.getUnit());
						lastdata.setChname(inpacks.getChname());
						lastdata.setBak(inpacks.getBak());
						subentity.add(lastdata);
					}
				}

				if (outpksVector != null && outpksVector.size() > 0) {
					for (int i = 0; i < outpksVector.size(); i++) {
						OutPkts outpacks = (OutPkts) outpksVector.elementAt(i);
						Hostlastcollectdata lastdata = new Hostlastcollectdata();
						lastdata.setIpaddress(outpacks.getIpaddress());
						lastdata.setCategory(outpacks.getCategory());
						lastdata.setEntity(outpacks.getEntity());
						lastdata.setSubentity(outpacks.getSubentity());
						lastdata.setThevalue(outpacks.getThevalue());
						lastdata.setCollecttime(outpacks.getCollecttime());
						lastdata.setRestype(outpacks.getRestype());
						lastdata.setUnit(outpacks.getUnit());
						lastdata.setChname(outpacks.getChname());
						lastdata.setBak(outpacks.getBak());
						subentity.add(lastdata);
					}
				}
				if (subentity != null && subentity.size() > 0) {
					for (int i = 0; i < subentity.size(); i++) {
						Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
						if (hdata.getCategory().equals("Interface") && hdata.getEntity().equals("index")) {
							list.add(hdata.getSubentity());
						}
					}
				}
				if (list != null && list.size() != 0) {
					for (int i = 0; i < list.size(); i++) {
						hash2.put(list.get(i).toString(), "");
					}

					// 有出口流速的端口
					list = null;
					list = new ArrayList();
					List orderList = new ArrayList();
					if (subentity != null && subentity.size() > 0) {
						for (int i = 0; i < subentity.size(); i++) {
							Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
							if (hdata.getEntity().equals(orderflag)) {
								if (list.contains(hdata.getSubentity())) {
									continue;
								}
								list.add(hdata.getSubentity());
								orderList.add(hdata);
							}
						}
					}
					// 对orderList根据theValue进行排序
					list = null;
					list = new ArrayList();
					if (orderList != null && orderList.size() > 0) {
						for (int m = 0; m < orderList.size(); m++) {
							Hostlastcollectdata hdata = (Hostlastcollectdata) orderList.get(m);
							for (int n = m + 1; n < orderList.size(); n++) {
								Hostlastcollectdata hosdata = (Hostlastcollectdata) orderList.get(n);
								if (orderflag.equalsIgnoreCase("index")) {
									if (new Double(hdata.getThevalue()).doubleValue() > new Double(hosdata.getThevalue()).doubleValue()) {
										orderList.remove(m);
										orderList.add(m, hosdata);
										orderList.remove(n);
										orderList.add(n, hdata);
										hdata = hosdata;
										hosdata = null;
									}
								} else {
									if (new Double(hdata.getThevalue()).doubleValue() < new Double(hosdata.getThevalue()).doubleValue()) {
										orderList.remove(m);
										orderList.add(m, hosdata);
										orderList.remove(n);
										orderList.add(n, hdata);
										hdata = hosdata;
										hosdata = null;
									}
								}
							}
							// 得到排序后的Subentity的列表
							list.add(hdata.getSubentity());
							hdata = null;
						}
					}

					Vector order = new Vector();
					for (int i = 0; i < list.size(); i++) {
						hash2.remove(list.get(i).toString());
						if (order.contains(list.get(i).toString())) {
							continue;
						}
						order.add(list.get(i).toString());
					}
					Set key = hash2.keySet();
					Iterator it = key.iterator();
					while (it.hasNext()) {
						String ss = (String) it.next();
						order.add(ss);
					}
					List list2 = new ArrayList();
					Hashtable hash = new Hashtable();
					if (subentity != null && subentity.size() > 0) {
						for (int i = 0; i < subentity.size(); i++) {
							Hostlastcollectdata hdata = (Hostlastcollectdata) subentity.get(i);
							if (hdata.getCategory().equals("Interface")) {
								for (int k = 0; k < netInterfaceItem.length; k++) {
									if (hdata.getEntity() != null && hdata.getEntity().equals(netInterfaceItem[k])) {
										list2.add(hdata);
										break;
									}
								}
							}
						}
					}
					if (list2.size() != 0) {
						for (int i = 0; i < list2.size(); i++) {
							Hostlastcollectdata obj = (Hostlastcollectdata) (list2.get(i));
							String value = obj.getThevalue();
							if (obj.getEntity().equalsIgnoreCase("OutBandwidthUtilHdxPerc") || obj.getEntity().equalsIgnoreCase("InBandwidthUtilHdxPerc")) {
								value = dofloat(value);
							}
							hash.put(obj.getEntity() + obj.getSubentity(), value);
						}
					}
					for (int j = 0; j < order.size(); j++) {
						String[] strs = new String[netInterfaceItem.length];
						for (int i = 0; i < strs.length; i++) {
							strs[i] = "";

							if (i == 2 || i == 8 || i == 9) {
								strs[i] = "0kb/s";
							}
							if (i == 4 || i == 5 || i == 6 || i == 7) {
								strs[i] = "0";
							}
						}
						String index = order.get(j).toString();
						for (int i = 0; i < netInterfaceItem.length; i++) {
							if (hash.get(netInterfaceItem[i] + index) != null) {
								String value = hash.get(netInterfaceItem[i] + index).toString();
								strs[i] = value;
							}
						}
						vector.add(strs);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			retHashtable.put(ip, vector);
		}
		return retHashtable;
	}

	/**
	 * @author hipo 作用：采集不到ipmac数据的时候从数据库获取历史数据
	 */
	public Vector getIpMac(String ip) throws Exception {
		Vector vector = new Vector();
		Hashtable ipmacHashtable;
		IpMac ipMac = new IpMac();
		String sql = "";
		if (ShareData.getSharedata().containsKey(ip)) {
			ipmacHashtable = (Hashtable) ShareData.getSharedata().get(ip);
			if (ipmacHashtable != null) {
				vector = (Vector) ipmacHashtable.get("ipmac");
				if (vector != null && !((IpMac) vector.get(0)).getIfindex().equals("unknown")) {
					return vector;
				} else {
					vector = new Vector();
					DBManager dbm = new DBManager();
					Calendar calendar = new GregorianCalendar();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					sql = "select * from ipmac where RELATEIPADDR='" + ip + "'";
					ResultSet rs = dbm.executeQuery(sql);
					while (rs.next()) {
						ipMac = new IpMac();
						Date date = sdf.parse(rs.getString("COLLECTTIME"));
						calendar.setTime(date);
						ipMac.setBak(rs.getString("BAK"));
						ipMac.setCollecttime(calendar);
						ipMac.setId(rs.getLong("id"));
						ipMac.setIfband(rs.getString("IFBAND"));
						ipMac.setIfindex(rs.getString("IFINDEX"));
						ipMac.setIfsms(rs.getString("IFSMS"));
						ipMac.setIpaddress(rs.getString("IPADDRESS"));
						ipMac.setMac(rs.getString("MAC"));
						ipMac.setRelateipaddr(rs.getString("RELATEIPADDR"));
						vector.add(ipMac);
					}
				}
			} else {
				vector = new Vector();
				DBManager dbm = new DBManager();
				Calendar calendar = new GregorianCalendar();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				sql = "select * from ipmac where RELATEIPADDR='" + ip + "'";
				ResultSet rs = dbm.executeQuery(sql);
				while (rs.next()) {
					ipMac = new IpMac();
					Date date = sdf.parse(rs.getString("COLLECTTIME"));
					calendar.setTime(date);
					ipMac.setBak(rs.getString("BAK"));
					ipMac.setCollecttime(calendar);
					ipMac.setId(rs.getLong("id"));
					ipMac.setIfband(rs.getString("IFBAND"));
					ipMac.setIfindex(rs.getString("IFINDEX"));
					ipMac.setIfsms(rs.getString("IFSMS"));
					ipMac.setIpaddress(rs.getString("IPADDRESS"));
					ipMac.setMac(rs.getString("MAC"));
					ipMac.setRelateipaddr(rs.getString("ip"));

					vector.add(ipMac);
				}
			}

		}
		return vector;
	}

	public Hashtable getLastDiscards(String ip) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getLastErrors(String ip) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public Hashtable getLastPacks(String ip) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	// memory可能有时间限制
	public Hashtable getMemory(String ip, String category, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		try {
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByIpaddress(ip);
				} catch (Exception e) {
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			}
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);

			MemoryTempDao memorydao = new MemoryTempDao();
			List allmemorylist = new ArrayList();
			try {
				allmemorylist = memorydao.getCurrMemoryListInfo(host.getId() + "", nodedto.getType(), nodedto.getSubtype());
			} catch (Exception e) {

			} finally {
				memorydao.close();
			}
			Vector memoryVector = new Vector();
			if (allmemorylist != null && allmemorylist.size() > 0) {
				MemoryCollectEntity memorydata = null;
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (int i = 0; i < allmemorylist.size(); i++) {
					NodeTemp nodetemp = (NodeTemp) allmemorylist.get(i);
					memorydata = new MemoryCollectEntity();
					memorydata.setIpaddress(host.getIpAddress());
					Date d = sdf1.parse(nodetemp.getCollecttime());
					Calendar c = Calendar.getInstance();
					c.setTime(d);
					memorydata.setCollecttime(c);
					memorydata.setCategory(nodetemp.getEntity());
					memorydata.setEntity(nodetemp.getSubentity());
					memorydata.setSubentity(nodetemp.getSindex());
					memorydata.setRestype(nodetemp.getRestype());
					memorydata.setUnit(nodetemp.getUnit());
					memorydata.setThevalue(nodetemp.getThevalue());
					memoryVector.addElement(memorydata);
				}
			}

			Vector sdata = memoryVector;
			if (category.toLowerCase().equalsIgnoreCase("memory")) {
				Vector keydata = new Vector();
				Vector alldata = new Vector();
				if (sdata != null && sdata.size() > 0) {
					for (int i = 0; i < sdata.size(); i++) {
						MemoryCollectEntity hdata = (MemoryCollectEntity) sdata.get(i);
						if (!hdata.getCategory().equalsIgnoreCase(category)) {
							continue;
						}
						alldata.add(hdata);
						if (keydata.contains(hdata.getSubentity())) {
							continue;
						}
						keydata.add(hdata.getSubentity());
					}
					String[] key = new String[keydata.size()];
					Hashtable[] hashs = new Hashtable[keydata.size()];
					for (int i = 0; i < keydata.size(); i++) {
						key[i] = (String) (keydata.get(i));
						hashs[i] = new Hashtable();
					}
					for (int j = 0; j < alldata.size(); j++) {
						MemoryCollectEntity h = (MemoryCollectEntity) alldata.get(j);
						for (int m = 0; m < keydata.size(); m++) {
							if (h.getSubentity().equals(key[m])) {
								hashs[m].put(h.getEntity(), dofloat(h.getThevalue()) + h.getUnit());
								break;
							}
						}
					}
					for (int i = 0; i < keydata.size(); i++) {
						hashs[i].put("name", key[i]);
						hash.put(new Integer(i), hashs[i]);
					}
				}
			} else if (category.toLowerCase().equalsIgnoreCase("disk")) {
				Vector keydata = new Vector();
				Vector alldata = new Vector();
				if (sdata != null && sdata.size() > 0) {
					for (int i = 0; i < sdata.size(); i++) {
						DiskCollectEntity hdata = (DiskCollectEntity) sdata.get(i);
						if (!hdata.getCategory().equalsIgnoreCase(category)) {
							continue;
						}
						alldata.add(hdata);
						if (keydata.contains(hdata.getSubentity())) {
							continue;
						}
						keydata.add(hdata.getSubentity());
					}
					String[] key = new String[keydata.size()];
					Hashtable[] hashs = new Hashtable[keydata.size()];
					for (int i = 0; i < keydata.size(); i++) {
						key[i] = (String) (keydata.get(i));
						hashs[i] = new Hashtable();
					}
					for (int j = 0; j < alldata.size(); j++) {
						Hostcollectdata h = (Hostcollectdata) alldata.get(j);
						for (int m = 0; m < keydata.size(); m++) {
							if (h.getSubentity().equals(key[m])) {
								hashs[m].put(h.getEntity(), dofloat(h.getThevalue()) + h.getUnit());
								break;
							}
						}
					}
					for (int i = 0; i < keydata.size(); i++) {
						hashs[i].put("name", key[i]);
						hash.put(new Integer(i), hashs[i]);
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	public Hashtable getMemory_share(String ip, String category, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		try {
			Hashtable sharedata = ShareData.getSharedata();
			Hashtable ipdata = (Hashtable) sharedata.get(ip);
			if (ipdata != null && ipdata.size() > 0) {
				Vector sdata = (Vector) ipdata.get(category.toLowerCase());
				if (category.toLowerCase().equalsIgnoreCase("memory")) {
					Vector keydata = new Vector();
					Vector alldata = new Vector();
					if (sdata != null && sdata.size() > 0) {
						for (int i = 0; i < sdata.size(); i++) {
							MemoryCollectEntity hdata = (MemoryCollectEntity) sdata.get(i);
							if (!hdata.getCategory().equalsIgnoreCase(category)) {
								continue;
							}
							alldata.add(hdata);
							if (keydata.contains(hdata.getSubentity())) {
								continue;
							}
							keydata.add(hdata.getSubentity());
						}
						String[] key = new String[keydata.size()];
						Hashtable[] hashs = new Hashtable[keydata.size()];
						for (int i = 0; i < keydata.size(); i++) {
							key[i] = (String) (keydata.get(i));
							hashs[i] = new Hashtable();
						}
						for (int j = 0; j < alldata.size(); j++) {
							MemoryCollectEntity h = (MemoryCollectEntity) alldata.get(j);
							for (int m = 0; m < keydata.size(); m++) {
								if (h.getSubentity().equals(key[m])) {
									hashs[m].put(h.getEntity(), dofloat(h.getThevalue()) + h.getUnit());
									break;
								}
							}
						}
						for (int i = 0; i < keydata.size(); i++) {
							hashs[i].put("name", key[i]);
							hash.put(new Integer(i), hashs[i]);
						}
					}
				} else if (category.toLowerCase().equalsIgnoreCase("disk")) {
					Vector keydata = new Vector();
					Vector alldata = new Vector();
					if (sdata != null && sdata.size() > 0) {
						for (int i = 0; i < sdata.size(); i++) {
							DiskCollectEntity hdata = (DiskCollectEntity) sdata.get(i);
							if (!hdata.getCategory().equalsIgnoreCase(category)) {
								continue;
							}
							alldata.add(hdata);
							if (keydata.contains(hdata.getSubentity())) {
								continue;
							}
							keydata.add(hdata.getSubentity());
						}
						String[] key = new String[keydata.size()];
						Hashtable[] hashs = new Hashtable[keydata.size()];
						for (int i = 0; i < keydata.size(); i++) {
							key[i] = (String) (keydata.get(i));
							hashs[i] = new Hashtable();
						}
						for (int j = 0; j < alldata.size(); j++) {
							Hostcollectdata h = (Hostcollectdata) alldata.get(j);
							for (int m = 0; m < keydata.size(); m++) {
								if (h.getSubentity().equals(key[m])) {
									hashs[m].put(h.getEntity(), dofloat(h.getThevalue()) + h.getUnit());
									break;
								}
							}
						}
						for (int i = 0; i < keydata.size(); i++) {
							hashs[i].put("name", key[i]);
							hash.put(new Integer(i), hashs[i]);
						}
					}
				}
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	public List[] getParameter(String[] ip) throws Exception {// monitoriplisttask里用到
		int size = ip.length;
		List[] host = new ArrayList[size];
		return host;
	}

	// process一定有时间限制
	public Hashtable getProcess(String ip, String category, String order, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		try {
			List list1 = new ArrayList();
			List list2 = new ArrayList();
			List orderList = new ArrayList();
			Host host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			if (host == null) {
				// 从数据库里获取
				HostNodeDao hostdao = new HostNodeDao();
				HostNode node = null;
				try {
					node = (HostNode) hostdao.findByIpaddress(ip);
				} catch (Exception e) {
				} finally {
					hostdao.close();
				}
				HostLoader loader = new HostLoader();
				loader.loadOne(node);
				host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
			}
			NodeUtil nodeUtil = new NodeUtil();
			NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
			List allprocesslist = new ArrayList();
			ProcessTempDao processdao = new ProcessTempDao();
			Vector processVector = new Vector();
			try {
				allprocesslist = processdao.getNodeTempList(host.getId() + "", nodedto.getType(), nodedto.getSubtype());
				if (allprocesslist != null && allprocesslist.size() > 0) {
					ProcessCollectEntity processdata = null;
					for (int i = 0; i < allprocesslist.size(); i++) {
						NodeTemp vo = (NodeTemp) allprocesslist.get(i);
						processdata = new ProcessCollectEntity();
						processdata.setIpaddress(host.getIpAddress());
						processdata.setCollecttime(null);
						processdata.setCategory(vo.getEntity());
						processdata.setEntity(vo.getSubentity());
						processdata.setSubentity(vo.getSindex());
						processdata.setRestype(vo.getRestype());
						processdata.setUnit(" ");
						processdata.setThevalue(vo.getThevalue());
						processdata.setChname(vo.getChname());
						processVector.addElement(processdata);
					}
				}
			} catch (Exception e) {

			} finally {
				processdao.close();
			}
			Vector sdata = processVector;
			if (category.toLowerCase().equalsIgnoreCase("Process")) {
				if (sdata != null && sdata.size() > 0) {
					for (int i = 0; i < sdata.size(); i++) {
						ProcessCollectEntity hdata = (ProcessCollectEntity) sdata.get(i);
						if (hdata.getCategory().equals(category)) {
							list2.add(hdata);
							if (hdata.getEntity().equals(order)) {
								if (!list1.contains(hdata.getSubentity())) {
									list1.add(hdata.getSubentity());
									orderList.add(hdata);
								}
							}
						}
					}
				}
				// 按照thevalue进行排序
				int aix_flag = 0;
				host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
				if ((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL || host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH)
						&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
					aix_flag = 1;// aix
				}
				if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
					aix_flag = 2;// linux
				}
				if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.11.2.3.10.1") >= 0) {
					aix_flag = 3;// hp-unix //HONGLI ADD
				}
				if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.42.2.1.1") >= 0) {
					aix_flag = 4;// sun-solaris //HONGLI ADD
				}
				list1 = null;
				list1 = new ArrayList();
				if (orderList != null && orderList.size() > 0) {
					for (int m = 0; m < orderList.size(); m++) {
						ProcessCollectEntity hdata = (ProcessCollectEntity) orderList.get(m);
						for (int n = m + 1; n < orderList.size(); n++) {
							if ((aix_flag == 1 || aix_flag == 2 || aix_flag == 3 || aix_flag == 4) && isCpuTime) {
								ProcessCollectEntity hosdata = (ProcessCollectEntity) orderList.get(n);
								String hdata_str = hdata.getThevalue();
								String hosdata_str = hosdata.getThevalue();
								if (hdata_str != null && hosdata_str != null && hdata_str.trim().length() > 0 && hosdata_str.trim().length() > 0) {
									String tmp = hdata.getThevalue();
									String[] time_str = tmp.split(":");
									int hdata_int = new Integer(time_str[0]) * 60 + new Integer(time_str[1]);
									time_str = hosdata.getThevalue().split(":");
									int hosdata_int = new Integer(time_str[0]) * 60 + new Integer(time_str[1]);
									if (hdata_int < hosdata_int) {
										orderList.remove(m);
										orderList.add(m, hosdata);
										orderList.remove(n);
										orderList.add(n, hdata);
										hdata = hosdata;
										hosdata = null;
									}
								}
							} else {
								ProcessCollectEntity hosdata = (ProcessCollectEntity) orderList.get(n);
								if (hdata.getThevalue() != null && hosdata.getThevalue() != null && hdata.getThevalue().trim().length() > 0 && hosdata.getThevalue().trim().length() > 0) {
									if (new Double(hdata.getThevalue()).doubleValue() < new Double(hosdata.getThevalue()).doubleValue()) {
										orderList.remove(m);
										orderList.add(m, hosdata);
										orderList.remove(n);
										orderList.add(n, hdata);
										hdata = hosdata;
										hosdata = null;
									}
								}
							}

						}
						list1.add(hdata.getSubentity());
						hdata = null;
					}
				}
				aix_flag = 0;
				isCpuTime = false;

				if (list1.size() != 0) {
					String[] key = new String[list1.size()];
					Hashtable[] hashs = new Hashtable[list1.size()];
					for (int i = 0; i < list1.size(); i++) {
						key[i] = (String) (list1.get(i));
						hashs[i] = new Hashtable();
					}
					for (int j = 0; j < list2.size(); j++) {
						ProcessCollectEntity h = (ProcessCollectEntity) list2.get(j);
						for (int m = 0; m < list1.size(); m++) {
							if (h.getSubentity().equals(key[m])) {
								String value = h.getThevalue();
								if (h.getEntity().equalsIgnoreCase("MemoryUtilization")) {
									if (value != null && value.trim().length() > 0) {
										value = dofloat(value);
									}
								}
								hashs[m].put(h.getEntity(), value + h.getUnit());
								break;
							}
						}
					}
					for (int i = 0; i < list1.size(); i++) {
						hashs[i].put("process_id", key[i]);
						hash.put(new Integer(i), hashs[i]);
					}
				}

			}

			list1 = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	// process一定有时间限制
	public Hashtable getProcess_share(String ip, String category, String order, String starttime, String endtime) throws Exception {
		Hashtable hash = new Hashtable();
		try {
			List list1 = new ArrayList();
			List list2 = new ArrayList();
			List orderList = new ArrayList();
			Hashtable sharedata = ShareData.getSharedata();
			Hashtable ipdata = (Hashtable) sharedata.get(ip);
			if (ipdata != null && ipdata.size() > 0) {
				Vector sdata = (Vector) ipdata.get(category.toLowerCase());
				if (category.toLowerCase().equalsIgnoreCase("Process")) {
					if (sdata != null && sdata.size() > 0) {
						for (int i = 0; i < sdata.size(); i++) {
							ProcessCollectEntity hdata = (ProcessCollectEntity) sdata.get(i);
							if (hdata.getCategory().equals(category)) {
								list2.add(hdata);
								if (hdata.getEntity().equals(order)) {
									if (!list1.contains(hdata.getSubentity())) {
										list1.add(hdata.getSubentity());
										orderList.add(hdata);
									}
								}
							}
						}
					}
					// 按照thevalue进行排序
					int aix_flag = 0;
					host = (Host) PollingEngine.getInstance().getNodeByIP(ip);
					if ((host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL || host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNET || host.getCollecttype() == SystemConstant.COLLECTTYPE_SSH)
							&& host.getSysOid().indexOf("1.3.6.1.4.1.2.3.1.2.1.1") >= 0) {
						aix_flag = 1;// aix
					}
					if (host.getCollecttype() == SystemConstant.COLLECTTYPE_SHELL && host.getSysOid().indexOf("1.3.6.1.4.1.2021") >= 0) {
						aix_flag = 2;// linux
					}
					list1 = null;
					list1 = new ArrayList();
					if (orderList != null && orderList.size() > 0) {
						for (int m = 0; m < orderList.size(); m++) {
							ProcessCollectEntity hdata = (ProcessCollectEntity) orderList.get(m);
							for (int n = m + 1; n < orderList.size(); n++) {
								if ((aix_flag == 1 || aix_flag == 2) && isCpuTime) {
									ProcessCollectEntity hosdata = (ProcessCollectEntity) orderList.get(n);
									String hdata_str = hdata.getThevalue();
									String hosdata_str = hosdata.getThevalue();
									if (hdata_str != null && hosdata_str != null && hdata_str.trim().length() > 0 && hosdata_str.trim().length() > 0) {
										String tmp = hdata.getThevalue();
										String[] time_str = tmp.split(":");
										int hdata_int = new Integer(time_str[0]) * 60 + new Integer(time_str[1]);
										time_str = hosdata.getThevalue().split(":");
										int hosdata_int = new Integer(time_str[0]) * 60 + new Integer(time_str[1]);
										if (hdata_int < hosdata_int) {
											orderList.remove(m);
											orderList.add(m, hosdata);
											orderList.remove(n);
											orderList.add(n, hdata);
											hdata = hosdata;
											hosdata = null;
										}
									}
								} else {
									ProcessCollectEntity hosdata = (ProcessCollectEntity) orderList.get(n);
									if (hdata.getThevalue() != null && hosdata.getThevalue() != null && hdata.getThevalue().trim().length() > 0 && hosdata.getThevalue().trim().length() > 0) {
										if (new Double(hdata.getThevalue()).doubleValue() < new Double(hosdata.getThevalue()).doubleValue()) {
											orderList.remove(m);
											orderList.add(m, hosdata);
											orderList.remove(n);
											orderList.add(n, hdata);
											hdata = hosdata;
											hosdata = null;
										}
									}
								}

							}
							list1.add(hdata.getSubentity());
							hdata = null;
						}
					}
					aix_flag = 0;
					isCpuTime = false;

					if (list1.size() != 0) {
						String[] key = new String[list1.size()];
						Hashtable[] hashs = new Hashtable[list1.size()];
						for (int i = 0; i < list1.size(); i++) {
							key[i] = (String) (list1.get(i));
							hashs[i] = new Hashtable();
						}
						for (int j = 0; j < list2.size(); j++) {
							ProcessCollectEntity h = (ProcessCollectEntity) list2.get(j);
							for (int m = 0; m < list1.size(); m++) {
								if (h.getSubentity().equals(key[m])) {
									String value = h.getThevalue();
									if (h.getEntity().equalsIgnoreCase("MemoryUtilization")) {
										if (value != null && value.trim().length() > 0) {
											value = dofloat(value);
										}
									}
									hashs[m].put(h.getEntity(), value + h.getUnit());
									break;
								}
							}
						}
						for (int i = 0; i < list1.size(); i++) {
							hashs[i].put("process_id", key[i]);
							hash.put(new Integer(i), hashs[i]);
						}
					}

				}
			}

			list1 = null;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return hash;
	}

	public Hashtable getStatic(String ipaddress, String category[]) throws Exception {
		Hashtable hash = new Hashtable();
		return hash;
	}

	public List loadAllFromRS(ResultSet rs) {
		List list = new ArrayList();
		try {
			while (rs.next()) {
				try {
					Hostlastcollectdata vo = new Hostlastcollectdata();

					vo.setId(new Long(rs.getInt("id")));
					vo.setIpaddress(rs.getString("ipaddress"));
					vo.setRestype(rs.getString("restype"));
					vo.setCategory(rs.getString("category"));
					vo.setEntity(rs.getString("entity"));
					vo.setSubentity(rs.getString("subentity"));
					vo.setThevalue(rs.getString("thevalue"));
					Date timestamp = rs.getTimestamp("collecttime");
					Calendar date = Calendar.getInstance();
					date.setTime(timestamp);
					vo.setCollecttime(date);
					vo.setUnit(rs.getString("unit"));
					list.add(vo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		NodeTemp vo = new NodeTemp();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setIp(rs.getString("ip"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setEntity(rs.getString("entity"));
			vo.setSubentity(rs.getString("subentity"));
			vo.setThevalue(rs.getString("thevalue"));
			vo.setChname(rs.getString("chname"));
			vo.setRestype(rs.getString("restype"));
			vo.setSindex(rs.getString("sindex"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setUnit(rs.getString("unit"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			SysLogger.error("InterfaceTempDao.loadFromRS()", e);
		}
		return vo;
	}

	public void setCpuTime(boolean cpuTime) {
		this.isCpuTime = cpuTime;
	}

	public void setHost(Host host) {
		this.host = host;
	}
}
