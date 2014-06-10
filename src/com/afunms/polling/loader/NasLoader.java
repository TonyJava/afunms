
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.NasConfigDao;
import com.afunms.application.model.NasConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Nas;

@SuppressWarnings("unchecked")
public class NasLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getNasList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Nas) {
				Nas node = (Nas) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						NasConfig hostNode = (NasConfig) baseVoList.get(j);
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
		NasConfigDao dao = new NasConfigDao();
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
		ShareData.setNasconfiglist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			NasConfig vo = (NasConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		NasConfig vo = (NasConfig) baseVo;
		Nas nas = new Nas();
		nas.setId(vo.getId());
		nas.setAlias(vo.getAlias());
		nas.setSendemail(vo.getSendemail());
		nas.setSendmobiles(vo.getSendmobiles());
		nas.setSendphone(vo.getSendphone());
		nas.setBid(vo.getNetid());
		nas.setMon_flag(vo.getMon_flag());
		nas.setIpAddress(vo.getIpAddress());
		nas.setCategory(121);
		nas.setStatus(0);
		nas.setType("Nas状态监视");

		Node node = PollingEngine.getInstance().getNasByID(nas.getId());
		if (node != null) {
			PollingEngine.getInstance().getNasList().remove(node);
		}
		PollingEngine.getInstance().addNas(nas);
	}
}