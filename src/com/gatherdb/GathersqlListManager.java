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
 * �ɼ�����sql�ڴ����ݹ�������б��뷽��
 * 
 */
@SuppressWarnings("unchecked")
public class GathersqlListManager {
	public static Queue<String> queue = new LinkedList<String>();// ��ʱ�������
	public static Queue<String> queue2 = new LinkedList<String>();// ��ʱ�������
	public static Hashtable<String, Vector> datatemplist = new Hashtable();
	public static Hashtable<String, Vector> datatemplist2 = new Hashtable();
	public static Logger logger = Logger.getLogger(GathersqlListManager.class);
	public static boolean qflg = true; // ��ѯ����״��
	public static boolean idbstatus = false;// �Ƿ������״̬
	public static boolean datatempflg = true;// ��ѯ����״��
	public static boolean idbdatatempstatus = false;// �Ƿ������״̬

	public static Queue<String> queue_alarm = new LinkedList<String>();// �澯���ݶ�ʱ���
	public static Queue<String> queue2_alarm = new LinkedList<String>();// �澯���ݶ�ʱ���

	public static boolean qflg_alarm = true; // ��ѯ����״��
	public static boolean idbstatus_alarm = false;// �Ƿ������״̬
	public static boolean datatempflg_alarm = true;// ��ѯ����״��
	public static boolean idbdatatempstatus_alarm = false;// �Ƿ������״̬

	public static Queue<String> queue_SMS = new LinkedList<String>();// �澯���ݶ�ʱ���
	public static Queue<String> queue2_SMS = new LinkedList<String>();// �澯���ݶ�ʱ���

	public static boolean qflg_SMS = true; // ��ѯ����״��
	public static boolean idbstatus_SMS = false;// �Ƿ������״̬
	public static boolean datatempflg_SMS = true;// ��ѯ����״��
	public static boolean idbdatatempstatus_SMS = false;// �Ƿ������״̬

	/**
	 * 
	 * ��sql���뵽�ڴ���У�������ݲ���ΪDHCC-DB ��ʾ�������
	 * 
	 * @param sql
	 *            �ַ�������2����ʽ��һ����sql��һ���Ǳ�ʾ����ڣ�DHCC-DB��
	 */
	public static void Addsql(String sql) {

		if (sql.equals("DHCC-DB")) {
			if (qflg == true) {
				qflg = !qflg;
				if (GathersqlListManager.queue.size() > 1) {
					idbstatus = true;// �������״̬
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
	 * ���Ÿ�ʽ����18611764841##�澯���� ��Ӷ��Ÿ澯�����ݵ��ڴ������ ��ʽ���� 18611764841##�澯����
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
	 * ��sql���뵽�ڴ���У�������ݲ���ΪDHCC-DB ��ʾ�������
	 * 
	 * @param sql
	 *            �ַ�������2����ʽ��һ����sql��һ���Ǳ�ʾ����ڣ�DHCC-DB��
	 */
	public static void Addsql_alarm(String sql) {
		if (sql.equals("DHCC-DB")) {
			if (qflg_alarm == true) {
				qflg_alarm = !qflg_alarm;
				if (GathersqlListManager.queue_alarm.size() > 0) {
					idbstatus_alarm = true;
					DBManager pollmg = new DBManager();// ���ݿ�������
					pollmg.excuteBatchSql(GathersqlListManager.queue_alarm);
					pollmg.close();
					pollmg = null;
					idbstatus_alarm = false;
				}
			} else if (qflg_alarm == false) {
				qflg_alarm = !qflg_alarm;
				if (GathersqlListManager.queue2_alarm.size() > 0) {
					idbstatus_alarm = true;
					DBManager pollmg = new DBManager();// ���ݿ�������
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
	 * ���ݷ���ģʽsql��� ��Ϊ�����������keyΪ DHCC-DB ��ʾ�����������ӿڣ���keyֵΪsql��ɾ�����ʱ
	 * ��ʾ�ѽ������sql���뵽�ڴ��б���,��������Ŀ����Ϊ�˱�֤�̵߳İ�ȫ�����ݵ�������
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
