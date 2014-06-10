
package com.afunms.topology.manage;

import java.util.ArrayList;
import java.util.List;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.node.Host;
import com.afunms.topology.dao.SubnetDao;

@SuppressWarnings("unchecked")
public class SubnetManager extends BaseManager implements ManagerInterface {

	private String list() {
		SubnetDao dao = new SubnetDao();
		List list = dao.loadAll();

		request.setAttribute("list", list);
		return "/topology/subnet/list.jsp";
	}

	private String listDevice() {
		int netId = getParaIntValue("id");
		List hostList = PollingEngine.getInstance().getNodeList();
		List list = new ArrayList(20);
		for (int i = 0; i < hostList.size(); i++) {
			if (((Node) hostList.get(i)).getCategory() >= 50)
				continue; // 应用的监视器>50

			Host host = (Host) hostList.get(i);
			if (host.getLocalNet() == netId)
				list.add(host);
		}
		request.setAttribute("list", list);
		request.setAttribute("address", getParaValue("ip"));
		return "/topology/subnet/list_device.jsp";
	}

	public String execute(String action) {
		if (action.equals("list"))
			return list();
		if (action.equals("list_device"))
			return listDevice();
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}
}