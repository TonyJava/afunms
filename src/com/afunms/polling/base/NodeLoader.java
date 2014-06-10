package com.afunms.polling.base;

import com.afunms.common.base.BaseVo;
import com.afunms.topology.dao.NodeMonitorDao;

public abstract class NodeLoader {

	public NodeLoader() {
	}

	/**
	 * �������нڵ�
	 */
	public abstract void loading();

	/**
	 * ����һ���ڵ�
	 */
	public abstract void loadOne(BaseVo vo);

	public NodeMonitorDao getNmDao() {
		NodeMonitorDao nmDao = new NodeMonitorDao();
		return nmDao;
	}

}