
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.WebLoginConfigDao;
import com.afunms.application.model.webloginConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.WebLogin;

@SuppressWarnings("unchecked")
public class WebLoginLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getWebloginList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof WebLogin) {
				WebLogin node = (WebLogin) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						webloginConfig hostNode = (webloginConfig) baseVoList.get(j);
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
		WebLoginConfigDao dao = new WebLoginConfigDao();
		List list = dao.loadAll();
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setWebloginlist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			webloginConfig vo = (webloginConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		webloginConfig vo = (webloginConfig) baseVo;
		WebLogin web = new WebLogin();
		web.setId(vo.getId());
		web.setAlias(vo.getAlias());
		web.setOutflag(vo.getOutflag());
		web.setOuturl(vo.getOuturl());
		web.setUrl(vo.getUrl());
		web.setFlag(Integer.parseInt(vo.getFlag()));
		web.setName_flag(vo.getName_flag());
		web.setPassword_flag(vo.getPassword_flag());
		web.setCode_flag(vo.getCode_flag());
		web.setUser_name(vo.getUser_name());
		web.setUser_password(vo.getUser_password());
		web.setUser_code(vo.getUser_code());
		web.setKeyword(vo.getKeyword());
		web.setTimeout(vo.getTimeout());
		web.setBid(vo.getBid());
		web.setSupperid(vo.getSupperid());
		web.setCategory(88);
		web.setStatus(0);
		web.setType("WEB虚拟登陆");


		Node node = PollingEngine.getInstance().getWebLoginByID(web.getId());
		if (node != null) {
			PollingEngine.getInstance().getWebloginList().remove(node);
		}
		PollingEngine.getInstance().addWebLogin(web);
	}
}