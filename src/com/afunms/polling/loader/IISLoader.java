package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.model.IISConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.IIS;

@SuppressWarnings("unchecked")
public class IISLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getIisList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof IIS) {
				IIS node = (IIS) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						IISConfig hostNode = (IISConfig) baseVoList.get(j);
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
		IISConfigDao dao = new IISConfigDao();
		List list = dao.loadAll();
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setIislist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			IISConfig vo = (IISConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		IISConfig vo = (IISConfig) baseVo;
		IIS iis = new IIS();
		iis.setId(vo.getId());
		iis.setAlias(vo.getName());
		iis.setName(vo.getName());
		iis.setIpAddress(vo.getIpaddress());
		iis.setCommunity(vo.getCommunity());
		iis.setSendemail(vo.getSendemail());
		iis.setSendmobiles(vo.getSendmobiles());
		iis.setSendphone(vo.getSendphone());
		iis.setBid(vo.getNetid());
		iis.setMon_flag(vo.getMon_flag());
		iis.setCategory(67);
		iis.setStatus(0);
		iis.setType("IIS");

		Node node = PollingEngine.getInstance().getIisByID(iis.getId());
		if (node != null) {
			PollingEngine.getInstance().getIisList().remove(node);
		}
		PollingEngine.getInstance().addIis(iis);
	}
}