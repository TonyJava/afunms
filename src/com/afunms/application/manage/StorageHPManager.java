package com.afunms.application.manage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.afunms.common.base.BaseManager;
import com.afunms.common.base.ManagerInterface;
import com.afunms.common.util.ShareData;
import com.afunms.emc.dao.sysDao;
import com.afunms.emc.model.Agent;
import com.afunms.event.dao.EventListDao;
import com.afunms.indicators.model.NodeDTO;
import com.afunms.indicators.util.NodeUtil;
import com.afunms.polling.PollingEngine;
import com.afunms.polling.node.ArrayInfo;
import com.afunms.polling.node.Controller;
import com.afunms.polling.node.Disk;
import com.afunms.polling.node.Enclosure;
import com.afunms.polling.node.Host;
import com.afunms.polling.node.Lun;
import com.afunms.polling.node.Port;
import com.afunms.polling.node.Sensor;
import com.afunms.polling.node.SshController;
import com.afunms.polling.node.SubSystemInfo;
import com.afunms.polling.node.SystemInfo;
import com.afunms.polling.node.VFP;
import com.afunms.polling.node.Volume;

public class StorageHPManager extends BaseManager implements ManagerInterface {

	public String execute(String action) {
		// TODO Auto-generated method stub
		if("system".equals(action)){
			return system();
		}
		if("array".equals(action)){
			return array();
		}
		if("enclosures".equals(action))
		{
			return enclosures();
		}
		if("ports".equals(action))
		{
			return ports();
		}
		if("disks".equals(action))
		{
			return disks();
		}
		if("luns".equals(action))
		{
			return luns();
		}
		if("vfps".equals(action))
		{
			return vfps();
		}
		if("subsysteminfo".equals(action))
		{
			return subsysteminfo();
		}
		if("controllers".equals(action))
		{
			return controllers();
		}
		if("sensor".equals(action))
		{
			return sensor();
		}
		if("volume".equals(action))
		{
			return volume();
		}
		if("alarm".equals(action))
		{
			return alarm();
		}
		return null;
	}
	public String sensor()
	{
		Hashtable ipAllData = new Hashtable();
		String jsp="/application/hpstorage/hp_sensor.jsp";
		List<Sensor> sensorList = null;
		String tmp ="";
		Host host = null;
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				sensorList = (List<Sensor>) ipAllData.get("sensor");
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("sensorList", sensorList);
		request.setAttribute("hpstorage", host);
		
	    return jsp;		
	}
	public String volume()
	{
		Hashtable ipAllData = new Hashtable();
		String jsp="/application/hpstorage/hp_volume.jsp";
		List<Volume> volumeList = null;
		String tmp ="";
		Host host = null;
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				volumeList = (List<Volume>) ipAllData.get("volume");
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("volumeList", volumeList);
		request.setAttribute("hpstorage", host);
		
	    return jsp;		
	}
	public String system()
	{
		Hashtable ipAllData = new Hashtable();
		String jsp="/application/hpstorage/System.jsp";
		SystemInfo systeminfo = null;
		String tmp ="";
		Host host = null;
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				systeminfo = (SystemInfo) ipAllData.get("systeminfo");
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("systeminfo", systeminfo);
		request.setAttribute("hpstorage", host);
		if(host.getCollecttype()==7){
			jsp="/application/hpstorage/hp_system.jsp";
		}
	    return jsp;		
	}
	
	public String array()
	{
		Hashtable ipAllData = new Hashtable();
		ArrayInfo arrayinfo = null;
		String tmp ="";
		Host host = null;
		List list=null;
		String jsp="/application/hpstorage/array.jsp";
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				if(host.getCollecttype()==7){
					jsp="/application/hpstorage/hp_vdisks.jsp";
					list = (List) ipAllData.get("vdisk");
					request.setAttribute("vdisk", list);
				}else{
				arrayinfo = (ArrayInfo) ipAllData.get("arrayinfo");
				request.setAttribute("arrayinfo", arrayinfo);
				}
		    }
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("hp storage arrayinfo error");
			e.printStackTrace();
		}
		
		request.setAttribute("id", tmp);
		
		request.setAttribute("hpstorage", host);
	    return jsp;		
	}
	
	public String enclosures()
	{
		Hashtable ipAllData = new Hashtable();
		Map enclosures = null;
		String tmp ="";
		Host host = null;
		String jsp="/application/hpstorage/enclosures.jsp";
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				enclosures = (Map) ipAllData.get("enclosures");
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(host.getCollecttype()==7){
			jsp="/application/hpstorage/hp_enclosures.jsp";
		}
		request.setAttribute("id", tmp);
		request.setAttribute("enclosures", enclosures);
		request.setAttribute("hpstorage", host);
	    return jsp;		
	}
	
	public String ports()
	{
		Hashtable ipAllData = new Hashtable();
		List ports = null;
		String tmp ="";
		Host host = null;
		String jsp="/application/hpstorage/ports.jsp";
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				ports = (List) ipAllData.get("ports");
		    }
		} catch (Exception e) {
			System.out.println("hp storage ports error");
			e.printStackTrace();
		}
		if(host.getCollecttype()==7){
			jsp="/application/hpstorage/hp_ports.jsp";
		}
		request.setAttribute("id", tmp);
		request.setAttribute("ports", ports);
		request.setAttribute("hpstorage", host);
	    return jsp;		
	}
	
	public String disks()
	{
		Hashtable ipAllData = new Hashtable();
		List disks = null;
		String tmp ="";
		String jsp="/application/hpstorage/disks.jsp";
		Host host = null;
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				disks = (List) ipAllData.get("disks");
		    }
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("hp storage disks error");
			e.printStackTrace();
		}
		if(host.getCollecttype()==7){
			jsp="/application/hpstorage/hp_disks.jsp";
		}
		request.setAttribute("id", tmp);
		request.setAttribute("disks", disks);
		request.setAttribute("hpstorage", host);
	    return jsp;		
	}
	
	public String luns()
	{
		Hashtable ipAllData = new Hashtable();
		List<Lun> luns = null;
		String tmp ="";
		Host host = null;
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				luns = (List<Lun>) ipAllData.get("luns");
		    }
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("hp storage luns error");
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("luns", luns);
		request.setAttribute("hpstorage", host);
	    return "/application/hpstorage/luns.jsp";		
	}
	
	public String vfps()
	{
		Hashtable ipAllData = new Hashtable();
		List<VFP> vfps = null;
		String tmp ="";
		Host host = null;
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				vfps = (List<VFP>) ipAllData.get("vfps");
		    }
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("hp storage vfps error");
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("vfps", vfps);
		request.setAttribute("hpstorage", host);
	    return "/application/hpstorage/vfps.jsp";		
	}
	
	public String subsysteminfo()
	{
		Hashtable ipAllData = new Hashtable();
		SubSystemInfo subSystemInfo = null;
		String tmp ="";
		Host host = null;
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				subSystemInfo = (SubSystemInfo) ipAllData.get("subSystemInfo");
		    }
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("hp storage subSystemInfo error");
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("subSystemInfo", subSystemInfo);
		request.setAttribute("hpstorage", host);
	    return "/application/hpstorage/subsysteminfo.jsp";		
	}
	
	public String controllers()
	{
		Hashtable ipAllData = new Hashtable();
		List controllers = null;
		String tmp ="";
		Host host = null;
		String jsp="/application/hpstorage/controllers.jsp";
		try {
			tmp = request.getParameter("id");
			host = (Host)PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
			ipAllData = (Hashtable) ShareData.getSharedata().get("hpstorage:" + host.getIpAddress());
			if(ipAllData != null){
				if(host.getCollecttype()==7){
					controllers = (List<SshController>) ipAllData.get("controllers");
				}else{
					 controllers = (List<Controller>) ipAllData.get("controllers");
				
				}
		    }
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("hp storage controllers error");
			e.printStackTrace();
		}
		request.setAttribute("id", tmp);
		request.setAttribute("controllers", controllers);
		request.setAttribute("hpstorage", host);
		if(host.getCollecttype()==7){
			jsp="/application/hpstorage/hp_controller.jsp";
		}
	    return jsp;		
	}
	
	public String alarm()
	{
		String tmp = request.getParameter("id");
		Host host = (Host) PollingEngine.getInstance().getNodeByID(Integer.parseInt(tmp));
		int alarmLevel = 0;
		String jsp="/application/hpstorage/hpstoragealarm.jsp";
		Hashtable checkEventHashtable = ShareData.getCheckEventHash();
		NodeUtil nodeUtil = new NodeUtil();
		NodeDTO nodeDTO = nodeUtil.conversionToNodeDTO(host);
		if (nodeDTO != null) {
			String chexkname = tmp + ":" + nodeDTO.getType() + ":" + nodeDTO.getSubtype() + ":";
			if (checkEventHashtable != null) {
				for (Iterator it = checkEventHashtable.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					if (key.startsWith(chexkname)) {
						if (alarmLevel < (Integer) checkEventHashtable.get(key)) {
							alarmLevel = (Integer) checkEventHashtable.get(key);
						}
					}
				}
			}
		}
		request.setAttribute("alarmLevel", alarmLevel);
		String b_time = "";
		String t_time = "";
		b_time = getParaValue("startdate");
		t_time = getParaValue("todate");
		if (b_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			b_time = sdf.format(new Date());
		}
		if (t_time == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			t_time = sdf.format(new Date());
		}
		EventListDao eldao = new EventListDao();
		List list = null;
		try {
			list = eldao.loadAll(" where nodeid = " + host.getId() + " and recordtime  <'" + t_time + " 23:59:00' and recordtime > '" + t_time + " 00:00:00'");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eldao.close();
		}
		if(host.getCollecttype()==7){
			jsp="/application/hpstorage/hp_alarm.jsp";
		}
		request.setAttribute("id", tmp);
		request.setAttribute("list", list);
		request.setAttribute("startdate", b_time);
		request.setAttribute("todate", t_time);
		return jsp;
	}
}
