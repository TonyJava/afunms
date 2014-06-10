package com.afunms.application.util;

import java.io.FileInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.afunms.common.util.SysUtil;
import com.mysql.jdbc.Driver;

/**
 * @author HONGLI E-mail: ty564457881@163.com
 * @version 创建时间：Jul 27, 2011 8:35:13 PM 类说明
 */
public class ClearDBUtil {
	private static ClearDBUtil clearDBUtil = new ClearDBUtil();

	public synchronized static ClearDBUtil getInstance() {
		if (clearDBUtil == null) {
			clearDBUtil = new ClearDBUtil();
		}
		return clearDBUtil;
	}

	public static void println(Object obj) {
		System.out.println(obj);
	}

	/**
	 * 执行单条截断语句
	 * 
	 * @param stmt
	 * @param sql
	 * @return
	 */
	public boolean execute(Statement stmt, String sql) {
		boolean b = true;
		try {
			stmt.execute(sql);
		} catch (SQLException e) {
			b = false;
			println(e.getMessage());
		}
		return b;
	}

	/**
	 * 截断数据库临时表
	 * 
	 * @throws Exception
	 */
	public void clearDB() throws Exception {
		// 获取数据库连接
		Driver driver = (Driver) Class.forName("com.mysql.jdbc.Driver").newInstance();
		Properties properties = new Properties();
		URL u = Thread.currentThread().getContextClassLoader().getResource("SystemConfigResources.properties");
		FileInputStream fis = new FileInputStream(u.getPath());
		properties.load(fis);
		String url = properties.getProperty("DATABASE_URL");
		properties.setProperty("user", properties.getProperty("DATABASE_USER"));
		properties.setProperty("password", properties.getProperty("DATABASE_PASSWORD"));
		Connection conn = driver.connect(url, properties);
		Statement stmt = conn.createStatement();
		Statement truncStmt = conn.createStatement();// 截断表
		ResultSet rs = stmt.executeQuery("select * from topo_host_node");
		try {
			while (rs.next()) {
				String ipaddress = rs.getString("ip_address");
				execute(truncStmt, "truncate table allutilhdx" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table autilhdxd" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table autilhdxh" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table buffer" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table bufferday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table bufferhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table cpu" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table cpuhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table cpuday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table cpudtl" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table cpudtlday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table cpudtlhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table dcarperd" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table dcarperh" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table discardsperc" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table disk" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table diskday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table diskhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table diskincre" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table diskincd" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table diskinch" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table errorsperc" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table errpercd" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table errperch" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table fan" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table fanday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table fanhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table flash" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table flashday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table flashhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table hdxpercday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table hdxperchour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table inpacks" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table ipackd" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table ipacksh" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table memory" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table memoryday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table memoryhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table outpacks" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table opacksd" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table opackh" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table packs" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table packshour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table packsday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table ping" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table pinghour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table pingday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table portstatus" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table power" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table powerday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table powerhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table pro" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table proday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table software" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table sqlping" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table sqlpingday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table sqlpinghour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table temper" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table temperd" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table temperh" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table utilhdx" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table utilhdxhour" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table utilhdxday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table utilhdxperc" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table vol" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table volday" + SysUtil.doip(ipaddress));
				execute(truncStmt, "truncate table volhour" + SysUtil.doip(ipaddress));
			}
			execute(truncStmt, "truncate table system_eventlist");
			execute(truncStmt, "truncate table nms_alarminfo");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				stmt.close();
			}
			if (truncStmt != null) {
				truncStmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			ClearDBUtil.getInstance().clearDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
