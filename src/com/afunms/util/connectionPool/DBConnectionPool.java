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
	private int numRequests;// 需要的连接数
	private int numWaits;// 等待中的客户程序数
	private int numCheckOut;// 已经取出的连接数
	private String driverClassName;
	private ConnectionReaper reaper;

	/**
	 * 找到所给的连接对象connection在向量(连接池)中的index
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
	 *            需要设置最后访问时间的ConnectionObject对象
	 */
	private void touch(ConnectionObject co) {
		if (co != null) {
			co.setLastAccess(System.currentTimeMillis());
		}
	}

	/**
	 * 关闭ConnectionObject中的连接
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
	 * 将不再使用的连接返回给连接池
	 * 
	 * @param con
	 *            客户程序释放的连接
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
	 * 将指定index的ConnectionObject对象从连接池中删除
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
	 * 删除无用连接
	 * 
	 * @param co
	 *            需要从连接池中删除的ConnectionObject 对象
	 */

	private synchronized void removeConnection(ConnectionObject co) {
		poolConnections.remove(co);
		try {
			co.getCon().close();
		} catch (SQLException ex) {
			logger.error("系统数据库超时连接关闭失败");
		}
	}

	/**
	 * 从连接池获得一个可用连接.如没有空闲的连接且当前连接数小于最大连接 数限制,则创建新连接. 如果取得的是不可用的连接，接着尝试连接池里的下一个连接
	 */
	public synchronized ConnectionObject getConnection() {
		ConnectionObject con = new ConnectionObject();
		con = null;
		if (poolConnections.size() > 0) {
			// 获取向量中第一个可用连接
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
				logger.error("从连接池" + name + "删除一个无效连接失败");
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
	 * 从连接池获取可用连接.可以指定客户程序能够等待的最长时间 参见前一个getConnection()方法.
	 * 
	 * @param timeout
	 *            以毫秒计的等待时间限制
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
				// wait()返回的原因是超时
				return null;
			}
		}
		return con;
	}

	/**
	 * 关闭所有连接
	 */
	public synchronized void release() {
		Enumeration allConnections = poolConnections.elements();
		while (allConnections.hasMoreElements()) {
			ConnectionObject co = (ConnectionObject) allConnections.nextElement();
			try {
				co.isAvailable();
				co.getCon().close();
			} catch (SQLException e) {
				logger.error("无法关闭连接池" + name + "中的连接");
			}
		}
		poolConnections.removeAllElements();
	}

	/**
	 * 创建新的连接ConnectionObject对象，加入连接池中，并且返回
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
	 * 创建新的连接池
	 * 
	 * @param name
	 *            连接池名字
	 * @param URL
	 *            数据库的JDBC URL
	 * @param user
	 *            数据库帐号,或 null
	 * @param password
	 *            密码,或 null
	 * @param minConn
	 *            此连接池至少保持的最小连接数
	 * @param maxConn
	 *            此连接池允许建立的最大连接数
	 * @param connectCheckOutTimeout
	 *            此连接从连接池取出而且没有返回的最大允许时间，超过时间，认为此连接已无效，从连接池中删除
	 * @param connectUseCount
	 *            此连接允许使用的最大次数
	 * @param connectUseTimeout
	 *            此连接允许空运行的最长时间，超过时间，关闭
	 * @param logFile
	 *            日志文件的路径（如果不写日志文件，可以不需要该参数）
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
	 * 检查连接池中的连接 Check all connections to make sure they haven't: 1) gone idle
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
	 * 将连接池填充到指定大小
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
