/*
 * Created on 2009-11-29
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.application.dao.CicsConfigDao;
import com.afunms.application.model.CicsConfig;
import com.afunms.application.util.CicsHelper;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.event.dao.AlarmInfoDao;
import com.afunms.event.dao.SmscontentDao;
import com.afunms.event.model.AlarmInfo;
import com.afunms.event.model.Smscontent;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.Cics;
import com.afunms.polling.om.PingCollectEntity;
import com.afunms.polling.om.Task;


/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CicsTask extends MonitorTask {
	//private final static boolean  debug=false; 
	/**
	 * 
	 */
	public CicsTask() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CicsConfigDao cicsConfigDao = new CicsConfigDao();
		try{
			List list=cicsConfigDao.getCicsByFlag(new Integer("1"));
    		int numThreads = 200;
    		try {
    			List numList = new ArrayList();
    			TaskXml taskxml = new TaskXml();
    			numList = taskxml.ListXml();
    			for (int i = 0; i < numList.size(); i++) {
    				Task task = new Task();
    				BeanUtils.copyProperties(task, numList.get(i));
    				if (task.getTaskname().equals("cicstask")){
    					numThreads = task.getPolltime().intValue();
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}

    		// 生成线程池
    		ThreadPool threadPool = null;	
    		if(list != null && list.size()>0){
    			threadPool = new ThreadPool(list.size());	
        		// 运行任务    		
        		for (int i=0; i<list.size(); i++) {    			
            			threadPool.runTask(createTask((CicsConfig)list.get(i)));
        		}
        		// 关闭线程池并等待所有任务完成
        		threadPool.join();
        		threadPool.close();
    		}
    		threadPool = null;

								
		}
		catch(Exception e){
			e.printStackTrace();
					
		}finally{
			cicsConfigDao.close();
			SysLogger.info("********CICS Thread Count : "+Thread.activeCount());
		}
		// TODO Auto-generated method stub
	}
	
    /**
    创建任务
*/	
private static Runnable createTask(final CicsConfig cicsConfig) {
    return new Runnable() {
        public void run() {
        	CicsHelper cicsHelper=null;
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try { 
            	cicsHelper = new CicsHelper();
            	//将历史告警清除
            	Cics _cics = (Cics)PollingEngine.getInstance().getCicsByID(cicsConfig.getId());	
				if(_cics == null){
					return;
				}
				if(_cics != null){
					_cics.setStatus(1);
					_cics.setAlarm(false);
					_cics.getAlarmMessage().clear();
					Calendar _tempCal = Calendar.getInstance();				
					Date _cc = _tempCal.getTime();
					String _time = sdf.format(_cc);
					_cics.setLastTime(_time);
				}
				try{
					//判断告警
					//System.out.println("=================判断CICS告警");
				    if(cicsHelper.openGateway(_cics.getGateway())){//cics服务器启动，且客户端监听进程启动
				    	cicsHelper.closeGateway();
					} else {
						//需要增加CICS服务所在的服务器是否能连通
						Vector ipPingData = (Vector)ShareData.getPingdata().get(cicsConfig.getIpaddress());
						if(ipPingData != null){
							PingCollectEntity pingdata = (PingCollectEntity)ipPingData.get(0);
							String pingvalue = pingdata.getThevalue();
							if(pingvalue == null || pingvalue.trim().length()==0)pingvalue="0";
							double pvalue = new Double(pingvalue);
							if(pvalue == 0){
								//CICS服务器连接不上***********************************************
								com.afunms.polling.node.Cics tnode=(com.afunms.polling.node.Cics)PollingEngine.getInstance().getCicsByIP(cicsConfig.getIpaddress());
								tnode.setAlarm(true);
								List alarmList = tnode.getAlarmMessage();
								if(alarmList == null)alarmList = new ArrayList();
								tnode.getAlarmMessage().add("CICS服务停止,因为所在的服务器连接不上");
					              try{
					            	  SmscontentDao eventdao = new SmscontentDao();
					            	  String eventdesc = "CICS服务("+tnode.getAlias()+" IP:"+cicsConfig.getIpaddress()+")"+"的CICS服务停止";
					            	  eventdao.createEventWithReasion("poll",tnode.getId()+"",tnode.getAdminIp()+"("+tnode.getAdminIp()+")",eventdesc,3,"cics","ping","所在的服务器连接不上");
					            	  PingCollectEntity hostdata=null;
										hostdata=new PingCollectEntity();
										hostdata.setIpaddress(cicsConfig.getIpaddress());
										Calendar date=Calendar.getInstance();
										hostdata.setCollecttime(date);
										hostdata.setCategory("CicsPing");
										hostdata.setEntity("Utilization");
										hostdata.setSubentity("ConnectUtilization");
										hostdata.setRestype("dynamic");
										hostdata.setUnit("%");
										hostdata.setThevalue("0");	
										CicsConfigDao cicsConfigDao=new CicsConfigDao();
										try{
											cicsConfigDao.createHostData(hostdata);	        								
										}catch(Exception e){
											e.printStackTrace();
										}finally{
											cicsConfigDao.close();
										}
					              }catch(Exception e){
					            	  e.printStackTrace();
					              }
							}else{
								com.afunms.polling.node.Cics tnode=(com.afunms.polling.node.Cics)PollingEngine.getInstance().getCicsByIP(cicsConfig.getIpaddress());
								tnode.setAlarm(true);
								List alarmList = tnode.getAlarmMessage();
								if(alarmList == null)alarmList = new ArrayList();
								tnode.getAlarmMessage().add("CICS服务停止");
    							PingCollectEntity hostdata=null;
    							hostdata=new PingCollectEntity();
    							hostdata.setIpaddress(cicsConfig.getIpaddress());
    							Calendar date=Calendar.getInstance();
    							hostdata.setCollecttime(date);
    							hostdata.setCategory("CicsPing");
    							hostdata.setEntity("Utilization");
    							hostdata.setSubentity("ConnectUtilization");
    							hostdata.setRestype("dynamic");
    							hostdata.setUnit("%");
    							hostdata.setThevalue("0");	
    							CicsConfigDao cicsConfigDao=new CicsConfigDao();
    							try{
    								cicsConfigDao.createHostData(hostdata);	        								
    							}catch(Exception e){
    								e.printStackTrace();
    							}finally{
    								cicsConfigDao.close();
    							}
    							createSMS("CICS服务停止",cicsConfig,"ping");
							}
							
						} else {
							//CICS客户端监听进程没有打开
							com.afunms.polling.node.Cics node=(com.afunms.polling.node.Cics)PollingEngine.getInstance().getCicsByID(cicsConfig.getId());
							node.setAlarm(true);
							List alaList = node.getAlarmMessage();
							if(alaList == null)alaList = new ArrayList();
							node.getAlarmMessage().add("CICS客户端监听进程没有打开");
							//SmscontentDao eventdao = new SmscontentDao();
			            	//String eventdesc = "CICS客户端监听进程(gateway:"+cicsConfig.getGateway()+")"+"没有打开";
			            	//eventdao.createEventWithReasion("poll",node.getId()+"",node.getAdminIp()+"("+node.getAdminIp()+")",eventdesc,2,"cics","open","所在的服务器连接不上");
							createSMS("CICS客户端监听进程没有打开",cicsConfig,"open");
							//////////////
//							com.afunms.polling.node.Cics tnode=(com.afunms.polling.node.Cics)PollingEngine.getInstance().getCicsByID(cicsConfig.getId());
//							tnode.setAlarm(true);
//							List alarmList = tnode.getAlarmMessage();
//							if(alarmList == null)alarmList = new ArrayList();
//							tnode.getAlarmMessage().add("CICS服务停止");
//							Pingcollectdata hostdata=null;
//							hostdata=new Pingcollectdata();
//							hostdata.setIpaddress(tnode.getIpaddress());
//							Calendar date=Calendar.getInstance();
//							hostdata.setCollecttime(date);
//							hostdata.setCategory("CicsPing");
//							hostdata.setEntity("Utilization");
//							hostdata.setSubentity("ConnectUtilization");
//							hostdata.setRestype("dynamic");
//							hostdata.setUnit("%");
//							hostdata.setThevalue("0");	
//							CicsConfigDao cicsConfigDao=new CicsConfigDao();
//							try{
//								cicsConfigDao.createHostData(hostdata);	        								
//							}catch(Exception e){
//								e.printStackTrace();
//							}finally{
//								cicsConfigDao.close();
//							}
//							createSMS("CICS服务停止",cicsConfig,"ping");
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
					_cics.setAlarm(true);
					List alarmList = _cics.getAlarmMessage();
					if(alarmList == null)alarmList = new ArrayList();
					_cics.getAlarmMessage().add("CICS服务停止");
					PingCollectEntity hostdata=null;
					hostdata=new PingCollectEntity();
					hostdata.setIpaddress(cicsConfig.getIpaddress());
					Calendar date=Calendar.getInstance();
					hostdata.setCollecttime(date);
					hostdata.setCategory("CicsPing");
					hostdata.setEntity("Utilization");
					hostdata.setSubentity("ConnectUtilization");
					hostdata.setRestype("dynamic");
					hostdata.setUnit("%");
					hostdata.setThevalue("0");	
					CicsConfigDao cicsConfigDao=new CicsConfigDao();
					try{
						cicsConfigDao.createHostData(hostdata);	        								
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						cicsConfigDao.close();
					}
					createSMS("服务停止",cicsConfig,"ping");
				}finally{
				}
				           	 
            }catch(Exception exc){
            	exc.printStackTrace();
            }
        }
    };
}

public static void createSMS(String chlname,CicsConfig cicsConfig,String flag){
 	//建立短信		 	
 	//从内存里获得当前这个IP的PING的值
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	AlarmInfoDao alarminfomanager=new AlarmInfoDao();

	String ipaddress = cicsConfig.getIpaddress();
	Hashtable sendeddata = ShareData.getSendeddata();
 	Calendar date=Calendar.getInstance();
 	String time = sdf.format(date.getTime());
 	try{
		if (!sendeddata.containsKey(ipaddress+":"+chlname)){
			//若不在，则建立短信，并且添加到发送列表里
 			Smscontent smscontent = new Smscontent();
 			smscontent.setLevel("2");
 			smscontent.setObjid(cicsConfig.getId()+"");
 			if("chl".equalsIgnoreCase(flag)){
 				smscontent.setMessage(cicsConfig.getRegion_name()+" ("+chlname+")"+"通道处于非正常运行状态");
 			}else
 				smscontent.setMessage(cicsConfig.getRegion_name()+"(IP:"+cicsConfig.getIpaddress()+")服务停止");
 			smscontent.setRecordtime(time);
 			smscontent.setSubtype("cics");
 			smscontent.setSubentity("ping");
 			smscontent.setIp(cicsConfig.getIpaddress());
 			//发送短信
 			SmscontentDao smsmanager=new SmscontentDao();
 			smsmanager.sendURLSmscontent(smscontent);	
			sendeddata.put(ipaddress+":"+chlname,date);		 					 				
		}else{
			//若在，则从已发送短信列表里判断是否已经发送当天的短信
			Calendar formerdate =(Calendar)sendeddata.get(ipaddress+":"+chlname);		 				
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
 			String errmsg = "";
 			long subvalue = current.getTime()-last.getTime();			 			
 			if (subvalue/(1000*60*60*24)>=1){
 				//超过一天，则再发信息
 				Smscontent smscontent = new Smscontent();
 	 			smscontent.setLevel("2");
 	 			smscontent.setObjid(cicsConfig.getId()+"");
 	 			if("chl".equalsIgnoreCase(flag)){
 	 				smscontent.setMessage(cicsConfig.getRegion_name()+" ("+chlname+")"+"通道处于非正常运行状态");
 	 			}else
 	 				smscontent.setMessage(cicsConfig.getRegion_name()+"(IP:"+cicsConfig.getIpaddress()+")服务停止");
 	 			errmsg = smscontent.getMessage();
 	 			smscontent.setRecordtime(time);
 	 			smscontent.setSubtype("cics");
 	 			smscontent.setSubentity("ping");
 	 			smscontent.setIp(cicsConfig.getIpaddress());
 	 			//发送短信
 	 			SmscontentDao smsmanager=new SmscontentDao();
 	 			smsmanager.sendURLSmscontent(smscontent);	
				//修改已经发送的短信记录	
				sendeddata.put(ipaddress+":"+chlname,date);	
	 			//则写声音告警数据
				//向声音告警表里写数据
				//只有在发短信后才产生语音告警
				
				AlarmInfo alarminfo = new AlarmInfo();
				alarminfo.setContent(smscontent.getMessage());
				alarminfo.setIpaddress(cicsConfig.getIpaddress());
				alarminfo.setLevel1(new Integer(2));
				alarminfo.setRecordtime(Calendar.getInstance());
				alarminfomanager.save(alarminfo);
				
	 		}else{
	 			//则写声音告警数据
				//向声音告警表里写数据
	 			
				AlarmInfo alarminfo = new AlarmInfo();
				alarminfo.setContent(errmsg);
				alarminfo.setIpaddress(cicsConfig.getIpaddress());
				alarminfo.setLevel1(new Integer(2));
				alarminfo.setRecordtime(Calendar.getInstance());
				alarminfomanager.save(alarminfo);
				

	 		}
		}	 			 			 			 			 	
 	}catch(Exception e){
 		e.printStackTrace();
 	}
 }

}
