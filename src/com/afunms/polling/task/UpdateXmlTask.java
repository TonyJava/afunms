/*
 * Created on 2005-4-14
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.IndicatorsTopoRelation;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SysUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.model.IpaddressPanel;
import com.afunms.event.model.CheckEvent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.topology.model.EquipImage;
import com.afunms.topology.model.HintNode;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.model.Relation;
import com.afunms.topology.util.PanelXmlOperator;
import com.afunms.util.DataGate;

@SuppressWarnings("unchecked")
public class UpdateXmlTask extends MonitorTask {
	private Logger logger = Logger.getLogger(this.getClass());

	public UpdateXmlTask() {
		super();
	}

	public void run() {
		logger.info(" 开始更新XML ");
		Date startdate1 = new Date();
		List panellist = new ArrayList();
		List list = new ArrayList();
		List trlist = new ArrayList();
		List ailist = new ArrayList();
		List hintlist = new ArrayList();
		List managexmllist = new ArrayList();
		List srlist = new ArrayList();
		List eventlist = new ArrayList();
		List imglist = new ArrayList();
		Hashtable nodedependhash = new Hashtable();
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			rs = stmt.executeQuery("select * from system_ipaddresspanel order by id");
			while (rs.next()) {
				panellist.add(loadFromRS1(rs));
			}
			rs = stmt.executeQuery("select * from nms_node_depend order by id");
			while (rs.next()) {
				list.add(loadFromRS2(rs));
			}
			rs = stmt.executeQuery("select * from nms_indicators_topo_relation order by id");
			while (rs.next()) {
				trlist.add(loadFromRS3(rs));
			}
			rs = stmt.executeQuery("select * from nms_alarm_indicators_node order by id");
			while (rs.next()) {
				ailist.add(loadFromRS4(rs));
			}
			rs = stmt.executeQuery("select * from nms_hint_node order by id");
			while (rs.next()) {
				hintlist.add(loadFromRS5(rs));
			}
			rs = stmt.executeQuery("select * from topo_manage_xml");
			while (rs.next()) {
				managexmllist.add(loadFromRS6(rs));
			}
			rs = stmt.executeQuery("select * from node_submap_relation order by id");
			while (rs.next()) {
				srlist.add(loadFromRS7(rs));
			}
			rs = stmt.executeQuery("select * from nms_checkevent");
			while (rs.next()) {
				eventlist.add(loadFromRS8(rs));
			}
			rs = stmt.executeQuery("select * from topo_equip_pic order by id");
			while (rs.next()) {
				imglist.add(loadFromRS9(rs));
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
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
		int numThreads = panellist.size();
		if (panellist != null && panellist.size() > 0) {
			ThreadPool threadPool = new ThreadPool(numThreads);
			for (int i = 0; i < panellist.size(); i++) {
				threadPool.runTask(createUpdatePanelTask((IpaddressPanel) panellist.get(i)));
			}
			threadPool.join();
		}
		try {
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					NodeDepend vo = (NodeDepend) list.get(i);
					if (nodedependhash.containsKey(vo.getXmlfile())) {
						((List) nodedependhash.get(vo.getXmlfile())).add(vo);
					} else {
						List templist = new ArrayList();
						templist.add(vo);
						nodedependhash.put(vo.getXmlfile(), templist);
					}

				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		ShareData.setAllnodedepend(nodedependhash);

		Hashtable tophash = new Hashtable();
		try {
			if (trlist != null && trlist.size() > 0) {
				for (int i = 0; i < trlist.size(); i++) {
					IndicatorsTopoRelation relation = (IndicatorsTopoRelation) trlist.get(i);
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
		}
		ShareData.setToprelation(tophash);

		Hashtable alarmnodehash = new Hashtable();
		try {
			if (ailist != null && ailist.size() > 0) {
				for (int i = 0; i < ailist.size(); i++) {
					AlarmIndicatorsNode alarmindi = (AlarmIndicatorsNode) ailist.get(i);
					alarmnodehash.put(alarmindi.getId() + ":" + alarmindi.getNodeid(), alarmindi);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareData.setAllalarmindicators(alarmnodehash);

		Hashtable hinthash = new Hashtable();
		try {
			if (hintlist != null && hintlist.size() > 0) {
				for (int i = 0; i < hintlist.size(); i++) {
					HintNode hintnode = (HintNode) hintlist.get(i);
					hinthash.put(hintnode.getNodeId() + ":" + hintnode.getXmlfile(), hintnode);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareData.setAllhintlinks(hinthash);

		Hashtable managexmlhash = new Hashtable();
		Hashtable managexmlhashtable = new Hashtable();
		try {
			if (managexmllist != null && managexmllist.size() > 0) {
				for (int i = 0; i < managexmllist.size(); i++) {
					ManageXml vo = (ManageXml) managexmllist.get(i);
					managexmlhash.put(vo.getXmlName(), vo);
					managexmlhashtable.put(vo.getId(), vo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareData.setManagexmlhashtable(managexmlhashtable);
		ShareData.setManagexmlhash(managexmlhash);

		Hashtable relationhash = new Hashtable();
		Hashtable relationhashtable = new Hashtable();
		try {
			if (srlist != null && srlist.size() > 0) {
				for (int i = 0; i < srlist.size(); i++) {
					Relation relation = (Relation) srlist.get(i);
					if (relationhash.containsKey(relation.getMapId() + "")) {
						((List) relationhash.get(relation.getMapId() + "")).add(relation);
					} else {
						List tlist = new ArrayList();
						tlist.add(relation);
						relationhash.put(relation.getMapId() + "", tlist);
					}
					relationhashtable.put(relation.getNodeId() + ":" + relation.getXmlName(), relation.getMapId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareData.setRelationhashtable(relationhashtable);
		ShareData.setRelationhash(relationhash);

		Hashtable checkEventHash = new Hashtable();
		try {
			if (eventlist != null && eventlist.size() > 0) {
				for (int i = 0; i < eventlist.size(); i++) {
					CheckEvent checkEvent = (CheckEvent) eventlist.get(i);
					String name = checkEvent.getNodeid() + ":" + checkEvent.getType() + ":" + checkEvent.getSubtype() + ":" + checkEvent.getIndicatorsName();
					if (checkEvent.getSindex() != null && checkEvent.getSindex().trim().length() > 0) {
						name = name + ":" + checkEvent.getSindex();
					}
					checkEventHash.put(name, checkEvent.getAlarmlevel());
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		ShareData.setCheckEventHash(checkEventHash);

		HashMap EquipMap = new HashMap();
		try {
			if (imglist != null && imglist.size() > 0) {
				for (int i = 0; i < imglist.size(); i++) {
					EquipImage equipImage = (EquipImage) imglist.get(i);
					EquipMap.put(equipImage.getId(), equipImage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ShareData.setAllequpimgs(EquipMap);
		Date enddate1 = new Date();
		logger.info(" UpdateXmlTask执行时间 " + (enddate1.getTime() - startdate1.getTime()) / 1000 + "秒 ");
	}

	/**
	 * 创建轮询面板的任务
	 */
	private Runnable createUpdatePanelTask(final IpaddressPanel ippanel) {
		return new Runnable() {
			public void run() {
				try {
					String ipaddress = ippanel.getIpaddress();
					Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
					if (host == null)
						return;
					// 过滤只通过PING采集数据的设备
					if (host.getCollecttype() == SystemConstant.COLLECTTYPE_PING || host.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT
							|| host.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT || "as400".equals(host.getSysOid()))
						return;
					String oid = host.getSysOid();
					oid = oid.replaceAll("\\.", "-");
					PanelXmlOperator panelxmlOpr = new PanelXmlOperator();
					String filename = SysUtil.doip(host.getIpAddress()) + ".jsp";
					panelxmlOpr.setFile(filename, 2);
					panelxmlOpr.setOid(oid);
					panelxmlOpr.setImageType(ippanel.getImageType());
					panelxmlOpr.setIpaddress(host.getIpAddress());
					// 写XML
					panelxmlOpr.init4createXml();
					panelxmlOpr.createXml(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}

	private BaseVo loadFromRS1(ResultSet rs) {
		IpaddressPanel vo = new IpaddressPanel();
		try {
			vo.setId(rs.getInt("id"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setStatus(rs.getString("status"));
			vo.setImageType(rs.getString("imageType"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	private BaseVo loadFromRS2(ResultSet rs) {
		NodeDepend vo = new NodeDepend();
		try {
			vo.setId(rs.getInt("id"));
			vo.setNodeId(rs.getString("node_id"));
			vo.setXmlfile(rs.getString("xml"));
			vo.setLocation(rs.getString("location"));
			vo.setAlias(rs.getString("alias"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	private BaseVo loadFromRS3(ResultSet rs) {
		IndicatorsTopoRelation relation = new IndicatorsTopoRelation();
		try {
			relation.setIndicatorsId(rs.getString("indicators_id"));
			relation.setSIndex(rs.getString("sindex"));
			relation.setTopoId(rs.getString("topo_id"));
			relation.setNodeid(rs.getString("nodeid"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return relation;
	}

	private BaseVo loadFromRS4(ResultSet rs) {
		AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
		try {
			alarmIndicatorsNode.setId(rs.getInt("id"));
			alarmIndicatorsNode.setNodeid(rs.getString("nodeid"));
			alarmIndicatorsNode.setSubentity(rs.getString("subentity"));
			alarmIndicatorsNode.setName(rs.getString("name"));
			alarmIndicatorsNode.setType(rs.getString("type"));
			alarmIndicatorsNode.setSubtype(rs.getString("subtype"));
			alarmIndicatorsNode.setDatatype(rs.getString("datatype"));
			alarmIndicatorsNode.setMoid(rs.getString("moid"));
			alarmIndicatorsNode.setThreshlod(rs.getInt("threshold"));
			alarmIndicatorsNode.setThreshlod_unit(rs.getString("threshold_unit"));
			alarmIndicatorsNode.setCompare(rs.getInt("compare"));
			alarmIndicatorsNode.setCompare_type(rs.getInt("compare_type"));
			alarmIndicatorsNode.setAlarm_times(rs.getString("alarm_times"));
			alarmIndicatorsNode.setAlarm_info(rs.getString("alarm_info"));
			alarmIndicatorsNode.setAlarm_level(rs.getString("alarm_level"));
			alarmIndicatorsNode.setEnabled(rs.getString("enabled"));
			alarmIndicatorsNode.setPoll_interval(rs.getString("poll_interval"));
			alarmIndicatorsNode.setInterval_unit(rs.getString("interval_unit"));
			alarmIndicatorsNode.setLimenvalue0(rs.getString("limenvalue0"));
			alarmIndicatorsNode.setLimenvalue1(rs.getString("limenvalue1"));
			alarmIndicatorsNode.setLimenvalue2(rs.getString("limenvalue2"));
			alarmIndicatorsNode.setTime0(rs.getString("time0"));
			alarmIndicatorsNode.setTime1(rs.getString("time1"));
			alarmIndicatorsNode.setTime2(rs.getString("time2"));
			alarmIndicatorsNode.setSms0(rs.getString("sms0"));
			alarmIndicatorsNode.setSms1(rs.getString("sms1"));
			alarmIndicatorsNode.setSms2(rs.getString("sms2"));
			alarmIndicatorsNode.setWay0(rs.getString("way0"));
			alarmIndicatorsNode.setWay1(rs.getString("way1"));
			alarmIndicatorsNode.setWay2(rs.getString("way2"));
			alarmIndicatorsNode.setCategory(rs.getString("category"));
			alarmIndicatorsNode.setDescr(rs.getString("descr"));
			alarmIndicatorsNode.setUnit(rs.getString("unit"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alarmIndicatorsNode;
	}

	private BaseVo loadFromRS5(ResultSet rs) {
		HintNode vo = new HintNode();
		try {
			vo.setId(rs.getInt("id"));
			vo.setNodeId(rs.getString("node_id"));
			vo.setXmlfile(rs.getString("xml_file"));
			vo.setType(rs.getString("node_type"));
			vo.setImage(rs.getString("image"));
			vo.setName(rs.getString("name"));
			vo.setAlias(rs.getString("alias"));
		} catch (Exception e) {
			e.printStackTrace();
			SysLogger.error("HintNodeDao.loadFromRS()", e);
		}
		return vo;
	}

	private BaseVo loadFromRS6(ResultSet rs) {
		ManageXml vo = new ManageXml();
		try {
			vo.setId(rs.getInt("id"));
			vo.setXmlName(rs.getString("xml_name"));
			vo.setTopoName(rs.getString("topo_name"));
			vo.setAliasName(rs.getString("alias_name"));
			vo.setTopoTitle(rs.getString("topo_title"));
			vo.setTopoArea(rs.getString("topo_area"));
			vo.setTopoBg(rs.getString("topo_bg"));
			vo.setTopoType(rs.getInt("topo_type"));
			vo.setBid(rs.getString("bid"));
			vo.setHome_view(rs.getInt("home_view"));
			vo.setBus_home_view(rs.getInt("bus_home_view"));
			vo.setPercent(rs.getFloat("zoom_percent"));
			vo.setUtilhdx(rs.getString("max_utilhdx"));
			vo.setUtilhdxperc(rs.getString("max_utilhdxperc"));
			vo.setSupperid(rs.getString("supperid"));
			vo.setFatherid(rs.getString("fatherid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	private BaseVo loadFromRS7(ResultSet rs) {
		Relation vo = new Relation();
		try {
			vo.setId(rs.getInt("id"));
			vo.setXmlName(rs.getString("xml_name"));
			vo.setNodeId(rs.getString("node_id"));
			vo.setCategory(rs.getString("category"));
			vo.setMapId(rs.getString("map_id"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	private BaseVo loadFromRS8(ResultSet rs) {
		CheckEvent vo = new CheckEvent();
		try {
			vo.setNodeid(rs.getString("nodeid"));
			vo.setType(rs.getString("type"));
			vo.setSubtype(rs.getString("subtype"));
			vo.setContent(rs.getString("content"));
			vo.setIndicatorsName(rs.getString("indicators_name"));
			vo.setSindex(rs.getString("sindex"));
			vo.setAlarmlevel(rs.getInt("alarmlevel"));
			vo.setCollecttime(rs.getString("collecttime"));
			vo.setThevalue(rs.getString("thevalue"));
			vo.setBid(rs.getString("bid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	private BaseVo loadFromRS9(ResultSet rs) {
		EquipImage vo = new EquipImage();
		try {
			vo.setId(rs.getInt("id"));
			vo.setCategory(rs.getInt("category"));
			vo.setCnName(rs.getString("cn_name"));
			vo.setEnName(rs.getString("en_name"));
			vo.setTopoImage(rs.getString("topo_image"));
			vo.setLostImage(rs.getString("lost_image"));
			vo.setAlarmImage(rs.getString("alarm_image"));
			vo.setPath(rs.getString("path"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

}
