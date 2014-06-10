package com.afunms.capreport.manage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.afunms.capreport.common.DateTime;
import com.afunms.capreport.dao.CycleReportConfigDao;
import com.afunms.capreport.dao.SubscribeResourcesDao;
import com.afunms.capreport.model.CycleReportConfig;
import com.afunms.capreport.model.ReportConfigNode;
import com.afunms.capreport.model.ReportTime;
import com.afunms.capreport.model.SubscribeResources;
import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.SendMailManager;
import com.afunms.common.util.SessionConstant;
import com.afunms.common.util.ShareData;
import com.afunms.common.util.SysLogger;
import com.afunms.config.dao.BusinessDao;
import com.afunms.config.model.Business;
import com.afunms.initialize.ResourceCenter;
import com.afunms.system.dao.UserDao;
import com.afunms.system.model.User;
import com.afunms.topology.dao.HostNodeDao;
import com.afunms.topology.model.HostNode;

public class SubscribeReportConfigManager extends BaseManager implements ManagerInterface
{
	private static Logger log = Logger.getLogger(SubscribeReportConfigManager.class);
	public final static int CYCLE_TIME_OF_WEEK = 86400000*7;
	static Hashtable weekHash = null;
	static{
		weekHash = new Hashtable();
		weekHash.put("1", "星期日");
		weekHash.put("2", "星期一");
		weekHash.put("3", "星期二");
		weekHash.put("4", "星期三");
		weekHash.put("5", "星期四");
		weekHash.put("6", "星期五");
		weekHash.put("7", "星期六");
	}
	public SubscribeReportConfigManager()
	{
		
	}
	public String execute(String action)
	{
		// TODO Auto-generated method stub
		if("list".equals(action))
		{
			return list();
		}
		if("delete".equals(action))
		{
			return delete();
		}
		if("ready_add".equals(action))
		{
			return ready_add();
		}
		if("user_list".equals(action))
		{
			return userList();
		}
		if("device_list".equals(action))
		{
			return deviceList();
		}
		if("add".equals(action))
		{
			return add();
		}
		if("ready_edit".equals(action))
		{
			return ready_edit();
		}
		if("update".equals(action))
		{
			return update();
		}
		if("sendEmail".equals(action))
		{
			return sendEmail("guzhiming@dhcc.com.cn");
		}
		if("test".equals(action))
		{
			return test();
		}
		if("createModel".equals(action))
		{
			return createModel();
		}
		 if(action.equals("assetReport")){
	        	return assetReport();
	        }
		return null;
	}
	private String assetReport(){
		
		String jsp="/equip/assetReport.jsp";
		return jsp;
	}
	private String test()
	{
		String s = getBidSql();
		return s;
	}
	private String sendEmail(String mailAddressOfReceiver)
	{
		SendMailManager mailManager = new SendMailManager();
		String fileName = ResourceCenter.getInstance().getSysPath() + "temp/hostnms_report.xls";
		mailManager.SendMailWithFile("supergzm@sina.com",mailAddressOfReceiver, "设备报表", fileName);
		return list();
	}
	private String update()
	{
		String id = this.getParaValue("id");
		Hashtable subscribeHash = ShareData.getSubscribeReportHash();
		//SysLogger.info("id===="+id);
		List taskList = (List)subscribeHash.get(Integer.parseInt(id.trim()));
		if(taskList != null && taskList.size()>0){
			for(int i =0;i<taskList.size();i++)
			{
				Timer t = (Timer)taskList.get(i);
				t.cancel();
			}
		}

		subscribeHash.remove(Integer.parseInt(id.trim()));
		String[] IDs = {id};
		CycleReportConfigDao dao = new CycleReportConfigDao();
		dao.delete(IDs);
		dao.close();
		add();
		return list();
	}
	private String ready_edit()
	{
		String id = this.getParaValue("id");
		CycleReportConfigDao dao = new CycleReportConfigDao();
		CycleReportConfig config = (CycleReportConfig)dao.findByID(id);
		List list = new ArrayList();
		list.add(config);
		List nodeList = cycleReportConfigConverToReportConfigNode(list);
		ReportConfigNode node = (ReportConfigNode)nodeList.get(0);
		String collectionTime = node.getConfig().getCollectionOfGenerationTime();
		String splitTime = collectionTime.substring(1);
		String[] split = splitTime.split(",");
		List timeList = new ArrayList();
		for(int i =0;i<split.length;i++)
		{
			String temp1 = split[i];
			String temp2 = temp1.substring(0,1);
			String temp3 = temp1.substring(2, temp1.length()-1);
			String temp4 = (String)weekHash.get(temp2);
			ReportTime time = new ReportTime();
			time.setDayofweek(Integer.parseInt(temp2));
			time.setHour(Integer.parseInt(temp3));
			timeList.add(time);
		}
		JSONArray json_timeList = JSONArray.fromObject(timeList);
		request.setAttribute("json_time", json_timeList.toString());
		System.out.println(json_timeList.toString());
		request.setAttribute("node", node);
		return "/capreport/subscribeReportConfig/edit.jsp";
	}
	
	private String delete()
	{
		String[] ids = this.getParaArrayValue("checkbox");
		CycleReportConfigDao dao = new CycleReportConfigDao();
		dao.delete(ids);
		dao.commit();
		dao.close();
		Hashtable subscribeHash = ShareData.getSubscribeReportHash();
		for(int i=0;i<ids.length;i++)
		{
			String id = ids[i];
			//SysLogger.info("===id=="+id);
			List taskList = (List)subscribeHash.get(Integer.parseInt(id.trim()));
			if(taskList != null){
				for(int j =0;j<taskList.size();j++)
				{
					Timer t = (Timer)taskList.get(j);
					t.cancel();
				}
			}
			subscribeHash.remove(Integer.parseInt(id.trim()));
		}
		return list();
	}
	
	public String arrayToString(String[] array) {
		StringBuilder sb = new StringBuilder();
		if (array != null) {
			for (String value : array) {
				sb.append("/");
				sb.append(value);
			}
			sb.append("/");
		}
		return sb.toString();
	}

	private String add() {
		String[] sendtimemonth = request.getParameterValues("sendtimemonth");
		String[] sendtimeweek = request.getParameterValues("sendtimeweek");
		String[] sendtimeday = request.getParameterValues("sendtimeday");
		String[] sendtimehou = request.getParameterValues("sendtimehou");
		String transmitfrequency = request.getParameter("transmitfrequency");

		String recieversId = this.getParaValue("recievers_id");
		String bid = this.getParaValue("bid");
		String devicesId = this.getParaValue("devices_id");
//		Hashtable hourHash = new Hashtable();
//		Hashtable weekHash = new Hashtable();
//		Enumeration paramsEnu = request.getParameterNames();
//		while (paramsEnu.hasMoreElements()) {
//			String temp = (String) paramsEnu.nextElement();
//			if (temp.startsWith("hour")) {
//				String key = temp.substring(4);
//				String value = this.getParaValue(temp);
//				hourHash.put(key, value);
//			} else if (temp.startsWith("week")) {
//				String key = temp.substring(4);
//				String value = this.getParaValue(temp);
//				weekHash.put(key, value);
//			}
//		}
//		String collectionTime = "";
//		Enumeration weekEnu = weekHash.keys();
//		List timeList = new ArrayList();
//
//		while (weekEnu.hasMoreElements()) {
//			String weekKey = (String) weekEnu.nextElement();
//			String weekValue = (String) weekHash.get(weekKey);
//			String hourValue = (String) hourHash.get(weekKey);
//			ReportTime t = new ReportTime();
//			t.setDayofweek(Integer.parseInt(weekValue));
//			t.setHour(Integer.parseInt(hourValue));
//			timeList.add(t);
//			String tempTime = weekValue + "周" + hourValue + "时";
//			collectionTime = collectionTime + "," + tempTime;
//		}
//		CycleReportConfig config = new CycleReportConfig();
//		config.setBids(bid);
//		config.setCollectionOfdeviceId(devicesId);
//		config.setCollectionOfGenerationTime(collectionTime);
//		config.setCollectionOfRecieverId(recieversId);
//		CycleReportConfigDao dao = new CycleReportConfigDao();
//		boolean b = dao.save(config);
//		dao.close();

		UserDao userDao = new UserDao();
		List userList = new ArrayList();
		try{
			userList = userDao.findbyIDs(recieversId.substring(1));
		}catch(Exception e){
			e.printStackTrace();
			log.error("",e);
		}finally{
			userDao.close();
		}
		StringBuffer buf = new StringBuffer();
		for(int i =0;i<userList.size();i++){
			User vo = (User)userList.get(i);
			buf.append(vo.getEmail());
			buf.append(",");
		}
		DateTime dt = new DateTime();
		SubscribeResources sr = new SubscribeResources();
		
		//sr.setSubscribe_id((int)Math.random());//需修改
		sr.setEmail(buf.toString());
		sr.setEmailtitle("网管邮件");
		sr.setEmailcontent("设备报表");
		sr.setAttachmentformat("xls");
		sr.setReport_type("day");
		sr.setReport_senddate(Integer.parseInt(dt.getMyDateTime(DateTime.Datetime_Format_14)));
		sr.setReport_sendfrequency(Integer.parseInt(transmitfrequency));
		sr.setReport_time_month(arrayToString(sendtimemonth));
		sr.setReport_time_week(arrayToString(sendtimeweek));
		sr.setReport_time_day(arrayToString(sendtimeday));
		sr.setReport_time_hou(arrayToString(sendtimehou));
		sr.setBidtext(bid);
		sr.setBID(bid);
		// sr.setReport_day_stop(report_day_stop);
		// sr.setReport_week_stop(report_week_stop);
		// sr.setReport_month_stop(report_month_stop);
		// sr.setReport_season_stop(report_season_stop);
		// sr.setReport_year_stop(report_year_stop);
		SubscribeResourcesDao srd = new SubscribeResourcesDao();
		boolean b = false;
		try{
			b = srd.save(sr);
		}catch(Exception e){
			e.printStackTrace();
		}

//		List taskList = new ArrayList();
//		for (int i = 0; i < timeList.size(); i++) {
//			ReportTime tt = (ReportTime) timeList.get(i);
//			Timer timer = new Timer();
//			MyTask task = new MyTask();
//			task.setConfig(config);
//			Date d = getSpecificCycleExecuteTime(tt.getDayofweek(), tt.getHour());
//			timer.schedule(task, d, CYCLE_TIME_OF_WEEK);
//			// System.out.println(d);
//			// timer.schedule(task, new Date(), CYCLE_TIME_OF_WEEK);
//			taskList.add(timer);
//		}
//		Hashtable subscribeHash = ShareData.getSubscribeReportHash();
//		subscribeHash.put(config.getId(), taskList);
		if (b){
			return list();
		}
		else{
			return null;
		}
	}

	private String userList()
	{
		String ctrlId = this.getParaValue("ctrlId");
		String hideCtrlId = this.getParaValue("hideCtrlId");
		request.setAttribute("ctrlId", ctrlId);
		request.setAttribute("hideCtrlId", hideCtrlId);
		UserDao userDao = new UserDao();
		List userList = userDao.loadAll();
		userDao.close();
		request.setAttribute("userList", userList);
		return "/capreport/subscribeReportConfig/user_list.jsp";
	}
	private String deviceList()
	{
		String ctrlId = this.getParaValue("ctrlId");
		String hideCtrlId = this.getParaValue("hideCtrlId");
		request.setAttribute("ctrlId", ctrlId);
		request.setAttribute("hideCtrlId", hideCtrlId);
		String category = request.getParameter("category");
		request.setAttribute("category", category);
		List  monitornodelist = getMonitorListByCategory(category);
		request.setAttribute("list", monitornodelist);
		return "/capreport/subscribeReportConfig/device_list.jsp";
	}
	private List getMonitorListByCategory(String category)
    {
    	
    	String where = "";
    	
//    	if("node".equals(category)){
//    		where = " where managed=1";
//    	}else if("host".equals(category)){
//    		where = " where managed=1 and category=4";
//    	}else if("net".equals(category)){
//    		where = " where managed=1 and (category=1 or category=2 or category=3 or category=7) ";
//    	}else if("router".equals(category)){
//    		where = " where managed=1 and category=1";
//    	}else if("switch".equals(category)){
//    		where = " where managed=1 and (category=2 or category=3 or category=7) ";
//    	}
    	
    	if("node".equals(category)){
    		where = " where managed=1";
    	}else if("net_server".equals(category)){
    		where = " where managed=1 and category=4";
    	}else if("net".equals(category)){
    		where = " where managed=1 and (category=1 or category=2 or category=3 or category=7) ";
    	}else if("net_router".equals(category)){
    		where = " where managed=1 and category=1";
    	}else if("net_switch".equals(category)){
    		where = " where managed=1 and (category=2 or category=3 or category=7) ";
    	}else{
    		where = " where managed=1";
    	}
    	where = where + getBidSql();
    	
    	HostNodeDao dao = new HostNodeDao();
    	
		String key = getParaValue("key");
		
		String value = getParaValue("value");
		if(key !=null && key.trim().length() > 0 && value != null && value.trim().length() > 0){
			where = where + " and " +  key + "='" + value + "'";
			
			System.out.println(where);
			
			
		}
		list(dao,where);
		dao.close();
		List list = (List)request.getAttribute("list");
    	return list;
    }
	/**
	 * 
	 * @author gzm 
	 * @time 2010-12-21
	 */
	private String getBidSql()
	{
    	User current_user = (User)session.getAttribute(SessionConstant.CURRENT_USER);
		String bid = this.getParaValue("bid");
		StringBuffer s = new StringBuffer();
		int _flag = 0;
		//if (current_user.getBusinessids() != null)
		if(bid!=null)
		{
			//if(current_user.getBusinessids() !="-1")
			if(bid!="-1")
			{
				//String[] bids = current_user.getBusinessids().split(",");
				String[] bids = bid.split(",");
				if(bids.length>0)
				{
					for(int i=0;i<bids.length;i++)
					{
						if(bids[i].trim().length()>0)
						{
							if(_flag==0)
							{
								s.append(" and ( bid like '%,"+bids[i].trim()+",%' ");
								_flag = 1;
							}
							else
							{
								//flag = 1;
								s.append(" or bid like '%,"+bids[i].trim()+",%' ");
							}
						}
					}
					s.append(") ") ;
				}
			}
		}
		
		SysLogger.info("select * from topo_host_node where managed=1 "+s);
		String sql = "";
		if(current_user.getRole() == 0)
		{
			sql = "";
		}
		else
		{
			sql = s.toString();
		}
		return sql;
    }
	private String ready_add()
	{
		
		//NetworkManager networkManager = new NetworkManager();
		//networkManager.execute("monitornodelist");
		//request.setAttribute("userList", userList);
		String jsp = "/capreport/subscribeReportConfig/add.jsp";
		return jsp;
	}
	private String createModel()
	{
		String ids=request.getParameter("ids");
		String type=request.getParameter("type");
		request.setAttribute("type", type);
		request.setAttribute("ids", ids);
		
		String jsp = "/capreport/subscribeReportConfig/creatModel.jsp";
		return jsp;
	}
	private String list()
	{
		String jsp = "/capreport/subscribeReportConfig/list.jsp";
		CycleReportConfigDao dao = new CycleReportConfigDao();
		setTarget(jsp);
		list(dao);
		List list = (List)request.getAttribute("list");
		SysLogger.info("list size ---------------"+list.size());
		List nodeList = cycleReportConfigConverToReportConfigNode(list);
		request.setAttribute("list", nodeList);
		return jsp;
	}
	private List cycleReportConfigConverToReportConfigNode(List list)
	{
		List nodeList = new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			CycleReportConfig reportConfig = (CycleReportConfig)list.get(i);
			String businessNames = bidIdConvertToName(reportConfig.getBids());
			String recieverNames = recieverIdsConvertToName(reportConfig.getCollectionOfRecieverId());
			String deviceNames = deviceIDsConvertToName(reportConfig.getCollectionOfdeviceId());
			String generationTime = generationTimeConvertToName(reportConfig.getCollectionOfGenerationTime());
			ReportConfigNode node = new ReportConfigNode();
			node.setBidNames(businessNames);
			node.setConfig(reportConfig);
			node.setDeviceNames(deviceNames);
			node.setRecieverNames(recieverNames);
			node.setCollectionOfGenerationTime(generationTime);
			nodeList.add(node);
		}
		return nodeList;
	}
	private String bidIdConvertToName(String bids)
	{
		BusinessDao dao = new BusinessDao();
		List list = new ArrayList();
		try{
			String bidstr = bids.substring(1);
			if(bidstr.endsWith(",")){
				list = dao.findByIDs(bidstr.substring(0,bidstr.length()-1));
			}else{
				list = dao.findByIDs(bids.substring(1));
			}
			
		}catch(Exception e){
			
		}finally{
			dao.close();
		}
		String temp="";
		for(int i=0;i<list.size();i++)
		{
			temp = temp+","+((Business)list.get(i)).getName();
		}
		return temp;
	}
	private String recieverIdsConvertToName(String recieverIDs)
	{
		UserDao dao = new UserDao();
		List list = dao.findbyIDs(recieverIDs.substring(1));
		String temp="";
		for(int i=0;i<list.size();i++)
		{
			temp = temp+","+((User)list.get(i)).getName();
		}
		dao.close();
		return temp;
	}
	private String deviceIDsConvertToName(String deviceIDs)
	{
		HostNodeDao dao = new HostNodeDao();
		List list = dao.findByIDs(deviceIDs.substring(1));
		String temp="";
		for(int i=0;i<list.size();i++)
		{
			temp = temp+","+((HostNode)list.get(i)).getAlias();
		}
		dao.close();
		return temp;
	}
	private String generationTimeConvertToName(String generationTime)
	{
		String temp="";
		if(generationTime!=null && !"".equals(generationTime)){
			String[] splitStr = generationTime.substring(1).split(",");
			for(int i =0;i<splitStr.length;i++)
			{
				String temp1 = splitStr[i];
				String temp2 = temp1.substring(0,1);
				String temp3 = temp1.substring(2, temp1.length()-1);
				String temp4 = (String)weekHash.get(temp2);
				temp = temp + ","+temp4+temp3+"时";
			}
		}
		return temp;
	}
	
	private Date getSpecificCycleExecuteTime(int dayOfWeek,int hour)
	{
		Calendar c = Calendar.getInstance();
		System.out.println("now time: " + c.getTime());
		int nowDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek<nowDayOfWeek)
		{
			c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH)+7);
		}
		else
		{
			c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		}
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		Date t = c.getTime();
		return t;
	}
}
