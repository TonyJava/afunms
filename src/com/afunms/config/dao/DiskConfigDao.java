package com.afunms.config.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.common.base.BaseDao;
import com.afunms.common.base.BaseVo;
import com.afunms.common.base.DaoInterface;
import com.afunms.common.util.DBManager;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.model.Diskconfig;
import com.afunms.detail.service.diskInfo.DiskInfoService;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.DiskCollectEntity;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

@SuppressWarnings("unchecked")
public class DiskConfigDao extends BaseDao implements DaoInterface {
	public DiskConfigDao() {
		super("nms_diskconfig");
	}

	public boolean delete(String[] id) {
		boolean result = false;
		try {
			for (int i = 0; i < id.length; i++) {
				conn.addBatch("delete from nms_diskconfig where id=" + id[i]);
			}
			conn.executeBatch();
			result = true;
			Hashtable allDiskAlarm = (Hashtable) getByAlarmflag(new Integer(99));
			ShareData.setAlldiskalarmdata(allDiskAlarm);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			conn.close();
		}
		return result;
	}

	public void empty() {
		try {
			conn.executeUpdate("delete from nms_diskconfig ");
			Hashtable allDiskAlarm = (Hashtable) getByAlarmflag(new Integer(99));
			ShareData.setAlldiskalarmdata(allDiskAlarm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	public BaseVo findByID(String id) {
		BaseVo vo = null;
		try {
			rs = conn.executeQuery("select * from nms_diskconfig where id=" + id);
			if (rs.next()) {
				vo = loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		} finally {
			conn.close();
		}
		return vo;
	}

	/*
	 * 
	 * ���ڴ�����ݿ�����ȡÿ��IP�Ķ˿���Ϣ������˿����ñ���
	 */
	public void fromLastToDiskconfig() throws Exception {
		List list = new ArrayList();
		List list1 = new ArrayList();
		List shareList = new ArrayList();
		Hashtable diskhash = new Hashtable();
		Diskconfig diskconfig = null;
		try {
			// ���ڴ��еõ����ж˿ڵĲɼ���Ϣ
			Hashtable sharedata = ShareData.getSharedata();
			// �����ݿ�õ�����IP�б�
			HostNodeDao hostnodedao = new HostNodeDao();
			shareList = hostnodedao.loadMonitorByMonCategory(1, 4);
			if (shareList != null && shareList.size() > 0) {
				for (int i = 0; i < shareList.size(); i++) {
					HostNode monitornode = (HostNode) shareList.get(i);
					Hashtable ipdata = (Hashtable) sharedata.get(monitornode.getIpAddress());
					if (ipdata == null) {
						continue;
					}
					Vector vector = (Vector) ipdata.get("disk");
					if (vector != null && vector.size() > 0) {
						for (int k = 0; k < vector.size(); k++) {
							DiskCollectEntity disk = (DiskCollectEntity) vector.get(k);
							if (disk.getEntity().equals("AllSize")) {
								list.add(disk);
							}
						}
					}
				}
			}
			// �Ӵ������ñ����ȡ�б�
			list1 = loadAll();
			if (list1 != null && list1.size() > 0) {
				for (int i = 0; i < list1.size(); i++) {
					diskconfig = (Diskconfig) list1.get(i);
					// IP:��������
					diskhash.put(diskconfig.getIpaddress() + ":" + diskconfig.getName(), diskconfig);
				}
			}
			// �жϲɼ����Ĵ�����Ϣ�Ƿ��Ѿ��ڴ������ñ����Ѿ����ڣ��������������
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					DiskCollectEntity diskdata = (DiskCollectEntity) list.get(i);
					Host host = (Host) PollingEngine.getInstance().getNodeByIP(diskdata.getIpaddress());
					if (host == null) {
						continue;
					}

					if (host.getOstype() == 0 || host.getOstype() == 5) {
						// ΪWINDOWS����,��Ҫ��ȡ��������,ȥǰ�����ַ�
						diskdata.setSubentity(diskdata.getSubentity().substring(0, 3));
					}
					if (!diskhash.containsKey(diskdata.getIpaddress() + ":" + diskdata.getSubentity())) {
						diskconfig = new Diskconfig();
						diskconfig.setName(diskdata.getSubentity());
						diskconfig.setBak("��������ֵ");
						diskconfig.setIpaddress(diskdata.getIpaddress());
						diskconfig.setLinkuse("");
						diskconfig.setMonflag(1);// 0�����澯 1���澯��Ĭ�ϵ�����Ǹ澯
						diskconfig.setSms(new Integer(1));// 0�����澯 1���澯��Ĭ�ϵ�����ǲ����Ͷ���
						diskconfig.setReportflag(new Integer(0));// 0���������ڱ��� 1�������ڱ���Ĭ�ϵ�����ǲ������ڱ���
						diskconfig.setLimenvalue(new Integer(80));
						diskconfig.setLimenvalue1(new Integer(90));
						diskconfig.setLimenvalue2(new Integer(95));
						diskconfig.setSms1(new Integer(0)); 
						diskconfig.setSms2(new Integer(0)); 
						diskconfig.setSms3(new Integer(0)); 
						try {
							conn = new DBManager();
							save(diskconfig);
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							conn.close();
						}

						diskconfig = new Diskconfig();
						diskconfig.setName(diskdata.getSubentity());
						diskconfig.setBak("��������ֵ");
						diskconfig.setIpaddress(diskdata.getIpaddress());
						diskconfig.setLinkuse("");

						diskconfig.setMonflag(1);// 0�����澯 1���澯��Ĭ�ϵ�����Ǹ澯
						diskconfig.setSms(new Integer(1));// 0�����澯
						diskconfig.setReportflag(new Integer(0));// 0���������ڱ���
						diskconfig.setLimenvalue(new Integer(5));
						diskconfig.setLimenvalue1(new Integer(10));
						diskconfig.setLimenvalue2(new Integer(15));
						diskconfig.setSms1(new Integer(0));// 0�����澯
						diskconfig.setSms2(new Integer(0));// 0�����澯
						diskconfig.setSms3(new Integer(0));// 0�����澯
						try {
							conn = new DBManager();
							save(diskconfig);
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							conn.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 * ���ڴ�����ݿ�����ȡÿ��IP�Ķ˿���Ϣ������˿����ñ���
	 */
	public void fromLastToDiskconfig(String ipaddress) throws Exception {
		List list = new ArrayList();
		List list1 = new ArrayList();
		List shareList = new ArrayList();
		Hashtable diskhash = new Hashtable();
		Diskconfig diskconfig = null;
		String runmodel = PollingEngine.getCollectwebflag();
		try {
			// ��ϵͳ����ģʽ�ж�
			if ("0".equals(runmodel)) {
				// �ɼ�������Ǽ���ģʽ
				// ���ڴ��еõ����ж˿ڵĲɼ���Ϣ
				Hashtable sharedata = ShareData.getSharedata();
				// �����ݿ�õ�����IP�б�
				HostNodeDao hostnodedao = new HostNodeDao();
				shareList = hostnodedao.findByCondition(" where ip_address = '" + ipaddress + "'");
				if (shareList != null && shareList.size() > 0) {
					for (int i = 0; i < shareList.size(); i++) {
						HostNode monitornode = (HostNode) shareList.get(i);
						Hashtable ipdata = (Hashtable) sharedata.get(monitornode.getIpAddress());
						if (ipdata == null) {
							continue;
						}
						Vector vector = (Vector) ipdata.get("disk");
						if (vector != null && vector.size() > 0) {
							for (int k = 0; k < vector.size(); k++) {
								DiskCollectEntity disk = (DiskCollectEntity) vector.get(k);
								if (disk.getEntity().equals("AllSize")) {
									list.add(disk);
								}
							}
						}
					}
				}
			} else {
				// �ɼ�������Ƿ���ģʽ
				Host host = (Host) PollingEngine.getInstance().getNodeByIP(ipaddress);
				NodeUtil nodeUtil = new NodeUtil();
				NodeDTO nodedto = nodeUtil.creatNodeDTOByNode(host);
				DiskInfoService diskInfoService = new DiskInfoService(nodedto.getId() + "", nodedto.getType(), nodedto.getSubtype());
				Vector vector = diskInfoService.getDiskInfoVector();
				if (vector != null && vector.size() > 0) {
					for (int k = 0; k < vector.size(); k++) {
						DiskCollectEntity disk = (DiskCollectEntity) vector.get(k);
						if (disk.getEntity().equals("AllSize")) {
							list.add(disk);
						}
					}
				}
			}
			// �Ӵ������ñ����ȡ�б�
			list1 = loadByIpaddress(ipaddress);
			if (list1 != null && list1.size() > 0) {
				for (int i = 0; i < list1.size(); i++) {
					diskconfig = (Diskconfig) list1.get(i);
					// IP:��������
					diskhash.put(diskconfig.getIpaddress() + ":" + diskconfig.getName(), diskconfig);
				}
			}
			// �жϲɼ����Ĵ�����Ϣ�Ƿ��Ѿ��ڴ������ñ����Ѿ����ڣ��������������
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					DiskCollectEntity diskdata = (DiskCollectEntity) list.get(i);
					Host host = (Host) PollingEngine.getInstance().getNodeByIP(diskdata.getIpaddress());
					if (host == null) {
						continue;
					}

					if (host.getOstype() == 0 || host.getOstype() == 5) {
						diskdata.setSubentity(diskdata.getSubentity().substring(0, 3));
					}
					if (!diskhash.containsKey(diskdata.getIpaddress() + ":" + diskdata.getSubentity())) {
						diskconfig = new Diskconfig();
						diskconfig.setName(diskdata.getSubentity());
						diskconfig.setBak("��������ֵ");
						diskconfig.setIpaddress(diskdata.getIpaddress());
						diskconfig.setLinkuse("");

						diskconfig.setMonflag(1);// 0�����澯 1���澯��Ĭ�ϵ�����Ǹ澯
						diskconfig.setSms(new Integer(1));// 0�����澯
						diskconfig.setReportflag(new Integer(0));// 0���������ڱ���
						diskconfig.setLimenvalue(new Integer(80));
						diskconfig.setLimenvalue1(new Integer(90));
						diskconfig.setLimenvalue2(new Integer(95));
						diskconfig.setSms1(new Integer(0));// 0�����澯
						diskconfig.setSms2(new Integer(0));// 0�����澯
						diskconfig.setSms3(new Integer(0));// 0�����澯
						try {
							conn = new DBManager();
							save(diskconfig);
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							conn.close();
						}
						diskconfig = new Diskconfig();
						diskconfig.setName(diskdata.getSubentity());
						diskconfig.setBak("��������ֵ");
						diskconfig.setIpaddress(diskdata.getIpaddress());
						diskconfig.setLinkuse("");

						diskconfig.setMonflag(1);// 0�����澯 1���澯��Ĭ�ϵ�����Ǹ澯
						diskconfig.setSms(new Integer(1));// 0�����澯
						diskconfig.setReportflag(new Integer(0));// 0���������ڱ���
						diskconfig.setLimenvalue(new Integer(5));
						diskconfig.setLimenvalue1(new Integer(10));
						diskconfig.setLimenvalue2(new Integer(15));
						diskconfig.setSms1(new Integer(0));// 0�����澯
						diskconfig.setSms2(new Integer(0));// 0�����澯
						diskconfig.setSms3(new Integer(0));// 0�����澯
						try {
							conn = new DBManager();
							save(diskconfig);
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
							conn.close();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ����IP���Ƿ�Ҫ��ʾ���ձ���ı�־λ��ѯ
	 * 
	 */
	public Hashtable getByAlarmflag(Integer smsflag) {
		List list = new ArrayList();
		Hashtable retValue = new Hashtable();
		String sql = "";
		try {
			if (smsflag == 99) {
				sql = "select * from nms_diskconfig order by ipaddress";
			} else {
				sql = "from nms_diskconfig where monflag=" + smsflag + " order by ipaddress";
			}
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					Diskconfig diskconfig = (Diskconfig) list.get(i);
					retValue.put(diskconfig.getIpaddress() + ":" + diskconfig.getName() + ":" + diskconfig.getBak(), diskconfig);// yangjun
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retValue;
	}

	public Diskconfig getByipandindex(String ip, String diskindex) {
		try {
			rs = conn.executeQuery("select * from nms_diskconfig where ipaddress='" + ip + "' and diskindex =" + diskindex + " order by id");
			if (rs.next()) {
				return (Diskconfig) loadFromRS(rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
		return null;
	}

	/*
	 * ����IP���Ƿ�Ҫ��ʾ���ձ���ı�־λ��ѯ
	 * 
	 */
	public List getByIpAndReportflag(String ip, Integer reportflag) {
		List list = new ArrayList();
		try {
			String sql = "select * from nms_diskconfig h where h.ipaddress = '" + ip + "' and h.reportflag=" + reportflag + " order by h.diskindex";
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List getIps() {
		List list = new ArrayList();
		try {
			String sql = "select distinct h.ipaddress from nms_diskconfig h order by h.ipaddress";
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				list.add(rs.getString("ipaddress"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public Hashtable getIpsHash(String ipaddress) {
		Hashtable hash = new Hashtable();
		try {
			String sql = "select * from nms_diskconfig h where h.ipaddress='" + ipaddress + "' order by h.diskindex";
			rs = conn.executeQuery(sql);
			while (rs.next()) {
				Diskconfig diskconfig = (Diskconfig) loadFromRS(rs);
				if (diskconfig.getLinkuse() != null && diskconfig.getLinkuse().trim().length() > 0) {
					hash.put(diskconfig.getIpaddress() + ":" + diskconfig.getDiskindex(), diskconfig.getLinkuse());
				} else {
					hash.put(diskconfig.getIpaddress() + ":" + diskconfig.getDiskindex(), "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}

	public List loadAll() {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_diskconfig order by ipaddress,diskindex");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			SysLogger.error("DiskconfigDao:loadAll()", e);
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public List loadByIpaddress(String ip) {
		List list = new ArrayList(5);
		try {
			rs = conn.executeQuery("select * from nms_diskconfig where ipaddress='" + ip + "' order by id");
			while (rs.next()) {
				list.add(loadFromRS(rs));
			}
		} catch (Exception e) {
			SysLogger.error("DiskconfigDao:loadAll()", e);
			list = null;
		} finally {
			conn.close();
		}
		return list;
	}

	public Diskconfig loadDiskconfig(int id) {
		List diskconfigList = findByCriteria("select * from nms_diskconfig where id=" + id);
		if (diskconfigList != null && diskconfigList.size() > 0) {
			Diskconfig portconfig = (Diskconfig) diskconfigList.get(0);
			return portconfig;
		}
		return null;
	}

	public BaseVo loadFromRS(ResultSet rs) {
		Diskconfig vo = new Diskconfig();
		try {
			vo.setId(rs.getInt("id"));
			vo.setBak(rs.getString("bak"));
			vo.setIpaddress(rs.getString("ipaddress"));
			vo.setLinkuse(rs.getString("linkuse"));
			vo.setName(rs.getString("name"));
			vo.setDiskindex(rs.getInt("diskindex"));
			vo.setMonflag(rs.getInt("monflag"));
			vo.setReportflag(rs.getInt("reportflag"));
			vo.setSms(rs.getInt("sms"));
			vo.setSms1(rs.getInt("sms1"));
			vo.setSms2(rs.getInt("sms2"));
			vo.setSms3(rs.getInt("sms3"));
			vo.setLimenvalue(rs.getInt("limenvalue"));
			vo.setLimenvalue1(rs.getInt("limenvalue1"));
			vo.setLimenvalue2(rs.getInt("limenvalue2"));
		} catch (Exception e) {
			e.printStackTrace();
			vo = null;
		}
		return vo;
	}

	public boolean save(BaseVo baseVo) {
		Diskconfig vo = (Diskconfig) baseVo;
		StringBuffer sql = new StringBuffer(100);
		sql.append("insert into nms_diskconfig(ipaddress,name,diskindex,linkuse,sms,bak,monflag,reportflag,sms1,sms2,sms3,limenvalue,limenvalue1,limenvalue2)values(");
		sql.append("'");
		sql.append(vo.getIpaddress());
		sql.append("','");
		sql.append(vo.getName());
		sql.append("',");
		sql.append(vo.getDiskindex());
		sql.append(",'");
		sql.append(vo.getLinkuse());
		sql.append("',");
		sql.append(vo.getSms());
		sql.append(",'");
		sql.append(vo.getBak());
		sql.append("',");
		sql.append(vo.getMonflag());
		sql.append(",");
		sql.append(vo.getReportflag());
		sql.append(",");
		sql.append(vo.getSms1());
		sql.append(",");
		sql.append(vo.getSms2());
		sql.append(",");
		sql.append(vo.getSms3());
		sql.append(",");
		sql.append(vo.getLimenvalue());
		sql.append(",");
		sql.append(vo.getLimenvalue1());
		sql.append(",");
		sql.append(vo.getLimenvalue2());
		sql.append(")");
		return saveOrUpdate(sql.toString());
	}

	public boolean update(BaseVo baseVo) {
		Diskconfig vo = (Diskconfig) baseVo;
		boolean result = false;
		StringBuffer sql = new StringBuffer();
		sql.append("update nms_diskconfig set ipaddress='");
		sql.append(vo.getIpaddress());
		sql.append("',name='");
		sql.append(vo.getName());
		sql.append("',diskindex=");
		sql.append(vo.getDiskindex());
		sql.append(",linkuse='");
		if (vo.getLinkuse() != null) {
			sql.append(vo.getLinkuse());
		} else {
			sql.append("");
		}
		sql.append("',sms=");
		sql.append(vo.getSms());
		sql.append(",bak='");
		sql.append(vo.getBak());
		sql.append("',monflag=");
		sql.append(vo.getMonflag());
		sql.append(",reportflag=");
		sql.append(vo.getReportflag());
		sql.append(",sms1=");
		sql.append(vo.getSms1());
		sql.append(",sms2=");
		sql.append(vo.getSms2());
		sql.append(",sms3=");
		sql.append(vo.getSms3());
		sql.append(",limenvalue=");
		sql.append(vo.getLimenvalue());
		sql.append(",limenvalue1=");
		sql.append(vo.getLimenvalue1());
		sql.append(",limenvalue2=");
		sql.append(vo.getLimenvalue2());
		sql.append(" where id=");
		sql.append(vo.getId());

		try {
			conn.executeUpdate(sql.toString());
			result = true;
			Hashtable allDiskAlarm = (Hashtable) getByAlarmflag(new Integer(99));
			ShareData.setAlldiskalarmdata(allDiskAlarm);
		} catch (Exception e) {
			result = false;
			SysLogger.error("DiskconfigDao:update()", e);
		} finally {
			conn.close();
		}
		return result;
	}
}
