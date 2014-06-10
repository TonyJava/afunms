package com.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.afunms.common.base.BaseVo;
import com.afunms.common.util.JdbcUtil;
import com.afunms.event.model.SendSmsConfig;
import com.database.config.SystemConfig;
  
public class SqlServerDBManager { 
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static SqlServerDBManager sqlmanager = null;
	
	public static SqlServerDBManager getSqlServerManager(){
		if(sqlmanager == null){
			sqlmanager = new SqlServerDBManager();
		}
		return sqlmanager;
	}
	
	public boolean save(BaseVo baseVo){
		boolean flag = true;
		SendSmsConfig vo = (SendSmsConfig) baseVo;
		Date d = new Date();
		String time = sdf.format(d);
		StringBuffer sql = new StringBuffer();
		sql.append("insert into sms_server(name,mobilenum,eventlist,eventtime)values(");
		sql.append("'");
		sql.append(vo.getName());
		sql.append("','");
		sql.append(vo.getMobilenum());
		sql.append("','");
		sql.append(vo.getEventlist());
		
		sql.append("','");
		sql.append(time);
		sql.append("'");
		sql.append(")");
		
		
		String url = "jdbc:jtds:sqlserver://192.168.110.110:1433;DatabaseName=lps;charset=GBK;SelectMethod=CURSOR";//sa身份连接  
		url = SystemConfig.getConfigInfomation("SqlserverConfigResources","DATABASE_URL");
		String user = SystemConfig.getConfigInfomation("SqlserverConfigResources", "DATABASE_USER");
		String pwd = SystemConfig.getConfigInfomation("SqlserverConfigResources", "DATABASE_PASSWORD");
		JdbcUtil jdbcutil = new JdbcUtil(url,user,pwd);
		jdbcutil.jdbc();
		try {
			jdbcutil.stmt.executeUpdate(sql.toString());
		} catch (SQLException e) {
			e.printStackTrace();
			flag = false;
		} finally{
			try {
				jdbcutil.closeStmt();
				jdbcutil.closeConn();
			} catch (Exception ep) {
			}
		}
		return flag;
	}
	
    public static void main(String args[]) {  
        String connectionUrl = "jdbc:sqlserver://localhost:1433;"  
                + "databaseName=AdventureWorks;integratedSecurity=true;";  
  
        String url = "jdbc:jtds:sqlserver://192.168.110.110:1433;DatabaseName=lps;charset=UTF-8;SelectMethod=CURSOR";//sa身份连接  
  
        String url2 = "jdbc:sqlserver://127.0.0.1:1368;databaseName=mydb;integratedSecurity=true;";//windows集成模式连接  
        ResultSet rs = null;  
        
        JdbcUtil util = new JdbcUtil(url, "sa", "root");
        util.jdbc();
        try {  
            System.out.println("begin.");  
            System.out.println("end.");  
  
            String SQL = "SELECT * FROM sms_server";  
            SQL = "select * from sms_server where issend='0' and convert(varchar(12),eventtime,111) = convert(varchar(12),getdate(),111)";
            rs = util.stmt.executeQuery(SQL);  
            while (rs.next()) {  
                System.out.println(rs.getString("name") + " " + rs.getString("mobilenum") + " " + rs.getString("eventlist"));  
            }  
        }  
        catch (Exception e) {  
            e.printStackTrace();  
        }finally {  
            if (rs != null)  
                try {  
                    rs.close();  
                } catch (Exception e) {  
                }  
        }  
    }  
}  