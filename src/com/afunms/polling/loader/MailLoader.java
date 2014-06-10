
package com.afunms.polling.loader;

import java.util.ArrayList;
import java.util.List;

import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.common.base.BaseVo;
import com.afunms.common.util.ShareData;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.base.NodeLoader;
import com.afunms.polling.node.Mail;

@SuppressWarnings("unchecked")
public class MailLoader extends NodeLoader {
	public void clearRubbish(List baseVoList) {
		List nodeList = PollingEngine.getInstance().getMailList(); // 得到内存中的list
		for (int index = 0; index < nodeList.size(); index++) {
			if (nodeList.get(index) instanceof Mail) {
				Mail node = (Mail) nodeList.get(index);
				if (baseVoList == null) {
					nodeList.remove(node);
				} else {
					boolean flag = false;
					for (int j = 0; j < baseVoList.size(); j++) {
						EmailMonitorConfig hostNode = (EmailMonitorConfig) baseVoList.get(j);
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
		EmailConfigDao dao = new EmailConfigDao();
		List list = dao.loadAll();
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setEmaillist(list);
		clearRubbish(list);
		for (int i = 0; i < list.size(); i++) {
			EmailMonitorConfig vo = (EmailMonitorConfig) list.get(i);
			loadOne(vo);
		}
	}

	@Override
	public void loadOne(BaseVo baseVo) {
		EmailMonitorConfig vo = (EmailMonitorConfig) baseVo;
		Mail mail = new Mail();
		mail.setId(vo.getId());
		mail.setName(vo.getName());
		mail.setAddress(vo.getAddress());
		mail.setUsername(vo.getUsername());
		mail.setPassword(vo.getPassword());
		mail.setRecivemail(vo.getRecivemail());
		mail.setFlag(vo.getFlag());
		mail.setMonflag(vo.getMonflag());
		mail.setTimeout(vo.getTimeout());
		mail.setAlias(vo.getName());
		mail.setSendemail(vo.getSendemail());
		mail.setSendmobiles(vo.getSendmobiles());
		mail.setSendphone(vo.getSendphone());
		mail.setBid(vo.getBid());
		mail.setMonflag(vo.getMonflag());
		mail.setIpAddress(vo.getIpaddress());
		mail.setCategory(56);
		mail.setStatus(0);
		mail.setType("邮件服务");


		Node node = PollingEngine.getInstance().getMailByID(mail.getId());
		if (node != null) {
			PollingEngine.getInstance().getMailList().remove(node);
		}
		PollingEngine.getInstance().addMail(mail);
	}
}