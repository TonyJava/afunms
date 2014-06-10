package com.afunms.application.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.common.util.EncryptUtil;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.DBNode;
import com.afunms.polling.snmp.db.DB2DataCollector;
import com.afunms.polling.snmp.db.InformixDataCollector;
import com.afunms.polling.snmp.db.MySqlDataCollector;
import com.afunms.polling.snmp.db.OracleDataCollector;
import com.afunms.polling.snmp.db.SQLServerDataCollector;
import com.afunms.polling.snmp.db.SybaseDataCollector;

@SuppressWarnings("unchecked")
public class DBRefreshHelper {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	public DBRefreshHelper() {
	}


	public void execute(DBVo vo) {
		DBDao dbdao = null;
		try {

			List mslist = null;

			List oclist = null;

			List sysbaselist = null;

			List informixlist = null;

			List db2list = null;

			List mysqllist = null;

			if (vo != null) {
				dbdao = new DBDao();
				try {
					String password = EncryptUtil.decode(vo.getPassword());
					vo.setPassword(password);
				} catch (RuntimeException e1) {
					e1.printStackTrace();
				} finally {
					dbdao.close();
				}
				if (vo != null) {
					DBTypeDao typeDao = new DBTypeDao();
					DBTypeVo type = null;
					try {
						type = (DBTypeVo) typeDao.findByID(String.valueOf(vo.getDbtype()));
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						typeDao.close();
					}
					if ("MySql".equals(type.getDbtype())) {
						mysqllist = new ArrayList();
						mysqllist.add(vo);
					} else if ("SQLServer".equals(type.getDbtype())) {
						mslist = new ArrayList();
						mslist.add(vo);
					} else if ("Oracle".equals(type.getDbtype())) {
						oclist = new ArrayList();
						oclist.add(vo);
					} else if ("Sybase".equals(type.getDbtype())) {
						sysbaselist = new ArrayList();
						sysbaselist.add(vo);
					} else if ("Informix".equals(type.getDbtype())) {
						informixlist = new ArrayList();
						informixlist.add(vo);
					} else if ("DB2".equals(type.getDbtype())) {
						db2list = new ArrayList();
						db2list.add(vo);
					}
				}
			}

			// sqlserver采集
			if (mslist != null) {
				for (int i = 0; i < mslist.size(); i++) {
					DBVo dbmonitorlist = (DBVo) mslist.get(i);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					// 初始化数据库节点状态
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();

					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
					List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
					try {
						// 获取被启用的ORACLE所有被监视指标
						monitorItemList = indicatorsdao.getByNodeId(dbnode.getId() + "", 1, "db", "sqlserver");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						indicatorsdao.close();
					}
					if (monitorItemList == null)
						monitorItemList = new ArrayList<NodeGatherIndicators>();
					Hashtable gatherHash = new Hashtable();
					for (int k = 0; k < monitorItemList.size(); k++) {
						NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(k);
						gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
					}
					SQLServerDataCollector sqlservercollector = new SQLServerDataCollector();
					sqlservercollector.collect_data(dbnode.getId() + "", gatherHash);
				}
			}

			// 取得oracle采集
			if (oclist != null) {
				for (int i = 0; i < oclist.size(); i++) {
					Object obj = oclist.get(i);
					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					if (dbnode != null) {
						dbnode.setStatus(0);
						dbnode.setAlarm(false);
						dbnode.getAlarmMessage().clear();
						Calendar _tempCal = Calendar.getInstance();
						Date _cc = _tempCal.getTime();
						String _time = sdf.format(_cc);
						dbnode.setLastTime(_time);
					} else
						continue;

					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
					List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
					try {
						// 获取被启用的ORACLE所有被监视指标
						monitorItemList = indicatorsdao.getByNodeId(dbnode.getId() + "", 1, "db", "oracle");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						indicatorsdao.close();
					}
					if (monitorItemList == null)
						monitorItemList = new ArrayList<NodeGatherIndicators>();
					Hashtable gatherHash = new Hashtable();
					for (int k = 0; k < monitorItemList.size(); k++) {
						NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(k);
						gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
					}

					OracleDataCollector oraclecollector = new OracleDataCollector();
					oraclecollector.collect_data(dbnode.getId() + "", gatherHash);
				}

			}

			// 取得sysbase采集
			if (sysbaselist != null) {
				for (int i = 0; i < sysbaselist.size(); i++) {
					Object obj = sysbaselist.get(i);
					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();

					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
					List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
					try {
						// 获取被启用的ORACLE所有被监视指标
						monitorItemList = indicatorsdao.getByNodeId(dbnode.getId() + "", 1, "db", "sybase");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						indicatorsdao.close();
					}
					if (monitorItemList == null)
						monitorItemList = new ArrayList<NodeGatherIndicators>();
					Hashtable gatherHash = new Hashtable();
					NodeGatherIndicators nodeGatherIndicators = null;
					for (int k = 0; k < monitorItemList.size(); k++) {
						nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(k);
						gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
					}
					try {
						SybaseDataCollector sybasecollector = new SybaseDataCollector();
						sybasecollector.collect_Data(nodeGatherIndicators);
					} catch (Exception exc) {

					}
				}
			}

			// 取得informix采集
			if (informixlist != null) {
				for (int i = 0; i < informixlist.size(); i++) {
					Object obj = informixlist.get(i);
					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
					List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
					try {
						// 获取被启用的ORACLE所有被监视指标
						monitorItemList = indicatorsdao.getByNodeId(dbnode.getId() + "", 1, "db", "informix");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						indicatorsdao.close();
					}
					if (monitorItemList == null)
						monitorItemList = new ArrayList<NodeGatherIndicators>();
					Hashtable gatherHash = new Hashtable();
					for (int k = 0; k < monitorItemList.size(); k++) {
						NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(k);
						gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
					}
					InformixDataCollector informixcollector = new InformixDataCollector();
					informixcollector.collect_data(dbnode.getId() + "", gatherHash);
				}
			}
			// 取得db2采集
			if (db2list != null) {
				for (int i = 0; i < db2list.size(); i++) {
					Object obj = db2list.get(i);
					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();
					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
					List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
					try {
						// 获取被启用的ORACLE所有被监视指标
						monitorItemList = indicatorsdao.getByNodeId(dbnode.getId() + "", 1, "db", "db2");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						indicatorsdao.close();
					}
					if (monitorItemList == null)
						monitorItemList = new ArrayList<NodeGatherIndicators>();
					Hashtable gatherHash = new Hashtable();
					NodeGatherIndicators nodeGatherIndicators = null;
					for (int k = 0; k < monitorItemList.size(); k++) {
						nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(k);
						gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
					}
					DB2DataCollector db2collector = new DB2DataCollector();
					db2collector.collect_Data(nodeGatherIndicators);
				}
			}

			// 取得mysql采集
			if (mysqllist != null) {
				for (int i = 0; i < mysqllist.size(); i++) {
					Object obj = mysqllist.get(i);
					DBVo dbmonitorlist = new DBVo();
					BeanUtils.copyProperties(dbmonitorlist, obj);
					DBNode dbnode = (DBNode) PollingEngine.getInstance().getDbByID(dbmonitorlist.getId());
					dbnode.setAlarm(false);
					dbnode.setStatus(0);
					Calendar _tempCal = Calendar.getInstance();
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					dbnode.setLastTime(_time);
					dbnode.getAlarmMessage().clear();

					NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
					List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
					try {
						// 获取被启用的ORACLE所有被监视指标
						monitorItemList = indicatorsdao.getByNodeId(dbnode.getId() + "", 1, "db", "mysql");
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						indicatorsdao.close();
					}
					if (monitorItemList == null)
						monitorItemList = new ArrayList<NodeGatherIndicators>();
					Hashtable gatherHash = new Hashtable();
					NodeGatherIndicators nodeGatherIndicators = null;
					for (int k = 0; k < monitorItemList.size(); k++) {
						nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(k);
						gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
					}
					try {
						MySqlDataCollector mysqlcollector = new MySqlDataCollector();
						mysqlcollector.collect_Data(nodeGatherIndicators);
					} catch (Exception exc) {
						exc.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbdao.close();
		}
	}

}
