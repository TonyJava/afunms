package com.afunms.polling.task;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.afunms.application.dao.Apachemonitor_historyDao;
import com.afunms.application.dao.Apachemonitor_realtimeDao;
import com.afunms.application.jbossmonitor.HttpClientJBoss;
import com.afunms.application.manage.ApacheManager;
import com.afunms.application.model.ApacheConfig;
import com.afunms.application.model.Apachemonitor_history;
import com.afunms.application.model.Apachemonitor_realtime;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.EventList;
import com.afunms.event.model.Smscontent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.impl.ProcessApacheData;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.PingCollectEntity;

public class ApacheTask extends MonitorTask{
	private Hashtable sendeddata = ShareData.getSendeddata();
	private ApacheManager apacheconf = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	
	public ApacheTask(){
		super();
	}
		
	public void run(){
		try{
			List<ApacheConfig> apacheConfigList = new ArrayList<ApacheConfig>();
			ApacheManager apacheManager = new ApacheManager();
			apacheConfigList = apacheManager.getApacheConfigListByMonflag(1);
			Calendar date=Calendar.getInstance();
			for (int i = 0; i < apacheConfigList.size(); i++) {
				Hashtable hst = null;
				SmscontentDao eventdao=null;
				Apachemonitor_realtimeDao apachemonitor_realtimeDao = null;
				Apachemonitor_historyDao apachemonitor_historyDao = null ;
				try{
					apachemonitor_historyDao = new Apachemonitor_historyDao();
					apachemonitor_realtimeDao = new Apachemonitor_realtimeDao();
					Hashtable realHash=new Hashtable();
					realHash = apachemonitor_realtimeDao.getAllReal();//
					
					Integer iscanconnected = new Integer(0+"");
					Apachemonitor_realtime apachemonitor_realtimeold = new Apachemonitor_realtime();
					String reason= "";
					ApacheConfig apacheConfig = apacheConfigList.get(i);
					hst = apacheManager.collectapachedata(apacheConfig);
	        		if(hst == null) {
	        			hst = new Hashtable();
	        		
	        		}
				
					Integer apache_id = apacheConfig.getId();
					boolean old=false;
					Integer smssign=new Integer(0);
					if(realHash !=null&& realHash.get(apache_id)!=null){
						old=true;
						apachemonitor_realtimeold=(Apachemonitor_realtime)realHash.get(apache_id);
						smssign=apachemonitor_realtimeold.getSms_sign();
					}
					HttpClientJBoss apache = new HttpClientJBoss();
					boolean isSucess=false;
				   	String response=apache.getGetResponseWithHttpClient("http://"+apacheConfig.getIpaddress()+":"+apacheConfig.getPort(), "GBK");
				   	
				   	if(response.toLowerCase().contains("it works!")){
				   		reason="Apache服务有效";
				   		isSucess=true;
				   	}
				   	String src=apache.getGetResponseWithHttpClient("http://"+apacheConfig.getIpaddress()+":"+apacheConfig.getPort()+"/server-status", "GBK");
				   	String time = sdf.format(date.getTime());
				   	try{					
						if (isSucess||src.contains("Version") ){
							reason="Apache服务有效";
							iscanconnected=new Integer(1);						
						}else{
							//需要增加邮件服务所在的服务器是否能连通
							reason="Apache服务无效";
							Host host = (Host)PollingEngine.getInstance().getNodeByIP(apacheConfig.getIpaddress());
							Vector ipPingData = (Vector)ShareData.getPingdata().get(apacheConfig.getIpaddress());
						      eventdao = new SmscontentDao();
			            	  String eventdesc = "Apache服务(IP:"+apacheConfig.getIpaddress()+")"+"的Apache服务停止";
			            	 
							if(ipPingData != null){
								PingCollectEntity pingdata = (PingCollectEntity)ipPingData.get(0);
								Calendar tempCal = (Calendar)pingdata.getCollecttime();							
								Date cc = tempCal.getTime();
								time = sdf.format(cc);		
								String lastTime = time;
								String pingvalue = pingdata.getThevalue();
								if(pingvalue == null || pingvalue.trim().length()==0)pingvalue="0";
								if(pingvalue.equals("0")){
									eventdesc = "Apache(IP:"+apacheConfig.getIpaddress()+")"+"服务器无法连通";
								}
								 
							}
							 //eventdao.createEventWithReasion("poll",apacheConfig.getId()+"",apacheConfig.getAlias()+"("+apacheConfig.getIpaddress()+")",eventdesc,3,"apache","ping","所在的服务器连接不上");
						}
						
					}catch(Exception ex){
						SysLogger.error("ApacheTask error",ex);
						
					}
					
					//保存进历史数据
					
//					Apachemonitor_history apacheMonitor_history = new Apachemonitor_history();
//					apacheMonitor_history.setApache_id(apacheConfig.getId());
//					apacheMonitor_history.setIs_canconnected(iscanconnected);
//					apacheMonitor_history.setMon_time(Calendar.getInstance());
//					apacheMonitor_history.setReason(reason);
//					apachemonitor_historyDao.save(apacheMonitor_history);	
//					
//					//UrlDataCollector udc = new UrlDataCollector();
//					Apachemonitor_realtime apachemonitor_realtime = new Apachemonitor_realtime();
//					apachemonitor_realtime.setApache_id(apacheConfig.getId());
//					apachemonitor_realtime.setIs_canconnected(iscanconnected);
//					apachemonitor_realtime.setReason(reason);
//					apachemonitor_realtime.setMon_time(Calendar.getInstance());
//					//实时数据
//					//ur.setUrl_id(url_id);
//					if(old==true){
//						apachemonitor_realtime.setSms_sign(1);
//					}
//					else{
//						apachemonitor_realtime.setSms_sign(smssign);
//					}
//					
//					
//					//保存realtime
//					if (old == true) {
//						apachemonitor_realtime.setId(apachemonitor_realtimeold.getId());
//						apachemonitor_realtimeDao.update(apachemonitor_realtime);
//						
//					}else{
//						apachemonitor_realtimeDao.save(apachemonitor_realtime);
//					}				
					
					
					if(sendeddata.containsKey("apache"+":"+apacheConfig.getIpaddress())){
						sendeddata.remove("apache"+":"+apacheConfig.getIpaddress());
					}
					try{
					    com.afunms.common.util.ShareData.setApachedata("apache:"+apacheConfig.getIpaddress(),hst);	
					}catch(Exception ex){
						ex.printStackTrace();
					} 
					hst = null;
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					apachemonitor_historyDao.close();
					apachemonitor_realtimeDao.close();
					
				}	
			}
			//将采集到的Apache Http Server 信息入库
//			ProcessApacheData processApacheData = new ProcessApacheData();
//			processApacheData.saveApacheData(apacheConfigList,ShareData.getApachedata());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}		
	}
	
	public void createEvent(ApacheConfig apacheConfig, String reason){
		Calendar date=Calendar.getInstance();
		String time = sdf.format(date.getTime());
		EventList event = new EventList();
		event.setEventtype("apacheserver");
		event.setEventlocation(apacheConfig.getIpaddress());
		event.setBusinessid(apacheConfig.getNetid());
		event.setManagesign(new Integer(0));
		event.setReportman("monitorpc");
		event.setRecordtime(new GregorianCalendar());		
		String errorcontent=time+" "+apacheConfig.getUsername()+"(IP:"+apacheConfig.getIpaddress()+")：Apache服务故障";		 		
		event.setContent(errorcontent);
		Integer level = new Integer(2);
		event.setLevel1(level);
		//reason="FTP服务无效";
		//EventListDao eventListDao = null ;
		try{
			//eventListDao = new EventListDao();
			//eventListDao.save(event);
		}catch(Exception e){
			
		}finally{
			//eventListDao.close();
		}
		
		Vector eventtmpV = new Vector();
		eventtmpV.add(event);
		createSMS("apacheserver",apacheConfig.getId()+"",errorcontent,apacheConfig.getIpaddress());
	}
	

	 public void createSMS(String apacheserver,String apache_id,String errmsg,String ftpstr){
	 	//建立短信		 	
	 	//从内存里获得当前这个IP的PING的值
	 	Calendar date=Calendar.getInstance();
	 	AlarmInfoDao alarmdao=null;
	 	try{
 			if (!sendeddata.containsKey(apacheserver+":"+apache_id)){
 				//若不在，则建立短信，并且添加到发送列表里
	 			Smscontent smscontent = new Smscontent();
	 			smscontent.setMessage(errmsg);
	 			smscontent.setObjid(apache_id);
	 			Calendar _tempCal = Calendar.getInstance();				
				Date _cc = _tempCal.getTime();
				String _time = sdf.format(_cc);
	 			smscontent.setRecordtime(_time);
	 			smscontent.setSubtype("apache");
	 			smscontent.setLevel(3+"");
	 			//发送短信
	 			SmscontentDao smsmanager=new SmscontentDao();
	 			smsmanager.sendURLSmscontent(smscontent);
				sendeddata.put(apacheserver+":"+apache_id,date);		 					 				
 			}else{
 				//若在，则从已发送短信列表里判断是否已经发送当天的短信
 				Calendar formerdate =(Calendar)sendeddata.get(apacheserver+":"+apache_id);		 				
	 			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	 			Date last = null;
	 			Date current = null;
	 			Calendar sendcalen = formerdate;
	 			Date cc = sendcalen.getTime();
	 			String tempsenddate = formatter.format(cc);
	 			
	 			Calendar currentcalen = date;
	 			cc = currentcalen.getTime();
	 			last = formatter.parse(tempsenddate);
	 			String currentsenddate = formatter.format(cc);
	 			current = formatter.parse(currentsenddate);
	 			
	 			long subvalue = current.getTime()-last.getTime();			 			
	 			if (subvalue/(1000*60*60*24)>=1){
	 				//超过一天，则再发信息
		 			Smscontent smscontent = new Smscontent();
		 			//String time = sdf.format(date.getTime());
		 			smscontent.setMessage(errmsg);
		 			smscontent.setObjid(apache_id);
		 			smscontent.setLevel(3+"");
		 			//发送短信
		 			SmscontentDao smsmanager=new SmscontentDao();
		 			smsmanager.sendURLSmscontent(smscontent);
		 			Calendar _tempCal = Calendar.getInstance();				
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
		 			smscontent.setRecordtime(_time);
		 			smscontent.setSubtype("apache");
					//修改已经发送的短信记录	
					sendeddata.put(apacheserver+":"+apache_id,date);	
		 		}else{
		 			//则写声音告警数据
					//向声音告警表里写数据
					AlarmInfo alarminfo = new AlarmInfo();
					alarminfo.setContent(errmsg);
					alarminfo.setIpaddress(errmsg);
					alarminfo.setLevel1(new Integer(2));
					alarminfo.setRecordtime(Calendar.getInstance());
					alarmdao = new AlarmInfoDao();
	                alarmdao.save(alarminfo);
		 			
		 			/*
					Calendar tempCal = Calendar.getInstance();						
					java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String time = sdf.format(cc);					

					String queryStr = "insert into alarminfor(content,ipaddress,level1,recordtime) values('"+errmsg+"','"+ftpstr+"',2,to_date('"+time+"','YYYY-MM-DD HH24:MI:SS'))";					
					Connection con = null;			
					PreparedStatement stmt = null;
					ResultSet rs = null;
					try{
						con=DataGate.getCon();
						stmt = con.prepareStatement(queryStr);
						stmt.execute();
						stmt.close();						
					}catch(Exception ex){
						ex.printStackTrace();
						//rs.close();
					}finally{
						try{
						stmt.close();
						DataGate.freeCon(con);
						}catch(Exception exp){
							//
						}
					}
					*/
	
		 		}
 			}	 			 			 			 			 	
	 	}catch(Exception e){
	 		e.printStackTrace();
	 	}
	 	finally
	 	{
	 		alarmdao.close();
	 	}
	 }

}
