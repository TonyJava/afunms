/*
 * Facade class used to manage the network monitor demo app.
 */

package com.afunms.flex.networkTopology;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import com.afunms.common.base.BaseVo;
import com.afunms.flex.networkTopology.vo.Device;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.dao.TreeNodeDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.model.TreeNode;

@SuppressWarnings("unchecked")
public class NetworkMonitor {
	private static FeedThread _thread;
	private static DataSource _dataSource;
	private static Properties props = new Properties();

	public Device[] getDevices(String xml) {
		return getDataSource(xml).getDevices();
	}

	private DataSource getDataSource(String xml) {
		_dataSource = new DataSource(xml);
		return _dataSource;
	}

	// 读取properties信息
	public String readValue(String filePath, String key) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			props.load(in);
			String s = new String(key.getBytes("UTF-8"), "ISO-8859-1");
			String value = props.getProperty(s);
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Device getDeviceDetail(String id) {
		return _dataSource.getDeviceDetail(id);
	}

	public boolean stopFeed() {
		if (_thread != null) {
			_thread.running = false;
			_thread = null;
		}

		return getFeedStatus();
	}

	public boolean getFeedStatus() {
		if (_thread == null)
			return false;

		return _thread.running;
	}

	/**
	 * 得到业务视图列表
	 * 
	 * @return
	 */

	public Hashtable getBussinessviewHash() {
		Hashtable bussinessviewHash = new Hashtable();
		List bussinessview = null;
		ManageXmlDao manageXmlDao = new ManageXmlDao();
		try {
			bussinessview = (ArrayList) manageXmlDao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manageXmlDao.close();
		}

		for (int k = 0; k < bussinessview.size(); k++) {
			ManageXml manageXml = (ManageXml) bussinessview.get(k);
			if (manageXml.getTopoType() != 1) {
				continue;
			}
			// 为业务视图的情况
			NodeDependDao nodeDependDao = new NodeDependDao();
			List<NodeDepend> list = null;
			try {
				list = nodeDependDao.findByXml(manageXml.getXmlName());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodeDependDao.close();
			}

			List<NodeDTO> nodeDTOList = new ArrayList<NodeDTO>();
			List<Node> nodeList = new ArrayList<Node>();
			Hashtable<Node, String> nodeTagHash = new Hashtable<Node, String>();
			Hashtable<Node, TreeNode> treeNodeHash = new Hashtable<Node, TreeNode>();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {// 为业务视图的情况
					NodeDepend nodeDepend = list.get(i);
					String nodeId = nodeDepend.getNodeId();
					String nodeTag = nodeId.substring(0, 3);
					String node_id = nodeId.substring(3);

					TreeNodeDao treeNodeDao = new TreeNodeDao();
					TreeNode vo = null;
					try {
						vo = (TreeNode) treeNodeDao.findByNodeTag(nodeTag);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						treeNodeDao.close();
					}
					Node node = null;
					if (vo != null && vo.getName() != null && !"".equals(vo.getName())) {
						node = PollingEngine.getInstance().getNodeByCategory(vo.getName(), Integer.parseInt(node_id));
					}
					if (node != null) {
						nodeList.add(node);
						nodeTagHash.put(node, nodeTag);
						treeNodeHash.put(node, vo);
					}

					// 此处为 从数据库表里获取数据 暂未使用 但功能已完成
					NodeUtil nodeUtil = new NodeUtil();
					List<BaseVo> baseVolist = nodeUtil.getByNodeTag(nodeTag, vo.getCategory());
					List<NodeDTO> AllNodeDTOList = nodeUtil.conversionToNodeDTO(baseVolist);
					if (AllNodeDTOList != null) {
						for (NodeDTO nodeDTO : AllNodeDTOList) {
							if (nodeDTO.getNodeid().equalsIgnoreCase(node_id)) {
								nodeDTOList.add(nodeDTO);
							}
						}
					}
				}
			}
			bussinessviewHash.put(manageXml, nodeList);
		}
		return bussinessviewHash;
	}
}