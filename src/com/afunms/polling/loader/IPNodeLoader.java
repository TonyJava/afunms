
package com.afunms.polling.loader;

import java.util.List;

import com.afunms.application.dao.IPNodeDao;
import com.afunms.application.model.IPNode;
import com.afunms.common.base.BaseVo;
import com.afunms.monitor.item.ResponseTimeItem;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.CustomIP;

@SuppressWarnings("unchecked")
public class IPNodeLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getNodeList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof CustomIP) {
				CustomIP node = (CustomIP) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						IPNode hostNode = (IPNode) baseVoList.get(j);
						if (node.getId() == hostNode.getId()) {
							flag = true;
						}
					}
					if (!flag) {
						nodeList.remove(node);
					}
				}
			}
		}
	}

	@Override
	public void loading() {
		IPNodeDao dao = new IPNodeDao();
		List list = dao.loadOrderByIP();
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			IPNode vo = (IPNode) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		IPNode vo = (IPNode) baseVo;
		CustomIP customIP = new CustomIP();
		customIP.setManaged(true);
		customIP.setId(vo.getId());
		customIP.setAlias(vo.getAlias());
		customIP.setIpAddress(vo.getIpAddress());

		ResponseTimeItem rti = new ResponseTimeItem(); // 仅加载ping监视器
		rti.setMoid("999001");
		rti.setResultType(1);
		rti.setEnabled(true);
		rti.setInterval(10 * 60); // 10分钟
		customIP.getMoidList().add(rti);

		Node node = PollingEngine.getInstance().getNodeByID(customIP.getId());
		if (node != null) {
			PollingEngine.getInstance().getNodeList().remove(node);
		}
		PollingEngine.getInstance().addNode(customIP);
	}
}