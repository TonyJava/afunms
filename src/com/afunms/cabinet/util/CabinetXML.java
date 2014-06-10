package com.afunms.cabinet.util;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.afunms.cabinet.model.CabinetEquipment;
import com.afunms.cabinet.model.EqpRoom;
import com.afunms.cabinet.model.MachineCabinet;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.CpuCollectEntity;
import com.afunms.polling.om.MemoryCollectEntity;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.topology.model.HostNode;
import com.afunms.util.DataGate;

@SuppressWarnings("unchecked")
public class CabinetXML {

	private static Element getElementForAttr(String name, String[] keyfield, String[] key) {
		Element item = new Element(name);
		if (keyfield != null && keyfield.length > 0) {
			for (int i = 0; i < keyfield.length; i++) {
				item.setAttribute(keyfield[i], key[i]);
			}
		}
		return item;
	}

	public static long WriteXMLDoc(Document Doc, String filepath, String filename) {
		try {
			org.jdom.output.Format format = org.jdom.output.Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("    "); // 缩进4个空格后换行
			XMLOutputter XMLOut = new XMLOutputter(format);
			// 输出 XML 文件；
			FileOutputStream fops = new FileOutputStream(filepath + filename);
			XMLOut.output(Doc, fops);
			fops.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	private BaseVo loadFromRS(ResultSet rs) {
		EqpRoom vo = new EqpRoom();
		try {
			vo.setId(rs.getInt("id"));
			vo.setName(rs.getString("name"));
			vo.setDescr(rs.getString("descr"));
			vo.setBak(rs.getString("bak"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	private BaseVo loadFromRS1(ResultSet rs) {
		MachineCabinet machineCabinet = new MachineCabinet();
		try {
			machineCabinet.setId(rs.getInt("id"));
			machineCabinet.setName(rs.getString("name"));
			machineCabinet.setMachinex(rs.getString("machinex"));
			machineCabinet.setMachiney(rs.getString("machiney"));
			machineCabinet.setMachinez(rs.getString("machinez"));
			machineCabinet.setUselect(rs.getString("uselect"));
			machineCabinet.setMotorroom(rs.getString("motorroom"));
			machineCabinet.setStandards(rs.getString("standards"));
			machineCabinet.setPowers(rs.getString("powers"));
			machineCabinet.setHeights(rs.getString("heights"));
			machineCabinet.setWidths(rs.getString("widths"));
			machineCabinet.setDepths(rs.getString("depths"));
			machineCabinet.setNos(rs.getString("nos"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return machineCabinet;
	}

	private BaseVo loadFromRS2(ResultSet rs) {
		CabinetEquipment vo = new CabinetEquipment();
		try {
			vo.setId(rs.getInt("id"));
			vo.setCabinetid(rs.getInt("cabinetid"));
			vo.setNodeid(rs.getInt("nodeid"));
			vo.setNodename(rs.getString("nodename"));
			vo.setNodedescr(rs.getString("nodedescr"));
			vo.setUnmubers(rs.getString("unumbers"));
			vo.setOperid(rs.getInt("operid"));
			vo.setBusinessid(rs.getInt("businessid"));
			vo.setBusinessName(rs.getString("businessName"));
			vo.setContactname(rs.getString("contactname"));
			vo.setContactphone(rs.getString("contactphone"));
			vo.setContactemail(rs.getString("contactemail"));
			vo.setRoomid(rs.getInt("roomid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	private BaseVo loadFromRS3(ResultSet rs) {
		HostNode vo = new HostNode();
		try {
			vo.setId(rs.getInt("id"));
			vo.setAssetid(rs.getString("asset_id"));
			vo.setLocation(rs.getString("location"));
			vo.setIpAddress(rs.getString("ip_address"));
			vo.setIpLong(rs.getLong("ip_long"));
			vo.setSysName(rs.getString("sys_name"));
			vo.setAlias(rs.getString("alias"));
			vo.setNetMask(rs.getString("net_mask"));
			vo.setSysDescr(rs.getString("sys_descr"));
			vo.setSysLocation(rs.getString("sys_location"));
			vo.setSysContact(rs.getString("sys_contact"));
			vo.setSysOid(rs.getString("sys_oid"));
			vo.setCommunity(rs.getString("community"));
			vo.setWriteCommunity(rs.getString("write_community"));
			vo.setSnmpversion(rs.getInt("snmpversion"));
			vo.setTransfer(rs.getInt("transfer"));
			vo.setCategory(rs.getInt("category"));
			vo.setManaged(rs.getInt("managed") == 1 ? true : false);
			vo.setType(rs.getString("type"));
			vo.setSuperNode(rs.getInt("super_node"));
			vo.setLocalNet(rs.getInt("local_net"));
			vo.setLayer(rs.getInt("layer"));
			vo.setBridgeAddress(rs.getString("bridge_address"));
			vo.setStatus(rs.getInt("status"));
			vo.setDiscovertatus(rs.getInt("discoverstatus"));
			vo.setOstype(rs.getInt("ostype"));
			vo.setCollecttype(rs.getInt("collecttype"));
			vo.setSendemail(rs.getString("sendemail"));
			vo.setSendmobiles(rs.getString("sendmobiles"));
			vo.setSendphone(rs.getString("sendphone"));
			vo.setBid(rs.getString("bid"));
			vo.setEndpoint(rs.getInt("endpoint"));
			vo.setSupperid(rs.getInt("supperid"));
			vo.setSecuritylevel(rs.getInt("securitylevel"));
			vo.setSecurityName(rs.getString("securityName"));
			vo.setV3_ap(rs.getInt("v3_ap"));
			vo.setAuthpassphrase(rs.getString("authpassphrase"));
			vo.setV3_privacy(rs.getInt("v3_privacy"));
			vo.setPrivacyPassphrase(rs.getString("privacyPassphrase"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public void CreateCabinetXML() {
		List<EqpRoom> roomList = new ArrayList<EqpRoom>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			try {
				rs = stmt.executeQuery("select * from nms_eqproom order by id");
				if (rs != null) {
					while (rs.next()) {
						roomList.add((EqpRoom) loadFromRS(rs));
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			NodeUtil nodeUtil = new NodeUtil();
			if (roomList != null && roomList.size() > 0) {
				try {
					for (int i = 0; i < roomList.size(); i++) {
						try {
							Element root = new Element("DataResult");
							root.setAttribute("label", "root");// 设置根节点属性
							Document Doc = new Document(root);
							List list = root.getChildren();
							Element jflocality = getElementForAttr("jflocality", null, null);
							int roomId = roomList.get(i).getId();
							String jfname = roomList.get(i).getName();
							Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
							rs = stmt.executeQuery("select count(*) con,uselect from nms_cabinet_config where motorroom = '" + roomId + "' group by uselect ");
							if (rs != null) {
								while (rs.next()) {
									ht.put(rs.getString("uselect"), rs.getInt("con"));
								}
							}
							if (ht != null && ht.size() > 0) {
								for (Iterator it = ht.keySet().iterator(); it.hasNext();) {
									try {
										String key = (String) it.next();
										List<MachineCabinet> machineList = new ArrayList<MachineCabinet>();
										rs = stmt.executeQuery("select * from nms_cabinet_config where motorroom = '" + roomId + "' and uselect = '" + key + "'");
										if (rs != null) {
											while (rs.next()) {
												machineList.add((MachineCabinet) loadFromRS1(rs));
											}
										}
										jflocality.setAttribute("jfid", roomId + "");
										jflocality.setAttribute("jfname", jfname);
										jflocality.setAttribute("jfaddress", jfname);
										jflocality.setAttribute("jfarea", machineList.size() + "");
										jflocality.setAttribute("jfcode", "xxxx");
										jflocality.setAttribute("jfgrade", "xxxx");
										jflocality.setAttribute("jfheight", "xxxx");
										jflocality.setAttribute("jftype", "xxxx");
										jflocality.setAttribute("jfwidth", "xxxx");
										jflocality.setAttribute("rowcount", "xxxx");
										jflocality.setAttribute("colcount", "xxxx");
										if (machineList != null && machineList.size() > 0) {
											for (int j = 0; j < machineList.size(); j++) {
												try {
													MachineCabinet mc = machineList.get(j);
													Element jglocality = new Element("jglocality");
													jglocality.setAttribute("jfid", roomId + "");
													jglocality.setAttribute("jgcolno", "x");
													jglocality.setAttribute("jgcustomer", "x");
													jglocality.setAttribute("jgid", mc.getId() + "");
													jglocality.setAttribute("jgname", mc.getName());
													jglocality.setAttribute("jgno", mc.getId() + "");
													jglocality.setAttribute("jgrowno", mc.getId() + "");
													jglocality.setAttribute("jgstatus", "3");
													jglocality.setAttribute("jgucount", mc.getUselect());
													jglocality.setAttribute("jgx", mc.getMachinex());
													jglocality.setAttribute("jgy", mc.getMachiney());
													jglocality.setAttribute("jgz", mc.getMachinez());
													jglocality.setAttribute("maxA", "120");
													jglocality.setAttribute("maxV", "380");
													List<CabinetEquipment> equipmentList = new ArrayList<CabinetEquipment>();
													try {
														if (mc.getId() != 0) {
															rs = stmt.executeQuery("select * from nms_cabinet_equipments where cabinetid =" + mc.getId());
														}
														while (rs.next()) {
															equipmentList.add((CabinetEquipment) loadFromRS2(rs));
														}
													} catch (Exception e1) {
														e1.printStackTrace();
													}
													for (int k = 0; k < equipmentList.size(); k++) {
														try {
															CabinetEquipment ce = equipmentList.get(k);
															int isalarm = 0;
															Host host = (Host) PollingEngine.getInstance().getNodeByID(ce.getNodeid());
															String hintstr = "";
															NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
															if (ce.getNodeid() != -1) {
																if (host == null) {
																	HostNode node = null;
																	try {
																		rs = stmt.executeQuery("select * from topo_host_node where id=" + ce.getNodeid());
																		if (rs.next()) {
																			node = (HostNode) loadFromRS3(rs);
																		}
																	} catch (Exception e) {
																		e.printStackTrace();
																	}
																	if (node == null) {
																		continue;
																	}
																	host = (Host) PollingEngine.getInstance().getNodeByID(ce.getNodeid());
																} else {
																	hintstr = getInfo(host);
																}
																Hashtable checkEventHashtable = ShareData.getCheckEventHash();
																if (nodeDTO != null) {
																	String chexkname = ce.getNodeid() + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
																	if (checkEventHashtable != null) {
																		for (Iterator its = checkEventHashtable.keySet().iterator(); its.hasNext();) {
																			String keys = (String) its.next();
																			if (keys.startsWith(chexkname)) {
																				isalarm = 1;
																			}
																		}
																	}
																}
															} else {
																host = new Host();
																isalarm = 0;
																host.setType("未监视类型");
																host.setIpAddress("");
																host.setCategory(-1);
																hintstr = "连通率:##CPU利用率:##内存利用率:";
															}
															Element sbargument = new Element("sbargument");
															sbargument.setAttribute("icon", "defaultHead");
															sbargument.setAttribute("jgid", mc.getId() + "");
															sbargument.setAttribute("sbalarm", isalarm + "");
															sbargument.setAttribute("sbapp", ce.getBusinessName());
															sbargument.setAttribute("sbcategory", host.getCategory() + "");
															sbargument.setAttribute("sbhint", hintstr);
															sbargument.setAttribute("sbid", host.getId() + "");
															sbargument.setAttribute("sbip", host.getIpAddress());
															sbargument.setAttribute("sbitype", nodeDTO.getSubtype());
															sbargument.setAttribute("sbname", ce.getNodename());
															sbargument.setAttribute("sbno", "sb" + (k + 1));
															sbargument.setAttribute("sbstartuw", ce.getUnmubers().split(",")[0]);
															sbargument.setAttribute("sbuw", "2");
															sbargument.setAttribute("type", host.getCategory() + "");

															jglocality.addContent(sbargument);
														} catch (Exception e) {
															e.printStackTrace();
														}

													}
													jflocality.addContent(jglocality);
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
							list.add(jflocality);
							String file = "newdata.xml";// 保存到项目文件夹下的指定文件夹
							String filePath = ResourceCenter.getInstance().getSysPath() + "fn" + (i + 1) + "/org/dhcc/dio/";// 获取系统文件夹路径
							WriteXMLDoc(Doc, filePath, file);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				DataGate.freeCon(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取ping、cpu和内存信息
	 * 
	 * @param host
	 *            网络设备或服务器model
	 * @return
	 */
	private String getInfo(Host host) {
		StringBuffer infoBuffer = new StringBuffer();
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
		String pingValue = "0";
		Vector memoryVector = new Vector();
		String cpuValue = "0";
		String memoryValue = "0";
		double memeryValueDouble = 0;
		double cpuValueDouble = 0;
		NumberFormat numberFormat = new DecimalFormat();
		numberFormat.setMaximumFractionDigits(0);
		String runmodel = PollingEngine.getCollectwebflag();
		Hashtable ipAllData = null;
		Vector allpingdata = null;
		if ("0".equals(runmodel)) {
			// 采集与访问是集成模式
			Hashtable sharedata = ShareData.getSharedata();
			ipAllData = (Hashtable) sharedata.get(host.getIpAddress());
			allpingdata = (Vector) ShareData.getPingdata().get(host.getIpAddress());
		} else {
			// 采集与访问是分离模式
			ipAllData = (Hashtable) ShareData.getAllNetworkData().get(host.getIpAddress());
			allpingdata = (Vector) ShareData.getAllNetworkPingData().get(host.getIpAddress());
		}
		if (ipAllData != null) {
			Vector cpuV = (Vector) ipAllData.get("cpu");
			if (cpuV != null && cpuV.size() > 0) {
				CpuCollectEntity cpu = (CpuCollectEntity) cpuV.get(0);
				if (cpu != null && cpu.getThevalue() != null) {
					cpuValueDouble = Double.valueOf(cpu.getThevalue());
					cpuValue = numberFormat.format(cpuValueDouble);
				}
			}
			memoryVector = (Vector) ipAllData.get("memory");
		}
		if (allpingdata != null && allpingdata.size() > 0) {
			PingCollectEntity pingcollectdata = (PingCollectEntity) allpingdata.get(0);
			pingValue = pingcollectdata.getThevalue();
		}
		// 根据设备的类型的不同，存储集合的结构也不一样
		if (nodedto.getType().equals("net")) {
			double allmemoryvalue = 0;
			if (memoryVector != null && memoryVector.size() > 0) {
				for (int i = 0; i < memoryVector.size(); i++) {
					MemoryCollectEntity memorycollectdata = (MemoryCollectEntity) memoryVector.get(i);
					allmemoryvalue = allmemoryvalue + Integer.parseInt(memorycollectdata.getThevalue());
				}
				memeryValueDouble = Math.round(allmemoryvalue / memoryVector.size());
				memoryValue = numberFormat.format(memeryValueDouble);
			}
		} else if (nodedto.getType().equals("host")) {
			// 得到内存利用率
			if (memoryVector != null && memoryVector.size() > 0) {
				for (int i = 0; i < memoryVector.size(); i++) {
					MemoryCollectEntity memorydata = (MemoryCollectEntity) memoryVector.get(i);
					if ("PhysicalMemory".equalsIgnoreCase(memorydata.getSubentity()) && "Utilization".equalsIgnoreCase(memorydata.getEntity())) {
						memoryValue = Math.round(Float.parseFloat(memorydata.getThevalue())) + "";
					}
				}
			}
		}
		infoBuffer.append("连通率:");
		infoBuffer.append(pingValue);
		infoBuffer.append("%");
		infoBuffer.append("##");
		infoBuffer.append("CPU利用率:");
		infoBuffer.append(cpuValue);
		infoBuffer.append("%");
		infoBuffer.append("##");
		infoBuffer.append("内存利用率:");
		infoBuffer.append(memoryValue);
		infoBuffer.append("%");
		infoBuffer.append("##");
		return infoBuffer.toString();
	}

}