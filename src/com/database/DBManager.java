package com.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.apache.log4j.Logger;
import java.util.Hashtable;

/**
 * 
 * @���� ʵ���������ݿ�Ĺ��ܣ����ṩ���ַ���������List��HashMap
 */
@SuppressWarnings({"static-access","unused","unchecked"})
public class DBManager {
	private static final Logger logger = Logger.getLogger(DBManager.class);
	private Connection conn = null; 
	private Statement stmt = null;
	private ResultSet rs = null; 

	private DBConnectionManager mg = new DBConnectionManager(); 

	/**
	 * ����Ĭ��ϵͳ���ݿ��ǿ����������ӵģ����Բ��׳�����
	 */
	public DBManager() {
		logger.info("����ϵͳ���ݿ�connection");
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @�������� ��ʼ�����ӳ����Ӻ�Statement
	 */
	
	private void init() {
		try {
			conn = mg.getConnection();
			stmt = conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @�������� ִ�в�ѯ������ResultSet
	 */
	public ResultSet executeQuery(String sql) {
		try {
			if (conn == null) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			rs = stmt.executeQuery(sql);
		} catch (SQLException se) {
			se.printStackTrace();
		}

		return rs;
	}

	/**
	 * @�������� ���º�Ĭ���ύ
	 */
	public int executeUpdate(String sql) {
		int iresult = -1;
		iresult = executeUpdate(sql, true);
		return iresult;
	}

	/**
	 * @�������� ���£�����bCommit�������Ƿ�ֱ���ύ
	 */
	private int executeUpdate(String sql, boolean bCommit) {
		int iresult = -1;
		try {
			if (conn == null) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			iresult = stmt.executeUpdate(sql);
			if (bCommit) {
				conn.commit();
			}
			return iresult;
		} catch (SQLException se) {
			se.printStackTrace();
			try {
				conn.rollback();
				return iresult;
			} catch (Exception sqle) {
				sqle.printStackTrace();
				return iresult;
			}

		}

	}

	/**
	 * @�������� �ύ
	 */
	@SuppressWarnings("unused")
	private void commit() {
		try {
			conn.commit();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * @�������� �ع�
	 */
	private void rollback() {
		try {
			conn.rollback();
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * @�������� ����������
	 */
	public void addBatch(String sql) {
		try {

			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			stmt.addBatch(sql);
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * @�������� ִ��������
	 */
	public int[] executeBatch() {
		int[] intlist = null;
		try {
			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
				conn.setAutoCommit(false);
			}
			conn.setAutoCommit(false);
			intlist = stmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			return intlist;
		} catch (BatchUpdateException bse) {
			bse.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			try {
				stmt.clearBatch();
				conn.setAutoCommit(true);
			} catch (SQLException xe) {
				xe.printStackTrace();
			}
		}
		return intlist;
	}

	/**
	 * @�������� �ر�����
	 */
	public void close() {
		try {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}

	/**
	 * @�������� ��ȡΨһid
	 */
	public synchronized int getNextID() {
		String sql = "SELECT SYS_BACK_SQ.NEXTVAL FROM DUAL";
		int max = -1;
		try {
			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				max = rs.getInt(1);
			}
		} catch (Exception se) {
			se.printStackTrace();
			return max;
		}
		return max;
	}

	/**
	 * @�������� ִ�е�����ѯ��sql�����뱣ֻ֤����һ����¼�� ���ڸ���ʱ��ȡ������¼��Ϣ
	 */
	public HashMap executeQueryHashMap(String sql) throws SQLException {
		HashMap hm = new HashMap();
		try {
			rs = executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCols = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= numCols; i++) {
					String key = rsmd.getColumnName(i);
					String value = rs.getString(i);
					if (value == null) {
						value = "";
					}
					hm.put(key, value);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;

		}
		return hm;
	}

	/**
	 * @�������� ִ�����ݿ��ѯ��䷵���������͵�����,������ÿһ��ֵ�ǹ�ϣ�� ÿ����ϣ�����һ����¼
	 */
	public Hashtable executeQuerykeyoneListHashMap(String sql, String indexkey) throws SQLException {
		ResultSetMetaData rsmd = null;
		Hashtable list = new Hashtable();
		int columnCount = 0;
		try {
			rs = executeQuery(sql);
			if (rs == null) {
				return null;
			}
			rsmd = rs.getMetaData();
			if (rsmd == null) {
				return null;
			}
			columnCount = rsmd.getColumnCount(); // �õ��ֶ�����
			if (columnCount == 0) {
				return null;
			}

			String[] keys = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				keys[i - 1] = rsmd.getColumnName(i); // ����ֶ���
			}
			while (rs.next()) {
				Hashtable hm = new Hashtable();
				hm.clear();
				String key = null;
				for (int i = 1; i <= columnCount; i++) {
					String result = rs.getString(i);
					if ((result == null) || (result.length() == 0)) {
						result = "";
					}
					if (keys[i - 1].equals(indexkey)) {
						key = result;
					}

					hm.put(keys[i - 1], result); // ��ÿ����¼���浽һ����ϣ���У�keyΪ�ֶ�����resultΪֵ
				}
				list.put(key, hm); // �����ݼ���ÿһ�в�������
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return list; // ����SQL���ԵĲ�ѯ�������
	}

	/**
	 * 
	 * ͨ�� onekye��twokey��ϳ�һ��Ψһ��key���������ݿ�Ĳ�ѯ��� �ŵ�hashtable ��;onekey-towkey Ϊ����
	 * 
	 * @�������� ִ�����ݿ��ѯ��䷵���������͵�����,������ÿһ��ֵ�ǹ�ϣ�� ÿ����ϣ�����һ����¼
	 */
	public Hashtable executeQuerykeytwoListHashMap(String sql, String onekey, String twokey) throws SQLException {
		ResultSetMetaData rsmd = null;
		Hashtable list = new Hashtable();
		int columnCount = 0;
		try {
			rs = executeQuery(sql);
			if (rs == null) {
				return null;
			}
			rsmd = rs.getMetaData();
			if (rsmd == null) {
				return null;
			}
			columnCount = rsmd.getColumnCount(); // �õ��ֶ�����
			if (columnCount == 0) {
				return null;
			}

			// �����ĵ�һ��ֵ����������
			String[] keys = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				keys[i - 1] = rsmd.getColumnName(i); // ����ֶ���
			}
			while (rs.next()) {
				Hashtable hm = new Hashtable();
				hm.clear();
				String key1 = null;
				String key2 = null;
				for (int i = 1; i <= columnCount; i++) {
					String result = rs.getString(i);
					if ((result == null) || (result.length() == 0)) {
						result = "";
					}
					if (keys[i - 1].equals(onekey)) {
						key1 = result;
					}
					if (keys[i - 1].equals(twokey)) {
						key2 = result;
					}
					hm.put(keys[i - 1], result); // ��ÿ����¼���浽һ����ϣ���У�keyΪ�ֶ�����resultΪֵ
				}
				list.put(key1 + "-" + key2, hm); // �����ݼ���ÿһ�в�������
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return list; // ����SQL���ԵĲ�ѯ�������
	}

	/**
	 * @�������� ִ�����ݿ��ѯ��䷵���������͵�����,������ÿһ��ֵ�ǹ�ϣ�� ÿ����ϣ�����һ����¼
	 */
	public List executeQueryListHashMap(String sql) throws SQLException {
		ResultSetMetaData rsmd = null;
		List list = new ArrayList();
		int columnCount = 0;
		try {
			rs = executeQuery(sql);
			if (rs == null) {
				return null;
			}
			rsmd = rs.getMetaData();
			if (rsmd == null) {
				return null;
			}
			columnCount = rsmd.getColumnCount(); // �õ��ֶ�����
			if (columnCount == 0) {
				return null;
			}

			// �����ĵ�һ��ֵ����������
			String[] keys = new String[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				keys[i - 1] = rsmd.getColumnName(i); // ����ֶ���
			}
			while (rs.next()) {
				HashMap hm = new HashMap();
				hm.clear();
				for (int i = 1; i <= columnCount; i++) {
					String result = rs.getString(i);
					if ((result == null) || (result.length() == 0)) {
						result = "";
					}
					hm.put(keys[i - 1], result); // ��ÿ����¼���浽һ����ϣ���У�keyΪ�ֶ�����resultΪֵ
				}
				list.add(hm); // �����ݼ���ÿһ�в�������
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return list; // ����SQL���ԵĲ�ѯ�������
	}

	/**
	 * @Description: TODO����ִ�и�����sql���(insert delete update)
	 */
	public boolean excuteBatchSql(List<String> list) {
		if (list == null) {
			return false;
		}
		try {
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					addBatch(list.get(i));
				}
				int[] iResult = null;
				iResult = executeBatch();
				if (iResult != null) {
					String str = iResult.toString();
					if (str.indexOf(-1) >= 0) {
						return false;
					}
				} else {
					return false;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @Description: TODO����ִ�и�����sql���(insert delete update)
	 */
	public boolean excuteBatchSql(Queue<String> list) {
		if (list == null) {
			return false;
		}
		try {
			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			conn.setAutoCommit(false);
			if (list.size() > 0) {
				String sql = "";
				while ((sql = list.poll()) != null) {
					stmt.addBatch(sql);
				}
				stmt.executeBatch();
				conn.commit();
				conn.close();
				stmt.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * @Description: TODO(�����ô���userid����)
	 */
	public synchronized int getNextUserID() {
		String sql = "SELECT SYSUSER_SQ.NEXTVAL FROM DUAL";
		int max = -1;
		try {
			if (null == conn) {
				conn = mg.getConnection();
				stmt = conn.createStatement();
			}
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				max = rs.getInt(1);
			}
		} catch (Exception se) {
			se.printStackTrace();
			return max;
		}
		return max;
	}
}
