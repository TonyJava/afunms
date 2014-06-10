package com.afunms.alarm.send;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.afunms.alarm.model.AlarmWayDetail;
import com.afunms.common.util.FtpTool;
import com.afunms.common.util.SendMailManager;
import com.afunms.event.model.EventList;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.dao.AlertEmailDao;
import com.afunms.system.dao.FtpTransConfigDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.AlertEmail;
import com.afunms.system.model.FtpTransConfig;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class SendMailAlarm implements SendAlarm {
	private Logger logger = Logger.getLogger(this.getClass());

	public void sendAlarm(EventList eventList, AlarmWayDetail alarmWayDetail) {
		logger.info(" 发送邮件告警 ");
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
		String userids = alarmWayDetail.getUserIds();
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try {
			userList = userDao.findbyIDs(userids);
		} catch (Exception e) {

		} finally {
			userDao.close();
		}
		if (userList != null && userList.size() > 0) {
			java.text.SimpleDateFormat _sdf1 = new java.text.SimpleDateFormat("MM-dd HH:mm");
			for (int i = 0; i < userList.size(); i++) {
				User vo = (User) userList.get(i);
				String mailAddressOfReceiver = vo.getEmail();
				try {
					Date cc = eventList.getRecordtime().getTime();
					String recordtime = _sdf1.format(cc);
					if (em != null) {
						sendEmail(em.getMailAddress(), mailAddressOfReceiver, recordtime + " " + eventList.getContent());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 生成doc格式的文件
	 * 
	 * @author HONGLI
	 */
	public void BuildEventXMLDoc(EventList eventList) {
		if (eventList == null || eventList.getNodeid() == 0) {
			return;
		}
		FtpTransConfigDao ftpTransConfigDao = null;
		try {
			ftpTransConfigDao = new FtpTransConfigDao();
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} finally {
			if (ftpTransConfigDao != null) {
				ftpTransConfigDao.close();
			}
		}
		String subentity = eventList.getSubentity();
		if (subentity != null
				&& (subentity.trim().equalsIgnoreCase("cpu") || subentity.trim().equalsIgnoreCase("physicalmemory") || subentity.trim().equalsIgnoreCase("pagingusage")
						|| subentity.trim().equalsIgnoreCase("iowait") || subentity.trim().contains("errptlog") || subentity.trim().equalsIgnoreCase("swapmemory"))) {
			// 根节点添加到文档中；
			// 创建日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			HostNodeDao dao = null;
			PrintWriter out = null;
			try {
				dao = new HostNodeDao();
				HostNode host = (HostNode) dao.findByID(eventList.getNodeid() + "");
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
				// 不是aix的情况
				if (!nodedto.getSubtype().equalsIgnoreCase("aix")) {
					return;
				}
				// 设置文件名、文件夹路径
				String filename = host.getAlias() + "_" + eventList.getSubentity() + ".txt";
				String filepath = ResourceCenter.getInstance().getSysPath() + "ftpupload/";
				// 生成doc格式的文件
				File file = new File(filepath + filename);
				file.createNewFile();
				out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath())));
				out.println("time=" + sdf.format(eventList.getRecordtime().getTime()));
				out.println("value=2");
				out.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
				if (out != null) {
					out.close();
				}
			}
		}
	}

	/**
	 * 
	 * @param filepath
	 *            要传送的文件的所属文件夹路径
	 * @param filename
	 *            要传送的文件的文件名
	 * @return
	 */
	public Boolean ftpEventXml(FtpTransConfig ftpTransConfig, String filepath, String filename) {
		Boolean retflag = false;
		FtpTool ftpTool = new FtpTool();
		ftpTool.setIp(ftpTransConfig.getIp());
		ftpTool.setPort(21);// 端口
		ftpTool.setUser(ftpTransConfig.getUsername());
		ftpTool.setPwd(ftpTransConfig.getPassword());
		ftpTool.setRemotePath("/");// ftp文件夹
		try {
			ftpTool.uploadFile(ftpTool.getRemotePath(), filepath, filename);
			retflag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retflag;
	}

	public void sendAlarm(EventList eventList, String userids) {
		logger.info(" 发送邮件告警 ");
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
		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try {
			userList = userDao.findbyIDs(userids);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userDao.close();
		}
		if (userList != null && userList.size() > 0) {
			for (int i = 0; i < userList.size(); i++) {
				User vo = (User) userList.get(i);
				String mailAddressOfReceiver = vo.getEmail();
				try {
					sendEmail(em.getMailAddress(), mailAddressOfReceiver, eventList.getContent());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void sendEmail(String fromAddress, String mailAddressOfReceiver, String body) {
		SendMailManager mailManager = new SendMailManager();
		mailManager.SendMailNoFile(fromAddress, mailAddressOfReceiver, body);
	}
}
