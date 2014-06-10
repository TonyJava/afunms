
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.TuxedoConfigDao;
import com.afunms.application.model.TuxedoConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;

@SuppressWarnings("unchecked")
public class TuxedoLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getApacheList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof TuxedoConfig) {
				TuxedoConfig node = (TuxedoConfig) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						TuxedoConfig hostNode = (TuxedoConfig) baseVoList.get(j);
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
		TuxedoConfigDao dao = new TuxedoConfigDao();
		List list = dao.loadAll();
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setTuxdolist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			TuxedoConfig vo = (TuxedoConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		TuxedoConfig vo = (TuxedoConfig) baseVo;
		com.afunms.polling.node.TuxedoConfig tuxedo = new com.afunms.polling.node.TuxedoConfig();
		tuxedo.setId(vo.getId());
		tuxedo.setAlias(vo.getName());
		tuxedo.setIpAddress(vo.getIpAddress());

		tuxedo.setSendemail(vo.getSendemail());
		tuxedo.setSendmobiles(vo.getSendmobiles());
		tuxedo.setBid(vo.getBid());
		tuxedo.setFlag(Integer.parseInt(vo.getMon_flag()));
		tuxedo.setCategory(71);
		tuxedo.setStatus(0);
		tuxedo.setType("tuxedo");

		Node node = PollingEngine.getInstance().getTuxedoById(tuxedo.getId());
		if (node != null) {
			PollingEngine.getInstance().getTuxedoList().remove(node);
		}
		PollingEngine.getInstance().addTuxedo(tuxedo);
	}
}