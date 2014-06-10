
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.TFTPConfigDao;
import com.afunms.application.model.TFTPConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Ftp;
import com.afunms.polling.node.TFtp;

@SuppressWarnings("unchecked")
public class TFtpLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getTftpList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Ftp) {
				TFtp node = (TFtp) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						TFTPConfig hostNode = (TFTPConfig) baseVoList.get(j);
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
		TFTPConfigDao dao = new TFTPConfigDao();
		List list = dao.loadAll();
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setTftplist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			TFTPConfig vo = (TFTPConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		TFTPConfig vo = (TFTPConfig) baseVo;
		TFtp tftp = new TFtp();
		tftp.setId(vo.getId());
		tftp.setName(vo.getName());
		tftp.setFilename(vo.getFilename());
		tftp.setUsername(vo.getUsername());
		tftp.setPassword(vo.getPassword());
		tftp.setMonflag(vo.getMonflag());
		tftp.setTimeout(vo.getTimeout());
		tftp.setAlias(vo.getName());
		tftp.setSendemail(vo.getSendemail());
		tftp.setSendmobiles(vo.getSendmobiles());
		tftp.setSendphone(vo.getSendphone());
		tftp.setBid(vo.getBid());
		tftp.setIpAddress(vo.getIpaddress());
		tftp.setCategory(94);
		tftp.setStatus(0);
		tftp.setType("TFTP");
		if (vo.getMonflag() == 1) {
			tftp.setManaged(true);
		} else {
			tftp.setManaged(false);
		}

		Node node = PollingEngine.getInstance().getTftpByID(tftp.getId());
		if (node != null) {
			PollingEngine.getInstance().getTftpList().remove(node);
		}
		PollingEngine.getInstance().addTftp(tftp);
	}
}