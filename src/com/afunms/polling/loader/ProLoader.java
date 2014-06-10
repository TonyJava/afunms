
package com.afunms.polling.loader;

import java.util.List;

import com.afunms.common.base.BaseVo;
import com.afunms.config.dao.ProcsDao;
import com.afunms.config.model.Procs;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Proces;

@SuppressWarnings("unchecked")
public class ProLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getProList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Proces) {
				Proces node = (Proces) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						Procs hostNode = (Procs) baseVoList.get(j);
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
		ProcsDao dao = new ProcsDao();
		List list = dao.loadAll();
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			Procs vo = (Procs) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		Procs vo = (Procs) baseVo;
		Proces pro = new Proces();
		pro.setId(vo.getId());
		pro.setIpAddress(vo.getIpaddress());
		pro.setName(vo.getBak());
		pro.setAlias(vo.getProcname());
		pro.setCategory(69);
		pro.setStatus(0);
		pro.setType("主机进程");// yangjun add


		Node node = PollingEngine.getInstance().getProByID(pro.getId());
		if (node != null) {
			PollingEngine.getInstance().getProList().remove(node);
		}
		PollingEngine.getInstance().addPro(pro);
	}
}