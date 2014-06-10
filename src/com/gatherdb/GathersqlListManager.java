package com.gatherdb;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.util.DataGate;
import com.database.DBManager;

/**
 * 
 * 采集数据sql内存数据管理对象列表与方法
 * 
 */
@SuppressWarnings("unchecked")
public class GathersqlListManager {
	public static Queue<String> queue = new LinkedList<String>();// 定时数据入库
	public static Queue<String> queue2 = new LinkedList<String>();// 定时数据入库
	public static Hashtable<String, Vector> datatemplist = new Hashtable();
	public static Hashtable<String, Vector> datatemplist2 = new Hashtable();
	public static Logger logger = Logger.getLogger(GathersqlListManager.class);
	public static boolean qflg = true; // 轮询队列状体
	public static boolean idbstatus = false;// 是否处于入库状态
	public static boolean datatempflg = true;// 轮询队列状体
	public static boolean idbdatatempstatus = false;// 是否处于入库状态

	public static Queue<String> queue_alarm = new LinkedList<String>();// 告警数据定时入库
	public static Queue<String> queue2_alarm = new LinkedList<String>();// 告警数据定时入库

	public static boolean qflg_alarm = true; // 轮询队列状体
	public static boolean idbstatus_alarm = false;// 是否处于入库状态
	public static boolean datatempflg_alarm = true;// 轮询队列状体
	public static boolean idbdatatempstatus_alarm = false;// 是否处于入库状态

	public static Queue<String> queue_SMS = new LinkedList<String>();// 告警数据定时入库
	public static Queue<String> queue2_SMS = new LinkedList<String>();// 告警数据定时入库

	public static boolean qflg_SMS = true; // 轮询队列状体
	public static boolean idbstatus_SMS = false;// 是否处于入库状态
	public static boolean datatempflg_SMS = true;// 轮询队列状体
	public static boolean idbdatatempstatus_SMS = false;// 是否处于入库状态

	/**
	 * 
	 * 把sql放入到内存队列，如果传递参数为DHCC-DB 表示数据入口
	 * 
	 * @param sql
	 *            字符参数有2个方式，一个是sql，一个是表示是入口（DHCC-DB）
	 */
	public static void Addsql(String sql) {

		if (sql.equals("DHCC-DB")) {
			if (qflg == true) {
				qflg = !qflg;
				if (GathersqlListManager.queue.size() > 1) {
					idbstatus = true;// 正在入库状态
					Connection conn = null;
					Statement stmt = null;
					try {
						conn = DataGate.getCon();
						conn.setAutoCommit(false);
						stmt = conn.createStatement();
						if (GathersqlListManager.queue.size() > 0) {
							String sql1 = "";
							while ((sql1 = GathersqlListManager.queue.poll()) != null) {
								stmt.addBatch(sql1);
							}
						}
						stmt.executeBatch();
						conn.commit();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							stmt.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						try {
							conn.commit();
							try {
								DataGate.freeCon(conn);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					idbstatus = false;
				}

			} else if (qflg == false) {
				qflg = !qflg;
				if (GathersqlListManager.queue2.size() > 1) {
					idbstatus = true;
					Connection conn = null;
					Statement stmt = null;
					try {
						conn = DataGate.getCon();
						conn.setAutoCommit(false);
						stmt = conn.createStatement();
						if (GathersqlListManager.queue2.size() > 0) {
							String sql1 = "";
							while ((sql1 = GathersqlListManager.queue2.poll()) != null) {
								stmt.addBatch(sql1);
							}
						}
						stmt.executeBatch();
						conn.commit();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							stmt.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						try {
							conn.commit();
							try {
								DataGate.freeCon(conn);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					idbstatus = false;
				}
			}
		} else {
			if (qflg) {
				synchronized (queue) {
					queue.offer(sql);
				}
			} else {
				synchronized (queue2) {
					queue2.offer(sql);
				}
			}
		}
	}

	/**
	 * 短信格式：：18611764841##告警内容 添加短信告警的内容到内存队列中 格式如下 18611764841##告警内容
	 * 
	 */
	public static void AddSMS(String sql) {
		if (qflg_SMS) {
			synchronized (queue_SMS) {
				queue_SMS.offer(sql);
			}
		} else {
			synchronized (queue2_SMS) {
				queue2_SMS.offer(sql);
			}
		}

	}

	/**
	 * 
	 * 把sql放入到内存队列，如果传递参数为DHCC-DB 表示数据入口
	 * 
	 * @param sql
	 *            字符参数有2个方式，一个是sql，一个是表示是入口（DHCC-DB）
	 */
	public static void Addsql_alarm(String sql) {
		if (sql.equals("DHCC-DB")) {
			if (qflg_alarm == true) {
				qflg_alarm = !qflg_alarm;
				if (GathersqlListManager.queue_alarm.size() > 0) {
					idbstatus_alarm = true;
					DBManager pollmg = new DBManager();// 数据库管理对象
					pollmg.excuteBatchSql(GathersqlListManager.queue_alarm);
					pollmg.close();
					pollmg = null;
					idbstatus_alarm = false;
				}
			} else if (qflg_alarm == false) {
				qflg_alarm = !qflg_alarm;
				if (GathersqlListManager.queue2_alarm.size() > 0) {
					idbstatus_alarm = true;
					DBManager pollmg = new DBManager();// 数据库管理对象
					pollmg.excuteBatchSql(GathersqlListManager.queue2_alarm);
					pollmg.close();
					pollmg = null;
					idbstatus_alarm = false;
				}
			}
		} else {
			if (qflg_alarm) {
				synchronized (queue_alarm) {
					queue_alarm.offer(sql);
				}
			} else {
				synchronized (queue2_alarm) {
					queue2_alarm.offer(sql);
				}
			}
		}
	}

	/**
	 * 数据分离模式sql入口 分为两种情况，当key为 DHCC-DB 表示调用数据入库接口，当key值为sql的删除语句时
	 * 表示把结果生成sql放入到内存列表中,这样做的目的是为了保证线程的安全和数据的完整性
	 * 
	 * @param sql
	 */
	public static void AdddateTempsql(String key, Vector sql) {
		if (key.equals("DHCC-DB")) {
			if (datatempflg == true) {
				datatempflg = !datatempflg;
				if (GathersqlListManager.datatemplist.size() > 0) {
					idbdatatempstatus = true;
					if (GathersqlListManager.datatemplist.size() > 0) {
						Iterator it = GathersqlListManager.datatemplist.keySet().iterator();
						Connection conn = null;
						Statement stmt = null;
						try {
							conn = DataGate.getCon();
							conn.setAutoCommit(false);
							stmt = conn.createStatement();
							while (it.hasNext()) {
								String keys;
								keys = (String) it.next();
								if (null != GathersqlListManager.datatemplist.get(keys)) {
									stmt.addBatch(keys);
									for (int i = 0; i < GathersqlListManager.datatemplist.get(keys).size(); i++) {
										stmt.addBatch((String) GathersqlListManager.datatemplist.get(keys).get(i).toString());
									}
								}
							}
							stmt.executeBatch();
							conn.commit();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								conn.commit();
								try {
									DataGate.freeCon(conn);
								} catch (Exception e) {
									e.printStackTrace();
								}
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							try {
								stmt.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						it = null;
					}
					GathersqlListManager.datatemplist.clear();
					idbdatatempstatus = false;
				}
			} else if (datatempflg == false) {
				datatempflg = !datatempflg;
				if (GathersqlListManager.datatemplist2.size() > 0) {
					idbdatatempstatus = true;
					if (GathersqlListManager.datatemplist2.size() > 0) {
						Iterator it = GathersqlListManager.datatemplist2.keySet().iterator();
						Connection conn = null;
						Statement stmt = null;
						try {
							conn = DataGate.getCon();
							conn.setAutoCommit(false);
							stmt = conn.createStatement();
							while (it.hasNext()) {
								String keys;
								keys = (String) it.next();
								if (null != GathersqlListManager.datatemplist2.get(keys)) {
									stmt.addBatch(keys);
									for (int i = 0; i < GathersqlListManager.datatemplist2.get(keys).size(); i++) {
										stmt.addBatch((String) GathersqlListManager.datatemplist2.get(keys).get(i).toString());
									}
								}
							}
							stmt.executeBatch();
							conn.commit();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								stmt.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
							try {
								conn.commit();
								try {
									DataGate.freeCon(conn);
								} catch (Exception e) {
									e.printStackTrace();
								}
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						it = null;
					}
					GathersqlListManager.datatemplist2.clear();
					idbdatatempstatus = false;
				}
			}
		} else if (key.startsWith("delete") || key.startsWith("DELETE")) {
			if (datatempflg) {
				datatemplist.put(key, sql);
			} else {
				datatemplist2.put(key, sql);
			}
		}
	}
}
