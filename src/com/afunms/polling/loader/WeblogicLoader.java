
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Weblogic;

@SuppressWarnings("unchecked")
public class WeblogicLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getWeblogicList(); // �õ��ڴ��е�list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Weblogic) {
				Weblogic node = (Weblogic) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						WeblogicConfig hostNode = (WeblogicConfig) baseVoList.get(j);
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
		WeblogicConfigDao dao = null;
		List list = null;
		try {
			dao = new WeblogicConfigDao();
			list = dao.loadAll();
		} catch (Exception e) {
			e.printStackTrace();
		}  

		clearRubbish(list);
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setWeblogiclist(list);
		for (int i = 0; i < list.size(); i++) {
			WeblogicConfig vo = (WeblogicConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		WeblogicConfig vo = (WeblogicConfig) baseVo;
		Weblogic weblogic = new Weblogic();
		weblogic.setId(vo.getId());
		weblogic.setAlias(vo.getAlias());
		weblogic.setIpAddress(vo.getIpAddress());
		weblogic.setCommunity(vo.getCommunity());
		weblogic.setPortnum(vo.getPortnum());
		weblogic.setSendemail(vo.getSendemail());
		weblogic.setSendmobiles(vo.getSendmobiles());
		weblogic.setSendphone(vo.getSendphone());
		weblogic.setBid(vo.getNetid());
		weblogic.setMon_flag(vo.getMon_flag());
		weblogic.setServerName(vo.getServerName());
		weblogic.setServerAddr(vo.getServerAddr());
		weblogic.setServerPort(vo.getServerPort());
		weblogic.setDomainName(vo.getDomainName());
		weblogic.setDomainPort(vo.getDomainPort());
		weblogic.setDomainVersion(vo.getDomainVersion());
		weblogic.setCategory(64);
		weblogic.setStatus(0);
		weblogic.setType("Weblogic�м��");


		Node node = PollingEngine.getInstance().getWeblogicByID(weblogic.getId());
		if (node != null) {
			PollingEngine.getInstance().getWeblogicList().remove(node);
		}
		PollingEngine.getInstance().addWeblogic(weblogic);
	}
}