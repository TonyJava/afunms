
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.model.FTPConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Ftp;

@SuppressWarnings("unchecked")
public class FtpLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getFtpList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Ftp) {
				Ftp node = (Ftp) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						FTPConfig hostNode = (FTPConfig) baseVoList.get(j);
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
		FTPConfigDao dao = new FTPConfigDao();
		List list = dao.loadAll();
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setFtplist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			FTPConfig vo = (FTPConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		FTPConfig vo = (FTPConfig) baseVo;
		Ftp ftp = new Ftp();
		ftp.setId(vo.getId());
		ftp.setName(vo.getName());
		ftp.setFilename(vo.getFilename());
		ftp.setUsername(vo.getUsername());
		ftp.setPassword(vo.getPassword());
		ftp.setMonflag(vo.getMonflag());
		ftp.setTimeout(vo.getTimeout());
		ftp.setAlias(vo.getName());
		ftp.setSendemail(vo.getSendemail());
		ftp.setSendmobiles(vo.getSendmobiles());
		ftp.setSendphone(vo.getSendphone());
		ftp.setBid(vo.getBid());
		ftp.setIpAddress(vo.getIpaddress());
		ftp.setCategory(58);
		ftp.setStatus(0);
		ftp.setType("FTP");
		if (vo.getMonflag() == 1) {
			ftp.setManaged(true);
		} else {
			ftp.setManaged(false);
		}

		Node node = PollingEngine.getInstance().getFtpByID(ftp.getId());
		if (node != null) {
			PollingEngine.getInstance().getFtpList().remove(node);
		}
		PollingEngine.getInstance().addFtp(ftp);
	}
}