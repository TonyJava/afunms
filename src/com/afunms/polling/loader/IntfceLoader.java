
package com.afunms.polling.loader;

import java.util.List;

import com.afunms.business.dao.BusinessNodeDao;
import com.afunms.business.model.BusinessNode;
import com.afunms.common.base.BaseVo;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Interface;

@SuppressWarnings("unchecked")
public class IntfceLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getIntfceList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Interface) {
				Interface node = (Interface) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						BusinessNode hostNode = (BusinessNode) baseVoList.get(j);
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
		BusinessNodeDao dao = new BusinessNodeDao();
		List list = dao.loadAll();
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			BusinessNode vo = (BusinessNode) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		BusinessNode vo = (BusinessNode) baseVo;
		Interface intfce = new Interface();
		intfce.setId(vo.getId());
		intfce.setFid(vo.getBid());
		intfce.setIpAddress(vo.getDesc());
		intfce.setName(vo.getName());
		intfce.setAlias(vo.getName());
		intfce.setMethod(vo.getMethod());
		intfce.setCategory(81);
		intfce.setStatus(0);
		intfce.setBid(vo.getBid() + "");
		intfce.setType("业务接口"); 

		Node node = PollingEngine.getInstance().getIntfaceByID(intfce.getId());
		if (node != null) {
			PollingEngine.getInstance().getIntfceList().remove(node);
		}
		PollingEngine.getInstance().addIntface(intfce);
	}
}