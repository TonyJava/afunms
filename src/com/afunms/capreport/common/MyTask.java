package com.afunms.capreport.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

import com.afunms.capreport.manage.HostCapReportManager;
import com.afunms.capreport.manage.NetCapReportManager;
import com.afunms.capreport.model.CycleReportConfig;
import com.afunms.common.util.SendMailManager;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.dao.AlertEmailDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.AlertEmail;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class MyTask extends TimerTask {
	CycleReportConfig config;

	public CycleReportConfig getConfig() {
		return config;
	}

	public void setConfig(CycleReportConfig config) {
		this.config = config;
	}

	public void run() {
		String[] deviceIds = config.getCollectionOfdeviceId().substring(1).split(",");
		HostNodeDao dao = new HostNodeDao();

		String fileName = ResourceCenter.getInstance().getSysPath() + "temp/network_and_host_report.xls";
		try {
			WritableWorkbook wb = Workbook.createWorkbook(new File(fileName));
			for (int i = 0; i < deviceIds.length; i++) {
				HostNode node = (HostNode) dao.findByID(deviceIds[i]);
				if (node.getCategory() == 4) {
					HostCapReportManager report = new HostCapReportManager();
					report.createselfhostreport(null, null, node.getIpAddress(), "host", "0", wb, "temp/network_and_host_report.xls");
					report = null;
				} else if (node.getCategory() == 1) {
					NetCapReportManager report = new NetCapReportManager();
					report.createselfnetreport(null, null, node.getIpAddress(), "net", "0", wb, "temp/network_and_host_report.xls");
				}
			}
			wb.write();
			wb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao.close();
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try {
			userList = userDao.findbyIDs(config.getCollectionOfRecieverId().substring(1));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.close();
		}

		AlertEmail em = null;
		AlertEmailDao emaildao = new AlertEmailDao();
		List list = null;
		try {
			list = emaildao.getByFlage(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emaildao.close();
		}
		if (list != null && list.size() > 0) {
			em = (AlertEmail) list.get(0);
		}
		if (em == null)
			return;
		for (int i = 0; i < userList.size(); i++) {
			User vo = (User) userList.get(i);
			String mailAddressOfReceiver = vo.getEmail();
			sendEmail(em.getMailAddress(), mailAddressOfReceiver, "temp/network_and_host_report.xls");
		}
	}

	private void sendEmail(String fromAddress, String mailAddressOfReceiver, String fileNa) {
		SendMailManager mailManager = new SendMailManager();
		String fileName = ResourceCenter.getInstance().getSysPath() + fileNa;
		mailManager.SendMailWithFile(fromAddress, mailAddressOfReceiver, "设备报表", fileName);
	}
}