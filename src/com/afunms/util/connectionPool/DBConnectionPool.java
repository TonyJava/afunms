package com.afunms.util.connectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.afunms.common.util.SystemConstant;

@SuppressWarnings("unchecked")
class ConnectionReaper extends Thread {

	private DBConnectionPool pool;
	private final long delay = 300000;

	ConnectionReaper(DBConnectionPool pool) {
		this.pool = pool;
	}

	public void run() {
		while (true) {
			try {
				sleep(delay);
			} catch (InterruptedException e) {
			}
			pool.reapIdleConnections();
		}
	}
}

@SuppressWarnings( { "unchecked", "unused" })
public class DBConnectionPool {
	private Logger logger = Logger.getLogger(this.getClass());
	private Vector poolConnections;
	private int minConn;
	private int maxConn;
	private String name;
	private String password;
	private String URL;
	private String user;
	private int connectUseTimeout;
	private int connectUseCount;
	private int connectCheckOutTimeout;
	private int numRequests;// ��Ҫ��������
	private int numWaits;// �ȴ��еĿͻ�������
	private int numCheckOut;// �Ѿ�ȡ����������
	private String driverClassName;
	private ConnectionReaper reaper;

	/**
	 * �ҵ����������Ӷ���connection������(���ӳ�)�е�index
	 * <p>
	 * Find the given connection in the pool
	 * 
	 * @return Index into the pool, or -1 if not found
	 */
	private int find(java.sql.Connection con, java.util.Vector vec) {
		int index = -1;
		// Find the matching Connection in the pool
		if ((con != null) && (vec != null)) {
			for (int i = 0; i < vec.size(); i++) {
				ConnectionObject co = (ConnectionObject) vec.elementAt(i);
				if (co.getCon() == con) {
					index = i;
					break;
				}
			}
		}
		return index;
	}

	/**
	 * <p>
	 * Sets the last access time for the given ConnectionObject
	 * 
	 * @param co
	 *            ��Ҫ����������ʱ���ConnectionObject����
	 */
	private void touch(ConnectionObject co) {
		if (co != null) {
			co.setLastAccess(System.currentTimeMillis());
		}
	}

	/**
	 * �ر�ConnectionObject�е�����
	 * <p>
	 * Closes the connection in the given ConnectionObject
	 * 
	 * @param connectObject
	 *            ConnectionObject
	 */
	private void close(ConnectionObject connectionObject) {
		if (connectionObject != null) {
			if (connectionObject.getCon() != null) {
				try {

					// Close the connection
					connectionObject.getCon().close();
				} catch (Exception ex) {
					// Ignore any exceptions during close
				}

				// Clear the connection object reference
				connectionObject.setCon(null);
			}
		}
	}

	/**
	 * ������ʹ�õ����ӷ��ظ����ӳ�
	 * 
	 * @param con
	 *            �ͻ������ͷŵ�����
	 */
	public synchronized void freeConnection(Connection con) {
		// Find the connection in the pool
		int index = find(con, poolConnections);

		if (index != -1) {
			ConnectionObject co = (ConnectionObject) poolConnections.elementAt(index);

			// If the use count exceeds the max, remove it from
			// the pool.
			if ((connectUseCount > 0) && (co.getUseCount() >= connectUseCount)) {
				removeFromPool(index);
			} else {
				// Clear the use count and reset the time last used
				touch(co);
				co.setInUse(false);
			}
		}
		numCheckOut--;
		notifyAll();
	}

	/**
	 * ��ָ��index��ConnectionObject��������ӳ���ɾ��
	 * <p>
	 * Removes the ConnectionObject from the pool at the given index
	 * 
	 * @param index
	 *            Index into the pool vector
	 */

	private synchronized void removeFromPool(int index) {
		// Make sure the pool and index are valid
		if (poolConnections != null) {
			if (index < poolConnections.size()) {
				// Get the ConnectionObject and close the connection
				ConnectionObject co = (ConnectionObject) poolConnections.elementAt(index);
				close(co);
				// Remove the element from the pool
				poolConnections.removeElementAt(index);
			}
		}
		notifyAll();
	}

	/**
	 * ɾ����������
	 * 
	 * @param co
	 *            ��Ҫ�����ӳ���ɾ����ConnectionObject ����
	 */

	private synchronized void removeConnection(ConnectionObject co) {
		poolConnections.remove(co);
		try {
			co.getCon().close();
		} catch (SQLException ex) {
			logger.error("ϵͳ���ݿⳬʱ���ӹر�ʧ��");
		}
	}

	/**
	 * �����ӳػ��һ����������.��û�п��е������ҵ�ǰ������С��������� ������,�򴴽�������. ���ȡ�õ��ǲ����õ����ӣ����ų������ӳ������һ������
	 */
	public synchronized ConnectionObject getConnection() {
		ConnectionObject con = new ConnectionObject();
		con = null;
		if (poolConnections.size() > 0) {
			// ��ȡ�����е�һ����������
			try {
				int poolSize = poolConnections.size();
				for (int i = 0; i < poolSize; i++) {
					ConnectionObject co = (ConnectionObject) poolConnections.elementAt(i);
					if (co.isAvailable()) {
						con = co;
						boolean flg = true;

						Statement st = null;
						try {
							st = co.getCon().createStatement();
						} catch (Exception e) {
							removeFromPool(i);
							flg = false;
						} finally {
							st.close();
						}

						if (flg) {
							break;
						}
					}
				}
			} catch (SQLException e) {
				logger.error("�����ӳ�" + name + "ɾ��һ����Ч����ʧ��");
			}
		}
		if ((con == null) && (numCheckOut < maxConn)) {
			con = newConnection();
		}
		if (con != null) {
			con.setLastAccess(System.currentTimeMillis());
			con.setInUse(true);
			int count = con.getUseCount();
			con.setUseCount(count++);
			touch(con);
			numCheckOut++;
		}
		return con;
	}

	/**
	 * �����ӳػ�ȡ��������.����ָ���ͻ������ܹ��ȴ����ʱ�� �μ�ǰһ��getConnection()����.
	 * 
	 * @param timeout
	 *            �Ժ���Ƶĵȴ�ʱ������
	 */
	public synchronized ConnectionObject getConnection(long timeout) {
		long startTime = new Date().getTime();
		ConnectionObject con;
		while ((con = getConnection()) == null) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
			}
			if ((new Date().getTime() - startTime) >= timeout) {
				// wait()���ص�ԭ���ǳ�ʱ
				return null;
			}
		}
		return con;
	}

	/**
	 * �ر���������
	 */
	public synchronized void release() {
		Enumeration allConnections = poolConnections.elements();
		while (allConnections.hasMoreElements()) {
			ConnectionObject co = (ConnectionObject) allConnections.nextElement();
			try {
				co.isAvailable();
				co.getCon().close();
			} catch (SQLException e) {
				logger.error("�޷��ر����ӳ�" + name + "�е�����");
			}
		}
		poolConnections.removeAllElements();
	}

	/**
	 * �����µ�����ConnectionObject���󣬼������ӳ��У����ҷ���
	 */
	private ConnectionObject newConnection() {
		ConnectionObject co = new ConnectionObject();
		try {
			if (user == null) {
				co.setCon(DriverManager.getConnection(URL));
			} else {
				if ("oracle".equalsIgnoreCase(SystemConstant.DBType)) {
					Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				}
				co.setCon(DriverManager.getConnection(URL, user, password));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		co.setInUse(false);
		co.setUseCount(0);
		co.setLastAccess(0);
		co.setStartTime(System.currentTimeMillis());
		poolConnections.addElement(co);
		return co;
	}

	/**
	 * �����µ����ӳ�
	 * 
	 * @param name
	 *            ���ӳ�����
	 * @param URL
	 *            ���ݿ��JDBC URL
	 * @param user
	 *            ���ݿ��ʺ�,�� null
	 * @param password
	 *            ����,�� null
	 * @param minConn
	 *            �����ӳ����ٱ��ֵ���С������
	 * @param maxConn
	 *            �����ӳ������������������
	 * @param connectCheckOutTimeout
	 *            �����Ӵ����ӳ�ȡ������û�з��ص��������ʱ�䣬����ʱ�䣬��Ϊ����������Ч�������ӳ���ɾ��
	 * @param connectUseCount
	 *            ����������ʹ�õ�������
	 * @param connectUseTimeout
	 *            ��������������е��ʱ�䣬����ʱ�䣬�ر�
	 * @param logFile
	 *            ��־�ļ���·���������д��־�ļ������Բ���Ҫ�ò�����
	 */
	public DBConnectionPool(String name, String URL, String user, String password, int minConn, int maxConn, int useTimeout, int useCount, int checkOutTimeout, String logFile) {
		this.name = name;
		this.URL = URL;
		this.user = user;
		this.password = password;
		this.minConn = minConn;
		this.maxConn = maxConn;
		this.connectCheckOutTimeout = checkOutTimeout;
		this.connectUseCount = useCount;
		this.connectUseTimeout = useTimeout;
		this.numCheckOut = 0;
		this.numRequests = 0;
		this.numWaits = 0;
		poolConnections = new Vector();
		try {
			fillPool(this.minConn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		reaper = new ConnectionReaper(this);
		reaper.start();

	}

	/**
	 * ������ӳ��е����� Check all connections to make sure they haven't: 1) gone idle
	 * for too long<br>
	 * 2) been checked out by a thread for too long (cursor leak)<br>
	 */

	public synchronized void reapIdleConnections() {
		long now = System.currentTimeMillis();
		long idleTimeout = now - (connectUseTimeout * 1000);
		long checkoutTimeout = now - (connectCheckOutTimeout * 1000);
		for (Enumeration e = poolConnections.elements(); e.hasMoreElements();) {
			ConnectionObject co = (ConnectionObject) e.nextElement();
			if (co.isInUse() && (co.getLastAccess() < checkoutTimeout)) {
				removeConnection(co);
				notifyAll();
			} else {
				if (co.getLastAccess() < idleTimeout) {
					if (co.isInUse()) {
						removeConnection(co);
						notifyAll();
					}
				}
			}
		}
		// Now ensure that the pool is still at it's minimum size
		try {
			if (poolConnections != null) {
				if (poolConnections.size() < minConn) {
					fillPool(minConn);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �����ӳ���䵽ָ����С
	 * <p>
	 * Brings the pool to the given size
	 */
	private synchronized void fillPool(int size) throws Exception {
		// Loop while we need to create more connections
		int i = 0;
		while (poolConnections.size() < size) {
			ConnectionObject co = newConnection();
			// Do some sanity checking on the first connection in the pool
			if (poolConnections.size() == 1) {
				// Get the maximum number of simultaneous connections
				// as reported by the JDBC driver
				java.sql.DatabaseMetaData md = co.getCon().getMetaData();
				if ((md.getMaxConnections() != 0) && (maxConn > md.getMaxConnections()))
					maxConn = md.getMaxConnections();
			}
			// Give a warning if the size of the pool will exceed
			// the maximum number of connections allowed by the
			// JDBC driver
			i++;
			if ((maxConn > 0) && (size > maxConn)) {
				logger.warn("WARNING: Size of pool will exceed safe maximum of " + maxConn);
			}
			if (i == size)
				break;
		}
	}

	public Vector getPoolConnections() {
		return poolConnections;
	}
}
