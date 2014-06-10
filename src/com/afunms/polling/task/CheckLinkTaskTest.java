/*
 * Created on 2005-4-22
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.afunms.polling.task;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.beanutils.BeanUtils;

import com.afunms.common.util.Arith;
import com.afunms.common.util.SysLogger;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.api.I_HostLastCollectData;
import com.afunms.polling.base.LinkRoad;
import com.afunms.polling.impl.HostCollectDataManager;
import com.afunms.polling.impl.HostLastCollectDataManager;
import com.afunms.polling.node.Host;
import com.afunms.polling.om.Interfacecollectdata;
import com.afunms.polling.om.Task;
import com.afunms.polling.om.UtilHdx;
import com.afunms.topology.dao.LinkDao;
import com.afunms.topology.model.Link;
import com.afunms.topology.model.NodeMonitor;
import com.afunms.common.util.ShareData;
import com.ibm.db2.jcc.c.i;

/**
 * @author hukelei
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CheckLinkTaskTest extends MonitorTask {
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	private String linkid;//链路的ID
	
	public String getLinkid() {
		return linkid;
	}

	public void setLinkid(String linkid) {
		this.linkid = linkid;
	}
	
	
	public void run() {
		//SysLogger.info("#####开始检查链路####"+linkid);
		if(linkid == null){//该节点不存在,nodeid未被初始化
			SysLogger.info("该链路不存在,linkid未被初始化");
			return;
		}
    	//开始运行任务
		try {
			//需要做分布式判断
			String runmodel = PollingEngine.getCollectwebflag(); 
			
			int flag = 0;
			LinkRoad lr = null;
			List linkList = PollingEngine.getInstance().getLinkList();
			if(linkList != null && linkList.size()>0){
				LinkRoad link = null;
				for(int i=0;i<linkList.size();i++){
					link = (LinkRoad)linkList.get(i);
					if(link.getId() == Integer.parseInt(linkid)){
						lr = link;
						break;
					}
				}
			}
			if(lr == null){
				SysLogger.info("该链路不存在,linkid未被初始化");
				return;
			}
			Host host1 = (Host) PollingEngine.getInstance().getNodeByID(
					lr.getStartId());
			Host host2 = (Host) PollingEngine.getInstance().getNodeByID(
					lr.getEndId());
			
//			IfEntity if1 = host1.getIfEntityByIndex(lr.getStartIndex());
//			IfEntity if2 = host2.getIfEntityByIndex(lr.getEndIndex());

			// yangjun
			String start_inutilhdx = "0";
			String start_oututilhdx = "0";
			String start_inutilhdxperc = "0";
			String start_oututilhdxperc = "0";
			String end_inutilhdx = "0";
			String end_oututilhdx = "0";
			String end_inutilhdxperc = "0";
			String end_oututilhdxperc = "0";
			String starOper = "";
			String endOper = "";
			// end
//			if (if1 == null || if2 == null) {
//				lr.setAlarm(true);
//				flag = 1;
//			}
			if (host1 != null && host2 != null) {
				

				Vector vector1 = new Vector();
				Vector vector2 = new Vector();
				String[] netInterfaceItem = { "index", "ifDescr",
						"ifSpeed", "ifOperStatus", "ifOutBroadcastPkts",
						"ifInBroadcastPkts", "ifOutMulticastPkts",
						"ifInMulticastPkts", "OutBandwidthUtilHdx",
						"InBandwidthUtilHdx", "InBandwidthUtilHdxPerc",
						"OutBandwidthUtilHdxPerc" };
					if("0".equals(runmodel)){
				       	//采集与访问是集成模式	
						I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
						vector1 = hostlastmanager.getInterface_share(host1
								.getIpAddress(), netInterfaceItem, "index", "",
								"");
						vector2 = hostlastmanager.getInterface_share(host2
								.getIpAddress(), netInterfaceItem, "index", "",
								"");
						hostlastmanager = null;
					}else{
						//采集与访问是分离模式
//						vector1 = hostlastmanager.getInterface(host1.getIpAddress(),netInterfaceItem,"index","",""); 
//						vector2 = hostlastmanager.getInterface(host2.getIpAddress(),netInterfaceItem,"index","",""); 
						//链路节点的数据集合
						Hashtable interfaceHash = ShareData.getAllLinknodeInterfaceData();
						//SysLogger.info(linkid+"============="+interfaceHash.size());
						if(interfaceHash != null && interfaceHash.containsKey(host1.getIpAddress())){
							vector1 = (Vector)interfaceHash.get(host1.getIpAddress());
						}
						if(interfaceHash != null && interfaceHash.containsKey(host2.getIpAddress())){
							vector2 = (Vector)interfaceHash.get(host2.getIpAddress());
						}
					}
				
					
				// SysLogger.info("#####开始检查链路端口####");
				if (vector1 != null && vector1.size() > 0) {
					for (int k = 0; k < vector1.size(); k++) {
						String[] strs = (String[]) vector1.get(k);
						String index = strs[0];
//						String ifOutNUcastPkts = strs[4]
//								.replaceAll("个", "");
//						String ifInNUcastPkts = strs[5].replaceAll("个", "");
//						String ifOutUcastPkts = strs[6].replaceAll("个", "");
//						String ifInUcastPkts = strs[7].replaceAll("个", "");
						// SysLogger.info(host1.getIpAddress()+"===="+ifOutNUcastPkts+"=="+ifInNUcastPkts+"==="+ifOutUcastPkts+"==="+ifInUcastPkts);
						if (index.equalsIgnoreCase(lr.getStartIndex())) {
							starOper = strs[3];
							start_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");// yangjun
							start_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");// yangjun
							start_oututilhdxperc = strs[10].replaceAll("%",
									"");// yangjun
							start_inutilhdxperc = strs[11].replaceAll("%",
									"");// yangjun
//							else {
//								// SysLogger.info("#####开始检查链路端口数据包####");
//								List molist = host1.getMoidList();
//								// SysLogger.info(host1.getIpAddress()+"====molist:
//								// "+molist.size());
//								if (molist != null && molist.size() > 0) {
//									for (int m = 0; m < molist.size(); m++) {
//										NodeMonitor nm = (NodeMonitor) molist
//												.get(m);
//										// SysLogger.info(host1.getIpAddress()+"==index:"+index+"===Subentity:"+nm.getSubentity());
//										if (nm
//												.getSubentity()
//												.equalsIgnoreCase(
//														"ifInMulticastPkts")) {
//											int pktflag = checkEvent(
//													host1,
//													index,
//													"ifInMulticastPkts",
//													Double
//															.parseDouble(ifInUcastPkts),
//													nm);
//											if (pktflag > 0) {
//												// 有告警产生
//												lr.setAlarm(true);
//												flag = 1;
//												break;
//											}
//										} else if (nm
//												.getSubentity()
//												.equalsIgnoreCase(
//														"ifInBroadcastPkts")) {
//											int pktflag = checkEvent(
//													host1,
//													index,
//													"ifInBroadcastPkts",
//													Double
//															.parseDouble(ifInNUcastPkts),
//													nm);
//											if (pktflag > 0) {
//												// 有告警产生
//												lr.setAlarm(true);
//												flag = 1;
//												break;
//											}
//										} else if (nm
//												.getSubentity()
//												.equalsIgnoreCase(
//														"ifOutMulticastPkts")) {
//											int pktflag = checkEvent(
//													host1,
//													index,
//													"ifOutMulticastPkts",
//													Double
//															.parseDouble(ifOutUcastPkts),
//													nm);
//											if (pktflag > 0) {
//												// 有告警产生
//												lr.setAlarm(true);
//												flag = 1;
//												break;
//											}
//										} else if (nm
//												.getSubentity()
//												.equalsIgnoreCase(
//														"ifOutBroadcastPkts")) {
//											int pktflag = checkEvent(
//													host1,
//													index,
//													"ifOutBroadcastPkts",
//													Double
//															.parseDouble(ifOutNUcastPkts),
//													nm);
//											if (pktflag > 0) {
//												// 有告警产生
//												lr.setAlarm(true);
//												flag = 1;
//												break;
//											}
//										}
//
//									}
//								}
//							}
//							if (flag == 1)break;
						}
					}
				}
				if (vector2 != null && vector2.size() > 0) {
					for (int k = 0; k < vector2.size(); k++) {
						String[] strs = (String[]) vector2.get(k);
						String index = strs[0];
//						String ifOutNUcastPkts = strs[4]
//								.replaceAll("个", "");
//						String ifInNUcastPkts = strs[5].replaceAll("个", "");
//						String ifOutUcastPkts = strs[6].replaceAll("个", "");
//						String ifInUcastPkts = strs[7].replaceAll("个", "");
						if (index.equalsIgnoreCase(lr.getEndIndex())) {
							endOper = strs[3];
							end_oututilhdx = strs[8].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");// yangjun
							end_inutilhdx = strs[9].replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", "");// yangjun
							end_oututilhdxperc = strs[10].replaceAll("%",
									"");// yangjun
							end_inutilhdxperc = strs[11]
									.replaceAll("%", "");// yangjun
//							else {
//								List molist = host2.getMoidList();
//								if (molist != null && molist.size() > 0) {
//									for (int m = 0; m < molist.size(); m++) {
//										NodeMonitor nm = (NodeMonitor) molist
//												.get(m);
//										if (nm.getSubentity().equals(
//												"ifInUcastPkts")) {
//											int pktflag = checkEvent(
//													host2,
//													index,
//													"ifInUcastPkts",
//													Double
//															.parseDouble(ifInUcastPkts),
//													nm);
//											if (pktflag > 0) {
//												// 有告警产生
//												lr.setAlarm(true);
//												flag = 1;
//												break;
//											}
//										} else if (nm.getSubentity()
//												.equals("ifInNUcastPkts")) {
//											int pktflag = checkEvent(
//													host2,
//													index,
//													"ifInNUcastPkts",
//													Double
//															.parseDouble(ifInNUcastPkts),
//													nm);
//											if (pktflag > 0) {
//												// 有告警产生
//												lr.setAlarm(true);
//												flag = 1;
//												break;
//											}
//										} else if (nm.getSubentity()
//												.equals("ifOutUcastPkts")) {
//											int pktflag = checkEvent(
//													host2,
//													index,
//													"ifOutUcastPkts",
//													Double
//															.parseDouble(ifOutUcastPkts),
//													nm);
//											if (pktflag > 0) {
//												// 有告警产生
//												lr.setAlarm(true);
//												flag = 1;
//												break;
//											}
//										} else if (nm.getSubentity()
//												.equals("ifOutNUcastPkts")) {
//											int pktflag = checkEvent(
//													host2,
//													index,
//													"ifOutNUcastPkts",
//													Double
//															.parseDouble(ifOutNUcastPkts),
//													nm);
//											if (pktflag > 0) {
//												// 有告警产生
//												lr.setAlarm(true);
//												flag = 1;
//												break;
//											}
//										}
//
//									}
//								}
//							}
//							if (flag == 1)break;
						}
					}
				}
				// yangjun
				if ("down".equalsIgnoreCase(starOper)) {
					lr.setAlarm(true);
					flag = 1;
				} 
				if ("down".equalsIgnoreCase(endOper)) {
					lr.setAlarm(true);
					flag = 1;
				} 
				int downspeed = (Integer.parseInt(start_oututilhdx) + Integer
						.parseInt(end_inutilhdx.replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;
				int upspeed = (Integer.parseInt(start_inutilhdx) + Integer
						.parseInt(end_oututilhdx.replaceAll("KB/秒", "").replaceAll("kb/s", "").replaceAll("kb/秒", "").replaceAll("KB/S", ""))) / 2;
				
				//将链路状态插入数据库
				Calendar date=Calendar.getInstance();
				int linkflag = 100;
				if ("down".equalsIgnoreCase(starOper)||"down".equalsIgnoreCase(endOper)) {
					linkflag = 0;
					lr.setAlarm(true);//HONGLI ADD
					flag = 1;//HONGLI ADD  
				}
//				System.out.println("starOper----->"+starOper);
				Interfacecollectdata interfacelink=new Interfacecollectdata();
				interfacelink.setIpaddress("");
				interfacelink.setCollecttime(date);
				interfacelink.setCategory("Ping");
				interfacelink.setEntity("Utilization");
				interfacelink.setSubentity("ConnectUtilization");
				interfacelink.setRestype("dynamic");
				interfacelink.setUnit("%");	
				interfacelink.setChname("");
				interfacelink.setThevalue(linkflag+"");
				Vector linkstatusv = new Vector();
				linkstatusv.add(interfacelink);
				
				//将上下行流速插入数据库,待完成
				Vector v = new Vector();
				
				UtilHdx uputilhdx=new UtilHdx();
				uputilhdx.setIpaddress("");
				uputilhdx.setCollecttime(date);
				uputilhdx.setCategory("");
				uputilhdx.setEntity("UP");
				uputilhdx.setSubentity("");
				uputilhdx.setRestype("dynamic");
				uputilhdx.setUnit("Kb/秒");	
				uputilhdx.setChname("");										   	
				DecimalFormat df=new DecimalFormat("#.##");//yangjun 
				uputilhdx.setThevalue(df.format(upspeed));
				v.add(uputilhdx);
				
				UtilHdx downutilhdx=new UtilHdx();
				downutilhdx.setIpaddress("");
				downutilhdx.setCollecttime(date);
				downutilhdx.setCategory("");
				downutilhdx.setEntity("DOWN");
				downutilhdx.setSubentity("");
				downutilhdx.setRestype("dynamic");
				downutilhdx.setUnit("Kb/秒");	
				downutilhdx.setChname("");
				downutilhdx.setThevalue(df.format(downspeed));
				v.add(downutilhdx);
				
				
				
				if (lr.getMaxSpeed() != null
						&& !"".equals(lr.getMaxSpeed())&& !"null".equals(lr.getMaxSpeed())) {
					if (upspeed > Integer.parseInt(lr.getMaxSpeed())) {
						// 有告警产生
						lr.setAlarm(true);
						flag = 1;
					}
					if (downspeed > Integer.parseInt(lr.getMaxSpeed())) {
						// 有告警产生
						lr.setAlarm(true);
						flag = 1;
					}
				} else {// 链路没有设定阀值的情况下，由端口阀值获得

				}
				double downperc = 0;
				try {
					if (start_oututilhdxperc != null
							&& start_oututilhdxperc.trim().length() > 0
							&& end_inutilhdxperc != null
							&& end_inutilhdxperc.trim().length() > 0)
						downperc = Arith.div((Double
								.parseDouble(start_oututilhdxperc) + Double
								.parseDouble(end_inutilhdxperc)), 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				double upperc = 0;
				try {
					if (start_inutilhdxperc != null
							&& start_inutilhdxperc.trim().length() > 0
							&& end_oututilhdxperc != null
							&& end_oututilhdxperc.trim().length() > 0)
						upperc = Arith.div((Double
								.parseDouble(start_inutilhdxperc) + Double
								.parseDouble(end_oututilhdxperc)), 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (lr.getMaxPer() != null && !"".equals(lr.getMaxPer())&& !"null".equals(lr.getMaxPer())) {
					if (upperc > Double.parseDouble(lr.getMaxPer())) {
                        //有告警产生
						lr.setAlarm(true);
						flag = 1;
					}
					if (downperc > Double.parseDouble(lr.getMaxPer())) {
                        //有告警产生
						lr.setAlarm(true);
						flag = 1;
					}
				} else {// 链路没有设定阀值的情况下，由端口阀值获得
					
				}
				
				Interfacecollectdata utilhdxperc=new Interfacecollectdata();
				utilhdxperc.setIpaddress("");
				utilhdxperc.setCollecttime(date);
				utilhdxperc.setCategory("");
				utilhdxperc.setEntity("");
				utilhdxperc.setSubentity("");
				utilhdxperc.setRestype("dynamic");
				utilhdxperc.setUnit("%");	
				utilhdxperc.setChname("");
				utilhdxperc.setThevalue(df.format(upperc+downperc));
				Vector utilhdxpercv = new Vector();
				utilhdxpercv.add(utilhdxperc);

				Hashtable linkdataHash = new Hashtable();
				linkdataHash.put("util", v);
				linkdataHash.put("linkstatus", linkstatusv);
				linkdataHash.put("linkutilperc", utilhdxpercv);
				linkdataHash.put("linkid", lr.getId());
				//SysLogger.info(lr.getId()+"  添加链路数据到ALLLINKDATA 。。。"+linkdataHash.size());
				ShareData.getAllLinkData().put(lr.getId()+"", linkdataHash);
				// end
			}
			if (flag == 0) {
				lr.setAlarm(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
	       	int linkCollectedSize = -1;
        	if(!linkid.equals("")){
        		linkCollectedSize = ShareData.addLinkCollectedSize();
        	}
        	int needCollectNodesSize = ShareData.getLinkTimerMap().keySet().size();
        	//System.out.println("####linkid:"+linkid+"####needCollectNodesSize:"+needCollectNodesSize+"====ShareData.getLinkCollectedSize():"+linkCollectedSize);
        	//判断所有Task是否运行完毕
			if(needCollectNodesSize == linkCollectedSize){//需要采集的设备个数 和 已采集的设备个数相等，则保存
	    		ShareData.setLinkCollectedSize(0);
	    		Date startdate = new Date();
	    		processLinkData();
	    		Date enddate = new Date();
	    		SysLogger.info("##############################");
	    		SysLogger.info("### 所有链路入库时间 "+(enddate.getTime()-startdate.getTime())+"####");
	    		SysLogger.info("##############################");
	    		
    	  		if(ShareData.getRunflag() == 0){
    	  			//第一次启动
    	  			ShareData.setRunflag(1);
    	  			SysLogger.info("### 系统第一次启动,不进行链路检测,跳过 ###");
    	  		}else{
    	  			//先集中采集所有链路节点的数据集合存到内存中 线程中不再做单个链路节点的数据采集
    	  			getLinknodeInterfaceData(PollingEngine.getInstance().getLinkList());
    	  		}
	    		
    	  		//清空
	    		//ShareData.getAllLinkData().clear();
	    		
	    		//判断链路是否已经启动定时，若未启动则启动
	    		LinkRoad link = null;
	    		Hashtable dohash = new Hashtable();
	    		TaskUtil taskutil = new TaskUtil();
	    		for(int i=0;i<PollingEngine.getInstance().getLinkList().size();i++){
	    			link = (LinkRoad)PollingEngine.getInstance().getLinkList().get(i);
	    			dohash.put(link.getId()+"", link.getId());
	    			if(!ShareData.getLinkTimerMap().containsKey(link.getId()+"")){
	    				//启动
	    				Hashtable taskhash = taskutil.getInterval("checklinktask");
	    				if(taskhash != null && taskhash.size()>0){
	    					taskutil.addLinkTask(link.getId()+"",taskhash);
	    				}
	    				//TaskUtil.addLinkTask(link.getId()+"");
	    			}
	    		} 
	    		taskutil = null;
	    		
	    		//判断链路是否被删除，若删除，则取消定时
	    		Iterator iterator = ShareData.getLinkTimerMap().keySet().iterator();
	    		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		while (iterator.hasNext()) {
	    			 String linkid = String.valueOf(iterator.next());
	    			 if(!dohash.containsKey(linkid)){
	    				 //取消
	    				 ShareData.getLinkTimerMap().get(linkid).cancel();
	    				 ShareData.getLinkTimerMap().remove(linkid);
	    			 }
	    		}
				SysLogger.info("********CheckLinkTaskTest Thread Count : "+Thread.activeCount());
			}
		}
        //结束运行任务
    
	}

	/**
	 * 先集中采集所有链路节点的数据集合存到内存中 
	 * @param linkList
	 */
	public static Hashtable getLinknodeInterfaceData(List linkList) {
		I_HostLastCollectData hostlastmanager = new HostLastCollectDataManager();
		String[] netInterfaceItem = { "index", "ifDescr",
				"ifSpeed", "ifOperStatus", "ifOutBroadcastPkts",
				"ifInBroadcastPkts", "ifOutMulticastPkts",
				"ifInMulticastPkts", "OutBandwidthUtilHdx",
				"InBandwidthUtilHdx", "InBandwidthUtilHdxPerc",
				"OutBandwidthUtilHdxPerc" };
		List<String> ipList = new ArrayList<String>();
		for(int m=0; m<linkList.size(); m++){
			Object obj = linkList.get(m);
			Host host1 = null;
			Host host2 = null;
			if(obj instanceof LinkRoad){
				LinkRoad lr = (LinkRoad)linkList.get(m);
				host1 = (Host) PollingEngine.getInstance().getNodeByID(lr.getStartId());
				host2 = (Host) PollingEngine.getInstance().getNodeByID(lr.getEndId());
			}
			if(obj instanceof Link){
				Link lr = (Link)linkList.get(m);
				host1 = (Host) PollingEngine.getInstance().getNodeByID(lr.getStartId());
				host2 = (Host) PollingEngine.getInstance().getNodeByID(lr.getEndId());
			}
			if(host1 != null){
				ipList.add(host1.getIpAddress());
			}
			if(host2 != null){
				ipList.add(host2.getIpAddress());
			}
		}
		//排除重复元素
		HashSet h = new HashSet(ipList);   
		//ipList.clear();   
		ipList.addAll(h); 
		
		Hashtable interfaceHash = hostlastmanager.getInterfaces(ipList,netInterfaceItem,"index","",""); 
		//存入内存
		ShareData.setAllLinknodeInterfaceData(interfaceHash);
		return interfaceHash;
	}
	
	/**
	 * 批量保存link数据
	 */
	private void processLinkData() {
		if(ShareData.getAllLinkData() != null && !ShareData.getAllLinkData().isEmpty()){
			LinkDao linkDao = null;
			try{
				linkDao = new LinkDao();
				//SysLogger.info(" ##### 开始处理链路入库 ##############");
				linkDao.processlinkData();
				//SysLogger.info(" ##### 结束处理链路入库 ##############");
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(linkDao != null){
					linkDao.close();
				}
			}
		}
	}
	
}
