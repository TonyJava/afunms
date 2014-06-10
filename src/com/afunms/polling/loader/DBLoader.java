package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.DBDao;
import com.afunms.application.model.DBVo;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.DBNode;

@SuppressWarnings("unchecked")
public class DBLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getDbList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof DBNode) {
				DBNode node = (DBNode) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						DBVo hostNode = (DBVo) baseVoList.get(j);
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
		List list = null;
		DBDao dao = new DBDao();
		try {
			list = dao.getDbByMonFlag(-1);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}
		clearRubbish(list);
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setDbconfiglist(list);
		for (int i = 0; i < list.size(); i++) {
			DBVo vo = (DBVo) list.get(i);
			loadOne(vo);
		}

	}

	@Override
	public void loadOne(BaseVo baseVo) {
		DBVo vo = (DBVo) baseVo;
		DBNode dbNode = new DBNode();
		dbNode.setManaged(true);
		dbNode.setId(vo.getId());
		dbNode.setAlias(vo.getAlias());
		dbNode.setIpAddress(vo.getIpAddress());
		dbNode.setCategory(vo.getCategory());
		dbNode.setBid(vo.getBid());
		dbNode.setDbtype(vo.getDbtype());
		dbNode.setUser(vo.getUser());
		dbNode.setPassword(vo.getPassword());
		dbNode.setPort(vo.getPort());
		dbNode.setDbName(vo.getDbName());
		dbNode.setStatus(0);
		dbNode.setCollecttype(vo.getCollecttype());
		dbNode.setType("数据库");

		Node node = PollingEngine.getInstance().getDbByID(dbNode.getId());
		if (node != null) {
			PollingEngine.getInstance().getDbList().remove(node);
		}
		PollingEngine.getInstance().addDb(dbNode);
	}

	/**
	 * 刷新内存中的数据库列表
	 */
	public void refreshDBConfiglist() {
		// 初始化所有数据库
		DBDao dao = new DBDao();
		List list = null;
		try {
			list = dao.getDbByMonFlag(-1);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}
		clearRubbish(list);
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setDbconfiglist(list);
	}
}