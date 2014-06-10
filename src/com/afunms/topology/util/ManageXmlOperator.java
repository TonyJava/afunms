package com.afunms.topology.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.TreeNodeDao;
import com.afunms.topology.model.TreeNode;

@SuppressWarnings("unchecked")
public class ManageXmlOperator extends XmlOperator {
	// ��ȡͼƬ��С
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
		return sourceImg.getWidth() + ":" + sourceImg.getHeight();

	}

	private List tempNodeList;

	public ManageXmlOperator() {
	}

	/**
	 * ����ʾ���� yangjun add
	 */
	public void addDemoNode(String nodeId, String categroy, String image, String alias, String x, String y) {
		Element eleNode = new Element("node");
		Element eleId = new Element("id");
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleW = new Element("width");
		Element eleH = new Element("height");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
		Element relationMap = new Element("relationMap");// ��������ͼ

		eleId.setText(nodeId);
		eleId.setAttribute("category", categroy);
		eleImg.setText(image);
		eleX.setText(x);
		eleY.setText(y);
		String sizeString = getImageSize(ResourceCenter.getInstance().getSysPath() + "resource/" + image);
		eleW.setText(sizeString.split(":")[0]);
		eleH.setText(sizeString.split(":")[1]);
		eleIp.setText(alias);
		eleAlias.setText(alias);
		eleInfo.setText("ʾ���豸");
		eleMenu.setText(NodeHelper.getMenuItem(nodeId));
		relationMap.setText("");
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleW);
		eleNode.addContent(eleH);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		eleNode.addContent(relationMap);
		nodes.addContent(eleNode);
	}

	// ����ʾ����·
	public void addLine(int id, String lineId, String id1, String id2, String width) {
		Element demoLine = new Element("demoLine");
		Element a = new Element("a");
		Element b = new Element("b");
		Element color = new Element("color");
		Element dash = new Element("dash");
		Element lineWidth = new Element("lineWidth"); 
		Element lineInfo = new Element("lineInfo"); 
		Element lineMenu = new Element("lineMenu"); 

		demoLine.setAttribute("id", lineId);
		a.setText(id1);
		b.setText(id2);
		color.setText("blue");
		dash.setText("Solid");
		lineWidth.setText(width);
		lineInfo.setText("ʾ����·");
		lineMenu.setText(NodeHelper.getMenuLine(id, lineId));

		demoLine.addContent(a);
		demoLine.addContent(b);
		demoLine.addContent(color);
		demoLine.addContent(dash);
		demoLine.addContent(lineWidth);
		demoLine.addContent(lineInfo);
		demoLine.addContent(lineMenu);
		demoLines.addContent(demoLine);
	}

	/**
	 * ���ӽ��(�����е���Դ������)������ͼʱʹ��
	 */
	public void addNode(String nodeId, int index) {
		Node node = null;
		TreeNodeDao treeNodeDao = new TreeNodeDao();
		TreeNode vo = (TreeNode) treeNodeDao.findByNodeTag(nodeId.substring(0, 3));
		if (vo != null && vo.getName() != null && !"".equals(vo.getName())) {
			node = PollingEngine.getInstance().getNodeByCategory(vo.getName(), Integer.parseInt(nodeId.substring(3)));
		}
		if (node == null) {
			return;
		}

		String eleId = String.valueOf(nodeId);
		String eleImage = null;
		if (node.getCategory() == 4) {
			eleImage = NodeHelper.getServerTopoImage(((Host) node).getSysOid());
		} else {
			eleImage = NodeHelper.getTopoImage(node.getCategory());
		}

		addNode(eleId, node.getCategory(), eleImage, node.getIpAddress(), node.getAlias(), String.valueOf(index * 30), "15");
	}

	/**
	 * ���ӽ��(�����е���Դ������)���豸������ʱʹ��
	 */
	public void addNode(String nodeId, int index, String category) {
		Node node = PollingEngine.getInstance().getNodeByCategory(category, Integer.parseInt(nodeId.substring(3)));
		if (node == null) {
			return;
		}
		String eleImage = null;
		if (node.getCategory() == 4) {
			eleImage = NodeHelper.getServerTopoImage(((Host) node).getSysOid());
		} else if (node.getCategory() == 14) {
			eleImage = NodeHelper.getStorageTopoImage(((Host) node).getSysOid());
		} else {
			eleImage = NodeHelper.getTopoImage(node.getCategory());
		}

		addNode(nodeId, node.getCategory(), eleImage, node.getIpAddress(), node.getAlias(), String.valueOf(index * 30), "15");
	}

	public void addPolicyDemoNode(String nodeId, String categroy, String image, String alias, String x, String y) {
		Element eleNode = new Element("node");
		Element eleId = new Element("id");
		Element eleImg = new Element("img");
		Element eleX = new Element("x");
		Element eleY = new Element("y");
		Element eleIp = new Element("ip");
		Element eleAlias = new Element("alias");
		Element eleInfo = new Element("info");
		Element eleMenu = new Element("menu");
		Element state = new Element("state");

		eleId.setText(nodeId);
		eleId.setAttribute("category", categroy);
		eleImg.setText(image);
		eleX.setText(x);
		eleY.setText(y);
		eleIp.setText(alias);
		eleAlias.setText(alias);
		eleInfo.setText("ʾ���豸");
		eleMenu.setText(NodeHelper.getMenuItem(nodeId));
		state.setText("0");
		eleNode.addContent(eleId);
		eleNode.addContent(eleImg);
		eleNode.addContent(eleX);
		eleNode.addContent(eleY);
		eleNode.addContent(eleIp);
		eleNode.addContent(eleAlias);
		eleNode.addContent(eleInfo);
		eleNode.addContent(eleMenu);
		eleNode.addContent(state);
		nodes.addContent(eleNode);
	}

	/**
	 * �༭�����,ɾ��û�õĽ��
	 */
	public void deleteNodes() {
		if (tempNodeList == null) {
			return;
		}

		for (int i = 0; i < tempNodeList.size(); i++) {
			XmlInfo xmlInfo = (XmlInfo) tempNodeList.get(i);
			if (!xmlInfo.isExist()) {
				deleteNodeByID(xmlInfo.getId());
			}
		}
	}

	// ��������ʾ����·id yangjun add
	public int findMaxDemoLineId() {
		int id = 0;
		List eleLines = demoLines.getChildren();
		if (eleLines.size() > 0) {
			for (int i = 0; i < eleLines.size(); i++) {
				Element ele = (Element) eleLines.get(i);
				if (Integer.parseInt(ele.getAttributeValue("id").substring(2)) > id) {
					id = Integer.parseInt(ele.getAttributeValue("id").substring(2));
				}
			}
		}
		return id + 1;
	}

	// �������Ľڵ�id yangjun add
	public int findMaxNodeId() {
		int id = 0;
		List eleNodes = nodes.getChildren();
		for (int i = 0; i < eleNodes.size(); i++) {
			Element ele = (Element) eleNodes.get(i);
			if (Integer.parseInt(ele.getChildText("id").substring(3)) > id) {
				id = Integer.parseInt(ele.getChildText("id").substring(3));
			}
		}
		return id + 1;
	}

	public Hashtable getAllXY() {
		Hashtable hash = new Hashtable();
		List eleNodes = nodes.getChildren();
		if (eleNodes.size() > 0) {
			for (int i = 0; i < eleNodes.size(); i++) {
				Element ele = (Element) eleNodes.get(i);
				hash.put(ele.getChildText("id"), ele.getChildText("x") + "," + ele.getChildText("y"));
			}
		}
		return hash;
	}

	/**
	 * Ϊ�༭������׼��
	 */
	public void init4editLines() {
		init4updateXml();
		root.removeContent(lines);
		lines = new Element("lines");
		root.addContent(lines);
	}

	/**
	 * Ϊ�༭�����׼��,�����н��Ĳ�����Ϣ�ȷŵ��ڴ���
	 */
	public void init4editNodes() {
		init4updateXml();
		tempNodeList = new ArrayList();

		List eleNodes = nodes.getChildren();
		int len = eleNodes.size();
		for (int i = 0; i < len; i++) {
			Element element = (Element) eleNodes.get(i);
			XmlInfo xmlInfo = new XmlInfo();
			xmlInfo.setId(element.getChildText("id"));
			xmlInfo.setExist(false);
			tempNodeList.add(xmlInfo);
		}
	}

	/**
	 * ���ڵ��Ƿ��Ѿ�����
	 */
	public boolean isIdExist(String hostId) {
		if (tempNodeList == null) {
			return false;
		}
		boolean exist = false;
		for (int i = 0; i < tempNodeList.size(); i++) {
			XmlInfo xmlInfo = (XmlInfo) tempNodeList.get(i);
			if (xmlInfo.getId().equals(hostId)) {
				exist = true;
				xmlInfo.setExist(true);
				break;
			}
		}
		return exist;
	}

	// �޸���·��Ϣ 
	public void updateAssLine(String lineId, String tag, String txt) {
		List eleLines = assistantLines.getChildren();
		for (int i = 0; i < eleLines.size(); i++) {
			Element ele = (Element) eleLines.get(i);
			if (ele.getAttributeValue("id").equals(lineId)) {
				ele.getChild(tag).setText(txt);
				break;
			}
		}
	}

	// �޸���·��Ϣ  
	public void updateDemoLine(String lineId, String tag, String txt) {
		List eleLines = demoLines.getChildren();
		for (int i = 0; i < eleLines.size(); i++) {
			Element ele = (Element) eleLines.get(i);
			if (ele.getAttributeValue("id").equals(lineId)) {
				ele.getChild(tag).setText(txt);
				break;
			}
		}
	}

	// �޸���·��Ϣ 
	public void updateLine(String lineId, String tag, String txt) {
		List eleLines = lines.getChildren();
		for (int i = 0; i < eleLines.size(); i++) {
			Element ele = (Element) eleLines.get(i);
			if (ele.getAttributeValue("id").equals(lineId)) {
				ele.getChild(tag).setText(txt);
				break;
			}
		}
	}

	// ���ҵ����ͼ���нڵ������λ��
	@Override
	public void updateNode(String nodeId, String tag, String txt) {
		List eleNodes = nodes.getChildren();
		for (int i = 0; i < eleNodes.size(); i++) {
			Element ele = (Element) eleNodes.get(i);
			if (ele.getChildText("id").equals(nodeId)) {
				ele.getChild(tag).setText(txt);
				break;
			}
		}
	}
}