package com.afunms.application.ajaxManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.alarm.dao.AlarmIndicatorsDao;
import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.dao.AlarmWayDao;
import com.afunms.alarm.model.AlarmIndicators;
import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.model.AlarmWay;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.discovery.RepairLink;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.IfEntity;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.dao.RepairLinkDao;
import com.afunms.topology.model.Link;
import com.afunms.topology.util.XmlOperator;

public class ThresHoldAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
	private void getNode() {
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		NodeDTO nodeDTO = null;

		List<NodeDTO> allNodeDTOlist = nodeGatherIndicatorsUtil.getNodeListByTypeAndSubtype(Constant.ALL_TYPE, Constant.ALL_SUBTYPE);
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		for (int i = 0; i < allNodeDTOlist.size(); i++) {
			nodeDTO = allNodeDTOlist.get(i);
			jsonString.append("{\"nodeId\":\"");
			jsonString.append(nodeDTO.getId());
			jsonString.append("\",");

			jsonString.append("\"subType\":\"");
			jsonString.append(nodeDTO.getSubtype());
			jsonString.append("\",");

			jsonString.append("\"alias\":\"");
			jsonString.append(nodeDTO.getName());
			jsonString.append("\"}");

			if (i != allNodeDTOlist.size() - 1) {
				jsonString.append(",");
			}

		}
		jsonString.append("],total:" + allNodeDTOlist.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getThresHoldList() {
		String type = getParaValue("type");
		String subtype = null;
		try {
			subtype = new String(getParaValue("subType").getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (subtype.equals("思科")) {
			subtype = "cisco";
		} else if (subtype.equals("华三")) {
			subtype = "h3c";
		}
		String nodeid = getParaValue("nodeId");
		if (type == null || "".equals(type)) {
			type = Constant.ALL_TYPE;
		}
		if (subtype == null || "".equals(subtype)) {
			subtype = Constant.ALL_SUBTYPE;
		}
		if (nodeid == null || "".equals(nodeid)) {
			nodeid = "-1";
		}

		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		String where = " where 1=1 ";

		if (!"-1".equalsIgnoreCase(type)) {
			where = where + " and type = '" + type + "'";
		}
		if (!"-1".equalsIgnoreCase(subtype)) {
			where = where + " and subtype = '" + subtype + "'";
		}
		if (!"-1".equalsIgnoreCase(nodeid)) {
			where = where + " and nodeid = '" + nodeid + "'";
		}
		List thresHoldList = new ArrayList();
		List<NodeDTO> allNodeDTOlist = new ArrayList<NodeDTO>();
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		try {
			thresHoldList = alarmIndicatorsNodeDao.findByCondition(where);
			allNodeDTOlist = nodeGatherIndicatorsUtil.getNodeListByTypeAndSubtype(Constant.ALL_TYPE, Constant.ALL_SUBTYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		AlarmIndicatorsNode thresHold = null;

		Hashtable<String, NodeDTO> nodeHt = new Hashtable<String, NodeDTO>();

		for (int i = 0; i < allNodeDTOlist.size(); i++) {
			nodeHt.put(allNodeDTOlist.get(i).getNodeid(), allNodeDTOlist.get(i));
		}

		Hashtable<Integer, String> compareTypeHt = new Hashtable<Integer, String>();
		compareTypeHt.put(0, "降序");
		compareTypeHt.put(1, "升序");
		compareTypeHt.put(2, "相等");

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		for (int i = 0; i < thresHoldList.size(); i++) {
			thresHold = (AlarmIndicatorsNode) thresHoldList.get(i);
			jsonString.append("{\"thresHoldId\":\"");
			jsonString.append(thresHold.getId());
			jsonString.append("\",");

			jsonString.append("\"alias\":\"");
			if (null != nodeHt.get(thresHold.getNodeid())) {
				jsonString.append(nodeHt.get(thresHold.getNodeid()).getName());
			} else {
				jsonString.append("null");
			}
			jsonString.append("\",");

			jsonString.append("\"ip\":\"");
			if (null != nodeHt.get(thresHold.getNodeid())) {
				jsonString.append(nodeHt.get(thresHold.getNodeid()).getIpaddress());
			} else {
				jsonString.append("null");
			}
			jsonString.append("\",");

			jsonString.append("\"type\":\"");
			jsonString.append(thresHold.getType());
			jsonString.append("\",");

			jsonString.append("\"subType\":\"");
			jsonString.append(thresHold.getSubtype());
			jsonString.append("\",");

			jsonString.append("\"thresHoldName\":\"");
			jsonString.append(thresHold.getName());
			jsonString.append("\",");

			jsonString.append("\"thresHoldDataType\":\"");
			jsonString.append(thresHold.getDatatype());
			jsonString.append("\",");

			jsonString.append("\"thresHoldUnit\":\"");
			jsonString.append(thresHold.getThreshlod_unit());
			jsonString.append("\",");

			jsonString.append("\"thresHoldCompareType\":\"");
			jsonString.append(compareTypeHt.get(thresHold.getCompare()));
			jsonString.append("\",");

			jsonString.append("\"thresHoldIsE\":\"");
			jsonString.append(thresHold.getEnabled());
			jsonString.append("\",");

			jsonString.append("\"firstLevelValue\":\"");
			jsonString.append(thresHold.getLimenvalue0());
			jsonString.append("\",");

			jsonString.append("\"firstLevelTimes\":\"");
			jsonString.append(thresHold.getTime0());
			jsonString.append("\",");

			jsonString.append("\"firstIsE\":\"");
			jsonString.append(thresHold.getSms0());
			jsonString.append("\",");

			jsonString.append("\"secondLevelValue\":\"");
			jsonString.append(thresHold.getLimenvalue1());
			jsonString.append("\",");

			jsonString.append("\"secondLevelTimes\":\"");
			jsonString.append(thresHold.getTime1());
			jsonString.append("\",");

			jsonString.append("\"secondIsE\":\"");
			jsonString.append(thresHold.getSms1());
			jsonString.append("\",");

			jsonString.append("\"thirdLevelValue\":\"");
			jsonString.append(thresHold.getLimenvalue2());
			jsonString.append("\",");

			jsonString.append("\"thirdLevelTimes\":\"");
			jsonString.append(thresHold.getTime2());
			jsonString.append("\",");

			jsonString.append("\"thirdIsE\":\"");
			jsonString.append(thresHold.getSms2());
			jsonString.append("\",");

			jsonString.append("\"remark\":\"");
			jsonString.append(thresHold.getDescr());
			jsonString.append("\"}");

			if (i != thresHoldList.size() - 1) {
				jsonString.append(",");
			}

		}
		jsonString.append("],total:" + thresHoldList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteThresHolds() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.delete(ids);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}
		out.print("成功删除");
		out.flush();
	}

	private void beforeEditThresHold() {
		String thresHoldId = getParaValue("thresHoldId");

		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		AlarmIndicatorsNode nodeThresHold = null;

		String firstAlarmWayName = "";
		String secondAlarmWayName = "";
		String thirdAlarmWayName = "";
		AlarmWayDao alarmWayDao = new AlarmWayDao();
		AlarmWay alarmWay = null;
		try {
			nodeThresHold = (AlarmIndicatorsNode) alarmIndicatorsNodeDao.findByID(thresHoldId);
			try {
				String[] alarmWayIdSArray = null;

				String alarmWayIdString = nodeThresHold.getWay0();
				if (null != alarmWayIdString) {
					alarmWayIdSArray = alarmWayIdString.split(",");
					if (null != alarmWayIdSArray && alarmWayIdSArray.length > 0) {
						for (int i = 0; i < alarmWayIdSArray.length; i++) {
							alarmWay = (AlarmWay) alarmWayDao.findByID(alarmWayIdSArray[i]);
							if(null==alarmWay)continue;
							firstAlarmWayName = firstAlarmWayName + alarmWay.getName() + "";
						}
					}
				}

				alarmWayIdString = nodeThresHold.getWay1();
				if (null != alarmWayIdString) {
					alarmWayIdSArray = alarmWayIdString.split(",");
					if (null != alarmWayIdSArray && alarmWayIdSArray.length > 0) {
						for (int i = 0; i < alarmWayIdSArray.length; i++) {
							alarmWay = (AlarmWay) alarmWayDao.findByID(alarmWayIdSArray[i]);
							if(null==alarmWay)continue;
							secondAlarmWayName = secondAlarmWayName + alarmWay.getName() + "";
						}
					}
				}

				alarmWayIdString = nodeThresHold.getWay2();
				if (null != alarmWayIdString) {
					alarmWayIdSArray = alarmWayIdString.split(",");
					if (null != alarmWayIdSArray && alarmWayIdSArray.length > 0) {
						for (int i = 0; i < alarmWayIdSArray.length; i++) {
							alarmWay = (AlarmWay) alarmWayDao.findByID(alarmWayIdSArray[i]);
							if(null==alarmWay)continue;
							thirdAlarmWayName = thirdAlarmWayName + alarmWay.getName() + "";
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				alarmWayDao.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"thresHoldId\":\"");
		jsonString.append(nodeThresHold.getId());
		jsonString.append("\",");

		jsonString.append("\"nodeId\":\"");
		jsonString.append(nodeThresHold.getNodeid());
		jsonString.append("\",");

		jsonString.append("\"type\":\"");
		jsonString.append(nodeThresHold.getType());
		jsonString.append("\",");

		jsonString.append("\"subType\":\"");
		jsonString.append(nodeThresHold.getSubtype());
		jsonString.append("\",");

		jsonString.append("\"thresHoldName\":\"");
		jsonString.append(nodeThresHold.getName());
		jsonString.append("\",");

		jsonString.append("\"thresHoldIsE\":\"");
		jsonString.append(nodeThresHold.getEnabled());
		jsonString.append("\",");

		jsonString.append("\"remark\":\"");
		jsonString.append(nodeThresHold.getDescr());
		jsonString.append("\",");

		jsonString.append("\"alarmInfo\":\"");
		jsonString.append(nodeThresHold.getAlarm_info());
		jsonString.append("\",");

		jsonString.append("\"thresHoldCompare\":\"");
		jsonString.append(nodeThresHold.getCompare());
		jsonString.append("\",");

		jsonString.append("\"thresHoldDataType\":\"");
		jsonString.append(nodeThresHold.getDatatype());
		jsonString.append("\",");

		jsonString.append("\"thresHoldUnit\":\"");
		jsonString.append(nodeThresHold.getThreshlod_unit());
		jsonString.append("\",");

		// 一级
		jsonString.append("\"firstLevelValue\":\"");
		jsonString.append(nodeThresHold.getLimenvalue0());
		jsonString.append("\",");

		jsonString.append("\"firstLevelTimes\":\"");
		jsonString.append(nodeThresHold.getTime0());
		jsonString.append("\",");

		jsonString.append("\"firstIsE\":\"");
		jsonString.append(nodeThresHold.getSms0());
		jsonString.append("\",");

		jsonString.append("\"firstAlarmWayName\":\"");
		jsonString.append(firstAlarmWayName);
		jsonString.append("\",");

		jsonString.append("\"firstAlarmWay\":\"");
		jsonString.append(nodeThresHold.getWay0());
		jsonString.append("\",");

		// 二级
		jsonString.append("\"secondLevelValue\":\"");
		jsonString.append(nodeThresHold.getLimenvalue1());
		jsonString.append("\",");

		jsonString.append("\"secondLevelTimes\":\"");
		jsonString.append(nodeThresHold.getTime1());
		jsonString.append("\",");

		jsonString.append("\"secondIsE\":\"");
		jsonString.append(nodeThresHold.getSms1());
		jsonString.append("\",");

		jsonString.append("\"secondAlarmWayName\":\"");
		jsonString.append(secondAlarmWayName);
		jsonString.append("\",");

		jsonString.append("\"secondAlarmWay\":\"");
		jsonString.append(nodeThresHold.getWay1());
		jsonString.append("\",");

		// 三级
		jsonString.append("\"thirdLevelValue\":\"");
		jsonString.append(nodeThresHold.getLimenvalue2());
		jsonString.append("\",");

		jsonString.append("\"thirdLevelTimes\":\"");
		jsonString.append(nodeThresHold.getTime2());
		jsonString.append("\",");

		jsonString.append("\"thirdIsE\":\"");
		jsonString.append(nodeThresHold.getSms2());
		jsonString.append("\",");

		jsonString.append("\"thirdAlarmWayName\":\"");
		jsonString.append(thirdAlarmWayName);
		jsonString.append("\",");

		jsonString.append("\"thirdAlarmWay\":\"");
		jsonString.append(nodeThresHold.getWay2());
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void editThresHold() {
		AlarmIndicatorsNode alarmIndicatorsNode = new AlarmIndicatorsNode();
		alarmIndicatorsNode.setId(getParaIntValue("thresHoldId"));
		alarmIndicatorsNode.setName(getParaValue("thresHoldName"));
		alarmIndicatorsNode.setType(getParaValue("type"));
		alarmIndicatorsNode.setSubtype(getParaValue("subType"));
		alarmIndicatorsNode.setDatatype(getParaValue("thresHoldDataType"));
		alarmIndicatorsNode.setNodeid(getParaValue("nodeId"));
		alarmIndicatorsNode.setThreshlod_unit(getParaValue("thresHoldUnit"));
		alarmIndicatorsNode.setCompare(getParaIntValue("thresHoldCompare"));
		alarmIndicatorsNode.setCompare_type(getParaIntValue("thresHoldCompare"));
		alarmIndicatorsNode.setAlarm_info(getParaValue("alarmInfo"));
		alarmIndicatorsNode.setEnabled(getParaValue("thresHoldIsE"));

		alarmIndicatorsNode.setLimenvalue0(getParaValue("firstLevelValue"));
		alarmIndicatorsNode.setLimenvalue1(getParaValue("secondLevelValue"));
		alarmIndicatorsNode.setLimenvalue2(getParaValue("thirdLevelValue"));

		alarmIndicatorsNode.setTime0(getParaValue("firstLevelTimes"));
		alarmIndicatorsNode.setTime1(getParaValue("secondLevelTimes"));
		alarmIndicatorsNode.setTime2(getParaValue("thirdLevelTimes"));

		alarmIndicatorsNode.setSms0(getParaValue("firstIsE"));
		alarmIndicatorsNode.setSms1(getParaValue("secondIsE"));
		alarmIndicatorsNode.setSms2(getParaValue("thirdIsE"));

		alarmIndicatorsNode.setWay0(getParaValue("firstAlarmWay"));
		alarmIndicatorsNode.setWay1(getParaValue("secondAlarmWay"));
		alarmIndicatorsNode.setWay2(getParaValue("thirdAlarmWay"));

		alarmIndicatorsNode.setDescr(getParaValue("remark"));

		AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
		try {
			alarmIndicatorsNodeDao.update(alarmIndicatorsNode);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			alarmIndicatorsNodeDao.close();
		}

		StringBuffer jsonString = new StringBuffer("修改成功");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getCanAddThresHoldsAndNodes() {
		String type = getParaValue("type");
		String subtype = getParaValue("subType");

		List thresHoldList = new ArrayList();
		List<NodeDTO> nodeDTOlist = new ArrayList<NodeDTO>();
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		try {
			thresHoldList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(type, subtype);
			nodeDTOlist = nodeGatherIndicatorsUtil.getNodeListByTypeAndSubtype(type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		}

		AlarmIndicators thresHold = null;
		NodeDTO node = null;

		StringBuffer jsonString = new StringBuffer("{ThresHoldRows:[");
		for (int i = 0; i < thresHoldList.size(); i++) {
			thresHold = (AlarmIndicators) thresHoldList.get(i);
			jsonString.append("{\"thresHoldName\":\"");
			jsonString.append(thresHold.getName());
			jsonString.append("\",");

			jsonString.append("\"thresHoldId\":\"");
			jsonString.append(thresHold.getId());
			jsonString.append("\",");

			jsonString.append("\"type\":\"");
			jsonString.append(thresHold.getType());
			jsonString.append("\",");

			jsonString.append("\"subType\":\"");
			jsonString.append(thresHold.getSubtype());
			jsonString.append("\",");

			jsonString.append("\"remark\":\"");
			jsonString.append(thresHold.getDescr());
			jsonString.append("\"}");

			if (i != thresHoldList.size() - 1) {
				jsonString.append(",");
			}

		}
		jsonString.append("],");
		jsonString.append("NodeRows:[");
		for (int i = 0; i < nodeDTOlist.size(); i++) {
			node = nodeDTOlist.get(i);
			jsonString.append("{\"nodeId\":\"");
			jsonString.append(node.getId());
			jsonString.append("\",");

			jsonString.append("\"nodeIp\":\"");
			jsonString.append(node.getIpaddress());
			jsonString.append("\",");

			jsonString.append("\"type\":\"");
			jsonString.append(node.getType());
			jsonString.append("\",");

			jsonString.append("\"subType\":\"");
			jsonString.append(node.getSubtype());
			jsonString.append("\",");

			jsonString.append("\"nodeAlias\":\"");
			jsonString.append(node.getName());
			jsonString.append("\",");

			jsonString.append("\"nodeBid\":\"");
			jsonString.append(node.getBusinessId());
			jsonString.append("\",");

			jsonString.append("\"nodeBSname\":\"");
			jsonString.append(node.getBusinessName());
			jsonString.append("\"}");

			if (i != nodeDTOlist.size() - 1) {
				jsonString.append(",");
			}
		}
		jsonString.append("]");
		jsonString.append("}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void saveAddThresHoldsAndNodes() {
		// 指标基础对象ids
		String toAddThresHoldsIds = getParaValue("toAddThresHoldsIds");
		// 需要应用指标的网元ids
		String toAddNodesValues = getParaValue("toAddNodesValues");
		// 网元对象类型
		String type = getParaValue("type");
		// 网元对象子类型
		String subType = getParaValue("subType");

		String[] thresHoldIdArray = null;
		String[] nodeIdArray = null;
		StringBuffer jsonString = null;
		if ("".equals(toAddThresHoldsIds) || "".equals(toAddNodesValues) || "".equals(type) || "".equals(subType)) {
			jsonString = new StringBuffer("操作失败");
		} else {
			thresHoldIdArray = toAddThresHoldsIds.split(";");
			nodeIdArray = toAddNodesValues.split(";");

			// 所有的网元
			List<NodeDTO> allNodeDTOlist = new ArrayList<NodeDTO>();
			// 需要应用指标的网元
			List<NodeDTO> needNodeDTOList = new ArrayList<NodeDTO>();

			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			try {
				allNodeDTOlist = alarmIndicatorsUtil.getNodeListByTypeAndSubtype(Constant.ALL_TYPE, Constant.ALL_SUBTYPE);
				if (null != nodeIdArray && nodeIdArray.length > 0) {
					NodeDTO nodeDTO = null;
					for (int i = 0; i < nodeIdArray.length; i++) {
						// 过滤空值
						if ("".equals(nodeIdArray[i])) {
							continue;
						} else {
							for (int j = 0; j < allNodeDTOlist.size(); j++) {
								nodeDTO = allNodeDTOlist.get(j);
								if (nodeDTO.getNodeid().equals(nodeIdArray[i]) && nodeDTO.getType().equals(type) && nodeDTO.getSubtype().equals(subType)) {
									needNodeDTOList.add(nodeDTO);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<AlarmIndicators> toAddThresHoldsList = new ArrayList<AlarmIndicators>();

			List<AlarmIndicatorsNode> saveList = new ArrayList<AlarmIndicatorsNode>();
			List<AlarmIndicatorsNode> updateList = new ArrayList<AlarmIndicatorsNode>();

			AlarmIndicators thresHold = null;

			if (null != thresHoldIdArray && thresHoldIdArray.length > 0) {
				AlarmIndicatorsDao gatherIndicatorsDao = new AlarmIndicatorsDao();
				try {
					for (int i = 0; i < thresHoldIdArray.length; i++) {
						// 过滤空值
						if ("".equals(thresHoldIdArray[i].trim())) {
							continue;
						} else {
							thresHold = (AlarmIndicators) gatherIndicatorsDao.findByID(thresHoldIdArray[i].trim());
							toAddThresHoldsList.add(thresHold);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					gatherIndicatorsDao.close();
				}
			}

			if (null != needNodeDTOList && needNodeDTOList.size() > 0) {
				AlarmIndicatorsNodeDao nodeThresHoldDao = new AlarmIndicatorsNodeDao();
				String nodeId = null;
				String where = " ";
				// 已经存在的网元指标
				List existNodeThresHoldsList = new ArrayList();
				AlarmIndicatorsNode tempNodeThresHold = null;
				for (int i = 0; i < needNodeDTOList.size(); i++) {
					nodeId = needNodeDTOList.get(i).getNodeid();
					for (int j = 0; j < toAddThresHoldsList.size(); j++) {
						thresHold = toAddThresHoldsList.get(j);

						// 由这个条件得出来的对象应该只有一个
						where = " where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subType + "' and name='" + thresHold.getName() + "'";
						try {
							// 查询是否存在同类指标
							nodeThresHoldDao = new AlarmIndicatorsNodeDao();
							existNodeThresHoldsList = nodeThresHoldDao.findByCondition(where);
							if (null != existNodeThresHoldsList && existNodeThresHoldsList.size() > 0) {
								updateList.add((AlarmIndicatorsNode) existNodeThresHoldsList.get(0));
							} else {
								tempNodeThresHold = alarmIndicatorsUtil.createAlarmIndicatorsNodeByAlarmIndicators(thresHold);
								tempNodeThresHold.setNodeid(nodeId);
								saveList.add(tempNodeThresHold);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				// 保存数据
				if (updateList != null && updateList.size() > 0) {
					try {
						nodeThresHoldDao = new AlarmIndicatorsNodeDao();
						nodeThresHoldDao.update(updateList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (saveList != null && saveList.size() > 0) {
					try {
						nodeThresHoldDao = new AlarmIndicatorsNodeDao();
						nodeThresHoldDao.saveBatch(saveList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			jsonString = new StringBuffer("操作成功");
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private void getLinkThresHoldList() {
		LinkDao dao = new LinkDao();
		List linkList = new ArrayList();
		try {
			linkList = (List) dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (linkList != null && linkList.size() > 0) {
			Link link = null;
			for (int i = 0; i < linkList.size(); i++) {
				link = (Link) linkList.get(i);
				String startName = link.getStartAlias();
				String endName = link.getEndAlias();

				jsonString.append("{\"linkId\":\"");
				jsonString.append(link.getId());
				jsonString.append("\",");

				jsonString.append("\"startAlias\":\"");
				jsonString.append(startName);
				jsonString.append("\",");

				jsonString.append("\"startPort\":\"");
				jsonString.append("IP地址:" + link.getStartIp() + " 索引:" + link.getStartIndex());
				jsonString.append("\",");

				jsonString.append("\"endAlias\":\"");
				jsonString.append(endName);
				jsonString.append("\",");

				jsonString.append("\"endPort\":\"");
				jsonString.append("IP地址:" + link.getEndIp() + " 索引:" + link.getEndIndex());
				jsonString.append("\",");

				jsonString.append("\"maxSpeed\":\"");
				jsonString.append(link.getMaxSpeed());
				jsonString.append("\",");

				jsonString.append("\"maxPer\":\"");
				jsonString.append(link.getMaxPer());
				jsonString.append("\"}");

				if (i != linkList.size() - 1) {
					jsonString.append(",");
				}
			}
		}
		jsonString.append("],total: " + linkList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	// by lyl
	private void linkEditAll() {
		String linkids = getParaValue("linkIdString");
		String maxSpeed = getParaValue("maxSpeed");
		String maxPer = getParaValue("maxPer");
		String[] ids = linkids.split(";");
		LinkDao dao = new LinkDao();
		boolean flagAll = true;
		boolean flag = true;
		try {
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];

				dao = new LinkDao();
				Link vo = (Link) dao.findByID(id);
				vo.setMaxSpeed(maxSpeed);
				vo.setMaxPer(maxPer);
				flag = dao.update(vo);
				if (!flag) {
					flagAll = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		String message = "修改成功";
		if (!flagAll) {
			message = "修改失败";
		}
		StringBuffer jsonString = new StringBuffer(message);
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteLinkThresHolds() {
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		LinkDao dao = new LinkDao();
		try {
			dao.delete(ids);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		out.print("成功删除");
		out.flush();
	}

	private void beforeEditLink() {
		String id = getParaValue("linkId");
		String startIndex = null;
		String endIndex = null;

		LinkDao linkDao = new LinkDao();
		Link link = null;
		try {
			link = (Link) linkDao.findByID(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != linkDao) {
				linkDao.close();
			}
		}

		if (null != link) {
			Host startHost = (Host) PollingEngine.getInstance().getNodeByID(link.getStartId());
			Host endHost = (Host) PollingEngine.getInstance().getNodeByID(link.getEndId());

			List<IfEntity> startHostIfentityList = getSortListByHash(startHost.getInterfaceHash());
			List<IfEntity> endHostIfentityList = getSortListByHash(endHost.getInterfaceHash());
			for (IfEntity ifObj : startHostIfentityList) {
				if (ifObj.getIndex().equals(link.getStartIndex())) {
					startIndex = ifObj.getIndex() + "(" + ifObj.getDescr() + ")";
				}
			}

			for (IfEntity ifObj : endHostIfentityList) {
				if (ifObj.getIndex().equals(link.getEndIndex())) {
					endIndex = ifObj.getIndex() + "(" + ifObj.getDescr() + ")";
				}
			}
			StringBuffer jsonString = new StringBuffer("{Rows:[");
			jsonString.append("{\"linkedId\":\"");
			jsonString.append(link.getId());
			jsonString.append("\",");
			jsonString.append("\"startId\":\"");
			jsonString.append(link.getStartId());
			jsonString.append("\",");

			jsonString.append("\"endId\":\"");
			jsonString.append(link.getEndId());
			jsonString.append("\",");

			jsonString.append("\"linkName\":\"");
			jsonString.append(link.getLinkName());
			jsonString.append("\",");

			jsonString.append("\"maxSpeed\":\"");
			jsonString.append(link.getMaxSpeed());
			jsonString.append("\",");

			jsonString.append("\"maxPer\":\"");
			jsonString.append(link.getMaxPer());
			jsonString.append("\",");

			jsonString.append("\"linkText\":\"");
			jsonString.append(link.getLinktype() + "");
			jsonString.append("\",");

			jsonString.append("\"startName\":\"");
			jsonString.append(link.getStartAlias() + "(" + link.getStartIp() + ")");
			jsonString.append("\",");

			jsonString.append("\"startIndex\":\"");
			jsonString.append(startIndex);
			jsonString.append("\",");

			jsonString.append("\"endName\":\"");
			jsonString.append(link.getEndAlias() + "(" + link.getEndIp() + ")");
			jsonString.append("\",");
			jsonString.append("\"endIndex\":\"");
			jsonString.append(endIndex);
			jsonString.append("\",");

			jsonString.append("\"startIp\":\"");
			jsonString.append(link.getStartIp());
			jsonString.append("\",");

			jsonString.append("\"indexStart\":\"");
			jsonString.append(link.getStartIndex());
			jsonString.append("\",");

			jsonString.append("\"endIp\":\"");
			jsonString.append(link.getEndIp());
			jsonString.append("\",");

			jsonString.append("\"indexEnd\":\"");
			jsonString.append(link.getEndIndex());
			jsonString.append("\",");

			jsonString.append("\"interfShow\":\"");
			jsonString.append(link.getShowinterf());
			jsonString.append("\"}");

			jsonString.append("],total: " + 1 + "}");
			out.print(jsonString.toString());
			out.flush();
		}

	}

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

	@SuppressWarnings("unchecked")
	private void editLink() {
		String id = getParaValue("linkedId");
		String startIndex = getParaValue("indexStart");
		String endIndex = getParaValue("indexEnd");
		int startId = getParaIntValue("startId");
		int endId = getParaIntValue("endId");

		String linkName = getParaValue("linkName");
		String maxSpeed = getParaValue("maxSpeed");
		String maxPer = getParaValue("maxPer");
		String interf = getParaValue("interfShow");

		if (startId == endId) {
			out.print("起始设备不能相同");
			out.flush();
		} else {
			LinkDao dao = new LinkDao();
			RepairLinkDao repairdao = new RepairLinkDao();
			Link formerLink = (Link) dao.findByID(id);
			String formerStartIndex = formerLink.getStartIndex();
			String formerEndIndex = formerLink.getEndIndex();
			Host startHost = (Host) PollingEngine.getInstance().getNodeByID(startId);
			IfEntity if1 = startHost.getIfEntityByIndex(startIndex);
			Host endHost = (Host) PollingEngine.getInstance().getNodeByID(endId);
			IfEntity if2 = endHost.getIfEntityByIndex(endIndex);

			String startHostName = startHost.getSysName();
			String endHostName = endHost.getSysName();
			String linkArisName = startHostName + "_" + startIndex + "/" + endHostName + "_" + endIndex;
			RepairLink repairLink = null;
			repairLink = repairdao.loadLink(startHost.getIpAddress(), formerStartIndex, endHost.getIpAddress(), formerEndIndex);

			formerLink.setLinktype(getParaIntValue("linkText"));
			formerLink.setMaxSpeed(maxSpeed);
			formerLink.setMaxPer(maxPer);
			formerLink.setShowinterf(Integer.parseInt(interf));
			formerLink.setLinkName(linkName);
			formerLink.setLinkArisName(linkArisName);
			formerLink.setStartId(startId);
			formerLink.setEndId(endId);
			formerLink.setStartIndex(startIndex);
			formerLink.setEndIndex(endIndex);
			formerLink.setStartIp(startHost.getIpAddress());
			formerLink.setEndIp(endHost.getIpAddress());
			formerLink.setStartDescr(if1.getDescr());
			formerLink.setEndDescr(if2.getDescr());
			formerLink.setType(1);
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

			// 更新xml
			XmlOperator opr = new XmlOperator();
			opr.setFile("network.jsp");
			opr.init4updateXml();
			opr.writeXml();

			LinkRoad lr = new LinkRoad();
			lr.setLinkName(formerLink.getLinkName());
			lr.setShowinterf(formerLink.getShowinterf());
			lr.setMaxPer(formerLink.getMaxPer());
			lr.setMaxSpeed(formerLink.getMaxSpeed());
			lr.setId(formerLink.getId());
			lr.setStartId(startId);
			if ("".equals(if1.getIpAddress()))
				lr.setStartIp(startHost.getIpAddress());
			else
				lr.setStartIp(if1.getIpAddress());
			lr.setStartIndex(startIndex);
			lr.setStartDescr(if1.getDescr());

			if ("".equals(if2.getIpAddress()))
				lr.setEndIp(endHost.getIpAddress());
			else
				lr.setEndIp(if2.getIpAddress());
			lr.setEndId(endId);
			lr.setEndIndex(endIndex);
			lr.setEndDescr(if2.getDescr());
			lr.setAssistant(formerLink.getAssistant());
			PollingEngine.getInstance().deleteLinkByID(lr.getId());
			PollingEngine.getInstance().getLinkList().add(lr);
			out.print("修改成功");
			out.flush();
		}
	}

	@SuppressWarnings("unchecked")
	private void getifIndexListById() {

		int id = getParaIntValue("id");

		Host host = (Host) PollingEngine.getInstance().getNodeByID(id);

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		List<IfEntity> IfentityList = getSortListByHash(host.getInterfaceHash());
		if (IfentityList != null && IfentityList.size() > 0) {
			for (IfEntity ifObj : IfentityList) {
				jsonString.append("{\"ifIndex\":\"");
				jsonString.append(ifObj.getIndex());
				jsonString.append("\",");

				jsonString.append("\"ifDescr\":\"");
				jsonString.append(ifObj.getDescr());
				jsonString.append("\"}");

				jsonString.append(",");
			}
			jsonString.substring(0, jsonString.length() - 1);
		}
		jsonString.append("],total: " + IfentityList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	@SuppressWarnings("unchecked")
	private void addLink() {
		String startIndex = getParaValue("startIndex");
		String endIndex = getParaValue("endIndex");
		int startId = getParaIntValue("startId");
		int endId = getParaIntValue("endId");
		String startAlias = getParaValue("startAlias");
		String endAlias = getParaValue("endAlias");
		String maxSpeed = getParaValue("maxSpeed");
		String maxPer = getParaValue("maxPer");
		String interf = getParaValue("interfShow");
		String startName = getParaValue("startName");
		String endName = getParaValue("endName");
		String linkText = getParaValue("linkText");// 链路显示
		String linkName = "";
		String linkAliasName = "";
		linkName = startName + "_" + startIndex + "/" + endName + "_" + endIndex;
		linkAliasName = startName + "_" + startIndex + "/" + endName + "_" + endIndex;

		if (startId == endId) {
			out.print("请选择两台不同的设备");
			out.flush();
		} else {
			LinkDao dao = new LinkDao();
			int exist = dao.linkExist(startId, startIndex, endId, endIndex);
			if (exist == 1) {
				dao.close();
				out.print("此链路已存在");
				out.flush();
			} else if (exist == 2) {
				dao.close();
				out.print("此链路已存在");
				out.flush();
			} else {
				Host startHost = (Host) PollingEngine.getInstance().getNodeByID(startId);
				IfEntity if1 = startHost.getIfEntityByIndex(startIndex);
				Host endHost = (Host) PollingEngine.getInstance().getNodeByID(endId);
				IfEntity if2 = endHost.getIfEntityByIndex(endIndex);
				Link link = new Link();
				link.setStartId(startId);
				link.setEndId(endId);
				link.setStartIndex(startIndex);
				link.setEndIndex(endIndex);
				link.setStartIp(startHost.getIpAddress());
				link.setEndIp(endHost.getIpAddress());
				link.setStartDescr(if1.getDescr());
				link.setEndDescr(if2.getDescr());
				link.setLinkName(linkName);// yangjun add
				link.setMaxSpeed(maxSpeed);// yangjun add
				link.setMaxPer(maxPer);// yangjun add
				link.setShowinterf(Integer.parseInt(interf));
				link.setType(1);
				link.setLinktype(Integer.parseInt(linkText));
				link.setLinkAliasName(linkAliasName);
				link.setStartAlias(startAlias);
				link.setEndAlias(endAlias);
				Link newLink = dao.save(link);
				// 更新xml
				XmlOperator opr = new XmlOperator();
				opr.setFile("network.jsp");
				opr.init4updateXml();
				if (newLink.getAssistant() == 0) {
					opr.addLine(String.valueOf(newLink.getId()), "net" + String.valueOf(startId), "net" + String.valueOf(endId));
				} else {
					opr.addAssistantLine(String.valueOf(newLink.getId()), "net" + String.valueOf(startId), "net" + String.valueOf(endId));
				}
				opr.writeXml();

				// 链路信息实时更新
				LinkRoad lr = new LinkRoad();
				lr.setId(newLink.getId());
				lr.setShowinterf(newLink.getShowinterf());
				lr.setMaxPer(newLink.getMaxPer());
				lr.setMaxSpeed(newLink.getMaxSpeed());
				lr.setLinkName(newLink.getLinkName());
				lr.setStartId(newLink.getStartId());
				lr.setStartIp(newLink.getStartIp());
				lr.setStartIndex(newLink.getStartIndex());
				lr.setStartDescr(newLink.getStartDescr());
				lr.setEndIp(newLink.getEndIp());
				lr.setEndId(newLink.getEndId());
				lr.setEndIndex(newLink.getEndIndex());
				lr.setEndDescr(newLink.getEndDescr());
				lr.setAssistant(newLink.getAssistant());
				PollingEngine.getInstance().getLinkList().add(lr);
				out.print("增加成功");
				out.flush();
			}
		}
	}

	private void getThresHoldEscapeList() {
		String type = getParaValue("type");
		String subType = null;
		try {
			subType = new String(getParaValue("subType").getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		if (subType.equals("思科")) {
			subType = "cisco";
		} else if (subType.equals("华三")) {
			subType = "h3c";
		}
		// 已添加的指标
		String thresHoldNameString = getParaValue("thresHoldNameString");
		String[] thresHoldNameStringArray = null;
		Hashtable<String, String> thresHoldNameStringHt = new Hashtable<String, String>();
		if (null != thresHoldNameString) {
			thresHoldNameStringArray = thresHoldNameString.split(";");
			if (null != thresHoldNameStringArray && thresHoldNameStringArray.length > 0) {
				for (int i = 0; i < thresHoldNameStringArray.length; i++) {
					thresHoldNameStringHt.put(thresHoldNameStringArray[i], thresHoldNameStringArray[i]);
				}
			}
		}
		List<AlarmIndicators> thresHoldList = new ArrayList<AlarmIndicators>();
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		try {
			thresHoldList = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(type, subType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int row = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != thresHoldList && thresHoldList.size() > 0) {
			AlarmIndicators alarmIndicators = null;
			for (int i = 0; i < thresHoldList.size(); i++) {
				alarmIndicators = thresHoldList.get(i);
				// 过滤已添加的
				if (null != thresHoldNameStringHt.get(alarmIndicators.getName())) {
					continue;
				}
				jsonString.append("{\"thresHoldId\":\"");
				jsonString.append(alarmIndicators.getId());
				jsonString.append("\",");

				jsonString.append("\"thresHoldName\":\"");
				jsonString.append(alarmIndicators.getName());
				jsonString.append("\",");

				jsonString.append("\"remark\":\"");
				jsonString.append(alarmIndicators.getDescr());
				jsonString.append("\"}");

				jsonString.append(",");
				row++;
			}
		}
		if (jsonString.indexOf(",") > 0) {
			jsonString.deleteCharAt(jsonString.length() - 1);
		}
		jsonString.append("],total:" + row + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void addNodeThresHold() {
		String thresHoldIdString = getParaValue("string");
		String nodeId = getParaValue("nodeId");
		String[] thresHoldIdArray = null;
		if (null != thresHoldIdString) {
			thresHoldIdArray = thresHoldIdString.split(";");
		}
		List<AlarmIndicatorsNode> toAddNodeThresHoldList = new ArrayList<AlarmIndicatorsNode>();
		if (null != thresHoldIdArray && thresHoldIdArray.length > 0) {
			AlarmIndicators thresHold = null;
			AlarmIndicatorsNode alarmIndicatorsNode = null;
			AlarmIndicatorsDao alarmIndicatorsDao = new AlarmIndicatorsDao();
			try {
				for (int i = 0; i < thresHoldIdArray.length; i++) {
					// 过滤空值
					if ("".equals(thresHoldIdArray[i].trim())) {
						continue;
					} else {
						thresHold = (AlarmIndicators) alarmIndicatorsDao.findByID(thresHoldIdArray[i].trim());
						if (null != thresHold) {
							alarmIndicatorsNode = new AlarmIndicatorsNode();
							alarmIndicatorsNode.setNodeid(nodeId);
							alarmIndicatorsNode.setSubentity(thresHold.getSubentity());
							alarmIndicatorsNode.setName(thresHold.getName());
							alarmIndicatorsNode.setType(thresHold.getType());
							alarmIndicatorsNode.setSubtype(thresHold.getSubtype());
							alarmIndicatorsNode.setDatatype(thresHold.getDatatype());
							alarmIndicatorsNode.setMoid(thresHold.getMoid());
							alarmIndicatorsNode.setThreshlod(thresHold.getThreshlod());
							alarmIndicatorsNode.setThreshlod_unit(thresHold.getThreshlod_unit());
							alarmIndicatorsNode.setCompare(thresHold.getCompare());
							alarmIndicatorsNode.setCompare_type(thresHold.getCompare_type());
							alarmIndicatorsNode.setAlarm_times(thresHold.getAlarm_times());
							alarmIndicatorsNode.setAlarm_info(thresHold.getAlarm_info());
							alarmIndicatorsNode.setAlarm_level(thresHold.getAlarm_level());
							alarmIndicatorsNode.setEnabled(thresHold.getEnabled());
							alarmIndicatorsNode.setPoll_interval(thresHold.getPoll_interval());
							alarmIndicatorsNode.setInterval_unit(thresHold.getInterval_unit());
							alarmIndicatorsNode.setLimenvalue0(thresHold.getLimenvalue0());
							alarmIndicatorsNode.setLimenvalue1(thresHold.getLimenvalue1());
							alarmIndicatorsNode.setLimenvalue2(thresHold.getLimenvalue2());
							alarmIndicatorsNode.setTime0(thresHold.getTime0());
							alarmIndicatorsNode.setTime1(thresHold.getTime1());
							alarmIndicatorsNode.setTime2(thresHold.getTime2());
							alarmIndicatorsNode.setSms0(thresHold.getSms0());
							alarmIndicatorsNode.setSms1(thresHold.getSms1());
							alarmIndicatorsNode.setSms2(thresHold.getSms2());
							alarmIndicatorsNode.setWay0(thresHold.getWay0());
							alarmIndicatorsNode.setWay1(thresHold.getWay1());
							alarmIndicatorsNode.setWay2(thresHold.getWay2());
							alarmIndicatorsNode.setCategory(thresHold.getCategory());
							alarmIndicatorsNode.setDescr(thresHold.getDescr());
							alarmIndicatorsNode.setUnit(thresHold.getUnit());
							toAddNodeThresHoldList.add(alarmIndicatorsNode);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				alarmIndicatorsDao.close();
			}
		}
		if (toAddNodeThresHoldList != null && toAddNodeThresHoldList.size() > 0) {
			AlarmIndicatorsNodeDao alarmIndicatorsNodeDao = null;
			try {
				alarmIndicatorsNodeDao = new AlarmIndicatorsNodeDao();
				alarmIndicatorsNodeDao.saveBatch(toAddNodeThresHoldList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != alarmIndicatorsNodeDao) {
					alarmIndicatorsNodeDao.close();
				}
			}
		}
		out.print("操作完成");
		out.flush();
	}

	public void execute(String action) {
		if (action.equals("getNode")) {
			getNode();
		} else if (action.equals("getThresHoldList")) {
			getThresHoldList();
		} else if (action.equals("deleteThresHolds")) {
			deleteThresHolds();
		} else if (action.equals("beforeEditThresHold")) {
			beforeEditThresHold();
		} else if (action.equals("editThresHold")) {
			editThresHold();
		} else if (action.equals("getCanAddThresHoldsAndNodes")) {
			getCanAddThresHoldsAndNodes();
		} else if (action.equals("saveAddThresHoldsAndNodes")) {
			saveAddThresHoldsAndNodes();
		} else if (action.equals("getLinkThresHoldList")) {
			getLinkThresHoldList();
		} else if (action.equals("linkEditAll")) {
			linkEditAll();
		} else if (action.equals("deleteLinkThresHolds")) {
			deleteLinkThresHolds();
		} else if (action.equals("beforeEditLink")) {
			beforeEditLink();
		} else if (action.equals("editLink")) {
			editLink();
		} else if (action.equals("getifIndexListById")) {
			getifIndexListById();
		} else if (action.equals("addLink")) {
			addLink();
		} else if (action.equals("getThresHoldEscapeList")) {
			getThresHoldEscapeList();
		} else if (action.equals("addNodeThresHold")) {
			addNodeThresHold();
		}
	}

}
