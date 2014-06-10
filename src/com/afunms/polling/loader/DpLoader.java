
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.DpConfigDao;
import com.afunms.application.model.DpConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Dp;

@SuppressWarnings("unchecked")
public class DpLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getDpList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Dp) {
				Dp node = (Dp) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						DpConfig hostNode = (DpConfig) baseVoList.get(j);
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
		DpConfigDao dao = new DpConfigDao();
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
		ShareData.setDpconfiglist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			DpConfig vo = (DpConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		DpConfig vo = (DpConfig) baseVo;
		Dp dp = new Dp();
		dp.setId(vo.getId());
		dp.setAlias(vo.getAlias());
		dp.setSendemail(vo.getSendemail());
		dp.setSendmobiles(vo.getSendmobiles());
		dp.setSendphone(vo.getSendphone());
		dp.setBid(vo.getNetid());
		dp.setMon_flag(vo.getMon_flag());
		dp.setIpAddress(vo.getIpAddress());
		dp.setCategory(120);
		dp.setStatus(0);
		dp.setType("Dp状态监视");

		Node node = PollingEngine.getInstance().getDpByID(dp.getId());
		if (node != null) {
			PollingEngine.getInstance().getDpList().remove(node);
		}
		PollingEngine.getInstance().addDp(dp);
	}
}