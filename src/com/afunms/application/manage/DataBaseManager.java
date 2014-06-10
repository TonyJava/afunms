package com.afunms.application.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.afunms.alarm.dao.AlarmIndicatorsNodeDao;
import com.afunms.alarm.util.AlarmConstant;
import com.afunms.alarm.util.AlarmIndicatorsUtil;
import com.afunms.application.dao.DBDao;
import com.afunms.application.dao.DBTypeDao;
import com.afunms.application.dao.Db2spaceconfigDao;
import com.afunms.application.dao.HostApplyDao;
import com.afunms.application.dao.InformixspaceconfigDao;
import com.afunms.application.dao.MySqlSpaceConfigDao;
import com.afunms.application.dao.OraclePartsDao;
import com.afunms.application.dao.OraspaceconfigDao;
import com.afunms.application.dao.SqldbconfigDao;
import com.afunms.application.dao.SybspaceconfigDao;
import com.afunms.application.model.DBTypeVo;
import com.afunms.application.model.DBVo;
import com.afunms.application.model.MonitorDBDTO;
import com.afunms.application.model.OracleEntity;
import com.afunms.application.util.DBPool;
import com.afunms.application.util.IpTranslation;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ErrorMessage;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ChartXml;
import com.afunms.common.util.EncryptUtil;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SystemConstant;
import com.afunms.config.dao.SupperDao;
import com.afunms.config.model.Supper;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.dao.NodeGatherIndicatorsDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.model.NodeGatherIndicators;
import com.afunms.indicators.util.NodeGatherIndicatorsUtil;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.base.Node;
import com.afunms.polling.loader.DBLoader;
import com.afunms.polling.node.DBNode;
import com.afunms.system.model.TimeGratherConfig;
import com.afunms.system.model.User;
import com.afunms.system.util.TimeGratherConfigUtil;
import com.afunms.system.util.TimeShareConfigUtil;
import com.afunms.topology.dao.LineDao;
import com.afunms.topology.dao.ManageXmlDao;
import com.afunms.topology.dao.NodeDependDao;
import com.afunms.topology.model.ManageXml;
import com.afunms.topology.model.NodeDepend;
import com.afunms.topology.util.KeyGenerator;

@SuppressWarnings("unchecked")
public class DataBaseManager extends BaseManager implements ManagerInterface {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

	private String add() {
		DBVo vo = new DBVo();
		vo.setUser(getParaValue("user"));
		String password = getParaValue("password");
		String enpassword = "";
		try {
			enpassword = EncryptUtil.encode(password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		vo.setPassword(enpassword);
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		vo.setBid(getParaValue("bid"));
		vo.setManaged(getParaIntValue("managed"));
		vo.setDbtype(getParaIntValue("dbtype"));
		vo.setCollecttype(getParaIntValue("collecttype"));
		vo.setSupperid(getParaIntValue("supperid"));
		vo.setId(KeyGenerator.getInstance().getNextKey());
		DBLoader dbloader = new DBLoader();
		dbloader.loadOne(vo);
		// 放到内存中
		ShareData.getDBList().add(vo);
		DBDao dao = new DBDao();
		try {
			dao.save(vo);
			TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
			try {
				timeShareConfigUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtil.getObjectType("1"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			try {
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("1"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		// 保存应用
		HostApplyManager.save(vo);
		// 刷新内存中的数据库列表
		new DBLoader().refreshDBConfiglist();

		DBTypeVo dbTypeVo = null;
		DBTypeDao typedao = null;
		try {
			typedao = new DBTypeDao();
			dbTypeVo = (DBTypeVo) typedao.findByID(getParaValue("dbtype"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}

		// 初始化告警指标
		try {
			AlarmIndicatorsUtil alarmIndicatorsUtil = new AlarmIndicatorsUtil();
			alarmIndicatorsUtil.saveAlarmInicatorsThresholdForNode(String.valueOf(vo.getId()), AlarmConstant.TYPE_DB, dbTypeVo.getDbtype());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		// 初始化采集指标
		try {
			NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
			if (vo.getCollecttype() == 2) {
				vo.setCollecttype(1);
			}
			nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", AlarmConstant.TYPE_DB, dbTypeVo.getDbtype(), "1", vo.getCollecttype());
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		// 对数据库进行数据采集
		NodeGatherIndicatorsDao indicatorsdao = new NodeGatherIndicatorsDao();
		List<NodeGatherIndicators> monitorItemList = new ArrayList<NodeGatherIndicators>();
		try {
			// 获取被启用的数据库所有被监视指标
			monitorItemList = indicatorsdao.getByNodeId(vo.getId() + "", 1, "db", dbTypeVo.getDbtype());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			indicatorsdao.close();
		}
		if (monitorItemList == null) {
			monitorItemList = new ArrayList<NodeGatherIndicators>();
		}
		Hashtable gatherHash = new Hashtable();
		for (int i = 0; i < monitorItemList.size(); i++) {
			NodeGatherIndicators nodeGatherIndicators = (NodeGatherIndicators) monitorItemList.get(i);
			// 数据库采集指标
			gatherHash.put(nodeGatherIndicators.getName(), nodeGatherIndicators);
		}
		return list();
	}

	private String addmanage() {
		String sid = getParaValue("sid");
		int tsid = -1;
		boolean flag = false;
		try {
			tsid = Integer.parseInt(sid);
		} catch (Exception e) {
			flag = true;
		}
		DBVo tvo = new DBVo();
		if (tsid != -1 && !flag) {
			OracleEntity vo = new OracleEntity();
			OraclePartsDao dao = new OraclePartsDao();
			try {
				vo = (OracleEntity) dao.getOracleById(tsid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			vo.setManaged(1);
			dao = new OraclePartsDao();
			try {
				dao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			DBDao bdao = new DBDao();
			DBVo bvo = null;
			try {
				bvo = (DBVo) bdao.findByID(vo.getDbid() + "");
				bvo.setAlias(vo.getAlias());
				bvo.setBid(vo.getBid());
				bvo.setCollecttype(vo.getCollectType());
				bvo.setId(vo.getId());
				bvo.setIpAddress(bvo.getIpAddress() + ":" + vo.getSid());
				bvo.setManaged(vo.getManaged());
				bvo.setPassword(vo.getPassword());
				bvo.setUser(vo.getUser());
				bvo.setSendemail(vo.getGzerid());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				bdao.close();
			}
			tvo = bvo;
		} else {
			DBVo vo = new DBVo();
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(getParaValue("id"));
				vo.setManaged(1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			dao = new DBDao();
			try {
				dao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			tvo = vo;
		}
		if (PollingEngine.getInstance().getDbByID(tvo.getId()) != null) {
			DBNode dbNode = (DBNode) PollingEngine.getInstance().getDbByID(tvo.getId());
			dbNode.setUser(tvo.getUser());
			dbNode.setPassword(tvo.getPassword());
			dbNode.setPort(tvo.getPort());
			dbNode.setIpAddress(tvo.getIpAddress());
			dbNode.setAlias(tvo.getAlias());
			dbNode.setDbName(tvo.getDbName());
		} else {
			DBNode dbNode = new DBNode();
			dbNode.setUser(tvo.getUser());
			dbNode.setPassword(tvo.getPassword());
			dbNode.setPort(tvo.getPort());
			dbNode.setIpAddress(tvo.getIpAddress());
			dbNode.setAlias(tvo.getAlias());
			dbNode.setDbName(tvo.getDbName());
			dbNode.setId(tvo.getId());
			PollingEngine.getInstance().addDb(dbNode);
		}
		// 刷新内存中的数据库列表
		new DBLoader().refreshDBConfiglist();
		return "/db.do?action=list";
	}

	private String cancelmanage() {
		String sid = getParaValue("sid");
		int tsid = -1;
		boolean flag = false;
		try {
			tsid = Integer.parseInt(sid);
		} catch (Exception e) {
			flag = true;
		}
		if (tsid != -1 && !flag) {
			OracleEntity vo = new OracleEntity();
			OraclePartsDao dao = new OraclePartsDao();
			try {
				vo = (OracleEntity) dao.getOracleById(tsid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			vo.setManaged(0);
			dao = new OraclePartsDao();
			try {
				dao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

		} else {
			DBVo vo = new DBVo();
			DBDao dao = new DBDao();
			try {
				vo = (DBVo) dao.findByID(getParaValue("id"));
				vo.setManaged(0);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
			dao = new DBDao();
			try {
				dao.update(vo);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		// 刷新内存中的数据库列表
		// 初始化所有数据库
		DBDao dao = new DBDao();
		List list = null;
		try {
			list = dao.getDbByMonFlag(1);// 取出所有监视中的数据库
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			dao.close();
		}
		new DBLoader().clearRubbish(list);
		if (list == null) {
			list = new ArrayList();
		}
		ShareData.setDbconfiglist(list);
		return "/db.do?action=list";
	}

	private String check() {
		DBVo vo = new DBVo();
		DBDao dao = new DBDao();
		String strid = getParaValue("id");
		String sid = getParaValue("sid");
		if (sid != null && sid.length() > 0 && !"null".equals(sid)) {
			OraclePartsDao partdao = new OraclePartsDao();
			DBDao dbdao = null;
			try {
				partdao = new OraclePartsDao();
				dbdao = new DBDao();
				vo = (DBVo) dbdao.findByID(sid);
				strid = vo.getId() + "";
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (partdao != null) {
					partdao.close();
				}
				if (dbdao != null) {
					dbdao.close();
				}
			}

		} else {
			try {
				vo = (DBVo) dao.findByID(strid);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo typevo = null;
		try {
			typevo = (DBTypeVo) typedao.findByID(vo.getDbtype() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		session.setAttribute("id", strid);
		session.setAttribute("sid", sid);
		if (typevo.getDbtype().equalsIgnoreCase("oracle")) {
			return "/oracle.do?action=oracleping&id=" + strid + "&sid=" + sid;
		} else if (typevo.getDbtype().equalsIgnoreCase("sqlserver")) {
			return "/sqlserver.do?action=sqlserverping&id=" + getParaValue("id");
		} else if (typevo.getDbtype().equalsIgnoreCase("mysql")) {
			return "/mysql.do?action=mysqlping&id=" + getParaValue("id");
		} else if (typevo.getDbtype().equalsIgnoreCase("db2")) {
			return "/db2.do?action=db2ping&id=" + getParaValue("id");
		} else if (typevo.getDbtype().equalsIgnoreCase("sybase")) {
			return "/sybase.do?action=sybasecap&id=" + getParaValue("id");
		} else if (typevo.getDbtype().equalsIgnoreCase("informix")) {
			return "/informix.do?action=informixping&id=" + getParaValue("id");
		} else {
			return "/db.do?action=list";
		}
	}

	public String delete() {
		String id = getParaValue("radio");
		boolean flag = true;
		DBTypeVo sqlservertypevo = null;
		DBTypeVo db2typevo = null;
		DBTypeVo sybasetypevo = null;
		DBTypeVo infomixtypevo = null;
		DBTypeVo mysqltypevo = null;
		DBTypeVo oracletypevo = null;
		DBTypeDao typedao = new DBTypeDao();
		try {
			sqlservertypevo = (DBTypeVo) typedao.findByDbtype("sqlserver");
			db2typevo = (DBTypeVo) typedao.findByDbtype("db2");
			sybasetypevo = (DBTypeVo) typedao.findByDbtype("sybase");
			infomixtypevo = (DBTypeVo) typedao.findByDbtype("informix");
			mysqltypevo = (DBTypeVo) typedao.findByDbtype("mysql");
			oracletypevo = (DBTypeVo) typedao.findByDbtype("oracle");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		DBDao dbdao = null;
		try {
			dbdao = new DBDao();
			DBVo dbvo = (DBVo) dbdao.findByID(id + "");
			String dbtype = "";
			if (dbvo.getDbtype() == sybasetypevo.getId()) {
				SybspaceconfigDao spacedao = null;
				dbtype = "sybase";
				try {
					spacedao = new SybspaceconfigDao();
					spacedao.deleteByIP(dbvo.getId() + "");
					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
					try {
						dao.deleteByNodeIdAndTypeAndSubtype(dbvo.getId() + "", "db", "sybase");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(dbvo.getId() + "", "db", "sybase");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}
					// 根据serverip和数据库id删除存储采集数据表的数据
					DBDao dbDao = new DBDao();
					// 删除system_sybspaceconf表里的相关数据
					dbDao.clearTable("system_sybspaceconf", dbvo.getIpAddress());
					String hex = IpTranslation.formIpToHex(dbvo.getIpAddress());
					String serverip = hex + ":" + dbvo.getId();
					String[] tableNames = { "nms_sybasestatus", "nms_sybaseperformance", "nms_sybasedbinfo", "nms_sybaseengineinfo", "nms_sybaseprocessinfo", "nms_sybasedeviceinfo", "nms_sybaseuserinfo", "nms_sybaseserversinfo",
							"nms_sybasedbdetailinfo" };
					try {
						dbDao.clearTablesData(tableNames, serverip);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dbDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (spacedao != null) {
						spacedao.close();
					}
				}
			} else if (dbvo.getDbtype() == oracletypevo.getId()) {
				OraspaceconfigDao configDao = null;
				dbtype = "Oracle";
				DBDao bdao = new DBDao();
				try {
					configDao = new OraspaceconfigDao();
					DBVo vo = (DBVo) bdao.findByID(id + "");
					configDao.deleteByIP(id);

					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
					try {
						dao.deleteByNodeIdAndTypeAndSubtype(id, "db", "oracle");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(id, "db", "oracle");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}

					// 根据serverip和表名删除存储采集数据表的数据
					DBDao dbDao = new DBDao();
					String hex = IpTranslation.formIpToHex(vo.getIpAddress());
					String serverip = hex + ":" + id;

					String[] tableNames = { "nms_oracontrfile", "nms_oracursors", "nms_oradbio", "nms_oraextent", "nms_oraisarchive", "nms_orakeepobj", "nms_oralock", "nms_oralogfile", "nms_oramemperfvalue", "nms_oramemvalue",
							"nms_orarollback", "nms_orasessiondata", "nms_oraspaces", "nms_orastatus", "nms_orasys", "nms_oratables", "nms_oratopsql", "nms_orauserinfo", "nms_orawait", "nms_oratopsql_sort", "nms_oratopsql_readwrite",
							"nms_oralockinfo", "nms_orabaseinfo" };
					try {
						dbDao.clearTable("system_oraspaceconf", serverip);
						dbDao.clearTablesData(tableNames, serverip);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dbDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					configDao.close();
					bdao.close();
				}
			} else if (dbvo.getDbtype() == db2typevo.getId()) {
				dbtype = "db2";
				Db2spaceconfigDao db2dao = null;
				try {
					db2dao = new Db2spaceconfigDao();
					db2dao.deleteByIP(id);
					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
					try {
						dao.deleteByNodeIdAndTypeAndSubtype(dbvo.getId() + "", "db", "db2");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(dbvo.getId() + "", "db", "db2");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}
					// 根据serverip和数据库id删除存储采集数据表的数据
					DBDao dbDao = new DBDao();
					// 删除system_db2spaceconf表里的相关数据
					dbDao.clearTable("system_db2spaceconf", dbvo.getIpAddress());
					String hex = IpTranslation.formIpToHex(dbvo.getIpAddress());
					String serverip = hex + ":" + dbvo.getId();
					String[] tableNames = { "nms_db2tablespace", "nms_db2common", "nms_db2conn", "nms_db2variable", "nms_db2spaceinfo", "nms_db2log", "nms_db2write", "nms_db2pool", "nms_db2lock", "nms_db2read", "nms_db2session",
							"nms_db2cach", "nms_db2sysinfo" };
					try {
						dbDao.clearTablesData(tableNames, serverip);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dbDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (db2dao != null) {
						db2dao.close();
					}
				}
			} else if (dbvo.getDbtype() == infomixtypevo.getId()) {
				dbtype = "informix";
				InformixspaceconfigDao informixdao = null;
				try {
					informixdao = new InformixspaceconfigDao();
					informixdao.deleteByIP(id);
					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
					try {
						dao.deleteByNodeIdAndTypeAndSubtype(dbvo.getId() + "", "db", "informix");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(dbvo.getId() + "", "db", "informix");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}
					// 根据serverip和表名删除存储采集数据表的数据
					DBDao dbDao = new DBDao();
					dbDao.clearTable("system_infomixspaceconf", dbvo.getIpAddress());
					String hex = IpTranslation.formIpToHex(dbvo.getIpAddress());
					String serverip = hex + ":" + dbvo.getDbName();
					String[] tableNames = { "nms_informixabout", "nms_informixconfig", "nms_informixdatabase", "nms_informixio", "nms_informixlock", "nms_informixlog", "nms_informixother", "nms_informixsession", "nms_informixspace",
							"nms_informixstatus", "nms_informixbaractlog" };
					try {
						dbDao.clearTablesData(tableNames, serverip);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dbDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					informixdao.close();
				}
			} else if (sqlservertypevo.getId() == dbvo.getDbtype()) {
				dbtype = "sqlserver";
				SqldbconfigDao sqldao = null;
				try {
					sqldao = new SqldbconfigDao();
					sqldao.deleteByIP(id);
					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
					try {
						dao.deleteByNodeIdAndTypeAndSubtype(dbvo.getId() + "", "db", "sqlserver");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(dbvo.getId() + "", "db", "sqlserver");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}
					// 根据serverip和表名删除存储采集数据表的数据
					DBDao dbDao = new DBDao();
					// 删除system_sqldbconf表里的相关数据
					dbDao.clearTable("system_sqldbconf", dbvo.getIpAddress());
					String hex = IpTranslation.formIpToHex(dbvo.getIpAddress());
					String serverip = hex + ":" + dbvo.getAlias();
					String[] tableNames = { "nms_sqlservercaches", "nms_sqlserverconns", "nms_sqlserverdbvalue", "nms_sqlserverinfo_v", "nms_sqlserverlockinfo_v", "nms_sqlserverlocks", "nms_sqlservermems", "nms_sqlserverpages",
							"nms_sqlserverscans", "nms_sqlserversqls", "nms_sqlserverstatisticshash", "nms_sqlserverstatus", "nms_sqlserversysvalue" };
					try {
						dbDao.clearTablesData(tableNames, serverip);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dbDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sqldao.close();
				}
			} else if (mysqltypevo.getId() == dbvo.getDbtype()) {
				dbtype = "mysql";
				MySqlSpaceConfigDao sqldao = null;
				try {
					sqldao = new MySqlSpaceConfigDao();
					sqldao.deleteByIP(id);
					// 删除该数据库的采集指标
					NodeGatherIndicatorsDao dao = new NodeGatherIndicatorsDao();
					try {
						dao.deleteByNodeIdAndTypeAndSubtype(dbvo.getId() + "", "db", "mysql");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dao.close();
					}
					// 删除该数据库的告警阀值
					AlarmIndicatorsNodeDao indidao = new AlarmIndicatorsNodeDao();
					try {
						indidao.deleteByNodeId(dbvo.getId() + "", "db", "mysql");
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						indidao.close();
					}

					// 根据serverip和表名删除存储采集数据表的数据
					DBDao dbDao = new DBDao();
					// 删除system_mysqlspaceconf表里的相关数据
					dbDao.clearTable("system_mysqlspaceconf", dbvo.getIpAddress());
					String hex = IpTranslation.formIpToHex(dbvo.getIpAddress());
					String serverip = hex + ":" + dbvo.getId();
					try {
						dbDao.clearTableData("nms_mysqlinfo", serverip);
					} catch (RuntimeException e) {
						e.printStackTrace();
					} finally {
						dbDao.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					sqldao.close();
				}
			}
			if (!dbtype.equals("")) {
				// 删除应用
				HostApplyDao hostApplyDao = null;
				try {
					hostApplyDao = new HostApplyDao();
					hostApplyDao.delete(" where ipaddress = '" + dbvo.getIpAddress() + "' and type = 'db' and subtype = '" + dbtype + "' and nodeid = '" + dbvo.getId() + "'");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (hostApplyDao != null) {
						hostApplyDao.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dbdao != null) {
				dbdao.close();
			}
		}
		if (flag) {
			DBDao dao = new DBDao();
			try {
				dao.delete(id);
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				timeShareConfigUtil.deleteTimeShareConfig(id, timeShareConfigUtil.getObjectType("1"));
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.deleteTimeGratherConfig(id, timeGratherConfigUtil.getObjectType("1"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}

			int nodeId = Integer.parseInt(id);
			PollingEngine.getInstance().deleteDbByID(nodeId);// yangjun
			DBPool.getInstance().removeConnect(nodeId);
			// 刷新内存中的数据库列表
			new DBLoader().refreshDBConfiglist();
		}

		// 更新业务视图
		NodeDependDao nodedependao = new NodeDependDao();
		List list = nodedependao.findByNode("dbs" + id);
		if (list != null && list.size() > 0) {
			for (int j = 0; j < list.size(); j++) {
				NodeDepend vo = (NodeDepend) list.get(j);
				if (vo != null) {
					LineDao lineDao = new LineDao();
					lineDao.deleteByidXml("dbs" + id, vo.getXmlfile());
					NodeDependDao nodeDependDao = new NodeDependDao();
					if (nodeDependDao.isNodeExist("dbs" + id, vo.getXmlfile())) {
						nodeDependDao.deleteByIdXml("dbs" + id, vo.getXmlfile());
					} else {
						nodeDependDao.close();
					}
					User user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
					ManageXmlDao mXmlDao = new ManageXmlDao();
					List xmlList = new ArrayList();
					try {
						xmlList = mXmlDao.loadByPerAll(user.getBusinessids());
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						mXmlDao.close();
					}
					try {
						ChartXml chartxml;
						chartxml = new ChartXml("tree");
						chartxml.addViewTree(xmlList);
					} catch (Exception e) {
						e.printStackTrace();
					}

					ManageXmlDao subMapDao = new ManageXmlDao();
					ManageXml manageXml = (ManageXml) subMapDao.findByXml(vo.getXmlfile());
					if (manageXml != null) {
						NodeDependDao nodeDepenDao = new NodeDependDao();
						try {
							List lists = nodeDepenDao.findByXml(vo.getXmlfile());
							ChartXml chartxml;
							chartxml = new ChartXml("NetworkMonitor", "/" + vo.getXmlfile().replace("jsp", "xml"));
							chartxml.addBussinessXML(manageXml.getTopoName(), lists);
							ChartXml chartxmlList;
							chartxmlList = new ChartXml("NetworkMonitor", "/" + vo.getXmlfile().replace("jsp", "xml").replace("businessmap", "list"));
							chartxmlList.addListXML(manageXml.getTopoName(), lists);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							nodeDepenDao.close();
						}
					}
				}
			}
		}
		return "/db.do?action=list";
	}

	public String execute(String action) {
		if (action.equals("list")) {
			return list();
		}
		if (action.equals("ready_add")) {
			return ready_add();
		}
		if (action.equals("add")) {
			return add();
		}
		if (action.equals("delete")) {
			return delete();
		}
		if (action.equals("ready_edit")) {
			return ready_edit();
		}
		if (action.equals("update")) {
			return update();
		}
		if (action.equals("cancelmanage")) {
			return cancelmanage();
		}
		if (action.equals("addmanage")) {
			return addmanage();
		}
		if (action.equals("check")) {
			return check();
		}
		setErrorCode(ErrorMessage.ACTION_NO_FOUND);
		return null;
	}

	/**
	 * 获得业务权限的 SQL 语句
	 * 
	 * @author nielin
	 * @date 2010-08-13
	 * @return
	 */
	public String getBidSql() {

		User current_user = (User) session.getAttribute(SessionConstant.CURRENT_USER);
		String curentbids = current_user.getBusinessids();

		String selectbids = getParaValue("selectbids");

		StringBuffer sql1 = new StringBuffer();
		StringBuffer s1 = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();
		StringBuffer s2 = new StringBuffer();
		int flag = 0;

		if (selectbids != null) {
			if (selectbids != "-1") {
				String[] bids = selectbids.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								s2.append(" and ( bid like '%" + bids[i].trim() + "%' ");
								flag = 1;
							} else {
								s2.append(" or bid like '%" + bids[i].trim() + "%' ");
							}
						}
					}
					s2.append(") ");
				}
			}
		}
		sql2.append("select * from app_db_node where 1=1 " + s2.toString());
		flag = 0;
		if (current_user.getRole() != 0 && curentbids != null) {
			if (curentbids != "-1") {
				String[] bids = curentbids.split(",");
				if (bids.length > 0) {
					for (int i = 0; i < bids.length; i++) {
						if (bids[i].trim().length() > 0) {
							if (flag == 0) {
								s1.append(" and ( bid like '%" + bids[i].trim() + "%' ");
								flag = 1;
							} else {
								s1.append(" or bid like '%" + bids[i].trim() + "%' ");
							}
						}
					}
					s1.append(") ");
				}
			}
		}
		if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
			sql1.append("select * from (" + sql2.toString() + ") as t where 1=1 " + s1.toString());
		} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
			sql1.append("select * from (" + sql2.toString() + ") where 1=1 " + s1.toString());
		}

		String treeBid = request.getParameter("treeBid");
		if (treeBid != null && treeBid.trim().length() > 0) {
			treeBid = treeBid.trim();
			treeBid = "," + treeBid + ",";
			String[] treeBids = treeBid.split(",");
			if (treeBids != null) {
				for (int i = 0; i < treeBids.length; i++) {
					if (treeBids[i].trim().length() > 0) {
						sql1 = sql1.append(" and " + "bid" + " like '%," + treeBids[i].trim() + ",%'");
					}
				}
			}
		}
		return sql1.toString();

	}

	/**
	 * 获取 list
	 * 
	 * @author nielin
	 * @date 2010-08-13
	 * @param <code>DBVo</code>
	 * @return
	 */
	public List getList() {
		List list = new ArrayList();
		String sql = getBidSql();
		DBDao dao = new DBDao();
		try {
			list = dao.findByCriteria(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return list;
	}

	public String getMonFlagSql() {
		String mon_flag = getParaValue("flag");
		String sql = "";
		if (mon_flag != null && "1".equals(mon_flag)) {
			sql = " and managed='1'";
		}
		request.setAttribute("flag", mon_flag);
		return sql;
	}

	/**
	 * 通过 DBVo 来组装 MonitorDBDTO
	 * 
	 * @author nielin
	 * @date 2010-08-13
	 * @param <code>DBVo</code>
	 * @return
	 */
	public MonitorDBDTO getMonitorDBDTOByDBVo(DBVo vo, int sid) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String date = simpleDateFormat.format(new Date());
		String starttime = date + " 00:00:00";
		String totime = date + " 23:59:59";
		int id = vo.getId(); // id
		String ipAddress = vo.getIpAddress(); // ipaddress
		String alias = vo.getAlias(); // 名称
		String dbname = vo.getDbName(); // 数据库名称
		String port = vo.getPort(); // 端口
		String mon_flag = "否";
		String dbtype = ""; // 数据库类型
		String status = ""; // 状态
		String pingValue = ""; // 可用性
		int alarmLevel = 0;
		Hashtable eventListSummary = new Hashtable(); // 告警

		if (vo.getManaged() == 1) {
			mon_flag = "是";
		}
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		Node DBNode = null;
		if (sid != 0) {
			DBNode = PollingEngine.getInstance().getDbByID(sid);
		} else {
			DBNode = PollingEngine.getInstance().getDbByID(vo.getId());
		}
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(DBNode);
		if (nodeDTO != null) {
			String chexkname = id + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}
		status = alarmLevel + "";
		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo dbTypeVo = null;
		try {
			dbTypeVo = (DBTypeVo) typedao.findByID(String.valueOf(vo.getDbtype()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			typedao.close();
		}
		if (dbTypeVo != null) {
			dbtype = dbTypeVo.getDbtype();
		} else {
			dbtype = "未知";
		}
		pingValue = "服务停止";
		if ("Oracle".equalsIgnoreCase(dbtype)) {
			DBDao dao = new DBDao();
			Hashtable oracleHash = (Hashtable) ShareData.getSharedata().get(ipAddress + ":" + id);
			try {
				String statusStr = "0";
				if (oracleHash != null) {
					statusStr = String.valueOf(oracleHash.get("ping"));
				}
				if ("100".equals(statusStr)) {
					pingValue = "正在运行";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		} else if ("SQLServer".equalsIgnoreCase(dbtype)) {
			DBDao dbDao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getAlias();
			Hashtable sqlserverHash = (Hashtable) ShareData.getSharedata().get(serverip);
			try {
				String statusStr = "0";
				if (sqlserverHash != null) {
					statusStr = String.valueOf(sqlserverHash.get("ping"));// String.valueOf(statusHashtable.get("status"));
				}
				if ("100".equals(statusStr)) {
					pingValue = "正在运行";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbDao.close();
			}
		} else if ("MySql".equalsIgnoreCase(dbtype)) {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getId();
			Hashtable statusHashtable;
			try {
				statusHashtable = dao.getMysql_nmsstatus(serverip);
				String statusStr = (String) statusHashtable.get("status");
				if ("1".equals(statusStr)) {
					pingValue = "正在运行";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}
		} else if ("DB2".equalsIgnoreCase(dbtype)) {
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			DBDao dao = new DBDao();
			String serverip = hex + ":" + vo.getId();
			String statusStr = "0";
			Hashtable tempStatusHashtable = null;
			try {
				tempStatusHashtable = dao.getDB2_nmsstatus(serverip + "");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}
			if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
				statusStr = (String) tempStatusHashtable.get("status");
			}
			if (statusStr.equals("1")) {
				pingValue = "正在运行";
			}
		} else if ("Sybase".equalsIgnoreCase(dbtype)) {
			// 获取sybase信息
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			DBDao dao = new DBDao();
			String serverip = hex + ":" + vo.getId();
			String statusStr = "0";
			Hashtable tempStatusHashtable = null;
			try {
				tempStatusHashtable = dao.getSybase_nmsstatus(serverip);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (dao != null) {
					dao.close();
				}
			}
			if (tempStatusHashtable != null && tempStatusHashtable.containsKey("status")) {
				statusStr = (String) tempStatusHashtable.get("status");
			}
			if (statusStr.equals("1")) {
				pingValue = "正在运行";
			}
		} else if ("Informix".equalsIgnoreCase(dbtype)) {
			DBDao dao = new DBDao();
			String hex = IpTranslation.formIpToHex(vo.getIpAddress());
			String serverip = hex + ":" + vo.getDbName();
			String statusStr;
			try {
				statusStr = String.valueOf(((Hashtable) dao.getInformix_nmsstatus(serverip)).get("status"));
				if ("1".equalsIgnoreCase(statusStr)) {
					pingValue = "正在运行";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dao.close();
			}
		}

		String generalAlarm = "0"; // 普通告警数 默认为 0
		String urgentAlarm = "0"; // 严重告警数 默认为 0
		String seriousAlarm = "0"; // 紧急告警数 默认为 0

		EventListDao eventListDao = new EventListDao();
		try {
			if ("mysql".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where subtype = 'db' and nodeid='" + id + "'" + " and level1='1' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				urgentAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='2' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
				seriousAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='3' and recordtime>='" + starttime + "' and recordtime<='" + totime + "'");
			} else if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
				generalAlarm = eventListDao.getCountByWhere(" where subtype = 'db' and nodeid='" + id + "'" + " and level1='1' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime
						+ "','YYYY-MM-DD HH24:MI:SS')");
				urgentAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='2' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime
						+ "','YYYY-MM-DD HH24:MI:SS')");
				seriousAlarm = eventListDao.getCountByWhere(" where  subtype = 'db' and nodeid='" + id + "'" + " and level1='3' and recordtime>=to_date('" + starttime + "','YYYY-MM-DD HH24:MI:SS') and recordtime<=to_date('" + totime
						+ "','YYYY-MM-DD HH24:MI:SS')");
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			eventListDao.close();
		}
		eventListSummary.put("generalAlarm", generalAlarm);
		eventListSummary.put("urgentAlarm", urgentAlarm);
		eventListSummary.put("seriousAlarm", seriousAlarm);

		MonitorDBDTO monitorDBDTO = new MonitorDBDTO();
		monitorDBDTO.setId(id);
		monitorDBDTO.setAlias(alias);
		monitorDBDTO.setDbname(dbname);
		monitorDBDTO.setDbtype(dbtype);
		monitorDBDTO.setPingValue(pingValue);
		monitorDBDTO.setEventListSummary(eventListSummary);
		monitorDBDTO.setIpAddress(ipAddress);
		monitorDBDTO.setPort(port);
		monitorDBDTO.setStatus(status);
		monitorDBDTO.setMon_flag(mon_flag);

		return monitorDBDTO;
	}

	private String list() {
		List list = getList();
		List monitorDBDTOList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			DBVo vo = (DBVo) list.get(i);
			MonitorDBDTO monitorDBDTO = null;
			monitorDBDTO = getMonitorDBDTOByDBVo(vo, 0);
			monitorDBDTOList.add(monitorDBDTO);
		}
		if (monitorDBDTOList == null) {
			monitorDBDTOList = new ArrayList();
		}
		request.setAttribute("list", monitorDBDTOList);
		return "/application/db/list.jsp";
	}

	private String ready_add() {
		SupperDao supperdao = new SupperDao();
		List<Supper> allSupper = supperdao.loadAll();
		String flag = getParaValue("flag");
		request.setAttribute("allSupper", allSupper);
		request.setAttribute("flag", flag);
		return "/application/db/add.jsp";
	}

	/**
	 * @nielin modify send edit.jsp
	 */
	private String ready_edit() {
		DBDao dao = new DBDao();
		try {
			boolean flag = false;
			int sid = -1;
			try {
				sid = Integer.parseInt(getParaValue("sid"));
			} catch (Exception e) {
				// e.printStackTrace();
				flag = true;
			}
			List timeShareConfigList = null;
			if (!flag && (sid != -1)) {
				readyEdit(dao);
				OraclePartsDao oracleDao = null;
				try {
					oracleDao = new OraclePartsDao();
					OracleEntity oracle = (OracleEntity) oracleDao.getOracleById(sid);
					DBVo vo = (DBVo) dao.findByID(oracle.getDbid() + "");
					vo.setAlias(oracle.getAlias());
					vo.setCollecttype(oracle.getCollectType());
					vo.setDbName(oracle.getSid());
					vo.setManaged(oracle.getManaged());
					vo.setPassword(oracle.getPassword());
					vo.setUser(oracle.getUser());
					vo.setSendemail(oracle.getGzerid());
					vo.setBid(oracle.getBid());
					request.setAttribute("vo", vo);
					request.setAttribute("sid", sid + "");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					oracleDao.close();
				}
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id") + ":oracle" + sid, timeShareConfigUtil.getObjectType("1"));

				SupperDao supperdao = new SupperDao();
				List<Supper> allSupper = supperdao.loadAll();
				request.setAttribute("allSupper", allSupper);
				TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
				List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("1"));
				for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
					timeGratherConfig.setHourAndMin();
				}
				request.setAttribute("timeGratherConfigList", timeGratherConfigList);

			} else {
				request.setAttribute("sid", "");
				readyEdit(dao);
				TimeShareConfigUtil timeShareConfigUtil = new TimeShareConfigUtil();
				timeShareConfigList = timeShareConfigUtil.getTimeShareConfigList(getParaValue("id"), timeShareConfigUtil.getObjectType("1"));
				SupperDao supperdao = new SupperDao();
				List<Supper> allSupper = supperdao.loadAll();
				request.setAttribute("allSupper", allSupper);
				TimeGratherConfigUtil tg = new TimeGratherConfigUtil();
				List<TimeGratherConfig> timeGratherConfigList = tg.getTimeGratherConfig(getParaValue("id"), tg.getObjectType("1"));
				for (TimeGratherConfig timeGratherConfig : timeGratherConfigList) {
					timeGratherConfig.setHourAndMin();
				}
				request.setAttribute("timeGratherConfigList", timeGratherConfigList);

			}

			request.setAttribute("timeShareConfigList", timeShareConfigList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return "/application/db/edit.jsp";
	}

	private String update() {
		DBVo vo = new DBVo();
		vo.setId(getParaIntValue("id"));
		vo.setUser(getParaValue("user"));
		// 需要判断用户输入的密码跟数据库里的密码是否一致,若一致,说明没修改密码,若不一致,需要用新密码加密后替换原来的加密密码
		String password = getParaValue("password");
		DBDao dbDao = new DBDao();
		DBVo dbVo = new DBVo();

		DBTypeDao typedao = new DBTypeDao();
		DBTypeVo dbTypeVo = (DBTypeVo) typedao.findByID(getParaValue("dbtype"));

		try {
			dbVo = (DBVo) dbDao.findByID(vo.getId() + "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbDao.close();
		}
		if (dbVo != null) {
			if (dbVo.getPassword().equals(password)) {
				vo.setPassword(password);
			} else {
				// 需要加密后替换原来密码
				String newPassword = "";
				try {
					newPassword = EncryptUtil.encode(password);
				} catch (Exception e) {
					e.printStackTrace();
				}
				vo.setPassword(newPassword);
			}
		}
		vo.setAlias(getParaValue("alias"));
		vo.setIpAddress(getParaValue("ip_address"));
		vo.setPort(getParaValue("port"));
		vo.setDbName(getParaValue("db_name"));
		vo.setCategory(getParaIntValue("category"));
		vo.setDbuse(getParaValue("dbuse"));
		vo.setSendmobiles(getParaValue("sendmobiles"));
		vo.setSendemail(getParaValue("sendemail"));
		vo.setSendphone(getParaValue("sendphone"));
		String sid = getParaValue("sid");
		int tsid = -1;
		String allbid = getParaValue("bid");
		vo.setBid(allbid);
		vo.setManaged(getParaIntValue("managed"));
		vo.setDbtype(getParaIntValue("dbtype"));
		vo.setCollecttype(getParaIntValue("collecttype"));
		vo.setSupperid(getParaIntValue("supperid"));
		boolean flag = false;
		try {
			tsid = Integer.parseInt(sid);
		} catch (Exception e) {
//			e.printStackTrace();
			flag = true;
		}
		OraclePartsDao oraDao = null;
		OraspaceconfigDao configDao = null;
		if (tsid != -1 && !flag) {
			DBDao dao = null;
			try {
				oraDao = new OraclePartsDao();
				configDao = new OraspaceconfigDao();
				String hexIp = IpTranslation.formIpToHex(vo.getIpAddress());
				configDao.deleteByIP(hexIp + ":" + tsid);
				OracleEntity oracle = new OracleEntity();
				oracle.setAlias(vo.getAlias());
				oracle.setCollectType(vo.getCollecttype());
				oracle.setDbid(vo.getId());
				oracle.setGzerid(vo.getSendemail());
				oracle.setId(Integer.parseInt(sid));
				oracle.setManaged(vo.getManaged());
				oracle.setPassword(vo.getPassword());
				oracle.setSid(vo.getDbName());
				oracle.setUser(vo.getUser());
				oracle.setBid(vo.getBid());

				oraDao.update(oracle);
				dao = new DBDao();
				dao.update(vo);
				if (PollingEngine.getInstance().getNodeByID(oracle.getId()) != null) {
					DBNode dbNode = (DBNode) PollingEngine.getInstance().getNodeByID(vo.getId());
					dbNode.setUser(vo.getUser());
					dbNode.setPassword(vo.getPassword());
					dbNode.setPort(vo.getPort());
					dbNode.setIpAddress(vo.getIpAddress());
					dbNode.setAlias(vo.getAlias());
					dbNode.setDbName(vo.getDbName());
					dbNode.setCollecttype(vo.getCollecttype());
					dbNode.setBid(vo.getBid());
				}
				TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
				timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("1"));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (oraDao != null) {
					oraDao.close();
				}
				if (configDao != null) {
					configDao.close();
				}
				if (dao != null) {
					dao.close();
				}
			}
			return list();
		}

		DBDao dao = null;
		try {
			dao = new DBDao();
			dao.update(vo);
			TimeShareConfigUtil timeShareConfigUtiligUtil = new TimeShareConfigUtil(); // nielin
			timeShareConfigUtiligUtil.saveTimeShareConfigList(request, String.valueOf(vo.getId()), timeShareConfigUtiligUtil.getObjectType("1"));
			TimeGratherConfigUtil timeGratherConfigUtil = new TimeGratherConfigUtil();
			timeGratherConfigUtil.saveTimeGratherConfigList(request, String.valueOf(vo.getId()), timeGratherConfigUtil.getObjectType("1"));

			if (PollingEngine.getInstance().getNodeByID(vo.getId()) != null) {
				DBNode dbNode = (DBNode) PollingEngine.getInstance().getNodeByID(vo.getId());
				dbNode.setUser(vo.getUser());
				dbNode.setPassword(vo.getPassword());
				dbNode.setPort(vo.getPort());
				dbNode.setIpAddress(vo.getIpAddress());
				dbNode.setAlias(vo.getAlias());
				dbNode.setDbName(vo.getDbName());
				dbNode.setCollecttype(vo.getCollecttype());
			}
			// 刷新内存中的数据库列表
			new DBLoader().refreshDBConfiglist();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dao != null) {
				dao.close();
			}
		}
		// 更新采集方式
		try {
			if ((dbVo.getCollecttype() != 3 && vo.getCollecttype() == 3) || (dbVo.getCollecttype() == 3 && vo.getCollecttype() != 3)) {
				NodeGatherIndicatorsUtil nodeGatherIndicatorsUtil = new NodeGatherIndicatorsUtil();
				if (vo.getCollecttype() == 2) {
					vo.setCollecttype(1);
				}
				nodeGatherIndicatorsUtil.deleteAllGatherIndicatorsForNode(vo.getId() + "", AlarmConstant.TYPE_DB, "oracle");
				nodeGatherIndicatorsUtil.addGatherIndicatorsForNode(vo.getId() + "", AlarmConstant.TYPE_DB, dbTypeVo.getDbtype(), "1", vo.getCollecttype());
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		return list();
	}

}
