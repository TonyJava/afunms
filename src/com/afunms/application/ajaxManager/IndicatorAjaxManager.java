package com.afunms.application.ajaxManager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.indicators.dao.GatherIndicatorsDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.GatherIndicators;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.Constant;
import com.afunms.indicators.util.GatherIndicatorsUtil;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;

public class IndicatorAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {
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

	private void getIndicatorList() {
		String type = getParaValue("type");
		String subtype = getParaValue("subType");
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

		NodeGatherIndicatorsDao nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
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
		List indicatorList = new ArrayList();
		List<NodeDTO> allNodeDTOlist = new ArrayList<NodeDTO>();
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		try {
			indicatorList = nodeGatherIndicatorsDao.findByCondition(where);
			allNodeDTOlist = nodeGatherIndicatorsUtil.getNodeListByTypeAndSubtype(Constant.ALL_TYPE, Constant.ALL_SUBTYPE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		NodeGatherIndicators indicator = null;

		Hashtable<String, NodeDTO> nodeHt = new Hashtable<String, NodeDTO>();

		for (int i = 0; i < allNodeDTOlist.size(); i++) {
			nodeHt.put(allNodeDTOlist.get(i).getNodeid(), allNodeDTOlist.get(i));
		}

		String alias = null;
		String ip = null;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		for (int i = 0; i < indicatorList.size(); i++) {
			indicator = (NodeGatherIndicators) indicatorList.get(i);
			if (null != nodeHt) {
				if (null != nodeHt.get(indicator.getNodeid())) {
					alias = nodeHt.get(indicator.getNodeid()).getName();
					ip = nodeHt.get(indicator.getNodeid()).getIpaddress();
				}
			}
			jsonString.append("{\"indicatorId\":\"");
			jsonString.append(indicator.getId());
			jsonString.append("\",");

			jsonString.append("\"alias\":\"");
			jsonString.append(alias);
			jsonString.append("\",");

			jsonString.append("\"ip\":\"");
			jsonString.append(ip);
			jsonString.append("\",");

			jsonString.append("\"type\":\"");
			jsonString.append(indicator.getType());
			jsonString.append("\",");

			jsonString.append("\"subType\":\"");
			jsonString.append(indicator.getSubtype());
			jsonString.append("\",");

			jsonString.append("\"indicatorName\":\"");
			jsonString.append(indicator.getName());
			jsonString.append("\",");

			jsonString.append("\"isC\":\"");
			jsonString.append(indicator.getIsCollection());
			jsonString.append("\",");

			jsonString.append("\"interval\":\"");
			jsonString.append(indicator.getPoll_interval() + indicator.getInterval_unit());
			jsonString.append("\",");

			jsonString.append("\"remark\":\"");
			jsonString.append(indicator.getDescription());
			jsonString.append("\"}");

			if (i != indicatorList.size() - 1) {
				jsonString.append(",");
			}

		}
		jsonString.append("],total:" + indicatorList.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void deleteIndicators() {
		StringBuffer jsonString = new StringBuffer("删除采集指标");
		String string = getParaValue("string");
		String[] ids = null;
		if (null != string && !"".equals(string)) {
			ids = string.split(";");
		}
		NodeGatherIndicatorsDao nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
		try {
			nodeGatherIndicatorsDao.delete(ids);
			jsonString.append("成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			jsonString.append("失败");
		} finally {
			nodeGatherIndicatorsDao.close();
		}
		out.print(jsonString.toString());
		out.flush();
	}

	private void beforeEditIndicator() {
		String indicatorId = getParaValue("indicatorId");

		NodeGatherIndicatorsDao nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
		NodeGatherIndicators indicator = null;

		try {
			indicator = (NodeGatherIndicators) nodeGatherIndicatorsDao.findByID(indicatorId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			nodeGatherIndicatorsDao.close();
		}

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		jsonString.append("{\"indicatorId\":\"");
		jsonString.append(indicator.getId());
		jsonString.append("\",");

		jsonString.append("\"nodeId\":\"");
		jsonString.append(indicator.getNodeid());
		jsonString.append("\",");

		jsonString.append("\"type\":\"");
		jsonString.append(indicator.getType());
		jsonString.append("\",");

		jsonString.append("\"subType\":\"");
		jsonString.append(indicator.getSubtype());
		jsonString.append("\",");

		jsonString.append("\"isDefault\":\"");
		jsonString.append(indicator.getIsDefault());
		jsonString.append("\",");

		jsonString.append("\"classPath\":\"");
		jsonString.append(indicator.getClasspath());
		jsonString.append("\",");

		jsonString.append("\"indicatorName\":\"");
		jsonString.append(indicator.getName());
		jsonString.append("\",");

		jsonString.append("\"indicatorAlias\":\"");
		jsonString.append(indicator.getAlias());
		jsonString.append("\",");

		jsonString.append("\"isC\":\"");
		jsonString.append(indicator.getIsCollection());
		jsonString.append("\",");

		jsonString.append("\"interval\":\"");
		jsonString.append(indicator.getPoll_interval() + ":" + indicator.getInterval_unit());
		jsonString.append("\",");

		jsonString.append("\"category\":\"");
		jsonString.append(indicator.getCategory());
		jsonString.append("\",");

		jsonString.append("\"remark\":\"");
		jsonString.append(indicator.getDescription());
		jsonString.append("\"}");

		jsonString.append("],total:1}");
		out.print(jsonString.toString());
		out.flush();
	}

	private void editIndicator() {
		String[] iu = new String[] { "5", "m" };
		if (null != getParaValue("interval")) {
			iu = getParaValue("interval").split(":");
		}
		NodeGatherIndicators nodeGatherIndicators = new NodeGatherIndicators();

		nodeGatherIndicators.setId(getParaIntValue("indicatorId"));
		nodeGatherIndicators.setName(getParaValue("indicatorName"));
		nodeGatherIndicators.setType(getParaValue("type"));
		nodeGatherIndicators.setSubtype(getParaValue("subType"));
		nodeGatherIndicators.setAlias(getParaValue("indicatorAlias"));
		nodeGatherIndicators.setDescription(getParaValue("remark"));
		nodeGatherIndicators.setCategory(getParaValue("category"));
		nodeGatherIndicators.setIsDefault(getParaValue("isDefault"));
		nodeGatherIndicators.setIsCollection(getParaValue("isC"));
		nodeGatherIndicators.setNodeid(getParaValue("nodeId"));
		nodeGatherIndicators.setPoll_interval(iu[0]);
		nodeGatherIndicators.setInterval_unit(iu[1]);
		nodeGatherIndicators.setClasspath(getParaValue("classPath"));
		NodeGatherIndicatorsDao nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
		try {
			nodeGatherIndicatorsDao.update(nodeGatherIndicators);
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			nodeGatherIndicatorsDao.close();
		}

		StringBuffer jsonString = new StringBuffer("修改成功");
		out.print(jsonString.toString());
		out.flush();
	}

	private void getCanAddIndicatorsAndNodes() {
		String type = getParaValue("type");
		String subtype = getParaValue("subType");

		List<GatherIndicators> indicatorList = new ArrayList<GatherIndicators>();
		List<NodeDTO> nodeDTOlist = new ArrayList<NodeDTO>();
		GatherIndicatorsUtil gatherIndicatorsUtil = new GatherIndicatorsUtil();
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		try {
			indicatorList = gatherIndicatorsUtil.getGatherIndicatorsByTypeAndSubtype(type, subtype);
			nodeDTOlist = nodeGatherIndicatorsUtil.getNodeListByTypeAndSubtype(type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		}

		GatherIndicators indicator = null;
		NodeDTO node = null;

		StringBuffer jsonString = new StringBuffer("{IndicatorRows:[");
		for (int i = 0; i < indicatorList.size(); i++) {
			indicator = indicatorList.get(i);
			jsonString.append("{\"indicatorName\":\"");
			jsonString.append(indicator.getName());
			jsonString.append("\",");

			jsonString.append("\"indicatorId\":\"");
			jsonString.append(indicator.getId());
			jsonString.append("\",");

			jsonString.append("\"type\":\"");
			jsonString.append(indicator.getType());
			jsonString.append("\",");

			jsonString.append("\"subType\":\"");
			jsonString.append(indicator.getSubtype());
			jsonString.append("\",");

			jsonString.append("\"remark\":\"");
			jsonString.append(indicator.getDescription());
			jsonString.append("\"}");

			if (i != indicatorList.size() - 1) {
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

	private void saveAddIndicatorsAndNodes() {
		// 指标基础对象ids
		String toAddIndicatorsIds = getParaValue("toAddIndicatorsIds");
		// 需要应用指标的网元ids
		String toAddNodesValues = getParaValue("toAddNodesValues");
		// 网元对象类型
		String type = getParaValue("type");
		// 网元对象子类型
		String subType = getParaValue("subType");

		String[] indicatorIdArray = null;
		String[] nodeIdArray = null;
		StringBuffer jsonString = null;
		if ("".equals(toAddIndicatorsIds) || "".equals(toAddNodesValues) || "".equals(type) || "".equals(subType)) {
			jsonString = new StringBuffer("操作失败");
		} else {
			indicatorIdArray = toAddIndicatorsIds.split(";");
			nodeIdArray = toAddNodesValues.split(";");

			// 所有的网元
			List<NodeDTO> allNodeDTOlist = new ArrayList<NodeDTO>();
			// 需要应用指标的网元
			List<NodeDTO> needNodeDTOList = new ArrayList<NodeDTO>();

			NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
			try {
				allNodeDTOlist = nodeGatherIndicatorsUtil.getNodeListByTypeAndSubtype(Constant.ALL_TYPE, Constant.ALL_SUBTYPE);
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

			List<GatherIndicators> toAddIndicatorsList = new ArrayList<GatherIndicators>();

			List<NodeGatherIndicators> saveList = new ArrayList<NodeGatherIndicators>();
			List<NodeGatherIndicators> updateList = new ArrayList<NodeGatherIndicators>();

			GatherIndicators gatherIndicator = null;

			if (null != indicatorIdArray && indicatorIdArray.length > 0) {
				GatherIndicatorsDao gatherIndicatorsDao = new GatherIndicatorsDao();
				try {
					for (int i = 0; i < indicatorIdArray.length; i++) {
						// 过滤空值
						if ("".equals(indicatorIdArray[i].trim())) {
							continue;
						} else {
							gatherIndicator = (GatherIndicators) gatherIndicatorsDao.findByID(indicatorIdArray[i].trim());
							toAddIndicatorsList.add(gatherIndicator);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					gatherIndicatorsDao.close();
				}
			}

			if (null != needNodeDTOList && needNodeDTOList.size() > 0) {
				NodeGatherIndicatorsDao nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
				String nodeId = null;
				String where = " ";
				// 已经存在的网元指标
				List existNodeGatherIndicatorList = new ArrayList();
				NodeGatherIndicators tempNodeGatherIndicators = null;
				for (int i = 0; i < needNodeDTOList.size(); i++) {
					nodeId = needNodeDTOList.get(i).getNodeid();
					for (int j = 0; j < toAddIndicatorsList.size(); j++) {
						gatherIndicator = toAddIndicatorsList.get(j);

						// 由这个条件得出来的对象只有一个
						where = " where nodeid='" + nodeId + "' and type='" + type + "' and subtype='" + subType + "' and name='" + gatherIndicator.getName() + "'";
						try {
							// 查询是否存在同类指标
							nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
							existNodeGatherIndicatorList = nodeGatherIndicatorsDao.findByCondition(where);
							if (null != existNodeGatherIndicatorList && existNodeGatherIndicatorList.size() > 0) {
								updateList.add((NodeGatherIndicators) existNodeGatherIndicatorList.get(0));
							} else {
								tempNodeGatherIndicators = nodeGatherIndicatorsUtil.createGatherIndicatorsForNode(gatherIndicator);
								tempNodeGatherIndicators.setNodeid(nodeId);
								saveList.add(tempNodeGatherIndicators);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				// 保存数据
				if (updateList != null && updateList.size() > 0) {
					try {
						nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
						nodeGatherIndicatorsDao.updateBatch(updateList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (saveList != null && saveList.size() > 0) {
					try {
						nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
						nodeGatherIndicatorsDao.saveBatch(saveList);
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

	private void getIndicatorEscapeList() {
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
		String indicatorNameString = getParaValue("indicatorNameString");
		String[] indicatorNameStringArray = null;
		Hashtable<String, String> indicatorNameStringHt = new Hashtable<String, String>();
		if (null != indicatorNameString) {
			indicatorNameStringArray = indicatorNameString.split(";");
			if (null != indicatorNameStringArray && indicatorNameStringArray.length > 0) {
				for (int i = 0; i < indicatorNameStringArray.length; i++) {
					indicatorNameStringHt.put(indicatorNameStringArray[i], indicatorNameStringArray[i]);
				}
			}
		}
		List<GatherIndicators> indicatorList = new ArrayList<GatherIndicators>();
		GatherIndicatorsUtil gatherIndicatorsUtil = new GatherIndicatorsUtil();
		try {
			indicatorList = gatherIndicatorsUtil.getGatherIndicatorsByTypeAndSubtype(type, subType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int row = 0;
		StringBuffer jsonString = new StringBuffer("{Rows:[");
		if (null != indicatorList && indicatorList.size() > 0) {
			GatherIndicators indicator = null;
			for (int i = 0; i < indicatorList.size(); i++) {
				indicator = indicatorList.get(i);
				// 过滤已添加的
				if (null != indicatorNameStringHt.get(indicator.getName())) {
					continue;
				}
				jsonString.append("{\"indicatorId\":\"");
				jsonString.append(indicator.getId());
				jsonString.append("\",");

				jsonString.append("\"indicatorName\":\"");
				jsonString.append(indicator.getName());
				jsonString.append("\",");

				jsonString.append("\"remark\":\"");
				jsonString.append(indicator.getDescription());
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

	private void addNodeIndicator() {
		String indicatorIdString = getParaValue("string");
		String nodeId = getParaValue("nodeId");
		String[] indicatorIdArray = null;
		if (null != indicatorIdString) {
			indicatorIdArray = indicatorIdString.split(";");
		}
		List<NodeGatherIndicators> toAddNodeIndicatorsList = new ArrayList<NodeGatherIndicators>();
		if (null != indicatorIdArray && indicatorIdArray.length > 0) {
			GatherIndicators gatherIndicator = null;
			NodeGatherIndicators nodeGatherIndicators = null;
			GatherIndicatorsDao gatherIndicatorsDao = new GatherIndicatorsDao();
			try {
				for (int i = 0; i < indicatorIdArray.length; i++) {
					// 过滤空值
					if ("".equals(indicatorIdArray[i].trim())) {
						continue;
					} else {
						gatherIndicator = (GatherIndicators) gatherIndicatorsDao.findByID(indicatorIdArray[i].trim());
						if (null != gatherIndicator) {
							nodeGatherIndicators = new NodeGatherIndicators();
							nodeGatherIndicators.setNodeid(nodeId);
							nodeGatherIndicators.setName(gatherIndicator.getName());
							nodeGatherIndicators.setType(gatherIndicator.getType());
							nodeGatherIndicators.setSubtype(gatherIndicator.getSubtype());
							nodeGatherIndicators.setAlias(gatherIndicator.getAlias());
							nodeGatherIndicators.setDescription(gatherIndicator.getDescription());
							nodeGatherIndicators.setCategory(gatherIndicator.getDescription());
							nodeGatherIndicators.setIsDefault(gatherIndicator.getIsDefault());
							nodeGatherIndicators.setIsCollection(gatherIndicator.getIsCollection());
							nodeGatherIndicators.setPoll_interval(gatherIndicator.getPoll_interval());
							nodeGatherIndicators.setInterval_unit(gatherIndicator.getInterval_unit());
							nodeGatherIndicators.setClasspath(gatherIndicator.getClasspath());
							toAddNodeIndicatorsList.add(nodeGatherIndicators);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				gatherIndicatorsDao.close();
			}
		}
		if (toAddNodeIndicatorsList != null && toAddNodeIndicatorsList.size() > 0) {
			NodeGatherIndicatorsDao nodeGatherIndicatorsDao = null;
			try {
				nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
				nodeGatherIndicatorsDao.saveBatch(toAddNodeIndicatorsList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (null != nodeGatherIndicatorsDao) {
					nodeGatherIndicatorsDao.close();
				}
			}
		}
		out.print("操作完成");
		out.flush();
	}

	private void saveBatchIndicatorNodes() {
		String nodeId = getParaValue("nodeId");
		String nodes = getParaValue("nodeids");
		String type = getParaValue("type");
		String subtype = getParaValue("subtype");
		String[] nodeids = null;
		if (nodes != null && nodes.length() > 0) {
			nodeids = nodes.split(";");
		}

		List<NodeGatherIndicators> indicatorList = new ArrayList<NodeGatherIndicators>();
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		try {
			indicatorList = nodeGatherIndicatorsUtil.getGatherIndicatorsForNode(nodeId, type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		}

		boolean flag = true;
		boolean flags = true;

		NodeGatherIndicatorsDao nodeGatherIndicatorsDao = null;
		if (nodeids != null && nodeids.length > 0) {
			List updatelist = new ArrayList();
			List savelist = new ArrayList();
			Hashtable nodeindihash = new Hashtable();
			try {
				List list2 = new ArrayList();
				for (int i = 0; i < nodeids.length; i++) {
					nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
					try {
						list2 = nodeGatherIndicatorsDao.findByNodeIdAndTypeAndSubtype(nodeids[i], type, subtype);
					} catch (Exception e) {
					} finally {
						nodeGatherIndicatorsDao.close();
					}

					if (list2 != null && list2.size() > 0) {
						for (int j = 0; j < list2.size(); j++) {
							NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) list2.get(j);
							nodeindihash.put(nodeGatherIndicators.getName() + ":" + nodeGatherIndicators.getType() + ":" + nodeGatherIndicators.getSubtype(), nodeGatherIndicators);
						}
						if (indicatorList != null && indicatorList.size() > 0) {
							try {
								for (int k = 0; k < indicatorList.size(); k++) {
									NodeGatherIndicators _nodeGatherIndicators = (NodeGatherIndicators) indicatorList.get(k);
									if (nodeindihash.containsKey(_nodeGatherIndicators.getName() + ":" + _nodeGatherIndicators.getType() + ":" + _nodeGatherIndicators.getSubtype())) {
										// 若存在,则修改
										NodeGatherIndicators nodeGatherIndicators_update = (NodeGatherIndicators) nodeindihash.get(_nodeGatherIndicators.getName() + ":" + _nodeGatherIndicators.getType() + ":" + _nodeGatherIndicators.getSubtype());
										NodeGatherIndicators nodeGatherIndicators_copy = nodeGatherIndicatorsUtil.createGatherIndicatorsForNode(_nodeGatherIndicators);
										nodeGatherIndicators_copy.setId(nodeGatherIndicators_update.getId());
										nodeGatherIndicators_copy.setNodeid(nodeGatherIndicators_update.getNodeid());
										updatelist.add(nodeGatherIndicators_copy);
									} else {
										// 不存在,则需要添加进去
										NodeGatherIndicators nodeGatherIndicators_copy = nodeGatherIndicatorsUtil.createGatherIndicatorsForNode(_nodeGatherIndicators);

										nodeGatherIndicators_copy.setNodeid(nodeids[i]);
										savelist.add(nodeGatherIndicators_copy);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} else {
						// 没设置任何采集指标,则添加全部
						if (indicatorList != null && indicatorList.size() > 0) {
							try {
								for (int k = 0; k < indicatorList.size(); k++) {
									// 不存在,则需要添加进去
									NodeGatherIndicators _nodeGatherIndicators = (NodeGatherIndicators) indicatorList.get(k);
									NodeGatherIndicators nodeGatherIndicators_copy = nodeGatherIndicatorsUtil.createGatherIndicatorsForNode(_nodeGatherIndicators);

									nodeGatherIndicators_copy.setNodeid(nodeids[i]);
									savelist.add(nodeGatherIndicators_copy);
								}
							} catch (Exception e) {

							}
						}
					}
				}

				if (updatelist != null && updatelist.size() > 0) {
					try {
						nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
						flag = nodeGatherIndicatorsDao.updateBatch(updatelist);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						nodeGatherIndicatorsDao.close();
					}
				}
				if (savelist != null && savelist.size() > 0) {
					try {
						nodeGatherIndicatorsDao = new NodeGatherIndicatorsDao();
						flags = nodeGatherIndicatorsDao.saveBatch(savelist);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						nodeGatherIndicatorsDao.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		nodeGatherIndicatorsUtil.refreshShareDataGather();
		nodeGatherIndicatorsUtil = null;

		if (flag && flags) {
			out.print("批量设置成功");
		} else {
			out.print("批量设置失败");
		}
		out.flush();
	}

	private void getBatchIndicatorsNodes() {
		String type = getParaValue("type");
		String subtype = getParaValue("subType");

		List<NodeDTO> nodeDTOlist = new ArrayList<NodeDTO>();
		NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
		try {
			nodeDTOlist = nodeGatherIndicatorsUtil.getNodeListByTypeAndSubtype(type, subtype);
		} catch (Exception e) {
			e.printStackTrace();
		}

		NodeDTO node = null;

		StringBuffer jsonString = new StringBuffer("{Rows:[");
		for (int i = 0; i < nodeDTOlist.size(); i++) {
			node = nodeDTOlist.get(i);
			jsonString.append("{\"nodeId\":\"");
			jsonString.append(node.getId());
			jsonString.append("\",");

			jsonString.append("\"nodeIp\":\"");
			jsonString.append(node.getIpaddress());
			jsonString.append("\",");

			jsonString.append("\"nodeAlias\":\"");
			jsonString.append(node.getName());
			jsonString.append("\",");

			jsonString.append("\"nodeBSname\":\"");
			jsonString.append(node.getBusinessName());
			jsonString.append("\"}");

			if (i != nodeDTOlist.size() - 1) {
				jsonString.append(",");
			}
		}
		jsonString.append("],total : " + nodeDTOlist.size() + "}");
		out.print(jsonString.toString());
		out.flush();
	}

	public void execute(String action) {
		if (action.equals("getNode")) {
			getNode();
		} else if (action.equals("getIndicatorList")) {
			getIndicatorList();
		} else if (action.equals("deleteIndicators")) {
			deleteIndicators();
		} else if (action.equals("beforeEditIndicator")) {
			beforeEditIndicator();
		} else if (action.equals("editIndicator")) {
			editIndicator();
		} else if (action.equals("getCanAddIndicatorsAndNodes")) {
			getCanAddIndicatorsAndNodes();
		} else if (action.equals("saveAddIndicatorsAndNodes")) {
			saveAddIndicatorsAndNodes();
		} else if (action.equals("getIndicatorEscapeList")) {
			getIndicatorEscapeList();
		} else if (action.equals("addNodeIndicator")) {
			addNodeIndicator();
		} else if (action.equals("getBatchIndicatorsNodes")) {
			getBatchIndicatorsNodes();
		} else if (action.equals("saveBatchIndicatorNodes")) {
			saveBatchIndicatorNodes();
		}
	}

}
