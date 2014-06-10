
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.GgsciConfigDao;
import com.afunms.application.model.GgsciConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Ggsci;

@SuppressWarnings("unchecked")
public class GgsciLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getGgsciList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Ggsci) {
				Ggsci node = (Ggsci) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						GgsciConfig hostNode = (GgsciConfig) baseVoList.get(j);
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
		GgsciConfigDao dao = new GgsciConfigDao();
		List list = null;
		try {
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setGgsciconfiglist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			GgsciConfig vo = (GgsciConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		GgsciConfig vo = (GgsciConfig) baseVo;
		Ggsci ggsci = new Ggsci();
		ggsci.setId(vo.getId());
		ggsci.setAlias(vo.getAlias());
		ggsci.setSendemail(vo.getSendemail());
		ggsci.setSendmobiles(vo.getSendmobiles());
		ggsci.setSendphone(vo.getSendphone());
		ggsci.setBid(vo.getNetid());
		ggsci.setMon_flag(vo.getMon_flag());
		ggsci.setIpAddress(vo.getIpAddress());
		ggsci.setCategory(122);
		ggsci.setStatus(0);
		ggsci.setType("Ggsci状态监视");

		Node node = PollingEngine.getInstance().getGgsciByID(ggsci.getId());
		if (node != null) {
			PollingEngine.getInstance().getGgsciList().remove(node);
		}
		PollingEngine.getInstance().addGgsci(ggsci);
	}
}