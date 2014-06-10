package com.afunms.event.dao;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.afunms.application.dao.ApacheConfigDao;
import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DnsConfigDao;
import com.afunms.application.dao.DominoConfigDao;
import com.afunms.application.dao.EmailConfigDao;
import com.afunms.application.dao.FTPConfigDao;
import com.afunms.application.dao.GrapesConfigDao;
import com.afunms.application.dao.IISConfigDao;
import com.afunms.application.dao.JBossConfigDao;
import com.afunms.application.dao.MQConfigDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.dao.PSTypeDao;
import com.afunms.application.dao.PlotConfigDao;
import com.afunms.application.dao.RadarConfigDao;
import com.afunms.application.dao.TomcatDao;
import com.afunms.application.dao.WasConfigDao;
import com.afunms.application.dao.WebConfigDao;
import com.afunms.application.dao.WeblogicConfigDao;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.CicsConfig;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.DnsConfig;
import com.afunms.application.model.DominoConfig;
import com.afunms.application.model.EmailMonitorConfig;
import com.afunms.application.model.FTPConfig;
import com.afunms.application.model.GrapesConfig;
import com.afunms.application.model.IISConfig;
import com.afunms.application.model.JBossConfig;
import com.afunms.application.model.MQConfig;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.model.PSTypeVo;
import com.afunms.application.model.PlotConfig;
import com.afunms.application.model.RadarConfig;
import com.afunms.application.model.Tomcat;
import com.afunms.application.model.WasConfig;
import com.afunms.application.model.WebConfig;
import com.afunms.application.model.WeblogicConfig;
import com.afunms.common.util.SendMailManager;
import com.afunms.config.model.Portconfig;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.node.Host;
import com.afunms.system.dao.AlertInfoServerDao;
import com.afunms.system.dao.TimeShareConfigDao;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.AlertInfoServer;
import com.afunms.system.model.TimeShareConfig;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.model.ManageXml;
import com.sxmcc.bwzy.util.SendAlertSoap;

@SuppressWarnings("unchecked")
public class SmscontentDao {

	String timeFormat = "MM-dd HH:mm:ss";
	java.text.SimpleDateFormat timeFormatter = new java.text.SimpleDateFormat(timeFormat);

	public void createEvent(String eventtype, String id, String eventlocation, String content, int level1, String subtype, String subentity) {
		// 生成事件
		EventList eventlist = new EventList();
		eventlist.setEventtype(eventtype);
		eventlist.setContent(content);
		eventlist.setLevel1(level1);
		eventlist.setManagesign(0);
		eventlist.setBak("");
		eventlist.setRecordtime(Calendar.getInstance());
		eventlist.setReportman("系统轮询");
		String bids = ",";
		String realId = id.split(":")[0];
		if (subtype.equalsIgnoreCase("db")) {
			DBDao dbdao = new DBDao();
			DBVo dbvo = null;
			if (id.indexOf(":") != -1) {
				String[] ids = id.split(":");
				id = ids[0];
			}
			try {
				dbvo = (DBVo) dbdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbdao.close();
			}

			if (dbvo != null) {
				bids = bids + dbvo.getBid();
				eventlist.setEventlocation(dbvo.getDbName() + "(" + dbvo.getIpAddress() + ")");
			} else {
				bids = bids + dbvo.getBid();
				eventlist.setEventlocation(dbvo.getDbName() + "(" + dbvo.getIpAddress() + ")");
			}
			dbvo = null;
		} else if (subtype.equalsIgnoreCase("bus")) {
			ManageXmlDao manageXmlDao = new ManageXmlDao();
			ManageXml vo = null;
			try {
				vo = (ManageXml) manageXmlDao.findById(Integer.parseInt(id));
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				manageXmlDao.close();
			}
			bids = bids + vo.getBid();
			eventlist.setEventlocation(vo.getTopoName());
		} else if (subtype.equalsIgnoreCase("web")) {
			WebConfigDao urldao = new WebConfigDao();
			WebConfig url = null;
			try {
				url = (WebConfig) urldao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				urldao.close();
			}
			bids = bids + url.getNetid();
			eventlist.setEventlocation(url.getAlias());
		} else if (subtype.equalsIgnoreCase("mail")) {
			EmailConfigDao emailConfigDao = new EmailConfigDao();
			EmailMonitorConfig mailConf = null;
			try {
				mailConf = (EmailMonitorConfig) emailConfigDao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				emailConfigDao.close();
			}
			bids = bids + mailConf.getBid();
			eventlist.setEventlocation(mailConf.getName());
		} else if (subtype.equalsIgnoreCase("wasserver")) {
			WasConfig wasconfig = null;
			WasConfigDao dao = new WasConfigDao();
			try {
				wasconfig = (WasConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + wasconfig.getNetid();
			eventlist.setEventlocation(wasconfig.getName() + "(" + wasconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("grapes")) {
			GrapesConfig grapesconfig = null;
			GrapesConfigDao dao = new GrapesConfigDao();
			try {
				grapesconfig = (GrapesConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + grapesconfig.getNetid();
			eventlist.setEventlocation(grapesconfig.getName() + "(" + grapesconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("radar")) {
			RadarConfig radarconfig = null;
			RadarConfigDao dao = new RadarConfigDao();
			try {
				radarconfig = (RadarConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + radarconfig.getNetid();
			eventlist.setEventlocation(radarconfig.getName() + "(" + radarconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("plot")) {
			PlotConfig plotconfig = null;
			PlotConfigDao dao = new PlotConfigDao();
			try {
				plotconfig = (PlotConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + plotconfig.getNetid();
			eventlist.setEventlocation(plotconfig.getName() + "(" + plotconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("tomcat")) {
			TomcatDao tomcatdao = new TomcatDao();
			Tomcat tomcat = null;
			try {
				tomcat = (Tomcat) tomcatdao.findByID(id);
				bids = bids + tomcat.getBid();
				eventlist.setEventlocation(tomcat.getAlias());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tomcatdao.close();
			}

		} else if (subtype.equalsIgnoreCase("ftp")) {
			FTPConfigDao ftpdao = new FTPConfigDao();
			FTPConfig ftp = null;
			try {
				ftp = (FTPConfig) ftpdao.findByID(id);
				bids = bids + ftp.getBid();
				eventlist.setEventlocation(ftp.getName());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ftpdao.close();
			}

		} else if (subtype.equalsIgnoreCase("mq")) {
			MQConfigDao mqdao = new MQConfigDao();
			MQConfig mq = null;
			try {
				mq = (MQConfig) mqdao.findByID(id);
				bids = bids + mq.getNetid();
				eventlist.setEventlocation(mq.getName());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mqdao.close();
			}

		} else if (subtype.equalsIgnoreCase("domino")) {
			DominoConfigDao dominodao = new DominoConfigDao();
			DominoConfig domino = null;
			try {
				domino = (DominoConfig) dominodao.findByID(id);
				bids = bids + domino.getNetid();
				eventlist.setEventlocation(domino.getName());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dominodao.close();
			}

		} else if (subtype.equalsIgnoreCase("iis")) {
			IISConfigDao iisdao = new IISConfigDao();
			IISConfig iis = null;
			try {
				iis = (IISConfig) iisdao.findByID(id);
				bids = bids + iis.getNetid();
				eventlist.setEventlocation(iis.getName());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				iisdao.close();
			}
		} else if (subtype.equalsIgnoreCase("socket")) {
			PSTypeDao configdao = new PSTypeDao();
			PSTypeVo vo = null;
			try {
				vo = (PSTypeVo) configdao.findByID(id);
				bids = bids + vo.getBid();
				eventlist.setEventlocation(vo.getIpaddress() + ":" + vo.getPort());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
		} else if (subtype.equalsIgnoreCase("host") || subtype.equalsIgnoreCase("network") || subtype.equalsIgnoreCase("net")) {
			HostNodeDao nodedao = new HostNodeDao();
			HostNode vo = null;
			try {
				vo = (HostNode) nodedao.findByID(id);
				bids = bids + vo.getBid();
				eventlist.setEventlocation(vo.getAlias() + "(" + vo.getIpAddress() + ")");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				nodedao.close();
			}
		} else if (subtype.equalsIgnoreCase("cics")) {
			CicsConfigDao cicsConfigDao = new CicsConfigDao();
			CicsConfig cics = null;
			try {
				cics = (CicsConfig) cicsConfigDao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cicsConfigDao.close();
			}
			bids = bids + cics.getNetid();
			eventlist.setEventlocation(cics.getAlias());

		} else if (subtype.equalsIgnoreCase("weblogic")) {
			WeblogicConfigDao weblogicdao = new WeblogicConfigDao();
			WeblogicConfig weblogic = null;
			try {
				weblogic = (WeblogicConfig) weblogicdao.findByID(id);
				bids = bids + weblogic.getNetid();
				eventlist.setEventlocation(weblogic.getAlias() + "(" + weblogic.getIpAddress() + ")");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				weblogicdao.close();
			}
		} else if (subtype.equalsIgnoreCase("dns")) {
			DnsConfigDao dnsdao = new DnsConfigDao();
			DnsConfig dns = null;
			try {
				dns = (DnsConfig) dnsdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dnsdao.close();
			}
			bids = bids + dns.getNetid();
			eventlist.setEventlocation(dns.getHostip());
		} else if (subtype.equalsIgnoreCase("jboss")) {
			JBossConfigDao jbossdao = new JBossConfigDao();
			JBossConfig jboss = null;
			try {
				jboss = (JBossConfig) jbossdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				jbossdao.close();
			}
			bids = bids + jboss.getNetid();
			eventlist.setEventlocation(jboss.getAlias() + "(" + jboss.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("apache")) {
			ApacheConfigDao apachedao = new ApacheConfigDao();
			ApacheConfig apache = null;
			try {
				apache = (ApacheConfig) apachedao.findByID(id);
				bids = bids + apache.getNetid();
				eventlist.setEventlocation(apache.getAlias());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				apachedao.close();
			}

		}
		eventlist.setBusinessid(bids);
		eventlist.setNodeid(Integer.parseInt(realId));
		eventlist.setOid(0);
		eventlist.setSubtype(subtype);
		eventlist.setSubentity(subentity);
		EventListDao eventlistdao = new EventListDao();
		try {
			eventlistdao.save(eventlist);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventlistdao.close();
		}
	}

	public void createEventWithReasion(String eventtype, String id, String eventlocation, String content, int level1, String subtype, String subentity, String reasion) {
		// 生成事件
		EventList eventlist = new EventList();
		eventlist.setEventtype(eventtype);
		if (reasion != null && reasion.trim().length() > 0) {
			eventlist.setContent(content + "," + reasion);
		} else {
			eventlist.setContent(content);
		}

		eventlist.setLevel1(level1);
		eventlist.setManagesign(0);
		eventlist.setBak("");
		eventlist.setRecordtime(Calendar.getInstance());
		eventlist.setReportman("系统轮询");
		String bids = ",";
		String realId = id;
		OracleEntity ora = null;
		if (subtype.equalsIgnoreCase("db")) {
			if (id != null && id.indexOf(":") != -1) {
				String[] ids = id.split(":");
				id = ids[0];
				realId = ids[1];
				OraclePartsDao oracleDao = new OraclePartsDao();
				realId = ids[1];
				try {
					ora = (OracleEntity) oracleDao.findByID(ids[1]);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					oracleDao.close();
				}
			}
			DBDao dbdao = new DBDao();
			DBVo dbvo = null;
			try {
				dbvo = (DBVo) dbdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbdao.close();
			}
			if (ora != null) {
				bids = bids + ora.getBid();
				eventlist.setEventlocation(ora.getSid() + "(" + dbvo.getIpAddress() + ")");
			} else {
				bids = bids + dbvo.getBid();
				eventlist.setEventlocation(dbvo.getDbName() + "(" + dbvo.getIpAddress() + ")");
			}
			ora = null;
		} else if (subtype.equalsIgnoreCase("bus")) {
			ManageXmlDao manageXmlDao = new ManageXmlDao();
			ManageXml vo = null;
			try {
				vo = (ManageXml) manageXmlDao.findById(Integer.parseInt(id));
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				manageXmlDao.close();
			}
			bids = bids + vo.getBid();
			eventlist.setEventlocation(vo.getTopoName());
		} else if (subtype.equalsIgnoreCase("web")) {
			WebConfigDao urldao = new WebConfigDao();
			WebConfig url = null;
			try {
				url = (WebConfig) urldao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				urldao.close();
			}
			bids = bids + url.getNetid();
			eventlist.setEventlocation(url.getAlias());
		} else if (subtype.equalsIgnoreCase("mail")) {
			EmailConfigDao maildao = new EmailConfigDao();
			EmailMonitorConfig mail = null;
			try {
				mail = (EmailMonitorConfig) maildao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				maildao.close();
			}
			bids = bids + mail.getBid();
			eventlist.setEventlocation(mail.getName());
		} else if (subtype.equalsIgnoreCase("socket")) {
			PSTypeDao socketdao = new PSTypeDao();
			PSTypeVo socketvo = null;
			try {
				socketvo = (PSTypeVo) socketdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				socketdao.close();
			}
			bids = bids + socketvo.getBid();
			eventlist.setEventlocation(socketvo.getIpaddress() + ":" + socketvo.getPortdesc());
		} else if (subtype.equalsIgnoreCase("ftp")) {
			FTPConfigDao ftpdao = new FTPConfigDao();
			FTPConfig ftp = null;
			try {
				ftp = (FTPConfig) ftpdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ftpdao.close();
			}
			bids = bids + ftp.getBid();
			eventlist.setEventlocation(ftp.getName());
		} else if (subtype.equalsIgnoreCase("mq")) {
			MQConfigDao mqdao = new MQConfigDao();
			MQConfig mq = null;
			try {
				mq = (MQConfig) mqdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mqdao.close();
			}
			bids = bids + mq.getNetid();
			eventlist.setEventlocation(mq.getName());
		} else if (subtype.equalsIgnoreCase("domino")) {
			DominoConfigDao dominodao = new DominoConfigDao();
			DominoConfig domino = null;
			try {
				domino = (DominoConfig) dominodao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dominodao.close();
			}
			if (domino != null) {
				bids = bids + domino.getNetid();
				eventlist.setEventlocation(domino.getName());
			}
		} else if (subtype.equalsIgnoreCase("iis")) {
			IISConfigDao iisdao = new IISConfigDao();
			IISConfig iis = null;
			try {
				iis = (IISConfig) iisdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				iisdao.close();
			}
			if (iis != null) {
				bids = bids + iis.getNetid();
				eventlist.setEventlocation(iis.getName());
			}
		} else if (subtype.equalsIgnoreCase("grapes")) {
			GrapesConfig grapesconfig = null;
			GrapesConfigDao dao = new GrapesConfigDao();
			try {
				grapesconfig = (GrapesConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + grapesconfig.getNetid();
			eventlist.setEventlocation(grapesconfig.getName() + "(" + grapesconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("radar")) {
			RadarConfig radarconfig = null;
			RadarConfigDao dao = new RadarConfigDao();
			try {
				radarconfig = (RadarConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + radarconfig.getNetid();
			eventlist.setEventlocation(radarconfig.getName() + "(" + radarconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("plot")) {
			PlotConfig plotconfig = null;
			PlotConfigDao dao = new PlotConfigDao();
			try {
				plotconfig = (PlotConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + plotconfig.getNetid();
			eventlist.setEventlocation(plotconfig.getName() + "(" + plotconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("tomcat")) {
			TomcatDao tomcatdao = new TomcatDao();
			Tomcat tomcat = null;
			try {
				tomcat = (Tomcat) tomcatdao.findByID(id);
				bids = bids + tomcat.getBid();
				eventlist.setEventlocation(tomcat.getAlias());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tomcatdao.close();
			}

		} else if (subtype.equalsIgnoreCase("weblogicDomain")) {
			WeblogicConfigDao weblogicdao = new WeblogicConfigDao();
			WeblogicConfig url = null;
			try {
				url = (WeblogicConfig) weblogicdao.findByID(id);
				bids = bids + url.getNetid();
				eventlist.setEventlocation(url.getAlias() + "(" + url.getIpAddress() + ")");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				weblogicdao.close();
			}
		} else if (subtype.equalsIgnoreCase("weblogicServer")) {
			WeblogicConfigDao wserverdao = new WeblogicConfigDao();
			WeblogicConfig weblogic = null;
			try {
				weblogic = (WeblogicConfig) wserverdao.findByID(id);
				bids = bids + weblogic.getNetid();
				eventlist.setEventlocation(weblogic.getAlias() + "(" + weblogic.getIpAddress() + ")");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wserverdao.close();
			}
		} else if (subtype.equalsIgnoreCase("wasserver")) {
			WasConfig wasconfig = null;
			WasConfigDao dao = new WasConfigDao();
			try {
				wasconfig = (WasConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + wasconfig.getNetid();
			eventlist.setEventlocation(wasconfig.getName() + "(" + wasconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("jboss")) {
			JBossConfig jbossconfig = null;
			JBossConfigDao dao = new JBossConfigDao();
			try {
				jbossconfig = (JBossConfig) dao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			bids = bids + jbossconfig.getNetid();
			eventlist.setEventlocation(jbossconfig.getAlias() + "(" + jbossconfig.getIpaddress() + ")");
		} else if (subtype.equalsIgnoreCase("dns")) {
			DnsConfigDao dnsdao = new DnsConfigDao();
			DnsConfig dns = null;
			try {
				dns = (DnsConfig) dnsdao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dnsdao.close();
			}
			bids = bids + dns.getNetid();
			eventlist.setEventlocation(dns.getHostip());
		} else if (subtype.equalsIgnoreCase("apache")) {
			ApacheConfigDao apachedao = new ApacheConfigDao();
			ApacheConfig apache = null;
			try {
				apache = (ApacheConfig) apachedao.findByID(id);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				apachedao.close();
			}
			bids = bids + apache.getNetid();
			eventlist.setEventlocation(apache.getAlias() + "(" + apache.getIpaddress() + ")");
		}
		eventlist.setBusinessid(bids);
		eventlist.setNodeid(Integer.parseInt(realId));
		eventlist.setOid(0);
		eventlist.setSubtype(subtype);
		eventlist.setSubentity(subentity);
		EventListDao eventlistdao = new EventListDao();
		try {
			eventlistdao.save(eventlist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventlistdao.close();
		}
	}

	private void refreshLinkState(Host host, String index, String value) {
		List linkList = PollingEngine.getInstance().getLinkList();
		if (linkList != null && linkList.size() > 0) {
			for (int j = 0; j < linkList.size(); j++) {
				LinkRoad lr = (LinkRoad) linkList.get(j);
				Host host1 = (Host) PollingEngine.getInstance().getNodeByID(lr.getStartId());
				Host host2 = (Host) PollingEngine.getInstance().getNodeByID(lr.getEndId());
				if (host.getId() == host1.getId() && index.equalsIgnoreCase(lr.getStartIndex())) {
					if ("down".equalsIgnoreCase(value)) {
						lr.setAlarm(true);
						lr.setLevels(2);
						lr.setStarOper("down");
					} else {
						lr.setAlarm(false);
						lr.setLevels(0);
						lr.setStarOper("up");
					}
				}
				if (host.getId() == host2.getId() && index.equalsIgnoreCase(lr.getEndIndex())) {
					if ("down".equalsIgnoreCase(value)) {
						lr.setAlarm(true);
						lr.setLevels(2);
						lr.setEndOper("down");
					} else {
						lr.setAlarm(false);
						lr.setLevels(0);
						lr.setEndOper("up");
					}
				}
			}
		}
	}

	// 数据库的短信发送
	public boolean sendDatabaseSmscontent(Smscontent smscontent) throws Exception {
		DBDao dbdao = new DBDao();
		UserDao userdao = new UserDao();
		try {
			Calendar c = Calendar.getInstance();
			smscontent.setSendtime(c);
			String content = smscontent.getMessage();
			String mailopers = "";
			DBVo dbmonitorlist = null;
			String id = "";
			dbmonitorlist = (DBVo) dbdao.findByID(smscontent.getObjid().split(":")[0]);
			String smstime = smscontent.getRecordtime().substring(5);
			String endcontent = smstime + " " + content;
			String IP = "";
			try {
				mailopers = dbmonitorlist.getSendemail();
				IP = dbmonitorlist.getIpAddress();
				id = smscontent.getObjid();
				if (mailopers != null && mailopers.trim().length() > 0) {
					if (mailopers.indexOf(",") >= 0) {
						String[] ids = mailopers.split(",");
						if (ids != null && ids.length > 0) {
							for (int j = 0; j < ids.length; j++) {
								String oid = ids[j];
								User op = (User) userdao.findByID(oid);
								if (op == null) {
									continue;
								}
								if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
									// 发送邮件
									SendMailManager sendmailmanager = new SendMailManager();
									boolean flag = false;
									try {
										flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
									} catch (Exception e) {
										e.printStackTrace();
									}
									if (flag == false) {
										try {
											createEvent("poll", id, dbmonitorlist.getAlias() + "(" + dbmonitorlist.getIpAddress() + ")", smscontent.getMessage() + "告警信息不能发送邮件到"
													+ op.getEmail(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
										} catch (Exception e) {
											e.printStackTrace();
										}

									}
								}
							}
						}
					} else {
						User op = (User) userdao.findByID(mailopers);
						if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
							SendMailManager sendmailmanager = new SendMailManager();
							boolean flag = false;
							try {
								flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (flag == false) {
								try {
									createEvent("poll", id, dbmonitorlist.getAlias() + "(" + dbmonitorlist.getIpAddress() + ")", smscontent.getMessage() + "告警信息不能发送邮件到"
											+ op.getEmail(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				/*
				 * lijun modify begin;
				 */
				// 开始发送短信
				String objectType = smscontent.getSubtype();
				String objectId = smscontent.getObjid();
				List list = null;
				TimeShareConfigDao configdao = new TimeShareConfigDao();
				try {
					list = configdao.getTimeShareConfigByObject(objectId, objectType);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
				try {
					sentDetailSMS(list, endcontent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 向声音告警表里写数据
				AlarmInfo alarminfo = new AlarmInfo();
				alarminfo.setContent(endcontent);
				alarminfo.setIpaddress(IP);
				alarminfo.setLevel1(new Integer(smscontent.getLevel()));
				alarminfo.setRecordtime(Calendar.getInstance());
				alarminfo.setType("");
				AlarmInfoDao alarmdao = new AlarmInfoDao();
				try {
					alarmdao.save(alarminfo);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					alarmdao.close();
				}

			} catch (Throwable error) {
				error.printStackTrace();
			}

			// 开始写事件
			try {
				createEvent("poll", id, dbmonitorlist.getAlias() + "(" + dbmonitorlist.getIpAddress() + ")", smscontent.getMessage(), Integer.parseInt(smscontent.getLevel()),
						smscontent.getSubtype(), smscontent.getSubentity());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			dbdao.close();
			userdao.close();

		}
		return true;
	}

	// Grapes的短信发送
	public boolean sendGrapesSmscontent(Smscontent smscontent) throws Exception {
		UserDao userdao = new UserDao();
		try {
			Calendar c = Calendar.getInstance();
			smscontent.setSendtime(c);
			String content = smscontent.getMessage();
			String opers = "";
			String mailopers = "";

			GrapesConfig grapesconfig = new GrapesConfig();
			GrapesConfigDao dao = new GrapesConfigDao();
			try {
				grapesconfig = (GrapesConfig) dao.findByID(smscontent.getObjid());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			// dao.close();
			opers = grapesconfig.getSendmobiles();
			mailopers = grapesconfig.getSendemail();
			String smstime = smscontent.getRecordtime().substring(5);

			String endcontent = smstime + " " + content;

			if (opers != null && opers.trim().length() > 0) {
				if (opers.indexOf(",") >= 0) {
					String[] ids = opers.split(",");
					String mobiles[] = new String[ids.length];
					if (ids != null && ids.length > 0) {
						for (int j = 0; j < ids.length; j++) {
							String oid = ids[j];
							User op = (User) userdao.findByID(oid);
							if (op == null) {
								continue;
							}
							if (op.getMobile() != null && op.getMobile().trim().length() > 0) {
								mobiles[j] = op.getMobile();
								try {
									String soapuri = "http://10.204.16.246:8081";
									SendAlertSoap.sendSMS(soapuri, "0019", "mobileidc", op.getMobile(), "", endcontent, "", "");
								} catch (Exception e) {
								}
							}
						}
					}
				} else {
					User op = (User) userdao.findByID(opers);
					if (op.getMobile() != null && op.getMobile().trim().length() > 0) {
						try {
							String soapuri = "http://10.204.16.246:8081";
							SendAlertSoap.sendSMS(soapuri, "0019", "mobileidc", op.getMobile(), "", endcontent, "", "");
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}
			}
			if (mailopers != null && mailopers.trim().length() > 0) {
				if (mailopers.indexOf(",") >= 0) {
					String[] ids = mailopers.split(",");
					if (ids != null && ids.length > 0) {
						for (int j = 0; j < ids.length; j++) {
							String oid = ids[j];
							User op = (User) userdao.findByID(oid);
							if (op == null) {
								continue;
							}
							if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
								SendMailManager sendmailmanager = new SendMailManager();
								boolean flag = false;
								try {
									flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (flag == false) {
									try {
										createEvent("poll", smscontent.getObjid(), grapesconfig.getName() + "(" + grapesconfig.getIpaddress() + ")", smscontent.getMessage()
												+ "告警信息不能发送邮件到" + op.getEmail(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							}
						}
					}
				} else {
					User op = (User) userdao.findByID(mailopers);
					if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
						SendMailManager sendmailmanager = new SendMailManager();
						boolean flag = false;
						try {
							flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (flag == false) {
							try {
								createEvent("poll", smscontent.getObjid(), grapesconfig.getName() + "(" + grapesconfig.getIpaddress() + ")", smscontent.getMessage()
										+ "告警信息不能发送邮件到" + op.getEmail(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				}
			}
			try {
				createEvent("poll", smscontent.getObjid(), grapesconfig.getName() + "(" + grapesconfig.getIpaddress() + ")", smscontent.getMessage(), Integer.parseInt(smscontent
						.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			userdao.close();
			System.gc();
		}
		return true;
	}

	// Plot的短信发送
	public boolean sendPlotSmscontent(Smscontent smscontent) throws Exception {
		UserDao userdao = new UserDao();
		try {
			Calendar c = Calendar.getInstance();
			smscontent.setSendtime(c);
			String content = smscontent.getMessage();
			String opers = "";
			String mailopers = "";

			PlotConfig plotconfig = new PlotConfig();
			PlotConfigDao dao = new PlotConfigDao();
			try {
				plotconfig = (PlotConfig) dao.findByID(smscontent.getObjid());
			} catch (Exception e) {

			} finally {
				dao.close();
			}

			opers = plotconfig.getSendmobiles();
			mailopers = plotconfig.getSendemail();
			String smstime = smscontent.getRecordtime().substring(5);

			String endcontent = smstime + " " + content;
			if (opers != null && opers.trim().length() > 0) {
				if (opers.indexOf(",") >= 0) {
					String[] ids = opers.split(",");
					String mobiles[] = new String[ids.length];
					if (ids != null && ids.length > 0) {
						for (int j = 0; j < ids.length; j++) {
							String oid = ids[j];
							User op = (User) userdao.findByID(oid);
							if (op == null) {
								continue;
							}
							if (op.getMobile() != null && op.getMobile().trim().length() > 0) {
								mobiles[j] = op.getMobile();
								try {
									String soapuri = "http://10.204.16.246:8081";
									SendAlertSoap.sendSMS(soapuri, "0019", "mobileidc", op.getMobile(), "", endcontent, "", "");
								} catch (Exception e) {
								}
							}
						}
					}
				} else {
					User op = (User) userdao.findByID(opers);
					if (op.getMobile() != null && op.getMobile().trim().length() > 0) {
						try {
							String soapuri = "http://10.204.16.246:8081";
							SendAlertSoap.sendSMS(soapuri, "0019", "mobileidc", op.getMobile(), "", endcontent, "", "");
						} catch (Exception e) {
						}
					}
				}
			}
			if (mailopers != null && mailopers.trim().length() > 0) {
				if (mailopers.indexOf(",") >= 0) {
					String[] ids = mailopers.split(",");
					if (ids != null && ids.length > 0) {
						for (int j = 0; j < ids.length; j++) {
							String oid = ids[j];
							User op = (User) userdao.findByID(oid);
							if (op == null) {
								continue;
							}
							if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
								// 发送邮件
								SendMailManager sendmailmanager = new SendMailManager();
								boolean flag = false;
								try {
									flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (flag == false) {
									try {
										createEvent("poll", smscontent.getObjid(), plotconfig.getName() + "(" + plotconfig.getIpaddress() + ")", smscontent.getMessage()
												+ "告警信息不能发送邮件到" + op.getEmail(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							}
						}
					}
				} else {
					User op = (User) userdao.findByID(mailopers);
					if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
						SendMailManager sendmailmanager = new SendMailManager();
						boolean flag = false;
						try {
							flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (flag == false) {
							// 发送邮件失败,写事件
							try {
								createEvent("poll", smscontent.getObjid(), plotconfig.getName() + "(" + plotconfig.getIpaddress() + ")", smscontent.getMessage() + "告警信息不能发送邮件到"
										+ op.getEmail(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}
				}
			}
			try {
				createEvent("poll", smscontent.getObjid(), plotconfig.getName() + "(" + plotconfig.getIpaddress() + ")", smscontent.getMessage(), Integer.parseInt(smscontent
						.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			userdao.close();
			System.gc();
		}
		return true;
	}

	// Radar的短信发送
	public boolean sendRadarSmscontent(Smscontent smscontent) throws Exception {
		UserDao userdao = new UserDao();
		try {
			Calendar c = Calendar.getInstance();
			smscontent.setSendtime(c);
			String content = smscontent.getMessage();
			String opers = "";
			String mailopers = "";

			RadarConfig radarconfig = new RadarConfig();
			RadarConfigDao dao = new RadarConfigDao();
			radarconfig = (RadarConfig) dao.findByID(smscontent.getObjid());
			dao.close();
			opers = radarconfig.getSendmobiles();
			mailopers = radarconfig.getSendemail();
			String smstime = smscontent.getRecordtime().substring(5);

			String endcontent = smstime + " " + content;

			if (opers != null && opers.trim().length() > 0) {
				if (opers.indexOf(",") >= 0) {
					String[] ids = opers.split(",");
					String mobiles[] = new String[ids.length];
					if (ids != null && ids.length > 0) {
						for (int j = 0; j < ids.length; j++) {
							String oid = ids[j];
							User op = (User) userdao.findByID(oid);
							if (op == null) {
								continue;
							}
							if (op.getMobile() != null && op.getMobile().trim().length() > 0) {
								mobiles[j] = op.getMobile();
								try {
									String soapuri = "http://10.204.16.246:8081";
									SendAlertSoap.sendSMS(soapuri, "0019", "mobileidc", op.getMobile(), "", endcontent, "", "");
								} catch (Exception e) {
								}
							}
						}

					}
				} else {
					User op = (User) userdao.findByID(opers);
					if (op.getMobile() != null && op.getMobile().trim().length() > 0) {
						try {
							String soapuri = "http://10.204.16.246:8081";
							SendAlertSoap.sendSMS(soapuri, "0019", "mobileidc", op.getMobile(), "", endcontent, "", "");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			if (mailopers != null && mailopers.trim().length() > 0) {
				if (mailopers.indexOf(",") >= 0) {
					String[] ids = mailopers.split(",");
					if (ids != null && ids.length > 0) {
						for (int j = 0; j < ids.length; j++) {
							String oid = ids[j];
							User op = (User) userdao.findByID(oid);
							if (op == null) {
								continue;
							}
							if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
								// 发送邮件
								SendMailManager sendmailmanager = new SendMailManager();
								boolean flag = false;
								try {
									flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (flag == false) {
									try {
										createEvent("poll", smscontent.getObjid(), radarconfig.getName() + "(" + radarconfig.getIpaddress() + ")", smscontent.getMessage()
												+ "告警信息不能发送邮件到" + op.getEmail(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							}
						}
					}
				} else {
					User op = (User) userdao.findByID(mailopers);
					if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
						SendMailManager sendmailmanager = new SendMailManager();
						boolean flag = false;
						try {
							flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (flag == false) {
							try {
								createEvent("poll", smscontent.getObjid(), radarconfig.getName() + "(" + radarconfig.getIpaddress() + ")", smscontent.getMessage() + "告警信息不能发送邮件到"
										+ op.getEmail(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			try {
				createEvent("poll", smscontent.getObjid(), radarconfig.getName() + "(" + radarconfig.getIpaddress() + ")", smscontent.getMessage(), Integer.parseInt(smscontent
						.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			userdao.close();
			System.gc();
		}
		return true;
	}

	public boolean sendSms(String time, Host host, Portconfig portconfig, String value) {
		String smscontent = time + " " + host.getAlias() + "(" + host.getIpAddress() + ")" + portconfig.getLinkuse() + "(第" + portconfig.getPortindex() + "号)端口状态改变为" + value;
		try {
			refreshLinkState(host, portconfig.getPortindex() + "", value);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 生成事件
		EventList eventlist = new EventList();
		eventlist.setEventtype("trap");
		eventlist.setEventlocation(host.getAlias() + "(IP:" + host.getIpAddress() + ")");
		eventlist.setContent(smscontent);
		if (portconfig.getAlarmlevel() != null) {
			eventlist.setLevel1(new Integer(portconfig.getAlarmlevel()));
		} else {
			eventlist.setLevel1(new Integer("1"));
		}
		eventlist.setLevel1(3);
		eventlist.setManagesign(0);
		eventlist.setBak("");
		eventlist.setRecordtime(Calendar.getInstance());
		eventlist.setReportman("Trap");
		eventlist.setBusinessid(host.getBid());
		eventlist.setNodeid(host.getId());
		eventlist.setOid(0);
		eventlist.setSubtype("net");
		eventlist.setSubentity("interface");
		EventListDao eventlistdao = new EventListDao();
		eventlistdao.save(eventlist);
		return true;
	}

	// 短信发送
	public boolean sendURLSmscontent(Smscontent smscontent) throws Exception {
		UserDao userdao = new UserDao();
		try {
			Calendar c = Calendar.getInstance();
			smscontent.setSendtime(c);
			String url_id = smscontent.getObjid();
			String smstime = smscontent.getRecordtime().substring(5);
			String mailopers = "";
			WebConfig urlConf = null;
			FTPConfig ftpConf = null;
			EmailMonitorConfig mailConf = null;
			MQConfig mqconf = null;
			DominoConfig dominoconf = null;
			JBossConfig jbossconf = null;
			Tomcat tomcat = null;
			IISConfig iisconfig = null;
			WeblogicConfig weblogicconf = null;
			DBVo dbmonitorlist = null;
			PSTypeVo socketconfig = null;
			HostNode hostnode = null;
			String urlstr = "";
			String objectType = "";
			String objectId = url_id + "";
			if (smscontent.getSubtype().equalsIgnoreCase("web")) {
				WebConfigDao configdao = new WebConfigDao();
				try {
					urlConf = (WebConfig) configdao.findByID(url_id);
					mailopers = urlConf.getSendemail();
					objectType = "webservice";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configdao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("db")) {
				DBDao dbdao = new DBDao();
				try {
					dbmonitorlist = (DBVo) dbdao.findByID(url_id);
					if (dbmonitorlist == null) {
						OraclePartsDao partdao = new OraclePartsDao();
						OracleEntity oracleEntity = null;
						try {
							oracleEntity = (OracleEntity) partdao.findByID(url_id);
						} catch (Exception e) {

						} finally {
							partdao.close();
						}
						dbmonitorlist = (DBVo) dbdao.findByID(oracleEntity.getDbid() + "");
						urlstr = dbmonitorlist.getDbName();
						mailopers = dbmonitorlist.getSendemail();
						objectType = "db";
						objectId = dbmonitorlist.getId() + ":" + url_id;
					} else {
						urlstr = dbmonitorlist.getDbName();
						mailopers = dbmonitorlist.getSendemail();
						objectType = "db";
						objectId = url_id + "";
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbdao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("cics")) {
				CicsConfigDao cicsConfigDao = new CicsConfigDao();
				try {
					urlstr = ftpConf.getName();
					mailopers = ftpConf.getSendemail();
					objectType = "cics";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					cicsConfigDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("ftp")) {
				FTPConfigDao ftpConfigDao = new FTPConfigDao();
				try {
					ftpConf = (FTPConfig) ftpConfigDao.findByID(url_id);
					urlstr = ftpConf.getName();
					mailopers = ftpConf.getSendemail();
					objectType = "ftpservice";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					ftpConfigDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("mail")) {
				EmailConfigDao emailConfigDao = new EmailConfigDao();
				try {
					mailConf = (EmailMonitorConfig) emailConfigDao.findByID(url_id);
					urlstr = mailConf.getName();
					mailopers = mailConf.getSendemail();
					objectType = "emailservice";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					emailConfigDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("mq")) {
				MQConfigDao mqConfigDao = new MQConfigDao();
				try {
					mqconf = (MQConfig) mqConfigDao.findByID(url_id);
					urlstr = mqconf.getName();
					mailopers = mqconf.getSendemail();
					objectType = "mq";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mqConfigDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("domino")) {
				DominoConfigDao configDao = new DominoConfigDao();
				try {
					dominoconf = (DominoConfig) configDao.findByID(url_id);
					urlstr = dominoconf.getName();
					mailopers = dominoconf.getSendemail();
					objectType = "domino";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("jboss")) {
				JBossConfigDao configDao = new JBossConfigDao();
				try {
					jbossconf = (JBossConfig) configDao.findByID(url_id);
					urlstr = jbossconf.getAlias();
					mailopers = jbossconf.getSendemail();
					objectType = "jboss";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("iis")) {
				IISConfigDao configDao = new IISConfigDao();
				try {
					iisconfig = (IISConfig) configDao.findByID(url_id);
					urlstr = iisconfig.getName();
					mailopers = iisconfig.getSendemail();
					objectType = "iis";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("socket")) {
				PSTypeDao configDao = new PSTypeDao();
				try {
					socketconfig = (PSTypeVo) configDao.findByID(url_id);
					urlstr = socketconfig.getIpaddress();
					mailopers = socketconfig.getSendemail();
					objectType = "portservice";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("weblogic")) {
				WeblogicConfigDao weblogicConfigDao = new WeblogicConfigDao();
				try {
					weblogicconf = (WeblogicConfig) weblogicConfigDao.findByID(url_id);
					urlstr = weblogicconf.getAlias();
					mailopers = weblogicconf.getSendemail();
					objectType = "weblogic";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					weblogicConfigDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("tomcat")) {
				TomcatDao tomcatDao = new TomcatDao();
				try {
					tomcat = (Tomcat) tomcatDao.findByID(url_id);
					urlstr = tomcat.getAlias();
					mailopers = tomcat.getSendemail();
					objectType = "tomcat";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					tomcatDao.close();
				}
			} else if (smscontent.getSubtype().equalsIgnoreCase("host") || smscontent.getSubtype().equalsIgnoreCase("network") || smscontent.getSubtype().equalsIgnoreCase("net")) {
				HostNodeDao nodedao = new HostNodeDao();
				try {
					hostnode = (HostNode) nodedao.findByID(url_id);
					urlstr = hostnode.getAlias();
					mailopers = hostnode.getSendemail();
					objectType = "equipment";
					objectId = url_id + "";
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					nodedao.close();
				}

			} else if (smscontent.getSubtype().equalsIgnoreCase("wasserver")) {
				objectType = "was";
				objectId = url_id + "";
			}
			String endcontent = smstime + " " + smscontent.getMessage();

			// 开始写事件
			String sysLocation = "";
			try {
				createEvent("poll", objectId, sysLocation, smscontent.getMessage(), Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
			} catch (Exception e) {
				e.printStackTrace();
			}

			List list = null;
			TimeShareConfigDao configdao = new TimeShareConfigDao();
			try {
				list = configdao.getTimeShareConfigByObject(url_id, objectType);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				configdao.close();
			}
			try {
				sentDetailSMS(list, endcontent);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 向声音告警表里写数据
			AlarmInfo alarminfo = new AlarmInfo();
			alarminfo.setContent(endcontent);
			alarminfo.setIpaddress(urlstr);
			alarminfo.setLevel1(new Integer(2));
			alarminfo.setRecordtime(Calendar.getInstance());
			alarminfo.setType("");
			AlarmInfoDao alarmdao = new AlarmInfoDao();
			try {
				alarmdao.save(alarminfo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				alarmdao.close();
			}

			if (mailopers != null && mailopers.trim().length() > 0) {
				if (mailopers.indexOf(",") >= 0) {
					String[] ids = mailopers.split(",");
					if (ids != null && ids.length > 0) {
						for (int j = 0; j < ids.length; j++) {
							String oid = ids[j];
							User op = (User) userdao.findByID(oid);
							if (op == null) {
								continue;
							}
							if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
								// 发送邮件
								SendMailManager sendmailmanager = new SendMailManager();
								boolean flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
								if (flag == false) {
									try {
										createEvent("poll", smscontent.getObjid(), "", smscontent.getMessage() + "告警信息不能发送邮件到" + op.getEmail(), Integer.parseInt(smscontent
												.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
				} else {
					User op = (User) userdao.findByID(mailopers);
					if (op != null) {
						if (op.getEmail() != null && op.getEmail().trim().length() > 0) {
							SendMailManager sendmailmanager = new SendMailManager();
							boolean flag = sendmailmanager.SendMail(op.getEmail(), endcontent);
							if (flag == false) {
								try {
									createEvent("poll", smscontent.getObjid(), "", smscontent.getMessage() + "告警信息不能发送邮件到" + op.getEmail(),
											Integer.parseInt(smscontent.getLevel()), smscontent.getSubtype(), smscontent.getSubentity());
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			System.gc();
			try {
				userdao.close();
			} catch (Exception ex) {
			}
		}
		return true;
	}

	public void sentDetailSMS(List list, String message) {

		if (list != null) {
			Iterator iterator = list.iterator();
			Calendar calendar = Calendar.getInstance();
			Date date = new Date();
			calendar.setTime(date);
			Calendar beginCalendar = Calendar.getInstance();
			beginCalendar.setTime(date);
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.setTime(date);
			while (iterator.hasNext()) {

				TimeShareConfig smsConfig = (TimeShareConfig) iterator.next();
				int beginHour = Integer.parseInt(smsConfig.getBeginTime());
				int endHour = Integer.parseInt(smsConfig.getEndTime());
				if (beginHour == 24) {
					beginCalendar.set(Calendar.HOUR_OF_DAY, 23);
					beginCalendar.set(Calendar.MINUTE, Integer.parseInt("59"));
				} else {
					beginCalendar.set(Calendar.HOUR_OF_DAY, beginHour);
					beginCalendar.set(Calendar.MINUTE, Integer.parseInt("0"));
				}
				if (endHour == 24) {
					endCalendar.set(Calendar.HOUR_OF_DAY, 23);
					endCalendar.set(Calendar.MINUTE, Integer.parseInt("59"));
				} else {
					endCalendar.set(Calendar.HOUR_OF_DAY, endHour);
					endCalendar.set(Calendar.MINUTE, Integer.parseInt("0"));
				}
				if ((calendar.after(beginCalendar) && calendar.before(endCalendar)) || (calendar.equals(beginCalendar)) || (calendar.equals(endCalendar))) {

					String[] ids = smsConfig.getUserIds().split(",");
					if (ids != null && ids.length > 0) {

						for (int j = 0; j < ids.length; j++) {

							String oid = ids[j];
							User op = null;
							UserDao userdao = new UserDao();
							try {
								op = (User) userdao.findByID(oid);
							} catch (Exception ex) {
								ex.printStackTrace();
							} finally {
								userdao.close();
							}
							if (op == null) {
								continue;
							}
							try {
								String info = "1&&" + op.getUserid() + "&&" + message + "\n";
								Socket socket = null;
								List alertserverlist = new ArrayList();
								AlertInfoServerDao alertserverdao = new AlertInfoServerDao();
								try {
									alertserverlist = alertserverdao.getByFlage(1);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									alertserverdao.close();
								}
								if (alertserverlist != null && alertserverlist.size() > 0) {
									// 设置了信息服务器
									AlertInfoServer vo = (AlertInfoServer) alertserverlist.get(0);
									try {
										socket = new Socket(vo.getIpaddress(), Integer.parseInt(vo.getPort()));
										java.io.OutputStream out = socket.getOutputStream();

										byte[] data = info.getBytes();
										out.write(data);
										out.flush();
									} catch (UnknownHostException e) {
										e.printStackTrace();
									} catch (IOException e) {
										e.printStackTrace();
									} finally {
										try {
											if (socket != null) {
												socket.close();
											}
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

}
