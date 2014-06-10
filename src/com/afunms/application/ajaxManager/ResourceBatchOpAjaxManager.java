package com.afunms.application.ajaxManager;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.common.base.AjaxBaseManager;
import com.afunms.common.base.AjaxManagerInterface;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.event.dao.NetSyslogRuleDao;
import com.afunms.event.model.NetSyslogRule;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.initialize.ResourceCenter;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.IpResourceReport;
import com.afunms.polling.node.Host;
import com.afunms.polling.task.UpdateXmlTask;
import com.afunms.report.abstraction.ExcelReport1;
import com.afunms.report.base.AbstractionReport1;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.dao.NetSyslogNodeRuleDao;
import com.afunms.topology.model.HostNode;
import com.afunms.topology.util.TopoHelper;

public class ResourceBatchOpAjaxManager extends AjaxBaseManager implements AjaxManagerInterface {

	private String uploadFileName = new String();

	@Override
	public void execute(String action) {
		if (action.equals("resourceBatchAdd")) {
			resourceBatchAdd();
		} else if (action.equals("upLoadFile")) {
			upLoadFile();
		} else if (action.equals("downLoadFile")) {
			downLoadFile();
		}
	}

	private void downLoadFile() {
		Hashtable reporthash = new Hashtable();
		AbstractionReport1 report = new ExcelReport1(new IpResourceReport(), reporthash);
		report.createReport_networklist("/download/networklist_report.xls");
		out.print("/download/networklist_report.xls");
		out.flush();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void upLoadFile() {
		synchronized (uploadFileName) {
			try {
				request.setCharacterEncoding("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			String folderPath = ResourceCenter.getInstance().getSysPath() + "/upload/";
			File folder = new File(folderPath);
			if (!folder.exists()) {
				folder.mkdirs();
			}
			DiskFileItemFactory fac = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(fac);
			upload.setHeaderEncoding("utf-8");
			List fileList = null;
			try {
				fileList = upload.parseRequest(request);
			} catch (FileUploadException ex) {
				ex.printStackTrace();
				return;
			}
			Iterator<FileItem> it = fileList.iterator();
			String name = "";
			while (it.hasNext()) {
				FileItem item = it.next();
				if (!item.isFormField()) {
					name = item.getName();
					if (name == null || name.trim().equals("")) {
						continue;
					}
					File saveFile = new File(folderPath + name);
					try {
						item.write(saveFile);
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			uploadFileName = name;
		}
	}

	private void resourceBatchAdd() {
		List nodeList = new ArrayList();
		StringBuffer sb = new StringBuffer("操作完成");
		String fileName = ResourceCenter.getInstance().getSysPath() + "/upload/" + uploadFileName;
		try {
			try {
				nodeList = readXls(fileName);
			} catch (Exception e) {
				e.printStackTrace();
				sb.append(",读取文件失败");
			}
			if (nodeList != null && nodeList.size() > 0) {
				for (int k = 0; k < nodeList.size(); k++) {
					try {
						HostNode hostNode = (HostNode) nodeList.get(k);
						String assetid = "";// 设备资产编号
						String location = "";// 机房位置
						String ipAddress = hostNode.getIpAddress();
						String alias = hostNode.getAlias();
						int snmpversion = hostNode.getSnmpversion();
						String community = hostNode.getCommunity();
						String writecommunity = hostNode.getWriteCommunity();

						int securityLevel = hostNode.getSecuritylevel(); // 安全级别
						String securityName = hostNode.getSecurityName(); // 用户名
						int v3_ap = hostNode.getV3_ap(); // 认证协议 1:MD5 2:SHA
						String authPassPhrase = hostNode.getAuthpassphrase(); // 通行码
						int v3_privacy = hostNode.getV3_privacy();
						// 加密协议 1:DES
						// 2:AES128
						// 3:AES196
						// 4:AES256
						String privacyPassPhrase = hostNode.getPrivacyPassphrase(); // 加密协议码

						boolean managed = hostNode.isManaged();
						int type = hostNode.getCategory();
						int transfer = 0;

						int osType = 0;
						String bid = "";
						String sendmobiles = "";
						String sendemail = "";
						String sendphone = "";
						int supperid = -1;
						int collectType = 0;
						try {
							osType = hostNode.getOstype();
							collectType = hostNode.getCollecttype();
							bid = hostNode.getBid();
						} catch (Exception e) {
							e.printStackTrace();
						}
						TopoHelper helper = new TopoHelper(); // 包括更新数据库和更新内存
						int addResult = 0;
						addResult = helper.addHost(assetid, location, ipAddress, alias, snmpversion, community, writecommunity, transfer, type, osType, collectType, bid, sendmobiles, sendemail,
								sendphone, supperid, managed, securityLevel, securityName, v3_ap, authPassPhrase, v3_privacy, privacyPassPhrase);
						if (addResult > 0) {
							NetSyslogNodeRuleDao netruledao = new NetSyslogNodeRuleDao();
							NetSyslogRuleDao ruledao = new NetSyslogRuleDao();
							try {
								String strFacility = "";
								List rulelist = new ArrayList();
								try {
									rulelist = ruledao.loadAll();
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									ruledao.close();
								}
								if (rulelist != null && rulelist.size() > 0 && "".equals(hostNode.getSysLocation())) {
									NetSyslogRule logrule = (NetSyslogRule) rulelist.get(0);
									strFacility = logrule.getFacility();
								} else {
									strFacility = hostNode.getSysLocation();
								}
								String strSql = "";
								strSql = "insert into nms_netsyslogrule_node(id,nodeid,facility)values(0,'" + hostNode.getId() + "','" + strFacility + "')";
								try {
									netruledao.saveOrUpdate(strSql);
								} catch (Exception e) {
									e.printStackTrace();
								}
								TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
								try {
									timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(addResult), timeShareConfigUtil.getObjectType("0"));
								} catch (Exception e) {
									e.printStackTrace();
								}
								TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
								try {
									timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(addResult), timeGratherConfigUtil.getObjectType("0"));
								} catch (Exception e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								netruledao.close();
								ruledao.close();
							}
						}
						Host node = (Host) PollingEngine.getInstance().getNodeByIP(ipAddress);
						try {
							if (node.getEndpoint() == 2) {
							} else {
								if (node.getCategory() == 4) {
									try {
										AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
										alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()));
										if (node.getCollecttype() == 1) {
											NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
											nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "1");
										} else if (node.getCollecttype() == 3 || node.getCollecttype() == 8 || node.getCollecttype() == 9) {
											NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
											nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNodePing(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "1", "ping");
										} else {
											NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
											nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId() + "", AlarmConstant.TYPE_HOST, getSutType(node.getSysOid()), "0",
													node.getCollecttype());

										}
									} catch (RuntimeException e) {
										e.printStackTrace();
									}
								} else if (node.getCategory() < 4 || node.getCategory() == 7 || node.getCategory() == 8) {
									try {
										AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
										alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()));
										if (node.getCollecttype() == 1) {
											NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
											nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()), "1");
										} else if (node.getCollecttype() == 3 || node.getCollecttype() == 8 || node.getCollecttype() == 9) {
											NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
											nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNodePing(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()), "1", "ping");
										} else {
											NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
											nodeGatherIndicatorsUtil.addGatherIndicatorsOtherForNode(node.getId() + "", AlarmConstant.TYPE_NET, getSutType(node.getSysOid()), "0",
													node.getCollecttype());
										}
									} catch (RuntimeException e) {
										e.printStackTrace();
									}
								} else if (node.getCategory() == 9) {
									if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING) {
										try {
											AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
											alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(node.getId() + "", AlarmConstant.TYPE_NET, "atm", "ping");
										} catch (RuntimeException e) {
											e.printStackTrace();
										}
										try {
											NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
											nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(node.getId() + "", AlarmConstant.TYPE_NET, "atm", "1", "ping");
										} catch (RuntimeException e) {
											e.printStackTrace();
										}
									}
								}
								if (node.getCollecttype() == SystemConstant.COLLECTTYPE_PING || node.getCollecttype() == SystemConstant.COLLECTTYPE_TELNETCONNECT
										|| node.getCollecttype() == SystemConstant.COLLECTTYPE_SSHCONNECT) {
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
						sb.append(",添加设备部分失败");
					}
				}
			}

			// 2.更新xml
			try {
				UpdateXmlTask xmltask = new UpdateXmlTask();
				xmltask.run();
			} catch (Exception e) {
				e.printStackTrace();
			}

			HostNodeDao hostNodeDao = new HostNodeDao();
			Hashtable nodehash = new Hashtable();
			try {
				List hostlist = hostNodeDao.loadIsMonitored(1);
				if (hostlist != null && hostlist.size() > 0) {
					for (int i = 0; i < hostlist.size(); i++) {
						HostNode node = (HostNode) hostlist.get(i);
						if (nodehash.containsKey(node.getCategory() + "")) {
							((List) nodehash.get(node.getCategory() + "")).add(node);
						} else {
							List _nodelist = new ArrayList();
							_nodelist.add(node);
							nodehash.put(node.getCategory() + "", _nodelist);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				hostNodeDao.close();
			}
			ShareData.setNodehash(nodehash);
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(sb.toString());
		out.flush();
	}

	public String getSutType(String oids) {
		String subtype = "";
		if (oids.startsWith("1.3.6.1.4.1.311.")) {
			subtype = "windows";
		} else if (oids.startsWith("1.3.6.1.4.1.2021") || oids.startsWith("1.3.6.1.4.1.8072")) {
			subtype = "linux";
		} else if (oids.startsWith("as400")) {
			subtype = "as400";

		} else if (oids.startsWith("1.3.6.1.4.1.42.2.1.1")) {
			subtype = "solaris";
		} else if (oids.startsWith("1.3.6.1.4.1.2.3.1.2.1.1")) {
			subtype = "aix";
		} else if (oids.startsWith("1.3.6.1.4.1.11.2.3.10.1")) {
			subtype = "hpunix";
		} else if (oids.startsWith("1.3.6.1.4.1.11.2.3.7.11")) {
			subtype = "hp";
		} else if (oids.startsWith("1.3.6.1.4.1.9.")) {
			subtype = "cisco";
		} else if (oids.startsWith("1.3.6.1.4.1.25506.") || oids.startsWith("1.3.6.1.4.1.2011.")) {
			subtype = "h3c";
		} else if (oids.startsWith("1.3.6.1.4.1.4881.")) {
			subtype = "redgiant";
		} else if (oids.startsWith("1.3.6.1.4.1.5651.")) {
			subtype = "maipu";
		} else if (oids.startsWith("1.3.6.1.4.1.171.")) {
			subtype = "dlink";
		} else if (oids.startsWith("1.3.6.1.4.1.2272.")) {
			subtype = "northtel";
		} else if (oids.startsWith("1.3.6.1.4.1.89.")) {
			subtype = "radware";
		} else if (oids.startsWith("1.3.6.1.4.1.3320.")) {
			subtype = "bdcom";
		} else if (oids.startsWith("1.3.6.1.4.1.1588.2.1.")) {
			subtype = "brocade";
		} else if (oids.startsWith("1.3.6.1.4.1.3902.")) {
			subtype = "zte";
		} else if (oids.startsWith("1.3.6.1.4.1.116.")) {
			subtype = "hds";
		} else if (oids.startsWith("1.3.6.1.4.1.14331.")) {
			// 天融信防火墙
			subtype = "topsec";
		} else if (oids.startsWith("1.3.6.1.4.1.800.")) {
			// Alcatel
			subtype = "alcatel";
		} else if (oids.startsWith("1.3.6.1.4.1.45.")) {
			// Avaya
			subtype = "avaya";
		} else if (oids.startsWith("1.3.6.1.4.1.6876.")) {
			// VMWare
			subtype = "vmware";
		} else if (oids.startsWith("1.3.6.1.4.1.1981.1")) {
			subtype = "emc_vnx";
		} else if (oids.startsWith("1.3.6.1.4.1.1981.2")) {
			subtype = "emc_dmx";
		} else if (oids.startsWith("1.3.6.1.4.1.1981.3")) {
			subtype = "emc_vmax";
		} else if (oids.startsWith("1.3.6.1.4.1.2636.")) {
			subtype = "juniper";
		} else if (oids.startsWith("1.3.6.1.4.1.3224.")) {
			subtype = "checkpoint";
		} else if (oids.startsWith("1.3.6.1.4.1.789.")) {
			subtype = "netapp";
		} else if (oids.startsWith("1.3.6.1.4.1.476.1.42") || oids.startsWith("1.3.6.1.4.1.13400.2.1")) {
			subtype = "emerson";
		}

		return subtype;
	}

	public List readXls(String fileName) {
		List list = new ArrayList();
		HostNode hostnode = new HostNode();
		String str = null;
		try {
			File file = new File(fileName);
			if (file.exists()) {
				Workbook book = Workbook.getWorkbook(new File(fileName));
				Sheet rs = book.getSheet(0);
				int rows = rs.getRows();// 行数
				int cols = rs.getColumns();// 列数
				for (int row = 2; row < rows; row++) {// 不包括第一行
					hostnode = new HostNode();
					for (int c = 0; c < cols; c++) {// 每一列
						if (c == 1) {// 1、2、9、10、11列
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setAlias(str);

						} else if (c == 2) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setIpAddress(str);
						} else if (c == 7) {
							str = rs.getCell(c, row).getContents().trim();
							if ("是".equalsIgnoreCase(str)) {
								hostnode.setManaged(true);
							} else {
								hostnode.setManaged(false);
							}
						} else if (c == 8) {
							str = rs.getCell(c, row).getContents().trim();
							if ("是".equalsIgnoreCase(str)) {
								hostnode.setEndpoint(1);
							} else {
								hostnode.setEndpoint(0);
							}
						} else if (c == 9) {
							str = rs.getCell(c, row).getContents().trim();
							int temp = Integer.parseInt(str);
							hostnode.setCollecttype(temp);
						} else if (c == 10) {
							str = rs.getCell(c, row).getContents().trim();
							int temp = Integer.parseInt(str);
							hostnode.setSnmpversion(temp);
						} else if (c == 11) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setCommunity(str);
						} else if (c == 13) {
							str = rs.getCell(c, row).getContents().trim();
							int temp = Integer.parseInt(str);
							hostnode.setCategory(temp);
						} else if (c == 14) {
							str = rs.getCell(c, row).getContents().trim();
							int temp = Integer.parseInt(str);
							hostnode.setOstype(temp);
						} else if (c == 15) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setBid(str);
						} else if (c == 16) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setSecuritylevel(Integer.parseInt(str));// 安全等级
						} else if (c == 17) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setSecurityName(str);
						} else if (c == 18) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setV3_ap(Integer.parseInt(str));
						} else if (c == 19) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setAuthpassphrase(str);
						} else if (c == 20) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setV3_privacy(Integer.parseInt(str));
						} else if (c == 21) {
							str = rs.getCell(c, row).getContents().trim();
							hostnode.setPrivacyPassphrase(str);
						}
					}
					hostnode.setDiscovertatus(-1);
					list.add(hostnode);
				}
				book.close();
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
}
