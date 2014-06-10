package com.afunms.polling.impl;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.afunms.application.model.MQConfig;
import com.afunms.common.util.ReflactUtil;
import com.afunms.common.util.SystemConstant;
import com.afunms.mq.MqQueue;
import com.afunms.polling.PollingEngine;
import com.afunms.util.DataGate;
/**
 * <p>处理采集的mq数据信息</p>
 * @author HONGLI  Apr 8, 2011
 *
 */
public class ProcessMQData {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 保存mq的信息
	 * @param mqs   MQConfig的集合
	 * @param mqdatas  mq的数据集合
	 */
	public void saveMqData(List<MQConfig> mqs, Hashtable mqdatas) {
		if(mqs == null || mqs.size() == 0 || mqdatas == null || mqdatas.isEmpty()){
			return ;
		}
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement deletePstmt = null;
		String sql = "insert into nms_mq_temp(nodeid, entity,subentity,sindex, thevalue, collecttime) values(?, ?, ?, ? ,?,?)";
		String deleteSql = "delete from nms_mq_temp where nodeid = ?";
		String[] fields = new String[]{"qname","qtype","persistent","usage","qdepth","remoteQName","remoteQM","xmitQName"};
		try {
			conn = DataGate.getCon();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			deletePstmt = conn.prepareStatement(deleteSql);
			Calendar tempCal=Calendar.getInstance();
			Date cc = tempCal.getTime();
			String time = sdf.format(cc);
			Hashtable mqValues = new Hashtable();
			Hashtable q_remote_ParaValues = new Hashtable();
			Hashtable q_local_ParaValues = new Hashtable();
			for (int i = 0; i < mqs.size(); i++) {
				MQConfig mq = (MQConfig)mqs.get(i);
				String id = mq.getId()+"";
				if(mqdatas.containsKey(mq.getIpaddress()+":"+mq.getManagername())){
					deletePstmt.setString(1, id);
					deletePstmt.execute();
					Hashtable mqData = (Hashtable)mqdatas.get(mq.getIpaddress()+":"+mq.getManagername());
					if(mqData == null){
						continue;
					}
					if(mqData.containsKey("mqValue")){
						Vector mqValue = (Vector)mqData.get("mqValue");
						mqValues.put(id, mqValue);
					}
					if(mqData.containsKey("remote")){
						List remote = (ArrayList)mqData.get("remote");
						q_remote_ParaValues.put(id, remote);
					}
					if(mqData.containsKey("local")){
						List local = (ArrayList)mqData.get("local");
						q_local_ParaValues.put(id, local);
					}
				}
			}
			
			//批量入库
			//通道信息
			if(mqValues != null && !mqValues.isEmpty()){
				Iterator iterator = mqValues.keySet().iterator();
				while (iterator.hasNext()) {
					String id = (String) iterator.next();
					Vector mqValue = (Vector)mqValues.get(id);
					if(mqValue != null){
						for(int i=0; i<mqValue.size(); i++){
							Hashtable cAttr = (Hashtable)mqValue.get(i);
							Iterator tempIterator = cAttr.keySet().iterator();
							while (tempIterator.hasNext()) {
								String subentity = (String) tempIterator.next();
								String value = (String)cAttr.get(subentity);
								pstmt.setString(1, id);
								pstmt.setString(2, "mqValue");//entity
								pstmt.setString(3, subentity);//subentity
								pstmt.setString(4, i+"");//sindex
								pstmt.setString(5, value.trim());//value
								if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
									pstmt.setString(6, time);
								}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
									//java.util.Date  Dates = new java.util.Date(time);
									java.text.DateFormat date = java.text.DateFormat.getDateInstance();
									Date dat = date.parse(time);
								    java.sql.Timestamp time1 = new java.sql.Timestamp(dat.getYear(),dat.getMonth(),dat.getDate(),dat.getHours(),dat.getMinutes(),dat.getSeconds(),0);
									pstmt.setTimestamp(6, time1);
								}
								pstmt.addBatch();
							}
						}
					}
				}
			}
			//本地队列信息
			if(q_local_ParaValues != null && !q_local_ParaValues.isEmpty()){
				Iterator iterator = q_local_ParaValues.keySet().iterator();
				while (iterator.hasNext()) {
					String id = (String) iterator.next();
					List q_local_ParaValue = (ArrayList)q_local_ParaValues.get(id);
					if(q_local_ParaValue != null){
						for(int i=0; i<q_local_ParaValue.size(); i++){
							MqQueue qAttr = (MqQueue)q_local_ParaValue.get(i);
							if(qAttr != null) {
								//此处可以利用反射来插入对象...
								//getQname
								for(String field:fields){
									//getQname
									String value = "--";
									Object obj = ReflactUtil.invokeGet(qAttr, field);
									if(obj != null && obj instanceof String){
										value = (String)obj;
									}
									pstmt.setString(1, id);
									pstmt.setString(2, "local");//entity
									pstmt.setString(3, field);//subentity
									pstmt.setString(4, i+"");//sindex
									pstmt.setString(5, value.trim());//value
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										pstmt.setString(6, time);
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										//java.util.Date  Dates = new java.util.Date(time);
										java.text.DateFormat date = java.text.DateFormat.getDateInstance();
										Date dat = date.parse(time);
									    java.sql.Timestamp time1 = new java.sql.Timestamp(dat.getYear(),dat.getMonth(),dat.getDate(),dat.getHours(),dat.getMinutes(),dat.getSeconds(),0);
										pstmt.setTimestamp(6, time1);
									}
									pstmt.addBatch();
								}
							}
						}
					}
				}
			}
			//远程队列信息
			if(q_remote_ParaValues != null && !q_remote_ParaValues.isEmpty()){
				Iterator iterator = q_remote_ParaValues.keySet().iterator();
				while (iterator.hasNext()) {
					String id = (String) iterator.next();
					List q_remote_ParaValue = (ArrayList)q_remote_ParaValues.get(id);
					if(q_remote_ParaValue != null){
						for(int i=0; i<q_remote_ParaValue.size(); i++){
							MqQueue qAttr = (MqQueue)q_remote_ParaValue.get(i);
							if(qAttr != null) {
								//此处可以利用反射来插入对象...
								//getQname
								for(String field:fields){
									//getQname
									String value = "--";
									Object obj = ReflactUtil.invokeGet(qAttr, field);
									if(obj != null && obj instanceof String){
										value = (String)obj;
									}
									pstmt.setString(1, id);
									pstmt.setString(2, "remote");//entity
									pstmt.setString(3, field);//subentity
									pstmt.setString(4, i+"");//sindex
									pstmt.setString(5, value.trim());//value
									if("mysql".equalsIgnoreCase(SystemConstant.DBType)){
										pstmt.setString(6, time);
									}else if("oracle".equalsIgnoreCase(SystemConstant.DBType)){
										//java.util.Date  Dates = new java.util.Date(time);
										java.text.DateFormat date = java.text.DateFormat.getDateInstance();
										Date dat = date.parse(time);
									    java.sql.Timestamp time1 = new java.sql.Timestamp(dat.getYear(),dat.getMonth(),dat.getDate(),dat.getHours(),dat.getMinutes(),dat.getSeconds(),0);
										pstmt.setTimestamp(6, time1);
									}
									pstmt.addBatch();
								}
							}
						}
					}
				}
			}
			pstmt.executeBatch();//批量插入
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally{
			if(deletePstmt != null){
				try {
					deletePstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
