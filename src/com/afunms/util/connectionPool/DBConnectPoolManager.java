package com.afunms.util.connectionPool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Explanation: java.sql.Connection getConnection(String name[,time:long])
 * variable name is the name of connectionpool,which is defined in the
 * configurable file. variable time is a optional parameters, referes to the
 * maximum number of millisecend a client can wait for. <p/>
 * freeConnection(String name,Connection con) variable name is the name of
 * connectionpool variable con is the current connection which would be return
 * to connectionpool.
 */

@SuppressWarnings("unchecked")
public class DBConnectPoolManager {
	private static Logger logger = Logger.getLogger(DBConnectPoolManager.class);

	private static DBConnectPoolManager instance; // Ψһʵ��
	private Hashtable pools = new Hashtable();
	private Vector drivers = new Vector();

	/**
	 * ��������˽���Է�ֹ�������󴴽�����ʵ��
	 */
	private DBConnectPoolManager() {
		init();
	}

	/**
	 * �����Ӷ��󷵻ظ�������ָ�������ӳ�
	 * 
	 * @param name
	 *            �������ļ��ж�������ӳ�����
	 * @param con
	 *            ���Ӷ���
	 */
	public void freeConnection(Connection con) {
		DBConnectionPool pool = (DBConnectionPool) pools.get("name");
		if (pool != null) {
			pool.freeConnection(con);
		}
	}

	/**
	 * ���һ�����õ�(���е�)����.���û�п�������,������������С����������� ����,�򴴽�������������
	 * 
	 * @param name
	 *            �������ļ��ж�������ӳ�����
	 * @return Connection �������ӻ�null
	 */
	public Connection getConnection() {
		DBConnectionPool pool = (DBConnectionPool) pools.get("name");
		if (pool != null) {
			return pool.getConnection().getCon();
		}
		return null;
	}

	/**
	 * ���һ����������.��û�п�������,������������С���������������, �򴴽�������������.����,��ָ����ʱ���ڵȴ������߳��ͷ�����.
	 * 
	 * @param name
	 *            ���ӳ�����
	 * @param time
	 *            �Ժ���Ƶĵȴ�ʱ��
	 * @return Connection �������ӻ�null
	 */
	public Connection getConnection(long time) {
		DBConnectionPool pool = (DBConnectionPool) pools.get("name");
		if (pool != null) {
			return pool.getConnection(time).getCon();
		}
		return null;
	}

	/**
	 * ����Ψһʵ��.����ǵ�һ�ε��ô˷���,�򴴽�ʵ��
	 * 
	 * @return DBConnectionManager Ψһʵ��
	 */
	static synchronized public DBConnectPoolManager getInstance() {
		if (instance == null) {
			logger.info("DBConnectpoolManager not init yet , now getInstance");
			instance = new DBConnectPoolManager();
		}
		return instance;
	}

	/**
	 * �ر���������,�������������ע��
	 */
	public synchronized void release() {
		// �ȴ�ֱ�����һ���ͻ��������

		Enumeration allPools = pools.elements();
		while (allPools.hasMoreElements()) {
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}
		Enumeration allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
			Driver driver = (Driver) allDrivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				logger.info("����JDBC�������� " + driver.getClass().getName() + "��ע��");
			} catch (SQLException e) {
				logger.error("�޷���������JDBC���������ע��: " + driver.getClass().getName());
			}
		}
	}

	/**
	 * ����ָ�����Դ������ӳ�ʵ��.
	 * 
	 * @param props
	 *            ���ӳ�����
	 */
	private void createPools() {
		String url = DBProperties.getUrl();
		if (url == null) {
			logger.info("û��Ϊ���ݿ����ӳ�ָ��URL");
			return;
		}
		String user = DBProperties.getUser();
		String password = DBProperties.getPassword();
		int max = DBProperties.getMaxconn();
		int min = DBProperties.getMinconn();
		int connectCheckOutTimeout = DBProperties.getConnectCheckOutTimeout();
		int connectUseTimeout = DBProperties.getConnectUseTimeout();
		int connectUseCount = DBProperties.getConnectUseCount();
		DBConnectionPool pool = new DBConnectionPool("name", url, user, password, min, max, connectUseTimeout, connectUseCount, connectCheckOutTimeout, "");
		pools.put("name", pool);// ���õ�hash����
	}

	/**
	 * ��ʼ�����ӳأ���ȡ�����ļ���������Ӧ�����ӳ�
	 * 
	 * @roseuid 39FB9F19011F
	 */
	private void init() {
		loadDrivers();
		createPools();
	}

	/**
	 * װ�غ�ע������JDBC��������
	 * 
	 * @param props
	 *            ����
	 */
	private void loadDrivers() {
		String driverClasses = DBProperties.getDrivers();
		StringTokenizer st = new StringTokenizer(driverClasses);
		while (st.hasMoreElements()) {
			String driverClassName = st.nextToken().trim();
			try {
				Driver driver = (Driver) Class.forName(driverClassName).newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
			} catch (Exception e) {
				logger.error("�޷�ע��JDBC��������: " + driverClassName + ", ����: " + e);
			}
		}
	}

	public Vector getPoolConnections() {
		DBConnectionPool pool = (DBConnectionPool) pools.get("name");
		return pool.getPoolConnections();
	}
}
