package com.afunms.polling.snmp.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.alarm.model.AlarmIndicatorsNode;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.OracleLockInfo;
import com.afunms.application.model.OracleTopSqlReadWrite;
import com.afunms.application.model.OracleTopSqlSort;
import com.afunms.application.model.Oracle_sessiondata;
import com.afunms.application.model.Oraspaceconfig;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.util.CheckEventUtil;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.common.util.SystemConstant;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.om.PingCollectEntity;

@SuppressWarnings("unchecked")
public class OracleDataCollector {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public OracleDataCollector() {

	}

	public void collect_data(String dbid, Hashtable gatherHash) {
		DBDao dbdao = null;
		try {
			List dbmonitorlists = new ArrayList();
			dbmonitorlists = ShareData.getDbconfiglist();
			DBVo dbmonitorlist = new DBVo();
			if (dbmonitorlists != null && dbmonitorlists.size() > 0) {
				for (int i = 0; i < dbmonitorlists.size(); i++) {
					DBVo vo = (DBVo) dbmonitorlists.get(i);
					if (vo.getId() == Integer.parseInt(dbid)) {
						dbmonitorlist = vo;
						break;
					}
				}
			}
			if (dbmonitorlist.getManaged() == 0) {
				return;
			}
			String serverip = dbmonitorlist.getIpAddress();
			String passwords = EncryptUtil.decode(dbmonitorlist.getPassword());
			int port = Integer.parseInt(dbmonitorlist.getPort());
			String dbnames = dbmonitorlist.getDbName();
			DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
			if (dbnode != null) {
				dbnode.setStatus(0);
				dbnode.setAlarm(false);
				dbnode.getAlarmMessage().clear();
				Calendar _tempCal = Calendar.getInstance();
				Date _cc = _tempCal.getTime();
				String _time = sdf.format(_cc);
				dbnode.setLastTime(_time);

			} else {
				return;
			}

			// �жϸ����ݿ��Ƿ���������
			boolean oracleIsOK = false;
			dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());

			if (dbnode != null) {
				dbnode.setStatus(0);
				dbnode.setAlarm(false);
				dbnode.getAlarmMessage().clear();
				Calendar _tempCal = Calendar.getInstance();
				Date _cc = _tempCal.getTime();
				String _time = sdf.format(_cc);
				dbnode.setLastTime(_time);
			}

			if (dbmonitorlist.getCollecttype() == SystemConstant.DBCOLLECTTYPE_SHELL) {
				// �ű��ɼ���ʽ
			} else {
				// JDBC�ɼ���ʽ
				String hex = IpTranslation.formIpToHex(serverip);
				Hashtable oracledata = new Hashtable();
				int flag = 0;
				try {
					dbdao = new DBDao();
					oracleIsOK = dbdao.getOracleIsOK(serverip, port, dbmonitorlist.getDbName(), dbmonitorlist.getUser(), EncryptUtil.decode(dbmonitorlist.getPassword()));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbdao.close();
				}
				if (!oracleIsOK) {
					flag = 1;
					try {
						dbnode.setAlarm(true);
						dbnode.setStatus(3);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (flag == 1) {
					// ��Ҫ�������ݿ����ڵķ������Ƿ�����ͨ
					Vector ipPingData = (Vector) ShareData.getPingdata().get(serverip);
					if (ipPingData != null) {
						PingCollectEntity pingdata = (PingCollectEntity) ipPingData.get(0);
						String pingvalue = pingdata.getThevalue();
						if (pingvalue == null || pingvalue.trim().length() == 0) {
							pingvalue = "0";
						}
						double pvalue = new Double(pingvalue);
						if (pvalue == 0) {
							try {
								dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
								dbnode.setAlarm(true);
								dbnode.setStatus(3);
								List alarmList = dbnode.getAlarmMessage();
								if (alarmList == null) {
									alarmList = new ArrayList();
								}
								dbnode.getAlarmMessage().add("���ݿ����ȫ��ֹͣ");
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							try {
								PingCollectEntity hostdata = null;
								hostdata = new PingCollectEntity();
								hostdata.setIpaddress(serverip + ":" + dbmonitorlist.getId());
								Calendar date = Calendar.getInstance();
								hostdata.setCollecttime(date);
								hostdata.setCategory("ORAPing");
								hostdata.setEntity("Utilization");
								hostdata.setSubentity("ConnectUtilization");
								hostdata.setRestype("dynamic");
								hostdata.setUnit("%");
								hostdata.setThevalue("0");
								dbdao.createHostData(hostdata);
								dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
								dbnode.setAlarm(true);
								dbnode.setStatus(3);
								List alarmList = dbnode.getAlarmMessage();
								if (alarmList == null) {
									alarmList = new ArrayList();
								}
								dbnode.getAlarmMessage().add("���ݿ����ֹͣ");

							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} else {
						try {
							PingCollectEntity hostdata = null;
							hostdata = new PingCollectEntity();
							hostdata.setIpaddress(serverip + ":" + dbmonitorlist.getId());
							Calendar date = Calendar.getInstance();
							hostdata.setCollecttime(date);
							hostdata.setCategory("ORAPing");
							hostdata.setEntity("Utilization");
							hostdata.setSubentity("ConnectUtilization");
							hostdata.setRestype("dynamic");
							hostdata.setUnit("%");
							hostdata.setThevalue("0");
							dbdao.createHostData(hostdata);
							dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
							dbnode.setAlarm(true);
							List alarmList = dbnode.getAlarmMessage();
							if (alarmList == null) {
								alarmList = new ArrayList();
							}
							dbnode.getAlarmMessage().add("���ݿ����ֹͣ");
							dbnode.setStatus(3);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} else {
					try {
						PingCollectEntity hostdata = null;
						hostdata = new PingCollectEntity();
						hostdata.setIpaddress(serverip + ":" + dbmonitorlist.getId());
						Calendar date = Calendar.getInstance();
						hostdata.setCollecttime(date);
						hostdata.setCategory("ORAPing");
						hostdata.setEntity("Utilization");
						hostdata.setSubentity("ConnectUtilization");
						hostdata.setRestype("dynamic");
						hostdata.setUnit("%");
						hostdata.setThevalue("100");
						try {
							dbdao.createHostData(hostdata);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							dbdao.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (flag == 0) {
					Date tempDate = new Date();
					Hashtable allOraData = dbdao.getAllOracleData(serverip, port, dbmonitorlist.getDbName(), dbmonitorlist.getUser(), passwords, gatherHash);
					SysLogger.info("####�ɼ�-sidΪ" + dbmonitorlist.getId() + "��oracle���ݿ��ʱ+" + (new Date().getTime() - tempDate.getTime()));
					Vector info = (Vector) allOraData.get("session");
					Vector tableinfo = (Vector) allOraData.get("tablespace");
					Vector rollbackinfo_v = (Vector) allOraData.get("rollback");
					Hashtable sysValue = (Hashtable) allOraData.get("sysinfo");
					Hashtable memValue = (Hashtable) allOraData.get("ga_hash");
					Vector lockinfo_v = (Vector) allOraData.get("lock");
					Hashtable memPerfValue = (Hashtable) allOraData.get("memoryPerf");
					Vector table_v = (Vector) allOraData.get("table");
					Vector sql_v = (Vector) allOraData.get("topsql");
					Vector contrFile_v = (Vector) allOraData.get("controlfile");
					Hashtable isArchive_h = (Hashtable) allOraData.get("sy_hash");
					Vector logFile_v = (Vector) allOraData.get("log");
					Vector keepObj_v = (Vector) allOraData.get("keepobj");
					String lstrnStatu = (String) allOraData.get("open_mode");
					Vector extent_v = (Vector) allOraData.get("extent");
					Hashtable userinfo_h = (Hashtable) allOraData.get("user");
					Hashtable cursors = (Hashtable) allOraData.get("cursors");
					Vector wait = (Vector) allOraData.get("wait");
					Hashtable dbio = (Hashtable) allOraData.get("dbio");
					OracleLockInfo oracleLockInfo = (OracleLockInfo) allOraData.get("oracleLockInfo");
					Vector<OracleTopSqlReadWrite> oracleTopSqlReadWriteVector = (Vector<OracleTopSqlReadWrite>) allOraData.get("topSqlReadWriteVector");
					Vector<OracleTopSqlSort> oracleTopSqlSortVector = (Vector<OracleTopSqlSort>) allOraData.get("topSqlSortVector");
					Hashtable<String, String> baseInfoHash = (Hashtable<String, String>) allOraData.get("baseInfoHash");

					if (info == null) {
						info = new Vector();
					}
					if (sysValue == null) {
						sysValue = new Hashtable();
					}
					if (memValue == null) {
						memValue = new Hashtable();
					}
					if (memPerfValue == null) {
						memPerfValue = new Hashtable();
					}
					if (tableinfo == null) {
						tableinfo = new Vector();
					}
					if (rollbackinfo_v == null) {
						rollbackinfo_v = new Vector();
					}
					if (lockinfo_v == null) {
						lockinfo_v = new Vector();
					}
					if (table_v == null) {
						table_v = new Vector();
					}
					if (sql_v == null) {
						sql_v = new Vector();
					}
					if (contrFile_v == null) {
						contrFile_v = new Vector();
					}
					if (logFile_v == null) {
						logFile_v = new Vector();
					}
					if (keepObj_v == null) {
						keepObj_v = new Vector();
					}
					if (isArchive_h == null) {
						isArchive_h = new Hashtable();
					}
					if (lstrnStatu == null) {
						lstrnStatu = "";
					}
					if (extent_v == null) {
						extent_v = new Vector();
					}
					if (userinfo_h == null) {
						userinfo_h = new Hashtable();
					}
					if (cursors == null) {
						cursors = new Hashtable();
					}
					if (wait == null) {
						wait = new Vector();
					}
					if (dbio == null) {
						dbio = new Hashtable();
					}
					if (baseInfoHash == null) {
						baseInfoHash = new Hashtable<String, String>();
					}
					if (oracleLockInfo == null) {
						oracleLockInfo = new OracleLockInfo();
					}
					if (oracleTopSqlReadWriteVector == null) {
						oracleTopSqlReadWriteVector = new Vector<OracleTopSqlReadWrite>();
					}
					if (oracleTopSqlSortVector == null) {
						oracleTopSqlSortVector = new Vector<OracleTopSqlSort>();
					}
					oracledata.put("sysValue", sysValue);
					oracledata.put("memValue", memValue);// nms_oramemvalue
					oracledata.put("memPerfValue", memPerfValue);// nms_oramemperfvalue
					oracledata.put("tableinfo_v", tableinfo);// ��ռ�
					oracledata.put("rollbackinfo_v", rollbackinfo_v);// �ع�����Ϣ
					oracledata.put("lockinfo_v", lockinfo_v);// ����Ϣ
					oracledata.put("info_v", info);// session��Ϣ
					oracledata.put("table_v", table_v);// ����Ϣ
					oracledata.put("sql_v", sql_v);// TOPN��SQL�����Ϣ
					oracledata.put("contrFile_v", contrFile_v);// �����ļ�
					oracledata.put("isArchive_h", isArchive_h); // nms_oraisarchive
					oracledata.put("keepObj_v", keepObj_v);// nms_orakeepobj
					oracledata.put("lstrnStatu", lstrnStatu);// ����״̬
					oracledata.put("extent_v", extent_v);// ��չ��Ϣnms_oraextent
					oracledata.put("logFile_v", logFile_v);// ��־�ļ�nms_oralogfile
					oracledata.put("userinfo_h", userinfo_h);// �û���Ϣnms_orauserinfo
					oracledata.put("cursors", cursors);// ָ����Ϣ //nms_oracursors
					oracledata.put("wait", wait);// �ȴ���Ϣ //nms_orawait
					oracledata.put("dbio", dbio);// IO��Ϣ //nms_oradbio
					oracledata.put("status", "1");// ״̬��Ϣ nms_orastatus
					oracledata.put("baseInfoHash", baseInfoHash);// ������Ϣ
					oracledata.put("oracleLockInfo", oracleLockInfo);// oracle��ָ���//��������
					oracledata.put("oracleTopSqlReadWriteVector", oracleTopSqlReadWriteVector);
					oracledata.put("oracleTopSqlSortVector", oracleTopSqlSortVector);
					ShareData.setAlloracledata(serverip + ":" + dbmonitorlist.getId(), oracledata);

					// �������ɼ���oracle���ݲ������ݿ�
					Date startDate = new Date();
					dbdao.processOracleData(ShareData.getAlloracledata());
					Date endDate = new Date();
					SysLogger.info("###################---sidΪ" + dbmonitorlist.getId() + "��oracle����ʱ(ms)��" + (endDate.getTime() - startDate.getTime()));

					Vector tableinfo_v = null;
					Hashtable datas = oracledata;
					try {
						tableinfo_v = (Vector) datas.get("tableinfo_v");
					} catch (Exception e) {
						e.printStackTrace();
					}
					// �Ա�ռ���и澯��֤����
					if (tableinfo_v != null && tableinfo_v.size() > 0) {
						ShareData.setOraspacedata(serverip + ":" + dbmonitorlist.getId(), tableinfo_v);
					}

					// ���ݿ��SESSION����ʷ����
					Vector info_v = null;
					info_v = (Vector) datas.get("info_v");
					if (info_v != null && info_v.size() > 0) {
						// ��ԭ��ʵʱ�����SESSION�������
						try {
							dbdao.clear_nmssessiondata(IpTranslation.formIpToHex(serverip) + ":" + dbmonitorlist.getId());
						} catch (Exception e) {

						}
					}
					for (int j = 0; j < info_v.size(); j++) {
						Oracle_sessiondata os = new Oracle_sessiondata();
						Hashtable ht = (Hashtable) info_v.get(j);
						String machine = ht.get("machine").toString();
						String usernames = ht.get("username").toString();
						String program = ht.get("program").toString();
						String status = ht.get("status").toString();
						String sessiontype = ht.get("sessiontype").toString();
						String command = ht.get("command").toString();
						String logontime = ht.get("logontime").toString();
						os.setCommand(command);
						os.setLogontime(sdf1.parse(logontime));
						os.setMachine(machine);
						Calendar _tempCal = Calendar.getInstance();
						Date _cc = _tempCal.getTime();
						os.setMon_time(_cc);
						os.setProgram(program);
						os.setSessiontype(sessiontype);
						os.setStatus(status);
						os.setUsername(usernames);
						os.setServerip(hex + ":" + dbmonitorlist.getId());
						os.setDbname(dbnames);
						try {
							// ������ʷ��
							dbdao.addOracle_sessiondata(os);
							// ����ʵʱ��
							dbdao.addOracle_nmssessiondata(os);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				String status = "0";
				String pingvalue = "0";
				if (flag == 0) {
					status = "1";
					pingvalue = "100";
				} else {
					dbdao.updateNmsValueByUniquekeyAndTablenameAndKey("nms_orastatus", "serverip", hex + ":" + dbmonitorlist.getId(), "status", status);
				}
				if (oracledata == null) {
					oracledata = new Hashtable();
				}
				oracledata.put("ping", pingvalue);
				if (oracledata != null) {
					ShareData.setAlloracledata(serverip + ":" + dbmonitorlist.getId(), oracledata);
					NodeUtil nodeUtil = new NodeUtil();
					NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
					updateData(nodeDTO, oracledata);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbdao != null) {
				dbdao.close();
			}
		}

	}

	public void updateData(NodeDTO nodeDTO, Object collectingData) {
		if (nodeDTO == null || collectingData == null) {
			return;
		}
		Hashtable datahashtable = (Hashtable) collectingData;

		Hashtable memeryHashtable = (Hashtable) datahashtable.get("memPerfValue");
		if (memeryHashtable == null) {
			memeryHashtable = new Hashtable();
		}

		Hashtable cursorsHashtable = (Hashtable) datahashtable.get("cursors");
		if (cursorsHashtable == null) {
			cursorsHashtable = new Hashtable();
		}

		Vector tableinfo_v = (Vector) datahashtable.get("tableinfo_v");

		String pingvalue = (String) datahashtable.get("ping");
		AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
		List list = alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(nodeDTO.getId() + "", nodeDTO.getType(), nodeDTO.getSubtype());
		CheckEventUtil checkEventUtil = new CheckEventUtil();
		for (int i = 0; i < list.size(); i++) {
			try {
				AlarmIndicatorsNode alarmIndicatorsNode = (AlarmIndicatorsNode) list.get(i);
				if ("ping".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (pingvalue != null) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, pingvalue);
					}
				} else if ("opencur".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (cursorsHashtable != null && cursorsHashtable.get("opencur") != null) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String) cursorsHashtable.get("opencur"));
					}
				} else if ("buffercache".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (memeryHashtable != null && memeryHashtable.get("buffercache") != null) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String) memeryHashtable.get("buffercache"));
					}
				} else if ("dictionarycache".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (memeryHashtable != null && memeryHashtable.get("dictionarycache") != null) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String) memeryHashtable.get("dictionarycache"));
					}
				} else if ("pctmemorysorts".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (memeryHashtable != null && memeryHashtable.get("pctmemorysorts") != null) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String) memeryHashtable.get("pctmemorysorts"));
					}
				} else if ("pctbufgets".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (memeryHashtable != null && memeryHashtable.get("pctbufgets") != null) {
						checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (String) memeryHashtable.get("pctbufgets"));
					}
				} else if ("tablespace".equalsIgnoreCase(alarmIndicatorsNode.getName())) {
					if (tableinfo_v != null && tableinfo_v.size() > 0) {
						OraspaceconfigDao oraspaceconfigManager = new OraspaceconfigDao();
						Hashtable oraspaces = null;
						try {
							oraspaces = oraspaceconfigManager.getByAlarmflag(1);
						} catch (Exception e1) {
							e1.printStackTrace();
						} finally {
							oraspaceconfigManager.close();
						}
						Vector spaces = new Vector();
						for (int k = 0; k < tableinfo_v.size(); k++) {
							Hashtable ht = (Hashtable) tableinfo_v.get(k);
							String tablespace = ht.get("tablespace").toString();
							if (spaces.contains(tablespace)) {
								continue;
							}
							spaces.add(tablespace);
							String percent = ht.get("percent_free").toString();
							if (oraspaces != null && oraspaces.containsKey(nodeDTO.getIpaddress() + ":" + nodeDTO.getId() + ":" + tablespace)) {
								// ������Ҫ�澯�ı�ռ�
								Integer free = 0;
								try {
									free = new Float(percent).intValue();
								} catch (Exception e) {
									e.printStackTrace();
								}
								// ���ݱ�ռ�澯�����ж��Ƿ�澯
								Oraspaceconfig oraspaceconfig = (Oraspaceconfig) oraspaces.get(nodeDTO.getIpaddress() + ":" + nodeDTO.getId() + ":" + tablespace);
								alarmIndicatorsNode.setLimenvalue0(oraspaceconfig.getAlarmvalue() + "");
								alarmIndicatorsNode.setLimenvalue1(oraspaceconfig.getAlarmvalue() + "");
								alarmIndicatorsNode.setLimenvalue2(oraspaceconfig.getAlarmvalue() + "");
								checkEventUtil.checkEvent(nodeDTO, alarmIndicatorsNode, (100 - free) + "", tablespace);
							}
						}

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// public void updateData(Object vo , Object collectingData){
	// if(vo == null || collectingData == null){
	// return ;
	// }
	// try {
	//			
	// DBVo dbmonitorlist = (DBVo)vo;
	//			
	// Hashtable datahashtable = (Hashtable)collectingData;
	//			
	// Hashtable memeryHashtable = (Hashtable)datahashtable.get("memPerfValue");
	// if(memeryHashtable == null)memeryHashtable = new Hashtable();
	//			
	// Hashtable cursorsHashtable = (Hashtable)datahashtable.get("cursors");
	// if(cursorsHashtable == null)cursorsHashtable = new Hashtable();
	//			
	// String pingvalue = (String)datahashtable.get("ping");
	// AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
	//			
	// List list =
	// alarmIndicatorsUtil.getAlarmInicatorsThresholdForNode(String.valueOf(dbmonitorlist.getId()),
	// AlarmConstant.TYPE_DB, "oracle");
	//			
	// CheckEventUtil checkEventUtil = new CheckEventUtil();
	//			
	// for(int i = 0 ; i < list.size() ; i ++){
	// AlarmIndicatorsNode alarmIndicatorsNode =
	// (AlarmIndicatorsNode)list.get(i);
	// if("1".equals(alarmIndicatorsNode.getEnabled())){
	// String indicators = alarmIndicatorsNode.getName();
	//					
	// String value = "";
	//					
	// if("buffercache".equals(indicators)){
	// value = (String)memeryHashtable.get(indicators);
	// }else if("dictionarycache".equals(indicators)){
	// value = (String)memeryHashtable.get(indicators);
	// }else if("pctmemorysorts".equals(indicators)){
	// value = (String)memeryHashtable.get(indicators);
	// }else if("pctbufgets".equals(indicators)){
	// value = (String)memeryHashtable.get(indicators);
	// }else if("ping".equals(indicators)){
	// value = pingvalue;
	// }else if("opencur".equals(indicators)){
	// value = (String)cursorsHashtable.get(indicators);
	// }else {
	// continue;
	// }
	// if(value == null)continue;
	// if(
	// AlarmConstant.DATATYPE_NUMBER.equals(alarmIndicatorsNode.getDatatype())){
	//						
	// try {
	// double value_int = Double.valueOf(value);
	// double Limenvalue2 =
	// Double.valueOf(alarmIndicatorsNode.getLimenvalue2());
	// double Limenvalue1 =
	// Double.valueOf(alarmIndicatorsNode.getLimenvalue1());
	// double Limenvalue0 =
	// Double.valueOf(alarmIndicatorsNode.getLimenvalue0());
	//							
	// String level = "";
	// String alarmTimes = "";
	// // �Ƿ񳬹���ֵ
	// boolean result = true;
	// if(alarmIndicatorsNode.getCompare()==0){
	// //����Ƚ�
	// if(value_int <= Limenvalue2){
	// level = "3";
	// alarmTimes = alarmIndicatorsNode.getTime2();
	// }else if(value_int <= Limenvalue1){
	// level = "2";
	// alarmTimes = alarmIndicatorsNode.getTime1();
	// }else if(value_int <= Limenvalue0){
	// level = "1";
	// alarmTimes = alarmIndicatorsNode.getTime0();
	// }else{
	// result = false;
	// // continue;
	// }
	// }else{
	// //����Ƚ�
	// if(value_int > Limenvalue2){
	// level = "3";
	// alarmTimes = alarmIndicatorsNode.getTime2();
	// }else if(value_int > Limenvalue1){
	// level = "2";
	// alarmTimes = alarmIndicatorsNode.getTime1();
	// }else if(value_int > Limenvalue0){
	// level = "1";
	// alarmTimes = alarmIndicatorsNode.getTime0();
	// }else{
	// result = false;
	// // continue;
	// }
	// }
	// String num =
	// (String)AlarmResourceCenter.getInstance().getAttribute(String.valueOf(alarmIndicatorsNode.getId()));
	// if(num == null || "".equals(num)){
	// num = "0";
	// }
	// if(!result){
	// // δ�����澯��ֵ ��ɾ���澯���͵�ʱ��ļ�¼
	// String name =
	// dbmonitorlist.getId()+":"+alarmIndicatorsNode.getType()+":"+alarmIndicatorsNode.getSubtype()+":"+alarmIndicatorsNode.getName();
	// SendAlarmTimeDao sendAlarmTimeDao = new SendAlarmTimeDao();
	// try {
	// sendAlarmTimeDao.delete(name);
	// //ɾ��node_indicator_alarm�еĸ澯��¼
	// NodeAlarmUtil nodeAlarmUtil = new NodeAlarmUtil();
	// nodeAlarmUtil.deleteByDeviceIdAndDeviceTypeAndIndicatorName(dbmonitorlist.getId()+"",
	// alarmIndicatorsNode.getType(), alarmIndicatorsNode.getName());
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// sendAlarmTimeDao.close();
	// }
	// // �����ʱδ�����澯 �� ��������Ϊ 0 ��
	// num = "0";
	// AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()),
	// num);
	// //��ǰָ���޸澯�������ж��ڴ����Ƿ��е�ǰָ��ĸ澯��Ϣ������������澯��Ϣ�����������κδ���
	// Hashtable checkEventHash = ShareData.getCheckEventHash();
	// if(checkEventHash != null && checkEventHash.size()>0){
	// if(checkEventHash.containsKey(name)){
	// //����澯�ѻָ����¼���Ϣ
	// Node
	// nodeVo=PollingEngine.getInstance().getNodeByID(dbmonitorlist.getId());
	// EventList eventList = checkEventUtil.createEvent(alarmIndicatorsNode,
	// nodeVo, value, name);
	// EventListDao eventListDao = new EventListDao();
	// try {
	// eventListDao.save(eventList);
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// eventListDao.close();
	// }
	// //ɾ��checkEvent�澯��Ϣ
	// checkEventUtil.deleteEvent(dbmonitorlist.getId()+"",alarmIndicatorsNode.getType(),alarmIndicatorsNode.getSubtype(),alarmIndicatorsNode.getName(),null);
	// }
	// }
	// return;
	// }
	// int num_int = Integer.valueOf(num);//��ǰ�澯����
	// int alarmTimes_int = Integer.valueOf(alarmTimes);//����ĸ澯����
	// if(num_int+1 >= alarmTimes_int){
	// // �澯
	// DBNode dbnode = (DBNode)
	// PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
	// dbnode.setAlarm(true);
	// List alarmList = dbnode.getAlarmMessage();
	// if (alarmList == null){
	// alarmList = new ArrayList();
	// }
	// dbnode.getAlarmMessage().add(alarmIndicatorsNode.getAlarm_info() + "
	// ��ǰֵΪ��" + value + alarmIndicatorsNode.getThreshlod_unit());
	// //������֮ǰ�ĸ澯����,������󼶱�
	// if(Integer.valueOf(level)> dbnode.getStatus()){
	// dbnode.setStatus(Integer.valueOf(level));
	// }
	//								
	// // createSMS(alarmIndicatorsNode.getType(),
	// alarmIndicatorsNode.getSubtype(), oracle.getAlias() , oracle.getId() +
	// "", alarmIndicatorsNode.getAlarm_info() + " ��ǰֵΪ��" + value +
	// alarmIndicatorsNode.getThreshlod_unit() , Integer.valueOf(level) , 1 ,
	// oracle.getAlias() , oracle.getBid(),oracle.getAlias() + "(" +
	// oracle.getAlias() + ")");
	// NodeUtil nodeUtil = new NodeUtil();
	// NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(dbmonitorlist);
	// checkEventUtil.sendAlarm(nodeDTO, alarmIndicatorsNode, value,
	// Integer.valueOf(level),0);
	// }else{
	// num_int = num_int + 1;
	// AlarmResourceCenter.getInstance().setAttribute(String.valueOf(alarmIndicatorsNode.getId()),
	// String.valueOf(num_int));
	// }
	//							
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// public void createSMS(String subtype,String subentity,String
	// ipaddress,String objid,String content,int flag,int checkday,String
	// sIndex,String bids,String sysLocation){
	// //��������
	// //���ڴ����õ�ǰ���IP��PING��ֵ
	// Calendar date=Calendar.getInstance();
	// Hashtable sendeddata = ShareData.getSendeddata();
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// try{
	// if
	// (!sendeddata.containsKey(subtype+":"+subentity+":"+ipaddress+":"+sIndex))
	// {
	// //�����ڣ��������ţ�������ӵ������б���
	// Smscontent smscontent = new Smscontent();
	// String time = sdf.format(date.getTime());
	// smscontent.setLevel(flag+"");
	// smscontent.setObjid(objid);
	// smscontent.setMessage(content);
	// smscontent.setRecordtime(time);
	// smscontent.setSubtype(subtype);
	// smscontent.setSubentity(subentity);
	// smscontent.setIp(ipaddress);
	// //���Ͷ���
	// SmscontentDao smsmanager=new SmscontentDao();
	// smsmanager.sendURLSmscontent(smscontent);
	// sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
	//				
	// } else {
	// //���ڣ�����ѷ��Ͷ����б����ж��Ƿ��Ѿ����͵���Ķ���
	// SmsDao smsDao = new SmsDao();
	// List list = new ArrayList();
	// String startTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date())
	// + " 00:00:00";
	// String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
	// Date());
	// try {
	// list = smsDao.findByEvent(content,startTime,endTime);
	// } catch (RuntimeException e) {
	// e.printStackTrace();
	// } finally {
	// smsDao.close();
	// }
	// if(list!=null&&list.size()>0){//�����б����Ѿ����͵���Ķ���
	// Calendar formerdate
	// =(Calendar)sendeddata.get(subtype+":"+subentity+":"+ipaddress+":"+sIndex);
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	// Date last = null;
	// Date current = null;
	// Calendar sendcalen = formerdate;
	// Date cc = sendcalen.getTime();
	// String tempsenddate = formatter.format(cc);
	//		 			
	// Calendar currentcalen = date;
	// Date ccc = currentcalen.getTime();
	// last = formatter.parse(tempsenddate);
	// String currentsenddate = formatter.format(ccc);
	// current = formatter.parse(currentsenddate);
	//		 			
	// long subvalue = current.getTime()-last.getTime();
	// if(checkday == 1){
	// //����Ƿ������˵��췢������,1Ϊ���,0Ϊ�����
	// if (subvalue/(1000*60*60*24)>=1){
	// //����һ�죬���ٷ���Ϣ
	// Smscontent smscontent = new Smscontent();
	// String time = sdf.format(date.getTime());
	// smscontent.setLevel(flag+"");
	// smscontent.setObjid(objid);
	// smscontent.setMessage(content);
	// smscontent.setRecordtime(time);
	// smscontent.setSubtype(subtype);
	// smscontent.setSubentity(subentity);
	// smscontent.setIp(ipaddress);//���Ͷ���
	// SmscontentDao smsmanager=new SmscontentDao();
	// smsmanager.sendURLSmscontent(smscontent);
	// //�޸��Ѿ����͵Ķ��ż�¼
	// sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
	// } else {
	// //��ʼд�¼�
	// //String sysLocation = "";
	// createEvent("poll",sysLocation,bids,content,flag,subtype,subentity,ipaddress,objid);
	// }
	// }
	// } else {
	// Smscontent smscontent = new Smscontent();
	// String time = sdf.format(date.getTime());
	// smscontent.setLevel(flag+"");
	// smscontent.setObjid(objid);
	// smscontent.setMessage(content);
	// smscontent.setRecordtime(time);
	// smscontent.setSubtype(subtype);
	// smscontent.setSubentity(subentity);
	// smscontent.setIp(ipaddress);
	// //���Ͷ���
	// SmscontentDao smsmanager=new SmscontentDao();
	// smsmanager.sendURLSmscontent(smscontent);
	// sendeddata.put(subtype+":"+subentity+":"+ipaddress+":"+sIndex,date);
	// }
	// 				
	// }
	// }catch(Exception e){
	// e.printStackTrace();
	// }
	// }

	// private void createEvent(String eventtype,String eventlocation,String
	// bid,String content,int level1,String subtype,String subentity,String
	// ipaddress,String objid){
	// //�����¼�
	// SysLogger.info("##############��ʼ�����¼�############");
	// EventList eventlist = new EventList();
	// eventlist.setEventtype(eventtype);
	// eventlist.setEventlocation(eventlocation);
	// eventlist.setContent(content);
	// eventlist.setLevel1(level1);
	// eventlist.setManagesign(0);
	// eventlist.setBak("");
	// eventlist.setRecordtime(Calendar.getInstance());
	// eventlist.setReportman("ϵͳ��ѯ");
	// eventlist.setBusinessid(bid);
	// eventlist.setNodeid(Integer.parseInt(objid));
	// eventlist.setOid(0);
	// eventlist.setSubtype(subtype);
	// eventlist.setSubentity(subentity);
	// EventListDao eventlistdao = new EventListDao();
	// try{
	// eventlistdao.save(eventlist);
	// }catch(Exception e){
	// e.printStackTrace();
	// }finally{
	// eventlistdao.close();
	// }
	// }

	/**
	 * ���ָ��serverip��Oracle������ʱ�������
	 * 
	 * @param dbdao
	 * @param serverip
	 */
	// private void clearOracleNmsTableData(DBDao dbdao, String serverip) {
	// dbdao.clearTableData("nms_oracontrfile", serverip);
	// dbdao.clearTableData("nms_oracursors", serverip);
	// dbdao.clearTableData("nms_oracursors", serverip);
	// dbdao.clearTableData("nms_oradbio", serverip);
	// dbdao.clearTableData("nms_oraextent", serverip);
	// dbdao.clearTableData("nms_oraisarchive", serverip);
	// dbdao.clearTableData("nms_orakeepobj", serverip);
	// dbdao.clearTableData("nms_oralock", serverip);
	// dbdao.clearTableData("nms_oralogfile", serverip);
	// dbdao.clearTableData("nms_oramemperfvalue", serverip);
	// dbdao.clearTableData("nms_oramemvalue", serverip);
	// dbdao.clearTableData("nms_orarollback", serverip);
	// dbdao.clearTableData("nms_orasessiondata", serverip);
	// dbdao.clearTableData("nms_oraspaces", serverip);
	// dbdao.clearTableData("nms_orastatus", serverip);
	// dbdao.clearTableData("nms_orasys", serverip);
	// dbdao.clearTableData("nms_oratables", serverip);
	// dbdao.clearTableData("nms_oratopsql", serverip);
	// dbdao.clearTableData("nms_orauserinfo", serverip);
	// dbdao.clearTableData("nms_orawait", serverip);
	// }
}
